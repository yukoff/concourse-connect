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
package com.concursive.connect.web.modules.scrum.portlets;

import com.concursive.connect.web.modules.calendar.utils.CalendarView;
import com.concursive.commons.date.DateUtils;
import com.concursive.commons.db.DatabaseUtils;
import com.concursive.commons.text.StringUtils;
import com.concursive.connect.Constants;
import com.concursive.connect.cms.portal.dao.ProjectItem;
import com.concursive.connect.cms.portal.dao.ProjectItemList;
import com.concursive.connect.web.modules.calendar.utils.CalendarBean;
import com.concursive.connect.web.modules.lists.dao.TaskCategoryList;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.portal.PortalUtils;

import javax.portlet.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Displays target releases and associated stories and a list of status
 *
 * @author matt rajkowski
 * @version $Id$
 * @created February 27, 2008
 */
public class ScrumReleaseBurnDownPortlet extends GenericPortlet {
  private static final String VIEW_PAGE = "/portlets/scrum_release_burn_down/scrum_release_burn_down-view.jsp";
  private static final String EDIT_PAGE = "/portlets/scrum_release_burn_down/scrum_release_burn_down-edit.jsp";
  private static final String HELP_PAGE = "/portlets/scrum_release_burn_down/scrum_release_burn_down-help.jsp";
  private static final String NOT_CONFIGURED_PAGE = "/portlets/not_configured.jsp";

  // GenericPortlet Impl -----------------------------------------------------

