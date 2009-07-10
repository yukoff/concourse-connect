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

package com.concursive.connect.cms.portal.actions;

import com.concursive.commons.date.DateUtils;
import com.concursive.commons.http.RequestUtils;
import com.concursive.commons.images.ImageUtils;
import com.concursive.commons.objects.ObjectUtils;
import com.concursive.commons.text.StringUtils;
import com.concursive.commons.web.mvc.actions.ActionContext;
import com.concursive.connect.Constants;
import com.concursive.connect.cache.utils.CacheUtils;
import com.concursive.connect.cms.portal.dao.*;
import com.concursive.connect.cms.portal.utils.DashboardUtils;
import com.concursive.connect.config.ApplicationPrefs;
import com.concursive.connect.indexer.IIndexerSearch;
import com.concursive.connect.web.controller.actions.GenericAction;
import com.concursive.connect.web.modules.badges.dao.ProjectBadgeList;
import com.concursive.connect.web.modules.blog.dao.BlogPostList;
import com.concursive.connect.web.modules.common.social.rating.beans.RatingBean;
import com.concursive.connect.web.modules.common.social.rating.dao.Rating;
import com.concursive.connect.web.modules.discussion.dao.TopicList;
import com.concursive.connect.web.modules.documents.beans.FileDownload;
import com.concursive.connect.web.modules.documents.dao.FileItem;
import com.concursive.connect.web.modules.documents.dao.FileItemList;
import com.concursive.connect.web.modules.documents.dao.Thumbnail;
import com.concursive.connect.web.modules.documents.utils.FileInfo;
import com.concursive.connect.web.modules.documents.utils.HttpMultiPartParser;
import com.concursive.connect.web.modules.documents.utils.ThumbnailUtils;
import com.concursive.connect.web.modules.issues.dao.TicketCategoryList;
import com.concursive.connect.web.modules.issues.dao.TicketList;
import com.concursive.connect.web.modules.lists.dao.Task;
import com.concursive.connect.web.modules.lists.dao.TaskCategoryList;
import com.concursive.connect.web.modules.lists.dao.TaskList;
import com.concursive.connect.web.modules.lists.utils.TaskUtils;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.members.dao.TeamMember;
import com.concursive.connect.web.modules.members.dao.TeamMemberList;
import com.concursive.connect.web.modules.plans.dao.*;
import com.concursive.connect.web.modules.profile.dao.*;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import com.concursive.connect.web.modules.search.utils.SearchUtils;
import com.concursive.connect.web.modules.wiki.dao.WikiList;
import com.concursive.connect.web.portal.PortletManager;
import com.concursive.connect.web.portal.ProjectPortalBean;
import com.concursive.connect.web.portal.ProjectPortalUtils;
import com.concursive.connect.web.utils.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.*;

/**
 * Actions for working with projects
 *
 * @author matt rajkowski
 * @version $Id$
 * @created November 6, 2001
 */
public final class ProjectManagement extends GenericAction {

  private static Log LOG = LogFactory.getLog(ProjectManagement.class);

  /**
   * Show the Project List by default
   *
   * @param context Description of Parameter
   * @return Description of the Returned Value
   */
  public String executeCommandDefault(ActionContext context) {
    setMaximized(context);
    return executeCommandDashboard(context);
  }

  public String executeCommandDashboard(ActionContext context) {
    setMaximized(context);
    if (getUser(context).getId() < 0) {
      return "PermissionError";
    }
    Connection db = null;
    try {
      DashboardPage page = DashboardUtils.loadDashboardPage(DashboardTemplateList.TYPE_NAVIGATION, "User Dashboard");
      context.getRequest().setAttribute("dashboardPage", page);
      db = getConnection(context);
      boolean isAction = PortletManager.processPage(context, db, page);
    } catch (Exception errorMessage) {
      context.getRequest().setAttribute("Error", errorMessage);
      return ("SystemError");
    } finally {
      this.freeConnection(context, db);
    }
    return ("DashboardOK");
  }

  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandProjectList(ActionContext context) {
    setMaximized(context);
    if (getUser(context).getId() < 0) {
      return "PermissionError";
    }
    Connection db = null;
    try {
      db = getConnection(context);
      //PagedList Info
      ProjectList projects = new ProjectList();
      PagedListInfo projectListInfo = this.getPagedListInfo(context, "projectListInfo");
      projectListInfo.setLink(context, ctx(context) + "/ProjectManagement.do?command=ProjectList");
      if (projectListInfo.getListView() == null) {
        projectListInfo.setItemsPerPage(0);
        // My open projects
        projectListInfo.setListView("open");
        // Categories
        projectListInfo.addFilter(1, "-1");
      }
      projects.setPagedListInfo(projectListInfo);
      //Project Info
      projects.setGroupId(getUser(context).getGroupId());
      projects.setProjectsForUser(getUserId(context));
      projects.setIncludeGuestProjects(false);
      projects.setPortalState(Constants.FALSE);
      //projects.setUserRange(this.getUserRange(context));
      if (projectListInfo.getListView().equals("open")) {
        projects.setOpenProjectsOnly(true);
      } else if (projectListInfo.getListView().equals("closed")) {
        projects.setClosedProjectsOnly(true);
      } else if (projectListInfo.getListView().equals("recent")) {
        projects.setDaysLastAccessed(7);
      }
      projects.setInvitationAcceptedOnly(true);
      projects.setBuildOverallIssues(true);
      //projects.setBuildPermissions(true);
      projects.setCategoryId(projectListInfo.getFilterValue("listFilter1"));
      projects.buildList(db);
      context.getRequest().setAttribute("projectList", projects);
      // Load the progress
      HashMap<Integer, RequirementList> projectRequirementsMap = new HashMap<Integer, RequirementList>();
      for (Project thisProject : projects) {
        RequirementList requirementList = new RequirementList();
        requirementList.setProjectId(thisProject.getId());
        requirementList.buildList(db);
        requirementList.buildPlanActivityCounts(db);
        projectRequirementsMap.put(thisProject.getId(), requirementList);
      }
      context.getRequest().setAttribute("projectRequirementsMap", projectRequirementsMap);
      // Prepare the list of categories to display, based on categories used
      ProjectCategoryList categoryList = new ProjectCategoryList();
      categoryList.setCategoriesForProjectUser(getUserId(context));
      categoryList.setIncludeId(projectListInfo.getFilterValue("listFilter1"));
      categoryList.setTopLevelOnly(true);
      categoryList.buildList(db);
      HtmlSelect thisSelect = categoryList.getHtmlSelect();
      thisSelect.addItem(-1, "All Categories", 0);
      context.getRequest().setAttribute("projectCategoryList", thisSelect);
    } catch (Exception errorMessage) {
      context.getRequest().setAttribute("Error", errorMessage);
      return ("SystemError");
    } finally {
      this.freeConnection(context, db);
    }
    return ("ProjectListOK");
  }


