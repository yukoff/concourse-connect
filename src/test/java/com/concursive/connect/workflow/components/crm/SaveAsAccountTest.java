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
package com.concursive.connect.workflow.components.crm;

import com.concursive.connect.workflow.components.crm.AbstractToolsAPITest;
import com.concursive.commons.xml.XMLUtils;
import org.aspcfs.apps.transfer.DataRecord;
import org.aspcfs.utils.CRMConnection;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Tests protocol connectivity with tools
 *
 * @author matt rajkowski
 * @created September 25, 2008
 */
public class SaveAsAccountTest extends AbstractToolsAPITest {

  private static final String COOKIE_PROPERTY = "CRMConnection.cookie";
  private AtomicInteger counter = new AtomicInteger();
  private ConcurrentHashMap<String, String> properties = new ConcurrentHashMap<String, String>();

  public class TransactionTask implements Callable<CRMConnection> {

    private final CRMConnection crmConnection;

    public TransactionTask(CRMConnection thisConnection) {
      this.crmConnection = thisConnection;
    }

    public CRMConnection call() throws Exception {
      assertNotNull("CRMConnection must not be null", crmConnection);
      // NOTE: The Tools currently invalidate the session per request, so cookie fails
      //crmConnection.setCookie(properties.get(COOKIE_PROPERTY));
      XMLUtils xml = null;
      try {
        String xmlString = crmConnection.generateXMLPacket();
        assertNotNull(xmlString);
        xml = new XMLUtils(xmlString);
        assertNotNull(xml);
      } catch (Exception e) {
        fail(e.getMessage());
      }
      // Process the transaction using an HTTP Connection to a running instance
      int currentRunner = counter.addAndGet(1);
      System.out.println("SaveAsAccountTest-> Transaction: " + currentRunner);
      crmConnection.commit();
      //properties.putIfAbsent(COOKIE_PROPERTY, crmConnection.getCookie());
      //System.out.println("Connection cookie: " + crmConnection.getCookie());
      return crmConnection;
    }

  }

  public void testStub() throws Exception {
    assertTrue(true);
  }

  public void DoNotAutotestSelectLookupList() throws Exception {

    int totalToRunCount = 5;
    int maxThreads = 1;

    List<TransactionTask> renderTasks = new ArrayList<TransactionTask>();

    for (int i = 0; i < totalToRunCount; i++) {

      CRMConnection crmConnection = new CRMConnection();
      crmConnection.setUrl(crm.getUrl());
      crmConnection.setId(crm.getId());
      crmConnection.setSystemId(crm.getSystemId());
      crmConnection.setCode(crm.getCode());
      crmConnection.setAutoCommit(false);

      if (i == 1) {
        {
          //Add Meta Info with fields required
          ArrayList<String> meta = new ArrayList<String>();
          meta.add("code");
          meta.add("description");
          crmConnection.setTransactionMeta(meta);

          // Find the lookup stage
          DataRecord record = new DataRecord();
          record.setName("lookupAccountStageList");
          record.setAction(DataRecord.SELECT);
          record.addField("tableName", "lookup_account_stage");
          record.addField("description", "Requested");
          crmConnection.save(record);
        }
      } else if (i == 2) {
        //Add Meta Info with fields required
        ArrayList<String> meta = new ArrayList<String>();
        meta.add("userId");
        meta.add("nameLast");
        crmConnection.setTransactionMeta(meta);

        // Find the sales person
        DataRecord record = new DataRecord();
        record.setName("contactList");
        record.setAction(DataRecord.SELECT);
        record.addField("employeesOnly", "1");
        record.addField("lastName", "ConnectSales");
        crmConnection.save(record);
      } else if (i == 3) {
        //Add Meta Info with fields required
        ArrayList<String> meta = new ArrayList<String>();
        meta.add("orgId");
        //meta.add("name");
        crmConnection.setTransactionMeta(meta);

        // Find the account
        DataRecord record = new DataRecord();
        record.setName("accountList");
        record.setAction(DataRecord.SELECT);
        record.addField("custom1", "49");
        crmConnection.save(record);
      } else if (i == 4) {
        //Add Meta Info with fields required
        ArrayList<String> meta = new ArrayList<String>();
        meta.add("code");
        meta.add("description");
        crmConnection.setTransactionMeta(meta);

        // Find the lookup stage
        DataRecord record = new DataRecord();
        record.setName("lookupAccountTypesList");
        record.setAction(DataRecord.SELECT);
        record.addField("tableName", "lookup_account_types");
        record.addField("description", "Beauty & Fitness");
        crmConnection.save(record);
      } else {
        // Update test...
        int orgId = -1;
        String orgName = null;

        //Add Meta Info with fields required
        ArrayList<String> meta = new ArrayList<String>();
        meta.add("id");
        meta.add("name");
        crmConnection.setTransactionMeta(meta);

        // Find the account id
        DataRecord record = new DataRecord();
        record.setName("accountList");
        record.setAction(DataRecord.SELECT);
        record.addField("custom1", "49");
        crmConnection.save(record);

        orgId = Integer.parseInt(crmConnection.getResponseValue("orgId"));
        orgName = crmConnection.getResponseValue("name");

        assertTrue("An orgId must exist", orgId > -1);

        DataRecord account = new DataRecord();
        account.setAction(DataRecord.UPDATE);
        account.setName("account");
        account.addField("id", orgId);
        account.addField("name", orgName);
        account.addField("enteredBy", "$U{default}");
        account.addField("modifiedBy", "$U{default}");
        // Set the stage of the account

        account.addField("stageName", "Unclaimed");
        account.addField("stageId", 1);   // Assumed value... probably need to verify.
        crmConnection.save(record);
      }
      renderTasks.add(new TransactionTask(crmConnection));
    }

    ExecutorService executor = Executors.newFixedThreadPool(maxThreads);
    // NOTE: this wrapper fix is for Java 1.5
    final Collection<Callable<CRMConnection>> wrapper =
        Collections.<Callable<CRMConnection>>unmodifiableCollection(renderTasks);
    System.out.println("Executing...");
    List<Future<CRMConnection>> futures = executor.invokeAll(wrapper);
    // Wait for the results
    Iterator<TransactionTask> taskIterator = renderTasks.iterator();
    for (Future<CRMConnection> f : futures) {
      TransactionTask task = taskIterator.next();
      CRMConnection responseConnection = f.get();
      assertFalse("API reported a transaction error: " + responseConnection.getLastResponse(), responseConnection.hasError());
      System.out.println("SaveAsAccountTest-> ResponseValue <code>: " + responseConnection.getResponseValue("code"));
      System.out.println("SaveAsAccountTest-> ResponseValue <description>: " + responseConnection.getResponseValue("description"));
      System.out.println("SaveAsAccountTest-> ResponseValue <userId>: " + responseConnection.getResponseValue("userId"));
      System.out.println("SaveAsAccountTest-> ResponseValue <orgId>: " + responseConnection.getResponseValue("orgId"));
      System.out.println("SaveAsAccountTest-> ResponseValue <name>: " + responseConnection.getResponseValue("name"));
      System.out.println("");
      assertTrue("Did not get just 1 record, received: " + responseConnection.getRecordCount(), responseConnection.getRecordCount() == 1);
    }
    executor.shutdown();
  }
}