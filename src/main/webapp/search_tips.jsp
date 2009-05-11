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
<b>Simple Query Syntax</b>
<br />
<br />
<table bgcolor="#DEDEDE" border="0" cellspacing="1" cellpadding="4" width="100%">
  <tr>
    <td><b>Search for project data by typing in words</b></td>
  </tr>
  <tr>
    <td>fall campaign</td>
  </tr>
  <tr>
    <td><hr color="#BFBFBB" noshade></td>
  </tr>
  <tr>
    <td><b>Search for exact phrases by using quotations</b></td>
  </tr>
  <tr>
    <td>"fall campaign"</td>
  </tr>
  <tr>
    <td><hr color="#BFBFBB" noshade></td>
  </tr>
  <tr>
    <td><b>Use AND, OR, NOT operators when searching</b></td>
  </tr>
  <tr>
    <td>campaign NOT fall<br />
	    &quot;fall campaign&quot; AND &quot;campaign ideas&quot;<br />
      &quot;fall campaign&quot; OR ideas<br />
      <br />
      Note: AND, OR, NOT can be uppercase or lowercase.
    </td>
  </tr>
  <tr>
    <td><hr color="#BFBFBB" noshade></td>
  </tr>
  <tr>
    <td><b>Search using single and multiple character wildcard searches</b></td>
  </tr>
  <tr>
    <td>
      To perform a single character wildcard search use the &quot;?&quot; symbol:<br />
      <br />
      cam?aign<br />
      <br />
      To perform a multiple character wildcard search use the &quot;*&quot; symbol:<br />
      <br />
      ca*gn<br />
      campaign*<br />
      <br />
      Note: You cannot use a * or ? symbol as the first character of a search.
    </td>
  </tr>
  <tr>
</table>
<br />
<br />
<b>Advanced Query Syntax</b>
<br />
<br />
<table bgcolor="#DEDEDE" border="0" cellspacing="1" cellpadding="4" width="100%">
  <tr>
    <td><b>Fuzzy Search</b></td>
  </tr>
  <tr>
    <td>To do a fuzzy search, use the tilde &quot;~&quot; symbol at the end of a single word term.
      For example, to search for a term similar in spelling to &quot;roam&quot;, use the search:<br />
        <br />
        roam~<br />
        <br />
        This search will find terms like foam and roams.
    </td>
  </tr>
  <tr>
    <td><hr color="#BFBFBB" noshade></td>
  </tr>
<%--
  <tr>
    <td><b>Proximity Search</b></td>
  </tr>
  <tr>
    <td>To do a proximity search, use the tilde &quot;~&quot; symbol at the end of a phrase.
      For example, to search for &quot;fall&quot; and &quot;campaign&quot; within 10 words of
      each other in a document, use the search:<br />
      <br />
	    &quot;fall campaign&quot;~10
    </td>
  </tr>
--%>
</table>
<br />
<input type="button" value='<ccp:label name="button.close">Close</ccp:label>' onClick="window.close()" />
