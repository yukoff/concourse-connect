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

package com.concursive.connect.web.modules.discussion.portlets.main;

import com.concursive.connect.web.modules.discussion.dao.Topic;
import com.concursive.connect.web.modules.discussion.dao.Forum;
import com.concursive.connect.web.modules.discussion.dao.Reply;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.login.utils.UserUtils;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import com.concursive.connect.web.portal.IPortletViewer;
import com.concursive.connect.web.portal.PortalUtils;
import static com.concursive.connect.web.portal.PortalUtils.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import java.sql.Connection;

/**
 * Generates a forum topic reply form
 *
 * @author matt rajkowski
 * @created November 12, 2008
 */
public class ReplyFormViewer implements IPortletViewer {

  private static Log LOG = LogFactory.getLog(PortalUtils.class);

  // Pages
  private static final String VIEW_PAGE = "/projects_center_issues_reply.jsp";

  // Preferences
  private static final String PREF_TITLE = "title";

  // Object Results
  private static final String TITLE = "title";
  private static final String TOPIC = "topic";
  private static final String FORUM = "forum";
  private static final String REPLY = "reply";
  private static final String MESSAGE_TO_QUOTE = "messageToQuote";
  private static final String QUOTE_MESSAGE = "quoteMessage";

  public String doView(RenderRequest request, RenderResponse response) throws Exception {
    // The JSP to show upon success
    String defaultView = VIEW_PAGE;

    // General display preferences
    request.setAttribute(TITLE, request.getPreferences().getValue(PREF_TITLE, "Topic"));

    // Determine the project container to use
    Project project = findProject(request);

    // Determine the record to show
    int recordId = getPageViewAsInt(request);

    // Check the user executing the action
    User user = getUser(request);

    // Determine the database connection
    Connection db = getConnection(request);

    // Check the request for the record and provide a value for the request scope
    Reply thisReply = (Reply) PortalUtils.getFormBean(request, REPLY, Reply.class);

    // Decide if adding or updating
    if (recordId > -1) {
      // Modifying an existing record so load the record for modifying
      LOG.debug("Updating a reply");
      thisReply = new Reply(db, recordId);
      // Additional permission check to allow owner of the message to edit
      if (!ProjectUtils.hasAccess(project.getId(), user, "project-discussion-messages-edit") &&
          thisReply.getEnteredBy() != user.getId()) {
        throw new PortletException("Unauthorized to modify this record");
      }
      request.setAttribute(REPLY, thisReply);
    } else {
      LOG.debug("Adding a reply");
      // Check permission for adding
      if (!ProjectUtils.hasAccess(project.getId(), user, "project-discussion-messages-reply")) {
        throw new PortletException("Unauthorized to add this record");
      }
      // Determine the message that is being replied to
      String replyToValue = request.getParameter("replyTo");
      if (replyToValue != null) {
        int replyToId = Integer.parseInt(replyToValue);
        thisReply.setReplyToId(replyToId);
        // Prepare the reply for quoting
        Reply quoteMessage = new Reply(db, thisReply.getReplyToId());
        User quoteUser = UserUtils.loadUser(quoteMessage.getEnteredBy());
        String messageToQuote =
            quoteUser.getNameFirstLastInitial() + " wrote:" + lf +
                quoteMessage.getBody() + lf +
                "-----" + lf + lf;
        request.setAttribute(MESSAGE_TO_QUOTE, messageToQuote);
        request.setAttribute(QUOTE_MESSAGE, "true");
        thisReply.setIssueId(quoteMessage.getIssueId());
        thisReply.setCategoryId(quoteMessage.getCategoryId());
      }
      // Determine the topic when adding
      String topicValue = request.getParameter("topic");
      if (topicValue != null) {
        int topicId = Integer.parseInt(topicValue);
        thisReply.setIssueId(topicId);
      }
    }

    // Load the topic for display
    Topic topic = new Topic(db, thisReply.getIssueId(), project.getId());
    request.setAttribute(TOPIC, topic);
    if (recordId == -1) {
      // Auto-set a reply subject
      thisReply.setSubject("RE: " + topic.getSubject());
    }

    // Load the forum for display
    Forum forum = new Forum(db, topic.getCategoryId(), project.getId());
    request.setAttribute(FORUM, forum);

    // JSP view
    return defaultView;
  }
}