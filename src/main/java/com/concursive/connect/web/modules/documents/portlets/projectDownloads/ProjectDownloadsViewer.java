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
package com.concursive.connect.web.modules.documents.portlets.projectDownloads;

import com.concursive.commons.text.StringUtils;
import com.concursive.connect.Constants;
import com.concursive.connect.web.modules.documents.dao.FileFolderList;
import com.concursive.connect.web.modules.documents.dao.FileItemList;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.dao.ProjectCategory;
import com.concursive.connect.web.modules.profile.dao.ProjectCategoryList;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import com.concursive.connect.web.portal.IPortletViewer;
import static com.concursive.connect.web.portal.PortalUtils.*;
import com.concursive.connect.web.utils.PagedListInfo;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import java.sql.Connection;

/**
 * Project Downloads mvc portlet
 *
 * @author Kailash Bhoopalam
 * @created January 28, 2009
 */
public class ProjectDownloadsViewer implements IPortletViewer {

  // Pages
  private static final String VIEW_PAGE1 = "/portlets/project_downloads/project_downloads-view.jsp";

  // Preferences
  private static String PREF_TITLE = "title";
  private static String PREF_LIMIT = "limit";
  private static String PREF_PROJECT_CATEGORY = "category";
  private static String PREF_SHOW_ALL_DOWNLOADS_LINK = "showAllDownloadsLink";

  //Object results
  private static String TITLE = "title";
  private static final String FILE_LIST = "fileItemList";
  private static final String FOLDER_LIST = "folderList";
  private static final String SHOW_ALL_DOWNLOADS_LINK = "showAllDownloadsLink";
  private static final String PROJECT = "project";

  public String doView(RenderRequest request, RenderResponse response)
      throws Exception {

    String defaultView = VIEW_PAGE1;
    request.setAttribute(TITLE, request.getPreferences().getValue(PREF_TITLE, ""));

    String category = request.getPreferences().getValue(PREF_PROJECT_CATEGORY, null);
    String limit = request.getPreferences().getValue(PREF_LIMIT, "5");

    User user = getUser(request);
    Connection db = useConnection(request);
    // Look for project documents
    FileItemList fileItemList = new FileItemList();
    fileItemList.setLinkModuleId(Constants.PROJECTS_FILES);
    // Use folders for the ui
    FileFolderList folderList = new FileFolderList();
    folderList.setLinkModuleId(Constants.PROJECTS_FILES);
    // Use a pagedList for limiting and sorting records
    PagedListInfo projectFileListInfo = getPagedListInfo(request, "projectFileListInfo");
    if (StringUtils.hasText(limit) && StringUtils.isNumber(limit)) {
      projectFileListInfo.setItemsPerPage(limit);
    }
    projectFileListInfo.setColumnToSortBy("modified DESC");
    fileItemList.setPagedListInfo(projectFileListInfo);
    // Filter the records
    fileItemList.setFeaturedFilesOnly(Constants.TRUE);
    // Check if a category is specified
    if (StringUtils.hasText(category)) {
      ProjectCategory projectCategory = null;
      ProjectCategoryList categories = new ProjectCategoryList();
      categories.setEnabled(true);
      categories.setTopLevelOnly(true);
      categories.setCategoryDescriptionLowerCase(category);
      categories.buildList(db);
      if (categories.size() == 0) {
        throw new PortletException("Category not found");
      }
      projectCategory = categories.get(0);
      fileItemList.setProjectCategoryId(projectCategory.getId());
      fileItemList.setPublicProjectFiles(Constants.TRUE);
      fileItemList.buildList(db);

      // Load the folder list
      folderList.setProjectCategoryId(projectCategory.getId());
      folderList.setPublicProjectFolders(Constants.TRUE);
      folderList.buildList(db);
    } else {
      // Use a project if one can be found, otherwise use public files only
      Project project = findProject(request);
      if (project != null) {
        // A project was found in the scope
        if (!ProjectUtils.hasAccess(project.getId(), user, "project-documents-view")) {
          throw new PortletException("Unauthorized to view in this project");
        }
        fileItemList.setLinkItemId(project.getId());

        // Load the folder list so user can see other files in the document folders
        folderList.setLinkItemId(project.getId());
        folderList.buildList(db);
        request.setAttribute(SHOW_ALL_DOWNLOADS_LINK, request.getPreferences().getValue(PREF_SHOW_ALL_DOWNLOADS_LINK, "true"));
        request.setAttribute(PROJECT, project);
      } else {
        // No project, no category so look across categories
        fileItemList.setPublicProjectFiles(Constants.TRUE);
      }
      fileItemList.buildList(db);
    }
    if (fileItemList.size() == 0) {
      return null;
    }

    request.setAttribute(FOLDER_LIST, folderList);
    request.setAttribute(FILE_LIST, fileItemList);
    return defaultView;
  }
}
