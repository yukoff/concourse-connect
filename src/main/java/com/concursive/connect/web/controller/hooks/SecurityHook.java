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
package com.concursive.connect.web.controller.hooks;

import com.concursive.commons.db.ConnectionElement;
import com.concursive.commons.db.ConnectionPool;
import com.concursive.commons.http.CookieUtils;
import com.concursive.commons.web.mvc.servlets.ControllerHook;
import com.concursive.connect.Constants;
import com.concursive.connect.cms.portal.beans.PortalBean;
import com.concursive.connect.config.ApplicationPrefs;
import com.concursive.connect.config.ApplicationVersion;
import com.concursive.connect.web.modules.login.auth.session.ISessionValidator;
import com.concursive.connect.web.modules.login.auth.session.SessionValidatorFactory;
import com.concursive.connect.web.modules.login.beans.LoginBean;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.login.utils.UserUtils;
import com.concursive.connect.web.modules.members.dao.InvitationList;
import com.concursive.connect.web.modules.messages.dao.PrivateMessageList;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.dao.ProjectCategory;
import com.concursive.connect.web.modules.profile.dao.ProjectCategoryList;
import com.concursive.connect.web.modules.profile.dao.ProjectList;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import com.concursive.connect.web.modules.search.beans.SearchBean;
import com.concursive.connect.web.modules.translation.dao.WebSiteLanguageList;
import com.concursive.connect.web.utils.ClientType;
import com.concursive.connect.web.utils.HtmlSelect;
import com.concursive.connect.web.utils.PagedListInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Executed on every request to verify user is logged in, and that system
 * resources are ready, also prepares portlets
 *
 * @author matt rajkowski
 * @version $Id$
 * @created May 7, 2003
 */
public class SecurityHook implements ControllerHook {

  private static Log LOG = LogFactory.getLog(SecurityHook.class);
  public final static String fs = System.getProperty("file.separator");
  private ISessionValidator sessionValidator = null;