  /**
   * Description of the Method
   *
   * @param context Description of Parameter
   * @return Description of the Returned Value
   */
  public String executeCommandRSVP(ActionContext context) {
    setMaximized(context);
    if (getUser(context).getId() < 0) {
      return "PermissionError";
    }
    Connection db = null;
    try {
      db = getConnection(context);
      //Get a list of projects that user has been invited to
      ProjectList invitedProjects = new ProjectList();
      invitedProjects.setProjectsForUser(getUserId(context));
      invitedProjects.setIncludeGuestProjects(false);
      invitedProjects.setInvitationPendingOnly(true);
      invitedProjects.buildList(db);
      context.getRequest().setAttribute("invitedProjectList", invitedProjects);
    } catch (Exception errorMessage) {
      context.getRequest().setAttribute("Error", errorMessage);
      return ("SystemError");
    } finally {
      this.freeConnection(context, db);
    }
    return ("RSVPOK");
  }


  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandOverview(ActionContext context) {
    setMaximized(context);
    if (getUser(context).getId() < 0) {
      return "PermissionError";
    }
    int MINIMIZED_ITEMS_PER_PAGE = 5;
    // For using the overview in a project tab
    String projectId = context.getRequest().getParameter("pid");
    String projectLink = "";
    if (projectId != null) {
      projectLink = "&pid=" + projectId;
    }
    String link = ctx(context) + "/ProjectManagement.do?command=Overview" + projectLink;
    Connection db = null;
    String sectionId = null;
    if (context.getRequest().getParameter("pagedListSectionId") != null) {
      sectionId = context.getRequest().getParameter("pagedListSectionId");
    }
    // Prepare the drop-down
    PagedListInfo overviewListInfo = this.getPagedListInfo(context, "overviewListInfo");
    overviewListInfo.setLink(context, link);
    if (overviewListInfo.getListView() == null) {
      overviewListInfo.setListView("48hours");
    }
    Calendar cal = Calendar.getInstance();
    if (overviewListInfo.getListView().equals("today")) {
      cal.set(Calendar.HOUR_OF_DAY, 0);
      cal.set(Calendar.MINUTE, 0);
      cal.set(Calendar.SECOND, 0);
    } else if (overviewListInfo.getListView().equals("24hours")) {
      cal.add(Calendar.DAY_OF_MONTH, -1);
    } else if (overviewListInfo.getListView().equals("48hours")) {
      cal.add(Calendar.DAY_OF_MONTH, -2);
    } else if (overviewListInfo.getListView().equals("14days")) {
      // 14 Days
      cal.add(Calendar.DAY_OF_MONTH, -14);
    } else if (overviewListInfo.getListView().equals("30days")) {
      // 30 Days
      cal.add(Calendar.DAY_OF_MONTH, -30);
    } else {
      // 7 Days -- default
      cal.add(Calendar.DAY_OF_MONTH, -7);
    }
    Timestamp alertRangeStart = new Timestamp(cal.getTimeInMillis());
    // Prepare lists
    BlogPostList newsList = new BlogPostList();
    TopicList topicList = new TopicList();
    FileItemList fileItemList = new FileItemList();
    WikiList wikiList = new WikiList();
    //reset the paged lists
    if (context.getRequest().getParameter("resetList") != null && context.getRequest().getParameter("resetList").equals("true")) {
      context.getSession().removeAttribute("overviewNewsListInfo");
      context.getSession().removeAttribute("overviewIssueListInfo");
      context.getSession().removeAttribute("overviewFileItemListListInfo");
      context.getSession().removeAttribute("overviewWikiListInfo");
    }
    // PagedLists needed
    newsList.setPagedListInfo(processPagedListInfo(context, sectionId, "overviewNewsListInfo", "n.start_date", "desc", link, MINIMIZED_ITEMS_PER_PAGE));
    topicList.setPagedListInfo(processPagedListInfo(context, sectionId, "overviewIssueListInfo", "i.last_reply_date", "desc", link, MINIMIZED_ITEMS_PER_PAGE));
    fileItemList.setPagedListInfo(processPagedListInfo(context, sectionId, "overviewFileItemListListInfo", "f.modified", "desc", link, MINIMIZED_ITEMS_PER_PAGE));
    wikiList.setPagedListInfo(processPagedListInfo(context, sectionId, "overviewWikiListInfo", "w.modified", "desc", link, MINIMIZED_ITEMS_PER_PAGE));
    // Query the records
    newsList.setForUser(getUserId(context));
    newsList.setAlertRangeStart(alertRangeStart);
    newsList.setCurrentNews(Constants.TRUE);
    topicList.setForUser(getUserId(context));
    topicList.setAlertRangeStart(alertRangeStart);
    fileItemList.setLinkModuleId(Constants.PROJECTS_FILES);
    fileItemList.setForProjectUser(getUserId(context));
    fileItemList.setAlertRangeStart(alertRangeStart);
    wikiList.setForUser(getUserId(context));
    wikiList.setAlertRangeStart(alertRangeStart);
    try {
      db = getConnection(context);
      // Try to load the project and place in request
      if (projectId != null) {
        Project thisProject = retrieveAuthorizedProject(Integer.parseInt(projectId), context);
        hasProjectAccess(context, thisProject.getId(), "project-details-view");
        context.getRequest().setAttribute("project", thisProject);
      }
      // Build the needed info
      if (!topicList.getPagedListInfo().getExpandedSelection() &&
          !wikiList.getPagedListInfo().getExpandedSelection() &&
          !fileItemList.getPagedListInfo().getExpandedSelection()) {
        newsList.buildList(db);
      }
      if (!newsList.getPagedListInfo().getExpandedSelection() &&
          !wikiList.getPagedListInfo().getExpandedSelection() &&
          !fileItemList.getPagedListInfo().getExpandedSelection()) {
        topicList.buildList(db);
      }
      if (!topicList.getPagedListInfo().getExpandedSelection() &&
          !wikiList.getPagedListInfo().getExpandedSelection() &&
          !newsList.getPagedListInfo().getExpandedSelection()) {
        fileItemList.buildList(db);
      }
      if (!newsList.getPagedListInfo().getExpandedSelection() &&
          !topicList.getPagedListInfo().getExpandedSelection() &&
          !fileItemList.getPagedListInfo().getExpandedSelection()) {
        wikiList.buildList(db);
      }
    } catch (Exception errorMessage) {
      context.getRequest().setAttribute("Error", errorMessage);
      return ("SystemError");
    } finally {
      this.freeConnection(context, db);
    }
    context.getRequest().setAttribute("newsList", newsList);
    context.getRequest().setAttribute("issueList", topicList);
    context.getRequest().setAttribute("fileItemList", fileItemList);
    context.getRequest().setAttribute("wikiList", wikiList);
    ClientType clientType = (ClientType) context.getSession().getAttribute("clientType");
    if (clientType != null && clientType.getMobile()) {
      return "MobileOverviewOK";
    }
    return ("OverviewOK");
  }


  public String executeCommandAssignments(ActionContext context) {
    setMaximized(context);
    if (getUser(context).getId() < 0) {
      return "PermissionError";
    }
    int MINIMIZED_ITEMS_PER_PAGE = 5;
    // For using the assignments in a project tab
    String projectId = context.getRequest().getParameter("pid");
    String projectLink = "";
    if (projectId != null) {
      projectLink = "&pid=" + projectId;
    }
    String link = ctx(context) + "/ProjectManagement.do?command=Assignments" + projectLink;
    Connection db = null;
    String sectionId = null;
    if (context.getRequest().getParameter("pagedListSectionId") != null) {
      sectionId = context.getRequest().getParameter("pagedListSectionId");
    }
    // Prepare the drop-down
    PagedListInfo assignmentsListInfo = this.getPagedListInfo(context, "assignmentsListInfo");
    assignmentsListInfo.setLink(context, link);
    //Prepare lists
    AssignmentList assignmentList = new AssignmentList();
    TicketList ticketList = new TicketList();
    //reset the paged lists
    if (context.getRequest().getParameter("resetList") != null && context.getRequest().getParameter("resetList").equals("true")) {
      context.getSession().removeAttribute("assignmentsAssignmentListInfo");
      context.getSession().removeAttribute("assignmentsTicketListInfo");
    }
    //PagedLists needed
    assignmentList.setPagedListInfo(processPagedListInfo(context, sectionId, "assignmentsAssignmentListInfo", "a.due_date", null, link, MINIMIZED_ITEMS_PER_PAGE));
    ticketList.setPagedListInfo(processPagedListInfo(context, sectionId, "assignmentsTicketListInfo", "t.entered", "desc", link, MINIMIZED_ITEMS_PER_PAGE));
    //Query the records
    assignmentList.setForProjectUser(getUserId(context));
    assignmentList.setAssignmentsForUser(getUserId(context));
    assignmentList.setIncompleteOnly(true);
    assignmentList.setOnlyIfRequirementOpen(true);
    assignmentList.setOnlyIfProjectOpen(true);
    ticketList.setForProjectUser(getUserId(context));
    ticketList.setAssignedTo(getUserId(context));
    ticketList.setOnlyOpen(true);
    ticketList.setOnlyIfProjectOpen(true);
    try {
      db = getConnection(context);
      // Try to load the project and place in request
      if (projectId != null) {
        Project thisProject = retrieveAuthorizedProject(Integer.parseInt(projectId), context);
        hasProjectAccess(context, thisProject.getId(), "project-details-view");
        context.getRequest().setAttribute("project", thisProject);
      }
      // Build the needed info
      if (!ticketList.getPagedListInfo().getExpandedSelection()) {
        assignmentList.buildList(db);
        // Build list to display names of requirements
        RequirementList requirementList = new RequirementList();
        requirementList.setAssignmentList(assignmentList);
        requirementList.buildList(db);
        context.getRequest().setAttribute("requirementList", requirementList);
      }
      if (!assignmentList.getPagedListInfo().getExpandedSelection()) {
        ticketList.buildList(db);
      }
    } catch (Exception errorMessage) {
      context.getRequest().setAttribute("Error", errorMessage);
      return ("SystemError");
    } finally {
      this.freeConnection(context, db);
    }
    context.getRequest().setAttribute("assignmentList", assignmentList);
    context.getRequest().setAttribute("ticketList", ticketList);
    return ("AssignmentsOK");
  }


  /**
   * Description of the Method
   *
   * @param context Description of Parameter
   * @return Description of the Returned Value
   */
  public String executeCommandAddProject(ActionContext context) {
    setMaximized(context);
    if (getUser(context).getId() < 0) {
      return "PermissionError";
    }
    if (!getUser(context).getAccessAddProjects()) {
      return "PermissionError";
    }
    Connection db = null;
    try {
      Project thisProject = (Project) context.getFormBean();
      if (thisProject.getRequestDate() == null) {
        thisProject.setRequestDate(DateUtils.roundUpToNextFive());
      }
      db = getConnection(context);
      //Category List
      ProjectCategoryList categoryList = new ProjectCategoryList();
      categoryList.setEnabled(true);
      categoryList.setTopLevelOnly(true);
      categoryList.buildList(db);
      context.getRequest().setAttribute("categoryList", categoryList);
      //Previous projects
      ProjectList projectList = new ProjectList();
      projectList.setGroupId(getUser(context).getGroupId());
      projectList.setEmptyHtmlSelectRecord("--None--");
      // if TE site, then maybe show only templates the user has access to
      projectList.setApprovedOnly(true);
      projectList.setIncludeTemplates(Constants.TRUE);
      projectList.buildList(db);
      context.getRequest().setAttribute("ProjectList", projectList);
    } catch (Exception errorMessage) {
      context.getRequest().setAttribute("Error", errorMessage);
      return ("SystemError");
    } finally {
      this.freeConnection(context, db);
    }
    return ("AddProjectOK");
  }


