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

package com.concursive.connect.web.modules.contacts.beans;

import com.concursive.commons.web.mvc.beans.GenericBean;

public class ContactSearchBean extends GenericBean {
  public static final int JOIN_AND = 1;
  public static final int JOIN_OR = 2;

  private int method = JOIN_AND;
  private String email = null;
  private String firstName = null;
  private String lastName = null;
  private String organization = null;

  public ContactSearchBean() {
  }


  public int getMethod() {
    return method;
  }

  public void setMethod(int method) {
    this.method = method;
  }

  public void setEmail(String tmp) {
    this.email = tmp;
  }

  public void setFirstName(String tmp) {
    this.firstName = tmp;
  }

  public void setLastName(String tmp) {
    this.lastName = tmp;
  }

  public void setOrganization(String tmp) {
    this.organization = tmp;
  }

  public String getEmail() {
    return email;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public String getOrganization() {
    return organization;
  }

  public String appendMethod(boolean found) {
    if (!found) {
      return "";
    } else {
      if (method == JOIN_AND) {
        return "AND ";
      } else if (method == JOIN_OR) {
        return "OR ";
      }
    }
    return "";
  }
}
