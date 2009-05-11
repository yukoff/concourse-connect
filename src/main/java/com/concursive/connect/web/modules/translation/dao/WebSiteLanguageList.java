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

import com.concursive.connect.Constants;
import com.concursive.connect.web.utils.HtmlSelect;

import javax.servlet.http.HttpServletRequest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Description of the Class
 *
 * @author matt
 * @created January 29, 2008
 */
public class WebSiteLanguageList extends ArrayList<WebSiteLanguage> {

  private String languageLocale = null;
  private boolean buildTeamMembers = false;
  private int memberId = -1;
  private int enabled = Constants.UNDEFINED;


  public String getLanguageLocale() {
    return languageLocale;
  }

  public void setLanguageLocale(String languageLocale) {
    this.languageLocale = languageLocale;
  }

  public boolean getBuildTeamMembers() {
    return buildTeamMembers;
  }

  public void setBuildTeamMembers(boolean buildTeamMembers) {
    this.buildTeamMembers = buildTeamMembers;
  }

  public int getMemberId() {
    return memberId;
  }

  public void setMemberId(int memberId) {
    this.memberId = memberId;
  }

  public int getEnabled() {
    return enabled;
  }

  public void setEnabled(int enabled) {
    this.enabled = enabled;
  }

  public WebSiteLanguageList() {
  }


  public void buildList(Connection db) throws SQLException {
    StringBuffer sqlSelect = new StringBuffer();
    StringBuffer sqlFilter = new StringBuffer();
    StringBuffer sqlOrder = new StringBuffer();
    sqlSelect.append("SELECT ");
    sqlSelect.append(
        "lpl.* " +
            "FROM lookup_project_language lpl " +
            "WHERE lpl.id > -1 ");
    createFilter(sqlFilter, db);
    sqlOrder.append("ORDER BY language_name ");
    PreparedStatement pst = db.prepareStatement(sqlSelect.toString() + sqlFilter.toString() + sqlOrder.toString());
    prepareFilter(pst);
    ResultSet rs = pst.executeQuery();
    while (rs.next()) {
      WebSiteLanguage thisLanguage = new WebSiteLanguage(rs);
      this.add(thisLanguage);
    }
    rs.close();
    pst.close();
    if (buildTeamMembers) {
      Iterator i = this.iterator();
      while (i.hasNext()) {
        WebSiteLanguage thisLanguage = (WebSiteLanguage) i.next();
        thisLanguage.getTeamList().setLanguageId(thisLanguage.getId());
        thisLanguage.getTeamList().buildList(db);
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
    if (languageLocale != null) {
      sqlFilter.append("AND language_name = ? ");
    }
    if (memberId > -1) {
      sqlFilter.append("AND id IN (SELECT language_id FROM project_language_team WHERE member_id = ?) ");
    }
    if (enabled != Constants.UNDEFINED) {
      sqlFilter.append("AND enabled = ? ");
    }
  }


  /**
   * Description of the Method
   *
   * @param pst Description of the Parameter
   * @return Description of the Return Value
   * @throws java.sql.SQLException Description of the Exception
   */
  private int prepareFilter(PreparedStatement pst) throws SQLException {
    int i = 0;
    if (languageLocale != null) {
      pst.setString(++i, languageLocale);
    }
    if (memberId > -1) {
      pst.setInt(++i, memberId);
    }
    if (enabled != Constants.UNDEFINED) {
      pst.setBoolean(++i, enabled == Constants.TRUE);
    }
    return i;
  }

  public WebSiteLanguage getLanguage(String languageLocale) {
    if (languageLocale == null) {
      return null;
    }
    Iterator i = this.iterator();
    while (i.hasNext()) {
      WebSiteLanguage language = (WebSiteLanguage) i.next();
      if (language.getLanguageLocale().equals(languageLocale)) {
        return language;
      }
    }
    return null;
  }

  public int getDefaultId() {
    Iterator i = this.iterator();
    while (i.hasNext()) {
      WebSiteLanguage language = (WebSiteLanguage) i.next();
      if (language.getDefaultItem()) {
        return language.getId();
      }
    }
    return -1;
  }

  public String getDefault() {
    Iterator i = this.iterator();
    while (i.hasNext()) {
      WebSiteLanguage language = (WebSiteLanguage) i.next();
      if (language.getDefaultItem()) {
        return language.getLanguageLocale();
      }
    }
    return "en_US";
  }

  public WebSiteLanguage getLanguage(int languageId) {
    Iterator i = this.iterator();
    while (i.hasNext()) {
      WebSiteLanguage language = (WebSiteLanguage) i.next();
      if (language.getId() == languageId) {
        return language;
      }
    }
    return null;
  }

  public boolean hasLanguage(WebSiteLanguage thisLanguage) {
    Iterator i = this.iterator();
    while (i.hasNext()) {
      WebSiteLanguage language = (WebSiteLanguage) i.next();
      if (language.getId() == thisLanguage.getId()) {
        return true;
      }
    }
    return false;
  }


  public HtmlSelect getHtmlSelect() {
    HtmlSelect thisSelect = new HtmlSelect();
    for (WebSiteLanguage thisItem : this) {
      thisSelect.addItem(
          thisItem.getId(),
          thisItem.getLanguageName());
    }
    return thisSelect;
  }


  public HtmlSelect getHtmlSelectByLocale() {
    HtmlSelect thisSelect = new HtmlSelect();
    for (WebSiteLanguage thisItem : this) {
      thisSelect.addItem(
          thisItem.getLanguageLocale(),
          thisItem.getLanguageName());
    }
    return thisSelect;
  }


  public String getLanguage(HttpServletRequest request) {
    String uri = request.getRequestURL().toString();
    Iterator<WebSiteLanguage> i = this.iterator();
    while (i.hasNext()) {
      WebSiteLanguage thisItem = i.next();
      if (uri.indexOf(thisItem.getLanguageLocale()) > -1) {
        return thisItem.getLanguageLocale();
      }
    }
    return null;
  }

  public int getLanguageId(HttpServletRequest request) {
    int defaultId = -1;
    String uri = request.getRequestURL().toString();
    Iterator<WebSiteLanguage> i = this.iterator();
    while (i.hasNext()) {
      WebSiteLanguage thisItem = i.next();
      if (uri.indexOf(thisItem.getLanguageLocale()) > -1) {
        return thisItem.getId();
      }
      if (thisItem.getDefaultItem()) {
        defaultId = thisItem.getId();
      }
    }
    return defaultId;
  }

  public void add(WebSiteLanguageList webSiteLanguageList) {
    if (webSiteLanguageList != null) {
      Iterator i = webSiteLanguageList.iterator();
      while (i.hasNext()) {
        WebSiteLanguage thisLanguage = (WebSiteLanguage) i.next();
        if (!this.hasLanguage(thisLanguage)) {
          this.add(thisLanguage);
        }
      }
    }
  }
}