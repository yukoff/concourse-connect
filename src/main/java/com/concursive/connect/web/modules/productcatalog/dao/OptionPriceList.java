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
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Description of the Class
 *
 * @author matt rajkowski
 * @version $Id$
 * @created September 21, 2004
 */
public class OptionPriceList extends ArrayList {

  private PagedListInfo pagedListInfo = null;
  private int enabled = Constants.UNDEFINED;
  private int optionId = -1;


  /**
   * Gets the pagedListInfo attribute of the OptionList object
   *
   * @return The pagedListInfo value
   */
  public PagedListInfo getPagedListInfo() {
    return pagedListInfo;
  }


  /**
   * Sets the pagedListInfo attribute of the OptionPriceList object
   *
   * @param tmp The new pagedListInfo value
   */
  public void setPagedListInfo(PagedListInfo tmp) {
    this.pagedListInfo = tmp;
  }


  /**
   * Gets the enabled attribute of the OptionPriceList object
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
   * Gets the optionId attribute of the OptionPriceList object
   *
   * @return The optionId value
   */
  public int getOptionId() {
    return optionId;
  }


  /**
   * Sets the optionId attribute of the OptionPriceList object
   *
   * @param tmp The new optionId value
   */
  public void setOptionId(int tmp) {
    this.optionId = tmp;
  }


