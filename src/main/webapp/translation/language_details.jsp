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
<%@ page import="java.util.*,java.text.DateFormat" %>
<%@ page
    import="com.concursive.connect.web.modules.translation.dao.LanguageTeam" %>
<jsp:useBean id="languagePack" class="com.concursive.connect.web.modules.translation.dao.LanguagePack" scope="request"/>
<jsp:useBean id="User" class="com.concursive.connect.web.modules.login.dao.User" scope="session"/>
<%@ include file="../initPage.jsp" %>
<a href="<%= ctx %>/Translation.do">Languages</a> >
<%= toHtml(languagePack.getLanguageName()) %><br />
<br />

<table cellpadding="0" cellspacing="0" border="0" width="100%">
<tr>
<td>

<table cellpadding="0" cellspacing="0" border="0" width="100%" height="100%">
  <tr>
    <td width="50%" style="padding-right: 4px" valign="top" height="100%">
      <table border="0" cellpadding="0" cellspacing="0" width="100%" height="100%" class="centricBox">
        <tr>
          <th height="20">Language Pack Details</th>
        </tr>
        <tr>
          <td height="100%" style="padding: 4px 4px 4px 4px" valign="top" class="empty">
            <table border="0" cellpadding="2" cellspacing="2" width="100%" height="100%">
              <tr>
                <td valign="top" nowrap align="right">
                  Maintained by:
                </td>
                <td valign="top">
                  <ccp:username id="<%= languagePack.getMaintainerId() %>"/>
                </td>
              </tr>
              <tr>
                <td valign="top" nowrap align="right">
                  Phrases translated:
                </td>
                <td valign="top">
                  <%= languagePack.getDictionaryTranslatedCount() %>
                </td>
              </tr>
              <tr>
                <td valign="top" nowrap align="right">
                  Phrases in dictionary:
                </td>
                <td valign="top">
                  <%= languagePack.getDictionaryItemCount() %>
                </td>
              </tr>
              <tr>
                <td valign="top" nowrap align="right">
                  Last modified:
                </td>
                <td valign="top">
                  <ccp:tz timestamp="<%= languagePack.getModified() %>" />
                </td>
              </tr>
            </table>
          </td>
        </tr>
      </table>
    </td>

    <td width="50%" style="padding-right: 4px" valign="top" height="100%">
      <table border="0" cellpadding="0" cellspacing="0" width="100%" height="100%" class="centricBox">
        <tr>
          <th height="20">Members</th>
        </tr>
        <tr>
          <td height="100%" style="padding: 4px 4px 4px 4px" valign="top" class="empty">
            <table border="0" cellpadding="2" cellspacing="2" width="100%" height="100%">
<%
    Iterator i = languagePack.getTeamList().iterator();
    while (i.hasNext()) {
      LanguageTeam thisMember = (LanguageTeam) i.next();
%>
              <tr>
                <td nowrap>
                  <ccp:username id="<%= thisMember.getMemberId() %>"/>
                </td>
              </tr>
<%
    }
%>
              <tr>
                <td>&nbsp;</td>
              </tr>
            </table>
          </td>
        </tr>
      </table>
    </td>
  </tr>
</table>

</td>
</tr>
</table>
<br />
<input type="button" value="Review phrases" onclick="window.location.href='<%= ctx %>/Translation.do?command=Translate&popup=true&languageId=<%= languagePack.getId() %>'">
<ccp:evaluate if="<%= languagePack.allowsTranslation(User.getId()) %>">
  <input type="button" value="Translate phrases" onclick="window.location.href='<%= ctx %>/Translation.do?command=Translate&popup=true&action=Translate&languageId=<%= languagePack.getId() %>'">
</ccp:evaluate>
<ccp:evaluate if="<%= languagePack.allowsApproval(User.getId()) %>">
  <input type="button" value="Approve phrases" onclick="window.location.href='<%= ctx %>/Translation.do?command=Translate&popup=true&action=Approve&languageId=<%= languagePack.getId() %>'">
</ccp:evaluate>
<input type="button" value="Search phrases" onclick="window.location.href='<%= ctx %>/Translation.do?command=SearchForm&popup=true&languageId=<%= languagePack.getId() %>'">
