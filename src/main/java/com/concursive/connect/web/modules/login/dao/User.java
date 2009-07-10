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

package com.concursive.connect.web.modules.login.dao;

import com.concursive.commons.codec.PasswordHash;
import com.concursive.commons.db.DatabaseUtils;
import com.concursive.commons.email.SMTPMessage;
import com.concursive.commons.email.SMTPMessageFactory;
import com.concursive.commons.text.StringUtils;
import com.concursive.commons.web.mvc.actions.ActionContext;
import com.concursive.commons.web.mvc.beans.GenericBean;
import com.concursive.connect.Constants;
import com.concursive.connect.cache.utils.CacheUtils;
import com.concursive.connect.config.ApplicationPrefs;
import com.concursive.connect.web.modules.login.utils.UserUtils;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import com.concursive.connect.web.modules.translation.dao.WebSiteLanguageList;
import com.concursive.connect.web.utils.PagedListInfo;
import com.concursive.connect.web.webdav.servlets.WebdavServlet;

import java.sql.*;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Stack;

/**
 * Represents a user of the system
 *
 * @author matt rajkowski
 * @version $Id$
 * @created January 6, 2002
 */
public class User extends GenericBean {

  public final static String lf = System.getProperty("line.separator");

  // Properties
  private int instanceId = -1;
  private int id = -1;
  private String firstName = null;
  private String lastName = null;
  private String company = null;
  private String email = null;
  private String username = null;
  private String password = null;
  private String password1 = null;
  private String password2 = null;
  private String temporaryPassword = null;
  private int groupId = -1;
  private int departmentId = -1;
  private String department = null;
  private boolean accessAdmin = false;
  private boolean accessInvite = false;
  private boolean accessUserSettings = false;
  private boolean accessGuestProjects = false;
  private boolean accessAddProjects = false;
  private boolean accessViewAllContacts = false;
  private boolean accessEditAllContacts = false;
  private int startPage = -1;
  private boolean enabled = false;
  private boolean terms = false;
  private int enteredBy = -1;
  private Timestamp entered = null;
  private int modifiedBy = -1;
  private Timestamp modified = null;
  private java.sql.Timestamp lastLogin = null;
  private Timestamp expiration = null;
  private boolean registered = true;
  private int accountSize = -1;
  private long currentAccountSize = 0;
  private String city = null;
  private String state = null;
  private String country = null;
  private String postalCode = null;
  private String timeZone = null;
  private String currency = null;
  private String language = null;
  private boolean watchForums = false;
  private String nickname = null;
  private int salutationId = -1;
  private int profileProjectId = -1;
  private int showProfileTo = Constants.WITH_ANYONE;
  private int showFullNameTo = Constants.WITH_FRIENDS;
  private int showEmailTo = Constants.WITH_NO_ONE;
  private int showGenderTo = Constants.WITH_ANYONE;
  private int showLocationTo = Constants.WITH_ANYONE;
  private int showCompanyTo = Constants.WITH_ANYONE;
  private int points = 0;

  // Helper properties
  private boolean apiRestore = false;
  private String browserType = "";
  private String template = null;
  private String cssFile = "";
  private String idRange = "";
  private Locale locale = null;
  private String sessionId = null;
  private Stack<Integer> recentProjects = new Stack<Integer>();
  private WebSiteLanguageList webSiteLanguageList = new WebSiteLanguageList();

  /**
   * Constructor for the User object
   */
  public User() {
  }


  /**
   * Constructor for the User object
   *
   * @param rs Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public User(ResultSet rs) throws SQLException {
    build(rs);
  }


  /**
   * Constructor for the User object
   *
   * @param db     Description of the Parameter
   * @param userId Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public User(Connection db, int userId) throws SQLException {
    queryRecord(db, userId);
  }


  /**
   * Constructor for the User object
   *
   * @param db      Description of the Parameter
   * @param groupId Description of the Parameter
   * @param userId  Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public User(Connection db, int groupId, int userId) throws SQLException {
    this.groupId = groupId;
    queryRecord(db, userId);
  }


  public void queryRecord(Connection db, int userId) throws SQLException {
    if (userId < 1) {
      throw new SQLException("User-> Invalid userId specified: " + userId);
    }
    String sql =
        "SELECT u.*, d.description as department " +
            "FROM users u LEFT JOIN departments d ON (u.department_id = d.code) " +
            "WHERE user_id = ? " + (groupId > -1 ? "AND u.group_id = ? " : "");
    PreparedStatement pst = db.prepareStatement(sql);
    pst.setInt(1, userId);
    if (groupId > -1) {
      pst.setInt(2, groupId);
    }
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      build(rs);
    }
    rs.close();
    pst.close();
  }


  /**
   * Description of the Method
   *
   * @param rs Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  private void build(ResultSet rs) throws SQLException {
    //user table
    id = rs.getInt("user_id");
    groupId = rs.getInt("group_id");
    departmentId = rs.getInt("department_id");
    firstName = rs.getString("first_name");
    lastName = rs.getString("last_name");
    username = rs.getString("username");
    password = rs.getString("password");
    temporaryPassword = rs.getString("temporary_password");
    company = rs.getString("company");
    email = rs.getString("email");
    entered = rs.getTimestamp("entered");
    enteredBy = rs.getInt("enteredby");
    enabled = rs.getBoolean("enabled");
    startPage = rs.getInt("start_page");
    accessAdmin = rs.getBoolean("access_admin");
    accessInvite = rs.getBoolean("access_invite");
    accessUserSettings = rs.getBoolean("access_settings");
    accessGuestProjects = rs.getBoolean("access_guest");
    lastLogin = rs.getTimestamp("last_login");
    expiration = rs.getTimestamp("expiration");
    registered = rs.getBoolean("registered");
    accountSize = DatabaseUtils.getInt(rs, "account_size");
    terms = rs.getBoolean("terms");
    timeZone = rs.getString("timezone");
    currency = rs.getString("currency");
    this.setLanguage(rs.getString("language"));
    modified = rs.getTimestamp("modified");
    modifiedBy = rs.getInt("modifiedby");
    // new fields not in original
    try {
      accessAddProjects = rs.getBoolean("access_add_projects");
      accessViewAllContacts = rs.getBoolean("access_contacts_view_all");
      accessEditAllContacts = rs.getBoolean("access_contacts_edit_all");
      watchForums = rs.getBoolean("watch_forums");
      nickname = rs.getString("nickname");
      salutationId = rs.getInt("salutation");
      profileProjectId = rs.getInt("profile_project_id");
      showProfileTo = DatabaseUtils.getInt(rs, "show_profile_to", Constants.WITH_ANYONE);
      showFullNameTo = DatabaseUtils.getInt(rs, "show_fullname_to", Constants.WITH_FRIENDS);
      showEmailTo = DatabaseUtils.getInt(rs, "show_email_to", Constants.WITH_NO_ONE);
      showGenderTo = DatabaseUtils.getInt(rs, "show_gender_to", Constants.WITH_ANYONE);
      showLocationTo = DatabaseUtils.getInt(rs, "show_location_to", Constants.WITH_ANYONE);
      showCompanyTo = DatabaseUtils.getInt(rs, "show_company_to", Constants.WITH_ANYONE);
      points = rs.getInt("points");
      //department table
      department = rs.getString("department");
      instanceId = DatabaseUtils.getInt(rs, "instance_id", -1);
    } catch (Exception e) {
      // since these field may not exist in an upgraded system,
      // do not throw an error
    }
    //cleanup
    idRange = String.valueOf(id);
  }

  public int getInstanceId() {
    return instanceId;
  }

  public void setInstanceId(int instanceId) {
    this.instanceId = instanceId;
  }

  public void setInstanceId(String tmp) {
    this.instanceId = Integer.parseInt(tmp);
  }

  /**
   * Sets the id attribute of the User object
   *
   * @param tmp The new id value
   */
  public void setId(int tmp) {
    this.id = tmp;
  }