  /**
   * Checks to see if a User session object exists, if not then the security
   * check fails.
   *
   * @param servlet  Description of the Parameter
   * @param request  Description of the Parameter
   * @param response Description of the Parameter
   * @return Description of the Return Value
   */
  public String beginRequest(Servlet servlet, HttpServletRequest request, HttpServletResponse response) {
    LOG.debug("Security request made");
    ServletConfig config = servlet.getServletConfig();
    ServletContext context = config.getServletContext();
    // Application wide preferences
    ApplicationPrefs prefs = (ApplicationPrefs) context.getAttribute("applicationPrefs");
    // Get the intended action, if going to the login module, then let it proceed
    String s = request.getServletPath();
    int slash = s.lastIndexOf("/");
    s = s.substring(slash + 1);
    // If not setup then go to setup
    if (!s.startsWith("Setup") && !prefs.isConfigured()) {
      return "NotSetupError";
    }
    // External calls
    if (s.startsWith("Service")) {
      return null;
    }
    // Set the layout relevant to this request
    if ("text".equals(request.getParameter("out")) ||
        "text".equals(request.getAttribute("out"))) {
    } else if ("true".equals(request.getParameter("popup"))) {
      request.setAttribute("PageLayout", "/layout1.jsp");
    } else if ("true".equals(request.getParameter("style"))) {
      request.setAttribute("PageLayout", "/layout1.jsp");
    } else {
      request.setAttribute("PageLayout", context.getAttribute("Template"));
    }
    // If going to Setup then allow
    if (s.startsWith("Setup")) {
      request.setAttribute("PageLayout", "/layout1.jsp");
      return null;
    }
    // If going to Upgrade then allow
    if (s.startsWith("Upgrade")) {
      request.setAttribute("PageLayout", "/layout1.jsp");
      return null;
    }
    // Detect mobile users and mobile formatting
    ClientType clientType = (ClientType) request.getSession().getAttribute("clientType");
    if (clientType == null) {
      clientType = new ClientType(request);
      request.getSession().setAttribute("clientType", clientType);
    }
    if (clientType.getMobile()) {
      request.setAttribute("PageLayout", "/layoutMobile.jsp");
    }
    // URL forwarding
    String requestedURL = (String) request.getAttribute("requestedURL");
    if (requestedURL == null && "GET".equals(request.getMethod()) && request.getParameter("redirectTo") == null) {
      // Save the requestURI to be used downstream
      String contextPath = request.getContextPath();
      String uri = request.getRequestURI();
      String queryString = request.getQueryString();
      requestedURL = uri.substring(contextPath.length()) + (queryString == null ? "" : "?" + queryString);
      request.setAttribute("requestedURL", requestedURL);
      // The portal has it's own redirect scheme, this is a general catch all
      if (!s.startsWith("Portal") && uri.indexOf(".shtml") == -1) {
        // Check to see if an old domain name is used
        PortalBean bean = new PortalBean(request);
        String expectedURL = prefs.get("URL");
        if (expectedURL != null && requestedURL != null &&
            !"127.0.0.1".equals(bean.getServerName()) &&
            !"localhost".equals(bean.getServerName()) &&
            !bean.getServerName().equals(expectedURL)) {
          if (uri != null && !uri.substring(1).equals(prefs.get("PORTAL.INDEX"))) {
            request.setAttribute("redirectTo", requestedURL.substring(requestedURL.lastIndexOf("/")));
          }
          request.removeAttribute("PageLayout");
          return "Redirect301";
        }
      }
    }

    // Version check
    if (!s.startsWith("Login") && ApplicationVersion.isOutOfDate(prefs)) {
      return "UpgradeCheck";
    }
    // Get the user object from the Session Validation...
    if (sessionValidator == null) {
      sessionValidator = SessionValidatorFactory.getInstance().getSessionValidator(servlet.getServletConfig().getServletContext(), request);
      LOG.debug("Found sessionValidator? " + (sessionValidator != null));
    }
    // Check cookie for user's previous location info
    SearchBean searchBean = (SearchBean) request.getSession().getAttribute(Constants.SESSION_SEARCH_BEAN);
    if (searchBean == null) {
      String searchLocation = CookieUtils.getCookieValue(request, Constants.COOKIE_USER_SEARCH_LOCATION);
      if (searchLocation != null) {
        LOG.debug("Setting search location from cookie: " + searchLocation);
        searchBean = new SearchBean();
        searchBean.setLocation(searchLocation);
        request.getSession().setAttribute(Constants.SESSION_SEARCH_BEAN, searchBean);
      }
    }
    // Continue with session creation...
    User userSession = sessionValidator.validateSession(context, request, response);
    ConnectionElement ceSession = (ConnectionElement) request.getSession().getAttribute("ConnectionElement");
    // The user is going to the portal so get them guest credentials if they need them
    if ("true".equals(prefs.get("PORTAL")) ||
        s.startsWith("Register") ||
        s.startsWith("ResetPassword") ||
        s.startsWith("LoginAccept") ||
        s.startsWith("LoginReject") ||
        s.startsWith("Search") ||
        s.startsWith("ContactUs")) {
      if (userSession == null || ceSession == null) {
        // Allow portal mode to create a default session with guest capabilities
        userSession = UserUtils.createGuestUser();
        request.getSession().setAttribute("User", userSession);
        // Give them a connection element
        ceSession = new ConnectionElement();
        ceSession.setDriver(prefs.get("SITE.DRIVER"));
        ceSession.setUrl(prefs.get("SITE.URL"));
        ceSession.setUsername(prefs.get("SITE.USER"));
        ceSession.setPassword(prefs.get("SITE.PASSWORD"));
        request.getSession().setAttribute("ConnectionElement", ceSession);
      }
      // Generate global items
      Connection db = null;
      try {
        db = getConnection(context, request);
        // TODO: Implement cache since every hit needs this list
        if (db != null) {
          // Load the project languages
          WebSiteLanguageList webSiteLanguageList = new WebSiteLanguageList();
          webSiteLanguageList.setEnabled(Constants.TRUE);
          webSiteLanguageList.buildList(db);
          // If an admin of a language, enable it...
          webSiteLanguageList.add(userSession.getWebSiteLanguageList());
          request.setAttribute("webSiteLanguageList", webSiteLanguageList);

          // Load the menu list
          ProjectList menu = new ProjectList();
          PagedListInfo menuListInfo = new PagedListInfo();
          menuListInfo.setColumnToSortBy("p.portal_default desc, p.level asc, p.title asc");
          menuListInfo.setItemsPerPage(-1);
          menu.setPagedListInfo(menuListInfo);
          menu.setIncludeGuestProjects(false);
          menu.setPortalState(Constants.TRUE);
          menu.setOpenProjectsOnly(true);
          menu.setApprovedOnly(true);
          menu.setBuildLink(true);
          menu.setLanguageId(webSiteLanguageList.getLanguageId(request));
          menu.buildList(db);
          request.setAttribute("menuList", menu);

          // Load the main profile to use throughout
          Project mainProfile = ProjectUtils.loadProject(prefs.get("MAIN_PROFILE"));
          request.setAttribute(Constants.REQUEST_MAIN_PROFILE, mainProfile);

          // Load the project categories for search and tab menus
          ProjectCategoryList categoryList = new ProjectCategoryList();
          categoryList.setEnabled(Constants.TRUE);
          categoryList.setTopLevelOnly(true);
          if (!userSession.isLoggedIn()) {
            categoryList.setSensitive(Constants.FALSE);
          }
          categoryList.buildList(db);
          request.setAttribute(Constants.REQUEST_TAB_CATEGORY_LIST, categoryList);

          // Category drop-down for search
          HtmlSelect thisSelect = categoryList.getHtmlSelect();
          thisSelect.addItem(-1, prefs.get("TITLE"), 0);
          request.setAttribute(Constants.REQUEST_MENU_CATEGORY_LIST, thisSelect);

          //Determine the tab that needs to be highlighted
          if (requestedURL != null) {
            boolean foundChosenTab = false;
            String chosenCategory = null;
            String chosenTab = null;
            if (requestedURL.length() > 0) {
              chosenTab = requestedURL.substring(1);
            }
            if (chosenTab == null || chosenTab.indexOf(".shtml") == -1) {
              chosenTab = "home.shtml";
            } else {
              for (ProjectCategory projectCategory : categoryList) {
                String categoryName = projectCategory.getDescription();
                String normalizedCategoryTab = projectCategory.getNormalizedCategoryName().concat(".shtml");
                if (normalizedCategoryTab.equals(chosenTab)) {
                  foundChosenTab = true;
                  chosenCategory = categoryName;
                }
              }
            }
            if (!foundChosenTab) {
              chosenTab = "home.shtml";
            }
            request.setAttribute("chosenTab", chosenTab);
            request.setAttribute("chosenCategory", chosenCategory);
          }
        }
      } catch (Exception e) {
        LOG.error("Global items error", e);
      } finally {
        this.freeConnection(context, db);
      }
    }

    // The user is not going to login, so verify login
    if (!s.startsWith("Login")) {
      if (userSession == null || ceSession == null) {
        // boot them off now
        LOG.debug("Security failed.");
        LoginBean failedSession = new LoginBean();
        failedSession.addError("actionError", "* Please login, your session has expired");
        failedSession.checkURL(request);
        request.setAttribute("LoginBean", failedSession);
        request.removeAttribute("PageLayout");
        return "SecurityCheck";
      } else {
        // The user should have a valid login now, so let them proceed
        LOG.debug("Security passed.");
      }
    }
    // Generate user's global items
    if (userSession != null && userSession.getId() > 0) {
      Connection db = null;
      try {
        db = getConnection(context, request);
        // TODO: Implement cache since every hit needs this list
        if (db != null) {
          // Check the # of invitations
          int invitationCount = InvitationList.queryCount(db, userSession.getId());
          request.setAttribute(Constants.REQUEST_INVITATION_COUNT, String.valueOf(invitationCount));

          int newMailCount = PrivateMessageList.queryRolledupUnreadCountForUser(db, userSession.getId());
          request.setAttribute(Constants.REQUEST_PRIVATE_MESSAGE_COUNT, String.valueOf(newMailCount));
          // NOTE: removed because not currently used
          // Check the number of what's new
//          int whatsNewCount = ProjectUtils.queryWhatsNewCount(db, userSession.getId());
//          request.setAttribute(Constants.REQUEST_WHATS_NEW_COUNT, String.valueOf(whatsNewCount));
          // Check the number of assignments
//          int whatsAssignedCount = ProjectUtils.queryWhatsAssignedCount(db, userSession.getId());
//          request.setAttribute(Constants.REQUEST_WHATS_ASSIGNED_COUNT, String.valueOf(whatsAssignedCount));
//          int projectCount = ProjectUtils.queryMyProjectCount(db, userSession.getId());
//          request.setAttribute(Constants.REQUEST_MY_PROJECT_COUNT, String.valueOf(projectCount));
        }
      } catch (Exception e) {
        LOG.error("User's global items error", e);
      } finally {
        this.freeConnection(context, db);
      }
    }
    return null;
  }

  /**
   * Description of the Method
   *
   * @param servlet  Description of the Parameter
   * @param request  Description of the Parameter
   * @param response Description of the Parameter
   * @return Description of the Return Value
   */
  public String endRequest(Servlet servlet, HttpServletRequest request, HttpServletResponse response) {
    return null;
  }

  /**
   * Gets the connection attribute of the SecurityHook object
   *
   * @param context Description of the Parameter
   * @param request Description of the Parameter
   * @return The connection value
   * @throws SQLException Description of the Exception
   */
  protected Connection getConnection(ServletContext context, HttpServletRequest request) throws SQLException {
    ConnectionPool sqlDriver = (ConnectionPool) context.getAttribute("ConnectionPool");
    ConnectionElement ce = (ConnectionElement) request.getSession().getAttribute("ConnectionElement");
    if (sqlDriver == null || ce == null) {
      return null;
    }
    return sqlDriver.getConnection(ce);
  }

  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @param db      Description of the Parameter
   */
  protected void freeConnection(ServletContext context, Connection db) {
    if (db != null) {
      ConnectionPool sqlDriver = (ConnectionPool) context.getAttribute("ConnectionPool");
      sqlDriver.free(db);
    }
    db = null;
  }
}

