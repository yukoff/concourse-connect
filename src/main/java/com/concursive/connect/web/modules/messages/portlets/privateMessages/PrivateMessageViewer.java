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
package com.concursive.connect.web.modules.messages.portlets.privateMessages;

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
import java.sql.Timestamp;
import java.util.Calendar;

/**
 * View Private Message
 *
 * @author Kailash Bhoopalam
 * @created December 26, 2008
 */
public class PrivateMessageViewer implements IPortletViewer {
  // Pages
  private static final String VIEW_PAGE = "/portlets/private_messages/private_message-view.jsp";

  // Preferences
  private static final String PREF_TITLE = "title";

  // Object Results
  private static final String FOLDER = "folder";
  private static final String TITLE = "title";
  private static final String PRIVATE_MESSAGE = "privateMessage";
  private static final String PROJECT = "project";

  public String doView(RenderRequest request, RenderResponse response)
      throws Exception {
    // The JSP to show upon success
    String defaultView = VIEW_PAGE;

    // General display preferences
    request.setAttribute(TITLE, request.getPreferences().getValue(PREF_TITLE, "Message"));

    // Determine the project container to use
    Project project = findProject(request);

    // Check the user's permissions
    User user = getUser(request);
    if (!ProjectUtils.hasAccess(project.getId(), user, "project-private-messages-view")) {
      throw new PortletException("Unauthorized to view in this project");
    }
    String folder = PortalUtils.getPageView(request);
    request.setAttribute(FOLDER, folder);

    // Determine the database connection to use
    Connection db = useConnection(request);
    boolean validAccess = false;
    String messageId = PortalUtils.getPageParameter(request);
    PrivateMessage privateMessage = new PrivateMessage(db, Integer.parseInt(messageId));
    if (privateMessage.getProjectId() == project.getId()) {
      //viewing a message in the inbox
      request.setAttribute(PRIVATE_MESSAGE, privateMessage);
      validAccess = true;
    } else {
      //viewing a reply to a message
      int parentId = privateMessage.getParentId();
      if (parentId != -1) {
        PrivateMessage parentPrivateMessage = new PrivateMessage(db, parentId);
        if (parentPrivateMessage.getProjectId() == project.getId()) {
          validAccess = true;
          request.setAttribute(PRIVATE_MESSAGE, privateMessage);
        }
      } else {
        //a user viewing his sent messages in his profile
        int privateMessageEnteredBy = privateMessage.getEnteredBy();
        User enteredByUser = UserUtils.loadUser(privateMessageEnteredBy);
        if (project.getId() == enteredByUser.getProfileProjectId()) {
          validAccess = true;
          request.setAttribute(PRIVATE_MESSAGE, privateMessage);
        } else {
          throw new PortletException("Unauthorized access to message");
        }
      }
    }
    if (privateMessage.getReadBy() == -1 && validAccess) {
      privateMessage.setReadDate(new Timestamp(Calendar.getInstance().getTimeInMillis()));
      privateMessage.setReadBy(user.getId());
      privateMessage.update(db);
    }

    request.setAttribute(PROJECT, project);

    // JSP view
    return defaultView;
  }
}
