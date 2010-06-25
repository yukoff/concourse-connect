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

package com.concursive.connect.web.modules.login.utils;

import com.concursive.commons.db.DatabaseUtils;
import com.concursive.commons.objects.ObjectUtils;
import com.concursive.commons.text.StringUtils;
import com.concursive.commons.workflow.ObjectHookAction;
import com.concursive.commons.workflow.ObjectHookManager;
import com.concursive.connect.Constants;
import com.concursive.connect.cache.utils.CacheUtils;
import com.concursive.connect.config.ApplicationPrefs;
import com.concursive.connect.indexer.IndexEvent;
import com.concursive.connect.scheduler.ScheduledJobs;
import com.concursive.connect.web.modules.common.social.tagging.dao.TagList;
import com.concursive.connect.web.modules.discussion.dao.DiscussionForumTemplate;
import com.concursive.connect.web.modules.discussion.dao.DiscussionForumTemplateList;
import com.concursive.connect.web.modules.discussion.dao.Forum;
import com.concursive.connect.web.modules.discussion.dao.ForumList;
import com.concursive.connect.web.modules.documents.dao.*;
import com.concursive.connect.web.modules.lists.dao.ListsTemplate;
import com.concursive.connect.web.modules.lists.dao.ListsTemplateList;
import com.concursive.connect.web.modules.lists.dao.TaskCategory;
import com.concursive.connect.web.modules.lists.dao.TaskCategoryList;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.login.dao.UserList;
import com.concursive.connect.web.modules.members.dao.TeamMember;
import com.concursive.connect.web.modules.profile.dao.*;
import com.concursive.connect.web.modules.wiki.dao.Wiki;
import com.concursive.connect.web.modules.wiki.dao.WikiList;
import com.concursive.connect.web.modules.wiki.dao.WikiTemplate;
import com.concursive.connect.web.modules.wiki.dao.WikiTemplateList;
import com.concursive.connect.web.utils.LookupList;
import com.concursive.connect.web.utils.PagedListInfo;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Scheduler;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.util.*;

/**
 * Utilities for working with users of the system
 *
 * @author matt rajkowski
 * @version $Id$
 * @created July 8, 2004
 */
public class UserUtils {

  private static Log LOG = LogFactory.getLog(UserUtils.class);

  public static String getUserName(int userId) {
    return StringUtils.toHtml(((User) CacheUtils.getObjectValue(Constants.SYSTEM_USER_CACHE, userId)).getNameFirstLastInitial());
  }

  public static int getUserId(HttpServletRequest request) {
    return ((User) request.getSession().getAttribute(Constants.SESSION_USER)).getId();
  }

  public static User loadUser(int userId) {
    if (userId > -1) {
      Ehcache cache = CacheUtils.getCache(Constants.SYSTEM_USER_CACHE);
      Element element = cache.get(userId);
      return (User) element.getObjectValue();
    } else {
      return null;
    }
  }

  /**
   * Gets the userRangeId attribute of the UserUtils class
   *
   * @param request Description of the Parameter
   * @return The userRangeId value
   */
  public static String getUserIdRange(HttpServletRequest request) {
    return ((User) request.getSession().getAttribute(Constants.SESSION_USER)).getIdRange();
  }

  /**
   * Gets the userTimeZone attribute of the UserUtils class
   *
   * @param request Description of the Parameter
   * @return The userTimeZone value
   */
  public static String getUserTimeZone(HttpServletRequest request) {
    return ((User) request.getSession().getAttribute(Constants.SESSION_USER)).getTimeZone();
  }

  public static TimeZone getUserTimeZone(User user) {
    TimeZone timeZone = Calendar.getInstance().getTimeZone();
    String tZone = user.getTimeZone();
    if (tZone != null && !"".equals(tZone)) {
      timeZone = TimeZone.getTimeZone(tZone);
    }
    return timeZone;
  }


  /**
   * Gets the userLocale attribute of the UserUtils class
   *
   * @param request Description of the Parameter
   * @return The userLocale value
   */
  public static Locale getUserLocale(HttpServletRequest request) {
    return ((User) request.getSession().getAttribute(Constants.SESSION_USER)).getLocale();
  }


  /**
   * Gets the userCurrency attribute of the UserUtils class
   *
   * @param request Description of the Parameter
   * @return The userCurrency value
   */
  public static String getUserCurrency(HttpServletRequest request) {
    return ((User) request.getSession().getAttribute(Constants.SESSION_USER)).getCurrency();
  }

