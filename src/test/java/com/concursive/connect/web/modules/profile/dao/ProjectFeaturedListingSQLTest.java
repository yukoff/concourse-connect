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
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.dao.ProjectFeaturedListing;
import com.concursive.connect.web.modules.profile.dao.ProjectFeaturedListingList;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Iterator;

/**
 * Tests project featured listing database access
 *
 * @author Kailash Bhoopalam
 * @created January 20, 2008
 */
public class ProjectFeaturedListingSQLTest extends AbstractConnectionPoolTest {

  protected static final int USER_ID = 1;
  
  //Required to insert a project
  protected static final int GROUP_ID = 1;

  public void testClassifiedCRUD() throws SQLException {

    // Insert project so that it can be referred to by the featured project
    Project project = new Project();
    project.setTitle("Project Featured Listing SQL Test");
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

    // Insert featured project
    ProjectFeaturedListing projectFeaturedListing = new ProjectFeaturedListing();
    projectFeaturedListing.setProjectId(project.getId());
    projectFeaturedListing.setPortletKey("ABCD");
    projectFeaturedListing.setFeaturedDate(new Timestamp(System.currentTimeMillis()));
    assertNotNull(projectFeaturedListing);
    boolean result = projectFeaturedListing.insert(db);
    assertTrue("ProjectFeaturedListing was not inserted", result);
    assertTrue("Inserted ProjectFeaturedListing did not have an id", projectFeaturedListing.getId() > -1);

    // Reload the featured project, then update
    int updateCount = -1;
    assertTrue(projectFeaturedListing.getId() > -1);
    projectFeaturedListing = new ProjectFeaturedListing(db, projectFeaturedListing.getId());
    projectFeaturedListing.setPortletKey("ABCDE");
    updateCount = projectFeaturedListing.update(db);
    assertTrue("The featured project was not updated by the database", updateCount == 1);

    // Find the previously set featured project
    int featuredId = projectFeaturedListing.getId();
    projectFeaturedListing = null;
    ProjectFeaturedListingList projectFeaturedListingList = new ProjectFeaturedListingList();
    projectFeaturedListingList.setProjectId(project.getId());
    projectFeaturedListingList.buildList(db);
    assertTrue(projectFeaturedListingList.size() > 0);
    Iterator<ProjectFeaturedListing> i = projectFeaturedListingList.iterator();
    while (i.hasNext()) {
    	ProjectFeaturedListing thisProjectFeaturedListing = i.next();
      if (thisProjectFeaturedListing.getId() == featuredId) {
        projectFeaturedListing = thisProjectFeaturedListing;
        break;
      }
    }
    assertNotNull(projectFeaturedListing);

    // Delete the featured project
    assertNotNull(projectFeaturedListing);
    projectFeaturedListing.delete(db);
    featuredId = projectFeaturedListing.getId();
    projectFeaturedListing = null;
    
    // Try to find the previously deleted featured project
    projectFeaturedListingList = new ProjectFeaturedListingList();
    projectFeaturedListingList.setId(featuredId);
    projectFeaturedListingList.buildList(db);
    Iterator<ProjectFeaturedListing> ij = projectFeaturedListingList.iterator();
    while (ij.hasNext()) {
    	ProjectFeaturedListing thisProjectFeaturedListing = ij.next();
      if (thisProjectFeaturedListing.getId() == featuredId) {
        assertNull("Private message exists when it shouldn't", thisProjectFeaturedListing);
      }
    }
    
    //Delete test project
    project.delete(db, (String) null);
  }

}