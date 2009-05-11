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

package com.concursive.connect.web.webdav.resources;

import com.bradmcevoy.http.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Description
 *
 * @author matt rajkowski
 * @created Dec 6, 2008 12:12:11 AM
 */
public class WebdavFolderResource extends WebdavResource implements PutableResource, MakeCollectionableResource {

  private static final Log LOG = LogFactory.getLog(WebdavResource.class);

  ArrayList<WebdavResource> children = new ArrayList<WebdavResource>();

  public WebdavFolderResource(WebdavFolderResource parent, String name) {
    super(parent, name);
  }

  @Override
  public Long getContentLength() {
    return null;
  }

  public String getContentType() {
    return null;
  }

  @Override
  public String checkRedirect(Request request) {
    return null;
  }

  public List<? extends WebdavResource> getChildren() {
    return children;
  }

  @Override
  protected void sendContentMiddle(final PrintWriter printer) {
    super.sendContentMiddle(printer);
    printer.print("file upload field");
    printer.print("<form method='POST' enctype='multipart/form-data' action='" + this.getHref() + "'>");
    printer.print("<input type='file' name='file1' /><input type='submit'>");
    printer.print("</form>");
  }

  @Override
  protected void sendContentMenu(final PrintWriter printer) {
    printer.print("<ul>");
    for (WebdavResource r : children) {
      printer.print("<li><a href='" + r.getHref() + "'>" + r.getName() + "</a>");
    }
    printer.print("</ul>");
  }

  @Override
  public Long getMaxAgeSeconds() {
    return (long) 10;
  }

  @Override
  protected Object clone() throws CloneNotSupportedException {
    WebdavFolderResource r = new WebdavFolderResource(parent, name);
    for (WebdavResource child : children) {
      child.clone(r);
    }
    return r;
  }

  private ByteArrayOutputStream readStream(final InputStream in) throws IOException {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    byte[] buf = new byte[1024];
    int len;
    while ((len = in.read(buf)) > 0) {
      bos.write(buf, 0, len);
    }
    return bos;
  }

  public CollectionResource createCollection(String newName) {
    WebdavFolderResource r = new WebdavFolderResource(this, newName);
    return r;
  }

  @Override
  public String processForm(Map<String, String> params, Map<String, com.bradmcevoy.http.FileItem> files) {
    super.processForm(params, files);
    Object file = params.get("file1");
    if (file != null) {
      FileItem fitem = (FileItem) file;
      LOG.debug("found file: " + fitem.getName());
      ByteArrayOutputStream bos;
      try {
        bos = readStream(fitem.getInputStream());
      } catch (IOException ex) {
        LOG.error("error reading stream: ", ex);
        return null;
      }
      new WebdavBinaryResource(this, fitem.getName(), bos.toByteArray());
    }
    return null;
  }

  public Resource createNew(String newName, InputStream inputStream, Long length, String contentType) throws IOException {
    ByteArrayOutputStream bos = readStream(inputStream);
    WebdavResource r = new WebdavBinaryResource(this, newName, bos.toByteArray());
    return r;
  }

  public Resource child(String childName) {
    for (Resource r : getChildren()) {
      if (r.getName().equals(childName)) return r;
    }
    return null;
  }

}
