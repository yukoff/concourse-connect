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
package com.concursive.connect.web.modules.profile.portlets;

import com.concursive.commons.objects.ObjectUtils;
import com.concursive.commons.text.StringUtils;
import com.concursive.commons.text.Template;
import com.concursive.commons.xml.XMLUtils;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.members.dao.TeamMember;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import com.concursive.connect.web.portal.PortalUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;

import javax.portlet.*;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * A collection of actions that can be selected by the current user
 *
 * @author matt rajkowski
 * @created July 31, 2008
 */
public class ProjectActionsPortlet extends GenericPortlet {

  private static Log LOG = LogFactory.getLog(ProjectActionsPortlet.class);
  // Pages
  private static final String VIEW_PAGE = "/portlets/project_actions/project_actions-view.jsp";
  // Preferences
  private static final String PREF_TITLE = "title";
  private static final String PREF_URLS = "urls";
  // Attribute names for objects available in the view
  private static final String TITLE = "title";
  private static final String URL_LIST = "urlList";

  public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
    try {
      // Define the current view
      String defaultView = VIEW_PAGE;

      // Get preferences
      request.setAttribute(TITLE, request.getPreferences().getValue(PREF_TITLE, null));

      // Check for which project to use
      Project project = null;

      // This portlet can consume data from other portlets
      for (String event : PortalUtils.getDashboardPortlet(request).getConsumeDataEvents()) {
        if ("project".equals(event)) {
          project = (Project) PortalUtils.getGeneratedData(request, event);
        }
      }

      // Object from the portal
      if (project == null) {
        project = PortalUtils.getProject(request);
      }

      User thisUser = PortalUtils.getUser(request);
      TeamMember member = project.getTeam().getTeamMember(thisUser.getId());

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
        // See if the user has permission
        if (url.containsKey("permission")) {
          boolean hasPermission = ProjectUtils.hasAccess(project.getId(), thisUser, url.get("permission"));
          if (!hasPermission) {
            valid = false;
          }
        }
        // See if the project has a particular service available
        if (url.containsKey("service")) {
          boolean hasService = project.getServices().hasService(url.get("service"));
          if (!hasService) {
            valid = false;
          }
        }
        // See if any conditions fail
        if (url.containsKey("projectCondition")) {
          boolean meetsCondition = Boolean.parseBoolean(ObjectUtils.getParam(project, url.get("projectCondition")));
          if (!meetsCondition) {
            valid = false;
          }
        }
        // See if there are any special rules
        if (url.containsKey("rule")) {
          String rule = url.get("rule");
          if ("userHasToolsEnabled".equals(rule)) {
            if (member == null || !member.getTools() || !StringUtils.hasText(project.getConcursiveCRMUrl())) {
              valid = false;
            }
          } else if ("userHasCRMAccess".equals(rule)) {
            if (!thisUser.isConnectCRMAdmin() && !thisUser.isConnectCRMManager()) {
              LOG.debug("Does not have ConnectCRM access");
              valid = false;
            }
          } else if ("userCanRequestToJoin".equals(rule)) {
            boolean canRequestToJoin =
                (thisUser != null && thisUser.getId() > 0 &&
                (project.getFeatures().getAllowGuests() || project.getFeatures().getAllowParticipants()) &&
                project.getFeatures().getMembershipRequired() &&
                (member == null || member.getId() == -1));
            if (!canRequestToJoin) {
              valid = false;
            }
          } else if ("userCanJoin".equals(rule)) {
            // TODO: Update the code that adds the user, and set the team member status to pending, then remove the membership required part
            boolean canJoin = (thisUser.getId() > 0 && project.getFeatures().getAllowParticipants() && !project.getFeatures().getMembershipRequired() && member == null);
            if (!canJoin) {
              valid = false;
            }
          } else if ("projectAllowsGuests".equals(rule)) {
            if (!project.getFeatures().getAllowGuests()) {
              valid = false;
            }
          } else if ("projectHasTools".equals(rule)) {
            if (!(StringUtils.hasText(project.getConcursiveCRMUrl()) && StringUtils.hasText(project.getBusinessPhone()))) {
              valid = false;
            }
          } else if ("canClaim".equals(rule)) {
            // not logged in
            boolean isUser = thisUser != null && thisUser.getId() > 0;
            if (!isUser) {
              valid = false;
            }
            // already owned
            if (project.getOwner() > -1) {
              valid = false;
            }
          } else if ("isThisUsersProfile".equals(rule)) {
            boolean isThisUsersProfile = thisUser != null && thisUser.isLoggedIn() && project.getProfile() && project.getOwner() == thisUser.getId();
            if (!isThisUsersProfile) {
              valid = false;
            }
          } else if ("isNotThisUsersProfile".equals(rule)) {
            boolean isUser = thisUser != null && thisUser.getId() > 0;
            if (!isUser) {
              valid = false;
            }
            boolean isThisUsersProfile = thisUser != null && thisUser.isLoggedIn() && project.getProfile() && project.getOwner() == thisUser.getId();
            if (isThisUsersProfile) {
              valid = false;
            }
          } else if ("isUser".equals(rule)) {
            boolean isUser = thisUser != null && thisUser.getId() > 0;
            if (!isUser) {
              valid = false;
            }
          } else if ("isNotUser".equals(rule)) {
            boolean isUser = thisUser != null && thisUser.getId() > 0;
            if (isUser) {
              valid = false;
            }
          } else if ("userCanReview".equals(rule)) {
            boolean isUserCanReview = thisUser != null && thisUser.isLoggedIn() && project.getOwner() != thisUser.getId();
            if (!isUserCanReview) {
              valid = false;
            }
          } else {
            LOG.error("Rule not found: " + rule);
            valid = false;
          }
        }
        // Global disable
        if (url.containsKey("enabled")) {
          if ("false".equals(url.get("enabled"))) {
            valid = false;
          }
        }
        // If valid
        if (valid) {
          // Append any special parameters to the url
          if (url.containsKey("parameter")) {
            String parameter = url.get("parameter");
            // This parameter takes the current url and appends to the link
            if ("returnURL".equals(parameter)) {
              String requestedURL = (String) request.getAttribute("requestedURL");
              if (requestedURL != null) {
                String value = URLEncoder.encode(requestedURL, "UTF-8");
                LOG.debug("Parameter: " + parameter + "=" + value);
                String link = url.get("href");
                if (link.contains("&")) {
                  link += "&" + parameter + "=" + value;
                } else {
                  link += "?" + parameter + "=" + value;
                }
                LOG.debug("Setting href to " + link);
                url.put("href", link);
              }
            }
          }
          // Add to the list
          urlList.add(url);
        }
      }
      request.setAttribute(URL_LIST, urlList);

      // Only output the portlet if there are any urls to show
      if (urlList.size() > 0) {
        // JSP view
        PortletContext context = getPortletContext();
        PortletRequestDispatcher requestDispatcher =
            context.getRequestDispatcher(defaultView);
        requestDispatcher.include(request, response);
      }
    } catch (Exception e) {
      LOG.error("doView", e);
      throw new PortletException(e.getMessage());
    }
  }
}
