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

package com.concursive.connect.web.modules.common.social.contribution.dao;

import com.concursive.commons.db.DatabaseUtils;
import com.concursive.connect.web.utils.PagedListInfo;

import java.sql.*;
import java.util.ArrayList;

/**
 * A collection of UserContributionLog objects, typically queried from a
 * database
 *
 * @author Kailash Bhoopalam
 * @version $Id$
 * @created January 29, 2009
 */
public class UserContributionLogList extends ArrayList<UserContributionLog> {

  private PagedListInfo pagedListInfo = null;
  private int id = -1;
  private int userId = -1;
  private int contributionId = -1;
  private Timestamp sinceContributionDate = null;
  private int projectId = -1;
  private int projectCategoryId = -1;


  public UserContributionLogList() {
  }

  /**
   * @return the pagedListInfo
   */
  public PagedListInfo getPagedListInfo() {
    return pagedListInfo;
  }

  /**
   * @param pagedListInfo the pagedListInfo to set
   */
  public void setPagedListInfo(PagedListInfo pagedListInfo) {
    this.pagedListInfo = pagedListInfo;
  }

  /**
   * @return the id
   */
  public int getId() {
    return id;
  }

  /**
   * @param id the id to set
   */
  public void setId(int id) {
    this.id = id;
  }

  public void setId(String id) {
    this.id = Integer.parseInt(id);
  }

  /**
   * @return the userId
   */
  public int getUserId() {
    return userId;
  }

  /**
   * @param userId the userId to set
   */
  public void setUserId(int userId) {
    this.userId = userId;
  }

  public void setUserId(String userId) {
    this.userId = Integer.parseInt(userId);
  }

  /**
   * @return the contributionId
   */
  public int getContributionId() {
    return contributionId;
  }

  /**
   * @param contributionId the contributionId to set
   */
  public void setContributionId(int contributionId) {
    this.contributionId = contributionId;
  }

  public void setContributionId(String contributionId) {
    this.contributionId = Integer.parseInt(contributionId);
  }

  /**
   * @return the sinceContributionDate
   */
  public Timestamp getSinceContributionDate() {
    return sinceContributionDate;
  }

  /**
   * @param sinceContributionDate the sinceContributionDate to set
   */
  public void setSinceContributionDate(Timestamp sinceContributionDate) {
    this.sinceContributionDate = sinceContributionDate;
  }

  public void setSinceContributionDate(String sinceContributionDate) {
    this.sinceContributionDate = DatabaseUtils.parseTimestamp(sinceContributionDate);
  }

  /**
   * @return the projectId
   */
  public int getProjectId() {
    return projectId;
  }

  /**
   * @param projectId the projectId to set
   */
  public void setProjectId(int projectId) {
    this.projectId = projectId;
  }

  public void setProjectId(String projectId) {
    this.projectId = Integer.parseInt(projectId);
  }

  /**
   * @return the projectCategoryId
   */
  public int getProjectCategoryId() {
    return projectCategoryId;
  }

  /**
   * @param projectCategoryId the projectCategoryId to set
   */
  public void setProjectCategoryId(int projectCategoryId) {
    this.projectCategoryId = projectCategoryId;
  }

  public void setProjectCategoryId(String projectCategoryId) {
    this.projectCategoryId = Integer.parseInt(projectCategoryId);
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
        "SELECT COUNT(*) as recordcount " +
            "FROM user_contribution_log ucl " +
            "WHERE ucl.record_id > 0 ");
    createFilter(sqlFilter, db);
    if (pagedListInfo != null) {
      //Get the total number of records matching filter
      pst = db.prepareStatement(
          sqlCount.toString() +
              sqlFilter.toString());
      items = prepareFilter(pst);
      rs = pst.executeQuery();
      if (rs.next()) {
        int maxRecords = rs.getInt("recordcount");
        pagedListInfo.setMaxRecords(maxRecords);
      }
      rs.close();
      pst.close();
      //Determine column to sort by
      pagedListInfo.setDefaultSort("ucl.entered", null);
      pagedListInfo.appendSqlTail(db, sqlOrder);
    } else {
      sqlOrder.append("ORDER BY ucl.entered ");
    }
    //Need to build a base SQL statement for returning records
    if (pagedListInfo != null) {
      pagedListInfo.appendSqlSelectHead(db, sqlSelect);
    } else {
      sqlSelect.append("SELECT ");
    }
    sqlSelect.append(
        "* " +
            "FROM user_contribution_log ucl " +
            "WHERE ucl.record_id > 0 ");
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
      UserContributionLog thisUserContributionLog = new UserContributionLog(rs);
      this.add(thisUserContributionLog);
    }
    rs.close();
    pst.close();
  }


  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public void delete(Connection db) throws SQLException {
    for (UserContributionLog thisUserContributionLog : this) {
      thisUserContributionLog.delete(db);
    }
  }


  /**
   * Description of the Method
   *
   * @param sqlFilter Description of the Parameter
   * @param db        Description of the Parameter
   */
  private void createFilter(StringBuffer sqlFilter, Connection db) {
    if (id > -1) {
      sqlFilter.append("AND record_id = ? ");
    }
    if (userId > -1) {
      sqlFilter.append("AND user_id = ? ");
    }
    if (contributionId > -1) {
      sqlFilter.append("AND contribution_id = ? ");
    }
    if (sinceContributionDate != null) {
      sqlFilter.append("AND contribution_date >= ? ");
    }
    if (projectId > -1) {
      sqlFilter.append("AND project_id = ? ");
    }
    if (projectCategoryId > -1) {
      sqlFilter.append("AND project_id IN (SELECT project_id FROM projects WHERE category_id = ?) ");
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
    if (id > -1) {
      pst.setInt(++i, id);
    }
    if (userId > -1) {
      pst.setInt(++i, userId);
    }
    if (contributionId > -1) {
      pst.setInt(++i, contributionId);
    }
    if (sinceContributionDate != null) {
      pst.setTimestamp(++i, sinceContributionDate);
    }
    if (projectId > -1) {
      pst.setInt(++i, projectId);
    }
    if (projectCategoryId > -1) {
      pst.setInt(++i, projectCategoryId);
    }
    return i;
  }

  /**
   * Queries the top users from the database
   *
   * @param db  the database connection to query
   * @param max the max user records requested in the result
   * @throws SQLException database exception
   */
  public void buildTopUsers(Connection db, int max) throws SQLException {
    StringBuffer sqlStatement = new StringBuffer();
    StringBuffer sqlFilter = new StringBuffer();
    sqlStatement.append(
        "SELECT user_id, SUM(points) AS points " +
            "FROM user_contribution_log ucl " +
            "WHERE ucl.record_id > 0 ");
    createFilter(sqlFilter, db);
    sqlFilter.append("GROUP BY user_id ");
    sqlFilter.append("ORDER BY points DESC ");
    if (max > 0) {
      // @todo postgresql only for now
      sqlFilter.append("LIMIT ").append(max).append(" ");
    }
    PreparedStatement pst = db.prepareStatement(sqlStatement.toString() + sqlFilter.toString());
    prepareFilter(pst);
    ResultSet rs = pst.executeQuery();
    while (rs.next()) {
      UserContributionLog thisUserContributionLog = new UserContributionLog();
      thisUserContributionLog.setUserId(rs.getInt("user_id"));
      thisUserContributionLog.setPoints(rs.getInt("points"));
      this.add(thisUserContributionLog);
    }
    rs.close();
    pst.close();
  }
}