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

import com.concursive.connect.indexer.IndexerQueryResult;
import com.concursive.connect.indexer.IndexerQueryResultList;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.dao.ProjectList;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import com.concursive.connect.web.portal.PortalUtils;

import javax.portlet.*;
import java.io.IOException;

/**
 * A google maps portlet
 *
 * @author matt rajkowski
 * @created May 19, 2008
 */
public class GoogleMapsFromSearchHitsPortlet extends GenericPortlet {
  // pages
  private static final String MAP_PAGE = "/portlets/google_maps_from_project_list/google_maps_from_project_list-view.jsp";
  private static final String MAP_EMPTY_PAGE = "/portlets/google_maps_from_project_list/google_maps_from_project_list-empty.jsp";
  // application preferences
  private static final String PREF_DOMAIN = "PREF_DOMAIN";
  private static final String PREF_KEY = "PREF_KEY";
  // portlet prefs
  private static final String PREF_MAX_RESULTS = "maxResults";
  // beans
  public static final String PROJECT_LIST = "projectList";
  public static final String HIT_LIMIT_REACHED = "hitLimitReached";

  public void doView(RenderRequest request, RenderResponse response)
      throws PortletException, IOException {
    try {
      request.setAttribute(PREF_DOMAIN, PortalUtils.getApplicationPrefs(request).get("GOOGLE_MAPS.DOMAIN"));
      request.setAttribute(PREF_KEY, PortalUtils.getApplicationPrefs(request).get("GOOGLE_MAPS.KEY"));

      int maxResultsPerCategory = Integer.parseInt(request.getPreferences().getValue(PREF_MAX_RESULTS, "20"));
      request.setAttribute(PREF_MAX_RESULTS, String.valueOf(maxResultsPerCategory));

      boolean hasMore = false;

      // This portlet can consume data from other portlets
      ProjectList projectList = new ProjectList();
      for (String event : PortalUtils.getDashboardPortlet(request).getConsumeDataEvents()) {
        int mappedCount = 0;
        IndexerQueryResultList hits = (IndexerQueryResultList) PortalUtils.getGeneratedData(request, event);
        if (hits != null) {
          for (int i = 0; i < hits.size() && mappedCount < maxResultsPerCategory; i++) {
            IndexerQueryResult document = hits.get(i);
            Project project = ProjectUtils.loadProject(Integer.parseInt(document.getProjectId()));
            if (project.isGeoCoded()) {
              projectList.add(project);
              ++mappedCount;
            }
          }
          if (hits.size() > maxResultsPerCategory) {
            hasMore = true;
          }
        }
      }
      request.setAttribute(PROJECT_LIST, projectList);
      request.setAttribute(HIT_LIMIT_REACHED, String.valueOf(hasMore));

      // JSP view
      String defaultView = MAP_PAGE;
      if (projectList.size() > 0) {
        if (System.getProperty("DEBUG") != null) {
          System.out.println("GoogleMapsFromSearchHitsPortlet-> RequestDispatcher.include() ...");
        }
        PortletContext context = getPortletContext();
        PortletRequestDispatcher requestDispatcher =
            context.getRequestDispatcher(defaultView);
        requestDispatcher.include(request, response);
      }
      if (System.getProperty("DEBUG") != null) {
        System.out.println("GoogleMapsFromSearchHitsPortlet-> Finished.");
      }
    } catch (Exception e) {
      e.printStackTrace(System.out);
      throw new PortletException(e.getMessage());
    }
  }
}