  public static boolean isUserDisabled(User thisUser) {
    return (!thisUser.getEnabled() ||
        (thisUser.getExpiration() != null && thisUser.getExpiration().getTime() <= System.currentTimeMillis()));
  }

  public static User createGuestUser() {
    User user = new User();
    user.setId(-2);
    user.setIdRange("-2");
    user.setGroupId(1);
    user.setFirstName("Guest");
    user.setEnabled(true);
    // @todo Use applicationPrefs for the correct defaults
    // Set a default time zone for user
    if (user.getTimeZone() == null) {
      user.setTimeZone(TimeZone.getDefault().getID());
    }
    // Set a default currency for user
    if (user.getCurrency() == null) {
      user.setCurrency(NumberFormat.getCurrencyInstance().getCurrency().getCurrencyCode());
    }
    // Set default locale: language, country
    if (user.getLanguage() == null) {
      user.setLanguage("en_US");
    }
    return user;
  }

  public static void createLoggedInUser(User thisUser, Connection db, ApplicationPrefs prefs, ServletContext context) throws SQLException {
    // Apply limitations
    thisUser.setCurrentAccountSize(FileItemVersionList.queryOwnerSize(db, thisUser.getId()));
    // Use standard template engine
    thisUser.setTemplate("template0");
    // Set a default time zone for user
    if (thisUser.getTimeZone() == null) {
      thisUser.setTimeZone(prefs.get(ApplicationPrefs.TIMEZONE));
    }
    // Set a default currency
    if (thisUser.getCurrency() == null) {
      thisUser.setCurrency(prefs.get(ApplicationPrefs.CURRENCY));
    }
    // Set a default locale
    if (thisUser.getLanguage() == null) {
      thisUser.setLanguage(prefs.get(ApplicationPrefs.LANGUAGE));
    }
    // Show recently accessed projects
    thisUser.queryRecentlyAccessedProjects(db);
    // Make sure the user has a profile
    if (thisUser.getProfileProjectId() == -1) {
      // @todo find out how the user profile is not already generated!
      LOG.warn("Adding a user profile (should have already existed)");
      UserUtils.addUserProfile(db, thisUser, prefs);

      // processInsertHook
      boolean sslEnabled = "true".equals(prefs.get("SSL"));
      String url = ("http" + (sslEnabled ? "s" : "") + "://" +
          prefs.get(ApplicationPrefs.WEB_DOMAIN_NAME) +
          (!"80".equals(prefs.get(ApplicationPrefs.WEB_PORT)) && !"443".equals(prefs.get(ApplicationPrefs.WEB_PORT)) ? ":" + prefs.get(ApplicationPrefs.WEB_PORT) : "") +
          prefs.get(ApplicationPrefs.WEB_CONTEXT));
      ObjectHookManager hookManager = (ObjectHookManager) context.getAttribute(Constants.OBJECT_HOOK_MANAGER);
      hookManager.process(ObjectHookAction.INSERT, null, thisUser.getProfileProject(), thisUser.getId(), url, url, null);

      // indexer
      Scheduler scheduler = (Scheduler) context.getAttribute(Constants.SCHEDULER);
      try {
        IndexEvent indexEvent = new IndexEvent(thisUser.getProfileProject(), IndexEvent.ADD);
        ((Vector) scheduler.getContext().get("IndexArray")).add(indexEvent);
        scheduler.triggerJob("indexer", (String) scheduler.getContext().get(ScheduledJobs.CONTEXT_SCHEDULER_GROUP));
      } catch (Exception e) {
        LOG.error("createUser-scheduler-index-profile", e);
      }
    }
  }

  public static String generateGuid(User thisUser) {
    return thisUser.getPassword().substring(2, 15) + thisUser.getEntered().getTime() + "-" + thisUser.getEntered().getNanos() + "-" + thisUser.getId();
  }

  public static String getPasswordSubStringFromGuid(String guid) {
    String passwordValue = guid.substring(0, 13);
    LOG.trace("password value: " + passwordValue);
    return passwordValue;
  }

  public static Timestamp getEnteredTimestampFromGuid(String guid) {
    long longValue = Long.parseLong(guid.substring(13, guid.indexOf("-")));
    int nanos = Integer.parseInt(guid.substring(guid.indexOf("-") + 1, guid.lastIndexOf("-")));
    LOG.trace("entered value: " + longValue + "." + nanos);
    Timestamp timestamp = new Timestamp(longValue);
    timestamp.setNanos(nanos);
    return timestamp;
  }

