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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Lookup list object properties
 *
 * @author matt rajkowski
 * @created June 30, 2006
 */
public class ProjectItem extends GenericBean {

  private int id = -1;
  private int projectId = -1;
  private String name = null;
  private boolean enabled = true;
  private int level = -1;


  public ProjectItem() {
  }


  public ProjectItem(Connection db, String table, int code) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "SELECT c.* " +
            "FROM " + table + " c " +
            "WHERE c.code = ? ");
    pst.setInt(1, code);
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      buildRecord(rs);
    }
    rs.close();
    pst.close();
  }


  public ProjectItem(ResultSet rs) throws SQLException {
    buildRecord(rs);
  }


  public int getId() {
    return id;
  }


  public void setId(int tmp) {
    this.id = tmp;
  }


  public void setId(String tmp) {
    this.id = Integer.parseInt(tmp);
  }


  public int getProjectId() {
    return projectId;
  }


  public void setProjectId(int tmp) {
    this.projectId = tmp;
  }


  public void setProjectId(String tmp) {
    this.projectId = Integer.parseInt(tmp);
  }


  public String getName() {
    return name;
  }


  public void setName(String tmp) {
    this.name = tmp;
  }


  public boolean getEnabled() {
    return enabled;
  }


  public void setEnabled(boolean tmp) {
    this.enabled = tmp;
  }


  public void setEnabled(String tmp) {
    this.enabled = DatabaseUtils.parseBoolean(tmp);
  }


  public int getLevel() {
    return level;
  }


  public void setLevel(int tmp) {
    this.level = tmp;
  }


  public void setLevel(String tmp) {
    this.level = Integer.parseInt(tmp);
  }


  private void buildRecord(ResultSet rs) throws SQLException {
    id = rs.getInt("code");
    projectId = rs.getInt("project_id");
    name = rs.getString("item_name");
    enabled = rs.getBoolean("enabled");
    level = rs.getInt("level");
  }


  public void insert(Connection db, String table) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "INSERT INTO " + table + " " +
            "(project_id, item_name, enabled, level) VALUES " +
            "(?, ?, ?, ?) ");
    int i = 0;
    pst.setInt(++i, projectId);
    pst.setString(++i, name);
    pst.setBoolean(++i, enabled);
    pst.setInt(++i, level);
    pst.execute();
    pst.close();
    id = DatabaseUtils.getCurrVal(db, table + "_code_seq", -1);
  }


  public void update(Connection db, String table) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "UPDATE " + table + " " +
            "SET project_id = ?, item_name = ?, enabled = ?, level = ? " +
            "WHERE code = ? ");
    int i = 0;
    pst.setInt(++i, projectId);
    pst.setString(++i, name);
    pst.setBoolean(++i, enabled);
    pst.setInt(++i, level);
    pst.setInt(++i, id);
    pst.executeUpdate();
    pst.close();
  }
}
