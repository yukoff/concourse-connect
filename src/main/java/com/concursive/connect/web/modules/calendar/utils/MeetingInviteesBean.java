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
package com.concursive.connect.web.modules.calendar.utils;

import com.concursive.commons.text.StringUtils;
import com.concursive.commons.web.mvc.beans.GenericBean;
import com.concursive.connect.config.ApplicationPrefs;
import com.concursive.connect.web.modules.admin.beans.UserSearchBean;
import com.concursive.connect.web.modules.calendar.dao.Meeting;
import com.concursive.connect.web.modules.calendar.dao.MeetingAttendee;
import com.concursive.connect.web.modules.calendar.dao.MeetingAttendeeList;
import com.concursive.connect.web.modules.calendar.dao.MeetingList;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.login.dao.UserList;
import com.concursive.connect.web.modules.login.utils.UserUtils;
import com.concursive.connect.web.modules.members.dao.TeamMember;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import com.concursive.connect.web.portal.PortalUtils;
import com.concursive.connect.web.utils.PagedListInfo;

import javax.portlet.ActionRequest;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Handles meeting and invitees parameters and processes invitee list
 *
 * @author Nanda Kumar
 * @created June 05, 2009
 */
public class MeetingInviteesBean extends GenericBean {

  private static final String NOT_MATCHED_PROFILE = "profileNotMatched";
  private static final String NOT_MATCHED_EMAIL = "emailNotMatched";
  private static final String NOT_MATCHED_NAME = "nameNotMatched";
  private Meeting meeting = null;
  private Project project = null;
  private MeetingAttendeeList meetingAttendeeList = null;
  private MeetingAttendee meetingAttendee = null;
  private int action = -1;
  private boolean isModifiedMeeting = false;
  private UserList cancelledUsers = null;
  private UserList meetingChangeUsers = null;
  private UserList rejectedUsers = null;
  private Map<User, String> membersFoundList = null;
  private Map<String, String> membersNotFoundList = null;
  private Map<String, UserList> membersMultipleList = null;

  public MeetingInviteesBean() {
  }

  public MeetingInviteesBean(Meeting meeting, Project project, int action) {
    //sets class member objects
    this.meeting = meeting;
    this.project = project;
    this.action = action;

    //create list
    membersFoundList = new LinkedHashMap<User, String>();
    membersNotFoundList = new LinkedHashMap<String, String>();
    membersMultipleList = new LinkedHashMap<String, UserList>();

    cancelledUsers = new UserList();
    meetingChangeUsers = new UserList();
    rejectedUsers = new UserList();
  }

  public Project getProject() {
    return project;
  }

  public Meeting getMeeting() {
    return meeting;
  }

  public MeetingAttendee getMeetingAttendee() {
    return meetingAttendee;
  }

  public void setMeetingAttendee(MeetingAttendee meetingAttendee) {
    this.meetingAttendee = meetingAttendee;
  }

  public int getAction() {
    return action;
  }

  public void setAction(int action) {
    this.action = action;
  }

  /*
   * Returns true if the meeting values have been modified.
   */
  public boolean getIsModifiedMeeting() {
    return isModifiedMeeting;
  }

  public void setIsModifiedMeeting(boolean isModifiedMeeting) {
    this.isModifiedMeeting = isModifiedMeeting;
  }

  /*
   * Returns the userlist for sending meeting cancelled mail
   */
  public UserList getCancelledUsers() {
    return cancelledUsers;
  }

  /*
   * Returns the userlist for sending meeting change mail
   */
  public UserList getMeetingChangeUsers() {
    return meetingChangeUsers;
  }

  /*
   * Returns the userlist for sending invitation rejected mail
   */
  public UserList getRejectedUsers() {
    return rejectedUsers;
  }

  /*
   * Returns map with invited users
   */
  public Map<User, String> getMembersFoundList() {
    return membersFoundList;
  }

  /*
   * Returns map with invitees to be confirmed or non member invitees
   */
  public Map<String, String> getMembersNotFoundList() {
    return membersNotFoundList;
  }

