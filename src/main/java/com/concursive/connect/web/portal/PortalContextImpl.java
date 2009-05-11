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

import javax.portlet.PortletMode;
import javax.portlet.WindowState;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Vector;

/**
 * JSR168 Portal Context
 *
 * @author matt rajkowski
 * @version $Id$
 * @created Feb 8, 2007
 */
public class PortalContextImpl extends HashMap implements javax.portlet.PortalContext {

  private String info = "Concursive Portal";
  private HashMap properties = new HashMap();
  private Vector portletModes = null;
  private Vector windowStates = null;

  public String getProperty(String name) {
    if (name == null) {
      throw new IllegalArgumentException("Property name == null");
    }
    return (String) properties.get(name);
  }

  public Enumeration getPropertyNames() {
    Vector names = new Vector(properties.keySet());
    return names.elements();
  }

  public Enumeration getSupportedPortletModes() {
    if (portletModes == null) {
      portletModes = new Vector();
      portletModes.add(new PortletMode("view"));
      portletModes.add(new PortletMode("edit"));
      portletModes.add(new PortletMode("help"));
    }
    return portletModes.elements();
  }

  public Enumeration getSupportedWindowStates() {
    if (windowStates == null) {
      windowStates = new Vector();
      windowStates.add(new WindowState("normal"));
      windowStates.add(new WindowState("maximized"));
      windowStates.add(new WindowState("minimized"));
    }
    return windowStates.elements();
  }

  public String getPortalInfo() {
    return info;
  }

  public void setProperty(String name, String value) {
    if (name == null) {
      throw new IllegalArgumentException("Property name == null");
    }
    properties.put(name, value);
  }
}
