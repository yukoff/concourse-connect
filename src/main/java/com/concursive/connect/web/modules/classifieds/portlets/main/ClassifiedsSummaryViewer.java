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
import com.concursive.connect.web.modules.classifieds.dao.ClassifiedCategoryList;
import com.concursive.connect.web.modules.classifieds.utils.ClassifiedsCounter;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.portal.IPortletViewer;
import com.concursive.connect.web.portal.PortalUtils;
import static com.concursive.connect.web.portal.PortalUtils.getConnection;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import java.sql.Connection;

/**
 * Project classifieds list counter viewer
 *
 * @author matt rajkowski
 * @created November 18, 2008
 */
public class ClassifiedsSummaryViewer implements IPortletViewer {

  // Logger
  private static Log LOG = LogFactory.getLog(ClassifiedsSummaryViewer.class);

  // Pages
  private static final String VIEW_PAGE = "/projects_center_classifieds_counts.jsp";

  // Object Results
  private static final String COUNTER = "counter";
  private static final String CATEGORY_LIST = "categoryList";

  public String doView(RenderRequest request, RenderResponse response) throws Exception {
    Project project = PortalUtils.findProject(request);

    if (project == null) {
      throw new PortletException("Required project record not found");
    }

    // Build a list of records
    Connection db = getConnection(request);

    // Build ad counts
    ClassifiedsCounter classifiedsCounter = new ClassifiedsCounter();
    classifiedsCounter.setProjectId(project.getId());
    classifiedsCounter.buildCounts(db);
    request.setAttribute(COUNTER, classifiedsCounter);

    // Prepare the list of categories to display
    ClassifiedCategoryList categoryList = new ClassifiedCategoryList();
    categoryList.setProjectCategoryId(project.getCategoryId());
    categoryList.setEnabled(Constants.TRUE);
    categoryList.buildList(db);
    request.setAttribute(CATEGORY_LIST, categoryList);

    if (classifiedsCounter.hasCounts()) {
      return VIEW_PAGE;
    } else {
      LOG.debug("didn't have any counts");
      return null;
    }
  }
}