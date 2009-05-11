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

import com.concursive.commons.text.Template;
import com.concursive.commons.workflow.ComponentContext;
import com.concursive.commons.workflow.ComponentInterface;
import com.concursive.commons.workflow.ObjectHookComponent;
import com.concursive.connect.web.modules.activity.dao.ProjectHistory;
import com.concursive.connect.web.modules.activity.dao.ProjectHistoryList;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.login.utils.UserUtils;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import com.concursive.connect.web.modules.wiki.utils.WikiLink;

import java.sql.Connection;

/**
 * Records a project profile's update events..
 *
 * @author Ananth
 * @created Feb 20, 2009
 */
public class SaveProjectUpdateEvent extends ObjectHookComponent implements ComponentInterface {
  public final static String HISTORY_UPDATE_USER_PROFILE_TEXT = "history.update-user-profile.text";
  public final static String HISTORY_BECOME_PROFILE_OWNER_TEXT = "history.become-profile-owner.text";
  public final static String HISTORY_UPDATE_PROFILE_TEXT = "history.update-profile.text";

  public String getDescription() {
    return "Records a project profile's update events";
  }

  public boolean execute(ComponentContext context) {
    boolean result = false;
    Connection db = null;

    try {
      db = getConnection(context);

      Project thisProject = (Project) context.getThisObject();
      Project prevProject = (Project) context.getPreviousObject();

      // load the user that updated the project
      User user = UserUtils.loadUser(thisProject.getModifiedBy());
      Project userProfile = ProjectUtils.loadProject(user.getProfileProjectId());

      // user is updating his/her own profile
      if (userProfile.getId() == thisProject.getId()) {
        // Prepare the wiki links
        context.setParameter("user", WikiLink.generateLink(userProfile));

        // Insert the history
        ProjectHistory history = new ProjectHistory();
        history.setEnteredBy(user.getId());
        history.setProjectId(userProfile.getId());
        history.setLinkObject(ProjectHistoryList.PROFILE_OBJECT);
        history.setEventType(ProjectHistoryList.UPDATE_USER_PROFILE_EVENT);
        history.setLinkItemId(userProfile.getId());
        history.setDescription(context.getParameter(HISTORY_UPDATE_USER_PROFILE_TEXT));
        history.insert(db);
      } else {
        // user just became the owner of this project
        if (prevProject.getOwner() != thisProject.getOwner()
            && thisProject.getOwner() == user.getId()) {
          // load the user who is now the owner of this listing
          User owner = UserUtils.loadUser(thisProject.getOwner());
          Project ownerProfile = ProjectUtils.loadProject(user.getProfileProjectId());

          // Prepare the wiki links
          context.setParameter("user", WikiLink.generateLink(ownerProfile));
          context.setParameter("profile", WikiLink.generateLink(thisProject));

          // Insert the history
          ProjectHistory history = new ProjectHistory();
          history.setEnteredBy(owner.getId());
          history.setProjectId(thisProject.getId());
          history.setLinkObject(ProjectHistoryList.PROFILE_OBJECT);
          history.setEventType(ProjectHistoryList.BECOME_PROFILE_OWNER_EVENT);
          history.setLinkItemId(thisProject.getId());

          //Prepare the wiki links
          Template template = new Template();
          template.addParseElement("user", WikiLink.generateLink(ownerProfile));
          template.addParseElement("profile", WikiLink.generateLink(thisProject));
          history.setDescription(context.getParameter(HISTORY_BECOME_PROFILE_OWNER_TEXT));
          history.insert(db);
        } else {
          // The user updated this listing

          // Prepare the wiki links
          context.setParameter("user", WikiLink.generateLink(userProfile));
          context.setParameter("profile", WikiLink.generateLink(thisProject));

          // Insert the history
          ProjectHistory history = new ProjectHistory();
          history.setEnteredBy(user.getId());
          history.setProjectId(thisProject.getId());
          history.setLinkObject(ProjectHistoryList.PROFILE_OBJECT);
          history.setEventType(ProjectHistoryList.UPDATE_PROFILE_EVENT);
          history.setLinkItemId(thisProject.getId());
          history.setDescription(context.getParameter(HISTORY_UPDATE_PROFILE_TEXT));
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
