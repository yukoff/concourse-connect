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

package com.concursive.connect.web.modules.productcatalog.dao;

import com.concursive.commons.text.StringUtils;
import com.concursive.commons.web.mvc.beans.GenericBean;

import java.util.Calendar;

/**
 * Description of the Class
 *
 * @author matt rajkowski
 * @version $Id$
 * @created September 21, 2004
 */
public class CreditCard extends GenericBean {
  private int id = -1;
  private String type = null;
  private String number = null;
  private int expirationMonth = -1;
  private int expirationYear = -1;

  /**
   * Constructor for the CreditCard object
   */
  public CreditCard() {
  }


  /**
   * Gets the id attribute of the CreditCard object
   *
   * @return The id value
   */
  public int getId() {
    return id;
  }


  /**
   * Sets the id attribute of the CreditCard object
   *
   * @param tmp The new id value
   */
  public void setId(int tmp) {
    this.id = tmp;
  }


  /**
   * Sets the id attribute of the CreditCard object
   *
   * @param tmp The new id value
   */
  public void setId(String tmp) {
    this.id = Integer.parseInt(tmp);
  }


  /**
   * Gets the type attribute of the CreditCard object
   *
   * @return The type value
   */
  public String getType() {
    return type;
  }


  /**
   * Sets the type attribute of the CreditCard object
   *
   * @param tmp The new type value
   */
  public void setType(String tmp) {
    this.type = tmp;
  }


  /**
   * Gets the number attribute of the CreditCard object
   *
   * @return The number value
   */
  public String getNumber() {
    return number;
  }


  /**
   * Gets the numberLast4 attribute of the CreditCard object
   *
   * @return The numberLast4 value
   */
  public String getNumberLast4() {
    return number.substring(number.length() - 4);
  }


  /**
   * Sets the number attribute of the CreditCard object
   *
   * @param tmp The new number value
   */
  public void setNumber(String tmp) {
    this.number = tmp;
  }

  public String getNumericNumber() {
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < number.length(); i++) {
      if ("0123456789".indexOf(number.charAt(i)) > -1) {
        sb.append(number.charAt(i));
      }
    }
    return sb.toString();
  }

  /**
   * Gets the expirationMonth attribute of the CreditCard object
   *
   * @return The expirationMonth value
   */
  public int getExpirationMonth() {
    return expirationMonth;
  }

  public String getExpirationMonthString() {
    if (expirationMonth < 10) {
      return "0" + expirationMonth;
    }
    return String.valueOf(expirationMonth);
  }


  /**
   * Sets the expirationMonth attribute of the CreditCard object
   *
   * @param tmp The new expirationMonth value
   */
  public void setExpirationMonth(int tmp) {
    this.expirationMonth = tmp;
  }


  /**
   * Sets the expirationMonth attribute of the CreditCard object
   *
   * @param tmp The new expirationMonth value
   */
  public void setExpirationMonth(String tmp) {
    this.expirationMonth = Integer.parseInt(tmp);
  }


  /**
   * Gets the expirationYear attribute of the CreditCard object
   *
   * @return The expirationYear value
   */
  public int getExpirationYear() {
    return expirationYear;
  }


  /**
   * Sets the expirationYear attribute of the CreditCard object
   *
   * @param tmp The new expirationYear value
   */
  public void setExpirationYear(int tmp) {
    this.expirationYear = tmp;
  }


  /**
   * Sets the expirationYear attribute of the CreditCard object
   *
   * @param tmp The new expirationYear value
   */
  public void setExpirationYear(String tmp) {
    this.expirationYear = Integer.parseInt(tmp);
  }

  /**
   * Gets the valid attribute of the CreditCard object
   *
   * @return The valid value
   */
  public boolean isValid() {
    errors.clear();
    if (type == null || "".equals(type.trim())) {
      errors.put("typeError", "Credit Card Type is required");
    }
    if (number == null || "".equals(number.trim()) || !StringUtils.hasNumberCount(number.trim(), 16)) {
      errors.put("numberError", "Credit Card Number is required");
    }
    if (expirationMonth == -1) {
      errors.put("expirationMonthError", "Expiration Month is required");
    }
    if (expirationYear == -1) {
      errors.put("expirationYearError", "Expiration Year is required");
    }
    Calendar today = Calendar.getInstance();
    int year = today.get(Calendar.YEAR);
    int month = today.get(Calendar.MONTH) + 1;
    // See if today's year is greater than specified year
    if (year > expirationYear) {
      errors.put("expirationYearError", "The card seems to have expired");
    }
    if (year == expirationYear) {
      // See if the month is greater than specified month
      if (month > expirationMonth) {
        errors.put("expirationMonthError", "The card seems to have expired");
      }
    }
    return (!hasErrors());
  }

  /**
   * Description of the Method
   *
   * @return Description of the Return Value
   */
  public String toString() {
    StringBuffer out = new StringBuffer();
    out.append("Bank: " + StringUtils.toString(type) + "\r\n");
    out.append("Number: " + "xxxx xxxx xxxx " + StringUtils.toString(this.getNumberLast4()) + "\r\n");
    out.append("Expiration: " + expirationMonth + "/" + expirationYear + "\r\n");
    return out.toString();
  }
}

