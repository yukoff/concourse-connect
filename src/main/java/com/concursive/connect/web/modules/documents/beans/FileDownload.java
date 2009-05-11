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

package com.concursive.connect.web.modules.documents.beans;

import com.concursive.commons.text.StringUtils;
import com.concursive.commons.web.mvc.actions.ActionContext;
import com.concursive.connect.web.modules.documents.dao.Thumbnail;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.portlet.RenderResponse;
import javax.servlet.ServletOutputStream;
import java.io.*;

/**
 * Description of the Class
 *
 * @author matt rajkowski
 * @version $Id$
 * @created December 17, 2001
 */
public class FileDownload {

  private static Log LOG = LogFactory.getLog(FileDownload.class);

  private String fullPath = null;
  private String displayName = null;
  private long fileTimestamp = 0;


  /**
   * The complete path and filename of the file to be sent.
   *
   * @param tmp The new FullPath value
   */
  public void setFullPath(String tmp) {
    this.fullPath = tmp;
  }


  /**
   * The filename that should be shown to the user's browser.
   *
   * @param tmp The new DisplayName value
   */
  public void setDisplayName(String tmp) {
    this.displayName = tmp;
  }


  /**
   * The complete path and filename of the file to be sent.
   *
   * @return The FullPath value
   */
  public String getFullPath() {
    return fullPath;
  }


  /**
   * The filename that should be shown to the user's browser.
   *
   * @return The DisplayName value
   */
  public String getDisplayName() {
    return displayName;
  }

  public long getFileTimestamp() {
    return fileTimestamp;
  }

  public void setFileTimestamp(long fileTimestamp) {
    this.fileTimestamp = fileTimestamp;
  }

  /**
   * Description of the Method
   */
  public FileDownload() {
  }


  /**
   * Returns whether the file exists.
   *
   * @return Description of the Returned Value
   */
  public boolean fileExists() {
    if (fullPath == null) {
      return false;
    }
    File downloadFile = new File(fullPath);
    return downloadFile.exists();
  }


  public static String getContentType(String filename) {
    String contentType = "application/octet-stream";
    if (filename.endsWith(".bmp")) {
      contentType = "image/bmp";
    } else if (filename.endsWith(".css")) {
      contentType = "text/plain";
    } else if (filename.endsWith(".csv")) {
      contentType = "text/csv";
    } else if (filename.endsWith(".doc")) {
      contentType = "application/msword";
    } else if (filename.endsWith(".dot")) {
      contentType = "application/msword";
    } else if (filename.endsWith(".eps")) {
      contentType = "application/postscript";
    } else if (filename.endsWith(".gif")) {
      contentType = "image/gif";
    } else if (filename.endsWith(".htm")) {
      contentType = "text/html";
    } else if (filename.endsWith(".html")) {
      contentType = "text/html";
    } else if (filename.endsWith(".java")) {
      contentType = "text/plain";
    } else if (filename.endsWith(".jpeg")) {
      contentType = "image/jpeg";
    } else if (filename.endsWith(".jpg")) {
      contentType = "image/jpeg";
    } else if (filename.endsWith(".js")) {
      contentType = "application/x-javascript";
    } else if (filename.endsWith(".mdb")) {
      contentType = "application/x-msaccess";
    } else if (filename.endsWith(".mid")) {
      contentType = "audio/mid";
    } else if (filename.endsWith(".midi")) {
      contentType = "audio/mid";
    } else if (filename.endsWith(".mp3")) {
      contentType = "audio/mpeg";
    } else if (filename.endsWith(".mpp")) {
      contentType = "application/vnd.ms-project";
    } else if (filename.endsWith(".pdf")) {
      contentType = "application/pdf";
    } else if (filename.endsWith(".png")) {
      contentType = "image/png";
    } else if (filename.endsWith(".pot")) {
      contentType = "application/vnd.ms-powerpoint";
    } else if (filename.endsWith(".pps")) {
      contentType = "application/vnd.ms-powerpoint";
    } else if (filename.endsWith(".ppt")) {
      contentType = "application/vnd.ms-powerpoint";
    } else if (filename.endsWith(".ps")) {
      contentType = "application/postscript";
    } else if (filename.endsWith(".rtf")) {
      contentType = "application/rtf";
    } else if (filename.endsWith(".sql")) {
      contentType = "text/plain";
    } else if (filename.endsWith(".swf")) {
      contentType = "application/x-shockwave-flash";
    } else if (filename.endsWith(".tgz")) {
      contentType = "application/x-compressed";
    } else if (filename.endsWith(".tif")) {
      contentType = "image/tiff";
    } else if (filename.endsWith(".tiff")) {
      contentType = "image/tiff";
    } else if (filename.endsWith(".txt")) {
      contentType = "text/plain";
    } else if (filename.endsWith(".wav")) {
      contentType = "audio/x-wav";
    } else if (filename.endsWith(".wks")) {
      contentType = "application/vnd.ms-works";
    } else if (filename.endsWith(".wps")) {
      contentType = "application/vnd.ms-works";
    } else if (filename.endsWith(".xls")) {
      contentType = "application/vnd.ms-excel";
    } else if (filename.endsWith(".xml")) {
      contentType = "text/xml";
    } else if (filename.endsWith(".xsl")) {
      contentType = "text/xml";
    } else if (filename.endsWith(".zip")) {
      contentType = "application/x-zip-compressed";
    } else if (filename.endsWith("README")) {
      contentType = "text/plain";
    }
    LOG.debug("File type: " + contentType);
    return contentType;
  }


