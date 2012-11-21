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
import com.concursive.connect.Constants;
import com.concursive.connect.config.ApplicationPrefs;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.utils.ClientType;
import com.concursive.connect.web.utils.PagedListInfo;

import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 * Displays the status of the PagedListInfo specified with record counts, next
 * and previous buttons, and optionally other items.
 *
 * @author chris
 * @created October, 2002
 */
public class PagedListStatusHandler extends BodyTagSupport {
  private String name = "statusProperties";
  private String label = "Records";
  private String object = null;
  private String bgColor = null;
  private String fontColor = "#666666";
  private String tdClass = null;
  private String tableClass = null;
  private boolean showHiddenParams = false;
  private boolean showForm = true;
  private boolean resetList = true;
  private boolean showExpandLink = false;
  private String title = "&nbsp;";
  private boolean showRefresh = true;
  private boolean showControlOnly = false;
  private boolean scrollReload = false;
  private boolean enableJScript = false;
  private String rss = null;


  /**
   * Sets the name attribute of the PagedListStatusHandler object
   *
   * @param tmp The new name value
   */
  public final void setName(String tmp) {
    name = tmp;
  }


  /**
   * Sets the label attribute of the PagedListStatusHandler object
   *
   * @param tmp The new label value
   */
  public void setLabel(String tmp) {
    this.label = tmp;
  }


  /**
   * Sets the object attribute of the PagedListStatusHandler object
   *
   * @param tmp The new object value
   */
  public final void setObject(String tmp) {
    object = tmp;
  }


  /**
   * Gets the showExpandLink attribute of the PagedListStatusHandler object
   *
   * @return The showExpandLink value
   */
  public boolean getShowExpandLink() {
    return showExpandLink;
  }


  /**
   * Sets the showExpandLink attribute of the PagedListStatusHandler object
   *
   * @param showExpandLink The new showExpandLink value
   */
  public void setShowExpandLink(boolean showExpandLink) {
    this.showExpandLink = showExpandLink;
  }


  /**
   * Sets the bgColor attribute of the PagedListStatusHandler object
   *
   * @param tmp The new bgColor value
   */
  public final void setBgColor(String tmp) {
    bgColor = tmp;
  }


  /**
   * Sets the fontColor attribute of the PagedListStatusHandler object
   *
   * @param tmp The new fontColor value
   */
  public final void setFontColor(String tmp) {
    fontColor = tmp;
  }


  /**
   * Gets the title attribute of the PagedListStatusHandler object
   *
   * @return The title value
   */
  public String getTitle() {
    return title;
  }


  /**
   * Sets the title attribute of the PagedListStatusHandler object
   *
   * @param title The new title value
   */
  public void setTitle(String title) {
    this.title = title;
  }


  /**
   * Sets the tdClass attribute of the PagedListStatusHandler object
   *
   * @param tmp The new tdClass value
   */
  public final void setTdClass(String tmp) {
    tdClass = tmp;
  }


  /**
   * Sets the tableClass attribute of the PagedListStatusHandler object
   *
   * @param tmp The new tableClass value
   */
  public final void setTableClass(String tmp) {
    tableClass = tmp;
  }


  /**
   * Sets the showForm attribute of the PagedListStatusHandler object
   *
   * @param tmp The new showForm value
   */
  public void setShowForm(String tmp) {
    this.showForm = DatabaseUtils.parseBoolean(tmp);
  }


  /**
   * Sets the showHiddenParams attribute of the PagedListStatusHandler object
   *
   * @param tmp The new showHiddenParams value
   */
  public void setShowHiddenParams(String tmp) {
    this.showHiddenParams = DatabaseUtils.parseBoolean(tmp);
  }


  /**
   * Sets the resetList attribute of the PagedListStatusHandler object
   *
   * @param resetList The new resetList value
   */
  public void setResetList(String resetList) {
    this.resetList = "true".equalsIgnoreCase(resetList);
  }


  /**
   * Sets the showRefresh attribute of the PagedListStatusHandler object
   *
   * @param tmp The new showRefresh value
   */
  public void setShowRefresh(boolean tmp) {
    this.showRefresh = tmp;
  }


  /**
   * Sets the showRefresh attribute of the PagedListStatusHandler object
   *
   * @param tmp The new showRefresh value
   */
  public void setShowRefresh(String tmp) {
    this.showRefresh = DatabaseUtils.parseBoolean(tmp);
  }


