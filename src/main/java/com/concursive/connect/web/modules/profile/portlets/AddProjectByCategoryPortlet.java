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

import com.concursive.commons.date.DateUtils;
import com.concursive.commons.objects.ObjectUtils;
import com.concursive.commons.text.StringUtils;
import com.concursive.connect.cache.utils.CacheUtils;
import com.concursive.connect.web.modules.calendar.dao.Meeting;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.members.dao.TeamMember;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.dao.ProjectCategory;
import com.concursive.connect.web.modules.profile.dao.ProjectCategoryList;
import com.concursive.connect.web.modules.profile.dao.ProjectFeatures;
import com.concursive.connect.web.portal.PortalUtils;
import com.concursive.connect.web.utils.LookupList;
import com.concursive.connect.web.utils.StateSelect;

import javax.portlet.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.Iterator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Add Project Portlet
 *
 * @author Kailash Bhoopalam
 * @created June 9, 2008
 */
public class AddProjectByCategoryPortlet extends GenericPortlet {

  private static final Log LOG = LogFactory.getLog(AddProjectByCategoryPortlet.class);

  // Pages
  private static final String VIEW_PAGE1 = "/portlets/add_project_by_category/add_project_by_category-add.jsp";
  private static final String VIEW_PAGE2 = "/portlets/add_project_by_category/add_project_by_category_message-view.jsp";

  // Preferences
  private static final String PREF_CATEGORY_NAME = "category";
  private static final String PREF_ALLOWED_CATEGORIES = "allowedCategories";
  private static final String PREF_TITLE = "title";
  private static final String PREF_INTRODUCTION_MESSAGE = "introductionMessage";
  private static final String PREF_IS_SUB_CATEGORY_MODIFIABLE = "showSubCategories";//true (or) false
  private static final String PREF_SUB_CATEGORY_NAME = "subCategoryName";
  private static final String PREF_SUCCESS_MESSAGE = "successMessage";
  private static final String PREF_FAILURE_MESSAGE = "failureMessage";
  private static final String PREF_ADD_USER_WITH_SPECIFIED_ROLE = "addUserWithRole";
  private static final String PREF_ADD_USER_AS_OWNER = "addUserAsOwner";
  private static final String PREF_ADD_USER_WITH_SPECIFIED_ROLE_IF_OWNER = "addOwnerUserWithRole";
  private static final String PREF_ALLOW_GUESTS = "allowGuests"; //true (or) false
  private static final String PREF_ALLOW_PARTICIPANTS = "allowParticipants"; //true (or) false
  private static final String PREF_SHOW_IS_OWNER = "showIsOwner"; //true (or) false
  private static final String PREF_SHOW_ALLOW_GUESTS_OPTION = "showAllowGuestsOption"; //true (or) false
  private static final String PREF_REQUIRES_MEMBERSHIP = "requiresMembership"; //true (or) false;
  private static final String PREF_SHOW_REQUIRES_MEMBERSHIP_OPTION = "showRequiresMembershipOption"; //true (or) false;
  private static final String PREF_MODULES = "modules"; //value array
  private static final String PREF_IS_APPROVED = "isApproved"; //value array
  private static final String PREF_SHOW_START_END_DATE_OPTION = "showStartEndDateOption";   // true (or) false
  private static final String PREF_REQUIRES_START_END_DATE = "requiresStartEndDate"; // true (or) false
  private static final String PREF_SPAWN_MEETING = "spawnMeeting"; // true (or) false
  private static final String PREF_SHOW_LOCATION_NAME = "showLocationName"; // true (or) false
  private static final String PREF_SHOW_ADDRESS = "showAddress"; // true (or) false
  private static final String PREF_SHOW_CONTACT_INFORMATION = "showContactInformation"; // true (or) false
  private static final String PREF_SHOW_SHORT_DESCRIPTION = "showShortDescription"; // true (or) false
  private static final String PREF_SHOW_LONG_DESCRIPTION = "showLongDescription"; // true (or) false
  private static final String PREF_SHOW_KEYWORDS = "showKeywords"; // true (or) false
  private static final String PREF_SHOW_WEBSITE = "showWebsite"; // true (or) false
  private static final String PREF_SHOW_SINGLE_IMAGE_ATTACHMENT = "showSingleImageAttachment"; // true (or) false
  private static final String PREF_REQUIRES_LONG_DESCRIPTION = "requiresLongDescription"; // true (or) false
  private static final String PREF_ALLOW_ONLY_IF_USER_CAN_START_PROJECTS = "allowOnlyIfUserCanStartProjects";

