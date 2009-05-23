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

package com.concursive.connect.web.modules.api.actions;

import com.concursive.commons.api.TransactionStatus;
import com.concursive.commons.api.TransactionStatusList;
import com.concursive.commons.db.ConnectionElement;
import com.concursive.commons.db.ConnectionPool;
import com.concursive.commons.http.RequestUtils;
import com.concursive.commons.text.StringUtils;
import com.concursive.commons.web.mvc.actions.ActionContext;
import com.concursive.commons.workflow.ObjectHookManager;
import com.concursive.commons.xml.XMLUtils;
import com.concursive.connect.config.ApplicationPrefs;
import com.concursive.connect.web.controller.actions.GenericAction;
import com.concursive.connect.web.modules.api.beans.AuthenticationItem;
import com.concursive.connect.web.modules.api.beans.PacketContext;
import com.concursive.connect.web.modules.api.dao.SyncClient;
import com.concursive.connect.web.modules.api.dao.SyncTable;
import com.concursive.connect.web.modules.api.dao.SyncTableList;
import com.concursive.connect.web.modules.api.utils.TransactionUtils;
import org.quartz.Scheduler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Set;

/**
 * An HTTP connector for incoming XML packet requests.
 *
 * @author matt rajkowski
 * @version $Id$
 * @created January 29, 2006
 */
public final class Service extends GenericAction {

  /**
   * Action for setting up a web application
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandDefault(ActionContext context) {
    TransactionStatusList statusMessages = null;
    Connection db = null;
    String encoding = "UTF-8";
    PacketContext packetContext = new PacketContext();

    ConnectionPool sqlDriver = (ConnectionPool) context.getServletContext().getAttribute("ConnectionPoolAPI");

    try {
      // Put the request into an XML document for validation;
      // use XMLStreamReader for future processing

      XMLUtils xml = new XMLUtils(RequestUtils.getData(context.getRequest()), true);
      if (System.getProperty("DEBUG") != null) {
        System.out.println("Service-> Parsing data");
      }

      ApplicationPrefs applicationPrefs = (ApplicationPrefs) context.getServletContext().getAttribute("applicationPrefs");

      // Verify the client
      AuthenticationItem auth = new AuthenticationItem();
      XMLUtils.populateObject(auth, xml.getFirstChild("authentication"));
      encoding = auth.getEncoding();

      // Get database connection using the dedicated connection pool
      ApplicationPrefs prefs = getApplicationPrefs(context);
      ConnectionElement ce = new ConnectionElement();
      ce.setDriver(prefs.get("SITE.DRIVER"));
      ce.setUrl(prefs.get("SITE.URL"));
      ce.setUsername(prefs.get("SITE.USER"));
      ce.setPassword(prefs.get("SITE.PASSWORD"));
      db = sqlDriver.getConnection(ce, false);

      // Validate the syncClient or throw an exception
      if (auth.getClientId() > -1) {
        // Client based authentication
        SyncClient syncClient = new SyncClient(db, auth.getClientId(), auth.getCode());
      } else {
        // System based authentication
        String compCode = prefs.get("SYSTEM_API.CODE");
        if (!StringUtils.hasText(compCode) || !auth.getCode().equals(compCode)) {
          throw new Exception("Service-> API authentication invalid");
        }
      }

      // Environment variables for this packet request
      packetContext.setActionContext(context);
      packetContext.setObjectHookManager((ObjectHookManager) context.getServletContext().getAttribute("ObjectHookManager"));
      packetContext.setScheduler((Scheduler) context.getServletContext().getAttribute("Scheduler"));
      packetContext.setObjectMap(getObjectMap(context, db, auth.getSystemId()));
      packetContext.setConnectionPool(sqlDriver);
      packetContext.setConnectionElement(ce);
      packetContext.setApplicationPrefs(applicationPrefs);
      packetContext.setBaseFilePath(getPref(context, "FILELIBRARY") + "1" + fs + "projects" + fs);
      packetContext.setResponse(context.getResponse());

      // Process the transactions and keep a running status list
      statusMessages = TransactionUtils.processTransactions(db, xml, packetContext);

    } catch (Exception e) {
      // The transaction usually catches errors, but not always
      e.printStackTrace();
      TransactionStatus thisStatus = new TransactionStatus();
      thisStatus.setStatusCode(TransactionUtils.TRANSACTION_ERROR);
      thisStatus.setMessage("Error: " + e.getMessage());
      statusMessages = new TransactionStatusList();
      statusMessages.add(thisStatus);
    } finally {
      sqlDriver.free(db);
    }
    if (packetContext.getReturnType() == PacketContext.RETURN_DATARECORDS) {
      return null;
    } else {
      String statusXML = TransactionUtils.constructXMLResponse(statusMessages, encoding);
      context.getRequest().setAttribute("statusXML", statusXML);
      return "ServiceOK";
    }
  }

  private HashMap<String, SyncTable> getObjectMap(ActionContext context, Connection db, int systemId) {
    SyncTableList systemObjectMap = (SyncTableList) context.getServletContext().getAttribute(
        "SyncObjectMap" + systemId);
    if (systemObjectMap == null) {
      synchronized (this) {
        systemObjectMap = (SyncTableList) context.getServletContext().getAttribute(
            "SyncObjectMap" + systemId);
        if (systemObjectMap == null) {
          systemObjectMap = new SyncTableList();
          systemObjectMap.setBuildTextFields(false);
          try {
            systemObjectMap.buildList(db);
          } catch (SQLException e) {
            e.printStackTrace(System.out);
          }
          // Revert to an xml file
          if (systemObjectMap.size() == 0) {
            try {
              systemObjectMap.setSystemId(systemId);
              // Load the core mappings
              systemObjectMap.loadObjectMap(SyncTableList.class.getResourceAsStream("/object_map.xml"));
              // Load plug-in mappings in the services path
              Set<String> serviceFiles = context.getServletContext().getResourcePaths("/WEB-INF/services/");
              if (serviceFiles != null && serviceFiles.size() > 0) {
                for (String thisFile : serviceFiles) {
                  if (thisFile.endsWith(".xml")) {
                    try {
                      LOG.debug("Adding services from... " + thisFile);
                      systemObjectMap.loadObjectMap(context.getServletContext().getResourceAsStream(thisFile));
                    } catch (Exception e) {
                      LOG.error("getObjectMap exception", e);
                    }
                  }
                }
              }
            } catch (Exception e) {
              e.printStackTrace(System.out);
            }
          }
          context.getServletContext().setAttribute(
              "SyncObjectMap" + systemId, systemObjectMap);
        }
      }
    }
    return systemObjectMap.getObjectMapping(systemId);
  }
}
