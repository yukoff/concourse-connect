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

import com.concursive.commons.date.DateUtils;
import com.concursive.commons.db.DatabaseUtils;
import com.concursive.commons.objects.ObjectUtils;
import com.concursive.commons.text.StringUtils;
import com.concursive.commons.web.mvc.beans.GenericBean;
import com.concursive.commons.workflow.ObjectHookAction;
import com.concursive.commons.workflow.ObjectHookManager;
import com.concursive.connect.cache.utils.CacheUtils;
import com.concursive.connect.cms.portal.dao.DashboardPage;
import com.concursive.connect.cms.portal.dao.DashboardPortlet;
import com.concursive.connect.config.ApplicationPrefs;
import com.concursive.connect.indexer.IndexEvent;
import com.concursive.connect.scheduler.ScheduledJobs;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.members.dao.TeamMember;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import com.concursive.connect.web.utils.LookupList;
import com.concursive.connect.web.utils.PagedListInfo;
import freemarker.template.Configuration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Scheduler;

import javax.portlet.*;
import java.io.IOException;
import java.security.Key;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.*;

/**
 * Allowed portal capabilities accessible by embedded portlets
 * <p/>
 * User: matt rajkowski
 * Date: Jan 19, 2008
 */
public class PortalUtils {

  public final static String fs = System.getProperty("file.separator");
  public final static String lf = System.getProperty("line.separator");
  private static Log LOG = LogFactory.getLog(PortalUtils.class);

  public PortalUtils() {
  }

  public static Connection getConnection(PortletRequest request) {
    return (Connection) request.getAttribute("connection");
  }

  public static DashboardPage getDashboardPage(PortletRequest request) {
    return (DashboardPage) request.getAttribute("dashboardPage");
  }

  public static Project getProject(PortletRequest request) {
    DashboardPage dashboard = getDashboardPage(request);
    if (dashboard != null && dashboard.getProjectId() > -1) {
      return ProjectUtils.loadProject(dashboard.getProjectId());
    } else {
      return null;
    }
  }

  public static Project findProject(PortletRequest request) {
    Project project = null;

    // Use the specified project in preferences first
    String uniqueId = request.getPreferences().getValue("project", null);
    if (uniqueId != null) {
      int projectId = ProjectUtils.retrieveProjectIdFromUniqueId(uniqueId);
      if (projectId > -1) {
        project = ProjectUtils.loadProject(projectId);
      }
    }

    // This portlet can consume data from other portlets
    if (project == null) {
      for (String event : PortalUtils.getDashboardPortlet(request).getConsumeDataEvents()) {
        Object object = PortalUtils.getGeneratedData(request, event);
        if (object instanceof Project) {
          project = (Project) PortalUtils.getGeneratedData(request, event);
        }
      }
    }

    // Object from the portal
    if (project == null) {
      project = PortalUtils.getProject(request);
    }
    request.setAttribute("project", project);
    return project;
  }

  public static User getUser(PortletRequest request) {
    return (User) request.getAttribute("user");
  }

  public static ApplicationPrefs getApplicationPrefs(PortletRequest request) {
    return (ApplicationPrefs) request.getAttribute("applicationPrefs");
  }

  public static DashboardPortlet getDashboardPortlet(PortletRequest request) {
    return (DashboardPortlet) request.getAttribute("dashboardPortlet");
  }

  public static TeamMember getCurrentTeamMember(PortletRequest request) {
    return (TeamMember) request.getAttribute("currentMember");
  }

  public static void setGeneratedData(PortletRequest request, String event, Object data) {
    LOG.debug("Setting generatedData for event: " + event);
    request.setAttribute("event" + event, data);
  }

  public static Object getGeneratedData(PortletRequest request, String event) {
    if (request.getAttribute("event" + event) == null) {
      LOG.warn("getGeneratedData IS NULL for " + event);
    }
    return request.getAttribute("event" + event);
  }

  public static int getUserLevel(int roleLevel) {
    LookupList roleList = CacheUtils.getLookupList("lookup_project_role");
    return roleList.getIdFromLevel(roleLevel);
  }

  public static Key getApplicationKey(PortletRequest request) {
    return (Key) request.getAttribute("TEAM.KEY");
  }


  public static String getApplicationUrl(PortletRequest request) {
    if (request.getAttribute("secureUrl") != null) {
      return (String) request.getAttribute("secureUrl");
    } else {
      return (String) request.getAttribute("url");
    }
  }

