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
package com.concursive.connect.web.modules.common.social.contribution.dao;

import com.concursive.commons.db.AbstractConnectionPoolTest;
import com.concursive.connect.web.modules.contribution.dao.LookupContribution;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.common.social.contribution.dao.UserContributionLogList;
import com.concursive.connect.web.modules.common.social.contribution.dao.UserContributionLog;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Iterator;

/**
 * Tests user contribution log database access
 *
 * @author Kailash Bhoopalam
 * @created January 28, 2009
 */
public class UserContributionLogSQLTest extends AbstractConnectionPoolTest {

  protected static final int USER_ID = 1;
  //Required to insert a project
  protected static final int GROUP_ID = 1;
  
  public void testClassifiedCRUD() throws SQLException {

    // Insert project so that it can be referred to by the test
    Project project = new Project();
    project.setTitle("Project SQL Test");
    project.setShortDescription("Project SQL Test testContributionCalculationForAdsPlaced");
    project.setRequestDate(new Timestamp(System.currentTimeMillis() - 10));
    project.setEstimatedCloseDate((Timestamp) null);
    project.setRequestedBy("Project SQL Test Requested By");
    project.setRequestedByDept("Project SQL Test Requested By Department");
    project.setBudgetCurrency("USD");
    project.setBudget("10000.75");
    project.setGroupId(GROUP_ID);
    project.setApprovalDate(new Timestamp(System.currentTimeMillis() - 10));
    project.setEnteredBy(USER_ID);
    project.setModifiedBy(USER_ID);
    assertNotNull(project);
    boolean projectResult = project.insert(db);
    assertTrue("Project was not inserted", projectResult);
    assertTrue("Inserted project did not have an id", project.getId() > -1);

    LookupContribution lookupContribution = new LookupContribution();
    lookupContribution.setConstant("200901101230");
    lookupContribution.setDescription("Test Description");
    assertNotNull(lookupContribution);
    boolean result = lookupContribution.insert(db);
    assertTrue("Contribution was not inserted", result);
    assertTrue("Contribution did not have an id", lookupContribution.getId() > -1);

    // Insert user contribution log record
    UserContributionLog userContributionLog = new UserContributionLog();
    userContributionLog.setUserId(USER_ID);
    userContributionLog.setProjectId(project.getId());
    userContributionLog.setPoints(10);
    userContributionLog.setContributionId(lookupContribution.getId());
    userContributionLog.setContributionDate(new Timestamp(System.currentTimeMillis()));
    userContributionLog.setEntered(new Timestamp(System.currentTimeMillis()));
    assertNotNull(userContributionLog);
    result = userContributionLog.insert(db);
    assertTrue("UserContributionLog was not inserted", result);
    assertTrue("Inserted UserContributionLog did not have an id", userContributionLog.getId() > -1);

    // Reload the user contribution log record, then update
    int updateCount = -1;
    assertTrue(userContributionLog.getId() > -1);
    userContributionLog = new UserContributionLog(db, userContributionLog.getId());
    userContributionLog.setPoints(20);
    updateCount = userContributionLog.update(db);
    assertTrue("The UserContributionLog was not updated by the database", updateCount == 1);

    // Find the user contribution log record
    int userContributionId = userContributionLog.getId();
    userContributionLog = null;
    UserContributionLogList userContributionLogList = new UserContributionLogList();
    userContributionLogList.setUserId(USER_ID);
    userContributionLogList.buildList(db);
    assertTrue(userContributionLogList.size() > 0);
    Iterator<UserContributionLog> i = userContributionLogList.iterator();
    while (i.hasNext()) {
    	UserContributionLog thisUserContributionLog = i.next();
      if (thisUserContributionLog.getId() == userContributionId) {
        userContributionLog = thisUserContributionLog;
        break;
      }
    }
    assertNotNull(userContributionLog);

    // Delete the user contribution log record
    assertNotNull(userContributionLog);
    userContributionLog.delete(db);
    userContributionId = userContributionLog.getId();
    userContributionLog = null;
    
    // Try to find the previously deleted user contribution log record
    userContributionLogList = new UserContributionLogList();
    userContributionLogList.setId(userContributionId);
    userContributionLogList.buildList(db);
    Iterator<UserContributionLog> ij = userContributionLogList.iterator();
    while (ij.hasNext()) {
    	UserContributionLog thisUserContributionLog = ij.next();
      if (thisUserContributionLog.getId() == userContributionId) {
        assertNull("user contribution record exists when it shouldn't", thisUserContributionLog);
      }
    }
    
    //Delete test contribution
    lookupContribution.delete(db);
    
    project.delete(db, null);
  }

}