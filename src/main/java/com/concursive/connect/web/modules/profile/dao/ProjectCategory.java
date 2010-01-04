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
import com.concursive.connect.Constants;
import com.concursive.connect.cache.utils.CacheUtils;
import com.concursive.connect.web.modules.badges.dao.BadgeCategoryList;
import com.concursive.connect.web.modules.classifieds.dao.ClassifiedCategoryList;
import com.concursive.connect.web.modules.documents.dao.FileItem;
import com.concursive.connect.web.modules.documents.dao.FileItemList;
import com.concursive.connect.web.modules.promotions.dao.AdCategoryList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Represents a project's category
 *
 * @author matt rajkowski
 * @version $Id$
 * @created December 27, 2004
 */
public class ProjectCategory extends GenericBean {

  // Properties
  private int id = -1;
  private String description = null;
  private boolean enabled = true;
  private int level = -1;
  private int logoId = -1;
  private int parentCategoryId = -1;
  private String style = null;
  private boolean styleEnabled = false;
  private boolean sensitive = false;

  // Helpers
  private FileItem logo = null;
  private String attachmentList = null;

  //Constants (Kind of a reserved category name)
  public static final String CATEGORY_NAME_ALL = "all";

  public ProjectCategory() {
  }


  public ProjectCategory(Connection db, int id) throws SQLException {
    queryRecord(db, id);
  }


