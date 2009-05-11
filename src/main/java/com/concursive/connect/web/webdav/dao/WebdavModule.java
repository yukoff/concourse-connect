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

package com.concursive.connect.web.webdav.dao;

import com.concursive.commons.db.DatabaseUtils;
import com.concursive.connect.web.webdav.context.ModuleContext;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Description of the Class
 *
 * @author ananth
 * @version $Id$
 * @created November 3, 2004
 */
public class WebdavModule {
  private int id = -1;
  private int categoryId = -1;
  private String className = null;
  private String displayName = null;
  private ModuleContext context = null;
  private java.sql.Timestamp entered = null;
  private int enteredBy = -1;
  private java.sql.Timestamp modified = null;
  private int modifiedBy = -1;
  // resources
  private boolean buildContext = false;
  private String fileLibraryPath = null;


  /**
   * Sets the buildContext attribute of the WebdavModule object
   *
   * @param tmp The new buildContext value
   */
  public void setBuildContext(boolean tmp) {
    this.buildContext = tmp;
  }


  /**
   * Sets the buildContext attribute of the WebdavModule object
   *
   * @param tmp The new buildContext value
   */
  public void setBuildContext(String tmp) {
    this.buildContext = DatabaseUtils.parseBoolean(tmp);
  }


  /**
   * Gets the buildContext attribute of the WebdavModule object
   *
   * @return The buildContext value
   */
  public boolean getBuildContext() {
    return buildContext;
  }


  /**
   * Sets the fileLibraryPath attribute of the WebdavModule object
   *
   * @param tmp The new fileLibraryPath value
   */
  public void setFileLibraryPath(String tmp) {
    this.fileLibraryPath = tmp;
  }


  /**
   * Gets the fileLibraryPath attribute of the WebdavModule object
   *
   * @return The fileLibraryPath value
   */
  public String getFileLibraryPath() {
    return fileLibraryPath;
  }


  /**
   * Sets the id attribute of the WebdavModule object
   *
   * @param tmp The new id value
   */
  public void setId(int tmp) {
    this.id = tmp;
  }


  /**
   * Sets the id attribute of the WebdavModule object
   *
   * @param tmp The new id value
   */
  public void setId(String tmp) {
    this.id = Integer.parseInt(tmp);
  }


  /**
   * Sets the categoryId attribute of the WebdavModule object
   *
   * @param tmp The new categoryId value
   */
  public void setCategoryId(int tmp) {
    this.categoryId = tmp;
  }


  /**
   * Sets the categoryId attribute of the WebdavModule object
   *
   * @param tmp The new categoryId value
   */
  public void setCategoryId(String tmp) {
    this.categoryId = Integer.parseInt(tmp);
  }


  /**
   * Sets the className attribute of the WebdavModule object
   *
   * @param tmp The new className value
   */
  public void setClassName(String tmp) {
    this.className = tmp;
  }


  /**
   * Sets the displayName attribute of the WebdavModule object
   *
   * @param tmp The new displayName value
   */
  public void setDisplayName(String tmp) {
    this.displayName = tmp;
  }


  /**
   * Sets the context attribute of the WebdavModule object
   *
   * @param tmp The new context value
   */
  public void setContext(ModuleContext tmp) {
    this.context = tmp;
  }


  /**
   * Sets the entered attribute of the WebdavModule object
   *
   * @param tmp The new entered value
   */
  public void setEntered(java.sql.Timestamp tmp) {
    this.entered = tmp;
  }


  /**
   * Sets the entered attribute of the WebdavModule object
   *
   * @param tmp The new entered value
   */
  public void setEntered(String tmp) {
    this.entered = DatabaseUtils.parseTimestamp(tmp);
  }


  /**
   * Sets the enteredBy attribute of the WebdavModule object
   *
   * @param tmp The new enteredBy value
   */
  public void setEnteredBy(int tmp) {
    this.enteredBy = tmp;
  }


  /**
   * Sets the enteredBy attribute of the WebdavModule object
   *
   * @param tmp The new enteredBy value
   */
  public void setEnteredBy(String tmp) {
    this.enteredBy = Integer.parseInt(tmp);
  }


  /**
   * Sets the modified attribute of the WebdavModule object
   *
   * @param tmp The new modified value
   */
  public void setModified(java.sql.Timestamp tmp) {
    this.modified = tmp;
  }


  /**
   * Sets the modified attribute of the WebdavModule object
   *
   * @param tmp The new modified value
   */
  public void setModified(String tmp) {
    this.modified = DatabaseUtils.parseTimestamp(tmp);
  }


  /**
   * Sets the modifiedBy attribute of the WebdavModule object
   *
   * @param tmp The new modifiedBy value
   */
  public void setModifiedBy(int tmp) {
    this.modifiedBy = tmp;
  }


