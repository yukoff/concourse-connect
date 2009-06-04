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
package com.concursive.connect.config;

import com.concursive.commons.codec.PrivateString;
import com.concursive.commons.db.ConnectionElement;
import com.concursive.commons.db.ConnectionPool;
import com.concursive.commons.jsp.JspUtils;
import com.concursive.commons.workflow.ObjectHookManager;
import com.concursive.commons.xml.XMLUtils;
import com.concursive.connect.Constants;
import com.concursive.connect.cache.CacheContext;
import com.concursive.connect.cache.Caches;
import com.concursive.connect.indexer.IndexerContext;
import com.concursive.connect.indexer.IndexerFactory;
import com.concursive.connect.scheduler.ScheduledJobs;
import com.concursive.connect.web.modules.upgrade.utils.UpgradeUtils;
import com.concursive.connect.web.webdav.WebdavManager;
import com.concursive.connect.workflow.utils.WorkflowUtils;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.cache.WebappTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Scheduler;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.servlet.ServletContext;
import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.security.Key;
import java.sql.Connection;
import java.util.*;
import java.util.prefs.Preferences;

/**
 * Handles storing and retrieving web application preferences
 *
 * @author matt rajkowski
 * @version $Id: ApplicationPrefs.java,v 2.18.2.1 2004/09/13 02:30:47 matt Exp
 *          $
 * @created August 26, 2003
 */
public class ApplicationPrefs {

  // Logger
  private static Log LOG = LogFactory.getLog(ApplicationPrefs.class);

  // Object constants
  public final static String FILE_LIBRARY_PATH = "FILELIBRARY";
  public final static String TEAM_KEY = "TEAM.KEY";

  // Connection Properties
  public final static String CONNECTION_DRIVER = "SITE.DRIVER";
  public final static String CONNECTION_URL = "SITE.URL";
  public final static String CONNECTION_USER = "SITE.USER";
  public final static String CONNECTION_PASSWORD = "SITE.PASSWORD";
  // Connection Pool Properties
  public final static String CONNECTION_POOL_DEBUG = "CONNECTION_POOL.DEBUG";
  public final static String CONNECTION_POOL_TEST_CONNECTIONS = "CONNECTION_POOL.TEST_CONNECTIONS";
  public final static String CONNECTION_POOL_ALLOW_SHRINKING = "CONNECTION_POOL.ALLOW_SHRINKING";
  public final static String CONNECTION_POOL_MAX_CONNECTIONS = "CONNECTION_POOL.MAX_CONNECTIONS";
  public final static String CONNECTION_POOL_MAX_IDLE_TIME = "CONNECTION_POOL.MAX_IDLE_TIME.SECONDS";
  public final static String CONNECTION_POOL_MAX_DEAD_TIME = "CONNECTION_POOL.MAX_DEAD_TIME.SECONDS";
  public final static String CONNECTION_POOL_MAX_RSS_CONNECTIONS = "CONNECTION_POOL.MAX_CONNECTIONS.RSS";
  public final static String CONNECTION_POOL_MAX_API_CONNECTIONS = "CONNECTION_POOL.MAX_CONNECTIONS.API";
  public final static String CONNECTION_POOL_MAX_WORKFLOW_CONNECTIONS = "CONNECTION_POOL.MAX_CONNECTIONS.WORKFLOW";
  public final static String CONNECTION_POOL_MAX_SCHEDULER_CONNECTIONS = "CONNECTION_POOL.MAX_CONNECTIONS.APPS";
  public final static String CONNECTION_POOL_MAX_CACHE_CONNECTIONS = "CONNECTION_POOL.MAX_CONNECTIONS.CACHE";
  // Mail Server Properties
  public final static String MAILSERVER = "MAILSERVER";
  public final static String MAILSERVER_USERNAME = "MAILSERVER.CONNECTION.USERNAME";
  public final static String MAILSERVER_PASSWORD = "MAILSERVER.CONNECTION.PASSWORD";
  public final static String MAILSERVER_PORT = "MAILSERVER.CONNECTION.PORT";
  public final static String MAILSERVER_SSL = "MAILSERVER.CONNECTION.SSL";
  public final static String EMAILADDRESS = "EMAILADDRESS";
  // Google Maps Properties
  public final static String GOOGLE_MAPS_API_DOMAIN = "GOOGLE_MAPS.DOMAIN";
  public final static String GOOGLE_MAPS_API_KEY = "GOOGLE_MAPS.KEY";
  // Services Properties
  public final static String CONCURSIVE_SERVICES_SERVER = "CONCURSIVE_SERVICES.SERVER";
  public final static String CONCURSIVE_SERVICES_ID = "CONCURSIVE_SERVICES.ID";
  public final static String CONCURSIVE_SERVICES_KEY = "CONCURSIVE_SERVICES.KEY";
  // Web Application Behavior Properties
  public final static String LOGIN_MODE = "LOGIN.MODE";
  public final static String PURPOSE = "PURPOSE";
  public final static String WEB_URL = "URL";
  public final static String WEB_PORT = "URL.PORT";
  // Web Application Look and Feel
  public final static String THEME = "THEME";
  public final static String COLOR_SCHEME = "COLOR_SCHEME";
  public final static String JSP_TEMPLATE = "TEMPLATE";
  public final static String CSS_FILE = "CSS";
  public final static String HOME_URL = "PORTAL.INDEX";
  public final static String WEB_PAGE_TITLE = "TITLE";
  public final static String WEB_PAGE_DESCRIPTION = "DESCRIPTION";
  public final static String WEB_PAGE_KEYWORDS = "KEYWORDS";
  public final static String WEB_PAGE_LOGO = "LOGO";
  // Application Behavior Properties
  public final static String WORKFLOW_FILE = "WORKFLOW";
  public final static String SYSTEM_SETTINGS_FILE = "SETTINGS";
  public final static String USERS_CAN_REGISTER = "REGISTER";
  public final static String INFORMATION_IS_SENSITIVE = "SENSITIVE_INFORMATION";
  public final static String USERS_CAN_INVITE = "INVITE";
  public final static String USERS_CAN_START_PROJECTS = "START_PROJECTS";
  public final static String USERS_ARE_ANONYMOUS = "ANONYMOUS";
  public final static String SHOW_TERMS_AND_CONDITIONS = "LICENSE";
  public final static String SEARCH_USES_LOCATION = "USE_LOCATIONS";
  public final static String LANGUAGE = "SYSTEM.LANGUAGE";
  public final static String LANGUAGES_SUPPORTED = "SUPPORTED.LANGUAGES";
  public final static String MAIN_PROFILE = "MAIN_PROFILE";
  // Default values
  public final static String DEFAULT_NODE = "primary";

