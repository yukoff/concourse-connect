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
package com.concursive.connect.web.modules.wiki.portlets;

import com.concursive.commons.text.StringUtils;
import com.concursive.connect.config.ApplicationPrefs;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.dao.ProjectCategoryList;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import com.concursive.connect.web.modules.wiki.dao.Wiki;
import com.concursive.connect.web.modules.wiki.dao.WikiList;
import com.concursive.connect.web.modules.wiki.utils.CustomFormUtils;
import com.concursive.connect.web.modules.wiki.utils.WikiToHTMLContext;
import com.concursive.connect.web.modules.wiki.utils.WikiToHTMLUtils;
import com.concursive.connect.web.modules.wiki.utils.WikiUtils;
import com.concursive.connect.web.modules.documents.dao.ImageInfo;
import com.concursive.connect.web.portal.PortalUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.portlet.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * Edit Project Wiki Portlet
 *
 * @author Kailash Bhoopalam
 * @created October 17, 2008
 */
public class EditProjectWikiPortlet extends GenericPortlet {

  private static Log LOG = LogFactory.getLog(EditProjectWikiPortlet.class);

  // Pages
  private static final String VIEW_PAGE = "/portlets/edit_project_wiki/edit_project_wiki-view.jsp";
  private static final String CLOSE_PAGE = "/portlets/edit_project_wiki/edit_project_wiki-refresh.jsp";

  // Preferences
  private static final String PREF_TITLE = "title";
  private static final String PREF_FAILURE_MESSAGE = "failureMessage";
  private static final String PREFERENCE_MAP = "preferenceMap";

  // Attribute names for objects available in the view
  private static final String TITLE = "title";
  private static final String WIKI = "wiki";
  private static final String ORIGINAL_WIKI = "originalWiki";
  private static final String WIKI_HTML = "wikiHtml";
  private static final String IMAGE_LIST = "imageList";
  private static final String PROJECT = "projectToEdit";
  private static final String HAS_PROJECT_ACCESS = "hasProjectAccess";

  private static final String VIEW_TYPE = "viewType";
  private static final String SAVE_FAILURE = "saveFailure";
  private static final String CLOSE = "close";

  public void doView(RenderRequest request, RenderResponse response)
      throws PortletException, IOException {
    try {
      String defaultView = VIEW_PAGE;
      String viewType = request.getParameter("viewType");
      if (viewType == null) {
        viewType = (String) request.getPortletSession().getAttribute("viewType");
      }
      ApplicationPrefs prefs = PortalUtils.getApplicationPrefs(request);

      // Set global preferences
      request.setAttribute(TITLE, request.getPreferences().getValue(PREF_TITLE, null));
      Project project = PortalUtils.getProject(request);
      request.setAttribute(PROJECT, project);

      Connection db = null;
      db = PortalUtils.useConnection(request);
      if (!ProjectUtils.hasAccess(PortalUtils.getProject(request).getId(), PortalUtils.getUser(request), "project-profile-admin")) {
        request.setAttribute(HAS_PROJECT_ACCESS, "false");
      } else if (CLOSE.equals(viewType)) {
        request.getPortletSession().removeAttribute(VIEW_TYPE);
        defaultView = CLOSE_PAGE;
      } else if (SAVE_FAILURE.equals(viewType)) {
        Wiki wiki = (Wiki) request.getPortletSession().getAttribute(ORIGINAL_WIKI);
        request.setAttribute(WIKI, wiki);

        request.getPortletSession().removeAttribute(VIEW_TYPE);
        request.getPortletSession().removeAttribute(ORIGINAL_WIKI);
      } else {

        // Build display preferences for editing
        String categoryName = getProjectCategoryName(db, project);
        if (StringUtils.hasText(categoryName)) {
          HashMap<String, String> preferenceMap = getDisplayPreferences(request, categoryName);
          request.setAttribute(PREFERENCE_MAP, preferenceMap);
        }

        //Load the wiki page
        Wiki wiki = WikiList.queryBySubject(db, "", project.getId());
        request.setAttribute(WIKI, wiki);
        // Load wiki image library dimensions (cache in future)
        HashMap<String, ImageInfo> imageList = WikiUtils.buildImageInfo(db, project.getId());
        request.setAttribute(IMAGE_LIST, imageList);
        // @note the context must be used instead of the full URL
        WikiToHTMLContext wikiContext = new WikiToHTMLContext(wiki, imageList, PortalUtils.getUser(request).getId(), true, request.getContextPath());

        wikiContext.setEditFormId(1);
        // Convert wiki to HTML
        String wikiHtml = WikiToHTMLUtils.getHTML(wikiContext, db);
        request.setAttribute(WIKI_HTML, wikiHtml);
      }

      PortletContext context = getPortletContext();
      PortletRequestDispatcher requestDispatcher =
          context.getRequestDispatcher(defaultView);
      requestDispatcher.include(request, response);
    } catch (Exception e) {
      e.printStackTrace();
      throw new PortletException(e);
    }
  }

