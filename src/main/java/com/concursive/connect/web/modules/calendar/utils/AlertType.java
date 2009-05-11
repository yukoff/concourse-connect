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
package com.concursive.connect.web.modules.calendar.utils;

/**
 * Represents an Alert.
 *
 * @author Mathur
 * @version $Id$
 * @created January 20, 2003
 */
public class AlertType {

  protected String name = null;
  protected String displayName = null;
  protected String className = null;


  /**
   * Constructor for the AlertType object
   *
   * @param name        Description of the Parameter
   * @param className   Description of the Parameter
   * @param displayName Description of the Parameter
   */
  public AlertType(String name, String className, String displayName) {
    this.name = name;
    this.displayName = displayName;
    this.className = className;
  }


  /**
   * Sets the name attribute of the AlertType object
   *
   * @param name The new name value
   */
  public void setName(String name) {
    this.name = name;
  }


  /**
   * Sets the className attribute of the AlertType object
   *
   * @param className The new className value
   */
  public void setClassName(String className) {
    this.className = className;
  }


  /**
   * Sets the displayName attribute of the AlertType object
   *
   * @param displayName The new displayName value
   */
  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }


  /**
   * Gets the displayName attribute of the AlertType object
   *
   * @return The displayName value
   */
  public String getDisplayName() {
    return displayName;
  }


  /**
   * Gets the name attribute of the AlertType object
   *
   * @return The name value
   */
  public String getName() {
    return name;
  }


  /**
   * Gets the className attribute of the AlertType object
   *
   * @return The className value
   */
  public String getClassName() {
    return className;
  }

}


