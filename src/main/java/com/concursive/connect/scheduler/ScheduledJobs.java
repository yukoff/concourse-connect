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

package com.concursive.connect.scheduler;

import com.concursive.commons.date.DateUtils;
import com.concursive.commons.xml.XMLUtils;
import com.concursive.connect.config.ApplicationPrefs;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.*;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.servlet.ServletContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Date;
import java.util.Set;

/**
 * Responsible for adding the webapplication jobs and triggers
 *
 * @author matt rajkowski
 * @created May 20, 2009
 */

public class ScheduledJobs {

  private static Log LOG = LogFactory.getLog(ScheduledJobs.class);

  public static String CONTEXT_SCHEDULER_GROUP = "SCHEDULER_GROUP";
  public static String UNIQUE_GROUP = "DEFAULT";

  /**
   * Scans the jobs path for the given ServletContext
   *
   * @param scheduler
   * @param context
   * @throws SchedulerException
   */
  public static void addJobs(Scheduler scheduler, ServletContext context) throws SchedulerException {
    // Find job files in the jobs path
    Set<String> jobFiles = context.getResourcePaths("/WEB-INF/jobs/");
    if (jobFiles != null && jobFiles.size() > 0) {
      for (String thisFile : jobFiles) {
        if (thisFile.endsWith(".xml")) {
          try {
            LOG.debug("Adding jobs from... " + thisFile);
            addJobs(scheduler, context.getResource(thisFile));
          } catch (Exception e) {
            LOG.error("addJobs exception", e);
          }
        }
      }
    }
  }

  /**
   * Adds jobs from the specified URL
   *
   * @param scheduler
   * @param jobFile
   * @throws Exception
   */
  public static void addJobs(Scheduler scheduler, URL jobFile) throws Exception {
    // Determine the ApplicationPrefs
    ApplicationPrefs prefs = (ApplicationPrefs) scheduler.getContext().get("ApplicationPrefs");

    // Read all jobs from XML
    XMLUtils document = new XMLUtils(jobFile);

    // Use XPath for querying xml elements
    XPath xpath = XPathFactory.newInstance().newXPath();

    NodeList jobList = (NodeList) xpath.evaluate("job", document.getDocumentElement(), XPathConstants.NODESET);
    LOG.debug("Jobs to process in this file: " + jobList.getLength());
    for (int nodeIndex = 0; nodeIndex < jobList.getLength(); nodeIndex++) {
      Node job = jobList.item(nodeIndex);
      // Check to see if the job is disabled
      String prefAttribute = ((Element) job).getAttribute("pref");
      if (prefAttribute != null && "false".equals(prefs.get(prefAttribute))) {
        continue;
      }
      JobDetail thisJob = null;
      try {
        thisJob = createJob(job, xpath, scheduler.getContext());
      } catch (Exception e) {
        LOG.error("Could not create job", e);
      }
      if (thisJob == null) {
        continue;
      }
      // Create any triggers
      scheduleJob(scheduler, thisJob, job, xpath);
    }
  }

  /**
   * Creates a job based on the job xml
   *
   * @param job
   * @param xpath
   * @param schedulerContext
   * @return
   * @throws Exception
   */
  private static JobDetail createJob(Node job, XPath xpath, SchedulerContext schedulerContext) throws Exception {
    // Create the job
    Node jobDetail = (Node) xpath.evaluate("job-detail", job, XPathConstants.NODE);
    Node jobName = (Node) xpath.evaluate("name", jobDetail, XPathConstants.NODE);
    Node jobGroup = (Node) xpath.evaluate("group", jobDetail, XPathConstants.NODE);
    //Node jobDescription = (Node) xpath.evaluate("description", jobDetail, XPathConstants.NODE);
    Node jobClass = (Node) xpath.evaluate("job-class", jobDetail, XPathConstants.NODE);
    // Construct the job class
    Class thisJobClass = Class.forName(jobClass.getTextContent());
    JobDetail thisJob = new JobDetail(
        jobName.getTextContent(),
        jobGroup.getTextContent(),
        thisJobClass,false,true,false);
    try {
      Method initMethod = thisJobClass.getDeclaredMethod("init", schedulerContext.getClass());
      initMethod.invoke(null, schedulerContext);
      LOG.debug("Job initialized... " + thisJob.getName());
    } catch (Exception e) {
      LOG.debug("Class does not have init method for additional initialization");
    }
    return thisJob;
  }

  private static void scheduleJob(Scheduler scheduler, JobDetail thisJob, Node job, XPath xpath) throws Exception {
    // Check for triggers
    Node trigger = (Node) xpath.evaluate("trigger", job, XPathConstants.NODE);
    if (trigger == null) {
      // No triggers, so just add the job for manually triggering
      scheduler.addJob(thisJob, true);
    } else {
      // Simple triggers
      NodeList simpleList = (NodeList) xpath.evaluate("simple", trigger, XPathConstants.NODESET);
      for (int nodeIndex = 0; nodeIndex < simpleList.getLength(); nodeIndex++) {
        // Retrieve the XML values
        Node simple = simpleList.item(nodeIndex);
        Node name = (Node) xpath.evaluate("name", simple, XPathConstants.NODE);
        Node group = (Node) xpath.evaluate("group", simple, XPathConstants.NODE);
        Node startTime = (Node) xpath.evaluate("start-time", simple, XPathConstants.NODE);
        Node endTime = (Node) xpath.evaluate("end-time", simple, XPathConstants.NODE);
        Node repeatCount = (Node) xpath.evaluate("repeat-count", simple, XPathConstants.NODE);
        Node repeatInterval = (Node) xpath.evaluate("repeat-interval", simple, XPathConstants.NODE);
        // Convert the input
        Date startTimeDate = (startTime != null ? DateUtils.createDate(startTime.getTextContent()) : null);
        Date endTimeDate = (endTime != null ? DateUtils.createDate(endTime.getTextContent()) : null);
        int repeatCountValue = (repeatCount != null ? Integer.parseInt(repeatCount.getTextContent()) : SimpleTrigger.REPEAT_INDEFINITELY);
        long repeatIntervalValue = (repeatInterval != null ? DateUtils.createInterval(repeatInterval.getTextContent()) : 0);
        // Instantiate the simple trigger
        LOG.info("Scheduling a job with a SimpleTrigger: " + thisJob.getName());
        SimpleTrigger simpleTrigger = new SimpleTrigger(
            name.getTextContent(),
            group.getTextContent(),
            startTimeDate,
            endTimeDate,
            repeatCountValue,
            repeatIntervalValue);
        scheduler.scheduleJob(thisJob, simpleTrigger);
      }
      // Cron triggers
      NodeList cronList = (NodeList) xpath.evaluate("cron", trigger, XPathConstants.NODESET);
      for (int nodeIndex = 0; nodeIndex < cronList.getLength(); nodeIndex++) {
        // Retrieve the XML values
        Node cron = cronList.item(nodeIndex);
        Node name = (Node) xpath.evaluate("name", cron, XPathConstants.NODE);
        Node group = (Node) xpath.evaluate("group", cron, XPathConstants.NODE);
        Node cronExpression = (Node) xpath.evaluate("cron-expression", cron, XPathConstants.NODE);
        LOG.info("Scheduling a job with a CronTrigger: " + thisJob.getName());
        CronTrigger cronTrigger = new CronTrigger(
            name.getTextContent(),
            group.getTextContent(),
            cronExpression.getTextContent());
        scheduler.scheduleJob(thisJob, cronTrigger);
      }
    }
  }
}
