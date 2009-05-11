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
package com.concursive.connect.cms.portal.actions;

import com.concursive.commons.text.StringUtils;
import com.concursive.commons.web.mvc.actions.ActionContext;
import com.concursive.connect.cms.portal.dao.DashboardPage;
import com.concursive.connect.cms.portal.dao.DashboardTemplateList;
import com.concursive.connect.cms.portal.utils.DashboardUtils;
import com.concursive.connect.web.controller.actions.GenericAction;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.portal.PortletManager;

import java.sql.Connection;

/**
 * Actions for handling dynamic portal using project parameters
 *
 * @author matt rajkowski
 * @created July 23, 2008
 */

public final class ProjectManagementPage extends GenericAction {

  public String executeCommandShowPortalPage(ActionContext context) {
    Connection db = null;
    try {
      // Expected parameters
      int projectId = Integer.parseInt(context.getRequest().getParameter("pid"));
      String pageName = context.getRequest().getParameter("name");
      db = this.getConnection(context);
      // Find the portal page
      DashboardPage page = DashboardUtils.loadDashboardPage(DashboardTemplateList.TYPE_PROJECTS, pageName);
      if (page == null) {
        return "SystemError";
      }
      context.getRequest().setAttribute("dashboardPage", page);
      // Load the project
      Project project = retrieveAuthorizedProject(projectId, context);
      if (!hasProjectAccess(context, project.getId(), StringUtils.getText(page.getPermission(), "project-profile-view"))) {
        return "SystemError";
      }
      context.getRequest().setAttribute("project", project);
      // A dashboard has a page (and a project in this case)
      page.setProjectId(project.getId());
      boolean isAction = PortletManager.processPage(context, db, page);
      if (isAction) {
        return ("-none-");
      }
      return "ShowPortalPageOK";
    } catch (Exception errorMessage) {
      context.getRequest().setAttribute("Error", errorMessage);
      errorMessage.printStackTrace(System.out);
      return ("SystemError");
    } finally {
      this.freeConnection(context, db);
    }
  }
}