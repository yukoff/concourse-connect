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

package com.concursive.connect.web.modules.calendar.portlets.main;

import com.concursive.connect.web.modules.calendar.utils.CalendarView;
import com.concursive.commons.text.StringUtils;
import com.concursive.connect.web.modules.calendar.utils.CalendarBean;
import com.concursive.connect.web.modules.calendar.utils.CalendarBeanUtils;
import com.concursive.connect.web.modules.calendar.utils.CalendarViewUtils;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import com.concursive.connect.web.portal.IPortletViewer;
import com.concursive.connect.web.portal.PortalUtils;
import static com.concursive.connect.web.portal.PortalUtils.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import java.sql.Connection;

/**
 * Generates a calendar agenda page
 *
 * @author matt rajkowski
 * @created November 24, 2008
 */
public class EventsViewer implements IPortletViewer {

  private static Log LOG = LogFactory.getLog(EventsViewer.class);

  // Pages
  private static final String VIEW_PAGE = "/projects_center_calendar_details.jsp";

  // Object Results
  private static final String CALENDAR_BEAN_BASE = "calendarInfoBean";
  private static final String CALENDAR_VIEW = "calendarView";

  public String doView(RenderRequest request, RenderResponse response) throws Exception {
    // The JSP to show upon success
    String defaultView = VIEW_PAGE;

    // Determine the project container to use
    Project project = findProject(request);

    // Check the user's permissions
    User user = getUser(request);
    if (!ProjectUtils.hasAccess(project.getId(), user, "project-calendar-view")) {
      throw new PortletException("Unauthorized to view in this project");
    }

    // Construct a CalendarBean for display
    CalendarBean calendarInfo = new CalendarBean(user.getLocale());
    request.setAttribute(CALENDAR_BEAN_BASE + project.getId(), calendarInfo);

    // Determine the view output
    String source = request.getParameter("source");
    if (source == null) {
      source = "calendarDetails";
    }
    calendarInfo.setSource(source);

    // Adjust view settings based on request
    CalendarBeanUtils.updateValues(calendarInfo, request, user);

    // Determine the display date, based on the URL
    String selectedDate = PortalUtils.getPageView(request);
    if (StringUtils.hasText(selectedDate)) {
      String[] dateParts = selectedDate.split("-");
      if (dateParts.length > 0) {
        // Year view (defaults to first month in that year for now)
        int year = Integer.parseInt(dateParts[0]);
        calendarInfo.setYearSelected(year);
        calendarInfo.setMonthSelected(1);
        //calendarInfo.setPrimaryYear(year);
        //calendarInfo.setPrimaryMonth(1);
        calendarInfo.setCalendarView("month");
        calendarInfo.resetParams("month");
      }
      if (dateParts.length > 1) {
        // Month view (specific month specified)
        int month = Integer.parseInt(dateParts[1]);
        calendarInfo.setMonthSelected(month);
        //calendarInfo.setPrimaryMonth(month);
        calendarInfo.setCalendarView("month");
        calendarInfo.resetParams("month");
      }
      if (dateParts.length > 2) {
        // A specific day was specified, with optional views...
        int day = Integer.parseInt(dateParts[2]);
        calendarInfo.setDaySelected(day);
        // Determine the view
        String view = request.getParameter("view");
        if ("week".equals(view)) {
          // Week view
          calendarInfo.setCalendarView("week");
          calendarInfo.resetParams("week");
          calendarInfo.setStartMonthOfWeek(calendarInfo.getMonthSelected());
          calendarInfo.setStartDayOfWeek(day);
        } else if ("today".equals(view)) {
          // Today
          calendarInfo.setCalendarView("day");
          calendarInfo.resetParams("day");
          //Calendar cal = Calendar.getInstance();
          //cal.setTimeZone(calendarInfo.getTimeZone());
          //calendarInfo.setPrimaryMonth(cal.get(Calendar.MONTH) + 1);
          //calendarInfo.setPrimaryYear(cal.get(Calendar.YEAR));
        } else {
          // Day view
          calendarInfo.setCalendarView("day");
          calendarInfo.resetParams("day");
        }
      }
    } else {
      // Upcoming events view
      calendarInfo.setAgendaView(true);
      calendarInfo.resetParams("agenda");
    }

    // Determine if there is a filter
    String filter = null;

    // Determine the database connection to use
    Connection db = getConnection(request);

    // Generate a calendar
    CalendarView calendarView = CalendarViewUtils.generateCalendarView(db, calendarInfo, project, user, filter);
    request.setAttribute(CALENDAR_VIEW, calendarView);
    LOG.debug("Events size: " + calendarView.getEvents(100).size());

    // JSP view
    return defaultView;
  }
}