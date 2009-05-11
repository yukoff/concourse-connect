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
package com.concursive.connect.web.modules.api;

import com.concursive.commons.api.AbstractAPITest;
import com.concursive.commons.api.DataRecord;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.services.dao.ServiceList;
import com.concursive.connect.web.utils.CustomLookupElement;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;

/**
 * Tests project service access
 *
 * @author matt rajkowski
 * @created September 8, 2008
 */
public class ServiceAPITest extends AbstractAPITest {

  public void testInsertProjectService() throws Exception {

    String title = "API Test Project Service";

    {
      // Add a project to add a service to
      ArrayList<String> meta = new ArrayList<String>();
      meta.add("id");
      meta.add("title");
      api.setTransactionMeta(meta);

      DataRecord record = new DataRecord();
      record.setName("project");
      record.setAction(DataRecord.INSERT);
      record.addField("title", title);
      record.addField("shortDescription", title + " short description");
      record.addField("requestDate", new Date());
      record.addField("enteredBy", USER_ID);
      record.addField("modifiedBy", USER_ID);
      record.addField("groupId", GROUP_ID);
      record.addField("requestedBy", "Project Manager");
      record.addField("showCalendar", "true");
      record.addField("showTickets", "false");
      record.addField("showDiscussion", "true");
      record.addField("showPlan", "true");
      record.addField("showLists", "true");
      record.addField("showDocuments", "true");
      api.save(record);
      processTheTransactions(api, packetContext);
      assertFalse("API reported a transaction error: " + api.getLastResponse(), api.hasError());
    }

    // Use the inserted project Id
    int newProjectId = Integer.parseInt(api.getResponseValue("id"));
    assertTrue(newProjectId > 0);

    // Insert a service to map to
    CustomLookupElement service = new CustomLookupElement();
    service.setTableName("lookup_service");
    service.setUniqueField("code");
    service.addField("description", "some service", Types.VARCHAR);
    service.addField("level", "10", Types.INTEGER);
    service.addField("enabled", "true", Types.BOOLEAN);
    service.insert(db);

    {
      // Map a project to a service using the API
      ArrayList<String> meta = new ArrayList<String>();
      meta.add("id");
      meta.add("description");
      api.setTransactionMeta(meta);

      DataRecord record = new DataRecord();
      record.setName("service");
      record.setAction(DataRecord.INSERT);
      record.addField("projectId", newProjectId);
      record.addField("serviceId", service.getId());
      api.save(record);
      processTheTransactions(api, packetContext);
      assertFalse("API reported a transaction error: " + api.getLastResponse(), api.hasError());
    }


    // Use the inserted mapping Id
    int serviceMapId = Integer.parseInt(api.getResponseValue("id"));
    assertTrue(serviceMapId > 0);

    {
      // Verify the existence of the service mapping
      ServiceList serviceList = new ServiceList();
      serviceList.setProjectId(newProjectId);
      serviceList.buildList(db);
      assertTrue(serviceList.size() == 1);
      int mappingId = serviceList.get(0).getId();
      assertTrue(serviceMapId == mappingId);
    }

    {
      // Find the mapping through the API
      ArrayList<String> meta = new ArrayList<String>();
      meta.add("id");
      meta.add("projectId");
      meta.add("serviceId");
      api.setTransactionMeta(meta);

      DataRecord record = new DataRecord();
      record.setName("serviceList");
      record.setAction(DataRecord.SELECT);
      record.addField("projectId", newProjectId);
      record.addField("serviceId", service.getId());
      api.save(record);
      processTheTransactions(api, packetContext);
      assertFalse("API reported a transaction error: " + api.getLastResponse(), api.hasError());
      assertTrue("Retrieved too many permissions, should have been 1", api.getRecordCount() == 1);
    }

    {
      // Delete the mapping through the API
      DataRecord record = new DataRecord();
      record.setName("service");
      record.setAction(DataRecord.DELETE);
      record.addField("id", serviceMapId);
      api.save(record);
      processTheTransactions(api, packetContext);
      assertFalse("API reported a transaction error: " + api.getLastResponse(), api.hasError());
    }

    {
      // Verify the the service mapping was deleted
      ServiceList serviceList = new ServiceList();
      serviceList.setProjectId(newProjectId);
      serviceList.buildList(db);
      assertTrue(serviceList.size() == 0);
    }

    // Cleanup
    Project thisProject = new Project(db, newProjectId);
    assertTrue(thisProject.delete(db, null));
    service.delete(db);
  }
}
