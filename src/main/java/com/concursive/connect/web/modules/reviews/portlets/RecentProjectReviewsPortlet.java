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
import com.concursive.connect.Constants;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.dao.ProjectCategoryList;
import com.concursive.connect.web.modules.reviews.dao.ProjectRatingList;
import com.concursive.connect.web.portal.PortalUtils;
import com.concursive.connect.web.utils.PagedListInfo;

import javax.portlet.*;
import java.io.IOException;
import java.sql.Connection;

/**
 * Recent Project Reviews portlet
 *
 * @author Kailash Bhoopalam
 * @created July 7, 2008
 */
public class RecentProjectReviewsPortlet extends GenericPortlet {

  // Pages
  private static final String VIEW_PAGE = "/portlets/recent_project_reviews/recent_project_reviews-view.jsp";

  // Request Attributes
  private static final String PROJECT_RATING_LIST = "projectRatingList";
  private static final String PROJECT_CATEGORY_LIST = "projectCategoryList";
  private static final String TITLE = "title";
  private static final String SHOW_PROJECT_TITLE = "showProjectTitle";
  private static final String SHOW_PROJECT_CATEGORY = "showProjectCategory";

  // Preferences
  private static final String PREF_TITLE = "title";
  private static final String PREF_NO_OF_REVIEWS_TO_SHOW = "limit";
  private static final String PREF_PROJECT_ID = "projectId";
  private static final String PREF_CATEGORY = "category";
  private static final String PREF_SHOW_PROJECT_TITLE = "showProjectTitle";
  private static final String PREF_SHOW_PROJECT_CATEGORY = "showProjectCategory";

  private static final String PREF_SORT_BY_RATING = "sortByRating";
  private static final String PREF_SORT_BY_RATING_AVG = "sortByRatingAvg";
  private static final String PREF_PREF_MINIMUM_RATING_COUNT = "minimumRatingCount";
  private static final String PREF_PREF_MINIMUM_RATING_AVG = "minimumRatingAvg";
  private static final String PREF_FILTER_INAPPROPRIATE = "filterInappropriate";

  public void doView(RenderRequest request, RenderResponse response)
      throws PortletException, IOException {
    try {
      String defaultView = VIEW_PAGE;
      User thisUser = PortalUtils.getUser(request);

      // Get global preferences
      String categoryName = request.getPreferences().getValue(PREF_CATEGORY, "");
      String title = request.getPreferences().getValue(PREF_TITLE, "");
      String showProjectTitle = request.getPreferences().getValue(PREF_SHOW_PROJECT_TITLE, "false");
      String showProjectCategory = request.getPreferences().getValue(PREF_SHOW_PROJECT_CATEGORY, "false");
      String noOfReviewsToShow = request.getPreferences().getValue(PREF_NO_OF_REVIEWS_TO_SHOW, "-1");

      String sortByRating = request.getPreferences().getValue(PREF_SORT_BY_RATING, null);
      String sortByRatingAvg = request.getPreferences().getValue(PREF_SORT_BY_RATING_AVG, null);
      String minimumRatingCount = request.getPreferences().getValue(PREF_PREF_MINIMUM_RATING_COUNT, null);
      String minimumRatingAvg = request.getPreferences().getValue(PREF_PREF_MINIMUM_RATING_AVG, null);
      String filterInappropriate = request.getPreferences().getValue(PREF_FILTER_INAPPROPRIATE, null);

      // Determine if a specific project should be used
      String projectId = request.getPreferences().getValue(PREF_PROJECT_ID, "-1");

      // Check the current project profile page
      if ("-1".equals(projectId) && "".equals(categoryName)) {
        Project project = PortalUtils.findProject(request);
        if (project != null) {
          projectId = String.valueOf(project.getId());
        }
      }

      try {
        Connection db = PortalUtils.getConnection(request);

        ProjectCategoryList categories = new ProjectCategoryList();
        categories.setEnabled(true);
        categories.setTopLevelOnly(true);
        categories.buildList(db);
        request.setAttribute(PROJECT_CATEGORY_LIST, categories);

        int categoryId = -1;
        if (!"".equals(categoryName)) {
          categoryId = categories.size() > 0 ? categories.getIdFromValue(categoryName) : -1;
        }

        // Use paged data for sorting
        PagedListInfo pagedListInfo = new PagedListInfo();
        pagedListInfo.setItemsPerPage(noOfReviewsToShow);
        StringBuffer columnsToSort = new StringBuffer();
        columnsToSort.append("entered desc");

        if (StringUtils.hasText(sortByRating) && ("asc".equals(sortByRating) || "desc".equals(sortByRating))) {
          columnsToSort.append(", rating " + sortByRating);
        }
        if (StringUtils.hasText(sortByRatingAvg) && ("asc".equals(sortByRatingAvg) || "desc".equals(sortByRatingAvg))) {
          columnsToSort.append(", rating_avg " + sortByRatingAvg);
        }
        pagedListInfo.setColumnToSortBy(columnsToSort.toString());

        // Projects to show
        ProjectRatingList projectRatingList = new ProjectRatingList();
        projectRatingList.setPagedListInfo(pagedListInfo);
        projectRatingList.setGroupId(thisUser.getGroupId());
        projectRatingList.setCategoryId(categoryId);
        projectRatingList.setProjectId(projectId);
        projectRatingList.setLoadProject(true);
        projectRatingList.setOpenProjectsOnly(true);
        if ("-1".equals(projectId)) {
          projectRatingList.setPublicProjects(Constants.TRUE);
        }
        if (StringUtils.hasText(minimumRatingCount) && StringUtils.isNumber(minimumRatingCount)) {
          projectRatingList.setMinimumRatingCount(minimumRatingCount);
        }
        if (StringUtils.hasText(minimumRatingAvg)) {
          projectRatingList.setMinimumRatingAvg(minimumRatingAvg);
        }
        if ("true".equals(filterInappropriate)) {
          projectRatingList.setFilterInappropriate(Constants.TRUE);
        }

        projectRatingList.buildList(PortalUtils.getConnection(request));
        request.setAttribute(PROJECT_RATING_LIST, projectRatingList);

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