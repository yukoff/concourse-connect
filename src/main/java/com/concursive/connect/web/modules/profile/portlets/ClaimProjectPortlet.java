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
package com.concursive.connect.web.modules.profile.portlets;

import com.concursive.commons.email.EmailUtils;
import com.concursive.commons.text.StringUtils;
import com.concursive.connect.cache.utils.CacheUtils;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.members.dao.TeamMember;
import com.concursive.connect.web.modules.members.dao.TeamMemberList;
import com.concursive.connect.web.modules.profile.beans.ProjectFormBean;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.portal.PortalUtils;
import com.concursive.connect.web.utils.LookupList;

import javax.portlet.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Claim Project Portlet
 *
 * @author Lorraine Bittner
 * @created July 31, 2008
 */
public class ClaimProjectPortlet extends GenericPortlet {

  // Pages
  private static final String FORM_PAGE = "/portlets/claim_project/claim_project-edit.jsp";
  private static final String MESSAGE_PAGE = "/portlets/claim_project/claim_project_message-view.jsp";
  private static final String CLOSE_PAGE = "/portlets/claim_project/claim_project-refresh.jsp";

  // Preferences
  private static final String PREF_TITLE = "title";
  private static final String PREF_INTRODUCTION_MESSAGE = "introductionMessage";
  private static final String PREF_SUCCESS_MESSAGE = "successMessage";

  // Attribute names for objects available in the view
  private static final String TITLE = "title";
  private static final String INTRODUCTION_MESSAGE = "introductionMessage";
  private static final String SUCCESS_MESSAGE = "successMessage";
  private static final String ERROR_MESSAGE = "errorMessage";
  private static final String CLAIM = "claim";

  private static final String VIEW_TYPE = "viewType";
  private static final String SAVE_SUCCESS = "saveSuccess";
  private static final String CLOSE = "close";

  public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {

    // Manage the form bean and make available to the request (do this first)
    PortalUtils.processFormBean(request);

    // Set global preferences
    request.setAttribute(TITLE, request.getPreferences().getValue(PREF_TITLE, null));
    request.setAttribute(INTRODUCTION_MESSAGE, request.getPreferences().getValue(PREF_INTRODUCTION_MESSAGE, null));

    // Retrieve objects from the page and populate in the request automatically
    Project project = PortalUtils.findProject(request);
    User user = PortalUtils.getUser(request);

    // Determine the view to use...
    String viewType = request.getParameter(VIEW_TYPE);
    String returnView = null;
    if (CLOSE.equals(viewType)) {
      // The panel is being closed, so the listing view will be refreshed
      returnView = CLOSE_PAGE;
    } else if (SAVE_SUCCESS.equals(viewType)) {
      // The form was successfully saved
      request.setAttribute(SUCCESS_MESSAGE, request.getPreferences().getValue(PREF_SUCCESS_MESSAGE, null));
      returnView = MESSAGE_PAGE;
    } else if (!user.isLoggedIn()) {
      // If user is not logged in, show error
      returnView = MESSAGE_PAGE;
      request.setAttribute(ERROR_MESSAGE, "You need to be logged in to perform this action");
    } else if (project == null || project.getId() == -1) {
      // If project is invalid, show error
      request.setAttribute(ERROR_MESSAGE, "No project was specified");
      returnView = MESSAGE_PAGE;
    } else if (project.getOwner() > -1) {
      // If project is already claimed, show error
      request.setAttribute(ERROR_MESSAGE, "This listing has a claim already pending");
      returnView = MESSAGE_PAGE;
    }

    // If there is no return view, then display the form by default
    if (returnView == null) {
      // Check the request for the record
      ProjectFormBean claimForm = (ProjectFormBean) PortalUtils.getFormBean(request, CLAIM, ProjectFormBean.class);
      // Prepare the claim project form
      claimForm.setProjectId(project.getId());
      claimForm.setUniqueId(project.getUniqueId());
      claimForm.setProjectTitle(project.getTitle());
      claimForm.setFirstName(user.getFirstName());
      claimForm.setLastName(user.getLastName());
      if (claimForm.getEmail() == null) {
        claimForm.setEmail(user.getEmail());
      }
      request.setAttribute(CLAIM, claimForm);
      returnView = FORM_PAGE;
    }

    // Show the view
    PortletContext context = getPortletContext();
    PortletRequestDispatcher requestDispatcher = context.getRequestDispatcher(returnView);
    requestDispatcher.include(request, response);
  }


