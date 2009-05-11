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
import com.concursive.commons.xml.XMLUtils;
import com.concursive.connect.Constants;
import com.concursive.connect.cms.portal.dao.Dashboard;
import com.concursive.connect.cms.portal.dao.DashboardPage;
import com.concursive.connect.cms.portal.dao.DashboardPortlet;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.utils.PermissionUtils;
import org.w3c.dom.Element;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.jsp.tagext.TryCatchFinally;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 * Renders the layout of a dashboard page.  Pages are layed out into rows
 * and columns.  Assumptions based on an occuring web action and portal configuration.
 *
 * @author matt rajkowski
 * @created Apr 3, 2008
 */
public class DashboardLayoutHandler extends TagSupport implements TryCatchFinally {

  private String css = "";

  public void doCatch(Throwable throwable) throws Throwable {
    // Required but not needed
  }

  public void doFinally() {
    // Reset each property or else the value gets reused
    css = "";
  }

  public void setCss(String css) {
    this.css = css;
  }

  public int doStartTag() throws JspException {
    JspWriter out = pageContext.getOut();

    String ctx = ((HttpServletRequest) pageContext.getRequest()).getContextPath();

    // The project in which the dashboard is being displayed (if any)
    Project project = (Project) pageContext.getRequest().getAttribute("project");

    // The dashboard represents the selected dashboard tab
    Dashboard dashboard = (Dashboard) pageContext.getRequest().getAttribute("dashboard");
    int editModeId = -1;
    try {
      editModeId = (Integer) pageContext.getRequest().getAttribute("editModeId");
    } catch (Exception ie) {
      // not a number
    }
    int maximizedModeId = -1;
    try {
      maximizedModeId = (Integer) pageContext.getRequest().getAttribute("maximizedModeId");
    } catch (Exception ie) {
      // not a number
    }

    // The dashboard page contains all of the portlets to be displayed for the selected dashboard tab
    DashboardPage dashboardPage = (DashboardPage) pageContext.getRequest().getAttribute("dashboardPage");

    // See if the page exists, and if the page has any portlets on it
    if (dashboardPage == null || !StringUtils.hasText(dashboardPage.getXmlDesign())) {
      return SKIP_BODY;
    }

    // The admin can edit the dashboard layout and parameters
    boolean projectDashboardAdminPermission = project != null && PermissionUtils.hasPermissionToAction(pageContext.getRequest(), pageContext.getSession(), "project-dashboard-admin");

    // Render the output
    try {
      // Base this on the xmlDesign of the DashboardPage
      XMLUtils xml = new XMLUtils(dashboardPage.getXmlDesign());

      // If in edit mode, or a portlet is maximized, find that portlet and output it
      if (editModeId > -1 || maximizedModeId > -1) {
        int falseIdCount = -1;
        ArrayList rows = new ArrayList();
        XMLUtils.getAllChildren(xml.getDocumentElement(), "row", rows);
        Iterator i = rows.iterator();
        while (i.hasNext()) {
          Element rowEl = (Element) i.next();
          ArrayList columns = new ArrayList();
          XMLUtils.getAllChildren(rowEl, "column", columns);
          Iterator j = columns.iterator();
          while (j.hasNext()) {
            Element columnEl = (Element) j.next();
            ArrayList portlets = new ArrayList();
            XMLUtils.getAllChildren(columnEl, "portlet", portlets);
            Iterator k = portlets.iterator();
            while (k.hasNext()) {
              Element portletEl = (Element) k.next();
              // portlets in the column
              ++falseIdCount;
              DashboardPortlet thisPortlet = dashboardPage.getPortletList().get(falseIdCount);
              if ((editModeId == thisPortlet.getId()) ||
                  maximizedModeId == thisPortlet.getId()) {
                // Output the portlet
                if (editModeId > -1) {
                  out.write("<div class=\"portletWindow\">");
                }
                String portalResponse = (String) pageContext.getRequest().getAttribute("portal_response_" + thisPortlet.getId());
                pageContext.getOut().print(portalResponse);
                if (editModeId > -1) {
                  out.write("</div>");
                }
                return SKIP_BODY;
              }
            }

          }
        }
      }

      // If not in edit mode and not maximized, output all portlets using the appropriate column width
      if (projectDashboardAdminPermission) {
        out.write("<script language=\"JavaScript\" type=\"text/javascript\" src=\"" + ctx + "/javascript/dashboardlayout.js?v=20080501\"></script>");
      }
      /*
      function portletMove(itemId, columnId) {
        callSavePageDesign(ctx + "/ProjectManagementListsBuckets.do?command=Move&pid=<%= Project.getId() %>&id=" + itemId + "&columnId=" + columnId + "&key=<%= itemList.getObjectKeyProperty() %>&out=text");
      }
      */
      /*
      function deletePortlet(taskId) {
        if (confirm("Delete?")) {
          callDeleteBucketItem(ctx + "/ProjectManagementListsBuckets.do?command=Delete&pid=<%= Project.getId() %>&cid=<%= category.getId() %>&id=" + taskId + "&out=text");
        }
      }
      */

      out.write("<table class=\"" + css + "portletContainer\"><tr><td>");
      out.write("<div class=\"" + css + "portalContainer\">");
      out.write("<div class=\"" + css + "portalContainer1\">");
      out.write("<div class=\"" + css + "portalContainer2\">");
      out.write("<div class=\"" + css + "portalContainer3\">");
      out.write("<div class=\"" + css + "portalContainer4\">");

      // The falseIdCount is used to access the portlets output, based on the
      // original count in the portal
      int falseIdCount = -1;
      // Dropzone gives a unique id to the portlet handler in the view
      int dropZone = 0;
      // Processing begins at the row level
      ArrayList rows = new ArrayList();
      XMLUtils.getAllChildren(xml.getDocumentElement(), "row", rows);
      int rowCount = 0;
      Iterator i = rows.iterator();
      while (i.hasNext()) {
        Element rowEl = (Element) i.next();
        // Filter out portlets depending on if the user is registered
        boolean output = true;
        if (rowEl.hasAttribute("isUser")) {
          User user = (User) pageContext.getSession().getAttribute(Constants.SESSION_USER);
          if ("true".equals(rowEl.getAttribute("isUser")) && user.getId() < 1) {
            output = false;
          }
          if ("false".equals(rowEl.getAttribute("isUser")) && user.getId() > 0) {
            output = false;
          }
        }
        if (!output) {
          // Still need to count up the portlets so the output can be accessed
          ArrayList columns = new ArrayList();
          XMLUtils.getAllChildren(rowEl, "column", columns);
          Iterator j = columns.iterator();
          while (j.hasNext()) {
            Element columnEl = (Element) j.next();
            ArrayList portlets = new ArrayList();
            XMLUtils.getAllChildren(columnEl, "portlet", portlets);
            falseIdCount += portlets.size();
          }
          continue;
        }
        ++rowCount;

        // Use a table for outputting the row and column(s)
        out.write("<table class=\"" + css + "portletTable" + (rowCount == 1 ? " " + css + "portletTable1" : "") + (i.hasNext() ? "" : " " + css + "portletTableLast") + "\">");

        // Determine if the row has a row class specified
        String rowCss = css;
        if (rowEl.hasAttribute("class")) {
          rowCss = rowEl.getAttribute("class");
        }
        out.write("<tr class=\"" + rowCss + "portletRow" + (rowCount == 1 ? " " + rowCss + "portletRow1" : "") + (i.hasNext() ? "" : " " + rowCss + "portletRowLast") + "\">");

        // For the given columns, determine if there are any portlets to render in them, and add them here
        LinkedHashMap<Element, ArrayList<Element>> columnsWithValidPortlets = new LinkedHashMap<Element, ArrayList<Element>>();

        // Process the columns in the row
        ArrayList<Element> columns = new ArrayList<Element>();
        XMLUtils.getAllChildren(rowEl, "column", columns);
        for (Object thisColumn : columns) {

          // Process the portlets in the column
          Element thisColumnEl = (Element) thisColumn;
          ArrayList portlets = new ArrayList();
          XMLUtils.getAllChildren(thisColumnEl, "portlet", portlets);

          // Store the valid portlets here
          ArrayList<Element> portletsInColumn = null;

          // Check to see if the portlet is output or not
          Iterator k = portlets.iterator();
          while (k.hasNext()) {
            Element portletEl = (Element) k.next();
            ++falseIdCount;
            // Check to see if the portlet is displayed based on whether a user or not
            if (portletEl.hasAttribute("isUser")) {
              User user = (User) pageContext.getSession().getAttribute(Constants.SESSION_USER);
              if ("true".equals(portletEl.getAttribute("isUser")) && user.getId() < 1) {
                continue;
              }
              if ("false".equals(portletEl.getAttribute("isUser")) && user.getId() > 0) {
                continue;
              }
            }
            if (portletEl.hasAttribute("isAdmin")) {
              User user = (User) pageContext.getSession().getAttribute(Constants.SESSION_USER);
              if ("true".equals(portletEl.getAttribute("isAdmin")) && !user.getAccessAdmin()) {
                continue;
              }
              if ("false".equals(portletEl.getAttribute("isAdmin")) && user.getAccessAdmin()) {
                continue;
              }
            }
            // Ignore empty portlets
            DashboardPortlet thisPortlet = dashboardPage.getPortletList().get(falseIdCount);
            String portalResponse = (String) pageContext.getRequest().getAttribute("portal_response_" + thisPortlet.getId());
            if (portalResponse == null || portalResponse.length() == 0) {
              continue;
            }
            // Map the falseId to the portlet for when it gets displayed
            portletEl.setAttribute("falseId", String.valueOf(falseIdCount));
            // Add the valid portlet to the column list
            if (portletsInColumn == null) {
              portletsInColumn = new ArrayList<Element>();
            }
            portletsInColumn.add(portletEl);
          }
          // If the columns has portlets, add it to the map for display
          if (portletsInColumn != null) {
            columnsWithValidPortlets.put(thisColumnEl, portletsInColumn);
          }
        }

        // Now that the number of columns is known, and the exact portlets
        // to be shown, draw the columns and portlets...
        int columnCount = 0;
        Iterator j = columnsWithValidPortlets.keySet().iterator();
        while (j.hasNext()) {
          ++columnCount;
          // Determine the column width
          Element columnEl = (Element) j.next();
          String width = (100 / columns.size()) + "%";
          if (columnEl.hasAttribute("width")) {
            width = columnEl.getAttribute("width");
          }
          ++dropZone;
          // Determine if the column has a column class specified
          String columnCss = css;
          if (columnEl.hasAttribute("class")) {
            columnCss = columnEl.getAttribute("class");
          }
          // Output the column
          out.write("<td id=\"portletZone_" + dropZone + "\" class=\"" + columnCss + "portletColumn" + (columnCount == 1 ? " " + columnCss + "portletColumn1" : "") + (j.hasNext() ? "" : " " + columnCss + "portletColumnLast") + "\" width=\"" + width + "\" valign=\"top\">");
          // Output the portlets
          ArrayList<Element> portlets = columnsWithValidPortlets.get(columnEl);
          int portletCount = 0;
          Iterator k = portlets.iterator();
          while (k.hasNext()) {
            Element portletEl = (Element) k.next();
            int thisIdCount = Integer.parseInt(portletEl.getAttribute("falseId"));
            ++portletCount;
            // Retrieve the portlet's response
            DashboardPortlet thisPortlet = dashboardPage.getPortletList().get(thisIdCount);
            String portalResponse = (String) pageContext.getRequest().getAttribute("portal_response_" + thisPortlet.getId());
            // Determine if the portlet has a window class specified
            String portletCss = css;
            if (thisPortlet.getHtmlClass() != null) {
              portletCss = thisPortlet.getHtmlClass();
            }
            // Output the portlet
            out.write("<div id=\"portletWindow_" + thisIdCount + "\" class=\"" + css + "portletWindow" + (portletCount == 1 ? " " + css + "portletWindow1" : "") + (k.hasNext() ? "" : " " + css + "portletWindowLast") + "\">");
            out.write("<div class=\"" + portletCss + "portletWindowBackground" + (portletCount == 1 ? " " + portletCss + "portletWindowBackground1" : "") + (k.hasNext() ? "" : " " + portletCss + "portletWindowBackgroundLast") + "\">");
            out.write("<div class=\"" + portletCss + "portlet1\">");
            out.write("<div class=\"" + portletCss + "portlet2\">");
            out.write("<div class=\"" + portletCss + "portlet3\">");
            out.write("<div class=\"" + portletCss + "portlet4\">");
            out.write("<div class=\"portletWrapper\">");
            if (thisPortlet.getLoaded()) {
              if (projectDashboardAdminPermission) {
                out.write("<div id=\"portletConfigure_" + thisIdCount + "\" class=\"" + css + "portletConfigure\">");
                out.write("[<a href=\"" + ctx + "/ProjectManagement.do?command=ProjectCenter&section=Dashboard&pid=" + project.getId() + "&dash=" + dashboard.getId() + "&portletMode=__pm" + thisPortlet.getWindowConfigId() + "_edit\">edit</a>]");
                out.write("[<a href=\"" + ctx + "/ProjectManagement.do?command=ProjectCenter&section=Dashboard&pid=" + project.getId() + "&dash=" + dashboard.getId() + "&portletMode=__pm" + thisPortlet.getWindowConfigId() + "_help\">help</a>]");
                out.write("</div>");
              } else {
                out.write("<div id=\"portletConfigure_" + thisIdCount + "\" class=\"" + css + "portletConfigure\">");
                out.write("move");
                out.write("</div>");
              }
            }
            pageContext.getOut().print(portalResponse);
            out.write("</div>");
            out.write("</div>");
            out.write("</div>");
            out.write("</div>");
            out.write("</div>");
            out.write("</div>");
            out.write("</div>");
          }
          out.write("</td>");
        }
        out.write("</tr>");
        out.write("</table>");
      }
      out.write("</div>");
      out.write("</div>");
      out.write("</div>");
      out.write("</div>");
      out.write("</div>");
      out.write("</td></tr></table>");
    } catch (Exception e) {
      e.printStackTrace(System.out);
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
