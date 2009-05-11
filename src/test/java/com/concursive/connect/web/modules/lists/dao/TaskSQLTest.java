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
import com.concursive.connect.Constants;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.lists.dao.Task;
import com.concursive.connect.web.modules.lists.dao.TaskList;
import com.concursive.connect.web.modules.lists.dao.TaskCategory;
import com.concursive.connect.web.utils.LookupList;
import com.concursive.connect.web.utils.LookupElement;

import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * Tests common projectbadge database access
 *
 * @author lorraine bittner
 * @created May 23, 2008
 */
public class TaskSQLTest extends AbstractConnectionPoolTest {

  protected static final int USER_ID = 1;

  public void testTaskCRUD() throws SQLException {
    int ownerProjectId;
    int linkProjectId;
    int taskCategoryId;
    int taskId;

    // Insert a project that will contain task
    Project ownerProject = new Project();
    ownerProject.setTitle("TaskSQLTest " + System.currentTimeMillis());
    ownerProject.setShortDescription("Project that contains a task");
    ownerProject.setRequestDate(new Timestamp(System.currentTimeMillis()));
    ownerProject.setEnteredBy(USER_ID);
    ownerProject.setModifiedBy(USER_ID);
    ownerProject.insert(db);
    ownerProjectId = ownerProject.getId();
    assertTrue("Inserted ownerProject did not have an id", ownerProjectId > -1);

    // Insert a project that will link to a task
    Project linkProject = new Project();
    linkProject.setTitle("TaskSQLTest " + System.currentTimeMillis());
    linkProject.setShortDescription("Project that links to a task");
    linkProject.setRequestDate(new Timestamp(System.currentTimeMillis()));
    linkProject.setEnteredBy(USER_ID);
    linkProject.setModifiedBy(USER_ID);
    linkProject.insert(db);
    linkProjectId = linkProject.getId();
    assertTrue("Inserted linkProject did not have an id", linkProjectId > -1);

    // Insert task category
    TaskCategory taskCategory = new TaskCategory();
    taskCategory.setLinkModuleId(Constants.TASK_CATEGORY_PROJECTS);
    taskCategory.setLinkItemId(ownerProjectId);
    taskCategory.setDescription("TaskSQLTest " + System.currentTimeMillis());
    assertTrue("TaskCategory was not inserted", taskCategory.insert(db));
    taskCategoryId = taskCategory.getId();

    LookupList priorityList = new LookupList(db, "lookup_task_priority");
    LookupElement priority = null;
    assertTrue("No priorities exist", priorityList.size() > 0);
    priority = priorityList.get(0);

    // Insert task
    Task task = new Task();
    task.setDescription("Task that links to a project");
    task.setLinkModuleId(Constants.TASK_CATEGORY_PROJECTS);
    task.setLinkItemId(linkProjectId);
    task.setProjectId(ownerProjectId);
    task.setCategoryId(taskCategoryId);
    task.setPriority(priority.getId());
    task.setEnteredBy(USER_ID);
    task.setModifiedBy(USER_ID);
    assertTrue("Task was not inserted", task.insert(db));
    taskId = task.getId();
    assertTrue("Inserted task did not have an id", taskId > -1);

    TaskList taskList = new TaskList();
    taskList.setProjectId(ownerProjectId);
    taskList.setLinkModuleId(Constants.TASK_CATEGORY_PROJECTS);
    taskList.setLinkItemId(linkProjectId);
    taskList.buildList(db);
    assertTrue("Task could not be found", taskList.size() > 0);
    task = null;
    for (Task tmpTask : taskList) {
      if (tmpTask.getId() == taskId) {
        task = tmpTask;
        break;
      }
    }
    assertNotNull("Task could not be found", task);

    // Delete the task
    task.delete(db);
    task = null;

    // Try to find the previously deleted badge project link
    taskList = new TaskList();
    taskList.setProjectId(ownerProjectId);
    taskList.setLinkModuleId(Constants.TASK_CATEGORY_PROJECTS);
    taskList.setLinkItemId(linkProjectId);
    taskList.buildList(db);
    for (Task tmpTask : taskList) {
      assertTrue("Task exists when it shouldn't", tmpTask.getId() != taskId);
    }
    //clean up the task category and projects created
    String basePath = null;
    assertTrue("TaskCategory was not deleted", taskCategory.delete(db));
    assertTrue("linkProject was not deleted", linkProject.delete(db, basePath));
    assertTrue("ownerProject was not deleted", ownerProject.delete(db, basePath));

  }

}