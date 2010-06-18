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

import com.concursive.commons.xml.XMLUtils;
import com.concursive.crm.api.client.CRMConnection;
import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.SQLException;

public abstract class AbstractToolsAPITest extends TestCase {

  protected static final int SYSTEM_ID = 4;
  protected static final String CLIENT_CODE = "plain-text password in database";
  protected static final int USER_ID = 1;

  protected CRMConnection crm;

  private static Log log = LogFactory.getLog(AbstractToolsAPITest.class);

  protected void setUp() throws Exception {
    crm = new CRMConnection();
    crm.setUrl("http://127.0.0.1:8080/centric");
    crm.setId("127.0.0.1");
    crm.setSystemId(SYSTEM_ID);
    crm.setCode(CLIENT_CODE);
    crm.setAutoCommit(false);
  }

  protected void processTheTransactions() throws SQLException {
    assertNotNull("CRMConnection must not be null", crm);
    XMLUtils xml = null;
    try {
      String xmlString = crm.generateXMLPacket();
      assertNotNull(xmlString);
      xml = new XMLUtils(xmlString);
      assertNotNull(xml);
    } catch (Exception e) {
      fail(e.getMessage());
    }
    // Process the transaction using an HTTP Connection to a running instance
    crm.commit();
  }
}
