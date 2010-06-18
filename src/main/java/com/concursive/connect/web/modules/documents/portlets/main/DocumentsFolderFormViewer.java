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

import com.concursive.connect.Constants;
import com.concursive.connect.web.modules.documents.dao.FileFolder;
import com.concursive.connect.web.modules.documents.dao.FileFolderList;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import com.concursive.connect.web.portal.IPortletViewer;
import com.concursive.connect.web.portal.PortalUtils;
import static com.concursive.connect.web.portal.PortalUtils.*;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import java.sql.Connection;

/**
 * Project documents folder form
 *
 * @author matt rajkowski
 * @created January 23, 2009
 */
public class DocumentsFolderFormViewer implements IPortletViewer {

  // Pages
  private static final String VIEW_PAGE = "/projects_center_file_folder.jsp";

  // Object Results
  private static final String CURRENT_FOLDER = "currentFolder";
  private static final String FOLDER = "fileFolder";
  private static final String FOLDER_LIST = "folderList";

  public String doView(RenderRequest request, RenderResponse response) throws Exception {
    // The JSP to show upon success
    String defaultView = VIEW_PAGE;

    // Determine the project container to use
    Project project = findProject(request);

    // Check the request for the record and provide a value for the request scope
    FileFolder thisItem = (FileFolder) PortalUtils.getFormBean(request, FOLDER, FileFolder.class);

    // Determine the connection to use
    Connection db = useConnection(request);

    // Generate a list of folders which will be used for display
    FileFolderList folderList = new FileFolderList();
    folderList.setLinkModuleId(Constants.PROJECTS_FILES);
    folderList.setLinkItemId(project.getId());
    folderList.buildList(db);
    request.setAttribute(FOLDER_LIST, folderList);

    // Determine the parent folder and validate
    int parentId = -1;
    String folderIdValue = request.getParameter("parent");
    if (folderIdValue != null) {
      parentId = Integer.parseInt(folderIdValue);
    } else {
      parentId = thisItem.getParentId();
    }
    if (parentId > -1 && !folderList.hasFolder(parentId)) {
      throw new PortletException("Unauthorized folder specified");
    }

    // Provide the folder to the view
    if (parentId > -1) {
      FileFolder currentFolder = folderList.getFolder(parentId);
      request.setAttribute(CURRENT_FOLDER, currentFolder);
    }

    // Determine if adding or updating a record
    int recordId = -1;
    String action = getPageAction(request);
    if ("create".equals(action)) {
      // A new file is being uploaded
      thisItem.setParentId(parentId);
    } else if ("modify".equals(action)) {
      // A folder is being modified
      recordId = getPageViewAsInt(request);
      // Load the folder to be modified
      thisItem.queryRecord(db, recordId);
    }

    // Check the user's permissions
    User user = getUser(request);
    if (recordId == -1 && !ProjectUtils.hasAccess(project.getId(), user, "project-documents-folders-add")) {
      throw new PortletException("Unauthorized to add in this project");
    } else if (recordId > -1 && !ProjectUtils.hasAccess(project.getId(), user, "project-documents-folders-edit")) {
      throw new PortletException("Unauthorized to add in this project");
    }

    // JSP view
    return defaultView;
  }
}
