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
package com.concursive.connect.web.modules.issues.dao;

import com.concursive.commons.db.AbstractConnectionPoolTest;
import com.concursive.connect.web.modules.issues.dao.TicketCategoryTemplate;
import com.concursive.connect.web.modules.issues.dao.TicketCategoryTemplateList;
import com.concursive.connect.web.modules.profile.dao.ProjectCategory;

import java.sql.SQLException;

/**
 * Tests tag database access
 *
 * @author Kailash Bhoopalam
 * @created August 12, 2008
 */
public class TicketCategoryTemplateSQLTest extends AbstractConnectionPoolTest {

  protected static final int USER_ID = 1;

  public void testTicketCategoryTemplateCRUD() throws SQLException {

    // Insert a project category
  	ProjectCategory projectCategory = new ProjectCategory();
  	projectCategory.setDescription("Businesses");
  	projectCategory.setEnabled(true);
  	projectCategory.setLevel(10);
  	boolean result = projectCategory.insert(db);
    assertTrue("The project category was not inserted", result);

    //Load the inserted tag from table
    TicketCategoryTemplate  ticketCategoryTemplate = new TicketCategoryTemplate();
    ticketCategoryTemplate.setProjectCategoryId(projectCategory.getId());
    ticketCategoryTemplate.setEnabled(true);
    ticketCategoryTemplate.setTicketCategories("Test Forum1");
    result = ticketCategoryTemplate.insert(db);
    assertTrue("The ticket category template was not inserted", result);
    int templateId = ticketCategoryTemplate.getId();
    
    ticketCategoryTemplate = new TicketCategoryTemplate();
    ticketCategoryTemplate.queryRecord(db, templateId);
    assertTrue("The ticket category template was not inserted", (ticketCategoryTemplate.getEntered() != null));
    
    
    ticketCategoryTemplate.setTicketCategories("Test Forum1 Updated");
    int resultValue = ticketCategoryTemplate.update(db);
    assertTrue("The ticket category template was not updated, update did not return 1", (resultValue == 1));
    
    ticketCategoryTemplate.queryRecord(db, templateId);
    assertTrue("The ticket category template was not updated", ("Test Forum1 Updated".equals(ticketCategoryTemplate.getTicketCategories())));
    
    
    TicketCategoryTemplateList ticketCategoryTemplateList = new TicketCategoryTemplateList();
    ticketCategoryTemplateList.setTicketCategoryTemplateId(ticketCategoryTemplate.getId());
    ticketCategoryTemplateList.buildList(db);
    assertTrue("Query with ticket category template Id failed", (ticketCategoryTemplateList.size() == 1));
    if (ticketCategoryTemplateList.size() == 1){
      assertNotNull("Entered date did not default to current timestamp",ticketCategoryTemplateList.get(0).getEntered());
    }
    
    ticketCategoryTemplateList = new TicketCategoryTemplateList();
    ticketCategoryTemplateList.setProjectCategoryId(projectCategory.getId());
    ticketCategoryTemplateList.buildList(db);
    assertTrue("The ticket category template was not inserted with the correct project category Id", (ticketCategoryTemplateList.size() == 1));
    
    
    ticketCategoryTemplate.delete(db);
    ticketCategoryTemplateList = new TicketCategoryTemplateList();
    ticketCategoryTemplateList.setTicketCategoryTemplateId(ticketCategoryTemplate.getId());
    ticketCategoryTemplateList.buildList(db);
    assertTrue("Discussion Forum template was not deleted", (ticketCategoryTemplateList.size() == 0));

    //Cleanup
    projectCategory.delete(db, null);
  }
}