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
import java.util.StringTokenizer;


/**
 * Represents a User Assigned to an Assignment in iTeam
 *
 * @author matt rajkowski
 * @version $Id$
 * @created June 7, 2005
 */
public class AssignedUser extends GenericBean {

  private int id = -1;
  private int assignmentId = -1;
  private int userId = -1;
  private int assignmentRoleId = -1;
  private int state = -1;
  private int enteredBy = -1;
  private int modifiedBy = -1;


  public AssignedUser() {
  }

  public AssignedUser(ResultSet rs) throws SQLException {
    buildRecord(rs);
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getAssignmentId() {
    return assignmentId;
  }

  public void setAssignmentId(int assignmentId) {
    this.assignmentId = assignmentId;
  }

  public int getUserId() {
    return userId;
  }

  public void setUserId(int userId) {
    this.userId = userId;
  }

  public int getAssignmentRoleId() {
    return assignmentRoleId;
  }

  public void setAssignmentRoleId(int assignmentRoleId) {
    this.assignmentRoleId = assignmentRoleId;
  }

  public int getState() {
    return state;
  }

  public void setState(int state) {
    this.state = state;
  }

  public int getEnteredBy() {
    return enteredBy;
  }

  public void setEnteredBy(int enteredBy) {
    this.enteredBy = enteredBy;
  }

  public int getModifiedBy() {
    return modifiedBy;
  }

  public void setModifiedBy(int modifiedBy) {
    this.modifiedBy = modifiedBy;
  }

  private void buildRecord(ResultSet rs) throws SQLException {
    id = rs.getInt("id");
    assignmentId = rs.getInt("assignment_id");
    userId = rs.getInt("user_id");
    assignmentRoleId = DatabaseUtils.getInt(rs, "assignment_role_id");
  }

  public void setRequestItems(String tmp) {
    if (tmp != null) {
      StringTokenizer st = new StringTokenizer(tmp, ",");
      userId = Integer.parseInt(st.nextToken());
      assignmentRoleId = Integer.parseInt(st.nextToken());
      state = Integer.parseInt(st.nextToken());
    }
  }

  public void insert(Connection db) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "INSERT INTO project_assignments_user " +
            "(assignment_id, user_id, assignment_role_id, enteredby, modifiedby) " +
            "VALUES (?, ?, ?, ?, ?) ");
    int i = 0;
    pst.setInt(++i, assignmentId);
    pst.setInt(++i, userId);
    DatabaseUtils.setInt(pst, ++i, assignmentRoleId);
    pst.setInt(++i, enteredBy);
    pst.setInt(++i, modifiedBy);
    pst.execute();
    pst.close();
    id = DatabaseUtils.getCurrVal(db, "project_assignments_user_id_seq", -1);
  }

  public void update(Connection db) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "UPDATE project_assignments_user " +
            "SET assignment_role_id = ?, modifiedby = ?, " +
            "modified = " + DatabaseUtils.getCurrentTimestamp(db) + " " +
            "WHERE assignment_id = ? " +
            "AND user_id = ? " +
            (assignmentRoleId != -1 ? "AND (assignment_role_id <> ? OR assignment_role_id IS NULL) " : "") +
            (assignmentRoleId == -1 ? "AND assignment_role_id IS NOT NULL " : ""));
    int i = 0;
    DatabaseUtils.setInt(pst, ++i, assignmentRoleId);
    pst.setInt(++i, modifiedBy);
    pst.setInt(++i, assignmentId);
    pst.setInt(++i, userId);
    if (assignmentRoleId != -1) {
      DatabaseUtils.setInt(pst, ++i, assignmentRoleId);
    }
    int count = pst.executeUpdate();
    pst.close();
  }

  public void delete(Connection db) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "DELETE FROM project_assignments_user " +
            "WHERE assignment_id = ? " +
            "AND user_id = ? ");
    int i = 0;
    pst.setInt(++i, assignmentId);
    pst.setInt(++i, userId);
    pst.execute();
    pst.close();
  }

}


