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

package com.concursive.connect.web.modules.documents.dao;

import com.concursive.commons.db.DatabaseUtils;
import com.concursive.commons.images.ImageBean;
import com.concursive.commons.web.mvc.beans.GenericBean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Description of the Class
 *
 * @author matt rajkowski
 * @version $Id$
 * @created September 7, 2003
 */
public class Thumbnail extends GenericBean {

  public final static String fs = System.getProperty("file.separator");

  private int id = -1;
  private String filename = null;
  private int size = 0;
  private double version = 0;
  private java.sql.Timestamp entered = null;
  private int enteredBy = -1;
  private java.sql.Timestamp modified = null;
  private int modifiedBy = -1;
  private int imageWidth = 0;
  private int imageHeight = 0;
  private String format = null;
  // helper
  private String clientFilename = null;


  /**
   * Constructor for the Thumbnail object
   */
  public Thumbnail() {
  }

  public Thumbnail(ResultSet rs) throws SQLException {
    buildRecord(rs);
  }

  public Thumbnail(ImageBean bean) {
    filename = bean.getFilename();
    size = bean.getSize();
    imageWidth = bean.getImageWidth();
    imageHeight = bean.getImageHeight();
    format = bean.getFormat();
  }


  /**
   * Sets the id attribute of the Thumbnail object
   *
   * @param tmp The new id value
   */
  public void setId(int tmp) {
    this.id = tmp;
  }


  /**
   * Sets the id attribute of the Thumbnail object
   *
   * @param tmp The new id value
   */
  public void setId(String tmp) {
    this.id = Integer.parseInt(tmp);
  }


  /**
   * Sets the filename attribute of the Thumbnail object
   *
   * @param tmp The new filename value
   */
  public void setFilename(String tmp) {
    this.filename = tmp;
  }


  /**
   * Sets the size attribute of the Thumbnail object
   *
   * @param tmp The new size value
   */
  public void setSize(int tmp) {
    this.size = tmp;
  }


  /**
   * Sets the size attribute of the Thumbnail object
   *
   * @param tmp The new size value
   */
  public void setSize(String tmp) {
    this.size = Integer.parseInt(tmp);
  }


  /**
   * Sets the version attribute of the Thumbnail object
   *
   * @param tmp The new version value
   */
  public void setVersion(double tmp) {
    this.version = tmp;
  }


  /**
   * Sets the entered attribute of the Thumbnail object
   *
   * @param tmp The new entered value
   */
  public void setEntered(java.sql.Timestamp tmp) {
    this.entered = tmp;
  }


  /**
   * Sets the entered attribute of the Thumbnail object
   *
   * @param tmp The new entered value
   */
  public void setEntered(String tmp) {
    this.entered = DatabaseUtils.parseTimestamp(tmp);
  }


  /**
   * Sets the enteredBy attribute of the Thumbnail object
   *
   * @param tmp The new enteredBy value
   */
  public void setEnteredBy(int tmp) {
    this.enteredBy = tmp;
  }


  /**
   * Sets the enteredBy attribute of the Thumbnail object
   *
   * @param tmp The new enteredBy value
   */
  public void setEnteredBy(String tmp) {
    this.enteredBy = Integer.parseInt(tmp);
  }


  /**
   * Sets the modified attribute of the Thumbnail object
   *
   * @param tmp The new modified value
   */
  public void setModified(java.sql.Timestamp tmp) {
    this.modified = tmp;
  }


  /**
   * Sets the modified attribute of the Thumbnail object
   *
   * @param tmp The new modified value
   */
  public void setModified(String tmp) {
    this.modified = DatabaseUtils.parseTimestamp(tmp);
  }


  /**
   * Sets the modifiedBy attribute of the Thumbnail object
   *
   * @param tmp The new modifiedBy value
   */
  public void setModifiedBy(int tmp) {
    this.modifiedBy = tmp;
  }


  /**
   * Sets the modifiedBy attribute of the Thumbnail object
   *
   * @param tmp The new modifiedBy value
   */
  public void setModifiedBy(String tmp) {
    this.modifiedBy = Integer.parseInt(tmp);
  }


  /**
   * Gets the id attribute of the Thumbnail object
   *
   * @return The id value
   */
  public int getId() {
    return id;
  }


  /**
   * Gets the filename attribute of the Thumbnail object
   *
   * @return The filename value
   */
  public String getFilename() {
    return filename;
  }


