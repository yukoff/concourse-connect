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

package com.concursive.connect.web.controller.actions;

import com.concursive.commons.db.ConnectionElement;
import com.concursive.commons.db.ConnectionPool;
import com.concursive.commons.http.RequestUtils;
import com.concursive.commons.text.StringUtils;
import com.concursive.commons.web.mvc.actions.ActionContext;
import com.concursive.commons.workflow.ObjectHookAction;
import com.concursive.commons.workflow.ObjectHookManager;
import com.concursive.connect.Constants;
import com.concursive.connect.cache.utils.CacheUtils;
import com.concursive.connect.config.ApplicationPrefs;
import com.concursive.connect.indexer.IndexEvent;
import com.concursive.connect.scheduler.ScheduledJobs;
import com.concursive.connect.web.modules.login.dao.Instance;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.login.utils.InstanceUtils;
import com.concursive.connect.web.modules.login.utils.UserUtils;
import com.concursive.connect.web.modules.members.dao.TeamMember;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import com.concursive.connect.web.utils.ClientType;
import com.concursive.connect.web.utils.LookupList;
import com.concursive.connect.web.utils.PagedListInfo;
import com.concursive.connect.scheduler.JobEvent;
import freemarker.template.Configuration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Scheduler;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Vector;

/**
 * This class is provided as a means to descend from, so that common generic
 * methods and fields can be placed here and all other action classes (that
 * descend from this one), inherit the common functionality. You may want to
 * provide a simple logging mechanism, helper methods that action classes might
 * find useful, and so on. note: If an action class you define will not be
 * descended by other classes, you should make it final, for the performance
 * gains a final class achieves.
 *
 * @author matt rajkowski
 * @version $Id$
 * @created January 8, 2002
 */

public class GenericAction implements java.io.Serializable {

  protected static final Log LOG = LogFactory.getLog(GenericAction.class);

  final static long serialVersionUID = -1388329390981035172L;
  public final static String fs = System.getProperty("file.separator");
  public final static String NOT_UPDATED_MESSAGE =
      "<b>This record could not be updated because someone else updated it first.</b><p>" +
          "You can hit the back button to review the changes that could not be committed, " +
          "but you must reload the record and make the changes again.";


  /**
   * 0-arg constructor
   */
  public GenericAction() {
  }


  /**
   * This is the default call by all actions if a command= paramter is not
   * passed along with the request. Descendant classes should override this
   * method to carry out default behavior that should occure BEFORE the initial
   * JSP page is displayed. This is most useful when the initial page needs to
   * show a list of items that are populated from the database, such as a
   * drop-down list of activity for a client, etc.
   *
   * @param context Description of Parameter
   * @return Description of the Returned Value
   */
  public String executeCommandDefault(ActionContext context) {
    return "DefaultOK";
  }


  protected Connection getConnection(ActionContext context, ConnectionElement ce, boolean persistant) throws SQLException {
    ConnectionPool sqlDriver = (ConnectionPool) context.getServletContext().getAttribute(Constants.CONNECTION_POOL);
    return sqlDriver.getConnection(ce, persistant);
  }

  protected Connection getConnection(ActionContext context, ConnectionElement ce) throws SQLException {
    return getConnection(context, ce, false);
  }


  protected Connection getConnection(ActionContext context, boolean persistant) throws SQLException {
    ConnectionElement ce = (ConnectionElement) context.getSession().getAttribute(Constants.SESSION_CONNECTION_ELEMENT);
    return getConnection(context, ce, persistant);
  }


  /**
   * Gets the connection attribute of the GenericAction object
   *
   * @param context Description of the Parameter
   * @return The connection value
   * @throws SQLException Description of the Exception
   */
  protected Connection getConnection(ActionContext context) throws SQLException {
    return getConnection(context, false);
  }


  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @param db      Description of the Parameter
   */
  protected void freeConnection(ActionContext context, Connection db) {
    if (db != null) {
      ConnectionPool sqlDriver = (ConnectionPool) context.getServletContext().getAttribute(Constants.CONNECTION_POOL);
      sqlDriver.free(db);
    }
  }


  /**
   * Gets the user attribute of the GenericAction object
   *
   * @param context Description of the Parameter
   * @return The user value
   */
  protected static User getUser(ActionContext context) {
    User user = (User) context.getRequest().getAttribute(Constants.REQUEST_CURRENT_USER);
    if (user != null) {
      return user;
    }
    return (User) context.getSession().getAttribute(Constants.SESSION_USER);
  }


