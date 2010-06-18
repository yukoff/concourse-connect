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
package com.concursive.connect.web.modules.profile.portlets;

import com.concursive.connect.Constants;
import com.concursive.connect.web.modules.badges.dao.Badge;
import com.concursive.connect.web.modules.badges.dao.BadgeList;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.dao.ProjectCategory;
import com.concursive.connect.web.modules.profile.dao.ProjectCategoryList;
import com.concursive.connect.web.modules.profile.dao.ProjectList;
import com.concursive.connect.web.portal.PortalUtils;
import com.concursive.connect.web.utils.PagedListInfo;

import javax.portlet.*;
import java.io.IOException;
import java.sql.Connection;

/**
 * Project list portlet
 *
 * @author matt rajkowski
 * @created May 14, 2008
 */
public class ProjectListByCategoryPortlet extends GenericPortlet {

  // Pages
  private static final String VIEW_PAGE = "/portlets/project_list_by_category/project_list_by_category-view.jsp";
  // Parameters
  private static final String PROJECT_LIST = "projectList";
  private static final String TITLE = "title";
  // Preferences
  private static final String PREF_TITLE = "title";
  private static final String PREF_CATEGORIES = "category";
  private static final String PREF_BADGES = "badge";
  private static final String PREF_FOR_USER = "forUser";

  public void doView(RenderRequest request, RenderResponse response)
      throws PortletException, IOException {
    String defaultView = VIEW_PAGE;

    // Determine the user
    User user = PortalUtils.getUser(request);

    // Determine if the portlet is on a project page
    Project project = PortalUtils.findProject(request);

    // Process the preferences
    String title = request.getPreferences().getValue(PREF_TITLE, null);
    boolean forUser = Boolean.parseBoolean(request.getPreferences().getValue(PREF_FOR_USER, "false"));

    try {
      // Determine the database connection
      Connection db = PortalUtils.useConnection(request);


      // Find the specified categories, if they are enabled
      ProjectCategoryList allowedCategoryList = new ProjectCategoryList();
      String allowedCategories = request.getPreferences().getValue(PREF_CATEGORIES, null);
      if (allowedCategories != null) {
        // A list of possible categories
        ProjectCategoryList categories = new ProjectCategoryList();
        categories.setEnabled(true);
        categories.setTopLevelOnly(true);
        categories.buildList(db);
        // Find the specific categories
        String[] categoryArray = allowedCategories.split(",");
        for (String thisCategory : categoryArray) {
          ProjectCategory allowedCategory = categories.getFromValue(thisCategory.trim());
          if (allowedCategory != null) {
            allowedCategoryList.add(allowedCategory);
          }
        }
      }

      // Determine if projects are limited by badge
      BadgeList allowedBadgeList = new BadgeList();
      String allowedBadges = request.getPreferences().getValue(PREF_BADGES, null);
      if (allowedBadges != null) {
        // Load the badges for comparison
        BadgeList badges = new BadgeList();
        badges.setEnabled(Constants.TRUE);
        badges.buildList(db);
        // Find the badges
        String[] badgeArray = allowedBadges.split(",");
        for (String thisBadge : badgeArray) {
          Badge allowedBadge = badges.getFromValue(thisBadge.trim());
          if (allowedBadge != null) {
            allowedBadgeList.add(allowedBadge);
          }
        }
      }

      // Show the category name if there is just one
      if (title == null && allowedCategoryList.size() == 1) {
        title = allowedCategoryList.get(0).getLabel();
      }

      // Set the title
      request.setAttribute(TITLE, title);

      // Use paged data for sorting the projects
      PagedListInfo pagedListInfo = new PagedListInfo();
      pagedListInfo.setItemsPerPage(0);

      // Projects to show
      ProjectList projects = new ProjectList();
      projects.setPagedListInfo(pagedListInfo);
      projects.setCategoryList(allowedCategoryList);
      projects.setBadgeList(allowedBadgeList);
      projects.setPortalState(Constants.FALSE);
      projects.setOpenProjectsOnly(true);
      projects.setProfile(Constants.FALSE);

      // If showing just this user's projects...
      if (forUser) {
        if (user.getId() < 1) {
          throw new Exception("No access to view projects");
        }
        projects.setGroupId(user.getGroupId());
        projects.setProjectsForUser(user.getId());
        projects.setInvitationAcceptedOnly(true);
      }

      // Build the list
      if (project == null || (forUser && project != null && project.getProfile() && project.getOwner() == user.getId())) {
        // Perform the query
        projects.buildList(db);
        request.setAttribute(PROJECT_LIST, projects);

        if (projects.size() > 0) {
          // JSP view
          PortletContext context = getPortletContext();
          PortletRequestDispatcher requestDispatcher =
              context.getRequestDispatcher(defaultView);
          requestDispatcher.include(request, response);
        }
      }
    } catch (Exception e) {
      // Don't show the portlet
    }
  }
}