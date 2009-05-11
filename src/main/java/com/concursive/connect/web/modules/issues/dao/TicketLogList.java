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

package com.concursive.connect.web.modules.issues.dao;

import com.concursive.commons.db.DatabaseUtils;
import com.concursive.connect.web.utils.PagedListInfo;

import javax.servlet.http.HttpServletRequest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * A generated list of changes to a ticket based on the saved history
 *
 * @author chris price
 * @version $Id$
 * @created December 5, 2001
 */
public class TicketLogList extends ArrayList {

  private PagedListInfo pagedListInfo = null;
  private int ticketId = -1;

  public final static String tableName = "ticketlog";
  public final static String uniqueField = "id";
  private java.sql.Timestamp lastAnchor = null;
  private java.sql.Timestamp nextAnchor = null;
  private boolean doSystemMessages = true;


  /**
   * Constructor for the TicketLogList object
   */
  public TicketLogList() {
  }


  /**
   * Constructor for the TicketLogList object
   *
   * @param request Description of Parameter
   * @param userId  Description of Parameter
   */
  public TicketLogList(HttpServletRequest request, int userId) {
    int i = 0;
    if (request.getParameter("newticketlogentry") != null) {
      TicketLog thisEntry = new TicketLog();
      thisEntry.setEnteredBy(userId);
      thisEntry.buildRecord(request);
      if (thisEntry.isValid()) {
        this.add(thisEntry);
      }
    }
  }


  /**
   * Sets the pagedListInfo attribute of the TicketLogList object
   *
   * @param tmp The new pagedListInfo value
   */
  public void setPagedListInfo(PagedListInfo tmp) {
    this.pagedListInfo = tmp;
  }


  /**
   * Sets the ticketId attribute of the TicketLogList object
   *
   * @param tmp The new ticketId value
   */
  public void setTicketId(int tmp) {
    this.ticketId = tmp;
  }


  /**
   * Gets the doSystemMessages attribute of the TicketLogList object
   *
   * @return The doSystemMessages value
   */
  public boolean getDoSystemMessages() {
    return doSystemMessages;
  }


  /**
   * Sets the doSystemMessages attribute of the TicketLogList object
   *
   * @param doSystemMessages The new doSystemMessages value
   */
  public void setDoSystemMessages(boolean doSystemMessages) {
    this.doSystemMessages = doSystemMessages;
  }


