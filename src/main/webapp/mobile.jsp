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
<%@ page import="com.concursive.commons.http.RequestUtils" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:useBean id="applicationPrefs" class="com.concursive.connect.config.ApplicationPrefs" scope="application"/>
<%@ include file="initPage.jsp" %>
<center>
  <table cellpadding="0" cellspacing="0" border="0">
    <c:if test="${!empty requestMainProfile}">
      <tr>
        <td align="center">
          <c:out value="${requestMainProfile.title}"/>
        </td>
      </tr>
    </c:if>
    <tr>
      <td align="center">
        <img src="<%= RequestUtils.getAbsoluteServerUrl(request) %>/images/teamelements/shape-sm.gif" alt="ConcourseConnect" />
      </td>
    </tr>
    <tr>
      <td align="center">
        <strong>A social utility that connects businesses, organizations, people and groups.</strong><br />
        <br />
        Blogs, wikis, reviews, discussions, documents, search,
        and more in a single application.
      </td>
    </tr>
    <tr>
      <td align="center">
        &nbsp;
      </td>
    </tr>
    <tr>
      <td align="center">
        <a href="<%= ctx %>/login"><img border="0" alt="Login" src="<%= RequestUtils.getAbsoluteServerUrl(request) %>/images/buttons/loginb-green.gif" /></a>
        <%--
        <a href="<%= ctx %>/register"><img src="<%= ctx %>/images/buttons/setup_an_account.gif" alt="Register" border="0" /></a>
        --%>
      </td>
    </tr>
    <tr>
      <td align="center">
        &nbsp;
      </td>
    </tr>
    <tr>
      <td align="center">
        <a href="<%= RequestUtils.getAbsoluteServerUrl(request) %>/?useMobile=false">Use full browsing</a>
      </td>
    </tr>
  </table>
  <br />
  (C) 2009 Concursive Corporation
</center>