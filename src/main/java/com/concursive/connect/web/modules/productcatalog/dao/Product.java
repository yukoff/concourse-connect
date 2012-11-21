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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Currency;

/**
 * Description of the Class
 *
 * @author matt rajkowski
 * @version $Id$
 * @created September 21, 2004
 */
public class Product extends GenericBean {

  private static final Log LOG = LogFactory.getLog(Product.class);

  private int id = -1;
  private int orderId = -1;
  private int orderItemId = -1;
  private String name = null;
  private String priceDescription = null;
  private String details = null;
  private double basePrice = -1;
  private String smallImage = null;
  private String largeImage = null;
  private boolean enabled = false;
  private boolean contactInformationRequired = false;
  private boolean billingAddressRequired = false;
  private boolean shippingAddressRequired = false;
  private boolean paymentRequired = false;
  private OptionList optionList = null;
  private AttachmentList attachmentList = null;
  private int uniqueId = 0;
  private String orderDescription = null;
  private String sku = null;
  private boolean showInCatalog = true;
  private boolean cartEnabled = true;
  private String actionText = null;


  /**
   * Constructor for the Product object
   */
  public Product() {
  }


  /**
   * Constructor for the Product object
   *
   * @param rs Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public Product(ResultSet rs) throws SQLException {
    buildRecord(rs);
  }


  /**
   * Constructor for the Product object
   *
   * @param db        Description of the Parameter
   * @param productId Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public Product(Connection db, int productId) throws SQLException {
    queryRecord(db, productId);
  }


  /**
   * Gets the id attribute of the Product object
   *
   * @return The id value
   */
  public int getId() {
    return id;
  }


  /**
   * Sets the id attribute of the Product object
   *
   * @param tmp The new id value
   */
  public void setId(int tmp) {
    this.id = tmp;
  }


  /**
   * Sets the id attribute of the Product object
   *
   * @param tmp The new id value
   */
  public void setId(String tmp) {
    this.id = Integer.parseInt(tmp);
  }


  /**
   * Gets the orderId attribute of the Product object
   *
   * @return The orderId value
   */
  public int getOrderId() {
    return orderId;
  }


  /**
   * Sets the orderId attribute of the Product object
   *
   * @param tmp The new orderId value
   */
  public void setOrderId(int tmp) {
    this.orderId = tmp;
  }


  /**
   * Sets the orderId attribute of the Product object
   *
   * @param tmp The new orderId value
   */
  public void setOrderId(String tmp) {
    this.orderId = Integer.parseInt(tmp);
  }


  /**
   * Gets the orderItemId attribute of the Product object
   *
   * @return The orderItemId value
   */
  public int getOrderItemId() {
    return orderItemId;
  }


  /**
   * Sets the orderItemId attribute of the Product object
   *
   * @param tmp The new orderItemId value
   */
  public void setOrderItemId(int tmp) {
    this.orderItemId = tmp;
  }


  /**
   * Sets the orderItemId attribute of the Product object
   *
   * @param tmp The new orderItemId value
   */
  public void setOrderItemId(String tmp) {
    this.orderItemId = Integer.parseInt(tmp);
  }


  /**
   * Gets the name attribute of the Product object
   *
   * @return The name value
   */
  public String getName() {
    return name;
  }


  /**
   * Sets the name attribute of the Product object
   *
   * @param tmp The new name value
   */
  public void setName(String tmp) {
    this.name = tmp;
  }


  /**
   * Gets the priceDescription attribute of the Product object
   *
   * @return The priceDescription value
   */
  public String getPriceDescription() {
    return priceDescription + ((optionList != null) ?optionList.getInvoiceText() : "");
  }


  /**
   * Sets the priceDescription attribute of the Product object
   *
   * @param tmp The new priceDescription value
   */
  public void setPriceDescription(String tmp) {
    this.priceDescription = tmp;
  }


  /**
   * Gets the details attribute of the Product object
   *
   * @return The details value
   */
  public String getDetails() {
    return details;
  }


  /**
   * Sets the details attribute of the Product object
   *
   * @param tmp The new details value
   */
  public void setDetails(String tmp) {
    this.details = tmp;
  }


  /**
   * Gets the basePrice attribute of the Product object
   *
   * @return The basePrice value
   */
  public double getBasePrice() {
    return basePrice;
  }


  /**
   * Sets the basePrice attribute of the Product object
   *
   * @param tmp The new basePrice value
   */
  public void setBasePrice(double tmp) {
    this.basePrice = tmp;
  }


  /**
   * Sets the basePrice attribute of the Product object
   *
   * @param tmp The new basePrice value
   */
  public void setBasePrice(String tmp) {
    this.basePrice = Double.parseDouble(tmp);
  }


  /**
   * Gets the smallImage attribute of the Product object
   *
   * @return The smallImage value
   */
  public String getSmallImage() {
    return smallImage;
  }


