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
<%@ page import="com.concursive.connect.web.modules.wiki.utils.WikiToHTMLUtils" %>
<%@ page import="com.concursive.connect.web.modules.documents.dao.FileItem" %>
<%@ page import="com.concursive.connect.Constants" %>
<%@ page import="com.concursive.commons.text.StringUtils" %>
<%@ page import="java.net.URLEncoder" %>
<%@ taglib uri="/WEB-INF/portlet.tld" prefix="portlet" %>
<%@ taglib uri="/WEB-INF/concourseconnect-taglib.tld" prefix="ccp" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<portlet:defineObjects/>
<jsp:include page="../../tinymce_comments_include.jsp" flush="true"/>
<jsp:useBean id="applicationPrefs" class="com.concursive.connect.config.ApplicationPrefs" scope="application"/>
<jsp:useBean id="User" class="com.concursive.connect.web.modules.login.dao.User" scope="session"/>
<%@ include file="../../initPage.jsp" %>
<%
  boolean sslEnabled = "true".equals(applicationPrefs.get("SSL"));
%>
<%--@elvariable id="project" type="com.concursive.connect.web.modules.profile.dao.Project"--%>
<c:set var="project" value="${project}" scope="request"/>
<jsp:useBean id="project" class="com.concursive.connect.web.modules.profile.dao.Project" scope="request"/>
<c:if test="${!empty project.logo}">
  <%-- use the carousel --%>
  <script type="text/javascript">
    var <portlet:namespace/>currentImage = "<%= project.getLogo().getUrlName(210,150)%>";
    var <portlet:namespace/>isOwner = (<%= project.getLogo().getEnteredBy() == User.getId() %><ccp:permission name="project-profile-images-delete"> || true
    </ccp:permission>)
    ;
    var <portlet:namespace/>handlePrevButtonState = function(type, args) {
      var enabling = args[0];
      var leftImage = args[1];
      if (enabling) {
        leftImage.src = "<%= ctx %>/images/left-enabled.png";
      }
    };

    var <portlet:namespace/>handleNextButtonState = function(type, args) {
      var enabling = args[0];
      var rightImage = args[1];
      if (enabling) {
        rightImage.src = "<%= ctx %>/images/right-enabled.png";
      }
    };

    var <portlet:namespace/>pageLoad = function(e) {
      new YAHOO.extension.Carousel("<portlet:namespace/>mycarousel",
      {
        numVisible: 3,
        animationSpeed: 0.15,
        scrollInc: 2,
        navMargin: 20,
        prevElement: "prev-arrow",
        nextElement: "next-arrow",
        wrap: true,
        size: <%= project.getImages().size() %>,
        prevButtonStateHandler: <portlet:namespace/>handlePrevButtonState,
        nextButtonStateHandler: <portlet:namespace/>handleNextButtonState
      }
          );
    };
    YAHOO.util.Event.addListener(window, 'load', <portlet:namespace/>pageLoad);

    function <portlet:namespace/>spotlight(url, owner) {
      <portlet:namespace/>currentImage = url;
      <portlet:namespace/>isOwner = (owner == <%= User.getId() %><ccp:permission name="project-profile-images-delete"> || true</ccp:permission>);
      if (<portlet:namespace/>isOwner) {
        showSpan('<portlet:namespace/>profileImageDelete');
      } else {
        hideSpan('<portlet:namespace/>profileImageDelete');
      }
      var link = "${ctx}/show/${project.uniqueId}/image/" + url.replace("210x150", "0x0");
      YAHOO.util.Dom.get('<portlet:namespace/>profileImage').innerHTML = "<a href=\"javascript:<portlet:namespace/>showImage('" + escape(link) + "',null,'<portlet:namespace/>images');\">" +
                                                                         "<img src='${ctx}/show/${project.uniqueId}/image/" + url + "' /></a>";
    }
    function <portlet:namespace/>deleteImage() {
      if (<portlet:namespace/>isOwner) {
        window.location.href = '${ctx}/remove/${project.uniqueId}/image/' + <portlet:namespace/>currentImage;
      } else {
        alert('You cannot delete other\'s images');
      }
    }
    function <portlet:namespace/>makeDefault() {
      window.location.href = '${ctx}/set/${project.uniqueId}/image/' + <portlet:namespace/>currentImage;
    }

    //Create an array of images with the width and height set
    var <portlet:namespace/>Images = {
      profileImages : [
        <c:forEach items="${project.images}" var="img" varStatus="status">
        <c:set var="pImage" value="${img}"/>
        { url: "${ctx}/show/${project.uniqueId}/image/<%= ((FileItem)pageContext.getAttribute("pImage")).getUrlName(0,0) %>",
          <c:choose>
          <c:when test="${!empty img.comment}">
          title: "<c:out value="${img.comment}" />",
          </c:when>
          <c:otherwise>
          title: "<c:if test="${!empty img.subject}"><c:out value='${img.subject} -' /></c:if><c:out value='${project.title}'/> image",
          </c:otherwise>
          </c:choose>
          height: ${img.imageHeight},
          width: ${img.imageWidth}
        }<c:if test="${!status.last}">,</c:if>
        </c:forEach>
      ]};

    function <portlet:namespace/>findImgByUrl(url) {
      for (var i = 0; i != <portlet:namespace/>Images["profileImages"].length; i++) {
        var img = <portlet:namespace/>Images["profileImages"][i];
        var urlFound = img["url"];
        if (url == urlFound) return img;
      }
      return null;
    }

    function <portlet:namespace/>showImage(url, width, groupName) {
      var imgFound = <portlet:namespace/>findImgByUrl(url);
      showImage(imgFound['title'], imgFound['url'], width, imgFound['height'], imgFound['width'], groupName);
    }
  </script>
