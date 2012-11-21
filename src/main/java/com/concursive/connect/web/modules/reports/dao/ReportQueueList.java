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

package com.concursive.connect.web.modules.reports.dao;

import com.concursive.commons.db.DatabaseUtils;
import com.concursive.connect.web.utils.PagedListInfo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

/**
 * A collection of ReportQueue objects
 *
 * @author matt rajkowski
 * @created October 1, 2003
 */
public class ReportQueueList extends ArrayList<ReportQueue> {

  private PagedListInfo pagedListInfo = null;
  private int enteredBy = -1;
  private boolean buildResources = false;
  private boolean processedOnly = false;
  private boolean unprocessedOnly = false;
  private boolean scheduledTodayOnly = false;
  private boolean inQueueOnly = false;
  private boolean sortAscending = false;
  private java.sql.Timestamp rangeStart = null;
  private java.sql.Timestamp rangeEnd = null;
  private int projectId = -1;
  private boolean expiredOnly = false;
  private int reportId = -1;
  private boolean enabled = true;

  /**
   * Constructor for the CategoryList object
   */
  public ReportQueueList() {
  }


  /**
   * Sets the pagedListInfo attribute of the ReportList object
   *
   * @param tmp The new pagedListInfo value
   */
  public void setPagedListInfo(PagedListInfo tmp) {
    this.pagedListInfo = tmp;
  }


  /**
   * Gets the pagedListInfo attribute of the ReportList object
   *
   * @return The pagedListInfo value
   */
  public PagedListInfo getPagedListInfo() {
    return pagedListInfo;
  }


  /**
   * Sets the enteredBy attribute of the ReportQueueList object
   *
   * @param tmp The new enteredBy value
   */
  public void setEnteredBy(int tmp) {
    this.enteredBy = tmp;
  }


  /**
   * Sets the enteredBy attribute of the ReportQueueList object
   *
   * @param tmp The new enteredBy value
   */
  public void setEnteredBy(String tmp) {
    this.enteredBy = Integer.parseInt(tmp);
  }


  /**
   * Gets the enteredBy attribute of the ReportQueueList object
   *
   * @return The enteredBy value
   */
  public int getEnteredBy() {
    return enteredBy;
  }


  /**
   * Sets the buildResources attribute of the ReportQueueList object
   *
   * @param tmp The new buildResources value
   */
  public void setBuildResources(boolean tmp) {
    this.buildResources = tmp;
  }


  /**
   * Sets the buildResources attribute of the ReportQueueList object
   *
   * @param tmp The new buildResources value
   */
  public void setBuildResources(String tmp) {
    this.buildResources = DatabaseUtils.parseBoolean(tmp);
  }


  /**
   * Gets the buildResources attribute of the ReportQueueList object
   *
   * @return The buildResources value
   */
  public boolean getBuildResources() {
    return buildResources;
  }


  /**
   * Sets the processedOnly attribute of the ReportQueueList object
   *
   * @param tmp The new processedOnly value
   */
  public void setProcessedOnly(boolean tmp) {
    this.processedOnly = tmp;
  }


  /**
   * Sets the processedOnly attribute of the ReportQueueList object
   *
   * @param tmp The new processedOnly value
   */
  public void setProcessedOnly(String tmp) {
    this.processedOnly = DatabaseUtils.parseBoolean(tmp);
  }


  /**
   * Sets the unprocessedOnly attribute of the ReportQueueList object
   *
   * @param tmp The new unprocessedOnly value
   */
  public void setUnprocessedOnly(boolean tmp) {
    this.unprocessedOnly = tmp;
  }


  /**
   * Sets the unprocessedOnly attribute of the ReportQueueList object
   *
   * @param tmp The new unprocessedOnly value
   */
  public void setUnprocessedOnly(String tmp) {
    this.unprocessedOnly = DatabaseUtils.parseBoolean(tmp);
  }

  public void setScheduledTodayOnly(boolean tmp) {
    this.scheduledTodayOnly = tmp;
  }

  public void setScheduledTodayOnly(String tmp) {
    this.scheduledTodayOnly = DatabaseUtils.parseBoolean(tmp);
  }


  /**
   * Sets the inQueueOnly attribute of the ReportQueueList object
   *
   * @param tmp The new inQueueOnly value
   */
  public void setInQueueOnly(boolean tmp) {
    this.inQueueOnly = tmp;
  }


  /**
   * Sets the inQueueOnly attribute of the ReportQueueList object
   *
   * @param tmp The new inQueueOnly value
   */
  public void setInQueueOnly(String tmp) {
    this.inQueueOnly = DatabaseUtils.parseBoolean(tmp);
  }


  /**
   * Sets the sortAscending attribute of the ReportQueueList object
   *
   * @param tmp The new sortAscending value
   */
  public void setSortAscending(boolean tmp) {
    this.sortAscending = tmp;
  }


