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
<%@ page import="com.concursive.commons.http.RequestUtils" %>
<jsp:useBean id="applicationPrefs" class="com.concursive.connect.config.ApplicationPrefs" scope="application"/>
<jsp:useBean id="CountrySelect" class="com.concursive.connect.web.utils.CountrySelect" scope="request"/>
<jsp:useBean id="CompanySizeSelect" class="com.concursive.connect.web.utils.CompanySizeSelect" scope="request"/>
<jsp:useBean id="CompanyRevenueSelect" class="com.concursive.connect.web.utils.CompanyRevenueSelect" scope="request"/>
<%@ taglib uri="/WEB-INF/concourseconnect-taglib.tld" prefix="ccp" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<jsp:useBean id="contactUs" class="com.concursive.connect.web.modules.contactus.dao.ContactUsBean" scope="request"/>
<%@ include file="initPage.jsp" %>
<script language="JavaScript" TYPE="text/javascript">
  function checkForm(form) {
    var message = "";
    var formTest = true;
    if (form.nameFirst.value == "") {
      message += "- First Name is required\r\n";
      formTest = false;
    }
    if (form.nameLast.value == "") {
      message += "- Last Name is required\r\n";
      formTest = false;
    }
    if (form.email.value == "") {
      message += "- Email address is required\r\n";
      formTest = false;
    } else {
      if (!checkEmail(form.email.value)) {
        message += "- The entered email address is invalid.  Make sure there are no invalid characters\r\n";
        formTest = false;
      }
    }
    if (form.description.value == "") {
      message += "- A question or comment is required\r\n";
      formTest = false;
    }
    if (form.captcha.value == "") {
      message += "- Please don't forget to input the validation image\r\n";
      formTest = false;
    }
    if (formTest) {
      return true;
    } else {
      alert("The form could not be submitted:\r\n" + message);
      return false;
    }
  }
  function newCaptcha() {
    document.contactUs.captimg.src="<%= RequestUtils.getAbsoluteServerUrl(request) %>/Captcha.png?" + Math.random();
  }
