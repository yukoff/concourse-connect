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
package com.concursive.connect.web.modules.reviews.portlets.main;

import com.concursive.commons.text.StringUtils;
import static com.concursive.connect.web.portal.PortalUtils.*;

import java.sql.Connection;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.servlet.http.HttpServletRequest;

import com.concursive.commons.web.mvc.beans.GenericBean;
import com.concursive.connect.web.modules.ModuleUtils;
import com.concursive.connect.web.modules.common.social.tagging.dao.TagLogList;
import com.concursive.connect.web.modules.common.social.tagging.dao.UserTagLogList;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import com.concursive.connect.web.portal.IPortletAction;
import com.concursive.connect.web.portal.PortalUtils;
import javax.portlet.PortletException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Updates users tag for a module and link
 *
 * @author Nanda Kumar
 * @created Aug 27, 2009
 */
public class SaveTagsAction implements IPortletAction {

  private static final Log LOG = LogFactory.getLog(SaveTagsAction.class);
  private static final String TAGS = "tagsText";
  private static final String CURRENT_TAGS = "ajaxMessage";

  public GenericBean processAction(ActionRequest request, ActionResponse response) throws Exception {
    // get the modules details
    int linkItemId = PortalUtils.getPageViewAsInt(request);
    String moduleName = PortalUtils.getPageDomainObject(request);

    // get parameter values
    String tags = request.getParameter(TAGS);

    // Determine the project container to use
    Project project = findProject(request);
    if (project == null) {
      throw new Exception("Project is null");
    }

    // @todo Load the object being tagged to make sure it exists

    // Check the user's permissions
    User user = getUser(request);
    if (!user.isLoggedIn()) {
      throw new PortletException("Unauthorized to add tags");
    }

    // @todo figure out the permission to check!
    if (!ProjectUtils.hasAccess(project.getId(), user, "project-profile-view")) {
      throw new PortletException("Unauthorized to access this profile");
    }

    // get database connection
    Connection db = getConnection(request);

    // update the tag changes
    UserTagLogList userTagLogList = new UserTagLogList();
    userTagLogList.setUserId(user.getId());
    userTagLogList.setLinkModuleId(ModuleUtils.getLinkModuleIdFromModuleName(moduleName));
    userTagLogList.setLinkItemId(linkItemId);
    userTagLogList.buildList(db);
    userTagLogList.updateTags(db, tags);

    // reload the tags to the page
    TagLogList tagLogList = new TagLogList();
    tagLogList.setTableName(ModuleUtils.getTableFromModuleName(moduleName));
    tagLogList.setUniqueField(ModuleUtils.getPrimaryKeyFromModuleName(moduleName));
    tagLogList.setLinkItemId(linkItemId);
    tagLogList.buildList(db);
    HttpServletRequest req = (HttpServletRequest) request;
    req.getSession().setAttribute(CURRENT_TAGS, StringUtils.toHtml(tagLogList.getTagsAsString()));
    response.sendRedirect(response.encodeURL(request.getContextPath() + "/projects_center_ajax_message.jsp"));

    return null;
  }
}
