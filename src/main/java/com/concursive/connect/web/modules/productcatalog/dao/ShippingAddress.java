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
import com.concursive.commons.text.StringUtils;
import com.concursive.commons.web.mvc.beans.GenericBean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Description of the Class
 *
 * @author matt rajkowski
 * @version $Id$
 * @created September 21, 2004
 */
public class ShippingAddress extends GenericBean {

  private int id = -1;
  private int orderId = -1;
  private boolean attempt = false;
  private String nameFirst = null;
  private String nameLast = null;
  private String organization = null;
  private String addressLine1 = null;
  private String addressLine2 = null;
  private String addressLine3 = null;
  private String city = null;
  private String state = null;
  private String postalCode = null;
  private String country = "UNITED STATES";
  private String email = null;


  /**
   * Constructor for the BillingAddress object
   */
  public ShippingAddress() {
  }


  /**
   * Gets the id attribute of the ShippingAddress object
   *
   * @return The id value
   */
  public int getId() {
    return id;
  }


  /**
   * Sets the id attribute of the ShippingAddress object
   *
   * @param tmp The new id value
   */
  public void setId(int tmp) {
    this.id = tmp;
  }


  /**
   * Sets the id attribute of the ShippingAddress object
   *
   * @param tmp The new id value
   */
  public void setId(String tmp) {
    this.id = Integer.parseInt(tmp);
  }


  /**
   * Gets the orderId attribute of the ShippingAddress object
   *
   * @return The orderId value
   */
  public int getOrderId() {
    return orderId;
  }


  /**
   * Sets the orderId attribute of the ShippingAddress object
   *
   * @param tmp The new orderId value
   */
  public void setOrderId(int tmp) {
    this.orderId = tmp;
  }


  /**
   * Sets the orderId attribute of the ShippingAddress object
   *
   * @param tmp The new orderId value
   */
  public void setOrderId(String tmp) {
    this.orderId = Integer.parseInt(tmp);
  }


  /**
   * Gets the attempt attribute of the ShippingAddress object
   *
   * @return The attempt value
   */
  public boolean getAttempt() {
    return attempt;
  }


  /**
   * Sets the attempt attribute of the ShippingAddress object
   *
   * @param tmp The new attempt value
   */
  public void setAttempt(boolean tmp) {
    this.attempt = tmp;
  }


  /**
   * Sets the attempt attribute of the ShippingAddress object
   *
   * @param tmp The new attempt value
   */
  public void setAttempt(String tmp) {
    this.attempt = DatabaseUtils.parseBoolean(tmp);
  }


  /**
   * Gets the nameFirst attribute of the ShippingAddress object
   *
   * @return The nameFirst value
   */
  public String getNameFirst() {
    return nameFirst;
  }


  /**
   * Sets the nameFirst attribute of the ShippingAddress object
   *
   * @param tmp The new nameFirst value
   */
  public void setNameFirst(String tmp) {
    this.nameFirst = tmp;
  }


  /**
   * Gets the nameLast attribute of the ShippingAddress object
   *
   * @return The nameLast value
   */
  public String getNameLast() {
    return nameLast;
  }


  /**
   * Sets the nameLast attribute of the ShippingAddress object
   *
   * @param tmp The new nameLast value
   */
  public void setNameLast(String tmp) {
    this.nameLast = tmp;
  }


  /**
   * Gets the organization attribute of the ShippingAddress object
   *
   * @return The organization value
   */
  public String getOrganization() {
    return organization;
  }


  /**
   * Sets the organization attribute of the ShippingAddress object
   *
   * @param tmp The new organization value
   */
  public void setOrganization(String tmp) {
    this.organization = tmp;
  }


  /**
   * Gets the addressLine1 attribute of the ShippingAddress object
   *
   * @return The addressLine1 value
   */
  public String getAddressLine1() {
    return addressLine1;
  }


  /**
   * Sets the addressLine1 attribute of the ShippingAddress object
   *
   * @param tmp The new addressLine1 value
   */
  public void setAddressLine1(String tmp) {
    this.addressLine1 = tmp;
  }


  /**
   * Gets the addressLine2 attribute of the ShippingAddress object
   *
   * @return The addressLine2 value
   */
  public String getAddressLine2() {
    return addressLine2;
  }


  /**
   * Sets the addressLine2 attribute of the ShippingAddress object
   *
   * @param tmp The new addressLine2 value
   */
  public void setAddressLine2(String tmp) {
    this.addressLine2 = tmp;
  }


  /**
   * Gets the addressLine3 attribute of the ShippingAddress object
   *
   * @return The addressLine3 value
   */
  public String getAddressLine3() {
    return addressLine3;
  }


  /**
   * Sets the addressLine3 attribute of the ShippingAddress object
   *
   * @param tmp The new addressLine3 value
   */
  public void setAddressLine3(String tmp) {
    this.addressLine3 = tmp;
  }


