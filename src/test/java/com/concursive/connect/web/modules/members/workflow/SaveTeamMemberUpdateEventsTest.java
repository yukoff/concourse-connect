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
package com.concursive.connect.web.modules.members.workflow;

import com.concursive.commons.workflow.AbstractWorkflowManagerTest;
import com.concursive.commons.workflow.BusinessProcess;
import com.concursive.connect.cache.utils.CacheUtils;
import com.concursive.connect.web.modules.activity.dao.ProjectHistory;
import com.concursive.connect.web.modules.activity.dao.ProjectHistoryList;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.login.utils.UserUtils;
import com.concursive.connect.web.modules.members.dao.TeamMember;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.dao.ProjectList;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import com.concursive.connect.web.modules.wiki.utils.WikiLink;
import com.concursive.connect.web.portal.PortalUtils;
import com.concursive.connect.web.utils.LookupList;

import java.sql.Timestamp;

/**
 * Tests to see if events are being recorded when a team member record is updated
 *
 * @author Ananth
 * @created Feb 22, 2009
 */
public class SaveTeamMemberUpdateEventsTest extends AbstractWorkflowManagerTest {
  protected static final int GROUP_ID = 1;
  protected static final String PROCESS_NAME = "teamelements.application.project.memberupdate";

  protected final static String HISTORY_INVITE_TEXT = "history.invite.text";
  protected final static String HISTORY_PROMOTE_TEXT = "history.promote.text";
  protected final static String HISTORY_GRANT_TEXT = "history.grant.text";


  protected Project createProject() throws Exception {
    // Insert project
    Project project = new Project();
    project.setTitle("Project SQL Test");
    project.setShortDescription("Project SQL Test Description");
    project.setRequestDate(new Timestamp(System.currentTimeMillis()));
    project.setEstimatedCloseDate((Timestamp) null);
    project.setRequestedBy("Project SQL Test Requested By");
    project.setRequestedByDept("Project SQL Test Requested By Department");
    project.setBudgetCurrency("USD");
    project.setBudget("10000.75");
    //project.setApprovalDate((Timestamp) null);
    //project.setCloseDate((Timestamp) null);
    project.setGroupId(GROUP_ID);
    project.setEnteredBy(USER_ID);
    project.setModifiedBy(USER_ID);
    assertNotNull(project);
    boolean result = project.insert(db);
    assertTrue("Project was not inserted", result);

    return project;
  }

  protected User createUser() throws Exception {
    //add a new user
    User user = new User();
    user.setGroupId(GROUP_ID);
    user.setDepartmentId(1);
    user.setFirstName("Test User");
    user.setLastName("Test User");
    user.setCompany("xxx");
    user.setEmail("test8" + System.currentTimeMillis() + "@concursive.com");
    user.setPassword("xxx");
    user.setUsername(user.getEmail());
    user.setEnteredBy(0);
    user.setModifiedBy(0);
    user.setEnabled(true);
    user.setStartPage(1);
    user.setRegistered(true);
    user.insert(db, "127.0.0.1", null);
    assertTrue("Unable to add a user..", user.getId() != -1);
    return user;
  }

