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

package com.concursive.connect.cms.portal.beans;

import com.concursive.commons.web.mvc.beans.GenericBean;

import javax.servlet.http.HttpServletRequest;

/**
 * User: matt
 * Date: Dec 7, 2007
 * Time: 2:34:27 PM
 */
public class PortalBean extends GenericBean {
  private String serverName = null;
  private int projectId = -1;
  private int categoryId = -1;
  private int newsId = -1;
  private String projectKey = null;
  private String portalPath = null;
  private String portalExtension = null;
  private String language = null;
  private int languageId = -1;
  private String redirect = null;

  public PortalBean() {
  }

  public PortalBean(HttpServletRequest request) {
    this.setServerName(request.getServerName());
    if (request.getParameter("pid") != null) {
      this.setProjectId(request.getParameter("pid"));
    }
    this.setProjectKey(request.getParameter("key"));
    this.setNewsId(request.getParameter("nid"));
    this.setPortalPath((String) request.getAttribute("PortalPath"));
    this.setPortalExtension((String) request.getAttribute("PortalExtension"));
    this.setLanguage(request.getParameter("webSiteLanguage"));
    if (language == null) {
      this.setLanguage((String) request.getAttribute("webSiteLanguage"));
    }
  }

  public String getServerName() {
    return serverName;
  }

  public void setServerName(String serverName) {
    this.serverName = serverName;
  }

  public int getProjectId() {
    return projectId;
  }

  public void setProjectId(int projectId) {
    this.projectId = projectId;
  }

  public void setProjectId(String tmp) {
    if (tmp != null) {
      projectId = Integer.parseInt(tmp);
    }
  }

  public int getCategoryId() {
    return categoryId;
  }

  public void setCategoryId(int categoryId) {
    this.categoryId = categoryId;
  }

  public int getNewsId() {
    return newsId;
  }

  public void setNewsId(int newsId) {
    this.newsId = newsId;
  }

  public void setNewsId(String tmp) {
    if (tmp != null) {
      newsId = Integer.parseInt(tmp);
    }
  }


  public String getProjectKey() {
    return projectKey;
  }

  public void setProjectKey(String projectKey) {
    this.projectKey = projectKey;
  }

  public String getPortalPath() {
    return portalPath;
  }

  public void setPortalPath(String portalPath) {
    this.portalPath = portalPath;
  }

  public String getPortalExtension() {
    return portalExtension;
  }

  public void setPortalExtension(String portalExtension) {
    this.portalExtension = portalExtension;
  }

  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }

  public int getLanguageId() {
    return languageId;
  }

  public void setLanguageId(int languageId) {
    this.languageId = languageId;
  }

  public String getRedirect() {
    return redirect;
  }

  public void setRedirect(String string) {
    redirect = string;
  }

  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("** PortalBean **").append("\r\n");
    sb.append("  serverName: ").append(serverName).append("\r\n");
    sb.append("  projectId: ").append(projectId).append("\r\n");
    sb.append("  categoryId: ").append(categoryId).append("\r\n");
    sb.append("  newsId: ").append(newsId).append("\r\n");
    sb.append("  projectKey: ").append(projectKey).append("\r\n");
    sb.append("  portalPath: ").append(portalPath).append("\r\n");
    sb.append("  portalExtension: ").append(portalExtension).append("\r\n");
    sb.append("  language: ").append(language).append("\r\n");
    sb.append("  languageId: ").append(languageId).append("\r\n");
    return sb.toString();
  }

}
