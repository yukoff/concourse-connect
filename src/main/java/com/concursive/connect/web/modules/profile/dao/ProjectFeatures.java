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

package com.concursive.connect.web.modules.profile.dao;

import com.concursive.commons.db.DatabaseUtils;
import com.concursive.commons.web.mvc.beans.GenericBean;
import com.concursive.connect.Constants;
import com.concursive.connect.cache.utils.CacheUtils;

import java.sql.*;

/**
 * Represents the features of a project
 *
 * @author matt rajkowski
 * @version $Id$
 * @created March 6, 2008
 */
public class ProjectFeatures extends GenericBean {

  private int id = -1;
  private int modifiedBy = -1;
  private Timestamp modified = null;

  private boolean allowGuests = false;
  private boolean updateAllowGuests = false;
  private boolean updateAllowParticipants = false;
  private boolean allowParticipants = false;
  private boolean membershipRequired = false;
  private boolean updateMembershipRequired = false;

  // Tab visibility
  private boolean showProfile = false;
  private boolean showWiki = false;
  private boolean showDashboard = false;
  private boolean showCalendar = false;
  private boolean showNews = false;
  private boolean showDetails = false;
  private boolean showTeam = false;
  private boolean showPlan = false;
  private boolean showLists = false;
  private boolean showDiscussion = false;
  private boolean showTickets = false;
  private boolean showDocuments = false;
  private boolean showBadges = false;
  private boolean showReviews = false;
  private boolean showClassifieds = false;
  private boolean showAds = false;
  private boolean showMessages = false;
  private boolean showWebcasts = false;

  // Tab name
  private String labelProfile = null;
  private String labelWiki = null;
  private String labelDashboard = null;
  private String labelCalendar = null;
  private String labelNews = null;
  private String labelDetails = null;
  private String labelTeam = null;
  private String labelPlan = null;
  private String labelLists = null;
  private String labelDiscussion = null;
  private String labelTickets = null;
  private String labelDocuments = null;
  private String labelBadges = null;
  private String labelReviews = null;
  private String labelClassifieds = null;
  private String labelAds = null;
  private String labelMessages = null;
  private String labelWebcasts = null;

  // Tab order
  private int orderProfile = 1;
  private int orderDashboard = 2;
  private int orderNews = 3;
  private int orderCalendar = 4;
  private int orderWiki = 5;
  private int orderDiscussion = 6;
  private int orderDocuments = 7;
  private int orderLists = 8;
  private int orderPlan = 9;
  private int orderTickets = 10;
  private int orderTeam = 11;
  private int orderDetails = 12;
  private int orderBadges = 13;
  private int orderReviews = 14;
  private int orderClassifieds = 15;
  private int orderAds = 16;
  private int orderMessages = 17;
  private int orderWebcasts = 18;

  // Describing the tabs
  private String descriptionProfile = null;
  private String descriptionDashboard = null;
  private String descriptionNews = null;
  private String descriptionCalendar = null;
  private String descriptionWiki = null;
  private String descriptionDiscussion = null;
  private String descriptionDocuments = null;
  private String descriptionLists = null;
  private String descriptionPlan = null;
  private String descriptionTickets = null;
  private String descriptionTeam = null;
  private String descriptionBadges = null;
  private String descriptionReviews = null;
  private String descriptionClassifieds = null;
  private String descriptionAds = null;
  private String descriptionMessages = null;
  private String descriptionWebcasts = null;

  /**
   * Constructor for the Project object
   */
  public ProjectFeatures() {
  }


  /**
   * Constructor for the Project object
   *
   * @param rs Description of Parameter
   * @throws java.sql.SQLException Description of Exception
   */
  public ProjectFeatures(ResultSet rs) throws SQLException {
    buildRecord(rs);
  }

  public ProjectFeatures(Connection db, int thisProjectId) throws SQLException {
    queryRecord(db, thisProjectId);
  }