  /**
   * Sets the sortAscending attribute of the ReportQueueList object
   *
   * @param tmp The new sortAscending value
   */
  public void setSortAscending(String tmp) {
    this.sortAscending = DatabaseUtils.parseBoolean(tmp);
  }


  /**
   * Sets the rangeStart attribute of the ReportQueueList object
   *
   * @param tmp The new rangeStart value
   */
  public void setRangeStart(java.sql.Timestamp tmp) {
    this.rangeStart = tmp;
  }


  /**
   * Sets the rangeStart attribute of the ReportQueueList object
   *
   * @param tmp The new rangeStart value
   */
  public void setRangeStart(String tmp) {
    this.rangeStart = DatabaseUtils.parseTimestamp(tmp);
  }


  /**
   * Sets the rangeEnd attribute of the ReportQueueList object
   *
   * @param tmp The new rangeEnd value
   */
  public void setRangeEnd(java.sql.Timestamp tmp) {
    this.rangeEnd = tmp;
  }


  /**
   * Sets the rangeEnd attribute of the ReportQueueList object
   *
   * @param tmp The new rangeEnd value
   */
  public void setRangeEnd(String tmp) {
    this.rangeEnd = DatabaseUtils.parseTimestamp(tmp);
  }


  /**
   * Gets the processedOnly attribute of the ReportQueueList object
   *
   * @return The processedOnly value
   */
  public boolean getProcessedOnly() {
    return processedOnly;
  }


  /**
   * Gets the unprocessedOnly attribute of the ReportQueueList object
   *
   * @return The unprocessedOnly value
   */
  public boolean getUnprocessedOnly() {
    return unprocessedOnly;
  }

  public boolean getScheduledTodayOnly() {
    return scheduledTodayOnly;
  }


  /**
   * Gets the inQueueOnly attribute of the ReportQueueList object
   *
   * @return The inQueueOnly value
   */
  public boolean getInQueueOnly() {
    return inQueueOnly;
  }


  /**
   * Gets the sortAscending attribute of the ReportQueueList object
   *
   * @return The sortAscending value
   */
  public boolean getSortAscending() {
    return sortAscending;
  }


  /**
   * Gets the rangeStart attribute of the ReportQueueList object
   *
   * @return The rangeStart value
   */
  public java.sql.Timestamp getRangeStart() {
    return rangeStart;
  }


  /**
   * Gets the rangeEnd attribute of the ReportQueueList object
   *
   * @return The rangeEnd value
   */
  public java.sql.Timestamp getRangeEnd() {
    return rangeEnd;
  }

  public int getProjectId() {
    return projectId;
  }

  public void setProjectId(int projectId) {
    this.projectId = projectId;
  }

  public boolean getExpiredOnly() {
    return expiredOnly;
  }

  public void setExpiredOnly(boolean expiredOnly) {
    this.expiredOnly = expiredOnly;
  }

  public int getReportId() {
    return reportId;
  }

  public void setReportId(int reportId) {
    this.reportId = reportId;
  }

  public boolean getEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public void setEnabled(String enabled) {
    this.enabled = Boolean.parseBoolean(enabled);
  }

