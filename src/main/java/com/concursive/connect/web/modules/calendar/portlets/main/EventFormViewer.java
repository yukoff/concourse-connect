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
package com.concursive.connect.web.modules.calendar.portlets.main;

import com.concursive.commons.date.DateUtils;
import com.concursive.connect.config.ApplicationPrefs;
import com.concursive.connect.web.modules.calendar.dao.Meeting;
import com.concursive.connect.web.modules.calendar.dao.MeetingAttendee;
import com.concursive.connect.web.modules.calendar.dao.MeetingAttendeeList;
import com.concursive.connect.web.modules.calendar.utils.MeetingInviteesBean;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.login.utils.UserUtils;
import com.concursive.connect.web.modules.members.dao.TeamMember;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import com.concursive.connect.web.portal.AbstractPortletModule;
import com.concursive.connect.web.portal.IPortletViewer;
import com.concursive.connect.web.portal.PortalUtils;
import static com.concursive.connect.web.portal.PortalUtils.*;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Meeting form
 *
 * @author matt rajkowski
 * @created November 24, 2008
 */
public class EventFormViewer implements IPortletViewer {

  // Pages
  private static final String VIEW_PAGE = "/projects_center_calendar_add.jsp";
  private static final String CONFIRM_INVITEES = "/projects_center_calendar_meetings_confirm.jsp";
  private static final String JOIN_EVENT_PAGE = "/projects_center_calendar_join_event.jsp";
  // Object attributes
  private static final String VIEW_TYPE = "viewType";
  private static final String JOIN_EVENT_VIEW = "joinEvent";
  private static final String MEETING_ID = "meetingId";
  // Object Results
  private static final String MEETING = "meeting";
  private static final String TEAM_MEMBERS_LIST = "teamMembersList";
  private static final String SHOW_DIMDIM_OPTION = "showDimDim";

  public String doView(RenderRequest request, RenderResponse response) throws Exception {

    String viewType = request.getParameter(VIEW_TYPE);
    if (JOIN_EVENT_VIEW.equals(viewType)) {
      request.setAttribute(MEETING_ID, request.getParameter("id"));
      return JOIN_EVENT_PAGE;
    }

    //Preferences
    ApplicationPrefs prefs = PortalUtils.getApplicationPrefs(request);
    request.setAttribute(SHOW_DIMDIM_OPTION, "true".equals(prefs.get(ApplicationPrefs.DIMDIM_ENABLED)) ? "true" : "false");

    // Check the request existence of invitees bean then display the confirm invitees page.
    Object meetingInviteesBean = request.getAttribute(AbstractPortletModule.FORM_BEAN);
    if (meetingInviteesBean instanceof MeetingInviteesBean) {
      meetingInviteesBean = (MeetingInviteesBean) PortalUtils.getFormBean(request, "meetingInviteesBean", MeetingInviteesBean.class);
      return CONFIRM_INVITEES;
    }

    // Determine the project container to use
    Project project = findProject(request);

    // Determine the record to show
    int recordId = getPageViewAsInt(request);

    // Check the user's permissions
    User user = getUser(request);
    if (!ProjectUtils.hasAccess(project.getId(), user, "project-calendar-add")) {
      throw new PortletException("Unauthorized to add in this project");
    }

    // Determine the connection to use
    Connection db = useConnection(request);

    // Check the request for the record and provide a value for the request scope
    Meeting meeting = (Meeting) PortalUtils.getFormBean(request, MEETING, Meeting.class);

    // Load the record
    if (recordId > -1) {
      meeting.queryRecord(db, recordId);

      // Fetch the attendee list
      setMeetingInvitees(db, meeting);

      // Verify the project id since the request cannot be trusted
      if (meeting.getProjectId() != project.getId()) {
        throw new PortletException("Project mismatch");
      }
    }

    // Meetings have defaults...
    if (meeting.getStartDate() == null) {
      meeting.setStartDate(DateUtils.roundUpToNextFive());
      if (meeting.getEndDate() == null) {
        meeting.setEndDate(meeting.getStartDate());
      }
    }

    //Build the invitee list for adding members from the project team list.
    List<User> teamMembersList = new ArrayList<User>();
    for (TeamMember teamMember : project.getTeam()) {
      User userInfo = UserUtils.loadUser(teamMember.getUserId());
      // add if the user is not the meeting host(current user)
      if (user.getId() != teamMember.getUserId()) {
        teamMembersList.add(userInfo);
      }
    }
    request.setAttribute(TEAM_MEMBERS_LIST, teamMembersList);

    // JSP view
    return VIEW_PAGE;
  }

  //reloads meeting invitees to the page.
  private void setMeetingInvitees(Connection db, Meeting meeting) throws SQLException {
    //in case of error return from confirm invitees page
    String inviteesConfirm = meeting.getMeetingInvitees();

    //find all meeting attendees
    MeetingAttendeeList meetingAttendeeList = new MeetingAttendeeList();
    meetingAttendeeList.setMeetingId(meeting.getId());
    meetingAttendeeList.buildList(db);

    //comma separate the invitees
    String meetingInvitees = "";
    for (MeetingAttendee thisMeetingAttendee : meetingAttendeeList) {
      if (!"".equals(meetingInvitees)) {
        meetingInvitees += ", ";
      }
      User userInfo = UserUtils.loadUser(thisMeetingAttendee.getUserId());
      meetingInvitees += userInfo.getNameFirstLast() + " (" + userInfo.getProfileProject().getUniqueId() + ")";
    }
    meeting.setMeetingInvitees(meetingInvitees);

    if (!"".equals(inviteesConfirm)) {
      meetingInvitees += ", " + inviteesConfirm;
      meeting.setMeetingInvitees(meetingInvitees);
    }
  }
}
