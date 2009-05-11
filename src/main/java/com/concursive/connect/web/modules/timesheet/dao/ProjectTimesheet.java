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

package com.concursive.connect.web.modules.timesheet.dao;

import com.concursive.commons.db.DatabaseUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;

/**
 * @author matt rajkowski
 * @version $Id$
 * @created December 3, 2004
 */
public class ProjectTimesheet {

  private int id = -1;
  private int timesheetId = -1;
  private int projectId = -1;
  private double hours = 0;
  private DailyTimesheetList dailyTimesheetList = null;


  /**
   * Gets the timesheetId attribute of the ProjectTimesheet object
   *
   * @return The timesheetId value
   */
  public int getTimesheetId() {
    return timesheetId;
  }


  /**
   * Sets the timesheetId attribute of the ProjectTimesheet object
   *
   * @param tmp The new timesheetId value
   */
  public void setTimesheetId(int tmp) {
    this.timesheetId = tmp;
  }


  /**
   * Sets the timesheetId attribute of the ProjectTimesheet object
   *
   * @param tmp The new timesheetId value
   */
  public void setTimesheetId(String tmp) {
    this.timesheetId = Integer.parseInt(tmp);
  }


  /**
   * Gets the projectId attribute of the ProjectTimesheet object
   *
   * @return The projectId value
   */
  public int getProjectId() {
    return projectId;
  }


  /**
   * Sets the projectId attribute of the ProjectTimesheet object
   *
   * @param tmp The new projectId value
   */
  public void setProjectId(int tmp) {
    this.projectId = tmp;
  }


  /**
   * Sets the projectId attribute of the ProjectTimesheet object
   *
   * @param tmp The new projectId value
   */
  public void setProjectId(String tmp) {
    this.projectId = Integer.parseInt(tmp);
  }


  /**
   * Gets the hours attribute of the ProjectTimesheet object
   *
   * @return The hours value
   */
  public double getHours() {
    return hours;
  }


  /**
   * Sets the hours attribute of the ProjectTimesheet object
   *
   * @param tmp The new hours value
   */
  public void setHours(double tmp) {
    this.hours = tmp;
  }


  /**
   * Sets the hours attribute of the ProjectTimesheet object
   *
   * @param tmp The new hours value
   */
  public void setHours(String tmp) {
    this.hours = Double.parseDouble(tmp);
  }


  /**
   * Gets the dailyTimesheetList attribute of the ProjectTimesheet object
   *
   * @return The dailyTimesheetList value
   */
  public DailyTimesheetList getDailyTimesheetList() {
    if (dailyTimesheetList == null) {
      dailyTimesheetList = new DailyTimesheetList();
    }
    return dailyTimesheetList;
  }


  /**
   * Sets the dailyTimesheetList attribute of the ProjectTimesheet object
   *
   * @param tmp The new dailyTimesheetList value
   */
  public void setDailyTimesheetList(DailyTimesheetList tmp) {
    this.dailyTimesheetList = tmp;
  }


  /**
   * Constructor for the ProjectTimesheet object
   */
  public ProjectTimesheet() {
  }


  /**
   * Constructor for the ProjectTimesheet object
   *
   * @param rs Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public ProjectTimesheet(ResultSet rs) throws SQLException {
    buildRecord(rs);
  }


  /**
   * Description of the Method
   *
   * @param rs Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public void buildRecord(ResultSet rs) throws SQLException {
    timesheetId = rs.getInt("timesheet_id");
    projectId = DatabaseUtils.getInt(rs, "project_id");
    hours = rs.getDouble("hours");
  }


  /**
   * Description of the Method
   *
   * @param hoursAdjustment Description of the Parameter
   */
  public void add(double hoursAdjustment) {
    hours += hoursAdjustment;
  }


  /**
   * Description of the Method
   *
   * @param dailyTimesheet Description of the Parameter
   */
  public void add(DailyTimesheet dailyTimesheet) {
    getDailyTimesheetList().add(dailyTimesheet);
  }


  /**
   * Gets the hours attribute of the ProjectTimesheet object
   *
   * @param millis Description of the Parameter
   * @return The hours value
   */
  public double getHours(long millis) {
    Calendar cal = Calendar.getInstance();
    cal.setTimeInMillis(millis);
    DailyTimesheet dailyTimesheet = getDailyTimesheetList().getTimesheet(cal);
    if (dailyTimesheet != null) {
      return dailyTimesheet.getTotalHours();
    }
    return 0;
  }


  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public void insert(Connection db) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "INSERT INTO timesheet_projects " +
            "(timesheet_id, project_id, hours) VALUES (?, ?, ?) ");
    int i = 0;
    pst.setInt(++i, timesheetId);
    DatabaseUtils.setInt(pst, ++i, projectId);
    pst.setDouble(++i, hours);
    pst.execute();
    pst.close();
    id = DatabaseUtils.getCurrVal(db, "timesheet_projects_id_seq", -1);
  }
}

