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
package com.concursive.connect.web.modules.profile.portlets.newProjectsByCategory;

import com.concursive.connect.Constants;
import com.concursive.connect.web.modules.profile.dao.ProjectCategory;
import com.concursive.connect.web.modules.profile.dao.ProjectCategoryList;
import com.concursive.connect.web.modules.profile.dao.ProjectList;
import com.concursive.connect.web.portal.IPortletViewer;
import com.concursive.connect.web.portal.PortalUtils;
import com.concursive.connect.web.utils.PagedListInfo;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import java.sql.Connection;

/**
 * Display new projects by category
 *
 * @author Kailash Bhoopalam
 * @created January 15, 2008
 */
public class NewProjectsByCategoryViewer implements IPortletViewer {

  // Pages
  private static final String VIEW_PAGE1 = "/portlets/new_projects_by_category/new_projects_by_category-view.jsp";

  //request attributes
  private static final String TITLE = "title";
  private static final String PROJECT_LIST = "projectList";


  // Preferences
  private static final String PREF_TITLE = "title";
  private static final String PREF_LIMIT = "limit";
  private static final String PREF_CATEGORY = "category";

  public String doView(RenderRequest request, RenderResponse response)
      throws Exception {

    String defaultView = VIEW_PAGE1;

    // Preferences
    String categoryName = request.getPreferences().getValue(PREF_CATEGORY, "");
    String title = request.getPreferences().getValue(PREF_TITLE, "");
    String limitString = request.getPreferences().getValue(PREF_LIMIT, "0");

    request.setAttribute(TITLE, title);

    //Get category id from name
    Connection db = PortalUtils.getConnection(request);

    ProjectCategoryList categories = new ProjectCategoryList();
    categories.setCategoryName(categoryName);
    categories.setEnabled(true);
    categories.setTopLevelOnly(true);
    categories.buildList(db);
    ProjectCategory category = (categories.size() > 0 ? categories.get(0) : null);

    if (category != null) {
      ProjectList projectList = new ProjectList();
      projectList.setCategoryId(category.getId());
      PagedListInfo projectListInfo = new PagedListInfo();
      projectListInfo.setItemsPerPage(Integer.parseInt(limitString));
      projectListInfo.setDefaultSort("entered", "DESC");
      projectList.setPagedListInfo(projectListInfo);
      if (PortalUtils.canShowSensitiveData(request)) {
        // Use the most generic settings since this portlet is cached
        projectList.setForParticipant(Constants.TRUE);
      } else {
        // Use the most generic settings since this portlet is cached
        projectList.setPublicOnly(true);
      }
      projectList.setProfileEnabled(Constants.TRUE);
      projectList.setBuildImages(true);
      projectList.buildList(db);
      request.setAttribute(PROJECT_LIST, projectList);
      if (projectList.size() == 0) {
        return null;
      }
    }
    // JSP view
    return defaultView;
  }
}
