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

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * A generic class that contains a list of LookupElement objects.
 *
 * @author matt rajkowski
 * @version $Id$
 * @created September 7, 2001
 */
public class LookupList extends ArrayList<LookupElement> {

  public static String uniqueField = "code";
  public String tableName = null;
  protected String jsEvent = null;
  protected int selectSize = 1;
  protected String selectStyle = null;
  protected boolean multiple = false;
  protected java.sql.Timestamp lastAnchor = null;
  protected java.sql.Timestamp nextAnchor = null;
  protected boolean showDisabledFlag = true;
  protected PagedListInfo pagedListInfo = null;
  protected HashMap selectedItems = null;


  /**
   * Constructor for the LookupList object. Generates an empty list, which is
   * not very useful.
   *
   * @since 1.1
   */
  public LookupList() {
  }


  /**
   * Builds a list of elements based on the database connection and the table
   * name specified for the lookup. Only retrieves "enabled" items at this
   * time.
   *
   * @param db        Description of Parameter
   * @param thisTable Description of Parameter
   * @throws SQLException Description of Exception
   */
  public LookupList(Connection db, String thisTable) throws SQLException {
    tableName = thisTable;
    buildList(db);
  }


  /**
   * Constructor for the LookupList object
   *
   * @param vals  Description of Parameter
   * @param names Description of Parameter
   */
  public LookupList(String[] vals, String[] names) {

    for (int i = 0; i < vals.length; i++) {
      LookupElement thisElement = new LookupElement();
      thisElement.setDescription(names[i]);

      //as long as it is not a new entry

      if (!(vals[i].startsWith("*"))) {
        thisElement.setCode(Integer.parseInt(vals[i]));
      }

      thisElement.setLevel(i);
      this.add(thisElement);
    }
  }


  /**
   * Gets the pagedListInfo attribute of the LookupList object
   *
   * @return The pagedListInfo value
   */
  public PagedListInfo getPagedListInfo() {
    return pagedListInfo;
  }


  /**
   * Sets the pagedListInfo attribute of the LookupList object
   *
   * @param pagedListInfo The new pagedListInfo value
   */
  public void setPagedListInfo(PagedListInfo pagedListInfo) {
    this.pagedListInfo = pagedListInfo;
  }


  /**
   * Gets the selectedItems attribute of the LookupList object
   *
   * @return The selectedItems value
   */
  public HashMap getSelectedItems() {
    return selectedItems;
  }


  /**
   * Sets the selectedItems attribute of the LookupList object
   *
   * @param tmp The new selectedItems value
   */
  public void setSelectedItems(HashMap tmp) {
    this.selectedItems = tmp;
  }


  /**
   * Constructor for the LookupList object
   *
   * @param db      Description of Parameter
   * @param table   Description of Parameter
   * @param fieldId Description of Parameter
   * @throws SQLException Description of Exception
   */
  public LookupList(Connection db, String table, int fieldId) throws SQLException {
    if (System.getProperty("DEBUG") != null) {
      System.out.println("LookupList-> " + table + ": " + fieldId);
    }
    Statement st = null;
    ResultSet rs = null;

    StringBuffer sql = new StringBuffer();
    sql.append(
        "SELECT * " +
            "FROM " + table + " " +
            "WHERE field_id = " + fieldId + " " +
            "AND CURRENT_TIMESTAMP > start_date " +
            "AND (CURRENT_TIMESTAMP < end_date OR end_date IS NULL) " +
            "ORDER BY level, description ");
    st = db.createStatement();
    rs = st.executeQuery(sql.toString());
    while (rs.next()) {
      LookupElement thisElement = new LookupElement(rs);
      thisElement.setTableName(table);
      this.add(thisElement);
    }
    rs.close();
    st.close();
  }


  /**
   * Sets the showDisabledFlag attribute of the LookupList object
   *
   * @param showDisabledFlag The new showDisabledFlag value
   */
  public void setShowDisabledFlag(boolean showDisabledFlag) {
    this.showDisabledFlag = showDisabledFlag;
  }


  /**
   * Gets the showDisabledFlag attribute of the LookupList object
   *
   * @return The showDisabledFlag value
   */
  public boolean getShowDisabledFlag() {
    return showDisabledFlag;
  }


  /**
   * Sets the table attribute of the LookupList object
   *
   * @param tmp The new table value
   */
  public void setTable(String tmp) {
    this.tableName = tmp;
  }


