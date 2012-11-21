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

import com.concursive.commons.text.StringUtils;
import com.concursive.commons.web.mvc.actions.ActionContext;
import com.concursive.connect.Constants;
import com.concursive.connect.cache.utils.CacheUtils;
import com.concursive.connect.config.ApplicationPrefs;
import com.concursive.connect.web.controller.actions.GenericAction;
import com.concursive.connect.web.modules.documents.dao.FileItemList;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.dao.ProjectList;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;

import java.sql.Connection;
import java.util.Set;

/**
 * Actions for the administration module
 *
 * @author Kailash Bhoopalam
 * @created March 9, 2009
 */
public final class AdminCustomize extends GenericAction {

  /**
   * Action to prepare a list of Admin options
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandDefault(ActionContext context) {
    if (!getUser(context).getAccessAdmin()) {
      return "PermissionError";
    }
    Connection db = null;
    try {

      db = getConnection(context);
      int projectId = ProjectList.querySystemDefault(db);
      Project project = ProjectUtils.loadProject(projectId);
      context.getRequest().setAttribute("mainProfile", project);

      project.buildSiteLogo(db);
      context.getRequest().setAttribute("fileItemList", project.getSiteLogos());

      Set<String> themes = context.getServletContext().getResourcePaths("/themes/");
      context.getRequest().setAttribute("themes", themes);

      ApplicationPrefs prefs = this.getApplicationPrefs(context);
      String selectedTheme = prefs.get(ApplicationPrefs.THEME);
      Set<String> colorSchemes = context.getServletContext().getResourcePaths("/themes/" + selectedTheme + "/color-schemes/");
      context.getRequest().setAttribute("colorSchemes", colorSchemes);

    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return ("SystemError");
    } finally {
      freeConnection(context, db);
    }
    return "DefaultOK";
  }


  public String executeCommandSave(ActionContext context) {
    if (!getUser(context).getAccessAdmin()) {
      return "PermissionError";
    }
    Connection db = null;
    int count = 0;
    try {
      db = getConnection(context);

      int projectId = ProjectList.querySystemDefault(db);
      Project project = ProjectUtils.loadProject(projectId);

      String title = context.getRequest().getParameter("title");
      String shortDescription = context.getRequest().getParameter("shortDescription");

      //Update description
      project.setTitle(title);
      project.setShortDescription(shortDescription);
      project.update(db);

      //Update the logo
      String attachmentList = context.getRequest().getParameter("attachmentList");
      String attachmentText = context.getRequest().getParameter("attachmentText");
      String removeLogo = context.getRequest().getParameter("removeLogo");

      //Reset site logo if a new image is chosen
      project.buildSiteLogo(db);
      FileItemList siteLogos = project.getSiteLogos();
      if ("true".equals(removeLogo) && siteLogos != null) {
        siteLogos.delete(db, getPath(context, "projects"));
      } else {
        if (siteLogos != null && siteLogos.size() > 0) {
          if (StringUtils.hasText(attachmentList) &&
              (siteLogos.get(0).getId() != Integer.parseInt(attachmentList))) {
            project.insertSiteLogo(db, getUser(context).getId(), getPath(context, "projects"), attachmentList);
          }
        } else {
          project.insertSiteLogo(db, getUser(context).getId(), getPath(context, "projects"), attachmentList);
        }
      }

      //Save preferences for theme, color and logo
      ApplicationPrefs prefs = this.getApplicationPrefs(context);
      // Determine the logo
      prefs.add(ApplicationPrefs.WEB_PAGE_TITLE, title);
      if ("true".equals(removeLogo)) {
        prefs.add(ApplicationPrefs.WEB_PAGE_LOGO, null);
      } else {
        if (StringUtils.hasText(attachmentList)) {
          String logoName = Constants.SITE_LOGO_FILES + "-0-" + attachmentList + "-300x100";
          prefs.add(ApplicationPrefs.WEB_PAGE_LOGO, logoName);
        }
      }
      // Determine the theme
      String theme = context.getRequest().getParameter("theme");
      prefs.add(ApplicationPrefs.THEME, theme);
      // Determine the color scheme
      String colorScheme = context.getRequest().getParameter("colorScheme");
      prefs.add(ApplicationPrefs.COLOR_SCHEME, colorScheme);
      // Activate and save the theme
      prefs.configureDefaultBehavior(context.getServletContext());
      prefs.save();
      CacheUtils.invalidateValue(Constants.SYSTEM_PROJECT_CACHE, projectId);
    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return ("SystemError");
    } finally {
      freeConnection(context, db);
    }
    return "SaveOK";
  }

  public String executeCommandListColorSchemes(ActionContext context) {

    if (!getUser(context).getAccessAdmin()) {
      return "PermissionError";
    }

    String selectedTheme = context.getRequest().getParameter("theme");
    if (StringUtils.hasText(selectedTheme)) {
      Set<String> colorSchemes = context.getServletContext().getResourcePaths("/themes/" + selectedTheme + "/color-schemes/");
      context.getRequest().setAttribute("colorSchemes", colorSchemes);
    }
    context.getRequest().setAttribute(Constants.REQUEST_PAGE_LAYOUT, "/layout1.jsp");
    return "ColorSchemesOK";
  }
}

