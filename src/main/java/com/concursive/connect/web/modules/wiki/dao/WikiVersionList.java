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

package com.concursive.connect.web.modules.wiki.dao;

import com.concursive.commons.db.DatabaseUtils;
import com.concursive.connect.web.utils.PagedListInfo;

import java.sql.*;
import java.util.ArrayList;

/**
 * Contains a collection of wiki pages
 *
 * @author matt rajkowski
 * @version $Id: WikiVersionList.java,v 1.11.6.1 2004/08/26 15:54:32 matt Exp
 *          $
 * @created February 7, 2006
 */
public class WikiVersionList extends ArrayList<WikiVersion> {

  private PagedListInfo pagedListInfo = null;
  private int wikiId = -1;
  private Timestamp enteredRangeStart = null;
  private Timestamp enteredRangeEnd = null;


  public WikiVersionList() {
  }

  public int getWikiId() {
    return wikiId;
  }

  public void setWikiId(int wikiId) {
    this.wikiId = wikiId;
  }

  public void setWikiId(String wikiId) {
    this.wikiId = Integer.parseInt(wikiId);
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
            "FROM project_wiki_version wv " +
            "WHERE wv.version_id > -1 ");
    createFilter(sqlFilter);
    if (pagedListInfo == null) {
      pagedListInfo = new PagedListInfo();
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
    pagedListInfo.setDefaultSort("wv.entered", "desc");
    pagedListInfo.appendSqlTail(db, sqlOrder);
    //Need to build a base SQL statement for returning records
    if (pagedListInfo != null) {
      pagedListInfo.appendSqlSelectHead(db, sqlSelect);
    } else {
      sqlSelect.append("SELECT ");
    }
    sqlSelect.append(
        "wv.* " +
            "FROM project_wiki_version wv " +
            "WHERE wv.version_id > -1 ");
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
      WikiVersion thisRecord = new WikiVersion(rs);
      this.add(thisRecord);
    }
    rs.close();
    pst.close();
  }

  protected void createFilter(StringBuffer sqlFilter) {
    if (wikiId > -1) {
      sqlFilter.append("AND wv.wiki_id = ? ");
    }
    if (enteredRangeStart != null) {
      sqlFilter.append("AND wv.entered >= ? ");
    }
    if (enteredRangeEnd != null) {
      sqlFilter.append("AND wv.entered < ? ");
    }
  }


  protected int prepareFilter(PreparedStatement pst) throws SQLException {
    int i = 0;
    if (wikiId > -1) {
      pst.setInt(++i, wikiId);
    }
    if (enteredRangeStart != null) {
      pst.setTimestamp(++i, enteredRangeStart);
    }
    if (enteredRangeEnd != null) {
      pst.setTimestamp(++i, enteredRangeEnd);
    }
    return i;
  }

}
