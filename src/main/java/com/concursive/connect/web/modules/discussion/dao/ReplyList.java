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
import com.concursive.connect.web.utils.PagedListInfo;

import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * A collection of discussion topic replies
 *
 * @author matt rajkowski
 * @created January 15, 2003
 */
public class ReplyList extends ArrayList<Reply> {

  private PagedListInfo pagedListInfo = null;
  private int lastReplies = -1;
  private int issueId = -1;
  private int replyTo = -1;
  private Timestamp solutionDateStart = null;
  private Timestamp solutionDateEnd = null;
  private int solutionsOnly = Constants.UNDEFINED;
  private int publicProjectIssueReplies = Constants.UNDEFINED;
  // resources
  private boolean buildFiles = false;
  private int categoryId = -1;
  private int projectId = -1;

  /**
   * Constructor for the IssueReplyList object
   */
  public ReplyList() {
  }


  /**
   * Sets the pagedListInfo attribute of the IssueReplyList object
   *
   * @param tmp The new pagedListInfo value
   */
  public void setPagedListInfo(PagedListInfo tmp) {
    this.pagedListInfo = tmp;
  }


  /**
   * Sets the lastReplies attribute of the IssueReplyList object
   *
   * @param tmp The new lastReplies value
   */
  public void setLastReplies(int tmp) {
    this.lastReplies = tmp;
  }

  public void setLastReplies(String tmp) {
    this.lastReplies = Integer.parseInt(tmp);
  }


  /**
   * Sets the issueId attribute of the IssueReplyList object
   *
   * @param tmp The new issueId value
   */
  public void setIssueId(int tmp) {
    this.issueId = tmp;
  }

  public void setIssueId(String tmp) {
    this.issueId = Integer.parseInt(tmp);
  }


  /**
   * @return the replyTo
   */
  public int getReplyTo() {
    return replyTo;
  }


  /**
   * @param replyTo the replyTo to set
   */
  public void setReplyTo(int replyTo) {
    this.replyTo = replyTo;
  }


  public void setReplyTo(String replyTo) {
    this.replyTo = Integer.parseInt(replyTo);
  }


  public void setBuildFiles(boolean buildFiles) {
    this.buildFiles = buildFiles;
  }

  public void setProjectId(int projectId) {
    this.projectId = projectId;
  }

  public void setProjectId(String tmp) {
    this.projectId = Integer.parseInt(tmp);
  }

  public void setCategoryId(int categoryId) {
    this.categoryId = categoryId;
  }

  public void setCategoryId(String tmp) {
    this.categoryId = Integer.parseInt(tmp);
  }

  /**
   * @return the solutionDateStart
   */
  public Timestamp getSolutionDateStart() {
    return solutionDateStart;
  }


  /**
   * @param solutionDateStart the solutionDateStart to set
   */
  public void setSolutionDateStart(Timestamp solutionDateStart) {
    this.solutionDateStart = solutionDateStart;
  }

  public void setSolutionDateStart(String solutionDateStart) {
    this.solutionDateStart = DatabaseUtils.parseTimestamp(solutionDateStart);
  }

  /**
   * @return the solutionDateEnd
   */
  public Timestamp getSolutionDateEnd() {
    return solutionDateEnd;
  }


  /**
   * @param solutionDateEnd the solutionDateEnd to set
   */
  public void setSolutionDateEnd(Timestamp solutionDateEnd) {
    this.solutionDateEnd = solutionDateEnd;
  }

  public void setSolutionDateEnd(String solutionDateEnd) {
    this.solutionDateEnd = DatabaseUtils.parseTimestamp(solutionDateEnd);
  }

  /**
   * @return the solutionsOnly
   */
  public int getSolutionsOnly() {
    return solutionsOnly;
  }


  /**
   * @param solutionsOnly the solutionsOnly to set
   */
  public void setSolutionsOnly(int solutionsOnly) {
    this.solutionsOnly = solutionsOnly;
  }

  public void setSolutionsOnly(String solutionsOnly) {
    this.solutionsOnly = Integer.parseInt(solutionsOnly);
  }

  /**
   * @return the publicProjectIssueReplies
   */
  public int getPublicProjectIssueReplies() {
    return publicProjectIssueReplies;
  }


  /**
   * @param publicProjectIssueReplies the publicProjectIssueReplies to set
   */
  public void setPublicProjectIssueReplies(int publicProjectIssueReplies) {
    this.publicProjectIssueReplies = publicProjectIssueReplies;
  }

  public void setPublicProjectIssueReplies(String publicProjectIssueReplies) {
    this.publicProjectIssueReplies = Integer.parseInt(publicProjectIssueReplies);
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
    sqlCount.append(
        "SELECT COUNT(*) AS recordcount " +
            "FROM project_issue_replies r " +
            "WHERE r.reply_id > -1 ");

    createFilter(sqlFilter);

    if (pagedListInfo == null) {
      pagedListInfo = new PagedListInfo();
      pagedListInfo.setItemsPerPage(lastReplies);
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
    pagedListInfo.setDefaultSort("entered", null);
    pagedListInfo.appendSqlTail(db, sqlOrder);

    //Need to build a base SQL statement for returning records
    pagedListInfo.appendSqlSelectHead(db, sqlSelect);
    sqlSelect.append(
        "r.* " +
            "FROM project_issue_replies r " +
            "WHERE r.reply_id > -1 ");

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
      Reply thisReply = new Reply(rs);
      this.add(thisReply);
    }
    rs.close();
    pst.close();
    // build resources
    if (buildFiles) {
      Iterator i = this.iterator();
      while (i.hasNext()) {
        Reply thisReply = (Reply) i.next();
        thisReply.buildFiles(db);
      }
    }
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
    if (issueId > -1) {
      sqlFilter.append("AND issue_id = ? ");
    }
    if (replyTo > -1) {
      sqlFilter.append("AND reply_to = ? ");
    }
    if (solutionDateStart != null) {
      sqlFilter.append("AND solution_date >= ? ");
    }
    if (solutionDateEnd != null) {
      sqlFilter.append("AND solution_date < ? ");
    }
    if (solutionsOnly != Constants.UNDEFINED) {
      if (solutionsOnly == Constants.TRUE) {
        sqlFilter.append("AND solution = ? ");
      } else {
        sqlFilter.append("AND (solution = ? OR solution IS NULL)");
      }
    }
    if (publicProjectIssueReplies == Constants.TRUE) {
      sqlFilter.append("AND issue_id IN ( SELECT issue_id FROM project_issues WHERE project_id IN ( SELECT project_id FROM projects WHERE allow_guests = ? AND approvaldate IS NOT NULL ) ) ");
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

    if (issueId > -1) {
      pst.setInt(++i, issueId);
    }
    if (replyTo > -1) {
      pst.setInt(++i, replyTo);
    }
    if (solutionDateStart != null) {
      pst.setTimestamp(++i, solutionDateStart);
    }
    if (solutionDateEnd != null) {
      pst.setTimestamp(++i, solutionDateEnd);
    }
    if (solutionsOnly != Constants.UNDEFINED) {
      if (solutionsOnly == Constants.TRUE) {
        pst.setBoolean(++i, true);
      } else {
        pst.setBoolean(++i, false);
      }
    }
    if (publicProjectIssueReplies == Constants.TRUE) {
      pst.setBoolean(++i, true);
    }
    return i;
  }

  public void delete(Connection db, String basePath) throws SQLException {
    for (Reply thisReply : this) {
      thisReply.setProjectId(projectId);
      thisReply.setCategoryId(categoryId);
      thisReply.delete(db, basePath);
    }
  }
}

