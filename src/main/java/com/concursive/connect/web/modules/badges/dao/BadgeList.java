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

package com.concursive.connect.web.modules.badges.dao;

import com.concursive.commons.db.DatabaseUtils;
import com.concursive.connect.Constants;
import com.concursive.connect.web.utils.PagedListInfo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Represents a collection of badges built from specified properties
 *
 * @author Kailash Bhoopalam
 * @version $Id$
 * @created May 12, 2008
 */
public class BadgeList extends ArrayList<Badge> {
  // main badge filters (default retrieves all badges)
  private PagedListInfo pagedListInfo = null;
  private int badgeId = -1;
  private int categoryId = -1;
  private int enabled = Constants.UNDEFINED;
  private boolean onlyWithoutProjectCategory = false;
  private boolean onlyWithoutBadgeCategory = false;
  private boolean buildLogos = false;


  /**
   * Constructor for the BadgeList object
   */
  public BadgeList() {
  }


  /**
   * Sets the pagedListInfo attribute of the BadgeList object
   *
   * @param tmp The new pagedListInfo value
   */
  public void setPagedListInfo(PagedListInfo tmp) {
    this.pagedListInfo = tmp;
  }


  /**
   * @return the badgeId
   */
  public int getBadgeId() {
    return badgeId;
  }


  /**
   * @param badgeId the badgeId to set
   */
  public void setBadgeId(int badgeId) {
    this.badgeId = badgeId;
  }


  /**
   * @param badgeId the badgeId to set
   */
  public void setBadgeId(String badgeId) {
    this.badgeId = Integer.parseInt(badgeId);
  }


  /**
   * @return the badgeCategoryId
   */
  public int getCategoryId() {
    return categoryId;
  }


  /**
   * @param categoryId the badgeCategoryId to set
   */
  public void setCategoryId(int categoryId) {
    this.categoryId = categoryId;
  }


  /**
   * @param categoryId the badgeCategoryId to set
   */
  public void setCategoryId(String categoryId) {
    this.categoryId = Integer.parseInt(categoryId);
  }


  /**
   * @return the enabled
   */
  public int getEnabled() {
    return enabled;
  }


  /**
   * @param enabled the enabled to set
   */
  public void setEnabled(int enabled) {
    this.enabled = enabled;
  }


  /**
   * @param enabled the enabled to set
   */
  public void setEnabled(String enabled) {
    this.enabled = DatabaseUtils.parseBooleanToConstant(enabled);
  }


  /**
   * @return the pagedListInfo
   */
  public PagedListInfo getPagedListInfo() {
    return pagedListInfo;
  }


  /**
   * @return the buildLogos
   */
  public boolean getBuildLogos() {
    return buildLogos;
  }


  /**
   * @param buildLogos the buildLogos to set
   */
  public void setBuildLogos(boolean buildLogos) {
    this.buildLogos = buildLogos;
  }

  public boolean isOnlyWithoutProjectCategory() {
    return onlyWithoutProjectCategory;
  }

  public void setOnlyWithoutProjectCategory(boolean onlyWithoutProjectCategory) {
    this.onlyWithoutProjectCategory = onlyWithoutProjectCategory;
  }

  public void setOnlyWithoutProjectCategory(String tmp) {
    this.onlyWithoutProjectCategory = DatabaseUtils.parseBoolean(tmp);
  }

  public boolean isOnlyWithoutBadgeCategory() {
    return onlyWithoutBadgeCategory;
  }

  public void setOnlyWithoutBadgeCategory(boolean onlyWithoutBadgeCategory) {
    this.onlyWithoutBadgeCategory = onlyWithoutBadgeCategory;
  }

  public void setOnlyWithoutBadgeCategory(String tmp) {
    this.onlyWithoutBadgeCategory = DatabaseUtils.parseBoolean(tmp);
  }

  /**
   * Description of the Method
   *
   * @param db Description of Parameter
   * @throws SQLException Description of Exception
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
            "FROM badge b " +
            "WHERE badge_id > -1 ");
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
    pagedListInfo.setDefaultSort("title", null);
    pagedListInfo.appendSqlTail(db, sqlOrder);

    //Need to build a base SQL statement for returning records
    pagedListInfo.appendSqlSelectHead(db, sqlSelect);
    sqlSelect.append(
        "* " +
            "FROM badge b " +
            "WHERE badge_id > -1 ");
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
      Badge thisBadge = new Badge(rs);
      this.add(thisBadge);
    }
    rs.close();
    pst.close();

    if (buildLogos) {
      buildLogos(db);
    }
  }


  /**
   * @param db
   * @throws java.sql.SQLException
   */
  private void buildLogos(Connection db) throws SQLException {
    for (Badge badge : this) {
      badge.buildLogo(db);
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
    if (badgeId > -1) {
      sqlFilter.append("AND badge_id = ? ");
    }
    if (categoryId > -1) {
      sqlFilter.append("AND badge_category_id = ? ");
    }
    if (enabled != Constants.UNDEFINED) {
      sqlFilter.append("AND (enabled = ?) ");
    }
    if (onlyWithoutProjectCategory) {
      sqlFilter.append("AND project_category_id IS NOT NULL ");
    }
    if (onlyWithoutBadgeCategory) {
      sqlFilter.append("AND badge_category_id IS NOT NULL ");
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
    if (badgeId > -1) {
      pst.setInt(++i, badgeId);
    }
    if (categoryId > -1) {
      pst.setInt(++i, categoryId);
    }
    if (enabled != Constants.UNDEFINED) {
      pst.setBoolean(++i, (enabled == Constants.TRUE));
    }
    return i;
  }


  /**
   * @param db
   * @param basePath
   */
  public void delete(Connection db, String basePath) throws SQLException {
    for (Badge badge : this) {
      badge.delete(db, basePath);
    }
  }

  /**
   * This method runs a query to find the member count of all the badgeIds in this list.
   * The member counts for each badge are then mapped to the badgeId and returned.
   * If the list is empty then an empty map is returned.
   *
   * @param db Connection
   * @return a mapping of all badge ids to their member counts
   * @throws java.sql.SQLException
   */
  public Map<Integer, Integer> findBadgeMemberCount(Connection db) throws SQLException {
    Map<Integer, Integer> memberCountMap = new HashMap<Integer, Integer>();
    // Put all the ids into a string
    StringBuffer idBuffer = new StringBuffer();
    List<Integer> idList = new ArrayList<Integer>(this.size());
    for (Badge badge : this) {
      idList.add(badge.getId());
    }
    if (idList.size() == 0) {
      return memberCountMap;
    } else {
      idBuffer.append(idList.remove(0));
    }
    for (Integer bId : idList) {
      idBuffer.append(",").append(bId);
    }
    // Query for all the counts of all the badges
    StringBuffer sql = new StringBuffer();
    sql.append(
        "SELECT badge_id, " +
            "COUNT(*) as badge_count " +
            "FROM badgelink_project " +
            "WHERE badge_id IN ( ");
    sql.append(idBuffer.toString());
    sql.append(" ) GROUP BY badge_id");
    PreparedStatement pst = db.prepareStatement(sql.toString());
    ResultSet rs = pst.executeQuery();
    while (rs.next()) {
      int bId = rs.getInt("badge_id");
      int bCount = rs.getInt("badge_count");
      memberCountMap.put(bId, bCount);
    }
    rs.close();
    pst.close();
    return memberCountMap;
  }

  public Badge getFromValue(String name) {
    for (Badge thisBadge : this) {
      if (name.equals(thisBadge.getTitle())) {
        return thisBadge;
      }
    }
    return null;
  }

}

