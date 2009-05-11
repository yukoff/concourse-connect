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

package com.concursive.connect.web.utils;

import com.concursive.commons.db.DatabaseUtils;
import com.concursive.commons.web.mvc.beans.GenericBean;
import com.concursive.connect.Constants;
import com.concursive.connect.cache.utils.CacheUtils;

import java.sql.*;

/**
 * Represents an item from a Lookup table, to be used primarily with HtmlSelect
 * objects and the LookupList object.
 *
 * @author matt rajkowski
 * @created September 5, 2001
 */
public class LookupElement extends GenericBean {

  protected String tableName = null;
  protected int code = 0;
  protected String description = "";
  protected boolean defaultItem = false;
  protected int level = 0;
  protected boolean enabled = true;
  protected java.sql.Timestamp entered = null;
  protected java.sql.Timestamp modified = null;
  protected int groupId = -1;
  protected int fieldId = -1;
  protected boolean group = false;


  /**
   * Constructor for the LookupElement object
   *
   * @since 1.1
   */
  public LookupElement() {
  }


  /**
   * Constructor for the LookupElement object
   *
   * @param db        Description of the Parameter
   * @param code      Description of the Parameter
   * @param tableName Description of the Parameter
   * @throws java.sql.SQLException Description of the Exception
   */
  public LookupElement(Connection db, int code, String tableName) throws java.sql.SQLException {
    if (System.getProperty("DEBUG") != null) {
      System.out.println("LookupElement-> Retrieving ID: " + code + " from table: " + tableName);
    }
    String sql =
        "SELECT code, description, default_item, level, enabled " +
            "FROM " + tableName + " " +
            "WHERE code = ? ";
    PreparedStatement pst = db.prepareStatement(sql);
    pst.setInt(1, code);
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      build(rs);
    } else {
      throw new java.sql.SQLException("ID not found");
    }
  }


  /**
   * Constructor for the LookupElement object
   *
   * @param rs Description of Parameter
   * @throws java.sql.SQLException Description of Exception
   * @since 1.1
   */
  public LookupElement(ResultSet rs) throws java.sql.SQLException {
    build(rs);
  }


  /**
   * Description of the Method
   *
   * @param rs Description of the Parameter
   * @throws java.sql.SQLException Description of the Exception
   */
  public void build(ResultSet rs) throws java.sql.SQLException {
    code = rs.getInt("code");
    description = rs.getString("description");
    defaultItem = rs.getBoolean("default_item");
    level = rs.getInt("level");
    //startDate = rs.getTimestamp("start_date");
    //endDate = rs.getTimestamp("end_date");
    enabled = rs.getBoolean("enabled");
    if (!(this.getEnabled())) {
      description += " (X)";
    }
    //not guaranteed to be here
    //entered = rs.getTimestamp("entered");
    //modified = rs.getTimestamp("modified");
  }


  /**
   * Sets the tableName attribute of the LookupElement object
   *
   * @param tmp The new tableName value
   */
  public void setTableName(String tmp) {
    this.tableName = tmp;
  }


  /**
   * Sets the newOrder attribute of the LookupElement object
   *
   * @param db        The new newOrder value
   * @param tableName The new newOrder value
   * @return Description of the Returned Value
   * @throws SQLException Description of Exception
   */
  public int setNewOrder(Connection db, String tableName) throws SQLException {
    int resultCount = 0;
    if (this.getCode() == 0) {
      throw new SQLException("Element Code not specified.");
    }
    PreparedStatement pst = null;
    StringBuffer sql = new StringBuffer();
    sql.append(
        "UPDATE " + tableName + " " +
            "SET level = ? " +
            "WHERE code = ? ");
    int i = 0;
    pst = db.prepareStatement(sql.toString());
    pst.setInt(++i, this.getLevel());
    pst.setInt(++i, this.getCode());
    resultCount = pst.executeUpdate();
    pst.close();
    CacheUtils.invalidateValue(Constants.SYSTEM_LOOKUP_LIST_CACHE, tableName);
    return resultCount;
  }


  /**
   * Sets the Code attribute of the LookupElement object
   *
   * @param tmp The new Code value
   * @since 1.1
   */
  public void setCode(int tmp) {
    this.code = tmp;
  }


  /**
   * Sets the code attribute of the LookupElement object
   *
   * @param tmp The new code value
   */
  public void setCode(String tmp) {
    this.code = Integer.parseInt(tmp);
  }


  /**
   * Sets the id attribute of the LookupElement object
   *
   * @param tmp The new id value
   */
  public void setId(int tmp) {
    this.code = tmp;
  }


  /**
   * Sets the id attribute of the LookupElement object
   *
   * @param tmp The new id value
   */
  public void setId(String tmp) {
    this.code = Integer.parseInt(tmp);
  }


  /**
   * Sets the Description attribute of the LookupElement object
   *
   * @param tmp The new Description value
   * @since 1.1
   */
  public void setDescription(String tmp) {
    this.description = tmp;
  }


  /**
   * Sets the DefaultItem attribute of the LookupElement object
   *
   * @param tmp The new DefaultItem value
   * @since 1.2
   */
  public void setDefaultItem(boolean tmp) {
    this.defaultItem = tmp;
  }


  /**
   * Sets the defaultItem attribute of the LookupElement object
   *
   * @param tmp The new defaultItem value
   */
  public void setDefaultItem(String tmp) {
    this.defaultItem = ("true".equalsIgnoreCase(tmp) || "on".equalsIgnoreCase(tmp));
  }


  /**
   * Sets the Level attribute of the LookupElement object
   *
   * @param tmp The new Level value
   * @since 1.2
   */
  public void setLevel(int tmp) {
    this.level = tmp;
  }


  /**
   * Sets the level attribute of the LookupElement object
   *
   * @param tmp The new level value
   */
  public void setLevel(String tmp) {
    this.level = Integer.parseInt(tmp);
  }


  /**
   * Sets the Enabled attribute of the LookupElement object
   *
   * @param tmp The new Enabled value
   * @since 1.1
   */
  public void setEnabled(boolean tmp) {
    this.enabled = tmp;
  }


  /**
   * Sets the enabled attribute of the LookupElement object
   *
   * @param tmp The new enabled value
   */
  public void setEnabled(String tmp) {
    this.enabled = ("true".equalsIgnoreCase(tmp) || "on".equalsIgnoreCase(tmp));
  }


  public void setGroupId(int groupId) {
    this.groupId = groupId;
  }

  /**
   * Sets the fieldId attribute of the LookupElement object
   *
   * @param tmp The new fieldId value
   */
  public void setFieldId(int tmp) {
    this.fieldId = tmp;
  }


  /**
   * Sets the fieldId attribute of the LookupElement object
   *
   * @param tmp The new fieldId value
   */
  public void setFieldId(String tmp) {
    this.fieldId = Integer.parseInt(tmp);
  }


  /**
   * Sets the group attribute of the LookupElement object
   *
   * @param tmp The new group value
   */
  public void setGroup(boolean tmp) {
    this.group = tmp;
  }


  /**
   * Sets the group attribute of the LookupElement object
   *
   * @param tmp The new group value
   */
  public void setGroup(String tmp) {
    this.group = DatabaseUtils.parseBoolean(tmp);
  }


  /**
   * Gets the tableName attribute of the LookupElement object
   *
   * @return The tableName value
   */
  public String getTableName() {
    return tableName;
  }


  /**
   * Gets the Code attribute of the LookupElement object
   *
   * @return The Code value
   * @since 1.1
   */
  public int getCode() {
    return code;
  }


  /**
   * Returns the code in String form for use in reflection.
   *
   * @return The codeString value
   */
  public String getCodeString() {
    return String.valueOf(code);
  }


  /**
   * Gets the id attribute of the LookupElement object, id is a required name
   * for some reflection parsing
   *
   * @return The id value
   */
  public int getId() {
    return code;
  }


  /**
   * Gets the Description attribute of the LookupElement object
   *
   * @return The Description value
   * @since 1.1
   */
  public String getDescription() {
    return description;
  }


  /**
   * Gets the DefaultItem attribute of the LookupElement object
   *
   * @return The DefaultItem value
   * @since 1.2
   */
  public boolean getDefaultItem() {
    return defaultItem;
  }


  /**
   * Gets the Level attribute of the LookupElement object
   *
   * @return The Level value
   * @since 1.2
   */
  public int getLevel() {
    return level;
  }


  /**
   * Gets the Enabled attribute of the LookupElement object
   *
   * @return The Enabled value
   * @since 1.1
   */
  public boolean getEnabled() {
    return enabled;
  }


  /**
   * Gets the modified attribute of the LookupElement object
   *
   * @return The modified value
   */
  public java.sql.Timestamp getModified() {
    if (modified == null) {
      return (new java.sql.Timestamp(new java.util.Date().getTime()));
    } else {
      return modified;
    }
  }


  public int getGroupId() {
    return groupId;
  }

  /**
   * Gets the fieldId attribute of the LookupElement object
   *
   * @return The fieldId value
   */
  public int getFieldId() {
    return fieldId;
  }


  /**
   * Gets the group attribute of the LookupElement object
   *
   * @return The group value
   */
  public boolean isGroup() {
    return group;
  }


  /**
   * Gets the group attribute of the LookupElement object
   *
   * @return The group value
   */
  public boolean getGroup() {
    return group;
  }

  public Timestamp getEntered() {
    return entered;
  }

  public void setEntered(Timestamp entered) {
    this.entered = entered;
  }

  public void setEntered(String entered) {
    this.entered = DatabaseUtils.parseTimestamp(entered);
  }

  public void setModified(Timestamp modified) {
    this.modified = modified;
  }

  public void setModified(String modified) {
    this.modified = DatabaseUtils.parseTimestamp(modified);
  }

  /**
   * Description of the Method
   *
   * @param db        Description of Parameter
   * @param tableName Description of Parameter
   * @return Description of the Returned Value
   * @throws SQLException Description of Exception
   */
  public int disableElement(Connection db, String tableName) throws SQLException {
    int resultCount = 0;
    if (this.getCode() == 0) {
      throw new SQLException("Element Code not specified.");
    }
    PreparedStatement pst = null;
    StringBuffer sql = new StringBuffer();
    sql.append(
        "UPDATE " + tableName + " " +
            "SET enabled = ? " +
            "WHERE code = ? ");
    int i = 0;
    pst = db.prepareStatement(sql.toString());
    pst.setBoolean(++i, false);
    pst.setInt(++i, this.getCode());
    resultCount = pst.executeUpdate();
    pst.close();
    CacheUtils.invalidateValue(Constants.SYSTEM_LOOKUP_LIST_CACHE, tableName);
    return resultCount;
  }


  /**
   * Description of the Method
   *
   * @param db        Description of Parameter
   * @param tableName Description of Parameter
   * @return Description of the Returned Value
   * @throws SQLException Description of Exception
   */
  public boolean insertElement(Connection db, String tableName) throws SQLException {
    return insertElement(db, tableName, -1);
  }


  /**
   * Description of the Method
   *
   * @param db        Description of Parameter
   * @param tableName Description of Parameter
   * @param fieldId   Description of Parameter
   * @return Description of the Returned Value
   * @throws SQLException Description of Exception
   */
  public boolean insertElement(Connection db, String tableName, int fieldId) throws SQLException {
    this.tableName = tableName;
    this.fieldId = fieldId;
    return insert(db);
  }

  public int update(Connection db) throws SQLException {
    int updateCount = -1;
    PreparedStatement pst = db.prepareStatement(
        "UPDATE " + tableName + " " +
            "SET description = ?, level = ?, enabled = ? " +
            "WHERE code = ?"
    );
    int i = 0;
    pst.setString(++i, this.getDescription());
    pst.setInt(++i, this.getLevel());
    pst.setBoolean(++i, this.getEnabled());
    pst.setInt(++i, this.getId());
    updateCount = pst.executeUpdate();
    pst.close();
    CacheUtils.invalidateValue(Constants.SYSTEM_LOOKUP_LIST_CACHE, tableName);
    return updateCount;
  }

  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  public boolean insert(Connection db) throws SQLException {
    StringBuffer sql = new StringBuffer();
    int i = 0;
    sql.append(
        "INSERT INTO " + tableName + " " +
            "(" + (groupId > -1 ? "group_id, " : "") + "description, level, enabled" + (fieldId > -1 ? ", field_id" : "") + ") " +
            "VALUES (" + (groupId > -1 ? "?, " : "") + "?, ?, ?" + (fieldId > -1 ? ", ?" : "") + ") ");
    i = 0;
    PreparedStatement pst = db.prepareStatement(sql.toString());
    if (groupId > -1) {
      pst.setInt(++i, groupId);
    }
    pst.setString(++i, this.getDescription());
    pst.setInt(++i, this.getLevel());
    pst.setBoolean(++i, true);
    if (fieldId > -1) {
      pst.setInt(++i, fieldId);
    }
    pst.execute();
    pst.close();

    String seqName = null;
    if (tableName.length() > 22) {
      seqName = tableName.substring(0, 22);
    } else {
      seqName = tableName;
    }
    code = DatabaseUtils.getCurrVal(db, seqName + "_code_seq", -1);
    return true;
  }
}