  /**
   * Sets the id attribute of the User object
   *
   * @param tmp The new id value
   */
  public void setId(String tmp) {
    this.id = Integer.parseInt(tmp);
  }


  /**
   * Sets the idRange attribute of the User object
   *
   * @param tmp The new idRange value
   */
  public void setIdRange(String tmp) {
    this.idRange = tmp;
  }


  /**
   * Sets the browserType attribute of the User object
   *
   * @param tmp The new browserType value
   */
  public void setBrowserType(String tmp) {
    this.browserType = tmp;
  }


  /**
   * Sets the cssFile attribute of the User object
   *
   * @param tmp The new cssFile value
   */
  public void setCssFile(String tmp) {
    this.cssFile = tmp;
  }


  /**
   * Sets the template attribute of the User object
   *
   * @param tmp The new template value
   */
  public void setTemplate(String tmp) {
    this.template = tmp;
  }


  /**
   * Sets the firstName attribute of the User object
   *
   * @param tmp The new firstName value
   */
  public void setFirstName(String tmp) {
    this.firstName = tmp;
  }


  /**
   * Sets the lastName attribute of the User object
   *
   * @param tmp The new lastName value
   */
  public void setLastName(String tmp) {
    this.lastName = tmp;
  }


  /**
   * Sets the company attribute of the User object
   *
   * @param tmp The new company value
   */
  public void setCompany(String tmp) {
    this.company = tmp;
  }


  /**
   * Sets the email attribute of the User object
   *
   * @param tmp The new email value
   */
  public void setEmail(String tmp) {
    this.email = tmp;
  }


  /**
   * Sets the username attribute of the User object
   *
   * @param tmp The new username value
   */
  public void setUsername(String tmp) {
    this.username = tmp;
  }


  /**
   * Sets the password attribute of the User object
   *
   * @param tmp The new password value
   */
  public void setPassword(String tmp) {
    this.password = tmp;
  }


  /**
   * Gets the password1 attribute of the User object
   *
   * @return The password1 value
   */
  public String getPassword1() {
    return password1;
  }


  /**
   * Sets the password1 attribute of the User object
   *
   * @param tmp The new password1 value
   */
  public void setPassword1(String tmp) {
    this.password1 = tmp;
  }


  /**
   * Gets the password2 attribute of the User object
   *
   * @return The password2 value
   */
  public String getPassword2() {
    return password2;
  }


  /**
   * Sets the password2 attribute of the User object
   *
   * @param tmp The new password2 value
   */
  public void setPassword2(String tmp) {
    this.password2 = tmp;
  }


  /**
   * Sets the temporaryPassword attribute of the User object
   *
   * @param tmp The new temporaryPassword value
   */
  public void setTemporaryPassword(String tmp) {
    this.temporaryPassword = tmp;
  }


  /**
   * Sets the groupId attribute of the User object
   *
   * @param tmp The new groupId value
   */
  public void setGroupId(int tmp) {
    this.groupId = tmp;
  }


  /**
   * Sets the groupId attribute of the User object
   *
   * @param tmp The new groupId value
   */
  public void setGroupId(String tmp) {
    this.groupId = Integer.parseInt(tmp);
  }


  /**
   * Sets the departmentId attribute of the User object
   *
   * @param tmp The new departmentId value
   */
  public void setDepartmentId(int tmp) {
    this.departmentId = tmp;
  }


  /**
   * Sets the departmentId attribute of the User object
   *
   * @param tmp The new departmentId value
   */
  public void setDepartmentId(String tmp) {
    this.departmentId = Integer.parseInt(tmp);
  }


  /**
   * Sets the accessAdmin attribute of the User object
   *
   * @param tmp The new accessAdmin value
   */
  public void setAccessAdmin(boolean tmp) {
    this.accessAdmin = tmp;
  }


  /**
   * Sets the accessAdmin attribute of the User object
   *
   * @param tmp The new accessAdmin value
   */
  public void setAccessAdmin(String tmp) {
    this.accessAdmin = DatabaseUtils.parseBoolean(tmp);
  }


  /**
   * Sets the accessInvite attribute of the User object
   *
   * @param tmp The new accessInvite value
   */
  public void setAccessInvite(boolean tmp) {
    this.accessInvite = tmp;
  }


  /**
   * Sets the accessInvite attribute of the User object
   *
   * @param tmp The new accessInvite value
   */
  public void setAccessInvite(String tmp) {
    this.accessInvite = DatabaseUtils.parseBoolean(tmp);
  }


  /**
   * Sets the accessUserSettings attribute of the User object
   *
   * @param tmp The new accessUserSettings value
   */
  public void setAccessUserSettings(boolean tmp) {
    this.accessUserSettings = tmp;
  }


  /**
   * Sets the accessUserSettings attribute of the User object
   *
   * @param tmp The new accessUserSettings value
   */
  public void setAccessUserSettings(String tmp) {
    this.accessUserSettings = DatabaseUtils.parseBoolean(tmp);
  }


