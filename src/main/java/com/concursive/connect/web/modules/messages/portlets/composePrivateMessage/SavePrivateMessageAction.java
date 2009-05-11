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
package com.concursive.connect.web.modules.messages.portlets.composePrivateMessage;

import com.concursive.commons.web.mvc.beans.GenericBean;
import com.concursive.connect.Constants;
import com.concursive.connect.web.modules.ModuleUtils;
import com.concursive.connect.web.modules.blog.dao.BlogPost;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.login.utils.UserUtils;
import com.concursive.connect.web.modules.messages.dao.PrivateMessage;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.portal.IPortletAction;
import com.concursive.connect.web.portal.PortalUtils;
import static com.concursive.connect.web.portal.PortalUtils.*;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * Action for saving a user's review
 *
 * @author Kailash Bhoopalam
 * @created December 22, 2008
 */
public class SavePrivateMessageAction implements IPortletAction {

  public GenericBean processAction(ActionRequest request, ActionResponse response) throws Exception {
    // Determine the project container to use
    Project project = findProject(request);
    if (project == null) {
      throw new Exception("Project is null");
    }

    // Check the user's permissions
    User user = getUser(request);
    if (!user.isLoggedIn()) {
      throw new PortletException("User needs to be logged in");
    }

    // Determine the database connection to use
    Connection db = getConnection(request);
    String body = request.getParameter("body");
    String moduleName = request.getParameter("linkModule");
    int linkModuleId = ModuleUtils.getLinkModuleIdFromModuleName(moduleName);
    String linkItemId = request.getParameter("linkItemId");

    PrivateMessage inboxPrivateMessage = null;
    PrivateMessage privateMessage = new PrivateMessage();
    privateMessage.setBody(body);
    privateMessage.setLinkItemId(linkItemId);
    privateMessage.setLinkModuleId(linkModuleId);
    if (linkModuleId != Constants.PROJECT_MESSAGES_FILES) {
      privateMessage.setProjectId(project.getId());
      int linkProjectId = getLinkProjectId(db, linkItemId, linkModuleId);
      if (linkProjectId == -1) {
        linkProjectId = project.getId();
      }
      privateMessage.setLinkProjectId(linkProjectId);

    } else {
      //reply to a message from the inbox, so the project id needs to be the profile of user who sent the message
      inboxPrivateMessage = new PrivateMessage(db, Integer.parseInt(linkItemId));
      int profileProjectIdOfEnteredByUser = UserUtils.loadUser(inboxPrivateMessage.getEnteredBy()).getProfileProjectId();
      privateMessage.setProjectId(profileProjectIdOfEnteredByUser);
      privateMessage.setParentId(inboxPrivateMessage.getId());

      //update the last reply date of the message
      inboxPrivateMessage.setLastReplyDate(new Timestamp(System.currentTimeMillis()));
    }
    privateMessage.setEnteredBy(user.getId());
    boolean inserted = privateMessage.insert(db);

    if (inserted) {

      if (inboxPrivateMessage != null && inboxPrivateMessage.getId() != -1) {
        inboxPrivateMessage.update(db);
      }

      PortalUtils.processInsertHook(request, privateMessage);
    }
    // This call will close panels and perform redirects
    // Close the panel, everything went well
    //TODO: Kailash: Need to find out why PortalUtils.performRefresh did not work
    String ctx = request.getContextPath();
    response.sendRedirect(ctx + "/close_panel_refresh.jsp");
    return null;
    //return (PortalUtils.performRefresh(request, response, "/show" + ("profile".equals(linkModule)?"":"/" + linkModule)));
    //return (PortalUtils.performRefresh(request, response, "/show/reviews"));

  }

  /**
   * Gets the project Id for the related item.
   * E.g, A message sent to a profile of a user can refer to the blog the user has written in a business, organization or any other profile
   *
   * @param linkItemId
   * @param linkModuleId
   * @return
   */
  private int getLinkProjectId(Connection db, String linkItemId, int linkModuleId) throws SQLException {
    int linkProjectId = -1;
    if (linkModuleId == Constants.PROJECT_BLOG_FILES) {
      BlogPost blogPost = new BlogPost(db, Integer.parseInt(linkItemId));
      linkProjectId = blogPost.getProjectId();
    }
    return linkProjectId;
  }
}
