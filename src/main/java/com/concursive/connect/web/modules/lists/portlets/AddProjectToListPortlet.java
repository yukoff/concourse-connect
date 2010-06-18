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

import com.concursive.commons.text.StringUtils;
import com.concursive.connect.Constants;
import com.concursive.connect.cache.utils.CacheUtils;
import com.concursive.connect.web.modules.lists.dao.Task;
import com.concursive.connect.web.modules.lists.dao.TaskCategory;
import com.concursive.connect.web.modules.lists.dao.TaskCategoryList;
import com.concursive.connect.web.modules.lists.dao.TaskList;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.dao.ProjectList;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import com.concursive.connect.web.portal.PortalUtils;
import com.concursive.connect.web.utils.LookupElement;
import com.concursive.connect.web.utils.LookupList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.portlet.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

/**
 * Add Project to List Portlet
 *
 * @author Lorraine Bittner
 * @created August 19, 2008
 */
public class AddProjectToListPortlet extends GenericPortlet {

  private static Log LOG = LogFactory.getLog(AddProjectToListPortlet.class);

  // Pages
  private static final String VIEW_FORM_PAGE = "/portlets/add_project_to_list/add_project_to_list-edit.jsp";
  private static final String VIEW_MESSAGE_PAGE = "/portlets/add_project_to_list/add_project_to_list-message-view.jsp";
  private static final String CLOSE_PAGE = "/portlets/add_project_to_list/add_project_to_list-refresh.jsp";

  // Preferences
  private static final String PREF_TITLE = "title";
  private static final String PREF_INTRODUCTION_MESSAGE = "introductionMessage";
  private static final String PREF_SUCCESS_MESSAGE = "successMessage";
  private static final String PREF_FAILURE_MESSAGE = "failureMessage";


  // Attribute names for objects available in the view
  private static final String TITLE = "title";
  private static final String INTRODUCTION_MESSAGE = "introductionMessage";
  private static final String SUCCESS_MESSAGE = "successMessage";
  private static final String ACTION_ERROR = "actionError";
  private static final String PROJECT = "project";
  private static final String AVAILABLE_LISTS = "availableLists";
  private static final String AVAILABLE_PROJECTS = "availableProjects";
  private static final String USED_LIST_MAP = "usedListMap";
  private static final String USER_PROFILE = "userProfile";
  private static final String CAN_ADD_LIST = "canAddList";
  private static final String CAN_DELETE_FROM_LIST = "canDeleteFromList";

  private static final String VIEW_TYPE = "viewType";
  private static final String SAVE_FAILURE = "saveFailure";
  private static final String SAVE_SUCCESS = "saveSuccess";

  // Names of parameters that are processed on action
  private static final String PROJECT_ID_OF_LISTS = "pidOfLists";
  private static final String PROJECT_ID_TO_BOOKMARK = "pidToBookmark";
  private static final String NEW_LIST_NAME = "newListName";
  private static final String LIST = "list"; //multiple parameters allowed

