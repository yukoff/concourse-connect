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

package com.concursive.connect.web.listeners;

import com.concursive.commons.db.ConnectionPool;
import com.concursive.commons.workflow.ObjectHookManager;
import com.concursive.commons.workflow.WorkflowManager;
import com.concursive.connect.Constants;
import com.concursive.connect.cms.portal.utils.Tracker;
import com.concursive.connect.indexer.IIndexerService;
import com.concursive.connect.indexer.IndexerFactory;
import com.concursive.connect.scheduler.ScheduledJobs;
import com.concursive.connect.web.portal.ContainerServicesImpl;
import com.concursive.connect.web.portal.PortletManager;
import com.concursive.connect.web.portal.wsrp4j.consumer.proxyportlet.impl.ProducerRegistryImpl;
import com.concursive.connect.web.webdav.WebdavManager;
import net.sf.ehcache.CacheManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pluto.PortletContainer;
import org.apache.pluto.PortletContainerFactory;
import org.apache.wsrp4j.commons.consumer.driver.producer.ProducerImpl;
import org.apache.wsrp4j.commons.consumer.interfaces.producer.ProducerRegistry;
import org.apache.wsrp4j.commons.exception.WSRPException;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;


/**
 * Responsible for initialization and cleanup when the web-app is loaded/reloaded
 *
 * @author matt rajkowski
 * @version $Id$
 * @created December 22, 2002
 */
public class ContextListener implements ServletContextListener {

  private static Log LOG = LogFactory.getLog(ContextListener.class);

  /**
   * Constructor for the ContextListener object
   */
  public ContextListener() {
  }


  /**
   * Code initialization for global objects like ConnectionPools
   *
   * @param event Description of the Parameter
   */
  public void contextInitialized(ServletContextEvent event) {
    ServletContext context = event.getServletContext();
    LOG.info("Initializing");
    // Start the connection pool
    try {
      ConnectionPool cp = new ConnectionPool();
      cp.setDebug(true);
      cp.setTestConnections(false);
      cp.setAllowShrinking(true);
      cp.setMaxConnections(10);
      cp.setMaxIdleTime(60000);
      cp.setMaxDeadTime(300000);
      context.setAttribute(Constants.CONNECTION_POOL, cp);
      LOG.info("Connection pool added");
    } catch (Exception e) {
      LOG.error("Connection pool error", e);
    }

    // Start the cache manager (caches are configured later)
    CacheManager.create();
    LOG.info("CacheManager created");
    // Start the work flow manager
    try {
      // Workflow manager
      WorkflowManager wfManager = new WorkflowManager();
      context.setAttribute(Constants.WORKFLOW_MANAGER, wfManager);
      // Hook manager
      ObjectHookManager hookManager = new ObjectHookManager();
      hookManager.setWorkflowManager(wfManager);
      context.setAttribute(Constants.OBJECT_HOOK_MANAGER, hookManager);
      LOG.info("Workflow manager added");
    } catch (Exception e) {
      LOG.error("Workflow manager error", e);
    }

    // Setup a web tracker
    Tracker tracker = new Tracker();
    context.setAttribute(Constants.USER_SESSION_TRACKER, tracker);

    // Setup Webdav Manager
    WebdavManager webdavManager = new WebdavManager();
    context.setAttribute(Constants.WEBDAV_MANAGER, webdavManager);
    LOG.info("Webdav manager added");

    // Setup scheduler
    try {
      SchedulerFactory schedulerFactory = new org.quartz.impl.StdSchedulerFactory();
      Scheduler scheduler = schedulerFactory.getScheduler();
      context.setAttribute(Constants.SCHEDULER, scheduler);
      LOG.info("Scheduler added");
    } catch (Exception e) {
      LOG.error("Scheduler error", e);
    }

    // Portlet container
    try {
      // Add the Container
      PortletContainerFactory portletFactory = PortletContainerFactory.getInstance();
      ContainerServicesImpl services = new ContainerServicesImpl();
      PortletContainer portletContainer =
          portletFactory.createContainer("PortletContainer", services, services);
      portletContainer.init(context);
      context.setAttribute(Constants.PORTLET_CONTAINER, portletContainer);
      LOG.info("PortletContainer added");
    } catch (Exception e) {
      LOG.error("PortletContainer error", e);
    }

    // Finished
    LOG.info("Initialized");
  }