  public void testTeamMemberAcceptedInvitationEvent() throws Exception {
    //Need a project
    Project project = this.createProject();

    //Need a user
    User memberUser = this.createUser();

    //Need a team member
    TeamMember prevMember = new TeamMember();
    prevMember.setProjectId(project.getId());
    prevMember.setUserId(memberUser.getId());
    prevMember.setUserLevel(PortalUtils.getUserLevel(TeamMember.MEMBER));
    prevMember.setEnteredBy(USER_ID);
    prevMember.setModifiedBy(USER_ID);
    prevMember.setStatus(TeamMember.STATUS_PENDING);
    prevMember.setCustomInvitationMessage("");
    prevMember.insert(db);
    assertTrue("Unable to add a new team member", prevMember.getId() != -1);

    //Update the team member
    TeamMember thisMember = new TeamMember(db, prevMember.getId());
    ProjectUtils.accept(db, thisMember.getProjectId(), thisMember.getUserId());
    thisMember = new TeamMember(db, thisMember.getId());

    Project memberProfile = ProjectUtils.loadProject(memberUser.getProfileProjectId());

    User user = UserUtils.loadUser(thisMember.getModifiedBy());
    Project userProfile = ProjectUtils.loadProject(user.getProfileProjectId());

    BusinessProcess thisProcess = (BusinessProcess) processList.get(PROCESS_NAME);
    assertTrue("Process not found...", thisProcess != null);
    context.setPreviousObject(prevMember);
    context.setThisObject(thisMember);
    context.setProcessName(PROCESS_NAME);
    context.setProcess(thisProcess);
    context.setAttribute("userId", new Integer(USER_ID));
    context.setAttribute("member", WikiLink.generateLink(memberProfile));
    context.setAttribute("user", WikiLink.generateLink(userProfile));
    context.setAttribute("profile", WikiLink.generateLink(project));

    workflowManager.execute(context);

    //Now query the database and test to see if a history item was added for the specified test user Id.
    ProjectHistoryList historyList = new ProjectHistoryList();
    historyList.setProjectId(project.getId());
    historyList.setEnteredBy(thisMember.getUserId());
    historyList.setLinkObject(ProjectHistoryList.INVITES_OBJECT);
    historyList.setEventType(ProjectHistoryList.ACCEPT_INVITATION_EVENT);
    historyList.setLinkItemId(thisMember.getId());
    historyList.buildList(db);
    assertTrue("History event (user was invited to become a team member) was not recorded!", historyList.size() == 1);

    ProjectHistory history = historyList.get(0);
    assertEquals("Recorded event mismatch",
        "[[|" + memberProfile.getId() + ":profile||" + memberProfile.getTitle() + "]] accepted an invitation from [[|" + userProfile.getId() + ":profile||" + userProfile.getTitle() + "]] to join [[|" + project.getId() + ":profile||Project SQL Test]]",
        history.getDescription());

    //Delete the history item because the test is done (and any other needed objects)
    history.delete(db);

    //delete the team member record
    thisMember.delete(db);

    //delete the user record
    memberUser.delete(db);

    //delete the project
    boolean deleteResult = project.delete(db, (String) null);
    assertTrue("Project wasn't deleted", deleteResult);
    // Try to find the previously deleted project
    ProjectList projectList = new ProjectList();
    projectList.setProjectId(project.getId());
    projectList.buildList(db);
    assertTrue("Project exists when it shouldn't -- id " + project.getId(), projectList.size() == 0);
  }

  public void testTeamMemberPromotionEvent() throws Exception {
    //Need a project
    Project project = this.createProject();

    //Need a user
    User memberUser = this.createUser();

    //Add a team member record for the current user
    TeamMember admin = new TeamMember();
    admin.setProjectId(project.getId());
    admin.setUserId(USER_ID);
    admin.setUserLevel(PortalUtils.getUserLevel(TeamMember.MANAGER));
    admin.setEnteredBy(USER_ID);
    admin.setModifiedBy(USER_ID);
    admin.setStatus(TeamMember.STATUS_ACCEPTED);
    admin.setCustomInvitationMessage("");
    admin.insert(db);
    assertTrue("Unable to add a new admin team member", admin.getId() != -1);

    //Need a team member
    TeamMember prevMember = new TeamMember();
    prevMember.setProjectId(project.getId());
    prevMember.setUserId(memberUser.getId());
    prevMember.setUserLevel(PortalUtils.getUserLevel(TeamMember.MEMBER));
    prevMember.setEnteredBy(USER_ID);
    prevMember.setModifiedBy(USER_ID);
    prevMember.setStatus(TeamMember.STATUS_ACCEPTED);
    prevMember.setCustomInvitationMessage("");
    prevMember.insert(db);
    assertTrue("Unable to add a new team member", prevMember.getId() != -1);

    project.getTeam().add(admin);
    project.getTeam().add(prevMember);

    //Update the team member
    TeamMember thisMember = new TeamMember(db, prevMember.getId());
    TeamMember.changeRole(db, project, USER_ID, prevMember.getUserId(), PortalUtils.getUserLevel(TeamMember.VIP));
    thisMember = new TeamMember(db, thisMember.getId());

    Project memberProfile = ProjectUtils.loadProject(memberUser.getProfileProjectId());
    Project userProfile = ProjectUtils.loadProject(workflowUser.getProfileProjectId());
    LookupList roleList = CacheUtils.getLookupList("lookup_project_role");

    BusinessProcess thisProcess = (BusinessProcess) processList.get(PROCESS_NAME);
    assertTrue("Process not found...", thisProcess != null);
    context.setPreviousObject(prevMember);
    context.setThisObject(thisMember);
    context.setProcessName(PROCESS_NAME);
    context.setProcess(thisProcess);
    context.setAttribute("userId", new Integer(USER_ID));
    context.setAttribute("user", WikiLink.generateLink(userProfile));
    context.setAttribute("member", WikiLink.generateLink(memberProfile));
    context.setAttribute("profile", WikiLink.generateLink(project));
    context.setAttribute("role", roleList.getValueFromId(thisMember.getUserLevel()));

    workflowManager.execute(context);

    //Now query the database and test to see if a history item was added for the specified test user Id.
    ProjectHistoryList historyList = new ProjectHistoryList();
    historyList.setProjectId(project.getId());
    historyList.setEnteredBy(USER_ID);
    historyList.setLinkObject(ProjectHistoryList.ROLE_OBJECT);
    historyList.setEventType(ProjectHistoryList.PROMOTE_MEMBER_EVENT);
    historyList.setLinkItemId(thisMember.getId());
    historyList.buildList(db);
    assertTrue("History event (team member was promoted to VIP status) was not recorded!", historyList.size() == 1);

    ProjectHistory history = historyList.get(0);
    assertEquals("Recorded event mismatch",
        "[[|" + userProfile.getId() + ":profile||" + userProfile.getTitle() + "]] promoted [[|" + memberProfile.getId() + ":profile||Test User Test User]] to [[|" + project.getId() + ":profile||Project SQL Test]] VIP",
        history.getDescription());

    //Delete the history item because the test is done (and any other needed objects)
    history.delete(db);

    //delete the team member records
    thisMember.delete(db);
    admin.delete(db);

    //delete the user record
    memberUser.delete(db);

    //delete the project
    boolean deleteResult = project.delete(db, (String) null);
    assertTrue("Project wasn't deleted", deleteResult);
    // Try to find the previously deleted project
    ProjectList projectList = new ProjectList();
    projectList.setProjectId(project.getId());
    projectList.buildList(db);
    assertTrue("Project exists when it shouldn't -- id " + project.getId(), projectList.size() == 0);
  }