  /**
   * Sets the accessGuestProjects attribute of the User object
   *
   * @param tmp The new accessGuestProjects value
   */
  public void setAccessGuestProjects(boolean tmp) {
    this.accessGuestProjects = tmp;
  }


  /**
   * Sets the accessGuestProjects attribute of the User object
   *
   * @param tmp The new accessGuestProjects value
   */
  public void setAccessGuestProjects(String tmp) {
    this.accessGuestProjects = DatabaseUtils.parseBoolean(tmp);
  }


  /**
   * Gets the accessAddProjects attribute of the User object
   *
   * @return The accessAddProjects value
   */
  public boolean getAccessAddProjects() {
    return accessAddProjects;
  }


  /**
   * Sets the accessAddProjects attribute of the User object
   *
   * @param tmp The new accessAddProjects value
   */
  public void setAccessAddProjects(boolean tmp) {
    this.accessAddProjects = tmp;
  }


  /**
   * Sets the accessAddProjects attribute of the User object
   *
   * @param tmp The new accessAddProjects value
   */
  public void setAccessAddProjects(String tmp) {
    this.accessAddProjects = DatabaseUtils.parseBoolean(tmp);
  }


  public boolean getAccessRunReports() {
    // Use the add projects permission
    return accessAddProjects;
  }

  public WebSiteLanguageList getWebSiteLanguageList() {
    return webSiteLanguageList;
  }

  public void setWebSiteLanguageList(WebSiteLanguageList webSiteLanguageList) {
    this.webSiteLanguageList = webSiteLanguageList;
  }

  public boolean hasContentEditorAccess(String languageLocale) {
    return (getAccessAdmin() ||
        (webSiteLanguageList.size() > 0 && webSiteLanguageList.getLanguage(languageLocale) != null));
  }

  public boolean hasContentEditorAccess(int languageId) {
    return (getAccessAdmin() ||
        (webSiteLanguageList.size() > 0 && webSiteLanguageList.getLanguage(languageId) != null));
  }

  /**
   * Sets the startPage attribute of the User object
   *
   * @param tmp The new startPage value
   */
  public void setStartPage(int tmp) {
    this.startPage = tmp;
  }


  /**
   * Sets the startPage attribute of the User object
   *
   * @param tmp The new startPage value
   */
  public void setStartPage(String tmp) {
    this.startPage = Integer.parseInt(tmp);
  }


  /**
   * Sets the enabled attribute of the User object
   *
   * @param tmp The new enabled value
   */
  public void setEnabled(boolean tmp) {
    this.enabled = tmp;
  }


  /**
   * Sets the enabled attribute of the User object
   *
   * @param tmp The new enabled value
   */
  public void setEnabled(String tmp) {
    this.enabled = DatabaseUtils.parseBoolean(tmp);
  }


  /**
   * Sets the terms attribute of the User object
   *
   * @param tmp The new terms value
   */
  public void setTerms(boolean tmp) {
    this.terms = tmp;
  }


  /**
   * Sets the terms attribute of the User object
   *
   * @param tmp The new terms value
   */
  public void setTerms(String tmp) {
    this.terms = DatabaseUtils.parseBoolean(tmp);
  }


  /**
   * Sets the enteredBy attribute of the User object
   *
   * @param tmp The new enteredBy value
   */
  public void setEnteredBy(int tmp) {
    this.enteredBy = tmp;
  }


  /**
   * Sets the enteredBy attribute of the User object
   *
   * @param tmp The new enteredBy value
   */
  public void setEnteredBy(String tmp) {
    this.enteredBy = Integer.parseInt(tmp);
  }


  /**
   * Sets the entered attribute of the User object
   *
   * @param tmp The new entered value
   */
  public void setEntered(Timestamp tmp) {
    this.entered = tmp;
  }


  /**
   * Sets the entered attribute of the User object
   *
   * @param tmp The new entered value
   */
  public void setEntered(String tmp) {
    this.entered = DatabaseUtils.parseTimestamp(tmp);
  }


  /**
   * Sets the modifiedBy attribute of the User object
   *
   * @param tmp The new modifiedBy value
   */
  public void setModifiedBy(int tmp) {
    this.modifiedBy = tmp;
  }


  /**
   * Sets the modifiedBy attribute of the User object
   *
   * @param tmp The new modifiedBy value
   */
  public void setModifiedBy(String tmp) {
    this.modifiedBy = Integer.parseInt(tmp);
  }


  /**
   * Sets the modified attribute of the User object
   *
   * @param tmp The new modified value
   */
  public void setModified(Timestamp tmp) {
    this.modified = tmp;
  }


  /**
   * Sets the modified attribute of the User object
   *
   * @param tmp The new modified value
   */
  public void setModified(String tmp) {
    this.modified = DatabaseUtils.parseTimestamp(tmp);
  }


  /**
   * Sets the department attribute of the User object
   *
   * @param tmp The new department value
   */
  public void setDepartment(String tmp) {
    this.department = tmp;
  }


  /**
   * Sets the expiration attribute of the User object
   *
   * @param tmp The new expiration value
   */
  public void setExpiration(Timestamp tmp) {
    this.expiration = tmp;
  }


  /**
   * Sets the expiration attribute of the User object
   *
   * @param tmp The new expiration value
   */
  public void setExpiration(String tmp) {
    this.expiration = DatabaseUtils.parseTimestamp(tmp);
  }


  /**
   * Sets the registered attribute of the User object
   *
   * @param tmp The new registered value
   */
  public void setRegistered(boolean tmp) {
    this.registered = tmp;
  }


  /**
   * Sets the registered attribute of the User object
   *
   * @param tmp The new registered value
   */
  public void setRegistered(String tmp) {
    this.registered = DatabaseUtils.parseBoolean(tmp);
  }


  /**
   * Sets the accountSize attribute of the User object
   *
   * @param tmp The new accountSize value
   */
  public void setAccountSize(int tmp) {
    this.accountSize = tmp;
  }


  /**
   * Sets the accountSize attribute of the User object
   *
   * @param tmp The new accountSize value
   */
  public void setAccountSize(String tmp) {
    this.accountSize = Integer.parseInt(tmp);
  }


  /**
   * Sets the currentAccountSize attribute of the User object
   *
   * @param tmp The new currentAccountSize value
   */
  public void setCurrentAccountSize(long tmp) {
    this.currentAccountSize = tmp;
  }


