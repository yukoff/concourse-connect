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

package com.concursive.connect.web.modules.activity.dao;

import com.concursive.commons.db.DatabaseUtils;
import com.concursive.commons.text.StringUtils;
import com.concursive.commons.web.URLFactory;
import com.concursive.connect.Constants;
import com.concursive.connect.web.utils.PagedListInfo;
import com.concursive.connect.web.modules.wiki.utils.WikiToHTMLContext;
import com.concursive.connect.web.modules.wiki.utils.WikiToHTMLUtils;

import java.sql.*;
import java.sql.Date;
import java.util.*;

/**
 * A collection of ProjectHistory objects
 *
 * @author Ananth
 * @version ProjectHistoryList.java Feb 11, 2009 5:15:27 PM Ananth $
 * @created Feb 11, 2009
 */
public class ProjectHistoryList extends ArrayList<ProjectHistory> {
  //object constants to be used while recording history events
  public final static String AD_OBJECT = "ad";
  public final static String BADGE_OBJECT = "badge";
  public final static String BLOG_OBJECT = "blog";
  public final static String CLAIM_OBJECT = "claim";
  public final static String CLASSIFIED_OBJECT = "classified";
  public final static String DISCUSSION_OBJECT = "discussion";
  public final static String DOCUMENT_OBJECT = "document";
  public final static String DOCUMENT_DOWNLOAD_OBJECT = "document-download";
  public final static String DOCUMENT_DELETE_OBJECT = "document-delete";
  public final static String FOLDER_OBJECT = "folder";
  public final static String IMAGE_OBJECT = "image";
  public final static String INVITES_OBJECT = "invites";
  public final static String LIST_OBJECT = "list";
  public final static String MEETING_OBJECT = "meeting";
  public final static String MEMBER_OBJECT = "member";
  public final static String PROFILE_OBJECT = "profile";
  public final static String RATING_OBJECT = "rating";
  public final static String ROLE_OBJECT = "role";
  public final static String SITE_OBJECT = "site";
  public final static String TOOLS_OBJECT = "tools";
  public final static String TOPIC_OBJECT = "topic";
  public final static String QUESTION_OBJECT = "question";
  public final static String REPLY_OBJECT = "reply";
  public final static String WIKI_OBJECT = "wiki";
  public final static String WIKI_COMMENT_OBJECT = "wiki-comment";
  public final static String ACTIVITY_ENTRY_OBJECT = "user-entry";
  public final static String SITE_CHATTER_OBJECT = "site-chatter";
  public final static String SITE_TWITTER_OBJECT = "site-twitter";
  public final static String TWITTER_OBJECT = "twitter";

