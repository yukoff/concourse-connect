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
 * Save action
 *
 * @author matt rajkowski
 * @created January 26, 2009
 */
public class SaveFileAction implements IPortletAction {

  public GenericBean processAction(ActionRequest request, ActionResponse response) throws Exception {

    // Determine the project container to use
    Project project = findProject(request);
    if (project == null) {
      throw new Exception("Project is null");
    }

    // Check the user's permissions
    User user = getUser(request);

    // Populate any info from the request
    FileItem thisRecord = (FileItem) getFormBean(request, FileItem.class);

    // Set default values when saving records
    thisRecord.setLinkModuleId(Constants.PROJECTS_FILES);
    thisRecord.setLinkItemId(project.getId());
    thisRecord.setModifiedBy(user.getId());

    // Determine the database connection to use
    Connection db = getConnection(request);

    // Save the record
    int resultCount = -1;
    if (thisRecord.getId() == -1) {
      // This action does not handle new uploads
      throw new PortletException("Unauthorized to add in this project");
    } else {
      // This is an existing record
      if (!ProjectUtils.hasAccess(project.getId(), user, "project-documents-files-rename")) {
        throw new PortletException("Unauthorized to edit in this project");
      }
      resultCount = thisRecord.update(db);
    }

    // Check if an error occurred
    if (resultCount <= 0) {
      System.out.println("NOT UPDATED");
      System.out.println("Errors: " + thisRecord.getErrors().size());
      return thisRecord;
    }

    // Index the record and its file contents
    String filePath = PortalUtils.getFileLibraryPath(request, "projects");
    thisRecord.setDirectory(filePath);
    PortalUtils.indexAddItem(request, thisRecord);

    // This call will close panels and perform redirects
    if (thisRecord.getFolderId() == -1) {
      return (PortalUtils.performRefresh(request, response, "/show/documents"));
    } else {
      return (PortalUtils.performRefresh(request, response, "/show/folder/" + thisRecord.getFolderId()));
    }
  }
}