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

import com.concursive.connect.indexer.IIndexerSearch;
import com.concursive.connect.indexer.IndexerQueryResultList;
import com.concursive.connect.web.modules.profile.dao.ProjectCategoryList;
import com.concursive.connect.web.modules.search.beans.SearchBean;
import com.concursive.connect.web.portal.PortalUtils;
import com.concursive.connect.web.utils.PagedListInfo;

import javax.portlet.*;
import java.io.IOException;

/**
 * Search results portlet
 *
 * @author matt rajkowski
 * @created May 16, 2008
 */
public class SearchResultsByProjectPortlet extends GenericPortlet {

  // Pages
  private static final String VIEW_PAGE = "/portlets/search_results_by_project_portlet/search_results_by_project_portlet-view.jsp";
  // Context Parameters
  private static final String SEARCHER = "projectSearcher";
  private static final String BASE_QUERY_STRING = "baseQueryString";
  private static final String PROJECT_CATEGORY_LIST = "projectCategoryList";
  private static final String SEARCH_BEAN = "searchBean";
  // Preferences
  private static final String PREF_CATEGORY_NAME = "category";
  private static final String PREF_RECORD_LIMIT = "limit";
  private static final String PREF_HAS_MORE_TITLE = "hasMoreTitle";
  private static final String PREF_HAS_MORE_URL = "hasMoreURL";
  private static final String PREF_HAS_PAGING = "hasPaging";

  // Object Results
  private static final String TITLE = "title";
  private static final String HITS = "hits";
  private static final String SEARCH_BEAN_ATTRIBUTE = "searchBean";
  private static final String RECORD_LIMIT = "recordLimit";
  private static final String HAS_MORE_TITLE = "hasMoreTitle";
  private static final String HAS_MORE_URL = "hasMoreURL";
  private static final String SEARCH_INFO = "searchInfo";
  private static final String HAS_PAGING = "hasPaging";
  private static final String HAS_MORE_RECORDS = "hasMoreRecords";

  public void doView(RenderRequest request, RenderResponse response)
      throws PortletException, IOException {
    try {
      String ctx = request.getContextPath();
      String defaultView = VIEW_PAGE;

      // Get portal items
      String queryString = (String) request.getAttribute(BASE_QUERY_STRING);
      IIndexerSearch searcher = (IIndexerSearch) request.getAttribute(SEARCHER);
      ProjectCategoryList categories = (ProjectCategoryList) request.getAttribute(PROJECT_CATEGORY_LIST);
      SearchBean searchBean = (SearchBean) request.getAttribute(SEARCH_BEAN);

      // Get preferences
      String category = request.getPreferences().getValue(PREF_CATEGORY_NAME, null);
      request.setAttribute(TITLE, request.getPreferences().getValue(TITLE, "Projects"));
      int recordLimit = Integer.parseInt(request.getPreferences().getValue(PREF_RECORD_LIMIT, "10"));
      request.setAttribute(RECORD_LIMIT, recordLimit);
      boolean hasPaging = "true".equals(request.getPreferences().getValue(PREF_HAS_PAGING, null));
      if (hasPaging) {
        request.setAttribute(HAS_PAGING, hasPaging);
      }

      if (category != null && categories.getIdFromValue(category) == -1) {
        // don't display anything because the category does not exist in the category list
      } else {
        // If the searchbean category is not this category, then provide a link to that category page
        if (category != null && categories.getIdFromValue(category) != searchBean.getCategoryId()) {
          request.setAttribute(HAS_MORE_URL, request.getPreferences().getValue(PREF_HAS_MORE_URL,
              searchBean.getUrlByCategory(categories.getIdFromValue(category))));
        } else {
          request.setAttribute(HAS_MORE_URL, request.getPreferences().getValue(PREF_HAS_MORE_URL, ""));
        }
        request.setAttribute(HAS_MORE_TITLE, request.getPreferences().getValue(PREF_HAS_MORE_TITLE, "Browse more items"));

        // Request items
        request.setAttribute(SEARCH_BEAN_ATTRIBUTE, searchBean);

        // Customize the string
        queryString +=
            " AND (type:project) " +
                (category != null ?
                    "AND (projectCategoryId:" + categories.getIdFromValue(category) + ")" : "");

        // Efficient unsorted list
        /*
        TopDocCollector collector = new TopDocCollector(50);
        searcher.search(query, collector);
        ScoreDoc[] hits = collector.topDocs().scoreDocs;
        */

        //
        IndexerQueryResultList hits = new IndexerQueryResultList();
        hits.setQueryString(queryString);
        hits.getPagedListInfo().setItemsPerPage(recordLimit);

        // Use paging if requested
        PagedListInfo searchBeanInfo = new PagedListInfo();
        if (hasPaging) {
          searchBeanInfo.setLink("/search");
          String offsetStr = request.getParameter("offset");
          int offset = offsetStr == null ? 0 : Integer.parseInt(offsetStr);
          searchBeanInfo.setCurrentOffset(offset);
          searchBeanInfo.setRenderParameters(searchBean.getParameterMap());
          searchBeanInfo.setNamespace(response.getNamespace());
          searchBeanInfo.setContextPath(ctx);
          searchBeanInfo.setItemsPerPage(recordLimit);
          hits.setPagedListInfo(searchBeanInfo);
        }

        // Configure the parameters for the search results
        if (searchBean.getFilter() == SearchBean.NEWLY_ADDED) {
          // Newly Added (parse in reverse)
          hits.getPagedListInfo().setColumnToSortBy("entered");
          hits.getPagedListInfo().setSortOrder("desc");
          searcher.search(hits);
        } else if (searchBean.getFilter() == SearchBean.HIGHEST_RATED) {
          // Highest Rated (parse in reverse)
          hits.getPagedListInfo().setColumnToSortBy("ratingAverage");
          hits.getPagedListInfo().setSortOrder("desc");
          searcher.search(hits);
        } else if (searchBean.getFilter() == SearchBean.MOST_REVIEWED) {
          // Most Reviewed (parse in reverse)
          hits.getPagedListInfo().setColumnToSortBy("ratingCount");
          hits.getPagedListInfo().setSortOrder("desc");
          searcher.search(hits);
        } else {
          // Best Match
          searcher.search(hits);
        }

        if (hits.getPagedListInfo().moreRecordsExist()) {
          request.setAttribute(HAS_MORE_RECORDS, "true");
        }

        // Set the results for the view
        request.setAttribute(HITS, hits);

        // This portlet can provide data to other portlets
        for (String event : PortalUtils.getDashboardPortlet(request).getGenerateDataEvents()) {
          PortalUtils.setGeneratedData(request, event, hits);
        }

        if (hits.size() > 0) {
          // JSP view
          PortletContext context = getPortletContext();
          PortletRequestDispatcher requestDispatcher =
              context.getRequestDispatcher(defaultView);
          requestDispatcher.include(request, response);
        }
      }
    } catch (Exception e) {
      e.printStackTrace(System.out);
      throw new PortletException(e);
    }
  }
}
