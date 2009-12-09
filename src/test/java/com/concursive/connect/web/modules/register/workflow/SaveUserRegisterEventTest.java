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
package com.concursive.connect.web.modules.register.workflow;

import com.concursive.connect.web.modules.register.beans.RegisterBean;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.login.dao.UserList;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import com.concursive.connect.web.modules.activity.dao.ProjectHistory;
import com.concursive.connect.web.modules.activity.dao.ProjectHistoryList;
import com.concursive.commons.codec.PasswordHash;
import com.concursive.commons.text.StringUtils;
import com.concursive.connect.web.modules.wiki.utils.WikiLink;
import com.concursive.commons.workflow.BusinessProcess;
import com.concursive.commons.workflow.AbstractWorkflowManagerTest;

/**
 * Test to see if a user register event is being recorded..
 *
 * @author Ananth
 * @created Feb 22, 2009
 */
public class SaveUserRegisterEventTest extends AbstractWorkflowManagerTest {
  protected static final int GROUP_ID = 1;
  protected static final String PROCESS_NAME = "teamelements.application.portal.register";
  protected static final String HISTORY_TEXT = "history.text";

  public void testSaveUserRegisterEvent() throws Exception {
    RegisterBean registerBean = new RegisterBean();
    registerBean.setPassword(String.valueOf(StringUtils.rand(100000, 999999)));
    registerBean.setEncryptedPassword(PasswordHash.encrypt(registerBean.getPassword()));
    //add a new user
    User user = new User();
    user.setGroupId(GROUP_ID);
    user.setDepartmentId(1);
    user.setFirstName("Test User");
    user.setLastName("Test User");
    user.setCompany("xxx");
    user.setEmail("jd-test" + System.currentTimeMillis() + "@concursive.com");
    user.setUsername(user.getEmail());
    user.setPassword(registerBean.getEncryptedPassword());
    user.setEnteredBy(0);
    user.setModifiedBy(0);
    user.setEnabled(true);
    user.setStartPage(1);
    user.setRegistered(true);
    user.insert(db, "127.0.0.1", mockPrefs);
    assertTrue("Unable to add a user..", user.getId() != -1);

    registerBean.setUser(user);

    Project userProfile = ProjectUtils.loadProject(user.getProfileProjectId());

    BusinessProcess thisProcess = (BusinessProcess) processList.get(PROCESS_NAME);
    assertTrue("Process not found...", thisProcess != null);
    context.setPreviousObject(null);
    context.setThisObject(registerBean);
    context.setProcessName(PROCESS_NAME);
    context.setProcess(thisProcess);
    context.setAttribute("user", WikiLink.generateLink(userProfile));
    context.setAttribute("prefs.title", mockPrefs.get("TITLE"));

    workflowManager.execute(context);

    //Now query the database and test to see if a history item was added for the specified test user Id.
    ProjectHistoryList historyList = new ProjectHistoryList();
    historyList.setProjectId(user.getProfileProjectId());
    historyList.setEnteredBy(user.getId());
    historyList.setLinkObject(ProjectHistoryList.SITE_OBJECT);
    historyList.setEventType(ProjectHistoryList.USER_REGISTRATION_EVENT);
    historyList.setLinkItemId(user.getId());
    historyList.buildList(db);
    assertTrue("History event was not recorded!", historyList.size() == 1);

    ProjectHistory history = historyList.get(0);
    assertEquals("Recorded event mismatch",
        "[[|" + userProfile.getId() + ":profile||Test User Test User]] joined the site",
        history.getDescription());

    //Delete the history item because the test is done (and any other needed objects)
    history.delete(db);

    // Delete the user record
    int deleteResult = user.delete(db);
    assertTrue("Project wasn't deleted", deleteResult != 0);
    // Try to find the previously deleted user
    UserList userList = new UserList();
    userList.setId(user.getId());
    userList.buildList(db);
    assertTrue("User exists when it shouldn't -- id " + user.getId(), userList.size() == 0);
  }
}