  // Attribute names for objects available in the view
  private static final String TITLE = "title";
  private static final String CATEGORY = "categoryName";
  private static final String ALLOWED_CATEGORY_LIST = "allowedCategoryList";
  private static final String SUB_CATEGORY = "subCategoryName";
  private static final String INTRODUCTION_MESSAGE = "introductionMessage";
  private static final String IS_SUB_CATEGORY_MODIFIABLE = "isSubCategoryModifiable";
  private static final String MESSAGE = "message";
  private static final String PROJECT = "project";
  private static final String SUB_CATEGORY_LIST = "subCategoryList";
  private static final String STATES = "states";
  private static final String SHOW_IS_OWNER = "showIsOwner"; //true (or) false
  private static final String SHOW_ALLOW_GUESTS_OPTION = "showAllowGuestsOption"; //true (or) false
  private static final String SHOW_REQUIRES_MEMBERSHIP_OPTION = "showRequiresMembershipOption"; //true (or) false;
  private static final String SHOW_START_END_DATE_OPTION = "showStartEndDateOption";   // true (or) false
  private static final String REQUIRES_START_END_DATE = "requiresStartEndDate"; // true (or) false
  private static final String USER = "user";
  private static final String SHOW_LOCATION_NAME = "showLocationName"; // true (or) false
  private static final String SHOW_ADDRESS = "showAddress"; // true (or) false
  private static final String SHOW_CONTACT_INFORMATION = "showContactInformation"; // true (or) false
  private static final String SHOW_SHORT_DESCRIPTION = "showShortDescription"; // true (or) false
  private static final String SHOW_LONG_DESCRIPTION = "showLongDescription"; // true (or) false
  private static final String SHOW_KEYWORDS = "showKeywords"; // true (or) false
  private static final String SHOW_WEBSITE = "showWebsite"; // true (or) false
  private static final String SHOW_SINGLE_IMAGE_ATTACHMENT = "showSingleImageAttachment"; // true (or) false