  /**
   * All objects that should not be persistent can be removed from the context
   * before the next reload
   *
   * @param event Description of the Parameter
   */
  public void contextDestroyed(ServletContextEvent event) {
    ServletContext context = event.getServletContext();
    LOG.info("Shutting down");

    // Remove scheduler
    try {
      Scheduler scheduler = (Scheduler) context.getAttribute(Constants.SCHEDULER);
      if (scheduler != null) {
        // Remove the App connection pool
        ConnectionPool appCP = (ConnectionPool) scheduler.getContext().get("ConnectionPool");
        // Interrupt any interruptable jobs
        scheduler.interrupt("directoryIndexer", (String) scheduler.getContext().get(ScheduledJobs.CONTEXT_SCHEDULER_GROUP));
        // Cleanup the scheduler
        scheduler.getContext().remove(appCP);
        int count = scheduler.getCurrentlyExecutingJobs().size();
        if (count > 0) {
          LOG.info("Waiting for scheduler jobs to finish executing... (" + count + ")");
        }
        scheduler.shutdown(true);
        LOG.info("Scheduler shutdown");
        if (appCP != null) {
          appCP.closeAllConnections();
          appCP.destroy();
        }
        context.removeAttribute(Constants.SCHEDULER);
      }
    } catch (Exception e) {
      LOG.error("Scheduler error", e);
    }

    // Remove the tracker
    context.removeAttribute(Constants.USER_SESSION_TRACKER);

    // Stop the object hook manager
    ObjectHookManager hookManager = (ObjectHookManager) context.getAttribute(Constants.OBJECT_HOOK_MANAGER);
    if (hookManager != null) {
      hookManager.reset();
      hookManager.shutdown();
      context.removeAttribute(Constants.OBJECT_HOOK_MANAGER);
    }

    // Stop the work flow manager
    WorkflowManager wfManager = (WorkflowManager) context.getAttribute(Constants.WORKFLOW_MANAGER);
    if (wfManager != null) {
      context.removeAttribute(Constants.WORKFLOW_MANAGER);
    }

    //De-register the remote wsrp producer
    ProducerRegistry producerRegistry = ProducerRegistryImpl.getInstance();
    ProducerImpl producer = (ProducerImpl) producerRegistry
        .getProducer(PortletManager.CONCURSIVE_WSRP_PRODUCER_ID);
    if (producer != null) {
      try {
        producer.deregister();
        LOG.info("Successfully deregistered remote wsrp producer");
      } catch (WSRPException e) {
        LOG.error("Unable to de-register the remote wsrp producer");
      }
    }

    // Stop the portlet container
    PortletContainer container = (PortletContainer) context.getAttribute(Constants.PORTLET_CONTAINER);
    if (container != null) {
      try {
        container.destroy();
      } catch (Exception e) {
        LOG.error("PortletContainer error", e);
      }
      context.removeAttribute(Constants.PORTLET_CONTAINER);
    }

    // Remove the cache manager
    CacheManager.getInstance().shutdown();

    // TODO: Create a connection pool array
    // Remove the connection pool
    ConnectionPool cp = (ConnectionPool) context.getAttribute(Constants.CONNECTION_POOL);
    if (cp != null) {
      cp.closeAllConnections();
      cp.destroy();
      context.removeAttribute(Constants.CONNECTION_POOL);
    }

    // Remove the RSS connection pool
    ConnectionPool rssCP = (ConnectionPool) context.getAttribute(Constants.CONNECTION_POOL_RSS);
    if (rssCP != null) {
      rssCP.closeAllConnections();
      rssCP.destroy();
      context.removeAttribute(Constants.CONNECTION_POOL_RSS);
    }

    // Remove the API connection pool
    ConnectionPool apiCP = (ConnectionPool) context.getAttribute(Constants.CONNECTION_POOL_API);
    if (apiCP != null) {
      apiCP.closeAllConnections();
      apiCP.destroy();
      context.removeAttribute(Constants.CONNECTION_POOL_API);
    }

    // Shutdown the indexer service
    IIndexerService indexer = IndexerFactory.getInstance().getIndexerService();
    if (indexer != null) {
      try {
        indexer.shutdown();
      } catch (Exception e) {
        LOG.error("Error shutting down indexer", e);
      }
    }

    // Remove system settings
    context.removeAttribute(Constants.SYSTEM_SETTINGS);

    // Remove the logger
    ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
    LogFactory.release(contextClassLoader);

    LOG.info("Shutdown complete");
  }
}
