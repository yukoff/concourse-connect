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
package com.concursive.connect.web.modules.profile.dao;

import com.concursive.commons.db.DatabaseUtils;
import com.concursive.connect.Constants;
import com.concursive.connect.cache.utils.CacheUtils;
import com.concursive.connect.web.utils.HtmlSelect;
import com.concursive.connect.web.utils.PagedListInfo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Contains a collection of project categories
 *
 * @author matt rajkowski
 * @version $Id$
 * @created December 27, 2004
 */
public class ProjectCategoryList extends ArrayList<ProjectCategory> {

  private PagedListInfo pagedListInfo = null;
  private String emptyHtmlSelectRecord = null;
  private int enabled = Constants.UNDEFINED;
  private int includeId = -1;
  private ArrayList categoryMap = null;
  private int categoriesForProjectUser = -1;
  private int categoryId = -1;
  private boolean buildLogos = false;
  private String categoryName = null;
  private String categoryNameLowerCase = null;
  private int parentCategoryId = -1;
  private boolean topLevelOnly = false;
  private int sensitive = Constants.UNDEFINED;

  public ProjectCategoryList() {
  }

  /**
   * @return the pagedListInfo
   */
  public PagedListInfo getPagedListInfo() {
    return pagedListInfo;
  }

  /**
   * Sets the pagedListInfo attribute of the ProjectCategoryList object
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

  public int getEnabled() {
    return enabled;
  }

  public void setEnabled(int tmp) {
    this.enabled = tmp;
  }

  public void setEnabled(boolean tmp) {
    this.enabled = (tmp ? Constants.TRUE : Constants.FALSE);
  }

  public void setEnabled(String tmp) {
    this.enabled = Integer.parseInt(tmp);
  }

  public int getIncludeId() {
    return includeId;
  }

  public void setIncludeId(int tmp) {
    this.includeId = tmp;
  }

  public void setIncludeId(String tmp) {
    try {
      this.includeId = Integer.parseInt(tmp);
    } catch (Exception e) {
      this.includeId = -1;
    }
  }

  public void setCategoriesForProjectUser(int userId) {
    categoriesForProjectUser = userId;
  }

  public void setCategoryId(int categoryId) {
    this.categoryId = categoryId;
  }

  public String getCategoryName() {
    return categoryName;
  }

  public void setCategoryName(String categoryName) {
    this.categoryName = categoryName;
  }

  /**
   * @return the categoryNameLowerCase
   */
  public String getCategoryNameLowerCase() {
    return categoryNameLowerCase;
  }

  /**
   * @param categoryNameLowerCase the categoryNameLowerCase to set
   */
  public void setCategoryNameLowerCase(String categoryNameLowerCase) {
    this.categoryNameLowerCase = categoryNameLowerCase;
  }

  public int getParentCategoryId() {
    return parentCategoryId;
  }

  public void setParentCategoryId(int parentCategoryId) {
    this.parentCategoryId = parentCategoryId;
  }

  public void setParentCategoryId(String tmp) {
    this.parentCategoryId = DatabaseUtils.parseInt(tmp, -1);
  }

  public boolean isTopLevelOnly() {
    return topLevelOnly;
  }

  public void setTopLevelOnly(boolean topLevelOnly) {
    this.topLevelOnly = topLevelOnly;
  }

  public void setTopLevelOnly(String topLevelOnly) {
    this.topLevelOnly = DatabaseUtils.parseBoolean(topLevelOnly);
  }

  public int getSensitive() {
    return sensitive;
  }

  public void setSensitive(int sensitive) {
    this.sensitive = sensitive;
  }

  public void setSensitive(String sensitive) {
    this.sensitive = DatabaseUtils.parseBooleanToConstant(sensitive);
  }

  /**
   * @return the buildLogos
   */
  public boolean getBuildLogos() {
    return buildLogos;
  }

  /**
   * @param buildLogos the buildLogos to set
   */
  public void setBuildLogos(boolean buildLogos) {
    this.buildLogos = buildLogos;
  }

