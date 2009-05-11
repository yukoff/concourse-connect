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
import com.concursive.commons.text.StringUtils;
import com.concursive.commons.web.mvc.beans.GenericBean;
import com.concursive.connect.Constants;
import com.concursive.connect.web.modules.documents.dao.FileItem;
import com.concursive.connect.web.modules.documents.dao.FileItemList;

import java.sql.*;

/**
 * @author Lorraine Bittner
 * @version $Id$
 * @created May 13, 2008
 */
public class AdCategory extends GenericBean {
  public static final String TABLE = "ad_category";
  public static final String PRIMARY_KEY = "code";

  private int id = -1;
  private int projectCategoryId = -1;
  private String itemName = null;
  private int logoId = -1;
  private java.sql.Timestamp entered = null;
  private int level = 0;
  private boolean enabled = true;

  private boolean buildAdList = false;
  private AdList adList = null;
  private FileItem logo = null;
  private String attachmentList = null;

  public AdCategory() {
  }

  public AdCategory(Connection db, int id) throws SQLException {
    queryRecord(db, id);
  }

  public AdCategory(ResultSet rs) throws SQLException {
    buildRecord(rs);
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public void setId(String tmp) {
    this.id = Integer.parseInt(tmp);
  }

  public String getItemName() {
    return itemName;
  }

  public void setItemName(String itemName) {
    this.itemName = itemName;
  }

  public int getLogoId() {
    return logoId;
  }

  public void setLogoId(int logoId) {
    this.logoId = logoId;
  }

  public void setLogoId(String tmp) {
    this.logoId = Integer.parseInt(tmp);
  }

  public Timestamp getEntered() {
    return entered;
  }

  public void setEntered(Timestamp entered) {
    this.entered = entered;
  }

  public void setEntered(String tmp) {
    this.entered = DatabaseUtils.parseTimestamp(tmp);
  }

  public int getLevel() {
    return level;
  }

  public void setLevel(int level) {
    this.level = level;
  }

  public void setLevel(String tmp) {
    this.level = Integer.parseInt(tmp);
  }

  public boolean getEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public void setEnabled(String tmp) {
    this.enabled = DatabaseUtils.parseBoolean(tmp);
  }

  public int getProjectCategoryId() {
    return projectCategoryId;
  }

  public void setProjectCategoryId(int projectCategoryId) {
    this.projectCategoryId = projectCategoryId;
  }

  public void setProjectCategoryId(String tmp) {
    this.projectCategoryId = Integer.parseInt(tmp);
  }

  /**
   * @return the buildAdList
   */
  public boolean getBuildAdList() {
    return buildAdList;
  }


  /**
   * @param buildAdList the buildAdList to set
   */
  public void setBuildAdList(boolean buildAdList) {
    this.buildAdList = buildAdList;
  }


  /**
   * @param tmp the buildAdList to set
   */
  public void setBuildAdList(String tmp) {
    this.buildAdList = DatabaseUtils.parseBoolean(tmp);
  }


  /**
   * @return the badgeList
   */
  public AdList getAdList() {
    return adList;
  }


  /**
   * @param adList the badgeList to set
   */
  public void setAdList(AdList adList) {
    this.adList = adList;
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

  public void queryRecord(Connection db, int adId) throws SQLException {
    StringBuffer sql = new StringBuffer();
    sql.append(
        "SELECT a.* " +
            "FROM ad_category a " +
            "WHERE code = ? ");
    PreparedStatement pst = db.prepareStatement(sql.toString());
    int i = 0;
    pst.setInt(++i, adId);
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      buildRecord(rs);
    }
    rs.close();
    pst.close();
    if (id == -1) {
      throw new SQLException(String.format("Ad category record not found for id %s.", adId));
    }
  }

  /**
   * Populates this news article from a database result set
   *
   * @param rs Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  protected void buildRecord(ResultSet rs) throws SQLException {
    id = rs.getInt("code");
    projectCategoryId = DatabaseUtils.getInt(rs, "project_category_id");
    itemName = rs.getString("item_name");
    logoId = DatabaseUtils.getInt(rs, "logo_id");
    entered = rs.getTimestamp("entered");
    level = rs.getInt("level");
    enabled = rs.getBoolean("enabled");
  }

  public boolean insert(Connection db) throws SQLException {
    if (!isValid()) {
      return false;
    }
    boolean commit = db.getAutoCommit();
    if (commit) {
      db.setAutoCommit(false);
    }
    StringBuffer sql = new StringBuffer();
    sql.append(
        "INSERT INTO ad_category " +
            "( project_category_id " +
            (id > -1 ? ", code " : "") +
            ", item_name" +
            ", logo_id" +
            ", level" +
            ", enabled");
    if (entered != null) {
      sql.append(", entered ");
    }
    sql.append(" ) ");
    sql.append("VALUES (? ");//project_category_id
    if (id > -1) {
      sql.append(", ?");//code
    }
    sql.append(", ?" +//item_name
        ", ?" +//logo_id
        ", ?" +//level
        ", ?");//enabled
    if (entered != null) {
      sql.append(", ?");//entered
    }
    sql.append(" ) ");
    int i = 0;
    try {
      //Insert
      PreparedStatement pst = db.prepareStatement(sql.toString());
      DatabaseUtils.setInt(pst, ++i, projectCategoryId);
      if (id > -1) {
        pst.setInt(++i, id);
      }
      pst.setString(++i, itemName);
      DatabaseUtils.setInt(pst, ++i, logoId);
      pst.setInt(++i, level);
      pst.setBoolean(++i, enabled);
      if (entered != null) {
        pst.setTimestamp(++i, entered);
      }
      pst.execute();
      pst.close();
      id = DatabaseUtils.getCurrVal(db, "ad_category_code_seq", id);
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

  public int update(Connection db) throws SQLException {
    if (this.getId() == -1) {
      throw new SQLException("ID was not specified");
    }
    if (!isValid()) {
      return -1;
    }
    int resultCount = 0;
    int i = 0;
    PreparedStatement pst = db.prepareStatement(
        "UPDATE ad_category SET " +
            " project_category_id = ?" +
            ", item_name = ?" +
            ", logo_id = ?" +
            ", level = ?" +
            ", enabled = ?" +
            " WHERE code = ? ");
    DatabaseUtils.setInt(pst, ++i, projectCategoryId);
    pst.setString(++i, itemName);
    DatabaseUtils.setInt(pst, ++i, logoId);
    pst.setInt(++i, level);
    pst.setBoolean(++i, enabled);
    pst.setInt(++i, id);
    resultCount = pst.executeUpdate();
    pst.close();
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
    if (commit) {
      db.setAutoCommit(false);
    }

    //Delete the associated badges
    adList = new AdList();
    adList.setCategoryId(this.id);
    adList.buildList(db);
    for (Ad ad : adList) {
      ad.delete(db);
    }
    try {
      //Delete the actual badge category
      PreparedStatement pst = db.prepareStatement(
          "DELETE FROM ad_category " +
              "WHERE code = ? ");
      pst.setInt(1, id);
      recordCount = pst.executeUpdate();
      pst.close();

      //Deleting files
      FileItemList files = new FileItemList();
      files.setLinkModuleId(Constants.AD_CATEGORY_FILES);
      files.setLinkItemId(this.id);
      files.buildList(db);
      files.delete(db, basePath);

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
      errors.put("actionError", "Ad Category could not be deleted because it no longer exists.");
      return false;
    } else {
      return true;
    }
  }


  /**
   * Gets the valid attribute of the Ad object
   *
   * @return The valid value
   */
  public boolean isValid() {
    if (itemName == null || itemName.trim().equals("")) {
      errors.put("itemNameError", "Required field");
    }
    return !hasErrors();
  }

  public FileItemList retrieveFiles(Connection db) throws SQLException {
    FileItemList files = new FileItemList();
    files.setLinkModuleId(Constants.AD_CATEGORY_FILES);
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
        int adCategoryLogoId = this.getLogoId();
        this.setLogoId(-1);
        this.update(db);

        FileItem fileItem = new FileItem(db, adCategoryLogoId);
        fileItem.delete(db, basePath);
      }
      FileItemList.convertTempFiles(db, Constants.AD_CATEGORY_FILES, userId, this.getId(), attachmentList);
      FileItemList files = this.retrieveFiles(db);
      if (files.size() > 0) {
        this.setLogoId(files.get(0).getId());
        this.update(db);
      }
    }
  }

}
