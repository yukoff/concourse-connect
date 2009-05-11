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

package com.concursive.connect.web.modules.lists.actions;

import com.concursive.commons.objects.ObjectUtils;
import com.concursive.commons.web.mvc.actions.ActionContext;
import com.concursive.connect.Constants;
import com.concursive.connect.cms.portal.dao.ProjectItem;
import com.concursive.connect.cms.portal.dao.ProjectItemList;
import com.concursive.connect.web.controller.actions.GenericAction;
import com.concursive.connect.web.modules.lists.dao.Task;
import com.concursive.connect.web.modules.lists.dao.TaskCategory;
import com.concursive.connect.web.modules.lists.dao.TaskCategoryList;
import com.concursive.connect.web.modules.lists.dao.TaskList;
import com.concursive.connect.web.modules.lists.utils.TaskUtils;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.login.utils.UserUtils;
import com.concursive.connect.web.modules.members.dao.TeamMember;
import com.concursive.connect.web.modules.members.dao.TeamMemberList;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.utils.TrailMap;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Handles web actions for the Project Management Lists sub module
 *
 * @author matt rajkowski
 * @version $Id: ProjectManagementListsBuckets.java,v 2.1 2002/12/23 01:53:34 matt
 *          Exp $
 * @created December 27, 2007
 */
public final class ProjectManagementListsBuckets extends GenericAction {


  public String executeCommandCategories(ActionContext context) {
    Connection db = null;
    // Parameters
    String projectId = context.getRequest().getParameter("pid");
    try {
      db = getConnection(context);
      // Load the project
      Project thisProject = retrieveAuthorizedProject(Integer.parseInt(projectId), context);
      if (!hasProjectAccess(context, thisProject.getId(), "project-lists-view")) {
        return "PermissionError";
      }
      context.getRequest().setAttribute("project", thisProject);
      context.getRequest().setAttribute("IncludeSection", "lists_buckets_categories");
      // Build the list of categories
      TaskCategoryList categoryList = new TaskCategoryList();
      categoryList.setProjectId(thisProject.getId());
      categoryList.buildList(db);
      context.getRequest().setAttribute("categoryList", categoryList);

      // Build the list items for the selected category (or all)
      TaskList outlineList = new TaskList();
      outlineList.setProjectId(thisProject.getId());
      outlineList.setComplete(Constants.FALSE);
      outlineList.buildList(db);
      HashMap<Integer, String> taskUrlMap = new HashMap<Integer, String>();
      for (Task t : outlineList) {
        String linkItemUrl = TaskUtils.getLinkItemUrl(ctx(context), t);
        if (linkItemUrl != null) {
          taskUrlMap.put(t.getId(), linkItemUrl);
        }
      }
      context.getRequest().setAttribute("taskUrlMap", taskUrlMap);
      context.getRequest().setAttribute("outlineList", outlineList);

      return ("ProjectCenterOK");
    } catch (Exception errorMessage) {
      context.getRequest().setAttribute("Error", errorMessage);
      return ("SystemError");
    } finally {
      this.freeConnection(context, db);
    }
  }

  public String executeCommandMoveCategory(ActionContext context) {
    Connection db = null;
    //Parameters
    String projectId = context.getRequest().getParameter("pid");
    String taskValue = context.getRequest().getParameter("id");
    String categoryValue = context.getRequest().getParameter("cid");
    try {
      db = getConnection(context);
      //Load the project
      Project thisProject = retrieveAuthorizedProject(Integer.parseInt(projectId), context);
      if (!hasProjectAccess(context, thisProject.getId(), "project-lists-modify")) {
        return "PermissionError";
      }
      // Load the task
      Task thisTask = new Task(db, Integer.parseInt(taskValue.substring(taskValue.indexOf("_") + 1)));
      if (thisTask.getProjectId() != thisProject.getId()) {
        return "PermissionError";
      }
      context.getRequest().setAttribute("task", thisTask);

      TaskCategoryList categoryList = new TaskCategoryList();
      categoryList.setProjectId(thisProject.getId());
      categoryList.setCategoryId(Integer.parseInt(categoryValue.substring(categoryValue.indexOf("_") + 1)));
      categoryList.buildList(db);
      if (categoryList.size() == 1) {
        TaskCategory thisCategory = (TaskCategory) categoryList.get(0);
        if (thisTask.getCategoryId() != thisCategory.getId()) {
          TaskList.updateCategoryId(db, thisTask.getId(), thisCategory.getId());
        }
      }
      return "BucketMoveOK";
    } catch (Exception errorMessage) {
      context.getRequest().setAttribute("Error", errorMessage);
      return ("SystemError");
    } finally {
      this.freeConnection(context, db);
    }
  }

