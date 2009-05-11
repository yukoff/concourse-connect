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

package com.concursive.connect.web.modules.contribution.dao;

import com.concursive.commons.db.DatabaseUtils;
import com.concursive.connect.Constants;
import com.concursive.connect.web.utils.HtmlSelect;
import com.concursive.connect.web.utils.PagedListInfo;

import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Description of the Class
 *
 * @author Kailash Bhoopalam
 * @version $Id$
 * @created January 29, 2009
 */
public class LookupContributionList extends ArrayList<LookupContribution> {

  protected PagedListInfo pagedListInfo = null;
  protected boolean showDisabledFlag = true;
  protected int enabled = Constants.UNDEFINED;

  public LookupContributionList() {
  }

  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public LookupContributionList(Connection db) throws SQLException {
    Statement st = null;
    ResultSet rs = null;

    StringBuffer sql = new StringBuffer();
    sql.append(
        "SELECT * " +
            "FROM lookup_contribution lc " +
            "WHERE code > 0 " +
            "ORDER BY level ");
    st = db.createStatement();
    rs = st.executeQuery(sql.toString());
    while (rs.next()) {
      LookupContribution thisElement = new LookupContribution(rs);
      this.add(thisElement);
    }
    rs.close();
    st.close();
  }

  /**
   * Gets the pagedListInfo attribute of the LookupList object
   *
   * @return The pagedListInfo value
   */
  public PagedListInfo getPagedListInfo() {
    return pagedListInfo;
  }


  /**
   * Sets the pagedListInfo attribute of the LookupList object
   *
   * @param pagedListInfo The new pagedListInfo value
   */
  public void setPagedListInfo(PagedListInfo pagedListInfo) {
    this.pagedListInfo = pagedListInfo;
  }

  /**
   * Gets the showDisabledFlag attribute of the LookupList object
   *
   * @return The showDisabledFlag value
   */
  public boolean getShowDisabledFlag() {
    return showDisabledFlag;
  }

  /**
   * Sets the showDisabledFlag attribute of the LookupList object
   *
   * @param showDisabledFlag The new showDisabledFlag value
   */
  public void setShowDisabledFlag(boolean showDisabledFlag) {
    this.showDisabledFlag = showDisabledFlag;
  }

  public void setShowDisabledFlag(String showDisabledFlag) {
    this.showDisabledFlag = DatabaseUtils.parseBoolean(showDisabledFlag);
  }


  /**
   * @return the enabled
   */
  public int getEnabled() {
    return enabled;
  }

  /**
   * @param enabled the enabled to set
   */
  public void setEnabled(int enabled) {
    this.enabled = enabled;
  }

  public void setEnabled(String enabled) {
    this.enabled = Integer.parseInt(enabled);
  }

  /**
   * Description of the Method
   *
   * @param db Description of Parameter
   * @throws SQLException Description of Exception
   */
  public void buildList(Connection db) throws SQLException {
    PreparedStatement pst = null;
    ResultSet rs = queryList(db, pst);
    int count = 0;
    while (rs.next()) {
      if (pagedListInfo != null && pagedListInfo.getItemsPerPage() > 0 &&
          DatabaseUtils.getType(db) == DatabaseUtils.MSSQL &&
          count >= pagedListInfo.getItemsPerPage()) {
        break;
      }
      LookupContribution thisElement = new LookupContribution(rs);
      boolean enabled = thisElement.getEnabled();
      if (enabled || !showDisabledFlag) {
        ++count;
        this.add(thisElement);
      }
    }
    rs.close();
    if (pst != null) {
      pst.close();
    }
  }


