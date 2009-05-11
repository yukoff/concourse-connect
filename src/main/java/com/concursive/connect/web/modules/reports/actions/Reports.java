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

package com.concursive.connect.web.modules.reports.actions;

import com.concursive.commons.jasperreports.JasperReportUtils;
import com.concursive.commons.web.mvc.actions.ActionContext;
import com.concursive.connect.Constants;
import com.concursive.connect.web.controller.actions.GenericAction;
import com.concursive.connect.web.modules.documents.beans.FileDownload;
import com.concursive.connect.web.modules.reports.dao.*;
import net.sf.jasperreports.engine.JasperReport;

import java.io.File;
import java.sql.Connection;
import java.util.Iterator;

/**
 * Description
 *
 * @author matt rajkowski
 * @version $Id$
 * @created Sep 6, 2005
 */

public final class Reports extends GenericAction {

  public String executeCommandList(ActionContext context) {
    if (!getUser(context).getAccessRunReports()) {
      return "PermissionError";
    }
    Connection db = null;
    try {
      db = getConnection(context);
      ReportQueueList queueList = new ReportQueueList();
      queueList.setBuildResources(true);
      queueList.setEnteredBy(getUserId(context));
      queueList.buildList(db);
      context.getRequest().setAttribute("queueList", queueList);
    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return ("SystemError");
    } finally {
      freeConnection(context, db);
    }
    return "ListOK";
  }

  public String executeCommandSetup(ActionContext context) {
    if (!getUser(context).getAccessRunReports()) {
      return "PermissionError";
    }
    Connection db = null;
    try {
      db = getConnection(context);
      ReportList reports = new ReportList();
      reports.setEnabled(Constants.TRUE);
      if (!getUser(context).getAccessAdmin()) {
        reports.setAdminReport(Constants.FALSE);
      }
      reports.buildList(db);
      context.getRequest().setAttribute("reportList", reports);
    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return ("SystemError");
    } finally {
      freeConnection(context, db);
    }
    return "SetupStep1OK";
  }

  public String executeCommandCriteria(ActionContext context) {
    if (!getUser(context).getAccessRunReports()) {
      return "PermissionError";
    }
    Connection db = null;
    try {
      int reportId = Integer.parseInt(context.getRequest().getParameter("reportId"));
      db = getConnection(context);
      // Load the report
      Report thisReport = new Report(db, reportId);
      if (thisReport.getAdminReport() && !getUser(context).getAccessAdmin()) {
        return "PermissionError";
      }
      context.getRequest().setAttribute("report", thisReport);
      //Read in the Jasper Report and return the parameters to the user
      JasperReport jasperReport = null;
      if (thisReport.getCustom()) {
        String file = getPath(context, "reports") + thisReport.getFilename();
        jasperReport = JasperReportUtils.getReport(file);
      } else {
        //Read in the Jasper Report and return the parameters to the user
        jasperReport = JasperReportUtils.getReport(context.getServletContext(), "/WEB-INF/reports/" + thisReport.getFilename());
      }
      //Generate the allowable parameter list
      ParameterList params = new ParameterList();
      params.setParameters(jasperReport);
      //Auto-populate some defaults to show the user
      Iterator i = params.iterator();
      while (i.hasNext()) {
        Parameter param = (Parameter) i.next();
        if (param.getIsForPrompting()) {
          //All of the configurability appears in the Parameter class
          param.prepareContext(context.getRequest(), db);
          if (System.getProperty("DEBUG") != null) {
            System.out.println("Reports-> Parameter for prompting: " + param.getName());
          }
        }
      }
      context.getRequest().setAttribute("parameterList", params);
    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return ("SystemError");
    } finally {
      freeConnection(context, db);
    }
    return "DetailsOK";
  }

  public String executeCommandGenerate(ActionContext context) {
    if (!getUser(context).getAccessRunReports()) {
      return "PermissionError";
    }
    Connection db = null;
    try {
      ReportQueue queue = (ReportQueue) context.getFormBean();
      db = getConnection(context);
      // Load the report
      Report thisReport = new Report(db, queue.getReportId());
      if (thisReport.getAdminReport() && !getUser(context).getAccessAdmin()) {
        return "PermissionError";
      }
      context.getRequest().setAttribute("report", thisReport);
      //Read in the Jasper Report and and test some values
      JasperReport jasperReport = null;
      if (thisReport.getCustom()) {
        String file = getPath(context, "reports") + thisReport.getFilename();
        jasperReport = JasperReportUtils.getReport(file);
      } else {
        //Read in the Jasper Report and return the parameters to the user
        jasperReport = JasperReportUtils.getReport(context.getServletContext(), "/WEB-INF/reports/" + thisReport.getFilename());
      }
      //Generate the allowable parameter list
      ParameterList params = new ParameterList();
      params.setParameters(jasperReport);
      boolean valid = params.setParameters(context.getRequest());
      if (params.getParameter("projectId") != null) {
        retrieveAuthorizedProject(params.getValueAsInt("projectId"), context);
      }
      if (!valid) {
        return "CriteriaERROR";
      }
      //queue.setReportId(thisReport.getId());
      queue.setEnteredBy(getUserId(context));
      if (queue.getCleanup() > 90) {
        queue.setCleanup(90);
      }
      queue.insert(db, params);
      if (!queue.hasSchedule()) {
        executeJob(context, "reports");
      }
    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return ("SystemError");
    } finally {
      freeConnection(context, db);
    }
    return "GenerateOK";
  }

