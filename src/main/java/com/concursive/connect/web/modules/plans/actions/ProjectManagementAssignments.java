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
package com.concursive.connect.web.modules.plans.actions;

import com.concursive.commons.db.DatabaseUtils;
import com.concursive.commons.text.StringUtils;
import com.concursive.commons.web.mvc.actions.ActionContext;
import com.concursive.connect.web.controller.actions.GenericAction;
import com.concursive.connect.web.modules.members.dao.TeamMember;
import com.concursive.connect.web.modules.members.dao.TeamMemberList;
import com.concursive.connect.web.modules.plans.dao.*;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.utils.HtmlPercentList;
import com.concursive.connect.web.utils.LookupList;
import com.concursive.connect.web.utils.PagedListInfo;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Description of the Class
 *
 * @author matt rajkowski
 * @version $Id: ProjectManagementAssignments.java,v 1.2 2003/02/24 04:00:19
 *          matt Exp $
 * @created November 28, 2001
 */
public final class ProjectManagementAssignments extends GenericAction {

  /**
   * Description of the Method
   *
   * @param context Description of Parameter
   * @return Description of the Returned Value
   */
  public String executeCommandAdd(ActionContext context) {
    Connection db = null;
    //Parameters
    String projectId = context.getRequest().getParameter("pid");
    checkReturnPage(context);
    try {
      db = getConnection(context);
      //Load the project
      Project thisProject = retrieveAuthorizedProject(Integer.parseInt(projectId), context);
      if (!hasProjectAccess(context, thisProject.getId(), "project-plan-outline-modify")) {
        return "PermissionError";
      }
      context.getRequest().setAttribute("project", thisProject);
      context.getRequest().setAttribute(
          "IncludeSection", "assignments_add");
      // Load team member drop-down list
      PagedListInfo projectTeamInfo = new PagedListInfo();
      projectTeamInfo.setItemsPerPage(0);
      projectTeamInfo.setDefaultSort("last_name", null);
      TeamMemberList team = new TeamMemberList();
      team.setProjectId(thisProject.getId());
      team.setPagedListInfo(projectTeamInfo);
      team.buildList(db);
      context.getRequest().setAttribute("teamMemberList", team);
      //Load priority drop-down list
      LookupList priorityList = new LookupList(db, "lookup_project_priority");
      context.getRequest().setAttribute("PriorityList", priorityList);
      //Load status drop-down list
      LookupList statusList = new LookupList(db, "lookup_project_status");
      context.getRequest().setAttribute("StatusList", statusList);
      //Load status percent drop-down list
      HtmlPercentList statusPercentList = new HtmlPercentList();
      context.getRequest().setAttribute(
          "StatusPercentList", statusPercentList);
      //Load LOE drop-down list
      LookupList loeList = new LookupList(db, "lookup_project_loe");
      context.getRequest().setAttribute("LoeList", loeList);
      //Load assignment role drop-down list
      LookupList activityList = new LookupList(
          db, "lookup_project_assignment_role");
      activityList.addItem(-1, "--None--");
      context.getRequest().setAttribute("activityList", activityList);
    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return ("SystemError");
    } finally {
      this.freeConnection(context, db);
    }
    if (context.getRequest().getParameter("popup") != null) {
      return ("PopupOK");
    } else {
      return ("ProjectCenterOK");
    }
  }

