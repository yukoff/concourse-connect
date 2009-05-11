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

package com.concursive.connect.web.modules.issues.dao;

import com.concursive.commons.db.DatabaseUtils;
import com.concursive.connect.Constants;
import com.concursive.connect.web.utils.HtmlSelect;
import com.concursive.connect.web.utils.PagedListInfo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

/**
 * Contains TicketCategory items for displaying to the user
 *
 * @author chris
 * @created December 11, 2001
 */
public class TicketCategoryList extends Vector {
  HtmlSelect catListSelect = new HtmlSelect();
  private PagedListInfo pagedListInfo = null;
  private int parentCode = -1;
  private int catLevel = -1;
  private String HtmlJsEvent = "";
  private int enabledState = -1;
  private int projectId = -1;


  /**
   * Constructor for the TicketCategoryList object
   */
  public TicketCategoryList() {
  }


  /**
   * Sets the PagedListInfo attribute of the TicketCategoryList object
   *
   * @param tmp The new PagedListInfo value
   */
  public void setPagedListInfo(PagedListInfo tmp) {
    this.pagedListInfo = tmp;
  }


  /**
   * Sets the HtmlJsEvent attribute of the TicketCategoryList object
   *
   * @param HtmlJsEvent The new HtmlJsEvent value
   */
  public void setHtmlJsEvent(String HtmlJsEvent) {
    this.HtmlJsEvent = HtmlJsEvent;
  }


  /**
   * Sets the CatListSelect attribute of the TicketCategoryList object
   *
   * @param catListSelect The new CatListSelect value
   */
  public void setCatListSelect(HtmlSelect catListSelect) {
    this.catListSelect = catListSelect;
  }


  /**
   * Sets the ParentCode attribute of the TicketCategoryList object
   *
   * @param tmp The new ParentCode value
   */
  public void setParentCode(int tmp) {
    this.parentCode = tmp;
  }


  /**
   * Sets the ParentCode attribute of the TicketCategoryList object
   *
   * @param tmp The new ParentCode value
   */
  public void setParentCode(String tmp) {
    this.parentCode = Integer.parseInt(tmp);
  }


  /**
   * Sets the CatLevel attribute of the TicketCategoryList object
   *
   * @param catLevel The new CatLevel value
   */
  public void setCatLevel(int catLevel) {
    this.catLevel = catLevel;
  }


  /**
   * Sets the CatLevel attribute of the TicketCategoryList object
   *
   * @param catLevel The new CatLevel value
   */
  public void setCatLevel(String catLevel) {
    this.catLevel = Integer.parseInt(catLevel);
  }


  /**
   * Sets the enabledState attribute of the TicketCategoryList object
   *
   * @param tmp The new enabledState value
   */
  public void setEnabledState(int tmp) {
    this.enabledState = tmp;
  }

  public void setEnabledState(String tmp) {
    this.enabledState = DatabaseUtils.parseBooleanToConstant(tmp);
  }


  /**
   * Gets the CatListSelect attribute of the TicketCategoryList object
   *
   * @return The CatListSelect value
   */
  public HtmlSelect getCatListSelect() {
    return catListSelect;
  }


  /**
   * Gets the HtmlJsEvent attribute of the TicketCategoryList object
   *
   * @return The HtmlJsEvent value
   */
  public String getHtmlJsEvent() {
    return HtmlJsEvent;
  }


  /**
   * Gets the HtmlSelect attribute of the TicketCategoryList object
   *
   * @param selectName Description of Parameter
   * @return The HtmlSelect value
   */
  public String getHtmlSelect(String selectName) {
    return getHtmlSelect(selectName, -1);
  }


  /**
   * Gets the CatLevel attribute of the TicketCategoryList object
   *
   * @return The CatLevel value
   */
  public int getCatLevel() {
    return catLevel;
  }


  /**
   * Gets the PagedListInfo attribute of the TicketCategoryList object
   *
   * @return The PagedListInfo value
   */
  public PagedListInfo getPagedListInfo() {
    return pagedListInfo;
  }


  /**
   * Gets the ParentCode attribute of the TicketCategoryList object
   *
   * @return The ParentCode value
   */
  public int getParentCode() {
    return parentCode;
  }


