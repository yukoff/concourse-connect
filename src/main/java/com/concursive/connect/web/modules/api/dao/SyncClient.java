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

package com.concursive.connect.web.modules.api.dao;

import com.concursive.commons.db.DatabaseUtils;
import com.concursive.commons.web.mvc.actions.ActionContext;
import com.concursive.commons.web.mvc.beans.GenericBean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A SyncClient represents a uniquely identifiable system that is performing
 * synchronization with the server. The server maintains specific information
 * about clients as well.
 *
 * @author matt rajkowski
 * @version $Id$
 * @created April 10, 2002
 */
public class SyncClient extends GenericBean {

  private int id = -1;
  private String type = null;
  private String version = null;
  private java.sql.Timestamp entered = null;
  private int enteredBy = -1;
  private java.sql.Timestamp modified = null;
  private int modifiedBy = -1;
  private java.sql.Timestamp anchor = null;
  private boolean enabled = false;
  private String code = null;

  /**
   * Constructor for the SyncClient object
   */
  public SyncClient() {
  }

  /**
   * Constructor for the SyncClient object
   *
   * @param rs Description of Parameter
   * @throws SQLException Description of Exception
   */
  public SyncClient(ResultSet rs) throws SQLException {
    buildRecord(rs);
  }

  /**
   * Constructor for the SyncClient object
   *
   * @param db       Description of Parameter
   * @param clientId Description of Parameter
   * @throws SQLException Description of Exception
   */
  public SyncClient(Connection db, int clientId, String code) throws SQLException {
    if (System.getProperty("DEBUG") != null) {
      System.out.println("SyncClient-> Looking up: " + clientId);
    }
    PreparedStatement pst = null;
    ResultSet rs = null;
    StringBuffer sql = new StringBuffer();
    sql.append(
        "SELECT * " +
            "FROM sync_client " +
            "WHERE client_id = ? " +
            "AND code = ? ");
    pst = db.prepareStatement(sql.toString());
    pst.setInt(1, clientId);
    pst.setString(2, code);
    rs = pst.executeQuery();
    if (rs.next()) {
      buildRecord(rs);
    }
    rs.close();
    pst.close();
    if (id == -1) {
      throw new SQLException("Sync Client record not found.");
    }
  }

  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  public boolean checkNormalSync(Connection db) throws SQLException {
    boolean result = false;
    PreparedStatement pst = null;
    ResultSet rs = null;
    StringBuffer sql = new StringBuffer();
    sql.append(
        "SELECT * " +
            "FROM sync_client " +
            "WHERE client_id = ? ");
    if (anchor == null) {
      sql.append("AND anchor is null ");
    } else {
      sql.append("AND anchor = ? ");
    }
    pst = db.prepareStatement(sql.toString());
    pst.setInt(1, id);
    if (anchor != null) {
      pst.setTimestamp(2, anchor);
    }
    rs = pst.executeQuery();
    if (rs.next()) {
      result = true;
    } else {
      result = false;
    }
    rs.close();
    pst.close();
    return result;
  }

  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  public boolean updateSyncAnchor(Connection db) throws SQLException {
    PreparedStatement pst = null;
    StringBuffer sql = new StringBuffer();
    sql.append(
        "UPDATE sync_client " +
            "SET anchor = ? " +
            "WHERE client_id = ? ");
    pst = db.prepareStatement(sql.toString());
    pst.setTimestamp(1, anchor);
    pst.setInt(2, id);
    pst.executeUpdate();
    pst.close();
    return true;
  }

  /**
   * Sets the id attribute of the SyncClient object
   *
   * @param tmp The new id value
   */
  public void setId(int tmp) {
    this.id = tmp;
  }

  /**
   * Sets the id attribute of the SyncClient object
   *
   * @param tmp The new id value
   */
  public void setId(String tmp) {
    this.id = Integer.parseInt(tmp);
  }

  /**
   * Sets the type attribute of the SyncClient object
   *
   * @param tmp The new type value
   */
  public void setType(String tmp) {
    this.type = tmp;
  }

  /**
   * Sets the version attribute of the SyncClient object
   *
   * @param tmp The new version value
   */
  public void setVersion(String tmp) {
    this.version = tmp;
  }

  /**
   * Sets the entered attribute of the SyncClient object
   *
   * @param tmp The new entered value
   */
  public void setEntered(java.sql.Timestamp tmp) {
    this.entered = tmp;
  }

  /**
   * Sets the enteredBy attribute of the SyncClient object
   *
   * @param tmp The new enteredBy value
   */
  public void setEnteredBy(int tmp) {
    this.enteredBy = tmp;
  }

  /**
   * Sets the enteredBy attribute of the SyncClient object
   *
   * @param tmp The new enteredBy value
   */
  public void setEnteredBy(String tmp) {
    this.enteredBy = Integer.parseInt(tmp);
  }

  /**
   * Sets the modified attribute of the SyncClient object
   *
   * @param tmp The new modified value
   */
  public void setModified(java.sql.Timestamp tmp) {
    this.modified = tmp;
  }

  /**
   * Sets the modifiedBy attribute of the SyncClient object
   *
   * @param tmp The new modifiedBy value
   */
  public void setModifiedBy(int tmp) {
    this.modifiedBy = tmp;
  }

  /**
   * Sets the modifiedBy attribute of the SyncClient object
   *
   * @param tmp The new modifiedBy value
   */
  public void setModifiedBy(String tmp) {
    this.modifiedBy = Integer.parseInt(tmp);
  }

  /**
   * Sets the anchor attribute of the SyncClient object
   *
   * @param tmp The new anchor value
   */
  public void setAnchor(java.sql.Timestamp tmp) {
    this.anchor = tmp;
  }

