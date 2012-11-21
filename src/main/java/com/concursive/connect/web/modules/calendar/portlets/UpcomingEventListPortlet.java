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
package com.concursive.connect.web.modules.calendar.portlets;

import com.concursive.connect.Constants;
import com.concursive.connect.web.modules.calendar.dao.Meeting;
import com.concursive.connect.web.modules.calendar.dao.MeetingAttendee;
import com.concursive.connect.web.modules.calendar.dao.MeetingAttendeeList;
import com.concursive.connect.web.modules.calendar.dao.MeetingList;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.dao.ProjectCategory;
import com.concursive.connect.web.modules.profile.dao.ProjectCategoryList;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import com.concursive.connect.web.portal.PortalUtils;
import com.concursive.connect.web.utils.PagedListInfo;
import com.concursive.commons.text.StringUtils;

import javax.portlet.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

/**
 * Project list portlet
 *
 * @author lorraine bittner
 * @created June 18, 2008
 */
public class UpcomingEventListPortlet extends GenericPortlet {

  // Pages
  private static final String VIEW_PAGE = "/portlets/event_upcoming_list/event_upcoming_list-view.jsp";
  private static final String MESSAGE_PAGE = "/portlets/event_upcoming_list/event_upcoming_list-message-view.jsp";
  private static final String CLOSE_PAGE = "/portlets/event_upcoming_list/event_upcoming_list-refresh.jsp";
  private static final String VIEW_TYPE = "viewType";
  private static final String ADD_SUCCESS = "addSuccess";
  private static final String DELETE_SUCCESS = "deleteSuccess";
  // Parameters
  private static final String PROJECT_BY_ID_MAP = "projectByIdMap";
  private static final String EVENT_LIST = "eventList";
  private static final String USER = "user";
  private static final String ATTENDEE_BY_MEETING_ID_MAP = "attendeeByMeetingIdMap";
  private static final String PROJECT_URL = "projectUrl";
  private static final String PROJECT = "project";
  // Preferences
  private static final String LIMIT = "limit";
  private static final String INCLUDE_CATEGORY_LIST = "category";
  private static final String TITLE = "title";
  private static final String PREF_SHOW_LINKS = "showLinks";
  private static final String PREF_SUCCESS_MESSAGE = "successMessage";
  private static final String INCLUDE_WEBCASTS_ONLY = "webcastsOnly";
  private static final String SUCCESS_MESSAGE = "successMessage";

  public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
    // Prefs
    request.setAttribute(TITLE, request.getPreferences().getValue(TITLE, "Upcoming Events"));
    request.setAttribute(PREF_SHOW_LINKS, request.getPreferences().getValue(PREF_SHOW_LINKS, "true"));
    String view = VIEW_PAGE;
    String viewType = request.getParameter(VIEW_TYPE);
    if (viewType == null) {
      viewType = (String) request.getPortletSession().getAttribute(VIEW_TYPE);
    }
    User thisUser = PortalUtils.getUser(request);
    String webcastsOnly = request.getPreferences().getValue(INCLUDE_WEBCASTS_ONLY, "false"); 
    String includeCategoryListString = request.getPreferences().getValue(INCLUDE_CATEGORY_LIST, "");
    String listSizeString = request.getPreferences().getValue(LIMIT, "10");

    int listSize = Integer.parseInt(listSizeString);
    Collection<String> includeCategoryList = Arrays.asList(includeCategoryListString.split(","));
    ProjectCategoryList allCategories = new ProjectCategoryList();
    List<Integer> includeCategoryIdList = new ArrayList<Integer>();

    // Check if all projects should be used, or a specific project
    int projectId = -1;
    Project project = null;

    if ("".equals(includeCategoryListString)) {
      // This portlet can consume data from other portlets so check that first
      for (String event : PortalUtils.getDashboardPortlet(request).getConsumeDataEvents()) {
        project = (Project) PortalUtils.getGeneratedData(request, event);
      }
      // This portlet can use the project in the request scope so check that last
      if (project == null) {
        project = PortalUtils.findProject(request);
      }
      if (project != null) {
        projectId = project.getId();
      }
    }

