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

package com.concursive.connect.web.modules.wiki.dao;

import com.concursive.commons.db.DatabaseUtils;
import com.concursive.commons.text.StringUtils;
import com.concursive.connect.web.utils.LookupElement;
import com.concursive.connect.web.utils.LookupList;

import java.util.StringTokenizer;

/**
 * Represents a form with custom fields
 *
 * @author matt rajkowski
 * @created Jun 2, 2008
 */
public class CustomFormField {
  // Types of custom fields
  public final static int TEXT = 1;
  public final static int SELECT = 2;
  public final static int TEXTAREA = 3;
  public final static int CHECKBOX = 4;
  public final static int LINK = 7;
  public final static int INTEGER = 9;
  public final static int FLOAT = 10;
  public final static int PERCENT = 11;
  public final static int CURRENCY = 12;
  public final static int EMAIL = 13;
  public final static int URL = 14;
  public final static int PHONE = 15;
  public final static int CALENDAR = 16;
  public final static int IMAGE = 17;

  public final static String TEXT_VALUE = "text";
  public final static String SELECT_VALUE = "select";
  public final static String TEXTAREA_VALUE = "textarea";
  public final static String CHECKBOX_VALUE = "checkbox";
  public final static String LINK_VALUE = "link";
  public final static String INTEGER_VALUE = "number";
  public final static String FLOAT_VALUE = "decimal";
  public final static String PERCENT_VALUE = "percent";
  public final static String CURRENCY_VALUE = "currency";
  public final static String EMAIL_VALUE = "email";
  public final static String URL_VALUE = "url";
  public final static String PHONE_VALUE = "phone";
  public final static String CALENDAR_VALUE = "calendar";
  public final static String IMAGE_VALUE = "image";


  // Properties that define a field
  private String label = null;
  private boolean labelDisplay = true;
  private String name = null;
  private int type = -1;
  private boolean required = false;
  private String additionalText = null;
  private String defaultValue = null;
  // Type dependent field properties
  private int size = -1;
  private int maxLength = -1;
  private int columns = -1;
  private int rows = -1;
  private String options = null;
  private String currency = null;
  // The entered values
  private String value = null;
  private String valueCurrency = null;

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public boolean getLabelDisplay() {
    return labelDisplay;
  }

  public void setLabelDisplay(boolean labelDisplay) {
    this.labelDisplay = labelDisplay;
  }

