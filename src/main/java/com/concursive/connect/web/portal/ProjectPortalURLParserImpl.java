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

import com.concursive.commons.text.StringUtils;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pluto.driver.url.PortalURL;
import org.apache.pluto.driver.url.PortalURLParameter;
import org.apache.pluto.driver.url.PortalURLParser;
import org.apache.pluto.driver.url.impl.RelativePortalURLImpl;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;
import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

/**
 * A replacement portal url parser which implements simple URLs for the
 * project module portlets
 *
 * @author matt rajkowski
 * @created October 24, 2008
 */
public class ProjectPortalURLParserImpl implements PortalURLParser {

  private static final Log LOG = LogFactory.getLog(ProjectPortalURLParserImpl.class);

  /**
   * The singleton parser instance.
   */
  private static final PortalURLParser PARSER = new ProjectPortalURLParserImpl();

  // Constants used for Encoding/Decoding ------------------------------------

  public static final String ALLOWED_PORTAL_PARAMETERS = "plutoAllowedPortalParameters";

  private static final String PREFIX = "__";
  private static final String DELIM = "_";
  private static final String PORTLET_ID = "pd";
  private static final String ACTION = "ac";
  private static final String RENDER_PARAM = "rp";
  private static final String WINDOW_STATE = "ws";
  private static final String PORTLET_MODE = "pm";
  private static final String VALUE_DELIM = "0x0";

  private static final String[][] ENCODINGS = new String[][]{
      //new String[]{"_", "0x1"},
      new String[]{".", "0x2"},
      new String[]{"/", "0x3"},
      new String[]{"\r", "0x4"},
      new String[]{"\n", "0x5"},
      new String[]{"<", "0x6"},
      new String[]{">", "0x7"},
      new String[]{" ", "0x8"},
      new String[]{"#", "0x9"},
  };

  // Constructor -------------------------------------------------------------

  /**
   * Private constructor that prevents external instantiation.
   */
  private ProjectPortalURLParserImpl() {
    // Do nothing.
  }

  /**
   * Returns the singleton parser instance.
   *
   * @return the singleton parser instance.
   */
  public static PortalURLParser getParser() {
    return PARSER;
  }

  // Public Methods ----------------------------------------------------------

  /**
   * Parse a servlet request to a portal URL.
   *
   * @param request the servlet request to parse.
   * @return the portal URL.
   */
  public PortalURL parse(HttpServletRequest request) {

    LOG.debug("Parsing URL: " + request.getRequestURI());

    StringBuffer url = new StringBuffer();

    // Build the URL from various items...
    if (request.getParameterMap() != null) {
      String action = request.getParameter("portlet-action");
      String projectValue = request.getParameter("portlet-pid");
      String object = request.getParameter("portlet-object");
      String value = request.getParameter("portlet-value");
      String params = request.getParameter("portlet-params");
      // Append the portlet action
      // @note TEST TEST TEST
      //url.append("/").append(action);
      url.append("/").append(StringUtils.encodeUrl(action));
      // Append the targeted profile
      Project project = ProjectUtils.loadProject(Integer.parseInt(projectValue));
      url.append("/").append(project.getUniqueId());
      // Append the object in the profile
      // @note TEST TEST TEST
      //url.append("/").append(object);
      url.append("/").append(StringUtils.encodeUrl(object));
      // Append any object value, like object id
      if (StringUtils.hasText(value)) {
        url.append("/").append(StringUtils.encodeUrl(value));
      }
      // Append any parameters
      if (StringUtils.hasText(params)) {
        // @note TEST TEST TEST
        //url.append("/").append(params);
        url.append("/").append(StringUtils.encodeUrl(params));
      }
      LOG.debug("reconstructed url: " + url.toString());
    }

    String servletName = url.toString();

    // Construct portal URL using info retrieved from servlet request.
    String contextPath = request.getContextPath();
    LOG.debug("contextPath: " + contextPath);
    PortalURL portalURL = new RelativePortalURLImpl(contextPath, servletName, getParser());

    // Action window definition: portalURL.setActionWindow().
    String portletAction = request.getParameter("portletAction");
    if (portletAction != null && portletAction.startsWith(PREFIX + ACTION)) {
      LOG.debug("found action");
      portalURL.setActionWindow(decodeControlParameter(portletAction)[0]);
      portalURL.setRenderPath(contextPath + ".");
    }

    // Window state definition: portalURL.setWindowState().
    String portletWindowState = null;
    int windowStateCount = 0;
    while ((portletWindowState = request.getParameter("portletWindowState" + (++windowStateCount))) != null) {
      String[] decoded = decodeControlParameter(portletWindowState);
      portalURL.setWindowState(decoded[0], new WindowState(decoded[1]));
    }

    // Portlet mode definition: portalURL.setPortletMode().
    String portletMode = request.getParameter("portletMode");
    if (portletMode != null) {
      String[] decoded = decodeControlParameter(portletMode);
      portalURL.setPortletMode(decoded[0], new PortletMode(decoded[1]));
    }

    // Portal URL parameter: portalURL.addParameter().
    Enumeration params = request.getParameterNames();
    while (params.hasMoreElements()) {
      String parameter = (String) params.nextElement();
      if (parameter.startsWith(PREFIX + RENDER_PARAM)) {
        String value = request.getParameter(parameter);
        LOG.debug("parameter: " + parameter);
        portalURL.addParameter(decodeParameter(parameter, value));
      }
    }

    // Return the portal URL.
    return portalURL;
  }

