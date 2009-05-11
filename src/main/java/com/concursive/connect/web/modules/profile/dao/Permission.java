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

package com.concursive.connect.web.modules.profile.dao;

import com.concursive.commons.db.DatabaseUtils;
import com.concursive.commons.web.mvc.beans.GenericBean;
import com.concursive.connect.Constants;
import com.concursive.connect.cache.utils.CacheUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Represents a list of permissions for a project
 *
 * @author matt rajkowski
 * @version $Id$
 * @created October 13, 2005
 */
public class Permission extends GenericBean {

  // base properties
  private int id = -1;
  private int projectId = -1;
  private int permissionId = -1;
  private int userLevel = -1;
  // helper properties
  private String name = null;

  public Permission() {
  }

  public Permission(ResultSet rs) throws SQLException {
    buildRecord(rs);
  }

  public Permission(Connection db, int id) throws SQLException {
    queryRecord(db, id);
  }

  public void queryRecord(Connection db, int projectPermissionId) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "SELECT p.id, p.project_id, p.permission_id, p.userlevel, lp.permission " +
            "FROM project_permissions p, lookup_project_permission lp " +
            "WHERE p.permission_id = lp.code " +
            "AND p.id = ? ");
    int i = 0;
    pst.setInt(++i, projectPermissionId);
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      buildRecord(rs);
    }
    rs.close();
    pst.close();
    if (id == -1) {
      throw new SQLException("Project Permission record not found.");
    }
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public void setId(String tmp) {
    this.id = Integer.parseInt(tmp);
  }

  public int getProjectId() {
    return projectId;
  }

  public void setProjectId(int projectId) {
    this.projectId = projectId;
  }

  public void setProjectId(String tmp) {
    this.projectId = Integer.parseInt(tmp);
  }


  public int getPermissionId() {
    return permissionId;
  }

  public void setPermissionId(int permissionId) {
    this.permissionId = permissionId;
  }

  public void setPermissionId(String permissionId) {
    this.permissionId = Integer.parseInt(permissionId);
  }

  public int getUserLevel() {
    return userLevel;
  }

  public void setUserLevel(int userLevel) {
    this.userLevel = userLevel;
  }

  public void setUserLevel(String tmp) {
    this.userLevel = Integer.parseInt(tmp);
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  private void buildRecord(ResultSet rs) throws SQLException {
    // base properties
    id = rs.getInt("id");
    projectId = rs.getInt("project_id");
    permissionId = rs.getInt("permission_id");
    userLevel = rs.getInt("userlevel");
    // lookup_project_permission
    name = rs.getString("permission");
  }

  public void insert(Connection db) throws SQLException {
    if (projectId == -1 || permissionId == -1 || userLevel == -1) {
      throw new SQLException("Values not set");
    }
    int i = 0;
    PreparedStatement pst = db.prepareStatement(
        "INSERT INTO project_permissions " +
            "(" + (id > -1 ? "id, " : "") + "project_id, permission_id, userlevel) " +
            "VALUES " +
            "(" + (id > -1 ? "?, " : "") + "?, ?, ?) ");
    if (id > -1) {
      pst.setInt(++i, id);
    }
    pst.setInt(++i, projectId);
    pst.setInt(++i, permissionId);
    pst.setInt(++i, userLevel);
    pst.execute();
    pst.close();
    id = DatabaseUtils.getCurrVal(db, "project_permissions_id_seq", id);
    CacheUtils.invalidateValue(Constants.SYSTEM_PROJECT_CACHE, projectId);
  }

  public int update(Connection db) throws SQLException {
    if (id == -1) {
      throw new SQLException("Values not set");
    }
    int count = -1;
    PreparedStatement pst = db.prepareStatement(
        "UPDATE project_permissions " +
            "SET project_id = ?, permission_id = ?, userlevel = ? " +
            "WHERE id = ? ");
    pst.setInt(1, projectId);
    pst.setInt(2, permissionId);
    pst.setInt(3, userLevel);
    pst.setInt(4, id);
    count = pst.executeUpdate();
    pst.close();
    CacheUtils.invalidateValue(Constants.SYSTEM_PROJECT_CACHE, projectId);
    return count;
  }

  public boolean delete(Connection db) throws SQLException {
    if (id == -1) {
      throw new SQLException("Values not set");
    }
    PreparedStatement pst = db.prepareStatement(
        "DELETE FROM project_permissions " +
            "WHERE id = ? ");
    pst.setInt(1, id);
    pst.execute();
    pst.close();
    CacheUtils.invalidateValue(Constants.SYSTEM_PROJECT_CACHE, projectId);
    return true;
  }
}