  /**
   * Description of the Method
   *
   * @param context Description of Parameter
   * @return Description of the Returned Value
   */
  public String executeCommandInsertProject(ActionContext context) {
    if (getUser(context).getId() < 0) {
      return "PermissionError";
    }
    if (!getUser(context).getAccessAddProjects()) {
      return "PermissionError";
    }
    Connection db = null;
    try {
      db = getConnection(context);
      Project thisProject = (Project) context.getFormBean();
      thisProject.setInstanceId(getInstance(context).getId());
      thisProject.setGroupId(getUser(context).getGroupId());
      thisProject.setEnteredBy(getUserId(context));
      thisProject.setModifiedBy(getUserId(context));
      // Only allow projects to be set as public by those with authority
      if (getUser(context).getAccessGuestProjects() || getUser(context).getAccessAdmin()) {
        thisProject.getFeatures().setUpdateAllowGuests(true);
        thisProject.getFeatures().setUpdateAllowParticipants(true);
        thisProject.getFeatures().setUpdateMembershipRequired(true);
      } else {
        thisProject.getFeatures().setUpdateAllowGuests(false);
        thisProject.getFeatures().setUpdateAllowParticipants(false);
        thisProject.getFeatures().setUpdateMembershipRequired(false);
      }
      if (thisProject.insert(db)) {
        CacheUtils.updateValue(Constants.SYSTEM_PROJECT_NAME_CACHE, thisProject.getId(), thisProject.getTitle());
        indexAddItem(context, thisProject);
        // Add the current user to the team
        TeamMember thisMember = new TeamMember();
        thisMember.setProjectId(thisProject.getId());
        thisMember.setUserId(getUserId(context));
        thisMember.setUserLevel(getUserLevel(TeamMember.PROJECT_ADMIN));
        thisMember.setEnteredBy(getUserId(context));
        thisMember.setModifiedBy(getUserId(context));
        thisMember.insert(db);
        // Go to the project
        context.getRequest().setAttribute("project", thisProject);
        processInsertHook(context, thisProject);
        return "InsertProjectOK";
      } else {
        this.processErrors(context, thisProject.getErrors());
        return (executeCommandAddProject(context));
      }
    } catch (Exception errorMessage) {
      context.getRequest().setAttribute("Error", errorMessage);
      return ("SystemError");
    } finally {
      freeConnection(context, db);
    }
  }


  /**
   * Description of the Method
   *
   * @param context Description of Parameter
   * @return Description of the Returned Value
   */
  public String executeCommandModifyProject(ActionContext context) {
    Connection db = null;
    //Params
    String projectId = context.getRequest().getParameter("pid");
    try {
      db = this.getConnection(context);
      Project thisProject = retrieveAuthorizedProject(Integer.parseInt(projectId), context);
      if (!hasProjectAccess(context, thisProject.getId(), "project-details-edit")) {
        return "PermissionError";
      }
      context.getRequest().setAttribute("project", thisProject);
      context.getRequest().setAttribute("IncludeSection", "modifyproject");
      //Category List
      ProjectCategoryList categoryList = new ProjectCategoryList();
      categoryList.setEnabled(true);
      categoryList.setTopLevelOnly(true);
      categoryList.buildList(db);
      context.getRequest().setAttribute("categoryList", categoryList);
      return ("ProjectCenterOK");
    } catch (Exception errorMessage) {
      context.getRequest().setAttribute("Error", errorMessage);
      return ("SystemError");
    } finally {
      this.freeConnection(context, db);
    }
  }


  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandCustomizeProject(ActionContext context) {
    Connection db = null;
    //Params
    String projectId = context.getRequest().getParameter("pid");
    try {
      db = this.getConnection(context);
      Project thisProject = retrieveAuthorizedProject(Integer.parseInt(projectId), context);
      if (!hasProjectAccess(context, thisProject.getId(), "project-setup-customize")) {
        return "PermissionError";
      }
      context.getRequest().setAttribute("project", thisProject);
      context.getRequest().setAttribute("IncludeSection", "setup_customize");
      return ("ProjectCenterOK");
    } catch (Exception errorMessage) {
      context.getRequest().setAttribute("Error", errorMessage);
      return ("SystemError");
    } finally {
      this.freeConnection(context, db);
    }
  }


  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandConfigurePermissions(ActionContext context) {
    Connection db = null;
    //Params
    String projectId = context.getRequest().getParameter("pid");
    try {
      db = this.getConnection(context);
      Project thisProject = retrieveAuthorizedProject(Integer.parseInt(projectId), context);
      if (!hasProjectAccess(context, thisProject.getId(), "project-setup-permissions")) {
        return "PermissionError";
      }
      context.getRequest().setAttribute("project", thisProject);
      context.getRequest().setAttribute("IncludeSection", "setup_permissions");
      //Load the possible permission categories and permissions
      PermissionCategoryLookupList categories = new PermissionCategoryLookupList();
      categories.setIncludeEnabled(Constants.TRUE);
      categories.buildList(db);
      categories.buildResources(db);
      context.getRequest().setAttribute("categories", categories);
      return ("ProjectCenterOK");
    } catch (Exception errorMessage) {
      context.getRequest().setAttribute("Error", errorMessage);
      return ("SystemError");
    } finally {
      this.freeConnection(context, db);
    }
  }

  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandCustomizeStyle(ActionContext context) {
    Connection db = null;
    //Params
    String projectId = context.getRequest().getParameter("pid");
    try {
      db = this.getConnection(context);
      Project thisProject = retrieveAuthorizedProject(Integer.parseInt(projectId), context);
      if (!hasProjectAccess(context, thisProject.getId(), "project-setup-style")) {
        return "PermissionError";
      }
      // Load the file for download
      FileItemList imageList = new FileItemList();
      imageList.setLinkModuleId(Constants.PROJECT_STYLE_FILES);
      imageList.setLinkItemId(thisProject.getId());
      imageList.buildList(db);
      context.getRequest().setAttribute("imageList", imageList);
      context.getRequest().setAttribute("project", thisProject);
      context.getRequest().setAttribute("IncludeSection", "setup_style");
      return ("ProjectCenterOK");
    } catch (Exception errorMessage) {
      errorMessage.printStackTrace();
      context.getRequest().setAttribute("Error", errorMessage);
      return ("SystemError");
    } finally {
      this.freeConnection(context, db);
    }
  }

  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandUpdatePermissions(ActionContext context) {
    Connection db = null;
    String projectId = context.getRequest().getParameter("pid");
    try {
      db = this.getConnection(context);
      Project thisProject = retrieveAuthorizedProject(Integer.parseInt(projectId), context);
      //Make sure user can modify permissions
      if (!hasProjectAccess(context, thisProject.getId(), "project-setup-permissions")) {
        return "PermissionError";
      }
      PermissionList.updateProjectPermissions(db, context.getRequest(), Integer.parseInt(projectId));
      // Redirect back to the setup page
      context.getRequest().setAttribute("project", thisProject);
      return "UpdatePermissionsOK";
    } catch (Exception errorMessage) {
      context.getRequest().setAttribute("Error", errorMessage);
      return ("SystemError");
    } finally {
      this.freeConnection(context, db);
    }
  }


  /**
   * Description of the Method
   *
   * @param context Description of Parameter
   * @return Description of the Returned Value
   */
  public String executeCommandUpdateProject(ActionContext context) {
    Project thisProject = (Project) context.getFormBean();
    Project previousProject = null;
    Connection db = null;
    int resultCount = 0;
    try {
      db = this.getConnection(context);
      thisProject.buildPermissionList(db);
      if (!hasProjectAccess(context, thisProject.getId(), "project-details-edit")) {
        return "PermissionError";
      }
      thisProject.setModifiedBy(getUserId(context));
      // Only allow projects to be set as public by those with authority
      if (getUser(context).getAccessGuestProjects() || getUser(context).getAccessAdmin()) {
        thisProject.getFeatures().setUpdateAllowGuests(true);
        thisProject.getFeatures().setUpdateAllowParticipants(true);
        thisProject.getFeatures().setUpdateMembershipRequired(true);
      } else {
        thisProject.getFeatures().setUpdateAllowGuests(false);
        thisProject.getFeatures().setUpdateAllowParticipants(false);
        thisProject.getFeatures().setUpdateMembershipRequired(false);
      }
      previousProject = ProjectUtils.loadProject(thisProject.getId());
      resultCount = thisProject.update(db);
      if (resultCount == -1) {
        this.processErrors(context, thisProject.getErrors());
        //Category List
        ProjectCategoryList categoryList = new ProjectCategoryList();
        categoryList.setEnabled(true);
        categoryList.setTopLevelOnly(true);
        categoryList.buildList(db);
        context.getRequest().setAttribute("categoryList", categoryList);
      } else if (resultCount == 1) {
        // Only allow the system default to be set by the admin
        if (getUser(context).getAccessAdmin() && thisProject.getSystemDefault()) {
          thisProject.updateSystemDefault(db);
        }
        indexAddItem(context, ProjectUtils.loadProject(thisProject.getId()));
      }
    } catch (Exception errorMessage) {
      context.getRequest().setAttribute("Error", errorMessage);
      return ("SystemError");
    } finally {
      this.freeConnection(context, db);
    }
    //Results
    if (resultCount == -1) {
      context.getRequest().setAttribute("project", thisProject);
      return ("ModifyProjectOK");
    } else if (resultCount == 1) {
      context.getRequest().setAttribute("project", thisProject);
      processUpdateHook(context, previousProject, thisProject);
      return ("UpdateProjectOK");
    } else {
      context.getRequest().setAttribute("Error", NOT_UPDATED_MESSAGE);
      return ("UserError");
    }
  }

  /**
   * Unset the owner of the project specified on context
   *
   * @param context the ActionContext with project to be modified
   *                specified
   * @return String representing the result of the action
   */
  public String executeCommandRemoveOwner(ActionContext context) {
    String projectIdStr = context.getRequest().getParameter("pid");
    int projectId = Integer.parseInt(projectIdStr);
    Connection db;
    try {
      Project thisProject = retrieveAuthorizedProject(projectId, context);
      db = this.getConnection(context);
      thisProject.buildPermissionList(db);
      if (!hasProjectAccess(context, thisProject.getId(), "project-details-edit")) {
        return "PermissionError";
      }
      thisProject.setOwner(-1);
      thisProject.update(db);
      context.getRequest().setAttribute("project", thisProject);
      return "UpdateOwnerOK";
    } catch (Exception e) {
      e.printStackTrace();
      context.getRequest().setAttribute("Error", NOT_UPDATED_MESSAGE);
      return ("UserError");
    }
  }

