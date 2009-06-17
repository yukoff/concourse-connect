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

import com.concursive.commons.text.StringUtils;
import com.concursive.connect.Constants;
import com.concursive.connect.cms.portal.dao.DashboardPage;
import com.concursive.connect.indexer.IIndexerSearch;
import com.concursive.connect.indexer.IndexerQueryResultList;
import com.concursive.connect.web.modules.profile.dao.ProjectCategory;
import com.concursive.connect.web.modules.profile.dao.ProjectCategoryList;
import com.concursive.connect.web.portal.PortalUtils;
import com.concursive.connect.web.utils.PagedListInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.portlet.*;
import java.io.IOException;
import java.sql.Connection;

/**
 * Project list portlet
 *
 * @author matt rajkowski
 * @created May 14, 2008
 */
public class ProjectSubCategoryListByProjectCategoryPortlet extends GenericPortlet {

  private static Log LOG = LogFactory.getLog(ProjectSubCategoryListByProjectCategoryPortlet.class);

  // Pages
  private static final String PROJECT_CATEGORY_VIEW_PAGE = "/portlets/project_subcategory_list_by_project_category/project_subcategory_list_by_project_category-view.jsp";
  private static final String PROJECT_VIEW_PAGE = "/portlets/project_subcategory_list_by_project_category/project_list_by_category-view.jsp";

  // Context Parameters
  private static final String SEARCHER = "projectSearcher";
  private static final String BASE_QUERY_STRING = "baseQueryString";

  // Preferences
  private static final String PREF_AUTO_SET_PAGE_TITLE = "autoSetPageTitle";
  private static final String PREF_CATEGORY_NAME = "category";
  private static final String PREF_TITLE = "title";
  private static final String PREF_LIMIT = "limit";
  private static final String PREF_PROJECT_LIMIT = "projectLimit";
  private static final String PREF_HAS_MORE_TITLE = "hasMoreTitle";
  private static final String PREF_HAS_MORE_URL = "hasMoreURL";
  private static final String PREF_HAS_PAGING = "hasPaging";
  private static final String PREF_SHOW_SUBCATEGORIES = "showSubcategories";
  private static final String PREF_SHOW_PROJECTS = "showProjects";
  private static final String PREF_COLUMNS = "columns";
  private static final String PREF_SHOW_CATEGORY_LANDING_PAGE_LINK = "showCategoryLandingPageLink";

  // Attribute names for objects available in the view
  private static final String PROJECT_SUBCATEGORY_LIST = "projectSubCategoryList";
  private static final String PROJECT_CATEGORY = "projectCategory";
  private static final String PROJECT_SUB_CATEGORY = "projectSubCategory";
  private static final String PROJECT_LIST = "projectList";
  private static final String TITLE = "title";
  private static final String HAS_MORE = "hasMore";
  private static final String HAS_MORE_TITLE = "hasMoreTitle";
  private static final String HAS_MORE_URL = "hasMoreURL";
  private static final String HAS_PAGING = "hasPaging";
  private static final String RECORD_LIMIT = "recordLimit";
  private static final String COLUMN_LENGTH = "columnLength";
  private static final String SHOW_CATEGORY_LANDING_PAGE_LINK = "showCategoryLandingPageLink";

