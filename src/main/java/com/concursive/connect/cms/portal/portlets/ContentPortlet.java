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
package com.concursive.connect.cms.portal.portlets;

import com.concursive.commons.text.StringUtils;
import com.concursive.connect.Constants;
import com.concursive.connect.config.ApplicationPrefs;
import com.concursive.connect.web.modules.documents.dao.FileItemList;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.portal.PortalUtils;

import javax.portlet.*;
import java.io.IOException;
import java.sql.Connection;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * An HTML portlet
 *
 * @author matt rajkowski
 * @version $Id$
 * @created Feb 9, 2007
 */
public class ContentPortlet extends GenericPortlet {

  private static Log LOG = LogFactory.getLog(ContentPortlet.class);
  private static final String VIEW_PAGE = "/portlets/content/content-view.jsp";
  private static final String EDIT_PAGE = "/portlets/content/content-edit.jsp";
  private static final String HELP_PAGE = "/portlets/content/content-help.jsp";
  private static final String CONTENT = "content";
  private static final String STYLE_IMAGE = "style-image";
  private static final String DEFAULT_CONTENT = "<p>This content can be edited.</p>";

  // GenericPortlet Impl -----------------------------------------------------
  public void doView(RenderRequest request, RenderResponse response)
      throws PortletException, IOException {
    // For content links
    String ctx = request.getContextPath();
    // Get the content and replace any parameters
    String content = request.getPreferences().getValue(CONTENT, DEFAULT_CONTENT);
    content = StringUtils.replace(content, "${ctx}", ctx);
    request.setAttribute(CONTENT, content);

    // This portlet can retrieve state data from other portlets
    boolean showThisPortlet = true;
    for (String event : PortalUtils.getDashboardPortlet(request).getConsumeDataEvents()) {
      // Detects if values are true
      if (event.equals("hideIfEmpty")) {
        String value = (String) PortalUtils.getGeneratedData(request, event);
        if (StringUtils.hasText(value) && "false".equals(value)) {
          showThisPortlet = false;
        }
      }
    }

    // Check any rules
    String styleImage = request.getPreferences().getValue(STYLE_IMAGE, null);
    if (styleImage != null) {
      // Determine the database connection
      Connection db = PortalUtils.getConnection(request);
      // Use the project for this rule
      Project project = PortalUtils.findProject(request);
      if (project != null) {
        try {
          // Check if the specified file exists, otherwise hide this portlet
          FileItemList imageList = new FileItemList();
          imageList.setLinkModuleId(Constants.PROJECT_STYLE_FILES);
          imageList.setLinkItemId(project.getId());
          imageList.setFilename(styleImage);
          imageList.buildList(db);
          if (imageList.size() < 1) {
            showThisPortlet = false;
          }
        } catch (Exception e) {
//          LOG.error("unexpected styleImage error", e);
        }
      }
    }

    // Hide the portlet if the portlet is for sensitive mode and sensitive is off
    ApplicationPrefs prefs = PortalUtils.getApplicationPrefs(request);
    LOG.debug("IsSensitive? " + PortalUtils.getDashboardPortlet(request).isSensitive());
    LOG.debug("Prefs set to sensitive? " + prefs.get(ApplicationPrefs.INFORMATION_IS_SENSITIVE));
    if (PortalUtils.getDashboardPortlet(request).isSensitive() &&
        !"true".equals(prefs.get(ApplicationPrefs.INFORMATION_IS_SENSITIVE))) {
      showThisPortlet = false;
    }

    if (showThisPortlet) {
      // Show the content
      LOG.debug("Showing the content... length: " + content.length());
      PortletContext context = getPortletContext();
      PortletRequestDispatcher requestDispatcher =
          context.getRequestDispatcher(VIEW_PAGE);
      requestDispatcher.include(request, response);
    } else {
      LOG.debug("Not showing the content.");
    }
  }

  protected void doEdit(RenderRequest request, RenderResponse response)
      throws PortletException, IOException {
    request.setAttribute(CONTENT, request.getPreferences().getValue(CONTENT, DEFAULT_CONTENT));
    PortletContext context = getPortletContext();
    PortletRequestDispatcher requestDispatcher =
        context.getRequestDispatcher(EDIT_PAGE);
    requestDispatcher.include(request, response);
  }

  protected void doHelp(RenderRequest request, RenderResponse response)
      throws PortletException, IOException {
    PortletContext context = getPortletContext();
    PortletRequestDispatcher requestDispatcher =
        context.getRequestDispatcher(HELP_PAGE);
    requestDispatcher.include(request, response);
  }

  public void processAction(ActionRequest request, ActionResponse response)
      throws PortletException, IOException {
    String content = request.getParameter(CONTENT);
    if (content != null) {
      PortletPreferences prefs = request.getPreferences();
      prefs.setValue(CONTENT, content);
      prefs.store();
    }
  }
}
