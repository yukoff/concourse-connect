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

package com.concursive.connect.web.modules.admin.actions;

import com.concursive.commons.jasperreports.JasperReportUtils;
import com.concursive.commons.web.mvc.actions.ActionContext;
import com.concursive.connect.Constants;
import com.concursive.connect.web.controller.actions.GenericAction;
import com.concursive.connect.web.modules.documents.utils.FileInfo;
import com.concursive.connect.web.modules.documents.utils.HttpMultiPartParser;
import com.concursive.connect.web.modules.reports.dao.Parameter;
import com.concursive.connect.web.modules.reports.dao.ParameterList;
import com.concursive.connect.web.modules.reports.dao.Report;
import com.concursive.connect.web.modules.reports.dao.ReportList;
import net.sf.jasperreports.engine.JasperReport;

import java.sql.Connection;
import java.util.HashMap;

/**
 * Description
 *
 * @author matt rajkowski
 * @version $Id$
 * @created Dec 27, 2004
 */

public class AdminReports extends GenericAction {

  public String executeCommandList(ActionContext context) {
    if (!getUser(context).getAccessAdmin()) {
      return "PermissionError";
    }
    Connection db = null;
    try {
      db = getConnection(context);
      // Load the reports lists
      ReportList activeReports = new ReportList();
      activeReports.setEnabled(Constants.TRUE);
      activeReports.buildList(db);
      context.getRequest().setAttribute("activeReportList", activeReports);

      ReportList disabledReports = new ReportList();
      disabledReports.setEnabled(Constants.FALSE);
      disabledReports.buildList(db);
      context.getRequest().setAttribute("disabledReportList", disabledReports);

      return ("ListOK");
    } catch (Exception errorMessage) {
      context.getRequest().setAttribute("Error", errorMessage);
      return ("SystemError");
    } finally {
      this.freeConnection(context, db);
    }
  }

  public String executeCommandDetails(ActionContext context) {
    if (!getUser(context).getAccessAdmin()) {
      return "PermissionError";
    }
    Connection db = null;
    try {
      String reportValue = context.getRequest().getParameter("reportId");
      if (reportValue == null) {
        reportValue = (String) context.getRequest().getAttribute("reportId");
      }
      int reportId = Integer.parseInt(reportValue);
      db = getConnection(context);
      // Load the report
      Report thisReport = new Report(db, reportId);
      context.getRequest().setAttribute("report", thisReport);
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
      for (Object param1 : params) {
        Parameter param = (Parameter) param1;
        if (param.getIsForPrompting()) {
          //All of the configurability appears in the Parameter class
          param.prepareContext(context.getRequest(), db);
          if (System.getProperty("DEBUG") != null) {
            System.out.println("AdminReports-> Parameter for prompting: " + param.getName());
          }
        }
      }
      context.getRequest().setAttribute("parameterList", params);
      return ("DetailsOK");
    } catch (Exception errorMessage) {
      context.getRequest().setAttribute("Error", errorMessage);
      return ("SystemError");
    } finally {
      this.freeConnection(context, db);
    }
  }

  public String executeCommandAdd(ActionContext context) {
    if (!getUser(context).getAccessAdmin()) {
      return "PermissionError";
    }
    return ("AddOK");
  }

