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

package com.concursive.connect.web.modules.documents.dao;

import com.concursive.commons.db.DatabaseUtils;
import com.concursive.connect.Constants;
import com.concursive.connect.web.utils.PagedListInfo;

import java.sql.*;
import java.util.ArrayList;

/**
 * Description of the Class
 *
 * @author matt rajkowski
 * @version $Id$
 * @created April 10, 2003
 */
public class FileFolderList extends ArrayList<FileFolder> {

  private int parentId = -1;
  private int linkModuleId = -1;
  private int linkItemId = -1;
  private PagedListInfo pagedListInfo = null;
  private boolean topLevelOnly = false;
  private boolean buildItemCount = false;
  private int enteredBy = -1;
  private int modifiedBy = -1;
  private int publicProjectFolders = Constants.UNDEFINED;
  private int projectCategoryId = -1;


  /**
   * Constructor for the FileFolderList object
   */
  public FileFolderList() {
  }


  /**
   * Sets the parentId attribute of the FileFolderList object
   *
   * @param tmp The new parentId value
   */
  public void setParentId(int tmp) {
    this.parentId = tmp;
  }


  /**
   * Sets the linkModuleId attribute of the FileFolderList object
   *
   * @param tmp The new linkModuleId value
   */
  public void setLinkModuleId(int tmp) {
    this.linkModuleId = tmp;
  }


  /**
   * Sets the linkItemId attribute of the FileFolderList object
   *
   * @param tmp The new linkItemId value
   */
  public void setLinkItemId(int tmp) {
    this.linkItemId = tmp;
  }


  /**
   * Sets the pagedListInfo attribute of the FileFolderList object
   *
   * @param pagedListInfo The new pagedListInfo value
   */
  public void setPagedListInfo(PagedListInfo pagedListInfo) {
    this.pagedListInfo = pagedListInfo;
  }


  /**
   * Sets the topLevelOnly attribute of the FileFolderList object
   *
   * @param tmp The new topLevelOnly value
   */
  public void setTopLevelOnly(boolean tmp) {
    this.topLevelOnly = tmp;
  }


  /**
   * Sets the buildItemCount attribute of the FileFolderList object
   *
   * @param tmp The new buildItemCount value
   */
  public void setBuildItemCount(boolean tmp) {
    this.buildItemCount = tmp;
  }


  /**
   * Gets the parentId attribute of the FileFolderList object
   *
   * @return The parentId value
   */
  public int getParentId() {
    return parentId;
  }


