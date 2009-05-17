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
package com.concursive.connect.web.modules.common.social.popularity.portlets;

import com.concursive.connect.Constants;
import com.concursive.connect.web.modules.common.social.popularity.beans.PopularityCriteria;
import com.concursive.connect.web.modules.profile.dao.ProjectCategory;
import com.concursive.connect.web.modules.profile.dao.ProjectCategoryList;
import com.concursive.connect.web.modules.profile.dao.ProjectPopularity;
import com.concursive.connect.web.portal.PortalUtils;

import javax.portlet.*;
import java.io.IOException;
import java.sql.Connection;
import java.util.Calendar;

/**
 * Project list portlet
 *
 * @author Kailash Bhoopalam
 * @created June 04, 2008
 */
public class PopularProjectsByCategoryPortlet extends GenericPortlet {

  // Pages
  private static final String VIEW_PAGE = "/portlets/popular_projects_by_category/popular_projects_by_category-view.jsp";

  // Preferences
  private static final String PREF_CATEGORY_NAME = "category";
  private static final String PREF_TITLE = "title";
  private static final String PREF_RECORD_LIMIT = "limit";
  private static final String PREF_ORDER = "order";
  private static final String PREF_DAYS_LIMIT = "daysLimit";

  // Attribute names for objects available in the view
  private static final String PROJECT_LIST = "projectList";
  private static final String TITLE = "title";

  public void doView(RenderRequest request, RenderResponse response)
      throws PortletException, IOException {
    try {
      String defaultView = VIEW_PAGE;

      // Get preferences
      String categoryValue = request.getPreferences().getValue(PREF_CATEGORY_NAME, null);
      request.setAttribute(TITLE, request.getPreferences().getValue(PREF_TITLE, null));

      Connection db = PortalUtils.getConnection(request);

      ProjectCategoryList categories = new ProjectCategoryList();
      categories.setCategoryName(categoryValue);
      categories.setEnabled(true);
      categories.setTopLevelOnly(true);
      categories.buildList(db);

      ProjectCategory category = (categories.size() > 0 ? categories.get(0) : null);
      if (category != null) {
        PopularityCriteria popularityCriteria = new PopularityCriteria();
        popularityCriteria.setLimit(request.getPreferences().getValue(PREF_RECORD_LIMIT, "-1"));
        popularityCriteria.setOrder(request.getPreferences().getValue(PREF_ORDER, "desc"));

        Calendar endCal = Calendar.getInstance();
        popularityCriteria.setEndDate(new java.sql.Timestamp(endCal.getTimeInMillis()));

        Calendar startCal = Calendar.getInstance();
        startCal.add(Calendar.DATE, Integer.parseInt(request.getPreferences().getValue(PREF_DAYS_LIMIT, "0")) * -1);
        popularityCriteria.setStartDate(new java.sql.Timestamp(startCal.getTimeInMillis()));

        if (PortalUtils.getDashboardPortlet(request).isCached()) {
          if (PortalUtils.canShowSensitiveData(request)) {
            // Use the most generic settings since this portlet is cached
            popularityCriteria.setForParticipant(Constants.TRUE);
          } else {
            // Use the most generic settings since this portlet is cached
            popularityCriteria.setForPublic(Constants.TRUE);
          }
        } else {
          // Use the most generic settings since this portlet is cached
          popularityCriteria.setForPublic(Constants.TRUE);
        }

        request.setAttribute(PROJECT_LIST, ProjectPopularity.retrieveProjects(db, popularityCriteria, category.getId()));

        // JSP view
        PortletContext context = getPortletContext();
        PortletRequestDispatcher requestDispatcher =
            context.getRequestDispatcher(defaultView);
        requestDispatcher.include(request, response);
      }
    } catch (Exception e) {
      throw new PortletException(e.getMessage());
    }
  }
}
