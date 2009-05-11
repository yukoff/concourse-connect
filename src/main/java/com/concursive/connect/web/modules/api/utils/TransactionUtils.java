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
package com.concursive.connect.web.modules.api.utils;

import com.concursive.commons.api.TransactionStatus;
import com.concursive.commons.api.TransactionStatusList;
import com.concursive.commons.xml.XMLUtils;
import com.concursive.connect.web.modules.api.beans.PacketContext;
import com.concursive.connect.web.modules.api.beans.Transaction;
import com.concursive.connect.web.modules.api.dao.SyncTable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;

/**
 * Utilities for transaction API processing
 * User: matt
 * Date: Jan 25, 2008
 * Time: 11:46:15 AM
 */
public class TransactionUtils {

  public static final int TRANSACTION_UNSET = -1;
  public static final int TRANSACTION_OK = 0;
  public static final int TRANSACTION_ERROR = 1;

  public static TransactionStatusList processTransactions(Connection db, XMLUtils xml, PacketContext packetContext) throws SQLException {

    TransactionStatusList statusMessages = new TransactionStatusList();

    // Execute the transaction
    SyncTable metaMapping = new SyncTable();
    metaMapping.setName("meta");
    metaMapping.setMappedClassName(
        "com.concursive.connect.web.modules.api.beans.TransactionMeta");
    packetContext.getObjectMap().put("meta", metaMapping);

    // Process the transactions using the server processer
    LinkedList<Element> transactionList = new LinkedList<Element>();
    XMLUtils.getAllChildren(
        xml.getDocumentElement(), "transaction", transactionList);
    for (Element thisElement : transactionList) {
      // Configure the transaction
      int statusCode;
      Transaction thisTransaction = new Transaction();
      thisTransaction.setPacketContext(packetContext);
      thisTransaction.build(thisElement);
      statusCode = thisTransaction.execute(db);

      // Build a status from the response
      TransactionStatus thisStatus = new TransactionStatus();
      thisStatus.setStatusCode(statusCode);
      thisStatus.setId(thisTransaction.getId());
      thisStatus.setMessage(thisTransaction.getErrorMessage());
      thisStatus.setRecordList(thisTransaction.getRecordList());
      statusMessages.add(thisStatus);
    }
    // Each transaction provides a status that needs to be returned to the client
    if (statusMessages.size() == 0 && transactionList.size() == 0) {
      TransactionStatus thisStatus = new TransactionStatus();
      thisStatus.setStatusCode(TRANSACTION_ERROR);
      thisStatus.setMessage("No transactions found");
      statusMessages.add(thisStatus);
    }
    return statusMessages;
  }

  public static String constructXMLResponse(TransactionStatusList statusMessages, String encoding) {
    try {
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = dbf.newDocumentBuilder();
      Document document = builder.newDocument();
      Element app = document.createElement("concursive");
      document.appendChild(app);
      // Convert the result messages to XML
      int returnedRecordCount = 0;
      if (System.getProperty("DEBUG") != null) {
        System.out.println(
            "TransactionUtils-> Processing StatusMessages for output: " + statusMessages.size());
      }
      //Process the status messages for output
      returnedRecordCount = statusMessages.appendResponse(document, app);
      if (System.getProperty("DEBUG") != null) {
        System.out.println("TransactionUtils-> Total Records: " + returnedRecordCount);
      }
      return XMLUtils.toString(document, encoding);
    } catch (Exception pce) {
      pce.printStackTrace(System.out);
    }
    return null;
  }
}
