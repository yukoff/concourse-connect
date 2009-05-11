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

package com.concursive.connect.web.modules.productcatalog.beans;

import com.concursive.commons.db.DatabaseUtils;
import com.concursive.commons.web.mvc.beans.GenericBean;
import com.concursive.connect.web.modules.productcatalog.dao.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;

/**
 * Description of the Class
 *
 * @author matt rajkowski
 * @version $Id$
 * @created September 21, 2004
 */
public class OrderBean extends GenericBean {

  private int id = -1;
  private String ipAddress = null;
  private String browser = null;
  private int userId = -1;
  // related data
  private ProductList productList = new ProductList();
  private ContactInformation contactInformation = new ContactInformation();
  private BillingAddress billing = new BillingAddress();
  private ShippingAddress shipping = new ShippingAddress();
  private Payment payment = new Payment();
  private int lineItem = 0;
  private boolean saved = false;


  /**
   * Constructor for the OrderBean object
   */
  public OrderBean() {
  }


  /**
   * Gets the id attribute of the OrderBean object
   *
   * @return The id value
   */
  public int getId() {
    return id;
  }


  /**
   * Sets the id attribute of the OrderBean object
   *
   * @param tmp The new id value
   */
  public void setId(int tmp) {
    this.id = tmp;
  }


  /**
   * Sets the id attribute of the OrderBean object
   *
   * @param tmp The new id value
   */
  public void setId(String tmp) {
    this.id = Integer.parseInt(tmp);
  }


  /**
   * Gets the ipAddress attribute of the OrderBean object
   *
   * @return The ipAddress value
   */
  public String getIpAddress() {
    return ipAddress;
  }


  /**
   * Sets the ipAddress attribute of the OrderBean object
   *
   * @param tmp The new ipAddress value
   */
  public void setIpAddress(String tmp) {
    this.ipAddress = tmp;
  }


  /**
   * Gets the browser attribute of the OrderBean object
   *
   * @return The browser value
   */
  public String getBrowser() {
    return browser;
  }


  /**
   * Sets the browser attribute of the OrderBean object
   *
   * @param tmp The new browser value
   */
  public void setBrowser(String tmp) {
    this.browser = tmp;
  }


  public int getUserId() {
    return userId;
  }

  public void setUserId(int userId) {
    this.userId = userId;
  }

  /**
   * Gets the productList attribute of the OrderBean object
   *
   * @return The productList value
   */
  public ProductList getProductList() {
    return productList;
  }


  /**
   * Sets the productList attribute of the OrderBean object
   *
   * @param tmp The new productList value
   */
  public void setProductList(ProductList tmp) {
    this.productList = tmp;
  }


  /**
   * Gets the contactInformation attribute of the OrderBean object
   *
   * @return The contactInformation value
   */
  public ContactInformation getContactInformation() {
    return contactInformation;
  }


  /**
   * Sets the contactInformation attribute of the OrderBean object
   *
   * @param tmp The new contactInformation value
   */
  public void setContactInformation(ContactInformation tmp) {
    this.contactInformation = tmp;
  }


  /**
   * Gets the billing attribute of the OrderBean object
   *
   * @return The billing value
   */
  public BillingAddress getBilling() {
    return billing;
  }


  /**
   * Sets the billing attribute of the OrderBean object
   *
   * @param tmp The new billing value
   */
  public void setBilling(BillingAddress tmp) {
    this.billing = tmp;
  }


  /**
   * Gets the shipping attribute of the OrderBean object
   *
   * @return The shipping value
   */
  public ShippingAddress getShipping() {
    return shipping;
  }


  /**
   * Sets the shipping attribute of the OrderBean object
   *
   * @param tmp The new shipping value
   */
  public void setShipping(ShippingAddress tmp) {
    this.shipping = tmp;
  }


  /**
   * Gets the payment attribute of the OrderBean object
   *
   * @return The payment value
   */
  public Payment getPayment() {
    return payment;
  }


  /**
   * Sets the payment attribute of the OrderBean object
   *
   * @param tmp The new payment value
   */
  public void setPayment(Payment tmp) {
    this.payment = tmp;
  }


  /**
   * Gets the saved attribute of the OrderBean object
   *
   * @return The saved value
   */
  public boolean isSaved() {
    return saved;
  }

  public void setSaved(boolean tmp) {
    saved = tmp;
  }


  /**
   * Description of the Method
   *
   * @param product Description of the Parameter
   */
  public synchronized void add(Product product) {
    product.setUniqueId(++lineItem);
    this.getProductList().add(product);
  }


  /**
   * Description of the Method
   *
   * @param uniqueId Description of the Parameter
   */
  public synchronized void remove(int uniqueId) {
    Iterator i = this.getProductList().iterator();
    while (i.hasNext()) {
      Product product = (Product) i.next();
      if (product.getUniqueId() == uniqueId) {
        i.remove();
        break;
      }
    }
  }


