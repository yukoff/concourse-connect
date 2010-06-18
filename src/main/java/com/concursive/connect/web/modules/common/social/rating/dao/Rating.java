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

package com.concursive.connect.web.modules.common.social.rating.dao;

import com.concursive.commons.db.DatabaseUtils;
import com.concursive.connect.Constants;
import com.concursive.connect.web.modules.common.social.rating.beans.RatingBean;
import com.concursive.connect.web.modules.profile.dao.Project;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.*;

/**
 * Handles rating an object by a user
 *
 * @author matt rajkowski
 * @version $Id$
 * @created March 19, 2008
 */
public class Rating {

  private static Log LOG = LogFactory.getLog(Rating.class);

  public static final int INAPPROPRIATE_COMMENT = -2;
  public static final String PRIMARY_KEY_RECORD_ID = "record_id";
  public static final String PRIMARY_KEY_RATING_ID = "rating_id";

  private int id = -1;
  private int objectId = -1;
  private String uniqueField = null;
  private int rating = 0;
  private boolean inappropriate = false;
  private int enteredby = -1;
  private Timestamp entered = null;
  private int projectId = -1;

  public Rating() {
  }

  public Rating(ResultSet rs, String primaryKeyField, String uniqueField, boolean hasInappropriate) throws SQLException {
    buildRecord(rs, primaryKeyField, uniqueField, hasInappropriate);
  }


  /**
   * @return the id
   */
  public int getId() {
    return id;
  }

  /**
   * @param id the recordId to set
   */
  public void setId(int id) {
    this.id = id;
  }

  public void setId(String id) {
    this.id = Integer.parseInt(id);
  }

  /**
   * @return the objectId
   */
  public int getObjectId() {
    return objectId;
  }

  /**
   * @param objectId the objectId to set
   */
  public void setObjectId(int objectId) {
    this.objectId = objectId;
  }

  public void setObjectId(String objectId) {
    this.objectId = Integer.parseInt(objectId);
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
   * @return the rating
   */
  public int getRating() {
    return rating;
  }

  /**
   * @param rating the rating to set
   */
  public void setRating(int rating) {
    this.rating = rating;
  }

  public void setRating(String rating) {
    this.rating = Integer.parseInt(rating);
  }

  /**
   * @return the inappropriate
   */
  public boolean getInappropriate() {
    return inappropriate;
  }

  /**
   * @param inappropriate the inappropriate to set
   */
  public void setInappropriate(boolean inappropriate) {
    this.inappropriate = inappropriate;
  }

  public void setInappropriate(String inappropriate) {
    this.inappropriate = DatabaseUtils.parseBoolean(inappropriate);
  }

  /**
   * @return the enteredby
   */
  public int getEnteredby() {
    return enteredby;
  }

  /**
   * @param enteredby the enteredby to set
   */
  public void setEnteredby(int enteredby) {
    this.enteredby = enteredby;
  }

  public void setEnteredby(String enteredby) {
    this.enteredby = Integer.parseInt(enteredby);
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

  public void setEntered(String entered) {
    this.entered = DatabaseUtils.parseTimestamp(entered);
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
  public void setProjectId(int projectId) {
    this.projectId = projectId;
  }

  public void setProjectId(String projectId) {
    this.projectId = Integer.parseInt(projectId);
  }

  /**
   * @param rs          the resultset to build the record from
   * @param uniqueField
   */
  private void buildRecord(ResultSet rs, String primaryKeyField, String uniqueField, boolean hasInappropriate) throws SQLException {
    id = rs.getInt(primaryKeyField);
    objectId = rs.getInt(uniqueField);
    rating = DatabaseUtils.getInt(rs, "rating");
    if (hasInappropriate) {
      inappropriate = rs.getBoolean("inappropriate");
    }
    entered = rs.getTimestamp("entered");
    enteredby = DatabaseUtils.getInt(rs, "enteredby");
    projectId = DatabaseUtils.getInt(rs, "project_id");
  }

  public static int queryUserRating(Connection db, int userId, int objectId, String table, String uniqueField) throws SQLException {
    int existingVote = -1;
    PreparedStatement pst = db.prepareStatement(
        "SELECT rating FROM " + table + "_rating WHERE " + uniqueField + " = ? AND enteredby = ? ");
    pst.setInt(1, objectId);
    pst.setInt(2, userId);
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      existingVote = rs.getInt("rating");
    }
    rs.close();
    pst.close();
    return existingVote;
  }

  public static int queryObjectRatingCount(Connection db, int objectId, String table, String uniqueField) throws SQLException {
    int count = -1;
    PreparedStatement pst = db.prepareStatement(
        "SELECT rating_count FROM " + table + " WHERE " + uniqueField + " = ? ");
    pst.setInt(1, objectId);
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      count = rs.getInt("rating_count");
    }
    rs.close();
    pst.close();
    return count;
  }

  public static int queryObjectRatingValue(Connection db, int objectId, String table, String uniqueField) throws SQLException {
    int count = -1;
    PreparedStatement pst = db.prepareStatement(
        "SELECT rating_value FROM " + table + " " +
            "WHERE " + uniqueField + " = ? ");
    pst.setInt(1, objectId);
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      count = rs.getInt("rating_value");
    }
    rs.close();
    pst.close();
    return count;
  }

