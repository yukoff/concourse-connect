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
package com.concursive.connect.web.portal;

import com.concursive.commons.http.RequestUtils;
import com.concursive.commons.text.StringUtils;
import com.concursive.commons.web.mvc.actions.ActionContext;
import com.concursive.connect.Constants;
import com.concursive.connect.cache.utils.CacheUtils;
import com.concursive.connect.cms.portal.dao.DashboardPage;
import com.concursive.connect.cms.portal.dao.DashboardPortlet;
import com.concursive.connect.cms.portal.dao.DashboardPortletPrefs;
import com.concursive.connect.cms.portal.dao.DashboardTemplateList;
import com.concursive.connect.config.ApplicationPrefs;
import com.concursive.connect.config.ApplicationVersion;
import com.concursive.connect.web.portal.wsrp4j.consumer.proxyportlet.impl.ProducerRegistryImpl;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import oasis.names.tc.wsrp.v1.types.Property;
import oasis.names.tc.wsrp.v1.types.RegistrationData;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pluto.PortletContainer;
import org.apache.pluto.PortletContainerException;
import org.apache.pluto.core.PortletContextManager;
import org.apache.pluto.driver.core.PortalRequestContext;
import org.apache.pluto.driver.core.PortalServletRequest;
import org.apache.pluto.driver.core.PortalServletResponse;
import org.apache.pluto.driver.core.PortletWindowImpl;
import org.apache.pluto.driver.services.portal.PortletWindowConfig;
import org.apache.pluto.driver.url.PortalURL;
import org.apache.pluto.driver.url.PortalURLParser;
import org.apache.wsrp4j.commons.consumer.driver.producer.ProducerImpl;
import org.apache.wsrp4j.commons.consumer.interfaces.producer.ProducerRegistry;
import org.apache.wsrp4j.commons.consumer.util.ConsumerConstants;
import org.apache.wsrp4j.commons.exception.WSRPException;

import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.WindowState;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.sql.Connection;
import java.util.*;
import java.util.concurrent.*;

/**
 * Handles fetching and rendering portlets
 *
 * @author matt rajkowski
 * @version $Id$
 * @created Feb 8, 2007
 */
public class PortletManager {

  public static final String CONCURSIVE_WSRP_PRODUCER_ID = "concursive-wsrp";

  private static final Log LOG = LogFactory.getLog(PortletManager.class);

