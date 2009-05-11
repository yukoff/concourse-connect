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
package com.concursive.connect.web.modules.contactus.dao;

import com.concursive.commons.db.DatabaseUtils;
import com.concursive.commons.email.SMTPMessage;
import com.concursive.commons.email.SMTPMessageFactory;
import com.concursive.commons.text.StringUtils;
import com.concursive.commons.web.mvc.actions.ActionContext;
import com.concursive.commons.web.mvc.beans.GenericBean;
import com.concursive.connect.config.ApplicationPrefs;
import com.concursive.connect.web.modules.login.utils.UserAdmins;
import nl.captcha.Captcha;

import javax.servlet.http.HttpSession;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * HTML form bean for the contact us page
 *
 * @author matt rajkowski
 * @version $Id$
 * @created December 2, 2003
 */
public class ContactUsBean extends GenericBean {

  public static final String lf = System.getProperty("line.separator");
  //Base properties
  private int id = -1;
  //Form properties
  private String nameFirst = null;
  private String nameLast = null;
  private String email = null;
  private String organization = null;
  private String companySize = null;
  private String companyRevenue = null;
  private String description = null;
  private String language = null;
  private boolean emailCopy = false;
  private String captcha = null;
  private String jobTitle = null;
  private String businessPhone = null;
  private String businessPhoneExt = null;
  private String addressLine1 = null;
  private String addressLine2 = null;
  private String addressLine3 = null;
  private String city = null;
  private String state = null;
  private String country = null;
  private String postalCode = null;
  private String[] formData = null;
  private String formDataString = null;
  private ArrayList formDataList = new ArrayList();

  public void setNameFirst(String tmp) {
    this.nameFirst = tmp;
  }


  public void setNameLast(String tmp) {
    this.nameLast = tmp;
  }


  public void setEmail(String tmp) {
    this.email = tmp;
  }


  public void setOrganization(String tmp) {
    this.organization = tmp;
  }


  public void setDescription(String tmp) {
    this.description = tmp;
  }


  public void setEmailCopy(boolean tmp) {
    this.emailCopy = tmp;
  }


  public void setEmailCopy(String tmp) {
    this.emailCopy = DatabaseUtils.parseBoolean(tmp);
  }


  public int getId() {
    return id;
  }


  public String getNameFirst() {
    return nameFirst;
  }


  public String getNameLast() {
    return nameLast;
  }


  public String getEmail() {
    return email;
  }


  public String getOrganization() {
    return organization;
  }


  public String getDescription() {
    return description;
  }


  public boolean getEmailCopy() {
    return emailCopy;
  }


  public String getLanguage() {
    return language;
  }


  public void setLanguage(String language) {
    this.language = language;
  }


  public String getCaptcha() {
    return captcha;
  }


  public void setCaptcha(String captcha) {
    this.captcha = captcha;
  }


  public String getJobTitle() {
    return jobTitle;
  }


  public void setJobTitle(String jobTitle) {
    this.jobTitle = jobTitle;
  }


  public String getBusinessPhone() {
    return businessPhone;
  }


  public void setBusinessPhone(String businessPhone) {
    this.businessPhone = businessPhone;
  }


  public String getBusinessPhoneExt() {
    return businessPhoneExt;
  }


  public void setBusinessPhoneExt(String businessPhoneExt) {
    this.businessPhoneExt = businessPhoneExt;
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


  public String getCompanySize() {
    return companySize;
  }


  public String getCompanyRevenue() {
    return companyRevenue;
  }


  public String[] getFormData() {
    return formData;
  }


  public void setFormData(String[] formData) {
    StringBuffer sbf = new StringBuffer();
    if (formData != null) {
      for (int i = 0; i < formData.length; i++) {
        formDataList.add(formData[i]);
        sbf.append("[" + formData[i] + "]");
      }
      this.formDataString = sbf.toString();
    }
    this.formData = formData;
  }


  public String getFormDataString() {
    return formDataString;
  }


  public String getFormData(String key) {
    if (formDataList == null) {
      return null;
    }

    Iterator it = formDataList.iterator();
    String temp = null;
    while (it.hasNext()) {
      temp = "" + it.next();
      if (temp.indexOf(key) != -1) {
        return temp;
      }
    }
    return null;
  }


  public boolean isValid(HttpSession session) {
    Captcha captchaValue = (Captcha) session.getAttribute(Captcha.NAME);
    session.removeAttribute(Captcha.NAME);
    if (captchaValue == null || captcha == null ||
        !captchaValue.isCorrect(captcha)) {
      errors.put("captchaError", "Text did not match image");
    }
    if (!StringUtils.hasText(getNameFirst())) {
      errors.put("nameFirstError", "Required field");
    }
    if (!StringUtils.hasText(getNameLast())) {
      errors.put("nameLastError", "Required field");
    }
    if (!StringUtils.hasText(getEmail())) {
      errors.put("emailError", "Required field");
    }
    if (!StringUtils.hasText(getDescription())) {
      errors.put("descriptionError", "Required field");
    }
    return (!hasErrors());
  }


  public HashMap<String, String> getError() {
    return errors;
  }


  public boolean save(ActionContext context, Connection db) throws SQLException {
    //store message in database
    PreparedStatement pst = db.prepareStatement(
        "INSERT INTO contact_us (first_name, last_name, email, organization, description, copied, ip_address, browser, language, " +
            "job_title, business_phone, business_phone_ext, addrline1, addrline2, addrline3, " +
            "city, state, country, postalcode, form_data) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ");
    StringBuffer sbf = new StringBuffer();
    if (formData != null) {
      for (int m = 0; m < formData.length; m++) {
        sbf.append("[" + formData[m] + "]");
      }
    }
    int i = 0;
    pst.setString(++i, nameFirst);
    pst.setString(++i, nameLast);
    pst.setString(++i, email);
    pst.setString(++i, organization);
    pst.setString(++i, description);
    pst.setBoolean(++i, emailCopy);
    pst.setString(++i, context.getIpAddress());
    pst.setString(++i, context.getBrowser());
    pst.setString(++i, language);
    pst.setString(++i, jobTitle);
    pst.setString(++i, businessPhone);
    pst.setString(++i, businessPhoneExt);
    pst.setString(++i, addressLine1);
    pst.setString(++i, addressLine2);
    pst.setString(++i, addressLine3);
    pst.setString(++i, city);
    pst.setString(++i, state);
    pst.setString(++i, country);
    pst.setString(++i, postalCode);
    pst.setString(++i, sbf.toString());
    pst.execute();
    pst.close();
    id = DatabaseUtils.getCurrVal(db, "contact_us_request_id_seq", -1);
    //send the confirmation message if requested
    if (emailCopy) {
      String form = "Contact Us";
      if (language != null) {
        form = "Language Application";
      }
      ApplicationPrefs prefs = (ApplicationPrefs) context.getServletContext().getAttribute(
          "applicationPrefs");
      SMTPMessage message = SMTPMessageFactory.createSMTPMessageInstance(prefs.getPrefs());
      message.setTo(UserAdmins.getEmailAddresses(db));
      message.addReplyTo(email);
      message.setFrom(prefs.get("EMAILADDRESS"));
      message.setSubject(form + " Form");
      message.setBody(
          "The following information was submitted using the \"" + form + "\" form: " + lf + lf +
              "First Name: " + nameFirst + lf +
              "Last Name: " + nameLast + lf +
              "Email Address: " + email + lf +
              "Organization: " + organization + lf +
              (language != null ? "Language: " + language + lf : "") +
              "Question/Comments: " + description + lf);
      message.send();
    }
    return true;
  }
}