  /**
   * Gets the showRefresh attribute of the PagedListStatusHandler object
   *
   * @return The showRefresh value
   */
  public boolean getShowRefresh() {
    return showRefresh;
  }


  /**
   * Sets the showControlOnly attribute of the PagedListStatusHandler object
   *
   * @param tmp The new showControlOnly value
   */
  public void setShowControlOnly(boolean tmp) {
    this.showControlOnly = tmp;
  }


  /**
   * Sets the showControlOnly attribute of the PagedListStatusHandler object
   *
   * @param tmp The new showControlOnly value
   */
  public void setShowControlOnly(String tmp) {
    this.showControlOnly = DatabaseUtils.parseBoolean(tmp);
  }


  /**
   * Sets the scrollReload attribute of the PagedListStatusHandler object
   *
   * @param tmp The new scrollReload value
   */
  public void setScrollReload(boolean tmp) {
    this.scrollReload = tmp;
  }


  /**
   * Sets the scrollReload attribute of the PagedListStatusHandler object
   *
   * @param tmp The new scrollReload value
   */
  public void setScrollReload(String tmp) {
    this.scrollReload = DatabaseUtils.parseBoolean(tmp);
  }


  /**
   * Sets the enableJScript attribute of the PagedListStatusHandler object
   *
   * @param enableJScript The new enableJScript value
   */
  public void setEnableJScript(boolean enableJScript) {
    this.enableJScript = enableJScript;
  }


  /**
   * Sets the enableJScript attribute of the PagedListStatusHandler object
   *
   * @param tmp The new enableJScript value
   */
  public void setEnableJScript(String tmp) {
    this.enableJScript = DatabaseUtils.parseBoolean(tmp);
  }

  public void setRss(String rss) {
    this.rss = rss;
  }

  /**
   * Description of the Method
   *
   * @return Description of the Return Value
   * @throws JspException Description of the Exception
   */
  public final int doAfterBody() throws JspException {
    try {
      // Display the body
      BodyContent bodyContent = getBodyContent();
      if (bodyContent != null) {
        title = bodyContent.getString();
      }
    } catch (Exception e) {
    }
    return SKIP_BODY;
  }


