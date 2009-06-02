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

import com.concursive.commons.objects.ObjectUtils;
import com.concursive.commons.text.StringUtils;
import com.concursive.commons.text.Template;
import com.concursive.commons.xml.XMLUtils;
import com.concursive.connect.cms.portal.dao.DashboardPortlet;
import com.concursive.connect.cms.portal.dao.DashboardPortletPrefs;
import com.concursive.connect.cms.portal.dao.DashboardPortletPrefsList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pluto.PortletContainerException;
import org.apache.pluto.PortletWindow;
import org.apache.pluto.internal.InternalPortletPreference;
import org.apache.pluto.internal.impl.PortletPreferenceImpl;
import org.apache.pluto.spi.optional.PortletPreferencesService;

import javax.portlet.PortletRequest;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Manages loading, saving, and caching preferences for portlets
 *
 * @author matt rajkowski
 * @version $Id$
 * @created Feb 8, 2007
 */
public class PortletPreferencesServiceImpl implements PortletPreferencesService {

  private static Log LOG = LogFactory.getLog(PortletPreferencesServiceImpl.class);

  private Map<String, InternalPortletPreference[]> storage = new HashMap<String, InternalPortletPreference[]>();

  public PortletPreferencesServiceImpl() {
    LOG.info("Constructor");
  }

  public InternalPortletPreference[] getStoredPreferences(PortletWindow portletWindow, PortletRequest request) throws PortletContainerException {
    String key = getFormattedKey(portletWindow);
    if (key.startsWith("T")) {
      // This is a temporary portlet so use the portlet defaults
      try {
        DashboardPortlet portlet = (DashboardPortlet) request.getAttribute("dashboardPortlet");
        LOG.debug("read-------------------------------------");
        LOG.debug("portlet page name: " + portlet.getPageName());
        LOG.debug("portlet key: " + portlet.getWindowConfigId());
        LOG.debug("portlet formatted key: " + key);
        LOG.debug("-----------------------------------------");

        // Internal Store for temporary prefs
        InternalPortletPreference[] preferences = storage.get(portlet.getPageName() + key);
        if (preferences == null) {
          preferences = new InternalPortletPreference[0];
        }

        // Default Preferences
        InternalPortletPreference[] prefs = new InternalPortletPreference[portlet.getDefaultPreferences().size() + preferences.length];
        int count = -1;
        for (DashboardPortletPrefs thisProperty : portlet.getDefaultPreferences().values()) {
          String name = thisProperty.getName();
          // Exclude sending portal preferences
          if ("portlet-events".equals(name) ||
              "portlet-rules".equals(name)) {
            continue;
          }
          String[] values = thisProperty.getValues();
          for (int i = 0; i < values.length; i++) {
            String value = values[i];
            // check for variables
            if (value.contains("${")) {
              Template template = new Template();
              template.setText(value);
              ArrayList templateVariables = template.getVariables();
              // replace the variables with the appropriate object's value
              for (Object o : templateVariables) {
                String templateVariable = (String) o;
                String[] variable = templateVariable.split("\\.");
                Object object = null;
                if ("project".equals(variable[0])) {
                  object = PortalUtils.getProject(request);
                } else if ("user".equals(variable[0])) {
                  object = PortalUtils.getUser(request);
                } else if ("system".equals(variable[0])) {
                  object = PortalUtils.getApplicationPrefs(request);
                } else {
                  object = request.getAttribute(variable[0]);
                }
                if (object != null) {
                  StringBuffer thisVariable = new StringBuffer();
                  String encodeMethod = null;
                  for (int splitCount = 1; splitCount < variable.length; splitCount++) {
                    if (thisVariable.length() > 0) {
                      thisVariable.append(".");
                    }
                    // Add the variable and determine if encoded
                    if (variable[splitCount].contains(":")) {
                      String[] encoded = variable[splitCount].split("[:]");
                      thisVariable.append(encoded[0]);
                      encodeMethod = encoded[1];
                    } else if (variable[splitCount].contains("?")) {
                      String[] encoded = variable[splitCount].split("[?]");
                      thisVariable.append(encoded[0]);
                      encodeMethod = encoded[1];
                    } else {
                      thisVariable.append(variable[splitCount]);
                    }
                  }
                  // Retrieve the object's value
                  String newValue = null;
                  if (variable.length == 1) {
                    newValue = (String) object;
                  } else if ("system".equals(variable[0])) {
                    Method method = object.getClass().getMethod("get", new Class[]{String.class});
                    newValue = (String) method.invoke(object, new Object[]{thisVariable.toString()});
                  } else {
                    newValue = ObjectUtils.getParam(object, thisVariable.toString());
                  }
                  if (encodeMethod == null) {
                    // Output as-is
                    template.addParseElement("${" + templateVariable + "}", newValue);
                  } else if ("xml".equals(encodeMethod)) {
                    template.addParseElement("${" + templateVariable + "}", XMLUtils.toXMLValue(newValue));
                  } else {
                    // Default encoding to html
                    template.addParseElement("${" + templateVariable + "}", StringUtils.toHtml(newValue));
                  }
                  values[i] = template.getParsedText();
                }
              }
            }
          }
          ++count;
          InternalPortletPreference thisPref = new PortletPreferenceImpl(name, values);
          prefs[count] = thisPref;
        }

        // Merge the default prefs with any stored prefs
        for (InternalPortletPreference thisPref : preferences) {
          LOG.debug("Temporary pref: " + thisPref.getName() + " - " + thisPref.getValues());
          ++count;
          prefs[count] = thisPref;
        }
        return prefs;
      } catch (Exception se) {
        throw new PortletContainerException("PortletPreferencesServiceImpl-> Error: " + se.getMessage());
      }
    } else {
      InternalPortletPreference[] preferences = storage.get(key);
      if (preferences != null) {
        return clonePreferences(preferences);
      }
      Connection db = (Connection) request.getAttribute("connection");
      if (db != null) {
        try {
          DashboardPortletPrefsList propertyMap = new DashboardPortletPrefsList();
          propertyMap.setPortletId(key);
          propertyMap.buildList(db);
          InternalPortletPreference[] prefs = new InternalPortletPreference[propertyMap.size()];
          Iterator i = propertyMap.iterator();
          int count = -1;
          while (i.hasNext()) {
            DashboardPortletPrefs thisProperty = (DashboardPortletPrefs) i.next();
            String name = thisProperty.getName();
            String[] values = thisProperty.getValues();
            ++count;
            InternalPortletPreference thisPref = new PortletPreferenceImpl(name, values);
            prefs[count] = thisPref;
          }
          storage.put(key, prefs);
          return prefs;
        } catch (Exception se) {
          throw new PortletContainerException("PortletPreferencesServiceImpl-> Error: " + se.getMessage());
        }
      }
    }
    if (System.getProperty("DEBUG") != null) {
      LOG.warn("Returning empty prefs because database connection is null");
    }
    return new InternalPortletPreference[0];
  }

