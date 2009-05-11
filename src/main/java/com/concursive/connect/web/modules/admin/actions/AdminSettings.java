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

import com.concursive.commons.web.mvc.actions.ActionContext;
import com.concursive.connect.config.ApplicationPrefs;
import com.concursive.connect.web.controller.actions.GenericAction;
import com.concursive.connect.web.modules.admin.beans.DatabaseSettingsBean;
import com.concursive.connect.web.modules.admin.beans.EmailSettingsBean;
import com.concursive.connect.web.modules.setup.beans.SiteSettingsBean;

import java.io.File;

/**
 * Actions for the administration module
 *
 * @author matt rajkowski
 * @version $Id$
 * @created February 24, 2004
 */
public final class AdminSettings extends GenericAction {

  private final static String fs = System.getProperty("file.separator");


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
  public String executeCommandLook(ActionContext context) {
    if (!getUser(context).getAccessAdmin()) {
      return "PermissionError";
    }
    boolean firstTime = (context.getRequest().getParameter("send") == null);
    if (firstTime) {
      context.getRequest().setAttribute("currentLook", getPref(context, "TEMPLATE"));
      context.getRequest().setAttribute("currentStyleSheet", getPref(context, "CSS"));
      context.getRequest().setAttribute("currentSkin", getPref(context, "SKIN"));
    }
    return "LookOK";
  }


  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandSaveLook(ActionContext context) {
    if (!getUser(context).getAccessAdmin()) {
      return "PermissionError";
    }
    String template = "layout" + context.getRequest().getParameter("template");
    String css = context.getRequest().getParameter("css");
    String skin = context.getRequest().getParameter("skin");
    boolean save = "Save".equals(context.getRequest().getParameter("send"));
    // set the template
    try {
      File templateJSP = new File(ApplicationPrefs.getRealPath(context.getServletContext()) + template + ".jsp");
      if (templateJSP.exists()) {
        context.getServletContext().setAttribute("Template", "/" + template + ".jsp");
        if (save) {
          getApplicationPrefs(context).add("TEMPLATE", "/" + template + ".jsp");
          context.getRequest().setAttribute("currentLook", getPref(context, "TEMPLATE"));
        } else {
          context.getRequest().setAttribute("currentLook", "/" + template + ".jsp");
        }
      }
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
    // set the css
    try {
      File cssFile = new File(ApplicationPrefs.getRealPath(context.getServletContext()) + "css" + fs + css + ".css");
      if (cssFile.exists()) {
        context.getServletContext().setAttribute("CSS", css);
        if (save) {
          getApplicationPrefs(context).add("CSS", css);
          context.getRequest().setAttribute("currentStyleSheet", getPref(context, "CSS"));
        } else {
          context.getRequest().setAttribute("currentStyleSheet", css);
        }
      }
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
    // set the skin
    try {
      File skinFile = new File(ApplicationPrefs.getRealPath(context.getServletContext()) + "images" + fs + "select_" + skin + ".gif");
      if (skinFile.exists()) {
        context.getServletContext().setAttribute("SKIN", skin);
        if (save) {
          getApplicationPrefs(context).add("SKIN", skin);
          context.getRequest().setAttribute("currentSkin", getPref(context, "SKIN"));
        } else {
          context.getRequest().setAttribute("currentSkin", skin);
        }
      }
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
    if (save) {
      getApplicationPrefs(context).save();
      return "SaveLookOK";
    }
    return "PreviewLookOK";
  }


  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandCancelLook(ActionContext context) {
    if (!getUser(context).getAccessAdmin()) {
      return "PermissionError";
    }
    // set the defaults
    context.getServletContext().setAttribute("Template", getPref(context, "TEMPLATE"));
    context.getServletContext().setAttribute("CSS", getPref(context, "CSS"));
    context.getServletContext().setAttribute("SKIN", getPref(context, "SKIN"));
    return "CancelOK";
  }


  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandSite(ActionContext context) {
    if (!getUser(context).getAccessAdmin()) {
      return "PermissionError";
    }
    try {
      SiteSettingsBean thisBean = (SiteSettingsBean) context.getFormBean();
      if (!thisBean.isLoaded()) {
        thisBean.load(getApplicationPrefs(context), getPath(context, "templates"));
      }
      return "SiteOK";
    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return ("SystemError");
    }
  }


  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandEmail(ActionContext context) {
    if (!getUser(context).getAccessAdmin()) {
      return "PermissionError";
    }
    try {
      EmailSettingsBean thisBean = (EmailSettingsBean) context.getFormBean();
      if (!thisBean.isLoaded()) {
        thisBean.load(getApplicationPrefs(context), getPath(context, "templates"));
      }
      return "EmailOK";
    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return ("SystemError");
    }
  }


  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandDatabase(ActionContext context) {
    if (!getUser(context).getAccessAdmin()) {
      return "PermissionError";
    }
    try {
      DatabaseSettingsBean thisBean = (DatabaseSettingsBean) context.getFormBean();
      if (!thisBean.isLoaded()) {
        thisBean.load(getApplicationPrefs(context), getPath(context, "templates"));
      }
      return "DatabaseOK";
    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return ("SystemError");
    }
  }

}

