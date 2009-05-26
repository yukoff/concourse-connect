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

package com.concursive.connect.web.modules.setup.utils;

import bsh.Interpreter;
import com.concursive.commons.api.APIConnection;
import com.concursive.commons.api.APIRestore;
import com.concursive.commons.codec.PasswordHash;
import com.concursive.commons.db.ConnectionElement;
import com.concursive.commons.db.ConnectionPool;
import com.concursive.commons.db.DatabaseUtils;
import com.concursive.commons.text.StringUtils;
import com.concursive.commons.xml.XMLUtils;
import com.concursive.connect.Constants;
import com.concursive.connect.cache.CacheContext;
import com.concursive.connect.cache.Caches;
import com.concursive.connect.cache.utils.CacheUtils;
import com.concursive.connect.config.ApplicationPrefs;
import com.concursive.connect.web.modules.api.beans.PacketContext;
import com.concursive.connect.web.modules.api.dao.SyncTableList;
import com.concursive.connect.web.modules.api.utils.TransactionUtils;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.login.dao.UserList;
import com.concursive.connect.web.modules.members.dao.TeamMember;
import com.concursive.connect.web.modules.profile.dao.*;
import com.concursive.connect.web.modules.upgrade.utils.UpgradeUtils;
import com.concursive.connect.web.utils.LookupList;
import net.sf.ehcache.CacheManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletContext;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Utilities for setting up and validating a ConcourseConnect installation
 *
 * @author matt rajkowski
 * @version $Id$
 * @created Nov 29, 2005
 */
public class SetupUtils {

  private static Log LOG = LogFactory.getLog(SetupUtils.class);

  public final static String fs = System.getProperty("file.separator");

  /**
   * This method is used by the ant task to install any default data
   *
   * @param args database connection settings: driver, url, user, password
   */
  public static void main(String[] args) {
    // Connection values
    String driver = args[0];
    String url = args[1];
    String user = args[2];
    String password = args[3];
    String fileLibraryPath = args[4];
    String title = args[5];
    String description = args[6];
    String keywords = args[7];
    String purpose = args[8];
    String userfirst = args[9];
    String userlast = args[10];
    String useremail = args[11];
    String userpass = args[12];
    // Insert default data
    ConnectionPool cp = new ConnectionPool();
    Connection db = null;
    try {
      ConnectionElement ce = new ConnectionElement(url, user, password);
      ce.setDriver(driver);
      db = cp.getConnection(ce, true);
      // Instantiate the caches
      CacheManager.create();
      CacheContext cacheContext = new CacheContext();
      cacheContext.setUpgradeConnection(db);
      Caches.addCaches(cacheContext);
      // Load the object map and services
      int SYSTEM_ID = 1;
      SyncTableList syncTableList = new SyncTableList();
      syncTableList.setSystemId(SYSTEM_ID);
      // Load the core object map
      syncTableList.loadObjectMap(SyncTableList.class.getResourceAsStream("/object_map.xml"));
      // Load plug-in mappings in the services path
      File[] serviceFiles = new File("src/main/webapp/WEB-INF/services").listFiles();
      if (serviceFiles != null && serviceFiles.length > 0) {
        for (File thisFile : serviceFiles) {
          if (thisFile.getAbsolutePath().endsWith(".xml")) {
            try {
              LOG.info("Adding services from... " + thisFile.getAbsolutePath());
              syncTableList.loadObjectMap(new FileInputStream(thisFile));
            } catch (Exception e) {
              LOG.error("getObjectMap exception", e);
            }
          }
        }
      }
      // The default category information
      insertDefaultCategories(db, syncTableList, fileLibraryPath);
      // Insert admin user
      if (useremail != null && userpass != null) {
        User adminUser = new User();
        adminUser.setFirstName(userfirst);
        adminUser.setLastName(userlast);
        adminUser.setUsername(useremail);
        adminUser.setEmail(useremail);
        adminUser.setPassword1(userpass);
        adminUser.setPassword2(userpass);
        insertDefaultAdmin(db, adminUser, null);
      }
      // The default system profile
      Project project = new Project();
      project.setTitle(title);
      project.setShortDescription(description);
      project.setKeywords(keywords);
      insertDefaultSiteConfig(db, fileLibraryPath, project, purpose);
      // The default profile content
      insertDefaultContent(db, syncTableList, fileLibraryPath, project);
    } catch (Exception e) {
      e.printStackTrace(System.out);
      LOG.error(e);
      System.exit(2);
    } finally {
      LOG.info("Cleaning up...");
      CacheManager.getInstance().shutdown();
      cp.free(db);
    }
    System.exit(0);
  }

  /**
   * Determines if the application is fully configured
   *
   * @param prefs
   * @return
   */
  public static boolean isConfigured(ApplicationPrefs prefs) {
    return prefs.isConfigured();
  }

