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

package com.concursive.connect.web.webdav.context;

import com.concursive.connect.cache.utils.CacheUtils;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.login.utils.UserUtils;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import com.concursive.connect.web.utils.LookupList;
import org.apache.naming.resources.ResourceAttributes;

import javax.naming.*;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

/**
 * All module webdav contexts extend this base class. Provides common methods
 * which can be used by all its subclasses
 *
 * @author ananth
 * @version $Id$
 * @created November 3, 2004
 */
public class BaseWebdavContext implements ModuleContext {

  protected final static String fs = System.getProperty("file.separator");
  protected String fileLibraryPath = null;
  // List of bindings for this context
  protected Hashtable bindings = new Hashtable();
  // List of attributes for each binding
  protected Hashtable attributes = new Hashtable();
  private String permission = "";
  private int userId = -1;


  /**
   * Sets the userId attribute of the BaseWebdavContext object
   *
   * @param tmp The new userId value
   */
  public void setUserId(int tmp) {
    this.userId = tmp;
  }


  /**
   * Sets the userId attribute of the BaseWebdavContext object
   *
   * @param tmp The new userId value
   */
  public void setUserId(String tmp) {
    this.userId = Integer.parseInt(tmp);
  }


  /**
   * Gets the userId attribute of the BaseWebdavContext object
   *
   * @return The userId value
   */
  public int getUserId() {
    return userId;
  }


  /**
   * Sets the permission attribute of the BaseWebdavContext object
   *
   * @param tmp The new permission value
   */
  public void setPermission(String tmp) {
    this.permission = tmp;
  }


  /**
   * Gets the permission attribute of the BaseWebdavContext object
   *
   * @return The permission value
   */
  public String getPermission() {
    return permission;
  }


  /**
   * Sets the fileLibraryPath attribute of the BaseWebdavContext object
   *
   * @param tmp The new fileLibraryPath value
   */
  public void setFileLibraryPath(String tmp) {
    this.fileLibraryPath = tmp;
  }


  /**
   * Sets the bindings attribute of the BaseWebdavContext object
   *
   * @param tmp The new bindings value
   */
  public void setBindings(Hashtable tmp) {
    this.bindings = tmp;
  }


  /**
   * Sets the attributes attribute of the BaseWebdavContext object
   *
   * @param tmp The new attributes value
   */
  public void setAttributes(Hashtable tmp) {
    this.attributes = tmp;
  }


  /**
   * Gets the fileLibraryPath attribute of the BaseWebdavContext object
   *
   * @return The fileLibraryPath value
   */
  public String getFileLibraryPath() {
    return fileLibraryPath;
  }


  /**
   * Gets the bindings attribute of the BaseWebdavContext object
   *
   * @return The bindings value
   */
  public Hashtable getBindings() {
    return bindings;
  }


  /**
   * Gets the attributes attribute of the BaseWebdavContext object
   *
   * @return The attributes value
   */
  public Hashtable getAttributes() {
    return attributes;
  }


  /**
   * Constructor for the BaseWebdavContext object
   */
  public BaseWebdavContext() {
  }


  /**
   * Constructor for the BaseWebdavContext object
   *
   * @param fileLibraryPath Description of the Parameter
   * @param userId          Description of the Parameter
   */
  public BaseWebdavContext(int userId, String fileLibraryPath) {
    this.userId = userId;
    this.fileLibraryPath = fileLibraryPath;
  }


  /**
   * Description of the Method
   *
   * @param name Description of the Parameter
   * @return Description of the Return Value
   * @throws NamingException Description of the Exception
   */
  public Object lookup(String name) throws NamingException {
    if ("".equals(name.trim())) {
      return this;
    }
    StringTokenizer st = new StringTokenizer(name, "/");
    Object current = this;
    while (st.hasMoreTokens()) {
      String token = st.nextToken();
      if (current instanceof ModuleContext) {
        current = ((ModuleContext) current).getBindings().get(token);
      }
    }
    if (current == null) {
      throw new NameNotFoundException(name + " NOT FOUND");
    }
    return current;
  }


