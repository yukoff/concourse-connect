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

package com.concursive.connect.web.modules.common.social.tagging.dao;

import com.concursive.commons.db.DatabaseUtils;
import com.concursive.commons.web.mvc.beans.GenericBean;

import java.sql.*;

/**
 * A history item for a tag
 *
 * @author Kailash Bhoopalam
 * @created July 17, 2008
 */
public class TagLog extends GenericBean {

  private int userId = -1;
  private int linkItemId = -1;
  private String tag = null;
  private Timestamp tagDate = null;

  //helper attributes
  private int linkModuleId = -1;
  private String uniqueField = null;
  private String tableName = null;

  public TagLog() {
  }

  /**
   * @return the userId
   */
  public int getUserId() {
    return userId;
  }

  /**
   * @param userId the userId to set
   */
  public void setUserId(int userId) {
    this.userId = userId;
  }

  /**
   * @param userId the userId to set
   */
  public void setUserId(String userId) {
    this.userId = Integer.parseInt(userId);
  }

  /**
   * @return the linkModuleId
   */
  public int getLinkModuleId() {
    return linkModuleId;
  }

  /**
   * @param linkModuleId the linkModuleId to set
   */
  public void setLinkModuleId(int linkModuleId) {
    this.linkModuleId = linkModuleId;
  }

  /**
   * @param linkModuleId the linkModuleId to set
   */
  public void setLinkModuleId(String linkModuleId) {
    this.linkModuleId = Integer.parseInt(linkModuleId);
  }

  /**
   * @return the linkItemId
   */
  public int getLinkItemId() {
    return linkItemId;
  }

  /**
   * @param linkItemId the linkItemId to set
   */
  public void setLinkItemId(int linkItemId) {
    this.linkItemId = linkItemId;
  }

  /**
   * @param linkItemId the linkItemId to set
   */
  public void setLinkItemId(String linkItemId) {
    this.linkItemId = Integer.parseInt(linkItemId);
  }

  /**
   * @return the tag
   */
  public String getTag() {
    return tag;
  }

  /**
   * @param tag the tag to set
   */
  public void setTag(String tag) {
    this.tag = tag;
  }

  /**
   * @return the tagDate
   */
  public Timestamp getTagDate() {
    return tagDate;
  }

  /**
   * @param tagDate the tagDate to set
   */
  public void setTagDate(Timestamp tagDate) {
    this.tagDate = tagDate;
  }

  /**
   * @param tagDate the tagDate to set
   */
  public void setTagDate(String tagDate) {
    this.tagDate = DatabaseUtils.parseDateToTimestamp(tagDate);
  }

  /**
   * @return the uniqueField
   */
  public String getUniqueField() {
    return uniqueField;
  }

  /**
   * @param uniqueField the uniqueField to set
   */
  public void setUniqueField(String uniqueField) {
    this.uniqueField = uniqueField;
  }

  /**
   * @return the tableName
   */
  public String getTableName() {
    return tableName;
  }

  /**
   * @param tableName the tableName to set
   */
  public void setTableName(String tableName) {
    this.tableName = tableName;
  }

  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  public boolean queryRecord(Connection db) throws SQLException {
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
      //use candidate key for selection as primary key as synthetic primary key does not exist for the tables
      sql.append(
          " SELECT " + uniqueField +
              " user_id ," +
              " tag , " +
              " tag_date ) " +
              " FROM " + tableName + "_tag_log " +
              " WHERE " +
              uniqueField + " = ? " +
              " user_id = ? " +
              " tag = ? "
      );
      int i = 0;
      PreparedStatement pst = db.prepareStatement(sql.toString());
      DatabaseUtils.setInt(pst, ++i, linkItemId);
      DatabaseUtils.setInt(pst, ++i, userId);
      pst.setString(++i, tag);
      ResultSet rs = pst.executeQuery();
      buildRecord(rs);
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

  private boolean isValid() {
    if ("".equals(tag)) {
      errors.put("tagError", "Tag is required");
    }
    if (linkModuleId == -1) {
      errors.put("linkModuleIdError", "Module Id is required");
    }
    if (linkItemId == -1) {
      errors.put("linkItemIdError", "Item Id is required");
    }
    return !hasErrors();
  }


  public void buildRecord(ResultSet rs) throws SQLException {
    linkItemId = DatabaseUtils.getInt(rs, uniqueField);
    userId = rs.getInt("user_id");
    tag = rs.getString("tag");
    tagDate = rs.getTimestamp("tag_date");
  }
}