  /**
   * @param context  The portal application's ActionContext
   * @param db       The database connection the page can use
   * @param thisPage The dashboard page to be rendered
   * @return if processing results in the portlet manager taking control of the dispatching for this request
   * @throws Exception any error
   */
  public static boolean processPage(ActionContext context, Connection db, DashboardPage thisPage) throws Exception {

    LOG.debug("processPage");

    ApplicationPrefs applicationPrefs = (ApplicationPrefs) context.getServletContext().getAttribute("applicationPrefs");

    // The portal is using parameters in the URL, instead of directories, so enable them
    ArrayList<String> moduleParamNames = new ArrayList<String>();
    if (!thisPage.getObjectType().equals(DashboardTemplateList.TYPE_NAVIGATION)) {
      moduleParamNames.add("command");
      moduleParamNames.add("section");
      moduleParamNames.add("pid");
      moduleParamNames.add("dash");
      moduleParamNames.add("name");
      moduleParamNames.add("popup");
    }
    context.getRequest().setAttribute(PortalURLParserImpl.ALLOWED_PORTAL_PARAMETERS, moduleParamNames);

    // Override Pluto's default mechanism for PortalURL
    PortalURLParser parser = null;
    if (thisPage.getObjectType().equals(DashboardTemplateList.TYPE_NAVIGATION)) {
      parser = ProjectPortalURLParserImpl.getParser();
    } else {
      parser = PortalURLParserImpl.getParser();
    }
    context.getRequest().setAttribute("ConcursivePortalURLParser", parser);

    // Get the portlet container
    PortletContainer container = (PortletContainer) context.getServletContext().getAttribute("PortletContainer");

    // Initialize the portlet mappings
    String applicationId = checkRegistryService(context.getServletContext(), context.getServlet().getServletConfig(), context.getRequest());

    // Register ConcourseConnect consumer with the remote wsrp producer
    boolean isConsumerRegistered = checkServicesRegistry(context.getServletContext(), applicationPrefs);

    // Maintain the context for portlet communication this request
    PortalRequestContext portalRequestContext =
        new PortalRequestContext(context.getServletContext(), context.getRequest(), context.getResponse());
    PortalURL portalURL = portalRequestContext.getRequestedPortalURL();

    HashMap<String, Object> eventData = new HashMap<String, Object>();

    // NOTE: WSRP requires a context path to use the resource proxy, however, this prevents WSRP
    // from using the actual web-apps contextPath so URLs cannot be based on that
    String ctx = context.getRequest().getContextPath();
    if (!StringUtils.hasText(ctx)) {
      ctx = "/PlutoInvoker";
    }
    // Override WSRP's context path locator because it can't find a root context
    context.getServletContext().setAttribute(ConsumerConstants.WSRP_PORTLET_CONTEXT_PATH, ctx);

    // Build a list of portlets to process for this request
    ArrayList<PortletTask> portlets = new ArrayList<PortletTask>();

    // Priority 1: Process the lone action if there is one
    // Priority 2: Check the portlet windowState and portletMode so that only
    // the portlet being edited or the portlet that is maximized is rendered
    // Priority 3: Render portlets that generate events
    // Priority 4: Render all the other portlets
    // NOTE: Some portals like to show the minimized portlets in some way,
    // especially when editing content

    // Iterate the specific portlets on this page and add them for processing later
    int maximizedModeId = -1;
    int editModeId = -1;
    String actionWindowId = portalURL.getActionWindow();
    for (DashboardPortlet thisPortlet : thisPage.getPortletList()) {
      // Handle ProxyPortlet instances
      if ("ProxyPortlet".equals(thisPortlet.getName())) {
        if (!isConsumerRegistered) {
          continue;
        }
        DashboardPortletPrefs portletHandle = (DashboardPortletPrefs)
            thisPortlet.getDefaultPreferences().get(ConsumerConstants.WSRP_PORTLET_HANDLE);

        DashboardPortletPrefs producerId = new DashboardPortletPrefs(ConsumerConstants.WSRP_PRODUCER_ID, PortletManager.CONCURSIVE_WSRP_PRODUCER_ID);
        DashboardPortletPrefs parentHandle = new DashboardPortletPrefs(ConsumerConstants.WSRP_PARENT_HANDLE, portletHandle.getValues());
        //inject proxy portlet preferences
        thisPortlet.getDefaultPreferences().put(ConsumerConstants.WSRP_PRODUCER_ID, producerId);
        thisPortlet.getDefaultPreferences().put(ConsumerConstants.WSRP_PARENT_HANDLE, parentHandle);
      }
      // Each portlet needs its own PortletWindow
      String windowConfigId;
      if (thisPortlet.getLoaded()) {
        windowConfigId = applicationId + "." + thisPortlet.getName() + "!" + thisPortlet.getId();
      } else {
        windowConfigId = applicationId + "." + thisPortlet.getName() + "!T" + thisPortlet.getId();
      }
      PortletWindowConfig windowConfig = PortletWindowConfig.fromId(windowConfigId);
      //windowConfig.setContextPath(context.getRequest().getContextPath());
      PortletWindowImpl portletWindow = new PortletWindowImpl(windowConfig, portalURL);
      // NOTE: potential concern when using different parsers
      thisPortlet.setWindowConfigId(PortalURLParserImpl.encodeCharacters(windowConfigId));
      thisPortlet.setPageName(thisPage.getName());

      // A single action
      if (actionWindowId != null) {
        if (actionWindowId.equals(portletWindow.getId().getStringId())) {
          portlets.add(new PortletTask(thisPortlet, portletWindow));
          break;
        }
        continue;
      }

      // A single edited portlet
      if (portletWindow.getPortletMode().equals(PortletMode.EDIT)) {
        portlets.clear();
        portlets.add(new PortletTask(thisPortlet, portletWindow));
        editModeId = thisPortlet.getId();
        break;
      }

      // A single maximized portlet
      if (portletWindow.getWindowState().equals(WindowState.MAXIMIZED)) {
        portlets.clear();
        portlets.add(new PortletTask(thisPortlet, portletWindow));
        maximizedModeId = thisPortlet.getId();
        break;
      }

      // Add generator portlets at the beginning
      if (!thisPortlet.getGenerateDataEvents().isEmpty()) {
        portlets.add(0, new PortletTask(thisPortlet, portletWindow));
      } else {
        portlets.add(new PortletTask(thisPortlet, portletWindow));
      }
    }
    context.getRequest().setAttribute("editModeId", editModeId);
    context.getRequest().setAttribute("maximizedModeId", maximizedModeId);

    // If ACTION then only the portlet the action for is executed
    if (actionWindowId != null) {
      PortletTask thisTask = portlets.get(0);
      DashboardPortlet thisPortlet = thisTask.getPortlet();
      PortletWindowImpl portletWindow = thisTask.getWindow();

      // Pass the request to the specified doAction
      // Since this is an embedded container, portlets can access the data store directly
      portalRequestContext.getRequest().setAttribute("connection", db);
      portalRequestContext.getRequest().setAttribute("dashboardPage", thisPage);
      portalRequestContext.getRequest().setAttribute("applicationPrefs", applicationPrefs);
      portalRequestContext.getRequest().setAttribute("user", context.getSession().getAttribute(Constants.SESSION_USER));
      portalRequestContext.getRequest().setAttribute("dashboardPortlet", thisPortlet);
      portalRequestContext.getRequest().setAttribute("objectHookManager", context.getServletContext().getAttribute("ObjectHookManager"));
      portalRequestContext.getRequest().setAttribute("scheduler", context.getServletContext().getAttribute("Scheduler"));
      portalRequestContext.getRequest().setAttribute("freemarkerConfiguration", context.getServletContext().getAttribute("FreemarkerConfiguration"));
      portalRequestContext.getRequest().setAttribute("TEAM.KEY", context.getServletContext().getAttribute("TEAM.KEY"));
      // If this portlet is requesting session data, provide it to the portlet
      if (!thisPortlet.getConsumeSessionData().isEmpty()) {
        for (String data : thisPortlet.getConsumeSessionData()) {
          String contextName = context.getRequest().getContextPath();
          if (contextName.startsWith("/")) {
            contextName = contextName.substring(1);
          }
          // javax.portlet.p./.RegisterPortlet!T1?TEST=VALUE
          // javax.portlet.p./contextname.RegisterPortlet!T1?TEST=VALUE
          String sessionName = "javax.portlet.p./" + contextName + "." + thisPortlet.getName() + "!T" + thisPortlet.getId() + "?" + data;
          context.getSession().setAttribute(sessionName, context.getSession().getAttribute(data));
        }
      }
      // provide the application's url
      portalRequestContext.getRequest().setAttribute("url", "http://" + RequestUtils.getServerUrl(context.getRequest()));
      // provide the secure url if enabled
      boolean sslEnabled = "true".equals(applicationPrefs.get("SSL"));
      String url = ("http" + (sslEnabled ? "s" : "") + "://" + RequestUtils.getServerUrl(context.getRequest()));
      portalRequestContext.getRequest().setAttribute("secureUrl", url);
      try {
        container.doAction(portletWindow, portalRequestContext.getRequest(), context.getResponse());
      } catch (PortletContainerException ex) {
        throw new ServletException(ex);
      } catch (PortletException ex) {
        throw new ServletException(ex);
      }
      LOG.debug("Action request processed.");
      // no more processing is needed, a portlet action was found
      return true;
    }

    // Render the portlets
    for (PortletTask task : portlets) {
      try {
        DashboardPortlet thisPortlet = task.getPortlet();

        // Bypass ProxyPortlet instances if consumer is not yet registered
        if ("ProxyPortlet".equals(thisPortlet.getName()) && !isConsumerRegistered) {
          continue;
        }

        PortletWindowImpl portletWindow = task.getWindow();
        // Each uses a request/response
        PortalServletRequest portalRequest = new PortalServletRequest(
            context.getRequest(), portletWindow);
        PortalServletResponse portalResponse = new PortalServletResponse(
            context.getResponse());
        // The context path to be used for parsing pluto portlet references
        String contextName = context.getRequest().getContextPath();
        if (contextName != null && contextName.startsWith("/")) {
          contextName = contextName.substring(1);
        }

        // Provide objects to the embedded portlets
        portalRequest.setAttribute("connection", db);
        portalRequest.setAttribute("dashboardPage", thisPage);
        portalRequest.setAttribute("applicationPrefs", applicationPrefs);
        portalRequest.setAttribute("user", context.getSession().getAttribute(Constants.SESSION_USER));
        portalRequest.setAttribute("dashboardPortlet", thisPortlet);
        portalRequest.setAttribute(AbstractPortletModule.COMMAND, thisPortlet.getViewer());
        portalRequest.setAttribute("objectHookManager", context.getServletContext().getAttribute("ObjectHookManager"));
        portalRequest.setAttribute("scheduler", context.getServletContext().getAttribute("Scheduler"));
        portalRequest.setAttribute("freemarkerConfiguration", context.getServletContext().getAttribute("FreemarkerConfiguration"));
        portalRequest.setAttribute("TEAM.KEY", context.getServletContext().getAttribute("TEAM.KEY"));
        portalRequest.setAttribute("projectSearcher", context.getRequest().getAttribute("projectSearcher"));
        portalRequest.setAttribute("baseQueryString", context.getRequest().getAttribute("baseQueryString"));
        // Framework display parameters
        portalRequest.setAttribute("popup", context.getRequest().getParameter("popup"));
        // Script Node and XHR DataSources
        portalRequest.setAttribute("query", context.getRequest().getParameter("query"));
        // provide the application's url
        portalRequest.setAttribute("url", "http://" + RequestUtils.getServerUrl(context.getRequest()));
        // provide the secure url if enabled
        boolean sslEnabled = "true".equals(applicationPrefs.get("SSL"));
        String url = ("http" + (sslEnabled ? "s" : "") + "://" + RequestUtils.getServerUrl(context.getRequest()));
        portalRequest.setAttribute("secureUrl", url);

        // If this portlet is requesting data, provide it to the portlet
        if (!thisPortlet.getConsumeDataEvents().isEmpty()) {
          for (String event : thisPortlet.getConsumeDataEvents()) {
            portalRequest.setAttribute("event" + event, eventData.get(event));
          }
        }

        // Render the portlet, and have the PortalURL know which portlet is being rendered
        LOG.debug("Render windowId: " + portletWindow.getId().getStringId());
        portalURL.setRenderPath(portletWindow.getId().getStringId());

        // Add the base params to this portlet
        if (parser instanceof ProjectPortalURLParserImpl) {
          LOG.debug("Adding all non-portlet specific parameters to portlet scope");
          ProjectPortalURLParserImpl.addAllParameters(context.getRequest(), portalURL);
        }

        boolean renderedNewPortlet = renderPortlet(context, container, thisPage, thisPortlet, portletWindow, portalRequest, portalResponse, isConsumerRegistered);
        if (!renderedNewPortlet) {
          continue;
        }

        // If this portlet is sharing data, and the portlet was just rendered, then get it from the portlet and request
        if (!thisPortlet.getGenerateDataEvents().isEmpty()) {
          if (LOG.isInfoEnabled()) {
            Enumeration test = portalRequest.getAttributeNames();
            while (test.hasMoreElements()) {
              String thisName = (String) test.nextElement();
              LOG.debug("Request attribute: " + thisName);
            }
          }
          for (String event : thisPortlet.getGenerateDataEvents()) {
            // Pluto_/.SearchResultsByProjectPortlet!T3_hits
            // Pluto_/team/.SearchResultsByProjectPortlet!T3_hits
            String thisPortletEvent = "Pluto_/" + contextName + "." + thisPortlet.getName() + "!T" + thisPortlet.getId() + "_event" + event;
            Object thisEvent = portalRequest.getAttribute(thisPortletEvent);
            if (thisEvent == null) {
              LOG.error("Shared event not found in context (" + contextName + ") : " + event);
            }
            eventData.put(event, portalRequest.getAttribute(thisPortletEvent));
          }
        }
        // If this portlet is sharing request data, then get it from the portlet
        if (!thisPortlet.getGenerateRequestData().isEmpty()) {
          for (String attribute : thisPortlet.getGenerateRequestData()) {
            String thisPortletAttributeName = "Pluto_/" + contextName + "." + thisPortlet.getName() + "!T" + thisPortlet.getId() + "_" + attribute;
            Object thisAttribute = portalRequest.getAttribute(thisPortletAttributeName);
            if (thisAttribute == null) {
              LOG.error("Shared request object not found in context (" + contextName + ") : " + attribute);
            }
            portalRequest.setAttribute(attribute, thisAttribute);
          }
        }
      } catch (Exception e) {
        LOG.error("Error loading portlet: " + e.getMessage());
        e.printStackTrace(System.out);
      }
    }
    return false;
  }