  /**
   * Gets the userId attribute of the GenericAction object
   *
   * @param context Description of the Parameter
   * @return The userId value
   */
  protected static int getUserId(ActionContext context) {
    return getUser(context).getId();
  }


  /**
   * Gets the groupId attribute of the GenericAction object
   *
   * @param context Description of the Parameter
   * @return The groupId value
   */
  protected int getGroupId(ActionContext context) {
    return getUser(context).getGroupId();
  }


  /**
   * Gets the userRange attribute of the GenericAction object
   *
   * @param context Description of the Parameter
   * @return The userRange value
   */
  protected String getUserRange(ActionContext context) {
    return getUser(context).getIdRange();
  }


  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @param errors  Description of the Parameter
   */
  protected void processErrors(ActionContext context, Map errors) {
    for (Object o : errors.keySet()) {
      String errorKey = (String) o;
      String errorMsg = (String) errors.get(errorKey);
      context.getRequest().setAttribute(errorKey, errorMsg);
    }
    context.getRequest().setAttribute("errors", errors);
    if (errors.size() > 0) {
      if (context.getRequest().getAttribute("actionError") == null) {
        context.getRequest().setAttribute("actionError", "The form could not be submitted, please review the messages below.");
      }
    }
  }


  /**
   * Gets the path attribute of the GenericAction object
   *
   * @param context          Description of the Parameter
   * @param moduleFolderName Description of the Parameter
   * @return The path value
   */
  protected String getPath(ActionContext context, String moduleFolderName) {
    return (
        getPref(context, "FILELIBRARY") +
            getUser(context).getGroupId() + fs +
            moduleFolderName + fs);
  }


  /**
   * Gets the datePath attribute of the GenericAction object
   *
   * @param fileDate Description of the Parameter
   * @return The datePath value
   */
  public static String getDatePath(java.sql.Timestamp fileDate) {
    SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy");
    String datePathToUse1 = formatter1.format(fileDate);
    SimpleDateFormat formatter2 = new SimpleDateFormat("MMdd");
    String datePathToUse2 = formatter2.format(fileDate);
    return datePathToUse1 + fs + datePathToUse2 + fs;
  }


  /**
   * Gets the pagedListInfo attribute of the GenericAction object
   *
   * @param context  Description of the Parameter
   * @param viewName Description of the Parameter
   * @return The pagedListInfo value
   */
  protected PagedListInfo getPagedListInfo(ActionContext context, String viewName) {
    PagedListInfo tmpInfo = (PagedListInfo) context.getSession().getAttribute(viewName);
    if (tmpInfo == null) {
      tmpInfo = new PagedListInfo();
      tmpInfo.setId(viewName);
      tmpInfo.setInitializationLevel(PagedListInfo.LEVEL_INITIALIZED);
      context.getSession().setAttribute(viewName, tmpInfo);
    } else {
      tmpInfo.setInitializationLevel(PagedListInfo.LEVEL_READY);
    }
    ActionContext actionContext = new ActionContext(
        context.getServlet(), null, null, context.getRequest(), context.getResponse());
    tmpInfo.setParameters(actionContext);
    return tmpInfo;
  }

  /**
   * Get the pagedListInfo, if it doesn't already exist then set the default items per page
   *
   * @param context
   * @param viewName
   * @param defaultItemsPerPage
   * @return The pagedListInfo value
   */
  protected PagedListInfo getPagedListInfo(ActionContext context, String viewName, int defaultItemsPerPage) {
    PagedListInfo tmpInfo = (PagedListInfo) context.getSession().getAttribute(viewName);
    if (tmpInfo == null) {
      tmpInfo = new PagedListInfo();
      tmpInfo.setId(viewName);
      tmpInfo.setItemsPerPage(defaultItemsPerPage);
      tmpInfo.setInitializationLevel(PagedListInfo.LEVEL_INITIALIZED);
      context.getSession().setAttribute(viewName, tmpInfo);
    } else {
      tmpInfo.setInitializationLevel(PagedListInfo.LEVEL_READY);
    }
    ActionContext actionContext = new ActionContext(
        context.getServlet(), null, null, context.getRequest(), context.getResponse());
    tmpInfo.setParameters(actionContext);
    return tmpInfo;
  }