  private static void appendParameter(StringBuffer url, String parameter) {
    if (url.length() == 0 || !url.toString().contains("?")) {
      url.append("?");
    } else {
      url.append("&");
    }
    LOG.debug("appendParameter: " + parameter);
    url.append(parameter);
  }

  /**
   * Converts a portal URL to a URL string.
   *
   * @param portalURL the portal URL to convert.
   * @return a URL string representing the portal URL.
   */
  public String toString(PortalURL portalURL) {

    //servletPath: /context/show/project/module
    LOG.debug("servletPath: " + portalURL.getServletPath());
    //renderPath: /context.SomePortlet!T3
    LOG.debug("renderPath: " + portalURL.getRenderPath());

    // Decode the current url
    StringBuffer buffer = new StringBuffer();

    // Detect if pointing to a new url based on passed parameters
    String action = null;
    String object = null;
    String value = null;
    String params = null;
    for (Object paramObject : portalURL.getParameters()) {
      PortalURLParameter param = (PortalURLParameter) paramObject;
      if ("portlet-action".equals(param.getName())) {
        action = param.getValues()[0];
      } else if ("portlet-object".equals(param.getName())) {
        object = param.getValues()[0];
      } else if ("portlet-value".equals(param.getName())) {
        value = param.getValues()[0];
      } else if ("portlet-params".equals(param.getName())) {
        params = param.getValues()[0];
      }
    }

    if (action != null || object != null || value != null || params != null) {

      // Strip off the context for determining the components of the url
      String url = portalURL.getServletPath();
      if (portalURL.getRenderPath() != null) {
        String ctx = portalURL.getRenderPath().substring(0, portalURL.getRenderPath().indexOf("."));
        if (ctx.length() > 1) {
          url = url.substring(ctx.length());
          buffer.append(ctx);
        }
      }
      LOG.debug("Base url: " + url);

      // Split apart the url items
      String[] currentURL = url.split("/");
      int level = 0;
      if (url.startsWith("/")) {
        level = 1;
      }

      // Add the action
      if (action != null) {
        buffer.append("/").append(action);
      } else {
        buffer.append("/").append(currentURL[level]);
      }

      // Add the project uniqueId
      buffer.append("/");
      buffer.append(currentURL[level + 1]);

      // Add the object
      if (object != null) {
        buffer.append("/").append(object);
      } else {
        if (currentURL.length > level + 2) {
          buffer.append("/").append(currentURL[level + 2]);
        }
      }

      // Add the value
      if (value != null) {
        buffer.append("/").append(value);
      }

      // Add the params
      if (params != null) {
        buffer.append("/").append(params);
      }

    } else {
      // Use the current path
      if (!portalURL.getServletPath().startsWith("/")) {
        buffer.append("/");
      }
      buffer.append(portalURL.getServletPath());
    }


    // Append action and render parameters
    for (Iterator it = portalURL.getParameters().iterator();
         it.hasNext();) {

      PortalURLParameter param = (PortalURLParameter) it.next();

      if ("portlet-action".equals(param.getName())) {
        continue;
      } else if ("portlet-object".equals(param.getName())) {
        continue;
      } else if ("portlet-value".equals(param.getName())) {
        continue;
      } else if ("portlet-params".equals(param.getName())) {
        continue;
      } else if ("out".equals(param.getName())) {
        if (StringUtils.hasText(param.getValues()[0])) {
          appendParameter(buffer, param.getName() + "=" + param.getValues()[0]);
        }
        continue;
      } else if ("popup".equals(param.getName())) {
        if (StringUtils.hasText(param.getValues()[0])) {
          appendParameter(buffer, param.getName() + "=" + param.getValues()[0]);
        }
        continue;
      }

      // Encode action params in the query appended at the end of the URL.
      if (portalURL.getActionWindow() != null
          && portalURL.getActionWindow().equals(param.getWindowId())) {
        for (int i = 0; i < param.getValues().length; i++) {
          if (StringUtils.hasText(param.getValues()[i])) {
            appendParameter(buffer, param.getName() + "=" + param.getValues()[i]);
          }
        }
      } else if (param.getValues() != null && param.getValues().length > 0) {
        // Encode the parameter ONLY if it targets the currently being rendered portlet
        if (param.getWindowId().equals(portalURL.getRenderPath())) {
          // The Project Portal uses clean URLs, so portlet window targeting is not used
          if (!buffer.toString().contains("?" + param.getName() + "=") &&
              !buffer.toString().contains("&" + param.getName() + "=")) {
            String valueString = encodeMultiValues(param.getValues());
            // @note TEST TEST TEST
            //appendParameter(buffer, param.getName() + "=" + valueString);
            appendParameter(buffer, param.getName() + "=" + StringUtils.encodeUrl(valueString));
          } else {
//           If param already exists, replace the value
          }
        }
      }
    }

    // Append the action window definition, if it exists
    if (portalURL.getActionWindow() != null) {
      appendParameter(buffer, "portletAction=" + PREFIX + ACTION + encodeCharacters(portalURL.getActionWindow()));
    }

    // Append portlet mode definitions
    for (Iterator it = portalURL.getPortletModes().entrySet().iterator();
         it.hasNext();) {
      Map.Entry entry = (Map.Entry) it.next();
      // If the portlet mode is "view" then no need to show the portlet mode in the url because that is the default
      if (!"view".equals(entry.getValue().toString())) {
        appendParameter(buffer, "portletMode=" + encodeControlParameter(PORTLET_MODE, entry.getKey().toString(),
            entry.getValue().toString()));
      }
    }

    // Append window state definitions
    int windowStateCount = 0;
    for (Iterator it = portalURL.getWindowStates().entrySet().iterator();
         it.hasNext();) {
      ++windowStateCount;
      Map.Entry entry = (Map.Entry) it.next();
      // If the portlet window state is "normal" then no need to show it in the url because that is the default
      if (!"normal".equals(entry.getValue().toString())) {
        appendParameter(buffer, "portletWindowState" + windowStateCount + "=" +
            encodeControlParameter(WINDOW_STATE, entry.getKey().toString(),
                entry.getValue().toString()));
      }
    }

    // Construct the string representing the portal URL.
    return buffer.toString();
  }