  public static synchronized String checkRegistryService(ServletContext context, ServletConfig config, HttpServletRequest request) throws PortletContainerException {
    // Add the registry service which preloads all of the portlets
    String contextPath = (String) context.getAttribute("PortletContextPath");
    if (contextPath == null) {
      PortletContextManager registryService = PortletContextManager.getManager();
      registryService.register(config);
      contextPath = request.getContextPath();
      if (!StringUtils.hasText(contextPath)) {
        // Pluto corrects for using a "/" as the context path
        contextPath = "/";
      }
      context.setAttribute("PortletContextPath", contextPath);
    }
    return contextPath;
  }

  private static synchronized boolean checkServicesRegistry(ServletContext context, ApplicationPrefs prefs) {
    if (!prefs.has("CONCURSIVE_SERVICES.SERVER")) {
      context.setAttribute("isConsumerRegistered", "false");
      return false;
    }
    boolean isConsumerRegistered = StringUtils.isTrue((String) context.getAttribute("isConsumerRegistered"));
    if (!isConsumerRegistered) {
      //load the WSRP producer information
      try {
        String wsrpServer = prefs.get(ApplicationPrefs.CONCURSIVE_SERVICES_SERVER);

        String markupURL = wsrpServer + "/services/WSRPBaseService";
        String serviceDescriptionURL = wsrpServer + "/services/WSRPServiceDescriptionService";
        String registrationURL = wsrpServer + "/services/WSRPRegistrationService";
        String portletManagementURL = wsrpServer + "/services/WSRPPortletManagementService";

        String consumerName = prefs.get(ApplicationPrefs.CONCURSIVE_SERVICES_ID);
        String consumerCode = prefs.get(ApplicationPrefs.CONCURSIVE_SERVICES_KEY);
        String consumerAgent = ApplicationVersion.TITLE + " " + ApplicationVersion.VERSION;

        ProducerImpl producer = new ProducerImpl(
            PortletManager.CONCURSIVE_WSRP_PRODUCER_ID, markupURL, serviceDescriptionURL);
        //ID and misc info
        producer.setID(PortletManager.CONCURSIVE_WSRP_PRODUCER_ID);
        //WSRP registration interface
        producer.setRegistrationInterfaceEndpoint(registrationURL);
        //WSRP portlet management interface
        producer.setPortletManagementInterfaceEndpoint(portletManagementURL);

        //Registration
        RegistrationData registrationData = new RegistrationData();
        registrationData.setConsumerName(consumerName);
        registrationData.setConsumerAgent(consumerAgent);
        if (consumerCode != null) {
          //Send the services key as a registration property; Server will authorize by enforcing a valid key
          Property property = new Property();
          property.setName(ApplicationPrefs.CONCURSIVE_SERVICES_KEY);
          property.setStringValue(consumerCode);
          registrationData.setRegistrationProperties(new Property[1]);
          registrationData.setRegistrationProperties(0, property);
        }
        //producer.setRegistrationData(registrationData);

        //Register the consumer with the remote producer
        LOG.info("Registering ConcourseConnect consumer with remote producer <" + wsrpServer + ">: " + producer.getID());
        producer.register(registrationData);

        ProducerRegistry producerRegistry = ProducerRegistryImpl.getInstance();
        producerRegistry.addProducer(producer);

        isConsumerRegistered = true;
        context.setAttribute("isConsumerRegistered", "true");
      } catch (WSRPException e) {
        isConsumerRegistered = false;
        LOG.error("Unable to register the consumer with the remote WSRP producer");
        context.setAttribute("isConsumerRegistered", "false");
      }
    }
    return isConsumerRegistered;
  }

