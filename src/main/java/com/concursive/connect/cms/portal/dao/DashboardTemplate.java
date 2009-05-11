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
package com.concursive.connect.cms.portal.dao;

import com.concursive.commons.web.mvc.beans.GenericBean;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * The importable definition of portlets
 *
 * @author matt rajkowski
 * @version $Id$
 * @created January 20, 2008
 */
public class DashboardTemplate extends GenericBean {

  // Object Properties
  private int id = -1;
  private String name = null;
  private String xmlDesign;
  private String permission = null;
  private String title = null;
  private String description = null;
  private String keywords = null;
  private String category = null;
  // Helper Properties
  private String objectType = null;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getXmlDesign() {
    return xmlDesign;
  }

  public void setXmlDesign(String xmlDesign) {
    this.xmlDesign = xmlDesign;
  }

  public String getPermission() {
    return permission;
  }

  public void setPermission(String permission) {
    this.permission = permission;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getKeywords() {
    return keywords;
  }

  public void setKeywords(String keywords) {
    this.keywords = keywords;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public String getObjectType() {
    return objectType;
  }

  public void setObjectType(String objectType) {
    this.objectType = objectType;
  }

  public int createDashboard(Connection db, int projectId) throws SQLException {
    int dashboardId = -1;
    boolean commit = db.getAutoCommit();
    try {
      if (commit) {
        db.setAutoCommit(false);
      }
      // A project has a dashboard
      Dashboard dashboard = new Dashboard();
      dashboard.setProjectId(projectId);
      dashboard.setName(name);
      dashboard.setEnabled(true);
      dashboard.insert(db);
      dashboardId = dashboard.getId();
      // A dashboard has a page
      DashboardPage page = new DashboardPage();
      page.setDashboardId(dashboard.getId());
      page.setName(name);
      page.setXmlDesign(xmlDesign);
      page.setProjectId(projectId);
      page.setEnabled(true);
      page.insert(db);
      // Add any portlets from the page
      page.getPortletList().insert(db, page);
      if (commit) {
        db.commit();
      }
    } catch (Exception e) {
      if (commit) {
        db.rollback();
      }
      throw new SQLException(e.getMessage());
    } finally {
      if (commit) {
        db.setAutoCommit(true);
      }
    }
    return dashboardId;
  }


}