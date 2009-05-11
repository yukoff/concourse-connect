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

package com.concursive.connect.web.webdav.utils;

import com.concursive.commons.db.DatabaseUtils;

import java.sql.Timestamp;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Currency;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Represents an ICalendar Object(.ics) for a particular user's
 * Calendar of Events. The calendar consists of various components like events,
 * alarms, todo lists and others. The calendar has a sequence of properties
 * followed by one or more components.<p>
 * <p/>
 * TODOs:<br>
 * - Task Events (Pending) <br>
 * <p/>
 * <p/>
 * EVENTS:<br>
 * - Call Events (Pending, Complete)<br>
 * - Project Assignments (Any Status)<br>
 * - Opportunity Events<br>
 * - Account Events<br>
 * - Account Contract Events<br>
 * - Ticket Request Events<br>
 *
 * @author ananth
 * @created December 1, 2004
 */
public class ICalendar {
  private StringBuffer buffer = new StringBuffer();
  private Timestamp created = null;
  private Calendar start = null;
  private Calendar end = null;

  private final static String CRLF = System.getProperty("line.separator");

  /**
   * Sets the created attribute of the ICalendar object
   *
   * @param tmp The new created value
   */
  public void setCreated(Timestamp tmp) {
    this.created = tmp;
  }


  /**
   * Sets the created attribute of the ICalendar object
   *
   * @param tmp The new created value
   */
  public void setCreated(String tmp) {
    this.created = DatabaseUtils.parseTimestamp(tmp);
  }


  /**
   * Sets the start attribute of the ICalendar object
   *
   * @param tmp The new start value
   */
  public void setStart(Calendar tmp) {
    this.start = tmp;
  }


  /**
   * Sets the end attribute of the ICalendar object
   *
   * @param tmp The new end value
   */
  public void setEnd(Calendar tmp) {
    this.end = tmp;
  }


  /**
   * Gets the created attribute of the ICalendar object
   *
   * @return The created value
   */
  public Timestamp getCreated() {
    return created;
  }


  /**
   * Gets the start attribute of the ICalendar object
   *
   * @return The start value
   */
  public Calendar getStart() {
    return start;
  }


  /**
   * Gets the end attribute of the ICalendar object
   *
   * @return The end value
   */
  public Calendar getEnd() {
    return end;
  }


  /**
   * Constructor for the ICalendar object
   */
  public ICalendar() {
  }


  /**
   * returns the timestamp in the format: yyyymmdd"T"hhmmss
   *
   * @param ts Description of the Parameter
   * @param tz Description of the Parameter
   * @return The dateTime value
   */
  public static String getDateTime(TimeZone tz, Timestamp ts) {
    Calendar cal = Calendar.getInstance(tz);
    cal.setTimeInMillis(ts.getTime());

    String year = String.valueOf(cal.get(Calendar.YEAR));
    String month = String.valueOf(cal.get(Calendar.MONTH) + 1);
    String date = String.valueOf(cal.get(Calendar.DATE));
    String hours = String.valueOf(cal.get(Calendar.HOUR_OF_DAY));
    String minutes = String.valueOf(cal.get(Calendar.MINUTE));
    String seconds = String.valueOf(cal.get(Calendar.SECOND));

    return ((year.length() == 2 ? "00" + year : year) + (month.length() == 1 ? "0" + month : month) +
        (date.length() == 1 ? "0" + date : date) + "T" + (hours.length() == 1 ? "0" + hours : hours) +
        (minutes.length() == 1 ? "0" + minutes : minutes) + (seconds.length() == 1 ? "0" + seconds : seconds));
  }


  /**
   * Gets the dateTimeUTC attribute of the ICalendar object
   *
   * @param ts Description of the Parameter
   * @return The dateTimeUTC value
   */
  public static String getDateTimeUTC(Timestamp ts) {
    Calendar cal = Calendar.getInstance();
    cal.setTimeInMillis(ts.getTime());

    String year = String.valueOf(cal.get(Calendar.YEAR));
    String month = String.valueOf(cal.get(Calendar.MONTH) + 1);
    String date = String.valueOf(cal.get(Calendar.DATE));
    String hours = String.valueOf(cal.get(Calendar.HOUR_OF_DAY));
    String minutes = String.valueOf(cal.get(Calendar.MINUTE));
    String seconds = String.valueOf(cal.get(Calendar.SECOND));
    return ((year.length() == 2 ? "00" + year : year) + (month.length() == 1 ? "0" + month : month) +
        (date.length() == 1 ? "0" + date : date) + "T" + (hours.length() == 1 ? "0" + hours : hours) +
        (minutes.length() == 1 ? "0" + minutes : minutes) + (seconds.length() == 1 ? "0" + seconds : seconds) + "Z");
  }


  /**
   * Description of the Method
   *
   * @param input Description of the Parameter
   * @return Description of the Return Value
   */
  public static String parseNewLine(String input) {
    StringBuffer clean = new StringBuffer();
    for (int i = 0; i < input.length(); ++i) {
      if (input.charAt(i) == '\n') {
        clean.append("\\n");
      } else {
        clean.append(input.charAt(i));
      }
    }
    return clean.toString().trim();
  }

  /**
   * The iCalendar object is organized into individual lines of text, called content lines. Content lines are delimited
   * by a line break, which is a CRLF sequence (US-ASCII decimal 13, followed by US-ASCII decimal 10). Lines of
   * text SHOULD NOT be longer than 75 octets, excluding the line break. Long content lines SHOULD be split into
   * a multiple line representations using a line "folding" technique. That is, a long line can be split between any
   * two characters by inserting a CRLF immediately followed by a single linear white space character (i.e., SPACE,
   * US-ASCII decimal 32 or HTAB, US-ASCII decimal 9). Any sequence of CRLF followed immediately by a single
   * linear white space character is ignored (i.e., removed) when processing the content type.
   *
   * @param input Description of the Parameter
   * @return Description of the Return Value
   */
  public static String foldLine(String input) {
    if (input != null) {
      int length = input.length();
      if (length <= 70) {
        return input;
      } else {
        //Fold the input string
        String result = "";
        int fold = length / 70; //number of times to fold
        int begin = 0, end = 70;
        for (int count = 0; count < fold; count++) {
          result += input.substring(begin, end) + CRLF + " "; //Folding
          begin = end;
          end = end + 70; //gets the next 70 chars
        }
        if (begin <= length) {
          result += input.substring(begin, length);
        }
        return result;
      }
    }
    return "";
  }

  /**
   * Gets the bytes attribute of the ICalendar object
   *
   * @return The bytes value
   */
  public byte[] getBytes() {
    return buffer.toString().getBytes();
  }


  /**
   * Gets the currencyFormat attribute of the ICalendar object
   *
   * @param value Description of the Parameter
   * @return The currencyFormat value
   */
  public static String getCurrencyFormat(double value, String currency, Locale locale) {
    NumberFormat formatter = NumberFormat.getCurrencyInstance(locale);
    formatter.setMaximumFractionDigits(4);
    if (currency != null) {
      Currency cur = Currency.getInstance(currency);
      formatter.setCurrency(cur);
    }
    return (formatter.format(value));
  }
}