  public String executeCommandList(ActionContext context) {
    Connection db = null;
    // Parameters
    String projectId = context.getRequest().getParameter("pid");
    String categoryId = context.getRequest().getParameter("cid");
    if (categoryId == null) {
      categoryId = context.getRequest().getParameter("categoryId");
    }
    String table = context.getRequest().getParameter("table");
    if (table == null || "null".equals(table)) {
      table = "functional_area";
    }
    String value = context.getRequest().getParameter("value");
    try {
      db = getConnection(context);
      // Load the project
      Project thisProject = retrieveAuthorizedProject(Integer.parseInt(projectId), context);
      if (!hasProjectAccess(context, thisProject.getId(), "project-lists-view")) {
        return "PermissionError";
      }
      context.getRequest().setAttribute("project", thisProject);
      context.getRequest().setAttribute("IncludeSection", "lists_buckets");

      // Establish a trailMap per project
      TrailMap trailMap = new TrailMap();
      trailMap.addItems(context.getRequest().getParameter("trail"));
      if (value != null) {
        trailMap.addItems(value);
      }
      context.getRequest().setAttribute("trailMap", trailMap);

      // Load the category
      TaskCategory thisCategory = new TaskCategory(db, Integer.parseInt(categoryId));
      context.getRequest().setAttribute("category", thisCategory);

      // Create an unset column
      ProjectItem unsetItem = new ProjectItem();
      unsetItem.setName("(unset)");
      unsetItem.setId(-1);
      unsetItem.setProjectId(thisProject.getId());

      // Build the values in which the item can be moved between
      ProjectItemList itemList = new ProjectItemList();
      itemList.setProjectId(projectId);
      itemList.setEnabled(Constants.TRUE);
      if ("owner".equals(table)) {
        // Convert the team to ProjectItems to use in bucket list
        TeamMemberList team = new TeamMemberList();
        team.setProjectId(thisProject.getId());
        team.setMinimumRoleLevel(TeamMember.VIP);
        team.buildList(db);
        Iterator i = team.iterator();
        while (i.hasNext()) {
          TeamMember thisMember = (TeamMember) i.next();
          User thisUser = UserUtils.loadUser(thisMember.getUserId());
          ProjectItem item = new ProjectItem();
          item.setId(thisMember.getUserId());
          item.setName(thisUser.getNameFirstLast());
          itemList.add(item);
        }
        itemList.setObjectKeyProperty("owner");
      } else {
        itemList.buildList(db, "lookup_task_" + table);
        itemList.setObjectKeyProperty(TaskList.getPropertyKey("lookup_task_" + table));
      }
      itemList.add(0, unsetItem);
      context.getRequest().setAttribute("itemList", itemList);

      // Build the list items for the selected category
      TaskList outlineList = new TaskList();
      outlineList.setProjectId(thisProject.getId());
      outlineList.setCategoryId(Integer.parseInt(categoryId));
      trailMap.applyFilters(outlineList);
      outlineList.setComplete(Constants.FALSE);
      outlineList.buildList(db);
      HashMap<Integer, String> taskUrlMap = new HashMap<Integer, String>();
      for (Task t : outlineList) {
        String linkItemUrl = TaskUtils.getLinkItemUrl(ctx(context), t);
        if (linkItemUrl != null) {
          taskUrlMap.put(t.getId(), linkItemUrl);
        }
      }
      context.getRequest().setAttribute("taskUrlMap", taskUrlMap);
      context.getRequest().setAttribute("outlineList", outlineList);

      // Build lookup lists
      ProjectItemList functionalAreaList = new ProjectItemList();
      functionalAreaList.setProjectId(thisProject.getId());
      functionalAreaList.buildList(db, ProjectItemList.LIST_FUNCTIONAL_AREA);
      functionalAreaList.add(0, unsetItem);
      context.getRequest().setAttribute("functionalAreaList", functionalAreaList);

      ProjectItemList complexityList = new ProjectItemList();
      complexityList.setProjectId(thisProject.getId());
      complexityList.buildList(db, ProjectItemList.LIST_COMPLEXITY);
      complexityList.add(0, unsetItem);
      context.getRequest().setAttribute("complexityList", complexityList);

      ProjectItemList businessValueList = new ProjectItemList();
      businessValueList.setProjectId(thisProject.getId());
      businessValueList.buildList(db, ProjectItemList.LIST_VALUE);
      businessValueList.add(0, unsetItem);
      context.getRequest().setAttribute("businessValueList", businessValueList);

      ProjectItemList targetSprintList = new ProjectItemList();
      targetSprintList.setProjectId(thisProject.getId());
      targetSprintList.buildList(db, ProjectItemList.LIST_TARGET_SPRINT);
      targetSprintList.add(0, unsetItem);
      context.getRequest().setAttribute("targetSprintList", targetSprintList);

      ProjectItemList targetReleaseList = new ProjectItemList();
      targetReleaseList.setProjectId(thisProject.getId());
      targetReleaseList.buildList(db, ProjectItemList.LIST_TARGET_RELEASE);
      targetReleaseList.add(0, unsetItem);
      context.getRequest().setAttribute("targetReleaseList", targetReleaseList);

      ProjectItemList statusList = new ProjectItemList();
      statusList.setProjectId(thisProject.getId());
      statusList.buildList(db, ProjectItemList.LIST_STATUS);
      statusList.add(0, unsetItem);
      context.getRequest().setAttribute("statusList", statusList);

      ProjectItemList loeRemainingList = new ProjectItemList();
      loeRemainingList.setProjectId(thisProject.getId());
      loeRemainingList.buildList(db, ProjectItemList.LIST_LOE_REMAINING);
      loeRemainingList.add(0, unsetItem);
      context.getRequest().setAttribute("loeRemainingList", loeRemainingList);

      return ("ProjectCenterOK");
    } catch (Exception errorMessage) {
      context.getRequest().setAttribute("Error", errorMessage);
      return ("SystemError");
    } finally {
      this.freeConnection(context, db);
    }
  }

