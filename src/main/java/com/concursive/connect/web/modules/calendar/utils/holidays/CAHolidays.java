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

package com.concursive.connect.web.modules.calendar.utils.holidays;

import com.concursive.connect.web.modules.calendar.utils.CalendarEvent;
import com.concursive.connect.web.modules.calendar.utils.CalendarView;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * US Holidays for the CalendarView class
 *
 * @author matt rajkowski
 * @version $Id$
 * @created November 17, 2004
 */
public class CAHolidays {

  /**
   * Adds a feature to the To attribute of the USHolidays class
   *
   * @param calendarView The feature to be added to the To attribute
   * @param theYear      The feature to be added to the To attribute
   */
  public final static void addTo(CalendarView calendarView, int theYear) {
    // NOTE: Remember that java month is 0-based and CalendarEvent is 1-based
    Calendar tmpCal = new GregorianCalendar();
    CalendarEvent thisEvent = null;
    int dayOfWeek = -1;

    //New Year's Day; Jan. 1
    thisEvent = new CalendarEvent();
    thisEvent.setSubject("New Year's Day");
    calendarView.addEvent("1/1/" + theYear, "holiday", thisEvent);

    //Good Friday; Friday before Easter
    tmpCal = EasterHoliday.getCalendar(theYear);
    dayOfWeek = tmpCal.get(Calendar.DAY_OF_WEEK);
    while (dayOfWeek != Calendar.FRIDAY) {
      tmpCal.add(Calendar.DATE, -1);
      dayOfWeek = tmpCal.get(Calendar.DAY_OF_WEEK);
    }
    thisEvent = new CalendarEvent();
    thisEvent.setSubject("Good Friday");
    calendarView.addEvent((tmpCal.get(Calendar.MONTH) + 1) + "/" + tmpCal.get(Calendar.DATE) + "/" + theYear, "holiday", thisEvent);

    //Easter Monday; Monday following Easter
    tmpCal = EasterHoliday.getCalendar(theYear);
    dayOfWeek = tmpCal.get(Calendar.DAY_OF_WEEK);
    while (dayOfWeek != Calendar.MONDAY) {
      tmpCal.add(Calendar.DATE, 1);
      dayOfWeek = tmpCal.get(Calendar.DAY_OF_WEEK);
    }
    thisEvent = new CalendarEvent();
    thisEvent.setSubject("Easter Monday");
    calendarView.addEvent((tmpCal.get(Calendar.MONTH) + 1) + "/" + tmpCal.get(Calendar.DATE) + "/" + theYear, "holiday", thisEvent);

    //Victoria Day; Monday preceding May 25
    tmpCal.set(theYear, Calendar.MAY, 25);
    dayOfWeek = tmpCal.get(Calendar.DAY_OF_WEEK);
    // If the 25th is a Monday, then we need the one before
    if (tmpCal.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
      tmpCal.add(Calendar.DATE, -7);
    }
    while (dayOfWeek != Calendar.MONDAY) {
      tmpCal.add(Calendar.DATE, -1);
      dayOfWeek = tmpCal.get(Calendar.DAY_OF_WEEK);
    }
    thisEvent = new CalendarEvent();
    thisEvent.setSubject("Victoria Day");
    calendarView.addEvent("5/" + tmpCal.get(Calendar.DATE) + "/" + theYear, "holiday", thisEvent);

    //Canada Day; July 1 (July 2 when July 1 is a Sunday)
    tmpCal.set(theYear, Calendar.JULY, 1);
    dayOfWeek = tmpCal.get(Calendar.DAY_OF_WEEK);
    if (tmpCal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
      // Set to July 2
      tmpCal.add(Calendar.DATE, 1);
    }
    thisEvent = new CalendarEvent();
    thisEvent.setSubject("Canada Day");
    calendarView.addEvent("7/" + tmpCal.get(Calendar.DATE) + "/" + theYear, "holiday", thisEvent);

    //Labour Day : first Monday in September;
    tmpCal.set(theYear, Calendar.SEPTEMBER, 1);
    dayOfWeek = tmpCal.get(Calendar.DAY_OF_WEEK);
    while (dayOfWeek != Calendar.MONDAY) {
      tmpCal.add(Calendar.DATE, 1);
      dayOfWeek = tmpCal.get(Calendar.DAY_OF_WEEK);
    }
    thisEvent = new CalendarEvent();
    thisEvent.setSubject("Labour Day");
    calendarView.addEvent("9/" + tmpCal.get(Calendar.DATE) + "/" + theYear, "holiday", thisEvent);

    //Thanksgiving Day : (second Monday of October)
    tmpCal.set(theYear, Calendar.OCTOBER, 1);
    dayOfWeek = tmpCal.get(Calendar.DAY_OF_WEEK);
    while (dayOfWeek != Calendar.MONDAY) {
      tmpCal.add(Calendar.DATE, 1);
      dayOfWeek = tmpCal.get(Calendar.DAY_OF_WEEK);
    }
    tmpCal.add(Calendar.DATE, 7);
    thisEvent = new CalendarEvent();
    thisEvent.setSubject("Thanksgiving Day");
    calendarView.addEvent("10/" + tmpCal.get(Calendar.DATE) + "/" + theYear, "holiday", thisEvent);

    //Remembrance Day : November 11
    thisEvent = new CalendarEvent();
    thisEvent.setSubject("Remembrance Day");
    calendarView.addEvent("11/11/" + theYear, "holiday", thisEvent);

    //Christmas : December 25;
    thisEvent = new CalendarEvent();
    thisEvent.setSubject("Christmas");
    calendarView.addEvent("12/25/" + theYear, "holiday", thisEvent);

    //Boxing Day : December 26 (moved to Monday if Sunday or Friday if Saturday);
    thisEvent = new CalendarEvent();
    thisEvent.setSubject("Boxing Day");
    calendarView.addEvent("12/26/" + theYear, "holiday", thisEvent);
  }
}

