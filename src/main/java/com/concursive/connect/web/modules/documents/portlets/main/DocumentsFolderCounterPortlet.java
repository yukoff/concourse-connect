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
package com.concursive.connect.web.modules.documents.portlets.main;

import com.concursive.connect.Constants;
import com.concursive.connect.web.modules.documents.dao.FileFolder;
import com.concursive.connect.web.modules.documents.dao.FileFolderList;
import com.concursive.connect.web.modules.documents.utils.FileItemCounter;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.portal.PortalUtils;
import static com.concursive.connect.web.portal.PortalUtils.useConnection;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.portlet.*;
import java.io.IOException;
import java.sql.Connection;

/**
 * Project blog list counter portlet
 *
 * @author matt rajkowski
 * @created October 28, 2008
 */
public class DocumentsFolderCounterPortlet extends GenericPortlet {

  private static Log LOG = LogFactory.getLog(DocumentsFolderCounterPortlet.class);

  // Pages
  private static final String VIEW_PAGE = "/projects_center_folder_counts.jsp";

  // Object Results
  private static final String COUNTER = "fileItemCounter";
  private static final String FOLDER_LIST = "fileFolderList";
  private static final String CURRENT_FOLDER = "currentFolder";
  private static final String VIEWER_NAMESPACE = "namespace";

  public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {

    Project project = PortalUtils.findProject(request);

    if (project == null) {
      throw new PortletException("Required project record not found");
    }

    // Build a list of records
    Connection db = useConnection(request);

    try {
      // Build news counts
      FileItemCounter counter = new FileItemCounter();
      counter.setLinkModuleId(Constants.PROJECTS_FILES);
      counter.setLinkItemId(project.getId());
      counter.buildCounts(db);
      request.setAttribute(COUNTER, counter);

      // Prepare the list of categories to display
      FileFolderList fileFolderList = new FileFolderList();
      fileFolderList.setLinkModuleId(Constants.PROJECTS_FILES);
      fileFolderList.setLinkItemId(project.getId());
      fileFolderList.buildList(db);
      request.setAttribute(FOLDER_LIST, fileFolderList);

      // Let the view know about the currently selected folder
      FileFolder currentFolder = (FileFolder) PortalUtils.getGeneratedData(request, "currentFolder");
      LOG.debug("getGeneratedData folder: " + currentFolder.getSubject());
      request.setAttribute(CURRENT_FOLDER, currentFolder);

      // This portlet can consume data from other portlets
      boolean display = true;
      for (String event : PortalUtils.getDashboardPortlet(request).getConsumeDataEvents()) {
        if (event.startsWith("namespace-")) {
          // The namespace needs to be known so that URLs can be generated for another portlet
          String namespace = (String) PortalUtils.getGeneratedData(request, event);
          if (namespace == null) {
            display = false;
          } else {
            request.setAttribute(VIEWER_NAMESPACE, namespace);
          }
        }
      }

      // JSP view
      if (display) {
        PortletContext context = getPortletContext();
        PortletRequestDispatcher requestDispatcher =
            context.getRequestDispatcher(VIEW_PAGE);
        requestDispatcher.include(request, response);
      }
    } catch (Exception e) {
      LOG.debug("doView", e);
      throw new PortletException(e);
    }

  }
}