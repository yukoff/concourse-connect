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
<jsp:useBean id="detailsBean" class="com.concursive.connect.web.modules.setup.beans.SetupDetailsBean" scope="request"/>
<%@ include file="initPage.jsp" %>
<form action="${ctx}/SetupDetails.do?command=SaveDetails" name="setupForm" method="post">
  <input type="hidden" name="auto-populate" value="true" />
  <%= showError(request, "actionError", false) %>
  <div style="width:100%; height:100%; background:#efefef; padding:15px 0 0 0; margin:0">
    <div style="width:600px; height:600px; position:relative; margin:0 auto">
      <img src="${ctx}/images/setup/step-4-header.jpg" alt="Step Four" style="margin:0 auto; display:block">
      <br style="clear:both">
      <h1 style="color:#425c61; font-size:x-large; margin:35px 0">Setup details</h1>
      <div class="formContainer" style="font-size:small; width:500px; margin:0 auto; background:#fff; -moz-border-radius:10px; -webkit-border-radius:10px; border-color:#acacac #cacaca #d4d4d4 #aeaeae; border-width:2px; border-style:solid; padding:15px">
        <fieldset style="border:none">
          <legend style="padding:0">Some important details</legend>

          <label><font color="red">*</font>Email Server:</label>
          <input type="text" name="server" value="<%= toHtmlValue(detailsBean.getServer()) %>" size="30" />
          <b><%= showAttribute(request, "serverError") %></b>

          <label><font color="red">*</font>Email Server Port:</label>
          <input type="text" name="serverPort" value="<%= toHtmlValue(detailsBean.getServerPort()) %>" size="5" />
          <b><%= showAttribute(request, "serverPortError") %></b>

          <label>Email Server Username:</label>
          <input type="text" name="serverUsername" value="<%= toHtmlValue(detailsBean.getServerUsername()) %>" size="30" />
          <b><%= showAttribute(request, "serverUsernameError") %></b>

          <label>Email Server Password:</label>
          <input type="password" name="serverPassword" value="<%= toHtmlValue(detailsBean.getServerPassword()) %>" size="30" />
          <b><%= showAttribute(request, "serverPasswordError") %></b>

          <fieldset style="margin:10px 0">
            <input type="checkbox" name="serverSsl" id="serverSsl" value="ON" <ccp:evaluate if="<%= detailsBean.getServerSsl() %>" >checked</ccp:evaluate> />
            <label>Use SSL for connecting to mail server</label>
          </fieldset>

          <label><font color="red">*</font>System's Email Address</label>
          <input type="text" name="address" value="<%= toHtmlValue(detailsBean.getAddress()) %>" size="40" />
          <b><%= showAttribute(request, "addressError") %></b>

          <label>Google Maps API Domain</label>
          <input type="text" name="googleMapsAPIDomain" value="<%= toHtmlValue(detailsBean.getGoogleMapsAPIDomain()) %>" size="40" />
          <b><%= showAttribute(request, "googleMapsAPIDomainError") %></b>
          <span class="characterCount">
            Visit <a target="_blank" href="http://code.google.com/apis/maps/">Google Maps API site</a> to obtain a maps key.<br />
            Specify the exact site url of this application install, this key can be changed later.<br />
            A key is not required for using ConcourseConnect, but maps will be disabled.
          </span>

          <label>Google Maps API Key</label>
          <input type="text" name="googleMapsAPIKey" value="<%= toHtmlValue(detailsBean.getGoogleMapsAPIKey()) %>" size="40" />
          <b><%= showAttribute(request, "googleMapsAPIKeyError") %></b>

          <label>Google Analytics</label>
          <input type="text" name="googleAnalyticsId" value="<%= toHtmlValue(detailsBean.getGoogleAnalyticsId()) %>" size="40" />
          <b><%= showAttribute(request, "googleAnalyticsIdError") %></b>
          <span class="characterCount">
            Visit <a target="_blank" href="http://www.google.com/analytics/">Google Analytics site</a> to create an account.<br />
            The tracker id is usually in the format of UA-########-#
          </span>

          <label>Google Analytics Verification Code</label>
          <input type="text" name="googleAnalyticsVerifyCode" value="<%= toHtmlValue(detailsBean.getGoogleAnalyticsVerifyCode()) %>" size="40" />
          <b><%= showAttribute(request, "googleAnalyticsVerifyCodeError") %></b>
          <span class="characterCount">
            A verification code is sometimes required -- during account creation Google may ask you to verify the ownership of this site, otherwise leave blank.
          </span>

          <label>Twitter Hashtag</label>
          <input type="text" name="twitterHashtag" value="<%= toHtmlValue(detailsBean.getTwitterHashtag()) %>" size="40" />
          <b><%= showAttribute(request, "twitterHashtagError") %></b>
          <span class="characterCount">
            Come up with a hashtag so that users can post to your site using Twitter.<br />
            For example, use 'connect' and then when a Twitter Id is linked to a profile, and that Twitter user<br />
            posts to #connect then that post will be imported.  Come up with a unique tag for your site!<br />
            (no spaces or special characters allowed)
          </span>

          <label><font color="red">*</font>Default Storage Limit (per user)</label>
          <b><%= showAttribute(request, "storageError") %></b>
          <input type="text" name="storage" value="<%= toHtmlValue(detailsBean.getStorage()) %>" size="10" /> MB
          <span class="characterCount">When new users are added to the system by invitation,<br />
          this is the default setting for storage allowed. Each user<br />
          can be configured separately by an administrator.<br />
          Use "-1" for no limit.</span>
        </fieldset>
      </div>
      <input type="submit" value="Continue" style="float:right; margin-top:10px" />
    </div>
  </div>
</form>
