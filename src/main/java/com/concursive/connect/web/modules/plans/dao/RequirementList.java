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

import com.concursive.commons.date.DateUtils;
import com.concursive.commons.db.DatabaseUtils;
import com.concursive.commons.text.StringUtils;
import com.concursive.connect.web.utils.HtmlSelect;
import com.concursive.connect.web.utils.PagedListInfo;

import java.sql.*;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TimeZone;

/**
 * Description of the Class
 *
 * @author matt rajkowski
 * @version $Id$
 * @created December 23, 2002
 */
public class RequirementList extends ArrayList<Requirement> {

  private PagedListInfo pagedListInfo = null;
  private String emptyHtmlSelectRecord = null;
  private int projectId = -1;
  private boolean buildAssignments = false;
  private boolean openOnly = false;
  private boolean closedOnly = false;
  private int enteredBy = -1;
  private int modifiedBy = -1;
  private AssignmentList assignmentList = null;
  // helpers
  private int planActivityCount = -1;
  private int planClosedCount = -1;
  private int planUpcomingCount = -1;
  private int planOverdueCount = -1;
  // cloning
  private long offset = 0;
  private boolean resetStatus = false;
  //calendar
  protected java.sql.Timestamp alertRangeStart = null;
  protected java.sql.Timestamp alertRangeEnd = null;

  /**
   * Constructor for the RequirementList object
   */
  public RequirementList() {
  }


  /**
   * Sets the pagedListInfo attribute of the RequirementList object
   *
   * @param tmp The new pagedListInfo value
   */
  public void setPagedListInfo(PagedListInfo tmp) {
    this.pagedListInfo = tmp;
  }


  /**
   * Sets the emptyHtmlSelectRecord attribute of the RequirementList object
   *
   * @param tmp The new emptyHtmlSelectRecord value
   */
  public void setEmptyHtmlSelectRecord(String tmp) {
    this.emptyHtmlSelectRecord = tmp;
  }


  /**
   * Sets the projectId attribute of the RequirementList object
   *
   * @param tmp The new projectId value
   */
  public void setProjectId(int tmp) {
    this.projectId = tmp;
  }


  /**
   * Sets the buildAssignments attribute of the RequirementList object
   *
   * @param tmp The new buildAssignments value
   */
  public void setBuildAssignments(boolean tmp) {
    this.buildAssignments = tmp;
  }


  /**
   * Sets the enteredBy attribute of the RequirementList object
   *
   * @param tmp The new enteredBy value
   */
  public void setEnteredBy(int tmp) {
    this.enteredBy = tmp;
  }


  /**
   * Sets the modifiedBy attribute of the RequirementList object
   *
   * @param tmp The new modifiedBy value
   */
  public void setModifiedBy(int tmp) {
    this.modifiedBy = tmp;
  }


  /**
   * Sets the openOnly attribute of the RequirementList object
   *
   * @param tmp The new openOnly value
   */
  public void setOpenOnly(boolean tmp) {
    this.openOnly = tmp;
  }


  /**
   * Sets the closedOnly attribute of the RequirementList object
   *
   * @param tmp The new closedOnly value
   */
  public void setClosedOnly(boolean tmp) {
    this.closedOnly = tmp;
  }

  public void setAssignmentList(AssignmentList assignmentList) {
    this.assignmentList = assignmentList;
  }

  /**
   * Gets the htmlSelect attribute of the RequirementList object
   *
   * @param selectName Description of the Parameter
   * @return The htmlSelect value
   */
  public String getHtmlSelect(String selectName) {
    return getHtmlSelect(selectName, -1);
  }


  /**
   * Gets the planActivityCount attribute of the RequirementList object
   *
   * @return The planActivityCount value
   */
  public int getPlanActivityCount() {
    return planActivityCount;
  }


  /**
   * Sets the planActivityCount attribute of the RequirementList object
   *
   * @param planActivityCount The new planActivityCount value
   */
  public void setPlanActivityCount(int planActivityCount) {
    this.planActivityCount = planActivityCount;
  }