  public void processAction(ActionRequest request, ActionResponse response) throws PortletException, IOException {
    // Determine if this is a close form request
    boolean isClose = "true".equals(request.getParameter("close"));
    if (isClose) {
      response.setRenderParameter(VIEW_TYPE, CLOSE);
    } else {
      // Retrieve objects from the page
      Project project = PortalUtils.findProject(request);
      User user = PortalUtils.getUser(request);

      // Determine permissions
      if (!user.isLoggedIn()) {
        throw new PortletException("You need to be logged in to perform this action");
      }
      if (project == null || project.getId() == -1) {
        throw new PortletException("Invalid project specified");
      }

      // Process the claim
      try {
        boolean isSuccess = claimProject(project, user, request);
        if (isSuccess) {
          response.setRenderParameter(VIEW_TYPE, SAVE_SUCCESS);
        }
      } catch (SQLException e) {
        e.printStackTrace();
        throw new RuntimeException(e);
      }
    }
  }

  private boolean claimProject(Project project, User user, ActionRequest request) throws SQLException {
    // Process the form
    ProjectFormBean claimForm = new ProjectFormBean();
    // Request parameters
    claimForm.setEmail(request.getParameter("email"));
    claimForm.setIsOwner(request.getParameter("owner"));
    claimForm.setComments(request.getParameter("comments"));
    // Generated parameters
    claimForm.setUserId(user.getId());
    claimForm.setFirstName(user.getFirstName());
    claimForm.setLastName(user.getLastName());
    claimForm.setProjectId(project.getId());
    claimForm.setProjectTitle(project.getTitle());
    claimForm.setAddressLine1(project.getAddressLine1());
    claimForm.setAddressLine2(project.getAddressLine2());
    claimForm.setAddressLine3(project.getAddressLine3());
    claimForm.setCity(project.getCity());
    claimForm.setState(project.getState());
    claimForm.setPostalCode(project.getPostalCode());
    claimForm.setCountry(project.getCountry());
    claimForm.setWebPage(project.getWebPage());
    claimForm.setPhone(project.getBusinessPhone());
    claimForm.setFax(project.getBusinessFax());
    claimForm.setUniqueId(project.getUniqueId());
    // Form validation
    if (!claimForm.getIsOwner()) {
      claimForm.addError("verifyError", "Verification of ownership is required");
    }
    if (!StringUtils.hasText(claimForm.getEmail())) {
      claimForm.addError("emailError", "Email is required");
    } else {
      boolean validEmail = EmailUtils.checkEmail(claimForm.getEmail());
      if (!validEmail) {
        claimForm.addError("emailError", "A valid email address is required");
      }
    }
    // Record validation
    if (project.getOwner() > -1) {
      claimForm.addError("actionError", "This listing has a claim already pending");
    }

    if (claimForm.hasErrors()) {
      // Need to add the form bean to the portlet session because the portal
      // uses redirects which voids the request; the request will immediately
      // remove the bean from the session
      PortalUtils.setFormBean(request, claimForm);
      return false;
    }

    // Determine the database connection to use
    Connection db = PortalUtils.getConnection(request);

    // 1. Save the user as the new owner
    int resultCount = claimForm.saveProjectOwner(db);
    // If there is a save error, let the view know
    if (resultCount != 1) {
      return false;
    } else {
      // 2. If user is not currently a team member, then create a member with ROLE = MEMBER
      TeamMemberList members = new TeamMemberList();
      members.setProjectId(project.getId());
      members.setUserId(user.getId());
      members.buildList(db);
      if (members.isEmpty()) {
        LookupList roleList = CacheUtils.getLookupList("lookup_project_role");
        int roleLevel = roleList.getIdFromLevel(TeamMember.MEMBER);
        TeamMember thisMember = new TeamMember();
        thisMember.setProjectId(project.getId());
        thisMember.setUserId(user.getId());
        thisMember.setUserLevel(roleLevel);
        thisMember.setEnteredBy(user.getId());
        thisMember.setModifiedBy(user.getId());
        thisMember.insert(db);
      }

    }
    // Trigger any workflow
    PortalUtils.processInsertHook(request, claimForm);
    return true;
  }

}