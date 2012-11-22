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

package com.concursive.connect.web.modules.members.actions;

import com.concursive.commons.codec.PrivateString;
import com.concursive.commons.db.DatabaseUtils;
import com.concursive.commons.email.SMTPMessage;
import com.concursive.commons.email.SMTPMessageFactory;
import com.concursive.commons.text.StringUtils;
import com.concursive.commons.web.mvc.actions.ActionContext;
import com.concursive.connect.cache.utils.CacheUtils;
import com.concursive.connect.config.ApplicationPrefs;
import com.concursive.connect.web.controller.actions.GenericAction;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.login.utils.UserUtils;
import com.concursive.connect.web.modules.members.dao.Invitation;
import com.concursive.connect.web.modules.members.dao.InvitationList;
import com.concursive.connect.web.modules.members.dao.TeamMember;
import com.concursive.connect.web.modules.members.dao.TeamMemberList;
import com.concursive.connect.web.modules.members.utils.TeamMemberUtils;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import com.concursive.connect.web.utils.LookupList;
import freemarker.template.Template;

import java.io.StringWriter;
import java.net.URLEncoder;
import java.security.Key;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

/**
 * Actions for the team members module
 *
 * @author matt rajkowski
 * @version $Id: ProjectManagementTeam.java,v 1.1 2003/01/30 04:57:12 matt Exp
 *          $
 * @created November 12, 2001
 */
public final class ProjectManagementTeam extends GenericAction {

  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandChangeRole(ActionContext context) {
    Connection db = null;
    // Process the params
    String projectId = context.getRequest().getParameter("pid");
    String userId = context.getRequest().getParameter("id");
    String newRoleLevel = context.getRequest().getParameter("role");
    try {
      db = this.getConnection(context);
      // Load the project
      Project thisProject = retrieveAuthorizedProject(Integer.parseInt(projectId), context);
      if (!hasProjectAccess(context, thisProject.getId(), "project-team-edit-role")) {
        return "PermissionError";
      }
      if (!hasMatchingFormToken(context)) {
        return "TokenError";
      }
      context.getRequest().setAttribute("project", thisProject);
      //load the team member record
      TeamMember prevMember = new TeamMember(db, Integer.parseInt(projectId), Integer.parseInt(userId));
      // Convert the role level to a role id
      LookupList roleList = CacheUtils.getLookupList("lookup_project_role");
      int newRowId = roleList.getIdFromLevel(new Integer(newRoleLevel));
      boolean changed = TeamMember.changeRole(db, thisProject, getUserId(context), Integer.parseInt(userId), newRowId);
      //Reload the team member record
      TeamMember thisMember = new TeamMember(db, Integer.parseInt(projectId), Integer.parseInt(userId));
      if (!changed) {
        return ("ChangeRoleERROR");
      } else {
        //trigger the workflow
        this.processUpdateHook(context, prevMember, thisMember);
      }
      return "ChangeRoleOK";
    } catch (Exception errorMessage) {
      context.getRequest().setAttribute("Error", errorMessage);
      errorMessage.printStackTrace(System.out);
      return ("SystemError");
    } finally {
      this.freeConnection(context, db);
    }
  }


