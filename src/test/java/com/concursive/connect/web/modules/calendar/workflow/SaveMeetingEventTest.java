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
package com.concursive.connect.web.modules.calendar.workflow;

import com.concursive.commons.date.DateUtils;
import com.concursive.commons.workflow.AbstractWorkflowManagerTest;
import com.concursive.commons.workflow.BusinessProcess;
import com.concursive.connect.web.modules.activity.dao.ProjectHistory;
import com.concursive.connect.web.modules.activity.dao.ProjectHistoryList;
import com.concursive.connect.web.modules.calendar.dao.Meeting;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.dao.ProjectList;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import com.concursive.connect.web.modules.wiki.utils.WikiLink;

import java.sql.Timestamp;
import java.util.Calendar;

/**
 * Tests to see if project meeting events are being recorded..
 *
 * @author Ananth
 * @created Feb 22, 2009
 */
public class SaveMeetingEventTest extends AbstractWorkflowManagerTest {
  protected static final int GROUP_ID = 1;

  protected final static String HISTORY_ADD_EVENT_TEXT = "history.addevent.text";
  protected final static String HISTORY_UPDATE_EVENT_TEXT = "history.updateevent.text";

  public void testSaveMeetingEvent() throws Exception {
    String PROCESS_NAME = "teamelements.application.project.setup-event";

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
    project.setGroupId(GROUP_ID);
    project.setEnteredBy(USER_ID);
    project.setModifiedBy(USER_ID);
    assertNotNull(project);
    boolean result = project.insert(db);
    assertTrue("Project was not inserted", result);

    //Add a project meeting record
    Meeting meeting = new Meeting();
    meeting.setProjectId(project.getId());
    meeting.setOwner(USER_ID);
    meeting.setTitle("Dummy Meeting Title");
    meeting.setEnteredBy(USER_ID);
    meeting.setModifiedBy(USER_ID);
    meeting.setStartDate(new Timestamp(System.currentTimeMillis()));
    meeting.setEndDate(new Timestamp(System.currentTimeMillis()));
    meeting.insert(db);
    assertTrue("Unable to add a meeting record", meeting.getId() > -1);

    Project userProfile = ProjectUtils.loadProject(workflowUser.getProfileProjectId());

    BusinessProcess thisProcess = processList.get(PROCESS_NAME);
    assertTrue("Process not found...", thisProcess != null);
    context.setPreviousObject(null);
    context.setThisObject(meeting);
    context.setProcessName(PROCESS_NAME);
    context.setProcess(thisProcess);
    context.setAttribute("userId", new Integer(USER_ID));
    context.setAttribute("user", WikiLink.generateLink(userProfile));
    context.setAttribute("event", WikiLink.generateLink(meeting));
    context.setAttribute("profile", WikiLink.generateLink(project));

    workflowManager.execute(context);

    //Now query the database and test to see if a history item was added for the specified test user Id.
    ProjectHistoryList historyList = new ProjectHistoryList();
    historyList.setProjectId(meeting.getProjectId());
    historyList.setEnteredBy(USER_ID);
    historyList.setLinkObject(ProjectHistoryList.MEETING_OBJECT);
    historyList.setEventType(ProjectHistoryList.SETUP_PROFILE_MEETING_EVENT);
    historyList.setLinkItemId(meeting.getId());
    historyList.buildList(db);
    assertTrue("History event was not recorded!", historyList.size() == 1);

    ProjectHistory history = historyList.get(0);
    assertEquals("Recorded event mismatch",
        "[[|" + userProfile.getId() + ":profile||" + userProfile.getTitle() + "]] scheduled [[|" + project.getId() + ":event|" + meeting.getId() + "|Dummy Meeting Title]] in [[|" + project.getId() + ":profile||" + project.getTitle() + "]]",
        history.getDescription());

    //Delete the history item because the test is done (and any other needed objects)
    history.delete(db);

    //Delete the meeting
    meeting.delete(db);

    // Delete the project
    boolean deleteResult = project.delete(db, (String) null);
    assertTrue("Project wasn't deleted", deleteResult);
    // Try to find the previously deleted project
    ProjectList projectList = new ProjectList();
    projectList.setProjectId(project.getId());
    projectList.buildList(db);
    assertTrue("Project exists when it shouldn't -- id " + project.getId(), projectList.size() == 0);
  }

  public void testProjectUpdateMeetingEvent() throws Exception {
    String PROCESS_NAME = "teamelements.application.project.update-event";

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
    project.setGroupId(GROUP_ID);
    project.setEnteredBy(USER_ID);
    project.setModifiedBy(USER_ID);
    assertNotNull(project);
    boolean result = project.insert(db);
    assertTrue("Project was not inserted", result);

    //Add a project meeting record
    Meeting meeting = new Meeting();
    meeting.setProjectId(project.getId());
    meeting.setOwner(USER_ID);
    meeting.setTitle("Dummy Meeting Title");
    meeting.setEnteredBy(USER_ID);
    meeting.setModifiedBy(USER_ID);
    meeting.setStartDate(new Timestamp(System.currentTimeMillis()));
    meeting.setEndDate(new Timestamp(System.currentTimeMillis()));
    meeting.insert(db);
    assertTrue("Unable to add a meeting record", meeting.getId() > -1);

    Meeting thisMeeting = new Meeting(db, meeting.getId());
    thisMeeting.setEndDate(DateUtils.getEndOfDay(Calendar.getInstance().getTimeInMillis()));
    thisMeeting.update(db);

    Project userProfile = ProjectUtils.loadProject(workflowUser.getProfileProjectId());

    BusinessProcess thisProcess = (BusinessProcess) processList.get(PROCESS_NAME);
    assertTrue("Process not found...", thisProcess != null);
    context.setPreviousObject(meeting);
    context.setThisObject(thisMeeting);
    context.setProcessName(PROCESS_NAME);
    context.setProcess(thisProcess);
    context.setAttribute("userId", new Integer(USER_ID));
    context.setAttribute("user", WikiLink.generateLink(userProfile));
    context.setAttribute("event", WikiLink.generateLink(meeting));
    context.setAttribute("profile", WikiLink.generateLink(project));

    workflowManager.execute(context);

    //Now query the database and test to see if a history item was added for the specified test user Id.
    ProjectHistoryList historyList = new ProjectHistoryList();
    historyList.setProjectId(meeting.getProjectId());
    historyList.setEnteredBy(USER_ID);
    historyList.setLinkObject(ProjectHistoryList.MEETING_OBJECT);
    historyList.setEventType(ProjectHistoryList.UPDATE_PROFILE_MEETING_EVENT);
    historyList.setLinkItemId(meeting.getId());
    historyList.buildList(db);
    assertTrue("History event was not recorded!", historyList.size() == 1);

    ProjectHistory history = historyList.get(0);
    assertEquals("Recorded event mismatch",
        "[[|" + userProfile.getId() + ":profile||" + userProfile.getTitle() + "]] re-scheduled [[|" + project.getId() + ":event|" + meeting.getId() + "|Dummy Meeting Title]] in [[|" + project.getId() + ":profile||" + project.getTitle() + "]]",
        history.getDescription());

    //Delete the history item because the test is done (and any other needed objects)
    history.delete(db);

    //Delete the meeting
    meeting.delete(db);

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
