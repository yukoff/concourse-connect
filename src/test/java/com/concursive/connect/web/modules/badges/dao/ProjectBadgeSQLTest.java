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
package com.concursive.connect.web.modules.badges.dao;

import com.concursive.commons.db.AbstractConnectionPoolTest;
import com.concursive.connect.web.modules.badges.dao.Badge;
import com.concursive.connect.web.modules.badges.dao.BadgeCategory;
import com.concursive.connect.web.modules.badges.dao.ProjectBadge;
import com.concursive.connect.web.modules.badges.dao.ProjectBadgeList;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.dao.ProjectCategory;

import java.sql.SQLException;

/**
 * Tests common projectbadge database access
 *
 * @author lorraine bittner
 * @created May 23, 2008
 */
public class ProjectBadgeSQLTest extends AbstractConnectionPoolTest {

    protected static final int USER_ID = 1;

  public void testProjectBadgeCRUD() throws SQLException {
    int badgeId;
    int projectId;
    // Insert a project category
    ProjectCategory projectCategory = new ProjectCategory();
    projectCategory.setDescription("ProjectBadgeSQLTest"+System.currentTimeMillis());
    projectCategory.insert(db);
    // Insert a badge category
    BadgeCategory badgeCategory = new BadgeCategory();
    badgeCategory.setProjectCategoryId(projectCategory.getId());
    badgeCategory.setItemName("ProjectBadgeSQLTest"+System.currentTimeMillis());
    badgeCategory.insert(db);
    // Insert a project
    Project project = new Project();
    project.setCategoryId(projectCategory.getId());
    project.setTitle("ProjectBadgeSQLTest "+System.currentTimeMillis());
    project.setShortDescription("description");
    project.setRequestDate(new java.sql.Timestamp(System.currentTimeMillis()));
    project.setEnteredBy(USER_ID);
    project.setModifiedBy(USER_ID);
    project.insert(db);
    projectId = project.getId();
    // Insert a badge
    Badge badge = new Badge();
    badge.setCategoryId(badgeCategory.getId());
    badge.setTitle("ProjectBadgeSQL Test "+System.currentTimeMillis());
    badge.setDescription("Test Description");
    badge.setEnabled(true);
    badge.setEnteredBy(USER_ID);
    badge.setModifiedBy(USER_ID);
    badge.insert(db);
    badgeId = badge.getId();
    // Insert badge project link
    ProjectBadge projectBadge = new ProjectBadge();
    projectBadge.setBadgeId(badgeId);
    projectBadge.setProjectId(projectId);
    assertNotNull(projectBadge);
    boolean result = projectBadge.insert(db);
    assertTrue("Badge was not inserted", result);
    assertTrue("Inserted badge did not have an id", projectBadge.getId() > -1);

    // Find the previously set badge project link
    int projectBadgeId = projectBadge.getId();
    projectBadge = null;
    ProjectBadgeList badgeList = new ProjectBadgeList();
    badgeList.setProjectId(projectId);
    badgeList.buildList(db);
    assertTrue(badgeList.size() > 0);
    for(ProjectBadge tmpBadge : badgeList) {
      if (tmpBadge.getId() == projectBadgeId) {
        projectBadge = tmpBadge;
        break;
      }
    }
    assertNotNull(projectBadge);

    // Delete the badge project link
    projectBadge.delete(db);
    projectBadgeId = projectBadge.getId();
    projectBadge = null;

    // Try to find the previously deleted badge project link
    badgeList = new ProjectBadgeList();
    badgeList.setProjectId(projectId);
    badgeList.buildList(db);
    for(ProjectBadge tmpBadge : badgeList) {
      if (tmpBadge.getId() == projectBadgeId) {
        assertNull("Badge Project link exists when it shouldn't", tmpBadge);
      }
    }
    //clean up the badge and project created
    String basePath = null;
    assertTrue("Project was not deleted", project.delete(db, basePath));
    assertTrue("Badge was not deleted", badge.delete(db, basePath));
    assertTrue("BadgeCategory was not deleted", badgeCategory.delete(db, basePath));
    assertTrue("ProjectCategory was not deleted", projectCategory.delete(db, basePath));

  }

}