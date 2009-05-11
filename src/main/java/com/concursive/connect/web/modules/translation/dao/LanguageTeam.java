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

package com.concursive.connect.web.modules.translation.dao;

import com.concursive.commons.db.DatabaseUtils;
import com.concursive.commons.web.mvc.beans.GenericBean;

import java.sql.*;

/**
 * Description of the Class
 *
 * @author Ananth
 * @created April 11, 2005
 */
public class LanguageTeam extends GenericBean {
  private int id = -1;
  private int memberId = -1;
  private int languagePackId = -1;
  private boolean allowTranslate = false;
  private boolean allowReview = false;
  private Timestamp entered = null;

  public int getLanguagePackId() {
    return languagePackId;
  }

  public void setLanguagePackId(int tmp) {
    this.languagePackId = tmp;
  }

  public void setLanguagePackId(String tmp) {
    this.languagePackId = Integer.parseInt(tmp);
  }

  public int getId() {
    return id;
  }

  public void setId(int tmp) {
    this.id = tmp;
  }

  public void setId(String tmp) {
    this.id = Integer.parseInt(tmp);
  }

  public int getMemberId() {
    return memberId;
  }

  public void setMemberId(int tmp) {
    this.memberId = tmp;
  }

  public void setMemberId(String tmp) {
    this.memberId = Integer.parseInt(tmp);
  }

  public boolean getAllowTranslate() {
    return allowTranslate;
  }

  public void setAllowTranslate(boolean tmp) {
    this.allowTranslate = tmp;
  }

  public void setAllowTranslate(String tmp) {
    this.allowTranslate = DatabaseUtils.parseBoolean(tmp);
  }

  public boolean getAllowReview() {
    return allowReview;
  }

  public void setAllowReview(boolean tmp) {
    this.allowReview = tmp;
  }

  public void setAllowReview(String tmp) {
    this.allowReview = DatabaseUtils.parseBoolean(tmp);
  }

  public Timestamp getEntered() {
    return entered;
  }

  public void setEntered(Timestamp tmp) {
    this.entered = tmp;
  }

  public void setEntered(String tmp) {
    this.entered = DatabaseUtils.parseTimestamp(tmp);
  }


  /**
   * Constructor for the LanguageTeam object
   */
  public LanguageTeam() {
  }


  /**
   * Constructor for the LanguageTeam object
   *
   * @param rs Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public LanguageTeam(ResultSet rs) throws SQLException {
    buildRecord(rs);
  }


  /**
   * Constructor for the LanguageTeam object
   *
   * @param db Description of the Parameter
   * @param id Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public LanguageTeam(Connection db, int id) throws SQLException {
    if (id == -1) {
      throw new SQLException("Invalid id specified");
    }
    PreparedStatement pst = db.prepareStatement(
        "SELECT lt.* " +
            "FROM language_team lt " +
            "WHERE lt.id = ? ");
    pst.setInt(1, id);
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      buildRecord(rs);
    }
    rs.close();
    pst.close();
  }


  /**
   * Description of the Method
   *
   * @param rs Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  protected void buildRecord(ResultSet rs) throws SQLException {
    id = rs.getInt("id");
    memberId = rs.getInt("member_id");
    languagePackId = rs.getInt("language_pack_id");
    allowTranslate = rs.getBoolean("allow_translate");
    allowReview = rs.getBoolean("allow_review");
    entered = rs.getTimestamp("entered");
  }


  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  public boolean insert(Connection db) throws SQLException {
    StringBuffer sql = new StringBuffer();
    sql.append("INSERT INTO language_team (member_id, language_pack_id, ");
    if (entered != null) {
      sql.append("entered, ");
    }
    sql.append("allow_translate, allow_review) ");
    sql.append("VALUES (?, ?, ");
    if (entered != null) {
      sql.append("?, ");
    }
    sql.append("?, ? ) ");
    int i = 0;
    PreparedStatement pst = db.prepareStatement(sql.toString());
    pst.setInt(++i, this.getMemberId());
    pst.setInt(++i, this.getLanguagePackId());
    if (entered != null) {
      pst.setTimestamp(++i, this.getEntered());
    }
    pst.setBoolean(++i, this.getAllowTranslate());
    pst.setBoolean(++i, this.getAllowReview());
    pst.execute();
    pst.close();
    id = DatabaseUtils.getCurrVal(db, "language_team_id_seq", -1);
    return true;
  }

  public void update(Connection db) throws SQLException {
    StringBuffer sql = new StringBuffer();
    sql.append("UPDATE language_team ");
    sql.append("SET allow_translate = ?, allow_review = ? ");
    sql.append("WHERE member_id = ? AND language_pack_id = ? ");
    int i = 0;
    PreparedStatement pst = db.prepareStatement(sql.toString());
    pst.setBoolean(++i, this.getAllowTranslate());
    pst.setBoolean(++i, this.getAllowReview());
    pst.setInt(++i, this.getMemberId());
    pst.setInt(++i, this.getLanguagePackId());
    pst.executeUpdate();
    pst.close();
  }

  public void delete(Connection db) throws SQLException {
    StringBuffer sql = new StringBuffer();
    sql.append("DELETE FROM language_team ");
    sql.append("WHERE member_id = ? AND language_pack_id = ? ");
    int i = 0;
    PreparedStatement pst = db.prepareStatement(sql.toString());
    pst.setInt(++i, this.getMemberId());
    pst.setInt(++i, this.getLanguagePackId());
    pst.execute();
    pst.close();
  }


}

