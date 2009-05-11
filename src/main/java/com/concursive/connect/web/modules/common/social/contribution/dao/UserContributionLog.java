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

package com.concursive.connect.web.modules.common.social.contribution.dao;

import com.concursive.commons.db.DatabaseUtils;
import com.concursive.commons.web.mvc.beans.GenericBean;

import java.sql.*;

/**
 * Handles viewing an object by a user
 *
 * @author Kailash Bhoopalam
 * @created January 27, 2009
 */
public class UserContributionLog extends GenericBean {

  private int id = -1;
  private int userId = -1;
  private Timestamp contributionDate = null;
  private int points = 0;
  private int contributionId = 0;
  private Timestamp entered = null;
  private int projectId = -1;

  public UserContributionLog() {
  }

  public UserContributionLog(Connection db, int id) throws SQLException {
    queryRecord(db, id);
  }

  public UserContributionLog(ResultSet rs) throws SQLException {
    buildRecord(rs);
  }

  /**
   * @return the id
   */
  public int getId() {
    return id;
  }

  /**
   * @param id the id to set
   */
  public void setId(int id) {
    this.id = id;
  }

  public void setId(String id) {
    this.id = Integer.parseInt(id);
  }

  /**
   * @return the userId
   */
  public int getUserId() {
    return userId;
  }

  /**
   * @param userId the userId to set
   */
  public void setUserId(int userId) {
    this.userId = userId;
  }

  public void setUserId(String userId) {
    this.userId = Integer.parseInt(userId);
  }

  /**
   * @return the contributionDate
   */
  public Timestamp getContributionDate() {
    return contributionDate;
  }

  /**
   * @param contributionDate the contributionDate to set
   */
  public void setContributionDate(Timestamp contributionDate) {
    this.contributionDate = contributionDate;
  }

  public void setContributionDate(String contributionDate) {
    this.contributionDate = DatabaseUtils.parseTimestamp(contributionDate);
  }

  /**
   * @return the points
   */
  public int getPoints() {
    return points;
  }

  /**
   * @param points the points to set
   */
  public void setPoints(int points) {
    this.points = points;
  }

  public void setPoints(String points) {
    this.points = Integer.parseInt(points);
  }

  /**
   * @return the contributionId
   */
  public int getContributionId() {
    return contributionId;
  }

  /**
   * @param contributionId the contributionId to set
   */
  public void setContributionId(int contributionId) {
    this.contributionId = contributionId;
  }

  public void setContributionId(String contributionId) {
    this.contributionId = Integer.parseInt(contributionId);
  }


  /**
   * @return the entered
   */
  public Timestamp getEntered() {
    return entered;
  }

  /**
   * @param entered the entered to set
   */
  public void setEntered(Timestamp entered) {
    this.entered = entered;
  }

  public void setEntered(String entered) {
    this.entered = DatabaseUtils.parseTimestamp(entered);
  }

  /**
   * @return the projectId
   */
  public int getProjectId() {
    return projectId;
  }

  /**
   * @param projectId the projectId to set
   */
  public void setProjectId(int projectId) {
    this.projectId = projectId;
  }

  public void setProjectId(String projectId) {
    this.projectId = Integer.parseInt(projectId);
  }

