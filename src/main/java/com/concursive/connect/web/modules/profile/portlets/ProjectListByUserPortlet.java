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

import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.members.dao.TeamMember;
import com.concursive.connect.web.modules.members.dao.TeamMemberList;
import com.concursive.connect.web.modules.messages.dao.PrivateMessageList;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.dao.ProjectCategory;
import com.concursive.connect.web.modules.profile.dao.ProjectCategoryList;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import com.concursive.connect.web.modules.reviews.dao.ProjectRating;
import com.concursive.connect.web.modules.reviews.dao.ProjectRatingList;
import com.concursive.connect.web.portal.PortalUtils;
import com.concursive.connect.web.utils.PagedListInfo;

import javax.portlet.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Iterator;

/**
 * List of projects for a user
 *
 * @author Kailash Bhoopalam
 * @created Sept 02, 2008
 */
public class ProjectListByUserPortlet extends GenericPortlet {

  // Pages
  private static final String VIEW_PAGE = "/portlets/projects_by_user/projects_by_user-view.jsp";

  // Request Attributes
  private static final String TEAM_MEMBER_LIST = "teamMemberList";
  private static final String PROJECT_RATING_MAP = "projectRatingMap";
  private static final String PRIVATE_MESSAGE_MAP = "privateMessageMap";
  private static final String TITLE = "title";
  private static final String CATEGORY_NAME = "categoryName";
  private static final String MODIFY_NOTIFICATION = "modifyNotification";
  private static final String PAGED_LIST_INFO = "pagedListInfo";

  // Preferences
  private static final String PREF_CATEGORY_NAME = "category";
  private static final String PREF_TITLE = "title";
  private static final String PREF_LIMIT = "limit";
  private static final String PREF_SHOW_IF_EMPTY = "showIfEmpty";

  public void doView(RenderRequest request, RenderResponse response)
      throws PortletException, IOException {
    try {
      String defaultView = VIEW_PAGE;
      // Get global preferences
      String maxNumberOfProjectsToShow = request.getPreferences().getValue(PREF_LIMIT, "-1");
      String categoryName = request.getPreferences().getValue(PREF_CATEGORY_NAME, null);
      String title = request.getPreferences().getValue(PREF_TITLE, null);
      boolean showIfEmpty = Boolean.parseBoolean(request.getPreferences().getValue(PREF_SHOW_IF_EMPTY, "false"));

      String viewType = request.getParameter("viewType");
      // Get the projects reviewed by this profile owner...
      int userId = -1;

      try {
        Connection db = PortalUtils.getConnection(request);
        if ("setNotification".equals(viewType)) {

          int teamMemberId = Integer.parseInt(request.getParameter("teamMemberId"));
          TeamMember teamMember = new TeamMember(db, teamMemberId);
          teamMember.setModifiedBy(PortalUtils.getUser(request).getId());
          teamMember.setNotification(request.getParameter("notification"));
          teamMember.update(db);

          response.setContentType("text/html");
          PrintWriter out = response.getWriter();
          out.println("");
          out.flush();
          defaultView = null;
        } else {
          User user = PortalUtils.getUser(request);
          Project project = PortalUtils.findProject(request);

          userId = PortalUtils.getProject(request).getOwner();

          // Get category id for the provided category name
          ProjectCategoryList categories = new ProjectCategoryList();
          categories.setEnabled(true);
          categories.setTopLevelOnly(true);
          categories.buildList(db);

          // Look for the specified category
          ProjectCategory category = categories.getFromValue(categoryName);

          if (category == null) {
            // The category was not found, so skip this portlet
            defaultView = null;
          } else {
            // Use paged data for sorting
            PagedListInfo pagedListInfo = new PagedListInfo();
            pagedListInfo.setItemsPerPage(maxNumberOfProjectsToShow);
            request.setAttribute(PAGED_LIST_INFO, pagedListInfo);

            // Fetch JUST the user's team member entry
            TeamMemberList teamMemberList = new TeamMemberList();
            teamMemberList.setUserId(userId);
            teamMemberList.setStatus(TeamMember.STATUS_ADDED);
            teamMemberList.setBuildProject(true);
            teamMemberList.setCategoryId((category != null) ? category.getId() : null);
            teamMemberList.setPagedListInfo(pagedListInfo);
            teamMemberList.buildList(db);

            if (teamMemberList.size() > 0 || showIfEmpty) {
              // fetch ratings by the user
              ProjectRatingList projectRatingList = new ProjectRatingList();
              projectRatingList.setEnteredBy(userId);
              projectRatingList.setOpenProjectsOnly(true);
              projectRatingList.setCategoryId(category.getId());
              // @todo include only the teamMemberList projects instead of all rated
              projectRatingList.buildList(PortalUtils.getConnection(request));

              HashMap<Integer, ProjectRating> projectRatingMap = new HashMap<Integer, ProjectRating>();
              HashMap<Integer, Integer> privateMessageMap = new HashMap<Integer, Integer>();
              Iterator i = teamMemberList.iterator();
              while (i.hasNext()) {
                TeamMember teamMember = (TeamMember) i.next();
                // Verify this user can access the project
                if (user.getId() != teamMember.getUserId() && !ProjectUtils.hasAccess(teamMember.getProjectId(), user, "project-profile-view")) {
                  i.remove();
                  continue;
                }

                // Retrieve the team member's rating for display
                ProjectRating projectRating = projectRatingList.getRatingForProject(teamMember.getProjectId());
                projectRatingMap.put(teamMember.getProjectId(), projectRating);

                // Fetch the new messages only if the user is viewing his profile
                if (user.getProfileProjectId() == project.getId()) {
                  //Check if the user has permissions to view messages in the projects in which he is a team member
                  if (ProjectUtils.hasAccess(teamMember.getProjectId(), user, "project-private-messages-view")) {
                    privateMessageMap.put(teamMember.getProjectId(), PrivateMessageList.queryUnreadCountForProject(db, teamMember.getProjectId()));
                  }
                }
              }
              request.setAttribute(PRIVATE_MESSAGE_MAP, privateMessageMap);
              request.setAttribute(TEAM_MEMBER_LIST, teamMemberList);
              request.setAttribute(PROJECT_RATING_MAP, projectRatingMap);
              request.setAttribute(CATEGORY_NAME, categoryName);
              request.setAttribute(TITLE, title);
              if (userId == PortalUtils.getUser(request).getId()) {
                request.setAttribute(MODIFY_NOTIFICATION, "true");
              } else {
                request.setAttribute(MODIFY_NOTIFICATION, "false");
              }
            } else {
              defaultView = null;
            }
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }

      // JSP view
      if (defaultView != null) {
        PortletContext context = getPortletContext();
        PortletRequestDispatcher requestDispatcher =
            context.getRequestDispatcher(defaultView);
        requestDispatcher.include(request, response);
      }
    } catch (Exception e) {
      throw new PortletException(e.getMessage());
    }
  }
}