  /**
   * Unset the owner of the project specified on context
   *
   * @param context the ActionContext with project to be modified
   *                specified
   * @return String representing the result of the action
   */
  public String executeCommandSetOwner(ActionContext context) {
    int projectId = Integer.parseInt(context.getRequest().getParameter("pid"));
    int newOwnerId = Integer.parseInt(context.getRequest().getParameter("owner"));
    Connection db;
    try {
      Project thisProject = retrieveAuthorizedProject(projectId, context);
      db = this.getConnection(context);
      thisProject.buildPermissionList(db);
      if (!hasProjectAccess(context, thisProject.getId(), "project-details-edit")) {
        return "PermissionError";
      }
      thisProject.setOwner(newOwnerId);
      thisProject.update(db);
      context.getRequest().setAttribute("project", thisProject);
      return "UpdateOwnerOK";
    } catch (Exception e) {
      e.printStackTrace();
      context.getRequest().setAttribute("Error", NOT_UPDATED_MESSAGE);
      return ("UserError");
    }
  }


  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandUpdateFeatures(ActionContext context) {
    Project thisProject = (Project) context.getFormBean();
    Connection db = null;
    int resultCount = 0;
    try {
      db = this.getConnection(context);
      thisProject.buildPermissionList(db);
      if (!hasProjectAccess(context, thisProject.getId(), "project-setup-customize")) {
        return "PermissionError";
      }
      thisProject.setModifiedBy(getUserId(context));
      resultCount = thisProject.updateFeatures(db);
      if (resultCount == -1) {
        this.processErrors(context, thisProject.getErrors());
        context.getRequest().setAttribute("project", thisProject);
        return ("CustomizeProjectOK");
      }
      if (resultCount == 1) {
        // Redirect to the setup page
        Project project = ProjectUtils.loadProject(thisProject.getId());
        context.getRequest().setAttribute("project", project);
        return ("UpdateFeaturesOK");
      }
    } catch (Exception errorMessage) {
      context.getRequest().setAttribute("Error", errorMessage);
      return ("SystemError");
    } finally {
      this.freeConnection(context, db);
    }
    context.getRequest().setAttribute("Error", NOT_UPDATED_MESSAGE);
    return ("UserError");
  }

  /**
   * This method will read in the CSS set on the request and it's
   * enabled status, then update the project record with the values
   *
   * @param context - the ActionContext
   * @return result - a String representing the result of the action
   */
  public String executeCommandUpdateStyle(ActionContext context) {
    Project thisProject = (Project) context.getFormBean();
    Connection db = null;
    try {
      db = this.getConnection(context);
      thisProject.buildPermissionList(db);
      if (!hasProjectAccess(context, thisProject.getId(), "project-setup-style")) {
        return "PermissionError";
      }
      thisProject.setModifiedBy(getUserId(context));
      thisProject.updateStyle(db);
      // Redirect to the setup page
      Project project = ProjectUtils.loadProject(thisProject.getId());
      context.getRequest().setAttribute("project", project);
      return ("UpdateFeaturesOK");
    } catch (Exception errorMessage) {
      errorMessage.printStackTrace();
      context.getRequest().setAttribute("Error", errorMessage);
      return ("SystemError");
    } finally {
      this.freeConnection(context, db);
    }
  }

  public String executeCommandUploadStyleImage(ActionContext context) {
    Connection db = null;
    boolean recordInserted = false;
    try {
      String filePath = this.getPath(context, "projects");
      //Process the form data
      HttpMultiPartParser multiPart = new HttpMultiPartParser();
      multiPart.setUsePathParam(false);
      multiPart.setUseUniqueName(true);
      multiPart.setUseDateForFolder(true);
      multiPart.setExtensionId(getUserId(context));
      HashMap parts = multiPart.parseData(context.getRequest(), filePath);
      String projectId = (String) parts.get("pid");
      String subject = "Style Image";
      db = getConnection(context);
      // Project
      Project thisProject = retrieveAuthorizedProject(Integer.parseInt(projectId), context);
      if (!hasProjectAccess(context, thisProject.getId(), "project-setup-style")) {
        return "PermissionError";
      }
      context.getRequest().setAttribute("project", thisProject);
      // Update the database with the resulting file
      if (parts.get("id" + projectId) instanceof FileInfo) {
        FileInfo newFileInfo = (FileInfo) parts.get("id" + projectId);
        FileItem thisItem = new FileItem();
        thisItem.setLinkModuleId(Constants.PROJECT_STYLE_FILES);
        thisItem.setLinkItemId(thisProject.getId());
        thisItem.setEnteredBy(getUserId(context));
        thisItem.setModifiedBy(getUserId(context));
        thisItem.setSubject(subject);
        thisItem.setClientFilename(newFileInfo.getClientFileName());
        thisItem.setFilename(newFileInfo.getRealFilename());
        thisItem.setSize(newFileInfo.getSize());
        thisItem.setImageSize(ImageUtils.getImageSize(newFileInfo.getLocalFile()));
        // check to see if this filename already exists for automatic versioning
        FileItemList fileItemList = new FileItemList();
        fileItemList.setLinkModuleId(Constants.PROJECT_STYLE_FILES);
        fileItemList.setLinkItemId(thisProject.getId());
        fileItemList.setFilename(newFileInfo.getClientFileName());
        fileItemList.buildList(db);
        if (fileItemList.size() == 0) {
          // this is a new document
          thisItem.setVersion(1.0);
          recordInserted = thisItem.insert(db);
        } else {
          // this is a new version of an existing document
          FileItem previousItem = fileItemList.get(0);
          thisItem.setId(previousItem.getId());
          thisItem.setVersion(previousItem.getVersionNextMajor());
          recordInserted = thisItem.insertVersion(db);
        }
        thisItem.setDirectory(filePath);
        if (!recordInserted) {
          processErrors(context, thisItem.getErrors());
        }
        // Image List
        FileItemList imageList = new FileItemList();
        imageList.setLinkModuleId(Constants.PROJECT_STYLE_FILES);
        imageList.setLinkItemId(thisProject.getId());
        imageList.buildList(db);
        context.getRequest().setAttribute("imageList", imageList);
        // Send the image name so it can be auto-selected
        context.getRequest().setAttribute("uploadedImage", newFileInfo.getClientFileName());
        context.getRequest().setAttribute("project", thisProject);
      }
    } catch (Exception e) {
      e.printStackTrace();
      context.getRequest().setAttribute("Error", e);
      return ("SystemError");
    } finally {
      freeConnection(context, db);
    }
    return "UploadStyleImageOK";
  }

  public String executeCommandStyleImg(ActionContext context) {
    Connection db = null;
    String pid = context.getRequest().getParameter("pid");
    String filename = context.getRequest().getParameter("subject");
    String thumbnailValue = context.getRequest().getParameter("th");
    boolean isDownload = "true".equals(context.getRequest().getParameter("download"));

    int projectId = Integer.parseInt(pid);
    FileItem fileItem = null;
    Thumbnail thumbnail = null;
    boolean showThumbnail = "true".equals(thumbnailValue);

    FileDownload fileDownload = new FileDownload();
    try {
      // Check project permissions
      Project thisProject = retrieveAuthorizedProject(projectId, context);
      if (!thisProject.getPortal() && !thisProject.getApproved()) {
        return "PermissionError";
      }
      // Load the file for download
      db = getConnection(context);
      FileItemList fileItemList = new FileItemList();
      fileItemList.setLinkModuleId(Constants.PROJECT_STYLE_FILES);
      fileItemList.setLinkItemId(projectId);
      fileItemList.setFilename(filename);
      fileItemList.buildList(db);
      if (fileItemList.size() > 0) {
        fileItem = fileItemList.get(0);
        if (showThumbnail) {
          thumbnail = ThumbnailUtils.retrieveThumbnail(db, fileItem, 0, 0, this.getPath(context, "projects"));
          String filePath = this.getPath(context, "projects") + getDatePath(fileItem.getModified()) + thumbnail.getFilename();
          fileDownload.setFullPath(filePath);
          fileDownload.setFileTimestamp(fileItem.getModificationDate().getTime());
        } else {
          String filePath = this.getPath(context, "projects") + getDatePath(fileItem.getModified()) + (showThumbnail ? fileItem.getThumbnailFilename() : fileItem.getFilename());
          fileDownload.setFullPath(filePath);
          fileDownload.setDisplayName(fileItem.getClientFilename());
        }
      }
    } catch (Exception e) {
    } finally {
      // Free the connection while the file downloads
      freeConnection(context, db);
    }

    try {
      // Stream the file
      if (thumbnail != null) {
        fileDownload.streamThumbnail(context, thumbnail);
      } else if (fileItem != null && fileDownload.fileExists()) {
        if (isDownload) {
          fileDownload.sendFile(context);
        } else {
          fileDownload.setFileTimestamp(fileItem.getModificationDate().getTime());
          fileDownload.streamContent(context);
        }
        //Get a db connection now that the download is complete
        //db = getConnection(context);
        //fileItem.updateCounter(db);
      }
    } catch (Exception e) {
      LOG.error("error", e);
      context.getRequest().setAttribute("Error", e);
      return ("SystemError");
    } finally {
      //this.freeConnection(context, db);
    }
    return null;
  }

