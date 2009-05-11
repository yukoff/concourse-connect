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

import com.concursive.commons.text.StringUtils;
import com.concursive.connect.cache.utils.CacheUtils;
import com.concursive.connect.config.ApplicationPrefs;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.login.dao.UserList;
import com.concursive.connect.web.modules.members.dao.TeamMember;
import com.concursive.connect.web.modules.members.dao.TeamMemberList;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import com.concursive.connect.web.portal.PortalUtils;
import com.concursive.connect.web.utils.LookupElement;
import com.concursive.connect.web.utils.LookupList;

import javax.portlet.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 * Invite members Portlet
 *
 * @author Kailash Bhoopalam
 * @created Octpber 23, 2008
 */
public class InviteMembersPortlet extends GenericPortlet {

  // Pages
  private static final String ENTER_MEMBERS_FORM = "/portlets/invite_members/enter_members_form-view.jsp";
  private static final String SELECT_MEMBERS_FORM = "/portlets/invite_members/select_members_form-view.jsp";
  private static final String ENTER_MESSAGE_FORM = "/portlets/invite_members/enter_invitation_message_form-view.jsp";
  private static final String INVITATION_SENT_PAGE = "/portlets/invite_members/invitation_sent-view.jsp";
  private static final String CLOSE_PAGE = "/portlets/invite_members/enter_members_form-refresh.jsp";

  // Preferences
  private static final String PREF_TITLE = "title";
  private static final String PREF_MESSAGE_SUBJECT = "messageSubject";
  private static final String PREF_MESSAGE_BODY = "messageBody";

  // Attribute names for objects available in the view
  private static final String TITLE = "title";
  private static final String ERROR_MESSAGE = "errorMessage";
  private static final String USER = "user";
  private static final String HAS_PROJECT_ACCESS = "hasProjectAccess";
  private static final String SHOW_ACCESS_TO_TOOLS = "showAccessToTools";
  private static final String ROLE_LIST = "roleList";
  private static final String DEFAULT_ROLE = "defaultRole";
  private static final String TEAM_MEMBER_LIST = "teamMemberList";
  private static final String MEMBERS_TO_INVITE = "membersToInvite";
  private static final String MEMBER_IDS_TO_INVITE = "memberIdsToInvite";
  private static final String MEMBERS = "members";
  private static final String INVITATION_SUBJECT = "invitationSubject";
  private static final String INVITATION_MESSAGE = "invitationMessage";
  private static final String OPTIONAL_MESSAGE = "optionalMessage";

  private static final String VIEW_TYPE = "viewType";
  private static final String SAVE_FAILURE = "saveFailure";
  private static final String CLOSE = "close";

  private static final String GET_MATCHES = "getMatches";
  private static final String GET_MEMBERS = "getMembers";
  private static final String GET_INVITATION_MESSAGE = "getInvitationMessage";
  private static final String SEND_INVITATION = "sendInvitation";

  private static final String NO_MATCH_FOUND = "noMatchFound";
  private static final String HAS_MULTIPLE_MATCHES = "hasMultipleMatches";
  private static final String ACTION_ERROR = "actionError";