</c:if>

<c:if test="${hideBasicInformation ne 'true'}">
  <div class="portlet-table">
    <ccp:rating id='${project.id}'
                   showText='true'
                   count='${project.ratingCount}'
                   value='${project.ratingValue}'
                   url=''/>
  </div>
</c:if>

<%-- Image Carousel for profile
Can be removed and made into a seperate portlet --%>
<div class="portlet-menu">
  <div class="portlet-menu-cascade">
    <ul>
      <%-- Show any administrative links --%>
      <c:if test="${!empty project.logo}">
        <ccp:permission name="project-profile-images-add">
          <li>
            <a href="${ctx}/FileAttachments.do?command=ShowForm&lmid=<%= Constants.PROJECT_IMAGE_FILES %>&pid=${project.id}&liid=${project.id}&selectorId=<%= FileItem.createUniqueValue() %>&selectorMode=single&allowCaption=true"
               rel="shadowbox" title="Share an Image">
              <img src="${ctx}/images/imageCarousel/grfx_pc_AddPhoto.png" alt="Share an Image"/>
            </a>
          </li>
        </ccp:permission>
      </c:if>
      <c:if test="${!empty project.logo}">
        <ccp:permission name="project-profile-admin">
          <li>
            <a href="javascript:<portlet:namespace/>makeDefault()" title="Make this current photo the default">
              <img src="${ctx}/images/imageCarousel/grfx_pc_SetDefault.png" alt="Set as default"/>
            </a>
          </li>
        </ccp:permission>
        <%-- Determine the initial state of the delete button since it changes when an image is clicked --%>
        <c:choose>
          <c:when test="<%= project.hasLogo() && project.getLogo().getEnteredBy() == User.getId() %>">
            <c:set value="true" var="showDelete"/>
          </c:when>
          <c:otherwise>
            <ccp:permission name="project-profile-images-delete">
              <c:set value="true" var="showDelete"/>
            </ccp:permission>
          </c:otherwise>
        </c:choose>
        <li
            <c:if test="${empty showDelete}">style="display:none"</c:if> id="<portlet:namespace/>profileImageDelete">
          <a href="javascript:<portlet:namespace/>deleteImage()" title="Delete the current photo">
            <img src="${ctx}/images/imageCarousel/grfx_pc_DeletePhoto.png" alt="Delete"/>
          </a>
        </li>
      </c:if>
      <c:choose>
        <c:when test="${!empty project.logo}">
          <c:set var="startImage" value="${project.logo}"/>
        </c:when>
        <c:when test="${!empty project.images}">
          <c:set var="startImage">
            <%= project.getImages().get(0) %>
          </c:set>
        </c:when>
      </c:choose>
      <c:if test="${!empty startImage}">
        <c:choose>
          <c:when test="${!empty startImage.comment}">
            <c:set var="siTitle" value="${startImage.comment}"/>
          </c:when>
          <c:otherwise>
            <c:set var="siTitle">
              <c:if test="${!empty startImage.subject}"><c:out value='${startImage.subject} -'/></c:if><c:out
                value='${project.title}'/> image
            </c:set>
          </c:otherwise>
        </c:choose>
      </c:if>
      <c:if test="${User.loggedIn eq true && !empty project.logo}">
        <li><a
            href="javascript:showPanel('Mark this image as Inappropriate','${ctx}/show/${project.uniqueId}/app/report_inappropriate?module=profileimage&pid=${project.id}&id=${startImage.id}',700)""
          title="Flag if inappropriate"><img src="${ctx}/images/imageCarousel/grfx_pc_InappPhoto.png"
                                             alt="Flag if inappropriate"></a></li>
      </c:if>
    </ul>
    <%-- TODO: Add current image number here;
         <p>1 / <%= project.getImages().size() %></p>
    --%>
    <c:if test="${!empty project.images}">
      <jsp:useBean id="startImage" class="com.concursive.connect.web.modules.documents.dao.FileItem" scope="page"/>
      <p>
        <a href="javascript:showImage('<c:out value="${siTitle}" />','${ctx}/show/${project.uniqueId}/image/<%= startImage.getUrlName(0,0) %>', null, ${startImage.imageHeight}, ${startImage.imageWidth}, '<portlet:namespace/>images');"
           title="Start Slideshow">
          <img src="${ctx}/images/imageCarousel/grfx_pc_Slideshow.png"
               alt="<ccp:label name="profile.enlargeImages">Enlarge images</ccp:label>"> Enlarge images
        </a>
      </p>
    </c:if>
  </div>
  <%-- Background from Profile Image --%>
  <div class="portlet-menu-item-selected<c:if test="${empty project.logo}">-empty</c:if>">
    <%-- If no photo, show no photo text --%>
    <c:if test="${empty project.logo}">
      <div class="portlet-menu-caption">
        <img src="${ctx}/images/imageCarousel/no_photo_image.png" alt="no image">

        <p>
          <span>Share your Images of <c:out value="${project.title}"/></span>
        </p>
        <ccp:permission name="project-profile-images-add">
          <p><a
              href="${ctx}/FileAttachments.do?command=ShowForm&lmid=<%= Constants.PROJECT_IMAGE_FILES %>&pid=${project.id}&liid=${project.id}&selectorId=<%= FileItem.createUniqueValue() %>&selectorMode=single&allowCaption=true"
              rel="shadowbox">Upload Now!</a></p>
        </ccp:permission>
        <ccp:permission name="project-profile-images-add" if="none">
          <p>
            <a href="http<ccp:evaluate if="<%= sslEnabled %>">s</ccp:evaluate>://<%= getServerUrl(request) %>/login<ccp:evaluate if='<%= request.getAttribute("requestedURL") != null %>'>?redirectTo=<%= URLEncoder.encode((String)request.getAttribute("requestedURL"), "UTF-8") %></ccp:evaluate>"
               title="Login to <c:out value="${requestMainProfile.title}"/>" accesskey="s" rel="nofollow">Sign In</a>
            <ccp:evaluate if='<%= "true".equals(applicationPrefs.get("REGISTER")) %>'>
              or
              <a href="http<ccp:evaluate if="<%= sslEnabled %>">s</ccp:evaluate>://<%= getServerUrl(request) %>/register"
                 title="Register with  <c:out value="${requestMainProfile.title}"/>" accesskey="r"
                 rel="nofollow">Register</a>
            </ccp:evaluate>
          </p>
        </ccp:permission>
      </div>
    </c:if>
    <%-- show the main image --%>
    <c:if test="${!empty project.logo}">
      <div id="<portlet:namespace/>profileImage">
        <a title="<c:out value='${logoTitle}'/>"
           href="javascript:showImage('<c:out value="${logoTitle}" />','${ctx}/show/${project.uniqueId}/image/<%= project.getLogo().getUrlName(0,0) %>', null, ${project.logo.imageHeight}, ${project.logo.imageWidth}, '<portlet:namespace/>images');">
          <img alt="<c:out value='${project.logo.subject} - ${project.title}'/> image"
               src="<%= ctx %>/show/${project.uniqueId}/image/<%= project.getLogo().getUrlName(210,150) %>"/>
        </a>
          <%-- TODO: Fix Comment region
                     Currently no comments submitted on image upload are being shown
          --%>
        <c:choose>
          <c:when test="${!empty project.logo.comment}">
            <c:set var="logoTitle" value="${project.logo.comment}"/>
          </c:when>
          <c:otherwise>
            <c:set var="logoTitle">
              <c:if test="${!empty project.logo.subject}"><c:out value='${project.logo.subject} -'/></c:if><c:out
                value='${project.title}'/> image
            </c:set>
          </c:otherwise>
        </c:choose>
      </div>
    </c:if>
  </div>
  <%-- show the controls for working with images --%>
  <%-- show the image scroller if images exist --%>
  <c:if test="${!empty project.logo}">
    <div id="<portlet:namespace/>mycarousel" class="carousel-component">
      <c:choose>
        <c:when test="<%= project.getImages().size() > 3 %>">
          <div class="carousel-prev">
              <%-- <button id="prev-arrow" name="previous" title="previous">&lt;</button> --%>
            <img id="prev-arrow" class="left-button-image" src="<%= ctx %>/images/left-enabled.png"
                 alt="Previous Button"/>
          </div>
          <div class="carousel-next">
              <%-- <button id="next-arrow" name="next" title="next" class="enabled">&gt;</button>--%>
            <img id="next-arrow" class="right-button-image" src="<%= ctx %>/images/right-enabled.png"
                 alt="Next Button"/>
          </div>
        </c:when>
        <c:otherwise>
          <div class="carousel-prev">
              <%-- <button id="prev-arrow" name="previous" title="previous">&lt;</button>
              <img id="prev-arrow" class="left-button-image" src="<%= ctx %>/images/left-disabled.png" alt="Previous Button"/>  --%>
          </div>
          <div class="carousel-next" title="next" class="disabled">
              <%--<button id="next-arrow" name="next" title="next">&gt;</button>
              <img id="next-arrow" class="right-button-image" src="<%= ctx %>/images/right-disabled.gif" alt="Next Button"/> --%>
          </div>
        </c:otherwise>
      </c:choose>
      <div class="carousel-clip-region">
        <ul class="carousel-list">
          <c:forEach items="${project.images}" var="fileItem" varStatus="rowCounter">
            <c:choose>
              <c:when test="${!empty fileItem.comment}">
                <c:set var="fiTitle" value="${fileItem.comment}"/>
              </c:when>
              <c:otherwise>
                <c:set var="fiTitle">
                  <c:if test="${!empty fileItem.subject}"><c:out value='${fileItem.subject} -'/></c:if><c:out
                    value='${project.title}'/> image
                </c:set>
              </c:otherwise>
            </c:choose>
            <c:set var="fileItem" value="${fileItem}" scope="request"/>
            <jsp:useBean id="fileItem" class="com.concursive.connect.web.modules.documents.dao.FileItem" scope="request"/>
            <li id="<portlet:namespace/>mycarousel-item-${rowCounter.count}"><a
                rel="shadowbox[<portlet:namespace/>images];width=;imageWidth=${fileItem.imageWidth};imageHeight=${fileItem.imageHeight};imageUrl=${ctx}/show/${project.uniqueId}/image/<%= fileItem.getUrlName(0,0) %>"
                title="<c:out value='${fiTitle}'/>"
                href="javascript:<portlet:namespace/>spotlight('<%= StringUtils.jsStringEscape(fileItem.getUrlName(210,150)) %>',<%= fileItem.getEnteredBy() %>);"><img
                width="50" height="50" alt="<c:out value='${fileItem.subject} - ${project.title}'/> image"
                src="${ctx}/show/${project.uniqueId}/image/<%= fileItem.getUrlName(50,50) %>"/></a><%-- caption can go here --%>
            </li>
          </c:forEach>
        </ul>
      </div>
    </div>
    <%--
    <div id="photo-meta-1000316113" class="photo-metadata">
      <strong class="photo-caption"><c:out value='${fileItem.subject} - ${project.title}'/></strong>
      <em class="photo-poster">by <ccp:username id="${fileItem.enteredBy}"/></em>
      <div class="photo-admin">
        <a href="${ctx}/modify/${project.uniqueId}/image/${fileItem.id}" class="photo-admin-default" id="default-1000316113">Set as Default</a>
        <a href="${ctx}/delete/${project.uniqueId}/image/${fileItem.id}" class="photo-admin-delete" id="delete-1000316113">Delete</a>
      </div>
    </div>
    --%>
  </c:if>
  <%-- TODO: Waiting on implementation
  <div class="contactUs">
    <p><a href="#" title="Contact <c:out value='${project.title}'/>">Contact Us</a></p>
  </div>
  --%>

