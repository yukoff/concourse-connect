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
import com.concursive.connect.web.modules.profile.dao.Project;

import java.util.ArrayList;
import java.util.Date;

/**
 * Tests common project database access
 *
 * @author matt
 * @created January 24, 2008
 */
public class ProjectAPITest extends AbstractAPITest {

  public void testInsertProject() throws Exception {

    String title = "API Test Project";

    //Add Meta Info with fields required
    ArrayList<String> meta = new ArrayList<String>();
    meta.add("id");
    meta.add("title");
    api.setTransactionMeta(meta);

    // The minimum number of fields for a successful project insert
    DataRecord record = new DataRecord();
    record.setName("project");
    record.setAction(DataRecord.INSERT);
    record.addField("title", title);
    record.addField("shortDescription", "API Test Project short description");
    record.addField("requestDate", new Date());
    record.addField("enteredBy", USER_ID);
    record.addField("modifiedBy", USER_ID);
    record.addField("groupId", GROUP_ID);
    // Other fields
    record.addField("requestedBy", "Project Manager");
    record.addField("showCalendar", "true");
    record.addField("showTickets", "false");
    record.addField("showDiscussion", "true");
    record.addField("showPlan", "true");
    record.addField("showLists", "true");
    record.addField("showDocuments", "true");
    api.save(record);

    // Process the complete transaction
    processTheTransactions(api, packetContext);
    assertFalse("API reported a transaction error: " + api.getLastResponse(), api.hasError());

    // When supplying meta, the API will return the record inserted
    int newProjectId = Integer.parseInt(api.getResponseValue("id"));
    assertTrue(newProjectId > 0);

    // Record-level things can be done too
    assertTrue(api.getRecordCount() == 1);

    ArrayList<DataRecord> records = api.getRecords();
    for (DataRecord thisRecord : records) {
      assertTrue(Integer.parseInt(thisRecord.getValue("id")) == newProjectId);
      assertTrue(title.equals(thisRecord.getValue("title")));
    }

    Project thisProject = new Project(db, newProjectId);
    assertTrue(thisProject.getFeatures().getShowCalendar());
    assertFalse(thisProject.getFeatures().getShowTickets());
    assertTrue(thisProject.getFeatures().getShowDiscussion());
    assertTrue(thisProject.getFeatures().getShowPlan());
    assertTrue(thisProject.getFeatures().getShowLists());
    assertTrue(thisProject.getFeatures().getShowDocuments());
    assertTrue(thisProject.delete(db, null));
  }

  public void testInsertAndSelectProject() throws Exception {

    String projectTitle = "API Test Project insert and select short description";

    //Add Meta Info with fields required
    ArrayList<String> meta = new ArrayList<String>();
    meta.add("id");
    meta.add("title");
    api.setTransactionMeta(meta);

    // Prepare a batch transaction
    {
      // The minimum number of fields for a successful project insert
      DataRecord record = new DataRecord();
      record.setName("project");
      record.setAction(DataRecord.INSERT);
      record.setShareKey(true);
      record.addField("title", projectTitle);
      record.addField("shortDescription", "API Test Project short description");
      record.addField("requestDate", new Date());
      record.addField("enteredBy", USER_ID);
      record.addField("modifiedBy", USER_ID);
      record.addField("groupId", GROUP_ID);
      api.save(record);
    }

    {
      // Ask the API to send the just inserted project back
      DataRecord record = new DataRecord();
      record.setName("projectList");
      record.setAction(DataRecord.SELECT);
      record.addField("projectId", "$C{project.id}");
      api.save(record);
    }

    // Process the complete transaction
    processTheTransactions(api, packetContext);
    assertFalse("API reported a transaction error: " + api.getLastResponse(), api.hasError());
    assertTrue("Couldn't find inserted project", api.getRecordCount() == 1);

    // In the meta section, only the id and title were requested
    Project partialProject = null;
    ArrayList<Object> projectObjects = api.getRecords("com.concursive.connect.web.modules.profile.dao.Project");
    assertTrue(projectObjects.size() == 1);
    for (Object projectObject : projectObjects) {
      partialProject = (Project) projectObject;
      assertTrue(partialProject.getId() > -1);
      assertTrue(projectTitle.equals(partialProject.getTitle()));
    }

    assertNotNull(partialProject);

    Project thisProject = new Project(db, partialProject.getId());
    assertTrue(thisProject.delete(db, null));
  }

