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

package com.concursive.connect.web.modules.issues.dao;

import com.concursive.commons.date.DateUtils;
import com.concursive.commons.db.DatabaseUtils;
import com.concursive.connect.Constants;
import com.concursive.connect.web.utils.PagedListInfo;

import java.sql.*;
import java.text.DateFormat;
import java.util.*;

/**
 * A collection of Ticket objects, can also be used for querying and filtering
 * the tickets that are included in the list.
 *
 * @author chris
 * @version $Id$
 * @created December 5, 2001
 */
public class TicketList extends ArrayList<Ticket> {
  //sync
  public final static String tableName = "ticket";
  public final static String uniqueField = "ticketid";
  private java.sql.Timestamp lastAnchor = null;
  private java.sql.Timestamp nextAnchor = null;
  //filters
  private PagedListInfo pagedListInfo = null;
  private int enteredBy = -1;
  private boolean onlyOpen = false;
  private boolean onlyClosed = false;
  private int id = -1;
  private int orgId = -1;
  private int department = -1;
  private int assignedTo = -1;
  private int excludeAssignedTo = -1;
  private boolean onlyAssigned = false;
  private boolean onlyUnassigned = false;
  private boolean unassignedToo = false;
  private int severity = 0;
  private int priority = 0;
  private String accountOwnerIdRange = null;
  private String description = null;
  private int minutesOlderThan = -1;
  private int projectId = -1;
  private int linkProjectId = -1;
  private int forProjectUser = -1;
  private boolean onlyIfProjectOpen = false;
  private int ownTickets = -1;
  private int forReview = Constants.UNDEFINED;
  //search filters
  private String searchText = "";
  private int catCode = -1;
  //calendar
  protected java.sql.Timestamp alertRangeStart = null;
  protected java.sql.Timestamp alertRangeEnd = null;


  /**
   * Constructor for the TicketList object
   */
  public TicketList() {
  }

  public int getLinkProjectId() {
    return linkProjectId;
  }

  public void setLinkProjectId(int linkProjectId) {
    this.linkProjectId = linkProjectId;
  }

  public void setLinkProjectId(String tmp) {
    this.linkProjectId = Integer.parseInt(tmp);  
  }

  /**
   * Sets the lastAnchor attribute of the TicketList object
   *
   * @param tmp The new lastAnchor value
   */
  public void setLastAnchor(java.sql.Timestamp tmp) {
    this.lastAnchor = tmp;
  }


  /**
   * Sets the lastAnchor attribute of the TicketList object
   *
   * @param tmp The new lastAnchor value
   */
  public void setLastAnchor(String tmp) {
    try {
      this.lastAnchor = java.sql.Timestamp.valueOf(tmp);
    } catch (Exception e) {
      this.lastAnchor = null;
    }
  }


  /**
   * Sets the nextAnchor attribute of the TicketList object
   *
   * @param tmp The new nextAnchor value
   */
  public void setNextAnchor(java.sql.Timestamp tmp) {
    this.nextAnchor = tmp;
  }


  /**
   * Sets the nextAnchor attribute of the TicketList object
   *
   * @param tmp The new nextAnchor value
   */
  public void setNextAnchor(String tmp) {
    try {
      this.nextAnchor = java.sql.Timestamp.valueOf(tmp);
    } catch (Exception e) {
      this.nextAnchor = null;
    }
  }


  /**
   * Sets the Id attribute of the TicketList object
   *
   * @param id The new Id value
   */
  public void setId(int id) {
    this.id = id;
  }


  /**
   * Sets the Id attribute of the TicketList object
   *
   * @param id The new Id value
   */
  public void setId(String id) {
    this.id = Integer.parseInt(id);
  }


  /**
   * Sets the assignedTo attribute of the TicketList object
   *
   * @param assignedTo The new assignedTo value
   */
  public void setAssignedTo(int assignedTo) {
    this.assignedTo = assignedTo;
  }


