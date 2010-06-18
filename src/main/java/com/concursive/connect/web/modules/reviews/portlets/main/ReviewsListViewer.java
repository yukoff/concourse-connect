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
package com.concursive.connect.web.modules.reviews.portlets.main;

import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import com.concursive.connect.web.modules.reviews.dao.ProjectRatingList;
import com.concursive.connect.web.portal.IPortletViewer;
import static com.concursive.connect.web.portal.PortalUtils.*;
import com.concursive.connect.web.utils.PagedListInfo;
import com.concursive.connect.Constants;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import java.sql.Connection;

/**
 * Project reviews list
 *
 * @author matt rajkowski
 * @created October 17, 2008
 */
public class ReviewsListViewer implements IPortletViewer {

  // Pages
  private static final String VIEW_PAGE = "/projects_center_reviews.jsp";

  // Preferences
  private static final String PREF_TITLE = "title";

  // Object Results
  private static final String TITLE = "title";
  private static final String PROJECT_RATING_LIST = "projectRatingList";
  private static final String USER_REVIEW_ID = "userReviewId";
  private static final String PAGED_LIST_INFO = "projectReviewInfo";

  public String doView(RenderRequest request, RenderResponse response) throws Exception {
    // The JSP to show upon success
    String defaultView = VIEW_PAGE;

    // General display preferences
    request.setAttribute(TITLE, request.getPreferences().getValue(PREF_TITLE, "Review"));

    // Determine the project container to use
    Project project = findProject(request);

    // Check the user's permissions
    User user = getUser(request);
    if (!ProjectUtils.hasAccess(project.getId(), user, "project-reviews-view")) {
      throw new PortletException("Unauthorized to view in this project");
    }

    // Paging will be used for remembering several list view settings
    PagedListInfo pagedListInfo = getPagedListInfo(request, PAGED_LIST_INFO);
    pagedListInfo.setItemsPerPage(-1);
    String sortOrder = getPageView(request);
    if ("recent".equals(sortOrder)) {
      pagedListInfo.setColumnToSortBy("modified");
      pagedListInfo.setSortOrder("desc");
    } else {
      pagedListInfo.setColumnToSortBy("rating_value");
      pagedListInfo.setSortOrder("desc");
    }

    // Determine the database connection
    Connection db = useConnection(request);

    // Load the records
    ProjectRatingList projectRatings = new ProjectRatingList();
    projectRatings.setPagedListInfo(pagedListInfo);
    projectRatings.setProjectId(project.getId());
    projectRatings.buildList(db);
    request.setAttribute(PROJECT_RATING_LIST, projectRatings);

    int userReviewId = projectRatings.getReviewIdByUser(getUser(request).getId());
    request.setAttribute(USER_REVIEW_ID, String.valueOf(userReviewId));

    // TODO: Show that a reviews list has been viewed

    // JSP view
    return defaultView;
  }
}
