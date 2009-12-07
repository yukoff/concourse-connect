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
import com.concursive.connect.web.modules.badges.dao.ProjectBadgeList;
import com.concursive.connect.web.modules.common.social.tagging.dao.TagList;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.profile.dao.*;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import com.concursive.connect.web.modules.wiki.dao.*;
import com.concursive.connect.web.modules.wiki.utils.CustomFormUtils;
import com.concursive.connect.web.portal.PortalUtils;
import com.concursive.connect.web.utils.PagedListInfo;

import javax.portlet.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Project profile portlet
 *
 * @author matt rajkowski
 * @created June 30, 2008
 */
public class ProjectProfilePortlet extends GenericPortlet {

  // Pages
  private static final String VIEW_PAGE = "/portlets/project_profile/project_profile-view.jsp";

  // Preferences
  private static final String PREF_TITLE = "title"; // true (title has link to project) false (no link)
  private static final String PREF_PROJECT_UNIQUE_ID = "project";
  private static final String PREF_WIKI_FORM_NAME = "wikiForm";
  private static final String PREF_WIKI_FORM_ATTRIBUTES = "wikiAttributes";
  private static final String PREF_LINKS = "links"; //value array; these preferences kick in of the user has access to the project's module
  private static final String PREF_SHOW_GET_INVOLVED = "showGetInvolved";
  private static final String PREF_SHOW_AUTHOR = "showAuthor";
  private static final String PREF_GET_INVOLVED_TEXT = "getInvolvedText"; //value array
  private static final String PREF_LINK_TITLE = "linkTitle"; // true (title has link to project) false (no link)
  private static final String PREF_HIDE_BASIC_INFORMATION = "hideBasicInformation"; // true (hides name, address, web site and edit link) false (shows name, address, web site and edit link)
  private static final String PREF_RANDOM_PROJECT = "randomProject"; //value array; these preferences are used to select a random project

  private static final String PREF_CATEGORY = "category"; //businesses, organizations, people, ideas, etc
  private static final String PREF_PUBLIC_ONLY = "publicOnly"; //true or false or null
  private static final String PREF_APPROVED_ONLY = "approvedOnly"; //true or false or null
  private static final String PREF_OPEN_PROJECTS_ONLY = "openProjectsOnly"; //true or false or null
  private static final String PREF_MINIMUM_AVERAGE_RATING = "minimumAverageRating"; // a number between 0 and 5
  private static final String PREF_DAYS_WITHOUT_BEING_FEATURED = "daysWithoutBeingFeatured"; // a number

  // Object Results
  private static final String TITLE = "title";
  private static final String PROJECT = "project";
  private static final String CURRENT_TEAM_MEMBER = "currentMember";
  private static final String PROJECT_SUBCATEGORY1 = "subCategory1";
  private static final String PROJECT_WIKI = "wiki";
  private static final String PROJECT_WIKI_FORM_ATTRIBUTES = "wikiAttributeList";
  private static final String PROJECT_TAG_LIST = "projectTagList";
  private static final String PROJECT_BADGE_LIST = "projectBadgeList";
  private static final String SHOW_GET_INVOLVED = "showGetInvolved";
  private static final String SHOW_AUTHOR = "showAuthor";
  private static final String GET_INVOLVED_TEXT = "getInvolvedText";
  private static final String PROJECT_TITLE_LINK = "projectTitleLink";
  private static final String HIDE_BASIC_INFORMATION = "hideBasicInformation";