  /*
   * Returns map with invitees in case if multiple profiles matches were found
   */
  public Map<String, UserList> getMembersMultipleList() {
    return membersMultipleList;
  }

  /*
   * Adds new invitees
   */
  public boolean inviteeNewMembers(Connection db, ActionRequest request, String[] firstName,
      String[] lastName, String[] emailAddress) throws SQLException {
    boolean isInserted = false;
    for (int i = 0; i < firstName.length; i++) {
      if (StringUtils.hasText(firstName[i]) && StringUtils.hasText(lastName[i]) && StringUtils.hasText(emailAddress[i])) {
        //set user details;
        User user = new User();
        user.setGroupId(1);
        user.setDepartmentId(1);
        user.setFirstName(firstName[i]);
        user.setLastName(lastName[i]);
        user.setEmail(emailAddress[i]);
        user.setUsername(emailAddress[i]);
        user.setPassword("unregistered");
        user.setEnteredBy(meeting.getModifiedBy());
        user.setModifiedBy(meeting.getModifiedBy());
        user.setStartPage(1);
        user.setRegistered(false);

        boolean invited = insertInvitee(db, request, firstName[i] + " " + lastName[i], user);
        //set if atleast one user has been invited.
        if (!isInserted) {
          isInserted = invited;
        }
      }
    }
    return isInserted;
  }

  /*
   * sets the cancelled users list for sending mail
   */
  public void cancelMeeting(Connection db) throws SQLException {
    meetingAttendeeList = new MeetingAttendeeList();
    meetingAttendeeList.setMeetingId(meeting.getId());
    meetingAttendeeList.buildList(db);

    //add them to cancel user list to send mail
    addAttendeeToCancelledUsers(meetingAttendeeList);
  }

  /**
   * @param db              - Connection object
   * @param request         - request object
   * @param previousMeeting - previous meeting
   * @return - true if the meeting has been modified.
   * @throws SQLException
   */
  public boolean compareInvitees(Connection db, ActionRequest request, Meeting previousMeeting)
      throws SQLException {
    boolean updated = false;

    //get all attendee list
    meetingAttendeeList = new MeetingAttendeeList();
    meetingAttendeeList.setMeetingId(meeting.getId());
    meetingAttendeeList.buildList(db);

    if (!meeting.getIsDimdim()) {
      meeting.setDimdimMeetingId(null);
    }

    //Check if there are meeting attendees.
    if (!StringUtils.hasText(meeting.getMeetingInvitees().trim())) {
      if (meeting.update(db) > 0) {
        updated = true;
      }
    } else {
      //check for any new invitees added to the list
      updated = processInvitees(db, request);
    }

    //check for removed users
    if (updated) {
      //check if the meeting has been modified
      isMeetingModified(previousMeeting);

      //check if any invitees remains after processing then delete them
      for (MeetingAttendee meetingAttendee : meetingAttendeeList) {
        meetingAttendee.delete(db);
      }

      //add the users to invitation rejected list
      addAttendeeToRejectedUsers(meetingAttendeeList);

      //call Dimdim API and delete meeting if the web meeting was unchecked during edit
      if (!meeting.getIsDimdim() && previousMeeting.getIsDimdim()) {
        //switch to cancel mode and use previous meeting instance for dimdim meetingId
        action = DimDimUtils.ACTION_MEETING_DIMDIM_CANCEL;
        Meeting temp = meeting;
        meeting = previousMeeting;
        //call api
        DimDimUtils.processDimdimMeeting(this, null);
        //switch back to edit mode for sending mail
        meeting = temp;
        action = DimDimUtils.ACTION_MEETING_DIMDIM_EDIT;
      }
    }

    return updated;
  }

