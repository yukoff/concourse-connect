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

var currentCalendarForm, currentCalendarField;

function popCalendar(formname, element) {
  return popCalendar(formname, element, 'en', 'US');
}

function popCalendar(formname, element, language, country) {
  var Dom = YAHOO.util.Dom;

  // set global values for the calendar
  currentCalendarForm = formname;
  currentCalendarField = element;

  // Create localized calendar (requires values from css_include)
  var cal = new YAHOO.widget.Calendar("popupCalendar", { close:true, START_WEEKDAY: connect_startDayOfWeek } );
  cal.cfg.setProperty("DATE_FIELD_DELIMITER", connect_dateFieldDelimiter);
  cal.cfg.setProperty("MDY_DAY_POSITION", connect_dateDayPosition);
  cal.cfg.setProperty("MDY_MONTH_POSITION", connect_dateMonthPosition);
  cal.cfg.setProperty("MDY_YEAR_POSITION", connect_dateYearPosition);
  cal.cfg.setProperty("MD_DAY_POSITION", connect_dateDayPosition);
  cal.cfg.setProperty("MD_MONTH_POSITION", connect_dateMonthPosition);

  // Read any existing date value
  var date = eval("document." + formname + "." + element + ".value");

  if (date) {
    var d = date.split(connect_dateFieldDelimiter);
    var year = d[connect_dateYearPosition-1];
    var month = d[connect_dateMonthPosition-1];
    var day = d[connect_dateDayPosition-1];

    cal.cfg.setProperty('selected', date);
    cal.cfg.setProperty('pagedate', new Date(year, month - 1, day), true);
  } else {
    cal.cfg.setProperty('selected', '');
    // @todo fix for locale
    cal.cfg.setProperty('pagedate', new Date(), true);
  }

  // Handler for when a date is selected
	cal.selectEvent.subscribe(setCalendarValue, cal, true);

  // Make sure the calendar closes if any panel is closed
  if (panel) {
    panel.cancelEvent.subscribe(function() { cal.hide(); });
  }

  // Show the calendar
  cal.render();
  Dom.setStyle('popupCalendar', 'display', 'block');
  Dom.setStyle('popupCalendar', 'z-index', '999');

  // Determine the onscreen position
  var el = document.getElementById(element);
  el.focus();
  var xy = Dom.getXY(el)
  var offset = 20;

  // Initial position of the calendar
  xy[1] = xy[1] + offset;
  Dom.setXY('popupCalendar', xy);

  // calendar top
  var elRegion = Dom.getRegion(el);
  var elTop = elRegion.top + offset;

  // calendar height
  var calEl = Dom.get("popupCalendar")
  var calRegion = Dom.getRegion(calEl);
  var calHeight = calRegion.bottom - calRegion.top;

  // scroll amount
  var posy = 0;
  if (window.scrollY) {
    posy = window.scrollY;
  } else {
    var rootd = null;
    if (document.documentElement && document.documentElement.scrollTop) {
      rootd = document.documentElement;
    } else {
      rootd = document.body;
    }
    posy = rootd.scrollTop;
  }

  // viewport height
  var viewh = Dom.getViewportHeight();
  
  // Determine the offscreen amount
  var padding = 5;
  var diff = (elTop + calHeight + padding) - (posy + viewh);
  if (diff > 0) {
    xy[1] = xy[1] - diff;
    Dom.setXY('popupCalendar', xy);
  }
}

function setCalendarValue(type,args,obj) {
  var datedata = args[0][0];
  var year = datedata[0];
  var month = datedata[1];
  var day = datedata[2];

  // Set the value and close the calendar
  var field = eval("document." + currentCalendarForm + "." + currentCalendarField);
  if (connect_dateDayPosition < connect_dateMonthPosition) {
    field.value = day + connect_dateFieldDelimiter + month + connect_dateFieldDelimiter + year;
  } else {
    field.value = month + connect_dateFieldDelimiter + day + connect_dateFieldDelimiter + year;
  }
  obj.hide();

  // Try a trigger if there is one
  try {
    calendarTrigger(currentCalendarField);
  } catch (ex) {
  
  }
}

function dateIsLaterThan(startDateEl, endDateEl) {
  var date1 = getFieldDate(startDateEl);
  var date2 = getFieldDate(endDateEl);
  if (date1 && date2) {
    return (YAHOO.widget.DateMath.after(date1, date2));
  } else if (date1) {
    return true;
  }
  return false;
}

function getFieldDate(el) {
  if (el) {
    var date = el.value;
    if (date) {
      var d = date.split(connect_dateFieldDelimiter);
      var year = d[connect_dateYearPosition-1];
      var month = d[connect_dateMonthPosition-1];
      var day = d[connect_dateDayPosition-1];
      return YAHOO.widget.DateMath.getDate(year,month-1,day);
    }
  }
  return null;
}