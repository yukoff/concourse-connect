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

package com.concursive.connect.web.modules.api.beans;

/**
 * When a module needs to get a connection to the database, it must first be
 * authenticated. Used by the Login module, XML transactions, and any of the
 * Process modules that do not go through login.
 *
 * @author matt rajkowski
 * @created November 11, 2002
 */
public class AuthenticationItem {

  private String id = null;
  private String code = null;
  private int systemId = -1;
  private int clientId = -1;
  private java.sql.Timestamp lastAnchor = null;
  private java.sql.Timestamp nextAnchor = null;
  private String authCode = "unset";
  private String encoding = "UTF-8";


  /**
   * Constructor for the AuthenticationItem object
   */
  public AuthenticationItem() {
  }


  /**
   * Sets the id attribute of the AuthenticationItem object
   *
   * @param tmp The new id value
   */
  public void setId(String tmp) {
    id = tmp;
  }


  /**
   * Sets the code attribute of the AuthenticationItem object, this is manually
   * set by the module
   *
   * @param tmp The new code value
   */
  public void setCode(String tmp) {
    code = tmp;
  }


  /**
   * Sets the clientId attribute of the AuthenticationItem object
   *
   * @param tmp The new clientId value
   */
  public void setClientId(int tmp) {
    clientId = tmp;
  }


  /**
   * Sets the clientId attribute of the AuthenticationItem object
   *
   * @param tmp The new clientId value
   */
  public void setClientId(String tmp) {
    clientId = Integer.parseInt(tmp);
  }


  /**
   * Sets the systemId attribute of the AuthenticationItem object
   *
   * @param tmp The new systemId value
   */
  public void setSystemId(int tmp) {
    this.systemId = tmp;
  }


  /**
   * Sets the systemId attribute of the AuthenticationItem object
   *
   * @param tmp The new systemId value
   */
  public void setSystemId(String tmp) {
    this.systemId = Integer.parseInt(tmp);
  }


  /**
   * Sets the lastAnchor attribute of the AuthenticationItem object
   *
   * @param tmp The new lastAnchor value
   */
  public void setLastAnchor(java.sql.Timestamp tmp) {
    this.lastAnchor = tmp;
  }


  /**
   * Sets the lastAnchor attribute of the AuthenticationItem object
   *
   * @param tmp The new lastAnchor value
   */
  public void setLastAnchor(String tmp) {
    this.lastAnchor = java.sql.Timestamp.valueOf(tmp);
  }


  /**
   * Sets the nextAnchor attribute of the AuthenticationItem object
   *
   * @param tmp The new nextAnchor value
   */
  public void setNextAnchor(java.sql.Timestamp tmp) {
    this.nextAnchor = tmp;
  }


  /**
   * Sets the nextAnchor attribute of the AuthenticationItem object
   *
   * @param tmp The new nextAnchor value
   */
  public void setNextAnchor(String tmp) {
    this.nextAnchor = java.sql.Timestamp.valueOf(tmp);
  }


  /**
   * Sets the authCode attribute of the AuthenticationItem object
   *
   * @param tmp The new authCode value
   */
  public void setAuthCode(String tmp) {
    this.authCode = tmp;
  }


  /**
   * Sets the XML encoding attribute of the AuthenticationItem object. The
   * encoding determines the encoding for all XML that will be returned.
   *
   * @param tmp The new encoding value
   */
  public void setEncoding(String tmp) {
    this.encoding = tmp;
  }


  /**
   * Gets the id attribute of the AuthenticationItem object
   *
   * @return The id value
   */
  public String getId() {
    return id;
  }


  /**
   * Gets the code attribute of the AuthenticationItem object
   *
   * @return The code value
   */
  public String getCode() {
    return code;
  }


  /**
   * Gets the clientId attribute of the AuthenticationItem object
   *
   * @return The clientId value
   */
  public int getClientId() {
    return clientId;
  }


  /**
   * Gets the systemId attribute of the AuthenticationItem object
   *
   * @return The systemId value
   */
  public int getSystemId() {
    return systemId;
  }


  /**
   * Gets the lastAnchor attribute of the AuthenticationItem object
   *
   * @return The lastAnchor value
   */
  public java.sql.Timestamp getLastAnchor() {
    return lastAnchor;
  }


  /**
   * Gets the nextAnchor attribute of the AuthenticationItem object
   *
   * @return The nextAnchor value
   */
  public java.sql.Timestamp getNextAnchor() {
    return nextAnchor;
  }


  /**
   * Gets the authCode attribute of the AuthenticationItem object
   *
   * @return The authCode value
   */
  public String getAuthCode() {
    return authCode;
  }


  /**
   * Gets the XML encoding attribute of the AuthenticationItem object. The
   * encoding specifies the preferred XML encoding for the client.
   *
   * @return The encoding value
   */
  public String getEncoding() {
    return encoding;
  }
}