  /*
   * Processes through the invitee list for adding attendees
   */
  public boolean processInvitees(Connection db, ActionRequest request) throws SQLException {
    boolean inserted = false;
    String meetingInvitees = meeting.getMeetingInvitees().trim();

    // find and set the dimdim credentials for the host from previous meeting
    if (meeting.getIsDimdim()) {
      setDimdimCredentials(db);
    }

    // check if dimdim option is not selected.
    if (meetingInvitees.length() < 1) {
      // insert the meeting without any invitees
      if (meeting.getId() == -1) {
        inserted = meeting.insert(db);
      }
      return inserted;
    }

    // check insert action for new meeting
    if (meeting.getId() == -1) {
      inserted = meeting.insert(db);
      if (!inserted) {
        return false;
      }
    }

    // check if update
    int updated = -1;
    if (action == DimDimUtils.ACTION_MEETING_DIMDIM_EDIT) {
      updated = meeting.update(db);
      if (updated == 0) {
        return false;
      }
    }

    // split the invitee list and loop through to process
    String[] meetingInviteesArr = meetingInvitees.split(",");

    for (String invitee : meetingInviteesArr) {
      invitee = invitee.trim();

      //find invitees with profile ids
      if (invitee.indexOf("(") > -1) {

        //get name and profile
        String[] nameProfile = invitee.split("\\(");

        int end = nameProfile[1].indexOf(")") < 0 ? nameProfile[1].length() : nameProfile[1].indexOf(")");
        String profile = nameProfile[1].substring(0, end).trim();
        String name = nameProfile[0].trim();

        //check if profile exists and add as meeting attendee
        checkProfile(db, request, profile, name);
        continue;
      }

      // find invitess with email ids
      if (invitee.indexOf("@") > -1) {
        checkUserEmail(db, request, invitee);
        continue;
      }

      //check name field
      checkName(db, invitee, NOT_MATCHED_NAME);
    }

    //check if the db has been committed after inviting members
    if (!membersFoundList.isEmpty()) {
      return true;
    }

    //return true after committing if there are members to be confirmed or if update has been successful
    return hasInviteesToConfirm() || updated > 0;
  }

  public boolean joinMeeting(Connection db, ActionRequest request, Meeting meeting, User user, String join) throws SQLException {
    //create attendee
    MeetingAttendee thisAttendee = new MeetingAttendee();
    thisAttendee.setMeetingId(meeting.getId());
    thisAttendee.setUserId(user.getId());
    thisAttendee.setEnteredBy(user.getId());
    thisAttendee.setModifiedBy(user.getId());

    //check if user is a member of the team
    TeamMember member = project.getTeam().getTeamMember(user.getId());
    boolean addAsTeamMember = (member == null || member.getId() == -1);

    //set attendee status
    if (meeting.getIsDimdim()) {
      if ("yes".equalsIgnoreCase(join)) {
        thisAttendee.setDimdimStatus(MeetingAttendee.STATUS_DIMDIM_APPROVE_YES);
      } else {
        thisAttendee.setDimdimStatus(MeetingAttendee.STATUS_DIMDIM_APPROVE_MAYBE);
      }

      //needs owner approval
      addAsTeamMember = false;
    } else {
      if ("yes".equalsIgnoreCase(join)) {
        thisAttendee.setDimdimStatus(MeetingAttendee.STATUS_DIMDIM_ACCEPTED);
      } else {
        thisAttendee.setDimdimStatus(MeetingAttendee.STATUS_DIMDIM_TENTATIVE);
      }
    }

    if (addAsTeamMember) {
      TeamMember thisMember = new TeamMember();
      thisMember.setProjectId(meeting.getProjectId());
      thisMember.setUserId(user.getId());
      thisMember.setUserLevel(PortalUtils.getUserLevel(TeamMember.PARTICIPANT));
      thisMember.setStatus(TeamMember.STATUS_JOINED);
      thisMember.setEnteredBy(user.getId());
      thisMember.setModifiedBy(user.getId());
      String optionalMessage = "";
      thisMember.setCustomInvitationMessage(optionalMessage);
      if (thisMember.insert(db)) {
        PortalUtils.processInsertHook(request, thisMember);
      }
    }

    //add user as attendee
    if (thisAttendee.insert(db)) {
      this.meetingAttendee = thisAttendee;
      return true;
    }
    return false;
  }