</div>
<%-- END Profile Image Carousel --%>

<%-- Begin Actual Content : --%>
<div class="portlet-section-body">
<h3><c:out value="${title}"/></h3>
<%-- Display Basic Info Test --%>
<c:if test="${hideBasicInformation ne 'true'}">
  <h1>
    <c:if test="${!empty projectTitleLink}"><a href="${projectTitleLink}" title="<c:out value="${project.title}"/>"></c:if>
    <c:out value="${project.title}"/>
    <c:if test="${!empty projectTitleLink}"></a></c:if>
  </h1>
  <ccp:permission name="project-profile-admin">
    <c:choose>
      <c:when test="${empty project.description}">
        <a href="javascript:showPanel('','${ctx}/show/${project.uniqueId}/app/edit_profile','600')"
           class="portlet-menu-edit">Edit Profile</a>
      </c:when>
      <c:otherwise>
        <a href="${ctx}/modify/${project.uniqueId}/profile" class="portlet-menu-edit">Edit Profile</a>
      </c:otherwise>
    </c:choose>
  </ccp:permission>

  <%-- Microformat vCard Spec --%>
  <div class="vcard">
    <c:if test="${!empty project.location}">
      <address class="adr">
        <c:if test="${!empty project.address}">
          <span class="street-address"><c:out value="${project.address}"/></span>
        </c:if>
        <c:if test="${!empty project.city && !empty project.state}">
          <span class="regoin"><c:out value="${project.city}"/></span>, <span class="locality"><c:out
            value="${project.state}"/></span>
        </c:if>
        <c:if test="${!empty project.postalCode}">
          <span class="postal-code"><c:out value="${project.postalCode}"/></span>
        </c:if>
      </address>
    </c:if>
    <c:if test="${!empty project.businessPhone}">
      <p class="tel work"><span>Phone:</span> <c:out value="${project.businessPhone}"/></p>
    </c:if>
    <c:if test="${!empty project.businessFax}">
      <p class="tel fax"><span>Fax:</span> <c:out value="${project.businessFax}"/></p>
    </c:if>
    <c:if test="${!empty project.webPage}">
      <p class="url">
        <span>Web site:</span> <a href="<c:out value="${project.webPage}"/>" title="<c:out value="${project.title}"/>"
                                  target="_blank"><c:out value="${project.webPage}"/></a>
      </p>
    </c:if>
    <c:if test="${subCategory1 != null}">
      <p class="category">
        <span>Category:</span>
        <a href="${ctx}/page/categories/<c:out value="${fn:toLowerCase(project.category.description)}"/>/<c:out value="${fn:toLowerCase(fn:replace(subCategory1.description, ' ', '_'))}"/>"
           title="Category <c:out value="${subCategory1.description}"/>"><c:out
            value="${subCategory1.description}"/></a>
      </p>
    </c:if>
      <%--
      <c:if test='<%= project.getServices().hasService("call") %>'>
        <li>
          <div class="callUs">
            <p><a href="${ctx}/show/${project.uniqueId}/app/call" title="Call <c:out value='${project.title}'/> for free" rel="shadowbox;width=400">Call us for free</a></p>
          </div>
        </li>
      </c:if>
      --%>
      <%--@elvariable id="subCategory1" type="com.concursive.connect.web.modules.profile.dao.ProjectCategory"--%>
  </div> <%-- End vcard --%>
