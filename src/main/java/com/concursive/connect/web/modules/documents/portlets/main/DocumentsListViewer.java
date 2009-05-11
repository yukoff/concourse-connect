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
import com.concursive.connect.web.modules.documents.dao.FileItemList;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import com.concursive.connect.web.portal.IPortletViewer;
import com.concursive.connect.web.portal.PortalUtils;
import static com.concursive.connect.web.portal.PortalUtils.*;
import com.concursive.connect.web.utils.PagedListInfo;

import javax.portlet.PortletException;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import java.sql.Connection;

/**
 * Project document list
 *
 * @author matt rajkowski
 * @created January 21, 2009
 */
public class DocumentsListViewer implements IPortletViewer {

  // Pages
  private static final String VIEW_PAGE = "/projects_center_file_library.jsp";

  // Object Results
  private static final String CURRENT_FOLDER = "currentFolder";
  private static final String FILE_LIST = "fileItemList";
  private static final String FOLDER_LIST = "folderList";
  private static final String PAGED_LIST_INFO = "projectFileItemListInfo";

  public String doView(RenderRequest request, RenderResponse response) throws Exception {
    // The JSP to show upon success
    String defaultView = VIEW_PAGE;

    // Determine the project container to use
    Project project = findProject(request);

    // Check the user's permissions
    User user = getUser(request);
    if (!ProjectUtils.hasAccess(project.getId(), user, "project-documents-view")) {
      throw new PortletException("Unauthorized to view in this project");
    }

    // Determine the database connection to use
    Connection db = getConnection(request);

    // Determine the paging url
    PortletURL renderURL = response.createRenderURL();
    String url = renderURL.toString();

    // Paging will be used for remembering several list view settings
    PagedListInfo pagedListInfo = getPagedListInfo(request, PAGED_LIST_INFO);
    pagedListInfo.setLink(url);
    pagedListInfo.setItemsPerPage(-1);

    // Prepare a list of files to be returned
    FileItemList files = new FileItemList();
    files.setLinkModuleId(Constants.PROJECTS_FILES);
    files.setLinkItemId(project.getId());
    files.setPagedListInfo(pagedListInfo);

    // Determine the current view
    String domainObject = getPageDomainObject(request);
    if ("folder".equals(domainObject)) {
      // A specific folder is requested
      FileFolder fileFolder = new FileFolder(db, getPageViewAsInt(request));
      if (fileFolder.getLinkModuleId() != Constants.PROJECTS_FILES || fileFolder.getLinkItemId() != project.getId()) {
        throw new PortletException("Project id mismatch");
      }
      files.setFolderId(fileFolder.getId());
      request.setAttribute(CURRENT_FOLDER, fileFolder);
      PortalUtils.setGeneratedData(request, CURRENT_FOLDER, fileFolder);
    } else {
      PortalUtils.setGeneratedData(request, CURRENT_FOLDER, new FileFolder());
    }
    if ("date".equals(getPageView(request))) {
      // A specific month is requested
      String filterParam = getPageParameter(request);
      files.setModifiedYearMonth(filterParam);
      request.setAttribute("currentDate", filterParam);
    }

    // Determine the sort order parameter
    String sortOrder = request.getParameter("view");
    if (sortOrder == null || "recent".equals(sortOrder)) {
      pagedListInfo.setColumnToSortBy("f.modified desc, f.subject");
    } else if ("a-z".equals(sortOrder)) {
      pagedListInfo.setColumnToSortBy("f.subject");
    }

    // Load the records
    files.buildList(db);
    request.setAttribute(FILE_LIST, files);

    // Load the folder list
    FileFolderList folderList = new FileFolderList();
    folderList.setLinkModuleId(Constants.PROJECTS_FILES);
    folderList.setLinkItemId(project.getId());
    folderList.buildList(db);
    request.setAttribute(FOLDER_LIST, folderList);

    // Record view
    PortalUtils.processSelectHook(request, files);

    // JSP view
    return defaultView;
  }
}