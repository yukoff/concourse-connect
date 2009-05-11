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

package com.concursive.connect.web.utils;

import com.concursive.connect.Constants;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import com.concursive.connect.web.portal.PortalUtils;

import javax.portlet.PortletRequest;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpSession;
import java.util.StringTokenizer;

/**
 * Reusable utilities for checking permissions within the action scope
 *
 * @author matt rajkowski
 * @created April 4, 2008
 */
public class PermissionUtils {

  private static final String ALL = "all";
  private static final String NONE = "none";
  private static final String ANY = "any";

  public static boolean hasPermissionToAction(ServletRequest request, HttpSession session, String permission) {
    return hasPermissionToAction(request, session, permission, ALL, null);
  }

  public static boolean hasPermissionToAction(ServletRequest request, HttpSession session, String permission, String includeIf, String objectName) {
    // Use the object name specified, or use the default here
    String thisObjectName = objectName;
    if (thisObjectName == null) {
      thisObjectName = "project";
    }

    try {
      // Find the project to check for permissions...
      Project thisProject = null;
      // Check the current portlet first
      PortletRequest renderRequest = (PortletRequest) request.getAttribute(org.apache.pluto.tags.Constants.PORTLET_REQUEST);
      if (renderRequest != null) {
        thisProject = (Project) renderRequest.getAttribute(thisObjectName);
        if (thisProject == null && objectName == null) {
          thisProject = PortalUtils.getProject(renderRequest);
        }
      }
      // Check the request object
      if (thisProject == null) {
        thisProject = (Project) request.getAttribute(thisObjectName);
      }
      // Deny if not found
      if (thisProject == null) {
        return false;
      }

      // Check this user's permissions
      User thisUser = null;
      // Check the portlet
      if (thisUser == null && renderRequest != null) {
        thisUser = PortalUtils.getUser(renderRequest);
      }
      // Check the session object
      if (thisUser == null) {
        thisUser = (User) session.getAttribute(Constants.SESSION_USER);
      }
      // Deny if not found
      if (thisUser == null) {
        return false;
      }

      // Multiple permissions to check
      boolean doCheck = true;
      String thisPermission = null;
      StringTokenizer st = new StringTokenizer(permission, ",");
      while (st.hasMoreTokens() || doCheck) {
        doCheck = false;
        if (st.hasMoreTokens()) {
          thisPermission = st.nextToken();
        } else {
          thisPermission = permission;
        }
        if (NONE.equals(includeIf)) {
          if (ProjectUtils.hasAccess(thisProject.getId(), thisUser, thisPermission)) {
            return false;
          }
        } else if (ANY.equals(includeIf)) {
          if (ProjectUtils.hasAccess(thisProject.getId(), thisUser, thisPermission)) {
            return true;
          }
        } else {
          if (!ProjectUtils.hasAccess(thisProject.getId(), thisUser, thisPermission)) {
            return false;
          }
        }
      }
      // If the above didn't trigger, then go with the default
      if (NONE.equals(includeIf)) {
        return true;
      } else if (ANY.equals(includeIf)) {
        return false;
      } else {
        return true;
      }
    } catch (Exception e) {
      System.out.println("PermissionUtils-> Permission Error: " + e.getMessage());
      return false;
    }
  }
}