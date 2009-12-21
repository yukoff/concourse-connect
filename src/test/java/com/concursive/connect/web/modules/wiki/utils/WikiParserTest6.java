/*
 * ConcourseConnect
 * Copyright 2009 Concursive Corporation
 * http://www.concursive.com
 *
 * This file is part of ConcourseConnect, an open source social business
 * software and community platform.
 *
 * Concursive ConcourseConnect is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, version 3 of the License.
 *
 * Under the terms of the GNU Affero General Public License you must release the
 * complete source code for any application that uses any part of ConcourseConnect
 * (system header files and libraries used by the operating system are excluded).
 * These terms must be included in any work that has ConcourseConnect components.
 * If you are developing and distributing open source applications under the
 * GNU Affero General Public License, then you are free to use ConcourseConnect
 * under the GNU Affero General Public License.
 *
 * If you are deploying a web site in which users interact with any portion of
 * ConcourseConnect over a network, the complete source code changes must be made
 * available.  For example, include a link to the source archive directly from
 * your web site.
 *
 * For OEMs, ISVs, SIs and VARs who distribute ConcourseConnect with their
 * products, and do not license and distribute their source code under the GNU
 * Affero General Public License, Concursive provides a flexible commercial
 * license.
 *
 * To anyone in doubt, we recommend the commercial license. Our commercial license
 * is competitively priced and will eliminate any confusion about how
 * ConcourseConnect can be used and distributed.
 *
 * ConcourseConnect is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with ConcourseConnect.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Attribution Notice: ConcourseConnect is an Original Work of software created
 * by Concursive Corporation
 */
package com.concursive.connect.web.modules.wiki.utils;

import com.concursive.commons.db.AbstractConnectionPoolTest;
import com.concursive.connect.web.modules.issues.dao.Ticket;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.members.dao.TeamMember;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.wiki.dao.Wiki;
import com.concursive.connect.web.modules.documents.dao.ImageInfo;

import java.sql.Timestamp;
import java.util.HashMap;

/**
 * Tests wiki parser functions
 *
 * @author matt rajkowski
 * @created April 8, 2008
 */
public class WikiParserTest6 extends AbstractConnectionPoolTest {

