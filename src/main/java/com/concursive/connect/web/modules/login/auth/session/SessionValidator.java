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
package com.concursive.connect.web.modules.login.auth.session;


import com.concursive.commons.db.ConnectionElement;
import com.concursive.commons.db.ConnectionPool;
import com.concursive.commons.http.CookieUtils;
import com.concursive.connect.Constants;
import com.concursive.connect.config.ApplicationPrefs;
import com.concursive.connect.web.modules.login.beans.LoginBean;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.login.utils.UserUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Connection;


/**
 * Description of the SessionValidator
 *
 * @author Artem.Zakolodkin
 * @created Jul 19, 2007
 */
public class SessionValidator implements ISessionValidator {

  private static Log LOG = LogFactory.getLog(SessionValidator.class);

  /**
   * Follow the current session validation schema and determine if there is a
   * valid session for the user. If there is a valid session, return the
   * <code>User</code> associated with that session, otherwise, return
   * <i>null</i>.
   *
   * @param request -
   *                The servlet request as provided by the
   *                <code>ControllerServlet</code>.
   * @return A valid <code>User</code> upon successful validation.<br>
   *         <code>Null</code> upon validation failure
   */
  public User validateSession(ServletContext context, HttpServletRequest request, HttpServletResponse response) {
    User thisUser = (User) request.getSession(false).getAttribute(Constants.SESSION_USER);
    LOG.debug("Has user session: " + (thisUser != null));
    if (thisUser == null || !thisUser.isLoggedIn()) {
      LOG.debug("Checking for cookie...");
      // Check cookie for session info and generate a logged in user
      String guid = CookieUtils.getCookieValue(request, Constants.COOKIE_USER_GUID);
      if (guid == null) {
        LOG.debug("No cookie found.");
        return (thisUser == null ? null : thisUser);
      }
      LOG.debug("Cookie found with guid: " + guid);
      // Retrieve prefs to see if user with guid exists
      ApplicationPrefs prefs = (ApplicationPrefs) context.getAttribute(Constants.APPLICATION_PREFS);
      // Connection info
      ConnectionElement ce = new ConnectionElement();
      ce.setDriver(prefs.get("SITE.DRIVER"));
      ce.setUrl(prefs.get("SITE.URL"));
      ce.setUsername(prefs.get("SITE.USER"));
      ce.setPassword(prefs.get("SITE.PASSWORD"));
      ConnectionPool sqlDriver = (ConnectionPool) context.getAttribute(Constants.CONNECTION_POOL);
      Connection db = null;
      try {
        db = sqlDriver.getConnection(ce);
        // Load the user record from the guid
        thisUser = UserUtils.loadUserFromGuid(db, guid);
        if (thisUser != null) {
          // Track the login
          thisUser.updateLogin(db, request, prefs, null);
          thisUser.setBrowserType(request.getHeader("USER-AGENT"));
          // Apply defaults
          UserUtils.createLoggedInUser(thisUser, db, prefs, context);
          // Extend the cookie
          Cookie userCookie = new Cookie(Constants.COOKIE_USER_GUID, UserUtils.generateGuid(thisUser));
          userCookie.setPath("/");
          // 14 day cookie
          userCookie.setMaxAge(14 * 24 * 60 * 60);
          response.addCookie(userCookie);
        }
      } catch (Exception e) {
        thisUser = null;
        e.printStackTrace();
      } finally {
        if (db != null) {
          sqlDriver.free(db);
        }
      }
      // Add to session
      request.getSession().setAttribute(Constants.SESSION_USER, thisUser);
      request.getSession().setAttribute(Constants.SESSION_CONNECTION_ELEMENT, ce);
    }
    return thisUser;
  }

  public String sessionError(HttpServletRequest request, HttpServletResponse response, String message) {
    LoginBean failedSession = new LoginBean();
    failedSession.setMessage(message);
    request.setAttribute("LoginBean", failedSession);
    LOG.debug(message);
    return "SecurityCheck";
  }
}
