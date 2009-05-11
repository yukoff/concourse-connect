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
import com.concursive.connect.web.controller.actions.GenericAction;
import com.concursive.connect.web.modules.discussion.dao.DiscussionForumTemplate;
import com.concursive.connect.web.modules.discussion.dao.DiscussionForumTemplateList;
import com.concursive.connect.web.modules.profile.dao.ProjectCategoryList;

import java.sql.Connection;

/**
 * Actions for the administration module to manage discussion forum templates
 *
 * @author Kailash Bhoopalam
 * @created August 12, 2008
 */
public final class AdminDiscussionForumTemplates extends GenericAction {

  public String executeCommandList(ActionContext context) {
    if (!getUser(context).getAccessAdmin()) {
      return "PermissionError";
    }
    Connection db = null;
    try {
      db = getConnection(context);
      // Get templates
      DiscussionForumTemplateList templateList = new DiscussionForumTemplateList();
      templateList.buildList(db);
      context.getRequest().setAttribute("discussionForumTemplateList", templateList);
      //Load project category drop-down list
      ProjectCategoryList projectCategoryList = new ProjectCategoryList();
      projectCategoryList.setTopLevelOnly(true);
      projectCategoryList.buildList(db);
      context.getRequest().setAttribute("projectCategoryList", projectCategoryList);
    } catch (Exception e) {
      e.printStackTrace();
      context.getRequest().setAttribute("Error", e);
      return ("SystemError");
    } finally {
      freeConnection(context, db);
    }
    return "ListOK";
  }


  /**
   * Action to modify the details of a specific category
   *
   * @param context the Context
   * @return the action result
   */
  public String executeCommandModify(ActionContext context) {
    if (!getUser(context).getAccessAdmin()) {
      return "PermissionError";
    }
    String id = context.getParameter("id");
    Connection db = null;
    try {
      // This can be adding, modifying, or fixing a failed save
      DiscussionForumTemplate template = (DiscussionForumTemplate) context.getFormBean();
      db = getConnection(context);
      if (template.getId() == -1 && id != null) {
        template = new DiscussionForumTemplate(db, Integer.parseInt(id));
      }
      if (template.getId() > -1 && template.getEntered() != null) {
        template = new DiscussionForumTemplate(db, template.getId());
      }
      context.getRequest().setAttribute("discussionForumTemplate", template);
      // Load project category drop-down list
      ProjectCategoryList projectCategoryList = new ProjectCategoryList();
      projectCategoryList.setTopLevelOnly(true);
      projectCategoryList.buildList(db);
      context.getRequest().setAttribute("projectCategoryList", projectCategoryList);
    } catch (Exception e) {
      e.printStackTrace();
      context.getRequest().setAttribute("Error", e);
      return ("SystemError");
    } finally {
      freeConnection(context, db);
    }
    return "ModifyOK";
  }

  /**
   * Action to save the details of a specific category
   *
   * @param context the Context
   * @return the action result
   */
  public String executeCommandSave(ActionContext context) {
    if (!getUser(context).getAccessAdmin()) {
      return "PermissionError";
    }
    Connection db = null;
    boolean recordUpdated = false;
    try {
      db = getConnection(context);
      DiscussionForumTemplate template = (DiscussionForumTemplate) context.getFormBean();
      if (template.getId() != -1) {
        int count = template.update(db);
        if (count == 1) {
          recordUpdated = true;
        }
      } else {
        recordUpdated = template.insert(db);
      }
      if (!recordUpdated) {
        processErrors(context, template.getErrors());
      }
    } catch (Exception e) {
      e.printStackTrace();
      context.getRequest().setAttribute("Error", e);
      return ("SystemError");
    } finally {
      freeConnection(context, db);
    }
    if (!recordUpdated) {
      return executeCommandModify(context);
    }
    return "SaveOK";
  }

  /**
   * Action to modify the details of a specific category
   *
   * @param context the Context
   * @return the action result
   */
  public String executeCommandDelete(ActionContext context) {
    if (!getUser(context).getAccessAdmin()) {
      return "PermissionError";
    }
    Connection db = null;
    try {
      db = getConnection(context);
      String idString = context.getRequest().getParameter("id");
      int id = Integer.parseInt(idString);
      DiscussionForumTemplate template = new DiscussionForumTemplate(db, id);
      template.delete(db);
    } catch (Exception e) {
      e.printStackTrace();
      context.getRequest().setAttribute("Error", e);
      return ("SystemError");
    } finally {
      freeConnection(context, db);
    }
    return "DeleteOK";
  }

}