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

package com.concursive.connect.web.modules.admin.actions;

import com.concursive.commons.text.StringUtils;
import com.concursive.commons.web.mvc.actions.ActionContext;
import com.concursive.connect.Constants;
import com.concursive.connect.cache.utils.CacheUtils;
import com.concursive.connect.web.controller.actions.GenericAction;
import com.concursive.connect.web.modules.admin.beans.UserSearchBean;
import com.concursive.connect.web.modules.documents.dao.FileItemVersionList;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.login.dao.UserList;
import com.concursive.connect.web.modules.login.dao.UserLogList;
import com.concursive.connect.web.modules.login.utils.UserUtils;
import com.concursive.connect.web.modules.profile.dao.ProjectList;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import com.concursive.connect.web.modules.translation.dao.LanguagePackList;
import com.concursive.connect.web.modules.translation.dao.LanguageTeamList;
import com.concursive.connect.web.modules.translation.dao.WebSiteLanguageList;
import com.concursive.connect.web.modules.translation.dao.WebSiteTeamList;
import com.concursive.connect.web.utils.PagedListInfo;

import java.sql.Connection;

/**
 * Actions for the administration module
 *
 * @author matt rajkowski
 * @created February 23, 2004
 */
public final class AdminUsers extends GenericAction {

  /**
   * Action to prepare a list of Admin options
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandDefault(ActionContext context) {
    if (!getUser(context).getAccessAdmin()) {
      return "PermissionError";
    }
    return "DefaultOK";
  }


  /**
   * Action to prepare the search criteria form
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandSearch(ActionContext context) {
    if (!getUser(context).getAccessAdmin()) {
      return "PermissionError";
    }
    Connection db = null;
    try {
      UserSearchBean searchCriteria = (UserSearchBean) context.getFormBean();
      if ("true".equals(context.getRequest().getParameter("resetList"))) {
        context.getSession().removeAttribute("adminUserListInfo");
      }
      PagedListInfo adminUserListInfo = this.getPagedListInfo(context, "adminUserListInfo");
      adminUserListInfo.setLink(context, ctx(context) + "/AdminUsers.do?command=Search");
      db = getConnection(context);
      // Load the user list with the specified criteria
      UserList userList = new UserList();
      userList.setPagedListInfo(adminUserListInfo);
      userList.setSearchCriteria(searchCriteria);
      userList.setGroupId(this.getGroupId(context));
      userList.buildList(db);
      context.getRequest().setAttribute("userList", userList);
    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return ("SystemError");
    } finally {
      freeConnection(context, db);
    }
    return "SearchOK";
  }


  /**
   * Action to generate a the details of a specific user
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandDetails(ActionContext context) {
    if (!getUser(context).getAccessAdmin()) {
      return "PermissionError";
    }
    resetPagedListInfo(context);
    Connection db = null;
    try {
      db = getConnection(context);
      // Load the user
      User thisUser = new User(db, this.getGroupId(context), Integer.parseInt(context.getRequest().getParameter("id")));
      thisUser.getWebSiteLanguageList().setMemberId(thisUser.getId());
      thisUser.getWebSiteLanguageList().buildList(db);
      context.getRequest().setAttribute("thisUser", thisUser);
      // Get the user's fileitem size
      thisUser.setCurrentAccountSize(FileItemVersionList.queryOwnerSize(db, thisUser.getId()));
      // Get the user's project size
      int projectCount = ProjectList.buildProjectCount(db, thisUser.getId());
      context.getRequest().setAttribute("userProjectCount", String.valueOf(projectCount));
    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return ("SystemError");
    } finally {
      freeConnection(context, db);
    }
    return "DetailsOK";
  }

  private void resetPagedListInfo(ActionContext context) {
    if ("true".equals(context.getRequest().getParameter("resetList"))) {
      context.getSession().removeAttribute("adminUsersProjectsInfo");
      context.getSession().removeAttribute("adminUsersLoginInfo");
    }
  }

  public String executeCommandModify(ActionContext context) {
    if (!getUser(context).getAccessAdmin()) {
      return "PermissionError";
    }
    Connection db = null;
    try {
      db = getConnection(context);
      // Load the user
      User thisUser = new User(db, this.getGroupId(context), Integer.parseInt(context.getRequest().getParameter("id")));
      context.getRequest().setAttribute("thisUser", thisUser);
    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return ("SystemError");
    } finally {
      freeConnection(context, db);
    }
    return "ModifyOK";
  }

  public String executeCommandSave(ActionContext context) {
    if (!getUser(context).getAccessAdmin()) {
      return "PermissionError";
    }
    if (!hasMatchingFormToken(context)) {
      return "TokenError";
    }
    User prevUser = null;
    User thisUser = (User) context.getFormBean();
    Connection db = null;
    int count = 0;
    try {
      db = getConnection(context);
      thisUser.setModifiedBy(getUserId(context));
      // the username is always the email address
      thisUser.setUsername(thisUser.getEmail());
      thisUser.setConnectCRMAdmin("admin".equals(context.getRequest().getParameter("crmRole")));
      thisUser.setConnectCRMManager("manager".equals(context.getRequest().getParameter("crmRole")));
      // TODO: Before updating the user, check and see if the email address changed
      // so that the user can be notified

      // TODO: Make sure the email address is unique before changing it
      prevUser = UserUtils.loadUser(thisUser.getId());
      count = thisUser.update(db);
      if (count > 0) {
        CacheUtils.invalidateValue(Constants.SYSTEM_USER_CACHE, thisUser.getId());
        //Update the user's session to reflect the CRM role
        getUser(context).setConnectCRMAdmin("admin".equals(context.getRequest().getParameter("crmRole")));
        getUser(context).setConnectCRMManager("manager".equals(context.getRequest().getParameter("crmRole")));
        // TODO: Update the user's session so that they have new abilities -- or fewer abilities       
      }
      //reload the user record.
      thisUser = UserUtils.loadUser(thisUser.getId());
    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return ("SystemError");
    } finally {
      freeConnection(context, db);
    }
    if (count > 0) {
      // Trigger the workflow
      processUpdateHook(context, prevUser, thisUser);
    }
    return "SaveOK";
  }


  /**
   * Action to generate a list of user logins for a specific user
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandLogins(ActionContext context) {
    if (!getUser(context).getAccessAdmin()) {
      return "PermissionError";
    }
    Connection db = null;
    try {
      db = getConnection(context);
      // Load the user
      User thisUser = new User(db, this.getGroupId(context), Integer.parseInt(context.getRequest().getParameter("id")));
      context.getRequest().setAttribute("thisUser", thisUser);
      // Use a pagedList
      PagedListInfo adminUsersLoginInfo = this.getPagedListInfo(context, "adminUsersLoginInfo");
      adminUsersLoginInfo.setLink(context, ctx(context) + "/AdminUserDetails.do?command=Logins&id=" + thisUser.getId());
      // Load the logins
      UserLogList logins = new UserLogList();
      logins.setUserId(thisUser.getId());
      logins.setPagedListInfo(adminUsersLoginInfo);
      logins.buildList(db);
      context.getRequest().setAttribute("logins", logins);
    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return ("SystemError");
    } finally {
      freeConnection(context, db);
    }
    return "LoginsOK";
  }

  public String executeCommandProjects(ActionContext context) {
    if (!getUser(context).getAccessAdmin()) {
      return "PermissionError";
    }
    resetPagedListInfo(context);
    Connection db = null;
    try {
      db = getConnection(context);
      // Load the user
      User thisUser = new User(db, this.getGroupId(context), Integer.parseInt(context.getRequest().getParameter("id")));
      context.getRequest().setAttribute("thisUser", thisUser);
      // Use a pagedList
      PagedListInfo adminUsersProjectsInfo = this.getPagedListInfo(context, "adminUsersProjectsInfo", 50);
      adminUsersProjectsInfo.setLink(context, ctx(context) + "/AdminUserDetails.do?command=Projects&id=" + thisUser.getId());
      // Load the projects
      ProjectList projects = new ProjectList();
      projects.setProjectsForUser(thisUser.getId());
      projects.setPagedListInfo(adminUsersProjectsInfo);
      projects.buildList(db);
      context.getRequest().setAttribute("projects", projects);
      // Load the team members
      projects.buildTeam(db);
    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return ("SystemError");
    } finally {
      freeConnection(context, db);
    }
    return "ProjectsOK";
  }

  public String executeCommandLanguages(ActionContext context) {
    if (!getUser(context).getAccessAdmin()) {
      return "PermissionError";
    }
    Connection db = null;
    try {
      db = getConnection(context);
      // Load the user
      User thisUser = new User(db, this.getGroupId(context), Integer.parseInt(context.getRequest().getParameter("id")));
      context.getRequest().setAttribute("thisUser", thisUser);
      // Load the languages
      LanguagePackList packList = new LanguagePackList();
      packList.setBuildTeamMembers(true);
      packList.buildList(db);
      context.getRequest().setAttribute("languagePackList", packList);
    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return ("SystemError");
    } finally {
      freeConnection(context, db);
    }
    return "LanguagesOK";
  }

  public String executeCommandSetLanguageMember(ActionContext context) {
    if (!getUser(context).getAccessAdmin()) {
      return "PermissionError";
    }
    Connection db = null;
    try {
      // Maintainer, Translator, Reviewer; true/false
      String function = context.getRequest().getParameter("function");
      String value = context.getRequest().getParameter("value");
      int userId = Integer.parseInt(context.getRequest().getParameter("userId"));
      // Determine the language
      int languagePackId = Integer.parseInt(StringUtils.getNumbersOnly(function));
      // Determine the field to be updated
      int languageField = Constants.UNDEFINED;
      if (function.startsWith("maintainer")) {
        languageField = LanguageTeamList.TEAM_MAINTAINER;
      } else if (function.startsWith("translator")) {
        languageField = LanguageTeamList.TEAM_TRANSLATOR;
      } else if (function.startsWith("reviewer")) {
        languageField = LanguageTeamList.TEAM_REVIEWER;
      }
      // Determine if adding or removing
      int languageFunction = Constants.UNDEFINED;
      if ("true".equals(value)) {
        languageFunction = LanguageTeamList.TEAM_ADD;
      } else {
        languageFunction = LanguageTeamList.TEAM_REMOVE;
      }
      db = getConnection(context);
      LanguageTeamList.modifyMember(db, languagePackId, userId, languageField, languageFunction);
    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return ("SystemError");
    } finally {
      freeConnection(context, db);
    }
    return null;
  }

  public String executeCommandWebSites(ActionContext context) {
    if (!getUser(context).getAccessAdmin()) {
      return "PermissionError";
    }
    Connection db = null;
    try {
      db = getConnection(context);
      // Load the user
      User thisUser = new User(db, this.getGroupId(context), Integer.parseInt(context.getRequest().getParameter("id")));
      context.getRequest().setAttribute("thisUser", thisUser);

      // Load the web sites
      WebSiteLanguageList websiteList = new WebSiteLanguageList();
      websiteList.setBuildTeamMembers(true);
      websiteList.buildList(db);
      context.getRequest().setAttribute("websiteList", websiteList);

    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return ("SystemError");
    } finally {
      freeConnection(context, db);
    }
    return "WebSitesOK";
  }

  public String executeCommandSetWebSiteMember(ActionContext context) {
    if (!getUser(context).getAccessAdmin()) {
      return "PermissionError";
    }
    Connection db = null;
    try {
      // Maintainer, Translator, Reviewer; true/false
      String function = context.getRequest().getParameter("function");
      String value = context.getRequest().getParameter("value");
      int userId = Integer.parseInt(context.getRequest().getParameter("userId"));
      // Determine the language
      int languageId = Integer.parseInt(StringUtils.getNumbersOnly(function));
      // Determine if adding or removing
      int languageFunction = Constants.UNDEFINED;
      if ("true".equals(value)) {
        languageFunction = WebSiteTeamList.TEAM_ADD;
      } else {
        languageFunction = WebSiteTeamList.TEAM_REMOVE;
      }
      db = getConnection(context);
      WebSiteTeamList.modifyMember(db, languageId, userId, languageFunction);
    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return ("SystemError");
    } finally {
      freeConnection(context, db);
    }
    return null;
  }

}