  public String executeCommandResendInvitation(ActionContext context) {
    Connection db = null;
    try {
      // Parameters
      int projectId = Integer.parseInt(context.getRequest().getParameter("pid"));
      int userId = Integer.parseInt(context.getRequest().getParameter("id"));
      Key key = (Key) context.getServletContext().getAttribute("TEAM.KEY");
      db = getConnection(context);
      // Project permissions
      Project thisProject = retrieveAuthorizedProject(projectId, context);
      if (!hasProjectAccess(context, thisProject.getId(), "project-team-edit")) {
        return "PermissionError";
      }
      if (!hasMatchingFormToken(context)) {
        return "TokenError";
      }
      if (!"true".equals(getPref(context, "INVITE")) &&
          !getUser(context).getAccessInvite()) {
        return "PermissionError";
      }
      context.getRequest().setAttribute("project", thisProject);
      context.getRequest().setAttribute("IncludeSection", "team_invite_status");
      context.getRequest().setAttribute("pid", String.valueOf(projectId));
      ApplicationPrefs prefs = (ApplicationPrefs) context.getServletContext().getAttribute("applicationPrefs");
      // Process the invitation
      InvitationList invitations = new InvitationList();
      User thisUser = UserUtils.loadUser(userId);
      TeamMember thisMember = new TeamMember(db, projectId, userId);
      Invitation thisInvitation = new Invitation(thisUser);
      invitations.add(thisInvitation);
      // Initialize the message template
      Template inviteSubject = null;
      Template inviteBody = null;
      // Set the data model
      Map subjectMappings = new HashMap();
      Map bodyMappings = new HashMap();
      bodyMappings.put("site", new HashMap());
      ((Map) bodyMappings.get("site")).put("title", prefs.get("TITLE"));
      bodyMappings.put("project", thisProject);
      bodyMappings.put("user", getUser(context));
      bodyMappings.put("link", new HashMap());
      bodyMappings.put("invite", new HashMap());
      bodyMappings.put("optional", new HashMap());
      ((Map) bodyMappings.get("invite")).put("firstName", thisUser.getFirstName());
      ((Map) bodyMappings.get("invite")).put("lastName", thisUser.getLastName());
      ((Map) bodyMappings.get("invite")).put("name", thisUser.getNameFirstLast());
      ((Map) bodyMappings.get("optional")).put("message", thisMember.getCustomInvitationMessage() != null ? StringUtils.toHtmlValue(thisMember.getCustomInvitationMessage(), false, true) : "");
      if (thisUser.getRegistered()) {
        // User IS registered with site
        inviteSubject = getFreemarkerConfiguration(context).getTemplate("project_invitation_email_subject-text.ftl");
        inviteBody = getFreemarkerConfiguration(context).getTemplate("project_invitation_email_body-html.ftl");
        ((Map) bodyMappings.get("link")).put("info", getServerUrl(context));
        ((Map) bodyMappings.get("link")).put("invitations", getServerUrl(context) + "/show/" + thisUser.getProfileProject().getUniqueId());
      } else {
        // User IS NOT registered
        inviteSubject = getFreemarkerConfiguration(context).getTemplate("project_invitation_for_new_user_email_subject-text.ftl");
        inviteBody = getFreemarkerConfiguration(context).getTemplate("project_invitation_for_new_user_email_body-html.ftl");
        String data = URLEncoder.encode(PrivateString.encrypt(key, "id=" + thisUser.getId() + ",pid=" + thisProject.getId()), "UTF-8");
        ((Map) bodyMappings.get("link")).put("accept", getServerUrl(context) + "/LoginAccept.do?data=" + data);
        ((Map) bodyMappings.get("link")).put("reject", getServerUrl(context) + "/LoginReject.do?data=" + data);
        ((Map) bodyMappings.get("link")).put("info", getServerUrl(context));
      }
      // Send the message
      SMTPMessage message = SMTPMessageFactory.createSMTPMessageInstance(prefs.getPrefs());
      message.setFrom(prefs.get(ApplicationPrefs.EMAILADDRESS));
      message.addReplyTo(getUser(context).getEmail(), getUser(context).getNameFirstLast());
      message.addTo(thisUser.getEmail());
      // Set the subject from the template
      StringWriter inviteSubjectTextWriter = new StringWriter();
      inviteSubject.process(subjectMappings, inviteSubjectTextWriter);
      message.setSubject(inviteSubjectTextWriter.toString());
      // Set the body from the template
      StringWriter inviteBodyTextWriter = new StringWriter();
      inviteBody.process(bodyMappings, inviteBodyTextWriter);
      message.setBody(inviteBodyTextWriter.toString());
      //Send the invitations
      message.setType("text/html");
      int result = message.send();
      if (result == 0) {
        //Record that message was delivered
        thisMember.setStatus(TeamMember.STATUS_PENDING);
        thisInvitation.setSentMail(true);
      } else {
        //Record that message was not delivered
        thisMember.setStatus(TeamMember.STATUS_MAILERROR);
        thisInvitation.setSentMail(false);
      }
      thisMember.updateStatus(db);
      context.getRequest().setAttribute("invitationList", invitations);
    } catch (Exception errorMessage) {
      LOG.error("resendInvitation", errorMessage);
      context.getRequest().setAttribute("Error", errorMessage);
      return ("SystemError");
    } finally {
      freeConnection(context, db);
    }
    return "ResendInvitationOK";
  }

