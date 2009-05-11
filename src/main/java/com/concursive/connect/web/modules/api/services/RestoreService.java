/*
 * ConcourseConnect
 * Copyright 2009 Concursive Corporation
 * http://www.concursive.com
 *
 * This file is part of ConcourseConnect, an open source social business
 * software and community platform.
 *
 * Concursive ConcourseConnect is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, version 3 of the License.
 *
 * Under the terms of the GNU Affero General Public License you must release the
 * complete source code for any application that uses any part of ConcourseConnect
 * (system header files and libraries used by the operating system are excluded).
 * These terms must be included in any work that has ConcourseConnect components.
 * If you are developing and distributing open source applications under the
 * GNU Affero General Public License, then you are free to use ConcourseConnect
 * under the GNU Affero General Public License.
 *
 * If you are deploying a web site in which users interact with any portion of
 * ConcourseConnect over a network, the complete source code changes must be made
 * available.  For example, include a link to the source archive directly from
 * your web site.
 *
 * For OEMs, ISVs, SIs and VARs who distribute ConcourseConnect with their
 * products, and do not license and distribute their source code under the GNU
 * Affero General Public License, Concursive provides a flexible commercial
 * license.
 *
 * To anyone in doubt, we recommend the commercial license. Our commercial license
 * is competitively priced and will eliminate any confusion about how
 * ConcourseConnect can be used and distributed.
 *
 * ConcourseConnect is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with ConcourseConnect.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Attribution Notice: ConcourseConnect is an Original Work of software created
 * by Concursive Corporation
 */
package com.concursive.connect.web.modules.api.services;

import com.concursive.commons.api.DataRecordFactory;
import com.concursive.commons.date.DateUtils;
import com.concursive.commons.files.FileUtils;
import com.concursive.commons.objects.ObjectUtils;
import com.concursive.connect.web.modules.api.beans.PacketContext;
import com.concursive.connect.web.modules.api.beans.TransactionContext;
import com.concursive.connect.web.modules.api.beans.TransactionItem;
import com.concursive.connect.web.modules.api.beans.TransactionMeta;
import com.concursive.connect.web.modules.documents.dao.FileItem;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.sql.Connection;
import java.util.HashMap;

/**
 * User: matt rajkowski
 * Date: Mar 14, 2008
 * Restore specified data
 */
public class RestoreService implements CustomActionHandler {

  private static Log LOG = LogFactory.getLog(RestoreService.class);

  private static final int MODE_UNDEFINED = -1;
  private static final int MODE_OVERWRITE = 1;
  private static final int MODE_COPY = 2;

