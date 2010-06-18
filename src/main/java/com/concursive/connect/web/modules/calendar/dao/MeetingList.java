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

package com.concursive.connect.web.modules.calendar.dao;

import com.concursive.commons.date.DateUtils;
import com.concursive.commons.db.DatabaseUtils;
import com.concursive.connect.Constants;
import com.concursive.connect.web.utils.PagedListInfo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

/**
 * Contains a collection of Meetings
 *
 * @author matt rajkowski
 * @version $Id: WikiList.java,v 1.11.6.1 2004/08/26 15:54:32 matt Exp
 *          $
 * @created February 7, 2006
 */
public class MeetingList extends ArrayList<Meeting> {

  private int instanceId = -1;
  private int projectId = -1;
  private PagedListInfo pagedListInfo = null;
  private int enteredBy = -1;
  private int modifiedBy = -1;
  private int forUser = -1;
  private int byInvitationOnly = Constants.UNDEFINED;
  private boolean publicOpenProjectsOnly = false;
  private int forParticipant = Constants.UNDEFINED;
  private int projectCategoryId = -1;
  private List<Integer> projectCategoryIdList = null; // set if multiple category ids are needed for filter
  private boolean isDimdim = false;
  private boolean isWebcast = false;

  public boolean isWebcast() {
    return isWebcast;
  }

  public void setIsWebcast(boolean isWebcast) {
    this.isWebcast = isWebcast;
  }

  public void setIsWebcast(String tmp) {
    this.isWebcast = DatabaseUtils.parseBoolean(tmp);
  }

  private boolean buildAttendees = false;

  // api - based on the start date of the event (start date only)
  protected java.sql.Timestamp alertRangeStart = null;
  protected java.sql.Timestamp alertRangeEnd = null;
  // calendar - takes into account duration of an event (start and end date)
  protected java.sql.Timestamp eventSpanStart = null;
  protected java.sql.Timestamp eventSpanEnd = null;


  public MeetingList() {
  }

  public int getInstanceId() {
    return instanceId;
  }

  public void setInstanceId(int instanceId) {
    this.instanceId = instanceId;
  }

