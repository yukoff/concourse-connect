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
import com.concursive.connect.web.modules.profile.dao.ProjectList;

import java.util.ArrayList;
import java.util.Date;

/**
 * Tests projectlist paging using api
 *
 * @author matt rajkowski
 * @created October 23, 2008
 */
public class ProjectListAPITest extends AbstractAPITest {

  public void testSelectProjectWithPaging() throws Exception {

    // Test inserting and retrieving a couple pages worth of data
    int pages = 3;
    int pageSize = 10;
    int insertCount = (pages * pageSize);

    // Unique project characteristics for testing against
    String projectTitle = "ProjectListAPITest Test Project insert " + insertCount + " records and select using paging";
    String keywords = "Tracker:" + System.currentTimeMillis();

    // Insert lots of projects for testing the paging
    {
      // Define the project
      DataRecord record = new DataRecord();
      record.setName("project");
      record.setAction(DataRecord.INSERT);
      record.setShareKey(true);
      record.addField("title", projectTitle);
      record.addField("shortDescription", "API Test Project short description");
      record.addField("keywords", keywords);
      record.addField("requestDate", new Date());
      record.addField("enteredBy", USER_ID);
      record.addField("modifiedBy", USER_ID);
      record.addField("groupId", GROUP_ID);
      // Insert it several times
      for (int insertTracker = 0; insertTracker < insertCount; insertTracker++) {
        api.save(record);
        processTheTransactions(api, packetContext);
        assertFalse("API reported a transaction error: " + api.getLastResponse(), api.hasError());
      }
    }

    int recordCountProcessed = 0;
    int currentOffset = 0;
    for (int pageCount = 0; pageCount < pages; pageCount++) {

      // Start at offset 0 and add the number per page
      currentOffset = (pageCount * pageSize);

      // Retrieve records by paging through the resultset
      {
        // Add Meta Info with fields required
        ArrayList<String> meta = new ArrayList<String>();
        meta.add("id");
        meta.add("title");
        meta.add("keywords");
        api.setTransactionMeta(meta);

        System.out.println("Checking offset: " + currentOffset);

        DataRecord record = new DataRecord();
        record.setName("projectList");
        record.setAction(DataRecord.SELECT);
        record.addField("keywords", keywords);
        // NOTE: Sort takes the actual database fields (instead of bean properties) comma separated
        record.setSort("project_id");
        record.setOffset(currentOffset);
        record.setItems(pageSize);
        api.save(record);

        processTheTransactions(api, packetContext);
        assertFalse("API reported a transaction error: " + api.getLastResponse(), api.hasError());
        assertTrue("Couldn't find inserted projects - " + api.getRecordCount(), api.getRecordCount() == record.getItems());
      }

      // Demonstrate using the raw DataRecord objects when the Project class is unavailable in the classpath
      ArrayList<DataRecord> recordList = api.getRecords();
      assertTrue(recordList.size() == pageSize);
      for (DataRecord thisRecord : recordList) {
        assertTrue("ProjectId is -1", thisRecord.getValueAsInt("id") > -1);
        assertTrue(projectTitle.equals(thisRecord.getValue("title")));
        assertTrue(keywords.equals(thisRecord.getValue("keywords")));
      }

      // Demonstrate converting the recordset to Project objects
      ArrayList<Object> projectObjects = api.getRecords("com.concursive.connect.web.modules.profile.dao.Project");
      assertTrue(projectObjects.size() == pageSize);
      for (Object projectObject : projectObjects) {
        Project partialProject = (Project) projectObject;
        assertNotNull(partialProject);
        assertTrue("ProjectId is -1", partialProject.getId() > -1);
        assertTrue(projectTitle.equals(partialProject.getTitle()));
        ++recordCountProcessed;
      }
    }
    
    // Cleanup any matching projects
    ProjectList projects = new ProjectList();
    projects.setKeywords(keywords);
    projects.buildList(db);
    assertEquals(insertCount, projects.size());
    for (Project project : projects) {
      project.delete(db, null);
    }

    // Make sure all the records were seen that were inserted
    assertEquals(insertCount, recordCountProcessed);
  }

}