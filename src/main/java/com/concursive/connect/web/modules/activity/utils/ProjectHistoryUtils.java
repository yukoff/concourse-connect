/*
 * ConcourseConnect
 * Copyright 2010 Concursive Corporation
 * http://www.concursive.com
 *
 * This file is part of ConcourseConnect and is licensed under a commercial
 * license, not an open source license.
 *
 * Attribution Notice: ConcourseConnect is an Original Work of software created
 * by Concursive Corporation
 */
package com.concursive.connect.web.modules.activity.utils;

import com.concursive.connect.web.modules.activity.beans.ProjectHistoryReplyBean;
import com.concursive.connect.web.modules.activity.dao.ProjectHistory;
import com.concursive.connect.web.modules.activity.dao.ProjectHistoryList;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.login.utils.UserUtils;
import com.concursive.connect.web.modules.wiki.utils.WikiLink;
import com.concursive.connect.web.modules.wiki.utils.WikiUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Utilities for working with ProjectHistory classes
 *
 * @author matt rajkowski
 * @created Jan 13, 2010 11:57:31 AM
 */
public class ProjectHistoryUtils {
  private static final Log LOG = LogFactory.getLog(ProjectHistoryUtils.class);

  public synchronized static void insertReply(Connection db, ProjectHistory parentProjectHistory, ProjectHistoryReplyBean reply, User user) throws SQLException {
    // Construct a project history based on the parent project history and the reply bean
    ProjectHistory projectHistory = new ProjectHistory();
    projectHistory.setEnteredBy(user.getId());
    projectHistory.setProjectId(parentProjectHistory.getProjectId());
    projectHistory.setEventType(ProjectHistoryList.ADD_ACTIVITY_ENTRY_EVENT);
    if (ProjectHistoryList.SITE_CHATTER_OBJECT.equals(parentProjectHistory.getLinkObject())) {
      // Maintain the site-chatter value
      projectHistory.setLinkObject(ProjectHistoryList.SITE_CHATTER_OBJECT);
    } else {
      // Default to a user-entry value
      projectHistory.setLinkObject(ProjectHistoryList.ACTIVITY_ENTRY_OBJECT);
    }
    projectHistory.setLinkItemId(user.getId());
    // @todo move to application.xml
    projectHistory.setDescription(
        WikiLink.generateLink(user.getProfileProject()) +
            (parentProjectHistory.getProjectId() != user.getProfileProject().getId() ?
                " @" + WikiLink.generateLink(parentProjectHistory.getProject()) : "") +
            (user.getId() != parentProjectHistory.getEnteredBy() ?
                " in reply to " + WikiLink.generateLink(UserUtils.loadUser(parentProjectHistory.getEnteredBy()).getProfileProject()) : "") +
            ": " +
            WikiUtils.addWikiLinks(reply.getDescription()));
    projectHistory.setParentId(parentProjectHistory.getId());
    if (parentProjectHistory.getTopId() > -1) {
      projectHistory.setTopId(parentProjectHistory.getTopId());
    } else {
      projectHistory.setTopId(parentProjectHistory.getId());
    }
    projectHistory.setPosition(ProjectHistoryUtils.findNextPosition(db, projectHistory.getTopId()));
    projectHistory.setThreadPosition(ProjectHistoryUtils.findNextThreadPosition(db, parentProjectHistory));
    projectHistory.setIndent(parentProjectHistory.getIndent() + 1);
    projectHistory.setRelativeEnteredby(parentProjectHistory.getEnteredBy());
    projectHistory.setLineage(parentProjectHistory.getLineage() + parentProjectHistory.getId() + "/");

    // Reply transaction
    try {
      db.setAutoCommit(false);
      projectHistory.updateThreadPosition(db);
      projectHistory.updateChildCount(db);
      projectHistory.insert(db);
      projectHistory.updateRelativeDate(db);
      db.commit();
    } catch (Exception e) {
      db.rollback();
    } finally {
      db.setAutoCommit(true);
    }

  }

  public static int findNextPosition(Connection db, int projectHistoryId) throws SQLException {
    int position = 0;
    PreparedStatement pst = db.prepareStatement(
        "SELECT max(position) AS position " +
            "FROM project_history " +
            "WHERE history_id = ? OR top_id = ? ");
    pst.setInt(1, projectHistoryId);
    pst.setInt(2, projectHistoryId);
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      position = rs.getInt("position");
    }
    rs.close();
    pst.close();
    return (position + 1);
  }

  public static int findNextThreadPosition(Connection db, ProjectHistory parentProjectHistory) throws SQLException {
    int count = 0;
    PreparedStatement pst = db.prepareStatement(
        "SELECT count(*) AS ccount " +
            "FROM project_history " +
            "WHERE lineage LIKE ? ");
    pst.setString(1, parentProjectHistory.getLineage() + parentProjectHistory.getId() + "/%");
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      count = rs.getInt("ccount");
    }
    rs.close();
    pst.close();
    return (parentProjectHistory.getThreadPosition() + count + 1);
  }

  public static int queryAdditionalCommentsCount(Connection db, ProjectHistory projectHistory) throws SQLException {
    int count = 0;
    int topId = projectHistory.getTopId();
    if (topId == -1) {
      topId = projectHistory.getId();
    }
    PreparedStatement pst = db.prepareStatement(
        "SELECT count(*) AS comment_count " +
            "FROM project_history " +
            "WHERE top_id = ? AND position > ? ");
    pst.setInt(1, topId);
    pst.setInt(2, projectHistory.getPosition());
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      count = rs.getInt("comment_count");
    }
    rs.close();
    pst.close();
    return count;
  }
}
