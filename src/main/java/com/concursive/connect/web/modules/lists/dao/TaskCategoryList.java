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

package com.concursive.connect.web.modules.lists.dao;

import com.concursive.commons.db.DatabaseUtils;
import com.concursive.connect.Constants;
import com.concursive.connect.web.utils.HtmlSelect;
import com.concursive.connect.web.utils.PagedListInfo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Queries the task category list depending on the supplied parameters
 *
 * @author matt rajkowski
 * @created November 17, 2002
 */
public class TaskCategoryList extends ArrayList<TaskCategory> {

  protected PagedListInfo pagedListInfo = null;
  protected int projectId = -1;
  protected int enteredBy = -1;
  protected int modifiedBy = -1;
  protected int owner = -1;
  protected int categoryId = -1;
  private int taskLinkModuleId = -1;
  private int taskLinkItemId = -1;


  /**
   * Constructor for the TaskCategoryList object
   */
  public TaskCategoryList() {
  }


  /**
   * Sets the pagedListInfo attribute of the TaskCategoryList object
   *
   * @param tmp The new pagedListInfo value
   */
  public void setPagedListInfo(PagedListInfo tmp) {
    this.pagedListInfo = tmp;
  }


  /**
   * Sets the projectId attribute of the TaskCategoryList object
   *
   * @param tmp The new projectId value
   */
  public void setProjectId(int tmp) {
    this.projectId = tmp;
  }

  public int getEnteredBy() {
    return enteredBy;
  }

  public void setEnteredBy(int enteredBy) {
    this.enteredBy = enteredBy;
  }

  public int getModifiedBy() {
    return modifiedBy;
  }

  public void setModifiedBy(int modifiedBy) {
    this.modifiedBy = modifiedBy;
  }

  public int getOwner() {
    return owner;
  }

  public void setOwner(int owner) {
    this.owner = owner;
  }

  public int getCategoryId() {
    return categoryId;
  }

  public void setCategoryId(int categoryId) {
    this.categoryId = categoryId;
  }

  public void setCategoryId(String categoryId) {
    this.categoryId = Integer.parseInt(categoryId);
  }

  public int getTaskLinkModuleId() {
    return taskLinkModuleId;
  }

  public void setTaskLinkModuleId(int taskLinkModuleId) {
    this.taskLinkModuleId = taskLinkModuleId;
  }

  public void setTaskLinkModuleId(String taskLinkModuleId) {
    this.taskLinkModuleId = Integer.parseInt(taskLinkModuleId);
  }

  public int getTaskLinkItemId() {
    return taskLinkItemId;
  }

  public void setTaskLinkItemId(int taskLinkItemId) {
    this.taskLinkItemId = taskLinkItemId;
  }

  public void setTaskLinkItemId(String taskLinkItemId) {
    this.taskLinkItemId = Integer.parseInt(taskLinkItemId);
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
            "FROM lookup_task_category c " +
            "WHERE c.code > -1 ");

    createFilter(sqlFilter);

    if (pagedListInfo != null) {
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
            "AND c.description > ? ");
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
      pagedListInfo.setDefaultSort("c.level, c.description", null);
      pagedListInfo.appendSqlTail(db, sqlOrder);
    } else {
      sqlOrder.append("ORDER BY c.level, c.description ");
    }

    //Need to build a base SQL statement for returning records
    if (pagedListInfo != null) {
      pagedListInfo.appendSqlSelectHead(db, sqlSelect);
    } else {
      sqlSelect.append("SELECT ");
    }
    sqlSelect.append(
        "c.code, c.description, c.default_item, c.level, c.enabled " +
            "FROM lookup_task_category c " +
            "WHERE c.code > -1 ");
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
      TaskCategory thisCategory = new TaskCategory(rs);
      this.add(thisCategory);
    }
    rs.close();
    pst.close();

    for (TaskCategory thisCategory : this) {
      thisCategory.buildResources(db);
    }
  }


  /**
   * Description of the Method
   *
   * @param sqlFilter Description of the Parameter
   */
  protected void createFilter(StringBuffer sqlFilter) {
    if (sqlFilter == null) {
      sqlFilter = new StringBuffer();
    }
    if (projectId > 0) {
      sqlFilter.append("AND c.code IN (SELECT category_id FROM taskcategory_project WHERE project_id = ?) ");
    }
    if (categoryId > 0) {
      sqlFilter.append("AND c.code = ? ");
    }
    if (taskLinkModuleId > 0 && taskLinkItemId > 0) {
      sqlFilter.append("AND c.code IN (SELECT category_id FROM task where link_module_id = ? AND link_item_id = ?) ");
    }
  }


  /**
   * Description of the Method
   *
   * @param pst Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  protected int prepareFilter(PreparedStatement pst) throws SQLException {
    int i = 0;
    if (projectId > 0) {
      pst.setInt(++i, projectId);
    }
    if (categoryId > 0) {
      pst.setInt(++i, categoryId);
    }
    if (taskLinkModuleId > 0 && taskLinkItemId > 0) {
      pst.setInt(++i, taskLinkModuleId);
      pst.setInt(++i, taskLinkItemId);
    }
    return i;
  }


  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public void delete(Connection db) throws SQLException {
    for (TaskCategory thisCategory : this) {
      thisCategory.delete(db);
    }
  }


  /**
   * Gets the htmlSelect attribute of the TaskCategoryList object
   *
   * @param selectName Description of the Parameter
   * @param selectedId Description of the Parameter
   * @return The htmlSelect value
   */
  public String getHtmlSelect(String selectName, int selectedId) {
    HtmlSelect thisSelect = new HtmlSelect();
    thisSelect.addItem(-1, "-- None --");
    for (TaskCategory thisCategory : this) {
      thisSelect.addItem(
          thisCategory.getId(),
          thisCategory.getDescription());
    }
    return thisSelect.getHtml(selectName, selectedId);
  }


  /**
   * Gets the valueFromId attribute of the TaskCategoryList object
   *
   * @param id Description of the Parameter
   * @return The valueFromId value
   */
  public String getValueFromId(int id) {
    for (TaskCategory thisCategory : this) {
      if (thisCategory.getId() == id) {
        return thisCategory.getDescription();
      }
    }
    return null;
  }


  public TaskCategory getCategoryFromId(int id) {
    for (TaskCategory thisCategory : this) {
      if (thisCategory.getId() == id) {
        return thisCategory;
      }
    }
    return null;
  }


  /**
   * Gets the htmlSelect attribute of the TaskCategoryList object
   *
   * @return The htmlSelect value
   */
  public HtmlSelect getHtmlSelect() {
    HtmlSelect thisSelect = new HtmlSelect();
    for (TaskCategory thisCategory : this) {
      thisSelect.addItem(
          thisCategory.getId(),
          thisCategory.getDescription());
    }
    return thisSelect;
  }

  public void insert(Connection db, boolean insertItems) throws SQLException {
    for (TaskCategory thisCategory : this) {
      TaskList tasks = null;
      if (insertItems) {
        tasks = new TaskList();
        tasks.setCategoryId(thisCategory.getId());
        tasks.buildList(db);
      }
      thisCategory.setLinkModuleId(Constants.TASK_CATEGORY_PROJECTS);
      thisCategory.setLinkItemId(projectId);
      thisCategory.insert(db);
      if (tasks != null) {
        tasks.setProjectId(projectId);
        tasks.setCategoryId(thisCategory.getId());
        tasks.setEnteredBy(enteredBy);
        tasks.setModifiedBy(modifiedBy);
        tasks.setOwner(owner);
        tasks.insert(db);
      }
    }
  }
}

