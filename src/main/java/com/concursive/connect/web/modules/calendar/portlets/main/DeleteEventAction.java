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
 * Delete action
 *
 * @author matt rajkowski
 * @created December 2, 2008
 */
public class DeleteEventAction implements IPortletAction {

  public GenericBean processAction(ActionRequest request, ActionResponse response) throws Exception {

    // Determine the project container to use
    Project project = findProject(request);
    if (project == null) {
      throw new Exception("Project is null");
    }

    // Check the user's permissions
    User user = getUser(request);
    if (!ProjectUtils.hasAccess(project.getId(), user, "project-calendar-delete")) {
      throw new PortletException("Unauthorized to delete in this project");
    }

    // Get the request parameter (try both since one is set in a drop-down menu)
    int id = -1;
    String idValue = request.getParameter("meeting");
    if (idValue != null) {
      id = Integer.parseInt(idValue);
    } else {
      id = getPageViewAsInt(request);
    }

    // Determine the database connectivity
    Connection db = useConnection(request);

    // Load the record and delete it
    Meeting meeting = new Meeting(db, id, project.getId());

    //Find the users to send meeting cancellation mail
    MeetingInviteesBean meetingInviteesBean = new MeetingInviteesBean(meeting, project, DimDimUtils.ACTION_MEETING_DIMDIM_CANCEL);
    meetingInviteesBean.cancelMeeting(db);

    meeting.delete(db);

    if (meeting.getIsDimdim()) {
      DimDimUtils.processDimdimMeeting(meetingInviteesBean, null);
    }

    // Remove from index
    indexDeleteItem(request, meeting);

    // Trigger the workflow
    processDeleteHook(request, meeting);

    //send mails on meeting cancellation
    if (!meetingInviteesBean.getCancelledUsers().isEmpty()) {
      processInsertHook(request, meetingInviteesBean);
    }
    // This call will close panels and perform redirects
    return (PortalUtils.performRefresh(request, response, "/show/calendar"));
  }
}
