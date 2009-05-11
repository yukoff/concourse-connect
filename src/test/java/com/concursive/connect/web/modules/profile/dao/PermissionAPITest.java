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

import com.concursive.commons.api.AbstractAPITest;
import com.concursive.commons.api.DataRecord;
import com.concursive.connect.web.modules.members.dao.TeamMember;
import com.concursive.connect.web.modules.profile.dao.Project;

import java.util.ArrayList;
import java.util.Date;


/**
 * Test PermissionList
 *
 * @author matt
 * @created February 22, 2008
 */
public class PermissionAPITest extends AbstractAPITest {

  public void testSelectAndUpdateProjectPermissions() throws Exception {

    {
      //Add Meta Info to get project id back
      ArrayList<String> meta = new ArrayList<String>();
      meta.add("id");
      api.setTransactionMeta(meta);

      // Insert a project, which also inserts permissions for a project
      DataRecord record = new DataRecord();
      record.setName("project");
      record.setAction(DataRecord.INSERT);
      record.addField("title", "PermissionAPITest Project");
      record.addField("shortDescription", "API Test Project short description");
      record.addField("requestDate", new Date());
      record.addField("enteredBy", USER_ID);
      record.addField("modifiedBy", USER_ID);
      record.addField("groupId", GROUP_ID);
      api.save(record);
      processTheTransactions(api, packetContext);
      assertFalse("API reported a transaction error: " + api.getLastResponse(), api.hasError());
    }
    int newProjectId = api.getResponseValueAsInt("id");
    assertTrue(newProjectId > -1);

    {
      // Get a list of permissions for the new project
      ArrayList<String> meta = new ArrayList<String>();
      meta.add("id");
      meta.add("projectId");
      meta.add("permissionId");
      meta.add("userLevel");
      meta.add("name");
      api.setTransactionMeta(meta);

      // Get just the permission for assigning tickets
      DataRecord record = new DataRecord();
      record.setName("projectPermissionList");
      record.setAction(DataRecord.SELECT);
      record.addField("projectId", newProjectId);
      record.addField("name", "project-tickets-assign");
      api.save(record);
      processTheTransactions(api, packetContext);
      assertFalse("API reported a transaction error: " + api.getLastResponse(), api.hasError());
      assertTrue("Retrieved too many permissions, should have been 1", api.getRecordCount() == 1);
    }
    int permissionIdToUpdate = api.getResponseValueAsInt("id");
    int currentUserLevel = api.getResponseValueAsInt("userLevel");
    assertTrue(currentUserLevel > -1);
    assertTrue(permissionIdToUpdate > -1);

    {
      // Get the id of a "Guest"
      ArrayList<String> meta = new ArrayList<String>();
      meta.add("id");
      meta.add("description");
      meta.add("level");
      api.setTransactionMeta(meta);

      DataRecord record = new DataRecord();
      record.setName("lookupRoleList");
      record.setAction(DataRecord.SELECT);
      // NOTE: This filter does not currently work, so all records are returned
      api.save(record);
      processTheTransactions(api, packetContext);
    }

    int guestCodeLevel = -1;
    for (DataRecord thisRecord : api.getRecords()) {
      if (thisRecord.getValueAsInt("level") == TeamMember.GUEST) {
        guestCodeLevel = thisRecord.getValueAsInt("id");
      }
    }
    assertTrue(guestCodeLevel > -1);

    {
      // Update the permission to "Guest"
      DataRecord record = new DataRecord();
      record.setName("projectPermission");
      record.setAction(DataRecord.UPDATE);
      record.addField("id", permissionIdToUpdate);
      record.addField("userLevel", guestCodeLevel);
      api.save(record);
      processTheTransactions(api, packetContext);
      assertFalse("API reported a transaction error: " + api.getLastResponse(), api.hasError());
    }

    {
      // Get the updated permission
      ArrayList<String> meta = new ArrayList<String>();
      meta.add("id");
      meta.add("projectId");
      meta.add("permissionId");
      meta.add("userLevel");
      meta.add("name");
      api.setTransactionMeta(meta);

      // Get the permission for assigning tickets
      DataRecord record = new DataRecord();
      record.setName("projectPermissionList");
      record.setAction(DataRecord.SELECT);
      record.addField("id", permissionIdToUpdate);
      record.addField("name", "project-tickets-assign");
      api.save(record);
      processTheTransactions(api, packetContext);
      assertFalse("API reported a transaction error: " + api.getLastResponse(), api.hasError());
      assertTrue("Retrieved too many permissions, should have been 1", api.getRecordCount() == 1);
    }
    int newUserLevel = api.getResponseValueAsInt("userLevel");

    assertTrue(currentUserLevel != newUserLevel && currentUserLevel > -1 && newUserLevel > -1);

    // Cleanup everything by deleting the project
    Project thisProject = new Project(db, newProjectId);
    assertTrue(thisProject.delete(db, null));

  }
}
