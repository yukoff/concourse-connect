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
package com.concursive.connect.web.modules.members.dao;

import com.concursive.commons.db.AbstractConnectionPoolTest;
import com.concursive.connect.cache.utils.CacheUtils;
import com.concursive.connect.web.modules.members.dao.TeamMember;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.utils.LookupList;

import java.sql.SQLException;
import java.sql.Timestamp;


/**
 * Tests common database access
 *
 * @author matt
 * @created July 17, 2008
 */
public class TeamMemberSQLTest extends AbstractConnectionPoolTest {

  protected static final int GROUP_ID = 1;
  protected static final int USER_ID = 1;

  public void testTeamMemberCRUD() throws SQLException {

    // Insert project
    Project project = new Project();
    project.setTitle("Team Member SQL Test");
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
    boolean projectResult = project.insert(db);
    assertTrue("Project was not inserted", projectResult);

    // Utilize the roles
    LookupList roleList = CacheUtils.getLookupList("lookup_project_role");

    // Add a team member
    TeamMember member = new TeamMember();
    member.setProjectId(project.getId());
    member.setUserId(USER_ID);
    member.setUserLevel(roleList.getIdFromLevel(TeamMember.PROJECT_ADMIN));
    member.setStatus(TeamMember.STATUS_ADDED);
    member.setEnteredBy(USER_ID);
    member.setModifiedBy(USER_ID);
    boolean memberResult = member.insert(db);
    assertTrue("Team member was not inserted", memberResult);

    // Update this project's members
    project.buildTeamMemberList(db);
    assertFalse("Team member list must not be 0", project.getTeam().size() == 0);

    // Test changing your own role
    TeamMember.changeRole(db, project, USER_ID, USER_ID, roleList.getIdFromLevel(TeamMember.VIP));

    // Delete the project
    project.delete(db, null);
  }

}
