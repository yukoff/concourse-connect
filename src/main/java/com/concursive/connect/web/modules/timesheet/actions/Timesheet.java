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

package com.concursive.connect.web.modules.timesheet.actions;

import com.concursive.commons.db.DatabaseUtils;
import com.concursive.commons.web.mvc.actions.ActionContext;
import com.concursive.connect.Constants;
import com.concursive.connect.web.controller.actions.GenericAction;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.profile.dao.ProjectList;
import com.concursive.connect.web.modules.timesheet.dao.DailyTimesheetList;
import com.concursive.connect.web.modules.timesheet.dao.ProjectTimesheetList;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Description of the Class
 *
 * @author matt rajkowski
 * @version $Id$
 * @created November 8, 2004
 */
public final class Timesheet extends GenericAction {

  private final static int PREVIOUS_WEEKS = 3;
  private final static int NEXT_WEEKS = 12;


  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandDefault(ActionContext context) {
    setMaximized(context);
    if (getUser(context).getId() < 0) {
      return "PermissionError";
    }
    try {
      // Determine the dates for display
      Calendar cal = Calendar.getInstance();
      // See if the request has a suggested date
      if (context.getRequest().getParameter("goToDate") != null) {
        User thisUser = getUser(context);
        Timestamp timestamp = DatabaseUtils.parseDateToTimestamp(context.getRequest().getParameter("goToDate"),
            thisUser.getLocale());
        cal.setTimeInMillis(timestamp.getTime());
        cal.setTimeZone(TimeZone.getTimeZone(thisUser.getTimeZone()));
      }
      cal.set(Calendar.MILLISECOND, 0);
      cal.set(Calendar.SECOND, 0);
      cal.set(Calendar.MINUTE, 0);
      cal.set(Calendar.HOUR_OF_DAY, 0);
      // Find the most recent Sunday
      while (cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
        cal.add(Calendar.DAY_OF_WEEK, -1);
      }
      Timestamp thisWeekStartDate = new Timestamp(cal.getTimeInMillis());
      context.getRequest().setAttribute("thisWeekStartDate", String.valueOf(thisWeekStartDate.getTime()));
    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return ("SystemError");
    }
    return "TimesheetOK";
  }


  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandSlider(ActionContext context) {
    if (getUser(context).getId() < 0) {
      return "PermissionError";
    }
    Connection db = null;
    try {
      // Determine the days to show
      String start = context.getRequest().getParameter("start");
      // Determine the dates for display
      Calendar cal = Calendar.getInstance();
      cal.set(Calendar.MILLISECOND, 0);
      cal.set(Calendar.SECOND, 0);
      cal.set(Calendar.MINUTE, 0);
      cal.set(Calendar.HOUR_OF_DAY, 0);
      // Determine the time period
      DailyTimesheetList timesheet = new DailyTimesheetList();
      timesheet.setUserId(getUserId(context));
      if (start != null) {
        long startLong = Long.parseLong(start);
        cal.setTimeInMillis(startLong);
      }
      // Find the most recent Sunday
      while (cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
        cal.add(Calendar.DAY_OF_WEEK, -1);
      }
      // 4 weeks earlier
      cal.add(Calendar.WEEK_OF_YEAR, -PREVIOUS_WEEKS);
      Timestamp startDate = new Timestamp(cal.getTimeInMillis());
      // 4 + 12 weeks later
      cal.add(Calendar.WEEK_OF_YEAR, (PREVIOUS_WEEKS + NEXT_WEEKS));
      Timestamp endDate = new Timestamp(cal.getTimeInMillis());
      timesheet.setStartDate(startDate);
      timesheet.setEndDate(endDate);
      db = getConnection(context);
      // Load the user's timesheet for the whole period
      timesheet.buildList(db);
      context.getRequest().setAttribute("timesheet", timesheet);
    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return ("SystemError");
    } finally {
      freeConnection(context, db);
    }
    return "SliderOK";
  }


  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandWeek(ActionContext context) {
    if (getUser(context).getId() < 0) {
      return "PermissionError";
    }
    Connection db = null;
    try {
      // Determine the days to show
      String start = context.getRequest().getParameter("start");
      // Determine the dates for display
      Calendar cal = Calendar.getInstance();
      cal.set(Calendar.MILLISECOND, 0);
      cal.set(Calendar.SECOND, 0);
      cal.set(Calendar.MINUTE, 0);
      cal.set(Calendar.HOUR_OF_DAY, 0);
      // Determine the time period
      DailyTimesheetList timesheet = new DailyTimesheetList();
      timesheet.setUserId(getUserId(context));
      long startLong = Long.parseLong(start);
      cal.setTimeInMillis(startLong);
      Timestamp startDate = new Timestamp(cal.getTimeInMillis());
      timesheet.setStartDate(startDate);
      cal.add(Calendar.WEEK_OF_YEAR, 1);
      Timestamp endDate = new Timestamp(cal.getTimeInMillis());
      timesheet.setEndDate(endDate);
      db = getConnection(context);
      // Load the user's timesheet for the whole period
      timesheet.buildList(db);
      context.getRequest().setAttribute("timesheet", timesheet);
      // Load the timesheet by project
      ProjectTimesheetList projectTimesheetList = new ProjectTimesheetList();
      projectTimesheetList.buildList(db, timesheet);
      context.getRequest().setAttribute("projectTimesheetList", projectTimesheetList);
      // Load the user's projects to be displayed as choices
      ProjectList projectList = new ProjectList();
      projectList.setProjectsForUser(getUserId(context));
      projectList.setIncludeGuestProjects(false);
      projectList.setOpenProjectsOnly(true);
      projectList.setWithProjectDaysComplete(-PREVIOUS_WEEKS * 7);
      projectList.setEmptyHtmlSelectRecord("");
      projectList.setPortalState(Constants.FALSE);
      projectList.buildList(db);
      context.getRequest().setAttribute("projectList", projectList);
    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return ("SystemError");
    } finally {
      freeConnection(context, db);
    }
    return "WeekOK";
  }


  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandSave(ActionContext context) {
    if (getUser(context).getId() < 0) {
      return "PermissionError";
    }
    Connection db = null;
    try {
      db = getConnection(context);
      // Populate and save the user timesheet
      DailyTimesheetList dailyTimesheetList = new DailyTimesheetList(context.getRequest());
      dailyTimesheetList.setUserId(getUserId(context));
      dailyTimesheetList.setEnteredBy(getUserId(context));
      dailyTimesheetList.setModifiedBy(getUserId(context));
      dailyTimesheetList.save(db);
    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return ("SystemError");
    } finally {
      freeConnection(context, db);
    }
    return "SaveOK";
  }
}

