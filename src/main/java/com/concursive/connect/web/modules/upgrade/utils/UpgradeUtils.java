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

package com.concursive.connect.web.modules.upgrade.utils;

import bsh.Interpreter;
import com.concursive.commons.db.DatabaseUtils;
import com.concursive.commons.text.StringUtils;
import com.concursive.connect.config.ApplicationPrefs;
import com.concursive.connect.Constants;

import javax.servlet.ServletContext;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Utilities for working with upgrade scripts
 *
 * @author matt rajkowski
 * @created February 6, 2009
 */
public class UpgradeUtils {

  private static Log LOG = LogFactory.getLog(UpgradeUtils.class);

  /**
   * Iterates through and executes available database upgrade scripts
   *
   * @param db
   * @param context
   * @return
   * @throws Exception
   */
  public static ArrayList<String> performUpgrade(Connection db, ServletContext context) throws Exception {
    ArrayList<String> installLog = new ArrayList<String>();
    // Load the database versions to check...
    ArrayList<String> versionList = UpgradeUtils.retrieveDatabaseVersions(context.getResourceAsStream("/WEB-INF/database/database_versions.txt"));
    for (String version : versionList) {
      if (version.length() == 10) {
        install(context, db, version, installLog);
      }
    }
    return installLog;
  }

  /**
   * Loads the list of available database upgrade scripts
   *
   * @param is
   * @return
   * @throws Exception
   */
  public static ArrayList<String> retrieveDatabaseVersions(InputStream is) throws Exception {
    // The versions are stored in a file
    ArrayList<String> versionList = new ArrayList<String>();
    StringUtils.loadText(is, versionList, true);
    return versionList;
  }

  /**
   * Queries the database to see if the script has already been executed
   *
   * @param db      Description of the Parameter
   * @param version Description of the Parameter
   * @return The installed value
   * @throws java.sql.SQLException Description of the Exception
   */
  public static boolean isInstalled(Connection db, String version) throws SQLException {
    boolean isInstalled = false;
    // Query the installed version
    PreparedStatement pst = db.prepareStatement(
        "SELECT script_version " +
            "FROM database_version " +
            "WHERE script_version = ? ");
    pst.setString(1, version);
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      isInstalled = true;
    }
    rs.close();
    pst.close();
    return isInstalled;
  }

  /**
   * Records a database version as being executed
   *
   * @param db      The feature to be added to the Version attribute
   * @param version The feature to be added to the Version attribute
   * @throws SQLException Description of the Exception
   */
  public static void addVersion(Connection db, String version) throws SQLException {
    // Add the specified version
    PreparedStatement pst = db.prepareStatement(
        "INSERT INTO database_version " +
            "(script_filename, script_version) VALUES (?, ?) ");
    pst.setString(1, DatabaseUtils.getTypeName(db) + "_" + version);
    pst.setString(2, version);
    pst.execute();
    pst.close();
  }

  /**
   * Processes the upgrade script if it is not already installed
   *
   * @param context
   * @param db
   * @param versionToInstall
   * @param installLog
   * @throws Exception
   */
  private static void install(ServletContext context, Connection db, String versionToInstall, ArrayList<String> installLog) throws Exception {
    if (!isInstalled(db, versionToInstall)) {
      upgradeTXT(context, db, versionToInstall + ".txt");
      installLog.add(versionToInstall + " database changes installed");
      addVersion(db, versionToInstall);
    }
  }

  /**
   * Processes the filenames listed in the specified .txt file and executes either
   * BSH or SQL
   *
   * @param context  the web context to use for finding the file resource
   * @param db       the database connection to use for executing the script against
   * @param baseName The resource filename
   * @throws SQLException Description of the Exception
   */
  private static void upgradeTXT(ServletContext context, Connection db, String baseName) throws Exception {
    String yearString = baseName.substring(0, 4) + "/";
    ArrayList<String> files = new ArrayList<String>();
    StringUtils.loadText(
        context.getResourceAsStream("/WEB-INF/database/upgrade/" + yearString + "upgrade_" + baseName),
        files, true);
    LOG.info("Scripts to process: " + files.size());
    for (String thisFile : files) {
      if (thisFile.endsWith(".bsh")) {
        // Try to run a specified bean shell script if found
        upgradeBSH(context, db, thisFile);
      } else if (thisFile.endsWith(".sql")) {
        // Try to run the specified sql file
        upgradeSQL(context, db, thisFile);
      }
    }
  }

  /**
   * Executes SQL scripts
   *
   * @param context  the web context to use for finding the file resource
   * @param db       the database connection to use for executing the script against
   * @param baseName The resource filename
   * @throws SQLException Description of the Exception
   */
  private static void upgradeSQL(ServletContext context, Connection db, String baseName) throws Exception {
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
    String pathString = baseName.substring(0, 4);
    LOG.info("Executing " + dbType + " script: " + baseName);
    DatabaseUtils.executeSQL(db, context.getResourceAsStream(
        "/WEB-INF/database/" + dbType + "/upgrade/" + pathString + "/" + baseName), true);
  }


  /**
   * Executes BSH scripts
   *
   * @param context    the web context to use for finding the file resource
   * @param db         the database connection to use for executing the script against
   * @param scriptName The resource filename
   * @throws Exception Description of the Exception
   */
  private static void upgradeBSH(ServletContext context, Connection db, String scriptName) throws Exception {
    LOG.info("Executing BeanShell script " + scriptName);
    // Prepare bean shell script, if needed
    Interpreter script = new Interpreter();
    script.set("db", db);
    // Add the ApplicationPrefs...
    ApplicationPrefs prefs = (ApplicationPrefs) context.getAttribute(Constants.APPLICATION_PREFS);
    script.set("prefs", prefs);
    // Read the script
    String pathString = scriptName.substring(0, 4);
    String setupPath = "/WEB-INF/database/common/" + pathString + "/";
    InputStream source = context.getResourceAsStream(setupPath + scriptName);
    BufferedReader in = new BufferedReader(new InputStreamReader(source));
    // Execute the script
    script.eval(in);
    in.close();
  }

}