  /**
   * Gets the size attribute of the Thumbnail object
   *
   * @return The size value
   */
  public int getSize() {
    return size;
  }


  /**
   * Gets the version attribute of the Thumbnail object
   *
   * @return The version value
   */
  public double getVersion() {
    return version;
  }


  /**
   * Gets the entered attribute of the Thumbnail object
   *
   * @return The entered value
   */
  public java.sql.Timestamp getEntered() {
    return entered;
  }


  /**
   * Gets the enteredBy attribute of the Thumbnail object
   *
   * @return The enteredBy value
   */
  public int getEnteredBy() {
    return enteredBy;
  }


  /**
   * Gets the modified attribute of the Thumbnail object
   *
   * @return The modified value
   */
  public java.sql.Timestamp getModified() {
    return modified;
  }


  /**
   * Gets the modifiedBy attribute of the Thumbnail object
   *
   * @return The modifiedBy value
   */
  public int getModifiedBy() {
    return modifiedBy;
  }


  /**
   * Gets the relativeSize attribute of the Thumbnail object
   *
   * @return The relativeSize value
   */
  public int getRelativeSize() {
    int newSize = (size / 1000);
    if (newSize == 0) {
      return 1;
    } else {
      return newSize;
    }
  }

  public int getImageWidth() {
    return imageWidth;
  }

  public void setImageWidth(int imageWidth) {
    this.imageWidth = imageWidth;
  }

  public int getImageHeight() {
    return imageHeight;
  }

  public void setImageHeight(int imageHeight) {
    this.imageHeight = imageHeight;
  }

  public void setImageSize(int[] imageSize) {
    if (imageSize.length == 2) {
      imageWidth = imageSize[0];
      imageHeight = imageSize[1];
    }
  }

  public String getFormat() {
    return format;
  }

  public void setFormat(String format) {
    this.format = format;
  }

  public String getClientFilename() {
    return clientFilename;
  }

  public void setClientFilename(String cientFilename) {
    this.clientFilename = cientFilename;
  }

  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  public boolean insert(Connection db) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "INSERT INTO project_files_thumbnail " +
            "(item_id, filename, version, size, " +
            (entered != null ? "entered, " : "") +
            (modified != null ? "modified, " : "") +
            "enteredBy, modifiedBy, image_width, image_height, format) " +
            "VALUES (?, ?, ?, ?, " +
            (entered != null ? "?, " : "") +
            (modified != null ? "?, " : "") +
            "?, ?, ?, ?, ?) ");
    int i = 0;
    pst.setInt(++i, id);
    pst.setString(++i, filename);
    pst.setDouble(++i, version);
    pst.setInt(++i, size);
    if (entered != null) {
      pst.setTimestamp(++i, entered);
    }
    if (modified != null) {
      pst.setTimestamp(++i, modified);
    }
    pst.setInt(++i, enteredBy);
    pst.setInt(++i, modifiedBy);
    pst.setInt(++i, imageWidth);
    pst.setInt(++i, imageHeight);
    pst.setString(++i, format);
    pst.execute();
    pst.close();
    return true;
  }


  /**
   * Description of the Method
   *
   * @param db           Description of the Parameter
   * @param baseFilePath Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  public boolean delete(Connection db, String baseFilePath) throws SQLException {
    //Need to delete the actual file
    String filePath = baseFilePath + FileItem.getDatePath(this.getEntered()) + this.getFilename();
    java.io.File fileToDelete = new java.io.File(filePath);
    if (!fileToDelete.delete()) {
      System.err.println("Thumbnail-> Tried to delete file: " + filePath);
    }
    //Delete database record
    String sql =
        "DELETE FROM project_files_thumbnail " +
            "WHERE item_id = ? " +
            "AND version = ? ";
    PreparedStatement pst = db.prepareStatement(sql);
    pst.setInt(1, this.getId());
    pst.setDouble(2, this.getVersion());
    pst.execute();
    pst.close();
    return true;
  }

  private void buildRecord(ResultSet rs) throws SQLException {
    id = rs.getInt("item_id");
    filename = rs.getString("filename");
    size = rs.getInt("size");
    version = rs.getDouble("version");
    entered = rs.getTimestamp("entered");
    enteredBy = rs.getInt("enteredby");
    modified = rs.getTimestamp("modified");
    modifiedBy = rs.getInt("modifiedby");
    imageWidth = rs.getInt("image_width");
    imageHeight = rs.getInt("image_height");
    format = rs.getString("format");
  }

}

