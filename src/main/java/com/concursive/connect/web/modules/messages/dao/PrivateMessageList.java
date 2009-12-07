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

package com.concursive.connect.web.modules.messages.dao;

import com.concursive.commons.db.DatabaseUtils;
import com.concursive.commons.text.StringUtils;
import com.concursive.connect.Constants;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.login.utils.UserUtils;
import com.concursive.connect.web.modules.members.dao.TeamMember;
import com.concursive.connect.web.modules.members.dao.TeamMemberList;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import com.concursive.connect.web.utils.PagedListInfo;

import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Represents a private message to a profile
 *
 * @author Kailash Bhoopalam
 * @version $Id$
 * @created December 21, 2008
 */
public class PrivateMessageList extends ArrayList<PrivateMessage> {

  // main filters (default retrieves all records)
  private PagedListInfo pagedListInfo = null;

  // The user's rating and review

  private int id = -1;
  private int projectId = -1;
  private int parentId = -1;
  private int enteredBy = -1;
  private int readBy = -1;
  private int readMessages = Constants.UNDEFINED;
  private int deletedByEnteredBy = Constants.UNDEFINED;
  private int deletedByUserId = Constants.UNDEFINED;
  private int sentFromProjectId = -1;
  private String projectIdsString = null;
  private String[] projectIdString = null;
  private int linkProjectId = -1;
  private Timestamp enteredRangeStart = null;
  private Timestamp enteredRangeEnd = null;
  private int linkModuleId = -1;

  /**
   * Constructor for the ProjectRating
   */
  public PrivateMessageList() {
  }


  public void setPagedListInfo(PagedListInfo tmp) {
    this.pagedListInfo = tmp;
  }


  public PagedListInfo getPagedListInfo() {
    return pagedListInfo;
  }


  public void setId(int messageId) {
    this.id = messageId;

  }

  public void setId(String messageId) {
    this.id = Integer.parseInt(messageId);

  }

  public int getId() {
    return id;

  }

  /**
   * Sets the enteredBy attribute of the Project object
   *
   * @param tmp The new enteredBy value
   */
  public void setEnteredBy(int tmp) {
    this.enteredBy = tmp;
  }


  /**
   * Sets the enteredBy attribute of the Project object
   *
   * @param tmp The new enteredBy value
   */
  public void setEnteredBy(String tmp) {
    this.enteredBy = Integer.parseInt(tmp);
  }


  /**
   * @param projectId the projectId to set
   */
  public void setProjectId(int projectId) {
    this.projectId = projectId;
  }


  /**
   * @param projectId the projectId to set
   */
  public void setProjectId(String projectId) {
    this.projectId = Integer.parseInt(projectId);
  }


  /**
   * Gets the enteredBy attribute of the Project object
   *
   * @return The enteredBy value
   */
  public int getEnteredBy() {
    return enteredBy;
  }


  /**
   * @return the projectId
   */
  public int getProjectId() {
    return projectId;
  }


  /**
   * @return the parentId
   */
  public int getParentId() {
    return parentId;
  }


  /**
   * @param parentId the parentId to set
   */
  public void setParentId(int parentId) {
    this.parentId = parentId;
  }


  public void setParentId(String parentId) {
    this.parentId = Integer.parseInt(parentId);
  }


  /**
   * @return the readBy
   */
  public int getReadBy() {
    return readBy;
  }


  /**
   * @param readBy the readBy to set
   */
  public void setReadBy(int readBy) {
    this.readBy = readBy;
  }


  public void setReadBy(String readBy) {
    this.readBy = Integer.parseInt(readBy);
  }

  /**
   * @return the readMessages
   */
  public int getReadMessages() {
    return readMessages;
  }


  /**
   * @param readMessages the readMessages to set
   */
  public void setReadMessages(int readMessages) {
    this.readMessages = readMessages;
  }


  public void setReadMessages(String readMessages) {
    this.readMessages = Integer.parseInt(readMessages);
  }

  /**
   * @return the deletedByEnteredBy
   */
  public int getDeletedByEnteredBy() {
    return deletedByEnteredBy;
  }


  /**
   * @param deletedByEnteredBy the deletedByEnteredBy to set
   */
  public void setDeletedByEnteredBy(int deletedByEnteredBy) {
    this.deletedByEnteredBy = deletedByEnteredBy;
  }


  public void setDeletedByEnteredBy(String deletedByEnteredBy) {
    this.deletedByEnteredBy = Integer.parseInt(deletedByEnteredBy);
  }


  /**
   * @return the deletedByUserId
   */
  public int getDeletedByUserId() {
    return deletedByUserId;
  }


