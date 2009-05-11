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

package com.concursive.connect.web.modules.documents.dao;

import com.concursive.commons.db.DatabaseUtils;
import com.concursive.commons.web.mvc.beans.GenericBean;

import java.sql.*;

/**
 * Description of the Class
 *
 * @author matt rajkowski
 * @version $Id$
 * @created January 15, 2003
 */
public class FileDownloadLog extends GenericBean {

  private int itemId = -1;
  private double version = -1;
  private int userId = -1;
  private Timestamp downloadDate = null;
  private int fileSize = 0;

  //helper attributes of the file item
  private int fileItemEnteredBy = -1;
  private int fileItemModifiedBy = -1;


  /**
   * Constructor for the FileDownloadLog object
   */
  public FileDownloadLog() {
  }


  /**
   * Constructor for the FileDownloadLog object
   *
   * @param rs Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public FileDownloadLog(ResultSet rs) throws SQLException {
    buildRecord(rs);
  }


  /**
   * Constructor for the FileDownloadLog object
   *
   * @param db Description of the Parameter
   * @param id Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public FileDownloadLog(Connection db, int id) throws SQLException {
    if (id < 1) {
      throw new SQLException("ID not specified");
    }
    PreparedStatement pst = db.prepareStatement(
        "SELECT item_id, version, user_download_id, download_date, entered, modified  " +
            "FROM project_files_download d " +
            "LEFT JOIN project_files f ON (d.item_id = f.item_id) " +
            "WHERE d.item_id = ? ");
    pst.setInt(1, id);
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      buildRecord(rs);
    }
    rs.close();
    pst.close();
  }


  /**
   * Sets the itemId attribute of the FileDownloadLog object
   *
   * @param tmp The new itemId value
   */
  public void setItemId(int tmp) {
    this.itemId = tmp;
  }


  /**
   * Sets the itemId attribute of the FileDownloadLog object
   *
   * @param tmp The new itemId value
   */
  public void setItemId(String tmp) {
    this.itemId = Integer.parseInt(tmp);
  }


  /**
   * Sets the version attribute of the FileDownloadLog object
   *
   * @param tmp The new version value
   */
  public void setVersion(double tmp) {
    this.version = tmp;
  }


  /**
   * Sets the version attribute of the FileDownloadLog object
   *
   * @param tmp The new version value
   */
  public void setVersion(String tmp) {
    this.version = Double.parseDouble(tmp);
  }


  /**
   * Sets the userId attribute of the FileDownloadLog object
   *
   * @param tmp The new userId value
   */
  public void setUserId(int tmp) {
    this.userId = tmp;
  }


  /**
   * Sets the userId attribute of the FileDownloadLog object
   *
   * @param tmp The new userId value
   */
  public void setUserId(String tmp) {
    this.userId = Integer.parseInt(tmp);
  }


  /**
   * Sets the downloadDate attribute of the FileDownloadLog object
   *
   * @param tmp The new downloadDate value
   */
  public void setDownloadDate(java.sql.Timestamp tmp) {
    this.downloadDate = tmp;
  }


  /**
   * Sets the downloadDate attribute of the FileDownloadLog object
   *
   * @param tmp The new downloadDate value
   */
  public void setDownloadDate(String tmp) {
    this.downloadDate = DatabaseUtils.parseTimestamp(tmp);
  }


  /**
   * Sets the fileSize attribute of the FileDownloadLog object
   *
   * @param tmp The new fileSize value
   */
  public void setFileSize(int tmp) {
    this.fileSize = tmp;
  }


  /**
   * Gets the itemId attribute of the FileDownloadLog object
   *
   * @return The itemId value
   */
  public int getItemId() {
    return itemId;
  }


  /**
   * Gets the version attribute of the FileDownloadLog object
   *
   * @return The version value
   */
  public double getVersion() {
    return version;
  }


  /**
   * Gets the userId attribute of the FileDownloadLog object
   *
   * @return The userId value
   */
  public int getUserId() {
    return userId;
  }


  /**
   * Gets the downloadDate attribute of the FileDownloadLog object
   *
   * @return The downloadDate value
   */
  public java.sql.Timestamp getDownloadDate() {
    return downloadDate;
  }


  /**
   * Gets the fileSize attribute of the FileDownloadLog object
   *
   * @return The fileSize value
   */
  public int getFileSize() {
    return fileSize;
  }


