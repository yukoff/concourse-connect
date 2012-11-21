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
import com.concursive.commons.text.StringUtils;
import com.concursive.commons.web.mvc.beans.GenericBean;
import com.concursive.connect.Constants;
import com.concursive.connect.cache.utils.CacheUtils;
import com.concursive.connect.web.modules.common.social.rating.dao.Rating;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Description of the Class
 *
 * @author matt rajkowski
 * @version $Id$
 * @created February 8, 2002
 */
public class FileItem extends GenericBean {

  private static Log LOG = LogFactory.getLog(FileItem.class);
  public final static String fs = System.getProperty("file.separator");
  public static final String TABLE = "project_files";
  public static final String PRIMARY_KEY = "item_id";
  protected int linkModuleId = -1;
  protected int linkItemId = -1;
  protected int id = -1;
  protected int folderId = -1;
  protected String subject = "";
  protected String clientFilename = "";
  protected String filename = "";
  protected String directory = "";
  protected int size = 0;
  protected double version = 0;
  protected String image = null;
  protected boolean enabled = false;
  protected boolean defaultFile = false;
  protected boolean doVersionInsert = true;
  protected int downloads = 0;
  protected java.sql.Timestamp entered = null;
  protected int enteredBy = -1;
  protected java.sql.Timestamp modified = null;
  protected int modifiedBy = -1;
  protected FileItemVersionList versionList = null;
  protected String thumbnailFilename = null;
  protected int imageWidth = 0;
  protected int imageHeight = 0;
  protected String comment = null;
  protected boolean featuredFile = false;
  protected int ratingCount = 0;
  protected int ratingValue = 0;
  protected double ratingAvg = 0.0;
  protected int inappropriateCount = 0;
  protected String fileAttachment = null;

  /**
   * Constructor for the FileItem object
   */
  public FileItem() {
  }

  /**
   * Constructor for the FileItem object
   *
   * @param db     Description of the Parameter
   * @param itemId Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public FileItem(Connection db, int itemId) throws SQLException {
    queryRecord(db, itemId);
  }

  /**
   * Constructor for the FileItem object
   *
   * @param db           Description of the Parameter
   * @param itemId       Description of the Parameter
   * @param moduleItemId Description of the Parameter
   * @param moduleId     Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public FileItem(Connection db, int itemId, int moduleItemId, int moduleId) throws SQLException {
    this.linkModuleId = moduleId;
    this.linkItemId = moduleItemId;
    queryRecord(db, itemId);
  }

  /**
   * Constructor for the FileItem object when the linkItemId is programmed to
   * have only ONE fileItem
   *
   * @param db           Description of the Parameter
   * @param moduleItemId Description of the Parameter
   * @param moduleId     Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public FileItem(Connection db, int moduleItemId, int moduleId) throws SQLException {
    this.linkModuleId = moduleId;
    this.linkItemId = moduleItemId;
    queryRecord(db, -1);
  }

  /**
   * Description of the Method
   *
   * @param db     Description of the Parameter
   * @param itemId Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  protected void queryRecord(Connection db, int itemId) throws SQLException {
    StringBuffer sql = new StringBuffer();
    sql.append(
        "SELECT f.*, t.filename AS thumbnail " +
            "FROM project_files f " +
            "LEFT JOIN project_files_thumbnail t ON (f.item_id = t.item_id AND f.version = t.version) " +
            "WHERE f.item_id > 0 ");
    if (itemId > -1) {
      sql.append("AND f.item_id = ? ");
    }
    if (linkModuleId > -1) {
      sql.append("AND f.link_module_id = ? ");
    }
    if (linkItemId > -1) {
      sql.append("AND f.link_item_id = ? ");
    }
    PreparedStatement pst = db.prepareStatement(sql.toString());
    int i = 0;
    if (itemId > -1) {
      pst.setInt(++i, itemId);
    }
    if (linkModuleId > -1) {
      pst.setInt(++i, linkModuleId);
    }
    if (linkItemId > -1) {
      pst.setInt(++i, linkItemId);
    }
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      buildRecord(rs, false);
    } else {
      rs.close();
      pst.close();
      throw new SQLException("File record not found.");
    }
    rs.close();
    pst.close();
  }

  /**
   * Constructor for the FileItem object
   *
   * @param rs Description of Parameter
   * @throws SQLException Description of Exception
   */
  public FileItem(ResultSet rs) throws SQLException {
    buildRecord(rs, false);
  }

  /**
   * Constructor for the FileItem object
   *
   * @param rs        Description of Parameter
   * @param isVersion Description of Parameter
   * @throws SQLException Description of Exception
   */
  public FileItem(ResultSet rs, boolean isVersion) throws SQLException {
    buildRecord(rs, isVersion);
  }

  /**
   * Gets the datePath attribute of the FileItem class
   *
   * @param fileDate Description of Parameter
   * @return The datePath value
   */
  public static String getDatePath(java.sql.Timestamp fileDate) {
    SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy");
    String datePathToUse1 = formatter1.format(fileDate);
    SimpleDateFormat formatter2 = new SimpleDateFormat("MMdd");
    String datePathToUse2 = formatter2.format(fileDate);
    return datePathToUse1 + fs + datePathToUse2 + fs;
  }

  /**
   * Gets the datePath attribute of the FileItem class
   *
   * @param filenameDate Description of the Parameter
   * @return The datePath value
   */
  public static String getDatePath(String filenameDate) {
    if (filenameDate.length() > 7) {
      return (filenameDate.substring(0, 4) + fs +
          filenameDate.substring(4, 8) + fs);
    } else {
      return null;
    }
  }

  /**
   * Gets the fullFilePath attribute of the FileItem object
   *
   * @return The fullFilePath value
   */
  public String getFullFilePath() {
    if ("".equals(directory)) {
      return filename;
    } else {
      if (modified != null) {
        return directory + getDatePath(modified) + filename;
      } else {
        return directory + getDatePath(filename) + filename;
      }
    }
  }

  /**
   * Sets the linkModuleId attribute of the FileItem object
   *
   * @param tmp The new linkModuleId value
   */
  public void setLinkModuleId(int tmp) {
    linkModuleId = tmp;
  }

