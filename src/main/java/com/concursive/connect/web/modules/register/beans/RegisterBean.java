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

package com.concursive.connect.web.modules.register.beans;

import com.concursive.commons.codec.PasswordHash;
import com.concursive.commons.db.DatabaseUtils;
import com.concursive.commons.email.EmailUtils;
import com.concursive.commons.text.StringUtils;
import com.concursive.commons.web.mvc.beans.GenericBean;
import com.concursive.connect.Constants;
import com.concursive.connect.cache.utils.CacheUtils;
import com.concursive.connect.config.ApplicationPrefs;
import com.concursive.connect.web.modules.common.social.geotagging.utils.LocationBean;
import com.concursive.connect.web.modules.common.social.geotagging.utils.LocationUtils;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import nl.captcha.Captcha;

import javax.portlet.ActionRequest;
import javax.portlet.PortletSession;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * HTML form bean for the registration process
 *
 * @author matt rajkowski
 * @version $Id$
 * @created September 30, 2003
 */
public class RegisterBean extends GenericBean {

  public final static String lf = System.getProperty("line.separator");

  // Bean properties
  private int instanceId = -1;

  // Form properties
  private String city = null;
  private String state = null;
  private String country = null;
  private String requestIteams = null;
  private String email = null;
  private String nameFirst = null;
  private String nameLast = null;
  private String organization = null;
  private String password = null;
  private String encryptedPassword = null;
  private boolean terms = false;
  private String captcha = null;
  private String postalCode = null;
  private String data = null;

  // Resulting user
  private User user = null;

  /**
   * Constructor for the RegisterBean object
   */
  public RegisterBean() {
  }

  public int getInstanceId() {
    return instanceId;
  }

  public void setInstanceId(int instanceId) {
    this.instanceId = instanceId;
  }

  /**
   * Sets the request iteams the RegisterBean object
   *
   * @param tmp the request iteams value
   */

  public void setRequestIteams(String tmp) {
    this.requestIteams = tmp;
  }

  /**
   * Sets the country attribute of the RegisterBean object
   *
   * @param tmp The new country value
   */

  public void setCountry(String tmp) {
    this.country = tmp;
  }


  /**
   * Sets the email attribute of the RegisterBean object
   *
   * @param tmp The new email value
   */
  public void setEmail(String tmp) {
    this.email = tmp;
  }


  /**
   * Sets the nameFirst attribute of the RegisterBean object
   *
   * @param tmp The new nameFirst value
   */
  public void setNameFirst(String tmp) {
    this.nameFirst = tmp;
  }


  /**
   * Sets the nameLast attribute of the RegisterBean object
   *
   * @param tmp The new nameLast value
   */
  public void setNameLast(String tmp) {
    this.nameLast = tmp;
  }


  /**
   * Sets the organization attribute of the RegisterBean object
   *
   * @param tmp The new organization value
   */
  public void setOrganization(String tmp) {
    this.organization = tmp;
  }


  /**
   * Sets the password attribute of the RegisterBean object
   *
   * @param tmp The new password value
   */
  public void setPassword(String tmp) {
    this.password = tmp;
  }


  /**
   * Sets the encryptedPassword attribute of the RegisterBean object
   *
   * @param tmp The new encryptedPassword value
   */
  public void setEncryptedPassword(String tmp) {
    this.encryptedPassword = tmp;
  }


  /**
   * Sets the terms attribute of the RegisterBean object
   *
   * @param tmp The new terms value
   */
  public void setTerms(boolean tmp) {
    this.terms = tmp;
  }


  /**
   * Sets the terms attribute of the RegisterBean object
   *
   * @param tmp The new terms value
   */
  public void setTerms(String tmp) {
    this.terms = (tmp.equals("accept") || DatabaseUtils.parseBoolean(tmp));
  }


  /**
   * Sets the user attribute of the RegisterBean object
   *
   * @param tmp The new user value
   */
  public void setUser(User tmp) {
    this.user = tmp;
  }


  /**
   * Gets the country attribute of the RegisterBean object
   *
   * @return The country value
   */
  public String getCountry() {
    return country;
  }


  /**
   * Gets the request iteams attribute of the RegisterBean object
   *
   * @return The request iteams value
   */
  public String getRequestIteams() {
    return requestIteams;
  }


  /**
   * Gets the email attribute of the RegisterBean object
   *
   * @return The email value
   */
  public String getEmail() {
    return email;
  }


  /**
   * Gets the nameFirst attribute of the RegisterBean object
   *
   * @return The nameFirst value
   */
  public String getNameFirst() {
    return nameFirst;
  }


  /**
   * Gets the nameLast attribute of the RegisterBean object
   *
   * @return The nameLast value
   */
  public String getNameLast() {
    return nameLast;
  }


  /**
   * Gets the organization attribute of the RegisterBean object
   *
   * @return The organization value
   */
  public String getOrganization() {
    return organization;
  }


  /**
   * Gets the password attribute of the RegisterBean object
   *
   * @return The password value
   */
  public String getPassword() {
    return password;
  }


  /**
   * Gets the encryptedPassword attribute of the RegisterBean object
   *
   * @return The encryptedPassword value
   */
  public String getEncryptedPassword() {
    return encryptedPassword;
  }


  /**
   * Gets the terms attribute of the RegisterBean object
   *
   * @return The terms value
   */
  public boolean getTerms() {
    return terms;
  }


  public String getCaptcha() {
    return captcha;
  }

  public void setCaptcha(String captcha) {
    this.captcha = captcha;
  }

  public String getPostalCode() {
    return postalCode;
  }

  public void setPostalCode(String postalCode) {
    this.postalCode = postalCode;
  }

  public String getData() {
    return data;
  }

  public void setData(String data) {
    this.data = data;
  }

