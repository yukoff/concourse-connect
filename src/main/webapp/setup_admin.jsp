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
<jsp:useBean id="applicationPrefs" class="com.concursive.connect.config.ApplicationPrefs" scope="application"/>
<jsp:useBean id="adminBean" class="com.concursive.connect.web.modules.login.dao.User" scope="request"/>
<jsp:useBean id="countries" class="com.concursive.connect.web.utils.CountrySelect" scope="request"/>
<%@ include file="initPage.jsp" %>
<form action="${ctx}/SetupAdmin.do?command=SaveAdmin" name="setupForm" method="post">
  <input type="hidden" name="auto-populate" value="true" />
  <div style="width:100%; height:100%; background:#efefef; padding:15px 0 0 0; margin:0">
    <div style="width:600px; height:600px; position:relative; margin:0 auto">
      <img src="${ctx}/images/setup/step-5-header.jpg" alt="Step 5" style="margin:0 auto; display:block">
      <br style="clear:both">
      <h1 style="color:#425c61; font-size:x-large; margin:35px 0">Create the administrator account</h1>
      <b><%= showError(request, "actionError", false) %></b>
      <div class="formContainer" style="font-size:small; width:500px; margin:0 auto; background:#fff; -moz-border-radius:10px; -webkit-border-radius:10px; border-color:#acacac #cacaca #d4d4d4 #aeaeae; border-width:2px; border-style:solid; padding:15px">
        <fieldset style="border:none">
          <legend style="padding:0">Administrator Details</legend>
          <p>The administrator will appear as a user of the system and a user profile will be created.</p>
          <label><font color="red">*</font>Email Address</label>
          <b><%= showAttribute(request, "emailError") %></b>
          <input type="text" name="email" value="<%= toHtmlValue(adminBean.getEmail()) %>" size="30" />
          <label><font color="red">*</font>First Name</label>
          <b><%= showAttribute(request, "firstNameError") %></b>
          <input type="text" name="firstName" value="<%= toHtmlValue(adminBean.getFirstName()) %>" size="30" />
          <label><font color="red">*</font>Last Name</label>
          <b><%= showAttribute(request, "lastNameError") %></b>
          <input type="text" name="lastName" value="<%= toHtmlValue(adminBean.getLastName()) %>" size="30" />
          <label>Organization Name</label>
          <b><%= showAttribute(request, "companyError") %></b>
          <input type="text" name="company" value="<%= toHtmlValue(adminBean.getCompany()) %>" size="30" />
          <label><font color="red">*</font>Country</label>
          <b><%= showAttribute(request, "countryError") %></b>
          <%= countries.getHtml("country", adminBean.getCountry()) %>
          <label>City</label>
          <b><%= showAttribute(request, "cityError") %></b>
          <input type="text" name="city" id="city" value="<%= toHtmlValue(adminBean.getCity()) %>">
          <label>State</label>
          <b><%= showAttribute(request, "stateError") %></b>
          <input type="text" name="state" id="state" value="<%= toHtmlValue(adminBean.getState()) %>">
          <label>Postal Code</label>
          <b><%= showAttribute(request, "postalCodeError") %></b>
          <input type="text" name="postalCode" id="postalCode" value="<%= toHtmlValue(adminBean.getPostalCode()) %>">
          <label><font color="red">*</font>Password</label>
          <b><%= showAttribute(request, "password1Error") %></b>
          <input type="password" name="password1" size="30" />
          <label><font color="red">*</font>Password (again)</label>
          <b><%= showAttribute(request, "password2Error") %></b>
          <input type="password" name="password2" size="30" />
        </fieldset>
      </div>
      <input type="submit" value="Continue" style="float:right; margin-top:10px" />
    </div>
  </div>
</form>