  // System constants
  public final static String ls = System.getProperty("line.separator");
  public final static String fs = System.getProperty("file.separator");

  // Variable context properties
  private String node = null;
  private Map<String, Dictionary> dictionaries = new HashMap<String, Dictionary>();
  private Map<String, String> prefs = new LinkedHashMap<String, String>();
  private Map<String, String> nodePrefs = new LinkedHashMap<String, String>();


  /**
   * Constructor for the ApplicationPrefs object
   */
  public ApplicationPrefs() {
  }


  /**
   * Constructor for the ApplicationPrefs object
   *
   * @param context Description of the Parameter
   */
  public ApplicationPrefs(ServletContext context) {
    initializePrefs(context);
  }


  /**
   * Description of the Method
   *
   * @param param Description of the Parameter
   * @return Description of the Return Value
   */
  public String get(String param) {
    String value = nodePrefs.get(param);
    if (value == null) {
      value = prefs.get(param);
    }
    return value;
  }

  public String get(String param, String defaultValue) {
    String value = get(param);
    if (value == null) {
      return defaultValue;
    }
    return value;
  }


  /**
   * Description of the Method
   *
   * @param param Description of the Parameter
   * @return Description of the Return Value
   */
  public boolean has(String param) {
    return (prefs.containsKey(param) || nodePrefs.containsKey(param));
  }


  /**
   * Description of the Method
   *
   * @param param Description of the Parameter
   * @param value Description of the Parameter
   */
  public void add(String param, String value) {
    if (param != null) {
      if (value != null) {
        prefs.put(param, value);
      } else {
        prefs.remove(param);
      }
    }
  }

  public void add(String param, boolean value) {
    add(param, (value ? "true" : "false"));
  }

  public Map<String, String> getPrefs() {
    Map<String, String> allPrefs = new LinkedHashMap<String, String>();
    allPrefs.putAll(prefs);
    allPrefs.putAll(nodePrefs);
    return allPrefs;
  }

  /**
   * Description of the Method
   */
  public void clear() {
    prefs.clear();
    nodePrefs.clear();
  }


  /**
   * Initializes preferences
   *
   * @param context ServletContext
   */
  public void initializePrefs(ServletContext context) {
    LOG.info("Initializing...");
    // Load the application node name, if any
    try {
      Properties instanceProperties = new Properties();
      instanceProperties.load(context.getResourceAsStream("/WEB-INF/instance.property"));
      node = instanceProperties.getProperty("node", DEFAULT_NODE);
      LOG.info("Node: " + node);
    } catch (Exception e) {
      node = DEFAULT_NODE;
    }
    // Determine the file library
    String fileLibrary = retrieveFileLibraryLocation(context);
    if (fileLibrary != null) {
      loadProperties(fileLibrary);
      this.add(FILE_LIBRARY_PATH, fileLibrary);
      configureDebug();
      verifyKey(context, fileLibrary);
      configureConnectionPool(context);
      configureFreemarker(context);
      configureWebdavManager(context);
      configureSystemSettings(context);
      configureCache(context);
      if (isConfigured()) {
        if (ApplicationVersion.isOutOfDate(this) && "true".equals(get("AUTO_UPGRADE"))) {
          // Use a lock file to to start upgrading
          File upgradeLockFile = new File(fileLibrary + "upgrade.lock");
          FileChannel fileChannel = null;
          FileLock fileLock = null;
          try {
            // Configure the file for locking
            fileChannel = new RandomAccessFile(upgradeLockFile, "rw").getChannel();
            // Use fileChannel.lock which blocks until the lock is obtained
            fileLock = fileChannel.lock();
            // Reload the prefs to make sure the upgrade isn't already complete
            loadProperties(fileLibrary);
            if (ApplicationVersion.isOutOfDate(this)) {
              // The application needs an update and auto upgrade is enabled
              performUpgrade(context);
            }
          } catch (Exception e) {
            LOG.error("initializePrefs-> performUpgrade", e);
          } finally {
            try {
              if (fileLock != null) {
                fileLock.release();
              }
              if (fileChannel != null) {
                fileChannel.close();
              }
            } catch (Exception eclose) {
              LOG.error("initializePrefs-> lock", eclose);
            }
          }
        }
        if (!ApplicationVersion.isOutOfDate(this)) {
          // Start the services now that everything is ready
          initializeServices(context);
        }
      }
    }
    configureDefaultBehavior(context);
    loadApplicationDictionaries(context);
  }


