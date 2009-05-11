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

package com.concursive.connect.web.webdav.context;

import com.concursive.connect.web.modules.documents.dao.FileFolder;
import com.concursive.connect.web.modules.documents.dao.FileFolderList;
import com.concursive.connect.web.modules.documents.dao.FileItem;
import com.concursive.connect.web.modules.documents.dao.FileItemList;
import org.apache.naming.resources.Resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;

/**
 * An Item Context represents a Module Context which has name-object bindings
 * An Item Context could be any entity that could have folders and files
 * associated with it. The object that is bound could either be a Folder context
 * or a File resource
 *
 * @author ananth
 * @version $Id$
 * @created November 3, 2004
 */
public class ItemContext
    extends BaseWebdavContext implements ModuleContext {

  private int linkModuleId = -1;
  private int linkItemId = -1;
  private String contextName = null;
  private String path = null;
  private String permission = null;
  private int userId = -1;


  /**
   * Sets the userId attribute of the ItemContext object
   *
   * @param tmp The new userId value
   */
  public void setUserId(int tmp) {
    this.userId = tmp;
  }


  /**
   * Sets the userId attribute of the ItemContext object
   *
   * @param tmp The new userId value
   */
  public void setUserId(String tmp) {
    this.userId = Integer.parseInt(tmp);
  }


  /**
   * Gets the userId attribute of the ItemContext object
   *
   * @return The userId value
   */
  public int getUserId() {
    return userId;
  }


  /**
   * Sets the linkModuleId attribute of the ItemContext object
   *
   * @param tmp The new linkModuleId value
   */
  public void setLinkModuleId(int tmp) {
    this.linkModuleId = tmp;
  }


  /**
   * Sets the linkModuleId attribute of the ItemContext object
   *
   * @param tmp The new linkModuleId value
   */
  public void setLinkModuleId(String tmp) {
    this.linkModuleId = Integer.parseInt(tmp);
  }


  /**
   * Sets the linkItemId attribute of the ItemContext object
   *
   * @param tmp The new linkItemId value
   */
  public void setLinkItemId(int tmp) {
    this.linkItemId = tmp;
  }


  /**
   * Sets the linkItemId attribute of the ItemContext object
   *
   * @param tmp The new linkItemId value
   */
  public void setLinkItemId(String tmp) {
    this.linkItemId = Integer.parseInt(tmp);
  }


  /**
   * Sets the contextName attribute of the ItemContext object
   *
   * @param tmp The new contextName value
   */
  public void setContextName(String tmp) {
    this.contextName = tmp;
  }


  /**
   * Sets the path attribute of the ItemContext object
   *
   * @param tmp The new path value
   */
  public void setPath(String tmp) {
    this.path = tmp;
  }


  /**
   * Sets the permission attribute of the ItemContext object
   *
   * @param tmp The new permission value
   */
  public void setPermission(String tmp) {
    this.permission = tmp;
  }


  /**
   * Gets the linkModuleId attribute of the ItemContext object
   *
   * @return The linkModuleId value
   */
  public int getLinkModuleId() {
    return linkModuleId;
  }


  /**
   * Gets the linkItemId attribute of the ItemContext object
   *
   * @return The linkItemId value
   */
  public int getLinkItemId() {
    return linkItemId;
  }


  /**
   * Gets the contextName attribute of the ItemContext object
   *
   * @return The contextName value
   */
  public String getContextName() {
    return contextName;
  }


  /**
   * Gets the path attribute of the ItemContext object
   *
   * @return The path value
   */
  public String getPath() {
    return path;
  }


  /**
   * Gets the permission attribute of the ItemContext object
   *
   * @return The permission value
   */
  public String getPermission() {
    return permission;
  }


  /**
   * Constructor for the ItemContext object
   */
  public ItemContext() {
  }


  /**
   * Constructor for the ItemContext object
   *
   * @param name     Description of the Parameter
   * @param moduleId Description of the Parameter
   * @param itemId   Description of the Parameter
   */
  public ItemContext(String name, int moduleId, int itemId) {
    this.contextName = name;
    this.linkModuleId = moduleId;
    this.linkItemId = itemId;
  }


  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @throws SQLException          Description of the Exception
   * @throws FileNotFoundException Description of the Exception
   */
  public void buildResources(Connection db) throws SQLException, FileNotFoundException {
    bindings.clear();
    if (hasPermission(db, this.linkItemId, this.userId, this.permission)) {
      populateBindings(db);
    }
  }


  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @throws SQLException          Description of the Exception
   * @throws FileNotFoundException Description of the Exception
   */
  public void populateBindings(Connection db) throws SQLException, FileNotFoundException {
    if (linkModuleId == -1) {
      throw new SQLException("Module ID not specified");
    }
    if (linkItemId == -1) {
      throw new SQLException("Item ID not specified");
    }

    populateFolderBindings(db);
    populateFileBindings(db);
  }


  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  private void populateFolderBindings(Connection db) throws SQLException {
    FileFolderList folders = new FileFolderList();
    folders.setLinkModuleId(linkModuleId);
    folders.setLinkItemId(linkItemId);
    folders.setTopLevelOnly(true);
    folders.buildList(db);
    Iterator i = folders.iterator();
    while (i.hasNext()) {
      FileFolder thisFolder = (FileFolder) i.next();
      thisFolder.buildItemCount(db);
      //TODO: determine if the user has permission to view this Folder
      //      If he has then include it in the binding list
      FolderContext context = new FolderContext();
      context.setContextName(thisFolder.getSubject());
      context.setLinkModuleId(linkModuleId);
      context.setLinkItemId(linkItemId);
      context.setFolderId(thisFolder.getId());
      context.setPath(path);
      context.setUserId(userId);
      context.setPermission(this.permission);
      bindings.put(thisFolder.getSubject(), context);
      // build properties
      buildProperties(thisFolder.getSubject(), thisFolder.getEntered(),
          thisFolder.getModified(), new Integer(thisFolder.getItemCount()));
    }
  }


  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @throws SQLException          Description of the Exception
   * @throws FileNotFoundException Description of the Exception
   */
  private void populateFileBindings(Connection db) throws SQLException, FileNotFoundException {
    FileItemList files = new FileItemList();
    files.setLinkModuleId(linkModuleId);
    files.setLinkItemId(linkItemId);
    files.setTopLevelOnly(true);
    files.setFileLibraryPath(path);
    files.buildList(db);
    Iterator j = files.iterator();
    while (j.hasNext()) {
      FileItem thisFile = (FileItem) j.next();
      // TODO: determine if the user has permission to view this File Item
      // If he has then include it in the binding list
      File file = new File(thisFile.getFullFilePath());
      Resource resource = new Resource(new FileInputStream(file));
      bindings.put(thisFile.getClientFilename(), resource);
      //build properties
      buildProperties(thisFile.getClientFilename(), thisFile.getEntered(),
          thisFile.getModified(), new Integer(thisFile.getSize()));
    }
  }
}