  /**
   * Sets the systemMessages attribute of the TicketLogList object
   *
   * @param current The new systemMessages value
   * @param prev    The new systemMessages value
   * @return Description of the Returned Value
   */
  public boolean setSystemMessages(TicketLog current, TicketLog prev) {
    TicketLog tempLog = null;
    if (prev == null) {
      //First log entry
      if (1 == 1) {
        tempLog = new TicketLog();
        tempLog.createSysMsg(current);
        tempLog.setEntryText("[ Ticket Opened ]");
        this.add(tempLog);
      }
      if (1 == 1) {
        tempLog = new TicketLog();
        tempLog.createSysMsg(current);
        if (tempLog.getAssignedToName() != null) {
          tempLog.setEntryText("[ Assigned to " + current.getAssignedToName() + " ]");
        } else {
          tempLog.setEntryText("[ Ticket is unassigned ]");
        }
        this.add(tempLog);
      }
      if (current.getDepartmentCode() > 0) {
        tempLog = new TicketLog();
        tempLog.createSysMsg(current);
        tempLog.setEntryText("[ Department assigned to " + current.getDepartmentName() + " ]");
        this.add(tempLog);
      }
      if (current.getPriorityCode() > 0) {
        tempLog = new TicketLog();
        tempLog.createSysMsg(current);
        tempLog.setEntryText("[ Priority set to " + current.getPriorityName() + " ]");
        this.add(tempLog);
      }
      if (current.getSeverityCode() > 0) {
        tempLog = new TicketLog();
        tempLog.createSysMsg(current);
        tempLog.setEntryText("[ Severity set to " + current.getSeverityName() + " ]");
        this.add(tempLog);
      }
      if (current.getClosed()) {
        tempLog = new TicketLog();
        tempLog.createSysMsg(current);
        tempLog.setEntryText("[ Ticket was immediately closed ]");
        this.add(tempLog);
      }
      return true;
    } else {
      //Comparative log entry
      if (current.getAssignedTo() != prev.getAssignedTo()) {
        tempLog = new TicketLog();
        tempLog.createSysMsg(current);
        if (prev.getAssignedToName() != null) {
          if (current.getAssignedToName() != null) {
            tempLog.setEntryText("[ Reassigned from " + isAssigned(prev.getAssignedToName()) + " to " + isAssigned(current.getAssignedToName()) + " ]");
          } else {
            tempLog.setEntryText("[ Ticket is unassigned ]");
          }
        } else {
          tempLog.setEntryText("[ Assigned to " + isAssigned(current.getAssignedToName()) + " ]");
        }
        this.add(tempLog);
      }
      if (current.getDepartmentCode() != prev.getDepartmentCode()) {
        tempLog = new TicketLog();
        tempLog.createSysMsg(current);
        tempLog.setEntryText("[ Department changed from " + isAssigned(prev.getDepartmentName()) + " to " + isAssigned(current.getDepartmentName()) + " ]");
        this.add(tempLog);
      }
      if (current.getPriorityCode() != prev.getPriorityCode()) {
        tempLog = new TicketLog();
        tempLog.createSysMsg(current);
        tempLog.setEntryText("[ Priority changed from " + isAssigned(prev.getPriorityName()) + " to " + isAssigned(current.getPriorityName()) + " ]");
        this.add(tempLog);
      }
      if (current.getSeverityCode() != prev.getSeverityCode()) {
        tempLog = new TicketLog();
        tempLog.createSysMsg(current);
        tempLog.setEntryText("[ Severity changed from " + isAssigned(prev.getSeverityName()) + " to " + isAssigned(current.getSeverityName()) + " ]");
        this.add(tempLog);
      }
      if (!current.getClosed() && prev.getClosed()) {
        tempLog = new TicketLog();
        tempLog.createSysMsg(current);
        tempLog.setEntryText("[ Ticket Reopened ]");
        this.add(tempLog);
      }
      if (current.getClosed() && !prev.getClosed()) {
        tempLog = new TicketLog();
        tempLog.createSysMsg(current);
        tempLog.setEntryText("[ Ticket Closed ]");
        this.add(tempLog);
      }
      return true;
    }
  }


  /**
   * Gets the tableName attribute of the TicketLogList object
   *
   * @return The tableName value
   */
  public String getTableName() {
    return tableName;
  }


  /**
   * Gets the uniqueField attribute of the TicketLogList object
   *
   * @return The uniqueField value
   */
  public String getUniqueField() {
    return uniqueField;
  }


  /**
   * Gets the lastAnchor attribute of the TicketLogList object
   *
   * @return The lastAnchor value
   */
  public java.sql.Timestamp getLastAnchor() {
    return lastAnchor;
  }


  /**
   * Gets the nextAnchor attribute of the TicketLogList object
   *
   * @return The nextAnchor value
   */
  public java.sql.Timestamp getNextAnchor() {
    return nextAnchor;
  }


  /**
   * Sets the lastAnchor attribute of the TicketLogList object
   *
   * @param tmp The new lastAnchor value
   */
  public void setLastAnchor(java.sql.Timestamp tmp) {
    this.lastAnchor = tmp;
  }


