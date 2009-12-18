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
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.login.utils.UserUtils;
import com.concursive.connect.web.modules.members.dao.TeamMember;

import java.sql.*;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Represents a queue to track email updates for a particular user
 *
 * @author Ananth
 * @created Nov 30, 2009
 */
public class EmailUpdatesQueue {
  //email update queue status
  public final static int STATUS_UNDEFINED = -1;
  public final static int STATUS_UNSCHEDULED = 0;
  public final static int STATUS_SCHEDULED = 1;
  public final static int STATUS_PROCESSING = 2;

  private int id = -1;
  private Timestamp entered = null;
  private Timestamp modified = null;
  private int enteredBy = -1;
  private int modifiedBy = -1;
  private boolean enabled = true;
  //how often to send the email
  private boolean scheduleOften = false;
  private boolean scheduleDaily = false;
  private boolean scheduleWeekly = false;
  private boolean scheduleMonthly = false;
  //which day of the week to send the email on
  private boolean scheduleMonday = false;
  private boolean scheduleTuesday = false;
  private boolean scheduleWednesday = false;
  private boolean scheduleThursday = false;
  private boolean scheduleFriday = false;
  private boolean scheduleSaturday = false;
  private boolean scheduleSunday = false;
  //the calculated next run time
  private Timestamp scheduleTime = null;
  private int status = STATUS_UNSCHEDULED;
  private Timestamp processed = null;

  public EmailUpdatesQueue() {
  }

  public EmailUpdatesQueue(Connection db, int queueId) throws SQLException {
    queryRecord(db, queueId);
  }

  public EmailUpdatesQueue(ResultSet rs) throws SQLException {
    buildRecord(rs);
  }

