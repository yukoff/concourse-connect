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
import com.concursive.commons.text.Template;
import com.concursive.connect.Constants;
import com.concursive.connect.config.ApplicationPrefs;
import com.concursive.connect.web.modules.login.dao.User;

import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.jsp.tagext.TryCatchFinally;
import java.util.HashMap;
import java.util.StringTokenizer;

/**
 * This Class evaluates whether a custom label exists for the supplied label.
 *
 * @author Matt Rajkowski
 * @created February 25, 2002
 */
public class LabelHandler extends TagSupport implements TryCatchFinally {
  private String labelName = null;
  private HashMap<String, String> params = null;
  private boolean mainMenuItem = false;
  private boolean subMenuItem = false;

  public void doCatch(Throwable throwable) throws Throwable {
    // Required but not needed
  }

  public void doFinally() {
    // Reset each property or else the value gets reused
    labelName = null;
    params = null;
    mainMenuItem = false;
    subMenuItem = false;
  }

  /**
   * Sets the Name attribute of the LabelHandler object
   *
   * @param tmp The new Name value
   * @since 1.1
   */
  public final void setName(String tmp) {
    labelName = tmp;
  }

  public final void setMainMenuItem(boolean tmp) {
    mainMenuItem = tmp;
  }

  public final void setSubMenuItem(boolean tmp) {
    subMenuItem = tmp;
  }


  /**
   * Sets the param attribute of the LabelHandler object
   *
   * @param tmp The new params value
   */
  public void setParam(String tmp) {
    params = new HashMap<String, String>();
    StringTokenizer tokens = new StringTokenizer(tmp, "|");
    while (tokens.hasMoreTokens()) {
      String pair = tokens.nextToken();
      String param = pair.substring(0, pair.indexOf("="));
      String value = pair.substring(pair.indexOf("=") + 1);
      params.put("${" + param + "}", value);
    }
  }


  /**
   * Checks to see if the SystemStatus has a preference set for this label. If
   * so, the found label will be used, otherwise the body tag will be used.
   *
   * @return Description of the Returned Value
   * @since 1.1
   */
  public final int doStartTag() {
    String newLabel = null;

    final ApplicationPrefs prefs =
        (ApplicationPrefs) pageContext.getServletContext().getAttribute(
            "applicationPrefs");
    final User user =
        (User) pageContext.getSession().getAttribute(Constants.SESSION_USER);
    final String language = user.getLanguage();
    if (null == language) {
      if (prefs != null) {
        newLabel = prefs.getLabel(labelName,
            prefs.get(ApplicationPrefs.LANGUAGE));
      }
    } else {
      // Look up the label key in system status to get the value
      if (mainMenuItem) {
        newLabel = prefs.getLabel("system.modules.label",
            labelName,
            language);
      } else if (subMenuItem) {
        newLabel = prefs.getLabel("system.submenu.label",
            labelName,
            language);
      } else {
        newLabel = prefs.getLabel(labelName, language);
      }
    }

    // If there are any parameters to substitute then do so
    if (newLabel != null && params != null && params.size() > 0) {
      Template labelText = new Template(newLabel);
      labelText.setParseElements(params);
      newLabel = labelText.getParsedText();
    }
    // Output the label value, else output the body of the tag
    if (newLabel != null) {
      try {
        this.pageContext.getOut().write(StringUtils.toHtmlChars(
            newLabel));
        return SKIP_BODY;
      } catch (java.io.IOException e) {
        //Nowhere to output
      }
    }
    return EVAL_BODY_INCLUDE;
  }
}
