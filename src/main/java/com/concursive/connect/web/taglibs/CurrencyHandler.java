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

import com.concursive.commons.text.StringUtils;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

/**
 * This Class formats the specified amount with the specified currency
 *
 * @author matt rajkowski
 * @version $Id$
 * @created March 17, 2004
 */
public class CurrencyHandler extends TagSupport {

  private double value = -1;
  private String code = null;
  private String defaultValue = null;
  private Locale locale = null;


  /**
   * Sets the value attribute of the CurrencyHandler object
   *
   * @param tmp The new value value
   */
  public void setValue(double tmp) {
    this.value = tmp;
  }


  /**
   * Sets the code attribute of the CurrencyHandler object
   *
   * @param tmp The new code value
   */
  public void setCode(String tmp) {
    this.code = tmp;
  }


  /**
   * Sets the default attribute of the CurrencyHandler object
   *
   * @param tmp The new default value
   */
  public void setDefault(String tmp) {
    this.defaultValue = tmp;
  }


  /**
   * Sets the locale attribute of the CurrencyHandler object
   *
   * @param tmp The new locale value
   */
  public void setLocale(Locale tmp) {
    this.locale = tmp;
  }


  /**
   * Description of the Method
   *
   * @return Description of the Return Value
   * @throws JspException Description of the Exception
   */
  public int doStartTag() throws JspException {
    try {
      if (value > -1) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(locale);
        if (code != null) {
          Currency currency = Currency.getInstance(code);
          formatter.setCurrency(currency);
        }
        this.pageContext.getOut().write(StringUtils.toHtmlValue(formatter.format(value)));
      } else {
        //no data found, output default
        if (defaultValue != null) {
          this.pageContext.getOut().write(defaultValue);
        }
      }
    } catch (Exception e) {
      System.out.println(e.getMessage());
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

