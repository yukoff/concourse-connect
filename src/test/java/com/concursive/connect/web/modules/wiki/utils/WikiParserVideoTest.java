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
import com.concursive.connect.Constants;
import com.concursive.connect.cache.utils.CacheUtils;
import com.concursive.connect.web.modules.documents.dao.ImageInfo;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.wiki.dao.Wiki;

import java.util.HashMap;

/**
 * Tests wiki video parsing
 *
 * @author matt
 * @created January 24, 2008
 */
public class WikiParserVideoTest extends AbstractConnectionPoolTest {

  // Various ways to embed a video
  protected final static String wikiSample = "[[Video:http://www.youtube.com?v=3LkNlTNHZzE&hl=true]]\n";
  protected final static String wikiSample2 = "[[Video:http://www.youtube.com/v/3LkNlTNHZzE]]\n";
  protected final static String wikiSample3 = "[[Video:http://www.youtube.com/v/3LkNlTNHZzE|This is a caption]]\n";

  protected final static String youtubeHtmlSample =
      "<p>" +
          "<object width=\"425\" height=\"344\" type=\"application/x-shockwave-flash\" data=\"http://www.youtube.com/v/3LkNlTNHZzE\">" +
          "<param name=\"movie\" value=\"http://www.youtube.com/v/3LkNlTNHZzE\" />" +
          "<param name=\"wmode\" value=\"transparent\" /></object>" +
          "</p>\n";

  protected final static String justinTVHtmlSample =
      "<object type=\"application/x-shockwave-flash\" height=\"300\" width=\"400\" id=\"live_embed_player_flash\" data=\"http://www.justin.tv/widgets/live_embed_player.swf?channel=redspades\" bgcolor=\"#000000\"><param name=\"allowFullScreen\" value=\"true\" /><param name=\"allowScriptAccess\" value=\"always\" /><param name=\"allowNetworking\" value=\"all\" /><param name=\"movie\" value=\"http://www.justin.tv/widgets/live_embed_player.swf\" /><param name=\"flashvars\" value=\"channel=redspades&auto_play=false&start_volume=25\" /></object><a href=\"http://www.justin.tv/redspades#r=TRp3S20~&s=em\" class=\"trk\" style=\"padding:2px 0px 4px; display:block; width:345px; font-weight:normal; font-size:10px; text-decoration:underline; text-align:center;\">Watch live video from RedSpades Live! Show on Justin.tv</a>\n";

  protected final static String vimeoHtmlSample =
      "<p>\n" +
          "<object width=\"400\" height=\"300\" value=\"http://vimeo.com/moogaloop.swf?clip_id=11513988&amp;server=vimeo.com&amp;show_title=1&amp;show_byline=1&amp;show_portrait=0&amp;color=&amp;fullscreen=1\">\n" +
          "<param name=\"allowScriptAccess\" value=\"always\" />\n" +
          "<param name=\"allowFullScreen\" value=\"true\" /><embed type=\"application/x-shockwave-flash\" width=\"400\" height=\"300\" src=\"http://vimeo.com/moogaloop.swf?clip_id=11513988&amp;server=vimeo.com&amp;show_title=1&amp;show_byline=1&amp;show_portrait=0&amp;color=&amp;fullscreen=1\" allowfullscreen=\"true\" allowscriptaccess=\"always\"></embed>\n" +
          "</object>\n" +
          "&nbsp;</p>\n";

  // Various ways to insert a link to a video
  protected final static String wikiSample4 = "[[Video:http://www.youtube.com/v/3LkNlTNHZzE|thumb]]\n";
  protected final static String wikiSample5 = "[[Video:http://www.youtube.com/v/3LkNlTNHZzE|thumb|This is a caption]]\n";

  protected final static String htmlSample2 =
      "<p>" +
          "<div class=\"video-thumbnail\">" +
          "<a href=\"http://www.youtube.com/v/3LkNlTNHZzE?autoplay=1\" title=\"Click to play\" target=\"_blank\"><img src=\"http://img.youtube.com/vi/3LkNlTNHZzE/default.jpg\" /><span>&#9658;</span></a>" +
          "</div>" +
          "</p>\n";

