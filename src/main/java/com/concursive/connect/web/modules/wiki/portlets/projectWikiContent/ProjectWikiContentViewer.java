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
package com.concursive.connect.web.modules.wiki.portlets.projectWikiContent;

import com.concursive.commons.text.StringUtils;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.login.utils.UserUtils;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import com.concursive.connect.web.modules.wiki.dao.Wiki;
import com.concursive.connect.web.modules.wiki.dao.WikiList;
import com.concursive.connect.web.modules.wiki.utils.WikiToHTMLContext;
import com.concursive.connect.web.modules.wiki.utils.WikiToHTMLUtils;
import com.concursive.connect.web.modules.wiki.utils.WikiUtils;
import com.concursive.connect.web.portal.IPortletViewer;
import com.concursive.connect.web.portal.PortalUtils;
import static com.concursive.connect.web.portal.PortalUtils.findProject;
import static com.concursive.connect.web.portal.PortalUtils.getConnection;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import java.sql.Connection;
import java.util.HashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Displays wiki information
 *
 * @author Kailash Bhoopalam
 * @created January 23, 2009
 */
public class ProjectWikiContentViewer implements IPortletViewer {

  private static Log LOG = LogFactory.getLog(ProjectWikiContentViewer.class);
  // Pages
  private static final String VIEW_PAGE = "/portlets/project_wiki_content/project_wiki_content-view.jsp";
  private static final String VIEW_PAGE_MESSAGE = "/portlets/project_wiki_content/project_wiki_content-view-message.jsp";
  // Preferences
  private static final String PREF_WIKI = "wiki";
  private static final String PREF_SHOW_TITLE = "showTitle";
  private static final String PREF_CONTENT_BASED_ON_USER = "contentIsBasedOnUser";
  // Object Results
  private static final String WIKI = "wiki";
  private static final String WIKI_NAME = "wikiName";
  private static final String WIKI_IMAGE_LIST = "imageList";
  private static final String WIKI_HTML = "wikiHtml";
  private static final String SHOW_TITLE = "showTitle";

  public String doView(RenderRequest request, RenderResponse response) throws Exception {

    String defaultView = VIEW_PAGE;

    // The wiki to display
    String wikiName = request.getPreferences().getValue(PREF_WIKI, null);

    // Determine if the wiki's title should be displayed
    request.setAttribute(SHOW_TITLE, request.getPreferences().getValue(PREF_SHOW_TITLE, "true"));

    // Determine the project container to use for accessing the wiki
    Project project = findProject(request);
    if (project == null) {
      LOG.debug("Skipping... project is null");
      return null;
    }

    // Determine if the wiki content is based on the current user or a default guest user
    boolean basedOnUser = Boolean.parseBoolean(request.getPreferences().getValue(PREF_CONTENT_BASED_ON_USER, "false"));

    // User
    User user = null;
    if (basedOnUser) {
      // Use the actual user
      user = PortalUtils.getUser(request);
      // Determine if the user has access to the content
      if (user == null || !ProjectUtils.hasAccess(project.getId(), user, "project-wiki-view")) {
        LOG.debug("Skipping... user is null or doesn't have access to view the wiki");
        return null;
      }
    }
    if (user == null) {
      // Simulate a guest user
      user = UserUtils.createGuestUser();
    }
    // Determine if the user has access to the content
    if (!ProjectUtils.hasAccess(project.getId(), user, "project-profile-view")) {
      LOG.debug("Skipping... no access to view the profile");
      return null;
    }

    // Load the wiki record
    Connection db = getConnection(request);
    Wiki wiki = WikiList.queryBySubject(db, wikiName, project.getId());
    request.setAttribute(WIKI, wiki);

    // Load wiki image library dimensions (@todo cache)
    HashMap imageList = WikiUtils.buildImageInfo(db, project.getId());
    request.setAttribute(WIKI_IMAGE_LIST, imageList);

    if (wiki.getId() > -1 && StringUtils.hasText(wiki.getContent())) {
      LOG.debug("Wiki found.");
      // Convert the wiki to html for this user
      WikiToHTMLContext wikiContext = new WikiToHTMLContext(wiki, imageList, db, user.getId(), false, request.getContextPath());
      String wikiHtml = WikiToHTMLUtils.getHTML(wikiContext);
      request.setAttribute(WIKI_HTML, wikiHtml);
    } else {
      LOG.debug("Wiki not found or has no content.");
      // Check if the current user can modify the target wiki content
      request.setAttribute(WIKI_NAME, wikiName);
      if (ProjectUtils.hasAccess(project.getId(), PortalUtils.getUser(request), "project-wiki-add")) {
        LOG.debug("Showing edit page information");
        return VIEW_PAGE_MESSAGE;
      }
      LOG.debug("Skipping.");
      return null;
    }

    // JSP view
    return defaultView;
  }
}
