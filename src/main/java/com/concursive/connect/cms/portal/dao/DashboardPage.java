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
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;

import java.sql.*;

/**
 * Represents the page characteristics of a dashboard
 *
 * @author matt rajkowski
 * @version $Id$
 * @created Feb 18, 2007
 */
public class DashboardPage extends GenericBean {

  // Main properties
  private int id = -1;
  private int dashboardId = -1;
  private String name = null;
  private int level = -1;
  private String xmlDesign = null;
  private String title = null;
  private String description = null;
  private String keywords = null;
  private String category = null;
  private boolean enabled = false;
  private Timestamp entered = null;
  private Timestamp modified = null;
  private int enteredBy = -1;
  private int projectId = -1;
  private String permission = null;

  // TODO: Helper properties (to be moved into a context)
  private DashboardPortletList portletList = new DashboardPortletList();
  private String objectType = null;

  public DashboardPage() {
  }

  public DashboardPage(Connection db, int thisId) throws SQLException {
    queryRecord(db, thisId);
  }

  public DashboardPage(ResultSet rs) throws SQLException {
    buildRecord(rs);
  }

  public DashboardPage(DashboardTemplate template) throws Exception {
    this.setName(template.getName());
    this.setTitle(template.getTitle());
    this.setDescription(template.getDescription());
    this.setKeywords(template.getKeywords());
    this.setCategory(template.getCategory());
    this.setXmlDesign(template.getXmlDesign());
    this.setPermission(template.getPermission());
    this.getPortletList().buildTemporaryList(this);
    this.setObjectType(template.getObjectType());
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getDashboardId() {
    return dashboardId;
  }

  public void setDashboardId(int dashboardId) {
    this.dashboardId = dashboardId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getLevel() {
    return level;
  }

  public void setLevel(int level) {
    this.level = level;
  }

  public String getXmlDesign() {
    return xmlDesign;
  }

  public void setXmlDesign(String xmlDesign) {
    this.xmlDesign = xmlDesign;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getKeywords() {
    return keywords;
  }

  public void setKeywords(String keywords) {
    this.keywords = keywords;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public boolean isEnabled() {
    return enabled;
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

  public int getEnteredBy() {
    return enteredBy;
  }

  public void setEnteredBy(int enteredBy) {
    this.enteredBy = enteredBy;
  }

  public DashboardPortletList getPortletList() {
    return portletList;
  }

  public void setPortletList(DashboardPortletList portletList) {
    this.portletList = portletList;
  }

  public Project getProject() {
    return ProjectUtils.loadProject(projectId);
  }

  public int getProjectId() {
    return projectId;
  }

  public void setProjectId(int projectId) {
    this.projectId = projectId;
  }

  public String getPermission() {
    return permission;
  }

  public void setPermission(String permission) {
    this.permission = permission;
  }

  public String getObjectType() {
    return objectType;
  }

  public void setObjectType(String objectType) {
    this.objectType = objectType;
  }

  private void queryRecord(Connection db, int thisId) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "SELECT page_id, page_name, page_level, page_design, " +
            "enabled, dashboard_id, entered, modified " +
            "FROM project_dashboard_page " +
            "WHERE page_id = ? ");
    pst.setInt(1, thisId);
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      buildRecord(rs);
    }
    rs.close();
    pst.close();
  }

  private void buildRecord(ResultSet rs) throws SQLException {
    id = rs.getInt("page_id");
    name = rs.getString("page_name");
    level = rs.getInt("page_level");
    xmlDesign = rs.getString("page_design");
    enabled = rs.getBoolean("enabled");
    dashboardId = rs.getInt("dashboard_id");
    entered = rs.getTimestamp("entered");
    modified = rs.getTimestamp("modified");
  }

  public void insert(Connection db) throws Exception {
    // Insert the page
    PreparedStatement pst = db.prepareStatement(
        "INSERT INTO project_dashboard_page " +
            "(dashboard_id, page_name, page_design, enabled " +
            (entered != null ? ", entered " : "") +
            (modified != null ? ", modified " : "") +
            ") VALUES (?, ?, ?, ?" +
            (entered != null ? ", ? " : "") +
            (modified != null ? ", ? " : "") +
            ")"
    );
    int i = 0;
    pst.setInt(++i, dashboardId);
    pst.setString(++i, name);
    pst.setString(++i, xmlDesign);
    pst.setBoolean(++i, enabled);
    if (entered != null) {
      pst.setTimestamp(++i, entered);
    }
    if (modified != null) {
      pst.setTimestamp(++i, modified);
    }
    pst.execute();
    pst.close();
    id = DatabaseUtils.getCurrVal(db, "project_dashboard_page_page_id_seq", -1);
  }

  public void delete(Connection db) throws SQLException {
    // Delete related portlet preferences
    PreparedStatement pst = db.prepareStatement(
        "DELETE FROM project_dashboard_portlet_prefs WHERE page_portlet_id IN (SELECT page_portlet_id FROM project_dashboard_portlet WHERE page_id = ?)"
    );
    pst.setInt(1, id);
    pst.execute();
    pst.close();
    // Delete related portlet
    pst = db.prepareStatement(
        "DELETE FROM project_dashboard_portlet WHERE page_id = ?"
    );
    pst.setInt(1, id);
    pst.execute();
    pst.close();
    // Now delete the page
    pst = db.prepareStatement(
        "DELETE FROM project_dashboard_page WHERE page_id = ?"
    );
    pst.setInt(1, id);
    pst.execute();
    pst.close();
  }

  public static int queryIdFromDashboardId(Connection db, int dashboardId) throws SQLException {
    int id = -1;
    PreparedStatement pst = db.prepareStatement(
        "SELECT page_id FROM project_dashboard_page WHERE dashboard_id = ?");
    pst.setInt(1, dashboardId);
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      id = rs.getInt(1);
    }
    rs.close();
    pst.close();
    return id;
  }
}