  //Event constants
  public final static int ADD_PROFILE_EVENT = 2009022601; //profile
  public final static int UPDATE_USER_PROFILE_EVENT = 2009022602; //profile
  public final static int BECOME_PROFILE_OWNER_EVENT = 2009022603; //profile
  public final static int UPDATE_PROFILE_EVENT = 2009022604; //profile
  public final static int CLAIM_LISTING_EVENT = 2009022605; //claim
  public final static int USER_REGISTRATION_EVENT = 2009022606; //site
  public final static int INVITE_MEMBER_EVENT = 2009022607; //invites
  public final static int BECOME_PROFILE_FAN_EVENT = 2009022608; //member
  public final static int ACCEPT_INVITATION_EVENT = 2009022609; //invites
  public final static int APPROVED_MEMBER_EVENT = 2009120701; //invites
  public final static int PROMOTE_MEMBER_EVENT = 2009022610; //role
  public final static int GRANT_MEMBER_TOOLS_EVENT = 2009022611; //tools
  public final static int BOOKMARK_PROFILE_EVENT = 2009022612; //list
  public final static int SHARE_PROFILE_IMAGE_EVENT = 2009022613; //image
  public final static int REVIEW_PROFILE_EVENT = 2009022614; //rating
  public final static int UPDATE_PROFILE_REVIEW_EVENT = 2009022615; //rating
  public final static int PUBLISH_BLOG_EVENT = 2009022616; //blog post
  public final static int SETUP_PROFILE_MEETING_EVENT = 2009022617; //meeting
  public final static int UPDATE_PROFILE_MEETING_EVENT = 2009022618; //meeting
  public final static int CONTRIBUTE_WIKI_EVENT = 2009022619; //wiki
  public final static int CONTRIBUTE_WIKI_COMMENT_EVENT = 2009082145; //wiki comment
  public final static int CREATE_FORUM_EVENT = 2009022620; //discussion
  public final static int POST_FORUM_TOPIC_EVENT = 2009022621; //topic
  public final static int POST_FORUM_QUESTION_EVENT = 2009030416; //topic
  public final static int POST_FORUM_TOPIC_RESPONSE_EVENT = 2009022622; //topic
  public final static int MARK_FORUM_TOPIC_RESPONSE_EVENT = 2009022623; //topic
  public final static int ADD_PROFILE_PROMOTION_EVENT = 2009022624; //ad
  public final static int POST_CLASSIFIED_AD_EVENT = 2009022625; //classified
  public final static int SHARE_PROFILE_DOCUMENT_EVENT = 2009022626; //document
  public final static int DOWNLOAD_PROFILE_DOCUMENT_EVENT = 2009022627; //download
  public final static int DELETE_PROFILE_DOCUMENT_EVENT = 2009022628; //document-delete
  public final static int CREATE_PROFILE_FOLDER_EVENT = 2009022629; //folder
  public final static int GRANT_PROFILE_BADGE_EVENT = 2009022630; //badge
  public final static int ADD_ACTIVITY_ENTRY_EVENT = 2009033118; //badge
  public final static int TWITTER_EVENT = 2009110514; //twitter

  private PagedListInfo pagedListInfo = null;
  private int projectId = -1;
  private int enteredBy = -1;
  private String linkObject = null;
  private int linkItemId = -1;
  private int eventType = -1;
  private ArrayList<String> objectPreferences = null;
  private int instanceId = -1;
  private int projectCategoryId = -1;
  private int forUser = -1;
  private Timestamp rangeStart = null;
  private Timestamp rangeEnd = null;
  private Timestamp untilLinkStartDate = null;
  private int publicProjects = Constants.UNDEFINED;
  private int forParticipant = Constants.UNDEFINED;
  private boolean forEmailUpdates = false;
  private int emailUpdatesMember = -1;
  private int emailUpdatesSchedule = -1;

  public void forMemberEmailUpdates(int emailUpdatesMember, int emailUpdatesSchedule) {
    this.forEmailUpdates = true;
    this.emailUpdatesMember = emailUpdatesMember;
    this.emailUpdatesSchedule = emailUpdatesSchedule;
  }

  public Timestamp getRangeStart() {
    return rangeStart;
  }

  public void setRangeStart(Timestamp rangeStart) {
    this.rangeStart = rangeStart;
  }

  public Timestamp getRangeEnd() {
    return rangeEnd;
  }

  public void setRangeEnd(Timestamp rangeEnd) {
    this.rangeEnd = rangeEnd;
  }

  public int getEventType() {
    return eventType;
  }

  public void setEventType(int eventType) {
    this.eventType = eventType;
  }

  public int getLinkItemId() {
    return linkItemId;
  }

  public void setLinkItemId(int linkItemId) {
    this.linkItemId = linkItemId;
  }

  public String getLinkObject() {
    return linkObject;
  }

  public void setLinkObject(String linkObject) {
    this.linkObject = linkObject;
  }

  public int getEnteredBy() {
    return enteredBy;
  }

  public void setEnteredBy(int enteredBy) {
    this.enteredBy = enteredBy;
  }

  public int getProjectId() {
    return projectId;
  }

