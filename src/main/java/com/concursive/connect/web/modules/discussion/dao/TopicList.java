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

package com.concursive.connect.web.modules.discussion.dao;

import com.concursive.commons.db.DatabaseUtils;
import com.concursive.connect.Constants;
import com.concursive.connect.web.utils.HtmlSelect;
import com.concursive.connect.web.utils.PagedListInfo;

import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Contains a collection of discussion forum topics
 *
 * @author matt rajkowski
 * @created July 23, 2001
 */
public class TopicList extends ArrayList<Topic> {
  //filters
  private PagedListInfo pagedListInfo = null;
  private String emptyHtmlSelectRecord = null;
  private int lastIssues = -1;
  private int projectId = -1;
  private int categoryId = -1;
  private int forUser = -1;
  private int projectCategoryId = -1;
  private int publicProjectIssues = Constants.UNDEFINED;
  private int forParticipant = Constants.UNDEFINED;
  //calendar
  protected Timestamp alertRangeStart = null;
  protected Timestamp alertRangeEnd = null;

  protected Timestamp enteredRangeStart = null;
  protected Timestamp enteredRangeEnd = null;

  /**
   * Constructor for the IssueList object
   */
  public TopicList() {
  }


  /**
   * Sets the pagedListInfo attribute of the IssueList object
   *
   * @param tmp The new pagedListInfo value
   */
  public void setPagedListInfo(PagedListInfo tmp) {
    this.pagedListInfo = tmp;
  }


  /**
   * Sets the emptyHtmlSelectRecord attribute of the IssueList object
   *
   * @param tmp The new emptyHtmlSelectRecord value
   */
  public void setEmptyHtmlSelectRecord(String tmp) {
    this.emptyHtmlSelectRecord = tmp;
  }


  /**
   * Sets the lastIssues attribute of the IssueList object
   *
   * @param tmp The new lastIssues value
   */
  public void setLastIssues(int tmp) {
    this.lastIssues = tmp;
  }

  public void setLastIssues(String tmp) {
    this.lastIssues = Integer.parseInt(tmp);
  }


  /**
   * Sets the projectId attribute of the IssueList object
   *
   * @param tmp The new projectId value
   */
  public void setProjectId(int tmp) {
    this.projectId = tmp;
  }


  public void setProjectId(String tmp) {
    this.projectId = Integer.parseInt(tmp);
  }


  /**
   * Sets the categoryId attribute of the IssueList object
   *
   * @param tmp The new categoryId value
   */
  public void setCategoryId(int tmp) {
    this.categoryId = tmp;
  }

  public void setCategoryId(String tmp) {
    this.categoryId = Integer.parseInt(tmp);
  }


  /**
   * Sets the forUser attribute of the IssueList object
   *
   * @param tmp The new forUser value
   */
  public void setForUser(int tmp) {
    this.forUser = tmp;
  }


  /**
   * Sets the forUser attribute of the IssueList object
   *
   * @param tmp The new forUser value
   */
  public void setForUser(String tmp) {
    this.forUser = Integer.parseInt(tmp);
  }


  /**
   * Sets the alertRangeStart attribute of the IssueList object
   *
   * @param tmp The new alertRangeStart value
   */
  public void setAlertRangeStart(java.sql.Timestamp tmp) {
    this.alertRangeStart = tmp;
  }


  /**
   * Sets the alertRangeStart attribute of the IssueList object
   *
   * @param tmp The new alertRangeStart value
   */
  public void setAlertRangeStart(String tmp) {
    this.alertRangeStart = DatabaseUtils.parseTimestamp(tmp);
  }


  /**
   * Sets the alertRangeEnd attribute of the IssueList object
   *
   * @param tmp The new alertRangeEnd value
   */
  public void setAlertRangeEnd(java.sql.Timestamp tmp) {
    this.alertRangeEnd = tmp;
  }


