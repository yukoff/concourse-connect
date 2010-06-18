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
package com.concursive.connect.web.modules.lists.portlets;

import com.concursive.connect.web.modules.lists.dao.Task;
import com.concursive.connect.web.modules.lists.dao.TaskCategory;
import com.concursive.connect.web.modules.lists.dao.TaskCategoryList;
import com.concursive.connect.web.modules.lists.dao.TaskList;
import com.concursive.connect.web.modules.lists.utils.TaskUtils;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import com.concursive.connect.web.portal.PortalUtils;
import com.concursive.connect.web.utils.PagedListInfo;

import javax.portlet.*;
import java.io.IOException;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

/**
 * Project list portlet
 *
 * @author lorraine bittner
 * @created June 18, 2008
 */
public class ListsByProjectPortlet extends GenericPortlet {

  // Pages
  private static final String VIEW_PAGE = "/portlets/lists_by_project/lists_by_project-view.jsp";

  // Parameters
  private static final String LIST_ITEMS_MAP = "listItemsMap";
  private static final String TASK_URL_MAP = "taskUrlMap";


  // Preferences
  private static final String LIMIT = "limit";
  private static final String TITLE = "title";
  private static final String PREF_SHOW_IF_EMPTY = "showIfEmpty";


  public void doView(RenderRequest request, RenderResponse response)
      throws PortletException, IOException {
    // Prefs
    request.setAttribute(TITLE, request.getPreferences().getValue(TITLE, "Lists"));
    boolean showIfEmpty = Boolean.parseBoolean(request.getPreferences().getValue(PREF_SHOW_IF_EMPTY, "false"));


    String defaultView = VIEW_PAGE;
    User thisUser = PortalUtils.getUser(request);

    String listSizeString = request.getPreferences().getValue(LIMIT, "10");
    int listSize = Integer.parseInt(listSizeString);

    Project project = PortalUtils.getProject(request);

    if (project == null) {
      throw new PortletException("Project must be specified.");
    }
    int projectId = project.getId();

    if (!ProjectUtils.hasAccess(projectId, thisUser, "project-lists-view")) {
      throw new PortletException("Unauthorized to view lists for this project");
    }

    try {
      Connection db = PortalUtils.useConnection(request);
      // First retrieve all the TaskCategories
      // Then get all the tasks for each task category and store
      // tasks mapped out to their task category
      TaskCategoryList taskCategories = new TaskCategoryList();
      taskCategories.setProjectId(projectId);
      taskCategories.buildList(db);
      Map<Integer, TaskCategory> tcMap = new HashMap<Integer, TaskCategory>();
      for (TaskCategory tc : taskCategories) {
        tcMap.put(tc.getId(), tc);
      }

      // Find all of the items on the list
      TaskList tasks = new TaskList();
      tasks.setProjectId(projectId);
      tasks.buildList(db);

      // Map the tasks to a category and check the permissions
      Map<TaskCategory, TaskList> tasksByCategoryMap = new HashMap<TaskCategory, TaskList>();
      HashMap<Integer, String> taskUrlMap = new HashMap<Integer, String>();
      for (Task task : tasks) {
        TaskCategory tc = tcMap.get(task.getCategoryId());
        TaskList tmpList = tasksByCategoryMap.get(tc);
        if (tmpList == null) {
          tmpList = new TaskList();
        }
        // Add tasks without a link, anyone can see these list items
        if (task.getLinkItemId() == -1) {
          tmpList.add(task);
          tasksByCategoryMap.put(tc, tmpList);
        } else {
          // Check permissions before adding
          String linkItemUrl = TaskUtils.getLinkItemUrl(thisUser, request.getContextPath(), task);
          if (linkItemUrl != null) {
            tmpList.add(task);
            tasksByCategoryMap.put(tc, tmpList);
            taskUrlMap.put(task.getId(), linkItemUrl);
          }
        }
      }

      PagedListInfo pagedListInfo = new PagedListInfo();
      pagedListInfo.setItemsPerPage(listSize);

      // Projects to show
      taskCategories.setPagedListInfo(pagedListInfo);
      request.setAttribute(LIST_ITEMS_MAP, tasksByCategoryMap);
      request.setAttribute(TASK_URL_MAP, taskUrlMap);
      request.setAttribute(LIMIT, listSize);

      if (!showIfEmpty && tasksByCategoryMap.isEmpty()) {
        defaultView = null;
      }

      if (defaultView != null) {
        // JSP view
        PortletContext context = getPortletContext();
        PortletRequestDispatcher requestDispatcher =
            context.getRequestDispatcher(defaultView);
        requestDispatcher.include(request, response);
      }
    } catch (Exception e) {
      e.printStackTrace();
      throw new PortletException(e);
    }
  }

}