  /**
   * Sets the smallImage attribute of the Product object
   *
   * @param tmp The new smallImage value
   */
  public void setSmallImage(String tmp) {
    this.smallImage = tmp;
  }


  /**
   * Gets the largeImage attribute of the Product object
   *
   * @return The largeImage value
   */
  public String getLargeImage() {
    return largeImage;
  }


  /**
   * Sets the largeImage attribute of the Product object
   *
   * @param tmp The new largeImage value
   */
  public void setLargeImage(String tmp) {
    this.largeImage = tmp;
  }


  /**
   * Gets the enabled attribute of the Product object
   *
   * @return The enabled value
   */
  public boolean getEnabled() {
    return enabled;
  }


  /**
   * Sets the enabled attribute of the Product object
   *
   * @param tmp The new enabled value
   */
  public void setEnabled(boolean tmp) {
    this.enabled = tmp;
  }


  /**
   * Sets the enabled attribute of the Product object
   *
   * @param tmp The new enabled value
   */
  public void setEnabled(String tmp) {
    this.enabled = DatabaseUtils.parseBoolean(tmp);
  }


  /**
   * Gets the contactInformationRequired attribute of the Product object
   *
   * @return The contactInformationRequired value
   */
  public boolean getContactInformationRequired() {
    return contactInformationRequired;
  }


  /**
   * Sets the contactInformationRequired attribute of the Product object
   *
   * @param tmp The new contactInformationRequired value
   */
  public void setContactInformationRequired(boolean tmp) {
    this.contactInformationRequired = tmp;
  }


  /**
   * Sets the contactInformationRequired attribute of the Product object
   *
   * @param tmp The new contactInformationRequired value
   */
  public void setContactInformationRequired(String tmp) {
    this.contactInformationRequired = DatabaseUtils.parseBoolean(tmp);
  }


  /**
   * Gets the billingAddressRequired attribute of the Product object
   *
   * @return The billingAddressRequired value
   */
  public boolean getBillingAddressRequired() {
    return billingAddressRequired;
  }


  /**
   * Sets the billingAddressRequired attribute of the Product object
   *
   * @param tmp The new billingAddressRequired value
   */
  public void setBillingAddressRequired(boolean tmp) {
    this.billingAddressRequired = tmp;
  }


  /**
   * Sets the billingAddressRequired attribute of the Product object
   *
   * @param tmp The new billingAddressRequired value
   */
  public void setBillingAddressRequired(String tmp) {
    this.billingAddressRequired = DatabaseUtils.parseBoolean(tmp);
  }


  /**
   * Gets the shippingAddressRequired attribute of the Product object
   *
   * @return The shippingAddressRequired value
   */
  public boolean getShippingAddressRequired() {
    return shippingAddressRequired;
  }


  /**
   * Sets the shippingAddressRequired attribute of the Product object
   *
   * @param tmp The new shippingAddressRequired value
   */
  public void setShippingAddressRequired(boolean tmp) {
    this.shippingAddressRequired = tmp;
  }


  /**
   * Sets the shippingAddressRequired attribute of the Product object
   *
   * @param tmp The new shippingAddressRequired value
   */
  public void setShippingAddressRequired(String tmp) {
    this.shippingAddressRequired = DatabaseUtils.parseBoolean(tmp);
  }


  /**
   * Gets the paymentRequired attribute of the Product object
   *
   * @return The paymentRequired value
   */
  public boolean getPaymentRequired() {
    return paymentRequired;
  }


  /**
   * Sets the paymentRequired attribute of the Product object
   *
   * @param tmp The new paymentRequired value
   */
  public void setPaymentRequired(boolean tmp) {
    this.paymentRequired = tmp;
  }


  /**
   * Sets the paymentRequired attribute of the Product object
   *
   * @param tmp The new paymentRequired value
   */
  public void setPaymentRequired(String tmp) {
    this.paymentRequired = DatabaseUtils.parseBoolean(tmp);
  }


  /**
   * Gets the optionList attribute of the Product object
   *
   * @return The optionList value
   */
  public OptionList getOptionList() {
    return optionList;
  }


  /**
   * Sets the optionList attribute of the Product object
   *
   * @param tmp The new optionList value
   */
  public void setOptionList(OptionList tmp) {
    this.optionList = tmp;
  }


  public AttachmentList getAttachmentList() {
    return attachmentList;
  }

  public void setAttachmentList(AttachmentList attachmentList) {
    this.attachmentList = attachmentList;
  }

  /**
   * Gets the uniqueId attribute of the Product object
   *
   * @return The uniqueId value
   */
  public int getUniqueId() {
    return uniqueId;
  }


  /**
   * Sets the uniqueId attribute of the Product object
   *
   * @param tmp The new uniqueId value
   */
  public void setUniqueId(int tmp) {
    this.uniqueId = tmp;
  }


