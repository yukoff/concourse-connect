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
package com.concursive.connect.web.modules.wiki.portlets.addWiki;

import com.concursive.commons.text.StringUtils;
import com.concursive.commons.web.mvc.beans.GenericBean;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import com.concursive.connect.web.portal.IPortletAction;
import com.concursive.connect.web.portal.PortalUtils;
import static com.concursive.connect.web.portal.PortalUtils.findProject;
import static com.concursive.connect.web.portal.PortalUtils.getUser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import java.util.HashMap;

/**
 * Action for starting a wiki
 *
 * @author matt rajkowski
 * @created July 8, 2009
 */
public class SaveAddWikiFormAction implements IPortletAction {

  private static Log LOG = LogFactory.getLog(AddWikiPortlet.class);

  public GenericBean processAction(ActionRequest request, ActionResponse response) throws Exception {

    // Determine the project container to use
    Project project = findProject(request);
    if (project == null) {
      throw new Exception("Project is null");
    }

    // Check the user's permissions
    User user = getUser(request);
    if (!ProjectUtils.hasAccess(project.getId(), user, "project-wiki-add")) {
      throw new PortletException("Unauthorized to add in this project");
    }

    String title = request.getParameter("title");
    // URL reserved
    //    title = StringUtils.replace(title, "=", "_");
    //    title = StringUtils.replace(title, ";", "_");
    title = StringUtils.replace(title, "/", "_");
    title = StringUtils.replace(title, "#", "_");
    //    title = StringUtils.replace(title, "?", "_");
    // Conformity
    title = StringUtils.replace(title, "&apos;", "'");
    title = StringUtils.replace(title, "&rsquo;", "'");
    title = StringUtils.replace(title, "\u2019", "'");
    title = StringUtils.replace(title, "\u0027", "'");
    title = StringUtils.replace(title, "%u2019", "'");
    title = StringUtils.replace(title, "&#8217;", "'");
    title = StringUtils.replace(StringUtils.jsEscape(title), "%20", "+");

    String templateId = request.getParameter("templateId");

    LOG.debug("title: " + title);
    LOG.debug("templateId: " + templateId);

    // This call will close panels and perform redirects
    HashMap<String, String> params = new HashMap<String, String>();
    //    params.put("mode", "raw");
    if (templateId != null) {
      params.put("template", templateId);
    }
    return (PortalUtils.performRefresh(request, response, "/modify/wiki/" + title, params));
  }
}
