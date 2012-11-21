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
package com.concursive.connect.web.modules.activity.portlets.activityInput;

import com.concursive.commons.text.StringUtils;
import com.concursive.commons.web.mvc.beans.GenericBean;
import com.concursive.connect.web.modules.activity.dao.ProjectHistory;
import com.concursive.connect.web.modules.activity.dao.ProjectHistoryList;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import com.concursive.connect.web.modules.wiki.utils.WikiLink;
import com.concursive.connect.web.modules.wiki.utils.WikiUtils;
import com.concursive.connect.web.portal.IPortletAction;
import com.concursive.connect.web.portal.PortalUtils;
import static com.concursive.connect.web.portal.PortalUtils.*;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import java.sql.Connection;
import java.sql.Timestamp;

/**
 * Action for saving a user's activity input
 *
 * @author Kailash Bhoopalam
 * @created March 31, 2008
 */
public class SaveActivityInputAction implements IPortletAction {

  // Preferences
  private static final String EVENT_TYPE = "event";
  private static final String ALLOW_USERS = "allowUsers";

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
    Connection db = useConnection(request);

    // Check the user's permissions
    if (Boolean.parseBoolean(request.getPreferences().getValue(ALLOW_USERS, "false"))) {
      if (!user.isLoggedIn()) {
        throw new PortletException("User needs to be logged in");
      }
    } else if (!ProjectUtils.hasAccess(project.getId(), user, "project-profile-activity-add")) {
      throw new PortletException("User does not have access to add activity");
    }

    // Determine the content to save
    String body = request.getParameter("body");

    // Validate the input
    if (StringUtils.hasText(body)) {

      // Turn content links into wiki links
      body = WikiUtils.addWikiLinks(body);

      ProjectHistory projectHistory = new ProjectHistory();
      projectHistory.setProjectId(project.getId());
      projectHistory.setEnabled(true);
      projectHistory.setEnteredBy(user.getId());
      projectHistory.setLinkStartDate(new Timestamp(System.currentTimeMillis()));

      // Determine the event type to save
      String eventType = request.getPreferences().getValue(EVENT_TYPE, null);
      if (eventType != null) {
        // ex. site-chatter
        projectHistory.setDescription(WikiLink.generateLink(user.getProfileProject()) + ": " + body);
        projectHistory.setLinkItemId(user.getId());
        projectHistory.setLinkObject(eventType);
      } else {
        if (project.getId() == user.getProfileProject().getId()) {
          // ex. on the user's profile
          projectHistory.setDescription(WikiLink.generateLink(user.getProfileProject()) + ": " + body);
        } else {
          // ex. on another profile
          projectHistory.setDescription(WikiLink.generateLink(user.getProfileProject()) + " @" + WikiLink.generateLink(project) + ": " + body);
        }
        projectHistory.setLinkItemId(project.getId());
        projectHistory.setLinkObject(ProjectHistoryList.ACTIVITY_ENTRY_OBJECT);
        projectHistory.setEventType(ProjectHistoryList.ADD_ACTIVITY_ENTRY_EVENT);
      }
      projectHistory.insert(db);
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
