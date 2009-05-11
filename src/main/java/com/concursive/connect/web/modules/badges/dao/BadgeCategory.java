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
import com.concursive.commons.text.StringUtils;
import com.concursive.commons.web.mvc.beans.GenericBean;
import com.concursive.connect.Constants;
import com.concursive.connect.web.modules.documents.dao.FileItem;
import com.concursive.connect.web.modules.documents.dao.FileItemList;

import java.sql.*;

/**
 * Represents a Badge in iTeam
 *
 * @author Kailash Bhoopalam
 * @version $Id$
 * @created May 13, 2008
 */
public class BadgeCategory extends GenericBean {

  public static final String TABLE = "badge_category";
  public static final String PRIMARY_KEY = "code";

  private int id = -1;
  private int projectCategoryId = -1;
  private String itemName = null;
  private int logoId = -1;
  private Timestamp entered = null;
  private boolean enabled = true;
  private int level = 0;

  private FileItem logo = null;
  private String attachmentList = null;
  private String projectCategoryDescription = null;

  /**
   * Constructor for the BadgeCategory object
   */
  public BadgeCategory() {
  }


  /**
   * Constructor for the BadgeCategory object
   *
   * @param rs Description of Parameter
   * @throws SQLException Description of Exception
   */
  public BadgeCategory(ResultSet rs) throws SQLException {
    buildRecord(rs);
  }


  /**
   * Constructor for the BadgeCategory object
   *
   * @param db     Description of the Parameter
   * @param thisId Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public BadgeCategory(Connection db, int thisId) throws SQLException {
    queryRecord(db, thisId);
  }


  /**
   * @return the id
   */
  public int getId() {
    return id;
  }


  /**
   * @param id the id to set
   */
  public void setId(int id) {
    this.id = id;
  }


  /**
   * @param id the id to set
   */
  public void setId(String id) {
    this.id = Integer.parseInt(id);
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
   * @return the itemName
   */
  public String getItemName() {
    return itemName;
  }


  /**
   * @param itemName the itemName to set
   */
  public void setItemName(String itemName) {
    this.itemName = itemName;
  }


  /**
   * @return the logoId
   */
  public int getLogoId() {
    return logoId;
  }


  /**
   * @param logoId the logoId to set
   */
  public void setLogoId(int logoId) {
    this.logoId = logoId;
  }


  /**
   * @param logoId the logoId to set
   */
  public void setLogoId(String logoId) {
    this.logoId = Integer.parseInt(logoId);
  }


  /**
   * @return the entered
   */
  public Timestamp getEntered() {
    return entered;
  }


  /**
   * @param entered the entered to set
   */
  public void setEntered(Timestamp entered) {
    this.entered = entered;
  }

  public void setEntered(String tmp) {
    this.entered = DatabaseUtils.parseTimestamp(tmp);
  }

  /**
   * @return the enabled
   */
  public boolean getEnabled() {
    return enabled;
  }


  /**
   * @param enabled the enabled to set
   */
  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }


  /**
   * @param enabled the enabled to set
   */
  public void setEnabled(String enabled) {
    this.enabled = DatabaseUtils.parseBoolean(enabled);
  }


  /**
   * @return the level
   */
  public int getLevel() {
    return level;
  }


  /**
   * @param level the level to set
   */
  public void setLevel(int level) {
    this.level = level;
  }


  /**
   * @param level the level to set
   */
  public void setLevel(String level) {
    this.level = Integer.parseInt(level);
  }


  /**
   * @return the logo
   */
  public FileItem getLogo() {
    return logo;
  }


  /**
   * @return the attachmentList
   */
  public String getAttachmentList() {
    return attachmentList;
  }


  /**
   * @param attachmentList the attachmentList to set
   */
  public void setAttachmentList(String attachmentList) {
    this.attachmentList = attachmentList;
  }


  /**
   * @return the projectCategoryDescription
   */
  public String getProjectCategoryDescription() {
    if (projectCategoryDescription == null) {
      return "-- None --";
    }
    return projectCategoryDescription;
  }


