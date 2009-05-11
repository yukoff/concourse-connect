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
package com.concursive.connect.web.modules.profile.workflow;

import com.concursive.commons.workflow.AbstractWorkflowManagerTest;
import com.concursive.commons.workflow.BusinessProcess;
import com.concursive.connect.cache.utils.CacheUtils;
import com.concursive.connect.web.modules.activity.dao.ProjectHistory;
import com.concursive.connect.web.modules.activity.dao.ProjectHistoryList;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.login.utils.UserUtils;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.dao.ProjectList;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import com.concursive.connect.web.modules.wiki.utils.WikiLink;
import com.concursive.connect.web.utils.LookupList;

import java.sql.Timestamp;

/**
 * Test to see if events are being recorded when a project is updated
 *
 * @author Ananth
 * @created Feb 22, 2009
 */
public class SaveProjectUpdateEventTest extends AbstractWorkflowManagerTest {
  protected static final int GROUP_ID = 1;
  protected static final String PROCESS_NAME = "teamelements.application.project.update";

  protected static final String HISTORY_UPDATE_USER_PROFILE_TEXT = "history.update-user-profile.text";
  protected static final String HISTORY_BECOME_PROFILE_OWNER_TEXT = "history.become-profile-owner.text";
  protected static final String HISTORY_UPDATE_PROFILE_TEXT = "history.update-profile.text";


  public void testSaveProjectUpdateEvent() throws Exception {
    //load the user profile for the current user
    User user = UserUtils.loadUser(USER_ID);
    Project userProfile = ProjectUtils.loadProject(user.getProfileProjectId());
    assertTrue("Unable to load the user's profile", userProfile.getId() > -1);

    //Update the profile
    Project thisProfile = new Project(db, user.getProfileProjectId());
    thisProfile.setCity("Alexandria");
    thisProfile.setModifiedBy(USER_ID);
    int updateCount = thisProfile.update(db);
    assertTrue("The project was not updated by the database", updateCount == 1);

    BusinessProcess thisProcess = processList.get(PROCESS_NAME);
    assertTrue("Process not found...", thisProcess != null);
    context.setPreviousObject(userProfile);
    context.setThisObject(thisProfile);
    context.setProcessName(PROCESS_NAME);
    context.setProcess(thisProcess);
    context.setAttribute("userId", new Integer(USER_ID));
    context.setAttribute("user", WikiLink.generateLink(userProfile));

    workflowManager.execute(context);

    //Now query the database and test to see if a history item was added for the specified test user Id.
    ProjectHistoryList historyList = new ProjectHistoryList();
    historyList.setProjectId(userProfile.getId());
    historyList.setEnteredBy(userProfile.getModifiedBy());
    historyList.setLinkObject(ProjectHistoryList.PROFILE_OBJECT);
    historyList.setEventType(ProjectHistoryList.UPDATE_USER_PROFILE_EVENT);
    historyList.setLinkItemId(userProfile.getId());
    historyList.buildList(db);
    assertTrue("History event was not recorded!", historyList.size() == 1);

    ProjectHistory history = historyList.get(0);
    assertEquals("Recorded event mismatch",
        "[[|" + userProfile.getId() + ":profile||" + userProfile.getTitle() + "]] updated my profile",
        history.getDescription());

    //Delete the history item because the test is done (and any other needed objects)
    history.delete(db);
  }

