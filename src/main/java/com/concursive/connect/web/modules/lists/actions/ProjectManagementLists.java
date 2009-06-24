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

import com.concursive.commons.web.mvc.actions.ActionContext;
import com.concursive.connect.Constants;
import com.concursive.connect.cms.portal.dao.ProjectItemList;
import com.concursive.connect.web.controller.actions.GenericAction;
import com.concursive.connect.web.modules.common.social.rating.beans.RatingBean;
import com.concursive.connect.web.modules.common.social.rating.dao.Rating;
import com.concursive.connect.web.modules.documents.beans.FileDownload;
import com.concursive.connect.web.modules.lists.dao.Task;
import com.concursive.connect.web.modules.lists.dao.TaskCategory;
import com.concursive.connect.web.modules.lists.dao.TaskCategoryList;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.login.utils.UserUtils;
import com.concursive.connect.web.modules.members.dao.TeamMember;
import com.concursive.connect.web.modules.members.dao.TeamMemberList;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.utils.LookupList;

import java.sql.Connection;

/**
 * Handles web actions for the Project Management Lists sub module
 *
 * @author matt rajkowski
 * @version $Id: ProjectManagementLists.java,v 2.1 2002/12/23 01:53:34 matt
 *          Exp $
 * @created November 17, 2002
 */
public final class ProjectManagementLists extends GenericAction {

