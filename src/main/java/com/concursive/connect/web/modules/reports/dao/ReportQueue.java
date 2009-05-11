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

package com.concursive.connect.web.modules.reports.dao;

import com.concursive.commons.date.DateUtils;
import com.concursive.commons.db.DatabaseUtils;
import com.concursive.commons.web.mvc.beans.GenericBean;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

/**
 * Represents a report that has been queued
 *
 * @author matt rajkowski
 * @created October 1, 2003
 */
public class ReportQueue extends GenericBean {

  public final static int STATUS_UNDEFINED = -1;
  public final static int STATUS_QUEUED = 0;
  public final static int STATUS_PROCESSING = 1;
  public final static int STATUS_PROCESSED = 2;
  public final static int STATUS_ERROR = 3;
  public final static int STATUS_DISABLED = 4;
  public final static int STATUS_SCHEDULED = 5;

  public final static int REPORT_TYPE_PDF = 1;
  public final static int REPORT_TYPE_CSV = 2;
  public final static int REPORT_TYPE_HTML = 3;
  public final static int REPORT_TYPE_EXCEL = 4;


  private int id = -1;
  private int reportId = -1;
  private Timestamp entered = null;
  private int enteredBy = -1;
  private int status = STATUS_UNDEFINED;
  private Timestamp processed = null;
  private String filename = null;
  private long size = -1;
  private boolean enabled = true;
  private int projectId = -1;

  private boolean sendEmail = false;
  private boolean scheduleMonday = false;
  private boolean scheduleTuesday = false;
  private boolean scheduleWednesday = false;
  private boolean scheduleThursday = false;
  private boolean scheduleFriday = false;
  private boolean scheduleSaturday = false;
  private boolean scheduleSunday = false;
  private int cleanup = 1;
  private Timestamp scheduleTime = null;
  private String output = "pdf";

  //Resources
  private int position = -1;
  private Report report = null;
  private boolean throwNotFoundException = true;


  /**
   * Constructor for the ReportQueue object
   */
  public ReportQueue() {
  }


  /**
   * Constructor for the ReportQueue object
   *
   * @param rs Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public ReportQueue(ResultSet rs) throws SQLException {
    buildRecord(rs);
  }


  /**
   * Constructor for the ReportQueue object
   *
   * @param db      Description of the Parameter
   * @param queueId Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public ReportQueue(Connection db, int queueId) throws SQLException {
    queryRecord(db, queueId);
  }


  /**
   * Constructor for the ReportQueue object
   *
   * @param db             Description of the Parameter
   * @param queueId        Description of the Parameter
   * @param throwException Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public ReportQueue(Connection db, int queueId, boolean throwException) throws SQLException {
    throwNotFoundException = throwException;
    queryRecord(db, queueId);
  }


  /**
   * Loads the specified ReportQueue
   *
   * @param db      Description of the Parameter
   * @param queueId Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public void queryRecord(Connection db, int queueId) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "SELECT q.* " +
            "FROM report_queue q " +
            "WHERE queue_id = ? ");
    pst.setInt(1, queueId);
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      buildRecord(rs);
    }
    rs.close();
    pst.close();
    if ((id == -1) && (throwNotFoundException)) {
      throw new SQLException("Queue record not found.");
    }
  }


  /**
   * Sets the id attribute of the ReportQueue object
   *
   * @param tmp The new id value
   */
  public void setId(int tmp) {
    this.id = tmp;
  }


  /**
   * Sets the id attribute of the ReportQueue object
   *
   * @param tmp The new id value
   */
  public void setId(String tmp) {
    this.id = Integer.parseInt(tmp);
  }


  /**
   * Sets the reportId attribute of the ReportQueue object
   *
   * @param tmp The new reportId value
   */
  public void setReportId(int tmp) {
    this.reportId = tmp;
  }


  /**
   * Sets the reportId attribute of the ReportQueue object
   *
   * @param tmp The new reportId value
   */
  public void setReportId(String tmp) {
    this.reportId = Integer.parseInt(tmp);
  }


