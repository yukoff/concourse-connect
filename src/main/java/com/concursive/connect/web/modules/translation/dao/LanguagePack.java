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
import java.util.Iterator;

/**
 * Description of the Class
 *
 * @author Ananth
 * @created April 11, 2005
 */
public class LanguagePack extends GenericBean {
  private static final long serialVersionUID = 1L;
  public final static String DEFAULT_LOCALE = "en_US";

  private int id = -1;
  private String languageName = null;
  private String languageLocale = null;
  private int maintainerId = -1;
  private Timestamp entered = null;
  private String maintainer = null;
  private Timestamp modified = null;
  //resources
  private LanguageTeamList teamList = new LanguageTeamList();
  private LanguageDictionaryList dictionaryList = new LanguageDictionaryList();
  private boolean buildStatistics = false;
  private boolean buildTeamMembers = false;
  private boolean buildDictionary = false;
  private String percentageComplete = null;
  private int dictionaryItemCount = -1;
  private int dictionaryTranslatedCount = -1;
  private int dictionaryDefaultCount = -1;
  private int percentComplete = 0;

  /**
   * Gets the dictionaryList attribute of the LanguagePack object
   *
   * @return The dictionaryList value
   */
  public LanguageDictionaryList getDictionaryList() {
    return dictionaryList;
  }


  /**
   * Sets the dictionaryList attribute of the LanguagePack object
   *
   * @param tmp The new dictionaryList value
   */
  public void setDictionaryList(LanguageDictionaryList tmp) {
    this.dictionaryList = tmp;
  }


  /**
   * Gets the buildDictionary attribute of the LanguagePack object
   *
   * @return The buildDictionary value
   */
  public boolean getBuildDictionary() {
    return buildDictionary;
  }


  /**
   * Sets the buildDictionary attribute of the LanguagePack object
   *
   * @param tmp The new buildDictionary value
   */
  public void setBuildDictionary(boolean tmp) {
    this.buildDictionary = tmp;
  }


  /**
   * Sets the buildDictionary attribute of the LanguagePack object
   *
   * @param tmp The new buildDictionary value
   */
  public void setBuildDictionary(String tmp) {
    this.buildDictionary = DatabaseUtils.parseBoolean(tmp);
  }


  /**
   * Gets the modified attribute of the LanguagePack object
   *
   * @return The modified value
   */
  public Timestamp getModified() {
    return modified;
  }


  /**
   * Sets the modified attribute of the LanguagePack object
   *
   * @param tmp The new modified value
   */
  public void setModified(Timestamp tmp) {
    this.modified = tmp;
  }


  /**
   * Sets the modified attribute of the LanguagePack object
   *
   * @param tmp The new modified value
   */
  public void setModified(String tmp) {
    this.modified = DatabaseUtils.parseTimestamp(tmp);
  }


  /**
   * Gets the buildTeamMembers attribute of the LanguagePack object
   *
   * @return The buildTeamMembers value
   */
  public boolean getBuildTeamMembers() {
    return buildTeamMembers;
  }


  /**
   * Sets the buildTeamMembers attribute of the LanguagePack object
   *
   * @param tmp The new buildTeamMembers value
   */
  public void setBuildTeamMembers(boolean tmp) {
    this.buildTeamMembers = tmp;
  }


  /**
   * Sets the buildTeamMembers attribute of the LanguagePack object
   *
   * @param tmp The new buildTeamMembers value
   */
  public void setBuildTeamMembers(String tmp) {
    this.buildTeamMembers = DatabaseUtils.parseBoolean(tmp);
  }


  /**
   * Gets the dictionaryItemCount attribute of the LanguagePack object
   *
   * @return The dictionaryItemCount value
   */
  public int getDictionaryItemCount() {
    return dictionaryItemCount;
  }


  /**
   * Sets the dictionaryItemCount attribute of the LanguagePack object
   *
   * @param tmp The new dictionaryItemCount value
   */
  public void setDictionaryItemCount(int tmp) {
    this.dictionaryItemCount = tmp;
  }


  /**
   * Sets the dictionaryItemCount attribute of the LanguagePack object
   *
   * @param tmp The new dictionaryItemCount value
   */
  public void setDictionaryItemCount(String tmp) {
    this.dictionaryItemCount = Integer.parseInt(tmp);
  }


  /**
   * Gets the dictionaryTranslatedCount attribute of the LanguagePack object
   *
   * @return The dictionaryTranslatedCount value
   */
  public int getDictionaryTranslatedCount() {
    return dictionaryTranslatedCount;
  }


  /**
   * Sets the dictionaryTranslatedCount attribute of the LanguagePack object
   *
   * @param tmp The new dictionaryTranslatedCount value
   */
  public void setDictionaryTranslatedCount(int tmp) {
    this.dictionaryTranslatedCount = tmp;
  }


  /**
   * Sets the dictionaryTranslatedCount attribute of the LanguagePack object
   *
   * @param tmp The new dictionaryTranslatedCount value
   */
  public void setDictionaryTranslatedCount(String tmp) {
    this.dictionaryTranslatedCount = Integer.parseInt(tmp);
  }