  public void queryRecord(Connection db, int queueId) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "SELECT q.* " +
            "FROM email_updates_queue q " +
            "WHERE queue_id = ? ");
    pst.setInt(1, queueId);
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      buildRecord(rs);
    }
    rs.close();
    pst.close();
    if (id == -1) {
      throw new SQLException("Queue record not found.");
    }
  }

  public void buildRecord(ResultSet rs) throws SQLException {
    id = rs.getInt("queue_id");
    entered = rs.getTimestamp("entered");
    modified = rs.getTimestamp("modified");
    enteredBy = rs.getInt("enteredby");
    modifiedBy = rs.getInt("modifiedby");
    enabled = rs.getBoolean("enabled");
    scheduleOften = rs.getBoolean("schedule_often");
    scheduleDaily = rs.getBoolean("schedule_daily");
    scheduleWeekly = rs.getBoolean("schedule_weekly");
    scheduleMonthly = rs.getBoolean("schedule_monthly");
    scheduleMonday = rs.getBoolean("schedule_monday");
    scheduleTuesday = rs.getBoolean("schedule_tuesday");
    scheduleWednesday = rs.getBoolean("schedule_wednesday");
    scheduleThursday = rs.getBoolean("schedule_thursday");
    scheduleFriday = rs.getBoolean("schedule_friday");
    scheduleSaturday = rs.getBoolean("schedule_saturday");
    scheduleSunday = rs.getBoolean("schedule_sunday");
    scheduleTime = rs.getTimestamp("schedule_time");
    status = rs.getInt("status");
    processed = rs.getTimestamp("processed");
  }

  public void insert(Connection db) throws SQLException {
    boolean commit = false;
    try {
      commit = db.getAutoCommit();
      if (commit) {
        db.setAutoCommit(false);
      }

      //Determine the day-of-the-week preference to use
      if (scheduleWeekly) {
        determineDayOfTheWeek(db);
      }

      id = DatabaseUtils.getNextSeq(db, "email_updates_queue_queue_id_seq", id);
      PreparedStatement pst = db.prepareStatement(
              "INSERT INTO email_updates_queue " +
                      "(" + (id > -1 ? "queue_id, " : "") + "enteredby, modifiedby, enabled, " +
                      "schedule_often, schedule_daily, schedule_weekly, schedule_monthly, " +
                      "schedule_monday, schedule_tuesday, schedule_wednesday, schedule_thursday, schedule_friday, schedule_saturday, schedule_sunday, " +
                      "schedule_time, status, processed) " +
                      "VALUES (" + (id > -1 ? "?, " : "") + "?, ?, ?, " +
                      "?, ?, ?, ?, " +
                      "?, ?, ?, ?, ?, ?, ?, " +
                      "?, ?, ?) ");
      int i = 0;
      if (id > -1) {
        pst.setInt(++i, id);
      }
      pst.setInt(++i, enteredBy);
      pst.setInt(++i, modifiedBy);
      pst.setBoolean(++i, enabled);
      pst.setBoolean(++i, scheduleOften);
      pst.setBoolean(++i, scheduleDaily);
      pst.setBoolean(++i, scheduleWeekly);
      pst.setBoolean(++i, scheduleMonthly);
      pst.setBoolean(++i, scheduleMonday);
      pst.setBoolean(++i, scheduleTuesday);
      pst.setBoolean(++i, scheduleWednesday);
      pst.setBoolean(++i, scheduleThursday);
      pst.setBoolean(++i, scheduleFriday);
      pst.setBoolean(++i, scheduleSaturday);
      pst.setBoolean(++i, scheduleSunday);
      pst.setTimestamp(++i, scheduleTime);
      pst.setInt(++i, status);
      pst.setTimestamp(++i, processed);
      pst.execute();
      pst.close();
      id = DatabaseUtils.getCurrVal(db, "email_updates_queue_queue_id_seq", id);

      //Determine the next schedule date for this queue
      calculateNextRunDate(db);

      if (commit) {
        db.commit();
      }
    } catch (SQLException e) {
      if (commit) {
        db.rollback();
      }
      throw new SQLException(e.getMessage());
    } finally {
      db.setAutoCommit(true);
    }
  }

  private void determineDayOfTheWeek(Connection db) throws SQLException {
    //The UI currently does not allow user to specify the day-of-the-week for weekly schedule.
    //Determine the day of the week based on today's date for now..
    User user = UserUtils.loadUser(enteredBy);
    Calendar today = Calendar.getInstance();
    if (user.getTimeZone() != null) {
      today = Calendar.getInstance(TimeZone.getTimeZone(user.getTimeZone()));
    }
    if (today.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
      scheduleSunday = true;
    } else if (today.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
      scheduleMonday = true;
    } else if (today.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY) {
      scheduleTuesday = true;
    } else if (today.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY) {
      scheduleWednesday = true;
    } else if (today.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY) {
      scheduleThursday = true;
    } else if (today.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) {
      scheduleFriday = true;
    } else if (today.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
      scheduleSaturday = true;
    }
  }

  public boolean calculateNextRunDate(Connection db) throws SQLException {
    User user = UserUtils.loadUser(enteredBy);
    Calendar next = Calendar.getInstance();
    if (user.getTimeZone() != null) {
      next = Calendar.getInstance(TimeZone.getTimeZone(user.getTimeZone()));
    }
    if (scheduleTime != null) {
      next.setTime(scheduleTime);
    }
    if (scheduleOften) {
      if (next.get(Calendar.HOUR_OF_DAY) < 8) {
        next.set(Calendar.HOUR_OF_DAY, 8);
        next.set(Calendar.MINUTE, 0);
      } else if (next.get(Calendar.HOUR_OF_DAY) < 12) {
        next.set(Calendar.HOUR_OF_DAY, 12);
        next.set(Calendar.MINUTE, 0);
      } else if (next.get(Calendar.HOUR_OF_DAY) < 16) {
        next.set(Calendar.HOUR_OF_DAY, 16);
        next.set(Calendar.MINUTE, 0);
      } else {
        //next day
        next.add(Calendar.DATE, 1);
        next.set(Calendar.HOUR_OF_DAY, 8);
        next.set(Calendar.MINUTE, 0);
      }
    } else if (scheduleDaily) {
      next.add(Calendar.DATE, 1);
    } else if (scheduleWeekly) {
      next.add(Calendar.DATE, 7);
    } else if (scheduleMonthly) {
      next.add(Calendar.MONTH, 1);
    }

    // Set the next time this should run...
    PreparedStatement pst = db.prepareStatement(
        "UPDATE email_updates_queue " +
        "SET status = ?, schedule_time = ? " +
        "WHERE queue_id = ? ");
    int i = 0;
    pst.setInt(++i, STATUS_SCHEDULED);
    pst.setTimestamp(++i, new Timestamp(next.getTimeInMillis()));
    pst.setInt(++i, id);
    pst.executeUpdate();
    pst.close();

    return true;
  }

  public boolean updateProcessedDate(Connection db, Timestamp processed) throws SQLException {
    // Set the next time this should run...
    PreparedStatement pst = db.prepareStatement(
        "UPDATE email_updates_queue " +
        "SET processed = ? " +
        "WHERE queue_id = ? ");
    int i = 0;
    pst.setTimestamp(++i, processed);
    pst.setInt(++i, id);
    pst.executeUpdate();
    pst.close();

    return true;
  }

  public void setType(int type) {
    if (type == TeamMember.EMAIL_OFTEN) {
      scheduleOften = true;
    } else if (type == TeamMember.EMAIL_DAILY) {
      scheduleDaily = true;
    } else if (type == TeamMember.EMAIL_WEEKLY) {
      scheduleWeekly = true;
    } else if (type == TeamMember.EMAIL_MONTHLY) {
      scheduleMonthly = true;
    }
    if (type == TeamMember.EMAIL_OFTEN || type == TeamMember.EMAIL_DAILY) {
      //Need to send on every day of the week
      scheduleMonday = true;
      scheduleTuesday = true;
      scheduleWednesday = true;
      scheduleThursday = true;
      scheduleFriday = true;
      scheduleSaturday = true;
      scheduleSunday = true;
    }
    if (type == TeamMember.EMAIL_WEEKLY) {
      //Need to send on today's day of week
      Calendar now = Calendar.getInstance();
      if (now.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) scheduleMonday = true;
      if (now.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY) scheduleTuesday = true;
      if (now.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY) scheduleWednesday = true;
      if (now.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY) scheduleThursday = true;
      if (now.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) scheduleFriday = true;
      if (now.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) scheduleSaturday = true;
      if (now.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) scheduleSunday = true;
    }
    if (type == TeamMember.EMAIL_MONTHLY) {
      //TODO: What day of the week?
    }
  }

  /**
   * Returns whether the specified queue was just locked, false if it already
   * locked
   *
   * @param queue
   * @param db
   * @return
   * @throws SQLException
   */
  public static boolean lockQueue(EmailUpdatesQueue queue, Connection db) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "UPDATE email_updates_queue " +
            "SET status = ? " +
            "WHERE queue_id = ? " +
            "AND status = ? ");
    pst.setInt(1, EmailUpdatesQueue.STATUS_PROCESSING);
    pst.setInt(2, queue.getId());
    pst.setInt(3, EmailUpdatesQueue.STATUS_SCHEDULED);
    int count = pst.executeUpdate();
    pst.close();
    return (count == 1);
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public Timestamp getEntered() {
    return entered;
  }

  public void setEntered(Timestamp entered) {
    this.entered = entered;
  }

  public Timestamp getModified() {
    return modified;
  }

  public void setModified(Timestamp modified) {
    this.modified = modified;
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

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public boolean getScheduleOften() {
    return scheduleOften;
  }

  public void setScheduleOften(boolean scheduleOften) {
    this.scheduleOften = scheduleOften;
  }

  public boolean getScheduleDaily() {
    return scheduleDaily;
  }

  public void setScheduleDaily(boolean scheduleDaily) {
    this.scheduleDaily = scheduleDaily;
  }

  public boolean getScheduleWeekly() {
    return scheduleWeekly;
  }

  public void setScheduleWeekly(boolean scheduleWeekly) {
    this.scheduleWeekly = scheduleWeekly;
  }

  public boolean getScheduleMonthly() {
    return scheduleMonthly;
  }

  public void setScheduleMonthly(boolean scheduleMonthly) {
    this.scheduleMonthly = scheduleMonthly;
  }

  public boolean getScheduleMonday() {
    return scheduleMonday;
  }

  public void setScheduleMonday(boolean scheduleMonday) {
    this.scheduleMonday = scheduleMonday;
  }

  public boolean getScheduleTuesday() {
    return scheduleTuesday;
  }

  public void setScheduleTuesday(boolean scheduleTuesday) {
    this.scheduleTuesday = scheduleTuesday;
  }

  public boolean getScheduleWednesday() {
    return scheduleWednesday;
  }

  public void setScheduleWednesday(boolean scheduleWednesday) {
    this.scheduleWednesday = scheduleWednesday;
  }

  public boolean getScheduleThursday() {
    return scheduleThursday;
  }

  public void setScheduleThursday(boolean scheduleThursday) {
    this.scheduleThursday = scheduleThursday;
  }

  public boolean getScheduleFriday() {
    return scheduleFriday;
  }

  public void setScheduleFriday(boolean scheduleFriday) {
    this.scheduleFriday = scheduleFriday;
  }

  public boolean getScheduleSaturday() {
    return scheduleSaturday;
  }

  public void setScheduleSaturday(boolean scheduleSaturday) {
    this.scheduleSaturday = scheduleSaturday;
  }

  public boolean getScheduleSunday() {
    return scheduleSunday;
  }

  public void setScheduleSunday(boolean scheduleSunday) {
    this.scheduleSunday = scheduleSunday;
  }

  public Timestamp getScheduleTime() {
    return scheduleTime;
  }

  public void setScheduleTime(Timestamp scheduleTime) {
    this.scheduleTime = scheduleTime;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public Timestamp getProcessed() {
    return processed;
  }

  public void setProcessed(Timestamp processed) {
    this.processed = processed;
  }
}