  /* Menu Commands */
  public String executeCommandView(ActionContext context) {
    if (!getUser(context).getAccessRunReports()) {
      return "PermissionError";
    }
    Connection db = null;
    ReportQueue queue = null;
    try {
      int queueId = Integer.parseInt(context.getRequest().getParameter("queueId"));
      db = getConnection(context);
      queue = new ReportQueue(db, queueId);
      if (queue.getEnteredBy() != getUserId(context)) {
        return "PermissionError";
      }
    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return ("SystemError");
    } finally {
      freeConnection(context, db);
    }
    if (queue != null) {
      try {
        File thisFile = new File(getPath(context, "projects") + getDatePath(
            queue.getEntered()) + queue.getFilename());
        if (!thisFile.exists()) {
          return "FileERROR";
        }
        FileDownload download = new FileDownload();
        download.setFullPath(
            getPath(context, "projects") + getDatePath(
                queue.getEntered()) + queue.getFilename());
        download.setDisplayName(queue.getFilename() + "." + queue.getOutput());
        download.setFileTimestamp(queue.getModificationDate().getTime());
        download.streamContent(context);
      } catch (Exception e) {
        e.printStackTrace(System.out);
      }
    }
    return "-none-";
  }

  public String executeCommandDownload(ActionContext context) {
    if (!getUser(context).getAccessRunReports()) {
      return "PermissionError";
    }
    Connection db = null;
    ReportQueue queue = null;
    try {
      int queueId = Integer.parseInt(context.getRequest().getParameter("queueId"));
      db = getConnection(context);
      queue = new ReportQueue(db, queueId);
      if (queue.getEnteredBy() != getUserId(context)) {
        return "PermissionError";
      }
    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return ("SystemError");
    } finally {
      freeConnection(context, db);
    }
    if (queue != null) {
      try {
        FileDownload download = new FileDownload();
        download.setFullPath(
            getPath(context, "projects") + getDatePath(
                queue.getEntered()) + queue.getFilename());
        switch (queue.getOutputTypeConstant()) {
          case ReportQueue.REPORT_TYPE_HTML:
            download.setDisplayName(queue.getFilename() + ".html");
            break;
          case ReportQueue.REPORT_TYPE_CSV:
            download.setDisplayName(queue.getFilename() + ".csv");
            break;
          case ReportQueue.REPORT_TYPE_EXCEL:
            download.setDisplayName(queue.getFilename() + ".xls");
            break;
          case ReportQueue.REPORT_TYPE_PDF:
            download.setDisplayName(queue.getFilename() + ".pdf");
            break;
        }
        download.sendFile(context);
      } catch (Exception e) {
        e.printStackTrace(System.out);
      }
    }
    return "-none-";
  }

  public String executeCommandUpdate(ActionContext context) {
    if (!getUser(context).getAccessRunReports()) {
      return "PermissionError";
    }
    Connection db = null;
    try {
      int queueId = Integer.parseInt(context.getRequest().getParameter("queueId"));
      db = getConnection(context);

      ReportQueue queue = new ReportQueue(db, queueId);
      if (queue.getEnteredBy() != getUserId(context)) {
        return "PermissionError";
      }

      CriteriaList criteriaList = new CriteriaList();
      criteriaList.setQueueId(queue.getId());
      criteriaList.buildList(db);
      // Insert new report
      queue.setId(-1);
      queue.insert(db, criteriaList);
      // Delete original report
      ReportQueue previousQueue = new ReportQueue(db, queueId);
      previousQueue.delete(db, getPath(context, "projects"));
      // Run the new report
      executeJob(context, "reports");
    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return ("SystemError");
    } finally {
      freeConnection(context, db);
    }
    return "UpdateOK";
  }

  public String executeCommandDelete(ActionContext context) {
    if (!getUser(context).getAccessRunReports()) {
      return "PermissionError";
    }
    Connection db = null;
    try {
      int queueId = Integer.parseInt(context.getRequest().getParameter("queueId"));
      db = getConnection(context);
      ReportQueue queue = new ReportQueue(db, queueId);
      if (queue.getEnteredBy() != getUserId(context)) {
        return "PermissionError";
      }
      queue.delete(db, getPath(context, "projects"));
    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return ("SystemError");
    } finally {
      freeConnection(context, db);
    }
    return "DeleteOK";
  }

  public String executeCommandDeleteAll(ActionContext context) {
    if (!getUser(context).getAccessRunReports()) {
      return "PermissionError";
    }
    Connection db = null;
    try {
      int projectId = Integer.parseInt(context.getRequest().getParameter("projectId"));
      db = getConnection(context);
      retrieveAuthorizedProject(projectId, context);
      ReportQueueList queueList = new ReportQueueList();
      queueList.setProjectId(projectId);
      queueList.setEnteredBy(getUserId(context));
      queueList.buildList(db);
      queueList.delete(db, getPath(context, "projects"));
    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return ("SystemError");
    } finally {
      freeConnection(context, db);
    }
    return "DeleteOK";
  }

}
