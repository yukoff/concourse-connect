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

/**
 * Presents an HTML language selection based on implemented Java languages
 *
 * @author matt rajkowski
 * @created March 18, 2004
 */
public class HtmlSelectLanguage {

  /**
   * Gets the select attribute of the HtmlSelectLanguage class
   *
   * @param name         Description of the Parameter
   * @param defaultValue Description of the Parameter
   * @return The select value
   */
  public static HtmlSelect getSelect(String name, String defaultValue) {
    if (defaultValue == null) {
      defaultValue = "en_US";
    }
    HtmlSelect select = new HtmlSelect();
    select.setSelectName(name);
    select.setDefaultValue(defaultValue);
    // Chinese - Simplified ????
    select.addItem("sq_AL", "Alban\u00e9s");
    select.addItem("zh_CN", "\u7b80\u4f53\u4e2d\u6587");
    select.addItem("zh_TW", "Chinese Traditional");
    select.addItem("cs_CZ", "\u0192\u00e7esky");
    // Croat (Croatian)
    select.addItem("hr_HR", "hrvatski (Hrvatska)");
    select.addItem("da_DK", "Dansk");
    // German
    select.addItem("de_DE", "Deutsch");
    //select.addItem("de_DE_EURO", "Deutsch (EURO)");
    select.addItem("en_AU", "English - AU");
    select.addItem("en_CA", "English - CA");
    select.addItem("en_IE", "English - EU");
    select.addItem("en_GB", "English - UK");
    select.addItem("en_US", "English - US");
    //Spanish
    select.addItem("es_VE", "Espa\u00f1ol (de America)");
    select.addItem("es_ES", "Espa\u00f1ol (de Espa\u00f1a)");
    select.addItem("fi_FI", "Finnish");
    //French
    select.addItem("fr_FR", "Fran\u00e7ais - FR");
    //select.addItem("fr_FR_EURO", StringUtils.toHtml("Fran\u00E7ais (EURO)"));
    select.addItem("fr_CA", "Fran\u00e7ais - CA");
    //Greek
    select.addItem("el_GR", "\u0395\u03bb\u03bb\u03b7\u03bd\u03b9\u03ba\u03ac");
    //Italian
    select.addItem("it_IT", "Italiano");
    //Japanese
    select.addItem("ja_JP", "Japanese");
    //Korean "ko" \uc548\ub155\ud558\uc138\uc694\uc138\uacc4
    select.addItem("lt_LT", "Lietuvi\u0161kai");
    select.addItem("nl_NL", "Nederlands");
    select.addItem("pl_PL", "Polski");
    select.addItem("pt_BR", "Portuguese - BR");
    select.addItem("ro_RO", "Romanian");
    select.addItem("ru_RU", "\u0420\u0443\u0441\u0441\u043a\u0438\u0439");
    select.addItem("sl_SI", "Slovene");
    select.addItem("sv_SE", "Svenska");
    select.addItem("th_TH", "\u0e20\u0e32\u0e29\u0e32\u0e44\u0e17\u0e22");
    select.addItem("tr_TR", "T\u00fcrk\u00e7e");
    return select;
  }
}
