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
package com.concursive.connect.web.modules.promotions.dao;

import com.concursive.commons.db.AbstractConnectionPoolTest;
import com.concursive.connect.Constants;
import com.concursive.connect.web.modules.promotions.dao.Ad;
import com.concursive.connect.web.modules.promotions.dao.AdList;

import java.sql.SQLException;

/**
 * Tests common ad database access
 *
 * @author Lorraine
 * @created May 12, 2008
 */
public class AdSQLTest extends AbstractConnectionPoolTest {

  //protected static final int PROJECT_ID = 1;
  protected static final int USER_ID = 1;


  public void testAdCRUD() throws SQLException {

    // Insert ad
    Ad ad = new Ad();
    ad.setHeading("Ad SQL Test");
    ad.setContent("Ad SQL Test Description");
    ad.setWebPage("http://www.concursive.com");
    ad.setPublishDate("05/15/2010");
    ad.setExpirationDate("05/15/2020");
    ad.setEntered("01/01/2000");
    ad.setEnabled("true");
    ad.setEnteredBy(""+USER_ID);
    ad.setModifiedBy(""+USER_ID);
    assertNotNull(ad);
    boolean result = ad.insert(db);
    assertTrue("Ad was not inserted", result);
    assertTrue("Inserted ad did not have an id", ad.getId() > -1);

    // Update ad
    // Try updating without reloading the ad
    assertNull(ad.getModified());
    assertTrue("Ad to update does not have an id", ad.getId() > -1);
    int updateCount = ad.update(db);
    assertTrue("The modified field checks for concurrent updates so the modified field must match the load value", updateCount == 0);
    // Reload the ad, then update
    assertTrue(ad.getId() > -1);
    ad = new Ad(db, ad.getId());
    ad.setHeading("Ad SQL Test Updated ad");
    updateCount = ad.update(db);
    assertTrue("The ad was not updated by the database", updateCount == 1);

    // Find the previously set ad
    int adId = ad.getId();
    ad = null;
    AdList adList = new AdList();
    adList.setEnabled(Constants.TRUE);
    adList.buildList(db);
    assertTrue(adList.size() > 0);
    for(Ad thisAd : adList){
        if(thisAd.getId() == adId) {
            ad = thisAd;
            break;
        }
    }
    assertNotNull(ad);

    // Delete the ad
    assertNotNull(ad);
    ad.delete(db);
    adId = ad.getId();
    ad = null;

    // Try to find the previously deleted ad
    adList = new AdList();
    adList.buildList(db);
    for(Ad thisAd : adList){
        if(thisAd.getId() == adId) {
            assertNull("Ad exists when it shouldn't", thisAd);
        }
    }
  }

}