  /**
   * Gets the user attribute of the RegisterBean object
   *
   * @return The user value
   */
  public User getUser() {
    return user;
  }

  public Project getProject() {
    if (user != null && user.getProfileProjectId() > -1) {
      return ProjectUtils.loadProject(user.getProfileProjectId());
    }
    return null;
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


  /**
   * @return the state
   */
  public String getState() {
    return state;
  }


  /**
   * @param state the state to set
   */
  public void setState(String state) {
    this.state = state;
  }


  /**
   * Gets the valid attribute of the RegisterBean object
   *
   * @param session http session for captcha check
   * @return The valid value
   */
  public boolean isValid(PortletSession session) {
    String captchaPassed = (String) session.getAttribute("TE-REGISTER-CAPTCHA-PASSED");
    if (!"passed".equals(captchaPassed)) {
      Captcha captchaValue = (Captcha) session.getAttribute(Captcha.NAME);
      session.removeAttribute(Captcha.NAME);
      if (captchaValue == null) {
        System.out.println("RegisterBean-> Could not find captcha session variable for comparison to user input");
      }
      if (captchaValue == null || captcha == null ||
          !captchaValue.isCorrect(captcha)) {
        errors.put("captchaError", "Text did not match image");
      } else {
        session.setAttribute("TE-REGISTER-CAPTCHA-PASSED", "passed");
      }
    }
    if (email == null || "".equals(email.trim())) {
      errors.put("emailError", "Required field");
    } else {
      if (!EmailUtils.checkEmail(email)) {
        errors.put("emailError", "Check the email address entered");
      }
    }
    if (nameFirst == null || "".equals(nameFirst.trim())) {
      errors.put("nameFirstError", "Required field");
    }
    if (nameLast == null || "".equals(nameLast.trim())) {
      errors.put("nameLastError", "Required field");
    }
    if (!StringUtils.hasText(country)) {
      errors.put("countryError", "Required field");
    }
    if (!StringUtils.hasText(postalCode)) {
      errors.put("postalCodeError", "Required field");
    } else {
      if ("UNITED STATES".equals(country)) {
        LocationBean location = LocationUtils.findLocationByZipCode(postalCode);
        if (location == null) {
          errors.put("postalCodeError", "Could not locate this postal code");
        }
      }
    }
    return (!hasErrors());
  }


  /**
   * Gets the alreadyRegistered attribute of the RegisterBean object
   *
   * @param db Description of the Parameter
   * @return The alreadyRegistered value
   * @throws SQLException Description of the Exception
   */
  public boolean isAlreadyRegistered(Connection db) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "SELECT count(*) AS records " +
            "FROM users " +
            "WHERE lower(email) = ? " +
            "AND registered = ?");
    pst.setString(1, email.toLowerCase());
    pst.setBoolean(2, true);
    ResultSet rs = pst.executeQuery();
    rs.next();
    int count = rs.getInt("records");
    rs.close();
    pst.close();
    return count >= 1;
  }


  /**
   * Description of the Method
   *
   * @param db      Description of the Parameter
   * @param prefs   Description of the Parameter
   * @param request Description of the Parameter
   * @param userIp  The user's ip address in which the registration was made from
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  public boolean save(Connection db, ApplicationPrefs prefs, ActionRequest request, String userIp) throws SQLException {
    // Remove the captcha's session value
    request.getPortletSession().removeAttribute("TE-REGISTER-CAPTCHA-PASSED");
    // Determine the user's password
    password = String.valueOf(StringUtils.rand(100000, 999999));
    encryptedPassword = PasswordHash.encrypt(password);
    // NOTE: A confirmation message will be sent by the workflow
    boolean commit = db.getAutoCommit();
    try {
      if (commit) {
        db.setAutoCommit(false);
      }
      // Store the user in the database
      user = new User();
      user.setId(User.getIdByEmailAddress(db, email));
      user.setInstanceId(instanceId);
      user.setGroupId(1);
      user.setDepartmentId(1);
      user.setFirstName(nameFirst);
      user.setLastName(nameLast);
      user.setCompany(organization);
      user.setEmail(email);
      user.setUsername(email);
      user.setPassword(encryptedPassword);
      user.setEnteredBy(0);
      user.setModifiedBy(0);
      user.setEnabled(true);
      user.setStartPage(1);
      user.setRegistered(true);
      user.setTerms(terms);
      user.setAccountSize(prefs.get("ACCOUNT.SIZE"));
      user.setAccessAddProjects(prefs.get("START_PROJECTS"));
      user.setCity(city);
      user.setState(state);
      user.setCountry(country);
      user.setPostalCode(postalCode);
      if (user.getId() == -1) {
        // This is a new account, so insert
        user.insert(db, userIp, prefs);
      } else {
        // Else set the status as registered and update the info
        updateRegisteredStatus(db, user);
      }
      if (commit) {
        db.commit();
      }
    } catch (Exception e) {
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
   * @param db   Description of the Parameter
   * @param user Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  private static boolean updateRegisteredStatus(Connection db, User user) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "UPDATE users " +
            "SET first_name = ?, last_name = ?, password = ?, " +
            "company = ?, registered = ?, enabled = ?, terms = ? " +
            "WHERE user_id = ? ");
    int i = 0;
    pst.setString(++i, user.getFirstName());
    pst.setString(++i, user.getLastName());
    pst.setString(++i, user.getPassword());
    pst.setString(++i, user.getCompany());
    pst.setBoolean(++i, true);
    pst.setBoolean(++i, true);
    pst.setBoolean(++i, user.getTerms());
    pst.setInt(++i, user.getId());
    int count = pst.executeUpdate();
    pst.close();
    CacheUtils.invalidateValue(Constants.SYSTEM_USER_CACHE, user.getId());
    return count == 1;
  }

}

