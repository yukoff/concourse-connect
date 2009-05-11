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

import com.concursive.commons.db.DatabaseUtils;
import com.concursive.connect.web.utils.CustomLookupElement;

import java.sql.*;

/**
 * Description of the AuthenticationClassesLookup
 *
 * @author Artem.Zakolodkin
 * @created Jul 19, 2007
 */
public class AuthenticationClassesLookup extends CustomLookupElement {
  /**
   *
   */
  private static final long serialVersionUID = 1L;
  private int code = -1;
  private String loginMode = null;
  private String loginAuthenticator = null;
  private String sessionValidator = null;
  private boolean enabled = false;
  private Timestamp entered = null;
  private Timestamp modified = null;

  public AuthenticationClassesLookup() {
  }

  /**
   * @param rs
   * @throws SQLException
   */
  public AuthenticationClassesLookup(ResultSet rs) throws SQLException {
    buildRecord(rs);
  }

  protected void buildRecord(ResultSet rs) throws SQLException {
    code = rs.getInt("code");
    loginMode = rs.getString("login_mode");
    loginAuthenticator = rs.getString("login_authenticator");
    sessionValidator = rs.getString("session_validator");
    enabled = rs.getBoolean("enabled");
    entered = rs.getTimestamp("entered");
    modified = rs.getTimestamp("modified");
  }

  public void queryRecord(Connection db, int id) throws SQLException {
    if (id == -1) {
      throw new SQLException("Invalid Authentication Class Lookup Code specified");
    }
    PreparedStatement pst = db.prepareStatement("SELECT lac.* " + "FROM lookup_authentication_classes lac " + "WHERE lac.code = ? ");
    pst.setInt(1, id);
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      buildRecord(rs);
    }
    rs.close();
    pst.close();
    if (this.getCode() == -1) {
      throw new SQLException("No Authentication records found...");
    }
  }

  public boolean insert(Connection db) throws SQLException {
    code = DatabaseUtils.getNextSeq(db, "lookup_authentication__code_seq", -1);
    PreparedStatement pst = db.prepareStatement(
        "INSERT INTO lookup_authentication_classes " +
            "(" + (code > -1 ? "code, " : "") + "login_mode, login_authenticator, session_validator, enabled) " +
            "VALUES (" + (code > -1 ? "?, " : "") + "?, ?, ?, ?) ");
    int i = 0;
    if (code > -1) {
      pst.setInt(++i, code);
    }
    pst.setString(++i, loginMode);
    pst.setString(++i, loginAuthenticator);
    pst.setString(++i, sessionValidator);
    pst.setBoolean(++i, enabled);
    pst.execute();
    pst.close();
    code = DatabaseUtils.getCurrVal(db, "lookup_authentication__code_seq", code);
    return true;
  }

  public int update(Connection db) throws SQLException {
    int resultCount = 0;
    PreparedStatement pst = db.prepareStatement(
        "UPDATE lookup_authentication_classes " +
            "SET login_mode = ? , login_authenticator = ?, " +
            "session_validator = ?, enabled = ?, entered = ?, modified = ? " +
            "WHERE code = ?");
    int i = 0;
    pst.setString(i++, loginMode);
    pst.setString(i++, loginAuthenticator);
    pst.setString(i++, sessionValidator);
    pst.setBoolean(i++, enabled);
    DatabaseUtils.setTimestamp(pst, ++i, entered);
    DatabaseUtils.setTimestamp(pst, ++i, modified);
    DatabaseUtils.setInt(pst, ++i, code);
    resultCount = pst.executeUpdate();
    pst.close();
    return resultCount;
  }

  public void setId(int id) {
    this.code = id;
  }

  public void setId(String id) {
    this.code = Integer.parseInt(id);
  }

  public int getId() {
    return code;
  }

  /**
   * @return the code
   */
  public int getCode() {
    return code;
  }

  /**
   * @param code the code to set
   */
  public void setCode(int code) {
    this.code = code;
  }

  public void setCode(String tmp) {
    this.code = Integer.parseInt(tmp);
  }

  /**
   * @return the loginMode
   */
  public String getLoginMode() {
    return loginMode;
  }

  /**
   * @param loginMode the loginMode to set
   */
  public void setLoginMode(String loginMode) {
    this.loginMode = loginMode;
  }

  /**
   * @return the loginAuthenticator
   */
  public String getLoginAuthenticator() {
    return loginAuthenticator;
  }

  /**
   * @param loginAuthenticator the loginAuthenticator to set
   */
  public void setLoginAuthenticator(String loginAuthenticator) {
    this.loginAuthenticator = loginAuthenticator;
  }

  /**
   * @return the sessionValidator
   */
  public String getSessionValidator() {
    return sessionValidator;
  }

  /**
   * @param sessionValidator the sessionValidator to set
   */
  public void setSessionValidator(String sessionValidator) {
    this.sessionValidator = sessionValidator;
  }

  /**
   * @return the enabled
   */
  public boolean isEnabled() {
    return enabled;
  }

  public boolean getEnabled() {
    return enabled;
  }

  /**
   * @param enabled the enabled to set
   */
  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public void setEnabled(String tmp) {
    this.enabled = DatabaseUtils.parseBoolean(tmp);
  }

  /**
   * @return the entered
   */
  public Timestamp getEntered() {
    return entered;
  }

  /**
   * @param entered the entered to set
   */
  public void setEntered(Timestamp entered) {
    this.entered = entered;
  }

  public void setEntered(String tmp) {
    this.entered = DatabaseUtils.parseTimestamp(tmp);
  }


  /**
   * @return the modified
   */
  public Timestamp getModified() {
    return modified;
  }

  /**
   * @param modified the modified to set
   */
  public void setModified(Timestamp modified) {
    this.modified = modified;
  }

  public void setModified(String tmp) {
    this.modified = DatabaseUtils.parseTimestamp(tmp);
  }
}
