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
package com.concursive.connect.web.modules.contribution.dao;

import com.concursive.commons.db.AbstractConnectionPoolTest;
import com.concursive.connect.web.modules.contribution.dao.LookupContribution;
import com.concursive.connect.web.modules.contribution.dao.LookupContributionList;

import java.sql.SQLException;
import java.util.Iterator;

/**
 * Tests user contribution log database access
 *
 * @author Kailash Bhoopalam
 * @created January 28, 2009
 */
public class LookupContributionSQLTest extends AbstractConnectionPoolTest {

  protected static final int USER_ID = 1;
  
  public void testClassifiedCRUD() throws SQLException {

    LookupContribution lookupContribution = new LookupContribution();
    lookupContribution.setConstant("200901101230");
    lookupContribution.setDescription("Test Description");
    assertNotNull(lookupContribution);
    boolean result = lookupContribution.insert(db);
    assertTrue("LookupContribution record was not inserted", result);
    assertTrue("LookupContribution record did not have an id", lookupContribution.getId() > -1);

    // Reload the user contribution log record, then update
    LookupContribution newLookupContribution = new LookupContribution(db, lookupContribution.getId());
    assertTrue("Query lookup contribution failed", "Test Description".equals(newLookupContribution.getDescription()));
    int updateCount = -1;
    newLookupContribution.setConstant("200901101231");
    updateCount = newLookupContribution.update(db);
    assertTrue("The LookupContribution was not updated by the database", updateCount == 1);

    // Find the user contribution log record
    int contributionCode = newLookupContribution.getId();
    newLookupContribution = null;
    LookupContributionList lookupContributionList = new LookupContributionList();
    lookupContributionList.buildList(db);
    assertTrue(lookupContributionList.size() > 0);
    Iterator<LookupContribution> i = lookupContributionList.iterator();
    while (i.hasNext()) {
    	LookupContribution thisLookupContribution = i.next();
      if (thisLookupContribution.getId() == contributionCode) {
      	newLookupContribution = thisLookupContribution;
        break;
      }
    }
    assertNotNull(newLookupContribution);

    newLookupContribution.delete(db);
    contributionCode = newLookupContribution.getId();
    newLookupContribution = null;
    
    // Try to find the previously deleted user contribution log record
    lookupContributionList = new LookupContributionList();
    lookupContributionList.buildList(db);
    Iterator<LookupContribution> ij = lookupContributionList.iterator();
    while (ij.hasNext()) {
    	LookupContribution thisLookupContribution = ij.next();
      if (thisLookupContribution.getId() == contributionCode) {
        assertNull("lookup contribution record exists when it shouldn't", thisLookupContribution);
      }
    }
  }
}