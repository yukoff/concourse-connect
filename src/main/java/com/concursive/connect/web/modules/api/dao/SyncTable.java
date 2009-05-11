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
import com.concursive.commons.xml.XMLUtils;
import org.w3c.dom.Element;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Represents an entry in the sync_table database tableName, used for mapping XML
 * object names to Java classes during an XML Transaction
 *
 * @author matt rajkowski
 * @version $Id$
 * @created June 24, 2002
 */
public class SyncTable {

  // object properties
  private int id = -1;
  private int systemId = -1;
  private String name = null;
  private String mappedClassName = null;
  private java.sql.Timestamp entered = null;
  private java.sql.Timestamp modified = null;
  private String createStatement = null;
  private int orderId = -1;
  private boolean syncItem = false;
  private String key = null;
  // in xml mapping, needs to be added to database if continued to be used
  private String tableName = null;
  private String uniqueField = null;
  private String sortBy = null;
  // build properties
  private boolean buildTextFields = true;


  /**
   * Constructor for the SyncTable object
   */
  public SyncTable() {
  }


  /**
   * Constructor for the SyncTable object
   *
   * @param rs Description of Parameter
   * @throws SQLException Description of Exception
   */
  public SyncTable(ResultSet rs) throws SQLException {
    buildRecord(rs);
  }

  public SyncTable(Element e) {
    // Populate from XML
    name = e.getAttribute("id");
    mappedClassName = e.getAttribute("class");
    Element guid = XMLUtils.getFirstChild(e, "property", "alias", "guid");
    if (guid != null) {
      key = XMLUtils.getNodeText(guid);
    }
    tableName = e.getAttribute("table");
    uniqueField = e.getAttribute("uniqueField");
    sortBy = e.getAttribute("sortBy");
  }