  private static boolean renderPortlet(ActionContext context, PortletContainer container, DashboardPage thisPage, DashboardPortlet thisPortlet, PortletWindowImpl portletWindow, PortalServletRequest portalRequest, PortalServletResponse portalResponse, boolean isConsumerRegistered) {
    // Portlet Cache Implementation
    // Utilize the cache if the portlet is configured for caching,
    // skip portlets that share data with other portlets
    if (thisPortlet.getCacheTime() > 0 && thisPortlet.getGenerateDataEvents().isEmpty() && thisPortlet.getGenerateRequestData().isEmpty()) {
      // Check the cache for this portlet -- use a system-wide unique key
      String key = thisPage.getName() + "|" + thisPortlet.getWindowConfigId();
      LOG.debug("Checking the cache for key: " + key);
      Ehcache cache = CacheUtils.getCache(Constants.SYSTEM_DASHBOARD_PORTLET_CACHE);
      try {
        Element element = cache.get(key);
        if (element == null) {
          // Render the portlet (important: all other cache.get calls will block until a put is called)
          renderPortlet(context, container, thisPortlet, portletWindow, portalRequest, portalResponse);
          // Set the cache
          String portletCache = portalResponse.getInternalBuffer().toString();
          element = new Element(key, portletCache);
          element.setTimeToLive(thisPortlet.getCacheTime());
          if (LOG.isDebugEnabled()) {
            // Override the TTL for developers to 3 seconds
            element.setTimeToLive(3);
          }
          cache.put(element);
          context.getRequest().setAttribute("portal_response_" + thisPortlet.getId(), portletCache);
          LOG.debug("Adding portlet response to the cache");
          LOG.trace("Render response: " + portletCache);
          return false;
        } else {
          // Use the cached portlet
          LOG.debug("Using the portlet cache value");
          context.getRequest().setAttribute("portal_response_" + thisPortlet.getId(), element.getValue());
          if (LOG.isTraceEnabled()) {
            LOG.trace("Cached portlet data (" + element.getTimeToLive() + "): " + element.getValue());
          }
          return false;
        }
      } catch (Exception e) {
        // Release the lock
        cache.put(new Element(key, null));
        if ("ProxyPortlet".equals(thisPortlet.getName()) && isConsumerRegistered) {
          LOG.debug("Unable to render a ProxyPortlet. Try to deregister first");
        }
        // The portlet could not be rendered so skip it
        LOG.error("Cache exception", e);
        return false;
      }
    } else {
      // No caching is involved, so render the portlet
      return renderPortlet(context, container, thisPortlet, portletWindow, portalRequest, portalResponse);
    }
  }

