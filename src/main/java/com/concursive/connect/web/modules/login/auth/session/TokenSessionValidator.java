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
import com.concursive.connect.Constants;
import com.concursive.connect.cache.utils.CacheUtils;
import com.concursive.connect.config.ApplicationPrefs;
import com.concursive.connect.config.ApplicationVersion;
import com.concursive.connect.web.modules.login.beans.LoginBean;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.login.utils.UserUtils;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

/**
 * Description of the SessionValidator
 *
 * @author Artem.Zakolodkin
 * @created Jul 19, 2007
 */
public class TokenSessionValidator implements ISessionValidator {

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
    User userSession = (User) request.getSession().getAttribute(Constants.SESSION_USER);
    if (userSession == null) {
      Ehcache tokenCache = CacheUtils.getCache(Constants.SESSION_AUTHENTICATION_TOKEN_CACHE);
      // No cache, no session carryover...
      if (tokenCache == null) {
        // No cache indicates some other issue, so we can't validate the carryover regardless of tokens.
        return null;
      }
      // Extract the authentication code from the URL.
      String s = request.getServletPath();
      int sessionIdStart = s.lastIndexOf("SessionId=");
      s = s.substring(sessionIdStart);
      // If not setup then go to setup
      if (!s.startsWith("SessionId=")) {
        return null;
      }
      // Now get the token...
      int tokenStart = s.lastIndexOf("=");
      // Token is from the end of "SessionId=" to then end of the request
      s = s.substring(tokenStart + 1);

      //String sessionId = request.getParameter("SessionId");

      // Is the authentication code in the cache?
      Element element = tokenCache.get(s);
      if (element != null) {
        String userName = (String) element.getObjectValue();
        // Remove the token...
        tokenCache.remove(s);
        return createUserSession(context, userName);
      } else {
        return null;
      }
    }
    return userSession;
  }

  private User createUserSession(ServletContext context, String UserId) {
    // Retrieve prefs
    ApplicationPrefs prefs = (ApplicationPrefs) context.getAttribute("applicationPrefs");
    // Connection info
    ConnectionElement ce = new ConnectionElement();
    ce.setDriver(prefs.get("SITE.DRIVER"));
    ce.setUrl(prefs.get("SITE.URL"));
    ce.setUsername(prefs.get("SITE.USER"));
    ce.setPassword(prefs.get("SITE.PASSWORD"));
    ConnectionPool sqlDriver = (ConnectionPool) context.getAttribute("ConnectionPool");
    if (sqlDriver == null) {
      if (System.getProperty("DEBUG") != null) {
        System.out.println("Connect321SessionValidator-> Database attribute not found...");
      }
      return null;
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
              "WHERE lower(username) = ? ");
      pst.setString(++i, UserId.toLowerCase());
      ResultSet rs = pst.executeQuery();
      if (rs.next()) {
        thisUser = new User(rs);
      }
      rs.close();
      pst.close();
      // Further validate user fields
      if (thisUser == null) {
        // Do nothing at this time...
      } else if (!thisUser.getEnabled()) {
        thisUser = null;
      } else if (thisUser.getExpiration() != null && (new Timestamp(System.currentTimeMillis())).after(thisUser.getExpiration())) {
        thisUser = null;
      }
      // Valid user -- Log the user in
      if (thisUser != null) {
        UserUtils.createLoggedInUser(thisUser, db, prefs, context);
        //hasInvitations = (InvitationList.queryCount(db, thisUser.getId()) > 0);
        //thisUser.queryRecentlyAccessedProjects(db);
        // Determine content editing capability
        thisUser.getWebSiteLanguageList().setMemberId(thisUser.getId());
        thisUser.getWebSiteLanguageList().buildList(db);
      }
    } catch (Exception e) {
      e.printStackTrace(System.out);
    } finally {
      sqlDriver.free(db);
    }
    return thisUser;
  }

  public String sessionError(HttpServletRequest request, HttpServletResponse response, String message) {
    LoginBean failedSession = new LoginBean();
    request.setAttribute("LoginBean", failedSession);
    if (System.getProperty("DEBUG") != null) {
      System.out.println(message);
    }
    return "SecurityCheck";
  }
}
