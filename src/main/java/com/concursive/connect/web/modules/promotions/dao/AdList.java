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

package com.concursive.connect.web.modules.promotions.dao;

import com.concursive.commons.db.DatabaseUtils;
import com.concursive.connect.Constants;
import com.concursive.connect.web.utils.PagedListInfo;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * @author Lorraine Bittner
 * @version $Id$
 * @created May 12, 2008
 */
public class AdList extends ArrayList<Ad> {
  //filters
  private int unreleasedAds = Constants.UNDEFINED;
  private int currentAds = Constants.UNDEFINED;
  private int archivedAds = Constants.UNDEFINED;
  private int projectId = -1;
  private boolean overviewAll = false;
  private PagedListInfo pagedListInfo = null;
  private int lastAds = -1;
  private int enteredBy = -1;
  private int modifiedBy = -1;
  private boolean checkNullProjectId = false;
  private boolean checkNullCategoryId = false;
  private boolean checkNullExpirationDate = false;
  private int categoryId = -1;
  private int enabled = Constants.UNDEFINED;
  private String publishedYearMonth = null;
  private int draft = Constants.UNDEFINED;
  private int published = Constants.UNDEFINED;
  private Timestamp startOfCurrentMonth = null;
  private Timestamp startOfNextMonth = null;
  private Timestamp publishedRangeStart = null;
  private Timestamp publishedRangeEnd = null;

  //Project filters
  private int projectCategoryId = -1;
  private int portalState = Constants.UNDEFINED;
  private boolean openProjectsOnly = false;
  private int publicProjects = Constants.UNDEFINED;
  private int forUser = -1;
  private int forParticipant = Constants.UNDEFINED;
  private int groupId = -1;

  /**
   * Constructor for the AdList object
   */
  public AdList() {
  }


  /**
   * Sets the projectId attribute of the AdList object
   *
   * @param tmp The new projectId value
   */
  public void setProjectId(int tmp) {
    this.projectId = tmp;
  }


  /**
   * Sets the projectId attribute of the AdList object
   *
   * @param tmp The new projectId value
   */
  public void setProjectId(String tmp) {
    this.projectId = Integer.parseInt(tmp);
  }


  /**
   * Sets the overview attribute of the AdList object
   *
   * @param tmp The new overview value
   */
  public void setOverviewAll(boolean tmp) {
    this.overviewAll = tmp;
  }


  /**
   * Sets the overview attribute of the AdList object
   *
   * @param tmp The new overview value
   */
  public void setOverviewAll(String tmp) {
    this.overviewAll = DatabaseUtils.parseBoolean(tmp);
  }


  /**
   * Gets the overview attribute of the AdList object
   *
   * @return The overview value
   */
  public boolean getOverviewAll() {
    return overviewAll;
  }


  /**
   * Sets the pagedListInfo attribute of the AdList object
   *
   * @param tmp The new pagedListInfo value
   */
  public void setPagedListInfo(PagedListInfo tmp) {
    this.pagedListInfo = tmp;
  }

  /**
   * Gets the projectId attribute of the AdList object
   *
   * @return The projectId value
   */
  public int getProjectId() {
    return projectId;
  }

  /**
   * Gets the pagedListInfo attribute of the AdList object
   *
   * @return The pagedListInfo value
   */
  public PagedListInfo getPagedListInfo() {
    return pagedListInfo;
  }

  public int getEnteredBy() {
    return enteredBy;
  }

  public void setEnteredBy(int enteredBy) {
    this.enteredBy = enteredBy;
  }

  /**
   * Sets the enteredBy attribute of the AdList object
   *
   * @param tmp The new enteredBy value
   */
  public void setEnteredBy(String tmp) {
    this.enteredBy = Integer.parseInt(tmp);
  }

  public int getModifiedBy() {
    return modifiedBy;
  }

  public void setModifiedBy(int modifiedBy) {
    this.modifiedBy = modifiedBy;
  }