  /**
   * Gets the pagedListInfo attribute of the FileFolderList object
   *
   * @return The pagedListInfo value
   */
  public PagedListInfo getPagedListInfo() {
    return pagedListInfo;
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

  /**
   * @return the publicProjectFolders
   */
  public int getPublicProjectFolders() {
    return publicProjectFolders;
  }


  /**
   * @param publicProjectFolders the publicProjectFolders to set
   */
  public void setPublicProjectFolders(int publicProjectFolders) {
    this.publicProjectFolders = publicProjectFolders;
  }

  public void setPublicProjectFolders(String publicProjectFolders) {
    this.publicProjectFolders = Integer.parseInt(publicProjectFolders);
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

  public void setProjectCategoryId(String projectCategoryId) {
    this.projectCategoryId = Integer.parseInt(projectCategoryId);
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
            "FROM project_folders f " +
            "WHERE f.link_module_id > -1 ");
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
      //Determine column to sort by
      pagedListInfo.setDefaultSort("subject", null);
      pagedListInfo.appendSqlTail(db, sqlOrder);
    } else {
      sqlOrder.append("ORDER BY subject ");
    }
    //Need to build a base SQL statement for returning records
    if (pagedListInfo != null) {
      pagedListInfo.appendSqlSelectHead(db, sqlSelect);
    } else {
      sqlSelect.append("SELECT ");
    }
    sqlSelect.append(
        "* " +
            "FROM project_folders f " +
            "WHERE f.link_module_id > -1 ");
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
      FileFolder thisFolder = new FileFolder(rs);
      this.add(thisFolder);
    }
    rs.close();
    pst.close();
    //Build any extra data
    if (buildItemCount) {
      for (FileFolder thisFolder : this) {
        thisFolder.buildItemCount(db);
      }
    }
  }


  /**
   * Description of the Method
   *
   * @param sqlFilter Description of the Parameter
   */
  private void createFilter(StringBuffer sqlFilter) {
    if (sqlFilter == null) {
      sqlFilter = new StringBuffer();
    }
    if (linkModuleId > -1) {
      sqlFilter.append("AND link_module_id = ? ");
    }
    if (linkItemId > -1) {
      sqlFilter.append("AND link_item_id = ? ");
    }
    if (parentId > -1) {
      sqlFilter.append("AND parent_id = ? ");
    }
    if (topLevelOnly) {
      sqlFilter.append("AND parent_id IS NULL ");
    }
    if (publicProjectFolders != Constants.UNDEFINED) {
      sqlFilter.append("AND f.link_module_id = ? AND f.link_item_id IN ( SELECT project_id FROM projects WHERE allow_guests = ? AND approvaldate IS NOT NULL ) ");
    }
    if (projectCategoryId != -1) {
      sqlFilter.append(" AND  f.link_module_id = ? AND f.link_item_id IN ( SELECT project_id FROM projects WHERE category_id = ? ) ");
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
    if (linkModuleId > -1) {
      pst.setInt(++i, linkModuleId);
    }
    if (linkItemId > -1) {
      pst.setInt(++i, linkItemId);
    }
    if (parentId > -1) {
      pst.setInt(++i, parentId);
    }
    if (publicProjectFolders != Constants.UNDEFINED) {
      pst.setInt(++i, Constants.PROJECTS_FILES);
      pst.setBoolean(++i, true);
    }
    if (projectCategoryId != -1) {
      pst.setInt(++i, Constants.PROJECTS_FILES);
      pst.setInt(++i, projectCategoryId);
    }
    return i;
  }


  /**
   * Description of the Method
   *
   * @param db           Description of the Parameter
   * @param linkModuleId Description of the Parameter
   * @param linkItemId   Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  public static int retrieveRecordCount(Connection db, int linkModuleId, int linkItemId) throws SQLException {
    int count = 0;
    PreparedStatement pst = db.prepareStatement(
        "SELECT COUNT(*) as filecount " +
            "FROM project_folders pf " +
            "WHERE pf.link_module_id = ? and pf.link_item_id = ? ");
    pst.setInt(1, linkModuleId);
    pst.setInt(2, linkItemId);
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      count = rs.getInt("filecount");
    }
    rs.close();
    pst.close();
    return count;
  }


  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public void delete(Connection db, String baseFilePath) throws SQLException {
    for (FileFolder thisFolder : this) {
      thisFolder.delete(db, baseFilePath);
    }
  }


  /**
   * Description of the Method
   *
   * @return Description of the Return Value
   */
  public FileFolderList buildCompleteHierarchy() {
    for (int i = 0; i < this.size(); i++) {
      FileFolder thisFolder = this.get(i);
      if (thisFolder.getSubFolders().size() > 0) {
        this.addAll(i + 1, thisFolder.getSubFolders());
      }
    }
    return this;
  }


  /**
   * Description of the Method
   *
   * @param folderId Description of the Parameter
   * @return Description of the Return Value
   */
  public boolean hasFolder(int folderId) {
    for (FileFolder thisFolder : this) {
      if (folderId == thisFolder.getId()) {
        return true;
      }
    }
    return false;
  }

  public FileFolder getFolder(int folderId) {
    for (FileFolder thisFolder : this) {
      if (folderId == thisFolder.getId()) {
        return thisFolder;
      }
    }
    return null;
  }

  public void insert(Connection db) throws SQLException {
    for (FileFolder thisFolder : this) {
      thisFolder.setLinkItemId(linkItemId);
      thisFolder.setId(-1);
      thisFolder.setEntered((Timestamp) null);
      thisFolder.setModified((Timestamp) null);
      thisFolder.setEnteredBy(enteredBy);
      thisFolder.setModifiedBy(modifiedBy);
      thisFolder.insert(db);
    }
  }
}