  /**
   * Description of the Method
   *
   * @param context Description of Parameter
   * @return Description of the Returned Value
   */
  public String executeCommandSave(ActionContext context) {
    Connection db = null;
    int resultCount = -1;
    boolean recordInserted = false;
    //Parameters
    String projectId = context.getRequest().getParameter("pid");
    checkReturnPage(context);
    Assignment thisAssignment = (Assignment) context.getFormBean();
    try {
      db = getConnection(context);
      // Load the project
      Project thisProject = retrieveAuthorizedProject(Integer.parseInt(projectId), context);
      thisAssignment.setProjectId(thisProject.getId());
      //Check permissions
      if (!hasProjectAccess(context, thisProject.getId(), "project-plan-outline-modify") &&
          !hasProjectAccess(context, thisProject.getId(), "project-plan-view")) {
        return "PermissionError";
      }
      // Only assign to users of the project
      if (!thisAssignment.hasValidTeam(db)) {
        return "PermissionError";
      }
      if (thisAssignment.getId() > 0) {
        thisAssignment.setModifiedBy(getUserId(context));
        resultCount = thisAssignment.update(db);
        // Index some items
        indexAddItem(context, thisAssignment);
        AssignmentNote assignmentNote = thisAssignment.getAssignmentNote();
        if (assignmentNote != null && assignmentNote.isValid()) {
          indexAddItem(context, assignmentNote);
        }
      } else {
        thisAssignment.setEnteredBy(getUserId(context));
        thisAssignment.setModifiedBy(getUserId(context));
        recordInserted = thisAssignment.insert(db);
        // Index some items
        indexAddItem(context, thisAssignment);
        AssignmentNote assignmentNote = thisAssignment.getAssignmentNote();
        if (assignmentNote != null && assignmentNote.isValid()) {
          indexAddItem(context, assignmentNote);
        }
      }
      if (!recordInserted && resultCount < 0) {
        processErrors(context, thisAssignment.getErrors());
      } else {
        context.getRequest().setAttribute("pid", projectId);
      }
    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return ("SystemError");
    } finally {
      freeConnection(context, db);
    }
    if (resultCount == 0) {
      context.getRequest().setAttribute("Error", NOT_UPDATED_MESSAGE);
      return ("UserError");
    } else if (recordInserted || resultCount == 1) {
      if ("true".equals(context.getRequest().getParameter("donew"))) {
        context.getRequest().removeAttribute("Assignment");
        Assignment empty = new Assignment();
        empty.setIndent(thisAssignment.getIndent());
        empty.setPrevIndent(thisAssignment.getIndent());
        empty.setPrevMapId(thisAssignment.getPrevMapId());
        context.getRequest().setAttribute("Assignment", empty);
        return (executeCommandAdd(context));
      }
      if (context.getRequest().getParameter("popup") != null) {
        return "PopupCloseOK";
      } else {
        return ("SaveOK");
      }
    }
    return (executeCommandAdd(context));
  }

  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandDetails(ActionContext context) {
    Connection db = null;
    //Parameters
    String projectId = context.getRequest().getParameter("pid");
    String assignmentId = context.getRequest().getParameter("aid");
    try {
      db = getConnection(context);
      //Load the project
      Project thisProject = retrieveAuthorizedProject(Integer.parseInt(projectId), context);
      if (!hasProjectAccess(context, thisProject.getId(), "project-plan-view")) {
        return "PermissionError";
      }
      context.getRequest().setAttribute("project", thisProject);
      context.getRequest().setAttribute("IncludeSection", "assignments_details");
      //Load the assignment
      Assignment thisAssignment = new Assignment(
          db, Integer.parseInt(assignmentId), thisProject.getId());
      context.getRequest().setAttribute("Assignment", thisAssignment);
      //Load priority drop-down list
      LookupList priorityList = new LookupList(db, "lookup_project_priority");
      context.getRequest().setAttribute("PriorityList", priorityList);
      //Load status drop-down list
      LookupList statusList = new LookupList(db, "lookup_project_status");
      context.getRequest().setAttribute("StatusList", statusList);
      //Load status percent drop-down list
      HtmlPercentList statusPercentList = new HtmlPercentList();
      context.getRequest().setAttribute(
          "StatusPercentList", statusPercentList);
    } catch (Exception errorMessage) {
      context.getRequest().setAttribute("Error", errorMessage);
      return ("SystemError");
    } finally {
      this.freeConnection(context, db);
    }
    if (isPopup(context)) {
      return ("PopupDetailsOK");
    } else {
      return ("ProjectCenterOK");
    }
  }

  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandModify(ActionContext context) {
    Connection db = null;
    //Parameters
    String projectId = context.getRequest().getParameter("pid");
    String assignmentId = context.getRequest().getParameter("aid");
    checkReturnPage(context);
    try {
      db = getConnection(context);
      //Load the project
      Project thisProject = retrieveAuthorizedProject(Integer.parseInt(projectId), context);
      //Check permissions
      if (!hasProjectAccess(context, thisProject.getId(), "project-plan-outline-modify") &&
          !hasProjectAccess(context, thisProject.getId(), "project-plan-view")) {
        return "PermissionError";
      }
      context.getRequest().setAttribute("project", thisProject);
      context.getRequest().setAttribute("IncludeSection", "assignments_add");
      //Load the assignment
      Assignment thisAssignment = new Assignment(
          db, Integer.parseInt(assignmentId), thisProject.getId());
      context.getRequest().setAttribute("Assignment", thisAssignment);
      //Check user permissions
      if (!hasModifyAccess(context, db, thisProject, thisAssignment)) {
        return "DetailsREDIRECT";
      }
      Requirement thisRequirement = new Requirement(db, thisAssignment.getRequirementId());
      if (thisRequirement.getReadOnly()) {
        return "DetailsREDIRECT";
      }
      //Generate form data
      PagedListInfo projectTeamInfo = new PagedListInfo();
      projectTeamInfo.setItemsPerPage(0);
      projectTeamInfo.setDefaultSort("last_name", null);
      TeamMemberList team = new TeamMemberList();
      team.setProjectId(thisProject.getId());
      team.setPagedListInfo(projectTeamInfo);
      team.buildList(db);
      context.getRequest().setAttribute("teamMemberList", team);
      //Load priority drop-down
      LookupList priorityList = new LookupList(db, "lookup_project_priority");
      context.getRequest().setAttribute("PriorityList", priorityList);
      //Load status drop-down
      LookupList statusList = new LookupList(db, "lookup_project_status");
      context.getRequest().setAttribute("StatusList", statusList);
      //Load status percent drop-down list
      HtmlPercentList statusPercentList = new HtmlPercentList();
      context.getRequest().setAttribute(
          "StatusPercentList", statusPercentList);
      //Load LOE drop-down
      LookupList loeList = new LookupList(db, "lookup_project_loe");
      context.getRequest().setAttribute("LoeList", loeList);
      //Load assignment role drop-down list
      LookupList activityList = new LookupList(
          db, "lookup_project_assignment_role");
      activityList.addItem(-1, "--None--");
      context.getRequest().setAttribute("activityList", activityList);
    } catch (Exception errorMessage) {
      context.getRequest().setAttribute("Error", errorMessage);
      return ("SystemError");
    } finally {
      this.freeConnection(context, db);
    }
    if (isPopup(context)) {
      return ("PopupOK");
    } else {
      return ("ProjectCenterOK");
    }
  }

  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandAddFolder(ActionContext context) {
    String projectId = context.getRequest().getParameter("pid");
    checkReturnPage(context);
    Connection db = null;
    try {
      db = getConnection(context);
      Project thisProject = retrieveAuthorizedProject(Integer.parseInt(projectId), context);
      if (!hasProjectAccess(context, thisProject.getId(), "project-plan-outline-modify")) {
        return "PermissionError";
      }
      context.getRequest().setAttribute("project", thisProject);
      context.getRequest().setAttribute("IncludeSection", "assignments_folder_add");
    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return ("SystemError");
    } finally {
      this.freeConnection(context, db);
    }
    if (context.getRequest().getParameter("popup") != null) {
      return ("PopupOK");
    } else {
      return ("ProjectCenterOK");
    }
  }

  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandSaveFolder(ActionContext context) {
    Connection db = null;
    int resultCount = -1;
    boolean recordInserted = false;
    //Parameters
    String projectId = context.getRequest().getParameter("pid");
    checkReturnPage(context);
    AssignmentFolder thisFolder = (AssignmentFolder) context.getFormBean();
    try {
      db = getConnection(context);
      //Load the project
      Project thisProject = retrieveAuthorizedProject(Integer.parseInt(projectId), context);
      if (!hasProjectAccess(context, thisProject.getId(), "project-plan-outline-modify")) {
        return "PermissionError";
      }
      context.getRequest().setAttribute("project", thisProject);
      context.getRequest().setAttribute("IncludeSection", "assignments_add");
      //Process the folder
      thisFolder.setModifiedBy(getUserId(context));
      if (thisFolder.getId() > 0) {
        thisFolder.setProjectId(thisProject.getId());
        resultCount = thisFolder.update(db, context);
        indexAddItem(context, thisFolder);
      } else {
        thisFolder.setEnteredBy(getUserId(context));
        recordInserted = thisFolder.insert(db);
        indexAddItem(context, thisFolder);
      }
      if (!recordInserted && resultCount < 0) {
        processErrors(context, thisFolder.getErrors());
      } else {
        context.getRequest().setAttribute("pid", projectId);
      }
    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return ("SystemError");
    } finally {
      freeConnection(context, db);
    }
    if (resultCount == 0) {
      context.getRequest().setAttribute("Error", NOT_UPDATED_MESSAGE);
      return ("UserError");
    } else if (recordInserted || resultCount == 1) {
      if ("true".equals(context.getRequest().getParameter("donew"))) {
        context.getRequest().removeAttribute("assignmentFolder");
        AssignmentFolder empty = new AssignmentFolder();
        empty.setIndent(thisFolder.getIndent());
        empty.setPrevIndent(thisFolder.getIndent());
        empty.setPrevMapId(thisFolder.getPrevMapId());
        context.getRequest().setAttribute("assignmentFolder", empty);
        return (executeCommandAddFolder(context));
      }
      if (context.getRequest().getParameter("popup") != null) {
        return "PopupCloseOK";
      } else {
        return ("SaveOK");
      }
    }
    return (executeCommandAddFolder(context));
  }

  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandDelete(ActionContext context) {
    Connection db = null;
    String projectId = context.getRequest().getParameter("pid");
    String assignmentId = context.getRequest().getParameter("aid");
    boolean recordDeleted = false;
    try {
      db = getConnection(context);
      Project thisProject = retrieveAuthorizedProject(Integer.parseInt(projectId), context);
      if (!hasProjectAccess(context, thisProject.getId(), "project-plan-outline-modify")) {
        return "PermissionError";
      }

      Assignment thisAssignment = new Assignment(
          db, Integer.parseInt(assignmentId), thisProject.getId());
      recordDeleted = thisAssignment.delete(db);
      indexDeleteItem(context, thisAssignment);
      if (!recordDeleted) {
        processErrors(context, thisAssignment.getErrors());
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
  public String executeCommandMove(ActionContext context) {
    String projectId = context.getRequest().getParameter("pid");
    String requirementId = context.getRequest().getParameter("rid");
    String assignmentId = context.getRequest().getParameter("aid");
    Connection db = null;
    try {
      db = getConnection(context);
      Project thisProject = retrieveAuthorizedProject(Integer.parseInt(projectId), context);
      if (!hasProjectAccess(context, thisProject.getId(), "project-plan-outline-modify")) {
        return "PermissionError";
      }

      Requirement thisRequirement = new Requirement(
          db, Integer.parseInt(requirementId), thisProject.getId());
      thisRequirement.buildFolderHierarchy(db);
      Assignment thisAssignment = new Assignment(
          db, Integer.parseInt(assignmentId), thisProject.getId());
      context.getRequest().setAttribute("project", thisProject);
      context.getRequest().setAttribute("requirement", thisRequirement);
      context.getRequest().setAttribute("assignment", thisAssignment);
    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return ("SystemError");
    } finally {
      freeConnection(context, db);
    }
    return "MoveOK";
  }

  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandSaveMove(ActionContext context) {
    Exception errorMessage = null;
    Connection db = null;
    //Parameters
    String projectId = context.getRequest().getParameter("pid");
    String assignmentId = context.getRequest().getParameter("aid");
    String newFolderId = context.getRequest().getParameter("parent");
    try {
      checkReturnPage(context);
      db = getConnection(context);
      //Load the project and permissions
      Project thisProject = retrieveAuthorizedProject(Integer.parseInt(projectId), context);
      if (!hasProjectAccess(context, thisProject.getId(), "project-plan-outline-modify")) {
        return "PermissionError";
      }
      //Load the assignment
      Assignment thisAssignment = new Assignment(
          db, Integer.parseInt(assignmentId), thisProject.getId());
      thisAssignment.updateFolderId(db, Integer.parseInt(newFolderId));
    } catch (Exception e) {
      errorMessage = e;
    } finally {
      freeConnection(context, db);
    }
    if (errorMessage == null) {
      return "PopupCloseOK";
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
  public String executeCommandDeleteFolder(ActionContext context) {
    Exception errorMessage = null;
    Connection db = null;

    String projectId = context.getRequest().getParameter("pid");
    String folderId = context.getRequest().getParameter("folderId");

    boolean recordDeleted = false;
    try {
      db = getConnection(context);
      Project thisProject = retrieveAuthorizedProject(Integer.parseInt(projectId), context);
      if (!hasProjectAccess(context, thisProject.getId(), "project-plan-outline-modify")) {
        return "PermissionError";
      }
      AssignmentFolder thisFolder = new AssignmentFolder(
          db, Integer.parseInt(folderId), thisProject.getId());
      recordDeleted = thisFolder.delete(db);
      if (!recordDeleted) {
        processErrors(context, thisFolder.getErrors());
      } else {
        indexDeleteItem(context, thisFolder);
        context.getRequest().setAttribute("pid", projectId);
      }
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
  public String executeCommandModifyFolder(ActionContext context) {
    Exception errorMessage = null;

    String projectId = context.getRequest().getParameter("pid");
    String folderId = context.getRequest().getParameter("folderId");
    checkReturnPage(context);

    Connection db = null;
    try {
      db = getConnection(context);
      //Load the project
      Project thisProject = retrieveAuthorizedProject(Integer.parseInt(projectId), context);
      if (!hasProjectAccess(context, thisProject.getId(), "project-plan-outline-modify")) {
        return "PermissionError";
      }
      context.getRequest().setAttribute("project", thisProject);
      context.getRequest().setAttribute("IncludeSection", "assignments_folder_add");
      //Load the folder
      AssignmentFolder thisFolder = new AssignmentFolder(
          db, Integer.parseInt(folderId), thisProject.getId());
      context.getRequest().setAttribute("assignmentFolder", thisFolder);
    } catch (Exception e) {
      errorMessage = e;
    } finally {
      this.freeConnection(context, db);
    }
    if (errorMessage == null) {
      if (context.getRequest().getParameter("popup") != null) {
        return ("PopupOK");
      } else {
        return ("ProjectCenterOK");
      }
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
  public String executeCommandFolderDetails(ActionContext context) {
    String projectId = context.getRequest().getParameter("pid");
    String folderId = context.getRequest().getParameter("folderId");
    Connection db = null;
    try {
      db = getConnection(context);
      //Load the project
      Project thisProject = retrieveAuthorizedProject(Integer.parseInt(projectId), context);
      if (!hasProjectAccess(context, thisProject.getId(), "project-plan-view")) {
        return "PermissionError";
      }
      context.getRequest().setAttribute("project", thisProject);
      context.getRequest().setAttribute("IncludeSection", "assignments_folder_details");
      //Load the folder
      AssignmentFolder thisFolder = new AssignmentFolder(
          db, Integer.parseInt(folderId), thisProject.getId());
      context.getRequest().setAttribute("assignmentFolder", thisFolder);
    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return ("SystemError");
    } finally {
      this.freeConnection(context, db);
    }
    if (context.getRequest().getParameter("popup") != null) {
      return ("PopupDetailsOK");
    } else {
      return ("ProjectCenterOK");
    }
  }

  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   */
  private static void checkReturnPage(ActionContext context) {
    String returnPage = StringUtils.encodeUrl(context.getRequest().getParameter("return"));
    if (returnPage == null) {
      returnPage = (String) context.getRequest().getAttribute("return");
    }
    context.getRequest().setAttribute("return", returnPage);
    //1st param
    String param = StringUtils.encodeUrl(context.getRequest().getParameter("param"));
    if (param == null) {
      param = (String) context.getRequest().getAttribute("param");
    }
    context.getRequest().setAttribute("param", param);
    //2nd param
    String param2 = StringUtils.encodeUrl(context.getRequest().getParameter("param2"));
    if (param2 == null) {
      param2 = (String) context.getRequest().getAttribute("param2");
    }
    context.getRequest().setAttribute("param2", param2);
  }

  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public synchronized String executeCommandMoveItem(ActionContext context) {
    Connection db = null;
    //Parameters
    String projectId = context.getRequest().getParameter("pid");
    String requirementId = context.getRequest().getParameter("rid");
    String assignmentId = context.getRequest().getParameter("aid");
    String folderId = context.getRequest().getParameter("folderId");
    String direction = context.getRequest().getParameter("dir");
    try {
      //this.checkReturnPage(context);
      db = getConnection(context);
      //Load the project and permissions
      Project thisProject = retrieveAuthorizedProject(Integer.parseInt(projectId), context);
      if (!hasProjectAccess(context, thisProject.getId(), "project-plan-outline-modify")) {
        return "PermissionError";
      }
      //Configure the item to be moved
      RequirementMapItem mapItem = new RequirementMapItem();
      mapItem.setProjectId(Integer.parseInt(projectId));
      mapItem.setRequirementId(Integer.parseInt(requirementId));
      mapItem.setFolderId(DatabaseUtils.parseInt(folderId, -1));
      mapItem.setAssignmentId(DatabaseUtils.parseInt(assignmentId, -1));
      try {
        db.setAutoCommit(false);
        if ("r".equals(direction)) {
          mapItem.moveRight(db);
        } else if ("l".equals(direction)) {
          mapItem.moveLeft(db);
        } else if ("u".equals(direction)) {
          mapItem.moveUp(db);
        } else if ("d".equals(direction)) {
          mapItem.moveDown(db);
        }
        db.commit();
      } catch (SQLException e) {
        db.rollback();
      } finally {
        db.setAutoCommit(true);
      }
    } catch (Exception errorMessage) {
      context.getRequest().setAttribute("Error", errorMessage);
      return ("SystemError");
    } finally {
      freeConnection(context, db);
    }
    return "MoveItemOK";
  }

  /**
   * Description of the Method
   *
   * @param context        Description of the Parameter
   * @param db             Description of the Parameter
   * @param thisProject    Description of the Parameter
   * @param thisAssignment Description of the Parameter
   * @return Description of the Return Value
   */
  private boolean hasModifyAccess(ActionContext context, Connection db, Project thisProject, Assignment thisAssignment) {
    //See if the team member has access to perform an assignment action
    TeamMember thisMember = (TeamMember) context.getRequest().getAttribute(
        "currentMember");
    if (thisMember == null) {
      try {
        //Load from project
        thisMember = new TeamMember(db, thisProject.getId(), getUserId(context));
      } catch (Exception notValid) {
        //Create a guest
        thisMember = new TeamMember();
        thisMember.setProjectId(thisProject.getId());
        thisMember.setUserLevel(getUserLevel(TeamMember.GUEST));
        thisMember.setRoleId(TeamMember.GUEST);
      }
      context.getRequest().setAttribute("currentMember", thisMember);
    }
    //Check the permission
    return (thisAssignment.includesUser(getUserId(context)) || thisMember.getRoleId() <= TeamMember.MANAGER);
  }

  /**
   * Prepares the list of notes for the specified assignment
   *
   * @param context the action context
   * @return if the show notes have been prepared
   */
  public String executeCommandShowNotes(ActionContext context) {
    String projectId = context.getRequest().getParameter("pid");
    String assignmentId = context.getRequest().getParameter("aid");
    Connection db = null;
    // Assignment has not been created yet (new assignment)
    if ("-1".equals(assignmentId)) {
      return "ShowNotesOK";
    }
    // Assignment exists
    try {
      db = getConnection(context);
      Project thisProject = retrieveAuthorizedProject(Integer.parseInt(projectId), context);
      if (!hasProjectAccess(context, thisProject.getId(), "project-plan-outline-modify") &&
          !hasProjectAccess(context, thisProject.getId(), "project-plan-view")) {
        return "PermissionError";
      }
      context.getRequest().setAttribute("project", thisProject);
      // Check assignment details
      Assignment thisAssignment = new Assignment(
          db, Integer.parseInt(assignmentId), thisProject.getId());
      context.getRequest().setAttribute("assignment", thisAssignment);
      // Get the notes
      AssignmentNoteList assignmentNoteList = new AssignmentNoteList();
      assignmentNoteList.setAssignmentId(thisAssignment.getId());
      assignmentNoteList.buildList(db);
      context.getRequest().setAttribute(
          "assignmentNoteList", assignmentNoteList);
    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return ("SystemError");
    } finally {
      freeConnection(context, db);
    }
    return "ShowNotesOK";
  }

  public String executeCommandUserList(ActionContext context) {
    String projectId = context.getRequest().getParameter("pid");

    Connection db = null;
    try {
      db = getConnection(context);
      Project thisProject = retrieveAuthorizedProject(Integer.parseInt(projectId), context);
      if (!hasProjectAccess(context, thisProject.getId(), "project-plan-outline-modify") &&
          !hasProjectAccess(context, thisProject.getId(), "project-plan-view")) {
        return "PermissionError";
      }
      // For each user in which state = 0 or 1, add to list with role
      ArrayList users = toArray(context.getRequest().getParameter("a"));
      ArrayList roles = toArray(context.getRequest().getParameter("r"));
      ArrayList state = toArray(context.getRequest().getParameter("s"));
      // Drop out the deleted users
      for (int i = state.size(); i > 0; i--) {
        String thisState = (String) state.get(i - 1);
        if (thisState.equals("2")) {
          users.remove(i - 1);
          roles.remove(i - 1);
          state.remove(i - 1);
        }
      }
      context.getRequest().setAttribute("usersArray", users);
      context.getRequest().setAttribute("rolesArray", roles);
      context.getRequest().setAttribute("stateArray", state);
      //Load assignment role drop-down list
      LookupList activityList = new LookupList(
          db, "lookup_project_assignment_role");
      activityList.addItem(-1, "--None--");
      context.getRequest().setAttribute("activityList", activityList);
    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return ("SystemError");
    } finally {
      freeConnection(context, db);
    }
    return "MakeUserListOK";
  }

  private static ArrayList toArray(String requestString) {
    ArrayList items = new ArrayList();
    StringTokenizer itemTokenizer = new StringTokenizer(requestString, "|");
    while (itemTokenizer.hasMoreTokens()) {
      String item = itemTokenizer.nextToken();
      items.add(item);
    }
    return items;
  }
}

