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
package com.concursive.connect.web.modules.promotions.portlets.promotionListByPromotionCategory;

import com.concursive.commons.text.StringUtils;
import com.concursive.connect.Constants;
import com.concursive.connect.cms.portal.dao.DashboardPage;
import com.concursive.connect.web.modules.profile.dao.ProjectCategory;
import com.concursive.connect.web.modules.profile.dao.ProjectCategoryList;
import com.concursive.connect.web.modules.promotions.dao.AdCategory;
import com.concursive.connect.web.modules.promotions.dao.AdCategoryList;
import com.concursive.connect.web.modules.promotions.dao.AdList;
import com.concursive.connect.web.portal.IPortletViewer;
import com.concursive.connect.web.portal.PortalUtils;
import com.concursive.connect.web.utils.PagedListInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import java.sql.Connection;

/**
 * Promotions list by promotions category portlet
 *
 * @author Kailash Bhoopalam
 * @created November 25, 2009
 */
public class PromotionListByPromotionCategoryViewer implements IPortletViewer {

  private static Log LOG = LogFactory.getLog(PromotionListByPromotionCategoryViewer.class);

  // Pages
  private static final String PROMOTION_CATEGORY_VIEW_PAGE = "/portlets/promotion_list_by_promotion_category/promotion_category_list_by_project_category-view.jsp";
  private static final String PROMOTION_VIEW_PAGE = "/portlets/promotion_list_by_promotion_category/promotion_list_by_promotion_category-view.jsp";
  // Preferences
  private static final String PREF_AUTO_SET_PAGE_TITLE = "autoSetPageTitle";
  private static final String PREF_CATEGORY_NAME = "category";
  private static final String PREF_TITLE = "title";
  private static final String PREF_LIMIT = "limit";
  private static final String PREF_HAS_MORE_TITLE = "hasMoreTitle";
  private static final String PREF_HAS_MORE_URL = "hasMoreURL";
  private static final String PREF_HAS_PAGING = "hasPaging";
  private static final String PREF_SHOW_PROMOTIONCATEGORIES = "showPromotionCategories";
  private static final String PREF_SHOW_PROMOTIONS = "showPromotions";
  private static final String PREF_COLUMNS = "columns";
  private static final String PREF_SHOW_CATEGORY_LANDING_PAGE_LINK = "showCategoryLandingPageLink";

  // Attribute names for objects available in the view
  private static final String PROMOTION_CATEGORY_LIST = "promotionCategoryList";
  private static final String PROJECT_CATEGORY = "projectCategory";
  private static final String PROMOTION_CATEGORY = "promotionCategory";
  private static final String PROMOTION_LIST = "promotionList";
  private static final String TITLE = "title";
  private static final String HAS_MORE = "hasMore";
  private static final String HAS_MORE_TITLE = "hasMoreTitle";
  private static final String HAS_MORE_URL = "hasMoreURL";
  private static final String HAS_PAGING = "hasPaging";
  private static final String RECORD_LIMIT = "recordLimit";
  private static final String COLUMN_LENGTH = "columnLength";
  private static final String SHOW_CATEGORY_LANDING_PAGE_LINK = "showCategoryLandingPageLink";
  private static final String SORT_ORDER = "sortOrder";
  private static final String SHOW_PROJECT_CATEGORY_NAME_IN_CATEGORY_LIST = "showProjectCategoryNameInCategoryList";

