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
 * Tests wiki streaming video parsing
 *
 * @author matt
 * @created April 23, 2010
 */
public class WikiParserStreamingVideoTest extends AbstractConnectionPoolTest {

  // Various ways to embed a video
  protected final static String wikiSample = "[[Video:http://www.livestream.com/spaceflightnow]]\n";
  protected final static String wikiSample2 = "[[Video:http://www.justin.tv/startuplessonslearned]]\n";
  protected final static String wikiSample3 = "[[Video:<object classid=\"clsid:d27cdb6e-ae6d-11cf-96b8-444553540000\" width=\"400\" height=\"320\" id=\"utv193564\"><param name=\"flashvars\" value=\"autoplay=false&amp;brand=embed&amp;cid=562961&amp;locale=en_US\"/><param name=\"allowfullscreen\" value=\"true\"/><param name=\"allowscriptaccess\" value=\"always\"/><param name=\"movie\" value=\"http://www.ustream.tv/flash/live/1/562961\"/><embed flashvars=\"autoplay=false&amp;brand=embed&amp;cid=562961&amp;locale=en_US\" width=\"400\" height=\"320\" allowfullscreen=\"true\" allowscriptaccess=\"always\" id=\"utv193564\" name=\"utv_n_644694\" src=\"http://www.ustream.tv/flash/live/1/562961\" type=\"application/x-shockwave-flash\" /></object><a href=\"http://www.ustream.tv/\" style=\"padding: 2px 0px 4px; width: 400px; background: #ffffff; display: block; color: #000000; font-weight: normal; font-size: 10px; text-decoration: underline; text-align: center;\" target=\"_blank\">Streaming live video by Ustream</a>]]\n";
  protected final static String wikiSample3b = "[[Video:<object classid=\"clsid:d27cdb6e-ae6d-11cf-96b8-444553540000\" height=\"320\" id=\"utv193564\" width=\"400\"><param name=\"flashvars\" value=\"autoplay=true&amp;brand=embed&amp;cid=562961&amp;locale=en_US\"/><param name=\"allowfullscreen\" value=\"true\"/><param name=\"allowscriptaccess\" value=\"always\"/><param name=\"movie\" value=\"http://www.ustream.tv/flash/live/1/562961\"/><embed allowfullscreen=\"true\" allowscriptaccess=\"always\" flashvars=\"autoplay=true&amp;brand=embed&amp;cid=562961&amp;locale=en_US\" height=\"320\" id=\"utv193564\" name=\"utv_n_644694\" src=\"http://www.ustream.tv/flash/live/1/562961\" type=\"application/x-shockwave-flash\" width=\"400\"/></object>]]\n";
  protected final static String wikiSample4 = "[[Video:http://qik.com/zeroio]]\n";

  protected final static String htmlSample =
      "<p><object width=\"400\" height=\"300\" id=\"lsplayer\" classid=\"clsid:D27CDB6E-AE6D-11cf-96B8-444553540000\"><param name=\"movie\" value=\"http://cdn.livestream.com/grid/LSPlayer.swf?channel=spaceflightnow&amp;autoPlay=true\"></param><param name=\"allowScriptAccess\" value=\"always\"></param><param name=\"allowFullScreen\" value=\"true\"></param><embed name=\"lsplayer\" wmode=\"transparent\" src=\"http://cdn.livestream.com/grid/LSPlayer.swf?channel=spaceflightnow&amp;autoPlay=true\" width=\"400\" height=\"300\" allowScriptAccess=\"always\" allowFullScreen=\"true\" type=\"application/x-shockwave-flash\"></embed></object></p>\n";

  protected final static String htmlSample2 =
      "<p><object type=\"application/x-shockwave-flash\" height=\"300\" width=\"400\" id=\"live_embed_player_flash\" data=\"http://www.justin.tv/widgets/live_embed_player.swf?channel=startuplessonslearned\" bgcolor=\"#000000\"><param name=\"allowFullScreen\" value=\"true\" /><param name=\"allowScriptAccess\" value=\"always\" /><param name=\"allowNetworking\" value=\"all\" /><param name=\"movie\" value=\"http://www.justin.tv/widgets/live_embed_player.swf\" /><param name=\"flashvars\" value=\"channel=startuplessonslearned&auto_play=true&start_volume=25\" /></object></p>\n";

  protected final static String htmlSample3 =
      "<p><object classid=\"clsid:d27cdb6e-ae6d-11cf-96b8-444553540000\" width=\"400\" height=\"320\" id=\"utv193564\"><param name=\"flashvars\" value=\"autoplay=true&amp;brand=embed&amp;cid=562961&amp;locale=en_US\"/><param name=\"allowfullscreen\" value=\"true\"/><param name=\"allowscriptaccess\" value=\"always\"/><param name=\"movie\" value=\"http://www.ustream.tv/flash/live/1/562961\"/><embed flashvars=\"autoplay=true&amp;brand=embed&amp;cid=562961&amp;locale=en_US\" width=\"400\" height=\"320\" allowfullscreen=\"true\" allowscriptaccess=\"always\" id=\"utv193564\" name=\"utv_n_644694\" src=\"http://www.ustream.tv/flash/live/1/562961\" type=\"application/x-shockwave-flash\" /></object></p>\n";

