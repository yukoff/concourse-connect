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
package com.concursive.connect.web.modules.badges.dao;

import com.concursive.commons.db.AbstractConnectionPoolTest;
import com.concursive.connect.Constants;
import com.concursive.connect.web.modules.badges.dao.BadgeCategory;
import com.concursive.connect.web.modules.badges.dao.BadgeCategoryList;
import com.concursive.connect.web.modules.profile.dao.ProjectCategory;

import java.sql.SQLException;
import java.util.Iterator;

/**
 * Tests common badge category database access
 *
 * @author Kailash
 * @created May 13, 2008
 */
public class BadgeCategorySQLTest extends AbstractConnectionPoolTest {

  public void testBadgeCategoryCRUD() throws SQLException {

    // Insert a required project category
    ProjectCategory projectCategory = new ProjectCategory();
    projectCategory.setDescription("Badge category SQL Test"+System.currentTimeMillis());
    projectCategory.insert(db);

    // Insert badge category
  	BadgeCategory badgeCategory = new BadgeCategory();
    badgeCategory.setProjectCategoryId(projectCategory.getId());
    badgeCategory.setItemName("Badge category SQL Test");
    badgeCategory.setLevel("10");
    badgeCategory.setEnabled(true);
    assertNotNull(badgeCategory);
    boolean result = badgeCategory.insert(db);
    assertTrue("Badge category was not inserted", result);
    assertTrue("Inserted badge category did not have an id", badgeCategory.getId() > -1);

    // Update badge category
    // Try updating without reloading the badge category
    assertTrue("Badge category to update does not have an id", badgeCategory.getId() > -1);
    int updateCount = badgeCategory.update(db);
    // Reload the badge category, then update
    assertTrue(badgeCategory.getId() > -1);
    badgeCategory = new BadgeCategory(db, badgeCategory.getId());
    badgeCategory.setItemName("Badge Category SQL Test Updated Badge");
    updateCount = badgeCategory.update(db);
    assertTrue("The badge category was not updated by the database", updateCount == 1);

    // Find the previously set badge
    int badgeCategoryId = badgeCategory.getId();
    badgeCategory = null;
    BadgeCategoryList badgeCategoryList = new BadgeCategoryList();
    badgeCategoryList.setEnabled(Constants.TRUE);
    badgeCategoryList.buildList(db);
    assertTrue(badgeCategoryList.size() > 0);
    Iterator i = badgeCategoryList.iterator();
    while (i.hasNext()) {
    	BadgeCategory thisBadgeCategory = (BadgeCategory) i.next();
      if (thisBadgeCategory.getId() == badgeCategoryId) {
      	badgeCategory = thisBadgeCategory;
        break;
      }
    }
    assertNotNull(badgeCategory);

    // delete the badge category
    badgeCategory.delete(db, null);
    badgeCategoryId = badgeCategory.getId();
    badgeCategory = null;
    // delete the project category
    projectCategory.delete(db, null);

    // Try to find the previously deleted badge category
    badgeCategoryList = new BadgeCategoryList();
    badgeCategoryList.setCode(badgeCategoryId);
    badgeCategoryList.buildList(db);
    Iterator ij = badgeCategoryList.iterator();
    while (ij.hasNext()) {
    	BadgeCategory thisBadgeCategory = (BadgeCategory) ij.next();
      if (thisBadgeCategory.getId() == badgeCategoryId) {
        assertNull("Badge category exists when it shouldn't", thisBadgeCategory);
      }
    }
  }

}