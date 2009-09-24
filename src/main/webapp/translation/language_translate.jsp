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
    import="com.concursive.connect.web.modules.translation.dao.LanguageDictionary" %>
<jsp:useBean id="languagePack" class="com.concursive.connect.web.modules.translation.dao.LanguagePack" scope="request"/>
<jsp:useBean id="defaultLanguagePack" class="com.concursive.connect.web.modules.translation.dao.LanguagePack" scope="request"/>
<jsp:useBean id="languageDictionaryList" class="com.concursive.connect.web.modules.translation.dao.LanguageDictionaryList" scope="request"/>
<jsp:useBean id="dictionaryListInfo" class="com.concursive.connect.web.utils.PagedListInfo" scope="session"/>
<jsp:useBean id="User" class="com.concursive.connect.web.modules.login.dao.User" scope="session"/>
<%
  String currentAction = request.getParameter("action");
  if ("Search".equals(currentAction)) {
    if (languagePack.allowsTranslation(User.getId())) {
      currentAction = "Translate";
    }
  }
%>
<%@ include file="../initPage.jsp" %>
<ccp:evaluate if="<%= languagePack.allowsTranslation(User.getId()) || languagePack.allowsApproval(User.getId()) %>">
<script language="javascript">
  function init() {
    var frm = document.forms['addParams'];
    var len = document.forms['addParams'].elements.length;
    var i=0;
    for( i=0 ; i<len ; i++) {
      if (frm.elements[i].name.indexOf('paramValue') != -1) {
        frm.elements[i].focus();
        break;
      }
    }
  }
  function checkForm(form) {
    if (form.buttonNext.value != 'Please Wait...') {
      form.buttonNext.value='Please Wait...';
      form.buttonNext.disabled = true;
      return true;
    } else {
      return false;
    }
  }
</script>
<body onLoad="init();">
<form name="addParams" action="<%= ctx %>/Translation.do?command=Update" method="post" onSubmit="return checkForm(this);">
</ccp:evaluate>
<input type="hidden" name="popup" value="<%= toHtmlValue(request.getParameter("popup")) %>" />
<a href="<%= ctx %>/Translation.do">Languages</a> >
<a href="<%= ctx %>/Translation.do?command=Language&languageId=<%= languagePack.getId() %>"><%= toHtml(languagePack.getLanguageName()) %></a> >
<ccp:evaluate if="<%= \"Search\".equals(request.getParameter(\"action\")) %>">
  <a href="<%= ctx %>/Translation.do?command=SearchForm&popup=true&languageId=<%= languagePack.getId() %>">Search</a> >
