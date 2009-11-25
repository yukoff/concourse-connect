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
package com.concursive.connect.web.modules.promotions.portlets.promotionResultsCategoryCounts;

import com.concursive.commons.text.StringUtils;
import com.concursive.connect.Constants;
import com.concursive.connect.web.modules.profile.dao.ProjectCategory;
import com.concursive.connect.web.modules.profile.dao.ProjectCategoryList;
import com.concursive.connect.web.modules.promotions.dao.AdCategory;
import com.concursive.connect.web.modules.promotions.dao.AdList;
import com.concursive.connect.web.portal.IPortletViewer;
import com.concursive.connect.web.portal.PortalUtils;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import java.io.IOException;
import java.sql.Connection;
import java.util.LinkedHashMap;

/**
 * Promotions counts by project category
 *
 * @author Kailash Bhoopalam
 * @created November 25, 2009
 */
public class PromotionResultsCategoryCountsViewer implements IPortletViewer {

  // Pages
  private static final String VIEW_PAGE1 = "/portlets/promotion_category_counts/promotion_category_counts-view.jsp";

  // Preferences
  private static final String PREF_INCLUDE_CATEGORIES = "category";
  private static final String PREF_PAGE_URL = "pageURL";

  // Attribute names for objects available in the view
  private static final String PROMOTIONS_COUNTS_MAP = "promotionsCountMap";
  private static final String CHOSEN_CATEGORY = "chosenCategory";
  private static final String NORMALIZED_CATEGORYNAME = "normalizedCategoryName";
  private static final String SORT_ORDER = "sortOrder";
  private static final String PAGE_URL = "pageURL";
  private static final String TOTAL = "total";


  public String doView(RenderRequest request, RenderResponse response)
      throws PortletException, IOException {
    try {
      String defaultView = VIEW_PAGE1;

      Connection db = PortalUtils.getConnection(request);

      // Set global preferences
      ProjectCategoryList categoriesToShow = null;
      ProjectCategoryList categories = (ProjectCategoryList) request.getAttribute(Constants.REQUEST_TAB_CATEGORY_LIST);
      String categoriesToInclude = request.getPreferences().getValue(PREF_INCLUDE_CATEGORIES, null);
      if (categoriesToInclude != null) {
        categoriesToShow = new ProjectCategoryList();
        String[] categoryArray = categoriesToInclude.split(",");
        for (String thisCategory : categoryArray) {
          ProjectCategory specifiedCategory = categories.getFromValue(thisCategory.trim());
          if (specifiedCategory != null) {
            categoriesToShow.add(specifiedCategory);
          }
        }
      } else {
        categoriesToShow = (ProjectCategoryList) categories.clone();
      }

      String pageURL = request.getPreferences().getValue(PREF_PAGE_URL, null);
      if (StringUtils.hasText(pageURL)) {
        request.setAttribute(PAGE_URL, pageURL);
      }
      String promotionCategoryName = PortalUtils.getPageParameter(request);
      if (StringUtils.hasText(promotionCategoryName)) {
        request.setAttribute(NORMALIZED_CATEGORYNAME, AdCategory.getNormalizedCategoryName(promotionCategoryName));
      }
      String chosenCategory = PortalUtils.getPageView(request);
      if (StringUtils.hasText(chosenCategory)) {
        request.setAttribute(CHOSEN_CATEGORY, chosenCategory);
      }

      request.setAttribute(SORT_ORDER, PortalUtils.getQueryParameter(request, "sort"));

      int total = 0;
      LinkedHashMap<ProjectCategory, Integer> promotionsCountMap = new LinkedHashMap<ProjectCategory, Integer>();

      // build the tags for the chosen categories
      for (ProjectCategory category : categoriesToShow) {
        // Get this project's tag cloud
        int categoryId = category.getId();

        AdList promotionListByProjectCategory = new AdList();
        promotionListByProjectCategory.setProjectCategoryId(categoryId);
        promotionListByProjectCategory.setCurrentAds(Constants.TRUE);
        promotionListByProjectCategory.setOpenProjectsOnly(true);
        promotionListByProjectCategory.setInstanceId(PortalUtils.getInstance(request).getId());
        if (PortalUtils.canShowSensitiveData(request) && PortalUtils.getUser(request).getId() > 0) {
          promotionListByProjectCategory.setForParticipant(Constants.TRUE);
        } else {
          // Use the most generic settings since this portlet is cached
          promotionListByProjectCategory.setPublicProjects(Constants.TRUE);
        }
        promotionListByProjectCategory.buildList(db);

        promotionsCountMap.put(category, promotionListByProjectCategory.size());
        total = total + promotionListByProjectCategory.size();
      }
      request.setAttribute(PROMOTIONS_COUNTS_MAP, promotionsCountMap);
      request.setAttribute(TOTAL, String.valueOf(total));

      // JSP view
      return defaultView;

    } catch (Exception e) {
      e.printStackTrace();
      throw new PortletException(e);
    }
  }
}
