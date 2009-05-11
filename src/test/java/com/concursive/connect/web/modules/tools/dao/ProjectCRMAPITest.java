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
package com.concursive.connect.web.modules.tools.dao;

import com.concursive.commons.api.AbstractAPITest;
import com.concursive.commons.api.DataRecord;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.tools.dao.ProjectCRM;

import java.util.ArrayList;
import java.util.Date;

/**
 * Tests common project database access
 *
 * @author Kailash Bhoopalam
 * @created June 18, 2008
 */
public class ProjectCRMAPITest extends AbstractAPITest {

  public void testUpdateProjectCRMConnectionAttributes() throws Exception {

  	String title = "API Test Project";
    int newProjectId = -1;

    {
      ArrayList<String> meta = new ArrayList<String>();
      meta.add("id");
      api.setTransactionMeta(meta);

      // Insert a new project (features are set when the project is inserted)
      DataRecord record = new DataRecord();
      record.setName("project");
      record.setAction(DataRecord.INSERT);
      record.addField("title", title);
      record.addField("shortDescription", "API Test Project CRM Connection information");
      record.addField("requestDate", new Date());
      record.addField("enteredBy", USER_ID);
      record.addField("modifiedBy", USER_ID);
      record.addField("groupId", GROUP_ID);

      api.save(record);
      processTheTransactions(api, packetContext);
      assertFalse("API reported a transaction error: " + api.getLastResponse(), api.hasError());
      newProjectId = Integer.parseInt(api.getResponseValue("id"));
      assertTrue(newProjectId > 0);

      ProjectCRM projectCRM = new ProjectCRM(db, newProjectId);
      assertNull(projectCRM.getConcursiveCRMUrl());
      assertNull(projectCRM.getConcursiveCRMClient());
      assertNull(projectCRM.getConcursiveCRMCode());
      assertNull(projectCRM.getConcursiveCRMDomain());
    }

    {
      DataRecord record = new DataRecord();
      record.setName("projectCRM");
      record.setAction(DataRecord.UPDATE);
      record.addField("id", newProjectId);
      record.addField("concursiveCRMUrl", "TEST");
      record.addField("concursiveCRMDomain", "TEST");
      record.addField("concursiveCRMCode", "TEST");
      record.addField("concursiveCRMClient", "TEST");
      record.addField("modifiedBy", USER_ID);
      api.save(record);
      processTheTransactions(api, packetContext);
      assertFalse("API reported a transaction error: " + api.getLastResponse(), api.hasError());

      ProjectCRM projectCRM = new ProjectCRM(db, newProjectId);
      assertTrue("TEST".equals(projectCRM.getConcursiveCRMUrl()));
      assertTrue("TEST".equals(projectCRM.getConcursiveCRMClient()));
      assertTrue("TEST".equals(projectCRM.getConcursiveCRMCode()));
      assertTrue("TEST".equals(projectCRM.getConcursiveCRMDomain()));
      
      Project project = new Project(db, newProjectId);
      project.delete(db, null);
    }
  }
}