  /**
   * Looks up a table_id for the given System ID and Class Name.
   *
   * @param db        Description of Parameter
   * @param className Description of Parameter
   * @param systemId  Description of the Parameter
   * @return Description of the Returned Value
   * @throws SQLException Description of the Exception
   */
  public static int lookupTableId(Connection db, int systemId, String className) throws SQLException {
    int tableId = -1;
    String sql =
        "SELECT table_id " +
            "FROM sync_table " +
            "WHERE system_id = ? " +
            "AND mapped_class_name = ? ";
    PreparedStatement pst = db.prepareStatement(sql);
    pst.setInt(1, systemId);
    pst.setString(2, className);
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      tableId = rs.getInt("table_id");
    }
    rs.close();
    pst.close();
    return tableId;
  }


  /**
   * Sets the id attribute of the SyncTable object
   *
   * @param tmp The new id value
   */
  public void setId(int tmp) {
    this.id = tmp;
  }


  /**
   * Sets the systemId attribute of the SyncTable object
   *
   * @param tmp The new systemId value
   */
  public void setSystemId(int tmp) {
    this.systemId = tmp;
  }


  /**
   * Sets the name attribute of the SyncTable object
   *
   * @param tmp The new name value
   */
  public void setName(String tmp) {
    this.name = tmp;
  }


  /**
   * Sets the mappedClassName attribute of the SyncTable object
   *
   * @param tmp The new mappedClassName value
   */
  public void setMappedClassName(String tmp) {
    this.mappedClassName = tmp;
  }


  /**
   * Sets the key attribute of the SyncTable object
   *
   * @param tmp The new key value
   */
  public void setKey(String tmp) {
    this.key = tmp;
  }


  /**
   * Sets the entered attribute of the SyncTable object
   *
   * @param tmp The new entered value
   */
  public void setEntered(java.sql.Timestamp tmp) {
    this.entered = tmp;
  }


  /**
   * Sets the modified attribute of the SyncTable object
   *
   * @param tmp The new modified value
   */
  public void setModified(java.sql.Timestamp tmp) {
    this.modified = tmp;
  }


  /**
   * Sets the createStatement attribute of the SyncTable object
   *
   * @param tmp The new createStatement value
   */
  public void setCreateStatement(String tmp) {
    this.createStatement = tmp;
  }


  /**
   * Sets the orderId attribute of the SyncTable object
   *
   * @param tmp The new orderId value
   */
  public void setOrderId(int tmp) {
    this.orderId = tmp;
  }


  /**
   * Sets the buildTextFields attribute of the SyncTable object
   *
   * @param tmp The new buildTextFields value
   */
  public void setBuildTextFields(boolean tmp) {
    this.buildTextFields = tmp;
  }


  /**
   * Gets the id attribute of the SyncTable object
   *
   * @return The id value
   */
  public int getId() {
    return id;
  }


  /**
   * Gets the systemId attribute of the SyncTable object
   *
   * @return The systemId value
   */
  public int getSystemId() {
    return systemId;
  }


  /**
   * Gets the name attribute of the SyncTable object
   *
   * @return The name value
   */
  public String getName() {
    return name;
  }


  /**
   * Gets the mappedClassName attribute of the SyncTable object
   *
   * @return The mappedClassName value
   */
  public String getMappedClassName() {
    return mappedClassName;
  }


  /**
   * Gets the key attribute of the SyncTable object
   *
   * @return The key value
   */
  public String getKey() {
    return key;
  }


  /**
   * Gets the entered attribute of the SyncTable object
   *
   * @return The entered value
   */
  public java.sql.Timestamp getEntered() {
    return entered;
  }


  /**
   * Gets the modified attribute of the SyncTable object
   *
   * @return The modified value
   */
  public java.sql.Timestamp getModified() {
    return modified;
  }


  /**
   * Gets the createStatement attribute of the SyncTable object
   *
   * @return The createStatement value
   */
  public String getCreateStatement() {
    return createStatement;
  }


  /**
   * Gets the orderId attribute of the SyncTable object
   *
   * @return The orderId value
   */
  public int getOrderId() {
    return orderId;
  }


  /**
   * Gets the syncItem attribute of the SyncTable object
   *
   * @return The syncItem value
   */
  public boolean getSyncItem() {
    return syncItem;
  }


  /**
   * Gets the buildTextFields attribute of the SyncTable object
   *
   * @return The buildTextFields value
   */
  public boolean getBuildTextFields() {
    return buildTextFields;
  }

  public String getTableName() {
    return tableName;
  }

  public void setTableName(String tableName) {
    this.tableName = tableName;
  }

  public String getUniqueField() {
    return uniqueField;
  }

  public void setUniqueField(String uniqueField) {
    this.uniqueField = uniqueField;
  }

  public String getSortBy() {
    return sortBy;
  }

  public void setSortBy(String sortBy) {
    this.sortBy = sortBy;
  }

  /**
   * Description of the Method
   *
   * @param rs Description of Parameter
   * @throws SQLException Description of Exception
   */
  public void buildRecord(ResultSet rs) throws SQLException {
    id = rs.getInt("table_id");
    systemId = rs.getInt("system_id");
    name = rs.getString("element_name");
    mappedClassName = rs.getString("mapped_class_name");
    entered = rs.getTimestamp("entered");
    modified = rs.getTimestamp("modified");
    if (buildTextFields) {
      createStatement = rs.getString("create_statement");
    }
    orderId = rs.getInt("order_id");
    syncItem = rs.getBoolean("sync_item");
    key = rs.getString("object_key");
  }

  public void insert(Connection db) throws SQLException {
    id = DatabaseUtils.getNextSeq(db, "sync_table_table_id_seq", id);
    PreparedStatement pst = db.prepareStatement(
        "INSERT INTO sync_table " +
            "(" + (id > -1 ? "table_id, " : "") + "system_id, element_name, mapped_class_name, create_statement, order_id, sync_item, object_key" + ") " +
            "VALUES (" + (id > -1 ? "?, " : "") + "?, ?, ?, ?, ?, ?, ?) ");
    int i = 0;
    if (id > -1) {
      pst.setInt(++i, id);
    }
    pst.setInt(++i, systemId);
    pst.setString(++i, name);
    pst.setString(++i, mappedClassName);
    pst.setString(++i, createStatement);
    pst.setInt(++i, orderId);
    pst.setBoolean(++i, syncItem);
    pst.setString(++i, key);
    pst.execute();
    id = DatabaseUtils.getCurrVal(db, "sync_table_table_id_seq", id);
  }
}

