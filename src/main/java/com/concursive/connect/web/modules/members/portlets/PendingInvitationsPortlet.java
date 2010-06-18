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
package com.concursive.connect.web.modules.members.portlets;

import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.members.dao.TeamMember;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.dao.ProjectList;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import com.concursive.connect.web.portal.PortalUtils;

import javax.portlet.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;

/**
 * Pending Invitations Portlet
 *
 * @author Kailash Bhoopalam
 * @created October 27, 2008
 */
public class PendingInvitationsPortlet extends GenericPortlet {

  // Pages
  private static final String VIEW_PAGE1 = "/portlets/pending_invitations/pending_invitations-view.jsp";

  // Preferences
  private static final String PREF_TITLE = "title";
  // Attribute names for objects available in the view
  private static final String TITLE = "title";
  private static final String USER = "user";
  private static final String PROJECT = "project";
  private static final String UPDATE_INVITATIONS = "updateInvitations";
  private static final String INVITATED_PROJECT_LIST = "invitedProjectList";

  private static final String VIEW_TYPE = "viewType";

  public void doView(RenderRequest request, RenderResponse response)
      throws PortletException, IOException {
    try {
      String defaultView = null;
      String viewType = request.getParameter(VIEW_TYPE);

      // Set global preferences
      request.setAttribute(TITLE, request.getPreferences().getValue(PREF_TITLE, null));

      // Determine the current profile
      Project project = PortalUtils.getProject(request);
      request.setAttribute(PROJECT, project);

      // Determine the user looking at this profile
      User user = PortalUtils.getUser(request);

      // Determine if the invites can be shown to the current user
      if (project.getProfile() && user.getId() == project.getOwner()) {

        defaultView = VIEW_PAGE1;
        Connection db = PortalUtils.useConnection(request);
        if ("setAccept".equals(viewType)) {
          String projectId = request.getParameter("projectId");
          String accept = request.getParameter("accept");
          Project targetProject = PortalUtils.retrieveAuthorizedProject(Integer.parseInt(projectId), request);
          TeamMember prevMember = new TeamMember(db, targetProject.getId(), project.getOwner());
          if ("true".equals(accept)) {
            // The user accepted becoming a member of a project
            ProjectUtils.accept(db, targetProject.getId(), project.getOwner());
            TeamMember thisMember = new TeamMember(db, targetProject.getId(), project.getOwner());
            // Let the workflow know
            PortalUtils.processUpdateHook(request, prevMember, thisMember);
          } else {
            // The user declined becoming a member of a project
            ProjectUtils.reject(db, targetProject.getId(),  project.getOwner());
            TeamMember thisMember = new TeamMember(db, targetProject.getId(), project.getOwner());
            // Let the workflow know
            PortalUtils.processUpdateHook(request, prevMember, thisMember);
          }
          response.setContentType("text/html");
          PrintWriter out = response.getWriter();
          out.println("");
          out.flush();
          defaultView = null;
        } else {
          //Get a list of projects that owner of the profile has been invited to
          ProjectList invitedProjects = new ProjectList();
          invitedProjects.setProjectsForUser(project.getOwner());
          invitedProjects.setIncludeGuestProjects(false);
          invitedProjects.setInvitationPendingOnly(true);
          invitedProjects.buildList(db);
  	      if (invitedProjects.size() == 0){
  	      	defaultView = null; 
  	      }

          request.setAttribute(INVITATED_PROJECT_LIST, invitedProjects);
          if (user.getId() == project.getOwner()) {
            request.setAttribute(UPDATE_INVITATIONS, "true");
          } else {
            request.setAttribute(UPDATE_INVITATIONS, "false");
          }
        }
      }
      // JSP view
      if (defaultView != null) {
        PortletContext context = getPortletContext();
        PortletRequestDispatcher requestDispatcher =
            context.getRequestDispatcher(defaultView);
        requestDispatcher.include(request, response);
      }
    } catch (Exception e) {
      e.printStackTrace();
      throw new PortletException(e);
    }
  }


  public void processAction(ActionRequest request, ActionResponse response)
      throws PortletException, IOException {
    try {
      // @todo is this needed?
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }
}
