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

package com.concursive.connect.web.modules.classifieds.dao;

import com.concursive.commons.db.DatabaseUtils;
import com.concursive.connect.Constants;
import com.concursive.connect.web.utils.PagedListInfo;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

/**
 * Represents a collection of Classified Ad objects
 *
 * @author Kailash Bhoopalam
 * @created May 21, 2008
 */
public class ClassifiedList extends ArrayList<Classified> {
  // main classified filters (default retrieves all classifieds)
  private PagedListInfo pagedListInfo = null;
  private int classifiedId = -1;
  private int projectId = -1;
  private int enabled = Constants.UNDEFINED;
  private String publishedYearMonth = null;
  private int draft = Constants.UNDEFINED;
  private int published = Constants.UNDEFINED;
  private Timestamp startOfCurrentMonth = null;
  private Timestamp startOfNextMonth = null;
  private int enteredBy = -1;
  private Timestamp publishedRangeStart = null;
  private Timestamp publishedRangeEnd = null;
  private int currentClassifieds = Constants.UNDEFINED; //valid values are Constants.UNDEFINED and Constants.TRUE
  private int archivedClassifieds = Constants.UNDEFINED; //valid values are Constants.UNDEFINED and Constants.TRUE

  //Classified category filters
  private int categoryId = -1;
  private String categoryLowercaseName = null;
  private boolean checkNullCategoryId = false;
  private boolean onlyWithoutProjectCategory = false;
  private boolean onlyWithoutClassifiedCategory = false;

  //Project filters
  private int projectCategoryId = -1;
  private boolean openProjectsOnly = false;
  private int publicProjects = Constants.UNDEFINED;
  private int forUser = -1;
  private int forParticipant = Constants.UNDEFINED;
  private int groupId = -1;

  /**
   * Constructor for the ClassifiedList object
   */
  public ClassifiedList() {
  }


  /**
   * Sets the pagedListInfo attribute of the ClassifiedList object
   *
   * @param tmp The new pagedListInfo value
   */
  public void setPagedListInfo(PagedListInfo tmp) {
    this.pagedListInfo = tmp;
  }


  /**
   * @return the classifiedId
   */
  public int getClassifiedId() {
    return classifiedId;
  }


  /**
   * @param classifiedId the classifiedId to set
   */
  public void setClassifiedId(int classifiedId) {
    this.classifiedId = classifiedId;
  }


  /**
   * @param classifiedId the classifiedId to set
   */
  public void setClassifiedId(String classifiedId) {
    this.classifiedId = Integer.parseInt(classifiedId);
  }


  /**
   * @return the projectCategoryId
   */
  public int getProjectCategoryId() {
    return projectCategoryId;
  }


  /**
   * @param projectCategoryId the projectCategoryId to set
   */
  public void setProjectCategoryId(int projectCategoryId) {
    this.projectCategoryId = projectCategoryId;
  }


