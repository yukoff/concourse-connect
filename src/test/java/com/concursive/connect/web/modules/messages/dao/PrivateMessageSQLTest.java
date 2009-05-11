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
package com.concursive.connect.web.modules.messages.dao;

import com.concursive.commons.db.AbstractConnectionPoolTest;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.messages.dao.PrivateMessage;
import com.concursive.connect.web.modules.messages.dao.PrivateMessageList;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Iterator;

/**
 * Tests common private message database access
 *
 * @author Kailash
 * @created December 21, 2008
 */
public class PrivateMessageSQLTest extends AbstractConnectionPoolTest {

  protected static final int USER_ID = 1;
  
  //Required to insert a project
  protected static final int GROUP_ID = 1;

  public void testClassifiedCRUD() throws SQLException {

    // Insert project so that it can be referred to by the private message
    Project project = new Project();
    project.setTitle("Project Private Message SQL Test");
    project.setShortDescription("Project SQL Test Description");
    project.setRequestDate(new Timestamp(System.currentTimeMillis()));
    project.setEstimatedCloseDate((Timestamp) null);
    project.setRequestedBy("Project SQL Test Requested By");
    project.setRequestedByDept("Project SQL Test Requested By Department");
    project.setBudgetCurrency("USD");
    project.setBudget("10000.75");
    project.setGroupId(GROUP_ID);
    project.setEnteredBy(USER_ID);
    project.setModifiedBy(USER_ID);
    assertNotNull(project);
    boolean projectResult = project.insert(db);
    assertTrue("Project was not inserted", projectResult);
    assertTrue("Inserted project did not have an id", project.getId() > -1);

    // Insert private message
    PrivateMessage privateMessage = new PrivateMessage();
    privateMessage.setBody("PrivateMessage SQL Test");
    privateMessage.setProjectId(project.getId());
    privateMessage.setLinkModuleId(1);
    privateMessage.setLinkItemId(1);
    privateMessage.setEnteredBy(USER_ID);
    assertNotNull(privateMessage);
    boolean result = privateMessage.insert(db);
    assertTrue("PrivateMessage was not inserted", result);
    assertTrue("Inserted PrivateMessage did not have an id", privateMessage.getId() > -1);

    // Reload the private message, then update
    int updateCount = -1;
    assertTrue(privateMessage.getId() > -1);
    privateMessage = new PrivateMessage(db, privateMessage.getId());
    privateMessage.setDeletedByUserId(true);
    updateCount = privateMessage.update(db);
    assertTrue("The private message was not updated by the database", updateCount == 1);

    // Find the previously set private message
    int messageId = privateMessage.getId();
    privateMessage = null;
    PrivateMessageList privateMessageList = new PrivateMessageList();
    privateMessageList.setProjectId(project.getId());
    privateMessageList.buildList(db);
    assertTrue(privateMessageList.size() > 0);
    Iterator<PrivateMessage> i = privateMessageList.iterator();
    while (i.hasNext()) {
      PrivateMessage thisPrivateMessage = i.next();
      if (thisPrivateMessage.getId() == messageId) {
        privateMessage = thisPrivateMessage;
        break;
      }
    }
    assertNotNull(privateMessage);

    // Delete the private message
    assertNotNull(privateMessage);
    privateMessage.delete(db);
    messageId = privateMessage.getId();
    privateMessage = null;
    
    // Try to find the previously deleted private message
    privateMessageList = new PrivateMessageList();
    privateMessageList.setId(messageId);
    privateMessageList.buildList(db);
    Iterator<PrivateMessage> ij = privateMessageList.iterator();
    while (ij.hasNext()) {
      PrivateMessage thisPrivateMessage = ij.next();
      if (thisPrivateMessage.getId() == messageId) {
        assertNull("Private message exists when it shouldn't", thisPrivateMessage);
      }
    }
    
    //Delete test project
    project.delete(db, (String) null);
  }

}