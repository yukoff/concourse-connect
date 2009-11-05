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

import com.concursive.commons.web.mvc.beans.GenericBean;
import com.concursive.connect.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

public class UserSearchBean extends GenericBean {

  private String email = null;
  private String firstName = null;
  private String lastName = null;
  private String company = null;
  private String name = null;
  private int admin = Constants.UNDEFINED;
  private int registered = Constants.UNDEFINED;
  private int enabled = Constants.UNDEFINED;
  private int expired = Constants.UNDEFINED;
  private int contentEditor = Constants.UNDEFINED;

  private String activeProject = null;
  private int isTeamMember = Constants.UNDEFINED;
  private HashMap roleIds = null;
  private HashMap ratings = null;
  private HashMap lastViewed = null;
  private HashMap active = null;

  public UserSearchBean() {
  }

  public void setEmail(String tmp) {
    this.email = tmp;
  }

  public void setFirstName(String tmp) {
    this.firstName = tmp;
  }

  public void setLastName(String tmp) {
    this.lastName = tmp;
  }

  public void setCompany(String tmp) {
    this.company = tmp;
  }

  public void setAdmin(int tmp) {
    this.admin = tmp;
  }

  /**
   * The value set to search first and last name fields
   *
   * @return name
   */

  public String getName() {
    return name;
  }

  /**
   * Searches the first and last name fields for likely matches
   *
   * @param name - value to search
   */
  public void setName(String name) {
    this.name = name;
  }

  public void setAdmin(String tmp) {
    this.admin = Integer.parseInt(tmp);
  }

  public void setRegistered(int tmp) {
    this.registered = tmp;
  }

  public void setRegistered(String tmp) {
    this.registered = Integer.parseInt(tmp);
  }

  public void setEnabled(int tmp) {
    this.enabled = tmp;
  }

  public void setEnabled(String tmp) {
    this.enabled = Integer.parseInt(tmp);
  }

  public void setExpired(int tmp) {
    this.expired = tmp;
  }

  public void setExpired(String tmp) {
    this.expired = Integer.parseInt(tmp);
  }

  public void setContentEditor(int tmp) {
    this.contentEditor = tmp;
  }

  public void setContentEditor(String tmp) {
    this.contentEditor = Integer.parseInt(tmp);
  }

  public String getEmail() {
    return email;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public String getCompany() {
    return company;
  }

  public int getAdmin() {
    return admin;
  }

  public int getRegistered() {
    return registered;
  }

  public int getEnabled() {
    return enabled;
  }

  public int getExpired() {
    return expired;
  }

  public int getContentEditor() {
    return contentEditor;
  }

  public String getActiveProject() {
    return activeProject;
  }

  public void setActiveProject(String activeProject) {
    this.activeProject = activeProject;
  }

  /**
   * @return the teamMember
   */
  public int getIsTeamMember() {
  	return isTeamMember;
  }

	/**
   * @param teamMember the teamMember to set
   */
  public void setIsTeamMember(int teamMember) {
  	this.isTeamMember = isTeamMember;
  }

  public void setIsTeamMember(String teamMember) {
  	this.isTeamMember = ("true".equals(teamMember)?Constants.TRUE:Constants.FALSE);
  }

  public HashMap getRoleIds() {
    return roleIds;
  }

  public void setRoleIds(HashMap roleIds) {
    this.roleIds = roleIds;
  }

  public HashMap getActive() {
    return active;
  }

  public void setActive(HashMap active) {
    this.active = active;
  }

  public HashMap getLastViewed() {
    return lastViewed;
  }

  public void setLastViewed(HashMap lastViewed) {
    this.lastViewed = lastViewed;
  }

  public HashMap getRatings() {
    return ratings;
  }

  public void setRatings(HashMap ratings) {
    this.ratings = ratings;
  }

  public void setRoleCriteria(String criteria) {
    if (roleIds == null) {
      roleIds = new HashMap();
    }
    StringTokenizer st1 = new StringTokenizer(criteria, "[|]");
    String operator = st1.nextToken();
    String value = st1.nextToken();

    ArrayList tmp = (ArrayList) roleIds.get(operator);
    if (tmp == null) {
      tmp = new ArrayList();
    }
    tmp.add(value);
    roleIds.remove(operator);
    roleIds.put(operator, tmp);
  }

  public void setRatingCriteria(String criteria) {
    if (ratings == null) {
      ratings = new HashMap();
    }
    StringTokenizer st1 = new StringTokenizer(criteria, "[|]");
    String operator = st1.nextToken();
    String value = st1.nextToken();

    ArrayList tmp = (ArrayList) ratings.get(operator);
    if (tmp == null) {
      tmp = new ArrayList();
    }
    tmp.add(value);
    ratings.remove(operator);
    ratings.put(operator, tmp);
  }

  public void setLastViewedCriteria(String criteria) {
    if (lastViewed == null) {
      lastViewed = new HashMap();
    }
    
    StringTokenizer st1 = new StringTokenizer(criteria, "[|]");
    String operator = st1.nextToken();
    String value = st1.nextToken();

    ArrayList tmp = (ArrayList) lastViewed.get(operator);
    if (tmp == null) {
      tmp = new ArrayList();
    }
    tmp.add(value);
    lastViewed.remove(operator);
    lastViewed.put(operator, tmp);
  }

  public void setActiveCriteria(String criteria) {
    if (active == null) {
      active = new HashMap();
    }
    StringTokenizer st1 = new StringTokenizer(criteria, "[|]");
    String operator = st1.nextToken();
    String value = st1.nextToken();

    ArrayList tmp = (ArrayList) active.get(operator);
    if (tmp == null) {
      tmp = new ArrayList();
    }
    tmp.add(value);
    active.remove(operator);
    active.put(operator, tmp);
  }
}