</c:if> <%-- End Basic Info Display Test --%>

<%-- Edit Menu for Wiki Atrributes --%>
<c:if test="${!empty wiki}">
  <ccp:permission name="project-profile-admin">
    <a href="javascript:showPanel('','${ctx}/show/${project.uniqueId}/app/edit_wiki','600')"
       class="portlet-menu-edit">Edit Details</a>
  </ccp:permission>
</c:if>
<c:if test="${showAuthor eq 'true'}">
      <p class="author">
        <span>Submitted By:</span>
        <ccp:username id="${project.enteredBy}" />
</c:if>
<%-- Wiki Attribute Display --%>
<c:if test="${!empty project.shortDescription || !empty project.description || !empty wikiAttributeList}">
  <dl>
    <dt>Description</dt>
  </c:if>
  <c:if test="${!empty project.shortDescription}">
    <dd><c:out value="${project.shortDescription}"/></dd>
    <dd>${project.description}</dd>
  </c:if>
  <%--@elvariable id="wikiAttributeList" type="java.util.ArrayList<CustomFormField>"--%>
  <c:if test="${!empty wikiAttributeList}">
    <dd>
      <jsp:useBean id="wiki" class="com.concursive.connect.web.modules.wiki.dao.Wiki" scope="request"/>
      <ul>
        <c:forEach items="${wikiAttributeList}" var="field">
          <c:set var="field" value="${field}" scope="request"/>
          <jsp:useBean id="field" class="com.concursive.connect.web.modules.wiki.dao.CustomFormField" scope="request"/>
          <c:if test="${!empty field.value}">
            <li><c:if test="${field.labelDisplay}"><span><c:out value="${field.label}"/>:</span></c:if>
              <%= WikiToHTMLUtils.toHtml(field, wiki, ctx) %>
            </li>
          </c:if>
        </c:forEach>
      </ul>
    </dd>
  </c:if>
