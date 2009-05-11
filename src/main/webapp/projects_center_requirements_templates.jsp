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
<jsp:useBean id="project" class="com.concursive.connect.web.modules.profile.dao.Project" scope="request"/>
<%@ include file="initPage.jsp" %>
<table class="containerNote" cellspacing="0">
<tr>
  <th>
    <img src="<%= ctx %>/images/icons/stock_about-16.gif" border="0" align="absmiddle" />
  </th>
  <td>
    <ccp:label name="projectsCenterRequirements.templates.message">
    These templates are designed to be used for working on an outline in an
    offline environment.  They also provide flexibility by leveraging desktop applications
    designed for working with outlines.<br />
    <br />
    Once complete, follow the directions to import the outline.<br />
    <br />
    To specify assigned users, enter the user's id which can be seen on the
    &quot;Team&quot; tab of this project.  Use a comma when specifying more than
    one.
    </ccp:label>
  </td>
</tr>
</table>
<table class="pagedList">
  <thead>
    <tr>
      <th width="8"><ccp:label name="projectsCenterRequirements.templates.action">Action</ccp:label></th>
      <th width="100%"><ccp:label name="projectsCenterRequirements.templates.description">Description</ccp:label></th>
    </tr>
  </thead>
  <tbody>
    <tr class="row1">
      <td valign="top" align="center" nowrap>
        <a href="<%= ctx %>/resources/Plan%20Outline%20Template.xlt"><ccp:label name="projectsCenterRequirements.templates.download">Download</ccp:label></a>
      </td>
      <td valign="top">
        <img src="<%= ctx %>/images/mime/gnome-application-vnd.ms-excel-23.gif" align="absMiddle" />
        <ccp:label name="projectsCenterRequirements.templates.excelPlanDescription">
        Plan Outline Template (Excel) - 19 KB<br />
        Usage: Download this template, open it in Microsoft Excel, add assignments, then
        save the outline in Excel format with a .xls extension.
        Import the outline from the &quot;Plan&quot; tab, by
        choosing Import from the drop-down menu of the target Outline.
        </ccp:label>
      </td>
    </tr>
    <tr class="row1">
      <td valign="top" align="center" nowrap>
        <a href="<%= ctx %>/resources/Plan%20Outline%20Template.oo3template"><ccp:label name="projectsCenterRequirements.templates.download">Download</ccp:label></a>
      </td>
      <td valign="top">
        <img src="<%= ctx %>/images/mime/gnome-text-plain-23.gif" align="absMiddle" />
        <ccp:label name="projectsCenterRequirements.templates.omniOutliner30PlanDescription">
        Plan Outline Template (OmniOutliner 3.0) - 1 KB<br />
        Usage: Download this template, open it in The Omni Group's OmniOutliner,
        add assignments, then save the outline in OmniOutliner's standard oo3
        format (compressed or uncompressed).
        Import the outline from the &quot;Plan&quot; tab, by
        choosing Import from the drop-down menu of the target Outline.
        </ccp:label>
      </td>
    </tr>
    <tr class="row1">
      <td valign="top" align="center" nowrap>
        <a href="<%= ctx %>/resources/Plan%20Outline%20Template.ooutline"><ccp:label name="projectsCenterRequirements.templates.download">Download</ccp:label></a>
      </td>
      <td valign="top">
        <img src="<%= ctx %>/images/mime/gnome-text-plain-23.gif" align="absMiddle" />
        <ccp:label name="projectsCenterRequirements.templates.omniOutliner20PlanDescription">
        Plan Outline Template (OmniOutliner 2.0) - 13 KB<br />
        Usage: Download this template, open it in The Omni Group's OmniOutliner,
        add assignments, then export the outline in OmniOutliner's .xmloutline
        format.
        Import the outline from the &quot;Plan&quot; tab, by
        choosing Import from the drop-down menu of the target Outline.
        </ccp:label>
      </td>
    </tr>
    <tr class="row1">
      <td valign="top" align="center" nowrap>
        <ccp:label name="projectsCenterRequirements.templates.reference">Reference</ccp:label>
      </td>
      <td valign="top">
        <img src="<%= ctx %>/images/mime/gnome-text-plain-23.gif" align="absMiddle" />
        <ccp:label name="projectsCenterRequirements.templates.microsoftProject">
        Microsoft Project files; including .mpp, .mpx, .xml, .mspdi, .mpt
        </ccp:label>
      </td>
    </tr>
  </tbody>
</table>
