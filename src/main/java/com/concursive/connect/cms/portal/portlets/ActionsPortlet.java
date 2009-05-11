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
package com.concursive.connect.cms.portal.portlets;

import com.concursive.commons.xml.XMLUtils;
import com.concursive.connect.Constants;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.profile.dao.ProjectCategory;
import com.concursive.connect.web.modules.profile.dao.ProjectCategoryList;
import com.concursive.connect.web.portal.PortalUtils;
import org.w3c.dom.Element;

import javax.portlet.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * A collection of actions that can be selected by the current user
 *
 * @author matt rajkowski
 * @created August 1, 2008
 */
public class ActionsPortlet extends GenericPortlet {

  // Pages
  private static final String VIEW_PAGE = "/portlets/actions/actions-view.jsp";

  // Preferences
  private static final String PREF_TITLE = "title";
  private static final String PREF_URLS = "urls";

  // Attribute names for objects available in the view
  private static final String TITLE = "title";
  private static final String URL_LIST = "urlList";

  public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
    try {
      // Define the current view
      String defaultView = VIEW_PAGE;

      // Get preferences
      request.setAttribute(TITLE, request.getPreferences().getValue(PREF_TITLE, null));

      User thisUser = PortalUtils.getUser(request);

      // Get the urls to display
      ArrayList<HashMap> urlList = new ArrayList<HashMap>();
      String[] urls = request.getPreferences().getValues(PREF_URLS, new String[0]);
      for (String urlPreference : urls) {
        XMLUtils xml = new XMLUtils("<values>" + urlPreference + "</values>", true);
        ArrayList<Element> items = new ArrayList<Element>();
        XMLUtils.getAllChildren(xml.getDocumentElement(), items);
        HashMap<String, String> url = new HashMap<String, String>();
        for (Element thisItem : items) {
          String name = thisItem.getTagName();
          String value = thisItem.getTextContent();
          url.put(name, value);
        }
        // Determine the secure URL if requested
        if (url.containsKey("href")) {
          String thisUrl = url.get("href");
          if (!thisUrl.startsWith("http")) {
            if ("true".equals(url.get("secure"))) {
              // If the portal has SSL enabled...
              boolean ssl = "true".equals(PortalUtils.getApplicationPrefs(request).get("SSL"));
              url.put("href", "http" + (ssl ? "s" : "") + "://" + PortalUtils.getServerDomainNameAndPort(request) + request.getContextPath() + thisUrl);
            } else {
              // The context path
              url.put("href", request.getContextPath() + thisUrl);
            }

          }
        }

        // Determine if the url can be shown
        boolean valid = true;

        // See if there are any special rules
        if (url.containsKey("rule")) {
          String rule = url.get("rule");
          if ("isLoggedIn".equals(rule)) {
            if (!(thisUser.getId() > 0)) {
              valid = false;
            }
          }
          if ("isLoggedOut".equals(rule)) {
            if (thisUser.getId() > 0) {
              valid = false;
            }
          }
          if ("isAdmin".equals(rule)) {
            if (!thisUser.getAccessAdmin()) {
              valid = false;
            }
          }
          if ("canStartProjects".equals(rule)) {
            if (!thisUser.getAccessAddProjects()) {
              valid = false;
            }
          }
        }

        // Determine if the rule requires an enabled category
        if (url.containsKey("categoryEnabled")) {
          // Get the category list
          ProjectCategoryList projectCategoryList = (ProjectCategoryList) request.getAttribute(Constants.REQUEST_TAB_CATEGORY_LIST);
          // Get the specific category
          ProjectCategory projectCategory = projectCategoryList.getFromValue(url.get("categoryEnabled"));
          if (projectCategory == null || !projectCategory.getEnabled()) {
            valid = false;
          }
        }

        // Global disable
        if (url.containsKey("enabled")) {
          if ("false".equals(url.get("enabled"))) {
            valid = false;
          }
        }
        // If valid, add it to the list
        if (valid) {
          urlList.add(url);
        }
      }
      request.setAttribute(URL_LIST, urlList);

      // Only output the portlet if there are any urls to show
      if (urlList.size() > 0) {
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