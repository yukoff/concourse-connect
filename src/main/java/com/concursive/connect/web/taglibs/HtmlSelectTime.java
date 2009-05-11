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

package com.concursive.connect.web.taglibs;

import com.concursive.commons.db.DatabaseUtils;
import com.concursive.connect.Constants;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.utils.HtmlSelectAMPM;
import com.concursive.connect.web.utils.HtmlSelectHours;
import com.concursive.connect.web.utils.HtmlSelectHours24;
import com.concursive.connect.web.utils.HtmlSelectMinutesFives;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Description of the Class
 *
 * @author matt rajkowski
 * @version $Id$
 * @created July 2, 2003
 */
public class HtmlSelectTime extends TagSupport {

  private String baseName = null;
  private Timestamp value = null;
  private String timeZone = null;


  /**
   * Sets the baseName attribute of the HtmlSelectTime object
   *
   * @param tmp The new baseName value
   */
  public void setBaseName(String tmp) {
    this.baseName = tmp;
  }


  /**
   * Sets the value attribute of the HtmlSelectTime object
   *
   * @param tmp The new value value
   */
  public void setValue(String tmp) {
    this.setValue(DatabaseUtils.parseTimestamp(tmp));
  }


  /**
   * Sets the value attribute of the HtmlSelectTime object
   *
   * @param tmp The new value value
   */
  public void setValue(java.sql.Timestamp tmp) {
    value = tmp;
  }


  /**
   * Sets the timeZone attribute of the HtmlSelectTime object
   *
   * @param tmp The new timeZone value
   */
  public void setTimeZone(String tmp) {
    timeZone = tmp;
  }


  /**
   * Description of the Method
   *
   * @return Description of the Return Value
   * @throws JspException Description of the Exception
   */
  public int doStartTag() throws JspException {
    try {
      Locale locale = Locale.getDefault();
      // Retrieve the user's locale from their session
      User thisUser = (User) pageContext.getSession().getAttribute(Constants.SESSION_USER);
      if (thisUser != null) {
        locale = thisUser.getLocale();
      }
      // Calculate the date and time
      boolean is24Hour = false;
      int hour = -1;
      int minute = -1;
      int AMPM = -1;
      Calendar cal = Calendar.getInstance();
      if (timeZone != null) {
        cal.setTimeZone(TimeZone.getTimeZone(timeZone));
      }
      try {
        cal.setTimeInMillis(value.getTime());
      } catch (Exception e) {
        cal.set(Calendar.HOUR, 12);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.AM_PM, Calendar.PM);
      }
      hour = cal.get(Calendar.HOUR);
      minute = cal.get(Calendar.MINUTE);
      AMPM = cal.get(Calendar.AM_PM);
      // Check if parsing has AM/PM else show 24 hour time
      DateFormat formatter = (DateFormat) DateFormat.getTimeInstance(
          DateFormat.SHORT, locale);
      if (formatter.format(cal.getTime()).indexOf("M") == -1) {
        is24Hour = true;
        hour = cal.get(Calendar.HOUR_OF_DAY);
      }
      //System.out.println(formatter.format(cal.getTime()));
      // output the results
      if (is24Hour) {
        // Show 24 hour selector
        this.pageContext.getOut().write(
            HtmlSelectHours24.getSelect(baseName + "Hour", (hour < 10 ? String.valueOf("0" + hour) : String.valueOf(hour))).toString());
      } else {
        // Show 12 hour selector
        this.pageContext.getOut().write(
            HtmlSelectHours.getSelect(baseName + "Hour", (hour < 10 ? String.valueOf("0" + hour) : String.valueOf(hour))).toString());
      }
      this.pageContext.getOut().write(":");
      this.pageContext.getOut().write(
          HtmlSelectMinutesFives.getSelect(baseName + "Minute",
              (minute < 10 ? String.valueOf("0" + minute) : String.valueOf(minute))).toString());
      if (is24Hour) {
        // Do not show AM/PM
      } else {
        if (AMPM == Calendar.AM) {
          this.pageContext.getOut().write(
              HtmlSelectAMPM.getSelect(baseName + "AMPM", "AM").toString());
        } else {
          this.pageContext.getOut().write(
              HtmlSelectAMPM.getSelect(baseName + "AMPM", "PM").toString());
        }
      }
    } catch (Exception e) {
      throw new JspException("HtmlSelectTime Error: " + e.getMessage());
    }
    return SKIP_BODY;
  }


  /**
   * Description of the Method
   *
   * @return Description of the Return Value
   */
  public int doEndTag() {
    return EVAL_PAGE;
  }

}

