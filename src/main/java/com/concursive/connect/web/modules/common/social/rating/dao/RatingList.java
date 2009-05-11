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

package com.concursive.connect.web.modules.common.social.rating.dao;

import com.concursive.commons.db.DatabaseUtils;
import com.concursive.commons.text.StringUtils;
import com.concursive.connect.web.utils.PagedListInfo;

import java.sql.*;
import java.util.ArrayList;

/**
 * Description of the Class
 *
 * @author Kailash Bhoopalam
 * @version $Id$
 * @created February 13, 2008
 */
public class RatingList extends ArrayList<Rating> {
  // main filters (default retrieves all records)
  private PagedListInfo pagedListInfo = null;
  private String table = null;
  private String uniqueField = null;
  private String primaryKeyField = null;
  private int ratingId = -1;
  private boolean hasInappropriate = false;
  private Timestamp enteredRangeStart = null;
  private Timestamp enteredRangeEnd = null;

  /**
   * Constructor for the ProjectRatingList object
   */
  public RatingList() {
  }


  /**
   * Sets the pagedListInfo attribute of the ProjectRatingList object
   *
   * @param tmp The new pagedListInfo value
   */
  public void setPagedListInfo(PagedListInfo tmp) {
    this.pagedListInfo = tmp;
  }


  /**
   * @return the table
   */
  public String getTable() {
    return table;
  }


  /**
   * @param table the table to set
   */
  public void setTable(String table) {
    this.table = table;
  }


  /**
   * @return the uniqueField
   */
  public String getUniqueField() {
    return uniqueField;
  }


  /**
   * @param uniqueField the uniqueField to set
   */
  public void setUniqueField(String uniqueField) {
    this.uniqueField = uniqueField;
  }


  /**
   * @return the primaryKeyField
   */
  public String getPrimaryKeyField() {
    return primaryKeyField;
  }


  /**
   * @param primaryKeyField the primaryKeyField to set
   */
  public void setPrimaryKeyField(String primaryKeyField) {
    this.primaryKeyField = primaryKeyField;
  }


  /**
   * @return the ratingId
   */
  public int getRatingId() {
    return ratingId;
  }


  /**
   * @param ratingId the ratingId to set
   */
  public void setRatingId(int ratingId) {
    this.ratingId = ratingId;
  }


  /**
   * @return the hasInappropriate
   */
  public boolean getHasInappropriate() {
    return hasInappropriate;
  }


  /**
   * @param hasInappropriate the hasInappropriate to set
   */
  public void setHasInappropriate(boolean hasInappropriate) {
    this.hasInappropriate = hasInappropriate;
  }

  public void setHasInappropriate(String hasInappropriate) {
    this.hasInappropriate = DatabaseUtils.parseBoolean(hasInappropriate);
  }

  /**
   * @param ratingId the ratingId to set
   */
  public void setRatingId(String ratingId) {
    this.ratingId = Integer.parseInt(ratingId);
  }


  /**
   * @return the pagedListInfo
   */
  public PagedListInfo getPagedListInfo() {
    return pagedListInfo;
  }

  /**
   * @return the enteredRangeStart
   */
  public Timestamp getEnteredRangeStart() {
    return enteredRangeStart;
  }

  /**
   * @param enteredRangeStart the enteredRangeStart to set
   */
  public void setEnteredRangeStart(Timestamp enteredRangeStart) {
    this.enteredRangeStart = enteredRangeStart;
  }

  public void setEnteredRangeStart(String enteredRangeStart) {
    this.enteredRangeStart = DatabaseUtils.parseTimestamp(enteredRangeStart);
  }

  /**
   * @return the enteredRangeEnd
   */
  public Timestamp getEnteredRangeEnd() {
    return enteredRangeEnd;
  }

  /**
   * @param enteredRangeEnd the enteredRangeEnd to set
   */
  public void setEnteredRangeEnd(Timestamp enteredRangeEnd) {
    this.enteredRangeEnd = enteredRangeEnd;
  }

  public void setEnteredRangeEnd(String enteredRangeEnd) {
    this.enteredRangeEnd = DatabaseUtils.parseTimestamp(enteredRangeEnd);
  }

  /**
   * Description of the Method
   *
   * @param db Description of Parameter
   * @throws SQLException Description of Exception
   */
  public void buildList(Connection db) throws SQLException {
    if (!StringUtils.hasText(primaryKeyField) || !StringUtils.hasText(uniqueField)) {
      throw new SQLException();
    }

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
            "FROM " + table + "_rating " +
            "WHERE record_id > -1 ");
    createFilter(sqlFilter);
    if (pagedListInfo == null) {
      pagedListInfo = new PagedListInfo();
      pagedListInfo.setItemsPerPage(0);
    }

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
      pst = db.prepareStatement(sqlCount.toString() +
          sqlFilter.toString() +
          "AND lower(title) < ? ");
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
    pagedListInfo.setDefaultSort("entered DESC", null);
    pagedListInfo.appendSqlTail(db, sqlOrder);

    //Need to build a base SQL statement for returning records
    pagedListInfo.appendSqlSelectHead(db, sqlSelect);
    sqlSelect.append(
        primaryKeyField + ", " + uniqueField + ", rating, " + (hasInappropriate ? "inappropriate, " : "") + "entered, enteredby, project_id " +
            "FROM " + table + "_rating " +
            "WHERE record_id > -1 ");
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
      Rating thisRating = new Rating(rs, primaryKeyField, uniqueField, hasInappropriate);
      this.add(thisRating);
    }
    rs.close();
    pst.close();

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
    if (enteredRangeStart != null) {
      sqlFilter.append("AND entered >= ? ");
    }
    if (enteredRangeEnd != null) {
      sqlFilter.append("AND entered < ? ");
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
    if (enteredRangeStart != null) {
      pst.setTimestamp(++i, enteredRangeStart);
    }
    if (enteredRangeEnd != null) {
      pst.setTimestamp(++i, enteredRangeEnd);
    }
    return i;
  }
}