  public void setLabelDisplay(String display) {
    if (display != null) {
      this.labelDisplay = DatabaseUtils.parseBoolean(display);
    }
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getType() {
    return type;
  }

  public String getTypeAsString() {
    switch (type) {
      case TEXT:
        return TEXT_VALUE;
      case SELECT:
        return SELECT_VALUE;
      case CHECKBOX:
        return CHECKBOX_VALUE;
      case TEXTAREA:
        return TEXTAREA_VALUE;
      case LINK:
        return LINK_VALUE;
      case URL:
        return URL_VALUE;
      case CALENDAR:
        return CALENDAR_VALUE;
      case IMAGE:
        return IMAGE_VALUE;
      case INTEGER:
        return INTEGER_VALUE;
      case FLOAT:
        return FLOAT_VALUE;
      case PERCENT:
        return PERCENT_VALUE;
      case CURRENCY:
        return CURRENCY_VALUE;
      case EMAIL:
        return EMAIL_VALUE;
      case PHONE:
        return PHONE_VALUE;
      default:
        return null;
    }
  }

  public void setType(int type) {
    this.type = type;
  }

  public void setType(String tmp) {
    try {
      this.type = Integer.parseInt(tmp);
    } catch (Exception e) {
      if (tmp.equalsIgnoreCase(TEXT_VALUE)) {
        this.type = TEXT;
      } else if (tmp.equalsIgnoreCase(SELECT_VALUE)) {
        this.type = SELECT;
      } else if (tmp.equalsIgnoreCase(CHECKBOX_VALUE)) {
        this.type = CHECKBOX;
      } else if (tmp.equalsIgnoreCase(TEXTAREA_VALUE)) {
        this.type = TEXTAREA;
      } else if (tmp.equalsIgnoreCase(LINK_VALUE)) {
        this.type = LINK;
      } else if (tmp.equalsIgnoreCase(URL_VALUE)) {
        this.type = URL;
      } else if (tmp.equalsIgnoreCase(CALENDAR_VALUE)) {
        this.type = CALENDAR;
      } else if (tmp.equalsIgnoreCase(IMAGE_VALUE)) {
        this.type = IMAGE;
      } else if (tmp.equalsIgnoreCase(INTEGER_VALUE)) {
        this.type = INTEGER;
      } else if (tmp.equalsIgnoreCase(FLOAT_VALUE)) {
        this.type = FLOAT;
      } else if (tmp.equalsIgnoreCase(PERCENT_VALUE)) {
        this.type = PERCENT;
      } else if (tmp.equalsIgnoreCase(CURRENCY_VALUE)) {
        this.type = CURRENCY;
      } else if (tmp.equalsIgnoreCase(EMAIL_VALUE)) {
        this.type = EMAIL;
      } else if (tmp.equalsIgnoreCase(PHONE_VALUE)) {
        this.type = PHONE;
      }
    }
  }

  public boolean getRequired() {
    return required;
  }

  public void setRequired(boolean required) {
    this.required = required;
  }

  public void setRequired(String tmp) {
    this.required = DatabaseUtils.parseBoolean(tmp);
  }

  public String getAdditionalText() {
    return additionalText;
  }

  public void setAdditionalText(String additionalText) {
    this.additionalText = additionalText;
  }

  public String getDefaultValue() {
    return defaultValue;
  }

  public void setDefaultValue(String defaultValue) {
    this.defaultValue = defaultValue;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String tmp) {
    value = tmp;
  }

  public boolean hasValue() {
    if (type == SELECT) {
      return (!"-- None --".equals(value));
    } else if (type == CHECKBOX) {
      return DatabaseUtils.parseBoolean(value);
    }
    return StringUtils.hasText(value);
  }

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
  }

  public void setSize(String tmp) {
    size = DatabaseUtils.parseInt(tmp, -1);
  }

  public int getMaxLength() {
    return maxLength;
  }

  public void setMaxLength(int maxLength) {
    this.maxLength = maxLength;
  }

  public void setMaxLength(String tmp) {
    maxLength = DatabaseUtils.parseInt(tmp, -1);
  }

  public int getColumns() {
    return columns;
  }

  public void setColumns(int columns) {
    this.columns = columns;
  }

  public void setColumns(String tmp) {
    this.columns = DatabaseUtils.parseInt(tmp, -1);
  }

  public int getRows() {
    return rows;
  }

  public void setRows(int rows) {
    this.rows = rows;
  }

  public void setRows(String tmp) {
    this.rows = DatabaseUtils.parseInt(tmp, -1);
  }

  public String getOptions() {
    return options;
  }

  public void setOptions(String options) {
    this.options = options;
  }

  public LookupList getLookupList() {
    LookupList elementData = new LookupList();
    int count = 0;
    StringTokenizer st = new StringTokenizer(options, "|");
    while (st.hasMoreTokens()) {
      String listField = st.nextToken();
      if (!listField.trim().equals("")) {
        ++count;
        LookupElement thisElement = new LookupElement();
        thisElement.setDescription(listField.trim());
        thisElement.setCode(count);
        thisElement.setLevel(count);
        thisElement.setDefaultItem(false);
        elementData.add(thisElement);
      }
    }
    return elementData;
  }

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public String getValueCurrency() {
    return valueCurrency;
  }

  public void setValueCurrency(String valueCurrency) {
    this.valueCurrency = valueCurrency;
  }
}