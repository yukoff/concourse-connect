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

import java.sql.Timestamp;

/**
 * Tests to see if events are being recorded when a new team member is being added.
 *
 * @author Ananth
 * @created Feb 22, 2009
 */
public class SaveTeamMemberAddEventsTest extends AbstractWorkflowManagerTest {
  protected static final int GROUP_ID = 1;
  protected static final String PROCESS_NAME = "teamelements.application.project.memberadd";
  protected final static String HISTORY_INVITE_TEXT = "history.invite.text";
  protected final static String HISTORY_FAN_TEXT = "history.fan.text";


  public void testSaveTeamMemberAddEvents() throws Exception {
    // Insert project
    Project project = new Project();
    project.setTitle("Concursive Test");
    project.setShortDescription("Concursive Test");
    project.setRequestDate(new Timestamp(System.currentTimeMillis()));
    project.setEstimatedCloseDate((Timestamp) null);
    project.setRequestedBy("Project SQL Test Requested By");
    project.setRequestedByDept("Project SQL Test Requested By Department");
    project.setBudgetCurrency("USD");
    project.setBudget("10000.75");
    project.setGroupId(GROUP_ID);
    project.setEnteredBy(USER_ID);
    project.setModifiedBy(USER_ID);
    assertNotNull(project);
    boolean result = project.insert(db);
    assertTrue("Project was not inserted", result);

    //add a new user
    User memberUser = new User();
    memberUser.setGroupId(GROUP_ID);
    memberUser.setDepartmentId(1);
    memberUser.setFirstName("Test User");
    memberUser.setLastName("Test User");
    memberUser.setCompany("xxx");
    memberUser.setEmail("test1" + System.currentTimeMillis() + "@concursive.com");
    memberUser.setUsername(memberUser.getEmail());
    memberUser.setPassword("xxx");
    memberUser.setEnteredBy(0);
    memberUser.setModifiedBy(0);
    memberUser.setEnabled(true);
    memberUser.setStartPage(1);
    memberUser.setRegistered(true);
    memberUser.insert(db, "127.0.0.1", null);
    assertTrue("Unable to add a user..", memberUser.getId() != -1);

    //invite a new team member
    TeamMember thisMember = new TeamMember();
    thisMember.setProjectId(project.getId());
    thisMember.setUserId(memberUser.getId());
    thisMember.setUserLevel(PortalUtils.getUserLevel(TeamMember.MEMBER));
    thisMember.setEnteredBy(USER_ID);
    thisMember.setModifiedBy(USER_ID);
    thisMember.setStatus(TeamMember.STATUS_PENDING);
    thisMember.setCustomInvitationMessage("");
    thisMember.insert(db);

    //load the user who created this member record
    User user = UserUtils.loadUser(thisMember.getEnteredBy());
    Project userProfile = ProjectUtils.loadProject(user.getProfileProjectId());

    //load the user that is being invited
    User member = UserUtils.loadUser(thisMember.getUserId());
    Project memberProfile = ProjectUtils.loadProject(member.getProfileProjectId());

    BusinessProcess thisProcess = processList.get(PROCESS_NAME);
    assertTrue("Process not found...", thisProcess != null);
    context.setPreviousObject(null);
    context.setThisObject(thisMember);
    context.setProcessName(PROCESS_NAME);
    context.setProcess(thisProcess);
    context.setAttribute("userId", new Integer(USER_ID));
    context.setAttribute("user", WikiLink.generateLink(userProfile));
    context.setAttribute("member", WikiLink.generateLink(memberProfile));
    context.setAttribute("profile", WikiLink.generateLink(project));

    workflowManager.execute(context);

    //Now query the database and test to see if a history item was added for the specified test user Id.
    ProjectHistoryList historyList = new ProjectHistoryList();
    historyList.setProjectId(project.getId());
    historyList.setEnteredBy(USER_ID);
    historyList.setLinkObject(ProjectHistoryList.INVITES_OBJECT);
    historyList.setEventType(ProjectHistoryList.INVITE_MEMBER_EVENT);
    historyList.setLinkItemId(thisMember.getId());
    historyList.buildList(db);
    assertTrue("History event (user was invited to become a team member) was not recorded!", historyList.size() == 1);

    ProjectHistory history = historyList.get(0);
    assertEquals("Recorded event mismatch",
        "[[|" + userProfile.getId() + ":profile||" + userProfile.getTitle() + "]] invited [[|" + memberProfile.getId() + ":profile||" + memberProfile.getTitle() + "]] to [[|" + project.getId() + ":profile||Concursive Test]]",
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

  public void testIsFanTeamMemberEvent() throws Exception {
    // Insert project
    Project project = new Project();
    project.setTitle("Concursive Test");
    project.setShortDescription("Concursive Test");
    project.setRequestDate(new Timestamp(System.currentTimeMillis()));
    project.setEstimatedCloseDate((Timestamp) null);
    project.setRequestedBy("Project SQL Test Requested By");
    project.setRequestedByDept("Project SQL Test Requested By Department");
    project.setBudgetCurrency("USD");
    project.setBudget("10000.75");
    project.setGroupId(GROUP_ID);
    project.setEnteredBy(USER_ID);
    project.setModifiedBy(USER_ID);
    assertNotNull(project);
    boolean result = project.insert(db);
    assertTrue("Project was not inserted", result);

    //add a new user
    User memberUser = new User();
    memberUser.setGroupId(GROUP_ID);
    memberUser.setDepartmentId(1);
    memberUser.setFirstName("Test User");
    memberUser.setLastName("Test User");
    memberUser.setCompany("xxx");
    memberUser.setEmail("test1" + System.currentTimeMillis() + "@concursive.com");
    memberUser.setUsername(memberUser.getEmail());
    memberUser.setPassword("xxx");
    memberUser.setEnteredBy(0);
    memberUser.setModifiedBy(0);
    memberUser.setEnabled(true);
    memberUser.setStartPage(1);
    memberUser.setRegistered(true);
    memberUser.insert(db, "127.0.0.1", null);
    assertTrue("Unable to add a user..", memberUser.getId() != -1);

    //add a team member with joined status
    TeamMember thisMember = new TeamMember();
    thisMember.setProjectId(project.getId());
    thisMember.setUserId(memberUser.getId());
    thisMember.setUserLevel(PortalUtils.getUserLevel(TeamMember.MEMBER));
    thisMember.setEnteredBy(USER_ID);
    thisMember.setModifiedBy(USER_ID);
    thisMember.setStatus(TeamMember.STATUS_JOINED);
    thisMember.setCustomInvitationMessage("");
    thisMember.insert(db);

    //load the user that became a member
    User member = UserUtils.loadUser(thisMember.getUserId());
    Project memberProfile = ProjectUtils.loadProject(member.getProfileProjectId());

    BusinessProcess thisProcess = processList.get(PROCESS_NAME);
    assertTrue("Process not found...", thisProcess != null);
    context.setPreviousObject(null);
    context.setThisObject(thisMember);
    context.setProcessName(PROCESS_NAME);
    context.setProcess(thisProcess);
    context.setAttribute("userId", new Integer(USER_ID));
    context.setAttribute("member", WikiLink.generateLink(memberProfile));
    context.setAttribute("profile", WikiLink.generateLink(project));

    workflowManager.execute(context);

    //Now query the database and test to see if a history item was added for the specified test user Id.
    ProjectHistoryList historyList = new ProjectHistoryList();
    historyList.setProjectId(project.getId());
    historyList.setEnteredBy(thisMember.getUserId());
    historyList.setLinkObject(ProjectHistoryList.MEMBER_OBJECT);
    historyList.setEventType(ProjectHistoryList.BECOME_PROFILE_FAN_EVENT);
    historyList.setLinkItemId(thisMember.getId());
    historyList.buildList(db);
    assertTrue("History event (team member became a fan) was not recorded!", historyList.size() == 1);

    ProjectHistory history = historyList.get(0);
    assertEquals("Recorded event mismatch",
        "[[|" + memberProfile.getId() + ":profile||" + memberProfile.getTitle() + "]] became a member of [[|" + project.getId() + ":profile||Concursive Test]]",
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
}