  /**
   * Prepare form for adding or updating a list item
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandAdd(ActionContext context) {
    Exception errorMessage = null;
    Connection db = null;
    String projectId = context.getRequest().getParameter("pid");
    try {
      db = getConnection(context);
      //Load the project
      Project thisProject = retrieveAuthorizedProject(Integer.parseInt(projectId), context);
      if (!hasProjectAccess(context, thisProject.getId(), "project-lists-modify")) {
        return "PermissionError";
      }
      context.getRequest().setAttribute("project", thisProject);
      context.getRequest().setAttribute("IncludeSection", "lists_add");
      //Prepare the category header
      String categoryId = context.getRequest().getParameter("cid");
      if (categoryId == null) {
        categoryId = context.getRequest().getParameter("categoryId");
      }
      TaskCategory thisCategory = new TaskCategory(db, Integer.parseInt(categoryId));
      context.getRequest().setAttribute("category", thisCategory);
      //Prepare the form lookups
      LookupList priorityList = new LookupList(db, "lookup_task_priority");
      context.getRequest().setAttribute("PriorityList", priorityList);

      // Make the assignable users available
      TeamMemberList teamMemberList = new TeamMemberList();
      teamMemberList.setProjectId(thisProject.getId());
      teamMemberList.setMinimumRoleLevel(TeamMember.VIP);
      teamMemberList.buildList(db);
      context.getRequest().setAttribute("teamMemberList", teamMemberList);

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

      ProjectItemList assignedPriorityList = new ProjectItemList();
      assignedPriorityList.setProjectId(thisProject.getId());
      assignedPriorityList.buildList(db, ProjectItemList.LIST_ASSIGNED_PRIORITY);
      context.getRequest().setAttribute("assignedPriorityList", assignedPriorityList);

      //Prepare the form object
      //Task thisTask = (Task) context.getFormBean();
    } catch (Exception e) {
      errorMessage = e;
    } finally {
      this.freeConnection(context, db);
    }
    if (errorMessage == null) {
      return ("ProjectCenterOK");
    } else {
      context.getRequest().setAttribute("Error", errorMessage);
      return ("SystemError");
    }
  }


  /**
   * Action to insert a new list item
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandSave(ActionContext context) {
    Connection db = null;
    int resultCount = 0;
    boolean recordInserted = false;
    try {
      db = this.getConnection(context);
      // Verify the user has access to the project
      String projectId = context.getRequest().getParameter("pid");
      Project thisProject = retrieveAuthorizedProject(Integer.parseInt(projectId), context);
      if (!hasProjectAccess(context, thisProject.getId(), "project-lists-modify")) {
        return "PermissionError";
      }
      context.getRequest().setAttribute("project", thisProject);
      // Process the list item
      Task thisTask = (Task) context.getFormBean();
      boolean newTask = (thisTask.getId() == -1);
      if (newTask) {
        thisTask.setEnteredBy(getUserId(context));
      }
      thisTask.setModifiedBy(getUserId(context));
      thisTask.setProjectId(thisProject.getId());
      // Verify the specified category is in the same project
      TaskCategoryList list = new TaskCategoryList();
      list.setProjectId(thisProject.getId());
      list.setCategoryId(thisTask.getCategoryId());
      list.buildList(db);
      if (list.size() == 0) {
        return "PermissionError";
      }
      context.getRequest().setAttribute("category", list.get(0));
      // Insert or update...
      if (newTask) {
        recordInserted = thisTask.insert(db);
        if (recordInserted) {
          indexAddItem(context, thisTask);
        }
      } else {
        resultCount = thisTask.update(db);
        indexAddItem(context, thisTask);
      }
      if (!recordInserted && resultCount < 1) {
        processErrors(context, thisTask.getErrors());
      }
    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return ("SystemError");
    } finally {
      this.freeConnection(context, db);
    }
    if (recordInserted) {
      if ("true".equals(context.getRequest().getParameter("donew"))) {
        context.getRequest().removeAttribute("Task");
        return (executeCommandAdd(context));
      }
    }
    if (recordInserted || resultCount == 1) {
      return ("SaveOK");
    }
    return executeCommandAdd(context);
  }


  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandModify(ActionContext context) {
    Connection db = null;
    try {
      String projectId = context.getRequest().getParameter("pid");
      String taskId = context.getRequest().getParameter("id");
      db = this.getConnection(context);
      //Verify the project permissions
      Project thisProject = retrieveAuthorizedProject(Integer.parseInt(projectId), context);
      if (!hasProjectAccess(context, thisProject.getId(), "project-lists-modify")) {
        return "PermissionError";
      }
      Task thisTask = new Task(db, Integer.parseInt(taskId));
      if (thisTask.getProjectId() != thisProject.getId()) {
        return "PermissionError";
      }
      context.getRequest().setAttribute("Task", thisTask);
    } catch (Exception errorMessage) {
      context.getRequest().setAttribute("Error", errorMessage);
      return ("SystemError");
    } finally {
      this.freeConnection(context, db);
    }
    return executeCommandAdd(context);
  }


  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandDetails(ActionContext context) {
    Connection db = null;
    try {
      String projectId = context.getRequest().getParameter("pid");
      String taskId = context.getRequest().getParameter("id");
      db = this.getConnection(context);
      //Verify the project permissions
      Project thisProject = retrieveAuthorizedProject(Integer.parseInt(projectId), context);
      if (!hasProjectAccess(context, thisProject.getId(), "project-lists-view")) {
        return "PermissionError";
      }
      Task thisTask = new Task(db, Integer.parseInt(taskId));
      if (thisTask.getProjectId() != thisProject.getId()) {
        return "PermissionError";
      }
      context.getRequest().setAttribute("Task", thisTask);
      return ("DetailsOK");
    } catch (Exception errorMessage) {
      context.getRequest().setAttribute("Error", errorMessage);
      return ("SystemError");
    } finally {
      this.freeConnection(context, db);
    }
  }


  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandDelete(ActionContext context) {
    Exception errorMessage = null;
    Connection db = null;
    String projectId = context.getRequest().getParameter("pid");
    String taskId = context.getRequest().getParameter("id");
    boolean recordDeleted = false;
    try {
      db = getConnection(context);
      Project thisProject = retrieveAuthorizedProject(Integer.parseInt(projectId), context);
      if (!hasProjectAccess(context, thisProject.getId(), "project-lists-modify")) {
        return "PermissionError";
      }
      Task thisTask = new Task(db, Integer.parseInt(taskId));
      thisTask.setProjectId(thisProject.getId());
      recordDeleted = thisTask.delete(db);
      indexDeleteItem(context, thisTask);
      if (!recordDeleted) {
        processErrors(context, thisTask.getErrors());
      }
      context.getRequest().setAttribute("task", thisTask);
    } catch (Exception e) {
      errorMessage = e;
    } finally {
      freeConnection(context, db);
    }
    if (errorMessage == null) {
      return "DeleteOK";
    } else {
      context.getRequest().setAttribute("Error", errorMessage);
      return ("SystemError");
    }
  }


  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandMove(ActionContext context) {
    Exception errorMessage = null;
    Connection db = null;
    //Parameters
    String projectId = context.getRequest().getParameter("pid");
    String taskId = context.getRequest().getParameter("id");
    try {
      db = getConnection(context);
      //Load the project
      Project thisProject = retrieveAuthorizedProject(Integer.parseInt(projectId), context);
      if (!hasProjectAccess(context, thisProject.getId(), "project-lists-modify")) {
        return "PermissionError";
      }
      context.getRequest().setAttribute("project", thisProject);
      //Load the category list
      TaskCategoryList categoryList = new TaskCategoryList();
      categoryList.setProjectId(thisProject.getId());
      categoryList.buildList(db);
      context.getRequest().setAttribute("categoryList", categoryList);
      //Load the task
      Task thisTask = new Task(db, Integer.parseInt(taskId));
      if (thisTask.getProjectId() != thisProject.getId()) {
        return "PermissionError";
      }
      context.getRequest().setAttribute("Task", thisTask);
    } catch (Exception e) {
      errorMessage = e;
    } finally {
      freeConnection(context, db);
    }
    if (errorMessage == null) {
      return "MoveOK";
    } else {
      context.getRequest().setAttribute("Error", errorMessage);
      return ("SystemError");
    }
  }


  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandSaveMove(ActionContext context) {
    Connection db = null;
    //Parameters
    String projectId = context.getRequest().getParameter("pid");
    String newCategoryId = context.getRequest().getParameter("cid");
    String taskId = context.getRequest().getParameter("id");
    try {
      db = getConnection(context);
      // Load the project
      Project thisProject = retrieveAuthorizedProject(Integer.parseInt(projectId), context);
      if (!hasProjectAccess(context, thisProject.getId(), "project-lists-modify")) {
        return "PermissionError";
      }
      context.getRequest().setAttribute("project", thisProject);
      // Load the task that is being moved
      Task thisTask = new Task(db, Integer.parseInt(taskId));
      if (thisTask.getProjectId() != thisProject.getId()) {
        return "PermissionError";
      }
      // Verify the new category belongs to this project
      TaskCategoryList list = new TaskCategoryList();
      list.setProjectId(thisProject.getId());
      list.buildList(db);
      TaskCategory newCategory = list.getCategoryFromId(Integer.parseInt(newCategoryId));
      if (newCategory == null) {
        return "PermissionError";
      }
      // Verify the current category belongs to this project
      TaskCategory category = list.getCategoryFromId(thisTask.getCategoryId());
      if (category == null) {
        return "PermissionError";
      }
      // The category to redirect to
      context.getRequest().setAttribute("category", category);
      // Update the category
      thisTask.updateCategoryId(db, newCategory.getId());
      return "SaveMoveOK";
    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return ("SystemError");
    } finally {
      freeConnection(context, db);
    }
  }


  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandAddCategory(ActionContext context) {
    Exception errorMessage = null;
    String projectId = context.getRequest().getParameter("pid");
    Connection db = null;
    try {
      db = getConnection(context);
      //Load the project
      Project thisProject = retrieveAuthorizedProject(Integer.parseInt(projectId), context);
      if (!hasProjectAccess(context, thisProject.getId(), "project-lists-add")) {
        return "PermissionError";
      }
      context.getRequest().setAttribute("project", thisProject);
      context.getRequest().setAttribute("IncludeSection", "lists_categories_add");
      //Process the category
      String categoryId = context.getParameter("cid");
      if (categoryId != null && !"".equals(categoryId)) {
        TaskCategoryList list = new TaskCategoryList();
        list.setProjectId(thisProject.getId());
        list.setCategoryId(categoryId);
        list.buildList(db);
        context.getRequest().setAttribute("category", list.get(0));
      }
    } catch (Exception e) {
      errorMessage = e;
    } finally {
      this.freeConnection(context, db);
    }
    if (errorMessage == null) {
      return ("ProjectCenterOK");
    } else {
      context.getRequest().setAttribute("Error", errorMessage);
      return ("SystemError");
    }
  }


  /**
   * Action to insert a new list category
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandInsertCategory(ActionContext context) {
    Connection db = null;
    boolean recordInserted = false;
    String projectId = context.getRequest().getParameter("pid");
    try {
      db = this.getConnection(context);
      // Load the project
      Project thisProject = retrieveAuthorizedProject(Integer.parseInt(projectId), context);
      if (!hasProjectAccess(context, thisProject.getId(), "project-lists-add")) {
        return "PermissionError";
      }
      context.getRequest().setAttribute("project", thisProject);
      context.getRequest().setAttribute("IncludeSection", "lists_categories_add");
      // Process the category
      TaskCategory newCategory = (TaskCategory) context.getFormBean();
      newCategory.setLinkModuleId(Constants.TASK_CATEGORY_PROJECTS);
      newCategory.setLinkItemId(thisProject.getId());
      recordInserted = newCategory.insert(db);
      indexAddItem(context, newCategory);
      if (!recordInserted) {
        processErrors(context, newCategory.getErrors());
      }
    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return ("SystemError");
    } finally {
      this.freeConnection(context, db);
    }
    if (recordInserted) {
      return ("SaveOK");
    }
    return executeCommandAddCategory(context);
  }


  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandUpdateCategory(ActionContext context) {
    Connection db = null;
    int resultCount = 0;
    String projectId = context.getRequest().getParameter("pid");
    try {
      db = this.getConnection(context);
      //Load the project
      Project thisProject = retrieveAuthorizedProject(Integer.parseInt(projectId), context);
      if (!hasProjectAccess(context, thisProject.getId(), "project-lists-edit")) {
        return "PermissionError";
      }
      context.getRequest().setAttribute("project", thisProject);
      context.getRequest().setAttribute("IncludeSection", "lists_categories_add");
      // Process the task category
      TaskCategory updatedCategory = (TaskCategory) context.getFormBean();
      // Verify the previous category
      TaskCategoryList list = new TaskCategoryList();
      list.setProjectId(thisProject.getId());
      list.setCategoryId(updatedCategory.getId());
      list.buildList(db);
      TaskCategory previousCategory = list.get(0);
      if (updatedCategory.getId() != previousCategory.getId()) {
        return "PermissionError";
      }
      resultCount = updatedCategory.update(db);
      if (resultCount == -1) {
        processErrors(context, updatedCategory.getErrors());
      } else {
        indexAddItem(context, updatedCategory);
        processUpdateHook(context, previousCategory, updatedCategory);
      }
      context.getRequest().setAttribute("category", updatedCategory);
    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return ("SystemError");
    } finally {
      this.freeConnection(context, db);
    }
    if (resultCount == -1) {
      return executeCommandAddCategory(context);
    } else if (resultCount == 1) {
      return ("SaveOK");
    }
    context.getRequest().setAttribute("Error", NOT_UPDATED_MESSAGE);
    return ("UserError");
  }


  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandDeleteCategory(ActionContext context) {
    Connection db = null;
    boolean recordDeleted = false;
    //Parameters
    String projectId = context.getRequest().getParameter("pid");
    String categoryId = context.getRequest().getParameter("cid");
    try {
      db = getConnection(context);
      // Load the project
      Project thisProject = retrieveAuthorizedProject(Integer.parseInt(projectId), context);
      if (!hasProjectAccess(context, thisProject.getId(), "project-lists-delete")) {
        return "PermissionError";
      }
      context.getRequest().setAttribute("project", thisProject);
      // Verify the previous category
      TaskCategoryList list = new TaskCategoryList();
      list.setProjectId(thisProject.getId());
      list.setCategoryId(categoryId);
      list.buildList(db);
      TaskCategory thisCategory = list.getCategoryFromId(list.getCategoryId());
      if (thisCategory != null) {
        recordDeleted = thisCategory.delete(db);
        if (recordDeleted) {
          indexDeleteItem(context, thisCategory);
        }
      }
    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return ("SystemError");
    } finally {
      freeConnection(context, db);
    }
    return "DeleteOK";
  }


  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandMarkItem(ActionContext context) {
    Connection db = null;
    //Process the params
    String projectId = context.getRequest().getParameter("pid");
    String taskId = context.getRequest().getParameter("id");
    String newState = context.getRequest().getParameter("check");
    try {
      db = this.getConnection(context);
      //Load the project
      Project thisProject = retrieveAuthorizedProject(Integer.parseInt(projectId), context);
      if (!hasProjectAccess(context, thisProject.getId(), "project-lists-modify")) {
        return "PermissionError";
      }
      Task thisTask = new Task(db, Integer.parseInt(taskId));
      if (thisTask.getProjectId() != thisProject.getId()) {
        return "PermissionError";
      }
      //Toggle the list item
      if ("on".equals(newState)) {
        Task.markComplete(db, thisTask.getId());
      } else {
        Task.markIncomplete(db, thisTask.getId());
      }
    } catch (Exception errorMessage) {
      context.getRequest().setAttribute("Error", errorMessage);
      return ("SystemError");
    } finally {
      this.freeConnection(context, db);
    }
    // Send the new checkmark
    try {
      if ("on".equals(newState)) {
        FileDownload.sendFile(context, context.getServletContext().getResourceAsStream("/images/box-checked.gif"), "image/gif", "box-checked.gif");
      } else {
        FileDownload.sendFile(context, context.getServletContext().getResourceAsStream("/images/box.gif"), "image/gif", "box.gif");
      }
    } catch (Exception error) {
      System.out.println("ProjectManagementLists-> markItem error: " + error.getMessage());
    }
    return null;
  }

  public String executeCommandSetRating(ActionContext context) {
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
      // Load the task
      String taskId = context.getRequest().getParameter("id");
      Task thisTask = new Task(db, Integer.parseInt(taskId));
      if (thisTask.getProjectId() != thisProject.getId()) {
        return "PermissionError";
      }
      // Save the rating for this user, or update their rating
      String vote = context.getRequest().getParameter("v");
      // Cast the user's vote
      RatingBean thisRating =
          Rating.save(db, getUserId(context), thisProject.getId(), thisTask.getId(), vote, "task", "task_id", Constants.UNDEFINED);
      context.getRequest().setAttribute("ratingBean", thisRating);
      context.getRequest().setAttribute("showText", false);
      context.getRequest().setAttribute("url", context.getRequest().getContextPath() +
          "/ProjectManagementLists.do?command=SetRating&pid=" + thisTask.getProjectId() + "&id=" + thisTask.getId() + "&v=${vote}&out=text");
      return "RatingOK";
    } catch (Exception errorMessage) {
      context.getRequest().setAttribute("Error", errorMessage);
      return ("SystemError");
    } finally {
      this.freeConnection(context, db);
    }
  }

}

