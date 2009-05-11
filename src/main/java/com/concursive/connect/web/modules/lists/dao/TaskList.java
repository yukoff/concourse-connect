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

package com.concursive.connect.web.modules.lists.dao;

import com.concursive.commons.date.DateUtils;
import com.concursive.commons.db.DatabaseUtils;
import com.concursive.connect.Constants;
import com.concursive.connect.cms.portal.dao.ProjectItemList;
import com.concursive.connect.web.utils.PagedListInfo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TimeZone;

/**
 * Description of the Class
 *
 * @author akhi_m
 * @version $Id$
 * @created August 15, 2002
 */
public class TaskList extends ArrayList<Task> {
  protected int enteredBy = -1;
  protected int modifiedBy = -1;
  protected PagedListInfo pagedListInfo = null;
  protected int owner = -1;
  protected int complete = -1;
  protected int tasksAssignedByUser = -1;
  protected java.sql.Timestamp alertRangeStart = null;
  protected java.sql.Timestamp alertRangeEnd = null;
  protected int categoryId = -1;
  protected int projectId = -1;
  protected int ticketId = -1;
  private int functionalArea = -1;
  private int status = -1;
  private int businessValue = -1;
  private int complexity = -1;
  private int targetRelease = -1;
  private int targetSprint = -1;
  private int loeRemaining = -1;
  private int assignedPriority = -1;
  private int linkModuleId = -1;
  private int linkItemId = -1;


  /**
   * Constructor for the TaskList object
   */
  public TaskList() {
  }


  /**
   * Sets the enteredBy attribute of the TaskList object
   *
   * @param enteredBy The new enteredBy value
   */
  public void setEnteredBy(int enteredBy) {
    this.enteredBy = enteredBy;
  }

  public int getModifiedBy() {
    return modifiedBy;
  }

  public void setModifiedBy(int modifiedBy) {
    this.modifiedBy = modifiedBy;
  }

  /**
   * Sets the pagedListInfo attribute of the TaskList object
   *
   * @param tmp The new pagedListInfo value
   */
  public void setPagedListInfo(PagedListInfo tmp) {
    this.pagedListInfo = tmp;
  }


  /**
   * Sets the complete attribute of the TaskList object
   *
   * @param tmp The new complete value
   */
  public void setComplete(int tmp) {
    this.complete = tmp;
  }


  /**
   * Sets the owner attribute of the TaskList object
   *
   * @param owner The new owner value
   */
  public void setOwner(int owner) {
    this.owner = owner;
  }

  public void setOwner(String tmp) {
    this.owner = Integer.parseInt(tmp);
  }

  public int getOwner() {
    return owner;
  }


  /**
   * Sets the tasksAssignedByUser attribute of the TaskList object
   *
   * @param tmp The new tasksAssignedByUser value
   */
  public void setTasksAssignedByUser(int tmp) {
    this.tasksAssignedByUser = tmp;
  }


  /**
   * Sets the alertRangeStart attribute of the Task object
   *
   * @param alertRangeStart The new alertRangeStart value
   */
  public void setAlertRangeStart(java.sql.Timestamp alertRangeStart) {
    this.alertRangeStart = alertRangeStart;
  }


  /**
   * Sets the ticketId attribute of the TaskList object
   *
   * @param ticketId The new ticketId value
   */
  public void setTicketId(int ticketId) {
    this.ticketId = ticketId;
  }


  /**
   * Gets the ticketId attribute of the TaskList object
   *
   * @return The ticketId value
   */
  public int getTicketId() {
    return ticketId;
  }

  public int getFunctionalArea() {
    return functionalArea;
  }

  public void setFunctionalArea(int functionalArea) {
    this.functionalArea = functionalArea;
  }

