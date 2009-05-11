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

import java.util.HashMap;

/**
 * Represents a Html Option component of a combo box
 *
 * @author akhi_m
 * @version $id: exp$
 * @created June 9, 2003
 */
public class HtmlOption {

  String value = null;
  String text = null;
  String jsEvent = null;
  HashMap<String, String> attributeList = null;
  boolean group = false;
  boolean enabled = true;


  /**
   * Constructor for the HtmlOption object
   */
  public HtmlOption() {
  }


  /**
   * Constructor for the Option object
   *
   * @param value Description of the Parameter
   * @param text  Description of the Parameter
   */
  public HtmlOption(String value, String text) {
    this.value = value;
    this.text = text;
  }


  /**
   * Constructor for the HtmlOption object
   *
   * @param value   Description of the Parameter
   * @param text    Description of the Parameter
   * @param jsEvent Description of the Parameter
   */
  public HtmlOption(String value, String text, String jsEvent) {
    this.value = value;
    this.text = text;
    this.jsEvent = jsEvent;
  }


  /**
   * Constructor for the HtmlOption object
   *
   * @param value         Description of the Parameter
   * @param text          Description of the Parameter
   * @param attributeList Description of the Parameter
   */
  public HtmlOption(String value, String text, HashMap<String, String> attributeList) {
    this.value = value;
    this.text = text;
    this.attributeList = attributeList;
  }


  /**
   * Constructor for the HtmlOption object
   *
   * @param value         Description of the Parameter
   * @param text          Description of the Parameter
   * @param attributeList Description of the Parameter
   * @param enabled       Description of the Parameter
   */
  public HtmlOption(String value, String text, HashMap<String, String> attributeList, boolean enabled) {
    this.value = value;
    this.text = text;
    this.attributeList = attributeList;
    this.enabled = enabled;
  }


  /**
   * Sets the value attribute of the Option object
   *
   * @param value The new value value
   */
  public void setValue(String value) {
    this.value = value;
  }


  /**
   * Sets the jsEvent attribute of the Option object
   *
   * @param jsEvent The new jsEvent value
   */
  public void setJsEvent(String jsEvent) {
    this.jsEvent = jsEvent;
  }


  /**
   * Sets the text attribute of the HtmlOption object
   *
   * @param text The new text value
   */
  public void setText(String text) {
    this.text = text;
  }


  /**
   * Sets the attributeList attribute of the HtmlOption object
   *
   * @param attributeList The new attributeList value
   */
  public void setAttributeList(HashMap<String, String> attributeList) {
    this.attributeList = attributeList;
  }


  /**
   * Gets the attributeList attribute of the HtmlOption object
   *
   * @return The attributeList value
   */
  public HashMap getAttributeList() {
    return attributeList;
  }


  /**
   * Sets the enabled attribute of the HtmlOption object
   *
   * @param tmp The new enabled value
   */
  public void setEnabled(boolean tmp) {
    this.enabled = tmp;
  }


  /**
   * Gets the enabled attribute of the HtmlOption object
   *
   * @return The enabled value
   */
  public boolean getEnabled() {
    return enabled;
  }


  /**
   * Gets the text attribute of the HtmlOption object
   *
   * @return The text value
   */
  public String getText() {
    return text;
  }


  /**
   * Gets the value attribute of the Option object
   *
   * @return The value value
   */
  public String getValue() {
    return value;
  }


  /**
   * Gets the jsEvent attribute of the Option object
   *
   * @return The jsEvent value
   */
  public String getJsEvent() {
    return jsEvent;
  }


  /**
   * Sets the group attribute of the HtmlOption object
   *
   * @param tmp The new group value
   */
  public void setGroup(boolean tmp) {
    this.group = tmp;
  }


  /**
   * Gets the group attribute of the HtmlOption object
   *
   * @return The group value
   */
  public boolean getGroup() {
    return group;
  }


  /**
   * Gets the group attribute of the HtmlOption object
   *
   * @return The group value
   */
  public boolean isGroup() {
    return group;
  }


  /**
   * Adds a feature to the Attribute attribute of the Option object
   *
   * @param attrName  The feature to be added to the Attribute attribute
   * @param attrValue The feature to be added to the Attribute attribute
   */
  public void addAttribute(String attrName, String attrValue) {
    if (attributeList == null) {
      attributeList = new HashMap<String, String>();
    }
    attributeList.put(attrName, attrValue);
  }


  /**
   * Gets the attributeList attribute of the Option object
   *
   * @return The attributeList value
   */
  public String getAttributes() {
    StringBuffer tmpList = new StringBuffer();
    if (attributeList != null) {
      tmpList.append(" ");
      for (String name : attributeList.keySet()) {
        tmpList.append(name).append("='").append(attributeList.get(name)).append("' ");
      }
    }
    return tmpList.toString();
  }
}

