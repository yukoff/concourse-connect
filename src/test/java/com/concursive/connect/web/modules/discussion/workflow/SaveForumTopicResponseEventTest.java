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
package com.concursive.connect.web.modules.discussion.workflow;

import com.concursive.commons.workflow.AbstractWorkflowManagerTest;
import com.concursive.commons.workflow.BusinessProcess;
import com.concursive.connect.web.modules.activity.dao.ProjectHistory;
import com.concursive.connect.web.modules.activity.dao.ProjectHistoryList;
import com.concursive.connect.web.modules.discussion.dao.Forum;
import com.concursive.connect.web.modules.discussion.dao.Reply;
import com.concursive.connect.web.modules.discussion.dao.Topic;
import com.concursive.connect.web.modules.login.utils.UserUtils;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.dao.ProjectList;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import com.concursive.connect.web.modules.wiki.utils.WikiLink;

import java.sql.Timestamp;

/**
 * Test to see if a response posted to a topic was recorded..
 *
 * @author Ananth
 * @created Feb 22, 2009
 */
public class SaveForumTopicResponseEventTest extends AbstractWorkflowManagerTest {
  protected static final int GROUP_ID = 1;
  protected static final String PROCESS_NAME = "teamelements.application.project.post-answer";

  public void testSaveForumTopicResponseEvent() throws Exception {
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

    //Insert a issue category
    Forum forum = new Forum();
    forum.setProjectId(project.getId());
    forum.setEnteredBy(USER_ID);
    forum.setModifiedBy(USER_ID);
    forum.setSubject("Dummy Forum Subject");
    forum.insert(db);
    assertTrue("Unable to create a forum", forum.getId() > -1);

    //Insert a forum topic record
    Topic topic = new Topic();
    topic.setProjectId(project.getId());
    topic.setEnteredBy(USER_ID);
    topic.setModifiedBy(USER_ID);
    topic.setCategoryId(forum.getId());
    topic.setSubject("Dummy Subject");
    topic.setBody("Dummy Body");
    topic.setQuestion(true);
    topic.insert(db);
    assertTrue("Unable to add a topic entry", topic.getId() > -1);

    //Insert a forum topic response record..
    Reply answer = new Reply();
    answer.setIssueId(topic.getId());
    answer.setEnteredBy(USER_ID);
    answer.setModifiedBy(USER_ID);
    answer.setSubject("Dummy Reply Subject");
    answer.setBody("Dummy Body");
    answer.setProjectId(project.getId());
    answer.setCategoryId(forum.getId());
    answer.insert(db);
    assertTrue("Unable to add a answer", answer.getId() > -1);

    Project userProfile = ProjectUtils.loadProject(workflowUser.getProfileProjectId());

    BusinessProcess thisProcess = processList.get(PROCESS_NAME);
    assertTrue("Process not found...", thisProcess != null);
    context.setPreviousObject(null);
    context.setThisObject(answer);
    context.setProcessName(PROCESS_NAME);
    context.setProcess(thisProcess);
    context.setAttribute("userId", new Integer(USER_ID));
    context.setAttribute("user", WikiLink.generateLink(userProfile));
    context.setAttribute("topic", WikiLink.generateLink(topic));
    context.setAttribute("profile", WikiLink.generateLink(project));

    workflowManager.execute(context);

    //Now query the database and test to see if a history item was added for the specified test user Id.
    ProjectHistoryList historyList = new ProjectHistoryList();
    historyList.setProjectId(answer.getProjectId());
    historyList.setEnteredBy(USER_ID);
    historyList.setLinkObject(ProjectHistoryList.REPLY_OBJECT);
    historyList.setEventType(ProjectHistoryList.POST_FORUM_TOPIC_RESPONSE_EVENT);
    historyList.setLinkItemId(answer.getId());
    historyList.buildList(db);
    assertTrue("History event was not recorded!", historyList.size() == 1);

    ProjectHistory history = historyList.get(0);
    assertEquals("Recorded event mismatch",
        "[[|" + userProfile.getId() + ":profile||" + userProfile.getTitle() + "]] " +
            "@[[|" + project.getId() + ":profile||" + project.getTitle() + "]] " +
            "posted a reply to [[|" + project.getId() + ":topic|" + topic.getId() + "|Dummy Subject]]",
        history.getDescription());

    //Delete the history item because the test is done (and any other needed objects)
    history.delete(db);

    //Delete the records
    answer.delete(db, (String) null);
    topic.delete(db, (String) null);
    forum.delete(db, (String) null);

    // Delete the project
    boolean deleteResult = project.delete(db, (String) null);
    assertTrue("Project wasn't deleted", deleteResult);
    // Try to find the previously deleted project
    ProjectList projectList = new ProjectList();
    projectList.setProjectId(project.getId());
    projectList.buildList(db);
    assertTrue("Project exists when it shouldn't -- id " + project.getId(), projectList.size() == 0);
  }


