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

import com.concursive.connect.Constants;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Description of Class
 *
 * @author matt rajkowski
 * @version $Id$
 * @created Feb 18, 2007
 */
public class DashboardList extends ArrayList<Dashboard> {

  private int projectId = -1;
  private int portal = Constants.UNDEFINED;

  public DashboardList() {
  }


  public int getProjectId() {
    return projectId;
  }

  public void setProjectId(int projectId) {
    this.projectId = projectId;
  }

  public int getPortal() {
    return portal;
  }

  public void setPortal(int portal) {
    this.portal = portal;
  }

  public void buildList(Connection db) throws SQLException {
    PreparedStatement pst;
    ResultSet rs;

    StringBuffer sqlSelect = new StringBuffer();
    StringBuffer sqlFilter = new StringBuffer();
    StringBuffer sqlOrder = new StringBuffer();

    createFilter(sqlFilter);
    sqlSelect.append(
        "SELECT * " +
            "FROM project_dashboard d " +
            "WHERE dashboard_id > -1 ");
    sqlOrder.append("ORDER BY dashboard_level, dashboard_name ");
    pst = db.prepareStatement(sqlSelect.toString() + sqlFilter.toString() + sqlOrder.toString());
    prepareFilter(pst);
    rs = pst.executeQuery();
    while (rs.next()) {
      Dashboard dashboard = new Dashboard(rs);
      this.add(dashboard);
    }
    rs.close();
    pst.close();
  }


  /**
   * Description of the Method
   *
   * @param sqlFilter Description of Parameter
   */
  private void createFilter(StringBuffer sqlFilter) {
    if (sqlFilter == null) {
      sqlFilter = new StringBuffer();
    }
    if (projectId > -1) {
      sqlFilter.append("AND (project_id = ?) ");
    }
    if (portal != Constants.UNDEFINED) {
      sqlFilter.append("AND (portal = ?) ");
    }
  }


  /**
   * Description of the Method
   *
   * @param pst Description of Parameter
   * @return Description of the Returned Value
   * @throws SQLException Description of Exception
   */
  private int prepareFilter(PreparedStatement pst) throws SQLException {
    int i = 0;
    if (projectId > -1) {
      pst.setInt(++i, projectId);
    }
    if (portal != Constants.UNDEFINED) {
      pst.setBoolean(++i, portal == Constants.TRUE);
    }
    return i;
  }

  public Dashboard getSelectedFromId(int id) {
    Iterator i = this.iterator();
    while (i.hasNext()) {
      Dashboard dashboard = (Dashboard) i.next();
      if (id == -1 || dashboard.getId() == id) {
        return dashboard;
      }
    }
    if (!this.isEmpty()) {
      return (Dashboard) this.get(0);
    } else {
      return null;
    }
  }

  public boolean delete(Connection db) throws SQLException {
    boolean commit = db.getAutoCommit();
    try {
      if (commit) {
        db.setAutoCommit(false);
      }
      for (Dashboard dashboard : this) {
        dashboard.delete(db);
      }
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

  public static int queryMaxLevel(Connection db, int projectId) throws SQLException {
    int max = 0;
    PreparedStatement pst = db.prepareStatement(
        "SELECT MAX(dashboard_level) AS maxlevel " +
            "FROM project_dashboard " +
            "WHERE project_id = ? ");
    pst.setInt(1, projectId);
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      max = rs.getInt("maxlevel");
    }
    rs.close();
    pst.close();
    return max;
  }

  public static int queryCurrentLevel(Connection db, int dashboardId) throws SQLException {
    int level = 0;
    PreparedStatement pst = db.prepareStatement(
        "SELECT dashboard_level " +
            "FROM project_dashboard " +
            "WHERE dashboard_id = ? ");
    pst.setInt(1, dashboardId);
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      level = rs.getInt("dashboard_level");
    }
    rs.close();
    pst.close();
    return level;
  }
}