  /**
   * @param deletedByUserId the deletedByUserId to set
   */
  public void setDeletedByUserId(int deletedByUserId) {
    this.deletedByUserId = deletedByUserId;
  }

  public void setDeletedByUserId(String deletedByUserId) {
    this.deletedByUserId = Integer.parseInt(deletedByUserId);
  }

  /**
   * @return the sentFromProjectId
   */
  public int getSentFromProjectId() {
    return sentFromProjectId;
  }

  /**
   * @return the projectIdsString
   */
  public String getProjectIdsString() {
    return projectIdsString;
  }


  /**
   * @param projectIdsString the projectIdsString to set
   */
  public void setProjectIdsString(String projectIdsString) {
    this.projectIdsString = projectIdsString;
    if (StringUtils.hasText(this.projectIdsString)) {
      projectIdString = this.projectIdsString.split(",");
    }
  }


  /**
   * @param sentFromProjectId the sentFromProjectId to set
   */
  public void setSentFromProjectId(int sentFromProjectId) {
    this.sentFromProjectId = sentFromProjectId;
  }

  public void setSentFromProjectId(String sentFromProjectId) {
    this.sentFromProjectId = Integer.parseInt(sentFromProjectId);
  }

  /**
   * @return the linkProjectId
   */
  public int getLinkProjectId() {
    return linkProjectId;
  }


  /**
   * @param linkProjectId the linkProjectId to set
   */
  public void setLinkProjectId(int linkProjectId) {
    this.linkProjectId = linkProjectId;
  }

  public void setLinkProjectId(String linkProjectId) {
    this.linkProjectId = Integer.parseInt(linkProjectId);
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
            "FROM project_private_message " +
            "WHERE message_id > -1 ");
    createFilter(sqlFilter);
    if (pagedListInfo == null) {
      pagedListInfo = new PagedListInfo();
      pagedListInfo.setItemsPerPage(0);
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
          "AND lower(body) < ? ");
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
    pagedListInfo.setDefaultSort("entered DESC", null);
    pagedListInfo.appendSqlTail(db, sqlOrder);

