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
import com.concursive.connect.web.modules.common.social.comments.dao.CommentList;
import com.concursive.connect.web.modules.common.social.rating.dao.Rating;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

/**
 * Contains a collection of wiki pages
 *
 * @author matt rajkowski
 * @version $Id: WikiCommentList.java Exp
 *          $
 * @created February 7, 2006
 */
public class WikiCommentList extends CommentList {

  public WikiCommentList() {
    tableName = WikiComment.TABLE;
    uniqueFieldId = WikiComment.UNIQUE_FIELD;
  }


  public void setWikiId(int tmp) {
    setLinkItemId(tmp);
  }

  public void setWikiId(String tmp) {
    setLinkItemId(tmp);
  }

  public int getWikiId() {
    return getLinkItemId();
  }

  public void buildList(Connection db) throws SQLException {
    PreparedStatement pst = prepareList(db);
    ResultSet rs = pst.executeQuery();
    while (rs.next()) {
      WikiComment thisRecord = new WikiComment(rs);
      this.add(thisRecord);
    }
    rs.close();
    pst.close();
  }

  /**
   * @param db
   * @param newsId
   * @throws SQLException Deletes the comments for the specified wiki and the the ratings associated with the comments
   */
  public void delete(Connection db) throws SQLException {
    Iterator itr = this.iterator();
    while (itr.hasNext()) {
      WikiComment wikiCommentComment = (WikiComment) itr.next();
      Rating.delete(db, wikiCommentComment.getId(), WikiComment.TABLE, Comment.PRIMARY_KEY);
    }
    CommentList.delete(db, WikiComment.UNIQUE_FIELD, getWikiId(), WikiComment.TABLE);
  }
}