  /**
   * Displays when a user chooses to join a listing (userCanJoin rule)
   *
   * @param context
   * @return
   */
  public String executeCommandConfirmJoin(ActionContext context) {
    String errorMessage = null;
    String projectIdStr = context.getRequest().getParameter("pid");
    User user = getUser(context);
    Project project = null;
    int projectId = -1;
    try {
      if (projectIdStr == null) {
        errorMessage = "Project id (pid) must be specified.";
      } else if (user == null || user.getId() == -1) {
        errorMessage = "User must be logged in to join.";
      } else {
        projectId = Integer.parseInt(context.getRequest().getParameter("pid"));
        // Project permissions
        project = retrieveAuthorizedProject(projectId, context);
        // Check that the user can join the project (userCanJoin rule)
        boolean canJoin = TeamMemberUtils.userCanJoin(user, project);
        if (!canJoin) {
          return "PermissionError";
        }
      }
      if (errorMessage != null) {
        context.getRequest().setAttribute("actionError", errorMessage);
        return "TeamERROR";
      } else {
        context.getRequest().setAttribute("project", project);
      }
    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return "SystemError";
    }
    return "ConfirmJoinOK";
  }

  /**
   * Displays when a user needs to ask to become a member of a listing
   * (canRequestToJoin rule)
   *
   * @param context
   * @return
   */
  public String executeCommandConfirmAskToBecomeMember(ActionContext context) {
    String errorMessage = null;
    String projectIdStr = context.getRequest().getParameter("pid");
    User user = getUser(context);
    Project project = null;
    int projectId = -1;
    try {
      if (projectIdStr == null) {
        errorMessage = "Project id (pid) must be specified.";
      } else if (user == null || user.getId() == -1) {
        errorMessage = "User must be logged in to request to become a member.";
      } else {
        projectId = Integer.parseInt(context.getRequest().getParameter("pid"));
        // Project permissions
        project = retrieveAuthorizedProject(projectId, context);
        // canRequestToJoin rule
        boolean canRequestToJoin = TeamMemberUtils.userCanRequestToJoin(user, project);
        if (!canRequestToJoin) {
          return "PermissionError";
        }
      }
      if (errorMessage != null) {
        context.getRequest().setAttribute("actionError", errorMessage);
        return "TeamERROR";
      } else {
        context.getRequest().setAttribute("project", project);
      }
    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return "SystemError";
    }
    return "ConfirmAskToBecomeMemberOK";
  }

  public String executeCommandConfirmationPending(ActionContext context) {
    String errorMessage = null;
    String projectIdStr = context.getRequest().getParameter("pid");
    if (projectIdStr == null) {
      projectIdStr = (String) context.getRequest().getAttribute("pid");
    }
    if (projectIdStr == null) {
      projectIdStr = context.getRequest().getParameter("portlet-pid");
    }
    if (projectIdStr == null) {
      projectIdStr = (String) context.getRequest().getAttribute("portlet-pid");
    }
    User user = getUser(context);
    Project project = null;
    int projectId = -1;
    try {
      if (projectIdStr == null) {
        errorMessage = "Project id (pid) must be specified.";
      } else if (user == null || user.getId() == -1) {
        errorMessage = "User must be logged in for member status.";
      } else {
        projectId = Integer.parseInt(projectIdStr);
        // Project permissions
        project = retrieveAuthorizedProject(projectId, context);
        // canRequestToJoin rule
        boolean isPending =
            (user.getId() > 0 &&
                (project.getFeatures().getAllowGuests() || project.getFeatures().getAllowParticipants()) &&
                project.getFeatures().getMembershipRequired());
        if (!isPending) {
          return "PermissionError";
        }
      }
      if (errorMessage != null) {
        context.getRequest().setAttribute("actionError", errorMessage);
        return "TeamERROR";
      } else {
        context.getRequest().setAttribute("project", project);
      }
    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return "SystemError";
    }
    return "ConfirmationPendingOK";
  }

