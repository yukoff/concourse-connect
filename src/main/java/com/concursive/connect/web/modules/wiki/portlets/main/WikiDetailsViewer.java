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
package com.concursive.connect.web.modules.wiki.portlets.main;

import com.concursive.commons.web.URLFactory;
import com.concursive.connect.Constants;
import com.concursive.connect.cms.portal.dao.ProjectItemList;
import com.concursive.connect.config.ApplicationPrefs;
import com.concursive.connect.web.modules.common.social.rating.dao.Rating;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import com.concursive.connect.web.modules.wiki.dao.Wiki;
import com.concursive.connect.web.modules.wiki.dao.WikiCommentList;
import com.concursive.connect.web.modules.wiki.dao.WikiList;
import com.concursive.connect.web.modules.wiki.utils.WikiToHTMLContext;
import com.concursive.connect.web.modules.wiki.utils.WikiToHTMLUtils;
import com.concursive.connect.web.modules.wiki.utils.WikiUtils;
import com.concursive.connect.web.modules.documents.dao.ImageInfo;
import com.concursive.connect.web.portal.IPortletViewer;
import com.concursive.connect.web.portal.PortalUtils;
import static com.concursive.connect.web.portal.PortalUtils.findProject;
import static com.concursive.connect.web.portal.PortalUtils.getConnection;
import com.concursive.connect.web.utils.TrailMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import java.sql.Connection;
import java.util.HashMap;

/**
 * Project wiki details
 *
 * @author matt rajkowski
 * @created October 30, 2008
 */
public class WikiDetailsViewer implements IPortletViewer {

  // Logger
  private static Log LOG = LogFactory.getLog(WikiDetailsViewer.class);

  // Pages
  private static final String VIEW_PAGE = "/projects_center_wiki.jsp";

  // Preferences
  private static final String PREF_TITLE = "title";

  // Object Results
  private static final String TITLE = "title";
  private static final String WIKI = "wiki";
  private static final String WIKI_IMAGE_LIST = "imageList";
  private static final String WIKI_COMMENT_LIST = "commentList";
  private static final String WIKI_HTML = "wikiHtml";
  private static final String WIKI_USER_RATING = "userRating";
  private static final String WIKI_STATE_LIST = "wikiStateList";
  private static final String WIKI_TRAIL_MAP = "trailMap";

  public String doView(RenderRequest request, RenderResponse response) throws Exception {
    // The JSP to show upon success
    String defaultView = VIEW_PAGE;

    ApplicationPrefs prefs = PortalUtils.getApplicationPrefs(request);

    // General Display Preferences
    request.setAttribute(TITLE, request.getPreferences().getValue(PREF_TITLE, "Wiki"));

    // Determine the project container to use
    Project project = findProject(request);

    // Check the user's permissions
    User user = PortalUtils.getUser(request);
    if (!ProjectUtils.hasAccess(project.getId(), user, "project-wiki-view")) {
      throw new PortletException("Unauthorized to view in this project");
    }

    // Determine the record to show
    String subject = PortalUtils.getPageView(request);

    // Load the record
    Connection db = getConnection(request);
    Wiki wiki = WikiList.queryBySubject(db, subject, project.getId());
    request.setAttribute(WIKI, wiki);

    // Load wiki image library dimensions (@todo cache)
    HashMap<String, ImageInfo> imageList = WikiUtils.buildImageInfo(db, project.getId());
    request.setAttribute(WIKI_IMAGE_LIST, imageList);

    if (wiki.getId() > -1) {
      // Load comments
      WikiCommentList commentList = new WikiCommentList();
      commentList.setWikiId(wiki.getId());
      commentList.buildList(db);
      request.setAttribute(WIKI_COMMENT_LIST, commentList);

      // Convert the wiki to html for this user
      // @note the context is used instead of the full URL
      WikiToHTMLContext wikiContext = new WikiToHTMLContext(wiki, imageList, user.getId(), false, request.getContextPath());
      String wikiHtml = WikiToHTMLUtils.getHTML(wikiContext, db);
      request.setAttribute(WIKI_HTML, wikiHtml);

      // Load this user's rating of the wiki
      String userRating = String.valueOf(Rating.queryUserRating(db, user.getId(), wiki.getId(), Wiki.TABLE, Wiki.PRIMARY_KEY));
      request.setAttribute(WIKI_USER_RATING, userRating);

      // This portlet can provide data to other portlets
      for (String event : PortalUtils.getDashboardPortlet(request).getGenerateDataEvents()) {
        PortalUtils.setGeneratedData(request, event, wikiContext);
      }
    }

    // Load the states
    ProjectItemList stateList = new ProjectItemList();
    stateList.setProjectId(project.getId());
    stateList.setEnabled(Constants.TRUE);
    stateList.setIncludeId(wiki.getStateId());
    stateList.buildList(db, ProjectItemList.WIKI_STATE);
    request.setAttribute(WIKI_STATE_LIST, stateList);

    // Establish a trailMap per project
    TrailMap trailMap = (TrailMap) request.getPortletSession().getAttribute("trailMap" + project.getId());
    if (trailMap == null) {
      trailMap = new TrailMap();
      request.getPortletSession().setAttribute("trailMap" + project.getId(), trailMap);
    }
    trailMap.addItem(subject);
    request.setAttribute(WIKI_TRAIL_MAP, trailMap);

    // Record that this record has been viewed
    PortalUtils.processSelectHook(request, wiki);

    // JSP view
    return defaultView;
  }
}