  /**
   * Gets the planClosedCount attribute of the RequirementList object
   *
   * @return The planClosedCount value
   */
  public int getPlanClosedCount() {
    return planClosedCount;
  }


  /**
   * Sets the planClosedCount attribute of the RequirementList object
   *
   * @param planClosedCount The new planClosedCount value
   */
  public void setPlanClosedCount(int planClosedCount) {
    this.planClosedCount = planClosedCount;
  }


  /**
   * Gets the planUpcomingCount attribute of the RequirementList object
   *
   * @return The planUpcomingCount value
   */
  public int getPlanUpcomingCount() {
    return planUpcomingCount;
  }


  /**
   * Sets the planUpcomingCount attribute of the RequirementList object
   *
   * @param planUpcomingCount The new planUpcomingCount value
   */
  public void setPlanUpcomingCount(int planUpcomingCount) {
    this.planUpcomingCount = planUpcomingCount;
  }


  /**
   * Gets the planOverdueCount attribute of the RequirementList object
   *
   * @return The planOverdueCount value
   */
  public int getPlanOverdueCount() {
    return planOverdueCount;
  }


  /**
   * Sets the planOverdueCount attribute of the RequirementList object
   *
   * @param planOverdueCount The new planOverdueCount value
   */
  public void setPlanOverdueCount(int planOverdueCount) {
    this.planOverdueCount = planOverdueCount;
  }

  public long getOffset() {
    return offset;
  }

  public void setOffset(long offset) {
    this.offset = offset;
  }

  public boolean getResetStatus() {
    return resetStatus;
  }

  public void setResetStatus(boolean resetStatus) {
    this.resetStatus = resetStatus;
  }

  public Timestamp getAlertRangeStart() {
    return alertRangeStart;
  }

  public void setAlertRangeStart(Timestamp alertRangeStart) {
    this.alertRangeStart = alertRangeStart;
  }

  public Timestamp getAlertRangeEnd() {
    return alertRangeEnd;
  }

  public void setAlertRangeEnd(Timestamp alertRangeEnd) {
    this.alertRangeEnd = alertRangeEnd;
  }

