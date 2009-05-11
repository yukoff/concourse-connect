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
package com.concursive.connect.web.modules.lists.dao;

import com.concursive.commons.db.AbstractConnectionPoolTest;
import com.concursive.connect.web.modules.lists.dao.ListsTemplate;
import com.concursive.connect.web.modules.lists.dao.ListsTemplateList;
import com.concursive.connect.web.modules.profile.dao.ProjectCategory;

import java.sql.SQLException;

/**
 * Tests tag database access
 *
 * @author Kailash Bhoopalam
 * @created August 13, 2008
 */
public class ListsTemplateSQLTest extends AbstractConnectionPoolTest {

  protected static final int USER_ID = 1;

  public void testListsTemplateCRUD() throws SQLException {

    // Insert a project category
  	ProjectCategory projectCategory = new ProjectCategory();
  	projectCategory.setDescription("Businesses");
  	projectCategory.setEnabled(true);
  	projectCategory.setLevel(10);
  	boolean result = projectCategory.insert(db);
    assertTrue("The project category was not inserted", result);

    //Load the inserted tag from table
    ListsTemplate  listsTemplate = new ListsTemplate();
    listsTemplate.setProjectCategoryId(projectCategory.getId());
    listsTemplate.setEnabled(true);
    listsTemplate.setListNames("Test Folder1");
    result = listsTemplate.insert(db);
    assertTrue("The list template was not inserted", result);
    int templateId = listsTemplate.getId();
    
    listsTemplate = new ListsTemplate();
    listsTemplate.queryRecord(db, templateId);
    assertTrue("The list template was not inserted", (listsTemplate.getEntered() != null));
    
    
    listsTemplate.setListNames("Test Folder1 Updated");
    int resultValue = listsTemplate.update(db);
    assertTrue("The list template was not updated, update did not return 1", (resultValue == 1));
    
    listsTemplate.queryRecord(db, templateId);
    assertTrue("The list template was not updated", ("Test Folder1 Updated".equals(listsTemplate.getListNames())));
    
    
    ListsTemplateList listsTemplateList = new ListsTemplateList();
    listsTemplateList.setListTemplateId(listsTemplate.getId());
    listsTemplateList.buildList(db);
    assertTrue("Query with list template Id failed", (listsTemplateList.size() == 1));
    if (listsTemplateList.size() == 1){
      assertNotNull("Entered date did not default to current timestamp",listsTemplateList.get(0).getEntered());
    }
    
    listsTemplateList = new ListsTemplateList();
    listsTemplateList.setProjectCategoryId(projectCategory.getId());
    listsTemplateList.buildList(db);
    assertTrue("The list template was not inserted with the correct project category Id", (listsTemplateList.size() == 1));
    
    
    listsTemplate.delete(db);
    listsTemplateList = new ListsTemplateList();
    listsTemplateList.setListTemplateId(listsTemplate.getId());
    listsTemplateList.buildList(db);
    assertTrue("The list template was not deleted", (listsTemplateList.size() == 0));
    
    //Cleanup
    projectCategory.delete(db, null);
  }
}