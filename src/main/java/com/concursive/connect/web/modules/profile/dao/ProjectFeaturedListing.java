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

package com.concursive.connect.web.modules.profile.dao;

import com.concursive.commons.db.DatabaseUtils;
import com.concursive.commons.text.StringUtils;
import com.concursive.commons.web.mvc.beans.GenericBean;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;

import java.sql.*;

/**
 * Represents a featured project
 *
 * @author Kailash Bhoopalam
 * @version $Id$
 * @created January 20, 2008
 */
public class ProjectFeaturedListing extends GenericBean {

  private int id = -1;
  private int projectId = -1;
  private String portletKey = null;
  private Timestamp featuredDate = null;

  /**
   * Constructor for the ProjectRating
   */
  public ProjectFeaturedListing() {
  }


  /**
   * Constructor for the ProjectRating object
   *
   * @param rs Description of Parameter
   * @throws SQLException Description of Exception
   */
  public ProjectFeaturedListing(ResultSet rs) throws SQLException {
    buildRecord(rs);
  }

  /**
   * Constructor for the ProjectRating object
   *
   * @param db     Description of the Parameter
   * @param thisId Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public ProjectFeaturedListing(Connection db, int thisId) throws SQLException {
    queryRecord(db, thisId);
  }


  /**
   * Description of the Method
   *
   * @param db     Description of the Parameter
   * @param thisId Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public void queryRecord(Connection db, int thisId) throws SQLException {
    StringBuffer sql = new StringBuffer();
    sql.append(
        "SELECT * " +
            "FROM project_featured_listing pfl " +
            "WHERE pfl.featured_id = ? ");

    PreparedStatement pst = db.prepareStatement(sql.toString());
    int i = 0;
    pst.setInt(++i, thisId);
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      buildRecord(rs);
    }
    rs.close();
    pst.close();
  }

  public boolean insert(Connection db) throws SQLException {
    if (!isValid()) {
      return false;
    }
    Exception errorMessage = null;
    boolean commit = db.getAutoCommit();
    try {
      if (commit) {
        db.setAutoCommit(false);
      }
      StringBuffer sql = new StringBuffer();
      sql.append("INSERT INTO project_featured_listing " +
          "(" + (id > -1 ? "featured_id," : "") +
          "project_id, " +
          (featuredDate != null ? "featured_date, " : "") +
          "portlet_key )");
      sql.append("VALUES (");
      if (id > -1) {
        sql.append("?, ");
      }
      if (featuredDate != null) {
        sql.append("?, ");
      }
      sql.append("?, ?)");
      int i = 0;
      PreparedStatement pst = db.prepareStatement(sql.toString());
      if (id > -1) {
        pst.setInt(++i, id);
      }
      DatabaseUtils.setInt(pst, ++i, projectId);
      if (featuredDate != null) {
        pst.setTimestamp(++i, featuredDate);
      }
      pst.setString(++i, portletKey);

      pst.execute();
      pst.close();
      id = DatabaseUtils.getCurrVal(db, "project_featured_listing_featured_id_seq", id);

      if (commit) {
        db.commit();
      }
    } catch (Exception e) {
      errorMessage = e;
      if (commit) {
        db.rollback();
      }
    } finally {
      if (commit) {
        db.setAutoCommit(true);
      }
    }
    if (errorMessage != null) {
      throw new SQLException(errorMessage.getMessage());
    }
    return true;
  }

  public int update(Connection db) throws SQLException {
    if (this.getId() == -1) {
      throw new SQLException("ID was not specified");
    }
    if (!isValid()) {
      return -1;
    }
    // Update the project
    int resultCount = 0;
    PreparedStatement pst = db.prepareStatement(
        "UPDATE  project_featured_listing SET " +
            " project_id = ? , " +
            (featuredDate != null ? " featured_date = ? , " : "") +
            " portlet_key = ? " +
            "WHERE featured_id = ? ");
    int i = 0;
    DatabaseUtils.setInt(pst, ++i, projectId);
    if (featuredDate != null) {
      pst.setTimestamp(++i, featuredDate);
    }
    pst.setString(++i, portletKey);
    DatabaseUtils.setInt(pst, ++i, id);
    resultCount = pst.executeUpdate();
    pst.close();
    return resultCount;
  }

  public boolean delete(Connection db) throws SQLException {
    if (this.getId() == -1) {
      throw new SQLException("ID was not specified");
    }
    int recordCount = 0;
    boolean commit = db.getAutoCommit();
    try {
      if (commit) {
        db.setAutoCommit(false);
      }
      //Delete the record for featured project
      PreparedStatement pst = db.prepareStatement(
          "DELETE FROM project_featured_listing " +
              "WHERE featured_id = ? ");
      pst.setInt(1, id);
      recordCount = pst.executeUpdate();
      pst.close();
      if (commit) {
        db.commit();
      }
    } catch (Exception e) {
      if (commit) {
        db.rollback();
      }
      e.printStackTrace(System.out);
      throw new SQLException(e.getMessage());
    } finally {
      if (commit) {
        db.setAutoCommit(true);
      }
    }
    if (recordCount == 0) {
      errors.put("actionError", "Featued Project could not be deleted because it no longer exists.");
      return false;
    } else {
      return true;
    }
  }


  /**
   * Sets the Id attribute of the ProjectRating object
   *
   * @param tmp The new Id value
   */
  public void setId(int tmp) {
    this.id = tmp;
  }


  /**
   * Sets the id attribute of the ProjectRating object
   *
   * @param tmp The new id value
   */
  public void setId(String tmp) {
    this.id = Integer.parseInt(tmp);
  }


  /**
   * @param projectId the projectId to set
   */
  public void setProjectId(int projectId) {
    this.projectId = projectId;
  }


  /**
   * @param projectId the projectId to set
   */
  public void setProjectId(String projectId) {
    this.projectId = Integer.parseInt(projectId);
  }


  /**
   * Gets the Id attribute of the Project object
   *
   * @return The Id value
   */
  public int getId() {
    return id;
  }


  /**
   * @return the projectId
   */
  public int getProjectId() {
    return projectId;
  }


  /**
   * @return the portletKey
   */
  public String getPortletKey() {
    return portletKey;
  }


  /**
   * @param portletKey the portletKey to set
   */
  public void setPortletKey(String portletKey) {
    this.portletKey = portletKey;
  }


  /**
   * @return the featuredDate
   */
  public Timestamp getFeaturedDate() {
    return featuredDate;
  }


  /**
   * @param featuredDate the featuredDate to set
   */
  public void setFeaturedDate(Timestamp featuredDate) {
    this.featuredDate = featuredDate;
  }

  public void setFeaturedDate(String featuredDate) {
    this.featuredDate = DatabaseUtils.parseTimestamp(featuredDate);
  }

  /**
   * Gets the valid attribute of the Project object
   *
   * @return The valid value
   */
  private boolean isValid() {
    if (!StringUtils.hasText(portletKey)) {
      this.getErrors().put("portletKeyError", "Portlet key is required");
    }
    if (projectId == -1) {
      this.getErrors().put("projectIdError", "Project Id is required");
    }
    return !this.hasErrors();
  }

  public Project getProject() {
    return ProjectUtils.loadProject(projectId);
  }

  /**
   * Description of the Method
   *
   * @param rs Description of Parameter
   * @throws SQLException Description of Exception
   */
  private void buildRecord(ResultSet rs) throws SQLException {
    id = rs.getInt("featured_id");
    projectId = DatabaseUtils.getInt(rs, "project_id");
    portletKey = rs.getString("portlet_key");
    featuredDate = rs.getTimestamp("featured_date");
  }
}