  /**
   * Description of the Method
   *
   * @param db            Description of the Parameter
   * @param thisProjectId Description of the Parameter
   * @throws java.sql.SQLException Description of the Exception
   */
  private void queryRecord(Connection db, int thisProjectId) throws SQLException {
    StringBuffer sql = new StringBuffer();
    sql.append(
        "SELECT * " +
            "FROM projects p " +
            "WHERE p.project_id = ? ");
    PreparedStatement pst = db.prepareStatement(sql.toString());
    int i = 0;
    pst.setInt(++i, thisProjectId);
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      buildRecord(rs);
    }
    rs.close();
    pst.close();
  }


  public void setId(int tmp) {
    this.id = tmp;
  }


  public void setId(String tmp) {
    this.id = Integer.parseInt(tmp);
  }

  public int getModifiedBy() {
    return modifiedBy;
  }

  public void setModifiedBy(int modifiedBy) {
    this.modifiedBy = modifiedBy;
  }

  public void setModifiedBy(String tmp) {
    this.modifiedBy = Integer.parseInt(tmp);
  }

  public Timestamp getModified() {
    return modified;
  }

  public void setModified(Timestamp modified) {
    this.modified = modified;
  }

  public void setModified(String tmp) {
    this.modified = DatabaseUtils.parseTimestamp(tmp);
  }

  /**
   * Sets the allowGuests attribute of the Project object
   *
   * @param tmp The new allowGuests value
   */
  public void setAllowGuests(boolean tmp) {
    this.allowGuests = tmp;
  }


  /**
   * Sets the allowGuests attribute of the Project object
   *
   * @param tmp The new allowGuests value
   */
  public void setAllowGuests(String tmp) {
    this.allowGuests = DatabaseUtils.parseBoolean(tmp);
  }


  /**
   * Sets the updateAllowGuests attribute of the Project object
   *
   * @param tmp The new updateAllowGuests value
   */
  public void setUpdateAllowGuests(boolean tmp) {
    this.updateAllowGuests = tmp;
  }

  public void setUpdateAllowGuests(String tmp) {
    this.updateAllowGuests = DatabaseUtils.parseBoolean(tmp);
  }

  public void setUpdateAllowParticipants(boolean tmp) {
    this.updateAllowParticipants = tmp;
  }

  public void setUpdateAllowParticipants(String tmp) {
    this.updateAllowParticipants = DatabaseUtils.parseBoolean(tmp);
  }


  public boolean getUpdateMembershipRequired() {
    return updateMembershipRequired;
  }

  public void setUpdateMembershipRequired(boolean updateMembershipRequired) {
    this.updateMembershipRequired = updateMembershipRequired;
  }

  public void setUpdateMembershipRequired(String updateMembershipRequired) {
    this.updateMembershipRequired = DatabaseUtils.parseBoolean(updateMembershipRequired);
  }

  public boolean getAllowParticipants() {
    return allowParticipants;
  }

  public boolean getUpdateAllowParticipants() {
    return updateAllowParticipants;
  }


  public void setAllowParticipants(boolean tmp) {
    this.allowParticipants = tmp;
  }

  public void setAllowParticipants(String tmp) {
    this.allowParticipants = DatabaseUtils.parseBoolean(tmp);
  }


  public boolean getMembershipRequired() {
    return membershipRequired;
  }

  public void setMembershipRequired(boolean membershipRequired) {
    this.membershipRequired = membershipRequired;
  }

  public void setMembershipRequired(String tmp) {
    this.membershipRequired = DatabaseUtils.parseBoolean(tmp);
  }

  public boolean getShowProfile() {
    return showProfile;
  }

  public void setShowProfile(boolean showProfile) {
    this.showProfile = showProfile;
  }

  public void setShowProfile(String showProfile) {
    this.showProfile = DatabaseUtils.parseBoolean(showProfile);
  }

  public String getLabelProfile() {
    return labelProfile;
  }

  public void setLabelProfile(String labelProfile) {
    this.labelProfile = labelProfile;
  }

  public int getOrderProfile() {
    return orderProfile;
  }

  public void setOrderProfile(int orderProfile) {
    this.orderProfile = orderProfile;
  }

  public void setOrderProfile(String orderProfile) {
    this.orderProfile = Integer.parseInt(orderProfile);
  }

  public String getDescriptionProfile() {
    return descriptionProfile;
  }

  public void setDescriptionProfile(String descriptionProfile) {
    this.descriptionProfile = descriptionProfile;
  }

  /**
   * Sets the showCalendar attribute of the Project object
   *
   * @param tmp The new showCalendar value
   */
  public void setShowCalendar(boolean tmp) {
    this.showCalendar = tmp;
  }


  /**
   * Sets the showCalendar attribute of the Project object
   *
   * @param tmp The new showCalendar value
   */
  public void setShowCalendar(String tmp) {
    this.showCalendar = DatabaseUtils.parseBoolean(tmp);
  }

  public void setShowWiki(boolean showWiki) {
    this.showWiki = showWiki;
  }

  public void setShowWiki(String tmp) {
    this.showWiki = DatabaseUtils.parseBoolean(tmp);
  }

  public void setShowDashboard(boolean showDashboard) {
    this.showDashboard = showDashboard;
  }

  public void setShowDashboard(String tmp) {
    this.showDashboard = DatabaseUtils.parseBoolean(tmp);
  }

  /**
   * Sets the showNews attribute of the Project object
   *
   * @param tmp The new showNews value
   */
  public void setShowNews(boolean tmp) {
    this.showNews = tmp;
  }

  public void setShowBlog(boolean tmp) {
    this.setShowNews(tmp);
  }


  /**
   * Sets the showNews attribute of the Project object
   *
   * @param tmp The new showNews value
   */
  public void setShowNews(String tmp) {
    this.showNews = DatabaseUtils.parseBoolean(tmp);
  }

  public boolean getShowWebcasts() {
    return showWebcasts;
  }

  public void setShowWebcasts(boolean showWebcasts) {
    this.showWebcasts = showWebcasts;
  }

  public void setShowWebcasts(String tmp) {
    this.showWebcasts = DatabaseUtils.parseBoolean(tmp);
  }

  public void setShowBlog(String tmp) {
    this.setShowNews(tmp);
  }


  /**
   * Sets the showDetails attribute of the Project object
   *
   * @param tmp The new showDetails value
   */
  public void setShowDetails(boolean tmp) {
    this.showDetails = tmp;
  }


  /**
   * Sets the showDetails attribute of the Project object
   *
   * @param tmp The new showDetails value
   */
  public void setShowDetails(String tmp) {
    this.showDetails = DatabaseUtils.parseBoolean(tmp);
  }


  /**
   * Sets the showTeam attribute of the Project object
   *
   * @param tmp The new showTeam value
   */
  public void setShowTeam(boolean tmp) {
    this.showTeam = tmp;
  }


  /**
   * Sets the showTeam attribute of the Project object
   *
   * @param tmp The new showTeam value
   */
  public void setShowTeam(String tmp) {
    this.showTeam = DatabaseUtils.parseBoolean(tmp);
  }


  /**
   * Sets the showPlan attribute of the Project object
   *
   * @param tmp The new showPlan value
   */
  public void setShowPlan(boolean tmp) {
    this.showPlan = tmp;
  }


  /**
   * Sets the showPlan attribute of the Project object
   *
   * @param tmp The new showPlan value
   */
  public void setShowPlan(String tmp) {
    this.showPlan = DatabaseUtils.parseBoolean(tmp);
  }


  /**
   * Sets the showLists attribute of the Project object
   *
   * @param tmp The new showLists value
   */
  public void setShowLists(boolean tmp) {
    this.showLists = tmp;
  }


  /**
   * Sets the showLists attribute of the Project object
   *
   * @param tmp The new showLists value
   */
  public void setShowLists(String tmp) {
    this.showLists = DatabaseUtils.parseBoolean(tmp);
  }


  /**
   * Sets the showDiscussion attribute of the Project object
   *
   * @param tmp The new showDiscussion value
   */
  public void setShowDiscussion(boolean tmp) {
    this.showDiscussion = tmp;
  }


  /**
   * Sets the showDiscussion attribute of the Project object
   *
   * @param tmp The new showDiscussion value
   */
  public void setShowDiscussion(String tmp) {
    this.showDiscussion = DatabaseUtils.parseBoolean(tmp);
  }


  /**
   * Sets the showTickets attribute of the Project object
   *
   * @param tmp The new showTickets value
   */
  public void setShowTickets(boolean tmp) {
    this.showTickets = tmp;
  }

  public void setShowIssues(boolean tmp) {
    this.setShowTickets(tmp);
  }


  /**
   * Sets the showTickets attribute of the Project object
   *
   * @param tmp The new showTickets value
   */
  public void setShowTickets(String tmp) {
    this.showTickets = DatabaseUtils.parseBoolean(tmp);
  }

  public void setShowIssues(String tmp) {
    this.setShowTickets(tmp);
  }


  /**
   * Sets the showDocuments attribute of the Project object
   *
   * @param tmp The new showDocuments value
   */
  public void setShowDocuments(boolean tmp) {
    this.showDocuments = tmp;
  }


  /**
   * Sets the showDocuments attribute of the Project object
   *
   * @param tmp The new showDocuments value
   */
  public void setShowDocuments(String tmp) {
    this.showDocuments = DatabaseUtils.parseBoolean(tmp);
  }


  /**
   * @return the showMessages
   */
  public boolean getShowMessages() {
    return showMessages;
  }


  /**
   * @param showMessages the showMessages to set
   */
  public void setShowMessages(boolean showMessages) {
    this.showMessages = showMessages;
  }


  public void setShowMessages(String showMessages) {
    this.showMessages = DatabaseUtils.parseBoolean(showMessages);
  }

  /**
   * Sets the labelCalendar attribute of the Project object
   *
   * @param tmp The new labelCalendar value
   */
  public void setLabelCalendar(String tmp) {
    this.labelCalendar = tmp;
  }

  public void setLabelWiki(String labelWiki) {
    this.labelWiki = labelWiki;
  }

  public void setLabelDashboard(String labelDashboard) {
    this.labelDashboard = labelDashboard;
  }

  /**
   * Sets the labelNews attribute of the Project object
   *
   * @param tmp The new labelNews value
   */
  public void setLabelNews(String tmp) {
    this.labelNews = tmp;
  }

  public void setLabelBlog(String tmp) {
    this.setLabelNews(tmp);
  }

  public String getLabelWebcasts() {
    return labelWebcasts;
  }

  public void setLabelWebcasts(String labelWebcasts) {
    this.labelWebcasts = labelWebcasts;
  }


  /**
   * Sets the labelDetails attribute of the Project object
   *
   * @param tmp The new labelDetails value
   */
  public void setLabelDetails(String tmp) {
    this.labelDetails = tmp;
  }


  /**
   * Sets the labelTeam attribute of the Project object
   *
   * @param tmp The new labelTeam value
   */
  public void setLabelTeam(String tmp) {
    this.labelTeam = tmp;
  }


  /**
   * Sets the labelPlan attribute of the Project object
   *
   * @param tmp The new labelPlan value
   */
  public void setLabelPlan(String tmp) {
    this.labelPlan = tmp;
  }


  /**
   * Sets the labelLists attribute of the Project object
   *
   * @param tmp The new labelLists value
   */
  public void setLabelLists(String tmp) {
    this.labelLists = tmp;
  }


  /**
   * Sets the labelDiscussion attribute of the Project object
   *
   * @param tmp The new labelDiscussion value
   */
  public void setLabelDiscussion(String tmp) {
    this.labelDiscussion = tmp;
  }


  /**
   * Sets the labelTickets attribute of the Project object
   *
   * @param tmp The new labelTickets value
   */
  public void setLabelTickets(String tmp) {
    this.labelTickets = tmp;
  }

  public void setLabelIssues(String tmp) {
    this.setLabelTickets(tmp);
  }


  /**
   * Sets the labelDocuments attribute of the Project object
   *
   * @param tmp The new labelDocuments value
   */
  public void setLabelDocuments(String tmp) {
    this.labelDocuments = tmp;
  }


  public int getOrderNews() {
    return orderNews;
  }

  public int getOrderBlog() {
    return this.getOrderNews();
  }

  public void setOrderNews(int orderNews) {
    this.orderNews = orderNews;
  }

  public void setOrderBlog(int orderNews) {
    this.setOrderNews(orderNews);
  }

  public void setOrderNews(String tmp) {
    this.orderNews = Integer.parseInt(tmp);
  }

  public void setOrderBlog(String tmp) {
    this.setOrderNews(tmp);
  }

  public int getOrderCalendar() {
    return orderCalendar;
  }

  public void setOrderCalendar(int orderCalendar) {
    this.orderCalendar = orderCalendar;
  }

  public void setOrderCalendar(String tmp) {
    this.orderCalendar = Integer.parseInt(tmp);
  }

  public int getOrderWiki() {
    return orderWiki;
  }

  public void setOrderWiki(int orderWiki) {
    this.orderWiki = orderWiki;
  }

  public void setOrderWiki(String tmp) {
    this.orderWiki = Integer.parseInt(tmp);
  }

  public int getOrderDashboard() {
    return orderDashboard;
  }

  public void setOrderDashboard(int orderDashboard) {
    this.orderDashboard = orderDashboard;
  }

  public void setOrderDashboard(String tmp) {
    this.orderDashboard = Integer.parseInt(tmp);
  }

  public int getOrderDiscussion() {
    return orderDiscussion;
  }

  public void setOrderDiscussion(int orderDiscussion) {
    this.orderDiscussion = orderDiscussion;
  }

  public void setOrderDiscussion(String tmp) {
    this.orderDiscussion = Integer.parseInt(tmp);
  }

  public int getOrderDocuments() {
    return orderDocuments;
  }

  public void setOrderDocuments(int orderDocuments) {
    this.orderDocuments = orderDocuments;
  }

  public void setOrderDocuments(String tmp) {
    this.orderDocuments = Integer.parseInt(tmp);
  }

  public int getOrderLists() {
    return orderLists;
  }

  public void setOrderLists(int orderLists) {
    this.orderLists = orderLists;
  }

  public void setOrderLists(String tmp) {
    this.orderLists = Integer.parseInt(tmp);
  }

  public int getOrderPlan() {
    return orderPlan;
  }

  public void setOrderPlan(int orderPlan) {
    this.orderPlan = orderPlan;
  }

  public void setOrderPlan(String tmp) {
    this.orderPlan = Integer.parseInt(tmp);
  }

  public int getOrderTickets() {
    return orderTickets;
  }

  public int getOrderIssues() {
    return this.getOrderTickets();
  }

  public void setOrderTickets(int orderTickets) {
    this.orderTickets = orderTickets;
  }

  public void setOrderIssues(int orderTickets) {
    this.setOrderTickets(orderTickets);
  }

  public void setOrderTickets(String tmp) {
    this.orderTickets = Integer.parseInt(tmp);
  }

  public void setOrderIssues(String tmp) {
    this.setOrderTickets(tmp);
  }

  public int getOrderTeam() {
    return orderTeam;
  }

  public void setOrderTeam(int orderTeam) {
    this.orderTeam = orderTeam;
  }

  public void setOrderTeam(String tmp) {
    this.orderTeam = Integer.parseInt(tmp);
  }

  public int getOrderDetails() {
    return orderDetails;
  }

  public void setOrderDetails(int orderDetails) {
    this.orderDetails = orderDetails;
  }

  public void setOrderDetails(String tmp) {
    this.orderDetails = Integer.parseInt(tmp);
  }

  /**
   * @return the orderMessages
   */
  public int getOrderMessages() {
    return orderMessages;
  }


  /**
   * @param orderMessages the orderMessages to set
   */
  public void setOrderMessages(int orderMessages) {
    this.orderMessages = orderMessages;
  }


  public void setOrderMessages(String orderMessages) {
    this.orderMessages = Integer.parseInt(orderMessages);
  }

  public int getOrderWebcasts() {
    return orderWebcasts;
  }

  public void setOrderWebcasts(int orderWebcasts) {
    this.orderWebcasts = orderWebcasts;
  }

  public void setOrderWebcasts(String orderWebcasts) {
    this.orderWebcasts = Integer.parseInt(orderWebcasts);
  }

  public int getId() {
    return id;
  }


  /**
   * Gets the allowGuests attribute of the Project object
   *
   * @return The allowGuests value
   */
  public boolean getAllowGuests() {
    return allowGuests;
  }


  /**
   * Gets the updateAllowGuests attribute of the Project object
   *
   * @return The updateAllowGuests value
   */
  public boolean getUpdateAllowGuests() {
    return updateAllowGuests;
  }


  /**
   * Gets the showCalendar attribute of the Project object
   *
   * @return The showCalendar value
   */
  public boolean getShowCalendar() {
    return showCalendar;
  }

  public boolean getShowWiki() {
    return showWiki;
  }


  public boolean getShowDashboard() {
    return showDashboard;
  }


  /**
   * Gets the showNews attribute of the Project object
   *
   * @return The showNews value
   */
  public boolean getShowNews() {
    return showNews;
  }

  public boolean getShowBlog() {
    return getShowNews();
  }


  /**
   * Gets the showDetails attribute of the Project object
   *
   * @return The showDetails value
   */
  public boolean getShowDetails() {
    return showDetails;
  }


  /**
   * Gets the showTeam attribute of the Project object
   *
   * @return The showTeam value
   */
  public boolean getShowTeam() {
    return showTeam;
  }


  public boolean getShowMembers() {
    return showTeam;
  }


  /**
   * Gets the showPlan attribute of the Project object
   *
   * @return The showPlan value
   */
  public boolean getShowPlan() {
    return showPlan;
  }


  /**
   * Gets the showLists attribute of the Project object
   *
   * @return The showLists value
   */
  public boolean getShowLists() {
    return showLists;
  }


  /**
   * Gets the showDiscussion attribute of the Project object
   *
   * @return The showDiscussion value
   */
  public boolean getShowDiscussion() {
    return showDiscussion;
  }


  /**
   * Gets the showTickets attribute of the Project object
   *
   * @return The showTickets value
   */
  public boolean getShowTickets() {
    return showTickets;
  }

  public boolean getShowIssues() {
    return getShowTickets();
  }


  /**
   * Gets the showDocuments attribute of the Project object
   *
   * @return The showDocuments value
   */
  public boolean getShowDocuments() {
    return showDocuments;
  }


  /**
   * Gets the labelNews attribute of the Project object
   *
   * @return The labelNews value
   */
  public String getLabelNews() {
    return labelNews;
  }

  public String getLabelBlog() {
    return getLabelNews();
  }


  /**
   * Gets the labelDetails attribute of the Project object
   *
   * @return The labelDetails value
   */
  public String getLabelDetails() {
    return labelDetails;
  }


  /**
   * Gets the labelTeam attribute of the Project object
   *
   * @return The labelTeam value
   */
  public String getLabelTeam() {
    return labelTeam;
  }


  /**
   * Gets the labelPlan attribute of the Project object
   *
   * @return The labelPlan value
   */
  public String getLabelPlan() {
    return labelPlan;
  }


  /**
   * Gets the labelLists attribute of the Project object
   *
   * @return The labelLists value
   */
  public String getLabelLists() {
    return labelLists;
  }


  /**
   * Gets the labelDiscussion attribute of the Project object
   *
   * @return The labelDiscussion value
   */
  public String getLabelDiscussion() {
    return labelDiscussion;
  }


  /**
   * Gets the labelTickets attribute of the Project object
   *
   * @return The labelTickets value
   */
  public String getLabelTickets() {
    return labelTickets;
  }

  public String getLabelIssues() {
    return getLabelTickets();
  }

  public String getLabelWiki() {
    return labelWiki;
  }

  public String getLabelDashboard() {
    return labelDashboard;
  }

  public String getLabelCalendar() {
    return labelCalendar;
  }

  /**
   * Gets the labelDocuments attribute of the Project object
   *
   * @return The labelDocuments value
   */
  public String getLabelDocuments() {
    return labelDocuments;
  }

  /**
   * @return the labelMessages
   */
  public String getLabelMessages() {
    return labelMessages;
  }


  /**
   * @param labelMessages the labelMessages to set
   */
  public void setLabelMessages(String labelMessages) {
    this.labelMessages = labelMessages;
  }


  public String getDescriptionDashboard() {
    return descriptionDashboard;
  }

  public void setDescriptionDashboard(String descriptionDashboard) {
    this.descriptionDashboard = descriptionDashboard;
  }

  public String getDescriptionNews() {
    return descriptionNews;
  }

  public String getDescriptionBlog() {
    return this.getDescriptionNews();
  }

  public void setDescriptionNews(String descriptionNews) {
    this.descriptionNews = descriptionNews;
  }

  public void setDescriptionBlog(String descriptionNews) {
    this.setDescriptionNews(descriptionNews);
  }

  public String getDescriptionCalendar() {
    return descriptionCalendar;
  }

  public void setDescriptionCalendar(String descriptionCalendar) {
    this.descriptionCalendar = descriptionCalendar;
  }

  public String getDescriptionWiki() {
    return descriptionWiki;
  }

  public void setDescriptionWiki(String descriptionWiki) {
    this.descriptionWiki = descriptionWiki;
  }

  public String getDescriptionDiscussion() {
    return descriptionDiscussion;
  }

  public void setDescriptionDiscussion(String descriptionDiscussion) {
    this.descriptionDiscussion = descriptionDiscussion;
  }

  public String getDescriptionDocuments() {
    return descriptionDocuments;
  }

  public void setDescriptionDocuments(String descriptionDocuments) {
    this.descriptionDocuments = descriptionDocuments;
  }

  public String getDescriptionLists() {
    return descriptionLists;
  }

  public void setDescriptionLists(String descriptionLists) {
    this.descriptionLists = descriptionLists;
  }

  public String getDescriptionPlan() {
    return descriptionPlan;
  }

  public void setDescriptionPlan(String descriptionPlan) {
    this.descriptionPlan = descriptionPlan;
  }

  public String getDescriptionTickets() {
    return descriptionTickets;
  }

  public String getDescriptionIssues() {
    return getDescriptionTickets();
  }

  public void setDescriptionTickets(String descriptionTickets) {
    this.descriptionTickets = descriptionTickets;
  }

  public void setDescriptionIssues(String descriptionTickets) {
    this.setDescriptionTickets(descriptionTickets);
  }

  public String getDescriptionTeam() {
    return descriptionTeam;
  }

  public void setDescriptionTeam(String descriptionTeam) {
    this.descriptionTeam = descriptionTeam;
  }

  public boolean getShowBadges() {
    return showBadges;
  }

  public void setShowBadges(boolean showBadges) {
    this.showBadges = showBadges;
  }

  public void setShowBadges(String tmp) {
    this.showBadges = DatabaseUtils.parseBoolean(tmp);
  }

  public boolean getShowReviews() {
    return showReviews;
  }

  public void setShowReviews(boolean showReviews) {
    this.showReviews = showReviews;
  }

  public void setShowReviews(String tmp) {
    this.showReviews = DatabaseUtils.parseBoolean(tmp);
  }

  public boolean getShowClassifieds() {
    return showClassifieds;
  }

  public void setShowClassifieds(boolean showClassifieds) {
    this.showClassifieds = showClassifieds;
  }

  public void setShowClassifieds(String tmp) {
    this.showClassifieds = DatabaseUtils.parseBoolean(tmp);
  }

  public boolean getShowAds() {
    return showAds;
  }

  public boolean getShowPromotions() {
    return getShowAds();
  }

  public void setShowAds(boolean showAds) {
    this.showAds = showAds;
  }

  public void setShowPromotions(boolean showAds) {
    this.setShowAds(showAds);
  }

  public void setShowAds(String tmp) {
    this.showAds = DatabaseUtils.parseBoolean(tmp);
  }

  public void setShowPromotions(String tmp) {
    this.setShowAds(tmp);
  }

  public String getLabelBadges() {
    return labelBadges;
  }

  public void setLabelBadges(String labelBadges) {
    this.labelBadges = labelBadges;
  }

  public String getLabelReviews() {
    return labelReviews;
  }

  public void setLabelReviews(String labelReviews) {
    this.labelReviews = labelReviews;
  }

  public String getLabelClassifieds() {
    return labelClassifieds;
  }

  public void setLabelClassifieds(String labelClassifieds) {
    this.labelClassifieds = labelClassifieds;
  }

  public String getLabelAds() {
    return labelAds;
  }

  public String getLabelPromotions() {
    return getLabelAds();
  }

  public void setLabelAds(String labelAds) {
    this.labelAds = labelAds;
  }

  public void setLabelPromotions(String labelAds) {
    this.setLabelAds(labelAds);
  }

  public int getOrderBadges() {
    return orderBadges;
  }

  public void setOrderBadges(int orderBadges) {
    this.orderBadges = orderBadges;
  }

  public void setOrderBadges(String tmp) {
    this.orderBadges = Integer.parseInt(tmp);
  }

  public int getOrderReviews() {
    return orderReviews;
  }

  public void setOrderReviews(int orderReviews) {
    this.orderReviews = orderReviews;
  }

  public void setOrderReviews(String tmp) {
    this.orderReviews = Integer.parseInt(tmp);
  }

  public int getOrderClassifieds() {
    return orderClassifieds;
  }

  public void setOrderClassifieds(int orderClassifieds) {
    this.orderClassifieds = orderClassifieds;
  }

  public void setOrderClassifieds(String tmp) {
    this.orderClassifieds = Integer.parseInt(tmp);
  }

  public int getOrderAds() {
    return orderAds;
  }

  public int getOrderPromotions() {
    return getOrderAds();
  }

  public void setOrderAds(int orderAds) {
    this.orderAds = orderAds;
  }

  public void setOrderPromotions(int orderAds) {
    this.setOrderAds(orderAds);
  }

  public void setOrderAds(String tmp) {
    this.orderAds = Integer.parseInt(tmp);
  }

  public void setOrderPromotions(String tmp) {
    this.setOrderAds(tmp);
  }

  public String getDescriptionBadges() {
    return descriptionBadges;
  }

  public void setDescriptionBadges(String descriptionBadges) {
    this.descriptionBadges = descriptionBadges;
  }

  public String getDescriptionReviews() {
    return descriptionReviews;
  }

  public void setDescriptionReviews(String descriptionReviews) {
    this.descriptionReviews = descriptionReviews;
  }

  public String getDescriptionClassifieds() {
    return descriptionClassifieds;
  }

  public void setDescriptionClassifieds(String descriptionClassifieds) {
    this.descriptionClassifieds = descriptionClassifieds;
  }

  public String getDescriptionAds() {
    return descriptionAds;
  }

  public String getDescriptionPromotions() {
    return getDescriptionAds();
  }

  public void setDescriptionAds(String descriptionAds) {
    this.descriptionAds = descriptionAds;
  }

  public void setDescriptionPromotions(String descriptionAds) {
    setDescriptionAds(descriptionAds);
  }

  /**
   * @return the descriptionMessages
   */
  public String getDescriptionMessages() {
    return descriptionMessages;
  }


  /**
   * @param descriptionMessages the descriptionMessages to set
   */
  public void setDescriptionMessages(String descriptionMessages) {
    this.descriptionMessages = descriptionMessages;
  }

  public String getDescriptionWebcasts() {
    return descriptionWebcasts;
  }

  public void setDescriptionWebcasts(String descriptionWebcasts) {
    this.descriptionWebcasts = descriptionWebcasts;
  }

  /**
   * Updates the features of a project
   *
   * @param db Description of the Parameter
   * @return Description of the Return Value
   * @throws java.sql.SQLException Description of the Exception
   */
  public int update(Connection db) throws SQLException {
    if (id == -1) {
      throw new SQLException("ProjectId was not specified");
    }
    int resultCount = 0;
    boolean commit = db.getAutoCommit();
    try {
      if (commit) {
        db.setAutoCommit(false);
      }
      PreparedStatement pst = db.prepareStatement(
          "UPDATE projects " +
              "SET " +
              (updateAllowGuests ? "allow_guests = ?, " : "") +
              (updateAllowParticipants ? "allows_user_observers = ?, " : "") +
              (updateMembershipRequired ? "membership_required = ?, " : "") +
              "calendar_enabled = ?, dashboard_enabled = ?, news_enabled = ?, details_enabled = ?, " +
              "team_enabled = ?, plan_enabled = ?, lists_enabled = ?, discussion_enabled = ?, wiki_enabled = ?, " +
              "tickets_enabled = ?, documents_enabled = ?, " +
              "calendar_label = ?, dashboard_label = ?, news_label = ?, details_label = ?, team_label = ?, plan_label = ?, lists_label = ?, " +
              "discussion_label = ?, wiki_label = ?, tickets_label = ?, documents_label = ?, " +
              "dashboard_order = ?, calendar_order = ?, news_order = ?, details_order = ?, team_order = ?, plan_order = ?, lists_order = ?, " +
              "discussion_order = ?, wiki_order = ?, tickets_order = ?, documents_order = ?, " +
              "badges_enabled = ?, badges_label = ?, badges_order = ?, badges_description = ?, " +
              "reviews_enabled = ?, reviews_label = ?, reviews_order = ?, reviews_description = ?, " +
              "classifieds_enabled = ?, classifieds_label = ?, classifieds_order = ?, classifieds_description = ?, " +
              "ads_enabled = ?, ads_label = ?, ads_order = ?, ads_description = ?, " +
              "profile_enabled = ?, profile_label = ?, profile_order = ?, profile_description = ?, " +
              "messages_enabled = ?, messages_label = ?, messages_order = ?, messages_description = ?, " +
              "webcasts_enabled = ?, webcasts_label = ?, webcasts_order = ?, webcasts_description = ?, " +
              "modifiedby = ?, modified = CURRENT_TIMESTAMP " +
              "WHERE project_id = ? ");
      int i = 0;
      if (updateAllowGuests) {
        pst.setBoolean(++i, allowGuests);
      }
      if (updateAllowParticipants) {
        pst.setBoolean(++i, allowParticipants);
      }
      if (updateMembershipRequired) {
        pst.setBoolean(++i, membershipRequired);
      }
      pst.setBoolean(++i, showCalendar);
      pst.setBoolean(++i, showDashboard);
      pst.setBoolean(++i, showNews);
      pst.setBoolean(++i, showDetails);
      pst.setBoolean(++i, showTeam);
      pst.setBoolean(++i, showPlan);
      pst.setBoolean(++i, showLists);
      pst.setBoolean(++i, showDiscussion);
      pst.setBoolean(++i, showWiki);
      pst.setBoolean(++i, showTickets);
      pst.setBoolean(++i, showDocuments);
      pst.setString(++i, labelCalendar);
      pst.setString(++i, labelDashboard);
      pst.setString(++i, labelNews);
      pst.setString(++i, labelDetails);
      pst.setString(++i, labelTeam);
      pst.setString(++i, labelPlan);
      pst.setString(++i, labelLists);
      pst.setString(++i, labelDiscussion);
      pst.setString(++i, labelWiki);
      pst.setString(++i, labelTickets);
      pst.setString(++i, labelDocuments);
      pst.setInt(++i, orderDashboard);
      pst.setInt(++i, orderCalendar);
      pst.setInt(++i, orderNews);
      pst.setInt(++i, orderDetails);
      pst.setInt(++i, orderTeam);
      pst.setInt(++i, orderPlan);
      pst.setInt(++i, orderLists);
      pst.setInt(++i, orderDiscussion);
      pst.setInt(++i, orderWiki);
      pst.setInt(++i, orderTickets);
      pst.setInt(++i, orderDocuments);
      pst.setBoolean(++i, showBadges);
      pst.setString(++i, labelBadges);
      pst.setInt(++i, orderBadges);
      pst.setString(++i, descriptionBadges);
      pst.setBoolean(++i, showReviews);
      pst.setString(++i, labelReviews);
      pst.setInt(++i, orderReviews);
      pst.setString(++i, descriptionReviews);
      pst.setBoolean(++i, showClassifieds);
      pst.setString(++i, labelClassifieds);
      pst.setInt(++i, orderClassifieds);
      pst.setString(++i, descriptionClassifieds);
      pst.setBoolean(++i, showAds);
      pst.setString(++i, labelAds);
      pst.setInt(++i, orderAds);
      pst.setString(++i, descriptionAds);
      pst.setBoolean(++i, showProfile);
      pst.setString(++i, labelProfile);
      pst.setInt(++i, orderProfile);
      pst.setString(++i, descriptionProfile);
      pst.setBoolean(++i, showMessages);
      pst.setString(++i, labelMessages);
      pst.setInt(++i, orderMessages);
      pst.setString(++i, descriptionMessages);
      pst.setBoolean(++i, showWebcasts);
      pst.setString(++i, labelWebcasts);
      pst.setInt(++i, orderWebcasts);
      pst.setString(++i, descriptionWebcasts);
      pst.setInt(++i, modifiedBy);
      pst.setInt(++i, id);
      resultCount = pst.executeUpdate();
      pst.close();
      if (commit) {
        db.commit();
      }
      CacheUtils.invalidateValue(Constants.SYSTEM_PROJECT_CACHE, id);
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
   * @param rs Description of Parameter
   * @throws java.sql.SQLException Description of Exception
   */
  private void buildRecord(ResultSet rs) throws SQLException {
    allowGuests = rs.getBoolean("allow_guests");
    showNews = rs.getBoolean("news_enabled");
    showDetails = rs.getBoolean("details_enabled");
    showTeam = rs.getBoolean("team_enabled");
    showPlan = rs.getBoolean("plan_enabled");
    showLists = rs.getBoolean("lists_enabled");
    showDiscussion = rs.getBoolean("discussion_enabled");
    showTickets = rs.getBoolean("tickets_enabled");
    showDocuments = rs.getBoolean("documents_enabled");
    labelNews = rs.getString("news_label");
    labelDetails = rs.getString("details_label");
    labelTeam = rs.getString("team_label");
    labelPlan = rs.getString("plan_label");
    labelLists = rs.getString("lists_label");
    labelDiscussion = rs.getString("discussion_label");
    labelTickets = rs.getString("tickets_label");
    labelDocuments = rs.getString("documents_label");
    allowParticipants = rs.getBoolean("allows_user_observers");
    showCalendar = rs.getBoolean("calendar_enabled");
    labelCalendar = rs.getString("calendar_label");
    showWiki = rs.getBoolean("wiki_enabled");
    labelWiki = rs.getString("wiki_label");
    orderDashboard = rs.getInt("dashboard_order");
    orderNews = rs.getInt("news_order");
    orderCalendar = rs.getInt("calendar_order");
    orderWiki = rs.getInt("wiki_order");
    orderDiscussion = rs.getInt("discussion_order");
    orderDocuments = rs.getInt("documents_order");
    orderLists = rs.getInt("lists_order");
    orderPlan = rs.getInt("plan_order");
    orderTickets = rs.getInt("tickets_order");
    orderTeam = rs.getInt("team_order");
    orderDetails = rs.getInt("details_order");
    showDashboard = rs.getBoolean("dashboard_enabled");
    labelDashboard = rs.getString("dashboard_label");
    descriptionDashboard = rs.getString("dashboard_description");
    descriptionNews = rs.getString("news_description");
    descriptionCalendar = rs.getString("calendar_description");
    descriptionWiki = rs.getString("wiki_description");
    descriptionDiscussion = rs.getString("discussion_description");
    descriptionDocuments = rs.getString("documents_description");
    descriptionLists = rs.getString("lists_description");
    descriptionPlan = rs.getString("plan_description");
    descriptionTickets = rs.getString("tickets_description");
    descriptionTeam = rs.getString("team_description");
    showBadges = rs.getBoolean("badges_enabled");
    labelBadges = rs.getString("badges_label");
    orderBadges = rs.getInt("badges_order");
    descriptionBadges = rs.getString("badges_description");
    showReviews = rs.getBoolean("reviews_enabled");
    labelReviews = rs.getString("reviews_label");
    orderReviews = rs.getInt("reviews_order");
    descriptionReviews = rs.getString("reviews_description");
    showClassifieds = rs.getBoolean("classifieds_enabled");
    labelClassifieds = rs.getString("classifieds_label");
    orderClassifieds = rs.getInt("classifieds_order");
    descriptionClassifieds = rs.getString("classifieds_description");
    showAds = rs.getBoolean("ads_enabled");
    labelAds = rs.getString("ads_label");
    orderAds = rs.getInt("ads_order");
    descriptionAds = rs.getString("ads_description");
    membershipRequired = rs.getBoolean("membership_required");
    showProfile = rs.getBoolean("profile_enabled");
    labelProfile = rs.getString("profile_label");
    orderProfile = rs.getInt("profile_order");
    descriptionProfile = rs.getString("profile_description");
    showMessages = rs.getBoolean("messages_enabled");
    labelMessages = rs.getString("messages_label");
    orderMessages = rs.getInt("messages_order");
    descriptionMessages = rs.getString("messages_description");
    showWebcasts = rs.getBoolean("webcasts_enabled");
    labelWebcasts = rs.getString("webcasts_label");
    orderWebcasts = rs.getInt("webcasts_order");
    descriptionWebcasts = rs.getString("webcasts_description");
    // NOTE: update the project buildRecord as well
  }

}