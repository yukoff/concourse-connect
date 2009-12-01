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
package com.concursive.connect.web.modules.processes.dao;

import com.concursive.commons.db.DatabaseUtils;
import com.concursive.connect.Constants;
import com.concursive.connect.web.utils.PagedListInfo;

import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Manages dao to the process_log table
 *
 * @author Nanda Kumar
 * @created November 23, 2009
 */
public class ProcessList extends ArrayList<Process> {

  // filter variables
  private String description = null;
  private int enabled = Constants.UNDEFINED;
  private Timestamp enteredStart = null;
  private Timestamp enteredEnd = null;
  private Timestamp modifiedStart = null;
  private Timestamp modifiedEnd = null;
  private Timestamp processedStart = null;
  private Timestamp processedEnd = null;
  private long longValue = -1;
  private PagedListInfo pagedListInfo = null;

  public ProcessList() {
  }


  /**
   * @param description the description to set
   */
  public void setDescription(String description) {
    this.description = description;
  }


  /**
   * @return the description
   */
  public String getDescription() {
    return description;
  }


  /**
   * @param enabled the enabled to set
   */
  public void setEnabled(int enabled) {
    this.enabled = enabled;
  }


  /**
   * @return the enabled
   */
  public int getEnabled() {
    return enabled;
  }


  /**
   * @param enteredStart the enteredStart to set
   */
  public void setEnteredStart(Timestamp enteredStart) {
    this.enteredStart = enteredStart;
  }


  /**
   * @return the enteredStart
   */
  public Timestamp getEnteredStart() {
    return enteredStart;
  }


  /**
   * @param enteredEnd the enteredEnd to set
   */
  public void setEnteredEnd(Timestamp enteredEnd) {
    this.enteredEnd = enteredEnd;
  }


  /**
   * @return the enteredEnd
   */
  public Timestamp getEnteredEnd() {
    return enteredEnd;
  }


  /**
   * @param modifiedStart the modifiedStart to set
   */
  public void setModifiedStart(Timestamp modifiedStart) {
    this.modifiedStart = modifiedStart;
  }


  /**
   * @return the modifiedStart
   */
  public Timestamp getModifiedStart() {
    return modifiedStart;
  }


  /**
   * @param modifiedEnd the modifiedEnd to set
   */
  public void setModifiedEnd(Timestamp modifiedEnd) {
    this.modifiedEnd = modifiedEnd;
  }


  /**
   * @return the modifiedEnd
   */
  public Timestamp getModifiedEnd() {
    return modifiedEnd;
  }


  /**
   * @param processedStart the processedStart to set
   */
  public void setProcessedStart(Timestamp processedStart) {
    this.processedStart = processedStart;
  }


  /**
   * @return the processedStart
   */
  public Timestamp getProcessedStart() {
    return processedStart;
  }


  /**
   * @param processedEnd the processedEnd to set
   */
  public void setProcessedEnd(Timestamp processedEnd) {
    this.processedEnd = processedEnd;
  }


  /**
   * @return the processedEnd
   */
  public Timestamp getProcessedEnd() {
    return processedEnd;
  }


  /**
   * @param longValue the long_value to set
   */
  public void setLongValue(long longValue) {
    this.longValue = longValue;
  }


  /**
   * @return the long_value
   */
  public long getLongValue() {
    return longValue;
  }


  /**
   * @param pagedListInfo the pagedListInfo to set
   */
  public void setPagedListInfo(PagedListInfo pagedListInfo) {
    this.pagedListInfo = pagedListInfo;
  }


  /**
   * @return the pagedListInfo
   */
  public PagedListInfo getPagedListInfo() {
    return pagedListInfo;
  }


  public void buildList(Connection db) throws SQLException {
    PreparedStatement pst;
    ResultSet rs;
    int items = -1;

    StringBuffer sqlSelect = new StringBuffer();
    StringBuffer sqlCount = new StringBuffer();
    StringBuffer sqlFilter = new StringBuffer();
    StringBuffer sqlOrder = new StringBuffer();
    //Need to build a base SQL statement for counting records
    sqlCount.append(
        "SELECT COUNT(*) AS recordcount " +
            "FROM process_log p " +
            "WHERE p.code > -1 ");
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
    pagedListInfo.setDefaultSort("p.code desc", null);
    pagedListInfo.appendSqlTail(db, sqlOrder);
    //Need to build a base SQL statement for returning records
    if (pagedListInfo != null) {
      pagedListInfo.appendSqlSelectHead(db, sqlSelect);
    } else {
      sqlSelect.append("SELECT ");
    }

    sqlSelect.append(
        "p.* " +
            "FROM process_log p " +
            "WHERE p.code > -1 ");

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
      Process process = new Process(rs);
      this.add(process);
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
    if (description != null) {
      sqlFilter.append("AND (description = ?) ");
    }
    if (enabled != Constants.UNDEFINED) {
      sqlFilter.append("AND (enabled = ?) ");
    }
    if (enteredStart != null) {
      sqlFilter.append("AND entered >= ? ");
    }
    if (enteredEnd != null) {
      sqlFilter.append("AND entered < ? ");
    }
    if (modifiedStart != null) {
      sqlFilter.append("AND modified >= ? ");
    }
    if (modifiedEnd != null) {
      sqlFilter.append("AND modified < ? ");
    }
    if (processedStart != null) {
      sqlFilter.append("AND processed >= ? ");
    }
    if (processedEnd != null) {
      sqlFilter.append("AND processed < ? ");
    }
    if (longValue > -1) {
      sqlFilter.append("AND long_value = ? ");
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
    if (description != null) {
      pst.setString(++i, description);
    }
    if (enabled != Constants.UNDEFINED) {
      pst.setBoolean(++i, (enabled == Constants.TRUE));
    }
    if (enteredStart != null) {
      pst.setTimestamp(++i, enteredStart);
    }
    if (enteredEnd != null) {
      pst.setTimestamp(++i, enteredEnd);
    }
    if (modifiedStart != null) {
      pst.setTimestamp(++i, modifiedStart);
    }
    if (modifiedEnd != null) {
      pst.setTimestamp(++i, modifiedEnd);
    }
    if (processedStart != null) {
      pst.setTimestamp(++i, processedStart);
    }
    if (processedEnd != null) {
      pst.setTimestamp(++i, processedEnd);
    }
    if (longValue > -1) {
      pst.setLong(++i, longValue);
    }
    return i;
  }

  public Process getSelectedFromCode(int code) {
    Iterator<Process> i = this.iterator();
    while (i.hasNext()) {
      Process process = i.next();
      if (code == -1 || process.getCode() == code) {
        return process;
      }
    }
    if (this.size() > 0) {
      return (Process) this.get(0);
    } else {
      return null;
    }
  }

  /**
   * Deletes all the processes in the list
   *
   * @param db - connection to the database
   * @return true, if there are no errors
   * @throws SQLException
   */
  public boolean delete(Connection db) throws SQLException {
    boolean commit = db.getAutoCommit();
    try {
      if (commit) {
        db.setAutoCommit(false);
      }
      for (Process process : this) {
        process.delete(db);
      }
      if (commit) {
        db.commit();
      }
      return true;
    } catch (Exception e) {
      if (commit) {
        db.rollback();
      }
      throw new SQLException(e.getMessage());
    } finally {
      if (commit) {
        db.setAutoCommit(true);
      }
    }
  }
}