  /**
   * Sets the modifiedBy attribute of the AdList object
   *
   * @param tmp The new modifiedBy value
   */
  public void setModifiedBy(String tmp) {
    this.modifiedBy = Integer.parseInt(tmp);
  }

  public boolean getCheckNullProjectId() {
    return checkNullProjectId;
  }

  public void setCheckNullProjectId(boolean checkNullProjectId) {
    this.checkNullProjectId = checkNullProjectId;
  }

  public void setCheckNullProjectId(String checkNullProjectId) {
    this.checkNullProjectId = DatabaseUtils.parseBoolean(checkNullProjectId);
  }

  public boolean getCheckNullCategoryId() {
    return checkNullCategoryId;
  }

  public void setCheckNullCategoryId(boolean checkNullCategoryId) {
    this.checkNullCategoryId = checkNullCategoryId;
  }

  public void setCheckNullCategoryId(String checkNullCategoryId) {
    this.checkNullCategoryId = DatabaseUtils.parseBoolean(checkNullCategoryId);
  }

  public boolean getCheckNullExpirationDate() {
    return checkNullExpirationDate;
  }

  public void setCheckNullExpirationDate(boolean checkNullExpirationDate) {
    this.checkNullExpirationDate = checkNullExpirationDate;
  }

  public void setCheckNullExpirationDate(String checkNullExpirationDate) {
    this.checkNullExpirationDate = DatabaseUtils.parseBoolean(checkNullExpirationDate);
  }

  public int getUnreleasedAds() {
    return unreleasedAds;
  }

  public void setUnreleasedAds(int unreleasedAds) {
    this.unreleasedAds = unreleasedAds;
  }

  public void setUnreleasedAds(String unreleasedAds) {
    this.unreleasedAds = Integer.parseInt(unreleasedAds);
  }

  public int getCurrentAds() {
    return currentAds;
  }

  public void setCurrentAds(int currentAds) {
    this.currentAds = currentAds;
  }

  public void setCurrentAds(String currentAds) {
    this.currentAds = Integer.parseInt(currentAds);
  }

  public int getArchivedAds() {
    return archivedAds;
  }

  public void setArchivedAds(int archivedAds) {
    this.archivedAds = archivedAds;
  }

  public void setArchivedAds(String archivedAds) {
    this.archivedAds = Integer.parseInt(archivedAds);
  }

  public int getCategoryId() {
    return categoryId;
  }

  public void setCategoryId(int categoryId) {
    this.categoryId = categoryId;
  }

  public void setCategoryId(String adCategoryId) {
    this.categoryId = Integer.parseInt(adCategoryId);
  }


  /**
   * @return the enabled
   */
  public int getEnabled() {
    return enabled;
  }


  /**
   * @param enabled the enabled to set
   */
  public void setEnabled(int enabled) {
    this.enabled = enabled;
  }


  /**
   * @param enabled the enabled to set
   */
  public void setEnabled(String enabled) {
    this.enabled = DatabaseUtils.parseBooleanToConstant(enabled);
  }


  /**
   * @return the publishedYearMonth
   */
  public String getPublishedYearMonth() {
    return publishedYearMonth;
  }


  /**
   * @param publishedYearMonth the publishedYearMonth (YYYY-MM) to set
   */
  public void setPublishedYearMonth(String publishedYearMonth) {
    this.publishedYearMonth = publishedYearMonth;
    int year = Integer.parseInt(publishedYearMonth.substring(0, publishedYearMonth.indexOf("-")));
    int month = Integer.parseInt(publishedYearMonth.substring(publishedYearMonth.indexOf("-") + 1)) - 1; //0 based month

    // Calculate the start of the month
    Calendar calendar = Calendar.getInstance();
    calendar.set(year, month, 1, 0, 0, 0);
    calendar.set(Calendar.MILLISECOND, 0);
    startOfCurrentMonth = new Timestamp(calendar.getTimeInMillis());

    // Calculate the end of the month
    calendar.set(year, month, 1, 23, 59, 59);
    calendar.set(Calendar.MILLISECOND, 999);
    calendar.add(Calendar.MONTH, 1);
    startOfNextMonth = new Timestamp(calendar.getTimeInMillis());
  }


