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

package com.concursive.connect.web.modules.wiki.actions;

import com.concursive.commons.web.mvc.actions.ActionContext;
import com.concursive.connect.web.controller.actions.GenericAction;
import com.concursive.connect.web.modules.documents.beans.FileDownload;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import com.concursive.connect.web.modules.wiki.beans.WikiExportBean;
import com.concursive.connect.web.modules.wiki.jobs.WikiExporterJob;
import org.quartz.Scheduler;

import java.util.Vector;

/**
 * Actions for working with the wiki portlet
 *
 * @author matt rajkowski
 * @created November 5, 2008
 */
public final class WikiExportActions extends GenericAction {

  public String executeCommandDownload(ActionContext context) {
    String file = context.getRequest().getParameter("portlet-value");
    try {
      Scheduler scheduler = (Scheduler) context.getServletContext().getAttribute("Scheduler");
      Vector<WikiExportBean> available = (Vector) scheduler.getContext().get(WikiExporterJob.WIKI_AVAILABLE_ARRAY);
      for (WikiExportBean bean : available) {
        if (bean.getUserId() == getUserId(context)) {
          if (bean.getExportedFile().getName().equals(file)) {
            // Construct a filename with the project name
            Project thisProject = ProjectUtils.loadProject(bean.getProjectId());
            // Download this file...
            FileDownload fileDownload = new FileDownload();
            fileDownload.setFullPath(bean.getExportedFile().getPath());
            fileDownload.setDisplayName(thisProject.getTitle() + " - " + bean.getDisplaySubject() + ".pdf");
            fileDownload.setFileTimestamp(bean.getExportedFile().lastModified());
            if (fileDownload.fileExists()) {
              fileDownload.sendFile(context);
            }
          }
        }
      }
      return ("-none-");
    } catch (Exception errorMessage) {
      context.getRequest().setAttribute("Error", errorMessage);
      return ("SystemError");
    }
  }

  public String executeCommandStream(ActionContext context) {
    String file = context.getRequest().getParameter("portlet-value");
    try {
      Scheduler scheduler = (Scheduler) context.getServletContext().getAttribute("Scheduler");
      Vector<WikiExportBean> available = (Vector) scheduler.getContext().get(WikiExporterJob.WIKI_AVAILABLE_ARRAY);
      for (WikiExportBean bean : available) {
        if (bean.getUserId() == getUserId(context)) {
          if (bean.getExportedFile().getName().equals(file)) {
            // Construct a filename with the project name
            Project thisProject = ProjectUtils.loadProject(bean.getProjectId());
            // Download this file...
            FileDownload fileDownload = new FileDownload();
            fileDownload.setFullPath(bean.getExportedFile().getPath());
            fileDownload.setDisplayName(thisProject.getTitle() + " - " + bean.getDisplaySubject() + ".pdf");
            fileDownload.setFileTimestamp(bean.getExportedFile().lastModified());
            if (fileDownload.fileExists()) {
              fileDownload.streamContent(context);
            }
          }
        }
      }
      return ("-none-");
    } catch (Exception errorMessage) {
      context.getRequest().setAttribute("Error", errorMessage);
      return ("SystemError");
    }
  }

}