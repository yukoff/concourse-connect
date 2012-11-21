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

package com.concursive.connect.web.modules.productcatalog.dao;

import com.concursive.connect.Constants;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * A collection of Attachment records
 *
 * @author matt rajkowski
 * @version $Id$
 * @created Feb 9, 2007
 */
public class AttachmentList extends ArrayList<Attachment> {

  private int productId = -1;
  private String productIdRange = null;
  private int allowBeforeCheckout = Constants.UNDEFINED;
  private int allowAfterCheckout = Constants.UNDEFINED;
  private int sendAsEmail = Constants.UNDEFINED;


  public int getProductId() {
    return productId;
  }

  public void setProductId(int productId) {
    this.productId = productId;
  }


  public String getProductIdRange() {
    return productIdRange;
  }

  public void setProductIdRange(String productIdRange) {
    this.productIdRange = productIdRange;
  }

  public int getAllowBeforeCheckout() {
    return allowBeforeCheckout;
  }

  public void setAllowBeforeCheckout(int allowBeforeCheckout) {
    this.allowBeforeCheckout = allowBeforeCheckout;
  }

  public int getAllowAfterCheckout() {
    return allowAfterCheckout;
  }

  public void setAllowAfterCheckout(int allowAfterCheckout) {
    this.allowAfterCheckout = allowAfterCheckout;
  }

  public int getSendAsEmail() {
    return sendAsEmail;
  }

  public void setSendAsEmail(int sendAsEmail) {
    this.sendAsEmail = sendAsEmail;
  }

  public void buildList(Connection db) throws SQLException {
    PreparedStatement pst = null;
    ResultSet rs = null;
    int items = -1;
    StringBuffer sqlSelect = new StringBuffer();
    StringBuffer sqlFilter = new StringBuffer();
    StringBuffer sqlOrder = new StringBuffer();
    //Need to build a base SQL statement for counting records
    createFilter(sqlFilter, db);
    sqlOrder.append("ORDER BY pf.client_filename ");
    sqlSelect.append("SELECT ");
    sqlSelect.append(
        "cpa.*, pf.client_filename, pf.size, pf.filename, pf.modified " +
            "FROM catalog_product_attachments cpa, project_files pf " +
            "WHERE cpa.file_id = pf.item_id ");
    pst = db.prepareStatement(sqlSelect.toString() + sqlFilter.toString() + sqlOrder.toString());
    items = prepareFilter(pst);
    rs = pst.executeQuery();
    int count = 0;
    while (rs.next()) {
      Attachment attachment = new Attachment(rs);
      this.add(attachment);
    }
    rs.close();
    pst.close();
  }


  /**
   * Description of the Method
   *
   * @param sqlFilter Description of the Parameter
   * @param db        Description of the Parameter
   */
  private void createFilter(StringBuffer sqlFilter, Connection db) {
    if (productId > -1) {
      sqlFilter.append("AND cpa.product_id = ? ");
    }
    if (productIdRange != null) {
      sqlFilter.append("AND cpa.product_id IN (" + productIdRange + ") ");
    }
    if (allowBeforeCheckout != Constants.UNDEFINED) {
      sqlFilter.append("AND cpa.allow_before_checkout = ? ");
    }
    if (allowAfterCheckout != Constants.UNDEFINED) {
      sqlFilter.append("AND cpa.allow_after_checkout = ? ");
    }
    if (sendAsEmail != Constants.UNDEFINED) {
      sqlFilter.append("AND cpa.send_as_email = ? ");
    }
  }


  /**
   * Description of the Method
   *
   * @param pst Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  private int prepareFilter(PreparedStatement pst) throws SQLException {
    int i = 0;
    if (productId > -1) {
      pst.setInt(++i, productId);
    }
    if (allowBeforeCheckout != Constants.UNDEFINED) {
      pst.setBoolean(++i, allowBeforeCheckout == Constants.TRUE);
    }
    if (allowAfterCheckout != Constants.UNDEFINED) {
      pst.setBoolean(++i, allowAfterCheckout == Constants.TRUE);
    }
    if (sendAsEmail != Constants.UNDEFINED) {
      pst.setBoolean(++i, sendAsEmail == Constants.TRUE);
    }
    return i;
  }

  public int getAllowBeforeCheckoutCount() {
    int count = 0;
    for (Attachment thisAttachment : this) {
      if (thisAttachment.getAllowBeforeCheckout()) {
        ++count;
      }
    }
    return count;
  }

  public int getAllowAfterCheckoutCount() {
    int count = 0;
    for (Attachment thisAttachment : this) {
      if (thisAttachment.getAllowAfterCheckout()) {
        ++count;
      }
    }
    return count;
  }

  public int getSendAsEmailCount() {
    int count = 0;
    for (Attachment thisAttachment : this) {
      if (thisAttachment.getSendAsEmail()) {
        ++count;
      }
    }
    return count;
  }

  public ArrayList<Attachment> getAllowBeforeCheckoutList() {
    ArrayList<Attachment> list = new ArrayList<Attachment>();
    for (Attachment thisAttachment : this) {
      if (thisAttachment.getAllowBeforeCheckout()) {
        list.add(thisAttachment);
      }
    }
    return list;
  }

  public ArrayList<Attachment> getAllowAfterCheckoutList() {
    ArrayList<Attachment> list = new ArrayList<Attachment>();
    for (Attachment thisAttachment : this) {
      if (thisAttachment.getAllowAfterCheckout()) {
        list.add(thisAttachment);
      }
    }
    return list;
  }

  public ArrayList<Attachment> getSendAsEmailList() {
    ArrayList<Attachment> list = new ArrayList<Attachment>();
    for (Attachment thisAttachment : this) {
      if (thisAttachment.getSendAsEmail()) {
        list.add(thisAttachment);
      }
    }
    return list;
  }
}
