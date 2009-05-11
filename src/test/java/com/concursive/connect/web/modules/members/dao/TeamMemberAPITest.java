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

import com.concursive.commons.api.AbstractAPITest;
import com.concursive.commons.api.DataRecord;
import com.concursive.connect.web.modules.members.dao.TeamMemberList;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.dao.ProjectList;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

/**
 * Tests common project database access
 *
 * @author matt
 * @created January 24, 2008
 */
public class TeamMemberAPITest extends AbstractAPITest {

  public void testInsertTeamMember() throws Exception {

    {
      ArrayList<String> meta = new ArrayList<String>();
      meta.add("id");
      api.setTransactionMeta(meta);

      // The minimum number of fields for a successful project insert
      DataRecord record = new DataRecord();
      record.setName("project");
      record.setAction(DataRecord.INSERT);
      record.setShareKey(true);
      record.addField("title", "API Test Project");
      record.addField("shortDescription", "API Test Project short description");
      record.addField("requestDate", new Date());
      record.addField("enteredBy", USER_ID);
      record.addField("modifiedBy", USER_ID);
      record.addField("groupId", GROUP_ID);
      api.save(record);
    }

    {
      DataRecord record = new DataRecord();
      record.setName("teamMember");
      record.setAction(DataRecord.INSERT);
      record.addField("projectId", "$C{project.id}");
      record.addField("userId", 1);
      record.addField("userLevel", 1);
      record.addField("enteredBy", 1);
      record.addField("modifiedBy", 1);
      api.save(record);
    }

    // Process the complete transaction
    processTheTransactions(api, packetContext);
    assertFalse("API reported a transaction error: " + api.getLastResponse(), api.hasError());
    int projectId = Integer.parseInt(api.getResponseValue("id"));
    assertTrue(projectId > -1);

    // Look and see if the team member exists...
    {
      //Add Meta Info with fields required
      ArrayList<String> meta = new ArrayList<String>();
      meta.add("id");
      meta.add("projectId");
      meta.add("userId");
      meta.add("lastAccessed");
      api.setTransactionMeta(meta);

      // Ask the API to send the just inserted project back
      DataRecord record = new DataRecord();
      record.setName("teamMemberList");
      record.setAction(DataRecord.SELECT);
      record.addField("projectId", projectId);
      record.addField("userId", "1");
      api.save(record);
    }

    // Process the complete transaction
    processTheTransactions(api, packetContext);
    assertFalse("API reported a transaction error: " + api.getLastResponse(), api.hasError());
    assertNull(api.getResponseValue("lastAccessed"));
    int teamMemberId = Integer.parseInt(api.getResponseValue("id"));
    assertTrue(teamMemberId > -1);

    // Delete this test project by querying the database directly
    // The object will delete referenced data
    int count = 0;
    ProjectList projectList = new ProjectList();
    projectList.setEnteredByUser(USER_ID);
    projectList.buildList(db);
    Iterator i = projectList.iterator();
    while (i.hasNext()) {
      Project thisProject = (Project) i.next();
      if (thisProject.getTitle().equals("API Test Project")) {
        ++count;
        thisProject.delete(db, null);
      }
    }
    assertTrue("Didn't find a project to delete", count > 0);
  }

  public void testDeleteTeamMember() throws Exception {
    //Create a project
    String title = "Test Delete Team Member";
    {
      ArrayList<String> meta = new ArrayList<String>();
      meta.add("id");
      api.setTransactionMeta(meta);

      // The minimum number of fields for a successful project insert
      DataRecord record = new DataRecord();
      record.setName("project");
      record.setAction(DataRecord.INSERT);
      record.setShareKey(true);
      record.addField("title", title);
      record.addField("shortDescription", "API Test Project short description");
      record.addField("requestDate", new Date());
      record.addField("enteredBy", USER_ID);
      record.addField("modifiedBy", USER_ID);
      record.addField("groupId", GROUP_ID);
      api.save(record);
    }

    //Inset a team member

    {
      DataRecord record = new DataRecord();
      record.setName("teamMember");
      record.setAction(DataRecord.INSERT);
      record.addField("projectId", "$C{project.id}");
      record.addField("userId", 1);
      record.addField("userLevel", 1);
      record.addField("enteredBy", 1);
      record.addField("modifiedBy", 1);
      api.save(record);
    }

    // Process the complete transaction
    processTheTransactions(api, packetContext);
    assertFalse("API reported a transaction error: " + api.getLastResponse(), api.hasError());
    int projectId = Integer.parseInt(api.getResponseValue("id"));
    assertTrue(projectId > -1);

    // Look and see if the team member exists...
    {
      //Add Meta Info with fields required
      ArrayList<String> meta = new ArrayList<String>();
      meta.add("id");
      meta.add("projectId");
      meta.add("userId");
      api.setTransactionMeta(meta);

      // Ask the API to send the just inserted project back
      DataRecord record = new DataRecord();
      record.setName("teamMemberList");
      record.setAction(DataRecord.SELECT);
      record.addField("projectId", projectId);
      record.addField("userId", "1");
      api.save(record);
    }

    // Process the complete transaction
    processTheTransactions(api, packetContext);
    assertFalse("API reported a transaction error: " + api.getLastResponse(), api.hasError());
    int teamMemberId = Integer.parseInt(api.getResponseValue("id"));
    assertTrue(teamMemberId > -1);

    DataRecord record = new DataRecord();
    record.setName("teamMember");
    record.setAction(DataRecord.DELETE);
    record.addField("id", teamMemberId);
    api.save(record);
    processTheTransactions(api, packetContext);
    assertFalse("API reported a transaction error: " + api.getLastResponse(), api.hasError());

    // Check and make sure the team member got deleted
    TeamMemberList team = new TeamMemberList();
    team.setProjectId(projectId);
    team.buildList(db);
    assert (team.size() == 0);

    // Delete this test project by querying the database directly
    // The object will delete referenced data
    int count = 0;
    ProjectList projectList = new ProjectList();
    projectList.setEnteredByUser(USER_ID);
    projectList.buildList(db);
    Iterator i = projectList.iterator();
    while (i.hasNext()) {
      Project thisProject = (Project) i.next();
      if (thisProject.getTitle().equals(title)) {
        ++count;
        thisProject.delete(db, null);
      }
    }

    assertTrue("Didn't find a project to delete", count > 0);
  }
}