</script>
<form action="<%= ctx %>/contact-us?command=Send&auto-populate=true" method="post" name="contactUs" onSubmit="return checkForm(this);">
<div class="spacerContainer">
  <div class="formContainer portletWindowBackground">
    <div class="leftColumn">
      <fieldset>
      <legend>Contact Us</legend>
        <h3>Who Would You Like to Contact?</h3>
          <label>
            <input type="radio" class="checkbox" name="formData" value="Contact reason: Sales"/>
            Sales
          </label>
          <label>
            <input type="radio" class="checkbox" name="formData" value="Contact reason: Support"/>
            Support
          </label>
          <label>
            <input type="radio" class="checkbox" name="formData" value="Contact reason: Services"/>
            Services
          </label>
          <label>
            <input type="radio" class="checkbox" name="formData" value="Contact reason: General"/>
            General Info
          </label>
        <h3>Tell Us About Yourself</h3>
          <label>First Name <span class="required">*</span></label>
          <%= showAttribute(request, "nameFirstError") %>
          <input type="text" name="nameFirst" value="<%= toHtmlValue(contactUs.getNameFirst()) %>"/>
          <label>Last Name <span class="required">*</span></label>
          <%= showAttribute(request, "nameLastError") %>
          <input type="text" name="nameLast" value="<%= toHtmlValue(contactUs.getNameLast()) %>"/>
          <label>Email Address <span class="required">*</span></label>
          <%= showAttribute(request, "emailError") %>
          <input type="text" name="email" value="<%= toHtmlValue(contactUs.getEmail()) %>"/>
          <label>Organization</label>
          <%= showAttribute(request, "organizationError") %>
          <input type="text" name="organization" value="<%= toHtmlValue(contactUs.getOrganization()) %>"/>
          <label>Phone</label>
          <%= showAttribute(request, "businessPhoneError") %>
          <input type="text" name="businessPhone" value="<%= toHtmlValue(contactUs.getBusinessPhone()) %>"/>
          <label>Extension</label>
          <input type="text" name="businessPhoneExt" value="<%= toHtmlValue(contactUs.getBusinessPhoneExt()) %>"/>
          <label>Address</label>
          <%= showAttribute(request, "addressLine1Error") %>
          <input type="text" name="addressLine1" value="<%= toHtmlValue(contactUs.getAddressLine1()) %>"/>
          <label>Address 2</label>
          <input type="text" name="addressLine2" value="<%= toHtmlValue(contactUs.getAddressLine2()) %>"/>
          <label>City</label>
          <%= showAttribute(request, "cityError") %>
          <input type="text" name="city" value="<%= toHtmlValue(contactUs.getCity()) %>"/>
          <label>State/Province</label>
          <%= showAttribute(request, "stateError") %>
          <input type="text" name="state" value="<%= toHtmlValue(contactUs.getState()) %>"/>
          <label>Zip/Postal Code</label>
          <%= showAttribute(request, "postalCodeError") %>
          <input type="text" name="postalCode" value="<%= toHtmlValue(contactUs.getPostalCode()) %>"/>
          <label>Country or area <span class="required">*</span></label>
          <%= showAttribute(request, "countryError") %>
          <%
          String selected_country = contactUs.getCountry();
          if(selected_country == null) {
              selected_country = "UNITED STATES";
          }
          %>
          <%= CountrySelect.getHtml("country", selected_country) %>
         <label>Please input the disguised word<span class="required">*</span></label>
        <img src="<%= RequestUtils.getAbsoluteServerUrl(request) %>/Captcha.png" id="captimg" name="captimg">
        <span class="characterCounter">Trouble reading this? <a style="padding-left:10px;" href="javascript:newCaptcha();">Try another word...</a></span>
        <input type="text" name="captcha">
        <label>Your Comments <span class="required">*</span> </label>
        <%= showAttribute(request, "descriptionError") %>
        <textarea name="description" class="height100"><%= toString(contactUs.getDescription()) %></textarea>
      </fieldset>
      <c:if test="${!empty contactUs.token}">
        <input type="hidden" name="token" value="${contactUs.token}"/>
      </c:if>
      <input type="hidden" name="after_submit" value="after_submit_url"/>
      <input type="submit" value="submit" name="submit" class="submit"/>
    </div>
    <div class="rightColumn">
      <h2><c:out value="${requestMainProfile.title}"/></h2>
      <address>
        <c:if test="${!empty requestMainProfile.addressToAndLocation}">
          <span><c:out value="${requestMainProfile.addressToAndLocation}"/></span>
        </c:if>
        <c:if test="${!empty requestMainProfile.businessPhone}">
          <span>
            <c:out value="${requestMainProfile.businessPhone}"/>
            <c:out value="${requestMainProfile.businessPhoneExt}"/>
          </span>
        </c:if>
      </address>

<%-- Draw the map if possible --%>
<c:if test='${!empty applicationPrefs.prefs["GOOGLE_MAPS.DOMAIN"] && !empty requestMainProfile.address}'>
<c:set var="project" value="${requestMainProfile}" scope="request"/>
<jsp:useBean id="project" type="com.concursive.connect.web.modules.profile.dao.Project" scope="request"/>
<%
  String mapId = "Map";
  request.setAttribute("mapId", mapId);