  /**
   * Determines if the services component of installation is fully configured
   *
   * @param prefs
   * @return
   */
  public static boolean isServicesConfigured(ApplicationPrefs prefs) {
    return prefs.has(ApplicationPrefs.CONCURSIVE_SERVICES_ID);
  }

  /**
   * Determines if the database schema has been created
   *
   * @param db
   * @return
   */
  public static boolean isDatabaseInstalled(Connection db) {
    int count = -1;
    try {
      Statement st = db.createStatement();
      ResultSet rs = st.executeQuery(
          "SELECT count(*) AS recordcount " +
              "FROM database_version ");
      rs.next();
      count = rs.getInt("recordcount");
      rs.close();
      st.close();
    } catch (Exception e) {
    }
    return count > 0;
  }

  /**
   * Determines if there is an administrative user configured in the database
   *
   * @param db
   * @return
   */
  public static boolean isAdminInstalled(Connection db) {
    int count = -1;
    try {
      PreparedStatement pst = db.prepareStatement(
          "SELECT count(*) AS recordcount " +
              "FROM users " +
              "WHERE access_admin = ? ");
      pst.setBoolean(1, true);
      ResultSet rs = pst.executeQuery();
      rs.next();
      count = rs.getInt("recordcount");
      rs.close();
      pst.close();
    } catch (Exception e) {
    }
    return count > 0;
  }

  /**
   * Determines if a default project has been installed
   *
   * @param db
   * @return
   */
  public static boolean isDefaultProjectInstalled(Connection db) {
    int count = -1;
    try {
      PreparedStatement pst = db.prepareStatement(
          "SELECT count(*) AS recordcount " +
              "FROM projects " +
              "WHERE system_default = ? ");
      pst.setBoolean(1, true);
      ResultSet rs = pst.executeQuery();
      rs.next();
      count = rs.getInt("recordcount");
      rs.close();
      pst.close();
    } catch (Exception e) {
    }
    return count > 0;
  }

  /**
   * Inserts the default database data
   *
   * @param db
   * @param context
   * @param setupPath
   * @throws Exception
   */
  public static void insertDefaultData(Connection db, ServletContext context, String setupPath) throws Exception {
    // Prepare the BSH interpreter
    Interpreter script = new Interpreter();
    script.set("db", db);

    // Default database inserts
    InputStream source = context.getResourceAsStream(setupPath + "common" + fs + "install.bsh");
    BufferedReader in = new BufferedReader(new InputStreamReader(source));
    script.eval(in);
    in.close();

    // Module Defaults
    String initPath = setupPath + "init" + fs;
    DatabaseUtils.executeSQL(db, context.getResourceAsStream(initPath + "project.sql"), true);
    DatabaseUtils.executeSQL(db, context.getResourceAsStream(initPath + "task.sql"), true);
    DatabaseUtils.executeSQL(db, context.getResourceAsStream(initPath + "ticket.sql"), true);

    // Bring the version database up-to-date with the upgrade utility
    ArrayList<String> versionList = UpgradeUtils.retrieveDatabaseVersions(context.getResourceAsStream(setupPath + "database_versions.txt"));
    for (String version : versionList) {
      if (version.length() == 10) {
        UpgradeUtils.addVersion(db, version);
      }
    }
  }

  public static void insertDefaultAdmin(Connection db, User thisUser, String ip) throws Exception {
    if (thisUser.getUsername() == null || thisUser.getPassword1() == null) {
      throw new Exception("Missing required fields");
    }
    // Insert the user
    thisUser.setPassword(PasswordHash.encrypt(thisUser.getPassword1()));
    thisUser.setGroupId(1);
    thisUser.setDepartmentId(1);
    thisUser.setAccessAdmin(true);
    thisUser.setAccessInvite(true);
    thisUser.setAccessUserSettings(true);
    thisUser.setAccessGuestProjects(true);
    thisUser.setAccessAddProjects(true);
    thisUser.setEnteredBy(0);
    thisUser.setModifiedBy(0);
    thisUser.setStartPage(1);
    thisUser.setEnabled(true);
    thisUser.setAccountSize("-1");
    thisUser.insert(db, ip, null);
  }

