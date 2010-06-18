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

import com.concursive.connect.web.modules.badges.dao.*;
import com.concursive.connect.web.modules.badges.utils.BadgeUtils;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import com.concursive.connect.web.portal.IPortletViewer;
import com.concursive.connect.web.portal.PortalUtils;
import static com.concursive.connect.web.portal.PortalUtils.*;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import java.sql.Connection;
import java.util.Map;

/**
 * Project add badge ajax call
 *
 * @author matt rajkowski
 * @created December 30, 2008
 */
public class BadgeAddViewer implements IPortletViewer {

  // Pages
  private static final String VIEW_PAGE = "/project_badge_detail_include.jsp";

  // Preferences
  // NOTE: none

  // Object Results
  private static final String BADGE = "badge";
  private static final String PROJECT_BADGE = "projectBadge";
  private static final String BADGE_MEMBER_COUNT_MAP = "badgeMemberCountMap";
  private static final String BADGE_MEMBER_COUNT = "memberCount";

  public String doView(RenderRequest request, RenderResponse response) throws Exception {
    // The JSP to show upon success
    String defaultView = VIEW_PAGE;

    // Determine the project container to use
    Project project = findProject(request);

    // Check the user's permissions
    User user = getUser(request);
    if (!ProjectUtils.hasAccess(project.getId(), user, "project-badges-admin")) {
      throw new PortletException("Unauthorized to view in this project");
    }

    // Determine the badge being added
    int badgeId = Integer.parseInt(request.getParameter("id"));

    // Determine the database connection
    Connection db = useConnection(request);

    // Make sure the badge matches the project category
    Badge badge = BadgeUtils.loadBadge(badgeId);
    BadgeCategory badgeCategory = new BadgeCategory(db, badge.getCategoryId());
    if (badgeCategory.getProjectCategoryId() != project.getCategoryId()) {
      throw new PortletException("Badge category does not match listing category id");
    }
    request.setAttribute(BADGE, badge);

    boolean recordInserted = false;
    // @todo move this to a BadgeUtils for adding/removing badges
    // Make sure the link does not already exist, then insert the badge
    ProjectBadgeList projectBadgeList = new ProjectBadgeList();
    projectBadgeList.setProjectId(project.getId());
    projectBadgeList.setBadgeId(badge.getId());
    projectBadgeList.buildList(db);
    if (projectBadgeList.size() == 0 && (user.getAccessAdmin() || !badge.getSystemAssigned())) {
      ProjectBadge projectBadge = new ProjectBadge();
      projectBadge.setProjectId(project.getId());
      projectBadge.setBadgeId(badge.getId());
      recordInserted = projectBadge.insert(db);
      projectBadge = new ProjectBadge(db, projectBadge.getId());
      request.setAttribute(PROJECT_BADGE, projectBadge);

      if (recordInserted) {
        //trigger the workflow
        PortalUtils.processInsertHook(request, projectBadge);
      }
    }

    // Count the number of members for this badge
    BadgeList badgeList = new BadgeList();
    badgeList.add(badge);
    Map<Integer, Integer> memberCountMap = badgeList.findBadgeMemberCount(db);
    request.setAttribute(BADGE_MEMBER_COUNT_MAP, memberCountMap);
    request.setAttribute(BADGE_MEMBER_COUNT, String.valueOf(memberCountMap.get(badge.getId())));

    // JSP view
    return defaultView;
  }
}