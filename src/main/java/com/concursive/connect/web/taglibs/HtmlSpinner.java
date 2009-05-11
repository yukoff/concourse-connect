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

import com.concursive.commons.http.RequestUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * Displays an HTML widget called a Spinner, basically a text field and arrows
 * to increase and decrease a value
 *
 * @author matt rajkowski
 * @version $Id$
 * @created July 23, 2003
 */
public class HtmlSpinner extends TagSupport {

  private String name = null;
  private String value = "0";
  private int min = -1;
  private int max = -1;


  /**
   * Sets the name attribute of the HtmlSpinner object
   *
   * @param tmp The new name value
   */
  public void setName(String tmp) {
    this.name = tmp;
  }


  /**
   * Sets the value attribute of the HtmlSpinner object
   *
   * @param tmp The new value value
   */
  public void setValue(String tmp) {
    this.value = tmp;
  }


  /**
   * Sets the value attribute of the HtmlSpinner object
   *
   * @param tmp The new value value
   */
  public void setValue(int tmp) {
    this.value = String.valueOf(tmp);
  }


  /**
   * Sets the min attribute of the HtmlSpinner object
   *
   * @param tmp The new min value
   */
  public void setMin(int tmp) {
    this.min = tmp;
  }


  /**
   * Sets the min attribute of the HtmlSpinner object
   *
   * @param tmp The new min value
   */
  public void setMin(String tmp) {
    this.min = Integer.parseInt(tmp);
  }


  /**
   * Sets the max attribute of the HtmlSpinner object
   *
   * @param tmp The new max value
   */
  public void setMax(int tmp) {
    this.max = tmp;
  }


  /**
   * Sets the max attribute of the HtmlSpinner object
   *
   * @param tmp The new max value
   */
  public void setMax(String tmp) {
    this.max = Integer.parseInt(tmp);
  }


  /**
   * Description of the Method
   *
   * @return Description of the Return Value
   * @throws JspException Description of the Exception
   */
  public int doStartTag() throws JspException {
    try {
      this.pageContext.getOut().write(
          "<script language=\"JavaScript\" type=\"text/javascript\">var spinMin=" + min + "; var spinMax=" + max + ";</script>\n");
      this.pageContext.getOut().write(
          "<script language=\"JavaScript\" type=\"text/javascript\" src=\"" + RequestUtils.getAbsoluteServerUrl((HttpServletRequest) pageContext.getRequest()) + "/javascript/spinner.js\"></script>\n");
      this.pageContext.getOut().write(
          "<input type=\"text\" name=\"" + name + "\" id=\"" + name + "\" value=\"" + value + "\" size=\"4\" maxlength=\"" + String.valueOf(max).length() + "\"> ");
      this.pageContext.getOut().write(
          "<a href=\"javascript:spinLeft('" + name + "');\">" +
              "<img alt=\"Unindent\" src=\"" + RequestUtils.getAbsoluteServerUrl((HttpServletRequest) pageContext.getRequest()) + "/images/icons/stock_left-16.gif\" border=\"0\" align=\"absmiddle\" height=\"16\" width=\"16\"/>" +
              "</a>");
      this.pageContext.getOut().write(
          "<a href=\"javascript:spinRight('" + name + "');\">" +
              "<img alt=\"Indent\" src=\"" + RequestUtils.getAbsoluteServerUrl((HttpServletRequest) pageContext.getRequest()) + "/images/icons/stock_right-16.gif\" border=\"0\" align=\"absmiddle\" height=\"16\" width=\"16\"/>" +
              "</a>\n");
    } catch (Exception e) {
      throw new JspException("HtmlSelectTime Error: " + e.getMessage());
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

