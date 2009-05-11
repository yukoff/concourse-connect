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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

/**
 * Represents a User Assigned to an Assignment in iTeam
 *
 * @author matt rajkowski
 * @version $Id$
 * @created June 7, 2005
 */
public class AssignedUserList extends ArrayList<AssignedUser> {

  public static final int EXISTING_USER_ID = 0;
  public static final int NEW_USER_ID = 1;
  public static final int DELETED_USER_ID = 2;

  int assignmentId = -1;
  int enteredBy = -1;
  int modifiedBy = -1;

  public AssignedUserList() {
  }

  public void setRequestItems(String tmp) {
    if (tmp != null) {
      StringTokenizer st = new StringTokenizer(tmp, "|");
      while (st.hasMoreTokens()) {
        String thisToken = st.nextToken();
        AssignedUser assignedUser = new AssignedUser();
        assignedUser.setRequestItems(thisToken);
        this.add(assignedUser);
      }
    }
  }


  public int getAssignmentId() {
    return assignmentId;
  }

  public void setAssignmentId(int assignmentId) {
    this.assignmentId = assignmentId;
  }

  public int getModifiedBy() {
    return modifiedBy;
  }

  public void setModifiedBy(int modifiedBy) {
    this.modifiedBy = modifiedBy;
  }

  public int getEnteredBy() {
    return enteredBy;
  }

  public void setEnteredBy(int enteredBy) {
    this.enteredBy = enteredBy;
  }

  public void buildList(Connection db) throws SQLException {
    PreparedStatement pst = null;
    ResultSet rs = null;
    int items = -1;
    StringBuffer sqlSelect = new StringBuffer();
    StringBuffer sqlFilter = new StringBuffer();
    StringBuffer sqlOrder = new StringBuffer();
    //Set the order
    sqlOrder.append("ORDER BY ar.\"level\", u.last_name ");
    createFilter(sqlFilter);
    //Need to build a base SQL statement for returning records
    sqlSelect.append("SELECT ");
    sqlSelect.append(
        "au.* " +
            "FROM project_assignments_user au " +
            "LEFT JOIN lookup_project_assignment_role ar ON (au.assignment_role_id = ar.code) " +
            "LEFT JOIN users u ON (au.user_id = u.user_id) " +
            "WHERE au.id > -1 ");
    pst = db.prepareStatement(
        sqlSelect.toString() + sqlFilter.toString() + sqlOrder.toString());
    items = prepareFilter(pst);
    rs = pst.executeQuery();
    while (rs.next()) {
      AssignedUser thisUser = new AssignedUser(rs);
      this.add(thisUser);
    }
    rs.close();
    pst.close();
  }

  protected void createFilter(StringBuffer sqlFilter) {
    if (assignmentId > -1) {
      sqlFilter.append("AND au.assignment_id = ? ");
    }
  }


  protected int prepareFilter(PreparedStatement pst) throws SQLException {
    int i = 0;
    if (assignmentId > -1) {
      pst.setInt(++i, assignmentId);
    }
    return i;
  }

  public String getAssignedCSV(String separator) {
    StringBuffer sb = new StringBuffer();
    Iterator i = this.iterator();
    while (i.hasNext()) {
      AssignedUser assignedUser = (AssignedUser) i.next();
      sb.append(assignedUser.getUserId());
      if (i.hasNext()) {
        sb.append(separator);
      }
    }
    return sb.toString();
  }

  public String getRoleCSV(String separator) {
    StringBuffer sb = new StringBuffer();
    Iterator i = this.iterator();
    while (i.hasNext()) {
      AssignedUser assignedUser = (AssignedUser) i.next();
      sb.append(assignedUser.getAssignmentRoleId());
      if (i.hasNext()) {
        sb.append(separator);
      }
    }
    return sb.toString();
  }

  public String getStateCSV(String separator) {
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < this.size(); i++) {
      sb.append(EXISTING_USER_ID);
      if ((i + 1) < this.size()) {
        sb.append(separator);
      }
    }
    return sb.toString();
  }

  public void insert(Connection db) throws SQLException {
    Iterator i = this.iterator();
    while (i.hasNext()) {
      AssignedUser thisAssignedUser = (AssignedUser) i.next();
      thisAssignedUser.setAssignmentId(assignmentId);
      thisAssignedUser.setEnteredBy(enteredBy);
      thisAssignedUser.setModifiedBy(modifiedBy);
      thisAssignedUser.insert(db);
    }
  }

  public void update(Connection db) throws SQLException {
    boolean autoCommit = db.getAutoCommit();
    try {
      if (autoCommit) {
        db.setAutoCommit(false);
      }
      // Determine if insert, update, or delete
      Iterator i = this.iterator();
      while (i.hasNext()) {
        AssignedUser thisAssignedUser = (AssignedUser) i.next();
        thisAssignedUser.setAssignmentId(assignmentId);
        switch (thisAssignedUser.getState()) {
          case EXISTING_USER_ID:
            thisAssignedUser.setModifiedBy(modifiedBy);
            thisAssignedUser.update(db);
            break;
          case NEW_USER_ID:
            thisAssignedUser.setEnteredBy(modifiedBy);
            thisAssignedUser.setModifiedBy(modifiedBy);
            thisAssignedUser.insert(db);
            break;
          case DELETED_USER_ID:
            thisAssignedUser.delete(db);
            break;
          default:
            break;
        }
      }
      if (autoCommit) {
        db.commit();
      }
    } catch (Exception e) {
      if (autoCommit) {
        db.rollback();
      }
      throw new SQLException(e.getMessage());
    } finally {
      if (autoCommit) {
        db.setAutoCommit(true);
      }
    }
  }
}


