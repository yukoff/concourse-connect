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

package com.concursive.connect.web.modules.api.beans;

import com.concursive.commons.api.RecordList;
import com.concursive.commons.objects.ObjectUtils;
import com.concursive.commons.xml.XMLUtils;
import com.concursive.connect.web.modules.api.dao.SyncTable;
import com.concursive.connect.web.modules.api.utils.TransactionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * A Transaction is an array of TransactionItems. When a system requests a
 * transaction to be performed on an object -- for example, inserting records
 * -- a Transaction is built from XML.<p>
 * <p/>
 * After the object is built, the transaction items can be executed.
 *
 * @author matt rajkowski
 * @version $Id$
 * @created April 10, 2002
 */
public class Transaction extends ArrayList<TransactionItem> {

  private static Log LOG = LogFactory.getLog(Transaction.class);

  private int id = -1;
  private StringBuffer errorMessage = new StringBuffer();
  private RecordList recordList = new RecordList();
  private TransactionMeta meta = null;
  private PacketContext packetContext = null;
  private TransactionContext transactionContext = new TransactionContext();


  /**
   * Constructor for the Transaction object
   */
  public Transaction() {
  }


  /**
   * Sets the id attribute of the Transaction object
   *
   * @param tmp The new id value
   */
  public void setId(int tmp) {
    id = tmp;
  }


  /**
   * Sets the id attribute of the Transaction object
   *
   * @param tmp The new id value
   */
  public void setId(String tmp) {
    try {
      id = Integer.parseInt(tmp);
    } catch (Exception e) {
      id = -1;
    }
  }


  /**
   * Sets the packetContext attribute of the Transaction object
   *
   * @param tmp The new packetContext value
   */
  public void setPacketContext(PacketContext tmp) {
    packetContext = tmp;
  }


  /**
   * Gets the id attribute of the Transaction object
   *
   * @return The id value
   */
  public int getId() {
    return id;
  }


  /**
   * Gets the errorMessage attribute of the Transaction object
   *
   * @return The errorMessage value
   */
  public String getErrorMessage() {
    return errorMessage.toString();
  }


  /**
   * Gets the recordList attribute of the Transaction object
   *
   * @return The recordList value
   */
  public RecordList getRecordList() {
    return recordList;
  }


  /**
   * Builds a list of TransactionItems from XML
   *
   * @param transactionElement Description of Parameter
   */
  public void build(Element transactionElement) {
    if (transactionElement.hasAttributes()) {
      // Use client transaction id
      if (transactionElement.hasAttribute("id")) {
        this.setId(transactionElement.getAttribute("id"));
      }
      // Use a different processor than the default
      if (transactionElement.hasAttribute("processor")) {

      }
    }
    // The default processor
    ArrayList<Element> objectElements = new ArrayList<Element>();
    XMLUtils.getAllChildren(transactionElement, objectElements);
    LOG.debug("Transaction items: " + objectElements.size());
    for (Element objectElement : objectElements) {
      TransactionItem thisItem = new TransactionItem(
          objectElement, packetContext, transactionContext);
      if (thisItem.getName().equals("meta")) {
        LOG.trace("Meta data found");
        meta = (TransactionMeta) thisItem.getObject();
      } else {
        LOG.trace("Adding transaction item: " + thisItem.getName());
        this.add(thisItem);
      }
    }
  }


  /**
   * Adds a feature to the Mapping attribute of the Transaction object
   *
   * @param key   The feature to be added to the Mapping attribute
   * @param value The feature to be added to the Mapping attribute
   */
  public void addMapping(String key, SyncTable value) {
    packetContext.getObjectMap().put(key, value);
  }


  /**
   * Adds a feature to the Transaction attribute of the Transaction object
   *
   * @param tmp The feature to be added to the Transaction attribute
   */
  public void addTransaction(TransactionItem tmp) {
    this.add(tmp);
  }


  /**
   * Executes all of the TransactionItems in the array
   *
   * @param db Description of Parameter
   * @return Description of the Returned Value
   * @throws SQLException Description of Exception
   */
  public int execute(Connection db) throws SQLException {
    Exception exception = null;
    try {
      int count = 0;
      db.setAutoCommit(false);
      //Process the transaction items
      for (TransactionItem thisItem : this) {
        thisItem.setMeta(meta);
        thisItem.setRecordList(recordList);
        thisItem.setCount(++count);
        thisItem.execute(db);
        //If the item generated an error, then add it to the list to show the client
        if (thisItem.hasError()) {
          LOG.debug("TransactionItem error: " + thisItem.getErrorMessage());
          appendErrorMessage(thisItem.getErrorMessage());
        }
        //If the item allows its key to be shared with other items, then add it
        //to the transactionContext; relies on the guid setting in mapping
        if (thisItem.getShareKey()) {
          String keyName = (packetContext.getObjectMap().get(
              thisItem.getName())).getKey();
          if (keyName != null) {
            transactionContext.getPropertyMap().put(
                thisItem.getName() + "." + keyName,
                ObjectUtils.getParam(thisItem.getObject(), keyName));
          }
        }
      }
      db.commit();
    } catch (Exception e) {
      exception = e;
      LOG.error(e);
      appendErrorMessage("Transaction failed");
      db.rollback();
    } finally {
      db.setAutoCommit(true);
    }

    if (exception == null && errorMessage.length() == 0) {
      return TransactionUtils.TRANSACTION_OK;
    }

    if (exception != null) {
      appendErrorMessage(exception.getMessage());
    }
    return TransactionUtils.TRANSACTION_ERROR;
  }


  /**
   * Description of the Method
   *
   * @return Description of the Returned Value
   */
  public boolean hasError() {
    return (errorMessage.length() > 0);
  }


  /**
   * Description of the Method
   *
   * @param tmp Description of Parameter
   */
  public void appendErrorMessage(String tmp) {
    if (tmp != null) {
      if (errorMessage.length() > 0) {
        errorMessage.append(System.getProperty("line.separator"));
      }
      errorMessage.append(tmp);
    }
  }
}

