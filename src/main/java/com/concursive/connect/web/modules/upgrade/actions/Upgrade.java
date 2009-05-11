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

package com.concursive.connect.web.modules.upgrade.actions;

import com.concursive.commons.web.mvc.actions.ActionContext;
import com.concursive.connect.config.ApplicationPrefs;
import com.concursive.connect.config.ApplicationVersion;
import com.concursive.connect.web.controller.actions.GenericAction;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.upgrade.utils.UpgradeUtils;

import java.sql.Connection;
import java.util.ArrayList;

/**
 * Actions that facilitate upgrading an installation of Dark Horse CRM
 *
 * @author matt rajkowski
 * @version $Id$
 * @created June 16, 2004
 */
public class Upgrade extends GenericAction {

  /**
   * Prepares instruction page
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandDefault(ActionContext context) {
    return "NeedUpgradeOK";
  }


  /**
   * Checks to see what version is installed and what the version should be
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandCheck(ActionContext context) {
    if (!isAllowedToUpgade(context)) {
      return "NeedUpgradeOK";
    }
    // Check version info
    ApplicationPrefs prefs = (ApplicationPrefs) context.getServletContext().getAttribute("applicationPrefs");
    if (ApplicationVersion.isOutOfDate(prefs)) {
      // All ok
      context.getRequest().setAttribute("status", "1");
    } else {
      // Something needs updating
      context.getRequest().setAttribute("status", "0");
    }
    context.getRequest().setAttribute("installedVersion", ApplicationVersion.getInstalledVersion(prefs));
    context.getRequest().setAttribute("newVersion", ApplicationVersion.VERSION);
    return "CheckOK";
  }


  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public synchronized String executeCommandPerformUpgrade(ActionContext context) {
    if (!isAllowedToUpgade(context)) {
      return "NeedUpgradeOK";
    }
    ApplicationPrefs prefs = (ApplicationPrefs) context.getServletContext().getAttribute("applicationPrefs");
    if (ApplicationVersion.isOutOfDate(prefs)) {
      // Display upgrade information
      context.getRequest().setAttribute("installedVersion", ApplicationVersion.getInstalledVersion(prefs));
      context.getRequest().setAttribute("newVersion", ApplicationVersion.VERSION);
      // Setup the connection pool
      Connection db = null;
      try {
        // Get a connection from the connection pool for this user
        db = this.getConnection(context, true);

        // Execute any upgrades that haven't been run
        ArrayList<String> installLog = UpgradeUtils.performUpgrade(db, context.getServletContext());

        // Create a log of upgrade events
        context.getRequest().setAttribute("installLog", installLog);

        // Save the preferences which stores the current version
        if (!prefs.save()) {
          context.getRequest().setAttribute("errorMessage", "No write permission on file library, build.properties");
          return "UpgradeERROR";
        }
      } catch (Exception e) {
        context.getRequest().setAttribute("errorMessage", e.getMessage());
        return "UpgradeERROR";
      } finally {
        //Always free the database connection
        this.freeConnection(context, db);
      }
    }
    // Startup the services now that the data is updated
    prefs.initializeServices(context.getServletContext());
    // Remove the user's session and force them to login again
    context.getSession().removeAttribute("UPGRADEOK");
    context.getSession().setAttribute("precompile", "true");
    return "UpgradeOK";
  }


  /**
   * Gets the administrator attribute of the Upgrade object
   *
   * @param context Description of the Parameter
   * @return The administrator value
   */
  private boolean isAllowedToUpgade(ActionContext context) {
    User thisUser = getUser(context);
    if (thisUser != null) {
      String status = (String) context.getSession().getAttribute("UPGRADEOK");
      return "UPGRADEOK".equals(status);
    }
    return false;
  }
}
