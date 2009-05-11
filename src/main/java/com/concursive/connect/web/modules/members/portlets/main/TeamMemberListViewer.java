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
package com.concursive.connect.web.modules.members.portlets.main;

import com.concursive.connect.cache.utils.CacheUtils;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.login.utils.UserUtils;
import com.concursive.connect.web.modules.members.dao.TeamMember;
import com.concursive.connect.web.modules.members.dao.TeamMemberList;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import com.concursive.connect.web.portal.IPortletViewer;
import com.concursive.connect.web.portal.PortalUtils;
import static com.concursive.connect.web.portal.PortalUtils.*;
import com.concursive.connect.web.utils.LookupElement;
import com.concursive.connect.web.utils.LookupList;
import com.concursive.connect.web.utils.PagedListInfo;

import javax.portlet.PortletException;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import java.sql.Connection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Project team member list
 *
 * @author matt rajkowski
 * @created December 11, 2008
 */
public class TeamMemberListViewer implements IPortletViewer {

  // Pages
  private static final String VIEW_PAGE = "/projects_center_team.jsp";

  // Object Results
  private static final String CURRENT_TEAM_MEMBER = "currentMember";
  private static final String TEAM_MEMBER_MAP = "teamMemberMap";
  private static final String PAGED_LIST_INFO = "projectTeamInfo";

  public String doView(RenderRequest request, RenderResponse response) throws Exception {
    // The JSP to show upon success
    String defaultView = VIEW_PAGE;

    // Determine the project container to use
    Project project = findProject(request);

    // Check the user's permissions
    User user = getUser(request);
    if (!ProjectUtils.hasAccess(project.getId(), user, "project-team-view")) {
      throw new PortletException("Unauthorized to view in this project");
    }

    // Add this user's membership level (to allow for edit links and such)
    request.setAttribute(CURRENT_TEAM_MEMBER, PortalUtils.getCurrentTeamMember(request));

    // Determine the database connection to use
    Connection db = getConnection(request);

    // Determine the paging url
    PortletURL renderURL = response.createRenderURL();
    renderURL.setParameter("portlet-action", "show");
    renderURL.setParameter("portlet-object", "members");
    String url = renderURL.toString();

    // Paging will be used for remembering several list view settings
    PagedListInfo pagedListInfo = getPagedListInfo(request, PAGED_LIST_INFO);
    pagedListInfo.setLink(url);
    pagedListInfo.setItemsPerPage(-1);
    pagedListInfo.setDefaultSort("r.level, last_name", null);

    // Prepare the role-team map for organizing users into roles
    Map<LookupElement, TeamMemberList> teamMemberMap = new LinkedHashMap<LookupElement, TeamMemberList>();
    LookupList roleList = CacheUtils.getLookupList("lookup_project_role");
    for (LookupElement role : roleList) {
      teamMemberMap.put(role, new TeamMemberList());
    }

    // Load the records
    TeamMemberList teamMemberList = new TeamMemberList();
    teamMemberList.setProjectId(project.getId());
    teamMemberList.setPagedListInfo(pagedListInfo);
    teamMemberList.buildList(db);
    // Map the users to a role
    for (TeamMember thisMember : teamMemberList) {
      User thisUser = UserUtils.loadUser(thisMember.getUserId());
      thisMember.setUser(thisUser);
      // On the profile page, skip the project's owner
      if (project.getProfile() && project.getOwner() == thisUser.getId()) {
        continue;
      }
      // Put the user into the correct role
      for (LookupElement role : teamMemberMap.keySet()) {
        if (thisMember.getRoleId() == role.getLevel()) {
          TeamMemberList thisList = teamMemberMap.get(role);
          thisList.add(thisMember);
        }
      }
    }
    request.setAttribute(TEAM_MEMBER_MAP, teamMemberMap);

    // Record view
    PortalUtils.processSelectHook(request, teamMemberList);

    // JSP view
    return defaultView;
  }
}