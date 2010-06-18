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
package com.concursive.connect.web.modules.login.auth;

import com.concursive.commons.codec.PasswordHash;
import com.concursive.commons.db.ConnectionElement;
import com.concursive.commons.db.ConnectionPool;
import com.concursive.commons.web.mvc.actions.ActionContext;
import com.concursive.connect.Constants;
import com.concursive.connect.config.ApplicationPrefs;
import com.concursive.connect.config.ApplicationVersion;
import com.concursive.connect.web.controller.actions.GenericAction;
import com.concursive.connect.web.modules.login.beans.LoginBean;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.login.utils.UserUtils;
import com.concursive.connect.web.modules.members.dao.InvitationList;
import com.concursive.connect.web.modules.translation.dao.WebSiteLanguageList;
import com.concursive.connect.web.utils.ClientType;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

/**
 * Description of the LoginAuthenticator
 *
 * @author Artem.Zakolodkin
 * @created Jul 19, 2007
 */
public class LoginAuthenticator extends GenericAction implements ILoginAuthenticator {

  public String authenticateLogin(ActionContext context) {
    if (System.getProperty("DEBUG") != null) {
      System.out.println("Login-> Logging in...");
    }
    LoginBean loginBean = (LoginBean) context.getFormBean();
    loginBean.checkURL(context.getRequest());
    if (!loginBean.isValid()) {
      loginBean.addError("actionError", "Form must be completely filled out");
      return "LoginRetry";
    }
    // Retrieve prefs
    ApplicationPrefs prefs = (ApplicationPrefs) context.getServletContext().getAttribute("applicationPrefs");
    // Connection info
    ConnectionElement ce = new ConnectionElement();
    ce.setDriver(prefs.get("SITE.DRIVER"));
    ce.setUrl(prefs.get("SITE.URL"));
    ce.setUsername(prefs.get("SITE.USER"));
    ce.setPassword(prefs.get("SITE.PASSWORD"));
    ConnectionPool sqlDriver =
        (ConnectionPool) context.getServletContext().getAttribute("ConnectionPool");
    if (sqlDriver == null) {
      loginBean.addError("actionError", "Access not allowed due to system error!");
      if (System.getProperty("DEBUG") != null) {
        System.out.println("Login-> Database attribute not found...");
      }
      return "LoginRetry";
    }
    User thisUser = null;
    Connection db = null;
    boolean hasInvitations = false;
    boolean upgradeMode = false;
    try {
      db = sqlDriver.getConnection(ce);
      if (System.getProperty("DEBUG") != null) {
        System.out.println("Login-> Got database connection...");
      }
      // Check to see if system is upgraded
      if (ApplicationVersion.isOutOfDate(prefs)) {
        upgradeMode = true;
      }
      // Check user credentials
      int i = 0;
      PreparedStatement pst = db.prepareStatement(
          "SELECT u.*, d.description AS department, p.projecttextid " +
              "FROM users u " +
              "LEFT JOIN departments d ON (u.department_id = d.code) " +
              "LEFT JOIN projects p ON (u.profile_project_id = p.project_id) " +
              "WHERE lower(username) = ? " +
              "AND (password = ? OR temporary_password = ?) ");
      pst.setString(++i, loginBean.getUsername().toLowerCase());
      pst.setString(++i, PasswordHash.encrypt(loginBean.getPassword()));
      pst.setString(++i, PasswordHash.encrypt(loginBean.getPassword()));
      ResultSet rs = pst.executeQuery();
      if (rs.next()) {
        thisUser = new User(rs);
      }
      rs.close();
      pst.close();
      // Further validate user fields
      if (thisUser == null) {
        loginBean.addError("actionError", "* Access denied: Invalid user or password.");
      } else if (!thisUser.getEnabled()) {
        loginBean.addError("actionError", "* Access denied: Account not active");
        thisUser = null;
      } else if (thisUser.getExpiration() != null && (new Timestamp(System.currentTimeMillis())).after(thisUser.getExpiration())) {
        loginBean.addError("actionError", "* Access denied: Account expired");
        thisUser = null;
      }
      // Valid user -- Log the user in
      if (thisUser != null) {
        // Make sure the user info is valid for the application
        thisUser.updateLogin(db, context.getRequest(), prefs, loginBean.getPassword());
        thisUser.setBrowserType(context.getBrowser());
        // Apply any defaults
        UserUtils.createLoggedInUser(thisUser, db, prefs, context.getServletContext());
        // Check if this user can perform an upgrade
        if (upgradeMode && thisUser.getAccessAdmin()) {
          context.getSession().setAttribute("UPGRADEOK", "UPGRADEOK");
        }
        if (!upgradeMode) {
          // Check invitatons to redirect them on login
          hasInvitations = (InvitationList.queryCount(db, thisUser.getId(), thisUser.getProfileProjectId()) > 0);
          // Determine content editing capability
          thisUser.getWebSiteLanguageList().setMemberId(thisUser.getId());
          thisUser.getWebSiteLanguageList().buildList(db);
        }
        if (!thisUser.getRegistered()) {
          // If registration is required, then send user to the registration page to create a profile
          // before allowing auto-login via cookie
          return "LoginNeedsRegistrationOK";
        } else {
          if (loginBean.getAddCookie()) {
            // Set a cookie so user doesn't have to login again, at their request
            Cookie userCookie = new Cookie(Constants.COOKIE_USER_GUID, UserUtils.generateGuid(thisUser));
            userCookie.setPath("/");
            // 14 day cookie
            userCookie.setMaxAge(14 * 24 * 60 * 60);
            context.getResponse().addCookie(userCookie);
          } else {
            // Set a cookie so user doesn't have to login during an open browser session
            Cookie userCookie = new Cookie(Constants.COOKIE_USER_GUID, UserUtils.generateGuid(thisUser));
            userCookie.setPath("/");
            // Make it temporary
            userCookie.setMaxAge(-1);
            context.getResponse().addCookie(userCookie);

          }
        }
      }
    } catch (Exception e) {
      loginBean.addError("actionError", e.getMessage());
      e.printStackTrace(System.out);
    } finally {
      sqlDriver.free(db);
    }
    if (thisUser == null) {
      context.getSession().removeAttribute(Constants.SESSION_USER);
      context.getSession().removeAttribute(Constants.SESSION_CONNECTION_ELEMENT);
      return "LoginRetry";
    }
    context.getSession().setAttribute(Constants.SESSION_USER, thisUser);
    context.getSession().setAttribute(Constants.SESSION_CONNECTION_ELEMENT, ce);
    if (upgradeMode) {
      return ("PerformUpgradeOK");
    }
    /*
    if (hasInvitations) {
      return "LoginHasInvitationsOK";
    }
    */
    String redirectTo = context.getRequest().getParameter("redirectTo");
    if (redirectTo != null &&
        !redirectTo.startsWith("null") &&
        !redirectTo.startsWith("/login") &&
        !redirectTo.startsWith("/register") &&
        !redirectTo.startsWith("/page/register")) {
      return "RedirectURL";
    }
    return "LoginOK";
  }

