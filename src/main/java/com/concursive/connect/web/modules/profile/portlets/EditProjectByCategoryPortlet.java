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

import com.concursive.commons.text.StringUtils;
import com.concursive.connect.web.modules.calendar.dao.Meeting;
import com.concursive.connect.web.modules.calendar.dao.MeetingList;
import com.concursive.connect.web.modules.profile.beans.ProjectFormBean;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.dao.ProjectCategoryList;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import com.concursive.connect.web.portal.PortalUtils;
import com.concursive.connect.web.utils.CountrySelect;
import com.concursive.connect.web.utils.PagedListInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.portlet.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * Edit Project By Category Portlet
 *
 * @author Kailash Bhoopalam
 * @created October 14, 2008
 */
public class EditProjectByCategoryPortlet extends GenericPortlet {

  private static Log LOG = LogFactory.getLog(EditProjectByCategoryPortlet.class);

  // Pages
  private static final String EDIT_PAGE = "/portlets/edit_project_by_category/project_by_category-edit.jsp";
  private static final String CLOSE_PAGE = "/portlets/edit_project_by_category/project_by_category-refresh.jsp";

  // Preferences
  private static final String PREF_TITLE = "title";
  private static final String PREF_FAILURE_MESSAGE = "failureMessage";
  private static final String PREF_SPAWN_MEETING = "spawnMeeting"; // true (or) false
  private static final String PREF_REQUIRES_START_END_DATE = "requiresStartEndDate"; // true (or) false
  private static final String PREF_REQUIRES_LONG_DESCRIPTION = "requiresLongDescription"; // true (or) false

  // Attribute names for objects available in the view
  private static final String TITLE = "title";
  private static final String ERROR_MESSAGE = "errorMessage";
  private static final String PROJECT_BEAN = "projectBean";
  private static final String PROJECT = "projectToEdit";
  private static final String USER = "user";
  private static final String SUB_CATEGORY_LIST = "subCategoryList";
  private static final String COUNTRY_LIST = "countryList";
  private static final String PREFERENCE_MAP = "preferenceMap";
  private static final String DEFAULT_COUNTRY = "defaultCountry";
  private static final String HAS_PROJECT_ACCESS = "hasProjectAccess";

  private static final String VIEW_TYPE = "viewType";
  private static final String SAVE_FAILURE = "saveFailure";
  private static final String CLOSE = "close";

  public void doView(RenderRequest request, RenderResponse response)
      throws PortletException, IOException {
    try {
      String defaultView = EDIT_PAGE;
      String viewType = request.getParameter("viewType");
      if (viewType == null) {
        viewType = (String) request.getPortletSession().getAttribute("viewType");
      }
      // Set global preferences
      request.setAttribute(TITLE, request.getPreferences().getValue(PREF_TITLE, null));
      request.setAttribute(USER, PortalUtils.getUser(request));

      //Test if the user has access to the project 
      if (!ProjectUtils.hasAccess(PortalUtils.getProject(request).getId(), PortalUtils.getUser(request), "project-profile-admin")) {
        request.setAttribute(HAS_PROJECT_ACCESS, "false");
      } else if (CLOSE.equals(viewType)) {
        Project project = (Project) request.getPortletSession().getAttribute(PROJECT);
        request.setAttribute(PROJECT, project);

        // Cleanup other session attributes
        request.getPortletSession().removeAttribute(PROJECT);
        request.getPortletSession().removeAttribute(PROJECT_BEAN);
        request.getPortletSession().removeAttribute(VIEW_TYPE);

        defaultView = CLOSE_PAGE;
      } else if (SAVE_FAILURE.equals(viewType)) {
        Connection db = PortalUtils.useConnection(request);
        //Build country list
        CountrySelect countrySelect = new CountrySelect();
        request.setAttribute(COUNTRY_LIST, countrySelect);

        // Show the form with any errors provided
        request.setAttribute(ERROR_MESSAGE, request.getPreferences().getValue(PREF_FAILURE_MESSAGE, null));

        ProjectFormBean projectBean = (ProjectFormBean) request.getPortletSession().getAttribute(PROJECT_BEAN);
        request.setAttribute(PROJECT_BEAN, projectBean);
        request.getPortletSession().removeAttribute(PROJECT_BEAN);

        Project project = (Project) request.getPortletSession().getAttribute(PROJECT);
        if (project != null) {
          PortalUtils.processErrors(request, project.getErrors());
          request.setAttribute(PROJECT, project);
          request.getPortletSession().removeAttribute(PROJECT);

          // Build display preferences for editing
          String categoryName = getProjectCategoryName(db, project);
          if (StringUtils.hasText(categoryName)) {
            request.setAttribute("preferenceMap", getDisplayPreferences(request, categoryName));
          }
          //Build sub category list for the category
          if (project.getCategoryId() != -1) {
            ProjectCategoryList projectSubCategoryList = getSubCategoryList(db,
                project);
            request.setAttribute(SUB_CATEGORY_LIST, projectSubCategoryList);
          }
        }
        request.getPortletSession().removeAttribute(VIEW_TYPE);
      } else {
        Project project = PortalUtils.getProject(request);
        request.setAttribute(PROJECT, project);
        int projectId = project == null ? -1 : project.getId();

        if (projectId <= 0) {
          request.setAttribute(ERROR_MESSAGE, "No project was specified");
        } else {
          ProjectFormBean projectBean = new ProjectFormBean();
          projectBean.buildBeanFromProject(project);
          request.setAttribute(PROJECT_BEAN, projectBean);
          Connection db = PortalUtils.useConnection(request);

          // Build display preferences for editing
          String categoryName = getProjectCategoryName(db, project);
          if (StringUtils.hasText(categoryName)) {
            LOG.debug("Finding display preferences for: " + categoryName);
            HashMap<String, String> preferenceMap = getDisplayPreferences(request, categoryName);
            request.setAttribute(PREFERENCE_MAP, preferenceMap);
          }
          //Build sub category list for the category
          if (project.getCategoryId() != -1) {
            ProjectCategoryList projectSubCategoryList = getSubCategoryList(db,
                project);
            request.setAttribute(SUB_CATEGORY_LIST, projectSubCategoryList);
          }
          //Build country list
          CountrySelect countrySelect = new CountrySelect();
          request.setAttribute(COUNTRY_LIST, countrySelect);

          //Get default country
          request.setAttribute(DEFAULT_COUNTRY, "UNITED STATES");
        }
      }
      PortletContext context = getPortletContext();
      PortletRequestDispatcher requestDispatcher =
          context.getRequestDispatcher(defaultView);
      requestDispatcher.include(request, response);
    } catch (Exception e) {
      LOG.error("view", e);
      throw new PortletException(e);
    }
  }

