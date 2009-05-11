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

package com.concursive.connect.web.modules.profile.actions;

import com.concursive.commons.web.mvc.actions.ActionContext;
import com.concursive.connect.Constants;
import com.concursive.connect.cache.utils.CacheUtils;
import com.concursive.connect.web.controller.actions.GenericAction;
import com.concursive.connect.web.modules.profile.beans.CloneBean;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.utils.ProjectCopier;

import java.sql.Connection;

/**
 * Description
 *
 * @author matt rajkowski
 * @version $Id$
 * @created Oct 18, 2005
 */

public final class ProjectManagementClone extends GenericAction {
  public String executeCommandWizard(ActionContext context) {
    if (getUser(context).getId() < 0) {
      return "PermissionError";
    }
    if (!getUser(context).getAccessAddProjects()) {
      return "PermissionError";
    }
    Connection db = null;
    try {
      int projectId = Integer.parseInt(context.getRequest().getParameter("projectId"));
      db = getConnection(context);
      Project project = retrieveAuthorizedProject(projectId, context);
      context.getRequest().setAttribute("project", project);
      hasProjectAccess(context, project.getId(), "project-news-view");
    } catch (Exception errorMessage) {
      context.getRequest().setAttribute("Error", errorMessage);
      return ("SystemError");
    } finally {
      this.freeConnection(context, db);
    }
    return "CloneWizardOK";
  }

  public String executeCommandSave(ActionContext context) {
    if (getUser(context).getId() < 0) {
      return "PermissionError";
    }
    if (!getUser(context).getAccessAddProjects()) {
      return "PermissionError";
    }
    Connection db = null;
    CloneBean bean = (CloneBean) context.getFormBean();
    try {
      db = getConnection(context);
      retrieveAuthorizedProject(bean.getProjectId(), context);
      Project newProject = ProjectCopier.clone(bean, db, getUser(context).getGroupId(), getUserId(context));
      if (newProject == null) {
        return "PermissionError";
      }
      CacheUtils.updateValue(Constants.SYSTEM_PROJECT_NAME_CACHE, newProject.getId(), newProject.getTitle());
      indexAddItem(context, newProject);
    } catch (Exception errorMessage) {
      context.getRequest().setAttribute("Error", errorMessage);
      return ("SystemError");
    } finally {
      this.freeConnection(context, db);
    }
    return "SaveOK";
  }
}
