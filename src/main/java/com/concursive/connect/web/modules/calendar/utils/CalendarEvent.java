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

import com.concursive.commons.text.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.StringTokenizer;

/**
 * Description of the Class
 *
 * @author matt rajkowski
 * @created July 27, 2001
 */
public class CalendarEvent implements Comparable {

  //General Information
  protected String subject = "";
  protected String location = "";
  protected String category = "";
  protected String icon = "";

  protected int id = -1;
  protected int idsub = -1;

  //Body of the event
  protected String details = "";
  //Special HTML formatting
  protected String HTMLClass = "";
  //Link where event goes to
  protected String link = "";
  //status of event
  protected int status = -1;

  //Date
  protected String month = "";
  protected String day = "";
  protected String year = "";

  //Time
  protected String time = "00:00";

  //Repeat
  protected int repeatType = 0;

  //Every [x] weeks [s,m,t,w,r,f]
  //On the [first] [Sunday] of every [1] months
  //Every month
  //Every year
  //--
  //Repeat until date
  //Repeat forever

  //Invites
  protected ArrayList invites = null;
  //email address/names
  //Show guest list to guests?

  //Reminder
  protected ArrayList reminders = null;
  //Alert me via Instant Message
  //Alert me via Pager/Cell Phone/Fax
  //Send email to ...
  //When? 10,15,20,30,45 minutes before; 1,2 hours before; on the day, 1 day before, 2 days before

  //Other Info
  protected String comments = "";

  private Comparator eventComparator = new comparatorEvent();
  private ArrayList relatedLinks = null;

  //Telephone Number

  //Email Address
  //URL

  /**
   * Constructor for the CalendarEvent object
   */
  public CalendarEvent() {
  }


  /**
   * Sets the date attribute of the CalendarEvent object
   *
   * @param eventDate The new date value
   */
  public void setDate(java.sql.Timestamp eventDate) {
    if (eventDate != null) {
      SimpleDateFormat shortDateFormat = new SimpleDateFormat("M/d/yyyy");
      String eventDateString = shortDateFormat.format(eventDate);
      StringTokenizer st = new StringTokenizer(eventDateString, "/");
      if (st.hasMoreTokens()) {
        this.setMonth(st.nextToken());
        this.setDay(st.nextToken());
        this.setYear(st.nextToken());
      }
    }
  }


  /**
   * Sets the Month attribute of the CalendarEvent object
   *
   * @param tmp The new Month value
   */
  public void setMonth(String tmp) {
    month = tmp;
  }


  /**
   * Sets the Icon attribute of the CalendarEvent object
   *
   * @param icon The new Icon value
   */
  public void setIcon(String icon) {
    this.icon = icon;
  }


  /**
   * Sets the idsub attribute of the CalendarEvent object
   *
   * @param idsub The new idsub value
   */
  public void setIdsub(int idsub) {
    this.idsub = idsub;
  }


  /**
   * Sets the Day attribute of the CalendarEvent object
   *
   * @param tmp The new Day value
   */
  public void setDay(String tmp) {
    day = tmp;
  }


  /**
   * Sets the Year attribute of the CalendarEvent object
   *
   * @param tmp The new Year value
   */
  public void setYear(String tmp) {
    year = tmp;
  }


  /**
   * Sets the Time attribute of the CalendarEvent object
   *
   * @param tmp The new Time value
   */
  public void setTime(String tmp) {
    if (time != null && !time.equals("")) {
      time = tmp;
    } else {
      time = "00:00";
    }
  }


  /**
   * Sets the id attribute of the CalendarEvent object
   *
   * @param tmp The new id value
   */
  public void setId(int tmp) {
    this.id = tmp;
  }


  /**
   * Sets the link attribute of the CalendarEvent object
   *
   * @param tmp The new link value
   */
  public void setLink(String tmp) {
    this.link = tmp;
  }


  /**
   * Sets the Subject attribute of the CalendarEvent object
   *
   * @param tmp The new Subject value
   */
  public void setSubject(String tmp) {
    subject = tmp;
  }


  /**
   * Sets the Category attribute of the CalendarEvent object
   *
   * @param tmp The new Category value
   */
  public void setCategory(String tmp) {
    category = tmp;
  }


