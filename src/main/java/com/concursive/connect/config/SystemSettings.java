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

package com.concursive.connect.config;

import com.concursive.commons.web.mvc.beans.GenericBean;
import com.concursive.commons.xml.XMLUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Handles storing and retrieving installation specific properties
 *
 * @author matt rajkowski
 * @created February 28, 2008
 */
public class SystemSettings extends GenericBean {

  private static Log LOG = LogFactory.getLog(SystemSettings.class);

  private ArrayList<String> hiddenList = new ArrayList<String>();

  public ArrayList<String> getHiddenList() {
    return hiddenList;
  }

  public void setHiddenList(ArrayList<String> hiddenList) {
    this.hiddenList = hiddenList;
  }

  public void initialize(Element documentElement) {
    hiddenList = new ArrayList<String>();
    parse(documentElement);
  }

  public boolean parse(Element element) {
    if (element == null) {
      return false;
    }
    try {
      //Process all hooks and the corresponding actions
      Element hiddenElements = XMLUtils.getFirstElement(element, "hiddenFields");
      // Make sure this section is enabled...
      String hiddenElementsEnabled = hiddenElements.getAttribute("enabled");
      if (hiddenElementsEnabled != null && "false".equals(hiddenElementsEnabled)) {
        return true;
      }
      // Add the hidden fields...
      List hookNodes = XMLUtils.getElements(hiddenElements, "field");
      Iterator hookElements = hookNodes.iterator();
      while (hookElements.hasNext()) {
        Element hookElement = (Element) hookElements.next();
        String fieldName = hookElement.getAttribute("name");
        hiddenList.add(fieldName);
      }
      LOG.info("Hidden field count: " + hiddenList.size());
    } catch (Exception e) {
      e.printStackTrace(System.out);
      return false;
    }
    return true;
  }


}