  /**
   * Sets the currentAccountSize attribute of the User object
   *
   * @param tmp The new currentAccountSize value
   */
  public void setCurrentAccountSize(String tmp) {
    this.currentAccountSize = Long.parseLong(tmp);
  }

  /**
   * @return the city
   */
  public String getCity() {
    return city;
  }


  /**
   * @param city the city to set
   */
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
   * Sets the timeZone attribute of the User object
   *
   * @param tmp The new timeZone value
   */
  public void setTimeZone(String tmp) {
    this.timeZone = tmp;
  }


  /**
   * Gets the timeZone attribute of the User object
   *
   * @return The timeZone value
   */
  public String getTimeZone() {
    return timeZone;
  }


  /**
   * Sets the currency attribute of the User object
   *
   * @param tmp The new currency value
   */
  public void setCurrency(String tmp) {
    this.currency = tmp;
  }


  /**
   * Gets the currency attribute of the User object
   *
   * @return The currency value
   */
  public String getCurrency() {
    return currency;
  }


  /**
   * Sets the language attribute of the User object
   *
   * @param tmp The new language value
   */
  public void setLanguage(String tmp) {
    this.language = tmp;
    if (language == null) {
      locale = Locale.getDefault();
    } else {
      switch (language.length()) {
        case 2:
          locale = new Locale(language.substring(0, 2), "");
          break;
        case 5:
          locale = new Locale(language.substring(0, 2), language.substring(3, 5));
          break;
        case 10:
          // fr_FR_EURO
          locale = new Locale(language.substring(0, 2), language.substring(3, 5), language.substring(6));
          break;
        default:
          locale = Locale.getDefault();
          break;
      }
    }
  }


  /**
   * Gets the language attribute of the User object
   *
   * @return The language value
   */
  public String getLanguage() {
    return language;
  }


  /**
   * Gets the id attribute of the User object
   *
   * @return The id value
   */
  public int getId() {
    return id;
  }


  /**
   * Sets the locale attribute of the User object
   *
   * @param tmp The new locale value
   */
  public void setLocale(Locale tmp) {
    this.locale = tmp;
  }


  /**
   * Gets the locale attribute of the User object
   *
   * @return The locale value
   */
  public Locale getLocale() {
    return locale;
  }


  /**
   * Gets the browserType attribute of the User object
   *
   * @return The browserType value
   */
  public String getBrowserType() {
    return browserType;
  }


  /**
   * Gets the cssFile attribute of the User object
   *
   * @return The cssFile value
   */
  public String getCssFile() {
    return cssFile;
  }


  /**
   * Gets the template attribute of the User object
   *
   * @return The template value
   */
  public String getTemplate() {
    return template;
  }


  /**
   * Gets the idRange attribute of the User object
   *
   * @return The idRange value
   */
  public String getIdRange() {
    return idRange;
  }


  /**
   * Gets the firstName attribute of the User object
   *
   * @return The firstName value
   */
  public String getFirstName() {
    if (StringUtils.hasText(firstName) && firstName.length() > 0) {
      return String.valueOf(firstName.charAt(0)).toUpperCase() + (firstName.length() > 1 ? firstName.substring(1) : "");
    }
    return firstName;
  }


  /**
   * Gets the lastName attribute of the User object
   *
   * @return The lastName value
   */
  public String getLastName() {
    return lastName;
  }


  /**
   * Gets the nameFirstLast attribute of the User object
   *
   * @return The nameFirstLast value
   */
  public String getNameFirstLast() {
    String tmp = "";
    if (StringUtils.hasText(firstName)) {
      tmp = getFirstName();
    }
    if (lastName != null) {
      tmp += " " + getLastName();
    }
    return (tmp.trim());
  }


  public String getNameFirstLastInitial() {
    StringBuffer name = new StringBuffer("");
    if (StringUtils.hasText(firstName)) {
      name.append(getFirstName());
    }
    if (lastName != null) {
      if (name.length() > 0) {
        name.append(" ");
      }
      if (lastName.length() > 0) {
        name.append(lastName.substring(0, 1).toUpperCase()).append(".");
      }
    }
    return name.toString();
  }


  /**
   * Gets the nameLastFirst attribute of the User object
   *
   * @return The nameLastFirst value
   */
  public String getNameLastFirst() {
    String tmp = "";
    if (lastName != null) {
      tmp = lastName;
      if (firstName != null) {
        tmp += ", ";
      }
    }
    if (firstName != null) {
      tmp += firstName;
    }
    return (tmp.trim());
  }


  /**
   * Gets the company attribute of the User object
   *
   * @return The company value
   */
  public String getCompany() {
    return company;
  }


  /**
   * Gets the email attribute of the User object
   *
   * @return The email value
   */
  public String getEmail() {
    return email;
  }


  public String getEmailSubstring() {
    if (email != null && email.indexOf("@") > 0) {
      return email.substring(email.lastIndexOf("@") + 1);
    }
    return email;
  }


  /**
   * Gets the username attribute of the User object
   *
   * @return The username value
   */
  public String getUsername() {
    return username;
  }


  /**
   * Gets the password attribute of the User object
   *
   * @return The password value
   */
  public String getPassword() {
    return password;
  }


  /**
   * Gets the temporaryPassword attribute of the User object
   *
   * @return The temporaryPassword value
   */
  public String getTemporaryPassword() {
    return temporaryPassword;
  }


  /**
   * Gets the groupId attribute of the User object
   *
   * @return The groupId value
   */
  public int getGroupId() {
    return groupId;
  }


  /**
   * Gets the departmentId attribute of the User object
   *
   * @return The departmentId value
   */
  public int getDepartmentId() {
    return departmentId;
  }


  /**
   * Gets the accessAdmin attribute of the User object
   *
   * @return The accessAdmin value
   */
  public boolean getAccessAdmin() {
    return accessAdmin;
  }


  /**
   * Gets the accessInvite attribute of the User object
   *
   * @return The accessInvite value
   */
  public boolean getAccessInvite() {
    return accessInvite;
  }


  /**
   * Gets the accessUserSettings attribute of the User object
   *
   * @return The accessUserSettings value
   */
  public boolean getAccessUserSettings() {
    return accessUserSettings;
  }