  /**
   * Sets the assignedTo attribute of the TicketList object
   *
   * @param assignedTo The new assignedTo value
   */
  public void setAssignedTo(String assignedTo) {
    this.assignedTo = Integer.parseInt(assignedTo);
  }


  /**
   * Sets the excludeAssignedTo attribute of the TicketList object
   *
   * @param tmp The new excludeAssignedTo value
   */
  public void setExcludeAssignedTo(int tmp) {
    this.excludeAssignedTo = tmp;
  }


  /**
   * Sets the excludeAssignedTo attribute of the TicketList object
   *
   * @param tmp The new excludeAssignedTo value
   */
  public void setExcludeAssignedTo(String tmp) {
    this.excludeAssignedTo = Integer.parseInt(tmp);
  }


  /**
   * Sets the onlyAssigned attribute of the TicketList object
   *
   * @param tmp The new onlyAssigned value
   */
  public void setOnlyAssigned(boolean tmp) {
    this.onlyAssigned = tmp;
  }


  /**
   * Sets the onlyAssigned attribute of the TicketList object
   *
   * @param tmp The new onlyAssigned value
   */
  public void setOnlyAssigned(String tmp) {
    this.onlyAssigned = DatabaseUtils.parseBoolean(tmp);
  }


  /**
   * Sets the onlyUnassigned attribute of the TicketList object
   *
   * @param tmp The new onlyUnassigned value
   */
  public void setOnlyUnassigned(boolean tmp) {
    this.onlyUnassigned = tmp;
  }


  /**
   * Sets the onlyUnassigned attribute of the TicketList object
   *
   * @param tmp The new onlyUnassigned value
   */
  public void setOnlyUnassigned(String tmp) {
    this.onlyUnassigned = DatabaseUtils.parseBoolean(tmp);
  }


  /**
   * Sets the unassignedToo attribute of the TicketList object
   *
   * @param unassignedToo The new unassignedToo value
   */
  public void setUnassignedToo(boolean unassignedToo) {
    this.unassignedToo = unassignedToo;
  }


  /**
   * Sets the severity attribute of the TicketList object
   *
   * @param tmp The new severity value
   */
  public void setSeverity(int tmp) {
    this.severity = tmp;
  }


  /**
   * Sets the priority attribute of the TicketList object
   *
   * @param tmp The new priority value
   */
  public void setPriority(int tmp) {
    this.priority = tmp;
  }


  /**
   * Sets the severity attribute of the TicketList object
   *
   * @param tmp The new severity value
   */
  public void setSeverity(String tmp) {
    this.severity = Integer.parseInt(tmp);
  }


  /**
   * Sets the projectId attribute of the TicketList object
   *
   * @param tmp The new projectId value
   */
  public void setProjectId(int tmp) {
    this.projectId = tmp;
  }


  /**
   * Sets the forProjectUser attribute of the TicketList object
   *
   * @param tmp The new forProjectUser value
   */
  public void setForProjectUser(int tmp) {
    this.forProjectUser = tmp;
  }


  /**
   * Sets the forProjectUser attribute of the TicketList object
   *
   * @param tmp The new forProjectUser value
   */
  public void setForProjectUser(String tmp) {
    this.forProjectUser = Integer.parseInt(tmp);
  }

  public boolean getOnlyIfProjectOpen() {
    return onlyIfProjectOpen;
  }

  public void setOnlyIfProjectOpen(boolean onlyIfProjectOpen) {
    this.onlyIfProjectOpen = onlyIfProjectOpen;
  }

  public int getOwnTickets() {
    return ownTickets;
  }

  public void setOwnTickets(int ownTickets) {
    this.ownTickets = ownTickets;
  }

  /**
   * Gets the tableName attribute of the TicketList object
   *
   * @return The tableName value
   */
  public String getTableName() {
    return tableName;
  }


  /**
   * Gets the uniqueField attribute of the TicketList object
   *
   * @return The uniqueField value
   */
  public String getUniqueField() {
    return uniqueField;
  }


