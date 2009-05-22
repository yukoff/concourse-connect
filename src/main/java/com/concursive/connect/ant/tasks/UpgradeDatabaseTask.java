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

package com.concursive.connect.ant.tasks;

import bsh.Interpreter;
import com.concursive.commons.db.ConnectionElement;
import com.concursive.commons.db.ConnectionPool;
import com.concursive.commons.db.DatabaseUtils;
import com.concursive.commons.text.StringUtils;
import com.concursive.connect.web.modules.upgrade.utils.UpgradeUtils;
import com.concursive.connect.config.ApplicationPrefs;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Ant task to upgrade the database
 *
 * @author matt rajkowski
 * @version $Id$
 * @created August 26, 2003
 */
public class UpgradeDatabaseTask extends Task {

  /**
   * Description of the Field
   */
  public final static String fs = System.getProperty("file.separator");
  private String driver = null;
  private String url = null;
  private String user = null;
  private String password = null;
  private String baseFilePath = null;
  private String baseFile = null;
  private String servletJar = null;
  private String libPath = null;
  private String fileLibrary = null;
  private String specificDatabase = null;
  private ApplicationPrefs prefs = new ApplicationPrefs();


  /**
   * Sets the driver attribute of the UpgradeDatabaseTask object
   *
   * @param tmp The new driver value
   */
  public void setDriver(String tmp) {
    this.driver = tmp;
  }


  /**
   * Sets the url attribute of the UpgradeDatabaseTask object
   *
   * @param tmp The new url value
   */
  public void setUrl(String tmp) {
    this.url = tmp;
  }


  /**
   * Sets the user attribute of the UpgradeDatabaseTask object
   *
   * @param tmp The new user value
   */
  public void setUser(String tmp) {
    this.user = tmp;
  }


  /**
   * Sets the password attribute of the UpgradeDatabaseTask object
   *
   * @param tmp The new password value
   */
  public void setPassword(String tmp) {
    this.password = tmp;
  }


  public void setBaseFilePath(String baseFilePath) {
    this.baseFilePath = baseFilePath;
  }


  /**
   * Sets the source attribute of the UpgradeDatabaseTask object
   *
   * @param tmp The new source value
   */
  public void setBaseFile(String tmp) {
    this.baseFile = tmp;
  }


  /**
   * Sets the servletJar attribute of the UpgradeDatabaseTask object
   *
   * @param tmp The new servletJar value
   */
  public void setServletJar(String tmp) {
    this.servletJar = tmp;
  }

  public void setLibPath(String libPath) {
    this.libPath = libPath;
  }

  /**
   * Sets the fileLibrary attribute of the UpgradeDatabaseTask object
   *
   * @param tmp The new fileLibrary value
   */
  public void setFileLibrary(String tmp) {
    this.fileLibrary = tmp;
  }


  /**
   * Sets the specificDatabase attribute of the UpgradeDatabaseTask object
   *
   * @param tmp The new specificDatabase value
   */
  public void setSpecificDatabase(String tmp) {
    this.specificDatabase = tmp;
  }


