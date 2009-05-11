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
 * Generates an HtmlSelect for choosing a percent range, for an opportunity
 * report
 *
 * @author matt rajkowski
 * @created October 6, 2003
 */
public class HtmlSelectProbabilityRange {

  /**
   * Gets the select attribute of the HtmlSelectProbabilityRange class
   *
   * @param name         Description of the Parameter
   * @param defaultValue Description of the Parameter
   * @return The select value
   */
  public static HtmlSelect getSelect(String name, String defaultValue) {
    HtmlSelect select = new HtmlSelect();
    select.setSelectName(name);
    select.setDefaultValue(defaultValue);
    populateSelect(select);
    return select;
  }


  /**
   * Gets the valueFromId attribute of the HtmlSelectProbabilityRange class
   *
   * @param key Description of the Parameter
   * @return The valueFromId value
   */
  public static String getValueFromId(String key) {
    HtmlSelect select = new HtmlSelect();
    populateSelect(select);
    return select.getValueFromId(key);
  }


  /**
   * Generates a list of choices for selecting a range of probabilities
   *
   * @param select Description of the Parameter
   */
  public static void populateSelect(HtmlSelect select) {
    select.addItem("-0.01|1.01", "All");
    select.addItem("0.1|1.01", "> 10%");
    select.addItem("0.2|1.01", "> 20%");
    select.addItem("0.3|1.01", "> 30%");
    select.addItem("0.4|1.01", "> 40%");
    select.addItem("0.5|1.01", "> 50%");
    select.addItem("0.6|1.01", "> 60%");
    select.addItem("0.7|1.01", "> 70%");
    select.addItem("0.8|1.01", "> 80%");
    select.addItem("0.9|1.01", "> 90%");
    select.addItem("-.01|0.9", "< 90%");
    select.addItem("-.01|0.8", "< 80%");
    select.addItem("-.01|0.7", "< 70%");
    select.addItem("-.01|0.6", "< 60%");
    select.addItem("-.01|0.5", "< 50%");
    select.addItem("-.01|0.4", "< 40%");
    select.addItem("-.01|0.3", "< 30%");
    select.addItem("-.01|0.2", "< 20%");
    select.addItem("-.01|0.1", "< 10%");
  }
}

