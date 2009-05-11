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
package com.concursive.connect.web.modules.common.social.tagging.dao;

import com.concursive.commons.db.AbstractConnectionPoolTest;
import com.concursive.connect.Constants;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.common.social.tagging.dao.TagList;
import com.concursive.connect.web.modules.common.social.tagging.dao.TagLogList;
import com.concursive.connect.web.modules.common.social.tagging.dao.UserTagLogList;
import com.concursive.connect.web.modules.common.social.tagging.dao.UserTagLog;

import java.sql.SQLException;

/**
 * Tests tag database access
 *
 * @author Kailash Bhoopalam
 * @created July 18, 2008
 */
public class UserTagLogSQLTest extends AbstractConnectionPoolTest {

  protected static final int USER_ID = 1;

  public void testProjectTagCRUD() throws SQLException {

  	UserTagLogList userTagLogList = new UserTagLogList();
  	userTagLogList.buildList(db);
  	for (UserTagLog userTagLog : userTagLogList){
  		userTagLog.delete(db);
  	}
  	
    // Insert a tag
    UserTagLog userTagLogForProject = new UserTagLog();
    userTagLogForProject.setUserId(USER_ID);
    userTagLogForProject.setLinkModuleId(Constants.PROJECTS_FILES);
    userTagLogForProject.setLinkItemId(1);
    userTagLogForProject.setTag("project tag");
    assertNotNull(userTagLogForProject);
    boolean result = userTagLogForProject.insert(db);
    assertTrue("The project tag was not inserted", result);

    //Load the inserted tag from table
    userTagLogList = new UserTagLogList(); 
    userTagLogList.setUserId(userTagLogForProject.getUserId());
    userTagLogList.setLinkModuleId(userTagLogForProject.getLinkModuleId());
    userTagLogList.setLinkItemId(userTagLogForProject.getLinkItemId());
    userTagLogList.setTag(userTagLogForProject.getTag().toLowerCase());
    userTagLogList.buildList(db);
    assertTrue("The user tag log was not inserted", (userTagLogList.size() == 1));
    if (userTagLogList.size() == 1){
      assertNotNull("Tag date not default to current timestamp",userTagLogList.get(0).getTagDate());
    }
    
    //Load the inserted tag from view project_tag_log
    TagLogList projectTagLogList = new TagLogList();
    projectTagLogList.setUserId(userTagLogForProject.getUserId());
    projectTagLogList.setLinkItemId(userTagLogForProject.getLinkItemId());
    projectTagLogList.setTableName(Project.TABLE);
    projectTagLogList.setUniqueField(Project.PRIMARY_KEY);
    projectTagLogList.setTag(userTagLogForProject.getTag().toLowerCase());
    projectTagLogList.buildList(db);
    assertTrue("The project tag log could not be fetched", (projectTagLogList.size() == 1));

    //Test record in the view project_tag
    TagList projectTagList = new TagList();
    projectTagList.setLinkItemId(userTagLogForProject.getLinkItemId());
    projectTagList.setTableName(Project.TABLE);
    projectTagList.setUniqueField(Project.PRIMARY_KEY);
    projectTagList.setTag(userTagLogForProject.getTag().toLowerCase());
    projectTagList.buildList(db);
    assertTrue("The project tag could not be fetched", (projectTagList.size() == 1));
    
    //Delete test tag
    userTagLogForProject.delete(db);

    //Test that it has been deleted from table
    userTagLogList = new UserTagLogList(); 
    userTagLogList.setUserId(userTagLogForProject.getUserId());
    userTagLogList.setLinkModuleId(userTagLogForProject.getLinkModuleId());
    userTagLogList.setLinkItemId(userTagLogForProject.getLinkItemId());
    userTagLogList.setTag(userTagLogForProject.getTag().toLowerCase());
    userTagLogList.buildList(db);
    assertTrue("The project tag log was not deleted", (userTagLogList.size() == 0));

    //Test that the record does not exist in the view.
    projectTagLogList = new TagLogList(); 
    projectTagLogList.setUserId(userTagLogForProject.getUserId());
    projectTagLogList.setLinkItemId(userTagLogForProject.getLinkItemId());
    projectTagLogList.setTableName(Project.TABLE);
    projectTagLogList.setUniqueField(Project.PRIMARY_KEY);
    projectTagLogList.setTag(userTagLogForProject.getTag());
    projectTagLogList.buildList(db);
    assertTrue("The project tag log was not deleted", (projectTagLogList.size() == 0));
   
    projectTagList = new TagList(); 
    projectTagList.setLinkItemId(userTagLogForProject.getLinkItemId());
    projectTagList.setTableName(Project.TABLE);
    projectTagList.setUniqueField(Project.PRIMARY_KEY);
    projectTagList.setTag(userTagLogForProject.getTag().toLowerCase());
    projectTagList.buildList(db);
    assertTrue("The project tag could was not deleted", (projectTagList.size() == 0));
  }
}