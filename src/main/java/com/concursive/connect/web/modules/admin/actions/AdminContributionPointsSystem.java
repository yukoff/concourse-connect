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

import java.sql.Connection;

import com.concursive.commons.text.StringUtils;
import com.concursive.commons.web.mvc.actions.ActionContext;
import com.concursive.connect.web.controller.actions.GenericAction;
import com.concursive.connect.web.modules.contribution.dao.LookupContribution;
import com.concursive.connect.web.modules.contribution.dao.LookupContributionList;
import com.concursive.connect.web.utils.PagedListInfo;

/**
 * For administrator to adjust the contribution point system 
 *
 * @author Nandan
 * @created Aug 11, 2009
 */
public class AdminContributionPointsSystem extends GenericAction {

  //forwards
  public static final String DEFAULT_OK = "DefaultOK";
  //attributes
  public static final String ERROR_PERMISSION = "PermissionError";
  public static final String ERROR_SYSTEM = "SystemError";
  public static final String ERROR = "Error";
  public static final String ERROR_VALIDATION_MSG = "Points should be a positive integer value";
  public static final String LOOKUP_CONTRIBUTION_LIST = "lookupContributionList";
  public static final String SORT_BY_COLUMN = "points_awarded";

  public AdminContributionPointsSystem() {
  }

  /**
   *  Lists the Contribution point system
   */
  public String executeCommandDefault(ActionContext context) {
    if (!getUser(context).getAccessAdmin()) {
      return ERROR_PERMISSION;
    }
    Connection db = null;
    try {
      //get db connection
      db = getConnection(context);

      //get contribution point system values
      LookupContributionList lookupContributionList = new LookupContributionList();
      // set order by 
      PagedListInfo pagedListInfo = new PagedListInfo();
      pagedListInfo.setColumnToSortBy(SORT_BY_COLUMN);
      pagedListInfo.setItemsPerPage(0);
      pagedListInfo.setSortOrder("desc");
      lookupContributionList.setPagedListInfo(pagedListInfo);
      lookupContributionList.buildList(db);

      //load the list for display
      context.getRequest().setAttribute(LOOKUP_CONTRIBUTION_LIST, lookupContributionList);
    } catch (Exception e) {
      e.printStackTrace();
      context.getRequest().setAttribute(ERROR, e);
      return (ERROR_SYSTEM);
    } finally {
      freeConnection(context, db);
    }
    return DEFAULT_OK;
  }

  /**
   * Updates the Contribution point system
   */
  public String executeCommandSave(ActionContext context) {
    if (!getUser(context).getAccessAdmin()) {
      return ERROR_PERMISSION;
    }
    Connection db = null;
    try {
      //get db connection
      db = getConnection(context);

      //get contribution point system values
      LookupContributionList lookupContributionList = new LookupContributionList();
      // set order by 
      PagedListInfo pagedListInfo = new PagedListInfo();
      pagedListInfo.setColumnToSortBy(SORT_BY_COLUMN);
      pagedListInfo.setItemsPerPage(0);
      pagedListInfo.setSortOrder("desc");
      lookupContributionList.setPagedListInfo(pagedListInfo);
      lookupContributionList.buildList(db);

      for (LookupContribution lookupContribution : lookupContributionList) {
        String pointsAwarded = (String) context.getRequest().getParameter(lookupContribution.getId() + "");
        if (StringUtils.hasText(pointsAwarded) && StringUtils.isNumber(pointsAwarded)) {
          lookupContribution.setPointsAwarded(pointsAwarded);
          lookupContribution.update(db);
        } else {
          context.getRequest().setAttribute("" + lookupContribution.getId(), pointsAwarded);
          context.getRequest().setAttribute(ERROR + lookupContribution.getId(), ERROR_VALIDATION_MSG);
        }
      }

      //load the list for display
      context.getRequest().setAttribute(LOOKUP_CONTRIBUTION_LIST, lookupContributionList);
    } catch (Exception e) {
      e.printStackTrace();
      context.getRequest().setAttribute(ERROR, e);
      return (ERROR_SYSTEM);
    } finally {
      freeConnection(context, db);
    }
    return DEFAULT_OK;
  }
}