  /**
   * Initializes services
   *
   * @param context ServletContext
   */
  public void initializeServices(ServletContext context) {
    // These require up-to-date objects, so postpone if an update is needed
    configureWorkflowManager(context);
    configureScheduler(context);
    configureIndexer();
  }


  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  private String retrieveFileLibraryLocation(ServletContext context) {
    String dir = ApplicationPrefs.getRealPath(context);
    try {
      if (dir == null) {
        dir = node;
      }
      // Read from Preferences
      LOG.info("Java preferences key: " + dir);
      Preferences javaPrefs = Preferences.userNodeForPackage(ApplicationPrefs.class);
      // Check "dir" prefs first, based on the installed directory of this webapp
      String fileLibrary = null;
      if (dir.length() <= Preferences.MAX_KEY_LENGTH) {
        fileLibrary = javaPrefs.get(dir, null);
      } else {
        fileLibrary = javaPrefs.get(dir.substring(dir.length() - Preferences.MAX_KEY_LENGTH), null);
      }
      boolean doSave = false;
      // Preferences not found
      if (fileLibrary == null) {
        // Check in the current dir of the webapp for a pointer to the properties
        // NOTE: Some containers return null for getRealPath()
        String realPath = ApplicationPrefs.getRealPath(context);
        if (realPath != null) {
          fileLibrary = realPath + "WEB-INF" + fs + "fileLibrary" + fs;
          doSave = true;
        }
      }
      // See if properties exist
      if (fileLibrary != null) {
        File propertyFile = new File(fileLibrary + "build.properties");
        if (propertyFile.exists()) {
          if (doSave) {
            saveFileLibraryLocation(dir, fileLibrary);
          }
          return fileLibrary;
        }
      }
    } catch (Exception e) {
      LOG.error("ApplicationPrefs", e);
      e.printStackTrace(System.out);
    }
    return null;
  }


  /**
   * Constructor for the ApplicationPrefs object
   *
   * @param path Description of the Parameter
   */
  public void loadProperties(String path) {
    this.clear();
    // Application properties
    loadProperties(path, "build.properties", prefs);
    // Any node properties
    File nodeFile = new File(path + "instances" + fs + node + ".properties");
    if (nodeFile.exists()) {
      loadProperties(path + "instances" + fs, node + ".properties", nodePrefs);
    }
  }

  private void loadProperties(String path, String filename, Map<String, String> prefsToAddTo) {
    try {
      BufferedReader in = new BufferedReader(new FileReader(path + filename));
      String line = null;
      int count = 0;
      while ((line = in.readLine()) != null) {
        ++count;
        if (!line.startsWith("#") && line.indexOf("=") > 0) {
          String param = line.substring(0, line.indexOf("="));
          String value = "";
          if (line.indexOf("=") + 1 < line.length()) {
            value = line.substring(line.indexOf("=") + 1);
          }
          prefsToAddTo.put(param, value);
        } else {
          prefsToAddTo.put("#" + count, line);
        }
      }
      in.close();
      LOG.info("Read properties: " + path + filename + " (" + count + ")");
    } catch (Exception e) {
      LOG.error("Could not load properties from: " + path + filename, e);
    }
  }


  /**
   * Description of the Method
   */
  private void configureDebug() {
    if (this.has("DEBUG")) {
      System.setProperty("Debug", this.get("DEBUG"));
      System.setProperty("DEBUG", this.get("DEBUG"));
    }
  }


