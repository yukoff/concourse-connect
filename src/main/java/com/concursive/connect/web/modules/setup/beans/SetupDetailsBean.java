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

import com.concursive.commons.db.DatabaseUtils;
import com.concursive.commons.text.StringUtils;
import com.concursive.commons.web.mvc.beans.GenericBean;

/**
 * Properties for setting up the application
 *
 * @author matt rajkowski
 * @version $Id$
 * @created September 19, 2004
 */
public class SetupDetailsBean extends GenericBean {
  private String server = null;
  private String serverPort = "25";
  private String serverUsername = null;
  private String serverPassword = null;
  private boolean serverSsl = false;
  private String address = null;
  private String storage = "-1";
  private String googleMapsAPIDomain = null;
  private String googleMapsAPIKey = null;


  /**
   * Gets the server attribute of the SetupDetailsBean object
   *
   * @return The server value
   */
  public String getServer() {
    return server;
  }


  /**
   * Sets the server attribute of the SetupDetailsBean object
   *
   * @param tmp The new server value
   */
  public void setServer(String tmp) {
    this.server = tmp;
  }

  public String getServerPort() {
    return serverPort;
  }

  public void setServerPort(String serverPort) {
    this.serverPort = serverPort;
  }

  public String getServerUsername() {
    return serverUsername;
  }

  public void setServerUsername(String serverUsername) {
    this.serverUsername = serverUsername;
  }

  public String getServerPassword() {
    return serverPassword;
  }

  public void setServerPassword(String serverPassword) {
    this.serverPassword = serverPassword;
  }

  public boolean getServerSsl() {
    return serverSsl;
  }

  public void setServerSsl(boolean serverSsl) {
    this.serverSsl = serverSsl;
  }

  public void setServerSsl(String serverSsl) {
    this.serverSsl = DatabaseUtils.parseBoolean(serverSsl);
  }

  /**
   * Gets the address attribute of the SetupDetailsBean object
   *
   * @return The address value
   */
  public String getAddress() {
    return address;
  }


  /**
   * Sets the address attribute of the SetupDetailsBean object
   *
   * @param tmp The new address value
   */
  public void setAddress(String tmp) {
    this.address = tmp;
  }


  /**
   * Gets the storage attribute of the SetupDetailsBean object
   *
   * @return The storage value
   */
  public String getStorage() {
    return storage;
  }


  /**
   * Sets the storage attribute of the SetupDetailsBean object
   *
   * @param tmp The new storage value
   */
  public void setStorage(String tmp) {
    this.storage = tmp;
  }

  public String getGoogleMapsAPIDomain() {
    return googleMapsAPIDomain;
  }

  public void setGoogleMapsAPIDomain(String googleMapsAPIDomain) {
    this.googleMapsAPIDomain = googleMapsAPIDomain;
  }

  public String getGoogleMapsAPIKey() {
    return googleMapsAPIKey;
  }

  public void setGoogleMapsAPIKey(String googleMapsAPIKey) {
    this.googleMapsAPIKey = googleMapsAPIKey;
  }

  /**
   * Gets the valid attribute of the SetupDetailsBean object
   *
   * @return The valid value
   */
  public boolean isValid() {
    if (!StringUtils.hasText(server)) {
      errors.put("serverError", "Mail server is required");
    }
    if (!StringUtils.hasText(address)) {
      errors.put("addressError", "Return email address is required");
    }
    if (!StringUtils.hasText(storage)) {
      errors.put("storageError", "Storage setting is required");
    }
    return !hasErrors();
  }

}