  public void queryRecord(Connection db, int id) throws SQLException {
    StringBuffer sql = new StringBuffer();
    sql.append(
        "SELECT ucl.* " +
            "FROM user_contribution_log ucl " +
            "WHERE record_id = ? ");

    PreparedStatement pst = db.prepareStatement(sql.toString());
    int i = 0;
    pst.setInt(++i, id);

    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      buildRecord(rs);
    }
    rs.close();
    pst.close();
    if (id == -1) {
      throw new SQLException("User contribution log record not found.");
    }
  }

  public boolean insert(Connection db) throws SQLException {
    if (!isValid()) {
      return false;
    }
    StringBuffer sql = new StringBuffer();
    sql.append(
        "INSERT INTO user_contribution_log " +
            "(" + (id > -1 ? "record_id," : "") +
            "user_id, contribution_date, contribution_id, points, project_id ");
    if (entered != null) {
      sql.append(", entered ");
    }
    sql.append(") VALUES (");
    if (id > -1) {
      sql.append("?,");
    }
    sql.append("?, ?, ?, ?, ? ");
    if (entered != null) {
      sql.append(", ?");
    }
    sql.append(") ");

    int i = 0;
    boolean commit = db.getAutoCommit();
    try {
      if (commit) {
        db.setAutoCommit(false);
      }
      //Insert the topic
      PreparedStatement pst = db.prepareStatement(sql.toString());
      if (id > -1) {
        pst.setInt(++i, id);
      }
      pst.setInt(++i, userId);
      pst.setTimestamp(++i, contributionDate);
      pst.setInt(++i, contributionId);
      pst.setInt(++i, points);
      pst.setInt(++i, projectId);
      if (entered != null) {
        pst.setTimestamp(++i, entered);
      }
      pst.execute();
      pst.close();
      id = DatabaseUtils.getCurrVal(db, "user_contribution_log_record_id_seq", -1);
      if (commit) {
        db.commit();
      }
    } catch (SQLException e) {
      if (commit) {
        db.rollback();
      }
      throw e;
    } finally {
      if (commit) {
        db.setAutoCommit(true);
      }
    }
    return true;
  }

  public int update(Connection db) throws SQLException {
    if (this.getId() == -1) {
      throw new SQLException("ID was not specified");
    }
    if (!isValid()) {
      return -1;
    }
    // Update the project
    int resultCount = 0;
    PreparedStatement pst = db.prepareStatement(
        "UPDATE  user_contribution_log SET " +
            " user_id = ? , " +
            " contribution_date = ? , " +
            " contribution_id = ? , " +
            " points = ? " +
            "WHERE record_id = ? ");
    int i = 0;
    pst.setInt(++i, userId);
    pst.setTimestamp(++i, contributionDate);
    pst.setInt(++i, contributionId);
    pst.setInt(++i, points);
    pst.setInt(++i, id);
    resultCount = pst.executeUpdate();
    pst.close();
    return resultCount;
  }

  public boolean delete(Connection db) throws SQLException {
    if (this.getId() == -1) {
      throw new SQLException("ID was not specified");
    }
    int recordCount = 0;
    boolean commit = db.getAutoCommit();
    try {
      if (commit) {
        db.setAutoCommit(false);
      }

      //Delete the contribution log
      PreparedStatement pst = db.prepareStatement(
          "DELETE FROM user_contribution_log " +
              "WHERE record_id = ? ");
      pst.setInt(1, id);
      recordCount = pst.executeUpdate();
      pst.close();
      if (commit) {
        db.commit();
      }
    } catch (Exception e) {
      if (commit) {
        db.rollback();
      }
      e.printStackTrace(System.out);
      throw new SQLException(e.getMessage());
    } finally {
      if (commit) {
        db.setAutoCommit(true);
      }
    }
    if (recordCount == 0) {
      errors.put("actionError", "User contribution log could not be deleted because it no longer exists.");
      return false;
    } else {
      return true;
    }
  }

  private boolean isValid() {
    if (userId == -1) {
      errors.put("userIdError", "User Id is required");
    }
    if (contributionId == -1) {
      errors.put("contributionIdError", "Contribution Id is required");
    }
    if (contributionDate == null) {
      errors.put("contributionDateError", "Contribution Date is required");
    }
    return !this.hasErrors();
  }

  /**
   * @param rs
   */
  private void buildRecord(ResultSet rs) throws SQLException {
    id = DatabaseUtils.getInt(rs, "record_id");
    userId = DatabaseUtils.getInt(rs, "user_id");
    contributionDate = rs.getTimestamp("contribution_date");
    contributionId = DatabaseUtils.getInt(rs, "contribution_id");
    points = DatabaseUtils.getInt(rs, "points");
    entered = rs.getTimestamp("entered");
    projectId = DatabaseUtils.getInt(rs, "project_id");
  }

}