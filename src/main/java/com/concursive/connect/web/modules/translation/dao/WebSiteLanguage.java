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
 * @author matt
 * @created January 29, 2008
 */
public class WebSiteLanguage extends GenericBean {
  // Properties
  private int id = -1;
  private String languageName = null;
  private String languageLocale = null;
  private Timestamp entered = null;
  private boolean enabled = false;
  private boolean defaultItem = false;
  // Helpers
  private WebSiteTeamList teamList = new WebSiteTeamList();
  private boolean buildTeamMembers = false;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getLanguageName() {
    return languageName;
  }

  public void setLanguageName(String languageName) {
    this.languageName = languageName;
  }

  public String getLanguageLocale() {
    return languageLocale;
  }

  public void setLanguageLocale(String languageLocale) {
    this.languageLocale = languageLocale;
  }

  public Timestamp getEntered() {
    return entered;
  }

  public void setEntered(Timestamp entered) {
    this.entered = entered;
  }

  public boolean getEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public WebSiteTeamList getTeamList() {
    return teamList;
  }

  public void setTeamList(WebSiteTeamList teamList) {
    this.teamList = teamList;
  }

  public boolean getBuildTeamMembers() {
    return buildTeamMembers;
  }

  public void setBuildTeamMembers(boolean buildTeamMembers) {
    this.buildTeamMembers = buildTeamMembers;
  }

  public boolean getDefaultItem() {
    return defaultItem;
  }

  public void setDefaultItem(boolean defaultItem) {
    this.defaultItem = defaultItem;
  }

  public WebSiteLanguage() {
  }


  public WebSiteLanguage(ResultSet rs) throws SQLException {
    buildRecord(rs);
  }


  public WebSiteLanguage(Connection db, int id) throws SQLException {
    if (id == -1) {
      throw new SQLException("Invalid id specified");
    }
    PreparedStatement pst = db.prepareStatement(
        "SELECT lpl.* " +
            "FROM lookup_project_language lpl " +
            "WHERE lpl.id = ? ");
    pst.setInt(1, id);
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      buildRecord(rs);
    }
    rs.close();
    pst.close();
    if (buildTeamMembers) {
      teamList.setLanguageId(id);
      teamList.buildList(db);
    }
  }


  /**
   * Description of the Method
   *
   * @param rs Description of the Parameter
   * @throws java.sql.SQLException Description of the Exception
   */
  protected void buildRecord(ResultSet rs) throws SQLException {
    id = rs.getInt("id");
    languageName = rs.getString("language_name");
    languageLocale = rs.getString("language_locale");
    entered = rs.getTimestamp("entered");
    enabled = rs.getBoolean("enabled");
    defaultItem = rs.getBoolean("default_item");
  }


  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @return Description of the Return Value
   * @throws java.sql.SQLException Description of the Exception
   */
  public boolean insert(Connection db) throws SQLException {
    StringBuffer sql = new StringBuffer();
    sql.append("INSERT INTO lookup_project_language (language_name, language_locale, default_item) ");
    sql.append("VALUES (?, ?, ?) ");
    int i = 0;
    PreparedStatement pst = db.prepareStatement(sql.toString());
    pst.setString(++i, languageName);
    pst.setString(++i, languageLocale);
    pst.setBoolean(++i, defaultItem);
    pst.execute();
    pst.close();
    id = DatabaseUtils.getCurrVal(db, "lookup_project_language_id_seq", -1);
    return true;
  }

  public void delete(Connection db) throws SQLException {
    StringBuffer sql = new StringBuffer();
    sql.append("DELETE FROM lookup_project_language ");
    sql.append("WHERE id = ? ");
    int i = 0;
    PreparedStatement pst = db.prepareStatement(sql.toString());
    pst.setInt(++i, id);
    pst.execute();
    pst.close();
  }


}