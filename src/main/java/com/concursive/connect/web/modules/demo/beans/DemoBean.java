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

package com.concursive.connect.web.modules.demo.beans;

import com.concursive.commons.db.DatabaseUtils;
import com.concursive.commons.text.StringUtils;
import com.concursive.commons.web.mvc.beans.GenericBean;
import com.concursive.commons.xml.XMLUtils;
import nl.captcha.Captcha;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Bean to encapsulate the demo request HTML form
 *
 * @author ananth
 * @version $Id$
 * @created December 12, 2003
 */
public class DemoBean extends GenericBean {
  private int id = -1;
  private String nameFirst = null;
  private String nameLast = null;
  private String companyName = null;
  private String title = null;
  private String phone = null;
  private String phoneExt = null;
  private String email = null;
  private String website = null;
  private String addressLine1 = null;
  private String addressLine2 = null;
  private String addressLine3 = null;
  private String city = null;
  private String state = null;
  private String country = null;
  private String postalCode = null;
  private String ipAddress = null;
  private String userBrowser = null;
  private String language = null;
  private String referer = null;
  private String captcha = null;
  private String requestedURL = null;
  private int numberOfSeats = -1;
  private boolean agreement = false;

  /**
   * Sets the firstName attribute of the UserBean object
   *
   * @param tmp The new firstName value
   */
  public void setNameFirst(String tmp) {
    this.nameFirst = tmp;
  }


  /**
   * Sets the lastName attribute of the UserBean object
   *
   * @param tmp The new lastName value
   */
  public void setNameLast(String tmp) {
    this.nameLast = tmp;
  }


  /**
   * Sets the companyName attribute of the UserBean object
   *
   * @param tmp The new companyName value
   */
  public void setCompanyName(String tmp) {
    this.companyName = tmp;
  }


  /**
   * Sets the title attribute of the UserBean object
   *
   * @param tmp The new title value
   */
  public void setTitle(String tmp) {
    this.title = tmp;
  }


  /**
   * Sets the phone attribute of the UserBean object
   *
   * @param tmp The new phone value
   */
  public void setPhone(String tmp) {
    this.phone = tmp;
  }


  /**
   * Sets the phoneExt attribute of the UserBean object
   *
   * @param tmp The new phoneExt value
   */
  public void setPhoneExt(String tmp) {
    this.phoneExt = tmp;
  }


  /**
   * Sets the email attribute of the UserBean object
   *
   * @param tmp The new email value
   */
  public void setEmail(String tmp) {
    this.email = tmp;
  }


  /**
   * Sets the ipAddress attribute of the UserBean object
   *
   * @param tmp The new ipAddress value
   */
  public void setIpAddress(String tmp) {
    this.ipAddress = tmp;
  }


  /**
   * Sets the userBrowser attribute of the UserBean object
   *
   * @param tmp The new userBrowser value
   */
  public void setUserBrowser(String tmp) {
    this.userBrowser = tmp;
  }


  /**
   * Gets the firstName attribute of the UserBean object
   *
   * @return The firstName value
   */
  public String getNameFirst() {
    return nameFirst;
  }


  /**
   * Gets the lastName attribute of the UserBean object
   *
   * @return The lastName value
   */
  public String getNameLast() {
    return nameLast;
  }


  /**
   * Gets the companyName attribute of the UserBean object
   *
   * @return The companyName value
   */
  public String getCompanyName() {
    return companyName;
  }


  /**
   * Gets the email attribute of the UserBean object
   *
   * @return The email value
   */
  public String getEmail() {
    return email;
  }


  /**
   * Gets the ipAddress attribute of the UserBean object
   *
   * @return The ipAddress value
   */
  public String getIpAddress() {
    return ipAddress;
  }


  /**
   * Gets the title attribute of the UserBean object
   *
   * @return The title value
   */
  public String getTitle() {
    return title;
  }


  /**
   * Gets the phone attribute of the UserBean object
   *
   * @return The phone value
   */
  public String getPhone() {
    return phone;
  }


  /**
   * Gets the phoneExt attribute of the UserBean object
   *
   * @return The phoneExt value
   */
  public String getPhoneExt() {
    return phoneExt;
  }


