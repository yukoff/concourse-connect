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
package com.concursive.connect.web.modules.common.social.contribution.portlets.topContributions;

import com.concursive.commons.text.StringUtils;
import com.concursive.connect.Constants;
import com.concursive.connect.web.modules.common.social.contribution.dao.UserContributionLog;
import com.concursive.connect.web.modules.common.social.contribution.dao.UserContributionLogList;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.login.utils.UserUtils;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.dao.ProjectCategoryList;
import com.concursive.connect.web.portal.IPortletViewer;
import com.concursive.connect.web.portal.PortalUtils;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * Provides a list of users with the highest contribution based on several preferences
 *
 * @author Kailash Bhoopalam
 * @created January 22, 2009
 */
public class TopContributionsViewer implements IPortletViewer {

  // Pages
  private static final String VIEW_PAGE = "/portlets/top_contributions/top_contributions-view.jsp";

  // Preferences
  private static final String PREF_TITLE = "title";
  private static final String PREF_LIMIT = "limit";
  private static final String PREF_DAYS_LIMIT = "daysLimit";
  private static final String PREF_CATEGORY = "category";

  // Object Results
  private static final String TITLE = "title";
  private static final String TOP_CONTRIBUTOR_LIST = "topContributorList";

  public String doView(RenderRequest request, RenderResponse response)
      throws Exception {

    String defaultView = VIEW_PAGE;
    request.setAttribute(TITLE, request.getPreferences().getValue(PREF_TITLE, null));
    int limit = Integer.parseInt(request.getPreferences().getValue(PREF_LIMIT, "-1"));
    String daysLimit = request.getPreferences().getValue(PREF_DAYS_LIMIT, "1");
    String projectCategoryName = request.getPreferences().getValue(PREF_CATEGORY, null);

    Connection db = PortalUtils.getConnection(request);
    Project project = PortalUtils.findProject(request);

    UserContributionLogList thisUserContributionLogList = new UserContributionLogList();
    thisUserContributionLogList.setSinceContributionDate(new Timestamp(System.currentTimeMillis() - 1000L * 60 * 60 * 24 * Long.parseLong(daysLimit)));

    if (project != null && project.getId() != -1) {
      thisUserContributionLogList.setProjectId(project.getId());
    } else {
      thisUserContributionLogList.setInstanceId(PortalUtils.getInstance(request).getId());
      int projectCategoryId = -1;
      if (StringUtils.hasText(projectCategoryName)) {
        ProjectCategoryList projectCategoryList = (ProjectCategoryList) request.getAttribute(Constants.REQUEST_TAB_CATEGORY_LIST);
        if (projectCategoryList.size() > 0) {
          projectCategoryId = projectCategoryList.getIdFromValue(projectCategoryName);
        }
        thisUserContributionLogList.setProjectCategoryId(projectCategoryId);
      }
    }
    thisUserContributionLogList.buildTopUsers(db, limit);

    // Convert the user ids and points into a user list
    ArrayList<User> topContributorList = prepareSortedList(thisUserContributionLogList);
    request.setAttribute(TOP_CONTRIBUTOR_LIST, topContributorList);

    return defaultView;
  }

  /**
   * @param thisUserContributionLogList
   * @return a list of top contributors
   */
  private ArrayList<User> prepareSortedList(UserContributionLogList thisUserContributionLogList) {
    // Create a list of users
    ArrayList<User> topContributors = new ArrayList<User>();
    for (UserContributionLog userContributionLog : thisUserContributionLogList) {
      int userId = userContributionLog.getUserId();
      // Load the user for the array
      User thisUser = UserUtils.loadUser(userId);
      // Add the user
      topContributors.add(thisUser);
    }
    return topContributors;
  }
}
