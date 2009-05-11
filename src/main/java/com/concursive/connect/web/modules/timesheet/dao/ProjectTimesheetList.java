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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 * Description of the Class
 *
 * @author matt rajkowski
 * @version $Id$
 * @created December 3, 2004
 */
public class ProjectTimesheetList extends LinkedHashMap {

  private int timesheetId = -1;


  /**
   * Constructor for the ProjectTimesheetList object
   */
  public ProjectTimesheetList() {
  }


  /**
   * Gets the timesheetId attribute of the ProjectTimesheetList object
   *
   * @return The timesheetId value
   */
  public int getTimesheetId() {
    return timesheetId;
  }


  /**
   * Sets the timesheetId attribute of the ProjectTimesheetList object
   *
   * @param tmp The new timesheetId value
   */
  public void setTimesheetId(int tmp) {
    this.timesheetId = tmp;
  }


  /**
   * Sets the timesheetId attribute of the ProjectTimesheetList object
   *
   * @param tmp The new timesheetId value
   */
  public void setTimesheetId(String tmp) {
    this.timesheetId = Integer.parseInt(tmp);
  }


  /**
   * Description of the Method
   *
   * @param db        Description of the Parameter
   * @param timesheet Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public void buildList(Connection db, DailyTimesheetList timesheet) throws SQLException {
    // Prepare the query
    PreparedStatement pst = db.prepareStatement(
        "SELECT timesheet_id, project_id, hours " +
            "FROM timesheet_projects " +
            "WHERE timesheet_id = ? " +
            "ORDER BY hours desc ");
    // Iterate the days
    Iterator days = timesheet.values().iterator();
    while (days.hasNext()) {
      DailyTimesheet dailyTimesheet = (DailyTimesheet) days.next();
      // Load the corresponding projects and hours
      pst.setInt(1, dailyTimesheet.getId());
      ResultSet rs = pst.executeQuery();
      while (rs.next()) {
        ProjectTimesheet projectTimesheet = new ProjectTimesheet(rs);
        // For this project and this day set the total hours
        DailyTimesheet newDailyTimesheet = new DailyTimesheet();
        newDailyTimesheet.setId(dailyTimesheet.getId());
        newDailyTimesheet.setEntryDate(dailyTimesheet.getEntryDate());
        newDailyTimesheet.setTotalHours(projectTimesheet.getHours());
        add(projectTimesheet, newDailyTimesheet);
      }
      rs.close();
    }
    pst.close();
  }


  /**
   * Description of the Method
   *
   * @param projectTimesheet Description of the Parameter
   * @param dailyTimesheet   Description of the Parameter
   */
  private void add(ProjectTimesheet projectTimesheet, DailyTimesheet dailyTimesheet) {
    Integer projectId = new Integer(projectTimesheet.getProjectId());
    ProjectTimesheet thisTimesheet = (ProjectTimesheet) this.get(projectId);
    if (thisTimesheet == null) {
      thisTimesheet = projectTimesheet;
      this.put(projectId, thisTimesheet);
    } else {
      thisTimesheet.add(projectTimesheet.getHours());
    }
    thisTimesheet.add(dailyTimesheet);
  }


  /**
   * Description of the Method
   *
   * @param thisProjectId   Description of the Parameter
   * @param hoursAdjustment Description of the Parameter
   */
  public void add(int thisProjectId, double hoursAdjustment) {
    Integer projectId = new Integer(thisProjectId);
    ProjectTimesheet projectTimesheet = (ProjectTimesheet) this.get(projectId);
    if (projectTimesheet == null) {
      projectTimesheet = new ProjectTimesheet();
      projectTimesheet.setProjectId(thisProjectId);
      this.put(projectId, projectTimesheet);
    }
    projectTimesheet.add(hoursAdjustment);
  }


  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public void insert(Connection db) throws SQLException {
    // TODO: Check if in auto-commit mode
    Iterator i = this.values().iterator();
    while (i.hasNext()) {
      ProjectTimesheet projectTimesheet = (ProjectTimesheet) i.next();
      if (projectTimesheet.getHours() != 0) {
        projectTimesheet.setTimesheetId(timesheetId);
        projectTimesheet.insert(db);
      }
    }
  }
}

