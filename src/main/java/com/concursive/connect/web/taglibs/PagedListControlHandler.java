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

import com.concursive.commons.db.DatabaseUtils;
import com.concursive.commons.text.Template;
import com.concursive.connect.web.utils.PagedListInfo;

import javax.portlet.PortletRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import java.util.HashMap;

/**
 * Provides a visual control panel (html form) that allows the user to jump to
 * another page, change the number of entries per page, etc.
 *
 * @author matt rajkowski
 * @version $Id: PagedListControlHandler.java,v 1.2 2002/08/06 21:03:07 akhi_m
 *          Exp $
 * @created June 12, 2002
 */
public class PagedListControlHandler extends TagSupport {
  private String name = "controlProperties";
  private String object = null;
  private String bgColor = null;
  private String fontColor = "#666666";
  private String tdClass = null;
  private boolean showForm = true;
  private boolean resetList = true;
  private boolean abbreviate = false;
  private boolean enableJScript = false;
  private String form = "0";

  public void release() {
    // Reset each property or else the value gets reused
    name = "controlProperties";
    object = null;
    bgColor = null;
    fontColor = "#666666";
    tdClass = null;
    showForm = true;
    resetList = true;
    abbreviate = false;
    enableJScript = false;
    form = "0";
    super.release();
  }


  /**
   * Sets the name attribute of the PagedListControlHandler object
   *
   * @param tmp The new name value
   */
  public final void setName(String tmp) {
    name = tmp;
  }

  /**
   * Sets the object attribute of the PagedListControlHandler object
   *
   * @param tmp The new object value
   */
  public final void setObject(String tmp) {
    object = tmp;
  }

  /**
   * Sets the bgColor attribute of the PagedListControlHandler object
   *
   * @param tmp The new bgColor value
   */
  public final void setBgColor(String tmp) {
    bgColor = tmp;
  }

  /**
   * Sets the fontColor attribute of the PagedListControlHandler object
   *
   * @param tmp The new fontColor value
   */
  public final void setFontColor(String tmp) {
    fontColor = tmp;
  }

  /**
   * Sets the tdClass attribute of the PagedListControlHandler object
   *
   * @param tmp The new tdClass value
   */
  public final void setTdClass(String tmp) {
    tdClass = tmp;
  }

  /**
   * Sets the showForm attribute of the PagedListControlHandler object
   *
   * @param showForm The new showForm value
   */

  public void setShowForm(String showForm) {
    this.showForm = "true".equalsIgnoreCase(showForm);
  }

  /**
   * Sets the resetList attribute of the PagedListControlHandler object
   *
   * @param resetList The new resetList value
   */
  public void setResetList(String resetList) {
    this.resetList = "true".equalsIgnoreCase(resetList);
  }

  /**
   * Gets the abbreviate attribute of the PagedListControlHandler object
   *
   * @return The abbreviate value
   */
  public boolean getAbbreviate() {
    return abbreviate;
  }

  /**
   * Sets the abbreviate attribute of the PagedListControlHandler object
   *
   * @param abbreviate The new abbreviate value
   */
  public void setAbbreviate(boolean abbreviate) {
    this.abbreviate = abbreviate;
  }

  /**
   * Sets the attribute of the PagedListControlHandler object
   *
   * @param enableJScript The new enableJScript value
   */
  public void setEnableJScript(boolean enableJScript) {
    this.enableJScript = enableJScript;
  }

  /**
   * Sets the enableJScript attribute of the PagedListControlHandler object
   *
   * @param tmp The new enableJScript value
   */
  public void setEnableJScript(String tmp) {
    this.enableJScript = DatabaseUtils.parseBoolean(tmp);
  }

  public void setForm(String form) {
    this.form = form;
  }

  /**
   * Description of the Method
   *
   * @return Description of the Returned Value
   */
  public final int doStartTag() {
    return EVAL_BODY_INCLUDE;
  }

