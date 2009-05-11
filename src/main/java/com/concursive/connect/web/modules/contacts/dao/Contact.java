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

package com.concursive.connect.web.modules.contacts.dao;

import com.concursive.commons.db.DatabaseUtils;
import com.concursive.commons.text.StringUtils;
import com.concursive.commons.web.mvc.beans.GenericBean;
import com.concursive.connect.Constants;
import com.concursive.connect.web.modules.issues.dao.TicketContact;
import com.concursive.connect.web.modules.login.dao.User;

import java.sql.*;

/**
 * Description of Class
 *
 * @author matt rajkowski
 * @version $Id:Contact.java 2246 2007-03-22 05:57:41Z matt $
 * @created Mar 12, 2007
 */
public class Contact extends GenericBean {

  private int id = -1;
  private boolean isOrganization = false;
  private String salutation = null;
  private String firstName = null;
  private String middleName = null;
  private String lastName = null;
  private String suffixName = null;
  private String organization = null;
  private String fileAs = null;
  private String jobTitle = null;
  private String role = null;
  private String email1 = null;
  private String email2 = null;
  private String email3 = null;
  private String homePhone = null;
  private String homePhoneExt = null;
  private String home2Phone = null;
  private String home2PhoneExt = null;
  private String homeFax = null;
  private String businessPhone = null;
  private String businessPhoneExt = null;
  private String business2Phone = null;
  private String business2PhoneExt = null;
  private String businessFax = null;
  private String mobilePhone = null;
  private String pagerNumber = null;
  private String carPhone = null;
  private String radioPhone = null;
  private String webPage = null;
  private String nickname = null;
  private String comments = null;
  private int owner = -1;
  private boolean global = false;
  private Timestamp entered = null;
  private int enteredBy = -1;
  private Timestamp modified = null;
  private int modifiedBy = -1;
  private String addressLine1 = null;
  private String addressLine2 = null;
  private String addressLine3 = null;
  private String city = null;
  private String state = null;
  private String country = null;
  private String postalCode = null;
  private double latitude = 0.0;
  private double longitude = 0.0;

  /*
  private String textMessageAddress = null;
  private String imAddress = null;
  */


  public Contact() {
  }

