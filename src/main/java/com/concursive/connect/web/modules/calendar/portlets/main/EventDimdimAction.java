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
import com.concursive.connect.web.modules.calendar.dao.Meeting;
import com.concursive.connect.web.modules.calendar.utils.DimDimUtils;
import com.concursive.connect.web.modules.calendar.utils.MeetingInviteesBean;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.login.utils.UserUtils;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.portal.IPortletAction;
import static com.concursive.connect.web.portal.PortalUtils.*;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.servlet.http.HttpServletRequest;
import java.sql.Connection;
import java.util.HashMap;

/**
 * Action to change meeting status and to start or join meeting
 *
 * @author Nanda Kumar
 * @created May 29, 2009
 */
public class EventDimdimAction implements IPortletAction {

  public GenericBean processAction(ActionRequest request, ActionResponse response) throws Exception {
    //get parameters
    int meetingAction = Integer.parseInt(request.getParameter("aid"));
    int meetingId = Integer.parseInt(request.getParameter("mid"));
    int meetingStatus = Integer.parseInt(request.getParameter("sid"));
    int attendeeUserId = -1;
    if (StringUtils.hasText(request.getParameter("uid"))) {
      attendeeUserId = Integer.parseInt(request.getParameter("uid"));
    }

    //get connection
    Connection db = useConnection(request);

    //get current user
    User currentUser = getUser(request);

    //get project
    Project project = getProject(request);

    //find the meeting
    Meeting meeting = new Meeting();
    meeting.queryRecord(db, meetingId);

    MeetingInviteesBean meetingInviteesBean = new MeetingInviteesBean(meeting, project, meetingAction);

    //check if action is attendee status change 
    if (meetingAction == DimDimUtils.ACTION_MEETING_STATUS_CHANGE) {
      //change status and send mail to meeting host
      if (meetingInviteesBean.setMeetingStatus(db, request, currentUser, meetingStatus)) {
        processInsertHook(request, meetingInviteesBean);
      }

      return (performRefresh(request, response, "/show/calendar"));
    }

    //check if action is approve user join
    if (meetingAction == DimDimUtils.ACTION_MEETING_APPROVE_JOIN) {
      //find attendee
      User attendeeUser = UserUtils.loadUser(attendeeUserId);

      //change status and send mail to meeting host
      if (meetingInviteesBean.setMeetingStatus(db, request, attendeeUser, meetingStatus)) {
        processInsertHook(request, meetingInviteesBean);
      }

      return (performRefresh(request, response, "/show/calendar"));
    }

    //process meeting scenario and get the url to dimdim server
    HashMap<String, String> resultMap = DimDimUtils.processDimdimMeeting(meetingInviteesBean, currentUser);

    //redirect to dimdim server and return
    if (resultMap.containsKey(DimDimUtils.DIMDIM_CODE_SUCCESS)) {
      response.sendRedirect(resultMap.get(DimDimUtils.DIMDIM_CODE_SUCCESS));
      return null;
    }

    //set error message
    ((HttpServletRequest) request).getSession().setAttribute("actionError" + meeting.getId(), resultMap.get(resultMap.keySet().toArray()[0]));
    return null;
  }
}