  public void testHtmlToWikiLinks() throws Exception {
    // Create a user
    User user = new User();
    user.setFirstName("John");
    user.setLastName("Smith");
    user.setUsername("jsmith@concursive.com");
    user.setPassword("none");
    user.setGroupId(1);
    user.setDepartmentId(1);
    user.setEnabled(true);
    user.insert(db, "127.0.0.1", mockPrefs);
    assertTrue("User not inserted", user.getId() > -1);
    // Create a project
    Project project = new Project();
    project.setTitle("Project SQL Test");
    //project.setUniqueId("project_for_" + user.getId());
    project.setShortDescription("Project SQL Test Description");
    project.setRequestDate(new Timestamp(System.currentTimeMillis()));
    project.setEstimatedCloseDate((Timestamp) null);
    project.setRequestedBy("Project SQL Test Requested By");
    project.setRequestedByDept("Project SQL Test Requested By Department");
    project.setBudgetCurrency("USD");
    project.setBudget("10000.75");
    project.setGroupId(user.getGroupId());
    project.setEnteredBy(user.getId());
    project.setModifiedBy(user.getId());
    project.insert(db);
    assertTrue("Project not inserted", project.getId() > -1);
    // Create a team member
    TeamMember teamMember = new TeamMember();
    teamMember.setProjectId(project.getId());
    teamMember.setUserId(user.getId());
    teamMember.setUserLevel(1);
    teamMember.setEnteredBy(user.getId());
    teamMember.setModifiedBy(user.getId());
    teamMember.insert(db);
    assertTrue("TeamMember not inserted", teamMember.getId() > -1);
    // Create a ticket
    Ticket ticket = new Ticket();
    ticket.setProblem("This is an issue");
    ticket.setContactId(user.getId());
    ticket.setProjectId(project.getId());
    ticket.setEnteredBy(user.getId());
    ticket.setModifiedBy(user.getId());
    ticket.insert(db);

    try {

      {
        Wiki thisWiki = new Wiki();
        thisWiki.setContent("[[Standard link]]");
        thisWiki.setProjectId(project.getId());
        // Parse it
        WikiToHTMLContext wikiContext = new WikiToHTMLContext(thisWiki, new HashMap<String, ImageInfo>(), user.getId(), false, "");
        String html = WikiToHTMLUtils.getHTML(wikiContext, db);
        assertEquals(
            "<p><a class=\"wikiLink newWiki\" href=\"/modify/" + project.getUniqueId() + "/wiki/Standard+link\">Standard link</a></p>\n", html);
        // Test the link
        WikiLink wikiLink = new WikiLink(project.getId(), thisWiki.getContent());
        assertEquals("wiki", wikiLink.getArea());
        assertEquals("Standard link", wikiLink.getName());
      }

      {
        Wiki thisWiki = new Wiki();
        thisWiki.setContent("[[Standard link|Renamed]]");
        thisWiki.setProjectId(project.getId());
        // Parse it
        WikiToHTMLContext wikiContext = new WikiToHTMLContext(thisWiki, new HashMap<String, ImageInfo>(), user.getId(), false, "");
        String html = WikiToHTMLUtils.getHTML(wikiContext, db);
        assertEquals(
            "<p><a class=\"wikiLink newWiki\" href=\"/modify/" + project.getUniqueId() + "/wiki/Standard+link\">Renamed</a></p>\n", html);
        // Test the link
        WikiLink wikiLink = new WikiLink(project.getId(), thisWiki.getContent());
        assertEquals("wiki", wikiLink.getArea());
        assertEquals("Renamed", wikiLink.getName());
        assertEquals("Standard link", wikiLink.getEntity());
      }

      {
        Wiki thisWiki = new Wiki();
        thisWiki.setContent("[[|" + project.getId() + ":issue|" + ticket.getId() + "|Some Ticket]]");
        thisWiki.setProjectId(project.getId());
        // Parse it
        WikiToHTMLContext wikiContext = new WikiToHTMLContext(thisWiki, new HashMap<String, ImageInfo>(), user.getId(), false, "");
        String html = WikiToHTMLUtils.getHTML(wikiContext, db);
        //============ WikiLink ============
        //project: 11714
        //area: issue
        //entity: 4937
        //name: Some Ticket
        //status: complex
        assertEquals(
            "<p><a class=\"wikiLink external\" href=\"/show/" + project.getUniqueId() + "/issue/" + ticket.getId() + "\">Some Ticket</a></p>\n", html);
        // Test the link
        WikiLink wikiLink = new WikiLink(project.getId(), thisWiki.getContent());
        assertEquals("issue", wikiLink.getArea());
        assertEquals("Some Ticket", wikiLink.getName());
        assertEquals(project.getId(), wikiLink.getProjectId());
        assertEquals(ticket.getId(), wikiLink.getEntityId());
        // Test the link without the project id specified
        WikiLink wikiLink2 = new WikiLink(thisWiki.getContent());
        assertEquals("issue", wikiLink2.getArea());
        assertEquals("Some Ticket", wikiLink2.getName());
        assertEquals(project.getId(), wikiLink2.getProjectId());
        assertEquals(ticket.getId(), wikiLink2.getEntityId());
      }

      {
        Wiki thisWiki = new Wiki();
        thisWiki.setContent("[[|" + project.getId() + ":issue|" + ticket.getId() + "]]");
        thisWiki.setProjectId(project.getId());
        // Parse it
        WikiToHTMLContext wikiContext = new WikiToHTMLContext(thisWiki, new HashMap<String, ImageInfo>(), user.getId(), false, "");
        String html = WikiToHTMLUtils.getHTML(wikiContext, db);
        assertEquals(
            "<p><a class=\"wikiLink external\" href=\"/show/" + project.getUniqueId() + "/issue/" + ticket.getId() + "\">&nbsp;</a></p>\n", html);
        // Test the link
        WikiLink wikiLink = new WikiLink(project.getId(), thisWiki.getContent());
        assertEquals("issue", wikiLink.getArea());
        assertEquals("", wikiLink.getName());
        assertEquals(project.getId(), wikiLink.getProjectId());
        assertEquals(ticket.getId(), wikiLink.getEntityId());
      }

      {
        // Test wiki link using the project unique id
        Wiki thisWiki = new Wiki();
        thisWiki.setContent("[[|" + project.getUniqueId() + ":issue|" + ticket.getId() + "]]");
        thisWiki.setProjectId(project.getId());
        // Parse it
        WikiToHTMLContext wikiContext = new WikiToHTMLContext(thisWiki, new HashMap<String, ImageInfo>(), user.getId(), false, "");
        String html = WikiToHTMLUtils.getHTML(wikiContext, db);
        assertEquals(
            "<p><a class=\"wikiLink external\" href=\"/show/" + project.getUniqueId() + "/issue/" + ticket.getId() + "\">&nbsp;</a></p>\n", html);
        // Test the link
        WikiLink wikiLink = new WikiLink(thisWiki.getContent());
        assertEquals("issue", wikiLink.getArea());
        assertEquals("", wikiLink.getName());
        assertEquals(project.getId(), wikiLink.getProjectId());
        assertEquals(ticket.getId(), wikiLink.getEntityId());
      }

      {
        // Convert wiki to html
        Wiki thisWiki = new Wiki();
        thisWiki.setContent("[[|:issue|" + ticket.getId() + "]]");
        thisWiki.setProjectId(project.getId());
        // Parse it
        WikiToHTMLContext wikiContext = new WikiToHTMLContext(thisWiki, new HashMap<String, ImageInfo>(), user.getId(), false, "");
        String html = WikiToHTMLUtils.getHTML(wikiContext, db);
        assertEquals(
            "<p><a class=\"wikiLink external\" href=\"/show/" + project.getUniqueId() + "/issue/" + ticket.getId() + "\">&nbsp;</a></p>\n", html);
        // Test the link
        WikiLink wikiLink = new WikiLink(project.getId(), thisWiki.getContent());
        assertEquals("issue", wikiLink.getArea());
        assertEquals("", wikiLink.getName());
        assertEquals(project.getId(), wikiLink.getProjectId());
        assertEquals(ticket.getId(), wikiLink.getEntityId());
      }

      {
        // Convert wiki to html
        Wiki thisWiki = new Wiki();
        thisWiki.setContent("[[|issue|" + ticket.getId() + "]]");
        thisWiki.setProjectId(project.getId());
        // Parse it
        WikiToHTMLContext wikiContext = new WikiToHTMLContext(thisWiki, new HashMap<String, ImageInfo>(), user.getId(), false, "");
        String html = WikiToHTMLUtils.getHTML(wikiContext, db);
        assertEquals(
            "<p><a class=\"wikiLink external\" href=\"/show/" + project.getUniqueId() + "/issue/" + ticket.getId() + "\">&nbsp;</a></p>\n", html);
        // Test the link
        WikiLink wikiLink = new WikiLink(project.getId(), thisWiki.getContent());
        assertEquals("issue", wikiLink.getArea());
        assertEquals("", wikiLink.getName());
        assertEquals(project.getId(), wikiLink.getProjectId());
        assertEquals(ticket.getId(), wikiLink.getEntityId());
      }

      {
        // Convert wiki to html
        Wiki thisWiki = new Wiki();
        thisWiki.setContent("[[http://www.concursive.com?hello=0&world=1 test]]");
        thisWiki.setProjectId(project.getId());
        // Parse it
        WikiToHTMLContext wikiContext = new WikiToHTMLContext(thisWiki, new HashMap<String, ImageInfo>(), user.getId(), false, "");
        String html = WikiToHTMLUtils.getHTML(wikiContext, db);
        assertEquals(
            "<p><a class=\"wikiLink external\" target=\"_blank\" href=\"http://www.concursive.com?hello=0&world=1\">test</a></p>\n", html);
        // Test the link
        WikiLink wikiLink = new WikiLink(project.getId(), "http://www.concursive.com?hello=0&world=1 test");
        assertEquals("test", wikiLink.getName());
        assertEquals("http://www.concursive.com?hello=0&world=1", wikiLink.getEntity());
        assertEquals(WikiLink.REFERENCE, wikiLink.getStatus());
        assertEquals("", wikiLink.getArea());
      }

      {
        // Test the link
        WikiLink wikiLink = new WikiLink(project.getId(), "[[http://www.concursive.com?hello=0&world=1]]");
        assertEquals("http://www.concursive.com?hello=0&world=1", wikiLink.getEntity());
        assertEquals(WikiLink.REFERENCE, wikiLink.getStatus());
        assertEquals("", wikiLink.getArea());
      }

    } catch (Exception e) {

    } finally {
      project.delete(db, null);
      user.delete(db);
    }
  }
}