  /**
   * Description of the Method
   *
   * @param db   Description of the Parameter
   * @param name Description of the Parameter
   * @return Description of the Return Value
   * @throws NamingException       Description of the Exception
   * @throws SQLException          Description of the Exception
   * @throws FileNotFoundException Description of the Exception
   */
  public Object lookup(Connection db, String name) throws NamingException, SQLException, FileNotFoundException {
    if ("".equals(name.trim())) {
      return this;
    }
    StringTokenizer st = new StringTokenizer(name, "/");
    Object current = this;
    while (st.hasMoreTokens()) {
      String token = st.nextToken();
      if (current instanceof ModuleContext) {
        current = ((ModuleContext) current).getBindings().get(token);
        if (current instanceof ItemContext) {
          // An ItemContext gets the path from its parent context
          ((ItemContext) current).buildResources(db);
        } else if (current instanceof FolderContext) {
          // A FolderContext gets the path from its parent context
          ((FolderContext) current).buildResources(db);
        } else if (current instanceof ModuleContext) {
          // A BaseWebdavContext or any other Top Level ModuleContext needs the
          // base fileLibrary path from the webdav manager
          ((ModuleContext) current).buildResources(db, userId, fileLibraryPath);
        }
      }
    }
    if (current == null) {
      throw new NameNotFoundException(name + " not found");
    }
    return current;
  }


  /**
   * A ModuleContext has name-object bindings. It also maintains a hashtable of
   * attributes for each object available in its bindings.
   *
   * @param db   Description of the Parameter
   * @param path Description of the Parameter
   * @return The attributes value
   * @throws NamingException       Description of the Exception
   * @throws SQLException          Description of the Exception
   * @throws FileNotFoundException Description of the Exception
   */
  public ResourceAttributes getAttributes(Connection db, String path)
      throws NamingException, SQLException, FileNotFoundException {
    if ("".equals(path.trim())) {
      return null;
    }
    StringTokenizer st = new StringTokenizer(path, "/");
    Object current = this;
    Object parent = null;
    String token = null;
    while (st.hasMoreTokens()) {
      token = st.nextToken();
      if (current instanceof ModuleContext) {
        parent = current;
        current = ((ModuleContext) current).getBindings().get(token);
        if (current instanceof ItemContext) {
          // An ItemContext gets the path from its parent context
          ((ItemContext) current).buildResources(db);
        } else if (current instanceof FolderContext) {
          // A FolderContext gets the path from its parent context
          ((FolderContext) current).buildResources(db);
        } else if (current instanceof ModuleContext) {
          // A BaseWebdavContext or any other Top Level ModuleContext needs the
          // base fileLibrary path from the webdav manager
          ((ModuleContext) current).buildResources(db, userId, fileLibraryPath);
        }
      }
    }
    if (current == null) {
      System.out.println("naming exception while fetching attrs for token: " + token);
      throw new NameNotFoundException(path + " not found");
    }
    //System.out.println("parent: " + parent + ", token: " + token);
    return (ResourceAttributes) (((ModuleContext) parent).getAttributes()).get(token);
  }


  /**
   * Description of the Method
   *
   * @param name     Description of the Parameter
   * @param entered  Description of the Parameter
   * @param modified Description of the Parameter
   * @param length   Description of the Parameter
   */
  public void buildProperties(String name, Timestamp entered, Timestamp modified, Integer length) {
    ResourceAttributes attrs = new ResourceAttributes();
    attrs.setContentLength(length.longValue());
    attrs.setCreation(entered.getTime());
    attrs.setLastModified(modified.getTime());
    attributes.put(name, attrs);
  }


  /**
   * Description of the Method
   *
   * @param db              Description of the Parameter
   * @param fileLibraryPath Description of the Parameter
   * @param userId          Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public void buildResources(Connection db, int userId, String fileLibraryPath) throws SQLException {
    this.userId = userId;
    this.fileLibraryPath = fileLibraryPath;
  }


  /**
   * Description of the Method
   *
   * @param name Description of the Parameter
   * @return Description of the Return Value
   * @throws NamingException Description of the Exception
   */
  public NamingEnumeration list(String name) throws NamingException {
    if ("".equals(name.trim())) {
      return new ListOfNames(bindings.keys());
    }
    try {
      Object target = lookup(name);
      if (target instanceof ModuleContext) {
        return ((ModuleContext) target).list("");
      }
    } catch (NameNotFoundException e) {
      // do nothing
    }
    return new ListOfNames(bindings.keys());
    //throw new NotContextException(name + " cannot be listed");
  }


