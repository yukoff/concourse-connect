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

import com.concursive.commons.db.DatabaseUtils;
import com.concursive.connect.Constants;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.utils.PagedListInfo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/**
 * Contains a collection of MeetingAttendees
 *
 * @author lorraine bittner
 * @version $Id$
 * @created July 3, 2008
 */
public class MeetingAttendeeList extends ArrayList<MeetingAttendee> {

  private int userId = -1;
  private PagedListInfo pagedListInfo = null;
  private int meetingId = -1;
  private int isTentative = Constants.UNDEFINED;
  private boolean isDimdimAttendee = false;

  private List<MeetingAttendee> invitedList = null;
  private List<MeetingAttendee> declinedList = null;
  private List<MeetingAttendee> acceptedList = null;
  private List<MeetingAttendee> tentativeList = null;

  public MeetingAttendeeList() {
  }

  public int getMeetingId() {
    return meetingId;
  }

  public void setMeetingId(int meetingId) {
    this.meetingId = meetingId;
  }

  public void setMeetingId(String tmp) {
    this.meetingId = Integer.parseInt(tmp);
  }

  public int getUserId() {
    return userId;
  }

  public void setUserId(int userId) {
    this.userId = userId;
  }

  public void setUserId(String tmp) {
    this.userId = Integer.parseInt(tmp);
  }

  public int getIsTentative() {
    return isTentative;
  }

  public void setIsTentaive(int tmp) {
    this.isTentative = tmp;
  }

  public void setIsTentaive(String tmp) {
    this.isTentative = DatabaseUtils.parseBooleanToConstant(tmp);
  }

  /*
   * if true returns all dimdim meeting attendees 
   */
  public boolean isDimdimAttendees() {
    return isDimdimAttendee;
  }

  /*
  * Set to true to retrive Dimdim meeting attendee
  */
  public void setDimdimAttendees(boolean isDimdimAttendee) {
    this.isDimdimAttendee = isDimdimAttendee;
  }

  /*
   * Set to true to retrive Dimdim meeting attendee
   */
  public void setDimdimAttendees(String tmp) {
    this.isDimdimAttendee = DatabaseUtils.parseBoolean(tmp);
  }

  public List<MeetingAttendee> getInvitedList() {
    return invitedList;
  }

  public void setInvitedList(List<MeetingAttendee> invitedList) {
    this.invitedList = invitedList;
  }

  public List<MeetingAttendee> getDeclinedList() {
    return declinedList;
  }

  public void setDeclinedList(List<MeetingAttendee> declinedList) {
    this.declinedList = declinedList;
  }

  public List<MeetingAttendee> getAcceptedList() {
    return acceptedList;
  }

  public void setAcceptedList(List<MeetingAttendee> acceptedList) {
    this.acceptedList = acceptedList;
  }

  public List<MeetingAttendee> getTentativeList() {
    return tentativeList;
  }

  public void setTentativeList(List<MeetingAttendee> tentativeList) {
    this.tentativeList = tentativeList;
  }

  public int queryCount(Connection db) throws SQLException {
    int count = 0;
    StringBuffer sqlCount = new StringBuffer();
    StringBuffer sqlFilter = new StringBuffer();
    sqlCount.append(
        "SELECT COUNT(*) AS recordcount " +
            "FROM project_calendar_meeting_attendees ma " +
            "WHERE ma.attendee_id > -1 ");
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
   * @param db Database Connection
   * @throws java.sql.SQLException
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
            "FROM project_calendar_meeting_attendees ma " +
            "WHERE ma.attendee_id > -1 ");
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
    pagedListInfo.setDefaultSort("ma.entered", null);
    pagedListInfo.appendSqlTail(db, sqlOrder);
    //Need to build a base SQL statement for returning records
    if (pagedListInfo != null) {
      pagedListInfo.appendSqlSelectHead(db, sqlSelect);
    } else {
      sqlSelect.append("SELECT ");
    }
    sqlSelect.append(
        "ma.* " +
            "FROM project_calendar_meeting_attendees ma " +
            "WHERE ma.attendee_id > -1 ");
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
      MeetingAttendee thisRecord = new MeetingAttendee(rs);
      this.add(thisRecord);
    }
    rs.close();
    pst.close();
    // Sort the attendees
    invitedList = new ArrayList<MeetingAttendee>();
    declinedList = new ArrayList<MeetingAttendee>();
    acceptedList = new ArrayList<MeetingAttendee>();
    tentativeList = new ArrayList<MeetingAttendee>();
    for (MeetingAttendee meetingAttendee : this) {
      if (meetingAttendee.getDimdimStatus() == MeetingAttendee.STATUS_DIMDIM_INVITED) {
        getInvitedList().add(meetingAttendee);
      }
      if (meetingAttendee.getDimdimStatus() == MeetingAttendee.STATUS_DIMDIM_ACCEPTED) {
        getAcceptedList().add(meetingAttendee);
      }
      if (meetingAttendee.getDimdimStatus() == MeetingAttendee.STATUS_DIMDIM_TENTATIVE) {
        getTentativeList().add(meetingAttendee);
      }
      if (meetingAttendee.getDimdimStatus() == MeetingAttendee.STATUS_DIMDIM_DECLINED) {
        getDeclinedList().add(meetingAttendee);
      }
    }
  }


  protected void createFilter(StringBuffer sqlFilter) {
    if (meetingId > -1) {
      sqlFilter.append("AND meeting_id = ? ");
    }
    if (userId > -1) {
      sqlFilter.append(" AND user_id = ? ");
    }
    if (isTentative != Constants.UNDEFINED) {
      sqlFilter.append("AND is_tentative = ? ");
    }
    if (isDimdimAttendee) {
      sqlFilter.append("AND dimdim_status IS NOT NULL ");
    }
  }


  protected int prepareFilter(PreparedStatement pst) throws SQLException {
    int i = 0;
    if (meetingId > -1) {
      pst.setInt(++i, meetingId);
    }
    if (userId > -1) {
      pst.setInt(++i, userId);
    }
    if (isTentative != Constants.UNDEFINED) {
      pst.setBoolean(++i, (isTentative == Constants.TRUE));
    }
    return i;
  }

  /**
   * Deletes all the attendees of a meeting
   *
   * @param db        - Database connection
   * @param meetingId - Id of the meeting
   * @throws SQLException
   */
  public void delete(Connection db, int meetingId) throws SQLException {
    boolean autoCommit = db.getAutoCommit();
    try {
      if (autoCommit) {
        db.setAutoCommit(false);
      }
      // Delete the Meeting
      PreparedStatement pst = db.prepareStatement(
          "DELETE FROM project_calendar_meeting_attendees " +
              "WHERE meeting_id = ? ");
      int i = 0;
      pst.setInt(++i, meetingId);
      pst.execute();
      pst.close();
      if (autoCommit) {
        db.commit();
      }
    } catch (SQLException e) {
      if (autoCommit) {
        db.rollback();
      }
      throw e;
    } finally {
      if (autoCommit) {
        db.setAutoCommit(true);
      }
    }
  }

  public MeetingAttendee getMeetingAttendee(User user) {
    for (MeetingAttendee meetingAttendee : this) {
      if (meetingAttendee.getUserId() == user.getId()) {
        return meetingAttendee;
      }
    }
    return null;
  }
}