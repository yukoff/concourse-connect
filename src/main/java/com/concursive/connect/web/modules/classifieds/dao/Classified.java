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
import com.concursive.commons.text.StringUtils;
import com.concursive.commons.web.mvc.beans.GenericBean;
import com.concursive.connect.Constants;
import com.concursive.connect.web.modules.common.social.rating.dao.Rating;
import com.concursive.connect.web.modules.common.social.viewing.utils.Viewing;
import com.concursive.connect.web.modules.documents.dao.FileItemList;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;

import java.sql.*;
import java.util.ArrayList;

/**
 * Represents a Classified Ad
 *
 * @author Kailash Bhoopalam
 * @version $Id$
 * @created May 21, 2008
 */
public class Classified extends GenericBean {

  public static final String TABLE = "project_classified";
  public static final String PRIMARY_KEY = "classified_id";
  public static final int DEFAULT_IMAGE_WIDTH = 320;
  public static final int DEFAULT_IMAGE_HEIGHT = 240;
  public static final long EXPIRATION_TIME_PERIOD = 1000L * 60 * 60 * 24 * 45; // 45 days

  private int id = -1;
  private int projectCategoryId = -1;
  private int categoryId = -1;
  private int projectId = -1;
  private String title = null;
  private String description = null;

  private double amount = 0.0;
  private String amountCurrency = null;
  private Timestamp publishDate = null;
  private Timestamp expirationDate = null;

  private boolean enabled = true;
  private int readCount = 0;
  private Timestamp readDate = null;
  private int ratingCount = 0;
  private int ratingValue = 0;
  private double ratingAvg = 0.0;
  private int modifiedBy = -1;
  private int enteredBy = -1;
  private Timestamp entered = null;
  private Timestamp modified = null;

  private FileItemList files = null;
  private String attachmentList = null;

  /**
   * Constructor for the Classified object
   */
  public Classified() {
  }


  /**
   * Constructor for the Classified object
   *
   * @param rs Description of Parameter
   * @throws SQLException Description of Exception
   */
  public Classified(ResultSet rs) throws SQLException {
    buildRecord(rs);
  }


  /**
   * Constructor for the Classified object
   *
   * @param db     Description of the Parameter
   * @param thisId Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public Classified(Connection db, int thisId) throws SQLException {
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
            "FROM project_classified " +
            "WHERE classified_id = ? ");

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
   * Sets the Id attribute of the Classified object
   *
   * @param tmp The new Id value
   */
  public void setId(int tmp) {
    this.id = tmp;
  }


  /**
   * Sets the id attribute of the Classified object
   *
   * @param tmp The new id value
   */
  public void setId(String tmp) {
    this.id = Integer.parseInt(tmp);
  }


