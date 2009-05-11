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
import com.concursive.connect.web.utils.PagedListInfo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Description of the Class
 *
 * @author Ananth
 * @created April 11, 2005
 */
public class LanguagePackList extends ArrayList {
  private PagedListInfo pagedListInfo = null;
  private boolean buildStatistics = false;
  private boolean buildTeamMembers = false;
  private boolean buildModified = false;
  private boolean buildDictionary = false;
  private boolean orderByComplete = false;


  /**
   * Gets the buildDictionary attribute of the LanguagePackList object
   *
   * @return The buildDictionary value
   */
  public boolean getBuildDictionary() {
    return buildDictionary;
  }


  /**
   * Sets the buildDictionary attribute of the LanguagePackList object
   *
   * @param tmp The new buildDictionary value
   */
  public void setBuildDictionary(boolean tmp) {
    this.buildDictionary = tmp;
  }


  /**
   * Sets the buildDictionary attribute of the LanguagePackList object
   *
   * @param tmp The new buildDictionary value
   */
  public void setBuildDictionary(String tmp) {
    this.buildDictionary = DatabaseUtils.parseBoolean(tmp);
  }


  /**
   * Gets the buildModified attribute of the LanguagePackList object
   *
   * @return The buildModified value
   */
  public boolean getBuildModified() {
    return buildModified;
  }


  /**
   * Sets the buildModified attribute of the LanguagePackList object
   *
   * @param tmp The new buildModified value
   */
  public void setBuildModified(boolean tmp) {
    this.buildModified = tmp;
  }


  /**
   * Sets the buildModified attribute of the LanguagePackList object
   *
   * @param tmp The new buildModified value
   */
  public void setBuildModified(String tmp) {
    this.buildModified = DatabaseUtils.parseBoolean(tmp);
  }


  /**
   * Gets the buildTeamMembers attribute of the LanguagePackList object
   *
   * @return The buildTeamMembers value
   */
  public boolean getBuildTeamMembers() {
    return buildTeamMembers;
  }


  /**
   * Sets the buildTeamMembers attribute of the LanguagePackList object
   *
   * @param tmp The new buildTeamMembers value
   */
  public void setBuildTeamMembers(boolean tmp) {
    this.buildTeamMembers = tmp;
  }


  /**
   * Sets the buildTeamMembers attribute of the LanguagePackList object
   *
   * @param tmp The new buildTeamMembers value
   */
  public void setBuildTeamMembers(String tmp) {
    this.buildTeamMembers = DatabaseUtils.parseBoolean(tmp);
  }


  /**
   * Gets the buildStatistics attribute of the LanguagePackList object
   *
   * @return The buildStatistics value
   */
  public boolean getBuildStatistics() {
    return buildStatistics;
  }


  /**
   * Sets the buildStatistics attribute of the LanguagePackList object
   *
   * @param tmp The new buildStatistics value
   */
  public void setBuildStatistics(boolean tmp) {
    this.buildStatistics = tmp;
  }


  /**
   * Sets the buildStatistics attribute of the LanguagePackList object
   *
   * @param tmp The new buildStatistics value
   */
  public void setBuildStatistics(String tmp) {
    this.buildStatistics = DatabaseUtils.parseBoolean(tmp);
  }

  public boolean isOrderByComplete() {
    return orderByComplete;
  }

  public void setOrderByComplete(boolean orderByComplete) {
    this.orderByComplete = orderByComplete;
  }