  /**
   * @return the draftAds
   */
  public int getDraft() {
    return draft;
  }


  /**
   * @param draftAds the draftAds to set
   */
  public void setDraft(int draftAds) {
    this.draft = draftAds;
  }


  /**
   * @param draftAds the draftAds to set
   */
  public void setDraft(String draftAds) {
    this.draft = Integer.parseInt(draftAds);
  }


  /**
   * @return the publishedAds
   */
  public int getPublished() {
    return published;
  }


  /**
   * @param publishedAds the publishedAds to set
   */
  public void setPublished(int publishedAds) {
    this.published = publishedAds;
  }

  /**
   * @param publishedAds the publishedAds to set
   */
  public void setPublished(String publishedAds) {
    this.published = Integer.parseInt(publishedAds);
  }


  /**
   * @return the publishedRangeStart
   */
  public Timestamp getPublishedRangeStart() {
    return publishedRangeStart;
  }


  /**
   * @param publishedRangeStart the publishedRangeStart to set
   */
  public void setPublishedRangeStart(Timestamp publishedRangeStart) {
    this.publishedRangeStart = publishedRangeStart;
  }

  public void setPublishedRangeStart(String publishedRangeStart) {
    this.publishedRangeStart = DatabaseUtils.parseTimestamp(publishedRangeStart);
  }

  /**
   * @return the publishedRangeEnd
   */
  public Timestamp getPublishedRangeEnd() {
    return publishedRangeEnd;
  }


  /**
   * @param publishedRangeEnd the publishedRangeEnd to set
   */
  public void setPublishedRangeEnd(Timestamp publishedRangeEnd) {
    this.publishedRangeEnd = publishedRangeEnd;
  }

  public void setPublishedRangeEnd(String publishedRangeEnd) {
    this.publishedRangeEnd = DatabaseUtils.parseTimestamp(publishedRangeEnd);
  }

  /**
   * @return the categoryId
   */
  public int getProjectCategoryId() {
    return projectCategoryId;
  }


  /**
   * @param projectCategoryId the categoryId to set
   */
  public void setProjectCategoryId(int projectCategoryId) {
    this.projectCategoryId = projectCategoryId;
  }


  /**
   * @param categoryId the categoryId to set
   */
  public void setProjectCategoryId(String categoryId) {
    this.projectCategoryId = Integer.parseInt(categoryId);
  }


  /**
   * @return the portalState
   */
  public int getPortalState() {
    return portalState;
  }


  /**
   * @param portalState the portalState to set
   */
  public void setPortalState(int portalState) {
    this.portalState = portalState;
  }


  /**
   * @param portalState the portalState to set
   */
  public void setPortalState(String portalState) {
    this.portalState = Integer.parseInt(portalState);
  }


  /**
   * @return the openProjectsOnly
   */
  public boolean getOpenProjectsOnly() {
    return openProjectsOnly;
  }


  /**
   * @param openProjectsOnly the openProjectsOnly to set
   */
  public void setOpenProjectsOnly(boolean openProjectsOnly) {
    this.openProjectsOnly = openProjectsOnly;
  }


  /**
   * @param openProjectsOnly the openProjectsOnly to set
   */
  public void setOpenProjectsOnly(String openProjectsOnly) {
    this.openProjectsOnly = DatabaseUtils.parseBoolean(openProjectsOnly);
  }

  public int getPublicProjects() {
    return publicProjects;
  }

  public void setPublicProjects(int publicProjects) {
    this.publicProjects = publicProjects;
  }

  public void setPublicProject(String publicProject) {
    this.publicProjects = DatabaseUtils.parseBooleanToConstant(publicProject);
  }

  public int getForUser() {
    return forUser;
  }

  public void setForUser(int forUser) {
    this.forUser = forUser;
  }

  public void setForUser(String forUser) {
    this.forUser = Integer.parseInt(forUser);
  }

