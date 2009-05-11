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
import com.bradmcevoy.http.Request.Method;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Map;

/**
 * Description
 *
 * @author matt rajkowski
 * @created Dec 6, 2008 12:11:55 AM
 */
public class WebdavResource implements PostableResource, GetableResource, PropFindableResource, DeletableResource, MoveableResource, CopyableResource {
  private static Log LOG = LogFactory.getLog(WebdavResource.class);

  String name;
  Date modDate;
  Date createdDate;
  WebdavFolderResource parent;

  private String user;
  private String password;

  public WebdavResource(WebdavFolderResource parent, String name) {
    this.parent = parent;
    this.name = name;
    modDate = new Date();
    createdDate = new Date();
    if (parent != null) {
      parent.children.add(this);
    }
  }

  public void setSecure(String user, String password) {
    this.user = user;
    this.password = password;
  }

  public String getHref() {
    if (parent == null) {
      return "/files/";
    } else {
      String s = parent.getHref();
      if (!s.endsWith("/")) s = s + "/";
      s = s + name;
      if (this instanceof CollectionResource) s = s + "/";
      return s;
    }
  }


  public void sendContent(OutputStream out, Range range, Map<String, String> params) throws IOException {
    PrintWriter printer = new PrintWriter(out, true);
    sendContentStart(printer);
    sendContentMiddle(printer);
    sendContentFinish(printer);
  }

  protected void sendContentMiddle(final PrintWriter printer) {
    printer.print("rename");
    printer.print("<form method='POST' action='" + this.getHref() + "'><input type='text' name='name' value='" + this.getName() + "'/><input type='submit'></form>");
  }

  protected void sendContentFinish(final PrintWriter printer) {
    printer.print("</body></html>");
    printer.flush();
  }

  protected void sendContentStart(final PrintWriter printer) {
    printer.print("<html><body>");
    printer.print("<h1>" + getName() + "</h1>");
    sendContentMenu(printer);
  }

  protected void sendContentMenu(final PrintWriter printer) {
    printer.print("<ul>");
    for (WebdavResource r : parent.children) {
      printer.print("<li><a href='" + r.getHref() + "'>" + r.getName() + "</a>");
    }
    printer.print("</ul>");
  }


  public Long getContentLength() {
    return null;
  }

  public String getContentType(String accept) {
    return Response.ContentType.HTTP.toString();
  }

  public String checkRedirect(Request request) {
    return null;
  }

  public String processForm(Map<String, String> parameters, Map<String, FileItem> files) {
    LOG.debug("processForm: " + parameters.size());
    for (String nm : parameters.keySet()) {
      LOG.debug(" - param: " + nm);
    }
    String name = parameters.get("name");
    if (name != null) {
      this.name = name;
    }
    return null;
  }

  public Long getMaxAgeSeconds() {
    return (long) 10;
  }

  public void moveTo(CollectionResource rDest, String name) {
    LOG.debug("moving..");
    WebdavFolderResource d = (WebdavFolderResource) rDest;
    this.parent.children.remove(this);
    this.parent = d;
    this.parent.children.add(this);
    this.name = name;
  }

  public Date getCreateDate() {
    return createdDate;
  }

  public String getName() {
    return name;
  }

  public Object authenticate(String user, String password) {
    if (this.user == null) return true;
    return (user.equals(this.user)) && (password != null && password.equals(this.password));
  }

  public boolean authorise(Request request, Method method, Auth auth) {
    if (auth == null) {
      return this.user == null;
    } else {
      return (this.user == null || auth.user.equals(this.user));
    }
  }

  public String getRealm() {
    return "mockRealm";
  }

  public Date getModifiedDate() {
    return modDate;
  }

  public void delete() {
    if (this.parent == null)
      throw new RuntimeException("attempt to delete root");
    if (this.parent.children == null)
      throw new NullPointerException("children is null");
    this.parent.children.remove(this);
  }

  public void copyTo(CollectionResource toCollection, String name) {
    WebdavResource rClone;
    rClone = (WebdavResource) this.clone((WebdavFolderResource) toCollection);
    rClone.name = name;
  }

  protected Object clone(WebdavFolderResource newParent) {
    return new WebdavResource(newParent, name);
  }

  public int compareTo(Resource o) {
    if (o instanceof WebdavResource) {
      WebdavResource res = (WebdavResource) o;
      return this.getName().compareTo(res.getName());
    } else {
      return -1;
    }
  }

  public String getUniqueId() {
    return this.hashCode() + "";
  }

}

