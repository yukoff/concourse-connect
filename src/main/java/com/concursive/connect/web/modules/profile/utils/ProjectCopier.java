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

package com.concursive.connect.web.modules.profile.utils;

import com.concursive.connect.Constants;
import com.concursive.connect.cms.portal.dao.ProjectItemList;
import com.concursive.connect.web.modules.blog.dao.BlogPostCategoryList;
import com.concursive.connect.web.modules.blog.dao.BlogPostList;
import com.concursive.connect.web.modules.discussion.dao.ForumList;
import com.concursive.connect.web.modules.documents.dao.FileFolderHierarchy;
import com.concursive.connect.web.modules.documents.dao.FileFolderList;
import com.concursive.connect.web.modules.issues.dao.TicketCategoryList;
import com.concursive.connect.web.modules.lists.dao.TaskCategoryList;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.login.utils.UserUtils;
import com.concursive.connect.web.modules.members.dao.TeamMember;
import com.concursive.connect.web.modules.members.dao.TeamMemberList;
import com.concursive.connect.web.modules.plans.dao.Requirement;
import com.concursive.connect.web.modules.plans.dao.RequirementList;
import com.concursive.connect.web.modules.profile.beans.CloneBean;
import com.concursive.connect.web.modules.profile.dao.PermissionList;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.wiki.dao.WikiList;
import com.concursive.connect.web.utils.LookupList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;

/**
 * Description
 *
 * @author matt rajkowski
 * @version $Id$
 * @created Oct 18, 2005
 */

public class ProjectCopier {

  private static Log LOG = LogFactory.getLog(ProjectCopier.class);