  /**
   * Executes the manager's decision for approving or denying a user's request
   * to become a member.
   *
   * @param context
   * @return
   */
  public String executeCommandApproveOrDenyRequestToBecomeMember(ActionContext context) {
    Connection db = null;
    String projectIdStr = context.getRequest().getParameter("pid");
    String idStr = context.getRequest().getParameter("id");
    boolean approval = "true".equals(context.getRequest().getParameter("approval"));
    User user = getUser(context);
    int projectId = Integer.parseInt(projectIdStr);
    try {
      db = getConnection(context);
      Project targetProject = retrieveAuthorizedProject(projectId, context);
      if (!hasProjectAccess(context, targetProject.getId(), "project-team-edit")) {
        return "PermissionError";
      }
      if (!hasMatchingFormToken(context)) {
        return "TokenError";
      }
      // Check for an existing team member record
      TeamMember prevMember = new TeamMember(db, targetProject.getId(), Integer.parseInt(idStr));

      if (prevMember.getStatus() == TeamMember.STATUS_JOINED_NEEDS_APPROVAL) {
        if (approval) {
          // Change the status of the member being approved
          TeamMember teamMember = new TeamMember(db, targetProject.getId(), Integer.parseInt(idStr));
          teamMember.setStatus(TeamMember.STATUS_ADDED);
          teamMember.setUserLevel(UserUtils.getUserLevel(TeamMember.MEMBER));
          teamMember.setModifiedBy(user.getId());
          teamMember.update(db);
          // Let the workflow know about the change
          processUpdateHook(context, prevMember, teamMember);

          //Reciprocate membership in the requesting users profile if the target project is a user profile
          if (targetProject.getOwner() != -1) {
            User ownerOfTargetProject = UserUtils.loadUser(targetProject.getOwner());
            if (ownerOfTargetProject.getProfileProjectId() == targetProject.getId()) {
              Project requestingUserProfileProject = UserUtils.loadUser(Integer.parseInt(idStr)).getProfileProject();
              TeamMemberList teamMembersOfRequestingUser = requestingUserProfileProject.getTeam();
              TeamMember reciprocatingTeamMember = null;
              //Determine if the reciprocal already exists, then update if necessary
              if (teamMembersOfRequestingUser.hasUserId(targetProject.getOwner())) {
                reciprocatingTeamMember = teamMembersOfRequestingUser.getTeamMember(targetProject.getOwner());
                if (reciprocatingTeamMember.getStatus() == TeamMember.STATUS_ADDED) {
                  // DO Nothing
                } else {
                  reciprocatingTeamMember.setStatus(TeamMember.STATUS_ADDED);
                  reciprocatingTeamMember.setUserLevel(UserUtils.getUserLevel(TeamMember.MEMBER));
                  reciprocatingTeamMember.update(db);
                }
              } else {
                //Reciprocal does not exist, therefore create one
                reciprocatingTeamMember = new TeamMember();
                reciprocatingTeamMember.setUserId(ownerOfTargetProject.getId());
                reciprocatingTeamMember.setProjectId(requestingUserProfileProject.getId());
                reciprocatingTeamMember.setStatus(TeamMember.STATUS_ADDED);
                reciprocatingTeamMember.setUserLevel(UserUtils.getUserLevel(TeamMember.MEMBER));
                reciprocatingTeamMember.setEnteredBy(user.getId());
                reciprocatingTeamMember.setModifiedBy(user.getId());
                reciprocatingTeamMember.insert(db);
              }
            }
          }
        } else {
          // Handle that the membership is being denied by deleting them.
          TeamMember teamMember = new TeamMember(db, targetProject.getId(), Integer.parseInt(idStr));
          teamMember.setStatus(TeamMember.STATUS_REFUSED);
          teamMember.delete(db);
          processUpdateHook(context, prevMember, teamMember);
        }
      } else {
        context.getRequest().setAttribute("actionError", "The member has already been approved or denied.");
      }
      context.getRequest().setAttribute("project", targetProject);
    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return "SystemError";
    }
    return "AskToBecomeMemberOK";
  }

