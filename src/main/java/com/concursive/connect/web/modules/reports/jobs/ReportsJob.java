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

package com.concursive.connect.web.modules.reports.jobs;

import com.concursive.commons.date.DateUtils;
import com.concursive.commons.db.DatabaseUtils;
import com.concursive.commons.email.SMTPMessage;
import com.concursive.commons.email.SMTPMessageFactory;
import com.concursive.commons.jasperreports.JasperReportUtils;
import com.concursive.commons.text.StringUtils;
import com.concursive.connect.config.ApplicationPrefs;
import com.concursive.connect.scheduler.SchedulerUtils;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.login.utils.UserUtils;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.reports.dao.*;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporterParameter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerContext;
import org.quartz.StatefulJob;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.FileOutputStream;
import java.io.StringWriter;
import java.sql.Connection;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;

import freemarker.template.Template;
import freemarker.template.Configuration;

/**
 * Runs reports and outputs as requested
 *
 * @author matt rajkowski
 * @version $Id$
 * @created Jun 20, 2005
 */

public class ReportsJob implements StatefulJob {

  private static Log LOG = LogFactory.getLog(ReportsJob.class);

  public void execute(JobExecutionContext context) throws JobExecutionException {
    SchedulerContext schedulerContext = null;
    Connection db = null;
    try {
      schedulerContext = context.getScheduler().getContext();
      ServletContext servletContext = (ServletContext) schedulerContext.get(
          "ServletContext");
      ApplicationPrefs prefs = (ApplicationPrefs) schedulerContext.get(
          "ApplicationPrefs");
      String fs = System.getProperty("file.separator");
      db = SchedulerUtils.getConnection(schedulerContext);
      LOG.debug("Checking reports...");
      // Process the unprocessed first...
      ReportQueueList queue = new ReportQueueList();
      queue.setSortAscending(true);
      queue.setUnprocessedOnly(true);
      queue.buildList(db);
      runReports(queue, db, schedulerContext, servletContext, prefs, fs);
      // Process the scheduled reports next...
      ReportQueueList scheduled = new ReportQueueList();
      scheduled.setSortAscending(true);
      scheduled.setScheduledTodayOnly(true);
      scheduled.buildList(db);
      runReports(scheduled, db, schedulerContext, servletContext, prefs, fs);
    } catch (Exception e) {
      throw new JobExecutionException(e.getMessage());
    } finally {
      SchedulerUtils.freeConnection(schedulerContext, db);
    }
  }

