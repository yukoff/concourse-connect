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
import com.concursive.connect.web.modules.common.social.rating.utils.RatingUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * This Class formats the specified amount with the specified currency
 *
 * @author matt rajkowski
 * @version $Id$
 * @created July 29, 2006
 */
public class RatingHandler extends TagSupport {

  private String id = null;
  private int count = -1;
  private int value = -1;
  private String url = null;
  private int vote = 0;
  private boolean showText = true;
  private String field = null;

  public void setId(String id) {
    this.id = id;
  }

  public void setId(int id) {
    this.id = String.valueOf(id);
  }

  public void setCount(int count) {
    this.count = count;
  }

  public void setValue(int value) {
    this.value = value;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public void setVote(int vote) {
    this.vote = vote;
  }

  public void setShowText(boolean showText) {
    this.showText = showText;
  }

  /**
   * @return the field
   */
  public String getField() {
    return field;
  }

  /**
   * @param field the field to set
   */
  public void setField(String field) {
    this.field = field;
  }

  public int doStartTag() throws JspException {
    try {
      String ctx = ((HttpServletRequest) pageContext.getRequest()).getContextPath();
      double width = RatingUtils.getImageWidth(count, value);
      if (showText) {
        this.pageContext.getOut().write("<table class=\"star-table\"><tr><td nowrap>");
      }
      if (StringUtils.hasText(url) || StringUtils.hasText(field)) {
        this.pageContext.getOut().write("<div id=\"rating_" + id + "\" class=\"star-div\">");
      }
      if (StringUtils.hasText(field)) {
        this.pageContext.getOut().write("<input type=\"hidden\" id=\"" + field + "\" name=\"" + field + "\" value=\"" + value + "\" />");
      }
      this.pageContext.getOut().write("<ul class=\"star-rating\">");
      this.pageContext.getOut().write("<li class=\"current-rating\" id=\"current-rating\" style=\"width: " + (int) width + "px; background: url(" + ctx + "/images/star_rating/stars16.png) left center !important; left: 0 !important; margin: 0 !important\"></li>");
      if (StringUtils.hasText(field) && StringUtils.hasText(url)) {
        url += "&ratingShowText=" + (showText ? "true" : "false");
        this.pageContext.getOut().write("<li><a href=\"javascript:document.getElementById('" + field + "').value='1';callURL('" + StringUtils.replace(url, "{vote}", "1") + "');\" title=\"1 star out of 5\" class=\"one-star\"></a></li>");
        this.pageContext.getOut().write("<li><a href=\"javascript:document.getElementById('" + field + "').value='2';callURL('" + StringUtils.replace(url, "{vote}", "2") + "');\" title=\"2 stars out of 5\" class=\"two-stars\"></a></li>");
        this.pageContext.getOut().write("<li><a href=\"javascript:document.getElementById('" + field + "').value='3';callURL('" + StringUtils.replace(url, "{vote}", "3") + "');\" title=\"3 stars out of 5\" class=\"three-stars\"></a></li>");
        this.pageContext.getOut().write("<li><a href=\"javascript:document.getElementById('" + field + "').value='4';callURL('" + StringUtils.replace(url, "{vote}", "4") + "');\" title=\"4 stars out of 5\" class=\"four-stars\"></a></li>");
        this.pageContext.getOut().write("<li><a href=\"javascript:document.getElementById('" + field + "').value='5';callURL('" + StringUtils.replace(url, "{vote}", "5") + "');\" title=\"5 stars out of 5\" class=\"five-stars\"></a></li>");
      } else if (StringUtils.hasText(url)) {
        url += "&ratingShowText=" + (showText ? "true" : "false");
        this.pageContext.getOut().write("<li><a href=\"javascript:callURL('" + StringUtils.replace(url, "{vote}", "1") + "');\" title=\"1 star out of 5\" class=\"one-star\"></a></li>");
        this.pageContext.getOut().write("<li><a href=\"javascript:callURL('" + StringUtils.replace(url, "{vote}", "2") + "');\" title=\"2 stars out of 5\" class=\"two-stars\"></a></li>");
        this.pageContext.getOut().write("<li><a href=\"javascript:callURL('" + StringUtils.replace(url, "{vote}", "3") + "');\" title=\"3 stars out of 5\" class=\"three-stars\"></a></li>");
        this.pageContext.getOut().write("<li><a href=\"javascript:callURL('" + StringUtils.replace(url, "{vote}", "4") + "');\" title=\"4 stars out of 5\" class=\"four-stars\"></a></li>");
        this.pageContext.getOut().write("<li><a href=\"javascript:callURL('" + StringUtils.replace(url, "{vote}", "5") + "');\" title=\"5 stars out of 5\" class=\"five-stars\"></a></li>");
      }


      this.pageContext.getOut().write("</ul>");
      if (StringUtils.hasText(url) || StringUtils.hasText(field)) {
        this.pageContext.getOut().write("</div>");
      }
      if (showText) {
        this.pageContext.getOut().write("</td>");
        this.pageContext.getOut().write("<td nowrap><div id=\"ratingCount_" + id + "\" class=\"star-count\">(" + count + ")</div></td>");
        this.pageContext.getOut().write("</tr></table>");
      }
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println(e.getMessage());
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