  /**
   * Constructor for the LanguagePackList object
   */
  public LanguagePackList() {
  }


  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public void buildList(Connection db) throws SQLException {
    PreparedStatement pst = null;
    ResultSet rs = null;
    int items = -1;
    StringBuffer sqlSelect = new StringBuffer();
    StringBuffer sqlCount = new StringBuffer();
    StringBuffer sqlFilter = new StringBuffer();
    StringBuffer sqlOrder = new StringBuffer();
    StringBuffer sqlGroup = new StringBuffer();
    //Need to build a base SQL statement for counting records
    sqlCount.append(
        "SELECT COUNT(*) AS recordcount " +
            "FROM language_pack lp " +
            "WHERE lp.id > 0 ");
    createFilter(sqlFilter, db);
    if (pagedListInfo != null) {
      //Get the total number of records matching filter
      pst = db.prepareStatement(sqlCount.toString() +
          sqlFilter.toString());
      items = prepareFilter(pst);
      rs = pst.executeQuery();
      if (rs.next()) {
        int maxRecords = rs.getInt("recordcount");
        pagedListInfo.setMaxRecords(maxRecords);
      }
      rs.close();
      pst.close();
      //Determine the offset, based on the filter, for the first record to show
      if (!pagedListInfo.getCurrentLetter().equals("")) {
        pst = db.prepareStatement(sqlCount.toString() +
            sqlFilter.toString() +
            "AND lower(lp.language_name) < ? ");
        items = prepareFilter(pst);
        pst.setString(++items, pagedListInfo.getCurrentLetter().toLowerCase());
        rs = pst.executeQuery();
        if (rs.next()) {
          int offsetCount = rs.getInt("recordcount");
          pagedListInfo.setCurrentOffset(offsetCount);
        }
        rs.close();
        pst.close();
      }
      //Determine column to sort by
      pagedListInfo.setDefaultSort("lp.language_name", null);
      pagedListInfo.appendSqlTail(db, sqlOrder);
    } else {
      if (!orderByComplete) {
        sqlOrder.append("ORDER BY lp.percent_complete desc, lp.language_name ");
      } else {
        sqlOrder.append("ORDER BY ldcount DESC, lp.language_name ");
      }
    }

    //Need to build a base SQL statement for returning records
    if (pagedListInfo != null) {
      pagedListInfo.appendSqlSelectHead(db, sqlSelect);
    } else {
      sqlSelect.append("SELECT ");
    }
    sqlSelect.append(
        "lp.* " +
            (orderByComplete ? ", COUNT(*) AS ldcount " : "") +
            "FROM language_pack lp " +
            (orderByComplete ? ", language_config lc, language_dictionary ld " : "") +
            "WHERE lp.id > 0 " +
            (orderByComplete ? "AND lp.id = lc.language_id AND lc.id = ld.config_id " : "") +
            (orderByComplete ? "AND ld.param_value1 <> '' AND ld.param_value1 IS NOT NULL " : "")
    );
    if (orderByComplete) {
      sqlGroup.append("GROUP BY lp.id, language_name, language_locale, maintainer_id, lp.entered, lp.percent_complete ");
    }
    pst = db.prepareStatement(sqlSelect.toString() + sqlFilter.toString() + sqlGroup.toString() + sqlOrder.toString());
    items = prepareFilter(pst);
    rs = pst.executeQuery();
    if (pagedListInfo != null) {
      pagedListInfo.doManualOffset(db, rs);
    }
    int count = 0;
    while (rs.next()) {
      if (pagedListInfo != null && pagedListInfo.getItemsPerPage() > 0 &&
          DatabaseUtils.getType(db) == DatabaseUtils.MSSQL &&
          count >= pagedListInfo.getItemsPerPage()) {
        break;
      }
      ++count;
      LanguagePack thisLanguage = new LanguagePack(rs);
      if (orderByComplete) {
        int translatedCount = rs.getInt("ldcount");
        thisLanguage.setDictionaryTranslatedCount(translatedCount);
      }
      this.add(thisLanguage);
    }
    rs.close();
    pst.close();
    // Build resources
    int defaultCount = 0;
    if (buildStatistics) {
      int languagePackId = LanguagePack.getLanguagePackId(db, LanguagePack.DEFAULT_LOCALE);
      defaultCount = LanguageDictionaryList.queryTotalCount(db, languagePackId);
    }
    Iterator i = this.iterator();
    while (i.hasNext()) {
      LanguagePack thisLanguage = (LanguagePack) i.next();
      if (buildStatistics) {
        thisLanguage.setDictionaryDefaultCount(defaultCount);
        thisLanguage.buildStatistics(db);
      }
      if (buildTeamMembers) {
        thisLanguage.buildTeamMembers(db);
      }
      if (buildDictionary) {
        thisLanguage.buildDictionary(db);
      }
    }

  }


  /**
   * Description of the Method
   *
   * @param sqlFilter Description of the Parameter
   * @param db        Description of the Parameter
   */
  private void createFilter(StringBuffer sqlFilter, Connection db) {

  }


  /**
   * Description of the Method
   *
   * @param pst Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  private int prepareFilter(PreparedStatement pst) throws SQLException {
    return 0;
  }
}