%>
<c:set var="mapHeight" value="300px" scope="request"/>
<c:set var="mapVersion" value="2.s" scope="request"/>
<script src="http://maps.google.com/maps?file=api&v=${mapVersion}&key=${applicationPrefs.prefs["GOOGLE_MAPS.KEY"]}&hl=en" type="text/javascript"></script>
<style type="text/css">
	v\:* {
		behavior:url(#default#VML);
	}
</style>
<script type="text/javascript">
var ${mapId};
if(document.implementation.hasFeature("http://www.w3.org/TR/SVG11/feature#SVG","1.1")) {
	_mSvgEnabled = true;
	_mSvgForced = true;
}
<c:choose>
  <c:when test="${empty project.address}">
    <c:set var="mapZoom" value="4" scope="request"/>
    var thisIcon = new GIcon();
    thisIcon.image = '${ctx}/images/map_markers/zone.png';
    thisIcon.shadow = '${ctx}/images/map_markers/zone.png';
    thisIcon.iconSize = new GSize(120, 120);
    thisIcon.shadowSize = new GSize(120, 120);
    thisIcon.iconAnchor = new GPoint(60, 60);
    thisIcon.infoWindowAnchor = new GPoint(60, 60);
  </c:when>
  <c:otherwise>
    <%-- set zoom level --%>
    <c:set var="mapZoom" value="2" scope="request"/>
    <%-- set icon --%>
    var thisIcon = new GIcon();
    thisIcon.shadow = '${ctx}/images/map_markers/marker_shadow.png';
    thisIcon.iconSize = new GSize(33, 37);
    thisIcon.shadowSize = new GSize(57, 33);
    thisIcon.iconAnchor = new GPoint(16, 34);
    thisIcon.infoWindowAnchor = new GPoint(28, 12);
    <c:choose>
      <c:when test="${project.category.description eq 'Businesses'}">
        thisIcon.image = '${ctx}/images/map_markers/marker_orange_business.png';
      </c:when>
      <c:when test="${project.category.description eq 'Organizations'}">
        thisIcon.image = '${ctx}/images/map_markers/marker_orange_org.png';
      </c:when>
      <c:when test="${project.category.description eq 'People'}">
        thisIcon.image = '${ctx}/images/map_markers/marker_blue_people.png';
      </c:when>
      <c:when test="${project.category.description eq 'Events'}">
        thisIcon.image = '${ctx}/images/map_markers/marker_purple_event.png';
      </c:when>
      <c:when test="${project.category.description eq 'Groups'}">
        thisIcon.image = '${ctx}/images/map_markers/marker_green_group.png';
      </c:when>
      <c:otherwise>
        thisIcon.image = '${ctx}/images/map_markers/marker_gray.png';
      </c:otherwise>
    </c:choose>
  </c:otherwise>
</c:choose>

function initializeMap${mapId}(e) {
	if (GBrowserIsCompatible()) {
		${mapId} = new GMap(document.getElementById("${mapId}"));
		${mapId}.setMapType(G_NORMAL_MAP);
		${mapId}.centerAndZoom(new GPoint(${project.longitude}, ${project.latitude}), ${mapZoom});
    <c:set var="markerContent" scope="request">
      <c:if test="${!empty project.category.logo}"><c:choose><c:when test="${!empty project.logo}"><img alt='<c:out value="${project.title}"/> photo' align='absmiddle' src='${ctx}/image/<%= project.getLogo().getUrlName(45,45) %>' /> </c:when><c:otherwise><img alt='Default photo' align='absmiddle' src='${ctx}/image/<%= project.getCategory().getLogo().getUrlName(45,45) %>'  class='default-photo' /> </c:otherwise></c:choose></c:if><c:out value="${project.title}"/><c:if test="${!empty project.address}"><br /><c:out value="${project.address}"/></c:if><br /><c:out value="${project.location}"/><c:if test="${!empty project.businessPhone}"><br />Phone: <strong><c:out value="${project.businessPhone}"/></strong></c:if><c:if test="${!empty project.businessFax}"><br />Fax: <strong><c:out value="${project.businessFax}"/></strong></c:if>
    </c:set>
    var marker1 = new GMarker(new GPoint(${project.longitude}, ${project.latitude}), {icon:thisIcon});
    ${mapId}.addOverlay(marker1);
		GEvent.addListener(marker1, 'click', function() {
			marker1.openInfoWindowHtml("<div>${markerContent}</div>");
		});
		${mapId}.addControl(new GSmallMapControl());
		${mapId}.enableDragging();
	}
}
</script>
<div id="${mapId}" style="height: ${mapHeight};width: 100%"></div>
<%-- Show project name when getting directions --%>
<c:set var="titleContent" scope="request">${project.title}</c:set>
<c:set var="titleContent" scope="request">${fn:replace(titleContent, ' ', '+')}</c:set>
<c:set var="titleContent" scope="request">${fn:replace(titleContent, '&', '%26')}</c:set>
<%-- Show project address when getting directions --%>
<c:set var="directionsContent" scope="request">${project.address} ${project.location}</c:set>
<c:set var="directionsContent" scope="request">${fn:replace(directionsContent, ' ', '+')}</c:set>
<c:set var="directionsContent" scope="request">${fn:replace(directionsContent, '&', '%26')}</c:set>
<a target="_blank" href="http://maps.google.com/maps?f=d&hl=en&geocode=&q=to:${directionsContent}(<c:out value="${titleContent}"/>)&ie=UTF8&z=13">get directions</a>
<%-- Use YUI to properly start the map in a portlet --%>
<script type="text/javascript">
  YAHOO.util.Event.addListener(window, "load", initializeMap${mapId});
  YAHOO.util.Event.addListener(window, "unload", GUnload);
</script>
</c:if>


    </div>
  </div>
</div>
</form>