  public void doView(RenderRequest request, RenderResponse response)
      throws PortletException, IOException {
    Connection db = null;
    try {
      db = PortalUtils.getConnection(request);

      String defaultView = VIEW_PAGE;

      Project project = null;

      //General Display Preferences
      String showGetInvolved = request.getPreferences().getValue(PREF_SHOW_GET_INVOLVED, "false");
      String showAuthor = request.getPreferences().getValue(PREF_SHOW_AUTHOR, "false");
      String getInvolvedText = request.getPreferences().getValue(PREF_GET_INVOLVED_TEXT, "Get Involved...");
      boolean isLinkTitle = "true".equals(request.getPreferences().getValue(PREF_LINK_TITLE, "false"));
      boolean hideBasicInformation = "true".equals(request.getPreferences().getValue(PREF_HIDE_BASIC_INFORMATION, "false"));
      String title = request.getPreferences().getValue(PREF_TITLE, "");

      request.setAttribute(SHOW_GET_INVOLVED, showGetInvolved);
      request.setAttribute(SHOW_AUTHOR, showAuthor);
      request.setAttribute(GET_INVOLVED_TEXT, getInvolvedText);
      request.setAttribute(HIDE_BASIC_INFORMATION, String.valueOf(hideBasicInformation));
      request.setAttribute(TITLE, title);

      // Build links for profile
      buildLinksForProfile(request);


      // Use the specified project in preferences first
      String uniqueId = request.getPreferences().getValue(PREF_PROJECT_UNIQUE_ID, null);
      if (uniqueId != null) {
        int projectId = ProjectUtils.retrieveProjectIdFromUniqueId(uniqueId);
        if (projectId > -1) {
          project = ProjectUtils.loadProject(projectId);
        }
      }

      // This portlet can consume data from other portlets
      if (project == null) {
        for (String event : PortalUtils.getDashboardPortlet(request).getConsumeDataEvents()) {
          project = (Project) PortalUtils.getGeneratedData(request, event);
        }
      }

      // Object from the portal
      if (project == null) {
        project = PortalUtils.getProject(request);
      }

      //Determine if there are random preferences to retrieve a project
      if (project == null) {
        project = getRandomProject(request, db);
      }

      // Check the project's permissions
      if (project != null) {
        User user = PortalUtils.getUser(request);
        if (!ProjectUtils.hasAccess(project.getId(), user, "project-profile-view")) {
          project = null;
        }
      }

      if (project != null) {
        // Add the project to this portlet's request scope
        request.setAttribute(PROJECT, project);

        // Add this user's membership level (to allow for edit links and such)
        request.setAttribute(CURRENT_TEAM_MEMBER, PortalUtils.getCurrentTeamMember(request));

        // Add a preference based on the project
        if (isLinkTitle) {
          request.setAttribute(PROJECT_TITLE_LINK, request.getContextPath() + "/show/" + project.getUniqueId());
        }

        // Get this project's photo

        // Get this project's sub-categories
        if (project.getSubCategory1Id() > -1) {
          ProjectCategory category = new ProjectCategory(db, project.getSubCategory1Id());
          request.setAttribute(PROJECT_SUBCATEGORY1, category);
        }

        // Get this project's specified wiki form
        String wikiFormName = request.getPreferences().getValue(PREF_WIKI_FORM_NAME, null);
        if (wikiFormName != null) {
          WikiList wikiList = new WikiList();
          wikiList.setProjectId(project.getId());
          wikiList.setWithFormName(wikiFormName);
          wikiList.buildList(db);
          if (wikiList.size() > 0) {
            Wiki wiki = wikiList.get(0);
            CustomForm wikiForm = CustomFormUtils.retrieveForm(wiki, wikiFormName);
            if (wikiForm != null) {
              request.setAttribute(PROJECT_WIKI, wiki);
              // Determine which fields to show, or all
              ArrayList<CustomFormField> attributeList = new ArrayList<CustomFormField>();
              String[] attributes = request.getPreferences().getValues(PREF_WIKI_FORM_ATTRIBUTES, null);
              if (attributes != null) {
                for (String name : attributes) {
                  CustomFormField field = wikiForm.getField(name);
                  if (field != null && field.hasValue()) {
                    attributeList.add(field);
                  }
                }
              } else {
                for (CustomFormGroup group : wikiForm) {
                  for (CustomFormField field : group) {
                    if (field.hasValue()) {
                      attributeList.add(field);
                    }
                  }
                }
              }
              request.setAttribute(PROJECT_WIKI_FORM_ATTRIBUTES, attributeList);

            }
          }
        }

        // Get this project's tag cloud
        TagList popularTagList = new TagList();
        popularTagList.setTableName(Project.TABLE);
        popularTagList.setUniqueField(Project.PRIMARY_KEY);
        popularTagList.setLinkItemId(project.getId());
        PagedListInfo tagListInfo = new PagedListInfo();
        tagListInfo.setColumnToSortBy("tag");
        popularTagList.setPagedListInfo(tagListInfo);
        popularTagList.setDetermineTagWeights(true);
        popularTagList.buildList(db);
        request.setAttribute(PROJECT_TAG_LIST, popularTagList);

        // Get this project's badges
        ProjectBadgeList projectBadgeList = new ProjectBadgeList();
        projectBadgeList.setProjectId(project.getId());
        projectBadgeList.buildList(db);
        request.setAttribute(PROJECT_BADGE_LIST, projectBadgeList);

        // Record view
        PortalUtils.processSelectHook(request, project);

        // JSP view
        PortletContext context = getPortletContext();
        PortletRequestDispatcher requestDispatcher =
            context.getRequestDispatcher(defaultView);
        requestDispatcher.include(request, response);
      }
    } catch (Exception e) {
      e.printStackTrace(System.out);
      throw new PortletException(e.getMessage());
    }
  }

