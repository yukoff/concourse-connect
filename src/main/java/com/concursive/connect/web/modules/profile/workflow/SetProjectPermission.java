/*
 * ConcourseConnect
 * Copyright 2010 Concursive Corporation
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

package com.concursive.connect.web.modules.profile.workflow;

import com.concursive.commons.text.StringUtils;
import com.concursive.commons.workflow.ComponentContext;
import com.concursive.commons.workflow.ComponentInterface;
import com.concursive.commons.workflow.ObjectHookComponent;
import com.concursive.connect.cache.utils.CacheUtils;
import com.concursive.connect.web.modules.profile.dao.Permission;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.utils.LookupList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;

/**
 * Set the specified permission to the specified role
 *
 * @author matt rajkowski
 * @created May 19, 2010
 */
public class SetProjectPermission extends ObjectHookComponent implements ComponentInterface {

  private static Log LOG = LogFactory.getLog(SetProjectPermission.class);

  public final static String PERMISSION = "permission";
  public final static String ROLE = "role";

  public String getDescription() {
    return "Set the specified permission to the specified role";
  }

  public boolean execute(ComponentContext context) {
    Project thisProject = (Project) context.getThisObject();

    // Make sure the object exists
    if (thisProject == null) {
      return false;
    }

    // Validate the parameters
    String permissionName = context.getParameter(PERMISSION);
    String role = context.getParameter(ROLE);
    if (!StringUtils.hasText(permissionName) || !StringUtils.hasText(role)) {
      if (!StringUtils.hasText(permissionName)) {
        LOG.error("Parameter permission: " + permissionName);
      }
      if (!StringUtils.hasText(role)) {
        LOG.error("Parameter role: " + role);
      }
      return false;
    }

    // Find the role
    LookupList roleList = CacheUtils.getLookupList("lookup_project_role");
    int userLevel = roleList.getIdFromValue(role);
    if (userLevel == -1) {
      LOG.error("User level not found for: " + role);
      return false;
    }

    // Update the permissions
    Permission permission = thisProject.getPermissions().get(permissionName);
    if (permission == null) {
      LOG.error("Permission not found: " + permissionName);
      return false;
    }

    if (permission.getUserLevel() != userLevel) {
      permission.setUserLevel(userLevel);
      // Update the listing
      Connection db = null;
      try {
        db = getConnection(context);
        permission.update(db);
      } catch (Exception e) {
        LOG.error("Could not update permission", e);
        return false;
      } finally {
        freeConnection(context, db);
      }
    }
    return true;
  }
}
