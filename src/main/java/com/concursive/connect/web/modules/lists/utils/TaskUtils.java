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
package com.concursive.connect.web.modules.lists.utils;

import com.concursive.commons.db.DatabaseUtils;
import com.concursive.connect.Constants;
import com.concursive.connect.web.modules.lists.dao.Task;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Class to manipulate task objects
 *
 * @author lorraine bittner
 * @created Sep 9, 2008
 */
public class TaskUtils {
  public static String getLinkItemUrl(User user, String ctx, Task t) {
    if (t.getLinkModuleId() == -1 || t.getLinkItemId() == -1) {
      return null;
    } else if (t.getLinkModuleId() == Constants.TASK_CATEGORY_PROJECTS) {
      Project p = ProjectUtils.loadProject(t.getLinkItemId());
      // Only show links that the user has access to...
      if (ProjectUtils.hasAccess(t.getLinkItemId(), user, "project-profile-view")) {
        return ctx + "/show/" + p.getUniqueId();
      }
    }
    return null;
  }

  public static void removeLinkedItemId(Connection db, int linkModuleId, int linkItemId) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "UPDATE task " +
            "SET link_module_id = ?, link_item_id = ? " +
            "WHERE link_module_id = ? AND link_item_id = ?"
    );
    int i = 0;
    DatabaseUtils.setInt(pst, ++i, -1);
    DatabaseUtils.setInt(pst, ++i, -1);
    pst.setInt(++i, linkModuleId);
    pst.setInt(++i, linkItemId);
    pst.executeUpdate();
    pst.close();
  }
}
