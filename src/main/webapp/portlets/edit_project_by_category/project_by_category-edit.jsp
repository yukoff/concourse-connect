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
<%@ page import="com.concursive.connect.web.portal.PortalUtils,javax.portlet.*" %>
<%@ taglib uri="/WEB-INF/portlet.tld" prefix="portlet" %>
<%@ taglib uri="/WEB-INF/concourseconnect-taglib.tld" prefix="ccp" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ include file="../../initPage.jsp" %>
<portlet:defineObjects/>
<%-- Use the minimal TinyMCE Editor for blog posts --%>
<c:set var="ctx" value="${renderRequest.contextPath}" scope="request"/>
<c:if test="${!empty preferenceMap.longDescription}">
	<jsp:include page="../../tinymce_comments_include.jsp" flush="true"/>
	<script language="javascript" type="text/javascript">
	  initEditor('description');
	</script>
</c:if>
<script language="JavaScript" type="text/javascript">
  function checkForm<portlet:namespace/>(form) {
    var message = "";
    var formTest = true;

    if (formTest) {
      return true;
    } else {
      alert("The form could not be submitted:\r\n" + message);
      return false;
    }
  }
</script>
<c:set var="ctx" value="${renderRequest.contextPath}" scope="request"/>
<div class="editProjectByCategoryPortletEdit">
  <c:if test="${hasProjectAccess eq 'false'}">
  	<c:out value="You do not have permissions to edit the profile"/>
  </c:if>
  <c:if test="${hasProjectAccess ne 'false'}">
	  <c:if test="${!empty actionError}">
	    <p><font color="red"><c:out value="${actionError}"/></font></p>
	  </c:if>
	  <c:if test="${!empty projectNotFoundError}">
	    <p><font color="red"><c:out value="${projectNotFoundError}"/></font></p>
	  </c:if>
	  <div class="formContainer">
		<portlet:actionURL var="submitContentUrl" portletMode="view" />
		<form method="POST" name="<portlet:namespace/>updateProjectForm" action="${submitContentUrl}" onSubmit="try {return checkForm<portlet:namespace/>(this);}catch(e){return true;}">
	    <fieldset id="editproject">
	      <legend>
	        <c:if test="${!empty preferenceMap.pageTitle}">
	          <c:out value="${preferenceMap.pageTitle}" />
	        </c:if>
	        <c:if test="${empty preferenceMap.pageTitle}">
	          <c:out value="${projectBean.projectTitle}" />
	        </c:if>
	      </legend>
	    	<c:if test="${!empty preferenceMap.title}">
		    	<label for="<portlet:namespace/>projectTitle"><c:out value="${preferenceMap.title}"/><span class="required">*</span></label>
				  <input type="text" name="projectTitle" id="<portlet:namespace/>projectTitle" class="input longInput" maxlength="100" value="<c:out value="${projectBean.projectTitle}"/>" />
          <%= showAttribute(request, "titleError") %>
          <span class="characterCounter">100 characters max</span>
			  </c:if>
	        <c:if test="${!empty projectBean.requestDate}">
	          <c:set var="requestDate">
	            <ccp:tz timestamp="${projectBean.requestDate}" dateOnly="true"/>
	          </c:set>
	        </c:if>
	        <c:if test="${!empty projectBean.estimatedCloseDate}">
	          <c:set var="estimatedCloseDate">
	            <ccp:tz timestamp="${projectBean.estimatedCloseDate}" dateOnly="true"/>
	          </c:set>
	        </c:if>
			<c:if test="${!empty preferenceMap.requestDate}">
	         <label for="requestDate"><c:out value="${preferenceMap.requestDate}"/><c:if test="${preferenceMap.requiresStartEndDate eq 'true'}"><span class="required">*</span></c:if></label>
	         <input type="text" name="requestDate" id="requestDate" class="inputDate" value="${requestDate}" />
	         <a href="javascript:popCalendar('<portlet:namespace/>updateProjectForm', 'requestDate', '${user.locale.language}', '${user.locale.country}', 'requestDate');"><img src="<%= ctx %>/images/icons/stock_form-date-field-16.gif" border="0" align="absmiddle"></a>
	         <ccp:label name="projectsAddProject.at">at</ccp:label>
	         <ccp:timeSelect baseName="requestDate" value="${projectBean.requestDate}" timeZone="${user.timeZone}"/>
	         <ccp:tz timestamp="<%= new Timestamp(System.currentTimeMillis()) %>" pattern="z"/>
	         <div class="error"><%= showAttribute(request, "requestDateError") %></div>
	    </c:if>
			<c:if test="${!empty preferenceMap.estimatedCloseDate}">
	         <label for="estimatedCloseDate"><c:out value="${preferenceMap.estimatedCloseDate}"/><c:if test="${preferenceMap.requiresStartEndDate eq 'true'}"><span class="required">*</span></c:if></label>
	         <input type="text" name="estimatedCloseDate" id="estimatedCloseDate" class="inputDate" value="${estimatedCloseDate}" >
	         <a href="javascript:popCalendar('<portlet:namespace/>updateProjectForm', 'estimatedCloseDate', '${user.locale.language}', '${user.locale.country}');"><img src="<%= ctx %>/images/icons/stock_form-date-field-16.gif" border="0" align="absmiddle"></a>
	         <ccp:label name="projectsAddProject.at">at</ccp:label>
	         <ccp:timeSelect baseName="estimatedCloseDate" value="${projectBean.estimatedCloseDate}" timeZone="${user.timeZone}"/>
	         <ccp:tz timestamp="<%= new Timestamp(System.currentTimeMillis()) %>" pattern="z"/>
	         <%= showAttribute(request, "estimatedCloseDateError") %>
	    </c:if>
		    <div class="leftColumn">
		    	<c:if test="${!empty preferenceMap.subCategory1}">
		    		<c:if test="${!empty subCategoryList}">
		             <label for="<portlet:namespace/>subCategory1Id"><c:out value="${preferenceMap.subCategory1}"/></label>
	                   <select name="subCategory1Id" id="<portlet:namespace/>subCategory1Id" class="input selectInput">
	                      <c:forEach items="${subCategoryList}" var="subCategory">
	                        <option value="${subCategory.id}"<c:if test="${subCategory.id == projectBean.subCategory1Id}"> selected</c:if>><c:out value="${subCategory.description}"/></option>
	                      </c:forEach>
	                    </select>
	                  </c:if>
	             </c:if>
		    	<c:if test="${!empty preferenceMap.addressTo}">
		             <label for="<portlet:namespace/>addressTo"><c:out value="${preferenceMap.addressTo}"/></label>
		             <input type="text" name="addressTo" id="<portlet:namespace/>addressTo" maxlength="80" value="<c:out value="${projectBean.addressTo}"/>">
				</c:if>
		    	<c:if test="${!empty preferenceMap.addressLine1}">
		             <label for="<portlet:namespace/>addressline1"><c:out value="${preferenceMap.addressLine1}"/></label>
		             <input type="text" name="addressLine1" id="<portlet:namespace/>addressLine1" maxlength="80" value="<c:out value="${projectBean.addressLine1}"/>">
				</c:if>
		    	<c:if test="${!empty preferenceMap.city}">
		             <label for="<portlet:namespace/>city"><c:out value="${preferenceMap.city}"/></label>
		             <input type="text" name="city" id="<portlet:namespace/>city" maxlength="80" value="<c:out value="${projectBean.city}"/>">
				</c:if>
		    	<c:if test="${!empty preferenceMap.state}">
		             <label for="<portlet:namespace/>state"><c:out value="${preferenceMap.state}"/></label>
		             <input type="text" name="state" id="<portlet:namespace/>state" maxlength="80" value="<c:out value="${projectBean.state}"/>">
				</c:if>
		    	<c:if test="${!empty preferenceMap.country}">
		             <label for="<portlet:namespace/>state"><c:out value="${preferenceMap.country}"/></label>
	                 <select name="country" id="<portlet:namespace/>country" class="input selectInput">
	                 <c:forEach items="${countryList}" var="country">
	                 	<c:if test="${!empty projectBean.country}">
	                       <option value="${country.value}" <c:if test="${country.value eq projectBean.country}"> selected</c:if>><c:out value="${country.text}"/></option>
	                     </c:if>
	                 	<c:if test="${empty projectBean.country}">
	                       <option value="${country.value}" <c:if test="${country.value eq defaultCountry}"> selected</c:if>><c:out value="${country.text}"/></option>
	                     </c:if>
	                </c:forEach>
	                </select>
				</c:if>
		    	<c:if test="${!empty preferenceMap.postalCode}">
		             <label for="<portlet:namespace/>postalCode"><c:out value="${preferenceMap.postalCode}"/></label>
		             <input type="text" name="postalCode" id="<portlet:namespace/>postalCode" maxlength="12" value="<c:out value="${projectBean.postalCode}"/>">
				</c:if>
		    </div>
		    <div class="rightColumn">
	    		<c:if test="${!empty preferenceMap.businessPhone}">
		             <label for="<portlet:namespace/>phone"><c:out value="${preferenceMap.businessPhone}"/></label>
		             <input type="text" name="phone" id="<portlet:namespace/>phone" maxlength="30" value="<c:out value="${projectBean.phone}"/>">
				</c:if>
	    		<c:if test="${!empty preferenceMap.businessFax}">
		             <label for="<portlet:namespace/>businessFax"><c:out value="${preferenceMap.businessFax}"/></label>
		             <input type="text" name="fax" id="<portlet:namespace/>fax" maxlength="30" value="<c:out value="${projectBean.fax}"/>">
				</c:if>
	    		<c:if test="${!empty preferenceMap.email1}">
		             <label for="<portlet:namespace/>email1"><c:out value="${preferenceMap.email1}"/></label>
		             <input type="text" name="email" id="<portlet:namespace/>email" maxlength="255" value="<c:out value="${projectBean.email}"/>">
				</c:if>
	    		<c:if test="${!empty preferenceMap.webPage}">
		             <label for="<portlet:namespace/>webPage"><c:out value="${preferenceMap.webPage}"/></label>
		             <input type="text" name="webPage" id="<portlet:namespace/>webPage" maxlength="200" value="<c:out value="${projectBean.webPage}"/>">
                 <span class="characterCounter">200 characters max</span>
				</c:if>
		    </div>
	    	<c:if test="${!empty preferenceMap.keywords}">
		        <label for="<portlet:namespace/>keywords"><c:out value="${preferenceMap.keywords}"/></label> (comma-separated)
		        <input type="text" name="keywords" id="<portlet:namespace/>keywords" class="input longInput"  maxlength="255" value="<c:out value="${projectBean.keywords}"/>">
            <span class="characterCounter">255 characters max</span>
			</c:if>
	    	<c:if test="${!empty preferenceMap.shortDescription}">
		        <label for="<portlet:namespace/>shortDescription"><c:out value="${preferenceMap.shortDescription}"/><span class="required">*</span></label>
		        <input type="text" name="shortDescription" id="<portlet:namespace/>shortDescription" class="input longInput" maxlength="1000" value="<c:out value="${projectBean.shortDescription}"/>">
		        <%= showAttribute(request, "shortDescriptionError") %>
            <span class="characterCounter">1000 characters max</span>
			</c:if>
	    	<c:if test="${!empty preferenceMap.longDescription}">
		        <label for="<portlet:namespace/>description"><c:out value="${preferenceMap.longDescription}"/><span class="required">*</span></label>
             <textarea id="description" name="description" class="height200">${projectBean.description}</textarea>
              <%= showAttribute(request, "descriptionError") %>
			</c:if>
	    </fieldset>
	      <c:if test="${'true' eq param.popup || 'true' eq popup}">
	        <input type="hidden" name="popup" value="true" />
	        <input type="hidden" name="close" value="true" />
	      </c:if>
	    <input type="submit" value="<c:out value="Save"/>" class="submit">
	    <c:choose>
	      <c:when test="${'true' eq popup}">
          <input type="button" value="Cancel" class="cancel" id="panelCloseButton">
	      </c:when>
	      <c:otherwise>
	        <a href="${ctx}/show/${projectBean.uniqueId}" class="cancel">Cancel</a>
	      </c:otherwise>
	    </c:choose>
      <img src="${ctx}/images/loading16.gif" alt="loading please wait" class="submitSpinner" style="display:none"/>
	  </form>
  </c:if>
</div>