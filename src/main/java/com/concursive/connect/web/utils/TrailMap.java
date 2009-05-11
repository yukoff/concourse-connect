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

package com.concursive.connect.web.utils;

import com.concursive.commons.objects.ObjectUtils;
import com.concursive.commons.text.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.StringTokenizer;

/**
 * Maintains a trail of terms, resetting to the point where an existing
 * term is found
 *
 * @author matt rajkowski
 * @version $Id$
 * @created February 24, 2006
 */
public class TrailMap extends ArrayList {

  public TrailMap() {
  }

  /**
   * Adds a unique set of parameters, no duplicates and nothing after a duplicate
   *
   * @param subject
   */
  public synchronized void addItem(String subject) {
    if (subject == null || "".equals(subject)) {
      // Reset the trail
      this.clear();
    } else {
      int index = this.indexOf(subject);
      if (index > -1) {
        ListIterator li = this.listIterator(index);
        // Skip this matching item
        li.next();
        // Remove the rest
        while (li.hasNext()) {
          String n = (String) li.next();
          li.remove();
        }
      } else {
        // Add to the end
        this.add(subject);
      }
    }
  }

  public synchronized void addItems(String items) {
    if (items == null || "".equals(items)) {
      this.clear();
    } else {
      StringTokenizer st = new StringTokenizer(items, "|");
      while (st.hasMoreTokens()) {
        this.add(st.nextToken());
      }
    }
  }

  public String getTrailParameters() {
    return getTrailParameters(null);
  }

  public String getTrailParameters(String key) {
    StringBuffer sb = new StringBuffer();
    String previous = null;
    Iterator i = this.iterator();
    while (i.hasNext()) {
      String thisSubject = (String) i.next();
      if (thisSubject.equals(key)) {
        i.next();
        continue;
      }
      if (!thisSubject.equals(previous)) {
        if (sb.length() > 0) {
          sb.append("|");
        }
        sb.append(StringUtils.jsEscape(thisSubject));
      }
      previous = thisSubject;
    }
    return sb.toString();
  }

  public void applyFilters(Object listObject) {
    applyFilters(listObject, null);
  }

  public void applyFilters(Object listObject, String ignore) {
    for (int i = 0; i < this.size(); i++) {
      if (i % 2 == 0) {
        String subject = (String) this.get(i);
        String value = (String) this.get(i + 1);
        if (ignore == null || !ignore.equals(value)) {
          ObjectUtils.setParam(listObject, subject, value);
        }
      }
    }
  }
}
