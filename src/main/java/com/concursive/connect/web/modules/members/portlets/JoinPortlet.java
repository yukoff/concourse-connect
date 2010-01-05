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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.portlet.*;
import java.io.IOException;
import java.sql.Connection;

import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.login.utils.UserUtils;
import com.concursive.connect.web.modules.members.utils.TeamMemberUtils;
import com.concursive.connect.web.modules.members.dao.TeamMemberList;
import com.concursive.connect.web.modules.members.dao.TeamMember;
import static com.concursive.connect.web.portal.PortalUtils.findProject;
import static com.concursive.connect.web.portal.PortalUtils.getUser;
import com.concursive.connect.web.portal.PortalUtils;

/**
 * Join a profile or request to become a member portlet
 *
 * @author Ananth
 * @created Jan 4, 2010
 */
public class JoinPortlet extends GenericPortlet {
  private static Log LOG = LogFactory.getLog(JoinPortlet.class);
  //Pages
  private static final String MEMBER_JOIN_PROFILE_FORM = "/portlets/member_profile_join/member_profile_join-view.jsp";
  // Attribute names for objects available in the view
  private static final String CAN_JOIN = "canJoin";
  private static final String CAN_REQUEST_TO_JOIN = "canRequestToJoin";
  private static final String IS_USER_PROFILE = "isUserProfile";

  public void doView(RenderRequest request, RenderResponse response)
          throws PortletException, IOException {
    // Determine the project container to use
    Project project = findProject(request);

    // Check the user's permissions
    User user = getUser(request);
    // Check that the user can join the project (userCanJoin rule)
    boolean canJoin = TeamMemberUtils.userCanJoin(user, project);
    // userCanRequestToJoin rule
    boolean canRequestToJoin = TeamMemberUtils.userCanRequestToJoin(user, project);

    if (canJoin || canRequestToJoin) {
      if (user.getProfileProjectId() != project.getId()) {
        if (ProjectUtils.isUserProfile(project)) {
          request.setAttribute(IS_USER_PROFILE, "true");
        }
        request.setAttribute(CAN_JOIN, canJoin ? "true" : "false");
        request.setAttribute(CAN_REQUEST_TO_JOIN, canRequestToJoin ? "true" : "false");

        PortletContext context = getPortletContext();
        PortletRequestDispatcher requestDispatcher = context.getRequestDispatcher(MEMBER_JOIN_PROFILE_FORM);
        requestDispatcher.include(request, response);
      }
    }
  }

  public void processAction(ActionRequest request, ActionResponse response)
          throws PortletException, IOException {
    Project project = PortalUtils.getProject(request);
    User user = PortalUtils.getUser(request);

    boolean canJoin = TeamMemberUtils.userCanJoin(user, project);
    boolean canRequestToJoin = TeamMemberUtils.userCanRequestToJoin(user, project);

    if (user.getProfileProjectId() != project.getId()) {
      try {
        Connection db = PortalUtils.getConnection(request);
        if (canJoin) {
          //Check for an existing team member record if the status is less then joined update to joined and save
          TeamMemberList members = new TeamMemberList();
          members.setProjectId(project.getId());
          members.setUserId(user.getId());
          members.buildList(db);
          if (members.size() == 1 && members.get(0).getStatus() != TeamMember.STATUS_ADDED) {
            TeamMember member = members.get(0);
            if (member.getRoleId() > TeamMember.MEMBER) {
              // If membership is not required
              member.setUserLevel(UserUtils.getUserLevel(TeamMember.MEMBER));
              member.setModifiedBy(user.getId());
              member.setStatus(TeamMember.STATUS_ADDED);
              member.update(db);
              // TODO If membership is required
              // TeamMember.PARTICIPANT
              // TeamMember.STATUS_JOINED_NEEDS_APPROVAL
            }
          } else if (members.isEmpty()) {
            // Otherwise insert new team member with status set to joined
            TeamMember thisMember = new TeamMember();
            thisMember.setProjectId(project.getId());
            thisMember.setUserId(user.getId());
            thisMember.setUserLevel(UserUtils.getUserLevel(TeamMember.MEMBER));
            thisMember.setStatus(TeamMember.STATUS_ADDED);
            thisMember.setEnteredBy(user.getId());
            thisMember.setModifiedBy(user.getId());
            if (thisMember.insert(db)) {
              PortalUtils.processInsertHook(request, thisMember);
            }
          }

          //Handle reciprocate membership if the user has accepted to be member of a user profile
          Project targetProject = ProjectUtils.loadProject(project.getId());
          if (targetProject.getOwner() != -1) {
            User ownerOfTargetProject = UserUtils.loadUser(targetProject.getOwner());
            if (ownerOfTargetProject.getProfileProjectId() == targetProject.getId()) {
              Project thisUserProfileProject = user.getProfileProject();
              TeamMemberList teamMemberList = thisUserProfileProject.getTeam();
              TeamMember reciprocatingTeamMember = null;
              //Determine if the reciprocal already exists, then update if necessary
              if (teamMemberList.hasUserId(targetProject.getOwner())) {
                reciprocatingTeamMember = thisUserProfileProject.getTeam().getTeamMember(user.getId());
                if (reciprocatingTeamMember.getStatus() == TeamMember.STATUS_ADDED) {
                  // DO Nothing
                } else {
                  reciprocatingTeamMember.setStatus(TeamMember.STATUS_ADDED);
                  if (reciprocatingTeamMember.getUserLevel() > TeamMember.MEMBER) {
                    reciprocatingTeamMember.setUserLevel(UserUtils.getUserLevel(TeamMember.MEMBER));
                  }
                  reciprocatingTeamMember.update(db);
                }
              } else {
                //Reciprocal does not exist, therefore create one
                reciprocatingTeamMember = new TeamMember();
                reciprocatingTeamMember.setProjectId(thisUserProfileProject.getId());
                reciprocatingTeamMember.setUserId(targetProject.getOwner());
                reciprocatingTeamMember.setUserLevel(UserUtils.getUserLevel(TeamMember.MEMBER));
                reciprocatingTeamMember.setEnteredBy(user.getId());
                reciprocatingTeamMember.setModifiedBy(user.getId());
                reciprocatingTeamMember.insert(db);
              }
            }
          }
        } else if (canRequestToJoin) {
          //Check for an existing team member record if the status is less then joined update to joined and save
          TeamMemberList members = new TeamMemberList();
          members.setProjectId(project.getId());
          members.setUserId(user.getId());
          members.buildList(db);
          if (members.size() == 0) {
            TeamMember member = new TeamMember();
            member.setProjectId(project.getId());
            member.setUserId(user.getId());
            if (project.getFeatures().getAllowParticipants()) {
              member.setUserLevel(UserUtils.getUserLevel(TeamMember.PARTICIPANT));
            } else {
              member.setUserLevel(UserUtils.getUserLevel(TeamMember.GUEST));
            }
            //if a user profile, set to TeamMember.MEMBER
            User ownerOfTargetProject = UserUtils.loadUser(project.getOwner());
            if (ownerOfTargetProject.getProfileProjectId() == project.getId()) {
              member.setUserLevel(UserUtils.getUserLevel(TeamMember.MEMBER));
            }
            member.setStatus(TeamMember.STATUS_JOINED_NEEDS_APPROVAL);
            member.setEnteredBy(user.getId());
            member.setModifiedBy(user.getId());
            member.insert(db);
            PortalUtils.processInsertHook(request, member);
          }
        }
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }
}