  // Private Encoding/Decoding Methods ---------------------------------------

  /**
   * Encode a control parameter.
   *
   * @param type     the type of the control parameter, which may be:
   *                 portlet mode, window state, or render parameter.
   * @param windowId the portlet window ID.
   * @param name     the name to encode.
   * @return encoded control parameter
   */
  private static String encodeControlParameter(String type,
                                               String windowId,
                                               String name) {
    StringBuffer buffer = new StringBuffer();
    buffer.append(PREFIX).append(type)
        .append(encodeCharacters(windowId))
        .append(DELIM).append(name);
    return buffer.toString();
  }

  /**
   * Encode a string array containing multiple values into a single string.
   * This method is used to encode multiple render parameter values.
   *
   * @param values the string array to encode.
   * @return a single string containing all the values.
   */
  private String encodeMultiValues(String[] values) {
    StringBuffer buffer = new StringBuffer();
    for (int i = 0; i < values.length; i++) {
      buffer.append(values[i] != null ? values[i] : "");
      if (i + 1 < values.length) {
        buffer.append(VALUE_DELIM);
      }
    }
    return encodeCharacters(buffer.toString());
  }

  /**
   * Encode special characters contained in the string value.
   *
   * @param string the string value to encode.
   * @return the encoded string.
   */
  public static String encodeCharacters(String string) {
    for (int i = 0; i < ENCODINGS.length; i++) {
      string = StringUtils.replace(string,
          ENCODINGS[i][0],
          ENCODINGS[i][1]);
    }
    return string;
  }


