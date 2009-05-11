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
 * under the GNU Affero General Public License. Ê
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

/**
 * Checks to see if a date is entered in 3-20-2000 or 3/20/2000 or 3.20.2000 format
 * @arg1 = date to check
 */

function checkDate(datein) {
  var m = 0, d = 0, y = 0;
  var sep;

  if (datein.indexOf("/") != -1) {
    sep = datein.split("/");
  } else if (datein.indexOf("-") != -1) {
    sep = datein.split("-");
  } else if (datein.indexOf(".") != -1) {
    sep = datein.split(".");
  } else {
    return false;
  }
  
  if (checkDigits(sep[0])) {
    m = parseInt(sep[0],10);
  } else {
    return false;
  }
  if (checkDigits(sep[1])) {
    d = parseInt(sep[1],10);
  } else {
    return false;
  }
  if (sep[2] != null) { 
    if (checkDigits(sep[2])) {
      y = parseInt(sep[2],10);
    } else {
      return false;
    }
  } else {
    return false;
  }
  
  if (sep[3] != null) return false;
  
  if ((m <= 0 || m > 31) ||
      (d <= 0 || d > 31) ||
      (y < 0 || (y > 99 && y < 999) || y > 2200)) {
    return false;
  } else {
    return true;
  }
}

function checkDigits(str) {
  var valid = "0123456789";
  for (var i=0; i < str.length; i++) {
    temp = str.substring(i, i+1);
    if (valid.indexOf(temp) == -1) {
      return false;
    }
  }
  return true;
}