  /**
   * Gets the Id attribute of the Classified object
   *
   * @return The Id value
   */
  public int getId() {
    return id;
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
  public void setProjectCategoryId(String projectCategoryId) {
    this.projectCategoryId = Integer.parseInt(projectCategoryId);
  }


  /**
   * @param projectCategoryId the projectCategoryId to set
   */
  public void setProjectCategoryId(int projectCategoryId) {
    this.projectCategoryId = projectCategoryId;
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
  public void setCategoryId(String categoryId) {
    this.categoryId = Integer.parseInt(categoryId);
  }


  /**
   * @param classifiedCategoryId the classifiedCategoryId to set
   */
  public void setCategoryId(int classifiedCategoryId) {
    this.categoryId = classifiedCategoryId;
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


  public Project getProject() {
    if (projectId != -1) {
      return ProjectUtils.loadProject(projectId);
    }
    return null;
  }

  /**
   * @return the title
   */
  public String getTitle() {
    return title;
  }


  /**
   * @param title the title to set
   */
  public void setTitle(String title) {
    this.title = title;
  }


  /**
   * @return the description
   */
  public String getDescription() {
    return description;
  }


  /**
   * @param description the description to set
   */
  public void setDescription(String description) {
    this.description = description;
  }


  /**
   * @return the amount
   */
  public double getAmount() {
    return amount;
  }


  /**
   * @param amount the amount to set
   */
  public void setAmount(String amount) {
    this.amount = Double.parseDouble(amount);
  }


  /**
   * @param amount the amount to set
   */
  public void setAmount(double amount) {
    this.amount = amount;
  }


  /**
   * @return the amountCurrency
   */
  public String getAmountCurrency() {
    return amountCurrency;
  }


  /**
   * @param amountCurrency the amountCurrency to set
   */
  public void setAmountCurrency(String amountCurrency) {
    this.amountCurrency = amountCurrency;
  }


  /**
   * @return the publishDate
   */
  public Timestamp getPublishDate() {
    return publishDate;
  }


  /**
   * @param publishDate the publishDate to set
   */
  public void setPublishDate(String publishDate) {
    this.publishDate = DatabaseUtils.parseTimestamp(publishDate);
  }


  /**
   * @param publishDate the publishDate to set
   */
  public void setPublishDate(Timestamp publishDate) {
    this.publishDate = publishDate;
  }

  public boolean isScheduledInFuture() {
    return publishDate != null && publishDate.getTime() > System.currentTimeMillis();
  }

  /**
   * @return the expirationDate
   */
  public Timestamp getExpirationDate() {
    return expirationDate;
  }


  /**
   * @param expirationDate the publishDate to set
   */
  public void setExpirationDate(String expirationDate) {
    this.expirationDate = DatabaseUtils.parseTimestamp(expirationDate);
  }


  /**
   * @param expirationDate the expirationDate to set
   */
  public void setExpirationDate(Timestamp expirationDate) {
    this.expirationDate = expirationDate;
  }

  public boolean isExpired() {
    return expirationDate != null && expirationDate.getTime() <= System.currentTimeMillis();
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
  public void setEnabled(String enabled) {
    this.enabled = DatabaseUtils.parseBoolean(enabled);
  }


  /**
   * @param enabled the enabled to set
   */
  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }


  /**
   * @return the readCount
   */
  public int getReadCount() {
    return readCount;
  }


  /**
   * @param readCount the readCount to set
   */
  public void setReadCount(String readCount) {
    this.readCount = Integer.parseInt(readCount);
  }


  /**
   * @param readCount the readCount to set
   */
  public void setReadCount(int readCount) {
    this.readCount = readCount;
  }


  /**
   * @return the readDate
   */
  public Timestamp getReadDate() {
    return readDate;
  }

  public void setReadDate(String tmp) {
    this.readDate = DatabaseUtils.parseTimestamp(tmp);
  }

  /**
   * @param readDate the readDate to set
   */
  public void setReadDate(Timestamp readDate) {
    this.readDate = readDate;
  }


  /**
   * @return the ratingCount
   */
  public int getRatingCount() {
    return ratingCount;
  }


  /**
   * @param ratingCount the ratingCount to set
   */
  public void setRatingCount(String ratingCount) {
    this.ratingCount = Integer.parseInt(ratingCount);
  }

  /**
   * @param ratingCount the ratingCount to set
   */
  public void setRatingCount(int ratingCount) {
    this.ratingCount = ratingCount;
  }


  /**
   * @return the ratingValue
   */
  public int getRatingValue() {
    return ratingValue;
  }


  /**
   * @param ratingValue the ratingValue to set
   */
  public void setRatingValue(String ratingValue) {
    this.ratingValue = Integer.parseInt(ratingValue);
  }


  /**
   * @param ratingValue the ratingValue to set
   */
  public void setRatingValue(int ratingValue) {
    this.ratingValue = ratingValue;
  }


  /**
   * @return the ratingAvg
   */
  public double getRatingAvg() {
    return ratingAvg;
  }


  /**
   * @param ratingAvg the ratingAvg to set
   */
  public void setRatingAvg(String ratingAvg) {
    this.ratingAvg = Double.parseDouble(ratingAvg);
  }


  /**
   * @param ratingAvg the ratingAvg to set
   */
  public void setRatingAvg(double ratingAvg) {
    this.ratingAvg = ratingAvg;
  }


  /**
   * @return the modifiedBy
   */
  public int getModifiedBy() {
    return modifiedBy;
  }


  /**
   * @param modifiedBy the modifiedBy to set
   */
  public void setModifiedBy(String modifiedBy) {
    this.modifiedBy = Integer.parseInt(modifiedBy);
  }


  /**
   * @param modifiedBy the modifiedBy to set
   */
  public void setModifiedBy(int modifiedBy) {
    this.modifiedBy = modifiedBy;
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
  public void setEnteredBy(String enteredBy) {
    this.enteredBy = Integer.parseInt(enteredBy);
  }


  /**
   * @param enteredBy the enteredBy to set
   */
  public void setEnteredBy(int enteredBy) {
    this.enteredBy = enteredBy;
  }


  /**
   * @return the entered
   */
  public Timestamp getEntered() {
    return entered;
  }


  /**
   * @param tmp the entered to set
   */
  public void setEntered(String tmp) {
    this.entered = DatabaseUtils.parseTimestamp(tmp);
  }


  /**
   * @param entered the entered to set
   */
  public void setEntered(Timestamp entered) {
    this.entered = entered;
  }


  /**
   * @return the modified
   */
  public Timestamp getModified() {
    return modified;
  }


  /**
   * @param modified the modified to set
   */
  public void setModified(Timestamp modified) {
    this.modified = modified;
  }


  public void setModified(String tmp) {
    this.modified = DatabaseUtils.parseTimestamp(tmp);
  }

  public FileItemList getFiles() {
    return files;
  }

  public boolean hasFiles() {
    return (files != null && files.size() > 0);
  }

  public String getAttachmentList() {
    return attachmentList;
  }

  public void setAttachmentList(String attachmentList) {
    this.attachmentList = attachmentList;
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
          "INSERT INTO project_classified " +
              "(" + (id > -1 ? "classified_id, " : "") +
              "project_category_id, " +
              "classified_category_id, " +
              "project_id, " +
              "title, " +
              "description, " +
              "amount, " +
              "amount_currency, " +
              "publish_date, " +
              "expiration_date, " +
              "enabled, " +
              "read_count, " +
              "read_date, " +
              "rating_count, " +
              "rating_value, " +
              "rating_avg, " +
              "enteredby, " +
              "modifiedby ");
      if (entered != null) {
        sql.append(", entered ");
      }
      if (modified != null) {
        sql.append(", modified ");
      }
      sql.append(") VALUES (");
      if (id > -1) {
        sql.append("?, ");
      }
      sql.append("?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?  ");
      if (entered != null) {
        sql.append(",? ");
      }
      if (modified != null) {
        sql.append(",? ");
      }
      sql.append(")");
      int i = 0;
      PreparedStatement pst = db.prepareStatement(sql.toString());
      if (id > -1) {
        pst.setInt(++i, id);
      }
      DatabaseUtils.setInt(pst, ++i, projectCategoryId);
      DatabaseUtils.setInt(pst, ++i, categoryId);
      DatabaseUtils.setInt(pst, ++i, projectId);
      pst.setString(++i, title);
      pst.setString(++i, description);
      DatabaseUtils.setDouble(pst, ++i, amount);
      pst.setString(++i, amountCurrency);
      pst.setTimestamp(++i, publishDate);
      pst.setTimestamp(++i, expirationDate);
      pst.setBoolean(++i, enabled);
      DatabaseUtils.setInt(pst, ++i, readCount);
      pst.setTimestamp(++i, readDate);
      DatabaseUtils.setInt(pst, ++i, ratingCount);
      DatabaseUtils.setInt(pst, ++i, ratingValue);
      DatabaseUtils.setDouble(pst, ++i, ratingAvg);

      pst.setInt(++i, enteredBy);
      pst.setInt(++i, modifiedBy);
      if (entered != null) {
        pst.setTimestamp(++i, entered);
      }
      if (modified != null) {
        pst.setTimestamp(++i, modified);
      }
      pst.execute();
      pst.close();
      id = DatabaseUtils.getCurrVal(db, "project_classified_classified_id_seq", id);
      if (attachmentList != null) {
        FileItemList.convertTempFiles(db, Constants.PROJECT_CLASSIFIEDS_FILES, this.getModifiedBy(), id, attachmentList);
      }
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
    int resultCount = 0;
    boolean commit = db.getAutoCommit();
    try {
      if (commit) {
        db.setAutoCommit(false);
      }
      buildFiles(db);
      files.delete(db, basePath);
      Viewing.delete(db, id, TABLE, PRIMARY_KEY);
      Rating.delete(db, id, TABLE, PRIMARY_KEY);

      //Delete the actual classified
      PreparedStatement pst = db.prepareStatement(
          "DELETE FROM project_classified " +
              "WHERE classified_id = ? ");
      pst.setInt(1, id);
      resultCount = pst.executeUpdate();
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
    if (resultCount == 0) {
      errors.put("actionError", "Classified could not be deleted because it no longer exists.");
      return false;
    } else {
      return true;
    }
  }


  public int update(Connection db) throws SQLException {
    return update(db, false);
  }

  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  public int update(Connection db, boolean override) throws SQLException {
    if (this.getId() == -1) {
      throw new SQLException("ID was not specified");
    }
    if (!isValid()) {
      return -1;
    }
    int resultCount = 0;

    StringBuffer sql = new StringBuffer();
    sql.append(
        "UPDATE project_classified " +
            "SET project_category_id = ?, " +
            "classified_category_id = ?,  " +
            "project_id = ?,  " +
            "title = ?, " +
            "description = ?, " +
            "amount = ?, " +
            "amount_currency = ?, " +
            "publish_date = ?, " +
            "expiration_date = ?, " +
            "enabled = ?, " +
            "modifiedby = ?, " +
            "modified = CURRENT_TIMESTAMP " +
            "WHERE classified_id = ? ");
    if (!override) {
      sql.append("AND modified = ? ");
    }
    int i = 0;
    PreparedStatement pst = db.prepareStatement(sql.toString());
    DatabaseUtils.setInt(pst, ++i, projectCategoryId);
    DatabaseUtils.setInt(pst, ++i, categoryId);
    DatabaseUtils.setInt(pst, ++i, projectId);
    pst.setString(++i, title);
    pst.setString(++i, description);
    DatabaseUtils.setDouble(pst, ++i, amount);
    pst.setString(++i, amountCurrency);
    pst.setTimestamp(++i, publishDate);
    pst.setTimestamp(++i, expirationDate);
    pst.setBoolean(++i, enabled);
    pst.setInt(++i, this.getModifiedBy());
    pst.setInt(++i, this.getId());
    if (!override) {
      pst.setTimestamp(++i, modified);
    }
    resultCount = pst.executeUpdate();
    pst.close();
    if (attachmentList != null) {
      FileItemList.convertTempFiles(db, Constants.PROJECT_CLASSIFIEDS_FILES, this.getModifiedBy(), id, attachmentList);
    }
    return resultCount;
  }


  /**
   * Description of the Method
   *
   * @param rs Description of Parameter
   * @throws SQLException Description of Exception
   */
  private void buildRecord(ResultSet rs) throws SQLException {
    id = rs.getInt("classified_id");
    projectCategoryId = DatabaseUtils.getInt(rs, "project_category_id");
    categoryId = DatabaseUtils.getInt(rs, "classified_category_id");
    projectId = DatabaseUtils.getInt(rs, "project_id");
    title = rs.getString("title");
    description = rs.getString("description");
    amount = DatabaseUtils.getDouble(rs, "amount", 0.0);
    amountCurrency = rs.getString("amount_currency");
    publishDate = rs.getTimestamp("publish_date");
    expirationDate = rs.getTimestamp("expiration_date");
    entered = rs.getTimestamp("entered");
    enteredBy = DatabaseUtils.getInt(rs, "enteredby");
    modified = rs.getTimestamp("modified");
    modifiedBy = DatabaseUtils.getInt(rs, "modifiedby");
    enabled = rs.getBoolean("enabled");
    readCount = DatabaseUtils.getInt(rs, "read_count", 0);
    readDate = rs.getTimestamp("read_date");
    ratingCount = DatabaseUtils.getInt(rs, "rating_count", 0);
    ratingValue = DatabaseUtils.getInt(rs, "rating_value", 0);
    ratingAvg = DatabaseUtils.getDouble(rs, "rating_avg", 0);
  }


  /**
   * The following fields depend on a timezone preference
   *
   * @return The timeZoneParams value
   */
  public static ArrayList getTimeZoneParams() {
    ArrayList thisList = new ArrayList();
    thisList.add("publishDate");
    thisList.add("expirationDate");
    return thisList;
  }


  /**
   * Gets the numberParams attribute of the Project class
   *
   * @return The numberParams value
   */
  public static ArrayList getNumberParams() {
    ArrayList thisList = new ArrayList();
    thisList.add("amount");
    return thisList;
  }

  /**
   * Gets the valid attribute of the Project object
   *
   * @return The valid value
   */
  private boolean isValid() {
    if (!StringUtils.hasText(title)) {
      errors.put("titleError", "Title is required");
    }
    if (!StringUtils.hasText(description)) {
      errors.put("descriptionError", "Description is required");
    }

    if (publishDate == null && expirationDate != null) {
      errors.put("publishDateError", "Publish Date is required if expiration date is entered");
    }
    if (publishDate != null && expirationDate == null) {
      errors.put("expirationDateError", "Expiration Date is required if publish date is entered");
    }
    if (publishDate != null && expirationDate != null && publishDate.getTime() > expirationDate.getTime()) {
      errors.put("publishDateError", "Publish Date must be before expiration date");
    }
    if (publishDate != null && expirationDate != null) {
      long publishDateTimeInMillis = publishDate.getTime();
      long expirationDateTimeInMillis = expirationDate.getTime();

      long timeDifference = expirationDateTimeInMillis - publishDateTimeInMillis;
      if (timeDifference > EXPIRATION_TIME_PERIOD) {
        errors.put("expirationDateError", "Expiration date must be no more than 45 days from publish date");
      }
    }

    return !hasErrors();
  }

  /**
   * @param db
   * @param userId
   * @return
   * @throws Exception
   */
  public boolean clone(Connection db, int userId) throws Exception {
    // @todo clone files
    this.setId(-1);
    this.setEnteredBy(userId);
    this.setEntered((Timestamp) null);
    this.setModifiedBy(userId);
    this.setModified((Timestamp) null);
    this.setPublishDate((Timestamp) null);
    this.setExpirationDate((Timestamp) null);
    this.setRatingCount(0);
    this.setRatingValue(0);
    this.setRatingAvg(0.0);
    this.setReadCount(0);
    this.setReadDate((Timestamp) null);
    return this.insert(db);
  }

  public void buildFiles(Connection db) throws SQLException {
    files = new FileItemList();
    files.setLinkModuleId(Constants.PROJECT_CLASSIFIEDS_FILES);
    files.setLinkItemId(this.getId());
    files.buildList(db);
  }

}