  /**
   * @param projectCategoryId the projectCategoryId to set
   */
  public void setProjectCategoryId(String projectCategoryId) {
    this.projectCategoryId = Integer.parseInt(projectCategoryId);
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

  public void setOpenProjectsOnly(String openProjectsOnly) {
    this.openProjectsOnly = DatabaseUtils.parseBoolean(openProjectsOnly);
  }

  public int getPublicProjects() {
    return publicProjects;
  }

  public void setPublicProjects(int publicProjects) {
    this.publicProjects = publicProjects;
  }

  public void setPublicProjects(String publicProjects) {
    this.publicProjects = DatabaseUtils.parseBooleanToConstant(publicProjects);
  }

  public int getForParticipant() {
    return forParticipant;
  }

  public void setForParticipant(int forParticipant) {
    this.forParticipant = forParticipant;
  }

  public void setForParticipant(String tmp) {
    forParticipant = DatabaseUtils.parseBooleanToConstant(tmp);
  }

  public int getForUser() {
    return forUser;
  }

  public void setForUser(int forUser) {
    this.forUser = forUser;
  }

  public void setForUser(String tmp) {
    this.forUser = Integer.parseInt(tmp);
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

  public void setGroupId(String groupId) {
    this.groupId = Integer.parseInt(groupId);
  }

  /**
   * @return the classifiedCategoryId
   */
  public int getCategoryId() {
    return categoryId;
  }


  /**
   * @param categoryId the classifiedCategoryId to set
   */
  public void setCategoryId(int categoryId) {
    this.categoryId = categoryId;
  }


  /**
   * @param classifiedCategoryId the classifiedCategoryId to set
   */
  public void setCategoryId(String classifiedCategoryId) {
    this.categoryId = Integer.parseInt(classifiedCategoryId);
  }


  /**
   * @return the categoryLowercaseName
   */
  public String getCategoryLowercaseName() {
    return categoryLowercaseName;
  }


  /**
   * @param categoryLowercaseName the categoryLowercaseName to set
   */
  public void setCategoryLowercaseName(String categoryLowercaseName) {
    this.categoryLowercaseName = categoryLowercaseName.toLowerCase();
  }


  /**
   * @return the projectId
   */
  public int getProjectId() {
    return projectId;
  }


  /**
   * @param projectId the projectId to set
   */
  public void setProjectId(String projectId) {
    this.projectId = Integer.parseInt(projectId);
  }


  /**
   * @param projectId the projectId to set
   */
  public void setProjectId(int projectId) {
    this.projectId = projectId;
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
   * @return the pagedListInfo
   */
  public PagedListInfo getPagedListInfo() {
    return pagedListInfo;
  }

  public boolean isOnlyWithoutProjectCategory() {
    return onlyWithoutProjectCategory;
  }

  public void setOnlyWithoutProjectCategory(boolean onlyWithoutProjectCategory) {
    this.onlyWithoutProjectCategory = onlyWithoutProjectCategory;
  }

  public void setOnlyWithoutProjectCategory(String tmp) {
    this.onlyWithoutProjectCategory = DatabaseUtils.parseBoolean(tmp);
  }

  public boolean isOnlyWithoutClassifiedCategory() {
    return onlyWithoutClassifiedCategory;
  }

  public void setOnlyWithoutClassifiedCategory(boolean onlyWithoutClassifiedCategory) {
    this.onlyWithoutClassifiedCategory = onlyWithoutClassifiedCategory;
  }

  public void setOnlyWithoutClassifiedCategory(String tmp) {
    this.onlyWithoutClassifiedCategory = DatabaseUtils.parseBoolean(tmp);
    ;
  }

  /**
   * @return the publishedYearMonth
   */
  public String getPublishedYearMonth() {
    return publishedYearMonth;
  }


  /**
   * @param publishedYearMonth the publishedYearMonth to set
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
   * @return the draft
   */
  public int getDraft() {
    return draft;
  }


  /**
   * @param draft the draft to set
   */
  public void setDraft(int draft) {
    this.draft = draft;
  }


  /**
   * @param draft the draft to set
   */
  public void setDraft(String draft) {
    this.draft = Integer.parseInt(draft);
  }


  /**
   * @return the published
   */
  public int getPublished() {
    return published;
  }


  /**
   * @param published the published to set
   */
  public void setPublished(int published) {
    this.published = published;
  }


  /**
   * @param published the published to set
   */
  public void setPublished(String published) {
    this.published = Integer.parseInt(published);
  }


  /**
   * @return the enteredBy
   */
  public int getEnteredBy() {
    return enteredBy;
  }


  /**
   * @param enteredBy the enteredBy to set
   */
  public void setEnteredBy(int enteredBy) {
    this.enteredBy = enteredBy;
  }


  /**
   * @param enteredBy the enteredBy to set
   */
  public void setEnteredBy(String enteredBy) {
    this.enteredBy = Integer.parseInt(enteredBy);
  }


  /**
   * @return the checkNullCategoryId
   */
  public boolean getCheckNullCategoryId() {
    return checkNullCategoryId;
  }


  /**
   * @param checkNullCategoryId the checkNullCategoryId to set
   */
  public void setCheckNullCategoryId(boolean checkNullCategoryId) {
    this.checkNullCategoryId = checkNullCategoryId;
  }


  /**
   * @param checkNullCategoryId the checkNullCategoryId to set
   */
  public void setCheckNullCategoryId(String checkNullCategoryId) {
    this.checkNullCategoryId = DatabaseUtils.parseBoolean(checkNullCategoryId);
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
   * @return the currentClassifieds
   */
  public int getCurrentClassifieds() {
    return currentClassifieds;
  }


  /**
   * @param currentClassifieds the currentClassifieds to set
   */
  public void setCurrentClassifieds(int currentClassifieds) {
    this.currentClassifieds = currentClassifieds;
  }


  public void setCurrentClassifieds(String currentClassifieds) {
    this.currentClassifieds = Integer.parseInt(currentClassifieds);
  }


  /**
   * @return the archivedClassifieds
   */
  public int getArchivedClassifieds() {
    return archivedClassifieds;
  }


  /**
   * @param archivedClassifieds the archivedClassifieds to set
   */
  public void setArchivedClassifieds(int archivedClassifieds) {
    this.archivedClassifieds = archivedClassifieds;
  }

  public void setArchivedClassifieds(String archivedClassifieds) {
    this.archivedClassifieds = Integer.parseInt(archivedClassifieds);
  }

  /**
   * Description of the Method
   *
   * @param db Description of Parameter
   * @throws SQLException Description of Exception
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
            "FROM project_classified " +
            "WHERE classified_id > -1 ");
    createFilter(db, sqlFilter);
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
          "AND lower(title) < ? ");
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
    pagedListInfo.setDefaultSort("title", null);
    pagedListInfo.appendSqlTail(db, sqlOrder);

    //Need to build a base SQL statement for returning records
    pagedListInfo.appendSqlSelectHead(db, sqlSelect);
    sqlSelect.append(
        "* " +
            "FROM project_classified " +
            "WHERE classified_id > -1 ");
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
      Classified thisClassified = new Classified(rs);
      this.add(thisClassified);
    }
    rs.close();
    pst.close();
    // Build extra data
    for (Classified c : this) {
      c.buildFiles(db);
    }
  }


  /**
   * Description of the Method
   *
   * @param sqlFilter Description of Parameter
   */
  private void createFilter(Connection db, StringBuffer sqlFilter) {
    if (sqlFilter == null) {
      sqlFilter = new StringBuffer();
    }
    if (classifiedId > -1) {
      sqlFilter.append("AND classified_id = ? ");
    }
    //TODO: the project_category_id field in project_classified is not used
    /*
    if (projectCategoryId > -1) {
     sqlFilter.append("AND project_category_id = ? ");
    }
    */
    if (projectCategoryId > -1 ||
        groupId > -1 ||
        openProjectsOnly ||
        publicProjects == Constants.TRUE ||
        forParticipant == Constants.TRUE) {
      sqlFilter.append("AND project_id IN (SELECT project_id FROM projects WHERE project_id > 0 ");
      if (projectCategoryId > -1) {
        sqlFilter.append("AND category_id = ? ");
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
    if (categoryId > -1) {
      sqlFilter.append("AND classified_category_id = ? ");
    }
    if (categoryLowercaseName != null) {
      sqlFilter.append("AND classified_category_id IN (SELECT code FROM classified_category WHERE " + DatabaseUtils.toLowerCase(db, "item_name") + " = ?) ");
    }
    if (projectId > -1) {
      sqlFilter.append("AND project_id = ? ");
    }
    if (enabled != Constants.UNDEFINED) {
      sqlFilter.append("AND (enabled = ?) ");
    }
    if (onlyWithoutProjectCategory) {
      sqlFilter.append("AND project_category_id IS NOT NULL ");
    }
    if (onlyWithoutClassifiedCategory) {
      sqlFilter.append("AND classified_category_id IS NOT NULL ");
    }
    if (draft == Constants.TRUE) {
      sqlFilter.append("AND (publish_date IS NULL) ");
    }
    if (published == Constants.TRUE) {
      sqlFilter.append("AND (publish_date IS NOT NULL) ");
    }
    if (publishedYearMonth != null) {
      sqlFilter.append(" AND ( publish_date >= ? AND publish_date < ? )");
    }
    if (enteredBy > 0) {
      sqlFilter.append(" AND enteredby = ? ");
    }
    if (checkNullCategoryId) {
      sqlFilter.append("AND classified_category_id IS NULL ");
    }
    if (publishedRangeStart != null) {
      sqlFilter.append(" AND (publish_date >= ? ) ");
    }
    if (publishedRangeEnd != null) {
      sqlFilter.append(" AND (publish_date < ?) ");
    }
    if (currentClassifieds == Constants.TRUE) {
      sqlFilter.append(
          "AND publish_date <= CURRENT_TIMESTAMP " +
              "AND (CURRENT_TIMESTAMP <= expiration_date OR expiration_date IS NULL) ");
    }
    if (archivedClassifieds == Constants.TRUE) {
      sqlFilter.append(
          "AND CURRENT_TIMESTAMP > expiration_date " +
              "AND expiration_date IS NOT NULL " +
              "AND publish_date IS NOT NULL ");
    }
  }


  /**
   * Description of the Method
   *
   * @param pst Description of Parameter
   * @return Description of the Returned Value
   * @throws SQLException Description of Exception
   */
  private int prepareFilter(PreparedStatement pst) throws SQLException {
    int i = 0;
    if (classifiedId > -1) {
      pst.setInt(++i, classifiedId);
    }
    //TODO: the project_category_id field in project_classified is not used
    /*
    if (projectCategoryId > -1) {
      pst.setInt(++i, projectCategoryId);
    }
    */
    if (projectCategoryId > -1 ||
        groupId > -1 ||
        openProjectsOnly ||
        publicProjects == Constants.TRUE ||
        forParticipant == Constants.TRUE) {
      if (projectCategoryId > -1) {
        pst.setInt(++i, projectCategoryId);
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
    if (categoryId > -1) {
      pst.setInt(++i, categoryId);
    }
    if (categoryLowercaseName != null) {
      pst.setString(++i, categoryLowercaseName);
    }
    if (projectId > -1) {
      pst.setInt(++i, projectId);
    }
    if (enabled != Constants.UNDEFINED) {
      pst.setBoolean(++i, (enabled == Constants.TRUE));
    }
    if (publishedYearMonth != null) {
      pst.setTimestamp(++i, startOfCurrentMonth);
      pst.setTimestamp(++i, startOfNextMonth);
    }
    if (enteredBy > 0) {
      pst.setInt(++i, enteredBy);
    }
    if (publishedRangeStart != null) {
      pst.setTimestamp(++i, publishedRangeStart);
    }
    if (publishedRangeEnd != null) {
      pst.setTimestamp(++i, publishedRangeEnd);
    }
    return i;
  }


  /**
   * @param db
   * @param basePath
   */
  public void delete(Connection db, String basePath) throws SQLException {
    Iterator<Classified> classifiedItr = this.iterator();
    while (classifiedItr.hasNext()) {
      Classified classified = classifiedItr.next();
      classified.delete(db, basePath);
    }
  }

}

