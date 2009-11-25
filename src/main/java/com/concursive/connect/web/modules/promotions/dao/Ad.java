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
import com.concursive.connect.web.modules.common.social.rating.dao.Rating;
import com.concursive.connect.web.modules.common.social.viewing.utils.Viewing;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;

import java.sql.*;
import java.util.ArrayList;

/**
 * @author Lorraine Bittner
 * @version $Id$
 * @created May 12, 2008
 */
public class Ad extends GenericBean {

  public static final String TABLE = "ad";
  public static final String PRIMARY_KEY = "ad_id";
  public static final long EXPIRATION_TIME_PERIOD = 1000L * 60 * 60 * 24 * 45; // 45 days

  private int id = -1;
  private int projectCategoryId = -1;
  private int categoryId = -1;
  private int projectId = -1;
  private String heading = null;
  private String briefDescription1 = null;
  private String briefDescription2 = null;
  private String content = null;
  private String webPage = null;
  private String destinationUrl = null;
  private java.sql.Timestamp publishDate = null;
  private java.sql.Timestamp expirationDate = null;
  private java.sql.Timestamp entered = null;
  private int enteredBy = -1;
  private java.sql.Timestamp modified = null;
  private int modifiedBy = -1;
  private boolean enabled = false;
  private int readCount = 0;
  private java.sql.Timestamp readDate = null;
  private int ratingCount = 0;
  private int ratingValue = 0;
  private double ratingAvg = 0.0;

  /**
   * Constructor for the Ad object
   */
  public Ad() {
  }

  /**
   * Constructor for the Ad object
   *
   * @param rs - ResultSet
   * @throws SQLException
   */
  public Ad(ResultSet rs) throws SQLException {
    buildRecord(rs);
  }