  public String executeCommandDeleteStyleImage(ActionContext context) {
    Connection db = null;
    String pid = context.getRequest().getParameter("pid");
    String filename = context.getRequest().getParameter("imageList");
    try {
      int projectId = Integer.parseInt(pid);
      db = getConnection(context);
      // Check project permissions
      Project thisProject = retrieveAuthorizedProject(projectId, context);
      context.getRequest().setAttribute("project", thisProject);

      // Check access to this project
      boolean allowed = true;
      if (!hasProjectAccess(context, thisProject.getId(), "project-setup-style")) {
        return "PermissionError";
      }
      // Load the file
      FileItemList fileItemList = new FileItemList();
      fileItemList.setLinkModuleId(Constants.PROJECT_STYLE_FILES);
      fileItemList.setLinkItemId(projectId);
      fileItemList.setFilename(filename);
      fileItemList.buildList(db);
      for (FileItem fileItem : fileItemList) {
        fileItem.delete(db, getPath(context, "projects"));
      }
    } catch (Exception e) {
      LOG.error("error", e);
    } finally {
      freeConnection(context, db);
    }
    return "DeleteStyleImageOK";
  }


  /**
   * Description of the Method
   *
   * @param context Description of Parameter
   * @return Description of the Returned Value
   */
  public String executeCommandProjectCenter(ActionContext context) {
    Connection db = null;
    Project thisProject = null;
    // Parameters
    String projectId = context.getRequest().getParameter("portlet-pid");
    if (projectId == null) {
      projectId = context.getRequest().getParameter("pid");
    }
    if (projectId == null) {
      projectId = (String) context.getRequest().getAttribute("pid");
    }
    // Determine the section to display
    String section = context.getRequest().getParameter("portlet-section");
    if (section == null && context.getRequest().getParameter("portlet-pid") == null) {
      section = context.getRequest().getParameter("section");
    }
    if (section == null || section.equals("")) {
      section = "Profile";
      // Reset any pagedListInfo objects for this new project
      deletePagedListInfo(context, "projectNewsInfo");
      deletePagedListInfo(context, "projectRequirementsInfo");
      deletePagedListInfo(context, "projectAssignmentsInfo");
      deletePagedListInfo(context, "projectIssueCategoryInfo");
      deletePagedListInfo(context, "projectIssuesInfo");
      deletePagedListInfo(context, "projectTicketsInfo");
      deletePagedListInfo(context, "projectTeamInfo");
      deletePagedListInfo(context, "projectAdsInfo");
      deletePagedListInfo(context, "projectClassifiedsInfo");
      deletePagedListInfo(context, "projectEmployeeTeamInfo");
      deletePagedListInfo(context, "projectAccountContactTeamInfo");
      deletePagedListInfo(context, "projectDocumentsGalleryInfo");
      deletePagedListInfo(context, "projectAccountsInfo");
      deletePagedListInfo(context, "projectBadgeInfo");
    }

    // The old URLs may have been bookmarked or indexed, so redirect to new URLs
    String redirect = null;
    // Determine if a redirect is needed
    if ("News".equals(section)) {
      // ProjectManagement.do?command=ProjectCenter&section=News&pid=175 /show/xyz/blog
      redirect = "/show/" + ProjectUtils.loadProject(Integer.parseInt(projectId)).getUniqueId() + "/blog";
    } else if ("Wiki".equals(section)) {
      // ProjectManagement.do?command=ProjectCenter&section=Wiki&pid=175
      // ProjectManagement.do?command=ProjectCenter&section=Wiki&pid=139&subject=Getting+Started+With+Centric+Team+Elements
      String subject = context.getRequest().getParameter("subject");
      redirect = "/show/" + ProjectUtils.loadProject(Integer.parseInt(projectId)).getUniqueId() + "/wiki" + (subject != null ? "/" + StringUtils.replace(StringUtils.jsEscape(subject), "%20", "+") : "");
    } else if ("Calendar".equals(section)) {
      // ProjectManagement.do?command=ProjectCenter&section=Calendar&source=Calendar&pid=161&reloadCalendarDetails=true
      redirect = "/show/" + ProjectUtils.loadProject(Integer.parseInt(projectId)).getUniqueId() + "/calendar";
    } else if ("Issues_Categories".equals(section)) {
      // ProjectManagement.do?command=ProjectCenter&section=Issues_Categories&pid=175
      redirect = "/show/" + ProjectUtils.loadProject(Integer.parseInt(projectId)).getUniqueId() + "/discussion";
    } else if ("Issues".equals(section)) {
      // ProjectManagement.do?command=ProjectCenter&section=Issues&pid=139&cid=174&resetList=true
      String id = context.getRequest().getParameter("cid");
      redirect = "/show/" + ProjectUtils.loadProject(Integer.parseInt(projectId)).getUniqueId() + "/forum/" + id;
    } else if ("File_Library".equals(section)) {
      // ProjectManagement.do?command=ProjectCenter&section=File_Library&pid=175
      // ProjectManagement.do?command=ProjectCenter&section=File_Library&pid=139&folderId=486
      String folderId = context.getRequest().getParameter("folderId");
      if (folderId == null || "-1".equals(folderId)) {
        redirect = "/show/" + ProjectUtils.loadProject(Integer.parseInt(projectId)).getUniqueId() + "/documents";
      } else {
        redirect = "/show/" + ProjectUtils.loadProject(Integer.parseInt(projectId)).getUniqueId() + "/folder/" + folderId;
      }
    } else if ("Team".equals(section)) {
      // ProjectManagement.do?command=ProjectCenter&section=Team&pid=139
      redirect = "/show/" + ProjectUtils.loadProject(Integer.parseInt(projectId)).getUniqueId() + "/members";
      //} else if ("Lists_Categories".equals(section)) {
      // ProjectManagement.do?command=ProjectCenter&section=Lists_Categories&pid=195
      //redirect = "/show/" + ProjectUtils.loadProject(Integer.parseInt(projectId)).getUniqueId() + "/lists";
      //} else if ("Lists".equals(section)) {
      //  // ProjectManagement.do?command=ProjectCenter&section=Lists&pid=195&cid=168
      //  redirect = "/show/" + ProjectUtils.loadProject(Integer.parseInt(projectId)).getUniqueId() + "/list/" + id;
      //} else if ("Requirements".equals(section)) {
      // ProjectManagement.do?command=ProjectCenter&section=Requirements&pid=175
      //  redirect = "/show/" + ProjectUtils.loadProject(Integer.parseInt(projectId)).getUniqueId() + "/plans";
      //} else if ("Assignments".equals(section)) {
      // ProjectManagement.do?command=ProjectCenter&section=Assignments&rid=435&pid=139
      //String id = context.getRequest().getParameter("rid");
      //redirect = "/show/" + ProjectUtils.loadProject(Integer.parseInt(projectId)).getUniqueId() + "/plan/" + id;
      //} else if ("Tickets".equals(section)) {
      // ProjectManagement.do?command=ProjectCenter&section=Tickets&pid=175
      //redirect = "/show/" + ProjectUtils.loadProject(Integer.parseInt(projectId)).getUniqueId() + "/issues";
    }
    // Perform the redirect
    if (redirect != null) {
      context.getRequest().setAttribute("redirectTo", redirect);
      context.getRequest().removeAttribute("PageLayout");
      return "Redirect301";
    }

    try {
      db = getConnection(context);
      // Determine the project
      thisProject = retrieveAuthorizedProject(Integer.parseInt(projectId), context);

      // Get this project's badges
      ProjectBadgeList projectBadgeList = new ProjectBadgeList();
      projectBadgeList.setProjectId(thisProject.getId());
      projectBadgeList.buildList(db);
      context.getRequest().setAttribute(Constants.REQUEST_PROJECT_BADGE_LIST, projectBadgeList);

      // Determine the first tab that has permission to
      if ("Profile".equals(section) &&
          (!thisProject.getFeatures().getShowProfile()) &&
          thisProject.getFeatures().getShowDashboard()) {
        section = "Dashboard";
      }

      // Note: these will be removed and made default once all modules are converted to portal pages
      if ("Profile".equals(section)) {

        LOG.debug("section=" + section);

        // Turn the URL into a ProjectPortalBean
        ProjectPortalBean portalBean = new ProjectPortalBean(context.getRequest());
        if (LOG.isTraceEnabled()) {
          LOG.trace(portalBean.toString());
        }

        // Determine the user viewing this page
        User thisUser = getUser(context);

        // Get the dashboard page with the portlets
        DashboardPage page = ProjectPortalUtils.retrieveDashboardPage(portalBean);
        if (page == null) {
          LOG.warn("Page could not be found for: " + portalBean.getDomainObject());
          return "404Error";
        }
        // Determine the current module
        String tabName = StringUtils.capitalize(portalBean.getModule());

        if (LOG.isDebugEnabled()) {
          LOG.debug("Checking permission... " + page.getPermission() + " and module " + tabName);
          LOG.debug("  Has permission? " + ProjectUtils.hasAccess(portalBean.getProjectId(), thisUser, page.getPermission()));
          LOG.debug("  Tab enabled? " + ObjectUtils.getObject(page.getProject().getFeatures(), "show" + tabName));
        }

        // Make sure the user has access to the page before showing it
        if ((page.getPermission() != null && !ProjectUtils.hasAccess(portalBean.getProjectId(), thisUser, page.getPermission())) ||
            (tabName != null && !(Boolean) ObjectUtils.getObject(page.getProject().getFeatures(), "show" + tabName))) {
          // The module needs permissions...
          if (thisUser.getId() > -1) {
            // Since the user is logged in, redirect to a permission error page
            LOG.debug("User is logged in, but doesn't have permission to this module");
            return "PermissionError";
          } else {
            // Since the user is not logged in, redirect to a login page
            LOG.debug("User needs to be logged in for this module and have permission to: " + page.getPermission());
            String requestedURL = (String) context.getRequest().getAttribute("requestedURL");
            boolean sslEnabled = "true".equals(getPref(context, "SSL"));
            ApplicationPrefs prefs = getApplicationPrefs(context);
            String url = ("http" + (sslEnabled ? "s" : "") + "://" + RequestUtils.getServerUrl(prefs.get(ApplicationPrefs.WEB_URL), prefs.get(ApplicationPrefs.WEB_PORT), context.getRequest())) + "/login?redirectTo=" + requestedURL;
            context.getRequest().setAttribute("redirectTo", url);
            return "Redirect301";
          }
        }

        LOG.debug("dashboardPage: " + page.getName());
        // Allow access
        context.getRequest().setAttribute("dashboardPage", page);

        // Set team member info
        TeamMember teamMember = ProjectUtils.retrieveTeamMember(thisProject.getId(), thisUser);
        context.getRequest().setAttribute("currentMember", teamMember);

        // Set shared project searcher
        IIndexerSearch projectSearcher = null;
        if ("true".equals(context.getServletContext().getAttribute(Constants.DIRECTORY_INDEX_INITIALIZED))) {
          // Search public projects only
          LOG.debug("Using directory index...");
          projectSearcher = SearchUtils.retrieveSearcher(Constants.INDEXER_DIRECTORY);
        } else {
          // Use the full index because the directory hasn't loaded
          LOG.debug("Using full index...");
          projectSearcher = SearchUtils.retrieveSearcher(Constants.INDEXER_FULL);
        }
        // Search public projects only
        String queryString =
            "(approved:1) " +
                "AND (guests:1) " +
                "AND (closed:0) " +
                "AND (website:0) ";
        // project searcher
        context.getRequest().setAttribute("projectSearcher", projectSearcher);
        context.getRequest().setAttribute("baseQueryString", queryString);

        // Set shared values
        context.getRequest().setAttribute("portletAction", portalBean.getAction());
        context.getRequest().setAttribute("portletDomainObject", portalBean.getDomainObject());
        context.getRequest().setAttribute("portletView", portalBean.getObjectValue());
        context.getRequest().setAttribute("portletParams", portalBean.getParams());

        boolean isAction = PortletManager.processPage(context, db, page);
        if (LOG.isDebugEnabled()) {
          if (context.getResponse().getContentType() != null) {
            LOG.debug("Content type: " + context.getResponse().getContentType());
          }
        }
        if (isAction) {
          return "-none-";
        }

        // Show the portal
        context.getRequest().setAttribute("includePortal", "portal");

        // Set the portal's tab to the corresponding module
        section = portalBean.getModule();

        if (section == null) {
          LOG.warn("Section is null after processing page");
        }

        if ("text".equals(context.getRequest().getParameter("out"))) {
          return ("ProjectCenterPortalOK");
        }

      } else if ("Dashboard".equals(section)) {
        if (!hasProjectAccess(context, thisProject.getId(), "project-dashboard-view")) {
          return "PermissionError";
        }
        int dashboardId = -1;
        String dashboardValue = context.getRequest().getParameter("dash");
        if (dashboardValue != null) {
          dashboardId = Integer.parseInt(dashboardValue);
        }

        // Get the dashboards for this project for menu
        DashboardList dashboards = new DashboardList();
        dashboards.setProjectId(thisProject.getId());
        dashboards.buildList(db);
        context.getRequest().setAttribute("dashboardList", dashboards);

        // Get the selected dashboard
        Dashboard thisDashboard = dashboards.getSelectedFromId(dashboardId);
        if (thisDashboard != null) {
          context.getRequest().setAttribute("dashboard", thisDashboard);
        }
        // Process this page's portlets
        DashboardPage page = null;

        if (thisDashboard != null) {
          page = new DashboardPage(db, DashboardPage.queryIdFromDashboardId(db, thisDashboard.getId()));
          page.setProjectId(thisProject.getId());
          page.getPortletList().setPageId(page.getId());
          page.getPortletList().buildList(db);
          page.setObjectType(DashboardTemplateList.TYPE_PROJECT_TEMPLATES);
        } else {
          page = new DashboardPage();
          page.setProjectId(thisProject.getId());
          page.setObjectType(DashboardTemplateList.TYPE_PROJECT_TEMPLATES);
          DashboardPortlet contentPortlet = new DashboardPortlet();
          contentPortlet.setName("ContentPortlet");
          contentPortlet.setId(0);
          page.getPortletList().add(contentPortlet);
        }

        // Ready to handle local context
        boolean isAction = PortletManager.processPage(context, db, page);
        if (isAction) {
          return ("-none-");
        }
        context.getRequest().setAttribute("dashboardPage", page);
      } else if ("Requirements".equals(section)) {
        context.getSession().removeAttribute("projectAssignmentsInfo");
        if (!hasProjectAccess(context, thisProject.getId(), "project-plan-view")) {
          return "PermissionError";
        }
        PagedListInfo requirementsInfo = this.getPagedListInfo(context, "projectRequirementsInfo", 50);
        requirementsInfo.setLink(context, ctx(context) + "/show/" + thisProject.getUniqueId() + "/plans");
        // Load the requirements
        RequirementList requirements = new RequirementList();
        requirements.setProjectId(thisProject.getId());
        requirements.setPagedListInfo(requirementsInfo);
        requirements.setBuildAssignments(false);
        if (requirementsInfo.getInitializationLevel() == PagedListInfo.LEVEL_INITIALIZED) {
          requirementsInfo.setListView("open");
        }
        if ("all".equals(requirementsInfo.getListView())) {

        } else if ("closed".equals(requirementsInfo.getListView())) {
          requirements.setClosedOnly(true);
        } else if ("open".equals(requirementsInfo.getListView())) {
          requirements.setOpenOnly(true);
        }
        requirements.buildList(db);
        requirements.buildPlanActivityCounts(db);
        context.getRequest().setAttribute("requirements", requirements);
      } else if ("Assignments".equals(section)) {
        if (!hasProjectAccess(context, thisProject.getId(), "project-plan-view")) {
          return "PermissionError";
        }
        // Configure paged list for handling the list view
        PagedListInfo projectAssignmentsInfo = this.getPagedListInfo(context, "projectAssignmentsInfo");
        projectAssignmentsInfo.setItemsPerPage(0);
        projectAssignmentsInfo.setLink(context, ctx(context) + "/ProjectManagement.do?command=ProjectCenter&section=Assignments&pid=" + thisProject.getId());
        //Variables that can be used
        //String folderId = context.getRequest().getParameter("fid");
        String expand = context.getRequest().getParameter("expand");
        String contract = context.getRequest().getParameter("contract");
        String requirementId = context.getRequest().getParameter("rid");
        //Build the requirement and the assignments
        Requirement thisRequirement = new Requirement(db, Integer.parseInt(requirementId));
        context.getRequest().setAttribute("requirement", thisRequirement);
        if ("open".equals(projectAssignmentsInfo.getListView())) {
          thisRequirement.getAssignments().setIncompleteOnly(true);
        } else if ("closed".equals(projectAssignmentsInfo.getListView())) {
          thisRequirement.getAssignments().setClosedOnly(true);
        } else {
          //All
        }
        //HashMap that contains folder state info
        HashMap<Integer, ArrayList> folderState = (HashMap) context.getSession().getAttribute("AssignmentsFolderState");
        if (folderState == null) {
          folderState = new HashMap<Integer, ArrayList>();
          context.getSession().setAttribute("AssignmentsFolderState", folderState);
        }
        ArrayList<Integer> thisFolderState = folderState.get(thisRequirement.getId());
        if (thisFolderState == null) {
          thisFolderState = new ArrayList<Integer>();
          folderState.put(thisRequirement.getId(), thisFolderState);
        }
        if (expand != null) {
          thisFolderState.add(Integer.parseInt(expand));
        }
        if (contract != null) {
          thisFolderState.remove(new Integer(Integer.parseInt(contract)));
        }
        //thisRequirement.buildPlan(db, thisFolderState);
        //Load the map for displaying in order
        RequirementMapList map = new RequirementMapList();
        map.setProjectId(thisProject.getId());
        map.setRequirementId(thisRequirement.getId());
        map.buildList(db);
        context.getRequest().setAttribute("mapList", map);
        // Load the assignments
        AssignmentList assignments = new AssignmentList();
        assignments.setRequirementId(thisRequirement.getId());
        assignments.buildList(db);
        context.getRequest().setAttribute("assignments", assignments);
        //Filter the maplist
        map.filter(assignments, RequirementMapList.FILTER_PRIORITY, projectAssignmentsInfo.getFilterValue("listFilter1"));
        if ("open".equals(projectAssignmentsInfo.getListView())) {
          map.filterAssignments(assignments, "incompleteOnly");
        } else if ("closed".equals(projectAssignmentsInfo.getListView())) {
          map.filterAssignments(assignments, "closedOnly");
        } else {
          //All
        }
        // Load the assignment folders
        AssignmentFolderList folders = new AssignmentFolderList();
        folders.setRequirementId(thisRequirement.getId());
        folders.buildList(db);
        context.getRequest().setAttribute("folders", folders);
        // Load the Priority Lookup for displaying values
        LookupList priorityList = new LookupList(db, "lookup_project_priority");
        context.getRequest().setAttribute("PriorityList", priorityList);
      } else if ("Lists_Categories".equals(section)) {
        if (!hasProjectAccess(context, thisProject.getId(), "project-lists-view")) {
          return "PermissionError";
        }
        TaskCategoryList categoryList = new TaskCategoryList();
        categoryList.setProjectId(thisProject.getId());
        //Check the pagedList filter
        PagedListInfo projectListsCategoriesInfo = this.getPagedListInfo(context, "projectListsCategoriesInfo");
        projectListsCategoriesInfo.setLink(context, ctx(context) + "/show/" + thisProject.getUniqueId() + "/lists");
        projectListsCategoriesInfo.setItemsPerPage(0);
        //Generate the list
        categoryList.setPagedListInfo(projectListsCategoriesInfo);
        categoryList.buildList(db);
        context.getRequest().setAttribute("categoryList", categoryList);
      } else if ("Lists".equals(section)) {
        if (!hasProjectAccess(context, thisProject.getId(), "project-lists-view")) {
          return "PermissionError";
        }
        String categoryId = context.getRequest().getParameter("cid");
        if (categoryId == null) {
          categoryId = context.getRequest().getParameter("categoryId");
        }
        // Add the category to the request
        LookupElement thisCategory = new LookupElement(db, Integer.parseInt(categoryId), "lookup_task_category");
        context.getRequest().setAttribute("category", thisCategory);

        // Make the filters available
        ProjectItemList functionalAreaList = new ProjectItemList();
        functionalAreaList.setProjectId(projectId);
        //functionalAreaList.setEnabled(Constants.TRUE);
        functionalAreaList.buildList(db, ProjectItemList.LIST_FUNCTIONAL_AREA);
        HtmlSelect functionalAreaSelect = functionalAreaList.getHtmlSelect();
        functionalAreaSelect.addItem(-1, "Any", 0);
        functionalAreaSelect.addItem(0, "Unset", 1);
        context.getRequest().setAttribute("functionalAreaList", functionalAreaSelect);

        ProjectItemList complexityList = new ProjectItemList();
        complexityList.setProjectId(projectId);
        //complexityList.setEnabled(Constants.TRUE);
        complexityList.buildList(db, ProjectItemList.LIST_COMPLEXITY);
        HtmlSelect complexitySelect = complexityList.getHtmlSelect();
        complexitySelect.addItem(-1, "Any", 0);
        complexitySelect.addItem(0, "Unset", 1);
        context.getRequest().setAttribute("complexityList", complexitySelect);

        ProjectItemList businessValueList = new ProjectItemList();
        businessValueList.setProjectId(projectId);
        //businessValueList.setEnabled(Constants.TRUE);
        businessValueList.buildList(db, ProjectItemList.LIST_VALUE);
        HtmlSelect businessValueSelect = businessValueList.getHtmlSelect();
        businessValueSelect.addItem(-1, "Any", 0);
        businessValueSelect.addItem(0, "Unset", 1);
        context.getRequest().setAttribute("businessValueList", businessValueSelect);

        ProjectItemList targetSprintList = new ProjectItemList();
        targetSprintList.setProjectId(projectId);
        //targetSprintList.setEnabled(Constants.TRUE);
        targetSprintList.buildList(db, ProjectItemList.LIST_TARGET_SPRINT);
        HtmlSelect targetSprintSelect = targetSprintList.getHtmlSelect();
        targetSprintSelect.addItem(-1, "Any", 0);
        targetSprintSelect.addItem(0, "Unset", 1);
        context.getRequest().setAttribute("targetSprintList", targetSprintSelect);

        ProjectItemList targetReleaseList = new ProjectItemList();
        targetReleaseList.setProjectId(projectId);
        //targetReleaseList.setEnabled(Constants.TRUE);
        targetReleaseList.buildList(db, ProjectItemList.LIST_TARGET_RELEASE);
        HtmlSelect targetReleaseSelect = targetReleaseList.getHtmlSelect();
        targetReleaseSelect.addItem(-1, "Any", 0);
        targetReleaseSelect.addItem(0, "Unset", 1);
        context.getRequest().setAttribute("targetReleaseList", targetReleaseSelect);

        ProjectItemList statusList = new ProjectItemList();
        statusList.setProjectId(projectId);
        //statusList.setEnabled(Constants.TRUE);
        statusList.buildList(db, ProjectItemList.LIST_STATUS);
        HtmlSelect statusSelect = statusList.getHtmlSelect();
        statusSelect.addItem(-1, "Any", 0);
        statusSelect.addItem(0, "Unset", 1);
        context.getRequest().setAttribute("statusList", statusSelect);

        ProjectItemList loeRemainingList = new ProjectItemList();
        loeRemainingList.setProjectId(projectId);
        //loeRemainingList.setEnabled(Constants.TRUE);
        loeRemainingList.buildList(db, ProjectItemList.LIST_LOE_REMAINING);
        HtmlSelect loeRemainingSelect = loeRemainingList.getHtmlSelect();
        loeRemainingSelect.addItem(-1, "Any", 0);
        loeRemainingSelect.addItem(0, "Unset", 1);
        context.getRequest().setAttribute("loeRemainingList", loeRemainingSelect);

        ProjectItemList assignedPriorityList = new ProjectItemList();
        assignedPriorityList.setProjectId(projectId);
        //priorityList.setEnabled(Constants.TRUE);
        assignedPriorityList.buildList(db, ProjectItemList.LIST_ASSIGNED_PRIORITY);
        HtmlSelect assignedPrioritySelect = assignedPriorityList.getHtmlSelect();
        assignedPrioritySelect.addItem(-1, "Any", 0);
        assignedPrioritySelect.addItem(0, "Unset", 1);
        context.getRequest().setAttribute("assignedPriorityList", assignedPrioritySelect);

        // Make the users that own tasks available
        TeamMemberList tmpTeamList = new TeamMemberList();
        tmpTeamList.setProjectId(thisProject.getId());
        tmpTeamList.buildList(db);
        Map<Integer, TeamMember> teamMap = new HashMap<Integer, TeamMember>();
        for (TeamMember tm : tmpTeamList) {
          teamMap.put(tm.getUserId(), tm);
        }
        TeamMemberList teamMemberList = new TeamMemberList();
        TaskList allTaskList = new TaskList();
        allTaskList.setProjectId(thisProject.getId());
        allTaskList.setCategoryId(Integer.parseInt(categoryId));
        allTaskList.buildList(db);
        for (Task t : allTaskList) {
          TeamMember tm = teamMap.get(t.getOwner());
          if (tm != null) {
            teamMap.remove(t.getOwner()); //remove so teammembers are not added more than once
            teamMemberList.add(tm);
          }
        }
        context.getRequest().setAttribute("teamMemberList", teamMemberList);

        // Build the list items
        TaskList outlineList = new TaskList();
        outlineList.setProjectId(thisProject.getId());
        outlineList.setCategoryId(Integer.parseInt(categoryId));
        // Check the pagedList filter
        PagedListInfo projectListsInfo = this.getPagedListInfo(context, "projectListsInfo");
        projectListsInfo.setLink(context, ctx(context) + "/show/" + thisProject.getUniqueId() + "/list/" + Integer.parseInt(categoryId));
        projectListsInfo.setItemsPerPage(0);
        if ("open".equals(projectListsInfo.getListView())) {
          outlineList.setComplete(Constants.FALSE);
        } else if ("closed".equals(projectListsInfo.getListView())) {
          outlineList.setComplete(Constants.TRUE);
        } else {
          outlineList.setComplete(Constants.UNDEFINED);
        }
        if (projectListsInfo.hasListFilter("listFilter1")) {
          outlineList.setFunctionalArea(projectListsInfo.getFilterValueAsInt("listFilter1"));
        }
        if (projectListsInfo.hasListFilter("listFilter2")) {
          outlineList.setComplexity(projectListsInfo.getFilterValueAsInt("listFilter2"));
        }
        if (projectListsInfo.hasListFilter("listFilter3")) {
          outlineList.setBusinessValue(projectListsInfo.getFilterValueAsInt("listFilter3"));
        }
        if (projectListsInfo.hasListFilter("listFilter4")) {
          outlineList.setTargetSprint(projectListsInfo.getFilterValueAsInt("listFilter4"));
        }
        if (projectListsInfo.hasListFilter("listFilter5")) {
          outlineList.setTargetRelease(projectListsInfo.getFilterValueAsInt("listFilter5"));
        }
        if (projectListsInfo.hasListFilter("listFilter6")) {
          outlineList.setStatus(projectListsInfo.getFilterValueAsInt("listFilter6"));
        }
        if (projectListsInfo.hasListFilter("listFilter7")) {
          outlineList.setLoeRemaining(projectListsInfo.getFilterValueAsInt("listFilter7"));
        }
        if (projectListsInfo.hasListFilter("listFilter8")) {
          outlineList.setOwner(projectListsInfo.getFilterValueAsInt("listFilter8"));
        }
        if (projectListsInfo.hasListFilter("listFilter9")) {
          outlineList.setAssignedPriority(projectListsInfo.getFilterValueAsInt("listFilter9"));
        }
        // Generate the list
        outlineList.setPagedListInfo(projectListsInfo);
        outlineList.buildList(db);
        HashMap<Integer, String> taskUrlMap = new HashMap<Integer, String>();
        for (Task t : outlineList) {
          //@TODO move this into TaskList to reduce overhead
          t.buildOwnerLinkedItemRating(db);
          String linkItemUrl = TaskUtils.getLinkItemUrl(ctx(context), t);
          if (linkItemUrl != null) {
            taskUrlMap.put(t.getId(), linkItemUrl);
          }
        }
        context.getRequest().setAttribute("taskUrlMap", taskUrlMap);
        context.getRequest().setAttribute("outlineList", outlineList);
      } else if ("Tickets".equals(section)) {
        if (!hasProjectAccess(context, thisProject.getId(), "project-tickets-view")) {
          return "PermissionError";
        }
        TicketList tickets = new TicketList();
        PagedListInfo projectTicketsInfo = this.getPagedListInfo(context, "projectTicketsInfo");
        projectTicketsInfo.setLink(context, ctx(context) + "/show/" + thisProject.getUniqueId() + "/issues");
        projectTicketsInfo.setMode(PagedListInfo.LIST_VIEW);
        if (projectTicketsInfo.getListView() == null) {
          projectTicketsInfo.setListView("open");
          // Categories
          projectTicketsInfo.addFilter(1, "-1");
        }
        tickets.setPagedListInfo(projectTicketsInfo);
        tickets.setProjectId(thisProject.getId());
        if (!hasProjectAccess(context, thisProject.getId(), "project-tickets-other")) {
          tickets.setOwnTickets(getUser(context).getId());
        }
        if ("all".equals(projectTicketsInfo.getListView())) {

        } else if ("review".equals(projectTicketsInfo.getListView())) {
          tickets.setOnlyOpen(true);
          tickets.setForReview(Constants.TRUE);
        } else if ("closed".equals(projectTicketsInfo.getListView())) {
          tickets.setOnlyClosed(true);
        } else {
          tickets.setOnlyOpen(true);
        }
        tickets.setCatCode(projectTicketsInfo.getFilterValueAsInt("listFilter1"));
        tickets.buildList(db);
        context.getRequest().setAttribute("ticketList", tickets);

        // Prepare the list of categories to display, based on categories used
        TicketCategoryList categoryList = new TicketCategoryList();
        categoryList.setProjectId(thisProject.getId());
        categoryList.setEnabledState(Constants.TRUE);
        categoryList.setCatLevel(0);
        categoryList.buildList(db);
        HtmlSelect thisSelect = categoryList.getHtmlSelect();
        thisSelect.addItem(-1, "All Categories", 0);
        context.getRequest().setAttribute("ticketCategoryList", thisSelect);
        // Try out the YUI list
        // TODO: Make a generic method for using JSON and YUI
        /*
        if ("json".equals(context.getRequest().getParameter("format"))) {
          section += "_json";
      } else {
          section = "tickets_yui";
        }
        */
      } else {
        // Just looking at the details
        if (!hasProjectAccess(context, thisProject.getId(), "project-details-view")) {
          return "PermissionError";
        }
        // Prepare the list of categories to display
        ProjectCategoryList categoryList = new ProjectCategoryList();
        if (thisProject.getCategoryId() > -1) {
          categoryList.setCategoryId(thisProject.getCategoryId());
          categoryList.buildList(db);
        }
        context.getRequest().setAttribute("projectCategoryList", categoryList);
      }
      context.getRequest().setAttribute("project", thisProject);
      context.getRequest().setAttribute("IncludeSection", section.toLowerCase(Locale.ENGLISH));
      // The user has access, so show that they accessed the project
      TeamMember.updateLastAccessed(db, thisProject.getId(), getUser(context));
      // This project is being accessed so trigger the view hook
      processSelectHook(context, thisProject);
      return ("ProjectCenterOK");
    } catch (Exception errorMessage) {
      context.getRequest().setAttribute("Error", errorMessage);
      LOG.error("error", errorMessage);
      return "404Error";
    } finally {
      this.freeConnection(context, db);
    }
  }


  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandDeleteProject(ActionContext context) {
    Connection db = null;
    //Params
    String projectId = context.getRequest().getParameter("pid");
    try {
      db = this.getConnection(context);
      Project thisProject = retrieveAuthorizedProject(Integer.parseInt(projectId), context);
      if (!hasProjectAccess(context, thisProject.getId(), "project-details-delete")) {
        return "PermissionError";
      }
      thisProject.delete(db, getPath(context, "projects"));
      indexDeleteItem(context, thisProject);
      processDeleteHook(context, thisProject);
      return "DeleteProjectOK";
    } catch (Exception errorMessage) {
      context.getRequest().setAttribute("Error", errorMessage);
      return ("SystemError");
    } finally {
      this.freeConnection(context, db);
    }
  }


  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandAcceptProject(ActionContext context) {
    if (getUser(context).getId() < 0) {
      return "PermissionError";
    }
    Connection db = null;
    //Params
    String projectId = context.getRequest().getParameter("pid");
    try {
      db = this.getConnection(context);
      Project thisProject = retrieveAuthorizedProject(Integer.parseInt(projectId), context);
      // Update the user's status
      TeamMember previousMember = new TeamMember(db, thisProject.getId(), getUserId(context));
      ProjectUtils.accept(db, thisProject.getId(), getUserId(context));
      TeamMember thisMember = new TeamMember(db, thisProject.getId(), getUserId(context));
      // Let the workflow know
      processUpdateHook(context, previousMember, thisMember);
      return "AcceptProjectOK";
    } catch (Exception errorMessage) {
      context.getRequest().setAttribute("Error", errorMessage);
      return ("SystemError");
    } finally {
      this.freeConnection(context, db);
    }
  }


  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandRejectProject(ActionContext context) {
    if (getUser(context).getId() < 0) {
      return "PermissionError";
    }
    Connection db = null;
    //Params
    String projectId = context.getRequest().getParameter("pid");
    try {
      db = this.getConnection(context);
      Project thisProject = retrieveAuthorizedProject(Integer.parseInt(projectId), context);
      // Update the user's status
      TeamMember previousMember = new TeamMember(db, thisProject.getId(), getUserId(context));
      ProjectUtils.reject(db, thisProject.getId(), getUserId(context));
      TeamMember thisMember = new TeamMember(db, thisProject.getId(), getUserId(context));
      // Let the workflow know
      processUpdateHook(context, previousMember, thisMember);
      return "AcceptProjectOK";
    } catch (Exception errorMessage) {
      context.getRequest().setAttribute("Error", errorMessage);
      return ("SystemError");
    } finally {
      this.freeConnection(context, db);
    }
  }


  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandResize(ActionContext context) {
    if ("max".equals(context.getRequest().getParameter("view"))) {
      context.getRequest().getSession().setAttribute("projectView", "MAXIMIZED");
    } else {
      context.getRequest().getSession().setAttribute("projectView", "MINIMIZED");
    }
    return ("ResizeOK");
  }

