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
package com.concursive.connect.web.modules.processes.dao;

import com.concursive.commons.db.DatabaseUtils;
import com.concursive.commons.text.StringUtils;
import com.concursive.commons.web.mvc.beans.GenericBean;

import java.sql.*;

/**
 * Manages dao to the process_log table
 *
 * @author Nanda Kumar
 * @created November 23, 2009
 */
public class Process extends GenericBean {

  private int code = -1;
  private String description = null;
  private boolean enabled = false;
  private Timestamp entered = null;
  private Timestamp modified = null;
  private Timestamp processed = null;
  private long longValue = -1;


  public Process() {
  }

  public Process(ResultSet rs) throws SQLException {
    buildRecord(rs);
  }

  public Process(Connection db, String description) throws SQLException {
    queryRecord(db, description);
  }

  /**
   * @param code the code to set
   */
  public void setCode(int code) {
    this.code = code;
  }

  /**
   * @return the code
   */
  public int getCode() {
    return code;
  }

  /**
   * @param description the description to set
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * @return the description
   */
  public String getDescription() {
    return description;
  }

  /**
   * @param enabled the enabled to set
   */
  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  /**
   * @param enabled the enabled to set
   */
  public void setEnabled(String enabled) {
    this.enabled = DatabaseUtils.parseBoolean(enabled);
  }

  /**
   * @return the enabled
   */
  public boolean getEnabled() {
    return enabled;
  }

  /**
   * @param entered the entered to set
   */
  public void setEntered(Timestamp entered) {
    this.entered = entered;
  }

  /**
   * @param entered the entered to set
   */
  public void setEntered(String entered) {
    this.entered = DatabaseUtils.parseTimestamp(entered);
  }

  /**
   * @return the entered
   */
  public Timestamp getEntered() {
    return entered;
  }

  /**
   * @param modified the modified to set
   */
  public void setModified(Timestamp modified) {
    this.modified = modified;
  }

  /**
   * @param modified the modified to set
   */
  public void setModified(String modified) {
    this.modified = DatabaseUtils.parseTimestamp(modified);
  }

  /**
   * @return the modified
   */
  public Timestamp getModified() {
    return modified;
  }

  /**
   * @param processed the processed to set
   */
  public void setProcessed(Timestamp processed) {
    this.processed = processed;
  }

  /**
   * @param processed the processed to set
   */
  public void setProcessed(String processed) {
    this.processed = DatabaseUtils.parseTimestamp(processed);
  }

  /**
   * @return the processed
   */
  public Timestamp getProcessed() {
    return processed;
  }

  /**
   * @param longValue the long_value to set
   */
  public void setLongValue(long longValue) {
    this.longValue = longValue;
  }

  /**
   * @return the long_value
   */
  public long getLongValue() {
    return longValue;
  }

  private void queryRecord(Connection db, String description) throws SQLException {

    StringBuffer sql = new StringBuffer();
    sql.append(
        "SELECT * " +
            "FROM process_log p " +
            "WHERE description = ? ");
    PreparedStatement pst = db.prepareStatement(sql.toString());
    int i = 0;
    pst.setString(++i, description);
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      buildRecord(rs);
    }
    rs.close();
    pst.close();

  }

  private void buildRecord(ResultSet rs) throws SQLException {
    code = rs.getInt("code");
    description = rs.getString("description");
    enabled = rs.getBoolean("enabled");
    entered = rs.getTimestamp("entered");
    modified = rs.getTimestamp("modified");
    longValue = rs.getLong("long_value");
  }

  private boolean isValid() {
    if (!StringUtils.hasText(description)) {
      errors.put("descriptionError", "Description is required");
    }
    return !hasErrors();
  }

  public boolean insert(Connection db) throws SQLException {
    if (!isValid()) {
      return false;
    }
    boolean commit = db.getAutoCommit();
    try {
      if (commit) {
        db.setAutoCommit(false);
      }
      PreparedStatement pst = db.prepareStatement(
          "INSERT INTO process_log " +
              "(description, enabled" +
              (entered != null ? ", entered" : "") +
              (modified != null ? ", modified" : "") +
              (processed != null ? ", processed" : "") +
              ",long_value) VALUES (?, ?" +
              (entered != null ? ", ?" : "") +
              (modified != null ? ", ?" : "") +
              (processed != null ? ", ?" : "") +
              ", ?)");
      int i = 0;
      pst.setString(++i, description);
      pst.setBoolean(++i, enabled);
      if (entered != null) {
        pst.setTimestamp(++i, entered);
      }
      if (modified != null) {
        pst.setTimestamp(++i, modified);
      }
      if (processed != null) {
        pst.setTimestamp(++i, processed);
      }
      pst.setLong(++i, longValue);
      pst.execute();
      pst.close();
      code = DatabaseUtils.getCurrVal(db, "process_log_code_seq", -1);
      if (commit) {
        db.commit();
      }
      return true;
    } catch (Exception e) {
      if (commit) {
        db.rollback();
      }
      throw new SQLException(e.getMessage());
    } finally {
      if (commit) {
        db.setAutoCommit(true);
      }
    }
  }

  public int update(Connection db) throws SQLException {
    if (!isValid()) {
      return -1;
    }
    boolean commit = db.getAutoCommit();
    try {
      if (commit) {
        db.setAutoCommit(false);
      }
      PreparedStatement pst = db.prepareStatement(
          "UPDATE process_log " +
              "SET enabled = ?" +
              (entered != null ? ", entered = ?" : "") +
              (modified != null ? ", modified = ?" : "") +
              (processed != null ? ", processed = ?" : "") +
              (longValue > -1 ? ", long_value = ?" : "") +
              "WHERE description = ?");
      int i = 0;
      pst.setBoolean(++i, enabled);
      if (entered != null) {
        pst.setTimestamp(++i, entered);
      }
      if (modified != null) {
        pst.setTimestamp(++i, modified);
      }
      if (processed != null) {
        pst.setTimestamp(++i, processed);
      }
      if (longValue > -1) {
        pst.setLong(++i, longValue);
      }
      pst.setString(++i, description);
      int updateCount = pst.executeUpdate();
      pst.close();
      if (commit) {
        db.commit();
      }
      return updateCount;
    } catch (Exception e) {
      if (commit) {
        db.rollback();
      }
      throw new SQLException(e.getMessage());
    } finally {
      if (commit) {
        db.setAutoCommit(true);
      }
    }
  }

  public boolean delete(Connection db) throws SQLException {
    if (this.getCode() == -1) {
      throw new SQLException("Code was not specified");
    }
    boolean commit = db.getAutoCommit();
    try {
      if (commit) {
        db.setAutoCommit(false);
      }
      PreparedStatement pst = db.prepareStatement(
          "DELETE FROM process_log " +
              "WHERE code = ?");
      int i = 0;
      pst.setInt(++i, code);
      pst.execute();
      pst.close();
      if (commit) {
        db.commit();
      }
      return true;
    } catch (Exception e) {
      if (commit) {
        db.rollback();
      }
      throw new SQLException(e.getMessage());
    } finally {
      if (commit) {
        db.setAutoCommit(true);
      }
    }
  }

  public void updateProcessed(Connection db, Timestamp processed, String description) throws SQLException {
    this.processed = processed;
    this.description = description;
    update(db);
  }
}
