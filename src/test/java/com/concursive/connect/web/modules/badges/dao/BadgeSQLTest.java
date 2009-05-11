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
import com.concursive.connect.web.modules.badges.utils.BadgeUtils;
import com.concursive.connect.web.modules.badges.dao.Badge;
import com.concursive.connect.web.modules.badges.dao.BadgeCategory;
import com.concursive.connect.web.modules.badges.dao.BadgeList;
import com.concursive.connect.web.modules.profile.dao.ProjectCategory;
import com.concursive.connect.Constants;

import java.sql.SQLException;
import java.util.Iterator;

/**
 * Tests common badge database access
 *
 * @author Kailash
 * @created May 12, 2008
 */
public class BadgeSQLTest extends AbstractConnectionPoolTest {

  protected static final int USER_ID = 1;

  public void testBadgeCRUD() throws SQLException {
    // Insert a project category
    ProjectCategory projectCategory = new ProjectCategory();
    projectCategory.setDescription("Badge SQL Test" + System.currentTimeMillis());
    projectCategory.insert(db);

    // Insert a badge category
    BadgeCategory badgeCategory = new BadgeCategory();
    badgeCategory.setProjectCategoryId(projectCategory.getId());
    badgeCategory.setItemName("Badge SQL Test" + System.currentTimeMillis());
    badgeCategory.insert(db);

    // Insert badge
    Badge badge = new Badge();
    badge.setCategoryId(badgeCategory.getId());
    badge.setTitle("Badge SQL Test");
    badge.setDescription("Badge SQL Test Description");
    badge.setEnabled(true);
    badge.setEnteredBy(USER_ID);
    badge.setModifiedBy(USER_ID);
    assertNotNull(badge);
    boolean result = badge.insert(db);
    assertTrue("Badge was not inserted", result);
    assertTrue("Inserted badge did not have an id", badge.getId() > -1);

    // Update badge
    // Try updating without reloading the badge
    assertNull(badge.getModified());
    assertTrue("Badge to update does not have an id", badge.getId() > -1);
    int updateCount = badge.update(db);
    assertTrue("The modified field checks for concurrent updates so the modified field must match the load value", updateCount == 0);
    // Reload the badge, then update
    assertTrue(badge.getId() > -1);
    badge = BadgeUtils.loadBadge(badge.getId());
    badge.setTitle("Badge SQL Test Updated Badge");
    updateCount = badge.update(db);
    assertTrue("The badge was not updated by the database", updateCount == 1);

    // Find the previously set badge
    int badgeId = badge.getId();
    badge = null;
    BadgeList badgeList = new BadgeList();
    badgeList.setEnabled(Constants.TRUE);
    badgeList.buildList(db);
    assertTrue(badgeList.size() > 0);
    Iterator i = badgeList.iterator();
    while (i.hasNext()) {
      Badge thisBadge = (Badge) i.next();
      if (thisBadge.getId() == badgeId) {
        badge = thisBadge;
        break;
      }
    }
    assertNotNull(badge);

    // Delete the badge
    assertNotNull(badge);
    badge.delete(db, null);
    badgeId = badge.getId();
    badge = null;

    // Delete the badge category
    badgeCategory.delete(db, null);

    // Delete the project category
    projectCategory.delete(db, null);

    // Try to find the previously deleted badge
    badgeList = new BadgeList();
    badgeList.setBadgeId(badgeId);
    badgeList.buildList(db);
    Iterator ij = badgeList.iterator();
    while (ij.hasNext()) {
      Badge thisBadge = (Badge) ij.next();
      if (thisBadge.getId() == badgeId) {
        assertNull("Badge exists when it shouldn't", thisBadge);
      }
    }
  }

}