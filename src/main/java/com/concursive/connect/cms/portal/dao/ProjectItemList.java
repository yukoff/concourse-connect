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

package com.concursive.connect.cms.portal.dao;

import com.concursive.connect.Constants;
import com.concursive.connect.web.utils.HtmlSelect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Contains a collection of lookup items
 *
 * @author matt rajkowski
 * @created June 30, 2006
 */
public class ProjectItemList extends ArrayList<ProjectItem> {
  // Wiki
  public static final String WIKI_STATE = "lookup_wiki_state";
  public static final String WIKI_CATEGORIES = "lookup_wiki_categories";
  // Tickets
  public static final String TICKET_CAUSE = "ticket_cause";
  public static final String TICKET_RESOLUTION = "ticket_resolution";
  public static final String TICKET_DEFECT = "ticket_defect";
  public static final String TICKET_ESCALATION = "ticket_escalation";
  public static final String TICKET_STATE = "ticket_state";
  // Lists
  public static final String LIST_FUNCTIONAL_AREA = "lookup_task_functional_area";
  public static final String LIST_STATUS = "lookup_task_status";
  public static final String LIST_VALUE = "lookup_task_value";
  public static final String LIST_COMPLEXITY = "lookup_task_complexity";
  public static final String LIST_TARGET_RELEASE = "lookup_task_release";
  public static final String LIST_TARGET_SPRINT = "lookup_task_sprint";
  public static final String LIST_LOE_REMAINING = "lookup_task_loe_remaining";
  public static final String LIST_ASSIGNED_PRIORITY = "lookup_task_assigned_priority";

  // Helpers
  private String objectKeyProperty = null;

  // Filters
  private int projectId = -1;
  private int enabled = Constants.UNDEFINED;
  private int includeId = -1;
  private String name = null;


  public ProjectItemList() {
  }


  public int getProjectId() {
    return projectId;
  }


  public void setProjectId(int tmp) {
    this.projectId = tmp;
  }


