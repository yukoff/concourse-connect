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

package com.concursive.connect.web.modules.profile.dao;

import com.concursive.commons.date.DateUtils;
import com.concursive.commons.db.DatabaseUtils;
import com.concursive.connect.web.utils.PagedListInfo;

import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Represents a featured listing
 *
 * @author Kailash Bhoopalam
 * @version $Id$
 * @created January 20, 2008
 */
public class ProjectFeaturedListingList extends ArrayList<ProjectFeaturedListing> {

  // main filters (default retrieves all records)
  private PagedListInfo pagedListInfo = null;

  // The user's rating and review

  private int id = -1;
  private int projectId = -1;
  private String portletKey = null;
  private Timestamp featuredDate = null;
  private Timestamp featuredSinceDate = null;

  //helper attributes
  private Timestamp startOfDay = null;
  private Timestamp endOfDay = null;


  /**
   * Constructor for the ProjectRating
   */
  public ProjectFeaturedListingList() {
  }


  public void setPagedListInfo(PagedListInfo tmp) {
    this.pagedListInfo = tmp;
  }


  public PagedListInfo getPagedListInfo() {
    return pagedListInfo;
  }


  public void setId(int messageId) {
    this.id = messageId;

  }

  public void setId(String messageId) {
    this.id = Integer.parseInt(messageId);

  }

  public int getId() {
    return id;
  }

  /**
   * @param projectId the projectId to set
   */
  public void setProjectId(int projectId) {
    this.projectId = projectId;
  }


  /**
   * @param projectId the projectId to set
   */
  public void setProjectId(String projectId) {
    this.projectId = Integer.parseInt(projectId);
  }


  /**
   * @return the projectId
   */
  public int getProjectId() {
    return projectId;
  }


  /**
   * @return the portletKey
   */
  public String getPortletKey() {
    return portletKey;
  }


  /**
   * @param portletKey the portletKey to set
   */
  public void setPortletKey(String portletKey) {
    this.portletKey = portletKey;
  }


  /**
   * @return the featuredDate
   */
  public Timestamp getFeaturedDate() {
    return featuredDate;
  }


  /**
   * @param featuredDate the featuredDate to set
   */
  public void setFeaturedDate(Timestamp featuredDate) {
    this.featuredDate = featuredDate;

    startOfDay = DateUtils.getStartOfDay(featuredDate.getTime());
    endOfDay = DateUtils.getEndOfDay(featuredDate.getTime());
  }

  public void setFeaturedDate(String featuredDate) {
    this.featuredDate = DatabaseUtils.parseTimestamp(featuredDate);

    startOfDay = DateUtils.getStartOfDay(this.featuredDate.getTime());
    endOfDay = DateUtils.getEndOfDay(this.featuredDate.getTime());
  }


  /**
   * @return the featuredSinceDate
   */
  public Timestamp getFeaturedSinceDate() {
    return featuredSinceDate;
  }


  /**
   * @param featuredSinceDate the featuredSinceDate to set
   */
  public void setFeaturedSinceDate(Timestamp featuredSinceDate) {
    this.featuredSinceDate = featuredSinceDate;
  }

  public void setFeaturedSinceDate(String featuredSinceDate) {
    this.featuredSinceDate = DatabaseUtils.parseTimestamp(featuredSinceDate);
  }

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
            "FROM project_featured_listing " +
            "WHERE featured_id > -1 ");
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

    //Determine column to sort by
    pagedListInfo.setDefaultSort("featured_date DESC", null);
    pagedListInfo.appendSqlTail(db, sqlOrder);

    //Need to build a base SQL statement for returning records
    pagedListInfo.appendSqlSelectHead(db, sqlSelect);
    sqlSelect.append(
        "* " +
            "FROM project_featured_listing " +
            "WHERE featured_id > -1 ");
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
      ProjectFeaturedListing thisProjectFeaturedListing = new ProjectFeaturedListing(rs);
      this.add(thisProjectFeaturedListing);
    }
    rs.close();
    pst.close();
  }

  private void createFilter(StringBuffer sqlFilter) {
    if (sqlFilter == null) {
      sqlFilter = new StringBuffer();
    }
    if (id > -1) {
      sqlFilter.append("AND featured_id = ? ");
    }
    if (projectId > -1) {
      sqlFilter.append("AND project_id = ? ");
    }
    if (portletKey != null) {
      sqlFilter.append("AND portlet_key = ? ");
    }
    if (featuredDate != null) {
      sqlFilter.append("AND ( featured_date < ? AND featured_date > ? )");
    }
    if (featuredSinceDate != null) {
      sqlFilter.append("AND featured_date > ? ");
    }
  }

  private int prepareFilter(PreparedStatement pst) throws SQLException {
    int i = 0;
    if (id > -1) {
      pst.setInt(++i, id);
    }
    if (projectId > -1) {
      pst.setInt(++i, projectId);
    }
    if (portletKey != null) {
      pst.setString(++i, portletKey);
    }
    if (featuredDate != null) {
      pst.setTimestamp(++i, this.endOfDay);
      pst.setTimestamp(++i, this.startOfDay);
    }
    if (featuredSinceDate != null) {
      pst.setTimestamp(++i, this.featuredSinceDate);
    }
    return i;
  }


  public void delete(Connection db) throws SQLException {
    Iterator<ProjectFeaturedListing> projectFeaturedListingItr = this.iterator();
    while (projectFeaturedListingItr.hasNext()) {
      ProjectFeaturedListing projectFeaturedListing = projectFeaturedListingItr.next();
      projectFeaturedListing.delete(db);
    }
  }
}