  public String executeCommandRSS(ActionContext context) {
    return ("RSSOK");
  }

  public String executeCommandChangeWatch(ActionContext context) {
    if (getUser(context).getId() < 0) {
      return "PermissionError";
    }
    //Parameters
    String feature = context.getRequest().getParameter("feature");
    String value = context.getRequest().getParameter("value");
    Connection db = null;
    try {
      db = getConnection(context);
      User thisUser = getUser(context);
      thisUser.updateWatch(db, feature, value);
      context.getRequest().setAttribute("feature", feature);
      context.getRequest().setAttribute("watchResult", value);
      return "MakeWatchOK";
    } catch (Exception e) {
      LOG.error("error", e);
    } finally {
      freeConnection(context, db);
    }
    return null;
  }

  public String executeCommandSetRating(ActionContext context) {
    if (getUserId(context) < 0) {
      return "PermissionError";
    }
    Connection db = null;
    //Parameters
    String projectId = context.getRequest().getParameter("pid");
    try {
      db = getConnection(context);
      // Load the project
      Project thisProject = retrieveAuthorizedProject(Integer.parseInt(projectId), context);
      // Save the rating for this user, or update their rating
      String vote = context.getRequest().getParameter("v");
      // Cast the user's vote
      RatingBean thisRating =
          Rating.save(db, getUserId(context), thisProject.getId(), thisProject.getId(), vote, Project.TABLE, Project.PRIMARY_KEY, Constants.UNDEFINED);
      context.getRequest().setAttribute("ratingBean", thisRating);
      CacheUtils.invalidateValue(Constants.SYSTEM_PROJECT_CACHE, thisProject.getId());
      return "RatingOK";
    } catch (Exception errorMessage) {
      context.getRequest().setAttribute("Error", errorMessage);
      return ("SystemError");
    } finally {
      this.freeConnection(context, db);
    }
  }

