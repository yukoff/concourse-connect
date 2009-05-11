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

package com.concursive.connect.web.modules.common.social.tagging.dao;

import com.concursive.commons.db.DatabaseUtils;
import com.concursive.connect.web.modules.common.social.tagging.utils.TagUtils;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.utils.PagedListInfo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * A collection of Tags
 *
 * @author Kailash Bhoopalam
 * @created July 17, 2008
 */
public class TagList extends ArrayList<Tag> {

  private PagedListInfo pagedListInfo = null;
  private int linkItemId = -1;
  private String tag = null;
  private String uniqueField = null;
  private String tableName = null;
  private boolean determineTagWeights = false;
  private int categoryId = -1;

  public TagList() {
  }

  public void setPagedListInfo(PagedListInfo tmp) {
    this.pagedListInfo = tmp;
  }

  public PagedListInfo getPagedListInfo() {
    return pagedListInfo;
  }

  /**
   * @return the linkItemId
   */
  public int getLinkItemId() {
    return linkItemId;
  }

  /**
   * @param linkItemId the linkItemId to set
   */
  public void setLinkItemId(int linkItemId) {
    this.linkItemId = linkItemId;
  }

  /**
   * @param linkItemId the linkItemId to set
   */
  public void setLinkItemId(String linkItemId) {
    this.linkItemId = Integer.parseInt(linkItemId);
  }

  /**
   * @return the tag
   */
  public String getTag() {
    return tag;
  }

  /**
   * @param tag the tag to set
   */
  public void setTag(String tag) {
    this.tag = tag;
  }

  /**
   * @return the uniqueField
   */
  public String getUniqueField() {
    return uniqueField;
  }

  /**
   * @param uniqueField the uniqueField to set
   */
  public void setUniqueField(String uniqueField) {
    this.uniqueField = uniqueField;
  }

  /**
   * @return the tableName
   */
  public String getTableName() {
    return tableName;
  }

  /**
   * @param tableName the tableName to set
   */
  public void setTableName(String tableName) {
    this.tableName = tableName;
  }

  public void setDetermineTagWeights(boolean determineTagWeights) {
    this.determineTagWeights = determineTagWeights;
  }

  public boolean isDetermineTagWeights() {
    return determineTagWeights;
  }

  /**
   * @return the categoryId
   */
  public int getCategoryId() {
    return categoryId;
  }

  /**
   * @param categoryId the categoryId to set
   */
  public void setCategoryId(int categoryId) {
    this.categoryId = categoryId;
  }

  /**
   * @param categoryId the categoryId to set
   */
  public void setCategoryId(String categoryId) {
    this.categoryId = Integer.parseInt(categoryId);
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
            " FROM " + tableName + "_tag" +
            " WHERE " + uniqueField + " > -1 ");
    createFilter(sqlFilter);
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
          "AND lower(tag) < ? ");
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
    pagedListInfo.setDefaultSort("tag", null);
    pagedListInfo.appendSqlTail(db, sqlOrder);

    //Need to build a base SQL statement for returning records
    pagedListInfo.appendSqlSelectHead(db, sqlSelect);
    sqlSelect.append(
        uniqueField + ", tag, tag_count, tag_date " +
            " FROM " + tableName + "_tag" +
            " WHERE " + uniqueField + " > -1 ");
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
      Tag thisTag = new Tag();
      thisTag.setUniqueField(uniqueField);
      thisTag.buildRecord(rs);
      this.add(thisTag);
    }
    rs.close();
    pst.close();
    if (determineTagWeights) {
      TagUtils.determineTagWeight(this);
    }
  }


  /**
   * Description of the Method
   *
   * @param sqlFilter Description of Parameter
   */
  private void createFilter(StringBuffer sqlFilter) {
    if (sqlFilter == null) {
      sqlFilter = new StringBuffer();
    }
    if (linkItemId > -1) {
      sqlFilter.append("AND " + uniqueField + " = ? ");
    }
    if (tag != null) {
      sqlFilter.append("AND tag = ?  ");
    }
    if (categoryId != -1 && Project.PRIMARY_KEY.equals(uniqueField)) {
      sqlFilter.append("AND project_id IN (SELECT project_id FROM projects WHERE category_id = ? )  ");
    }
  }


  /**
   * Description of the Method
   *
   * @param pst Description of Parameter
   * @return Description of the Returned Value
   * @throws SQLException Description of Exception
   */
  private int prepareFilter(PreparedStatement pst) throws SQLException {
    int i = 0;
    if (linkItemId > -1) {
      pst.setInt(++i, linkItemId);
    }
    if (tag != null) {
      pst.setString(++i, tag);
    }
    if (categoryId != -1 && Project.PRIMARY_KEY.equals(uniqueField)) {
      pst.setInt(++i, categoryId);
    }
    return i;
  }
}