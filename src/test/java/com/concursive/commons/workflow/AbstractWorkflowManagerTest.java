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

package com.concursive.commons.workflow;

import com.concursive.commons.db.AbstractConnectionPoolTest;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.utils.LookupList;
import com.concursive.connect.cache.utils.CacheUtils;
import freemarker.template.Configuration;
import com.concursive.commons.workflow.BusinessProcessList;
import com.concursive.commons.workflow.ComponentContext;
import com.concursive.commons.workflow.WorkflowManager;

import java.security.Key;
import java.sql.Timestamp;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Description of the Class
 *
 * @author Ananth
 * @created Feb 12, 2009
 */
public abstract class AbstractWorkflowManagerTest extends AbstractConnectionPoolTest {
  protected WorkflowManager workflowManager;
  protected ComponentContext context;
  protected BusinessProcessList processList;
  protected String fileLibraryPath;
  protected Configuration freemarkerConfiguration;
  protected Key key;
  protected ConcurrentHashMap<String, Object> globalParameters;
  protected User workflowUser;
  protected Project workflowUserProfile;
  protected int USER_ID = -1;

  protected void setUp() throws Exception {
    super.setUp();

    LookupList projectCategoryList = CacheUtils.getLookupList("lookup_project_category");
    int projectCategory = projectCategoryList.getIdFromValue("People");

    // Insert project
    workflowUserProfile = new Project();
    workflowUserProfile.setTitle("John Doe");
    workflowUserProfile.setShortDescription("John Doe's Profile");
    workflowUserProfile.setRequestDate(new Timestamp(System.currentTimeMillis()));
    workflowUserProfile.setEstimatedCloseDate((Timestamp) null);
    workflowUserProfile.setRequestedBy("Project SQL Test Requested By");
    workflowUserProfile.setRequestedByDept("Project SQL Test Requested By Department");
    workflowUserProfile.setBudgetCurrency("USD");
    workflowUserProfile.setBudget("10000.75");
    workflowUserProfile.setGroupId(1);
    workflowUserProfile.setEnteredBy(1);
    workflowUserProfile.setModifiedBy(1);
    workflowUserProfile.setCategoryId(projectCategory);
    workflowUserProfile.setProfile(true);
    assertNotNull(workflowUserProfile);
    boolean result = workflowUserProfile.insert(db);
    assertTrue("Project was not inserted", result);

    //add a new user
    workflowUser = new User();
    workflowUser.setGroupId(1);
    workflowUser.setDepartmentId(1);
    workflowUser.setFirstName("John");
    workflowUser.setLastName("Doe");
    workflowUser.setCompany("xxx");
    workflowUser.setEmail("jdoe" + System.currentTimeMillis() + "@concursive.com");
    workflowUser.setUsername(workflowUser.getEmail());
    workflowUser.setPassword("xxx");
    workflowUser.setEnteredBy(0);
    workflowUser.setModifiedBy(0);
    workflowUser.setEnabled(true);
    workflowUser.setStartPage(1);
    workflowUser.setRegistered(true);
    workflowUser.setProfileProjectId(workflowUserProfile.getId());
    workflowUser.insert(db, "127.0.0.1", mockPrefs);
    assertTrue("Unable to add a user..", workflowUser.getId() != -1);

    USER_ID = workflowUser.getId();

    workflowManager = new WorkflowManager();

    globalParameters = new ConcurrentHashMap<String, Object>();

    context = new ComponentContext();
    context.setParameter(ComponentContext.FILE_LIBRARY_PATH, fileLibraryPath);
    context.setAttribute(ComponentContext.CONNECTION_POOL, connectionPool);
    context.setAttribute(ComponentContext.CONNECTION_ELEMENT, ce);
    context.setAttribute(ComponentContext.APPLICATION_MAIL_SERVER, mockPrefs.get("MAILSERVER"));
    context.setAttribute(ComponentContext.APPLICATION_EMAIL_ADDRESS, mockPrefs.get("EMAILADDRESS"));
    context.setAttribute(ComponentContext.TEAM_KEY, key);
    context.setApplicationPrefs(mockPrefs.getPrefs());
    context.setGlobalParameters(globalParameters);
    context.setAttribute(ComponentContext.FREEMARKER_CONFIGURATION, freemarkerConfiguration);

    // Let the test find the application.xml
    processList = new BusinessProcessList();
    processList.buildList(WorkflowManager.class.getResourceAsStream("/application.xml"));

  }

  protected void tearDown() throws Exception {
    // Delete the user and the user's profile
    workflowUser.delete(db);

    processList = null;
    context = null;
    workflowManager = null;

    super.tearDown();
  }
}
