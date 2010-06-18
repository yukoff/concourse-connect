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

import com.concursive.connect.Constants;
import com.concursive.connect.web.modules.documents.dao.ImageInfo;
import com.concursive.connect.web.modules.wiki.dao.Wiki;
import com.concursive.connect.web.modules.wiki.dao.WikiList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Class to manipulate wiki objects
 *
 * @author matt rajkowski
 * @version $Id$
 * @created February 7, 2006
 */
public class WikiUtils {

  private static Log LOG = LogFactory.getLog(WikiUtils.class);

  public static void countDiff(DiffCounter counter, String diff) {
    int UNDEFINED = -1;
    int DELETE = 1;
    int CHANGE_TO = 2;
    int INSERT = 3;
    int CHANGE_FROM = 4;
    int MOVED = 5;
    int mode = UNDEFINED;
    String line = null;
    int method = 1;
    int count = 0;
    try {
      BufferedReader in = new BufferedReader(new StringReader(diff));
      while ((line = in.readLine()) != null) {
        ++count;
        if (count == 1 && line.startsWith(">>>>")) {
          method = 2;
        }
        if (method == 1) {
          // Unix normal diff format
          if (!line.startsWith("> ") &&
              !line.startsWith("< ") &&
              !line.startsWith("---")) {
            /*
            if (line.indexOf("a") > -1) {
              int added = 1;
              String range = line.substring(line.indexOf("a") + 1);
              if (range.indexOf(",") > -1) {
                added += (Integer.parseInt(range.indexOf(",") + 1));
              }


              counter.lineAdded();
            } else if (line.indexOf("c") > -1) {

            } else if (line.indexOf("d") > -1) {

            }
*/

          }


        } else {
          // JDiff format
          if (line.startsWith(">>>> DELETE AT ")) {
            mode = DELETE;
            counter.update();
            continue;
          } else if (line.startsWith(">>>> ") && line.endsWith(" CHANGED FROM")) {
            mode = CHANGE_FROM;
            counter.update();
            continue;
          } else if (line.startsWith(">>>>     CHANGED TO")) {
            mode = CHANGE_TO;
            continue;
          } else if (line.startsWith(">>>> INSERT BEFORE ")) {
            mode = INSERT;
            counter.update();
            continue;
          } else if (line.startsWith(">>>> ") &&
              line.indexOf(" THRU ") > 0 &&
              line.indexOf(" MOVED TO BEFORE ") > 0) {
            mode = MOVED;
            counter.update();
            continue;
          } else if (line.startsWith(">>>> End of differences.")) {
            break;
          }
          if (mode == DELETE) {
            counter.lineDeleted(1);
          } else if (mode == INSERT) {
            counter.lineAdded(1);
          } else if (mode == CHANGE_FROM) {
            counter.lineChangedFrom();
          } else if (mode == CHANGE_TO) {
            counter.lineChangedTo();
          }
        }
      }
      counter.update();
      in.close();
    } catch (Exception e) {
      LOG.error("countDiff error", e);
    }
  }

  public static HashMap<String, ImageInfo> buildImageInfo(Connection db, int projectId) throws SQLException {
    HashMap<String, ImageInfo> images = new HashMap<String, ImageInfo>();
    // Full size image
    PreparedStatement pst = db.prepareStatement(
        "SELECT client_filename, filename, image_width, image_height, version " +
            "FROM project_files " +
            "WHERE link_module_id = ? " +
            "AND link_item_id = ? ");
    pst.setInt(1, Constants.PROJECT_WIKI_FILES);
    pst.setInt(2, projectId);
    ResultSet rs = pst.executeQuery();
    while (rs.next()) {
      ImageInfo image = new ImageInfo(rs);
      images.put(image.getFilename(), image);
    }
    rs.close();
    pst.close();
    return images;
  }

  public static void updatePageLinks(Connection db, Wiki wiki) throws SQLException {
    // Delete any items that no longer exist
    // Add any new items
    ArrayList pageList = getPageLinks(wiki);
    Iterator i = pageList.iterator();
    while (i.hasNext()) {
      String subject = (String) i.next();
      LOG.debug("updatePageLinks - PageLink: " + subject);
    }
  }

  /**
   * Recursively determine the latest modified date
   *
   * @param wiki
   * @param traverse
   * @param db
   * @return the latest modification date of associated wikis
   * @throws SQLException
   */
  public static Timestamp getLatestModifiedDate(Wiki wiki, boolean traverse, Connection db) throws SQLException {
    if (!traverse) {
      return wiki.getModified();
    }
    Timestamp latest = wiki.getModified();
    ArrayList children = getPageLinks(wiki);
    ArrayList scanned = new ArrayList();
    return getLatestModifiedDate(wiki, db, children, latest, scanned);
  }

  private static Timestamp getLatestModifiedDate(Wiki wiki, Connection db, ArrayList children, Timestamp latest, ArrayList scanned) throws SQLException {
    Iterator i = children.iterator();
    while (i.hasNext()) {
      String pageLink = (String) i.next();
      LOG.debug("Checking getLatestModifiedDate: " + pageLink);
      if (!scanned.contains(pageLink)) {
        scanned.add(pageLink);
        Wiki thisWiki = WikiList.queryBySubject(db, pageLink, wiki.getProjectId());
        if (thisWiki.getId() != -1) {
          if (thisWiki.getModified().after(latest)) {
            latest = thisWiki.getModified();
          }
          latest = getLatestModifiedDate(thisWiki, db, getPageLinks(thisWiki), latest, scanned);
        }
      }
    }
    return latest;
  }

