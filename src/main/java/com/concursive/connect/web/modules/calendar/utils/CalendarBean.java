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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * CalendarBean maintains all the users setting on his home page's calendar
 * view like date,alerts etc so his view is maintained whenever he returns to
 * home page in that session.
 *
 * @author akhi_m
 * @version $Id$
 * @created September 9, 2002
 */
public class CalendarBean {

  private static Log LOG = LogFactory.getLog(CalendarBean.class);

  private String calendarDetailsView = "all";
  private String calendarView = "";
  private Calendar cal = null;
  private int primaryMonth = -1;
  private int monthSelected = -1;
  private int primaryYear = -1;
  private int yearSelected = -1;
  private int daySelected = -1;
  private int startMonthOfWeek = -1;
  private int startDayOfWeek = -1;
  private int selectedUserId = -1;
  private boolean agendaView = true;
  private ArrayList alertTypes = new ArrayList();
  private TimeZone timeZone = null;
  private String source = null;

  /**
   * Constructor for the CalendarBean object
   */
  public CalendarBean() {
  }


  /**
   * Constructor for the CalendarBean object
   *
   * @param locale Description of the Parameter
   */
  public CalendarBean(Locale locale) {
    cal = Calendar.getInstance(locale);
    primaryMonth = cal.get(Calendar.MONTH) + 1;
    monthSelected = cal.get(Calendar.MONTH) + 1;
    primaryYear = cal.get(Calendar.YEAR);
    yearSelected = cal.get(Calendar.YEAR);
  }


  /**
   * Sets the view attribute of the CalendarBean object
   *
   * @param calendarDetailsView The new calendarDetailsView value
   */
  public void setCalendarDetailsView(String calendarDetailsView) {
    this.calendarDetailsView = calendarDetailsView;
  }


  /**
   * Sets the daySelected attribute of the CalendarBean object
   *
   * @param daySelected The new daySelected value
   */
  public void setDaySelected(int daySelected) {
    this.daySelected = daySelected;
  }


  /**
   * Sets the calendarView attribute of the CalendarBean object
   *
   * @param calendarView The new calendarView value
   */
  public void setCalendarView(String calendarView) {
    this.calendarView = calendarView;
  }


  /**
   * Sets the selectedUserId attribute of the CalendarBean object
   *
   * @param selectedUserId The new selectedUserId value
   */
  public void setSelectedUserId(int selectedUserId) {
    this.selectedUserId = selectedUserId;
  }


  /**
   * Sets the primaryYear attribute of the CalendarBean object
   *
   * @param primaryYear The new primaryYear value
   */
  public void setPrimaryYear(int primaryYear) {
    this.primaryYear = primaryYear;
  }


  /**
   * Sets the timeZone attribute of the CalendarBean object
   *
   * @param timeZone The new timeZone value
   */
  public void setTimeZone(TimeZone timeZone) {
    this.timeZone = timeZone;
    cal.setTimeZone(timeZone);
    if (timeZone == null) {
      primaryMonth = cal.get(Calendar.MONTH) + 1;
      primaryYear = cal.get(Calendar.YEAR);
      monthSelected = cal.get(Calendar.MONTH) + 1;
      yearSelected = cal.get(Calendar.YEAR);
    }
  }


  /**
   * Gets the timeZone attribute of the CalendarBean object
   *
   * @return The timeZone value
   */
  public TimeZone getTimeZone() {
    return timeZone;
  }


  /**
   * Gets the primaryYear attribute of the CalendarBean object
   *
   * @return The primaryYear value
   */
  public int getPrimaryYear() {
    return primaryYear;
  }


  /**
   * Sets the monthSelected attribute of the CalendarBean object
   *
   * @param monthSelected The new monthSelected value
   */
  public void setMonthSelected(int monthSelected) {
    this.monthSelected = monthSelected;
  }


  /**
   * Sets the yearSelected attribute of the CalendarBean object
   *
   * @param yearSelected The new yearSelected value
   */
  public void setYearSelected(int yearSelected) {
    this.yearSelected = yearSelected;
  }


  /**
   * Sets the startMonthOfWeek attribute of the CalendarBean object
   *
   * @param startMonthOfWeek The new startMonthOfWeek value
   */
  public void setStartMonthOfWeek(int startMonthOfWeek) {
    this.startMonthOfWeek = startMonthOfWeek;
  }


  /**
   * Sets the startDayOfWeek attribute of the CalendarBean object
   *
   * @param startDayOfWeek The new startDayOfWeek value
   */
  public void setStartDayOfWeek(int startDayOfWeek) {
    this.startDayOfWeek = startDayOfWeek;
  }


  /**
   * Sets the agendaView attribute of the CalendarBean object
   *
   * @param agendaView The new agendaView value
   */
  public void setAgendaView(boolean agendaView) {
    this.agendaView = agendaView;
  }


  /**
   * Sets the alertTypes attribute of the CalendarBean object
   *
   * @param alertTypes The new alertTypes value
   */
  public void setAlertTypes(ArrayList alertTypes) {
    this.alertTypes = alertTypes;
  }


