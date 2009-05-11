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

import com.concursive.commons.db.DatabaseUtils;
import com.concursive.commons.text.StringUtils;
import com.concursive.commons.web.mvc.beans.GenericBean;
import com.concursive.connect.web.modules.wiki.utils.DiffCounter;
import com.concursive.connect.web.modules.wiki.utils.WikiUtils;
import org.suigeneris.jrcs.diff.Diff;
import org.suigeneris.jrcs.diff.Revision;
import org.suigeneris.jrcs.diff.simple.SimpleDiff;
import org.suigeneris.jrcs.util.ToString;

import java.sql.*;

/**
 * Represents the version info for a wiki page
 *
 * @author matt rajkowski
 * @version $Id$
 * @created February 7, 2006
 */
public class WikiVersion extends GenericBean {

  private int id = -1;
  private int wikiId = -1;
  private String summary = "";
  private String content = null;
  private Timestamp entered = null;
  private int enteredBy = -1;
  private boolean enabled = true;
  private int readCount = 0;
  private int size = 0;
  private int linesAdded = 0;
  private int linesChanged = 0;
  private int linesDeleted = 0;
  private int linesTotal = 0;


  public WikiVersion() {
  }

  public WikiVersion(ResultSet rs) throws SQLException {
    buildRecord(rs);
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public void setId(String tmp) {
    this.id = Integer.parseInt(tmp);
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public Timestamp getEntered() {
    return entered;
  }

  public void setEntered(Timestamp entered) {
    this.entered = entered;
  }

  public void setEntered(String entered) {
    this.entered = DatabaseUtils.parseTimestamp(entered);
  }

  public int getEnteredBy() {
    return enteredBy;
  }

  public void setEnteredBy(int enteredBy) {
    this.enteredBy = enteredBy;
  }

  public void setEnteredBy(String enteredBy) {
    this.enteredBy = Integer.parseInt(enteredBy);
  }

  public boolean getEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public void setEnabled(String enabled) {
    this.enabled = DatabaseUtils.parseBoolean(enabled);
  }

  public int getWikiId() {
    return wikiId;
  }

  public void setWikiId(int wikiId) {
    this.wikiId = wikiId;
  }

  public void setWikiId(String wikiId) {
    this.wikiId = Integer.parseInt(wikiId);
  }

  public String getSummary() {
    return summary;
  }

  public void setSummary(String summary) {
    this.summary = summary;
  }

  public int getLinesAdded() {
    return linesAdded;
  }

  public void setLinesAdded(int linesAdded) {
    this.linesAdded = linesAdded;
  }

  public void setLinesAdded(String linesAdded) {
    this.linesAdded = Integer.parseInt(linesAdded);
  }

  public int getLinesChanged() {
    return linesChanged;
  }

  public void setLinesChanged(int linesChanged) {
    this.linesChanged = linesChanged;
  }

  public void setLinesChanged(String linesChanged) {
    this.linesChanged = Integer.parseInt(linesChanged);
  }

  public int getLinesDeleted() {
    return linesDeleted;
  }

  public void setLinesDeleted(int linesDeleted) {
    this.linesDeleted = linesDeleted;
  }

  public void setLinesDeleted(String linesDeleted) {
    this.linesDeleted = Integer.parseInt(linesDeleted);
  }

  public int getReadCount() {
    return readCount;
  }

  public void setReadCount(int readCount) {
    this.readCount = readCount;
  }

  public void setReadCount(String readCount) {
    this.readCount = Integer.parseInt(readCount);
  }

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
  }

  public void setSize(String size) {
    this.size = Integer.parseInt(size);
  }

  public int getLinesTotal() {
    return linesTotal;
  }

  public void setLinesTotal(int linesTotal) {
    this.linesTotal = linesTotal;
  }

  public void setLinesTotal(String linesTotal) {
    this.linesTotal = Integer.parseInt(linesTotal);
  }

  private void buildRecord(ResultSet rs) throws SQLException {
    id = rs.getInt("version_id");
    wikiId = rs.getInt("wiki_id");
    content = rs.getString("content");
    entered = rs.getTimestamp("entered");
    enteredBy = rs.getInt("enteredby");
    enabled = rs.getBoolean("enabled");
    readCount = rs.getInt("read_count");
    summary = rs.getString("summary");
    linesAdded = rs.getInt("lines_added");
    linesChanged = rs.getInt("lines_changed");
    linesDeleted = rs.getInt("lines_deleted");
    size = rs.getInt("size");
    linesTotal = rs.getInt("lines_total");
  }

  public boolean insert(Connection db) throws SQLException {
    boolean commit = db.getAutoCommit();
    try {
      if (commit) {
        db.setAutoCommit(false);
      }
      StringBuffer sql = new StringBuffer();
      sql.append(
          "INSERT INTO project_wiki_version " +
              "(" + (id > -1 ? "version_id, " : "") + "wiki_id, content, enabled, read_count, summary, lines_added, lines_changed, lines_deleted, size, lines_total, ");
      if (entered != null) {
        sql.append("entered, ");
      }
      sql.append(
          "enteredby) ");
      sql.append("VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ");
      if (id > -1) {
        sql.append("?, ");
      }
      if (entered != null) {
        sql.append("?, ");
      }
      sql.append("?) ");
      int i = 0;
      //Insert the page
      PreparedStatement pst = db.prepareStatement(sql.toString());
      if (id > -1) {
        pst.setInt(++i, id);
      }
      pst.setInt(++i, wikiId);
      pst.setString(++i, content);
      pst.setBoolean(++i, enabled);
      pst.setInt(++i, readCount);
      pst.setString(++i, summary);
      pst.setInt(++i, linesAdded);
      pst.setInt(++i, linesChanged);
      pst.setInt(++i, linesDeleted);
      pst.setInt(++i, size);
      pst.setInt(++i, linesTotal);
      if (entered != null) {
        pst.setTimestamp(++i, entered);
      }
      pst.setInt(++i, enteredBy);
      pst.execute();
      pst.close();
      if (commit) {
        db.commit();
      }
      id = DatabaseUtils.getCurrVal(db, "project_wiki_version_version_id_seq", id);
    } catch (SQLException e) {
      if (commit) {
        db.rollback();
      }
      throw new SQLException(e.getMessage());
    } finally {
      if (commit) {
        db.setAutoCommit(true);
      }
    }
    return true;
  }

  public static boolean insert(Connection db, Wiki wiki) throws SQLException {
    boolean commit = db.getAutoCommit();
    try {
      if (commit) {
        db.setAutoCommit(false);
      }
      StringBuffer sql = new StringBuffer();
      sql.append(
          "INSERT INTO project_wiki_version " +
              "(wiki_id, content, size, lines_total, lines_added, ");
      if (wiki.getModified() != null) {
        sql.append("entered, ");
      }
      sql.append(
          "enteredby) ");
      sql.append("VALUES (?, ?, ?, ?, ?, ");
      if (wiki.getModified() != null) {
        sql.append("?, ");
      }
      sql.append("?) ");
      int i = 0;
      //Insert the page
      PreparedStatement pst = db.prepareStatement(sql.toString());
      pst.setInt(++i, wiki.getId());
      pst.setString(++i, wiki.getContent());
      pst.setInt(++i, wiki.getContent().length());
      pst.setInt(++i, StringUtils.countLines(wiki.getContent()));
      pst.setInt(++i, StringUtils.countLines(wiki.getContent()));
      if (wiki.getModified() != null) {
        pst.setTimestamp(++i, wiki.getModified());
      }
      pst.setInt(++i, wiki.getModifiedBy());
      pst.execute();
      pst.close();
      if (commit) {
        db.commit();
      }
      //id = DatabaseUtils.getCurrVal(db, "project_wiki_version_version_id_seq", id);
    } catch (SQLException e) {
      if (commit) {
        db.rollback();
      }
      throw new SQLException(e.getMessage());
    } finally {
      if (commit) {
        db.setAutoCommit(true);
      }
    }
    return true;
  }

  public static boolean insert(Connection db, Wiki previousWiki, Wiki updatedWiki) throws SQLException {
    boolean commit = db.getAutoCommit();
    try {
      // Determine changes
      String changes = null;
      try {
        Revision revision = Diff.diff(
            ToString.stringToArray(previousWiki.getContent()),
            ToString.stringToArray(updatedWiki.getContent()), null);
        changes = revision.toString();
      } catch (Exception e) {
        try {
          Revision revision = Diff.diff(
              ToString.stringToArray(previousWiki.getContent()),
              ToString.stringToArray(updatedWiki.getContent()), new SimpleDiff());
          changes = revision.toString();
        } catch (Exception e2) {
          com.concursive.connect.web.modules.wiki.utils.Diff diff = new com.concursive.connect.web.modules.wiki.utils.Diff();
          changes = diff.doDiff(previousWiki.getContent(), updatedWiki.getContent());
        }
      }

      // Count the changes
      DiffCounter counter = new DiffCounter();
      WikiUtils.countDiff(counter, changes);

      // Insert version
      if (commit) {
        db.setAutoCommit(false);
      }
      StringBuffer sql = new StringBuffer();
      sql.append(
          "INSERT INTO project_wiki_version " +
              "(wiki_id, content, lines_total, size, enteredby, lines_added, lines_changed, lines_deleted) ");
      sql.append("VALUES (?, ?, ?, ?, ?, ?, ?, ?) ");
      int i = 0;
      //Insert the page
      PreparedStatement pst = db.prepareStatement(sql.toString());
      pst.setInt(++i, updatedWiki.getId());
      pst.setString(++i, changes);
      pst.setInt(++i, StringUtils.countLines(updatedWiki.getContent()));
      pst.setInt(++i, updatedWiki.getContent().length());
      pst.setInt(++i, updatedWiki.getModifiedBy());
      pst.setInt(++i, counter.getAdded());
      pst.setInt(++i, counter.getChanged());
      pst.setInt(++i, counter.getDeleted());
      pst.execute();
      pst.close();
      if (commit) {
        db.commit();
      }
      //id = DatabaseUtils.getCurrVal(db, "project_wiki_version_version_id_seq", id);
    } catch (SQLException e) {
      if (commit) {
        db.rollback();
      }
      throw new SQLException(e.getMessage());
    } finally {
      if (commit) {
        db.setAutoCommit(true);
      }
    }
    return true;
  }
}
