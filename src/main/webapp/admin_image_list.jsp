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
<%@ taglib uri="/WEB-INF/concourseconnect-taglib.tld" prefix="ccp" %>
<%@ page import="com.concursive.commons.http.RequestUtils" %>
<%@ page import="com.concursive.connect.web.modules.documents.dao.FileItem" %>
<link rel="stylesheet" href="<%= RequestUtils.getAbsoluteServerUrl(request) %>/css/iteam-images.css" type="text/css">
<jsp:useBean id="SKIN" class="java.lang.String" scope="application"/>
<jsp:useBean id="imageList" class="com.concursive.connect.web.modules.documents.dao.FileItemList" scope="request"/>
<%@ include file="initPage.jsp" %>
<a href="<%= ctx %>/admin">System Administration</a> >
Global Image Library<br />
<br />
<img src="<%= RequestUtils.getAbsoluteServerUrl(request) %>/images/icons/stock_insert-file-16.gif" border="0" align="absmiddle">
<a href="<%= ctx %>/AdminImageLibrary.do?command=Add">Submit File</a><br />
<table cellpadding="10" cellspacing="0" border="0" width="100%">
<%
	if (imageList.size() == 0) {
%>
    <tr>
      <td class="ImageList" valign="center">
        No images to display.
      </td>
    </tr>
<%
  } else {
    int rowcount = 0;
    int count = 0;
    //Show the images
    Iterator i = imageList.iterator();
    while (i.hasNext()) {
      FileItem thisItem = (FileItem) i.next();
      ++count;
      if ((count+2) % 3 == 0) {
        ++rowcount;
      }
%>
<ccp:evaluate if="<%= (count+2) % 3 == 0 %>">
  <tr>
</ccp:evaluate>
    <td class="ImageList<%= (rowcount == 1?"":"AdditionalRow") %>">
      <span>
        <img border="0" src="Portal.do?command=ThumbnailImage&i=<%= thisItem.getId() %>" align="absmiddle" alt="" /><br />
        <%= toHtml(thisItem.getSubject()) %><br />
        <a href="<%= ctx %>/AdminImageLibrary.do?command=DeleteImage&i=<%= thisItem.getId() %>">delete</a>
      </span>
    </td>
<ccp:evaluate if="<%= count % 3 == 0 %>">
  </tr>
</ccp:evaluate>
<%
    }
%>
<ccp:evaluate if="<%= count % 3 != 0 %>">
  </tr>
</ccp:evaluate>
<%}%>
</table>