  /**
   * Description of the Method
   *
   * @param context     Description of the Parameter
   * @param fileLibrary Description of the Parameter
   */
  private void verifyKey(ServletContext context, String fileLibrary) {
    // Configure the encryption key
    Key key = PrivateString.generateKeyFile(fileLibrary + "team.key");
    context.setAttribute(TEAM_KEY, key);
  }


  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   */
  public void configureConnectionPool(ServletContext context) {
    //Define the ConnectionPool, else defaults from the ContextListener will be used
    ConnectionPool cp = (ConnectionPool) context.getAttribute(Constants.CONNECTION_POOL);
    if (cp != null) {
      // Apply any settings
      if (this.has(CONNECTION_POOL_DEBUG)) {
        cp.setDebug(this.get(CONNECTION_POOL_DEBUG));
      }
      if (this.has(CONNECTION_POOL_TEST_CONNECTIONS)) {
        cp.setTestConnections(this.get(CONNECTION_POOL_TEST_CONNECTIONS));
      }
      if (this.has(CONNECTION_POOL_ALLOW_SHRINKING)) {
        cp.setAllowShrinking(this.get(CONNECTION_POOL_ALLOW_SHRINKING));
      }
      if (this.has(CONNECTION_POOL_MAX_CONNECTIONS)) {
        cp.setMaxConnections(this.get(CONNECTION_POOL_MAX_CONNECTIONS));
      }
      if (this.has(CONNECTION_POOL_MAX_IDLE_TIME)) {
        cp.setMaxIdleTimeSeconds(this.get(CONNECTION_POOL_MAX_IDLE_TIME));
      }
      if (this.has(CONNECTION_POOL_MAX_DEAD_TIME)) {
        cp.setMaxDeadTimeSeconds(this.get(CONNECTION_POOL_MAX_DEAD_TIME));
      }
      // Clone it for RSS Feeds
      if (this.get(CONNECTION_POOL_MAX_RSS_CONNECTIONS) != null) {
        ConnectionPool rssCP = new ConnectionPool();
        rssCP.setDebug(cp.getDebug());
        rssCP.setTestConnections(cp.getTestConnections());
        rssCP.setAllowShrinking(cp.getAllowShrinking());
        rssCP.setMaxConnections(this.get(CONNECTION_POOL_MAX_RSS_CONNECTIONS));
        rssCP.setMaxIdleTime(cp.getMaxIdleTime());
        rssCP.setMaxDeadTime(cp.getMaxDeadTime());
        context.setAttribute(Constants.CONNECTION_POOL_RSS, rssCP);
      } else {
        context.setAttribute(Constants.CONNECTION_POOL_RSS, cp);
      }
      // Clone it for API Requests
      if (this.get(CONNECTION_POOL_MAX_API_CONNECTIONS) != null) {
        ConnectionPool apiCP = new ConnectionPool();
        apiCP.setDebug(cp.getDebug());
        apiCP.setTestConnections(cp.getTestConnections());
        apiCP.setAllowShrinking(cp.getAllowShrinking());
        apiCP.setMaxConnections(this.get(CONNECTION_POOL_MAX_API_CONNECTIONS));
        apiCP.setMaxIdleTime(cp.getMaxIdleTime());
        apiCP.setMaxDeadTime(cp.getMaxDeadTime());
        context.setAttribute(Constants.CONNECTION_POOL_API, apiCP);
      } else {
        context.setAttribute(Constants.CONNECTION_POOL_API, cp);
      }
    } else {
      LOG.error("ConnectionPool is null");
    }
  }


  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   */
  public void configureWorkflowManager(ServletContext context) {
    // Load the defaults
    ObjectHookManager hookManager = (ObjectHookManager) context.getAttribute(Constants.OBJECT_HOOK_MANAGER);
    if (hookManager != null) {
      // Configure a few settings
      hookManager.setFileLibraryPath(this.get(FILE_LIBRARY_PATH));
      hookManager.setApplicationPrefs(this.getPrefs());
      if (ApplicationPrefs.getFreemarkerConfiguration(context) == null) {
        LOG.error("Free marker configuration is null");
      }
      hookManager.setFreemarkerConfiguration(ApplicationPrefs.getFreemarkerConfiguration(context));
      hookManager.setKey((Key) context.getAttribute(TEAM_KEY));
      try {
        // Configure a separate connection pool
        ConnectionPool commonCP = (ConnectionPool) context.getAttribute(Constants.CONNECTION_POOL);
        if (commonCP != null) {
          if (!this.has(CONNECTION_POOL_MAX_WORKFLOW_CONNECTIONS)) {
            hookManager.setConnectionPool((ConnectionPool) context.getAttribute(Constants.CONNECTION_POOL));
          } else {
            ConnectionPool workflowCP = new ConnectionPool();
            workflowCP.setDebug(commonCP.getDebug());
            workflowCP.setTestConnections(commonCP.getTestConnections());
            workflowCP.setAllowShrinking(commonCP.getAllowShrinking());
            workflowCP.setMaxConnections(this.get(CONNECTION_POOL_MAX_WORKFLOW_CONNECTIONS));
            workflowCP.setMaxIdleTime(commonCP.getMaxIdleTime());
            workflowCP.setMaxDeadTime(commonCP.getMaxDeadTime());
            hookManager.setConnectionPool(workflowCP);
          }
        }
        ConnectionElement ce = new ConnectionElement();
        ce.setDriver(this.get(CONNECTION_DRIVER));
        ce.setUrl(this.get(CONNECTION_URL));
        ce.setUsername(this.get(CONNECTION_USER));
        ce.setPassword(this.get(CONNECTION_PASSWORD));
        hookManager.setConnectionElement(ce);
        WorkflowUtils.addWorkflow(hookManager, context);
      } catch (Exception e) {
        e.printStackTrace(System.out);
        LOG.error("Workflow Error: " + e.getMessage(), e);
      }
    }
  }

