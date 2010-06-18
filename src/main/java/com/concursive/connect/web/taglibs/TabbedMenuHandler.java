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
package com.concursive.connect.web.taglibs;

import com.concursive.commons.text.StringUtils;
import com.concursive.connect.Constants;
import com.concursive.connect.config.ApplicationPrefs;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.profile.dao.Project;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import java.util.StringTokenizer;

/**
 * Description of the Class
 *
 * @author matt rajkowski
 * @version $Id$
 * @created June 15, 2003
 */
public class TabbedMenuHandler extends TagSupport {

  private String text;
  private String url;
  private String key;
  private String value;
  private String type;
  private String object;

  public void release() {
    // Reset each property or else the value gets reused
    text = null;
    url = null;
    key = null;
    value = null;
    type = null;
    object = null;
    super.release();
  }

  /**
   * Sets the text attribute of the TabbedMenuHandler object
   *
   * @param tmp The new text value
   */
  public void setText(String tmp) {
    this.text = tmp;
  }

  /**
   * Sets the url attribute of the TabbedMenuHandler object
   *
   * @param tmp The new url value
   */
  public void setUrl(String tmp) {
    this.url = tmp;
  }

  /**
   * Sets the key attribute of the TabbedMenuHandler object
   *
   * @param tmp The new key value
   */
  public void setKey(String tmp) {
    this.key = tmp.toLowerCase();
  }

  /**
   * Sets the value attribute of the TabbedMenuHandler object
   *
   * @param tmp The new value value
   */
  public void setValue(String tmp) {
    this.value = tmp.toLowerCase();
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public void setObject(String object) {
    this.object = object;
  }

  /**
   * Description of the Method
   *
   * @return Description of the Return Value
   * @throws JspException Description of the Exception
   */
  public int doStartTag() throws JspException {
    JspWriter out = this.pageContext.getOut();
    boolean selected = false;
    if (key.indexOf(",") > -1) {
      StringTokenizer st = new StringTokenizer(key, ",");
      while (st.hasMoreTokens()) {
        if (value.toLowerCase().startsWith(st.nextToken().toLowerCase())) {
          selected = true;
          break;
        }
      }
    } else if (value.toLowerCase().startsWith(key.toLowerCase())) {
      selected = true;
    }
    final ApplicationPrefs prefs = (ApplicationPrefs) pageContext.getServletContext().getAttribute("applicationPrefs");
    final User user = (User) pageContext.getSession().getAttribute(Constants.SESSION_USER);
    final String language = (null != user.getLanguage())
        ? user.getLanguage()
        : prefs.get(ApplicationPrefs.LANGUAGE);
    final String newText = StringUtils.toHtml(StringUtils.getText(prefs.getLabel("tabbedMenu.tab." + text, language), text));
    // use the project title for the href title
    String title = newText;
    Project project = null;
    if (object != null) {
      // Use the specified request object
      project = (Project) pageContext.getRequest().getAttribute(object);
    } else {
      // Use the default project profile object
      project = (Project) pageContext.getRequest().getAttribute("project");
    }
    if (project != null) {
      title = StringUtils.toHtml(project.getTitle()) + " " + title;
    }
    // output the tab
    try {
      if ("yui".equals(type)) {
        // Render as JavaScript YUI TabView
        String active = (selected ? ", active: true" : "");
        out.write("tabView.addTab(new YAHOO.widget.Tab({" +
            "label: '" + newText + "', " +
            "dataSrc: '" + url + "&out=text', " +
            "cacheData: true" + active + "}));");
      } else if ("li".equals(type)) {
        // Render as list items
        out.write("<li>" +
            "<a " +
            (StringUtils.hasText(key) ? "id=\"ccp-tab-" + key + "\" " : "") +
            "href=\"" + url + "\" " +
            "title=\"" + title + "\"" +
            (selected ? " class=\"active\"" : "") + ">" +
            "<span class=\"projectCenterList_tl\"></span><span class=\"projectCenterList_tr\"></span><em>" + newText + "</em><span class=\"projectCenterList_bl\"></span><span class=\"projectCenterList_br\"></span></a></li>");
      } else {
        // Render as table cells
        String tag = (selected ? "th" : "td");
        out.write("<" + tag + " nowrap>");
        out.write("<a href=\"" + url + "\" title=\"" + title + "\">" + newText + "</a>");
        out.write("</" + tag + ">");
      }
    } catch (Exception e) {
    }
    return SKIP_BODY;
  }

  /**
   * Description of the Method
   *
   * @return Description of the Return Value
   */
  public int doEndTag() {
    return EVAL_PAGE;
  }
}