  /**
   * Description of the Method
   *
   * @return Description of the Return Value
   * @throws JspException Description of the Exception
   */
  public final int doEndTag() throws JspException {
    try {
      PortletRequest renderRequest = (PortletRequest) pageContext.getRequest().getAttribute(org.apache.pluto.tags.Constants.PORTLET_REQUEST);
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

        pagedListInfo.setEnableJScript(enableJScript);
        JspWriter out = this.pageContext.getOut();
        //include java scripts if any
        if (enableJScript) {
          out.write("<SCRIPT LANGUAGE=\"JavaScript\" TYPE=\"text/javascript\" SRC=\"" + ctx + "/javascript/pageListInfo.js\"></SCRIPT>");
        }
        //Draw the header of the PagedList table
        out.write("<table " +
            ((tableClass != null) ? "class=\"" + tableClass + "\" " : "") +
            "align=\"center\" width=\"100%\" cellpadding=\"4\" cellspacing=\"0\" border=\"0\">");
        out.write("<tr>");
        //Display the title
        if (!showControlOnly) {
          //Display the title
          if (showExpandLink) {
            out.write("<th ");
          } else {
            out.write("<td ");
          }
          out.write("nowrap valign=\"bottom\" " +
              "align=\"left\"" +
              ((bgColor != null) ? " bgColor=\"" + bgColor + "\"" : "") +
              ((tdClass != null) ? " class=\"" + tdClass + "\"" : "") +
              ">");
          // Show the title
          final ApplicationPrefs prefs =
              (ApplicationPrefs) pageContext.getServletContext()
                  .getAttribute("applicationPrefs");
          final User user = (User) pageContext.getSession().getAttribute(Constants.SESSION_USER);
          final String language = (null != user.getLanguage())
              ? user.getLanguage()
              : prefs.get(ApplicationPrefs.LANGUAGE);
          final String newTitle =
              prefs.getLabel("pagedListInfo.pagedListStatus." + title,
                  language);
          out.write((null != newTitle) ? newTitle : title);
          //show hidden values only if showform is false
          if (showHiddenParams) {
            out.write("<input type=\"hidden\" name=\"offset\" value=\"\" />");
            out.write("<input type=\"hidden\" name=\"pagedListInfoId\" value=\"" + object + "\" />");
          }
          if (showExpandLink) {
            out.write("</th>");
          } else {
            out.write("</td>");
          }
          //Display expansion link
          if (showExpandLink) {
            out.write("<td nowrap width=\"100%\" valign=\"bottom\" " +
                "align=\"left\">");
            final String expandLinkTitle = "Show more";
            final String localizedExpandLinkTitle =
                prefs.getLabel("pagedListInfo.expandLink", language);
            final String returnLinkTitle = "Return to overview";
            final String localizedReturnLinkTitle =
                prefs.getLabel("pagedListInfo.returnLink", language);

            out.write(" ("
                + pagedListInfo.getExpandLink(
                (null != localizedExpandLinkTitle)
                    ? localizedExpandLinkTitle
                    : expandLinkTitle,
                (null != localizedReturnLinkTitle)
                    ? localizedReturnLinkTitle
                    : returnLinkTitle)
                + ")");
            out.write("</td>");
          }
        }
        String returnAction = pageContext.getRequest().getParameter("return");
        ClientType clientType = (ClientType) pageContext.getSession().getAttribute(Constants.SESSION_CLIENT_TYPE);
        if ((clientType == null || !clientType.getMobile()) && (returnAction == null || !returnAction.equals("details"))) {
          //The status cell on the right
          out.write("<td valign=\"bottom\" align=\"" + (showControlOnly ? "center" : "right") + "\" nowrap>");
          //Display record count
          if (pagedListInfo.getMaxRecords() > 0) {
            if (pagedListInfo.getItemsPerPage() == 1) {
              //1 of 20 [Previous|Next]
              out.write(String.valueOf(pagedListInfo.getCurrentOffset() + 1));
            } else {
              // 5 records total

              //Items 1 to 10 of 20 total [Previous|Next]
              out.write(label + " " + (pagedListInfo.getCurrentOffset() + 1) + " to ");
              if (pagedListInfo.getItemsPerPage() <= 0) {
                out.write(String.valueOf(pagedListInfo.getMaxRecords()));
              } else if ((pagedListInfo.getCurrentOffset() + pagedListInfo.getItemsPerPage()) < pagedListInfo.getMaxRecords()) {
                out.write(String.valueOf(pagedListInfo.getCurrentOffset() + pagedListInfo.getItemsPerPage()));
              } else {
                out.write(String.valueOf(pagedListInfo.getMaxRecords()));
              }
            }
            out.write(" of " + pagedListInfo.getMaxRecords());
            if (pagedListInfo.getItemsPerPage() != 1) {
              out.write(" total");
            }
          } else {
            out.write("No " + label.toLowerCase() + " to display");
          }
          //Display next/previous buttons
          if (pagedListInfo.getItemsPerPage() > 0) {
            if (pagedListInfo.getExpandedSelection() || !showExpandLink) {
              pagedListInfo.setScrollReload(scrollReload);
              out.write(" [" +
                  pagedListInfo.getPreviousPageLink("<font class='underline'>Previous</font>", "Previous") +
                  "|" +
                  pagedListInfo.getNextPageLink("<font class='underline'>Next</font>", "Next") +
                  "]");
              out.write(" ");
            }
          }
          //Display refresh icon
          if (pagedListInfo.hasLink() && showRefresh) {
            out.write(" " + pagedListInfo.getRefreshTag("<img src=\"" + ctx + "/images/refresh.gif\" border=\"0\" align=\"absbottom\" />", pageContext.getRequest()));
          }
          // Show link to RSS feed
          if (rss != null) {
            out.write(" ");
            out.write("<a href=\"" + rss + "\">");
            out.write("<img src=\"" + ctx + "/images/xml.gif\" align=\"absMiddle\" border=\"0\" />");
            out.write("</a>");
          }
          //Close the cell
          out.write("</td>");
        }
        //Close the table
        out.write("</tr></table>");
      } else {
        System.out.println("PagedListStatusHandler-> Status not found in request: " + object);
      }
    } catch (Exception e) {
      e.printStackTrace(System.out);
    }
    return EVAL_PAGE;
  }
}


