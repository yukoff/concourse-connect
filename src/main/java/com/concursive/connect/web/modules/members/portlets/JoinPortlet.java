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
package com.concursive.connect.web.modules.members.portlets;

import com.concursive.commons.objects.ObjectUtils;
import com.concursive.commons.text.Template;
import com.concursive.commons.xml.XMLUtils;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.members.utils.TeamMemberUtils;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.portal.PortalUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;

import javax.portlet.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Join a profile or request to become a member portlet
 *
 * @author Ananth
 * @created Jan 4, 2010
 */
public class JoinPortlet extends GenericPortlet {
  private static Log LOG = LogFactory.getLog(JoinPortlet.class);
  //Pages
  private static final String MEMBER_JOIN_PROFILE_FORM = "/portlets/member_profile_join/member_profile_join-view.jsp";
  //Preferences
  private static final String PREF_URLS = "urls";
  // Attribute names for objects available in the view
  private static final String IS_USER_PROFILE = "isUserProfile";
  private static final String URL_LIST = "urlList";

  public void doView(RenderRequest request, RenderResponse response)
      throws PortletException, IOException {
    // Determine the project container to use
    Project project = PortalUtils.findProject(request);

    // Check the user's permissions
    User user = PortalUtils.getUser(request);

    try {
      // Get the urls to display
      ArrayList<HashMap> urlList = new ArrayList<HashMap>();
      String[] urls = request.getPreferences().getValues(PREF_URLS, new String[0]);
      for (String urlPreference : urls) {
        XMLUtils xml = new XMLUtils("<values>" + urlPreference + "</values>", true);
        ArrayList<Element> items = new ArrayList<Element>();
        XMLUtils.getAllChildren(xml.getDocumentElement(), items);
        HashMap<String, String> url = new HashMap<String, String>();
        for (Element thisItem : items) {
          String name = thisItem.getTagName();
          String value = thisItem.getTextContent();
          if (value.contains("${")) {
            Template template = new Template(value);
            for (String templateVariable : template.getVariables()) {
              String[] variable = templateVariable.split("\\.");
              template.addParseElement("${" + templateVariable + "}", ObjectUtils.getParam(PortalUtils.getGeneratedData(request, variable[0]), variable[1]));
            }
            value = template.getParsedText();
          }
          url.put(name, value);
        }

        // Determine if the url can be shown
        boolean valid = true;
        // See if there are any special rules
        if (url.containsKey("rule")) {
          String rule = url.get("rule");
          if ("userCanRequestToJoin".equals(rule)) {
            boolean canRequestToJoin = TeamMemberUtils.userCanRequestToJoin(user, project);
            if (!canRequestToJoin) {
              valid = false;
            }
          } else if ("userCanJoin".equals(rule)) {
            // TODO: Update the code that adds the user, and set the team member status to pending, then remove the membership required part
            boolean canJoin = TeamMemberUtils.userCanJoin(user, project);
            if (!canJoin) {
              valid = false;
            }
          } else {
            LOG.error("Rule not found: " + rule);
            valid = false;
          }
        }

        if (valid) {
          // Add to the list
          urlList.add(url);
        }
      }
      request.setAttribute(URL_LIST, urlList);

      // Only output the portlet if there are any urls to show
      if (urlList.size() > 0) {
        // Don't show the portlet on the user's own page
        if (user.getProfileProjectId() != project.getId()) {
          // Let the view know if this is a user profile
          if (project.getProfile()) {
            request.setAttribute(IS_USER_PROFILE, "true");
          }
          // Show the view
          PortletContext context = getPortletContext();
          PortletRequestDispatcher requestDispatcher = context.getRequestDispatcher(MEMBER_JOIN_PROFILE_FORM);
          requestDispatcher.include(request, response);
        }
      }
    } catch (Exception e) {
      LOG.error("doView", e);
      throw new PortletException(e.getMessage());
    }
  }
}
