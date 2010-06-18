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
package com.concursive.connect.web.modules.classifieds.portlets.main;

import com.concursive.connect.Constants;
import com.concursive.connect.web.modules.classifieds.dao.ClassifiedList;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import com.concursive.connect.web.portal.IPortletViewer;
import com.concursive.connect.web.portal.PortalUtils;
import static com.concursive.connect.web.portal.PortalUtils.*;
import com.concursive.connect.web.utils.PagedListInfo;

import javax.portlet.PortletException;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import java.sql.Connection;

/**
 * Project classifieds list
 *
 * @author matt rajkowski
 * @created November 18, 2008
 */
public class ClassifiedsListViewer implements IPortletViewer {

  // Pages
  private static final String VIEW_PAGE = "/projects_center_classifieds.jsp";

  // Object Results
  private static final String CLASSIFIED_LIST = "classifiedList";
  private static final String PAGED_LIST_INFO = "projectClassifiedsInfo";

  public String doView(RenderRequest request, RenderResponse response) throws Exception {
    // The JSP to show upon success
    String defaultView = VIEW_PAGE;

    // Determine the project container to use
    Project project = findProject(request);

    // Check the user's permissions
    User user = getUser(request);
    if (!ProjectUtils.hasAccess(project.getId(), user, "project-classifieds-view")) {
      throw new PortletException("Unauthorized to view in this project");
    }

    // Determine the database connection to use
    Connection db = useConnection(request);

    // Determine the paging url
    PortletURL renderURL = response.createRenderURL();
    renderURL.setParameter("portlet-action", "show");
    renderURL.setParameter("portlet-object", "classifieds");
    String url = renderURL.toString();

    // Paging will be used for remembering several list view settings
    PagedListInfo pagedListInfo = getPagedListInfo(request, PAGED_LIST_INFO);
    pagedListInfo.setLink(url);

    // Load the records
    ClassifiedList classifiedList = new ClassifiedList();
    classifiedList.setProjectId(project.getId());
    classifiedList.setPagedListInfo(pagedListInfo);
    // Limit the records displayed based on access
    if ("archived".equals(pagedListInfo.getListView()) && ProjectUtils.hasAccess(project.getId(), user, "project-classifieds-add")) {
      //classifiedList.setArchived(Constants.TRUE);
    } else if ("unreleased".equals(pagedListInfo.getListView()) && ProjectUtils.hasAccess(project.getId(), user, "project-classifieds-add")) {
      //classifiedList.setUnreleased(Constants.TRUE);
    } else if ("unpublished".equals(pagedListInfo.getListView()) && ProjectUtils.hasAccess(project.getId(), user, "project-classifieds-add")) {
      classifiedList.setDraft(Constants.TRUE);
    } else {
      if (ProjectUtils.hasAccess(project.getId(), user, "project-classifieds-add")) {
        // show ALL records
        //classifiedList.setOverviewAll(true);
      } else {
        // show CURRENT records
        classifiedList.setPublished(Constants.TRUE);
      }
    }
    // Set the sort order
    pagedListInfo.setColumnToSortBy("expiration_date asc, publish_date desc");

    // Determine the current view
    String filter = getPageView(request);
    String filterParam = getPageParameter(request);
    // By Category
    if ("category".equals(filter)) {
      if ("-1".equals(filterParam)) {
        classifiedList.setCheckNullCategoryId(true);
      } else {
        classifiedList.setCategoryId(filterParam);
      }
    }
    // By Author
    if ("author".equals(filter)) {
      classifiedList.setEnteredBy(filterParam);
    }
    // By Date
    if ("date".equals(filter)) {
      classifiedList.setPublishedYearMonth(filterParam);
    }

    classifiedList.buildList(db);
    request.setAttribute(CLASSIFIED_LIST, classifiedList);

    // Record view
    PortalUtils.processSelectHook(request, classifiedList);

    // JSP view
    return defaultView;
  }
}