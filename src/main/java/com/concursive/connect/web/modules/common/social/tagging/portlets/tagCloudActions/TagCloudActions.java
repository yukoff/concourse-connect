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
package com.concursive.connect.web.modules.common.social.tagging.portlets.tagCloudActions;

import com.concursive.commons.text.StringUtils;
import com.concursive.connect.web.portal.IPortletViewer;
import com.concursive.connect.web.portal.PortalUtils;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import java.io.IOException;

/**
 * Sorting on the tag cloud
 *
 * @author Kailash Bhoopalam
 * @created November 17, 2008
 */
public class TagCloudActions implements IPortletViewer {

  // Pages
  private static final String VIEW_PAGE = "/portlets/tag_cloud_actions_portlet/tag_cloud_actions_portlet-view.jsp";

  //Preferences
  private static final String PREF_TITLE = "title";
  private static final String PREF_PAGE_URL = "pageURL";

  // Object Results
  private static final String CATEGORY_NAME_ATTRIBUTE = "categoryName";
  private static final String SORT_ORDER_ATTRIBUTE = "sortOrder";
  private static final String TITLE_ATTRIBUTE = "title";
  private static final String PAGE_URL_ATTRIBUTE = "pageURL";

  public String doView(RenderRequest request, RenderResponse response)
      throws PortletException, IOException {
    try {
      String defaultView = VIEW_PAGE;

      request.setAttribute(TITLE_ATTRIBUTE, request.getPreferences().getValue(PREF_TITLE, null));
      String pageURL = request.getPreferences().getValue(PREF_PAGE_URL, null);
      request.setAttribute(PAGE_URL_ATTRIBUTE, pageURL);

      String categoryName = PortalUtils.getPageView(request);
      if (StringUtils.hasText(categoryName)) {
        request.setAttribute(CATEGORY_NAME_ATTRIBUTE, categoryName);
      }

      String sortOrder = PortalUtils.getPageParameter(request);
      if (StringUtils.hasText(sortOrder)) {
        request.setAttribute(SORT_ORDER_ATTRIBUTE, sortOrder);
      }

      // JSP view
      return defaultView;

    } catch (Exception e) {
      e.printStackTrace(System.out);
      throw new PortletException(e.getMessage());
    }
  }
}
