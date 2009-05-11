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
import com.concursive.commons.db.DatabaseUtils;
import com.concursive.commons.web.mvc.beans.GenericBean;

import javax.servlet.http.HttpServletRequest;

/**
 * Description of the Class
 *
 * @author matt
 * @version $Id$
 * @created January 5, 2002
 */
public class LoginBean extends GenericBean {
  private String username = null;
  private String password = null;
  private String redirectTo = null;
  private boolean addCookie = false;

  /**
   * Constructor for the LoginBean object
   *
   * @since 1.1
   */
  public LoginBean() {
  }


  /**
   * Sets the Username attribute of the LoginBean object
   *
   * @param tmp The new Username value
   * @since 1.1
   */
  public void setUsername(String tmp) {
    username = tmp;
  }


  /**
   * Sets the Password attribute of the LoginBean object
   *
   * @param tmp The new Password value
   * @since 1.1
   */
  public void setPassword(String tmp) {
    password = tmp;
  }


  /**
   * Gets the Username attribute of the LoginBean object
   *
   * @return The Username value
   * @since 1.1
   */
  public String getUsername() {
    return username;
  }


  /**
   * Gets the Password attribute of the LoginBean object
   *
   * @return The Password value
   * @since 1.1
   */
  public String getPassword() {
    return password;
  }


  /**
   * Gets the PasswordHash attribute of the LoginBean object
   *
   * @return The PasswordHash value
   * @since 1.1
   */
  public String getPasswordHash() {
    return PasswordHash.encrypt(password);
  }

  public String getRedirectTo() {
    return redirectTo;
  }

  public void setRedirectTo(String redirectTo) {
    this.redirectTo = redirectTo;
  }

  public boolean getAddCookie() {
    return addCookie;
  }

  public void setAddCookie(boolean addCookie) {
    this.addCookie = addCookie;
  }

  public void setAddCookie(String tmp) {
    this.addCookie = DatabaseUtils.parseBoolean(tmp);
  }

  public boolean isValid() {
    if (username == null || password == null ||
        "".equals(username.trim()) || "".equals(password.trim())) {
      return false;
    }
    return true;
  }

  public void checkURL(HttpServletRequest request) {
    if (redirectTo == null) {
      String requestedURL = (String) request.getAttribute("requestedURL");
      if (requestedURL != null &&
          !requestedURL.toLowerCase().startsWith("login") &&
          !requestedURL.toLowerCase().startsWith("/login") &&
          !requestedURL.toLowerCase().startsWith("logout") &&
          !requestedURL.toLowerCase().startsWith("/logout")) {
        setRedirectTo(requestedURL);
      }
    }

  }
}