  public static class PortletRenderTask implements Callable<PortletWindowImpl> {
    private final PortletContainer container;
    private final PortletWindowImpl portletWindow;
    private final PortalServletRequest portalRequest;
    private final PortalServletResponse portalResponse;

    public PortletRenderTask(PortletContainer container, PortletWindowImpl portletWindow, PortalServletRequest portalRequest, PortalServletResponse portalResponse) {
      this.container = container;
      this.portletWindow = portletWindow;
      this.portalRequest = portalRequest;
      this.portalResponse = portalResponse;
    }

    public PortletWindowImpl call() throws Exception {
      try {
        container.doRender(portletWindow, portalRequest, portalResponse);
      } catch (Exception e) {
        LOG.error("Portlet render exception", e);
      }
      return portletWindow;
    }
  }

  private static boolean renderPortlet(ActionContext context, PortletContainer container, DashboardPortlet thisPortlet, PortletWindowImpl portletWindow, PortalServletRequest portalRequest, PortalServletResponse portalResponse) {
    ExecutorService executor = null;
    List<Future<PortletWindowImpl>> futures = null;
    try {
      long doRenderStartTime = System.currentTimeMillis();
      // NOTE: The infrastructure is here to run in threads, but more work needs to be done
      // for this to be reliable.  So the timeout feature of Executor cannot be used yet
//      if (thisPortlet.getTimeout() <= 0) {
      if (true) {
        // Render the portlet immediately
        container.doRender(portletWindow, portalRequest, portalResponse);
      } else {
        // Render the portlet using an executor; this will allow for cancelling timed-out portlets
        LOG.debug("Using executor...");
        List<PortletRenderTask> renderTasks = new ArrayList<PortletRenderTask>();
        renderTasks.add(new PortletRenderTask(container, portletWindow, portalRequest, portalResponse));
        executor = Executors.newFixedThreadPool(1);
        // NOTE: this wrapper fix is for Java 1.5
        final Collection<Callable<PortletWindowImpl>> wrapper =
            Collections.<Callable<PortletWindowImpl>>unmodifiableCollection(renderTasks);
        if (thisPortlet.getTimeout() <= 0) {
          futures = executor.invokeAll(wrapper);
        } else {
          futures = executor.invokeAll(wrapper, thisPortlet.getTimeout(), TimeUnit.SECONDS);
        }
        for (Future<PortletWindowImpl> f : futures) {
          if (f.isCancelled()) {
            LOG.debug("Portlet was cancelled due to timeout");
            return false;
          }
        }
      }
      long doRenderEndTime = System.currentTimeMillis();
      LOG.debug("Portlet (" + thisPortlet.getName() + ") took: " + (doRenderEndTime - doRenderStartTime) + " ms");
      // When the portlet is rendered, place the response in the request for displaying later
      context.getRequest().setAttribute("portal_response_" + thisPortlet.getId(), portalResponse.getInternalBuffer().toString());
      LOG.trace("Render response: " + portalResponse.getInternalBuffer().toString());
    } catch (Exception re) {
      // The portlet could not be rendered so skip it
      LOG.error("Render exception", re);
      return false;
    } finally {
      if (executor != null) {
        executor.shutdown();
      }
    }
    return true;
  }
}