  /*
   * Verifies if a profile exists
   */
  private boolean checkProfile(Connection db, ActionRequest request, String profile, String name) throws SQLException {
    // find user
    Project userProject = ProjectUtils.loadProject(profile);
    if (userProject != null && userProject.getProfile()) {
      // user found
      User user = UserUtils.loadUser(userProject.getOwner());
      return insertInvitee(db, request, name, user);
    }

    // check first and last names
    return checkName(db, name, MeetingInviteesBean.NOT_MATCHED_PROFILE);
  }

  /*
   * Verifies if a user with the emailid exists
   */
  private boolean checkUserEmail(Connection db, ActionRequest request, String email)
      throws SQLException {
    // Query users for email
    int userId = User.getIdByEmailAddress(db, (String) DimDimUtils.processEmail(email).get(DimDimUtils.EMAIL));

    // user found
    if (userId > -1) {
      User thisUser = new User(db, userId);
      return insertInvitee(db, request, email, thisUser);
    }

    // user does not exist
    addToMemberNotFoundList(email, MeetingInviteesBean.NOT_MATCHED_EMAIL);
    return false;
  }

  /*
   * checks if user(s) can be found for the invitee name
   */
  private boolean checkName(Connection db, String invitee, String notMatched)
      throws SQLException {

    String[] inviteeName = invitee.split(" ");

    // first name and last name present
    UserList userList = new UserList();
    if (inviteeName.length == 2) {
      userList.setFirstName(inviteeName[0].trim());
      userList.setLastName(inviteeName[1].trim());
      userList.buildList(db);
    }
    // only one name present search first and last names
    if (inviteeName.length == 1) {
      UserSearchBean userSearch = new UserSearchBean();
      userSearch.setName(inviteeName[0].trim());
      userList.setSearchCriteria(userSearch);
      userList.buildList(db);
    }

    // multiple user found
    if (userList.size() >= 1) {
      if (addToMultipleMemberList(invitee, userList)) {
        return true;
      }
    }

    //user does not exist
    addToMemberNotFoundList(invitee, notMatched);
    return false;
  }

  /*
   * Add the invitee accordingly to the user, member and attendee tables
   */
  private boolean insertInvitee(Connection db, ActionRequest request, String invitee, User user) throws SQLException {
    boolean inserted = true;

    // check if the user is present in attendeelist, in case of modify meeting
    for (int i = 0; meetingAttendeeList != null && i < meetingAttendeeList.size();) {
      MeetingAttendee meetingAttendee = meetingAttendeeList.get(i);
      if (meetingAttendee.getUserId() == user.getId()) {
        meetingAttendeeList.remove(i);

        // if valid dimdim meeting then send meeting change mail otherwise send invitation mail.
        if (meeting.getIsDimdim() && !StringUtils.hasText(meeting.getDimdimMeetingId())) {
          addToMemberFoundList(user, invitee);
        } else {
          meetingChangeUsers.add(user);
        }
        return false;
      }
      i++;
    }

    // return if the user has already been invited.
    if (isMemberInvited(user)) {
      return false;
    }

    // check if the user is registered
    boolean isNewMember = false;
    if (user.getId() == -1) {
      ApplicationPrefs prefs = PortalUtils.getApplicationPrefs(request);
      isNewMember = true;
      user.setAccountSize(prefs.get("ACCOUNT.SIZE"));
      user.setAccessAddProjects(prefs.get("START_PROJECTS"));
      if ("true".equals(prefs.get(ApplicationPrefs.USERS_CAN_INVITE)) || PortalUtils.getUser(request).getAccessInvite() || PortalUtils.getUser(request).getAccessAdmin()) {
        user.setInstanceId(PortalUtils.getInstance(request).getId());
        inserted = user.insert(db, PortalUtils.getServerDomainNameAndPort(request), prefs);
      }
    }

    // check if the user is a member of the team
    if (!isNewMember) {
      TeamMember teamMember = project.getTeam().getTeamMember(user.getId());
      if (teamMember == null) {
        isNewMember = true;
      }
    }

    // Insert user into project as pending
    TeamMember thisMember = new TeamMember();
    if (isNewMember && inserted) {
      thisMember.setProjectId(meeting.getProjectId());
      thisMember.setUserId(user.getId());
      thisMember.setUserLevel(PortalUtils.getUserLevel(TeamMember.PARTICIPANT));
      thisMember.setStatus(TeamMember.STATUS_INVITING);
      thisMember.setEnteredBy(meeting.getModifiedBy());
      thisMember.setModifiedBy(meeting.getModifiedBy());
      String optionalMessage = "";
      thisMember.setCustomInvitationMessage(optionalMessage);
      inserted = thisMember.insert(db);
    }

    // insert to attendee table
    if (inserted) {
      MeetingAttendee thisAttendee = new MeetingAttendee();
      thisAttendee.setMeetingId(meeting.getId());
      thisAttendee.setUserId(user.getId());
      thisAttendee.setDimdimStatus(MeetingAttendee.STATUS_DIMDIM_INVITED);
      thisAttendee.setEnteredBy(meeting.getEnteredBy());
      thisAttendee.setModifiedBy(meeting.getModifiedBy());
      inserted = thisAttendee.insert(db);
      this.meetingAttendee = thisAttendee;
    }

    //add the invitee to found list
    if (inserted) {
      if (isNewMember) {
        PortalUtils.processInsertHook(request, thisMember);
      }
      addToMemberFoundList(user, invitee);
    }
    return inserted;
  }