  /**
   * Sets the HTMLClass attribute of the CalendarEvent object
   *
   * @param tmp The new HTMLClass value
   */
  public void setHTMLClass(String tmp) {
    HTMLClass = tmp;
  }


  /**
   * Sets the status attribute of the CalendarEvent object
   *
   * @param status The new status value
   */
  public void setStatus(int status) {
    this.status = status;
  }


  /**
   * Sets the relatedLinks attribute of the CalendarEvent object
   *
   * @param relatedLinks The new relatedLinks value
   */
  public void setRelatedLinks(ArrayList relatedLinks) {
    this.relatedLinks = relatedLinks;
  }


  /**
   * Adds a feature to the RelatedLink attribute of the CalendarEvent object
   *
   * @param link The feature to be added to the RelatedLink attribute
   */
  public void addRelatedLink(String link) {
    if (relatedLinks == null) {
      relatedLinks = new ArrayList();
    }
    relatedLinks.add(link);
  }


  /**
   * Gets the relatedLinks attribute of the CalendarEvent object
   *
   * @return The relatedLinks value
   */
  public ArrayList getRelatedLinks() {
    return relatedLinks;
  }


  /**
   * Gets the status attribute of the CalendarEvent object
   *
   * @return The status value
   */
  public int getStatus() {
    return status;
  }


  /**
   * Gets the idsub attribute of the CalendarEvent object
   *
   * @return The idsub value
   */
  public int getIdsub() {
    return idsub;
  }


  /**
   * Gets the id attribute of the CalendarEvent object
   *
   * @return The id value
   */
  public int getId() {
    return id;
  }


  /**
   * Gets the Subject attribute of the CalendarEvent object
   *
   * @return The Subject value
   */
  public String getSubject() {
    return subject;
  }


  /**
   * Gets the Category attribute of the CalendarEvent object
   *
   * @return The Category value
   */
  public String getCategory() {
    return category;
  }


  /**
   * Gets the icon attribute of the CalendarEvent class
   *
   * @param thisCategory Description of the Parameter
   * @return The icon value
   */
  public static String getIcon(String thisCategory, String contextPath) {
    if (thisCategory.equals("event")) {
      return "<img border=\"0\" src=\"" + contextPath + "/images/icons/stock_about-16.gif\" align=\"texttop\" width=\"12\" height=\"12\" title=\"" + getLabel(
          "calendar.Event", "Event") + "\" />";
    } else if (thisCategory.equalsIgnoreCase("holiday")) {
      return "<img border=\"0\" src=\"" + contextPath + "/images/icons/stock_about-16.gif\" align=\"texttop\" width=\"12\" height=\"12\" title=\"" + getLabel(
          "calendar.Holiday", "Holiday") + "\" />";
    } else if (thisCategory.equalsIgnoreCase("System Alerts")) {
      return "<img border=\"0\" src=\"" + contextPath + "/images/box-hold.gif\" align=\"texttop\" width=\"16\" height=\"15\" title=\"" + getLabel(
          "calendar.UserAccountExpires", "User Account Expires") + "\" />";
    } else if (thisCategory.equalsIgnoreCase("Opportunity") || thisCategory.equalsIgnoreCase(
        "Opportunities")) {
      return "<img border=\"0\" src=\"" + contextPath + "/images/alertopp.gif\" align=\"texttop\" title=\"" + getLabel(
          "calendar.Opportunities", "Opportunities") + "\" />";
    } else if (thisCategory.equalsIgnoreCase("Activities") || thisCategory.equalsIgnoreCase(
        "Contact Activities") || thisCategory.equalsIgnoreCase(
        "Opportunity Activities")) {
      return "<img border=\"0\" src=\"" + contextPath + "/images/alertcall.gif\" align=\"texttop\" title=\"" + getLabel(
          "calendar.Activities", "Activities") + "\" />";
    } else if (thisCategory.equalsIgnoreCase("Pending Activities")) {
      return "<img border=\"0\" src=\"" + contextPath + "/images/box-hold.gif\" align=\"texttop\" title=\"" + getLabel(
          "calendar.PendingActivities", "Pending Activities") + "\" />";
    } else if (thisCategory.equalsIgnoreCase("Assignments")) {
      return "<img border=\"0\" src=\"" + contextPath + "/images/alertassignment.gif\" align=\"texttop\" title=\"" + getLabel(
          "calendar.Assignments", "Assignments") + "\" />";
    } else if (thisCategory.equalsIgnoreCase("Account Alerts") || thisCategory.equalsIgnoreCase(
        "Account Contract Alerts")) {
      return "<img border=\"0\" src=\"" + contextPath + "/images/accounts.gif\" width=\"14\" height=\"14\" align=\"texttop\" title=\"" + getLabel(
          "calendar.Accounts", "Accounts") + "\" />";
    } else if (thisCategory.equalsIgnoreCase("Tasks")) {
      return "<img src=\"" + contextPath + "/images/box.gif\" border=\"0\" align=\"texttop\" width=\"14\" height=\"14\" title=\"" + getLabel(
          "calendar.Tasks", "Tasks") + "\" />";
    } else if (thisCategory.equalsIgnoreCase("Ticket Requests")) {
      return "<img src=\"" + contextPath + "/images/tree0.gif\" border=\"0\" align=\"texttop\" title=\"" + getLabel(
          "calendar.Tickets", "Tickets") + "\" />";
    } else if (thisCategory.equalsIgnoreCase("Tickets")) {
      return "<img src=\"" + contextPath + "/images/tree1.gif\" border=\"0\" align=\"texttop\" title=\"" + getLabel(
          "calendar.projectTickets", "Project Tickets") + "\" />";
    } else if (thisCategory.equalsIgnoreCase("Events")) {
      return "<img border=\"0\" src=\"" + contextPath + "/images/teamelements/join-16.gif\" align=\"texttop\" width=\"12\" height=\"12\" title=\"" + getLabel(
          "calendar.Meeting", "Meeting") + "\" />";
    } else if (thisCategory.equalsIgnoreCase("Milestone Start Dates")) {
      return "<img border=\"0\" src=\"" + contextPath + "/images/arrowright.gif\" align=\"texttop\" width=\"12\" height=\"12\" title=\"" + getLabel(
          "calendar.MilestoneStart", "Milestone Start") + "\" />";
    } else if (thisCategory.equalsIgnoreCase("Milestone End Dates")) {
      return "<img border=\"0\" src=\"" + contextPath + "/images/red-1.gif\" align=\"texttop\" width=\"12\" height=\"12\" title=\"" + getLabel(
          "calendar.MilestoneEnd", "Milestone End") + "\" />";
    }
    return "";
  }


