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

import java.sql.*;

/**
 * Description
 *
 * @author matt rajkowski
 * @version $Id$
 * @created Dec 16, 2004
 */

public class AssignmentNote extends GenericBean {
  private int id = -1;
  private int assignmentId = -1;
  private int userId = -1;
  private String description = null;
  private Timestamp entered = null;
  private int statusId = -1;
  private int percentComplete = -1;
  private int projectId = -1;

  public AssignmentNote() {
  }

  public AssignmentNote(Assignment assignment) {
    assignmentId = assignment.getId();
    userId = assignment.getModifiedBy();
    description = assignment.getAdditionalNote();
    statusId = assignment.getStatusId();
    percentComplete = assignment.getPercentComplete();
    projectId = assignment.getProjectId();
  }

  public AssignmentNote(ResultSet rs) throws SQLException {
    buildRecord(rs);
  }

  private void buildRecord(ResultSet rs) throws SQLException {
    id = rs.getInt("status_id");
    assignmentId = rs.getInt("assignment_id");
    userId = rs.getInt("user_id");
    description = rs.getString("description");
    entered = rs.getTimestamp("status_date");
    //percentComplete = rs.getDouble("percent_complete");
    //statusId = rs.getInt("project_status_id");
    //projectId = rs.getInt("project_id");
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

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Timestamp getEntered() {
    return entered;
  }

  public void setEntered(Timestamp entered) {
    this.entered = entered;
  }

  public int getStatusId() {
    return statusId;
  }

  public void setStatusId(int statusId) {
    this.statusId = statusId;
  }

  public int getPercentComplete() {
    return percentComplete;
  }

  public void setPercentComplete(int percentComplete) {
    if (percentComplete == -1) {
      this.percentComplete = 0;
    } else {
      this.percentComplete = percentComplete;
    }
  }

  public int getProjectId() {
    return projectId;
  }

  public void setProjectId(int projectId) {
    this.projectId = projectId;
  }

  public boolean isValid() {
    if (description == null || "".equals(description.trim())) {
      return false;
    }
    if (userId == -1) {
      return false;
    }
    return true;
  }

  public void insert(Connection db) throws SQLException {
    PreparedStatement pst = db.prepareStatement("INSERT INTO project_assignments_status " +
        "(assignment_id, user_id, description, percent_complete, project_status_id) " +
        "VALUES (?, ?, ?, ?, ?)");
    int i = 0;
    pst.setInt(++i, assignmentId);
    pst.setInt(++i, userId);
    pst.setString(++i, description);
    pst.setInt(++i, percentComplete);
    pst.setInt(++i, statusId);
    pst.execute();
    pst.close();
    id = DatabaseUtils.getCurrVal(db, "project_assignmen_status_id_seq", -1);
  }


}
