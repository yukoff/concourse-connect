/*
 * ConcourseConnect
 * Copyright 2012 Concursive Corporation
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
import com.concursive.connect.web.modules.calendar.utils.CalendarEventList;
import com.concursive.connect.web.modules.calendar.utils.CalendarView;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Indian Holidays for the CalendarView class
 * NOTE: Some of the hindu specific holidays are based on the hindu calendar and the dates change every year
 *
 * @author ananth
 * @version $Id:
 * @created 29/10/12
 */
public class INHolidays {

  public final static void addTo(CalendarView calendarView, int theYear) {

    // NOTE: These holidays are based on indian holidays and holidays in particular for the
    // state of Karnataka.
    // National & Karnataka holidays for the year 2012.

    // NOTE: Some of the hindu specific holidays are based on the hindu calendar and the dates change every year

    // http://en.wikipedia.org/wiki/Public_holidays_in_India#National_holidays

    Calendar tmpCal = new GregorianCalendar();
    CalendarEvent thisEvent = null;
    int dayOfWeek = -1;

    // New Year's Day : January 1
    thisEvent = new CalendarEvent();
    thisEvent.setSubject("New Year's Day");
    //tmpCal.set(theYear, Calendar.JANUARY, 1);
    calendarView.addEvent("1/1/" + theYear, CalendarEventList.EVENT_TYPES[7], thisEvent);

    // Sankranti/Pongal : January 15
    thisEvent = new CalendarEvent();
    thisEvent.setSubject("Sankranti/Pongal");
    //tmpCal.set(theYear, Calendar.JANUARY, 15);
    calendarView.addEvent("1/15/" + theYear, CalendarEventList.EVENT_TYPES[7], thisEvent);

    // Republic Day : January 26
    thisEvent = new CalendarEvent();
    thisEvent.setSubject("Republic Day");
    //tmpCal.set(theYear, Calendar.JANUARY, 26);
    calendarView.addEvent("1/26/" + theYear, CalendarEventList.EVENT_TYPES[7], thisEvent);

    // Mahashivaratri : February 20
    thisEvent = new CalendarEvent();
    thisEvent.setSubject("Mahashivaratri");
    //tmpCal.set(theYear, Calendar.FEBRUARY, 20);
    calendarView.addEvent("2/20/" + theYear, CalendarEventList.EVENT_TYPES[7], thisEvent);

    // Holi : March 08
    thisEvent = new CalendarEvent();
    thisEvent.setSubject("Holi");
    //tmpCal.set(theYear, Calendar.MARCH, 08);
    calendarView.addEvent("3/08/" + theYear, CalendarEventList.EVENT_TYPES[7], thisEvent);

    // Ugadi : March 23
    thisEvent = new CalendarEvent();
    thisEvent.setSubject("Ugadi");
    //tmpCal.set(theYear, Calendar.MARCH, 23);
    calendarView.addEvent("3/23/" + theYear, CalendarEventList.EVENT_TYPES[7], thisEvent);

    // Ram Navami: April
    thisEvent = new CalendarEvent();
    thisEvent.setSubject("Ram Navami");
    //tmpCal.set(theYear, Calendar.APRIL, 01);
    calendarView.addEvent("4/01/" + theYear, CalendarEventList.EVENT_TYPES[7], thisEvent);

    // Good Friday
    // 2 days before Easter; Good Friday
    tmpCal = EasterHoliday.getCalendar(theYear);
    tmpCal.add(Calendar.DATE, -2);
    thisEvent = new CalendarEvent();
    thisEvent.setSubject("Good Friday");
    calendarView.addEvent(
        (tmpCal.get(Calendar.MONTH) + 1) + "/" + tmpCal.get(Calendar.DATE) + "/" + theYear, CalendarEventList.EVENT_TYPES[7], thisEvent);

    // Labour Day: May 01
    thisEvent.setSubject("Labour Day");
    //tmpCal.set(theYear, Calendar.MAY, 01);
    calendarView.addEvent("5/01/" + theYear, CalendarEventList.EVENT_TYPES[7], thisEvent);

    // Independence Day : August 15
    thisEvent.setSubject("Independence Day");
    //tmpCal.set(theYear, Calendar.AUGUST, 15);
    calendarView.addEvent("8/15/" + theYear, CalendarEventList.EVENT_TYPES[7], thisEvent);

    // Ramzan : August 20
    thisEvent.setSubject("Ramzan");
    //tmpCal.set(theYear, Calendar.AUGUST, 20);
    calendarView.addEvent("8/20/" + theYear, CalendarEventList.EVENT_TYPES[7], thisEvent);

    // Ganesh Chathurthi : September 19
    thisEvent.setSubject("Ganesh Chathurthi");
    //tmpCal.set(theYear, Calendar.SEPTEMBER, 19);
    calendarView.addEvent("9/19/" + theYear, CalendarEventList.EVENT_TYPES[7], thisEvent);

    // Gandhi Jayanthi : October 02
    thisEvent.setSubject("Gandhi Jayanthi");
    //tmpCal.set(theYear, Calendar.OCTOBER, 02);
    calendarView.addEvent("10/02/" + theYear, CalendarEventList.EVENT_TYPES[7], thisEvent);

    // Dussera / Vijaya Dashami : October 24
    thisEvent.setSubject("Duserra / Vijaya Dashami");
    //tmpCal.set(theYear, Calendar.OCTOBER, 24);
    calendarView.addEvent("10/24/" + theYear, CalendarEventList.EVENT_TYPES[7], thisEvent);

    // Kannada Rajyothsava : November 01
    thisEvent.setSubject("Kannada Rajyothsava");
    //tmpCal.set(theYear, Calendar.NOVEMBER, 13);
    calendarView.addEvent("11/01/" + theYear, CalendarEventList.EVENT_TYPES[7], thisEvent);

    // Diwali : November 13 and November 14
    thisEvent.setSubject("Diwali");
    //tmpCal.set(theYear, Calendar.NOVEMBER, 13);
    calendarView.addEvent("11/13/" + theYear, CalendarEventList.EVENT_TYPES[7], thisEvent);

    thisEvent.setSubject("Diwali");
    //tmpCal.set(theYear, Calendar.NOVEMBER, 14);
    calendarView.addEvent("11/14/" + theYear, CalendarEventList.EVENT_TYPES[7], thisEvent);

    // Moharram : November 25
    thisEvent.setSubject("Moharram");
    //tmpCal.set(theYear, Calendar.NOVEMBER, 25);
    calendarView.addEvent("11/25/" + theYear, CalendarEventList.EVENT_TYPES[7], thisEvent);

    // Christmas : December 25
    thisEvent.setSubject("Christmas");
    //tmpCal.set(theYear, Calendar.DECEMBER, 25);
    calendarView.addEvent("12/25/" + theYear, CalendarEventList.EVENT_TYPES[7], thisEvent);
  }
}