  /**
   * Gets the htmlSelect attribute of the RequirementList object
   *
   * @param selectName Description of the Parameter
   * @param defaultKey Description of the Parameter
   * @return The htmlSelect value
   */
  public String getHtmlSelect(String selectName, int defaultKey) {
    HtmlSelect listSelect = new HtmlSelect();
    if (emptyHtmlSelectRecord != null) {
      listSelect.addItem(-1, emptyHtmlSelectRecord);
    }
    for (Requirement thisRequirement : this) {
      listSelect.addItem(
          thisRequirement.getId(),
          StringUtils.toHtml(thisRequirement.getShortDescription()));
    }
    return listSelect.getHtml(selectName, defaultKey);
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

    //Need to build a base SQL statement for counting records
    sqlCount.append(
        "SELECT COUNT(*) AS recordcount " +
            "FROM project_requirements r " +
            "WHERE r.requirement_id > -1 ");

    createFilter(sqlFilter);

    if (pagedListInfo == null) {
      pagedListInfo = new PagedListInfo();
      pagedListInfo.setItemsPerPage(0);
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

    //Determine the offset, based on the filter, for the first record to show
    if (!pagedListInfo.getCurrentLetter().equals("")) {
      pst = db.prepareStatement(sqlCount.toString() +
          sqlFilter.toString() +
          "AND lower(shortDescription) < ? ");
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
    pagedListInfo.setDefaultSort("startdate,r.shortdescription", null);
    pagedListInfo.appendSqlTail(db, sqlOrder);

    //Need to build a base SQL statement for returning records
    pagedListInfo.appendSqlSelectHead(db, sqlSelect);
    sqlSelect.append(
        "r.*, loe_e.description as loe_estimated_type, loe_a.description as loe_actual_type " +
            "FROM project_requirements r " +
            " LEFT JOIN lookup_project_loe loe_e ON (r.estimated_loetype = loe_e.code) " +
            " LEFT JOIN lookup_project_loe loe_a ON (r.actual_loetype = loe_a.code) " +
            "WHERE r.requirement_id > -1 ");

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
      Requirement thisRequirement = new Requirement(rs);
      this.add(thisRequirement);
    }
    rs.close();
    pst.close();

    if (buildAssignments) {
      for (Requirement thisRequirement : this) {
        thisRequirement.buildAssignmentList(db);
      }
    }
  }


  /**
   * Description of the Method
   *
   * @param sqlFilter Description of the Parameter
   */
  private void createFilter(StringBuffer sqlFilter) {
    if (sqlFilter == null) {
      sqlFilter = new StringBuffer();
    }
    if (projectId > -1) {
      sqlFilter.append("AND project_id = ? ");
    }
    if (openOnly) {
      sqlFilter.append("AND closedate IS NULL ");
    }
    if (closedOnly) {
      sqlFilter.append("AND closedate IS NOT NULL ");
    }
    if (assignmentList != null && assignmentList.size() > 0) {
      sqlFilter.append("AND r.requirement_id IN (SELECT requirement_id FROM project_assignments WHERE assignment_id IN (" + assignmentList.getIdsAsCSV() + ")) ");
    }
    if (alertRangeStart != null) {
      sqlFilter.append("AND (r.startdate >= ? OR r.deadline >= ?) ");
    }
    if (alertRangeEnd != null) {
      sqlFilter.append("AND (r.startdate < ? OR r.deadline < ?) ");
    }
  }


  /**
   * Description of the Method
   *
   * @param pst Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  private int prepareFilter(PreparedStatement pst) throws SQLException {
    int i = 0;
    if (projectId > -1) {
      pst.setInt(++i, projectId);
    }
    if (alertRangeStart != null) {
      pst.setTimestamp(++i, alertRangeStart);
      pst.setTimestamp(++i, alertRangeStart);
    }
    if (alertRangeEnd != null) {
      pst.setTimestamp(++i, alertRangeEnd);
      pst.setTimestamp(++i, alertRangeEnd);
    }
    return i;
  }


  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  public boolean insert(Connection db, int fromProjectId) throws SQLException {
    for (Requirement thisRequirement : this) {
      thisRequirement.setProjectId(projectId);
      thisRequirement.setEnteredBy(enteredBy);
      thisRequirement.setModifiedBy(modifiedBy);
      //thisRequirement.setApproved(false);
      thisRequirement.setOffset(offset);
      thisRequirement.clone(db, fromProjectId, offset, resetStatus);
    }
    return true;
  }


  /**
   * Description of the Method
   *
   * @param db       Database connection
   * @param filePath Location of stored documents
   * @throws SQLException Description of the Exception
   */
  public void delete(Connection db, String filePath) throws SQLException {
    for (Requirement thisRequirement : this) {
      thisRequirement.delete(db, filePath);
    }
  }


  /**
   * Gets the percentClosed attribute of the RequirementList object
   *
   * @return The percentClosed value
   */
  public int getPercentClosed() {
    if (planActivityCount == 0 || planClosedCount == planActivityCount) {
      return 100;
    }
    return (int) Math.round(((double) planClosedCount / (double) planActivityCount) * 100.0);
  }


  /**
   * Gets the percentUpcoming attribute of the RequirementList object
   *
   * @return The percentUpcoming value
   */
  public int getPercentUpcoming() {
    if (planActivityCount == 0 || planUpcomingCount == 0) {
      return 0;
    }
    return (int) Math.round(((double) planUpcomingCount / (double) planActivityCount) * 100.0);
  }


  /**
   * Gets the percentOverdue attribute of the RequirementList object
   *
   * @return The percentOverdue value
   */
  public int getPercentOverdue() {
    if (planActivityCount == 0 || planOverdueCount == 0) {
      return 0;
    }
    return (int) Math.round(((double) planOverdueCount / (double) planActivityCount) * 100.0);
  }


  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public void buildPlanActivityCounts(Connection db) throws SQLException {
    planActivityCount = 0;
    planClosedCount = 0;
    planUpcomingCount = 0;
    planOverdueCount = 0;
    for (Requirement thisRequirement : this) {
      thisRequirement.buildPlanActivityCounts(db);
      planActivityCount += thisRequirement.getPlanActivityCount();
      planClosedCount += thisRequirement.getPlanClosedCount();
      planUpcomingCount += thisRequirement.getPlanUpcomingCount();
      planOverdueCount += thisRequirement.getPlanOverdueCount();
    }
  }

  public String getRequirement(int requirementId) {
    for (Requirement thisRequirement : this) {
      if (thisRequirement.getId() == requirementId) {
        return thisRequirement.getShortDescription();
      }
    }
    return null;
  }

  /**
   * Gets Requirements counts on
   *
   * @param db
   * @param timeZone
   * @return requirements count
   * @throws SQLException
   */
  public HashMap<String, HashMap<String, Integer>> queryRecordCount(Connection db, TimeZone timeZone) throws SQLException {

    PreparedStatement pst = null;
    ResultSet rs = null;
    HashMap<String, HashMap<String, Integer>> events = new HashMap<String, HashMap<String, Integer>>();
    HashMap<String, Integer> startEvents = new HashMap<String, Integer>();
    HashMap<String, Integer> endEvents = new HashMap<String, Integer>();

    StringBuffer sqlstrDate = new StringBuffer();
    StringBuffer sqlFilter = new StringBuffer();
    StringBuffer sqlendDate = new StringBuffer();
    StringBuffer sqlstrTail = new StringBuffer();
    StringBuffer sqlendTail = new StringBuffer();
    sqlstrDate.append(
        "SELECT " +
            DatabaseUtils.castDateTimeToDate(db, "r.startdate") + " AS group_date, " +
            "COUNT(*) AS recordcount " +
            "FROM project_requirements r " +
            "WHERE r.requirement_id > -1 " +
            "AND r.startdate IS NOT NULL ");
    sqlstrTail.append("GROUP BY group_date ");
    createFilter(sqlFilter);
    pst = db.prepareStatement(sqlstrDate.toString() + sqlFilter.toString() + sqlstrTail.toString());
    prepareFilter(pst);
    rs = pst.executeQuery();
    while (rs.next()) {
      String alertDate = DateUtils.getServerToUserDateString(timeZone, DateFormat.SHORT, rs.getTimestamp("group_date"));
      int alertCount = rs.getInt("recordcount");
      startEvents.put(alertDate, new Integer(alertCount));
    }
    rs.close();
    pst.close();

    sqlendDate.append(
        "SELECT " +
            DatabaseUtils.castDateTimeToDate(db, "r.deadline") + " AS group_date, " +
            "COUNT(*) AS recordcount " +
            "FROM project_requirements r " +
            "WHERE r.requirement_id > -1 " +
            "AND r.deadline IS NOT NULL ");
    sqlendTail.append("GROUP BY group_date ");
    //createFilter(sqlFilter);
    pst = db.prepareStatement(sqlendDate.toString() + sqlFilter.toString() + sqlendTail.toString());
    prepareFilter(pst);
    rs = pst.executeQuery();
    while (rs.next()) {
      String alertDate = DateUtils.getServerToUserDateString(timeZone, DateFormat.SHORT, rs.getTimestamp("group_date"));
      int alertCount = rs.getInt("recordcount");
      endEvents.put(alertDate, new Integer(alertCount));
    }
    rs.close();
    pst.close();
    events.put("startdate", startEvents);
    events.put("enddate", endEvents);
    return events;
  }

}

