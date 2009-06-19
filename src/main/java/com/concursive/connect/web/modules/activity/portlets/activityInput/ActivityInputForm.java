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
package com.concursive.connect.web.modules.activity.portlets.activityInput;

import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import com.concursive.connect.web.portal.IPortletViewer;
import static com.concursive.connect.web.portal.PortalUtils.findProject;
import static com.concursive.connect.web.portal.PortalUtils.getUser;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

/**
 * Activity input form
 *
 * @author Kailash Bhoopalam
 * @created March 31, 2009
 */
public class ActivityInputForm implements IPortletViewer {
  // Pages
  private static final String VIEW_PAGE = "/portlets/activity_input/activity_input_form-view.jsp";

  // Preferences
  private static final String PREF_TITLE = "title";
  private static final String ALLOW_USERS = "allowUsers";

  // Object Results
  private static final String TITLE = "title";

  public String doView(RenderRequest request, RenderResponse response) throws Exception {
    // General display preferences
    request.setAttribute(TITLE, request.getPreferences().getValue(PREF_TITLE, ""));

    // Determine the project to store the event against
    Project project = findProject(request);

    // Check the user's permissions
    User user = getUser(request);
    if (Boolean.parseBoolean(request.getPreferences().getValue(ALLOW_USERS, "false"))) {
      if (!user.isLoggedIn()) {
        return null;
      }
    } else if (!ProjectUtils.hasAccess(project.getId(), user, "project-profile-activity-add")) {
      return null;
    }

    return VIEW_PAGE;
  }
}