  /**
   * Sets the primaryMonth attribute of the CalendarBean object
   *
   * @param primaryMonth The new primaryMonth value
   */
  public void setPrimaryMonth(int primaryMonth) {
    this.primaryMonth = primaryMonth;
  }


  /**
   * Gets the primaryMonth attribute of the CalendarBean object
   *
   * @return The primaryMonth value
   */
  public int getPrimaryMonth() {
    return primaryMonth;
  }


  /**
   * Adds a feature to the AlertType attribute of the CalendarBean object
   *
   * @param alert       The feature to be added to the AlertType attribute
   * @param className   The feature to be added to the AlertType attribute
   * @param displayName The feature to be added to the AlertType attribute
   */
  public void addAlertType(String alert, String className, String displayName) {
    this.alertTypes.add(new AlertType(alert, className, displayName));
  }


  /**
   * Gets the alertTypes attribute of the CalendarBean object
   *
   * @return The alertTypes value
   */
  public ArrayList getAlertTypes() {
    return alertTypes;
  }


  /**
   * Gets the agendaView attribute of the CalendarBean object
   *
   * @return The agendaView value
   */
  public boolean isAgendaView() {
    return agendaView;
  }


  /**
   * Gets the startMonthOfWeek attribute of the CalendarBean object
   *
   * @return The startMonthOfWeek value
   */
  public int getStartMonthOfWeek() {
    return startMonthOfWeek;
  }


  /**
   * Gets the startDayOfWeek attribute of the CalendarBean object
   *
   * @return The startDayOfWeek value
   */
  public int getStartDayOfWeek() {
    return startDayOfWeek;
  }


  /**
   * Gets the monthSelected attribute of the CalendarBean object
   *
   * @return The monthSelected value
   */
  public int getMonthSelected() {
    return monthSelected;
  }


  /**
   * Gets the yearSelected attribute of the CalendarBean object
   *
   * @return The yearSelected value
   */
  public int getYearSelected() {
    return yearSelected;
  }


  /**
   * Gets the calendarView attribute of the CalendarBean object
   *
   * @return The calendarView value
   */
  public String getCalendarView() {
    return calendarView;
  }


  /**
   * Gets the userId attribute of the CalendarBean object
   *
   * @return The userId value
   */
  public int getSelectedUserId() {
    return selectedUserId;
  }


  /**
   * Gets the daySelected attribute of the CalendarBean object
   *
   * @return The daySelected value
   */
  public int getDaySelected() {
    return daySelected;
  }


  /**
   * Gets the calendarDetailsView attribute of the CalendarBean object
   *
   * @return The calendarDetailsView value
   */
  public String getCalendarDetailsView() {
    return calendarDetailsView;
  }


  /**
   * Gets the startOfWeekDate attribute of the CalendarBean object
   *
   * @return The startOfWeekDate value
   */
  public java.util.Date getStartOfWeekDate() {
    Calendar thisCal = Calendar.getInstance();
    thisCal.set(yearSelected, startMonthOfWeek - 1, startDayOfWeek);
    return thisCal.getTime();
  }


  /**
   * Gets the endOfWeekDate attribute of the CalendarBean object
   *
   * @return The endOfWeekDate value
   */
  public java.util.Date getEndOfWeekDate() {
    Calendar thisCal = Calendar.getInstance();
    thisCal.set(yearSelected, startMonthOfWeek - 1, startDayOfWeek);
    thisCal.add(java.util.Calendar.DATE, +6);
    return thisCal.getTime();
  }

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public void update(User thisUser) {
    String tZone = thisUser.getTimeZone();
    if (tZone != null &&
        (timeZone == null || !timeZone.hasSameRules(TimeZone.getTimeZone(tZone)))) {
      LOG.debug("Setting timezone to " + tZone);
      setTimeZone(TimeZone.getTimeZone(tZone));
      cal.setTimeZone(timeZone);
      if (timeZone == null) {
        primaryMonth = cal.get(Calendar.MONTH) + 1;
        primaryYear = cal.get(Calendar.YEAR);
        monthSelected = cal.get(Calendar.MONTH) + 1;
        yearSelected = cal.get(Calendar.YEAR);
      }
    }
  }


  /**
   * Description of the Method
   *
   * @param viewChanged Description of the Parameter
   */
  public void resetParams(String viewChanged) {
    if (viewChanged.equalsIgnoreCase("month")) {
      daySelected = -1;
      startDayOfWeek = -1;
      startMonthOfWeek = -1;
      agendaView = false;
    }
    if (viewChanged.equalsIgnoreCase("week")) {
      daySelected = -1;
      agendaView = false;
    }
    if (viewChanged.equalsIgnoreCase("day")) {
      startDayOfWeek = -1;
      startMonthOfWeek = -1;
      agendaView = false;
    }
    if (viewChanged.equalsIgnoreCase("agenda")) {
      daySelected = -1;
      startDayOfWeek = -1;
      startMonthOfWeek = -1;
      agendaView = true;
    }
  }
}