  /**
   * Inserts additional default data
   *
   * @param db
   * @param syncTableList   the mapping of objects and services
   * @param fileLibraryPath
   * @throws Exception
   */
  public static void insertDefaultCategories(Connection db, SyncTableList syncTableList, String fileLibraryPath) throws Exception {
    int SYSTEM_ID = 1;

    // From the Service action method
    PacketContext packetContext = new PacketContext();
    packetContext.setObjectMap(syncTableList.getObjectMapping(SYSTEM_ID));
    packetContext.setBaseFilePath(fileLibraryPath);

    // Retrieve and stream the records to restore
    LOG.debug("Loading the configuration xml...");
    InputStream inputStream = SetupUtils.class.getResourceAsStream("/configuration_en_US.xml");

    // Restore the state of the objects as-supplied, start by deleting the record and dependents
    ArrayList<String> meta = new ArrayList<String>();
    meta.add("mode=copy");

    ArrayList exclude = new ArrayList();

    HashMap replace = new HashMap();
    replace.put("enteredBy", "1");
    replace.put("modifiedBy", "1");
    replace.put("thumbnailFilename", "null");
    replace.put("logoId", "-1");

    HashMap options = new HashMap();
    options.put("exclude", exclude);
    options.put("replace", replace);

    // Use the APIConnection to do the shared work
    LOG.debug("Parsing the records...");
    APIConnection api = new APIConnection();
    api.setAutoCommit(false);
    api.setTransactionMeta(meta);
    APIRestore.restore(api, inputStream, null, -1, options);

    // Construct the xml
    LOG.debug("Generating XML...");
    XMLUtils xml = new XMLUtils(api.generateXMLPacket());

    // Restore this item
    TransactionUtils.processTransactions(db, xml, packetContext);

    LOG.debug("Finished.");
  }

  public static void insertDefaultSiteConfig(Connection db, String fileLibraryPath, Project project, String purpose) throws Exception {
    LOG.debug("insertDefaultSiteConfig");
    // Validate the pre-reqs
    if (!StringUtils.hasText(project.getTitle()) ||
        !StringUtils.hasText(project.getShortDescription()) ||
        !StringUtils.hasText(project.getKeywords())) {
      throw new Exception("Title, description, keywords are required");
    }

    // Load the categories
    ProjectCategoryList categoryList = new ProjectCategoryList();
    categoryList.setTopLevelOnly(true);
    categoryList.buildList(db);

    // Determine the default's category
    int categoryId = categoryList.getIdFromValue("Groups");

    // Determine the tabs
    ProjectCategoryList validCategoryList = new ProjectCategoryList();
    if ("social".equals(purpose)) {
      validCategoryList.add(categoryList.getFromValue("Groups"));
      validCategoryList.add(categoryList.getFromValue("People"));
      validCategoryList.add(categoryList.getFromValue("Events"));
      validCategoryList.add(categoryList.getFromValue("Ideas"));
      validCategoryList.add(categoryList.getFromValue("Sponsors"));
    } else if ("directory".equals(purpose)) {
      validCategoryList.add(categoryList.getFromValue("Businesses"));
      validCategoryList.add(categoryList.getFromValue("Organizations"));
      validCategoryList.add(categoryList.getFromValue("Groups"));
      validCategoryList.add(categoryList.getFromValue("People"));
      validCategoryList.add(categoryList.getFromValue("Places"));
      validCategoryList.add(categoryList.getFromValue("Events"));
      validCategoryList.add(categoryList.getFromValue("Ideas"));
    } else if ("community".equals(purpose)) {
      validCategoryList.add(categoryList.getFromValue("Groups"));
      validCategoryList.add(categoryList.getFromValue("People"));
      validCategoryList.add(categoryList.getFromValue("Ideas"));
    } else if ("intranet".equals(purpose)) {
      validCategoryList.add(categoryList.getFromValue("Groups"));
      validCategoryList.add(categoryList.getFromValue("People"));
      validCategoryList.add(categoryList.getFromValue("Events"));
      validCategoryList.add(categoryList.getFromValue("Ideas"));
      validCategoryList.add(categoryList.getFromValue("Projects"));
    } else if ("projects".equals(purpose)) {
      validCategoryList.add(categoryList.getFromValue("Groups"));
      validCategoryList.add(categoryList.getFromValue("People"));
      validCategoryList.add(categoryList.getFromValue("Projects"));
    } else if ("web".equals(purpose)) {
      validCategoryList.add(categoryList.getFromValue("Products"));
      validCategoryList.add(categoryList.getFromValue("Services"));
      validCategoryList.add(categoryList.getFromValue("Partners"));
      validCategoryList.add(categoryList.getFromValue("Groups"));
      validCategoryList.add(categoryList.getFromValue("People"));
      validCategoryList.add(categoryList.getFromValue("Events"));
      validCategoryList.add(categoryList.getFromValue("Ideas"));
      validCategoryList.add(categoryList.getFromValue("Projects"));
    }
    // Update the related tabs, or leave all enabled
    if (validCategoryList.size() > 0) {
      for (ProjectCategory category : categoryList) {
        category.setEnabled(validCategoryList.get(category) != null);
        category.update(db);
      }
    }

    // Determine the profile visibility
    project.setUpdateAllowGuests(true);
    project.setAllowGuests(true);
    project.setUpdateAllowParticipants(true);
    project.setAllowParticipants(true);
    project.setUpdateMembershipRequired(true);
    project.setMembershipRequired(false);
    project.setApproved(true);

    // Determine the tabs
    project.setShowProfile(true);
    project.getFeatures().setLabelProfile("Overview");
    project.setShowNews(true);
    project.getFeatures().setLabelNews("Blog");
    project.setShowReviews(true);
    project.setShowWiki(true);
    project.setShowCalendar(true);
    project.setShowDiscussion(true);
    project.setShowDocuments(true);
    project.setShowClassifieds(true);
    project.setShowBadges(true);
    project.setShowLists(true);
    project.setShowIssues(true);
    project.setShowTeam(true);
    project.getFeatures().setLabelTickets("Issues");
    project.getFeatures().setLabelTeam("Participants");
    project.setShowMessages(true);

    // Determine the record details
    project.setUniqueId("main-profile");
    project.setGroupId(1);
    project.setCategoryId(categoryId);
    project.setOwner(1);
    project.setEnteredBy(1);
    project.setModifiedBy(1);
    project.insert(db);
    project.updateFeatures(db);
    project.setSystemDefault(true);
    project.updateSystemDefault(db);

    // Build a list of roles
    LookupList roleList = CacheUtils.getLookupList("lookup_project_role");

    // Build a list of the default permissions
    PermissionList permissionList = new PermissionList();
    permissionList.setProjectId(project.getId());
    permissionList.buildList(db);

    // Modify the permissions for the default profile
    LinkedHashMap<String, Integer> permissionMap = new LinkedHashMap<String, Integer>();
    permissionMap.put("project-tickets-view", TeamMember.MEMBER);
    permissionMap.put("project-tickets-other", TeamMember.CHAMPION);
    permissionMap.put("project-tickets-add", TeamMember.MEMBER);
    permissionMap.put("project-tickets-edit", TeamMember.CHAMPION);
    permissionMap.put("project-tickets-assign", TeamMember.CHAMPION);
    permissionMap.put("project-tickets-close", TeamMember.CHAMPION);
    permissionMap.put("project-tickets-delete", TeamMember.MANAGER);

    for (String name : permissionMap.keySet()) {
      Permission permission = permissionList.get(name);
      permission.setUserLevel(roleList.getIdFromLevel(permissionMap.get(name)));
      LOG.debug("Updating a permission: " + name);
      permission.update(db);
    }

    // Get the admin role
    int adminRowLevel = roleList.getIdFromValue("Manager");

    // Add the admins as a member
    UserList userList = new UserList();
    userList.setAdmin(Constants.TRUE);
    userList.buildList(db);
    for (User thisUser : userList) {
      TeamMember thisMember = new TeamMember();
      thisMember.setProjectId(project.getId());
      thisMember.setUserId(thisUser.getId());
      thisMember.setUserLevel(adminRowLevel);
      thisMember.setEnteredBy(1);
      thisMember.setModifiedBy(1);
      thisMember.insert(db);
    }
  }

