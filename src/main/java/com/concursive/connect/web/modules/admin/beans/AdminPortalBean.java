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

import javax.servlet.http.HttpServletRequest;

/**
 * Decodes the url into various module properties
 *
 * @author matt rajkowski
 * @created December 14, 2009
 */
public class AdminPortalBean {

  // Request Properties
  private String action;
  private String domainObject;
  private String objectValue;
  private String params;
  // Helper Properties
  private String module;
  private boolean popup = false;

  public AdminPortalBean() {
  }

  public AdminPortalBean(HttpServletRequest request) {
    action = request.getParameter("portlet-action");
    domainObject = request.getParameter("portlet-object");
    objectValue = request.getParameter("portlet-value");
    params = request.getParameter("portlet-params");
    // check for popup
    if ("true".equals(request.getParameter("popup"))) {
      popup = true;
    }
  }

  public String getAction() {
    return action;
  }

  public void setAction(String action) {
    this.action = action;
  }

  public String getDomainObject() {
    return domainObject;
  }

  public void setDomainObject(String domainObject) {
    this.domainObject = domainObject;
  }

  public String getObjectValue() {
    return objectValue;
  }

  public void setObjectValue(String objectValue) {
    this.objectValue = objectValue;
  }

  public String getParams() {
    return params;
  }

  public void setParams(String params) {
    this.params = params;
  }

  public String getModule() {
    return module;
  }

  public void setModule(String module) {
    this.module = module;
  }

  public boolean isPopup() {
    return popup;
  }

  public void setPopup(boolean popup) {
    this.popup = popup;
  }

  public String toString() {
    String CRLF = System.getProperty("line.separator");
    StringBuffer sb = new StringBuffer();
    sb.append("==== AdminPortalBean ================================").append(CRLF);
    sb.append(" action       = ").append(action).append(CRLF);
    sb.append(" domainObject = ").append(domainObject).append(CRLF);
    sb.append(" objectValue  = ").append(objectValue).append(CRLF);
    sb.append(" params       = ").append(params).append(CRLF);
    return sb.toString();
  }
}