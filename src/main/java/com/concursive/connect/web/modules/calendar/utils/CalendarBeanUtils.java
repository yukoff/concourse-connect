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

package com.concursive.connect.web.modules.calendar.utils;

import com.concursive.connect.web.modules.login.dao.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.portlet.PortletRequest;
import java.sql.SQLException;
import java.util.TimeZone;

/**
 * Utilities for working with the calendar bean
 *
 * @author matt rajkowski
 * @created Nov 24, 2008 2:00:03 PM
 */
public class CalendarBeanUtils {

  private static Log LOG = LogFactory.getLog(CalendarBeanUtils.class);

  public static void updateValues(CalendarBean bean, PortletRequest request, User user) throws SQLException {

    // Set the timezone if it is different from the server timezone
    String tZone = user.getTimeZone();
    if (tZone != null &&
        (bean.getTimeZone() == null || !bean.getTimeZone().hasSameRules(TimeZone.getTimeZone(tZone)))) {
      LOG.debug("Setting timezone from user to: " + tZone);
      bean.setTimeZone(TimeZone.getTimeZone(tZone));
    }

    if (request.getParameter("primaryMonth") != null) {
      bean.setPrimaryMonth(Integer.parseInt(request.getParameter("primaryMonth")));
    }

    if (request.getParameter("primaryYear") != null) {
      bean.setPrimaryYear(Integer.parseInt(request.getParameter("primaryYear")));
    }

    if (request.getParameter("alertsView") != null) {
      bean.setCalendarDetailsView(request.getParameter("alertsView"));
    }

    if (request.getParameter("userId") != null) {
      bean.setSelectedUserId(Integer.parseInt(request.getParameter("userId")));
    }

    if (request.getParameter("day") != null) {
      bean.setDaySelected(Integer.parseInt(request.getParameter("day")));
    }

    if (request.getParameter("month") != null) {
      bean.setMonthSelected(Integer.parseInt(request.getParameter("month")));
    }

    if (request.getParameter("year") != null) {
      bean.setYearSelected(Integer.parseInt(request.getParameter("year")));
    }

    if (request.getParameter("startMonthOfWeek") != null) {
      bean.setStartMonthOfWeek(Integer.parseInt(request.getParameter("startMonthOfWeek")));
    }

    if (request.getParameter("startDayOfWeek") != null) {
      bean.setStartDayOfWeek(Integer.parseInt(request.getParameter("startDayOfWeek")));
    }

  }
}
