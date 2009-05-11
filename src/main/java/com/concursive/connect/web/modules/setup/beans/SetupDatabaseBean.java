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

package com.concursive.connect.web.modules.setup.beans;

import com.concursive.commons.web.mvc.beans.GenericBean;

/**
 * Description of the Class
 *
 * @author matt rajkowski
 * @version $Id$
 * @created August 14, 2004
 */
public class SetupDatabaseBean extends GenericBean {
  private String type = null;
  private String address = null;
  private int port = -1;
  private String database = null;
  private String user = null;
  private String password = null;


  /**
   * Constructor for the SetupDatabaseBean object
   */
  public SetupDatabaseBean() {
  }


  /**
   * Gets the type attribute of the SetupDatabaseBean object
   *
   * @return The type value
   */
  public String getType() {
    return type;
  }


  /**
   * Sets the type attribute of the SetupDatabaseBean object
   *
   * @param tmp The new type value
   */
  public void setType(String tmp) {
    this.type = tmp;
  }


  /**
   * Gets the address attribute of the SetupDatabaseBean object
   *
   * @return The address value
   */
  public String getAddress() {
    return address;
  }


  /**
   * Sets the address attribute of the SetupDatabaseBean object
   *
   * @param tmp The new address value
   */
  public void setAddress(String tmp) {
    this.address = tmp;
  }


  /**
   * Gets the port attribute of the SetupDatabaseBean object
   *
   * @return The port value
   */
  public int getPort() {
    return port;
  }


  /**
   * Sets the port attribute of the SetupDatabaseBean object
   *
   * @param tmp The new port value
   */
  public void setPort(int tmp) {
    this.port = tmp;
  }


  /**
   * Sets the port attribute of the SetupDatabaseBean object
   *
   * @param tmp The new port value
   */
  public void setPort(String tmp) {
    this.port = Integer.parseInt(tmp);
  }


  /**
   * Gets the database attribute of the SetupDatabaseBean object
   *
   * @return The database value
   */
  public String getDatabase() {
    return database;
  }


  /**
   * Sets the database attribute of the SetupDatabaseBean object
   *
   * @param tmp The new database value
   */
  public void setDatabase(String tmp) {
    this.database = tmp;
  }


  /**
   * Gets the user attribute of the SetupDatabaseBean object
   *
   * @return The user value
   */
  public String getUser() {
    return user;
  }


  /**
   * Sets the user attribute of the SetupDatabaseBean object
   *
   * @param tmp The new user value
   */
  public void setUser(String tmp) {
    this.user = tmp;
  }


  /**
   * Gets the password attribute of the SetupDatabaseBean object
   *
   * @return The password value
   */
  public String getPassword() {
    return password;
  }


  /**
   * Sets the password attribute of the SetupDatabaseBean object
   *
   * @param tmp The new password value
   */
  public void setPassword(String tmp) {
    this.password = tmp;
  }


  /**
   * Gets the valid attribute of the SetupDatabaseBean object
   *
   * @return The valid value
   */
  public boolean isValid() {
    if ("none".equals(type)) {
      errors.put("typeError", "Database type is required");
    }
    if (address == null || "".equals(address.trim())) {
      errors.put("addressError", "Database IP or domain name is required");
    }
    if (port == -1) {
      errors.put("portError", "Database port number is required");
    }
    if (database == null || "".equals(database.trim())) {
      errors.put("databaseError", "Database name is required");
    }
    if (user == null || "".equals(user.trim())) {
      errors.put("userError", "Database user name is required");
    }
    return !hasErrors();
  }


  /**
   * Gets the driver attribute of the SetupDatabaseBean object
   *
   * @return The driver value
   */
  public String getDriver() {
    if ("postgresql".equals(type)) {
      return "org.postgresql.Driver";
    } else if ("mssql".equals(type)) {
      return "net.sourceforge.jtds.jdbc.Driver";
    }
    return null;
  }


  /**
   * Gets the url attribute of the SetupDatabaseBean object
   *
   * @return The url value
   */
  public String getUrl() {
    if ("postgresql".equals(type)) {
      return "jdbc:postgresql://" + address + ":" + port + "/" + database;
    } else if ("mssql".equals(type)) {
      return "jdbc:jtds:sqlserver://" + address + ":" + port + "/" + database;
    }
    return null;
  }
}

