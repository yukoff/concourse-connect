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
package com.concursive.connect.web.modules.setup.actions;

import com.concursive.commons.codec.PrivateString;
import com.concursive.commons.db.ConnectionElement;
import com.concursive.commons.db.DatabaseUtils;
import com.concursive.commons.http.RequestUtils;
import com.concursive.commons.net.HTTPUtils;
import com.concursive.commons.objects.ObjectUtils;
import com.concursive.commons.text.StringUtils;
import com.concursive.commons.web.mvc.actions.ActionContext;
import com.concursive.connect.Constants;
import com.concursive.connect.config.ApplicationPrefs;
import com.concursive.connect.config.ApplicationVersion;
import com.concursive.connect.web.controller.actions.GenericAction;
import com.concursive.connect.web.modules.api.dao.SyncTableList;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.login.dao.UserList;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import com.concursive.connect.web.modules.setup.beans.SetupDatabaseBean;
import com.concursive.connect.web.modules.setup.beans.SetupDetailsBean;
import com.concursive.connect.web.modules.setup.beans.SetupRegistrationBean;
import com.concursive.connect.web.modules.setup.beans.SetupSiteBean;
import com.concursive.connect.web.modules.setup.utils.SetupUtils;

import java.io.File;
import java.security.Key;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;
import java.util.Set;

/**
 * Actions for setting up an installation
 *
 * @author matt rajkowski
 * @version $Id$
 * @created April 23, 2004
 */
public final class Setup extends GenericAction {

