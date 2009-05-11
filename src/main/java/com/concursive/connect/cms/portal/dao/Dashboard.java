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
 * Represents where the dashboard tab is visible
 *
 * @author matt rajkowski
 * @version $Id$
 * @created Feb 18, 2007
 */
public class Dashboard extends GenericBean {
  private int id = -1;
  private String name = null;
  private int level = -1;
  private int projectId = -1;
  private boolean portal = false;
  private boolean enabled = false;
  private Timestamp entered = null;
  private Timestamp modified = null;

  public Dashboard() {
  }

  public Dashboard(ResultSet rs) throws SQLException {
    buildRecord(rs);
  }

  public Dashboard(Connection db, int thisId) throws SQLException {
    queryRecord(db, thisId);
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public void setId(String id) {
    this.id = Integer.parseInt(id);
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

  public void setLevel(String tmp) {
    level = Integer.parseInt(tmp);
  }

  public int getProjectId() {
    return projectId;
  }

  public void setProjectId(int projectId) {
    this.projectId = projectId;
  }

  public boolean getPortal() {
    return portal;
  }

  public void setPortal(boolean portal) {
    this.portal = portal;
  }

  public boolean getEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public void setEnabled(String tmp) {
    enabled = DatabaseUtils.parseBoolean(tmp);
  }

  public Timestamp getEntered() {
    return entered;
  }

  public void setEntered(Timestamp entered) {
    this.entered = entered;
  }

  public void setEntered(String tmp) {
    this.entered = DatabaseUtils.parseTimestamp(tmp);
  }

  public Timestamp getModified() {
    return modified;
  }

  public void setModified(Timestamp modified) {
    this.modified = modified;
  }

  public void setModified(String tmp) {
    this.modified = DatabaseUtils.parseTimestamp(tmp);
  }

  private void queryRecord(Connection db, int dashboardId) throws SQLException {
    StringBuffer sql = new StringBuffer();
    sql.append(
        "SELECT * " +
            "FROM project_dashboard d " +
            "WHERE dashboard_id = ? ");
    PreparedStatement pst = db.prepareStatement(sql.toString());
    int i = 0;
    pst.setInt(++i, dashboardId);
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      buildRecord(rs);
    }
    rs.close();
    pst.close();
  }

  private void buildRecord(ResultSet rs) throws SQLException {
    id = rs.getInt("dashboard_id");
    name = rs.getString("dashboard_name");
    level = rs.getInt("dashboard_level");
    projectId = rs.getInt("project_id");
    portal = rs.getBoolean("portal");
    enabled = rs.getBoolean("enabled");
    entered = rs.getTimestamp("entered");
    modified = rs.getTimestamp("modified");
  }

  private boolean isValid() {
    if (name.equals("")) {
      errors.put("nameError", "Name is required");
    }
    return !hasErrors();
  }

  public boolean insert(Connection db) throws SQLException {
    if (!isValid()) {
      return false;
    }
    boolean commit = db.getAutoCommit();
    try {
      if (commit) {
        db.setAutoCommit(false);
      }
      // The dashboard will be appended to the end
      if (level == -1) {
        level = (DashboardList.queryMaxLevel(db, projectId) + 1);
      }
      // Insert the record
      PreparedStatement pst = db.prepareStatement(
          "INSERT INTO project_dashboard " +
              "(dashboard_name, dashboard_level, project_id, portal, enabled " +
              (entered != null ? ", entered " : "") +
              (modified != null ? ", modified " : "") +
              ") VALUES (?, ?, ?, ?, ?" +
              (entered != null ? ", ? " : "") +
              (modified != null ? ", ? " : "") +
              ")"
      );
      int i = 0;
      pst.setString(++i, name);
      pst.setInt(++i, level);
      DatabaseUtils.setInt(pst, ++i, projectId);
      pst.setBoolean(++i, portal);
      pst.setBoolean(++i, enabled);
      if (entered != null) {
        pst.setTimestamp(++i, entered);
      }
      if (modified != null) {
        pst.setTimestamp(++i, modified);
      }
      pst.execute();
      pst.close();
      id = DatabaseUtils.getCurrVal(db, "project_dashboard_dashboard_id_seq", -1);
      if (commit) {
        db.commit();
      }
      return true;
    } catch (Exception e) {
      if (commit) {
        db.rollback();
      }
      throw new SQLException(e.getMessage());
    } finally {
      if (commit) {
        db.setAutoCommit(true);
      }
    }
  }

  public int update(Connection db) throws SQLException {
    if (this.getId() == -1) {
      throw new SQLException("ID was not specified");
    }
    if (!isValid()) {
      return -1;
    }
    boolean commit = db.getAutoCommit();
    try {
      if (commit) {
        db.setAutoCommit(false);
      }
      // Update existing dashboard levels
      int currentLevel = DashboardList.queryCurrentLevel(db, id);
      int maxLevel = DashboardList.queryMaxLevel(db, projectId);
      if (level > maxLevel + 1) {
        level = maxLevel + 1;
      }
      PreparedStatement pst;
      if (level < currentLevel) {
        // Update any dashboards that need to be shifted
        pst = db.prepareStatement(
            "UPDATE project_dashboard " +
                "SET dashboard_level = dashboard_level + 1 " +
                "WHERE project_id = ? " +
                "AND dashboard_level >= ? " +
                "AND dashboard_level <= ? "
        );
        pst.setInt(1, projectId);
        pst.setInt(2, level);
        pst.setInt(3, currentLevel);
        pst.executeUpdate();
        pst.close();
      } else if (level > currentLevel) {
        // Update any dashboards that need to be shifted
        pst = db.prepareStatement(
            "UPDATE project_dashboard " +
                "SET dashboard_level = dashboard_level - 1 " +
                "WHERE project_id = ? " +
                "AND dashboard_level >= ? " +
                "AND dashboard_level <= ? "
        );
        pst.setInt(1, projectId);
        pst.setInt(2, currentLevel);
        pst.setInt(3, level);
        pst.executeUpdate();
        pst.close();
      }

      // Update the record
      pst = db.prepareStatement(
          "UPDATE project_dashboard " +
              "SET dashboard_name = ?, dashboard_level = ?, project_id = ?, portal = ?, enabled = ? " +
              (entered != null ? ", entered = ? " : "") + ", modified = CURRENT_TIMESTAMP " +
              "WHERE modified = ? ");
      int i = 0;
      pst.setString(++i, name);
      pst.setInt(++i, level);
      pst.setInt(++i, projectId);
      pst.setBoolean(++i, portal);
      pst.setBoolean(++i, enabled);
      if (entered != null) {
        pst.setTimestamp(++i, entered);
      }
      pst.setTimestamp(++i, modified);
      int updateCount = pst.executeUpdate();
      pst.close();
      if (commit) {
        db.commit();
      }
      return updateCount;
    } catch (Exception e) {
      if (commit) {
        db.rollback();
      }
      throw new SQLException(e.getMessage());
    } finally {
      if (commit) {
        db.setAutoCommit(true);
      }
    }
  }

  public boolean delete(Connection db) throws SQLException {
    if (this.getId() == -1) {
      throw new SQLException("ID was not specified");
    }
    boolean commit = db.getAutoCommit();
    try {
      if (commit) {
        db.setAutoCommit(false);
      }

      DashboardPageList pageList = new DashboardPageList();
      pageList.setDashboardId(id);
      pageList.buildList(db);
      pageList.delete(db);

      PreparedStatement pst = db.prepareStatement(
          "DELETE FROM project_dashboard " +
              "WHERE dashboard_id = ? ");
      pst.setInt(1, id);
      pst.execute();
      pst.close();

      // Shift existing dashboards to the left
      pst = db.prepareStatement(
          "UPDATE project_dashboard " +
              "SET dashboard_level = dashboard_level - 1 " +
              "WHERE project_id = ? " +
              "AND dashboard_level > ? "
      );
      pst.setInt(1, projectId);
      pst.setInt(2, level);
      pst.executeUpdate();
      pst.close();

      if (commit) {
        db.commit();
      }
      return true;
    } catch (Exception e) {
      if (commit) {
        db.rollback();
      }
      throw new SQLException(e.getMessage());
    } finally {
      if (commit) {
        db.setAutoCommit(true);
      }
    }
  }
}
