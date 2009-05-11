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
import com.concursive.connect.web.modules.translation.beans.TranslationSearchBean;
import com.concursive.connect.web.utils.PagedListInfo;

import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Description of the Class
 *
 * @author Ananth
 * @created April 11, 2005
 */
public class LanguageDictionaryList extends ArrayList {
  private PagedListInfo pagedListInfo = null;
  private int configId = -1;
  private int languagePackId = -1;
  private boolean buildEmptyPhrasesOnly = false;
  private boolean buildTranslatedPhrasesOnly = false;
  private boolean buildDefaultValue = false;
  private int modifiedBy = -1;
  private int ignoreFromLanguagePackId = -1;
  private boolean ignoreApproved = false;
  private Timestamp approvedAfter = null;
  private TranslationSearchBean searchBean = null;
  private String paramName = null;

  /**
   * Gets the buildTranslatedPhrasesOnly attribute of the
   * LanguageDictionaryList object
   *
   * @return The buildTranslatedPhrasesOnly value
   */
  public boolean getBuildTranslatedPhrasesOnly() {
    return buildTranslatedPhrasesOnly;
  }


  /**
   * Sets the buildTranslatedPhrasesOnly attribute of the
   * LanguageDictionaryList object
   *
   * @param tmp The new buildTranslatedPhrasesOnly value
   */
  public void setBuildTranslatedPhrasesOnly(boolean tmp) {
    this.buildTranslatedPhrasesOnly = tmp;
  }


  /**
   * Sets the buildTranslatedPhrasesOnly attribute of the
   * LanguageDictionaryList object
   *
   * @param tmp The new buildTranslatedPhrasesOnly value
   */
  public void setBuildTranslatedPhrasesOnly(String tmp) {
    this.buildTranslatedPhrasesOnly = DatabaseUtils.parseBoolean(tmp);
  }


  /**
   * Gets the buildDefaultValue attribute of the LanguageDictionaryList object
   *
   * @return The buildDefaultValue value
   */
  public boolean getBuildDefaultValue() {
    return buildDefaultValue;
  }


  /**
   * Sets the buildDefaultValue attribute of the LanguageDictionaryList object
   *
   * @param tmp The new buildDefaultValue value
   */
  public void setBuildDefaultValue(boolean tmp) {
    this.buildDefaultValue = tmp;
  }


  /**
   * Sets the buildDefaultValue attribute of the LanguageDictionaryList object
   *
   * @param tmp The new buildDefaultValue value
   */
  public void setBuildDefaultValue(String tmp) {
    this.buildDefaultValue = DatabaseUtils.parseBoolean(tmp);
  }


  /**
   * Gets the buildEmptyPhrasesOnly attribute of the LanguageDictionaryList
   * object
   *
   * @return The buildEmptyPhrasesOnly value
   */
  public boolean getBuildEmptyPhrasesOnly() {
    return buildEmptyPhrasesOnly;
  }


  /**
   * Sets the buildEmptyPhrasesOnly attribute of the LanguageDictionaryList
   * object
   *
   * @param tmp The new buildEmptyPhrasesOnly value
   */
  public void setBuildEmptyPhrasesOnly(boolean tmp) {
    this.buildEmptyPhrasesOnly = tmp;
  }


  /**
   * Sets the buildEmptyPhrasesOnly attribute of the LanguageDictionaryList
   * object
   *
   * @param tmp The new buildEmptyPhrasesOnly value
   */
  public void setBuildEmptyPhrasesOnly(String tmp) {
    this.buildEmptyPhrasesOnly = DatabaseUtils.parseBoolean(tmp);
  }


  /**
   * Gets the pagedListInfo attribute of the LanguageDictionaryList object
   *
   * @return The pagedListInfo value
   */
  public PagedListInfo getPagedListInfo() {
    return pagedListInfo;
  }


  /**
   * Sets the pagedListInfo attribute of the LanguageDictionaryList object
   *
   * @param tmp The new pagedListInfo value
   */
  public void setPagedListInfo(PagedListInfo tmp) {
    this.pagedListInfo = tmp;
  }


