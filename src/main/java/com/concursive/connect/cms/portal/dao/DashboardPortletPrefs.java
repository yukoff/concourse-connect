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
import com.concursive.commons.xml.XMLUtils;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * The preferences for a specific instance of a portlet
 *
 * @author matt rajkowski
 * @version $Id$
 * @created Feb 18, 2007
 */
public class DashboardPortletPrefs extends GenericBean {
  private int id = -1;
  private int portletId = -1;
  private String name = null;
  private String value = null;
  private Timestamp entered = null;
  private Timestamp modified = null;

  public DashboardPortletPrefs() {
  }

  public DashboardPortletPrefs(String name, String value) throws Exception {
    String[] values = new String[1];
    values[0] = value;
    this.setName(name);
    this.setValues(values);
  }

  public DashboardPortletPrefs(String name, String[] values) throws Exception {
    this.setName(name);
    this.setValues(values);
  }

  public DashboardPortletPrefs(ResultSet rs) throws SQLException {
    buildRecord(rs);
  }

  public DashboardPortletPrefs(Connection db, int preferenceId) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "SELECT * " +
            "FROM project_dashboard_portlet_prefs " +
            "WHERE preference_id = ? "
    );
    pst.setInt(1, preferenceId);
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      buildRecord(rs);
    }
    rs.close();
    pst.close();
    if (id == -1) {
      throw new SQLException("Preference record not found.");
    }
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getPortletId() {
    return portletId;
  }

  public void setPortletId(int portletId) {
    this.portletId = portletId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public void setValues(String[] values) throws Exception {
    // Serialize for storage
    Document document = XMLUtils.createDocument("values");
    for (String value1 : values) {
      Element tabElement = document.createElement("value");
      if (value1 != null) {
        CDATASection cdata = document.createCDATASection(value1);
        tabElement.appendChild(cdata);
      }
      document.getDocumentElement().appendChild(tabElement);
    }
    value = XMLUtils.toString(document);
  }

  public String[] getValues() throws Exception {
    // De-serialize for the preferences API
    XMLUtils xml = new XMLUtils(value);
    List<Element> arrayList = XMLUtils.getElements(xml.getDocumentElement(), "value");
    List<String> valueList = new ArrayList<String>();
    Iterator i = (arrayList.iterator());
    while (i.hasNext()) {
      String text = XMLUtils.getNodeText((Element) i.next());
      valueList.add(text);
    }
    return valueList.toArray(new String[valueList.size()]);
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

  public void insert(Connection db) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "INSERT INTO project_dashboard_portlet_prefs " +
            "(page_portlet_id, property_name, property_value " +
            (entered != null ? ", entered " : "") +
            (modified != null ? ", modified " : "") +
            ") VALUES (?, ?, ?" +
            (entered != null ? ", ? " : "") +
            (modified != null ? ", ? " : "") +
            ")"
    );
    int i = 0;
    pst.setInt(++i, portletId);
    pst.setString(++i, name);
    pst.setString(++i, value);
    if (entered != null) {
      pst.setTimestamp(++i, entered);
    }
    if (modified != null) {
      pst.setTimestamp(++i, modified);
    }
    pst.execute();
    pst.close();
    id = DatabaseUtils.getCurrVal(db, "project_dashboard_portlet_prefs_preference_id_seq", -1);
  }

  protected void buildRecord(ResultSet rs) throws SQLException {
    id = rs.getInt("preference_id");
    portletId = rs.getInt("page_portlet_id");
    name = rs.getString("property_name");
    value = rs.getString("property_value");
    entered = rs.getTimestamp("entered");
    modified = rs.getTimestamp("modified");
  }
}