  /**
   * Sets the uniqueId attribute of the Product object
   *
   * @param tmp The new uniqueId value
   */
  public void setUniqueId(String tmp) {
    this.uniqueId = Integer.parseInt(tmp);
  }


  public String getOrderDescription() {
    return orderDescription;
  }

  public void setOrderDescription(String orderDescription) {
    this.orderDescription = orderDescription;
  }

  public String getSku() {
    return sku;
  }

  public void setSku(String sku) {
    this.sku = sku;
  }

  public boolean getShowInCatalog() {
    return showInCatalog;
  }

  public void setShowInCatalog(boolean showInCatalog) {
    this.showInCatalog = showInCatalog;
  }

  public void setShowInCatalog(String showInCatalog) {
    this.showInCatalog = DatabaseUtils.parseBoolean(showInCatalog);
  }

  public boolean getCartEnabled() {
    return cartEnabled;
  }

  public void setCartEnabled(boolean cartEnabled) {
    this.cartEnabled = cartEnabled;
  }

  public void setCartEnabled(String cartEnabled) {
    this.cartEnabled = DatabaseUtils.parseBoolean(cartEnabled);
  }

  public String getActionText() {
    return actionText;
  }

  public void setActionText(String actionText) {
    this.actionText = actionText;
  }

  /**
   * Description of the Method
   *
   * @param db        Description of the Parameter
   * @param productId Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public void queryRecord(Connection db, int productId) throws SQLException {
    if (productId == -1) {
      throw new SQLException("ID not specified");
    }
    PreparedStatement pst = db.prepareStatement(
        "SELECT cp.* " +
            "FROM catalog_product cp " +
            "WHERE cp.product_id = ? ");
    pst.setInt(1, productId);
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
    id = rs.getInt("product_id");
    name = rs.getString("product_name");
    priceDescription = rs.getString("price_description");
    details = rs.getString("details");
    basePrice = rs.getDouble("base_price");
    smallImage = rs.getString("small_image");
    largeImage = rs.getString("large_image");
    enabled = rs.getBoolean("enabled");
    contactInformationRequired = rs.getBoolean("contact_information_required");
    billingAddressRequired = rs.getBoolean("billing_address_required");
    shippingAddressRequired = rs.getBoolean("shipping_address_required");
    paymentRequired = rs.getBoolean("payment_required");
    orderDescription = rs.getString("order_description");
    sku = rs.getString("product_sku");
    showInCatalog = rs.getBoolean("show_in_catalog");
    cartEnabled = rs.getBoolean("cart_enabled");
    actionText = rs.getString("action_text");
  }


  /**
   * Gets the totalPrice attribute of the Product object
   *
   * @return The totalPrice value
   */
  public double getTotalPrice() {
    try {
      if (optionList == null) {
        LOG.error("optionList IS NULL, but is expected");
        return -1;
      }
      // Calculate price
      return basePrice + optionList.getTotalPrice();
    } catch (Exception e) {
      return -1;
    }
  }


  /**
   * Gets the valid attribute of the Product object
   *
   * @return The valid value
   */
  public boolean isValid() {
    return optionList.isValid();
  }

  public String getConfigurationErrors() {
    return optionList.getConfigurationErrors();
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
        "INSERT INTO customer_order_product " +
            "(order_id, product_id, product_name, price_description, total_price) VALUES (?,?,?,?,?) ");
    int i = 0;
    pst.setInt(++i, orderId);
    pst.setInt(++i, id);
    pst.setString(++i, name);
    pst.setString(++i, priceDescription);
    pst.setDouble(++i, this.getTotalPrice());
    pst.execute();
    pst.close();
    orderItemId = DatabaseUtils.getCurrVal(db, "customer_order_product_item_id_seq", -1);
    // Insert the selected options
    optionList.setOrderItemId(orderItemId);
    optionList.insert(db);
    return true;
  }


  /**
   * Gets the configuredSummary attribute of the Product object
   *
   * @return The configuredSummary value
   */
  public String getConfiguredSummary() {
    return optionList.getConfiguredSummary();
  }


  /**
   * Description of the Method
   *
   * @return Description of the Return Value
   */
  public String toString() {
    NumberFormat formatter = NumberFormat.getCurrencyInstance();
    Currency currency = Currency.getInstance("USD");
    formatter.setCurrency(currency);
    StringBuffer out = new StringBuffer();
    out.append("Product: " + this.getName() + "\r\n");
    out.append("Description: " + this.getPriceDescription() + "\r\n");
    if (sku != null) {
      out.append("Sku: " + this.getSku() + "\r\n");
    }
    out.append("Price: " + formatter.format(this.getTotalPrice()) + "\r\n");
    out.append("Configuration: " + this.getConfiguredSummary() + "\r\n");
    if (orderDescription != null) {
      out.append("Additional Information: " + this.getOrderDescription() + "\r\n");
    }
    out.append("\r\n");
    return out.toString();
  }
}

