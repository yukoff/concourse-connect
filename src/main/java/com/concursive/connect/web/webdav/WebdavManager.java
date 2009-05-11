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

package com.concursive.connect.web.webdav;

import com.concursive.commons.codec.PasswordHash;
import com.concursive.commons.db.DatabaseUtils;
import com.concursive.connect.web.webdav.beans.WebdavUser;
import com.concursive.connect.web.webdav.context.BaseWebdavContext;
import com.concursive.connect.web.webdav.context.ModuleContext;
import com.concursive.connect.web.webdav.dao.WebdavModule;
import com.concursive.connect.web.webdav.dao.WebdavModuleList;
import com.concursive.connect.web.webdav.servlets.WebdavServlet;
import org.apache.catalina.servlets.DefaultServlet;
import org.apache.catalina.util.MD5Encoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;

/**
 * Description of the Class
 *
 * @author ananth
 * @version $Id$
 * @created November 2, 2004
 */
public class WebdavManager {

  protected static MessageDigest md5Helper = null;

  private String fileLibraryPath = null;
  private boolean modulesBuilt = false;
  //User List cache
  private HashMap users = new HashMap();
  //Webdav Module cache
  private WebdavModuleList moduleList = new WebdavModuleList();
  private java.sql.Timestamp creationDate = new java.sql.Timestamp(System.currentTimeMillis());


  /**
   * Sets the fileLibraryPath attribute of the WebdavManager object
   *
   * @param tmp The new fileLibraryPath value
   */
  public void setFileLibraryPath(String tmp) {
    this.fileLibraryPath = tmp;
  }


  /**
   * Sets the modulesBuilt attribute of the WebdavManager object
   *
   * @param tmp The new modulesBuilt value
   */
  public void setModulesBuilt(boolean tmp) {
    this.modulesBuilt = tmp;
  }


  /**
   * Sets the modulesBuilt attribute of the WebdavManager object
   *
   * @param tmp The new modulesBuilt value
   */
  public void setModulesBuilt(String tmp) {
    this.modulesBuilt = DatabaseUtils.parseBoolean(tmp);
  }


  /**
   * Sets the users attribute of the WebdavManager object
   *
   * @param tmp The new users value
   */
  public void setUsers(HashMap tmp) {
    this.users = tmp;
  }


  /**
   * Sets the moduleList attribute of the WebdavManager object
   *
   * @param tmp The new moduleList value
   */
  public void setModuleList(WebdavModuleList tmp) {
    this.moduleList = tmp;
  }


  /**
   * Gets the fileLibraryPath attribute of the WebdavManager object
   *
   * @return The fileLibraryPath value
   */
  public String getFileLibraryPath() {
    return fileLibraryPath;
  }


  /**
   * Gets the modulesBuilt attribute of the WebdavManager object
   *
   * @return The modulesBuilt value
   */
  public boolean getModulesBuilt() {
    return modulesBuilt;
  }


  /**
   * Gets the users attribute of the WebdavManager object
   *
   * @return The users value
   */
  public HashMap getUsers() {
    return users;
  }


  /**
   * Gets the moduleList attribute of the WebdavManager object
   *
   * @return The moduleList value
   */
  public WebdavModuleList getModuleList() {
    return moduleList;
  }


  /**
   * Constructor for the WebdavManager object
   */
  public WebdavManager() {
  }


  /**
   * Constructor for the WebdavManager object
   *
   * @param fileLibraryPath Description of the Parameter
   */
  public WebdavManager(String fileLibraryPath) {
    this.fileLibraryPath = fileLibraryPath;
  }