  public static String getPageAction(PortletRequest request) {
    return (String) request.getAttribute("portletAction");
  }

  public static String getPageDomainObject(PortletRequest request) {
    return (String) request.getAttribute("portletDomainObject");
  }

  public static String getPageView(PortletRequest request) {
    return (String) request.getAttribute("portletView");
  }

  public static int getPageViewAsInt(PortletRequest request) {
    String value = getPageView(request);
    if (!StringUtils.hasText(value)) {
      return -1;
    }
    return Integer.parseInt(value);
  }

  public static String getPageParameter(PortletRequest request) {
    String[] params = PortalUtils.getPageParameters(request);
    if (params != null && params.length > 0) {
      return params[0];
    }
    return null;
  }

  public static int getPageParameterAsInt(PortletRequest request) {
    String param = getPageParameter(request);
    if (param != null) {
      return Integer.parseInt(param);
    }
    return -1;
  }

  public static double getPageParameterAsDouble(PortletRequest request) {
    String param = getPageParameter(request);
    if (StringUtils.hasText(param)) {
      return Double.parseDouble(param);
    }
    return -1.0;
  }

  public static String[] getPageParameters(PortletRequest request) {
    String paramsString = (String) request.getAttribute("portletParams");
    if (paramsString != null) {
      String[] params = paramsString.split("\\+");
      if (params != null && params.length > 0) {
        return params;
      }
    }
    return null;
  }

  public static String getQueryParameter(PortletRequest request, String paramName) {
    Map<String, String> paramMap = getQueryParameterMap(request);
    if (paramMap != null) {
      if (paramMap.containsKey(paramName)) {
        return paramMap.get(paramName);
      }
    }
    return null;
  }

  /**
   * Processes the query string in the requestedURL attribute
   *
   * @param request
   * @return
   */
  public static Map<String, String> getQueryParameterMap(PortletRequest request) {
    String requestedURLString = ((String) request.getAttribute("requestedURL"));
    if (requestedURLString.indexOf("?") != -1) {
      String queryString = requestedURLString.substring(requestedURLString.indexOf("?") + 1);
      String params[] = queryString.split("&");
      if (params != null && params.length > 0) {
        Map<String, String> paramMap = new HashMap<String, String>();
        for (String param : params) {
          String[] paramParts = param.split("=");
          if (paramParts != null && paramParts.length == 2) {
            String paramName = paramParts[0];
            String paramValue = paramParts[1];
            paramMap.put(paramName, paramValue);
          }
        }
        if (paramMap.size() > 0) {
          return paramMap;
        }
      }
    }
    return null;
  }

  public static void processInsertHook(PortletRequest request, Object object) {
    process(request, ObjectHookAction.INSERT, null, object);
  }

  public static void processUpdateHook(PortletRequest request, Object previousObject, Object object) {
    process(request, ObjectHookAction.UPDATE, previousObject, object);
  }

  public static void processSelectHook(PortletRequest request, Object object) {
    process(request, ObjectHookAction.SELECT, null, object);
  }

  public static void processDeleteHook(PortletRequest request, Object previousObject) {
    process(request, ObjectHookAction.DELETE, previousObject, null);
  }

  private static void process(PortletRequest request, int action, Object previousObject, Object object) {
    // Required objects for workflow
    User user = (User) request.getAttribute("user");
    int userId = -1;
    if (user != null) {
      userId = user.getId();
    }
    String url = (String) request.getAttribute("url");
    String secureUrl = (String) request.getAttribute("secureUrl");
    // Find the ObjectHookManager instance and execute the process asynchronously
    ObjectHookManager hookManager = (ObjectHookManager) request.getAttribute("objectHookManager");
    hookManager.process(action, previousObject, object, userId, url, secureUrl);
  }


  public static Scheduler getScheduler(PortletRequest request) {
    return (Scheduler) request.getAttribute("scheduler");
  }

  public static boolean indexAddItem(PortletRequest request, Object item) throws IOException {
    if (item == null) {
      return false;
    }
    Scheduler scheduler = getScheduler(request);
    try {
      IndexEvent indexEvent = new IndexEvent(item, IndexEvent.ADD);
      ((Vector) scheduler.getContext().get("IndexArray")).add(indexEvent);
      scheduler.triggerJob("indexer", (String) scheduler.getContext().get(ScheduledJobs.CONTEXT_SCHEDULER_GROUP));
    } catch (Exception e) {
      LOG.error("indexAddItem failed: " + e.getMessage());
    }
    return true;
  }