  public void setProjectId(int projectId) {
    this.projectId = projectId;
  }

  /**
   * @return the objectPreferences
   */
  public ArrayList<String> getObjectPreferences() {
    return objectPreferences;
  }

  /**
   * @param objectPreferences the objectPreferences to set
   */
  public void setObjectPreferences(ArrayList<String> objectPreferences) {
    this.objectPreferences = objectPreferences;
  }

  public int getInstanceId() {
    return instanceId;
  }

  public void setInstanceId(int instanceId) {
    this.instanceId = instanceId;
  }

  public void setInstanceId(String tmp) {
    this.instanceId = Integer.parseInt(tmp);
  }

  /**
   * @return the projectCategoryId
   */
  public int getProjectCategoryId() {
    return projectCategoryId;
  }

  /**
   * @param projectCategoryId the projectCategoryId to set
   */
  public void setProjectCategoryId(int projectCategoryId) {
    this.projectCategoryId = projectCategoryId;
  }

  public void setProjectCategoryId(String projectCategoryId) {
    this.projectCategoryId = Integer.parseInt(projectCategoryId);
  }

  public int getForUser() {
    return forUser;
  }

  public void setForUser(int tmp) {
    this.forUser = tmp;
  }

  public void setForUser(String tmp) {
    this.forUser = Integer.parseInt(tmp);
  }

  public ProjectHistoryList() {
  }

  public void setPagedListInfo(PagedListInfo tmp) {
    this.pagedListInfo = tmp;
  }

  public PagedListInfo getPagedListInfo() {
    return pagedListInfo;
  }

  public static ProjectHistory getObject(ResultSet rs) throws SQLException {
    ProjectHistory object = new ProjectHistory(rs);
    return object;
  }

  /**
   * @return the untilLinkStartDate
   */
  public Timestamp getUntilLinkStartDate() {
    return untilLinkStartDate;
  }

  /**
   * @param untilLinkStartDate the untilLinkStartDate to set
   */
  public void setUntilLinkStartDate(Timestamp untilLinkStartDate) {
    this.untilLinkStartDate = untilLinkStartDate;
  }

  public void setUntilLinkStartDate(String untilLinkStartDate) {
    this.untilLinkStartDate = DatabaseUtils.parseTimestamp(untilLinkStartDate);
  }

  public int getPublicProjects() {
    return publicProjects;
  }

  public void setPublicProjects(int publicProjects) {
    this.publicProjects = publicProjects;
  }

  public void setPublicProjects(String publicProjects) {
    this.publicProjects = DatabaseUtils.parseBooleanToConstant(publicProjects);
  }

  public int getForParticipant() {
    return forParticipant;
  }

  public void setForParticipant(int forParticipant) {
    this.forParticipant = forParticipant;
  }

  public void setForParticipant(String tmp) {
    forParticipant = DatabaseUtils.parseBooleanToConstant(tmp);
  }

  public void select(Connection db) throws SQLException {
    buildList(db);
  }

