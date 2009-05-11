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

package com.concursive.connect.config;

/**
 * Class for reading the application version information; APP_VERSION triggers
 * CSS and Javascript updates; VERSION and DB_VERSION trigger application
 * upgrades
 *
 * @author matt rajkowski
 * @created August 30, 2004
 */
public class ApplicationVersion {
  public final static String TITLE = "ConcourseConnect";
  public final static String VERSION = "1.0 beta (2009-05-04)";
  public final static String APP_VERSION = "2009-05-04";
  public final static String DB_VERSION = "2009-05-04";


  /**
   * Gets the outOfDate attribute of the ApplicationVersion class
   *
   * @param prefs Description of the Parameter
   * @return The outOfDate value
   */
  public static boolean isOutOfDate(ApplicationPrefs prefs) {
    String installedVersion = getInstalledVersion(prefs);
    return !"true".equals(prefs.get("MANUAL_UPGRADE")) && !installedVersion.equals(ApplicationVersion.VERSION);
  }


  /**
   * Gets the installedVersion attribute of the ApplicationVersion class
   *
   * @param prefs Description of the Parameter
   * @return The installedVersion value
   */
  public static String getInstalledVersion(ApplicationPrefs prefs) {
    String installedVersion = prefs.get("VERSION");
    if (installedVersion == null || "".equals(installedVersion)) {
      return "2.1.3 (2004-09-13)";
    } else {
      return installedVersion;
    }
  }
}

