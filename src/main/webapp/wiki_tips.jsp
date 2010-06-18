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
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<strong>Basic Text Formatting</strong><br />
<br />
<table border="1" cellspacing="1" cellpadding="4" width="100%">
  <tr>
    <td width="50%"><strong>What you see</strong></td>
    <td width="50%"><strong>What you type</strong></td>
  </tr>
  <tr>
    <td>Three apostrophes for <strong>Bold</strong></td>
    <td>'''Bold'''</td>
  </tr>
  <tr>
    <td>Two apostrophes for <em>Italic</em></td>
    <td>''Italic''</td>
  </tr>
  <tr>
    <td>Five apostrophes for <strong><em>Bold Italic</em></strong></td>
    <td>'''''Bold Italic'''''</td>
  </tr>
</table>
<br />

<strong>Paragrah Formatting</strong><br />
<br />
<table border="1" cellspacing="1" cellpadding="4" width="100%">
  <tr>
    <td width="50%"><strong>What you see</strong></td>
    <td width="50%"><strong>What you type</strong></td>
  </tr>
  <tr>
    <td>Sentences must start at the left and do not require linebreaks, they will wrap automatically.</td>
    <td>Sentences must start at the left and do not require linebreaks, they will wrap automatically.</td>
  </tr>
  <tr>
    <td valign="top">
      To preserve formatting, indent the line with a space.<br />
      <pre>This line is preserved
because it starts with a space.</pre>
    </td>
    <td valign="top">
      To preserve formatting, indent the line with a space.<br />
      <br />
      &nbsp;This line is preserved<br />
      because it starts with a space.
    </td>
  </tr>
  <tr>
    <td valign="top">
      To preserve formatting for several lines of text, you can put
      &lt;code&gt; and &lt;/code&gt; tags around the text.
      <pre>These lines are preserved
      This is line 2.</pre>
    </td>
    <td valign="top">
      To preserve formatting for several lines of text, you can put
      &lt;code&gt; and &lt;/code&gt; tags around the text.<br />
      <br />
      &lt;code&gt;<br />
      These lines are preserved<br />
This is line 2.<br />
      &lt;/code&gt;
    </td>
  </tr>
</table>
<br />

<strong>Organizing your writing</strong><br />
<br />
<table border="1" cellspacing="1" cellpadding="4" width="100%">
  <tr>
    <td width="50%"><strong>What you see</strong></td>
    <td width="50%"><strong>What you type</strong></td>
  </tr>
  <tr>
    <td valign="top">
      <h1>Section headings</h1>
      Headings organize your writing into sections. Headings are used to automatically generate a table of contents.<br />
      <br />
      <h2>Subsection</h2>
      Using more equals signs creates a subsection.<br />
      <br />
      <h3>A smaller subsection</h3>
      Don't skip levels, like from two to four equals signs. Start with two equals signs; don't use single equals signs.
    </td>
    <td valign="top">
      == Section Headings ==<br />
      Headings organize your writing into sections. Headings are used to automatically generate a table of contents.<br />
      <br />
      === Subsection ===<br />
      Using more equals signs creates a subsection.<br />
      <br />
      ==== Another Subsection ====<br />
      Don't skip levels, like from two to four equals signs. Start with two equals signs; don't use single equals signs.
    </td>
  </tr>
  <tr>
    <td valign="top" class="wikiDisplay">
      For a bulleted list, use asterisks:<br />
      <ul>
        <li>Level 1</li>
        <ul><li>Level 2</li></ul>
        <li>Back to Level 1</li>
      </ul>
      A new line marks the end of the list.
    </td>
    <td valign="top">
      For a bulleted list, use asterisks:<br />
      * Level 1<br />
      ** Level 2<br />
      * Back to Level 1<br />
      <br />
      A new line marks the end of the list.
    </td>
  </tr>
  <tr>
    <td valign="top" class="wikiDisplay">
      For a numbered list:<br />
      <ol>
        <li>Level 1</li>
        <ol><li>Level 2</li></ol>
        <li>Back to Level 1</li>
      </ol>
      A new line marks the end of the list.
    </td>
    <td valign="top">
      For a numbered list:<br />
      # Level 1<br />
      ## Level 2<br />
      # Back to Level 1<br />
      <br />
      A new line marks the end of the list.
    </td>
  </tr>