  public void processAction(ActionRequest request, ActionResponse response)
      throws PortletException, IOException {

    try {
      if (!ProjectUtils.hasAccess(PortalUtils.getProject(request).getId(), PortalUtils.getUser(request), "project-profile-admin")) {
        request.setAttribute(HAS_PROJECT_ACCESS, "false");
      } else {
        int success = updateWiki(request);
        if (success == -1) {
          request.getPortletSession().setAttribute(VIEW_TYPE, SAVE_FAILURE);
        } else {
          request.getPortletSession().setAttribute(VIEW_TYPE, CLOSE);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

  /**
   * @param project
   * @return
   * @throws SQLException
   */
  private String getProjectCategoryName(Connection db, Project project) throws SQLException {
    String categoryName = null;
    ProjectCategoryList projectCategoryList = new ProjectCategoryList();
    projectCategoryList.setEnabled(true);
    projectCategoryList.setTopLevelOnly(true);
    projectCategoryList.buildList(db);
    categoryName = projectCategoryList.getValueFromId(project.getCategoryId());
    return categoryName;
  }

  private int updateWiki(ActionRequest request) throws Exception {
    Project project = PortalUtils.getProject(request);
    Connection db = null;
    db = PortalUtils.useConnection(request);
    int resultCount = -1;
    // Existing entry
    Wiki originalWiki = WikiList.queryBySubject(db, "", project.getId());

    // Replace the form in the wiki with the modified form...
    Wiki wiki = new Wiki(db, originalWiki.getId());
    wiki.setModifiedBy(PortalUtils.getUser(request).getId());
    wiki.setModified(originalWiki.getModified());
    wiki.setSubject(originalWiki.getSubject());
    wiki.setId(originalWiki.getId());
    wiki.setProjectId(originalWiki.getProjectId());
    CustomFormUtils.populateForm(wiki, request);
    // Update it...
    if (!originalWiki.getContent().equals(wiki.getContent())) {
      resultCount = wiki.update(db, originalWiki);
      if (resultCount <= 0) {
        // The record didn't get updated so alert user to resolve conflict
        request.getPortletSession().setAttribute(ORIGINAL_WIKI, originalWiki);
        return -1;
      }
      PortalUtils.indexAddItem(request, wiki);
    }

    return 0;
  }

  private HashMap<String, String> getDisplayPreferences(RenderRequest request, String categoryName) {
    String[] displayPreferences = request.getPreferences().getValues(categoryName.toLowerCase(), null);
    HashMap<String, String> preferenceMap = new HashMap<String, String>();
    if (displayPreferences != null) {
      int numberOfModules = displayPreferences.length;
      int count = 0;
      while (count < numberOfModules) {
        String label = null;
        String value = null;
        String displayPreference = displayPreferences[count];
        if (displayPreference.indexOf("=") != -1) {
          label = displayPreference.split("=")[0];
          value = displayPreference.split("=")[1];
        } else {
          label = displayPreference;
          value = displayPreference;
        }
        preferenceMap.put(label, value);
        count++;
      }
    }
    return preferenceMap;
  }

}
