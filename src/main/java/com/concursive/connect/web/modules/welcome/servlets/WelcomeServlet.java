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

package com.concursive.connect.web.modules.welcome.servlets;

import com.concursive.connect.Constants;
import com.concursive.connect.config.ApplicationPrefs;
import com.concursive.connect.config.ApplicationVersion;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.utils.ClientType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * The welcome page
 *
 * @author matt rajkowski
 * @version $Id$
 * @created February 8, 2008
 */

public class WelcomeServlet extends HttpServlet {

  private static final Log LOG = LogFactory.getLog(WelcomeServlet.class);

  public void init() {
  }

  public void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
    try {
      request.setCharacterEncoding("UTF-8");
    } catch (Exception e) {
      LOG.warn("Unsupported encoding");
    }
    try {
      // Save the requestURI to be used downstream (it gets rewritten on forwards)
      String contextPath = request.getContextPath();
      String uri = request.getRequestURI();
      String queryString = request.getQueryString();
      String requestedURL = uri.substring(contextPath.length()) + (queryString == null ? "" : "?" + queryString);
      if ("/index.shtml".equals(requestedURL)) {
        requestedURL = "";
      }
      request.setAttribute("requestedURL", requestedURL);
      // Configure the user's client
      ClientType clientType = (ClientType) request.getSession().getAttribute("clientType");
      if (clientType == null) {
        clientType = new ClientType();
        clientType.setParameters(request);
        request.getSession().setAttribute("clientType", clientType);
      }

      // Detect mobile
      if ("false".equals(request.getParameter("useMobile"))) {
        clientType.setMobile(false);
      }

      // Context startup initializes the prefs
      ApplicationPrefs applicationPrefs = (ApplicationPrefs) request.getSession().getServletContext().getAttribute("applicationPrefs");
      if (!applicationPrefs.isConfigured()) {
        RequestDispatcher initialSetup = request.getRequestDispatcher("/Setup.do?command=Default");
        initialSetup.forward(request, response);
      } else if (ApplicationVersion.isOutOfDate(applicationPrefs)) {
        // If the site is setup, then check to see if this is an upgraded version of the app
        RequestDispatcher upgrade = getServletConfig().getServletContext().getRequestDispatcher("/Upgrade.do?command=Default&style=true");
        upgrade.forward(request, response);
      } else if ("true".equals(applicationPrefs.get("PORTAL"))) {
        // If the site supports a portal, go to the portal
        // @todo implement mobile pages then turn this back on
//        if (clientType.getMobile()) {
        // If a mobile device is detected, offer a low-bandwidth option
//          RequestDispatcher portal = request.getRequestDispatcher("/Login.do?command=DetectMobile");
//          portal.forward(request, response);
//        } else {
        String pathToUse = request.getRequestURI().substring(request.getContextPath().length());
        RequestDispatcher portal = request.getRequestDispatcher(pathToUse + applicationPrefs.get("PORTAL.INDEX"));
        portal.forward(request, response);
//        }
      } else {
        // Go to the user's home page if logged in
        User thisUser = (User) request.getSession().getAttribute(Constants.SESSION_USER);
        if (thisUser != null && thisUser.getId() > 0) {
          RequestDispatcher portal = request.getRequestDispatcher("/ProjectManagement.do?command=Default");
          portal.forward(request, response);
        } else {
          RequestDispatcher portal = request.getRequestDispatcher("/Login.do?command=Default");
          portal.forward(request, response);
        }
      }
    } catch (Exception ex) {
      String msg = "Welcome failed";
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, msg);
    }
  }

}