  /**
   * Sets the linkModuleId attribute of the FileItem object
   *
   * @param tmp The new linkModuleId value
   */
  public void setLinkModuleId(String tmp) {
    linkModuleId = Integer.parseInt(tmp);
  }

  /**
   * Sets the linkItemId attribute of the FileItem object
   *
   * @param tmp The new linkItemId value
   */
  public void setLinkItemId(int tmp) {
    linkItemId = tmp;
  }

  /**
   * Sets the linkItemId attribute of the FileItem object
   *
   * @param tmp The new linkItemId value
   */
  public void setLinkItemId(String tmp) {
    linkItemId = Integer.parseInt(tmp);
  }

  /**
   * Sets the id attribute of the FileItem object
   *
   * @param tmp The new id value
   */
  public void setId(int tmp) {
    this.id = tmp;
  }

  /**
   * Sets the id attribute of the FileItem object
   *
   * @param tmp The new id value
   */
  public void setId(String tmp) {
    this.id = Integer.parseInt(tmp);
  }

  /**
   * Sets the folderId attribute of the FileItem object
   *
   * @param tmp The new folderId value
   */
  public void setFolderId(int tmp) {
    this.folderId = tmp;
  }

  /**
   * Sets the folderId attribute of the FileItem object
   *
   * @param tmp The new folderId value
   */
  public void setFolderId(String tmp) {
    this.folderId = Integer.parseInt(tmp);
  }

  /**
   * Sets the subject attribute of the FileItem object
   *
   * @param tmp The new subject value
   */
  public void setSubject(String tmp) {
    this.subject = tmp;
  }

  /**
   * Sets the clientFilename attribute of the FileItem object
   *
   * @param tmp The new clientFilename value
   */
  public void setClientFilename(String tmp) {
    this.clientFilename = tmp;
  }

  /**
   * Sets the filename attribute of the FileItem object
   *
   * @param tmp The new filename value
   */
  public void setFilename(String tmp) {
    this.filename = tmp;
  }

  /**
   * Sets the directory attribute of the FileItem object
   *
   * @param tmp The new directory value
   */
  public void setDirectory(String tmp) {
    this.directory = tmp;
  }

  /**
   * Sets the size attribute of the FileItem object
   *
   * @param tmp The new size value
   */
  public void setSize(int tmp) {
    this.size = tmp;
  }

  /**
   * Sets the size attribute of the FileItem object
   *
   * @param tmp The new size value
   */
  public void setSize(String tmp) {
    this.size = Integer.parseInt(tmp);
  }

  /**
   * Sets the version attribute of the FileItem object
   *
   * @param tmp The new version value
   */
  public void setVersion(double tmp) {
    this.version = tmp;
  }

  /**
   * Sets the version attribute of the FileItem object
   *
   * @param tmp The new version value
   */
  public void setVersion(String tmp) {
    this.version = Double.parseDouble(tmp);
  }

  /**
   * Sets the image attribute of the FileItem object
   *
   * @param tmp The new image value
   */
  public void setImage(String tmp) {
    this.image = tmp;
  }

  /**
   * Sets the entered attribute of the FileItem object
   *
   * @param tmp The new entered value
   */
  public void setEntered(java.sql.Timestamp tmp) {
    this.entered = tmp;
  }

  /**
   * Sets the entered attribute of the FileItem object
   *
   * @param tmp The new entered value
   */
  public void setEntered(String tmp) {
    this.entered = DatabaseUtils.parseTimestamp(tmp);
  }

  /**
   * Sets the enteredBy attribute of the FileItem object
   *
   * @param tmp The new enteredBy value
   */
  public void setEnteredBy(int tmp) {
    this.enteredBy = tmp;
  }

  /**
   * Sets the enteredBy attribute of the FileItem object
   *
   * @param tmp The new enteredBy value
   */
  public void setEnteredBy(String tmp) {
    this.enteredBy = Integer.parseInt(tmp);
  }

  /**
   * Sets the modified attribute of the FileItem object
   *
   * @param tmp The new modified value
   */
  public void setModified(java.sql.Timestamp tmp) {
    this.modified = tmp;
  }

  /**
   * Sets the modified attribute of the FileItem object
   *
   * @param tmp The new modified value
   */
  public void setModified(String tmp) {
    this.modified = DatabaseUtils.parseTimestamp(tmp);
  }

  /**
   * Sets the modifiedBy attribute of the FileItem object
   *
   * @param tmp The new modifiedBy value
   */
  public void setModifiedBy(int tmp) {
    this.modifiedBy = tmp;
  }

  /**
   * Sets the modifiedBy attribute of the FileItem object
   *
   * @param tmp The new modifiedBy value
   */
  public void setModifiedBy(String tmp) {
    this.modifiedBy = Integer.parseInt(tmp);
  }

  /**
   * Sets the enabled attribute of the FileItem object
   *
   * @param tmp The new enabled value
   */
  public void setEnabled(boolean tmp) {
    this.enabled = tmp;
  }

  /**
   * Sets the enabled attribute of the FileItem object
   *
   * @param tmp The new enabled value
   */
  public void setEnabled(String tmp) {
    this.enabled = DatabaseUtils.parseBoolean(tmp);
  }

  /**
   * Sets the defaultFile attribute of the FileItem object
   *
   * @param tmp The new defaultFile value
   */
  public void setDefaultFile(boolean tmp) {
    this.defaultFile = tmp;
  }

  /**
   * Sets the defaultFile attribute of the FileItem object
   *
   * @param tmp The new defaultFile value
   */
  public void setDefaultFile(String tmp) {
    defaultFile = DatabaseUtils.parseBoolean(tmp);
  }

  /**
   * Gets the defaultFile attribute of the FileItem object
   *
   * @return The defaultFile value
   */
  public boolean getDefaultFile() {
    return defaultFile;
  }

  /**
   * Sets the downloads attribute of the FileItem object
   *
   * @param tmp The new downloads value
   */
  public void setDownloads(int tmp) {
    downloads = tmp;
  }