  /**
   * Gets the DateTimeString attribute of the CalendarEvent object
   *
   * @return The DateTimeString value
   */
  public String getDateTimeString() {
    return (year + "-" + month + "-" + day + " " + time);
  }


  /**
   * Gets the dateString attribute of the CalendarEvent object
   *
   * @return The dateString value
   */
  public String getDateString() {
    return (month + "/" + day + "/" + year);
  }


  /**
   * Gets the holiday attribute of the CalendarEvent object
   *
   * @return The holiday value
   */
  public boolean isHoliday() {
    return ("holiday").equalsIgnoreCase(this.category);
  }


  /**
   * Gets the label attribute of the CalendarEvent object
   *
   * @param label        Description of the Parameter
   * @param defaultValue Description of the Parameter
   * @return The label value
   */
  public static String getLabel(String label, String defaultValue) {
    return StringUtils.toHtml(defaultValue);
  }


  /**
   * Description of the Method
   *
   * @return Description of the Returned Value
   */
  public String toString() {
    return month + "/" + day + "/" + year + ": " + category;
  }


  /**
   * Description of the Method
   *
   * @param object Description of Parameter
   * @return Description of the Returned Value
   */
  public int compareTo(Object object) {
    return (eventComparator.compare(this, object));
  }


  /**
   * Description of the Method
   *
   * @param object Description of Parameter
   * @return Description of the Returned Value
   */
  public int compareDateTo(Object object) {
    return (eventComparator.compare(this, object));
  }


  /**
   * Description of the Class
   *
   * @author matt rajkowski
   * @created July 27, 2001
   */
  class comparatorEvent implements Comparator {
    /**
     * Description of the Method
     *
     * @param left  Description of Parameter
     * @param right Description of Parameter
     * @return Description of the Returned Value
     */
    public int compare(Object left, Object right) {
      return (((CalendarEvent) left).getDateTimeString().compareTo(
          ((CalendarEvent) right).getDateTimeString()));
    }

  }
}

