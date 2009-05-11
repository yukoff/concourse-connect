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

package com.concursive.connect.web.modules.issues.actions;

import com.concursive.commons.web.mvc.actions.ActionContext;
import com.concursive.connect.Constants;
import com.concursive.connect.cms.portal.dao.ProjectItemList;
import com.concursive.connect.web.controller.actions.GenericAction;
import com.concursive.connect.web.modules.issues.beans.CategoryEditor;
import com.concursive.connect.web.modules.issues.dao.TicketCategoryList;
import com.concursive.connect.web.modules.profile.dao.Project;

import java.sql.Connection;
import java.util.StringTokenizer;

/**
 * Description of the Class
 *
 * @author matt rajkowski
 * @version $Id: ProjectManagementTickets.java,v 1.12.4.1 2004/08/20 19:48:25
 *          matt Exp $
 * @created June 8, 2004
 */
public final class ProjectManagementTicketsConfig extends GenericAction {

  public String executeCommandOptions(ActionContext context) {
    Connection db = null;
    //Params
    String projectId = context.getRequest().getParameter("pid");
    try {
      db = this.getConnection(context);
      // Load project, check permissions
      Project thisProject = retrieveAuthorizedProject(Integer.parseInt(projectId), context);
      if (!hasProjectAccess(context, thisProject.getId(), "project-setup-customize")) {
        return "PermissionError";
      }
      context.getRequest().setAttribute("project", thisProject);
      context.getRequest().setAttribute("IncludeSection", "tickets_config");
      return ("ProjectCenterOK");
    } catch (Exception errorMessage) {
      context.getRequest().setAttribute("Error", errorMessage);
      return ("SystemError");
    } finally {
      this.freeConnection(context, db);
    }
  }

  public String executeCommandConfigureCategories(ActionContext context) {
    Connection db = null;
    //Params
    String projectId = context.getRequest().getParameter("pid");
    try {
      db = this.getConnection(context);
      // Load project, check permissions
      Project thisProject = retrieveAuthorizedProject(Integer.parseInt(projectId), context);
      if (!hasProjectAccess(context, thisProject.getId(), "project-setup-customize")) {
        return "PermissionError";
      }
      context.getRequest().setAttribute("project", thisProject);
      context.getRequest().setAttribute("IncludeSection", "tickets_ticket_categories");
      // Load the category list
      TicketCategoryList categoryList = new TicketCategoryList();
      categoryList.setParentCode(0);
      categoryList.setEnabledState(Constants.TRUE);
      categoryList.setProjectId(thisProject.getId());
      categoryList.buildList(db);
      context.getRequest().setAttribute("editList", categoryList);
      // Define this editor
      CategoryEditor editor = new CategoryEditor();
      editor.setMaxLevels(4);
      context.getRequest().setAttribute("categoryEditor", editor);
      return ("ProjectCenterOK");
    } catch (Exception errorMessage) {
      context.getRequest().setAttribute("Error", errorMessage);
      return ("SystemError");
    } finally {
      this.freeConnection(context, db);
    }
  }

  public String executeCommandCategoryJSList(ActionContext context) {
    if (!getUser(context).getAccessAdmin()) {
      return "PermissionError";
    }
    Connection db = null;
    try {
      int categoryId = Integer.parseInt(context.getRequest().getParameter("categoryId"));
      int nextLevel = Integer.parseInt(context.getRequest().getParameter("nextLevel"));
      int projectId = Integer.parseInt(context.getRequest().getParameter("projectId"));
      db = getConnection(context);
      // Check project pemissions
      Project thisProject = null;
      if (projectId != -1) {

      }
      // Load the category list for the next level
      if (System.getProperty("DEBUG") != null) {
        System.out.println("AdminApplication-> categoryId: " + categoryId);
      }
      TicketCategoryList categoryList = new TicketCategoryList();
      categoryList.setParentCode(categoryId);
      categoryList.setCatLevel(nextLevel);
      categoryList.setEnabledState(Constants.TRUE);
      categoryList.setProjectId(projectId);
      categoryList.buildList(db);
      context.getRequest().setAttribute("editList", categoryList);
      // Define this editor
      CategoryEditor editor = new CategoryEditor();
      editor.setMaxLevels(4);
      context.getRequest().setAttribute("categoryEditor", editor);
    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return ("SystemError");
    } finally {
      this.freeConnection(context, db);
    }
    return ("TicketCategoryJSListOK");
  }

  public String executeCommandEditTicketList(ActionContext context) {
    Connection db = null;
    try {
      int parentId = Integer.parseInt(context.getRequest().getParameter("parentId"));
      int catLevel = Integer.parseInt(context.getRequest().getParameter("catLevel"));
      int projectId = Integer.parseInt(context.getRequest().getParameter("projectId"));
      db = getConnection(context);
      // Check project permissions
      if (projectId == -1) {
        if (!getUser(context).getAccessAdmin()) {
          return "PermissionError";
        }
      } else {
        // Load project, check permissions
        Project thisProject = retrieveAuthorizedProject(projectId, context);
        if (!hasProjectAccess(context, thisProject.getId(), "project-setup-customize")) {
          return "PermissionError";
        }
      }
      // Load the category list
      TicketCategoryList categoryList = new TicketCategoryList();
      categoryList.setParentCode(parentId);
      categoryList.setCatLevel(catLevel);
      categoryList.setProjectId(projectId);
      categoryList.setEnabledState(Constants.TRUE);
      categoryList.buildList(db);
      context.getRequest().setAttribute("editList", categoryList.getHtmlSelect(-1));
      // Edit List properties
      context.getRequest().setAttribute("subTitle", "Modify the ticket level categories");
      context.getRequest().setAttribute("returnUrl", ctx(context) + "/ProjectManagementTicketsConfig.do?command=SaveTicketList&categoryId=" + parentId + "&catLevel=" + catLevel);
      return ("EditTicketListPopupOK");
    } catch (Exception errorMessage) {
      context.getRequest().setAttribute("Error", errorMessage);
      return ("SystemError");
    } finally {
      this.freeConnection(context, db);
    }
  }

