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
package com.concursive.connect.web.modules.common.social.tagging.portlets.tagCloud;

import com.concursive.commons.text.StringUtils;
import com.concursive.connect.Constants;
import com.concursive.connect.web.modules.common.social.tagging.dao.TagList;
import com.concursive.connect.web.modules.common.social.tagging.dao.UniqueTagList;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.dao.ProjectCategory;
import com.concursive.connect.web.modules.profile.dao.ProjectCategoryList;
import com.concursive.connect.web.portal.IPortletViewer;
import com.concursive.connect.web.portal.PortalUtils;
import com.concursive.connect.web.utils.PagedListInfo;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import java.io.IOException;
import java.sql.Connection;

/**
 * Tag Cloud
 *
 * @author Kailash Bhoopalam
 * @created November 10, 2008
 */
public class TagCloudViewer implements IPortletViewer {

  // Pages
  private static final String VIEW_PAGE1 = "/portlets/tag_cloud_by_project/tag_cloud_by_project-view.jsp";

  // Preferences
  private static final String PREF_TITLE = "title";
  private static final String PREF_LIMIT = "limit";
  private static final String PREF_SORTBY_TAGCOUNT = "sortByTagCount";
  private static final String PREF_HAS_MORE_URL = "hasMoreURL";
  private static final String PREF_HAS_MORE_TITLE = "hasMoreTitle";
  private static final String PREF_CATEGORY = "category";

  // Attribute names for objects available in the view
  private static final String TITLE = "title";
  private static final String TAG_LIST = "tagList";
  private static final String HAS_MORE_URL = "hasMoreURL";
  private static final String HAS_MORE_TITLE = "hasMoreTitle";
  private static final String CATEGORY_NAME = "categoryName";

  public String doView(RenderRequest request, RenderResponse response)
      throws PortletException, IOException {
    try {
      String defaultView = VIEW_PAGE1;

      // Set global preferences
      request.setAttribute(TITLE, request.getPreferences().getValue(PREF_TITLE, null));
      String limitString = request.getPreferences().getValue(PREF_LIMIT, null);
      String sortByTagCount = request.getPreferences().getValue(PREF_SORTBY_TAGCOUNT, null);
      String hasMoreURL = request.getPreferences().getValue(PREF_HAS_MORE_URL, null);
      String hasMoreTitle = request.getPreferences().getValue(PREF_HAS_MORE_TITLE, null);
      String projectCategory = request.getPreferences().getValue(PREF_CATEGORY, null);
      request.setAttribute(HAS_MORE_URL, hasMoreURL);
      request.setAttribute(HAS_MORE_TITLE, hasMoreTitle);

      int limit = -1;
      if (StringUtils.hasText(limitString)) {
        limit = Integer.parseInt(limitString);
      }

      Connection db = PortalUtils.getConnection(request);
      //If preference for category is not set, get it from the page view
      if (!StringUtils.hasText(projectCategory)) {
        projectCategory = PortalUtils.getPageView(request);
      }
      if (!StringUtils.hasText(projectCategory)) {
        projectCategory = ProjectCategory.CATEGORY_NAME_ALL;
      }
      request.setAttribute(CATEGORY_NAME, projectCategory);
      //Get id for selected category
      int projectCategoryId = -1;
      if (StringUtils.hasText(projectCategory) && !"all".equals(projectCategory)) {
        ProjectCategoryList categories = (ProjectCategoryList) request.getAttribute(Constants.REQUEST_TAB_CATEGORY_LIST);
        projectCategoryId = categories.getIdFromValue(projectCategory);
        if (projectCategoryId == -1) {
          return null;
        }
      }

      String sortFilter = PortalUtils.getPageParameter(request);
      Project project = PortalUtils.getProject(request);
      if (project == null) {
        // Get this category's tag cloud
        UniqueTagList tagList = new UniqueTagList();
        tagList.setTableName(Project.TABLE);
        PagedListInfo tagListInfo = new PagedListInfo();
        if ("true".equals(sortByTagCount) || (StringUtils.hasText(sortFilter) && ("popular".equals(sortFilter)))) {
          tagListInfo.setColumnToSortBy("tag_count DESC");
        } else if ((StringUtils.hasText(sortFilter) && "newly-added".equals(sortFilter))) {
          tagListInfo.setColumnToSortBy("tag_date DESC");
        } else {
          tagListInfo.setColumnToSortBy("tag");
        }
        if (StringUtils.hasText(limitString)) {
          tagListInfo.setItemsPerPage(limit);
        } else {
          tagListInfo.setItemsPerPage(-1);
        }
        tagList.setProjectCategoryId(projectCategoryId);
        tagList.setPagedListInfo(tagListInfo);
        tagList.setDetermineTagWeights(true);
        tagList.buildList(db);
        if (tagList.size() == 0) {
          return null;
        }
        request.setAttribute(TAG_LIST, tagList);
      } else {
        // Get this project's tag cloud
        TagList tagList = new TagList();
        tagList.setTableName(Project.TABLE);
        PagedListInfo tagListInfo = new PagedListInfo();
        if ("true".equals(sortByTagCount) || (StringUtils.hasText(sortFilter) && ("popular".equals(sortFilter)))) {
          tagListInfo.setColumnToSortBy("tag_count DESC");
        } else if ((StringUtils.hasText(sortFilter) && "new-added".equals(sortFilter))) {
          tagListInfo.setColumnToSortBy("tag_date DESC");
        } else {
          tagListInfo.setColumnToSortBy("tag");
        }
        if (StringUtils.hasText(limitString)) {
          tagListInfo.setItemsPerPage(limit);
        } else {
          tagListInfo.setItemsPerPage(-1);
        }
        tagList.setLinkItemId(project.getId());
        tagList.setPagedListInfo(tagListInfo);
        tagList.setDetermineTagWeights(true);
        tagList.buildList(db);
        if (tagList.size() == 0) {
          return null;
        }
        request.setAttribute(TAG_LIST, tagList);
      }

      // JSP view
      return defaultView;

    } catch (Exception e) {
      e.printStackTrace();
      throw new PortletException(e);
    }
  }
}
