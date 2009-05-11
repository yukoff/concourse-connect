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

package com.concursive.connect.web.modules.profile.webdav;

import com.concursive.connect.Constants;
import com.concursive.connect.web.webdav.context.BaseWebdavContext;
import com.concursive.connect.web.webdav.context.ItemContext;
import com.concursive.connect.web.webdav.context.ModuleContext;

import java.sql.*;
import java.util.Hashtable;


/**
 * Description of the Class
 *
 * @author ananth
 * @version $Id$
 * @created November 11, 2004
 */
public class ProjectsWebdavContext
    extends BaseWebdavContext implements ModuleContext {

  private final static String PROJECTS = "projects";
  private int linkModuleId = Constants.PROJECTS_FILES;
  private int userId = -1;
  private String contextName = null;
  private String fileLibraryPath = null;
  private String permission = "projects-view";


  /**
   * Sets the userId attribute of the ProjectsWebdavContext object
   *
   * @param tmp The new userId value
   */
  public void setUserId(int tmp) {
    this.userId = tmp;
  }


  /**
   * Sets the userId attribute of the ProjectsWebdavContext object
   *
   * @param tmp The new userId value
   */
  public void setUserId(String tmp) {
    this.userId = Integer.parseInt(tmp);
  }


  /**
   * Gets the userId attribute of the ProjectsWebdavContext object
   *
   * @return The userId value
   */
  public int getUserId() {
    return userId;
  }


  /**
   * Sets the linkModuleId attribute of the ProjectsWebdavContext object
   *
   * @param tmp The new linkModuleId value
   */
  public void setLinkModuleId(int tmp) {
    this.linkModuleId = tmp;
  }


  /**
   * Sets the linkModuleId attribute of the ProjectsWebdavContext object
   *
   * @param tmp The new linkModuleId value
   */
  public void setLinkModuleId(String tmp) {
    this.linkModuleId = Integer.parseInt(tmp);
  }


  /**
   * Sets the contextName attribute of the ProjectsWebdavContext object
   *
   * @param tmp The new contextName value
   */
  public void setContextName(String tmp) {
    this.contextName = tmp;
  }


  /**
   * Sets the fileLibraryPath attribute of the ProjectsWebdavContext object
   *
   * @param tmp The new fileLibraryPath value
   */
  public void setFileLibraryPath(String tmp) {
    this.fileLibraryPath = tmp;
  }


  /**
   * Sets the permission attribute of the ProjectsWebdavContext object
   *
   * @param tmp The new permission value
   */
  public void setPermission(String tmp) {
    this.permission = tmp;
  }


  /**
   * Gets the linkModuleId attribute of the ProjectsWebdavContext object
   *
   * @return The linkModuleId value
   */
  public int getLinkModuleId() {
    return linkModuleId;
  }


  /**
   * Gets the contextName attribute of the ProjectsWebdavContext object
   *
   * @return The contextName value
   */
  public String getContextName() {
    return contextName;
  }


  /**
   * Gets the fileLibraryPath attribute of the ProjectsWebdavContext object
   *
   * @return The fileLibraryPath value
   */
  public String getFileLibraryPath() {
    return fileLibraryPath;
  }


  /**
   * Gets the permission attribute of the ProjectsWebdavContext object
   *
   * @return The permission value
   */
  public String getPermission() {
    return permission;
  }


  /**
   * Constructor for the ProjectsWebdavContext object
   */
  public ProjectsWebdavContext() {
  }


  /**
   * Constructor for the ProjectsWebdavContext object
   *
   * @param name         Description of the Parameter
   * @param linkModuleId Description of the Parameter
   */
  public ProjectsWebdavContext(String name, int linkModuleId) {
    this.contextName = name;
    this.linkModuleId = linkModuleId;
  }


  /**
   * Description of the Method
   *
   * @param db              Description of the Parameter
   * @param fileLibraryPath Description of the Parameter
   * @param userId          Description of the Parameter
   * @param thisSystem      Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public void buildResources(Hashtable thisSystem, Connection db, int userId, String fileLibraryPath) throws SQLException {
    this.fileLibraryPath = fileLibraryPath;
    this.userId = userId;
    bindings.clear();
    populateBindings(db);
  }


  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public void populateBindings(Connection db) throws SQLException {
    if (linkModuleId == -1) {
      throw new SQLException("Module ID not specified");
    }
    PreparedStatement pst = db.prepareStatement(
        "SELECT project_id, title, entered, modified " +
            "FROM projects " +
            "WHERE project_id > -1 " +
            "AND project_id IN (SELECT project_id FROM project_team WHERE user_id = ? " +
            "AND status IS NULL " +
            "AND portal = ?) "
    );
    pst.setInt(1, userId);
    pst.setBoolean(2, false);
    ResultSet rs = pst.executeQuery();
    while (rs.next()) {
      ItemContext item = new ItemContext();
      item.setLinkModuleId(linkModuleId);
      item.setLinkItemId(rs.getInt("project_id"));
      item.setContextName(rs.getString("title"));
      item.setPath(fileLibraryPath + "1" + fs + PROJECTS + fs);
      item.setUserId(userId);
      //TODO: is this correct?? or is it projects-
      item.setPermission("project-documents-view");
      bindings.put(item.getContextName(), item);
      Timestamp entered = rs.getTimestamp("entered");
      Timestamp modified = rs.getTimestamp("modified");
      buildProperties(item.getContextName(), entered, modified, new Integer(0));
    }
    rs.close();
    pst.close();
  }
}

