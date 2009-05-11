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
package com.concursive.connect.web.modules.profile.dao;

import com.concursive.commons.db.AbstractConnectionPoolTest;
import com.concursive.connect.web.modules.profile.dao.ProjectCategoryList;
import com.concursive.connect.web.modules.profile.dao.ProjectCategory;

import java.sql.SQLException;


/**
 * Tests common database access
 *
 * @author matt
 * @created June 19, 2008
 */
public class ProjectCategorySQLTest extends AbstractConnectionPoolTest {

  protected static final int GROUP_ID = 1;
  protected static final int USER_ID = 1;

  public void testProjectCategoryCRUD() throws SQLException {

    // Insert
    ProjectCategory category = new ProjectCategory();
    category.setDescription("Test Description");
    boolean result = category.insert(db);
    assertTrue("Insert did not return true", result);
    assertTrue("Id property didn't get populated", category.getId() > -1);

    // Find the previously inserted item
    int id = category.getId();
    ProjectCategory thisCategory = new ProjectCategory(db, id);
    assertTrue(thisCategory.getId() == id);
    
    // See if it's in the list too
    ProjectCategoryList categoryList = new ProjectCategoryList();
    categoryList.setCategoryId(id);
    categoryList.buildList(db);
    assertTrue(categoryList.size() == 1);
    category = categoryList.get(0);
    assertNotNull(category);
    assertTrue("Retrieved object does not match id", category.getId() == id);
    assertTrue(thisCategory.getId() == category.getId());

    // Update
    category.setDescription("A new test description");
    int updateCount = category.update(db);
    assertTrue(updateCount == 1);

    // Delete
    boolean deleteResult = category.delete(db, (String) null);
    assertTrue("Item wasn't deleted", deleteResult);

    // Try to find the previously deleted item
    categoryList = new ProjectCategoryList();
    categoryList.setCategoryId(id);
    categoryList.buildList(db);
    assertTrue("Record exists when it shouldn't -- id " + id, categoryList.size() == 0);
  }

}