  public String doView(RenderRequest request, RenderResponse response)
      throws Exception {
    String defaultView = PROMOTION_CATEGORY_VIEW_PAGE;

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
    boolean showPromotionCategories = Boolean.parseBoolean(request.getPreferences().getValue(PREF_SHOW_PROMOTIONCATEGORIES, "false"));
    boolean showPromotions = Boolean.parseBoolean(request.getPreferences().getValue(PREF_SHOW_PROMOTIONS, "false"));

    // Base the selected subcategory off of the URL
    String promotionCategoryString = PortalUtils.getPageParameter(request);
    // convert the space back to a space
    promotionCategoryString = StringUtils.replace(promotionCategoryString, "_", " ");
    LOG.debug("promotionCategory: " + promotionCategoryString);

    // Get the top-level category
    ProjectCategoryList categories = (ProjectCategoryList) request.getAttribute(Constants.REQUEST_TAB_CATEGORY_LIST);
    ProjectCategory category = categories.getFromValue(categoryValue);
    if (category == null) {
      category = new ProjectCategory();
      category.setDescription("all");
    }
    request.setAttribute(PROJECT_CATEGORY, category);

    // Determine the database connection to use
    Connection db = PortalUtils.getConnection(request);

    // Determine the promotions to use for showing projects
    AdCategory promotionCategory = null;
    if (showPromotionCategories && StringUtils.hasText(categoryValue)) {
      AdCategoryList promotionCategories = new AdCategoryList();
      promotionCategories.setProjectCategoryId(category.getId());
      promotionCategories.setEnabled(Constants.TRUE);
      promotionCategories.buildList(db);
      if (promotionCategories.size() > 0) {
        promotionCategory = promotionCategories.get(0);
        request.setAttribute(PROMOTION_CATEGORY, promotionCategory);
      }
    }

    // This portlet can retrieve state data from other portlets
    boolean showThisPortlet = true;
    for (String event : PortalUtils.getDashboardPortlet(request).getConsumeDataEvents()) {
      // Detects if another instance of this portlet is showing categories
      if (event.equals("promotioncategory")) {
        String promotioncategoryBeingShownByAnotherPortlet = (String) PortalUtils.getGeneratedData(request, event);
        LOG.debug("Consumed a promotion category: " + promotioncategoryBeingShownByAnotherPortlet);
        if (!StringUtils.hasText(promotioncategoryBeingShownByAnotherPortlet)) {
          showThisPortlet = false;
          defaultView = null;
        }
      }
    }

    // This portlet can provide state data to other portlets
    for (String event : PortalUtils.getDashboardPortlet(request).getGenerateDataEvents()) {
      if (event.toLowerCase().equals("promotioncategory") && promotionCategory != null) {
        PortalUtils.setGeneratedData(request, event, promotionCategory.getItemName());
        LOG.debug("SetData: " + event + " - " + PortalUtils.getGeneratedData(request, event));
      } else if (event.toLowerCase().equals("category")) {
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
        if (promotionCategory != null) {
          pageTitle = promotionCategory.getItemName() + " - " + category.getDescription();
        } else {
          pageTitle = category.getDescription();
        }
        if (pageTitle != null) {
          request.setAttribute(Constants.REQUEST_GENERATED_TITLE, pageTitle);
        }
        // Set the category
        request.setAttribute(Constants.REQUEST_GENERATED_CATEGORY, pageTitle);
      }
    }

    if (showThisPortlet) {
      AdList promotions = new AdList();
      if (showPromotions) {
        request.setAttribute(PREF_SHOW_CATEGORY_LANDING_PAGE_LINK, request.getPreferences().getValue(PREF_SHOW_CATEGORY_LANDING_PAGE_LINK, "false"));

        AdCategoryList promotionCategories = new AdCategoryList();
        promotionCategory = new AdCategory();
        if (StringUtils.hasText(promotionCategoryString)) {
          promotionCategories.setProjectCategoryId(category.getId());
          promotionCategories.setEnabled(Constants.TRUE);
          promotionCategories.setCategoryLowercaseName(promotionCategoryString);
          promotionCategories.buildList(db);
          if (promotionCategories.size() > 0) {
            promotionCategory = promotionCategories.get(0);
          }
        }

        // Show the projects
        boolean hasPaging = "true".equals(request.getPreferences().getValue(PREF_HAS_PAGING, "false"));
        PagedListInfo promotionListInfo = new PagedListInfo();
        if (hasPaging) {
          // Use paged data for sorting
          String limit = request.getPreferences().getValue(PREF_LIMIT, "10");
          request.setAttribute(RECORD_LIMIT, limit);
          promotionListInfo.setItemsPerPage(limit);

          String pageNumber = null;
          String[] params = PortalUtils.getPageParameters(request);
          if (promotionCategory.getId() != -1) {
            if (params != null) {
              //E.g., {url}/page/${page name}/${project category}/${promotion category}/${page number}
              if (params.length == 2) {
                pageNumber = params[1];
              }
            }
          } else {
            if (params != null) {
              //E.g., {url}/page/${page name}/${project category}/${page number}
              if (params.length == 1) {
                pageNumber = params[0];
              }
            }
          }
          if (StringUtils.hasText(pageNumber) && StringUtils.isNumber(pageNumber)) {
            promotionListInfo.setCurrentPage(Integer.parseInt(pageNumber));
          }
        } else {
          promotionListInfo.setItemsPerPage(-1);
        }
        request.setAttribute(HAS_PAGING, request.getPreferences().getValue(PREF_HAS_PAGING, "false"));
        String sortOrder = PortalUtils.getQueryParameter(request, "sort");
        if ("new".equals(sortOrder)) {
          promotionListInfo.setColumnToSortBy("entered DESC");
        } else if ("expire".equals(sortOrder)) {
          promotionListInfo.setColumnToSortBy("expiration_date ASC");
        } else {
          promotionListInfo.setColumnToSortBy("heading ASC");
        }
        promotionListInfo.setContextPath(request.getContextPath());
        // Projects to show
        promotions.setPagedListInfo(promotionListInfo);
        promotions.setInstanceId(PortalUtils.getInstance(request).getId());
        if (PortalUtils.canShowSensitiveData(request) && PortalUtils.getUser(request).getId() > 0) {
        	promotions.setForParticipant(Constants.TRUE);
        } else {
          // Use the most generic settings since this portlet is cached
        	promotions.setPublicProjects(Constants.TRUE);
        }
        promotions.setCategoryId(promotionCategory.getId());
        promotions.setProjectCategoryId(category.getId());
        promotions.setOpenProjectsOnly(true);
        promotions.setPublicProjects(Constants.TRUE);
        promotions.setCurrentAds(Constants.TRUE);
        promotions.buildList(db);

        request.setAttribute(SORT_ORDER, sortOrder);
        request.setAttribute(PROMOTION_LIST, promotions);
        request.setAttribute(PROMOTION_CATEGORY, promotionCategory);
        defaultView = PROMOTION_VIEW_PAGE;
      }

      if (showPromotionCategories && promotions.size() == 0) {
        int columns = Integer.parseInt(request.getPreferences().getValue(PREF_COLUMNS, "1"));

        // Use paged list for limiting number of records, and for counting all
        String limit = request.getPreferences().getValue(PREF_LIMIT, "-1");
        PagedListInfo pagedListInfo = new PagedListInfo();
        pagedListInfo.setItemsPerPage(Integer.parseInt(limit));
        pagedListInfo.setColumnToSortBy("lpc.level ASC, ac.level ASC, ac.item_name ASC");
        AdCategoryList promotionCategories = new AdCategoryList();
        promotionCategories.setPagedListInfo(pagedListInfo);
        if (category.getId() != -1) {
          promotionCategories.setProjectCategoryId(category.getId());
          request.setAttribute(SHOW_PROJECT_CATEGORY_NAME_IN_CATEGORY_LIST, "false");
        } else {
          request.setAttribute(SHOW_PROJECT_CATEGORY_NAME_IN_CATEGORY_LIST, "true");
        }
        promotionCategories.setProjectCategoryEnabled(Constants.TRUE);
        promotionCategories.setEnabled(Constants.TRUE);
        promotionCategories.buildList(db);
        request.setAttribute(PREF_COLUMNS, columns);
        request.setAttribute(PROMOTION_CATEGORY_LIST, promotionCategories);
        request.setAttribute(HAS_MORE, String.valueOf(pagedListInfo.getNumberOfPages() > 1));

        if (promotionCategories.size() == 0) {
          defaultView = null;
        }
      }
    }
    return defaultView;
  }
}