  /**
   * Sets the tableName attribute of the LookupList object
   *
   * @param tmp The new tableName value
   */
  public void setTableName(String tmp) {
    this.tableName = tmp;
  }


  /**
   * Sets the lastAnchor attribute of the LookupList object
   *
   * @param tmp The new lastAnchor value
   */
  public void setLastAnchor(java.sql.Timestamp tmp) {
    this.lastAnchor = tmp;
  }


  /**
   * Sets the lastAnchor attribute of the LookupList object
   *
   * @param tmp The new lastAnchor value
   */
  public void setLastAnchor(String tmp) {
    this.lastAnchor = java.sql.Timestamp.valueOf(tmp);
  }


  /**
   * Sets the nextAnchor attribute of the LookupList object
   *
   * @param tmp The new nextAnchor value
   */
  public void setNextAnchor(java.sql.Timestamp tmp) {
    this.nextAnchor = tmp;
  }


  /**
   * Sets the nextAnchor attribute of the LookupList object
   *
   * @param tmp The new nextAnchor value
   */
  public void setNextAnchor(String tmp) {
    this.nextAnchor = java.sql.Timestamp.valueOf(tmp);
  }


  /**
   * Sets the Multiple attribute of the LookupList object
   *
   * @param multiple The new Multiple value
   */
  public void setMultiple(boolean multiple) {
    this.multiple = multiple;
  }


  /**
   * Sets the JsEvent attribute of the LookupList object
   *
   * @param tmp The new JsEvent value
   */
  public void setJsEvent(String tmp) {
    this.jsEvent = tmp;
  }


  /**
   * Sets the SelectSize attribute of the LookupList object
   *
   * @param tmp The new SelectSize value
   */
  public void setSelectSize(int tmp) {
    this.selectSize = tmp;
  }


  /**
   * Sets the selectStyle attribute of the LookupList object
   *
   * @param tmp The new selectStyle value
   */
  public void setSelectStyle(String tmp) {
    this.selectStyle = tmp;
  }


  /**
   * Gets the tableName attribute of the LookupList object
   *
   * @return The tableName value
   */
  public String getTableName() {
    return tableName;
  }


  /**
   * Gets the uniqueField attribute of the LookupList object
   *
   * @return The uniqueField value
   */
  public String getUniqueField() {
    return uniqueField;
  }


  /**
   * Gets the table attribute of the LookupList object
   *
   * @return The table value
   */
  public String getTable() {
    return tableName;
  }


  /**
   * Gets the Multiple attribute of the LookupList object
   *
   * @return The Multiple value
   */
  public boolean getMultiple() {
    return multiple;
  }


  /**
   * Gets the htmlSelectDefaultNone attribute of the LookupList object
   *
   * @param selectName Description of the Parameter
   * @return The htmlSelectDefaultNone value
   */
  public String getHtmlSelectDefaultNone(String selectName) {
    HtmlSelect thisSelect = new HtmlSelect();
    thisSelect.addItem(-1, "-- None --");

    for (LookupElement thisElement : this) {
      thisSelect.addItem(
          thisElement.getCode(),
          thisElement.getDescription());
    }

    return thisSelect.getHtml(selectName);
  }


  /**
   * Gets the htmlSelectDefaultNone attribute of the LookupList object
   *
   * @param selectName Description of the Parameter
   * @param defaultKey Description of the Parameter
   * @return The htmlSelectDefaultNone value
   */
  public String getHtmlSelectDefaultNone(String selectName, int defaultKey) {
    HtmlSelect thisSelect = new HtmlSelect();
    thisSelect.addItem(-1, "-- None --");
    for (LookupElement thisElement : this) {
      thisSelect.addItem(
          thisElement.getCode(),
          thisElement.getDescription());
    }
    return thisSelect.getHtml(selectName, defaultKey);
  }


  /**
   * Gets the enabledElementCount attribute of the LookupList object
   *
   * @return The enabledElementCount value
   */
  public int getEnabledElementCount() {
    int count = 0;
    for (LookupElement thisElement : this) {
      if (thisElement.getEnabled()) {
        count++;
      }
    }
    return count;
  }


  /**
   * Gets the HtmlSelect attribute of the ContactEmailTypeList object
   *
   * @param selectName Description of Parameter
   * @param defaultKey Description of Parameter
   * @return The HtmlSelect value
   * @since 1.1
   */
  public String getHtmlSelect(String selectName, int defaultKey) {
    HtmlSelect thisSelect = getHtmlSelectObj(defaultKey);
    return thisSelect.getHtml(selectName);
  }


