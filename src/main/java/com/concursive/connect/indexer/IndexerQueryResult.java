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

package com.concursive.connect.indexer;

import com.concursive.commons.objects.ObjectUtils;
import com.concursive.commons.web.mvc.beans.GenericBean;
import com.concursive.connect.Constants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author jfielek, matt rajkowski
 * @created Jan 15, 2009
 */
public class IndexerQueryResult extends GenericBean {

  private static Log LOG = LogFactory.getLog(IndexerQueryResult.class);

  private String queryString = null;
  private int queryIndexType = Constants.INDEXER_FULL;

  // Indexed fields
  private int indexId = -1;
  private String type;
  private String objectId;
  private String objectSerialId;
  private String projectId;
  private String guests;
  private String membership;
  private String title;
  private String contents;
  private String modified;
  private String size;
  private String filename;
  private String newsPortal;
  private String newsStatus;
  private String subjectLink;

  public IndexerQueryResult() {
  }

  public IndexerQueryResult(IndexerQueryResultList response) {
    this.queryString = response.getQueryString();
    this.queryIndexType = response.getQueryIndexTypeInt();
  }

  public String toString() {
    String retval = "\n";
//    retval += "queryString    : " + queryString + "\n";
    retval += "queryIndexType : " + queryIndexType + "\n";
    retval += "indexId        : " + indexId + "\n";
    retval += "type           : " + type + "\n";
    retval += "objectId       : " + objectId + "\n";
    retval += "projectId      : " + projectId + "\n";
    retval += "guests         : " + guests + "\n";
    retval += "membership     : " + membership + "\n";
    retval += "title          : " + title + "\n";
//    retval += "contents       : " + contents + "\n";
    retval += "membership     : " + membership + "\n";
    retval += "modified       : " + modified + "\n";
    return retval;
  }

  public String getQueryIndexType() {
    return Integer.toString(queryIndexType);
  }

  public void setQueryIndexType(int queryIndexType) {
    this.queryIndexType = queryIndexType;
  }

  public void setQueryIndexType(String queryIndexType) {
    this.queryIndexType = new Integer(queryIndexType);
  }

  public String getQueryString() {
    return queryString;
  }

  public void setQueryString(String queryString) {
    this.queryString = queryString;
  }

  public String get(String field) {
    return ObjectUtils.getParam(this, field);
  }

  public String getFilename() {
    return filename;
  }

  public void setFilename(String filename) {
    this.filename = filename;
  }

  public String getModified() {
    return modified;
  }

  public void setModified(String modified) {
    this.modified = modified;
  }

  public String getSize() {
    return size;
  }

  public void setSize(String size) {
    this.size = size;
  }

  public String getIndexId() {
    return Integer.toString(this.indexId);
  }

  public void setIndexId(String id) {
    this.indexId = new Integer(id);
  }

  public void setIndexId(int id) {
    this.indexId = id;
  }

  public String getContents() {
    return contents;
  }

  public void setContents(String contents) {
    this.contents = contents;
  }

  public String getGuests() {
    return guests;
  }

  public void setGuests(String guests) {
    this.guests = guests;
  }

  public String getMembership() {
    return membership;
  }

  public void setMembership(String membership) {
    this.membership = membership;
  }

  public String getObjectId() {
    return objectId;
  }

  public void setObjectId(String objectId) {
    this.objectId = objectId;
  }

  public String getObjectSerialId() {
    return objectSerialId;
  }

  public void setObjectSerialId(String objectSerialId) {
    this.objectSerialId = objectSerialId;
  }

  public String getProjectId() {
    return projectId;
  }

  public void setProjectId(String projectId) {
    this.projectId = projectId;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getSubjectLink() {
    return subjectLink;
  }

  public void setSubjectLink(String subjectLink) {
    this.subjectLink = subjectLink;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getNewsStatus() {
    return newsStatus;
  }

  public void setNewsStatus(String newsStatus) {
    this.newsStatus = newsStatus;
  }

  public String getNewsPortal() {
    return newsPortal;
  }

  public void setNewsPortal(String newsPortal) {
    this.newsPortal = newsPortal;
  }
}
