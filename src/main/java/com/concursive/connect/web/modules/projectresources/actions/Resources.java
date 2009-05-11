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

package com.concursive.connect.web.modules.projectresources.actions;

import com.concursive.commons.web.mvc.actions.ActionContext;
import com.concursive.connect.web.controller.actions.GenericAction;
import com.concursive.connect.web.modules.plans.dao.AssignmentAllocationList;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.Calendar;

/**
 * Actions for generating allocation
 *
 * @author matt rajkowski
 * @version $Id$
 * @created October 28, 2004
 */
public final class Resources extends GenericAction {

  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandUsers(ActionContext context) {
    if (getUser(context).getId() < 0) {
      return "PermissionError";
    }
    // Determine the dates for display
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.MILLISECOND, 0);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.HOUR_OF_DAY, 0);
    // Find the most recent Sunday
    while (cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
      cal.add(Calendar.DAY_OF_WEEK, -1);
    }
    //Timestamp thisWeekStartDate = new Timestamp(cal.getTimeInMillis());
    // 4 weeks earlier
    cal.add(Calendar.WEEK_OF_YEAR, -2);
    Timestamp startDate = new Timestamp(cal.getTimeInMillis());
    // 4 + 12 weeks later
    cal.add(Calendar.WEEK_OF_YEAR, (2 + 12));
    Timestamp endDate = new Timestamp(cal.getTimeInMillis());
    Connection db = null;
    try {
      // Query the assignments
      db = getConnection(context);
      // Get the assignments in the range
      AssignmentAllocationList alloc = new AssignmentAllocationList();
      alloc.setProjectsForUser(getUserId(context));
      alloc.setStartDate(startDate);
      alloc.setEndDate(endDate);
      alloc.buildUserList(db);
      context.getRequest().setAttribute("allocationList", alloc);
    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return ("SystemError");
    } finally {
      freeConnection(context, db);
    }
    return "UsersOK";
  }


  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandUsersProjects(ActionContext context) {
    if (getUser(context).getId() < 0) {
      return "PermissionError";
    }
    Connection db = null;
    try {
      // Determine the dates for display
      Calendar cal = Calendar.getInstance();
      cal.set(Calendar.MILLISECOND, 0);
      cal.set(Calendar.SECOND, 0);
      cal.set(Calendar.MINUTE, 0);
      cal.set(Calendar.HOUR_OF_DAY, 0);
      // Find the most recent Sunday
      while (cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
        cal.add(Calendar.DAY_OF_WEEK, -1);
      }
      //Timestamp thisWeekStartDate = new Timestamp(cal.getTimeInMillis());
      // 4 weeks earlier
      cal.add(Calendar.WEEK_OF_YEAR, -2);
      Timestamp startDate = new Timestamp(cal.getTimeInMillis());
      // 4 + 12 weeks later
      cal.add(Calendar.WEEK_OF_YEAR, (2 + 12));
      Timestamp endDate = new Timestamp(cal.getTimeInMillis());
      // Query the assignments
      db = getConnection(context);
      // Get the assignments in the range
      AssignmentAllocationList alloc = new AssignmentAllocationList();
      alloc.setProjectsForUser(getUserId(context));
      alloc.setStartDate(startDate);
      alloc.setEndDate(endDate);
      alloc.buildUserList(db);
      context.getRequest().setAttribute("allocationList", alloc);
    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return ("SystemError");
    } finally {
      freeConnection(context, db);
    }
    return "UsersProjectsOK";
  }


  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandProjects(ActionContext context) {
    if (getUser(context).getId() < 0) {
      return "PermissionError";
    }
    // Determine the dates for display
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.MILLISECOND, 0);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.HOUR_OF_DAY, 0);
    // Find the most recent Sunday
    while (cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
      cal.add(Calendar.DAY_OF_WEEK, -1);
    }
    //Timestamp thisWeekStartDate = new Timestamp(cal.getTimeInMillis());
    // 4 weeks earlier
    cal.add(Calendar.WEEK_OF_YEAR, -2);
    Timestamp startDate = new Timestamp(cal.getTimeInMillis());
    // 4 + 12 weeks later
    cal.add(Calendar.WEEK_OF_YEAR, (2 + 12));
    Timestamp endDate = new Timestamp(cal.getTimeInMillis());
    Connection db = null;
    try {
      // Query the assignments
      db = getConnection(context);
      // Get the assignments in the range
      AssignmentAllocationList alloc = new AssignmentAllocationList();
      alloc.setProjectsForUser(getUserId(context));
      alloc.setStartDate(startDate);
      alloc.setEndDate(endDate);
      alloc.buildProjectList(db);
      context.getRequest().setAttribute("allocationList", alloc);
    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return ("SystemError");
    } finally {
      freeConnection(context, db);
    }
    return "ProjectsOK";
  }


  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandProjectsUsers(ActionContext context) {
    if (getUser(context).getId() < 0) {
      return "PermissionError";
    }
    Connection db = null;
    try {
      // Determine the dates for display
      Calendar cal = Calendar.getInstance();
      cal.set(Calendar.MILLISECOND, 0);
      cal.set(Calendar.SECOND, 0);
      cal.set(Calendar.MINUTE, 0);
      cal.set(Calendar.HOUR_OF_DAY, 0);
      // Find the most recent Sunday
      while (cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
        cal.add(Calendar.DAY_OF_WEEK, -1);
      }
      //Timestamp thisWeekStartDate = new Timestamp(cal.getTimeInMillis());
      // 4 weeks earlier
      cal.add(Calendar.WEEK_OF_YEAR, -2);
      Timestamp startDate = new Timestamp(cal.getTimeInMillis());
      // 4 + 12 weeks later
      cal.add(Calendar.WEEK_OF_YEAR, (2 + 12));
      Timestamp endDate = new Timestamp(cal.getTimeInMillis());
      // Query the assignments
      db = getConnection(context);
      // Get the assignments in the range
      AssignmentAllocationList alloc = new AssignmentAllocationList();
      alloc.setProjectsForUser(getUserId(context));
      alloc.setStartDate(startDate);
      alloc.setEndDate(endDate);
      alloc.buildProjectList(db);
      context.getRequest().setAttribute("allocationList", alloc);
    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return ("SystemError");
    } finally {
      freeConnection(context, db);
    }
    return "ProjectsUsersOK";
  }
}

