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
 * Description
 *
 * @author wli
 * @version $Id: CompanySizeSelect.java
 * @created January 7, 2008
 */

public class CompanySizeSelect extends HtmlSelect {

  /**
   * Constructor for the CompanySizeSelect object
   */
  public CompanySizeSelect() {
    this.addItem(-1, "Please Select");
    addCompanySize();
  }


  /**
   * Constructor for the CompanySizeSelect object
   *
   * @param emptyItem Description of the Parameter
   */
  public CompanySizeSelect(String emptyItem) {
    this.addItem(-1, emptyItem);
    this.setDefaultValue(-1);
    addCompanySize();
  }

  /**
   * Adds a feature to the CompanySize attribute of the CompanySizeSelect object
   */
  private void addCompanySize() {
    this.addItem("0-25 Employees", "0-25 Employees");
    this.addItem("26-50 Employees", "26-50 Employees");
    this.addItem("51-100 Employees", "51-100 Employees");
    this.addItem("101-500 Employees", "101-500 Employees");
    this.addItem("500+ Employees", "500+ Employees");
  }
}
