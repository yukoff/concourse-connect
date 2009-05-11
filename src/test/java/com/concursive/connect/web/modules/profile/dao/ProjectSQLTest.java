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
package com.concursive.connect.web.modules.profile.dao;

import com.concursive.commons.db.AbstractConnectionPoolTest;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.dao.ProjectList;

import java.sql.SQLException;
import java.sql.Timestamp;


/**
 * Tests common project database access
 *
 * @author matt
 * @created January 24, 2008
 */
public class ProjectSQLTest extends AbstractConnectionPoolTest {

  protected static final int GROUP_ID = 1;
  protected static final int USER_ID = 1;

  public void testProjectCRUD() throws SQLException {
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
    assertTrue("Inserted project did not have an id", project.getId() > -1);

    // Update project
    // Try updating without reloading the project
    assertNull(project.getModified());
    assertTrue("Project to update does not have an id", project.getId() > -1);
    int updateCount = project.update(db);
    assertTrue("The modified field checks for concurrent updates so the modified field must match the load value", updateCount == 0);
    // Reload the project, then update
    assertTrue(project.getId() > -1);
    project = new Project(db, project.getId());
    project.setTitle("Project SQL Test Updated Project");
    updateCount = project.update(db);
    assertTrue("The project was not updated by the database", updateCount == 1);

    // Find the previously set project
    int projectId = project.getId();
    project = null;
    ProjectList projectList = new ProjectList();
    projectList.setProjectId(projectId);
    projectList.buildList(db);
    assertTrue(projectList.size() == 1);
    project = projectList.get(0);
    assertNotNull(project);
    assertTrue("Retrieved project does not match id", project.getId() == projectId);

    // Delete the project
    boolean deleteResult = project.delete(db, (String) null);
    assertTrue("Project wasn't deleted", deleteResult);
    // Try to find the previously deleted project
    projectList = new ProjectList();
    projectList.setProjectId(projectId);
    projectList.buildList(db);
    assertTrue("Project exists when it shouldn't -- id " + projectId, projectList.size() == 0);
  }

  public void testExpandedProjectCRUD() throws SQLException {
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
    project.setWebPage("www.concursive.com");
    project.setBusinessPhone("800.555.1212");
    project.setGroupId(GROUP_ID);
    project.setEnteredBy(USER_ID);
    project.setModifiedBy(USER_ID);
    assertNotNull(project);
    boolean result = project.insert(db);
    assertTrue("Project was not inserted", result);
    assertTrue("Inserted project did not have an id", project.getId() > -1);

    // Check if the formatters worked
    assertTrue("http://www.concursive.com".equals(project.getWebPage()));
    assertEquals("(800) 555-1212", project.getBusinessPhone());

    // Delete the project
    boolean deleteResult = project.delete(db, (String) null);
    assertTrue("Project wasn't deleted", deleteResult);
  }

}