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

package com.concursive.connect.web.modules.reviews.dao;

import com.concursive.commons.db.DatabaseUtils;
import com.concursive.connect.Constants;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import com.concursive.connect.web.utils.PagedListInfo;

import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Description of the Class
 *
 * @author Kailash Bhoopalam
 * @version $Id$
 * @created June 26, 2008
 */
public class ProjectRatingList extends ArrayList<ProjectRating> {
  // main filters (default retrieves all records)
  private PagedListInfo pagedListInfo = null;
  private int ratingId = -1;
  private int projectId = -1;
  private int enteredBy = -1;
  private int minimumRatingCount = -1;
  private double minimumRatingAvg = -1;
  private int filterInappropriate = Constants.UNDEFINED;
  private Timestamp enteredRangeStart = null;
  private Timestamp enteredRangeEnd = null;

  //Project Filters
  private int categoryId = -1;
  private int portalState = Constants.UNDEFINED;
  private int publicProjects = Constants.UNDEFINED;
  private int forParticipant = Constants.UNDEFINED;
  private boolean openProjectsOnly = false;
  private int groupId = -1;
  private int forUser = -1;
  private boolean loadProject = true;

  /**
   * Constructor for the ProjectRatingList object
   */
  public ProjectRatingList() {
  }


  /**
   * Sets the pagedListInfo attribute of the ProjectRatingList object
   *
   * @param tmp The new pagedListInfo value
   */
  public void setPagedListInfo(PagedListInfo tmp) {
    this.pagedListInfo = tmp;
  }


  /**
   * @return the ratingId
   */
  public int getRatingId() {
    return ratingId;
  }


  /**
   * @param ratingId the ratingId to set
   */
  public void setRatingId(int ratingId) {
    this.ratingId = ratingId;
  }


