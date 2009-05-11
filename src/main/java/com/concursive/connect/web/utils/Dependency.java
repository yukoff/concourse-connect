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

package com.concursive.connect.web.utils;

import com.concursive.commons.web.mvc.beans.GenericBean;

/**
 * Signifies a dependency of an item with another.<br>
 * e.g Accounts -- Contacts; Tickets -- Contacts .
 *
 * @author Mathur
 * @version $Id$
 * @created December 18, 2002
 */
public class Dependency extends GenericBean {
  private String name = null;
  private boolean canDelete = false;
  private int count = 0;


  /**
   * Sets the title attribute of the Dependency object
   *
   * @param name The new name value
   */
  public void setName(String name) {
    this.name = name;
  }


  /**
   * Sets the canDelete attribute of the Dependency object
   *
   * @param canDelete The new canDelete value
   */
  public void setCanDelete(boolean canDelete) {
    this.canDelete = canDelete;
  }


  /**
   * Sets the count attribute of the Dependency object
   *
   * @param count The new count value
   */
  public void setCount(int count) {
    this.count = count;
  }


  /**
   * Gets the title attribute of the Dependency object
   *
   * @return The title value
   */
  public String getName() {
    return name;
  }


  /**
   * Gets the canDelete attribute of the Dependency object
   *
   * @return The canDelete value
   */
  public boolean getCanDelete() {
    return canDelete;
  }


  /**
   * Gets the count attribute of the Dependency object
   *
   * @return The count value
   */
  public int getCount() {
    return count;
  }

}

