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
import com.concursive.connect.web.modules.classifieds.dao.Classified;
import com.concursive.connect.Constants;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.classifieds.dao.ClassifiedList;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Iterator;

/**
 * Tests common classified database access
 *
 * @author Kailash
 * @created May 21, 2008
 */
public class ClassifiedSQLTest extends AbstractConnectionPoolTest {

  protected static final int USER_ID = 1;
  
  //Required to insert a project
  protected static final int GROUP_ID = 1;

  public void testClassifiedCRUD() throws SQLException {

    // Insert project so that it can be referred to by the the classified
    Project project = new Project();
    project.setTitle("Project SQL Test");
    project.setShortDescription("Project SQL Test ClassifiedSQLTest");
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

    // Insert classified
    Classified classified = new Classified();
    classified.setTitle("Classified SQL Test");
    classified.setDescription("Classified SQL Test Description");
    classified.setProjectId(project.getId());
    classified.setEnabled(true);
    classified.setEnteredBy(USER_ID);
    classified.setModifiedBy(USER_ID);
    assertNotNull(classified);
    boolean result = classified.insert(db);
    assertTrue("Classified was not inserted", result);
    assertTrue("Inserted classified did not have an id", classified.getId() > -1);

    // Update classified
    // Try updating without reloading the classified
    assertNull(classified.getModified());
    assertTrue("Classified to update does not have an id", classified.getId() > -1);
    int updateCount = classified.update(db);
    assertTrue("The modified field checks for concurrent updates so the modified field must match the load value", updateCount == 0);
    // Reload the classified, then update
    assertTrue(classified.getId() > -1);
    classified = new Classified(db, classified.getId());
    classified.setTitle("Classified SQL Test Updated Classified");
    updateCount = classified.update(db);
    assertTrue("The classified was not updated by the database", updateCount == 1);

    // Find the previously set classified
    int classifiedId = classified.getId();
    classified = null;
    ClassifiedList classifiedList = new ClassifiedList();
    classifiedList.setEnabled(Constants.TRUE);
    classifiedList.buildList(db);
    assertTrue(classifiedList.size() > 0);
    Iterator i = classifiedList.iterator();
    while (i.hasNext()) {
      Classified thisClassified = (Classified) i.next();
      if (thisClassified.getId() == classifiedId) {
        classified = thisClassified;
        break;
      }
    }
    assertNotNull(classified);

    // Delete the classified
    assertNotNull(classified);
    classified.delete(db, (String) null);
    classifiedId = classified.getId();
    classified = null;
    
    // Try to find the previously deleted classified
    classifiedList = new ClassifiedList();
    classifiedList.setClassifiedId(classifiedId);
    classifiedList.buildList(db);
    Iterator ij = classifiedList.iterator();
    while (ij.hasNext()) {
      Classified thisClassified = (Classified) ij.next();
      if (thisClassified.getId() == classifiedId) {
        assertNull("Classified exists when it shouldn't", thisClassified);
      }
    }
    
    //Delete test project
    project.delete(db, (String) null);
  }

}