  /**
   * @param db
   * @param project
   * @return
   * @throws SQLException
   */
  private ProjectCategoryList getSubCategoryList(Connection db, Project project)
      throws SQLException {
    ProjectCategoryList projectSubCategoryList = new ProjectCategoryList();
    projectSubCategoryList.setParentCategoryId(project.getCategoryId());
    projectSubCategoryList.setEnabled(true);
    projectSubCategoryList.buildList(db);
    return projectSubCategoryList;
  }

  /**
   * @param db
   * @param project
   * @return the project's category name
   * @throws SQLException
   */
  private String getProjectCategoryName(Connection db, Project project) throws SQLException {
    String categoryName = null;
    ProjectCategoryList projectCategoryList = new ProjectCategoryList();
    projectCategoryList.setEnabled(true);
    projectCategoryList.setTopLevelOnly(true);
    projectCategoryList.buildList(db);
    categoryName = projectCategoryList.getValueFromId(project.getCategoryId());
    return categoryName;
  }

  public void processAction(ActionRequest request, ActionResponse response)
      throws PortletException, IOException {

    try {
      if (!ProjectUtils.hasAccess(PortalUtils.getProject(request).getId(), PortalUtils.getUser(request), "project-profile-admin")) {
        request.setAttribute(HAS_PROJECT_ACCESS, "false");
      } else {
        Project updatedProject = updateProject(request);
        if (updatedProject == null) {
          request.getPortletSession().setAttribute(VIEW_TYPE, SAVE_FAILURE);
        } else {
          if ("true".equals(request.getAttribute("popup")) || "true".equals(request.getParameter("popup"))) {
            // Use the popup to create the redirect
            request.getPortletSession().setAttribute(VIEW_TYPE, CLOSE);
            request.getPortletSession().setAttribute(PROJECT, updatedProject);
          } else {
            // Redirect now
            // Cleanup other session attributes
            request.getPortletSession().removeAttribute(PROJECT);
            request.getPortletSession().removeAttribute(PROJECT_BEAN);
            request.getPortletSession().removeAttribute(VIEW_TYPE);
            // Redirect to the project home page
            String ctx = request.getContextPath();
            response.sendRedirect(ctx + "/show/" + updatedProject.getUniqueId());
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

  private Project updateProject(ActionRequest request) throws Exception {
    int saved = -1;
    Connection db = null;
    db = PortalUtils.useConnection(request);
    // Load the previous state of the project for comparison
    Project prevProject = new Project(db, PortalUtils.getProject(request).getId());

    // Load the project to make changes to it
    Project projectToEdit = new Project(db, PortalUtils.getProject(request).getId());

    // Populate the updated project
    ProjectFormBean projectBean = new ProjectFormBean();
    PortalUtils.populateObject(projectBean, request);

    //Check required fields enforced by preferences
    String categoryName = getProjectCategoryName(db, projectToEdit);
    HashMap<String, String> preferenceMap = getDisplayPreferences(request, categoryName);

    // Populate project based on portlet preferences
    projectBean.populateProjectFromBeanBasedOnPreferences(preferenceMap, projectToEdit);

    // Update who modified the project
    projectToEdit.setModifiedBy(PortalUtils.getUser(request).getId());

    if (isValid(request, projectToEdit, preferenceMap)) {
      saved = projectToEdit.update(db);
      if (saved != -1) {
        // Save as meeting based on preferences
        boolean isSpawnMeeting = (preferenceMap.get(PREF_SPAWN_MEETING) != null && "true".equals(preferenceMap.get(PREF_SPAWN_MEETING))) ? true : false;
        if (isSpawnMeeting) {
          // Update an existing meeting corresponding to the event if it exists, else create a new one
          MeetingList meetingList = new MeetingList();
          PagedListInfo meetingListInfo = new PagedListInfo();
          meetingListInfo.setColumnToSortBy("m.modified");
          meetingList.setPagedListInfo(meetingListInfo);
          meetingList.setProjectId(projectToEdit.getId());
          meetingList.buildList(db);
          Meeting originalMeeting = null;
          if (meetingList.size() > 0) {
            // Get the latest record
            originalMeeting = meetingList.get(meetingList.size() - 1);
          }
          Meeting meeting = Meeting.createMeetingFromProject(projectToEdit);
          if (originalMeeting == null) {
            meeting.insert(db);
          } else {
            meeting.setId(originalMeeting.getId());
            meeting.setModified(originalMeeting.getModified());
            meeting.update(db);
          }
        }
      }
    }
    request.getPortletSession().setAttribute(PROJECT_BEAN, projectBean);
    if (saved == -1) {
      //set project to the session so that errors can be displayed in the form
      request.getPortletSession().setAttribute(PROJECT, projectToEdit);
      return null;
    } else {
      PortalUtils.indexAddItem(request, projectToEdit);
      PortalUtils.processUpdateHook(request, prevProject, projectToEdit);
    }
    return projectToEdit;
  }

  private HashMap<String, String> getDisplayPreferences(PortletRequest request, String categoryName) {
    String catName = categoryName.trim().toLowerCase();
    String[] displayPreferences = request.getPreferences().getValues(catName, null);
    HashMap<String, String> preferenceMap = new HashMap<String, String>();
    if (displayPreferences != null) {
      int numberOfFields = displayPreferences.length;
      int count = 0;
      while (count < numberOfFields) {
        String label = null;
        String value = null;
        String displayPreference = displayPreferences[count];
        if (displayPreference.indexOf("=") != -1) {
          label = displayPreference.split("=")[0];
          value = displayPreference.split("=")[1];
        } else {
          label = displayPreference;
          value = displayPreference;
        }
        preferenceMap.put(label, value);
        count++;
      }
    }
    return preferenceMap;
  }

  private boolean isValid(ActionRequest request, Project project, HashMap<String, String> preferenceMap) {
    boolean isValid = true;
    boolean isStartEndDateRequired = (preferenceMap.get(PREF_REQUIRES_START_END_DATE) != null && "true".equals(preferenceMap.get(PREF_REQUIRES_START_END_DATE))) ? true : false;
    if (isStartEndDateRequired) {
      if (project.getRequestDate() == null || project.getEstimatedCloseDate() == null) {
        if (project.getRequestDate() == null) {
          project.addError("requestDateError", "Date is required");
        }
        if (project.getEstimatedCloseDate() == null) {
          project.addError("estimatedCloseDateError", "Date is required");
        }
        isValid = false;
      }
    }
    boolean isLongDescriptionRequired = (preferenceMap.get(PREF_REQUIRES_LONG_DESCRIPTION) != null && "true".equals(preferenceMap.get(PREF_REQUIRES_LONG_DESCRIPTION))) ? true : false;
    if (isLongDescriptionRequired) {
      if (!StringUtils.hasText(project.getDescription())) {
        project.addError("descriptionError", "Description is required");
        isValid = false;
      }
    }
    return isValid;
  }
}