  /**
   * Gets the htmlSelectObj attribute of the LookupList object
   *
   * @param defaultKey Description of the Parameter
   * @return The htmlSelectObj value
   */
  public HtmlSelect getHtmlSelectObj(int defaultKey) {
    HtmlSelect thisSelect = new HtmlSelect();
    thisSelect.setSelectSize(selectSize);
    thisSelect.setSelectStyle(selectStyle);
    thisSelect.setMultiple(multiple);
    thisSelect.setJsEvent(jsEvent);
    Iterator i = this.iterator();
    boolean keyFound = false;
    int lookupDefault = defaultKey;
    while (i.hasNext()) {
      LookupElement thisElement = (LookupElement) i.next();
      if (thisElement.getEnabled() || !showDisabledFlag) {
        thisSelect.addItem(thisElement.getCode(), thisElement.getDescription());
        if (thisElement.getDefaultItem()) {
          lookupDefault = thisElement.getCode();
        }
      } else if (thisElement.getCode() == defaultKey) {
        thisSelect.addItem(thisElement.getCode(), thisElement.getDescription());
      }
      if (thisElement.getCode() == defaultKey) {
        keyFound = true;
      }
    }
    if (keyFound) {
      thisSelect.setDefaultKey(defaultKey);
    } else {
      thisSelect.setDefaultKey(lookupDefault);
    }
    return thisSelect;
  }


  /**
   * Gets the HtmlSelect attribute of the ContactEmailTypeList object
   *
   * @param selectName   Description of Parameter
   * @param defaultValue Description of Parameter
   * @return The HtmlSelect value
   * @since 1.1
   */
  public String getHtmlSelect(String selectName, String defaultValue) {
    HtmlSelect thisSelect = new HtmlSelect();
    thisSelect.setSelectSize(selectSize);
    thisSelect.setSelectStyle(selectStyle);
    thisSelect.setJsEvent(jsEvent);
    for (LookupElement thisElement : this) {
      if (thisElement.getEnabled()) {
        thisSelect.addItem(thisElement.getCode(), thisElement.getDescription());
      } else if (thisElement.getDescription().equals(defaultValue)) {
        thisSelect.addItem(thisElement.getCode(), thisElement.getDescription());
      }
    }
    return thisSelect.getHtml(selectName, defaultValue);
  }


  /**
   * Gets the htmlSelect attribute of the LookupList object
   *
   * @param selectName Description of Parameter
   * @param ms         Description of Parameter
   * @return The htmlSelect value
   */
  public String getHtmlSelect(String selectName, LookupList ms) {
    HtmlSelect thisSelect = new HtmlSelect();
    thisSelect.setSelectSize(selectSize);
    thisSelect.setSelectStyle(selectStyle);
    thisSelect.setJsEvent(jsEvent);
    thisSelect.setMultiple(multiple);
    thisSelect.setMultipleSelects(ms);
    for (LookupElement thisElement : this) {
      if (thisElement.getEnabled()) {
        thisSelect.addItem(thisElement.getCode(), thisElement.getDescription());
      }
    }
    return thisSelect.getHtml(selectName);
  }


  /**
   * Gets the SelectedValue attribute of the LookupList object
   *
   * @param selectedId Description of Parameter
   * @return The SelectedValue value
   */
  public String getValueFromId(int selectedId) {
    LookupElement keyFound = null;
    for (LookupElement thisElement : this) {
      if (thisElement.getCode() == selectedId) {
        return thisElement.getDescription();
      }
      if (thisElement.getDefaultItem()) {
        keyFound = thisElement;
      }
    }
    if (keyFound != null) {
      return keyFound.getDescription();
    } else {
      return "";
    }
  }


  /**
   * Gets the selectedValue attribute of the LookupList object
   *
   * @param selectedId Description of Parameter
   * @return The selectedValue value
   */
  public String getValueFromId(String selectedId) {
    try {
      return getValueFromId(Integer.parseInt(selectedId));
    } catch (Exception e) {
      return "";
    }
  }

  public LookupElement getObjectFromLevel(int level) {
    for (LookupElement lookup : this) {
      if (lookup.getLevel() == level) {
        return lookup;
      }
    }
    return null;
  }


