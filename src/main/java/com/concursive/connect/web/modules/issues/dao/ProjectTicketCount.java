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

package com.concursive.connect.web.modules.issues.dao;

import com.concursive.commons.db.DatabaseUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Represents the information needed for tracking the ticket count in a project
 *
 * @author matt rajkowski
 * @created March 18, 2008
 */

public class ProjectTicketCount {
  private int id = -1;
  private int projectId = -1;
  private int keyCount = 0;

  public int getProjectId() {
    return projectId;
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

  public void setProjectId(int projectId) {
    this.projectId = projectId;
  }

  public void setProjectId(String tmp) {
    this.projectId = Integer.parseInt(tmp);
  }

  public int getKeyCount() {
    return keyCount;
  }

  public void setKeyCount(int keyCount) {
    this.keyCount = keyCount;
  }

  public void setKeyCount(String tmp) {
    this.keyCount = Integer.parseInt(tmp);
  }

  public ProjectTicketCount() {
  }

  public ProjectTicketCount(Connection db, int thisId) throws SQLException {
    queryRecord(db, thisId);
  }

  public ProjectTicketCount(ResultSet rs) throws SQLException {
    buildRecord(rs);
  }

  private void queryRecord(Connection db, int thisId) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "SELECT id, project_id, key_count FROM project_ticket_count WHERE project_id = ? ");
    pst.setInt(1, thisId);
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      buildRecord(rs);
    }
    rs.close();
    pst.close();
  }

  private void buildRecord(ResultSet rs) throws SQLException {
    id = rs.getInt("id");
    projectId = rs.getInt("project_id");
    keyCount = rs.getInt("key_count");
  }

  public void insert(Connection db) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "INSERT INTO project_ticket_count " +
            "(" + (id > -1 ? "id, " : "") + "project_id, key_count) VALUES " +
            "(" + (id > -1 ? "?, " : "") + "?, ?) ");
    int i = 0;
    if (id > -1) {
      pst.setInt(++i, id);
    }
    pst.setInt(++i, projectId);
    pst.setInt(++i, keyCount);
    pst.execute();
    pst.close();
    id = DatabaseUtils.getCurrVal(db, "project_ticket_count_id_seq", id);
  }

  public void update(Connection db) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "UPDATE project_ticket_count SET key_count = ? WHERE project_id = ? ");
    pst.setInt(1, keyCount);
    pst.setInt(2, projectId);
    pst.execute();
    pst.close();
  }

  public void delete(Connection db) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "DELETE FROM project_ticket_count WHERE project_id = ? ");
    pst.setInt(1, projectId);
    pst.execute();
    pst.close();
  }

  /**
   * Each ticket in a project has its own unique count
   *
   * @param db        Description of the Parameter
   * @param projectId Description of the Parameter
   * @throws java.sql.SQLException Description of the Exception
   */
  public static void insertProjectTicketCount(Connection db, int projectId) throws SQLException {
    // Every new project needs a project_ticket_count record
    PreparedStatement pst = db.prepareStatement(
        "INSERT INTO project_ticket_count " +
            "(project_id) VALUES " +
            "(?) ");
    pst.setInt(1, projectId);
    pst.execute();
    pst.close();
  }

  /**
   * Each ticket in a project has its own unique count
   *
   * @param db        Description of the Parameter
   * @param projectId Description of the Parameter
   * @throws java.sql.SQLException Description of the Exception
   */
  public static void deleteProjectTicketCount(Connection db, int projectId) throws SQLException {
    // Every new project needs a project_ticket_count record
    PreparedStatement pst = db.prepareStatement(
        "DELETE FROM project_ticket_count " +
            "WHERE project_id = ? ");
    pst.setInt(1, projectId);
    pst.execute();
    pst.close();
  }
}