  public void testTeamMemberRecievedToolsAccessEvent() throws Exception {
    //Need a project
    Project project = this.createProject();

    //Need a user
    User memberUser = this.createUser();

    //Need a team member
    TeamMember prevMember = new TeamMember();
    prevMember.setProjectId(project.getId());
    prevMember.setUserId(memberUser.getId());
    prevMember.setUserLevel(PortalUtils.getUserLevel(TeamMember.MEMBER));
    prevMember.setEnteredBy(USER_ID);
    prevMember.setModifiedBy(USER_ID);
    prevMember.setStatus(TeamMember.STATUS_ACCEPTED);
    prevMember.setCustomInvitationMessage("");
    prevMember.insert(db);
    assertTrue("Unable to add a new team member", prevMember.getId() != -1);

    //Update the team member
    TeamMember thisMember = new TeamMember(db, prevMember.getId());
    thisMember.setTools(true);
    thisMember.updateTools(db);
    thisMember = new TeamMember(db, thisMember.getId());

    Project memberProfile = ProjectUtils.loadProject(memberUser.getProfileProjectId());
    Project userProfile = ProjectUtils.loadProject(workflowUser.getProfileProjectId());

    BusinessProcess thisProcess = processList.get(PROCESS_NAME);
    assertTrue("Process not found...", thisProcess != null);
    context.setPreviousObject(prevMember);
    context.setThisObject(thisMember);
    context.setProcessName(PROCESS_NAME);
    context.setProcess(thisProcess);
    context.setAttribute("userId", new Integer(USER_ID));
    context.setAttribute("user", WikiLink.generateLink(userProfile));
    context.setAttribute("member", WikiLink.generateLink(memberProfile));

    workflowManager.execute(context);

    //Now query the database and test to see if a history item was added for the specified test user Id.
    ProjectHistoryList historyList = new ProjectHistoryList();
    historyList.setProjectId(project.getId());
    historyList.setEnteredBy(USER_ID);
    historyList.setLinkObject(ProjectHistoryList.TOOLS_OBJECT);
    historyList.setEventType(ProjectHistoryList.GRANT_MEMBER_TOOLS_EVENT);
    historyList.setLinkItemId(thisMember.getId());
    historyList.buildList(db);
    assertTrue("History event (team member was granted tools access) was not recorded!", historyList.size() == 1);

    ProjectHistory history = historyList.get(0);
    assertEquals("Recorded event mismatch",
        "[[|" + userProfile.getId() + ":profile||" + userProfile.getTitle() + "]] granted tools access to [[|" + memberProfile.getId() + ":profile||Test User Test User]]",
        history.getDescription());

    //Delete the history item because the test is done (and any other needed objects)
    history.delete(db);

    //delete the team member records
    thisMember.delete(db);

    //delete the user record
    memberUser.delete(db);

    //delete the project
    boolean deleteResult = project.delete(db, (String) null);
    assertTrue("Project wasn't deleted", deleteResult);
    // Try to find the previously deleted project
    ProjectList projectList = new ProjectList();
    projectList.setProjectId(project.getId());
    projectList.buildList(db);
    assertTrue("Project exists when it shouldn't -- id " + project.getId(), projectList.size() == 0);
  }
}