  public LookupElement getObjectFromId(int id) {
    for (LookupElement lookup : this) {
      if (lookup.getId() == id) {
        return lookup;
      }
    }
    return null;
  }


  /**
   * Gets the object attribute of the LookupList object
   *
   * @param rs Description of Parameter
   * @return The object value
   * @throws SQLException Description of Exception
   */
  public LookupElement getObject(ResultSet rs) throws SQLException {
    return new LookupElement(rs);
  }


  /**
   * Description of the Method
   *
   * @param db Description of Parameter
   * @throws SQLException Description of Exception
   */
  public void select(Connection db) throws SQLException {
    buildList(db);
  }


  /**
   * Description of the Method
   *
   * @param db Description of Parameter
   * @throws SQLException Description of Exception
   */
  public void buildList(Connection db) throws SQLException {
    // TODO: Fix hanging cursor
    PreparedStatement pst = null;
    ResultSet rs = queryList(db, pst);
    int count = 0;
    while (rs.next()) {
      if (pagedListInfo != null && pagedListInfo.getItemsPerPage() > 0 &&
          DatabaseUtils.getType(db) == DatabaseUtils.MSSQL &&
          count >= pagedListInfo.getItemsPerPage()) {
        break;
      }
      LookupElement thisElement = this.getObject(rs);
      thisElement.setTableName(tableName);
      boolean enabled = thisElement.getEnabled();
      if (enabled || !showDisabledFlag || hasItem(thisElement.getCode())) {
        ++count;
        this.add(thisElement);
      }
    }
    rs.close();
    if (pst != null) {
      pst.close();
    }
  }


  /**
   * This method is required for synchronization, it allows for the resultset
   * to be streamed with lower overhead
   *
   * @param db  Description of the Parameter
   * @param pst Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  public ResultSet queryList(Connection db, PreparedStatement pst) throws SQLException {
    ResultSet rs = null;
    int items = -1;
    StringBuffer sqlCount = new StringBuffer();
    StringBuffer sqlOrder = new StringBuffer();
    StringBuffer sqlFilter = new StringBuffer();
    StringBuffer sqlSelect = new StringBuffer();
    sqlCount.append(
        "SELECT COUNT(*) AS recordcount " +
            "FROM " + tableName + " " +
            "WHERE code > -1 ");
    createFilter(sqlFilter);
    if (pagedListInfo != null) {
      //Get the total number of records matching filter
      pst = db.prepareStatement(sqlCount.toString() + sqlFilter.toString());
      items = prepareFilter(pst);
      rs = pst.executeQuery();
      if (rs.next()) {
        int maxRecords = rs.getInt("recordcount");
        pagedListInfo.setMaxRecords(maxRecords);
      }
      rs.close();
      pst.close();
      //Determine the offset, based on the filter, for the first record to show
      if (!pagedListInfo.getCurrentLetter().equals("")) {
        pst = db.prepareStatement(sqlCount.toString() + sqlFilter.toString() +
            "AND description < ? ");
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
      pagedListInfo.setDefaultSort("description ", null);
      pagedListInfo.appendSqlTail(db, sqlOrder);
    } else {
      sqlOrder.append("ORDER BY level,description ");
    }
    if (pagedListInfo != null) {
      pagedListInfo.appendSqlSelectHead(db, sqlSelect);
    } else {
      sqlSelect.append("SELECT ");
    }
    sqlSelect.append(
        "* " +
            "FROM " + tableName + " " +
            "WHERE code > -1 ");
    pst = db.prepareStatement(sqlSelect.toString() + sqlFilter.toString() + sqlOrder.toString());
    items = prepareFilter(pst);
    rs = pst.executeQuery();
    if (pagedListInfo != null) {
      pagedListInfo.doManualOffset(db, rs);
    }
    return rs;
  }


  /**
   * Description of the Method
   *
   * @param key Description of Parameter
   * @return Description of the Returned Value
   */
  public boolean containsKey(int key) {
    Iterator i = this.iterator();
    boolean keyFound = false;

    while (i.hasNext()) {
      LookupElement thisElement = (LookupElement) i.next();

      if (thisElement.getEnabled() && thisElement.getCode() == key) {
        keyFound = true;
      }
    }

    return keyFound;
  }


  /**
   * Description of the Method
   *
   * @return Description of the Returned Value
   */
  public String valuesAsString() {
    Iterator i = this.iterator();
    String result = "";
    int count = 0;
    while (i.hasNext()) {
      LookupElement thisElement = (LookupElement) i.next();
      if (count > 0) {
        result += ", " + thisElement.getDescription();
      } else {
        result += thisElement.getDescription();
      }
      count++;
    }
    return result;
  }


