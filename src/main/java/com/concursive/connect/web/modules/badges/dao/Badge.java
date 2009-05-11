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
import com.concursive.connect.cache.utils.CacheUtils;
import com.concursive.connect.web.modules.common.social.rating.dao.Rating;
import com.concursive.connect.web.modules.common.social.viewing.utils.Viewing;
import com.concursive.connect.web.modules.documents.dao.FileItem;
import com.concursive.connect.web.modules.documents.dao.FileItemList;

import java.sql.*;
import java.util.ArrayList;

/**
 * Represents a Badge in iTeam
 *
 * @author Kailash Bhoopalam
 * @version $Id$
 * @created May 12, 2008
 */
public class Badge extends GenericBean {

  public static final String TABLE = "badge";
  public static final String PRIMARY_KEY = "badge_id";

  private int id = -1;
  private int categoryId = -1;
  private String title = null;
  private String description = null;
  private int logoId = -1;
  private String email1 = null;
  private String email2 = null;
  private String email3 = null;
  private String businessPhone = null;
  private String businessPhoneExt = null;
  private String webPage = null;
  private String addrline1 = null;
  private String addrline2 = null;
  private String addrline3 = null;
  private String state = null;
  private String city = null;
  private String country = null;
  private String postalCode = null;
  private double latitude = 0.0;
  private double longitude = 0.0;
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
  private boolean systemAssigned = false;
  private int systemConstant = -1;

  private FileItem logo = null;
  private String attachmentList = null;


  /**
   * Constructor for the Badge object
   */
  public Badge() {
  }


  /**
   * Constructor for the Badge object
   *
   * @param rs Description of Parameter
   * @throws SQLException Description of Exception
   */
  public Badge(ResultSet rs) throws SQLException {
    buildRecord(rs);
  }


  /**
   * Constructor for the Badge object
   *
   * @param db     Description of the Parameter
   * @param thisId Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public Badge(Connection db, int thisId) throws SQLException {
    queryRecord(db, thisId);
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
            "FROM badge b " +
            "WHERE b.badge_id = ? ");

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
   * Sets the Id attribute of the Badge object
   *
   * @param tmp The new Id value
   */
  public void setId(int tmp) {
    this.id = tmp;
  }


  /**
   * Sets the id attribute of the Badge object
   *
   * @param tmp The new id value
   */
  public void setId(String tmp) {
    this.id = Integer.parseInt(tmp);
  }


  /**
   * Gets the Id attribute of the Badge object
   *
   * @return The Id value
   */
  public int getId() {
    return id;
  }


  /**
   * @return the badgeCategoryId
   */
  public int getCategoryId() {
    return categoryId;
  }


  /**
   * @param categoryId the badgeCategoryId to set
   */
  public void setCategoryId(String categoryId) {
    this.categoryId = Integer.parseInt(categoryId);
  }


