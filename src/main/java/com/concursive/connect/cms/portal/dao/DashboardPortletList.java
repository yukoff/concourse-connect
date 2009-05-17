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
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import org.w3c.dom.Element;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Represents the portlets used on a dashboard page
 *
 * @author matt rajkowski
 * @version $Id$
 * @created January 20, 2008
 */
public class DashboardPortletList extends ArrayList<DashboardPortlet> {

  private int pageId = -1;

  public DashboardPortletList() {
  }

  public int getPageId() {
    return pageId;
  }

  public void setPageId(int pageId) {
    this.pageId = pageId;
  }

  public void buildList(Connection db) throws SQLException {
    PreparedStatement pst = null;
    ResultSet rs;
    int items = -1;
    StringBuffer sqlSelect = new StringBuffer();
    StringBuffer sqlFilter = new StringBuffer();
    StringBuffer sqlOrder = new StringBuffer();
    sqlSelect.append(
        "SELECT pdp.page_portlet_id, pdp.page_id, pdp.portlet_id, lpp.portlet_name " +
            "FROM project_dashboard_portlet pdp " +
            "LEFT JOIN lookup_project_portlet lpp ON (pdp.portlet_id = lpp.portlet_id) " +
            "WHERE pdp.page_portlet_id > -1 ");
    createFilter(sqlFilter);
    pst = db.prepareStatement(sqlSelect.toString() + sqlFilter.toString() + sqlOrder.toString());
    items = prepareFilter(pst);
    rs = pst.executeQuery();
    while (rs.next()) {
      DashboardPortlet thisPortlet = new DashboardPortlet(rs);
      this.add(thisPortlet);
    }
    rs.close();
    pst.close();
  }


  protected void createFilter(StringBuffer sqlFilter) {
    if (pageId > -1) {
      sqlFilter.append("AND pdp.page_id = ? ");
    }
  }

  protected int prepareFilter(PreparedStatement pst) throws SQLException {
    int i = 0;
    if (pageId > -1) {
      pst.setInt(++i, pageId);
    }
    return i;
  }

  /**
   * Take page design and build portlet list in memory only, preferences cannot
   * be saved on these portlets
   *
   * @param page
   * @throws Exception
   */
  public void buildTemporaryList(DashboardPage page) throws Exception {
    XMLUtils xml = new XMLUtils(page.getXmlDesign());
    // Counter for number of instances on this page
    int falseIdCount = 0;
    // Pages have rows
    ArrayList rows = new ArrayList();
    XMLUtils.getAllChildren(xml.getDocumentElement(), "row", rows);
    Iterator i = rows.iterator();
    while (i.hasNext()) {
      Element rowEl = (Element) i.next();
      // Rows have columns
      ArrayList columns = new ArrayList();
      XMLUtils.getAllChildren(rowEl, "column", columns);
      Iterator j = columns.iterator();
      while (j.hasNext()) {
        Element columnEl = (Element) j.next();
        // Columns have portlets
        ArrayList portlets = new ArrayList();
        XMLUtils.getAllChildren(columnEl, "portlet", portlets);
        Iterator k = portlets.iterator();
        while (k.hasNext()) {
          Element portletEl = (Element) k.next();
          // Give the portlet an instance reference
          ++falseIdCount;
          // Set the portlet information
          DashboardPortlet portlet = new DashboardPortlet();
          portlet.setPageId(page.getId());
          portlet.setName(portletEl.getAttribute("name"));
          if (portletEl.hasAttribute("viewer")) {
            portlet.setViewer(portletEl.getAttribute("viewer"));
          }
          if (portletEl.hasAttribute("class")) {
            portlet.setHtmlClass(portletEl.getAttribute("class"));
          }
          if (portletEl.hasAttribute("cache")) {
            portlet.setCacheTime(Integer.parseInt(portletEl.getAttribute("cache")));
          }
          if (portletEl.hasAttribute("timeout")) {
            portlet.setTimeout(Integer.parseInt(portletEl.getAttribute("timeout")));
          }
          if (portletEl.hasAttribute("isSensitive")) {
            portlet.setSensitive(portletEl.getAttribute("isSensitive"));
          }
          // This portlet can temporarily be used
          DashboardPortletItem portletItem = new DashboardPortletItem();
          portletItem.setName(portlet.getName());
          portletItem.setEnabled(true);
          // Portlets could have default preferences specified in the layout
          ArrayList<Element> preferences = new ArrayList<Element>();
          XMLUtils.getAllChildren(portletEl, preferences);
          Iterator l = preferences.iterator();
          while (l.hasNext()) {
            Element preferenceEl = (Element) l.next();
            if ("portlet-events".equals(preferenceEl.getNodeName())) {
              // This is the registration of a generateDataEvent
              ArrayList<Element> generateDataEvents = new ArrayList<Element>();
              XMLUtils.getAllChildren(preferenceEl, "generates-data", generateDataEvents);
              for (Element gde : generateDataEvents) {
                portlet.addGenerateDataEvent(XMLUtils.getNodeText(gde));
              }
              // This is the registration of a consumeDataEvent
              ArrayList<Element> consumeDataEvents = new ArrayList<Element>();
              XMLUtils.getAllChildren(preferenceEl, "consumes-data", consumeDataEvents);
              for (Element cde : consumeDataEvents) {
                portlet.addConsumeDataEvent(XMLUtils.getNodeText(cde));
              }
              // This is the registration of generateSessionData
              ArrayList<Element> generateSessionData = new ArrayList<Element>();
              XMLUtils.getAllChildren(preferenceEl, "generates-session-data", generateSessionData);
              for (Element cde : generateSessionData) {
                portlet.addGenerateSessionData(XMLUtils.getNodeText(cde));
              }
              // This is the registration of consumeSessionData
              ArrayList<Element> consumeSessionData = new ArrayList<Element>();
              XMLUtils.getAllChildren(preferenceEl, "consumes-session-data", consumeSessionData);
              for (Element cde : consumeSessionData) {
                portlet.addConsumeSessionDataEvent(XMLUtils.getNodeText(cde));
              }
              // This is the registration of generateRequestData
              ArrayList<Element> generateRequestData = new ArrayList<Element>();
              XMLUtils.getAllChildren(preferenceEl, "generates-request-data", generateRequestData);
              for (Element cde : generateRequestData) {
                portlet.addGenerateRequestData(XMLUtils.getNodeText(cde));
              }
              // This is the registration of consumeRequestData
              ArrayList<Element> consumeRequestData = new ArrayList<Element>();
              XMLUtils.getAllChildren(preferenceEl, "consumes-request-data", consumeRequestData);
              for (Element cde : consumeRequestData) {
                portlet.addConsumeRequestDataEvent(XMLUtils.getNodeText(cde));
              }
            } else {
              // Provide the default preference
              DashboardPortletPrefs prefs = new DashboardPortletPrefs();
              prefs.setName(preferenceEl.getNodeName());
              // Check to see if the prefs are provided as an array
              ArrayList<String> valueList = new ArrayList<String>();
              ArrayList valueElements = new ArrayList();
              XMLUtils.getAllChildren(preferenceEl, "value", valueElements);
              if (valueElements.size() > 0) {
                // There are <value> nodes
                Iterator vi = valueElements.iterator();
                while (vi.hasNext()) {
                  valueList.add(XMLUtils.getNodeText((Element) vi.next()));
                }
                prefs.setValues(valueList.toArray(new String[valueList.size()]));
              } else {
                // There is a single value
                prefs.setValues(new String[]{XMLUtils.getNodeText(preferenceEl)});
              }
              portlet.addDefaultPreference(prefs.getName(), prefs);
            }
          }
          portlet.setId(falseIdCount);
          this.add(portlet);
        }
      }
    }
  }

