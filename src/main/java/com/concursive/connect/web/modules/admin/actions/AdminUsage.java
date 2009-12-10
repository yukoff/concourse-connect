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

package com.concursive.connect.web.modules.admin.actions;

import com.concursive.commons.files.FileUtils;
import com.concursive.commons.http.RequestUtils;
import com.concursive.commons.web.URLFactory;
import com.concursive.commons.web.mvc.actions.ActionContext;
import com.concursive.connect.Constants;
import com.concursive.connect.config.ApplicationPrefs;
import com.concursive.connect.config.ApplicationVersion;
import com.concursive.connect.indexer.IIndexerService;
import com.concursive.connect.indexer.IndexerFactory;
import com.concursive.connect.web.controller.actions.GenericAction;
import com.concursive.connect.web.modules.documents.dao.FileItemVersionList;
import com.concursive.connect.web.modules.login.dao.UserList;
import com.concursive.connect.web.modules.profile.dao.ProjectList;

import java.sql.Connection;
import java.text.NumberFormat;

/**
 * Actions for the administration module
 *
 * @author matt rajkowski
 * @version $Id$
 * @created February 23, 2004
 */
public final class AdminUsage extends GenericAction {

  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandDefault(ActionContext context) {
    ApplicationPrefs prefs = getApplicationPrefs(context);
    // Make sure the user is an admin
    if (!getUser(context).getAccessAdmin()) {
      return "PermissionError";
    }
    NumberFormat formatter = NumberFormat.getInstance();
    Connection db = null;
    // Load free disk space
    long size = FileUtils.getFreeBytes(getPref(context, "FILELIBRARY"));
    context.getRequest().setAttribute("diskFree", FileUtils.getRelativeSize(size, null));
    // File library location
    context.getRequest().setAttribute("diskPath", getPref(context, "FILELIBRARY"));
    // Application version
    context.getRequest().setAttribute("applicationVersion", ApplicationVersion.VERSION);
    // set the Indexer...
    IIndexerService indexer = IndexerFactory.getInstance().getIndexerService();
    context.getRequest().setAttribute(Constants.DIRECTORY_INDEX, FileUtils.getRelativeSize(indexer.getIndexSizeInBytes(Constants.INDEXER_DIRECTORY), null));
    try {
      db = getConnection(context);
      // Load project count
      int projectCount = ProjectList.buildProjectCount(db);
      context.getRequest().setAttribute("projectCount", formatter.format(projectCount));
      // Load user count
      int userCount = UserList.buildUserCount(db);
      context.getRequest().setAttribute("userCount", formatter.format(userCount));
      // Document library size
      FileItemVersionList fileItemList = new FileItemVersionList();
      long fileSize = fileItemList.queryFileSize(db);
      context.getRequest().setAttribute("fileSize", FileUtils.getRelativeSize(fileSize, null));
      // Services Id
      context.getRequest().setAttribute("servicesId", prefs.get("CONCURSIVE_SERVICES.ID"));
    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return ("SystemError");
    } finally {
      freeConnection(context, db);
    }
    return "DefaultOK";
  }

  public String executeCommandReloadWorkflows(ActionContext context) {
    if (!getUser(context).getAccessAdmin()) {
      return "PermissionError";
    }
    getApplicationPrefs(context).configureWorkflowManager(context.getServletContext());
    return "ReloadWorkflowsOK";
  }

  /**
   * A utility which allows an admin to have a properly configured system.
   * The prefs are used by background tasks which don't have access to the
   * intended URL.
   *
   * @param context
   * @return
   */
  public String executeCommandStoreURL(ActionContext context) {
    if (!getUser(context).getAccessAdmin()) {
      return "PermissionError";
    }
    ApplicationPrefs prefs = getApplicationPrefs(context);
    String url = URLFactory.createURL(prefs.getPrefs());
    if (url == null || !url.equals(RequestUtils.getAbsoluteServerUrl(context.getRequest()))) {
      LOG.debug("Saving the WEB properties");
      prefs.add(ApplicationPrefs.WEB_SCHEME, context.getRequest().getScheme());
      prefs.add(ApplicationPrefs.WEB_DOMAIN_NAME, context.getRequest().getServerName());
      prefs.add(ApplicationPrefs.WEB_PORT, String.valueOf(context.getRequest().getServerPort()));
      prefs.add(ApplicationPrefs.WEB_CONTEXT, context.getRequest().getContextPath());
      prefs.save();
      url = URLFactory.createURL(prefs.getPrefs());
    }
    context.getRequest().setAttribute("redirectTo", url);
    return "Redirect301";
  }

}