  /**
   * Gets the enabledState attribute of the TicketCategoryList object
   *
   * @return The enabledState value
   */
  public int getEnabledState() {
    return enabledState;
  }

  public void setProjectId(int projectId) {
    this.projectId = projectId;
  }

  public void setProjectId(String projectId) {
    this.projectId = Integer.parseInt(projectId);
  }

  public int getProjectId() {
    return projectId;
  }


  /**
   * Gets the HtmlSelect attribute of the TicketCategoryList object
   *
   * @param selectName Description of Parameter
   * @param defaultKey Description of Parameter
   * @return The HtmlSelect value
   */
  public String getHtmlSelect(String selectName, int defaultKey) {
    Iterator i = this.iterator();
    catListSelect.addAttribute("id", selectName);
    while (i.hasNext()) {
      TicketCategory thisCat = (TicketCategory) i.next();
      String elementText = thisCat.getDescription();
      if (thisCat.getEnabled()) {
        catListSelect.addItem(
            thisCat.getId(),
            elementText);
      } else if ((!(thisCat.getEnabled()) && thisCat.getId() == defaultKey) || enabledState == -1) {
        if (catListSelect.getSelectSize() > 1) {
          HashMap colorAttribute = new HashMap();
          colorAttribute.put("style", "color: red");
          catListSelect.addItem(
              thisCat.getId(),
              elementText, colorAttribute);
        } else {
          elementText += "*";
          catListSelect.addItem(
              thisCat.getId(),
              elementText);
        }
      }
    }

    if (!(this.getHtmlJsEvent().equals(""))) {
      catListSelect.setJsEvent(this.getHtmlJsEvent());
    }

    return catListSelect.getHtml(selectName, defaultKey);
  }


  /**
   * Description of the Method
   *
   * @param db Description of Parameter
   * @throws SQLException Description of Exception
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
            "FROM ticket_category tc " +
            "WHERE tc.id > -1 ");

    createFilter(sqlFilter);

    if (pagedListInfo != null) {
      //Get the total number of records matching filter
      pst = db.prepareStatement(sqlCount.toString() +
          sqlFilter.toString());
      items = prepareFilter(pst);
      rs = pst.executeQuery();
      if (rs.next()) {
        int maxRecords = rs.getInt("recordcount");
        pagedListInfo.setMaxRecords(maxRecords);
      }
      rs.close();
      pst.close();

      //Determine the offset, based on the filter, for the first record to show
      if (!pagedListInfo.getCurrentLetter().equals("")) {
        pst = db.prepareStatement(sqlCount.toString() +
            sqlFilter.toString() +
            "AND tc.id < ? ");
        items = prepareFilter(pst);
        pst.setString(++items, pagedListInfo.getCurrentLetter().toLowerCase());
        rs = pst.executeQuery();
        if (rs.next()) {
          int offsetCount = rs.getInt("recordcount");
          pagedListInfo.setCurrentOffset(offsetCount);
        }
        rs.close();
        pst.close();
      }

      //Determine column to sort by
      pagedListInfo.setDefaultSort("tc.description", null);
      pagedListInfo.appendSqlTail(db, sqlOrder);
    } else {
      sqlOrder.append("ORDER BY cat_level, parent_cat_code, level, tc.description");
    }

    //Need to build a base SQL statement for returning records
    if (pagedListInfo != null) {
      pagedListInfo.appendSqlSelectHead(db, sqlSelect);
    } else {
      sqlSelect.append("SELECT ");
    }
    sqlSelect.append(
        "tc.* " +
            "FROM ticket_category tc " +
            "WHERE tc.id > -1 ");
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
      TicketCategory thisCat = new TicketCategory(rs);
      this.addElement(thisCat);
    }
    rs.close();
    pst.close();
  }


  /**
   * Description of the Method
   *
   * @param sqlFilter Description of Parameter
   */
  private void createFilter(StringBuffer sqlFilter) {
    if (sqlFilter == null) {
      sqlFilter = new StringBuffer();
    }
    if (enabledState != -1) {
      sqlFilter.append("AND tc.enabled = ? ");
    }
    if (parentCode != -1) {
      sqlFilter.append("AND tc.parent_cat_code = ? ");
    }
    if (catLevel != -1) {
      sqlFilter.append("AND tc.cat_level = ? ");
    }
    if (projectId != -1) {
      sqlFilter.append("AND tc.project_id = ? ");
    } else {
      sqlFilter.append("AND tc.project_id IS NULL ");
    }
  }