  private void configureScheduler(ServletContext context) {
    Scheduler scheduler = (Scheduler) context.getAttribute(Constants.SCHEDULER);
    if (scheduler != null) {
      // Initialize
      try {
        scheduler.getContext().setAllowsTransientData(true);
        scheduler.getContext().put("ServletContext", context);
        // Give the scheduler its own connection pool... this can speed up the web-tier
        // when background processing is occurring
        ConnectionPool commonCP = (ConnectionPool) context.getAttribute(Constants.CONNECTION_POOL);
        if (commonCP != null) {
          if (!this.has(CONNECTION_POOL_MAX_SCHEDULER_CONNECTIONS)) {
            scheduler.getContext().put(Constants.CONNECTION_POOL, context.getAttribute(Constants.CONNECTION_POOL));
          } else {
            ConnectionPool schedulerCP = new ConnectionPool();
            schedulerCP.setDebug(commonCP.getDebug());
            schedulerCP.setTestConnections(commonCP.getTestConnections());
            schedulerCP.setAllowShrinking(commonCP.getAllowShrinking());
            schedulerCP.setMaxConnections(this.get(CONNECTION_POOL_MAX_SCHEDULER_CONNECTIONS));
            schedulerCP.setMaxIdleTime(commonCP.getMaxIdleTime());
            schedulerCP.setMaxDeadTime(commonCP.getMaxDeadTime());
            scheduler.getContext().put("ConnectionPool", schedulerCP);
          }
        }
        ConnectionElement ce = new ConnectionElement();
        ce.setDriver(this.get(CONNECTION_DRIVER));
        ce.setUrl(this.get(CONNECTION_URL));
        ce.setUsername(this.get(CONNECTION_USER));
        ce.setPassword(this.get(CONNECTION_PASSWORD));
        scheduler.getContext().put("ConnectionElement", ce);
        scheduler.getContext().put("ApplicationPrefs", this);
        scheduler.start();
        scheduler.getContext().put(ScheduledJobs.CONTEXT_SCHEDULER_GROUP, ScheduledJobs.UNIQUE_GROUP);
        ScheduledJobs.addJobs(scheduler, context);
      } catch (Exception e) {
        e.printStackTrace(System.out);
        LOG.error("Scheduler Error: " + e.getMessage(), e);
      }
    }
  }


  private void configureWebdavManager(ServletContext context) {
    WebdavManager webdavManager = (WebdavManager) context.getAttribute(Constants.WEBDAV_MANAGER);
    if (webdavManager != null) {
      webdavManager.setFileLibraryPath(this.get(FILE_LIBRARY_PATH));
    }
  }

