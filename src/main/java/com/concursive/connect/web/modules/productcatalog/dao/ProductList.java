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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Iterator;

/**
 * Provides methods for generating a collection of product objects from the database
 *
 * @author matt rajkowski
 * @version $Id$
 * @created September 21, 2004
 */
public class ProductList extends ArrayList<Product> {

  private PagedListInfo pagedListInfo = null;
  private int enabled = Constants.UNDEFINED;
  private int orderId = -1;
  private int parentId = -1;
  private int parents = Constants.UNDEFINED;
  private int productId = -1;
  private int showInCatalog = Constants.UNDEFINED;
  private int cartEnabled = Constants.UNDEFINED;


  /**
   * Gets the pagedListInfo attribute of the ProductList object
   *
   * @return The pagedListInfo value
   */
  public PagedListInfo getPagedListInfo() {
    return pagedListInfo;
  }


  /**
   * Sets the pagedListInfo attribute of the ProductList object
   *
   * @param tmp The new pagedListInfo value
   */
  public void setPagedListInfo(PagedListInfo tmp) {
    this.pagedListInfo = tmp;
  }


  /**
   * Gets the enabled attribute of the ProductList object
   *
   * @return The enabled value
   */
  public int getEnabled() {
    return enabled;
  }


  /**
   * Sets the enabled attribute of the ProductList object
   *
   * @param tmp The new enabled value
   */
  public void setEnabled(int tmp) {
    this.enabled = tmp;
  }


  /**
   * Sets the enabled attribute of the ProductList object
   *
   * @param tmp The new enabled value
   */
  public void setEnabled(String tmp) {
    this.enabled = Integer.parseInt(tmp);
  }


  /**
   * Gets the orderId attribute of the ProductList object
   *
   * @return The orderId value
   */
  public int getOrderId() {
    return orderId;
  }


  /**
   * Sets the orderId attribute of the ProductList object
   *
   * @param tmp The new orderId value
   */
  public void setOrderId(int tmp) {
    this.orderId = tmp;
  }


  /**
   * Sets the orderId attribute of the ProductList object
   *
   * @param tmp The new orderId value
   */
  public void setOrderId(String tmp) {
    this.orderId = Integer.parseInt(tmp);
  }


  public int getProductId() {
    return productId;
  }

  public void setProductId(int productId) {
    this.productId = productId;
  }

  /**
   * Gets the parentId attribute of the ProductList object
   *
   * @return The parentId value
   */
  public int getParentId() {
    return parentId;
  }


  /**
   * Sets the parentId attribute of the ProductList object
   *
   * @param tmp The new parentId value
   */
  public void setParentId(int tmp) {
    this.parentId = tmp;
  }


  /**
   * Sets the parentId attribute of the ProductList object
   *
   * @param tmp The new parentId value
   */
  public void setParentId(String tmp) {
    this.parentId = Integer.parseInt(tmp);
  }


  /**
   * Gets the parents attribute of the ProductList object
   *
   * @return The parents value
   */
  public int getParents() {
    return parents;
  }


  /**
   * Sets the parents attribute of the ProductList object
   *
   * @param tmp The new parents value
   */
  public void setParents(int tmp) {
    this.parents = tmp;
  }


  /**
   * Sets the parents attribute of the ProductList object
   *
   * @param tmp The new parents value
   */
  public void setParents(String tmp) {
    this.parents = Integer.parseInt(tmp);
  }

  public void setShowInCatalog(int showInCatalog) {
    this.showInCatalog = showInCatalog;
  }

  public void setCartEnabled(int cartEnabled) {
    this.cartEnabled = cartEnabled;
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
            "FROM catalog_product cp " +
            "WHERE cp.product_id > 0 ");
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
      pagedListInfo.setDefaultSort("cp.product_name", null);
      //Determine the offset
      pagedListInfo.appendSqlTail(db, sqlOrder);
    } else {
      sqlOrder.append("ORDER BY cp.product_name ");
    }
    //Need to build a base SQL statement for returning records
    if (pagedListInfo != null) {
      pagedListInfo.appendSqlSelectHead(db, sqlSelect);
    } else {
      sqlSelect.append("SELECT ");
    }
    sqlSelect.append(
        "cp.* " +
            "FROM catalog_product cp " +
            "WHERE cp.product_id > 0 ");
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
      Product product = new Product(rs);
      this.add(product);
    }
    rs.close();
    pst.close();
  }


  /**
   * Description of the Method
   *
   * @param sqlFilter Description of the Parameter
   * @param db        Description of the Parameter
   */
  private void createFilter(StringBuffer sqlFilter, Connection db) {
    if (enabled != Constants.UNDEFINED) {
      sqlFilter.append("AND cp.enabled = ? ");
    }
    if (parentId > -1) {
      sqlFilter.append("AND cp.parent_id = ? ");
    }
    if (parents == Constants.TRUE) {
      sqlFilter.append("AND cp.parent_id IS NULL ");
    }
    if (parents == Constants.FALSE) {
      sqlFilter.append("AND cp.parent_id IS NOT NULL ");
    }
    if (orderId > -1) {
      sqlFilter.append("AND cp.product_id IN (SELECT product_id FROM customer_order_product WHERE order_id = ?) ");
    }
    if (productId > -1) {
      sqlFilter.append("AND cp.product_id = ? ");
    }
    if (showInCatalog != Constants.UNDEFINED) {
      sqlFilter.append("AND cp.show_in_catalog = ? ");
    }
    if (cartEnabled != Constants.UNDEFINED) {
      sqlFilter.append("AND cp.cart_enabled = ? ");
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
    if (parentId > -1) {
      pst.setInt(++i, parentId);
    }
    if (orderId > -1) {
      pst.setInt(++i, orderId);
    }
    if (productId > -1) {
      pst.setInt(++i, productId);
    }
    if (showInCatalog != Constants.UNDEFINED) {
      pst.setBoolean(++i, (showInCatalog == Constants.TRUE));
    }
    if (cartEnabled != Constants.UNDEFINED) {
      pst.setBoolean(++i, (cartEnabled == Constants.TRUE));
    }
    return i;
  }


  /**
   * Gets the totalPrice attribute of the ProductList object
   *
   * @return The totalPrice value
   */
  public double getTotalPrice() {
    double amount = 0;
    for (Product product : this) {
      double tmpAmount = product.getTotalPrice();
      if (tmpAmount == -1) {
        return -1;
      }
      amount += tmpAmount;
    }
    return amount;
  }


  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  public boolean insert(Connection db) throws SQLException {
    for (Product thisProduct : this) {
      thisProduct.setOrderId(orderId);
      thisProduct.insert(db);
    }
    return true;
  }


  /**
   * Description of the Method
   *
   * @return Description of the Return Value
   */
  public String toString() {
    StringBuffer out = new StringBuffer();
    for (Product thisProduct : this) {
      out.append(thisProduct.toString());
    }
    NumberFormat formatter = NumberFormat.getCurrencyInstance();
    Currency currency = Currency.getInstance("USD");
    formatter.setCurrency(currency);
    out.append("GRAND TOTAL: " + formatter.format(getTotalPrice()) + "\r\n");
    out.append("\r\n");
    return out.toString();
  }

  public String getIdRange() {
    StringBuffer sb = new StringBuffer();
    Iterator i = this.iterator();
    while (i.hasNext()) {
      Product thisProduct = (Product) i.next();
      sb.append(thisProduct.getId());
      if (i.hasNext()) {
        sb.append(",");
      }
    }
    return sb.toString();
  }
}
