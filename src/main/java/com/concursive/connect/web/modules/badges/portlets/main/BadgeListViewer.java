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

import com.concursive.connect.web.modules.badges.dao.Badge;
import com.concursive.connect.web.modules.badges.dao.BadgeList;
import com.concursive.connect.web.modules.badges.dao.ProjectBadge;
import com.concursive.connect.web.modules.badges.dao.ProjectBadgeList;
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
 * Project badge list
 *
 * @author matt rajkowski
 * @created December 30, 2008
 */
public class BadgeListViewer implements IPortletViewer {

  // Pages
  private static final String VIEW_PAGE = "/projects_center_badges.jsp";

  // Preferences
  // NOTE: none

  // Object Results
  private static final String BADGE_LIST = "badgeList";
  private static final String PROJECT_BADGE_MAP = "projectBadgeMap";
  private static final String BADGE_MEMBER_COUNT_MAP = "badgeMemberCountMap";

  public String doView(RenderRequest request, RenderResponse response) throws Exception {
    // The JSP to show upon success
    String defaultView = VIEW_PAGE;

    // Determine the project container to use
    Project project = findProject(request);

    // Check the user's permissions
    User user = getUser(request);
    if (!ProjectUtils.hasAccess(project.getId(), user, "project-badges-view")) {
      throw new PortletException("Unauthorized to view in this project");
    }

    // Determine the database connection
    Connection db = useConnection(request);

    // Load the records
    ProjectBadgeList projectBadges = new ProjectBadgeList();
    projectBadges.setProjectId(project.getId());
    projectBadges.buildList(db);
    // For each project badge, add the individual badge for grouping them
    BadgeList badgeList = new BadgeList();
    for (ProjectBadge projectBadge : projectBadges) {
      Badge badge = BadgeUtils.loadBadge(projectBadge.getBadgeId());
      badgeList.add(badge);
    }
    request.setAttribute(BADGE_LIST, badgeList);

    // For showing which badges this project already has, and when
    Map<Integer, ProjectBadge> projectBadgeMap = BadgeUtils.createBadgeIdToProjectBadgeMap(projectBadges);
    request.setAttribute(PROJECT_BADGE_MAP, projectBadgeMap);

    // Count the number of other members for this badge
    Map<Integer, Integer> memberCountMap = badgeList.findBadgeMemberCount(db);
    request.setAttribute(BADGE_MEMBER_COUNT_MAP, memberCountMap);

    // JSP view
    return defaultView;
  }
}