  /**
   * Gets the configId attribute of the LanguageDictionaryList object
   *
   * @return The configId value
   */
  public int getConfigId() {
    return configId;
  }


  /**
   * Sets the configId attribute of the LanguageDictionaryList object
   *
   * @param tmp The new configId value
   */
  public void setConfigId(int tmp) {
    this.configId = tmp;
  }


  /**
   * Sets the configId attribute of the LanguageDictionaryList object
   *
   * @param tmp The new configId value
   */
  public void setConfigId(String tmp) {
    this.configId = Integer.parseInt(tmp);
  }


  /**
   * Gets the languagePackId attribute of the LanguageDictionaryList object
   *
   * @return The languagePackId value
   */
  public int getLanguagePackId() {
    return languagePackId;
  }


  /**
   * Sets the languagePackId attribute of the LanguageDictionaryList object
   *
   * @param tmp The new languagePackId value
   */
  public void setLanguagePackId(int tmp) {
    this.languagePackId = tmp;
  }


  /**
   * Sets the languagePackId attribute of the LanguageDictionaryList object
   *
   * @param tmp The new languagePackId value
   */
  public void setLanguagePackId(String tmp) {
    this.languagePackId = Integer.parseInt(tmp);
  }

  public int getModifiedBy() {
    return modifiedBy;
  }

  public void setModifiedBy(int modifiedBy) {
    this.modifiedBy = modifiedBy;
  }

  public int getIgnoreFromLanguagePackId() {
    return ignoreFromLanguagePackId;
  }

  public void setIgnoreFromLanguagePackId(int ignoreFromLanguagePackId) {
    this.ignoreFromLanguagePackId = ignoreFromLanguagePackId;
  }

  public String getParamName() {
    return paramName;
  }

  public void setParamName(String paramName) {
    this.paramName = paramName;
  }

  /**
   * Constructor for the LanguageDictionaryList object
   */
  public LanguageDictionaryList() {
  }


  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public void buildList(Connection db) throws SQLException {
    PreparedStatement pst = null;
    ResultSet rs = null;
    int items = -1;
    StringBuffer sqlSelect = new StringBuffer();
    StringBuffer sqlCount = new StringBuffer();
    StringBuffer sqlFilter = new StringBuffer();
    StringBuffer sqlOrder = new StringBuffer();
    //Need to build a base SQL statement for counting records
    sqlCount.append(
        "SELECT COUNT(*) AS recordcount " +
            "FROM language_dictionary ld " +
            "WHERE ld.id > 0 ");
    createFilter(sqlFilter, db);
    if (pagedListInfo != null) {
      //Get the total number of records matching filter
      pst = db.prepareStatement(sqlCount.toString() +
          sqlFilter.toString());
      items = prepareFilter(pst);
      rs = pst.executeQuery();
      if (rs.next()) {
        int maxRecords = rs.getInt("recordcount");
        System.out.println("MAX RECORDS: " + maxRecords);
        pagedListInfo.setMaxRecords(maxRecords);
      }
      rs.close();
      pst.close();
      //Determine the offset, based on the filter, for the first record to show
      if (!pagedListInfo.getCurrentLetter().equals("")) {
        pst = db.prepareStatement(sqlCount.toString() +
            sqlFilter.toString() +
            "AND lower(ld.param_name) < ? ");
        items = prepareFilter(pst);
        pst.setString(++items, pagedListInfo.getCurrentLetter().toLowerCase());
        rs = pst.executeQuery();
        if (rs.next()) {
          int offsetCount = rs.getInt("recordcount");
          pagedListInfo.setCurrentOffset(offsetCount);
        }
        rs.close();
        pst.close();
      }
      //Determine column to sort by
      pagedListInfo.setDefaultSort("ld.param_name", null);
      pagedListInfo.appendSqlTail(db, sqlOrder);
    } else {
      sqlOrder.append("ORDER BY ld.param_name ");
    }

    //Need to build a base SQL statement for returning records
    if (pagedListInfo != null) {
      pagedListInfo.appendSqlSelectHead(db, sqlSelect);
    } else {
      sqlSelect.append("SELECT ");
    }
    sqlSelect.append(
        "ld.*, lc.config_name " +
            "FROM language_dictionary ld " +
            "LEFT JOIN language_config lc ON ld.config_id = lc.id " +
            "WHERE ld.id > 0 ");
    pst = db.prepareStatement(sqlSelect.toString() + sqlFilter.toString() + sqlOrder.toString());
    items = prepareFilter(pst);
    rs = pst.executeQuery();
    if (pagedListInfo != null) {
      pagedListInfo.doManualOffset(db, rs);
    }
    int count = 0;
    while (rs.next()) {
      if (pagedListInfo != null && pagedListInfo.getItemsPerPage() > 0 &&
          DatabaseUtils.getType(db) == DatabaseUtils.MSSQL &&
          count >= pagedListInfo.getItemsPerPage()) {
        break;
      }
      ++count;
      LanguageDictionary thisItem = new LanguageDictionary(rs);
      if (buildDefaultValue) {
        thisItem.buildDefaultValue(db);
      }
      //this.put(thisItem.getParamName(), thisItem);
      this.add(thisItem);
    }
    rs.close();
    pst.close();
  }


