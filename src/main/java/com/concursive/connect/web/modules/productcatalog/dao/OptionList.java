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

package com.concursive.connect.web.modules.productcatalog.dao;

import com.concursive.commons.db.DatabaseUtils;
import com.concursive.connect.Constants;
import com.concursive.connect.web.utils.PagedListInfo;

import javax.servlet.http.HttpServletRequest;
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
 * @version $Id$
 * @created September 21, 2004
 */
public class OptionList extends ArrayList {

  private PagedListInfo pagedListInfo = null;
  private int enabled = Constants.UNDEFINED;
  private int productId = -1;
  private int orderItemId = -1;


  /**
   * Gets the pagedListInfo attribute of the OptionList object
   *
   * @return The pagedListInfo value
   */
  public PagedListInfo getPagedListInfo() {
    return pagedListInfo;
  }


  /**
   * Sets the pagedListInfo attribute of the OptionList object
   *
   * @param tmp The new pagedListInfo value
   */
  public void setPagedListInfo(PagedListInfo tmp) {
    this.pagedListInfo = tmp;
  }


  /**
   * Gets the enabled attribute of the OptionList object
   *
   * @return The enabled value
   */
  public int getEnabled() {
    return enabled;
  }


  /**
   * Sets the enabled attribute of the OptionList object
   *
   * @param tmp The new enabled value
   */
  public void setEnabled(int tmp) {
    this.enabled = tmp;
  }


  /**
   * Sets the enabled attribute of the OptionList object
   *
   * @param tmp The new enabled value
   */
  public void setEnabled(String tmp) {
    this.enabled = Integer.parseInt(tmp);
  }


  /**
   * Gets the productId attribute of the OptionList object
   *
   * @return The productId value
   */
  public int getProductId() {
    return productId;
  }


  /**
   * Sets the productId attribute of the OptionList object
   *
   * @param tmp The new productId value
   */
  public void setProductId(int tmp) {
    this.productId = tmp;
  }


  /**
   * Sets the productId attribute of the OptionList object
   *
   * @param tmp The new productId value
   */
  public void setProductId(String tmp) {
    this.productId = Integer.parseInt(tmp);
  }


  /**
   * Gets the orderItemId attribute of the OptionList object
   *
   * @return The orderItemId value
   */
  public int getOrderItemId() {
    return orderItemId;
  }


  /**
   * Sets the orderItemId attribute of the OptionList object
   *
   * @param tmp The new orderItemId value
   */
  public void setOrderItemId(int tmp) {
    this.orderItemId = tmp;
  }