  public static boolean indexDeleteItem(PortletRequest request, Object item) throws IOException {
    if (item == null) {
      return false;
    }
    Scheduler scheduler = getScheduler(request);
    try {
      IndexEvent indexEvent = new IndexEvent(item, IndexEvent.DELETE);
      ((Vector) scheduler.getContext().get("IndexArray")).add(indexEvent);
      scheduler.triggerJob("indexer", (String) scheduler.getContext().get(ScheduledJobs.CONTEXT_SCHEDULER_GROUP));
    } catch (Exception e) {
      LOG.error("indexDeleteItem failed: " + e.getMessage());
    }
    return true;
  }

  public static String getServerDomainNameAndPort(PortletRequest request) {
    String serverName = request.getServerName();
    int port = request.getServerPort();
    return (serverName + (port != 80 && port != 443 ? ":" + String.valueOf(port) : ""));
  }

  public static void processErrors(PortletRequest request, HashMap errors) {
    for (Object o : errors.keySet()) {
      String errorKey = (String) o;
      String errorMsg = (String) errors.get(errorKey);
      request.setAttribute(errorKey, errorMsg);
    }
    request.setAttribute("errors", errors);
    if (errors.size() > 0) {
      if (request.getAttribute("actionError") == null) {
        request.setAttribute("actionError", "Form could not be submitted, review messages below.");
      }
    }
  }

  public static void setFormBean(ActionRequest request, Object object) {
    PortletSession session = request.getPortletSession();
    session.setAttribute(AbstractPortletModule.FORM_BEAN, object);
  }

  public static Object getFormBean(RenderRequest request, String beanName, Class beanClass) {
    // The RenderRequest will not auto-populate, but looks for an existing form bean from the action
    Object beanRef = request.getAttribute(AbstractPortletModule.FORM_BEAN);
    if (beanRef == null) {
      // Construct the bean if not found
      try {
        beanRef = beanClass.newInstance();
      } catch (InstantiationException ie) {
        LOG.error("Instantiation Exception. MESSAGE = " + ie.getMessage(), ie);
      } catch (IllegalAccessException iae) {
        LOG.error("Illegal Access Exception. MESSAGE = " + iae.getMessage(), iae);
      }
    }
    if (beanRef != null) {
      // Add it to the request
      request.setAttribute(beanName, beanRef);
    }
    return beanRef;
  }

  public static Object getFormBean(ActionRequest request, Class beanClass) {
    try {
      // Construct the bean
      Object beanRef = beanClass.newInstance();
      // The ActionRequest will auto-populate any values
      populateObject(beanRef, request);
      return beanRef;
    } catch (InstantiationException ie) {
      LOG.error("Instantiation Exception. MESSAGE = " + ie.getMessage(), ie);
    } catch (IllegalAccessException iae) {
      LOG.error("Illegal Access Exception. MESSAGE = " + iae.getMessage(), iae);
    }
    return null;
  }

  public static void processFormBean(RenderRequest request) {
    // Retrieve a form from the portlet session, turn it into a request
    // attribute and remove the session attribute
    // TODO: Use a URL token for managing the specific bean instance per request
    // for thread safety
    PortletSession session = request.getPortletSession(false);
    if (session != null) {
      GenericBean formBean = (GenericBean) session.getAttribute(AbstractPortletModule.FORM_BEAN);
      if (formBean != null) {
        // Manage the scope
        request.setAttribute(AbstractPortletModule.FORM_BEAN, formBean);
        session.removeAttribute(AbstractPortletModule.FORM_BEAN);
        // Populate the error properties for the view
        if (formBean.hasErrors()) {
          // Add errors from the bean
          for (Object o : formBean.getErrors().keySet()) {
            String errorKey = (String) o;
            String errorMsg = (String) formBean.getErrors().get(errorKey);
            request.setAttribute(errorKey, errorMsg);
          }
          request.setAttribute("errors", formBean.getErrors());
          // Add errors from the action
          String actionError = (String) formBean.getErrors().get("actionError");
          if (actionError == null) {
            actionError = "Form could not be submitted, review messages below.";
          }
          request.setAttribute("actionError", actionError);
        }
      }
    }
  }

