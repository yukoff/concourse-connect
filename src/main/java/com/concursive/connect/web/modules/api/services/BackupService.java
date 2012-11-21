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

import com.concursive.commons.api.DataField;
import com.concursive.commons.api.DataRecord;
import com.concursive.commons.api.DataRecordFactory;
import com.concursive.commons.api.PropertyList;
import com.concursive.commons.date.DateUtils;
import com.concursive.commons.files.FileUtils;
import com.concursive.commons.objects.ObjectUtils;
import com.concursive.commons.text.StringUtils;
import com.concursive.connect.web.modules.api.beans.PacketContext;
import com.concursive.connect.web.modules.api.beans.TransactionItem;
import com.concursive.connect.web.modules.api.beans.TransactionMeta;
import com.concursive.connect.web.modules.documents.dao.FileItem;
import com.concursive.connect.web.utils.CustomLookupList;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import java.io.File;
import java.io.OutputStream;
import java.sql.Connection;
import java.util.*;

/**
 * User: matt rajkowski
 * Date: Mar 6, 2008
 * Backs up specified data as XML
 */
public class BackupService implements CustomActionHandler {

  private static Log LOG = LogFactory.getLog(BackupService.class);

  public boolean process(TransactionItem transactionItem, Connection db) throws Exception {

    LOG.debug("Backup requested...");

    // TODO: Fix cyclical/endless backups; need to keep an array of ids already archived
    // TODO: Fix if lookup value is -1, then a lookup is not needed

    DataRecordFactory factory = DataRecordFactory.INSTANCE;

    // Override the default response packet so that the returned data records
    // can be replayed using the RestoreService; stream the output
    PacketContext packetContext = transactionItem.getPacketContext();
    packetContext.setReturnType(PacketContext.RETURN_DATARECORDS);

    // Start with the original record(s) being backed up
    LOG.debug("Performing buildList query");
    Object object = transactionItem.getObject();
    if (object instanceof java.util.AbstractList || object instanceof java.util.AbstractMap) {
      Object result = TransactionItem.doExecute(transactionItem.getObject(), db, TransactionItem.SELECT, packetContext, "buildList");
      // TODO: check result
    } else {
      Object newObject = ObjectUtils.constructObject(
          object.getClass(), db, ObjectUtils.getParamAsInt(object, factory.retrieveUniqueField(transactionItem.getName() + "List")));
      transactionItem.setObject(newObject);
    }

    // Start backup recursion...
    //   Consider lower-memory options to stream records without holding in memory
    //   option 1: use pagedList to get x record(s) at a time
    //   option 2: consider queue for limiting backup requests and asynchronous backups

    XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
    OutputStream outputStream = null;
    if (packetContext.getOutputStream() == null) {
      outputStream = packetContext.getResponse().getOutputStream();
    } else {
      outputStream = packetContext.getOutputStream();
    }
    XMLStreamWriter writer = outputFactory.createXMLStreamWriter(outputStream, "utf-8");
    writer.writeStartDocument();
    writer.writeCharacters(System.getProperty("line.separator"));
    writer.writeStartElement("concursive");
    writer.writeCharacters(System.getProperty("line.separator"));

    ArrayList<String> addedRecords = new ArrayList<String>();
    addRecords(writer, transactionItem.getObject(), transactionItem, packetContext, db, addedRecords);
    writer.writeEndElement();
    writer.writeCharacters(System.getProperty("line.separator"));
    writer.flush();
    writer.close();

    return true;
  }