  /**
   * Sets the entered attribute of the ReportQueue object
   *
   * @param tmp The new entered value
   */
  public void setEntered(Timestamp tmp) {
    this.entered = tmp;
  }


  /**
   * Sets the entered attribute of the ReportQueue object
   *
   * @param tmp The new entered value
   */
  public void setEntered(String tmp) {
    this.entered = DatabaseUtils.parseTimestamp(tmp);
  }


  /**
   * Sets the enteredBy attribute of the ReportQueue object
   *
   * @param tmp The new enteredBy value
   */
  public void setEnteredBy(int tmp) {
    this.enteredBy = tmp;
  }


  /**
   * Sets the enteredBy attribute of the ReportQueue object
   *
   * @param tmp The new enteredBy value
   */
  public void setEnteredBy(String tmp) {
    this.enteredBy = Integer.parseInt(tmp);
  }


  /**
   * Sets the status attribute of the ReportQueue object
   *
   * @param tmp The new status value
   */
  public void setStatus(int tmp) {
    this.status = tmp;
  }


  /**
   * Sets the status attribute of the ReportQueue object
   *
   * @param tmp The new status value
   */
  public void setStatus(String tmp) {
    this.status = Integer.parseInt(tmp);
  }


  /**
   * Sets the processed attribute of the ReportQueue object
   *
   * @param tmp The new processed value
   */
  public void setProcessed(Timestamp tmp) {
    this.processed = tmp;
  }


  /**
   * Sets the processed attribute of the ReportQueue object
   *
   * @param tmp The new processed value
   */
  public void setProcessed(String tmp) {
    this.processed = DatabaseUtils.parseTimestamp(tmp);
  }


  /**
   * Sets the filename attribute of the ReportQueue object
   *
   * @param tmp The new filename value
   */
  public void setFilename(String tmp) {
    this.filename = tmp;
  }


  /**
   * Sets the size attribute of the ReportQueue object
   *
   * @param tmp The new size value
   */
  public void setSize(long tmp) {
    this.size = tmp;
  }


  /**
   * Sets the enabled attribute of the ReportQueue object
   *
   * @param tmp The new enabled value
   */
  public void setEnabled(boolean tmp) {
    this.enabled = tmp;
  }


  /**
   * Sets the enabled attribute of the ReportQueue object
   *
   * @param tmp The new enabled value
   */
  public void setEnabled(String tmp) {
    this.enabled = DatabaseUtils.parseBoolean(tmp);
  }

  public int getProjectId() {
    return projectId;
  }

  public void setProjectId(int projectId) {
    this.projectId = projectId;
  }

  /**
   * Sets the position attribute of the ReportQueue object
   *
   * @param tmp The new position value
   */
  public void setPosition(int tmp) {
    this.position = tmp;
  }


  /**
   * Sets the position attribute of the ReportQueue object
   *
   * @param tmp The new position value
   */
  public void setPosition(String tmp) {
    this.position = Integer.parseInt(tmp);
  }


  /**
   * Sets the report attribute of the ReportQueue object
   *
   * @param tmp The new report value
   */
  public void setReport(Report tmp) {
    this.report = tmp;
  }


  /**
   * Sets the throwNotFoundException attribute of the ReportQueue object
   *
   * @param tmp The new throwNotFoundException value
   */
  public void setThrowNotFoundException(boolean tmp) {
    this.throwNotFoundException = tmp;
  }


  /**
   * Sets the throwNotFoundException attribute of the ReportQueue object
   *
   * @param tmp The new throwNotFoundException value
   */
  public void setThrowNotFoundException(String tmp) {
    this.throwNotFoundException = DatabaseUtils.parseBoolean(tmp);
  }


  /**
   * Gets the id attribute of the ReportQueue object
   *
   * @return The id value
   */
  public int getId() {
    return id;
  }


  /**
   * Gets the reportId attribute of the ReportQueue object
   *
   * @return The reportId value
   */
  public int getReportId() {
    return reportId;
  }


  /**
   * Gets the entered attribute of the ReportQueue object
   *
   * @return The entered value
   */
  public Timestamp getEntered() {
    return entered;
  }

