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

package com.concursive.connect.web.modules.translation.dao;

import com.concursive.commons.db.DatabaseUtils;
import com.concursive.commons.text.Template;
import com.concursive.commons.web.mvc.beans.GenericBean;

import java.sql.*;
import java.util.Iterator;
import java.util.List;

/**
 * Description of the Class
 *
 * @author Ananth
 * @created April 11, 2005
 */
public class LanguageDictionary extends GenericBean {
  private int id = -1;
  private int configId = -1;
  private String paramName = null;
  private String paramValue1 = null;
  private String paramValue2 = null;
  private int approved = UNDECIDED;
  private Timestamp entered = null;
  private Timestamp modified = null;
  private int enteredBy = -1;
  private int modifiedBy = -1;
  private String configName = null;

  //resources
  private String defaultValue = null;
  private boolean buildDefaultValue = false;

  // approval status
  public static final int UNDECIDED = -1;
  public static final int APPROVED = 1;
  public static final int NOT_APPROVED = 2;


  /**
   * Gets the buildDefaultValue attribute of the LanguageDictionary object
   *
   * @return The buildDefaultValue value
   */
  public boolean getBuildDefaultValue() {
    return buildDefaultValue;
  }


  /**
   * Sets the buildDefaultValue attribute of the LanguageDictionary object
   *
   * @param tmp The new buildDefaultValue value
   */
  public void setBuildDefaultValue(boolean tmp) {
    this.buildDefaultValue = tmp;
  }


  /**
   * Sets the buildDefaultValue attribute of the LanguageDictionary object
   *
   * @param tmp The new buildDefaultValue value
   */
  public void setBuildDefaultValue(String tmp) {
    this.buildDefaultValue = DatabaseUtils.parseBoolean(tmp);
  }


  /**
   * Gets the defaultValue attribute of the LanguageDictionary object
   *
   * @return The defaultValue value
   */
  public String getDefaultValue() {
    return defaultValue;
  }


  /**
   * Sets the defaultValue attribute of the LanguageDictionary object
   *
   * @param tmp The new defaultValue value
   */
  public void setDefaultValue(String tmp) {
    this.defaultValue = tmp;
  }


  /**
   * Gets the approved attribute of the LanguageDictionary object
   *
   * @return The approved value
   */
  public int getApproved() {
    return approved;
  }


  /**
   * Sets the approved attribute of the LanguageDictionary object
   *
   * @param tmp The new approved value
   */
  public void setApproved(int tmp) {
    this.approved = tmp;
  }


  /**
   * Sets the approved attribute of the LanguageDictionary object
   *
   * @param tmp The new approved value
   */
  public void setApproved(String tmp) {
    this.approved = Integer.parseInt(tmp);
  }


  /**
   * Gets the id attribute of the LanguageDictionary object
   *
   * @return The id value
   */
  public int getId() {
    return id;
  }


  /**
   * Sets the id attribute of the LanguageDictionary object
   *
   * @param tmp The new id value
   */
  public void setId(int tmp) {
    this.id = tmp;
  }


  /**
   * Sets the id attribute of the LanguageDictionary object
   *
   * @param tmp The new id value
   */
  public void setId(String tmp) {
    this.id = Integer.parseInt(tmp);
  }


  /**
   * Gets the configId attribute of the LanguageDictionary object
   *
   * @return The configId value
   */
  public int getConfigId() {
    return configId;
  }


  /**
   * Sets the configId attribute of the LanguageDictionary object
   *
   * @param tmp The new configId value
   */
  public void setConfigId(int tmp) {
    this.configId = tmp;
  }


  /**
   * Sets the configId attribute of the LanguageDictionary object
   *
   * @param tmp The new configId value
   */
  public void setConfigId(String tmp) {
    this.configId = Integer.parseInt(tmp);
  }


  /**
   * Gets the paramName attribute of the LanguageDictionary object
   *
   * @return The paramName value
   */
  public String getParamName() {
    return paramName;
  }


  /**
   * Sets the paramName attribute of the LanguageDictionary object
   *
   * @param tmp The new paramName value
   */
  public void setParamName(String tmp) {
    this.paramName = tmp;
  }


  /**
   * Gets the paramValue1 attribute of the LanguageDictionary object
   *
   * @return The paramValue1 value
   */
  public String getParamValue1() {
    return paramValue1;
  }


