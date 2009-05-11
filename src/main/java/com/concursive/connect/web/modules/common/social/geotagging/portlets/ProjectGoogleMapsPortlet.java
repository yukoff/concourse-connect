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
package com.concursive.connect.web.modules.common.social.geotagging.portlets;

import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.portal.PortalUtils;

import javax.portlet.*;
import java.io.IOException;

/**
 * A google map portlet that shows a project location
 *
 * @author matt rajkowski
 * @created June 30, 2008
 */
public class ProjectGoogleMapsPortlet extends GenericPortlet {
  // JSPs
  private static final String VIEW_PAGE = "/portlets/project_google_maps/project_google_maps-view.jsp";

  // Attributes for view
  public static final String API_DOMAIN = "domain";
  public static final String API_KEY = "key";
  public static final String PROJECT = "project";

  public void doView(RenderRequest request, RenderResponse response)
      throws PortletException, IOException {
    try {
      // Objects from portal
      Project project = PortalUtils.getProject(request);
      request.setAttribute(PROJECT, project);

      request.setAttribute(API_DOMAIN, PortalUtils.getApplicationPrefs(request).get("GOOGLE_MAPS.DOMAIN"));
      request.setAttribute(API_KEY, PortalUtils.getApplicationPrefs(request).get("GOOGLE_MAPS.KEY"));

      // Skip the portlet if the project isn't geocoded
      if (project.isGeoCoded()) {
        // JSP view
        String defaultView = VIEW_PAGE;
        PortletContext context = getPortletContext();
        PortletRequestDispatcher requestDispatcher =
            context.getRequestDispatcher(defaultView);
        requestDispatcher.include(request, response);
      }
    } catch (Exception e) {
      e.printStackTrace(System.out);
      throw new PortletException(e.getMessage());
    }
  }
}