  public void doView(RenderRequest request, RenderResponse response)
      throws PortletException, IOException {
    try {
      PortletContext context = getPortletContext();
      //
      Locale locale = PortalUtils.getUser(request).getLocale();
      CalendarBean calendarInfo = new CalendarBean(locale);
      calendarInfo.update(PortalUtils.getUser(request));
      CalendarView calendarView = new CalendarView(calendarInfo, locale);
      calendarView.addHolidays();

      // Preferences
      String releaseValue = request.getPreferences().getValue("releaseId", "-1");
      String daysToShow = request.getPreferences().getValue("daysToShow", "45");
      Double estimatedHours = Double.parseDouble(request.getPreferences().getValue("estimatedHours", "0.0"));
      Double workHours = Double.parseDouble(request.getPreferences().getValue("workHours", "7.0"));

      String startDateValue = request.getPreferences().getValue("startDate", "1/23/2008");
      SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
      Date startDate = formatter.parse(startDateValue);
      String chartHeight = request.getPreferences().getValue("chartHeight", "250");

      // Configuration check
      if ("-1".equals(releaseValue)) {
        // Portlet is not configured
        PortletRequestDispatcher requestDispatcher =
            context.getRequestDispatcher(NOT_CONFIGURED_PAGE);
        requestDispatcher.include(request, response);
      } else {
        // Portlet is configured
        int releaseId = Integer.parseInt(releaseValue);

        Connection db = PortalUtils.useConnection(request);
        Project thisProject = PortalUtils.getProject(request);

        ProjectItem release = new ProjectItem(db, ProjectItemList.LIST_TARGET_RELEASE, releaseId);
        request.setAttribute("release", release);

        HashMap<String, HashMap<Integer, Double>> dateMap =
            new HashMap<String, HashMap<Integer, Double>>();

        // The task can be updated 0, 1, or more times per day, so get the latest
        PreparedStatement pst = db.prepareStatement(
            "SELECT " +
                DatabaseUtils.castDateTimeToDate(db, "tl.entered") + " AS status_date, " +
                "task_id, " +
                "item_name AS remaining " +
                "FROM tasklog tl " +
                "LEFT JOIN lookup_task_loe_remaining tlr ON tl.loe_remaining = tlr.code " +
                "WHERE target_release = ? " +
                "ORDER BY tl.entered, task_id ");
        pst.setInt(1, releaseId);
        ResultSet rs = pst.executeQuery();
        while (rs.next()) {
          // Process result set
          java.util.Date statusDate = rs.getDate("status_date");
          int taskId = rs.getInt("task_id");
          String remainingValue = rs.getString("remaining");
          if (remainingValue == null) {
            remainingValue = "0";
          }

          // Add to dateMap
          HashMap<Integer, Double> tasksOnDate = dateMap.get(formatter.format(statusDate));
          if (tasksOnDate == null) {
            tasksOnDate = new HashMap<Integer, Double>();
            dateMap.put(formatter.format(statusDate), tasksOnDate);
          }
          tasksOnDate.put(taskId, Double.parseDouble(remainingValue));
        }
        rs.close();
        pst.close();

        // Today
        Calendar calendarToday = Calendar.getInstance();
        calendarToday.setTime(new Date());
        calendarToday.set(Calendar.MILLISECOND, 0);
        calendarToday.set(Calendar.SECOND, 0);
        calendarToday.set(Calendar.MINUTE, 59);
        calendarToday.set(Calendar.HOUR_OF_DAY, 23);

        // Go through the Calendar days requested and plot the values
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);

        // Go through the calendar days and build the remaining values
        LinkedHashMap<Date, Double> graphMap = new LinkedHashMap<Date, Double>();
        HashMap<Integer, Double> recentMap = new HashMap<Integer, Double>();
        for (int i = 0; i < Integer.parseInt(daysToShow); i++) {
          // Get the tasks (if any), put them in a most recent map
          HashMap<Integer, Double> tasksOnDate = dateMap.get(formatter.format(calendar.getTime()));
          if (tasksOnDate == null) {
            tasksOnDate = new HashMap<Integer, Double>();
          }
          for (Integer thisTask : tasksOnDate.keySet()) {
            Double thisRemaining = tasksOnDate.get(thisTask);
            recentMap.put(thisTask, thisRemaining);
          }
          // Add up the most recent map
          Double graphValue = 0.0;
          for (Double thisValue : recentMap.values()) {
            graphValue += thisValue;
          }
          if (calendar.before(calendarToday)) {
            graphMap.put(calendar.getTime(), graphValue);
          } else {
            graphMap.put(calendar.getTime(), null);
          }
          // Advance the date
          calendar.add(Calendar.DATE, 1);
        }
        request.setAttribute("graphMap", graphMap);

        // Graph map 2 (steady pace)
        calendar.setTime(startDate);
        LinkedHashMap<Date, Double> graphMap2 = new LinkedHashMap<Date, Double>();
        for (int i = 0; i < Integer.parseInt(daysToShow); i++) {
          graphMap2.put(calendar.getTime(), estimatedHours);
          calendar.add(Calendar.DATE, 1);
          if (!calendarView.isHoliday(calendar) &&
              !DateUtils.isWeekend(calendar)) {
            estimatedHours = estimatedHours - workHours;
          }
        }
        request.setAttribute("graphMap2", graphMap2);

        // Ready to generate the graph markup
        request.setAttribute("chartHeight", chartHeight);
        PortletRequestDispatcher requestDispatcher =
            context.getRequestDispatcher(VIEW_PAGE);
        requestDispatcher.include(request, response);
      }
    } catch (Exception e) {
      throw new PortletException(e.getMessage());
    }

  }

  protected void doEdit(RenderRequest request, RenderResponse response)
      throws PortletException, IOException {
    try {
      Connection db = PortalUtils.useConnection(request);
      Project thisProject = PortalUtils.getProject(request);

      // Get the categories for the user
      TaskCategoryList categoryList = new TaskCategoryList();
      categoryList.setProjectId(PortalUtils.getProject(request).getId());
      categoryList.buildList(db);
      request.setAttribute("categoryList", categoryList);
      request.setAttribute("categoryId", request.getPreferences().getValue("categoryId", "-1"));

      // Get the date
      Date startDate = new Date(System.currentTimeMillis());
      String startDateValue = request.getPreferences().getValue("startDate", null);
      if (startDateValue != null) {
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        try {
          startDate = formatter.parse(startDateValue);
        } catch (Exception e) {

        }
      }
      request.setAttribute("startDate", startDate);

      // Show the releases
      ProjectItemList targetReleaseList = new ProjectItemList();
      targetReleaseList.setProjectId(thisProject.getId());
      targetReleaseList.setEnabled(Constants.TRUE);
      targetReleaseList.buildList(db, ProjectItemList.LIST_TARGET_RELEASE);
      request.setAttribute("releaseList", targetReleaseList);
      request.setAttribute("releaseId", request.getPreferences().getValue("releaseId", "-1"));

      // Chart parameters
      request.setAttribute("daysToShow", request.getPreferences().getValue("daysToShow", "45"));
      request.setAttribute("estimatedHours", request.getPreferences().getValue("estimatedHours", "0.0"));
      request.setAttribute("workHours", request.getPreferences().getValue("workHours", "7.0"));
      request.setAttribute("chartHeight", request.getPreferences().getValue("chartHeight", "250"));

      // Show the edit page
      PortletContext context = getPortletContext();
      PortletRequestDispatcher requestDispatcher =
          context.getRequestDispatcher(EDIT_PAGE);
      requestDispatcher.include(request, response);
    } catch (Exception e) {

    }

  }

  public void processAction(ActionRequest request, ActionResponse response)
      throws PortletException, IOException {
    // TODO: Permission check
    PortletPreferences prefs = request.getPreferences();

    String categoryId = request.getParameter("categoryId");
    if (categoryId != null) {
      prefs.setValue("categoryId", categoryId);
    }

    String startDate = request.getParameter("startDate");
    if (startDate != null) {
      try {
        // Could be in this format
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy");
        Date thisDate = formatter.parse(startDate);
        // It needs to be in this format
        SimpleDateFormat formatter2 = new SimpleDateFormat("MM/dd/yyyy");
        startDate = formatter2.format(thisDate);
        prefs.setValue("startDate", startDate);
      } catch (Exception e) {

      }
    }

    String releaseId = request.getParameter("releaseId");
    if (releaseId != null) {
      prefs.setValue("releaseId", releaseId);
    }

    String daysToShow = request.getParameter("daysToShow");
    if (daysToShow != null && StringUtils.isNumber(daysToShow)) {
      prefs.setValue("daysToShow", daysToShow);
    }

    String estimatedHours = request.getParameter("estimatedHours");
    if (estimatedHours != null && StringUtils.isNumber(estimatedHours)) {
      prefs.setValue("estimatedHours", estimatedHours);
    }

    String workHours = request.getParameter("workHours");
    if (workHours != null && StringUtils.isNumber(workHours)) {
      prefs.setValue("workHours", workHours);
    }

    String chartHeight = request.getParameter("chartHeight");
    if (chartHeight != null && StringUtils.isNumber(chartHeight)) {
      prefs.setValue("chartHeight", chartHeight);
    }

    prefs.store();
  }
}