  /**
   * Description of the Method
   *
   * @param sqlFilter Description of the Parameter
   * @param db        Description of the Parameter
   */
  private void createFilter(StringBuffer sqlFilter, Connection db) throws SQLException {
    if (configId > -1) {
      sqlFilter.append("AND ld.config_id = ? ");
    }
    if (languagePackId > -1) {
      sqlFilter.append("AND ld.config_id IN (SELECT id FROM language_config WHERE language_id = ?) ");
    }
    if (buildEmptyPhrasesOnly) {
      sqlFilter.append("AND (ld.param_value1 IS NULL OR ld.param_value1 = '') ");
    }
    if (buildTranslatedPhrasesOnly) {
      sqlFilter.append("AND ld.param_value1 <> '' AND ld.param_value1 IS NOT NULL ");
    }
    if (ignoreFromLanguagePackId > -1) {
      sqlFilter.append(
          "AND ld.param_name NOT IN " +
              "(SELECT param_name FROM language_dictionary lda " +
              "WHERE lda.config_id IN (SELECT id FROM language_config WHERE language_id = ?)) ");
    }
    if (ignoreApproved) {
      if (approvedAfter != null) {
        sqlFilter.append("AND (ld.approved <> ? OR ld.modified > ?) ");
      } else {
        sqlFilter.append("AND ld.approved <> ? ");
      }
    }
    if (searchBean != null) {
      if (hasValue(searchBean.getDefaultWord())) {
        // TODO: This should look for the param_value1 in the default dictionary and match the config_name and param_name 
        sqlFilter.append("AND lower(ld.param_value1) LIKE lower(?) ");
      }
      if (hasValue(searchBean.getTranslatedWord())) {
        sqlFilter.append("AND lower(ld.param_value1) LIKE lower(?) ");
      }
      if (hasValue(searchBean.getLanguageParam())) {
        sqlFilter.append("AND lower(ld.param_name) LIKE lower(?) ");
      }
    }
    if (paramName != null) {
      sqlFilter.append("AND ld.param_name = ? ");
    }
  }


