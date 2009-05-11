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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pluto.driver.url.PortalURL;
import org.apache.pluto.driver.url.PortalURLParameter;
import org.apache.pluto.driver.url.PortalURLParser;
import org.apache.pluto.driver.url.impl.RelativePortalURLImpl;
import org.apache.pluto.util.StringUtils;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

/**
 * The replacement portal url parser used by the embedded portal
 *
 * @author matt rajkowski
 * @created October 24, 2008
 */
public class PortalURLParserImpl implements PortalURLParser {

  private static final Log LOG = LogFactory.getLog(PortalURLParserImpl.class);

  /**
   * The singleton parser instance.
   */
  private static final PortalURLParser PARSER = new PortalURLParserImpl();

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
  private PortalURLParserImpl() {
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
    if (request.getParameterMap() != null) {
      // To construct the base URL for the portal, append the allowed portal URL
      // parameters that are specified by the portal manager.
      ArrayList allowedPortalParams = (ArrayList) request.getAttribute(ALLOWED_PORTAL_PARAMETERS);
      if (allowedPortalParams != null) {
        Iterator i = allowedPortalParams.iterator();
        while (i.hasNext()) {
          String paramName = (String) i.next();
          String paramValue = request.getParameter(paramName);
          if (paramValue != null) {
            appendParameter(url, paramName + "=" + paramValue);
          }
        }
      }
    }

    String contextPath = request.getContextPath();
    String servletName = request.getServletPath() + url.toString();

    // Construct portal URL using info retrieved from servlet request.
    PortalURL portalURL = new RelativePortalURLImpl(contextPath, servletName, getParser());

    StringBuffer renderPath = new StringBuffer();

    // Action window definition: portalURL.setActionWindow().
    String portletAction = request.getParameter("portletAction");
    if (portletAction != null && portletAction.startsWith(PREFIX + ACTION)) {
      portalURL.setActionWindow(decodeControlParameter(portletAction)[0]);
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
        portalURL.addParameter(decodeParameter(parameter, value));
      }
    }

    if (renderPath.length() > 0) {
      portalURL.setRenderPath(renderPath.toString());
    }

    // Return the portal URL.
    return portalURL;
  }

  private void appendParameter(StringBuffer url, String parameter) {
    if (url.length() == 0 || !url.toString().contains("?")) {
      url.append("?");
    } else {
      url.append("&");
    }
    url.append(parameter);
  }

  /**
   * Converts a portal URL to a URL string.
   *
   * @param portalURL the portal URL to convert.
   * @return a URL string representing the portal URL.
   */
  public String toString(PortalURL portalURL) {

    StringBuffer buffer = new StringBuffer();

    // Append the server URI and the servlet path.
    buffer.append(portalURL.getServletPath().startsWith("/") ? "" : "/")
        .append(portalURL.getServletPath());

    // Append the action window definition, if it exists.
    if (portalURL.getActionWindow() != null) {
      appendParameter(buffer, "portletAction=" + PREFIX + ACTION + encodeCharacters(portalURL.getActionWindow()));
    }

    // Append portlet mode definitions.
    for (Iterator it = portalURL.getPortletModes().entrySet().iterator();
         it.hasNext();) {
      Map.Entry entry = (Map.Entry) it.next();
      // If the portlet mode is "view" then no need to show the portlet mode in the url because that is the default
      if (!"view".equals(entry.getValue().toString())) {
        appendParameter(buffer, "portletMode=" + encodeControlParameter(PORTLET_MODE, entry.getKey().toString(),
            entry.getValue().toString()));
      }
    }

    // Append window state definitions.
    int windowStateCount = 0;
    for (Iterator it = portalURL.getWindowStates().entrySet().iterator();
         it.hasNext();) {
      ++windowStateCount;
      Map.Entry entry = (Map.Entry) it.next();
      appendParameter(buffer, "portletWindowState" + windowStateCount + "=" +
          encodeControlParameter(WINDOW_STATE, entry.getKey().toString(),
              entry.getValue().toString()));
    }

    // Append action and render parameters.
    for (Iterator it = portalURL.getParameters().iterator();
         it.hasNext();) {

      PortalURLParameter param = (PortalURLParameter) it.next();

      // Encode action params in the query appended at the end of the URL.
      if (portalURL.getActionWindow() != null
          && portalURL.getActionWindow().equals(param.getWindowId())) {
        for (int i = 0; i < param.getValues().length; i++) {
          appendParameter(buffer, param.getName() + "=" + param.getValues()[i]);
        }
      }

      // Encode render params as a part of the URL.
      else if (param.getValues() != null
          && param.getValues().length > 0) {
        String valueString = encodeMultiValues(param.getValues());
        if (valueString.length() > 0) {
          appendParameter(buffer, encodeControlParameter(RENDER_PARAM, param.getWindowId(),
              param.getName()) + "=" + valueString);
        }
      }
    }

    // Construct the string representing the portal URL.
    return buffer.toString();
  }

  private String encodeQueryParam(String param) {
    try {
      return URLEncoder.encode(param, "UTF-8");
    }
    catch (UnsupportedEncodingException e) {
      // If this happens, we've got bigger problems.
      throw new RuntimeException(e);
    }
  }

  // Private Encoding/Decoding Methods ---------------------------------------

  /**
   * Encode a control parameter.
   *
   * @param type     the type of the control parameter, which may be:
   *                 portlet mode, window state, or render parameter.
   * @param windowId the portlet window ID.
   * @param name     the name to encode.
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

    if (LOG.isDebugEnabled()) {
      LOG.debug("Decoding parameter: name=" + name
          + ", value=" + value);
    }

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

    // Construct portal URL parameter and return.
    return new PortalURLParameter(windowId, paramName, paramValues);
  }

  /**
   * Decode special characters contained in the string value.
   *
   * @param string the string value to decode.
   * @return the decoded string.
   */
  private String decodeCharacters(String string) {
    for (int i = 0; i < ENCODINGS.length; i++) {
      string = StringUtils.replace(string,
          ENCODINGS[i][1],
          ENCODINGS[i][0]);
    }
    return string;
  }

}