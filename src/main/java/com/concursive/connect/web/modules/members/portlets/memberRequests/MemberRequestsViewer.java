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
package com.concursive.connect.web.modules.members.portlets.memberRequests;

import static com.concursive.connect.web.portal.PortalUtils.findProject;
import static com.concursive.connect.web.portal.PortalUtils.getConnection;
import static com.concursive.connect.web.portal.PortalUtils.getUser;

import java.io.PrintWriter;
import java.sql.Connection;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.aspcfs.utils.StringUtils;

import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.login.utils.UserUtils;
import com.concursive.connect.web.modules.members.dao.TeamMember;
import com.concursive.connect.web.modules.members.dao.TeamMemberList;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.portal.IPortletViewer;
import com.concursive.connect.web.portal.PortalUtils;

/**
 * Project team member list
 *
 * @author Kailash Bhoopalam
 * @created November 20, 2008
 */
public class MemberRequestsViewer implements IPortletViewer {
  // Pages
  private static final String VIEW_PAGE = "/portlets/member_requests/member_requests-view.jsp";

  //Preferences
  private static final String PREF_TITLE = "title";

  // Object Results
  private static final String TITLE = "title";
  private static final String PROJECT = "project";
  private static final String MEMBER_REQUESTS = "memberRequests";

  public String doView(RenderRequest request, RenderResponse response)
	    throws Exception {
    // The JSP to show upon success
    String defaultView = VIEW_PAGE;

    // Determine the project container to use
    Project project = findProject(request);
    request.setAttribute(PROJECT, project);

    User user = getUser(request);

    // Set global preferences
    request.setAttribute(TITLE, request.getPreferences().getValue(PREF_TITLE, null));

    // Determine the current profile

    // Determine if the invites can be shown to the current user
    if (project.getProfile() && user.getId() == project.getOwner()) {
    	String approve = request.getParameter("approve");
    	
      // Determine the database connection to use
      Connection db = getConnection(request);
    	if (StringUtils.hasText(approve)){
        String teamMemberId = request.getParameter("teamMemberId");
        TeamMember teamMember = new TeamMember(db, Integer.parseInt(teamMemberId));
        TeamMember prevMember = new TeamMember(db, Integer.parseInt(teamMemberId));
        
        int requestingUserId = teamMember.getUserId();
        
        if ("true".equals(approve)) {
          if (project.getProfile() && user.getId() == project.getOwner()) {
          	//Reciprocate as it is request to become a friend of a user profile
          	Project requestingUserProfile = (UserUtils.loadUser(requestingUserId)).getProfileProject();
          	TeamMember reciprocatingTeamMember = null;
          	//Determine if the reciprocal already exists
          	TeamMemberList existingReciprocalTeamMemberList = new TeamMemberList();
          	existingReciprocalTeamMemberList.setProjectId(requestingUserProfile.getId());
          	existingReciprocalTeamMemberList.setUserId(user.getId());
          	existingReciprocalTeamMemberList.buildList(db);
          	if (existingReciprocalTeamMemberList.size() == 1){
          		reciprocatingTeamMember = (TeamMember)existingReciprocalTeamMemberList.get(0);
          	}
          	if (reciprocatingTeamMember == null){
	          	reciprocatingTeamMember = new TeamMember();
	          	reciprocatingTeamMember.setProjectId(requestingUserProfile.getId());
	          	reciprocatingTeamMember.setUserId(user.getId());
	          	reciprocatingTeamMember.setUserLevel(UserUtils.getUserLevel(TeamMember.MEMBER));
	            reciprocatingTeamMember.setStatus(TeamMember.STATUS_ADDED);
	            reciprocatingTeamMember.setEnteredBy(requestingUserId);
	            reciprocatingTeamMember.setModifiedBy(requestingUserId);
	            reciprocatingTeamMember.insert(db);
          	} else {
          		if (reciprocatingTeamMember.getStatus() == TeamMember.STATUS_ADDED){
          			// DO Nothing
          		} else {
  	            reciprocatingTeamMember.setStatus(TeamMember.STATUS_ADDED);
  	            if (reciprocatingTeamMember.getUserLevel() > TeamMember.MEMBER ) {
  		          	reciprocatingTeamMember.setUserLevel(UserUtils.getUserLevel(TeamMember.MEMBER));
  	            }
  	            reciprocatingTeamMember.update(db);
          		}
          	}
          }
          //Change status of team member
          teamMember.setStatus(TeamMember.STATUS_JOINED);
          teamMember.setModifiedBy(user.getId());
          teamMember.update(db);
          PortalUtils.processUpdateHook(request, prevMember, teamMember);
        } else {
          if (project.getProfile() && user.getId() == project.getOwner()) {
          	//Remove team member if request to become a friend of a user profile is denied
            teamMember.delete(db);
            PortalUtils.processDeleteHook(request, prevMember);
          } else {
          	//Change user status to refused
            teamMember.setStatus(TeamMember.STATUS_REFUSED);
            teamMember.update(db);
            PortalUtils.processUpdateHook(request, prevMember, teamMember);
          }
        }

    		response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("");
        out.flush();
    		defaultView  = null;
    	} else {
	      
	      TeamMemberList teamMemberList = new TeamMemberList();
	      teamMemberList.setProjectId(project.getId());
	      teamMemberList.setStatus(TeamMember.STATUS_JOINED_NEEDS_APPROVAL);
	      teamMemberList.buildList(db);
	      
	      if (teamMemberList.size() == 0){
	      	defaultView = null; 
	      }
	      
	      request.setAttribute(MEMBER_REQUESTS, teamMemberList);
    	}
      // JSP view
      return defaultView;
    }
    return null;
	}
}
