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
package com.concursive.connect.web.modules.profile.workflow;

import com.concursive.commons.workflow.ComponentContext;
import com.concursive.commons.workflow.ComponentInterface;
import com.concursive.commons.workflow.ObjectHookComponent;
import com.concursive.connect.Constants;
import com.concursive.connect.web.modules.activity.dao.ProjectHistory;
import com.concursive.connect.web.modules.activity.dao.ProjectHistoryList;
import com.concursive.connect.web.modules.documents.dao.FileItem;
import com.concursive.connect.web.modules.documents.dao.FileItemList;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.login.utils.UserUtils;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import com.concursive.connect.web.modules.wiki.utils.WikiLink;

import java.sql.Connection;

/**
 * Records an image share event within a particular project
 *
 * @author Ananth
 * @created Feb 20, 2009
 */
public class SaveProjectShareImageEvent extends ObjectHookComponent implements ComponentInterface {
  public final static String HISTORY_TEXT = "history.text";
  public final static String USER_PROFILE_HISTORY_TEXT = "history.update-user-profile.text";

  public String getDescription() {
    return "Records the event when the user shares an image with a particular project profile..";
  }

  public boolean execute(ComponentContext context) {
    boolean result = false;
    Connection db = null;

    try {
      db = getConnection(context);

      FileItemList files = (FileItemList) context.getThisObject();

      for (FileItem fileItem : files) {
        if (fileItem.getLinkModuleId() == Constants.PROJECT_IMAGE_FILES) {
          // load the user that submitted the image
          User user = UserUtils.loadUser(fileItem.getEnteredBy());
          Project userProfile = ProjectUtils.loadProject(user.getProfileProjectId());

          // load the project profile
          Project profile = ProjectUtils.loadProject(fileItem.getLinkItemId());

          // Prepare the wiki links
          context.setParameter("user", WikiLink.generateLink(userProfile));
          context.setParameter("profile", WikiLink.generateLink(profile));

          // Insert the history
          ProjectHistory history = new ProjectHistory();
          history.setEnteredBy(user.getId());
          history.setProjectId(fileItem.getLinkItemId());
          history.setLinkObject(ProjectHistoryList.IMAGE_OBJECT);
          history.setEventType(ProjectHistoryList.SHARE_PROFILE_IMAGE_EVENT);
          history.setLinkItemId(fileItem.getId());

          if (userProfile.getId() == profile.getId()) {
            // The user is adding an image to their profile
            history.setDescription(context.getParameter(USER_PROFILE_HISTORY_TEXT));
          } else {
            // The user is adding an image to another profile
            history.setDescription(context.getParameter(HISTORY_TEXT));
          }
          history.insert(db);
        }
      }
      result = true;
    } catch (Exception e) {
      e.printStackTrace(System.out);
    } finally {
      freeConnection(context, db);
    }
    return result;
  }
}
