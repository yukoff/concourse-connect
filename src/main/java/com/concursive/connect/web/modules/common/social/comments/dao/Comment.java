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

package com.concursive.connect.web.modules.common.social.comments.dao;

import com.concursive.commons.db.DatabaseUtils;
import com.concursive.commons.web.mvc.beans.GenericBean;
import com.concursive.connect.web.modules.common.social.rating.dao.Rating;

import java.sql.*;

/**
 * Represents a comment
 *
 * @author Kailash Bhoopalam
 * @version $Id$
 * @created November 25, 2008
 */
public class Comment extends GenericBean {

  private int id = -1;
  protected int linkItemId = -1;
  private String comment = null;
  private Timestamp entered = null;
  private int enteredBy = -1;
  private Timestamp modified = null;
  private int modifiedBy = -1;
  private Timestamp closed = null;
  private int closedBy = -1;
  private int ratingCount = 0;
  private int ratingValue = 0;
  private double ratingAvg = 0.0;
  private int inappropriateCount = 0;

  protected String tableName = null;
  protected String uniqueFieldId = null;

  public static String PRIMARY_KEY = "comment_id";

  public Comment() {
  }

  public Comment(ResultSet rs) throws SQLException {
    buildRecord(rs);
  }

  public Comment(Connection db, int id, int wikiId) throws SQLException {
    this.linkItemId = wikiId;
    queryRecord(db, id);
  }

  protected void queryRecord(Connection db, int commentId) throws SQLException {
    StringBuffer sql = new StringBuffer();
    sql.append(
        "SELECT c.* " +
            "FROM " + tableName + " c " +
            "WHERE comment_id = ? ");
    if (linkItemId > -1) {
      sql.append("AND " + uniqueFieldId + "= ? ");
    }
    PreparedStatement pst = db.prepareStatement(sql.toString());
    int i = 0;
    pst.setInt(++i, commentId);
    if (linkItemId > -1) {
      pst.setInt(++i, linkItemId);
    }
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      buildRecord(rs);
    }
    rs.close();
    pst.close();
    if (id == -1) {
      throw new SQLException("Comment record not found.");
    }
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

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
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

  public int getEnteredBy() {
    return enteredBy;
  }

  public void setEnteredBy(int enteredBy) {
    this.enteredBy = enteredBy;
  }

  public void setEnteredBy(String enteredBy) {
    this.enteredBy = Integer.parseInt(enteredBy);
  }

  public Timestamp getModified() {
    return modified;
  }

