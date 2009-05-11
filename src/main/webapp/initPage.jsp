<%--
  ~ ConcourseConnect
  ~ Copyright 2009 Concursive Corporation
  ~ http://www.concursive.com
  ~
  ~ This file is part of ConcourseConnect, an open source social business
  ~ software and community platform.
  ~
  ~ Concursive ConcourseConnect is free software: you can redistribute it and/or
  ~ modify it under the terms of the GNU Affero General Public License as published
  ~ by the Free Software Foundation, version 3 of the License.
  ~
  ~ Under the terms of the GNU Affero General Public License you must release the
  ~ complete source code for any application that uses any part of ConcourseConnect
  ~ (system header files and libraries used by the operating system are excluded).
  ~ These terms must be included in any work that has ConcourseConnect components.
  ~ If you are developing and distributing open source applications under the
  ~ GNU Affero General Public License, then you are free to use ConcourseConnect
  ~ under the GNU Affero General Public License.
  ~
  ~ If you are deploying a web site in which users interact with any portion of
  ~ ConcourseConnect over a network, the complete source code changes must be made
  ~ available.  For example, include a link to the source archive directly from
  ~ your web site.
  ~
  ~ For OEMs, ISVs, SIs and VARs who distribute ConcourseConnect with their
  ~ products, and do not license and distribute their source code under the GNU
  ~ Affero General Public License, Concursive provides a flexible commercial
  ~ license.
  ~
  ~ To anyone in doubt, we recommend the commercial license. Our commercial license
  ~ is competitively priced and will eliminate any confusion about how
  ~ ConcourseConnect can be used and distributed.
  ~
  ~ ConcourseConnect is distributed in the hope that it will be useful, but WITHOUT
  ~ ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
  ~ FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more
  ~ details.
  ~
  ~ You should have received a copy of the GNU Affero General Public License
  ~ along with ConcourseConnect.  If not, see <http://www.gnu.org/licenses/>.
  ~
  ~ Attribution Notice: ConcourseConnect is an Original Work of software created
  ~ by Concursive Corporation
  --%><%-- SPACING IS INTENTIONAL --%><%@ page import="java.util.*,java.io.*,java.sql.*,java.text.*,javax.servlet.*,javax.servlet.http.*" %><%@ page import="com.concursive.commons.db.ConnectionElement"%><%@ page import="com.concursive.connect.Constants"%><%@ page import="com.concursive.commons.text.StringUtils"%><%@ page import="javax.portlet.RenderRequest" %><%@ page import="javax.portlet.PortletRequest" %><%@ page import="com.concursive.connect.web.utils.PagedListInfo" %>
<%!
    //Declare variables to be used by page
    public static String includeFile(String sourceFile){
       StringBuffer HTMLBuffer = new StringBuffer();
       int c;
       FileReader in;
       try {
         File inputFile = new File(sourceFile);
          in = new FileReader(inputFile);
         while ((c = in.read()) != -1) HTMLBuffer.append((char) c);
         in.close();
       } catch (IOException ex) {
         HTMLBuffer.append(ex.toString());
       }
       return HTMLBuffer.toString();
    }

    public static String toHtml(String s) {
      return StringUtils.toHtml(s);
    }

    public static String toHtmlValue(String s) {
      return StringUtils.toHtmlValue(s);
    }

    public static String toJavaScript(String s) {
      return StringUtils.jsStringEscape(s);
    }

    public static String showAttribute(HttpServletRequest request, String errorEntry) {
      String error = (String) request.getAttribute(errorEntry);
      if (error == null) {
        PortletRequest renderRequest = (PortletRequest) request.getAttribute(org.apache.pluto.tags.Constants.PORTLET_REQUEST);
        if (renderRequest != null) {
          error = (String) renderRequest.getAttribute(errorEntry);
        }
      }
      if (error != null) { return "<div class=\"portlet-message-alert\"><p>" + toHtml(error) + "</p></div>"; } else {return "";}
    }

    public static String showError(HttpServletRequest request, String errorEntry) {
      return showError(request, errorEntry, false);
    }

    public static String showError(HttpServletRequest request, String errorEntry, boolean showSpace) {
      String error = (String) request.getAttribute(errorEntry);
      if (error == null) {
        PortletRequest renderRequest = (PortletRequest) request.getAttribute(org.apache.pluto.tags.Constants.PORTLET_REQUEST);
        if (renderRequest != null) {
          error = (String) renderRequest.getAttribute(errorEntry);
        }
      }
      if (error == null) {
        error = request.getParameter(errorEntry);
      }
      if (error != null) {
        return (showSpace ? "&nbsp;<br />" : "") + "<div class=\"portlet-message-error\"><p>" + toHtml(error) + "</p></div>";
      } else {
        return (showSpace ? "&nbsp;" : "");
      }
    }

    public static boolean hasText(String in) {
      return (in != null && !("".equals(in)));
    }

    public static boolean hasReturn(String in) {
      return (hasText(in) && (in.indexOf("\r") > -1 || in.indexOf("\n") > -1));
    }

    public static String toString(String s) {
      if (s != null) {
        s = StringUtils.replace(s, "&", "&amp;");
        return(s);
      } else {
        return("");
      }
    }

    public static boolean isPopup(HttpServletRequest request) {
      boolean isPopup = false;
      if ("true".equals(request.getParameter("popup")) || "true".equals(request.getAttribute("popup"))) {
        isPopup = true;
      }
      PortletRequest renderRequest = (PortletRequest) request.getAttribute(org.apache.pluto.tags.Constants.PORTLET_REQUEST);
      if (renderRequest != null) {
        if ("true".equals(renderRequest.getAttribute("popup"))) {
          isPopup = true;
        }
      }
      return isPopup;
    }

    public static String getServerPort(HttpServletRequest request) {
      int port = request.getServerPort();
      if (port != 80 && port != 443) {
        return ":" + String.valueOf(port);
      }
      return "";
    }

    public static String getServerUrl(HttpServletRequest request) {
      return request.getServerName() + getServerPort(request) + request.getContextPath();
    }
%><% String ctx = request.getContextPath(); request.setAttribute("ctx", ctx); %>