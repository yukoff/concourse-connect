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
package com.concursive.connect.web.modules.login.actions;

import com.concursive.commons.http.RequestUtils;
import com.concursive.commons.text.StringUtils;
import com.concursive.commons.web.mvc.actions.ActionContext;
import com.concursive.connect.Constants;
import com.concursive.connect.config.ApplicationPrefs;
import com.concursive.connect.web.controller.actions.GenericAction;
import com.concursive.connect.web.modules.login.auth.ILoginAuthenticator;
import com.concursive.connect.web.modules.login.auth.LoginAuthenticatorFactory;
import com.concursive.connect.web.modules.login.beans.LoginBean;
import com.concursive.connect.web.modules.login.dao.User;

/**
 * Controls user login
 *
 * @author matt rajkowski
 * @created May 7, 2003
 */
public final class Login extends GenericAction {

  public String executeCommandDetectMobile(ActionContext context) {
    return "DetectMobileOK";
  }

  public String executeCommandDefault(ActionContext context) {
    // If SSL, then redirect to SSL
    ApplicationPrefs prefs = getApplicationPrefs(context);
    boolean sslEnabled = "true".equals(getPref(context, "SSL"));
    if (sslEnabled && !"https".equals(context.getRequest().getScheme())) {
      String redirectTo = "";
      if (StringUtils.hasText(context.getRequest().getParameter("redirectTo"))) {
        redirectTo = "?redirectTo=" + context.getRequest().getParameter("redirectTo");
      }
      String url = ("https://" + RequestUtils.getServerUrl(prefs.get(ApplicationPrefs.WEB_URL), prefs.get(ApplicationPrefs.WEB_PORT), context.getRequest())) + "/login" + redirectTo;
      context.getRequest().setAttribute("redirectTo", url);
      return "Redirect301";
    }
    // Show the login form
    return "LoginFormOK";
  }

  /**
   * Determine the type of security error occurred
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandSecurity(ActionContext context) {
    if ("true".equals(getPref(context, "PORTAL"))) {
      return "LogoutPortalOK";
    } else {
      return "LogoutOK";
    }
  }

  /**
   * Determine the type of permission error that occurred
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandPermission(ActionContext context) {
    User userSession = (User) context.getRequest().getSession().getAttribute(Constants.SESSION_USER);
    if (userSession == null || userSession.getId() < 0) {
      //User is not logged in so ask to login
      LoginBean loginBean = (LoginBean) context.getFormBean();
      loginBean.addError("actionError", "* Please login, your session has expired");
      loginBean.checkURL(context.getRequest());
      return "LogoutOK";
    } else {
      //User is logged in, but doesn't have access
      return "UserPermissionError";
    }
  }

  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandLogin(ActionContext context) {
    // If SSL, then redirect to SSL
    ApplicationPrefs prefs = getApplicationPrefs(context);
    boolean sslEnabled = "true".equals(getPref(context, "SSL"));
    if (sslEnabled && !"https".equals(context.getRequest().getScheme())) {
      String redirectTo = "";
      if (StringUtils.hasText(context.getRequest().getParameter("redirectTo"))) {
        redirectTo = "?redirectTo=" + context.getRequest().getParameter("redirectTo");
      }
      String url = ("https://" + RequestUtils.getServerUrl(prefs.get(ApplicationPrefs.WEB_URL), prefs.get(ApplicationPrefs.WEB_PORT), context.getRequest())) + "/login" + redirectTo;
      context.getRequest().setAttribute("redirectTo", url);
      return "Redirect301";
    }
    // Authenticate the login
    LoginAuthenticatorFactory authenticatorFactory = LoginAuthenticatorFactory.getInstance(context);
    ILoginAuthenticator authenticator = authenticatorFactory.getLoginAuthenticator(context);
    return authenticator.authenticateLogin(context);
  }

  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandLogout(ActionContext context) {
    LoginAuthenticatorFactory authenticatorFactory = LoginAuthenticatorFactory.getInstance(context);
    ILoginAuthenticator authenticator = authenticatorFactory.getLoginAuthenticator(context);
    return authenticator.authenticateLogout(context);
  }
}