  /**
   * Gets the id attribute of the LanguagePack object
   *
   * @return The id value
   */
  public int getId() {
    return id;
  }


  /**
   * Sets the id attribute of the LanguagePack object
   *
   * @param tmp The new id value
   */
  public void setId(int tmp) {
    this.id = tmp;
  }


  /**
   * Sets the id attribute of the LanguagePack object
   *
   * @param tmp The new id value
   */
  public void setId(String tmp) {
    this.id = Integer.parseInt(tmp);
  }


  /**
   * Gets the languageName attribute of the LanguagePack object
   *
   * @return The languageName value
   */
  public String getLanguageName() {
    return languageName;
  }


  /**
   * Sets the languageName attribute of the LanguagePack object
   *
   * @param tmp The new languageName value
   */
  public void setLanguageName(String tmp) {
    this.languageName = tmp;
  }


  /**
   * Gets the languageLocale attribute of the LanguagePack object
   *
   * @return The languageLocale value
   */
  public String getLanguageLocale() {
    return languageLocale;
  }


  /**
   * Sets the languageLocale attribute of the LanguagePack object
   *
   * @param tmp The new languageLocale value
   */
  public void setLanguageLocale(String tmp) {
    this.languageLocale = tmp;
  }


  /**
   * Gets the maintainerId attribute of the LanguagePack object
   *
   * @return The maintainerId value
   */
  public int getMaintainerId() {
    return maintainerId;
  }


  /**
   * Sets the maintainerId attribute of the LanguagePack object
   *
   * @param tmp The new maintainerId value
   */
  public void setMaintainerId(int tmp) {
    this.maintainerId = tmp;
  }


  /**
   * Sets the maintainerId attribute of the LanguagePack object
   *
   * @param tmp The new maintainerId value
   */
  public void setMaintainerId(String tmp) {
    this.maintainerId = Integer.parseInt(tmp);
  }


  /**
   * Gets the entered attribute of the LanguagePack object
   *
   * @return The entered value
   */
  public Timestamp getEntered() {
    return entered;
  }


  /**
   * Sets the entered attribute of the LanguagePack object
   *
   * @param tmp The new entered value
   */
  public void setEntered(Timestamp tmp) {
    this.entered = tmp;
  }


  /**
   * Sets the entered attribute of the LanguagePack object
   *
   * @param tmp The new entered value
   */
  public void setEntered(String tmp) {
    this.entered = DatabaseUtils.parseTimestamp(tmp);
  }


  /**
   * Gets the maintainer attribute of the LanguagePack object
   *
   * @return The maintainer value
   */
  public String getMaintainer() {
    return maintainer;
  }


  /**
   * Sets the maintainer attribute of the LanguagePack object
   *
   * @param tmp The new maintainer value
   */
  public void setMaintainer(String tmp) {
    this.maintainer = tmp;
  }


  /**
   * Gets the percentageComplete attribute of the LanguagePack object
   *
   * @return The percentageComplete value
   */
  public String getPercentageComplete() {
    if (percentageComplete != null) {
      return percentageComplete;
    } else {
      return (percentComplete + "%");
    }
  }


  /**
   * Sets the percentageComplete attribute of the LanguagePack object
   *
   * @param tmp The new percentageComplete value
   */
  public void setPercentageComplete(String tmp) {
    this.percentageComplete = tmp;
  }


  /**
   * Gets the buildStatistics attribute of the LanguagePack object
   *
   * @return The buildStatistics value
   */
  public boolean getBuildStatistics() {
    return buildStatistics;
  }


  /**
   * Sets the buildStatistics attribute of the LanguagePack object
   *
   * @param tmp The new buildStatistics value
   */
  public void setBuildStatistics(boolean tmp) {
    this.buildStatistics = tmp;
  }


  /**
   * Sets the buildStatistics attribute of the LanguagePack object
   *
   * @param tmp The new buildStatistics value
   */
  public void setBuildStatistics(String tmp) {
    this.buildStatistics = DatabaseUtils.parseBoolean(tmp);
  }

  public int getDictionaryDefaultCount() {
    return dictionaryDefaultCount;
  }

  public void setDictionaryDefaultCount(int dictionaryDefaultCount) {
    this.dictionaryDefaultCount = dictionaryDefaultCount;
  }

  public LanguageTeamList getTeamList() {
    return teamList;
  }

  public int getPercentComplete() {
    return percentComplete;
  }

  public void setPercentComplete(int percentComplete) {
    this.percentComplete = percentComplete;
  }

  /**
   * Constructor for the LanguagePack object
   */
  public LanguagePack() {
  }


