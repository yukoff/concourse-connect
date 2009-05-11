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

package com.concursive.connect.web.modules.admin.actions;

import com.concursive.commons.images.ImageUtils;
import com.concursive.commons.web.mvc.actions.ActionContext;
import com.concursive.connect.Constants;
import com.concursive.connect.web.controller.actions.GenericAction;
import com.concursive.connect.web.modules.documents.dao.FileItem;
import com.concursive.connect.web.modules.documents.dao.FileItemList;
import com.concursive.connect.web.modules.documents.dao.Thumbnail;
import com.concursive.connect.web.modules.documents.utils.FileInfo;
import com.concursive.connect.web.modules.documents.utils.HttpMultiPartParser;

import java.io.File;
import java.sql.Connection;
import java.util.HashMap;

/**
 * Description of the Class
 *
 * @author matt rajkowski
 * @version $Id$
 * @created June 6, 2004
 */
public final class AdminImageLibrary extends GenericAction {

  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandDefault(ActionContext context) {
    if (!getUser(context).getAccessAdmin()) {
      return "PermissionError";
    }
    Connection db = null;
    try {
      db = getConnection(context);
      //Build the file item list
      FileItemList files = new FileItemList();
      files.setLinkModuleId(Constants.IMAGELIBRARY_FILES);
      files.buildList(db);
      context.getRequest().setAttribute("imageList", files);
      return "ImageListOK";
    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return ("SystemError");
    } finally {
      freeConnection(context, db);
    }
  }


  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandAdd(ActionContext context) {
    if (!getUser(context).getAccessAdmin()) {
      return "PermissionError";
    }
    return ("ImageAddOK");
  }


  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandUpload(ActionContext context) {
    if (!getUser(context).getAccessAdmin()) {
      return "PermissionError";
    }
    Connection db = null;
    boolean recordInserted = false;
    try {
      String filePath = this.getPath(context, "imageLibrary");
      //Process the form data
      HttpMultiPartParser multiPart = new HttpMultiPartParser();
      multiPart.setUsePathParam(false);
      multiPart.setUseUniqueName(true);
      multiPart.setUseDateForFolder(true);
      multiPart.setExtensionId(getUserId(context));
      HashMap parts = multiPart.parseData(context.getRequest(), filePath);
      String subject = (String) parts.get("subject");
      //Update the database with the resulting file
      FileInfo newFileInfo = (FileInfo) parts.get("id");
      db = getConnection(context);
      FileItem thisItem = new FileItem();
      thisItem.setLinkModuleId(Constants.IMAGELIBRARY_FILES);
      thisItem.setLinkItemId(getUserId(context));
      thisItem.setEnteredBy(getUserId(context));
      thisItem.setModifiedBy(getUserId(context));
      thisItem.setSubject(subject);
      thisItem.setClientFilename(newFileInfo.getClientFileName());
      thisItem.setFilename(newFileInfo.getRealFilename());
      thisItem.setSize(newFileInfo.getSize());
      thisItem.setVersion(1.0);
      recordInserted = thisItem.insert(db);
      if (!recordInserted) {
        processErrors(context, thisItem.getErrors());
        return ("ImageAddOK");
      } else {
        if (thisItem.isImageFormat()) {
          //Create a thumbnail if this is an image
          File thumbnailFile = new File(newFileInfo.getLocalFile().getPath() + "TH");
          Thumbnail thumbnail = new Thumbnail(ImageUtils.saveThumbnail(newFileInfo.getLocalFile(), thumbnailFile, 133d, -1d, "jpg"));
          // Store thumbnail in database
          thumbnail.setId(thisItem.getId());
          thumbnail.setFilename(newFileInfo.getRealFilename() + "TH");
          thumbnail.setVersion(thisItem.getVersion());
          thumbnail.setSize((int) thumbnailFile.length());
          thumbnail.setEnteredBy(thisItem.getEnteredBy());
          thumbnail.setModifiedBy(thisItem.getModifiedBy());
          recordInserted = thumbnail.insert(db);
        }
      }
      return ("ImageUploadOK");
    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return ("SystemError");
    } finally {
      freeConnection(context, db);
    }
  }


  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandDeleteImage(ActionContext context) {
    if (!getUser(context).getAccessAdmin()) {
      return "PermissionError";
    }
    Connection db = null;
    try {
      db = getConnection(context);
      String itemId = context.getRequest().getParameter("i");
      FileItem fileItem = new FileItem(db, Integer.parseInt(itemId));
      if (fileItem.getLinkModuleId() != Constants.IMAGELIBRARY_FILES) {
        return ("DeleteImageERROR");
      }
      fileItem.delete(db, this.getPath(context, "imageLibrary"));
    } catch (Exception e) {
      e.printStackTrace(System.out);
    } finally {
      freeConnection(context, db);
    }
    return "DeleteImageOK";
  }
}

