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

package com.concursive.connect.web.controller.utils;

import com.concursive.commons.date.DateUtils;
import com.concursive.commons.db.DatabaseUtils;
import com.concursive.commons.objects.ObjectUtils;
import com.concursive.commons.web.mvc.beans.BeanUtils;
import com.concursive.connect.Constants;
import com.concursive.connect.web.modules.login.dao.User;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.*;

/**
 * Enhanced web app capabilities
 *
 * @author matt rajkowski
 * @version $Id$
 * @created March 14, 2004
 */
public class AutoPopulate extends BeanUtils {

  /**
   * When objects are being auto-populated from an HTML request, additional
   * properties can be populated from multiple fields
   *
   * @param bean            Description of the Parameter
   * @param request         Description of the Parameter
   * @param nestedAttribute Description of the Parameter
   * @param indexAttribute  Description of the Parameter
   */
  public void populateObject(Object bean, HttpServletRequest request,
                             String nestedAttribute, String indexAttribute) {
    super.populateObject(bean, request, nestedAttribute, indexAttribute);
    ObjectUtils.invokeMethod(bean, "setRequestItems", new HttpRequestContext(request));
    // Check for valid user
    User thisUser = (User) request.getSession().getAttribute(Constants.SESSION_USER);
    if (thisUser != null) {
      // Populate date/time fields using the user's timezone and locale
      if (thisUser.getTimeZone() != null) {
        ArrayList timeParams = (ArrayList) ObjectUtils.getObject(bean, "TimeZoneParams");
        if (timeParams != null) {
          Iterator i = timeParams.iterator();
          while (i.hasNext()) {
            Calendar cal = Calendar.getInstance();
            // The property that can be set
            String name = (String) i.next();
            // See if it is in the request
            String value = (String) request.getParameter(name);
            if (value != null) {
              // See if time is in request too
              String hourValue = (String) request.getParameter(name + "Hour");
              if (hourValue == null) {
                // Date fields: 1-1 mapping between HTML field and Java property
                ObjectUtils.setParam(bean, name, DateUtils.getUserToServerDateTimeString(TimeZone.getTimeZone(thisUser.getTimeZone()), DateFormat.SHORT, DateFormat.LONG, value, thisUser.getLocale()));
              } else {
                // Date & Time fields: 4-1 mapping between HTML fields and Java property
                try {
                  Timestamp timestamp = DatabaseUtils.parseDateToTimestamp(value, thisUser.getLocale());
                  cal.setTimeInMillis(timestamp.getTime());
                  int hour = Integer.parseInt(hourValue);
                  int minute = Integer.parseInt((String) request.getParameter(name + "Minute"));
                  String ampmString = request.getParameter(name + "AMPM");
                  if (ampmString != null) {
                    int ampm = Integer.parseInt(ampmString);
                    if (ampm == Calendar.AM) {
                      if (hour == 12) {
                        hour = 0;
                      }
                    } else {
                      if (hour < 12) {
                        hour += 12;
                      }
                    }
                  }
                  cal.set(Calendar.HOUR_OF_DAY, hour);
                  cal.set(Calendar.MINUTE, minute);
                  cal.setTimeZone(TimeZone.getTimeZone(thisUser.getTimeZone()));
                  ObjectUtils.setParam(bean, name, new Timestamp(cal.getTimeInMillis()));
                } catch (Exception dateE) {
                }
              }
            }
          }
        }
      }

      // Populate number fields using the user's locale
      if (thisUser.getLocale() != null) {
        ArrayList numberParams = (ArrayList) ObjectUtils.getObject(bean, "NumberParams");
        if (numberParams != null) {
          NumberFormat nf = NumberFormat.getInstance(thisUser.getLocale());
          Iterator i = numberParams.iterator();
          while (i.hasNext()) {
            // The property that can be set
            String name = (String) i.next();
            // See if it is in the request
            String value = (String) request.getParameter(name);
            if (value != null && !"".equals(value)) {
              try {
                // Parse the value
                ObjectUtils.setParam(bean, name, nf.parse(value).doubleValue());
              } catch (Exception e) {
                //e.printStackTrace(System.out);
              }
            }
          }
        }
      }
    }
  }

  /**
   * When a form is submitted with enctype="multipart/form-data", then the
   * parameters and values are placed into a parts HashMap which can now
   * be auto-populated
   *
   * @param bean
   * @param parts
   */
  public static void populateObject(Object bean, HashMap parts) {
    if (parts != null) {
      Iterator names = parts.keySet().iterator();
      while (names.hasNext()) {
        String paramName = (String) names.next();
        Object paramValues = parts.get(paramName);
        if (paramValues != null && paramValues instanceof String) {
          BeanUtils.populateParameter(paramName, (String) paramValues, bean, "_", "-");
        }
      }
    }
  }
}

