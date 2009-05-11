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

import com.concursive.commons.objects.ObjectUtils;
import com.concursive.connect.web.modules.calendar.utils.MeetingEventList;
import com.concursive.connect.web.modules.issues.calendar.TicketEventList;
import com.concursive.connect.web.modules.plans.calendar.ProjectEventList;
import com.concursive.connect.web.modules.plans.calendar.RequirementEventList;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Description of the Class
 *
 * @author
 * @created December 18, 2002
 */
public class CalendarEventList extends HashMap<String, Object> {

  private java.util.Date date = null;
  private HashMap eventTypes = null;

  public final static int ASSIGNMENT = 8;
  public final static int TICKET = 11;
  public final static int EVENT = 15;
  public final static int MILESTONE_START = 16;
  public final static int MILESTONE_END = 17;

  //anything added to this array should be added at the end
  public final static String[] EVENT_TYPES = {
      "Tasks",
      "Activities",
      "Opportunities",
      "Account Alerts",
      "Account Contract Alerts",
      "Contact Activities",
      "Opportunity Activities",
      "Holiday",
      "Assignments",
      "System Alerts",
      "Quotes",
      "Tickets",
      "Ticket Requests",
      "Pending Activities",
      "Project Tickets",
      "Events",
      "Milestone Start Dates",
      "Milestone End Dates"};


  /**
   * Constructor for the CalendarEventList object
   */
  public CalendarEventList() {
  }


  /**
   * Sets the date attribute of the CalendarEventList object
   *
   * @param tmp The new date value
   */
  public void setDate(java.util.Date tmp) {
    this.date = tmp;
  }


  /**
   * Sets the eventTypes attribute of the CalendarEventList object
   *
   * @param eventTypes The new eventTypes value
   */
  public void setEventTypes(HashMap eventTypes) {
    this.eventTypes = eventTypes;
  }


  /**
   * Gets the eventTypes attribute of the CalendarEventList object
   *
   * @return The eventTypes value
   */
  public HashMap getEventTypes() {
    return eventTypes;
  }


  /**
   * Gets the date attribute of the CalendarEventList object
   *
   * @return The date value
   */
  public java.util.Date getDate() {
    return date;
  }


  /**
   * Adds a feature to the Event attribute of the CallEventList object
   *
   * @param eventType The feature to be added to the Event attribute
   * @param event     The feature to be added to the Event attribute
   */
  public void addEvent(String eventType, Object event) {
    Object categoryEvents = null;
    if (this.containsKey(eventType)) {
      categoryEvents = this.get(eventType);
    } else {
      categoryEvents = addCategoryEventList(eventType);
    }
    if ((eventType.equalsIgnoreCase(EVENT_TYPES[7])) ||
        (eventType.equalsIgnoreCase(EVENT_TYPES[9]))) {
      ((ArrayList) categoryEvents).add(event);
    } else {
      ObjectUtils.invokeMethod(categoryEvents, "addEvent", event);
    }
  }


  /**
   * Gets the events attribute of the CalendarEventList object
   *
   * @param eventType Description of the Parameter
   * @return The events value
   */
  public Object getEvents(String eventType) {
    Object categoryEvents = null;
    if (this.containsKey(eventType)) {
      categoryEvents = this.get(eventType);
    } else {
      categoryEvents = addCategoryEventList(eventType);
    }
    return categoryEvents;
  }


  /**
   * Associates a count with a event Type specifying the number of events of
   * that type for that day.
   *
   * @param eventType  The feature to be added to the EventCount attribute
   * @param eventCount The feature to be added to the EventCount attribute
   */
  public void addEventCount(String eventType, Object eventCount) {
    Object categoryEvents = null;
    if (this.containsKey(eventType)) {
      categoryEvents = this.get(eventType);
    } else {
      categoryEvents = addCategoryEventList(eventType);
    }
    ObjectUtils.invokeMethod(categoryEvents, "setSize", eventCount);
  }


  /**
   * Adds a feature to the CategoryEventList attribute of the CallEventList
   * object
   *
   * @param eventType The feature to be added to the CategoryEventList
   *                  attribute
   * @return Description of the Return Value
   */
  public Object addCategoryEventList(String eventType) {
    Object thisList = null;
    if (eventType.equalsIgnoreCase(EVENT_TYPES[0])) {
      //thisList = new TaskEventList();
    } else if (eventType.equals(EVENT_TYPES[1])) {
      //thisList = new CallEventList();
    } else if (eventType.equals(EVENT_TYPES[13])) {
      //thisList = new CallEventList();
    } else if (eventType.equals(EVENT_TYPES[2])) {
      //thisList = new OpportunityEventList();
    } else if (eventType.equals(EVENT_TYPES[3])) {
      //thisList = new OrganizationEventList();
    } else if (eventType.equals(EVENT_TYPES[10])) {
      //thisList = new QuoteEventList();
    } else if (eventType.equals(EVENT_TYPES[TICKET])) {
      thisList = new TicketEventList();
    } else if (eventType.equals(EVENT_TYPES[12])) {
      //thisList = new TicketEventList();
    } else if (eventType.equals(EVENT_TYPES[14])) {
      //thisList = new TicketEventList();
    } else if (eventType.equals(EVENT_TYPES[7])) {
      thisList = new ArrayList();
    } else if (eventType.equals(EVENT_TYPES[9])) {
      thisList = new ArrayList();
    } else if (eventType.equals(EVENT_TYPES[ASSIGNMENT])) {
      thisList = new ProjectEventList();
    } else if (eventType.equalsIgnoreCase(EVENT_TYPES[EVENT])) {
      thisList = new MeetingEventList();
    } else if (eventType.equals(EVENT_TYPES[MILESTONE_START])) {
      thisList = new RequirementEventList();
    } else if (eventType.equals(EVENT_TYPES[MILESTONE_END])) {
      thisList = new RequirementEventList();
    }
    this.put(eventType, thisList);
    return thisList;
  }
}

