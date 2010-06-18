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

import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import com.concursive.connect.web.portal.IPortletViewer;
import com.concursive.connect.web.portal.PortalUtils;
import static com.concursive.connect.web.portal.PortalUtils.findProject;
import static com.concursive.connect.web.portal.PortalUtils.getUser;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

/**
 * Activity input form
 *
 * @author Kailash Bhoopalam
 * @created March 31, 2009
 */
public class ActivityInputForm implements IPortletViewer {
  // Pages
  private static final String VIEW_PAGE = "/portlets/activity_input/activity_input_form-view.jsp";

  // Preferences
  private static final String PREF_TITLE = "title";
  private static final String PREF_MESSAGE = "message";
  private static final String ALLOW_USERS = "allowUsers";

  // Object Results
  private static final String TITLE = "title";
  private static final String MESSAGE = "message";
  private static final String PROFILE = "profile";
  private static final String VIEWER_NAMESPACE = "namespace";

  public String doView(RenderRequest request, RenderResponse response) throws Exception {
    // General display preferences
    request.setAttribute(TITLE, request.getPreferences().getValue(PREF_TITLE, ""));
    request.setAttribute(MESSAGE, request.getPreferences().getValue(PREF_MESSAGE, null));

    // Determine the project to store the event against
    Project project = findProject(request);

    // Determine the profile to display next to the input
    Project profile = project;

    // Check the user's permissions
    User user = getUser(request);
    if (Boolean.parseBoolean(request.getPreferences().getValue(ALLOW_USERS, "false"))) {
      if (!user.isLoggedIn()) {
        return null;
      }
      // Since users are allowed to post, use their profile instead
      profile = user.getProfileProject();
    } else if (!ProjectUtils.hasAccess(project.getId(), user, "project-profile-activity-add")) {
      return null;
    }
    // Set the profile that will be displayed
    request.setAttribute(PROFILE, profile);

    // This portlet can consume data from other portlets
    for (String event : PortalUtils.getDashboardPortlet(request).getConsumeDataEvents()) {
      if (event.startsWith("namespace-")) {
        request.setAttribute(VIEWER_NAMESPACE, PortalUtils.getGeneratedData(request, event));
      }
    }

    return VIEW_PAGE;
  }
}