  public static void populateObject(Object bean, ActionRequest request) {
    String nestedAttribute = "_";

    Enumeration en = request.getParameterNames();
    String paramName = null;
    while (en.hasMoreElements()) {
      paramName = (String) en.nextElement();

      // a form has been submitted and requested to be auto-populated,
      // so we do that here..going through every element and trying
      // to call a setXXX() method on the bean object passed in for the value
      // of the request parameter currently being checked.
      String[] paramValues = request.getParameterValues(paramName);
      if (paramValues.length > 1) {
        ObjectUtils.setParam(bean, paramName, paramValues, nestedAttribute);
      } else {
        ObjectUtils.setParam(bean, paramName, paramValues[0], nestedAttribute);
      }
    }

    // TODO: currently for ticket and ticket history
    //ObjectUtils.invokeMethod(bean, "setRequestItems", new HttpRequestContext(request));
    // Check for valid user
    User thisUser = (User) request.getAttribute("user");
    if (thisUser != null) {
      // Populate date/time fields using the user's timezone and locale
      if (thisUser.getTimeZone() != null) {
        ArrayList timeParams = (ArrayList) ObjectUtils.getObject(bean, "TimeZoneParams");
        if (timeParams != null) {
          Calendar cal = Calendar.getInstance();
          Iterator i = timeParams.iterator();
          while (i.hasNext()) {
            // The property that can be set
            String name = (String) i.next();
            // See if it is in the request
            String value = request.getParameter(name);
            if (value != null) {
              // See if time is in request too
              String hourValue = request.getParameter(name + "Hour");
              if (hourValue == null) {
                // Date fields: 1-1 mapping between HTML field and Java property
                ObjectUtils.setParam(bean, name, DateUtils.getUserToServerDateTimeString(TimeZone.getTimeZone(thisUser.getTimeZone()), DateFormat.SHORT, DateFormat.LONG, value, thisUser.getLocale()));
              } else {
                // Date & Time fields: 4-1 mapping between HTML fields and Java property
                try {
                  Timestamp timestamp = DatabaseUtils.parseDateToTimestamp(value, thisUser.getLocale());
                  cal.setTimeInMillis(timestamp.getTime());
                  int hour = Integer.parseInt(hourValue);
                  int minute = Integer.parseInt(request.getParameter(name + "Minute"));
                  String ampmString = request.getParameter(name + "AMPM");
                  if (ampmString != null) {
                    int ampm = Integer.parseInt(ampmString);
                    if (ampm == Calendar.AM) {
                      if (hour == 12) {
                        hour = 0;
                      }
                    } else {
                      if (hour < 12) {
                        hour += 12;
                      }
                    }
                  }
                  cal.set(Calendar.HOUR_OF_DAY, hour);
                  cal.set(Calendar.MINUTE, minute);
                  cal.setTimeZone(TimeZone.getTimeZone(thisUser.getTimeZone()));
                  ObjectUtils.setParam(bean, name, new Timestamp(cal.getTimeInMillis()));
                } catch (Exception dateE) {
                }
              }
            }
          }
        }
      }

      // Populate number fields using the user's locale
      if (thisUser.getLocale() != null) {
        ArrayList numberParams = (ArrayList) ObjectUtils.getObject(bean, "NumberParams");
        if (numberParams != null) {
          NumberFormat nf = NumberFormat.getInstance(thisUser.getLocale());
          Iterator i = numberParams.iterator();
          while (i.hasNext()) {
            // The property that can be set
            String name = (String) i.next();
            // See if it is in the request
            String value = (String) request.getParameter(name);
            if (value != null && !"".equals(value)) {
              try {
                // Parse the value
                ObjectUtils.setParam(bean, name, nf.parse(value).doubleValue());
              } catch (Exception e) {
                //e.printStackTrace(System.out);
              }
            }
          }
        }
      }
    }
  }

  /**
   * When a form is submitted with enctype="multipart/form-data", then the
   * parameters and values are placed into a parts HashMap which can now
   * be auto-populated
   *
   * @param bean
   * @param parts
   */
  public static void populateObject(Object bean, HashMap parts) {
    if (parts != null) {
      Iterator names = parts.keySet().iterator();
      while (names.hasNext()) {
        String paramName = (String) names.next();
        Object paramValues = parts.get(paramName);
        if (paramValues != null && paramValues instanceof String) {
          ObjectUtils.setParam(bean, paramName, paramValues, "_");
        }
      }
    }
  }

