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

import java.util.GregorianCalendar;

/**
 * Calculates and returns Easter Day for specified year.
 *
 * @author Originally from Mark Lussier, AppVision <MLussier@best.com>
 * @version $Id$
 * @created November 17, 2004
 */
public class EasterHoliday {

  /**
   * Gets the calendar attribute of the EasterHoliday class
   *
   * @param year Description of the Parameter
   * @return The calendar value
   */
  public final static GregorianCalendar getCalendar(int year) {
    int nMonth;
    int nDay;
    int nMoon;
    int nEpact;
    int nSunday;
    int nGold;
    int nCent;
    int nCorx;
    int nCorz;
    // The Golden Number of the year in the 19 year Metonic Cycle
    nGold = (year % 19) + 1;
    // Calculate the Century: }
    nCent = (year / 100) + 1;
    // Number of years in which leap year was dropped in order...
    // to keep in step with the sun: }
    nCorx = (3 * nCent) / 4 - 12;
    // Special correction to syncronize Easter with moon's orbit
    nCorz = (8 * nCent + 5) / 25 - 5;
    // Find Sunday
    nSunday = (5 * year) / 4 - nCorx - 10;
    // Set Epact - specifies occurrence of full moon
    nEpact = (11 * nGold + 20 + nCorz - nCorx) % 30;
    if (nEpact < 0) {
      nEpact = nEpact + 30;
    }
    if (((nEpact == 25) && (nGold > 11)) || (nEpact == 24)) {
      nEpact = nEpact + 1;
    }
    // Find Full Moon
    nMoon = 44 - nEpact;
    if (nMoon < 21) {
      nMoon = nMoon + 30;
    }
    // Advance to Sunday
    nMoon = nMoon + 7 - ((nSunday + nMoon) % 7);
    if (nMoon > 31) {
      nMonth = 4;
      nDay = nMoon - 31;
    } else {
      nMonth = 3;
      nDay = nMoon;
    }
    if (System.getProperty("DEBUG") != null) {
      System.out.println("Easter: " + nMonth + "/" + nDay + "/" + year);
    }
    return new GregorianCalendar(year, nMonth - 1, nDay);
  }
}