  /**
   * This method is called by Ant when the upgradeDatabaseTask is used
   *
   * @throws BuildException Description of the Exception
   */
  public void execute() throws BuildException {
    String fsEval = System.getProperty("file.separator");
    if ("\\".equals(fsEval)) {
      fsEval = "\\\\";
      servletJar = StringUtils.replace(servletJar, "\\", "\\\\");
      fileLibrary = StringUtils.replace(fileLibrary, "\\", "\\\\");
    }
    System.out.println("Beginning database task...");
    try {
      // Load the application prefs
      prefs.loadProperties(fileLibrary);
      //Create a Connection Pool to facilitate connections
      ConnectionPool sqlDriver = new ConnectionPool();
      sqlDriver.setDebug(true);
      sqlDriver.setTestConnections(false);
      sqlDriver.setAllowShrinking(true);
      sqlDriver.setMaxConnections(10);
      sqlDriver.setMaxIdleTime(60000);
      sqlDriver.setMaxDeadTime(300000);
      //Cache a list of databases to upgrade
      ArrayList<HashMap> siteList = new ArrayList<HashMap>();
      //Prepare the database
      ConnectionElement ce = new ConnectionElement(url, user, password);
      ce.setDriver(driver);
      //Set the database to update
      if (1 == 1) {
        HashMap<String, String> siteInfo = new HashMap<String, String>();
        siteInfo.put("url", url);
        siteInfo.put("dbName", specificDatabase);
        siteInfo.put("user", user);
        siteInfo.put("password", password);
        siteInfo.put("driver", driver);
        if ((specificDatabase == null || "".equals(specificDatabase)) ||
            (specificDatabase.equals(siteInfo.get("dbName")))) {
          siteList.add(siteInfo);
        }
      }
      //Iterate over the databases to upgrade and run the correct
      //sql code and bean shell scripts
      Iterator i = siteList.iterator();
      while (i.hasNext()) {
        HashMap siteInfo = (HashMap) i.next();
        ce = new ConnectionElement(
            (String) siteInfo.get("url"),
            (String) siteInfo.get("user"),
            (String) siteInfo.get("password"));
        ce.setDriver((String) siteInfo.get("driver"));
        System.out.println("");

        if (baseFilePath == null) {
          baseFilePath = "";
        }

        String thisDbName = (String) siteInfo.get("dbName");

        // Connect to the database
        Connection db = sqlDriver.getConnection(ce, true);

        // Run any scripts
        if ("all".equals(baseFile)) {
          // Look for scripts to run
          InputStream is = new File("src/main/webapp/WEB-INF/database/database_versions.txt").toURL().openStream();
          ArrayList<String> versionList = UpgradeUtils.retrieveDatabaseVersions(is);
          for (String version : versionList) {
            if (!UpgradeUtils.isInstalled(db, version)) {
              System.out.println("Executing database version: " + version);
              executeTxtFile(db, version + ".txt", fsEval, thisDbName);
              UpgradeUtils.addVersion(db, version);
            }
          }
        } else if (baseFile.indexOf(".txt") > -1) {
          // try a single .txt
          executeTxtFile(db, baseFile, fsEval, thisDbName);
        } else {
          // assume it is a .bsh or .sql
          executeFile(db, baseFile, fsEval, thisDbName);
        }
        sqlDriver.free(db);
      }
      // Close the connections
      sqlDriver.closeAllConnections();
    } catch (Exception e) {
      throw new BuildException("Script Error: " + e.getMessage());
    }
  }

  private void executeTxtFile(Connection db, String baseFile, String fsEval, String dbName) throws Exception {
    //Connection db, String path, String thisScriptFile, String fsEval, String dbName
    ArrayList<String> files = new ArrayList<String>();
    if (baseFile.startsWith("upgrade_")) {
      baseFile = baseFile.substring(8);
    }
    // Try the coded path
    String yearString = (new File(baseFile)).getName().substring(0, 4);
    String upgradeFile = baseFilePath + "upgrade" + fs + yearString + fs + "upgrade_" + baseFile;
    File testFile = new File(upgradeFile);
    if (testFile.exists()) {
      StringUtils.loadText(upgradeFile, files, true);
    } else {
      // Try the specified file
      StringUtils.loadText(baseFile, files, true);
    }
    System.out.println("Scripts to process: " + files.size());

    for (String thisFile : files) {
      executeFile(db, thisFile, fsEval, dbName);
    }
  }

  private void executeFile(Connection db, String thisFile, String fsEval, String dbName) throws Exception {
    if (thisFile.endsWith(".bsh")) {
      // Try to run a specified bean shell script if found
      executeScript(db, thisFile, fsEval, dbName);
    } else if (thisFile.endsWith(".sql")) {
      // Try to run the specified sql file
      executeSql(db, baseFilePath, thisFile);
    } else {
      throw new Exception("UpgradeDatabaseTask-> File type not understood: " + thisFile);
    }
  }