  /**
   * @param buildLogos the buildLogos to set
   */
  public void setBuildLogos(String buildLogos) {
    this.buildLogos = DatabaseUtils.parseBoolean(buildLogos);
  }

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
        "FROM lookup_project_category pc " +
        "WHERE code > -1 ");
    createFilter(sqlFilter, db);
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
          "AND lower(description) < ? ");
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
    pagedListInfo.setDefaultSort("parent_category desc,level,description", null);
    pagedListInfo.appendSqlTail(db, sqlOrder);

    //Need to build a base SQL statement for returning records
    pagedListInfo.appendSqlSelectHead(db, sqlSelect);
    sqlSelect.append(
        " * " +
        "FROM lookup_project_category pc " +
        "WHERE code > -1 ");
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
      ProjectCategory thisProjectCategory = new ProjectCategory(rs);
      this.add(thisProjectCategory);
    }
    rs.close();
    pst.close();

    if (buildLogos) {
      buildLogos(db);
    }
  }

  /**
   * @param db
   * @throws java.sql.SQLException
   */
  private void buildLogos(Connection db) throws SQLException {
    for (ProjectCategory projectCategory : this) {
      projectCategory.buildLogo(db);
    }
  }

  public void addCategories(ProjectList projects) {
    categoryMap = new ArrayList();
    for (Project thisProject : projects) {
      if (thisProject.getCategoryId() > -1) {
        Integer integerCategoryId = new Integer(thisProject.getCategoryId());
        if (!categoryMap.contains(integerCategoryId)) {
          categoryMap.add(integerCategoryId);
        }
      }
    }
  }

  protected void createFilter(StringBuffer sqlFilter, Connection db) throws SQLException {
    if (enabled != Constants.UNDEFINED) {
      if (includeId == -1) {
        sqlFilter.append("AND pc.enabled = ? ");
      } else {
        sqlFilter.append("AND (pc.enabled = ? OR pc.code = ?) ");
      }
    }
    if (categoryMap != null && categoryMap.size() > 0) {
      sqlFilter.append("AND (pc.code IN (");
      Iterator i = categoryMap.iterator();
      while (i.hasNext()) {
        Integer integerCategoryId = (Integer) i.next();
        sqlFilter.append(integerCategoryId.intValue());
        if (i.hasNext()) {
          sqlFilter.append(", ");
        }
      }
      sqlFilter.append(") ");
      if (includeId > -1) {
        sqlFilter.append("OR pc.code = ? ");
      }
      sqlFilter.append(") ");
    }
    if (categoriesForProjectUser > -1) {
      sqlFilter.append(
          "AND (pc.code IN (SELECT category_id FROM projects WHERE project_id IN (SELECT DISTINCT project_id FROM project_team WHERE user_id = ? " +
          "AND status IS NULL) OR project_id IN (SELECT project_id FROM projects WHERE allow_guests = ? AND approvaldate IS NOT NULL)) ");
      if (includeId > -1) {
        sqlFilter.append("OR pc.code = ? ");
      }
      sqlFilter.append(") ");
    }
    if (categoryId > -1) {
      sqlFilter.append("AND pc.code = ? ");
    }
    if (categoryName != null) {
      sqlFilter.append("AND pc.description = ? ");
    }
    if (categoryNameLowerCase != null) {
      sqlFilter.append("AND " + DatabaseUtils.toLowerCase(db, "pc.description") + " = ? ");
    }
    if (parentCategoryId > -1) {
      sqlFilter.append("AND pc.parent_category = ? ");
    }
    if (topLevelOnly) {
      sqlFilter.append("AND pc.parent_category IS NULL ");
    }
    if (sensitive != Constants.UNDEFINED) {
      sqlFilter.append("AND pc.is_sensitive = ? ");
    }
  }

  protected int prepareFilter(PreparedStatement pst) throws SQLException {
    int i = 0;
    if (enabled != Constants.UNDEFINED) {
      if (includeId == -1) {
        pst.setBoolean(++i, enabled == Constants.TRUE);
      } else {
        pst.setBoolean(++i, enabled == Constants.TRUE);
        pst.setInt(++i, includeId);
      }
    }
    if (categoryMap != null && categoryMap.size() > 0) {
      if (includeId > -1) {
        pst.setInt(++i, includeId);
      }
    }
    if (categoriesForProjectUser > -1) {
      pst.setInt(++i, categoriesForProjectUser);
      pst.setBoolean(++i, true);
      if (includeId > -1) {
        pst.setInt(++i, includeId);
      }
    }
    if (categoryId > -1) {
      pst.setInt(++i, categoryId);
    }
    if (categoryName != null) {
      pst.setString(++i, categoryName);
    }
    if (categoryNameLowerCase != null) {
      pst.setString(++i, categoryNameLowerCase.toLowerCase());
    }
    if (parentCategoryId > -1) {
      pst.setInt(++i, parentCategoryId);
    }
    if (sensitive != Constants.UNDEFINED) {
      pst.setBoolean(++i, sensitive == Constants.TRUE);
    }
    return i;
  }

  public String getValueFromId(int id) {
    for (ProjectCategory thisCategory : this) {
      if (thisCategory.getId() == id) {
        return thisCategory.getDescription();
      }
    }
    return null;
  }

  public int getIdFromValue(String name) {
    for (ProjectCategory thisCategory : this) {
      if (name.equalsIgnoreCase(thisCategory.getDescription())) {
        return thisCategory.getId();
      }
    }
    return -1;
  }

  public ProjectCategory get(ProjectCategory category) {
    for (ProjectCategory thisCategory : this) {
      if (category.getDescription().equalsIgnoreCase(thisCategory.getDescription())) {
        return thisCategory;
      }
    }
    return null;
  }

  public ProjectCategory getFromValue(String name) {
    for (ProjectCategory thisCategory : this) {
      if (name.equalsIgnoreCase(thisCategory.getDescription())) {
        return thisCategory;
      }
    }
    return null;
  }

  public ProjectCategory getMinFromValue(String name) {
    ProjectCategory minCategory = null;
    for (ProjectCategory thisCategory : this) {
      if (name.equalsIgnoreCase(thisCategory.getDescription())) {
        if (minCategory == null || thisCategory.getId() < minCategory.getId()) {
          minCategory = thisCategory;
        }
      }
    }
    return minCategory;
  }

  public ProjectCategory getMaxFromValue(String name) {
    ProjectCategory maxCategory = null;
    for (ProjectCategory thisCategory : this) {
      if (name.equalsIgnoreCase(thisCategory.getDescription())) {
        if (maxCategory == null || thisCategory.getId() > maxCategory.getId()) {
          maxCategory = thisCategory;
        }
      }
    }
    return maxCategory;
  }

  public String getHtmlSelect(String selectName, int selectedId) {
    HtmlSelect thisSelect = new HtmlSelect();
    if (emptyHtmlSelectRecord != null) {
      thisSelect.addItem(-1, emptyHtmlSelectRecord);
    }
    for (ProjectCategory thisCategory : this) {
      thisSelect.addItem(thisCategory.getId(),
          thisCategory.getDescription());
    }
    return thisSelect.getHtml(selectName, selectedId);
  }

  public HtmlSelect getHtmlSelect() {
    HtmlSelect thisSelect = new HtmlSelect();
    for (ProjectCategory thisCategory : this) {
      thisSelect.addItem(thisCategory.getId(),
          thisCategory.getDescription());
    }
    return thisSelect;
  }

  public void updateValues(Connection db, String[] params, String[] names) throws SQLException {

    // Put into something manageable
    ArrayList<String> arrayList = new ArrayList<String>();
    for (int i = 0; i < params.length; i++) {
      System.out.println("ProjectCategoryList-> Name: " + names[i]);
      System.out.println("ProjectCategoryList-> Param: " + params[i]);
      arrayList.add(params[i]);
    }

    // BEGIN TRANSACTION

    // Iterate through this article list
    Iterator items = this.iterator();
    while (items.hasNext()) {
      ProjectCategory thisCategory = (ProjectCategory) items.next();
      // If item is not in the passed array, then disable the entry
      if (!arrayList.contains(String.valueOf(thisCategory.getId()))) {
        thisCategory.setEnabled(false);
        thisCategory.update(db);
        items.remove();
      }
    }

    // Iterate through the array
    for (int i = 0; i < params.length; i++) {
      if (System.getProperty("DEBUG") != null) {
        System.out.println("ProjectCategoryList-> Name: " + names[i]);
        System.out.println("ProjectCategoryList-> Param: " + params[i]);
      }
      if (params[i].startsWith("*")) {
        // TODO: Check to see if a previously disabled entry has the same name,
        // and enable it

        // New item, add it at the correct position
        ProjectCategory thisCategory = new ProjectCategory();
        thisCategory.setDescription(names[i]);
        thisCategory.setLevel(i);
        thisCategory.insert(db);
        this.add(thisCategory);
      } else {
        // Existing item, update the name and position
        updateName(db, Integer.parseInt(params[i]), names[i]);
        updateLevel(db, Integer.parseInt(params[i]), i);
      }
    }

    // END TRANSACTION
  }

  public void updateLevel(Connection db, int id, int level) throws SQLException {
    PreparedStatement pst = db.prepareStatement("UPDATE lookup_project_category " +
        "SET level = ? " +
        "WHERE code = ? ");
    int i = 0;
    pst.setInt(++i, level);
    pst.setInt(++i, id);
    pst.executeUpdate();
    pst.close();
    CacheUtils.invalidateValue(Constants.SYSTEM_PROJECT_CATEGORY_LIST_CACHE, id);
  }

  public void updateName(Connection db, int id, String name) throws SQLException {
    PreparedStatement pst = db.prepareStatement("UPDATE lookup_project_category " +
        "SET description = ? " +
        "WHERE code = ? ");
    int i = 0;
    pst.setString(++i, name);
    pst.setInt(++i, id);
    pst.executeUpdate();
    pst.close();
    CacheUtils.invalidateValue(Constants.SYSTEM_PROJECT_CATEGORY_LIST_CACHE, id);
  }
}