  public void doView(RenderRequest request, RenderResponse response)
      throws PortletException, IOException {
    try {
      String defaultView = ENTER_MEMBERS_FORM;
      String viewType = request.getParameter(VIEW_TYPE);
      // Set global preferences
      request.setAttribute(TITLE, request.getPreferences().getValue(PREF_TITLE, null));
      Project project = PortalUtils.findProject(request);

      User user = PortalUtils.getUser(request);
      request.setAttribute(USER, user);

      //Test if the user has access to the project 
      if (!ProjectUtils.hasAccess(project.getId(), user, "project-profile-admin")) {
        request.setAttribute(HAS_PROJECT_ACCESS, "false");
      } else if (CLOSE.equals(viewType)) {

        // Cleanup other session attributes
        request.getPortletSession().removeAttribute(MEMBERS_TO_INVITE + "Error");
        request.getPortletSession().removeAttribute(MEMBERS_TO_INVITE);
        request.getPortletSession().removeAttribute(MEMBER_IDS_TO_INVITE);

        defaultView = CLOSE_PAGE;
      } else if (SAVE_FAILURE.equals(viewType)) {
      } else if (GET_MATCHES.equals(viewType)) {
        request.setAttribute(MEMBERS, request.getPortletSession().getAttribute(MEMBERS));
        //build request attributes to show what the user has entered.
        request.setAttribute(HAS_MULTIPLE_MATCHES, (String) request.getPortletSession().getAttribute(HAS_MULTIPLE_MATCHES));

        if (StringUtils.hasText((String) request.getPortletSession().getAttribute(ACTION_ERROR))) {
          request.setAttribute(ACTION_ERROR, request.getPortletSession().getAttribute(ACTION_ERROR));
          LinkedHashMap<String, String> matchUserId = new LinkedHashMap<String, String>();
          LinkedHashMap<String, String> matchRole = new LinkedHashMap<String, String>();

          String[] matches = (String[]) request.getPortletSession().getAttribute("matches");
          String[] accessToTools = (String[]) request.getPortletSession().getAttribute("accessToTools");
          request.setAttribute("matches", matches);
          request.setAttribute("accessToTools", accessToTools);
          for (String chosenId : matches) {
            matchUserId.put(chosenId, (String) request.getPortletSession().getAttribute("matchUserId-" + chosenId));
            matchRole.put(chosenId, (String) request.getPortletSession().getAttribute("matchedRole-" + chosenId));
          }
          request.setAttribute("matchUserId", matchUserId);
          request.setAttribute("matchRole", matchRole);

          String[] mismatches = (String[]) request.getPortletSession().getAttribute("mismatches");
          String[] notMatchedAccessToTools = (String[]) request.getPortletSession().getAttribute("notMatchedAccessToTools");

          LinkedHashMap<String, String> noMatchFirstName = new LinkedHashMap<String, String>();
          LinkedHashMap<String, String> noMatchLastName = new LinkedHashMap<String, String>();
          LinkedHashMap<String, String> noMatchEmail = new LinkedHashMap<String, String>();
          LinkedHashMap<String, String> noMatchRole = new LinkedHashMap<String, String>();
          request.setAttribute("mismatches", mismatches);
          request.setAttribute("notMatchedAccessToTools", notMatchedAccessToTools);
          for (String unmatchedEntry : mismatches) {
            if (request.getPortletSession().getAttribute("firstName-" + unmatchedEntry) != null) {
              noMatchFirstName.put(unmatchedEntry, (String) request.getPortletSession().getAttribute("firstName-" + unmatchedEntry));
            }
            if (request.getPortletSession().getAttribute("lastName-" + unmatchedEntry) != null) {
              noMatchLastName.put(unmatchedEntry, (String) request.getPortletSession().getAttribute("lastName-" + unmatchedEntry));
            }
            if (request.getPortletSession().getAttribute("email-" + unmatchedEntry) != null) {
              noMatchEmail.put(unmatchedEntry, (String) request.getPortletSession().getAttribute("email-" + unmatchedEntry));
            }
            if (request.getPortletSession().getAttribute("notMatchedRole-" + unmatchedEntry) != null) {
              noMatchRole.put(unmatchedEntry, (String) request.getPortletSession().getAttribute("notMatchedRole-" + unmatchedEntry));
            }
          }
          request.setAttribute("noMatchFirstName", noMatchFirstName);
          request.setAttribute("noMatchLastName", noMatchLastName);
          request.setAttribute("noMatchEmail", noMatchEmail);
          request.setAttribute("noMatchRole", noMatchRole);
        }

        //Determine if the access to tools option needs to be shown
        if (ProjectUtils.hasAccess(project.getId(), user, "project-team-tools") && (StringUtils.hasText(project.getConcursiveCRMUrl()))) {
          request.setAttribute(SHOW_ACCESS_TO_TOOLS, "true");
        }
        //Determine the allowed roles
        TeamMember member = project.getTeam().getTeamMember(user.getId());
        int userLevel = -1;
        if (member != null) {
          userLevel = member.getUserLevel();
        }
        LookupList cachedRoleList = CacheUtils.getLookupList("lookup_project_role");
        LookupList roleList = new LookupList();
        int userAccessLevel = cachedRoleList.getLevelFromId(userLevel);
        for (LookupElement role : cachedRoleList) {
          if (project.getFeatures().getAllowParticipants() && role.getLevel() == TeamMember.GUEST) {
            // do not add the guest role as participant is the lowest allowed role for such projects
          } else {
            if (user.getAccessAdmin()) {
              roleList.add(role);
            } else {
              if (role.getLevel() >= userAccessLevel) {
                roleList.add(role);
              }
            }
          }
        }
        request.setAttribute(ROLE_LIST, roleList);

        if (project.getFeatures().getAllowParticipants()) {
          request.setAttribute(DEFAULT_ROLE, new Integer(TeamMember.PARTICIPANT));
        } else {
          request.setAttribute(DEFAULT_ROLE, new Integer(TeamMember.GUEST));
        }
        // Cleanup other session attributes
        request.getPortletSession().removeAttribute(HAS_MULTIPLE_MATCHES);
        request.getPortletSession().removeAttribute(ACTION_ERROR);

        defaultView = SELECT_MEMBERS_FORM;
      } else if (GET_INVITATION_MESSAGE.equals(viewType)) {

        defaultView = ENTER_MESSAGE_FORM;
      } else if (SEND_INVITATION.equals(viewType)) {

        // Cleanup other session attributes
        request.getPortletSession().removeAttribute(MEMBERS);
        request.getPortletSession().removeAttribute(MEMBERS_TO_INVITE + "Error");
        request.getPortletSession().removeAttribute(MEMBERS_TO_INVITE);
        request.getPortletSession().removeAttribute(MEMBER_IDS_TO_INVITE);

        defaultView = INVITATION_SENT_PAGE;
      } else if (GET_MEMBERS.equals(viewType) || !StringUtils.hasText(viewType)) {
        Project userProfileProject = user.getProfileProject();
        TeamMemberList fullTeamMemberList = userProfileProject.getTeam();
        TeamMemberList teamMemberList = new TeamMemberList();
        //Remove self
        for (TeamMember teamMember : fullTeamMemberList) {
          if (teamMember.getUserId() != PortalUtils.getUser(request).getId()) {
            teamMemberList.add(teamMember);
          }
        }
        request.setAttribute(TEAM_MEMBER_LIST, teamMemberList);

        // If the user is currently navigating this portlet, then keep populating
        // the user's values, else reset the form
        if (StringUtils.hasText(viewType)) {
          //Set request attributes if any
          request.setAttribute(MEMBER_IDS_TO_INVITE, request.getPortletSession().getAttribute(MEMBER_IDS_TO_INVITE));
          request.setAttribute(MEMBERS_TO_INVITE, request.getPortletSession().getAttribute(MEMBERS_TO_INVITE));
          request.setAttribute(MEMBERS_TO_INVITE + "Error", request.getPortletSession().getAttribute(MEMBERS_TO_INVITE + "Error"));
        }

        // Cleanup other session attributes
        request.getPortletSession().removeAttribute(MEMBERS_TO_INVITE + "Error");
      }
      PortletContext context = getPortletContext();
      PortletRequestDispatcher requestDispatcher =
          context.getRequestDispatcher(defaultView);
      requestDispatcher.include(request, response);
    } catch (Exception e) {
      e.printStackTrace();
      throw new PortletException(e);
    }
  }


