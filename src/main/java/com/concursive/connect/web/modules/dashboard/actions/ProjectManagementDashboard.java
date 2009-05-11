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
package com.concursive.connect.web.modules.dashboard.actions;

import com.concursive.commons.web.mvc.actions.ActionContext;
import com.concursive.connect.cms.portal.dao.Dashboard;
import com.concursive.connect.cms.portal.dao.DashboardList;
import com.concursive.connect.cms.portal.dao.DashboardTemplate;
import com.concursive.connect.cms.portal.dao.DashboardTemplateList;
import com.concursive.connect.web.controller.actions.GenericAction;
import com.concursive.connect.web.modules.profile.dao.Project;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Actions for the dashboard tab
 *
 * @author matt rajkowski
 * @version $Id$
 * @created Oct 18, 2005
 */

public final class ProjectManagementDashboard extends GenericAction {

  public String executeCommandImport(ActionContext context) {
    if (getUser(context).getId() < 0) {
      return "PermissionError";
    }
    Connection db = null;
    try {
      int projectId = Integer.parseInt(context.getRequest().getParameter("pid"));
      db = getConnection(context);
      //Load the project
      Project project = retrieveAuthorizedProject(projectId, context);
      if (!hasProjectAccess(context, project.getId(), "project-dashboard-admin")) {
        return "PermissionError";
      }
      context.getRequest().setAttribute("project", project);
      context.getRequest().setAttribute("IncludeSection", "dashboard_import");
      buildElements(context, db, projectId, -1);
      DashboardTemplateList templates = new DashboardTemplateList(DashboardTemplateList.TYPE_PROJECT_TEMPLATES);
      context.getRequest().setAttribute("templates", templates);
    } catch (Exception errorMessage) {
      context.getRequest().setAttribute("Error", errorMessage);
      return ("SystemError");
    } finally {
      this.freeConnection(context, db);
    }
    return "ProjectCenterOK";
  }

  public String executeCommandImportTemplate(ActionContext context) {
    if (getUser(context).getId() < 0) {
      return "PermissionError";
    }
    Connection db = null;
    try {
      int projectId = Integer.parseInt(context.getRequest().getParameter("pid"));
      int templateId = Integer.parseInt(context.getRequest().getParameter("template"));
      db = getConnection(context);
      //Load the project
      Project project = retrieveAuthorizedProject(projectId, context);
      if (!hasProjectAccess(context, project.getId(), "project-dashboard-admin")) {
        return "PermissionError";
      }
      DashboardTemplateList templates = new DashboardTemplateList(DashboardTemplateList.TYPE_PROJECT_TEMPLATES);
      DashboardTemplate template = templates.getTemplateById(templateId);
      int dashboardId = template.createDashboard(db, project.getId());
      context.getRequest().setAttribute("dash", String.valueOf(dashboardId));

    } catch (Exception errorMessage) {
      context.getRequest().setAttribute("Error", errorMessage);
      return ("SystemError");
    } finally {
      this.freeConnection(context, db);
    }
    return "ImportOK";
  }

  public String executeCommandAdd(ActionContext context) {
    if (getUser(context).getId() < 0) {
      return "PermissionError";
    }
    Connection db = null;
    try {
      int projectId = Integer.parseInt(context.getRequest().getParameter("pid"));
      db = getConnection(context);
      //Load the project
      Project project = retrieveAuthorizedProject(projectId, context);
      if (!hasProjectAccess(context, project.getId(), "project-dashboard-admin")) {
        return "PermissionError";
      }
      context.getRequest().setAttribute("project", project);
      context.getRequest().setAttribute("IncludeSection", "dashboard_add");
      buildElements(context, db, projectId, -1);
      // Determine the next level to use for ordering
      Dashboard dashboard = (Dashboard) context.getFormBean();
      if (dashboard.getLevel() == -1) {
        dashboard.setLevel(DashboardList.queryMaxLevel(db, projectId) + 1);
        dashboard.setEnabled(true);
      }
    } catch (Exception errorMessage) {
      context.getRequest().setAttribute("Error", errorMessage);
      return ("SystemError");
    } finally {
      this.freeConnection(context, db);
    }
    return "ProjectCenterOK";
  }

