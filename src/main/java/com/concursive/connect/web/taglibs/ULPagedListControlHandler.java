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
import com.concursive.commons.text.StringUtils;
import com.concursive.commons.text.Template;
import com.concursive.connect.web.utils.PagedListInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.portlet.PortletRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;
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
public class ULPagedListControlHandler extends TagSupport {

  // Logger
  private static Log LOG = LogFactory.getLog(ULPagedListControlHandler.class);

  private String name = "controlProperties";
  private String object = null;
  private boolean showForm = true;
  private boolean resetList = true;
  private boolean enableJScript = false;
  private String url = null;
  private String urlSuffix = null;

  private static final String CLASS_PAGINATE = "pagination";
  private static final String CLASS_PREVIOUS_ENABLED = "previous";
  private static final String CLASS_PREVIOUS_DISABLED = "previous-off";
  private static final String CLASS_NEXT_ENABLED = "next";
  private static final String CLASS_NEXT_DISABLED = "next-off";
  private static final String CLASS_ACTIVE = "active";

  public void release() {
    // Reset each property or else the value gets reused
    name = "controlProperties";
    object = null;
    showForm = true;
    resetList = true;
    enableJScript = false;
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

  /**
   * @return the url
   */
  public String getUrl() {
    return url;
  }


  /**
   * @param url the url to set
   */
  public void setUrl(String url) {
    this.url = url;
  }


  /**
   * @return the urlSuffix
   */
  public String getUrlSuffix() {
    return urlSuffix;
  }


  /**
   * @param urlSuffix the urlSuffix to set
   */
  public void setUrlSuffix(String urlSuffix) {
    this.urlSuffix = urlSuffix;
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
        boolean useCtx = true;
        if (url != null && url.startsWith(ctx)) {
          useCtx = false;
        }

        pagedListInfo.setShowForm(showForm);
        pagedListInfo.setResetList(resetList);
        pagedListInfo.setEnableJScript(enableJScript);
        JspWriter out = this.pageContext.getOut();
        String prevClass = pagedListInfo.getHasPreviousPageLink() ? CLASS_PREVIOUS_ENABLED : CLASS_PREVIOUS_DISABLED;
        String nextClass = pagedListInfo.getHasNextPageLink() ? CLASS_NEXT_ENABLED : CLASS_NEXT_DISABLED;

        if (enableJScript) {
          out.write("<SCRIPT LANGUAGE=\"JavaScript\" TYPE=\"text/javascript\" SRC=\"" + ctx + "/javascript/pageListInfo.js\"></SCRIPT>");
        }
        if (pagedListInfo.getMaxRecords() > 0) {
          out.write("<em>" + pagedListInfo.getMaxRecords() + " result" + (pagedListInfo.getMaxRecords() == 1 ? "" : "s") + " found</em>");
        }
        if (pagedListInfo.getNumberOfPages() > 1) {
          out.write("<ol class=\"" + CLASS_PAGINATE + "\">");
          if (url != null) {
            int prevPage = 1;
            if (pagedListInfo.getCurrentPageNumber() != 1) {
              prevPage = pagedListInfo.getCurrentPageNumber() - 1;
              String prevLink = (useCtx ? ctx : "") + url + ((prevPage != 1) ? "/" + prevPage : "");
              out.write("<li class=\"" + prevClass + "\"><a href=\"" + prevLink + "\">Previous</a></li>");
            } else {
              out.write("<li class=\"" + prevClass + "\">Previous</li>");
            }
          } else {
            out.write("<li class=\"" + prevClass + "\">" + pagedListInfo.getPreviousPageLink("Previous", "Previous", null, renderResponse) + "</li>");
          }
          //@TODO make number of links shown available to customize for cases where pagination needs to be larger or smaller
          int PRIOR_LIMIT = 3; //this might be useful to be dynamic
          int AFTER_LIMIT = 3;
          int padPrior = 0;
          int padAfter = 0;
          int currentPage = pagedListInfo.getCurrentPageNumber();
          int lastPage = pagedListInfo.getNumberOfPages();
          int pageStart = currentPage - PRIOR_LIMIT;
          int pageEnd = currentPage + AFTER_LIMIT;
          // Make Sure pageStart and pageEnd fall within legal ranges and calculate any padding
          if (pageStart < 1) {
            padAfter += 0 - pageStart;
            pageStart = 1;
          }
          if (pageEnd > lastPage) {
            padPrior += pageEnd - lastPage;
            pageEnd = lastPage;
          }
          // Check to see if prior or after can get extra padding
          if (padAfter > 0 && pageEnd != lastPage) {
            pageEnd += padAfter;
            if (pageEnd > lastPage) pageEnd = lastPage;
          }
          if (padPrior > 0 && pageStart != 1) {
            pageStart -= padPrior;
            if (pageStart < 1) pageStart = 1;
          }

          if (pageStart != 1) {
            // Show 1st 2 @TODO make dynamic
            for (int i = 1; i < 3; i++) {
              if (pageStart == i) break; // don't print link twice
              String link = null;
              if (url != null) {
                link = (useCtx ? ctx : "") + url + ((i != 1) ? "/" + i : "");
              } else {
                link = pagedListInfo.getLinkForPage(i, renderResponse);
              }
              out.write("<li><a href=\"" + getCompleteLink(link) + "\">" + i + "</a></li>");
            }
            if (pageStart > 3) { // only show ... if there was a break in the counting
              out.write("<li>...</li>");
            }
          }

          for (int i = pageStart; i != pageEnd + 1; i++) {
            if (i == pagedListInfo.getCurrentPageNumber()) {
              out.write("<li class=\"" + CLASS_ACTIVE + "\">" + i + "</li>");
            } else {
              String link = null;
              if (url != null) {
                link = (useCtx ? ctx : "") + url + ((i != 1) ? "/" + i : "");
              } else {
                link = pagedListInfo.getLinkForPage(i, renderResponse);
              }
              out.write("<li><a href=\"" + getCompleteLink(link) + "\">" + i + "</a></li>");
            }
          }

          if (pageEnd != lastPage) {
            if (pageEnd < lastPage - 2) {
              out.write("<li>...</li>");
            }
            // Show last 2 @TODO make dynamic
            for (int i = lastPage - 1; i <= lastPage; i++) {
              if (pageEnd == i) continue; // don't print link twice
              String link = null;
              if (url != null) {
                link = (useCtx ? ctx : "") + url + ((i != 1) ? "/" + i : "");
              } else {
                link = pagedListInfo.getLinkForPage(i, renderResponse);
              }
              out.write("<li><a href=\"" + getCompleteLink(link) + "\">" + i + "</a></li>");
            }
          }

          if (url != null) {
            int nextPage = 1;
            if (pagedListInfo.getCurrentPageNumber() != pagedListInfo.getNumberOfPages()) {
              nextPage = pagedListInfo.getCurrentPageNumber() + 1;
              String nextLink = (useCtx ? ctx : "") + url + "/" + nextPage;
              out.write("<li class=\"" + nextClass + "\"><a href=\"" + getCompleteLink(nextLink) + "\">Next</a></li>");
            } else {
              out.write("<li class=\"" + nextClass + "\">Next</li>");
            }
          } else {
            out.write("<li class=\"" + nextClass + "\">" + pagedListInfo.getNextPageLink("Next", "Next", null, renderResponse) + "</li>");
          }
          out.write("</ol>");
        }
      } else {
        LOG.error("Control not found in request: " + object);
      }
    } catch (IOException e) {
      LOG.error("doEndTag error", e);
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

  private String getCompleteLink(String link) {
    if (StringUtils.hasText(link)) {
      return link + (StringUtils.hasText(urlSuffix) ? urlSuffix : "");
    }
    return link;
  }
}