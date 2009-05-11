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
package com.concursive.connect.web.modules.reviews.dao;

import com.concursive.commons.db.AbstractConnectionPoolTest;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.reviews.dao.ProjectRating;
import com.concursive.connect.web.modules.reviews.dao.ProjectRatingList;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Iterator;

/**
 * Tests common project rating database access
 *
 * @author Kailash Bhoopalam
 * @created July 10, 2008
 */
public class ThreadedProjectRatingSQLTest extends AbstractConnectionPoolTest {

  protected static final int USER_ID = 1;

  //Required to insert a project
  protected static final int GROUP_ID = 1;

  public void testThreadedProjectRatingCRUD() throws SQLException {
  }
  
  public void xtestThreadedProjectRatingCRUD() throws SQLException {

    // Insert project so that it can be referred to by the the classified
    Project project = new Project();
    project.setTitle("Project SQL Test");
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

    // Insert project rating
    ProjectRating projectRating = new ProjectRating();
    projectRating.setTitle("Project Rating Title");
    projectRating.setComment("Project Rating Comment");
    projectRating.setProjectId(project.getId());
    projectRating.setRating(3);
    projectRating.setEnteredBy(USER_ID);
    projectRating.setModifiedBy(USER_ID);
    assertNotNull(projectRating);
    boolean result = ProjectRating.save(db, projectRating);
    assertTrue("Project Rating was not inserted", result);
    assertTrue("Inserted badge did not have an id", projectRating.getId() > -1);

    // Reload the badge, then update
    assertTrue(projectRating.getId() > -1);
    projectRating = new ProjectRating(db, projectRating.getId());
    projectRating.setTitle("Project Rating SQL Test Updated Project Rating");
    result = ProjectRating.save(db, projectRating);
    assertTrue("The Project Rating was not updated by the database", result);

    // Find the previously set ProjectRating
    int ratingId = projectRating.getId();
    projectRating = null;
    ProjectRatingList projectRatingList = new ProjectRatingList();
    projectRatingList.buildList(db);
    assertTrue(projectRatingList.size() > 0);
    Iterator<ProjectRating> i = projectRatingList.iterator();
    while (i.hasNext()) {
    	ProjectRating thisProjectRating = (ProjectRating) i.next();
      if (thisProjectRating.getId() == ratingId) {
        projectRating = thisProjectRating;
        break;
      }
    }
    assertNotNull(projectRating);

    // Delete the ProjectRating
    assertNotNull(projectRating);
    ProjectRating.delete(db, projectRating);
    ratingId = projectRating.getId();
    projectRating = null;
    
    // Try to find the previously deleted ProjectRating
    projectRatingList = new ProjectRatingList();
    projectRatingList.setRatingId(ratingId);
    projectRatingList.buildList(db);
    Iterator<ProjectRating> ij = projectRatingList.iterator();
    while (ij.hasNext()) {
    	ProjectRating thisProjectRating = (ProjectRating) ij.next();
      if (thisProjectRating.getId() == ratingId) {
        assertNull("ProjectRating exists when it shouldn't", thisProjectRating);
      }
    }

    //Delete test project
    project.delete(db, (String) null);
  }
  
  class ThreadedProjectSave implements Runnable{

		/* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    public void run() {
	    // TODO Auto-generated method stub
	    
    }
  	
  }

  class ThreadedProjectDelete implements Runnable{

		/* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    public void run() {
	    // TODO Auto-generated method stub
	    
    }
  	
  }
  
  class ThreadedProjectRatingSave implements Runnable{

		/* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    public void run() {
	    // TODO Auto-generated method stub
	    
    }
  	
  }

  class ThreadedProjectRatingDelete implements Runnable{

		/* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    public void run() {
	    // TODO Auto-generated method stub
	    
    }
  	
  }
  
  class ThreadedProjectRatingRatingSave implements Runnable{

		/* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    public void run() {
	    // TODO Auto-generated method stub
	    
    }
  	
  }

  class ThreadedProjectRatingRatingDelete implements Runnable{

		/* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    public void run() {
	    // TODO Auto-generated method stub
	    
    }
  	
  }

}