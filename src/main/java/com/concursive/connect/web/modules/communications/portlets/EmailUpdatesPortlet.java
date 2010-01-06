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
package com.concursive.connect.web.modules.communications.portlets;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.portlet.*;
import java.io.IOException;
import java.sql.Connection;

import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.members.utils.TeamMemberUtils;
import com.concursive.connect.web.modules.members.dao.TeamMember;
import com.concursive.connect.web.modules.communications.utils.EmailUpdatesUtils;
import static com.concursive.connect.web.portal.PortalUtils.findProject;
import static com.concursive.connect.web.portal.PortalUtils.getUser;
import com.concursive.connect.web.portal.PortalUtils;
import com.concursive.commons.text.StringUtils;

/**
 * Display the user's notification settings for the current profile and allow them to make changes.
 *
 * @author Ananth
 * @created Jan 4, 2010
 */
public class EmailUpdatesPortlet extends GenericPortlet {
  private static Log LOG = LogFactory.getLog(EmailUpdatesPortlet.class);
  //Pages
  private static final String EMAIL_UPDATES_PREFS = "/portlets/email_updates_prefs/email_updates_prefs-view.jsp";
  // Attribute names for objects available in the view
  private static final String MEMBER = "member";

  public void doView(RenderRequest request, RenderResponse response)
          throws PortletException, IOException {
    // Determine the project container to use
    Project project = findProject(request);

    // Check the user's permissions
    User user = getUser(request);

    boolean isActive = TeamMemberUtils.isActiveMember(user, project);
    if (isActive) {
      if (user.getProfileProjectId() != project.getId()) {
        TeamMember member = TeamMemberUtils.getMember(user, project);
        request.setAttribute(MEMBER, member);
        // Determine the action or viewer
        String notifications = request.getParameter("notifications");
        String schedule = request.getParameter("schedule");
        try {
          Connection db = PortalUtils.getConnection(request);
          // Handle the request
          if (notifications != null || schedule != null) {
            setAjaxNotification(request, db, member);
          }
        } catch (Exception e) {
          e.printStackTrace();
        } finally {
          // Connect here doesn't get closed because it was obtained elsewhere
        }
        PortletContext context = getPortletContext();
        PortletRequestDispatcher requestDispatcher = context.getRequestDispatcher(EMAIL_UPDATES_PREFS);
        requestDispatcher.include(request, response);
      }
    }
  }

  private void setAjaxNotification(RenderRequest request, Connection db, TeamMember member) throws Exception {
    String notifications = request.getParameter("notifications");
    String schedule = request.getParameter("schedule");

    if (StringUtils.hasText(notifications)) {
      member.setNotification(notifications);
    }
    if (StringUtils.hasText(schedule)) {
      member.setEmailUpdatesSchedule(schedule);
    }
    member.update(db);
    EmailUpdatesUtils.saveQueue(db, member);    
  }
}
