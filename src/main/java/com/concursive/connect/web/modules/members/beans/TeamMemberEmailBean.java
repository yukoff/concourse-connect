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
package com.concursive.connect.web.modules.members.beans;

import com.concursive.commons.db.DatabaseUtils;
import com.concursive.commons.text.StringUtils;
import com.concursive.commons.web.mvc.beans.GenericBean;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.login.utils.UserUtils;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;

import java.sql.*;

/**
 * Represents a private message to a profile
 *
 * @author Kailash Bhoopalam
 * @version $Id: TeamMemberEmailBean.java
 * @created December 28, 2009
 */
public class TeamMemberEmailBean extends GenericBean {

  private int id = -1;
  private int projectId = -1;
  private String body = null;
  private Timestamp entered = null;
  private int enteredBy = -1;

  /**
   * Constructor for the TeamMemberEmailBean
   */
  public TeamMemberEmailBean() {
  }

  /**
   * Sets the Id attribute of the TeamMemberEmailBean object
   *
   * @param tmp The new Id value
   */
  public void setId(int tmp) {
    this.id = tmp;
  }

  /**
   * Sets the id attribute of the TeamMemberEmailBean object
   *
   * @param tmp The new id value
   */
  public void setId(String tmp) {
    this.id = Integer.parseInt(tmp);
  }

  /**
   * Sets the enteredBy attribute of the TeamMemberEmailBean object
   *
   * @param tmp The new enteredBy value
   */
  public void setEnteredBy(int tmp) {
    this.enteredBy = tmp;
  }

  /**
   * Sets the entered attribute of the TeamMemberEmailBean object
   *
   * @param tmp The new entered value
   */
  public void setEntered(String tmp) {
    this.entered = DatabaseUtils.parseTimestamp(tmp);
  }

  /**
   * Sets the entered attribute of the TeamMemberEmailBean object
   *
   * @param tmp The new entered value
   */
  public void setEntered(Timestamp tmp) {
    entered = tmp;
  }

  /**
   * Sets the enteredBy attribute of the TeamMemberEmailBean object
   *
   * @param tmp The new enteredBy value
   */
  public void setEnteredBy(String tmp) {
    this.enteredBy = Integer.parseInt(tmp);
  }

  /**
   * Sets the projectId attribute of the TeamMemberEmailBean object
   *
   * @param tmp The new projectId value
   */
  public void setProjectId(int projectId) {
    this.projectId = projectId;
  }

  /**
   * Sets the projectId attribute of the TeamMemberEmailBean object
   *
   * @param tmp The new projectId value
   */
  public void setProjectId(String projectId) {
    this.projectId = Integer.parseInt(projectId);
  }

  /**
   * Gets the Id attribute of the TeamMemberEmailBean object
   *
   * @return The Id value
   */
  public int getId() {
    return id;
  }

  /**
   * Gets the entered attribute of the TeamMemberEmailBean object
   *
   * @return The entered value
   */
  public Timestamp getEntered() {
    return entered;
  }

  /**
   * Gets the enteredBy attribute of the TeamMemberEmailBean object
   *
   * @return The enteredBy value
   */
  public int getEnteredBy() {
    return enteredBy;
  }

  /**
   * @return the projectId
   */
  public int getProjectId() {
    return projectId;
  }

  /**
   * @return the body
   */
  public String getBody() {
    return body;
  }

  public String getHtmlBody() {
    if (StringUtils.hasText(body)) {
      return StringUtils.toHtmlValue(body);
    }

    return null;
  }

  /**
   * @param body the body to set
   */
  public void setBody(String body) {
    this.body = body;
  }

  /**
   * Gets the valid attribute of the TeamMemberEmailBean object
   *
   * @return The valid value
   */
  private boolean isValid() {
    if (!StringUtils.hasText(getBody())) {
      errors.put("bodyError", "Body is required");
    }
    return !this.hasErrors();
  }

  public User getUser() {
    return UserUtils.loadUser(enteredBy);
  }

  public Project getProject() {
    return ProjectUtils.loadProject(projectId);
  }

}
