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
 * @version $Id: WikiList.java,v 1.11.6.1 2004/08/26 15:54:32 matt Exp
 *          $
 * @created February 7, 2006
 */
public class WikiList extends ArrayList<Wiki> {

  private int projectId = -1;
  private PagedListInfo pagedListInfo = null;
  private int enteredBy = -1;
  private int modifiedBy = -1;
  private int forUser = -1;
  private String subject = null;
  private int wikiId = -1;
  private String withFormName = null;
  //calendar
  protected java.sql.Timestamp alertRangeStart = null;
  protected java.sql.Timestamp alertRangeEnd = null;


  public WikiList() {
  }


  public void setProjectId(int tmp) {
    this.projectId = tmp;
  }


  public void setProjectId(String tmp) {
    this.projectId = Integer.parseInt(tmp);
  }

  public int getProjectId() {
    return projectId;
  }

  public int getEnteredBy() {
    return enteredBy;
  }

  public void setEnteredBy(int enteredBy) {
    this.enteredBy = enteredBy;
  }

  public int getModifiedBy() {
    return modifiedBy;
  }

  public void setModifiedBy(int modifiedBy) {
    this.modifiedBy = modifiedBy;
  }

  public void setPagedListInfo(PagedListInfo tmp) {
    this.pagedListInfo = tmp;
  }

  public PagedListInfo getPagedListInfo() {
    return pagedListInfo;
  }

  public void setForUser(int tmp) {
    this.forUser = tmp;
  }

  public void setForUser(String tmp) {
    this.forUser = Integer.parseInt(tmp);
  }

  public int getForUser() {
    return forUser;
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

  public String getSubject() {
    return subject;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public int getWikiId() {
    return wikiId;
  }

  public void setWikiId(int wikiId) {
    this.wikiId = wikiId;
  }

  public void setWikiId(String tmp) {
    this.wikiId = Integer.parseInt(tmp);
  }

  public String getWithFormName() {
    return withFormName;
  }

  public void setWithFormName(String withFormName) {
    this.withFormName = withFormName;
  }

  public int queryCount(Connection db) throws SQLException {
    int count = 0;
    StringBuffer sqlCount = new StringBuffer();
    StringBuffer sqlFilter = new StringBuffer();
    sqlCount.append(
        "SELECT COUNT(*) AS recordcount " +
            "FROM project_wiki w " +
            "WHERE w.wiki_id > -1 ");
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
            "FROM project_wiki w " +
            "WHERE w.wiki_id > -1 ");
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
    pagedListInfo.setDefaultSort("w.subject", null);
    pagedListInfo.appendSqlTail(db, sqlOrder);
    //Need to build a base SQL statement for returning records
    if (pagedListInfo != null) {
      pagedListInfo.appendSqlSelectHead(db, sqlSelect);
    } else {
      sqlSelect.append("SELECT ");
    }
    sqlSelect.append(
        "w.* " +
            "FROM project_wiki w " +
            "WHERE w.wiki_id > -1 ");
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
      Wiki thisRecord = new Wiki(rs);
      this.add(thisRecord);
    }
    rs.close();
    pst.close();
  }


  protected void createFilter(StringBuffer sqlFilter) {
    if (projectId > 0) {
      sqlFilter.append("AND w.project_id = ? ");
    }
    if (forUser > -1) {
      sqlFilter.append("AND (w.project_id IN (SELECT DISTINCT project_id FROM project_team WHERE user_id = ? " +
          "AND status IS NULL) OR w.project_id IN (SELECT project_id FROM projects WHERE allow_guests = ? AND approvaldate IS NOT NULL)) ");
    }
    if (alertRangeStart != null) {
      sqlFilter.append("AND w.modified >= ? ");
    }
    if (alertRangeEnd != null) {
      sqlFilter.append("AND w.modified < ? ");
    }
    if (subject != null) {
      sqlFilter.append("AND w.subject = ? ");
    }
    if (wikiId > -1) {
      sqlFilter.append("AND w.wiki_id = ? ");
    }
    if (withFormName != null) {
      sqlFilter.append("AND w.content LIKE ? ");
    }
  }


  protected int prepareFilter(PreparedStatement pst) throws SQLException {
    int i = 0;
    if (projectId > 0) {
      pst.setInt(++i, projectId);
    }
    if (forUser > -1) {
      pst.setInt(++i, forUser);
      pst.setBoolean(++i, true);
    }
    if (alertRangeStart != null) {
      pst.setTimestamp(++i, alertRangeStart);
    }
    if (alertRangeEnd != null) {
      pst.setTimestamp(++i, alertRangeEnd);
    }
    if (subject != null) {
      pst.setString(++i, subject);
    }
    if (wikiId > -1) {
      pst.setInt(++i, wikiId);
    }
    if (withFormName != null) {
      pst.setString(++i, "%[{form name=\"" + withFormName + "\"}]%");
    }
    return i;
  }


  public static void delete(Connection db, int projectId) throws SQLException {
    WikiList wikiList = new WikiList();
    wikiList.setProjectId(projectId);
    wikiList.buildList(db);
    for (Wiki wiki : wikiList) {
      wiki.delete(db);
    }
  }


  public static Wiki queryBySubject(Connection db, String subject, int projectId) throws SQLException {
    Wiki wiki = null;
    PreparedStatement pst = db.prepareStatement(
        "SELECT w.* " +
            "FROM project_wiki w " +
            "WHERE w.project_id = ? " +
            "AND lower(subject) = ? ");
    pst.setInt(1, projectId);
    if (subject == null) {
      subject = "";
    }
    pst.setString(2, subject.toLowerCase());
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      wiki = new Wiki(rs);
    } else {
      wiki = new Wiki();
      wiki.setSubject(subject);
      wiki.setProjectId(projectId);
    }
    rs.close();
    pst.close();
    return wiki;
  }

  public void insert(Connection db) throws SQLException {
    for (Wiki wiki : this) {
      wiki.setId(-1);
      wiki.setProjectId(projectId);
      wiki.setEnteredBy(enteredBy);
      wiki.setModifiedBy(modifiedBy);
      wiki.setEntered((Timestamp) null);
      wiki.setModified((Timestamp) null);
      wiki.insert(db);
      if (System.getProperty("DEBUG") != null) {
        System.out.println("WikiList-> Inserted: " + wiki.getId());
      }
    }
  }

  public static boolean checkExistsBySubject(Connection db, String subject, int projectId) throws SQLException {
    if (db == null) {
      return false;
    }
    boolean result = false;
    PreparedStatement pst = db.prepareStatement(
        "SELECT wiki_id " +
            "FROM project_wiki w " +
            "WHERE w.project_id = ? " +
            "AND lower(subject) = ? ");
    pst.setInt(1, projectId);
    if (subject == null) {
      subject = "";
    }
    pst.setString(2, subject.toLowerCase());
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      result = true;
    }
    rs.close();
    pst.close();
    return result;
  }
}