  /**
   * Description of the Method
   *
   * @param db              Description of the Parameter
   * @param fileLibraryPath Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public void buildModules(Connection db, String fileLibraryPath) throws SQLException {
    if (System.getProperty("DEBUG") != null) {
      System.out.println("WebdavManager-> buildModules");
    }
    this.fileLibraryPath = fileLibraryPath;
    // build the modules
    moduleList.clear();
    moduleList.setFileLibraryPath(fileLibraryPath);
    moduleList.setBuildContext(true);
    moduleList.buildList(db);
    modulesBuilt = true;
  }


  /**
   * used by basic authentication scheme
   *
   * @param db       Description of the Parameter
   * @param username Description of the Parameter
   * @param password Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  public boolean allowUser(Connection db, String username, String password) throws SQLException {
    boolean status = false;
    PreparedStatement pst = db.prepareStatement(
        "SELECT password, expiration, user_id " +
            "FROM users " +
            "WHERE username = ? " +
            "AND enabled = ? ");
    pst.setString(1, username);
    pst.setBoolean(2, true);
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      //TODO: determine if the user account has not expired
      String pw = rs.getString("password");
      if (pw.equals(PasswordHash.encrypt(password))) {
        int userId = rs.getInt("user_id");
        int roleId = -1;
        WebdavUser user = new WebdavUser();
        user.setUserId(userId);
        user.setRoleId(roleId);
        users.put(username.toLowerCase(), user);
        status = true;
      }
    }
    rs.close();
    pst.close();
    return status;
  }


  /**
   * Description of the Method
   *
   * @param db       Description of the Parameter
   * @param username Description of the Parameter
   * @param nonce    The feature to be added to the User attribute
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  public boolean addUser(Connection db, String username, String nonce) throws SQLException {
    boolean status = false;
    PreparedStatement pst = db.prepareStatement(
        "SELECT user_id, webdav_access, webdav_password " +
            "FROM users " +
            "WHERE username = ? " +
            "AND webdav_access = ? " +
            "AND enabled = ? ");
    pst.setString(1, username);
    pst.setBoolean(2, true);
    pst.setBoolean(3, true);
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      //TODO: determine if the user account has not expired
      int userId = rs.getInt("user_id");
      int roleId = -1;
      String digest = rs.getString("webdav_password");
      WebdavUser user = new WebdavUser();
      user.setUserId(userId);
      user.setRoleId(roleId);
      user.setDigest(digest);
      user.setNonce(nonce);
      users.put(username, user);
      status = true;
    }
    rs.close();
    pst.close();
    return status;
  }


  /**
   * Gets the webdavPassword attribute of the WebdavManager object
   *
   * @param db       Description of the Parameter
   * @param username Description of the Parameter
   * @return The webdavPassword value
   * @throws SQLException Description of the Exception
   */
  public static String getWebdavPassword(Connection db, String username) throws SQLException {
    String password = "";
    PreparedStatement pst = db.prepareStatement(
        "SELECT webdav_password " +
            "FROM users " +
            "WHERE username = ? " +
            "AND enabled = ? ");
    pst.setString(1, username);
    pst.setBoolean(2, true);
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      password = rs.getString("webdav_password");
    }
    rs.close();
    pst.close();
    return password;
  }


  /**
   * Description of the Method
   *
   * @param username Description of the Parameter
   * @return Description of the Return Value
   */
  public boolean hasUser(String username) {
    return (users.containsKey(username.toLowerCase()));
  }


  /**
   * Gets the user attribute of the WebdavManager object
   *
   * @param username Description of the Parameter
   * @return The user value
   */
  public WebdavUser getUser(String username) {
    return ((WebdavUser) users.get(username.toLowerCase()));
  }


  /**
   * Description of the Method
   *
   * @param username Description of the Parameter
   */
  public void removeUser(String username) {
    if (hasUser(username)) {
      users.remove(username.toLowerCase());
    }
  }


  /**
   * Iterates through the cached top level webdav modules for this system and
   * determines if the user has permission to view this module. If so the
   * modules list of bindings are populated
   *
   * @param db       Description of the Parameter
   * @param username Description of the Parameter
   * @return The resources value
   * @throws SQLException Description of the Exception
   */
  public ModuleContext getResources(Connection db, String username) throws SQLException {
    WebdavUser user = this.getUser(username);
    BaseWebdavContext context = new BaseWebdavContext(user.getUserId(), fileLibraryPath);
    Iterator i = moduleList.keySet().iterator();
    while (i.hasNext()) {
      String moduleName = (String) i.next();
      WebdavModule module = (WebdavModule) moduleList.get(moduleName);
      String permission = module.getContext().getPermission();
      if (hasPermission(user.getUserId(), permission)) {
        context.getBindings().put(moduleName, module.getContext());
        context.buildProperties(moduleName, module.getEntered(), module.getModified(), new Integer(0));
      }
    }
    //TODO: Remove this hardcoding
    BaseWebdavContext synchronization = new BaseWebdavContext();
    //CalendarContext calendar = new CalendarContext();
    //synchronization.getBindings().put("Calendars", calendar);
    //synchronization.buildProperties("Calendars", creationDate, creationDate, new Integer(0));
    context.getBindings().put("Synchronization", synchronization);
    context.buildProperties("Synchronization", creationDate, creationDate, new Integer(0));
    return context;
  }


  /**
   * Description of the Method
   *
   * @param userId     Description of the Parameter
   * @param permission Description of the Parameter
   * @return Description of the Return Value
   */
  public boolean hasPermission(int userId, String permission) {
    //return thisSystem.hasPermission(userId, permission);
    return true;
  }

  public static int validateUser(Connection db, HttpServletRequest req) throws Exception {
    String argHeader = req.getHeader("Authorization");
    HashMap params = getAuthenticationParams(argHeader);
    String username = (String) params.get("username");

    if (md5Helper == null) {
      md5Helper = MessageDigest.getInstance("MD5");
    }

    int userId = -1;
    String password = null;
    PreparedStatement pst = db.prepareStatement(
        "SELECT user_id, webdav_password " +
            "FROM users " +
            "WHERE username = ? " +
            "AND enabled = ? ");
    pst.setString(1, username);
    pst.setBoolean(2, true);
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      userId = rs.getInt("user_id");
      password = rs.getString("webdav_password");
    }
    rs.close();
    pst.close();
    if (userId == -1) {
      return userId;
    }
    String method = req.getMethod();
    String uri = (String) params.get("uri");
    String a2 = MD5Encoder.encode(md5Helper.digest((method + ":" + uri).getBytes()));
    String digest =
        MD5Encoder.encode(
            md5Helper.digest(
                (password + ":" +
                    params.get("nonce") + ":" +
                    a2).getBytes()));
    if (!digest.equals(params.get("response"))) {
      userId = -1;
    }
    return userId;
  }

  protected static HashMap getAuthenticationParams(String argHeader) {
    HashMap params = new HashMap();
    StringTokenizer st = new StringTokenizer(argHeader, ",");
    while (st.hasMoreTokens()) {
      String token = st.nextToken();
      if (token.startsWith("Digest")) {
        token = token.substring("Digest".length());
      }
      if (token.contains("=") && token.contains("\"")) {
        String param = token.substring(0, token.indexOf("=")).trim();
        String value = token.substring(token.indexOf("\"") + 1, token.lastIndexOf("\""));
        params.put(param, value);
      }
    }
    return params;
  }

  public static boolean checkAuthentication(HttpServletRequest req) throws Exception {
    String argHeader = req.getHeader("Authorization");
    if (argHeader == null || !argHeader.startsWith("Digest")) {
      return false;
    }
    return true;
  }

  public static void askForAuthentication(HttpServletResponse res) throws Exception {
    String nonce = DefaultServlet.generateNonce();
    // determine the 'opaque' value which should be returned as-is by the client
    String opaque = DefaultServlet.generateOpaque();
    res.setHeader(
        "WWW-Authenticate", "Digest realm=\"" + WebdavServlet.USER_REALM + "\", " +
            "nonce=\"" + nonce + "\", " +
            "opaque=\"" + opaque + "\"");
    res.sendError(HttpServletResponse.SC_UNAUTHORIZED);
  }
}