    try {
      // Now that the meetings have been sorted by date collect them all into a single list
      MeetingList events = new MeetingList();

      if (ADD_SUCCESS.equals(viewType)) {
        view = MESSAGE_PAGE;
        Project p = (Project) request.getPortletSession().getAttribute(PROJECT);

        request.setAttribute(SUCCESS_MESSAGE, request.getPreferences().getValue(PREF_SUCCESS_MESSAGE, null));
        request.setAttribute(PROJECT_URL, request.getContextPath() + "/show/" + p.getUniqueId());
      } else {
        Connection db = PortalUtils.useConnection(request);
        allCategories.buildList(db);

        // For each category get all the public projects that match the category id
        for (String thisCategory : includeCategoryList) {
          ProjectCategory category = allCategories.getFromValue(thisCategory.trim());
          if (category != null) {
            includeCategoryIdList.add(category.getId());
          }
        }
        Map<Integer, Project> projectByIdMap = new HashMap<Integer, Project>();
        // using treemap to sort meetings by date
        Map<Date, MeetingList> meetingByDateMap = new TreeMap<Date, MeetingList>();
        PagedListInfo pagedListInfo = new PagedListInfo();
        pagedListInfo.setItemsPerPage(listSize);
        pagedListInfo.setColumnToSortBy("m.start_date");
        pagedListInfo.setSortOrder("asc");

        // Show the current or upcoming events
        events.setPagedListInfo(pagedListInfo);
        events.setEventSpanStart(new Timestamp(System.currentTimeMillis()));

        // Show webcasts only
        if (StringUtils.isTrue(webcastsOnly)) {
          events.setIsWebcast(true);
        }
        if (projectId > -1) {
          // Show the meetings for a specific project
          events.setProjectId(projectId);
        } else {
          events.setInstanceId(PortalUtils.getInstance(request).getId());
          if (PortalUtils.getDashboardPortlet(request).isCached()) {
            if (PortalUtils.isPortletInProtectedMode(request)) {
              // Use the most generic settings since this portlet is cached
              events.setForParticipant(Constants.TRUE);
            } else {
              // Use the most generic settings since this portlet is cached
              events.setPublicOpenProjectsOnly(true);
            }
          } else {
            if (thisUser.isLoggedIn()) {
              // Use the current user's setting
              events.setForUser(thisUser.getId());
            } else {
              // Use the most generic settings
              events.setPublicOpenProjectsOnly(true);
            }
          }
          // Show meetings that exist for the specified categories
          events.setProjectCategoryIdList(includeCategoryIdList);
        }

        // Build the list
        events.buildList(db);
        for (Meeting m : events) {
          Project p = projectByIdMap.get(m.getProjectId()); // check if project was already added
          if (p == null) {
            p = ProjectUtils.loadProject(m.getProjectId());
          }
          projectByIdMap.put(p.getId(), p);
        }

        // Get all meeting attendee records for this user
        Map<Integer, MeetingAttendee> attendeeByMeetingIdMap = new HashMap<Integer, MeetingAttendee>();
        if (thisUser != null && thisUser.getId() > 0) {
          MeetingAttendeeList attendeeList = new MeetingAttendeeList();
          attendeeList.setUserId(thisUser.getId());
          attendeeList.buildList(db);
          for (MeetingAttendee attendee : attendeeList) {
            attendeeByMeetingIdMap.put(attendee.getMeetingId(), attendee);
          }
        }

        request.setAttribute(EVENT_LIST, events);
        request.setAttribute(PROJECT_BY_ID_MAP, projectByIdMap);
        request.setAttribute(ATTENDEE_BY_MEETING_ID_MAP, attendeeByMeetingIdMap);
        request.setAttribute(USER, thisUser);
      }

      // Clean up session
      request.getPortletSession().removeAttribute(VIEW_TYPE);
      request.getPortletSession().removeAttribute(PROJECT);

      if (events.size() > 0) {
        // JSP view
        PortletContext context = getPortletContext();
        PortletRequestDispatcher requestDispatcher =
            context.getRequestDispatcher(view);
        requestDispatcher.include(request, response);
      }
    } catch (Exception e) {
      e.printStackTrace();
      throw new PortletException(e);
    }
  }

  public void processAction(ActionRequest request, ActionResponse response) throws PortletException {
    MeetingAttendee attendee = null;
    Meeting meeting = null;
    Connection db = PortalUtils.useConnection(request);
    User user = PortalUtils.getUser(request);
    String meetingIdStr = request.getParameter("meetingId");
    int meetingId = meetingIdStr == null ? -1 : Integer.parseInt(meetingIdStr);
    String attendeeIdStr = request.getParameter("attendeeId");
    int attendeeId = attendeeIdStr == null ? -1 : Integer.parseInt(attendeeIdStr);
    try {
      if (attendeeId != -1) {
        attendee = new MeetingAttendee(db, attendeeId);
        // verify the user is modifying their own attendee record
        if (attendee.getUserId() != user.getId()) {
          throw new PortletException("Action is not authorized.");
        }
        meetingId = attendee.getMeetingId();
      }
      if (meetingId != -1) {
        meeting = new Meeting(db, meetingId);
      } else {
        throw new PortletException("Meeting must be specified.");
      }
      meeting = new Meeting(db, meetingId);
      Project project = ProjectUtils.loadProject(meeting.getProjectId());
      request.getPortletSession().setAttribute(PROJECT, project);
      if ("Add".equals(request.getParameter("cmd"))) {
        attendee = addAttendee(user, meeting, db);
        request.getPortletSession().setAttribute(VIEW_TYPE, ADD_SUCCESS);
      } else if ("Delete".equals(request.getParameter("cmd"))) {
        attendee.delete(db);
        request.getPortletSession().setAttribute(VIEW_TYPE, DELETE_SUCCESS);
      }
    } catch (SQLException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

  public MeetingAttendee addAttendee(User user, Meeting meeting, Connection db) throws SQLException {
    int userId = user.getId();
    int meetingId = meeting.getId();

    MeetingAttendee attendee = new MeetingAttendee();

    //Check that the user does not already have an attendee record for the meeting
    MeetingAttendeeList attendeeList = new MeetingAttendeeList();
    attendeeList.setUserId(userId);
    attendeeList.setMeetingId(meetingId);
    attendeeList.buildList(db);
    if (attendeeList.size() > 0) {
      attendee = attendeeList.get(0);
    } else {

      if (userId > 0) {
        attendee.setMeetingId(meetingId);
        attendee.setUserId(userId);
        attendee.setEnteredBy(userId);
        attendee.setModifiedBy(userId);
        attendee.insert(db);
      } else {
        throw new SQLException("Invalid user id specified for adding MeetingAttendee.");
      }
    }
    return attendee;
  }
}
