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
package com.concursive.connect.web.modules.webcast.dao;

import com.concursive.commons.db.DatabaseUtils;

import java.sql.*;

/**
 * A project's webcast live stream
 *
 * @author Ananth
 * @created May 3, 2010
 */
public class Webcast {
  public static final String TABLE = "project_webcast";
  public static final String PRIMARY_KEY = "webcast_id";

  private int id = -1;
  private int projectId = -1;
  private Timestamp entered = null;
  private int enteredBy = -1;
  private int ratingCount = 0;
  private int ratingValue = 0;
  private double ratingAvg = 0.0;
  private int inappropriateCount = 0;

  public int getInappropriateCount() {
    return inappropriateCount;
  }

  public void setInappropriateCount(int inappropriateCount) {
    this.inappropriateCount = inappropriateCount;
  }

  public void setInappropriateCount(String tmp) {
    this.inappropriateCount = Integer.parseInt(tmp);
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

  public int getProjectId() {
    return projectId;
  }

  public void setProjectId(int projectId) {
    this.projectId = projectId;
  }

  public void setProjectId(String tmp) {
    this.projectId = Integer.parseInt(tmp);
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

  public void setEnteredBy(String tmp) {
    this.enteredBy = Integer.parseInt(tmp);
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

  public Webcast() {}
  
  public Webcast(Connection db, int recordId) throws SQLException {
    queryRecord(db, recordId);
  }

  public void queryRecord(Connection db, int recordId) throws SQLException {
    StringBuffer sql = new StringBuffer();
    sql.append(
        "SELECT w.* " +
            "FROM project_webcast w " +
            "WHERE w.webcast_id = ? ");
    PreparedStatement pst = db.prepareStatement(sql.toString());
    int i = 0;
    pst.setInt(++i, recordId);
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      buildRecord(rs);
    }
    rs.close();
    pst.close();
    if (id == -1) {
      throw new SQLException("Webcast record not found.");
    }
  }

  protected void buildRecord(ResultSet rs) throws SQLException {
    id = rs.getInt("webcast_id");
    projectId = DatabaseUtils.getInt(rs, "project_id");
    entered = rs.getTimestamp("entered");
    enteredBy = rs.getInt("enteredby");
    ratingCount = DatabaseUtils.getInt(rs, "rating_count", 0);
    ratingValue = DatabaseUtils.getInt(rs, "rating_value", 0);
    ratingAvg = DatabaseUtils.getDouble(rs, "rating_avg", 0.0);
    inappropriateCount = DatabaseUtils.getInt(rs, "inappropriate_count", 0);
  }


  public boolean insert(Connection db) throws SQLException {
    StringBuffer sql = new StringBuffer();
    sql.append(
            "INSERT INTO project_webcast " +
                    "(" + (id > -1 ? "webcast_id, " : "") + "project_id, ");
    if (entered != null) {
      sql.append("entered, ");
    }
    sql.append(
            "enteredby, rating_count, rating_avg) ");
    sql.append("VALUES ( ");
    if (id > -1) {
      sql.append("?, ");
    }
    sql.append("?, ");
    if (entered != null) {
      sql.append("?, ");
    }
    sql.append("?, ?, ?) ");
    int i = 0;
    //Insert the webcast
    PreparedStatement pst = db.prepareStatement(sql.toString());
    if (id > -1) {
      pst.setInt(++i, id);
    }
    DatabaseUtils.setInt(pst, ++i, projectId);
    if (entered != null) {
      pst.setTimestamp(++i, entered);
    }
    pst.setInt(++i, enteredBy);
    pst.setInt(++i, ratingCount);
    pst.setDouble(++i, ratingAvg);
    pst.execute();
    pst.close();
    id = DatabaseUtils.getCurrVal(db, "project_webcast_webcast_id_seq", id);

    return true;
  }


  public void delete(Connection db) throws SQLException {
    //delete all webcast ratings
    PreparedStatement pst = db.prepareStatement(
            "DELETE FROM project_webcast_rating " +
            "WHERE webcast_id = ? ");
    pst.setInt(1, id);
    pst.executeUpdate();
    pst.close();

    //delete the webcast
    pst = db.prepareStatement(
            "DELETE FROM project_webcast " +
            "WHERE webcast_id = ? ");
    pst.setInt(1, id);
    pst.executeUpdate();
    pst.close();
  }
}