  public ProjectCategory(ResultSet rs) throws SQLException {
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


  public String getDescription() {
    return description;
  }

  public String getLowerCaseDescription() {
    if (StringUtils.hasText(description)) {
      return description.trim().toLowerCase();
    }
    return null;
  }


  public void setDescription(String tmp) {
    this.description = tmp;
  }


  public boolean getEnabled() {
    return enabled;
  }


  public void setEnabled(boolean tmp) {
    this.enabled = tmp;
  }


  public void setEnabled(String tmp) {
    this.enabled = DatabaseUtils.parseBoolean(tmp);
  }


  public int getLevel() {
    return level;
  }


  public void setLevel(int tmp) {
    this.level = tmp;
  }


  public void setLevel(String tmp) {
    this.level = Integer.parseInt(tmp);
  }


  /**
   * @return the logoId
   */
  public int getLogoId() {
    return logoId;
  }

  public boolean hasLogoId() {
    return logoId > -1;
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
   * @return the logo
   */
  public FileItem getLogo() {
    return logo;
  }


  /**
   * @param logo the logo to set
   */
  public void setLogo(FileItem logo) {
    this.logo = logo;
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

  public int getParentCategoryId() {
    return parentCategoryId;
  }

  public void setParentCategoryId(int parentCategoryId) {
    this.parentCategoryId = parentCategoryId;
  }

  public void setParentCategoryId(String parentCategoryId) {
    this.parentCategoryId = Integer.parseInt(parentCategoryId);
  }

  /**
   * @return the style
   */
  public String getStyle() {
    return style;
  }


  /**
   * @param style the style to set
   */
  public void setStyle(String style) {
    this.style = style;
  }


  /**
   * @return the styleEnabled
   */
  public boolean getStyleEnabled() {
    return styleEnabled;
  }


  /**
   * @param styleEnabled the styleEnabled to set
   */
  public void setStyleEnabled(boolean styleEnabled) {
    this.styleEnabled = styleEnabled;
  }


  public void setStyleEnabled(String styleEnabled) {
    this.styleEnabled = DatabaseUtils.parseBoolean(styleEnabled);
  }

  public boolean getSensitive() {
    return sensitive;
  }

  public void setSensitive(boolean sensitive) {
    this.sensitive = sensitive;
  }

  public void setSensitive(String tmp) {
    this.sensitive = DatabaseUtils.parseBoolean(tmp);
  }


  private void buildRecord(ResultSet rs) throws SQLException {
    id = rs.getInt("code");
    description = rs.getString("description");
    enabled = rs.getBoolean("enabled");
    level = rs.getInt("level");
    logoId = DatabaseUtils.getInt(rs, "logo_id");
    parentCategoryId = DatabaseUtils.getInt(rs, "parent_category");
    style = rs.getString("style");
    styleEnabled = rs.getBoolean("style_enabled");
    sensitive = rs.getBoolean("is_sensitive");
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
        "SELECT * " +
            "FROM lookup_project_category " +
            "WHERE code = ? ");
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
      PreparedStatement pst = db.prepareStatement(
          "INSERT INTO lookup_project_category " +
              "(description, enabled, level, logo_id, parent_category, style, style_enabled, is_sensitive) VALUES " +
              "(?, ?, ?, ?, ?, ?, ?, ?) ");
      int i = 0;
      pst.setString(++i, description);
      pst.setBoolean(++i, enabled);
      pst.setInt(++i, level);
      DatabaseUtils.setInt(pst, ++i, logoId);
      DatabaseUtils.setInt(pst, ++i, parentCategoryId);
      pst.setString(++i, style);
      pst.setBoolean(++i, styleEnabled);
      pst.setBoolean(++i, sensitive);
      pst.execute();
      pst.close();
      id = DatabaseUtils.getCurrVal(db, "lookup_project_cat_code_seq", -1);

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
    int resultCount = 0;
    PreparedStatement pst = db.prepareStatement(
        "UPDATE lookup_project_category " +
            "SET description = ?, enabled = ?, level = ?, logo_id = ?, parent_category = ?, style = ?, style_enabled = ?, is_sensitive = ? " +
            "WHERE code = ? ");
    int i = 0;
    pst.setString(++i, description);
    pst.setBoolean(++i, enabled);
    pst.setInt(++i, level);
    DatabaseUtils.setInt(pst, ++i, logoId);
    DatabaseUtils.setInt(pst, ++i, parentCategoryId);
    pst.setString(++i, style);
    pst.setBoolean(++i, styleEnabled);
    pst.setBoolean(++i, sensitive);
    pst.setInt(++i, id);
    resultCount = pst.executeUpdate();
    pst.close();
    CacheUtils.invalidateValue(Constants.SYSTEM_PROJECT_CATEGORY_LIST_CACHE, id);
    return resultCount;
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

      // Don't leave invalid ids on the project
      PreparedStatement pst = db.prepareStatement(
          "UPDATE projects SET category_id = NULL WHERE category_id = ?");
      pst.setInt(1, id);
      pst.execute();
      pst.close();

      //Delete the associated badge categories
      BadgeCategoryList badgeCategoryList = new BadgeCategoryList();
      badgeCategoryList.setProjectCategoryId(this.id);
      badgeCategoryList.buildList(db);
      badgeCategoryList.delete(db, basePath);

      //Delete the associated classified categories
      ClassifiedCategoryList classifiedCategoryList = new ClassifiedCategoryList();
      classifiedCategoryList.setProjectCategoryId(this.id);
      classifiedCategoryList.buildList(db);
      classifiedCategoryList.delete(db, basePath);

      //Delete the associated ad categories
      AdCategoryList adCategoryList = new AdCategoryList();
      adCategoryList.setProjectCategoryId(this.id);
      adCategoryList.buildList(db);
      adCategoryList.delete(db, basePath);

      //Delete the actual category
      pst = db.prepareStatement(
          "DELETE FROM lookup_project_category " +
              "WHERE code = ? ");
      pst.setInt(1, id);
      recordCount = pst.executeUpdate();
      pst.close();

      //Deleting files (last because they have associated files that get deleted)
      FileItemList files = new FileItemList();
      files.setLinkModuleId(Constants.PROJECT_CATEGORY_FILES);
      files.setLinkItemId(this.id);
      files.buildList(db);
      files.delete(db, basePath);

      if (commit) {
        db.commit();
      }
      CacheUtils.invalidateValue(Constants.SYSTEM_PROJECT_CATEGORY_LIST_CACHE, id);
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
      errors.put("actionError", "Project Category could not be deleted because it no longer exists.");
      return false;
    } else {
      return true;
    }
  }

  /**
   * Gets the valid attribute of the BadgeCategory object
   *
   * @return The valid value
   */
  private boolean isValid() {
    if (description == null || "".equals(description.trim())) {
      errors.put("descriptionError", "Description is required");
    }
    return !hasErrors();
  }

  public FileItemList retrieveFiles(Connection db) throws SQLException {
    FileItemList files = new FileItemList();
    files.setLinkModuleId(Constants.PROJECT_CATEGORY_FILES);
    files.setLinkItemId(id);
    files.buildList(db);
    return files;
  }


  /**
   * @param db
   * @throws java.sql.SQLException
   */
  public void buildLogo(Connection db) throws SQLException {
    if (this.getLogoId() != -1) {
      logo = new FileItem(db, this.getLogoId());
    }
  }


  /**
   * @param db
   * @param userId
   * @throws java.sql.SQLException
   */
  public void saveAttachments(Connection db, int userId, String basePath) throws SQLException {
    if (StringUtils.hasText(this.getAttachmentList())) {
      if (this.getLogoId() != -1) {
        int projectLogoId = this.getLogoId();
        this.setLogoId(-1);
        this.update(db);
        FileItem fileItem = new FileItem(db, projectLogoId);
        fileItem.delete(db, basePath);
      }
      FileItemList.convertTempFiles(db, Constants.PROJECT_CATEGORY_FILES, userId, this.getId(), attachmentList);
      FileItemList files = this.retrieveFiles(db);
      if (files.size() > 0) {
        this.setLogoId(files.get(0).getId());
        this.update(db);
      }
    }
  }

  public String getNormalizedCategoryName() {
  	return getNormalizedCategoryName(description);
  }

  public static String getNormalizedCategoryName(String projectCategoryName) {
    if (StringUtils.hasText(projectCategoryName)) {
      return StringUtils.jsEscape(StringUtils.replace(projectCategoryName.toLowerCase(), " ", "_"));
    }

    return null;
  }
  
  public static String getCategoryNameFromNormalizedCategoryName(String normalizedCategoryName){
    if (StringUtils.hasText(normalizedCategoryName)) {
      return StringUtils.jsUnEscape(StringUtils.replace(normalizedCategoryName, "_", " "));
    }

    return null;
  }
}

