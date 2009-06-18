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

package com.concursive.connect.web.controller.beans;

import com.concursive.commons.text.StringUtils;
import com.concursive.commons.web.mvc.beans.GenericBean;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class provides a list of URL parts based on a parsed path
 *
 * @author matt rajkowski
 * @created Jun 13, 2008
 */
public class URLControllerBean extends GenericBean {

  private static Log LOG = LogFactory.getLog(URLControllerBean.class);

  // Root context URL values
  public static final String ADMIN = "admin";
  public static final String BADGE = "badge";
  public static final String BROWSE = "browse";
  public static final String CATALOG = "catalog";
  public static final String CONTACT_US = "contact-us";
  public static final String EDITOR = "editor";
  public static final String FEED = "feed";
  public static final String FILES = "files";
  public static final String IMAGE = "image";
  public static final String INVITATIONS = "invites";
  public static final String LOGIN = "login";
  public static final String LOGOUT = "logout";
  public static final String PAGE = "page";
  public static final String PROFILE = "profile";
  public static final String REGISTER = "register";
  public static final String REPORTS = "reports";
  public static final String RSS = "rss";
  public static final String RSVP = "rsvp";
  public static final String SEARCH = "search";
  public static final String SETTINGS = "settings";
  public static final String SETUP = "setup";
  public static final String SITEMAP = "sitemap";
  public static final String SIGN_UP = "sign-up";
  public static final String SUPPORT = "support";

  // Root context project values
  // Ex. /show/project-name [/blog][/x]
  // Ex. /show/project-name/image/xyz
  // Ex. /set/project-name/image/xyz
  // Ex. /remove/project-name [/blog/x]
  // Ex. /modify/project-name [/blog/x]
  public static final String SHOW = "show";
  public static final String CREATE = "create";
  public static final String REMOVE = "remove";
  public static final String SET = "set";
  public static final String MODIFY = "modify";
  public static final String CONFIG = "configure";
  public static final String MANAGE = "manage";
  public static final String EXECUTE = "execute";
  public static final String DOWNLOAD = "download";
  public static final String STREAM = "stream";
  public static final String EXPORT = "export";
  public static final String IMPORT = "import";
  public static final String CLONE = "clone";
  public static final String ACCEPT = "accept";
  public static final String REJECT = "reject";

  // Bean properties
  private String action;
  private String projectTitle;
  private int projectId = -1;
  private String domainObject;
  private String objectValue;
  private String params;

  public URLControllerBean(String actionPath, String context) {
    parsePath(actionPath, context);
  }

  private void parsePath(String actionPath, String contextPath) {
    LOG.debug("ActionPath: " + actionPath);
    LOG.debug("ContextPath: " + contextPath);
    // Format: http://127.0.0.1/action/projectTitle/domainObject/objectValue(id)
    // Format: http://127.0.0.1/context/action/projectTitle/domainObject/objectValue(id)
    // Format: /action/projectTitle/domainObject/objectValue(id)
    // Format: /context/action/projectTitle/domainObject/objectValue(id)
    // Format: action/projectTitle/domainObject/objectValue(id)

    // Strip any URL protocol
    if (actionPath.startsWith("http")) {
      actionPath = actionPath.substring(actionPath.indexOf("/", actionPath.indexOf("//") + 2));
    }

    // Strip any context
    if (StringUtils.hasText(contextPath) && actionPath.startsWith(contextPath)) {
      actionPath = actionPath.substring(contextPath.length());
    }

    // Strip the beginning /
    if (actionPath.startsWith("/")) {
      actionPath = actionPath.substring(1);
    }
    // Configure the action
    String[] thisAction = actionPath.split("/");
    if (thisAction.length > 0) {
      action = thisAction[0];
    }

    // Handle cases without a project
    if (action.equals(PAGE)) {
      if (thisAction.length > 1) {
        domainObject = thisAction[1];
      }
      if (thisAction.length > 2) {
        objectValue = thisAction[2];
      }
      if (thisAction.length > 3) {
        params = thisAction[3];
        int count = 4;
        while (count < thisAction.length) {
          params = params + ";;;" + thisAction[count];
          count++;
        }
      }
    } else if (action.equals(IMAGE)) {
      domainObject = thisAction[1];
    } else if (action.equals(BADGE)) {
      objectValue = thisAction[1];
    } else if (action.equals(LOGIN)) {
      if (thisAction.length > 1) {
        domainObject = thisAction[1];
      }
    } else {
      // Handle cases with a project
      if (thisAction.length > 1) {
        projectTitle = thisAction[1];
      }
      if (thisAction.length > 2) {
        domainObject = thisAction[2];
        int separator = domainObject.indexOf("?");
        if (separator > -1) {
          params = domainObject.substring(separator + 1);
          domainObject = domainObject.substring(0, separator);
        }
      }
      if (thisAction.length > 3) {
        objectValue = thisAction[3];
        int separator = objectValue.indexOf("?");
        if (separator > -1) {
          params = objectValue.substring(separator + 1);
          objectValue = objectValue.substring(0, separator);
        } else if (thisAction.length > 4) {
          params = thisAction[4];
        }
      }
      if (projectTitle != null) {
        projectId = ProjectUtils.retrieveProjectIdFromUniqueId(projectTitle);
      }
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

  public boolean hasDomainObject() {
    return (domainObject != null);
  }

  public void setDomainObject(String domainObject) {
    this.domainObject = domainObject;
  }

  public String getProjectTitle() {
    return projectTitle;
  }

  public void setProjectTitle(String projectTitle) {
    this.projectTitle = projectTitle;
  }

  public int getProjectId() {
    return projectId;
  }

  public void setProjectId(int projectId) {
    this.projectId = projectId;
  }

  public String getObjectValue() {
    return objectValue;
  }

  public int getObjectValueAsInt() {
    return Integer.parseInt(objectValue);
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

  public String toString() {
    String CRLF = System.getProperty("line.separator");
    StringBuffer sb = new StringBuffer();
    sb.append("===========================================").append(CRLF);
    sb.append("action=").append(action).append(CRLF);
    sb.append("project_title=").append(projectTitle).append(CRLF);
    sb.append("project_id=").append(projectId).append(CRLF);
    sb.append("domain_object=").append(domainObject).append(CRLF);
    sb.append("value=").append(objectValue).append(CRLF);
    sb.append("params=").append(params).append(CRLF);
    return sb.toString();
  }
}
