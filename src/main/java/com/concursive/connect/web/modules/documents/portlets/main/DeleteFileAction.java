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
package com.concursive.connect.web.modules.documents.portlets.main;

import com.concursive.commons.web.mvc.beans.GenericBean;
import com.concursive.connect.Constants;
import com.concursive.connect.web.modules.documents.dao.FileItem;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import com.concursive.connect.web.portal.IPortletAction;
import com.concursive.connect.web.portal.PortalUtils;
import static com.concursive.connect.web.portal.PortalUtils.*;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import java.sql.Connection;

/**
 * Delete action
 *
 * @author matt rajkowski
 * @created January 26, 2009
 */
public class DeleteFileAction implements IPortletAction {

  public GenericBean processAction(ActionRequest request, ActionResponse response) throws Exception {

    // Determine the project container to use
    Project project = findProject(request);
    if (project == null) {
      throw new Exception("Project is null");
    }

    // Check the user's permissions
    User user = getUser(request);
    if (!ProjectUtils.hasAccess(project.getId(), user, "project-documents-files-delete")) {
      throw new PortletException("Unauthorized to delete in this project");
    }

    // Get the record id
    int id = getPageViewAsInt(request);

    // Get the record's version
    double version = getPageParameterAsDouble(request);

    // Determine the file path for documents
    String filePath = PortalUtils.getFileLibraryPath(request, "projects");

    // Determine the database connectivity
    Connection db = getConnection(request);

    // Load the record
    FileItem thisItem = new FileItem(db, id, project.getId(), Constants.PROJECTS_FILES);
    // Determine if just 1 version or the whole file is to be deleted
    boolean isVersion = false;
    boolean recordDeleted = false;
    if (version > 0) {
      thisItem.buildVersionList(db);
      if (thisItem.getVersionList().size() > 1) {
        isVersion = true;
        if (version == thisItem.getVersionList().get(0).getVersion()) {
          // Remove from index the old file item
          indexDeleteItem(request, thisItem);
          // The first entry in the list is being deleted
          // Delete the version, the next item will update the FileItem
          thisItem.getVersionList().get(0).delete(db, filePath);
          thisItem.updateVersion(db, thisItem.getVersionList().get(1));
          // Index the new top entry
          thisItem.setDirectory(filePath);
          indexAddItem(request, thisItem);
          // Trigger the workflow
          processDeleteHook(request, thisItem.getVersionList().get(0));
        } else {
          // Just delete the version since it's not the latest
          thisItem.getVersion(version).delete(db, filePath);
          // Trigger the workflow
          processDeleteHook(request, thisItem.getVersionList().get(0));
        }
      } else {
        // Delete the only version in the list
        thisItem.delete(db, filePath);

        // Remove from index
        indexDeleteItem(request, thisItem);

        // Trigger the workflow
        processDeleteHook(request, thisItem);
      }
    } else {
      // All versions are being deleted
      thisItem.delete(db, filePath);

      // Remove from index
      indexDeleteItem(request, thisItem);

      // Trigger the workflow
      processDeleteHook(request, thisItem);
    }

    // This call will close panels and perform redirects
    if (isVersion) {
      return (PortalUtils.performRefresh(request, response, "/show/file/" + thisItem.getId()));
    } else {
      if (thisItem.getFolderId() > -1) {
        return (PortalUtils.performRefresh(request, response, "/show/folder/" + thisItem.getFolderId()));
      } else {
        return (PortalUtils.performRefresh(request, response, "/show/documents/"));
      }
    }
  }
}