  public void setModified(Timestamp modified) {
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

  public void setModifiedBy(String modifiedBy) {
    this.modifiedBy = Integer.parseInt(modifiedBy);
  }

  public Timestamp getClosed() {
    return closed;
  }

  public void setClosed(Timestamp closed) {
    this.closed = closed;
  }

  public void setClosed(String tmp) {
    this.closed = DatabaseUtils.parseTimestamp(tmp);
  }

  public int getClosedBy() {
    return closedBy;
  }

  public void setClosedBy(int closedBy) {
    this.closedBy = closedBy;
  }

  public void setClosedBy(String closedBy) {
    this.closedBy = Integer.parseInt(closedBy);
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
  public void setRatingCount(int ratingCount) {
    this.ratingCount = ratingCount;
  }

  public void setRatingCount(String ratingCount) {
    this.ratingCount = Integer.parseInt(ratingCount);
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
  public void setRatingValue(int ratingValue) {
    this.ratingValue = ratingValue;
  }

  public void setRatingValue(String ratingValue) {
    this.ratingValue = Integer.parseInt(ratingValue);
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
  public void setRatingAvg(double ratingAvg) {
    this.ratingAvg = ratingAvg;
  }

  public void setRatingAvg(String ratingAvg) {
    this.ratingAvg = Double.parseDouble(ratingAvg);
  }

  /**
   * @return the inappropriateCount
   */
  public int getInappropriateCount() {
    return inappropriateCount;
  }


  /**
   * @param inappropriateCount the inappropriateCount to set
   */
  public void setInappropriateCount(int inappropriateCount) {
    this.inappropriateCount = inappropriateCount;
  }

  public void setInappropriateCount(String inappropriateCount) {
    this.inappropriateCount = Integer.parseInt(inappropriateCount);
  }

  /**
   * @return the linkItemId
   */
  protected int getLinkItemId() {
    return linkItemId;
  }

  /**
   * @param linkItemId the linkItemId to set
   */
  protected void setLinkItemId(int linkItemId) {
    this.linkItemId = linkItemId;
  }

  /**
   * @return the tableName
   */
  protected String getTableName() {
    return tableName;
  }

  /**
   * @param tableName the tableName to set
   */
  protected void setTableName(String tableName) {
    this.tableName = tableName;
  }

  /**
   * @return the uniqueId
   */
  protected String getUniqueFieldId() {
    return uniqueFieldId;
  }

  /**
   * @param uniqueId the uniqueId to set
   */
  protected void setUniqueFieldId(String uniqueFieldId) {
    this.uniqueFieldId = uniqueFieldId;
  }

  protected void buildRecord(ResultSet rs) throws SQLException {
    id = rs.getInt("comment_id");
    linkItemId = rs.getInt(uniqueFieldId);
    comment = rs.getString("comment");
    entered = rs.getTimestamp("entered");
    enteredBy = rs.getInt("enteredby");
    closed = rs.getTimestamp("closed");
    closedBy = rs.getInt("closedby");
    ratingCount = DatabaseUtils.getInt(rs, "rating_count", 0);
    ratingValue = DatabaseUtils.getInt(rs, "rating_value", 0);
    ratingAvg = DatabaseUtils.getDouble(rs, "rating_avg", 0.0);
    inappropriateCount = DatabaseUtils.getInt(rs, "inappropriate_count", 0);
  }

  public boolean isValid() {
    if (linkItemId == -1) {
      errors.put("actionError", "ID not specified");
    }
    if (comment == null || comment.trim().equals("")) {
      errors.put("commentError", "Required field");
    }
    return !hasErrors();
  }

  protected boolean insert(Connection db) throws SQLException {
    if (!isValid()) {
      return false;
    }
    boolean commit = db.getAutoCommit();
    try {
      if (commit) {
        db.setAutoCommit(false);
      }
      StringBuffer sql = new StringBuffer();
      sql.append(
          "INSERT INTO " + tableName +
              "(" + uniqueFieldId + ", comment, ");
      if (entered != null) {
        sql.append("entered, ");
      }
      if (modified != null) {
        sql.append("modified, ");
      }
      if (closed != null) {
        sql.append("closed, ");
      }
      sql.append(
          "enteredby, modifiedby, closedby) ");
      sql.append("VALUES (?, ?, ");
      if (entered != null) {
        sql.append("?, ");
      }
      if (modified != null) {
        sql.append("?, ");
      }
      if (closed != null) {
        sql.append("?, ");
      }
      sql.append("?, ?, ?) ");
      int i = 0;
      //Insert the comment
      PreparedStatement pst = db.prepareStatement(sql.toString());
      pst.setInt(++i, linkItemId);
      pst.setString(++i, comment);
      if (entered != null) {
        pst.setTimestamp(++i, entered);
      }
      if (modified != null) {
        pst.setTimestamp(++i, entered);
      }
      if (closed != null) {
        pst.setTimestamp(++i, closed);
      }
      pst.setInt(++i, enteredBy);
      pst.setInt(++i, modifiedBy);
      DatabaseUtils.setInt(pst, ++i, closedBy);
      pst.execute();
      pst.close();
      id = DatabaseUtils.getCurrVal(db, tableName + "_comment_id_seq", -1);
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

  protected int update(Connection db, Comment originalComment) throws SQLException {
    int resultCount = 0;
    boolean commit = db.getAutoCommit();
    try {
      if (commit) {
        db.setAutoCommit(false);
      }
      if (this.getId() == -1) {
        throw new SQLException("ID was not specified");
      }
      if (!isValid()) {
        return -1;
      }
      int i = 0;
      PreparedStatement pst = db.prepareStatement(
          "UPDATE " + tableName +
              "SET comment = ?, " +
              "modifiedby = ?, modified = CURRENT_TIMESTAMP " +
              "WHERE comment_id = ? " +
              "AND modified = ? ");
      pst.setString(++i, comment);
      pst.setInt(++i, this.getModifiedBy());
      pst.setInt(++i, id);
      pst.setTimestamp(++i, modified);
      resultCount = pst.executeUpdate();
      pst.close();
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
    return resultCount;
  }

  protected void delete(Connection db) throws SQLException {
    Rating.delete(db, this.getId(), tableName, Comment.PRIMARY_KEY);

    PreparedStatement pst = db.prepareStatement(
        "DELETE FROM " + tableName +
            " WHERE comment_id = ? ");
    int i = 0;
    pst.setInt(++i, id);
    pst.executeUpdate();
    pst.close();
  }

  protected void close(Connection db) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "UPDATE " + tableName +
            "SET closed = CURRENT_TIMESTAMP, " +
            "closedby = ? " +
            "WHERE comment_id = ? ");
    int i = 0;
    pst.setInt(++i, closedBy);
    pst.setInt(++i, id);
    pst.executeUpdate();
    pst.close();
  }
}
