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

import com.concursive.connect.Constants;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.dao.ProjectList;
import com.concursive.connect.web.utils.HtmlSelect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Contains a collection of project assignment roles
 *
 * @author matt rajkowski
 * @version $Id$
 * @created June 6, 2005
 */
public class AssignmentRoleList extends ArrayList<AssignmentRole> {
  private int enabled = Constants.UNDEFINED;
  private int includeId = -1;
  private ArrayList categoryMap = null;
  private int categoryId = -1;

  public AssignmentRoleList() {
  }


  public int getEnabled() {
    return enabled;
  }


  public void setEnabled(int tmp) {
    this.enabled = tmp;
  }


  public void setEnabled(String tmp) {
    this.enabled = Integer.parseInt(tmp);
  }


  public int getIncludeId() {
    return includeId;
  }


  public void setIncludeId(int tmp) {
    this.includeId = tmp;
  }


  public void setIncludeId(String tmp) {
    try {
      this.includeId = Integer.parseInt(tmp);
    } catch (Exception e) {
      this.includeId = -1;
    }
  }

  public void setCategoryId(int categoryId) {
    this.categoryId = categoryId;
  }

  public void buildList(Connection db) throws SQLException {
    PreparedStatement pst = null;
    ResultSet rs = null;
    int items = -1;
    StringBuffer sqlSelect = new StringBuffer();
    StringBuffer sqlFilter = new StringBuffer();
    StringBuffer sqlOrder = new StringBuffer();
    //Set the order
    sqlOrder.append("ORDER BY ar.\"level\", ar.description ");
    createFilter(sqlFilter);
    //Need to build a base SQL statement for returning records
    sqlSelect.append("SELECT ");
    sqlSelect.append(
        "ar.* " +
            "FROM lookup_project_assignment_role ar " +
            "WHERE ar.code > -1 ");
    pst = db.prepareStatement(
        sqlSelect.toString() + sqlFilter.toString() + sqlOrder.toString());
    items = prepareFilter(pst);
    rs = pst.executeQuery();
    while (rs.next()) {
      AssignmentRole thisRole = new AssignmentRole(rs);
      this.add(thisRole);
    }
    rs.close();
    pst.close();
  }

  public void addCategories(ProjectList projects) {
    categoryMap = new ArrayList();
    Iterator i = projects.iterator();
    while (i.hasNext()) {
      Project thisProject = (Project) i.next();
      if (thisProject.getCategoryId() > -1) {
        Integer integerCategoryId = new Integer(thisProject.getCategoryId());
        if (!categoryMap.contains(integerCategoryId)) {
          categoryMap.add(integerCategoryId);
        }
      }
    }
  }

  protected void createFilter(StringBuffer sqlFilter) {
    if (enabled != Constants.UNDEFINED) {
      if (includeId == -1) {
        sqlFilter.append("AND ar.enabled = ? ");
      } else {
        sqlFilter.append("AND (ar.enabled = ? OR ar.code = ?) ");
      }
    }
    if (categoryMap != null && categoryMap.size() > 0) {
      sqlFilter.append("AND (ar.code IN (");
      Iterator i = categoryMap.iterator();
      while (i.hasNext()) {
        Integer integerCategoryId = (Integer) i.next();
        sqlFilter.append(integerCategoryId.intValue());
        if (i.hasNext()) {
          sqlFilter.append(", ");
        }
      }
      sqlFilter.append(") ");
      if (includeId > -1) {
        sqlFilter.append("OR ar.code = ? ");
      }
      sqlFilter.append(") ");
    }
    if (categoryId > -1) {
      sqlFilter.append("AND ar.code = ? ");
    }
  }


  protected int prepareFilter(PreparedStatement pst) throws SQLException {
    int i = 0;
    if (enabled != Constants.UNDEFINED) {
      if (includeId == -1) {
        pst.setBoolean(++i, enabled == Constants.TRUE);
      } else {
        pst.setBoolean(++i, enabled == Constants.TRUE);
        pst.setInt(++i, includeId);
      }
    }
    if (categoryMap != null && categoryMap.size() > 0) {
      if (includeId > -1) {
        pst.setInt(++i, includeId);
      }
    }
    if (categoryId > -1) {
      pst.setInt(++i, categoryId);
    }
    return i;
  }


  public String getValueFromId(int id) {
    Iterator i = this.iterator();
    while (i.hasNext()) {
      AssignmentRole thisRole = (AssignmentRole) i.next();
      if (thisRole.getId() == id) {
        return thisRole.getDescription();
      }
    }
    return null;
  }


  public String getHtmlSelect(String selectName, int selectedId) {
    HtmlSelect thisSelect = new HtmlSelect();
    thisSelect.addItem(-1, "-- None --");
    Iterator i = this.iterator();
    while (i.hasNext()) {
      AssignmentRole thisRole = (AssignmentRole) i.next();
      thisSelect.addItem(
          thisRole.getId(),
          thisRole.getDescription());
    }
    return thisSelect.getHtml(selectName, selectedId);
  }


  public HtmlSelect getHtmlSelect() {
    HtmlSelect thisSelect = new HtmlSelect();
    Iterator i = this.iterator();
    while (i.hasNext()) {
      AssignmentRole thisRole = (AssignmentRole) i.next();
      thisSelect.addItem(
          thisRole.getId(),
          thisRole.getDescription());
    }
    return thisSelect;
  }


  public void updateValues(Connection db, String[] params, String[] names) throws SQLException {

    // Put into something manageable
    ArrayList arrayList = new ArrayList();
    for (int i = 0; i < params.length; i++) {
      System.out.println("AssignmentRoleList-> Name: " + names[i]);
      System.out.println("AssignmentRoleList-> Param: " + params[i]);
      arrayList.add(params[i]);
    }

    // BEGIN TRANSACTION

    // Iterate through this article list
    Iterator items = this.iterator();
    while (items.hasNext()) {
      AssignmentRole thisRole = (AssignmentRole) items.next();
      // If item is not in the passed array, then disable the entry
      if (!arrayList.contains(String.valueOf(thisRole.getId()))) {
        thisRole.setEnabled(false);
        thisRole.update(db);
        items.remove();
      }
    }

    // Iterate through the array
    for (int i = 0; i < params.length; i++) {
      if (System.getProperty("DEBUG") != null) {
        System.out.println("AssignmentRoleList-> Name: " + names[i]);
        System.out.println("AssignmentRoleList-> Param: " + params[i]);
      }
      if (params[i].startsWith("*")) {
        // TODO: Check to see if a previously disabled entry has the same name,
        // and enable it


        // New item, add it at the correct position
        AssignmentRole thisRole = new AssignmentRole();
        thisRole.setDescription(names[i]);
        thisRole.setLevel(i);
        thisRole.insert(db);
        this.add(thisRole);
      } else {
        // Existing item, update the name and position
        updateName(db, Integer.parseInt(params[i]), names[i]);
        updateLevel(db, Integer.parseInt(params[i]), i);
      }
    }

    // END TRANSACTION
  }


  public void updateLevel(Connection db, int id, int level) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "UPDATE lookup_project_assignment_role " +
            "SET \"level\" = ? " +
            "WHERE code = ? ");
    int i = 0;
    pst.setInt(++i, level);
    pst.setInt(++i, id);
    pst.executeUpdate();
    pst.close();
  }


  public void updateName(Connection db, int id, String name) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "UPDATE lookup_project_assignment_role " +
            "SET description = ? " +
            "WHERE code = ? ");
    int i = 0;
    pst.setString(++i, name);
    pst.setInt(++i, id);
    pst.executeUpdate();
    pst.close();
  }

}

