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
<%@ page import="com.concursive.connect.web.utils.HtmlSelectChoice" %>
<jsp:useBean id="languagePack" class="com.concursive.connect.web.modules.translation.dao.LanguagePack" scope="request"/>
<jsp:useBean id="defaultLanguagePack" class="com.concursive.connect.web.modules.translation.dao.LanguagePack" scope="request"/>
<jsp:useBean id="User" class="com.concursive.connect.web.modules.login.dao.User" scope="session"/>
<jsp:useBean id="translationSearchForm" class="com.concursive.connect.web.modules.translation.beans.TranslationSearchBean" scope="session"/>
<%@ include file="../initPage.jsp" %>
<script language="JavaScript">
  function clearForm() {
    document.forms['searchForm'].translatedWord.value="";
    <%--
    document.forms['searchForm'].defaultWord.value="";
    --%>
    document.forms['searchForm'].languageParam.value="";
    document.forms['searchForm'].translatedWord.focus();
  }
</script>
<body onLoad="javascript:document.searchForm.translatedWord.focus()">
  <form name="searchForm" action="<%= ctx %>/Translation.do?command=Translate&popup=true&auto-populate=true&action=Search&languageId=<%= languagePack.getId() %>" method="post">
    <a href="<%= ctx %>/Translation.do">Languages</a> >
    <a href="<%= ctx %>/Translation.do?command=Language&languageId=<%= languagePack.getId() %>"><%= toHtml(languagePack.getLanguageName()) %></a> >
    Search<br />
    <br />
    <table class="pagedList">
      <thead>
        <tr>
          <th colspan="2">
            Search Language Dictionary
          </th>
        </tr>
      </thead>
      <tbody>
        <tr class="containerBody">
          <td nowrap class="formLabel"><%= toHtml(languagePack.getLanguageName()) %> Phrase</td>
          <td>
            <input type="text" name="translatedWord" value="<%= toHtmlValue(translationSearchForm.getTranslatedWord()) %>" />
          </td>
        </tr>
        <%--
        <tr class="containerBody">
          <td nowrap class="formLabel"><%= defaultLanguagePack.getLanguageName() %> Phrase</td>
          <td>
            <input type="text" name="defaultWord" value="<%= toHtmlValue(translationSearchForm.getDefaultWord()) %>" />
          </td>
        </tr>
        --%>
        <tr class="containerBody">
          <td nowrap class="formLabel">Parameter</td>
          <td>
            <input type="text" name="languageParam" value="<%= toHtmlValue(translationSearchForm.getLanguageParam()) %>" />
          </td>
        </tr>
      </tbody>
    </table>
    <input type="submit" name="Search" value="Search" />
    <input type="button" name="Clear" value="Clear" onClick="clearForm()" />
  </form>
</body>