  public void testMarkAnswerEvent() throws Exception {
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

    //Insert a issue category
    Forum forum = new Forum();
    forum.setProjectId(project.getId());
    forum.setEnteredBy(USER_ID);
    forum.setModifiedBy(USER_ID);
    forum.setSubject("Dummy Forum Subject");
    forum.insert(db);
    assertTrue("Unable to create a forum", forum.getId() > -1);

    //Insert a forum topic record
    Topic topic = new Topic();
    topic.setProjectId(project.getId());
    topic.setEnteredBy(USER_ID);
    topic.setModifiedBy(USER_ID);
    topic.setCategoryId(forum.getId());
    topic.setSubject("Dummy Subject");
    topic.setBody("Dummy Body");
    topic.setQuestion(true);
    topic.insert(db);
    assertTrue("Unable to add a topic entry", topic.getId() > -1);

    //Insert a forum topic response record..
    Reply answer = new Reply();
    answer.setIssueId(topic.getId());
    answer.setEnteredBy(USER_ID);
    answer.setModifiedBy(USER_ID);
    answer.setSubject("Dummy Reply Subject");
    answer.setBody("Dummy Body");
    answer.setProjectId(project.getId());
    answer.setCategoryId(forum.getId());
    answer.insert(db);
    assertTrue("Unable to add a reply", answer.getId() > -1);

    //Insert a forum topic response record..
    Reply reply = new Reply();
    reply.setIssueId(topic.getId());
    reply.setEnteredBy(USER_ID);
    reply.setModifiedBy(USER_ID);
    reply.setSubject("Dummy Reply Subject");
    reply.setBody("Dummy Body");
    reply.setProjectId(project.getId());
    reply.setReplyToId(answer.getId());
    reply.setAnswered(Reply.ANSWERED);
    reply.setCategoryId(forum.getId());
    reply.insert(db);
    assertTrue("Unable to add a reply", reply.getId() > -1);

    Project userProfile = ProjectUtils.loadProject(workflowUser.getProfileProjectId());

    Project answeredByProfile = ProjectUtils.loadProject((UserUtils.loadUser(answer.getEnteredBy())).getProfileProjectId());

    BusinessProcess thisProcess = processList.get(PROCESS_NAME);
    assertTrue("Process not found...", thisProcess != null);
    context.setPreviousObject(null);
    context.setThisObject(reply);
    context.setProcessName(PROCESS_NAME);
    context.setProcess(thisProcess);
    context.setAttribute("userId", new Integer(USER_ID));
    context.setAttribute("user", WikiLink.generateLink(userProfile));
    context.setAttribute("answeredby", WikiLink.generateLink(answeredByProfile));
    context.setAttribute("topic", WikiLink.generateLink(topic));
    context.setAttribute("profile", WikiLink.generateLink(project));

    workflowManager.execute(context);

    //Now query the database and test to see if a history item was added for the specified test user Id.
    ProjectHistoryList historyList = new ProjectHistoryList();
    historyList.setProjectId(reply.getProjectId());
    historyList.setEnteredBy(USER_ID);
    historyList.setLinkObject(ProjectHistoryList.REPLY_OBJECT);
    historyList.setEventType(ProjectHistoryList.MARK_FORUM_TOPIC_RESPONSE_EVENT);
    historyList.setLinkItemId(reply.getId());
    historyList.buildList(db);

    assertTrue("History event was not recorded!", historyList.size() == 1);

    ProjectHistory history = historyList.get(0);
    assertEquals("Recorded event mismatch",
        "[[|" + userProfile.getId() + ":profile||" + userProfile.getTitle() + "]] " +
            "@[[|" + project.getId() + ":profile||" + project.getTitle() + "]] " +
            "found an answer for [[|" + project.getId() + ":topic|" + topic.getId() + "|Dummy Subject]]",
        history.getDescription());

    //Delete the history item because the test is done (and any other needed objects)
    history.delete(db);

    //Delete the records
    answer.delete(db, (String) null);
    reply.delete(db, (String) null);
    topic.delete(db, (String) null);
    forum.delete(db, (String) null);

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
