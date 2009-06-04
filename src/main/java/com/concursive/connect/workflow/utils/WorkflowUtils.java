package com.concursive.connect.workflow.utils;

import com.concursive.commons.workflow.ObjectHookManager;
import com.concursive.commons.workflow.WorkflowManager;
import com.concursive.commons.xml.XMLUtils;
import com.concursive.connect.config.ApplicationPrefs;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Set;

/**
 * Utilities for working with workflow data
 *
 * @author matt rajkowski
 * @created Jun 4, 2009 10:24:27 AM
 */
public class WorkflowUtils {

  private static Log LOG = LogFactory.getLog(WorkflowUtils.class);

  /**
   * Adds object hooks and business processes workflow to the ObjectHookManager
   *
   * @param hookManager
   * @param context
   */
  public static void addWorkflow(ObjectHookManager hookManager, ServletContext context) {
    // Reset the workflow manager
    hookManager.reset();
    // Load application workflow processes...
    try {
      LOG.info("Loading application workflow processes...");
      InputStream source = WorkflowManager.class.getResourceAsStream("/application.xml");
      XMLUtils xml = new XMLUtils(source);
      hookManager.initializeBusinessProcessList(xml.getDocumentElement());
      hookManager.initializeObjectHookList(xml.getDocumentElement());
      source.close();
    } catch (Exception e) {
      LOG.error("addWorkflow exception - /application.xml", e);
    }

    // Find job files in the jobs path
    LOG.info("Loading workflow processes...");
    Set<String> workflowFiles = context.getResourcePaths("/WEB-INF/workflow/");
    if (workflowFiles != null && workflowFiles.size() > 0) {
      for (String thisFile : workflowFiles) {
        if (thisFile.endsWith(".xml")) {
          try {
            LOG.info("Adding workflow from... " + thisFile);
            InputStream source = context.getResourceAsStream(thisFile);
            XMLUtils xml = new XMLUtils(source);
            hookManager.initializeBusinessProcessList(xml.getDocumentElement());
            hookManager.initializeObjectHookList(xml.getDocumentElement());
            source.close();
          } catch (Exception e) {
            LOG.error("addWorkflow exception - " + thisFile, e);
          }
        }
      }

      // Find jobs in the fileLibrary
      LOG.info("Checking for custom workflow...");
      File workflowDirectory = new File(hookManager.getApplicationPrefs().get(ApplicationPrefs.FILE_LIBRARY_PATH) + "workflow");
      if (workflowDirectory.exists() && workflowDirectory.isDirectory()) {
        String[] children = workflowDirectory.list();
        for (String thisFilename : children) {
          if (thisFilename.endsWith(".xml")) {
            try {
              LOG.info("Adding workflow from... " + thisFilename);
              File thisFile = new File(workflowDirectory, thisFilename);
              InputStream source = new FileInputStream(thisFile);
              XMLUtils xml = new XMLUtils(source);
              hookManager.initializeBusinessProcessList(xml.getDocumentElement());
              hookManager.initializeObjectHookList(xml.getDocumentElement());
              source.close();
            } catch (Exception e) {
              LOG.error("addWorkflow exception - " + thisFilename, e);
            }
          }
        }
      }
    }
  }
}
