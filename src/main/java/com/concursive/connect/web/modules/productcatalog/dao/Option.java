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

import bsh.Interpreter;
import com.concursive.commons.db.DatabaseUtils;
import com.concursive.commons.web.mvc.beans.GenericBean;
import com.concursive.connect.Constants;

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
public class Option extends GenericBean {

  public final static int TYPE_UNDEFINED = -1;
  public final static int TYPE_INTEGER = 1;
  public final static int TYPE_LOOKUPLIST = 2;
  public final static int TYPE_STRING = 3;
  public final static int TYPE_TERMS_AND_CONDITIONS = 4;

  private int id = -1;
  private int orderItemId = -1;
  private String name = null;
  private String text = null;
  private int type = TYPE_UNDEFINED;
  private boolean enabled = false;
  private int level = 0;
  private String defaultValue = null;
  private String additionalText = null;
  private String validationScript = null;
  private String skuModifier = null;
  // Helpers
  private OptionPriceList priceList = null;
  private OptionValueList valueList = null;
  private String lastError = null;


  /**
   * Constructor for the Option object
   */
  public Option() {
  }


  /**
   * Constructor for the Option object
   *
   * @param rs Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public Option(ResultSet rs) throws SQLException {
    buildRecord(rs);
  }


  /**
   * Constructor for the Option object
   *
   * @param db        Description of the Parameter
   * @param productId Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public Option(Connection db, int productId) throws SQLException {
    queryRecord(db, productId);
  }


  /**
   * Gets the id attribute of the Option object
   *
   * @return The id value
   */
  public int getId() {
    return id;
  }


  /**
   * Sets the id attribute of the Option object
   *
   * @param tmp The new id value
   */
  public void setId(int tmp) {
    this.id = tmp;
  }


  /**
   * Sets the id attribute of the Option object
   *
   * @param tmp The new id value
   */
  public void setId(String tmp) {
    this.id = Integer.parseInt(tmp);
  }


  /**
   * Gets the orderItemId attribute of the Option object
   *
   * @return The orderItemId value
   */
  public int getOrderItemId() {
    return orderItemId;
  }


  /**
   * Sets the orderItemId attribute of the Option object
   *
   * @param tmp The new orderItemId value
   */
  public void setOrderItemId(int tmp) {
    this.orderItemId = tmp;
  }


  /**
   * Sets the orderItemId attribute of the Option object
   *
   * @param tmp The new orderItemId value
   */
  public void setOrderItemId(String tmp) {
    this.orderItemId = Integer.parseInt(tmp);
  }


  /**
   * Gets the name attribute of the Option object
   *
   * @return The name value
   */
  public String getName() {
    return name;
  }


  /**
   * Sets the name attribute of the Option object
   *
   * @param tmp The new name value
   */
  public void setName(String tmp) {
    this.name = tmp;
  }


  /**
   * Gets the type attribute of the Option object
   *
   * @return The type value
   */
  public int getType() {
    return type;
  }


  /**
   * Sets the type attribute of the Option object
   *
   * @param tmp The new type value
   */
  public void setType(int tmp) {
    this.type = tmp;
  }


  /**
   * Sets the type attribute of the Option object
   *
   * @param tmp The new type value
   */
  public void setType(String tmp) {
    this.type = Integer.parseInt(tmp);
  }


  /**
   * Gets the enabled attribute of the Option object
   *
   * @return The enabled value
   */
  public boolean getEnabled() {
    return enabled;
  }


  /**
   * Sets the enabled attribute of the Option object
   *
   * @param tmp The new enabled value
   */
  public void setEnabled(boolean tmp) {
    this.enabled = tmp;
  }


  /**
   * Sets the enabled attribute of the Option object
   *
   * @param tmp The new enabled value
   */
  public void setEnabled(String tmp) {
    this.enabled = DatabaseUtils.parseBoolean(tmp);
  }

  public int getLevel() {
    return level;
  }

  public void setLevel(int level) {
    this.level = level;
  }

  public void setLevel(String level) {
    this.level = Integer.parseInt(level);
  }

  /**
   * Gets the defaultValue attribute of the Option object
   *
   * @return The defaultValue value
   */
  public String getDefaultValue() {
    return defaultValue;
  }


  /**
   * Sets the defaultValue attribute of the Option object
   *
   * @param tmp The new defaultValue value
   */
  public void setDefaultValue(String tmp) {
    this.defaultValue = tmp;
  }


  /**
   * Gets the additionalText attribute of the Option object
   *
   * @return The additionalText value
   */
  public String getAdditionalText() {
    return additionalText;
  }


  /**
   * Sets the additionalText attribute of the Option object
   *
   * @param tmp The new additionalText value
   */
  public void setAdditionalText(String tmp) {
    this.additionalText = tmp;
  }

  public String getValidationScript() {
    return validationScript;
  }

  public void setValidationScript(String validationScript) {
    this.validationScript = validationScript;
  }

  /**
   * Gets the priceList attribute of the Option object
   *
   * @return The priceList value
   */
  public OptionPriceList getPriceList() {
    return priceList;
  }


  /**
   * Sets the priceList attribute of the Option object
   *
   * @param tmp The new priceList value
   */
  public void setPriceList(OptionPriceList tmp) {
    this.priceList = tmp;
  }


  /**
   * Gets the valueList attribute of the Option object
   *
   * @return The valueList value
   */
  public OptionValueList getValueList() {
    return valueList;
  }


  /**
   * Sets the valueList attribute of the Option object
   *
   * @param tmp The new valueList value
   */
  public void setValueList(OptionValueList tmp) {
    this.valueList = tmp;
  }

  public String getLastError() {
    return lastError;
  }

