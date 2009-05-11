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

import com.concursive.commons.db.DatabaseUtils;
import com.concursive.connect.web.utils.PagedListInfo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Represents an array of Project Message Recipients who can receive a project message
 *
 * @author Ananth
 * @version ProjectMessageRecipientList.java Jul 25, 2008 4:14:27 PM Ananth $
 * @created Jul 25, 2008
 */
public class ProjectMessageRecipientList extends ArrayList<ProjectMessageRecipient> {
  private PagedListInfo pagedListInfo = null;

  private int messageId = -1;
  private int projectId = -1;

  /**
   * Gets the 'messageId' attribute of the ProjectMessageRecipientList object
   *
   * @return The 'messageId' value
   */
  public int getMessageId() {
    return messageId;
  }

  /**
   * Sets the 'messageId' attribute of the ProjectMessageRecipientList
   *
   * @param messageId The new 'messageId' value
   */
  public void setMessageId(int messageId) {
    this.messageId = messageId;
  }

  public void setMessageId(String messageId) {
    this.messageId = Integer.parseInt(messageId);
  }

  /**
   * Gets the 'projectId' attribute of the ProjectMessageRecipientList object
   *
   * @return The 'projectId' value
   */
  public int getProjectId() {
    return projectId;
  }

  /**
   * Sets the 'projectId' attribute of the ProjectMessageRecipientList
   *
   * @param projectId The new 'projectId' value
   */
  public void setProjectId(int projectId) {
    this.projectId = projectId;
  }

  public void setProjectId(String projectId) {
    this.projectId = Integer.parseInt(projectId);
  }

  public ProjectMessageRecipientList() {
  }

  public void setPagedListInfo(PagedListInfo tmp) {
    this.pagedListInfo = tmp;
  }

  public PagedListInfo getPagedListInfo() {
    return pagedListInfo;
  }

  public void select(Connection db) throws SQLException {
    buildList(db);
  }

  public void buildList(Connection db) throws SQLException {
    PreparedStatement pst = null;
    ResultSet rs;
    int items = -1;
    StringBuffer sqlSelect = new StringBuffer();
    StringBuffer sqlCount = new StringBuffer();
    StringBuffer sqlFilter = new StringBuffer();
    StringBuffer sqlOrder = new StringBuffer();
    //Need to build a base SQL statement for counting records
    sqlCount.append(
        "SELECT COUNT(*) AS recordcount " +
            "FROM project_msg_recipients pmr " +
            "LEFT JOIN contacts c ON (pmr.contact_id = c.contact_id) " +
            "WHERE pmr.recipient_id > -1 ");
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
    pagedListInfo.setDefaultSort("c.first_name, c.last_name", null);
    pagedListInfo.appendSqlTail(db, sqlOrder);
    //Need to build a base SQL statement for returning records
    if (pagedListInfo != null) {
      pagedListInfo.appendSqlSelectHead(db, sqlSelect);
    } else {
      sqlSelect.append("SELECT ");
    }
    sqlSelect.append(
        "pmr.*, c.first_name, c.last_name " +
            "FROM project_msg_recipients pmr " +
            "LEFT JOIN contacts c ON (pmr.contact_id = c.contact_id) " +
            "WHERE pmr.recipient_id > -1 ");
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
      ProjectMessageRecipient thisRecord = new ProjectMessageRecipient(rs);
      this.add(thisRecord);
    }
    rs.close();
    pst.close();
  }

  protected void createFilter(StringBuffer sqlFilter) throws SQLException {
    if (sqlFilter == null) {
      sqlFilter = new StringBuffer();
    }
    if (messageId > -1) {
      sqlFilter.append("AND message_id = ? ");
    }
    if (projectId > -1) {
      sqlFilter.append("AND message_id IN (SELECT message_id FROM project_message WHERE project_id = ?) ");
    }
  }


  protected int prepareFilter(PreparedStatement pst) throws SQLException {
    int i = 0;

    if (messageId > -1) {
      pst.setInt(++i, messageId);
    }
    if (projectId > -1) {
      pst.setInt(++i, projectId);
    }

    return i;
  }
}