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

import com.concursive.connect.Constants;
import com.concursive.connect.cache.utils.CacheUtils;

import javax.servlet.http.HttpServletRequest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Represents a list of permissions for a project
 *
 * @author matt rajkowski
 * @version $Id$
 * @created August 10, 2003
 */
public class PermissionList extends HashMap<String, Permission> {

  private int id = -1;
  private int projectId = -1;
  private int permissionId = -1;
  private int userLevel = -1;
  private String name = null;


  /**
   * Constructor for the PermissionList object
   */
  public PermissionList() {
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

  /**
   * Sets the projectId attribute of the PermissionList object
   *
   * @param tmp The new projectId value
   */
  public void setProjectId(int tmp) {
    this.projectId = tmp;
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

  public void setPermissionId(String tmp) {
    this.permissionId = Integer.parseInt(tmp);
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

  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public void buildList(Connection db) throws SQLException {
    StringBuffer sql = new StringBuffer();
    sql.append(
        "SELECT p.id, p.project_id, p.permission_id, p.userlevel, lp.permission " +
            "FROM project_permissions p, lookup_project_permission lp " +
            "WHERE p.permission_id = lp.code ");
    createFilter(sql);
    PreparedStatement pst = db.prepareStatement(sql.toString());
    prepareFilter(pst);
    ResultSet rs = pst.executeQuery();
    while (rs.next()) {
      Permission permission = new Permission(rs);
      this.put(permission.getName(), permission);
    }
    rs.close();
    pst.close();
  }


  /**
   * Description of the Method
   *
   * @param sqlFilter Description of the Parameter
   */
  private void createFilter(StringBuffer sqlFilter) {
    if (sqlFilter == null) {
      sqlFilter = new StringBuffer();
    }
    if (id > -1) {
      sqlFilter.append("AND id = ? ");
    }
    if (projectId > -1) {
      sqlFilter.append("AND project_id = ? ");
    }
    if (permissionId > -1) {
      sqlFilter.append("AND permission_id = ? ");
    }
    if (userLevel > -1) {
      sqlFilter.append("AND userlevel = ? ");
    }
    if (name != null) {
      sqlFilter.append("AND permission = ? ");
    }
  }


  /**
   * Description of the Method
   *
   * @param pst Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  private int prepareFilter(PreparedStatement pst) throws SQLException {
    int i = 0;
    if (id > -1) {
      pst.setInt(++i, id);
    }
    if (projectId > -1) {
      pst.setInt(++i, projectId);
    }
    if (permissionId > -1) {
      pst.setInt(++i, permissionId);
    }
    if (userLevel > -1) {
      pst.setInt(++i, userLevel);
    }
    if (name != null) {
      pst.setString(++i, name);
    }
    return i;
  }


  /**
   * Gets the accessLevel attribute of the PermissionList object
   *
   * @param permissionName Description of the Parameter
   * @return The accessLevel value
   */
  public int getAccessLevel(String permissionName) {
    Permission permission = this.get(permissionName);
    if (permission == null) {
      return 1;
    } else {
      return permission.getUserLevel();
    }
  }

  public void setAccessLevel(String permissionName, int userLevel) {
    Permission permission = this.get(permissionName);
    if (permission != null) {
      permission.setUserLevel(userLevel);
    }
  }


  /**
   * Description of the Method
   *
   * @param db        Description of the Parameter
   * @param request   Description of the Parameter
   * @param projectId Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public static void updateProjectPermissions(Connection db, HttpServletRequest request, int projectId) throws SQLException {
    //Look through the request and put the permissions in buckets
    try {
      db.setAutoCommit(false);
      //Delete the previous settings
      PreparedStatement pst = db.prepareStatement(
          "DELETE FROM project_permissions " +
              "WHERE project_id = ? ");
      pst.setInt(1, projectId);
      pst.execute();
      pst.close();
      //Insert the new settings
      int count = 0;
      String permissionId = null;
      while ((permissionId = request.getParameter("perm" + (++count))) != null) {
        pst = db.prepareStatement(
            "INSERT INTO project_permissions (project_id, permission_id, userlevel) " +
                "VALUES (?, ?, ?)");
        pst.setInt(1, projectId);
        pst.setInt(2, Integer.parseInt(permissionId));
        pst.setInt(3, Integer.parseInt(request.getParameter("perm" + count + "level")));
        pst.execute();
      }
      pst.close();
      db.commit();
      CacheUtils.invalidateValue(Constants.SYSTEM_PROJECT_CACHE, projectId);
    } catch (SQLException e) {
      db.rollback();
    } finally {
      db.setAutoCommit(true);
    }
  }


  /**
   * Description of the Method
   *
   * @param db        Description of the Parameter
   * @param projectId Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public static void delete(Connection db, int projectId) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "DELETE FROM project_permissions " +
            "WHERE project_id = ? ");
    pst.setInt(1, projectId);
    pst.execute();
    pst.close();
  }


  /**
   * Description of the Method
   *
   * @param db        Description of the Parameter
   * @param projectId Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public static void insertDefaultPermissions(Connection db, int projectId) throws SQLException {
    //Make sure no permissions exist, then insert
    PreparedStatement pst = db.prepareStatement(
        "SELECT count(*) AS perm_count " +
            "FROM project_permissions " +
            "WHERE project_id = ? ");
    pst.setInt(1, projectId);
    ResultSet rs = pst.executeQuery();
    rs.next();
    int count = rs.getInt("perm_count");
    rs.close();
    pst.close();
    //Insert the permissions
    if (count == 0) {
      PermissionLookupList list = new PermissionLookupList();
      list.setIncludeEnabled(Constants.TRUE);
      list.buildList(db);
      for (PermissionLookup thisPermission : list) {
        pst = db.prepareStatement(
            "INSERT INTO project_permissions " +
                "(project_id, permission_id, userlevel) VALUES (?, ?, ?) ");
        pst.setInt(1, projectId);
        pst.setInt(2, thisPermission.getId());
        pst.setInt(3, thisPermission.getDefaultRole());
        pst.execute();
      }
      pst.close();
    }
  }

  public void insert(Connection db) throws SQLException {
    for (Permission permission : this.values()) {
      permission.setId(-1);
      permission.setProjectId(projectId);
      permission.insert(db);
    }
  }
}

