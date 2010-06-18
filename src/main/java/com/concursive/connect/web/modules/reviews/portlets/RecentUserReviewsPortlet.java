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
package com.concursive.connect.web.modules.reviews.portlets;

import com.concursive.commons.text.StringUtils;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.login.utils.UserUtils;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.dao.ProjectCategoryList;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import com.concursive.connect.web.modules.reviews.dao.ProjectRatingList;
import com.concursive.connect.web.portal.PortalUtils;
import com.concursive.connect.web.utils.PagedListInfo;
import com.concursive.connect.Constants;

import javax.portlet.*;
import java.io.IOException;
import java.sql.Connection;

/**
 * Recent User Reviews portlet
 *
 * @author Matt Rajkowski
 * @created August 12, 2008
 */
public class RecentUserReviewsPortlet extends GenericPortlet {

  // Pages
  private static final String VIEW_PAGE = "/portlets/recent_user_reviews/recent_user_reviews-view.jsp";

  // Request Attributes
  private static final String PROJECT_RATING_LIST = "projectRatingList";
  private static final String PROJECT_CATEGORY_LIST = "projectCategoryList";
  private static final String TITLE = "title";
  private static final String SHOW_PROJECT_TITLE = "showProjectTitle";
  private static final String SHOW_PROJECT_CATEGORY = "showProjectCategory";
  private static final String PAGED_LIST_INFO = "pagedListInfo";
  private static final String USER = "profileUser";

  // Preferences
  private static final String PREF_TITLE = "title";
  private static final String PREF_NO_OF_REVIEWS_TO_SHOW = "limit";
  private static final String PREF_USER_PROFILE_NAME = "profile";
  private static final String PREF_CATEGORY = "category";
  private static final String PREF_SHOW_PROJECT_TITLE = "showProjectTitle";
  private static final String PREF_SHOW_PROJECT_CATEGORY = "showProjectCategory";

  public void doView(RenderRequest request, RenderResponse response)
      throws PortletException, IOException {
    try {
      String defaultView = VIEW_PAGE;

      // Get global preferences
      String categoryName = request.getPreferences().getValue(PREF_CATEGORY, "");
      String title = request.getPreferences().getValue(PREF_TITLE, "");
      String showProjectTitle = request.getPreferences().getValue(PREF_SHOW_PROJECT_TITLE, "false");
      String showProjectCategory = request.getPreferences().getValue(PREF_SHOW_PROJECT_CATEGORY, "false");
      String noOfReviewsToShow = request.getPreferences().getValue(PREF_NO_OF_REVIEWS_TO_SHOW, "-1");

      // Get the user's reviews...
      int userId = -1;

      // Determine if a specified user's reviews should be used from prefs
      String userProfileName = request.getPreferences().getValue(PREF_USER_PROFILE_NAME, null);
      if (userProfileName != null) {
        int projectId = ProjectUtils.retrieveProjectIdFromUniqueId(userProfileName);
        if (projectId > -1) {
          Project userProfileProject = ProjectUtils.loadProject(projectId);
          if (userProfileProject.getProfile()) {
            userId = userProfileProject.getOwner();
          }
        }
      }

      // This portlet can consume data from other portlets so check that next
      for (String event : PortalUtils.getDashboardPortlet(request).getConsumeDataEvents()) {
        Project userProfileProject = (Project) PortalUtils.getGeneratedData(request, event);
        if (userProfileProject != null) {
          if (userProfileProject.getProfile()) {
            userId = userProfileProject.getOwner();
          }
        }
      }

      // Use the currently displaying project to determine the user Id (last check)
      if (userId == -1) {
        Project userProfileProject = PortalUtils.getProject(request);
        if (userProfileProject != null) {
          if (userProfileProject.getProfile()) {
            userId = userProfileProject.getOwner();
          }
        }
      }

      if (userId == -1) {
        throw new PortletException("Portlet not configured... userId not found");
      }

      try {
        Connection db = PortalUtils.useConnection(request);

        User profileUser = UserUtils.loadUser(userId);
        request.setAttribute(USER, profileUser);

        // Limit the reviews to a specific category if specified
        int categoryId = -1;
        if (StringUtils.hasText(categoryName)) {
          ProjectCategoryList categories = new ProjectCategoryList();
          categories.setEnabled(true);
          categories.setTopLevelOnly(true);
          categories.buildList(db);
          request.setAttribute(PROJECT_CATEGORY_LIST, categories);
          categoryId = categories.getIdFromValue(categoryName);
        }

        // Use paged data for sorting
        PagedListInfo pagedListInfo = new PagedListInfo();
        pagedListInfo.setItemsPerPage(noOfReviewsToShow);
        pagedListInfo.setColumnToSortBy("entered");
        pagedListInfo.setSortOrder("desc");
        request.setAttribute(PAGED_LIST_INFO, pagedListInfo);

        // Reviews to show
        ProjectRatingList projectRatingList = new ProjectRatingList();
        projectRatingList.setPagedListInfo(pagedListInfo);
        projectRatingList.setCategoryId(categoryId);
        projectRatingList.setEnteredBy(userId);
        projectRatingList.setLoadProject(true);
        projectRatingList.setOpenProjectsOnly(true);
        // Determine the permissions for viewing the portlet
        if (PortalUtils.getDashboardPortlet(request).isCached()) {
          // Use the most generic settings since this portlet is cached
          User thisUser = UserUtils.createGuestUser();
          projectRatingList.setGroupId(thisUser.getGroupId());
          projectRatingList.setForUser(thisUser.getId());
        } else {
          // Use the current user's setting
          User thisUser = PortalUtils.getUser(request);
          projectRatingList.setGroupId(thisUser.getGroupId());
          projectRatingList.setForUser(thisUser.getId());
        }
        projectRatingList.buildList(PortalUtils.useConnection(request));
        request.setAttribute(PROJECT_RATING_LIST, projectRatingList);

        // Display preferences...
        request.setAttribute(TITLE, title);
        request.setAttribute(SHOW_PROJECT_TITLE, showProjectTitle);
        request.setAttribute(SHOW_PROJECT_CATEGORY, showProjectCategory);

      } catch (Exception e) {
        e.printStackTrace();
      }

      // JSP view
      PortletContext context = getPortletContext();
      PortletRequestDispatcher requestDispatcher =
          context.getRequestDispatcher(defaultView);
      requestDispatcher.include(request, response);
    } catch (Exception e) {
      throw new PortletException(e.getMessage());
    }
  }
}