  public void processAction(ActionRequest request, ActionResponse response)
      throws PortletException, IOException {
    try {
      if (!ProjectUtils.hasAccess(PortalUtils.getProject(request).getId(), PortalUtils.getUser(request), "project-profile-admin")) {
        request.setAttribute(HAS_PROJECT_ACCESS, "false");
      } else {
        String actionType = request.getParameter("actionType");
        if (GET_MEMBERS.equals(actionType)) {
          response.setRenderParameter(VIEW_TYPE, actionType);
        } else if (GET_MATCHES.equals(actionType)) {
          String membersToInvite = request.getParameter(MEMBERS_TO_INVITE);
          String memberIdsToInvite = request.getParameter(MEMBER_IDS_TO_INVITE);

          if (StringUtils.hasText(membersToInvite)) {
            buildMatches(request);
            request.getPortletSession().setAttribute(MEMBERS_TO_INVITE, membersToInvite);
            request.getPortletSession().setAttribute(MEMBER_IDS_TO_INVITE, memberIdsToInvite);
            response.setRenderParameter(VIEW_TYPE, actionType);
          } else {
            request.getPortletSession().setAttribute(MEMBERS_TO_INVITE + "Error", "Required");
            response.setRenderParameter(VIEW_TYPE, GET_MEMBERS);
          }
        } else if (GET_INVITATION_MESSAGE.equals(actionType)) {
          boolean hasError = buildInvitationList(request);
          if (hasError) {
            response.setRenderParameter(VIEW_TYPE, GET_MATCHES);
          } else {
            response.setRenderParameter(VIEW_TYPE, actionType);
          }
        } else if (SEND_INVITATION.equals(actionType)) {
          sendInvitation(request);
          response.setRenderParameter(VIEW_TYPE, actionType);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }


  /**
   * @param request
   */
  private void sendInvitation(ActionRequest request) throws Exception {
    Connection db = PortalUtils.getConnection(request);
    //Get message body and subject
    String optionalMessage = request.getParameter(OPTIONAL_MESSAGE);

    //Get invitation list
    String[] matches = (String[]) request.getPortletSession().getAttribute("matches");
    String[] accessToTools = (String[]) request.getPortletSession().getAttribute("accessToTools");

    if (matches != null) {
      for (String chosenId : matches) {
        String matchedUserId = (String) request.getPortletSession().getAttribute("matchUserId-" + chosenId);
        String matchedRole = (String) request.getPortletSession().getAttribute("matchedRole-" + chosenId);
        request.getPortletSession().removeAttribute("matchUserId-" + chosenId);
        request.getPortletSession().removeAttribute("matchedRole-" + chosenId);
        //Determine if this user needs to be provided access to tools
        boolean provideAccessToTools = false;
        if (accessToTools != null) {
          for (String toolsEntry : accessToTools) {
            if (toolsEntry.equals(chosenId)) {
              provideAccessToTools = true;
              break;
            }
          }
        }
        //Add as team members
        TeamMember thisMember = new TeamMember();
        thisMember.setProjectId(PortalUtils.getProject(request).getId());
        thisMember.setUserId(matchedUserId);
        thisMember.setTools(provideAccessToTools);
        thisMember.setUserLevel(PortalUtils.getUserLevel(Integer.parseInt(matchedRole)));
        thisMember.setEnteredBy(PortalUtils.getUser(request).getId());
        thisMember.setModifiedBy(PortalUtils.getUser(request).getId());
        thisMember.setStatus(TeamMember.STATUS_PENDING);
        thisMember.setCustomInvitationMessage(optionalMessage);
        if (!TeamMemberList.isOnTeam(db, PortalUtils.getProject(request).getId(), Integer.parseInt(matchedUserId))) {
          if (thisMember.insert(db)) {
            PortalUtils.processInsertHook(request, thisMember);
          }
        }
      }
    }

    String[] mismatches = (String[]) request.getPortletSession().getAttribute("mismatches");
    String[] notMatchedAccessToTools = (String[]) request.getPortletSession().getAttribute("notMatchedAccessToTools");
    if (mismatches != null) {
      for (String unmatchedEntry : mismatches) {
        String firstName = (String) request.getPortletSession().getAttribute("firstName-" + unmatchedEntry);
        String lastName = (String) request.getPortletSession().getAttribute("lastName-" + unmatchedEntry);
        String email = (String) request.getPortletSession().getAttribute("email-" + unmatchedEntry);
        String notMatchedRole = (String) request.getPortletSession().getAttribute("notMatchedRole-" + unmatchedEntry);
        ApplicationPrefs prefs = PortalUtils.getApplicationPrefs(request);
        Project thisProject = PortalUtils.getProject(request);

        request.getPortletSession().removeAttribute("email-" + unmatchedEntry);
        request.getPortletSession().removeAttribute("firstName-" + unmatchedEntry);
        request.getPortletSession().removeAttribute("lastName-" + unmatchedEntry);
        request.getPortletSession().removeAttribute("notMatchedRole-" + notMatchedRole);

        //insert user
        User thisUser = new User();
        thisUser.setGroupId(1);
        thisUser.setDepartmentId(1);
        thisUser.setFirstName(firstName);
        thisUser.setLastName(lastName);
        //company?
        thisUser.setEmail(email);
        thisUser.setUsername(email);
        thisUser.setPassword("unregistered");
        thisUser.setEnteredBy(PortalUtils.getUser(request).getId());
        thisUser.setModifiedBy(PortalUtils.getUser(request).getId());

        //enabled?
        thisUser.setStartPage(1);
        thisUser.setRegistered(false);
        thisUser.setAccountSize(prefs.get("ACCOUNT.SIZE"));
        thisUser.setAccessAddProjects(prefs.get("START_PROJECTS"));
        thisUser.insert(db, PortalUtils.getServerDomainNameAndPort(request), prefs);

        //Insert user into project as pending
        TeamMember thisMember = new TeamMember();
        thisMember.setProjectId(thisProject.getId());
        thisMember.setUserId(thisUser.getId());
        thisMember.setUserLevel(PortalUtils.getUserLevel(Integer.parseInt(notMatchedRole)));
        thisMember.setStatus(TeamMember.STATUS_INVITING);
        thisMember.setEnteredBy(PortalUtils.getUser(request).getId());
        thisMember.setModifiedBy(PortalUtils.getUser(request).getId());
        thisMember.setCustomInvitationMessage(optionalMessage);

        //Determine if this user needs to be provided access to tools
        boolean provideAccessToTools = false;
        if (notMatchedAccessToTools != null) {
          for (String notMatchedToolsEntry : notMatchedAccessToTools) {
            if (notMatchedToolsEntry.equals(unmatchedEntry)) {
              provideAccessToTools = true;
              break;
            }
          }
        }
        thisMember.setTools(provideAccessToTools);
        if (thisMember.insert(db)) {
          PortalUtils.processInsertHook(request, thisMember);
        }
      }
    }

    //remove session attributes
    request.getPortletSession().removeAttribute("matches");
    request.getPortletSession().removeAttribute("accessToTools");
    request.getPortletSession().removeAttribute("mismatches");
    request.getPortletSession().removeAttribute("notMatchedAccessToTools");
  }


  /**
   * @param request
   */
  private boolean buildInvitationList(ActionRequest request) {

    boolean hasError = false;
    String[] matches = request.getParameterValues("matches");
    String[] accessToTools = request.getParameterValues("accessToTools");
    //build invitation list from the chosen ids
    if (matches != null) {
      request.getPortletSession().setAttribute("matches", matches);
      if (accessToTools != null) {
        request.getPortletSession().setAttribute("accessToTools", accessToTools);
      }
      for (String chosenId : matches) {
        request.getPortletSession().setAttribute("matchUserId-" + chosenId, request.getParameter("matchUserId-" + chosenId));
        request.getPortletSession().setAttribute("matchedRole-" + chosenId, request.getParameter("matchedRole-" + chosenId));
      }
    }

    String[] mismatches = request.getParameterValues("mismatches");
    String[] notMatchedAccessToTools = request.getParameterValues("notMatchedAccessToTools");
    if (mismatches != null) {
      request.getPortletSession().setAttribute("mismatches", mismatches);
      if (notMatchedAccessToTools != null) {
        request.getPortletSession().setAttribute("notMatchedAccessToTools", notMatchedAccessToTools);
      }
      for (String unmatchedId : mismatches) {
        String firstName = request.getParameter("firstName-" + unmatchedId);
        String lastName = request.getParameter("lastName-" + unmatchedId);
        String email = request.getParameter("email-" + unmatchedId);
        String notMatchedRole = request.getParameter("notMatchedRole-" + unmatchedId);
        request.getPortletSession().setAttribute("firstName-" + unmatchedId, firstName);
        request.getPortletSession().setAttribute("lastName-" + unmatchedId, lastName);
        request.getPortletSession().setAttribute("email-" + unmatchedId, email);
        request.getPortletSession().setAttribute("notMatchedRole-" + unmatchedId, notMatchedRole);
        if (!StringUtils.hasText(email) || !StringUtils.hasText(firstName) || !StringUtils.hasText(lastName)) {
          hasError = true;
        }
      }
    }
    if (matches == null && mismatches == null) {
      request.getPortletSession().setAttribute(ACTION_ERROR, "Please choose atleast one entry before preparing the invitation message.");
    } else if (hasError) {
      request.getPortletSession().setAttribute(ACTION_ERROR, "Name and email is required for every checked entry.");
    }
    return (matches == null) && (mismatches == null) || hasError;
  }


  /**
   * @param request
   */
  private void buildMatches(ActionRequest request) throws SQLException {

    Connection db = PortalUtils.getConnection(request);
    String[] membersToInvite = request.getParameter(MEMBERS_TO_INVITE).split(",");
    String memberIdsToInvite = request.getParameter(MEMBER_IDS_TO_INVITE);
    LinkedHashMap<String, String> members = new LinkedHashMap<String, String>();
    boolean hasMultipleMatches = false;
    for (String member : membersToInvite) {
      members.put(member.trim(), NO_MATCH_FOUND);
    }
    //1. Id based Query
    //build query from memberIdsTo Invite
    //Get the abbreviated names from the query and see if they match any of the names that are entered.
    if (StringUtils.hasText(memberIdsToInvite)) {
      UserList userList = new UserList();
      userList.setUserIds(memberIdsToInvite);
      userList.buildList(db);
      for (User user : userList) {
        if (members.get(user.getNameFirstLastInitial()) != null) {
          if (members.get(user.getNameFirstLastInitial()).equals(NO_MATCH_FOUND)) {
            members.put(user.getNameFirstLastInitial(), String.valueOf(user.getId()));
          }
        } else if (members.get(user.getNameFirstLast()) != null) {
          if (members.get(user.getNameFirstLast()).equals(NO_MATCH_FOUND)) {
            members.put(user.getNameFirstLast(), String.valueOf(user.getId()));
          }
        }
      }
    }

    //2. Name based Query based on first and last name
    //for the items that did not match in 1.get the names (i.e., first and last names) only (i.e., filter out the emails)
    //Fetch from users by matching first name and last name if more than one character exists in the last name
    Iterator<String> keyIterator = members.keySet().iterator();
    while (keyIterator.hasNext()) {
      String name = keyIterator.next();
      if (members.get(name).equals(NO_MATCH_FOUND) && (name.indexOf("@") == -1)) {
        String[] nameParts = name.split(" ");
        UserList userList = new UserList();
        if (nameParts.length == 1) {
          userList.setFirstName(nameParts[0]);
        } else if (nameParts.length == 2) {
          userList.setFirstName(nameParts[0]);
          userList.setLastName(nameParts[1]);
        }
        userList.buildList(db);
        if (userList.size() > 0) {
          if (userList.size() > 1) {
            hasMultipleMatches = true;
          }
          StringBuffer idStringBuffer = new StringBuffer();
          for (User user : userList) {
            idStringBuffer.append("," + user.getId());
          }
          members.put(name, idStringBuffer.toString().substring(1));
        }
      }
    }

    //3. Email based Query
    keyIterator = members.keySet().iterator();
    while (keyIterator.hasNext()) {
      String email = keyIterator.next();
      if (members.get(email).equals(NO_MATCH_FOUND) && (email.indexOf("@") != -1)) {
        UserList userList = new UserList();
        userList.setEmail(email);
        userList.buildList(db);
        if (userList.size() > 0) {
          if (userList.size() > 1) {
            hasMultipleMatches = true;
          }
          StringBuffer idStringBuffer = new StringBuffer();
          for (User user : userList) {
            idStringBuffer.append("," + user.getId());
          }
          members.put(email, idStringBuffer.toString().substring(1));
        }
      }
    }
    request.getPortletSession().setAttribute(MEMBERS, members);
    request.getPortletSession().setAttribute(HAS_MULTIPLE_MATCHES, String.valueOf(hasMultipleMatches));
  }
}