  /**
   * Action for setting up a web application
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandDefault(ActionContext context) {
    if (SetupUtils.isConfigured(getApplicationPrefs(context))) {
      return "AlreadySetupOK";
    }
    return "SetupOK";
  }

  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandConfigureLibrary(ActionContext context) {
    if (SetupUtils.isConfigured(getApplicationPrefs(context))) {
      return "AlreadySetupOK";
    }
    // populate from request
    String fileLibrary = context.getRequest().getParameter("directory");
    // populate from prefs
    if (fileLibrary == null) {
      fileLibrary = getPref(context, ApplicationPrefs.FILE_LIBRARY_PATH);
    }
    // create a default depending on os
    if (fileLibrary == null || "".equals(fileLibrary.trim())) {
      String contextPath = context.getRequest().getContextPath();
      if (!StringUtils.hasText(contextPath)) {
        contextPath = "connect";
      }
      contextPath = StringUtils.replace(contextPath, "/", "");
      String os = System.getProperty("os.name");
      if (os.startsWith("Windows")) {
        // Windows
        fileLibrary = "c:\\Concursive\\" + contextPath + "\\fileLibrary";
      } else if (os.startsWith("Mac")) {
        // Mac OSX
        fileLibrary = "/Library/Application Support/Concursive/" + contextPath + "/fileLibrary";
      } else {
        // Linux, Solaris, SunOS, OS/2, HP-UX, AIX, FreeBSD, etc
        fileLibrary = "/var/lib/concursive/" + contextPath + "/fileLibrary";
      }
    }
    context.getRequest().setAttribute("directory", fileLibrary);
    return "SetupConfigureLibraryOK";
  }

  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandSaveLibrary(ActionContext context) {
    if (SetupUtils.isConfigured(getApplicationPrefs(context))) {
      return "AlreadySetupOK";
    }
    String fileLibrary = context.getRequest().getParameter("directory");
    if (fileLibrary == null || "".equals(fileLibrary.trim())) {
      context.getRequest().setAttribute("actionError", "Directory is a required field");
      return "SetupSaveLibraryERROR";
    }
    String fs = System.getProperty("file.separator");
    String confirm = context.getRequest().getParameter("confirm");
    if (!fileLibrary.endsWith(fs)) {
      fileLibrary += fs;
    }
    File path = new File(fileLibrary);
    // See if the directory exists, if not confirm
    if (!"yes".equals(confirm) && !path.exists()) {
      return "SetupSaveLibraryCONFIRM";
    }

    try {
      File propertiesFile = new File(fileLibrary + "build.properties");
      // If the directory exists, see if it is an existing file library
      // and ask user if it should be used instead
      if (path.exists() && propertiesFile.exists()) {
        if (!"yes".equals(confirm)) {
          return "SetupSaveLibraryDUPLICATE";
        } else {
          // Save the location of the fileLibrary
          String thisPath = ApplicationPrefs.getRealPath(context.getServletContext());
          if (thisPath == null) {
            Properties instanceProperties = new Properties();
            instanceProperties.load(context.getServletContext().getResourceAsStream("/WEB-INF/instance.property"));
            String potentialPath = instanceProperties.getProperty("path");
            if (potentialPath != null) {
              thisPath = potentialPath;
            }
          }
          ApplicationPrefs.saveFileLibraryLocation(thisPath, fileLibrary);
          // Load the specified prefs...
          getApplicationPrefs(context).initializePrefs(context.getServletContext());

          // ask to reload prefs
          if (SetupUtils.isConfigured(getApplicationPrefs(context))) {
            return "ReloadOK";
          } else {
            // Test to see if registration of services is enabled
            try {
              Class clientClass = Class.forName("com.concursive.connect.plugins.services.Client");
              ObjectUtils.constructObject(clientClass);
              context.getRequest().setAttribute("setupRegistration", "true");
            } catch (Exception e) {
              LOG.info("Services will be disabled");
            }
            return "SetupSaveLibraryOK";
          }
        }
      }

      // finalize the directory
      if ("yes".equals(confirm)) {
        // create the directory
        if (!path.exists()) {
          path.mkdirs();
        }
        // account for group Id not being set yet
        File templatesPath = new File(fileLibrary + "1" + fs + "email");
        if (!templatesPath.exists()) {
          templatesPath.mkdirs();
        }
        // ready for next step
        getApplicationPrefs(context).add("CONFIGURING", "true");
        getApplicationPrefs(context).add(ApplicationPrefs.FILE_LIBRARY_PATH, fileLibrary);
        // save the temporary prefs so user can return
        getApplicationPrefs(context).save(fileLibrary + "build.properties");
        // save the location
        String thisPath = ApplicationPrefs.getRealPath(context.getServletContext());
        if (thisPath == null) {
          Properties instanceProperties = new Properties();
          instanceProperties.load(context.getServletContext().getResourceAsStream("/WEB-INF/instance.property"));
          String potentialPath = instanceProperties.getProperty("path");
          if (potentialPath != null) {
            thisPath = potentialPath;
          }
        }
        ApplicationPrefs.saveFileLibraryLocation(thisPath, fileLibrary);
        // Test to see if registration of services is enabled
        try {
          Class clientClass = Class.forName("com.concursive.connect.plugins.services.Client");
          ObjectUtils.constructObject(clientClass);
          context.getRequest().setAttribute("setupRegistration", "true");
        } catch (Exception e) {
          LOG.info("Services will be disabled");
        }
        return "SetupSaveLibraryOK";
      }
    } catch (Exception e) {
      e.printStackTrace(System.out);
      context.getRequest().setAttribute("actionError", "An error was reported trying to use the specified directory: " + e.getMessage());
    }
    return "SetupSaveLibraryERROR";
  }

  public String executeCommandRegistrationForm(ActionContext context) {
    ApplicationPrefs prefs = getApplicationPrefs(context);
    if (SetupUtils.isConfigured(prefs)) {
      return "AlreadySetupOK";
    }
    if (SetupUtils.isServicesConfigured(prefs)) {
      return "SaveRegistrationOK";
    }
    SetupRegistrationBean bean = (SetupRegistrationBean) context.getFormBean();
    // If the form hasn't been seen by the user, make some assumptions
    if (context.getRequest().getParameter("auto-populate") == null) {
      bean.setSsl(true);
      bean.setProfile(RequestUtils.getServerUrl(context.getRequest()));
    }
    bean.setOs(System.getProperty("os.name") + " " + System.getProperty("os.arch") + " " + System.getProperty("os.version"));
    bean.setJava(System.getProperty("java.version"));
    bean.setWebserver(HTTPUtils.getServerName(
        context.getRequest().getScheme() + "://" +
        RequestUtils.getServerUrl(context.getRequest())));
    return "SetupRegistrationFormOK";
  }

  public synchronized String executeCommandSaveRegistration(ActionContext context) {
    ApplicationPrefs prefs = getApplicationPrefs(context);
    if (SetupUtils.isConfigured(prefs)) {
      return "AlreadySetupOK";
    }
    if (SetupUtils.isServicesConfigured(prefs)) {
      return "SaveRegistrationOK";
    }
    // Populate the bean
    SetupRegistrationBean bean = (SetupRegistrationBean) context.getFormBean();
    // Validate the form
    if (!bean.isValid()) {
      LOG.warn("Registration bean did not validate");
      processErrors(context, bean.getErrors());
      return "SaveRegistrationERROR";
    }
    // Set system properties
    bean.setOs(System.getProperty("os.name") + " " + System.getProperty("os.arch") + " " + System.getProperty("os.version"));
    bean.setJava(System.getProperty("java.version"));
    bean.setWebserver(HTTPUtils.getServerName(
        context.getRequest().getScheme() + "://" +
        RequestUtils.getServerUrl(context.getRequest())));
    // Set key for exchanging info
    Key key = PrivateString.generateKey();
    bean.setKey(PrivateString.encodeHex(key));
    bean.setCode(PrivateString.encrypt(key, ApplicationVersion.TITLE + " " + ApplicationVersion.VERSION));
    // Try to use the license client
    boolean success = false;
    try {
      // Retrieve the services license
      Class clientClass = Class.forName("com.concursive.connect.plugins.services.Client");
      Object registrationClient = ObjectUtils.constructObject(clientClass);
      success = ObjectUtils.invokeMethod(registrationClient, "processRegistration", bean);
      if (success) {
        prefs.add(ApplicationPrefs.CONCURSIVE_SERVICES_ID, bean.getId());
        prefs.add(ApplicationPrefs.CONCURSIVE_SERVICES_KEY, bean.getKey());
        prefs.add(ApplicationPrefs.CONCURSIVE_SERVICES_SERVER, bean.getUrl());
      }
    } catch (ClassNotFoundException e) {
      context.getRequest().setAttribute("actionError", "A registration component is missing");
      LOG.error("registration error", e);
      return "SaveRegistrationERROR";
    } catch (Exception e) {
      context.getRequest().setAttribute("actionError", "A connection error occurred: " + e.getMessage());
      LOG.error("registration error", e);
      return "SaveRegistrationERROR";
    }
    // Not sure why this value didn't get set
    if (!success || !StringUtils.hasText(bean.getId())) {
      context.getRequest().setAttribute("actionError", "An unspecified error occurred... try again?");
      return "SaveRegistrationERROR";
    }
    return "SaveRegistrationOK";
  }

  public String executeCommandConfigureDatabase(ActionContext context) {
    if (SetupUtils.isConfigured(getApplicationPrefs(context))) {
      return "AlreadySetupOK";
    }
    SetupDatabaseBean bean = (SetupDatabaseBean) context.getFormBean();
    if (bean.getDatabase() == null || "".equals(bean.getDatabase())) {
      bean.setDatabase("connect");
      // try to load from prefs
      ApplicationPrefs prefs = getApplicationPrefs(context);
      if (prefs.has("DATABASE.NAME")) {
        bean.setDatabase(prefs.get("DATABASE.NAME"));
      }
      if (prefs.has("SITE.DBTYPE")) {
        bean.setType(prefs.get("SITE.DBTYPE"));
      }
      if (prefs.has("SITE.ADDRESS")) {
        bean.setAddress(prefs.get("SITE.ADDRESS"));
      }
      if (prefs.has("SITE.PORT")) {
        bean.setPort(prefs.get("SITE.PORT"));
      }
      if (prefs.has("SITE.USER")) {
        bean.setUser(prefs.get("SITE.USER"));
      }
      if (prefs.has("SITE.PASSWORD")) {
        bean.setPassword(prefs.get("SITE.PASSWORD"));
      }
    }
    return "SetupConfigureDatabaseOK";
  }

  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandSaveDatabase(ActionContext context) {
    if (SetupUtils.isConfigured(getApplicationPrefs(context))) {
      return "AlreadySetupOK";
    }
    SetupDatabaseBean bean = (SetupDatabaseBean) context.getFormBean();
    if (bean.isValid()) {
      // Verify the database connection with the specified parameters
      Connection db = null;
      // If timeout needs to be adjusted, record the default before changing
      int timeout = DriverManager.getLoginTimeout();
      try {
        // Create a connection
        Class.forName(bean.getDriver());
        DriverManager.setLoginTimeout(10);
        db = DriverManager.getConnection(
            bean.getUrl(), bean.getUser(), bean.getPassword());
        db.close();
        // Store the database connection prefs
        ApplicationPrefs prefs = getApplicationPrefs(context);
        prefs.add("DATABASE.NAME", bean.getDatabase());
        prefs.add("SITE.DBTYPE", bean.getType());
        prefs.add("SITE.ADDRESS", String.valueOf(bean.getAddress()));
        prefs.add("SITE.PORT", String.valueOf(bean.getPort()));
        prefs.add(ApplicationPrefs.CONNECTION_DRIVER, bean.getDriver());
        prefs.add(ApplicationPrefs.CONNECTION_URL, bean.getUrl());
        prefs.add(ApplicationPrefs.CONNECTION_USER, bean.getUser());
        prefs.add(ApplicationPrefs.CONNECTION_PASSWORD, bean.getPassword());
        // Store the connection pool prefs
        prefs.add(ApplicationPrefs.CONNECTION_POOL_DEBUG, "false");
        prefs.add(ApplicationPrefs.CONNECTION_POOL_TEST_CONNECTIONS, "false");
        prefs.add(ApplicationPrefs.CONNECTION_POOL_ALLOW_SHRINKING, "true");
        prefs.add(ApplicationPrefs.CONNECTION_POOL_MAX_CONNECTIONS, "10");
        prefs.add(ApplicationPrefs.CONNECTION_POOL_MAX_IDLE_TIME, "60");
        prefs.add(ApplicationPrefs.CONNECTION_POOL_MAX_DEAD_TIME, "300");
        prefs.add(ApplicationPrefs.CONNECTION_POOL_MAX_RSS_CONNECTIONS, "2");
        prefs.add(ApplicationPrefs.CONNECTION_POOL_MAX_API_CONNECTIONS, "2");
        prefs.add(ApplicationPrefs.CONNECTION_POOL_MAX_WORKFLOW_CONNECTIONS, "3");
        prefs.add(ApplicationPrefs.CONNECTION_POOL_MAX_SCHEDULER_CONNECTIONS, "5");
        prefs.add(ApplicationPrefs.CONNECTION_POOL_MAX_CACHE_CONNECTIONS, "3");
        // save the temporary prefs so user can return
        prefs.save();
        // ask to reload prefs to instantiate connection pool
        prefs.initializePrefs(context.getServletContext());
        return "SetupSaveDatabaseOK";
      } catch (Exception e) {
        context.getRequest().setAttribute("actionError",
            "An error occurred while trying to connect to the database, the " +
            "following error was provided by the database driver: " + e.getMessage());
      } finally {
        DriverManager.setLoginTimeout(timeout);
        if (db != null) {
          try {
            db.close();
          } catch (Exception ee) {
          }
        }
      }
    }
    processErrors(context, bean.getErrors());
    return "SetupSaveDatabaseERROR";
  }

  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public synchronized String executeCommandInstallDatabase(ActionContext context) {
    ApplicationPrefs prefs = getApplicationPrefs(context);
    if (SetupUtils.isConfigured(prefs)) {
      return "AlreadySetupOK";
    }
    // Make sure a connection element is ready
    initializeConnectionElement(context);
    // Proceed with the database install
    Connection db = null;
    try {
      db = getConnection(context);
      String pathType = null;
      if (!SetupUtils.isDatabaseInstalled(db)) {
        // Determine directory
        switch (DatabaseUtils.getType(db)) {
          case DatabaseUtils.POSTGRESQL:
            System.out.println("Setup-> Installing PostgreSQL");
            pathType = "postgresql";
            break;
          case DatabaseUtils.MSSQL:
            System.out.println("Setup-> Installing MSSQL");
            pathType = "mssql";
            break;
          default:
            System.out.println("Setup-> * Database could not be determined: " + DatabaseUtils.getType(db));
            break;
        }
        // Install the schema
        DatabaseUtils.executeSQL(db, context.getServletContext().getResourceAsStream("/WEB-INF/database/" + pathType + "/new_db.sql"));
        DatabaseUtils.executeSQL(db, context.getServletContext().getResourceAsStream("/WEB-INF/database/" + pathType + "/new_project.sql"));
        DatabaseUtils.executeSQL(db, context.getServletContext().getResourceAsStream("/WEB-INF/database/" + pathType + "/new_project_blog.sql"));
        DatabaseUtils.executeSQL(db, context.getServletContext().getResourceAsStream("/WEB-INF/database/" + pathType + "/new_project_wiki.sql"));
        DatabaseUtils.executeSQL(db, context.getServletContext().getResourceAsStream("/WEB-INF/database/" + pathType + "/new_project_ads.sql"));
        DatabaseUtils.executeSQL(db, context.getServletContext().getResourceAsStream("/WEB-INF/database/" + pathType + "/new_project_badges.sql"));
        DatabaseUtils.executeSQL(db, context.getServletContext().getResourceAsStream("/WEB-INF/database/" + pathType + "/new_project_classifieds.sql"));
        DatabaseUtils.executeSQL(db, context.getServletContext().getResourceAsStream("/WEB-INF/database/" + pathType + "/new_task.sql"));
        DatabaseUtils.executeSQL(db, context.getServletContext().getResourceAsStream("/WEB-INF/database/" + pathType + "/new_ticket.sql"));
        DatabaseUtils.executeSQL(db, context.getServletContext().getResourceAsStream("/WEB-INF/database/" + pathType + "/new_order.sql"));
        DatabaseUtils.executeSQL(db, context.getServletContext().getResourceAsStream("/WEB-INF/database/" + pathType + "/new_timesheet.sql"));
        DatabaseUtils.executeSQL(db, context.getServletContext().getResourceAsStream("/WEB-INF/database/" + pathType + "/new_translation.sql"));
        DatabaseUtils.executeSQL(db, context.getServletContext().getResourceAsStream("/WEB-INF/database/" + pathType + "/new_reports.sql"));
        DatabaseUtils.executeSQL(db, context.getServletContext().getResourceAsStream("/WEB-INF/database/" + pathType + "/new_service.sql"));
        DatabaseUtils.executeSQL(db, context.getServletContext().getResourceAsStream("/WEB-INF/database/" + pathType + "/new_dashboard.sql"));
        // Load the objects and services
        SyncTableList syncTableList = loadSyncTableList(context);
        // Add default data
        SetupUtils.insertDefaultData(db, context.getServletContext(), "/WEB-INF/database/");
        // Add the default categories
        SetupUtils.insertDefaultCategories(db, syncTableList, prefs.get(ApplicationPrefs.FILE_LIBRARY_PATH) + "1" + fs + "projects" + fs);
        // One last verify...
        if (!SetupUtils.isDatabaseInstalled(db)) {
          throw new Exception("Database installation error.");
        }
      }
    } catch (Exception e) {
      e.printStackTrace(System.out);
      context.getRequest().setAttribute("actionError",
          "An error occurred while trying to install the database, the " +
          "following error was provided: " + e.getMessage());
      return "SetupInstallDatabaseERROR";
    } finally {
      freeConnection(context, db);
    }
    return "SetupInstallDatabaseOK";
  }

  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandConfigureDetails(ActionContext context) {
    if (SetupUtils.isConfigured(getApplicationPrefs(context))) {
      return "AlreadySetupOK";
    }
    // load settings if any
    SetupDetailsBean bean = (SetupDetailsBean) context.getFormBean();
    if (bean.getServer() == null) {
      // try to load from prefs
      if (hasPref(context, ApplicationPrefs.MAILSERVER)) {
        bean.setServer(getPref(context, ApplicationPrefs.MAILSERVER));
      }
      if (hasPref(context, ApplicationPrefs.MAILSERVER_USERNAME)) {
        bean.setServerUsername(getPref(context, ApplicationPrefs.MAILSERVER_USERNAME));
      }
      if (hasPref(context, ApplicationPrefs.MAILSERVER_PASSWORD)) {
        bean.setServerPassword(getPref(context, ApplicationPrefs.MAILSERVER_PASSWORD));
      }
      if (hasPref(context, ApplicationPrefs.MAILSERVER_PORT)) {
        bean.setServerPort(getPref(context, ApplicationPrefs.MAILSERVER_PORT));
      }
      if (hasPref(context, ApplicationPrefs.MAILSERVER_SSL)) {
        bean.setServerSsl(getPref(context, ApplicationPrefs.MAILSERVER_SSL));
      }
      if (hasPref(context, ApplicationPrefs.EMAILADDRESS)) {
        bean.setAddress(getPref(context, ApplicationPrefs.EMAILADDRESS));
      } else {
        bean.setAddress("Example Company <noreply@example.com>");
      }
      if (hasPref(context, ApplicationPrefs.GOOGLE_MAPS_API_DOMAIN)) {
        bean.setGoogleMapsAPIDomain(getPref(context, ApplicationPrefs.GOOGLE_MAPS_API_DOMAIN));
      }
      if (hasPref(context, ApplicationPrefs.GOOGLE_MAPS_API_KEY)) {
        bean.setGoogleMapsAPIKey(getPref(context, ApplicationPrefs.GOOGLE_MAPS_API_KEY));
      }
      if (hasPref(context, "ACCOUNT.SIZE")) {
        bean.setStorage(getPref(context, "ACCOUNT.SIZE"));
      }
    }
    return "SetupConfigureDetailsOK";
  }

  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandSaveDetails(ActionContext context) {
    if (SetupUtils.isConfigured(getApplicationPrefs(context))) {
      return "AlreadySetupOK";
    }
    SetupDetailsBean bean = (SetupDetailsBean) context.getFormBean();
    if (!bean.isValid()) {
      processErrors(context, bean.getErrors());
      return "SetupSaveDetailsERROR";
    }
    // save the settings
    ApplicationPrefs prefs = getApplicationPrefs(context);
    prefs.add(ApplicationPrefs.MAILSERVER, bean.getServer());
    prefs.add(ApplicationPrefs.MAILSERVER_USERNAME, bean.getServerUsername());
    prefs.add(ApplicationPrefs.MAILSERVER_PASSWORD, bean.getServerPassword());
    prefs.add(ApplicationPrefs.MAILSERVER_PORT, bean.getServerPort());
    prefs.add(ApplicationPrefs.MAILSERVER_SSL, bean.getServerSsl());
    prefs.add(ApplicationPrefs.EMAILADDRESS, bean.getAddress());
    prefs.add("ACCOUNT.SIZE", bean.getStorage());
    if (StringUtils.hasText(bean.getGoogleMapsAPIDomain()) &&
        StringUtils.hasText(bean.getGoogleMapsAPIKey())) {
      prefs.add(ApplicationPrefs.GOOGLE_MAPS_API_DOMAIN, RequestUtils.getDomainName(bean.getGoogleMapsAPIDomain()).trim());
      prefs.add(ApplicationPrefs.GOOGLE_MAPS_API_KEY, bean.getGoogleMapsAPIKey().trim());
    }
    // save the temporary prefs so user can return
    prefs.save();
    return "SetupSaveDetailsOK";
  }

  /**
   * Determines if the admin has been configured
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandConfigureAdmin(ActionContext context) {
    ApplicationPrefs prefs = getApplicationPrefs(context);
    if (SetupUtils.isConfigured(prefs)) {
      return "AlreadySetupOK";
    }
    // Make sure a connection element is ready
    initializeConnectionElement(context);
    // Proceed with configuration form
    Connection db = null;
    try {
      db = getConnection(context);
      if (SetupUtils.isAdminInstalled(db)) {
        return "SetupAdminCompleteOK";
      }
    } catch (Exception e) {
      context.getRequest().setAttribute("actionError",
          "An error occurred while trying to verify the account, the " +
          "following error was provided: " + e.getMessage());
      LOG.error("configureAdmin", e);
      return "SetupConfigureAdminERROR";
    } finally {
      freeConnection(context, db);
    }
    return "SetupConfigureAdminOK";
  }

  /**
   * Saves the admin's details
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public synchronized String executeCommandSaveAdmin(ActionContext context) {
    ApplicationPrefs prefs = getApplicationPrefs(context);
    if (SetupUtils.isConfigured(prefs)) {
      return "AlreadySetupOK";
    }
    User thisUser = (User) context.getFormBean();
    thisUser.validateEmail();
    if (!thisUser.isValid()) {
      processErrors(context, thisUser.getErrors());
      return "SetupSaveAdminERROR";
    }
    // Make sure a connection element is ready
    initializeConnectionElement(context);
    // Save the admin
    Connection db = null;
    try {
      db = getConnection(context);
      if (SetupUtils.isAdminInstalled(db)) {
        return "SetupAdminCompleteOK";
      }
      // Get user ip
      String ip = context.getRequest().getRemoteAddr();
      // Insert the new admin account
      thisUser.setUsername(thisUser.getEmail());
      SetupUtils.insertDefaultAdmin(db, thisUser, ip);
    } catch (Exception e) {
      context.getRequest().setAttribute("actionError",
          "An error occurred while trying to create the account, the " +
          "following error was provided: " + e.getMessage());
      LOG.error("userInsert", e);
      return "SetupSaveAdminERROR";
    } finally {
      freeConnection(context, db);
    }
    return "SetupSaveAdminOK";
  }

  /**
   * Prepares the page which inquires about the purpose of the site
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandConfigureSite(ActionContext context) {
    if (SetupUtils.isConfigured(getApplicationPrefs(context))) {
      return "AlreadySetupOK";
    }
    // Make sure a connection element is ready
    initializeConnectionElement(context);
    // Show the site config
    Connection db = null;
    try {
      db = getConnection(context);
      if (SetupUtils.isDefaultProjectInstalled(db)) {
        return "SetupSiteCompleteOK";
      }
    } catch (Exception e) {
      context.getRequest().setAttribute("actionError",
          "An error occurred while trying to verify the site, the " +
          "following error was provided: " + e.getMessage());
      return "SetupConfigureSiteERROR";
    } finally {
      freeConnection(context, db);
    }

    // load settings if any
    SetupSiteBean bean = (SetupSiteBean) context.getFormBean();
    if (bean.getTitle() == null) {
      if (hasPref(context, ApplicationPrefs.WEB_PAGE_TITLE)) {
        bean.setTitle(getPref(context, ApplicationPrefs.WEB_PAGE_TITLE));
      }
      if (hasPref(context, ApplicationPrefs.WEB_PAGE_DESCRIPTION)) {
        bean.setShortDescription(getPref(context, ApplicationPrefs.WEB_PAGE_DESCRIPTION));
      }
      if (hasPref(context, ApplicationPrefs.WEB_PAGE_KEYWORDS)) {
        bean.setKeywords(getPref(context, ApplicationPrefs.WEB_PAGE_KEYWORDS));
      }
      if (bean.getTitle() == null) {
        bean.setTitle("My Community");
      }
    }
    return "SetupConfigureSiteOK";
  }

  /**
   * Saves the purpose of the site
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public synchronized String executeCommandSaveSite(ActionContext context) {
    ApplicationPrefs prefs = getApplicationPrefs(context);
    if (SetupUtils.isConfigured(prefs)) {
      return "AlreadySetupOK";
    }
    SetupSiteBean bean = (SetupSiteBean) context.getFormBean();
    if (!bean.isValid()) {
      return "SetupSaveSiteERROR";
    }
    // Make sure a connection element is ready
    initializeConnectionElement(context);
    // Save the default site
    Connection db = null;
    try {
      db = getConnection(context);
      if (SetupUtils.isDefaultProjectInstalled(db)) {
        return "SetupSiteCompleteOK";
      }

      LOG.debug("Site purpose: " + bean.getPurpose());
      prefs.add(ApplicationPrefs.PURPOSE, bean.getPurpose());

      // Load the objects and services
      SyncTableList syncTableList = loadSyncTableList(context);

      // Prepare a default project
      Project project = new Project();
      project.setTitle(bean.getTitle());
      project.setShortDescription(bean.getShortDescription());
      project.setKeywords(bean.getKeywords());
      SetupUtils.insertDefaultSiteConfig(db, ApplicationPrefs.FILE_LIBRARY_PATH, project, bean.getPurpose());

      // The default profile content
      SetupUtils.insertDefaultContent(db, syncTableList, prefs.get(ApplicationPrefs.FILE_LIBRARY_PATH) + "1" + fs + "projects" + fs, project);

      // Update the application prefs
      prefs.add(ApplicationPrefs.WEB_PAGE_TITLE, bean.getTitle());
      prefs.save();

    } catch (Exception e) {
      context.getRequest().setAttribute("actionError",
          "An error occurred while trying to create the account, the " +
          "following error was provided: " + e.getMessage());
      LOG.error("userInsert", e);
      return "SetupSaveSiteERROR";
    } finally {
      freeConnection(context, db);
    }
    return "SetupSaveSiteOK";
  }


  // @todo Ask the installation user what the security option should be

  // @todo Ask the installation user what the invitation option should be

  // @todo Ask the installation user what the registration option should be

  // @todo Ask the installation user which tabs are enabled and protected

  /**
   * Completes the setup process
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandComplete(ActionContext context) {
    if (SetupUtils.isConfigured(getApplicationPrefs(context))) {
      return "AlreadySetupOK";
    }
    Connection db = null;
    User thisUser = null;
    // Make sure a connection element is ready
    initializeConnectionElement(context);
    // Complete the setup
    try {
      db = getConnection(context);
      if (!SetupUtils.isAdminInstalled(db)) {
        return "SetupInstallDatabaseERROR";
      }
      UserList userList = new UserList();
      userList.setAdmin(Constants.TRUE);
      userList.buildList(db);
      thisUser = userList.get(0);
    } catch (Exception e) {
      context.getRequest().setAttribute("actionError",
          "An error occurred while finalizing setup.");
      return "SetupInstallDatabaseERROR";
    } finally {
      freeConnection(context, db);
    }
    // Tell setup we are done!
    getApplicationPrefs(context).add("CONFIGURING", null);
    // save the prefs so user can login
    getApplicationPrefs(context).save();
    // startup the rest of the application
    getApplicationPrefs(context).initializePrefs(context.getServletContext());
    // now that the indexer is started, add the admin to the index
    Project userProfile = thisUser.getProfileProject();
    // add the admin to the workflow engine to get default data associated
    processInsertHook(context, userProfile);
    // index the admin
    indexAddItem(context, userProfile);
    // index the default project
    Project project = ProjectUtils.loadProject("main-profile");
    indexAddItem(context, project);
    return "SetupCompleteOK";
  }

  public static ConnectionElement initializeConnectionElement(ActionContext context) {
    ApplicationPrefs prefs = ApplicationPrefs.getApplicationPrefs(context.getServletContext());
    ConnectionElement ce = new ConnectionElement();
    ce.setDriver(prefs.get(ApplicationPrefs.CONNECTION_DRIVER));
    ce.setUrl(prefs.get(ApplicationPrefs.CONNECTION_URL));
    ce.setUsername(prefs.get(ApplicationPrefs.CONNECTION_USER));
    ce.setPassword(prefs.get(ApplicationPrefs.CONNECTION_PASSWORD));
    context.getSession().setAttribute(Constants.SESSION_CONNECTION_ELEMENT, ce);
    return ce;
  }

  public static SyncTableList loadSyncTableList(ActionContext context) {
    try {
      // Load the objects and services
      int SYSTEM_ID = 1;
      LOG.debug("Loading the object map...");
      SyncTableList syncTableList = new SyncTableList();
      syncTableList.setSystemId(SYSTEM_ID);
      // Load the core mappings
      syncTableList.loadObjectMap(SyncTableList.class.getResourceAsStream("/object_map.xml"));
      // Load plug-in mappings in the services path
      Set<String> serviceFiles = context.getServletContext().getResourcePaths("/WEB-INF/services/");
      if (serviceFiles != null && serviceFiles.size() > 0) {
        for (String thisFile : serviceFiles) {
          if (thisFile.endsWith(".xml")) {
            try {
              LOG.debug("Adding services from... " + thisFile);
              syncTableList.loadObjectMap(context.getServletContext().getResourceAsStream(thisFile));
            } catch (Exception e) {
              LOG.error("getObjectMap exception", e);
            }
          }
        }
      }
      return syncTableList;
    } catch (Exception e) {
      LOG.error("Couldn't load syncTableList", e);
    }
    return null;
  }
}
