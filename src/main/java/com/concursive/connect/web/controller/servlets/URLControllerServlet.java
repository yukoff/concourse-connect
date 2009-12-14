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
package com.concursive.connect.web.controller.servlets;

import com.concursive.commons.db.ConnectionElement;
import com.concursive.commons.db.ConnectionPool;
import com.concursive.commons.text.StringUtils;
import com.concursive.connect.config.ApplicationPrefs;
import com.concursive.connect.web.controller.beans.URLControllerBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * This servlet receives requests as mapped in web.xml; it is used for executing
 * a RequestDispatcher based on simple URLs; since not all pages have been
 * implemented as portlets there are some legacy mappings done here
 *
 * @author matt rajkowski
 * @created Jun 12, 2008
 */
public class URLControllerServlet extends HttpServlet {

  private static Log LOG = LogFactory.getLog(URLControllerServlet.class);

  public void init() {
  }

  public void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Translate the URI into its parts
    // URLControllerServlet-> contextPath: /context
    // URLControllerServlet-> path: /context/show/something
    String path = request.getRequestURI();
    String contextPath = request.getContextPath();
    URLControllerBean bean = new URLControllerBean(path, contextPath);
    String queryString = request.getQueryString();
    String requestedURL = path.substring(contextPath.length()) + (queryString == null ? "" : "?" + queryString);
    request.setAttribute("requestedURL", requestedURL);
    // Map to the MVC Action
    Connection db = null;
    String mappedPath = null;
    try {
      if (bean.getAction().equals(URLControllerBean.EDITOR)) {
        mappedPath = "ProjectPortal.do?command=Builder";
      } else if (bean.getAction().equals(URLControllerBean.REGISTER)) {
        mappedPath = "page/register";
      } else if (bean.getAction().equals(URLControllerBean.LOGIN)) {
        if (bean.getDomainObject() == null) {
          mappedPath = "Login.do?command=Default";
        } else {
          mappedPath = "Login.do?command=Login";
        }
      } else if (bean.getAction().equals(URLControllerBean.LOGOUT)) {
        mappedPath = "Login.do?command=Logout";
      } else if (bean.getAction().equals(URLControllerBean.RSS)) {
        mappedPath = "ProjectManagement.do?command=RSS";
      } else if (bean.getAction().equals(URLControllerBean.RSVP)) {
        mappedPath = "";
      } else if (bean.getAction().equals(URLControllerBean.INVITATIONS)) {
        mappedPath = "ProjectManagement.do?command=RSVP";
      } else if (bean.getAction().equals(URLControllerBean.SETUP)) {
        mappedPath = "Setup.do";
      } else if (bean.getAction().equals(URLControllerBean.ADMIN)) {
        mappedPath = LinkGenerator.getAdminPortalLink(bean);
      } else if (bean.getAction().equals(URLControllerBean.CATALOG)) {
        mappedPath = "Order.do";
      } else if (bean.getAction().equals(URLControllerBean.CONTACT_US)) {
        mappedPath = "ContactUs.do";
      } else if (bean.getAction().equals(URLControllerBean.PAGE)) {
        mappedPath = "Portal.do?command=ShowPortalPage&name=" + URLEncoder.encode(bean.getDomainObject(), "UTF-8") +
            (StringUtils.hasText(bean.getObjectValue()) ? "&view=" + URLEncoder.encode(bean.getObjectValue(), "UTF-8") : "") +
            (StringUtils.hasText(bean.getParams()) ? "&params=" + bean.getParams() : "");
      } else if (bean.getAction().equals(URLControllerBean.IMAGE)) {
        mappedPath = "Portal.do?command=Img&url=" + bean.getDomainObject();
      } else if (bean.getAction().equals(URLControllerBean.SETTINGS)) {
        mappedPath = "Profile.do";
      } else if (bean.getAction().equals(URLControllerBean.PROFILE)) {
        mappedPath = "ProjectManagement.do?command=Dashboard";
      } else if (bean.getAction().equals(URLControllerBean.BROWSE)) {
        mappedPath = "ProjectManagement.do?command=ProjectList";
      } else if (bean.getAction().equals(URLControllerBean.SEARCH)) {
        mappedPath = "Search.do?command=Default";
      } else if (bean.getAction().equals(URLControllerBean.SUPPORT)) {
        mappedPath = "ContactUs.do";
      } else if (bean.getAction().equals(URLControllerBean.REPORTS)) {
        mappedPath = "Reports.do?command=List";
      } else if (bean.getAction().equals(URLControllerBean.BADGE)) {
        mappedPath = "Badges.do?command=Details&id=" + bean.getObjectValue();
      } else if (bean.getAction().equals(URLControllerBean.MANAGEMENT_CRM)) {
        mappedPath = LinkGenerator.getCRMLink();
      } else if (bean.getAction().equals(URLControllerBean.SHOW)) {
        if (bean.getDomainObject() == null) {
          mappedPath = LinkGenerator.getProjectPortalLink(bean);
        } else if ("image".equals(bean.getDomainObject())) {
          mappedPath = LinkGenerator.getProfileImageLink(bean.getProjectId(), bean.getObjectValue());
        } else if ("dashboard".equals(bean.getDomainObject())) {
          mappedPath = LinkGenerator.getDashboardLink(bean.getProjectId(), bean.getObjectValue());
        } else if ("blog-image".equals(bean.getDomainObject())) {
          mappedPath = LinkGenerator.getBlogImageLink(bean.getProjectId(), bean.getObjectValue());
        } else if ("wiki-image".equals(bean.getDomainObject())) {
          mappedPath = LinkGenerator.getWikiImageLink(bean.getProjectId(), bean.getObjectValue());
        } else if ("lists".equals(bean.getDomainObject())) {
          mappedPath = LinkGenerator.getListsLink(bean.getProjectId());
        } else if ("list".equals(bean.getDomainObject())) {
          mappedPath = LinkGenerator.getListDetailsLink(bean.getProjectId(), bean.getObjectValueAsInt());
        } else if ("plans".equals(bean.getDomainObject())) {
          mappedPath = LinkGenerator.getPlanLink(bean.getProjectId());
        } else if ("plan".equals(bean.getDomainObject())) {
          mappedPath = LinkGenerator.getPlanLink(bean.getProjectId(), bean.getObjectValueAsInt());
        } else if ("issues".equals(bean.getDomainObject())) {
          mappedPath = LinkGenerator.getTicketsLink(bean.getProjectId());
        } else if ("issue".equals(bean.getDomainObject())) {
          mappedPath = LinkGenerator.getTicketDetailsLink(bean.getProjectId(), bean.getObjectValueAsInt());
        } else if ("details".equals(bean.getDomainObject())) {
          mappedPath = LinkGenerator.getDetailsLink(bean.getProjectId());
        } else if ("setup".equals(bean.getDomainObject())) {
          mappedPath = LinkGenerator.getSetupLink(bean.getProjectId());
        } else if ("customize".equals(bean.getDomainObject())) {
          mappedPath = LinkGenerator.getCustomizeLink(bean.getProjectId());
        } else if ("permissions".equals(bean.getDomainObject())) {
          mappedPath = LinkGenerator.getPermissionsLink(bean.getProjectId());
        } else if ("customize-style".equals(bean.getDomainObject())) {
          mappedPath = LinkGenerator.getCustomizeStyleLink(bean.getProjectId());
        } else if ("style-image".equals(bean.getDomainObject())) {
          mappedPath = LinkGenerator.getStyleImageLink(bean.getProjectId(), bean.getObjectValue());
        } else if ("app".equals(bean.getDomainObject())) {
          mappedPath = LinkGenerator.getPageLink(bean.getProjectId(), bean.getObjectValue());
        } else if ("tools".equals(bean.getDomainObject())) {
          mappedPath = LinkGenerator.getToolsLink(bean.getProjectId(), bean.getObjectValue());
        } else if ("crm-account".equals(bean.getDomainObject())) {
          mappedPath = LinkGenerator.getCRMAccountLink(bean.getProjectId(), bean.getObjectValue());
        } else {
          mappedPath = LinkGenerator.getProjectPortalLink(bean);
        }
      } else if (bean.getAction().equals(URLControllerBean.CREATE)) {
        if (bean.getDomainObject() == null) {
          mappedPath = "ProjectManagement.do?command=ModifyProject&pid=" + bean.getProjectId() + "&return=ProjectCenter";
        } else {
          mappedPath = LinkGenerator.getProjectPortalLink(bean);
        }
      } else if (bean.getAction().equals(URLControllerBean.MODIFY)) {
        if (bean.getDomainObject() == null) {
          mappedPath = "ProjectManagement.do?command=ModifyProject&pid=" + bean.getProjectId() + "&return=ProjectCenter";
        } else {
          mappedPath = LinkGenerator.getProjectPortalLink(bean);
        }
      } else if (bean.getAction().equals(URLControllerBean.REMOVE)) {
        if ("image".equals(bean.getDomainObject())) {
          mappedPath = LinkGenerator.getRemoveProfileImageLink(bean.getProjectId(), bean.getObjectValue());
        }
      } else if (bean.getAction().equals(URLControllerBean.SET)) {
        if ("image".equals(bean.getDomainObject())) {
          mappedPath = LinkGenerator.getSetProfileImageLink(bean.getProjectId(), bean.getObjectValue());
        } else {
          mappedPath = LinkGenerator.getProjectPortalLink(bean);
        }
      } else if (bean.getAction().equals(URLControllerBean.ACCEPT)) {
        mappedPath = "ProjectManagement.do?command=AcceptProject&pid=" + bean.getProjectId();
      } else if (bean.getAction().equals(URLControllerBean.REJECT)) {
        mappedPath = "ProjectManagement.do?command=RejectProject&pid=" + bean.getProjectId();
      } else if (bean.getAction().equals(URLControllerBean.DOWNLOAD) ||
          bean.getAction().equals(URLControllerBean.STREAM)) {
        if ("file".equals(bean.getDomainObject())) {
          mappedPath = "ProjectManagementFiles.do?command=Download" +
              "&pid=" + bean.getProjectId() +
              "&fid=" + bean.getObjectValue() +
              (bean.getParams() != null ? "&ver=" + bean.getParams() : "") +
              (bean.getAction().equals(URLControllerBean.STREAM) ? "&view=true" : "");
        } else {
          mappedPath = LinkGenerator.getProjectActionLink(bean);
        }
      } else {
        if (bean.getProjectId() > -1) {
          mappedPath = LinkGenerator.getProjectPortalLink(bean);
        }
      }
    } catch (Exception ex) {
      String msg = "URLControllerServletError1";
      LOG.error(msg, ex);
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, msg);
    }

    try {
      String forwardPath = null;
      if (mappedPath == null) {
        forwardPath = "/redirect404.jsp";
        LOG.error("A mapped path was not found for action: " + path);
      } else {
        forwardPath = "/" + mappedPath;
        LOG.debug("Forwarding request to: " + forwardPath);
      }
      RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(forwardPath);
      dispatcher.forward(request, response);
    } catch (Exception ex) {
      String msg = "URLControllerServletError2";
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, msg);
    }


  }

  protected static ConnectionElement getConnectionElement(ServletContext context) {
    ApplicationPrefs prefs = (ApplicationPrefs) context.getAttribute("applicationPrefs");
    ConnectionElement ce = new ConnectionElement();
    ce.setDriver(prefs.get("SITE.DRIVER"));
    ce.setUrl(prefs.get("SITE.URL"));
    ce.setUsername(prefs.get("SITE.USER"));
    ce.setPassword(prefs.get("SITE.PASSWORD"));
    return ce;
  }

  protected static Connection getConnection(ServletContext context) throws SQLException {
    ConnectionElement ce = getConnectionElement(context);
    ConnectionPool sqlDriver = (ConnectionPool) context.getAttribute("ConnectionPool");
    return sqlDriver.getConnection(ce, false);
  }

  protected static void freeConnection(Connection db, ServletContext context) {
    if (db != null) {
      ConnectionPool sqlDriver = (ConnectionPool) context.getAttribute("ConnectionPool");
      sqlDriver.free(db);
    }
    db = null;
  }
}