  /**
   * Executes asking to become a member (userCanRequestToJoin rule)
   *
   * @param context
   * @return
   */
  public String executeCommandAskToBecomeMember(ActionContext context) {
    Connection db = null;
    String errorMessage = null;
    String projectIdStr = context.getRequest().getParameter("pid");
    boolean isNotify = DatabaseUtils.parseBoolean(context.getRequest().getParameter("notification"));
    int emailUpdateSchedule = Integer.parseInt(context.getRequest().getParameter("emailNotification"));
    User user = getUser(context);
    Project targetProject = null;
    int projectId = -1;
    try {
      db = getConnection(context);
      if (projectIdStr == null) {
        errorMessage = "Project id (pid) must be specified.";
      } else if (user == null || user.getId() == -1) {
        errorMessage = "User must be logged in to join.";
      } else {
        projectId = Integer.parseInt(context.getRequest().getParameter("pid"));
        // Project permissions
        targetProject = retrieveAuthorizedProject(projectId, context);
        // userCanRequestToJoin rule
        boolean canRequestToJoin = TeamMemberUtils.userCanRequestToJoin(user, targetProject);
        if (!canRequestToJoin) {
          return "PermissionError";
        }
      }
      if (errorMessage != null) {
        context.getRequest().setAttribute("actionError", errorMessage);
        return "TeamERROR";
      } else {
        //Check for an existing team member record if the status is less then joined update to joined and save
        TeamMemberList members = new TeamMemberList();
        members.setProjectId(projectId);
        members.setUserId(user.getId());
        members.buildList(db);
        if (members.size() == 0) {
          TeamMember member = new TeamMember();
          member.setProjectId(projectId);
          member.setUserId(user.getId());
          if (targetProject.getFeatures().getAllowParticipants()) {
            member.setUserLevel(getUserLevel(TeamMember.PARTICIPANT));
          } else {
            member.setUserLevel(getUserLevel(TeamMember.GUEST));
          }
          //if a user profile, set to TeamMember.MEMBER
          User ownerOfTargetProject = UserUtils.loadUser(targetProject.getOwner());
          if (ownerOfTargetProject.getProfileProjectId() == targetProject.getId()) {
            member.setUserLevel(getUserLevel(TeamMember.MEMBER));
          }
          member.setStatus(TeamMember.STATUS_JOINED_NEEDS_APPROVAL);
          member.setEnteredBy(user.getId());
          member.setModifiedBy(user.getId());
          member.setNotification(isNotify);
          member.setEmailUpdatesSchedule(emailUpdateSchedule);
          member.insert(db);
          processInsertHook(context, member);

        } else if (members.size() > 0) {
          context.getRequest().setAttribute("actionError", "You are already a team member");
          return "TeamERROR";
        }
        context.getRequest().setAttribute("project", targetProject);
      }
    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return "SystemError";
    }
    return "AskToBecomeMemberOK";
  }

  /**
   * Executes joining a team (userCanJoin rule)
   *
   * @param context
   * @return
   */
  public String executeCommandJoin(ActionContext context) {
    Connection db = null;
    String errorMessage = null;
    //Parameters
    String projectIdStr = context.getRequest().getParameter("pid");
    boolean isNotify = DatabaseUtils.parseBoolean(context.getRequest().getParameter("notification"));
    int emailUpdateSchedule = Integer.parseInt(context.getRequest().getParameter("emailNotification"));
    User user = getUser(context);
    Project project = null;
    int projectId = -1;
    try {
      db = getConnection(context);
      if (projectIdStr == null) {
        errorMessage = "Project id (pid) must be specified.";
      } else if (user == null || user.getId() == -1) {
        errorMessage = "User must be logged in to join.";
      } else {
        projectId = Integer.parseInt(context.getRequest().getParameter("pid"));
        // Project permissions
        project = retrieveAuthorizedProject(projectId, context);
        // userCanJoin rule
        boolean canJoin = TeamMemberUtils.userCanJoin(user, project);
        if (!canJoin) {
          return "PermissionError";
        }
        context.getRequest().setAttribute("project", project);
      }
      if (errorMessage != null) {
        context.getRequest().setAttribute("actionError", errorMessage);
      } else {
        //Check for an existing team member record if the status is less then joined update to joined and save
        TeamMemberList members = new TeamMemberList();
        members.setProjectId(projectId);
        members.setUserId(user.getId());
        members.buildList(db);
        if (members.size() == 1 && members.get(0).getStatus() != TeamMember.STATUS_ADDED) {
          TeamMember member = members.get(0);
          if (member.getRoleId() > TeamMember.MEMBER) {
            // If membership is not required
            member.setUserLevel(getUserLevel(TeamMember.MEMBER));
            member.setModifiedBy(user.getId());
            member.setStatus(TeamMember.STATUS_ADDED);
            member.setNotification(isNotify);
            member.setEmailUpdatesSchedule(emailUpdateSchedule);
            member.update(db);
            // TODO If membership is required
            // TeamMember.PARTICIPANT
            // TeamMember.STATUS_JOINED_NEEDS_APPROVAL
          }
        } else if (members.isEmpty()) {
          // Otherwise insert new team member with status set to joined
          TeamMember thisMember = new TeamMember();
          thisMember.setProjectId(projectId);
          thisMember.setUserId(user.getId());
          thisMember.setUserLevel(getUserLevel(TeamMember.MEMBER));
          thisMember.setStatus(TeamMember.STATUS_ADDED);
          thisMember.setEnteredBy(user.getId());
          thisMember.setModifiedBy(user.getId());
          thisMember.setNotification(isNotify);
          thisMember.setEmailUpdatesSchedule(emailUpdateSchedule);
          if (thisMember.insert(db)) {
            processInsertHook(context, thisMember);
          }
        }

        //Handle reciprocate membership if the user has accepted to be member of a user profile
        Project targetProject = ProjectUtils.loadProject((projectId));
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
      }
    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return "SystemError";
    } finally {
      freeConnection(context, db);
    }
    return "JoinOK";
  }