  public boolean process(TransactionItem transactionItem, Connection db) throws Exception {

    TransactionMeta meta = transactionItem.getMeta();
    boolean first = transactionItem.getCount() == 1;
    if (first) {
      LOG.debug("Restore requested... " + meta.toString());
    }

    // Determine the restore properties
    int mode = MODE_UNDEFINED;
    if (meta.hasField("mode=overwrite")) {
      // The existing project will be deleted and inserted with the same id
      mode = MODE_OVERWRITE;
    } else if (meta.hasField("mode=copy")) {
      // A copy of the project will be inserted
      mode = MODE_COPY;
    }
    if (mode == MODE_UNDEFINED) {
      transactionItem.appendErrorMessage("RESTORE MODE NOT SPECIFIED");
      return false;
    }

    boolean triggerHooks = false;
    boolean triggerCache = true;

    // Start restoring...
    DataRecordFactory factory = DataRecordFactory.INSTANCE;
    PacketContext packetContext = transactionItem.getPacketContext();
    TransactionContext transactionContext = transactionItem.getTransactionContext();
    Object object = transactionItem.getObject();
    String uniqueField = factory.retrieveUniqueField(transactionItem.getName());

    // Execute a restore method...
    if (mode == MODE_OVERWRITE) {
      // Delete the previous object (if it exists), then insert the same id
      Object previousObject = ObjectUtils.constructObject(object.getClass(), db, Integer.parseInt(ObjectUtils.getParam(object, uniqueField)));
      if (previousObject != null && ObjectUtils.getParamAsInt(object, uniqueField) == ObjectUtils.getParamAsInt(previousObject, uniqueField)) {
        LOG.debug(" deleting previous record: " + transactionItem.getName() + " (" + ObjectUtils.getParam(object, uniqueField) + ")");
        TransactionItem.doExecute(previousObject, db, TransactionItem.DELETE, packetContext, "delete", triggerHooks, triggerCache);
      }
      ObjectUtils.setParam(object, "apiRestore", true);
      TransactionItem.doExecute(object, db, TransactionItem.INSERT, packetContext, "insert", triggerHooks, triggerCache);
      LOG.debug(" inserting record: " + transactionItem.getName() + " (" + ObjectUtils.getParam(object, uniqueField) + ")");

    } else if (mode == MODE_COPY) {
      // NOTE: Assumes copying into the same system because not all references are in the restore set
      transactionItem.setShareKey(true);
      if (meta.hasField("disable=true")) {
        // TODO: This assumes the record has an "enabled" property -- which some don't (like projects)
        // Set enabled=false and call update
        String enabledValue = ObjectUtils.getParam(object, "enabled");
        if (enabledValue != null) {
          boolean enabled = "true".equals(enabledValue);
          if (!enabled) {
            ObjectUtils.setParam(object, "enabled", "false");
            TransactionItem.doExecute(object, db, TransactionItem.UPDATE, packetContext, "update", triggerHooks, triggerCache);
            LOG.debug(" disabled record: " + transactionItem.getName() + " (" + ObjectUtils.getParam(object, uniqueField) + ")");
            ObjectUtils.setParam(object, "enabled", "true");
          }
        }
      }

      // Reset the unique id to force a new record
      String uniqueId = ObjectUtils.getParam(object, uniqueField);
      ObjectUtils.setParam(object, uniqueField, -1);

      // If this object references any of the shared keys, then substitute the value before calling insert
      HashMap<String, String> lookups = factory.retrieveLookupNames(transactionItem.getName());
      for (String thisField : lookups.keySet()) {
        String thisLookup = lookups.get(thisField);
        String mapValue = transactionContext.getPropertyMap().get(thisLookup + "." + ObjectUtils.getParam(object, thisField));
        if (mapValue != null) {
          LOG.trace(" replacing value: " + thisField + "=" + mapValue);
          ObjectUtils.setParam(object, thisField, mapValue);
        }
      }
      ObjectUtils.setParam(object, "apiRestore", true);
      LOG.debug(" found: " + object.getClass().getName());
      TransactionItem.doExecute(object, db, TransactionItem.INSERT, packetContext, "insert", triggerHooks, triggerCache);
      // Store the shared key
      transactionContext.getPropertyMap().put(transactionItem.getName() + "." + uniqueField + "." + uniqueId, ObjectUtils.getParam(object, uniqueField));
      LOG.debug(" inserted record copy: " + transactionItem.getName() + "." + uniqueField + "." + uniqueId + " (" + ObjectUtils.getParam(object, uniqueField) + ")");
    }

    // Check if there is a file attachment
    if (object instanceof FileItem) {
      FileItem fileItem = (FileItem) object;
      if (fileItem.getFileAttachment() != null) {
        // Make sure the destination path exists for writing the file attachment
        String directory =
            transactionItem.getPacketContext().getBaseFilePath() +
                DateUtils.getDatePath(fileItem.getModified());
        File dir = new File(directory);
        if (!dir.exists()) {
          LOG.debug("Making directory: " + directory);
          dir.mkdirs();
        }
        // Write the file
        File destinationFile = new File(directory + fileItem.getFilename());
        LOG.debug("Writing the file: " + destinationFile.getAbsolutePath());
        byte[] inputBytes = Base64.decodeBase64(fileItem.getFileAttachment().getBytes("UTF8"));
        boolean success = FileUtils.copyBytesToFile(inputBytes, destinationFile, true);
        if (!success) {
          LOG.error("Could not write file attachment: " + destinationFile.getAbsolutePath());
        }
        if (LOG.isDebugEnabled()) {
          boolean lengthMatches = (destinationFile.length() == fileItem.getSize());
          if (!lengthMatches) {
            LOG.error("Length should be " + fileItem.getSize() + " instead of " + destinationFile.length());
          }
        }
      }
    }

    return true;
  }
}