  public static int getUserIdFromGuid(String guid) {
    String userIdValue = guid.substring(guid.lastIndexOf("-") + 1);
    LOG.trace("userId value: " + userIdValue);
    return Integer.parseInt(userIdValue);
  }

  public static User loadUserFromGuid(Connection db, String guid) throws SQLException {
    UserList userList = new UserList();
    userList.setGuid(guid);
    userList.buildList(db);
    LOG.trace("guid users found: " + userList.size());
    if (userList.size() == 1) {
      return userList.get(0);
    }
    return null;
  }

  /**
   * Creates a user's profile and sets the id on the user object
   *
   * @param db
   * @param user
   * @param prefs
   * @throws SQLException
   */
  public static void addUserProfile(Connection db, User user, ApplicationPrefs prefs) throws SQLException {
    boolean autoCommit = db.getAutoCommit();
    try {
      if (autoCommit) {
        db.setAutoCommit(false);
      }
      // Determine the project features
      ProjectFeatures features = new ProjectFeatures();
      ArrayList<String> modules = new ArrayList<String>();
      modules.add("Profile");
      String enabledModules = prefs.get(ApplicationPrefs.DEFAULT_USER_PROFILE_TABS);
//      if (enabledModules == null || enabledModules.contains("Reviews")) {
//       modules.add("Reviews");
//      }
      if (enabledModules == null || enabledModules.contains("Blog")) {
        modules.add("News=My Blog");
      }
      if (enabledModules == null || enabledModules.contains("Wiki")) {
        modules.add("Wiki=About Me");
      }
      if (enabledModules == null || enabledModules.contains("Classifieds")) {
        modules.add("My Classifieds");
      }
      if (enabledModules == null || enabledModules.contains("Documents")) {
        modules.add("Documents=My Documents");
      }
      if (enabledModules == null || enabledModules.contains("Lists")) {
        modules.add("My Lists");
      }
      if (enabledModules == null || enabledModules.contains("Badges")) {
        modules.add("My Badges");
      }
      if (enabledModules == null || enabledModules.contains("Friends")) {
        modules.add("Team=Friends");
      }
      if (enabledModules == null || enabledModules.contains("Messages")) {
        modules.add("Messages");
      }
      int count = 0;
      for (String modulePreference : modules) {
        String moduleName = null;
        String moduleLabel = null;
        if (modulePreference.indexOf("=") != -1) {
          moduleName = modulePreference.split("=")[0];
          moduleLabel = modulePreference.split("=")[1];
        } else {
          moduleName = modulePreference;
          moduleLabel = modulePreference;
        }
        ObjectUtils.setParam(features, "order" + moduleName, count + 1);
        ObjectUtils.setParam(features, "label" + moduleName, moduleLabel);
        ObjectUtils.setParam(features, "show" + moduleName, true);
      }
      // Determine the category id
      ProjectCategoryList projectCategoryList = new ProjectCategoryList();
      projectCategoryList.setCategoryDescription("People");
      projectCategoryList.buildList(db);
      ProjectCategory people = projectCategoryList.getFromValue("People");
      // Create a user project profile
      Project project = new Project();
      project.setInstanceId(user.getInstanceId());
      project.setGroupId(1);
      project.setApproved(user.getEnabled());
      project.setProfile(true);
      project.setCategoryId(people.getId());
      project.setOwner(user.getId());
      project.setEnteredBy(user.getId());
      project.setModifiedBy(user.getId());
      // Determine how to record the user's name in the profile
      if ("true".equals(prefs.get(ApplicationPrefs.USERS_ARE_ANONYMOUS))) {
        project.setTitle(user.getNameFirstLastInitial());
      } else {
        project.setTitle(user.getNameFirstLast());
      }
      project.setKeywords(user.getNameFirstLast());
      project.setShortDescription("My Profile");
      project.setCity(user.getCity());
      project.setState(user.getState());
      project.setCountry(user.getCountry());
      project.setPostalCode(user.getPostalCode());
      project.setFeatures(features);
      // Access rules will allow this profile to be searched and seen
      project.getFeatures().setUpdateAllowGuests(true);
      project.getFeatures().setAllowGuests(true);
      if ("true".equals(prefs.get(ApplicationPrefs.INFORMATION_IS_SENSITIVE))) {
        project.getFeatures().setAllowGuests(false);
      }
      // A join request can be made which requires approval by the profile owner
      project.getFeatures().setUpdateAllowParticipants(true);
      project.getFeatures().setAllowParticipants(true);
      // Anyone can see the profile page
      project.getFeatures().setUpdateMembershipRequired(true);
      project.getFeatures().setMembershipRequired(true);
      project.insert(db);
      project.getFeatures().setId(project.getId());
      project.getFeatures().update(db);
      updateProfileProjectId(db, user, project);
      // Determine which role level the user is for their own profile
      LookupList roleList = CacheUtils.getLookupList("lookup_project_role");
      int defaultUserLevel = roleList.getIdFromLevel(TeamMember.MANAGER);
      if (!user.getAccessAdmin() && prefs.has(ApplicationPrefs.DEFAULT_USER_PROFILE_ROLE)) {
        int userLevelPreference = roleList.getIdFromValue(prefs.get(ApplicationPrefs.DEFAULT_USER_PROFILE_ROLE));
        if (userLevelPreference > -1) {
          defaultUserLevel = userLevelPreference;
        }
      }
      // Add the user as a member of the profile
      TeamMember member = new TeamMember();
      member.setUserId(user.getId());
      member.setProjectId(project.getId());
      member.setUserLevel(defaultUserLevel);
      member.setStatus(TeamMember.STATUS_ADDED);
      member.setNotification(true);
      member.setEnteredBy(user.getId());
      member.setModifiedBy(user.getId());
      member.insert(db);
      if (autoCommit) {
        db.commit();
      }
      // Success, now that the database is committed, invalidate the cache
      CacheUtils.invalidateValue(Constants.SYSTEM_USER_CACHE, user.getId());
      CacheUtils.invalidateValue(Constants.SYSTEM_PROJECT_CACHE, project.getId());
    } catch (Exception e) {
      LOG.error("addUserProfile", e);
      if (autoCommit) {
        db.rollback();
      }
      throw new SQLException(e.getMessage());
    } finally {
      if (autoCommit) {
        db.setAutoCommit(true);
      }
    }
  }

