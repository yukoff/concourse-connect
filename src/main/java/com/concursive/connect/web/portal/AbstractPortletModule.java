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
package com.concursive.connect.web.portal;

import com.concursive.commons.web.mvc.beans.GenericBean;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.portlet.*;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;


/**
 * This class provides a framework for developing MVC portlets by utilizing
 * IPortletViewer and IPortletAction implementations.
 * <p/>
 * IPortletViewer implementations return a JSP file for invoking, IPortletAction
 * implementations return null OR a bean to be auto-supplied to a view.
 *
 * @author matt rajkowski
 */
public abstract class AbstractPortletModule extends GenericPortlet {

  // Logger
  private static Log LOG = LogFactory.getLog(AbstractPortletModule.class);

  /**
   * The key to the map for an action and its associated viewer.
   */
  public static final String COMMAND = "portlet-command";

  /**
   * The storage prefix for a form bean in the session
   */
  public static final String FORM_BEAN = "portletFormBean";

  /**
   * A map containing all the IPortletViewer objects for a given portlet.
   */
  protected Map<String, IPortletViewer> viewers = new HashMap<String, IPortletViewer>();

  /**
   * A map containing all the IPortletAction objects for a given portlet.
   */
  protected Map<String, IPortletAction> actions = new HashMap<String, IPortletAction>();

  /**
   * The default command for a portlet. This is the value of the command that the
   * portlet framework uses to determine the first viewer to use when the portlet
   * is rendered for the first time.
   */
  protected String defaultCommand;

  /**
   * This is the standard GenericPortlet's init method. It has the additional
   * responsibility of putting the actions and viewers into the viewer and
   * action maps for subsequent use by the framework.
   */
  public void init(PortletConfig portletConfig) throws PortletException {
    super.init(portletConfig);
    LOG.info("Initializing: " + this.getClass().getSimpleName());
    doPopulateActionsAndViewers();
  }

  protected abstract void doPopulateActionsAndViewers();

  protected void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {

    LOG.debug("Determining routing using viewers map (" + viewers.size() + ") and actions map (" + actions.size() + ")");
    if (LOG.isTraceEnabled()) {
      showAllRequestParameters(request);
      showAllSessionParameters(request);
    }

    try {
      // Determine the command to execute, as set by the portlet
      String command = request.getParameter(COMMAND);
      if (!StringUtils.isEmpty(command)) {
        LOG.debug("using command from portlet: " + command);
      } else {
        // Determine the command to execute, as set by the portlet session
        PortletSession session = request.getPortletSession(false);
        if (session != null) {
          command = (String) session.getAttribute(COMMAND);
        }
        if (!StringUtils.isEmpty(command)) {
          LOG.debug("using command from portlet action: " + command);
          session.removeAttribute(COMMAND);
        } else {
          // Determine the command to execute, as set by the portal
          command = (String) request.getAttribute(COMMAND);
          if (!StringUtils.isEmpty(command)) {
            LOG.debug("using command from portal: " + command);
          } else {
            // Use the default command
            command = defaultCommand;
            LOG.debug("using default command: " + command);
          }
        }
      }
      request.setAttribute("portletCommand", command);

      // Retrieve the viewer from the map
      LOG.debug("Searching for viewer using command: " + command);
      IPortletViewer viewer = viewers.get(command);
      if (viewer == null) {
        LOG.error("A viewer was not found for command: " + command);
      } else {
        // Manage the form bean and make available to the request
        PortalUtils.processFormBean(request);

        // Execute the command
        LOG.debug("Executing viewer: " + viewer.getClass().getName());
        String jsp = viewer.doView(request, response);

        // The viewer optionally returns a JSP view
        if (jsp != null) {
          PortletContext context = getPortletContext();
          PortletRequestDispatcher requestDispatcher = context.getRequestDispatcher(jsp);
          if (requestDispatcher == null) {
            LOG.error("JSP compile error: " + jsp);
          }
          LOG.debug("Dispatching to JSP: " + jsp);
          requestDispatcher.include(request, response);
        }
      }

    } catch (Throwable t) {
      LOG.error("doView", t);
    }
  }

  public void processAction(ActionRequest request, ActionResponse response) {
    if (actions.size() == 0) {
      return;
    }

    if (LOG.isTraceEnabled()) {
      showAllRequestParameters(request);
      showAllSessionParameters(request);
    }

    // Determine the command to execute, as set by the portlet
    String command = request.getParameter(COMMAND);
    if (!StringUtils.isEmpty(command)) {
      LOG.debug("using command from portlet: " + command);
    } else {
      // Determine the command to execute, as set by the portal
      command = (String) request.getAttribute(COMMAND);
      if (!StringUtils.isEmpty(command)) {
        LOG.debug("using command from portal: " + command);
      } else {
        LOG.error("could not find a command");
      }
    }
    request.setAttribute("portletCommand", command);

    // Retrieve the action from the map
    LOG.debug("Searching for action using command: " + command);
    IPortletAction action = actions.get(command);
    if (action == null) {
      LOG.error("An action was not found for command: " + command);
    }
    try {
      GenericBean formBean = action.processAction(request, response);
      if (formBean != null) {
        // Need to add the form bean to the portlet session because the portal
        // uses redirects which voids the request
        PortletSession session = request.getPortletSession();
        session.setAttribute(FORM_BEAN, formBean);
        // Handle parameters
        if ("true".equals(request.getParameter("popup"))) {
          response.setRenderParameter("popup", "true");
        }
        if (request.getParameter("redirectTo") != null) {
          response.setRenderParameter("redirectTo", request.getParameter("redirectTo"));
        }
        if (request.getAttribute("portlet-command") != null) {
          session.setAttribute(COMMAND, request.getAttribute("portlet-command"));
        }
      }
    } catch (Throwable t) {
      LOG.error("Error with command: " + command, t);
    }
  }

  private void showAllRequestParameters(PortletRequest request) {
    Enumeration paramNames = request.getParameterNames();
    LOG.trace("=============== Portlet Request Parameters ===============");
    while (paramNames.hasMoreElements()) {
      String paramName = (String) paramNames.nextElement();
      String[] paramValues = request.getParameterValues(paramName);
      if (paramValues.length == 1) {
        String paramValue = paramValues[0];
        LOG.trace(paramName + "=" + paramValue);
      } else {
        for (String paramValue : paramValues) {
          LOG.trace(paramName + "=" + paramValue);
        }
        LOG.trace("====");
      }
    }
  }

  private void showAllSessionParameters(PortletRequest request) {
    //HttpSession pSession = ((HttpServletRequest) request).getSession();
    PortletSession pSession = request.getPortletSession();
    Enumeration attrNames = pSession.getAttributeNames();
    LOG.trace("=============== Portlet Session Parameters ===============");
    while (attrNames.hasMoreElements()) {
      String attrName = (String) attrNames.nextElement();
      Object attrValue = pSession.getAttribute(attrName);
      LOG.trace(attrName + "=[" + attrValue + "]");
    }
  }

}