  public String authenticateLogout(ActionContext context) {
    // Re-use the ClientType language
    String previousLanguage = null;
    ClientType clientType = (ClientType) context.getSession().getAttribute("clientType");
    if (clientType != null) {
      previousLanguage = clientType.getLanguage();
      // Let the portal know about the language choice
      if (previousLanguage != null) {
        WebSiteLanguageList webSiteLanguageList = (WebSiteLanguageList) context.getRequest().getAttribute("webSiteLanguageList");
        if (webSiteLanguageList != null) {
          if (!previousLanguage.equals(webSiteLanguageList.getDefault())) {
            context.getRequest().setAttribute("redirectParameter", "webSiteLanguage=" + previousLanguage);
          }
        }
      }
    }
    // Cleanup the cookie
    Cookie userCookie = new Cookie(Constants.COOKIE_USER_GUID, "");
    userCookie.setPath("/");
    userCookie.setMaxAge(0);
    context.getResponse().addCookie(userCookie);
    // Cleanup user resources
    HttpSession oldSession = context.getRequest().getSession(false);
    if (oldSession != null) {
      oldSession.removeAttribute(Constants.SESSION_USER);
      oldSession.removeAttribute(Constants.SESSION_CONNECTION_ELEMENT);
      oldSession.invalidate();
    }
    if ("true".equals(getPref(context, "PORTAL"))) {
      context.getRequest().setAttribute("redirectTo", "");
      context.getRequest().removeAttribute("PageLayout");
      return "Redirect301";
    } else {
      return "LogoutOK";
    }
  }
}