  /**
   * Executes leaving a team
   *
   * @param context
   * @return
   */
  public String executeCommandLeave(ActionContext context) {
    Connection db = null;
    String errorMessage = null;
    //Parameters
    String projectIdStr = context.getRequest().getParameter("pid");
    User user = getUser(context);
    Project project = null;
    int projectId = -1;
    try {
      db = getConnection(context);
      if (projectIdStr == null) {
        errorMessage = "Project id (pid) must be specified.";
      } else if (user == null || user.getId() == -1) {
        errorMessage = "User must be logged in to join.";
      } else {
        projectId = Integer.parseInt(context.getRequest().getParameter("pid"));
      }
      if (errorMessage != null) {
        context.getRequest().setAttribute("actionError", errorMessage);
        return "SystemError";
      }
      // Verify the project and use for refresh
      project = retrieveAuthorizedProject(projectId, context);
      context.getRequest().setAttribute("project", project);
      // Check the member's current status
      TeamMember member = new TeamMember(db, project.getId(), user.getId());
      if (member.getStatus() != TeamMember.STATUS_ADDED) {
        return "PermissionError";
      }
      boolean deleted = false;
      deleted = member.delete(db);
      if (deleted) {
        processDeleteHook(context, member);
      }
    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return "SystemError";
    } finally {
      freeConnection(context, db);
    }
    return "LeaveOK";
  }

  /**
   * Changes the user's access to tools
   *
   * @param context
   * @return
   */
  public String executeCommandUpdateTools(ActionContext context) {
    Connection db = null;
    String errorMessage = null;
    // Parameters
    String projectIdStr = context.getRequest().getParameter("pid");
    String targetUserId = context.getRequest().getParameter("id");
    String enable = context.getRequest().getParameter("enable");
    User user = getUser(context);
    Project project = null;
    int projectId = -1;
    try {
      db = getConnection(context);
      if (projectIdStr == null) {
        errorMessage = "Project id (pid) must be specified.";
      } else if (user == null || user.getId() == -1) {
        errorMessage = "User must be logged in to join.";
      } else {
        projectId = Integer.parseInt(context.getRequest().getParameter("pid"));
      }
      if (errorMessage != null) {
        context.getRequest().setAttribute("actionError", errorMessage);
        return "SystemError";
      }
      // Verify the project and use for refresh
      project = retrieveAuthorizedProject(projectId, context);
      if (!hasProjectAccess(context, project.getId(), "project-team-tools")) {
        return "PermissionError";
      }
      if (!hasMatchingFormToken(context)) {
        return "TokenError";
      }
      context.getRequest().setAttribute("project", project);
      // Update the target user's status
      TeamMember prevMember = new TeamMember(db, project.getId(), new Integer(targetUserId));
      TeamMember member = new TeamMember(db, project.getId(), new Integer(targetUserId));
      if (member.getTools() != "true".equals(enable)) {
        member.setTools("true".equals(enable));
        member.updateTools(db);
      }
      //trigger the workflow
      this.processUpdateHook(context, prevMember, member);
    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return "SystemError";
    } finally {
      freeConnection(context, db);
    }
    return "UpdateToolsOK";
  }

  public String executeCommandTooltip(ActionContext context) {
    Connection db = null;
    // Parameters
    String xId = context.getRequest().getParameter("id");
    String teamMemberIdValue = xId.substring(xId.indexOf("_") + 1);
    int teamMemberId = Integer.parseInt(teamMemberIdValue);
    try {
      db = getConnection(context);
      // Load the team member
      TeamMember thisMember = new TeamMember(db, teamMemberId);
      // Verify the current user has access to view
      Project project = retrieveAuthorizedProject(thisMember.getProjectId(), context);
      if (!hasProjectAccess(context, project.getId(), "project-team-view")) {
        return "UserPermissionError";
      }
      context.getRequest().setAttribute("project", project);
      context.getRequest().setAttribute("teamMember", thisMember);
      return "TooltipOK";
    } catch (Exception errorMessage) {
      context.getRequest().setAttribute("Error", errorMessage);
      return ("SystemError");
    } finally {
      this.freeConnection(context, db);
    }
  }

}
