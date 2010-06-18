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
  --%>
<jsp:useBean id="project" class="com.concursive.connect.web.modules.profile.dao.Project" scope="request"/>
<%@ include file="initPage.jsp" %>
<html>
<%
  boolean scrollReload = false;
  String location = null;
  String returnPage = (String) request.getAttribute("return");
  if (returnPage == null) {
    returnPage = request.getParameter("return");
  }
  String param = (String) request.getAttribute("param");
  if (param == null) {
    param = StringUtils.encodeUrl(request.getParameter("param"));
  }
  String param2 = (String) request.getAttribute("param2");
  if (param2 == null) {
    param2 = StringUtils.encodeUrl(request.getParameter("param2"));
  }
  if (returnPage != null) {
    // TODO: separate these out into proper refresh files
    if ("ProjectRequirements".equals(returnPage)) {
      location = ctx + "/ProjectManagement.do?command=ProjectCenter&section=Requirements&pid=" + param;
      scrollReload = true;
    } else if ("ProjectAssignments".equals(returnPage)) {
      location = ctx + "/ProjectManagement.do?command=ProjectCenter&section=Assignments&pid=" + param + "&rid=" + param2;
      scrollReload = true;
    } else if ("ProjectFiles".equals(returnPage)) {
      location = ctx + "/ProjectManagement.do?command=ProjectCenter&section=File_Library&pid=" + param + "&folderId=" + param2;
      scrollReload = true;
    } else if ("Portal".equals(returnPage)) {
      location = ctx + "/Portal.do?pid=" + param + "&nid=" + param2;
      scrollReload = false;
    } else if ("ProjectMeetings".equals(returnPage)) {
      location = ctx + "/ProjectManagement.do?command=ProjectCenter&section=Calendar&source=Calendar&reloadCalendarDetails=true&pid=" + param ;
      scrollReload = true;
    } else {
      location = returnPage;
    }
  }
  
  if (location == null) {
%>
<body onload="window.opener.document.location.href='<%= ctx %>/ProjectManagement.do'; window.close();">
<% 
  } else if (scrollReload) {
%>
<body onload="window.opener.scrollReload('<%= location %>'); window.close();">
<%  
  } else {
%>
<body onload="window.opener.location='<%= location %>'; window.close();">
<%
  }
%>
</body>
</html>
