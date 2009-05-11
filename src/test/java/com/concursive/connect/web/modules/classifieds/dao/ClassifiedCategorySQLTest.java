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
package com.concursive.connect.web.modules.classifieds.dao;

import com.concursive.commons.db.AbstractConnectionPoolTest;
import com.concursive.connect.Constants;
import com.concursive.connect.web.modules.classifieds.dao.ClassifiedCategory;
import com.concursive.connect.web.modules.classifieds.dao.ClassifiedCategoryList;

import java.sql.SQLException;
import java.util.Iterator;

/**
 * Tests common classified category database access
 *
 * @author Kailash
 * @created May 21, 2008
 */
public class ClassifiedCategorySQLTest extends AbstractConnectionPoolTest {

  public void testClassifiedCategoryCRUD() throws SQLException {

    // Insert classified category
  	ClassifiedCategory classifiedCategory = new ClassifiedCategory();
    classifiedCategory.setItemName("Classified category SQL Test");
    classifiedCategory.setLevel("10");
    classifiedCategory.setEnabled(true);
    assertNotNull(classifiedCategory);
    boolean result = classifiedCategory.insert(db);
    assertTrue("Classified category was not inserted", result);
    assertTrue("Inserted classified category did not have an id", classifiedCategory.getId() > -1);

    // Update classified category
    // Try updating without reloading the classified category
    assertTrue("Classified category to update does not have an id", classifiedCategory.getId() > -1);
    int updateCount = classifiedCategory.update(db);
    // Reload the classified category, then update
    assertTrue(classifiedCategory.getId() > -1);
    classifiedCategory = new ClassifiedCategory(db, classifiedCategory.getId());
    classifiedCategory.setItemName("Classified Category SQL Test Updated Classified");
    updateCount = classifiedCategory.update(db);
    assertTrue("The classified category was not updated by the database", updateCount == 1);

    // Find the previously set classified
    int classifiedCategoryId = classifiedCategory.getId();
    classifiedCategory = null;
    ClassifiedCategoryList classifiedCategoryList = new ClassifiedCategoryList();
    classifiedCategoryList.setEnabled(Constants.TRUE);
    classifiedCategoryList.buildList(db);
    assertTrue(classifiedCategoryList.size() > 0);
    Iterator i = classifiedCategoryList.iterator();
    while (i.hasNext()) {
    	ClassifiedCategory thisClassifiedCategory = (ClassifiedCategory) i.next();
      if (thisClassifiedCategory.getId() == classifiedCategoryId) {
      	classifiedCategory = thisClassifiedCategory;
        break;
      }
    }
    assertNotNull(classifiedCategory);

    classifiedCategory.delete(db, (String) null);
    classifiedCategoryId = classifiedCategory.getId();
    classifiedCategory = null;
    
    // Try to find the previously deleted classified category
    classifiedCategoryList = new ClassifiedCategoryList();
    classifiedCategoryList.setCode(classifiedCategoryId);
    classifiedCategoryList.buildList(db);
    Iterator ij = classifiedCategoryList.iterator();
    while (ij.hasNext()) {
    	ClassifiedCategory thisClassifiedCategory = (ClassifiedCategory) ij.next();
      if (thisClassifiedCategory.getId() == classifiedCategoryId) {
        assertNull("Classified category exists when it shouldn't", thisClassifiedCategory);
      }
    }
  }

}