  public static Project clone(CloneBean bean, Connection db, int groupId, int userId) throws SQLException {
    Project project = null;
    try {
      db.setAutoCommit(false);

      int oldProjectId = bean.getProjectId();

      // Load permissions and resources for this member
      LOG.debug("ProjectCopier-> ProjectId: " + oldProjectId);
      LOG.debug("ProjectCopier-> UserId: " + userId);

      User user = UserUtils.loadUser(userId);

      LookupList roleList = new LookupList(db, "lookup_project_role");

      // Load old project, change some values, save as new project
      project = new Project(db, oldProjectId);

      TeamMember member = null;
      if (!project.getPortal()) {
        member = new TeamMember(db, oldProjectId, userId);
      }

      // Offset in days
      long offset = 0;
      if (bean.getResetActivityDates() && bean.getRequestDate() != null && project.getRequestDate() != null) {
        offset = bean.getRequestDate().getTime() - project.getRequestDate().getTime();
      }
      project.setId(-1);
      project.setUniqueId(null);
      project.setGroupId(groupId);
      project.setEnteredBy(userId);
      project.setModifiedBy(userId);
      project.setEntered((Timestamp) null);
      project.setModified((Timestamp) null);
      if (bean.getTitle() != null) {
        project.setTitle(bean.getTitle());
      } else {
        project.setTitle(project.getTitle() + " (copy)");
      }
      if (!hasPermission(db, project, user, member, "project-details-view", roleList)) {
        project.setRequestedBy("");
        project.setRequestedByDept("");
        project.setBudget(0);
      }
      if (bean.getRequestDate() != null) {
        project.setRequestDate(bean.getRequestDate());
      } else {
        project.setRequestDate(new Timestamp(System.currentTimeMillis()));
      }
      project.setClosed(false);
      project.setCloseDate((Timestamp) null);
      project.setEstimatedCloseDate((Timestamp) null);
      project.setApprovalDate((Timestamp) null);
      project.setApproved(false);
      project.setClone(true);
      project.buildPermissionList(db);
      project.insert(db);
      if (project.getId() == -1) {
        throw new SQLException("Project object validation failed");
      }
      project.updateFeatures(db);

      // Permissions
      PermissionList permissions = new PermissionList();
      permissions.setProjectId(oldProjectId);
      permissions.buildList(db);
      permissions.setProjectId(project.getId());
      permissions.insert(db);

      // Team
      if (bean.getCloneTeam() &&
          hasPermission(db, project, user, member, "project-team-view", roleList)) {
        TeamMemberList team = new TeamMemberList();
        team.setProjectId(oldProjectId);
        team.buildList(db);
        team.setProjectId(project.getId());
        team.setEnteredBy(userId);
        team.setModifiedBy(userId);
        team.removeTeamMember(userId);
        team.insert(db);
      }
      // Add the current user to the team as Project Lead
      int roleUserLevel = roleList.getIdFromLevel(TeamMember.PROJECT_ADMIN);
      TeamMember thisMember = new TeamMember();
      thisMember.setProjectId(project.getId());
      thisMember.setUserId(userId);
      thisMember.setUserLevel(roleUserLevel);
      thisMember.setEnteredBy(userId);
      thisMember.setModifiedBy(userId);
      thisMember.insert(db);

      // News
      if (bean.getCloneNewsCategories() || bean.getCloneNews()) {
        HashMap categoryMap = new HashMap();
        if (bean.getCloneNewsCategories() &&
            hasPermission(db, project, user, member, "project-news-view", roleList)) {
          // Copy the news categories
          BlogPostCategoryList categoryList = new BlogPostCategoryList();
          categoryList.setProjectId(oldProjectId);
          categoryList.buildList(db);
          categoryList.setProjectId(project.getId());
          categoryList.insert(db, categoryMap);
        }

        if (bean.getCloneNews() &&
            hasPermission(db, project, user, member, "project-news-view", roleList)) {
          // Copy the news
          BlogPostList news = new BlogPostList();
          news.setProjectId(oldProjectId);
          news.buildList(db);
          news.setProjectId(project.getId());
          news.setEnteredBy(userId);
          news.setModifiedBy(userId);
          news.remapCategories(categoryMap);
          // TODO: Adjust startDate and endDate based on today?
          news.insert(db);
          // TODO: Need to copy the news image library
        }
      }

      // Discussion Forums
      if (bean.getCloneForums() &&
          hasPermission(db, project, user, member, "project-discussion-forums-view", roleList)) {
        ForumList forums = new ForumList();
        forums.setProjectId(oldProjectId);
        forums.buildList(db);
        // TODO: Discussion Topics
        forums.setProjectId(project.getId());
        forums.setEnteredBy(userId);
        forums.setModifiedBy(userId);
        forums.insert(db);
      }

      // Document Folders
      if (bean.getCloneDocumentFolders() &&
          hasPermission(db, project, user, member, "project-documents-view", roleList)) {
        FileFolderHierarchy hierarchy = new FileFolderHierarchy();
        hierarchy.setLinkModuleId(Constants.PROJECTS_FILES);
        hierarchy.setLinkItemId(oldProjectId);
        hierarchy.build(db);
        FileFolderList folders = hierarchy.getHierarchy();
        folders.setLinkItemId(project.getId());
        folders.setEnteredBy(userId);
        folders.setModifiedBy(userId);
        folders.insert(db);
      }

      // Lists
      if (bean.getCloneLists() &&
          hasPermission(db, project, user, member, "project-lists-view", roleList)) {
        TaskCategoryList lists = new TaskCategoryList();
        lists.setProjectId(oldProjectId);
        lists.buildList(db);
        lists.setProjectId(project.getId());
        lists.setEnteredBy(userId);
        lists.setModifiedBy(userId);
        lists.setOwner(userId);
        if (bean.getCloneListItems()) {
          lists.insert(db, true);
        } else {
          lists.insert(db, false);
        }
      }

      if (System.getProperty("DEBUG") != null) {
        System.out.println("ProjectCopier-> before wiki");
      }
      // Wiki
      if (bean.getCloneWiki() &&
          hasPermission(db, project, user, member, "project-wiki-view", roleList)) {
        LOG.debug("ProjectCopier-> Inserting wiki");

        // The wiki items
        WikiList wikiList = new WikiList();
        wikiList.setProjectId(oldProjectId);
        wikiList.buildList(db);
        wikiList.setEnteredBy(userId);
        wikiList.setModifiedBy(userId);
        wikiList.setProjectId(project.getId());
        wikiList.insert(db);
        wikiList = null;
        // TODO: The wiki images -- references and files
        /*FileItemList wikiImages = new FileItemList();
        wikiImages.setLinkModuleId(Constants.PROJECT_WIKI_FILES);
        wikiImages.setLinkItemId(oldProjectId);
        wikiImages.buildList(db);
        wikiImages.setLinkItemId(project.getId());
        wikiImages.copyTo(newPath);
        wikiImages.insert(db);
        wikiImages = null;*/
      }

      // Ticket Configuration
      if (bean.getCloneTicketConfig() &&
          hasPermission(db, project, user, member, "project-tickets-view", roleList)) {
        // Ticket Categories
        HashMap categoryMap = new HashMap();
        TicketCategoryList categoryList = new TicketCategoryList();
        categoryList.setProjectId(oldProjectId);
        categoryList.buildList(db);
        categoryList.setProjectId(project.getId());
        categoryList.insert(db, categoryMap);
        // Ticket Defect Lookup
        ProjectItemList defectList = new ProjectItemList();
        defectList.setProjectId(oldProjectId);
        defectList.setEnabled(Constants.TRUE);
        defectList.buildList(db, ProjectItemList.TICKET_DEFECT);
        defectList.setProjectId(project.getId());
        defectList.insert(db, null, ProjectItemList.TICKET_DEFECT);
        // Ticket States Lookup
        ProjectItemList stateList = new ProjectItemList();
        stateList.setProjectId(oldProjectId);
        stateList.setEnabled(Constants.TRUE);
        stateList.buildList(db, ProjectItemList.TICKET_STATE);
        stateList.setProjectId(project.getId());
        stateList.insert(db, null, ProjectItemList.TICKET_STATE);
        // Ticket Causes Lookup
        ProjectItemList causeList = new ProjectItemList();
        causeList.setProjectId(oldProjectId);
        causeList.setEnabled(Constants.TRUE);
        causeList.buildList(db, ProjectItemList.TICKET_CAUSE);
        causeList.setProjectId(project.getId());
        causeList.insert(db, null, ProjectItemList.TICKET_CAUSE);
        // Ticket Resolution Lookup
        ProjectItemList resolutionList = new ProjectItemList();
        resolutionList.setProjectId(oldProjectId);
        resolutionList.setEnabled(Constants.TRUE);
        resolutionList.buildList(db, ProjectItemList.TICKET_RESOLUTION);
        resolutionList.setProjectId(project.getId());
        resolutionList.insert(db, null, ProjectItemList.TICKET_RESOLUTION);
        // Ticket Escalation Lookup
        ProjectItemList escalationList = new ProjectItemList();
        escalationList.setProjectId(oldProjectId);
        escalationList.setEnabled(Constants.TRUE);
        escalationList.buildList(db, ProjectItemList.TICKET_ESCALATION);
        escalationList.setProjectId(project.getId());
        escalationList.insert(db, null, ProjectItemList.TICKET_ESCALATION);
      }

      // Outlines, Activities, Activity Folders (no notes yet)
      if (bean.getCloneActivities() &&
          hasPermission(db, project, user, member, "project-plan-view", roleList)) {
        RequirementList outlines = new RequirementList();
        outlines.setProjectId(oldProjectId);
        outlines.buildList(db);
        outlines.setProjectId(project.getId());
        outlines.setEnteredBy(userId);
        outlines.setModifiedBy(userId);
        outlines.setOffset(offset);
        if (bean.getResetActivityStatus()) {
          outlines.setResetStatus(true);
        }
        outlines.insert(db, oldProjectId);
      }
      db.commit();
    } catch (Exception e) {
       LOG.error("clone", e);
      db.rollback();
    } finally {
      db.setAutoCommit(true);
    }
    return project;
  }