  protected final static String htmlSample4 =
      "<p><object classid=\"clsid:d27cdb6e-ae6d-11cf-96b8-444553540000\" codebase=\"http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=9,0,115,0\" width=\"400\" height=\"300\" id=\"qikPlayer\" align=\"middle\"><param name=\"allowScriptAccess\" value=\"sameDomain\" /><param name=\"allowFullScreen\" value=\"true\" /><param name=\"movie\" value=\"http://assets0.qik.com/swfs/qikPlayer5.swf?1271067225\" /><param name=\"quality\" value=\"high\" /><param name=\"bgcolor\" value=\"#000000\" /><param name=\"FlashVars\" value=\"username=zeroio\" /><embed src=\"http://assets0.qik.com/swfs/qikPlayer5.swf?1271067225\" quality=\"high\" bgcolor=\"#000000\" width=\"400\" height=\"300\" name=\"qikPlayer\" align=\"middle\" allowScriptAccess=\"sameDomain\" allowFullScreen=\"true\" type=\"application/x-shockwave-flash\" pluginspage=\"http://www.macromedia.com/go/getflashplayer\" FlashVars=\"username=zeroio\"></embed></object></p>\n";

  public void testWikiToHTMLVideo1() throws Exception {
    // Convert wiki to html
    Wiki thisWiki = new Wiki();
    thisWiki.setContent(wikiSample);
    WikiToHTMLContext wikiContext = new WikiToHTMLContext(thisWiki, new HashMap<String, ImageInfo>(), -1, false, "");
    String html = WikiToHTMLUtils.getHTML(wikiContext, null);
    assertNotNull(html);
    assertEquals(htmlSample, html);
  }

  public void testWikiToHTMLVideo2() throws Exception {
    // Convert wiki to html
    Wiki thisWiki = new Wiki();
    thisWiki.setContent(wikiSample2);
    WikiToHTMLContext wikiContext = new WikiToHTMLContext(thisWiki, new HashMap<String, ImageInfo>(), -1, false, "");
    String html = WikiToHTMLUtils.getHTML(wikiContext, null);
    assertNotNull(html);
    assertEquals(htmlSample2, html);
  }

  public void testWikiToHTMLVideo3() throws Exception {
    // Convert wiki to html
    Wiki thisWiki = new Wiki();
    thisWiki.setContent(wikiSample3);
    WikiToHTMLContext wikiContext = new WikiToHTMLContext(thisWiki, new HashMap<String, ImageInfo>(), -1, false, "");
    String html = WikiToHTMLUtils.getHTML(wikiContext, null);
    assertNotNull(html);
    assertEquals(htmlSample3, html);
  }

  public void testWikiToHTMLVideo4() throws Exception {
    // Convert wiki to html
    Wiki thisWiki = new Wiki();
    thisWiki.setContent(wikiSample4);
    WikiToHTMLContext wikiContext = new WikiToHTMLContext(thisWiki, new HashMap<String, ImageInfo>(), -1, false, "");
    String html = WikiToHTMLUtils.getHTML(wikiContext, null);
    assertNotNull(html);
    assertEquals(htmlSample4, html);
  }



  public void testHTMLVideoToWiki1() throws Exception {
    // Stage a project for the cache
    Project project = new Project();
    project.setId(9999999);
    project.setTitle("Some Project");
    project.setUniqueId("some-project");
    CacheUtils.updateValue(Constants.SYSTEM_PROJECT_CACHE, 9999999, project);
    CacheUtils.updateValue(Constants.SYSTEM_PROJECT_UNIQUE_ID_CACHE, "some-project", 9999999);

    String wiki = HTMLToWikiUtils.htmlToWiki(htmlSample, "", project.getId());
    assertEquals(wikiSample, wiki);
  }

  public void testHTMLVideoToWiki2() throws Exception {
    // Stage a project for the cache
    Project project = new Project();
    project.setId(9999999);
    project.setTitle("Some Project");
    project.setUniqueId("some-project");
    CacheUtils.updateValue(Constants.SYSTEM_PROJECT_CACHE, 9999999, project);
    CacheUtils.updateValue(Constants.SYSTEM_PROJECT_UNIQUE_ID_CACHE, "some-project", 9999999);

    String wiki = HTMLToWikiUtils.htmlToWiki(htmlSample2, "", project.getId());
    assertEquals(wikiSample2, wiki);
  }

  public void testHTMLVideoToWiki3() throws Exception {
    // Stage a project for the cache
    Project project = new Project();
    project.setId(9999999);
    project.setTitle("Some Project");
    project.setUniqueId("some-project");
    CacheUtils.updateValue(Constants.SYSTEM_PROJECT_CACHE, 9999999, project);
    CacheUtils.updateValue(Constants.SYSTEM_PROJECT_UNIQUE_ID_CACHE, "some-project", 9999999);

    String wiki = HTMLToWikiUtils.htmlToWiki(htmlSample3, "", project.getId());
    assertEquals(wikiSample3b, wiki);
  }

  public void testHTMLVideoToWiki4() throws Exception {
    // Stage a project for the cache
    Project project = new Project();
    project.setId(9999999);
    project.setTitle("Some Project");
    project.setUniqueId("some-project");
    CacheUtils.updateValue(Constants.SYSTEM_PROJECT_CACHE, 9999999, project);
    CacheUtils.updateValue(Constants.SYSTEM_PROJECT_UNIQUE_ID_CACHE, "some-project", 9999999);

    String wiki = HTMLToWikiUtils.htmlToWiki(htmlSample4, "", project.getId());
    assertEquals(wikiSample4, wiki);
  }

  public void testRoundtripWikiVideoLink() throws Exception {
    // Convert wiki to html
    Wiki thisWiki = new Wiki();
    thisWiki.setContent(wikiSample2);
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
    assertEquals(wikiSample2, wiki);
  }
}