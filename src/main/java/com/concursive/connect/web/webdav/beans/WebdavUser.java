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

package com.concursive.connect.web.webdav.beans;

/**
 * Description of the Class
 *
 * @author ananth
 * @version $Id$
 * @created November 2, 2004
 */
public class WebdavUser {
  private int userId = -1;
  private int roleId = -1;
  private String digest = null;
  private String nonce = null;


  /**
   * Sets the nonce attribute of the WebdavUser object
   *
   * @param tmp The new nonce value
   */
  public void setNonce(String tmp) {
    this.nonce = tmp;
  }


  /**
   * Gets the nonce attribute of the WebdavUser object
   *
   * @return The nonce value
   */
  public String getNonce() {
    return nonce;
  }


  /**
   * Sets the digest attribute of the WebdavUser object
   *
   * @param tmp The new digest value
   */
  public void setDigest(String tmp) {
    this.digest = tmp;
  }


  /**
   * Gets the digest attribute of the WebdavUser object
   *
   * @return The digest value
   */
  public String getDigest() {
    return digest;
  }


  /**
   * Sets the userId attribute of the WebdavUser object
   *
   * @param tmp The new userId value
   */
  public void setUserId(int tmp) {
    this.userId = tmp;
  }


  /**
   * Sets the userId attribute of the WebdavUser object
   *
   * @param tmp The new userId value
   */
  public void setUserId(String tmp) {
    this.userId = Integer.parseInt(tmp);
  }


  /**
   * Sets the roleId attribute of the WebdavUser object
   *
   * @param tmp The new roleId value
   */
  public void setRoleId(int tmp) {
    this.roleId = tmp;
  }


  /**
   * Sets the roleId attribute of the WebdavUser object
   *
   * @param tmp The new roleId value
   */
  public void setRoleId(String tmp) {
    this.roleId = Integer.parseInt(tmp);
  }


  /**
   * Gets the userId attribute of the WebdavUser object
   *
   * @return The userId value
   */
  public int getUserId() {
    return userId;
  }


  /**
   * Gets the roleId attribute of the WebdavUser object
   *
   * @return The roleId value
   */
  public int getRoleId() {
    return roleId;
  }


  /**
   * Constructor for the WebdavUser object
   */
  public WebdavUser() {
  }


  /**
   * Constructor for the WebdavUser object
   *
   * @param userId Description of the Parameter
   * @param roleId Description of the Parameter
   */
  public WebdavUser(int userId, int roleId) {
    this.userId = userId;
    this.roleId = roleId;
  }
}

