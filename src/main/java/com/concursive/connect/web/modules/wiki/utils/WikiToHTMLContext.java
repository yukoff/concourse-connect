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

package com.concursive.connect.web.modules.wiki.utils;

import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import com.concursive.connect.web.modules.wiki.dao.CustomForm;
import com.concursive.connect.web.modules.wiki.dao.Wiki;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Encapsulates objects used by the parser
 *
 * @author matt rajkowski
 * @created July 7, 2008
 */
public class WikiToHTMLContext {
  // properties used for parsing a wiki
  private Wiki wiki;
  private HashMap imageList;
  private LinkedHashMap<String, String> headerAnchors = new LinkedHashMap<String, String>();
  private int userId;
  private String contextPath;
  private int sectionIdCount = 0;
  private boolean canAppend = true;
  private int currentHeaderLevel = 1;
  // properties that affect the output
  private boolean editMode;
  private int editSectionId = -1;
  private int editSectionHeaderLevel = -1;
  private int editFormId = -1;

  public WikiToHTMLContext(Wiki wiki, HashMap imageList, int userId, boolean editMode, String contextPath) {
    this.wiki = wiki;
    this.imageList = imageList;
    this.userId = userId;
    this.editMode = editMode;
    this.contextPath = contextPath;
  }

  public WikiToHTMLContext(int userId, String contextPath) {
    this.wiki = null;
    this.imageList = new HashMap();
    this.userId = userId;
    this.editMode = false;
    this.contextPath = contextPath;
  }

  public Wiki getWiki() {
    return wiki;
  }

  public void setWiki(Wiki wiki) {
    this.wiki = wiki;
  }

  public HashMap getImageList() {
    return imageList;
  }

  public void setImageList(HashMap imageList) {
    this.imageList = imageList;
  }

  public LinkedHashMap<String, String> getHeaderAnchors() {
    return headerAnchors;
  }

  public void setHeaderAnchors(LinkedHashMap<String, String> headerAnchors) {
    this.headerAnchors = headerAnchors;
  }

  public int getUserId() {
    return userId;
  }

  public void setUserId(int userId) {
    this.userId = userId;
  }

  public boolean isEditMode() {
    return editMode;
  }

  public void setEditMode(boolean editMode) {
    this.editMode = editMode;
  }

  public String getContextPath() {
    return contextPath;
  }

  public void setContextPath(String contextPath) {
    this.contextPath = contextPath;
  }

  public int getSectionIdCount() {
    return sectionIdCount;
  }

  public void setSectionIdCount(int sectionIdCount) {
    this.sectionIdCount = sectionIdCount;
  }

  public boolean canAppend() {
    return canAppend;
  }

  public int getCurrentHeaderLevel() {
    return currentHeaderLevel;
  }

  public void setCurrentHeaderLevel(int currentHeaderLevel) {
    this.currentHeaderLevel = currentHeaderLevel;
  }

  public Project getProject() {
    return ProjectUtils.loadProject(wiki.getProjectId());
  }

  public int foundHeader(int headerLevel) {
    sectionIdCount += 1;
    currentHeaderLevel = headerLevel;
    // enabled/disable appending based on full or partial section mode
    if (editSectionId > 0) {
      if (editSectionId == sectionIdCount) {
        editSectionHeaderLevel = currentHeaderLevel;
        canAppend = true;
      }
      if (sectionIdCount > editSectionId && currentHeaderLevel <= editSectionHeaderLevel) {
        canAppend = false;
      }
    }
    return sectionIdCount;
  }

  public void foundForm(CustomForm form) {
    // enabled/disable appending based on full or partial section mode
    if (editSectionId > -1) {
      canAppend = false;
    }
    if (editFormId > -1) {
      canAppend = true;
    }
  }

  public void foundFormEnd() {
    // enabled/disable appending based on full or partial section mode
    if (editSectionId == 0) {
      canAppend = true;
    }
    // form edit mode overrides the output
    if (editFormId > -1) {
      canAppend = false;
    }
  }

  public int getEditSectionId() {
    return editSectionId;
  }

  public void setEditSectionId(int editSectionId) {
    this.editSectionId = editSectionId;
    canAppend = (editSectionId <= 0 && editFormId == -1);
  }

  public void setEditSectionId(String editSectionId) {
    setEditSectionId(Integer.parseInt(editSectionId));
  }

  public int getEditSectionHeaderLevel() {
    return editSectionHeaderLevel;
  }

  public void setEditSectionHeaderLevel(int editSectionHeaderLevel) {
    this.editSectionHeaderLevel = editSectionHeaderLevel;
  }

  public int getEditFormId() {
    return editFormId;
  }

  public void setEditFormId(int editFormId) {
    this.editFormId = editFormId;
    if (editFormId > -1) {
      canAppend = false;
    }
  }

  public void setEditFormId(String editFormId) {
    setEditFormId(Integer.parseInt(editFormId));
  }

  public int getProjectId() {
    if (wiki != null) {
      return wiki.getProjectId();
    } else {
      return -1;
    }
  }
}