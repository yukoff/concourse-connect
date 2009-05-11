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
import com.concursive.commons.text.StringUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Works with database lookup tables and conforms to the API
 *
 * @author matt rajkowski
 * @created January 14, 2003
 */
public class CustomLookupList extends ArrayList<CustomLookupElement> {

  // Table properties
  public static String uniqueField = "code";
  public String tableName = null;
  public String sortBy = null;

  // Dynamic query properties
  ArrayList<String> fields = new ArrayList<String>();

  public CustomLookupList() {
  }

  public static String getUniqueField() {
    return uniqueField;
  }

  public static void setUniqueField(String uniqueField) {
    CustomLookupList.uniqueField = uniqueField;
  }

  public String getTableName() {
    return tableName;
  }

  public void setTableName(String tableName) {
    this.tableName = tableName;
  }

  public ArrayList<String> getFields() {
    return fields;
  }

  public void setFields(ArrayList<String> fields) {
    this.fields = fields;
  }

  public String getSortBy() {
    return sortBy;
  }

  public void setSortBy(String sortBy) {
    this.sortBy = sortBy;
  }

  /**
   * Adds a feature to the Field attribute of the CustomLookupList object
   *
   * @param fieldName The feature to be added to the Field attribute
   */
  public void addField(String fieldName) {
    if (fields == null) {
      fields = new ArrayList<String>();
    }
    fields.add(fieldName);
  }


  /**
   * Gets the object attribute of the CustomLookupList object
   *
   * @param rs Description of the Parameter
   * @return The object value
   * @throws SQLException Description of the Exception
   */
  public Object getObject(ResultSet rs) throws SQLException {
    CustomLookupElement thisElement = new CustomLookupElement(rs);
    thisElement.setTableName(tableName);
    thisElement.setUniqueField(uniqueField);
    return thisElement;
  }


  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public void buildList(Connection db) throws SQLException {
    PreparedStatement pst = prepareList(db);
    ResultSet rs = pst.executeQuery();
    while (rs.next()) {
      CustomLookupElement thisElement = (CustomLookupElement) this.getObject(rs);
      this.add(thisElement);
    }
    rs.close();
    pst.close();
  }


  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  public PreparedStatement prepareList(Connection db) throws SQLException {
    int items = -1;
    StringBuffer sqlFilter = new StringBuffer();
    StringBuffer sqlSelect = new StringBuffer();
    createFilter(sqlFilter);
    // Query the specified fields, or all
    sqlSelect.append("SELECT ");
    if (fields.size() > 0) {
      Iterator i = fields.iterator();
      while (i.hasNext()) {
        String field = (String) i.next();
        sqlSelect.append(
            DatabaseUtils.parseReservedWord(db, field));
        if (i.hasNext()) {
          sqlSelect.append(",");
        }
        sqlSelect.append(" ");
      }
    } else {
      sqlSelect.append("* ");
    }
    sqlSelect.append("FROM ").append(tableName).append(" ");
    // Determine the sort order
    StringBuffer sqlOrder = new StringBuffer();
    // If a sortBy is present, then use the sortBy
    if (StringUtils.hasText(sortBy)) {
      sqlOrder.append("ORDER BY ");
      sqlOrder.append(sortBy);
      sqlOrder.append(" ");
    }
    PreparedStatement pst = db.prepareStatement(
        sqlSelect.toString() + sqlFilter.toString() + sqlOrder.toString());
    items = prepareFilter(pst);
    return pst;
  }


  private void createFilter(StringBuffer sqlFilter) {

  }


  private int prepareFilter(PreparedStatement pst) throws SQLException {
    int i = 0;
    return i;
  }
}

