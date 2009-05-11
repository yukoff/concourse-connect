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

import com.concursive.commons.db.DatabaseUtils;
import com.concursive.commons.web.mvc.beans.GenericBean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Represents a category in which a Ticket is classified
 *
 * @author chris
 * @version $Id$
 * @created December 11, 2001
 */
public class TicketCategory extends GenericBean {

  private int id = -1;
  private int categoryLevel = -1;
  private int parentCode = -1;
  private String description = "";
  private boolean enabled = true;
  private int level = -1;
  private int projectId = -1;


  /**
   * Constructor for the TicketCategory object
   */
  public TicketCategory() {
  }


  /**
   * Constructor for the TicketCategory object
   *
   * @param rs Description of Parameter
   * @throws SQLException Description of Exception
   */
  public TicketCategory(ResultSet rs) throws SQLException {
    buildRecord(rs);
  }


  /**
   * Constructor for the TicketCategory object
   *
   * @param db Description of Parameter
   * @param id Description of the Parameter
   * @throws SQLException Description of Exception
   */
  public TicketCategory(Connection db, int id) throws SQLException {
    if (id < 0) {
      throw new SQLException("Ticket Category not specified");
    }
    String sql =
        "SELECT tc.* " +
            "FROM ticket_category tc " +
            "WHERE tc.id > -1 " +
            "AND tc.id = ? ";
    PreparedStatement pst = db.prepareStatement(sql);
    pst.setInt(1, id);
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      buildRecord(rs);
    } else {
      rs.close();
      pst.close();
      throw new SQLException("Ticket Category record not found.");
    }
    rs.close();
    pst.close();
  }


  /**
   * Sets the Code attribute of the TicketCategory object
   *
   * @param tmp The new Code value
   */
  public void setId(int tmp) {
    this.id = tmp;
  }


  /**
   * Sets the id attribute of the TicketCategory object
   *
   * @param tmp The new id value
   */
  public void setId(String tmp) {
    this.id = Integer.parseInt(tmp);
  }


  /**
   * Sets the categoryLevel attribute of the TicketCategory object
   *
   * @param tmp The new categoryLevel value
   */
  public void setCategoryLevel(int tmp) {
    this.categoryLevel = tmp;
  }


  /**
   * Sets the categoryLevel attribute of the TicketCategory object
   *
   * @param tmp The new categoryLevel value
   */
  public void setCategoryLevel(String tmp) {
    this.categoryLevel = Integer.parseInt(tmp);
  }


  /**
   * Sets the Level attribute of the TicketCategory object
   *
   * @param level The new Level value
   */
  public void setLevel(int level) {
    this.level = level;
  }


  /**
   * Sets the Level attribute of the TicketCategory object
   *
   * @param level The new Level value
   */
  public void setLevel(String level) {
    this.level = Integer.parseInt(level);
  }


  /**
   * Sets the ParentCode attribute of the TicketCategory object
   *
   * @param tmp The new ParentCode value
   */
  public void setParentCode(int tmp) {
    this.parentCode = tmp;
  }


  /**
   * Sets the ParentCode attribute of the TicketCategory object
   *
   * @param tmp The new ParentCode value
   */
  public void setParentCode(String tmp) {
    this.parentCode = Integer.parseInt(tmp);
  }


  /**
   * Sets the Description attribute of the TicketCategory object
   *
   * @param tmp The new Description value
   */
  public void setDescription(String tmp) {
    this.description = tmp;
  }


  /**
   * Sets the Enabled attribute of the TicketCategory object
   *
   * @param tmp The new Enabled value
   */
  public void setEnabled(boolean tmp) {
    this.enabled = tmp;
  }


  /**
   * Sets the enabled attribute of the TicketCategory object
   *
   * @param tmp The new enabled value
   */
  public void setEnabled(String tmp) {
    this.enabled = DatabaseUtils.parseBoolean(tmp);
  }


  /**
   * Gets the Level attribute of the TicketCategory object
   *
   * @return The Level value
   */
  public int getLevel() {
    return level;
  }


  /**
   * Gets the Code attribute of the TicketCategory object
   *
   * @return The Code value
   */
  public int getId() {
    return id;
  }


  /**
   * Gets the categoryLevel attribute of the TicketCategory object
   *
   * @return The categoryLevel value
   */
  public int getCategoryLevel() {
    return categoryLevel;
  }


  /**
   * Gets the ParentCode attribute of the TicketCategory object
   *
   * @return The ParentCode value
   */
  public int getParentCode() {
    return parentCode;
  }


  /**
   * Gets the Description attribute of the TicketCategory object
   *
   * @return The Description value
   */
  public String getDescription() {
    return description;
  }


  /**
   * Gets the Enabled attribute of the TicketCategory object
   *
   * @return The Enabled value
   */
  public boolean getEnabled() {
    return enabled;
  }

  public int getProjectId() {
    return projectId;
  }

  public void setProjectId(int projectId) {
    this.projectId = projectId;
  }

  public void setProjectId(String projectId) {
    this.projectId = Integer.parseInt(projectId);
  }

  /**
   * Description of the Method
   *
   * @param db Description of Parameter
   * @return Description of the Returned Value
   * @throws SQLException Description of Exception
   */
  public boolean insert(Connection db) throws SQLException {
    StringBuffer sql = new StringBuffer();
    boolean commit = db.getAutoCommit();
    try {
      if (commit) {
        db.setAutoCommit(false);
      }
      sql.append(
          "INSERT INTO ticket_category " +
              "(cat_level, parent_cat_code, description, level, enabled, project_id) " +
              "VALUES (?, ?, ?, ?, ?, ?) ");
      int i = 0;
      PreparedStatement pst = db.prepareStatement(sql.toString());
      pst.setInt(++i, this.getCategoryLevel());
      if (parentCode > 0) {
        pst.setInt(++i, this.getParentCode());
      } else {
        pst.setInt(++i, 0);
      }
      pst.setString(++i, this.getDescription());
      pst.setInt(++i, this.getLevel());
      pst.setBoolean(++i, this.getEnabled());
      DatabaseUtils.setInt(pst, ++i, projectId);
      pst.execute();
      pst.close();
      id = DatabaseUtils.getCurrVal(db, "ticket_category_id_seq", -1);
      if (commit) {
        db.commit();
      }
    } catch (SQLException e) {
      if (commit) {
        db.rollback();
      }
      throw new SQLException(e.getMessage());
    } finally {
      if (commit) {
        db.setAutoCommit(true);
      }
    }
    return true;
  }


  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  public int update(Connection db) throws SQLException {
    if (id == -1) {
      throw new SQLException("Id not specified");
    }
    int i = 0;
    int count = 0;
    try {
      db.setAutoCommit(false);
      PreparedStatement pst = db.prepareStatement(
          "UPDATE ticket_category " +
              "SET description = ?, cat_level = ?, parent_cat_code = ?, level = ?, enabled = ? " +
              "WHERE  id = ? ");
      pst.setString(++i, this.getDescription());
      pst.setInt(++i, this.getCategoryLevel());
      pst.setInt(++i, this.getParentCode());
      pst.setInt(++i, this.getLevel());
      pst.setBoolean(++i, this.getEnabled());
      pst.setInt(++i, this.getId());
      count = pst.executeUpdate();
      pst.close();
      db.commit();
    } catch (SQLException e) {
      db.rollback();
      throw new SQLException(e.getMessage());
    } finally {
      db.setAutoCommit(true);
    }
    return count;
  }


  /**
   * Description of the Method
   *
   * @param rs Description of Parameter
   * @throws SQLException Description of Exception
   */
  protected void buildRecord(ResultSet rs) throws SQLException {
    id = rs.getInt("id");
    categoryLevel = rs.getInt("cat_level");
    parentCode = rs.getInt("parent_cat_code");
    description = rs.getString("description");
    level = rs.getInt("level");
    enabled = rs.getBoolean("enabled");
    projectId = DatabaseUtils.getInt(rs, "project_id");
  }
}