  /**
   * Description of the Method
   *
   * @param pst Description of Parameter
   * @return Description of the Returned Value
   * @throws SQLException Description of Exception
   */
  private int prepareFilter(PreparedStatement pst) throws SQLException {
    int i = 0;
    if (enabledState != -1) {
      pst.setBoolean(++i, enabledState == Constants.TRUE);
    }
    if (parentCode != -1) {
      pst.setInt(++i, parentCode);
    }
    if (catLevel != -1) {
      pst.setInt(++i, catLevel);
    }
    if (projectId != -1) {
      pst.setInt(++i, projectId);
    }
    return i;
  }


  /**
   * Returns just an HtmlSelect object without generating the Html output
   *
   * @param defaultKey Description of the Parameter
   * @return The htmlSelect value
   */
  public HtmlSelect getHtmlSelect(int defaultKey) {
    HtmlSelect catListSelect = new HtmlSelect();
    Iterator i = this.iterator();
    while (i.hasNext()) {
      TicketCategory thisCat = (TicketCategory) i.next();
      String elementText = thisCat.getDescription();
      if (thisCat.getEnabled()) {
        catListSelect.addItem(thisCat.getId(), elementText);
      } else if ((!thisCat.getEnabled() && thisCat.getId() == defaultKey) || enabledState == -1) {
        if (catListSelect.getSelectSize() > 1) {
          HashMap colorAttribute = new HashMap();
          colorAttribute.put("style", "color: red");
          catListSelect.addItem(thisCat.getId(), elementText, colorAttribute, false);
        } else {
          elementText += "*";
          catListSelect.addItem(thisCat.getId(), elementText);
        }
      }
    }
    return catListSelect;
  }

  public HtmlSelect getHtmlSelect() {
    HtmlSelect thisSelect = new HtmlSelect();
    Iterator i = this.iterator();
    while (i.hasNext()) {
      TicketCategory thisCategory = (TicketCategory) i.next();
      thisSelect.addItem(thisCategory.getId(),
          thisCategory.getDescription());
    }
    return thisSelect;
  }


  /**
   * Gets the idFromValue attribute of the TicketCategoryList object
   *
   * @param value Description of the Parameter
   * @return The idFromValue value
   */
  public int getIdFromValue(String value) {
    Iterator i = this.iterator();
    while (i.hasNext()) {
      TicketCategory thisCategory = (TicketCategory) i.next();
      if (value.equals(thisCategory.getDescription())) {
        return thisCategory.getId();
      }
    }
    return -1;
  }

  public boolean containsKey(int id) {
    Iterator i = this.iterator();
    while (i.hasNext()) {
      TicketCategory thisCategory = (TicketCategory) i.next();
      if (id == thisCategory.getId()) {
        return true;
      }
    }
    return false;
  }

  public TicketCategory getTicketCategory(int id) {
    Iterator i = this.iterator();
    while (i.hasNext()) {
      TicketCategory thisCategory = (TicketCategory) i.next();
      return thisCategory;
    }
    return null;
  }

  public String toString() {
    return "catLevel: " + catLevel + "\r\n" +
        "projectId: " + projectId + "\r\n" +
        "parentCode: " + parentCode;
  }

  public static void delete(Connection db, int projectId) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "DELETE FROM ticket_category " +
            "WHERE project_id = ? ");
    pst.setInt(1, projectId);
    pst.execute();
    pst.close();
  }

  public void insert(Connection db, HashMap categories) throws SQLException {
    Iterator i = this.iterator();
    while (i.hasNext()) {
      TicketCategory thisCategory = (TicketCategory) i.next();
      int oldId = thisCategory.getId();
      thisCategory.setId(-1);
      thisCategory.setProjectId(projectId);
      if (thisCategory.getParentCode() > 0) {
        int parentId = ((Integer) categories.get(new Integer(thisCategory.getParentCode()))).intValue();
        thisCategory.setParentCode(parentId);
      }
      thisCategory.insert(db);
      int newId = thisCategory.getId();
      categories.put(new Integer(oldId), new Integer(newId));
    }
  }
}