  /**
   * Take page design and insert portlets into database (if necessary) and ready
   * for use.
   *
   * @param db   database connection
   * @param page dashboard page
   * @throws Exception
   */
  public void insert(Connection db, DashboardPage page) throws Exception {
    // Read in the portlet definitions from XML
    buildTemporaryList(page);
    // Reset the Id and insert the portlet reference information and the portlets
    for (DashboardPortlet portlet : this) {
      // Retrieve and/or save the portlet reference to the library
      int portletId = DashboardPortletItemList.queryPortletIdByName(db, portlet.getName());
      if (portletId == -1) {
        // Insert a new DashboardPortletItem reference if none exists
        DashboardPortletItem portletItem = new DashboardPortletItem();
        portletItem.setName(portlet.getName());
        portletItem.setEnabled(true);
        if (page.getProjectId() > -1) {
          Project project = ProjectUtils.loadProject(page.getProjectId());
          if (project.getPortal()) {
            portletItem.setPortalEnabled(true);
          } else {
            portletItem.setProjectEnabled(true);
          }
        }
        portletItem.insert(db);
        portlet.setPortletId(portletItem.getId());
      } else {
        // Update the existing DashboardPortletItem reference with features
        if (page.getProjectId() > -1) {
          boolean changed = false;
          Project project = ProjectUtils.loadProject(page.getProjectId());
          DashboardPortletItem portletItem = new DashboardPortletItem(db, portletId);
          if (project.getPortal() && !portletItem.getPortalEnabled()) {
            portletItem.setPortalEnabled(true);
            changed = true;
          } else {
            if (!portletItem.getProjectEnabled()) {
              portletItem.setProjectEnabled(true);
              changed = true;
            }
          }
          if (changed) {
            portletItem.update(db);
          }
        }
        portlet.setPortletId(portletId);
      }
      // Insert the portlet into this page
      portlet.insert(db);
      // Now save all the preferences for the portlet
      for (DashboardPortletPrefs prefs : portlet.getDefaultPreferences().values()) {
        prefs.setPortletId(portlet.getId());
        prefs.insert(db);
      }
    }
  }

  public boolean has(String portletName) {
    for (DashboardPortlet portlet : this) {
      if (portletName.equals(portlet.getName())) {
        return true;
      }
    }
    return false;
  }

  public DashboardPortlet get(String portletName) {
    for (DashboardPortlet portlet : this) {
      if (portletName.equals(portlet.getName())) {
        return portlet;
      }
    }
    return null;
  }
}