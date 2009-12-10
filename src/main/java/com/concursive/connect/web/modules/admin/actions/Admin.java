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

import com.concursive.commons.http.RequestUtils;
import com.concursive.commons.web.mvc.actions.ActionContext;
import com.concursive.connect.config.ApplicationPrefs;
import com.concursive.connect.web.controller.actions.GenericAction;
import com.concursive.connect.web.modules.documents.beans.FileDownload;
import com.concursive.connect.web.modules.documents.dao.FileItem;

import java.io.File;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;

/**
 * Actions for the administration module
 *
 * @author matt rajkowski
 * @version $Id$
 * @created January 29, 2003
 */
public final class Admin extends GenericAction {

  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandDefault(ActionContext context) {
    if (!getUser(context).getAccessAdmin()) {
      return "PermissionError";
    }
    return "DefaultOK";
  }


  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandPrecompileJSPs(ActionContext context) {
    if (!getUser(context).getAccessAdmin() &&
        !"true".equals(context.getSession().getAttribute("precompile"))) {
      return "PermissionError";
    }
    File baseDir = new File(context.getServletContext().getRealPath("/"));
    precompileDirectory(context, baseDir, "/");
    return "PrecompileOK";
  }

  public String executeCommandEnableDemos(ActionContext context) {
    if (!getUser(context).getAccessAdmin()) {
      return "PermissionError";
    }
    context.getServletContext().setAttribute("demoAllowed", "online");
    return "DefaultOK";
  }

  public String executeCommandDisableDemos(ActionContext context) {
    if (!getUser(context).getAccessAdmin()) {
      return "PermissionError";
    }
    context.getServletContext().setAttribute("demoAllowed", "offline");
    return "DefaultOK";
  }

  /**
   * Action to begin precompiling JSPs by specifying the directory to compile
   *
   * @param context       Description of the Parameter
   * @param thisDirectory Description of the Parameter
   * @param dir           Description of the Parameter
   */
  private void precompileDirectory(ActionContext context, File thisDirectory, String dir) {
    File[] listing = thisDirectory.listFiles();
    for (File thisFile : listing) {
      if (thisFile.isDirectory()) {
        precompileDirectory(context, thisFile, dir + thisFile.getName() + "/");
      } else {
        precompileJSP(context, thisFile, dir);
      }
    }
  }


  /**
   * Method to compile a JSP by making an http request of the JSP
   *
   * @param context  Description of the Parameter
   * @param thisFile Description of the Parameter
   * @param dir      Description of the Parameter
   */
  private void precompileJSP(ActionContext context, File thisFile, String dir) {
    if (thisFile.getName().endsWith(".jsp") &&
        !thisFile.getName().endsWith("_include.jsp") &&
        !thisFile.getName().endsWith("_menu.jsp")) {
      String serverName = "http://" + RequestUtils.getServerUrl(context.getRequest());
      String jsp = serverName + dir + thisFile.getName();
      try {
        URL url = new URL(jsp);
        URLConnection conn = url.openConnection();
        // Re-use the same session for precompiling
        String thisCookie = (String) context.getRequest().getAttribute("cookies");
        if (thisCookie != null) {
          conn.setDoOutput(true);
          conn.setRequestProperty("Cookie", thisCookie);
        }
        conn.getContent();
        if (thisCookie == null) {
          String cookies = conn.getHeaderField("Set-Cookie");
          if (cookies != null) {
            context.getRequest().setAttribute("cookies", cookies);
          }
        }
      } catch (Exception e) {

      }
    }
  }


  public String executeCommandImg(ActionContext context) {
    if (!getUser(context).getAccessAdmin()) {
      return "PermissionError";
    }
    Connection db = null;
    FileItem fileItem = null;
    String fileItemIdString = context.getRequest().getParameter("fileItemId");
    try {
      db = getConnection(context);
      fileItem = new FileItem(db, Integer.parseInt(fileItemIdString));
    } catch (Exception e) {
      e.printStackTrace(System.out);
    } finally {
      freeConnection(context, db);
    }
    try {
      String filePath = this.getPath(context, "projects") + getDatePath(fileItem.getModified()) + fileItem.getFilename();
      // Stream the file
      FileDownload fileDownload = new FileDownload();
      fileDownload.setFullPath(filePath);
      fileDownload.setDisplayName(fileItem.getClientFilename());
      if (fileDownload.fileExists()) {
        fileDownload.setFileTimestamp(fileItem.getModificationDate().getTime());
        fileDownload.streamContent(context);
        return "-none-";
      } else {
        return "SystemERROR";
      }
    } catch (java.net.SocketException se) {
      se.printStackTrace();
      //User either canceled the download or lost connection
    } catch (Exception e) {
      e.printStackTrace();
    }
    return ("-none-");
  }
}

