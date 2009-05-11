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

import com.concursive.commons.text.StringUtils;
import com.concursive.commons.web.mvc.actions.ActionContext;
import com.concursive.connect.web.controller.actions.GenericAction;
import com.concursive.connect.web.modules.badges.dao.BadgeCategory;
import com.concursive.connect.web.modules.badges.dao.BadgeCategoryList;
import com.concursive.connect.web.modules.documents.dao.FileItemList;
import com.concursive.connect.web.modules.profile.dao.ProjectCategoryList;
import com.concursive.connect.web.utils.PagedListInfo;

import java.sql.Connection;

/**
 * Actions for the administration module
 *
 * @author Kailash Bhoopalam
 * @created May 13, 2008
 */
public final class AdminBadgeCategories extends GenericAction {

  public String executeCommandDefault(ActionContext context) {
    if (!getUser(context).getAccessAdmin()) {
      return "PermissionError";
    }
    return "DefaultOK";
  }


  /**
   * Action to prepare a list of Admin Badge Categories
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandList(ActionContext context) {
    if (!getUser(context).getAccessAdmin()) {
      return "PermissionError";
    }
    resetPagedListInfo(context);
    Connection db = null;
    try {
      db = getConnection(context);
      // Get badge categories
      PagedListInfo adminBadgeCategoriesInfo = this.getPagedListInfo(context, "adminBadgeCategoriesInfo");
      adminBadgeCategoriesInfo.setItemsPerPage(0);
      adminBadgeCategoriesInfo.setLink(context, ctx(context) + "/AdminBadgeCategories.do?command=List");
      BadgeCategoryList badgeCategoryList = new BadgeCategoryList();
      badgeCategoryList.setPagedListInfo(adminBadgeCategoriesInfo);
      badgeCategoryList.setBuildLogos(true);
      badgeCategoryList.buildList(db);

      context.getRequest().setAttribute("badgeCategoryList", badgeCategoryList);

      //Load project category drop-down list
      ProjectCategoryList projectCategoryList = new ProjectCategoryList();
      projectCategoryList.setTopLevelOnly(true);
      projectCategoryList.buildList(db);
      context.getRequest().setAttribute("projectCategoryList", projectCategoryList);

    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return ("SystemError");
    } finally {
      freeConnection(context, db);
    }
    return "ListOK";
  }


  /**
   * Action to generate a the details of a specific user
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandDetails(ActionContext context) {
    if (!getUser(context).getAccessAdmin()) {
      return "PermissionError";
    }
    resetPagedListInfo(context);
    Connection db = null;
    try {
      db = getConnection(context);
      int badgeCategoryId = Integer.parseInt(context.getRequest().getParameter("badgeCategoryId"));
      // Get the badge
      BadgeCategory badgeCategory = new BadgeCategory(db, badgeCategoryId);
      context.getRequest().setAttribute("badgeCategory", badgeCategory);
    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return ("SystemError");
    } finally {
      freeConnection(context, db);
    }
    return "DetailsOK";
  }

  private void resetPagedListInfo(ActionContext context) {
    if ("true".equals(context.getRequest().getParameter("resetList"))) {
      context.getSession().removeAttribute("adminBadgeCategoriesInfo");
    }
  }

  public String executeCommandModify(ActionContext context) {
    if (!getUser(context).getAccessAdmin()) {
      return "PermissionError";
    }
    Connection db = null;
    BadgeCategory badgeCategory = null;
    try {
      db = getConnection(context);
      String badgeCategoryIdString = context.getRequest().getParameter("badgeCategoryId");
      if (StringUtils.hasText(badgeCategoryIdString)) {
        int badgeCategoryId = Integer.parseInt(badgeCategoryIdString);
        // Get the badge
        badgeCategory = new BadgeCategory(db, badgeCategoryId);
        context.getRequest().setAttribute("badgeCategory", badgeCategory);
      }
      //Load project category drop-down list
      ProjectCategoryList projectCategoryList = new ProjectCategoryList();
      projectCategoryList.setTopLevelOnly(true);
      projectCategoryList.buildList(db);
      context.getRequest().setAttribute("projectCategoryList", projectCategoryList);

      //Get logo
      if (badgeCategory != null && badgeCategory.getLogoId() != -1) {
        FileItemList fileItemList = badgeCategory.retrieveFiles(db);
        context.getRequest().setAttribute("fileItemList", fileItemList);
      }
    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return ("SystemError");
    } finally {
      freeConnection(context, db);
    }
    return "AddOK";
  }

  public String executeCommandSave(ActionContext context) {
    if (!getUser(context).getAccessAdmin()) {
      return "PermissionError";
    }
    Connection db = null;
    boolean recordUpdated = false;
    try {
      db = getConnection(context);
      BadgeCategory thisBadgeCategory = (BadgeCategory) context.getFormBean();
      if (context.getRequest().getParameter("enabled") == null) {
        thisBadgeCategory.setEnabled(false);
      }
      if (thisBadgeCategory.getId() != -1) {
        int count = thisBadgeCategory.update(db);
        if (count == 1) {
          recordUpdated = true;
        }
      } else {
        recordUpdated = thisBadgeCategory.insert(db);
      }
      if (recordUpdated) {
        thisBadgeCategory.saveAttachments(db, getUserId(context), getPath(context, "projects"));
      } else {
        processErrors(context, thisBadgeCategory.getErrors());
      }
    } catch (Exception e) {
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

  public String executeCommandDelete(ActionContext context) {
    if (!getUser(context).getAccessAdmin()) {
      return "PermissionError";
    }
    Connection db = null;
    try {
      db = getConnection(context);
      String badgeCategoryIdString = context.getRequest().getParameter("badgeCategoryId");
      if (StringUtils.hasText(badgeCategoryIdString)) {
        int badgeCategoryId = Integer.parseInt(badgeCategoryIdString);
        BadgeCategory badgeCategory = new BadgeCategory(db, badgeCategoryId);
        badgeCategory.delete(db, getPath(context, "projects"));
      }
    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return ("SystemError");
    } finally {
      freeConnection(context, db);
    }
    return "DeleteOK";
  }

}