  /**
   * Description of the Method
   *
   * @param db         Description of the Parameter
   * @param linkItemId Description of the Parameter
   * @param userId     Description of the Parameter
   * @param permission Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  public boolean hasPermission(Connection db, int linkItemId, int userId, String permission)
      throws SQLException {
    if (permission.startsWith("project")) {
      return (hasProjectAccess(db, linkItemId, userId, permission));
    } else {
      return (userId > 0);
    }
  }


  /**
   * Gets the userLevel attribute of the BaseWebdavContext object
   *
   * @param roleLevel Description of the Parameter
   * @return The userLevel value
   * @throws SQLException Description of the Exception
   */
  protected int getUserLevel(int roleLevel) throws SQLException {
    LookupList roleList = CacheUtils.getLookupList("lookup_project_role");
    return roleList.getIdFromLevel(roleLevel);
  }


  /**
   * Gets the roleId attribute of the BaseWebdavContext object
   *
   * @param userlevel Description of the Parameter
   * @return The roleId value
   * @throws SQLException Description of the Exception
   */
  protected int getRoleId(int userlevel) throws SQLException {
    LookupList roleList = CacheUtils.getLookupList("lookup_project_role");
    return roleList.getLevelFromId(userlevel);
  }


  /**
   * Description of the Method
   *
   * @param db         Description of the Parameter
   * @param projectId  Description of the Parameter
   * @param userId     Description of the Parameter
   * @param permission Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  protected boolean hasProjectAccess(Connection db, int projectId, int userId, String permission) throws SQLException {
    // Load the user (will not be found if a guest)
    User thisUser = null;
    try {
      thisUser = UserUtils.loadUser(userId);
    } catch (Exception notAUser) {
      thisUser = UserUtils.createGuestUser();
    }
    return ProjectUtils.hasAccess(projectId, thisUser, permission);
  }


  /**
   * Description of the Class
   *
   * @author ananth
   * @version $Id$
   * @created November 5, 2004
   */
  class ListOfNames implements NamingEnumeration {
    protected Enumeration names;


    /**
     * Constructor for the ListOfNames object
     *
     * @param names Description of the Parameter
     */
    ListOfNames(Enumeration names) {
      this.names = names;
    }


    /**
     * Description of the Method
     *
     * @return Description of the Return Value
     */
    public boolean hasMoreElements() {
      try {
        return hasMore();
      } catch (NamingException e) {
        return false;
      }
    }


    /**
     * Description of the Method
     *
     * @return Description of the Return Value
     * @throws NamingException Description of the Exception
     */
    public boolean hasMore() throws NamingException {
      return names.hasMoreElements();
    }


    /**
     * Description of the Method
     *
     * @return Description of the Return Value
     * @throws NamingException Description of the Exception
     */
    public Object next() throws NamingException {
      String name = (String) names.nextElement();
      String className = bindings.get(name).getClass().getName();
      return new NameClassPair(name, className);
    }


    /**
     * Description of the Method
     *
     * @return Description of the Return Value
     */
    public Object nextElement() {
      try {
        return next();
      } catch (NamingException e) {
        throw new NoSuchElementException(e.toString());
      }
    }


    /**
     * Description of the Method
     */
    public void close() {
    }
  }

  // Class for enumerating bindings

  /**
   * Description of the Class
   *
   * @author ananth
   * @version $Id$
   * @created November 5, 2004
   */
  class ListOfBindings extends ListOfNames {

    /**
     * Constructor for the ListOfBindings object
     *
     * @param names Description of the Parameter
     */
    ListOfBindings(Enumeration names) {
      super(names);
    }


    /**
     * Description of the Method
     *
     * @return Description of the Return Value
     * @throws NamingException Description of the Exception
     */
    public Object next() throws NamingException {
      String name = (String) names.nextElement();
      return new Binding(name, bindings.get(name));
    }
  }
}