  /**
   * Sets the paramValue1 attribute of the LanguageDictionary object
   *
   * @param tmp The new paramValue1 value
   */
  public void setParamValue1(String tmp) {
    this.paramValue1 = tmp;
  }


  /**
   * Gets the paramValue2 attribute of the LanguageDictionary object
   *
   * @return The paramValue2 value
   */
  public String getParamValue2() {
    return paramValue2;
  }


  /**
   * Sets the paramValue2 attribute of the LanguageDictionary object
   *
   * @param tmp The new paramValue2 value
   */
  public void setParamValue2(String tmp) {
    this.paramValue2 = tmp;
  }


  /**
   * Gets the entered attribute of the LanguageDictionary object
   *
   * @return The entered value
   */
  public Timestamp getEntered() {
    return entered;
  }


  /**
   * Sets the entered attribute of the LanguageDictionary object
   *
   * @param tmp The new entered value
   */
  public void setEntered(Timestamp tmp) {
    this.entered = tmp;
  }


  /**
   * Sets the entered attribute of the LanguageDictionary object
   *
   * @param tmp The new entered value
   */
  public void setEntered(String tmp) {
    this.entered = DatabaseUtils.parseTimestamp(tmp);
  }


  /**
   * Gets the modified attribute of the LanguageDictionary object
   *
   * @return The modified value
   */
  public Timestamp getModified() {
    return modified;
  }


  /**
   * Sets the modified attribute of the LanguageDictionary object
   *
   * @param tmp The new modified value
   */
  public void setModified(Timestamp tmp) {
    this.modified = tmp;
  }


  /**
   * Sets the modified attribute of the LanguageDictionary object
   *
   * @param tmp The new modified value
   */
  public void setModified(String tmp) {
    this.modified = DatabaseUtils.parseTimestamp(tmp);
  }


  /**
   * Gets the enteredBy attribute of the LanguageDictionary object
   *
   * @return The enteredBy value
   */
  public int getEnteredBy() {
    return enteredBy;
  }


  /**
   * Sets the enteredBy attribute of the LanguageDictionary object
   *
   * @param tmp The new enteredBy value
   */
  public void setEnteredBy(int tmp) {
    this.enteredBy = tmp;
  }


  /**
   * Sets the enteredBy attribute of the LanguageDictionary object
   *
   * @param tmp The new enteredBy value
   */
  public void setEnteredBy(String tmp) {
    this.enteredBy = Integer.parseInt(tmp);
  }


  /**
   * Gets the modifiedBy attribute of the LanguageDictionary object
   *
   * @return The modifiedBy value
   */
  public int getModifiedBy() {
    return modifiedBy;
  }


  /**
   * Sets the modifiedBy attribute of the LanguageDictionary object
   *
   * @param tmp The new modifiedBy value
   */
  public void setModifiedBy(int tmp) {
    this.modifiedBy = tmp;
  }


  /**
   * Sets the modifiedBy attribute of the LanguageDictionary object
   *
   * @param tmp The new modifiedBy value
   */
  public void setModifiedBy(String tmp) {
    this.modifiedBy = Integer.parseInt(tmp);
  }

  public String getConfigName() {
    return configName;
  }

  public void setConfigName(String configName) {
    this.configName = configName;
  }


  /**
   * Constructor for the LanguageDictionary object
   */
  public LanguageDictionary() {
  }


