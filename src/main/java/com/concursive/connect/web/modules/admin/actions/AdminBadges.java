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
import com.concursive.connect.Constants;
import com.concursive.connect.web.controller.actions.GenericAction;
import com.concursive.connect.web.modules.badges.dao.Badge;
import com.concursive.connect.web.modules.badges.dao.BadgeCategory;
import com.concursive.connect.web.modules.badges.dao.BadgeCategoryList;
import com.concursive.connect.web.modules.badges.dao.BadgeList;
import com.concursive.connect.web.modules.badges.utils.BadgeUtils;
import com.concursive.connect.web.modules.documents.dao.FileItemList;
import com.concursive.connect.web.modules.profile.dao.ProjectCategory;
import com.concursive.connect.web.modules.profile.dao.ProjectCategoryList;
import com.concursive.connect.web.utils.PagedListInfo;

import java.sql.Connection;

/**
 * Actions for the administration module
 *
 * @author Kailash Bhoopalam
 * @created May 13, 2008
 */
public final class AdminBadges extends GenericAction {

  public String executeCommandDefault(ActionContext context) {
    if (!getUser(context).getAccessAdmin()) {
      return "PermissionError";
    }
    return "DefaultOK";
  }


  /**
   * Action to prepare a list of Admin Badges
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
      String badgeCategoryId = context.getRequest().getParameter("badgeCategoryId");
      if (!StringUtils.hasText(badgeCategoryId)) {
        badgeCategoryId = (String) context.getRequest().getAttribute("badgeCategoryId");
      }
      context.getRequest().setAttribute("badgeCategoryId", badgeCategoryId);
      // Get badge categories
      PagedListInfo adminBadgeCategoriesInfo = this.getPagedListInfo(context, "adminBadgeCategoriesInfo");
      adminBadgeCategoriesInfo.setItemsPerPage(0);
      adminBadgeCategoriesInfo.setLink(context, ctx(context) + "/AdminBadgeCategories.do?command=List");
      adminBadgeCategoriesInfo.setColumnToSortBy(" lpc.description, bc.item_name asc ");
      BadgeCategoryList badgeCategoryList = new BadgeCategoryList();
      badgeCategoryList.setPagedListInfo(adminBadgeCategoriesInfo);
      badgeCategoryList.setEnabled(Constants.TRUE);
      badgeCategoryList.buildList(db);
      context.getRequest().setAttribute("badgeCategoryList", badgeCategoryList);
      if (badgeCategoryList.size() > 0 && !StringUtils.hasText(badgeCategoryId)) {
        badgeCategoryId = String.valueOf(badgeCategoryList.get(0).getId());
      }

      // Get badges
      if (StringUtils.hasText(badgeCategoryId)){
	      PagedListInfo adminBadgesInfo = this.getPagedListInfo(context, "adminBadgesInfo");
	      adminBadgesInfo.setLink(context, ctx(context) + "/AdminBadges.do?command=List");
	      BadgeList badgeList = new BadgeList();
	      badgeList.setPagedListInfo(adminBadgesInfo);
	      badgeList.setEnabled(Constants.TRUE);
	      badgeList.setCategoryId(badgeCategoryId);
	      badgeList.setBuildLogos(true);
	      badgeList.buildList(db);
	      context.getRequest().setAttribute("badgeList", badgeList);
	      
	      BadgeCategory badgeCategory = new BadgeCategory(db, Integer.parseInt(badgeCategoryId));
	      ProjectCategory projectCategory = new ProjectCategory(db, badgeCategory.getProjectCategoryId());
	      context.getRequest().setAttribute("badgeCategory", badgeCategory);
	      context.getRequest().setAttribute("projectCategory", projectCategory);
      }
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
    if ("true".equals(context.getRequest().getParameter("onlyBadges"))) {
      context.getRequest().setAttribute("onlyBadges", "true");
      context.getRequest().setAttribute("PageLayout", "/layout1.jsp");
      return "ListOK";
    }
    return "ListAllOK";
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
      int badgeId = Integer.parseInt(context.getRequest().getParameter("badgeId"));
      // Get the badge
      Badge badge = BadgeUtils.loadBadge(badgeId);
      context.getRequest().setAttribute("badge", badge);
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
      context.getSession().removeAttribute("adminBadgesInfo");
      context.getSession().removeAttribute("adminBadgeCategoriesInfo");
    }
  }

  public String executeCommandModify(ActionContext context) {
    if (!getUser(context).getAccessAdmin()) {
      return "PermissionError";
    }
    Badge badge = null;
    Connection db = null;
    try {
      db = getConnection(context);
      String badgeIdString = context.getRequest().getParameter("badgeId");
      String badgeCategoryIdString = context.getRequest().getParameter("badgeCategoryId");
      if (!StringUtils.hasText(badgeCategoryIdString)) {
        badgeCategoryIdString = "-1";
      }
      context.getRequest().setAttribute("badgeCategoryId", badgeCategoryIdString);
      if (StringUtils.hasText(badgeIdString)) {
        int badgeId = Integer.parseInt(badgeIdString);
        // Get the badge
        badge = BadgeUtils.loadBadge(badgeId);
        context.getRequest().setAttribute("badge", badge);
      }
      BadgeCategory badgeCategory = null;
      if (badge != null){
      	badgeCategory = new BadgeCategory(db, badge.getCategoryId());
      } else {
      	badgeCategory = new BadgeCategory(db, Integer.parseInt(badgeCategoryIdString));
      }
      context.getRequest().setAttribute("badgeCategory", badgeCategory);
      if (badgeCategory != null) {
	      ProjectCategory projectCategory = new ProjectCategory(db, badgeCategory.getProjectCategoryId());
	      context.getRequest().setAttribute("projectCategory", projectCategory);
      }      
      BadgeCategoryList badgeCategoryList = new BadgeCategoryList();
      badgeCategoryList.setEnabled(Constants.TRUE);
      badgeCategoryList.setProjectCategoryId(badgeCategory.getProjectCategoryId());
      badgeCategoryList.setEmptyHtmlSelectRecord("--None--");
      badgeCategoryList.buildList(db);
      context.getRequest().setAttribute("badgeCategoryList", badgeCategoryList);
      //Get logo
      if (badge != null && badge.getLogoId() != -1) {
        FileItemList fileItemList = badge.retrieveFiles(db);
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
      Badge thisBadge = (Badge) context.getFormBean();
      thisBadge.setModifiedBy(getUserId(context));
      if (thisBadge.getId() != -1) {
        int count = thisBadge.update(db);
        if (count == 1) {
          recordUpdated = true;
        }
      } else {
        thisBadge.setEnteredBy(getUserId(context));
        if (thisBadge.getCategoryId() > -1) {
          BadgeCategory badgeCategory = new BadgeCategory(db, thisBadge.getCategoryId());
        }
        recordUpdated = thisBadge.insert(db);
      }
      if (recordUpdated) {
        thisBadge.saveAttachments(db, getUserId(context), getPath(context, "projects"));
        this.processInsertHook(context, thisBadge);
      } else {
        processErrors(context, thisBadge.getErrors());
      }
      context.getRequest().setAttribute("badgeCategoryId", thisBadge.getCategoryId());
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
    boolean recordDeleted = false;
    try {
      db = getConnection(context);
      String badgeIdString = context.getRequest().getParameter("badgeId");
      if (StringUtils.hasText(badgeIdString)) {
        int badgeId = Integer.parseInt(badgeIdString);
        Badge badge = BadgeUtils.loadBadge(badgeId);
        context.getRequest().setAttribute("badgeCategoryId", badge.getCategoryId());
        recordDeleted = badge.delete(db, getPath(context, "badges"));
        if (recordDeleted) {
          this.processDeleteHook(context, badge);
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

}

