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

import com.concursive.commons.codec.PrivateString;
import com.concursive.commons.db.DatabaseUtils;
import com.concursive.commons.web.mvc.beans.GenericBean;

import java.security.Key;
import java.sql.Connection;
import java.sql.PreparedStatement;
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
public class Payment extends GenericBean {

  public final static int TYPE_UNDEFINED = -1;
  public final static int TYPE_CREDITCARD = 1;

  private int type = TYPE_UNDEFINED;
  private int id = -1;
  private int orderId = -1;
  private boolean attempt = false;
  private CreditCard creditCard = new CreditCard();
  private Key key = null;
  private double chargeAmount = 0;
  private boolean reset = false;

  /**
   * Constructor for the CreditCard object
   */
  public Payment() {
  }


  /**
   * Gets the type attribute of the Payment object
   *
   * @return The type value
   */
  public int getType() {
    return type;
  }


  /**
   * Sets the type attribute of the Payment object
   *
   * @param tmp The new type value
   */
  public void setType(int tmp) {
    this.type = tmp;
  }


  /**
   * Sets the type attribute of the Payment object
   *
   * @param tmp The new type value
   */
  public void setType(String tmp) {
    this.type = Integer.parseInt(tmp);
  }


  /**
   * Gets the id attribute of the Payment object
   *
   * @return The id value
   */
  public int getId() {
    return id;
  }


  /**
   * Sets the id attribute of the Payment object
   *
   * @param tmp The new id value
   */
  public void setId(int tmp) {
    this.id = tmp;
  }


  /**
   * Sets the id attribute of the Payment object
   *
   * @param tmp The new id value
   */
  public void setId(String tmp) {
    this.id = Integer.parseInt(tmp);
  }


  /**
   * Gets the orderId attribute of the Payment object
   *
   * @return The orderId value
   */
  public int getOrderId() {
    return orderId;
  }


  /**
   * Sets the orderId attribute of the Payment object
   *
   * @param tmp The new orderId value
   */
  public void setOrderId(int tmp) {
    this.orderId = tmp;
  }


  /**
   * Sets the orderId attribute of the Payment object
   *
   * @param tmp The new orderId value
   */
  public void setOrderId(String tmp) {
    this.orderId = Integer.parseInt(tmp);
  }


  /**
   * Gets the attempt attribute of the Payment object
   *
   * @return The attempt value
   */
  public boolean getAttempt() {
    return attempt;
  }


  /**
   * Sets the attempt attribute of the Payment object
   *
   * @param tmp The new attempt value
   */
  public void setAttempt(boolean tmp) {
    this.attempt = tmp;
  }


  /**
   * Sets the attempt attribute of the Payment object
   *
   * @param tmp The new attempt value
   */
  public void setAttempt(String tmp) {
    this.attempt = DatabaseUtils.parseBoolean(tmp);
  }


  /**
   * Gets the creditCard attribute of the Payment object
   *
   * @return The creditCard value
   */
  public CreditCard getCreditCard() {
    return creditCard;
  }


  /**
   * Sets the creditCard attribute of the Payment object
   *
   * @param tmp The new creditCard value
   */
  public void setCreditCard(CreditCard tmp) {
    this.creditCard = tmp;
  }


  /**
   * Gets the key attribute of the Payment object
   *
   * @return The key value
   */
  public Key getKey() {
    return key;
  }


  /**
   * Sets the key attribute of the Payment object
   *
   * @param tmp The new key value
   */
  public void setKey(Key tmp) {
    this.key = tmp;
  }


  public double getChargeAmount() {
    return chargeAmount;
  }

  public void setChargeAmount(double chargeAmount) {
    this.chargeAmount = chargeAmount;
  }

  public boolean isReset() {
    return reset;
  }

  public void setReset(boolean reset) {
    this.reset = reset;
  }

  /**
   * Gets the valid attribute of the CreditCard object
   *
   * @return The valid value
   */
  public boolean isValid() {
    errors.clear();
    if (type == TYPE_UNDEFINED) {
      errors.put("typeError", "Payment Type is required");
    }
    if (type == TYPE_CREDITCARD) {
      if (creditCard == null) {
        errors.put("paymentCreditCardError", "Credit Card information is required");
      } else {
        if (!creditCard.isValid()) {
          errors = creditCard.getErrors();
        }
      }
    }
    if (reset) {
      return false;
    }
    return (!hasErrors());
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
        "INSERT INTO customer_order_payment " +
            "(order_id, payment_type, credit_card_type, credit_card_number, " +
            "credit_card_exp_month, credit_card_exp_year, charge_amount) VALUES " +
            "(?,?,?,?,?,?,?) ");
    int i = 0;
    pst.setInt(++i, orderId);
    pst.setString(++i, "Credit Card");
    pst.setString(++i, creditCard.getType());
    pst.setString(++i, PrivateString.encryptAsymmetric(key, creditCard.getNumber()));
    pst.setInt(++i, creditCard.getExpirationMonth());
    pst.setInt(++i, creditCard.getExpirationYear());
    pst.setDouble(++i, chargeAmount);
    pst.execute();
    pst.close();
    id = DatabaseUtils.getCurrVal(db, "customer_order_payment_payment_id_seq", -1);
    return true;
  }

  public boolean updateProcessed(Connection db) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "UPDATE customer_order_payment " +
            "SET processed = CURRENT_TIMESTAMP " +
            "WHERE payment_id = ? ");
    int i = 0;
    pst.setInt(++i, id);
    pst.execute();
    pst.close();
    return true;
  }


  /**
   * Description of the Method
   *
   * @return Description of the Return Value
   */
  public String toString() {
    StringBuffer out = new StringBuffer();
    out.append("==[ Payment ]========================================\r\n");
    if (type == TYPE_CREDITCARD) {
      out.append("Type: " + "Credit Card" + "\r\n");
      out.append(creditCard.toString());
      if (chargeAmount > 0.0) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        Currency currency = Currency.getInstance("USD");
        formatter.setCurrency(currency);
        out.append("Amount Charged: " + formatter.format(chargeAmount) + "\r\n");
      }
    }
    out.append("\r\n");
    return out.toString();
  }
}