  /**
   * Sets the downloads attribute of the FileItem object
   *
   * @param tmp The new downloads value
   */
  public void setDownloads(String tmp) {
    downloads = Integer.parseInt(tmp);
  }

  /**
   * Sets the doVersionInsert attribute of the FileItem object
   *
   * @param tmp The new doVersionInsert value
   */
  public void setDoVersionInsert(boolean tmp) {
    this.doVersionInsert = tmp;
  }

  /**
   * Sets the doVersionInsert attribute of the FileItem object
   *
   * @param tmp The new doVersionInsert value
   */
  public void setDoVersionInsert(String tmp) {
    doVersionInsert = DatabaseUtils.parseBoolean(tmp);
  }

  /**
   * Sets the thumbnailFilename attribute of the FileItem object
   *
   * @param tmp The new thumbnailFilename value
   */
  public void setThumbnailFilename(String tmp) {
    this.thumbnailFilename = tmp;
  }

  public void setImageWidth(int imageWidth) {
    this.imageWidth = imageWidth;
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

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  /**
   * @return the featuredFile
   */
  public boolean getFeaturedFile() {
    return featuredFile;
  }

  /**
   * @param featuredFile the featuredFile to set
   */
  public void setFeaturedFile(boolean featuredFile) {
    this.featuredFile = featuredFile;
  }

  public void setFeaturedFile(String featuredFile) {
    this.featuredFile = DatabaseUtils.parseBoolean(featuredFile);
  }

  /**
   * @return the ratingCount
   */
  public int getRatingCount() {
    return ratingCount;
  }

  /**
   * @param ratingCount the ratingCount to set
   */
  public void setRatingCount(int ratingCount) {
    this.ratingCount = ratingCount;
  }

  public void setRatingCount(String ratingCount) {
    this.ratingCount = Integer.parseInt(ratingCount);
  }

  /**
   * @return the ratingValue
   */
  public int getRatingValue() {
    return ratingValue;
  }

  /**
   * @param ratingValue the ratingValue to set
   */
  public void setRatingValue(int ratingValue) {
    this.ratingValue = ratingValue;
  }

  public void setRatingValue(String ratingValue) {
    this.ratingValue = Integer.parseInt(ratingValue);
  }

  /**
   * @return the ratingAvg
   */
  public double getRatingAvg() {
    return ratingAvg;
  }

  /**
   * @param ratingAvg the ratingAvg to set
   */
  public void setRatingAvg(double ratingAvg) {
    this.ratingAvg = ratingAvg;
  }

  public void setRatingAvg(String ratingAvg) {
    this.ratingAvg = Double.parseDouble(ratingAvg);
  }

  /**
   * @return the inappropriateCount
   */
  public int getInappropriateCount() {
    return inappropriateCount;
  }

  /**
   * @param inappropriateCount the inappropriateCount to set
   */
  public void setInappropriateCount(int inappropriateCount) {
    this.inappropriateCount = inappropriateCount;
  }

  public void setInappropriateCount(String inappropriateCount) {
    this.inappropriateCount = Integer.parseInt(inappropriateCount);
  }

  public String getFileAttachment() {
    return fileAttachment;
  }

  public void setFileAttachment(String fileAttachment) {
    this.fileAttachment = fileAttachment;
  }

  /**
   * Gets the doVersionInsert attribute of the FileItem object
   *
   * @return The doVersionInsert value
   */
  public boolean getDoVersionInsert() {
    return doVersionInsert;
  }

  /**
   * Gets the linkModuleId attribute of the FileItem object
   *
   * @return The linkModuleId value
   */
  public int getLinkModuleId() {
    return linkModuleId;
  }

  /**
   * Gets the linkItemId attribute of the FileItem object
   *
   * @return The linkItemId value
   */
  public int getLinkItemId() {
    return linkItemId;
  }

  /**
   * Gets the id attribute of the FileItem object
   *
   * @return The id value
   */
  public int getId() {
    return id;
  }

  /**
   * Gets the folderId attribute of the FileItem object
   *
   * @return The folderId value
   */
  public int getFolderId() {
    return folderId;
  }

  /**
   * Gets the subject attribute of the FileItem object
   *
   * @return The subject value
   */
  public String getSubject() {
    return subject;
  }

  /**
   * Gets the clientFilename attribute of the FileItem object
   *
   * @return The clientFilename value
   */
  public String getClientFilename() {
    return clientFilename;
  }

  /**
   * Gets the extension attribute of the FileItem object
   *
   * @return The extension value
   */
  public String getExtension() {
    return getExtension(clientFilename);
  }

  /**
   * Gets the extension attribute of the FileItem class
   *
   * @param clientFilename Description of the Parameter
   * @return The extension value
   */
  public static String getExtension(String clientFilename) {
    if (clientFilename.indexOf(".") > 0) {
      return clientFilename.substring(clientFilename.lastIndexOf(".")).toLowerCase();
    } else {
      return "";
    }
  }

  /**
   * Gets the filename attribute of the FileItem object
   *
   * @return The filename value
   */
  public String getFilename() {
    return filename;
  }

  /**
   * Gets the directory attribute of the FileItem object
   *
   * @return The directory value
   */
  public String getDirectory() {
    return directory;
  }

  /**
   * Gets the size attribute of the FileItem object
   *
   * @return The size value
   */
  public int getSize() {
    return size;
  }

  /**
   * Gets the relativeSize attribute of the FileItem object
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

  /**
   * Gets the version attribute of the FileItem object
   *
   * @return The version value
   */
  public double getVersion() {
    return version;
  }

  /**
   * Description of the Method
   *
   * @param version Description of the Parameter
   * @return Description of the Return Value
   */
  public boolean hasVersion(double version) {
    return (getVersion(version) != null);
  }

  /**
   * Gets the version attribute of the FileItem object
   *
   * @param version Description of Parameter
   * @return The version value
   */
  public FileItemVersion getVersion(double version) {
    if (versionList != null) {
      for (FileItemVersion thisVersion : versionList) {
        if (thisVersion.getVersion() == version) {
          return thisVersion;
        }
      }
    }
    return null;
  }

  /**
   * Gets the versionNextMajor attribute of the FileItem object
   *
   * @return The versionNextMajor value
   */
  public String getVersionNextMajor() {
    return (((int) version + 1) + ".0");
  }

  /**
   * Gets the versionNextMinor attribute of the FileItem object
   *
   * @return The versionNextMinor value
   */
  public String getVersionNextMinor() {
    String value = String.valueOf(version);
    if (value.indexOf(".") > -1) {
      value = value.substring(0, value.indexOf(".") + 2);
    }

    String newVersion = (new java.math.BigDecimal(value).add(new java.math.BigDecimal(".1"))).toString();
    if (Double.parseDouble(newVersion) > (Double.parseDouble(getVersionNextMajor()))) {
      return getVersionNextMajor();
    } else {
      return newVersion;
    }
  }

  /**
   * Gets the versionNextChanges attribute of the FileItem object
   *
   * @return The versionNextChanges value
   */
  public String getVersionNextChanges() {
    return (new java.math.BigDecimal("" + version).add(new java.math.BigDecimal(".01"))).toString();
  }

  /**
   * Gets the image attribute of the FileItem object
   *
   * @return The image value
   */
  public String getImage() {
    return image;
  }

  public String getImageTag(String imageExt, String url) {
    return getImageTag(image, imageExt, getExtension(), url);
  }

  /**
   * Gets the imageTag attribute of the FileItem object
   *
   * @param imageSizeExtension Description of the Parameter
   * @param image              Description of the Parameter
   * @param ext                Description of the Parameter
   * @param imageUrl
   * @return The imageTag value
   */
  public static String getImageTag(String image, String imageSizeExtension, String ext, String imageUrl) {
    if (image == null) {
      if (".bmp".equals(ext)) {
        image = "gnome-image-bmp";
      } else if (".dia".equals(ext)) {
        image = "gnome-application-x-dia-diagram";
      } else if (".doc".equals(ext)) {
        image = "gnome-application-msword";
      } else if (".eps".equals(ext)) {
        image = "gnome-application-encapsulated_postscript";
      } else if (".gif".equals(ext)) {
        image = "gnome-image-gif";
      } else if (".gz".equals(ext)) {
        image = "gnome-compressed";
      } else if (".gzip".equals(ext)) {
        image = "gnome-compressed";
      } else if (".html".equals(ext)) {
        image = "gnome-text-html";
      } else if (".jar".equals(ext)) {
        image = "gnome-application-x-jar";
      } else if (".java".equals(ext)) {
        image = "gnome-application-x-java-source";
      } else if (".jpeg".equals(ext)) {
        image = "gnome-image-jpeg";
      } else if (".jpg".equals(ext)) {
        image = "gnome-image-jpeg";
      } else if (".midi".equals(ext)) {
        image = "gnome-audio-midi";
      } else if (".mp3".equals(ext)) {
        image = "gnome-audio-mpg";
      } else if (".mpeg".equals(ext)) {
        image = "gnome-video-mpeg";
      } else if (".mpg".equals(ext)) {
        image = "gnome-video-mpeg";
      } else if (".pdf".equals(ext)) {
        image = "gnome-application-pdf";
      } else if (".png".equals(ext)) {
        image = "gnome-image-png";
      } else if (".ppt".equals(ext)) {
        image = "gnome-application-vnd.ms-powerpoint";
      } else if (".psd".equals(ext)) {
        image = "gnome-image-psd";
      } else if (".ps".equals(ext)) {
        image = "gnome-application-postscript";
      } else if (".sql".equals(ext)) {
        image = "gnome-text-x-sql";
      } else if (".sdc".equals(ext)) {
        image = "gnome-application-x-generic-spreadsheet";
      } else if (".sdd".equals(ext)) {
        image = "gnome-application-x-staroffice-presentation";
      } else if (".sdw".equals(ext)) {
        image = "gnome-application-x-staroffice-words";
      } else if (".sxc".equals(ext)) {
        image = "gnome-application-x-generic-spreadsheet";
      } else if (".sxd".equals(ext)) {
        image = "gnome-application-x-openoffice-presentation";
      } else if (".sxi".equals(ext)) {
        image = "gnome-application-x-openoffice-words";
      } else if (".sxw".equals(ext)) {
        image = "gnome-application-x-openoffice-words";
      } else if (".tgz".equals(ext)) {
        image = "gnome-compressed";
      } else if (".tif".equals(ext)) {
        image = "gnome-image-tiff";
      } else if (".tiff".equals(ext)) {
        image = "gnome-image-tiff";
      } else if (".wav".equals(ext)) {
        image = "gnome-audio-x-wav";
      } else if (".xls".equals(ext)) {
        image = "gnome-application-vnd.ms-excel";
      } else if (".zip".equals(ext)) {
        image = "gnome-compressed";
      } else {
        image = "gnome-text-plain";
      }
    }
    return "<img border=\"0\" src=\"" + (imageUrl != null ? imageUrl : "") + "/images/mime/" + image + imageSizeExtension + ".png\" align=\"absmiddle\" alt=\"" + "" + "\" />";
  }

  /**
   * Gets the thumbnail attribute of the FileItem object
   *
   * @param contextPath the url of the application
   * @return The thumbnail value
   */
  public String getThumbnail(String contextPath) {
    if (hasThumbnail()) {
      return "<img border=\"0\" src=\"" + contextPath + "/ProjectManagementFiles.do?command=ShowThumbnail&p=" + linkItemId + "&i=" + id + "&v=" + version + "\" align=\"absmiddle\" alt=\"\" />";
    } else {
      return getImageTag(image, "", getExtension(), contextPath);
    }
  }

  public String getFullImageFromAdmin(String contextPath) {
    if (hasThumbnail()) {
      return "<img border=\"0\" src=\"" + contextPath + "/Admin.do?command=Img&fileItemId=" + id + "\" align=\"absmiddle\" alt=\"\" />";
    } else {
      return getImageTag(image, "", getExtension(), contextPath);
    }
  }

  /**
   * Gets the fullImage attribute of the FileItem object
   *
   * @return The fullImage value
   */
  public String getFullImage(String contextPath) {
    if (isImageFormat() && hasValidImageSize()) {
      return "<img border=\"0\" src=\"" + contextPath + "/ProjectManagementFiles.do?command=ShowThumbnail&p=" + linkItemId + "&i=" + id + "&v=" + version + "&s=full" + "\" align=\"absmiddle\" alt=\"\" />";
    } else {
      return getImageTag(image, "", getExtension(), contextPath);
    }
  }

  /**
   * Gets the entered attribute of the FileItem object
   *
   * @return The entered value
   */
  public java.sql.Timestamp getEntered() {
    return entered;
  }

  /**
   * Gets the enteredString attribute of the FileItem object
   *
   * @return The enteredString value
   */
  public String getEnteredString() {
    try {
      return DateFormat.getDateInstance(3).format(entered);
    } catch (NullPointerException e) {
    }
    return "";
  }

  /**
   * Gets the enteredDateTimeString attribute of the FileItem object
   *
   * @return The enteredDateTimeString value
   */
  public String getEnteredDateTimeString() {
    try {
      return DateFormat.getDateTimeInstance(3, 3).format(entered);
    } catch (NullPointerException e) {
    }
    return "";
  }

  /**
   * Gets the enteredBy attribute of the FileItem object
   *
   * @return The enteredBy value
   */
  public int getEnteredBy() {
    return enteredBy;
  }

  /**
   * Gets the modified attribute of the FileItem object
   *
   * @return The modified value
   */
  public java.sql.Timestamp getModified() {
    return modified;
  }

  public Timestamp getModificationDate() {
    if (modified != null) {
      return modified;
    }
    if (entered != null) {
      return entered;
    }
    return new Timestamp((new java.util.Date()).getTime());
  }

  /**
   * Gets the modifiedBy attribute of the FileItem object
   *
   * @return The modifiedBy value
   */
  public int getModifiedBy() {
    return modifiedBy;
  }

  /**
   * Gets the downloads attribute of the FileItem object
   *
   * @return The downloads value
   */
  public int getDownloads() {
    return downloads;
  }

  /**
   * Gets the enabled attribute of the FileItem object
   *
   * @return The enabled value
   */
  public boolean getEnabled() {
    return enabled;
  }

  /**
   * Gets the versionList attribute of the FileItem object
   *
   * @return The versionList value
   */
  public FileItemVersionList getVersionList() {
    return versionList;
  }

  /**
   * Gets the thumbnailFilename attribute of the FileItem object
   *
   * @return The thumbnailFilename value
   */
  public String getThumbnailFilename() {
    return thumbnailFilename;
  }

  public int getImageWidth() {
    return imageWidth;
  }

  public int getImageHeight() {
    return imageHeight;
  }

  public Project getProject() {
    if (linkModuleId == Constants.PROJECTS_FILES) {
      return ProjectUtils.loadProject(linkItemId);
    } else if (linkModuleId == Constants.PROJECT_IMAGE_FILES) {
      return ProjectUtils.loadProject(linkItemId);
    }
    return null;
  }

  /**
   * Description of the Method
   *
   * @param db Description of Parameter
   * @return Description of the Returned Value
   * @throws SQLException Description of Exception
   */
  public boolean insert(Connection db) throws SQLException {
    if (!isValid()) {
      LOG.debug("Object validation failed");
      return false;
    }

    boolean result = false;
    boolean doCommit = false;
    try {
      if (doCommit = db.getAutoCommit()) {
        db.setAutoCommit(false);
      }
      StringBuffer sql = new StringBuffer();
      sql.append(
          "INSERT INTO project_files " +
              "(folder_id, subject, client_filename, filename, version, size, ");
      sql.append("enabled, downloads, ");
      if (entered != null) {
        sql.append("entered, ");
      }
      if (modified != null) {
        sql.append("modified, ");
      }
      sql.append(" link_module_id, link_item_id, " +
          " enteredby, modifiedby, default_file, image_width, image_height, comment, featured_file) " +
          "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ");
      if (entered != null) {
        sql.append("?, ");
      }
      if (modified != null) {
        sql.append("?, ");
      }
      sql.append("?, ?, ?, ?, ?, ?, ?, ?, ?) ");

      int i = 0;
      PreparedStatement pst = db.prepareStatement(sql.toString());
      if (folderId > 0) {
        pst.setInt(++i, folderId);
      } else {
        pst.setNull(++i, java.sql.Types.INTEGER);
      }
      pst.setString(++i, subject);
      pst.setString(++i, clientFilename);
      pst.setString(++i, filename);
      pst.setDouble(++i, version);
      pst.setInt(++i, size);
      pst.setBoolean(++i, enabled);
      pst.setInt(++i, downloads);
      if (entered != null) {
        pst.setTimestamp(++i, entered);
      }
      if (modified != null) {
        pst.setTimestamp(++i, modified);
      }
      pst.setInt(++i, linkModuleId);
      pst.setInt(++i, linkItemId);
      pst.setInt(++i, enteredBy);
      pst.setInt(++i, modifiedBy);
      pst.setBoolean(++i, defaultFile);
      pst.setInt(++i, imageWidth);
      pst.setInt(++i, imageHeight);
      pst.setString(++i, comment);
      pst.setBoolean(++i, featuredFile);
      pst.execute();
      pst.close();
      id = DatabaseUtils.getCurrVal(db, "project_files_item_id_seq", -1);
      // New default item
      if (defaultFile) {
        updateDefaultRecord(db, linkModuleId, linkItemId, id);
      }
      // Insert the version information
      if (doVersionInsert) {
        FileItemVersion thisVersion = new FileItemVersion();
        thisVersion.setId(this.getId());
        thisVersion.setSubject(subject);
        thisVersion.setClientFilename(clientFilename);
        thisVersion.setFilename(filename);
        thisVersion.setVersion(version);
        thisVersion.setSize(size);
        thisVersion.setEnteredBy(enteredBy);
        thisVersion.setModifiedBy(modifiedBy);
        thisVersion.setImageWidth(imageWidth);
        thisVersion.setImageHeight(imageHeight);
        thisVersion.setComment(comment);
        thisVersion.insert(db);
      }
      logUpload(db);
      if (doCommit) {
        db.commit();
      }
      result = true;
    } catch (Exception e) {
      e.printStackTrace(System.out);
      if (doCommit) {
        db.rollback();
      }
      throw new SQLException(e.getMessage());
    } finally {
      if (doCommit) {
        db.setAutoCommit(true);
      }
    }
    return result;
  }

  /**
   * Description of the Method
   *
   * @param db Description of Parameter
   * @return Description of the Returned Value
   * @throws SQLException Description of Exception
   */
  public boolean insertVersion(Connection db) throws SQLException {
    if (!isValid()) {
      return false;
    }
    boolean result = false;
    boolean doCommit = false;
    try {
      if (doCommit = db.getAutoCommit()) {
        db.setAutoCommit(false);
      }
      //Insert a new version of an existing file
      FileItemVersion thisVersion = new FileItemVersion();
      thisVersion.setId(this.getId());
      thisVersion.setSubject(subject);
      thisVersion.setClientFilename(clientFilename);
      thisVersion.setFilename(filename);
      thisVersion.setVersion(version);
      thisVersion.setSize(size);
      thisVersion.setEnteredBy(enteredBy);
      thisVersion.setModifiedBy(modifiedBy);
      thisVersion.setImageWidth(imageWidth);
      thisVersion.setImageHeight(imageHeight);
      thisVersion.setComment(comment);
      thisVersion.insert(db);

      //Update the master record
      int i = 0;
      PreparedStatement pst = db.prepareStatement(
          "UPDATE project_files " +
              "SET subject = ?, client_filename = ?, filename = ?, version = ?, " +
              "size = ?, modifiedby = ?, modified = CURRENT_TIMESTAMP, comment = ?, image_width = ?, image_height = ? , featured_file = ? " +
              "WHERE item_id = ? ");
      pst.setString(++i, subject);
      pst.setString(++i, clientFilename);
      pst.setString(++i, filename);
      pst.setDouble(++i, version);
      pst.setInt(++i, size);
      pst.setInt(++i, modifiedBy);
      pst.setString(++i, comment);
      pst.setInt(++i, imageWidth);
      pst.setInt(++i, imageHeight);
      pst.setBoolean(++i, featuredFile);
      pst.setInt(++i, this.getId());
      pst.execute();
      pst.close();
      logUpload(db);
      if (doCommit) {
        db.commit();
      }
      result = true;
    } catch (Exception e) {
      LOG.error("Could not insert version", e);
      if (doCommit) {
        db.rollback();
      }
      throw new SQLException(e.getMessage());
    } finally {
      if (doCommit) {
        db.setAutoCommit(true);
      }
    }
    return result;
  }

  /**
   * Description of the Method
   *
   * @param db Description of Parameter
   * @return Description of the Returned Value
   * @throws SQLException Description of Exception
   */
  public int update(Connection db) throws SQLException {
    if (this.getId() == -1) {
      throw new SQLException("ID was not specified");
    }
    if (!isValid()) {
      return -1;
    }
    int resultCount = 0;
    boolean commit = db.getAutoCommit();
    try {
      if (commit) {
        db.setAutoCommit(false);
      }
      int i = 0;
      // NOTE: Do not update the "modified" field because it is used for file paths
      PreparedStatement pst = db.prepareStatement(
          "UPDATE project_files " +
              "SET subject = ?, client_filename = ?, default_file = ?, comment = ?, featured_file = ? " +
              "WHERE item_id = ? ");
      pst.setString(++i, subject);
      pst.setString(++i, clientFilename);
      pst.setBoolean(++i, defaultFile);
      pst.setString(++i, comment);
      pst.setBoolean(++i, featuredFile);
      pst.setInt(++i, this.getId());
      resultCount = pst.executeUpdate();
      pst.close();
      // Set default
      if (defaultFile) {
        updateDefaultRecord(db, linkModuleId, linkItemId, id);
      }
      // Retrieve any versions
      this.buildVersionList(db);
      // Update version info for the corresponding file item version
      for (FileItemVersion latestVersion : versionList) {
        if (Double.toString(this.version).equals(
            Double.toString(latestVersion.getVersion()))) {
          latestVersion.setClientFilename(this.getClientFilename());
          latestVersion.setSubject(this.getSubject());
          latestVersion.setComment(this.getComment());
          latestVersion.update(db);
          break;
        }
      }
    } catch (Exception e) {
      if (commit) {
        db.rollback();
      }
      throw new SQLException(e.getMessage());
    } finally {
      if (commit) {
        db.setAutoCommit(true);
      }
    }
    return resultCount;
  }

  /**
   * Description of the Method
   *
   * @param db          Description of the Parameter
   * @param thisVersion Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  public boolean updateVersion(Connection db, FileItemVersion thisVersion) throws SQLException {
    // Set the master record
    subject = thisVersion.getSubject();
    clientFilename = thisVersion.getClientFilename();
    filename = thisVersion.getFilename();
    version = thisVersion.getVersion();
    size = thisVersion.getSize();
    enteredBy = thisVersion.getEnteredBy();
    modifiedBy = thisVersion.getModifiedBy();
    comment = thisVersion.getComment();
    modified = thisVersion.getModified();
    // Update the master record
    int i = 0;
    PreparedStatement pst = db.prepareStatement(
        "UPDATE project_files " +
            "SET subject = ?, client_filename = ?, filename = ?, version = ?, " +
            "size = ?, modifiedby = ?, modified = ?, comment = ?, featured_file = ? " +
            "WHERE item_id = ? ");
    pst.setString(++i, subject);
    pst.setString(++i, clientFilename);
    pst.setString(++i, filename);
    pst.setDouble(++i, version);
    pst.setInt(++i, size);
    pst.setInt(++i, modifiedBy);
    pst.setTimestamp(++i, modified);
    pst.setString(++i, comment);
    pst.setBoolean(++i, featuredFile);
    pst.setInt(++i, this.getId());
    pst.execute();
    pst.close();
    return true;
  }

  /**
   * Description of the Method
   *
   * @param db Description of Parameter
   * @return Description of the Returned Value
   * @throws SQLException Description of Exception
   */
  public boolean updateCounter(Connection db) throws SQLException {
    FileDownloadLog thisLog = new FileDownloadLog();
    thisLog.setItemId(id);
    thisLog.setVersion(version);
    thisLog.setUserId(enteredBy);
    thisLog.setFileSize(size);
    return thisLog.updateCounter(db);
  }

  /**
   * Description of the Method
   *
   * @param db Description of Parameter
   * @return Description of the Returned Value
   * @throws SQLException Description of Exception
   */
  public boolean buildVersionList(Connection db) throws SQLException {
    if (versionList == null) {
      versionList = new FileItemVersionList();
    } else {
      versionList.clear();
    }
    versionList.setItemId(this.getId());
    versionList.buildList(db);
    return true;
  }

  /**
   * Description of the Method
   *
   * @param db           Description of Parameter
   * @param baseFilePath Description of Parameter
   * @return Description of the Returned Value
   * @throws SQLException Description of Exception
   */
  public boolean delete(Connection db, String baseFilePath) throws SQLException {
    if (id == -1) {
      throw new SQLException("Id not specified");
    }
    this.buildVersionList(db);
    // Need to delete the actual files
    for (FileItemVersion thisVersion : versionList) {
      // For each version, delete the main record
      String filePath = baseFilePath + getDatePath(thisVersion.getEntered());
      File fileToDelete = new File(filePath + thisVersion.getFilename());
      if (!fileToDelete.delete()) {
        LOG.error("File not found -- could not delete file: " + fileToDelete.getPath());
      }
      // Delete the thumbnails for this version
      ThumbnailList thumbnailList = new ThumbnailList();
      thumbnailList.setItemId(thisVersion.getId());
      thumbnailList.setVersion(thisVersion.getVersion());
      thumbnailList.buildList(db);
      for (Thumbnail thisThumbnail : thumbnailList) {
        File thumbnailToDelete = new File(filePath + thisThumbnail.getFilename());
        if (!thumbnailToDelete.delete()) {
          LOG.error("File thumbnail not found -- could not delete file: " + fileToDelete.getPath());
        }
      }
    }
    boolean result = false;
    boolean commit = db.getAutoCommit();
    try {
      if (commit) {
        db.setAutoCommit(false);
      }
      //Delete the ratings
      Rating.delete(db, this.getId(), TABLE, PRIMARY_KEY);

      // Delete the log of downloads
      int i = 0;
      PreparedStatement pst = db.prepareStatement(
          "DELETE FROM project_files_download " +
              "WHERE item_id = ? ");
      pst.setInt(++i, this.getId());
      pst.execute();
      pst.close();
      // Delete the thumbnail
      i = 0;
      pst = db.prepareStatement(
          "DELETE FROM project_files_thumbnail " +
              "WHERE item_id = ? ");
      pst.setInt(++i, this.getId());
      pst.execute();
      pst.close();
      // Delete all of the versions
      i = 0;
      pst = db.prepareStatement(
          "DELETE FROM project_files_version " +
              "WHERE item_id = ? ");
      pst.setInt(++i, this.getId());
      pst.execute();
      pst.close();
      // Delete the master record
      i = 0;
      pst = db.prepareStatement(
          "DELETE FROM project_files " +
              "WHERE item_id = ? ");
      pst.setInt(++i, this.getId());
      pst.execute();
      pst.close();
      if (linkModuleId == Constants.PROJECT_IMAGE_FILES) {
        if (this.getDefaultFile()) {
          Project project = ProjectUtils.loadProject(linkItemId);
          // Enable the next image
          FileItemList files = new FileItemList();
          files.setLinkModuleId(Constants.PROJECT_IMAGE_FILES);
          files.setLinkItemId(linkItemId);
          files.setIgnoreId(this.getId());
          files.buildList(db);
          if (files.size() > 0) {
            project.setLogoId(files.get(0).getId());
          } else {
            project.setLogoId(-1);
          }
          project.updateLogoId(db);
        }
      }
      if (commit) {
        db.commit();
      }
      if (linkModuleId == Constants.PROJECT_IMAGE_FILES) {
        CacheUtils.invalidateValue(Constants.SYSTEM_PROJECT_CACHE, linkItemId);
      }
      result = true;
    } catch (Exception e) {
      if (commit) {
        db.rollback();
      }
      throw new SQLException(e.getMessage());
    } finally {
      if (commit) {
        db.setAutoCommit(true);
      }
    }
    return result;
  }

  /**
   * Gets the valid attribute of the FileItem object
   *
   * @return The valid value
   */
  private boolean isValid() {
    if (linkModuleId == -1 || linkItemId == -1) {
      LOG.debug("linkModuleId=" + linkModuleId);
      LOG.debug("linkItemId=" + linkItemId);
      errors.put("actionError", "Id not specified");
    }
    if (!StringUtils.hasText(subject)) {
      LOG.debug("subject=" + subject);
      errors.put("subjectError", "Required field");
    }
    if (!StringUtils.hasText(clientFilename)) {
      LOG.debug("clientFilename=" + clientFilename);
      errors.put("clientFilenameError", "Required field");
    }
    return !hasErrors();
  }

  /**
   * Populates the object's properties from the result set
   *
   * @param rs        Description of Parameter
   * @param isVersion Description of Parameter
   * @throws SQLException Description of Exception
   */
  protected void buildRecord(ResultSet rs, boolean isVersion) throws SQLException {
    id = rs.getInt("item_id");
    if (!isVersion) {
      linkModuleId = rs.getInt("link_module_id");
      linkItemId = rs.getInt("link_item_id");
      folderId = DatabaseUtils.getInt(rs, "folder_id", -1);
    }
    clientFilename = rs.getString("client_filename");
    filename = rs.getString("filename");
    subject = rs.getString("subject");
    size = rs.getInt("size");
    version = rs.getDouble("version");
    enabled = rs.getBoolean("enabled");
    downloads = rs.getInt("downloads");
    entered = rs.getTimestamp("entered");
    enteredBy = rs.getInt("enteredby");
    modified = rs.getTimestamp("modified");
    modifiedBy = rs.getInt("modifiedby");
    if (!isVersion) {
      thumbnailFilename = rs.getString("thumbnail");
    }
    defaultFile = rs.getBoolean("default_file");
    imageWidth = rs.getInt("image_width");
    imageHeight = rs.getInt("image_height");
    comment = rs.getString("comment");
    featuredFile = rs.getBoolean("featured_file");
    ratingCount = DatabaseUtils.getInt(rs, "rating_count", 0);
    ratingValue = DatabaseUtils.getInt(rs, "rating_value", 0);
    ratingAvg = DatabaseUtils.getDouble(rs, "rating_avg", 0.0);
    inappropriateCount = DatabaseUtils.getInt(rs, "inappropriate_count", 0);
  }

  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  private void logUpload(Connection db) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "INSERT INTO usage_log " +
            "(enteredby, action, record_id, record_size) VALUES (?, ?, ?, ?) ");
    int i = 0;
    pst.setInt(++i, enteredBy);
    pst.setInt(++i, 1);
    pst.setInt(++i, id);
    pst.setInt(++i, size);
    pst.execute();
    pst.close();
  }

  /**
   * Description of the Method
   *
   * @param db          Description of the Parameter
   * @param newFolderId Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public void updateFolderId(Connection db, int newFolderId) throws SQLException {
    if (id == -1) {
      throw new SQLException("ID not specified");
    }
    PreparedStatement pst = db.prepareStatement(
        "UPDATE project_files " +
            "SET folder_id = ? " +
            "WHERE item_id = ?");
    int i = 0;
    if (newFolderId > 0) {
      pst.setInt(++i, newFolderId);
    } else {
      pst.setNull(++i, java.sql.Types.INTEGER);
    }
    pst.setInt(++i, id);
    int count = pst.executeUpdate();
    pst.close();
    if (count == 1) {
      if (newFolderId > 0) {
        folderId = newFolderId;
      } else {
        folderId = -1;
      }
    }
  }

  /**
   * Gets the imageFormat attribute of the FileItem object
   *
   * @return The imageFormat value
   */
  public boolean isImageFormat() {
    String extension = getExtension();
    return (".gif".equals(extension) ||
        ".jpg".equals(extension) ||
        ".jpeg".equals(extension) ||
        ".png".equals(extension) ||
        ".bmp".equals(extension));
  }

  public boolean hasValidImageSize() {
    return (imageHeight > 0 && imageWidth > 0);
  }

  /**
   * Description of the Method
   *
   * @return Description of the Return Value
   */
  public boolean hasThumbnail() {
    return thumbnailFilename != null;
  }

  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  public boolean enable(Connection db) throws SQLException {
    if (this.getId() == -1) {
      throw new SQLException("File ID not specified");
    }
    boolean success = false;
    int i = 0;
    PreparedStatement pst = db.prepareStatement(
        "UPDATE project_files " +
            "SET enabled = " + DatabaseUtils.getTrue(db) + " " +
            "WHERE item_id = ? " +
            "AND modified = ? ");
    pst.setInt(++i, this.getId());
    pst.setTimestamp(++i, this.getModified());
    int resultCount = pst.executeUpdate();
    pst.close();
    if (resultCount == 1) {
      success = true;
    }
    return success;
  }

  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  public boolean disable(Connection db) throws SQLException {
    if (this.getId() == -1) {
      throw new SQLException("File ID not specified");
    }
    boolean success = false;
    int i = 0;
    PreparedStatement pst = db.prepareStatement(
        "UPDATE project_files SET " +
            "enabled = " + DatabaseUtils.getFalse(db) + " " +
            "WHERE item_id = ? " +
            "AND modified = ? ");
    pst.setInt(++i, this.getId());
    pst.setTimestamp(++i, this.getModified());
    int resultCount = pst.executeUpdate();
    pst.close();
    if (resultCount == 1) {
      success = true;
    }
    return success;
  }

  /**
   * Description of the Method
   *
   * @param db           Description of the Parameter
   * @param linkModuleId Description of the Parameter
   * @param linkItemId   Description of the Parameter
   * @param id           Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public static synchronized void updateDefaultRecord(Connection db, int linkModuleId, int linkItemId, int id) throws SQLException {
    // Turn off other defaults
    PreparedStatement pst = db.prepareStatement(
        "UPDATE project_files " +
            "SET default_file = ? " +
            "WHERE link_module_id = ? " +
            "AND link_item_id = ? " +
            "AND default_file = ? ");
    int i = 0;
    pst.setBoolean(++i, false);
    pst.setInt(++i, linkModuleId);
    pst.setInt(++i, linkItemId);
    pst.setBoolean(++i, true);
    pst.execute();
    pst.close();
    // Turn on this default
    pst = db.prepareStatement(
        "UPDATE project_files " +
            "SET default_file = ? " +
            "WHERE item_id = ? " +
            "AND default_file = ? ");
    pst.setBoolean(1, true);
    pst.setInt(2, id);
    pst.setBoolean(3, false);
    pst.execute();
    pst.close();
  }

  /**
   * For temporary UI entries, this unique value can be used for file selector
   *
   * @return date based unique value
   */
  public static String createUniqueValue() {
    Calendar cal = Calendar.getInstance();
    return (String.valueOf(cal.get(Calendar.HOUR)) +
        String.valueOf(cal.get(Calendar.MINUTE)) +
        String.valueOf(cal.get(Calendar.SECOND)) +
        String.valueOf(cal.get(Calendar.MILLISECOND)));
  }

  public String getUrlName(String maxWidth, String maxHeight) {
    return getUrlName(Integer.parseInt(maxWidth), Integer.parseInt(maxHeight));
  }

  public String getUrlName(int maxWidth, int maxHeight) {
    return linkModuleId + "-" + linkItemId + "-" + id + "-" + maxWidth + "x" + maxHeight + "/" + StringUtils.replace(StringUtils.toAllowedOnly("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890`~!@$^*()-_=+;:,.{}[]\" ", clientFilename), " ", "+");
  }
}