    //Need to build a base SQL statement for returning records
    pagedListInfo.appendSqlSelectHead(db, sqlSelect);
    sqlSelect.append(
        "* " +
            "FROM project_private_message " +
            "WHERE message_id > -1 ");
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
      PrivateMessage thisPrivateMessage = new PrivateMessage(rs);
      this.add(thisPrivateMessage);
    }
    rs.close();
    pst.close();
  }

  private void createFilter(StringBuffer sqlFilter) {
    if (sqlFilter == null) {
      sqlFilter = new StringBuffer();
    }
    if (id > -1) {
      sqlFilter.append("AND message_id = ? ");
    }
    if (projectId > -1) {
      sqlFilter.append("AND project_id = ? ");
    }
    if (parentId > -1) {
      sqlFilter.append("AND parent_id = ? ");
    }
    if (enteredBy > 0) {
      sqlFilter.append(" AND enteredby = ? ");
    }
    if (readBy > 0) {
      sqlFilter.append(" AND read_by = ? ");
    }
    if (readMessages != Constants.UNDEFINED) {
      if (readMessages == Constants.TRUE) {
        sqlFilter.append(" AND read_by IS NOT NULL ");
      } else if (readMessages == Constants.FALSE) {
        sqlFilter.append(" AND read_by IS NULL ");
      }
    }
    if (deletedByEnteredBy != Constants.UNDEFINED) {
      sqlFilter.append(" AND deleted_by_entered_by = ? ");
    }
    if (deletedByUserId > Constants.UNDEFINED) {
      sqlFilter.append(" AND deleted_by_user_id = ? ");
    }
    if (sentFromProjectId != -1) {
      sqlFilter.append("AND parent_id IN (SELECT message_id FROM project_private_message WHERE project_id = ?) ");
    }

    if (projectIdString != null && projectIdString.length > 0) {
      sqlFilter.append("AND project_id IN (");
      int count = 0;
      boolean isNumber = false;
      while (count < projectIdString.length) {
        if (StringUtils.isNumber(projectIdString[count])) {
          sqlFilter.append("?");
          isNumber = true;
        }
        count++;
        if (count < projectIdString.length && isNumber) {
          sqlFilter.append(",");
        }
        isNumber = false;
      }
      sqlFilter.append(") ");
    }
    if (linkProjectId != -1) {
      sqlFilter.append(" AND link_project_id = ? ");
    }
    if (enteredRangeStart != null) {
      sqlFilter.append("AND entered >= ? ");
    }
    if (enteredRangeEnd != null) {
      sqlFilter.append("AND entered < ? ");
    }
    if (linkModuleId != -1) {
      sqlFilter.append("AND link_module_id = ? ");
    }
  }

  private int prepareFilter(PreparedStatement pst) throws SQLException {
    int i = 0;
    if (id > -1) {
      pst.setInt(++i, id);
    }
    if (projectId > -1) {
      pst.setInt(++i, projectId);
    }
    if (parentId > -1) {
      pst.setInt(++i, parentId);
    }
    if (enteredBy > 0) {
      pst.setInt(++i, enteredBy);
    }
    if (readBy > 0) {
      pst.setInt(++i, readBy);
    }
    if (deletedByEnteredBy != Constants.UNDEFINED) {
      pst.setBoolean(++i, (deletedByEnteredBy == Constants.TRUE));
    }
    if (deletedByUserId > Constants.UNDEFINED) {
      pst.setBoolean(++i, (deletedByUserId == Constants.TRUE));
    }
    if (sentFromProjectId != -1) {
      pst.setInt(++i, sentFromProjectId);
    }
    if (projectIdString != null && projectIdString.length > 0) {
      int count = 0;
      while (count < projectIdString.length) {
        if (StringUtils.isNumber(projectIdString[count])) {
          pst.setInt(++i, Integer.parseInt(projectIdString[count]));
        }
        count++;
      }
    }
    if (linkProjectId != -1) {
      pst.setInt(++i, linkProjectId);
    }
    if (enteredRangeStart != null) {
      pst.setTimestamp(++i, enteredRangeStart);
    }
    if (enteredRangeEnd != null) {
      pst.setTimestamp(++i, enteredRangeEnd);
    }
    if (linkModuleId != -1) {
      pst.setInt(++i, linkModuleId);
    }
    return i;
  }


  public void delete(Connection db) throws SQLException {
    //TODO:kailash need to accommodate for parent id dependency (or) remove the database dependency
    Iterator<PrivateMessage> privateMessageItr = this.iterator();
    while (privateMessageItr.hasNext()) {
      PrivateMessage privateMessage = privateMessageItr.next();
      privateMessage.delete(db);
    }
  }


  /**
   * @param db
   * @param userId
   * @return the number of messages in the user's profile project
   */
  public static int queryUnreadCountForUser(Connection db, int userId) throws SQLException {

    // Get the projects in which the user is has permission project 'project-private-messages-view'
    // load the user
    User thisUser = UserUtils.loadUser(userId);

    // Build the private messages of the user's profile project
    PrivateMessageList privateMessageList = new PrivateMessageList();
    privateMessageList.setProjectId(thisUser.getProfileProjectId());
    privateMessageList.setReadMessages(Constants.FALSE);
    privateMessageList.setDeletedByUserId(Constants.FALSE);
    privateMessageList.buildList(db);

    return privateMessageList.size();
  }


  /**
   * @param db
   * @param projectId
   * @return the number of unread messages in a project
   */
  public static int queryUnreadCountForProject(Connection db, int projectId) throws SQLException {
    // Build the private messages of the user's profile project
    PrivateMessageList privateMessageList = new PrivateMessageList();
    privateMessageList.setProjectId(projectId);
    privateMessageList.setReadMessages(Constants.FALSE);
    privateMessageList.setDeletedByUserId(Constants.FALSE);
    privateMessageList.buildList(db);
    return privateMessageList.size();
  }


  /**
   * @param db
   * @param userId
   * @return the sum total of messages in all the projects that the user has access to view messages
   */
  public static int queryRolledupUnreadCountForUser(Connection db, int userId) throws SQLException {
    // Get the projects in which the user is has permission project 'project-private-messages-view'
    // load the user
    User thisUser = UserUtils.loadUser(userId);

    // Get the list of projects to which the user has accepted invitations
    TeamMemberList teamMemberList = new TeamMemberList();
    teamMemberList.setUserId(userId);
    teamMemberList.setStatus(TeamMember.STATUS_ADDED);
    teamMemberList.buildList(db);
    StringBuffer projectIdStringBuffer = new StringBuffer();

    // Build a string of projectids for of only those projects to which the user has the permission to view messages
    for (TeamMember teamMember : teamMemberList) {
      int projectId = teamMember.getProjectId();
      if (ProjectUtils.hasPermissionAsTeamMember(projectId, thisUser, "project-private-messages-view")) {
        projectIdStringBuffer.append(projectId);
        projectIdStringBuffer.append(",");
      }
    }

    // Build the private messages of all the projects to which the user has access to view messages
    PrivateMessageList privateMessageList = new PrivateMessageList();
    privateMessageList.setProjectIdsString(projectIdStringBuffer.toString());
    privateMessageList.setReadMessages(Constants.FALSE);
    privateMessageList.setDeletedByUserId(Constants.FALSE);
    privateMessageList.buildList(db);

    return privateMessageList.size();
  }

}