  private void configureSystemSettings(ServletContext context) {
    SystemSettings systemSettings = new SystemSettings();
    try {
      // Load the settings...
      InputStream source = null;
      // Look in build.properties, or use default
      String settingsFile = this.get(SYSTEM_SETTINGS_FILE);
      if (settingsFile != null) {
        LOG.info("SystemSettings path: " + this.get(FILE_LIBRARY_PATH) + settingsFile);
        source = new FileInputStream(this.get(FILE_LIBRARY_PATH) + settingsFile);
      } else {
        LOG.info("SystemSettings path: /WEB-INF/settings.xml");
        source = context.getResourceAsStream("/WEB-INF/settings.xml");
      }
      if (source != null) {
        LOG.info("Loading system settings...");
        XMLUtils xml = new XMLUtils(source);
        systemSettings.initialize(xml.getDocumentElement());
        source.close();
      }
    } catch (Exception e) {
      e.printStackTrace(System.out);
      LOG.error("System Settings Error", e);
    }
    context.setAttribute(Constants.SYSTEM_SETTINGS, systemSettings);
  }


  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   */
  public void configureDefaultBehavior(ServletContext context) {
    LOG.info("Configuring default behavior...");
    // Default login and session validation
    if (!this.has(LOGIN_MODE)) {
      this.add(LOGIN_MODE, "Default");
    }
    // Detect if this instance is using a legacy default, and upgrade to a theme
    if (!this.has(THEME)) {
      if (!this.has(JSP_TEMPLATE) || "/layoutDefault.jsp".equals(this.get(JSP_TEMPLATE))) {
        this.add(THEME, "default");
        this.add(COLOR_SCHEME, "dark_blue");
      }
    }

    // Determine the site theme
    if (this.has(THEME)) {
      // Use a theme and its color scheme; always use the default if the theme is missing
      String theme = "default";
      String colorScheme = "dark_blue";
      Set<String> themeFiles = context.getResourcePaths("/themes/" + this.get(THEME) + "/color-schemes");
      if (themeFiles != null && themeFiles.size() > 0) {
        for (String thisFile : themeFiles) {
          if (thisFile.startsWith("/themes/" + this.get(THEME) + "/color-schemes/" + this.get(COLOR_SCHEME))) {
            theme = this.get(THEME);
            colorScheme = this.get(COLOR_SCHEME);
          }
        }
      }
      if (!theme.equals(get(THEME))) {
        LOG.error("The theme (" + get(THEME) + ") and color scheme (" + get(COLOR_SCHEME) + ") could not be found, using default theme");
      }
      addParameter(context, Constants.TEMPLATE_THEME, theme);
      addParameter(context, Constants.TEMPLATE_COLOR_SCHEME, colorScheme);
      // Determine the layout (or use the default layout)
      String layout = "default";
      Set<String> layoutFiles = context.getResourcePaths("/themes/" + this.get(THEME) + "/jsp/");
      if (layoutFiles != null && layoutFiles.size() > 0) {
        for (String thisFile : layoutFiles) {
          if (("/themes/" + this.get(THEME) + "/jsp/layout.jsp").equals(thisFile)) {
            layout = this.get(THEME);
          }
        }
      }
      if ("default".equals(layout) && !"default".equals(this.get(THEME))) {
        try {
          // Check for a compiled layout
          Class.forName("org.apache.jsp.themes." + JspUtils.makeJavaIdentifier(this.get(THEME)) + ".jsp.layout_jsp");
          layout = this.get(THEME);
        } catch (Exception e) {
          LOG.info("Using default theme: " + e.getMessage());
        }
      }
      addParameter(context, Constants.TEMPLATE_LAYOUT, "/themes/" + layout + "/jsp/layout.jsp");
      LOG.info("THEME: " + get(THEME));
      LOG.info("  COLOR SCHEME: " + get(COLOR_SCHEME));
      LOG.info("  LAYOUT: " + "/themes/" + layout + "/jsp/layout.jsp");
    } else {
      // Use the specified template
      addParameter(context, Constants.TEMPLATE_LAYOUT, this.get(JSP_TEMPLATE));
      // Default CSS for all items on page
      addParameter(context, Constants.TEMPLATE_CSS, this.get(CSS_FILE));
    }

    // Default color scheme for themeable items (deprecated)
    if (this.has("SKIN")) {
      addParameter(context, "SKIN", this.get("SKIN"));
    } else {
      addParameter(context, "SKIN", "blue");
      this.add("SKIN", "blue");
    }

    // Application Settings
    if (!this.has(USERS_CAN_REGISTER)) {
      this.add(USERS_CAN_REGISTER, "true");
    }
    if (!this.has(USERS_CAN_INVITE)) {
      this.add(USERS_CAN_INVITE, "true");
    }
    if (!this.has(SHOW_TERMS_AND_CONDITIONS)) {
      this.add(SHOW_TERMS_AND_CONDITIONS, "true");
    }
    if (!this.has(USERS_CAN_START_PROJECTS)) {
      this.add(USERS_CAN_START_PROJECTS, "false");
    }
    if (!this.has(USERS_ARE_ANONYMOUS)) {
      this.add(USERS_ARE_ANONYMOUS, "false");
    }
    if (!this.has(SEARCH_USES_LOCATION)) {
      this.add(SEARCH_USES_LOCATION, "true");
    }

    // Portal
    if (!this.has(HOME_URL)) {
      this.add(HOME_URL, "index.shtml");
    } else if ("Portal.do?command=Default".equals(this.get(HOME_URL))) {
      this.add(HOME_URL, "index.shtml");
    }
    if (!this.has("PORTAL")) {
      this.add("PORTAL", "true");
    }
    if (!this.has(WEB_PAGE_TITLE)) {
      this.add(WEB_PAGE_TITLE, ApplicationVersion.TITLE);
    }
    if (!this.has(LANGUAGE)) {
      this.add(LANGUAGE, "en_US");
    }
    if (!this.has(LANGUAGES_SUPPORTED)) {
      this.add(LANGUAGES_SUPPORTED, "en_US");
    }
    if (!this.has(MAIN_PROFILE)) {
      this.add(MAIN_PROFILE, "main-profile");
    }
  }

  private void configureCache(ServletContext context) {
    CacheContext cacheContext = new CacheContext();
    // Give the cache manager its own connection pool... this can speed up the web-tier
    // when background processing is occurring
    ConnectionPool commonCP = (ConnectionPool) context.getAttribute(Constants.CONNECTION_POOL);
    if (commonCP != null) {
      if (!this.has(CONNECTION_POOL_MAX_CACHE_CONNECTIONS)) {
        cacheContext.setConnectionPool(commonCP);
      } else {
        ConnectionPool cacheCP = new ConnectionPool();
        cacheCP.setDebug(commonCP.getDebug());
        cacheCP.setTestConnections(commonCP.getTestConnections());
        cacheCP.setAllowShrinking(commonCP.getAllowShrinking());
        cacheCP.setMaxConnections(this.get(CONNECTION_POOL_MAX_CACHE_CONNECTIONS));
        cacheCP.setMaxIdleTime(commonCP.getMaxIdleTime());
        cacheCP.setMaxDeadTime(commonCP.getMaxDeadTime());
        cacheContext.setConnectionPool(cacheCP);
      }
    }
    ConnectionElement ce = new ConnectionElement();
    ce.setDriver(this.get(CONNECTION_DRIVER));
    ce.setUrl(this.get(CONNECTION_URL));
    ce.setUsername(this.get(CONNECTION_USER));
    ce.setPassword(this.get(CONNECTION_PASSWORD));
    cacheContext.setConnectionElement(ce);
    cacheContext.setApplicationPrefs(this);
    Caches.addCaches(cacheContext);
  }