  public String executeCommandUpload(ActionContext context) {
    if (!getUser(context).getAccessAdmin()) {
      return "PermissionError";
    }
    Connection db = null;
    boolean recordInserted = false;
    try {
      String filePath = getPath(context, "reports");
      //Process the form data
      HttpMultiPartParser multiPart = new HttpMultiPartParser();
      multiPart.setUsePathParam(false);
      multiPart.setUseUniqueName(false);
      multiPart.setUseDateForFolder(false);
      HashMap parts = multiPart.parseData(context.getRequest(), filePath);
      String title = (String) parts.get("title");
      String type = (String) parts.get("type");
      //String userReport = (String) parts.get("userReport");
      //String adminReport = (String) parts.get("adminReport");
      //String enabled = (String) parts.get("enabled");
      db = getConnection(context);
      //Update the database with the resulting file
      if ((Object) parts.get("file") instanceof FileInfo) {
        FileInfo newFileInfo = (FileInfo) parts.get("file");
        Report report = new Report();
        report.setTitle(title);
        report.setEnteredBy(getUserId(context));
        report.setModifiedBy(getUserId(context));
        report.setEnabled(false);
        if ("user".equals(type)) {
          report.setUserReport(true);
        } else {
          report.setAdminReport(true);
        }
        report.setCustom(true);
        report.setFilename(newFileInfo.getRealFilename());
        report.setDescription(newFileInfo.getClientFileName());
        report.insert(db);
        context.getRequest().setAttribute("reportId", String.valueOf(report.getId()));
        return ("UploadOK");
      }
      context.getRequest().setAttribute("actionError", "Error in uploading report");
      return ("UploadERROR");
    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return ("SystemError");
    } finally {
      freeConnection(context, db);
    }
  }

  public String executeCommandActivate(ActionContext context) {
    if (!getUser(context).getAccessAdmin()) {
      return "PermissionError";
    }
    Connection db = null;
    try {
      int reportId = Integer.parseInt((String) context.getParameter("reportId"));
      db = getConnection(context);
      Report report = new Report(db, reportId);
      report.activate(db);
      return ("ActivateOK");
    } catch (Exception errorMessage) {
      context.getRequest().setAttribute("Error", errorMessage);
      return ("SystemError");
    } finally {
      this.freeConnection(context, db);
    }
  }

  public String executeCommandDisable(ActionContext context) {
    if (!getUser(context).getAccessAdmin()) {
      return "PermissionError";
    }
    Connection db = null;
    try {
      int reportId = Integer.parseInt((String) context.getParameter("reportId"));
      db = getConnection(context);
      Report report = new Report(db, reportId);
      report.disable(db);
      return ("DisableOK");
    } catch (Exception errorMessage) {
      context.getRequest().setAttribute("Error", errorMessage);
      return ("SystemError");
    } finally {
      this.freeConnection(context, db);
    }
  }

  public String executeCommandDelete(ActionContext context) {
    if (!getUser(context).getAccessAdmin()) {
      return "PermissionError";
    }
    Connection db = null;
    try {
      int reportId = Integer.parseInt((String) context.getParameter("reportId"));
      db = getConnection(context);
      Report report = new Report(db, reportId);
      if (report.getCustom()) {
        report.delete(db, getPath(context, "reports"));
      }
      return ("DeleteOK");
    } catch (Exception errorMessage) {
      context.getRequest().setAttribute("Error", errorMessage);
      return ("SystemError");
    } finally {
      this.freeConnection(context, db);
    }
  }

  public String executeCommandSetAsAdmin(ActionContext context) {
    if (!getUser(context).getAccessAdmin()) {
      return "PermissionError";
    }
    Connection db = null;
    try {
      int reportId = Integer.parseInt((String) context.getParameter("reportId"));
      db = getConnection(context);
      Report report = new Report(db, reportId);
      report.updateAsAdmin(db);
      return ("SetAsAdminOK");
    } catch (Exception errorMessage) {
      context.getRequest().setAttribute("Error", errorMessage);
      return ("SystemError");
    } finally {
      this.freeConnection(context, db);
    }
  }

  public String executeCommandSetAsUser(ActionContext context) {
    if (!getUser(context).getAccessAdmin()) {
      return "PermissionError";
    }
    Connection db = null;
    try {
      int reportId = Integer.parseInt((String) context.getParameter("reportId"));
      db = getConnection(context);
      Report report = new Report(db, reportId);
      report.updateAsUser(db);
      return ("SetAsUserOK");
    } catch (Exception errorMessage) {
      context.getRequest().setAttribute("Error", errorMessage);
      return ("SystemError");
    } finally {
      this.freeConnection(context, db);
    }
  }


}
