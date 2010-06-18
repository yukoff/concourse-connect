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

import com.concursive.commons.web.mvc.beans.GenericBean;
import com.concursive.connect.web.modules.activity.dao.ProjectHistory;
import com.concursive.connect.web.modules.activity.dao.ProjectHistoryList;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import com.concursive.connect.web.portal.IPortletAction;
import static com.concursive.connect.web.portal.PortalUtils.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.WindowState;
import java.sql.Connection;

/**
 * Delete activity stream entries
 *
 * @author Nanda Kumar
 * @created December 11, 2009
 */
public class DeleteActivityAction implements IPortletAction {

  private static Log LOG = LogFactory.getLog(DeleteActivityAction.class);

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

    // Get the request parameter (try both since one is set in a drop-down menu)
    int id = -1;
    String idValue = request.getParameter("id");
    if (idValue != null) {
      id = Integer.parseInt(idValue);
    }

    // Determine the database connection
    Connection db = useConnection(request);

    // To avoid user confusion, always return a success
    try {

      // Load the record and delete it
      ProjectHistory projectHistory = new ProjectHistory(db, id);

      // Determine if the item can be deleted
      // @note MAINTAIN THESE CONDITIONS IN THE JSP TOO
      boolean canDelete = false;
      // Can the user delete the item?
      if (user.getAccessAdmin()) {
        // Admin can
        canDelete = true;
      } else if (projectHistory.getLinkObject().equals(ProjectHistoryList.ACTIVITY_ENTRY_OBJECT) ||
          projectHistory.getLinkObject().equals(ProjectHistoryList.SITE_CHATTER_OBJECT)) {
        if (user.getId() == projectHistory.getEnteredBy()) {
          // The user that created the user-entry can
          canDelete = true;
        } else if (ProjectUtils.hasAccess(projectHistory.getProjectId(), user, "project-profile-activity-delete")) {
          // Other users that have access can
          canDelete = true;
        }
      }
      if (projectHistory.getChildCount() > 0) {
        canDelete = false;
      }
      if (!canDelete) {
        throw new PortletException("Unauthorized to delete in this activity");
      }

      // Delete the history item
      projectHistory.delete(db);

      // Remove from index
      indexDeleteItem(request, projectHistory);

      // Trigger the workflow
      processDeleteHook(request, projectHistory);
    } catch (Exception e) {
      LOG.warn("projectHistory couldn't be deleted", e);
    }

    // See if this is an AJAX response
    if ("text".equals(request.getParameter("out"))) {
      // Send back the refreshed view
      response.setWindowState(WindowState.MAXIMIZED);
      response.setRenderParameter("out", "text");
      response.setRenderParameter("ajax", "true");
      response.setRenderParameter("limit", request.getParameter("limit"));
      response.setRenderParameter("offset", request.getParameter("offset"));
      response.setRenderParameter("streamType", request.getParameter("streamType"));
      return null;
      // Send an 200 response
//      response.sendRedirect(response.encodeURL(request.getContextPath() + "/empty.html"));
//      return null;
    } else {
      response.setWindowState(WindowState.NORMAL);
//      response.setRenderParameter("limit", request.getParameter("limit"));
//      response.setRenderParameter("offset", request.getParameter("offset"));
//      response.setRenderParameter("streamType", request.getParameter("streamType"));
      return null;
    }
  }

}
