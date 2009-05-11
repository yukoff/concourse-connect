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

package com.concursive.connect.web.modules.profile.utils;

import com.concursive.commons.db.AbstractConnectionPoolTest;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.dao.ProjectList;

import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * Tests the uniqueId behavior when inserting and updating projects
 *
 * @author matt rajkowski
 * @created Jun 12, 2008
 */
public class ProjectUtilsUniqueIdTest extends AbstractConnectionPoolTest {

  protected static final int GROUP_ID = 1;
  protected static final int USER_ID = 1;

  public void testInsertProjectUniqueId() throws SQLException {
    String uniqueId = System.currentTimeMillis() + "";
    String title = "Unique Id Test " + uniqueId;

    String projectUniqueId1 = null;
    String projectUniqueId2 = null;
    {
      // Insert project
      Project project = new Project();
      project.setTitle(title);
      project.setShortDescription("Project SQL Test Description");
      project.setRequestDate(new Timestamp(System.currentTimeMillis()));
      project.setGroupId(GROUP_ID);
      project.setEnteredBy(USER_ID);
      project.setModifiedBy(USER_ID);
      assertNotNull(project);
      boolean result = project.insert(db);
      assertTrue("Project was not inserted", result);
      assertNotNull(project.getUniqueId());
      assertEquals("unique-id-test-" + uniqueId, project.getUniqueId());
      projectUniqueId1 = project.getUniqueId();
    }

    {
      // Insert project with same name
      Project project = new Project();
      project.setTitle(title);
      project.setShortDescription("Project SQL Test Description");
      project.setRequestDate(new Timestamp(System.currentTimeMillis()));
      project.setGroupId(GROUP_ID);
      project.setEnteredBy(USER_ID);
      project.setModifiedBy(USER_ID);
      assertNotNull(project);
      boolean result = project.insert(db);
      assertTrue("Project was not inserted", result);
      assertNotNull(project.getUniqueId());
      assertEquals("Incremented uniqueId failed", "unique-id-test-" + uniqueId + "-2", project.getUniqueId());
      projectUniqueId2 = project.getUniqueId();
    }

    {
      // Insert project with same name
      Project project = new Project();
      project.setTitle(title);
      project.setShortDescription("Project SQL Test Description");
      project.setRequestDate(new Timestamp(System.currentTimeMillis()));
      project.setGroupId(GROUP_ID);
      project.setEnteredBy(USER_ID);
      project.setModifiedBy(USER_ID);
      assertNotNull(project);
      boolean result = project.insert(db);
      assertTrue("Project was not inserted", result);
      assertNotNull(project.getUniqueId());
      assertEquals("Incremented uniqueId failed", "unique-id-test-" + uniqueId + "-3", project.getUniqueId());
      projectUniqueId2 = project.getUniqueId();
    }

    // Try to find the previously deleted project
    ProjectList projectList = new ProjectList();
    projectList.setTitle(title);
    projectList.buildList(db);
    assertTrue("Found " + projectList.size() + " projects, instead of 3", projectList.size() == 3);
    for (Project project : projectList) {
      project.delete(db, null);
    }
  }

  public void testUpdateProjectUniqueId() throws SQLException {
    String uniqueId = System.currentTimeMillis() + "r";
    String title = "Unique Id Test " + uniqueId;

    String projectUniqueId1 = null;
    String projectUniqueId2 = null;
    {
      // Insert project
      Project project = new Project();
      project.setTitle(title);
      project.setShortDescription("Project SQL Test Description");
      project.setRequestDate(new Timestamp(System.currentTimeMillis()));
      project.setGroupId(GROUP_ID);
      project.setEnteredBy(USER_ID);
      project.setModifiedBy(USER_ID);
      assertNotNull(project);
      boolean result = project.insert(db);
      assertTrue("Project was not inserted", result);
      assertNotNull(project.getUniqueId());
      assertEquals("unique-id-test-" + uniqueId, project.getUniqueId());
      // updating keeps the same uniqueId
      project.update(db);
      assertEquals("unique-id-test-" + uniqueId, project.getUniqueId());
      // even if the uniqueId is nulled out (not submitted)
      project.setUniqueId(null);
      project.update(db);
      assertEquals("unique-id-test-" + uniqueId, project.getUniqueId());

      projectUniqueId1 = project.getUniqueId();
    }

    {
      // Insert project with same name
      Project project = new Project();
      project.setTitle(title);
      project.setShortDescription("Project SQL Test Description");
      project.setRequestDate(new Timestamp(System.currentTimeMillis()));
      project.setGroupId(GROUP_ID);
      project.setEnteredBy(USER_ID);
      project.setModifiedBy(USER_ID);
      assertNotNull(project);
      boolean result = project.insert(db);
      assertTrue("Project was not inserted", result);
      assertNotNull(project.getUniqueId());
      assertEquals("unique-id-test-" + uniqueId + "-2", project.getUniqueId());
      // updating keeps the same uniqueId
      project.update(db);
      assertEquals("unique-id-test-" + uniqueId + "-2", project.getUniqueId());
      // even if the uniqueId is nulled out (not submitted)
      project.setUniqueId(null);
      project.update(db);
      assertEquals("unique-id-test-" + uniqueId + "-2", project.getUniqueId());
      // if the user overrides the unique id, allow it if not taken
      project.setUniqueId("unique-id-test-" + uniqueId + "-3");
      project.update(db);
      assertEquals("unique-id-test-" + uniqueId + "-3", project.getUniqueId());
      projectUniqueId2 = project.getUniqueId();
    }

{
      // Insert project with same name
      Project project = new Project();
      project.setTitle(title);
      project.setShortDescription("Project SQL Test Description");
      project.setRequestDate(new Timestamp(System.currentTimeMillis()));
      project.setGroupId(GROUP_ID);
      project.setEnteredBy(USER_ID);
      project.setModifiedBy(USER_ID);
      assertNotNull(project);
      boolean result = project.insert(db);
      assertTrue("Project was not inserted", result);
      assertNotNull(project.getUniqueId());
      assertEquals("unique-id-test-" + uniqueId + "-4", project.getUniqueId());
      // updating keeps the same uniqueId
      project.update(db);
      assertEquals("unique-id-test-" + uniqueId + "-4", project.getUniqueId());
      // even if the uniqueId is nulled out (not submitted)
      project.setUniqueId(null);
      project.update(db);
      assertEquals("unique-id-test-" + uniqueId + "-4", project.getUniqueId());
      // if the user overrides the unique id, allow it if not taken
      project.setUniqueId("unique-id-test-" + uniqueId + "-5");
      project.update(db);
      assertEquals("unique-id-test-" + uniqueId + "-5", project.getUniqueId());
      projectUniqueId2 = project.getUniqueId();
    }


    // Try to find the previously deleted project
    ProjectList projectList = new ProjectList();
    projectList.setTitle(title);
    projectList.buildList(db);
    assertTrue("Found " + projectList.size() + " projects, instead of 3", projectList.size() == 3);
    for (Project project : projectList) {
      project.delete(db, null);
    }
  }
}