  public static synchronized RatingBean save(Connection db, int userId, int projectId, int objectId, String vote, String table, String uniqueField, int setInappropriateColumn) throws SQLException {
    boolean commit = false;
    try {
      commit = db.getAutoCommit();
      if (commit) {
        db.setAutoCommit(false);
      }
      // Determine the current value
      int existingVote = queryUserRating(db, userId, objectId, table, uniqueField);
      int newVote = Integer.parseInt(vote);
      PreparedStatement pst = null;
      ResultSet rs = null;
      // Determine if an update, insert, or delete is required
      if (existingVote == -1) {
        // Perform an insert
        pst = db.prepareStatement(
            "INSERT INTO " + table + "_rating " +
                "(project_id, " + (Project.PRIMARY_KEY.equals(uniqueField) ? "" : uniqueField + ", ") + ((setInappropriateColumn != Constants.UNDEFINED) ? "inappropriate, " : "") + "rating, enteredby) " +
                "VALUES (?, " + (Project.PRIMARY_KEY.equals(uniqueField) ? "" : "?, ") + ((setInappropriateColumn != Constants.UNDEFINED) ? "?, " : "") + "?, ?)");
        int i = 0;
        pst.setInt(++i, projectId);
        if (!Project.PRIMARY_KEY.equals(uniqueField)) {
          pst.setInt(++i, objectId);
        }
        if (setInappropriateColumn != Constants.UNDEFINED) {
          pst.setBoolean(++i, (setInappropriateColumn == Constants.TRUE));
        }
        pst.setInt(++i, newVote);
        pst.setInt(++i, userId);
        pst.execute();
        pst.close();
      } else if (existingVote != newVote) {
        // Try an update
        pst = db.prepareStatement(
            "UPDATE " + table + "_rating " +
                "SET rating = ?, entered = " + DatabaseUtils.getCurrentTimestamp(db) + " " +
                ((setInappropriateColumn != Constants.UNDEFINED) ? ", inappropriate = ? " : "") +
                "WHERE " + uniqueField + " = ? AND enteredby = ? ");
        int i = 0;
        pst.setInt(++i, newVote);
        if (setInappropriateColumn != Constants.UNDEFINED) {
          pst.setBoolean(++i, (setInappropriateColumn == Constants.TRUE));
        }
        pst.setInt(++i, objectId);
        pst.setInt(++i, userId);
        pst.execute();
        pst.close();
      }
      if (existingVote != newVote) {
        // Update the object count and value
        pst = db.prepareStatement(
            "UPDATE " + table + " " +
                "SET rating_count = rating_count + ?, rating_value = rating_value + ?, " +
                "rating_avg = ((rating_value + ?) / (rating_count + ?)) " +
                "WHERE " + uniqueField + " = ? ");
        int i = 0;
        if (existingVote == -1) {
          if (newVote == INAPPROPRIATE_COMMENT) {
            //rating count is incremented, but no change in rating value, therefore, rating average decreases
            pst.setInt(++i, 1);
            pst.setInt(++i, 0);
            pst.setInt(++i, 0);
            pst.setInt(++i, 1);
          } else {
            pst.setInt(++i, 1);
            pst.setInt(++i, newVote);
            pst.setInt(++i, newVote);
            pst.setInt(++i, 1);
          }
        } else {
          if (newVote == INAPPROPRIATE_COMMENT || existingVote == INAPPROPRIATE_COMMENT) {
            if (newVote == INAPPROPRIATE_COMMENT) {
              //The effects of the previous rating are reversed.
              pst.setInt(++i, 0);
              pst.setInt(++i, (-1) * existingVote);
              pst.setInt(++i, (-1) * existingVote);
              pst.setInt(++i, 0);
            } else if (existingVote == INAPPROPRIATE_COMMENT) {
              //The new rating by the user is recorded,
              //as an existing inappropriate comment was never considered towards rating value, no additional math is required
              pst.setInt(++i, 0);
              pst.setInt(++i, newVote);
              pst.setInt(++i, newVote);
              pst.setInt(++i, 0);
            }
          } else {
            pst.setInt(++i, 0);
            pst.setInt(++i, newVote - existingVote);
            pst.setInt(++i, newVote - existingVote);
            pst.setInt(++i, 0);
          }
        }
        pst.setInt(++i, objectId);
        //System.out.println(pst);
        pst.execute();
        pst.close();
      }

      if (setInappropriateColumn != Constants.UNDEFINED) {
        int inappropriateCount = 0;
        pst = db.prepareStatement(
            "SELECT count(*) AS ic " +
                "FROM " + table + "_rating " +
                "WHERE " + uniqueField + " = ? AND inappropriate = ?");
        int i = 0;
        pst.setInt(++i, objectId);
        pst.setBoolean(++i, true);
        rs = pst.executeQuery();
        if (rs.next()) {
          inappropriateCount = rs.getInt("ic");
        }
        rs.close();
        pst.close();

        pst = db.prepareStatement(
            "UPDATE " + table + " " +
                "SET  inappropriate_count = ? " +
                "WHERE " + uniqueField + " = ? ");
        i = 0;
        pst.setInt(++i, inappropriateCount);
        pst.setInt(++i, objectId);
        pst.execute();
        pst.close();
      }

      // Retrieve the values
      pst = db.prepareStatement(
          "SELECT rating_count, rating_value " +
              "FROM " + table + " WHERE " + uniqueField + " = ?");
      pst.setInt(1, objectId);
      rs = pst.executeQuery();
      int count = 0;
      int value = 0;
      if (rs.next()) {
        count = rs.getInt("rating_count");
        value = rs.getInt("rating_value");
      }
      rs.close();
      pst.close();
      if (commit) {
        db.commit();
      }
      // Share the rating bean
      RatingBean rating = new RatingBean();
      rating.setItemId(objectId);
      rating.setCount(count);
      rating.setValue(value);
      return rating;
    } catch (Exception e) {
      if (commit) {
        db.rollback();
      }
      LOG.error("save", e);
      throw new SQLException(e.getMessage());
    } finally {
      if (commit) {
        db.setAutoCommit(true);
      }
    }
  }