  /**
   * Gets the city attribute of the ShippingAddress object
   *
   * @return The city value
   */
  public String getCity() {
    return city;
  }


  /**
   * Sets the city attribute of the ShippingAddress object
   *
   * @param tmp The new city value
   */
  public void setCity(String tmp) {
    this.city = tmp;
  }


  /**
   * Gets the state attribute of the ShippingAddress object
   *
   * @return The state value
   */
  public String getState() {
    return state;
  }


  /**
   * Sets the state attribute of the ShippingAddress object
   *
   * @param tmp The new state value
   */
  public void setState(String tmp) {
    this.state = tmp;
  }


  /**
   * Gets the postalCode attribute of the ShippingAddress object
   *
   * @return The postalCode value
   */
  public String getPostalCode() {
    return postalCode;
  }


  /**
   * Sets the postalCode attribute of the ShippingAddress object
   *
   * @param tmp The new postalCode value
   */
  public void setPostalCode(String tmp) {
    this.postalCode = tmp;
  }


  /**
   * Gets the country attribute of the ShippingAddress object
   *
   * @return The country value
   */
  public String getCountry() {
    return country;
  }


  /**
   * Sets the country attribute of the ShippingAddress object
   *
   * @param tmp The new country value
   */
  public void setCountry(String tmp) {
    this.country = tmp;
  }


  /**
   * Gets the email attribute of the ShippingAddress object
   *
   * @return The email value
   */
  public String getEmail() {
    return email;
  }


  /**
   * Sets the email attribute of the ShippingAddress object
   *
   * @param tmp The new email value
   */
  public void setEmail(String tmp) {
    this.email = tmp;
  }


  /**
   * Gets the valid attribute of the ShippingAddress object
   *
   * @return The valid value
   */
  public boolean isValid() {
    errors.clear();
    if (nameFirst == null || "".equals(nameFirst.trim())) {
      errors.put("nameFirstError", "First name is required");
    }
    if (nameLast == null || "".equals(nameLast.trim())) {
      errors.put("nameLastError", "Last name is required");
    }
    if (addressLine1 == null || "".equals(addressLine1.trim())) {
      errors.put("addressLine1Error", "Address Line 1 is required");
    }
    if (city == null || "".equals(city.trim())) {
      errors.put("cityError", "City is required");
    }
    if (state == null || "".equals(state.trim())) {
      errors.put("stateError", "State is required");
    }
    if (postalCode == null || "".equals(postalCode.trim())) {
      errors.put("postalCodeError", "Postal Code is required");
    }
    if (country == null || "".equals(country.trim())) {
      errors.put("countryError", "Country is required");
    }
    if (email == null || "".equals(email.trim())) {
      errors.put("emailError", "Email is required");
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
        "INSERT INTO customer_order_address " +
            "(order_id, address_type, namefirst, namelast, organization, " +
            "addressline1, addressline2, addressline3, city, state, " +
            "postal_code, country, email, primary_address) VALUES " +
            "(?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
    int i = 0;
    pst.setInt(++i, orderId);
    pst.setString(++i, "Shipping");
    pst.setString(++i, nameFirst);
    pst.setString(++i, nameLast);
    pst.setString(++i, organization);
    pst.setString(++i, addressLine1);
    pst.setString(++i, addressLine2);
    pst.setString(++i, addressLine3);
    pst.setString(++i, city);
    pst.setString(++i, state);
    pst.setString(++i, postalCode);
    pst.setString(++i, country);
    pst.setString(++i, email);
    pst.setBoolean(++i, false);
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
    out.append("==[ Billing ]========================================\r\n");
    out.append("First Name: " + StringUtils.toString(this.getNameFirst()) + "\r\n");
    out.append("Last Name: " + StringUtils.toString(this.getNameLast()) + "\r\n");
    out.append("Organization: " + StringUtils.toString(this.getOrganization()) + "\r\n");
    out.append("Address Line 1: " + StringUtils.toString(this.getAddressLine1()) + "\r\n");
    out.append("Address Line 2: " + StringUtils.toString(this.getAddressLine2()) + "\r\n");
    out.append("Address Line 3: " + StringUtils.toString(this.getAddressLine3()) + "\r\n");
    out.append("City: " + StringUtils.toString(this.getCity()) + "\r\n");
    out.append("State: " + StringUtils.toString(this.getState()) + "\r\n");
    out.append("Postal Code: " + StringUtils.toString(this.getPostalCode()) + "\r\n");
    out.append("Country: " + StringUtils.toString(this.getCountry()) + "\r\n");
    out.append("Email: " + StringUtils.toString(this.getEmail()) + "\r\n");
    out.append("\r\n");
    return out.toString();
  }
}