  public String executeCommandMove(ActionContext context) {
    Connection db = null;
    //Parameters
    String projectId = context.getRequest().getParameter("pid");
    String taskValue = context.getRequest().getParameter("id");
    String codeValue = context.getRequest().getParameter("columnId");
    codeValue = codeValue.substring(codeValue.indexOf("_") + 1);
    String key = context.getRequest().getParameter("key");
    try {
      db = getConnection(context);
      //Load the project
      Project thisProject = retrieveAuthorizedProject(Integer.parseInt(projectId), context);
      if (!hasProjectAccess(context, thisProject.getId(), "project-lists-modify")) {
        return "PermissionError";
      }
      // Load the task
      Task thisTask = new Task(db, Integer.parseInt(taskValue.substring(taskValue.indexOf("_") + 1)));
      if (thisTask.getProjectId() != thisProject.getId()) {
        return "PermissionError";
      }
      // Apply the modified value
      ObjectUtils.setParam(thisTask, key, codeValue);
      thisTask.update(db);
      // TODO: ProcessHook

      context.getRequest().setAttribute("task", thisTask);
      return "BucketMoveOK";
    } catch (Exception errorMessage) {
      context.getRequest().setAttribute("Error", errorMessage);
      return ("SystemError");
    } finally {
      this.freeConnection(context, db);
    }
  }

  public String executeCommandAdd(ActionContext context) {
    Connection db = null;
    //Parameters
    String projectId = context.getRequest().getParameter("pid");
    String categoryId = context.getRequest().getParameter("cid");
    String description = context.getRequest().getParameter("description");
    //String taskValue = context.getRequest().getParameter("id");
    try {
      db = getConnection(context);
      //Load the project
      Project thisProject = retrieveAuthorizedProject(Integer.parseInt(projectId), context);
      if (!hasProjectAccess(context, thisProject.getId(), "project-lists-modify")) {
        return "PermissionError";
      }
      // Establish a trailMap per project
      TrailMap trailMap = new TrailMap();
      trailMap.addItems(context.getRequest().getParameter("trail"));
      // Load the task
      //Task thisTask = new Task(db, Integer.parseInt(taskValue.substring(taskValue.indexOf("_") + 1)));
      Task thisTask = new Task();
      thisTask.setProjectId(thisProject.getId());
      thisTask.setDescription(description);
      thisTask.setCategoryId(categoryId);
      thisTask.setPriority(1);
      thisTask.setEnteredBy(getUserId(context));
      thisTask.setModifiedBy(getUserId(context));
      trailMap.applyFilters(thisTask, "0");
      thisTask.insert(db);
      indexAddItem(context, thisTask);
      context.getRequest().setAttribute("task", thisTask);
      // Set the coded value on the task and save the task
      return "BucketAddOK";
    } catch (Exception errorMessage) {
      context.getRequest().setAttribute("Error", errorMessage);
      return ("SystemError");
    } finally {
      this.freeConnection(context, db);
    }
  }

