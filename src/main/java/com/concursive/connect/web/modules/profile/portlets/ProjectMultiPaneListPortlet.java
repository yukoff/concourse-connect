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
package com.concursive.connect.web.modules.profile.portlets;

import com.concursive.connect.Constants;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.profile.dao.ProjectList;
import com.concursive.connect.web.portal.PortalUtils;
import com.concursive.connect.web.utils.PagedListInfo;

import javax.portlet.*;
import java.io.IOException;

/**
 * Project list portlet
 *
 * @author matt rajkowski
 * @created April 30, 2008
 */
public class ProjectMultiPaneListPortlet extends GenericPortlet {
  private static final String VIEW_PAGE = "/portlets/project_multi_pane_list/project_multi_pane_list-view.jsp";
  private static final String VIEW_PAGE_ALL = "/portlets/project_multi_pane_list/project_multi_pane_list-view-all.jsp";

  private static final String PROJECT_LIST = "projectList";
  private static final String PROJECT_LIST_SESSION = "projectListSession";

  public void doView(RenderRequest request, RenderResponse response)
      throws PortletException, IOException {
    try {
      // Shows the multipanelist if no tab is selected
      String defaultView = VIEW_PAGE;
      User thisUser = PortalUtils.getUser(request);

      // Use paged data for sorting
      PagedListInfo pagedListInfo = new PagedListInfo();
      pagedListInfo.setItemsPerPage(0);

      // currentTab = top_rated, recent, new, closed, all
      String currentTab = request.getParameter("tab");
      if (currentTab != null) {
        // Projects to show
        ProjectList projects = new ProjectList();
        projects.setPagedListInfo(pagedListInfo);
        projects.setGroupId(thisUser.getGroupId());
        projects.setProjectsForUser(thisUser.getId());
        projects.setPortalState(Constants.FALSE);
        if ("top_rated".equals(currentTab)) {
          projects.setUserRating(5);
        }
        if ("recent".equals(currentTab)) {
          projects.setDaysLastAccessed(10);

          // Change the sorting
          /*
          PagedListInfo tmpInfo = new PagedListInfo();
          tmpInfo.setId("dashboardProjectListInfo");
          tmpInfo.setColumnToSortBy("last_accessed");
          tmpInfo.setSortOrder("desc");
          tmpInfo.setItemsPerPage(PagedListInfo.DEFAULT_ITEMS_PER_PAGE);
          projects.setPagedListInfo(tmpInfo);
          */
        }
        if ("new".equals(currentTab)) {
          projects.setIncludeGuestProjects(true);
          projects.setDaysLastApproved(10);

          PagedListInfo tmpInfo = new PagedListInfo();
          tmpInfo.setId("dashboardProjectListInfo");
          tmpInfo.setColumnToSortBy("approvaldate");
          tmpInfo.setSortOrder("desc");
          tmpInfo.setItemsPerPage(PagedListInfo.DEFAULT_ITEMS_PER_PAGE);
          projects.setPagedListInfo(tmpInfo);
        }
        if ("closed".equals(currentTab)) {
          projects.setClosedProjectsOnly(true);
        } else {
          projects.setOpenProjectsOnly(true);
        }
        projects.setInvitationAcceptedOnly(true);
        projects.setBuildOverallIssues(true);
        projects.buildList(PortalUtils.getConnection(request));
        projects.buildTeam(PortalUtils.getConnection(request));
        request.setAttribute(PROJECT_LIST, projects);
        defaultView = VIEW_PAGE_ALL;
        // Remember the user's tab selection for next time the page is drawn
        request.getPortletSession().setAttribute(PROJECT_LIST_SESSION, currentTab);
      }

      // JSP view
      PortletContext context = getPortletContext();
      PortletRequestDispatcher requestDispatcher =
          context.getRequestDispatcher(defaultView);
      requestDispatcher.include(request, response);
    } catch (Exception e) {
      throw new PortletException(e.getMessage());
    }
  }
}