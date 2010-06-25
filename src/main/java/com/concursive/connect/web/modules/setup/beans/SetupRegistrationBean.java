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

package com.concursive.connect.web.modules.setup.beans;

import com.concursive.commons.db.DatabaseUtils;
import com.concursive.commons.email.EmailUtils;
import com.concursive.commons.text.StringUtils;
import com.concursive.commons.web.mvc.beans.GenericBean;

/**
 * Represents the properties for capturing a registration form
 *
 * @author matt rajkowski
 * @created February 19, 2009
 */
public class SetupRegistrationBean extends GenericBean {

  private String id = null;
  private String profile = null;
  private String key = null;
  private String code = null;
  private String nameFirst = null;
  private String nameLast = null;
  private String organization = null;
  private String email = null;
  private boolean ssl = false;
  private String os = null;
  private String java = null;
  private String webserver = null;
  private boolean proxy = false;
  private String proxyHost = null;
  private String proxyPort = null;
  private String proxyUsername = null;
  private String proxyPassword = null;
  private String vendor = null;
  private String url = null;

  public SetupRegistrationBean() {
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getProfile() {
    return profile;
  }

  public void setProfile(String profile) {
    this.profile = profile;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getNameFirst() {
    return nameFirst;
  }

  public void setNameFirst(String nameFirst) {
    this.nameFirst = nameFirst;
  }

  public String getNameLast() {
    return nameLast;
  }

  public void setNameLast(String nameLast) {
    this.nameLast = nameLast;
  }

  public String getOrganization() {
    return organization;
  }

  public void setOrganization(String organization) {
    this.organization = organization;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getOs() {
    return os;
  }

  public void setOs(String os) {
    this.os = os;
  }

  public String getJava() {
    return java;
  }

  public void setJava(String java) {
    this.java = java;
  }

  public String getWebserver() {
    return webserver;
  }

  public void setWebserver(String webserver) {
    this.webserver = webserver;
  }

  public boolean getSsl() {
    return ssl;
  }

  public void setSsl(boolean ssl) {
    this.ssl = ssl;
  }

  public void setSsl(String ssl) {
    this.ssl = DatabaseUtils.parseBoolean(ssl);
  }

  public boolean getProxy() {
    return proxy;
  }

  public void setProxy(boolean proxy) {
    this.proxy = proxy;
  }

  public void setProxy(String proxy) {
    this.proxy = DatabaseUtils.parseBoolean(proxy);
  }

  public String getProxyHost() {
    return proxyHost;
  }

  public void setProxyHost(String proxyHost) {
    this.proxyHost = proxyHost;
  }

  public String getProxyPort() {
    return proxyPort;
  }

  public void setProxyPort(String proxyPort) {
    this.proxyPort = proxyPort;
  }

  public String getProxyUsername() {
    return proxyUsername;
  }

  public void setProxyUsername(String proxyUsername) {
    this.proxyUsername = proxyUsername;
  }

  public String getProxyPassword() {
    return proxyPassword;
  }

  public void setProxyPassword(String proxyPassword) {
    this.proxyPassword = proxyPassword;
  }

  public String getVendor() {
    return vendor;
  }

  public void setVendor(String vendor) {
    this.vendor = vendor;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public boolean isValid() {
    if (!StringUtils.hasText(nameFirst)) {
      errors.put("nameFirstError", "First Name is required");
    }
    if (!StringUtils.hasText(nameLast)) {
      errors.put("nameLastError", "Last Name is required");
    }
    if (!StringUtils.hasText(email)) {
      errors.put("emailError", "Email is required");
    }
    if (!EmailUtils.checkEmail(email)) {
      errors.put("emailError", "Check the email address entered");
    }
    return !hasErrors();
  }
}
