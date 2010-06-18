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

package com.concursive.connect.web.modules.promotions.dao;

import com.concursive.commons.db.DatabaseUtils;
import com.concursive.connect.Constants;
import com.concursive.connect.web.utils.HtmlSelect;
import com.concursive.connect.web.utils.PagedListInfo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Description of the Class
 *
 * @author lbittner
 * @version $Id$
 * @created May 15, 2008
 */
public class AdCategoryList extends ArrayList<AdCategory> {
  // main ad category filters
  private PagedListInfo pagedListInfo = null;
  private String emptyHtmlSelectRecord = null;
  private int code = -1;
  private int projectCategoryId = -1;
  private boolean onlyWithoutProjectCategory = false;
  private int enabled = Constants.UNDEFINED;
  private boolean buildLogos = false;
  private String categoryLowercaseName = null;
  private int projectCategoryEnabled = Constants.UNDEFINED;


  /**
   * Constructor for the AdCategoryList object
   */
  public AdCategoryList() {
  }


  /**
   * @return the pagedListInfo
   */
  public PagedListInfo getPagedListInfo() {
    return pagedListInfo;
  }


  /**
   * Sets the pagedListInfo attribute of the BadgeCategoryList object
   *
   * @param tmp The new pagedListInfo value
   */
  public void setPagedListInfo(PagedListInfo tmp) {
    this.pagedListInfo = tmp;
  }


  /**
   * @return the emptyHtmlSelectRecord
   */
  public String getEmptyHtmlSelectRecord() {
    return emptyHtmlSelectRecord;
  }


  /**
   * @param emptyHtmlSelectRecord the emptyHtmlSelectRecord to set
   */
  public void setEmptyHtmlSelectRecord(String emptyHtmlSelectRecord) {
    this.emptyHtmlSelectRecord = emptyHtmlSelectRecord;
  }


  /**
   * @return the code
   */
  public int getCode() {
    return code;
  }


  /**
   * @param code the badgeId to set
   */
  public void setCode(int code) {
    this.code = code;
  }


  /**
   * @param code the badgeId to set
   */
  public void setCode(String code) {
    this.code = Integer.parseInt(code);
  }


  /**
   * @return the projectCategoryId
   */
  public int getProjectCategoryId() {
    return projectCategoryId;
  }


  /**
   * @param projectCategoryId the projectCategoryId to set
   */
  public void setProjectCategoryId(int projectCategoryId) {
    this.projectCategoryId = projectCategoryId;
  }


  /**
   * @param projectCategoryId the projectCategoryId to set
   */
  public void setProjectCategoryId(String projectCategoryId) {
    this.projectCategoryId = Integer.parseInt(projectCategoryId);
  }


  public boolean isOnlyWithoutProjectCategory() {
    return onlyWithoutProjectCategory;
  }

  public void setOnlyWithoutProjectCategory(boolean onlyWithoutProjectCategory) {
    this.onlyWithoutProjectCategory = onlyWithoutProjectCategory;
  }

  public void setOnlyWithoutProjectCategory(String tmp) {
    this.onlyWithoutProjectCategory = DatabaseUtils.parseBoolean(tmp);
  }


  /**
   * @return the enabled
   */
  public int getEnabled() {
    return enabled;
  }


  /**
   * @param enabled the enabled to set
   */
  public void setEnabled(int enabled) {
    this.enabled = enabled;
  }


  /**
   * @param enabled the enabled to set
   */
  public void setEnabled(String enabled) {
    this.enabled = DatabaseUtils.parseBooleanToConstant(enabled);
  }

  /**
   * @param buildLogos
   */
  public void setBuildLogos(boolean buildLogos) {
    this.buildLogos = buildLogos;

  }

  /**
   * @param buildLogos
   */
  public void setBuildLogos(String buildLogos) {
    this.buildLogos = DatabaseUtils.parseBoolean(buildLogos);
  }


  /**
   * @return the categoryLowercaseName
   */
  public String getCategoryLowercaseName() {
  	return categoryLowercaseName;
  }


	/**
   * @param categoryLowercaseName the categoryLowercaseName to set
   */
  public void setCategoryLowercaseName(String categoryLowercaseName) {
  	this.categoryLowercaseName = categoryLowercaseName;
  }


	/**
   * @return the projectCategoryEnabled
   */
  public int getProjectCategoryEnabled() {
  	return projectCategoryEnabled;
  }


	/**
   * @param projectCategoryEnabled the projectCategoryEnabled to set
   */
  public void setProjectCategoryEnabled(int projectCategoryEnabled) {
  	this.projectCategoryEnabled = projectCategoryEnabled;
  }

  public void setProjectCategoryEnabled(String projectCategoryEnabled) {
  	this.projectCategoryEnabled = Integer.parseInt(projectCategoryEnabled);
  }