  public void setStreamingResponse(RenderResponse response) throws Exception {
    if (fullPath.endsWith("TH")) {
      // NOTE: A temporary fix because all thumbnails (that are scaled)
      // are saved as JPG.  Actual size thumbnails match the original
      // filetype (PNG, GIF, JPG) but there is no way to tell
      response.setContentType(getContentType(".jpg"));
    } else {
      response.setContentType(getContentType(this.getDisplayName().toLowerCase()));
    }
  }


  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @throws Exception Description of the Exception
   */
  public void streamContent(ActionContext context) throws Exception {
    if (fullPath.endsWith("TH")) {
      // NOTE: A temporary fix because all thumbnails (that are scaled)
      // are saved as JPG.  Actual size thumbnails match the original
      // filetype (PNG, GIF, JPG) but there is no way to tell
      context.getResponse().setContentType(getContentType(".jpg"));
    } else {
      context.getResponse().setContentType(getContentType(this.getDisplayName().toLowerCase()));
    }
    if (fileTimestamp > 0) {
      context.getResponse().setDateHeader("Last-Modified", fileTimestamp);
      //context.getResponse().setDateHeader("Expires", 0);
      if (fullPath != null) {
        File downloadFile = new File(fullPath);
        context.getResponse().setContentLength((int) downloadFile.length());
      }
    }
    this.send(context);
  }


  public void streamThumbnail(ActionContext context, Thumbnail thumbnail) throws Exception {
    context.getResponse().setContentType(getContentType("." + StringUtils.getText(thumbnail.getFormat(), "jpg")));
    context.getResponse().setContentLength(thumbnail.getSize());
    if (fileTimestamp > 0) {
      context.getResponse().setDateHeader("Last-Modified", fileTimestamp);
      //context.getResponse().setDateHeader("Expires", 0);
    }
    this.send(context);
  }


  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @throws Exception Description of the Exception
   */
  public void sendFile(ActionContext context) throws Exception {
    sendFile(context, "application/octet-stream");
  }


  /**
   * Description of the Method
   *
   * @param context     Description of the Parameter
   * @param contentType Description of the Parameter
   * @throws Exception Description of the Exception
   */
  public void sendFile(ActionContext context, String contentType) throws Exception {
    File downloadFile = new File(fullPath);
    context.getResponse().setContentType(contentType);
    if (contentType.startsWith("application")) {
      context.getResponse().setHeader("Content-Disposition", "attachment; filename=\"" + displayName + "\";");
      context.getResponse().setContentLength((int) downloadFile.length());
    }
    this.send(context);
  }

  public void setDownloadResponse(RenderResponse response) {
    response.setContentType("application/octet-stream");
  }


  /**
   * Description of the Method
   *
   * @param context     Description of the Parameter
   * @param bytes       Description of the Parameter
   * @param contentType Description of the Parameter
   * @throws Exception Description of the Exception
   */
  public void sendFile(ActionContext context, byte[] bytes, String contentType) throws Exception {
    context.getResponse().setContentType(contentType);
    if (contentType.startsWith("application")) {
      context.getResponse().setHeader("Content-Disposition", "attachment; filename=\"" + displayName + "\";");
      context.getResponse().setContentLength(bytes.length);
    }
    ServletOutputStream outputStream = context.getResponse().getOutputStream();
    outputStream.write(bytes, 0, bytes.length);
    outputStream.flush();
    outputStream.close();
  }


  public static void sendFile(ActionContext context, InputStream is, String contentType, String displayName) throws Exception {
    context.getResponse().setContentType(contentType);
    if (contentType.startsWith("application")) {
      context.getResponse().setHeader("Content-Disposition", "attachment; filename=\"" + displayName + "\";");
    }
    ServletOutputStream outputStream = context.getResponse().getOutputStream();
    send(outputStream, is);
  }


  /**
   * Description of the Method
   *
   * @param context     Description of the Parameter
   * @param bytes       Description of the Parameter
   * @param contentType Description of the Parameter
   * @throws Exception Description of the Exception
   */
  public static void streamFile(ActionContext context, byte[] bytes, String contentType) throws Exception {
    context.getResponse().setContentType(contentType);
    ServletOutputStream outputStream = context.getResponse().getOutputStream();
    outputStream.write(bytes, 0, bytes.length);
    outputStream.flush();
    outputStream.close();
  }


  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @throws Exception Description of the Exception
   */
  private void send(ActionContext context) throws Exception {
    BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(fullPath));
    ServletOutputStream outputStream = context.getResponse().getOutputStream();
    send(outputStream, inputStream);
  }

  public void send(OutputStream outputStream) throws Exception {
    BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(fullPath));
    send(outputStream, inputStream);
  }

  public static void send(OutputStream outputStream, InputStream is) throws Exception {
    BufferedInputStream inputStream =
        new BufferedInputStream(is);
    byte[] buf = new byte[4 * 1024];
    // 4K buffer
    int len;
    while ((len = inputStream.read(buf, 0, buf.length)) != -1) {
      outputStream.write(buf, 0, len);
    }
    outputStream.flush();
    outputStream.close();
    inputStream.close();
  }


  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @param text    Description of the Parameter
   * @throws Exception Description of the Exception
   */
  public void sendTextAsFile(ActionContext context, String text) throws Exception {
    context.getResponse().setContentType("application/octet-stream");
    context.getResponse().setHeader("Content-Disposition", "attachment;filename=" + displayName + ";");
    context.getResponse().setContentLength((int) text.length());

    ServletOutputStream outputStream = context.getResponse().getOutputStream();
    StringReader strReader = new StringReader(text);
    int data;
    while ((data = strReader.read()) != -1) {
      outputStream.write(data);
    }
    strReader.close();
    outputStream.close();
  }

}

