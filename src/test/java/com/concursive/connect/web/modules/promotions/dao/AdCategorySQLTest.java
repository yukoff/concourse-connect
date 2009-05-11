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
import com.concursive.connect.web.modules.promotions.dao.AdCategory;
import com.concursive.connect.web.modules.promotions.dao.AdCategoryList;
import com.concursive.connect.Constants;

import java.sql.SQLException;

/**
 * Tests common ad database access
 *
 * @author Lorraine
 * @created May 13, 2008
 */
public class AdCategorySQLTest extends AbstractConnectionPoolTest {


    public void testAdCRUD() throws SQLException {

        // Insert ad category
        AdCategory adCategory = new AdCategory();
        adCategory.setItemName("Ad Category SQL Test");
        adCategory.setEntered("01/01/2000");
        adCategory.setEnabled("true");
        assertNotNull(adCategory);
        boolean result = adCategory.insert(db);
        assertTrue("Ad was not inserted", result);
        assertTrue("Inserted ad did not have an id", adCategory.getId() > -1);

        // Update ad category
        // Reload the ad, then update
        assertTrue(adCategory.getId() > -1);
        adCategory = new AdCategory(db, adCategory.getId());
        adCategory.setItemName("Ad SQL Test Updated ad");
        int updateCount = adCategory.update(db);
        assertTrue("The ad was not updated by the database", updateCount == 1);

        // Find the previously set ad category
        int adCategoryId = adCategory.getId();
        adCategory = null;
        AdCategoryList adCategoryList = new AdCategoryList();
        adCategoryList.setEnabled(Constants.TRUE);
        adCategoryList.buildList(db);
        assertTrue(adCategoryList.size() > 0);
        for (AdCategory thisAdCategory : adCategoryList) {
            if (thisAdCategory.getId() == adCategoryId) {
                adCategory = thisAdCategory;
                break;
            }
        }
        assertNotNull(adCategory);

        // Delete the ad category
        assertNotNull(adCategory);
        adCategory.delete(db, "projects");
        adCategory = null;

        // Try to find the previously deleted ad category
        adCategoryList = new AdCategoryList();
        adCategoryList.buildList(db);
        for (AdCategory thisAdCategory : adCategoryList) {
            if (thisAdCategory.getId() == adCategoryId) {
                assertNull("Ad exists when it shouldn't", thisAdCategory);
            }
        }
    }

}