  /**
   * Sets the optionId attribute of the OptionPriceList object
   *
   * @param tmp The new optionId value
   */
  public void setOptionId(String tmp) {
    this.optionId = Integer.parseInt(tmp);
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
            "FROM catalog_option_price cop " +
            "WHERE cop.price_id > 0 ");
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
      pagedListInfo.setDefaultSort("cop.price_id", null);
      //Determine the offset
      pagedListInfo.appendSqlTail(db, sqlOrder);
    } else {
      sqlOrder.append("ORDER BY cop.price_id ");
    }
    //Need to build a base SQL statement for returning records
    if (pagedListInfo != null) {
      pagedListInfo.appendSqlSelectHead(db, sqlSelect);
    } else {
      sqlSelect.append("SELECT ");
    }
    sqlSelect.append(
        "cop.* " +
            "FROM catalog_option_price cop " +
            "WHERE cop.price_id > 0 ");
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
      OptionPrice optionPrice = new OptionPrice(rs);
      this.add(optionPrice);
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
      sqlFilter.append("AND cop.enabled = ? ");
    }
    if (optionId > -1) {
      sqlFilter.append("AND cop.option_id = ? ");
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
    if (optionId > -1) {
      pst.setInt(++i, optionId);
    }
    return i;
  }


  /**
   * Description of the Method
   *
   * @param quantity Description of the Parameter
   * @return Description of the Return Value
   */
  public double findAmountBasedOnQuantity(double quantity) {
    double amount = 0;
    Iterator p = this.iterator();
    while (p.hasNext()) {
      OptionPrice optionPrice = (OptionPrice) p.next();
      if (optionPrice.getRangeLow() == -1 && quantity <= optionPrice.getRangeHigh()) {

/*
        if (optionPrice.getRangeBlock() > 0) {
          if (quantity % optionPrice.getRangeBlock() != 0) {
            return false;
          }
        }
*/
        if (optionPrice.getRangeBlock() > 0) {
          while (quantity % optionPrice.getRangeBlock() != 0) {
            ++quantity;
          }
        }
        amount = optionPrice.getAmountByQuantity(quantity);
      }
      if (optionPrice.getRangeLow() <= quantity && quantity <= optionPrice.getRangeHigh()) {
        if (optionPrice.getRangeBlock() > 0) {
          while (quantity % optionPrice.getRangeBlock() != 0) {
            ++quantity;
          }
        }
        amount = optionPrice.getAmountByQuantity(quantity);
      }
      if (optionPrice.getRangeLow() <= quantity && optionPrice.getRangeHigh() == -1) {
        if (optionPrice.getRangeBlock() > 0) {
          while (quantity % optionPrice.getRangeBlock() != 0) {
            ++quantity;
          }
        }
        amount = optionPrice.getAmountByQuantity(quantity);
      }
    }
    return amount;
  }


  public String getInvoiceText(double quantity) {
    String description = "";
    Iterator p = this.iterator();
    while (p.hasNext()) {
      OptionPrice optionPrice = (OptionPrice) p.next();
      if (optionPrice.getRangeLow() == -1 && quantity <= optionPrice.getRangeHigh()) {
        description = optionPrice.getInvoiceText();
      }
      if (optionPrice.getRangeLow() <= quantity && quantity <= optionPrice.getRangeHigh()) {
        description = optionPrice.getInvoiceText();
      }
      if (optionPrice.getRangeLow() <= quantity && optionPrice.getRangeHigh() == -1) {
        description = optionPrice.getInvoiceText();
      }
    }
    return description;
  }


  /**
   * Description of the Method
   *
   * @param value Description of the Parameter
   * @return Description of the Return Value
   */
  public double findAmountBasedOnSelection(int value) {
    double amount = 0;
    Iterator p = this.iterator();
    while (p.hasNext()) {
      OptionPrice optionPrice = (OptionPrice) p.next();
      if (value == optionPrice.getValueId()) {
        amount = optionPrice.getPriceAmount();
      }
    }
    return amount;
  }


  /**
   * Gets the qtyMultiplier attribute of the OptionPriceList object
   *
   * @param quantity Description of the Parameter
   * @return The qtyMultiplier value
   */
  public double getQtyMultiplier(double quantity) {
    double multiplier = 1;
    Iterator p = this.iterator();
    while (p.hasNext()) {
      OptionPrice optionPrice = (OptionPrice) p.next();
      if (optionPrice.getRangeLow() == -1 && quantity <= optionPrice.getRangeHigh()) {
        multiplier = optionPrice.getQtyMultiplier(quantity);
      }
      if (optionPrice.getRangeLow() <= quantity && quantity <= optionPrice.getRangeHigh()) {
        multiplier = optionPrice.getQtyMultiplier(quantity);
      }
      if (optionPrice.getRangeLow() <= quantity && optionPrice.getRangeHigh() == -1) {
        multiplier = optionPrice.getQtyMultiplier(quantity);
      }
    }
    return multiplier;
  }


  /**
   * Gets the quantityValid attribute of the OptionPriceList object
   *
   * @param quantity Description of the Parameter
   * @return The quantityValid value
   */
  public boolean isQuantityValid(double quantity) {
    Iterator p = this.iterator();
    while (p.hasNext()) {
      OptionPrice optionPrice = (OptionPrice) p.next();
      if (optionPrice.getRangeLow() == -1 && quantity <= optionPrice.getRangeHigh()) {
        if (optionPrice.getInvalid()) {
          return false;
        }
      }
      if (optionPrice.getRangeLow() <= quantity && quantity <= optionPrice.getRangeHigh()) {
        if (optionPrice.getInvalid()) {
          return false;
        }
      }
      if (optionPrice.getRangeLow() <= quantity && optionPrice.getRangeHigh() == -1) {
        if (optionPrice.getInvalid()) {
          return false;
        }
      }
    }
    return true;
  }


  /**
   * Description of the Method
   *
   * @param value Description of the Parameter
   * @return Description of the Return Value
   */
  public double findMultiplier(int value) {
    double multiplier = 1;
    Iterator p = this.iterator();
    while (p.hasNext()) {
      OptionPrice optionPrice = (OptionPrice) p.next();
      if (value == optionPrice.getValueId()) {
        multiplier = optionPrice.getPriceMultiplier();
      }
    }
    return multiplier;
  }


  /**
   * Description of the Method
   *
   * @param value Description of the Parameter
   * @return Description of the Return Value
   */
  public double findAddOn(int value) {
    double addOn = 0;
    Iterator p = this.iterator();
    while (p.hasNext()) {
      OptionPrice optionPrice = (OptionPrice) p.next();
      if (value == optionPrice.getValueId()) {
        addOn = optionPrice.getPriceAddOn();
      }
    }
    return addOn;
  }
}