  /**
   * Description of the Method
   *
   * @param pst Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  private int prepareFilter(PreparedStatement pst) throws SQLException {
    int i = 0;
    if (configId > -1) {
      pst.setInt(++i, configId);
    }
    if (languagePackId > -1) {
      pst.setInt(++i, languagePackId);
    }
    if (ignoreFromLanguagePackId > -1) {
      pst.setInt(++i, ignoreFromLanguagePackId);
    }
    if (ignoreApproved) {
      pst.setInt(++i, LanguageDictionary.APPROVED);
      if (approvedAfter != null) {
        pst.setTimestamp(++i, approvedAfter);
      }
    }
    if (searchBean != null) {
      if (hasValue(searchBean.getDefaultWord())) {
        pst.setString(++i, "%" + searchBean.getDefaultWord().toLowerCase() + "%");
      }
      if (hasValue(searchBean.getTranslatedWord())) {
        pst.setString(++i, "%" + searchBean.getTranslatedWord().toLowerCase() + "%");
      }
      if (hasValue(searchBean.getLanguageParam())) {
        pst.setString(++i, "%" + searchBean.getLanguageParam().toLowerCase() + "%");
      }
    }
    if (paramName != null) {
      pst.setString(++i, paramName);
    }
    return i;
  }


  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public void update(Connection db) throws SQLException {
    //Iterator i = this.keySet().iterator();
    Iterator i = this.iterator();
    while (i.hasNext()) {
      //String key = (String) i.next();
      LanguageDictionary thisItem = (LanguageDictionary) i.next();
      if (thisItem.getParamValue1() != null && !"".equals(thisItem.getParamValue1())) {
        thisItem.setModifiedBy(modifiedBy);
        thisItem.update(db);
      }
    }
    // Update the modified in language_pack table
    PreparedStatement pst = db.prepareStatement(
        "UPDATE language_pack " +
            "SET modified = " + DatabaseUtils.getCurrentTimestamp(db) + " " +
            "WHERE id = ? ");
    pst.setInt(1, languagePackId);
    pst.executeUpdate();
    pst.close();
  }


  public static int queryTotalCount(Connection db, int languagePackId) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "SELECT COUNT(*) AS lang_count " +
            "FROM language_dictionary " +
            "WHERE config_id IN (SELECT id FROM language_config WHERE language_id = ?) ");
    pst.setInt(1, languagePackId);
    ResultSet rs = pst.executeQuery();
    rs.next();
    int count = rs.getInt("lang_count");
    rs.close();
    pst.close();
    return count;
  }

  public static int queryTranslatedCount(Connection db, int languagePackId) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "SELECT COUNT(*) AS lang_count " +
            "FROM language_dictionary " +
            "WHERE config_id IN (SELECT id FROM language_config WHERE language_id = ?) " +
            "AND param_value1 <> '' AND param_value1 IS NOT NULL ");
    pst.setInt(1, languagePackId);
    ResultSet rs = pst.executeQuery();
    rs.next();
    int count = rs.getInt("lang_count");
    rs.close();
    pst.close();
    return count;
  }

  public static int queryDefaultId(Connection db, int dictionaryItemId) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "SELECT id " +
            "FROM language_dictionary " +
            "WHERE param_name IN (SELECT param_name FROM language_dictionary WHERE id = ?) " +
            "AND config_id IN (SELECT id FROM language_config WHERE language_id IN " +
            "(SELECT id FROM language_pack WHERE language_locale = ?)) ");
    pst.setInt(1, dictionaryItemId);
    pst.setString(2, LanguagePack.DEFAULT_LOCALE);
    ResultSet rs = pst.executeQuery();
    rs.next();
    int id = rs.getInt("id");
    rs.close();
    pst.close();
    return id;
  }

  public void setIgnoreApproved(boolean ignoreApproved) {
    this.ignoreApproved = ignoreApproved;
  }

  public void setApprovedAfter(Timestamp lastLogin) {
    this.approvedAfter = lastLogin;
  }

  public void setSearchBean(TranslationSearchBean searchBean) {
    this.searchBean = searchBean;
  }

  public static String lookupValue(Connection db, int languageId, String configName, String paramName, int valueId) throws SQLException {
    String value1 = null;
    String value2 = null;
    PreparedStatement pst = db.prepareStatement(
        "SELECT param_value1, param_value2 " +
            "FROM language_dictionary " +
            "WHERE param_name = ? " +
            "AND config_id IN " +
            "(SELECT id FROM language_config WHERE config_name = ? AND language_id IN" +
            " (SELECT id FROM language_pack WHERE language_id = ?)) ");
    pst.setString(1, paramName);
    pst.setString(2, configName);
    pst.setInt(3, languageId);
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      value1 = rs.getString("param_value1");
      value2 = rs.getString("param_value2");
    }
    rs.close();
    pst.close();
    switch (valueId) {
      case 1:
        return value1;
      case 2:
        return value2;
      default:
        return null;
    }
  }

  private boolean hasValue(String value) {
    return value != null && !"".equals(value.trim());
  }
}

