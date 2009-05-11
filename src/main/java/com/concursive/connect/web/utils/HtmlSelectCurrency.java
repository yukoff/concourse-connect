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
 * @version $Id$
 * @created March 17, 2004
 */
public class HtmlSelectCurrency {

  /**
   * Gets the select attribute of the HtmlSelectCurrency class
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
    select.addItem("AUD", "Australian Currency (AUD)");
    //select.addItem("ATS", "Austrian Schillings (ATS)");
    //select.addItem("BHD", "Bahrain Dinars (BHD)");
    //select.addItem("BEF", "Belgian Franc (BEF)");
    select.addItem("CAD", "Canadian Dollars (CAD)");
    //select.addItem("XPF", "Cfp Francs (XPF)");
    //select.addItem("CYP", "Cyprus Pounds (CYP)");
    //select.addItem("DKK", "Danish Kroner (DKK)");
    select.addItem("EUR", "European Currency (EUR)");
    //select.addItem("FIM", "Finnish Markka (FIM)");
    //select.addItem("FRF", "French Francs (FRF)");
    //select.addItem("DEM", "German D'Marks (DEM)");
    //select.addItem("GRD", "Greek Drachma (GRD)");
    //select.addItem("HKD", "Hong Kong Dollars (HKD)");
    //select.addItem("INR", "Indian Rupees (INR)");
    //select.addItem("IEP", "Irish Pounds (IEP)");
    //select.addItem("ITL", "Italian Lira (ITL)");
    select.addItem("JPY", "Japanese Yen (JPY)");
    //select.addItem("KES", "Kenyan Shilling (KES)");
    //select.addItem("KWD", "Kuwaiti Dinars (KWD)");
    //select.addItem("MTL", "Maltese Lira (MTL)");
    //select.addItem("NLG", "Netherlands Guilder (NLG)");
    //select.addItem("NZD", "New Zealand Dollars (NZD)");
    //select.addItem("NOK", "Norwegian Krone (NOK)");
    //select.addItem("PGK", "P.N.G. Kina (PGK)");
    //select.addItem("PKR", "Pakistani Rupees (PKR)");
    //select.addItem("PHP", "Philippine Pesos (PHP)");
    //select.addItem("PTE", "Portugese Escudos (PTE)");
    //select.addItem("SAR", "Saudi Arabian Riyals (SAR)");
    //select.addItem("SGD", "Singapore Dollars (SGD)");
    //select.addItem("ESP", "Spanish Pesetas (ESP)");
    //select.addItem("LKR", "Sri Lanka Rupees (LKR)");
    //select.addItem("ZAR", "South African Rand (ZAR)");
    //select.addItem("SEK", "Swedish Krona (SEK)");
    //select.addItem("CHF", "Swiss Francs (CHF)");
    //select.addItem("THB", "Thai Bahts (THB)");
    //select.addItem("AED", "Uae Dirhams (AED)");
    select.addItem("GBP", "UK Pounds Sterling (GBP)");
    select.addItem("USD", "United States Dollar (USD)");
    //select.addItem("WST", "Western Samoa Tala (WST)");
    if (!select.hasKey(defaultValue)) {
      select.addItem(defaultValue);
    }
    return select;
  }
}