  public Contact(Connection db, int contactId) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "SELECT c.* " +
            "FROM contacts c " +
            "WHERE contact_id = ? ");
    pst.setInt(1, contactId);
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      buildRecord(rs);
    }
    rs.close();
    pst.close();
    if (id == -1) {
      throw new SQLException("Record not found");
    }
  }

  public Contact(ResultSet rs) throws SQLException {
    buildRecord(rs);
  }


  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public void setId(String tmp) {
    id = Integer.parseInt(tmp);
  }

  public boolean getIsOrganization() {
    return isOrganization;
  }

  public void setOrganization(boolean organization) {
    isOrganization = organization;
  }

  public void setIsOrganization(String tmp) {
    isOrganization = DatabaseUtils.parseBoolean(tmp);
  }

  public String getSalutation() {
    return salutation;
  }

  public void setSalutation(String salutation) {
    this.salutation = salutation;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getMiddleName() {
    return middleName;
  }

  public void setMiddleName(String middleName) {
    this.middleName = middleName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getSuffixName() {
    return suffixName;
  }

  public void setSuffixName(String suffixName) {
    this.suffixName = suffixName;
  }

  public String getOrganization() {
    return organization;
  }

  public void setOrganization(String organization) {
    this.organization = organization;
  }

  public String getFileAs() {
    return fileAs;
  }

  public void setFileAs(String fileAs) {
    this.fileAs = fileAs;
  }

  public String getJobTitle() {
    return jobTitle;
  }

  public void setJobTitle(String jobTitle) {
    this.jobTitle = jobTitle;
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }


  public String getEmail1() {
    return email1;
  }

  public void setEmail1(String email1) {
    this.email1 = email1;
  }

  public String getEmail2() {
    return email2;
  }

  public void setEmail2(String email2) {
    this.email2 = email2;
  }

  public String getEmail3() {
    return email3;
  }

  public void setEmail3(String email3) {
    this.email3 = email3;
  }

  public String getHomePhone() {
    return homePhone;
  }

  public void setHomePhone(String homePhone) {
    this.homePhone = homePhone;
  }

  public String getHomePhoneExt() {
    return homePhoneExt;
  }

  public void setHomePhoneExt(String homePhoneExt) {
    this.homePhoneExt = homePhoneExt;
  }

  public String getHome2Phone() {
    return home2Phone;
  }

  public void setHome2Phone(String home2Phone) {
    this.home2Phone = home2Phone;
  }

  public String getHome2PhoneExt() {
    return home2PhoneExt;
  }

  public void setHome2PhoneExt(String home2PhoneExt) {
    this.home2PhoneExt = home2PhoneExt;
  }

  public String getHomeFax() {
    return homeFax;
  }

  public void setHomeFax(String homeFax) {
    this.homeFax = homeFax;
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

  public String getBusiness2Phone() {
    return business2Phone;
  }

  public void setBusiness2Phone(String business2Phone) {
    this.business2Phone = business2Phone;
  }

  public String getBusiness2PhoneExt() {
    return business2PhoneExt;
  }

  public void setBusiness2PhoneExt(String business2PhoneExt) {
    this.business2PhoneExt = business2PhoneExt;
  }

  public String getBusinessFax() {
    return businessFax;
  }

  public void setBusinessFax(String businessFax) {
    this.businessFax = businessFax;
  }

  public String getMobilePhone() {
    return mobilePhone;
  }

  public void setMobilePhone(String mobilePhone) {
    this.mobilePhone = mobilePhone;
  }

  public String getPagerNumber() {
    return pagerNumber;
  }

  public void setPagerNumber(String pagerNumber) {
    this.pagerNumber = pagerNumber;
  }

  public String getCarPhone() {
    return carPhone;
  }

  public void setCarPhone(String carPhone) {
    this.carPhone = carPhone;
  }

  public String getRadioPhone() {
    return radioPhone;
  }

  public void setRadioPhone(String radioPhone) {
    this.radioPhone = radioPhone;
  }

  public String getWebPage() {
    return webPage;
  }

  public void setWebPage(String webPage) {
    this.webPage = webPage;
  }

  public String getNickname() {
    return nickname;
  }

  public void setNickname(String nickname) {
    this.nickname = nickname;
  }

  public String getComments() {
    return comments;
  }

  public void setComments(String comments) {
    this.comments = comments;
  }

  public int getOwner() {
    return owner;
  }

  public void setOwner(int owner) {
    this.owner = owner;
  }

  public void setOwner(String tmp) {
    owner = Integer.parseInt(tmp);
  }

  public boolean getGlobal() {
    return global;
  }

  public void setGlobal(boolean global) {
    this.global = global;
  }

  public void setGlobal(String tmp) {
    global = DatabaseUtils.parseBoolean(tmp);
  }

  public Timestamp getEntered() {
    return entered;
  }

  public void setEntered(Timestamp entered) {
    this.entered = entered;
  }

  public void setEntered(String tmp) {
    this.entered = DatabaseUtils.parseTimestamp(tmp);
  }

  public int getEnteredBy() {
    return enteredBy;
  }

  public void setEnteredBy(int enteredBy) {
    this.enteredBy = enteredBy;
  }

  public void setEnteredBy(String tmp) {
    this.enteredBy = Integer.parseInt(tmp);
  }

  public Timestamp getModified() {
    return modified;
  }

  public void setModified(Timestamp modified) {
    this.modified = modified;
  }

  public void setModified(String tmp) {
    this.modified = DatabaseUtils.parseTimestamp(tmp);
  }

  public int getModifiedBy() {
    return modifiedBy;
  }

  public void setModifiedBy(int modifiedBy) {
    this.modifiedBy = modifiedBy;
  }

  public void setModifiedBy(String tmp) {
    this.modifiedBy = Integer.parseInt(tmp);
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

  /**
   * @return the latitude
   */
  public double getLatitude() {
    return latitude;
  }


  /**
   * @param latitude the latitude to set
   */
  public void setLatitude(String latitude) {
    this.latitude = Double.parseDouble(latitude);
  }


  /**
   * @param latitude the latitude to set
   */
  public void setLatitude(double latitude) {
    this.latitude = latitude;
  }


  /**
   * @return the longitude
   */
  public double getLongitude() {
    return longitude;
  }


  /**
   * @param longitude the longitude to set
   */
  public void setLongitude(String longitude) {
    this.longitude = Double.parseDouble(longitude);
  }


  /**
   * @param longitude the longitude to set
   */
  public void setLongitude(double longitude) {
    this.longitude = longitude;
  }

  public String getIndexAs() {
    // Ways to display:
    // Organization (First Last)
    // First Last (Organization)

    // Organization (Last, First)
    // Last, First (Organization)
    return
        ((isOrganization || (!StringUtils.hasText(firstName) && !StringUtils.hasText(lastName))) ?
            (organization + " " +
                (StringUtils.hasText(firstName) || StringUtils.hasText(lastName) ? "(" +
                    (StringUtils.hasText(firstName) ? firstName + " " : "") +
                    (StringUtils.hasText(lastName) ? lastName : "") +
                    ") " : "")
            ).trim() :

            (StringUtils.hasText(firstName) ? firstName + " " : "") +
                (StringUtils.hasText(lastName) ? lastName + " " : "") +
                (StringUtils.hasText(organization) ? "(" + organization + ")" : "")
        ).trim();
  }

  public String getEmailAddress() {
    if (StringUtils.hasText(email1)) {
      return email1;
    } else if (StringUtils.hasText(email2)) {
      return email2;
    } else if (StringUtils.hasText(email3)) {
      return email3;
    }
    return null;
  }

  private void buildRecord(ResultSet rs) throws SQLException {
    id = rs.getInt("contact_id");
    isOrganization = rs.getBoolean("is_organization");
    salutation = rs.getString("salutation");
    firstName = rs.getString("first_name");
    middleName = rs.getString("middle_name");
    lastName = rs.getString("last_name");
    suffixName = rs.getString("suffix_name");
    organization = rs.getString("organization");
    fileAs = rs.getString("file_as");
    jobTitle = rs.getString("job_title");
    role = rs.getString("role");
    email1 = rs.getString("email1");
    email2 = rs.getString("email2");
    email3 = rs.getString("email3");
    homePhone = rs.getString("home_phone");
    homePhoneExt = rs.getString("home_phone_ext");
    home2Phone = rs.getString("home2_phone");
    home2PhoneExt = rs.getString("home2_phone_ext");
    homeFax = rs.getString("home_fax");
    businessPhone = rs.getString("business_phone");
    businessPhoneExt = rs.getString("business_phone_ext");
    business2Phone = rs.getString("business2_phone");
    business2PhoneExt = rs.getString("business2_phone_ext");
    businessFax = rs.getString("business_fax");
    mobilePhone = rs.getString("mobile_phone");
    pagerNumber = rs.getString("pager_number");
    carPhone = rs.getString("car_phone");
    radioPhone = rs.getString("radio_phone");
    webPage = rs.getString("web_page");
    nickname = rs.getString("nickname");
    comments = rs.getString("comments");
    owner = DatabaseUtils.getInt(rs, "owner");
    global = rs.getBoolean("global");
    entered = rs.getTimestamp("entered");
    enteredBy = rs.getInt("enteredby");
    modified = rs.getTimestamp("modified");
    modifiedBy = rs.getInt("modifiedby");
    addressLine1 = rs.getString("addrline1");
    addressLine2 = rs.getString("addrline2");
    addressLine3 = rs.getString("addrline3");
    city = rs.getString("city");
    state = rs.getString("state");
    country = rs.getString("country");
    postalCode = rs.getString("postalcode");
    latitude = DatabaseUtils.getDouble(rs, "latitude", 0.0);
    longitude = DatabaseUtils.getDouble(rs, "longitude", 0.0);
  }

  public boolean insert(Connection db) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "INSERT INTO contacts (is_organization, salutation, first_name, middle_name, last_name," +
            "suffix_name, organization, file_as, job_title, role, " +
            "email1, email2, email3, " +
            "home_phone, home_phone_ext, home2_phone, home2_phone_ext, " +
            "home_fax, business_phone, business_phone_ext, " +
            "business2_phone, business2_phone_ext, business_fax, " +
            "mobile_phone, pager_number, car_phone, radio_phone, " +
            "web_page, nickname, comments, owner, global, " +
            "addrline1, addrline2, addrline3, city, state, country, postalcode, " +
            (entered != null ? "entered, " : "") +
            (modified != null ? "modified, " : "") +
            "enteredby, " +
            "modifiedby" +
            //"title, " +
            //"categories, birthday, anniversary, spouse, children, " +
            //"department, office_location, " +
            //"assistant_name, assistant_telephone, " +
            //"home_address_street, home_address_city, home_address_state, home_address_postal_code, home_address_country, " +
            //"other_address_street, other_address_city, other_address_state, other_address_postal_code, other_address_country, " +
            //"business_address_street, business_address_city, business_address_state, business_address_postal_code, business_address_country, " +            
            ") " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
            "?, ?, ?, ?, ?, ?, ?, " +
            (entered != null ? "?, " : "") +
            (modified != null ? "?, " : "") +
            "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
    int i = 0;
    pst.setBoolean(++i, isOrganization);
    pst.setString(++i, salutation);
    pst.setString(++i, firstName);
    pst.setString(++i, middleName);
    pst.setString(++i, lastName);
    pst.setString(++i, suffixName);
    pst.setString(++i, organization);
    if (!StringUtils.hasText(fileAs)) {
      fileAs = getIndexAs();
    }
    pst.setString(++i, fileAs);
    pst.setString(++i, jobTitle);
    pst.setString(++i, role);
    pst.setString(++i, email1);
    pst.setString(++i, email2);
    pst.setString(++i, email3);
    pst.setString(++i, homePhone);
    pst.setString(++i, homePhoneExt);
    pst.setString(++i, home2Phone);
    pst.setString(++i, home2PhoneExt);
    pst.setString(++i, homeFax);
    pst.setString(++i, businessPhone);
    pst.setString(++i, businessPhoneExt);
    pst.setString(++i, business2Phone);
    pst.setString(++i, business2PhoneExt);
    pst.setString(++i, businessFax);
    pst.setString(++i, mobilePhone);
    pst.setString(++i, pagerNumber);
    pst.setString(++i, carPhone);
    pst.setString(++i, radioPhone);
    pst.setString(++i, webPage);
    pst.setString(++i, nickname);
    pst.setString(++i, comments);
    DatabaseUtils.setInt(pst, ++i, owner);
    pst.setBoolean(++i, global);
    pst.setString(++i, addressLine1);
    pst.setString(++i, addressLine2);
    pst.setString(++i, addressLine3);
    pst.setString(++i, city);
    pst.setString(++i, state);
    pst.setString(++i, country);
    pst.setString(++i, postalCode);
    if (entered != null) {
      pst.setTimestamp(++i, entered);
    }
    if (modified != null) {
      pst.setTimestamp(++i, modified);
    }
    pst.setInt(++i, enteredBy);
    pst.setInt(++i, modifiedBy);
    pst.execute();
    pst.close();
    id = DatabaseUtils.getCurrVal(db, "contacts_contact_id_seq", -1);
    return true;
  }

  public int update(Connection db) throws SQLException {
    int updateCount = -1;
    PreparedStatement pst = db.prepareStatement(
        "UPDATE contacts " +
            "SET is_organization = ?, salutation = ?, first_name = ?, middle_name = ?, last_name = ?," +
            "suffix_name = ?, organization = ?, file_as = ?, job_title = ?, role = ?, " +
            "email1 = ?, email2 = ?, email3 = ?, " +
            "home_phone = ?, home_phone_ext = ?, home2_phone = ?, home2_phone_ext = ?, " +
            "home_fax = ?, business_phone = ?, business_phone_ext = ?, " +
            "business2_phone = ?, business2_phone_ext = ?, business_fax = ?, " +
            "mobile_phone = ?, pager_number = ?, car_phone = ?, radio_phone = ?, " +
            "web_page = ?, nickname = ?, comments = ?, owner = ?, global = ?, " +
            "addrline1 = ?, addrline2 = ?, addrline3 = ?, city = ?, state = ?, country = ?, postalcode = ?, " +
            (entered != null ? "entered = ?, " : "") +
            "modified = CURRENT_TIMESTAMP, " +
            "modifiedby = ? " +
            "WHERE contact_id = ?");
    int i = 0;
    pst.setBoolean(++i, isOrganization);
    pst.setString(++i, salutation);
    pst.setString(++i, firstName);
    pst.setString(++i, middleName);
    pst.setString(++i, lastName);
    pst.setString(++i, suffixName);
    pst.setString(++i, organization);
    if (!StringUtils.hasText(fileAs)) {
      fileAs = getIndexAs();
    }
    pst.setString(++i, fileAs);
    pst.setString(++i, jobTitle);
    pst.setString(++i, role);
    pst.setString(++i, email1);
    pst.setString(++i, email2);
    pst.setString(++i, email3);
    pst.setString(++i, homePhone);
    pst.setString(++i, homePhoneExt);
    pst.setString(++i, home2Phone);
    pst.setString(++i, home2PhoneExt);
    pst.setString(++i, homeFax);
    pst.setString(++i, businessPhone);
    pst.setString(++i, businessPhoneExt);
    pst.setString(++i, business2Phone);
    pst.setString(++i, business2PhoneExt);
    pst.setString(++i, businessFax);
    pst.setString(++i, mobilePhone);
    pst.setString(++i, pagerNumber);
    pst.setString(++i, carPhone);
    pst.setString(++i, radioPhone);
    pst.setString(++i, webPage);
    pst.setString(++i, nickname);
    pst.setString(++i, comments);
    DatabaseUtils.setInt(pst, ++i, owner);
    pst.setBoolean(++i, global);
    pst.setString(++i, addressLine1);
    pst.setString(++i, addressLine2);
    pst.setString(++i, addressLine3);
    pst.setString(++i, city);
    pst.setString(++i, state);
    pst.setString(++i, country);
    pst.setString(++i, postalCode);
    if (entered != null) {
      pst.setTimestamp(++i, entered);
    }
    pst.setInt(++i, modifiedBy);
    pst.setInt(++i, id);
    updateCount = pst.executeUpdate();
    pst.close();
    return updateCount;
  }

  public void delete(Connection db) throws SQLException {
    // Delete referential data
    TicketContact ticketContact = new TicketContact();
    ticketContact.setId(id);
    ticketContact.delete(db);
    // Delete the contact
    PreparedStatement pst = db.prepareStatement(
        "DELETE FROM contacts WHERE contact_id = ? ");
    pst.setInt(1, id);
    pst.execute();
    pst.close();
  }

  public void addToShare(Connection db, int sharedFrom, int sharedTo, boolean allowEdit) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "INSERT INTO contacts_share (contact_id, shared_from, shared_to, allow_edit) " +
            "VALUES (?, ?, ?, ?) ");
    int i = 0;
    pst.setInt(++i, id);
    DatabaseUtils.setInt(pst, ++i, sharedFrom);
    DatabaseUtils.setInt(pst, ++i, sharedTo);
    pst.setBoolean(++i, allowEdit);
    pst.execute();
    pst.close();
  }

  public String getEmailAsText() {
    StringBuffer sb = new StringBuffer("");
    if (StringUtils.hasText(email1)) {
      sb.append(email1);
    }
    if (StringUtils.hasText(email2)) {
      if (sb.length() > 0) {
        sb.append(Constants.LF);
      }
      sb.append(email2);
    }
    if (StringUtils.hasText(email3)) {
      if (sb.length() > 0) {
        sb.append(Constants.LF);
      }
      sb.append(email3);
    }
    return sb.toString();
  }

  public String getPhoneAsText() {
    StringBuffer sb = new StringBuffer("");
    if (StringUtils.hasText(businessPhone)) {
      sb.append(businessPhone);
      if (StringUtils.hasText(businessPhoneExt)) {
        sb.append(" x").append(businessPhoneExt);
      }
      sb.append(" [Business]");
    }
    if (StringUtils.hasText(business2Phone)) {
      if (sb.length() > 0) {
        sb.append(Constants.LF);
      }
      sb.append(business2Phone);
      if (StringUtils.hasText(business2PhoneExt)) {
        sb.append(" x").append(business2PhoneExt);
      }
      sb.append(" [Business 2]");
    }
    if (StringUtils.hasText(mobilePhone)) {
      if (sb.length() > 0) {
        sb.append(Constants.LF);
      }
      sb.append(mobilePhone);
      sb.append(" [Mobile]");
    }
    if (StringUtils.hasText(homePhone)) {
      if (sb.length() > 0) {
        sb.append(Constants.LF);
      }
      sb.append(homePhone);
      if (StringUtils.hasText(homePhoneExt)) {
        sb.append(" x").append(homePhoneExt);
      }
      sb.append(" [Home]");
    }
    if (StringUtils.hasText(home2Phone)) {
      if (sb.length() > 0) {
        sb.append(Constants.LF);
      }
      sb.append(home2Phone);
      if (StringUtils.hasText(home2PhoneExt)) {
        sb.append(" x").append(home2PhoneExt);
      }
      sb.append(" [Home 2]");
    }
    return sb.toString();
  }

  public boolean hasReadAccess(User thisUser) {
    return (thisUser.getId() == owner || (thisUser.getAccessViewAllContacts() && global));
  }

  public boolean hasWriteAccess(User thisUser) {
    return (thisUser.getId() == owner || (thisUser.getAccessEditAllContacts() && global));
  }

  public boolean isGeoCoded() {
    return (latitude != 0.0 && longitude != 0.0);
  }
}
