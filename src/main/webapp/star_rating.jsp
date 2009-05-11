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
  --%><%-- Spacing is intentional for output to be correct --%><%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %><%@ taglib uri="/WEB-INF/concourseconnect-taglib.tld" prefix="ccp" %><%@ page import="com.concursive.commons.http.RequestUtils" %><jsp:useBean id="ratingBean" class="com.concursive.connect.web.modules.common.social.rating.beans.RatingBean" scope="request"/>rating_<%= ratingBean.getItemId() %>|
<%
if(request.getAttribute("url") == null) {
  %>
<ul class="star-rating">
<li class="current-rating" id="current-rating" style="width: <%= ratingBean.getImageWidth() %>px; background: url(<%= RequestUtils.getAbsoluteServerUrl(request) %>/images/star_rating/stars16.png) left center!important; left: 0!important; margin: 0!important"></li>
<li class="one-star"></li>
<li class="two-stars"></li>
<li class="three-stars"></li>
<li class="four-stars"></li>
<li class="five-stars"></li>
</ul>
<%
  if ("true".equals(request.getParameter("ratingShowText"))) {
%>
  |ratingCount_<%= ratingBean.getItemId() %>| (<%= ratingBean.getCount() %>)
<%
  }
%>
<%
} else {
%>
<ccp:rating id='${ratingBean.itemId}'
        showText='${ratingShowText}'
        count='${ratingBean.count}'
        value='${ratingBean.value}'
        field='${field}'
        url='${url}'/>
<%
  }
%>