  /**
   * Decode a control parameter.
   *
   * @param control the control parameter to decode.
   * @return values  a pair of decoded values.
   */
  private String[] decodeControlParameter(String control) {
    String[] valuePair = new String[2];
    control = control.substring((PREFIX + PORTLET_ID).length());
    int index = control.indexOf(DELIM);
    if (index >= 0) {
      valuePair[0] = control.substring(0, index);
      valuePair[0] = decodeCharacters(valuePair[0]);
      if (index + 1 <= control.length()) {
        valuePair[1] = control.substring(index + 1);
        valuePair[1] = decodeCharacters(valuePair[1]);
      } else {
        valuePair[1] = "";
      }
    } else {
      valuePair[0] = decodeCharacters(control);
    }
    return valuePair;
  }

  /**
   * Decode a name-value pair into a portal URL parameter.
   *
   * @param name  the parameter name.
   * @param value the parameter value.
   * @return the decoded portal URL parameter.
   */
  private PortalURLParameter decodeParameter(String name, String value) {

    LOG.debug("Decoding parameter: name=" + name + ", value=" + value);

    // Defect PLUTO-361
    // ADDED: if the length is less than this, there is no parameter...
    if (name.length() < (PREFIX + PORTLET_ID).length()) {
      return null;
    }

    // Decode the name into window ID and parameter name.
    String noPrefix = name.substring((PREFIX + PORTLET_ID).length());
    String windowId = noPrefix.substring(0, noPrefix.indexOf(DELIM));
    String paramName = noPrefix.substring(noPrefix.indexOf(DELIM) + 1);

    // Decode special characters in window ID and parameter value.
    windowId = decodeCharacters(windowId);
    if (value != null) {
      value = decodeCharacters(value);
    }

    // Split multiple values into a value array.
    String[] paramValues = value.split(VALUE_DELIM);

    if (paramValues.length == 1) {
      LOG.debug(windowId + " - parameter: " + paramName + "=" + paramValues[0]);
    }


    // Construct portal URL parameter and return.
    return new PortalURLParameter(windowId, paramName, paramValues);
  }

  /**
   * Decode special characters contained in the string value.
   *
   * @param string the string value to decode.
   * @return the decoded string.
   */
  private static String decodeCharacters(String string) {
    for (int i = 0; i < ENCODINGS.length; i++) {
      string = StringUtils.replace(string,
          ENCODINGS[i][1],
          ENCODINGS[i][0]);
    }
    return string;
  }

  public static void addAllParameters(HttpServletRequest request, PortalURL portalURL) {
    Enumeration params = request.getParameterNames();
    while (params.hasMoreElements()) {
      String parameter = (String) params.nextElement();
      String value = request.getParameter(parameter);
      if (StringUtils.hasText(value)) {
        if (!parameter.startsWith(PREFIX + RENDER_PARAM)
            && !parameter.equals("portletWindowState1")
            && !parameter.startsWith("portlet-")
            && !(parameter.equals("command") && "ProjectCenter".equals(value))) {
          if (StringUtils.hasText(value)) {
            LOG.debug("Made a parameter available to the portlet: " + parameter);
            portalURL.addParameter(new PortalURLParameter(portalURL.getRenderPath(), parameter, value));
          }
        } else {
          if ("portlet-command".equals(parameter)) {
            LOG.debug("Made a parameter available to the portlet: " + parameter);
            portalURL.addParameter(new PortalURLParameter(portalURL.getRenderPath(), parameter, value));
          }
        }
      }
    }
  }
}
