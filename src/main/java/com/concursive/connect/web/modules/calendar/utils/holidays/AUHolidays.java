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
 * AU Holidays for the CalendarView class
 *
 * @author matt rajkowski
 * @created October 23, 2012
 */
public class AUHolidays {

  /**
   * Australian Holidays
   *
   * @param calendarView The feature to be added to the To attribute
   * @param theYear      The feature to be added to the To attribute
   */
  public final static void addTo(CalendarView calendarView, int theYear) {

    // NOTE: These holidays are based on TAS holidays and will need to be
    // updated for other states
    // TAS holidays
    // http://www.wst.tas.gov.au/__data/assets/pdf_file/0006/157974/GB067.pdf

    Calendar tmpCal = new GregorianCalendar();
    CalendarEvent thisEvent = null;
    int dayOfWeek = -1;

    // New Year's Day : January 1 (Moved to Monday if Saturday or Sunday)
    thisEvent = new CalendarEvent();
    thisEvent.setSubject("New Year's Day");
    tmpCal.set(theYear, Calendar.JANUARY, 1);
    dayOfWeek = tmpCal.get(Calendar.DAY_OF_WEEK);
    if (dayOfWeek == Calendar.SUNDAY) {
      calendarView.addEvent(
          "1/2/" + theYear, CalendarEventList.EVENT_TYPES[7], thisEvent);
    } else if (dayOfWeek == Calendar.SATURDAY) {
      calendarView.addEvent(
          "1/3/" + theYear, CalendarEventList.EVENT_TYPES[7], thisEvent);
    } else {
      calendarView.addEvent(
          "1/1/" + theYear, CalendarEventList.EVENT_TYPES[7], thisEvent);
    }

    // Australia Day : January 26 (Moved to Monday if Saturday or Sunday)
    thisEvent = new CalendarEvent();
    thisEvent.setSubject("Australia Day");
    tmpCal.set(theYear, Calendar.JANUARY, 26);
    dayOfWeek = tmpCal.get(Calendar.DAY_OF_WEEK);
    if (dayOfWeek == Calendar.SUNDAY) {
      calendarView.addEvent(
          "1/27/" + theYear, CalendarEventList.EVENT_TYPES[7], thisEvent);
    } else if (dayOfWeek == Calendar.SATURDAY) {
      calendarView.addEvent(
          "1/28/" + theYear, CalendarEventList.EVENT_TYPES[7], thisEvent);
    } else {
      calendarView.addEvent(
          "1/26/" + theYear, CalendarEventList.EVENT_TYPES[7], thisEvent);
    }

    // TAS holidays
    /*
    // Royal Hobart Regatta (South) : second Monday in February;
    // (All parts of Tasmania south of and including Oatlands and Swansea excluding Bronte Catagunya, Strathgordon, Tarraleah, Wayatinah and the West Coast)
    thisEvent = new CalendarEvent();
    thisEvent.setSubject("Royal Hobart Regatta (South)");
    tmpCal.set(theYear, Calendar.FEBRUARY, 1);
    dayOfWeek = tmpCal.get(Calendar.DAY_OF_WEEK);
    while (dayOfWeek != Calendar.MONDAY) {
      tmpCal.add(Calendar.DATE, 1);
      dayOfWeek = tmpCal.get(Calendar.DAY_OF_WEEK);
    }
    calendarView.addEvent(
        "2/" + (tmpCal.get(Calendar.DATE) + 7) + "/" + theYear, CalendarEventList.EVENT_TYPES[7], thisEvent);


    // Eight Hours Day : second Monday in March;
    thisEvent = new CalendarEvent();
    thisEvent.setSubject("Eight Hours Day");
    tmpCal.set(theYear, Calendar.MARCH, 1);
    dayOfWeek = tmpCal.get(Calendar.DAY_OF_WEEK);
    while (dayOfWeek != Calendar.MONDAY) {
      tmpCal.add(Calendar.DATE, 1);
      dayOfWeek = tmpCal.get(Calendar.DAY_OF_WEEK);
    }
    calendarView.addEvent(
        "3/" + (tmpCal.get(Calendar.DATE) + 7) + "/" + theYear, CalendarEventList.EVENT_TYPES[7], thisEvent);
    */

    // 2 days before Easter; Good Friday
    tmpCal = EasterHoliday.getCalendar(theYear);
    tmpCal.add(Calendar.DATE, -2);
    thisEvent = new CalendarEvent();
    thisEvent.setSubject("Good Friday");
    calendarView.addEvent(
        (tmpCal.get(Calendar.MONTH) + 1) + "/" + tmpCal.get(Calendar.DATE) + "/" + theYear, CalendarEventList.EVENT_TYPES[7], thisEvent);

    // Easter Sunday
    tmpCal = EasterHoliday.getCalendar(theYear);
    thisEvent = new CalendarEvent();
    thisEvent.setSubject("Easter");
    calendarView.addEvent(
        (tmpCal.get(Calendar.MONTH) + 1) + "/" + tmpCal.get(Calendar.DATE) + "/" + theYear, CalendarEventList.EVENT_TYPES[7], thisEvent);

    // Easter Monday
    tmpCal = EasterHoliday.getCalendar(theYear);
    dayOfWeek = tmpCal.get(Calendar.DAY_OF_WEEK);
    while (dayOfWeek != Calendar.MONDAY) {
      tmpCal.add(Calendar.DATE, 1);
      dayOfWeek = tmpCal.get(Calendar.DAY_OF_WEEK);
    }
    thisEvent = new CalendarEvent();
    thisEvent.setSubject("Easter Monday");
    calendarView.addEvent(
        (tmpCal.get(Calendar.MONTH) + 1) + "/" + tmpCal.get(Calendar.DATE) + "/" + theYear, CalendarEventList.EVENT_TYPES[7], thisEvent);

    // Anzac Day : April 25
    thisEvent = new CalendarEvent();
    thisEvent.setSubject("Anzac Day");
    calendarView.addEvent(
        "4/25/" + theYear, CalendarEventList.EVENT_TYPES[7], thisEvent);

    // TAS holidays
    /*
    // TAS holiday date; each territory is different
    // Queen's Birthday : second Monday in June;
    thisEvent = new CalendarEvent();
    thisEvent.setSubject("Queen's Birthday");
    tmpCal.set(theYear, Calendar.JUNE, 1);
    dayOfWeek = tmpCal.get(Calendar.DAY_OF_WEEK);
    while (dayOfWeek != Calendar.MONDAY) {
      tmpCal.add(Calendar.DATE, 1);
      dayOfWeek = tmpCal.get(Calendar.DAY_OF_WEEK);
    }
    calendarView.addEvent(
        "6/" + (tmpCal.get(Calendar.DATE) + 7) + "/" + theYear, CalendarEventList.EVENT_TYPES[7], thisEvent);

    // Recreation Day (North) : first Monday in November
    // (All parts of Tasmania in which a statutory holiday is not observed for the Royal Hobart Regatta)
    thisEvent = new CalendarEvent();
    thisEvent.setSubject("Recreation Day (North)");
    tmpCal.set(theYear, Calendar.NOVEMBER, 1);
    dayOfWeek = tmpCal.get(Calendar.DAY_OF_WEEK);
    while (dayOfWeek != Calendar.MONDAY) {
      tmpCal.add(Calendar.DATE, 1);
      dayOfWeek = tmpCal.get(Calendar.DAY_OF_WEEK);
    }
    calendarView.addEvent(
        "11/" + (tmpCal.get(Calendar.DATE)) + "/" + theYear, CalendarEventList.EVENT_TYPES[7], thisEvent);
    */

    // Christmas Day : December 25 (moved to Monday if Sunday or Friday if Saturday);
    thisEvent = new CalendarEvent();
    thisEvent.setSubject("Christmas Day");
    calendarView.addEvent(
        "12/25/" + theYear, CalendarEventList.EVENT_TYPES[7], thisEvent);
    //* if Christmas Day falls on a Saturday, the Monday following Christmas Day
    //* if Christmas Day falls on a Sunday, the Tuesday following Christmas Day
    thisEvent = new CalendarEvent();
    tmpCal.set(theYear, Calendar.DECEMBER, 25);
    dayOfWeek = tmpCal.get(Calendar.DAY_OF_WEEK);
    if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) {
      thisEvent.setSubject("Christmas Day (Observed)");
      calendarView.addEvent(
          "12/27/" + theYear, CalendarEventList.EVENT_TYPES[7], thisEvent);
    }

    // Boxing Day : December 26
    // If falls on a Saturday or Sunday, then the following Monday or Tuesday
    thisEvent = new CalendarEvent();
    thisEvent.setSubject("Boxing Day");
    tmpCal.set(theYear, Calendar.DECEMBER, 26);
    dayOfWeek = tmpCal.get(Calendar.DAY_OF_WEEK);
    if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) {
      calendarView.addEvent(
          "12/28/" + theYear, CalendarEventList.EVENT_TYPES[7], thisEvent);
    } else {
      calendarView.addEvent("12/26/" + theYear, "holiday", thisEvent);
    }

  }
}