  /**
   * Sets the priority attribute of the TicketList object
   *
   * @param tmp The new priority value
   */
  public void setPriority(String tmp) {
    this.priority = Integer.parseInt(tmp);
  }


  /**
   * Sets the searchText attribute of the TicketList object
   *
   * @param searchText The new searchText value
   */
  public void setSearchText(String searchText) {
    this.searchText = searchText;
  }

  public int getCatCode() {
    return catCode;
  }

  public void setCatCode(int catCode) {
    this.catCode = catCode;
  }

  /**
   * Sets the accountOwnerIdRange attribute of the TicketList object
   *
   * @param accountOwnerIdRange The new accountOwnerIdRange value
   */
  public void setAccountOwnerIdRange(String accountOwnerIdRange) {
    this.accountOwnerIdRange = accountOwnerIdRange;
  }


  /**
   * Sets the OrgId attribute of the TicketList object
   *
   * @param orgId The new OrgId value
   */
  public void setOrgId(int orgId) {
    this.orgId = orgId;
  }


  /**
   * Sets the OrgId attribute of the TicketList object
   *
   * @param orgId The new OrgId value
   */
  public void setOrgId(String orgId) {
    this.orgId = Integer.parseInt(orgId);
  }


  /**
   * Sets the PagedListInfo attribute of the TicketList object
   *
   * @param tmp The new PagedListInfo value
   */
  public void setPagedListInfo(PagedListInfo tmp) {
    this.pagedListInfo = tmp;
  }


  /**
   * Sets the EnteredBy attribute of the TicketList object
   *
   * @param tmp The new EnteredBy value
   */
  public void setEnteredBy(int tmp) {
    this.enteredBy = tmp;
  }


  /**
   * Gets the description attribute of the TicketList object
   *
   * @return The description value
   */
  public String getDescription() {
    return description;
  }


  /**
   * Sets the description attribute of the TicketList object
   *
   * @param description The new description value
   */
  public void setDescription(String description) {
    this.description = description;
  }


  /**
   * Sets the minutesOlderThan attribute of the TicketList object
   *
   * @param tmp The new minutesOlderThan value
   */
  public void setMinutesOlderThan(int tmp) {
    this.minutesOlderThan = tmp;
  }


  /**
   * Sets the minutesOlderThan attribute of the TicketList object
   *
   * @param tmp The new minutesOlderThan value
   */
  public void setMinutesOlderThan(String tmp) {
    this.minutesOlderThan = Integer.parseInt(tmp);
  }


  /**
   * Sets the onlyClosed attribute of the TicketList object
   *
   * @param onlyClosed The new onlyClosed value
   */
  public void setOnlyClosed(boolean onlyClosed) {
    this.onlyClosed = onlyClosed;
  }


  /**
   * Sets the OnlyOpen attribute of the TicketList object
   *
   * @param onlyOpen The new OnlyOpen value
   */
  public void setOnlyOpen(boolean onlyOpen) {
    this.onlyOpen = onlyOpen;
  }


  public void setForReview(int forReview) {
    this.forReview = forReview;
  }

  /**
   * Sets the Department attribute of the TicketList object
   *
   * @param department The new Department value
   */
  public void setDepartment(int department) {
    this.department = department;
  }


  /**
   * Sets the alertRangeStart attribute of the TicketList object
   *
   * @param tmp The new alertRangeStart value
   */
  public void setAlertRangeStart(java.sql.Timestamp tmp) {
    this.alertRangeStart = tmp;
  }


  /**
   * Sets the alertRangeStart attribute of the TicketList object
   *
   * @param tmp The new alertRangeStart value
   */
  public void setAlertRangeStart(String tmp) {
    this.alertRangeStart = DatabaseUtils.parseTimestamp(tmp);
  }


  /**
   * Sets the alertRangeEnd attribute of the TicketList object
   *
   * @param tmp The new alertRangeEnd value
   */
  public void setAlertRangeEnd(java.sql.Timestamp tmp) {
    this.alertRangeEnd = tmp;
  }


