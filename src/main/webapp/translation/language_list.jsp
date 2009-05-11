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
<%@ page import="java.util.*" %>
<%@ page
    import="com.concursive.connect.web.modules.translation.dao.LanguagePack" %>
<jsp:useBean id="languagePackList" class="com.concursive.connect.web.modules.translation.dao.LanguagePackList" scope="request"/>
<%@ include file="../initPage.jsp" %>
<table cellpadding="0" cellspacing="0" border="0" width="100%" height="100%">
  <tr>
    <%-- column 1 --%>
    <td width="50%" style="padding-right: 4px" valign="top" height="100%">
      <table border="0" cellpadding="0" cellspacing="0" width="100%" height="100%" class="centricBox">
        <tr>
          <th height="20">The ConcourseSuite Language Gateway</th>
        </tr>
        <tr>
          <td height="100%" style="padding: 4px 4px 4px 4px" valign="top">
            <b>Translating ConcourseSuite into new languages is now easier than ever via the online translation gateway.</b><br >
            <br />
            The Translation Gateway provides a web-based tool that allows one person or a group of people to collaborate on a translation.  You can ask to join a translation project already in progress or you can begin a new translation on your own.  And translations can be done in a day or over a weekend.<br />
            <br />
            Translations are shepherded by &quot;maintainers&quot; who may decide to work by themselves or work with others.  We accept applications for those who want to become &quot;maintainers&quot; and the application process is informal and takes only a few minutes.<br />
            <br />
            The minimum requirement is that you have a valid e-mail address so we can communicate with you.  We also take into account your activity in our community, your experience, if any, with the product, whether you're part of an organization (hopefully that at least has a website), and other stuff like that.  Becoming a &quot;maintainer&quot; doesn't cost anything.<br />
            <br />
            <b>Frequently Asked Questions:</b><br />
            <br />
            <i>Why become a &quot;maintainer?&quot;</i><br />
            <br />
            The short answer is you like ConcourseConnect but need it in another language.  As well, if your core business is aided by additional visibility, then a good translation and the traffic that surrounds it will likely bring you business.<br />
            <br />
            <i>What about translated &quot;Help&quot; and &quot;User Documentation?&quot;</i><br />
          </td>
        </tr>
      </table>
      <br />
      <table border="0" cellpadding="0" cellspacing="0" width="100%" height="100%" class="centricBox">
        <tr>
          <th height="20">Peer-Review Process</th>
        </tr>
        <tr>
          <td height="100%" style="padding: 4px 4px 4px 4px" valign="top" class="empty">
            Translations come in 3 flavors:  &quot;Not-reviewed&quot;,  &quot;Reviewed&quot;, and &quot;Community Approved&quot;.<br />
            <br />
            &quot;Not-reviewed&quot; means the &quot;maintainer&quot; and his/her team have translated the words but have not, to their own satisfaction, reviewed them to their own internal satisfaction.  &quot;Reviewed&quot; means the &quot;maintainer&quot; has reviewed the terms and accepted them as acceptable by their own standards.  &quot;Community Approved&quot; means that the community has found the translation to be acceptable after an appropriate peer-review period.
          </td>
        </tr>
      </table>
    </td>
    <%-- column 2 --%>
    <td width="50%" style="padding-right: 4px" valign="top" height="100%">
      <table cellpadding="0" cellspacing="0" border="0" width="100%" height="100%">
        <tr>
          <td valign="top" style="padding-bottom: 4px">
            <table border="0" cellpadding="0" cellspacing="0" width="100%" class="centricBox">
              <tr>
                <th height="20">Languages</th>
              </tr>
              <tr>
                <td height="100%" style="padding: 4px 4px 4px 4px" valign="top" class="empty">
                  <table border="0" cellpadding="2" cellspacing="2" width="100%">

<%
    Iterator i = languagePackList.iterator();
    while (i.hasNext()) {
      LanguagePack thisLanguage = (LanguagePack) i.next();
%>
                    <tr>
                      <td nowrap valign="top">
                        <a href='<%= ctx %>/Translation.do?command=Language&languageId=<%= thisLanguage.getId() %>'><%= toHtml(thisLanguage.getLanguageName()) %></a>
                      </td>
                      <td nowrap valign="top" align="right">
                        (<%= toHtml(thisLanguage.getPercentageComplete()) %>)
                      </td>
                      <td valign="top">
                        <ccp:username id="<%= thisLanguage.getMaintainerId() %>" />
                      </td>
                    </tr>
<%
    }
%>
                  </table>
                  <br />
                  <input type="button" name="translator" value="Apply to become a Translator" onclick="window.location.href='<%= ctx %>/TranslationApplication.do?command=Apply';" />
                </td>
              </tr>
            </table>
          </td>
        </tr>
      </table>
    </td>
  </tr>
</table>
