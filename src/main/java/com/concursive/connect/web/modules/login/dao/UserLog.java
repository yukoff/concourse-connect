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
import com.concursive.commons.web.mvc.beans.GenericBean;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * Represents a user login with characteristics of the login
 *
 * @author matt rajkowski
 * @version $Id$
 * @created April 6, 2004
 */
public class UserLog extends GenericBean {

  private int userId = -1;
  private String ipAddress = null;
  private Timestamp logDate = null;
  private String browser = null;


  /**
   * Constructor for the UserLog object
   */
  public UserLog() {
  }


  /**
   * Constructor for the UserLog object
   *
   * @param rs Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public UserLog(ResultSet rs) throws SQLException {
    buildRecord(rs);
  }


  /**
   * Sets the userId attribute of the UserLog object
   *
   * @param tmp The new userId value
   */
  public void setUserId(int tmp) {
    this.userId = tmp;
  }


  /**
   * Sets the userId attribute of the UserLog object
   *
   * @param tmp The new userId value
   */
  public void setUserId(String tmp) {
    this.userId = Integer.parseInt(tmp);
  }


  /**
   * Sets the ipAddress attribute of the UserLog object
   *
   * @param tmp The new ipAddress value
   */
  public void setIpAddress(String tmp) {
    this.ipAddress = tmp;
  }


  /**
   * Sets the logDate attribute of the UserLog object
   *
   * @param tmp The new logDate value
   */
  public void setLogDate(Timestamp tmp) {
    this.logDate = tmp;
  }


  /**
   * Sets the logDate attribute of the UserLog object
   *
   * @param tmp The new logDate value
   */
  public void setLogDate(String tmp) {
    this.logDate = DatabaseUtils.parseTimestamp(tmp);
  }


  /**
   * Sets the browser attribute of the UserLog object
   *
   * @param tmp The new browser value
   */
  public void setBrowser(String tmp) {
    this.browser = tmp;
  }


  /**
   * Gets the userId attribute of the UserLog object
   *
   * @return The userId value
   */
  public int getUserId() {
    return userId;
  }


  /**
   * Gets the ipAddress attribute of the UserLog object
   *
   * @return The ipAddress value
   */
  public String getIpAddress() {
    return ipAddress;
  }


  /**
   * Gets the logDate attribute of the UserLog object
   *
   * @return The logDate value
   */
  public Timestamp getLogDate() {
    return logDate;
  }


  /**
   * Gets the browser attribute of the UserLog object
   *
   * @return The browser value
   */
  public String getBrowser() {
    return browser;
  }


  /**
   * Description of the Method
   *
   * @param rs Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public void buildRecord(ResultSet rs) throws SQLException {
    userId = rs.getInt("user_id");
    logDate = rs.getTimestamp("log_date");
    browser = rs.getString("browser");
    ipAddress = rs.getString("ip_address");
  }

}

