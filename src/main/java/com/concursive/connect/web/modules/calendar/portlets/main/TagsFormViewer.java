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
package com.concursive.connect.web.modules.calendar.portlets.main;

import static com.concursive.connect.web.portal.PortalUtils.findProject;
import static com.concursive.connect.web.portal.PortalUtils.getConnection;
import static com.concursive.connect.web.portal.PortalUtils.getUser;

import java.sql.Connection;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import com.concursive.connect.web.modules.ModuleUtils;
import com.concursive.connect.web.modules.calendar.dao.Meeting;
import com.concursive.connect.web.modules.common.social.tagging.dao.TagList;
import com.concursive.connect.web.modules.common.social.tagging.dao.TagLogList;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.login.utils.UserUtils;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import com.concursive.connect.web.portal.IPortletViewer;
import com.concursive.connect.web.portal.PortalUtils;
import com.concursive.connect.web.utils.PagedListInfo;

/**
 * Displays the Tags form page
 *
 * @author Nanda Kumar
 * @created Oct 14, 2009
 */
public class TagsFormViewer implements IPortletViewer {
  // pages

  private static final String TAGS_FORM_ADD = "/tags_form_add.jsp";
  // attributes
  private static final String POPUP = "popup";
  private static final String POPULAR_TAGS = "popularTags";
  private static final String USER_TAGS = "userTags";
  private static final String USER_RECENT_TAGS = "userRecentTags";

  public TagsFormViewer() {
  }

  public String doView(RenderRequest request, RenderResponse response) throws Exception {
    // get the modules details
    int linkItemId = PortalUtils.getPageViewAsInt(request);
    String moduleName = PortalUtils.getPageDomainObject(request);

    // set the request attributes
    request.setAttribute(POPUP, request.getParameter(POPUP));

    //validate the object being tagged
    if (!ModuleUtils.MODULENAME_CALENDAR.equals(moduleName)) {
      throw new PortletException("Module mismatch while tagging object");
    }

    // Determine the project container to use
    Project project = findProject(request);
    if (project == null) {
      throw new Exception("Project is null");
    }

    // Check the user's permissions
    User user = getUser(request);

    if (!ProjectUtils.hasAccess(project.getId(), user, "project-profile-view")) {
      throw new PortletException("Unauthorized to access this profile");
    }
    if (!ProjectUtils.hasAccess(project.getId(), user, "project-calendar-view")) {
      throw new PortletException("Unauthorized to view this record");
    }

    // get database connection
    Connection db = getConnection(request);

    //Load the object being tagged to make sure it exists
    try {
      Meeting meeting = new Meeting(db, linkItemId);
    } catch (Exception e) {
      throw new Exception(e.getMessage());
    }

    // get popular tags for the module
    TagList popularTagList = new TagList();
    popularTagList.setTableName(Meeting.TABLE);
    popularTagList.setUniqueField(Meeting.PRIMARY_KEY);
    popularTagList.setLinkItemId(linkItemId);
    PagedListInfo tagListInfo = new PagedListInfo();
    tagListInfo.setColumnToSortBy("tag_count DESC, tag");
    tagListInfo.setItemsPerPage(10);
    popularTagList.setPagedListInfo(tagListInfo);
    popularTagList.buildList(db);
    request.setAttribute(POPULAR_TAGS, popularTagList);

    // get the user's tags used for this item
    TagLogList tagLogList = new TagLogList();
    tagLogList.setTableName(Meeting.TABLE);
    tagLogList.setUniqueField(Meeting.PRIMARY_KEY);
    tagLogList.setUserId(user.getId());
    tagLogList.setLinkItemId(linkItemId);
    tagLogList.buildList(db);
    request.setAttribute(USER_TAGS, tagLogList);

    // get the recent tags used by the user
    request.setAttribute(USER_RECENT_TAGS, UserUtils.loadRecentlyUsedTagsByUser(db, user.getId()));

    return TAGS_FORM_ADD;
  }
}