  private void runReports(ReportQueueList queue, Connection db, SchedulerContext schedulerContext, ServletContext servletContext, ApplicationPrefs prefs, String fs) throws Exception {
    //Iterate the list
    Iterator list = queue.iterator();
    while (list.hasNext()) {
      ReportQueue thisQueue = (ReportQueue) list.next();
      // For previously executed and re-schedued, change the status back to queued
      if (thisQueue.getStatus() == ReportQueue.STATUS_SCHEDULED) {
        thisQueue.setStatus(ReportQueue.STATUS_QUEUED);
        thisQueue.updateStatus(db);
      }
      // Attempt a lock and start processing
      if (ReportQueueList.lockReport(thisQueue, db)) {
        // User who owns the report
        User user = UserUtils.loadUser(thisQueue.getEnteredBy());
        // Destination directory for report
        String destDir = prefs.get("FILELIBRARY") + user.getGroupId() + fs + "projects" + fs +
            DateUtils.getDatePath(thisQueue.getEntered());
        File destPath = new File(destDir);
        destPath.mkdirs();
        String filename = DateUtils.getFilename(thisQueue.getEntered()) + "-Q" + thisQueue.getId();
        // The report
        Report thisReport = new Report(db, thisQueue.getReportId());
        if (!thisReport.getEnabled()) {
          thisQueue.setStatus(ReportQueue.STATUS_DISABLED);
          thisQueue.updateStatus(db);
          continue;
        }
        File reportFile = null;
        try {
          String reportPath = null;
          if (thisReport.getCustom()) {
            reportPath = prefs.get("FILELIBRARY") + user.getGroupId() + fs + "reports" + fs;
          }
          JasperReport jasperReport = null;
          // Determine the method for loading the report
          if ("html".equals(thisQueue.getOutput()) ||
              "csv".equals(thisQueue.getOutput()) ||
              "excel".equals(thisQueue.getOutput())) {
            JasperDesign jasperDesign = null;
            // For HTML, load the design first
            if (thisReport.getCustom()) {
              String file = reportPath + thisReport.getFilename();
              jasperDesign = JasperReportUtils.getDesign(file);
            } else {
              jasperDesign = JasperReportUtils.getDesign(
                  servletContext, "/WEB-INF/reports/" + thisReport.getFilename());
            }
            // Remove the page footers and headers
            jasperDesign.setLeftMargin(0);
            jasperDesign.setRightMargin(0);
            jasperDesign.setTopMargin(0);
            jasperDesign.setBottomMargin(0);
            jasperDesign.setPageHeader(null);
            jasperDesign.setPageFooter(null);

            // Construct the report for the rest of the process
            jasperReport = JasperCompileManager.compileReport(jasperDesign);
          } else {
            // Load the report
            if (thisReport.getCustom()) {
              String file = reportPath + thisReport.getFilename();
              jasperReport = JasperReportUtils.getReport(file);
            } else {
              //Read in the Jasper Report and return the parameters to the user
              jasperReport = JasperReportUtils.getReport(
                  servletContext, "/WEB-INF/reports/" + thisReport.getFilename());
            }
          }
          // Set report parameters
          ParameterList params = new ParameterList();
          params.setParameters(jasperReport);
          CriteriaList criteriaList = new CriteriaList(params);
          criteriaList.setQueueId(thisQueue.getId());
          criteriaList.buildList(db);
          Map<String, Object> parameters = criteriaList.getParameters();
          //Set the system generated parameters
          //parameters.put(
          //  "path_icons", context.getServletContext().getRealPath("/") + "images" + fs + "icons" + fs);
          //params.addParam(
          //  "path_report_images", getPath(context, "report_images"));
          //Set some database specific parameters
          parameters.put("user_name", user.getNameFirstLast());
          if (params.getParameter("projectId") != null) {
            Criteria criteria = (Criteria) criteriaList.get("projectId");
            int projectId = criteria.getValueAsInt();
            Project project = null;
            if (user.getAccessAdmin()) {
              project = new Project(db, projectId);
            } else {
              project = new Project(db, projectId, user.getIdRange());
            }
            parameters.put("projectTitle", project.getTitle());
          }
          if (params.getParameter("year_part") != null) {
            Parameter thisParam = params.getParameter("year_part");
            parameters.put(
                "year_part", DatabaseUtils.getYearPart(
                    db, thisParam.getDescription()));
          }
          if (params.getParameter("month_part") != null) {
            Parameter thisParam = params.getParameter("month_part");
            parameters.put(
                "month_part", DatabaseUtils.getMonthPart(
                    db, thisParam.getDescription()));
          }
          if (params.getParameter("day_part") != null) {
            Parameter thisParam = params.getParameter("day_part");
            parameters.put(
                "day_part", DatabaseUtils.getDayPart(
                    db, thisParam.getDescription()));
          }
          // TODO: any date conversions from user to server?
          if (thisReport.getCustom()) {
            parameters.put("path", reportPath);
          } else {
            //parameters.put("path", path);
          }
          // parameters.put(CENTRIC_DICTIONARY, localizationPrefs);
          if (thisQueue.getOutputTypeConstant() == ReportQueue.REPORT_TYPE_CSV ||
              thisQueue.getOutputTypeConstant() == ReportQueue.REPORT_TYPE_EXCEL ||
              thisQueue.getOutputTypeConstant() == ReportQueue.REPORT_TYPE_HTML) {
            //disable paged output if csv or excel format
            parameters.put(JRParameter.IS_IGNORE_PAGINATION, Boolean.TRUE);
          }
          if ("html".equals(thisQueue.getOutput())) {
            // Export the html report to fileLibrary for this site
            LOG.debug("Run the report to HTML");
            JasperPrint print = JasperFillManager.fillReport(jasperReport, parameters, db);
            reportFile = new File(destDir + filename);
            JRHtmlExporter exporter = new JRHtmlExporter();
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, print);
            exporter.setParameter(JRExporterParameter.OUTPUT_FILE, reportFile);
            exporter.setParameter(JRHtmlExporterParameter.IS_USING_IMAGES_TO_ALIGN, Boolean.FALSE);
            exporter.setParameter(JRHtmlExporterParameter.HTML_HEADER, "");
            exporter.setParameter(JRHtmlExporterParameter.BETWEEN_PAGES_HTML, "");
            exporter.setParameter(JRHtmlExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
            exporter.setParameter(JRHtmlExporterParameter.HTML_FOOTER, "");
            exporter.exportReport();
          } else if ("csv".equals(thisQueue.getOutput())) {
            JasperPrint print = JasperFillManager.fillReport(jasperReport, parameters, db);
            reportFile = new File(destDir + filename);
            JRCsvExporter exporterCSV = new JRCsvExporter();
            exporterCSV.setParameter(JRExporterParameter.JASPER_PRINT, print);
            exporterCSV.setParameter(JRExporterParameter.OUTPUT_FILE, reportFile);
            exporterCSV.exportReport();
          } else if ("excel".equals(thisQueue.getOutput())) {
            JasperPrint print = JasperFillManager.fillReport(jasperReport, parameters, db);
            reportFile = new File(destDir + filename);
            JRXlsExporter exporterXls = new JRXlsExporter();
            exporterXls.setParameter(JRExporterParameter.JASPER_PRINT, print);
            exporterXls.setParameter(JRExporterParameter.OUTPUT_FILE, reportFile);
            exporterXls.exportReport();
          } else {
            // Export the pdf to fileLibrary for this site
            LOG.debug("Run the report to PDF");
            byte[] bytes = JasperRunManager.runReportToPdf(jasperReport, parameters, db);
            reportFile = new File(destDir + filename);
            FileOutputStream destination = new FileOutputStream(reportFile);
            destination.write(bytes, 0, bytes.length);
          }
          // Save the report
          LOG.debug("Save the report to file");
          // Determine the size
          long size = -1;
          if (reportFile.exists()) {
            size = reportFile.length();
          }
          thisQueue.setFilename(filename);
          thisQueue.setSize(size);
          thisQueue.setStatus(ReportQueue.STATUS_PROCESSED);
        } catch (Exception e) {
          thisQueue.setStatus(ReportQueue.STATUS_ERROR);
          LOG.error("Report queue processing error", e);
        } finally {
          LOG.debug("Update the queue");
          thisQueue.updateStatus(db);
          thisQueue.calculateNextRunDate(db);
        }
        if (thisQueue.getSendEmail()) {
          LOG.debug("Send email as requested by the user");
          SMTPMessage message = SMTPMessageFactory.createSMTPMessageInstance(prefs.getPrefs());
          message.setFrom(prefs.get("EMAILADDRESS"));
          message.addReplyTo(prefs.get("EMAILADDRESS"));
          message.addTo(user.getEmail());
          message.setSubject(thisReport.getTitle());
          message.setType("text/html");
          // Populate the message template
          Configuration configuration = ApplicationPrefs.getFreemarkerConfiguration(servletContext);
          Template template = configuration.getTemplate("report_generation_email_notification-html.ftl");
          Map bodyMappings = new HashMap();
          if (thisQueue.getStatus() == ReportQueue.STATUS_PROCESSED) {
            switch (thisQueue.getOutputTypeConstant()) {
              case ReportQueue.REPORT_TYPE_HTML:
                // Just place the HTML in the email body, not as an attachment...
                bodyMappings.put("body", "The attached report was generated and emailed as requested...<br /><br />" +
                    StringUtils.loadText(destDir + filename));
                break;
              case ReportQueue.REPORT_TYPE_CSV:
                // Attach the CSV
                bodyMappings.put("body", "The attached report was generated and emailed as requested...<br /><br />");
                message.addFileAttachment(destDir + filename, filename + ".csv");
                break;
              case ReportQueue.REPORT_TYPE_PDF:
                // Attach the PDF
                bodyMappings.put("body", "The attached report was generated and emailed as requested...<br /><br />");
                message.addFileAttachment(destDir + filename, filename + ".pdf");
                break;
              case ReportQueue.REPORT_TYPE_EXCEL:
                // Attach the Excel
                bodyMappings.put("body", "The attached report was generated and emailed as requested...<br /><br />");
                message.addFileAttachment(destDir + filename, filename + ".xls");
                break;
            }
          } else {
            bodyMappings.put("body", "There was an error in processing the requested report.");
          }
          // Parse and send
          StringWriter inviteBodyTextWriter = new StringWriter();
          template.process(bodyMappings, inviteBodyTextWriter);
          message.setBody(inviteBodyTextWriter.toString());
          //Send the invitations
          message.send();
        }
      }
    }
  }
}