  /*
   * Checks if the user has already been invited
   */
  private boolean isMemberInvited(User user) {
    //check in new mail list
    Set<User> keySet = membersFoundList.keySet();
    for (User key : keySet) {
      if (key.getId() == user.getId()) {
        return true;
      }
    }

    //check in changed mail list
    for (User key : meetingChangeUsers) {
      if (key.getId() == user.getId()) {
        return true;
      }
    }
    return false;
  }

  /**
   * Add members to multiple member found list
   *
   * @param invitee  - As entered by the user
   * @param userList - List of users to add.
   */
  private boolean addToMultipleMemberList(String invitee, UserList userList) {
    removeInvitedMembers(userList);

    //add to multiple userlist if the list is not empty
    if (!userList.isEmpty()) {
      membersMultipleList.put(invitee, userList);
      return true;
    }
    return false;
  }

  //check if any of the users in the userlist has been already invited. Remove meeting host also from the list.
  private void removeInvitedMembers(UserList userList) {
    for (int i = 0; i < userList.size();) {
      User thisUser = userList.get(i);
      if (meeting.getOwner() == thisUser.getId() || isMemberInvited(thisUser)) {
        userList.remove(i);
        continue;
      }
      i++;
    }
  }

  /**
   * Finds dimdim credentials from previous meeting setup by the host
   */
  private void setDimdimCredentials(Connection db) throws SQLException {
    //check if credentials are already set for the meeting.
    if (StringUtils.hasText(meeting.getDimdimUrl()) || StringUtils.hasText(meeting.getDimdimUsername()) || StringUtils.hasText(meeting.getDimdimPassword())) {
      return;
    }

    //get the meeting created by the user
    MeetingList meetingList = new MeetingList();
    meetingList.setForUser(meeting.getOwner());
    meetingList.setIsDimdim(true);

    //order the meeting list to get the latest first
    PagedListInfo pagedListInfo = new PagedListInfo();
    pagedListInfo.setColumnToSortBy("m.meeting_id");
    pagedListInfo.setSortOrder("desc");
    pagedListInfo.setItemsPerPage(3);
    meetingList.setPagedListInfo(pagedListInfo);
    meetingList.buildList(db);

    for (Meeting thisMeeting : meetingList) {
      // skip the current meeting
      if (thisMeeting.getId() != meeting.getId()) {
        meeting.setDimdimUrl(thisMeeting.getDimdimUrl());
        meeting.setDimdimUsername(thisMeeting.getDimdimUsername());
        meeting.setDimdimPassword(thisMeeting.getDimdimPassword());
        break;
      }
    }
  }