</table>
<br />

<strong>Creating links to other pages</strong><br />
<br />
Wikis allow for content to link to other wiki content that may or may
not exist yet.  This enables for the quick creation of content by one or
more content writers.  There are several types of links that can be created.
These links can point to other internal wiki content, or can link to external
content on the internet.<br />
<br />
<table border="1" cellspacing="1" cellpadding="4" width="100%">
  <tr>
    <td width="50%"><strong>What you see</strong></td>
    <td width="50%"><strong>What you type</strong></td>
  </tr>
  <tr>
    <td valign="top">Here's a link to a page called <a href="#">Project Management</a></td>
    <td valign="top">Here's a link to a page called [[Project Management]]</td>
  </tr>
  <tr>
    <td valign="top">Here's another link to the page called Project Management, but the user sees <a href="#">Project Management System</a> instead of Project Management</td>
    <td valign="top">Here's another link to the page called Project Management, but the user sees [[Project Management|Project Management System]] instead of Project Management</td>
  </tr>
  <tr>
    <td valign="top">Here's a link to <a class="wikiLink external" target="_blank" href="http://www.concursive.com">Concursive</a></td>
    <td valign="top">Here's a link to [[http://www.concursive.com Concursive]]</td>
  </tr>
</table>
<br />

<strong>Inserting images</strong><br />
<br />
An image must first be uploaded using the &quot;Images&quot; utility.
Images can then be inserted into a wiki using one of several styles.
Image options can be combined.<br />
<br />
<table border="1" cellspacing="1" cellpadding="4" width="100%">
  <tr>
    <td width="50%"><strong>What you see</strong></td>
    <td width="50%"><strong>What you type</strong></td>
  </tr>
  <tr>
    <td valign="top">Image by itself</td>
    <td valign="top">[[Image:Image by itself.jpg]]</td>
  </tr>
  <tr>
    <td valign="top">Image with a caption<br />This is the caption</td>
    <td valign="top">[[Image:Image with a caption.jpg|This is the caption]]</td>
  </tr>
  <tr>
    <td valign="top">Thumbnail image</td>
    <td valign="top">[[Image:Thumbnail image.jpg|thumb]]</td>
  </tr>
  <tr>
    <td valign="top">Thumbnail aligned right</td>
    <td valign="top">[[Image:Thumbnail image.jpg|right]]</td>
  </tr>
  <tr>
    <td valign="top">Thumbnail aligned left</td>
    <td valign="top">[[Image:Thumbnail image.jpg|left]]</td>
  </tr>
</table>
<br />

<strong>Creating tables</strong><br />
<br />
Simple tables can be used.  These tables can have a header with any number of rows and cells.<br />
<br />
<table border="1" cellspacing="1" cellpadding="4" width="100%">
  <tr>
    <td width="50%"><strong>What you see</strong></td>
    <td width="50%"><strong>What you type</strong></td>
  </tr>
  <tr>
    <td valign="top"><table border="1"><tr><th>header</th></tr></table></td>
    <td valign="top">||header||</td>
  </tr>
  <tr>
    <td valign="top"><table border="1"><tr><th colspan="2">header spans two cells</th></tr><tr><td>cell 1</td><td>cell 2</td></tr></table></td>
    <td valign="top">||||header spans two cells||<br />|cell 1|cell 2|</td>
  </tr>
</table>
<br />

<strong>Reserved Content Characters</strong><br />
<br />
<table border="1" cellspacing="1" cellpadding="4" width="100%">
  <tr>
    <td width="50%"><strong>What you see</strong></td>
    <td width="50%"><strong>What you type</strong></td>
  </tr>
  <tr>
    <td>Escape content characters like:<br />*<br />|<br />#<br />=<br />[<br />]</td>
    <td>Escape content characters like:<br />\*<br />\|<br /><c:out value="\#"/> <br />\=<br />\[<br />\]</td>
  </tr>
</table>
<br />

<input type="button" value="Close" onClick="window.close()" />
