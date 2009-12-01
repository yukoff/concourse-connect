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
package com.concursive.connect.web.modules.activity.portlets.activityStream;

import com.concursive.commons.date.DateUtils;
import com.concursive.commons.text.StringUtils;
import com.concursive.connect.Constants;
import com.concursive.connect.web.modules.activity.dao.ProjectHistory;
import com.concursive.connect.web.modules.activity.dao.ProjectHistoryList;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.dao.ProjectCategoryList;
import com.concursive.connect.web.modules.wiki.utils.WikiToHTMLContext;
import com.concursive.connect.web.modules.wiki.utils.WikiToHTMLUtils;
import com.concursive.connect.web.portal.IPortletViewer;
import com.concursive.connect.web.portal.PortalUtils;
import com.concursive.connect.web.utils.PagedListInfo;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * View activity stream
 *
 * @author Kailash Bhoopalam
 * @created March 04, 2009
 */
public class ActivityStreamViewer implements IPortletViewer {
  // Pages
  private static final String VIEW_PAGE = "/portlets/activity_stream/activity_stream-view.jsp";

  // Preferences
  private static final String PREF_TITLE = "title";
  private static final String PREF_CONTENT = "content";
  private static final String PREF_LIMIT = "limit";
  private static final String PREF_EVENTS = "events";//An array list
  private static final String PREF_CATEGORY = "category";//An array list

  // Object Results
  private static final String TITLE = "title";
  private static final String CONTENT = "content";
  private static final String PROJECT_HISTORY_LIST = "projectHistoryList";
  private static final String PROJECT_HISTORY_ARRAY_LIST = "projectHistoryArrayList";
  private static final String EVENT_ARRAY_LIST = "eventArrayList";

  public String doView(RenderRequest request, RenderResponse response)
      throws Exception {
    // The JSP to show upon success
    String defaultView = VIEW_PAGE;

    String limit = request.getPreferences().getValue(PREF_LIMIT, "10");
    // General display preferences
    request.setAttribute(TITLE, request.getPreferences().getValue(PREF_TITLE, "Recent Activity"));
    request.setAttribute(CONTENT, request.getPreferences().getValue(PREF_CONTENT, null));

    Project project = PortalUtils.findProject(request);
    Connection db = PortalUtils.getConnection(request);
    ArrayList eventArrayList = getEventPreferences(request);

    // Query the activity stream data
    ProjectHistoryList projectHistoryList = new ProjectHistoryList();
    PagedListInfo projectHistoryListInfo = PortalUtils.getPagedListInfo(request, "projectHistoryListInfo");
    projectHistoryListInfo.setItemsPerPage(Integer.parseInt(limit));
    projectHistoryList.setPagedListInfo(projectHistoryListInfo);
    projectHistoryList.setObjectPreferences(eventArrayList);
    projectHistoryList.setUntilLinkStartDate(new Timestamp(System.currentTimeMillis()));

    // Determine if the portlet is on a project page
    if (project != null && project.getId() != -1) {
      // Determine if this is a user's page
      User user = PortalUtils.getUser(request);
      if (user.getProfileProjectId() == project.getId()) {
        // Show the user's activity
        projectHistoryList.setEnteredBy(user.getId());
      } else {
        // Show the project's activity
        projectHistoryList.setProjectId(project.getId());
      }
    } else {
      // Constrain to the current instance
      projectHistoryList.setInstanceId(PortalUtils.getInstance(request).getId());
      // Determine if a specific category is specified
      String projectCategoryName = request.getPreferences().getValue(PREF_CATEGORY, null);
      int projectCategoryId = -1;
      if (StringUtils.hasText(projectCategoryName)) {
        ProjectCategoryList projectCategoryList = (ProjectCategoryList) request.getAttribute(Constants.REQUEST_TAB_CATEGORY_LIST);
        if (projectCategoryList.size() > 0) {
          projectCategoryId = projectCategoryList.getIdFromValue(projectCategoryName);
        }
        projectHistoryList.setProjectCategoryId(projectCategoryId);
      }
    }

    // Determine which data is returned
    User thisUser = null;
    if (PortalUtils.getDashboardPortlet(request).isCached()) {
      if (PortalUtils.canShowSensitiveData(request)) {
        // Need to use a participant's setting here, this user is fine
        // because this is for building the wiki links, not the data
        thisUser = PortalUtils.getUser(request);
        // Limit the data to a participant
        projectHistoryList.setForParticipant(Constants.TRUE);
      } else {
        // Use the most generic settings since this portlet is cached
        projectHistoryList.setPublicProjects(Constants.TRUE);
      }
    } else {
      // Use the current user's setting
      thisUser = PortalUtils.getUser(request);
      projectHistoryList.setForUser(thisUser.getId());
    }
    projectHistoryList.buildList(db);
    request.setAttribute(PROJECT_HISTORY_LIST, projectHistoryList);

    // Go through the activities and organize into lists for the view to render
    ArrayList<ArrayList> activityStreamList = new ArrayList<ArrayList>();
    // A new list for each day
    ArrayList<ArrayList> dayList = null;
    Timestamp lastDate = null;
    // Per day, each user has a new list
    ArrayList<ProjectHistory> eventList = null;
    int lastId = -1;

    for (ProjectHistory projectHistory : projectHistoryList) {
      // This is a new day
      if (lastDate == null ||
          DateUtils.isAtleastNextDay(projectHistory.getLinkStartDate().getTime(), lastDate.getTime())) {
        // This is a new date so create a new array for storing into dayList
        dayList = new ArrayList<ArrayList>();
        activityStreamList.add(dayList);
        // New day, new people
        lastId = -1;
      }
      // This is a new day or this is a new event owner
      int thisOwner = projectHistory.getUser().getId();
      if (projectHistory.getEventType() == ProjectHistoryList.ADD_ACTIVITY_ENTRY_EVENT) {
        // This event type will have a custom handler
        thisOwner = 0;
      }
      if (lastId == -1 || thisOwner != lastId) {
        eventList = new ArrayList<ProjectHistory>();
        dayList.add(eventList);
      }
      // Parse the wiki content
      int userId = -1;
      if (thisUser != null) {
        userId = thisUser.getId();
      }
      WikiToHTMLContext wikiToHTMLContext = new WikiToHTMLContext(userId, request.getContextPath());
      String wikiLinkString = WikiToHTMLUtils.getHTML(wikiToHTMLContext, db, projectHistory.getDescription());
      projectHistory.setHtmlLink(wikiLinkString);
      // Add the activity stream item to the person
      eventList.add(projectHistory);
      // Update the pointers
      lastDate = projectHistory.getLinkStartDate();
      lastId = thisOwner;
    }

    // Determine if the portlet should be shown
    if (activityStreamList.size() > 0) {
      request.setAttribute(PROJECT_HISTORY_ARRAY_LIST, activityStreamList);
      request.setAttribute(EVENT_ARRAY_LIST, eventArrayList);
      // Show the view
      return defaultView;
    } else {
      return null;
    }
  }

  private ArrayList<String> getEventPreferences(RenderRequest request) {
    ArrayList<String> objectPreference = null;
    String[] objectPreferences = request.getPreferences().getValues(PREF_EVENTS, null);
    if (objectPreferences != null) {
      objectPreference = new ArrayList<String>();
      int numberOfPreferences = objectPreferences.length;
      int count = 0;
      while (count < numberOfPreferences) {
        String preferenceName = objectPreferences[count];
        objectPreference.add(preferenceName);
        count++;
      }
    }
    return objectPreference;
  }
}
