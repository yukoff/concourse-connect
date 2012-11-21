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

package com.concursive.connect.web.modules.blog.dao;

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
 * Contains a collection of blog post categories
 *
 * @author matt rajkowski
 * @created June 23, 2003
 */
public class BlogPostCategoryList extends ArrayList<BlogPostCategory> {
  private int projectId = -1;
  private int enabled = Constants.UNDEFINED;
  private int includeId = -1;


  /**
   * Constructor for the NewsArticleCategoryList object
   */
  public BlogPostCategoryList() {
  }


  /**
   * Gets the projectId attribute of the NewsArticleCategoryList object
   *
   * @return The projectId value
   */
  public int getProjectId() {
    return projectId;
  }


  /**
   * Sets the projectId attribute of the NewsArticleCategoryList object
   *
   * @param tmp The new projectId value
   */
  public void setProjectId(int tmp) {
    this.projectId = tmp;
  }


  /**
   * Sets the projectId attribute of the NewsArticleCategoryList object
   *
   * @param tmp The new projectId value
   */
  public void setProjectId(String tmp) {
    this.projectId = Integer.parseInt(tmp);
  }


  /**
   * Gets the enabled attribute of the NewsArticleCategoryList object
   *
   * @return The enabled value
   */
  public int getEnabled() {
    return enabled;
  }


  /**
   * Sets the enabled attribute of the NewsArticleCategoryList object
   *
   * @param tmp The new enabled value
   */
  public void setEnabled(int tmp) {
    this.enabled = tmp;
  }


  /**
   * Sets the enabled attribute of the NewsArticleCategoryList object
   *
   * @param tmp The new enabled value
   */
  public void setEnabled(String tmp) {
    this.enabled = Integer.parseInt(tmp);
  }


  /**
   * Gets the includeId attribute of the NewsArticleCategoryList object
   *
   * @return The includeId value
   */
  public int getIncludeId() {
    return includeId;
  }


  /**
   * Sets the includeId attribute of the NewsArticleCategoryList object
   *
   * @param tmp The new includeId value
   */
  public void setIncludeId(int tmp) {
    this.includeId = tmp;
  }


  /**
   * Sets the includeId attribute of the NewsArticleCategoryList object
   *
   * @param tmp The new includeId value
   */
  public void setIncludeId(String tmp) {
    this.includeId = Integer.parseInt(tmp);
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
    StringBuffer sqlFilter = new StringBuffer();
    StringBuffer sqlOrder = new StringBuffer();
    //Set the order
    sqlOrder.append("ORDER BY c.project_id, c.level, c.category_name ");
    createFilter(sqlFilter);
    //Need to build a base SQL statement for returning records
    sqlSelect.append("SELECT ");
    sqlSelect.append(
        "c.* " +
            "FROM project_news_category c " +
            "WHERE c.category_id > -1 ");
    pst = db.prepareStatement(sqlSelect.toString() + sqlFilter.toString() + sqlOrder.toString());
    items = prepareFilter(pst);
    rs = pst.executeQuery();
    while (rs.next()) {
      BlogPostCategory thisCategory = new BlogPostCategory(rs);
      this.add(thisCategory);
    }
    rs.close();
    pst.close();
  }


