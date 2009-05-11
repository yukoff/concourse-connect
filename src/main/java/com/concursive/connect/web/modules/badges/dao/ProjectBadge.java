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

package com.concursive.connect.web.modules.badges.dao;

import com.concursive.commons.db.DatabaseUtils;
import com.concursive.commons.web.mvc.beans.GenericBean;
import com.concursive.connect.web.modules.badges.utils.BadgeUtils;

import java.sql.*;


/**
 * Represents a linking of a project to a badge
 *
 * @author lorraine bittner
 * @version $Id$
 * @created May 23, 2008
 */
public class ProjectBadge extends GenericBean {

  // Properties
  private int id = -1;
  private int badgeId = -1;
  private int projectId = -1;
  private Timestamp entered = null;

  public ProjectBadge() {
  }


  public ProjectBadge(Connection db, int id) throws SQLException {
    queryRecord(db, id);
  }

  public ProjectBadge(ResultSet rs) throws SQLException {
    buildRecord(rs);
  }


  public int getId() {
    return id;
  }


  public void setId(int tmp) {
    this.id = tmp;
  }


  public void setId(String tmp) {
    this.id = Integer.parseInt(tmp);
  }


  public int getBadgeId() {
    return badgeId;
  }


  public void setBadgeId(int tmp) {
    this.badgeId = tmp;
  }


  public void setBadgeId(String tmp) {
    this.badgeId = Integer.parseInt(tmp);
  }


  public int getProjectId() {
    return projectId;
  }


  public void setProjectId(int tmp) {
    this.projectId = tmp;
  }


  public void setProjectId(String tmp) {
    this.projectId = Integer.parseInt(tmp);
  }


  public java.sql.Timestamp getEntered() {
    return entered;
  }

  public void setEntered(java.sql.Timestamp entered) {
    this.entered = entered;
  }

  public void setEntered(String tmp) {
    this.entered = DatabaseUtils.parseTimestamp(tmp);
  }

  public Badge getBadge() {
    return BadgeUtils.loadBadge(badgeId);
  }

  private void buildRecord(ResultSet rs) throws SQLException {
    id = rs.getInt("id");
    badgeId = rs.getInt("badge_id");
    projectId = rs.getInt("project_id");
    entered = rs.getTimestamp("entered");
  }


  /**
   * Queries the record
   *
   * @param db     Connection
   * @param thisId int id of the record
   * @return true if successful, false if failure
   * @throws java.sql.SQLException the SQLException that occurred
   */
  private void queryRecord(Connection db, int thisId) throws SQLException {
    StringBuffer sql = new StringBuffer();
    sql.append(
        "SELECT id" +
            ", badge_id" +
            ", project_id" +
            ", entered " +
            "FROM badgelink_project " +
            "WHERE id = ? ");
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

  /**
   * Inserts the record
   *
   * @param db Connection
   * @return true if successful, false if failure
   * @throws java.sql.SQLException the SQLException that occurred
   */
  public boolean insert(Connection db) throws SQLException {
    if (!isValid()) {
      return false;
    }
    boolean commit = db.getAutoCommit();
    try {
      if (commit) {
        db.setAutoCommit(false);
      }
      StringBuffer sql = new StringBuffer();
      sql.append("INSERT INTO badgelink_project " +
          "(badge_id" +
          ", project_id");
      if (entered != null) {
        sql.append(", entered");//entered
      }
      sql.append(") VALUES " +
          "(?" +
          ", ?");
      if (entered != null) {
        sql.append(", ?");//entered
      }
      sql.append(") ");
      PreparedStatement pst = db.prepareStatement(sql.toString());

      int i = 0;
      pst.setInt(++i, badgeId);
      pst.setInt(++i, projectId);
      if (entered != null) {
        pst.setTimestamp(++i, entered);
      }
      pst.execute();
      pst.close();
      id = DatabaseUtils.getCurrVal(db, "badgelink_project_id_seq", -1);

      if (commit) {
        db.commit();
      }
    } catch (SQLException e) {
      if (commit) {
        db.rollback();
      }
      throw e;
    } finally {
      if (commit) {
        db.setAutoCommit(true);
      }
    }
    return true;
  }

  /**
   * Deletes the record
   *
   * @param db Connection
   * @return true if successful, false if failure
   * @throws java.sql.SQLException the SQLException that occurred
   */
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

      PreparedStatement pst = db.prepareStatement(
          "DELETE FROM badgelink_project " +
              "WHERE id = ?");
      pst.setInt(1, id);
      recordCount = pst.executeUpdate();
      pst.close();
      if (commit) {
        db.commit();
      }
    } catch (SQLException e) {
      if (commit) {
        db.rollback();
      }
      e.printStackTrace();
      throw e;
    } finally {
      if (commit) {
        db.setAutoCommit(true);
      }
    }
    if (recordCount == 0) {
      errors.put("actionError", "Badge could not be removed because it no longer exists.");
      return false;
    } else {
      return true;
    }
  }

  /**
   * Gets the valid attribute of the ProjectBadge object
   *
   * @return The valid value
   */
  private boolean isValid() {
    if (badgeId < 0) {
      errors.put("badgeError", "Badge is required");
    }
    if (projectId < 0) {
      errors.put("projectError", "Project is required");
    }
    return !hasErrors();
  }

}