  /**
   * Gets the userBrowser attribute of the UserBean object
   *
   * @return The userBrowser value
   */
  public String getUserBrowser() {
    return userBrowser;
  }

  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }

  public String getReferer() {
    return referer;
  }

  public void setReferer(String referer) {
    this.referer = referer;
  }

  public String getCaptcha() {
    return captcha;
  }

  public void setCaptcha(String captcha) {
    this.captcha = captcha;
  }

  public String getWebsite() {
    return website;
  }

  public void setWebsite(String website) {
    this.website = website;
  }

  public String getAddressLine1() {
    return addressLine1;
  }

  public void setAddressLine1(String addressLine1) {
    this.addressLine1 = addressLine1;
  }

  public String getAddressLine2() {
    return addressLine2;
  }

  public void setAddressLine2(String addressLine2) {
    this.addressLine2 = addressLine2;
  }

  public String getAddressLine3() {
    return addressLine3;
  }

  public void setAddressLine3(String addressLine3) {
    this.addressLine3 = addressLine3;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public String getPostalCode() {
    return postalCode;
  }

  public void setPostalCode(String postalCode) {
    this.postalCode = postalCode;
  }

  public String getRequestedURL() {
    return requestedURL;
  }

  public void setRequestedURL(String requestedURL) {
    this.requestedURL = requestedURL;
  }

  public int getNumberOfSeats() {
    return numberOfSeats;
  }

  public void setNumberOfSeats(int numberOfSeats) {
    this.numberOfSeats = numberOfSeats;
  }

  public void setNumberOfSeats(String tmp) {
    this.numberOfSeats = Integer.parseInt(tmp);
  }

  public boolean isAgreement() {
    return agreement;
  }

  public void setAgreement(boolean agreement) {
    this.agreement = agreement;
  }

  public void setAgreement(String tmp) {
    this.agreement = DatabaseUtils.parseBoolean(tmp);
  }


  /**
   * Inserts this object into the database
   *
   * @param db Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public void insert(Connection db) throws SQLException {
    String sql =
        "INSERT INTO user_request " +
            "(request, namefirst, namelast, company_name, title, " +
            "phone_number, phone_extension, email, ipaddress, browser," +
            "addrline1, addrline2, addrline3, " +
            "city, state, country, postalcode," +
            "website, number_of_seats, requested_url, language ) " +
            "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ";
    int i = 0;
    PreparedStatement pst = db.prepareStatement(sql);
    pst.setString(++i, "Account Activation");
    pst.setString(++i, nameFirst);
    pst.setString(++i, nameLast);
    pst.setString(++i, companyName);
    pst.setString(++i, title);
    pst.setString(++i, phone);
    pst.setString(++i, phoneExt);
    pst.setString(++i, email);
    pst.setString(++i, ipAddress);
    pst.setString(++i, userBrowser);
    pst.setString(++i, addressLine1);
    pst.setString(++i, addressLine2);
    pst.setString(++i, addressLine3);
    pst.setString(++i, city);
    pst.setString(++i, state);
    pst.setString(++i, country);
    pst.setString(++i, postalCode);
    pst.setString(++i, website);
    pst.setInt(++i, numberOfSeats);
    pst.setString(++i, requestedURL);
    pst.setString(++i, language);
    pst.execute();
    pst.close();
    id = DatabaseUtils.getCurrVal(db, "user_request_request_id_seq", -1);
  }


  /**
   * Gets the valid attribute of the UserBean object
   *
   * @param session the browser sessions
   * @return The valid value
   */
  public boolean isValid(HttpSession session) {
    Captcha captchaValue = (Captcha) session.getAttribute(Captcha.NAME);
    session.removeAttribute(Captcha.NAME);
    if (captchaValue == null || captcha == null ||
        !captchaValue.isCorrect(captcha)) {
      errors.put("captchaError", "Text did not match image");
    }
    if (!StringUtils.hasText(nameFirst)) {
      errors.put("nameFirstError", "Required field");
    }
    if (!StringUtils.hasText(nameLast)) {
      errors.put("nameLastError", "Required field");
    }
    if (!StringUtils.hasText(companyName)) {
      errors.put("companyNameError", "Required field");
    }
    if (!StringUtils.hasText(email)) {
      errors.put("emailError", "Required field");
    }
    if (!StringUtils.hasText(phone)) {
      errors.put("phoneError", "Required field");
    }
    if (!StringUtils.hasText(addressLine1)) {
      errors.put("addressLine1Error", "Required field");
    }
    if (!StringUtils.hasText(getCity())) {
      errors.put("cityError", "Required field");
    }
    if (getState() == null || getState().equals("-1")) {
      errors.put("stateError", "Required field");
    }
    if (getCountry() == null || getCountry().equals("-1")) {
      errors.put("countryError", "Required field");
    }
    if (!agreement) {
      errors.put("agreementError", "Accepting the terms and conditions is required");
    }
    return (!hasErrors());
  }


  /**
   * Updates the record to indicate that it has been processed
   *
   * @param db Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public void markProcessed(Connection db) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "UPDATE user_request " +
            "SET processed = CURRENT_TIMESTAMP " +
            "WHERE request_id = ?");
    pst.setInt(1, id);
    pst.executeUpdate();
    pst.close();
  }


  /**
   * Description of the Method
   *
   * @return Description of
   *         the Return Value
   * @throws javax.xml.parsers.ParserConfigurationException
   *          Description of
   *          the Exception
   */
  public String toXmlPacket(String appId, String appCode, String appClientId) throws javax.xml.parsers.ParserConfigurationException {
    //Build an XML document needed for request
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = dbf.newDocumentBuilder();
    Document document = builder.newDocument();
    //Document element
    Element documentElement = document.createElement("app");
    document.appendChild(documentElement);
    //Authentication node
    Element authElement = document.createElement("authentication");
    documentElement.appendChild(authElement);

    Element idElement = document.createElement("id");
    idElement.appendChild(document.createTextNode(appId));
    authElement.appendChild(idElement);

    Element codeElement = document.createElement("code");
    codeElement.appendChild(document.createTextNode(appCode));
    authElement.appendChild(codeElement);

    Element systemIdElement = document.createElement("systemId");
    systemIdElement.appendChild(document.createTextNode("4"));
    authElement.appendChild(systemIdElement);

    Element clientIdElement = document.createElement("clientId");
    clientIdElement.appendChild(document.createTextNode(appClientId));
    authElement.appendChild(clientIdElement);

    //Demo account node
    Element rootElement = document.createElement("demoAccount");
    documentElement.appendChild(rootElement);
    //First name
    Element nameFirstElement = document.createElement("nameFirst");
    nameFirstElement.appendChild(document.createTextNode(this.getNameFirst()));
    rootElement.appendChild(nameFirstElement);
    //Last name
    Element nameLastElement = document.createElement("nameLast");
    nameLastElement.appendChild(document.createTextNode(this.getNameLast()));
    rootElement.appendChild(nameLastElement);
    //Company name
    Element organizationElement = document.createElement("organization");
    organizationElement.appendChild(document.createTextNode(this.getCompanyName()));
    rootElement.appendChild(organizationElement);
    //Title
    Element titleElement = document.createElement("title");
    titleElement.appendChild(document.createTextNode(this.getTitle()));
    rootElement.appendChild(titleElement);
    //Phone
    Element phoneElement = document.createElement("phone");
    phoneElement.appendChild(document.createTextNode(this.getPhone()));
    rootElement.appendChild(phoneElement);
    //Extension
    Element extensionElement = document.createElement("extension");
    extensionElement.appendChild(document.createTextNode(this.getPhoneExt()));
    rootElement.appendChild(extensionElement);
    //Email address
    Element emailElement = document.createElement("email");
    emailElement.appendChild(document.createTextNode(this.getEmail()));
    rootElement.appendChild(emailElement);
    //IP address
    Element ipAddressElement = document.createElement("ipAddress");
    ipAddressElement.appendChild(document.createTextNode(this.getIpAddress()));
    rootElement.appendChild(ipAddressElement);
    //Browser
    if (this.getUserBrowser() != null) {
      Element browserElement = document.createElement("browser");
      browserElement.appendChild(document.createTextNode(this.getUserBrowser()));
      rootElement.appendChild(browserElement);
    }
    //Language
    if (this.getLanguage() != null) {
      Element languageElement = document.createElement("language");
      languageElement.appendChild(document.createTextNode(this.getLanguage()));
      rootElement.appendChild(languageElement);
    }
    //Referer
    if (this.getReferer() != null) {
      Element refererElement = document.createElement("referer");
      refererElement.appendChild(document.createTextNode(this.getReferer()));
      rootElement.appendChild(refererElement);
    }
    //return as string
    return XMLUtils.toString(documentElement);
  }
}