  public String executeCommandDelete(ActionContext context) {
    Connection db = null;
    //Parameters
    String projectId = context.getRequest().getParameter("pid");
    String categoryId = context.getRequest().getParameter("cid");
    String taskId = context.getRequest().getParameter("id");
    try {
      db = getConnection(context);
      //Load the project
      Project thisProject = retrieveAuthorizedProject(Integer.parseInt(projectId), context);
      if (!hasProjectAccess(context, thisProject.getId(), "project-lists-modify")) {
        return "PermissionError";
      }
      // Load the task
      Task thisTask = new Task(db, Integer.parseInt(taskId));
      if (thisTask.getProjectId() == thisProject.getId()) {
        indexDeleteItem(context, thisTask);
        thisTask.delete(db);
        context.getRequest().setAttribute("task", thisTask);
      }
      return "BucketDeleteOK";
    } catch (Exception errorMessage) {
      context.getRequest().setAttribute("Error", errorMessage);
      return ("SystemError");
    } finally {
      this.freeConnection(context, db);
    }
  }

  public String executeCommandTooltip(ActionContext context) {
    Connection db = null;
    //Parameters
    String xId = context.getRequest().getParameter("id");
    String taskId = xId.substring(xId.indexOf("_") + 1);
    try {
      db = getConnection(context);
      // Load the task
      Task thisTask = new Task(db, Integer.parseInt(taskId));
      // Load the project
      Project thisProject = retrieveAuthorizedProject(thisTask.getProjectId(), context);
      if (!hasProjectAccess(context, thisProject.getId(), "project-lists-view")) {
        return "PermissionError";
      }
      context.getRequest().setAttribute("task", thisTask);

      // Build lookup lists
      ProjectItemList functionalAreaList = new ProjectItemList();
      functionalAreaList.setProjectId(thisProject.getId());
      functionalAreaList.buildList(db, ProjectItemList.LIST_FUNCTIONAL_AREA);
      context.getRequest().setAttribute("functionalAreaList", functionalAreaList);

      ProjectItemList complexityList = new ProjectItemList();
      complexityList.setProjectId(thisProject.getId());
      complexityList.buildList(db, ProjectItemList.LIST_COMPLEXITY);
      context.getRequest().setAttribute("complexityList", complexityList);

      ProjectItemList businessValueList = new ProjectItemList();
      businessValueList.setProjectId(thisProject.getId());
      businessValueList.buildList(db, ProjectItemList.LIST_VALUE);
      context.getRequest().setAttribute("businessValueList", businessValueList);

      ProjectItemList targetSprintList = new ProjectItemList();
      targetSprintList.setProjectId(thisProject.getId());
      targetSprintList.buildList(db, ProjectItemList.LIST_TARGET_SPRINT);
      context.getRequest().setAttribute("targetSprintList", targetSprintList);

      ProjectItemList targetReleaseList = new ProjectItemList();
      targetReleaseList.setProjectId(thisProject.getId());
      targetReleaseList.buildList(db, ProjectItemList.LIST_TARGET_RELEASE);
      context.getRequest().setAttribute("targetReleaseList", targetReleaseList);

      ProjectItemList statusList = new ProjectItemList();
      statusList.setProjectId(thisProject.getId());
      statusList.buildList(db, ProjectItemList.LIST_STATUS);
      context.getRequest().setAttribute("statusList", statusList);

      ProjectItemList loeRemainingList = new ProjectItemList();
      loeRemainingList.setProjectId(thisProject.getId());
      loeRemainingList.buildList(db, ProjectItemList.LIST_LOE_REMAINING);
      context.getRequest().setAttribute("loeRemainingList", loeRemainingList);

      return "BucketTooltipOK";
    } catch (Exception errorMessage) {
      context.getRequest().setAttribute("Error", errorMessage);
      return ("SystemError");
    } finally {
      this.freeConnection(context, db);
    }
  }

}