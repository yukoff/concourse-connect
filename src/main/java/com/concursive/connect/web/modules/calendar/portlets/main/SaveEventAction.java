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

import com.concursive.commons.web.mvc.beans.GenericBean;
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

/**
 * Action for saving a calendar event
 *
 * @author matt rajkowski
 * @created December 2, 2008
 */
public class SaveEventAction implements IPortletAction {

  public GenericBean processAction(ActionRequest request, ActionResponse response) throws Exception {
    // Determine the project container to use
    Project project = findProject(request);
    if (project == null) {
      throw new Exception("Project is null");
    }

    // Check the user's permissions
    User user = getUser(request);
    if (!ProjectUtils.hasAccess(project.getId(), user, "project-calendar-add")) {
      throw new PortletException("Unauthorized to add in this project");
    }

    // Populate any info from the request
    Meeting meeting = (Meeting) getFormBean(request, Meeting.class);

    MeetingInviteesBean meetingInviteesBean = null;

    // Set default values when saving records
    meeting.setProjectId(project.getId());
    meeting.setModifiedBy(user.getId());

    // Determine the database connection to use
    Connection db = getConnection(request);

    //validate the checkbox and participants textarea contents
    if (meeting.getIsDimdim() && (meeting.getMeetingInvitees() == null || meeting.getMeetingInvitees().equals(""))) {
      meeting.addError("inviteesError", "Required field if Dimdim web meeting option is choosen.");
      return meeting;
    }
    if (!meeting.getIsDimdim() && meeting.getMeetingInvitees() != null && !meeting.getMeetingInvitees().equals("")) {
      meeting.addError("inviteesError", "Participants can be added only if Dimdim option is choosen.");
      return meeting;
    }

    // Save the record
    boolean recordInserted = false;
    int resultCount = -1;
    if (meeting.getId() == -1) {
      // This appears to be a new record
      meeting.setEnteredBy(user.getId());
      // process the invitees list and save meeting
      meetingInviteesBean = new MeetingInviteesBean(meeting, project, DimDimUtils.ACTION_MEETING_DIMDIM_SCHEDULE);
      recordInserted = meetingInviteesBean.processInvitees(db, request);

      // Trigger the workflow
      if (recordInserted) {
        PortalUtils.processInsertHook(request, meeting);
      }
    } else {
      // Load the previous record for use in the confirmation page
      Meeting previousMeeting = new Meeting(db, meeting.getId());
      meeting.setEnteredBy(previousMeeting.getEnteredBy());
      meeting.setDimdimMeetingId(previousMeeting.getDimdimMeetingId());
      meeting.setDimdimUrl(previousMeeting.getDimdimUrl());
      meeting.setDimdimUsername(previousMeeting.getDimdimUsername());
      meeting.setDimdimPassword(previousMeeting.getDimdimPassword());
      // Verify the record matches the specified project
      if (previousMeeting.getProjectId() != project.getId()) {
        throw new PortletException("Mismatched projectId found");
      }

      //check if new invitees were added or existing invitees removed and update meeting
      meetingInviteesBean = new MeetingInviteesBean(meeting, project, DimDimUtils.ACTION_MEETING_DIMDIM_EDIT);
      resultCount = meetingInviteesBean.compareInvitees(db, request, previousMeeting);

      // Trigger the workflow
      if (resultCount == 1) {
        PortalUtils.processUpdateHook(request, previousMeeting, meeting);
      }
    }

    // Check if an error occurred
    if (!recordInserted && resultCount <= 0) {
      return meeting;
    }

    // Index the record
    PortalUtils.indexAddItem(request, meeting);

    //if meeting is dimdim then show confirmation page
    if (meeting.getIsDimdim()) {
      return meetingInviteesBean;
    }

    //if dimdim option have been removed from the current meeting, then send web meeting cancellation mail
    if (meetingInviteesBean.getPreviousMeetingIsDimidim()) {
      PortalUtils.processInsertHook(request, meetingInviteesBean);
    }

    // This call will close panels and perform redirects
    return (PortalUtils.performRefresh(request, response, "/show/calendar"));
  }
}
