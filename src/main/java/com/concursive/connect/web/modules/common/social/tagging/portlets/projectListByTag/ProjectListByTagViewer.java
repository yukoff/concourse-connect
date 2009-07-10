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

/**
 *
 */
package com.concursive.connect.web.modules.common.social.tagging.portlets.projectListByTag;

import com.concursive.commons.text.StringUtils;
import com.concursive.connect.Constants;
import com.concursive.connect.web.modules.common.social.tagging.dao.Tag;
import com.concursive.connect.web.modules.common.social.tagging.dao.TagList;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.dao.ProjectCategoryList;
import com.concursive.connect.web.modules.profile.dao.ProjectList;
import com.concursive.connect.web.portal.IPortletViewer;
import com.concursive.connect.web.portal.PortalUtils;
import com.concursive.connect.web.utils.PagedListInfo;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import java.io.IOException;
import java.sql.Connection;
import java.util.Iterator;

/**
 * Project List By Tag
 *
 * @author Kailash Bhoopalam
 * @created November 10, 2008
 */
public class ProjectListByTagViewer implements IPortletViewer {

  // Pages
  private static final String VIEW_PAGE1 = "/portlets/project_list_by_tag/project_list_by_tag-view.jsp";

  // Preferences
  private static final String PREF_TITLE = "title";
  private static final String PREF_LIMIT = "limit";
  private static final String PREF_HAS_MORE_URL = "hasMoreURL";

  // Attribute names for objects available in the view
  private static final String TITLE = "title";
  private static final String HAS_MORE_URL = "hasMoreURL";
  private static final String PROJECT_LIST_BY_TAG = "projectListByTag";
  private static final String CATEGORY_NAME = "category";
  private static final String TAG_NAME = "tagName";
  private static final String NORMALIZED_TAG = "normalizedTag";

  public String doView(RenderRequest request, RenderResponse response)
      throws PortletException, IOException {
    try {
      String defaultView = VIEW_PAGE1;

      // Set global preferences
      request.setAttribute(TITLE, request.getPreferences().getValue(PREF_TITLE, null));
      String limitString = request.getPreferences().getValue(PREF_LIMIT, "-1");
      String hasMoreURL = request.getPreferences().getValue(PREF_HAS_MORE_URL, null);
      request.setAttribute(HAS_MORE_URL, hasMoreURL);

      int limit = -1;
      if (StringUtils.hasText(limitString)) {
        limit = Integer.parseInt(limitString);
      }

      String categoryName = PortalUtils.getPageView(request);
      Connection db = PortalUtils.getConnection(request);
      int categoryId = -1;
      if (StringUtils.hasText(categoryName) && !"all".equals(categoryName)) {
        ProjectCategoryList categories = (ProjectCategoryList) request.getAttribute(Constants.REQUEST_TAB_CATEGORY_LIST);
        categoryId = categories.getIdFromValue(categoryName);
        if (categoryId == -1) {
          return null;
        }
      }
      request.setAttribute(CATEGORY_NAME, categoryName);
      //Get the tag name from the url
      String tagName = PortalUtils.getPageParameter(request);
      request.setAttribute(TAG_NAME, tagName);
      request.setAttribute(NORMALIZED_TAG, StringUtils.replace(tagName, " ", "-"));

      // Get the projects that match a tag
      TagList projectListByTag = new TagList();
      projectListByTag.setInstanceId(PortalUtils.getInstance(request).getId());
      if (PortalUtils.canShowSensitiveData(request) && PortalUtils.getUser(request).getId() > 0) {
        projectListByTag.setForParticipant(Constants.TRUE);
      } else {
        // Use the most generic settings since this portlet is cached
        projectListByTag.setForGuest(Constants.TRUE);
      }
      projectListByTag.setTableName(Project.TABLE);
      projectListByTag.setTag(StringUtils.replace(tagName, "-", " "));
      // @todo Fix showing disabled category projects
      projectListByTag.setCategoryId(categoryId);
      projectListByTag.setUniqueField(Project.PRIMARY_KEY);
      projectListByTag.buildList(db);

      //Building the project list
      ProjectList projectList = new ProjectList();
      if (projectListByTag.size() > 0) {
        StringBuffer projectIdStringBuffer = new StringBuffer();
        Iterator<Tag> projectTagItr = projectListByTag.iterator();
        while (projectTagItr.hasNext()) {
          Tag tag = projectTagItr.next();
          projectIdStringBuffer.append(tag.getLinkItemId());
          if (projectTagItr.hasNext()) {
            projectIdStringBuffer.append(",");
          }
        }

        projectList.setProjectIdsString(projectIdStringBuffer.toString());
        PagedListInfo projectListInfo = new PagedListInfo();
        if (StringUtils.hasText(limitString)) {
          projectListInfo.setItemsPerPage(limit);
        } else {
          projectListInfo.setItemsPerPage(-1);
        }
        String[] params = PortalUtils.getPageParameters(request);
        // page/tag/{categoryName}/{tagName}/{pageOffset} OR page/tag/{categoryName}/{tagName}/{sortCriteria}
        if (params != null && params.length == 2) {
          String paramString = params[1];
          if (StringUtils.isNumber(paramString)) {
            projectListInfo.setCurrentPage(Integer.parseInt(paramString));
          } else {
            setSortCriteria(projectListInfo, paramString);
          }
        }
        // page/tag/{categoryName}/{tagName}/{sortCriteria}/{pageOffset}
        if (params != null && params.length == 3) {
          String sortCriteria = params[1];
          setSortCriteria(projectListInfo, sortCriteria);

          String paramString = params[2];
          if (StringUtils.isNumber(paramString)) {
            projectListInfo.setCurrentPage(Integer.parseInt(paramString));
          }
        }
        projectList.setPagedListInfo(projectListInfo);
        projectList.setPublicOnly(true);
        projectList.setProfileEnabled(Constants.TRUE);
        projectList.setBuildImages(true);
        projectList.buildList(db);
      }
      request.setAttribute(PROJECT_LIST_BY_TAG, projectList);

      // JSP view
      return defaultView;

    } catch (Exception e) {
      e.printStackTrace();
      throw new PortletException(e);
    }
  }

  /**
   * @param projectListInfo
   * @param sortCriteria
   */
  private void setSortCriteria(PagedListInfo projectListInfo, String sortCriteria) {
    if ("popular".equals(sortCriteria)) {
      //TODO: What is popular
    } else if ("newly-added".equals(sortCriteria)) {
      projectListInfo.setColumnToSortBy("entered DESC");
    } else if ("most-reviewed".equals(sortCriteria)) {
      projectListInfo.setColumnToSortBy("rating_count DESC");
    } else if ("highest-rated".equals(sortCriteria)) {
      projectListInfo.setColumnToSortBy("rating_avg DESC");
    }
  }
}