  /**
   * Executes the specified BeanShell script on the given database connection
   *
   * @param db             Description of the Parameter
   * @param thisScriptFile Description of the Parameter
   * @param fsEval         Description of the Parameter
   * @param dbName         Description of the Parameter
   * @throws Exception Description of the Exception
   */
  private void executeScript(Connection db, String thisScriptFile, String fsEval, String dbName) throws Exception {
    String scriptFile = thisScriptFile;
    if (scriptFile.endsWith(".bsh")) {
      if (!(new File(scriptFile).exists())) {
        // look for script stored in a common directory
        String yearString = (new File(scriptFile)).getName().substring(0, 4);
        scriptFile = baseFilePath + "common" + fs + yearString + fs + (new File(scriptFile)).getName();
      }
      if (!(new File(scriptFile).exists())) {
        throw new Exception("BSH FILE NOT FOUND: " + thisScriptFile);
      }
      // Setup the classpath and environment
      System.out.println("\nSetting up BSH environment...");
      Interpreter script = new Interpreter();

      // Dynamically add all libraries
      if (libPath != null) {
        File directory = new File(libPath);
        if (directory.isDirectory()) {
          System.out.println("\nAdding libraries from... " + libPath);
          String[] libraries = directory.list();
          for (String library : libraries) {
            if (library.endsWith(".jar")) {
              script.eval("addClassPath(bsh.cwd + \"" + fsEval + "lib" + fsEval + library + "\")");
            }
          }
        }
      }
      script.eval("addClassPath(bsh.cwd + \"" + fsEval + "src" + fsEval + "main" + fsEval + "resources\")");
      script.eval("addClassPath(\"" + servletJar + "\")");
      script.set("prefs", prefs);
      script.set("db", db);
      script.set("fileLibraryPath", fileLibrary);

      // Some classes use the cache so make it available during upgrades
      System.out.println("\nAdding caches...");
      script.eval("import com.concursive.connect.cache.CacheContext;");
      script.eval("import com.concursive.connect.cache.Caches;");
      script.eval("import net.sf.ehcache.CacheManager;");
      script.eval("CacheManager.create();");
      script.eval("CacheContext cacheContext = new CacheContext();");
      script.eval("cacheContext.setUpgradeConnection(db);");
      script.eval("Caches.addCaches(cacheContext);");

      // Execute the script
      System.out.println("\nExecuting: " + thisScriptFile);
      script.source(scriptFile);
      System.out.println("");
      // Cleanup...
      System.out.println("\nCleaning up BSH environment...");
      script.eval("CacheManager.getInstance().shutdown();");
    }
  }


  /**
   * Executes the specified sql file on the given database connection
   *
   * @param db          Description of the Parameter
   * @param path        Description of the Parameter
   * @param sqlFileName Description of the Parameter
   * @throws Exception Description of the Exception
   */
  private void executeSql(Connection db, String path, String sqlFileName) throws Exception {
    String dbType = null;
    switch (DatabaseUtils.getType(db)) {
      case DatabaseUtils.POSTGRESQL:
        dbType = "postgresql";
        break;
      case DatabaseUtils.MSSQL:
        dbType = "mssql";
        break;
      default:
        throw new Exception("Upgrade-> * Database could not be determined: " + DatabaseUtils.getType(db));
    }
    String yearString = sqlFileName.substring(0, 4);
    String sqlFile = path + dbType + fs + "upgrade" + fs + yearString + fs + sqlFileName;
    if (sqlFile.endsWith(".sql")) {
      if (!(new File(sqlFile).exists())) {
        throw new Exception("SQL FILE NOT FOUND: " + sqlFile);
      }
      System.out.println("\nExecuting: " + sqlFile);
      try {
        db.setAutoCommit(false);
        Statement st = db.createStatement();
        st.execute(StringUtils.loadText(sqlFile));
        st.close();
        db.commit();
        System.out.println("");
      } catch (SQLException sq) {
        db.rollback();
        System.out.println("     SQL ERROR: " + sq.getMessage());
        throw new Exception(sq);
      } finally {
        db.setAutoCommit(true);
      }
    }
  }
}