  public void doView(RenderRequest request, RenderResponse response)
      throws PortletException, IOException {
    try {
      String defaultView = VIEW_PAGE1;
      String viewType = request.getParameter("viewType");

      if (viewType == null) {
        viewType = (String) request.getPortletSession().getAttribute("viewType");
      }

      // Determine the user
      User user = PortalUtils.getUser(request);

      // Set global preferences
      request.setAttribute(TITLE, request.getPreferences().getValue(PREF_TITLE, null));
      request.setAttribute(INTRODUCTION_MESSAGE, request.getPreferences().getValue(PREF_INTRODUCTION_MESSAGE, null));
      request.setAttribute(IS_SUB_CATEGORY_MODIFIABLE, request.getPreferences().getValue(PREF_IS_SUB_CATEGORY_MODIFIABLE, "false"));
      request.setAttribute(SHOW_IS_OWNER, request.getPreferences().getValue(PREF_SHOW_IS_OWNER, "false"));
      request.setAttribute(SHOW_ALLOW_GUESTS_OPTION, request.getPreferences().getValue(PREF_SHOW_ALLOW_GUESTS_OPTION, "false"));
      request.setAttribute(SHOW_REQUIRES_MEMBERSHIP_OPTION, request.getPreferences().getValue(PREF_SHOW_REQUIRES_MEMBERSHIP_OPTION, "false"));
      request.setAttribute(SHOW_START_END_DATE_OPTION, request.getPreferences().getValue(PREF_SHOW_START_END_DATE_OPTION, "false"));
      request.setAttribute(REQUIRES_START_END_DATE, request.getPreferences().getValue(PREF_REQUIRES_START_END_DATE, "false"));
      request.setAttribute(USER, PortalUtils.getUser(request));
      request.setAttribute(SHOW_LOCATION_NAME, request.getPreferences().getValue(PREF_SHOW_LOCATION_NAME, "false"));
      request.setAttribute(SHOW_ADDRESS, request.getPreferences().getValue(PREF_SHOW_ADDRESS, "false"));
      request.setAttribute(SHOW_CONTACT_INFORMATION, request.getPreferences().getValue(PREF_SHOW_CONTACT_INFORMATION, "false"));
      request.setAttribute(SHOW_SHORT_DESCRIPTION, request.getPreferences().getValue(PREF_SHOW_SHORT_DESCRIPTION, "false"));
      request.setAttribute(SHOW_LONG_DESCRIPTION, request.getPreferences().getValue(PREF_SHOW_LONG_DESCRIPTION, "false"));
      request.setAttribute(SHOW_KEYWORDS, request.getPreferences().getValue(PREF_SHOW_KEYWORDS, "false"));
      request.setAttribute(SHOW_WEBSITE, request.getPreferences().getValue(PREF_SHOW_WEBSITE, "false"));
      request.setAttribute(SHOW_SINGLE_IMAGE_ATTACHMENT, request.getPreferences().getValue(PREF_SHOW_SINGLE_IMAGE_ATTACHMENT, "false"));
      request.setAttribute(SHOW_SINGLE_IMAGE_ATTACHMENT, request.getPreferences().getValue(PREF_SHOW_SINGLE_IMAGE_ATTACHMENT, "false"));

      // Verify the user has access if required
      boolean hasAccess = true;
      String allowOnlyIfUserCanStartProjects = request.getPreferences().getValue(PREF_ALLOW_ONLY_IF_USER_CAN_START_PROJECTS, "false");
      if ("true".equals(allowOnlyIfUserCanStartProjects)) {
        if (!user.getAccessAddProjects()) {
          hasAccess = false;
        }
      }

      if (!hasAccess) {
        defaultView = VIEW_PAGE2;
        request.setAttribute(MESSAGE, "You do not have access for adding projects, please contact an administrator.");
      } else {
        // Add custom labels to the request as "labelMap"
        PortalUtils.populateDisplayLabels(request);

        Project project = new Project();
        if ("saveFailure".equals(viewType)) {
          // Prep the form to show errors...
          project = (Project) request.getPortletSession().getAttribute(PROJECT);
          request.setAttribute(MESSAGE, request.getPreferences().getValue(PREF_FAILURE_MESSAGE, null));
          // Show the form with any errors provided
          PortalUtils.processErrors(request, project.getErrors());
          // Cleanup the session
          request.getPortletSession().removeAttribute("viewType");
          request.getPortletSession().removeAttribute(PROJECT);
        }

        if ("saveSuccess".equals(viewType)) {
          // Save Success
          request.setAttribute(MESSAGE, request.getPreferences().getValue(PREF_SUCCESS_MESSAGE, null));
          defaultView = VIEW_PAGE2;

          // Use the project on the results page
          project = (Project) request.getPortletSession().getAttribute(PROJECT);
          request.setAttribute(PROJECT, project);

          // This portlet can provide data to other portlets
          for (String event : PortalUtils.getDashboardPortlet(request).getGenerateDataEvents()) {
            PortalUtils.setGeneratedData(request, event, project);
          }
          // Clean up the session
          request.getPortletSession().removeAttribute(PROJECT);
          request.getPortletSession().removeAttribute("viewType");
        } else if ("getSubCategories".equals(viewType)) {
          Connection db = PortalUtils.getConnection(request);
          String categoryId = request.getParameter("category");
          ProjectCategoryList subCategories = new ProjectCategoryList();
          subCategories.setEnabled(true);
          subCategories.setParentCategoryId(categoryId != null ? Integer.parseInt(categoryId) : -1);
          subCategories.buildList(db);

          /**** sample json *****
           String responseText = "[" +
           "{ \"value\" : \"1\", \"text\" : \"Retail\" }," +
           "{ \"value\" : \"2\", \"text\" : \"Wholesale\" }]";
           */
          StringBuffer responseBuffer = new StringBuffer();
          responseBuffer.append("[");
          Iterator<ProjectCategory> subCatItr = subCategories.iterator();
          while (subCatItr.hasNext()) {
            ProjectCategory projectCategory = subCatItr.next();
            responseBuffer.append("{");
            responseBuffer.append(" \"value\" : \"");
            responseBuffer.append(String.valueOf(projectCategory.getId()));
            responseBuffer.append("\" , \"text\" :\"");
            responseBuffer.append(projectCategory.getDescription());
            responseBuffer.append("\" ");
            responseBuffer.append(" }");
            if (subCatItr.hasNext()) {
              responseBuffer.append(",");
            }
          }
          responseBuffer.append("]");
          response.setContentType("text/html");
          PrintWriter out = response.getWriter();
          out.println(responseBuffer.toString());
          out.flush();
          defaultView = null;
        } else {
          if (!user.isLoggedIn()) {
            // If user is not logged in, redirect
            defaultView = VIEW_PAGE2;
            request.setAttribute(MESSAGE, "You need to be logged in to perform this action");
          } else {
            // Show the add a project form, possibly partially filled out
            Connection db = PortalUtils.getConnection(request);

            // A list of possible categories
            ProjectCategoryList categories = new ProjectCategoryList();
            categories.setEnabled(true);
            categories.setTopLevelOnly(true);
            categories.buildList(db);

            // A specific category is enforced on the project
            String categoryValue = request.getPreferences().getValue(PREF_CATEGORY_NAME, null);
            ProjectCategory category = null;
            if (categoryValue != null) {
              category = categories.getFromValue(categoryValue);
              request.setAttribute(CATEGORY, category.getDescription());
              project.setCategoryId(category.getId());
            }
            // An option list of categories is presented and enforced
            ProjectCategoryList allowedCategoryList = new ProjectCategoryList();
            String allowedCategories = request.getPreferences().getValue(PREF_ALLOWED_CATEGORIES, null);
            if (allowedCategories != null) {
              String[] categoryArray = allowedCategories.split(",");
              for (String thisCategory : categoryArray) {
                ProjectCategory allowedCategory = categories.getFromValue(thisCategory.trim());
                if (allowedCategory != null) {
                  if (project.getCategoryId() == -1) {
                    project.setCategoryId(allowedCategory.getId());
                  }
                  allowedCategoryList.add(allowedCategory);
                }
              }
            }
            request.setAttribute(ALLOWED_CATEGORY_LIST, allowedCategoryList);

            //TODO: Needs more work here
            ProjectCategory subCategory = null;
            String subCategoryValue = request.getPreferences().getValue(PREF_SUB_CATEGORY_NAME, null);
            String isSubCategoryModifiable = request.getPreferences().getValue(PREF_IS_SUB_CATEGORY_MODIFIABLE, null);
            if ((isSubCategoryModifiable != null && "true".equals(isSubCategoryModifiable)) ||
                (subCategoryValue != null)) {
              ProjectCategoryList subCategories = new ProjectCategoryList();
              subCategories.setCategoryName(subCategoryValue);
              if (category != null) {
                subCategories.setParentCategoryId(category.getId());
              } else if (allowedCategoryList != null && allowedCategoryList.size() > 0) {
                subCategories.setParentCategoryId(allowedCategoryList.get(0).getId());
              }
              subCategories.setEnabled(true);
              subCategories.buildList(db);
              request.setAttribute(SUB_CATEGORY_LIST, subCategories);

              if (subCategoryValue != null) {
                subCategory = subCategories.getFromValue(subCategoryValue) != null ? subCategories.getFromValue(subCategoryValue) : null;
                request.setAttribute(SUB_CATEGORY, subCategory.getDescription());
              }
            }
            if (subCategory != null) {
              project.setSubCategory1Id(subCategory.getId());
            }

            if (project.getId() == -1 && project.getRequestDate() == null && project.getEstimatedCloseDate() == null) {
              Timestamp now = DateUtils.roundUpToNextFive();
              project.setRequestDate(now);
              project.setEstimatedCloseDate(now);
            }
            project.setCountry("UNITED STATES");
            request.setAttribute(PROJECT, project);

            StateSelect stateSelect = new StateSelect();
            stateSelect.clear();
            stateSelect.addItem(-1, "--None--");
            stateSelect.addStatesForUnitedStates();
            request.setAttribute(STATES, stateSelect);
          }
        }
      }

      // JSP view
      if (defaultView != null) {
        PortletContext context = getPortletContext();
        PortletRequestDispatcher requestDispatcher =
            context.getRequestDispatcher(defaultView);
        requestDispatcher.include(request, response);
      }
    } catch (Exception e) {
      e.printStackTrace();
      throw new PortletException(e);
    }
  }