  public Timestamp getModificationDate() {
    if (entered != null) {
      return entered;
    }
    return new Timestamp((new java.util.Date()).getTime());
  }

  /**
   * Gets the enteredBy attribute of the ReportQueue object
   *
   * @return The enteredBy value
   */
  public int getEnteredBy() {
    return enteredBy;
  }


  /**
   * Gets the status attribute of the ReportQueue object
   *
   * @return The status value
   */
  public int getStatus() {
    return status;
  }


  /**
   * Gets the processed attribute of the ReportQueue object
   *
   * @return The processed value
   */
  public Timestamp getProcessed() {
    return processed;
  }


  /**
   * Gets the filename attribute of the ReportQueue object
   *
   * @return The filename value
   */
  public String getFilename() {
    return filename;
  }


  /**
   * Gets the size attribute of the ReportQueue object
   *
   * @return The size value
   */
  public long getSize() {
    return size;
  }

  public long getRelativeSize() {
    long newSize = (size / 1000);
    if (newSize == 0) {
      return 1;
    } else {
      return newSize;
    }
  }


  /**
   * Gets the enabled attribute of the ReportQueue object
   *
   * @return The enabled value
   */
  public boolean getEnabled() {
    return enabled;
  }


  /**
   * Gets the position attribute of the ReportQueue object
   *
   * @return The position value
   */
  public int getPosition() {
    return position;
  }


  /**
   * Gets the report attribute of the ReportQueue object
   *
   * @return The report value
   */
  public Report getReport() {
    return report;
  }


  public boolean getSendEmail() {
    return sendEmail;
  }

  public void setSendEmail(boolean sendEmail) {
    this.sendEmail = sendEmail;
  }

  public void setSendEmail(String tmp) {
    sendEmail = DatabaseUtils.parseBoolean(tmp);
  }

  public boolean hasSchedule() {
    return (scheduleMonday || scheduleTuesday || scheduleWednesday || scheduleThursday ||
        scheduleFriday || scheduleSaturday || scheduleSunday);
  }

  public boolean getScheduleMonday() {
    return scheduleMonday;
  }

  public void setScheduleMonday(boolean scheduleMonday) {
    this.scheduleMonday = scheduleMonday;
  }

  public void setScheduleMonday(String tmp) {
    this.scheduleMonday = DatabaseUtils.parseBoolean(tmp);
  }

  public boolean getScheduleTuesday() {
    return scheduleTuesday;
  }

  public void setScheduleTuesday(boolean scheduleTuesday) {
    this.scheduleTuesday = scheduleTuesday;
  }

  public void setScheduleTuesday(String tmp) {
    this.scheduleTuesday = DatabaseUtils.parseBoolean(tmp);
  }

  public boolean getScheduleWednesday() {
    return scheduleWednesday;
  }

  public void setScheduleWednesday(boolean scheduleWednesday) {
    this.scheduleWednesday = scheduleWednesday;
  }

  public void setScheduleWednesday(String tmp) {
    this.scheduleWednesday = DatabaseUtils.parseBoolean(tmp);
  }

  public boolean getScheduleThursday() {
    return scheduleThursday;
  }

  public void setScheduleThursday(boolean scheduleThursday) {
    this.scheduleThursday = scheduleThursday;
  }

  public void setScheduleThursday(String tmp) {
    this.scheduleThursday = DatabaseUtils.parseBoolean(tmp);
  }

  public boolean getScheduleFriday() {
    return scheduleFriday;
  }

  public void setScheduleFriday(boolean scheduleFriday) {
    this.scheduleFriday = scheduleFriday;
  }

  public void setScheduleFriday(String tmp) {
    this.scheduleFriday = DatabaseUtils.parseBoolean(tmp);
  }

  public boolean getScheduleSaturday() {
    return scheduleSaturday;
  }

  public void setScheduleSaturday(boolean scheduleSaturday) {
    this.scheduleSaturday = scheduleSaturday;
  }