  public void buildList(Connection db) throws SQLException {
    PreparedStatement pst = null;
    ResultSet rs = null;
    int items = -1;

    StringBuffer sqlSelect = new StringBuffer();
    StringBuffer sqlCount = new StringBuffer();
    StringBuffer sqlFilter = new StringBuffer();
    StringBuffer sqlOrder = new StringBuffer();

    //Need to build a base SQL statement for counting records
    sqlCount.append(
        "SELECT COUNT(*) AS recordcount " +
            "FROM project_history ph " +
            "WHERE history_id > -1 ");

    createFilter(sqlFilter);

    if (pagedListInfo == null) {
      pagedListInfo = new PagedListInfo();
      pagedListInfo.setItemsPerPage(0);
    }

    //Get the total number of records matching filter
    pst = db.prepareStatement(sqlCount.toString() + sqlFilter.toString());
    items = prepareFilter(pst);
    rs = pst.executeQuery();
    if (rs.next()) {
      int maxRecords = rs.getInt("recordcount");
      pagedListInfo.setMaxRecords(maxRecords);
    }
    rs.close();
    pst.close();

    //Determine the offset, based on the filter, for the first record to show
    if (!pagedListInfo.getCurrentLetter().equals("")) {
      pst = db.prepareStatement(sqlCount.toString() +
          sqlFilter.toString() +
          "AND lower(description) < ? ");
      items = prepareFilter(pst);
      pst.setString(++items, pagedListInfo.getCurrentLetter().toLowerCase());
      rs = pst.executeQuery();
      if (rs.next()) {
        int offsetCount = rs.getInt("recordcount");
        pagedListInfo.setCurrentOffset(offsetCount);
      }
      rs.close();
      pst.close();
    }

    //Determine column to sort by
    pagedListInfo.setDefaultSort("link_start_date DESC", null);
    pagedListInfo.appendSqlTail(db, sqlOrder);

    //Need to build a base SQL statement for returning records
    pagedListInfo.appendSqlSelectHead(db, sqlSelect);
    sqlSelect.append(
        "* " +
            "FROM project_history ph " +
            "WHERE history_id > -1 ");
    pst = db.prepareStatement(sqlSelect.toString() + sqlFilter.toString() + sqlOrder.toString());
    items = prepareFilter(pst);

    rs = pst.executeQuery();
    if (pagedListInfo != null) {
      pagedListInfo.doManualOffset(db, rs);
    }
    int count = 0;
    while (rs.next()) {
      if (pagedListInfo != null && pagedListInfo.getItemsPerPage() > 0 &&
          DatabaseUtils.getType(db) == DatabaseUtils.MSSQL &&
          count >= pagedListInfo.getItemsPerPage()) {
        break;
      }
      ++count;
      ProjectHistory thisHistory = new ProjectHistory(rs);
      this.add(thisHistory);
    }
    rs.close();
    pst.close();
  }

  protected void createFilter(StringBuffer sqlFilter) throws SQLException {
    if (sqlFilter == null) {
      sqlFilter = new StringBuffer();
    }
    if (projectId > -1) {
      sqlFilter.append("AND project_id = ? ");
    }
    if (enteredBy > -1) {
      sqlFilter.append("AND enteredby = ? ");
    }
    if (linkObject != null) {
      sqlFilter.append("AND link_object = ? ");
    }
    if (linkItemId > -1) {
      sqlFilter.append("AND link_item_id = ? ");
    }
    if (eventType > -1) {
      sqlFilter.append("AND event_type = ? ");
    }
    if (rangeStart != null) {
      sqlFilter.append("AND link_start_date >= ? ");
    }
    if (rangeEnd != null) {
      sqlFilter.append("AND link_start_date < ? ");
    }
    if (untilLinkStartDate != null) {
      sqlFilter.append("AND link_start_date <= ? ");
    }
    if (objectPreferences != null && objectPreferences.size() > 0) {
      sqlFilter.append(" AND link_object IN ( ");
      Iterator<String> itr = objectPreferences.iterator();
      while (itr.hasNext()) {
        String objectPreference = itr.next();
        if (StringUtils.hasText(objectPreference)) {
          sqlFilter.append("?");
        }
        if (itr.hasNext()) {
          sqlFilter.append(",");
        }
      }
      sqlFilter.append(" ) ");
    }
    if (instanceId > -1 || projectCategoryId > -1) {
      sqlFilter.append("AND project_id IN " +
          "(SELECT project_id FROM projects " +
          "WHERE project_id > -1 " +
          (instanceId > -1 ? "AND instance_id = ? " : "") +
          (projectCategoryId > -1 ? "AND category_id = ? " : "") +
          ") ");
    }
    if (forUser != -1) {
      sqlFilter.append("AND (ph.project_id IN (SELECT DISTINCT project_id FROM project_team WHERE user_id = ? " +
          "AND status IS NULL) OR ph.project_id IN (SELECT project_id FROM projects WHERE allow_guests = ? AND approvaldate IS NOT NULL)) ");
    }
    if (forEmailUpdates) {
      sqlFilter.append("AND ph.project_id IN " +
              "(SELECT DISTINCT project_id FROM project_team WHERE user_id = ? " +
              " AND status IS NULL AND email_updates_schedule = ?) ");
    }
    if (publicProjects == Constants.TRUE || forParticipant == Constants.TRUE) {
      sqlFilter.append("AND project_id IN (SELECT project_id FROM projects WHERE project_id > 0 ");
      if (publicProjects == Constants.TRUE) {
        sqlFilter.append("AND allow_guests = ? AND approvaldate IS NOT NULL ");
      }
      if (forParticipant == Constants.TRUE) {
        sqlFilter.append("AND (allows_user_observers = ? OR allow_guests = ?) AND approvaldate IS NOT NULL ");
      }
      sqlFilter.append(") ");
    }
  }


