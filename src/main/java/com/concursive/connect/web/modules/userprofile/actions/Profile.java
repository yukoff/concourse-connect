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

package com.concursive.connect.web.modules.userprofile.actions;

import com.concursive.commons.http.RequestUtils;
import com.concursive.commons.web.mvc.actions.ActionContext;
import com.concursive.connect.config.ApplicationPrefs;
import com.concursive.connect.web.controller.actions.GenericAction;
import com.concursive.connect.web.modules.login.beans.Password;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.login.utils.UserUtils;

import java.sql.Connection;
import java.text.NumberFormat;
import java.util.TimeZone;

/**
 * Description of the Class
 *
 * @author matt rajkowski
 * @version $Id$
 * @created November 20, 2003
 */
public final class Profile extends GenericAction {

  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandDefault(ActionContext context) {
    setMaximized(context);
    if (getUserId(context) < 0) {
      return "PermissionError";
    }
    if (!getUser(context).getAccessUserSettings()) {
      return "PermissionError";
    }
    Connection db = null;
    try {
      db = getConnection(context);
      User thisUser = UserUtils.loadUser(getUserId(context));
      context.getRequest().setAttribute("User", thisUser);
    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return ("SystemError");
    } finally {
      freeConnection(context, db);
    }
    return "DefaultOK";
  }


  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandModifyContactInformation(ActionContext context) {
    if (getUserId(context) < 0) {
      return "PermissionError";
    }
    if (!getUser(context).getAccessUserSettings()) {
      return "PermissionError";
    }
    Connection db = null;
    try {
      db = getConnection(context);
      User thisUser = new User(db, this.getGroupId(context), getUserId(context));
      context.getRequest().setAttribute("User", thisUser);
    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return ("SystemError");
    } finally {
      freeConnection(context, db);
    }
    return ("ModifyContactInformationOK");
  }


  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandSaveContactInformation(ActionContext context) {
    if (getUserId(context) < 0) {
      return "PermissionError";
    }
    if (!getUser(context).getAccessUserSettings()) {
      return "PermissionError";
    }
    User thisUser = (User) context.getFormBean();
    Connection db = null;
    try {
      if (thisUser.isValid()) {
        thisUser.setId(getUserId(context));
        db = getConnection(context);
      } else {
        this.processErrors(context, thisUser.getErrors());
        return ("SaveContactInformationError");
      }
    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return ("SystemError");
    } finally {
      freeConnection(context, db);
    }
    return ("SaveContactInformationOK");
  }


  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandChangePassword(ActionContext context) {
    if (getUserId(context) < 0) {
      return "PermissionError";
    }
    if (!getUser(context).getAccessUserSettings()) {
      return "PermissionError";
    }
    // If SSL, then redirect to SSL
    ApplicationPrefs prefs = getApplicationPrefs(context);
    boolean sslEnabled = "true".equals(getPref(context, "SSL"));
    if (sslEnabled && !"https".equals(context.getRequest().getScheme())) {
      String url = ("https://" + RequestUtils.getServerUrl(prefs.get(ApplicationPrefs.WEB_URL), prefs.get(ApplicationPrefs.WEB_PORT), context.getRequest())) + "/Password.do?command=ChangePassword";
      context.getRequest().setAttribute("redirectTo", url);
      return "Redirect301";
    }
    // Show the change password form
    return "ChangePasswordOK";
  }


  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandSavePassword(ActionContext context) {
    // If SSL, then redirect to SSL
    ApplicationPrefs prefs = getApplicationPrefs(context);
    boolean sslEnabled = "true".equals(getPref(context, "SSL"));
    if (sslEnabled && !"https".equals(context.getRequest().getScheme())) {
      String url = ("https://" + RequestUtils.getServerUrl(prefs.get(ApplicationPrefs.WEB_URL), prefs.get(ApplicationPrefs.WEB_PORT), context.getRequest())) + "/Password.do?command=ChangePassword";
      context.getRequest().setAttribute("redirectTo", url);
      return "Redirect301";
    }
    if (getUserId(context) < 0) {
      return "PermissionError";
    }
    if (!getUser(context).getAccessUserSettings()) {
      return "PermissionError";
    }
    Password password = (Password) context.getFormBean();
    if (password.isValid()) {
      Connection db = null;
      try {
        password.setUserId(getUserId(context));
        password.setUsername(getUser(context).getUsername());
        db = getConnection(context);
        password.update(db);
      } catch (Exception e) {
      } finally {
        freeConnection(context, db);
      }
    }
    if (password.hasErrors()) {
      processErrors(context, password.getErrors());
      return "SavePasswordError";
    }
    return "SavePasswordOK";
  }


  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandModifyLocation(ActionContext context) {
    if (getUserId(context) < 0) {
      return "PermissionError";
    }
    if (!getUser(context).getAccessUserSettings()) {
      return "PermissionError";
    }
    Connection db = null;
    try {
      db = getConnection(context);
      User thisUser = new User(db, this.getGroupId(context), getUserId(context));
      // Set a default time zone for user
      if (thisUser.getTimeZone() == null) {
        thisUser.setTimeZone(TimeZone.getDefault().getID());
      }
      // Set a default currency for user
      if (thisUser.getCurrency() == null) {
        thisUser.setCurrency(NumberFormat.getCurrencyInstance().getCurrency().getCurrencyCode());
      }
      context.getRequest().setAttribute("User", thisUser);
    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return ("SystemError");
    } finally {
      freeConnection(context, db);
    }
    return ("ModifyLocationOK");
  }


  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandSaveLocation(ActionContext context) {
    if (getUserId(context) < 0) {
      return "PermissionError";
    }
    if (!getUser(context).getAccessUserSettings()) {
      return "PermissionError";
    }
    String timeZone = context.getRequest().getParameter("timeZone");
    String currency = context.getRequest().getParameter("currency");
    String language = context.getRequest().getParameter("language");
    if (timeZone != null) {
      Connection db = null;
      try {
        db = getConnection(context);
        User.updateLocation(db, getUserId(context), timeZone, currency, language);
        getUser(context).setTimeZone(timeZone);
        getUser(context).setCurrency(currency);
        getUser(context).setLanguage(language);
      } catch (Exception e) {
        System.out.println("Profile-> ERROR: " + e.getMessage());
      } finally {
        freeConnection(context, db);
      }
    }
    return "SaveLocationOK";
  }

  public String executeCommandViewTime(ActionContext context) {
    return "ViewTimeOK";
  }
}