  public void testUserBecameProfileOwner() throws Exception {
    LookupList projectCategoryList = CacheUtils.getLookupList("lookup_project_category");
    int projectCategory = projectCategoryList.getIdFromValue("Businesses");

    //Add a listing
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
    project.setCategoryId(projectCategory);
    assertNotNull(project);
    boolean result = project.insert(db);
    assertTrue("Project was not inserted", result);
    assertTrue("Project already has an owner", project.getOwner() == -1);

    //Reload the project
    Project previousProject = new Project(db, project.getId());
    Project thisProject = new Project(db, project.getId());
    thisProject.setOwner(USER_ID);
    int updateCount = thisProject.update(db);
    assertTrue("The project was not updated by the database", updateCount == 1);

    //load the user that owns the listing
    User user = UserUtils.loadUser(thisProject.getOwner());
    Project userProfile = ProjectUtils.loadProject(user.getProfileProjectId());

    BusinessProcess thisProcess = (BusinessProcess) processList.get(PROCESS_NAME);
    assertTrue("Process not found...", thisProcess != null);
    context.setPreviousObject(previousProject);
    context.setThisObject(thisProject);
    context.setProcessName(PROCESS_NAME);
    context.setProcess(thisProcess);
    context.setAttribute("userId", new Integer(USER_ID));
    context.setAttribute("user", WikiLink.generateLink(userProfile));
    context.setAttribute("profile", WikiLink.generateLink(project));

    workflowManager.execute(context);

    //Now query the database and test to see if a history item was added for the specified test user Id.
    ProjectHistoryList historyList = new ProjectHistoryList();
    historyList.setProjectId(project.getId());
    historyList.setEnteredBy(project.getOwner());
    historyList.setLinkObject(ProjectHistoryList.PROFILE_OBJECT);
    historyList.setEventType(ProjectHistoryList.BECOME_PROFILE_OWNER_EVENT);
    historyList.setLinkItemId(project.getId());
    historyList.buildList(db);
    assertTrue("History event was not recorded!", historyList.size() == 1);

    ProjectHistory history = historyList.get(0);
    assertEquals("Recorded event mismatch",
        "[[|" + userProfile.getId() + ":profile||" + userProfile.getTitle() + "]] became the owner of [[|" + project.getId() + ":profile||Concursive Test]]",
        history.getDescription());

    //Delete the history item because the test is done (and any other needed objects)
    history.delete(db);

    // Delete the project
    boolean deleteResult = project.delete(db, (String) null);
    assertTrue("Project wasn't deleted", deleteResult);
    // Try to find the previously deleted project
    ProjectList projectList = new ProjectList();
    projectList.setProjectId(project.getId());
    projectList.buildList(db);
    assertTrue("Project exists when it shouldn't -- id " + project.getId(), projectList.size() == 0);
  }


  public void testUserUpdatedListingProfile() throws Exception {
    LookupList projectCategoryList = CacheUtils.getLookupList("lookup_project_category");
    int projectCategory = projectCategoryList.getIdFromValue("Businesses");
    //Add a listing
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
    project.setCategoryId(projectCategory);
    assertNotNull(project);
    boolean result = project.insert(db);
    assertTrue("Project was not inserted", result);
    assertTrue("Project already has an owner", project.getOwner() == -1);

    //Reload the project
    Project previousProject = new Project(db, project.getId());
    Project thisProject = new Project(db, project.getId());
    thisProject.setCity("Alexandria");
    thisProject.setModifiedBy(USER_ID);
    int updateCount = thisProject.update(db);
    assertTrue("The project was not updated by the database", updateCount == 1);

    //load the user that modified the listing
    User user = UserUtils.loadUser(thisProject.getModifiedBy());
    Project userProfile = ProjectUtils.loadProject(user.getProfileProjectId());

    BusinessProcess thisProcess = (BusinessProcess) processList.get(PROCESS_NAME);
    assertTrue("Process not found...", thisProcess != null);
    context.setPreviousObject(previousProject);
    context.setThisObject(thisProject);
    context.setProcessName(PROCESS_NAME);
    context.setProcess(thisProcess);
    context.setAttribute("userId", new Integer(USER_ID));
    context.setAttribute("user", WikiLink.generateLink(userProfile));
    context.setAttribute("profile", WikiLink.generateLink(project));

    workflowManager.execute(context);

    //Now query the database and test to see if a history item was added for the specified test user Id.
    ProjectHistoryList historyList = new ProjectHistoryList();
    historyList.setProjectId(project.getId());
    historyList.setEnteredBy(project.getModifiedBy());
    historyList.setLinkObject(ProjectHistoryList.PROFILE_OBJECT);
    historyList.setEventType(ProjectHistoryList.UPDATE_PROFILE_EVENT);
    historyList.setLinkItemId(project.getId());
    historyList.buildList(db);
    assertTrue("History event was not recorded!", historyList.size() == 1);

    ProjectHistory history = historyList.get(0);
    assertEquals("Recorded event mismatch",
        "[[|" + userProfile.getId() + ":profile||" + userProfile.getTitle() + "]] updated [[|" + project.getId() + ":profile||Concursive Test]]",
        history.getDescription());

    //Delete the history item because the test is done (and any other needed objects)
    history.delete(db);

    // Delete the project
    boolean deleteResult = project.delete(db, (String) null);
    assertTrue("Project wasn't deleted", deleteResult);
    // Try to find the previously deleted project
    ProjectList projectList = new ProjectList();
    projectList.setProjectId(project.getId());
    projectList.buildList(db);
    assertTrue("Project exists when it shouldn't -- id " + project.getId(), projectList.size() == 0);
  }
}