  /**
   * Description of the Method
   *
   * @param db     Description of the Parameter
   * @param thisId Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  private void queryRecord(Connection db, int thisId) throws SQLException {
    StringBuffer sql = new StringBuffer();
    sql.append(
        "SELECT bc.*, lpc.description " +
            "FROM badge_category bc " +
            "LEFT JOIN lookup_project_category lpc ON (lpc.code = bc.project_category_id) " +
            "WHERE bc.code = ? ");

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
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
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
      sql.append(
          "INSERT INTO badge_category " +
              "(" + (id > -1 ? "code, " : "") +
              "project_category_id, " +
              "item_name, " +
              "logo_id, " +
              "enabled, " +
              "level ");
      if (entered != null) {
        sql.append(", entered ");
      }
      sql.append(") VALUES (");
      if (id > -1) {
        sql.append("?, ");
      }
      sql.append("?, ?, ?, ?, ?  ");
      if (entered != null) {
        sql.append(",? ");
      }
      sql.append(")");
      int i = 0;
      PreparedStatement pst = db.prepareStatement(sql.toString());
      if (id > -1) {
        pst.setInt(++i, id);
      }
      DatabaseUtils.setInt(pst, ++i, projectCategoryId);
      pst.setString(++i, itemName);
      DatabaseUtils.setInt(pst, ++i, logoId);
      pst.setBoolean(++i, enabled);
      pst.setInt(++i, level);
      if (entered != null) {
        pst.setTimestamp(++i, entered);
      }
      pst.execute();
      pst.close();
      id = DatabaseUtils.getCurrVal(db, "badge_category_code_seq", id);

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


  /**
   * Description of the Method
   *
   * @param db       Description of the Parameter
   * @param basePath Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  public boolean delete(Connection db, String basePath) throws SQLException {
    if (this.getId() == -1) {
      throw new SQLException("ID was not specified");
    }
    int recordCount = 0;
    boolean commit = db.getAutoCommit();
    try {
      if (commit) {
        db.setAutoCommit(false);
      }

      //Delete the associated badges
      BadgeList badgeList = new BadgeList();
      badgeList.setCategoryId(this.id);
      badgeList.buildList(db);
      badgeList.delete(db, basePath);

      //Delete the actual badge category
      PreparedStatement pst = db.prepareStatement(
          "DELETE FROM badge_category " +
              "WHERE code = ? ");
      pst.setInt(1, id);
      recordCount = pst.executeUpdate();
      pst.close();

      //Deleting files
      FileItemList files = new FileItemList();
      files.setLinkModuleId(Constants.BADGE_CATEGORY_FILES);
      files.setLinkItemId(this.id);
      files.buildList(db);
      files.delete(db, basePath);

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
      errors.put("actionError", "Badge Category could not be deleted because it no longer exists.");
      return false;
    } else {
      return true;
    }
  }


  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  public int update(Connection db) throws SQLException {
    if (this.getId() == -1) {
      throw new SQLException("ID was not specified");
    }
    if (!isValid()) {
      return -1;
    }
    int resultCount = 0;

    PreparedStatement pst = db.prepareStatement(
        "UPDATE badge_category " +
            "SET project_category_id = ?, " +
            "item_name = ?, " +
            "logo_id = ?, " +
            "enabled = ?, " +
            "level = ? " +
            "WHERE code = ? ");

    int i = 0;
    DatabaseUtils.setInt(pst, ++i, projectCategoryId);
    pst.setString(++i, itemName);
    DatabaseUtils.setInt(pst, ++i, logoId);
    pst.setBoolean(++i, enabled);
    DatabaseUtils.setInt(pst, ++i, level);
    pst.setInt(++i, id);
    resultCount = pst.executeUpdate();
    pst.close();

    return resultCount;
  }


  /**
   * Description of the Method
   *
   * @param rs Description of Parameter
   * @throws SQLException Description of Exception
   */
  private void buildRecord(ResultSet rs) throws SQLException {
    id = rs.getInt("code");
    projectCategoryId = DatabaseUtils.getInt(rs, "project_category_id");
    itemName = rs.getString("item_name");
    logoId = DatabaseUtils.getInt(rs, "logo_id");
    entered = rs.getTimestamp("entered");
    level = DatabaseUtils.getInt(rs, "level", 0);
    enabled = rs.getBoolean("enabled");

    //From lookup_project_category
    projectCategoryDescription = rs.getString("description");
  }


  /**
   * Gets the valid attribute of the BadgeCategory object
   *
   * @return The valid value
   */
  private boolean isValid() {
    if (!StringUtils.hasText(itemName)) {
      errors.put("itemNameError", "Item Name is required");
    }
    if (projectCategoryId == -1) {
      errors.put("actionError", "Project category id is missing");
    }
    return !hasErrors();
  }

  public FileItemList retrieveFiles(Connection db) throws SQLException {
    FileItemList files = new FileItemList();
    files.setLinkModuleId(Constants.BADGE_CATEGORY_FILES);
    files.setLinkItemId(id);
    files.buildList(db);
    return files;
  }


  /**
   * @param db
   */
  public void buildLogo(Connection db) throws SQLException {
    if (this.getLogoId() != -1) {
      logo = new FileItem(db, this.getLogoId());
    }
  }


  /**
   * @param db
   * @param userId
   */
  public void saveAttachments(Connection db, int userId, String basePath) throws SQLException {
    if (StringUtils.hasText(this.getAttachmentList())) {
      if (this.getLogoId() != -1) {
        int badgeCategoryLogoId = this.getLogoId();
        this.setLogoId(-1);
        this.update(db);

        FileItem fileItem = new FileItem(db, badgeCategoryLogoId);
        fileItem.delete(db, basePath);
      }
      FileItemList.convertTempFiles(db, Constants.BADGE_CATEGORY_FILES, userId, this.getId(), attachmentList);
      FileItemList files = this.retrieveFiles(db);
      if (files.size() > 0) {
        this.setLogoId(files.get(0).getId());
        this.update(db);
      }
    }
  }

  @Override
  public String toString() {
    return "BadgeCategory [ " +
        " id = [" + id + "] " +
        " projectCategoryId = [" + projectCategoryId + "] " +
        " itemName = [" + itemName + "] " +
        " logoId = [" + logoId + "] " +
        " entered = [" + entered + "] " +
        " level = [" + level + "] " +
        " enabled = [" + enabled + "] " +
        "]\n";
  }
}

