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
 * Tag rollup by user
 *
 * @author Kailash Bhoopalam
 * @created July 17, 2008
 */
public class UserTagLog extends GenericBean {

  private int userId = -1;
  private int linkModuleId = -1;
  private int linkItemId = -1;
  private String tag = null;
  private Timestamp tagDate = null;

  public UserTagLog() {
  }

  public UserTagLog(ResultSet rs) throws SQLException {
    buildRecord(rs);
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
          " INSERT INTO user_tag_log ( " +
              " user_id ," +
              " link_module_id , " +
              " link_item_id , " +
              " tag )" +
              " VALUES ( ?, ?, ?, ? ) ");
      int i = 0;
      PreparedStatement pst = db.prepareStatement(sql.toString());
      DatabaseUtils.setInt(pst, ++i, userId);
      DatabaseUtils.setInt(pst, ++i, linkModuleId);
      DatabaseUtils.setInt(pst, ++i, linkItemId);
      pst.setString(++i, tag.trim().toLowerCase());
      pst.execute();
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


  public boolean delete(Connection db) throws SQLException {
    int recordCount = 0;
    boolean commit = db.getAutoCommit();
    try {
      if (commit) {
        db.setAutoCommit(false);
      }
      //The candidate key is (user_id, link_module_id, link_item_id, tag)
      PreparedStatement pst = db.prepareStatement(
          " DELETE FROM user_tag_log " +
              " WHERE user_id = ? " +
              " AND link_module_id = ? " +
              " AND link_item_id = ? " +
              " AND tag = ? ");
      int i = 0;
      pst.setInt(++i, userId);
      pst.setInt(++i, linkModuleId);
      pst.setInt(++i, linkItemId);
      pst.setString(++i, tag);
      recordCount = pst.executeUpdate();
      pst.close();
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
      errors.put("actionError", "Tag could not be deleted because it no longer exists.");
      return false;
    } else {
      return true;
    }
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


  private void buildRecord(ResultSet rs) throws SQLException {
    userId = rs.getInt("user_id");
    linkModuleId = DatabaseUtils.getInt(rs, "link_module_id");
    linkItemId = DatabaseUtils.getInt(rs, "link_item_id");
    tag = rs.getString("tag");
    tagDate = rs.getTimestamp("tag_date");
  }
}