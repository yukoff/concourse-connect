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
package com.concursive.connect.cms.portal.dao;

import com.concursive.commons.db.DatabaseUtils;
import com.concursive.commons.web.mvc.beans.GenericBean;
import com.concursive.connect.Constants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Represents a portlet attached to a page
 *
 * @author matt rajkowski
 * @version $Id$
 * @created Feb 18, 2007
 */
public class DashboardPortlet extends GenericBean {

  private static Log LOG = LogFactory.getLog(DashboardPortlet.class);
  // Local properties
  private int id = -1;
  private int pageId = -1;
  private int portletId = -1;
  private Timestamp entered = null;
  private Timestamp modified = null;
  // If loaded from the database
  private boolean loaded = false;
  // External properties
  private String pageName = null;
  private String name = null;
  private String htmlClass = null;
  private String viewer = null;
  private int cacheTime = 0;
  private int timeout = 0;
  private int sensitive = Constants.UNDEFINED;
  // Portal properties
  private String windowConfigId = null;
  // Preference defaults
  private HashMap<String, DashboardPortletPrefs> defaultPreferences = new HashMap<String, DashboardPortletPrefs>();
  // Events
  private ArrayList<String> generateDataEvents = new ArrayList<String>();
  private ArrayList<String> consumeDataEvents = new ArrayList<String>();
  private ArrayList<String> generateSessionData = new ArrayList<String>();
  private ArrayList<String> consumeSessionData = new ArrayList<String>();
  private ArrayList<String> generateRequestData = new ArrayList<String>();
  private ArrayList<String> consumeRequestData = new ArrayList<String>();

  public DashboardPortlet() {
  }

  public DashboardPortlet(ResultSet rs) throws SQLException {
    buildRecord(rs);
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getPageId() {
    return pageId;
  }

  public void setPageId(int pageId) {
    this.pageId = pageId;
  }

  public int getPortletId() {
    return portletId;
  }

  public void setPortletId(int portletId) {
    this.portletId = portletId;
  }

  public Timestamp getEntered() {
    return entered;
  }

  public void setEntered(Timestamp entered) {
    this.entered = entered;
  }

  public Timestamp getModified() {
    return modified;
  }

  public void setModified(Timestamp modified) {
    this.modified = modified;
  }

  public String getPageName() {
    return pageName;
  }

  public void setPageName(String pageName) {
    this.pageName = pageName;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getHtmlClass() {
    return htmlClass;
  }

  public void setHtmlClass(String htmlClass) {
    this.htmlClass = htmlClass;
  }

  public String getViewer() {
    return viewer;
  }

  public void setViewer(String viewer) {
    LOG.debug("viewer set: " + viewer);
    this.viewer = viewer;
  }

  public int getCacheTime() {
    return cacheTime;
  }

  public void setCacheTime(int cacheTime) {
    this.cacheTime = cacheTime;
  }

  public void setCacheTime(String cacheTime) {
    this.cacheTime = Integer.parseInt(cacheTime);
  }

  public boolean isCached() {
    return (cacheTime > 0);
  }

  public int getTimeout() {
    return timeout;
  }

  public void setTimeout(int timeout) {
    this.timeout = timeout;
  }

  public void setTimeout(String timeout) {
    this.timeout = Integer.parseInt(timeout);
  }

  public int getSensitive() {
    return sensitive;
  }

  public boolean isSensitive() {
    return (sensitive == Constants.TRUE);
  }

  public void setSensitive(int sensitive) {
    this.sensitive = sensitive;
  }

  public void setSensitive(String tmp) {
    this.sensitive = DatabaseUtils.parseBooleanToConstant(tmp);
  }

  public boolean getLoaded() {
    return loaded;
  }

  public void setLoaded(boolean loaded) {
    this.loaded = loaded;
  }

  public String getWindowConfigId() {
    return windowConfigId;
  }

  public void setWindowConfigId(String windowConfigId) {
    this.windowConfigId = windowConfigId;
  }

  public void addDefaultPreference(String name, DashboardPortletPrefs prefs) {
    defaultPreferences.put(name, prefs);
  }

  public HashMap<String, DashboardPortletPrefs> getDefaultPreferences() {
    return defaultPreferences;
  }

  public ArrayList<String> getGenerateDataEvents() {
    return generateDataEvents;
  }

  public void setGenerateDataEvents(ArrayList<String> generateDataEvents) {
    this.generateDataEvents = generateDataEvents;
  }

  public void addGenerateDataEvent(String tmp) {
    generateDataEvents.add(tmp);
  }

  public ArrayList<String> getConsumeDataEvents() {
    return consumeDataEvents;
  }

  public void setConsumeDataEvents(ArrayList<String> consumeDataEvents) {
    this.consumeDataEvents = consumeDataEvents;
  }

  public void addConsumeDataEvent(String tmp) {
    consumeDataEvents.add(tmp);
  }

  public ArrayList<String> getGenerateSessionData() {
    return generateSessionData;
  }

  public void setGenerateSessionData(ArrayList<String> generateSessionData) {
    this.generateSessionData = generateSessionData;
  }

  public void addGenerateSessionData(String tmp) {
    generateSessionData.add(tmp);
  }

  public ArrayList<String> getConsumeSessionData() {
    return consumeSessionData;
  }

  public void setConsumeSessionData(ArrayList<String> consumeSessionData) {
    this.consumeSessionData = consumeSessionData;
  }

  public void addConsumeSessionDataEvent(String tmp) {
    consumeSessionData.add(tmp);
  }

  public ArrayList<String> getGenerateRequestData() {
    return generateRequestData;
  }

  public void setGenerateRequestData(ArrayList<String> generateRequestData) {
    this.generateRequestData = generateRequestData;
  }

  public void addGenerateRequestData(String tmp) {
    generateRequestData.add(tmp);
  }

  public ArrayList<String> getConsumeRequestData() {
    return consumeRequestData;
  }

  public void setConsumeRequestData(ArrayList<String> consumeRequestData) {
    this.consumeRequestData = consumeRequestData;
  }

  public void addConsumeRequestDataEvent(String tmp) {
    consumeRequestData.add(tmp);
  }

  private void buildRecord(ResultSet rs) throws SQLException {
    id = rs.getInt("page_portlet_id");
    pageId = rs.getInt("page_id");
    portletId = rs.getInt("portlet_id");
    // lookup_project_portlet
    name = rs.getString("portlet_name");
    loaded = true;
  }

  public void insert(Connection db) throws SQLException {
    // Insert the page
    PreparedStatement pst = db.prepareStatement(
        "INSERT INTO project_dashboard_portlet " +
        "(page_id, portlet_id " +
        (entered != null ? ", entered " : "") +
        (modified != null ? ", modified " : "") +
        ") VALUES (?, ?" +
        (entered != null ? ", ? " : "") +
        (modified != null ? ", ? " : "") +
        ")");
    int i = 0;
    pst.setInt(++i, pageId);
    pst.setInt(++i, portletId);
    if (entered != null) {
      pst.setTimestamp(++i, entered);
    }
    if (modified != null) {
      pst.setTimestamp(++i, modified);
    }
    pst.execute();
    pst.close();
    id = DatabaseUtils.getCurrVal(db, "project_dashboard_portlet_page_portlet_id_seq", -1);
  }
}
