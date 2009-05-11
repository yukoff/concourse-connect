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

package com.concursive.connect.web.modules.plans.calendar;

import com.concursive.connect.web.modules.plans.dao.Assignment;
import com.concursive.connect.web.modules.plans.dao.AssignmentList;

public class ProjectEventList {
  AssignmentList pendingAssignments = new AssignmentList();
  int size = 0;


  /**
   * Gets the pendingAssignments attribute of the ProjectEventList object
   *
   * @return The pendingAssignments value
   */
  public AssignmentList getPendingAssignments() {
    return pendingAssignments;
  }


  /**
   * Sets the pendingAssignments attribute of the ProjectEventList object
   *
   * @param tmp The new pendingAssignments value
   */
  public void setPendingAssignments(AssignmentList tmp) {
    this.pendingAssignments = tmp;
  }


  /**
   * Gets the size attribute of the ProjectEventList object
   *
   * @return The size value
   */
  public int getSize() {
    return size;
  }


  /**
   * Sets the size attribute of the ProjectEventList object
   *
   * @param tmp The new size value
   */
  public void setSize(int tmp) {
    this.size = tmp;
  }


  /**
   * Sets the size attribute of the ProjectEventList object
   *
   * @param tmp The new size value
   */
  public void setSize(String tmp) {
    this.size = Integer.parseInt(tmp);
  }


  /**
   * Sets the size attribute of the ProjectEventList object
   *
   * @param size The new size value
   */
  public void setSize(Integer size) {
    this.size = size.intValue();
  }


  /**
   * Gets the sizeString attribute of the ProjectEventList object
   *
   * @return The sizeString value
   */
  public String getSizeString() {
    return String.valueOf(size);
  }


  /**
   * Adds a feature to the Event attribute of the ProjectEventList object
   *
   * @param thisAssignment The feature to be added to the Event attribute
   */
  public void addEvent(Assignment thisAssignment) {
    if (thisAssignment != null) {
      pendingAssignments.add(thisAssignment);
    }
  }
}