  public String executeCommandModify(ActionContext context) {
    if (getUser(context).getId() < 0) {
      return "PermissionError";
    }
    Connection db = null;
    try {
      int projectId = Integer.parseInt(context.getRequest().getParameter("pid"));
      int dashboardId = Integer.parseInt(context.getRequest().getParameter("dash"));
      db = getConnection(context);
      //Load the project
      Project project = retrieveAuthorizedProject(projectId, context);
      if (!hasProjectAccess(context, project.getId(), "project-dashboard-admin")) {
        return "PermissionError";
      }
      context.getRequest().setAttribute("project", project);
      context.getRequest().setAttribute("IncludeSection", "dashboard_add");
      buildElements(context, db, projectId, dashboardId);
      Dashboard dashboard = new Dashboard(db, dashboardId);
      context.getRequest().setAttribute("dashboard", dashboard);
    } catch (Exception errorMessage) {
      context.getRequest().setAttribute("Error", errorMessage);
      return ("SystemError");
    } finally {
      this.freeConnection(context, db);
    }
    return "ProjectCenterOK";
  }

  public String executeCommandSave(ActionContext context) {
    if (getUser(context).getId() < 0) {
      return "PermissionError";
    }
    int resultCount = -1;
    boolean recordInserted = false;
    Connection db = null;
    try {
      int projectId = Integer.parseInt(context.getRequest().getParameter("pid"));
      db = getConnection(context);
      //Load the project
      Project project = retrieveAuthorizedProject(projectId, context);
      if (!hasProjectAccess(context, project.getId(), "project-dashboard-admin")) {
        return "PermissionError";
      }
      Dashboard dashboard = (Dashboard) context.getFormBean();
      dashboard.setProjectId(project.getId());
      if (dashboard.getId() == -1) {
        recordInserted = dashboard.insert(db);
      } else {
        resultCount = dashboard.update(db);
      }
      if (!recordInserted && resultCount < 0) {
        processErrors(context, dashboard.getErrors());
      } else {
        context.getRequest().setAttribute("dash", String.valueOf(dashboard.getId()));
      }
    } catch (Exception errorMessage) {
      context.getRequest().setAttribute("Error", errorMessage);
      return ("SystemError");
    } finally {
      this.freeConnection(context, db);
    }
    if (resultCount == 0) {
      context.getRequest().setAttribute("Error", NOT_UPDATED_MESSAGE);
      return ("UserError");
    }
    return ("SaveOK");
  }

  public String executeCommandEdit(ActionContext context) {
    if (getUser(context).getId() < 0) {
      return "PermissionError";
    }
    Connection db = null;
    try {
      int projectId = Integer.parseInt(context.getRequest().getParameter("pid"));
      int dashboardId = Integer.parseInt(context.getRequest().getParameter("dash"));
      db = getConnection(context);
      //Load the project
      Project project = retrieveAuthorizedProject(projectId, context);
      if (!hasProjectAccess(context, project.getId(), "project-dashboard-admin")) {
        return "PermissionError";
      }
      context.getRequest().setAttribute("project", project);
      context.getRequest().setAttribute("IncludeSection", "dashboard_editor");
      //hasProjectAccess(context, db, project, "project-dashboard-edit");
      buildElements(context, db, projectId, dashboardId);
    } catch (Exception errorMessage) {
      context.getRequest().setAttribute("Error", errorMessage);
      return ("SystemError");
    } finally {
      this.freeConnection(context, db);
    }
    return "ProjectCenterOK";
  }

  public String executeCommandDelete(ActionContext context) {
    if (getUser(context).getId() < 0) {
      return "PermissionError";
    }
    Connection db = null;
    try {
      int projectId = Integer.parseInt(context.getRequest().getParameter("pid"));
      int dashboardId = Integer.parseInt(context.getRequest().getParameter("dash"));
      db = getConnection(context);
      //Load the project
      Project project = retrieveAuthorizedProject(projectId, context);
      if (!hasProjectAccess(context, project.getId(), "project-dashboard-admin")) {
        return "PermissionError";
      }
      context.getRequest().setAttribute("project", project);
      Dashboard dashboard = new Dashboard(db, dashboardId);
      dashboard.delete(db);
    } catch (Exception errorMessage) {
      context.getRequest().setAttribute("Error", errorMessage);
      return ("SystemError");
    } finally {
      this.freeConnection(context, db);
    }
    return "DeleteOK";
  }


  private void buildElements(ActionContext context, Connection db, int projectId, int dashboardId) throws SQLException {
    // Get the dashboards for this project
    DashboardList dashboards = new DashboardList();
    dashboards.setProjectId(projectId);
    dashboards.buildList(db);
    context.getRequest().setAttribute("dashboardList", dashboards);
  }
}
