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

import com.concursive.commons.date.DateUtils;
import com.concursive.commons.db.DatabaseUtils;
import com.concursive.commons.text.StringUtils;

import java.sql.*;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Description of the Class
 *
 * @author matt rajkowski
 * @created September 16, 2004
 */
public class CustomLookupElement extends HashMap<String, CustomLookupColumn> {

  protected int id = -1;
  protected String tableName = null;
  protected String uniqueField = null;

  protected String currentField = null;
  protected String currentValue = null;
  protected String currentType = null;

  protected java.sql.Timestamp entered = null;
  protected java.sql.Timestamp modified = null;


  /**
   * Constructor for the CustomLookupElement object
   */
  public CustomLookupElement() {
  }


  /**
   * Constructor for the CustomLookupElement object
   *
   * @param rs Description of the Parameter
   * @throws java.sql.SQLException Description of the Exception
   */
  public CustomLookupElement(ResultSet rs) throws java.sql.SQLException {
    build(rs);
  }


  /**
   * Constructor for the CustomLookupElement object
   *
   * @param db          Description of the Parameter
   * @param code        Description of the Parameter
   * @param tableName   Description of the Parameter
   * @param uniqueField Description of the Parameter
   * @throws java.sql.SQLException Description of the Exception
   */
  public CustomLookupElement(Connection db, int code, String tableName, String uniqueField) throws java.sql.SQLException {
    if (System.getProperty("DEBUG") != null) {
      System.out.println(
          "CustomLookupElement-> Retrieving ID: " + code + " from table: " + tableName);
    }
    String sql =
        "SELECT " + uniqueField + " " +
            "FROM " + tableName + " " +
            "WHERE " + uniqueField + " = ? ";
    PreparedStatement pst = db.prepareStatement(sql);
    pst.setInt(1, code);
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      build(rs);
    } else {
      throw new java.sql.SQLException("ID not found");
    }
    rs.close();
    pst.close();
  }


  /**
   * Dynamically build the properties from the database
   *
   * @param rs Description of the Parameter
   * @throws java.sql.SQLException Description of the Exception
   */
  public void build(ResultSet rs) throws java.sql.SQLException {
    ResultSetMetaData meta = rs.getMetaData();
    if (meta.getColumnCount() == 1) {
      id = rs.getInt(1);
    } else {
      for (int i = 1; i < meta.getColumnCount() + 1; i++) {
        String columnName = meta.getColumnName(i);
        int columnType = meta.getColumnType(i);
        String data = null;
        if (columnType == Types.CLOB || columnType == Types.BLOB || columnType == Types.LONGVARCHAR) {
          data = rs.getString(i);
          columnType = Types.VARCHAR;
        } else {
          Object obj = rs.getObject(i);
          if (obj != null) {
            data = String.valueOf(obj);
          }
        }
        CustomLookupColumn thisColumn =
            new CustomLookupColumn(columnName, data, columnType);
        this.put(columnName, thisColumn);
      }
    }
  }


  /**
   * Adds a feature to the Field attribute of the CustomLookupElement object
   *
   * @param fieldName The feature to be added to the Field attribute
   * @param value     The feature to be added to the Field attribute
   * @param type      The feature to be added to the Field attribute
   */
  public void addField(String fieldName, String value, int type) {
    CustomLookupColumn thisColumn =
        new CustomLookupColumn(fieldName, value, type);
    this.put(fieldName, thisColumn);
  }


  /**
   * Sets the tableName attribute of the CustomLookupElement object
   *
   * @param tmp The new tableName value
   */
  public void setTableName(String tmp) {
    this.tableName = tmp;
  }


  /**
   * Sets the uniqueField attribute of the CustomLookupElement object
   *
   * @param tmp The new uniqueField value
   */
  public void setUniqueField(String tmp) {
    this.uniqueField = tmp;
  }


  /**
   * Sets the id attribute of the CustomLookupElement object
   *
   * @param tmp The new id value
   */
  public void setId(int tmp) {
    this.id = tmp;
  }


  /**
   * Sets the id attribute of the CustomLookupElement object
   *
   * @param tmp The new id value
   */
  public void setId(String tmp) {
    this.id = Integer.parseInt(tmp);
  }


  /**
   * Sets the field attribute of the CustomLookupElement object
   *
   * @param tmp The new field value
   */
  public void setField(String tmp) {
    currentField = tmp;
  }


  /**
   * Sets the data attribute of the CustomLookupElement object
   *
   * @param tmp The new data value
   */
  public void setData(String tmp) {
    currentValue = tmp;
  }


  /**
   * Gets the currentType attribute of the CustomLookupElement object
   *
   * @return The currentType value
   */
  public String getCurrentType() {
    return currentType;
  }


  /**
   * Sets the currentType attribute of the CustomLookupElement object
   *
   * @param tmp The new currentType value
   */
  public void setType(String tmp) {
    this.currentType = tmp;
    addProperty();
  }


  /**
   * Adds a feature to the Property attribute of the CustomLookupElement object
   */
  private void addProperty() {
    if (!"code".equals(currentField) && !"guid".equals(currentField)) {
      if (currentField != null && currentValue != null && currentType != null) {
        CustomLookupColumn thisColumn =
            new CustomLookupColumn(currentField, currentValue,
                Integer.parseInt(currentType));
        this.put(currentField, thisColumn);
      }
    }
    currentField = null;
    currentValue = null;
    currentType = null;
  }


  /**
   * Sets the serverMapId attribute of the CustomLookupElement object
   *
   * @param value The new serverMapId value
   */
  public void setServerMapId(String value) {
    String field = value.substring(0, value.indexOf("="));
    String recordId = value.substring(value.indexOf("=") + 1);
    CustomLookupColumn thisColumn = this.get(field);
    if (thisColumn != null) {
      thisColumn.setValue(recordId);
    }
  }


  /**
   * Gets the id attribute of the CustomLookupElement object
   *
   * @return The id value
   */
  public int getId() {
    if (uniqueField != null && getValue(uniqueField) != null) {
      return Integer.parseInt(getValue(uniqueField));
    }
    return id;
  }


  /**
   * Gets the tableName attribute of the CustomLookupElement object
   *
   * @return The tableName value
   */
  public String getTableName() {
    return tableName;
  }


  /**
   * Gets the uniqueField attribute of the CustomLookupElement object
   *
   * @return The uniqueField value
   */
  public String getUniqueField() {
    return uniqueField;
  }


  /**
   * Gets the value attribute of the CustomLookupElement object
   *
   * @param tmp Description of the Parameter
   * @return The value value
   */
  public String getValue(String tmp) {
    CustomLookupColumn thisColumn = this.get(tmp);
    if (thisColumn != null) {
      return thisColumn.getValue();
    }
    return null;
  }


  /**
   * Gets the type attribute of the CustomLookupElement object
   *
   * @param tmp Description of the Parameter
   * @return The type value
   */
  public int getType(String tmp) {
    CustomLookupColumn thisColumn = this.get(tmp);
    if (thisColumn != null) {
      return thisColumn.getType();
    }
    return -1;
  }


  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  public boolean insert(Connection db) throws SQLException {
    if (tableName == null) {
      throw new SQLException("Table name not specified");
    }
    if (this.size() == 0) {
      throw new SQLException("Fields not specified");
    }

    String seqName = null;
    if (id < 0) {
      if (this.getUniqueField() != null && !"".equals(this.getUniqueField().trim())) {
        seqName = getPostgresSeqName(tableName, getUniqueField());
        id = DatabaseUtils.getNextSeq(db, seqName, id);
      }
    }
    //tableName = DatabaseUtils.getTableName(db, tableName);

    StringBuffer sql = new StringBuffer();
    sql.append("INSERT INTO " + tableName + " ");
    sql.append("(");
    if (this.getUniqueField() != null && id > -1) {
      sql.append(this.getUniqueField() + ", ");
    }
    Iterator fields = this.keySet().iterator();
    while (fields.hasNext()) {
      sql.append(DatabaseUtils.parseReservedWord(db, (String) fields.next()));
      if (fields.hasNext()) {
        sql.append(", ");
      }
    }
    sql.append(") VALUES (");
    if (this.getUniqueField() != null && id > -1) {
      sql.append("?, ");
    }
    for (int i = 0; i < this.size(); i++) {
      sql.append("?");
      if (i < this.size() - 1) {
        sql.append(",");
      }
    }
    sql.append(")");
    PreparedStatement pst = db.prepareStatement(sql.toString());
    int paramCount = 0;
    if (this.getUniqueField() != null && id > -1) {
      pst.setInt(++paramCount, id);
    }
    Iterator paramters = this.keySet().iterator();
    while (paramters.hasNext()) {
      String paramName = ((String) paramters.next());

      CustomLookupColumn thisColumn = this.get(paramName);

      //This code needs to be maintained. If support for new column types are
      //required, then the corresponding "else if" needs to be added.
      if (thisColumn.getType() == java.sql.Types.CHAR ||
          thisColumn.getType() == java.sql.Types.VARCHAR ||
          thisColumn.getType() == java.sql.Types.LONGVARCHAR) {
        pst.setString(++paramCount, thisColumn.getValue());
      } else if (thisColumn.getType() == java.sql.Types.INTEGER) {
        DatabaseUtils.setInt(pst, ++paramCount,
            StringUtils.hasText(thisColumn.getValue()) ?
                Integer.parseInt(thisColumn.getValue()) : -1);
      } else if (thisColumn.getType() == java.sql.Types.DOUBLE) {
        DatabaseUtils.setDouble(pst, ++paramCount,
            StringUtils.hasText(thisColumn.getValue()) ?
                Double.parseDouble(thisColumn.getValue()) : -1.0);
      } else if (thisColumn.getType() == java.sql.Types.BOOLEAN ||
          thisColumn.getType() == java.sql.Types.BIT) {
        pst.setBoolean(++paramCount,
            StringUtils.hasText(thisColumn.getValue()) ?
                DatabaseUtils.parseBoolean(thisColumn.getValue()) : false);
      } else if (thisColumn.getType() == java.sql.Types.TIMESTAMP) {
        DatabaseUtils.setTimestamp(pst, ++paramCount,
            StringUtils.hasText(thisColumn.getValue()) ?
                DateUtils.parseTimestampString(thisColumn.getValue()) :
                new java.sql.Timestamp(System.currentTimeMillis()));
      }
    }
    pst.execute();
    pst.close();
    if (id < 0) {
      if (this.getUniqueField() != null && !"".equals(this.getUniqueField().trim())) {
        id = DatabaseUtils.getCurrVal(db, seqName, id);
      }
    }
    return true;
  }


  /**
   * Gets the sequenceName attribute of the CustomLookupElement object
   *
   * @param tableName   Description of the Parameter
   * @param uniqueField Description of the Parameter
   * @return The sequenceName value
   */
  private String getPostgresSeqName(String tableName, String uniqueField) {
    String seqName = null;
    if (tableName.length() > 22) {
      seqName = tableName.substring(0, 22) + "_" + uniqueField + "_seq";
    } else {
      seqName = tableName + "_" + uniqueField + "_seq";
    }
    return seqName;
  }

  public boolean delete(Connection db) throws SQLException {
    if (tableName == null) {
      throw new SQLException("Table name not specified");
    }
    if (uniqueField == null) {
      throw new SQLException("UniqueField not specified");
    }
    if (id == -1) {
      throw new SQLException("Id not specified");
    }
    PreparedStatement pst = db.prepareStatement(
        "DELETE FROM " + tableName + " WHERE " + uniqueField + " = ? "
    );
    pst.setInt(1, id);
    int count = pst.executeUpdate();
    pst.close();
    return count == 1;
  }
}