  public void setScheduleSaturday(String tmp) {
    this.scheduleSaturday = DatabaseUtils.parseBoolean(tmp);
  }

  public boolean getScheduleSunday() {
    return scheduleSunday;
  }

  public void setScheduleSunday(boolean scheduleSunday) {
    this.scheduleSunday = scheduleSunday;
  }

  public void setScheduleSunday(String tmp) {
    this.scheduleSunday = DatabaseUtils.parseBoolean(tmp);
  }

  public int getCleanup() {
    return cleanup;
  }

  public void setCleanup(int cleanup) {
    this.cleanup = cleanup;
  }

  public void setCleanup(String tmp) {
    this.cleanup = Integer.parseInt(tmp);
  }

  public void setScheduleTime(Timestamp tmp) {
    this.scheduleTime = tmp;
  }


  /**
   * Sets the requestDate attribute of the Project object
   *
   * @param tmp The new requestDate value
   */
  public void setScheduleTime(String tmp) {
    scheduleTime = DatabaseUtils.parseTimestamp(tmp);
  }

  public Timestamp getScheduleTime() {
    return scheduleTime;
  }

  public String getOutput() {
    return output;
  }

  public void setOutput(String output) {
    this.output = output;
  }

  public int getOutputTypeConstant() {
    if ("pdf".equals(output)) {
      return REPORT_TYPE_PDF;
    } else if ("html".equals(output)) {
      return REPORT_TYPE_HTML;
    } else if ("csv".equals(output)) {
      return REPORT_TYPE_CSV;
    } else if ("excel".equals(output)) {
      return REPORT_TYPE_EXCEL;
    }
    return -1;
  }

  /**
   * Gets the throwNotFoundException attribute of the ReportQueue object
   *
   * @return The throwNotFoundException value
   */
  public boolean getThrowNotFoundException() {
    return throwNotFoundException;
  }


