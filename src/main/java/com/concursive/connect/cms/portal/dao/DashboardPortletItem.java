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

import java.sql.*;

/**
 * Represents a portlet in the master library
 *
 * @author matt rajkowski
 * @version $Id$
 * @created January 20, 2008
 */
public class DashboardPortletItem extends GenericBean {
  private int id = -1;
  private String name = null;
  private String description = null;
  private boolean portalEnabled = false;
  private boolean projectEnabled = false;
  private boolean adminEnabled = false;
  private boolean enabled = false;
  private Timestamp entered = null;
  private Timestamp modified = null;

  public DashboardPortletItem() {
  }

  public DashboardPortletItem(Connection db, int id) throws SQLException {
    queryRecord(db, id);
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public boolean getPortalEnabled() {
    return portalEnabled;
  }

  public void setPortalEnabled(boolean portalEnabled) {
    this.portalEnabled = portalEnabled;
  }

  public boolean getProjectEnabled() {
    return projectEnabled;
  }

  public void setProjectEnabled(boolean projectEnabled) {
    this.projectEnabled = projectEnabled;
  }

  public boolean getAdminEnabled() {
    return adminEnabled;
  }

  public void setAdminEnabled(boolean adminEnabled) {
    this.adminEnabled = adminEnabled;
  }

  public boolean getEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
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

  public void queryRecord(Connection db, int portletId) throws SQLException {
    StringBuffer sql = new StringBuffer();
    sql.append(
        "SELECT * " +
            "FROM lookup_project_portlet " +
            "WHERE portlet_id = ? ");
    PreparedStatement pst = db.prepareStatement(sql.toString());
    int i = 0;
    pst.setInt(++i, portletId);
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      buildRecord(rs);
    }
    rs.close();
    pst.close();
    if (id == -1) {
      throw new SQLException("Portlet record not found.");
    }
  }

  protected void buildRecord(ResultSet rs) throws SQLException {
    id = rs.getInt("portlet_id");
    name = rs.getString("portlet_name");
    description = rs.getString("portlet_description");
    portalEnabled = rs.getBoolean("portal_enabled");
    projectEnabled = rs.getBoolean("project_enabled");
    adminEnabled = rs.getBoolean("admin_enabled");
    enabled = rs.getBoolean("enabled");
    entered = rs.getTimestamp("entered");
    modified = rs.getTimestamp("modified");
  }

  public void insert(Connection db) throws SQLException {
    // Insert the page
    PreparedStatement pst = db.prepareStatement(
        "INSERT INTO lookup_project_portlet " +
            "(portlet_name, portlet_description, enabled, portal_enabled, project_enabled, admin_enabled " +
            (entered != null ? ", entered " : "") +
            (modified != null ? ", modified " : "") +
            ") VALUES (?, ?, ?, ?, ?, ?" +
            (entered != null ? ", ? " : "") +
            (modified != null ? ", ? " : "") +
            ")"
    );
    int i = 0;
    pst.setString(++i, name);
    pst.setString(++i, description);
    pst.setBoolean(++i, enabled);
    pst.setBoolean(++i, portalEnabled);
    pst.setBoolean(++i, projectEnabled);
    pst.setBoolean(++i, adminEnabled);
    if (entered != null) {
      pst.setTimestamp(++i, entered);
    }
    if (modified != null) {
      pst.setTimestamp(++i, modified);
    }
    pst.execute();
    pst.close();
    id = DatabaseUtils.getCurrVal(db, "lookup_project_portlet_portlet_id_seq", -1);
  }

  public void update(Connection db) throws SQLException {
    if (id == -1) {
      throw new SQLException("ID was not specified");
    }
    PreparedStatement pst = db.prepareStatement(
        "UPDATE lookup_project_portlet " +
            "SET portlet_name = ?, portlet_description = ?, enabled = ?, " +
            "portal_enabled = ?, project_enabled = ?, admin_enabled = ?," +
            "modified = CURRENT_TIMESTAMP " +
            "WHERE portlet_id = ? "
    );
    int i = 0;
    pst.setString(++i, name);
    pst.setString(++i, description);
    pst.setBoolean(++i, enabled);
    pst.setBoolean(++i, portalEnabled);
    pst.setBoolean(++i, projectEnabled);
    pst.setBoolean(++i, adminEnabled);
    pst.setInt(++i, id);
    pst.execute();
    pst.close();
  }

}