  private void configureIndexer() {
    // Determine the Lucene Indexer Context
    LOG.info("Configuring Indexer...");
    IndexerContext indexerContext = new IndexerContext(this);
    try {
      // Get the IndexerService instance... which configures it as well
      IndexerFactory.getInstance().initializeIndexService(indexerContext);
    } catch (Exception e) {
      LOG.error("configureIndexer", e);
      e.printStackTrace(System.out);
    }
  }

  /**
   * Configures freemarker for template loading
   *
   * @param context the servlet context to store the configuration
   */
  private void configureFreemarker(ServletContext context) {
    try {
      Configuration freemarkerConfiguration = new Configuration();
      // Customized templates are stored here
      File customEmailFolder = new File(get(FILE_LIBRARY_PATH) + "1" + fs + "email");
      FileTemplateLoader ftl = null;
      if (customEmailFolder.exists()) {
        ftl = new FileTemplateLoader(customEmailFolder);
      }
      // Default templates are stored here
      WebappTemplateLoader wtl = new WebappTemplateLoader(context, "/WEB-INF/email");
      // Order the loaders
      TemplateLoader[] loaders = null;
      if (ftl != null) {
        loaders = new TemplateLoader[]{ftl, wtl};
      } else {
        loaders = new TemplateLoader[]{wtl};
      }
      MultiTemplateLoader mtl = new MultiTemplateLoader(loaders);
      freemarkerConfiguration.setTemplateLoader(mtl);
      freemarkerConfiguration.setObjectWrapper(new DefaultObjectWrapper());
      context.setAttribute(Constants.FREEMARKER_CONFIGURATION, freemarkerConfiguration);
    } catch (Exception e) {
      LOG.error("freemarker error", e);
    }
  }

  private void performUpgrade(ServletContext context) {
    Connection db = null;
    ConnectionPool commonCP = (ConnectionPool) context.getAttribute(Constants.CONNECTION_POOL);
    try {
      LOG.info("Upgrading the database...");

      // Determine the database connection to use
      ConnectionElement ce = new ConnectionElement();
      ce.setDriver(this.get(CONNECTION_DRIVER));
      ce.setUrl(this.get(CONNECTION_URL));
      ce.setUsername(this.get(CONNECTION_USER));
      ce.setPassword(this.get(CONNECTION_PASSWORD));

      // Retrieve a database connection
      db = commonCP.getConnection(ce, true);

      // Perform the upgrade
      UpgradeUtils.performUpgrade(db, context);

      // Persist the new version info
      save();

    } catch (Exception e) {
      LOG.error("performUpgrade", e);
      e.printStackTrace(System.out);
    } finally {
      commonCP.free(db);
    }
  }

