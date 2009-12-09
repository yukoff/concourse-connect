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
package com.concursive.connect.web.modules.common.social.contribution;

import com.concursive.commons.db.AbstractConnectionPoolTest;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.Constants;
import com.concursive.connect.web.modules.documents.dao.FileDownloadLog;
import com.concursive.connect.web.modules.documents.dao.FileItem;
import com.concursive.connect.web.modules.documents.dao.FileItemList;
import com.concursive.connect.web.modules.documents.contribution.ContributionCalculationForFilesDownloaded;
import com.concursive.connect.web.modules.contribution.dao.LookupContribution;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.common.social.contribution.dao.UserContributionLogList;

import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * Tests user contribution log database access
 *
 * @author Kailash Bhoopalam
 * @created February 09, 2009
 */
public class ContributionCalculationForFilesDownloadedTest extends AbstractConnectionPoolTest {

  protected static final int USER_ID = 1;
  protected static final int GROUP_ID = 1;
  protected static final int DEPARTMENT_ID = 1;
  
  public void testContributionCalculationForFilesDownloaded() throws SQLException {

  	// Inserting user
  	User user = new User();
  	user.setFirstName("First Name");
  	user.setLastName("Last Name");
  	user.setUsername("Username");
  	user.setPassword1("Password");
  	user.setPassword2("Password");
  	user.setPassword("Password");
  	user.setEnteredBy(USER_ID);
  	user.setModifiedBy(USER_ID);
  	user.setGroupId(DEPARTMENT_ID);
  	user.setDepartmentId(1);
    boolean userInserted = user.insert(db, "127.0.0.1", mockPrefs);
    assertTrue("User was not inserted ", userInserted);
    assertTrue("User did not have an id ", user.getId() > -1);
  	
    LookupContribution lookupContribution = new LookupContribution();
    lookupContribution.setConstant("200901101230");
    lookupContribution.setDescription("Test files downloaded contribution");
    lookupContribution.setPointsAwarded(2);
    assertNotNull(lookupContribution);
    boolean result = lookupContribution.insert(db);
    assertTrue("Contribution was not inserted ", result);
    assertTrue("Contribution did not have an id ", lookupContribution.getId() > -1);
    assertTrue("Contribution had run date when it should not have had one ", lookupContribution.getRunDate() == null);

    // Insert project so that it can be referred to by the test
    Project project = new Project();
    project.setTitle("Project SQL Test");
    project.setShortDescription("Project SQL Test ContributionCalculationForFilesDownloadedTest");
    project.setRequestDate(new Timestamp(System.currentTimeMillis() - 10));
    project.setEstimatedCloseDate((Timestamp) null);
    project.setRequestedBy("Project SQL Test Requested By");
    project.setRequestedByDept("Project SQL Test Requested By Department");
    project.setBudgetCurrency("USD");
    project.setBudget("10000.75");
    project.setGroupId(GROUP_ID);
    project.setApprovalDate(new Timestamp(System.currentTimeMillis() - 10));
    project.setEnteredBy(USER_ID);
    project.setModifiedBy(USER_ID);
    assertNotNull(project);
    boolean projectResult = project.insert(db);
    assertTrue("Project was not inserted", projectResult);
    assertTrue("Inserted project did not have an id", project.getId() > -1);
    
    //Inserting FileItem
    FileItem newFileItem = new FileItem();
    newFileItem.setLinkItemId(project.getId());
    newFileItem.setLinkModuleId(Constants.PROJECTS_FILES);
    newFileItem.setSubject("File Subject");
    newFileItem.setClientFilename("File client file name");
    newFileItem.setEnteredBy(USER_ID);
    newFileItem.setModifiedBy(USER_ID);
    newFileItem.setFeaturedFile(true);
    boolean fileInserted = newFileItem.insert(db);
    assertTrue("File Item was not inserted", fileInserted);
    assertTrue("Inserted fileItem did not have an id", newFileItem.getId() > -1);
    
    FileDownloadLog fileDownloadLog = new FileDownloadLog();
    fileDownloadLog.setItemId(newFileItem.getId());
    fileDownloadLog.setDownloadDate(new Timestamp(System.currentTimeMillis()));
    fileDownloadLog.setUserId(user.getId());
    fileDownloadLog.setVersion("0");
    boolean fileDownloadLogInserted = fileDownloadLog.insert(db);
    assertTrue("File download log was not inserted", fileDownloadLogInserted);
    
    //load the user to get the points before the calculation of his contribution
    User user1 = new User(db, USER_ID);
    int originalPoints = user1.getPoints();
    
    //load the user contributions to get the points before the calculation of his contribution
    UserContributionLogList userContributionLogList = new UserContributionLogList();
    userContributionLogList.setContributionId(lookupContribution.getId());
    userContributionLogList.setUserId(USER_ID);
    userContributionLogList.setProjectId(project.getId());
    userContributionLogList.buildList(db);
    int numberOfUserContributions = userContributionLogList.size();

    // Insert user contribution log record
    ContributionCalculationForFilesDownloaded contributionCalculationForFilesDownloaded = new ContributionCalculationForFilesDownloaded();
    contributionCalculationForFilesDownloaded.process(db, lookupContribution);
    
    //test that the run date in lookup contribution has been set
    int lookupContributionId = lookupContribution.getId();
    lookupContribution = new LookupContribution(db, lookupContributionId);
    assertTrue("Contribution had run date when it should not have had one ", lookupContribution.getRunDate() != null);
    
    //load the user to get the points after the calculation of his contribution
    user1 = new User(db, USER_ID);
    //test that the points in the user record is set
    assertTrue("Contribution points is not set ", user1.getPoints() > originalPoints);
    
    //test that a user contribution log has been inserted
    userContributionLogList = new UserContributionLogList();
    userContributionLogList.setContributionId(lookupContribution.getId());
    userContributionLogList.setUserId(USER_ID);
    userContributionLogList.setProjectId(project.getId());
    userContributionLogList.buildList(db);
    
    //TODO: this test may need revision
    assertTrue("User contribution log not inserted ", userContributionLogList.size() > numberOfUserContributions);

  	//Reset User Points
  	User.resetPoints(db, USER_ID);
  	
    //Delete test contribution which also deletes the contribution log records
    lookupContribution.delete(db);

    //delete the download log
    fileDownloadLog.delete(db);
    
    //Delete test project and file
    project.delete(db, (String) null);
    
    //delete test user
    user.delete(db);
  }
}
