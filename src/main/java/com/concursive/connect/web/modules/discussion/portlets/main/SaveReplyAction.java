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

import com.concursive.commons.web.mvc.beans.GenericBean;
import com.concursive.connect.web.modules.discussion.dao.Topic;
import com.concursive.connect.web.modules.discussion.dao.Reply;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import com.concursive.connect.web.portal.IPortletAction;
import com.concursive.connect.web.portal.PortalUtils;
import static com.concursive.connect.web.portal.PortalUtils.*;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import java.sql.Connection;

/**
 * Action for saving a forum topic reply
 *
 * @author matt rajkowski
 * @created Nov 12, 2008 9:20:28 AM
 */
public class SaveReplyAction implements IPortletAction {

  public GenericBean processAction(ActionRequest request, ActionResponse response) throws Exception {

    // Determine the project container to use
    Project project = findProject(request);
    if (project == null) {
      throw new Exception("Project is null");
    }

    // Determine the user to use
    User user = getUser(request);

    // Determine the database connectivity
    Connection db = getConnection(request);
    boolean recordInserted = false;
    int resultCount = -1;

    // Populate any info from the request
    Reply reply = (Reply) getFormBean(request, Reply.class);

    // Set default values when saving records
    reply.setProjectId(project.getId());
    reply.setModifiedBy(user.getId());

    // Load the topic
    Topic topic = new Topic(db, reply.getIssueId(), project.getId());
    reply.setCategoryId(topic.getCategoryId());

    // Save the record
    Reply previousReply = null;
    if (reply.getId() == -1) {
      // This is a new record
      if (!ProjectUtils.hasAccess(project.getId(), user, "project-discussion-messages-reply")) {
        throw new PortletException("Unauthorized to add in this project");
      }
      reply.setEnteredBy(user.getId());
      recordInserted = reply.insert(db);
    } else {
      // This is an existing record
      if (!ProjectUtils.hasAccess(project.getId(), user, "project-discussion-messages-edit")) {
        throw new PortletException("Unauthorized to edit in this project");
      }
      // Load the previous reply for comparision
      previousReply = new Reply(db, reply.getId());
      reply.setEnteredBy(previousReply.getEnteredBy());
      resultCount = reply.update(db);
    }

    // Check if an error occurred
    if (!recordInserted && resultCount <= 0) {
      return reply;
    }

    // Index the record
    PortalUtils.indexAddItem(request, reply);

    // Trigger any workflow
    if (recordInserted) {
      PortalUtils.processInsertHook(request, reply);
    }
    if (resultCount > 0) {
      PortalUtils.processUpdateHook(request, previousReply, reply);
    }

    // This call will close panels and perform redirects
    return (PortalUtils.performRefresh(request, response, "/show/topic/" + topic.getId()));
  }
}
