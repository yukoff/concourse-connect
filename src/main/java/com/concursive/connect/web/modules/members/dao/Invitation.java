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

package com.concursive.connect.web.modules.members.dao;

import com.concursive.connect.web.modules.login.dao.User;

import javax.servlet.http.HttpServletRequest;

/**
 * Represents an invitation to a user to join a profile
 *
 * @author matt rajkowski
 * @version $Id$
 * @created October 27, 2003
 */
public class Invitation {

  //bean properties
  private String email = null;
  private String firstName = null;
  private String lastName = null;
  //helpers
  private boolean sentMail = false;


  /**
   * Constructor for the Invitation object
   */
  public Invitation() {
  }

  public Invitation(User thisUser) {
    email = thisUser.getEmail();
    firstName = thisUser.getFirstName();
    lastName = thisUser.getLastName();
  }


  /**
   * Constructor for the Invitation object
   *
   * @param request Description of the Parameter
   * @param id      Description of the Parameter
   */
  public Invitation(HttpServletRequest request, int id) {
    email = request.getParameter("email" + id);
    firstName = request.getParameter("firstName" + id);
    lastName = request.getParameter("lastName" + id);
  }


  /**
   * Sets the email attribute of the Invitation object
   *
   * @param tmp The new email value
   */
  public void setEmail(String tmp) {
    this.email = tmp;
  }


  /**
   * Sets the firstName attribute of the Invitation object
   *
   * @param tmp The new firstName value
   */
  public void setFirstName(String tmp) {
    this.firstName = tmp;
  }


  /**
   * Sets the lastName attribute of the Invitation object
   *
   * @param tmp The new lastName value
   */
  public void setLastName(String tmp) {
    this.lastName = tmp;
  }


  /**
   * Sets the sentMail attribute of the Invitation object
   *
   * @param tmp The new sentMail value
   */
  public void setSentMail(boolean tmp) {
    this.sentMail = tmp;
  }


  /**
   * Gets the email attribute of the Invitation object
   *
   * @return The email value
   */
  public String getEmail() {
    return email;
  }


  /**
   * Gets the firstName attribute of the Invitation object
   *
   * @return The firstName value
   */
  public String getFirstName() {
    return firstName;
  }


  /**
   * Gets the lastName attribute of the Invitation object
   *
   * @return The lastName value
   */
  public String getLastName() {
    return lastName;
  }


  /**
   * Gets the sentMail attribute of the Invitation object
   *
   * @return The sentMail value
   */
  public boolean getSentMail() {
    return sentMail;
  }


  /**
   * Gets the valid attribute of the Invitation object
   *
   * @return The valid value
   */
  public boolean isValid() {
    if (email == null || "".equals(email.trim())) {
      return false;
    }
    if (firstName == null || "".equals(firstName.trim())) {
      return false;
    }
    if (lastName == null || "".equals(lastName.trim())) {
      return false;
    }
    return true;
  }

}

