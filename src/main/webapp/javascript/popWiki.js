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
 * under the GNU Affero General Public License. Ê
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

/**
 * Displays the wikis for this project to select from
 * @arg1 = form name of the object being modified
 * @arg2 = element name of the entry field
 */
function popWiki(formname, element, pid) {
  var theLink = escape(eval("document." + formname + "." + element + ".value"));
  // NOTE: global teamelements_ctx variable must exist
  var filename = (teamelements_ctx + '/ProjectManagementWiki.do?command=Selector&popup=true&form=' + formname + '&element=' + element + '&pid=' + pid + '&link=' + theLink);
  var posx = 0;
  var posy = 0;
  posx = (screen.width - 690)/2;
  posy = (screen.height - 600)/2;
  var newwin=window.open(filename, 'wiki_selector', 'WIDTH=700,HEIGHT=600,RESIZABLE=yes,SCROLLBARS=yes,STATUS=0,LEFT=' + posx + ',TOP=' + posy + ',screenx=' + posx + ',screeny=' + posy);
  newwin.focus();
  if (newwin != null) {
    if (newwin.opener == null)
      newwin.opener = self;
  }
}
