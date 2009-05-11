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
package com.concursive.connect.web.modules.profile.actions;

import com.concursive.commons.web.mvc.actions.ActionContext;
import com.concursive.connect.web.controller.actions.GenericAction;
import com.concursive.connect.web.modules.documents.dao.FileItem;
import com.concursive.connect.web.modules.profile.dao.Project;

import java.sql.Connection;

/**
 * Actions for project profile
 *
 * @author matt rajkowski
 * @created September 11, 2008
 */

public final class ProjectManagementProfile extends GenericAction {

  public String executeCommandDeleteImg(ActionContext context) {
    Connection db = null;
    try {
      // Expected parameters
      int projectId = Integer.parseInt(context.getRequest().getParameter("pid"));
      String url = context.getRequest().getParameter("url");
      // Convert to working values
      String[] values = url.split("-");
      int constant = Integer.parseInt(values[0]);
      int linkItemId = Integer.parseInt(values[1]);
      int fileItemId = Integer.parseInt(values[2]);
      db = this.getConnection(context);
      // Load the fileItem and compare with the parameters
      FileItem fileItem = new FileItem(db, fileItemId);
      if (fileItem.getLinkItemId() != projectId) {
        return "SystemError";
      }
      // Load the project
      Project project = retrieveAuthorizedProject(fileItem.getLinkItemId(), context);
      // Images can be deleted by the enteredBy or by permission
      if (fileItem.getEnteredBy() != getUserId(context) && !hasProjectAccess(context, project.getId(), "project-profile-images-delete")) {
        return "PermissionError";
      }
      // Check to see if the logo is currently the default logo
      if (project.getLogoId() == fileItem.getId()) {
        // Set the logo to another entry...
        for (FileItem thisImage : project.getImages()) {
          if (thisImage.getId() != project.getLogoId()) {
            project.setLogoId(thisImage.getId());
            break;
          }
        }
        // Otherwise, just remove it
        if (project.getLogoId() == fileItem.getId()) {
          project.setLogoId(-1);
        }
        project.updateLogoId(db);
      }
      fileItem.delete(db, getPath(context, "projects"));
      context.getRequest().setAttribute("project", project);
      return "DeleteImageOK";
    } catch (Exception errorMessage) {
      context.getRequest().setAttribute("Error", errorMessage);
      errorMessage.printStackTrace(System.out);
      return ("SystemError");
    } finally {
      this.freeConnection(context, db);
    }
  }

  public String executeCommandSetImg(ActionContext context) {
    Connection db = null;
    try {
      // Expected parameters
      int projectId = Integer.parseInt(context.getRequest().getParameter("pid"));
      String url = context.getRequest().getParameter("url");
      // Convert to working values
      String[] values = url.split("-");
      int constant = Integer.parseInt(values[0]);
      int linkItemId = Integer.parseInt(values[1]);
      int fileItemId = Integer.parseInt(values[2]);
      db = this.getConnection(context);
      // Load the fileItem and compare with the parameters
      FileItem fileItem = new FileItem(db, fileItemId);
      if (fileItem.getLinkItemId() != projectId) {
        return "SystemError";
      }
      // Load the project
      Project project = retrieveAuthorizedProject(fileItem.getLinkItemId(), context);
      // Images can be set by permission
      if (!hasProjectAccess(context, project.getId(), "project-profile-admin")) {
        return "PermissionError";
      }
      project.setLogoId(fileItem.getId());
      project.updateLogoId(db);
      context.getRequest().setAttribute("project", project);
      return "SetImageOK";
    } catch (Exception errorMessage) {
      context.getRequest().setAttribute("Error", errorMessage);
      errorMessage.printStackTrace(System.out);
      return ("SystemError");
    } finally {
      this.freeConnection(context, db);
    }
  }
}