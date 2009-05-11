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

package com.concursive.connect.web.modules.contacts.utils;

/**
 * Utilities for working with Contact properties
 *
 * @author matt rajkowski
 * @version $Id$
 * @created August 29, 2001
 */
public class ContactUtils {


  /**
   * Combines the first and last name of a contact, depending on the length of
   * the strings
   *
   * @param nameLast  Description of the Parameter
   * @param nameFirst Description of the Parameter
   * @return The nameLastFirst value
   */
  public static String getNameLastFirst(String nameLast, String nameFirst) {
    StringBuffer out = new StringBuffer();
    if (nameLast != null && nameLast.trim().length() > 0) {
      out.append(nameLast);
    }
    if (nameFirst != null && nameFirst.trim().length() > 0) {
      if (out.length() > 0) {
        out.append(", ");
      }
      out.append(nameFirst);
    }
    if (out.toString().length() == 0) {
      return null;
    }
    return out.toString().trim();
  }


  /**
   * Gets the nameFirstLast attribute of the Contact class
   *
   * @param nameFirst Description of the Parameter
   * @param nameLast  Description of the Parameter
   * @return The nameFirstLast value
   */
  public static String getNameFirstLast(String nameFirst, String nameLast) {
    StringBuffer out = new StringBuffer();
    if (nameFirst != null && nameFirst.trim().length() > 0) {
      out.append(nameFirst);
    }
    if (nameLast != null && nameLast.trim().length() > 0) {
      if (out.length() > 0) {
        out.append(" ");
      }
      out.append(nameLast);
    }
    if (out.toString().length() == 0) {
      return null;
    }
    return out.toString().trim();
  }
}