  protected int prepareFilter(PreparedStatement pst) throws SQLException {
    int i = 0;
    if (projectId > -1) {
      pst.setInt(++i, projectId);
    }
    if (enteredBy > -1) {
      pst.setInt(++i, enteredBy);
    }
    if (linkObject != null) {
      pst.setString(++i, linkObject);
    }
    if (linkItemId > -1) {
      pst.setInt(++i, linkItemId);
    }
    if (eventType > -1) {
      pst.setInt(++i, eventType);
    }
    if (rangeStart != null) {
      pst.setTimestamp(++i, rangeStart);
    }
    if (rangeEnd != null) {
      pst.setTimestamp(++i, rangeEnd);
    }
    if (untilLinkStartDate != null) {
      pst.setTimestamp(++i, untilLinkStartDate);
    }
    if (objectPreferences != null && objectPreferences.size() > 0) {
      Iterator<String> itr = objectPreferences.iterator();
      while (itr.hasNext()) {
        String objectPreference = itr.next();
        if (StringUtils.hasText(objectPreference)) {
          pst.setString(++i, objectPreference);
        }
      }
    }
    if (instanceId > -1) {
      pst.setInt(++i, instanceId);
    }
    if (projectCategoryId > -1) {
      pst.setInt(++i, projectCategoryId);
    }
    if (forUser != -1) {
      pst.setInt(++i, forUser);
      pst.setBoolean(++i, true);
    }
    if (forEmailUpdates) {
      pst.setInt(++i, emailUpdatesMember);
      pst.setInt(++i, emailUpdatesSchedule);
    }
    if (publicProjects == Constants.TRUE) {
      pst.setBoolean(++i, true);
    }
    if (forParticipant == Constants.TRUE) {
      pst.setBoolean(++i, true);
      pst.setBoolean(++i, true);
    }
    return i;
  }

  public static void delete(Connection db, int projectId) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "DELETE FROM project_history " +
            "WHERE project_id = ? ");
    pst.setInt(1, projectId);
    pst.execute();
    pst.close();
  }

  public HashMap getList(Connection db, int userId, String serverURL) throws SQLException {
    this.buildList(db);

    HashMap map = new HashMap();
    for (ProjectHistory history : this) {
      Date historyDate = new Date(history.getLinkStartDate().getTime());
      ArrayList descriptions = (ArrayList) map.get(historyDate.toString());
      if (descriptions == null) {
        descriptions = new ArrayList();
        map.put(historyDate.toString(), descriptions);
      }
      WikiToHTMLContext wikiToHTMLContext = new WikiToHTMLContext(userId, serverURL);
      String wikiLinkString = WikiToHTMLUtils.getHTML(wikiToHTMLContext, db, history.getDescription());
      descriptions.add(wikiLinkString);
    }

    return map;
  }
}