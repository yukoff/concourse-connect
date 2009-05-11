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
 * @version $Id: ContactInformation.java,v 1.1.2.1 2004/09/23 13:44:37 matt
 *          Exp $
 * @created September 21, 2004
 */
public class ContactInformation extends GenericBean {

  private int id = -1;
  private int orderId = -1;
  private boolean attempt = false;
  private String nameFirst = null;
  private String nameLast = null;
  private String organization = null;
  private String title = null;
  private String email = null;
  private String phoneNumber = null;
  private String phoneNumberExt = null;


  /**
   * Constructor for the ContactInformation object
   */
  public ContactInformation() {
  }


  /**
   * Gets the id attribute of the ContactInformation object
   *
   * @return The id value
   */
  public int getId() {
    return id;
  }


  /**
   * Sets the id attribute of the ContactInformation object
   *
   * @param tmp The new id value
   */
  public void setId(int tmp) {
    this.id = tmp;
  }


  /**
   * Sets the id attribute of the ContactInformation object
   *
   * @param tmp The new id value
   */
  public void setId(String tmp) {
    this.id = Integer.parseInt(tmp);
  }


  /**
   * Gets the orderId attribute of the ContactInformation object
   *
   * @return The orderId value
   */
  public int getOrderId() {
    return orderId;
  }


  /**
   * Sets the orderId attribute of the ContactInformation object
   *
   * @param tmp The new orderId value
   */
  public void setOrderId(int tmp) {
    this.orderId = tmp;
  }


  /**
   * Sets the orderId attribute of the ContactInformation object
   *
   * @param tmp The new orderId value
   */
  public void setOrderId(String tmp) {
    this.orderId = Integer.parseInt(tmp);
  }


  /**
   * Gets the attempt attribute of the ContactInformation object
   *
   * @return The attempt value
   */
  public boolean getAttempt() {
    return attempt;
  }


  /**
   * Sets the attempt attribute of the ContactInformation object
   *
   * @param tmp The new attempt value
   */
  public void setAttempt(boolean tmp) {
    this.attempt = tmp;
  }


  /**
   * Sets the attempt attribute of the ContactInformation object
   *
   * @param tmp The new attempt value
   */
  public void setAttempt(String tmp) {
    this.attempt = DatabaseUtils.parseBoolean(tmp);
  }


  /**
   * Gets the nameFirst attribute of the ContactInformation object
   *
   * @return The nameFirst value
   */
  public String getNameFirst() {
    return nameFirst;
  }


  /**
   * Sets the nameFirst attribute of the ContactInformation object
   *
   * @param tmp The new nameFirst value
   */
  public void setNameFirst(String tmp) {
    this.nameFirst = tmp;
  }


  /**
   * Gets the nameLast attribute of the ContactInformation object
   *
   * @return The nameLast value
   */
  public String getNameLast() {
    return nameLast;
  }


  /**
   * Sets the nameLast attribute of the ContactInformation object
   *
   * @param tmp The new nameLast value
   */
  public void setNameLast(String tmp) {
    this.nameLast = tmp;
  }


  /**
   * Gets the organization attribute of the ContactInformation object
   *
   * @return The organization value
   */
  public String getOrganization() {
    return organization;
  }


  /**
   * Sets the organization attribute of the ContactInformation object
   *
   * @param tmp The new organization value
   */
  public void setOrganization(String tmp) {
    this.organization = tmp;
  }


  /**
   * Gets the title attribute of the ContactInformation object
   *
   * @return The title value
   */
  public String getTitle() {
    return title;
  }


  /**
   * Sets the title attribute of the ContactInformation object
   *
   * @param tmp The new title value
   */
  public void setTitle(String tmp) {
    this.title = tmp;
  }


  /**
   * Gets the email attribute of the ContactInformation object
   *
   * @return The email value
   */
  public String getEmail() {
    return email;
  }


  /**
   * Sets the email attribute of the ContactInformation object
   *
   * @param tmp The new email value
   */
  public void setEmail(String tmp) {
    this.email = tmp;
  }


  /**
   * Gets the phoneNumber attribute of the ContactInformation object
   *
   * @return The phoneNumber value
   */
  public String getPhoneNumber() {
    return phoneNumber;
  }


  /**
   * Sets the phoneNumber attribute of the ContactInformation object
   *
   * @param tmp The new phoneNumber value
   */
  public void setPhoneNumber(String tmp) {
    this.phoneNumber = tmp;
  }


  /**
   * Gets the phoneNumberExt attribute of the ContactInformation object
   *
   * @return The phoneNumberExt value
   */
  public String getPhoneNumberExt() {
    return phoneNumberExt;
  }


  /**
   * Sets the phoneNumberExt attribute of the ContactInformation object
   *
   * @param tmp The new phoneNumberExt value
   */
  public void setPhoneNumberExt(String tmp) {
    this.phoneNumberExt = tmp;
  }


  /**
   * Gets the valid attribute of the ContactInformation object
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
    if (organization == null || "".equals(organization.trim())) {
      errors.put("organizationError", "Organization is required");
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
            "(order_id, address_type, namefirst, namelast, " +
            "organization, title, phone_number, phone_extension) VALUES " +
            "(?,?,?,?,?,?,?,?) ");
    int i = 0;
    pst.setInt(++i, orderId);
    pst.setString(++i, "Contact Information");
    pst.setString(++i, nameFirst);
    pst.setString(++i, nameLast);
    pst.setString(++i, organization);
    pst.setString(++i, title);
    pst.setString(++i, phoneNumber);
    pst.setString(++i, phoneNumberExt);
    pst.execute();
    pst.close();
    //customer_order_address_address_id_seq
    return true;
  }


  /**
   * Description of the Method
   *
   * @return Description of the Return Value
   */
  public String toString() {
    StringBuffer out = new StringBuffer();
    out.append("==[ Contact ]========================================\r\n");
    out.append("First Name: " + StringUtils.toString(this.getNameFirst()) + "\r\n");
    out.append("Last Name: " + StringUtils.toString(this.getNameLast()) + "\r\n");
    out.append("Organization: " + StringUtils.toString(this.getOrganization()) + "\r\n");
    out.append("Title: " + StringUtils.toString(this.getTitle()) + "\r\n");
    out.append("Email: " + StringUtils.toString(this.getEmail()) + "\r\n");
    out.append("Phone: " + StringUtils.toString(this.getPhoneNumber()) + "\r\n");
    out.append("Phone Ext: " + StringUtils.toString(this.getPhoneNumberExt()) + "\r\n");
    out.append("\r\n");
    return out.toString();
  }
}

