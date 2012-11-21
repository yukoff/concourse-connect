/*
 * ConcourseConnect
 * Copyright 2010 Concursive Corporation
 * http://www.concursive.com
 *
 * This file is part of ConcourseConnect and is licensed under a commercial
 * license, not an open source license.
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
import com.concursive.connect.web.modules.activity.utils.ProjectHistoryUtils;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.dao.ProjectCategoryList;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
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
  private static final String PREF_CATEGORY = "category";
  private static final String PREF_TITLE = "title";
  private static final String PREF_CONTENT = "content";
  private static final String PREF_LIMIT = "limit";
  private static final String PREF_EVENTS = "events";
  private static final String PREF_ALLOW_REPLIES = "allowReplies";
  private static final String PREF_SHOW_CONTROLS = "showControls";


  // Object Results
  private static final String TITLE = "title";
  private static final String CONTENT = "content";
  private static final String PROJECT_HISTORY_LIST = "projectHistoryList";
  private static final String PROJECT_HISTORY_ARRAY_LIST = "projectHistoryArrayList";
  private static final String EVENT_ARRAY_LIST = "eventArrayList";
  private static final String ALLOW_REPLIES = "allowReplies";
  private static final String SHOW_CONTROLS = "showControls";

  public String doView(RenderRequest request, RenderResponse response)
      throws Exception {

    // Read the paging parameters, which can be sent from other portlets
    String ajax = request.getParameter("ajax");
    String limit = request.getParameter("limit");
    if (limit == null) {
      // Look for the global request parameter
      limit = PortalUtils.getQueryParameter(request, "limit");
    }
    String offset = request.getParameter("offset");
    // Ajax null means non-ajax request
    if (ajax == null) {
      ajax = "false";
    }

    // General display preferences
    request.setAttribute(TITLE, request.getPreferences().getValue(PREF_TITLE, "Recent Activity"));
    request.setAttribute(ALLOW_REPLIES, request.getPreferences().getValue(PREF_ALLOW_REPLIES, "true"));
    request.setAttribute(CONTENT, request.getPreferences().getValue(PREF_CONTENT, null));
    String prefLimit = request.getPreferences().getValue(PREF_LIMIT, "10");
    // Change the limit for showing more entries if limit is not null
    if (limit == null) {
      limit = prefLimit;
    }

    Project project = PortalUtils.findProject(request);
    User user = PortalUtils.getUser(request);
    Connection db = PortalUtils.useConnection(request);

    // Query the activity stream
    ProjectHistoryList projectHistoryList = new ProjectHistoryList();
    PagedListInfo projectHistoryListInfo = PortalUtils.getPagedListInfo(request, "projectHistoryListInfo");
    projectHistoryListInfo.setItemsPerPage(Integer.parseInt(limit));
    if (offset != null) {
      projectHistoryListInfo.setCurrentOffset(offset);
    }
    projectHistoryList.setPagedListInfo(projectHistoryListInfo);
    projectHistoryList.setUntilLinkStartDate(new Timestamp(System.currentTimeMillis()));

    // Prepare the stream type
    String streamType = request.getParameter("streamType");
    if (streamType == null) {
      streamType = "1";
    }
    request.setAttribute("streamType", streamType);

    // Determine if the portlet is on a project page
    if (project != null && project.getId() != -1) {
      // determine if the user has access to members of this profile
      if (!ProjectUtils.hasAccess(project.getId(), user, "project-profile-activity-view")) {
        return null;
      }
      // Determine what kind of project page
      if (user.isLoggedIn() && user.getProfileProjectId() == project.getId()) {
        // This is the current user's profile...
        if ("1".equals(streamType)) {
          // Show all activities in all of my related profiles
          projectHistoryList.setForMember(user.getId());
          // @todo Limit the object types based on access to each profile
          ArrayList<String> eventArrayList = getEventPreferences(request, null, user);
          projectHistoryList.setObjectPreferences(eventArrayList);
        } else {
          // Show just my profile's activities
          projectHistoryList.setProjectId(user.getProfileProjectId());
          // Limit the object types based on access to my profile
          ArrayList<String> eventArrayList = getEventPreferences(request, project, user);
          projectHistoryList.setObjectPreferences(eventArrayList);
        }
        request.setAttribute(SHOW_CONTROLS, request.getPreferences().getValue(PREF_SHOW_CONTROLS, "true"));
      } else if (project.getProfile()) {
        if (user.isLoggedIn()) {
          // All of the user's activities that I have access to
          projectHistoryList.setForUserUpdates(user.getId(), project.getOwner(), project.getId());
          // @todo Limit the object types based on access to each profile
          ArrayList<String> eventArrayList = getEventPreferences(request, null, user);
          projectHistoryList.setObjectPreferences(eventArrayList);
        } else {
          // Show just the user profile page activities
          projectHistoryList.setProjectId(project.getId());
          // Limit the object types based on access to this profile
          ArrayList<String> eventArrayList = getEventPreferences(request, project, user);
          projectHistoryList.setObjectPreferences(eventArrayList);
        }
      } else {
        // Show the project's activity
        projectHistoryList.setProjectId(project.getId());
        // Limit the object types based on access
        ArrayList<String> eventArrayList = getEventPreferences(request, project, user);
        projectHistoryList.setObjectPreferences(eventArrayList);
      }
    } else {
      // Constrain to the current instance
      projectHistoryList.setInstanceId(PortalUtils.getInstance(request).getId());
      // @todo Limit the object types based on access to each profile
      ArrayList<String> eventArrayList = getEventPreferences(request, null, null);
      projectHistoryList.setObjectPreferences(eventArrayList);
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
      // Since this is a category page, determine which data is shown
      if (PortalUtils.getDashboardPortlet(request).isCached()) {
        if (PortalUtils.isPortletInProtectedMode(request)) {
          // Limit the data to a participant
          projectHistoryList.setForParticipant(Constants.TRUE);
        } else {
          // Use the most generic settings since this portlet is cached
          projectHistoryList.setPublicProjects(Constants.TRUE);
        }
      } else {
        if (user.isLoggedIn()) {
          // Use the current user's setting
          projectHistoryList.setForUser(user.getId());
        } else {
          // Use the most generic settings since this portlet is cached
          projectHistoryList.setPublicProjects(Constants.TRUE);
        }
      }
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
          DateUtils.isAtleastNextDay(projectHistory.getRelativeDate().getTime(), lastDate.getTime())) {
        // This is a new date so create a new array for storing into dayList
        dayList = new ArrayList<ArrayList>();
        activityStreamList.add(dayList);

        // New day, new people
        lastId = -1;
      }
      // This is a new day or this is a new event owner
      int thisOwner = projectHistory.getUser().getId();
//      if (projectHistory.getEventType() == ProjectHistoryList.ADD_ACTIVITY_ENTRY_EVENT) {
//        // This event type will have a custom handler
//        thisOwner = 0;
//      }


      if (lastId == -1 || projectHistory.getIndent() == 0) {
        eventList = new ArrayList<ProjectHistory>();
        dayList.add(eventList);
      }
      // Parse the wiki content
      int userId = -1;
      if (user != null) {
        userId = user.getId();
      }
      // @note the context is used instead of the full URL
      WikiToHTMLContext wikiToHTMLContext = new WikiToHTMLContext(userId, request.getContextPath());
      String wikiLinkString = WikiToHTMLUtils.getHTML(wikiToHTMLContext, db, projectHistory.getDescription());
      projectHistory.setHtmlLink(wikiLinkString);
      // Add the activity stream item to the person
      eventList.add(projectHistory);
      // Update the pointers
      lastDate = projectHistory.getRelativeDate();
      lastId = thisOwner;
    }

    // Determine if the portlet should be shown
    if (activityStreamList.size() > 0) {
      // Share the namespace of this portlet so it can be controlled
      for (String event : PortalUtils.getDashboardPortlet(request).getGenerateDataEvents()) {
        if (event.startsWith("namespace-")) {
          PortalUtils.setGeneratedData(request, event, response.getNamespace());
        }
      }
      // Show the activity list
      request.setAttribute(PROJECT_HISTORY_ARRAY_LIST, activityStreamList);
      // Show the more button
      if (projectHistoryList.getPagedListInfo().getHasNextPageLink()) {
        request.setAttribute("hasNext", "1");
      }
      // Show how many more comments there are for the current activity
      ProjectHistory lastHistoryItem = projectHistoryList.get(projectHistoryList.size() - 1);
      int additionalComments = ProjectHistoryUtils.queryAdditionalCommentsCount(db, lastHistoryItem);
      if (additionalComments > 0) {
        request.setAttribute("additionalComments", String.valueOf(additionalComments));
      }
      // Setting status of request type (ajax/non-ajax)
      request.setAttribute("ajax", ajax);
      request.setAttribute("prefLimit", prefLimit);
      request.setAttribute("limit", limit);
      request.setAttribute("offset", "-1");
      return VIEW_PAGE;
    } else {
      return null;
    }
  }

  private ArrayList<String> getEventPreferences(RenderRequest request, Project project, User user) {
    ArrayList<String> objectPreference = null;
    String[] objectPreferences = request.getPreferences().getValues(PREF_EVENTS, null);
    if (objectPreferences != null) {
      objectPreference = new ArrayList<String>();
      for (String preferenceName : objectPreferences) {
        // Make sure the user has access to the data
        String permission = ProjectHistoryList.getPermission(preferenceName);
        if (user == null || project == null || permission == null || ProjectUtils.hasAccess(project.getId(), user, permission)) {
          objectPreference.add(preferenceName);
        }
      }
    }
    request.setAttribute(EVENT_ARRAY_LIST, objectPreference);
    return objectPreference;
  }
}