  /**
   * Add invitee to member not found list
   *
   * @param invitee    - As entered by the user
   * @param notMatched - reason for not being found
   *                   (email not matched, name not matched or profile not matched)
   */
  private void addToMemberNotFoundList(String invitee, String notMatched) {
    membersNotFoundList.put(invitee, notMatched);
  }

  /**
   * Add the member to member found list and removes this member from the multiple memberlist
   *
   * @param user    - user to be added
   * @param invitee - As entered by the user
   */
  private void addToMemberFoundList(User user, String invitee) {
    //add the member to the member found list
    membersFoundList.put(user, invitee);

    //check if this user is present in multiple member found list
    Set<String> keySet = membersMultipleList.keySet();

    Iterator<String> iterator = keySet.iterator();
    while (iterator.hasNext()) {
      String key = iterator.next();
      UserList userList = membersMultipleList.get(key);
      removeInvitedMembers(userList);
      if (userList.isEmpty()) {
        iterator.remove();
      }
    }
  }

  /*
   * finds the user info of the meeting attendees and adds to invitation rejected users list for sending mail
   */
  private void addAttendeeToRejectedUsers(MeetingAttendeeList meetingAttendeeList) {
    for (MeetingAttendee meetingAttendee : meetingAttendeeList) {
      User user = UserUtils.loadUser(meetingAttendee.getUserId());
      rejectedUsers.add(user);
    }
  }

  /*
   * finds the user info of the meeting attendees and adds to cancelled users list for sending mail
   */
  private void addAttendeeToCancelledUsers(MeetingAttendeeList meetingAttendeeList) {
    for (MeetingAttendee meetingAttendee : meetingAttendeeList) {
      User user = UserUtils.loadUser(meetingAttendee.getUserId());
      cancelledUsers.add(user);
    }
  }

  /**
   * Add comma separated userIds to mail user list
   */
  private void addToMailUserList(String userIds, UserList userList) {
    //check if ids are empty
    if ("".equals(userIds) || userIds == null) {
      return;
    }

    //split the userlist
    userIds = userIds.trim();
    String[] arrId = userIds.split(",");

    //add them to the member found list
    for (String userId : arrId) {
      userId = userId.trim();
      if (!"".equals(userId)) {
        userList.add(UserUtils.loadUser(Integer.parseInt(userId)));
      }
    }
  }

  /**
   * Populate mail user list
   */
  public void populateMailUserList(String invitedUserIds, String rejectedUserIds, String cancelledUserIds, String changedUserIds) {
    addToMailUserList(rejectedUserIds, rejectedUsers);
    addToMailUserList(cancelledUserIds, cancelledUsers);
    addToMailUserList(changedUserIds, meetingChangeUsers);

    //add to member found list
    if ("".equals(invitedUserIds) || invitedUserIds == null) {
      return;
    }

    //split the userlist
    invitedUserIds = invitedUserIds.trim();
    String[] arrId = invitedUserIds.split(",");

    //add them to the member found list
    for (String userId : arrId) {
      userId = userId.trim();
      if (!"".equals(userId)) {
        membersFoundList.put(UserUtils.loadUser(Integer.parseInt(userId)), "");
      }
    }
  }

  /**
   * Check if all the invitees have been processed or if dimdim credentials are empty
   *
   * @return false - if all invitees have been processed and dimdim credentials are also available
   */
  public boolean hasInviteesToConfirm() {
    return !membersNotFoundList.isEmpty() || !membersMultipleList.isEmpty();
  }

  /**
   * Checks the values of previous and current meeting
   *
   * @param previousMeeting - previous meeting to be compared.
   * @return - true if previousMeeting is not null and current meeting values have changed. Sets isModifiedMeeting to true
   */
  private boolean isMeetingModified(Meeting previousMeeting) {
    if (previousMeeting == null) {
      return false;
    }
    isModifiedMeeting = true;
    if (!meeting.getTitle().equalsIgnoreCase(previousMeeting.getTitle())) {
      return true;
    }
    if (!meeting.getDescription().equalsIgnoreCase((previousMeeting.getDescription()))) {
      return true;
    }
    if (!meeting.getLocation().equalsIgnoreCase((previousMeeting.getLocation()))) {
      return true;
    }
    if (meeting.getStartDate().compareTo(previousMeeting.getStartDate()) != 0) {
      return true;
    }
    if (meeting.getEndDate().compareTo(previousMeeting.getEndDate()) != 0) {
      return true;
    }
    if (meeting.getIsDimdim() != previousMeeting.getIsDimdim()) {
      return true;
    }
    isModifiedMeeting = false;
    return false;
  }