  private static boolean updateProfileProjectId(Connection db, User user, Project project) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "UPDATE users " +
            "SET profile_project_id = ? " +
            "WHERE user_id = ? ");
    int i = 0;
    pst.setInt(++i, project.getId());
    pst.setInt(++i, user.getId());
    int count = pst.executeUpdate();
    pst.close();
    // Relate the two objects
    user.setProfileProjectId(project.getId());
    CacheUtils.invalidateValue(Constants.SYSTEM_USER_CACHE, user.getId());
    return count == 1;
  }

  public static boolean detachProfile(Connection db, int projectId) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "UPDATE users " +
            "SET profile_project_id = ? " +
            "WHERE profile_project_id = ? ");
    int i = 0;
    DatabaseUtils.setInt(pst, ++i, -1);
    pst.setInt(++i, projectId);
    int count = pst.executeUpdate();
    pst.close();
    return count == 1;
  }

  public static int getUserLevel(int roleLevel) {
    return CacheUtils.getLookupList("lookup_project_role").getIdFromLevel(roleLevel);
  }

  public static void populateUserProfiles(Connection db) throws SQLException {
    // Determine the category id
    ProjectCategoryList projectCategoryList = new ProjectCategoryList();
    projectCategoryList.setCategoryDescription("People");
    projectCategoryList.buildList(db);
    ProjectCategory people = projectCategoryList.getFromValue("People");

    // Wiki
    WikiTemplateList wikiTemplateList = new WikiTemplateList();
    wikiTemplateList.setProjectCategoryId(people.getId());
    wikiTemplateList.buildList(db);

    // Document Folders
    DocumentFolderTemplateList folderTemplateList = new DocumentFolderTemplateList();
    folderTemplateList.setProjectCategoryId(people.getId());
    folderTemplateList.buildList(db);

    // Discussion Forums
    DiscussionForumTemplateList forumTemplateList = new DiscussionForumTemplateList();
    forumTemplateList.setProjectCategoryId(people.getId());
    forumTemplateList.buildList(db);

    // Lists
    ListsTemplateList listTemplateList = new ListsTemplateList();
    listTemplateList.setProjectCategoryId(people.getId());
    listTemplateList.buildList(db);

    // Page through the updates just in case there are lots of users do 100 at a time...
    PagedListInfo pagedListInfo = new PagedListInfo();
    pagedListInfo.setItemsPerPage(100);
    pagedListInfo.setDefaultSort("project_id", null);

    // Build the records
    ProjectList projectList = new ProjectList();
    projectList.setPagedListInfo(pagedListInfo);
    projectList.setCategoryId(people.getId());
    projectList.buildList(db);

    while (projectList.size() > 0) {
      LOG.debug(" Page: " + pagedListInfo.getPage() + " of " + pagedListInfo.getNumberOfPages());
      for (Project project : projectList) {
        // Check to see if wiki needs to be added
        for (WikiTemplate template : wikiTemplateList) {
          String wikiSubject = template.getTitle();
          if ("Home".equals(wikiSubject)) {
            wikiSubject = "";
          }
          Wiki thisWiki = WikiList.queryBySubject(db, wikiSubject, project.getId());
          if (thisWiki.getId() == -1) {
            // Insert the wiki
            Wiki wiki = new Wiki();
            wiki.setProjectId(project.getId());
            wiki.setSubject(wikiSubject);
            wiki.setContent(template.getContent());
            wiki.setEnteredBy(project.getModifiedBy());
            wiki.setModifiedBy(project.getModifiedBy());
            wiki.setTemplateId(template.getId());
            wiki.insert(db);
          }
        }
        // Check to see if folders need to be added
        FileFolderList fileFolderList = new FileFolderList();
        fileFolderList.setLinkModuleId(Constants.PROJECTS_FILES);
        fileFolderList.setLinkItemId(project.getId());
        fileFolderList.buildList(db);
        if (fileFolderList.size() == 0) {
          for (DocumentFolderTemplate template : folderTemplateList) {
            ArrayList<String> folderNames = template.getFolderNamesArrayList();
            for (String folderName : folderNames) {
              FileFolder fileFolder = new FileFolder();
              fileFolder.setLinkModuleId(Constants.PROJECTS_FILES);
              fileFolder.setLinkItemId(project.getId());
              fileFolder.setSubject(folderName);
              fileFolder.setEnteredBy(project.getModifiedBy());
              fileFolder.setModifiedBy(project.getModifiedBy());
              fileFolder.insert(db);
            }
          }
        }
        // Check to see if forums need to be added
        ForumList forumList = new ForumList();
        forumList.setProjectId(project.getId());
        forumList.buildList(db);
        if (forumList.size() == 0) {
          for (DiscussionForumTemplate template : forumTemplateList) {
            ArrayList<String> folderNames = template.getForumNamesArrayList();
            for (String forumName : folderNames) {
              Forum forum = new Forum();
              forum.setAllowFileAttachments(true);
              forum.setEnabled(true);
              forum.setProjectId(project.getId());
              forum.setSubject(forumName);
              forum.setEnteredBy(project.getModifiedBy());
              forum.setModifiedBy(project.getModifiedBy());
              forum.insert(db);
            }
          }
        }
        // Check to see if lists need to be added
        TaskCategoryList lists = new TaskCategoryList();
        lists.setProjectId(project.getId());
        lists.buildList(db);
        if (lists.size() == 0) {
          int level = 0;
          for (ListsTemplate template : listTemplateList) {
            ArrayList<String> listNames = template.getListNamesArrayList();
            for (String listName : listNames) {
              TaskCategory newCategory = new TaskCategory();
              newCategory.setLinkModuleId(Constants.TASK_CATEGORY_PROJECTS);
              newCategory.setLinkItemId(project.getId());
              newCategory.setDescription(listName);
              level = level + 10;
              newCategory.setLevel(level);
              newCategory.insert(db);
            }
          }
        }
      }
      // Always reset the list or else the records are included on buildList again
      projectList.clear();
      if (pagedListInfo.getPage() < pagedListInfo.getNumberOfPages()) {
        pagedListInfo.setCurrentPage(pagedListInfo.getPage() + 1);
        projectList.buildList(db);
      }
    }
  }

  /**
   * Get recently used tags by the user
   *
   * @param db
   * @param userId
   * @return
   * @throws SQLException
   */
  public static TagList loadRecentlyUsedTagsByUser(Connection db, int userId) throws SQLException {
    TagList popularTagList = new TagList();
    popularTagList.setTableName("user");
    popularTagList.setUniqueField("user_id");
    popularTagList.setLinkItemId(userId);
    PagedListInfo tagListInfo = new PagedListInfo();
    tagListInfo.setColumnToSortBy("tag_date DESC, tag_count DESC, tag");
    popularTagList.setPagedListInfo(tagListInfo);
    popularTagList.buildList(db);
    return popularTagList;
  }
}
