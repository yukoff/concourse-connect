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

package com.concursive.connect.web.modules.profile.beans;

import com.concursive.commons.db.DatabaseUtils;
import com.concursive.commons.web.mvc.beans.GenericBean;

import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * Represents the areas of a project that can be cloned
 *
 * @author matt rajkowski
 * @version $Id$
 * @created Oct 18, 2005
 */

public class CloneBean extends GenericBean {
  private int projectId = -1;
  private String title = null;
  private boolean cloneNews = false;
  private boolean cloneNewsCategories = false;
  private boolean cloneWiki = false;
  private boolean cloneTeam = false;
  private boolean cloneForums = false;
  private boolean cloneTopics = false;
  private boolean cloneDocumentFolders = false;
  private boolean cloneTicketConfig = false;
  private boolean cloneLists = false;
  private boolean cloneListItems = false;
  private boolean cloneOutlines = false;
  private boolean cloneActivities = false;
  private boolean resetActivityDates = false;
  private boolean resetActivityStatus = false;
  private Timestamp requestDate = null;

  public int getProjectId() {
    return projectId;
  }

  public void setProjectId(int projectId) {
    this.projectId = projectId;
  }

  public void setProjectId(String projectId) {
    this.projectId = Integer.parseInt(projectId);
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public boolean getCloneNews() {
    return cloneNews;
  }

  public void setCloneNews(boolean cloneNews) {
    this.cloneNews = cloneNews;
  }

  public void setCloneNews(String tmp) {
    cloneNews = DatabaseUtils.parseBoolean(tmp);
  }

  public boolean getCloneNewsCategories() {
    return cloneNewsCategories;
  }

  public void setCloneNewsCategories(boolean cloneNewsCategories) {
    this.cloneNewsCategories = cloneNewsCategories;
  }

  public void setCloneNewsCategories(String tmp) {
    cloneNewsCategories = DatabaseUtils.parseBoolean(tmp);
  }

  public boolean getCloneWiki() {
    return cloneWiki;
  }

  public void setCloneWiki(boolean cloneWiki) {
    this.cloneWiki = cloneWiki;
  }

  public void setCloneWiki(String tmp) {
    cloneWiki = DatabaseUtils.parseBoolean(tmp);
  }

  public boolean getCloneTeam() {
    return cloneTeam;
  }

  public void setCloneTeam(boolean cloneTeam) {
    this.cloneTeam = cloneTeam;
  }

  public void setCloneTeam(String tmp) {
    cloneTeam = DatabaseUtils.parseBoolean(tmp);
  }

  public boolean getCloneForums() {
    return cloneForums;
  }

  public void setCloneForums(boolean cloneForums) {
    this.cloneForums = cloneForums;
  }

  public void setCloneForums(String tmp) {
    cloneForums = DatabaseUtils.parseBoolean(tmp);
  }

  public boolean getCloneTopics() {
    return cloneTopics;
  }

  public void setCloneTopics(boolean cloneTopics) {
    this.cloneTopics = cloneTopics;
  }

  public void setCloneTopics(String tmp) {
    cloneTopics = DatabaseUtils.parseBoolean(tmp);
  }

  public boolean getCloneDocumentFolders() {
    return cloneDocumentFolders;
  }

  public void setCloneDocumentFolders(boolean cloneDocumentFolders) {
    this.cloneDocumentFolders = cloneDocumentFolders;
  }

  public void setCloneDocumentFolders(String tmp) {
    cloneDocumentFolders = DatabaseUtils.parseBoolean(tmp);
  }

  public boolean getCloneTicketConfig() {
    return cloneTicketConfig;
  }

  public void setCloneTicketConfig(boolean cloneTicketConfig) {
    this.cloneTicketConfig = cloneTicketConfig;
  }

  public void setCloneTicketConfig(String tmp) {
    cloneTicketConfig = DatabaseUtils.parseBoolean(tmp);
  }

  public boolean getCloneLists() {
    return cloneLists;
  }

  public void setCloneLists(boolean cloneLists) {
    this.cloneLists = cloneLists;
  }

  public void setCloneLists(String tmp) {
    cloneLists = DatabaseUtils.parseBoolean(tmp);
  }

  public boolean getCloneListItems() {
    return cloneListItems;
  }

  public void setCloneListItems(boolean cloneListItems) {
    this.cloneListItems = cloneListItems;
  }

  public void setCloneListItems(String tmp) {
    cloneListItems = DatabaseUtils.parseBoolean(tmp);
  }

  public boolean getCloneOutlines() {
    return cloneOutlines;
  }

  public void setCloneOutlines(boolean cloneOutlines) {
    this.cloneOutlines = cloneOutlines;
  }

  public void setCloneOutlines(String tmp) {
    cloneOutlines = DatabaseUtils.parseBoolean(tmp);
  }

  public boolean getCloneActivities() {
    return cloneActivities;
  }

  public void setCloneActivities(boolean cloneActivities) {
    this.cloneActivities = cloneActivities;
  }

  public void setCloneActivities(String tmp) {
    cloneActivities = DatabaseUtils.parseBoolean(tmp);
  }

  public boolean getResetActivityDates() {
    return resetActivityDates;
  }

  public void setResetActivityDates(boolean resetActivityDates) {
    this.resetActivityDates = resetActivityDates;
  }

  public void setResetActivityDates(String tmp) {
    resetActivityDates = DatabaseUtils.parseBoolean(tmp);
  }

  public boolean getResetActivityStatus() {
    return resetActivityStatus;
  }

  public void setResetActivityStatus(boolean resetActivityStatus) {
    this.resetActivityStatus = resetActivityStatus;
  }

  public void setResetActivityStatus(String tmp) {
    resetActivityStatus = DatabaseUtils.parseBoolean(tmp);
  }

  /**
   * Sets the RequestDate attribute of the Project object
   *
   * @param tmp The new RequestDate value
   */
  public void setRequestDate(Timestamp tmp) {
    this.requestDate = tmp;
  }

  /**
   * Sets the requestDate attribute of the Project object
   *
   * @param tmp The new requestDate value
   */
  public void setRequestDate(String tmp) {
    requestDate = DatabaseUtils.parseTimestamp(tmp);
  }

  /**
   * Gets the RequestDate attribute of the Project object
   *
   * @return The RequestDate value
   */
  public Timestamp getRequestDate() {
    return requestDate;
  }

  public static ArrayList getTimeZoneParams() {
    ArrayList thisList = new ArrayList();
    thisList.add("requestDate");
    return thisList;
  }
}