  public void store(PortletWindow portletWindow, PortletRequest request, InternalPortletPreference[] preferences) throws PortletContainerException {
    String key = getFormattedKey(portletWindow);
    if (key.startsWith("T")) {
      LOG.debug("Storing temporary portlet prefs");
      DashboardPortlet portlet = (DashboardPortlet) request.getAttribute("dashboardPortlet");
      LOG.debug("write------------------------------------");
      LOG.debug("portlet page name: " + portlet.getPageName());
      LOG.debug("portlet key: " + portlet.getWindowConfigId());
      LOG.debug("portlet formatted key: " + key);
      LOG.debug("-----------------------------------------");
      // Store them in an array for quick retrieval
      storage.put(key, clonePreferences(preferences));
    } else {
      // Store them in an array for quick retrieval
      storage.put(key, clonePreferences(preferences));
      // Store the preferences into the database -- container does not specify what changed
      // so delete them all
      int portletId = Integer.parseInt(key);
      Connection db = (Connection) request.getAttribute("connection");
      try {
        db.setAutoCommit(false);
        DashboardPortletPrefsList.delete(db, portletId);
        for (InternalPortletPreference thisPref : preferences) {
          DashboardPortletPrefs portletPrefs = new DashboardPortletPrefs();
          portletPrefs.setPortletId(portletId);
          portletPrefs.setName(thisPref.getName());
          portletPrefs.setValues(thisPref.getValues());
          portletPrefs.insert(db);
        }
        db.commit();
      } catch (Exception e) {
        try {
          db.rollback();
          e.printStackTrace(System.out);
          LOG.error("Preferences", e);
        } catch (Exception e2) {

        }
      } finally {
        try {
          db.setAutoCommit(true);
        } catch (Exception e2) {

        }
      }
    }
  }

  private String getFormattedKey(PortletWindow portletWindow) {
    // TODO: Make user and project prefs
    //request.getAttribute("dashboardPage");
    // All users share the same prefs...

    // Pluto uses: contextPath + "." + portletName + "!" + metaInfo;
    String uniqueKey = portletWindow.getId().getStringId();
    return (uniqueKey.substring(uniqueKey.lastIndexOf("!") + 1));
  }

  private InternalPortletPreference[] clonePreferences(InternalPortletPreference[] preferences) {
    if (preferences == null) {
      return null;
    }
    InternalPortletPreference[] copy =
        new InternalPortletPreference[preferences.length];
    for (int i = 0; i < preferences.length; i++) {
      if (preferences[i] != null) {
        copy[i] = (InternalPortletPreference) preferences[i].clone();
      } else {
        copy[i] = null;
      }
    }
    return copy;
  }
}
