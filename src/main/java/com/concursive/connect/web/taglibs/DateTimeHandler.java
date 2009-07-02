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

import com.concursive.commons.date.DateUtils;
import com.concursive.connect.Constants;
import com.concursive.connect.web.modules.login.dao.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * This Class formats the specified date/time with the timezone for the current
 * UserBean session.
 *
 * @author matt rajkowski
 * @created September 3, 2003
 */
public class DateTimeHandler extends TagSupport {

  private static Log LOG = LogFactory.getLog(DateTimeHandler.class);

  private Timestamp timestamp = null;
  private boolean dateOnly = false;
  private boolean timeOnly = false;
  private int timeFormat = DateFormat.SHORT;
  private int dateFormat = DateFormat.SHORT;
  private String pattern = null;
  private String defaultValue = "";


  /**
   * The date to be formatted
   *
   * @param tmp The new timestamp value
   */
  public void setTimestamp(Timestamp tmp) {
    this.timestamp = tmp;
  }

  public void setDate(Date tmp) {
    this.timestamp = new Timestamp(tmp.getTime());
  }

  /**
   * Default in case the date is empty/null
   *
   * @param tmp The new default value
   */
  public void setDefault(String tmp) {
    this.defaultValue = tmp;
  }


  /**
   * Gets the date only without the time
   *
   * @param dateOnly The new dateOnly value
   */
  public void setDateOnly(boolean dateOnly) {
    this.dateOnly = dateOnly;
  }


  /**
   * Gets the date without the time
   *
   * @param dateOnly The new dateOnly value
   */
  public void setDateOnly(String dateOnly) {
    this.dateOnly = "true".equals(dateOnly);
  }

  public void setTimeOnly(boolean tmp) {
    this.timeOnly = tmp;
  }

  public void setTimeOnly(String tmp) {
    this.timeOnly = "true".equals(tmp);
  }


  /**
   * Sets a pattern
   *
   * @param pattern The new pattern value
   */
  public void setPattern(String pattern) {
    this.pattern = pattern;
  }


  /**
   * Sets a time format
   *
   * @param timeFormat The new timeFormat value
   */
  public void setTimeFormat(int timeFormat) {
    this.timeFormat = timeFormat;
  }


  /**
   * Sets a time format
   *
   * @param timeFormat The new timeFormat value
   */
  public void setTimeFormat(String timeFormat) {
    this.timeFormat = Integer.parseInt(timeFormat);
  }


  /**
   * Sets the date format
   *
   * @param dateFormat The new dateFormat value
   */
  public void setDateFormat(int dateFormat) {
    this.dateFormat = dateFormat;
  }


  /**
   * Sets the date format
   *
   * @param dateFormat The new dateFormat value
   */
  public void setDateFormat(String dateFormat) {
    this.dateFormat = Integer.parseInt(dateFormat);
  }


  /**
   * Description of the Method
   *
   * @return Description of the Return Value
   * @throws JspException Description of the Exception
   */
  public int doStartTag() throws JspException {
    try {
      if (timestamp != null) {
        String timeZone = null;
        Locale locale = null;
        // Retrieve the user's timezone from their session
        User thisUser = (User) pageContext.getSession().getAttribute(Constants.SESSION_USER);
        if (thisUser != null) {
          timeZone = thisUser.getTimeZone();
          locale = thisUser.getLocale();
        }
        if (locale == null) {
          locale = Locale.getDefault();
        }
        // Determine the output type
        if (pattern != null && "relative".equals(pattern)) {
          // Output a relative date
          String relativeDate = DateUtils.createRelativeDate(timestamp);
          if (relativeDate != null) {
            this.pageContext.getOut().write(relativeDate);
          }
        } else {
          // Format the specified timestamp with the retrieved timezone
          SimpleDateFormat formatter = null;
          if (dateOnly) {
            formatter = (SimpleDateFormat) SimpleDateFormat.getDateInstance(
                dateFormat, locale);
          } else if (timeOnly) {
            formatter = (SimpleDateFormat) SimpleDateFormat.getTimeInstance(
                timeFormat, locale);
          } else {
            formatter = (SimpleDateFormat) SimpleDateFormat.getDateTimeInstance(
                dateFormat, timeFormat, locale);
          }

          // Use a Java formatter pattern
          if (pattern != null) {
            formatter.applyPattern(pattern);
          }
          // Adjust the date/time based on any timezone
          if (timeZone != null) {
            java.util.TimeZone tz = java.util.TimeZone.getTimeZone(timeZone);
            formatter.setTimeZone(tz);
          }
          this.pageContext.getOut().write(formatter.format(timestamp));
        }
      } else {
        //no date found, output default
        this.pageContext.getOut().write(defaultValue);
      }
    } catch (Exception e) {
      LOG.debug("Conversion error", e);
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

