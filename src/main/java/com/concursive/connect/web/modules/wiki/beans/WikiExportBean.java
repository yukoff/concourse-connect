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

package com.concursive.connect.web.modules.wiki.beans;

import com.concursive.commons.db.DatabaseUtils;
import com.concursive.commons.text.StringUtils;
import com.concursive.commons.web.mvc.beans.GenericBean;

import java.io.File;

/**
 * Represents the capabilities of a wiki export
 *
 * @author matt rajkowski
 * @version $Id$
 * @created Oct 18, 2005
 */

public class WikiExportBean extends GenericBean {

  private int wikiId = -1;
  private int projectId = -1;
  private String subject = null;
  private int userId = -1;
  private boolean includeTitle = false;
  private boolean followLinks = false;
  private File exportedFile = null;
  private long fileSize = 0;

  public int getWikiId() {
    return wikiId;
  }

  public void setWikiId(int wikiId) {
    this.wikiId = wikiId;
  }

  public int getProjectId() {
    return projectId;
  }

  public void setProjectId(int projectId) {
    this.projectId = projectId;
  }

  public void setProjectId(String projectId) {
    this.projectId = Integer.parseInt(projectId);
  }

  public String getSubject() {
    return subject;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public int getUserId() {
    return userId;
  }

  public void setUserId(int userId) {
    this.userId = userId;
  }

  public boolean getIncludeTitle() {
    return includeTitle;
  }

  public void setIncludeTitle(boolean includeTitle) {
    this.includeTitle = includeTitle;
  }

  public void setIncludeTitle(String includeTitle) {
    this.includeTitle = DatabaseUtils.parseBoolean(includeTitle);
  }

  public boolean getFollowLinks() {
    return followLinks;
  }

  public void setFollowLinks(boolean followLinks) {
    this.followLinks = followLinks;
  }

  public void setFollowLinks(String followLinks) {
    this.followLinks = DatabaseUtils.parseBoolean(followLinks);
  }

  public File getExportedFile() {
    return exportedFile;
  }

  public void setExportedFile(File exportedFile) {
    this.exportedFile = exportedFile;
  }

  public long getFileSize() {
    return fileSize;
  }

  public void setFileSize(long fileSize) {
    this.fileSize = fileSize;
  }

  public String getDisplaySubject() {
    String displaySubject = subject;
    if (!StringUtils.hasText(subject)) {
      displaySubject = "Home";
    }
    return displaySubject;
  }

  public boolean isSimilar(WikiExportBean bean) {
    return (
        bean.getUserId() == this.getUserId() &&
            bean.getWikiId() == this.getWikiId() &&
            bean.getProjectId() == this.getProjectId() &&
            bean.getFollowLinks() == this.getFollowLinks() &&
            bean.getIncludeTitle() == this.getIncludeTitle());
  }
}