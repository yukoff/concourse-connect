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

package com.concursive.connect.web.modules.communications.dao;

import com.concursive.connect.web.utils.PagedListInfo;
import com.concursive.connect.web.modules.reports.dao.ReportQueue;
import com.concursive.connect.web.modules.members.dao.TeamMember;

import java.util.ArrayList;
import java.util.Calendar;
import java.sql.*;

/**
 * Represents a list of queues to track email updates for a particular user
 *
 * @author Ananth
 * @created Nov 30, 2009
 */
public class EmailUpdatesQueueList extends ArrayList {
  private PagedListInfo pagedListInfo = null;
  private int enteredBy = -1;
  private boolean processedOnly = false;
  private boolean unprocessedOnly = false;
  private boolean scheduledOnly = false;
  private boolean inQueueOnly = false;
  private boolean sortAscending = false;
  private java.sql.Timestamp rangeStart = null;
  private java.sql.Timestamp rangeEnd = null;
  private int type = -1;
  private int max = -1;

  public int getMax() {
    return max;
  }

  public void setMax(int max) {
    this.max = max;
  }

  public EmailUpdatesQueueList() {
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
        "SELECT COUNT(*) as recordcount " +
            "FROM email_updates_queue q " +
            "WHERE queue_id > -1 ");
    createFilter(sqlFilter, db);
    if (pagedListInfo != null) {
      //Get the total number of records matching filter
      pst = db.prepareStatement(sqlCount.toString() + sqlFilter.toString() + ((max != -1) ? "LIMIT " + max + " " : ""));
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
          "ORDER BY q.status, q.processed desc, q.schedule_time ");
    }

    //Need to build a base SQL statement for returning records
    if (pagedListInfo != null) {
      pagedListInfo.appendSqlSelectHead(db, sqlSelect);
    } else {
      sqlSelect.append("SELECT ");
    }
    sqlSelect.append(
        "q.* " +
        "FROM email_updates_queue q " +
        "WHERE queue_id > -1 ");
    pst = db.prepareStatement(
        sqlSelect.toString() + sqlFilter.toString() + sqlOrder.toString() + ((max != -1) ? "LIMIT " + max + " " : ""));
    //System.out.println("PST: " + pst);
    items = prepareFilter(pst);
    rs = pst.executeQuery();
    if (pagedListInfo != null) {
      pagedListInfo.doManualOffset(db, rs);
    }
    while (rs.next()) {
      EmailUpdatesQueue thisQueue = new EmailUpdatesQueue(rs);
      this.add(thisQueue);
    }
    rs.close();
    pst.close();    
  }

  protected void createFilter(StringBuffer sqlFilter, Connection db) {
    if (sqlFilter == null) {
      sqlFilter = new StringBuffer();
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
          "AND q.status = " + EmailUpdatesQueue.STATUS_UNSCHEDULED + " ");
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
    if (type != -1) {
      if (type == TeamMember.EMAIL_OFTEN) {
        sqlFilter.append("AND q.schedule_often = ? ");
      } else if (type == TeamMember.EMAIL_DAILY) {
        sqlFilter.append("AND q.schedule_daily = ? ");
      } else if (type == TeamMember.EMAIL_WEEKLY) {
        sqlFilter.append("AND q.schedule_weekly = ? ");
      } else if (type == TeamMember.EMAIL_MONTHLY) {
        sqlFilter.append("AND q.schedule_monthly = ? ");
      }
    }
    if (scheduledOnly) {
      sqlFilter.append(
          "AND q.status = " + EmailUpdatesQueue.STATUS_SCHEDULED + " " +
          "AND (q.processed < CURRENT_TIMESTAMP OR q.processed IS NULL) " +
          "AND q.schedule_time <= CURRENT_TIMESTAMP ");
    }
  }

  protected int prepareFilter(PreparedStatement pst) throws SQLException {
    int i = 0;
    if (enteredBy != -1) {
      pst.setInt(++i, enteredBy);
    }
    if (rangeStart != null) {
      pst.setTimestamp(++i, rangeStart);
    }
    if (rangeEnd != null) {
      pst.setTimestamp(++i, rangeEnd);
    }
    if (type != -1) {
      if (type == TeamMember.EMAIL_OFTEN || type == TeamMember.EMAIL_DAILY ||
          type == TeamMember.EMAIL_WEEKLY || type == TeamMember.EMAIL_MONTHLY) {
        pst.setBoolean(++i, true);
      }
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
        "UPDATE email_updates_queue " +
            "SET status = ? " +
            "WHERE queue_id = ? " +
            "AND status = ? ");
    pst.setInt(1, EmailUpdatesQueue.STATUS_PROCESSING);
    pst.setInt(2, thisReport.getId());
    pst.setInt(3, EmailUpdatesQueue.STATUS_SCHEDULED);
    int count = pst.executeUpdate();
    pst.close();
    return (count == 1);
  }

  public PagedListInfo getPagedListInfo() {
    return pagedListInfo;
  }

  public void setPagedListInfo(PagedListInfo pagedListInfo) {
    this.pagedListInfo = pagedListInfo;
  }

  public int getEnteredBy() {
    return enteredBy;
  }

  public void setEnteredBy(int enteredBy) {
    this.enteredBy = enteredBy;
  }

  public boolean getProcessedOnly() {
    return processedOnly;
  }

  public void setProcessedOnly(boolean processedOnly) {
    this.processedOnly = processedOnly;
  }

  public boolean getUnprocessedOnly() {
    return unprocessedOnly;
  }

  public void setUnprocessedOnly(boolean unprocessedOnly) {
    this.unprocessedOnly = unprocessedOnly;
  }

  public boolean getScheduledOnly() {
    return scheduledOnly;
  }

  public void setScheduledOnly(boolean scheduledOnly) {
    this.scheduledOnly = scheduledOnly;
  }

  public boolean getInQueueOnly() {
    return inQueueOnly;
  }

  public void setInQueueOnly(boolean inQueueOnly) {
    this.inQueueOnly = inQueueOnly;
  }

  public boolean getSortAscending() {
    return sortAscending;
  }

  public void setSortAscending(boolean sortAscending) {
    this.sortAscending = sortAscending;
  }

  public Timestamp getRangeStart() {
    return rangeStart;
  }

  public void setRangeStart(Timestamp rangeStart) {
    this.rangeStart = rangeStart;
  }

  public Timestamp getRangeEnd() {
    return rangeEnd;
  }

  public void setRangeEnd(Timestamp rangeEnd) {
    this.rangeEnd = rangeEnd;
  }

  public int getType() {
    return type;
  }

  public void setType(int type) {
    this.type = type;
  }
}