  private void buildLinksForProfile(RenderRequest request) {
    String[] linkPreferences = request.getPreferences().getValues(PREF_LINKS, null);
    if (linkPreferences != null) {
      int numberOfLinks = linkPreferences.length;
      int count = 0;
      while (count < numberOfLinks) {
        String linkName = null;
        String linkLabel = null;
        String linkPreference = linkPreferences[count];
        if (linkPreference.indexOf("=") != -1) {
          linkName = linkPreference.split("=")[0];
          linkLabel = linkPreference.split("=")[1];
        } else {
          linkName = linkPreference;
          linkLabel = linkPreference;
        }
        request.setAttribute("label" + linkName, linkLabel);
        request.setAttribute("show" + linkName, "true");
        count++;
      }
    }
  }

  private HashMap<String, String> getRandomProjectPreferences(RenderRequest request) {
    HashMap<String, String> randomProjectPreference = null;
    String[] randomProjectPreferences = request.getPreferences().getValues(PREF_RANDOM_PROJECT, null);
    if (randomProjectPreferences != null) {
      randomProjectPreference = new HashMap<String, String>();
      int numberOfPreferences = randomProjectPreferences.length;
      int count = 0;
      while (count < numberOfPreferences) {
        String preferenceName = null;
        String preferenceValue = null;
        String linkPreference = randomProjectPreferences[count];
        if (linkPreference.indexOf("=") != -1) {
          preferenceName = linkPreference.split("=")[0];
          preferenceValue = linkPreference.split("=")[1];
          randomProjectPreference.put(preferenceName, preferenceValue);
        }
        count++;
      }
    }
    return randomProjectPreference;
  }

