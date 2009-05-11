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

package com.concursive.connect.web.modules.communications.dao;

import com.concursive.commons.db.DatabaseUtils;
import com.concursive.connect.web.utils.PagedListInfo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Represents an array of Message Template objects
 *
 * @author Ananth
 * @version MessageTemplateList.java Jul 29, 2008 1:32:51 PM Ananth $
 * @created Jul 29, 2008
 */
public class MessageTemplateList extends ArrayList<MessageTemplate> {
  private PagedListInfo pagedListInfo = null;
  int projectCategoryId = -1;
  int projectId = -1;
  int templateId = -1;

  /**
   * Gets the 'projectId' attribute of the MessageTemplateList object
   *
   * @return The 'projectId' value
   */
  public int getProjectId() {
    return projectId;
  }

  /**
   * Sets the 'projectId' attribute of the MessageTemplateList
   *
   * @param projectId The new 'projectId' value
   */
  public void setProjectId(int projectId) {
    this.projectId = projectId;
  }

  public void setProjectId(String projectId) {
    this.projectId = Integer.parseInt(projectId);
  }

  /**
   * Gets the 'templateId' attribute of the MessageTemplateList object
   *
   * @return The 'templateId' value
   */
  public int getTemplateId() {
    return templateId;
  }

  /**
   * Sets the 'templateId' attribute of the MessageTemplateList
   *
   * @param templateId The new 'templateId' value
   */
  public void setTemplateId(int templateId) {
    this.templateId = templateId;
  }

  public void setTemplateId(String templateId) {
    this.templateId = Integer.parseInt(templateId);
  }

  /**
   * Gets the 'projectCategoryId' attribute of the MessageTemplateList object
   *
   * @return The 'projectCategoryId' value
   */
  public int getProjectCategoryId() {
    return projectCategoryId;
  }

  /**
   * Sets the 'projectCategoryId' attribute of the MessageTemplateList
   *
   * @param projectCategoryId The new 'projectCategoryId' value
   */
  public void setProjectCategoryId(int projectCategoryId) {
    this.projectCategoryId = projectCategoryId;
  }

  public void setProjectCategoryId(String projectCategoryId) {
    this.projectCategoryId = Integer.parseInt(projectCategoryId);
  }

  public MessageTemplateList() {
  }

  public void setPagedListInfo(PagedListInfo tmp) {
    this.pagedListInfo = tmp;
  }

  public PagedListInfo getPagedListInfo() {
    return pagedListInfo;
  }

  public void select(Connection db) throws SQLException {
    buildList(db);
  }

  public void buildList(Connection db) throws SQLException {
    PreparedStatement pst = null;
    ResultSet rs;
    int items = -1;
    StringBuffer sqlSelect = new StringBuffer();
    StringBuffer sqlCount = new StringBuffer();
    StringBuffer sqlFilter = new StringBuffer();
    StringBuffer sqlOrder = new StringBuffer();
    //Need to build a base SQL statement for counting records
    sqlCount.append(
        "SELECT COUNT(*) AS recordcount " +
            "FROM project_message_template pmt " +
            "WHERE pmt.template_id > -1 ");
    createFilter(sqlFilter);
    if (pagedListInfo == null) {
      pagedListInfo = new PagedListInfo();
      pagedListInfo.setItemsPerPage(-1);
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
    //Determine column to sort by
    pagedListInfo.setDefaultSort("pmt.title", null);
    pagedListInfo.appendSqlTail(db, sqlOrder);
    //Need to build a base SQL statement for returning records
    if (pagedListInfo != null) {
      pagedListInfo.appendSqlSelectHead(db, sqlSelect);
    } else {
      sqlSelect.append("SELECT ");
    }
    sqlSelect.append(
        "pmt.* " +
            "FROM project_message_template pmt " +
            "WHERE pmt.template_id > -1 ");
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
      MessageTemplate thisRecord = new MessageTemplate(rs);
      this.add(thisRecord);
    }
    rs.close();
    pst.close();
  }

  protected void createFilter(StringBuffer sqlFilter) throws SQLException {
    if (sqlFilter == null) {
      sqlFilter = new StringBuffer();
    }

    if (projectCategoryId > -1) {
      sqlFilter.append("AND project_category_id = ? ");
    }
    if (projectId > -1) {
      sqlFilter.append("AND project_category_id IN (SELECT category_id FROM projects WHERE project_id = ?) ");
    }
    if (templateId > -1) {
      sqlFilter.append("AND template_id = ? ");
    }
  }


  protected int prepareFilter(PreparedStatement pst) throws SQLException {
    int i = 0;

    if (projectCategoryId > -1) {
      pst.setInt(++i, projectCategoryId);
    }
    if (projectId > -1) {
      pst.setInt(++i, projectId);
    }
    if (templateId > -1) {
      pst.setInt(++i, templateId);
    }

    return i;
  }
}