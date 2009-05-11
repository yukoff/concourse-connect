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
import com.concursive.commons.web.mvc.beans.GenericBean;

import java.sql.*;

/**
 * Represents a MeetingAttendee
 *
 * @author lorraine bittner
 * @version $Id$
 * @created July 3, 2008
 */
public class MeetingAttendee extends GenericBean {
  private int id = -1;
  private int meetingId = -1;
  private int userId = -1;
  private boolean isTentative = false;
  private Timestamp entered = null;

  public MeetingAttendee() {
  }

  public MeetingAttendee(ResultSet rs) throws SQLException {
    buildRecord(rs);
  }

  public MeetingAttendee(Connection db, int id) throws SQLException {
    queryRecord(db, id);
  }

  public void queryRecord(Connection db, int attendeeId) throws SQLException {
    StringBuffer sql = new StringBuffer();
    sql.append(
        "SELECT ma.* " +
            "FROM project_calendar_meeting_attendees ma " +
            "WHERE attendee_id = ? ");
    PreparedStatement pst = db.prepareStatement(sql.toString());
    int i = 0;
    pst.setInt(++i, attendeeId);
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      buildRecord(rs);
    }
    rs.close();
    pst.close();
    if (id == -1) {
      throw new SQLException("Attendee record not found.");
    }
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public void setId(String tmp) {
    this.id = Integer.parseInt(tmp);
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

  public boolean getIsTentative() {
    return isTentative;
  }

  public void setIsTentative(boolean tentative) {
    isTentative = tentative;
  }

  public void setIsTentative(String tmp) {
    isTentative = DatabaseUtils.parseBoolean(tmp);
  }

  public Timestamp getEntered() {
    return entered;
  }

  public void setEntered(Timestamp entered) {
    this.entered = entered;
  }

  public void setEntered(String tmp) {
    this.entered = DatabaseUtils.parseTimestamp(tmp);
  }

  private void buildRecord(ResultSet rs) throws SQLException {
    id = rs.getInt("attendee_id");
    meetingId = rs.getInt("meeting_id");
    userId = rs.getInt("user_id");
    isTentative = rs.getBoolean("is_tentative");
    entered = rs.getTimestamp("entered");
  }

  public boolean isValid() {
    if (meetingId == -1) {
      errors.put("actionError", "Meeting Id not specified");
    }
    if (userId == -1) {
      errors.put("actionError", "User Id not specified");
    }

    if (hasErrors()) {
      return false;
    } else {
      return true;
    }
  }

  public boolean insert(Connection db) throws SQLException {
    if (!isValid()) {
      return false;
    }
    boolean commit = db.getAutoCommit();
    try {
      if (commit) {
        db.setAutoCommit(false);
      }
      StringBuffer sql = new StringBuffer();
      sql.append(
          "INSERT INTO project_calendar_meeting_attendees " +
              "(meeting_id, user_id, is_tentative ");

      if (entered != null) {
        sql.append(", entered ");
      }
      sql.append(") VALUES (?, ?, ? ");

      if (entered != null) {
        sql.append(", ? ");
      }
      sql.append(") ");
      int i = 0;
      //Insert the Meeting
      PreparedStatement pst = db.prepareStatement(sql.toString());
      pst.setInt(++i, meetingId);
      pst.setInt(++i, userId);
      pst.setBoolean(++i, isTentative);
      if (entered != null) {
        pst.setTimestamp(++i, entered);
      }

      pst.execute();
      pst.close();
      id = DatabaseUtils.getCurrVal(db, "project_calendar_meeting_attendees_attendee_id_seq", -1);
      if (commit) {
        db.commit();
      }
    } catch (SQLException e) {
      if (commit) {
        db.rollback();
      }
      throw e;
    } finally {
      if (commit) {
        db.setAutoCommit(true);
      }
    }
    return true;
  }

  public void delete(Connection db) throws SQLException {
    boolean autoCommit = db.getAutoCommit();
    try {
      if (autoCommit) {
        db.setAutoCommit(false);
      }
      // Delete the Meeting
      PreparedStatement pst = db.prepareStatement(
          "DELETE FROM project_calendar_meeting_attendees " +
              "WHERE attendee_id = ? ");
      int i = 0;
      pst.setInt(++i, id);
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
}
