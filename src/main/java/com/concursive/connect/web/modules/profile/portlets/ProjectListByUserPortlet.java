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
package com.concursive.connect.web.modules.profile.portlets;

import com.concursive.connect.Constants;
import com.concursive.connect.web.modules.communications.utils.EmailUpdatesUtils;
import com.concursive.connect.web.modules.members.dao.TeamMember;
import com.concursive.connect.web.modules.members.dao.TeamMemberList;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.portal.PortalUtils;
import com.concursive.connect.web.utils.PagedListInfo;

import javax.portlet.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * List of projects for a user
 *
 * @author Kailash Bhoopalam
 * @created Sept 02, 2008
 */
public class ProjectListByUserPortlet extends GenericPortlet {

  // Pages
  private static final String VIEW_PAGE = "/portlets/projects_by_user/projects_by_user-view.jsp";

  // Preferences
  private static final String PREF_TITLE = "title";
  private static final String PREF_LIMIT = "limit";
  private static final String PREF_USER_PROFILES = "userProfiles";
  private static final String PREF_IS_THE_USER = "isTheUser";

  // Request Attributes
  private static final String TEAM_MEMBER_LIST = "teamMemberList";
  private static final String TITLE = "title";
  private static final String MODIFY_NOTIFICATION = "modifyNotification";
  private static final String PAGED_LIST_INFO = "pagedListInfo";

  public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
    try {
      // Determine the action or viewer
      String value = request.getParameter("value");
      // Prepare the response
      String view = null;
      try {
        Connection db = PortalUtils.getConnection(request);
        // Handle the request
        if (value != null) {
          setAjaxNotification(request, response, db);
        } else {
          view = projectListViewer(request, db);
        }
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        // Connect here doesn't get closed because it was obtained elsewhere
      }

      // Generate the output
      if (view != null) {
        PortletContext context = getPortletContext();
        PortletRequestDispatcher requestDispatcher =
            context.getRequestDispatcher(view);
        requestDispatcher.include(request, response);
      }
    } catch (Exception e) {
      throw new PortletException(e.getMessage());
    }
  }

  private String projectListViewer(RenderRequest request, Connection db) throws SQLException {
    // Retrieve the preferences
    String maxNumberOfProjectsToShow = request.getPreferences().getValue(PREF_LIMIT, "-1");
    String title = request.getPreferences().getValue(PREF_TITLE, null);
    boolean userProfiles = Boolean.valueOf(request.getPreferences().getValue(PREF_USER_PROFILES, "false"));
    boolean isTheUser = Boolean.valueOf(request.getPreferences().getValue(PREF_IS_THE_USER, "false"));

    // Show all of the user's joined profiles...
    Project project = PortalUtils.findProject(request);
    int userId = project.getOwner();

    // Determine if this portlet can be shown to this user
    if ((isTheUser && (userId != PortalUtils.getUser(request).getId())) ||
        (!isTheUser && (userId == PortalUtils.getUser(request).getId()))) {
      return null;
    }

    // Use paged data for sorting
    PagedListInfo pagedListInfo = new PagedListInfo();
    pagedListInfo.setItemsPerPage(maxNumberOfProjectsToShow);
    request.setAttribute(PAGED_LIST_INFO, pagedListInfo);

    // Fetch JUST the user's team member entry
    TeamMemberList teamMemberList = new TeamMemberList();
    teamMemberList.setUserId(userId);
    teamMemberList.setStatus(TeamMember.STATUS_ADDED);
    teamMemberList.setPagedListInfo(pagedListInfo);
    // Decide if showing non-user profiles or user profiles
    if (userProfiles) {
      teamMemberList.setUserProfiles(Constants.TRUE);
      pagedListInfo.setDefaultSort("p.title", null);
    } else {
      teamMemberList.setUserProfiles(Constants.FALSE);
      pagedListInfo.setDefaultSort("t.email_updates_schedule, p.title", null);
    }
    // For when other users look at this profile...
    if (userId != PortalUtils.getUser(request).getId()) {
      // Show the allowable profiles...
      teamMemberList.setForTeamMateUserId(PortalUtils.getUser(request).getId());
      teamMemberList.setIgnoreOwnerUserId(userId);
      // Sort by title
      pagedListInfo.setDefaultSort("p.title", null);
    }
    teamMemberList.buildList(db);

    // Return the correct view
    if (teamMemberList.size() > 0) {
      // Set request values
      request.setAttribute(TEAM_MEMBER_LIST, teamMemberList);
      request.setAttribute(TITLE, title);
      // Set modification permissions
      if (userId == PortalUtils.getUser(request).getId()) {
        request.setAttribute(MODIFY_NOTIFICATION, "true");
      } else {
        request.setAttribute(MODIFY_NOTIFICATION, "false");
      }
      return VIEW_PAGE;
    } else {
      return null;
    }
  }

  /**
   * Updates the team member's notifications
   *
   * @param request
   * @param db
   * @throws SQLException
   */
  private void setAjaxNotification(RenderRequest request, RenderResponse response, Connection db) throws Exception {
    // Verify the user is changing their own record
    int teamMemberId = Integer.parseInt(request.getParameter("id"));
    String value = request.getParameter("value");
    // Load the corresponding member
    TeamMember teamMember = new TeamMember(db, teamMemberId);
    // Configure the notifications for this user only
    if (teamMember.getUserId() == PortalUtils.getUser(request).getId()) {
      teamMember.setModifiedBy(PortalUtils.getUser(request).getId());
      if ("often".equals(value)) {
        teamMember.setEmailUpdatesSchedule(TeamMember.EMAIL_OFTEN);
      } else if ("daily".equals(value)) {
        teamMember.setEmailUpdatesSchedule(TeamMember.EMAIL_DAILY);
      } else if ("weekly".equals(value)) {
        teamMember.setEmailUpdatesSchedule(TeamMember.EMAIL_WEEKLY);
      } else if ("monthly".equals(value)) {
        teamMember.setEmailUpdatesSchedule(TeamMember.EMAIL_MONTHLY);
      } else if ("never".equals(value)) {
        teamMember.setEmailUpdatesSchedule(TeamMember.EMAIL_NEVER);
      } else if ("notifications-true".equals(value)) {
        teamMember.setNotification("false");
      } else if ("notifications-false".equals(value)) {
        teamMember.setNotification("true");
      } else {
        throw new Exception("Invalid parameter: " + value);
      }
      // Update the appropriate records...
      teamMember.update(db);
      EmailUpdatesUtils.saveQueue(db, teamMember);
      // Render the output
      response.setContentType("text/html");
      PrintWriter out = response.getWriter();
      out.println("," + teamMember.getId() + "," + teamMember.getNotification() + "," + teamMember.getEmailUpdatesSchedule());
      out.flush();
    } else {

    }
  }


}