  public void setFunctionalArea(String tmp) {
    functionalArea = Integer.parseInt(tmp);
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public void setStatus(String tmp) {
    status = Integer.parseInt(tmp);
  }

  public int getBusinessValue() {
    return businessValue;
  }

  public void setBusinessValue(int businessValue) {
    this.businessValue = businessValue;
  }

  public void setBusinessValue(String tmp) {
    businessValue = Integer.parseInt(tmp);
  }


  public int getComplexity() {
    return complexity;
  }

  public void setComplexity(int complexity) {
    this.complexity = complexity;
  }

  public void setComplexity(String tmp) {
    complexity = Integer.parseInt(tmp);
  }


  public int getTargetRelease() {
    return targetRelease;
  }

  public void setTargetRelease(int targetRelease) {
    this.targetRelease = targetRelease;
  }

  public void setTargetRelease(String tmp) {
    targetRelease = Integer.parseInt(tmp);
  }

  public int getTargetSprint() {
    return targetSprint;
  }

  public void setTargetSprint(int targetSprint) {
    this.targetSprint = targetSprint;
  }

  public void setTargetSprint(String tmp) {
    targetSprint = Integer.parseInt(tmp);
  }

  public int getLoeRemaining() {
    return loeRemaining;
  }

  public void setLoeRemaining(int loeRemaining) {
    this.loeRemaining = loeRemaining;
  }

  public void setLoeRemaining(String tmp) {
    loeRemaining = Integer.parseInt(tmp);
  }

  public int getAssignedPriority() {
    return assignedPriority;
  }

  public void setAssignedPriority(int assignedPriority) {
    this.assignedPriority = assignedPriority;
  }

  public void setAssignedPriority(String tmp) {
    this.assignedPriority = Integer.parseInt(tmp);
  }

  /**
   * Sets the alertRangeEnd attribute of the Task object
   *
   * @param alertRangeEnd The new alertRangeEnd value
   */
  public void setAlertRangeEnd(java.sql.Timestamp alertRangeEnd) {
    this.alertRangeEnd = alertRangeEnd;
  }


  /**
   * Sets the categoryId attribute of the TaskList object
   *
   * @param tmp The new categoryId value
   */
  public void setCategoryId(int tmp) {
    this.categoryId = tmp;
  }


  /**
   * Sets the projectId attribute of the TaskList object
   *
   * @param tmp The new projectId value
   */
  public void setProjectId(int tmp) {
    this.projectId = tmp;
  }


  /**
   * Gets the enteredBy attribute of the TaskList object
   *
   * @return The enteredBy value
   */
  public int getEnteredBy() {
    return enteredBy;
  }

  public int getLinkModuleId() {
    return linkModuleId;
  }

  public void setLinkModuleId(int linkModuleId) {
    this.linkModuleId = linkModuleId;
  }

  public void setLinkModuleId(String tmp) {
    linkModuleId = Integer.parseInt(tmp);
  }

  public int getLinkItemId() {
    return linkItemId;
  }

  public void setLinkItemId(int linkItemId) {
    this.linkItemId = linkItemId;
  }

  public void setLinkItemId(String tmp) {
    linkItemId = Integer.parseInt(tmp);
  }


  /**
   * Return a mapping of number of alerts for each alert category.
   *
   * @param db       Description of the Parameter
   * @param timeZone Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  public HashMap queryRecordCount(Connection db, TimeZone timeZone) throws SQLException {
    PreparedStatement pst = null;
    ResultSet rs = null;
    HashMap events = new HashMap();
    StringBuffer sqlSelect = new StringBuffer();
    StringBuffer sqlFilter = new StringBuffer();
    StringBuffer sqlTail = new StringBuffer();
    createFilter(sqlFilter);
    sqlSelect.append(
        "SELECT duedate, count(*) AS nocols " +
            "FROM task t " +
            "WHERE t.task_id > -1 ");
    sqlFilter.append("AND duedate IS NOT NULL ");
    sqlTail.append("GROUP BY duedate ");
    pst = db.prepareStatement(sqlSelect.toString() + sqlFilter.toString() + sqlTail.toString());
    prepareFilter(pst);
    rs = pst.executeQuery();
    if (System.getProperty("DEBUG") != null) {
      System.out.println("TaskList-> Building Record Count ");
    }
    while (rs.next()) {
      String dueDate = DateUtils.getServerToUserDateString(timeZone, DateFormat.SHORT, rs.getTimestamp("duedate"));
      int temp = rs.getInt("nocols");
      events.put(dueDate, new Integer(temp));
    }
    rs.close();
    pst.close();
    return events;
  }


  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public void buildShortList(Connection db) throws SQLException {
    PreparedStatement pst = null;
    ResultSet rs = null;
    StringBuffer sqlSelect = new StringBuffer();
    StringBuffer sqlFilter = new StringBuffer();
    createFilter(sqlFilter);
    sqlSelect.append(
        "SELECT t.task_id, t.description, t.duedate, t.complete " +
            "FROM task t " +
            "WHERE t.task_id > -1 ");
    pst = db.prepareStatement(sqlSelect.toString() + sqlFilter.toString());
    prepareFilter(pst);
    rs = pst.executeQuery();
    while (rs.next()) {
      Task thisTask = new Task();
      thisTask.setId(rs.getInt("task_id"));
      thisTask.setDescription(rs.getString("description"));
      thisTask.setDueDate(rs.getTimestamp("duedate"));
      thisTask.setComplete(rs.getBoolean("complete"));
      this.add(thisTask);
    }
    rs.close();
    pst.close();
  }


  public int queryCount(Connection db) throws SQLException {
    PreparedStatement pst = null;
    ResultSet rs = null;
    StringBuffer sqlSelect = new StringBuffer();
    StringBuffer sqlFilter = new StringBuffer();
    createFilter(sqlFilter);
    sqlSelect.append(
        "SELECT count(*) AS record_count " +
            "FROM task t " +
            "WHERE t.task_id > -1 ");
    pst = db.prepareStatement(sqlSelect.toString() + sqlFilter.toString());
    prepareFilter(pst);
    rs = pst.executeQuery();
    rs.next();
    int count = rs.getInt(1);
    rs.close();
    pst.close();
    return count;
  }


  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public void buildList(Connection db) throws SQLException {
    PreparedStatement pst = null;
    ResultSet rs = null;
    int items = -1;
    StringBuffer sqlSelect = new StringBuffer();
    StringBuffer sqlCount = new StringBuffer();
    StringBuffer sqlFilter = new StringBuffer();
    StringBuffer sqlOrder = new StringBuffer();
    //Build a base SQL statement for counting records
    sqlCount.append(
        "SELECT COUNT(*) AS recordcount " +
            "FROM task t " +
            "WHERE t.task_id > -1 ");
    createFilter(sqlFilter);
    if (pagedListInfo != null) {
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
      //Determine the offset, based on the filter, for the first record to show
      if (!pagedListInfo.getCurrentLetter().equals("")) {
        pst = db.prepareStatement(sqlCount.toString() +
            sqlFilter.toString() +
            "AND t.priority > ? ");
        items = prepareFilter(pst);
        pst.setString(++items, pagedListInfo.getCurrentLetter().toLowerCase());
        rs = pst.executeQuery();
        if (rs.next()) {
          int offsetCount = rs.getInt("recordcount");
          pagedListInfo.setCurrentOffset(offsetCount);
        }
        rs.close();
        pst.close();
      }
      //Determine column to sort by
      pagedListInfo.setDefaultSort("t.priority,lower(description)", null);
      pagedListInfo.appendSqlTail(db, sqlOrder);
    } else {
      sqlOrder.append("ORDER BY t.priority, lower(description) ");
    }
    //Build a base SQL statement for returning records
    if (pagedListInfo != null) {
      pagedListInfo.appendSqlSelectHead(db, sqlSelect);
    } else {
      sqlSelect.append("SELECT ");
    }
    sqlSelect.append(
        "t.task_id, t.entered, t.enteredby, t.priority, t.description, " +
            "t.duedate, t.notes, t.sharing, t.complete, t.estimatedloe, " +
            "t.estimatedloetype, t.owner, t.completedate, t.modified, " +
            "t.modifiedby, t.category_id, rating_count, rating_value, rating_avg, " +
            "functional_area, status, business_value, complexity, target_release, target_sprint, loe_remaining, " +
            "assigned_priority, link_module_id, link_item_id " +
            "FROM task t " +
            "WHERE t.task_id > -1 ");
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
      Task thisTask = new Task(rs);
      this.add(thisTask);
    }
    rs.close();
    pst.close();
    Iterator i = this.iterator();
    while (i.hasNext()) {
      Task thisTask = (Task) i.next();
      thisTask.buildResources(db);
      /* if (thisTask.getType() != Task.GENERAL) {
        thisTask.buildLinkDetails(db);
      } */
    }
  }


  /**
   * Description of the Method
   *
   * @param sqlFilter Description of the Parameter
   */
  protected void createFilter(StringBuffer sqlFilter) {
    if (sqlFilter == null) {
      sqlFilter = new StringBuffer();
    }

    if (enteredBy != -1) {
      sqlFilter.append("AND t.enteredby = ? ");
    }

    if (tasksAssignedByUser > 0) {
      sqlFilter.append("AND t.enteredby = ? AND t.owner NOT IN (SELECT user_id FROM contact WHERE user_id = ?) AND t.owner IS NOT NULL ");
    }

    if (owner > -1) {
      if (owner == 0) {
        sqlFilter.append("AND t.owner IS NULL ");
      } else {
        sqlFilter.append("AND t.owner = ? ");
      }
    }

    if (complete != -1) {
      sqlFilter.append("AND t.complete = ? ");
    }

    if (alertRangeStart != null) {
      sqlFilter.append("AND t.duedate >= ? ");
    }

    if (alertRangeEnd != null) {
      sqlFilter.append("AND t.duedate < ? ");
    }

    if (categoryId > 0) {
      sqlFilter.append("AND t.category_id = ? ");
    }

    if (projectId > 0) {
      sqlFilter.append("AND t.task_id IN (SELECT task_id FROM tasklink_project WHERE project_id = ?) ");
    }

    if (ticketId > 0) {
      sqlFilter.append("AND t.task_id IN (SELECT task_id FROM tasklink_ticket WHERE ticket_id = ?) ");
    }
    if (functionalArea > -1) {
      if (functionalArea == 0) {
        sqlFilter.append("AND t.functional_area IS NULL ");
      } else {
        sqlFilter.append("AND t.functional_area = ? ");
      }
    }
    if (status > -1) {
      if (status == 0) {
        sqlFilter.append("AND t.status IS NULL ");
      } else {
        sqlFilter.append("AND t.status = ? ");
      }
    }
    if (businessValue > -1) {
      if (businessValue == 0) {
        sqlFilter.append("AND t.business_value IS NULL ");
      } else {
        sqlFilter.append("AND t.business_value = ? ");
      }
    }
    if (complexity > -1) {
      if (complexity == 0) {
        sqlFilter.append("AND t.complexity IS NULL ");
      } else {
        sqlFilter.append("AND t.complexity = ? ");
      }
    }
    if (targetRelease > -1) {
      if (targetRelease == 0) {
        sqlFilter.append("AND t.target_release IS NULL ");
      } else {
        sqlFilter.append("AND t.target_release = ? ");
      }
    }
    if (targetSprint > -1) {
      if (targetSprint == 0) {
        sqlFilter.append("AND t.target_sprint IS NULL ");
      } else {
        sqlFilter.append("AND t.target_sprint = ? ");
      }
    }
    if (loeRemaining > -1) {
      if (loeRemaining == 0) {
        sqlFilter.append("AND t.loe_remaining IS NULL ");
      } else {
        sqlFilter.append("AND t.loe_remaining = ? ");
      }
    }
    if (assignedPriority > -1) {
      if (assignedPriority == 0) {
        sqlFilter.append("AND t.assigned_priority IS NULL ");
      } else {
        sqlFilter.append("AND t.assigned_priority = ? ");
      }
    }
    if (linkModuleId > -1 && linkItemId > -1) {
      sqlFilter.append("AND t.link_module_id = ? AND t.link_item_id = ? ");
    }
  }


  /**
   * Description of the Method
   *
   * @param pst Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  protected int prepareFilter(PreparedStatement pst) throws SQLException {
    int i = 0;
    if (enteredBy != -1) {
      pst.setInt(++i, enteredBy);
    }
    if (tasksAssignedByUser > 0) {
      pst.setInt(++i, tasksAssignedByUser);
      pst.setInt(++i, tasksAssignedByUser);
    }
    if (owner > 0) {
      pst.setInt(++i, owner);
    }
    if (complete != -1) {
      pst.setBoolean(++i, (complete == Constants.TRUE));
    }
    if (alertRangeStart != null) {
      pst.setTimestamp(++i, alertRangeStart);
    }
    if (alertRangeEnd != null) {
      pst.setTimestamp(++i, alertRangeEnd);
    }
    if (categoryId > 0) {
      pst.setInt(++i, categoryId);
    }
    if (projectId > 0) {
      pst.setInt(++i, projectId);
    }
    if (ticketId > 0) {
      pst.setInt(++i, ticketId);
    }
    if (functionalArea > 0) {
      DatabaseUtils.setInt(pst, ++i, functionalArea, 0);
    }
    if (status > 0) {
      DatabaseUtils.setInt(pst, ++i, status, 0);
    }
    if (businessValue > 0) {
      DatabaseUtils.setInt(pst, ++i, businessValue, 0);
    }
    if (complexity > 0) {
      DatabaseUtils.setInt(pst, ++i, complexity, 0);
    }
    if (targetRelease > 0) {
      DatabaseUtils.setInt(pst, ++i, targetRelease, 0);
    }
    if (targetSprint > 0) {
      DatabaseUtils.setInt(pst, ++i, targetSprint, 0);
    }
    if (loeRemaining > 0) {
      DatabaseUtils.setInt(pst, ++i, loeRemaining, 0);
    }
    if (assignedPriority > 0) {
      DatabaseUtils.setInt(pst, ++i, assignedPriority, 0);
    }
    if (linkModuleId > -1 && linkItemId > -1) {
      pst.setInt(++i, linkModuleId);
      pst.setInt(++i, linkItemId);
    }
    return i;
  }


  /**
   * Description of the Method
   *
   * @param db     Description of the Parameter
   * @param userId Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  public static int queryPendingCount(Connection db, int userId) throws SQLException {
    int toReturn = 0;
    String sql =
        "SELECT count(*) as taskcount " +
            "FROM task " +
            "WHERE owner = ? AND complete = ? ";
    int i = 0;
    PreparedStatement pst = db.prepareStatement(sql);
    pst.setInt(++i, userId);
    pst.setBoolean(++i, false);
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      toReturn = rs.getInt("taskcount");
    }
    rs.close();
    pst.close();
    return toReturn;
  }


  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public void delete(Connection db) throws SQLException {
    Iterator tasks = this.iterator();
    while (tasks.hasNext()) {
      Task thisTask = (Task) tasks.next();
      thisTask.delete(db);
    }
  }

  public void insert(Connection db) throws SQLException {
    Iterator tasks = this.iterator();
    while (tasks.hasNext()) {
      Task thisTask = (Task) tasks.next();
      thisTask.setProjectId(projectId);
      thisTask.setCategoryId(categoryId);
      thisTask.setComplete(false);
      thisTask.setEnteredBy(enteredBy);
      thisTask.setModifiedBy(modifiedBy);
      thisTask.setOwner(owner);
      thisTask.setId(-1);
      thisTask.insert(db);
    }
  }

  public static int updateCategoryId(Connection db, int taskId, int categoryId) throws SQLException {
    PreparedStatement pst = db.prepareStatement("UPDATE task SET category_id = ? WHERE task_id = ? AND category_id <> ? ");
    pst.setInt(1, categoryId);
    pst.setInt(2, taskId);
    pst.setInt(3, categoryId);
    int count = pst.executeUpdate();
    pst.close();
    return count;
  }

  public static boolean isValidTable(String table) {
    return (table.equals(ProjectItemList.LIST_FUNCTIONAL_AREA) ||
        table.equals(ProjectItemList.LIST_STATUS) ||
        table.equals(ProjectItemList.LIST_VALUE) ||
        table.equals(ProjectItemList.LIST_COMPLEXITY) ||
        table.equals(ProjectItemList.LIST_TARGET_RELEASE) ||
        table.equals(ProjectItemList.LIST_TARGET_SPRINT) ||
        table.equals(ProjectItemList.LIST_LOE_REMAINING) ||
        table.equals(ProjectItemList.LIST_ASSIGNED_PRIORITY));
  }

  public static String getPropertyKey(String table) {
    if (table.equals(ProjectItemList.LIST_FUNCTIONAL_AREA)) {
      return "functionalArea";
    }
    if (table.equals(ProjectItemList.LIST_STATUS)) {
      return "status";
    }
    if (table.equals(ProjectItemList.LIST_VALUE)) {
      return "businessValue";
    }
    if (table.equals(ProjectItemList.LIST_COMPLEXITY)) {
      return "complexity";
    }
    if (table.equals(ProjectItemList.LIST_TARGET_RELEASE)) {
      return "targetRelease";
    }
    if (table.equals(ProjectItemList.LIST_TARGET_SPRINT)) {
      return "targetSprint";
    }
    if (table.equals(ProjectItemList.LIST_LOE_REMAINING)) {
      return "loeRemaining";
    }
    if (table.equals(ProjectItemList.LIST_ASSIGNED_PRIORITY)) {
      return "assignedPriority";
    }
    return null;
  }

}