  /**
   * Sets the alertRangeEnd attribute of the IssueList object
   *
   * @param tmp The new alertRangeEnd value
   */
  public void setAlertRangeEnd(String tmp) {
    this.alertRangeEnd = DatabaseUtils.parseTimestamp(tmp);
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


  /**
   * Gets the htmlSelect attribute of the IssueList object
   *
   * @param selectName Description of the Parameter
   * @return The htmlSelect value
   */
  public String getHtmlSelect(String selectName) {
    return getHtmlSelect(selectName, -1);
  }


  /**
   * Gets the htmlSelect attribute of the IssueList object
   *
   * @param selectName Description of the Parameter
   * @param defaultKey Description of the Parameter
   * @return The htmlSelect value
   */
  public String getHtmlSelect(String selectName, int defaultKey) {
    HtmlSelect listSelect = new HtmlSelect();
    if (emptyHtmlSelectRecord != null) {
      listSelect.addItem(-1, emptyHtmlSelectRecord);
    }
    Iterator i = this.iterator();
    while (i.hasNext()) {
      Topic thisTopic = (Topic) i.next();
      listSelect.addItem(
          thisTopic.getId(),
          thisTopic.getSubject());
    }
    return listSelect.getHtml(selectName, defaultKey);
  }


  /**
   * Gets the pagedListInfo attribute of the IssueList object
   *
   * @return The pagedListInfo value
   */
  public PagedListInfo getPagedListInfo() {
    return pagedListInfo;
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
   * @return the publicProjectIssues
   */
  public int getPublicProjectIssues() {
    return publicProjectIssues;
  }


  /**
   * @param publicProjectIssues the publicProjectIssues to set
   */
  public void setPublicProjectIssues(int publicProjectIssues) {
    this.publicProjectIssues = publicProjectIssues;
  }


  public void setPublicProjectIssues(String publicProjectIssues) {
    this.publicProjectIssues = Integer.parseInt(publicProjectIssues);
  }

  public int getForParticipant() {
    return forParticipant;
  }

  public void setForParticipant(int forParticipant) {
    this.forParticipant = forParticipant;
  }

  public void setForParticipant(String tmp) {
    forParticipant = DatabaseUtils.parseBooleanToConstant(tmp);
  }


  /**
   * Gets the alertRangeStart attribute of the IssueList object
   *
   * @return The alertRangeStart value
   */
  public java.sql.Timestamp getAlertRangeStart() {
    return alertRangeStart;
  }


  /**
   * Gets the alertRangeEnd attribute of the IssueList object
   *
   * @return The alertRangeEnd value
   */
  public java.sql.Timestamp getAlertRangeEnd() {
    return alertRangeEnd;
  }


  /**
   * Gets the forUser attribute of the IssueList object
   *
   * @return The forUser value
   */
  public int getForUser() {
    return forUser;
  }

  public int queryCount(Connection db) throws SQLException {
    int count = 0;
    StringBuffer sqlCount = new StringBuffer();
    StringBuffer sqlFilter = new StringBuffer();
    sqlCount.append(
        "SELECT COUNT(*) AS recordcount " +
            "FROM project_issues i " +
            "WHERE i.issue_id > -1 ");
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
            "FROM project_issues i " +
            "WHERE i.issue_id > -1 ");
    createFilter(sqlFilter);
    if (pagedListInfo == null) {
      pagedListInfo = new PagedListInfo();
      pagedListInfo.setItemsPerPage(lastIssues);
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
          "AND lower(subject) < ? ");
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
    pagedListInfo.setDefaultSort("entered", "desc");
    pagedListInfo.appendSqlTail(db, sqlOrder);
    //Need to build a base SQL statement for returning records
    pagedListInfo.appendSqlSelectHead(db, sqlSelect);
    sqlSelect.append(
        "i.* " +
            "FROM project_issues i " +
            "WHERE i.issue_id > -1 ");
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
      Topic thisTopic = new Topic(rs);
      this.add(thisTopic);
    }
    rs.close();
    pst.close();
  }


  /**
   * Description of the Method
   *
   * @param sqlFilter Description of the Parameter
   */
  private void createFilter(StringBuffer sqlFilter) {
    if (sqlFilter == null) {
      sqlFilter = new StringBuffer();
    }
    if (projectId > -1) {
      sqlFilter.append("AND project_id = ? ");
    }
    if (categoryId > -1) {
      sqlFilter.append("AND category_id = ? ");
    }
    if (alertRangeStart != null) {
      sqlFilter.append("AND i.last_reply_date >= ? ");
    }
    if (alertRangeEnd != null) {
      sqlFilter.append("AND i.last_reply_date < ? ");
    }
    if (enteredRangeStart != null) {
      sqlFilter.append("AND i.entered >= ? ");
    }
    if (enteredRangeEnd != null) {
      sqlFilter.append("AND i.entered < ? ");
    }
    if (forUser > -1) {
      sqlFilter.append("AND (i.project_id IN (SELECT DISTINCT project_id FROM project_team WHERE user_id = ? " +
          "AND status IS NULL) OR i.project_id IN (SELECT project_id FROM projects WHERE allow_guests = ? AND approvaldate IS NOT NULL)) ");
    }
    if (publicProjectIssues == Constants.TRUE) {
      sqlFilter.append("AND i.project_id IN ( SELECT project_id FROM projects WHERE allow_guests = ? AND approvaldate IS NOT NULL ) ");
    }
    if (forParticipant == Constants.TRUE) {
      sqlFilter.append("AND i.project_id IN ( SELECT project_id FROM projects WHERE (allows_user_observers = ? OR allow_guests = ?) AND approvaldate IS NOT NULL ) ");
    }
    if (projectCategoryId != -1) {
      sqlFilter.append(" AND i.project_id IN ( SELECT project_id FROM projects WHERE category_id = ? ) ");
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
    if (projectId > -1) {
      pst.setInt(++i, projectId);
    }
    if (categoryId > -1) {
      pst.setInt(++i, categoryId);
    }
    if (alertRangeStart != null) {
      pst.setTimestamp(++i, alertRangeStart);
    }
    if (alertRangeEnd != null) {
      pst.setTimestamp(++i, alertRangeEnd);
    }
    if (enteredRangeStart != null) {
      pst.setTimestamp(++i, enteredRangeStart);
    }
    if (enteredRangeEnd != null) {
      pst.setTimestamp(++i, enteredRangeEnd);
    }
    if (forUser > -1) {
      pst.setInt(++i, forUser);
      pst.setBoolean(++i, true);
    }
    if (publicProjectIssues == Constants.TRUE) {
      pst.setBoolean(++i, true);
    }
    if (forParticipant == Constants.TRUE) {
      pst.setBoolean(++i, true);
      pst.setBoolean(++i, true);
    }
    if (projectCategoryId != -1) {
      pst.setInt(++i, projectCategoryId);
    }
    return i;
  }

  public void delete(Connection db, String basePath) throws SQLException {
    Iterator i = this.iterator();
    while (i.hasNext()) {
      Topic thisTopic = (Topic) i.next();
      thisTopic.delete(db, basePath);
    }
  }
}

