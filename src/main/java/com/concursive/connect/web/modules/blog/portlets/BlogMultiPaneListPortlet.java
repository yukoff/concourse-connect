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
package com.concursive.connect.web.modules.blog.portlets;

import com.concursive.connect.Constants;
import com.concursive.connect.web.modules.blog.dao.BlogPostList;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.portal.PortalUtils;
import com.concursive.connect.web.utils.PagedListInfo;

import javax.portlet.*;
import java.io.IOException;

/**
 * A blog listing portlet
 *
 * @author matt rajkowski
 * @created April 30, 2008
 */
public class BlogMultiPaneListPortlet extends GenericPortlet {
  private static final String VIEW_PAGE = "/portlets/blog_multi_pane_list/blog_multi_pane_list-view.jsp";
  private static final String VIEW_PAGE_ALL = "/portlets/blog_multi_pane_list/blog_multi_pane_list-view-all.jsp";

  private static final String BLOG_LIST = "blogList";

  public void doView(RenderRequest request, RenderResponse response)
      throws PortletException, IOException {
    try {
      String defaultView = VIEW_PAGE;
      User thisUser = PortalUtils.getUser(request);
      String currentTab = request.getParameter("tab");
      if (currentTab != null) {

        // Change the sorting
        PagedListInfo tmpInfo = new PagedListInfo();
        tmpInfo.setId("dashboardBlogListInfo");
        tmpInfo.setColumnToSortBy("n.start_date");
        tmpInfo.setSortOrder("desc");
        tmpInfo.setItemsPerPage(PagedListInfo.DEFAULT_ITEMS_PER_PAGE);

        // Blogs to show
        BlogPostList newsList = new BlogPostList();
        newsList.setForUser(thisUser.getId());
        newsList.setPagedListInfo(tmpInfo);
        newsList.setCurrentNews(Constants.TRUE);
        newsList.buildList(PortalUtils.useConnection(request));
        request.setAttribute(BLOG_LIST, newsList);
        defaultView = VIEW_PAGE_ALL;
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