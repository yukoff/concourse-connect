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
import com.concursive.connect.config.ApplicationPrefs;

import java.io.IOException;

/**
 * Description of the Class
 *
 * @author matt rajkowski
 * @version $Id$
 * @created February 24, 2004
 */
public class SiteSettingsBean {

  private boolean isLoaded = false;
  private int accountSize = -1;
  private String timeZone = null;
  private boolean allowRegistration = false;
  private boolean allowInvitations = false;
  private boolean allowAddProjects = false;
  private String invitationSubject = null;
  private String invitationMessage = null;


  /**
   * Constructor for the SiteSettingsBean object
   */
  public SiteSettingsBean() {
  }


  /**
   * Sets the isLoaded attribute of the SiteSettingsBean object
   *
   * @param tmp The new isLoaded value
   */
  public void setIsLoaded(boolean tmp) {
    this.isLoaded = tmp;
  }


  /**
   * Sets the isLoaded attribute of the SiteSettingsBean object
   *
   * @param tmp The new isLoaded value
   */
  public void setIsLoaded(String tmp) {
    this.isLoaded = DatabaseUtils.parseBoolean(tmp);
  }


  /**
   * Sets the accountSize attribute of the SiteSettingsBean object
   *
   * @param tmp The new accountSize value
   */
  public void setAccountSize(int tmp) {
    this.accountSize = tmp;
  }


  /**
   * Sets the accountSize attribute of the SiteSettingsBean object
   *
   * @param tmp The new accountSize value
   */
  public void setAccountSize(String tmp) {
    this.accountSize = Integer.parseInt(tmp);
  }


  /**
   * Sets the timeZone attribute of the SiteSettingsBean object
   *
   * @param tmp The new timeZone value
   */
  public void setTimeZone(String tmp) {
    this.timeZone = tmp;
  }


  /**
   * Sets the allowRegistration attribute of the SiteSettingsBean object
   *
   * @param tmp The new allowRegistration value
   */
  public void setAllowRegistration(boolean tmp) {
    this.allowRegistration = tmp;
  }


  /**
   * Sets the allowRegistration attribute of the SiteSettingsBean object
   *
   * @param tmp The new allowRegistration value
   */
  public void setAllowRegistration(String tmp) {
    this.allowRegistration = DatabaseUtils.parseBoolean(tmp);
  }


  /**
   * Sets the allowInvitations attribute of the SiteSettingsBean object
   *
   * @param tmp The new allowInvitations value
   */
  public void setAllowInvitations(boolean tmp) {
    this.allowInvitations = tmp;
  }


  /**
   * Sets the allowInvitations attribute of the SiteSettingsBean object
   *
   * @param tmp The new allowInvitations value
   */
  public void setAllowInvitations(String tmp) {
    this.allowInvitations = DatabaseUtils.parseBoolean(tmp);
  }


  /**
   * Sets the invitationSubject attribute of the SiteSettingsBean object
   *
   * @param tmp The new invitationSubject value
   */
  public void setInvitationSubject(String tmp) {
    this.invitationSubject = tmp;
  }


  /**
   * Sets the invitationMessage attribute of the SiteSettingsBean object
   *
   * @param tmp The new invitationMessage value
   */
  public void setInvitationMessage(String tmp) {
    this.invitationMessage = tmp;
  }


  /**
   * Gets the allowAddProjects attribute of the SiteSettingsBean object
   *
   * @return The allowAddProjects value
   */
  public boolean getAllowAddProjects() {
    return allowAddProjects;
  }


  /**
   * Sets the allowAddProjects attribute of the SiteSettingsBean object
   *
   * @param tmp The new allowAddProjects value
   */
  public void setAllowAddProjects(boolean tmp) {
    this.allowAddProjects = tmp;
  }


  /**
   * Sets the allowAddProjects attribute of the SiteSettingsBean object
   *
   * @param tmp The new allowAddProjects value
   */
  public void setAllowAddProjects(String tmp) {
    this.allowAddProjects = DatabaseUtils.parseBoolean(tmp);
  }


  /**
   * Gets the loaded attribute of the SiteSettingsBean object
   *
   * @return The loaded value
   */
  public boolean isLoaded() {
    return isLoaded;
  }


  /**
   * Gets the accountSize attribute of the SiteSettingsBean object
   *
   * @return The accountSize value
   */
  public int getAccountSize() {
    return accountSize;
  }


  /**
   * Gets the timeZone attribute of the SiteSettingsBean object
   *
   * @return The timeZone value
   */
  public String getTimeZone() {
    return timeZone;
  }


  /**
   * Gets the allowRegistration attribute of the SiteSettingsBean object
   *
   * @return The allowRegistration value
   */
  public boolean getAllowRegistration() {
    return allowRegistration;
  }


  /**
   * Gets the allowInvitations attribute of the SiteSettingsBean object
   *
   * @return The allowInvitations value
   */
  public boolean getAllowInvitations() {
    return allowInvitations;
  }


  /**
   * Gets the invitationSubject attribute of the SiteSettingsBean object
   *
   * @return The invitationSubject value
   */
  public String getInvitationSubject() {
    return invitationSubject;
  }


  /**
   * Gets the invitationMessage attribute of the SiteSettingsBean object
   *
   * @return The invitationMessage value
   */
  public String getInvitationMessage() {
    return invitationMessage;
  }


  /**
   * Description of the Method
   *
   * @param prefs    Description of the Parameter
   * @param filePath Description of the Parameter
   * @throws IOException Description of the Exception
   */
  public void load(ApplicationPrefs prefs, String filePath) throws IOException {
    accountSize = Integer.parseInt(prefs.get("ACCOUNT.SIZE"));
    allowRegistration = "true".equals(prefs.get("REGISTER"));
    allowInvitations = "true".equals(prefs.get("INVITE"));
    allowAddProjects = "true".equals(prefs.get("START_PROJECTS"));
    // @todo load templates from freemarker for review
    //invitationSubject = StringUtils.loadText(filePath + prefs.get("INVITE.SUBJECT"));
    //invitationMessage = StringUtils.loadText(filePath + prefs.get("INVITE.MESSAGE"));
    isLoaded = true;
  }

}

