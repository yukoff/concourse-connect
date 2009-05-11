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
 * Description of the Class
 *
 * @author matt rajkowski
 * @version $Id$
 * @created January 15, 2003
 */
public class StateSelect extends HtmlSelect {

  /**
   * Constructor for the StateSelect object
   */
  public StateSelect() {
    this.addItem(-1, "--None--");
    addStates();
  }


  public void addStatesForCanada() {
    this.addItem("BC", "British Columbia");
    this.addItem("MB", "Manitoba");
    this.addItem("NF", "Newfoundland");
    this.addItem("NB", "New Brunswick");
    this.addItem("NT", "Northwest Territories");
    this.addItem("NS", "Nova Scotia");
    this.addItem("ON", "Ontario");
    this.addItem("PE", "Prince Edward Island");
    this.addItem("QC", "Quebec");
    this.addItem("SK", "Saskatchewan");
  }

  /**
   * Adds a feature to the States attribute of the StateSelect object
   */
  public void addStatesForUnitedStates() {
    this.addItem("AL", "Alabama");
    this.addItem("AK", "Alaska");
    this.addItem("AB", "Alberta");
    this.addItem("AS", "American Samoa");
    this.addItem("AZ", "Arizona");
    this.addItem("AR", "Arkansas");
    this.addItem("CA", "California");
    this.addItem("CO", "Colorado");
    this.addItem("CT", "Connecticut");
    this.addItem("DE", "Delaware");
    this.addItem("DC", "District of Columbia");
    this.addItem("FM", "Federated States of Micronesia");
    this.addItem("FL", "Florida");
    this.addItem("GA", "Georgia");
    this.addItem("GU", "Guam");
    this.addItem("HI", "Hawaii");
    this.addItem("ID", "Idaho");
    this.addItem("IL", "Illinois");
    this.addItem("IN", "Indiana");
    this.addItem("IA", "Iowa");
    this.addItem("KS", "Kansas");
    this.addItem("KY", "Kentucky");
    this.addItem("LA", "Louisiana");
    this.addItem("ME", "Maine");
    this.addItem("MH", "Marshall Islands");
    this.addItem("MD", "Maryland");
    this.addItem("MA", "Massachusetts");
    this.addItem("MI", "Michigan");
    this.addItem("MN", "Minnesota");
    this.addItem("MS", "Mississippi");
    this.addItem("MO", "Missouri");
    this.addItem("MT", "Montana");
    this.addItem("NE", "Nebraska");
    this.addItem("NV", "Nevada");
    this.addItem("NH", "New Hampshire");
    this.addItem("NJ", "New Jersey");
    this.addItem("NM", "New Mexico");
    this.addItem("NY", "New York");
    this.addItem("NC", "North Carolina");
    this.addItem("ND", "North Dakota");
    this.addItem("MP", "Northern Mariana Islands");
    this.addItem("OH", "Ohio");
    this.addItem("OK", "Oklahoma");
    this.addItem("OR", "Oregon");
    this.addItem("PW", "Palau");
    this.addItem("PA", "Pennsylvania");
    this.addItem("PR", "Puerto Rico");
    this.addItem("RI", "Rhode Island");
    this.addItem("SC", "South Carolina");
    this.addItem("SD", "South Dakota");
    this.addItem("TN", "Tennessee");
    this.addItem("TX", "Texas");
    this.addItem("UT", "Utah");
    this.addItem("VT", "Vermont");
    this.addItem("VI", "Virgin Islands");
    this.addItem("VA", "Virginia");
    this.addItem("WA", "Washington");
    this.addItem("WV", "West Virginia");
    this.addItem("WI", "Wisconsin");
    this.addItem("WY", "Wyoming");
  }

  private void addStates() {
    this.addItem("AL", "Alabama");
    this.addItem("AK", "Alaska");
    this.addItem("AB", "Alberta");
    this.addItem("AS", "American Samoa");
    this.addItem("AZ", "Arizona");
    this.addItem("AR", "Arkansas");
    this.addItem("BC", "British Columbia");
    this.addItem("CA", "California");
    this.addItem("CO", "Colorado");
    this.addItem("CT", "Connecticut");
    this.addItem("DE", "Delaware");
    this.addItem("DC", "District of Columbia");
    this.addItem("FM", "Federated States of Micronesia");
    this.addItem("FL", "Florida");
    this.addItem("GA", "Georgia");
    this.addItem("GU", "Guam");
    this.addItem("HI", "Hawaii");
    this.addItem("ID", "Idaho");
    this.addItem("IL", "Illinois");
    this.addItem("IN", "Indiana");
    this.addItem("IA", "Iowa");
    this.addItem("KS", "Kansas");
    this.addItem("KY", "Kentucky");
    this.addItem("LA", "Louisiana");
    this.addItem("ME", "Maine");
    this.addItem("MB", "Manitoba");
    this.addItem("MH", "Marshall Islands");
    this.addItem("MD", "Maryland");
    this.addItem("MA", "Massachusetts");
    this.addItem("MI", "Michigan");
    this.addItem("MN", "Minnesota");
    this.addItem("MS", "Mississippi");
    this.addItem("MO", "Missouri");
    this.addItem("MT", "Montana");
    this.addItem("NE", "Nebraska");
    this.addItem("NV", "Nevada");
    this.addItem("NF", "Newfoundland");
    this.addItem("NB", "New Brunswick");
    this.addItem("NH", "New Hampshire");
    this.addItem("NJ", "New Jersey");
    this.addItem("NM", "New Mexico");
    this.addItem("NY", "New York");
    this.addItem("NC", "North Carolina");
    this.addItem("ND", "North Dakota");
    this.addItem("MP", "Northern Mariana Islands");
    this.addItem("NT", "Northwest Territories");
    this.addItem("NS", "Nova Scotia");
    this.addItem("OH", "Ohio");
    this.addItem("OK", "Oklahoma");
    this.addItem("ON", "Ontario");
    this.addItem("OR", "Oregon");
    this.addItem("PW", "Palau");
    this.addItem("PA", "Pennsylvania");
    this.addItem("PE", "Prince Edward Island");
    this.addItem("PR", "Puerto Rico");
    this.addItem("QC", "Quebec");
    this.addItem("RI", "Rhode Island");
    this.addItem("SK", "Saskatchewan");
    this.addItem("SC", "South Carolina");
    this.addItem("SD", "South Dakota");
    this.addItem("TN", "Tennessee");
    this.addItem("TX", "Texas");
    this.addItem("UT", "Utah");
    this.addItem("VT", "Vermont");
    this.addItem("VI", "Virgin Islands");
    this.addItem("VA", "Virginia");
    this.addItem("WA", "Washington");
    this.addItem("WV", "West Virginia");
    this.addItem("WI", "Wisconsin");
    this.addItem("WY", "Wyoming");
  }
}

