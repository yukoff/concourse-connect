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

import java.text.NumberFormat;

/**
 * Presents an HTML currency selection based on allowed Java currencies
 *
 * @author matt rajkowski
 * @version $Id: HtmlSelectCurrencyCode.java,v 1.2 2004/03/19 04:56:17 matt
 *          Exp $
 * @created March 17, 2004
 */
public class HtmlSelectCurrencyCode {

  /**
   * Constructor for the HtmlSelectCurrencyCode object
   */
  public HtmlSelectCurrencyCode() {
  }


  /**
   * Gets the select attribute of the HtmlSelectCurrencyCode class
   *
   * @param name         Description of the Parameter
   * @param defaultValue Description of the Parameter
   * @return The select value
   */
  public static HtmlSelect getSelect(String name, String defaultValue) {
    if (defaultValue == null) {
      defaultValue = NumberFormat.getCurrencyInstance().getCurrency().getCurrencyCode();
    }
    HtmlSelect select = new HtmlSelect();
    select.setSelectName(name);
    select.setDefaultValue(defaultValue);
    // TODO: Sort these items
    select.addItem("AUD");
    //select.addItem("ATS");
    //select.addItem("BHD");
    //select.addItem("BEF");
    select.addItem("CAD");
    //select.addItem("XPF");
    //select.addItem("CYP");
    //select.addItem("DKK");
    select.addItem("EUR");
    //select.addItem("FIM");
    //select.addItem("FRF");
    //select.addItem("DEM");
    //select.addItem("GRD");
    //select.addItem("HKD");
    //select.addItem("INR");
    //select.addItem("IEP");
    //select.addItem("ITL");
    select.addItem("GBP");
    select.addItem("JPY");
    //select.addItem("KES");
    //select.addItem("KWD");
    //select.addItem("MTL");
    //select.addItem("NLG");
    //select.addItem("NZD");
    //select.addItem("NOK");
    //select.addItem("PGK");
    //select.addItem("PKR");
    //select.addItem("PHP");
    //select.addItem("PTE");
    //select.addItem("SAR");
    //select.addItem("SGD");
    //select.addItem("ESP");
    //select.addItem("LKR");
    //select.addItem("ZAR");
    //select.addItem("SEK");
    //select.addItem("CHF");
    //select.addItem("THB");
    //select.addItem("AED");
    select.addItem("USD");
    //select.addItem("WST");
    if (!select.hasKey(defaultValue)) {
      select.addItem(defaultValue);
    }
    return select;
  }
}

