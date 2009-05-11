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

import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.login.utils.UserUtils;
import com.concursive.connect.web.modules.messages.dao.PrivateMessage;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import com.concursive.connect.web.portal.IPortletViewer;
import com.concursive.connect.web.portal.PortalUtils;
import static com.concursive.connect.web.portal.PortalUtils.*;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import java.sql.Connection;

/**
 * Compose Private Message
 *
 * @author Kailash Bhoopalam
 * @created December 21, 2008
 */
public class ComposePrivateMessageForm implements IPortletViewer {

  // Pages
  private static final String VIEW_PAGE = "/portlets/compose_private_message/compose_private_message-form.jsp";

  // Preferences
  private static final String PREF_TITLE = "title";

  // Object Results
  private static final String TITLE = "title";
  private static final String PROJECT = "project";
  private static final String LINK_ITEM_ID = "linkItemId";
  private static final String LINK_MODULE = "linkModule";
  private static final String MESSAGE_TO = "messageTo";

  public String doView(RenderRequest request, RenderResponse response)
      throws Exception {
    // The JSP to show upon success
    String defaultView = VIEW_PAGE;

    // General display preferences
    request.setAttribute(TITLE, request.getPreferences().getValue(PREF_TITLE, "Pending Messages"));

    // Determine the project container to use
    Project project = findProject(request);

    request.setAttribute(PROJECT, project);

    // Check if the user is logged in
    User user = getUser(request);
    if (!user.isLoggedIn()) {
      throw new PortletException("User needs to be logged in to compose a message");
    }

    //Set the context
    String itemId = PortalUtils.getQueryParameter(request, "id");
    request.setAttribute(LINK_ITEM_ID, itemId);

    String module = PortalUtils.getQueryParameter(request, "module");

    //replying to message
    if (PrivateMessage.FOLDER_INBOX.equals(module)) {
      //determine if the user has access to reply to the message

      // Determine the database connection
      Connection db = getConnection(request);

      PrivateMessage inboxPrivateMessage = new PrivateMessage(db, Integer.parseInt(itemId));
      int projectId = inboxPrivateMessage.getProjectId();

      if (!ProjectUtils.hasAccess(projectId, user, "project-private-messages-reply")) {
        throw new PortletException("Unauthorized to reply to message in project");
      }

      if (UserUtils.loadUser(inboxPrivateMessage.getEnteredBy()).getProfileProject() != null) {
        request.setAttribute(MESSAGE_TO, UserUtils.loadUser(inboxPrivateMessage.getEnteredBy()).getProfileProject().getTitle());
      } else {
        request.setAttribute(MESSAGE_TO, UserUtils.loadUser(inboxPrivateMessage.getEnteredBy()).getNameFirstLastInitial());
      }
    } else {
      request.setAttribute(MESSAGE_TO, project.getTitle());
    }
    request.setAttribute(LINK_MODULE, module);

    // JSP view
    return defaultView;
  }
}