  /**
   * Deletes references from the specified table when the object is being deleted
   *
   * @param db
   * @param objectId
   * @param table
   * @param uniqueField
   * @throws SQLException
   */
  public static void delete(Connection db, int objectId, String table, String uniqueField) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "DELETE FROM " + table + "_rating " +
            "WHERE " + uniqueField + " = ? ");
    pst.setInt(1, objectId);
    pst.execute();
    pst.close();
  }

  public static void deleteByProject(Connection db, int projectId, String table, String uniqueField) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "DELETE FROM " + table + "_rating " +
            "WHERE " + uniqueField + " IN (SELECT " + uniqueField + "FROM " + table + " WHERE project_id = ?)");
    pst.setInt(1, projectId);
    pst.execute();
    pst.close();
  }

  /**
   * Deletes just a specific user's rating, and updates the parent table with a proper calculation
   *
   * @param db
   * @param userId
   * @param objectId
   * @param table
   * @param uniqueField
   * @throws SQLException
   */
  public static synchronized void delete(Connection db, int userId, int objectId, String table, String uniqueField) throws SQLException {
    boolean commit = false;
    try {
      commit = db.getAutoCommit();
      if (commit) {
        db.setAutoCommit(false);
      }

      // Get the project's rating
      int ratingCount = queryObjectRatingCount(db, objectId, table, uniqueField);

      // Get the user's rating
      int thisRating = queryUserRating(db, userId, objectId, table, uniqueField);

      // Delete the user's rating
      PreparedStatement pst = db.prepareStatement(
          "DELETE FROM " + table + "_rating " +
              "WHERE " + uniqueField + " = ? " +
              "AND enteredby = ? ");
      pst.setInt(1, objectId);
      pst.setInt(2, userId);
      int deleteCount = pst.executeUpdate();
      pst.close();

      if (deleteCount > 0 && thisRating != INAPPROPRIATE_COMMENT) {
        // Update the parent table's rating information
        // NOTE: make sure not to divide by 0
        pst = db.prepareStatement(
            "UPDATE " + table + " " +
                "SET rating_count = rating_count - ?, rating_value = rating_value - ?, " +
                (ratingCount == 0 ?
                    "rating_avg = 0 " :
                    "rating_avg = ((rating_value - ?) / (rating_count - ?)) ") +
                "WHERE " + uniqueField + " = ? ");
        int i = 0;
        pst.setInt(++i, 1);
        pst.setInt(++i, thisRating);
        if (ratingCount > 1) {
          pst.setInt(++i, thisRating);
          pst.setInt(++i, 1);
        }
        pst.execute();
        pst.close();
      }
    } catch (Exception e) {
      if (commit) {
        db.rollback();
      }
      throw new SQLException(e.getMessage());
    } finally {
      if (commit) {
        db.setAutoCommit(true);
      }
    }
  }
}