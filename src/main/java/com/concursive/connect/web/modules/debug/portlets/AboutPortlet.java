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

package com.concursive.connect.web.modules.debug.portlets;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.portlet.*;
import java.io.IOException;

/**
 * Description of Class
 *
 * @author matt rajkowski
 * @version $Id$
 * @created Feb 9, 2007
 */
public class AboutPortlet extends GenericPortlet {

  private static Log LOG = LogFactory.getLog(AboutPortlet.class);

  private static final String VIEW_PAGE = "/portlets/about/about-view.jsp";
  private static final String EDIT_PAGE = "/portlets/about/about-edit.jsp";
  private static final String HELP_PAGE = "/portlets/about/about-help.jsp";

  // GenericPortlet Impl -----------------------------------------------------

  public void doView(RenderRequest request, RenderResponse response)
      throws PortletException, IOException {
    LOG.debug("doView");

    // Generate a URL
    //PortletURL renderURL = response.createRenderURL();
    //renderURL.setParameter("name1", "value1");
    //renderURL.setParameter("name2", "value2");
    //String url = renderURL.toString();

    // Write directly
    //response.setContentType("text/html");
    //PrintWriter out = response.getWriter();
    //out.println("Hello World!");

    // Send a redirect
    //response.setPortletMode(PortletMode.VIEW);
    //response.sendRedirect("/");

    PortletPreferences prefs = request.getPreferences();
    String[] stringArrayNullValues = prefs.getValues("stringArrayNullValues", new String[]{null, "notNull", "notNull"});

    // See if the session value is unique
    PortletSession session = request.getPortletSession();
    if (session == null) {
      LOG.debug("Session is null!");
    } else {
      LOG.debug("Found session...");
      // Retrieve from session
      String value = (String) session.getAttribute("sessionValue");
      LOG.debug(" Getting the session value...");
      LOG.debug("  Value = " + value);
      request.setAttribute("sessionValue", value);
      // Set a value and try it right away
      LOG.debug(" Setting the session value...");
      request.getPortletSession().setAttribute("sessionValue", String.valueOf(System.currentTimeMillis()));
      LOG.debug(" Getting the session value...");
      LOG.debug("  Value = " + session.getAttribute("sessionValue"));
    }

    // Dispatch to JSP
    PortletContext context = getPortletContext();

    PortletRequestDispatcher requestDispatcher =
        context.getRequestDispatcher(VIEW_PAGE);

    requestDispatcher.include(request, response);

  }

  protected void doEdit(RenderRequest request, RenderResponse response)
      throws PortletException, IOException {
    LOG.debug("doEdit");
    PortletContext context = getPortletContext();
    PortletRequestDispatcher requestDispatcher =
        context.getRequestDispatcher(EDIT_PAGE);
    requestDispatcher.include(request, response);
  }

  protected void doHelp(RenderRequest request, RenderResponse response)
      throws PortletException, IOException {
    LOG.debug("doHelp");
    PortletContext context = getPortletContext();
    PortletRequestDispatcher requestDispatcher =
        context.getRequestDispatcher(HELP_PAGE);
    requestDispatcher.include(request, response);
  }

  public void processAction(ActionRequest request, ActionResponse response)
      throws PortletException, IOException {
    LOG.debug("processAction");

    LOG.debug("Writing prefs...");
    PortletPreferences prefs = request.getPreferences();
    prefs.setValue("nullValue", null);
    prefs.setValue("stringValue", "StringValue");
    prefs.setValues("stringArrayValues", new String[]{"stringArrayValues1", "stringArrayValues2"});
    prefs.setValues("stringArrayNullValues", new String[]{"stringArrayNullValues1", null, "stringArrayNullValues3 (2 is null)"});
    prefs.store();

    prefs.reset("stringArrayValues");
    prefs.store();

    response.setRenderParameter("test", "\"true\"");

    //response.setPortletMode(PortletMode.VIEW);
    //response.sendRedirect("/");
  }
}