  /**
   * Description of the Method
   *
   * @return Description of the Returned Value
   */
  public int doEndTag() {
    try {
      PortletRequest renderRequest = (PortletRequest) pageContext.getRequest().getAttribute(org.apache.pluto.tags.Constants.PORTLET_REQUEST);
      RenderResponse renderResponse = (RenderResponse) pageContext.getRequest().getAttribute(org.apache.pluto.tags.Constants.PORTLET_RESPONSE);
      PagedListInfo pagedListInfo = null;

      // Check the request first
      pagedListInfo = (PagedListInfo) pageContext.getRequest().getAttribute(object);

      // Check the portlet next
      if (pagedListInfo == null) {
        if (renderRequest != null) {
          pagedListInfo = (PagedListInfo) renderRequest.getAttribute(object);
          if (pagedListInfo == null) {
            pagedListInfo = (PagedListInfo) renderRequest.getPortletSession().getAttribute(object);
          }
        }
      }

      // Check the session last
      if (pagedListInfo == null) {
        pagedListInfo = (PagedListInfo) pageContext.getSession().getAttribute(object);
      }

      // Display the control
      if (pagedListInfo != null) {
        String ctx = ((HttpServletRequest) pageContext.getRequest()).getContextPath();

        pagedListInfo.setShowForm(showForm);
        pagedListInfo.setResetList(resetList);
        pagedListInfo.setEnableJScript(enableJScript);
        JspWriter out = this.pageContext.getOut();

        String batchHTML = (String) this.getValue("batchHTML");
        if (enableJScript) {
          out.write("<SCRIPT LANGUAGE=\"JavaScript\" TYPE=\"text/javascript\" SRC=\"" + ctx + "/javascript/pageListInfo.js\"></SCRIPT>");
        }
        out.write("<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" width=\"100%\">");
        out.write(pagedListInfo.getListPropertiesHeader(name));
        out.write("<tr>");
        out.write("<td valign=\"middle\" " + "align=\"center\"" + ((bgColor != null) ? " bgColor=\"" + bgColor + "\"" : "")
            + ((tdClass != null) ? " class=\"" + tdClass + "\"" : "") + " width=\"100%\">");
        out.write("<input type=\"hidden\" name=\"offset\" value=\"\">");
        out.write("<input type=\"hidden\" name=\"pagedListInfoId\" value=\"" + object + "\">");
        out.write("[" + pagedListInfo.getPreviousPageLink("<font class='underline'>Previous</font>", "Previous", form, renderResponse) + "|"
            + pagedListInfo.getNextPageLink("<font class='underline'>Next</font>", "Next", form, renderResponse) + "] ");
        out.write("<font color=\"" + fontColor + "\">");
        out.write("Page " + pagedListInfo.getNumericalPageEntry() + " ");
        if (!abbreviate) {
          out.write("of " + ((pagedListInfo.getNumberOfPages() == 0) ? "1" : String.valueOf(pagedListInfo.getNumberOfPages())) + ", ");
          String quotesAll = "All";
          out.write("Items per page: " + pagedListInfo.getItemsPerPageEntry(quotesAll + " "));
        } else {
          out.write("of " + ((pagedListInfo.getNumberOfPages() == 0) ? "1" : String.valueOf(pagedListInfo.getNumberOfPages())));
          out.write("&nbsp;&nbsp;");
        }
        out.write("<input type=\"submit\" value=\"go\">");
        out.write("</font>");
        out.write("</td>");
        out.write("</tr>");
        if (batchHTML != null) {
          out.write("<tr><td valign=\"middle\" align=\"center\"" + ((bgColor != null) ? " bgColor=\"" + bgColor + "\"" : "")
              + ((tdClass != null) ? " class=\"" + tdClass + "\"" : "") + " nowrap>");
          out.write(batchHTML);
          out.write("</td></tr>");
        }
        out.write(pagedListInfo.getListPropertiesFooter());
        out.write("</table>");
      } else {
        System.out.println("PagedListControlHandler-> Control not found in request: " + object);
      }
    } catch (Exception e) {
      e.printStackTrace(System.out);
    }
    return EVAL_PAGE;
  }

  /**
   * Gets the label attribute of the PagedListControlHandler object
   *
   * @param map   Description of the Parameter
   * @param input Description of the Parameter
   * @return The label value
   */
  public String getLabel(HashMap map, String input) {
    Template template = new Template(input);
    template.setParseElements(map);
    return template.getParsedText();
  }
}