  /**
   * Gets the pagedListInfo attribute of the GenericAction object
   *
   * @param context       Description of the Parameter
   * @param viewName      Description of the Parameter
   * @param defaultColumn Description of the Parameter
   * @param defaultOrder  Description of the Parameter
   * @return The pagedListInfo value
   */
  protected PagedListInfo getPagedListInfo(ActionContext context, String viewName, String defaultColumn, String defaultOrder) {
    PagedListInfo tmpInfo = (PagedListInfo) context.getSession().getAttribute(viewName);
    if (tmpInfo == null) {
      tmpInfo = new PagedListInfo();
      tmpInfo.setId(viewName);
      tmpInfo.setColumnToSortBy(defaultColumn);
      tmpInfo.setSortOrder(defaultOrder);
      tmpInfo.setInitializationLevel(PagedListInfo.LEVEL_INITIALIZED);
      context.getSession().setAttribute(viewName, tmpInfo);
    } else {
      tmpInfo.setInitializationLevel(PagedListInfo.LEVEL_READY);
    }
    ActionContext actionContext = new ActionContext(
        context.getServlet(), null, null, context.getRequest(), context.getResponse());
    tmpInfo.setParameters(actionContext);
    return tmpInfo;
  }


  /**
   * Description of the Method
   *
   * @param context  Description of the Parameter
   * @param viewName Description of the Parameter
   */
  protected void deletePagedListInfo(ActionContext context, String viewName) {
    PagedListInfo tmpInfo = (PagedListInfo) context.getSession().getAttribute(viewName);
    if (tmpInfo != null) {
      context.getSession().removeAttribute(viewName);
    }
  }


  /**
   * Gets the userLevel attribute of the GenericAction object
   *
   * @param roleLevel Description of the Parameter
   * @return The userLevel value
   */
  protected static int getUserLevel(int roleLevel) {
    return UserUtils.getUserLevel(roleLevel);
  }


  /**
   * Gets the level of the role from cache
   *
   * @param code The role id
   * @return The level value
   */
  protected static int getRoleId(int code) {
    LookupList roleList = CacheUtils.getLookupList("lookup_project_role");
    return roleList.getLevelFromId(code);
  }


  /**
   * Description of the Method
   *
   * @param context    Description of the Parameter
   * @param projectId  Description of the Parameter
   * @param permission Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  protected static boolean hasProjectAccess(ActionContext context, int projectId, String permission) throws SQLException {
    // Get the project from cache
    Project thisProject = ProjectUtils.loadProject(projectId);
    // Check access to the system first
    User thisUser = getUser(context);
    if (thisUser == null) {
      thisUser = UserUtils.createGuestUser();
    }
    // See if the team member has access to perform a project action
    TeamMember thisMember = (TeamMember) context.getRequest().getAttribute("currentMember");
    if (thisMember == null || thisMember.getProjectId() != thisProject.getId()) {
      try {
        // Load from project
        thisMember = thisProject.getTeam().getTeamMember(getUserId(context));
        if (thisMember == null) {
          throw new SQLException("Member record not found.");
        }
      } catch (Exception notValid) {
        // Retrieve or generate a team member based on project settings
        thisMember = ProjectUtils.retrieveTeamMember(thisProject.getId(), thisUser);
        if (thisMember == null) {
          return false;
        }
      }
      context.getRequest().setAttribute("currentMember", thisMember);
    }
    // Return the status of the permission
    if (thisMember.getRoleId() == TeamMember.PROJECT_ADMIN) {
      return true;
    }
    // If this is an administrator of the system, give them upgraded access
    if (thisUser.getAccessAdmin()) {
      thisMember.setUserLevel(getUserLevel(TeamMember.PROJECT_ADMIN));
      thisMember.setRoleId(TeamMember.PROJECT_ADMIN);
      thisMember.setTemporaryAdmin(true);
    }

    // If this is a content editor of the system, give them upgraded access
    if (thisUser.hasContentEditorAccess(thisProject.getLanguageId()) && thisProject.getPortal()) {
      thisMember.setUserLevel(getUserLevel(TeamMember.PROJECT_ADMIN));
      thisMember.setRoleId(TeamMember.PROJECT_ADMIN);
      thisMember.setTemporaryAdmin(true);
    }

    // See what the minimum required is and see if user meets that
    int accessLevel = thisProject.getAccessUserLevel(permission);
    // The following returns a number... typically 10-100
    int projectRoleId = getRoleId(accessLevel);
    if (accessLevel == -1 || projectRoleId == -1) {
      return false;
    }
    return (thisMember.getRoleId() <= projectRoleId);
  }


  /**
   * Checks the request to see if the url was called as a popup window
   *
   * @param context Description of the Parameter
   * @return The popup value
   */
  protected static boolean isPopup(ActionContext context) {
    return ("true".equals(context.getRequest().getParameter("popup")));
  }


