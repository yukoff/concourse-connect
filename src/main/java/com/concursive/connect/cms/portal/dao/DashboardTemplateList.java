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
package com.concursive.connect.cms.portal.dao;

import com.concursive.commons.xml.XMLUtils;
import com.concursive.connect.cms.portal.utils.DashboardUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Provides a list of portal page templates
 *
 * @author matt rajkowski
 * @version $Id$
 * @created January 20, 2008
 */
public class DashboardTemplateList extends ArrayList<DashboardTemplate> {

  private static Log LOG = LogFactory.getLog(DashboardTemplateList.class);

  public static final String TYPE_PORTAL = "portal";
  public static final String TYPE_NAVIGATION = "navigation";
  public static final String TYPE_PROJECTS = "projects";
  public static final String TYPE_PROJECT_TEMPLATES = "templates";
  public static final String TYPE_SEARCH = "search";
  public static final String TYPE_PAGES = "pages";

  private String objectType = null;

  public String getObjectType() {
    return objectType;
  }

  public void setObjectType(String objectType) {
    this.objectType = objectType;
  }

  public DashboardTemplateList() {
  }

  public DashboardTemplateList(String objectType) throws Exception {
    this(objectType, "dashboards_en_US.xml");
  }

  public DashboardTemplateList(String objectType, String fileName) throws Exception {
    this.objectType = objectType;
    // Load the templates from XML
    String file = (fileName == null ? "dashboards_en_US.xml" : fileName);
    URL resource = DashboardUtils.class.getResource("/portal/" + file);
    LOG.debug("dashboards config file: " + resource.toString());
    XMLUtils library = new XMLUtils(resource);
    parseLibrary(library);
  }

  private void parseLibrary(XMLUtils library) {
    LOG.debug("objectType=" + objectType);
    LOG.debug("has xml? " + (library != null));
    if (LOG.isTraceEnabled()) {
      LOG.trace(library.toString());
    }

    // Use XPath for querying xml elements
    XPath xpath = XPathFactory.newInstance().newXPath();

    // Build a list of dashboard pages
    ArrayList<Element> pageElements = new ArrayList<Element>();
    XMLUtils.getAllChildren(XMLUtils.getFirstChild(library.getDocumentElement(), objectType), "page", pageElements);
    Iterator i = pageElements.iterator();
    int count = 0;
    while (i.hasNext()) {
      ++count;
      Element el = (Element) i.next();
      DashboardTemplate thisTemplate = new DashboardTemplate();
      thisTemplate.setId(count);
      thisTemplate.setName(el.getAttribute("name"));

      // Check for xml included fragments declaration
      // <xml-include fragment="portal-fragments-id"/>
      // NOTE: since the document is being read as a resource, xinclude and xpointer could not be used
      // mainly due to xpointer not being implemented
      try {
        NodeList includeList = (NodeList) xpath.evaluate("row/column/portlet/xml-include", el, XPathConstants.NODESET);
        // XML Include found, so find all the fragments
        for (int nodeIndex = 0; nodeIndex < includeList.getLength(); nodeIndex++) {
          Node xmlInclude = includeList.item(nodeIndex);
          String fragmentId = ((Element) xmlInclude).getAttribute("fragment");

          NodeList fragmentNodeList = (NodeList) xpath.evaluate("*/fragment[@id=\"" + fragmentId + "\"]/*", library.getDocumentElement(), XPathConstants.NODESET);
          if (LOG.isDebugEnabled() && fragmentNodeList.getLength() == 0) {
            LOG.error("Could not find fragment with id: " + fragmentId);
          }
          for (int prefIndex = 0; prefIndex < fragmentNodeList.getLength(); prefIndex++) {
            xmlInclude.getParentNode().appendChild(fragmentNodeList.item(prefIndex).cloneNode(true));
          }
          // Remove the XML Include declaration
          xmlInclude.getParentNode().removeChild(xmlInclude);

        }
      } catch (Exception e) {
        LOG.error("Replace xml fragments", e);
      }

      // Set the completed xml layout
      thisTemplate.setXmlDesign(XMLUtils.toString(el));

      // Check for properties that affect the rendering of the portal page
      if (el.hasAttribute("permission")) {
        thisTemplate.setPermission(el.getAttribute("permission"));
      }
      if (el.hasAttribute("title")) {
        thisTemplate.setTitle(el.getAttribute("title"));
      }
      if (el.hasAttribute("description")) {
        thisTemplate.setDescription(el.getAttribute("description"));
      }
      if (el.hasAttribute("keywords")) {
        thisTemplate.setKeywords(el.getAttribute("keywords"));
      }
      if (el.hasAttribute("category")) {
        thisTemplate.setCategory(el.getAttribute("category"));
      }
      this.add(thisTemplate);
    }
  }

  public DashboardTemplate getTemplateById(int templateId) {
    for (Object o : this) {
      DashboardTemplate thisTemplate = (DashboardTemplate) o;
      if (thisTemplate.getId() == templateId) {
        return thisTemplate;
      }
    }
    return null;
  }

  public DashboardTemplate getTemplateByName(String templateName) {
    for (Object o : this) {
      DashboardTemplate thisTemplate = (DashboardTemplate) o;
      if (thisTemplate.getName().equals(templateName)) {
        thisTemplate.setObjectType(objectType);
        return thisTemplate;
      }
    }
    return null;
  }
}