  /**
   * Description of the Method
   *
   * @return Description of the Return Value
   */
  public boolean requiresContactInformation() {
    // If any product requires billing, then show the form
    Iterator p = productList.iterator();
    while (p.hasNext()) {
      Product thisProduct = (Product) p.next();
      if (thisProduct.getContactInformationRequired() && !contactInformation.isValid()) {
        return true;
      }
    }
    return false;
  }


  /**
   * Description of the Method
   *
   * @return Description of the Return Value
   */
  public boolean requiresBillingAddress() {
    if (billing.isValid()) {
      return false;
    }
    // If any product requires billing, then show the form
    Iterator p = productList.iterator();
    while (p.hasNext()) {
      Product thisProduct = (Product) p.next();
      if (thisProduct.getBillingAddressRequired() && !billing.isValid()) {
        return true;
      }
    }
    return false;
  }

  public boolean showBillingAddress() {
    // If any product requires billing, then show the form
    Iterator p = productList.iterator();
    while (p.hasNext()) {
      Product thisProduct = (Product) p.next();
      if (thisProduct.getBillingAddressRequired()) {
        return true;
      }
    }
    return false;
  }


  /**
   * Description of the Method
   *
   * @return Description of the Return Value
   */
  public boolean requiresShippingAddress() {
    if (shipping.isValid()) {
      return false;
    }
    Iterator p = productList.iterator();
    while (p.hasNext()) {
      Product thisProduct = (Product) p.next();
      if (thisProduct.getShippingAddressRequired() &&
          (!billing.getUseForShipping() && !shipping.isValid())) {
        return true;
      }
    }
    return false;
  }

  public boolean showShippingAddress() {
    Iterator p = productList.iterator();
    while (p.hasNext()) {
      Product thisProduct = (Product) p.next();
      if (thisProduct.getShippingAddressRequired() &&
          !billing.getUseForShipping()) {
        return true;
      }
    }
    return false;
  }


  /**
   * Description of the Method
   *
   * @return Description of the Return Value
   */
  public boolean requiresPayment() {
    if (payment.isValid()) {
      return false;
    }
    Iterator p = productList.iterator();
    while (p.hasNext()) {
      Product thisProduct = (Product) p.next();
      if (thisProduct.getPaymentRequired() && !payment.isValid()) {
        return true;
      }
    }
    return false;
  }

  public boolean showPayment() {
    Iterator p = productList.iterator();
    while (p.hasNext()) {
      Product thisProduct = (Product) p.next();
      if (thisProduct.getPaymentRequired()) {
        return true;
      }
    }
    return false;
  }

  public double getChargeAmount() {
    double chargeAmount = 0.0;
    Iterator p = productList.iterator();
    while (p.hasNext()) {
      Product thisProduct = (Product) p.next();
      if (thisProduct.getPaymentRequired()) {
        chargeAmount += thisProduct.getTotalPrice();
      }
    }
    return chargeAmount;
  }

  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  public boolean insert(Connection db) throws SQLException {
    try {
      db.setAutoCommit(false);
      // Insert the base order
      PreparedStatement pst = db.prepareStatement(
          "INSERT INTO customer_order " +
              "(ipaddress, browser, total_price, order_by) VALUES (?,?,?,?) ");
      int i = 0;
      pst.setString(++i, ipAddress);
      pst.setString(++i, browser);
      pst.setDouble(++i, productList.getTotalPrice());
      DatabaseUtils.setInt(pst, ++i, userId);
      pst.execute();
      pst.close();
      id = DatabaseUtils.getCurrVal(db, "customer_order_order_id_seq", -1);
      // Insert the products
      productList.setOrderId(id);
      productList.insert(db);
      // Insert the contact info
      if (contactInformation.isValid()) {
        contactInformation.setOrderId(id);
        contactInformation.insert(db);
      }
      // Insert the addresses
      if (billing.isValid()) {
        billing.setOrderId(id);
        billing.insert(db);
      }
      if (shipping.isValid()) {
        shipping.setOrderId(id);
        shipping.insert(db);
      }
      // Insert the payment info
      if (payment.isValid()) {
        payment.setOrderId(id);
        payment.setChargeAmount(getChargeAmount());
        payment.insert(db);
      }
      db.commit();
      // Finalize
      saved = true;
      return true;
    } catch (Exception e) {
      db.rollback();
      e.printStackTrace(System.out);
      throw new SQLException("Could not save");
    } finally {
      db.setAutoCommit(true);
    }
  }


  /**
   * Description of the Method
   *
   * @return Description of the Return Value
   */
  public String toString() {
    StringBuffer out = new StringBuffer();
    out.append("Order # " + this.getId() + "\r\n");
    out.append("IP Address: " + this.getIpAddress() + "\r\n");
    out.append("Browser: " + this.getBrowser() + "\r\n");
    out.append("\r\n");
    out.append(productList.toString());
    if (contactInformation.isValid()) {
      out.append(contactInformation.toString());
    }
    if (billing.isValid()) {
      out.append(billing.toString());
    }
    if (shipping.isValid()) {
      out.append(shipping.toString());
    }
    if (payment.isValid()) {
      out.append(payment.toString());
    }
    return out.toString();
  }
}