  /**
   * Gets the pref attribute of the GenericAction object
   *
   * @param context Description of the Parameter
   * @param name    Description of the Parameter
   * @return The pref value
   */
  protected String getPref(ActionContext context, String name) {
    return getApplicationPrefs(context).get(name);
  }


  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @param name    Description of the Parameter
   * @return Description of the Return Value
   */
  protected boolean hasPref(ActionContext context, String name) {
    return getApplicationPrefs(context).has(name);
  }

  public static ClientType getClientType(ActionContext context) {
    ClientType clientType = (ClientType) context.getSession().getAttribute(Constants.SESSION_CLIENT_TYPE);
    if (clientType == null) {
      clientType = new ClientType(context.getRequest());
      context.getSession().setAttribute(Constants.SESSION_CLIENT_TYPE, clientType);
    } else if (clientType.getId() == -1) {
      clientType.setParameters(context.getRequest());
    }
    return clientType;
  }

  /**
   * Compares the form's token to the value in the session
   *
   * @param context
   * @return
   */
  protected static boolean hasMatchingFormToken(ActionContext context) {
    ClientType clientType = getClientType(context);
    String requestToken = context.getRequest().getParameter("token");
    return (clientType != null && requestToken != null && StringUtils.hasText(clientType.getToken()) && requestToken.equals(clientType.getToken()));
  }

  /**
   * Gets the applicationPrefs attribute of the GenericAction object
   *
   * @param context Description of the Parameter
   * @return The applicationPrefs value
   */
  protected static ApplicationPrefs getApplicationPrefs(ActionContext context) {
    return ApplicationPrefs.getApplicationPrefs(context.getServletContext());
  }

  protected synchronized boolean triggerJob(ActionContext context, String name, Object item) {
    Scheduler scheduler = (Scheduler) context.getServletContext().getAttribute(Constants.SCHEDULER);
    try {
    	if (item != null){
	      JobEvent jobEvent = new JobEvent(item);
	      ((Vector) scheduler.getContext().get(name + "EventArray")).add(jobEvent);
    	}
      scheduler.triggerJob(name, (String) scheduler.getContext().get(ScheduledJobs.CONTEXT_SCHEDULER_GROUP));
    } catch (Exception e) {
      System.out.println("GenericAction-> Scheduler failed: " + e.getMessage());
    }
    return true;
  }

  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @param item    Description of the Parameter
   * @return whether an item was added to the array
   * @throws IOException Description of the Exception
   */
  protected synchronized boolean indexAddItem(ActionContext context, Object item) {
    if (item == null) {
      return false;
    }
    Scheduler scheduler = (Scheduler) context.getServletContext().getAttribute(Constants.SCHEDULER);
    try {
      IndexEvent indexEvent = new IndexEvent(item, IndexEvent.ADD);
      ((Vector) scheduler.getContext().get("IndexArray")).add(indexEvent);
      scheduler.triggerJob("indexer", (String) scheduler.getContext().get(ScheduledJobs.CONTEXT_SCHEDULER_GROUP));
    } catch (Exception e) {
      System.out.println("GenericAction-> Scheduler failed: " + e.getMessage());
    }
    return true;
  }


  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @param item    Description of the Parameter
   * @return whether an item was added to the array
   * @throws IOException Description of the Exception
   */
  protected synchronized boolean indexDeleteItem(ActionContext context, Object item) {
    if (item == null) {
      return false;
    }
    Scheduler scheduler = (Scheduler) context.getServletContext().getAttribute(Constants.SCHEDULER);
    try {
      IndexEvent indexEvent = new IndexEvent(item, IndexEvent.DELETE);
      ((Vector) scheduler.getContext().get("IndexArray")).add(indexEvent);
      scheduler.triggerJob("indexer", (String) scheduler.getContext().get(ScheduledJobs.CONTEXT_SCHEDULER_GROUP));
    } catch (Exception e) {
      System.out.println("GenericAction-> Scheduler failed: " + e.getMessage());
    }
    return true;
  }


  /**
   * Sets the maximized attribute of the ProjectManagement object
   *
   * @param context The new maximized value
   */
  protected void setMaximized(ActionContext context) {
    context.getRequest().getSession(false).removeAttribute("projectView");
  }


  /**
   * Used for sending inserted objects to the rules engine for possible further processing
   *
   * @param context Description of the Parameter
   * @param object  Description of the Parameter
   */
  protected void processInsertHook(ActionContext context, Object object) {
    processInsertHook(context, object, null);
  }

