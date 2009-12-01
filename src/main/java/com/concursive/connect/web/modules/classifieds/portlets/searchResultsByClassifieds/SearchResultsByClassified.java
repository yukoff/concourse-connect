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
package com.concursive.connect.web.modules.classifieds.portlets.searchResultsByClassifieds;


import com.concursive.connect.Constants;
import com.concursive.connect.indexer.IIndexerSearch;
import com.concursive.connect.indexer.IndexerQueryResultList;
import com.concursive.connect.web.modules.profile.dao.ProjectCategoryList;
import com.concursive.connect.web.portal.IPortletViewer;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 * Search results for classifieds
 *
 * @author Kailash Bhoopalam
 * @created November 27, 2009
 */
public class SearchResultsByClassified implements IPortletViewer {

  // Pages
  private static final String VIEW_PAGE = "/portlets/search_results_by_classifieds/search_results_by_classifieds-view.jsp";

  // Context Parameters
  private static final String SEARCHER = "searcher";
  private static final String BASE_QUERY_STRING = "dataQueryString";
  private static final String PROJECT_CATEGORY_LIST = "projectCategoryList";
  // Preferences
  private static final String PREFS_TITLE = "title";
  private static final String PREFS_LIMIT = "limit";
  private static final String PREFS_CATEGORY_NAME = "category";
  // Object Results
  private static final String TITLE = "title";
  private static final String LIMIT = "limit";
  private static final String HITS = "hits";

  public String doView(RenderRequest request, RenderResponse response)
      throws PortletException, IOException {
      String defaultView = VIEW_PAGE;

      // Get portal items
      String queryString = (String) request.getAttribute(BASE_QUERY_STRING);
      IIndexerSearch searcher = (IIndexerSearch) request.getAttribute(SEARCHER);

      // Get preferences
      int limit = Integer.parseInt(request.getPreferences().getValue(PREFS_LIMIT, "10"));
      request.setAttribute(TITLE, request.getPreferences().getValue(PREFS_TITLE, null));
      request.setAttribute(LIMIT, request.getPreferences().getValue(PREFS_LIMIT, "10"));
      String category = request.getPreferences().getValue(PREFS_CATEGORY_NAME, null);
      if (category != null) {
        ProjectCategoryList categories = (ProjectCategoryList) request.getAttribute(PROJECT_CATEGORY_LIST);
        int categoryId = categories.getIdFromValue(category);
        if (categoryId > -1) {
          queryString += " AND (projectCategoryId:" + categoryId + ")";
        }
      }
      Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
      SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
      
      //Fetch only those that are published. TODO: performance testing as this results in a query explosion
      queryString += " AND (published:[20030101 TO " + String.valueOf(formatter.format(currentTimestamp) + "])");
      //Fetch only those that have not expired
      queryString += " NOT (expired:{20030101 TO " + String.valueOf(formatter.format(currentTimestamp) + "})");
      
      
      // Customize the string
      queryString += " AND (type:classifieds) ";
      
      // Search results will be set in query object...
      IndexerQueryResultList query = new IndexerQueryResultList(queryString);
      query.setQueryIndexType(Constants.INDEXER_FULL);
      query.getPagedListInfo().setItemsPerPage(limit);
      searcher.search(query);
      //Sort sort = new Sort("type");
      //Hits hits = searcher.search(query, sort);
      request.setAttribute(HITS, query);

      if (query.size() == 0) {
        // JSP view
      	defaultView = null;
      }
    	return defaultView;
  }
}