  /**
   * @param badgeCategoryId the badgeCategoryId to set
   */
  public void setCategoryId(int badgeCategoryId) {
    this.categoryId = badgeCategoryId;
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
   * @return the logoId
   */
  public int getLogoId() {
    return logoId;
  }


  /**
   * @param logoId the logoId to set
   */
  public void setLogoId(String logoId) {
    this.logoId = Integer.parseInt(logoId);
  }


  /**
   * @param logoId the logoId to set
   */
  public void setLogoId(int logoId) {
    this.logoId = logoId;
  }


  /**
   * @return the email1
   */
  public String getEmail1() {
    return email1;
  }


  /**
   * @param email1 the email1 to set
   */
  public void setEmail1(String email1) {
    this.email1 = email1;
  }


  /**
   * @return the email2
   */
  public String getEmail2() {
    return email2;
  }


  /**
   * @param email2 the email2 to set
   */
  public void setEmail2(String email2) {
    this.email2 = email2;
  }


  /**
   * @return the email3
   */
  public String getEmail3() {
    return email3;
  }


  /**
   * @param email3 the email3 to set
   */
  public void setEmail3(String email3) {
    this.email3 = email3;
  }


  /**
   * @return the businessPhone
   */
  public String getBusinessPhone() {
    return businessPhone;
  }


  /**
   * @param businessPhone the businessPhone to set
   */
  public void setBusinessPhone(String businessPhone) {
    this.businessPhone = businessPhone;
  }


  /**
   * @return the businessPhoneExt
   */
  public String getBusinessPhoneExt() {
    return businessPhoneExt;
  }


  /**
   * @param businessPhoneExt the businessPhoneExt to set
   */
  public void setBusinessPhoneExt(String businessPhoneExt) {
    this.businessPhoneExt = businessPhoneExt;
  }


  /**
   * @return the webPage
   */
  public String getWebPage() {
    return webPage;
  }


  /**
   * @param webPage the webPage to set
   */
  public void setWebPage(String webPage) {
    this.webPage = webPage;
  }


  /**
   * @return the addrline1
   */
  public String getAddrline1() {
    return addrline1;
  }


  /**
   * @param addrline1 the addrline1 to set
   */
  public void setAddrline1(String addrline1) {
    this.addrline1 = addrline1;
  }


  /**
   * @return the addrline2
   */
  public String getAddrline2() {
    return addrline2;
  }


  /**
   * @param addrline2 the addrline2 to set
   */
  public void setAddrline2(String addrline2) {
    this.addrline2 = addrline2;
  }


  /**
   * @return the addrline3
   */
  public String getAddrline3() {
    return addrline3;
  }


  /**
   * @param addrline3 the addrline3 to set
   */
  public void setAddrline3(String addrline3) {
    this.addrline3 = addrline3;
  }


  /**
   * @return the state
   */
  public String getState() {
    return state;
  }


  /**
   * @param state the state to set
   */
  public void setState(String state) {
    this.state = state;
  }


  /**
   * @return the city
   */
  public String getCity() {
    return city;
  }


  /**
   * @param city the city to set
   */
  public void setCity(String city) {
    this.city = city;
  }


  /**
   * @return the country
   */
  public String getCountry() {
    return country;
  }


  /**
   * @param country the country to set
   */
  public void setCountry(String country) {
    this.country = country;
  }


  /**
   * @return the postalCode
   */
  public String getPostalCode() {
    return postalCode;
  }


  /**
   * @param postalCode the postalCode to set
   */
  public void setPostalCode(String postalCode) {
    this.postalCode = postalCode;
  }


  /**
   * @return the latitude
   */
  public double getLatitude() {
    return latitude;
  }


  /**
   * @param latitude the latitude to set
   */
  public void setLatitude(String latitude) {
    this.latitude = Double.parseDouble(latitude);
  }


  /**
   * @param latitude the latitude to set
   */
  public void setLatitude(double latitude) {
    this.latitude = latitude;
  }


  /**
   * @return the longitude
   */
  public double getLongitude() {
    return longitude;
  }


  /**
   * @param longitude the longitude to set
   */
  public void setLongitude(String longitude) {
    this.longitude = Double.parseDouble(longitude);
  }


  /**
   * @param longitude the longitude to set
   */
  public void setLongitude(double longitude) {
    this.longitude = longitude;
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

  /**
   * @return the systemAssigned
   */
  public boolean getSystemAssigned() {
    return systemAssigned;
  }


  /**
   * @param systemAssigned the systemAssigned to set
   */
  public void setSystemAssigned(String systemAssigned) {
    this.systemAssigned = DatabaseUtils.parseBoolean(systemAssigned);
  }


  /**
   * @param systemAssigned the systemAssigned to set
   */
  public void setSystemAssigned(boolean systemAssigned) {
    this.systemAssigned = systemAssigned;
  }

  /**
   * @return the enteredBy
   */
  public int getSystemConstant() {
    return systemConstant;
  }


  /**
   * @param systemConstant the systemConstant to set
   */
  public void setSystemConstant(String systemConstant) {
    this.systemConstant = Integer.parseInt(systemConstant);
  }


  /**
   * @param systemConstant the systemConstant to set
   */
  public void setSystemConstant(int systemConstant) {
    this.systemConstant = systemConstant;
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
          "INSERT INTO badge " +
              "(" + (id > -1 ? "badge_id, " : "") +
              "badge_category_id, " +
              "title, " +
              "description, " +
              "logo_id, " +
              "email1, " +
              "email2, " +
              "email3, " +
              "business_phone, " +
              "business_phone_ext, " +
              "web_page, " +
              "addrline1, " +
              "addrline2, " +
              "addrline3, " +
              "city, " +
              "state, " +
              "country, " +
              "postalcode, " +
              "latitude, " +
              "longitude, " +
              "enabled, " +
              "read_count, " +
              "read_date, " +
              "rating_count, " +
              "rating_value, " +
              "rating_avg, " +
              "enteredby, " +
              "modifiedby," +
              "system_assigned," +
              "system_constant ");
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
      sql.append("?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?  ");
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
      DatabaseUtils.setInt(pst, ++i, categoryId);
      pst.setString(++i, title);
      pst.setString(++i, description);
      DatabaseUtils.setInt(pst, ++i, logoId);
      pst.setString(++i, email1);
      pst.setString(++i, email2);
      pst.setString(++i, email3);
      pst.setString(++i, businessPhone);
      pst.setString(++i, businessPhoneExt);
      pst.setString(++i, webPage);
      pst.setString(++i, addrline1);
      pst.setString(++i, addrline2);
      pst.setString(++i, addrline3);
      pst.setString(++i, city);
      pst.setString(++i, state);
      pst.setString(++i, country);
      pst.setString(++i, postalCode);
      pst.setDouble(++i, latitude);
      pst.setDouble(++i, longitude);
      pst.setBoolean(++i, enabled);
      DatabaseUtils.setInt(pst, ++i, readCount);
      pst.setTimestamp(++i, readDate);
      DatabaseUtils.setInt(pst, ++i, ratingCount);
      DatabaseUtils.setInt(pst, ++i, ratingValue);
      DatabaseUtils.setDouble(pst, ++i, ratingAvg);

      pst.setInt(++i, enteredBy);
      pst.setInt(++i, modifiedBy);
      pst.setBoolean(++i, systemAssigned);
      DatabaseUtils.setInt(pst, ++i, systemConstant);
      if (entered != null) {
        pst.setTimestamp(++i, entered);
      }
      if (modified != null) {
        pst.setTimestamp(++i, modified);
      }
      pst.execute();
      pst.close();
      id = DatabaseUtils.getCurrVal(db, "badge_badge_id_seq", id);

      if (commit) {
        db.commit();
      }
    } catch (Exception e) {
      e.printStackTrace(System.out);
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

      Viewing.delete(db, id, TABLE, PRIMARY_KEY);
      Rating.delete(db, id, TABLE, PRIMARY_KEY);

      //Delete badge project link
      PreparedStatement pst = db.prepareStatement(
          "DELETE FROM badgelink_project " +
              "WHERE badge_id = ? ");
      pst.setInt(1, this.getId());
      pst.execute();
      pst.close();

      //Delete the actual badge
      pst = db.prepareStatement(
          "DELETE FROM badge " +
              "WHERE badge_id = ? ");
      pst.setInt(1, id);
      recordCount = pst.executeUpdate();
      pst.close();

      FileItemList files = new FileItemList();
      files.setLinkModuleId(Constants.BADGE_FILES);
      files.setLinkItemId(id);
      files.buildList(db);
      files.delete(db, basePath);

      CacheUtils.invalidateValue(Constants.SYSTEM_BADGE_LIST_CACHE, id);
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
      errors.put("actionError", "Badge could not be deleted because it no longer exists.");
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
        "UPDATE badge " +
            "SET " +
            "badge_category_id = ?,  " +
            "title = ?, " +
            "description = ?, " +
            "logo_id = ?, " +
            "email1 = ?, " +
            "email2 = ?, " +
            "email3 = ?, " +
            "business_phone = ?, " +
            "business_phone_ext = ?, " +
            "web_page = ?, " +
            "addrline1 = ?, " +
            "addrline2 = ?, " +
            "addrline3 = ?, " +
            "city = ?, " +
            "state = ?, " +
            "country = ?, " +
            "postalcode = ?, " +
            "latitude = ?, " +
            "longitude = ?, " +
            "enabled = ?, " +
            "modifiedby = ?, " +
            "modified = CURRENT_TIMESTAMP, " +
            "system_assigned = ?, " +
            "system_constant = ? " +
            "WHERE badge_id = ? ");
    if (!override) {
      sql.append("AND modified = ? ");
    }
    int i = 0;
    PreparedStatement pst = db.prepareStatement(sql.toString());
    DatabaseUtils.setInt(pst, ++i, categoryId);
    pst.setString(++i, title);
    pst.setString(++i, description);
    DatabaseUtils.setInt(pst, ++i, logoId);
    pst.setString(++i, email1);
    pst.setString(++i, email2);
    pst.setString(++i, email3);
    pst.setString(++i, businessPhone);
    pst.setString(++i, businessPhoneExt);
    pst.setString(++i, webPage);
    pst.setString(++i, addrline1);
    pst.setString(++i, addrline2);
    pst.setString(++i, addrline3);
    pst.setString(++i, city);
    pst.setString(++i, state);
    pst.setString(++i, country);
    pst.setString(++i, postalCode);
    DatabaseUtils.setDouble(pst, ++i, latitude);
    DatabaseUtils.setDouble(pst, ++i, longitude);
    pst.setBoolean(++i, enabled);
    pst.setInt(++i, this.getModifiedBy());
    pst.setBoolean(++i, systemAssigned);
    DatabaseUtils.setInt(pst, ++i, systemConstant);
    pst.setInt(++i, this.getId());
    if (!override) {
      pst.setTimestamp(++i, modified);
    }
    resultCount = pst.executeUpdate();
    pst.close();
    CacheUtils.invalidateValue(Constants.SYSTEM_BADGE_LIST_CACHE, id);
    return resultCount;
  }