  /**
   * @return the fileItemEnteredBy
   */
  public int getFileItemEnteredBy() {
    return fileItemEnteredBy;
  }


  /**
   * @param fileItemEnteredBy the fileItemEnteredBy to set
   */
  public void setFileItemEnteredBy(int fileItemEnteredBy) {
    this.fileItemEnteredBy = fileItemEnteredBy;
  }

  public void setFileItemEnteredBy(String fileItemEnteredBy) {
    this.fileItemEnteredBy = Integer.parseInt(fileItemEnteredBy);
  }

  /**
   * @return the fileItemModifiedBy
   */
  public int getFileItemModifiedBy() {
    return fileItemModifiedBy;
  }


  /**
   * @param fileItemModifiedBy the fileItemModifiedBy to set
   */
  public void setFileItemModifiedBy(int fileItemModifiedBy) {
    this.fileItemModifiedBy = fileItemModifiedBy;
  }

  public void setFileItemModifiedBy(String fileItemModifiedBy) {
    this.fileItemModifiedBy = Integer.parseInt(fileItemModifiedBy);
  }

  /**
   * Description of the Method
   *
   * @param rs Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  private void buildRecord(ResultSet rs) throws SQLException {
    itemId = rs.getInt("item_id");
    version = rs.getDouble("version");
    userId = rs.getInt("user_download_id");
    downloadDate = rs.getTimestamp("download_date");

    fileItemEnteredBy = rs.getInt("enteredby");
    fileItemModifiedBy = rs.getInt("modifiedby");
  }


  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  public boolean insert(Connection db) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "INSERT INTO project_files_download " +
            "(item_id, version, user_download_id, download_date) " +
            "VALUES (?, ?, ?, ?)");
    int i = 0;
    pst.setInt(++i, itemId);
    pst.setDouble(++i, version);
    pst.setInt(++i, userId);
    pst.setTimestamp(++i, downloadDate);
    pst.execute();
    pst.close();
    return true;
  }


  /**
   * Deletes a download log record
   *
   * @param db Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  public boolean delete(Connection db) throws SQLException {
    boolean result = false;
    boolean commit = db.getAutoCommit();
    try {
      if (commit) {
        db.setAutoCommit(false);
      }
      int i = 0;
      PreparedStatement pst = db.prepareStatement(
          "DELETE FROM project_files_download " +
              "WHERE item_id = ? " +
              "AND user_download_id = ? ");
      pst.setInt(++i, this.getItemId());
      pst.setInt(++i, this.getUserId());
      pst.execute();
      pst.close();
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

    return result;
  }

  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  public boolean updateCounter(Connection db) throws SQLException {
    if (itemId < 0 || version < 0) {
      return false;
    }
    //Record the raw number of downloads
    PreparedStatement pst = db.prepareStatement(
        "UPDATE project_files " +
            "SET downloads = (downloads + 1) " +
            "WHERE item_id = ? ");
    pst.setInt(1, itemId);
    pst.executeUpdate();
    pst.close();
    pst = db.prepareStatement(
        "UPDATE project_files_version " +
            "SET downloads = (downloads + 1) " +
            "WHERE item_id = ? " +
            "AND version = ? ");
    pst.setInt(1, itemId);
    pst.setDouble(2, version);
    pst.executeUpdate();
    pst.close();
    //Track bandwidth used for downloads
    String sql =
        "INSERT INTO usage_log " +
            "(enteredby, action, record_id, record_size) VALUES (?, ?, ?, ?) ";
    int i = 0;
    pst = db.prepareStatement(sql);
    DatabaseUtils.setInt(pst, ++i, userId);
    pst.setInt(++i, 2);
    pst.setInt(++i, itemId);
    pst.setInt(++i, fileSize);
    pst.execute();
    pst.close();
    //Track each download by user (if not a guest)
    if (userId < 0) {
      return false;
    }
    sql =
        "INSERT INTO project_files_download " +
            "(item_id, version, user_download_id) VALUES (?, ?, ?) ";
    i = 0;
    pst = db.prepareStatement(sql);
    pst.setInt(++i, itemId);
    pst.setDouble(++i, version);
    pst.setInt(++i, userId);
    pst.execute();
    pst.close();
    return true;
  }
}

