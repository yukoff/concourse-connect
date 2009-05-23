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
package com.concursive.commons.api;

import com.concursive.commons.db.AbstractConnectionPoolTest;
import com.concursive.commons.xml.XMLUtils;
import com.concursive.connect.indexer.jobs.IndexerJob;
import com.concursive.connect.web.modules.api.beans.PacketContext;
import com.concursive.connect.web.modules.api.dao.SyncClient;
import com.concursive.connect.web.modules.api.dao.SyncTableList;
import com.concursive.connect.web.modules.api.utils.TransactionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleTrigger;

import java.io.File;
import java.io.FileInputStream;
import java.sql.SQLException;
import java.util.Date;
import java.util.Vector;

public abstract class AbstractAPITest extends AbstractConnectionPoolTest {

  protected static final int USER_ID = 1;
  protected static final int GROUP_ID = 1;
  protected static final int SYSTEM_ID = 1;
  protected static final String CLIENT_CODE = "XYZ";

  protected SyncClient syncClient;
  protected APIConnection api;
  protected PacketContext packetContext;
  protected Scheduler scheduler;

  private static Log log = LogFactory.getLog(AbstractAPITest.class);

  protected void setUp() throws Exception {
    // Turn on the API Debug mode which shows errors
    // TODO: Add error checking to the API when there is a problem
    System.setProperty("DEBUG-API", "true");
    // Get a database connection
    super.setUp();
    assertTrue("The database connection was null.", db != null);

    // Insert a new sync client for the API to test against
    syncClient = new SyncClient();
    syncClient.setType("API Test Client");
    syncClient.setVersion((String) null);
    syncClient.setEnteredBy(USER_ID);
    syncClient.setModifiedBy(USER_ID);
    syncClient.setEnabled(true);
    syncClient.setCode(CLIENT_CODE);
    syncClient.insert(db);
    assertTrue(syncClient.getId() > -1);
    syncClient = new SyncClient(db, syncClient.getId(), CLIENT_CODE);

    api = new APIConnection();
    api.setUrl("http://127.0.0.1:8080");
    api.setClientId(syncClient.getId());
    api.setSystemId(SYSTEM_ID);
    api.setCode(CLIENT_CODE);
    api.setAutoCommit(false);

    // Declare the server-side class instead of looking it up in the database
    SyncTableList syncTableList = new SyncTableList();
    syncTableList.setSystemId(SYSTEM_ID);
    // Load the core object map
    syncTableList.loadObjectMap(SyncTableList.class.getResourceAsStream("/object_map.xml"));
    assertFalse("Did not find any objects in object_map.xml", syncTableList.size() == 0);
    // Load plug-in mappings in the services path
    File[] serviceFiles = new File("src/main/webapp/WEB-INF/services").listFiles();
    assertTrue("Services not found in working directory... " + "src/main/webapp/WEB-INF/services", serviceFiles != null && serviceFiles.length > 0);
    if (serviceFiles != null && serviceFiles.length > 0) {
      for (File thisFile : serviceFiles) {
        if (thisFile.getAbsolutePath().endsWith(".xml")) {
          try {
            LOG.info("Adding services from... " + thisFile.getAbsolutePath());
            syncTableList.loadObjectMap(new FileInputStream(thisFile));
          } catch (Exception e) {
            LOG.error("getObjectMap exception", e);
          }
        }
      }
    }
    assertNotNull("Could not find 'backup' service", syncTableList.getSyncTableByName("backup"));

    // From the Service action method
    packetContext = new PacketContext();
    packetContext.setObjectMap(syncTableList.getObjectMapping(SYSTEM_ID));

    // Test the indexer when the API is used
    SchedulerFactory schedulerFactory = new org.quartz.impl.StdSchedulerFactory();
    scheduler = schedulerFactory.getScheduler();
    scheduler.getContext().setAllowsTransientData(true);
    scheduler.getContext().put("ConnectionPool", connectionPool);
    scheduler.getContext().put("ConnectionElement", ce);
    scheduler.getContext().put("ApplicationPrefs", mockPrefs);
    scheduler.getContext().put("IndexArray", new Vector());
    JobDetail job = new JobDetail(
        "indexer",
        Scheduler.DEFAULT_GROUP,
        IndexerJob.class);
    // Update every 24 hours, starting in 5 minutes
    long startTime = System.currentTimeMillis() + (5L * 60L * 1000L);
    SimpleTrigger trigger = new SimpleTrigger(
        "indexer",
        Scheduler.DEFAULT_GROUP,
        new Date(startTime),
        null,
        SimpleTrigger.REPEAT_INDEFINITELY,
        24L * 60L * 60L * 1000L);
    scheduler.scheduleJob(job, trigger);
    //scheduler.addJob(job, true);
    scheduler.start();
    // Jobs will be added by the tests
    packetContext.setScheduler(scheduler);
  }

  protected void tearDown() throws Exception {
    // Delete the syncClient
    syncClient.delete(db);
    // Remove the scheduler
    scheduler.shutdown(true);
    // Close the database connection
    super.tearDown();
  }

  protected void processTheTransactions(APIConnection api, PacketContext packetContext) throws SQLException {
    assertNotNull("APIConnection must not be null", api);
    assertNotNull("PacketContext must not be null", packetContext);
    assertNotNull("Connection must not be null", db);
    XMLUtils xml = null;
    try {
      String xmlString = api.generateXMLPacket();
      assertNotNull(xmlString);
      xml = new XMLUtils(xmlString);
      assertNotNull(xml);
    } catch (Exception e) {
      LOG.error(e);
      fail(e.getMessage());
    }

    boolean mockConnection = true;

    // Process the transaction using a Mock Connection
    // Since commit is not called, must reset if being reused
    if (mockConnection) {
      TransactionStatusList statusMessages = TransactionUtils.processTransactions(db, xml, packetContext);
      assertNotNull("Status Messages must not be null", statusMessages);
      String lastResponse = TransactionUtils.constructXMLResponse(statusMessages, "UTF-8");
      api.reset();
      assertNotNull("Last Response must not be null", lastResponse);
      api.setLastResponse(lastResponse);
    } else {
      // Process the transaction using an HTTP Connection to a running instance
      api.commit();
    }

  }
}