  /**
   * Sets the orderItemId attribute of the OptionList object
   *
   * @param tmp The new orderItemId value
   */
  public void setOrderItemId(String tmp) {
    this.orderItemId = Integer.parseInt(tmp);
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
    StringBuffer sqlCount = new StringBuffer();
    StringBuffer sqlFilter = new StringBuffer();
    StringBuffer sqlOrder = new StringBuffer();
    //Need to build a base SQL statement for counting records
    sqlCount.append(
        "SELECT COUNT(*) AS recordcount " +
            "FROM catalog_option co " +
            "WHERE co.option_id > 0 ");
    createFilter(sqlFilter, db);
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
      // Declare default sort, if unset
      pagedListInfo.setDefaultSort("co.level, co.option_name", null);
      //Determine the offset
      pagedListInfo.appendSqlTail(db, sqlOrder);
    } else {
      sqlOrder.append("ORDER BY co.level, co.option_name ");
    }
    //Need to build a base SQL statement for returning records
    if (pagedListInfo != null) {
      pagedListInfo.appendSqlSelectHead(db, sqlSelect);
    } else {
      sqlSelect.append("SELECT ");
    }
    sqlSelect.append(
        "co.* " +
            "FROM catalog_option co " +
            "WHERE co.option_id > 0 ");
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
      Option option = new Option(rs);
      this.add(option);
    }
    rs.close();
    pst.close();
    // Build resources
    Iterator o = this.iterator();
    while (o.hasNext()) {
      Option option = (Option) o.next();
      option.buildResources(db);
    }
  }


  /**
   * Description of the Method
   *
   * @param sqlFilter Description of the Parameter
   * @param db        Description of the Parameter
   */
  private void createFilter(StringBuffer sqlFilter, Connection db) {
    if (enabled != Constants.UNDEFINED) {
      sqlFilter.append("AND co.enabled = ? ");
    }
    if (productId > -1) {
      sqlFilter.append(
          "AND co.option_id IN " +
              "(SELECT option_id FROM catalog_product_config WHERE product_id = ?) ");
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
    if (enabled != Constants.UNDEFINED) {
      pst.setBoolean(++i, (enabled == Constants.TRUE));
    }
    if (productId > -1) {
      pst.setInt(++i, productId);
    }
    return i;
  }


  /**
   * Description of the Method
   *
   * @param request Description of the Parameter
   */
  public void populate(HttpServletRequest request) {
    int optionCount = 0;
    String optionParam = null;
    // Look for each param/value in request
    while ((optionParam = request.getParameter("optionCount" + (++optionCount))) != null) {
      String optionValue = request.getParameter("option" + optionParam);
      // Set the selected value
      Iterator i = this.iterator();
      while (i.hasNext()) {
        Option thisOption = (Option) i.next();
        if (thisOption.getId() == Integer.parseInt(optionParam)) {
          thisOption.setDefaultValue(optionValue);
          break;
        }
      }
    }
  }


  /**
   * Gets the totalPrice attribute of the OptionList object
   *
   * @return The totalPrice value
   * @throws Exception Description of the Exception
   */
  public double getTotalPrice() throws Exception {
    double totalOptionPrice = 0;
    double optionMultiplier = 1;
    double optionAddOnPrice = 0;
    Iterator o = this.iterator();
    while (o.hasNext()) {
      Option thisOption = (Option) o.next();
      totalOptionPrice += thisOption.getAmount();
      optionAddOnPrice += thisOption.getAddOn();
      optionMultiplier = optionMultiplier * thisOption.getMultiplier();
    }
    return (totalOptionPrice * optionMultiplier) + optionAddOnPrice;
  }


  /**
   * Gets the valid attribute of the OptionList object
   *
   * @return The valid value
   */
  public boolean isValid() {
    Iterator o = this.iterator();
    while (o.hasNext()) {
      Option thisOption = (Option) o.next();
      if (!thisOption.isValid()) {
        return false;
      }
    }
    return true;
  }


  public String getConfigurationErrors() {
    StringBuffer sb = new StringBuffer();
    Iterator o = this.iterator();
    while (o.hasNext()) {
      Option thisOption = (Option) o.next();
      if (thisOption.getLastError() != null) {
        if (sb.length() > 0) {
          sb.append("; ");
        }
        sb.append(thisOption.getLastError());
      }
    }
    if (sb.length() > 0) {
      return sb.toString();
    }
    return null;
  }


  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  public boolean insert(Connection db) throws SQLException {
    Iterator i = this.iterator();
    while (i.hasNext()) {
      Option thisOption = (Option) i.next();
      thisOption.setOrderItemId(orderItemId);
      thisOption.insert(db);
    }
    return true;
  }


  /**
   * Gets the configuredSummary attribute of the OptionList object
   *
   * @return The configuredSummary value
   */
  public String getConfiguredSummary() {
    StringBuffer sb = new StringBuffer();
    Iterator o = this.iterator();
    while (o.hasNext()) {
      Option thisOption = (Option) o.next();
      String text = thisOption.getConfiguredSummary();
      if (text != null) {
        if (sb.length() > 0) {
          sb.append(", ");
        }
        sb.append(text);
      }
    }
    return sb.toString();
  }

  public String getInvoiceText() {
    StringBuffer sb = new StringBuffer();
    Iterator o = this.iterator();
    while (o.hasNext()) {
      Option thisOption = (Option) o.next();
      String text = thisOption.getInvoiceText();
      if (text != null && !"".equals(text)) {
        sb.append("; ");
        sb.append(text);
      }
    }
    return sb.toString();
  }

}