  /**
   * Sets the alertRangeEnd attribute of the TicketList object
   *
   * @param tmp The new alertRangeEnd value
   */
  public void setAlertRangeEnd(String tmp) {
    this.alertRangeEnd = DatabaseUtils.parseTimestamp(tmp);
  }


  /**
   * Gets the assignedTo attribute of the TicketList object
   *
   * @return The assignedTo value
   */
  public int getAssignedTo() {
    return assignedTo;
  }


  /**
   * Gets the excludeAssignedTo attribute of the TicketList object
   *
   * @return The excludeAssignedTo value
   */
  public int getExcludeAssignedTo() {
    return excludeAssignedTo;
  }


  /**
   * Gets the onlyAssigned attribute of the TicketList object
   *
   * @return The onlyAssigned value
   */
  public boolean getOnlyAssigned() {
    return onlyAssigned;
  }


  /**
   * Gets the onlyUnassigned attribute of the TicketList object
   *
   * @return The onlyUnassigned value
   */
  public boolean getOnlyUnassigned() {
    return onlyUnassigned;
  }


  /**
   * Gets the unassignedToo attribute of the TicketList object
   *
   * @return The unassignedToo value
   */
  public boolean getUnassignedToo() {
    return unassignedToo;
  }


  /**
   * Gets the severity attribute of the TicketList object
   *
   * @return The severity value
   */
  public int getSeverity() {
    return severity;
  }


  /**
   * Gets the priority attribute of the TicketList object
   *
   * @return The priority value
   */
  public int getPriority() {
    return priority;
  }


  /**
   * Gets the searchText attribute of the TicketList object
   *
   * @return The searchText value
   */
  public String getSearchText() {
    return searchText;
  }


  /**
   * Gets the accountOwnerIdRange attribute of the TicketList object
   *
   * @return The accountOwnerIdRange value
   */
  public String getAccountOwnerIdRange() {
    return accountOwnerIdRange;
  }


  /**
   * Gets the onlyClosed attribute of the TicketList object
   *
   * @return The onlyClosed value
   */
  public boolean getOnlyClosed() {
    return onlyClosed;
  }


  /**
   * Gets the OrgId attribute of the TicketList object
   *
   * @return The OrgId value
   */
  public int getOrgId() {
    return orgId;
  }


  /**
   * Gets the Id attribute of the TicketList object
   *
   * @return The Id value
   */
  public int getId() {
    return id;
  }


  /**
   * Gets the OnlyOpen attribute of the TicketList object
   *
   * @return The OnlyOpen value
   */
  public boolean getOnlyOpen() {
    return onlyOpen;
  }


  public int getForReview() {
    return forReview;
  }

  /**
   * Gets the Department attribute of the TicketList object
   *
   * @return The Department value
   */
  public int getDepartment() {
    return department;
  }


  /**
   * Gets the pagedListInfo attribute of the TicketList object
   *
   * @return The pagedListInfo value
   */
  public PagedListInfo getPagedListInfo() {
    return pagedListInfo;
  }


  /**
   * Gets the alertRangeStart attribute of the TicketList object
   *
   * @return The alertRangeStart value
   */
  public java.sql.Timestamp getAlertRangeStart() {
    return alertRangeStart;
  }


  /**
   * Gets the alertRangeEnd attribute of the TicketList object
   *
   * @return The alertRangeEnd value
   */
  public java.sql.Timestamp getAlertRangeEnd() {
    return alertRangeEnd;
  }


  /**
   * Gets the forProjectUser attribute of the TicketList object
   *
   * @return The forProjectUser value
   */
  public int getForProjectUser() {
    return forProjectUser;
  }