</dl>
  <%--@elvariable id="projectTagList" type="com.concursive.connect.web.modules.common.social.tagging.dao.TagList"--%>
<c:if test="${!empty projectTagList}">
  <dl class="horizontal-list">
    <dt>Tag Cloud</dt>
    <c:forEach items="${projectTagList}" var="tag">
      <%--@elvariable id="tag" type="com.concursive.connect.web.modules.common.social.tagging.dao.Tag"--%>
      <dd class="portlet-text-${tag.weight}"><a
          href="${ctx}/page/tag/<c:out value="${fn:toLowerCase(project.category.description)}"/>/${tag.normalizedTag}"><c:out
          value="${tag.tag}"/></a></dd>
    </c:forEach>
  </dl>
</c:if>
  <%--@elvariable id="projectBadgeList" type="com.concursive.connect.web.modules.badges.dao.ProjectBadgeList"--%>
  <%--@elvariable id="projectBadge" type="com.concursive.connect.web.modules.badges.dao.ProjectBadge"--%>
  <%--@elvariable id="badge" type="com.concursive.connect.web.modules.badges.dao.Badge"--%>
<c:if test="${!empty projectBadgeList}">
  <dl>
    <dt>Badges</dt>
    <c:forEach items="${projectBadgeList}" var="projectBadge">
      <dd><c:out value="${projectBadge.badge.title}"/></dd>
    </c:forEach>
  </dl>