  public int getForParticipant() {
    return forParticipant;
  }

  public void setForParticipant(int forParticipant) {
    this.forParticipant = forParticipant;
  }

  public void setForParticipant(String forParticipant) {
    this.forParticipant = DatabaseUtils.parseBooleanToConstant(forParticipant);
  }

  /**
   * @return the groupId
   */
  public int getGroupId() {
    return groupId;
  }


  /**
   * @param groupId the groupId to set
   */
  public void setGroupId(int groupId) {
    this.groupId = groupId;
  }


  /**
   * @param groupId the groupId to set
   */
  public void setGroupId(String groupId) {
    this.groupId = Integer.parseInt(groupId);
  }


  public int queryCount(Connection db) throws SQLException {
    int count = 0;
    StringBuffer sqlCount = new StringBuffer();
    StringBuffer sqlFilter = new StringBuffer();
    sqlCount.append(
        "SELECT COUNT(*) AS recordcount " +
            "FROM ad a " +
            "WHERE a.ad_id > -1 ");
    createFilter(sqlFilter);
    PreparedStatement pst = db.prepareStatement(sqlCount.toString() + sqlFilter.toString());
    prepareFilter(pst);
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      count = rs.getInt("recordcount");
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
            "FROM ad a " +
            "WHERE a.ad_id > -1 ");
    createFilter(sqlFilter);
    if (pagedListInfo == null) {
      pagedListInfo = new PagedListInfo();
      pagedListInfo.setItemsPerPage(lastAds);
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
          "AND lower(a.heading) > ? ");
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
    pagedListInfo.setDefaultSort("a.publish_date desc", null);
    pagedListInfo.appendSqlTail(db, sqlOrder);
    //Need to build a base SQL statement for returning records
    if (pagedListInfo != null) {
      pagedListInfo.appendSqlSelectHead(db, sqlSelect);
    } else {
      sqlSelect.append("SELECT ");
    }
    sqlSelect.append(
        "a.* " +
            "FROM ad a " +
            "LEFT JOIN ad_category c ON (a.ad_category_id = c.code) " +
            "WHERE a.ad_id > -1 ");
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
      Ad ad = new Ad(rs);
      this.add(ad);
    }
    rs.close();
    pst.close();
  }

  protected void createFilter(StringBuffer sqlFilter) {
    if (projectId > 0) {
      sqlFilter.append("AND a.project_id = ? ");
    }
    if (currentAds == Constants.TRUE) {
      sqlFilter.append(
          "AND a.publish_date <= CURRENT_TIMESTAMP " +
              "AND (CURRENT_TIMESTAMP <= a.expiration_date OR a.expiration_date IS NULL) ");
    }
    if (archivedAds == Constants.TRUE) {
      sqlFilter.append(
          "AND CURRENT_TIMESTAMP > a.expiration_date " +
              "AND a.expiration_date IS NOT NULL " +
              "AND a.publish_date IS NOT NULL ");
    }
    if (unreleasedAds == Constants.TRUE) {
      sqlFilter.append("AND (CURRENT_TIMESTAMP < a.publish_date) ");
    }
    if (draft == Constants.TRUE) {
      sqlFilter.append("AND (a.publish_date IS NULL) ");
    }
    if (published == Constants.TRUE) {
      sqlFilter.append("AND (a.publish_date IS NOT NULL) ");
    }
    if (overviewAll) {
      sqlFilter.append(
          "AND (( " +
              "    a.publish_date <= CURRENT_TIMESTAMP " +
              "    AND (CURRENT_TIMESTAMP <= a.expiration_date OR a.expiration_date IS NULL) " +
              "  OR ( " +
              "          (CURRENT_TIMESTAMP < a.publish_date OR a.publish_date IS NULL)))) ");
    }
    //@TODO add projectCategoryId
    if (categoryId > -1) {
      sqlFilter.append("AND a.ad_category_id = ? ");
    }
    if (checkNullProjectId) {
      sqlFilter.append("AND a.project_id IS NULL ");
    }
    if (checkNullCategoryId) {
      sqlFilter.append("AND a.ad_category_id IS NULL ");
    }
    if (checkNullExpirationDate) {
      sqlFilter.append("AND a.expiration_date IS NULL ");
    }
    if (enteredBy > 0) {
      sqlFilter.append(" AND a.enteredby = ? ");
    }
    if (publishedYearMonth != null) {
      sqlFilter.append(" AND ( a.publish_date >= ? AND a.publish_date < ? )");
    }

    if (projectCategoryId > -1 ||
        portalState != Constants.UNDEFINED ||
        groupId > -1 ||
        openProjectsOnly ||
        publicProjects == Constants.TRUE ||
        forParticipant == Constants.TRUE) {
      sqlFilter.append("AND project_id IN (select project_id from projects WHERE project_id > 0 ");
      if (projectCategoryId > -1) {
        sqlFilter.append("AND category_id = ? ");
      }
      if (portalState != Constants.UNDEFINED) {
        sqlFilter.append("AND portal = ?  ");
      }
      if (groupId > -1) {
        sqlFilter.append("AND group_id = ?  ");
      }
      if (openProjectsOnly) {
        sqlFilter.append("AND closedate IS NULL ");
      }
      if (publicProjects == Constants.TRUE) {
        sqlFilter.append("AND allow_guests = ? AND approvaldate IS NOT NULL ");
      }
      if (forParticipant == Constants.TRUE) {
        sqlFilter.append("AND (allows_user_observers = ? OR allow_guests = ?) AND approvaldate IS NOT NULL ");
      }
      sqlFilter.append(") ");
    }
    if (forUser > -1) {
      sqlFilter.append("AND (project_id IN (SELECT DISTINCT project_id FROM project_team WHERE user_id = ? " +
          "AND status IS NULL) OR project_id IN (SELECT project_id FROM projects WHERE allow_guests = ? AND approvaldate IS NOT NULL)) ");
    }
    if (publishedRangeStart != null) {
      sqlFilter.append(" AND (a.publish_date >= ? ) ");
    }
    if (publishedRangeEnd != null) {
      sqlFilter.append(" AND (a.publish_date < ?) ");
    }
  }

  protected int prepareFilter(PreparedStatement pst) throws SQLException {
    int i = 0;
    if (projectId > 0) {
      pst.setInt(++i, projectId);
    }
    if (categoryId > 0) {
      pst.setInt(++i, categoryId);
    }
    if (enteredBy > 0) {
      pst.setInt(++i, enteredBy);
    }
    if (publishedYearMonth != null) {
      pst.setTimestamp(++i, startOfCurrentMonth);
      pst.setTimestamp(++i, startOfNextMonth);
    }
    if (projectCategoryId > -1 ||
        portalState != Constants.UNDEFINED ||
        groupId > -1 ||
        openProjectsOnly ||
        publicProjects == Constants.TRUE ||
        forParticipant == Constants.TRUE) {
      if (projectCategoryId > -1) {
        pst.setInt(++i, projectCategoryId);
      }
      if (portalState != Constants.UNDEFINED) {
        pst.setBoolean(++i, (portalState == Constants.TRUE));
      }
      if (groupId > -1) {
        pst.setInt(++i, groupId);
      }
      if (publicProjects == Constants.TRUE) {
        pst.setBoolean(++i, true);
      }
      if (forParticipant == Constants.TRUE) {
        pst.setBoolean(++i, true);
        pst.setBoolean(++i, true);
      }
    }
    if (forUser > -1) {
      pst.setInt(++i, forUser);
      pst.setBoolean(++i, true);
    }
    if (publishedRangeStart != null) {
      pst.setTimestamp(++i, publishedRangeStart);
    }
    if (publishedRangeEnd != null) {
      pst.setTimestamp(++i, publishedRangeEnd);
    }

    return i;
  }

  public void delete(Connection db) throws SQLException {
    for (Ad ad : this) {
      ad.delete(db);
    }
  }

}