  public void processAction(ActionRequest request, ActionResponse response)
      throws PortletException, IOException {
    try {
      // Determine the user
      User user = PortalUtils.getUser(request);
      // Verify the user has access if required
      boolean hasAccess = true;
      String allowOnlyIfUserCanStartProjects = request.getPreferences().getValue(PREF_ALLOW_ONLY_IF_USER_CAN_START_PROJECTS, "false");
      if ("true".equals(allowOnlyIfUserCanStartProjects)) {
        if (!user.getAccessAddProjects()) {
          hasAccess = false;
        }
      }
      if (hasAccess) {
        Project project = saveProject(request);
        if (project.getId() > 0) {
          request.getPortletSession().setAttribute("viewType", "saveSuccess");
          request.getPortletSession().setAttribute(PROJECT, project);
          boolean isSpawnMeeting = "true".equals(request.getPreferences().getValue(PREF_SPAWN_MEETING, "false"));
          if (isSpawnMeeting) {
            Meeting meeting = Meeting.createMeetingFromProject(project);
            meeting.insert(PortalUtils.getConnection(request));
          }
        } else {
          request.getPortletSession().setAttribute("viewType", "saveFailure");
          request.getPortletSession().setAttribute(PROJECT, project);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      throw new IOException("Exception in AddProjectByCategoryPortlet");
    }
  }


  /**
   * @param request
   * @return
   */
  private Project saveProject(ActionRequest request) throws Exception {
    Connection db = PortalUtils.getConnection(request);
    // Populate the project information
    Project project = new Project();
    PortalUtils.populateObject(project, request);
    // Override some values not present in the form
    if (project.getRequestDate() == null) {
      project.setRequestDate(new Timestamp(System.currentTimeMillis()));
    }
    project.setGroupId(PortalUtils.getUser(request).getGroupId());
    project.setEnteredBy(PortalUtils.getUser(request).getId());
    project.setModifiedBy(PortalUtils.getUser(request).getId());
    // Enforce any preferences
    ProjectCategoryList categories = new ProjectCategoryList();
    categories.setEnabled(true);
    categories.setTopLevelOnly(true);
    categories.buildList(db);
    // A specific category is enforced on the project
    String categoryValue = request.getPreferences().getValue(PREF_CATEGORY_NAME, null);
    ProjectCategory category = null;
    if (categoryValue != null) {
      category = categories.getFromValue(categoryValue);
      if (category != null) {
        project.setCategoryId(category.getId());
      } else {
        return project;
      }
    }
    String showShortDescription = request.getPreferences().getValue(SHOW_SHORT_DESCRIPTION, null);
    if (!"true".equals(showShortDescription)) {
      project.setShortDescription(project.getTitle());
    }
    // An option list of categories is presented and enforced
    String allowedCategories = request.getPreferences().getValue(PREF_ALLOWED_CATEGORIES, null);
    if (allowedCategories != null) {
      boolean categoryFound = false;
      String[] categoryArray = allowedCategories.split(",");
      for (String thisCategory : categoryArray) {
        ProjectCategory allowedCategory = categories.getFromValue(thisCategory.trim());
        if (allowedCategory != null && project.getCategoryId() == allowedCategory.getId()) {
          category = allowedCategory;
          categoryFound = true;
        }
      }
      if (!categoryFound) {
        return project;
      }
    }
    //check validity
    if (!isValid(request, project)) {
      return project;
    }
    project.setApproved(request.getPreferences().getValue(PREF_IS_APPROVED, "false"));

    // Ask if the user wants to be the owner...
    String showIsOwner = request.getPreferences().getValue(PREF_SHOW_IS_OWNER, "false");
    if ("true".equals(showIsOwner)) {
      if (project.getOwner() > -1) {
        // They said yes because the field is set to their id
        project.setOwner(PortalUtils.getUser(request).getId());
      }
    } else {
      // Reset the owner field...
      project.setOwner(-1);
    }
    // See if there is an override that forces the user to be the owner
    if ("true".equals(request.getPreferences().getValue(PREF_ADD_USER_AS_OWNER, "false"))) {
      project.setOwner(PortalUtils.getUser(request).getId());
    }
    if (project.getOwner() < -1) {
      project.setOwner(-1);
    }

    // Determines if guests are allowed to see the project
    project.getFeatures().setUpdateAllowGuests(true);
    if ("false".equals(request.getPreferences().getValue(PREF_SHOW_ALLOW_GUESTS_OPTION, "false"))) {
      project.getFeatures().setAllowGuests(request.getPreferences().getValue(PREF_ALLOW_GUESTS, "false"));
    }
    // Override the guests option if the site is private
    if (category.getSensitive()) {
      project.getFeatures().setAllowGuests(false);
    }

    // Determines if guests are promoted to participant if they are logged in
    project.getFeatures().setUpdateAllowParticipants(true);
    project.getFeatures().setAllowParticipants(request.getPreferences().getValue(PREF_ALLOW_PARTICIPANTS, "false"));

    // Determines if membership is required to see any data besides the project details
    project.getFeatures().setUpdateMembershipRequired(true);
    if ("false".equals(request.getPreferences().getValue(PREF_SHOW_REQUIRES_MEMBERSHIP_OPTION, "false"))) {
      project.getFeatures().setMembershipRequired(request.getPreferences().getValue(PREF_REQUIRES_MEMBERSHIP, "false"));
    }

    // RULE: If membership is required, downgrade allowing participants
    if (project.getFeatures().getMembershipRequired() && project.getFeatures().getAllowParticipants()) {
      project.getFeatures().setAllowParticipants(false);
    }

    // Set the instance id, if there is one
    project.setInstanceId(PortalUtils.getInstance(request).getId());

    // Insert the validated project
    boolean recordInserted = project.insert(db);
    if (recordInserted) {
      //save image attachments if any
      String showSingleAttachments = request.getPreferences().getValue(PREF_SHOW_SINGLE_IMAGE_ATTACHMENT, "false");
      if ("true".equals(showSingleAttachments)) {
        String attachmentList = request.getParameter("attachmentList");
        project.insertLogo(db, PortalUtils.getUser(request).getId(), PortalUtils.getFileLibraryPath(request, "projects"), attachmentList);
      }

      // Index the new project
      PortalUtils.indexAddItem(request, project);
      //Set order and label for allowed modules
      ProjectFeatures features = getFeaturesForProject(request);
      if (features != null) {
        features.setId(project.getId());
        features.setModifiedBy(PortalUtils.getUser(request).getId());
        features.update(db);
      }

      // Add the user to the project if specified...
      int userId = PortalUtils.getUser(request).getId();
      String userRole = null;
      // Test for the role
      userRole = request.getPreferences().getValue(PREF_ADD_USER_WITH_SPECIFIED_ROLE, null);
      // If an owner, then use any specified owner's role (or drop back to specified role)
      if (project.getOwner() == userId) {
        userRole = request.getPreferences().getValue(PREF_ADD_USER_WITH_SPECIFIED_ROLE_IF_OWNER, userRole);
      }
      if (userRole != null) {
        LookupList roleList = CacheUtils.getLookupList("lookup_project_role");
        int newRowLevel = roleList.getIdFromValue(userRole);
        if (newRowLevel == -1) {
          LOG.error("Could not insert team member with role: " + userRole);
        }
        TeamMember thisMember = new TeamMember();
        thisMember.setProjectId(project.getId());
        thisMember.setUserId(userId);
        thisMember.setUserLevel(newRowLevel);
        thisMember.setEnteredBy(PortalUtils.getUser(request).getId());
        thisMember.setModifiedBy(PortalUtils.getUser(request).getId());
        thisMember.insert(db);
      }
      // Send to the rules engine for any additional workflow
      PortalUtils.processInsertHook(request, project);
    } else {
      LOG.debug("Listing was not added -- Form did not validate");
    }
    return project;
  }

  private ProjectFeatures getFeaturesForProject(ActionRequest request) {
    String[] modulePreferences = request.getPreferences().getValues(PREF_MODULES, null);
    ProjectFeatures features = null;
    if (modulePreferences != null) {
      features = new ProjectFeatures();
      int numberOfModules = modulePreferences.length;
      int count = 0;
      while (count < numberOfModules) {
        String moduleName = null;
        String moduleLabel = null;
        String modulePreference = modulePreferences[count];
        if (modulePreference.indexOf("=") != -1) {
          moduleName = modulePreference.split("=")[0];
          moduleLabel = modulePreference.split("=")[1];
        } else {
          moduleName = modulePreference;
          moduleLabel = modulePreference;
        }
        ObjectUtils.setParam(features, "order" + moduleName, count + 1);
        ObjectUtils.setParam(features, "label" + moduleName, moduleLabel);
        ObjectUtils.setParam(features, "show" + moduleName, true);
        count++;
      }
    }
    return features;
  }

  private boolean isValid(ActionRequest request, Project project) {
    boolean isValid = true;
    boolean isStartEndDateRequired = "true".equals(request.getPreferences().getValue(PREF_REQUIRES_START_END_DATE, "false"));
    if (isStartEndDateRequired) {
      if (project.getRequestDate() == null || project.getEstimatedCloseDate() == null) {
        isValid = false;
      }
    }
    boolean isLongDescriptionRequired = "true".equals(request.getPreferences().getValue(PREF_REQUIRES_LONG_DESCRIPTION, "false"));
    if (isLongDescriptionRequired) {
      if (!StringUtils.hasText(project.getDescription())) {
        project.addError("descriptionError", "Description is required");
        isValid = false;
      }
    }
    return true;
  }
}