  /**
   * Sets the nextAnchor attribute of the TicketLogList object
   *
   * @param tmp The new nextAnchor value
   */
  public void setNextAnchor(java.sql.Timestamp tmp) {
    this.nextAnchor = tmp;
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
            "FROM ticketlog t " +
            "WHERE t.id > 0 ");
    createFilter(sqlFilter);
    if (pagedListInfo != null) {
      //Get the total number of records matching filter
      pst = db.prepareStatement(sqlCount.toString() +
          sqlFilter.toString());
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
            "AND t.comment < ? ");
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
      pagedListInfo.setDefaultSort("t.entered", null);
      //NOTE: Do not change the order due to the system message method
      pagedListInfo.appendSqlTail(db, sqlOrder);
    } else {
      //NOTE: Do not change the order due to the system message method
      sqlOrder.append("ORDER BY t.entered ");
    }
    //Need to build a base SQL statement for returning records
    if (pagedListInfo != null) {
      pagedListInfo.appendSqlSelectHead(db, sqlSelect);
    } else {
      sqlSelect.append("SELECT ");
    }
    sqlSelect.append(
        "t.*, " +
            "tp.description AS priorityname, ts.description AS severityname, " +
            "ct_eb.last_name AS eb_namelast, ct_eb.first_name AS eb_namefirst, " +
            "ct_at.last_name AS at_namelast, ct_at.first_name AS at_namefirst " +
            "FROM ticketlog t " +
            "LEFT JOIN ticket_category tc ON (t.cat_code = tc.id) " +
            "LEFT JOIN users ct_eb ON (t.enteredby = ct_eb.user_id) " +
            "LEFT JOIN users ct_at ON (t.assigned_to = ct_at.user_id) " +
            "LEFT JOIN ticket_priority tp ON (t.pri_code = tp.code) " +
            "LEFT JOIN ticket_severity ts ON (t.scode = ts.code) " +
            "WHERE t.id > 0 ");
    pst = db.prepareStatement(sqlSelect.toString() + sqlFilter.toString() + sqlOrder.toString());
    items = prepareFilter(pst);
    rs = pst.executeQuery();
    if (pagedListInfo != null) {
      pagedListInfo.doManualOffset(db, rs);
    }
    TicketLog prevTicketLog = null;
    int count = 0;
    while (rs.next()) {
      if (pagedListInfo != null && pagedListInfo.getItemsPerPage() > 0 &&
          DatabaseUtils.getType(db) == DatabaseUtils.MSSQL &&
          count >= pagedListInfo.getItemsPerPage()) {
        break;
      }
      ++count;
      TicketLog thisTicketLog = new TicketLog(rs);
      if (doSystemMessages) {
        //Add the system generated messages
        this.setSystemMessages(thisTicketLog, prevTicketLog);
        if (thisTicketLog.getEntryText() != null && !thisTicketLog.getEntryText().equals("")) {
          //Add the comments
          this.add(thisTicketLog);
        }
      } else {
        //Just add the whole thing
        this.add(thisTicketLog);
      }
      prevTicketLog = thisTicketLog;
    }
    rs.close();
    pst.close();
  }


  /**
   * Builds a base SQL where statement for filtering records to be used by
   * sqlSelect and sqlCount
   *
   * @param sqlFilter Description of Parameter
   * @since 1.2
   */
  private void createFilter(StringBuffer sqlFilter) {
    if (ticketId > -1) {
      sqlFilter.append("AND t.ticketid = ? ");
    }
  }


  /**
   * Sets the parameters for the preparedStatement - these items must
   * correspond with the createFilter statement
   *
   * @param pst Description of Parameter
   * @return Description of the Returned Value
   * @throws SQLException Description of Exception
   * @since 1.2
   */
  private int prepareFilter(PreparedStatement pst) throws SQLException {
    int i = 0;
    if (ticketId > -1) {
      pst.setInt(++i, ticketId);
    }
    return i;
  }


  /**
   * Returns the string or if null returns unassigned
   *
   * @param value Description of the Parameter
   * @return The assigned value
   */
  private String isAssigned(String value) {
    if (value == null) {
      return "unassigned";
    } else {
      return value;
    }
  }
}