  /**
   * Constructor for the LanguageDictionary object
   *
   * @param rs Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public LanguageDictionary(ResultSet rs) throws SQLException {
    buildRecord(rs);
  }


  /**
   * Constructor for the LanguageDictionary object
   *
   * @param db Description of the Parameter
   * @param id Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public LanguageDictionary(Connection db, int id) throws SQLException {
    queryRecord(db, id);
  }


  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @param id Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public void queryRecord(Connection db, int id) throws SQLException {
    if (id == -1) {
      throw new SQLException("Invalid id specified");
    }
    PreparedStatement pst = db.prepareStatement(
        "SELECT ld.*, lc.config_name " +
            "FROM language_dictionary ld " +
            "LEFT JOIN language_config lc ON ld.config_id = lc.id " +
            "WHERE ld.id = ? ");
    pst.setInt(1, id);
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      buildRecord(rs);
    }
    rs.close();
    pst.close();
    if (buildDefaultValue) {
      this.buildDefaultValue(db);
    }
  }


  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public void buildDefaultValue(Connection db) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "SELECT ld.param_value1 AS defaultvalue " +
            "FROM language_dictionary ld " +
            "WHERE ld.param_name = ? " +
            "AND ld.config_id IN " +
            "(SELECT id FROM language_config " +
            "WHERE config_name = ? AND language_id = ?) ");
    pst.setString(1, this.getParamName());
    pst.setString(2, this.getConfigName());
    pst.setInt(3, LanguagePack.getLanguagePackId(db, LanguagePack.DEFAULT_LOCALE));
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      defaultValue = rs.getString("defaultvalue");
    }
    rs.close();
    pst.close();
  }


  /**
   * Description of the Method
   *
   * @param rs Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  protected void buildRecord(ResultSet rs) throws SQLException {
    id = rs.getInt("id");
    configId = rs.getInt("config_id");
    paramName = rs.getString("param_name");
    paramValue1 = rs.getString("param_value1");
    paramValue2 = rs.getString("param_value2");
    approved = rs.getInt("approved");
    entered = rs.getTimestamp("entered");
    enteredBy = rs.getInt("enteredby");
    modified = rs.getTimestamp("modified");
    modifiedBy = rs.getInt("modifiedby");
    configName = rs.getString("config_name");
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
    sql.append("INSERT INTO language_dictionary (config_id, param_name, param_value1, param_value2, approved, ");
    if (entered != null) {
      sql.append("entered, ");
    }
    sql.append("enteredby, ");
    if (modified != null) {
      sql.append("modified, ");
    }
    sql.append("modifiedby) ");

    sql.append("VALUES (?, ?, ?, ?, ?, ");
    if (entered != null) {
      sql.append("?, ");
    }
    sql.append("?, ");
    if (modified != null) {
      sql.append("?, ");
    }
    sql.append("?) ");
    int i = 0;
    PreparedStatement pst = db.prepareStatement(sql.toString());
    pst.setInt(++i, this.getConfigId());
    pst.setString(++i, this.getParamName());
    pst.setString(++i, this.getParamValue1());
    pst.setString(++i, this.getParamValue2());
    pst.setInt(++i, this.getApproved());
    if (entered != null) {
      pst.setTimestamp(++i, this.getEntered());
    }
    pst.setInt(++i, this.getEnteredBy());
    if (modified != null) {
      pst.setTimestamp(++i, this.getModified());
    }
    pst.setInt(++i, this.getModifiedBy());
    pst.execute();
    pst.close();
    id = DatabaseUtils.getCurrVal(db, "language_dictionary_id_seq", -1);
    return true;
  }


  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  public int update(Connection db) throws SQLException {
    int resultCount = -1;
    if (this.getId() == -1) {
      throw new SQLException("ID not specified");
    }
    PreparedStatement pst = null;
    StringBuffer sql = new StringBuffer();
    sql.append(
        "UPDATE language_dictionary " +
            "SET config_id = ?, param_name = ?, param_value1 = ?, " +
            "param_value2 = ?, approved = ?, ");
    sql.append("modified = " + DatabaseUtils.getCurrentTimestamp(db) + ", modifiedby = ? ");
    sql.append("WHERE id = ? ");
    int i = 0;
    pst = db.prepareStatement(sql.toString());
    DatabaseUtils.setInt(pst, ++i, this.getConfigId());
    pst.setString(++i, this.getParamName());
    pst.setString(++i, this.getParamValue1());
    pst.setString(++i, this.getParamValue2());
    pst.setInt(++i, this.getApproved());
    pst.setInt(++i, this.getModifiedBy());
    pst.setInt(++i, this.getId());
    resultCount = pst.executeUpdate();
    pst.close();
    return resultCount;
  }


  /**
   * Gets the valid attribute of the LanguageDictionary object
   *
   * @return The valid value
   */
  public boolean isValid() {
    if (this.getDefaultValue() != null && this.getParamValue1() != null) {
      if (!"".equals(this.getParamValue1().trim())) {
        Template srcTemplate = new Template(this.getDefaultValue());
        Template destTemplate = new Template(this.getParamValue1());
        List var1 = srcTemplate.getVariables();
        List var2 = destTemplate.getVariables();
        if (var1.size() != var2.size()) {
          return false;
        } else {
          Iterator i = var1.iterator();
          Iterator j = var2.iterator();
          while (i.hasNext() && j.hasNext()) {
            String variable1 = (String) i.next();
            String variable2 = (String) j.next();
            if (!variable1.equals(variable2)) {
              return false;
            }
          }
        }
      }
    }
    return true;
  }
}