  /**
   * Gets the accessGuestProjects attribute of the User object
   *
   * @return The accessGuestProjects value
   */
  public boolean getAccessGuestProjects() {
    return accessGuestProjects;
  }


  /**
   * Gets the startPage attribute of the User object
   *
   * @return The startPage value
   */
  public int getStartPage() {
    return startPage;
  }


  /**
   * Gets the enabled attribute of the User object
   *
   * @return The enabled value
   */
  public boolean getEnabled() {
    return enabled;
  }


  /**
   * Gets the terms attribute of the User object
   *
   * @return The terms value
   */
  public boolean getTerms() {
    return terms;
  }


  /**
   * Gets the enteredBy attribute of the User object
   *
   * @return The enteredBy value
   */
  public int getEnteredBy() {
    return enteredBy;
  }


  /**
   * Gets the entered attribute of the User object
   *
   * @return The entered value
   */
  public Timestamp getEntered() {
    return entered;
  }


  /**
   * Gets the modifiedBy attribute of the User object
   *
   * @return The modifiedBy value
   */
  public int getModifiedBy() {
    return modifiedBy;
  }


  /**
   * Gets the modified attribute of the User object
   *
   * @return The modified value
   */
  public Timestamp getModified() {
    return modified;
  }


  /**
   * Gets the department attribute of the User object
   *
   * @return The department value
   */
  public String getDepartment() {
    return department;
  }


  /**
   * Gets the lastLogin attribute of the User object
   *
   * @return The lastLogin value
   */
  public Timestamp getLastLogin() {
    return lastLogin;
  }


  /**
   * Gets the expiration attribute of the User object
   *
   * @return The expiration value
   */
  public Timestamp getExpiration() {
    return expiration;
  }


  /**
   * Gets the registered attribute of the User object
   *
   * @return The registered value
   */
  public boolean getRegistered() {
    return registered;
  }


  /**
   * Gets the accountSize attribute of the User object
   *
   * @return The accountSize value
   */
  public int getAccountSize() {
    return accountSize;
  }


  /**
   * Gets the accountSizeInMB attribute of the User object
   *
   * @return The accountSizeInMB value
   */
  public int getAccountSizeInMB() {
    return accountSize;
  }


  /**
   * Gets the accountSizeInBytes attribute of the User object
   *
   * @return The accountSizeInBytes value
   */
  public int getAccountSizeInBytes() {
    return (accountSize * 1024 * 1024);
  }


  /**
   * Gets the currentAccountSize attribute of the User object
   *
   * @return The currentAccountSize value
   */
  public long getCurrentAccountSize() {
    return currentAccountSize;
  }


  /**
   * Gets the currentAccountSizeInBytes attribute of the User object
   *
   * @return The currentAccountSizeInBytes value
   */
  public long getCurrentAccountSizeInBytes() {
    return currentAccountSize;
  }


  /**
   * Gets the currentAccountSizeInMB attribute of the User object
   *
   * @return The currentAccountSizeInMB value
   */
  public long getCurrentAccountSizeInMB() {
    return currentAccountSize / 1024 / 1024;
  }


  public String getSessionId() {
    return sessionId;
  }


  public void setSessionId(String sessionId) {
    this.sessionId = sessionId;
  }


  public Stack getRecentProjects() {
    return recentProjects;
  }

  public void setRecentProjects(Stack<Integer> recentProjects) {
    this.recentProjects = recentProjects;
  }


  public boolean getAccessViewAllContacts() {
    return accessViewAllContacts;
  }


  public void setAccessViewAllContacts(boolean accessViewAllContacts) {
    this.accessViewAllContacts = accessViewAllContacts;
  }


  public void setAccessViewAllContacts(String tmp) {
    this.accessViewAllContacts = DatabaseUtils.parseBoolean(tmp);
  }


  public boolean getAccessEditAllContacts() {
    return accessEditAllContacts;
  }


  public void setAccessEditAllContacts(boolean accessEditAllContacts) {
    this.accessEditAllContacts = accessEditAllContacts;
  }


  public void setAccessEditAllContacts(String tmp) {
    this.accessEditAllContacts = DatabaseUtils.parseBoolean(tmp);
  }


  public boolean getWatchForums() {
    return watchForums;
  }


  public void setWatchForums(boolean watchForums) {
    this.watchForums = watchForums;
  }


  public void setWatchForums(String watchForums) {
    this.watchForums = DatabaseUtils.parseBoolean(watchForums);
  }

  public String getNickname() {
    return nickname;
  }

  public void setNickname(String nickname) {
    this.nickname = nickname;
  }

  public int getSalutationId() {
    return salutationId;
  }

  public void setSalutationId(int salutationId) {
    this.salutationId = salutationId;
  }

  public void setSalutationId(String tmp) {
    this.salutationId = Integer.parseInt(tmp);
  }

  public Project getProfileProject() {
    if (profileProjectId > -1) {
      return ProjectUtils.loadProject(profileProjectId);
    }
    return null;
  }

  public int getProfileProjectId() {
    return profileProjectId;
  }

  public void setProfileProjectId(int profileProjectId) {
    this.profileProjectId = profileProjectId;
  }

  public void setProfileProjectId(String tmp) {
    this.profileProjectId = Integer.parseInt(tmp);
  }

  public int getShowProfileTo() {
    return showProfileTo;
  }

  public void setShowProfileTo(int showProfileTo) {
    this.showProfileTo = showProfileTo;
  }

  public void setShowProfileTo(String tmp) {
    this.showProfileTo = Integer.parseInt(tmp);
  }

  public int getShowFullNameTo() {
    return showFullNameTo;
  }

  public void setShowFullNameTo(int showFullNameTo) {
    this.showFullNameTo = showFullNameTo;
  }

  public void setShowFullNameTo(String tmp) {
    this.showFullNameTo = Integer.parseInt(tmp);
  }

  public int getShowEmailTo() {
    return showEmailTo;
  }

  public void setShowEmailTo(int showEmailTo) {
    this.showEmailTo = showEmailTo;
  }

  public void setShowEmailTo(String tmp) {
    this.showEmailTo = Integer.parseInt(tmp);
  }

  public int getShowGenderTo() {
    return showGenderTo;
  }

  public void setShowGenderTo(int showGenderTo) {
    this.showGenderTo = showGenderTo;
  }

  public void setShowGenderTo(String tmp) {
    this.showGenderTo = Integer.parseInt(tmp);
  }

  public int getShowLocationTo() {
    return showLocationTo;
  }

