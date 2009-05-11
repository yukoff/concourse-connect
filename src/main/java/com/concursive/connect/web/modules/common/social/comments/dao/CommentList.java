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

package com.concursive.connect.web.modules.common.social.comments.dao;

import com.concursive.commons.db.DatabaseUtils;
import com.concursive.connect.web.utils.PagedListInfo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Contains a collection of wiki comments
 *
 * @author Kailash Bhoopalam
 * @version $Id: WikiCommentList.java Exp
 *          $
 * @created November 26, 2008
 */
public class CommentList extends ArrayList<Comment> {

  protected int linkItemId = -1;
  protected int projectId = -1;
  protected PagedListInfo pagedListInfo = null;
  //calendar
  protected java.sql.Timestamp alertRangeStart = null;
  protected java.sql.Timestamp alertRangeEnd = null;

  protected String tableName = null;
  protected String uniqueFieldId = null;

  public CommentList() {
  }

  protected void setLinkItemId(int tmp) {
    this.linkItemId = tmp;
  }


  protected void setLinkItemId(String tmp) {
    this.linkItemId = Integer.parseInt(tmp);
  }

  protected int getLinkItemId() {
    return linkItemId;
  }

  public int getProjectId() {
    return projectId;
  }

  public void setProjectId(int projectId) {
    this.projectId = projectId;
  }

  public void setPagedListInfo(PagedListInfo tmp) {
    this.pagedListInfo = tmp;
  }

  public PagedListInfo getPagedListInfo() {
    return pagedListInfo;
  }

  public void setAlertRangeStart(java.sql.Timestamp tmp) {
    this.alertRangeStart = tmp;
  }

  public void setAlertRangeStart(String tmp) {
    this.alertRangeStart = DatabaseUtils.parseTimestamp(tmp);
  }

  public java.sql.Timestamp getAlertRangeStart() {
    return alertRangeStart;
  }

  public void setAlertRangeEnd(java.sql.Timestamp tmp) {
    this.alertRangeEnd = tmp;
  }

  public void setAlertRangeEnd(String tmp) {
    this.alertRangeEnd = DatabaseUtils.parseTimestamp(tmp);
  }

  public java.sql.Timestamp getAlertRangeEnd() {
    return alertRangeEnd;
  }

  /**
   * @return the tableName
   */
  protected String getTableName() {
    return tableName;
  }


  /**
   * @param tableName the tableName to set
   */
  protected void setTableName(String tableName) {
    this.tableName = tableName;
  }


  /**
   * @return the uniqueFieldId
   */
  protected String getUniqueFieldId() {
    return uniqueFieldId;
  }


  /**
   * @param uniqueFieldId the uniqueFieldId to set
   */
  protected void setUniqueFieldId(String uniqueFieldId) {
    this.uniqueFieldId = uniqueFieldId;
  }

  public int queryCount(Connection db) throws SQLException {
    int count = 0;
    StringBuffer sqlCount = new StringBuffer();
    StringBuffer sqlFilter = new StringBuffer();
    sqlCount.append(
        "SELECT COUNT(*) AS recordcount " +
            "FROM " + tableName + " c " +
            "WHERE c.comment_id > -1 ");
    createFilter(sqlFilter);
    PreparedStatement pst = db.prepareStatement(sqlCount.toString() + sqlFilter.toString());
    prepareFilter(pst);
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      count = rs.getInt("recordcount");
    }
    rs.close();
    pst.close();
    return count;
  }

  public PreparedStatement prepareList(Connection db) throws SQLException {
    int items = -1;
    PreparedStatement pst = null;
    ResultSet rs = null;
    StringBuffer sqlSelect = new StringBuffer();
    StringBuffer sqlCount = new StringBuffer();
    StringBuffer sqlFilter = new StringBuffer();
    StringBuffer sqlOrder = new StringBuffer();
    //Need to build a base SQL statement for counting records
    sqlCount.append(
        "SELECT COUNT(*) AS recordcount " +
            "FROM " + tableName + " c " +
            "WHERE c.comment_id > -1 ");
    createFilter(sqlFilter);
    if (pagedListInfo == null) {
      pagedListInfo = new PagedListInfo();
      pagedListInfo.setItemsPerPage(-1);
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
    //Determine column to sort by
    pagedListInfo.setDefaultSort("c.entered", null);
    pagedListInfo.appendSqlTail(db, sqlOrder);
    //Need to build a base SQL statement for returning records
    if (pagedListInfo != null) {
      pagedListInfo.appendSqlSelectHead(db, sqlSelect);
    } else {
      sqlSelect.append("SELECT ");
    }
    sqlSelect.append(
        "c.* " +
            "FROM " + tableName + " c " +
            "WHERE c.comment_id > -1 ");
    pst = db.prepareStatement(sqlSelect.toString() + sqlFilter.toString() + sqlOrder.toString());
    items = prepareFilter(pst);
    return pst;
  }


  protected void createFilter(StringBuffer sqlFilter) {
    if (linkItemId > 0) {
      sqlFilter.append("AND c." + uniqueFieldId + "  = ? ");
    }
    if (alertRangeStart != null) {
      sqlFilter.append("AND c.modified >= ? ");
    }
    if (alertRangeEnd != null) {
      sqlFilter.append("AND c.modified < ? ");
    }
  }


  protected int prepareFilter(PreparedStatement pst) throws SQLException {
    int i = 0;
    if (linkItemId > 0) {
      pst.setInt(++i, linkItemId);
    }
    if (alertRangeStart != null) {
      pst.setTimestamp(++i, alertRangeStart);
    }
    if (alertRangeEnd != null) {
      pst.setTimestamp(++i, alertRangeEnd);
    }
    return i;
  }


  protected static void delete(Connection db, String uniqueFieldId, int objectId, String tableName) throws SQLException {
    // Delete the version data
    PreparedStatement pst = db.prepareStatement(
        "DELETE FROM " + tableName +
            " WHERE " + uniqueFieldId + " = ? ");
    pst.setInt(1, objectId);
    pst.execute();
    pst.close();
  }
}