  public void testWikiToHTMLVideo() throws Exception {
    // Convert wiki to html
    Wiki thisWiki = new Wiki();
    thisWiki.setContent(wikiSample);
    WikiToHTMLContext wikiContext = new WikiToHTMLContext(thisWiki, new HashMap<String, ImageInfo>(), -1, false, "");
    String html = WikiToHTMLUtils.getHTML(wikiContext, null);
    assertNotNull(html);
    assertEquals(youtubeHtmlSample, html);
  }

  public void testWikiToHTMLVideoPage() throws Exception {
    // Convert wiki to html
    Wiki thisWiki = new Wiki();
    thisWiki.setContent(wikiSample2);
    WikiToHTMLContext wikiContext = new WikiToHTMLContext(thisWiki, new HashMap<String, ImageInfo>(), -1, false, "");
    String html = WikiToHTMLUtils.getHTML(wikiContext, null);
    assertNotNull(html);
    assertEquals(youtubeHtmlSample, html);
  }

  public void testWikiToHTMLVideoPageCaption() throws Exception {
    // @note currently a caption is ignored
    // Convert wiki to html
    Wiki thisWiki = new Wiki();
    thisWiki.setContent(wikiSample3);
    WikiToHTMLContext wikiContext = new WikiToHTMLContext(thisWiki, new HashMap<String, ImageInfo>(), -1, false, "");
    String html = WikiToHTMLUtils.getHTML(wikiContext, null);
    assertNotNull(html);
    assertEquals(youtubeHtmlSample, html);
  }


  public void testHTMLVideoToWiki() throws Exception {
    // Stage a project for the cache
    Project project = new Project();
    project.setId(9999999);
    project.setTitle("Some Project");
    project.setUniqueId("some-project");
    CacheUtils.updateValue(Constants.SYSTEM_PROJECT_CACHE, 9999999, project);
    CacheUtils.updateValue(Constants.SYSTEM_PROJECT_UNIQUE_ID_CACHE, "some-project", 9999999);

    // Test YouTube
    String youTubeWiki = HTMLToWikiUtils.htmlToWiki(youtubeHtmlSample, "", project.getId());
    assertEquals(wikiSample2, youTubeWiki);

    // Test JustinTV
    String justinTvWiki = HTMLToWikiUtils.htmlToWiki(justinTVHtmlSample, "", project.getId());
    assertTrue("Incorrect format: " + justinTvWiki, justinTvWiki.contains("[[Video:http://www.justin.tv/redspades]]"));

    // Test Vimeo
    String vimeoWiki = HTMLToWikiUtils.htmlToWiki(vimeoHtmlSample, "", project.getId());
    assertTrue("Incorrect format: " + vimeoWiki, vimeoWiki.contains("[[Video:http://www.vimeo.com/11513988]]"));
  }

  public void testRoundtripWikiVideoLink() throws Exception {
    // Convert wiki to html
    Wiki thisWiki = new Wiki();
    thisWiki.setContent(wikiSample4);
    WikiToHTMLContext wikiContext = new WikiToHTMLContext(thisWiki, new HashMap<String, ImageInfo>(), -1, false, "");
    String html = WikiToHTMLUtils.getHTML(wikiContext, null);
    assertNotNull(html);
    assertEquals(htmlSample2, html);

    // Stage a project for the cache
    Project project = new Project();
    project.setId(9999999);
    project.setTitle("Some Project");
    project.setUniqueId("some-project");
    CacheUtils.updateValue(Constants.SYSTEM_PROJECT_CACHE, 9999999, project);
    CacheUtils.updateValue(Constants.SYSTEM_PROJECT_UNIQUE_ID_CACHE, "some-project", 9999999);

    // Convert html to wiki
    String wiki = HTMLToWikiUtils.htmlToWiki(htmlSample2, "", project.getId());
    assertEquals(wikiSample4, wiki);
  }
}
