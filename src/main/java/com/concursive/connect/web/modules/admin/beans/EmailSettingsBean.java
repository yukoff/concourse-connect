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

package com.concursive.connect.web.modules.admin.beans;

import com.concursive.commons.db.DatabaseUtils;
import com.concursive.connect.config.ApplicationPrefs;

import java.io.IOException;

/**
 * Encapsulates the properties for setting up the application's email server
 *
 * @author matt rajkowski
 * @version $Id$
 * @created February 24, 2004
 */
public class EmailSettingsBean {

  private boolean isLoaded = false;
  private String mailServer = null;
  private String emailAddress = null;


  /**
   * Constructor for the EmailSettingsBean object
   */
  public EmailSettingsBean() {
  }


  /**
   * Sets the isLoaded attribute of the EmailSettingsBean object
   *
   * @param tmp The new isLoaded value
   */
  public void setIsLoaded(boolean tmp) {
    this.isLoaded = tmp;
  }


  /**
   * Sets the isLoaded attribute of the EmailSettingsBean object
   *
   * @param tmp The new isLoaded value
   */
  public void setIsLoaded(String tmp) {
    this.isLoaded = DatabaseUtils.parseBoolean(tmp);
  }


  /**
   * Gets the loaded attribute of the EmailSettingsBean object
   *
   * @return The loaded value
   */
  public boolean isLoaded() {
    return isLoaded;
  }


  /**
   * Sets the mailServer attribute of the EmailSettingsBean object
   *
   * @param tmp The new mailServer value
   */
  public void setMailServer(String tmp) {
    this.mailServer = tmp;
  }


  /**
   * Sets the emailAddress attribute of the EmailSettingsBean object
   *
   * @param tmp The new emailAddress value
   */
  public void setEmailAddress(String tmp) {
    this.emailAddress = tmp;
  }


  /**
   * Gets the mailServer attribute of the EmailSettingsBean object
   *
   * @return The mailServer value
   */
  public String getMailServer() {
    return mailServer;
  }


  /**
   * Gets the emailAddress attribute of the EmailSettingsBean object
   *
   * @return The emailAddress value
   */
  public String getEmailAddress() {
    return emailAddress;
  }


  /**
   * Description of the Method
   *
   * @param prefs    Description of the Parameter
   * @param filePath Description of the Parameter
   * @throws IOException Description of the Exception
   */
  public void load(ApplicationPrefs prefs, String filePath) throws IOException {
    mailServer = prefs.get("MAILSERVER");
    emailAddress = prefs.get("EMAILADDRESS");
    isLoaded = true;
  }

}