  /**
   * Handles caching an instance specific paged list info
   *
   * @param request  used for storing the pagedlist
   * @param viewName the name to use for the pagedListInfo
   * @return a cached or new PagedListInfo
   */
  public static PagedListInfo getPagedListInfo(PortletRequest request, String viewName) {
    PagedListInfo tmpInfo = new PagedListInfo();
    tmpInfo.setId(viewName);
    request.setAttribute(viewName, tmpInfo);

    LOG.debug("View: " + PortalUtils.getPageView(request));
    LOG.debug("Param: " + PortalUtils.getPageParameter(request));

    // Determine the paging info
    String offsetStr = request.getParameter("offset");
    int offset = offsetStr == null ? 0 : Integer.parseInt(offsetStr);
    tmpInfo.setCurrentOffset(offset);
    //searchBeanInfo.setRenderParameters(searchBean.getParameterMap());
    tmpInfo.setContextPath(request.getContextPath());

    return tmpInfo;
  }

  // TODO: Ask why this method exists... curious about specific use for portlets
  public static Project retrieveAuthorizedProject(int projectId, PortletRequest request) throws SQLException {
    // Get the project from cache
    Project project = ProjectUtils.loadProject(projectId);
    // Check the user's permission
    User thisUser = PortalUtils.getUser(request);
    if (thisUser.getAccessAdmin()) {
      return project;
    }
    // Allowed reasons to retrieve a project (permissions will be validated elsewhere)
    if (project.getTeam().hasUserId(thisUser.getId()) ||
        project.getFeatures().getAllowGuests() ||
        project.getPortal()) {
      return project;
    }
    project = new Project();
    project.setId(projectId);
    return project;
  }

  public static String getFileLibraryPath(PortletRequest request, String moduleFolderName) {
    return (
        getApplicationPrefs(request).get("FILELIBRARY") +
            getUser(request).getGroupId() + fs +
            moduleFolderName + fs);
  }

  public static Configuration getFreemarkerConfiguration(PortletRequest request) {
    return (Configuration) request.getAttribute("freemarkerConfiguration");
  }

  /**
   * This call will close panels and perform redirects based on whether a popup
   * is used and whether a redirectTo is specified by the calling a href
   *
   * @param request
   * @param response
   * @param defaultUrl
   * @return
   * @throws java.io.IOException
   */
  public static GenericBean performRefresh(ActionRequest request, ActionResponse response, String defaultUrl) throws java.io.IOException {
    String ctx = request.getContextPath();
    boolean isPopup = "true".equals(request.getParameter("popup"));
    if (request.getParameter("redirectTo") != null) {
      // Redirect to the suggested location
      response.sendRedirect(ctx + "/redirect302.jsp?redirectTo=" + request.getParameter("redirectTo") + (isPopup ? "&popup=true" : ""));
    } else {
      if (isPopup) {
        // Close the panel without a redirect
        response.sendRedirect(ctx + "/projects_center_panel_refresh.jsp");
      } else {
        // Use the default redirect
        int offset = 0;
        if (defaultUrl.startsWith("/")) {
          offset = 1;
        }
        String[] url = defaultUrl.split("[/]");
        if (url.length > offset) {
          response.setRenderParameter("portlet-action", url[offset]);
        }
        if (url.length > 1 + offset) {
          response.setRenderParameter("portlet-object", url[1 + offset]);
        }
        if (url.length > 2 + offset) {
          response.setRenderParameter("portlet-value", url[2 + offset]);
        }
      }
    }
    return null;
  }

  public static String getPortletUniqueKey(PortletRequest request) {
    DashboardPortlet portlet = PortalUtils.getDashboardPortlet(request);
    if (portlet.getPortletId() > -1) {
      return String.valueOf(portlet.getPortletId());
    } else {
      return portlet.getPageName() + "|" + portlet.getWindowConfigId();
    }
  }

  public static boolean canShowSensitiveData(PortletRequest request) {
    // Use the application prefs
    ApplicationPrefs prefs = PortalUtils.getApplicationPrefs(request);
    return PortalUtils.getDashboardPortlet(request).isSensitive() &&
        "true".equals(prefs.get(ApplicationPrefs.INFORMATION_IS_SENSITIVE));
  }
}
