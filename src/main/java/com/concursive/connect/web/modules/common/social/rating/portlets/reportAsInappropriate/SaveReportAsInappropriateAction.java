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
package com.concursive.connect.web.modules.common.social.rating.portlets.reportAsInappropriate;

import com.concursive.commons.text.StringUtils;
import com.concursive.commons.web.mvc.beans.GenericBean;
import com.concursive.connect.Constants;
import com.concursive.connect.web.modules.ModuleUtils;
import com.concursive.connect.web.modules.common.social.rating.dao.Rating;
import com.concursive.connect.web.modules.common.social.rating.portlets.ReportAsInappropriateBean;
import com.concursive.connect.web.modules.issues.dao.Ticket;
import com.concursive.connect.web.modules.issues.dao.TicketCategoryList;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.portal.IPortletAction;
import com.concursive.connect.web.portal.PortalUtils;
import static com.concursive.connect.web.portal.PortalUtils.findProject;
import static com.concursive.connect.web.portal.PortalUtils.getUser;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import java.sql.Connection;

/**
 * saves a report of inappropriate content
 *
 * @author Kailash Bhoopalam
 * @created February 18, 2008
 */
public class SaveReportAsInappropriateAction implements IPortletAction {

  private static final String PREF_PROJECT_UNIQUE_ID = "project";
  private static final String PREF_TICKET_CATEGORY = "ticketCategory";

  public GenericBean processAction(ActionRequest request, ActionResponse response) throws Exception {

    // Will find the specified project in preferences first
    Project selectedProject = findProject(request);
    if (selectedProject == null) {
      throw new PortletException("Selected Project is null a project was not specified in the preferences or the specified project was not found");
    }

    // Check the user's permissions
    User user = getUser(request);
    if (!user.isLoggedIn()) {
      throw new PortletException("User needs to be logged in");
    }

    Connection db = PortalUtils.useConnection(request);
    String ticketCategory = request.getPreferences().getValue(PREF_TICKET_CATEGORY, null);
    int ticketCategoryId = -1;
    if (StringUtils.hasText(ticketCategory)) {
      TicketCategoryList ticketCategoryList = new TicketCategoryList();
      ticketCategoryList.setCatLevel(0);
      ticketCategoryList.setProjectId(selectedProject.getId());
      ticketCategoryList.setEnabledState(Constants.TRUE);
      ticketCategoryList.buildList(db);
      ticketCategoryId = ticketCategoryList.getIdFromValue(ticketCategory);
    }

    ReportAsInappropriateBean reportAsInappropriateBean = (ReportAsInappropriateBean) PortalUtils.getFormBean(request, ReportAsInappropriateBean.class);
    reportAsInappropriateBean.setLinkModuleId(ModuleUtils.getLinkModuleIdFromModuleName(reportAsInappropriateBean.getLinkModule()));
    Ticket ticket = new Ticket();
    if (reportAsInappropriateBean.populateTicketFromBean(ticket)) {
      ticket.setProjectId(selectedProject.getId());
      ticket.setContactId(user.getId());
      ticket.setEnteredBy(user.getId());
      ticket.setModifiedBy(user.getId());
      ticket.setCatCode(ticketCategoryId);
      if (ticket.insert(db)) {
        //Insert inappropriate rating if the object being reported IS NOT a profile
        if (!reportAsInappropriateBean.getLinkModule().equals(ModuleUtils.MODULENAME_PROFILE)) {
          Rating.save(db, user.getId(), reportAsInappropriateBean.getLinkProjectId(), reportAsInappropriateBean.getLinkItemId(), String.valueOf(Rating.INAPPROPRIATE_COMMENT), ModuleUtils.getTableFromModuleName(reportAsInappropriateBean.getLinkModule()), ModuleUtils.getPrimaryKeyFromModuleName(reportAsInappropriateBean.getLinkModule()), Constants.TRUE);

          //PortalUtils.processInsertHook(request, reportAsInappropriateBean);
        }
      }
    }
    String ctx = request.getContextPath();
    response.sendRedirect(ctx + "/close_panel_refresh.jsp");
    return null;
  }

}