  /**
   * Gets the idFromLevel attribute of the LookupList object
   *
   * @param level Description of the Parameter
   * @return The idFromLevel value
   */
  public int getIdFromLevel(int level) {
    for (LookupElement thisElement : this) {
      if (thisElement.getLevel() == level) {
        return thisElement.getId();
      }
    }
    return -1;
  }


  public int getIdFromValue(String roleName) {
    for (LookupElement thisElement : this) {
      if (thisElement.getDescription().equals(roleName)) {
        return thisElement.getId();
      }
    }
    return -1;
  }


  /**
   * Gets the levelFromId attribute of the LookupList object
   *
   * @param id Description of the Parameter
   * @return The levelFromId value
   */
  public int getLevelFromId(int id) {
    for (LookupElement thisElement : this) {
      if (thisElement.getCode() == id) {
        return thisElement.getLevel();
      }
    }
    return -1;
  }


  /**
   * Description of the Method
   */
  public void printVals() {
    for (LookupElement thisElement : this) {
      System.out.println("Level: " + thisElement.getLevel() + ", Desc: " + thisElement.getDescription() + ", Code: " + thisElement.getCode());
    }
  }


  /**
   * Adds a feature to the Item attribute of the LookupList object
   *
   * @param tmp1 The feature to be added to the Item attribute
   * @param tmp2 The feature to be added to the Item attribute
   */
  public void addItem(int tmp1, String tmp2) {
    if (!exists(tmp1)) {
      LookupElement thisElement = new LookupElement();
      thisElement.setCode(tmp1);
      thisElement.setDescription(tmp2);
      if (this.size() > 0) {
        this.add(0, thisElement);
      } else {
        this.add(thisElement);
      }
    }
  }


  /**
   * Checks to see if the entry is already in the list
   *
   * @param tmp1 Description of the Parameter
   * @return Description of the Return Value
   */
  public boolean exists(int tmp1) {
    for (LookupElement thisElement : this) {
      if (thisElement.getCode() == tmp1) {
        return true;
      }
    }
    return false;
  }


  /**
   * Description of the Method
   *
   * @param tmp1 Description of Parameter
   * @param tmp2 Description of Parameter
   */
  public void appendItem(int tmp1, String tmp2) {
    LookupElement thisElement = new LookupElement();
    thisElement.setCode(tmp1);
    thisElement.setDescription(tmp2);
    if (this.size() <= 0) {
      this.add(0, thisElement);
    } else {
      this.add(this.size(), thisElement);
    }
  }


  /**
   * Description of the Method
   *
   * @param sqlFilter Description of Parameter
   */
  private void createFilter(StringBuffer sqlFilter) {
    if (sqlFilter == null) {
      sqlFilter = new StringBuffer();
    }
    if (selectedItems != null) {
      if (selectedItems.size() > 0) {
        sqlFilter.append("AND (enabled = ? OR code IN (" + getItemsAsList() + ")) ");
      } else {
        sqlFilter.append("AND enabled = ? ");
      }
    }
  }


  /**
   * Description of the Method
   *
   * @param pst Description of Parameter
   * @return Description of the Returned Value
   * @throws SQLException Description of Exception
   */
  private int prepareFilter(PreparedStatement pst) throws SQLException {
    int i = 0;
    if (selectedItems != null) {
      pst.setBoolean(++i, true);
    }
    return i;
  }


  /**
   * If a list of codes is provided, then hasItem will return whether the list
   * contains the specified code
   *
   * @param code Description of the Parameter
   * @return Description of the Return Value
   */
  private boolean hasItem(int code) {
    if (selectedItems != null) {
      if (!selectedItems.containsKey(new Integer(code))) {
        return false;
      }
    }
    return true;
  }


  /**
   * Gets the itemsAsList attribute of the LookupList object
   *
   * @return The itemsAsList value
   */
  private String getItemsAsList() {
    StringBuffer sb = new StringBuffer();
    if (selectedItems != null) {
      Iterator i = selectedItems.keySet().iterator();
      while (i.hasNext()) {
        sb.append(String.valueOf((Integer) i.next()));
        if (i.hasNext()) {
          sb.append(",");
        }
      }
    }
    return sb.toString();
  }
}

