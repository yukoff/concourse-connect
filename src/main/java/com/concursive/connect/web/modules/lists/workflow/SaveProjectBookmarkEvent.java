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
package com.concursive.connect.web.modules.lists.workflow;

import com.concursive.commons.workflow.ComponentContext;
import com.concursive.commons.workflow.ComponentInterface;
import com.concursive.commons.workflow.ObjectHookComponent;
import com.concursive.connect.web.modules.activity.dao.ProjectHistory;
import com.concursive.connect.web.modules.activity.dao.ProjectHistoryList;
import com.concursive.connect.web.modules.lists.dao.Task;
import com.concursive.connect.web.modules.lists.dao.TaskCategory;
import com.concursive.connect.web.modules.lists.dao.TaskList;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.login.utils.UserUtils;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import com.concursive.connect.web.modules.wiki.utils.WikiLink;

import java.sql.Connection;
import java.util.Iterator;

/**
 * Records project bookmark events...
 *
 * @author Ananth
 * @created Feb 18, 2009
 */
public class SaveProjectBookmarkEvent extends ObjectHookComponent implements ComponentInterface {
  public final static String HISTORY_BOOKMARK_SINGLELIST_TEXT = "history.bookmark.singlelist.text";
  public final static String HISTORY_BOOKMARK_MULTIPLELIST_TEXT = "history.bookmark.multiplelist.text";

  public String getDescription() {
    return "Records project bookmark event as a history item...";
  }

  public boolean execute(ComponentContext context) {
    boolean result = false;
    TaskList taskList = (TaskList) context.getThisObject();
    Connection db = null;
    try {
      db = getConnection(context);

      int owner = -1;
      int projectId = -1;
      int linkItemId = -1;

      StringBuffer sb = new StringBuffer();
      int count = 0;
      Iterator i = taskList.iterator();
      while (i.hasNext()) {
        count++;
        Task thisTask = (Task) i.next();
        // Get the user who entered the task
        User user = UserUtils.loadUser(thisTask.getOwner());
        // Reload the task category, and populate some things that won't be populated
        TaskCategory taskCategory = new TaskCategory(db, thisTask.getCategoryId());
        taskCategory.setLinkModuleId(thisTask.getLinkModuleId());
        taskCategory.setLinkItemId(user.getProfileProjectId());
        // Build the wiki links
        sb.append(WikiLink.generateLink(taskCategory));
        if (i.hasNext()) {
          if (sb.length() > 0) {
            sb.append(", ");
          }
        }

        owner = thisTask.getOwner(); //user bookmarking the project
        projectId = thisTask.getProjectId(); //project this list belongs to
        linkItemId = thisTask.getLinkItemId(); //project that is being bookmarked
      }

      if (taskList.size() > 0) {
        User user = UserUtils.loadUser(owner);
        Project userProfile = ProjectUtils.loadProject(user.getProfileProjectId());
        Project bookmarkProject = ProjectUtils.loadProject(linkItemId);

        // Prepare the wiki links
        context.setParameter("user", WikiLink.generateLink(userProfile));
        context.setParameter("profile", WikiLink.generateLink(bookmarkProject));
        if (taskList.size() == 1) {
          context.setParameter("list", sb.toString());
        } else {
          context.setParameter("lists", sb.toString());
        }

        // Insert the history
        ProjectHistory history = new ProjectHistory();
        history.setEnteredBy(owner);
        history.setProjectId(projectId);
        history.setLinkObject(ProjectHistoryList.LIST_OBJECT);
        history.setEventType(ProjectHistoryList.BOOKMARK_PROFILE_EVENT);
        history.setLinkItemId(linkItemId);
        if (taskList.size() == 1) {
          history.setDescription(context.getParameter(HISTORY_BOOKMARK_SINGLELIST_TEXT));
        } else {
          history.setDescription(context.getParameter(HISTORY_BOOKMARK_MULTIPLELIST_TEXT));
        }
        history.insert(db);
        result = true;
      }
    } catch (Exception e) {
      e.printStackTrace(System.out);
    } finally {
      freeConnection(context, db);
    }
    return result;
  }
}
