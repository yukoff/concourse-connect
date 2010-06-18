/*
 * ConcourseConnect
 * Copyright 2010 Concursive Corporation
 * http://www.concursive.com
 *
 * This file is part of ConcourseConnect and is licensed under a commercial
 * license, not an open source license.
 *
 * Attribution Notice: ConcourseConnect is an Original Work of software created
 * by Concursive Corporation
 */
package com.concursive.connect.web.modules.activity.portlets.activityStream;

import com.concursive.commons.text.StringUtils;
import com.concursive.commons.web.mvc.beans.GenericBean;
import com.concursive.connect.web.modules.activity.beans.ProjectHistoryReplyBean;
import com.concursive.connect.web.modules.activity.dao.ProjectHistory;
import com.concursive.connect.web.modules.activity.utils.ProjectHistoryUtils;
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
 * Save activity stream reply
 *
 * @author Nanda Kumar
 * @created December 07, 2009
 */
public class SaveActivityReplyAction implements IPortletAction {

  public GenericBean processAction(ActionRequest request,
                                   ActionResponse response) throws Exception {

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
    Connection db = useConnection(request);

    // Populate any info from the request
    ProjectHistoryReplyBean reply = (ProjectHistoryReplyBean) getFormBean(request, ProjectHistoryReplyBean.class);

    // Load the previous history item being replied to
    ProjectHistory projectHistory = new ProjectHistory(db, reply.getParentId());

    // Check authorization
    if (!ProjectUtils.hasAccess(projectHistory.getProjectId(), user, "project-profile-activity-reply")) {
      throw new PortletException("Unauthorized to add in this project");
    }

    // Validate the form
    if (StringUtils.hasText(reply.getDescription())) {
      // Insert the reply
      ProjectHistoryUtils.insertReply(db, projectHistory, reply, user);

      // Index the record
      PortalUtils.indexAddItem(request, projectHistory);

      // Trigger any workflow
      PortalUtils.processInsertHook(request, projectHistory);
    }

    // See if this is an AJAX response
    if ("text".equals(request.getParameter("out"))) {
      // Send a 200 response
      response.sendRedirect(response.encodeURL(request.getContextPath() + "/empty.html"));
      return null;
    } else {
      // Refresh the page
      return (PortalUtils.performRefresh(request, response, "/show"));
    }
  }
}