  /**
   * Description of the Method
   *
   * @param filename Description of the Parameter
   * @return Description of the Return Value
   */
  public boolean save(String filename) {
    try {
      BufferedWriter out = new BufferedWriter(new FileWriter(filename));
      add("VERSION", ApplicationVersion.VERSION);
      add("APP_VERSION", ApplicationVersion.APP_VERSION);
      add("DB_VERSION", ApplicationVersion.DB_VERSION);
      for (String param : prefs.keySet()) {
        String value = prefs.get(param);
        if (param.startsWith("#")) {
          out.write(value + ls);
        } else {
          out.write(param + "=" + value + ls);
        }
      }
      out.close();
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * Saves the properties to where they were loaded from
   *
   * @return Description of the Return Value
   */
  public boolean save() {
    if (this.has(FILE_LIBRARY_PATH)) {
      return save(this.get(FILE_LIBRARY_PATH) + "build.properties");
    }
    return false;
  }

  /**
   * Adds a feature to the Parameter attribute of the ApplicationPrefs object
   *
   * @param context The feature to be added to the Parameter attribute
   * @param param   The feature to be added to the Parameter attribute
   * @param value   The feature to be added to the Parameter attribute
   */
  private void addParameter(ServletContext context, String param, String value) {
    addParameter(context, param, value, null);
  }

  /**
   * Adds a feature to the Parameter attribute of the ApplicationPrefs object
   *
   * @param context      The feature to be added to the Parameter attribute
   * @param param        The feature to be added to the Parameter attribute
   * @param value        The feature to be added to the Parameter attribute
   * @param defaultValue The feature to be added to the Parameter attribute
   */
  private void addParameter(ServletContext context, String param, String value, String defaultValue) {
    if (value != null) {
      context.setAttribute(param, value);
    } else {
      if (defaultValue != null) {
        context.setAttribute(param, defaultValue);
      } else {
        context.removeAttribute(param);
      }
    }
  }

  /**
   * Save a name/value pair to the Java Preferences store
   *
   * @param instanceName        Description of the Parameter
   * @param fileLibraryLocation Description of the Parameter
   * @return Description of the Return Value
   */
  public static boolean saveFileLibraryLocation(String instanceName, String fileLibraryLocation) {
    try {
      Preferences javaPrefs = Preferences.userNodeForPackage(ApplicationPrefs.class);
      if (instanceName.length() <= Preferences.MAX_KEY_LENGTH) {
        javaPrefs.put(instanceName, fileLibraryLocation);
      } else {
        javaPrefs.put(instanceName.substring(instanceName.length() - Preferences.MAX_KEY_LENGTH), fileLibraryLocation);
      }
      javaPrefs.flush();
      return true;
    } catch (Exception e) {
      LOG.error("saveFileLibraryLocation", e);
      e.printStackTrace(System.out);
      return false;
    }
  }

  /**
   * Gets the configured attribute of the ApplicationPrefs object
   *
   * @return The configured value
   */
  public boolean isConfigured() {
    return (this.has(FILE_LIBRARY_PATH) && !this.has("CONFIGURING"));
  }

  /**
   * Gets the realPath attribute of the ApplicationPrefs class
   *
   * @param context Description of the Parameter
   * @return The realPath value
   */
  public static String getRealPath(ServletContext context) {
    String dir = context.getRealPath("/");
    if (dir != null && !dir.endsWith(fs)) {
      dir += fs;
    }
    return dir;
  }

  /**
   * Gets the war attribute of the ApplicationPrefs class
   *
   * @param context Description of the Parameter
   * @return The war value
   */
  public static boolean isWar(ServletContext context) {
    String realPath = context.getRealPath("/");
    String fs = System.getProperty("file.separator");
    if (realPath == null) {
      return true;
    } else if (realPath.endsWith(".war")) {
      return true;
    } else if (realPath.endsWith(".war" + fs)) {
      return true;
    } else if (realPath.indexOf("Instance") > -1) {
      return true;
    } else {
      return false;
    }
  }

  public void loadApplicationDictionaries(ServletContext context) {
    // Load the default
    String language = this.get(LANGUAGE);
    if (language == null) {
      language = "en_US";
    }
    addDictionary(context, language);

    // Check for additional languages
    String languages = this.get(LANGUAGES_SUPPORTED);
    if (languages == null) {
      return;
    }

    // Load additional languages
    if (languages.indexOf(",") > -1) {
      StringTokenizer tokenizer = new StringTokenizer(languages, ",");
      while (tokenizer.hasMoreTokens()) {
        String langToken = tokenizer.nextToken();
        addDictionary(context, langToken);
      }
    } else {
      if (!languages.equals(language)) {
        addDictionary(context, languages);
      }
    }
  }

  public synchronized void addDictionary(ServletContext context, String language) {
    if (language == null) {
      LOG.error("addDictionary: language cannot be null");
    } else {
      if (!dictionaries.containsKey(language)) {
        LOG.info("Loading dictionary: " + language);
        try {
          // Create a dictionary with the default language
          String languagePath = "/WEB-INF/languages/";
          Dictionary dictionary = new Dictionary(context, languagePath, "en_US");
          if (!"en_US".equals(language)) {
            // Override the text with a selected language
            dictionary.load(context, languagePath, language);
          }
          dictionaries.put(language, dictionary);
        } catch (Exception e) {
          LOG.error("Language loading error (file exists?): " + e.getMessage(), e);
        }
      }
    }
  }

  public String getValue(String section,
                         String parameter,
                         String tagName,
                         String language) {
    if (null == dictionaries) {
      return null;
    }

    final Dictionary dictionary = dictionaries.get(language);
    if (null == dictionary) {
      return null;
    }

    Map prefGroup = (Map) dictionary.getLocalizationPrefs().get(section);
    if (null != prefGroup) {
      Node param = (Node) prefGroup.get(parameter);
      if (null != param) {
        return XMLUtils.getNodeText(
            XMLUtils.getFirstChild((Element) param, tagName));
      }
    }
    return null;
  }

  public String getLabel(String parameter, String language) {
    return getLabel("system.fields.label", parameter, language);
  }

  public String getLabel(String section, String parameter, String language) {
    return getValue(section, parameter, "value", language);
  }

  public static ApplicationPrefs getApplicationPrefs(ServletContext context) {
    return (ApplicationPrefs) context.getAttribute(Constants.APPLICATION_PREFS);
  }

  public static Configuration getFreemarkerConfiguration(ServletContext context) {
    return (Configuration) context.getAttribute(Constants.FREEMARKER_CONFIGURATION);
  }
}