  /**
   * Builds a list of ReportQueue objects based on the filter properties that
   * have been set
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
            "FROM report_queue q " +
            "LEFT JOIN projects p ON (q.project_id = p.project_id) " +
            "WHERE queue_id > -1 ");
    createFilter(sqlFilter, db);
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
      //Determine column to sort by
      pagedListInfo.setDefaultSort("q.entered", "DESC");
      pagedListInfo.appendSqlTail(db, sqlOrder);
    } else {
      sqlOrder.append(
          "ORDER BY p.title, p.project_id, q.status, q.processed desc, q.schedule_time ");
    }

    //Need to build a base SQL statement for returning records
    if (pagedListInfo != null) {
      pagedListInfo.appendSqlSelectHead(db, sqlSelect);
    } else {
      sqlSelect.append("SELECT ");
    }
    sqlSelect.append(
        "q.* " +
            "FROM report_queue q " +
            "LEFT JOIN projects p ON (q.project_id = p.project_id) " +
            "WHERE queue_id > -1 ");
    pst = db.prepareStatement(
        sqlSelect.toString() + sqlFilter.toString() + sqlOrder.toString());
    items = prepareFilter(pst);
    rs = pst.executeQuery();
    if (pagedListInfo != null) {
      pagedListInfo.doManualOffset(db, rs);
    }
    while (rs.next()) {
      ReportQueue thisQueue = new ReportQueue(rs);
      this.add(thisQueue);
    }
    rs.close();
    pst.close();
    if (buildResources) {
      Iterator i = this.iterator();
      while (i.hasNext()) {
        ReportQueue thisQueue = (ReportQueue) i.next();
        thisQueue.buildReport(db);
      }
    }
  }


  /**
   * Defines additional parameters to be used by the query
   *
   * @param sqlFilter Description of the Parameter
   */
  protected void createFilter(StringBuffer sqlFilter, Connection db) {
    if (sqlFilter == null) {
      sqlFilter = new StringBuffer();
    }
    if (enabled) {
      sqlFilter.append("AND q.enabled = ? ");
    }
    if (enteredBy != -1) {
      sqlFilter.append("AND q.enteredby = ? ");
    }
    if (processedOnly) {
      sqlFilter.append("AND q.processed IS NOT NULL ");
    }
    if (unprocessedOnly) {
      sqlFilter.append(
          "AND q.processed IS NULL " +
              "AND q.status = " + ReportQueue.STATUS_QUEUED + " ");
    }
    if (inQueueOnly) {
      sqlFilter.append("AND q.processed IS NULL ");
    }
    if (rangeStart != null) {
      sqlFilter.append("AND q.processed >= ? ");
    }
    if (rangeEnd != null) {
      sqlFilter.append("AND q.processed < ? ");
    }
    if (projectId > -1) {
      sqlFilter.append("AND q.project_id = ? ");
    }
    if (expiredOnly) {
      sqlFilter.append(
          "AND " +
              "((q.processed IS NOT NULL AND CURRENT_TIMESTAMP > " + DatabaseUtils.addTimestampInterval(db, DatabaseUtils.DAY, "cleanup", "q.processed") + ") OR " +
              "(q.processed IS NULL AND CURRENT_TIMESTAMP > " + DatabaseUtils.addTimestampInterval(db, DatabaseUtils.DAY, "cleanup", "q.entered") + ")) ");
      sqlFilter.append(
          "AND q.schedule_time IS NULL ");
    }
    if (scheduledTodayOnly) {
      sqlFilter.append(
          "AND q.status = " + ReportQueue.STATUS_SCHEDULED + " " +
              "AND (q.processed < CURRENT_TIMESTAMP OR q.processed IS NULL) " +
              "AND q.schedule_time <= CURRENT_TIMESTAMP ");
      Calendar today = Calendar.getInstance();
      if (today.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
        sqlFilter.append("AND schedule_monday = ? ");
      }
      if (today.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY) {
        sqlFilter.append("AND schedule_tuesday = ? ");
      }
      if (today.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY) {
        sqlFilter.append("AND schedule_wednesday = ? ");
      }
      if (today.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY) {
        sqlFilter.append("AND schedule_thursday = ? ");
      }
      if (today.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) {
        sqlFilter.append("AND schedule_friday = ? ");
      }
      if (today.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
        sqlFilter.append("AND schedule_saturday = ? ");
      }
      if (today.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
        sqlFilter.append("AND schedule_sunday = ? ");
      }
    }
    if (reportId > -1) {
      sqlFilter.append("AND q.report_id = ? ");
    }
  }


  /**
   * Sets the additional parameters for the query
   *
   * @param pst Description of Parameter
   * @return Description of the Returned Value
   * @throws SQLException Description of Exception
   */
  protected int prepareFilter(PreparedStatement pst) throws SQLException {
    int i = 0;
    if (enabled) {
      pst.setBoolean(++i, true);
    }
    if (enteredBy != -1) {
      pst.setInt(++i, enteredBy);
    }
    if (rangeStart != null) {
      pst.setTimestamp(++i, rangeStart);
    }
    if (rangeEnd != null) {
      pst.setTimestamp(++i, rangeEnd);
    }
    if (projectId > -1) {
      pst.setInt(++i, projectId);
    }

    if (scheduledTodayOnly) {
      Calendar today = Calendar.getInstance();
      if (today.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
        pst.setBoolean(++i, true);
      }
      if (today.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY) {
        pst.setBoolean(++i, true);
      }
      if (today.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY) {
        pst.setBoolean(++i, true);
      }
      if (today.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY) {
        pst.setBoolean(++i, true);
      }
      if (today.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) {
        pst.setBoolean(++i, true);
      }
      if (today.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
        pst.setBoolean(++i, true);
      }
      if (today.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
        pst.setBoolean(++i, true);
      }
    }
    if (reportId > -1) {
      pst.setInt(++i, reportId);
    }
    return i;
  }


  /**
   * Returns whether the specified report was just locked, false if it already
   * locked
   *
   * @param thisReport Description of the Parameter
   * @param db         Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  public static boolean lockReport(ReportQueue thisReport, Connection db) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "UPDATE report_queue " +
            "SET status = ? " +
            "WHERE queue_id = ? " +
            "AND status = ? ");
    pst.setInt(1, ReportQueue.STATUS_PROCESSING);
    pst.setInt(2, thisReport.getId());
    pst.setInt(3, ReportQueue.STATUS_QUEUED);
    int count = pst.executeUpdate();
    pst.close();
    return (count == 1);
  }

  public void delete(Connection db, String basePath) throws SQLException {
    for (ReportQueue thisQueue : this) {
      thisQueue.delete(db, basePath);
    }
  }

}