  /**
   * Sets the modifiedBy attribute of the WebdavModule object
   *
   * @param tmp The new modifiedBy value
   */
  public void setModifiedBy(String tmp) {
    this.modifiedBy = Integer.parseInt(tmp);
  }


  /**
   * Gets the id attribute of the WebdavModule object
   *
   * @return The id value
   */
  public int getId() {
    return id;
  }


  /**
   * Gets the categoryId attribute of the WebdavModule object
   *
   * @return The categoryId value
   */
  public int getCategoryId() {
    return categoryId;
  }


  /**
   * Gets the className attribute of the WebdavModule object
   *
   * @return The className value
   */
  public String getClassName() {
    return className;
  }


  /**
   * Gets the displayName attribute of the WebdavModule object
   *
   * @return The displayName value
   */
  public String getDisplayName() {
    return displayName;
  }


  /**
   * Gets the context attribute of the WebdavModule object
   *
   * @return The context value
   */
  public ModuleContext getContext() {
    return context;
  }


  /**
   * Gets the entered attribute of the WebdavModule object
   *
   * @return The entered value
   */
  public java.sql.Timestamp getEntered() {
    return entered;
  }


  /**
   * Gets the enteredBy attribute of the WebdavModule object
   *
   * @return The enteredBy value
   */
  public int getEnteredBy() {
    return enteredBy;
  }


  /**
   * Gets the modified attribute of the WebdavModule object
   *
   * @return The modified value
   */
  public java.sql.Timestamp getModified() {
    return modified;
  }


  /**
   * Gets the modifiedBy attribute of the WebdavModule object
   *
   * @return The modifiedBy value
   */
  public int getModifiedBy() {
    return modifiedBy;
  }


  /**
   * Constructor for the WebdavModule object
   */
  public WebdavModule() {
  }


  /**
   * Constructor for the WebdavModule object
   *
   * @param rs Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public WebdavModule(ResultSet rs) throws SQLException {
    buildRecord(rs);
  }


  /**
   * Constructor for the WebdavModule object
   *
   * @param db Description of the Parameter
   * @param id Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public WebdavModule(Connection db, int id) throws SQLException {
    queryRecord(db, id);
  }


  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @param id Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public void queryRecord(Connection db, int id) throws SQLException {
    if (id == -1) {
      throw new SQLException("Invalid Module ID specified");
    }
    PreparedStatement pst = db.prepareStatement(
        "SELECT w.*, pc.category " +
            "FROM webdav w " +
            "LEFT JOIN permission_category pc ON (w.category_id = pc.category_id) " +
            "WHERE id = ? ");
    pst.setInt(1, id);
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      buildRecord(rs);
    }
    rs.close();
    pst.close();
    if (this.getId() == -1) {
      throw new SQLException("Webdav Module record not found");
    }
    if (buildContext) {
      buildContext();
    }
  }


  /**
   * Description of the Method
   */
  protected void buildContext() {
    if (className != null) {
      try {
        context = (ModuleContext) Class.forName(className).newInstance();
      } catch (ClassNotFoundException e) {
        e.printStackTrace(System.out);
      } catch (InstantiationException e) {
        e.printStackTrace(System.out);
      } catch (IllegalAccessException e) {
        e.printStackTrace(System.out);
      }
    }
  }


  /**
   * Description of the Method
   *
   * @param db     Description of the Parameter
   * @param userId Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public void buildResources(Connection db, int userId) throws SQLException {
    if (context != null) {
      context.buildResources(db, userId, fileLibraryPath);
    }
  }


  /**
   * Description of the Method
   *
   * @param rs Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  protected void buildRecord(ResultSet rs) throws SQLException {
    //webdav table
    id = rs.getInt("id");
    categoryId = rs.getInt("category_id");
    className = rs.getString("class_name");
    entered = rs.getTimestamp("entered");
    enteredBy = rs.getInt("enteredby");
    modified = rs.getTimestamp("modified");
    modifiedBy = rs.getInt("modifiedby");
    //permission_category table
    displayName = rs.getString("category");
  }


  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  public boolean insert(Connection db) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "INSERT INTO webdav " +
            "(category_id, className, enteredby, modifiedby) " +
            "VALUES (?, ?, ?, ?) ");
    int i = 0;
    pst.setInt(++i, categoryId);
    pst.setString(++i, className);
    pst.setInt(++i, enteredBy);
    pst.setInt(++i, modifiedBy);
    pst.execute();
    pst.close();
    id = DatabaseUtils.getCurrVal(db, "webdav_id_seq", -1);
    return true;
  }


  /**
   * Gets the permission attribute of the WebdavModule object
   *
   * @return The permission value
   */
  public String getPermission() {
    if (context != null) {
      return getContext().getPermission();
    } else {
      return "";
    }
  }

}