  public void doView(RenderRequest request, RenderResponse response)
      throws PortletException, IOException {
    try {
      String defaultView = PROJECT_CATEGORY_VIEW_PAGE;

      // Get preferences
      boolean autoSetPageTitle = Boolean.parseBoolean(request.getPreferences().getValue(PREF_AUTO_SET_PAGE_TITLE, "false"));
      String categoryValue = request.getPreferences().getValue(PREF_CATEGORY_NAME, PortalUtils.getPageView(request));
      LOG.debug("Category: " + categoryValue);

      if (categoryValue == null) {
        // @todo return a list of top-level categories to choose from
      }

      String title = request.getPreferences().getValue(PREF_TITLE, null);
      title = StringUtils.replace(title, "${category}", categoryValue);
      request.setAttribute(TITLE, title);
      LOG.debug("Title: " + title);

      String hasMoreUrl = request.getPreferences().getValue(PREF_HAS_MORE_URL, null);
      hasMoreUrl = StringUtils.replace(hasMoreUrl, "${category}", categoryValue);
      request.setAttribute(HAS_MORE_URL, hasMoreUrl);
      LOG.debug("HasMoreURL: " + hasMoreUrl);

      request.setAttribute(HAS_MORE_TITLE, request.getPreferences().getValue(PREF_HAS_MORE_TITLE, "Browse more categories"));
      boolean showSubcategories = Boolean.parseBoolean(request.getPreferences().getValue(PREF_SHOW_SUBCATEGORIES, "false"));
      boolean showProjects = Boolean.parseBoolean(request.getPreferences().getValue(PREF_SHOW_PROJECTS, "false"));

      // Base the selected subcategory off of the URL
      String subCategoryString = PortalUtils.getPageParameter(request);
      // convert the space back to a space
      subCategoryString = StringUtils.replace(subCategoryString, "_", " ");
      LOG.debug("Subcategory: " + subCategoryString);

      // Get the top-level category
      ProjectCategoryList categories = (ProjectCategoryList) request.getAttribute(Constants.REQUEST_TAB_CATEGORY_LIST);
      ProjectCategory category = categories.getFromValue(categoryValue);
      request.setAttribute(PROJECT_CATEGORY, category);

      // Determine the database connection to use
      Connection db = PortalUtils.getConnection(request);

      // Determine the subcategory to use for showing projects
      ProjectCategory subCategory = null;
      if (showSubcategories && subCategoryString != null) {
        ProjectCategoryList subCategories = new ProjectCategoryList();
        subCategories.setParentCategoryId(category.getId());
        subCategories.setCategoryNameLowerCase(subCategoryString);
        subCategories.setEnabled(true);
        subCategories.buildList(db);
        if (subCategories.size() > 0) {
          subCategory = subCategories.get(0);
          request.setAttribute(PROJECT_SUB_CATEGORY, subCategory);
        }
      }

      // This portlet can retrieve state data from other portlets
      boolean showThisPortlet = true;
      for (String event : PortalUtils.getDashboardPortlet(request).getConsumeDataEvents()) {
        // Detects if another instance of this portlet is showing categories
        if (event.equals("subcategory")) {
          String subcategoryBeingShownByAnotherPortlet = (String) PortalUtils.getGeneratedData(request, event);
          LOG.debug("Consumed a subcategory: " + subcategoryBeingShownByAnotherPortlet);
          if (!StringUtils.hasText(subcategoryBeingShownByAnotherPortlet)) {
            showThisPortlet = false;
            defaultView = null;
          }
        }
      }

      // This portlet can provide state data to other portlets
      for (String event : PortalUtils.getDashboardPortlet(request).getGenerateDataEvents()) {
        if (event.toLowerCase().equals("subcategory") && subCategory != null) {
          PortalUtils.setGeneratedData(request, event, subCategory.getDescription());
          LOG.debug("SetData: " + event + " - " + PortalUtils.getGeneratedData(request, event));
        } else if (event.toLowerCase().equals("category") && category != null) {
          PortalUtils.setGeneratedData(request, event, category.getDescription());
          LOG.debug("SetData: " + event + " - " + PortalUtils.getGeneratedData(request, event));
        }
      }

      // Generate a page title
      if (autoSetPageTitle) {
        DashboardPage dashboardPage = PortalUtils.getDashboardPage(request);
        String pageTitle = null;
        if (dashboardPage != null) {
          // Set the title
          if (subCategory != null) {
            pageTitle = subCategory.getDescription() + " - " + category.getDescription();
          } else if (category != null) {
            pageTitle = category.getDescription();
          }
          if (pageTitle != null) {
            request.setAttribute(Constants.REQUEST_GENERATED_TITLE, pageTitle);
          }
          // Set the category
          if (category != null) {
            request.setAttribute(Constants.REQUEST_GENERATED_CATEGORY, pageTitle);
          }
        }
      }

      if (showThisPortlet) {
        if (showProjects && (subCategoryString != null || !showSubcategories)) {
          request.setAttribute(PREF_SHOW_CATEGORY_LANDING_PAGE_LINK, request.getPreferences().getValue(PREF_SHOW_CATEGORY_LANDING_PAGE_LINK, "false"));

          // Show the projects
          boolean hasPaging = "true".equals(request.getPreferences().getValue(PREF_HAS_PAGING, "false"));
          PagedListInfo projectListInfo = new PagedListInfo();
          if (hasPaging) {
            // Use paged data for sorting
            String limit = request.getPreferences().getValue(PREF_PROJECT_LIMIT, "-1");
            request.setAttribute(RECORD_LIMIT, limit);
            projectListInfo.setItemsPerPage(limit);

            if (showSubcategories) {
              String pageNumber = null;
              String[] params = PortalUtils.getPageParameters(request);
              if (params != null) {
                //E.g., {url}/page/${page name}/${project category}/${project sub category}/${page number}
                if (params.length == 2) {
                  pageNumber = params[1];
                }
              }
              if (StringUtils.hasText(pageNumber) && StringUtils.isNumber(pageNumber)) {
                projectListInfo.setCurrentPage(Integer.parseInt(pageNumber));
              }
            } else {
              String pageNumber = null;
              String[] params = PortalUtils.getPageParameters(request);
              if (params != null) {
                //E.g., {url}/page/${page name}/${project category}/${page number}
                if (params.length == 1) {
                  pageNumber = params[0];
                }
              }
              if (StringUtils.hasText(pageNumber) && StringUtils.isNumber(pageNumber)) {
                projectListInfo.setCurrentPage(Integer.parseInt(pageNumber));
              }
            }
          } else {
            projectListInfo.setItemsPerPage(-1);
          }
          request.setAttribute(HAS_PAGING, request.getPreferences().getValue(PREF_HAS_PAGING, "false"));
          projectListInfo.setColumnToSortBy("titleLower");
          projectListInfo.setContextPath(request.getContextPath());

          // Get portal items
          IIndexerSearch searcher = (IIndexerSearch) request.getAttribute(SEARCHER);
          if (searcher == null) {
            LOG.error("SEARCHER IS NULL!");
          }
          String queryString = (String) request.getAttribute(BASE_QUERY_STRING);
          queryString +=
              "AND (type:project) " +
                  "AND (projectCategoryId:" + category.getId() + ") ";
          if (subCategory != null) {
            queryString += "AND (projectCategoryId1:" + subCategory.getId() + ") ";
          }
          if (PortalUtils.canShowSensitiveData(request)) {
            // Use the most generic settings since this portlet is cached
            if (PortalUtils.getUser(request).getId() > 0) {
              queryString += "AND ((guests:1) OR (participants:1))";
            } else {
              queryString += "AND (guests:1)";
            }
          } else {
            // Use the most generic settings
            queryString += "AND (guests:1)";
          }
          IndexerQueryResultList hits = new IndexerQueryResultList(queryString);
          hits.setPagedListInfo(projectListInfo);
          searcher.search(hits);
          request.setAttribute("hits", hits);

          defaultView = PROJECT_VIEW_PAGE;
        } else {
          int columns = Integer.parseInt(request.getPreferences().getValue(PREF_COLUMNS, "1"));

          // Use paged list for limiting number of records, and for counting all
          String limit = request.getPreferences().getValue(PREF_LIMIT, "-1");
          PagedListInfo pagedListInfo = new PagedListInfo();
          pagedListInfo.setItemsPerPage(Integer.parseInt(limit));

          ProjectCategoryList subCategories = new ProjectCategoryList();
          subCategories.setPagedListInfo(pagedListInfo);
          if (category != null) {
            subCategories.setParentCategoryId(category.getId());
            subCategories.setEnabled(true);
            subCategories.buildList(db);
          }
          request.setAttribute(PREF_COLUMNS, columns);
          request.setAttribute(PROJECT_SUBCATEGORY_LIST, subCategories);
          request.setAttribute(HAS_MORE, String.valueOf(pagedListInfo.getNumberOfPages() > 1));

          if (subCategories.size() == 0) {
            defaultView = null;
          }
        }
      }
      if (defaultView != null) {
        PortletContext context = getPortletContext();
        PortletRequestDispatcher requestDispatcher =
            context.getRequestDispatcher(defaultView);
        requestDispatcher.include(request, response);
      }
    } catch (Exception e) {
      LOG.error("Exception", e);
      throw new PortletException(e.getMessage());
    }
  }
}