  @Override
  public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
    try {
      String view = VIEW_FORM_PAGE;
      String viewType = request.getParameter("viewType");
      if (viewType == null) {
        viewType = (String) request.getPortletSession().getAttribute("viewType");
      }
      // Set global preferences
      request.setAttribute(TITLE, request.getPreferences().getValue(PREF_TITLE, null));
      request.setAttribute(INTRODUCTION_MESSAGE, request.getPreferences().getValue(PREF_INTRODUCTION_MESSAGE, null));

      User user = PortalUtils.getUser(request);
      Project project = PortalUtils.getProject(request);

      Task task = (Task) request.getPortletSession().getAttribute("task");

      int projectId = project == null ? -1 : project.getId();
      String projectIdOfLists = request.getParameter(PROJECT_ID_OF_LISTS);
      int pidOfLists = projectIdOfLists == null ? -1 : Integer.parseInt(projectIdOfLists);

      LOG.debug("doView: pidOfLists -- " + pidOfLists);

      // Clean up session
      request.getPortletSession().removeAttribute(VIEW_TYPE);
      request.getPortletSession().removeAttribute(PROJECT_ID_OF_LISTS);

      if (SAVE_FAILURE.equals(viewType)) {
        // Prep the form to show errors...
        request.setAttribute(ACTION_ERROR, request.getPreferences().getValue(PREF_FAILURE_MESSAGE, null));
        // Show the form with any errors provided
        PortalUtils.processErrors(request, task.getErrors());
      } else if (SAVE_SUCCESS.equals(viewType)) {
        // Save Success
        request.setAttribute(SUCCESS_MESSAGE, request.getPreferences().getValue(PREF_SUCCESS_MESSAGE, null));
        view = VIEW_MESSAGE_PAGE;
      } else {
        if (!user.isLoggedIn()) {
          // If user is not logged in, redirect
          view = VIEW_MESSAGE_PAGE;
          request.setAttribute(ACTION_ERROR, "You need to be logged in to perform this action");
        } else if (projectId <= 0) {
          request.setAttribute(ACTION_ERROR, "No project was specified");
          view = VIEW_MESSAGE_PAGE;
        } else {
          view = VIEW_FORM_PAGE;
          try {
            Connection db = PortalUtils.useConnection(request);

            int userProfileId = user.getProfileProjectId();
            Project userProfile;
            if (userProfileId == -1) {
              view = VIEW_MESSAGE_PAGE;
              request.setAttribute(ACTION_ERROR, "No profile is available to bookmark.");
            } else {
              userProfile = ProjectUtils.loadProject(userProfileId);
              if (pidOfLists == -1) {
                pidOfLists = userProfile.getId();
              }
              // check the user has permissions to add/delete from lists for the project whose lists are being modified
              // these will be used by the view to dynamically show/hide functionality
              request.setAttribute(CAN_ADD_LIST, ProjectUtils.hasAccess(pidOfLists, user, "project-lists-add"));
              request.setAttribute(CAN_DELETE_FROM_LIST, ProjectUtils.hasAccess(pidOfLists, user, "project-lists-delete"));

              ProjectList projectList = findUserProjects(db, user);
              ProjectList availableProjects = new ProjectList();
              // Profile needs to appear on top so remove it from list and don't add twice
              availableProjects.add(userProfile);
              for (int i = 0; i != projectList.size(); i++) {
                Project p = projectList.get(i);
                if (p.getId() != userProfile.getId()) {
                  availableProjects.add(p);
                }
              }

              TaskCategoryList availableLists = getAvailableLists(db, pidOfLists);
              
              String errorMessage = (String)request.getPortletSession().getAttribute(ACTION_ERROR);
              if (StringUtils.hasText(errorMessage)){
              	request.setAttribute(ACTION_ERROR, errorMessage);
              }
              
              Map<Integer, TaskCategory> usedLists = findExistingTaskCategorysForProjects(db, pidOfLists, project.getId());
              request.setAttribute(PROJECT_ID_OF_LISTS, pidOfLists);
              request.setAttribute(PROJECT, project);
              request.setAttribute(AVAILABLE_LISTS, availableLists);
              request.setAttribute(USER_PROFILE, userProfile);
              request.setAttribute(AVAILABLE_PROJECTS, availableProjects);
              request.setAttribute(USED_LIST_MAP, usedLists);
            }
          } catch (SQLException e) {
            e.printStackTrace();
            view = VIEW_MESSAGE_PAGE;
            request.setAttribute(ACTION_ERROR, "An error occurred processing your request. Please try again.");
          }
        }
      }
      // Clean up session
      request.getPortletSession().removeAttribute(ACTION_ERROR);
      PortletContext context = getPortletContext();
      PortletRequestDispatcher requestDispatcher = context.getRequestDispatcher(view);
      requestDispatcher.include(request, response);
    } catch (Exception e) {
      e.printStackTrace();
      throw new PortletException(e);
    }
  }

  @Override
  public void processAction(ActionRequest request, ActionResponse response)
      throws PortletException, IOException {
    String ctx = request.getContextPath();
    boolean isClose = "true".equals(request.getParameter("close"));

    if (isClose) {
      // Clean up the session
      LOG.debug("Closing the form");
      request.getPortletSession().removeAttribute(VIEW_TYPE);
      response.sendRedirect(ctx + CLOSE_PAGE);
      return;
    }

    int projectIdToBookmark = Integer.valueOf(request.getParameter(PROJECT_ID_TO_BOOKMARK));
    int projectIdOfLists = Integer.valueOf(request.getParameter(PROJECT_ID_OF_LISTS));
    String newListName = request.getParameter(NEW_LIST_NAME);

    // If the user selected an item in the drop-down, then that means they are
    // changing lists
    int pidToCompare = Integer.valueOf(request.getParameter("pidToCompare"));
    boolean isChangePidOfLists = pidToCompare != projectIdOfLists;
    if (isChangePidOfLists) {
      LOG.debug("A new project has been selected: " + projectIdOfLists);
      response.setRenderParameter(PROJECT_ID_OF_LISTS, String.valueOf(projectIdOfLists));
      return;
    }

    try {
      LOG.debug("Saving the form...");

      boolean isSuccess;

      User user = PortalUtils.getUser(request);
      int userId = user.getId();
      Connection db = PortalUtils.useConnection(request);
      Project projectOfLists = new Project(db, projectIdOfLists);
      Project projectToBookmark = new Project(db, projectIdToBookmark);
      Collection<Integer> listIds = getListIds(request.getParameterValues(LIST));
      //verify user can modify lists for project
      boolean isAddNewList = false;
      if (ProjectUtils.hasAccess(projectOfLists.getId(), user, "project-lists-modify")) {
      	
      	if (!StringUtils.hasText(newListName) && (listIds.size() == 0)){
          System.out.println("Error need to show From");
          request.getPortletSession().setAttribute(ACTION_ERROR, "Choose a list or create one");
          request.getPortletSession().setAttribute(VIEW_TYPE, VIEW_FORM_PAGE);
          return;
      	}
      	
        if (StringUtils.hasText(newListName)) {
          if (!ProjectUtils.hasAccess(projectOfLists.getId(), user, "project-lists-add")) {
            request.getPortletSession().setAttribute(ACTION_ERROR, "Not authorized to create new list");
            request.getPortletSession().setAttribute(VIEW_TYPE, VIEW_FORM_PAGE);
            return;
          }
          int newListId = saveNewList(db, projectIdOfLists, newListName);
          if (newListId == -1) {
            request.getPortletSession().setAttribute(ACTION_ERROR, "Unable to create new list.");
            request.getPortletSession().setAttribute(VIEW_TYPE, SAVE_FAILURE);
            return;
          } else {
            listIds.add(newListId);
            isAddNewList = true;
          }
        }
        TaskList existingTasks = findExistingTasksForProjects(db, projectIdOfLists, projectIdToBookmark);
        //check to see if the user is deleting tasks (listItems)
        if ((isAddNewList && existingTasks.size() > listIds.size() - 1)
            || !isAddNewList && existingTasks.size() > listIds.size()) {
          if (!ProjectUtils.hasAccess(projectOfLists.getId(), user, "project-lists-delete")) {
            request.getPortletSession().setAttribute(ACTION_ERROR, "Not authorized to delete items");
            request.getPortletSession().setAttribute(VIEW_TYPE, VIEW_FORM_PAGE);
            return;
          } else {
            deleteFromLists(db, existingTasks, listIds);
          }
        }
        isSuccess = saveToLists(db, existingTasks, listIds, userId, projectIdToBookmark, projectToBookmark.getTitle(),
            projectIdOfLists, request);
      } else {
        isSuccess = false;
        request.getPortletSession().setAttribute(ACTION_ERROR, "Not authorized to bookmark");
      }

      if (isSuccess) {
        // Close the panel, everything went well
        response.sendRedirect(ctx + "/close_panel_refresh.jsp");
      } else {
        request.getPortletSession().setAttribute(VIEW_TYPE, SAVE_FAILURE);
      }
    } catch (SQLException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

  /**
   * Inserts the project to boomark in new listItem (task) records, a record is created for each of the
   * listIds (taskCategoryIds) specified.
   * <p/>
   * Returns boolean on whether the operation was successful. Once one failure occurs the method returns
   * false without further processing.
   *
   * @param db                    - the database connection
   * @param existingTasks         - the list of tasks already persisted
   * @param listIds               - this list of listIds that will have the projectIdToBookmark saved
   * @param userId                - the id of the user inserting the records
   * @param projectIdToBookmark   - the id of the project that is being bookmarked
   * @param projectNameToBookmark - the name that will saved for the bookmark (task)
   * @param projectIdOfLists      - the id of the project that owns the lists
   * @param request               - the request
   * @return boolean on whether the operation was successful
   * @throws SQLException - generated trying to retrieve data
   */
  private boolean saveToLists(Connection db, TaskList existingTasks, Collection<Integer> listIds, int userId, int projectIdToBookmark,
                              String projectNameToBookmark, int projectIdOfLists, ActionRequest request) throws SQLException {
    Set<Integer> existingIds = new HashSet<Integer>(existingTasks.size());
    Set<Integer> createForTaskCategoryIds = new HashSet<Integer>(listIds);
    for (Task task : existingTasks) {
      existingIds.add(task.getCategoryId());
    }
    // find all the task category ids that do not already have tasks (these will have tasks inserted)
    createForTaskCategoryIds.removeAll(existingIds);
    if (!createForTaskCategoryIds.isEmpty()) {
      boolean recordInserted = false;
      LookupList priorityList = CacheUtils.getLookupList("lookup_task_priority");
      if (priorityList.isEmpty())
        throw new RuntimeException("Could not load task priorities");
      // just default to the top priority
      int priorityId = priorityList.get(0).getId();
      for (LookupElement priority : priorityList) {
        if (priority.getDefaultItem()) {
          priorityList.get(0).getId();
          break;
        }
      }

      //Parameters
      TaskList taskList = new TaskList();
      for (Integer taskCategoryId : createForTaskCategoryIds) {

        Task task = new Task();
        task.setEnteredBy(userId);
        task.setOwner(userId);
        task.setDescription(projectNameToBookmark);
        task.setModifiedBy(userId);
        task.setProjectId(projectIdOfLists);
        task.setLinkModuleId(Constants.TASK_CATEGORY_PROJECTS);
        task.setLinkItemId(projectIdToBookmark);
        task.setCategoryId(taskCategoryId);
        task.setPriority(priorityId);
        // Verify the specified category is in the same project
        TaskCategoryList list = new TaskCategoryList();
        list.setProjectId(projectIdOfLists);
        list.setCategoryId(taskCategoryId);
        list.buildList(db);
        if (list.size() == 0) {
          return false;
        }
        recordInserted = task.insert(db);
        if (!recordInserted) {
          request.getPortletSession().setAttribute("task", task);
          return false;
        }
        taskList.add(task);
      }

      //Trigger the workflow
      PortalUtils.processInsertHook(request, taskList);

      return recordInserted;
    } else {
      return true; //no inserts needed
    }
  }

  /**
   * Deletes any existing task records that are not specified in the listIds. This
   * method allows a user to specify on the request which lists they want to bookmark
   * too, if any existing links were not specified they will be deleted.
   *
   * @param db            - connection to database
   * @param existingTasks - list of tasks already persisted
   * @param listIds       - list of tasks that were specified by user
   * @throws SQLException - generated trying to delete records
   */
  private void deleteFromLists(Connection db, TaskList existingTasks, Collection<Integer> listIds) throws SQLException {
    Set<Integer> deleteTaskIds = new HashSet<Integer>(existingTasks.size());
    for (Task task : existingTasks) {
      deleteTaskIds.add(task.getCategoryId());
    }
    // find all the task ids that were not requested (these will be deleted)
    deleteTaskIds.removeAll(listIds);
    if (deleteTaskIds.size() > 0) {
      for (Task task : existingTasks) {
        if (deleteTaskIds.contains(task.getCategoryId())) {
          task.delete(db);
        }
      }
    }
  }

  /**
   * Inserts new list (taskCategory) record, and returns the id of the newly inserted
   * record
   *
   * @param db          - database connection
   * @param projectId   - the project id that will own the new list (taskCategory)
   * @param description - description that will be saved for the list (taskCategory)
   * @return int - id of the new list (taskCategory) saved
   * @throws SQLException - generated trying to retrieve data
   */
  private int saveNewList(Connection db, int projectId, String description) throws SQLException {
    int id;
    // Process the category
    TaskCategory newCategory = new TaskCategory();
    newCategory.setLinkModuleId(Constants.TASK_CATEGORY_PROJECTS);
    newCategory.setLinkItemId(projectId);
    newCategory.setDescription(description);
    newCategory.insert(db);
    id = newCategory.getId();
    return id;
  }


  /**
   * Retrieves all the lists available to the project
   * <p/>
   * If no lists are available then the empty list is returned
   *
   * @param db        - database connection
   * @param projectId - id of project to find it's available lists
   * @return TaskCategoryList - the lists available to the user
   * @throws SQLException - generated trying to retrieve data
   */
  private TaskCategoryList getAvailableLists(Connection db, int projectId) throws SQLException {
    TaskCategoryList availableLists = new TaskCategoryList();
    availableLists.setProjectId(projectId);
    availableLists.buildList(db);
    return availableLists;
  }

  /**
   * Retrieves all task categories that contain this projectToBookmark as a task
   * and are owned by the projectOfLists
   *
   * @param db                  - database connection
   * @param projectIdOfLists    - the id of the project that owns the lists (taskCategories)
   * @param projectIdToBookmark - the id of the project that is linked by bookmark
   * @return Map<Integer, TaskCategory> all taskCategories found mapped by their id
   * @throws SQLException - generated trying to retrieve data
   */
  private Map<Integer, TaskCategory> findExistingTaskCategorysForProjects(Connection db, int projectIdOfLists, int projectIdToBookmark) throws SQLException {
    Map<Integer, TaskCategory> map = new HashMap<Integer, TaskCategory>();
    TaskCategoryList categoryList = new TaskCategoryList();
    categoryList.setTaskLinkModuleId(Constants.TASK_CATEGORY_PROJECTS);
    categoryList.setTaskLinkItemId(projectIdToBookmark);
    categoryList.setProjectId(projectIdOfLists);
    categoryList.buildList(db);
    for (TaskCategory tc : categoryList) {
      map.put(tc.getId(), tc);
    }
    return map;
  }

  /**
   * Returns all the existing tasks for that are owned by the projectIdOfLists
   * and are linked to the projectIdToBookmark
   *
   * @param db                  - the database connection
   * @param projectIdOfLists    - this is the main project where the lists are stored against
   * @param projectIdToBookmark - the project that has been linked (bookmarked) to the above project
   *                            by a list
   * @return taskList - the list of existing tasks matching the parameters
   * @throws SQLException - generated trying to retrieve data
   */
  private TaskList findExistingTasksForProjects(Connection db, int projectIdOfLists, int projectIdToBookmark) throws SQLException {
    TaskList taskList = new TaskList();
    taskList.setLinkModuleId(Constants.TASK_CATEGORY_PROJECTS);
    taskList.setLinkItemId(projectIdToBookmark);
    taskList.setProjectId(projectIdOfLists);
    taskList.buildList(db);
    return taskList;
  }

  /**
   * Reads through a String[] and attempts to transform into a Collection of Integers.
   * <p/>
   * If null is passed as a parameter an empty list is returned.
   *
   * @param listIdsStr - the String[] to be transformed
   * @return Collection<Integer> representing the values shown in the listIdsStr
   */
  private static Collection<Integer> getListIds(String[] listIdsStr) {
    Collection<Integer> listIds = new ArrayList<Integer>();
    if (listIdsStr == null) return listIds;
    for (int i = 0; i != listIdsStr.length; i++) {
      Integer listId = Integer.valueOf(listIdsStr[i]);
      listIds.add(listId);
    }
    return listIds;
  }

  /**
   * Retrieves all user projects that they are authorized to modify.
   *
   * @param db   - the database connection
   * @param user - the user to find projects for
   * @return ProjectList - the list of projects the user is authorized to modify
   * @throws SQLException - generated trying to retrieve the data
   */
  private ProjectList findUserProjects(Connection db, User user) throws SQLException {
    ProjectList tmpList = new ProjectList();
    ProjectList returnList = new ProjectList();
    tmpList.setOpenProjectsOnly(true);
    tmpList.setProjectsForUser(user.getId());
    tmpList.buildList(db);
    for (Project p : tmpList) {
      if (ProjectUtils.hasAccess(p.getId(), user, "project-lists-modify")) {
        returnList.add(p);
      }
    }
    return returnList;
  }

}