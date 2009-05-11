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

package com.concursive.connect.web.modules.login.beans;

import com.concursive.commons.codec.PasswordHash;
import com.concursive.commons.web.mvc.beans.GenericBean;
import com.concursive.connect.web.webdav.servlets.WebdavServlet;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Form bean to allow user to change password
 *
 * @author matt rajkowski
 * @version $Id$
 * @created November 20, 2003
 */
public class Password extends GenericBean {
  private int userId = -1;
  private String username = null;
  private String password = null;
  private String newPassword1 = null;
  private String newPassword2 = null;


  /**
   * Sets the userId attribute of the Password object
   *
   * @param tmp The new userId value
   */
  public void setUserId(int tmp) {
    this.userId = tmp;
  }


  /**
   * Sets the userId attribute of the Password object
   *
   * @param tmp The new userId value
   */
  public void setUserId(String tmp) {
    this.userId = Integer.parseInt(tmp);
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  /**
   * Sets the password attribute of the Password object
   *
   * @param tmp The new password value
   */
  public void setPassword(String tmp) {
    this.password = tmp;
  }


  /**
   * Sets the newPassword1 attribute of the Password object
   *
   * @param tmp The new newPassword1 value
   */
  public void setNewPassword1(String tmp) {
    this.newPassword1 = tmp;
  }


  /**
   * Sets the newPassword2 attribute of the Password object
   *
   * @param tmp The new newPassword2 value
   */
  public void setNewPassword2(String tmp) {
    this.newPassword2 = tmp;
  }


  /**
   * Gets the userId attribute of the Password object
   *
   * @return The userId value
   */
  public int getUserId() {
    return userId;
  }


  /**
   * Gets the password attribute of the Password object
   *
   * @return The password value
   */
  public String getPassword() {
    return password;
  }


  /**
   * Gets the newPassword1 attribute of the Password object
   *
   * @return The newPassword1 value
   */
  public String getNewPassword1() {
    return newPassword1;
  }


  /**
   * Gets the newPassword2 attribute of the Password object
   *
   * @return The newPassword2 value
   */
  public String getNewPassword2() {
    return newPassword2;
  }


  /**
   * Gets the valid attribute of the Password object
   *
   * @return The valid value
   */
  public boolean isValid() {
    if (password == null || password.trim().equals("")) {
      errors.put("passwordError", "Password is required");
    }
    if (newPassword1 == null || newPassword1.trim().equals("")) {
      errors.put("newPasswordError", "New password is required");
    }
    if (!newPassword1.equals(newPassword2)) {
      errors.put("newPasswordError", "New passwords need to match");
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
  public boolean update(Connection db) throws SQLException {
    if (isValid()) {
      PreparedStatement pst = db.prepareStatement(
          "UPDATE users " +
              "SET password = ?, temporary_password = NULL, webdav_password = ? " +
              "WHERE user_id = ? " +
              "AND (password = ? OR temporary_password = ?) ");
      int i = 0;
      pst.setString(++i, PasswordHash.encrypt(newPassword1));
      pst.setString(++i, PasswordHash.encrypt(username + ":" + WebdavServlet.USER_REALM + ":" + newPassword1));
      pst.setInt(++i, userId);
      pst.setString(++i, PasswordHash.encrypt(password));
      pst.setString(++i, PasswordHash.encrypt(password));
      int count = pst.executeUpdate();
      pst.close();
      if (count != 1) {
        errors.put("actionError", "Password was not be updated, check password and try again");
      }
    } else {
      errors.put("actionError", "Make sure all required fields are supplied");
    }
    return (!hasErrors());
  }
}