  /**
   * Constructor for the LanguagePack object
   *
   * @param rs Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public LanguagePack(ResultSet rs) throws SQLException {
    buildRecord(rs);
  }


  /**
   * Constructor for the LanguagePack object
   *
   * @param db Description of the Parameter
   * @param id Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public LanguagePack(Connection db, int id) throws SQLException {
    queryRecord(db, id);
  }


  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @param id Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public void queryRecord(Connection db, int id) throws SQLException {
    if (id == -1) {
      throw new SQLException("Invalid id specified");
    }
    PreparedStatement pst = db.prepareStatement(
        "SELECT lp.* " +
            "FROM language_pack lp " +
            "WHERE lp.id = ? ");
    pst.setInt(1, id);
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      buildRecord(rs);
    }
    rs.close();
    pst.close();
    if (languageName == null) {
      throw new SQLException("Invalid id specified");
    }
    if (buildStatistics) {
      this.buildStatistics(db);
    }
    if (buildTeamMembers) {
      this.buildTeamMembers(db);
    }
    if (buildDictionary) {
      this.buildDictionary(db);
    }
  }


  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public void buildStatistics(Connection db) throws SQLException {
    int languagePackId = getLanguagePackId(db, DEFAULT_LOCALE);
    if (dictionaryDefaultCount == -1) {
      dictionaryDefaultCount = LanguageDictionaryList.queryTotalCount(db, languagePackId);
    }
    if (languagePackId == this.getId()) {
      dictionaryItemCount = dictionaryDefaultCount;
    } else {
      dictionaryItemCount = LanguageDictionaryList.queryTotalCount(db, this.getId());
    }
    if (dictionaryTranslatedCount == -1) {
      dictionaryTranslatedCount = LanguageDictionaryList.queryTranslatedCount(db, this.getId());
    }
    if (dictionaryDefaultCount > 0) {
      percentageComplete = (int) ((dictionaryTranslatedCount * 1.0 / dictionaryDefaultCount * 1.0) * 100.0) + "%";
    } else {
      percentageComplete = "0%";
    }
  }


  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public void buildTeamMembers(Connection db) throws SQLException {
    teamList.setLanguagePackId(this.getId());
    teamList.buildList(db);
  }


  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public void buildDictionary(Connection db) throws SQLException {
    dictionaryList.setLanguagePackId(this.getId());
    dictionaryList.buildList(db);
  }


  /**
   * Gets the country attribute of the LanguagePack object
   *
   * @return The country value
   */
  public String getCountry() {
    if (languageLocale != null) {
      if (languageLocale.indexOf("_") > -1) {
        return languageLocale.substring(languageLocale.indexOf("_") + 1);
      }
    }
    return "";
  }


  /**
   * Description of the Method
   *
   * @param rs Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  protected void buildRecord(ResultSet rs) throws SQLException {
    id = rs.getInt("id");
    languageName = rs.getString("language_name");
    languageLocale = rs.getString("language_locale");
    maintainerId = rs.getInt("maintainer_id");
    entered = rs.getTimestamp("entered");
    percentComplete = rs.getInt("percent_complete");
    modified = rs.getTimestamp("modified");
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
    sql.append("INSERT INTO language_pack (language_name, language_locale, percent_complete ");
    if (entered != null) {
      sql.append(", entered ");
    }
    if (maintainerId > -1) {
      sql.append(", maintainer_id ");
    }
    sql.append(") ");
    sql.append("VALUES (?, ?, ? ");
    if (entered != null) {
      sql.append(", ? ");
    }
    if (maintainerId > -1) {
      sql.append(", ? ");
    }
    sql.append(") ");
    int i = 0;
    PreparedStatement pst = db.prepareStatement(sql.toString());
    pst.setString(++i, this.getLanguageName());
    pst.setString(++i, this.getLanguageLocale());
    pst.setInt(++i, percentComplete);
    if (entered != null) {
      pst.setTimestamp(++i, this.getEntered());
    }
    if (maintainerId > -1) {
      pst.setInt(++i, this.getMaintainerId());
    }
    pst.execute();
    pst.close();
    id = DatabaseUtils.getCurrVal(db, "language_pack_id_seq", -1);
    return true;
  }


  /**
   * Description of the Method
   *
   * @param userId Description of the Parameter
   * @return Description of the Return Value
   */
  public boolean allowsTranslation(int userId) {
    Iterator i = teamList.iterator();
    while (i.hasNext()) {
      LanguageTeam member = (LanguageTeam) i.next();
      if (member.getMemberId() == userId && member.getAllowTranslate()) {
        return true;
      }
    }
    return false;
  }


  /**
   * Description of the Method
   *
   * @param userId Description of the Parameter
   * @return Description of the Return Value
   */
  public boolean allowsApproval(int userId) {
    Iterator i = teamList.iterator();
    while (i.hasNext()) {
      LanguageTeam member = (LanguageTeam) i.next();
      if (member.getMemberId() == userId && member.getAllowReview()) {
        return true;
      }
    }
    return false;
  }


  /**
   * Gets the languagePackId attribute of the LanguagePack class
   *
   * @param db     Description of the Parameter
   * @param locale Description of the Parameter
   * @return The languagePackId value
   * @throws SQLException Description of the Exception
   */
  public static int getLanguagePackId(Connection db, String locale) throws SQLException {
    int packId = -1;
    PreparedStatement pst = db.prepareStatement(
        "SELECT id " +
            "FROM language_pack " +
            "WHERE language_locale = ? ");
    pst.setString(1, locale);
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      packId = rs.getInt("id");
    }
    rs.close();
    pst.close();
    return packId;
  }
}

