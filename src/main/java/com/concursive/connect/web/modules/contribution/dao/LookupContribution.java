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

package com.concursive.connect.web.modules.contribution.dao;

import com.concursive.commons.db.DatabaseUtils;
import com.concursive.commons.text.StringUtils;
import com.concursive.commons.web.mvc.beans.GenericBean;
import com.concursive.connect.web.modules.common.social.contribution.dao.UserContributionLogList;

import java.sql.*;

/**
 * Handles viewing an object by a user
 *
 * @author Kailash Bhoopalam
 * @created January 27, 2009
 */
public class LookupContribution extends GenericBean {

  private int id = -1;
  private String constant = null;
  private String description = null;
  private int level = 0;
  private boolean enabled = true;
  private Timestamp runDate = null;
  private int pointsAwarded = 1;

  public LookupContribution() {
  }

  public LookupContribution(Connection db, int id) throws SQLException {
    queryRecord(db, id);
  }

  public LookupContribution(ResultSet rs) throws SQLException {
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
   * @return the constant
   */
  public String getConstant() {
    return constant;
  }

  /**
   * @param constant the constant to set
   */
  public void setConstant(String constant) {
    this.constant = constant;
  }

  /**
   * @return the description
   */
  public String getDescription() {
    return description;
  }

  /**
   * @param description the description to set
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * @return the level
   */
  public int getLevel() {
    return level;
  }

  /**
   * @param level the level to set
   */
  public void setLevel(int level) {
    this.level = level;
  }

  public void setLevel(String level) {
    this.level = Integer.parseInt(level);
  }

  /**
   * @return the enabled
   */
  public boolean getEnabled() {
    return enabled;
  }

  /**
   * @param enabled the enabled to set
   */
  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  /**
   * @return the runDate
   */
  public Timestamp getRunDate() {
    return runDate;
  }

  /**
   * @param runDate the runDate to set
   */
  public void setRunDate(Timestamp runDate) {
    this.runDate = runDate;
  }

  public void setRunDate(String runDate) {
    this.runDate = DatabaseUtils.parseTimestamp(runDate);
  }

  /**
   * @return the pointsAwarded
   */
  public int getPointsAwarded() {
    return pointsAwarded;
  }

  /**
   * @param pointsAwarded the pointsAwarded to set
   */
  public void setPointsAwarded(int pointsAwarded) {
    this.pointsAwarded = pointsAwarded;
  }

  public void setPointsAwarded(String pointsAwarded) {
    this.pointsAwarded = Integer.parseInt(pointsAwarded);
  }

  public void queryRecord(Connection db, int id) throws SQLException {
    StringBuffer sql = new StringBuffer();
    sql.append(
        "SELECT lc.* " +
            "FROM lookup_contribution lc " +
            "WHERE code = ? ");

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
      throw new SQLException("Contribution record not found.");
    }
  }

  public boolean insert(Connection db) throws SQLException {
    if (!isValid()) {
      return false;
    }
    StringBuffer sql = new StringBuffer();
    sql.append(
        "INSERT INTO lookup_contribution " +
            "(" + (id > -1 ? "code," : "") +
            (runDate != null ? "run_date, " : "") +
            "constant, description, level, enabled, points_awarded )");

    sql.append("VALUES (");
    if (id > -1) {
      sql.append("?,");
    }
    if (runDate != null) {
      sql.append("?,");
    }
    sql.append("?, ?, ?, ?, ? )");

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
      if (runDate != null) {
        pst.setTimestamp(++i, runDate);
      }
      pst.setString(++i, constant);
      pst.setString(++i, description);
      pst.setInt(++i, level);
      pst.setBoolean(++i, enabled);
      pst.setInt(++i, pointsAwarded);

      pst.execute();
      pst.close();
      id = DatabaseUtils.getCurrVal(db, "lookup_contribution_code_seq", -1);
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
        "UPDATE  lookup_contribution SET " +
            (runDate != null ? "run_date = ?, " : "") +
            " constant = ? , " +
            " description = ? , " +
            " level = ? , " +
            " enabled = ? , " +
            " points_awarded = ? " +
            "WHERE code = ? ");
    int i = 0;
    if (runDate != null) {
      pst.setTimestamp(++i, runDate);
    }
    pst.setString(++i, constant);
    pst.setString(++i, description);
    pst.setInt(++i, level);
    pst.setBoolean(++i, enabled);
    pst.setInt(++i, pointsAwarded);
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

      UserContributionLogList userContributionLogList = new UserContributionLogList();
      userContributionLogList.setContributionId(id);
      userContributionLogList.buildList(db);
      userContributionLogList.delete(db);

      //Delete the private message
      PreparedStatement pst = db.prepareStatement(
          "DELETE FROM lookup_contribution " +
              "WHERE code = ? ");
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
      errors.put("actionError", "Contribution could not be deleted because it no longer exists.");
      return false;
    } else {
      return true;
    }
  }

  private boolean isValid() {
    if (!StringUtils.hasText(constant)) {
      errors.put("constantError", "Constant is required");
    }
    if (!StringUtils.hasText(description)) {
      errors.put("descriptionError", "Description is required");
    }
    return !this.hasErrors();
  }

  /**
   * @param rs
   */
  private void buildRecord(ResultSet rs) throws SQLException {
    id = rs.getInt("code");
    constant = rs.getString("constant");
    description = rs.getString("description");
    level = rs.getInt("level");
    enabled = rs.getBoolean("enabled");
    runDate = rs.getTimestamp("run_date");
    pointsAwarded = rs.getInt("points_awarded");
  }

}