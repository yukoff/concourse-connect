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

import com.concursive.commons.text.StringUtils;
import com.concursive.connect.web.modules.documents.dao.ImageInfo;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.URL;
import java.util.HashMap;

/**
 * Processes the wiki link as an image
 *
 * @author matt rajkowski
 * @created April 8, 2008
 */
public class WikiImageLink {

  private static Log LOG = LogFactory.getLog(WikiImageLink.class);

  public static String CRLF = System.getProperty("line.separator");
  private String value = "";
  private boolean needsCRLF = true;

  public WikiImageLink(String link, int projectId, HashMap<String, ImageInfo> imageList, boolean lineTest, boolean editMode, String contextPath) {
    Project project = ProjectUtils.loadProject(projectId);
    StringBuffer sb = new StringBuffer();
    String image = link.substring(6);
    String title = null;
    int frame = -1;
    int thumbnail = -1;
    int left = -1;
    int right = -1;
    int center = -1;
    int none = -1;
    int imageLink = -1;
    int alt = -1;
    if (image.indexOf("|") > 0) {
      // the image is first
      image = image.substring(0, image.indexOf("|"));
      // any directives are next
      frame = link.indexOf("|frame");
      thumbnail = link.indexOf("|thumb");
      left = link.indexOf("|left");
      right = link.indexOf("|right");
      center = link.indexOf("|center");
      none = link.indexOf("|none");
      imageLink = link.indexOf("|link=");
      alt = link.indexOf("|alt=");
      // the optional caption is last
      int last = link.lastIndexOf("|");
      if (last > frame &&
          last > thumbnail &&
          last > left &&
          last > right &&
          last > center &&
          last > none &&
          last > imageLink &&
          last > alt) {
        title = link.substring(last + 1);
      }
    }

    // Determine if local or external image
    String imageUrl = null;
    String panelImageUrl = null;
    if (!image.contains(".do?command=Img") &&
        !image.contains("/wiki-image/") &&
        (image.startsWith("http://") || image.startsWith("https://"))) {
      // external image
      try {
        URL url = new URL(image);
        imageUrl = image;
      } catch (Exception e) {
        LOG.error("Could not create URL based on input", e);
      }
    } else {
      // local image
      image = StringUtils.replace(image, "\\{", "[");
      image = StringUtils.replace(image, "\\}", "]");
      imageUrl = contextPath + "/show/" + project.getUniqueId() + "/wiki-image/" + StringUtils.replace(StringUtils.jsEscape(image) + (thumbnail > -1 ? "?th=true" : ""), "%20", "+");
      panelImageUrl = contextPath + "/show/" + project.getUniqueId() + "/wiki-image/" + StringUtils.replace(StringUtils.jsEscape(image), "%20", "+") + "?panel=true";
    }

    if (imageUrl != null) {

      //A picture, including alternate text:
      // [[Image:Wiki.png|The logo for this Wiki]]

      //You can put the image in a frame with a caption:
      //[[Image:Wiki.png|frame|The logo for this Wiki]]

      // Access some image details
      int width = 0;
      int height = 0;
      int fullWidth = 0;
      ImageInfo imageInfo = imageList.get(image);
      if (imageInfo == null) {
        LOG.warn("Image not found: " + image);
      } else {
        if (thumbnail > -1) {
          // Use the typical thumbnail dimensions
          width = 210;
          height = 150;
          fullWidth = imageInfo.getWidth();
        } else {
          // Determine the width and height for the output from the image
          width = imageInfo.getWidth();
          height = imageInfo.getHeight();
          fullWidth = width;
        }
        // Determine the version so that an image isn't cached with a prior version
        if (imageInfo.getVersion() > 1.0) {
          imageUrl += (!imageUrl.contains("?") ? "?" : "&") + "v=" + imageInfo.getVersion();
        }
      }

      if (!editMode) {
        if (frame > -1 || thumbnail > -1) {
          // Width = the image width + border size * 2 + margin * 2 of inner div
          // Output the frame
          sb.append(
              "<div style=\"background: white; " +
                  (width > 0 ? "max-width: " + width + "px; " : "") +
                  (right > -1 ? "float: right; margin-left: 8px; margin-bottom: 4px; padding: 3px; text-align: center; " : "") +
                  (left > -1 ? "float: left; margin-right: 8px; margin-bottom: 4px; padding: 3px; text-align: center; " : "") +
                  (center > -1 ? "display: block; margin: 0 auto; " : "") +
                  "position:relative; border: 1px solid #999999; margin-bottom: 5px; padding:5px; \">");
        }
      }

      // Alt
      String altText = null;
      if (alt > -1) {
        int startIndex = alt + 4;
        int endIndex = link.indexOf("|", startIndex);
        if (endIndex == -1) {
          endIndex = link.length();
        }
        altText = link.substring(startIndex, endIndex);
      }

      // Looks like the image needs a link (which is always last)
      if (imageLink > -1) {
        // Get the entered link
        int startIndex = imageLink + 6;
        int endIndex = link.length();
        String href = link.substring(startIndex, endIndex);

        // Treat as a wikiLink to validate and to create a proper url
        LOG.debug("Create a wiki link from: " + href);
        WikiLink wikiLink = new WikiLink(project.getId(), (altText != null ? href + " " + altText : href));

        String url = wikiLink.getUrl(contextPath);
        sb.append("<a href=\"");
        sb.append(url);
        sb.append("\"");

        if (!editMode && "app".equals(wikiLink.getArea())) {
          // open apps in a panel
          sb.append(" rel=\"shadowbox\"");
        }

        // If an external link, open in a new window
        if (wikiLink.getStatus().equals(WikiLink.REFERENCE)) {
          if (url.startsWith("http://") || url.startsWith("https://")) {
            sb.append(" target=\"_blank\"");
          }
        }
        // Show alt text
        if (StringUtils.hasText(wikiLink.getName())) {
          sb.append(" alt=\"").append(StringUtils.toHtmlValue(wikiLink.getName())).append("\"");
        }
        sb.append(">");
      }

      // Output the image
      sb.append(
          "<img " +
              (width > 0 ? "width=\"" + width + "\" " : "") +
              (height > 0 ? "height=\"" + height + "\" " : "") +
              (right > -1 && ((frame == -1 && thumbnail == -1) || editMode) ? "style=\"float: right; margin-left: 8px; margin-bottom: 4px;\" " : "") +
              (left > -1 && ((frame == -1 && thumbnail == -1) || editMode) ? "style=\"float: left; margin-right: 8px; margin-bottom: 4px;\" " : "") +
              (center > -1 ? "style=\"display: block; margin: 0 auto;\" " : "") +
              "src=\"" + imageUrl + "\" " +
              (StringUtils.hasText(title) ? "title=\"" + StringUtils.toHtmlValue(title) + "\"" : "") + " " +
              "alt=\"" + StringUtils.toHtmlValue(image) + "\" />");

      // Close the image link
      if (imageLink > -1) {
        sb.append("</a>");
      }

      if (!editMode) {
        if (frame > -1 || thumbnail > -1) {
          sb.append("<div id=\"caption\" style=\"text-align: left;\">");
        }
        if (thumbnail > -1) {
          sb.append(
              "<div style=\"float:right\">" +
                  "<a " +
                  "href=\"" + panelImageUrl + "\" " +
                  "rel=\"shadowbox[wikiImages];width=660;imageWidth=640;imageHeight=480;imageUrl=" + panelImageUrl + "\" " +
                  "title=\"" +(StringUtils.hasText(title) ? StringUtils.toHtmlValue(title) : "&nbsp;") + "\">" +
                  "<img src=\"" + contextPath + "/images/magnify-clip.png\" width=\"15\" height=\"11\" alt=\"Enlarge\" border=\"0\" />" +
                  "</a>" +
                  "</div>");
        }
        if (frame > -1 || thumbnail > -1) {
          if (title != null) {
            sb.append(StringUtils.toHtml(title));
          } else {
            sb.append("&nbsp;");
          }
          sb.append("</div></div>");
        }
        // Close the frame
        if (none > -1) {
          sb.append("<br clear=\"all\">");
        }
      }

      if (lineTest && (right > -1 || left > -1) || none > -1) {
        needsCRLF = false;
      }
      value = sb.toString();
    }
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public boolean getNeedsCRLF() {
    return needsCRLF;
  }

  public void setNeedsCRLF(boolean needsCRLF) {
    this.needsCRLF = needsCRLF;
  }
}