  /**
   * Description of the Method
   *
   * @param rs Description of Parameter
   * @throws SQLException Description of Exception
   */
  private void buildRecord(ResultSet rs) throws SQLException {
    id = rs.getInt("badge_id");
    categoryId = DatabaseUtils.getInt(rs, "badge_category_id");
    title = rs.getString("title");
    description = rs.getString("description");
    logoId = DatabaseUtils.getInt(rs, "logo_id");
    email1 = rs.getString("email1");
    email2 = rs.getString("email2");
    email3 = rs.getString("email3");
    businessPhone = rs.getString("business_phone");
    businessPhoneExt = rs.getString("business_phone_ext");
    webPage = rs.getString("web_page");
    addrline1 = rs.getString("addrline1");
    addrline2 = rs.getString("addrline2");
    addrline3 = rs.getString("addrline3");
    city = rs.getString("city");
    state = rs.getString("state");
    country = rs.getString("country");
    postalCode = rs.getString("postalcode");
    latitude = DatabaseUtils.getDouble(rs, "latitude", 0.0);
    longitude = DatabaseUtils.getDouble(rs, "longitude", 0.0);
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
    systemAssigned = rs.getBoolean("system_assigned");
    systemConstant = DatabaseUtils.getInt(rs, "system_constant");
  }


  /**
   * The following fields depend on a timezone preference
   *
   * @return The timeZoneParams value
   */
  public static ArrayList getTimeZoneParams() {
    ArrayList thisList = new ArrayList();
    return thisList;
  }


  /**
   * Gets the numberParams attribute of the Project class
   *
   * @return The numberParams value
   */
  public static ArrayList getNumberParams() {
    ArrayList thisList = new ArrayList();
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
    return !hasErrors();
  }


  public FileItemList retrieveFiles(Connection db) throws SQLException {
    FileItemList files = new FileItemList();
    files.setLinkModuleId(Constants.BADGE_FILES);
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
        int badgeLogoId = this.getLogoId();
        this.setLogoId(-1);
        this.update(db, true);

        FileItem fileItem = new FileItem(db, badgeLogoId);
        fileItem.delete(db, basePath);
      }
      FileItemList.convertTempFiles(db, Constants.BADGE_FILES, userId, this.getId(), attachmentList);
      FileItemList files = this.retrieveFiles(db);
      if (files.size() > 0) {
        this.setLogoId(files.get(0).getId());
        this.update(db, true);
      }
    }
  }

}

