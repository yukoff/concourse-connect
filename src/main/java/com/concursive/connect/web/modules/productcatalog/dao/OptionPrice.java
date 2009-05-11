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
import com.concursive.commons.web.mvc.beans.GenericBean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Description of the Class
 *
 * @author matt rajkowski
 * @version $Id$
 * @created September 21, 2004
 */
public class OptionPrice extends GenericBean {

  private int id = -1;
  private int valueId = -1;
  private String description = null;
  private String invoiceText = null;
  private int rangeLow = -1;
  private int rangeHigh = -1;
  private int rangeBlock = -1;
  private double priceAmount = 0;
  private double pricePerQty = 0;
  private double priceMultiplier = 1;
  private boolean priceQtyMultiplier = false;
  private double priceAddOn = 0;
  private boolean enabled = false;
  private boolean invalid = false;


  /**
   * Constructor for the OptionPrice object
   */
  public OptionPrice() {
  }


  /**
   * Constructor for the OptionPrice object
   *
   * @param rs Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public OptionPrice(ResultSet rs) throws SQLException {
    buildRecord(rs);
  }


  /**
   * Constructor for the OptionPrice object
   *
   * @param db      Description of the Parameter
   * @param priceId Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public OptionPrice(Connection db, int priceId) throws SQLException {
    queryRecord(db, priceId);
  }


  /**
   * Gets the id attribute of the OptionPrice object
   *
   * @return The id value
   */
  public int getId() {
    return id;
  }


  /**
   * Sets the id attribute of the OptionPrice object
   *
   * @param tmp The new id value
   */
  public void setId(int tmp) {
    this.id = tmp;
  }


  /**
   * Sets the id attribute of the OptionPrice object
   *
   * @param tmp The new id value
   */
  public void setId(String tmp) {
    this.id = Integer.parseInt(tmp);
  }


  /**
   * Gets the valueId attribute of the OptionPrice object
   *
   * @return The valueId value
   */
  public int getValueId() {
    return valueId;
  }


  /**
   * Sets the valueId attribute of the OptionPrice object
   *
   * @param tmp The new valueId value
   */
  public void setValueId(int tmp) {
    this.valueId = tmp;
  }


  /**
   * Sets the valueId attribute of the OptionPrice object
   *
   * @param tmp The new valueId value
   */
  public void setValueId(String tmp) {
    this.valueId = Integer.parseInt(tmp);
  }


  /**
   * Gets the description attribute of the OptionPrice object
   *
   * @return The description value
   */
  public String getDescription() {
    return description;
  }


  /**
   * Sets the description attribute of the OptionPrice object
   *
   * @param tmp The new description value
   */
  public void setDescription(String tmp) {
    this.description = tmp;
  }

  public String getInvoiceText() {
    return invoiceText;
  }

  public void setInvoiceText(String invoiceText) {
    this.invoiceText = invoiceText;
  }

  /**
   * Gets the rangeLow attribute of the OptionPrice object
   *
   * @return The rangeLow value
   */
  public int getRangeLow() {
    return rangeLow;
  }


  /**
   * Sets the rangeLow attribute of the OptionPrice object
   *
   * @param tmp The new rangeLow value
   */
  public void setRangeLow(int tmp) {
    this.rangeLow = tmp;
  }


  /**
   * Sets the rangeLow attribute of the OptionPrice object
   *
   * @param tmp The new rangeLow value
   */
  public void setRangeLow(String tmp) {
    this.rangeLow = Integer.parseInt(tmp);
  }


  /**
   * Gets the rangeHigh attribute of the OptionPrice object
   *
   * @return The rangeHigh value
   */
  public int getRangeHigh() {
    return rangeHigh;
  }


  /**
   * Sets the rangeHigh attribute of the OptionPrice object
   *
   * @param tmp The new rangeHigh value
   */
  public void setRangeHigh(int tmp) {
    this.rangeHigh = tmp;
  }


  /**
   * Sets the rangeHigh attribute of the OptionPrice object
   *
   * @param tmp The new rangeHigh value
   */
  public void setRangeHigh(String tmp) {
    this.rangeHigh = Integer.parseInt(tmp);
  }

  public int getRangeBlock() {
    return rangeBlock;
  }

  public void setRangeBlock(int rangeBlock) {
    this.rangeBlock = rangeBlock;
  }

  public void setRangeBlocks(String rangeBlocks) {
    this.rangeBlock = Integer.parseInt(rangeBlocks);
  }

  /**
   * Gets the priceAmount attribute of the OptionPrice object
   *
   * @return The priceAmount value
   */
  public double getPriceAmount() {
    return priceAmount;
  }


  /**
   * Sets the priceAmount attribute of the OptionPrice object
   *
   * @param tmp The new priceAmount value
   */
  public void setPriceAmount(double tmp) {
    this.priceAmount = tmp;
  }


  /**
   * Sets the priceAmount attribute of the OptionPrice object
   *
   * @param tmp The new priceAmount value
   */
  public void setPriceAmount(String tmp) {
    this.priceAmount = Double.parseDouble(tmp);
  }


  /**
   * Gets the pricePerQty attribute of the OptionPrice object
   *
   * @return The pricePerQty value
   */
  public double getPricePerQty() {
    return pricePerQty;
  }


  /**
   * Sets the pricePerQty attribute of the OptionPrice object
   *
   * @param tmp The new pricePerQty value
   */
  public void setPricePerQty(double tmp) {
    this.pricePerQty = tmp;
  }


  /**
   * Sets the pricePerQty attribute of the OptionPrice object
   *
   * @param tmp The new pricePerQty value
   */
  public void setPricePerQty(String tmp) {
    this.pricePerQty = Double.parseDouble(tmp);
  }


