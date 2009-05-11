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

package com.concursive.connect.web.modules.common.social.viewing.utils;

import com.concursive.commons.db.DatabaseUtils;

import java.sql.*;

/**
 * Handles viewing an object by a user
 *
 * @author matt rajkowski
 * @created April 25, 2008
 */
public class Viewing {

  public static void save(Connection db, int userId, int objectId, String table, String uniqueField) throws SQLException {
    if (objectId == -1) {
      throw new SQLException("objectId must be set and cannot be -1");
    }
    if (userId < -1) {
      userId = -1;
    }
    boolean commit = false;
    try {
      commit = db.getAutoCommit();
      if (commit) {
        db.setAutoCommit(false);
      }
      PreparedStatement pst = null;
      // Perform an insert (guests are allowed)
      pst = db.prepareStatement(
          "INSERT INTO " + table + "_view " +
              "(" + uniqueField + ", user_id) VALUES (?, ?)");
      pst.setInt(1, objectId);
      DatabaseUtils.setInt(pst, 2, userId);
      pst.execute();
      pst.close();
      // Update the object count and value
      saveSummary(db, objectId, table, uniqueField);
      if (commit) {
        db.commit();
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

  public static void saveSummary(Connection db, int objectId, String table, String uniqueField) throws SQLException {
    if (objectId == -1) {
      throw new SQLException("objectId must be set and cannot be -1");
    }
    // Update the object count and value
    PreparedStatement pst = db.prepareStatement(
        "UPDATE " + table + " " +
            "SET read_count = read_count + 1, " +
            "read_date = CURRENT_TIMESTAMP " +
            "WHERE " + uniqueField + " = ? ");
    pst.setInt(1, objectId);
    pst.execute();
    pst.close();
  }

  public static void saveNew(Connection db, int userId, int objectId, String table, String uniqueField, Timestamp modified) throws SQLException {
    if (userId < -1) {
      userId = -1;
    }
    PreparedStatement pst = db.prepareStatement(
        "SELECT 1 FROM " + table + "_view " +
            "WHERE " + uniqueField + " = ? " +
            "AND user_id = ? " +
            "AND view_date > ? "
    );
    pst.setInt(1, objectId);
    DatabaseUtils.setInt(pst, 2, userId);
    pst.setTimestamp(3, modified);
    ResultSet rs = pst.executeQuery();
    boolean doSave = false;
    if (rs.next()) {
      doSave = true;
    }
    rs.close();
    pst.close();
    if (doSave) {
      save(db, userId, objectId, table, uniqueField);
    }
  }

  public static void delete(Connection db, int objectId, String table, String uniqueField) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "DELETE FROM " + table + "_view WHERE " + uniqueField + " = ? ");
    pst.setInt(1, objectId);
    pst.execute();
    pst.close();
  }

  public static void deleteByProject(Connection db, int projectId, String table, String uniqueField) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "DELETE FROM " + table + "_view " +
            "WHERE " + uniqueField + " IN (SELECT " + uniqueField + "FROM " + table + " WHERE project_id = ?)");
    pst.setInt(1, projectId);
    pst.execute();
    pst.close();
  }

}