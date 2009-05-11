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
<%@ page import="com.concursive.connect.web.modules.classifieds.dao.Classified" %>
<jsp:useBean id="project" class="com.concursive.connect.web.modules.profile.dao.Project" scope="request"/>
<jsp:useBean id="classifiedList" class="com.concursive.connect.web.modules.classifieds.dao.ClassifiedList" scope="request"/>
<jsp:useBean id="projectClassifiedsInfo" class="com.concursive.connect.web.utils.PagedListInfo" scope="session"/>
<jsp:useBean id="classifiedCategoryList" class="com.concursive.connect.web.modules.classifieds.dao.ClassifiedCategoryList" scope="request"/>
<%@ include file="initPage.jsp" %>
<%-- begin ads --%>
<%
  Iterator i = classifiedList.iterator();
  while (i.hasNext()) {
    Classified thisClassified = (Classified) i.next();
%>
<div id="newsContainer">
<table cellpadding="4" cellspacing="0" width="100%" class="newsContainer">
  <tr class="newsArticle">
    <th width="100%">
      <table border="0" cellpadding="0" cellspacing="0" width="100%">
        <tr>
          <td nowrap="" width="100%">
            <div style="vertical-align:baseline;">
              <a style="color: #000066; font-size: 14px; font-weight: bold; padding-right: 5px; text-decoration:none;" href="javascript:popURL('<%= ctx %>/ProjectManagementClassifieds.do?command=Details&pid=<%= project.getId() %>&id=<%= thisClassified.getId() %>&popup=true','Classified_Detail','600','500','yes','yes');"><%= toHtml(thisClassified.getTitle()) %></a>
              <font color="red">
              <ccp:evaluate if="<%= thisClassified.getPublishDate() == null %>"><ccp:label name="projectsCenterClassifieds.byClassified.draft">(Draft)</ccp:label></ccp:evaluate>
              <ccp:evaluate if="<%= thisClassified.getPublishDate() != null %>"><ccp:label name="projectsCenterClassifieds.byClassified.approved">(Approved)</ccp:label></ccp:evaluate>&nbsp;
              </font>
            </div>
          </td>
          <td align="right" nowrap>
            <ccp:evaluate if="<%= thisClassified.getPublishDate() == null%>">
	            <ccp:permission name="project-classifieds-add">
	              <%-- edit classified --%>
	              <a href="<%= ctx %>/ProjectManagementClassifieds.do?command=Edit&pid=<%= project.getId() %>&id=<%= thisClassified.getId() %>"><img src="<%= ctx %>/images/icons/stock_edit-16.gif" border="0" align="absmiddle" title="<ccp:label name="projectsCenterClassifieds.byClassified.editThisItem">Edit this item</ccp:label>"></a>
	            </ccp:permission>
	        </ccp:evaluate>
            <ccp:evaluate if="<%= thisClassified.getPublishDate() != null%>">
	            <ccp:permission name="project-classifieds-admin">
	              <%-- edit classified --%>
	              <a href="<%= ctx %>/ProjectManagementClassifieds.do?command=Edit&pid=<%= project.getId() %>&id=<%= thisClassified.getId() %>"><img src="<%= ctx %>/images/icons/stock_edit-16.gif" border="0" align="absmiddle" title="<ccp:label name="projectsCenterClassifieds.byClassified.editThisItem">Edit this item</ccp:label>"></a>
	           </ccp:permission>
	         </ccp:evaluate>
            <ccp:permission name="project-classifieds-add">
              <%-- clone classified --%>
              <a href="javascript:confirmForward('<%= ctx %>/ProjectManagementClassifieds.do?command=Clone&pid=<%= project.getId() %>&id=<%= thisClassified.getId() %>');"><img src="<%= ctx %>/images/icons/stock_copy-16.gif" border="0" align="absmiddle" title="<ccp:label name="projectsCenterClassifieds.byClassified.makeACopyOfThisItem">Make a copy of this item</ccp:label>"></a>
            </ccp:permission>
            <ccp:evaluate if="<%= thisClassified.getPublishDate() == null%>">
            <ccp:permission name="project-classifieds-add">
              <%-- delete classified --%>
              <a href="javascript:confirmDelete('<%= ctx %>/ProjectManagementClassifieds.do?command=Delete&pid=<%= project.getId() %>&id=<%= thisClassified.getId() %>');"><img src="<%= ctx %>/images/icons/stock_delete-16.gif" border="0" align="absmiddle" title="<ccp:label name="projectsCenterClassifieds.byClassified.deleteThisItem">Delete this item</ccp:label>"></a>
            </ccp:permission>
	        </ccp:evaluate>
            <ccp:evaluate if="<%= thisClassified.getPublishDate() != null%>">
	            <ccp:permission name="project-classifieds-admin">
	              <%-- delete classified --%>
	              <a href="javascript:confirmDelete('<%= ctx %>/ProjectManagementClassifieds.do?command=Delete&pid=<%= project.getId() %>&id=<%= thisClassified.getId() %>');"><img src="<%= ctx %>/images/icons/stock_delete-16.gif" border="0" align="absmiddle" title="<ccp:label name="projectsCenterClassifieds.byClassified.deleteThisItem">Delete this item</ccp:label>"></a>
	            </ccp:permission>
	         </ccp:evaluate>
            <ccp:rating id='<%= thisClassified.getId() %>'
                           showText='false'
                           count='<%= thisClassified.getRatingCount() %>'
                           value='<%= thisClassified.getRatingValue() %>'
                           url='<%= ctx + "/ProjectManagementClassifieds.do?command=SetRating&pid=" + thisClassified.getProjectId() + "&id=" + thisClassified.getId() + "&v=${vote}&out=text" %>'/>
          </td>
        </tr>
        <tr>
          <td nowrap="" colspan="2">
            <div style="color:#999999; font-size: .8em;">
              <ccp:tz timestamp="<%= thisClassified.getPublishDate() %>" dateFormat="<%= DateFormat.LONG %>" />
              by <ccp:username id="<%= thisClassified.getEnteredBy() %>"/>
            </div>
          </td>
        </tr>
      </table>
    </th>
  </tr>
  <tr>
    <td>
      <div>
      <%= toHtml(thisClassified.getTitle()) %>
      <ccp:evaluate if="<%= hasText(thisClassified.getDescription()) %>">
        <br />
        <a style="color:#000066; text-decoration:none;" href="javascript:popURL('<%= ctx %>/ProjectManagementClassifieds.do?command=Details&pid=<%= project.getId() %>&id=<%= thisClassified.getId() %>&popup=true','Classified_Detail','600','500','yes','yes');">Read more &raquo;</a>
      </ccp:evaluate>
      </div>
    </td>
  </tr>
</table>
</div>
<br />
<%
  }
%>