  public static void insertDefaultContent(Connection db, SyncTableList syncTableList, String fileLibraryPath, Project project) throws Exception {
    LOG.debug("insertDefaultContent");
    int SYSTEM_ID = 1;

    // From the Service action method
    PacketContext packetContext = new PacketContext();
    packetContext.setObjectMap(syncTableList.getObjectMapping(SYSTEM_ID));
    packetContext.setBaseFilePath(fileLibraryPath);

    // Retrieve and stream the records to restore
    LOG.debug("Loading the configuration xml...");
    InputStream inputStream = SetupUtils.class.getResourceAsStream("/content_en_US.xml");

    // Restore the state of the objects as-supplied, start by deleting the record and dependents
    ArrayList<String> meta = new ArrayList<String>();
    meta.add("mode=copy");

    ArrayList exclude = new ArrayList();

    HashMap<String, String> replace = new HashMap<String, String>();
    replace.put("enteredBy", String.valueOf(project.getEnteredBy()));
    replace.put("modifiedBy", String.valueOf(project.getEnteredBy()));
    replace.put("projectId", String.valueOf(project.getId()));
    replace.put("linkItemId", String.valueOf(project.getId()));
    replace.put("thumbnailFilename", "null");
    replace.put("logoId", "-1");

    HashMap options = new HashMap();
    options.put("exclude", exclude);
    options.put("replace", replace);

    // Use the APIConnection to do the shared work
    LOG.debug("Parsing the records...");
    APIConnection api = new APIConnection();
    api.setAutoCommit(false);
    api.setTransactionMeta(meta);
    APIRestore.restore(api, inputStream, null, -1, options);

    // Construct the xml
    LOG.debug("Generating XML...");
    XMLUtils xml = new XMLUtils(api.generateXMLPacket());

    // Restore this item
    TransactionUtils.processTransactions(db, xml, packetContext);

    LOG.debug("Finished.");
  }
}
