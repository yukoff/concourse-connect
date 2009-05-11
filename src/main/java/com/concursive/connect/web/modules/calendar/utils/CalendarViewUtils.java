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

import com.concursive.commons.date.DateUtils;
import com.concursive.commons.db.DatabaseUtils;
import com.concursive.connect.web.modules.calendar.dao.Meeting;
import com.concursive.connect.web.modules.calendar.dao.MeetingList;
import com.concursive.connect.web.modules.issues.dao.Ticket;
import com.concursive.connect.web.modules.issues.dao.TicketList;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.login.utils.UserUtils;
import com.concursive.connect.web.modules.plans.dao.Assignment;
import com.concursive.connect.web.modules.plans.dao.AssignmentList;
import com.concursive.connect.web.modules.plans.dao.Requirement;
import com.concursive.connect.web.modules.plans.dao.RequirementList;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.HashMap;
import java.util.Locale;

/**
 * Utilities for working with the calendar view
 *
 * @author matt rajkowski
 * @created Nov 24, 2008 2:18:17 PM
 */
public class CalendarViewUtils {

  private static Log LOG = LogFactory.getLog(CalendarViewUtils.class);

  public static CalendarView generateCalendarView(Connection db, CalendarBean calendarInfo, Project project, User user, String filter) throws SQLException {

    // Generate a new calendar
    CalendarView calendarView = new CalendarView(calendarInfo, user.getLocale());

    // Add some holidays based on the user locale
    calendarView.addHolidays();

    // Set Start and End Dates for the view
    java.sql.Timestamp startDate = DatabaseUtils.parseTimestamp(
        DateUtils.getUserToServerDateTimeString(
            calendarInfo.getTimeZone(), DateFormat.SHORT, DateFormat.LONG, calendarView.getCalendarStartDate(
                calendarInfo.getSource()), Locale.US));
    startDate.setNanos(0);

    java.sql.Timestamp endDate = DatabaseUtils.parseTimestamp(
        DateUtils.getUserToServerDateTimeString(
            calendarInfo.getTimeZone(), DateFormat.SHORT, DateFormat.LONG, calendarView.getCalendarEndDate(
                calendarInfo.getSource()), Locale.US));
    endDate.setNanos(0);

    if (ProjectUtils.hasAccess(project.getId(), user, "project-tickets-view")) {
      // Show open and closed tickets
      TicketList ticketList = new TicketList();
      ticketList.setProjectId(project.getId());
      ticketList.setOnlyAssigned(true);
      ticketList.setAlertRangeStart(startDate);
      ticketList.setAlertRangeEnd(endDate);
      if ("pending".equals(filter)) {
        ticketList.setOnlyOpen(true);
      } else if ("completed".equals(filter)) {
        ticketList.setOnlyClosed(true);
      }

      // Retrieve the tickets that meet the criteria
      ticketList.buildList(db);
      for (Ticket thisTicket : ticketList) {
        if (thisTicket.getEstimatedResolutionDate() != null) {
          String alertDate = DateUtils.getServerToUserDateString(UserUtils.getUserTimeZone(user), DateFormat.SHORT, thisTicket.getEstimatedResolutionDate());
          calendarView.addEvent(alertDate, CalendarEventList.EVENT_TYPES[11], thisTicket);
        }
      }

      // Retrieve the dates in which a ticket has been resolved
      HashMap<String, Integer> dayEvents = ticketList.queryRecordCount(db, UserUtils.getUserTimeZone(user));
      for (String thisDay : dayEvents.keySet()) {
        calendarView.addEventCount(thisDay, CalendarEventList.EVENT_TYPES[CalendarEventList.TICKET], dayEvents.get(thisDay));
      }
    }
    if (ProjectUtils.hasAccess(project.getId(), user, "project-plan-view")) {
      // List open and closed Requirements
      RequirementList requirementList = new RequirementList();
      requirementList.setProjectId(project.getId());
      requirementList.setBuildAssignments(false);
      requirementList.setAlertRangeStart(startDate);
      requirementList.setAlertRangeEnd(endDate);
      if ("pending".equals(filter)) {
        requirementList.setOpenOnly(true);
      } else if ("completed".equals(filter)) {
        requirementList.setClosedOnly(true);
      }

      // Retrieve the requirements that meet the criteria
      requirementList.buildList(db);
      requirementList.buildPlanActivityCounts(db);
      for (Requirement thisRequirement : requirementList) {
        // Display Milestone startDate
        if (thisRequirement.getStartDate() != null) {
          String start = DateUtils.getServerToUserDateString(
              UserUtils.getUserTimeZone(user), DateFormat.SHORT, thisRequirement.getStartDate());
          calendarView.addEvent(
              start, CalendarEventList.EVENT_TYPES[16], thisRequirement);
        }
        // Display Milestone endDate
        if (thisRequirement.getDeadline() != null) {
          String end = DateUtils.getServerToUserDateString(UserUtils.getUserTimeZone(user), DateFormat.SHORT, thisRequirement.getDeadline());
          calendarView.addEvent(end, CalendarEventList.EVENT_TYPES[17], thisRequirement);
        }
      }

      // Retrieve the dates in which a requirement has a start or end date
      HashMap<String, HashMap<String, Integer>> dayGroups = requirementList.queryRecordCount(db, UserUtils.getUserTimeZone(user));
      for (String type : dayGroups.keySet()) {
        if ("startdate".equals(type)) {
          HashMap<String, Integer> dayEvents = dayGroups.get(type);
          for (String thisDay : dayEvents.keySet()) {
            calendarView.addEventCount(thisDay, CalendarEventList.EVENT_TYPES[CalendarEventList.MILESTONE_START], dayEvents.get(thisDay));
          }
        } else if ("enddate".equals(type)) {
          HashMap<String, Integer> dayEvents = dayGroups.get(type);
          for (String thisDay : dayEvents.keySet()) {
            calendarView.addEventCount(thisDay, CalendarEventList.EVENT_TYPES[CalendarEventList.MILESTONE_END], dayEvents.get(thisDay));
          }
        }

      }

      // Retrieve assignments that meet the criteria
      AssignmentList assignmentList = new AssignmentList();
      assignmentList.setProjectId(project.getId());
      assignmentList.setOnlyIfRequirementOpen(true);
      assignmentList.setAlertRangeStart(startDate);
      assignmentList.setAlertRangeEnd(endDate);
      if ("pending".equals(filter)) {
        assignmentList.setIncompleteOnly(true);
      } else if ("completed".equals(filter)) {
        assignmentList.setClosedOnly(true);
      }

      // Display the user's assignments by due date
      assignmentList.buildList(db);
      for (Assignment thisAssignment : assignmentList) {
        if (thisAssignment.getDueDate() != null) {
          String dueDate = DateUtils.getServerToUserDateString(UserUtils.getUserTimeZone(user), DateFormat.SHORT, thisAssignment.getDueDate());
          calendarView.addEvent(dueDate, CalendarEventList.EVENT_TYPES[8], thisAssignment);
        }
      }

      // Retrieve the dates in which an assignment has a due date
      HashMap<String, Integer> dayEvents = assignmentList.queryAssignmentRecordCount(db, UserUtils.getUserTimeZone(user));
      for (String thisDay : dayEvents.keySet()) {
        calendarView.addEventCount(thisDay, CalendarEventList.EVENT_TYPES[CalendarEventList.ASSIGNMENT], dayEvents.get(thisDay));
      }
    }

    if (ProjectUtils.hasAccess(project.getId(), user, "project-calendar-view")) {
      MeetingList meetingList = new MeetingList();
      meetingList.setProjectId(project.getId());
      meetingList.setAlertRangeStart(startDate);
      meetingList.setAlertRangeEnd(endDate);
      meetingList.buildList(db);
      LOG.debug("Meeting count = " + meetingList.size());
      // Display the meetings by date
      for (Meeting thisMeeting : meetingList) {
        if (thisMeeting.getStartDate() != null) {
          String start = DateUtils.getServerToUserDateString(UserUtils.getUserTimeZone(user), DateFormat.SHORT, thisMeeting.getStartDate());
          if ("pending".equals(filter)) {
            if (thisMeeting.getStartDate().getTime() > System.currentTimeMillis())
              calendarView.addEvent(start, CalendarEventList.EVENT_TYPES[CalendarEventList.EVENT], thisMeeting);
          } else if ("completed".equals(filter)) {
            if (thisMeeting.getEndDate().getTime() < System.currentTimeMillis())
              calendarView.addEvent(start, CalendarEventList.EVENT_TYPES[CalendarEventList.EVENT], thisMeeting);
          } else {
            calendarView.addEvent(start, CalendarEventList.EVENT_TYPES[CalendarEventList.EVENT], thisMeeting);
          }
          LOG.debug("Meeting added for date: " + start);
        }
      }

      // Retrieve the dates for meeting events
      HashMap<String, Integer> dayEvents = meetingList.queryRecordCount(db, UserUtils.getUserTimeZone(user));
      for (String thisDay : dayEvents.keySet()) {
        calendarView.addEventCount(thisDay, CalendarEventList.EVENT_TYPES[CalendarEventList.EVENT], dayEvents.get(thisDay));
      }
    }

    return calendarView;
  }
}