  public void setLastError(String lastError) {
    this.lastError = lastError;
  }

  public String getSkuModifier() {
    return skuModifier;
  }

  public void setSkuModifier(String skuModifier) {
    this.skuModifier = skuModifier;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  /**
   * Description of the Method
   *
   * @param db       Description of the Parameter
   * @param optionId Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public void queryRecord(Connection db, int optionId) throws SQLException {
    if (optionId == -1) {
      throw new SQLException("ID not specified");
    }
    PreparedStatement pst = db.prepareStatement(
        "SELECT co.* " +
            "FROM catalog_option co " +
            "WHERE co.option_id = ? ");
    pst.setInt(1, optionId);
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
    id = rs.getInt("option_id");
    name = rs.getString("option_name");
    type = DatabaseUtils.getInt(rs, "option_type");
    defaultValue = rs.getString("default_value");
    enabled = rs.getBoolean("enabled");
    additionalText = rs.getString("additional_text");
    validationScript = rs.getString("validation_script");
    skuModifier = rs.getString("option_sku_modifier");
    level = DatabaseUtils.getInt(rs, "level", 0);
    text = rs.getString("option_text");
  }


  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public void buildResources(Connection db) throws SQLException {
    // price list
    priceList = new OptionPriceList();
    priceList.setOptionId(id);
    priceList.setEnabled(Constants.TRUE);
    priceList.buildList(db);
    // value list
    if (type == TYPE_LOOKUPLIST) {
      valueList = new OptionValueList();
      valueList.setOptionId(id);
      valueList.setEnabled(Constants.TRUE);
      valueList.buildList(db);
    }
  }


  /**
   * Gets the amount attribute of the Option object
   *
   * @return The amount value
   */
  public double getAmount() {
    double amount = 0;
    if (type == Option.TYPE_INTEGER) {
      double quantity = Double.parseDouble(defaultValue);
      amount = priceList.findAmountBasedOnQuantity(quantity);
    }
    if (type == Option.TYPE_LOOKUPLIST) {
      amount = priceList.findAmountBasedOnSelection(Integer.parseInt(defaultValue));
    }
    return amount;
  }


  /**
   * Gets the multiplier attribute of the Option object
   *
   * @return The multiplier value
   */
  public double getMultiplier() {
    double multiplier = 1;
    if (type == Option.TYPE_LOOKUPLIST) {
      multiplier = multiplier * priceList.findMultiplier(Integer.parseInt(defaultValue));
    }
    if (type == Option.TYPE_INTEGER) {
      multiplier = multiplier * priceList.getQtyMultiplier(Double.parseDouble(defaultValue));
    }
    return multiplier;
  }


  /**
   * Gets the addOn attribute of the Option object
   *
   * @return The addOn value
   */
  public double getAddOn() {
    double addOn = 0;
    if (type == Option.TYPE_LOOKUPLIST) {
      addOn += priceList.findAddOn(Integer.parseInt(defaultValue));
    }
    return addOn;
  }


  /**
   * Gets the valid attribute of the Option object
   *
   * @return The valid value
   */
  public boolean isValid() {
    lastError = null;
    if (type == Option.TYPE_INTEGER) {
      try {
        int quantityWholeNumber = Integer.parseInt(defaultValue);
        double quantity = Double.parseDouble(defaultValue);
        return priceList.isQuantityValid(quantity);
      } catch (Exception e) {
        lastError = "Quantity is invalid";
        return false;
      }
    }
    if (type == Option.TYPE_LOOKUPLIST) {
      try {
        int selection = Integer.parseInt(defaultValue);
        return true;
      } catch (Exception e) {
        lastError = "An invalid selection was made";
        return false;
      }
    }
    if (validationScript != null) {
      Interpreter script = new Interpreter();
      try {
        script.set("thisOption", this);
        script.eval(validationScript);
      } catch (Exception e) {
        try {
          lastError = (String) script.get("error");
        } catch (Exception ce) {

        }
        if (System.getProperty("DEBUG") != null) {
          e.printStackTrace();
        }
        return false;
      }
    }
    if (type == Option.TYPE_TERMS_AND_CONDITIONS) {
      if (!"yes".equals(defaultValue)) {
        lastError = "The terms and conditions need to be accepted";
        return false;
      }
    }
    return true;
  }


  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  public boolean insert(Connection db) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "INSERT INTO customer_order_product_options " +
            "(item_id, option_id, option_name, option_value, price_amount) VALUES " +
            "(?,?,?,?,?) ");
    int i = 0;
    pst.setInt(++i, orderItemId);
    pst.setInt(++i, id);
    pst.setString(++i, name);
    pst.setString(++i, defaultValue);
    pst.setDouble(++i, this.getAmount());
    pst.execute();
    pst.close();
    //customer_order_product_options_item_option_id_seq
    return true;
  }


  /**
   * Gets the configuredSummary attribute of the Option object
   *
   * @return The configuredSummary value
   */
  public String getConfiguredSummary() {
    if (type == TYPE_LOOKUPLIST) {
      return (name + ": " + valueList.getSelectedValue(Integer.parseInt(defaultValue)));
    } else if (type == TYPE_INTEGER) {
      return (name + ": " + defaultValue);
    } else if (type == TYPE_STRING) {
      return (name + ": " + defaultValue);
    } else if (type == TYPE_TERMS_AND_CONDITIONS) {
      return (name + ": " + defaultValue);
    }
    return null;
  }

  public String getInvoiceText() {
    if (type == Option.TYPE_INTEGER) {
      double quantity = Double.parseDouble(defaultValue);
      return priceList.getInvoiceText(quantity);
    }
    return "";
  }
}

