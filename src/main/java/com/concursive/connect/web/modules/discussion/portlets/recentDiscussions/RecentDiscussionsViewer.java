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
package com.concursive.connect.web.modules.discussion.portlets.recentDiscussions;

import com.concursive.commons.text.StringUtils;
import com.concursive.connect.Constants;
import com.concursive.connect.web.modules.discussion.dao.TopicList;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.dao.ProjectCategory;
import com.concursive.connect.web.modules.profile.dao.ProjectCategoryList;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import com.concursive.connect.web.portal.IPortletViewer;
import com.concursive.connect.web.portal.PortalUtils;
import com.concursive.connect.web.utils.PagedListInfo;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import java.sql.Connection;

/**
 * Recent discussion MVC portlet
 *
 * @author Kailash Bhoopalam
 * @created January 22, 2009
 */
public class RecentDiscussionsViewer implements IPortletViewer {

  // Pages
  private static final String VIEW_PAGE = "/portlets/recent_discussions/recent_discussions-view.jsp";

  // Preferences
  private static final String PREF_TITLE = "title";
  private static final String PREF_LIMIT = "limit";
  private static final String PREF_CATEGORY = "category";
  private static final String PREF_PREF_MINIMUM_RATING_COUNT = "minimumRatingCount";
  private static final String PREF_PREF_MINIMUM_RATING_AVG = "minimumRatingAvg";
  private static final String PREF_FILTER_INAPPROPRIATE = "filterInappropriate";

  // Object Results
  private static final String TITLE = "title";
  private static final String SHOW_PROJECT_TITLE = "showProjectTitle";
  private static final String SHOW_DISCUSSION_LINK = "showDiscussionLink";
  private static final String DISCUSSION_LIST = "discussionList";

  public String doView(RenderRequest request, RenderResponse response)
      throws Exception {

    String defaultView = VIEW_PAGE;

    // Preferences
    request.setAttribute(TITLE, request.getPreferences().getValue(PREF_TITLE, null));
    String limit = request.getPreferences().getValue(PREF_LIMIT, null);

    // Filter Preferences
    String minimumRatingCount = request.getPreferences().getValue(PREF_PREF_MINIMUM_RATING_COUNT, null);
    String minimumRatingAvg = request.getPreferences().getValue(PREF_PREF_MINIMUM_RATING_AVG, null);
    String filterInappropriate = request.getPreferences().getValue(PREF_FILTER_INAPPROPRIATE, null);

    Connection db = PortalUtils.useConnection(request);

    //Get the project if available
    Project project = PortalUtils.findProject(request);

    TopicList recentTopicList = new TopicList();
    PagedListInfo recentIssueListInfo = PortalUtils.getPagedListInfo(request, "recentIssueListInfo");
    recentIssueListInfo.setColumnToSortBy("entered DESC");
    recentIssueListInfo.setItemsPerPage(limit);
    recentTopicList.setPagedListInfo(recentIssueListInfo);

    // Filters based on ratings
    if (StringUtils.hasText(minimumRatingCount) && StringUtils.isNumber(minimumRatingCount)) {
      recentTopicList.setMinimumRatingCount(minimumRatingCount);
    }
    if (StringUtils.hasText(minimumRatingAvg)) {
      recentTopicList.setMinimumRatingAvg(minimumRatingAvg);
    }
    if ("true".equals(filterInappropriate)) {
      recentTopicList.setFilterInappropriate(Constants.TRUE);
    }

    // if no project is available, look for category in preferences
    User thisUser = PortalUtils.getUser(request);
    if (project == null) {
      recentTopicList.setInstanceId(PortalUtils.getInstance(request).getId());
      boolean privateCategory = false;
      String categoryValue = request.getPreferences().getValue(PREF_CATEGORY, null);
      if (categoryValue != null) {
        ProjectCategoryList categories = new ProjectCategoryList();
        categories.setEnabled(true);
        categories.setTopLevelOnly(true);
        categories.setCategoryDescriptionLowerCase(categoryValue.toLowerCase());
        categories.buildList(db);
        if (categories.size() > 0) {
          ProjectCategory category = categories.get(0);
          if (category.getSensitive()) {
            privateCategory = true;
          }
          recentTopicList.setProjectCategoryId(category.getId());
        }
      }
      if (PortalUtils.getDashboardPortlet(request).isCached()) {
        if (PortalUtils.canShowSensitiveData(request) || privateCategory) {
          // Use the most generic settings since this portlet is cached
          recentTopicList.setForParticipant(Constants.TRUE);
        } else {
          // Use the most generic settings since this portlet is cached
          recentTopicList.setPublicProjectIssues(Constants.TRUE);
        }
      } else {
        // Use the current user's setting
        if (thisUser.isLoggedIn()) {
          recentTopicList.setForUser(thisUser.getId());
        } else {
          recentTopicList.setPublicProjectIssues(Constants.TRUE);
        }
      }
      request.setAttribute(SHOW_PROJECT_TITLE, "true");
      request.setAttribute(SHOW_DISCUSSION_LINK, "true");
    } else {
      //determine if the user has access to view discussion topics of the project
      if (ProjectUtils.hasAccess(project.getId(), thisUser, "project-discussion-forums-view")) {
        request.setAttribute(SHOW_DISCUSSION_LINK, "true");
      }
      recentTopicList.setProjectId(project.getId());
    }
    recentTopicList.buildList(db);
    if (recentTopicList.size() == 0) {
      return null;
    }
    request.setAttribute(DISCUSSION_LIST, recentTopicList);

    return defaultView;
  }
}
