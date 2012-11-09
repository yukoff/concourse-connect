/*
 * ConcourseConnect
 * Copyright 2012 Concursive Corporation
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
package com.concursive.connect.web.filters;

import com.concursive.connect.Constants;
import com.concursive.connect.web.utils.ClientType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Makes sure a request has a ClientType object in the session
 *
 * @author matt rajkowski
 * @created July 27, 2012
 */
public class ClientTypeRequestFilter implements Filter {

  private static Log LOG = LogFactory.getLog(ClientTypeRequestFilter.class);

  public void init(FilterConfig config) throws ServletException {
  }

  public void destroy() {
  }

  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
    HttpServletRequest hsRequest = (HttpServletRequest) request;
    HttpServletResponse hsResponse = (HttpServletResponse) response;

    // Determine the requested path
    String contextPath = hsRequest.getContextPath();
    String uri = hsRequest.getRequestURI();
    String requestedPath = uri.substring(contextPath.length());

    LOG.debug("Requested Path is: " + requestedPath);

    // Determine if a client session is required
    if (requestedPath.startsWith("/api") || requestedPath.startsWith("/Process")) {
      // No need to track this client via a session

    } else {
      // Track information about the client
      ClientType clientType = (ClientType) hsRequest.getSession().getAttribute(Constants.SESSION_CLIENT_TYPE);
      if (clientType == null) {
        clientType = new ClientType(hsRequest);
        hsRequest.getSession().setAttribute(Constants.SESSION_CLIENT_TYPE, clientType);
        LOG.debug("Created clientType");
      } else if (clientType.getId() == -1) {
        clientType.setParameters(hsRequest);
      }
    }
    chain.doFilter(hsRequest, hsResponse);
  }
}