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
package com.concursive.connect.web.modules.api.beans;

import com.concursive.commons.db.ConnectionElement;
import com.concursive.commons.db.ConnectionPool;
import com.concursive.commons.web.mvc.actions.ActionContext;
import com.concursive.commons.workflow.ObjectHookManager;
import com.concursive.commons.workflow.WorkflowManager;
import com.concursive.connect.config.ApplicationPrefs;
import com.concursive.connect.web.modules.api.dao.SyncTable;
import org.quartz.Scheduler;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.HashMap;

/**
 * When a service request is initiated, this context is
 * created to pass information throughout the service transactions.
 *
 * @author matt rajkowski
 * @version $Id$
 * @created November 11, 2002
 */
public class PacketContext {

  public static final int RETURN_DEFAULT = -1;
  public static final int RETURN_DATARECORDS = 1;

  private int returnType = RETURN_DEFAULT;
  private HashMap<String, SyncTable> objectMap = null;
  private ObjectHookManager objectHookManager = null;
  private WorkflowManager workflowManager = null;
  private Scheduler scheduler = null;
  private ActionContext actionContext = null;
  private String baseFilePath = null;
  private ConnectionPool connectionPool = null;
  private ConnectionElement connectionElement = null;
  private ApplicationPrefs applicationPrefs = null;
  private HttpServletResponse response = null;
  private OutputStream outputStream = null;

  /**
   * Constructor for the PacketContext object
   */
  public PacketContext() {
  }

  public int getReturnType() {
    return returnType;
  }

  public void setReturnType(int returnType) {
    this.returnType = returnType;
  }

  /**
   * Sets the objectMap attribute of the PacketContext object
   *
   * @param tmp The new objectMap value
   */
  public void setObjectMap(HashMap<String, SyncTable> tmp) {
    this.objectMap = tmp;
  }

  /**
   * Sets the objectHookManager attribute of the PacketContext object
   *
   * @param tmp The new objectHookManager value
   */
  public void setObjectHookManager(ObjectHookManager tmp) {
    this.objectHookManager = tmp;
  }

  /**
   * Sets the workflowManager attribute of the PacketContext object
   *
   * @param tmp The new workflowManager value
   */
  public void setWorkflowManager(WorkflowManager tmp) {
    this.workflowManager = tmp;
  }

  /**
   * Sets the actionContext attribute of the PacketContext object
   *
   * @param tmp The new actionContext value
   */
  public void setActionContext(ActionContext tmp) {
    this.actionContext = tmp;
  }

  /**
   * Gets the objectMap attribute of the PacketContext object
   *
   * @return The objectMap value
   */
  public HashMap<String, SyncTable> getObjectMap() {
    return objectMap;
  }

  /**
   * Gets the objectHookManager attribute of the PacketContext object
   *
   * @return The objectHookManager value
   */
  public ObjectHookManager getObjectHookManager() {
    return objectHookManager;
  }

  /**
   * Gets the workflowManager attribute of the PacketContext object
   *
   * @return The workflowManager value
   */
  public WorkflowManager getWorkflowManager() {
    return workflowManager;
  }

  /**
   * Gets the actionContext attribute of the PacketContext object
   *
   * @return The actionContext value
   */
  public ActionContext getActionContext() {
    return actionContext;
  }

  /**
   * Gets the baseFilePath attribute of the PacketContext object
   *
   * @return The baseFileString value
   */
  public String getBaseFilePath() {
    return baseFilePath;
  }

  /**
   * sets the baseFilePath attribute of the PacketContext object
   *
   * @param tmp The new baseFilePath value
   */
  public void setBaseFilePath(String tmp) {
    this.baseFilePath = tmp;
  }

  /**
   * Gets the applicationPrefs attribute of the PacketContext object
   *
   * @return The applicationPrefs value
   */
  public ApplicationPrefs getApplicationPrefs() {
    return applicationPrefs;
  }

  /**
   * Sets the applicationPrefs attribute of the PacketContext object
   *
   * @param applicationPrefs The new applicationPrefs value
   */
  public void setApplicationPrefs(ApplicationPrefs applicationPrefs) {
    this.applicationPrefs = applicationPrefs;
  }

  public OutputStream getOutputStream() {
    return outputStream;
  }

  public void setOutputStream(OutputStream outputStream) {
    this.outputStream = outputStream;
  }

  public HttpServletResponse getResponse() {
    return response;
  }

  public void setResponse(HttpServletResponse response) {
    this.response = response;
  }

  public ConnectionPool getConnectionPool() {
    return connectionPool;
  }

  public void setConnectionPool(ConnectionPool connectionPool) {
    this.connectionPool = connectionPool;
  }

  public ConnectionElement getConnectionElement() {
    return connectionElement;
  }

  public void setConnectionElement(ConnectionElement connectionElement) {
    this.connectionElement = connectionElement;
  }

  public Scheduler getScheduler() {
    return scheduler;
  }

  public void setScheduler(Scheduler scheduler) {
    this.scheduler = scheduler;
  }
}