  /**
   * Changes the status of a meeting attendee
   *
   * @param db            - Connection object
   * @param user          - attendee to change the status
   * @param meetingStatus - status to change to
   *                      MeetingAttendee.STATUS_DIMDIM_ACCEPTED
   *                      MeetingAttendee.STATUS_DIMDIM_DECLINED
   *                      MeetingAttendee.STATUS_DIMDIM_INVITED
   *                      MeetingAttendee.STATUS_DIMDIM_TENTATIVE
   * @return - true if status has changed and the attendee it set to MeetingAttendeeList
   *         for sending status mail to meeting host
   * @throws SQLException
   */
  public boolean setMeetingStatus(Connection db, ActionRequest request, User user, int meetingStatus) throws SQLException {
    MeetingAttendeeList meetingAttendeeList = new MeetingAttendeeList();
    meetingAttendeeList.setMeetingId(meeting.getId());
    meetingAttendeeList.buildList(db);

    this.meetingAttendee = meetingAttendeeList.getMeetingAttendee(user);
    if (meetingAttendee == null) {
      return false;
    }

    meetingAttendee.setModifiedBy(meetingAttendee.getUserId());

    if (action == DimDimUtils.ACTION_MEETING_APPROVE_JOIN) {
      //check if meeting owner denied meeting request.
      if (meetingStatus == MeetingAttendee.STATUS_DIMDIM_DECLINED) {
        meetingAttendee.delete(db);
        return true;
      }

      //if accepted then check if the user status is 'maybe'
      if (meetingStatus == MeetingAttendee.STATUS_DIMDIM_ACCEPTED &&
          meetingAttendee.getDimdimStatus() == MeetingAttendee.STATUS_DIMDIM_APPROVE_MAYBE) {
        meetingStatus = MeetingAttendee.STATUS_DIMDIM_TENTATIVE;
      }
      meetingAttendee.setModifiedBy(meeting.getOwner());

      //add attendee as team member
      TeamMember member = project.getTeam().getTeamMember(meetingAttendee.getUserId());
      if ((member == null || member.getId() == -1)) {
        TeamMember thisMember = new TeamMember();
        thisMember.setProjectId(meeting.getProjectId());
        thisMember.setUserId(meetingAttendee.getUserId());
        thisMember.setUserLevel(PortalUtils.getUserLevel(TeamMember.PARTICIPANT));
        thisMember.setStatus(TeamMember.STATUS_JOINED);
        thisMember.setEnteredBy(meeting.getOwner());
        thisMember.setModifiedBy(meeting.getOwner());
        String optionalMessage = "";
        thisMember.setCustomInvitationMessage(optionalMessage);
        if (thisMember.insert(db)) {
          PortalUtils.processInsertHook(request, thisMember);
        }
      }
    }

    //set attendee status and update
    meetingAttendee.setDimdimStatus(meetingStatus);
    if (meetingAttendee.update(db) > 0) {

      //call dimdim api to edit meeting
      if (meeting.getIsDimdim() && action == DimDimUtils.ACTION_MEETING_APPROVE_JOIN) {
        String attendeeIds = "";
        for (MeetingAttendee thisAttendee : meetingAttendeeList) {
          attendeeIds += thisAttendee.getUserId() + ", ";
        }
        attendeeIds = DimDimUtils.trimComma(attendeeIds);
        populateMailUserList(null, null, null, attendeeIds);
        DimDimUtils.processDimdimMeeting(this, null);
      }
      return true;
    }
    return false;
  }
}
