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

import com.concursive.commons.text.StringUtils;
import com.concursive.commons.web.mvc.beans.GenericBean;
import com.concursive.connect.config.ApplicationPrefs;
import com.concursive.connect.web.modules.calendar.dao.Meeting;
import com.concursive.connect.web.modules.calendar.utils.DimDimUtils;
import com.concursive.connect.web.modules.calendar.utils.MeetingInviteesBean;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import com.concursive.connect.web.portal.IPortletAction;
import com.concursive.connect.web.portal.PortalUtils;
import static com.concursive.connect.web.portal.PortalUtils.*;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * Displays the confirmation of meeting invitees and Dimdim server credentials
 *
 * @author Nanda Kumar
 * @created June 08, 2009
 */
public class SaveEventInviteesAction implements IPortletAction {

  public GenericBean processAction(ActionRequest request, ActionResponse response) throws Exception {

    // Find current project
    Project project = findProject(request);

    // Determine if Save or Cancel was selected
    String submitAction = request.getParameter("submitAction");
    System.out.println("SubmitAction: " + submitAction);
    if ("Cancel".equals(submitAction)) {
      return (PortalUtils.performRefresh(request, response, "/show/calendar"));
    }

    // Determine the db connection to use
    Connection db = getConnection(request);

    // Reload the meeting information
    int meetingId = Integer.parseInt(request.getParameter("meetingId"));
    Meeting meeting = new Meeting();
    meeting.queryRecord(db, meetingId);

    // Verify permissions
    User user = getUser(request);
    if (meeting.getProjectId() != project.getId()) {
      throw new PortletException("Project and meeting mismatch");
    }
    if (!ProjectUtils.hasAccess(project.getId(), user, "project-calendar-add")) {
      throw new PortletException("Unauthorized to add in this project");
    }

    // Initialize invitee bean class
    MeetingInviteesBean meetingInviteesBean = new MeetingInviteesBean(meeting, project, Integer.parseInt(request.getParameter("meetingAction")));
    meetingInviteesBean.setIsModifiedMeeting("true".equalsIgnoreCase(request.getParameter("isModifiedMeeting")));

    // Populate the email user list
    meetingInviteesBean.populateMailUserList(request.getParameter("membersInvited"),
        request.getParameter("rejectedUsers"), request.getParameter("cancelledUsers"),
        request.getParameter("meetingChangeUsers"));

    //comma seperate the invitee profile list.
    String[] profiles = request.getParameterValues("multipleInvitees");
    if (profiles != null) {
      String meetingInvitees = "";
      for (String profile : profiles) {
        if (!"".equals(meetingInvitees)) {
          meetingInvitees += ", ";
        }
        meetingInvitees += profile;
      }
      meetingInviteesBean.getMeeting().setMeetingInvitees(meetingInvitees);
      meetingInviteesBean.processInvitees(db, request);
    }

    // process new members
    String[] firstName = request.getParameterValues("firstName");
    String[] lastName = request.getParameterValues("lastName");
    String[] emailAddress = request.getParameterValues("emailAddress");
    if (firstName != null && lastName != null && emailAddress != null) {
      meetingInviteesBean.inviteeNewMembers(db, request, firstName, lastName, emailAddress);
    }

    // update dimdim credentials
    String dimdimUrl = request.getParameter("dimdimUrl");
    if (dimdimUrl == null) {
      dimdimUrl = PortalUtils.getApplicationPrefs(request).get(ApplicationPrefs.DIMDIM_API_DOMAIN);
    }
    String dimdimUsername = request.getParameter("dimdimUsername");
    String dimdimPassword = request.getParameter("dimdimPassword");
    if (StringUtils.hasText(dimdimUrl) && StringUtils.hasText(dimdimUsername) && StringUtils.hasText(dimdimPassword)) {
      meeting.setDimdimUrl(dimdimUrl);
      meeting.setDimdimPassword(dimdimPassword);
      meeting.setDimdimUsername(dimdimUsername);
    }

    // for save action check if no invitees were inserted
    if (meetingInviteesBean.getMembersFoundList().isEmpty() && meetingInviteesBean.getAction() == DimDimUtils.ACTION_MEETING_DIMDIM_SCHEDULE) {
      meeting.addError("inviteesError", "No participants were invited");
      setMeetingInvitees(meeting, meetingInviteesBean);
      return null;
    }

    // schedule dimdim meeting
    if (!scheduleDimdimMeeting(db, meetingInviteesBean)) {
      return meeting;
    }

    // send invitation mail
    PortalUtils.processInsertHook(request, meetingInviteesBean);

    return (PortalUtils.performRefresh(request, response, "/show/calendar"));
  }

  //saves or updates meeting values on Dimdim
  private boolean scheduleDimdimMeeting(Connection db, MeetingInviteesBean meetingInviteesBean)
      throws SQLException {
    Meeting meeting = meetingInviteesBean.getMeeting();

    if (!StringUtils.hasText(meeting.getDimdimUrl()) || !StringUtils.hasText(meeting.getDimdimUsername()) ||
        !StringUtils.hasText(meeting.getDimdimPassword())) {
      meeting.addError("inviteesError", "Missing Dimdim credentials");
      return false;
    }

    HashMap<String, String> resultMap = DimDimUtils.processDimdimMeeting(meetingInviteesBean, null);
    if (resultMap.containsKey(DimDimUtils.DIMDIM_CODE_SUCCESS)) {
      String dimdimMeetingId = resultMap.get(DimDimUtils.DIMDIM_CODE_SUCCESS);
      meeting.setDimdimMeetingId(dimdimMeetingId);
      meeting.update(db);
      return true;
    }
    meeting.addError("inviteesError", resultMap.get(resultMap.keySet().toArray()[0]));
    return false;
  }

  private void setMeetingInvitees(Meeting meeting, MeetingInviteesBean meetingInviteesBean) {
    String meetingInvitees = "";

    for (String invitee : meetingInviteesBean.getMembersNotFoundList().keySet()) {
      if (!"".equals(meetingInvitees)) {
        meetingInvitees += ", ";
      }
      meetingInvitees += invitee;
    }

    for (String invitee : meetingInviteesBean.getMembersMultipleList().keySet()) {
      if (!"".equals(meetingInvitees)) {
        meetingInvitees += ", ";
      }
      meetingInvitees += invitee;
    }
    meeting.setMeetingInvitees(meetingInvitees);
  }
}