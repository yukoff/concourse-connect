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

package com.concursive.connect.web.modules.admin.actions;

import com.concursive.commons.web.mvc.actions.ActionContext;
import com.concursive.connect.Constants;
import com.concursive.connect.web.controller.actions.GenericAction;
import com.concursive.connect.web.modules.plans.dao.AssignmentRoleList;

import java.sql.Connection;
import java.util.StringTokenizer;

/**
 * Description
 *
 * @author matt rajkowski
 * @version $Id$
 * @created Dec 27, 2004
 */

public class AdminApplication extends GenericAction {
  public String executeCommandDefault(ActionContext context) {
    if (!getUser(context).getAccessAdmin()) {
      return "PermissionError";
    }
    return "DefaultOK";
  }


  public String executeCommandEditAssignmentRoleList(ActionContext context) {
    if (!getUser(context).getAccessAdmin()) {
      return "PermissionError";
    }
    Connection db = null;
    try {
      db = getConnection(context);
      // Load the role list
      AssignmentRoleList roleList = new AssignmentRoleList();
      roleList.setEnabled(Constants.TRUE);
      roleList.buildList(db);
      context.getRequest().setAttribute("editList", roleList.getHtmlSelect());
      // Edit List properties
      context.getRequest().setAttribute(
          "subTitle", "Modify assignment roles, available for any project");
      context.getRequest().setAttribute(
          "returnUrl", ctx(context) + "/AdminApplication.do?command=SaveAssignmentRoleList");
      return ("EditCategoryListOK");
    } catch (Exception errorMessage) {
      context.getRequest().setAttribute("Error", errorMessage);
      return ("SystemError");
    } finally {
      this.freeConnection(context, db);
    }
  }

  public String executeCommandSaveAssignmentRoleList(ActionContext context) {
    if (!getUser(context).getAccessAdmin()) {
      return "PermissionError";
    }
    Connection db = null;
    try {
      db = getConnection(context);
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
          System.out.println("AdminApplication-> Item: " + names[j]);
        }
        j++;
      }
      // Load the previous category list
      AssignmentRoleList roleList = new AssignmentRoleList();
      roleList.buildList(db);
      roleList.updateValues(db, params, names);
      return ("SaveCategoryListOK");
    } catch (Exception errorMessage) {
      context.getRequest().setAttribute("Error", errorMessage);
      return ("SystemError");
    } finally {
      this.freeConnection(context, db);
    }
  }

}