  public void setShowLocationTo(int showLocationTo) {
    this.showLocationTo = showLocationTo;
  }

  public void setShowLocationTo(String tmp) {
    this.showLocationTo = Integer.parseInt(tmp);
  }

  public int getShowCompanyTo() {
    return showCompanyTo;
  }

  public void setShowCompanyTo(int showCompanyTo) {
    this.showCompanyTo = showCompanyTo;
  }

  public void setShowCompanyTo(String tmp) {
    this.showCompanyTo = Integer.parseInt(tmp);
  }

  /**
   * @return the points
   */
  public int getPoints() {
    return points;
  }


  /**
   * @param points the points to set
   */
  public void setPoints(int points) {
    this.points = points;
  }

  public void setPoints(String points) {
    this.points = Integer.parseInt(points);
  }

  public boolean isApiRestore() {
    return apiRestore;
  }

  public void setApiRestore(boolean apiRestore) {
    this.apiRestore = apiRestore;
  }

  /**
   * Description of the Method
   *
   * @param db       Description of the Parameter
   * @param context  Description of the Parameter
   * @param password Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  public boolean updateLogin(Connection db, ActionContext context, String password) throws SQLException {
    // Update the last login
    PreparedStatement pst = db.prepareStatement(
        "UPDATE users " +
            "SET last_login = CURRENT_TIMESTAMP " +
            "WHERE user_id = ? ");
    pst.setInt(1, id);
    pst.execute();
    pst.close();
    try {
      // Update the permanent password if using the temporary password
      pst = db.prepareStatement(
          "UPDATE users " +
              "SET password = ? " +
              "WHERE user_id = ? " +
              "AND password <> ? AND temporary_password = ? ");
      String passwordHash = PasswordHash.encrypt(password);
      pst.setString(1, passwordHash);
      pst.setInt(2, id);
      pst.setString(3, passwordHash);
      pst.setString(4, passwordHash);
      pst.execute();
      pst.close();
      // Update the webdav passwords
      pst = db.prepareStatement(
          "UPDATE users " +
              "SET webdav_password = ? " +
              "WHERE user_id = ? " +
              "AND (webdav_password IS NULL OR webdav_password <> ?) ");
      String webdav = PasswordHash.encrypt(username + ":" + WebdavServlet.USER_REALM + ":" + password);
      pst.setString(1, webdav);
      pst.setInt(2, id);
      pst.setString(3, webdav);
      pst.execute();
      pst.close();
      // Update the htpasswd
      ApplicationPrefs prefs = (ApplicationPrefs) context.getServletContext().getAttribute("applicationPrefs");
      if ("true".equals(prefs.get("HTPASSWD"))) {
        pst = db.prepareStatement(
            "UPDATE users " +
                "SET htpasswd = ?, htpasswd_date = CURRENT_TIMESTAMP " +
                "WHERE user_id = ? " +
                "AND (htpasswd IS NULL OR htpasswd <> ?) ");
        String htpasswd = PasswordHash.htpasswd(username, password);
        pst.setString(1, htpasswd);
        pst.setInt(2, id);
        pst.setString(3, htpasswd);
        pst.execute();
        pst.close();
      }
    } catch (Exception e) {
      // This column might not exist yet so catch the fail
    }
    // Load the current last login
    pst = db.prepareStatement(
        "SELECT last_login " +
            "FROM users " +
            "WHERE user_id = ? ");
    pst.setInt(1, id);
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      lastLogin = rs.getTimestamp("last_login");
    }
    rs.close();
    pst.close();
    //Record the IP
    String ipAddress = context.getIpAddress();
    if (System.getProperty("DEBUG") != null) {
      System.out.println("User-> Logging user IP: " + ipAddress);
    }
    pst = db.prepareStatement(
        "INSERT INTO user_log (user_id, ip_address, browser) VALUES (?, ?, ?)");
    pst.setInt(1, id);
    pst.setString(2, ipAddress);
    pst.setString(3, context.getBrowser());
    pst.execute();
    pst.close();
    CacheUtils.invalidateValue(Constants.SYSTEM_USER_CACHE, id);
    return true;
  }

  /**
   * Basic user insert method, used by API
   *
   * @param db The database connection
   * @return true if the record was added successfully
   * @throws SQLException Database exception
   */
  public boolean insert(Connection db) throws SQLException {
    return insert(db, null, null);
  }

