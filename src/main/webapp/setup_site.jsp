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
<jsp:useBean id="siteBean" class="com.concursive.connect.web.modules.setup.beans.SetupSiteBean" scope="request"/>
<%@ include file="initPage.jsp" %>
<form action="${ctx}/SetupSite.do?command=SaveSite" name="setupForm" method="post">
  <input type="hidden" name="auto-populate" value="true" />
  <div style="width:100%; height:100%; background:#efefef; padding:15px 0 0 0; margin:0">
    <div style="width:600px; height:600px; position:relative; margin:0 auto">
      <img src="${ctx}/images/setup/step-5-header.jpg" alt="Step 5" style="margin:0 auto; display:block">
      <br style="clear:both">
      <h1 style="color:#425c61; font-size:x-large; margin:35px 0">Create the name of the site</h1>
      <b><%= showError(request, "actionError", false) %></b>
      <%-- Site Details --%>
      <div class="formContainer" style="font-size:small; width:500px; margin:0 auto; background:#fff; -moz-border-radius:10px; -webkit-border-radius:10px; border-color:#acacac #cacaca #d4d4d4 #aeaeae; border-width:2px; border-style:solid; padding:15px">
        <fieldset style="border:none">
          <legend style="padding:0">Site Details</legend>
          <p>The name of the site will appear throughout.  The name can be modified later.</p>
          <label><font color="red">*</font>What is the site called?</label>
          <b><%= showAttribute(request, "titleError") %></b>
          <input type="text" name="title" value="<%= toHtmlValue(siteBean.getTitle()) %>" size="100" />

          <label><font color="red">*</font>What is the description of the site, as seen by users?</label>
          <b><%= showAttribute(request, "shortDescriptionError") %></b>
          <input type="text" name="shortDescription" value="<%= toHtmlValue(siteBean.getShortDescription()) %>" size="255" />

          <label><font color="red">*</font>Keywords for the site (comma-separated)</label>
          <b><%= showAttribute(request, "keywordsError") %></b>
          <input type="text" name="keywords" value="<%= toHtmlValue(siteBean.getKeywords()) %>" size="30" />
        </fieldset>
      </div>

      <%-- Site Purpose --%>
      <div class="formContainer" style="font-size:small; width:500px; margin:0 auto; background:#fff; -moz-border-radius:10px; -webkit-border-radius:10px; border-color:#acacac #cacaca #d4d4d4 #aeaeae; border-width:2px; border-style:solid; padding:15px; margin-top: 15px">
        <fieldset style="border:none">
          <legend style="padding:0">Site Purpose</legend>
          <p>This software can be configured in many different ways.  To get you started with a few presets, select the site's purpose.  The site can be customized later.</p>
          <label>
            <p><font color="red">*</font>How should the application be configured?</p>
            <b><%= showAttribute(request, "purposeError") %></b>
          </label>
          <label style="clear:right">
            <input type="radio" name="purpose" value="intranet" <ccp:evaluate if="<%= siteBean.getPurpose() %>" >checked</ccp:evaluate> />
            <strong>Enterprise social computing</strong> -
             A private site used for collaboration using features like blogs, wiki, documents, forums, photos, groups and projects
          </label>
          <label style="clear:right">
            <input type="radio" name="purpose" value="social" <ccp:evaluate if="<%= siteBean.getPurpose() %>" >checked</ccp:evaluate> />
            <strong>Social networking</strong> -
            A public special interest site that fosters collaboration around a particular theme
          </label>
          <label style="clear:right">
            <input type="radio" name="purpose" value="directory" <ccp:evaluate if="<%= siteBean.getPurpose() %>" >checked</ccp:evaluate> />
            <strong>Commercial social community</strong> -
            A public site fostering collaboration around a directory of places, things and people highlighting reviews as an important feature
          </label>
          <label style="clear:right">
            <input type="radio" name="purpose" value="community" <ccp:evaluate if="<%= siteBean.getPurpose() %>" >checked</ccp:evaluate> />
            <strong>Customer community</strong> -
            A public site that supplements an existing website with rich community features and an idea forum
          </label>
          <label style="clear:right">
            <input type="radio" name="purpose" value="projects" <ccp:evaluate if="<%= siteBean.getPurpose() %>" >checked</ccp:evaluate> />
            <strong>Client projects</strong> -
            A private site to exchange documents with clients
          </label>
          <label style="clear:right">
            <input type="radio" name="purpose" value="web" <ccp:evaluate if="<%= siteBean.getPurpose() %>" >checked</ccp:evaluate> />
            <strong>Business web Site</strong> -
            Setup a public marketing and collaborative web presence all in one
          </label>
        </fieldset>
      </div>
      <input type="submit" value="Continue" style="float:right; margin-top:10px" />
    </div>
  </div>
</form>