  public void setInstanceId(String tmp) {
    this.instanceId = Integer.parseInt(tmp);
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

  public void setEventSpanStart(java.sql.Timestamp tmp) {
    this.eventSpanStart = tmp;
  }

  public void setEventSpanStart(String tmp) {
    this.eventSpanStart = DatabaseUtils.parseTimestamp(tmp);
  }

  public java.sql.Timestamp getEventSpanStart() {
    return eventSpanStart;
  }

  public void setEventSpanEnd(java.sql.Timestamp tmp) {
    this.eventSpanEnd = tmp;
  }

  public void setEventSpanEnd(String tmp) {
    this.eventSpanEnd = DatabaseUtils.parseTimestamp(tmp);
  }

  public java.sql.Timestamp getEventSpanEnd() {
    return eventSpanEnd;
  }

  /**
   * @return the byInvitationOnly value
   */
  public int getByInvitationOnly() {
    return byInvitationOnly;
  }


  /**
   * @param byInvitationOnly the byInvitationOnly value to set
   */
  public void setByInvitationOnly(int byInvitationOnly) {
    this.byInvitationOnly = byInvitationOnly;
  }


  /**
   * @param byInvitationOnly the byInvitationOnly value to set
   */
  public void setByInvitationOnly(String byInvitationOnly) {
    this.byInvitationOnly = DatabaseUtils.parseBooleanToConstant(byInvitationOnly);
  }

  public boolean getPublicOpenProjectsOnly() {
    return publicOpenProjectsOnly;
  }

  public void setPublicOpenProjectsOnly(boolean publicOpenProjectsOnly) {
    this.publicOpenProjectsOnly = publicOpenProjectsOnly;
  }

  public void setPublicOpenProjectsOnly(String publicOpenProjectsOnly) {
    this.publicOpenProjectsOnly = DatabaseUtils.parseBoolean(publicOpenProjectsOnly);
  }

  public int getForParticipant() {
    return forParticipant;
  }

  public void setForParticipant(int forParticipant) {
    this.forParticipant = forParticipant;
  }

  public void setForParticipant(String forParticipant) {
    this.forParticipant = DatabaseUtils.parseBooleanToConstant(forParticipant);
  }

  public int getProjectCategoryId() {
    return projectCategoryId;
  }

  public void setProjectCategoryId(int projectCategoryId) {
    this.projectCategoryId = projectCategoryId;
  }

  public void setProjectCategoryId(String projectCategoryId) {
    this.projectCategoryId = Integer.parseInt(projectCategoryId);

  }

  public void setProjectCategoryIdList(List<Integer> projectCategoryIdList) {
    this.projectCategoryIdList = projectCategoryIdList;
  }

  public List<Integer> setProjectCategoryIdList() {
    return this.projectCategoryIdList;
  }

  /*
   * Set true if dimdim meeting are to be searched
   */
  public void setIsDimdim(boolean isDimdim) {
    this.isDimdim = isDimdim;
  }

  public boolean getIsDimdim() {
    return isDimdim;
  }

  public boolean getBuildAttendees() {
    return buildAttendees;
  }

  public void setBuildAttendees(boolean buildAttendees) {
    this.buildAttendees = buildAttendees;
  }

  public int queryCount(Connection db) throws SQLException {
    int count = 0;
    StringBuffer sqlCount = new StringBuffer();
    StringBuffer sqlFilter = new StringBuffer();
    sqlCount.append(
        "SELECT COUNT(*) AS recordcount " +
            "FROM project_calendar_meeting m " +
            "WHERE m.meeting_id > -1 ");
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

  /**
   * builds MeetingList
   *
   * @param db
   * @throws SQLException
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
            "FROM project_calendar_meeting m " +
            "WHERE m.meeting_id > -1 ");
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
    pagedListInfo.setDefaultSort("m.start_date, m.end_date", null);
    pagedListInfo.appendSqlTail(db, sqlOrder);
    //Need to build a base SQL statement for returning records
    if (pagedListInfo != null) {
      pagedListInfo.appendSqlSelectHead(db, sqlSelect);
    } else {
      sqlSelect.append("SELECT ");
    }
    sqlSelect.append(
        "m.* " +
            "FROM project_calendar_meeting m " +
            "WHERE m.meeting_id > -1 ");
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
      Meeting thisRecord = new Meeting(rs);
      this.add(thisRecord);
    }
    rs.close();
    pst.close();
    // Further queries
    for (Meeting thisMeeting : this) {
      if (buildAttendees) {
        thisMeeting.buildAttendeeList(db);
      }
    }
  }


  protected void createFilter(StringBuffer sqlFilter) {
    if (projectId > 0) {
      sqlFilter.append("AND m.project_id = ? ");
    }
    if (forUser > -1) {
      sqlFilter.append("AND (m.project_id IN (SELECT DISTINCT project_id FROM project_team WHERE user_id = ? " +
          "AND status IS NULL) OR m.project_id IN (SELECT project_id FROM projects WHERE allow_guests = ? AND approvaldate IS NOT NULL)) ");
    }
    if (alertRangeStart != null) {
      sqlFilter.append("AND m.start_date >= ? ");
    }
    if (alertRangeEnd != null) {
      sqlFilter.append("AND m.start_date < ? ");
    }
    if (eventSpanStart != null && eventSpanEnd != null) {
      // Find events within a start and end date
      sqlFilter.append("AND (");
      sqlFilter.append("(m.start_date >= ? AND m.start_date < ?) ");
      sqlFilter.append("OR (m.start_date <= ? AND m.end_date >= ?) ");
      sqlFilter.append(") ");
    } else if (eventSpanStart != null) {
      // Find the next upcoming events given just a start date
      sqlFilter.append("AND (");
      sqlFilter.append("(m.start_date >= ?) ");
      sqlFilter.append("OR (m.start_date <= ? AND m.end_date >= ?) ");
      sqlFilter.append(") ");
    }
    if (byInvitationOnly != Constants.UNDEFINED) {
      sqlFilter.append("AND by_invitation_only = ? ");
    }
    if (instanceId > -1) {
      sqlFilter.append("AND m.project_id IN (SELECT project_id FROM projects WHERE instance_id = ?) ");
    }
    if (publicOpenProjectsOnly) {
      sqlFilter.append("AND m.project_id IN (SELECT project_id FROM projects WHERE allow_guests = ? AND approvaldate IS NOT NULL) ");
    }
    if (forParticipant == Constants.TRUE) {
      sqlFilter.append("AND m.project_id IN (SELECT project_id FROM projects WHERE (allows_user_observers = ? OR allow_guests = ?) AND approvaldate IS NOT NULL) ");
    }
    if (projectCategoryId > 0) {
      sqlFilter.append("AND m.project_id IN (SELECT project_id FROM projects WHERE category_id = ?) ");
    } else if (projectCategoryIdList != null && !projectCategoryIdList.isEmpty()) {
      sqlFilter.append("AND m.project_id IN (SELECT project_id FROM projects WHERE category_id IN (");
      sqlFilter.append(projectCategoryIdList.get(0));
      for (int i = 1; i < projectCategoryIdList.size(); i++) {
        sqlFilter.append("," + projectCategoryIdList.get(i));
      }
      sqlFilter.append(") )");
    }
    if (isDimdim) {
      sqlFilter.append("AND m.is_dimdim = ? ");
    }
    if (isWebcast) {
      sqlFilter.append("AND m.is_webcast = ? ");
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
    if (eventSpanStart != null && eventSpanEnd != null) {
      pst.setTimestamp(++i, eventSpanStart);
      pst.setTimestamp(++i, eventSpanEnd);
      pst.setTimestamp(++i, eventSpanStart);
      pst.setTimestamp(++i, eventSpanStart);
    } else if (eventSpanStart != null) {
      pst.setTimestamp(++i, eventSpanStart);
      pst.setTimestamp(++i, eventSpanStart);
      pst.setTimestamp(++i, eventSpanStart);
    }
    if (byInvitationOnly != Constants.UNDEFINED) {
      pst.setBoolean(++i, (byInvitationOnly == Constants.TRUE));
    }
    if (instanceId > -1) {
      pst.setInt(++i, instanceId);
    }
    if (publicOpenProjectsOnly) {
      pst.setBoolean(++i, true);
    }
    if (forParticipant == Constants.TRUE) {
      pst.setBoolean(++i, true);
      pst.setBoolean(++i, true);
    }
    if (projectCategoryId > 0) {
      pst.setInt(++i, projectCategoryId);
    }
    if (isDimdim) {
      pst.setBoolean(++i, true);
    }
    if (isWebcast) {
      pst.setBoolean(++i, true);
    }
    return i;
  }

  /**
   * Delete Meeting
   *
   * @param db
   * @param projectId
   * @throws SQLException
   */
  public static void delete(Connection db, int projectId) throws SQLException {
    // Delete the Meeting
    PreparedStatement pst = db.prepareStatement(
        "DELETE FROM project_calendar_meeting " +
            "WHERE project_id = ? ");
    pst.setInt(1, projectId);
    pst.execute();
    pst.close();
  }

  /**
   * Insert Meeting
   *
   * @param db
   * @throws SQLException
   */
  public void insert(Connection db) throws SQLException {
    for (Meeting meeting : this) {
      meeting.setProjectId(projectId);
      meeting.setEnteredBy(enteredBy);
      meeting.setModifiedBy(modifiedBy);
      meeting.insert(db);
    }
  }

  /**
   * Gets Meeting count grouped by startdate
   *
   * @param db
   * @param timeZone
   * @return
   * @throws SQLException
   */
  public HashMap<String, Integer> queryRecordCount(Connection db, TimeZone timeZone) throws SQLException {
    HashMap<String, Integer> events = new HashMap<String, Integer>();
    StringBuffer sqlCount = new StringBuffer();
    StringBuffer sqlFilter = new StringBuffer();
    StringBuffer sqlTail = new StringBuffer();
    sqlCount.append(
        "SELECT m.start_date " +
            "FROM project_calendar_meeting m " +
            "WHERE m.meeting_id > -1 " +
            "AND m.start_date IS NOT NULL ");
    createFilter(sqlFilter);
    PreparedStatement pst = db.prepareStatement(sqlCount.toString() + sqlFilter.toString() + sqlTail.toString());
    prepareFilter(pst);
    ResultSet rs = pst.executeQuery();
    while (rs.next()) {
      String alertDate = DateUtils.getServerToUserDateString(timeZone, DateFormat.SHORT, rs.getTimestamp("start_date"));
      int alertCount = 1;
      if (events.containsKey(alertDate)) {
        alertCount = events.get(alertDate) + 1;
      }
      events.put(alertDate, alertCount);
    }
    rs.close();
    pst.close();
    return events;
  }

}