  /**
   * Gets the id attribute of the SyncClient object
   *
   * @return The id value
   */
  public int getId() {
    return id;
  }

  /**
   * Gets the type attribute of the SyncClient object
   *
   * @return The type value
   */
  public String getType() {
    return type;
  }

  /**
   * Gets the version attribute of the SyncClient object
   *
   * @return The version value
   */
  public String getVersion() {
    return version;
  }

  /**
   * Gets the entered attribute of the SyncClient object
   *
   * @return The entered value
   */
  public java.sql.Timestamp getEntered() {
    return entered;
  }

  /**
   * Gets the enteredBy attribute of the SyncClient object
   *
   * @return The enteredBy value
   */
  public int getEnteredBy() {
    return enteredBy;
  }

  /**
   * Gets the modified attribute of the SyncClient object
   *
   * @return The modified value
   */
  public java.sql.Timestamp getModified() {
    return modified;
  }

  /**
   * Gets the modifiedBy attribute of the SyncClient object
   *
   * @return The modifiedBy value
   */
  public int getModifiedBy() {
    return modifiedBy;
  }

  /**
   * Gets the anchor attribute of the SyncClient object
   *
   * @return The anchor value
   */
  public java.sql.Timestamp getAnchor() {
    return anchor;
  }

  /**
   * Gets the enabled attribute of the SyncClient object
   *
   * @return The enabled value
   */
  public boolean getEnabled() {
    return enabled;
  }

  /**
   * Sets the enabled attribute of the SyncClient object
   *
   * @param tmp The new enabled value
   */
  public void setEnabled(boolean tmp) {
    this.enabled = tmp;
  }

  /**
   * Sets the enabled attribute of the SyncClient object
   *
   * @param tmp The new enabled value
   */
  public void setEnabled(String tmp) {
    this.enabled = DatabaseUtils.parseBoolean(tmp);
  }

  /**
   * Gets the code attribute of the SyncClient object
   *
   * @return The code value
   */
  public String getCode() {
    return code;
  }

  /**
   * Sets the code attribute of the SyncClient object
   *
   * @param tmp The new code value
   */
  public void setCode(String tmp) {
    this.code = tmp;
  }

  /**
   * Description of the Method
   *
   * @param db Description of Parameter
   * @return Description of the Returned Value
   * @throws SQLException Description of Exception
   */
  public boolean insert(Connection db) throws SQLException {
    id = DatabaseUtils.getNextSeq(db, "sync_client_client_id_seq", id);
    PreparedStatement pst = db.prepareStatement(
        "INSERT INTO sync_client " +
            "(" + (id > -1 ? "client_id, " : "") + "\"type\", version, enteredby, modifiedby, code) " +
            "VALUES (" + (id > -1 ? "?, " : "") + "?, ?, ?, ?, ?) ");
    int i = 0;
    if (id > -1) {
      pst.setInt(++i, id);
    }
    pst.setString(++i, type);
    pst.setString(++i, version);
    pst.setInt(++i, this.getEnteredBy());
    pst.setInt(++i, this.getEnteredBy());
    pst.setString(++i, code);
    pst.execute();
    pst.close();
    id = DatabaseUtils.getCurrVal(db, "sync_client_client_id_seq", id);
    return true;
  }

  /**
   * Description of the Method
   *
   * @param db Description of Parameter
   * @return Description of the Returned Value
   * @throws SQLException Description of Exception
   */
  public boolean delete(Connection db) throws SQLException {
    if (id == -1) {
      throw new SQLException("ID was not specified");
    }

    PreparedStatement pst = null;
    //Delete related records (mappings)

    //Delete the record
    int recordCount = 0;
    pst = db.prepareStatement(
        "DELETE FROM sync_client " +
            "WHERE client_id = ? ");
    pst.setInt(1, id);
    recordCount = pst.executeUpdate();
    pst.close();

    if (recordCount == 0) {
      errors.put(
          "actionError",
          "Sync Client could not be deleted because it no longer exists.");
      return false;
    } else {
      return true;
    }
  }

  /**
   * Description of the Method
   *
   * @param db      Description of Parameter
   * @param context Description of Parameter
   * @return Description of the Returned Value
   * @throws SQLException Description of Exception
   */
  public int update(Connection db, ActionContext context) throws SQLException {
    if (this.getId() == -1) {
      throw new SQLException("Call ID was not specified");
    }

    int resultCount = 0;
    PreparedStatement pst = null;
    StringBuffer sql = new StringBuffer();
    sql.append(
        "UPDATE sync_client " +
            "SET \"type\" = ?, version = ?, modifiedby = ?, " +
            "modified = CURRENT_TIMESTAMP " +
            "WHERE client_id = ? " +
            "AND modified = ? ");
    int i = 0;
    pst = db.prepareStatement(sql.toString());
    pst.setString(++i, type);
    pst.setString(++i, version);
    pst.setInt(++i, modifiedBy);
    pst.setInt(++i, id);
    pst.setTimestamp(++i, this.getModified());
    resultCount = pst.executeUpdate();
    pst.close();
    return resultCount;
  }

  /**
   * Description of the Method
   *
   * @param rs Description of Parameter
   * @throws SQLException Description of Exception
   */
  protected void buildRecord(ResultSet rs) throws SQLException {
    id = rs.getInt("client_id");
    type = rs.getString("type");
    version = rs.getString("version");
    entered = rs.getTimestamp("entered");
    enteredBy = rs.getInt("enteredby");
    modified = rs.getTimestamp("modified");
    modifiedBy = rs.getInt("modifiedby");
    anchor = rs.getTimestamp("anchor");
    enabled = rs.getBoolean("enabled");
    code = rs.getString("code");
  }

}