	/**
   * Populate this object
   *
   * @param db Connection
   * @throws java.sql.SQLException a SQL error was encountered
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
            "FROM ad_category ac " +
            "LEFT JOIN lookup_project_category lpc ON (lpc.code = ac.project_category_id) " +
            "WHERE ac.code > -1 ");
    createFilter(db, sqlFilter);
    if (pagedListInfo == null) {
      pagedListInfo = new PagedListInfo();
      pagedListInfo.setItemsPerPage(0);
    }

    //Get the total number of records matching filter
    pst = db.prepareStatement(sqlCount.toString() + sqlFilter.toString());
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
          "AND lower(item_name) < ? ");
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
    pagedListInfo.setDefaultSort("lpc.description,level,item_name", null);
    pagedListInfo.appendSqlTail(db, sqlOrder);

    //Need to build a base SQL statement for returning records
    pagedListInfo.appendSqlSelectHead(db, sqlSelect);
    sqlSelect.append(
        "ac.*, lpc.description " +
            "FROM ad_category ac " +
            "LEFT JOIN lookup_project_category lpc ON (lpc.code = ac.project_category_id) " +
            "WHERE ac.code > -1 ");
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
      AdCategory thisAdCategory = new AdCategory(rs);
      this.add(thisAdCategory);
    }
    rs.close();
    pst.close();

    if (buildLogos) {
      buildLogos(db);
    }
  }


  /**
   * Description of the Method
   *
   * @param sqlFilter Description of Parameter
   */
  private void createFilter(Connection db, StringBuffer sqlFilter) {
    if (sqlFilter == null) {
      sqlFilter = new StringBuffer();
    }
    if (code > -1) {
      sqlFilter.append("AND (ac.code = ?) ");
    }
    if (projectCategoryId > -1) {
      sqlFilter.append("AND (ac.project_category_id = ?) ");
    }
    if (enabled != Constants.UNDEFINED) {
      sqlFilter.append("AND (ac.enabled = ?) ");
    }
    if (onlyWithoutProjectCategory) {
      sqlFilter.append("AND ac.project_category_id IS NOT NULL ");
    }
    if (categoryLowercaseName != null) {
      sqlFilter.append("AND " + DatabaseUtils.toLowerCase(db, "ac.item_name") + " = ?  ");
    }
    if (projectCategoryEnabled != Constants.UNDEFINED) {
      sqlFilter.append("AND lpc.enabled = ? ");
    }
  }


  /**
   * Description of the Method
   *
   * @param pst Description of Parameter
   * @return Description of the Returned Value
   * @throws java.sql.SQLException Description of Exception
   */
  private int prepareFilter(PreparedStatement pst) throws SQLException {
    int i = 0;
    if (code > -1) {
      pst.setInt(++i, code);
    }
    if (projectCategoryId > -1) {
      pst.setInt(++i, projectCategoryId);
    }
    if (enabled != Constants.UNDEFINED) {
      pst.setBoolean(++i, (enabled == Constants.TRUE));
    }
    if (categoryLowercaseName != null) {
      pst.setString(++i, categoryLowercaseName);
    }
    if (projectCategoryEnabled != Constants.UNDEFINED) {
      pst.setBoolean(++i, (enabled == Constants.TRUE));
    }
    return i;
  }


  /**
   * Gets the htmlSelect attribute of the BadgeCategoryList object
   *
   * @param selectName Description of Parameter
   * @return The htmlSelect value
   */
  public String getHtmlSelect(String selectName) {
    return getHtmlSelect(selectName, -1);
  }


  /**
   * Gets the htmlSelect attribute of the BadgeCategoryList object
   *
   * @param selectName Description of Parameter
   * @param defaultKey Description of Parameter
   * @return The htmlSelect value
   */
  public String getHtmlSelect(String selectName, int defaultKey) {
    HtmlSelect listSelect = this.getHtmlSelect();
    return listSelect.getHtml(selectName, defaultKey);
  }


  /**
   * Gets the htmlSelect attribute of the BadgeCategoryList object
   *
   * @return The htmlSelect value
   */
  public HtmlSelect getHtmlSelect() {
    HtmlSelect listSelect = new HtmlSelect();
    if (emptyHtmlSelectRecord != null) {
      listSelect.addItem(-1, emptyHtmlSelectRecord);
    }
    for (AdCategory thisAdCategory : this) {
      listSelect.addItem(
          thisAdCategory.getId(),
          thisAdCategory.getItemName());
    }
    return listSelect;
  }


  /**
   * @param db
   * @param basePath
   */
  public void delete(Connection db, String basePath) throws SQLException {
    for (AdCategory adCategory : this) {
      adCategory.delete(db, basePath);
    }
  }


  /**
   * @param db
   */
  private void buildLogos(Connection db) throws SQLException {
    for (AdCategory adCategory : this) {
      adCategory.buildLogo(db);
    }
  }

  /**
   * Gets the SelectedValue attribute of the LookupList object
   *
   * @param selectedId Description of Parameter
   * @return The SelectedValue value
   */
  public String getValueFromId(int selectedId) {
    for (AdCategory thisElement : this) {
      if (thisElement.getId() == selectedId) {
        return thisElement.getItemName();
      }
    }
    return "--None--";
  }


  /**
   * Gets the selectedValue attribute of the LookupList object
   *
   * @param selectedId Description of Parameter
   * @return The selectedValue value
   */
  public String getValueFromId(String selectedId) {
    try {
      return getValueFromId(Integer.parseInt(selectedId));
    } catch (Exception e) {
      return "";
    }
  }

  public AdCategory getCategoryFromId(int selectedId) {
    for (AdCategory thisElement : this) {
      if (thisElement.getId() == selectedId) {
        return thisElement;
      }
    }
    return null;
  }
}