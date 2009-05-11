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

import com.concursive.connect.Constants;
import com.concursive.connect.cms.portal.dao.ProjectItemList;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import com.concursive.connect.web.portal.IPortletViewer;
import com.concursive.connect.web.portal.PortalUtils;
import static com.concursive.connect.web.portal.PortalUtils.findProject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.portlet.PortletException;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import java.sql.Connection;

/**
 * Project wiki config
 *
 * @author matt rajkowski
 * @created November 3, 2008
 */
public class WikiConfigViewer implements IPortletViewer {

  // Logger
  private static Log LOG = LogFactory.getLog(WikiConfigViewer.class);

  // Pages
  private static final String VIEW_PAGE = "/projects_center_wiki_config.jsp";
  private static final String LIST_EDITOR_PAGE = "/projects_center_wiki_config_list.jsp";

  // Preferences
  private static final String PREF_TITLE = "title";

  // Object Results
  private static final String TITLE = "title";
  private static final String EDIT_LIST = "editList";
  private static final String SUB_TITLE = "subTitle";
  private static final String RETURN_MODULE = "returnUrl";

  public String doView(RenderRequest request, RenderResponse response) throws Exception {
    // The JSP to show upon success
    String defaultView = VIEW_PAGE;

    // General Display Preferences
    request.setAttribute(TITLE, request.getPreferences().getValue(PREF_TITLE, "Wiki"));

    // Determine the project container to use
    Project project = findProject(request);

    // Check the user's permissions
    User user = PortalUtils.getUser(request);
    if (!ProjectUtils.hasAccess(project.getId(), user, "project-setup-customize")) {
      throw new PortletException("Unauthorized to configure in this project");
    }

    // Check parameters for configuring the module, otherwise default to options
    String category = PortalUtils.getPageView(request);
    String object = PortalUtils.getPageParameter(request);

    Connection db = PortalUtils.getConnection(request);
    if (category != null) {
      // A list is being configured
      if ("lists".equals(category) && object != null) {
        // Make sure the database table is allowed to be accessed
        if (!WikiPortlet.isValidList("lookup_wiki_" + object)) {
          throw new PortletException("Invalid object specified");
        }
        // Load the data to be modified
        ProjectItemList itemList = new ProjectItemList();
        itemList.setProjectId(project.getId());
        itemList.setEnabled(Constants.TRUE);
        itemList.buildList(db, "lookup_wiki_" + object);
        request.setAttribute(EDIT_LIST, itemList.getHtmlSelect());
        // Determine the action which does the saving
        PortletURL actionURL = response.createActionURL();
        actionURL.setParameter("portlet-action", "configure");
        actionURL.setParameter("portlet-object", "wiki");
        actionURL.setParameter("portlet-value", "lists");
        actionURL.setParameter("portlet-params", object);
        actionURL.setParameter("portlet-command", WikiPortlet.SAVE_CONFIGURE_LISTS_ACTION);
        String url = actionURL.toString();
        LOG.debug("URL: " + url);

        // Provide properties to the editor
        request.setAttribute(SUB_TITLE, "Modify this project's list: " + object);
        String ctx = request.getContextPath();
        request.setAttribute(RETURN_MODULE, url);
        defaultView = LIST_EDITOR_PAGE;
      }
    }

    // JSP view
    return defaultView;
  }
}