  public static ArrayList getPageLinks(Wiki wiki) {
    ArrayList pageList = new ArrayList();
    // Read the lines and grab the [[ ]] strings.  For each, if not an image
    // then get the first phrase if more than 1 separated by a |
    try {
      BufferedReader in = new BufferedReader(new StringReader(wiki.getContent()));
      String line = null;
      int startIndex = -1;
      int endIndex = -1;
      while ((line = in.readLine()) != null) {

        startIndex = line.indexOf("[[");
        if (startIndex > -1) {
          endIndex = line.indexOf("]]", startIndex);
        }
        while (endIndex > -1) {
          String link = line.substring(startIndex + 2, endIndex);
          if (!link.startsWith("Image:") &&
              !link.startsWith("image:") &&
              !link.startsWith("http://") &&
              !link.startsWith("https://")) {
            if (link.indexOf("|") > -1) {
              link = link.substring(0, link.indexOf("|"));
            }
            pageList.add(link);
          }
          startIndex = line.indexOf("[[", endIndex);
          if (startIndex > -1) {
            endIndex = line.indexOf("]]", startIndex);
          } else {
            endIndex = -1;
          }
        }
      }
      in.close();
    } catch (Exception e) {
      return null;
    }
    return pageList;
  }

  public static String merge(Wiki wiki, String sectionMarkup, int editSectionId) throws IOException {
    // Track the line reading
    int sectionIdCount = 0;
    int currentHeaderLevel = 1;
    int editSectionHeaderLevel = -1;
    boolean canAppend = true;
    boolean firstLine = true;

    // Hold the merged result
    StringBuffer sb = new StringBuffer();

    // Find the headers
    BufferedReader in = new BufferedReader(new StringReader(wiki.getContent()));
    String line = null;
    while ((line = in.readLine()) != null) {
      if (!firstLine) {
        if (canAppend) {
          sb.append(WikiToHTMLUtils.CRLF);
        }
      } else {
        firstLine = false;
      }
      if (line.startsWith("=") && line.endsWith("=")) {
        // determine the header level
        int hCount = WikiToHTMLUtils.parseHCount(line, "=");
        if (hCount > 6) {
          hCount = 6;
        }
        // Keep track of which header was found
        sectionIdCount += 1;
        currentHeaderLevel = hCount;
        if (editSectionId > 0) {
          if (editSectionId == sectionIdCount) {
            editSectionHeaderLevel = currentHeaderLevel;
            sb.append(sectionMarkup).append(WikiToHTMLUtils.CRLF);
            canAppend = false;
          }
          if (sectionIdCount > editSectionId && currentHeaderLevel <= editSectionHeaderLevel) {
            canAppend = true;
          }
        }
      }
      if (canAppend) {
        sb.append(line);
      }
    }
    return (sb.toString());
  }

  /**
   * Merge the forms from the original wiki into the modified markup
   *
   * @param wiki
   * @param wikiMarkup
   * @return
   * @throws IOException
   */
  public static String merge(Wiki wiki, String wikiMarkup) throws IOException {
    // Hold the merged result
    StringBuffer sb = new StringBuffer();
    if (wiki.getContent().startsWith("[{form")) {
      // Copy the form, then append the content
      sb.append(wiki.getContent().substring(0, wiki.getContent().indexOf("+++") + 3));
      sb.append("\n");
      sb.append(wikiMarkup);
    } else {
      // Copy the content, then append the form
      sb.append(wikiMarkup);
      if (!wikiMarkup.endsWith("\n")) {
        sb.append("\n");
      }
      sb.append(wiki.getContent().substring(wiki.getContent().indexOf("[{form")));
      if (!sb.toString().endsWith("\n")) {
        sb.append("\n");
      }
    }
    return sb.toString();
  }

  /**
   * Takes a string of text and if there are any http:// or https:// links, then turns them
   * into wiki links.
   *
   * @param content
   * @return
   */
  public static String addWikiLinks(String content) {
    // Convert links to wiki links...
    ArrayList<String> terms = new ArrayList<String>();
    terms.add("http://");
    terms.add("https://");
    // Parse through and add the wiki links...
    int begin = -1;
    for (String term : terms) {
      while ((begin = content.indexOf(term, begin)) > -1) {
        int end = content.indexOf(" ", begin);
        if (end == -1) {
          // content goes to the length of the string
          end = content.length();
        }

        // Determine the URL rendering
        String url = content.substring(begin, end);
        // If the URL is lengthy, shorten it...
        String displayUrl = "";
        if (url.length() > 30) {
          displayUrl = url;
          int slash = displayUrl.indexOf("/", 9);
          if (slash > -1 && slash < 29) {
            displayUrl = " " + displayUrl.substring(0, slash + 2) + "...";
          } else {
            displayUrl = " " + displayUrl.substring(0, 28) + "...";
          }
        }

        // Insert the wiki brackets around the link
        if (begin == 0) {
          content = "[[" + url + displayUrl + "]]" + content.substring(end);
        } else {
          content = content.substring(0, begin) + "[[" + url + displayUrl + "]]" + content.substring(end);
        }
        // Reset the begin position and account for the new brackets
        begin = (end + 4);
      }
    }
    return content;
  }
}
