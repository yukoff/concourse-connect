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
package com.concursive.connect.web.modules.wiki.portlets.wikiForm;

import com.concursive.connect.web.modules.contactus.dao.ContactUsBean;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.wiki.dao.Wiki;
import com.concursive.connect.web.modules.wiki.utils.WikiToHTMLContext;
import com.concursive.connect.web.modules.wiki.utils.WikiToHTMLUtils;
import com.concursive.connect.web.portal.IPortletViewer;
import com.concursive.connect.web.portal.PortalUtils;
import java.util.HashMap;
import static com.concursive.connect.web.portal.PortalUtils.findProject;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Displays a CustomForm form
 *
 * @author Matt Rajkowski
 * @created November 9, 2009
 */
public class WikiFormViewer implements IPortletViewer {

  private static Log LOG = LogFactory.getLog(WikiFormViewer.class);
  // Pages
  private static final String VIEW_PAGE_FORM = "/portlets/wiki_form/wiki_form-view.jsp";
  private static final String VIEW_PAGE_SUCCESS = "/portlets/wiki_form/wiki_form_success-view.jsp";
  // Preferences
  private static final String PREF_PROJECT = "project";
  private static final String PREF_TITLE = "title";
  private static final String PREF_INTRODUCTION_MESSAGE = "introductionMessage";
  private static final String PREF_FORM_CONTENT = "form";
  private static final String PREF_SUCCESS_MESSAGE = "successMessage";
  // Object Results
  private static final String TITLE = "title";
  private static final String INTRODUCTION_MESSAGE = "introductionMessage";
  private static final String WIKI_HTML = "wikiHtml";
  private static final String SUCCESS_MESSAGE = "successMessage";

  public String doView(RenderRequest request, RenderResponse response) throws Exception {

    // Determine the portlet's view to use
    String view = PortalUtils.getViewer(request);
    LOG.debug("Viewer... " + view);

    // The bean that holds the form validations
    PortalUtils.getFormBean(request, "contactUs", ContactUsBean.class);

    if ("success".equals(view)) {
      // Set the portlet preferences
      request.setAttribute(TITLE, request.getPreferences().getValue(PREF_TITLE, null));
      request.setAttribute(SUCCESS_MESSAGE, request.getPreferences().getValue(PREF_SUCCESS_MESSAGE, null));
      return VIEW_PAGE_SUCCESS;
    }

    // Determine the project container to use for accessing the wiki
    String uniqueId = request.getPreferences().getValue(PREF_PROJECT, null);
    Project project = findProject(request);
    if (project == null || !uniqueId.equals(project.getUniqueId())) {
      LOG.debug("Skipping...");
      return null;
    }

    // Convert wiki to html
    Wiki thisWiki = new Wiki();
    thisWiki.setContent(request.getPreferences().getValue(PREF_FORM_CONTENT, null));
    // Parse it
    WikiToHTMLContext wikiContext = new WikiToHTMLContext(thisWiki, new HashMap(), null, -1, true, request.getContextPath());
    String html = WikiToHTMLUtils.getHTML(wikiContext);

    // Set the portlet preferences
    request.setAttribute(TITLE, request.getPreferences().getValue(PREF_TITLE, null));
    request.setAttribute(INTRODUCTION_MESSAGE, request.getPreferences().getValue(PREF_INTRODUCTION_MESSAGE, null));
    request.setAttribute(WIKI_HTML, html);

    // JSP view
    return VIEW_PAGE_FORM;
  }
}
