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
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.profile.dao.ProjectCategory;
import com.concursive.connect.web.modules.profile.dao.ProjectCategoryList;
import com.concursive.connect.web.modules.profile.dao.ProjectList;
import com.concursive.connect.web.modules.profile.dao.ProjectPopularity;
import com.concursive.connect.web.portal.PortalUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.portlet.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.Calendar;

/**
 * Project list portlet
 *
 * @author Kailash Bhoopalam
 * @created June 04, 2008
 */
public class PopularProjectsByCategoryPortlet extends GenericPortlet {

  private static Log LOG = LogFactory.getLog(PopularProjectsByCategoryPortlet.class);

  // Pages
  private static final String VIEW_PAGE = "/portlets/popular_projects_by_category/popular_projects_by_category-view.jsp";
  // Preferences
  private static final String PREF_CATEGORY_NAME = "category";
  private static final String PREF_TITLE = "title";
  private static final String PREF_RECORD_LIMIT = "limit";
  private static final String PREF_ORDER = "order";
  private static final String PREF_DAYS_LIMIT = "daysLimit";
  private static final String PREF_SHOW_RATING = "showRating";
  private static final String PREF_SHOW_POINTS = "showPoints";
  // Attribute names for objects available in the view
  private static final String PROJECT_LIST = "projectList";
  private static final String TITLE = "title";
  private static final String SHOW_RATING = "showRating";
  private static final String SHOW_POINTS = "showPoints";

  public void doView(RenderRequest request, RenderResponse response)
      throws PortletException, IOException {
    try {
      String defaultView = VIEW_PAGE;

      // Get preferences
      String categoryValue = request.getPreferences().getValue(PREF_CATEGORY_NAME, null);
      request.setAttribute(TITLE, request.getPreferences().getValue(PREF_TITLE, null));
      request.setAttribute(SHOW_RATING, request.getPreferences().getValue(PREF_SHOW_RATING, null));
      request.setAttribute(SHOW_POINTS, request.getPreferences().getValue(PREF_SHOW_POINTS, null));

      Connection db = PortalUtils.useConnection(request);

      ProjectCategoryList categories = new ProjectCategoryList();
      categories.setCategoryDescription(categoryValue);
      categories.setEnabled(true);
      categories.setTopLevelOnly(true);
      categories.buildList(db);

      ProjectCategory category = (categories.size() > 0 ? categories.get(0) : null);
      if (category != null) {
        LOG.debug("Found category: " + category.getDescription());

        PopularityCriteria popularityCriteria = new PopularityCriteria();
        popularityCriteria.setInstanceId(PortalUtils.getInstance(request).getId());
        popularityCriteria.setLimit(request.getPreferences().getValue(PREF_RECORD_LIMIT, "-1"));
        popularityCriteria.setOrder(request.getPreferences().getValue(PREF_ORDER, "desc"));

        int daysLimit = Integer.parseInt(request.getPreferences().getValue(PREF_DAYS_LIMIT, "0")) * -1;
        if (daysLimit < 0) {
          Calendar startCal = Calendar.getInstance();
          startCal.add(Calendar.DATE, daysLimit);
          popularityCriteria.setStartDate(new Timestamp(startCal.getTimeInMillis()));
          if (LOG.isDebugEnabled()) {
            LOG.debug("daysLimit: " + daysLimit + " " + startCal.getTime());
          }
        }

        popularityCriteria.setEndDate(new Timestamp(System.currentTimeMillis()));

        if (PortalUtils.getDashboardPortlet(request).isCached()) {
          if (PortalUtils.isPortletInProtectedMode(request)) {
            // Use the most generic settings since this portlet is cached
            LOG.debug("setForParticipant");
            popularityCriteria.setForParticipant(Constants.TRUE);
          } else {
            // Use the most generic settings since this portlet is cached
            LOG.debug("setForPublic");
            popularityCriteria.setForPublic(Constants.TRUE);
          }
        } else {
          // Use the current user's setting
          User thisUser = PortalUtils.getUser(request);
          if (thisUser.isLoggedIn()) {
            // Show what's popular out of all the projects the user has access to
            popularityCriteria.setForParticipant(Constants.TRUE);
          } else {
            // Just show the public ones
            popularityCriteria.setForPublic(Constants.TRUE);
          }
        }

        // Query the projects based on the popularity criteria
        ProjectList projectList = ProjectPopularity.retrieveProjects(db, popularityCriteria, category.getId());
        request.setAttribute(PROJECT_LIST, projectList);

        LOG.debug("Projects found: " + projectList.size());

        if (projectList.size() > 0) {
          PortletContext context = getPortletContext();
          PortletRequestDispatcher requestDispatcher =
              context.getRequestDispatcher(defaultView);
          requestDispatcher.include(request, response);
        }
      }
    } catch (Exception e) {
      throw new PortletException(e.getMessage());
    }
  }
}
