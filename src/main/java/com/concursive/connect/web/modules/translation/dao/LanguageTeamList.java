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
public class LanguageTeamList extends ArrayList {

  // Field
  public static final int TEAM_MAINTAINER = 1;
  public static final int TEAM_TRANSLATOR = 2;
  public static final int TEAM_REVIEWER = 3;
  // Function
  public static final int TEAM_ADD = 1;
  public static final int TEAM_REMOVE = 2;

  private PagedListInfo pagedListInfo = null;
  private int languagePackId = -1;
  private int memberId = -1;


  /**
   * Gets the languagePackId attribute of the LanguageTeamList object
   *
   * @return The languagePackId value
   */
  public int getLanguagePackId() {
    return languagePackId;
  }


  /**
   * Sets the languagePackId attribute of the LanguageTeamList object
   *
   * @param tmp The new languagePackId value
   */
  public void setLanguagePackId(int tmp) {
    this.languagePackId = tmp;
  }


  /**
   * Sets the languagePackId attribute of the LanguageTeamList object
   *
   * @param tmp The new languagePackId value
   */
  public void setLanguagePackId(String tmp) {
    this.languagePackId = Integer.parseInt(tmp);
  }


  public int getMemberId() {
    return memberId;
  }

  public void setMemberId(int memberId) {
    this.memberId = memberId;
  }


  /**
   * Constructor for the LanguageTeamList object
   */
  public LanguageTeamList() {
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
    //Need to build a base SQL statement for counting records
    sqlCount.append(
        "SELECT COUNT(*) AS recordcount " +
            "FROM language_team lt " +
            "WHERE lt.id > 0 ");
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
      //Determine column to sort by
      //pagedListInfo.setDefaultSort("lt.language_name", null);
      //pagedListInfo.appendSqlTail(db, sqlOrder);
    } else {
      //sqlOrder.append("ORDER BY lp.language_name ");
    }

    //Need to build a base SQL statement for returning records
    if (pagedListInfo != null) {
      pagedListInfo.appendSqlSelectHead(db, sqlSelect);
    } else {
      sqlSelect.append("SELECT ");
    }
    sqlSelect.append(
        "lt.* " +
            "FROM language_team lt " +
            "WHERE lt.id > -1 ");
    pst = db.prepareStatement(sqlSelect.toString() + sqlFilter.toString() + sqlOrder.toString());
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
      LanguageTeam thisTeam = new LanguageTeam(rs);
      this.add(thisTeam);
    }
    rs.close();
    pst.close();
  }


  /**
   * Description of the Method
   *
   * @param sqlFilter Description of the Parameter
   * @param db        Description of the Parameter
   */
  private void createFilter(StringBuffer sqlFilter, Connection db) {
    if (languagePackId > -1) {
      sqlFilter.append("AND lt.language_pack_id = ? ");
    }
    if (memberId > -1) {
      sqlFilter.append("AND lt.member_id = ? ");
    }
  }


  /**
   * Description of the Method
   *
   * @param pst Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  private int prepareFilter(PreparedStatement pst) throws SQLException {
    int i = 0;
    if (languagePackId > -1) {
      pst.setInt(++i, languagePackId);
    }
    if (memberId > -1) {
      pst.setInt(++i, memberId);
    }
    return i;
  }

  public LanguageTeam getMember(int userId) {
    Iterator i = this.iterator();
    while (i.hasNext()) {
      LanguageTeam member = (LanguageTeam) i.next();
      if (member.getMemberId() == userId) {
        return member;
      }
    }
    return null;
  }

  public synchronized static void modifyMember(Connection db, int languagePackId, int userId, int field, int function) throws SQLException {
    if (field == TEAM_MAINTAINER) {
      PreparedStatement pst = db.prepareStatement(
          "UPDATE language_pack SET maintainer_id = ? WHERE id = ?"
      );
      if (function == TEAM_REMOVE) {
        DatabaseUtils.setInt(pst, 1, -1);
      } else {
        pst.setInt(1, userId);
      }
      pst.setInt(2, languagePackId);
      pst.executeUpdate();
      pst.close();
    } else {
      // See if the member is on the language team already
      LanguageTeam thisMember = null;
      LanguageTeamList teamList = new LanguageTeamList();
      teamList.setMemberId(userId);
      teamList.setLanguagePackId(languagePackId);
      teamList.buildList(db);
      if (teamList.size() == 1) {
        thisMember = (LanguageTeam) teamList.get(0);
      } else {
        thisMember = new LanguageTeam();
        thisMember.setLanguagePackId(languagePackId);
        thisMember.setMemberId(userId);
      }
      // Set the values
      if (field == TEAM_TRANSLATOR) {
        thisMember.setAllowTranslate((function == TEAM_ADD));
      } else if (field == TEAM_REVIEWER) {
        thisMember.setAllowReview(function == TEAM_ADD);
      }
      // Add, update, or remove the member
      if (teamList.size() == 1) {
        if (!thisMember.getAllowTranslate() && !thisMember.getAllowReview()) {
          thisMember.delete(db);
        } else {
          thisMember.update(db);
        }
      } else {
        if (!thisMember.getAllowTranslate() && !thisMember.getAllowReview()) {
          // nothing
        } else {
          thisMember.insert(db);
        }
      }
    }
  }
}

