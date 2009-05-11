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

package com.concursive.connect.web.modules.plans.dao;

import com.concursive.commons.db.DatabaseUtils;
import com.concursive.commons.web.mvc.beans.GenericBean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Represents a project's category
 *
 * @author matt rajkowski
 * @version $Id$
 * @created June 6, 2005
 */
public class AssignmentRole extends GenericBean {

  private int id = -1;
  private String description = null;
  private boolean enabled = true;
  private int level = -1;


  public AssignmentRole() {
  }


  public AssignmentRole(ResultSet rs) throws SQLException {
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


  public String getDescription() {
    return description;
  }


  public void setDescription(String tmp) {
    this.description = tmp;
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
    description = rs.getString("description");
    enabled = rs.getBoolean("enabled");
    level = rs.getInt("level");
  }


  public void insert(Connection db) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "INSERT INTO lookup_project_assignment_role " +
            "(description, enabled, \"level\") VALUES " +
            "(?, ?, ?) ");
    int i = 0;
    pst.setString(++i, description);
    pst.setBoolean(++i, enabled);
    pst.setInt(++i, level);
    pst.execute();
    pst.close();
    id = DatabaseUtils.getCurrVal(db, "lookup_project_ass_code_seq", -1);
  }


  public void update(Connection db) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "UPDATE lookup_project_assignment_role " +
            "SET description = ?, enabled = ?, \"level\" = ? " +
            "WHERE code = ? ");
    int i = 0;
    pst.setString(++i, description);
    pst.setBoolean(++i, enabled);
    pst.setInt(++i, level);
    pst.setInt(++i, id);
    pst.executeUpdate();
    pst.close();
  }
}

