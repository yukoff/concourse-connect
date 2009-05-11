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

package com.concursive.connect.web.modules.wiki.dao;

import com.concursive.connect.web.modules.common.social.comments.dao.Comment;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Represents a wiki page
 *
 * @author matt rajkowski
 * @version $Id$
 * @created February 7, 2006
 */
public class WikiComment extends Comment {

  public static String TABLE = "project_wiki_comment";
  public static String UNIQUE_FIELD = "wiki_id";

  public WikiComment() {
    tableName = TABLE;
    uniqueFieldId = UNIQUE_FIELD;
  }

  public WikiComment(ResultSet rs) throws SQLException {
    tableName = TABLE;
    uniqueFieldId = UNIQUE_FIELD;
    buildRecord(rs);
  }

  public WikiComment(Connection db, int id) throws SQLException {
    tableName = TABLE;
    uniqueFieldId = UNIQUE_FIELD;
    queryRecord(db, id);
  }

  public WikiComment(Connection db, int id, int wikiId) throws SQLException {
    tableName = TABLE;
    uniqueFieldId = UNIQUE_FIELD;
    this.setWikiId(wikiId);
    queryRecord(db, id);
  }

  public int getWikiId() {
    return linkItemId;
  }

  public void setWikiId(int wikiId) {
    this.linkItemId = wikiId;
  }

  public void setWikiId(String wikiId) {
    this.linkItemId = Integer.parseInt(wikiId);
  }

  public void queryRecord(Connection db, int commentId) throws SQLException {
    super.queryRecord(db, commentId);
  }

  public boolean insert(Connection db) throws SQLException {
    return super.insert(db);
  }

  public int update(Connection db, WikiComment originalWiki) throws SQLException {
    return super.update(db, originalWiki);
  }

  public void delete(Connection db) throws SQLException {
    super.delete(db);
  }

  public void close(Connection db) throws SQLException {
    super.close(db);
  }
}