  /**
   * Description of the Method
   *
   * @param sqlFilter Description of the Parameter
   */
  protected void createFilter(StringBuffer sqlFilter) {
    if (projectId > 0) {
      sqlFilter.append("AND c.project_id = ? ");
    }
    if (enabled != Constants.UNDEFINED) {
      if (includeId == -1) {
        sqlFilter.append("AND c.enabled = ? ");
      } else {
        sqlFilter.append("AND (c.enabled = ? OR c.category_id = ?) ");
      }
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
    if (enabled != Constants.UNDEFINED) {
      if (includeId == -1) {
        pst.setBoolean(++i, enabled == Constants.TRUE);
      } else {
        pst.setBoolean(++i, enabled == Constants.TRUE);
        pst.setInt(++i, includeId);
      }
    }
    return i;
  }


  /**
   * Gets the valueFromId attribute of the NewsArticleCategoryList object
   *
   * @param id Description of the Parameter
   * @return The valueFromId value
   */
  public String getValueFromId(int id) {
    Iterator i = this.iterator();
    while (i.hasNext()) {
      BlogPostCategory thisCategory = (BlogPostCategory) i.next();
      if (thisCategory.getId() == id) {
        return thisCategory.getName();
      }
    }
    return "--None--";
  }

  public int getIdFromValue(String name) {
    for (BlogPostCategory thisCategory : this) {
      if (thisCategory.getName().equals(name)) {
        return thisCategory.getId();
      }
    }
    return -1;
  }


  /**
   * Gets the htmlSelect attribute of the NewsArticleCategoryList object
   *
   * @param selectName Description of the Parameter
   * @param selectedId Description of the Parameter
   * @return The htmlSelect value
   */
  public String getHtmlSelect(String selectName, int selectedId) {
    HtmlSelect thisSelect = new HtmlSelect();
    thisSelect.addItem(-1, "-- None --");
    for (BlogPostCategory thisCategory : this) {
      thisSelect.addItem(
          thisCategory.getId(),
          thisCategory.getName());
    }
    return thisSelect.getHtml(selectName, selectedId);
  }


  /**
   * Gets the htmlSelect attribute of the NewsArticleCategoryList object
   *
   * @return The htmlSelect value
   */
  public HtmlSelect getHtmlSelect() {
    HtmlSelect thisSelect = new HtmlSelect();
    for (BlogPostCategory thisCategory : this) {
      thisSelect.addItem(
          thisCategory.getId(),
          thisCategory.getName());
    }
    return thisSelect;
  }


  /**
   * Description of the Method
   *
   * @param db     Description of the Parameter
   * @param params Description of the Parameter
   * @param names  Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public void updateValues(Connection db, String[] params, String[] names) throws SQLException {
    boolean commit = db.getAutoCommit();
    try {
      if (commit) {
        db.setAutoCommit(false);
      }
      // Put into something manageable
      ArrayList arrayList = new ArrayList();
      for (int i = 0; i < params.length; i++) {
        arrayList.add(params[i]);
      }

      // BEGIN TRANSACTION

      // Iterate through this article list
      Iterator items = this.iterator();
      while (items.hasNext()) {
        BlogPostCategory thisCategory = (BlogPostCategory) items.next();
        // If item is not in the passed array, then disable the entry
        if (!arrayList.contains(String.valueOf(thisCategory.getId()))) {
          thisCategory.setEnabled(false);
          thisCategory.update(db);
          items.remove();
        }
      }

      // Iterate through the array
      for (int i = 0; i < params.length; i++) {
        if (params[i].startsWith("*")) {
          // TODO: Check to see if a previously disabled entry has the same name,
          // and enable it

          // New item, add it at the correct position
          BlogPostCategory thisCategory = new BlogPostCategory();
          thisCategory.setProjectId(projectId);
          thisCategory.setName(names[i]);
          thisCategory.setLevel(i);
          thisCategory.insert(db);
          this.add(thisCategory);
        } else {
          // Existing item, update the name and position
          updateName(db, Integer.parseInt(params[i]), names[i]);
          updateLevel(db, Integer.parseInt(params[i]), i);
          updateEnabled(db, Integer.parseInt(params[i]), true);
        }
      }
      if (commit) {
        db.commit();
      }
    } catch (Exception e) {
      if (commit) {
        db.rollback();
      }
      throw new SQLException(e.getMessage());
    }
    finally {
      if (commit) {
        db.setAutoCommit(true);
      }
    }
  }


  /**
   * Description of the Method
   *
   * @param db    Description of the Parameter
   * @param id    Description of the Parameter
   * @param level Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public void updateLevel(Connection db, int id, int level) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "UPDATE project_news_category " +
            "SET level = ? " +
            "WHERE category_id = ? ");
    int i = 0;
    pst.setInt(++i, level);
    pst.setInt(++i, id);
    pst.executeUpdate();
    pst.close();
  }


  public void updateEnabled(Connection db, int id, boolean enabled) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "UPDATE project_news_category " +
            "SET enabled = ? " +
            "WHERE category_id = ? ");
    int i = 0;
    pst.setBoolean(++i, enabled);
    pst.setInt(++i, id);
    pst.executeUpdate();
    pst.close();
  }


  /**
   * Description of the Method
   *
   * @param db   Description of the Parameter
   * @param id   Description of the Parameter
   * @param name Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public void updateName(Connection db, int id, String name) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "UPDATE project_news_category " +
            "SET category_name = ? " +
            "WHERE category_id = ? ");
    int i = 0;
    pst.setString(++i, name);
    pst.setInt(++i, id);
    pst.executeUpdate();
    pst.close();
  }


  /**
   * Description of the Method
   *
   * @param db        Description of the Parameter
   * @param projectId Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public static void delete(Connection db, int projectId) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "DELETE FROM project_news_category " +
            "WHERE project_id = ? ");
    pst.setInt(1, projectId);
    pst.execute();
    pst.close();
  }

  public void insert(Connection db, HashMap map) throws SQLException {
    for (BlogPostCategory category : this) {
      int currentId = category.getId();
      category.setId(-1);
      category.setProjectId(projectId);
      category.insert(db);
      int newId = category.getId();
      map.put(new Integer(currentId), new Integer(newId));
    }
  }

  /**
   * Gets the selectedValue attribute of the LookupList object
   *
   * @param selectedId Description of Parameter
   * @return The selectedValue value
   */
  public String getValueFromId(String selectedId) {
    try {
      return getValueFromId(Integer.parseInt(selectedId));
    } catch (Exception e) {
      return "";
    }
  }
}

