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
package com.concursive.connect.web.modules.blog.portlets.main;

import com.concursive.commons.text.StringUtils;
import com.concursive.connect.Constants;
import com.concursive.connect.web.modules.blog.dao.BlogPostList;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import com.concursive.connect.web.portal.IPortletViewer;
import com.concursive.connect.web.portal.PortalUtils;
import static com.concursive.connect.web.portal.PortalUtils.*;
import com.concursive.connect.web.utils.PagedListInfo;

import javax.portlet.PortletException;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import java.sql.Connection;

/**
 * Project blog list
 *
 * @author matt rajkowski
 * @created October 28, 2008
 */
public class BlogListViewer implements IPortletViewer {

  // Pages
  private static final String VIEW_PAGE = "/projects_center_news.jsp";

  // Object Results
  private static final String BLOG_LIST = "newsList";
  private static final String PAGED_LIST_INFO = "projectNewsInfo";

  public String doView(RenderRequest request, RenderResponse response) throws Exception {
    // The JSP to show upon success
    String defaultView = VIEW_PAGE;

    // Determine the project container to use
    Project project = findProject(request);

    // Check the user's permissions
    User user = getUser(request);
    if (!ProjectUtils.hasAccess(project.getId(), user, "project-news-view")) {
      throw new PortletException("Unauthorized to view in this project");
    }

    // Determine the database connection to use
    Connection db = getConnection(request);

    // Determine the paging url
    PortletURL renderURL = response.createRenderURL();
    renderURL.setParameter("portlet-action", "show");
    renderURL.setParameter("portlet-object", "blog");
    String url = renderURL.toString();

    // Paging will be used for remembering several list view settings
    PagedListInfo pagedListInfo = getPagedListInfo(request, PAGED_LIST_INFO);
    pagedListInfo.setLink(url);
    pagedListInfo.setColumnToSortBy("n.priority_id asc, n.start_date desc");

    // Load the records
    BlogPostList newsList = new BlogPostList();
    newsList.setProjectId(project.getId());
    newsList.setPagedListInfo(pagedListInfo);
    // Limit the records displayed based on access
    if ("archived".equals(pagedListInfo.getListView()) && ProjectUtils.hasAccess(project.getId(), user, "project-news-view-archived")) {
      newsList.setArchivedNews(Constants.TRUE);
    } else if ("unreleased".equals(pagedListInfo.getListView()) && ProjectUtils.hasAccess(project.getId(), user, "project-news-view-unreleased")) {
      newsList.setUnreleasedNews(Constants.TRUE);
    } else if ("drafts".equals(pagedListInfo.getListView()) && ProjectUtils.hasAccess(project.getId(), user, "project-news-view-unreleased")) {
      newsList.setIncompleteNews(Constants.TRUE);
    } else {
      if (ProjectUtils.hasAccess(project.getId(), user, "project-news-view-unreleased")) {
        //all news (project access)
        newsList.setOverviewAll(true);
      } else {
        //current news (default)
        newsList.setCurrentNews(Constants.TRUE);
      }
    }

    // Determine the current view
    String filter = getPageView(request);
    String filterParam = getPageParameter(request);
    // By Category
    if ("category".equals(filter)) {
      if ("-1".equals(filterParam)) {
        newsList.setCheckNullCategoryId(true);
      } else {
        newsList.setCategoryId(filterParam);
      }
    }
    // By Author
    if ("author".equals(filter)) {
      newsList.setEnteredBy(filterParam);
    }
    // By Date
    if ("date".equals(filter)) {
      newsList.setPublishedYearMonth(filterParam);
    }

    if (StringUtils.isNumber(filter)) {
      pagedListInfo.setPage(Integer.parseInt(filter));
    }

    // If the filter is a # then use paging...
    newsList.setBuildCommentCount(true);
    newsList.buildList(db);
    request.setAttribute(BLOG_LIST, newsList);

    // Record view
    PortalUtils.processSelectHook(request, newsList);

    // JSP view
    return defaultView;
  }
}