  protected void processInsertHook(ActionContext context, Object object, String processName) {
    int userId = -1;
    User user = getUser(context);
    if (user != null) {
      userId = user.getId();
    }
    ObjectHookManager hookManager = (ObjectHookManager) context.getServletContext().getAttribute(Constants.OBJECT_HOOK_MANAGER);
    hookManager.process(ObjectHookAction.INSERT, null, object, userId, getServerUrl(context), getServerUrl(context), processName);
  }

  protected void processUpdateHook(ActionContext context, Object previousObject, Object object) {
    processUpdateHook(context, previousObject, object, null);
  }

  protected void processUpdateHook(ActionContext context, Object previousObject, Object object, String processName) {
    int userId = -1;
    User user = getUser(context);
    if (user != null) {
      userId = user.getId();
    }
    ObjectHookManager hookManager = (ObjectHookManager) context.getServletContext().getAttribute(Constants.OBJECT_HOOK_MANAGER);
    hookManager.process(ObjectHookAction.UPDATE, previousObject, object, userId, getServerUrl(context), getServerUrl(context), processName);
  }

  protected void processSelectHook(ActionContext context, Object object) {
    processSelectHook(context, object, null);
  }

  protected void processSelectHook(ActionContext context, Object object, String processName) {
    if (object != null) {
      int userId = -1;
      User user = getUser(context);
      if (user != null) {
        userId = user.getId();
      }
      ObjectHookManager hookManager = (ObjectHookManager) context.getServletContext().getAttribute(Constants.OBJECT_HOOK_MANAGER);
      hookManager.process(ObjectHookAction.SELECT, null, object, userId, getServerUrl(context), getServerUrl(context), processName);
    }
  }

  protected void processDeleteHook(ActionContext context, Object previousObject) {
    processDeleteHook(context, previousObject, null);
  }

  protected void processDeleteHook(ActionContext context, Object previousObject, String processName) {
    int userId = -1;
    User user = getUser(context);
    if (user != null) {
      userId = user.getId();
    }
    ObjectHookManager hookManager = (ObjectHookManager) context.getServletContext().getAttribute(Constants.OBJECT_HOOK_MANAGER);
    hookManager.process(ObjectHookAction.DELETE, previousObject, null, userId, getServerUrl(context), getServerUrl(context), processName);
  }

  /**
   * Gets the link attribute of the GenericAction object
   *
   * @param context Description of the Parameter
   * @return The link value
   */
  protected static String getServerUrl(ActionContext context) {
    ApplicationPrefs prefs = (ApplicationPrefs) context.getServletContext().getAttribute(Constants.APPLICATION_PREFS);
    boolean sslEnabled = "true".equals(prefs.get("SSL"));
    return ("http" + (sslEnabled ? "s" : "") + "://" + RequestUtils.getServerUrl(context.getRequest()));
  }

  protected String getLink(ActionContext context, String url) {
    return getServerUrl(context) + "/" + url;
  }

  protected Project retrieveAuthorizedProject(int projectId, ActionContext context) throws SQLException {
    // Get the project from cache
    Project project = ProjectUtils.loadProject(projectId);
    // Check the user's permission
    User thisUser = getUser(context);
    if (thisUser != null && thisUser.getAccessAdmin()) {
      return project;
    }
    // Allowed reasons to retrieve a project (permissions will be validated elsewhere)
    if (thisUser != null && project.getTeam().hasUserId(thisUser.getId()) ||
        project.getFeatures().getAllowGuests() ||
        (thisUser != null && thisUser.isLoggedIn() && project.getFeatures().getAllowParticipants()) ||
        project.getPortal()) {
      return project;
    }
    project = new Project();
    project.setId(projectId);
    return project;
  }

  protected void executeJob(ActionContext context, String jobName) {
    Scheduler scheduler = (Scheduler) context.getServletContext().getAttribute(Constants.SCHEDULER);
    try {
      scheduler.triggerJob(jobName, (String) scheduler.getContext().get(ScheduledJobs.CONTEXT_SCHEDULER_GROUP));
    } catch (Exception e) {
      System.out.println("GenericAction-> Scheduler failed: " + e.getMessage());
    }
  }

  public static String ctx(ActionContext context) {
    return context.getRequest().getContextPath();
  }

  protected Configuration getFreemarkerConfiguration(ActionContext context) {
    return ApplicationPrefs.getFreemarkerConfiguration(context.getServletContext());
  }

  protected static Instance getInstance(ActionContext context) {
    return InstanceUtils.getInstance(getServerUrl(context));
  }

}
