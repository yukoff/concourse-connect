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
<%@ page import="com.concursive.connect.web.modules.translation.dao.LanguagePack"%>
<%@ page import="com.concursive.connect.web.modules.translation.dao.LanguageTeam"%>
<%@ page import="com.concursive.commons.http.RequestUtils" %>
<jsp:useBean id="thisUser" class="com.concursive.connect.web.modules.login.dao.User" scope="request"/>
<jsp:useBean id="languagePackList" class="com.concursive.connect.web.modules.translation.dao.LanguagePackList" scope="request"/>
<%@ include file="initPage.jsp" %>
<script type="text/javascript">
  function setLanguageMember(vals) {
    window.frames['server_commands'].location.href=
      "AdminUserDetails.do?command=SetLanguageMember&function=" + vals.name + "&value=" + vals.checked + "&userId=<%= thisUser.getId() %>" + "&out=text";
  }
</script>
<%-- Temp. fix for Weblogic --%>
<%
String detailsUrl = ctx + "/AdminUserDetails.do?command=Details&id=" + thisUser.getId();
String projectsUrl = ctx + "/AdminUserDetails.do?command=Projects&id=" + thisUser.getId();
String loginsUrl = ctx + "/AdminUserDetails.do?command=Logins&id=" + thisUser.getId();
String languagesUrl = ctx + "/AdminUserDetails.do?command=Languages&id=" + thisUser.getId();
String webSitesUrl = ctx + "/AdminUserDetails.do?command=WebSites&id=" + thisUser.getId();
%>
<a href="<%= ctx %>/admin">System Administration</a> >
<a href="<%= ctx %>/AdminUsers.do">Manage Users</a> >
<a href="<%= ctx %>/AdminUsers.do?command=Search">Search Results</a> >
Language Details<br />
<br />
<table border="0" width="100%">
  <ccp:evaluate if="<%= !thisUser.getEnabled() %>">
  <tr>
    <td>
      <img src="<%= ctx %>/images/error.gif" border="0" align="absMiddle"/>
    </td>
    <td width="100%" colspan="2">
      This user is not allowed to login
    </td>
  </tr>
  </ccp:evaluate>
  <tr>
    <td>
      <img src="<%= ctx %>/images/icons/stock_new-bcard-16.gif" border="0" align="absMiddle"/>
    </td>
    <td width="100%">
      <strong><%= toHtml(thisUser.getNameFirstLast()) %></strong>
    </td>
    <td align="right" nowrap>
      (Type: <ccp:evaluate if="<%= thisUser.getAccessAdmin() %>">Administrator</ccp:evaluate><ccp:evaluate if="<%= !thisUser.getAccessAdmin() %>">User</ccp:evaluate>)
      &nbsp;
    </td>
  </tr>
</table>
<div class="tabs-te" id="toptabs">
<table cellpadding="4" cellspacing="0" border="0" width="100%">
  <tr>
    <ccp:tabbedMenu text="Details" key="details" value="languages" url="<%= detailsUrl %>"/>
    <ccp:tabbedMenu text="Projects" key="projects" value="languages" url="<%= projectsUrl %>"/>
    <ccp:tabbedMenu text="Logins" key="logins" value="languages" url="<%= loginsUrl %>"/>
    <ccp:tabbedMenu text="Languages" key="languages" value="languages" url="<%= languagesUrl %>"/>
    <ccp:tabbedMenu text="Web Sites" key="web-sites" value="languages" url="<%= webSitesUrl %>"/>
    <td width="100%" style="background-image: none; background-color: transparent; border: 0; border-bottom: 1px solid #666; cursor: default;">&nbsp;</td>
  </tr>
</table>
</div>
<%= showError(request, "actionError") %>
Check or uncheck the items to make changes immediately.<br />
<table class="pagedList">
  <thead>
    <tr>
      <th width="100%">Language</th>
      <th width="8" nowrap>Maintainer</th>
      <th width="8" nowrap>Translator</th>
      <th width="8" nowrap>Reviewer</th>
      <th width="8" nowrap>Member Date</th>
    </tr>
  </thead>
  <tbody>
    <%
      if (languagePackList.size() == 0) {
    %>
      <tr class="row2">
        <td colspan="5">No languages to display.</td>
      </tr>
    <%
      }
      int rowid = 0;
      int count = 0;
      Iterator i = languagePackList.iterator();
      while (i.hasNext()) {
        rowid = (rowid != 1?1:2);
        LanguagePack thisLanguage = (LanguagePack) i.next();
        LanguageTeam thisMember = thisLanguage.getTeamList().getMember(thisUser.getId());
        ++count;
    %>
      <tr class="row<%= rowid %>" onmouseover="swapClass(this,'rowHighlight')" onmouseout="swapClass(this,'row<%= rowid %>')">
        <td valign="top">
          <%= toHtml(thisLanguage.getLanguageName()) %>
        </td>
        <td valign="top" align="center" nowrap>
          <input type="checkbox" name="maintainer<%= thisLanguage.getId() %>" value="ON" onClick="setLanguageMember(this)"
            <ccp:evaluate if="<%= thisLanguage.getMaintainerId() == thisUser.getId() %>">checked</ccp:evaluate>
          />
        </td>
        <td valign="top" align="center" nowrap>
          <input type="checkbox" name="translator<%= thisLanguage.getId() %>" value="ON" onClick="setLanguageMember(this)"
            <ccp:evaluate if="<%= thisMember != null && thisMember.getAllowTranslate() %>">checked</ccp:evaluate>
          />
        </td>
        <td valign="top" align="center" nowrap>
          <input type="checkbox" name="reviewer<%= thisLanguage.getId() %>" value="ON" onClick="setLanguageMember(this)"
            <ccp:evaluate if="<%= thisMember != null && thisMember.getAllowReview() %>">checked</ccp:evaluate>
          />
        </td>
        <td valign="top" align="center" nowrap>
          <ccp:evaluate if="<%= thisMember != null %>"><ccp:tz timestamp="<%= thisMember.getEntered() %>" dateOnly="true" default="&nbsp;"/></ccp:evaluate>
          <ccp:evaluate if="<%= thisMember == null %>">&nbsp;</ccp:evaluate>
        </td>

      </tr>
    <%
      }
    %>
  </tbody>
</table>
<%-- end container content --%>
<iframe src="<%= RequestUtils.getAbsoluteServerUrl(request) %>/empty.html" name="server_commands" id="server_commands" style="visibility:hidden" height="0"></iframe>