  public void setProjectId(String tmp) {
    this.projectId = Integer.parseInt(tmp);
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
    this.includeId = Integer.parseInt(tmp);
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getObjectKeyProperty() {
    return objectKeyProperty;
  }

  public void setObjectKeyProperty(String objectKeyProperty) {
    this.objectKeyProperty = objectKeyProperty;
  }

  public void buildList(Connection db, String table) throws SQLException {
    PreparedStatement pst = null;
    ResultSet rs = null;
    int items = -1;
    StringBuffer sqlSelect = new StringBuffer();
    StringBuffer sqlFilter = new StringBuffer();
    StringBuffer sqlOrder = new StringBuffer();
    //Set the order
    sqlOrder.append("ORDER BY c.project_id, c.level, c.item_name ");
    createFilter(sqlFilter);
    //Need to build a base SQL statement for returning records
    sqlSelect.append("SELECT ");
    sqlSelect.append(
        "c.* " +
            "FROM " + table + " c " +
            "WHERE c.code > -1 ");
    pst = db.prepareStatement(sqlSelect.toString() + sqlFilter.toString() + sqlOrder.toString());
    items = prepareFilter(pst);
    rs = pst.executeQuery();
    while (rs.next()) {
      ProjectItem thisItem = new ProjectItem(rs);
      this.add(thisItem);
    }
    rs.close();
    pst.close();
  }


  protected void createFilter(StringBuffer sqlFilter) {
    if (projectId > 0) {
      sqlFilter.append("AND c.project_id = ? ");
    }
    if (enabled != Constants.UNDEFINED) {
      if (includeId == -1) {
        sqlFilter.append("AND c.enabled = ? ");
      } else {
        sqlFilter.append("AND (c.enabled = ? OR c.code = ?) ");
      }
    }
    if (name != null) {
      sqlFilter.append("AND c.item_name = ? ");
    }
  }


  protected int prepareFilter(PreparedStatement pst) throws SQLException {
    int i = 0;
    if (projectId > 0) {
      pst.setInt(++i, projectId);
    }
    if (enabled != Constants.UNDEFINED) {
      if (includeId == -1) {
        pst.setBoolean(++i, enabled == Constants.TRUE);
      } else {
        pst.setBoolean(++i, enabled == Constants.TRUE);
        pst.setInt(++i, includeId);
      }
    }
    if (name != null) {
      pst.setString(++i, name);
    }
    return i;
  }


  public String getValueFromId(int id) {
    Iterator i = this.iterator();
    while (i.hasNext()) {
      ProjectItem thisItem = (ProjectItem) i.next();
      if (thisItem.getId() == id) {
        return thisItem.getName();
      }
    }
    // The database will not have a 0
    if (id == 0) {
      return getValueFromId(-1);
    }
    return null;
  }

  public ProjectItem getFromId(int id) {
    Iterator i = this.iterator();
    while (i.hasNext()) {
      ProjectItem thisItem = (ProjectItem) i.next();
      if (thisItem.getId() == id) {
        return thisItem;
      }
    }
    // The database will not have a 0
    if (id == 0) {
      return getFromId(-1);
    }
    return null;
  }

  public String getHtmlSelect(String selectName, int selectedId) {
    return getHtmlSelect(selectName, selectedId, true);
  }

  public String getHtmlSelect(String selectName, int selectedId, boolean showNone) {
    HtmlSelect thisSelect = new HtmlSelect();
    if (showNone) {
      thisSelect.addItem(-1, "-- None --");
    }
    Iterator i = this.iterator();
    while (i.hasNext()) {
      ProjectItem thisItem = (ProjectItem) i.next();
      thisSelect.addItem(
          thisItem.getId(),
          thisItem.getName());
    }
    return thisSelect.getHtml(selectName, selectedId);
  }


  public HtmlSelect getHtmlSelect() {
    HtmlSelect thisSelect = new HtmlSelect();
    Iterator i = this.iterator();
    while (i.hasNext()) {
      ProjectItem thisItem = (ProjectItem) i.next();
      thisSelect.addItem(
          thisItem.getId(),
          thisItem.getName());
    }
    return thisSelect;
  }


  public void updateValues(Connection db, String[] params, String[] names, String table) throws SQLException {

    // Put into something manageable
    ArrayList<String> arrayList = new ArrayList<String>();
    if (params != null && names != null) {
      for (int i = 0; i < params.length; i++) {
        System.out.println("LookupItem-> Name: " + names[i]);
        System.out.println("LookupItem-> Param: " + params[i]);
        arrayList.add(params[i]);
      }
    }
    // BEGIN TRANSACTION

    // Iterate through this list
    Iterator items = this.iterator();
    while (items.hasNext()) {
      ProjectItem thisItem = (ProjectItem) items.next();
      // If item is not in the passed array, then disable the entry
      if (arrayList.isEmpty() || !arrayList.contains(String.valueOf(thisItem.getId()))) {
        thisItem.setEnabled(false);
        thisItem.update(db, table);
        items.remove();
      }
    }

    // Add new items and restore inactivated items
    if (params != null && names != null) {
    for (int i = 0; i < params.length; i++) {
      if (System.getProperty("DEBUG") != null) {
        System.out.println("LookupItem-> Name: " + names[i]);
        System.out.println("LookupItem-> Param: " + params[i]);
      }
      if (params[i].startsWith("*")) {
        // Check to see if a previously disabled entry has the same name,
        // and enable it
        ProjectItemList testList = new ProjectItemList();
        testList.setProjectId(projectId);
        testList.setEnabled(Constants.FALSE);
        testList.setName(names[i]);
        testList.buildList(db, table);
        if (testList.size() > 0) {
          // Found old item that can be enabled
          ProjectItem oldItem = testList.get(0);
          updateLevel(db, oldItem.getId(), i + 1, table);
          updateEnabled(db, oldItem.getId(), true, table);
          this.add(oldItem);
        } else {
          // New item, add it at the correct position
          ProjectItem thisItem = new ProjectItem();
          thisItem.setProjectId(projectId);
          thisItem.setName(names[i]);
          thisItem.setLevel(i + 1);
          thisItem.insert(db, table);
          this.add(thisItem);
        }
      } else {
        // Existing item, update the name and position
        updateName(db, Integer.parseInt(params[i]), names[i], table);
        updateLevel(db, Integer.parseInt(params[i]), i + 1, table);
      }
    }
    }

    // END TRANSACTION
  }


  public void updateLevel(Connection db, int id, int level, String table) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "UPDATE " + table + " " +
            "SET level = ? " +
            "WHERE code = ? ");
    int i = 0;
    pst.setInt(++i, level);
    pst.setInt(++i, id);
    pst.executeUpdate();
    pst.close();
  }


  public void updateName(Connection db, int id, String name, String table) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "UPDATE " + table + " " +
            "SET item_name = ? " +
            "WHERE code = ? ");
    int i = 0;
    pst.setString(++i, name);
    pst.setInt(++i, id);
    pst.executeUpdate();
    pst.close();
  }

  public void updateEnabled(Connection db, int id, boolean enabledState, String table) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "UPDATE " + table + " " +
            "SET enabled = ? " +
            "WHERE code = ? ");
    int i = 0;
    pst.setBoolean(++i, enabledState);
    pst.setInt(++i, id);
    pst.executeUpdate();
    pst.close();
  }

  public static void delete(Connection db, int projectId, String table) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "DELETE FROM " + table + " " +
            "WHERE project_id = ? ");
    pst.setInt(1, projectId);
    pst.execute();
    pst.close();
  }

  public void insert(Connection db, HashMap<Integer, Integer> map, String table) throws SQLException {
    Iterator i = this.iterator();
    while (i.hasNext()) {
      ProjectItem thisItem = (ProjectItem) i.next();
      int currentId = thisItem.getId();
      thisItem.setId(-1);
      thisItem.setProjectId(projectId);
      thisItem.insert(db, table);
      int newId = thisItem.getId();
      if (map != null) {
        map.put(currentId, newId);
      }
    }
  }
}