  /**
   * Gets the priceMultiplier attribute of the OptionPrice object
   *
   * @return The priceMultiplier value
   */
  public double getPriceMultiplier() {
    return priceMultiplier;
  }


  /**
   * Sets the priceMultiplier attribute of the OptionPrice object
   *
   * @param tmp The new priceMultiplier value
   */
  public void setPriceMultiplier(double tmp) {
    this.priceMultiplier = tmp;
  }


  /**
   * Sets the priceMultiplier attribute of the OptionPrice object
   *
   * @param tmp The new priceMultiplier value
   */
  public void setPriceMultiplier(String tmp) {
    this.priceMultiplier = Double.parseDouble(tmp);
  }


  /**
   * Gets the priceQtyMultiplier attribute of the OptionPrice object
   *
   * @return The priceQtyMultiplier value
   */
  public boolean getPriceQtyMultiplier() {
    return priceQtyMultiplier;
  }


  /**
   * Sets the priceQtyMultiplier attribute of the OptionPrice object
   *
   * @param tmp The new priceQtyMultiplier value
   */
  public void setPriceQtyMultiplier(boolean tmp) {
    this.priceQtyMultiplier = tmp;
  }


  /**
   * Sets the priceQtyMultiplier attribute of the OptionPrice object
   *
   * @param tmp The new priceQtyMultiplier value
   */
  public void setPriceQtyMultiplier(String tmp) {
    this.priceQtyMultiplier = DatabaseUtils.parseBoolean(tmp);
  }


  /**
   * Gets the priceAddOn attribute of the OptionPrice object
   *
   * @return The priceAddOn value
   */
  public double getPriceAddOn() {
    return priceAddOn;
  }


  /**
   * Sets the priceAddOn attribute of the OptionPrice object
   *
   * @param tmp The new priceAddOn value
   */
  public void setPriceAddOn(double tmp) {
    this.priceAddOn = tmp;
  }


  /**
   * Sets the priceAddOn attribute of the OptionPrice object
   *
   * @param tmp The new priceAddOn value
   */
  public void setPriceAddOn(String tmp) {
    this.priceAddOn = Double.parseDouble(tmp);
  }


  /**
   * Gets the enabled attribute of the OptionPrice object
   *
   * @return The enabled value
   */
  public boolean getEnabled() {
    return enabled;
  }


  /**
   * Sets the enabled attribute of the OptionPrice object
   *
   * @param tmp The new enabled value
   */
  public void setEnabled(boolean tmp) {
    this.enabled = tmp;
  }


  /**
   * Sets the enabled attribute of the OptionPrice object
   *
   * @param tmp The new enabled value
   */
  public void setEnabled(String tmp) {
    this.enabled = DatabaseUtils.parseBoolean(tmp);
  }


  /**
   * Gets the invalid attribute of the OptionPrice object
   *
   * @return The invalid value
   */
  public boolean getInvalid() {
    return invalid;
  }


  /**
   * Sets the invalid attribute of the OptionPrice object
   *
   * @param tmp The new invalid value
   */
  public void setInvalid(boolean tmp) {
    this.invalid = tmp;
  }


  /**
   * Sets the invalid attribute of the OptionPrice object
   *
   * @param tmp The new invalid value
   */
  public void setInvalid(String tmp) {
    this.invalid = DatabaseUtils.parseBoolean(tmp);
  }


  /**
   * Description of the Method
   *
   * @param db      Description of the Parameter
   * @param priceId Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public void queryRecord(Connection db, int priceId) throws SQLException {
    if (priceId == -1) {
      throw new SQLException("ID not specified");
    }
    PreparedStatement pst = db.prepareStatement(
        "SELECT cop.* " +
            "FROM catalog_option_price cop " +
            "WHERE cop.price_id = ? ");
    pst.setInt(1, priceId);
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      buildRecord(rs);
    }
    rs.close();
    pst.close();
    if (id == -1) {
      throw new SQLException("ID not found");
    }
  }


  /**
   * Description of the Method
   *
   * @param rs Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public void buildRecord(ResultSet rs) throws SQLException {
    id = rs.getInt("price_id");
    description = rs.getString("description");
    rangeLow = DatabaseUtils.getInt(rs, "range_low");
    rangeHigh = DatabaseUtils.getInt(rs, "range_high");
    valueId = DatabaseUtils.getInt(rs, "value_id");
    enabled = rs.getBoolean("enabled");
    invalid = rs.getBoolean("invalid");
    priceAmount = DatabaseUtils.getDouble(rs, "price_amount", 0);
    pricePerQty = DatabaseUtils.getDouble(rs, "price_per_qty", 0);
    priceMultiplier = DatabaseUtils.getDouble(rs, "price_multiplier", 1);
    priceQtyMultiplier = rs.getBoolean("price_qty_multiplier");
    priceAddOn = DatabaseUtils.getDouble(rs, "price_add_on", 0);
    rangeBlock = DatabaseUtils.getInt(rs, "range_block");
    invoiceText = rs.getString("invoice_text");
  }


  /**
   * Gets the amountByQuantity attribute of the OptionPrice object
   *
   * @param quantity Description of the Parameter
   * @return The amountByQuantity value
   */
  public double getAmountByQuantity(double quantity) {
    return (quantity * pricePerQty) + priceAmount;
  }


  /**
   * Gets the qtyMultiplier attribute of the OptionPrice object
   *
   * @param quantity Description of the Parameter
   * @return The qtyMultiplier value
   */
  public double getQtyMultiplier(double quantity) {
    if (priceQtyMultiplier) {
      return quantity;
    }
    return 1;
  }
}

