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

import java.util.HashMap;

/**
 * Processes the wiki link as an image
 *
 * @author matt rajkowski
 * @created April 8, 2008
 */
public class WikiImageLink {

  public static String CRLF = System.getProperty("line.separator");
  private String value = "";
  private boolean needsCRLF = true;

  public WikiImageLink(String link, int projectId, HashMap imageList, boolean lineTest, boolean editMode, String contextPath) {
    Project project = ProjectUtils.loadProject(projectId);
    StringBuffer sb = new StringBuffer();
    String image = link.substring(6);
    String title = null;
    int frame = -1;
    int thumbnail = -1;
    int left = -1;
    int right = -1;
    int none = -1;
    if (image.indexOf("|") > 0) {
      // the image is first
      image = image.substring(0, image.indexOf("|"));
      // any directives are next
      frame = link.indexOf("|frame");
      thumbnail = link.indexOf("|thumb");
      left = link.indexOf("|left");
      right = link.indexOf("|right");
      none = link.indexOf("|none");
      // the optional caption is last
      int last = link.lastIndexOf("|");
      if (last > frame &&
          last > thumbnail &&
          last > left &&
          last > right &&
          last > none) {
        title = link.substring(last + 1);
      }
    }

    //A picture, including alternate text:
    //[[Image:Wiki.png|The logo for this Wiki]]

    //You can put the image in a frame with a caption:
    //[[Image:Wiki.png|frame|The logo for this Wiki]]

    // Access some image details
    int width = 0;
    int height = 0;
    int fullWidth = 0;
    ImageInfo imageInfo = (ImageInfo) imageList.get(image + (thumbnail > -1 ? "-TH" : ""));
    if (imageInfo != null) {
      width = imageInfo.getWidth();
      height = imageInfo.getHeight();

      fullWidth = width;
      if (thumbnail > -1) {
        ImageInfo fullImageInfo = (ImageInfo) imageList.get(image);
        if (fullImageInfo != null) {
          fullWidth = fullImageInfo.getWidth();
        }
      }

    }

    if (!editMode) {
      if (frame > -1 || thumbnail > -1) {
        // Width = the image width + border size * 2 + margin * 2 of inner div
        // Output the frame
        sb.append(
            "<div style=\"" +
                (width > 0 ? "width: " + (width + 12) + "px; " : "") +
                (right > -1 ? "float: right; " : "") +
                (left > -1 ? "float: left; " : "") +
                "position:relative; border: 1px solid #999999; margin-bottom: 5px; \">" +
                "<div style=\"border: 1px solid #999999; margin: 5px;\">");
      }
    }

    // Determine if local or external image
    String imageUrl;
    if (!image.contains(".do?command=Img") &&
        !image.contains("/wiki-image/") &&
        (image.startsWith("http://") || image.startsWith("https://"))) {
      // external image
      imageUrl = image;
    } else {
      // local image
      imageUrl = contextPath + "/show/" + project.getUniqueId() + "/wiki-image/" + StringUtils.replace(StringUtils.jsEscape(image) + (thumbnail > -1 ? "?th=true" : ""), "%20", "+");
    }

    // Output the image
    sb.append(
        "<img " +
            (width > 0 ? "width=\"" + width + "\" " : "") +
            (height > 0 ? "height=\"" + height + "\" " : "") +
            (right > -1 ? "style=\"float: right;\" " : "") +
            (left > -1 ? "style=\"float: left;\" " : "") +
            "src=\"" + imageUrl + "\" " +
            (StringUtils.hasText(title) ? "title=\"" + StringUtils.toHtmlValue(title) + "\"" : "") + " " +
            "alt=\"" + StringUtils.toHtmlValue(image) + "\" />");

    if (!editMode) {
      if (frame > -1 || thumbnail > -1) {
        sb.append("</div>");
        sb.append("<div id=\"caption\" style=\"margin-bottom: 5px; margin-left: 5px; margin-right: 5px; text-align: left;\">");
      }
      if (thumbnail > -1) {
        sb.append("<div style=\"float:right\"><a href=\"" + contextPath + "/show/" + project.getUniqueId() + "/wiki-image/" + StringUtils.replace(StringUtils.jsEscape(image), "%20", "+") + "\" rel=\"shadowbox;width=" + fullWidth + "\"><img src=\"" + contextPath + "/images/magnify-clip.png\" width=\"15\" height=\"11\" alt=\"Enlarge\" border=\"0\" /></a></div>");
      }
      if (frame > -1 || thumbnail > -1) {
        if (title != null) {
          sb.append(StringUtils.toHtml(title));
        }
        sb.append("</div></div>");
      }
      if (none > -1) {
        sb.append("<br clear=\"all\">");
      }
    }

    if (lineTest && (right > -1 || left > -1) || none > -1) {
      needsCRLF = false;
    }
    value = sb.toString();
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