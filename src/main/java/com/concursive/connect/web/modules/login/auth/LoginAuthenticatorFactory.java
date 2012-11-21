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

import com.concursive.commons.db.ConnectionElement;
import com.concursive.commons.db.ConnectionPool;
import com.concursive.commons.web.mvc.actions.ActionContext;
import com.concursive.connect.config.ApplicationPrefs;
import com.concursive.connect.web.modules.login.dao.AuthenticationClassesLookup;
import com.concursive.connect.web.modules.login.dao.AuthenticationClassesLookupList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;

/**
 * Serves as a tool for the creation of the appropriate LoginAuthenticator for
 * the given login method as set in the <i>master.properties</i> file
 *
 * @author Artem.Zakolodkin
 * @created Jul 19, 2007
 */
public class LoginAuthenticatorFactory {

  private static final Log LOG = LogFactory.getLog(LoginAuthenticatorFactory.class);
  private static String DEFAULT_LOGIN_AUTHENTICATOR = "com.concursive.connect.web.modules.login.auth.LoginAuthenticator";
  private static LoginAuthenticatorFactory instance = null;

  public static LoginAuthenticatorFactory getInstance(ActionContext context) {
    if (instance == null) {
      instance = new LoginAuthenticatorFactory();
    }
    return instance;
  }

  private LoginAuthenticatorFactory() {
  }

  /**
   * Determine the current login model based upon the LOGIN.MODE in the
   * <i>master.properties</i> file.
   *
   * @param context -
   *                Application Preferences.
   * @return An instance of an <code>ILoginAuthenticator</code> object, or
   *         <b>null</b>.
   */
  public ILoginAuthenticator getLoginAuthenticator(ActionContext context) {
    // Use the database to determine the class name, or use default
    ConnectionElement ce = (ConnectionElement) context.getSession().getAttribute("ConnectionElement");
    ConnectionPool sqlDriver = (ConnectionPool) context.getServletContext().getAttribute("ConnectionPool");
    Connection db = null;
    String className = null;
    try {
      String loginMode = ((ApplicationPrefs) context.getServletContext().getAttribute("applicationPrefs")).get(ApplicationPrefs.LOGIN_MODE);
      LOG.debug("Seeking Login Authenticator for Mode: " + loginMode);

      // Get connection
      db = sqlDriver.getConnection(ce);
      // Get specified authentication class
      AuthenticationClassesLookupList aclList = new AuthenticationClassesLookupList();
      aclList.setLoginMode(loginMode);
      aclList.buildList(db);
      if (aclList.size() == 0) {
        className = DEFAULT_LOGIN_AUTHENTICATOR;
      } else {
        AuthenticationClassesLookup acl = aclList.get(0);
        className = acl.getLoginAuthenticator();
      }
    } catch (Exception e) {
      // The table might not exist yet
      e.printStackTrace();
    } finally {
      if (db != null) {
        sqlDriver.free(db);
      }
    }
    // Instantiate the authenticator class
    try {
      if (className == null) {
        className = DEFAULT_LOGIN_AUTHENTICATOR;
      }
      Class clazz = Class.forName(className);
      return (ILoginAuthenticator) clazz.newInstance();
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }
}
