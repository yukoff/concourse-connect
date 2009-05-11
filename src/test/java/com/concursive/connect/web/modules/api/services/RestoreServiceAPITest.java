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

package com.concursive.connect.web.modules.api.services;

import com.concursive.commons.api.AbstractAPITest;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.commons.api.DataRecord;
import com.concursive.commons.api.APIRestore;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;

/**
 * Tests restoring data using the API
 *
 * @author matt rajkowski
 * @created March 6, 2008
 */
public class RestoreServiceAPITest extends AbstractAPITest {


  public void testRestoreProjectOverwriteAll() throws Exception {
    int projectId = -1;
    {
      //Add Meta Info with fields required
      ArrayList<String> meta = new ArrayList<String>();
      meta.add("id");
      meta.add("title");
      api.setTransactionMeta(meta);

      // The minimum number of fields for a successful project insert
      DataRecord record = new DataRecord();
      record.setName("project");
      record.setAction(DataRecord.INSERT);
      record.addField("title", "Backup and Restore Test");
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
      projectId = api.getResponseValueAsInt("id");
      assertTrue("Did not retreive a valid projectId: " + projectId, projectId > -1);
    }

    {
      // Backup to a file
      packetContext.setOutputStream(new FileOutputStream("backup.xml"));

      // Backup the project to a file
      DataRecord record = new DataRecord();
      record.setName("projectList");
      record.setAction("backup");
      record.addField("projectId", projectId);

      api.setAutoGenerateMeta(true);
      api.save(record);
      processTheTransactions(api, packetContext);
      assertFalse("testRestoreProject-> backup API reported a transaction error: " + api.getLastResponse(), api.hasError());
    }

    packetContext.setOutputStream(null);

    {
      // Restore the state of the objects as-supplied, start by deleting the record and dependents
      ArrayList<String> meta = new ArrayList<String>();
      meta.add("mode=overwrite");
      api.setAutoGenerateMeta(false);
      api.setTransactionMeta(meta);

      APIRestore.restoreAll(api, new File("backup.xml"));
      processTheTransactions(api, packetContext);
      assertFalse("API reported a transaction error: " + api.getLastResponse(), api.hasError());
    }

    Project project = new Project(db, projectId);
    project.delete(db, null);

    {
      // Restore the state of the objects as-supplied, start by deleting the record and dependents
      ArrayList<String> meta = new ArrayList<String>();
      meta.add("mode=overwrite");
      api.setTransactionMeta(meta);

      APIRestore.restore(api, new File("backup.xml"), "project", projectId);
      processTheTransactions(api, packetContext);
      assertFalse("API reported a transaction error: " + api.getLastResponse(), api.hasError());
    }

    project = new Project(db, projectId);
    project.delete(db, null);
  }
}