  /**
   * @param request
   * @param db
   * @return a random project based on preferences
   */
  private Project getRandomProject(RenderRequest request, Connection db) throws SQLException {
    Project randomProject = null;

    HashMap<String, String> randomProjectPreference = getRandomProjectPreferences(request);
    if (randomProjectPreference != null) {
      String portletKey = PortalUtils.getPortletUniqueKey(request);

      //Determine if the record for the key exists for today in the database
      ProjectFeaturedListingList projectFeaturedListingList = new ProjectFeaturedListingList();
      projectFeaturedListingList.setInstanceId(PortalUtils.getInstance(request).getId());
      projectFeaturedListingList.setPortletKey(portletKey);
      projectFeaturedListingList.setFeaturedDate(new Timestamp(System.currentTimeMillis()));
      projectFeaturedListingList.buildList(db);
      if (projectFeaturedListingList.size() == 1) {
        //if it exists load the project from projectUtils
        randomProject = ProjectUtils.loadProject(projectFeaturedListingList.get(0).getProjectId());
      } else {
        //if record does not exist, fetch the record from projectList based on randomProjectPreference
        //Need to take the preference of not having been viewed in the last x number of days
        String category = randomProjectPreference.get(PREF_CATEGORY);
        String publicOnly = randomProjectPreference.get(PREF_PUBLIC_ONLY);
        String approvedOnly = randomProjectPreference.get(PREF_APPROVED_ONLY);
        String openProjectsOnly = randomProjectPreference.get(PREF_OPEN_PROJECTS_ONLY);
        String minimumAverageRating = randomProjectPreference.get(PREF_MINIMUM_AVERAGE_RATING);
        String daysWithoutBeingFeatured = randomProjectPreference.get(PREF_DAYS_WITHOUT_BEING_FEATURED);

        ProjectList projectList = new ProjectList();
        projectList.setInstanceId(PortalUtils.getInstance(request).getId());
        //get categoryId for the provided category
        if (category != null) {
          ProjectCategoryList categories = new ProjectCategoryList();
          categories.setEnabled(true);
          categories.setTopLevelOnly(true);
          categories.setCategoryNameLowerCase(category.toLowerCase());
          categories.buildList(db);
          if (categories.size() > 0) {
            projectList.setCategoryId(categories.get(0).getId());
          }
        }

        // @todo leverage this in the ProjectList and incorporate into a sql condition
        //get the list of projects to exclude as they have been featured in the previously specified number of days
        if (daysWithoutBeingFeatured != null && StringUtils.isNumber(daysWithoutBeingFeatured)) {
          projectFeaturedListingList = new ProjectFeaturedListingList();
          projectFeaturedListingList.setPortletKey(portletKey);
          projectFeaturedListingList.setFeaturedSinceDate(new Timestamp(System.currentTimeMillis() - 1000L * 60 * 60 * 24 * Integer.parseInt(daysWithoutBeingFeatured)));
          projectFeaturedListingList.buildList(db);

          if (projectFeaturedListingList.size() > 0) {
            StringBuffer excludeProjectIdsStringBuffer = new StringBuffer();
            for (ProjectFeaturedListing projectFeaturedListing : projectFeaturedListingList) {
              excludeProjectIdsStringBuffer.append(projectFeaturedListing.getProjectId());
              excludeProjectIdsStringBuffer.append(",");
            }
            projectList.setExcludeProjectIdsString(excludeProjectIdsStringBuffer.toString());
          }
        }
        // Determine which projects can be shown
        if (publicOnly != null) {
          projectList.setPublicOnly("true".equals(publicOnly));
        }
        if (approvedOnly != null) {
          projectList.setApprovedOnly("true".equals(approvedOnly));
        }
        if (openProjectsOnly != null) {
          projectList.setOpenProjectsOnly("true".equals(openProjectsOnly));
        }
        if (minimumAverageRating != null) {
          projectList.setMinimumAverageRating(minimumAverageRating);
        }
        // Leave off the member based ones due to exposing images and such
        projectList.setRequiresMembership(false);

        PagedListInfo randomProjectListInfo = PortalUtils.getPagedListInfo(request, "randomProjectListInfo");

        //fetch a random project based on preference from the database
        randomProjectListInfo.setRandomOrder(true);
        randomProjectListInfo.setItemsPerPage(1);
        projectList.setPagedListInfo(randomProjectListInfo);
        projectList.buildList(db);

        //load the project and the preference in the database
        if (projectList.size() > 0) {
          randomProject = ProjectUtils.loadProject(projectList.get(0).getId());

          ProjectFeaturedListing projectFeaturedListing = new ProjectFeaturedListing();
          projectFeaturedListing.setPortletKey(portletKey);
          projectFeaturedListing.setProjectId(randomProject.getId());
          projectFeaturedListing.setFeaturedDate(new Timestamp(System.currentTimeMillis()));
          projectFeaturedListing.insert(db);
        }
      }
    }

    return randomProject;
  }
}