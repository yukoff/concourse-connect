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
import com.concursive.connect.web.modules.documents.dao.FileItem;
import com.concursive.connect.web.modules.documents.dao.FileItemVersionList;
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
import java.util.LinkedHashMap;

/**
 * Project documents file form
 *
 * @author matt rajkowski
 * @created January 23, 2009
 */
public class DocumentsFileFormViewer implements IPortletViewer {

  // Pages
  private static final String VIEW_PAGE = "/projects_center_file_upload.jsp";
  private static final String MODIFY_VIEW_PAGE = "/projects_center_file_modify.jsp";
  private static final String FILE_LIMIT_VIEW_PAGE = "/projects_center_file_limit.jsp";

  // Object Results
  private static final String FILE_ITEM = "fileItem";
  private static final String CURRENT_FOLDER_ID = "currentFolderId";
  private static final String CURRENT_FOLDER = "currentFolder";
  private static final String FOLDER_HIERARCHY = "folderLevels";
  private static final String FOLDER_LIST = "folderList";

  public String doView(RenderRequest request, RenderResponse response) throws Exception {
    // The JSP to show upon success
    String defaultView = VIEW_PAGE;

    // Determine the project container to use
    Project project = findProject(request);

    // Determine the record to show
    int recordId = getPageViewAsInt(request);

    // Check the user's permissions
    User user = getUser(request);
    if (recordId == -1 && !ProjectUtils.hasAccess(project.getId(), user, "project-documents-files-upload")) {
      throw new PortletException("Unauthorized to add this record");
    } else if (recordId > -1 && !ProjectUtils.hasAccess(project.getId(), user, "project-documents-files-rename")) {
      throw new PortletException("Unauthorized to modify this record");
    }

    // Check the request for the record and provide a value for the request scope
    FileItem thisItem = (FileItem) PortalUtils.getFormBean(request, FILE_ITEM, FileItem.class);

    // Determine the database connection
    Connection db = getConnection(request);

    // Generate a list of folders which will be displayed in a drop-down
    FileFolderList folderList = new FileFolderList();
    folderList.setLinkModuleId(Constants.PROJECTS_FILES);
    folderList.setLinkItemId(project.getId());
    folderList.buildList(db);
    request.setAttribute(FOLDER_LIST, folderList);

    // Process the current folder information
    int folderId = -1;
    String folderIdValue = request.getParameter("folder");
    if (folderIdValue != null) {
      folderId = Integer.parseInt(folderIdValue);
    } else {
      folderId = thisItem.getFolderId();
    }
    if (folderId > -1 && !folderList.hasFolder(folderId)) {
      throw new PortletException("Unauthorized folder specified");
    }
    request.setAttribute(CURRENT_FOLDER_ID, String.valueOf(folderId));

    // Provide the folder to the view
    if (folderId > -1) {
      FileFolder currentFolder = folderList.getFolder(folderId);
      request.setAttribute(CURRENT_FOLDER, currentFolder);
    }

    // Determine if adding or updating
    String action = getPageAction(request);
    if ("create".equals(action)) {

      // Check the user's account size first
      if (user.getAccountSize() > -1) {
        // Refresh the user's account size
        user.setCurrentAccountSize(FileItemVersionList.queryOwnerSize(db, user.getId()));
        if (!user.isWithinAccountSize()) {
          defaultView = FILE_LIMIT_VIEW_PAGE;
        }
      }

      // Determine if this is a new file or new version of a file
      String object = getPageDomainObject(request);
      if ("file".equals(object)) {
        // A new file is being uploaded
        thisItem.setFolderId(folderId);
      } else if ("file-version".equals(object)) {
        // A version is being uploaded to an existing file
        thisItem = new FileItem(db, recordId, project.getId(), Constants.PROJECTS_FILES);
        request.setAttribute(FILE_ITEM, thisItem);
      }

    } else if ("modify".equals(action)) {
      // Load the file item to be modified
      thisItem = new FileItem(db, recordId, project.getId(), Constants.PROJECTS_FILES);
      thisItem.buildVersionList(db);
      request.setAttribute(FILE_ITEM, thisItem);
      defaultView = MODIFY_VIEW_PAGE;
    }

    // Show the folder hierarchy
    if (folderId > 0) {
      LinkedHashMap folderLevels = new LinkedHashMap();
      FileFolder.buildHierarchy(db, folderLevels, folderId);
      request.setAttribute(FOLDER_HIERARCHY, folderLevels);
    }

    // JSP view
    return defaultView;
  }
}