  /**
   * Description of the Method
   *
   * @param context                  Description of the Parameter
   * @param sectionId                Description of the Parameter
   * @param infoName                 Description of the Parameter
   * @param sortColumn               Description of the Parameter
   * @param sortOrder                Description of the Parameter
   * @param link                     Description of the Parameter
   * @param MINIMIZED_ITEMS_PER_PAGE Description of the Parameter
   * @return Description of the Return Value
   */
  private PagedListInfo processPagedListInfo(ActionContext context, String sectionId, String infoName, String sortColumn, String sortOrder, String link, int MINIMIZED_ITEMS_PER_PAGE) {
    PagedListInfo thisInfo = this.getPagedListInfo(context, infoName, sortColumn, sortOrder);
    thisInfo.setLink(context, link);
    if (sectionId == null) {
      if (!thisInfo.getExpandedSelection()) {
        if (thisInfo.getItemsPerPage() != MINIMIZED_ITEMS_PER_PAGE) {
          thisInfo.setItemsPerPage(MINIMIZED_ITEMS_PER_PAGE);
        }
      } else {
        if (thisInfo.getItemsPerPage() == MINIMIZED_ITEMS_PER_PAGE) {
          thisInfo.setItemsPerPage(PagedListInfo.DEFAULT_ITEMS_PER_PAGE);
        }
      }
    } else if (sectionId.equals(thisInfo.getId())) {
      thisInfo.setExpandedSelection(true);
    }
    return thisInfo;
  }

}

