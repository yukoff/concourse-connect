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

package com.concursive.connect.web.modules.plans.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Description of the Class
 *
 * @author matt rajkowski
 * @version $Id: AssignmentFolderList.java,v 1.1 2003/02/26 05:41:38 matt Exp
 *          $
 * @created February 25, 2003
 */
public class AssignmentFolderList extends ArrayList<AssignmentFolder> {

  private int requirementId = -1;
  private int parentId = -1;


  /**
   * Constructor for the AssignmentFolderList object
   */
  public AssignmentFolderList() {
  }


  /**
   * Sets the requirementId attribute of the AssignmentFolderList object
   *
   * @param tmp The new requirementId value
   */
  public void setRequirementId(int tmp) {
    requirementId = tmp;
  }


  /**
   * Gets the requirementId attribute of the AssignmentFolderList object
   *
   * @return The requirementId value
   */
  public int getRequirementId() {
    return requirementId;
  }


  /**
   * Sets the parentId attribute of the AssignmentFolderList object
   *
   * @param tmp The new parentId value
   */
  public void setParentId(int tmp) {
    parentId = tmp;
  }


  /**
   * Gets the parentId attribute of the AssignmentFolderList object
   *
   * @return The parentId value
   */
  public int getParentId() {
    return parentId;
  }


  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public void buildList(Connection db) throws SQLException {
    StringBuffer sql = new StringBuffer();
    sql.append(
        "SELECT * " +
            "FROM project_assignments_folder " +
            "WHERE requirement_id = ? ");
    if (parentId > -1) {
      if (parentId == 0) {
        sql.append("AND parent_id IS NULL ");
      } else {
        sql.append("AND parent_id = ? ");
      }
    }
    PreparedStatement pst = db.prepareStatement(sql.toString());
    int i = 0;
    pst.setInt(++i, requirementId);
    if (parentId > 0) {
      pst.setInt(++i, parentId);
    }
    ResultSet rs = pst.executeQuery();
    while (rs.next()) {
      AssignmentFolder thisFolder = new AssignmentFolder(rs);
      this.add(thisFolder);
    }
    rs.close();
    pst.close();
  }


  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public void delete(Connection db) throws SQLException {
    Iterator folders = this.iterator();
    while (folders.hasNext()) {
      AssignmentFolder thisFolder = (AssignmentFolder) folders.next();
      thisFolder.delete(db);
    }
  }


  /**
   * Description of the Method
   *
   * @param db            Description of the Parameter
   * @param requirementId Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public static void delete(Connection db, int requirementId) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "DELETE FROM project_assignments_folder " +
            "WHERE requirement_id = ? ");
    pst.setInt(1, requirementId);
    pst.execute();
    pst.close();
  }


  /**
   * Gets the assignmentFolder attribute of the AssignmentFolderList object
   *
   * @param id Description of the Parameter
   * @return The assignmentFolder value
   */
  public AssignmentFolder getAssignmentFolder(int id) {
    Iterator i = this.iterator();
    while (i.hasNext()) {
      AssignmentFolder thisFolder = (AssignmentFolder) i.next();
      if (thisFolder.getId() == id) {
        return thisFolder;
      }
    }
    return null;
  }
}