</c:if>
</div><%-- End portlet-section-body --%>

<%-- TODO: Move to a "Get Involved" portlet --%>
<%-- Begin portlet-section-footer --%>
<c:if test="${showGetInvolved eq 'true'}">
  <div class="portlet-section-footer">
    <h4><c:out value='${getInvolvedText}'/></h4>
  </c:if>
  <ul>
    <c:if test="${project.features.showAds}">
      <ccp:permission name="project-ads-view">
        <c:if test="${showAds eq 'true'}">
          <li><a href="${ctx}/show/${project.uniqueId}/promotions"
                 title="<c:out value='${project.title}'/> promotions"><c:out value='${labelAds}'/></a></li>
        </c:if>
      </ccp:permission>
    </c:if>
    <c:if test="${project.features.showDiscussion}">
      <ccp:permission name="project-discussion-forums-view">
        <c:if test="${showDiscussion eq 'true'}">
          <li><a href="${ctx}/show/${project.uniqueId}/discussion"
                 title="<c:out value='${project.title}'/> discussions"><c:out value='${labelDiscussion}'/></a></li>
        </c:if>
      </ccp:permission>
    </c:if>
    <c:if test="${project.features.showNews}">
      <ccp:permission name="project-news-view">
        <c:if test="${showNews eq 'true'}">
          <li><a href="${ctx}/show/${project.uniqueId}/blog" title="<c:out value='${project.title}'/> blog"><c:out
              value='${labelNews}'/></a></li>
        </c:if>
      </ccp:permission>
    </c:if>
    <c:if test="${project.features.showReviews}">
      <ccp:permission name="project-reviews-view">
        <c:if test="${showReviews eq 'true'}">
          <li><a href="${ctx}/show/${project.uniqueId}/reviews" title="<c:out value='${project.title}'/> reviews"><c:out
              value='${labelReviews}'/></a></li>
        </c:if>
      </ccp:permission>
    </c:if>
    <c:if test="${project.features.showClassifieds}">
      <ccp:permission name="project-classifieds-view">
        <c:if test="${showClassifieds eq 'true'}">
          <li><a href="${ctx}/show/${project.uniqueId}/classifieds" title="<c:out value='${project.title}'/> classifieds"><c:out
              value='${labelClassifieds}'/></a></li>
        </c:if>
      </ccp:permission>
    </c:if>
    <c:if test="${project.features.showTeam}">
      <ccp:permission name="project-team-view">
        <c:if test="${showTeam eq 'true'}">
          <li><a href="${ctx}/show/${project.uniqueId}/members"
                 title="<c:out value='${project.title}'/> mailing list"><c:out value='${labelTeam}'/></a></li>
        </c:if>
      </ccp:permission>
    </c:if>
    <c:if test="${project.features.showCalendar}">
      <ccp:permission name="project-calendar-view">
        <c:if test="${showCalendar eq 'true'}">
          <li><a href="${ctx}/show/${project.uniqueId}/calendar" title="<c:out value='${project.title}'/> events"><c:out
              value='${labelCalendar}'/></a></li>
        </c:if>
      </ccp:permission>
    </c:if>
    <c:if test="${project.features.showWiki}">
      <ccp:permission name="project-wiki-view">
        <c:if test="${showWiki eq 'true'}">
          <li><a href="${ctx}/show/${project.uniqueId}/wiki" title="<c:out value='${project.title}'/> wiki"><c:out
              value='${labelWiki}'/></a></li>
        </c:if>
      </ccp:permission>
    </c:if>
    <c:if test="${project.features.showTickets}">
      <ccp:permission name="project-tickets-view">
        <c:if test="${showTickets eq 'true'}">
          <li><a href="${ctx}/show/${project.uniqueId}/issues" title="<c:out value='${project.title}'/> support"><c:out
              value='${labelTickets}'/></a></li>
        </c:if>
      </ccp:permission>
    </c:if>
    <c:if test="${project.features.showDocuments}">
      <ccp:permission name="project-documents-view">
        <c:if test="${showDocuments eq 'true'}">
          <li><a href="${ctx}/show/${project.uniqueId}/documents"
                 title="<c:out value='${project.title}'/> documents"><c:out value='${labelDocuments}'/></a></li>
        </c:if>
      </ccp:permission>
    </c:if>
    <c:if test="${project.features.showBadges}">
      <ccp:permission name="project-badges-view">
        <c:if test="${showBadges eq 'true'}">
          <li><a href="${ctx}/show/${project.uniqueId}/badges" title="<c:out value='${project.title}'/> badges"><c:out
              value='${labelBadges}'/></a></li>
        </c:if>
      </ccp:permission>
    </c:if>
  </ul>
</div><%-- End portlet-section-footer --%>
<%-- End configurable area --%>
</div>
</div>
