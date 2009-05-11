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
package com.concursive.connect.web.modules.badges.portlets.main;

import com.concursive.connect.Constants;
import com.concursive.connect.web.modules.badges.dao.*;
import com.concursive.connect.web.modules.badges.utils.BadgeUtils;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import com.concursive.connect.web.portal.IPortletViewer;
import static com.concursive.connect.web.portal.PortalUtils.*;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import java.sql.Connection;
import java.util.Map;

/**
 * Project badge management
 *
 * @author matt rajkowski
 * @created December 30, 2008
 */
public class BadgeManageViewer implements IPortletViewer {

  // Pages
  private static final String VIEW_PAGE = "/projects_center_badges_add.jsp";

  // Preferences
  // NOTE: none

  // Object Results
  private static final String PROJECT_BADGE_MAP = "projectBadgeMap";
  private static final String BADGE_CATEGORY_MAP = "badgeCategoryMap";
  private static final String BADGE_MEMBER_COUNT_MAP = "badgeMemberCountMap";

  public String doView(RenderRequest request, RenderResponse response) throws Exception {
    // The JSP to show upon success
    String defaultView = VIEW_PAGE;

    // Determine the project container to use
    Project project = findProject(request);

    // Check the user's permissions
    User user = getUser(request);
    if (!ProjectUtils.hasAccess(project.getId(), user, "project-badges-admin")) {
      throw new PortletException("Unauthorized to manage in this project");
    }

    // Determine the database connection
    Connection db = getConnection(request);

    // Load this project's badges
    ProjectBadgeList projectBadgeList = new ProjectBadgeList();
    projectBadgeList.setProjectId(project.getId());
    projectBadgeList.buildList(db);
    // Determine which badges this project already has for related info
    Map<Integer, ProjectBadge> projectBadgeMap = BadgeUtils.createBadgeIdToProjectBadgeMap(projectBadgeList);
    request.setAttribute(PROJECT_BADGE_MAP, projectBadgeMap);

    // Load the complete list of badges for this project category
    BadgeList badgeList = new BadgeList();
    badgeList.setEnabled(Constants.TRUE);
    badgeList.setBuildLogos(true);
    badgeList.buildList(db);

    // Group the badges into categories
    BadgeCategoryList badgeCategoryList = new BadgeCategoryList();
    badgeCategoryList.setProjectCategoryId(project.getCategoryId());
    badgeCategoryList.setEnabled(Constants.TRUE);
    badgeCategoryList.buildList(db);
    Map<BadgeCategory, BadgeList> badgeByCategoryMap = BadgeUtils.createBadgeByCategoryMap(badgeList, badgeCategoryList);
    request.setAttribute(BADGE_CATEGORY_MAP, badgeByCategoryMap);

    // Count the number of members for each badge
    Map<Integer, Integer> memberCountMap = badgeList.findBadgeMemberCount(db);
    request.setAttribute(BADGE_MEMBER_COUNT_MAP, memberCountMap);

    // JSP view
    return defaultView;
  }
}