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
import com.concursive.connect.Constants;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;

/**
 * Description of the Class
 *
 * @author ananth
 * @version $Id$
 * @created November 3, 2004
 */
public class WebdavModuleList extends HashMap {
  // Permission Category is enabled by default
  // filters
  private int enabled = Constants.TRUE;
  private int categoryId = -1;
  private String displayName = null;
  // resources
  private boolean buildContext = false;
  private String fileLibraryPath = null;


  /**
   * Sets the buildContext attribute of the WebdavModuleList object
   *
   * @param tmp The new buildContext value
   */
  public void setBuildContext(boolean tmp) {
    this.buildContext = tmp;
  }


  /**
   * Sets the buildContext attribute of the WebdavModuleList object
   *
   * @param tmp The new buildContext value
   */
  public void setBuildContext(String tmp) {
    this.buildContext = DatabaseUtils.parseBoolean(tmp);
  }


  /**
   * Gets the buildContext attribute of the WebdavModuleList object
   *
   * @return The buildContext value
   */
  public boolean getBuildContext() {
    return buildContext;
  }


  /**
   * Sets the fileLibraryPath attribute of the WebdavModuleList object
   *
   * @param tmp The new fileLibraryPath value
   */
  public void setFileLibraryPath(String tmp) {
    this.fileLibraryPath = tmp;
  }


  /**
   * Gets the fileLibraryPath attribute of the WebdavModuleList object
   *
   * @return The fileLibraryPath value
   */
  public String getFileLibraryPath() {
    return fileLibraryPath;
  }


  /**
   * Sets the enabled attribute of the WebdavModuleList object
   *
   * @param tmp The new enabled value
   */
  public void setEnabled(int tmp) {
    this.enabled = tmp;
  }


  /**
   * Sets the enabled attribute of the WebdavModuleList object
   *
   * @param tmp The new enabled value
   */
  public void setEnabled(String tmp) {
    this.enabled = Integer.parseInt(tmp);
  }


  /**
   * Sets the categoryId attribute of the WebdavModuleList object
   *
   * @param tmp The new categoryId value
   */
  public void setCategoryId(int tmp) {
    this.categoryId = tmp;
  }


  /**
   * Sets the categoryId attribute of the WebdavModuleList object
   *
   * @param tmp The new categoryId value
   */
  public void setCategoryId(String tmp) {
    this.categoryId = Integer.parseInt(tmp);
  }


  /**
   * Sets the displayName attribute of the WebdavModuleList object
   *
   * @param tmp The new displayName value
   */
  public void setDisplayName(String tmp) {
    this.displayName = tmp;
  }


  /**
   * Gets the enabled attribute of the WebdavModuleList object
   *
   * @return The enabled value
   */
  public int getEnabled() {
    return enabled;
  }


  /**
   * Gets the categoryId attribute of the WebdavModuleList object
   *
   * @return The categoryId value
   */
  public int getCategoryId() {
    return categoryId;
  }


  /**
   * Gets the displayName attribute of the WebdavModuleList object
   *
   * @return The displayName value
   */
  public String getDisplayName() {
    return displayName;
  }


  /**
   * Constructor for the WebdavModuleList object
   */
  public WebdavModuleList() {
  }


  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public void buildList(Connection db) throws SQLException {
    WebdavModule thisModule = new WebdavModule();
    thisModule.setId(-1);
    thisModule.setCategoryId(-1);
    thisModule.setClassName("com.concursive.connect.web.modules.profile.webdav.ProjectsWebdavContext");
    thisModule.setEntered(new Timestamp(System.currentTimeMillis()));
    thisModule.setEnteredBy(-1);
    thisModule.setModified(new Timestamp(System.currentTimeMillis()));
    thisModule.setModifiedBy(-1);
    thisModule.setDisplayName("Projects");
    thisModule.setFileLibraryPath(fileLibraryPath);
    thisModule.buildContext();
    this.put(thisModule.getDisplayName(), thisModule);
    /*
    StringBuffer sqlSelect = new StringBuffer();
    StringBuffer sqlFilter = new StringBuffer();
    StringBuffer sqlOrder = new StringBuffer();
    sqlSelect.append(
        "SELECT w.*, pc.category, pc.webdav " +
        "FROM webdav w " +
        "LEFT JOIN permission_category pc ON (w.category_id = pc.category_id) " +
        "WHERE w.id > 0 ");
    createFilter(sqlFilter);
    sqlOrder.append("ORDER BY id ");
    PreparedStatement pst = db.prepareStatement(sqlSelect.toString() + sqlFilter.toString() + sqlOrder.toString());
    prepareFilter(pst);
    ResultSet rs = pst.executeQuery();
    while (rs.next()) {
      WebdavModule thisModule = new WebdavModule(rs);
      // set the fileLibraryPath for documents
      thisModule.setFileLibraryPath(fileLibraryPath);
      this.put(thisModule.getDisplayName(), thisModule);
      System.out.println("WebdavModuleList-> Adding " + thisModule.getDisplayName() + " to List");
    }
    rs.close();
    pst.close();
    if (buildContext) {
      Iterator i = this.keySet().iterator();
      while (i.hasNext()) {
        String moduleName = (String) i.next();
        WebdavModule thisModule = (WebdavModule) this.get(moduleName);
        thisModule.buildContext();
      }
    }
    */
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
    sqlFilter.append("AND pc.webdav = ? ");
    if (categoryId > -1) {
      sqlFilter.append("AND w.category_id = ? ");
    }
    if (enabled != Constants.UNDEFINED) {
      sqlFilter.append("AND pc.enabled = ? ");
    }
    if (displayName != null) {
      sqlFilter.append("AND pc.category = ? ");
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
    pst.setBoolean(++i, true);
    if (categoryId > -1) {
      pst.setInt(++i, categoryId);
    }
    if (enabled != Constants.UNDEFINED) {
      pst.setBoolean(++i, enabled == Constants.TRUE);
    }
    if (displayName != null) {
      pst.setString(++i, displayName);
    }
    return i;
  }
}