  public int queryCount(Connection db) throws SQLException {
    int count = 0;
    StringBuffer sqlCount = new StringBuffer();
    StringBuffer sqlFilter = new StringBuffer();
    sqlCount.append(
        "SELECT COUNT(*) AS recordcount " +
            "FROM ticket t " +
            "WHERE t.ticketid > 0 ");
    createFilter(sqlFilter, db);
    PreparedStatement pst = db.prepareStatement(sqlCount.toString() +
        sqlFilter.toString());
    prepareFilter(pst);
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      count += rs.getInt("recordcount");
    }
    rs.close();
    pst.close();
    return count;
  }

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
            "FROM ticket t " +
            "WHERE t.ticketid > 0 ");
    createFilter(sqlFilter, db);
    if (pagedListInfo != null) {
      //Get the total number of records matching filter
      pst = db.prepareStatement(sqlCount.toString() +
          sqlFilter.toString());
      items = prepareFilter(pst);
      rs = pst.executeQuery();
      if (rs.next()) {
        int maxRecords = rs.getInt("recordcount");
        pagedListInfo.setMaxRecords(maxRecords);
      }
      rs.close();
      pst.close();
      // Declare default sort, if unset
      pagedListInfo.setDefaultSort("t.entered", null);
      //Determine the offset, based on the filter, for the first record to show
      if (pagedListInfo.getMode() == PagedListInfo.DETAILS_VIEW && id > 0) {
        String direction = null;
        if ("desc".equalsIgnoreCase(pagedListInfo.getSortOrder())) {
          direction = ">";
        } else {
          direction = "<";
        }
        String sqlSubCount = "AND " + pagedListInfo.getColumnToSortBy() + " " + direction + " (SELECT " + pagedListInfo.getColumnToSortBy() + " FROM ticket t WHERE ticketid = ?) ";
        pst = db.prepareStatement(sqlCount.toString() +
            sqlFilter.toString() +
            sqlSubCount);
        items = prepareFilter(pst);
        pst.setInt(++items, id);
        rs = pst.executeQuery();
        if (rs.next()) {
          int offsetCount = rs.getInt("recordcount");
          pagedListInfo.setCurrentOffset(offsetCount);
        }
        rs.close();
        pst.close();
      }
      //Determine the offset
      pagedListInfo.appendSqlTail(db, sqlOrder);
    } else {
      sqlOrder.append("ORDER BY t.entered ");
    }

    //Need to build a base SQL statement for returning records
    if (pagedListInfo != null) {
      pagedListInfo.appendSqlSelectHead(db, sqlSelect);
    } else {
      sqlSelect.append("SELECT ");
    }
    sqlSelect.append(
        "t.*, " +
            "tp.description AS ticpri, " +
            "ts.description AS ticsev, " +
            "tc.description AS catname, " +
            "tc1.description AS subcatname1, " +
            "tc2.description AS subcatname2, " +
            "tc3.description AS subcatname3, " +
            "lu_ts.description AS sourcename, " +
            "tlp.project_id " +
            "FROM ticket t " +
            "LEFT JOIN ticket_priority tp ON (t.pri_code = tp.code) " +
            "LEFT JOIN ticket_severity ts ON (t.scode = ts.code) " +
            "LEFT JOIN ticket_category tc ON (t.cat_code = tc.id) " +
            "LEFT JOIN ticket_category tc1 ON (t.subcat_code1 = tc1.id) " +
            "LEFT JOIN ticket_category tc2 ON (t.subcat_code2 = tc2.id) " +
            "LEFT JOIN ticket_category tc3 ON (t.subcat_code3 = tc3.id) " +
            "LEFT JOIN lookup_ticketsource lu_ts ON (t.source_code = lu_ts.code) " +
            "LEFT JOIN ticketlink_project tlp ON (t.ticketid = tlp.ticket_id) " +
            "WHERE t.ticketid > 0 ");
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
      Ticket thisTicket = new Ticket(rs);
      this.add(thisTicket);
    }
    rs.close();
    pst.close();
    //Build resources
    Iterator i = this.iterator();
    while (i.hasNext()) {
      Ticket thisTicket = (Ticket) i.next();
      thisTicket.buildFiles(db);
      if (thisTicket.getAssignedTo() > -1) {
        thisTicket.checkEnabledOwnerAccount(db);
      }
    }
  }


  /**
   * Description of the Method
   *
   * @param db Description of Parameter
   * @throws SQLException Description of Exception
   */
  public void delete(Connection db, String baseFilePath) throws SQLException {
    Iterator tickets = this.iterator();
    while (tickets.hasNext()) {
      Ticket thisTicket = (Ticket) tickets.next();
      thisTicket.delete(db, baseFilePath);
    }
  }


  /**
   * Description of the Method
   *
   * @param db       Description of the Parameter
   * @param newOwner Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  public int reassignElements(Connection db, int newOwner) throws SQLException {
    int total = 0;
    for (Ticket thisTicket : this) {
      if (thisTicket.reassign(db, newOwner)) {
        total++;
      }
    }
    return total;
  }


  /**
   * Builds a base SQL where statement for filtering records to be used by
   * sqlSelect and sqlCount
   *
   * @param sqlFilter Description of Parameter
   * @param db        Description of the Parameter
   * @since 1.2
   */
  private void createFilter(StringBuffer sqlFilter, Connection db) {
    if (enteredBy > -1) {
      sqlFilter.append("AND t.enteredby = ? ");
    }
    if (description != null) {
      if (description.indexOf("%") >= 0) {
        if (DatabaseUtils.getType(db) == DatabaseUtils.MSSQL) {
          sqlFilter.append(
              "AND ( LOWER(CONVERT(VARCHAR(2000),t.problem)) LIKE LOWER(?)) ");
        } else {
          sqlFilter.append("AND lower(t.problem) like lower(?) ");
        }
      } else {
        if (DatabaseUtils.getType(db) == DatabaseUtils.MSSQL) {
          sqlFilter.append(
              "AND ( LOWER(CONVERT(VARCHAR(2000),t.problem)) = LOWER(?)) ");
        } else {
          sqlFilter.append("AND lower(t.problem) = lower(?) ");
        }
      }
    }
    if (onlyOpen) {
      sqlFilter.append("AND t.closed IS NULL ");
    }
    if (forReview == Constants.TRUE) {
      sqlFilter.append("AND t.ready_for_close = ? ");
    }
    if (onlyClosed) {
      sqlFilter.append("AND t.closed IS NOT NULL ");
    }
    if (orgId > -1) {
      sqlFilter.append("AND t.org_id = ? ");
    }
    if (department > -1) {
      if (unassignedToo) {
        sqlFilter.append("AND (t.department_code in (?, 0, -1) OR (t.department_code IS NULL)) ");
      } else {
        sqlFilter.append("AND t.department_code = ? ");
      }
    }
    if (assignedTo > -1) {
      sqlFilter.append("AND t.assigned_to = ? ");
    }
    if (excludeAssignedTo > -1) {
      sqlFilter.append("AND (t.assigned_to <> ? OR t.assigned_to IS NULL) ");
    }
    if (onlyAssigned) {
      sqlFilter.append("AND t.assigned_to > 0 AND t.assigned_to IS NOT NULL ");
    }
    if (onlyUnassigned) {
      sqlFilter.append("AND (t.assigned_to IS NULL OR t.assigned_to = 0 OR t.assigned_to = -1) ");
    }
    if (severity > 0) {
      sqlFilter.append("AND t.scode = ? ");
    }
    if (priority > 0) {
      sqlFilter.append("AND t.pri_code = ? ");
    }
    if (accountOwnerIdRange != null) {
      sqlFilter.append("AND t.org_id IN (SELECT org_id FROM organization WHERE owner IN (" + accountOwnerIdRange + ")) ");
    }
    if (projectId > 0) {
      sqlFilter.append("AND t.ticketid IN (SELECT ticket_id FROM ticketlink_project WHERE project_id = ?) ");
    }
    if (linkProjectId > 0) {
      sqlFilter.append("AND t.link_project_id = ? ");
    }
    if (forProjectUser > -1) {
      sqlFilter.append("AND t.ticketid IN (SELECT ticket_id FROM ticketlink_project WHERE project_id in (SELECT DISTINCT project_id FROM project_team WHERE user_id = ? " +
          "AND status IS NULL " +
          (onlyIfProjectOpen ? "AND project_id IN (SELECT project_id FROM projects WHERE closedate IS NULL) " : "") + ")) ");
    }
    if (ownTickets != -1) {
      sqlFilter.append("AND (t.enteredby = ? OR t.assigned_to = ?) ");
    }
    if (catCode != -1) {
      sqlFilter.append("AND t.cat_code = ? ");
    }
    if (alertRangeStart != null) {
      sqlFilter.append("AND t.entered >= ? ");
    }
    if (alertRangeEnd != null) {
      sqlFilter.append("AND t.entered < ? ");
    }
    //No sync, but still need to factor in age
    if (minutesOlderThan > 0) {
      sqlFilter.append("AND t.entered <= ? ");
    }
    if (searchText != null && !(searchText.equals(""))) {
      if (DatabaseUtils.getType(db) == DatabaseUtils.MSSQL) {
        sqlFilter.append(
            "AND ( LOWER(CONVERT(VARCHAR(2000),t.problem)) LIKE LOWER(?) OR " +
                "LOWER(CONVERT(VARCHAR(2000),t.comment)) LIKE LOWER(?) OR " +
                "LOWER(CONVERT(VARCHAR(2000),t.solution)) LIKE LOWER(?) ) ");
      } else {
        sqlFilter.append(
            "AND ( LOWER(t.problem) LIKE LOWER(?) OR " +
                "LOWER(t.comment) LIKE LOWER(?) OR " +
                "LOWER(t.solution) LIKE LOWER(?) ) ");
      }
    }
  }


  /**
   * Sets the parameters for the preparedStatement - these items must
   * correspond with the createFilter statement
   *
   * @param pst Description of Parameter
   * @return Description of the Returned Value
   * @throws SQLException Description of Exception
   * @since 1.2
   */
  private int prepareFilter(PreparedStatement pst) throws SQLException {
    int i = 0;
    if (enteredBy > -1) {
      pst.setInt(++i, enteredBy);
    }
    if (description != null) {
      pst.setString(++i, description);
    }
    if (forReview == Constants.TRUE) {
      pst.setBoolean(++i, true);
    }
    if (orgId > -1) {
      pst.setInt(++i, orgId);
    }
    if (department > -1) {
      pst.setInt(++i, department);
    }
    if (assignedTo > -1) {
      pst.setInt(++i, assignedTo);
    }
    if (excludeAssignedTo > -1) {
      pst.setInt(++i, excludeAssignedTo);
    }
    if (severity > 0) {
      pst.setInt(++i, severity);
    }
    if (priority > 0) {
      pst.setInt(++i, priority);
    }
    if (projectId > 0) {
      pst.setInt(++i, projectId);
    }
    if (linkProjectId > 0) {
      pst.setInt(++i, linkProjectId);
    }
    if (forProjectUser > -1) {
      pst.setInt(++i, forProjectUser);
    }
    if (ownTickets != -1) {
      pst.setInt(++i, ownTickets);
      pst.setInt(++i, ownTickets);
    }
    if (catCode != -1) {
      pst.setInt(++i, catCode);
    }
    if (alertRangeStart != null) {
      pst.setTimestamp(++i, alertRangeStart);
    }
    if (alertRangeEnd != null) {
      pst.setTimestamp(++i, alertRangeEnd);
    }
    //No sync, but still need to factor in age
    if (minutesOlderThan > 0) {
      Calendar now = Calendar.getInstance();
      now.add(Calendar.MINUTE, minutesOlderThan - (2 * minutesOlderThan));
      java.sql.Timestamp adjustedDate = new java.sql.Timestamp(now.getTimeInMillis());
      pst.setTimestamp(++i, adjustedDate);
    }
    if (searchText != null && !(searchText.equals(""))) {
      pst.setString(++i, searchText);
      pst.setString(++i, searchText);
      pst.setString(++i, searchText);
    }
    return i;
  }


  /**
   * Description of the Method
   *
   * @param db       Description of the Parameter
   * @param moduleId Description of the Parameter
   * @param itemId   Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  public static int retrieveRecordCount(Connection db, int moduleId, int itemId) throws SQLException {
    int count = 0;
    StringBuffer sql = new StringBuffer();
    sql.append(
        "SELECT COUNT(*) as itemcount " +
            "FROM ticket t " +
            "WHERE ticketid > 0 ");
    PreparedStatement pst = db.prepareStatement(sql.toString());
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      count = rs.getInt("itemcount");
    }
    rs.close();
    pst.close();
    return count;
  }


  public static int countClosedTickets(Connection db, int projectId) throws SQLException {
    int i = 0;
    PreparedStatement pst = db.prepareStatement(
        "SELECT COUNT(*) " +
            "FROM ticketlink_project " +
            "WHERE project_id = ? " +
            "AND ticket_id IN " +
            "(SELECT ticketid FROM ticket WHERE closed IS NOT NULL)");
    pst.setInt(1, projectId);
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      i = rs.getInt(1);
    }
    rs.close();
    pst.close();
    return i;
  }

  public static int countOpenTickets(Connection db, int projectId) throws SQLException {
    int i = 0;
    PreparedStatement pst = db.prepareStatement(
        "SELECT COUNT(*) " +
            "FROM ticketlink_project " +
            "WHERE project_id = ? " +
            "AND ticket_id IN " +
            "(SELECT ticketid FROM ticket WHERE closed IS NULL)");
    pst.setInt(1, projectId);
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      i = rs.getInt(1);
    }
    rs.close();
    pst.close();
    return i;
  }

  public static int countOverdueTickets(Connection db, int projectId) throws SQLException {
    int i = 0;
    PreparedStatement pst = db.prepareStatement(
        "SELECT COUNT(*) " +
            "FROM ticketlink_project " +
            "WHERE project_id = ? " +
            "AND ticket_id IN " +
            "(SELECT ticketid FROM ticket WHERE closed IS NULL " +
            "AND est_resolution_date > CURRENT_TIMESTAMP) ");
    pst.setInt(1, projectId);
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      i = rs.getInt(1);
    }
    rs.close();
    pst.close();
    return i;
  }


  /**
   * Creates a hashmap of the number of tickets based on the estimated
   * resolution date
   *
   * @param db       Description of the Parameter
   * @param timeZone Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  public HashMap<String, Integer> queryRecordCount(Connection db, TimeZone timeZone) throws SQLException {
    PreparedStatement pst = null;
    ResultSet rs = null;
    HashMap<String, Integer> events = new HashMap<String, Integer>();
    StringBuffer sqlSelect = new StringBuffer();
    StringBuffer sqlFilter = new StringBuffer();
    StringBuffer sqlTail = new StringBuffer();
    createFilter(sqlFilter, db);
    sqlSelect.append(
        "SELECT " +
            DatabaseUtils.castDateTimeToDate(db, "est_resolution_date") + " AS group_date, " +
            "count(*) as nocols " +
            "FROM ticket t " +
            "WHERE ticketid > -1 " +
            "AND est_resolution_date IS NOT NULL ");
    sqlTail.append("GROUP BY group_date ");
    pst = db.prepareStatement(
        sqlSelect.toString() + sqlFilter.toString() + sqlTail.toString());
    prepareFilter(pst);
    rs = pst.executeQuery();
    while (rs.next()) {
      Timestamp estRes = rs.getTimestamp("group_date");
      String estResolutionDate = null;
      if (estRes != null) {
        estResolutionDate = DateUtils.getServerToUserDateString(
            timeZone, DateFormat.SHORT, estRes);
        int thisCount = rs.getInt("nocols");
        if (events.containsKey(estResolutionDate)) {
          int tmpCount = ((Integer) events.get(estResolutionDate)).intValue();
          thisCount += tmpCount;
        }
        events.put(estResolutionDate, new Integer(thisCount));
      }
    }
    rs.close();
    pst.close();
    return events;
  }

}