  /**
   * Description of the Method
   *
   * @param db        The database connection
   * @param ipAddress The ip address requesting the user to be added
   * @param prefs     The application prefs
   * @return true if the record was added successfully
   * @throws SQLException Database exception
   */
  public boolean insert(Connection db, String ipAddress, ApplicationPrefs prefs) throws SQLException {
    boolean commit = db.getAutoCommit();
    try {
      if (commit) {
        db.setAutoCommit(false);
      }
      // Insert the user
      PreparedStatement pst = db.prepareStatement(
          "INSERT INTO users " +
              "(instance_id, group_id, department_id, first_name, last_name, username, password, temporary_password, " +
              "company, email, enteredby, modifiedby, enabled, start_page, access_personal, access_enterprise, " +
              "access_admin, access_inbox, access_resources, expiration, registered, " +
              "account_size, access_invite, access_add_projects, terms, timezone, currency, language" +
              (entered != null ? ", entered" : "") +
              (modified != null ? ", modified" : "") +
              ") " +
              "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?" +
              (entered != null ? ", ?" : "") +
              (modified != null ? ", ?" : "") +
              ") ");
      int i = 0;
      DatabaseUtils.setInt(pst, ++i, instanceId);
      pst.setInt(++i, groupId);
      pst.setInt(++i, departmentId);
      pst.setString(++i, firstName);
      pst.setString(++i, lastName);
      pst.setString(++i, username);
      pst.setString(++i, password);
      pst.setString(++i, temporaryPassword);
      pst.setString(++i, company);
      pst.setString(++i, email);
      pst.setInt(++i, enteredBy);
      pst.setInt(++i, modifiedBy);
      pst.setBoolean(++i, enabled);
      pst.setInt(++i, startPage);
      pst.setBoolean(++i, true);
      pst.setBoolean(++i, true);
      pst.setBoolean(++i, accessAdmin);
      pst.setBoolean(++i, false);
      pst.setBoolean(++i, false);
      DatabaseUtils.setTimestamp(pst, ++i, expiration);
      pst.setBoolean(++i, registered);
      DatabaseUtils.setInt(pst, ++i, accountSize);
      pst.setBoolean(++i, accessInvite);
      pst.setBoolean(++i, accessAddProjects);
      pst.setBoolean(++i, terms);
      pst.setString(++i, timeZone);
      pst.setString(++i, currency);
      pst.setString(++i, language);
      if (entered != null) {
        pst.setTimestamp(++i, entered);
      }
      if (modified != null) {
        pst.setTimestamp(++i, modified);
      }
      pst.execute();
      pst.close();
      id = DatabaseUtils.getCurrVal(db, "users_user_id_seq", -1);
      // Record the IP
      if (ipAddress != null) {
        pst = db.prepareStatement(
            "INSERT INTO user_log (user_id, ip_address) VALUES (?, ?)");
        pst.setInt(1, id);
        pst.setString(2, ipAddress);
        pst.execute();
        pst.close();
      }
      if (!isApiRestore()) {
        // Insert a corresponding user profile project
        Project project = UserUtils.addUserProfile(db, this, prefs);
        profileProjectId = project.getId();
      }
      if (commit) {
        db.commit();
      }
    } catch (Exception e) {
      e.printStackTrace(System.out);
      if (commit) {
        db.rollback();
      }
      throw new SQLException(e.getMessage());
    } finally {
      if (commit) {
        db.setAutoCommit(true);
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
  public int update(Connection db) throws SQLException {
    // Update the user
    PreparedStatement pst = db.prepareStatement(
        "UPDATE users " +
            "SET first_name = ?, last_name = ?, username = ?, " +
            (password != null ? "password = ?, " : "") +
            "company = ?, email = ?, enabled = ?, access_admin = ?, expiration = ?, " +
            "access_invite = ?, " +
            "account_size = ?, access_add_projects = ?, " +
            "access_contacts_view_all = ?, access_contacts_edit_all = ?, watch_forums = ?, " +
            "modified = " + DatabaseUtils.getCurrentTimestamp(db) + ", modifiedby = ? " +
            "WHERE user_id = ? ");
    int i = 0;
    pst.setString(++i, firstName);
    pst.setString(++i, lastName);
    pst.setString(++i, username);
    if (password != null) {
      pst.setString(++i, password);
    }
    pst.setString(++i, company);
    pst.setString(++i, email);
    pst.setBoolean(++i, enabled);
    pst.setBoolean(++i, accessAdmin);
    DatabaseUtils.setTimestamp(pst, ++i, expiration);
    pst.setBoolean(++i, accessInvite);
    DatabaseUtils.setInt(pst, ++i, accountSize);
    pst.setBoolean(++i, accessAddProjects);
    pst.setBoolean(++i, accessViewAllContacts);
    pst.setBoolean(++i, accessEditAllContacts);
    pst.setBoolean(++i, watchForums);
    pst.setInt(++i, modifiedBy);
    pst.setInt(++i, id);
    int count = pst.executeUpdate();
    pst.close();
    CacheUtils.invalidateValue(Constants.SYSTEM_USER_CACHE, id);
    return count;
  }


  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  public int delete(Connection db) throws SQLException {
    int count = 0;
    boolean commit = db.getAutoCommit();
    try {
      if (commit) {
        db.setAutoCommit(false);
      }
      // Delete the user's profile
      if (profileProjectId > -1) {
        Project project = new Project(db, profileProjectId);
        project.delete(db, null);
      }
      // Delete related data
      PreparedStatement pst = db.prepareStatement("DELETE FROM user_log where user_id = ?");
      pst.setInt(1, id);
      pst.execute();
      pst.close();
      // Delete the user
      pst = db.prepareStatement("DELETE FROM users where user_id = ?");
      pst.setInt(1, id);
      count = pst.executeUpdate();
      pst.close();
      if (commit) {
        db.commit();
      }
      CacheUtils.invalidateValue(Constants.SYSTEM_USER_CACHE, id);
      //@todo enable
      //CacheUtils.invalidateValue(Constants.SYSTEM_USER_PROJECT_ROLE_CACHE, id);
    } catch (SQLException e) {
      if (commit) {
        db.rollback();
      }
      throw new SQLException(e.getMessage());
    } finally {
      if (commit) {
        db.setAutoCommit(true);
      }
    }
    return count;
  }


  /**
   * Gets the idByEmailAddress attribute of the User class
   *
   * @param db    Description of the Parameter
   * @param email Description of the Parameter
   * @return The idByEmailAddress value
   * @throws SQLException Description of the Exception
   */
  public static int getIdByEmailAddress(Connection db, String email) throws SQLException {
    int userId = -1;
    PreparedStatement pst = db.prepareStatement(
        "SELECT user_id " +
            "FROM users " +
            "WHERE lower(email) = ? ");
    pst.setString(1, email.toLowerCase());
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      userId = rs.getInt("user_id");
    }
    rs.close();
    pst.close();
    return userId;
  }


  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @param db      Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  public boolean resetPassword(ActionContext context, Connection db) throws SQLException {
    String temporaryPassword = String.valueOf(StringUtils.rand(100000, 999999));
    String encryptedPassword = PasswordHash.encrypt(temporaryPassword);
    ApplicationPrefs prefs = (ApplicationPrefs) context.getServletContext().getAttribute("applicationPrefs");
    //send the confirmation message
    SMTPMessage message = SMTPMessageFactory.createSMTPMessageInstance(prefs.getPrefs());
    message.setTo(email);
    message.setFrom(prefs.get("EMAILADDRESS"));
    message.setSubject("Your Password");
    message.setBody(
        "Hello " + firstName + "," + lf +
            lf +
            "Your temporary password is:" + lf +
            lf +
            temporaryPassword + lf +
            lf +
            "You may use this password to log into the site." + lf +
            lf +
            "After logging in, you can change your password by choosing Personal Settings from the " +
            "menu options.");
    int result = message.send();
    if (result > 0) {
      errors.put("emailError", message.getErrorMsg());
      return false;
    }
    //updatePassword
    PreparedStatement pst = db.prepareStatement(
        "UPDATE users " +
            "SET temporary_password = ? " +
            "WHERE user_id = ? ");
    pst.setString(1, encryptedPassword);
    pst.setInt(2, id);
    pst.executeUpdate();
    pst.close();
    CacheUtils.invalidateValue(Constants.SYSTEM_USER_CACHE, id);
    return true;
  }


  /**
   * Gets the valid attribute of the User object
   *
   * @return The valid value
   */
  public boolean isValid() {
    if (firstName == null || "".equals(firstName)) {
      errors.put("firstNameError", "First name is required");
    }
    if (lastName == null || "".equals(lastName)) {
      errors.put("lastNameError", "Last name is required");
    }
    if (password1 != null && password2 != null) {
      if (password1.trim().length() == 0) {
        errors.put("password1Error", "Password is required");
      }
      if (!password1.equals(password2)) {
        errors.put("password2Error", "Passwords do not match");
      }
    }
    return !hasErrors();
  }


  /**
   * Description of the Method
   */
  public void validateEmail() {
    if (email == null || "".equals(email)) {
      errors.put("emailError", "Email is required");
    }
  }


  /**
   * Description of the Method
   */
  public void validateCompany() {
    if (company == null || "".equals(company)) {
      errors.put("companyError", "Organization is required");
    }
  }


  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  public int updateContact(Connection db) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "UPDATE users " +
            "SET first_name = ?, last_name = ?, company = ? " +
            "WHERE user_id = ?");
    int i = 0;
    pst.setString(++i, firstName);
    pst.setString(++i, lastName);
    pst.setString(++i, company);
    pst.setInt(++i, id);
    int count = pst.executeUpdate();
    pst.close();
    CacheUtils.invalidateValue(Constants.SYSTEM_USER_CACHE, id);
    return count;
  }