  /**
   * Constructor for the Ad object
   *
   * @param db - Connection
   * @param id Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public Ad(Connection db, int id) throws SQLException {
    queryRecord(db, id);
  }


  /**
   * Constructor for the Ad object
   *
   * @param db        Description of the Parameter
   * @param id        Description of the Parameter
   * @param projectId Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public Ad(Connection db, int id, int projectId) throws SQLException {
    this.projectId = projectId;
    queryRecord(db, id);
  }

  /**
   * @return
   */
  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public void setId(String tmp) {
    this.id = Integer.parseInt(tmp);
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

  public int getCategoryId() {
    return categoryId;
  }

  public void setCategoryId(int categoryId) {
    this.categoryId = categoryId;
  }

  public void setCategoryId(String tmp) {
    this.categoryId = Integer.parseInt(tmp);
  }

  public int getProjectId() {
    return projectId;
  }

  public void setProjectId(int projectId) {
    this.projectId = projectId;
  }

  public void setProjectId(String tmp) {
    this.projectId = Integer.parseInt(tmp);
  }

  public Project getProject() {
    if (projectId != -1) {
      return ProjectUtils.loadProject(projectId);
    }
    return null;
  }

  public String getHeading() {
    return heading;
  }

  public void setHeading(String heading) {
    this.heading = heading;
  }

  public String getContent() {
    return content;
  }

  public String getContentHeader() {
    if (content.trim().length() > 180) {
      return (content.substring(0, 180) + "...");
    } else {
      return getContent();
    }
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getWebPage() {
    return webPage;
  }

  public void setWebPage(String webPage) {
    this.webPage = webPage;
  }

  public java.sql.Timestamp getPublishDate() {
    return publishDate;
  }

  public void setPublishDate(java.sql.Timestamp publishDate) {
    this.publishDate = publishDate;
  }

  public void setPublishDate(String tmp) {
    this.publishDate = DatabaseUtils.parseTimestamp(tmp);
  }

  public boolean isScheduledInFuture() {
    return publishDate != null && publishDate.getTime() > System.currentTimeMillis();
  }

  public java.sql.Timestamp getExpirationDate() {
    return expirationDate;
  }

  public void setExpirationDate(java.sql.Timestamp expirationDate) {
    this.expirationDate = expirationDate;
  }

  public void setExpirationDate(String tmp) {
    this.expirationDate = DatabaseUtils.parseTimestamp(tmp);
  }

  public boolean isExpired() {
    return expirationDate != null && expirationDate.getTime() <= System.currentTimeMillis();
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

  public int getEnteredBy() {
    return enteredBy;
  }

  public void setEnteredBy(int enteredBy) {
    this.enteredBy = enteredBy;
  }

  public void setEnteredBy(String tmp) {
    this.enteredBy = Integer.parseInt(tmp);
  }

  public java.sql.Timestamp getModified() {
    return modified;
  }

  public void setModified(java.sql.Timestamp modified) {
    this.modified = modified;
  }

  public void setModified(String tmp) {
    this.modified = DatabaseUtils.parseTimestamp(tmp);
  }

  public int getModifiedBy() {
    return modifiedBy;
  }

  public void setModifiedBy(int modifiedBy) {
    this.modifiedBy = modifiedBy;
  }

  public void setModifiedBy(String tmp) {
    this.modifiedBy = Integer.parseInt(tmp);
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

  public int getReadCount() {
    return readCount;
  }

  public void setReadCount(int readCount) {
    this.readCount = readCount;
  }

  public void setReadCount(String tmp) {
    this.readCount = Integer.parseInt(tmp);
  }

  public java.sql.Timestamp getReadDate() {
    return readDate;
  }

  public void setReadDate(String tmp) {
    this.readDate = DatabaseUtils.parseTimestamp(tmp);
  }

  public void setReadDate(java.sql.Timestamp readDate) {
    this.readDate = readDate;
  }

  public int getRatingCount() {
    return ratingCount;
  }

  public void setRatingCount(int ratingCount) {
    this.ratingCount = ratingCount;
  }

  public void setRatingCount(String tmp) {
    this.ratingCount = Integer.parseInt(tmp);
  }

  public int getRatingValue() {
    return ratingValue;
  }

  public void setRatingValue(int ratingValue) {
    this.ratingValue = ratingValue;
  }

  public void setRatingValue(String tmp) {
    this.ratingValue = Integer.parseInt(tmp);
  }

  public double getRatingAvg() {
    return ratingAvg;
  }

  public void setRatingAvg(double ratingAvg) {
    this.ratingAvg = ratingAvg;
  }

  public void setRatingAvg(String tmp) {
    this.ratingAvg = Double.parseDouble(tmp);
  }

  public String getBriefDescription1() {
    return briefDescription1;
  }

  public void setBriefDescription1(String briefDescription1) {
    this.briefDescription1 = briefDescription1;
  }

  public String getBriefDescription2() {
    return briefDescription2;
  }

  public void setBriefDescription2(String briefDescription2) {
    this.briefDescription2 = briefDescription2;
  }

  public String getDestinationUrl() {
    return destinationUrl;
  }

  public void setDestinationUrl(String destinationUrl) {
    this.destinationUrl = destinationUrl;
  }

  public void queryRecord(Connection db, int adId) throws SQLException {
    StringBuffer sql = new StringBuffer();
    sql.append(
        "SELECT a.* " +
            "FROM ad a " +
            "WHERE ad_id = ? ");
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
      throw new SQLException(String.format("Ad record not found for id %s.", adId));
    }
  }

  /**
   * Populates this record from a database result set
   *
   * @param rs Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  protected void buildRecord(ResultSet rs) throws SQLException {
    id = rs.getInt("ad_id");
    projectCategoryId = DatabaseUtils.getInt(rs, "project_category_id");
    categoryId = DatabaseUtils.getInt(rs, "ad_category_id");
    projectId = DatabaseUtils.getInt(rs, "project_id");
    heading = rs.getString("heading");
    content = rs.getString("content");
    webPage = rs.getString("web_page");
    publishDate = rs.getTimestamp("publish_date");
    expirationDate = rs.getTimestamp("expiration_date");
    entered = rs.getTimestamp("entered");
    enteredBy = rs.getInt("enteredby");
    modified = rs.getTimestamp("modified");
    modifiedBy = rs.getInt("modifiedby");
    enabled = rs.getBoolean("enabled");
    readCount = DatabaseUtils.getInt(rs, "read_count", 0);
    readDate = rs.getTimestamp("read_date");
    ratingCount = DatabaseUtils.getInt(rs, "rating_count", 0);
    ratingValue = DatabaseUtils.getInt(rs, "rating_value", 0);
    ratingAvg = rs.getDouble("rating_avg");
    briefDescription1 = rs.getString("brief_description_1");
    briefDescription2 = rs.getString("brief_description_2");
    destinationUrl = rs.getString("destination_url");
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
        "INSERT INTO ad " +
            "( project_id " +
            (id > -1 ? ", ad_id " : "") +
            ", project_category_id" +
            ", ad_category_id" +
            ", heading" +
            ", brief_description_1" +
            ", brief_description_2" +
            ", content" +
            ", web_page" +
            ", destination_url" +
            ", enabled");
    if (entered != null) {
      sql.append(", entered ");
    }
    if (modified != null) {
      sql.append(", modified ");
    }
    sql.append(
        ", enteredby" +
            ", modifiedby" +
            ", publish_date" +
            ", expiration_date" +
            ", rating_count" +
            ", rating_value" +
            ", rating_avg" +
            ", read_count" +
            ", read_date" +
            " ) ");
    sql.append("VALUES (? ");//project_id
    if (id > -1) {
      sql.append(", ?");//ad_id
    }
    sql.append(", ?" +//project_category_id
        ", ?" +//ad_category_id
        ", ?" +//heading
        ", ?" +
        ", ?" +
        ", ?" +//content
        ", ?" +//web_page
        ", ?" +
        ", ?");//enabled
    if (entered != null) {
      sql.append(", ?");//entered
    }
    if (modified != null) {
      sql.append(", ?");//modified
    }
    sql.append(", ?" +//enteredBy
        ", ?" +//modifiedBy
        ", ?" +//publish_date
        ", ?" +//expiration_date
        ", ?" +//rating_count
        ", ?" +//rating_value
        ", ?" +//rating_avg
        ", ?" +//read_count
        ", ?" +//read_date
        ") ");
    int i = 0;
    try {
      //Insert the ad
      PreparedStatement pst = db.prepareStatement(sql.toString());
      DatabaseUtils.setInt(pst, ++i, projectId);
      if (id > -1) {
        pst.setInt(++i, id);
      }
      DatabaseUtils.setInt(pst, ++i, projectCategoryId);
      DatabaseUtils.setInt(pst, ++i, categoryId);
      pst.setString(++i, heading);
      pst.setString(++i, briefDescription1);
      pst.setString(++i, briefDescription2);
      pst.setString(++i, content);
      pst.setString(++i, webPage);
      pst.setString(++i, destinationUrl);
      pst.setBoolean(++i, enabled);
      if (entered != null) {
        pst.setTimestamp(++i, entered);
      }
      if (modified != null) {
        pst.setTimestamp(++i, modified);
      }

      pst.setInt(++i, enteredBy);
      pst.setInt(++i, modifiedBy);
      DatabaseUtils.setTimestamp(pst, ++i, publishDate);
      DatabaseUtils.setTimestamp(pst, ++i, expirationDate);
      pst.setInt(++i, ratingCount);
      pst.setInt(++i, ratingValue);
      pst.setDouble(++i, ratingAvg);
      pst.setInt(++i, readCount);
      pst.setTimestamp(++i, readDate);
      pst.execute();
      pst.close();
      id = DatabaseUtils.getCurrVal(db, "ad_ad_id_seq", id);
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
        "UPDATE ad SET " +
            " project_id = ?" +
            ", project_category_id = ?" +
            ", ad_category_id = ?" +
            ", heading = ?" +
            ", brief_description_1 = ?" +
            ", brief_description_2 = ?" +
            ", content = ?" +
            ", web_page = ?" +
            ", destination_url = ?" +
            ", modifiedby = ?" +
            ", modified = CURRENT_TIMESTAMP" +
            ", publish_date = ?" +
            ", expiration_date = ?" +
            ", enabled = ?" +
            " WHERE ad_id = ? AND modified = ? ");
    DatabaseUtils.setInt(pst, ++i, projectId);
    DatabaseUtils.setInt(pst, ++i, projectCategoryId);
    DatabaseUtils.setInt(pst, ++i, categoryId);
    pst.setString(++i, heading);
    pst.setString(++i, briefDescription1);
    pst.setString(++i, briefDescription2);
    pst.setString(++i, content);
    pst.setString(++i, webPage);
    pst.setString(++i, destinationUrl);
    pst.setInt(++i, this.getModifiedBy());
    DatabaseUtils.setTimestamp(pst, ++i, publishDate);
    DatabaseUtils.setTimestamp(pst, ++i, expirationDate);
    pst.setBoolean(++i, enabled);
    pst.setInt(++i, id);
    pst.setTimestamp(++i, modified);
    resultCount = pst.executeUpdate();
    pst.close();
    return resultCount;
  }

  public boolean delete(Connection db) throws SQLException {
    if (this.getId() < 0) {
      throw new SQLException("ID was not specified");
    }
    int resultCount = 0;
    boolean commit = db.getAutoCommit();
    try {
      if (commit) {
        db.setAutoCommit(false);
      }

      Viewing.delete(db, id, TABLE, PRIMARY_KEY);
      Rating.delete(db, id, TABLE, PRIMARY_KEY);

      PreparedStatement pst = db.prepareStatement(
          "DELETE FROM ad " +
              "WHERE ad_id = ? ");
      pst.setInt(1, this.getId());
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
      errors.put("actionError", "Ad could not be deleted because it no longer exists.");
      return false;
    } else {
      return true;
    }
  }

  /**
   * The following fields depend on a timezone preference
   *
   * @return The dateTimeParams value
   */
  public static ArrayList<String> getTimeZoneParams() {
    ArrayList<String> thisList = new ArrayList<String>();
    thisList.add("publishDate");
    thisList.add("expirationDate");
    return thisList;
  }

  /**
   * Gets the valid attribute of the Ad object
   *
   * @return The valid value
   */
  public boolean isValid() {
    if (!StringUtils.hasText(heading)) {
      errors.put("headingError", "Required field");
    }
    if (!StringUtils.hasText(content)) {
      errors.put("contentError", "Required field");
    }
    if (publishDate != null && expirationDate != null && publishDate.getTime() > expirationDate.getTime()) {
      errors.put("publishDateError", "Publish Date must be before expiration date");
    }
    if (publishDate == null && expirationDate != null) {
      errors.put("publishDateError", "Publish Date is required if expiration date is entered");
    }
    if (publishDate != null && expirationDate == null) {
      errors.put("expirationDateError", "Expiration Date is required if publish date is entered");
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
}
