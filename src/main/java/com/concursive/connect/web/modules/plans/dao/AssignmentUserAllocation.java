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

package com.concursive.connect.web.modules.plans.dao;

import com.concursive.connect.web.modules.timesheet.dao.DailyTimesheetList;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Description of the Class
 *
 * @author matt rajkowski
 * @version $Id: AssignmentUserAllocation.java,v 1.3 2004/10/29 05:14:39 matt
 *          Exp $
 * @created October 29, 2004
 */
public class AssignmentUserAllocation extends HashMap {

  private AssignmentProjectAllocation projectAllocation = null;
  private int userId = -1;
  // calculation breaks characteristics into days
  private HashMap estimatedDailyHours = new HashMap();
  private HashMap actualDailyHours = new HashMap();
  private DailyTimesheetList timesheet = null;


  /**
   * Constructor for the AssignmentUserAllocation object
   */
  public AssignmentUserAllocation() {
  }


  /**
   * Constructor for the AssignmentUserAllocation object
   *
   * @param tmp Description of the Parameter
   */
  public AssignmentUserAllocation(int tmp) {
    userId = tmp;
  }


  /**
   * Gets the projectMap attribute of the AssignmentUserAllocation object
   *
   * @param id Description of the Parameter
   * @return The projectMap value
   */
  public AssignmentProjectAllocation getProjectMap(int id) {
    Integer projectId = new Integer(id);
    AssignmentProjectAllocation projectMap = (AssignmentProjectAllocation) this.get(projectId);
    if (projectMap == null) {
      projectMap = new AssignmentProjectAllocation();
      this.put(projectId, projectMap);
    }
    projectMap.setUserAllocation(this);
    return projectMap;
  }


  /**
   * Gets the projectAllocation attribute of the AssignmentUserAllocation
   * object
   *
   * @return The projectAllocation value
   */
  public AssignmentProjectAllocation getProjectAllocation() {
    return projectAllocation;
  }


  /**
   * Sets the projectAllocation attribute of the AssignmentUserAllocation
   * object
   *
   * @param tmp The new projectAllocation value
   */
  public void setProjectAllocation(AssignmentProjectAllocation tmp) {
    this.projectAllocation = tmp;
  }


  /**
   * Gets the userId attribute of the AssignmentUserAllocation object
   *
   * @return The userId value
   */
  public int getUserId() {
    return userId;
  }


  /**
   * Sets the userId attribute of the AssignmentUserAllocation object
   *
   * @param tmp The new userId value
   */
  public void setUserId(int tmp) {
    this.userId = tmp;
  }


  /**
   * Sets the userId attribute of the AssignmentUserAllocation object
   *
   * @param tmp The new userId value
   */
  public void setUserId(String tmp) {
    this.userId = Integer.parseInt(tmp);
  }


  /**
   * Gets the estimatedDailyHours attribute of the AssignmentUserAllocation
   * object
   *
   * @return The estimatedDailyHours value
   */
  public HashMap getEstimatedDailyHours() {
    return estimatedDailyHours;
  }


  /**
   * Sets the estimatedDailyHours attribute of the AssignmentUserAllocation
   * object
   *
   * @param tmp The new estimatedDailyHours value
   */
  public void setEstimatedDailyHours(HashMap tmp) {
    this.estimatedDailyHours = tmp;
  }


  /**
   * Gets the actualDailyHours attribute of the AssignmentUserAllocation object
   *
   * @return The actualDailyHours value
   */
  public HashMap getActualDailyHours() {
    return actualDailyHours;
  }


  /**
   * Sets the actualDailyHours attribute of the AssignmentUserAllocation object
   *
   * @param tmp The new actualDailyHours value
   */
  public void setActualDailyHours(HashMap tmp) {
    this.actualDailyHours = tmp;
  }

  public DailyTimesheetList getTimesheet() {
    if (timesheet == null) {
      timesheet = new DailyTimesheetList();
    }
    return timesheet;
  }

  public void setTimesheet(DailyTimesheetList timesheet) {
    this.timesheet = timesheet;
  }

  /**
   * Adds a feature to the Entry attribute of the AssignmentUserAllocation
   * object
   *
   * @param assignment The feature to be added to the Entry attribute
   */
  public void addEntry(Assignment assignment) {
    AssignmentAllocation alloc = new AssignmentAllocation(assignment, timesheet);
    if (alloc.getValid()) {
      this.update(alloc);
      //projectAllocation.update(alloc);
    }
  }


  /**
   * Description of the Method
   *
   * @param alloc Description of the Parameter
   */
  private void update(AssignmentAllocation alloc) {
    Iterator days = alloc.getEstimatedDailyHours().keySet().iterator();
    while (days.hasNext()) {
      String date = (String) days.next();
      Double hours = (Double) alloc.getEstimatedDailyHours().get(date);
      add(date, hours);
    }
  }


  /**
   * Description of the Method
   *
   * @param date  Description of the Parameter
   * @param hours Description of the Parameter
   */
  private void add(String date, Double hours) {
    Double value = (Double) estimatedDailyHours.get(date);
    if (value == null) {
      value = new Double(0.0);
    }
    value = new Double(value.doubleValue() + hours.doubleValue());
    estimatedDailyHours.put(date, value);
  }
}

