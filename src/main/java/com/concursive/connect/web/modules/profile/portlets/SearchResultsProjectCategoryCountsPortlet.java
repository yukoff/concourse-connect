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

import com.concursive.connect.Constants;
import com.concursive.connect.indexer.IIndexerSearch;
import com.concursive.connect.indexer.IndexerQueryResultList;
import com.concursive.connect.web.modules.profile.dao.ProjectCategory;
import com.concursive.connect.web.modules.profile.dao.ProjectCategoryList;
import com.concursive.connect.web.portal.PortalUtils;

import javax.portlet.*;
import java.io.IOException;
import java.util.LinkedHashMap;

/**
 * Search results category counts portlet
 *
 * @author matt rajkowski
 * @created May 18, 2008
 */
public class SearchResultsProjectCategoryCountsPortlet extends GenericPortlet {

  // Pages
  private static final String VIEW_PAGE = "/portlets/search_results_project_category_counts_portlet/search_results_project_category_counts_portlet-view.jsp";
  // Parameters
  private static final String SEARCHER = "projectSearcher";
  private static final String BASE_QUERY_STRING = "baseQueryString";
  private static final String PROJECT_CATEGORY_LIST = "projectCategoryList";
  // Prefs
  private static final String PROJECT_CATEGORIES_TO_INCLUDE = "category";
  // Object Results
  private static final String TOTAL = "total";
  private static final String COUNTS = "counts";

  public void doView(RenderRequest request, RenderResponse response)
      throws PortletException, IOException {
    try {
      String defaultView = VIEW_PAGE;

      // Get portal items
      String queryString = (String) request.getAttribute(BASE_QUERY_STRING);
      IIndexerSearch searcher = (IIndexerSearch) request.getAttribute(SEARCHER);
      ProjectCategoryList categories = (ProjectCategoryList) request.getAttribute(PROJECT_CATEGORY_LIST);

      // Get preferences
      String categoriesToInclude = request.getPreferences().getValue(PROJECT_CATEGORIES_TO_INCLUDE, null);

      // If no categories specified, then use them all
      if (categoriesToInclude == null) {
        StringBuffer sb = new StringBuffer();
        int count = 0;
        for (ProjectCategory thisCategory : categories) {
          ++count;
          sb.append(thisCategory.getDescription());
          if (count < categories.size()) {
            sb.append(",");
          }
        }
        categoriesToInclude = sb.toString();
      }

      // Perform the search counts
      Integer total = 0;
      LinkedHashMap<ProjectCategory, Integer> counts = new LinkedHashMap<ProjectCategory, Integer>();
      String[] categoryArray = categoriesToInclude.split(",");
      for (String thisCategory : categoryArray) {
        ProjectCategory category = categories.getFromValue(thisCategory.trim());
        if (category != null && category.getEnabled()) {
          // Customize the string
          String thisQuery = queryString + " AND (type:project) AND (projectCategoryId:" + category.getId() + ")";
          IndexerQueryResultList query = new IndexerQueryResultList(thisQuery);
          query.setQueryIndexType(Constants.INDEXER_FULL);
          query.getPagedListInfo().setItemsPerPage(1);
          searcher.search(query);
          counts.put(category, query.getPagedListInfo().getMaxRecords());
          total += query.getPagedListInfo().getMaxRecords();
        }
      }
      request.setAttribute(COUNTS, counts);
      request.setAttribute(TOTAL, total);

      // This portlet can provide state data to other portlets
      for (String event : PortalUtils.getDashboardPortlet(request).getGenerateDataEvents()) {
        if (event.equals("hideIfEmpty")) {
          PortalUtils.setGeneratedData(request, event, String.valueOf(total == 0));
        }
      }

      // JSP view
      PortletContext context = getPortletContext();
      PortletRequestDispatcher requestDispatcher =
          context.getRequestDispatcher(defaultView);
      requestDispatcher.include(request, response);
    } catch (Exception e) {
      e.printStackTrace(System.out);
      throw new PortletException(e.getMessage());
    }
  }
}