</ccp:evaluate>
Details
<ccp:pagedListStatus title="<%= showError(request, \"actionError\") %>" object="dictionaryListInfo"/>
<table cellpadding="4" cellspacing="0" border="0" width="100%" class="centricBox">
   <tr>
      <th align="left" nowrap>Reference Id</th>
      <th style="text-align: center !important" width="48%">
        <strong><%= toHtml(defaultLanguagePack.getLanguageName()) %></strong>
      </th>
      <th style="text-align: center !important">
        <strong><%= toHtml(languagePack.getLanguageName()) %></strong>
      </th>
      <ccp:evaluate if="<%= \"Approve\".equals(request.getParameter(\"action\")) %>">
        <th><strong>Status</strong></th>
      </ccp:evaluate>
   </tr>
   <%
    int rowid = 0;
    Iterator i = languageDictionaryList.iterator();
    while (i.hasNext()) {
      rowid = (rowid != 1?1:2);
      LanguageDictionary dictionaryItem = (LanguageDictionary) i.next();
   %>
    <tr class="row<%= rowid %>">
      <input type="hidden" name="paramId<%= dictionaryItem.getId() %>" value="<%= dictionaryItem.getId() %>">
      <td align="center">
        <a href="javascript:popURL('<%= ctx %>/Translation.do?command=Context&languageId=<%= languagePack.getId() %>&id=<%= dictionaryItem.getId() %>&popup=true','Translation_Context','600','300','yes','yes');"><%= dictionaryItem.getId() %></a>
      </td>
      <ccp:evaluate if="<%= !hasReturn(dictionaryItem.getDefaultValue()) %>">
        <td align="right">
          <%= toHtml(dictionaryItem.getDefaultValue()) %>
        </td>
        <td>
          <input type="text" name="paramValue<%= dictionaryItem.getId() %>" size="50" value="<%= toHtmlValue(dictionaryItem.getParamValue1()) %>">
          <ccp:evaluate if="<%= hasText((String) request.getAttribute(dictionaryItem.getId() + \"Error\")) %>">
            <%= showAttribute(request, "paramValue" + dictionaryItem.getId() + "Error") %>
          </ccp:evaluate>
        </td>
      </ccp:evaluate>
      <ccp:evaluate if="<%= hasReturn(dictionaryItem.getDefaultValue()) %>">
        <td valign="top">
          <%--<%= toHtml(dictionaryItem.getDefaultValue()) %>--%>
          <textarea cols="49" rows="8" wrap="off"><%= toString(dictionaryItem.getDefaultValue()).trim() %></textarea>
        </td>
        <td>
          <textarea name="paramValue<%= dictionaryItem.getId() %>" cols="49" rows="8" wrap="off"><%= toString(dictionaryItem.getParamValue1()) %></textarea>
          <ccp:evaluate if='<%= hasText((String) request.getAttribute(dictionaryItem.getId() + "Error")) %>'>
            <br />
            <%= showAttribute(request, "paramValue" + dictionaryItem.getId() + "Error") %>
          </ccp:evaluate>
        </td>
      </ccp:evaluate>
      <ccp:evaluate if="<%= \"Approve\".equals(request.getParameter(\"action\")) %>">
        <td>
          <select name="approved<%= dictionaryItem.getId() %>">
            <option value="-1" <%= (dictionaryItem.getApproved() == -1) ? "selected":"" %>>Undecided</option>
            <option value="1" <%= (dictionaryItem.getApproved() == 1) ? "selected":"" %>>Approved</option>
            <option value="2" <%= (dictionaryItem.getApproved() == 2) ? "selected":"" %>>Not Approved</option>
          </select>
        </td>
      </ccp:evaluate>
    </tr>
   <%
    }
   %>
   <ccp:evaluate if="<%= languageDictionaryList.size() == 0 %>">
    <tr><td colspan="<%= "Approve".equals(request.getParameter("action")) ? 4 : 3 %>">No records pending for translation</td></tr>
   </ccp:evaluate>
</table>
&nbsp;<br />
<input type="hidden" name="languageId" value="<%= languagePack.getId() %>">
<input type="hidden" name="action" value="<%= toHtmlValue(request.getParameter("action")) %>">
<input type="hidden" name="offset" value="<%= dictionaryListInfo.getCurrentOffset() + dictionaryListInfo.getItemsPerPage() %>">
<ccp:evaluate if="<%= languageDictionaryList.size() > 0 %>">
  <ccp:evaluate if="<%= \"Translate\".equals(currentAction) %>">
    <input type="hidden" name="source" value="Translate">
    <ccp:evaluate if="<%= languagePack.allowsTranslation(User.getId()) %>">
      <center>
        <input type="button" value="< Previous" <%= (dictionaryListInfo.getCurrentOffset() <= 0 ? "disabled" : "") %> onclick="window.location.href='<%= ctx %>/Translation.do?command=Translate&languageId=<%= languagePack.getId() %>&action=<%= StringUtils.encodeUrl(request.getParameter("action")) %>&popup=true&offset=<%= (dictionaryListInfo.getCurrentOffset() - dictionaryListInfo.getItemsPerPage()) %>'"/>
        <input type="submit" name="buttonNext" value="Next >" />
      </center>
    </ccp:evaluate>
  </ccp:evaluate>
  <ccp:evaluate if="<%= \"Approve\".equals(currentAction) %>">
    <input type="hidden" name="source" value="Approve">
    <ccp:evaluate if="<%= languagePack.allowsApproval(User.getId()) %>">
      <center>
        <input type="button" value="< Previous" <%= (dictionaryListInfo.getCurrentOffset() <= 0 ? "disabled" : "") %> onclick="window.location.href='<%= ctx %>/Translation.do?command=Translate&languageId=<%= languagePack.getId() %>&action=<%= StringUtils.encodeUrl(request.getParameter("action")) %>&popup=true&offset=<%= (dictionaryListInfo.getCurrentOffset() - dictionaryListInfo.getItemsPerPage()) %>'"/>
        <input type="submit" name="buttonNext" value="Next >" />
      </center>
    </ccp:evaluate>
  </ccp:evaluate>
</ccp:evaluate>
<ccp:evaluate if="<%= languagePack.allowsTranslation(User.getId()) || languagePack.allowsApproval(User.getId()) %>">
</form>
</body>
</ccp:evaluate>