  public String executeCommandSaveTicketList(ActionContext context) {
    int projectId = -1;
    try {
      projectId = Integer.parseInt(context.getRequest().getParameter("projectId"));
    } catch (Exception e) {
      // Not in a project
    }
    Connection db = null;
    try {
      db = getConnection(context);
      // Check project permissions
      if (projectId == -1) {
        if (!getUser(context).getAccessAdmin()) {
          return "PermissionError";
        }
      } else {
        // Load project, check permissions
        Project thisProject = retrieveAuthorizedProject(projectId, context);
        if (!hasProjectAccess(context, thisProject.getId(), "project-setup-customize")) {
          return "PermissionError";
        }
      }
      TicketCategoryList categoryList = CategoryEditor.update(db, context.getRequest());
      categoryList.clear();
      categoryList.setEnabledState(Constants.TRUE);
      categoryList.buildList(db);
      context.getRequest().setAttribute("editList", categoryList.getHtmlSelect(-1));
      return ("EditTicketListPopupCloseOK");
    } catch (Exception errorMessage) {
      context.getRequest().setAttribute("Error", errorMessage);
      return ("SystemError");
    } finally {
      this.freeConnection(context, db);
    }
  }

  public String executeCommandConfigureItemList(ActionContext context) {
    // Load the list for display
    int projectId = -1;
    try {
      projectId = Integer.parseInt(context.getRequest().getParameter("pid"));
    } catch (Exception e) {
      // Not in a project
    }
    String list = context.getRequest().getParameter("list");
    if (!isValidList("ticket_" + list)) {
      return "PermissionError";
    }
    Connection db = null;
    try {
      if (projectId == -1 || list == null) {
        return "PermissionError";
      }
      db = getConnection(context);
      // Load project, check permissions
      Project thisProject = retrieveAuthorizedProject(projectId, context);
      if (!hasProjectAccess(context, thisProject.getId(), "project-setup-customize")) {
        return "PermissionError";
      }
      ProjectItemList itemList = new ProjectItemList();
      itemList.setProjectId(projectId);
      itemList.setEnabled(Constants.TRUE);
      itemList.buildList(db, "ticket_" + list);
      context.getRequest().setAttribute("editList", itemList.getHtmlSelect());

      // Edit List properties
      context.getRequest().setAttribute(
          "subTitle", "Modify this project's list: " + list);
      context.getRequest().setAttribute(
          "returnUrl", ctx(context) + "/ProjectManagementTicketsConfig.do?command=SaveItemList&pid=" + thisProject.getId() + "&list=" + list);

      context.getRequest().setAttribute("project", thisProject);
      context.getRequest().setAttribute("IncludeSection", "tickets_config_list");
      return ("ProjectCenterOK");

    } catch (Exception errorMessage) {
      context.getRequest().setAttribute("Error", errorMessage);
      return ("SystemError");
    } finally {
      this.freeConnection(context, db);
    }
  }

  public String executeCommandSaveItemList(ActionContext context) {
    // Update the contents of the list
    int projectId = -1;
    try {
      projectId = Integer.parseInt(context.getRequest().getParameter("pid"));
    } catch (Exception e) {
      // Not in a project
    }
    String list = context.getRequest().getParameter("list");
    if (!isValidList("ticket_" + list)) {
      return "PermissionError";
    }
    Connection db = null;
    try {
      if (projectId == -1) {
        return "PermissionError";
      }
      db = getConnection(context);
      // Load project, check permissions
      Project thisProject = retrieveAuthorizedProject(projectId, context);
      if (!hasProjectAccess(context, thisProject.getId(), "project-setup-customize")) {
        return "PermissionError";
      }
      // Parse the request for items
      String[] params = context.getRequest().getParameterValues(
          "selectedList");
      String[] names = new String[params.length];
      int j = 0;
      StringTokenizer st = new StringTokenizer(
          context.getRequest().getParameter("selectNames"), "^");
      while (st.hasMoreTokens()) {
        names[j] = st.nextToken();
        if (System.getProperty("DEBUG") != null) {
          System.out.println("ProjectManagementTicketsConfig-> Item: " + names[j]);
        }
        j++;
      }
      // Load the previous category list
      ProjectItemList itemList = new ProjectItemList();
      itemList.setProjectId(thisProject.getId());
      itemList.buildList(db, "ticket_" + list);
      itemList.updateValues(db, params, names, "ticket_" + list);
      return ("SaveItemListOK");
    } catch (Exception errorMessage) {
      context.getRequest().setAttribute("Error", errorMessage);
      return ("SystemError");
    } finally {
      this.freeConnection(context, db);
    }
  }

  private boolean isValidList(String table) {
    return (table.equals(ProjectItemList.TICKET_DEFECT) ||
        table.equals(ProjectItemList.TICKET_CAUSE) ||
        table.equals(ProjectItemList.TICKET_STATE) ||
        table.equals(ProjectItemList.TICKET_RESOLUTION) ||
        table.equals(ProjectItemList.TICKET_ESCALATION));
  }
}