  private static boolean hasPermission(Connection db, Project thisProject, User thisUser, TeamMember thisMember, String permission, LookupList roleList) throws SQLException {
    if (thisUser.getAccessAdmin()) {
      return true;
    }
    if (thisMember.getRoleId() == TeamMember.PROJECT_ADMIN) {
      return true;
    }
    int code = thisProject.getAccessUserLevel(permission);
    int roleId = roleList.getLevelFromId(code);
    if (code == -1 || roleId == -1) {
      return false;
    }
    return (thisMember.getRoleId() <= roleId);
  }

  public static Requirement cloneRequirement(CloneBean bean, Connection db, int groupId, int userId, int requirementId) throws SQLException {
    Requirement requirement = null;
    try {
      db.setAutoCommit(false);

      // Load permissions and resources for this member
      LOG.debug("ProjectCopier-> RequirementId: " + requirementId);

      Project project = new Project(db, bean.getProjectId());
      TeamMember member = new TeamMember(db, project.getId(), userId);
      User user = UserUtils.loadUser(userId);

      LookupList roleList = new LookupList(db, "lookup_project_role");

      // Load old requirement, change some values, save as new requirement
      requirement = new Requirement(db, requirementId);

      // Outlines, Activities, Activity Folders (no notes yet)
      if (hasPermission(db, project, user, member, "project-plan-view", roleList)) {
        requirement.setEnteredBy(userId);
        requirement.setModifiedBy(userId);
        requirement.setEntered((Timestamp) null);
        requirement.setModified((Timestamp) null);
        requirement.setShortDescription(requirement.getShortDescription() + " (copy)");
        boolean resetStatus = false;
        long offset = 0;
        if (bean.getResetActivityStatus()) {
          resetStatus = true;
        }
        requirement.clone(db, project.getId(), offset, resetStatus);
      }
      db.commit();
    } catch (Exception e) {
      LOG.error("ProjectCopier-> cloneRequirement", e);
      db.rollback();
    } finally {
      db.setAutoCommit(true);
    }
    return requirement;
  }
}