  /**
   * For the given object, recurse through the object map until no more
   * related objects are found.
   *
   * @param writer        Stores the datarecords that will be returned
   * @param object        The object to traverse for adding dependent records
   * @param packetContext The transaction packet context
   * @param db            Database connection
   * @throws Exception
   */
  private void addRecords(XMLStreamWriter writer, Object object, TransactionItem transactionItem, PacketContext packetContext, Connection db, ArrayList<String> addedRecords) throws Exception {
    // Contains backup instructions
    TransactionMeta meta = transactionItem.getMeta();
    ArrayList<String> propertyStopList = new ArrayList<String>();
    for (String field : meta.getFields()) {
      if (field.startsWith("stop=")) {
        propertyStopList.add(field.substring(5));
      }
    }
    // Handles converting an object to a datarecord
    DataRecordFactory factory = DataRecordFactory.INSTANCE;
    // Process the array depending on the underlying Class
    Collection objectList = null;
    if (object instanceof java.util.AbstractList) {
      if (object.getClass().getName().equals("CustomLookupList")) {
        objectList = ((CustomLookupList) object);
      } else {
        objectList = (AbstractList) object;
      }
    } else if (object instanceof java.util.AbstractMap) {
      objectList = ((AbstractMap) object).values();
    } else {
      objectList = new ArrayList<Object>();
      objectList.add(object);
      /*
      Record thisRecord = new Record(recordAction);
      this.addFields(thisRecord, meta, object);
      recordList.add(thisRecord);
      return thisRecord;
       */
      //throw new Exception("BackupService-> Needs to be implemented for: " + object.getClass().getName());
    }
    // Save each object as a data record using the streaming writer
    for (Object thisObject : objectList) {
      // Convert the object into a datarecord
      DataRecord thisRecord = factory.parse(thisObject);
      String uniqueField = factory.retrieveUniqueField(thisRecord.getName());
      String currentId = ObjectUtils.getParam(thisObject, uniqueField);
      String key = thisRecord.getName() + "-" + currentId;
      // Only add the record to the backup set once
      if (!addedRecords.contains(key)) {
        // Add the record
        save(writer, thisRecord, currentId, thisObject, packetContext);
        addedRecords.add(key);
      }

      // Determine if children should be retrieved
      if (meta.hasField("descend=false")) {
        LOG.debug("Do not descend directive found.");
        continue;
      }

      // For each referenced object add them to the backup
      List<PropertyList> names = factory.retrieveDependentNames(thisRecord.getName());
      for (PropertyList thisProperty : names) {
        String dependentListClass = factory.retrieveClassName(thisProperty.getId() + "List");
        if (dependentListClass == null) {
          LOG.error("Dependent class not found for name = " + thisProperty.getId() + "List");
        } else if (propertyStopList.contains(thisProperty.getId())) {
          LOG.debug("Found a property to stop at... " + thisProperty.getId());
        } else {
          Object dependentList = Class.forName(dependentListClass).newInstance();
          // set the properties in which there is a dependency
          // @todo will need to set all referencing properties on a multi-level backup
          // for now performing a Project backup works because only 1 value is being used, the project Id
          String referencingProperty = thisProperty.retrieveReferencingProperty(thisRecord.getName());
          ObjectUtils.setParam(dependentList, referencingProperty, ObjectUtils.getParam(thisObject, uniqueField));
          // Apply any object values that are defined
          Map<String, String> parameters = factory.retrieveParameterMap(thisProperty.getId() + "List");
          if (parameters.size() > 0) {
            for (String parameter : parameters.keySet()) {
              ObjectUtils.setParam(dependentList, parameter, parameters.get(parameter));
            }
          }
          // @todo implement paging
          // Build the list of records
          TransactionItem.doExecute(dependentList, db, TransactionItem.SELECT, packetContext, "buildList");
          // Recurse through the dependent records
          addRecords(writer, dependentList, transactionItem, packetContext, db, addedRecords);
        }
      }
    }
  }

  private void save(XMLStreamWriter writer, DataRecord record, String recordId, Object object, PacketContext context) throws Exception {
    // Data record
    writer.writeCharacters("  ");
    writer.writeStartElement("dataRecord");
    writer.writeAttribute("name", record.getName());
    writer.writeAttribute("id", recordId);
    writer.writeCharacters(System.getProperty("line.separator"));
    // Data fields
    for (DataField thisField : record) {
      writer.writeCharacters("    ");
      writer.writeStartElement(thisField.getName());
      if (thisField.getValue() != null) {
        if (StringUtils.countLines(thisField.getValue()) > 1) {
          writer.writeCData(thisField.getValue());
        } else {
          writer.writeCharacters(thisField.getValue());
        }
      } else {
        writer.writeCharacters(DataRecord.NULL);
      }
      writer.writeEndElement();
      writer.writeCharacters(System.getProperty("line.separator"));
    }

    // If this is a file, stream it too
    if (object instanceof FileItem) {
      // Find the file in the filesystem
      File file = new File(
          context.getBaseFilePath() +
              DateUtils.getDatePath(record.getValueAsTimestamp("modified")) +
              record.getValue("filename"));
      if (!file.exists()) {
        LOG.error("File not found: " + file.getAbsolutePath());
      } else {
        // If there is a fileAttachment, then attach it as base64
        writer.writeCharacters("    ");
        writer.writeStartElement("fileAttachment");
        // Convert to base64 and append
        writer.writeCData(new String(Base64.encodeBase64(FileUtils.getBytesFromFile(file), true)));
        // Close the element
        writer.writeEndElement();
        writer.writeCharacters(System.getProperty("line.separator"));
      }
    }

    // Close the record
    writer.writeCharacters("  ");
    writer.writeEndElement();
    writer.writeCharacters(System.getProperty("line.separator"));
  }
}