  /**
   * This method is required for synchronization, it allows for the resultset
   * to be streamed with lower overhead
   *
   * @param db  Description of the Parameter
   * @param pst Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  public ResultSet queryList(Connection db, PreparedStatement pst) throws SQLException {
    ResultSet rs = null;
    int items = -1;
    StringBuffer sqlCount = new StringBuffer();
    StringBuffer sqlOrder = new StringBuffer();
    StringBuffer sqlFilter = new StringBuffer();
    StringBuffer sqlSelect = new StringBuffer();
    sqlCount.append(
        "SELECT COUNT(*) AS recordcount " +
            "FROM lookup_contribution lc " +
            "WHERE code > -1 ");
    createFilter(sqlFilter);
    if (pagedListInfo != null) {
      //Get the total number of records matching filter
      pst = db.prepareStatement(sqlCount.toString() + sqlFilter.toString());
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
        pst = db.prepareStatement(sqlCount.toString() + sqlFilter.toString() +
            "AND description < ? ");
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
      pagedListInfo.setDefaultSort("description ", null);
      pagedListInfo.appendSqlTail(db, sqlOrder);
    } else {
      sqlOrder.append("ORDER BY level,description ");
    }
    if (pagedListInfo != null) {
      pagedListInfo.appendSqlSelectHead(db, sqlSelect);
    } else {
      sqlSelect.append("SELECT ");
    }
    sqlSelect.append(
        "* " +
            "FROM lookup_contribution lc " +
            "WHERE code > -1 ");
    pst = db.prepareStatement(sqlSelect.toString() + sqlFilter.toString() + sqlOrder.toString());
    items = prepareFilter(pst);
    rs = pst.executeQuery();
    if (pagedListInfo != null) {
      pagedListInfo.doManualOffset(db, rs);
    }
    return rs;
  }

  private void createFilter(StringBuffer sqlFilter) {
    if (sqlFilter == null) {
      sqlFilter = new StringBuffer();
    }
    if (enabled != Constants.UNDEFINED) {
      sqlFilter.append("AND enabled = ? ");
    }
  }

  private int prepareFilter(PreparedStatement pst) throws SQLException {
    int i = 0;
    if (enabled != Constants.UNDEFINED) {
      pst.setBoolean(++i, enabled == Constants.TRUE);
    }
    return i;
  }

  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public void delete(Connection db) throws SQLException {
    Iterator<LookupContribution> i = this.iterator();
    while (i.hasNext()) {
      LookupContribution thisLookupContribution = (LookupContribution) i.next();
      thisLookupContribution.delete(db);
    }
  }

  public int getEnabledElementCount() {
    int count = 0;
    for (LookupContribution thisElement : this) {
      if (thisElement.getEnabled()) {
        count++;
      }
    }
    return count;
  }

  public String getHtmlSelect(String selectName, int defaultKey) {
    HtmlSelect thisSelect = getHtmlSelectObj(defaultKey);
    return thisSelect.getHtml(selectName);
  }

  public HtmlSelect getHtmlSelectObj(int defaultKey) {
    HtmlSelect thisSelect = new HtmlSelect();
    Iterator<LookupContribution> i = this.iterator();
    boolean keyFound = false;
    int lookupDefault = defaultKey;
    while (i.hasNext()) {
      LookupContribution thisElement = i.next();
      if (thisElement.getEnabled() || !showDisabledFlag) {
        thisSelect.addItem(thisElement.getId(), thisElement.getDescription());
      } else if (thisElement.getId() == defaultKey) {
        thisSelect.addItem(thisElement.getId(), thisElement.getDescription());
      }
      if (thisElement.getId() == defaultKey) {
        keyFound = true;
      }
    }
    if (keyFound) {
      thisSelect.setDefaultKey(defaultKey);
    } else {
      thisSelect.setDefaultKey(lookupDefault);
    }
    return thisSelect;
  }

  public String getHtmlSelect(String selectName, String defaultValue) {
    HtmlSelect thisSelect = new HtmlSelect();
    for (LookupContribution thisElement : this) {
      if (thisElement.getEnabled()) {
        thisSelect.addItem(thisElement.getId(), thisElement.getDescription());
      } else if (thisElement.getDescription().equals(defaultValue)) {
        thisSelect.addItem(thisElement.getId(), thisElement.getDescription());
      }
    }
    return thisSelect.getHtml(selectName, defaultValue);
  }

  /**
   * Gets the SelectedValue attribute of the LookupList object
   *
   * @param selectedId Description of Parameter
   * @return The SelectedValue value
   */
  public String getValueFromId(int selectedId) {
    LookupContribution keyFound = null;
    for (LookupContribution thisElement : this) {
      if (thisElement.getId() == selectedId) {
        return thisElement.getDescription();
      }
    }
    if (keyFound != null) {
      return keyFound.getDescription();
    } else {
      return "";
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

  /**
   * Description of the Method
   *
   * @param key Description of Parameter
   * @return Description of the Returned Value
   */
  public boolean containsKey(int key) {
    Iterator<LookupContribution> i = this.iterator();
    boolean keyFound = false;

    while (i.hasNext()) {
      LookupContribution thisElement = i.next();

      if (thisElement.getEnabled() && thisElement.getId() == key) {
        keyFound = true;
      }
    }

    return keyFound;
  }

  public int getIdFromValue(String roleName) {
    for (LookupContribution thisElement : this) {
      if (thisElement.getDescription().equals(roleName)) {
        return thisElement.getId();
      }
    }
    return -1;
  }

  public int getIdFromConstant(String contant) {
    for (LookupContribution thisElement : this) {
      if (thisElement.getConstant().equals(contant)) {
        return thisElement.getId();
      }
    }
    return -1;
  }

}