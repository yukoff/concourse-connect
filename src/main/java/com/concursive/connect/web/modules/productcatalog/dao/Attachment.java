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

import com.concursive.commons.db.DatabaseUtils;
import com.concursive.commons.web.mvc.beans.GenericBean;

import java.sql.*;

/**
 * A document attachment to a product catalog item so that users can download
 * product information before and/or after ordering; attachments can also be
 * emailed after a successful checkout
 *
 * @author matt rajkowski
 * @version $Id$
 * @created Feb 9, 2007
 */
public class Attachment extends GenericBean {

  // Attachment properties
  private int id = -1;
  private int productId = -1;
  private int fileId = -1;
  private boolean allowBeforeCheckout = false;
  private boolean allowAfterCheckout = false;
  private boolean sendAsEmail = false;
  private int daysAllowed = 0;
  private int hoursAllowed = 1;
  // File properties
  private String attachmentClientFilename = null;
  private int attachmentSize = -1;
  private String attachmentFilename = null;
  private java.sql.Timestamp attachmentModified = null;

  public Attachment() {
  }

  public Attachment(ResultSet rs) throws SQLException {
    buildRecord(rs);
  }


  public Attachment(Connection db, int attachmentId) throws SQLException {
    queryRecord(db, attachmentId);
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public void setId(String id) {
    this.id = Integer.parseInt(id);
  }

  public int getProductId() {
    return productId;
  }

  public void setProductId(int productId) {
    this.productId = productId;
  }

  public void setProductId(String id) {
    this.productId = Integer.parseInt(id);
  }

  public int getFileId() {
    return fileId;
  }

  public void setFileId(int fileId) {
    this.fileId = fileId;
  }

  public void setFileId(String id) {
    this.fileId = Integer.parseInt(id);
  }

  public boolean getAllowBeforeCheckout() {
    return allowBeforeCheckout;
  }

  public void setAllowBeforeCheckout(boolean allowBeforeCheckout) {
    this.allowBeforeCheckout = allowBeforeCheckout;
  }

  public void setAllowBeforeCheckout(String allowBeforeCheckout) {
    this.allowBeforeCheckout = DatabaseUtils.parseBoolean(allowBeforeCheckout);
  }

  public boolean getAllowAfterCheckout() {
    return allowAfterCheckout;
  }

  public void setAllowAfterCheckout(boolean allowAfterCheckout) {
    this.allowAfterCheckout = allowAfterCheckout;
  }

  public void setAllowAfterCheckout(String allowAfterCheckout) {
    this.allowAfterCheckout = DatabaseUtils.parseBoolean(allowAfterCheckout);
  }

  public boolean getSendAsEmail() {
    return sendAsEmail;
  }

  public void setSendAsEmail(boolean sendAsEmail) {
    this.sendAsEmail = sendAsEmail;
  }

  public void setSendAsEmail(String sendAsEmail) {
    this.sendAsEmail = DatabaseUtils.parseBoolean(sendAsEmail);
  }

  public int getDaysAllowed() {
    return daysAllowed;
  }

  public void setDaysAllowed(int daysAllowed) {
    this.daysAllowed = daysAllowed;
  }

  public void setDaysAllowed(String daysAllowed) {
    this.daysAllowed = Integer.parseInt(daysAllowed);
  }

  public int getHoursAllowed() {
    return hoursAllowed;
  }

  public void setHoursAllowed(int hoursAllowed) {
    this.hoursAllowed = hoursAllowed;
  }

  public void setHoursAllowed(String hoursAllowed) {
    this.hoursAllowed = Integer.parseInt(hoursAllowed);
  }

  public String getAttachmentClientFilename() {
    return attachmentClientFilename;
  }

  public void setAttachmentClientFilename(String attachmentClientFilename) {
    this.attachmentClientFilename = attachmentClientFilename;
  }

  public int getAttachmentSize() {
    return attachmentSize;
  }

  public int getRelativeAttachmentSize() {
    int newSize = (attachmentSize / 1000);
    if (newSize == 0) {
      return 1;
    } else {
      return newSize;
    }
  }

  public void setAttachmentSize(int attachmentSize) {
    this.attachmentSize = attachmentSize;
  }


  public String getAttachmentFilename() {
    return attachmentFilename;
  }

  public void setAttachmentFilename(String attachmentFilename) {
    this.attachmentFilename = attachmentFilename;
  }

  public Timestamp getAttachmentModified() {
    return attachmentModified;
  }

  public void setAttachmentModified(Timestamp attachmentModified) {
    this.attachmentModified = attachmentModified;
  }

  public void queryRecord(Connection db, int attachmentId) throws SQLException {
    if (attachmentId == -1) {
      throw new SQLException("ID not specified");
    }
    PreparedStatement pst = db.prepareStatement(
        "SELECT cpa.*, pf.client_filename, pf.size, pf.filename, pf.modified " +
            "FROM catalog_product_attachments cpa, project_files pf " +
            "WHERE cpa.file_id = pf.item_id " +
            "AND attachment_id = ? ");
    pst.setInt(1, attachmentId);
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      buildRecord(rs);
    }
    rs.close();
    pst.close();
    if (id == -1) {
      throw new SQLException("ID not found");
    }
  }

  private void buildRecord(ResultSet rs) throws SQLException {
    // catalog_product_attachments table
    id = rs.getInt("attachment_id");
    productId = rs.getInt("product_id");
    fileId = rs.getInt("file_id");
    allowBeforeCheckout = rs.getBoolean("allow_before_checkout");
    allowAfterCheckout = rs.getBoolean("allow_after_checkout");
    sendAsEmail = rs.getBoolean("send_as_email");
    daysAllowed = rs.getInt("days_allowed");
    hoursAllowed = rs.getInt("hours_allowed");
    // project_files table
    attachmentClientFilename = rs.getString("client_filename");
    attachmentSize = rs.getInt("size");
    attachmentFilename = rs.getString("filename");
    attachmentModified = rs.getTimestamp("modified");
  }

  public boolean isExpired(long baseTime) {
    if ((baseTime + (daysAllowed * 86400000) + (hoursAllowed * 3600000)) <= System.currentTimeMillis()) {
      return true;
    }
    return false;
  }
}
