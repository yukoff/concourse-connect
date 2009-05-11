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
package com.concursive.connect.web.modules.wiki.dao;

import com.concursive.commons.api.AbstractAPITest;
import com.concursive.commons.api.DataRecord;
import com.concursive.connect.web.modules.wiki.dao.Wiki;
import com.concursive.connect.web.modules.profile.dao.ProjectList;
import com.concursive.connect.web.modules.profile.dao.Project;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Tests common project wiki database access
 *
 * @author wli
 * @created February 24, 2008
 */
public class WikiAPITest extends AbstractAPITest {

  public void testInsertProjectWiki() throws Exception {
    String title = "API Test Project";

    {
      //Add Meta Info with fields required
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
      record.addField("requestDate", "2008-01-24 00:00:00 -0400");
      record.addField("enteredBy", USER_ID);
      record.addField("modifiedBy", USER_ID);
      record.addField("groupId", GROUP_ID);
      api.save(record);
    }

    {
      // Test submitting a "Home" Wiki -- one without a subject
      DataRecord record = new DataRecord();
      record.setName("wiki");
      record.addField("projectId", "$C{project.id}");
      record.setAction(DataRecord.INSERT);
      record.addField("subject", "");
      record.addField("content", "API Test ProjectWiki Content");
      record.addField("enteredBy", USER_ID);
      record.addField("modifiedBy", USER_ID);
      api.save(record);
    }

    {
      // Test submitting another Wiki page
      DataRecord record = new DataRecord();
      record.setName("wiki");
      record.addField("projectId", "$C{project.id}");
      record.setAction(DataRecord.INSERT);
      record.addField("subject", "Project Wiki Test");
      record.addField("content", "API Test ProjectWiki Content");
      record.addField("enteredBy", USER_ID);
      record.addField("modifiedBy", USER_ID);
      api.save(record);
    }

    // Process the complete transaction
    processTheTransactions(api, packetContext);
    assertFalse("API reported a transaction error: " + api.getLastResponse(), api.hasError());
    assertTrue("An inserted record must get an action of 'processed' returned", api.getRecords().get(0).getAction().equals("processed"));
    assertTrue("An inserted record must get an action of 'processed' returned", api.getRecords().get(1).getAction().equals("processed"));
    assertTrue("An inserted record must get an action of 'processed' returned", api.getRecords().get(2).getAction().equals("processed"));
    int projectId = Integer.parseInt(api.getResponseValue("id"));
    assertTrue(projectId > -1);

    // Look and see if the Wiki exists...
    {
      //Add Meta Info with fields required
      ArrayList<String> meta = new ArrayList<String>();
      meta.add("id");
      meta.add("projectId");
      meta.add("subject");
      api.setTransactionMeta(meta);

      // Ask the API to send the just inserted project wiki back
      DataRecord record = new DataRecord();
      record.setName("wikiList");
      record.setAction(DataRecord.SELECT);
      record.addField("projectId", projectId);
      api.save(record);
    }

    processTheTransactions(api, packetContext);
    assertFalse("API reported a transaction error: " + api.getLastResponse(), api.hasError());
    assertTrue(api.getRecordCount() == 2);
    assertTrue(api.getRecords().get(0).getValueAsInt("projectId") == projectId);
    assertTrue(api.getRecords().get(0).getValue("subject").equals(""));
    assertTrue(api.getRecords().get(1).getValue("subject").equals("Project Wiki Test"));

    // This will target the first returned record
    int wikiId = Integer.parseInt(api.getResponseValue("id"));
    assertTrue(wikiId > -1);

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

  public void testUpdateProjectWiki() throws Exception {
    String title = "Test Update Wiki";
    //Create a project
    {
      //Add Meta Info with fields required
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
      record.addField("requestDate", "2008-01-24 00:00:00 -0400");
      record.addField("enteredBy", USER_ID);
      record.addField("modifiedBy", USER_ID);
      record.addField("groupId", GROUP_ID);
      api.save(record);
    }

    // Add Wikis for that Project

    {
      // Test submitting a "Home" Wiki -- one without a subject
      DataRecord record = new DataRecord();
      record.setName("wiki");
      record.addField("projectId", "$C{project.id}");
      record.setAction(DataRecord.INSERT);
      record.addField("subject", "");
      record.addField("content", "API Test ProjectWiki Content");
      record.addField("enteredBy", USER_ID);
      record.addField("modifiedBy", USER_ID);
      api.save(record);
    }

    {
      // Test submitting another Wiki page
      DataRecord record = new DataRecord();
      record.setName("wiki");
      record.addField("projectId", "$C{project.id}");
      record.setAction(DataRecord.INSERT);
      record.addField("subject", "Project Wiki Test");
      record.addField("content", "API Test ProjectWiki Content");
      record.addField("enteredBy", USER_ID);
      record.addField("modifiedBy", USER_ID);
      api.save(record);
    }

    // Process the complete transaction
    processTheTransactions(api, packetContext);
    assertFalse("API reported a transaction error: " + api.getLastResponse(), api.hasError());
    assertTrue(api.getRecordCount() == 3);
    int projectId = Integer.parseInt(api.getResponseValue("id"));
    assertTrue(projectId > -1);

    // Look and see if the Wiki exists...
    {
      //Add Meta Info with fields required
      ArrayList<String> meta = new ArrayList<String>();
      meta.add("id");
      meta.add("projectId");
      meta.add("subject");
      api.setTransactionMeta(meta);

      // Ask the API to send the just inserted project wiki back
      DataRecord record = new DataRecord();
      record.setName("wikiList");
      record.setAction(DataRecord.SELECT);
      record.addField("projectId", projectId);
      api.save(record);
    }

    processTheTransactions(api, packetContext);
    assertFalse("API reported a transaction error: " + api.getLastResponse(), api.hasError());
    assertTrue(api.getRecordCount() == 2);
    assertTrue(api.getRecords().get(0).getValue("subject").equals(""));
    assertTrue(api.getRecords().get(1).getValue("subject").equals("Project Wiki Test"));

    int wikiId = Integer.parseInt(api.getResponseValue("id"));
    assertTrue(wikiId > -1);

    String updatedWikiContent = "Updated API Test ProjectWiki Content";
    //Update this wiki with returned wikiId
    {
      // Test submitting another Wiki page
      DataRecord record = new DataRecord();
      record.setName("wiki");
      record.setAction(DataRecord.UPDATE);
      record.addField("id", wikiId);
      record.addField("subject", "Project Wiki Test");
      record.addField("content", updatedWikiContent);
      record.addField("enteredBy", USER_ID);
      record.addField("modifiedBy", USER_ID);
      api.save(record);
    }

    // Process the complete transaction
    processTheTransactions(api, packetContext);
    assertFalse("API reported a transaction error: " + api.getLastResponse(), api.hasError());
    assertTrue(api.getRecordCount() == 1);
    assertTrue("An updated record must get an action of 'processed' returned", api.getRecords().get(0).getAction().equals("processed"));

    // Verify the wiki got updated
    Wiki wiki = new Wiki(db, wikiId);
    assertTrue(wiki.getContent().equals(updatedWikiContent));

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

  public void testDeleteProjectWiki() throws Exception {
    String title = "Test Delete Wiki";
    //Create a project
    {
      //Add Meta Info with fields required
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
      record.addField("requestDate", "2008-01-24 00:00:00 -0400");
      record.addField("enteredBy", USER_ID);
      record.addField("modifiedBy", USER_ID);
      record.addField("groupId", GROUP_ID);
      api.save(record);
    }

    // Add Wikis for that Project

    {
      // Test submitting a "Home" Wiki -- one without a subject
      DataRecord record = new DataRecord();
      record.setName("wiki");
      record.addField("projectId", "$C{project.id}");
      record.setAction(DataRecord.INSERT);
      record.addField("subject", "");
      record.addField("content", "API Test ProjectWiki Content");
      record.addField("enteredBy", USER_ID);
      record.addField("modifiedBy", USER_ID);
      api.save(record);
    }

    {
      // Test submitting another Wiki page
      DataRecord record = new DataRecord();
      record.setName("wiki");
      record.addField("projectId", "$C{project.id}");
      record.setAction(DataRecord.INSERT);
      record.addField("subject", "Project Wiki Test");
      record.addField("content", "API Test ProjectWiki Content");
      record.addField("enteredBy", USER_ID);
      record.addField("modifiedBy", USER_ID);
      api.save(record);
    }

    // Process the complete transaction
    processTheTransactions(api, packetContext);
    assertFalse("API reported a transaction error: " + api.getLastResponse(), api.hasError());
    int projectId = Integer.parseInt(api.getResponseValue("id"));
    assertTrue(projectId > -1);

    // Look and see if the Wiki exists...
    {
      //Add Meta Info with fields required
      ArrayList<String> meta = new ArrayList<String>();
      meta.add("id");
      meta.add("projectId");
      meta.add("subject");
      api.setTransactionMeta(meta);

      // Ask the API to send the just inserted project wiki back
      DataRecord record = new DataRecord();
      record.setName("wikiList");
      record.setAction(DataRecord.SELECT);
      record.addField("projectId", projectId);
      api.save(record);
    }

    processTheTransactions(api, packetContext);
    assertFalse("API reported a transaction error: " + api.getLastResponse(), api.hasError());
    assertTrue(api.getRecordCount() == 2);

    int wikiId1 = api.getRecords().get(0).getValueAsInt("id");
    assertTrue(wikiId1 > -1);
    assertTrue(api.getRecords().get(0).getValue("subject").equals(""));

    int wikiId2 = api.getRecords().get(1).getValueAsInt("id");
    assertTrue(wikiId2 > -1);
    assertTrue(api.getRecords().get(1).getValue("subject").equals("Project Wiki Test"));
    assertTrue(wikiId1 != wikiId2);

    //Delete the wikis with returned wikiId
    {
      // Test submitting another Wiki page
      DataRecord record = new DataRecord();
      record.setName("wiki");
      record.setAction(DataRecord.DELETE);
      record.addField("id", wikiId1);
      api.save(record);
    }
    {
      // Test submitting another Wiki page
      DataRecord record = new DataRecord();
      record.setName("wiki");
      record.setAction(DataRecord.DELETE);
      record.addField("id", wikiId2);
      api.save(record);
    }

    processTheTransactions(api, packetContext);
    assertFalse("API reported a transaction error: " + api.getLastResponse(), api.hasError());
    assertTrue(api.getRecordCount() == 2);
    assertTrue("A deleted record must get an action of 'delete' returned", api.getRecords().get(0).getAction().equals(DataRecord.DELETE));
    assertTrue("A deleted record must get an action of 'delete' returned", api.getRecords().get(1).getAction().equals(DataRecord.DELETE));

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