  /**
   * @param ratingId the ratingId to set
   */
  public void setRatingId(String ratingId) {
    this.ratingId = Integer.parseInt(ratingId);
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
   * @return the pagedListInfo
   */
  public PagedListInfo getPagedListInfo() {
    return pagedListInfo;
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
   * @return the minimumRatingCount
   */
  public int getMinimumRatingCount() {
    return minimumRatingCount;
  }


  /**
   * @param minimumRatingCount the minimumRatingCount to set
   */
  public void setMinimumRatingCount(int minimumRatingCount) {
    this.minimumRatingCount = minimumRatingCount;
  }


  public void setMinimumRatingCount(String minimumRatingCount) {
    this.minimumRatingCount = Integer.parseInt(minimumRatingCount);
  }


  /**
   * @return the minimumRatingAvg
   */
  public double getMinimumRatingAvg() {
    return minimumRatingAvg;
  }


  /**
   * @param minimumRatingAvg the minimumRatingAvg to set
   */
  public void setMinimumRatingAvg(double minimumRatingAvg) {
    this.minimumRatingAvg = minimumRatingAvg;
  }

  public void setMinimumRatingAvg(String minimumRatingAvg) {
    this.minimumRatingAvg = Double.parseDouble(minimumRatingAvg);
  }

  /**
   * @return the filterInappropriate
   */
  public int getFilterInappropriate() {
    return filterInappropriate;
  }


  /**
   * @param filterInappropriate the filterInappropriate to set
   */
  public void setFilterInappropriate(int filterInappropriate) {
    this.filterInappropriate = filterInappropriate;
  }

  public void setFilterInappropriate(String filterInappropriate) {
    this.filterInappropriate = Integer.parseInt(filterInappropriate);
  }

  /**
   * @return the enteredRangeStart
   */
  public Timestamp getEnteredRangeStart() {
    return enteredRangeStart;
  }

  /**
   * @param enteredRangeStart the enteredRangeStart to set
   */
  public void setEnteredRangeStart(Timestamp enteredRangeStart) {
    this.enteredRangeStart = enteredRangeStart;
  }

  public void setEnteredRangeStart(String enteredRangeStart) {
    this.enteredRangeStart = DatabaseUtils.parseTimestamp(enteredRangeStart);
  }

  /**
   * @return the enteredRangeEnd
   */
  public Timestamp getEnteredRangeEnd() {
    return enteredRangeEnd;
  }

  /**
   * @param enteredRangeEnd the enteredRangeEnd to set
   */
  public void setEnteredRangeEnd(Timestamp enteredRangeEnd) {
    this.enteredRangeEnd = enteredRangeEnd;
  }

  public void setEnteredRangeEnd(String enteredRangeEnd) {
    this.enteredRangeEnd = DatabaseUtils.parseTimestamp(enteredRangeEnd);
  }

  /**
   * @return the categoryId
   */
  public int getCategoryId() {
    return categoryId;
  }


  /**
   * @param categoryId the categoryId to set
   */
  public void setCategoryId(int categoryId) {
    this.categoryId = categoryId;
  }


  /**
   * @param categoryId the categoryId to set
   */
  public void setCategoryId(String categoryId) {
    this.categoryId = Integer.parseInt(categoryId);
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

  public int getForUser() {
    return forUser;
  }

  public void setForUser(int tmp) {
    this.forUser = tmp;
  }

  public void setForUser(String tmp) {
    this.forUser = Integer.parseInt(tmp);
  }

  /**
   * @return the loadProject
   */
  public boolean getLoadProject() {
    return loadProject;
  }


  /**
   * @param loadProject the loadProject to set
   */
  public void setLoadProject(boolean loadProject) {
    this.loadProject = loadProject;
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
            "FROM projects_rating pr " +
            "WHERE rating_id > -1 ");
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
    pagedListInfo.setDefaultSort("rating_avg DESC", null);
    pagedListInfo.appendSqlTail(db, sqlOrder);

    //Need to build a base SQL statement for returning records
    pagedListInfo.appendSqlSelectHead(db, sqlSelect);
    sqlSelect.append(
        "* " +
            "FROM projects_rating pr " +
            "WHERE rating_id > -1 ");
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
      ProjectRating thisProjectRating = new ProjectRating(rs);
      if (loadProject) {
        thisProjectRating.setProject(ProjectUtils.loadProject(thisProjectRating.getProjectId()));
      }
      this.add(thisProjectRating);
    }
    rs.close();
    pst.close();

  }


  /**
   * Description of the Method
   *
   * @param sqlFilter Description of Parameter
   */
  private void createFilter(StringBuffer sqlFilter) {
    if (sqlFilter == null) {
      sqlFilter = new StringBuffer();
    }
    if (projectId > -1) {
      sqlFilter.append("AND project_id = ? ");
    }
    if (enteredBy > 0) {
      sqlFilter.append(" AND enteredby = ? ");
    }
    if (categoryId > -1 ||
        portalState != Constants.UNDEFINED ||
        groupId > -1 ||
        openProjectsOnly ||
        publicProjects != Constants.UNDEFINED ||
        forParticipant != Constants.UNDEFINED) {
      sqlFilter.append("AND project_id IN (select project_id from projects WHERE project_id > 0 ");
      if (categoryId > -1) {
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
      if (publicProjects != Constants.UNDEFINED) {
        sqlFilter.append("AND allow_guests = ? AND approvaldate IS NOT NULL ");
      }
      if (forParticipant != Constants.UNDEFINED) {
        sqlFilter.append("AND (allows_user_observers = ? OR allow_guests = ?) AND approvaldate IS NOT NULL ");
      }
      sqlFilter.append(") ");
    }
    if (forUser != -1) {
      sqlFilter.append("AND (pr.project_id IN (SELECT DISTINCT project_id FROM project_team WHERE user_id = ? " +
          "AND status IS NULL) OR pr.project_id IN (SELECT project_id FROM projects WHERE allow_guests = ? AND approvaldate IS NOT NULL)) ");
    }
    if (minimumRatingCount != -1) {
      sqlFilter.append("AND rating_count > ? ");
    }
    if (minimumRatingAvg != -1) {
      sqlFilter.append("AND rating_avg > ? ");
    }
    if (filterInappropriate != Constants.UNDEFINED) {
      sqlFilter.append("AND ( inappropriate_count IS NULL OR inappropriate_count = ? ) ");
    }
    if (enteredRangeStart != null) {
      sqlFilter.append("AND entered >= ? ");
    }
    if (enteredRangeEnd != null) {
      sqlFilter.append("AND entered < ? ");
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
    if (projectId > -1) {
      pst.setInt(++i, projectId);
    }
    if (enteredBy > 0) {
      pst.setInt(++i, enteredBy);
    }
    if (categoryId > -1 ||
        portalState != Constants.UNDEFINED ||
        groupId > -1 ||
        openProjectsOnly ||
        publicProjects != Constants.UNDEFINED ||
        forParticipant != Constants.UNDEFINED) {
      if (categoryId > -1) {
        pst.setInt(++i, categoryId);
      }
      if (portalState != Constants.UNDEFINED) {
        pst.setBoolean(++i, (portalState == Constants.TRUE));
      }
      if (groupId > -1) {
        pst.setInt(++i, groupId);
      }
      if (publicProjects != Constants.UNDEFINED) {
        pst.setBoolean(++i, (publicProjects == Constants.TRUE));
      }
      if (forParticipant == Constants.TRUE) {
        pst.setBoolean(++i, true);
        pst.setBoolean(++i, true);
      }
    }
    if (forUser != -1) {
      pst.setInt(++i, forUser);
      pst.setBoolean(++i, true);
    }
    if (minimumRatingCount != -1) {
      pst.setInt(++i, minimumRatingCount);
    }
    if (minimumRatingAvg != -1) {
      pst.setDouble(++i, minimumRatingAvg);
    }
    if (filterInappropriate != Constants.UNDEFINED) {
      pst.setInt(++i, 0);
    }
    if (enteredRangeStart != null) {
      pst.setTimestamp(++i, enteredRangeStart);
    }
    if (enteredRangeEnd != null) {
      pst.setTimestamp(++i, enteredRangeEnd);
    }
    return i;
  }


  /**
   * @param db
   */
  public void delete(Connection db) throws SQLException {
    Iterator<ProjectRating> projectRatingItr = this.iterator();
    while (projectRatingItr.hasNext()) {
      ProjectRating projectRating = projectRatingItr.next();
      ProjectRating.delete(db, projectRating);
    }
  }

  public int getReviewIdByUser(int userId) {
    int id = -1;
    for (ProjectRating pr : this) {
      if (pr.getEnteredBy() == userId) {
        id = pr.getId();
        break;
      }
    }
    return id;
  }

  public ProjectRating getRatingForProject(int projectId) {
    ProjectRating projectRating = null;
    Iterator<ProjectRating> itr = this.iterator();
    while (itr.hasNext()) {
      projectRating = itr.next();
      if (projectRating.getProjectId() == projectId) {
        return projectRating;
      }
    }
    return null;

  }
}