  /**
   * Populates this object from a resultset
   *
   * @param rs Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public void buildRecord(ResultSet rs) throws SQLException {
    id = rs.getInt("queue_id");
    reportId = rs.getInt("report_id");
    entered = rs.getTimestamp("entered");
    enteredBy = rs.getInt("enteredby");
    processed = rs.getTimestamp("processed");
    status = rs.getInt("status");
    filename = rs.getString("filename");
    size = DatabaseUtils.getLong(rs, "filesize");
    enabled = rs.getBoolean("enabled");
    projectId = DatabaseUtils.getInt(rs, "project_id");
    sendEmail = rs.getBoolean("send_email");
    scheduleMonday = rs.getBoolean("schedule_monday");
    scheduleTuesday = rs.getBoolean("schedule_tuesday");
    scheduleWednesday = rs.getBoolean("schedule_wednesday");
    scheduleThursday = rs.getBoolean("schedule_thursday");
    scheduleFriday = rs.getBoolean("schedule_friday");
    scheduleSaturday = rs.getBoolean("schedule_saturday");
    scheduleSunday = rs.getBoolean("schedule_sunday");
    cleanup = rs.getInt("cleanup");
    scheduleTime = rs.getTimestamp("schedule_time");
    output = rs.getString("output");
  }

  private void insertReport(Connection db, int projectId) throws SQLException {
    id = DatabaseUtils.getNextSeq(db, "report_queue_queue_id_seq", id);
    PreparedStatement pst = db.prepareStatement(
        "INSERT INTO report_queue " +
            "(" + (id > -1 ? "queue_id, " : "") + "report_id, enteredby, project_id, " +
            "send_email, " +
            "schedule_monday, schedule_tuesday, schedule_wednesday, schedule_thursday, schedule_friday, schedule_saturday, schedule_sunday, " +
            "schedule_time, cleanup, output) " +
            "VALUES (" + (id > -1 ? "?, " : "") + "?, ?, ?, " +
            "?, " +
            "?, ?, ?, ?, ?, ?, ?, " +
            "?, ?, ?) ");
    int i = 0;
    if (id > -1) {
      pst.setInt(++i, id);
    }
    pst.setInt(++i, reportId);
    pst.setInt(++i, enteredBy);
    DatabaseUtils.setInt(pst, ++i, projectId);
    pst.setBoolean(++i, sendEmail);
    pst.setBoolean(++i, scheduleMonday);
    pst.setBoolean(++i, scheduleTuesday);
    pst.setBoolean(++i, scheduleWednesday);
    pst.setBoolean(++i, scheduleThursday);
    pst.setBoolean(++i, scheduleFriday);
    pst.setBoolean(++i, scheduleSaturday);
    pst.setBoolean(++i, scheduleSunday);
    pst.setTimestamp(++i, scheduleTime);
    pst.setInt(++i, cleanup);
    pst.setString(++i, output);
    pst.execute();
    pst.close();
    id = DatabaseUtils.getCurrVal(db, "report_queue_queue_id_seq", id);
    calculateNextRunDate(db);
  }

  /**
   * Based on Criteria that has been set, this methods makes a copy and stores
   * the settings for running the specified report
   *
   * @param db         Description of the Parameter
   * @param parameters Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public void insert(Connection db, ParameterList parameters) throws SQLException {
    try {
      db.setAutoCommit(false);
      insertReport(db, parameters.getValueAsInt("projectId"));
      //Insert the criteria for processing the report
      int rqcId = DatabaseUtils.getNextSeq(
          db, "report_criteria_criteria_id_seq", -1);
      PreparedStatement pst = db.prepareStatement(
          "INSERT INTO report_criteria " +
              "(" + (rqcId > -1 ? "criteria_id, " : "") + "queue_id, \"parameter\", value) " +
              "VALUES (" + (rqcId > -1 ? "?, " : "") + "?, ?, ?) ");
      Iterator params = parameters.iterator();
      while (params.hasNext()) {
        Parameter param = (Parameter) params.next();
        if (param.hasValue()) {
          int ip = 0;
          if (rqcId > -1) {
            pst.setInt(++ip, rqcId);
          }
          pst.setInt(++ip, id);
          pst.setString(++ip, param.getName());
          pst.setString(++ip, param.getValue());
          pst.execute();
          if (rqcId > -1 && params.hasNext()) {
            rqcId = DatabaseUtils.getNextSeq(
                db, "report_criteria_criteria_id_seq", rqcId);
          }
        }
      }
      pst.close();
    } catch (Exception e) {
      db.rollback();
      throw new SQLException(e.getMessage());
    } finally {
      db.setAutoCommit(true);
    }
  }

  public void insert(Connection db, CriteriaList criteria) throws SQLException {
    try {
      db.setAutoCommit(false);
      insertReport(db, criteria.getValueAsInt("projectId"));
      //Insert the criteria for processing the report
      int rqcId = DatabaseUtils.getNextSeq(
          db, "report_criteria_criteria_id_seq", -1);
      PreparedStatement pst = db.prepareStatement(
          "INSERT INTO report_criteria " +
              "(" + (rqcId > -1 ? "criteria_id, " : "") + "queue_id, \"parameter\", value) " +
              "VALUES (" + (rqcId > -1 ? "?, " : "") + "?, ?, ?) ");
      Iterator params = criteria.values().iterator();
      while (params.hasNext()) {
        Criteria param = (Criteria) params.next();
        int ip = 0;
        if (rqcId > -1) {
          pst.setInt(++ip, rqcId);
        }
        pst.setInt(++ip, id);
        pst.setString(++ip, param.getParameter());
        pst.setString(++ip, param.getValue());
        pst.execute();
        if (rqcId > -1 && params.hasNext()) {
          rqcId = DatabaseUtils.getNextSeq(
              db, "report_criteria_criteria_id_seq", rqcId);
        }
      }
      pst.close();
    } catch (Exception e) {
      db.rollback();
      throw new SQLException(e.getMessage());
    } finally {
      db.setAutoCommit(true);
    }
  }


  /**
   * Populates the report property when needed, otherwise the report property
   * is unset by default
   *
   * @param db Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public void buildReport(Connection db) throws SQLException {
    report = new Report(db, reportId);
  }


  /**
   * Updates the status of the report during different stages of processing
   *
   * @param db Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  public boolean updateStatus(Connection db) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "UPDATE report_queue " +
            "SET status = ?, " +
            (filename != null ? "filename = ?, " : "") +
            "filesize = ?, " +
            "processed = " + DatabaseUtils.getCurrentTimestamp(db) + " " +
            "WHERE queue_id = ? ");
    int i = 0;
    pst.setInt(++i, status);
    if (filename != null) {
      pst.setString(++i, filename);
    }
    DatabaseUtils.setLong(pst, ++i, size);
    pst.setInt(++i, id);
    int count = pst.executeUpdate();
    pst.close();
    return (count == 1);
  }


  /**
   * Deletes the ReportQueue reference and the associated file
   *
   * @param db           Description of the Parameter
   * @param baseFilePath Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  public boolean delete(Connection db, String baseFilePath) throws SQLException {
    if (id == -1) {
      return false;
    }
    // Try to delete the file first
    if (filename != null) {
      String filePath = baseFilePath + DateUtils.getDatePath(getEntered()) + getFilename();
      File file = new File(filePath);
      if (file.exists()) {
        file.delete();
      }
    }
    // Delete any associated report files
    if (filename != null) {
      String filePath = baseFilePath + DateUtils.getDatePath(getEntered()) + getFilename() + "_files";
      File file = new File(filePath);
      if (file.isDirectory()) {
        file.delete();
      }
    }

    //Delete the record and associated data
    boolean commit = true;
    try {
      commit = db.getAutoCommit();
      if (commit) {
        db.setAutoCommit(false);
      }
      //Delete the criteria
      PreparedStatement pst = db.prepareStatement(
          "DELETE FROM report_criteria " +
              "WHERE queue_id = ? ");
      pst.setInt(1, id);
      pst.execute();
      pst.close();
      //Delete the queue
      pst = db.prepareStatement(
          "DELETE FROM report_queue " +
              "WHERE queue_id = ? ");
      pst.setInt(1, id);
      pst.execute();
      pst.close();
      if (commit) {
        db.commit();
      }
    } catch (Exception e) {
      if (commit) {
        db.rollback();
      }
      throw new SQLException(e.getMessage());
    } finally {
      if (commit) {
        db.setAutoCommit(true);
      }
    }
    return true;
  }

  public static ArrayList getTimeZoneParams() {
    ArrayList thisList = new ArrayList();
    thisList.add("scheduleTime");
    return thisList;
  }

  public boolean calculateNextRunDate(Connection db) throws SQLException {
    if (scheduleTime == null) {
      return false;
    }
    Calendar nextCal = Calendar.getInstance();
    nextCal.setTime(scheduleTime);
    // Find next date after now
    Calendar now = Calendar.getInstance();
    while (nextCal.before(now)) {
      nextCal.add(Calendar.DATE, 1);
    }
    boolean found = false;
    int count = 0;
    while (!found && count < 8) {
      ++count;
      if ((nextCal.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY && scheduleMonday) ||
          (nextCal.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY && scheduleTuesday) ||
          (nextCal.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY && scheduleWednesday) ||
          (nextCal.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY && scheduleThursday) ||
          (nextCal.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY && scheduleFriday) ||
          (nextCal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY && scheduleSaturday) ||
          (nextCal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY && scheduleSunday)) {
        found = true;
      }
      if (!found) {
        nextCal.add(Calendar.DATE, 1);
      }
    }
    if (found) {
      // Set the next time this should run...
      PreparedStatement pst = db.prepareStatement(
          "UPDATE report_queue " +
              "SET status = ?, " +
              "schedule_time = ? " +
              "WHERE queue_id = ? ");
      int i = 0;
      pst.setInt(++i, STATUS_SCHEDULED);
      pst.setTimestamp(++i, new Timestamp(nextCal.getTimeInMillis()));
      pst.setInt(++i, id);
      pst.executeUpdate();
      pst.close();
    }
    return true;
  }
}

