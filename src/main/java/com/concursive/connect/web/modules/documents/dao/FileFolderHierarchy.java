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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;

/**
 * Description of the Class
 *
 * @author matt rajkowski
 * @version $Id$
 * @created July 6, 2004
 */
public class FileFolderHierarchy {

  private int linkModuleId = -1;
  private int linkItemId = -1;
  private FileFolderList hierarchy = null;


  /**
   * Sets the linkModuleId attribute of the FileFolderHierarchy object
   *
   * @param tmp The new linkModuleId value
   */
  public void setLinkModuleId(int tmp) {
    this.linkModuleId = tmp;
  }


  /**
   * Sets the linkItemId attribute of the FileFolderHierarchy object
   *
   * @param tmp The new linkItemId value
   */
  public void setLinkItemId(int tmp) {
    this.linkItemId = tmp;
  }


  /**
   * Sets the hierarchy attribute of the FileFolderHierarchy object
   *
   * @param tmp The new hierarchy value
   */
  public void setHierarchy(FileFolderList tmp) {
    this.hierarchy = tmp;
  }


  /**
   * Gets the linkModuleId attribute of the FileFolderHierarchy object
   *
   * @return The linkModuleId value
   */
  public int getLinkModuleId() {
    return linkModuleId;
  }


  /**
   * Gets the linkItemId attribute of the FileFolderHierarchy object
   *
   * @return The linkItemId value
   */
  public int getLinkItemId() {
    return linkItemId;
  }


  /**
   * Gets the hierarchy attribute of the FileFolderHierarchy object
   *
   * @return The hierarchy value
   */
  public FileFolderList getHierarchy() {
    return hierarchy;
  }


  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public void build(Connection db) throws SQLException {
    if (linkModuleId == -1 || linkItemId == -1) {
      throw new SQLException("ID not specified");
    }
    hierarchy = new FileFolderList();
    hierarchy.setLinkModuleId(linkModuleId);
    hierarchy.setLinkItemId(linkItemId);
    hierarchy.setTopLevelOnly(true);
    hierarchy.buildList(db);
    buildItems(db, hierarchy, 1);
    FileFolderList buffer = hierarchy.buildCompleteHierarchy();
  }


  /**
   * Description of the Method
   *
   * @param db       Description of the Parameter
   * @param parentId Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public void build(Connection db, int parentId) throws SQLException {
    if (linkModuleId == -1 || linkItemId == -1) {
      throw new SQLException("ID not specified");
    }
    hierarchy = new FileFolderList();
    hierarchy.setLinkModuleId(linkModuleId);
    hierarchy.setLinkItemId(linkItemId);
    hierarchy.setParentId(parentId);
    hierarchy.buildList(db);
    buildItems(db, hierarchy, 1);
    FileFolderList buffer = hierarchy.buildCompleteHierarchy();
  }


  /**
   * Description of the Method
   *
   * @param db         Description of the Parameter
   * @param folderList Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  private void buildItems(Connection db, FileFolderList folderList) throws SQLException {
    Iterator i = folderList.iterator();
    while (i.hasNext()) {
      FileFolder folder = (FileFolder) i.next();
      FileFolderList folders = new FileFolderList();
      folders.setParentId(folder.getId());
      folders.buildList(db);
      folder.setSubFolders(folders);
      buildItems(db, folders);
    }
  }


  /**
   * Description of the Method
   *
   * @param db         Description of the Parameter
   * @param folderList Description of the Parameter
   * @param level      Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  private void buildItems(Connection db, FileFolderList folderList, int level) throws SQLException {
    Iterator i = (Iterator) folderList.iterator();
    while (i.hasNext()) {
      FileFolder folder = (FileFolder) i.next();
      folder.setLevel(level);
      FileFolderList folders = new FileFolderList();
      folders.setParentId(folder.getId());
      folders.buildList(db);
      folder.setSubFolders(folders);
      buildItems(db, folders, level + 1);
    }
  }
}

