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

package com.concursive.connect.web.modules.classifieds.actions;

import com.concursive.commons.web.mvc.actions.ActionContext;
import com.concursive.connect.Constants;
import com.concursive.connect.web.controller.actions.GenericAction;
import com.concursive.connect.web.modules.classifieds.dao.Classified;
import com.concursive.connect.web.modules.documents.beans.FileDownload;
import com.concursive.connect.web.modules.documents.dao.FileItem;
import com.concursive.connect.web.modules.profile.dao.Project;

import java.sql.Connection;

/**
 * Actions for working with Classifieds
 *
 * @author Kailash Bhoopalam
 * @created May 23, 2008
 */
public final class ProjectManagementClassifieds extends GenericAction {

  public String executeCommandDownload(ActionContext context) {
    String projectId = context.getRequest().getParameter("pid");
    String classifiedId = context.getRequest().getParameter("cid");
    String itemId = context.getRequest().getParameter("fid");
    String view = context.getRequest().getParameter("view");
    String size = context.getRequest().getParameter("size"); //210x150
    if (size != null && size.trim().length() > 0) {
      size = "-" + size + "-TH";
    } else {
      size = "";
    }
    FileItem thisItem = null;
    Connection db = null;
    try {
      db = getConnection(context);
      Project thisProject = retrieveAuthorizedProject(Integer.parseInt(projectId), context);
      if (!hasProjectAccess(context, thisProject.getId(), "project-classifieds-view")) {
        return "PermissionError";
      }
      if (classifiedId != null) {
        Classified classified = new Classified(db, Integer.parseInt(classifiedId));
        thisItem = new FileItem(db, Integer.parseInt(itemId), classified.getId(), Constants.PROJECT_CLASSIFIEDS_FILES);
      }
    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return ("SystemError");
    } finally {
      this.freeConnection(context, db);
    }
    //Start the download
    try {
      FileItem itemToDownload = thisItem;
      itemToDownload.setEnteredBy(getUserId(context));
      String filePath = this.getPath(context, "projects") +
          getDatePath(itemToDownload.getModified()) +
          itemToDownload.getFilename()
          + size;
      FileDownload fileDownload = new FileDownload();
      fileDownload.setFullPath(filePath);
      fileDownload.setDisplayName(itemToDownload.getClientFilename());
      if (fileDownload.fileExists()) {
        if (view != null && "true".equals(view)) {
          fileDownload.setFileTimestamp(itemToDownload.getModificationDate().getTime());
          fileDownload.streamContent(context);
        } else {
          fileDownload.sendFile(context);
        }
        //Get a db connection now that the download is complete
        db = getConnection(context);
        itemToDownload.updateCounter(db);
      } else {
        System.err.println(new java.util.Date() + ":: ProjectManagementClassified-> Trying to send a file that does not exist");
      }
    } catch (java.net.SocketException se) {
      //User either canceled the download or lost connection
    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return ("SystemError");
    } finally {
      this.freeConnection(context, db);
    }
    return ("-none-");
  }

  public String executeCommandFileDelete(ActionContext context) {
    Connection db = null;
    Classified thisClassified = null;
    try {
      db = this.getConnection(context);
      //Load the ticket and change the status
      thisClassified = new Classified(db, Integer.parseInt(context.getRequest().getParameter("cid")));
      //Load the project
      Project thisProject = retrieveAuthorizedProject(thisClassified.getProjectId(), context);
      if (!hasProjectAccess(context, thisProject.getId(), "project-classifieds-add")) {
        return "PermissionError";
      }
      String itemId = context.getRequest().getParameter("fid");
      FileItem thisItem = new FileItem(db, Integer.parseInt(itemId), thisClassified.getId(), Constants.PROJECT_CLASSIFIEDS_FILES);
      thisItem.delete(db, this.getPath(context, "projects"));
      // Set the project for refresh
      context.getRequest().setAttribute("project", thisProject);
      return "FileDeleteOK";
    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return ("SystemError");
    } finally {
      this.freeConnection(context, db);
    }
  }
}