  public void testDeleteProject() throws Exception {

    String projectTitle = "API Test Project to delete";

    //Add Meta Info with fields required
    ArrayList<String> meta = new ArrayList<String>();
    meta.add("id");
    meta.add("title");
    meta.add("owner");
    api.setTransactionMeta(meta);

    // Prepare a batch transaction
    {
      // The minimum number of fields for a successful project insert
      DataRecord record = new DataRecord();
      record.setName("project");
      record.setAction(DataRecord.INSERT);
      record.setShareKey(true);
      record.addField("title", projectTitle);
      record.addField("shortDescription", "API Test Project short description");
      record.addField("requestDate", new Date());
      record.addField("enteredBy", USER_ID);
      record.addField("modifiedBy", USER_ID);
      record.addField("groupId", GROUP_ID);
      record.addField("owner", USER_ID);
      api.save(record);
    }

    {
      // Ask the API to send the just inserted project back
      DataRecord record = new DataRecord();
      record.setName("projectList");
      record.setAction(DataRecord.SELECT);
      record.addField("projectId", "$C{project.id}");
      api.save(record);
    }

    // Process the complete transaction
    processTheTransactions(api, packetContext);
    assertFalse("API reported a transaction error: " + api.getLastResponse(), api.hasError());
    assertTrue("Couldn't find inserted project", api.getRecordCount() == 1);

    // In the meta section, some items were requested
    Project partialProject = null;
    ArrayList<Object> projectObjects = api.getRecords("com.concursive.connect.web.modules.profile.dao.Project");
    assertTrue(projectObjects.size() == 1);
    for (Object projectObject : projectObjects) {
      partialProject = (Project) projectObject;
      assertTrue(partialProject.getId() > -1);
      assertTrue(projectTitle.equals(partialProject.getTitle()));
      assertTrue(partialProject.getOwner() == USER_ID);
    }

    assertNotNull(partialProject);

    // Construct a delete request
    DataRecord record = new DataRecord();
    record.setName("project");
    record.setAction(DataRecord.DELETE);
    record.addField("id", partialProject.getId());
    api.save(record);

    processTheTransactions(api, packetContext);
    assertFalse("API reported a transaction error: " + api.getLastResponse(), api.hasError());

    Project thisProject = new Project(db, partialProject.getId());
    assertTrue("Shouldn't have found the deleted project", thisProject.getId() == -1);

  }

  public void testUpdateProject() throws Exception {

    String title = "API Test Project";

    //Add Meta Info with fields required
    ArrayList<String> meta = new ArrayList<String>();
    meta.add("id");
    meta.add("title");
    api.setTransactionMeta(meta);

    {
      // The minimum number of fields for a successful project insert
      DataRecord record = new DataRecord();
      record.setName("project");
      record.setAction(DataRecord.INSERT);
      record.addField("title", title);
      record.addField("shortDescription", "API Test Project short description");
      record.addField("requestDate", new Date());
      record.addField("enteredBy", USER_ID);
      record.addField("modifiedBy", USER_ID);
      record.addField("groupId", GROUP_ID);
      // Other fields
      record.addField("requestedBy", "Project Manager");
      record.addField("showCalendar", "true");
      record.addField("showTickets", "false");
      record.addField("showDiscussion", "true");
      record.addField("showPlan", "true");
      record.addField("showLists", "true");
      record.addField("showDocuments", "true");
      api.save(record);
    }

    // Process the complete transaction
    processTheTransactions(api, packetContext);
    assertFalse("API reported a transaction error: " + api.getLastResponse(), api.hasError());

    // When supplying meta, the API will return the record inserted
    int newProjectId = Integer.parseInt(api.getResponseValue("id"));
    assertTrue(newProjectId > 0);

    // Record-level things can be done too
    assertTrue(api.getRecordCount() == 1);

    ArrayList<DataRecord> records = api.getRecords();
    for (DataRecord thisRecord : records) {
      assertTrue(Integer.parseInt(thisRecord.getValue("id")) == newProjectId);
      assertTrue(title.equals(thisRecord.getValue("title")));
    }

    {
      // Update the base data of a project
      DataRecord record = new DataRecord();
      record.setName("project");
      record.setAction(DataRecord.UPDATE);
      record.addField("title", title);
      record.addField("id", newProjectId);
      record.addField("shortDescription", "Updated API Test Project short description");
      record.addField("requestDate", new Date());
      record.addField("enteredBy", USER_ID);
      record.addField("modifiedBy", USER_ID);
      record.addField("groupId", GROUP_ID);
      record.addField("requestedBy", "Project Manager");
      api.save(record);
    }

    processTheTransactions(api, packetContext);
    assertFalse("API reported a transaction error: " + api.getLastResponse(), api.hasError());

    Project thisProject = new Project(db, newProjectId);
    assertTrue(thisProject.delete(db, null));
  }
}
