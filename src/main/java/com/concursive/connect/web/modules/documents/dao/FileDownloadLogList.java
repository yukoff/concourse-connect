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

package com.concursive.connect.web.modules.documents.dao;

import com.concursive.commons.db.DatabaseUtils;
import com.concursive.connect.web.utils.PagedListInfo;

import java.sql.*;
import java.util.ArrayList;

/**
 * Description of the Class
 *
 * @author matt rajkowski
 * @version $Id$
 * @created January 15, 2003
 */
public class FileDownloadLogList extends ArrayList<FileDownloadLog> {

  PagedListInfo pagedListInfo = null;
  private int linkModuleId = -1;
  private int linkItemId = -1;
  private Timestamp downloadsRangeStart = null;
  private Timestamp downloadsRangeEnd = null;

  /**
   * Constructor for the FileDownloadLogList object
   */
  public FileDownloadLogList() {
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
   * @return the linkModuleId
   */
  public int getLinkModuleId() {
    return linkModuleId;
  }


  /**
   * @param linkModuleId the linkModuleId to set
   */
  public void setLinkModuleId(int linkModuleId) {
    this.linkModuleId = linkModuleId;
  }

  public void setLinkModuleId(String linkModuleId) {
    this.linkModuleId = Integer.parseInt(linkModuleId);
  }

  /**
   * @return the linkItemId
   */
  public int getLinkItemId() {
    return linkItemId;
  }


  /**
   * @param linkItemId the linkItemId to set
   */
  public void setLinkItemId(int linkItemId) {
    this.linkItemId = linkItemId;
  }

  public void setLinkItemId(String linkItemId) {
    this.linkItemId = Integer.parseInt(linkItemId);
  }


  /**
   * @return the downloadsRangeStart
   */
  public Timestamp getDownloadsRangeStart() {
    return downloadsRangeStart;
  }


  /**
   * @param downloadsRangeStart the downloadsRangeStart to set
   */
  public void setDownloadsRangeStart(Timestamp downloadsRangeStart) {
    this.downloadsRangeStart = downloadsRangeStart;
  }

  public void setDownloadsRangeStart(String downloadsRangeStart) {
    this.downloadsRangeStart = DatabaseUtils.parseTimestamp(downloadsRangeStart);
  }

  /**
   * @return the downloadsRangeEnd
   */
  public Timestamp getDownloadsRangeEnd() {
    return downloadsRangeEnd;
  }


  /**
   * @param downloadsRangeEnd the downloadsRangeEnd to set
   */
  public void setDownloadsRangeEnd(Timestamp downloadsRangeEnd) {
    this.downloadsRangeEnd = downloadsRangeEnd;
  }

  public void setDownloadsRangeEnd(String downloadsRangeEnd) {
    this.downloadsRangeEnd = DatabaseUtils.parseTimestamp(downloadsRangeEnd);
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
    //Need to build a base SQL statement for counting records
    sqlCount.append(
        "SELECT COUNT(*) AS recordcount " +
            "FROM project_files_download d " +
            "LEFT JOIN project_files f ON (d.item_id = f.item_id) " +
            "WHERE f.item_id > -1 ");
    createFilter(sqlFilter);
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
      //Determine the offset, based on the filter, for the first record to show
      if (!pagedListInfo.getCurrentLetter().equals("")) {
        pst = db.prepareStatement(sqlCount.toString() +
            sqlFilter.toString() +
            "AND lower(f.subject) < ? ");
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
      pagedListInfo.setDefaultSort("d.download_date ", null);
      pagedListInfo.appendSqlTail(db, sqlOrder);
    } else {
      sqlOrder.append("ORDER BY d.download_date ");
    }
    //Build a base SQL statement for returning records
    if (pagedListInfo != null) {
      pagedListInfo.appendSqlSelectHead(db, sqlSelect);
    } else {
      sqlSelect.append("SELECT ");
    }
    // TODO: Make the following work with SQL Server
    sqlSelect.append("d.item_id, d.version, user_download_id, download_date, enteredby, modifiedby " +
        "FROM project_files_download d " +
        "LEFT JOIN project_files f ON (d.item_id = f.item_id) " +
        "WHERE d.item_id > -1 ");
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
      FileDownloadLog thisItem = new FileDownloadLog(rs);
      this.add(thisItem);
    }
    rs.close();
    pst.close();
  }

  private void createFilter(StringBuffer sqlFilter) {
    if (sqlFilter == null) {
      sqlFilter = new StringBuffer();
    }
    if (linkModuleId > -1) {
      sqlFilter.append("AND f.link_module_id = ? ");
    }
    if (linkItemId > -1) {
      sqlFilter.append("AND f.link_item_id = ? ");
    }
    if (downloadsRangeStart != null) {
      sqlFilter.append("AND d.download_date >= ? ");
    }
    if (downloadsRangeEnd != null) {
      sqlFilter.append("AND d.download_date < ? ");
    }
  }

  private int prepareFilter(PreparedStatement pst) throws SQLException {
    int i = 0;
    if (linkModuleId > -1) {
      pst.setInt(++i, linkModuleId);
    }
    if (linkItemId > -1) {
      pst.setInt(++i, linkItemId);
    }
    if (downloadsRangeStart != null) {
      pst.setTimestamp(++i, downloadsRangeStart);
    }
    if (downloadsRangeEnd != null) {
      pst.setTimestamp(++i, downloadsRangeEnd);
    }
    return i;
  }

}

