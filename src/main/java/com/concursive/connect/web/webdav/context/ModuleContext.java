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

import org.apache.naming.resources.ResourceAttributes;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Hashtable;

/**
 * Description of the Interface
 *
 * @author ananth
 * @version $Id$
 * @created November 3, 2004
 */
public interface ModuleContext {

  /**
   * Description of the Method
   *
   * @param db              Description of the Parameter
   * @param fileLibraryPath Description of the Parameter
   * @param userId          Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public void buildResources(Connection db, int userId, String fileLibraryPath) throws SQLException;


  /**
   * Description of the Method
   *
   * @param name Description of the Parameter
   * @return Description of the Return Value
   * @throws NamingException Description of the Exception
   */
  public Object lookup(String name) throws NamingException;


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
  public Object lookup(Connection db, String name) throws NamingException, SQLException, FileNotFoundException;


  /**
   * Gets the bindings attribute of the ModuleContext object
   *
   * @return The bindings value
   */
  public Hashtable getBindings();


  /**
   * Gets the attributes attribute of the ModuleContext object
   *
   * @return The attributes value
   */
  public Hashtable getAttributes();


  /**
   * Gets the attributes attribute of the ModuleContext object
   *
   * @param path Description of the Parameter
   * @param db   Description of the Parameter
   * @return The attributes value
   * @throws NamingException       Description of the Exception
   * @throws SQLException          Description of the Exception
   * @throws FileNotFoundException Description of the Exception
   */
  public ResourceAttributes getAttributes(Connection db, String path)
      throws NamingException, SQLException, FileNotFoundException;


  /**
   * Description of the Method
   *
   * @param name Description of the Parameter
   * @return Description of the Return Value
   * @throws NamingException Description of the Exception
   */
  public NamingEnumeration list(String name) throws NamingException;


  /**
   * Gets the permission attribute of the ModuleContext object
   *
   * @return The permission value
   */
  public String getPermission();
}