  /**
   * Description of the Method
   *
   * @param db       Description of the Parameter
   * @param userId   Description of the Parameter
   * @param timeZone Description of the Parameter
   * @param currency Description of the Parameter
   * @param language Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  public static int updateLocation(Connection db, int userId, String timeZone, String currency, String language) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "UPDATE users " +
            "SET timezone = ?, currency = ?, language = ? " +
            "WHERE user_id = ?");
    int i = 0;
    pst.setString(++i, timeZone);
    pst.setString(++i, currency);
    pst.setString(++i, language);
    pst.setInt(++i, userId);
    int count = pst.executeUpdate();
    pst.close();
    CacheUtils.invalidateValue(Constants.SYSTEM_USER_CACHE, userId);
    return count;
  }

  public static int incrementPoints(Connection db, int userId, int points) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "UPDATE users " +
            "SET points = points + ? " +
            "WHERE user_id = ?");
    int i = 0;
    pst.setInt(++i, points);
    pst.setInt(++i, userId);
    int count = pst.executeUpdate();
    pst.close();
    CacheUtils.invalidateValue(Constants.SYSTEM_USER_CACHE, userId);
    return count;
  }

  public static int resetPoints(Connection db, int userId) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "UPDATE users " +
            "SET points = ? " +
            "WHERE user_id = ?");
    int i = 0;
    pst.setInt(++i, 0);
    pst.setInt(++i, userId);
    int count = pst.executeUpdate();
    pst.close();
    CacheUtils.invalidateValue(Constants.SYSTEM_USER_CACHE, userId);
    return count;
  }

  /**
   * Gets the withinAccountSize attribute of the User object
   *
   * @return The withinAccountSize value
   */
  public boolean isWithinAccountSize() {
    return (this.getCurrentAccountSizeInBytes() < this.getAccountSizeInBytes());
  }


  /**
   * The following fields depend on a timezone preference
   *
   * @return The timeZoneParams value
   */
  public static ArrayList<String> getTimeZoneParams() {
    ArrayList<String> thisList = new ArrayList<String>();
    thisList.add("expiration");
    return thisList;
  }


  /**
   * Gets the loggedIn attribute of the User object
   *
   * @return The loggedIn value
   */
  public boolean isLoggedIn() {
    return id > 0;
  }


  /**
   * Gets the emailAddressById attribute of the User class
   *
   * @param db Description of the Parameter
   * @param id Description of the Parameter
   * @return The emailAddressById value
   * @throws SQLException Description of the Exception
   */
  public static String getEmailAddressById(Connection db, int id) throws SQLException {
    String email = null;
    PreparedStatement pst = db.prepareStatement(
        "SELECT email " +
            "FROM users " +
            "WHERE user_id = ? ");
    pst.setInt(1, id);
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      email = rs.getString("email");
    }
    rs.close();
    pst.close();
    return email;
  }


  public void queryRecentlyAccessedProjects(Connection db) throws SQLException {
    // Make sure list is empty
    recentProjects.clear();
    // Use a pagedList to get the last 10 records
    PagedListInfo pagedListInfo = new PagedListInfo();
    pagedListInfo.setItemsPerPage(10);
    pagedListInfo.setDefaultSort("last_accessed", "desc");
    // Query the list
    StringBuffer sqlSelect = new StringBuffer();
    pagedListInfo.appendSqlSelectHead(db, sqlSelect);
    sqlSelect.append(
        "project_id " +
            "FROM project_team " +
            "WHERE user_id = ? " +
            "AND last_accessed IS NOT NULL ");
    pagedListInfo.appendSqlTail(db, sqlSelect);
    PreparedStatement pst = db.prepareStatement(sqlSelect.toString());
    pst.setInt(1, id);
    ResultSet rs = pst.executeQuery();
    while (rs.next()) {
      int projectId = rs.getInt("project_id");
      recentProjects.add(projectId);
    }
    rs.close();
    pst.close();
  }


  public void addRecentProject(Integer projectId) {
    synchronized (this) {
      int i = recentProjects.search(projectId);
      if (i > 1) {
        recentProjects.remove(projectId);
      }
      if (i == -1 || i > 1) {
        recentProjects.push(projectId);
      }
      while (recentProjects.size() > 10) {
        recentProjects.remove(10);
      }
    }
  }


  public void updateWatch(Connection db, String feature, String value) throws SQLException {
    String field = null;
    if ("forums".equals(feature)) {
      field = "watch_forums";
    }
    if (field != null) {
      PreparedStatement pst = db.prepareStatement(
          "UPDATE users SET " + field + " = ? WHERE user_id = ?");
      pst.setBoolean(1, DatabaseUtils.parseBoolean(value));
      pst.setInt(2, id);
      pst.executeUpdate();
      pst.close();
      // Update the user's session
      if ("forums".equals(feature)) {
        watchForums = DatabaseUtils.parseBoolean(value);
      }
      CacheUtils.invalidateValue(Constants.SYSTEM_USER_CACHE, id);
    }
  }
}


