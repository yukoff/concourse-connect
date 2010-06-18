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
package com.concursive.connect.web.modules.profile.beans;

import com.concursive.commons.db.DatabaseUtils;
import com.concursive.commons.web.mvc.beans.GenericBean;
import com.concursive.connect.Constants;
import com.concursive.connect.cache.utils.CacheUtils;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Represents a Project Claim Form
 *
 * @author lorraine bittner
 * @version $Id$
 * @created July 29, 2008
 */
public class ProjectFormBean extends GenericBean {
  private String source = null;
  private String firstName = null;
  private String lastName = null;

  private int projectId = -1;
  private int userId = -1;
  private String projectTitle = null;
  private String company = null;
  private String email = null;
  private String addressTo = null;
  private String addressLine1 = null;
  private String addressLine2 = null;
  private String addressLine3 = null;
  private String city = null;
  private String state = null;
  private String postalCode = null;
  private String country = null;
  private String webPage = null;
  private String twitterId = null;
  private String facebookPage = null;
  private String youtubeChannelId = null;
  // Live Video properties
  private String ustreamId = null;
  private String livestreamId = null;
  private String justintvId = null;
  private String qikId = null;
  private String phone = null;
  private String fax = null;
  private String comments = null;
  private String uniqueId = null;
  private boolean isOwner = false;

  private int subCategory1Id = -1;
  private String keywords = null;
  private String shortDescription = null;
  private String description = null;
  private Timestamp requestDate = null;
  private Timestamp estimatedCloseDate = null;

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public int getProjectId() {
    return projectId;
  }

  public void setProjectId(int projectId) {
    this.projectId = projectId;
  }

  public void setProjectId(String tmp) {
    this.projectId = Integer.parseInt(tmp);
  }

  public int getUserId() {
    return userId;
  }

  public void setUserId(int userId) {
    this.userId = userId;
  }

  public void setUserId(String tmp) {
    this.userId = Integer.parseInt(tmp);
  }

  public String getProjectTitle() {
    return projectTitle;
  }

  public void setProjectTitle(String projectTitle) {
    this.projectTitle = projectTitle;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getCompany() {
    return company;
  }

  public void setCompany(String company) {
    this.company = company;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getAddressTo() {
    return addressTo;
  }

  public void setAddressTo(String addressTo) {
    this.addressTo = addressTo;
  }

  public String getAddressLine1() {
    return addressLine1;
  }

  public void setAddressLine1(String addressLine1) {
    this.addressLine1 = addressLine1;
  }

  public String getAddressLine2() {
    return addressLine2;
  }

  public void setAddressLine2(String addressLine2) {
    this.addressLine2 = addressLine2;
  }

  public String getAddressLine3() {
    return addressLine3;
  }

  public void setAddressLine3(String addressLine3) {
    this.addressLine3 = addressLine3;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public String getPostalCode() {
    return postalCode;
  }

  public void setPostalCode(String postalCode) {
    this.postalCode = postalCode;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public String getWebPage() {
    return webPage;
  }

  public void setWebPage(String webPage) {
    this.webPage = webPage;
  }

  /**
	 * @param twitterId the twitterId to set
	 */
	public void setTwitterId(String twitterId) {
		this.twitterId = twitterId;
	}

	/**
	 * @return the twitterId
	 */
	public String getTwitterId() {
		return twitterId;
	}

  public String getFacebookPage() {
    return facebookPage;
  }

  public void setFacebookPage(String facebookPage) {
    this.facebookPage = facebookPage;
  }

  public String getYoutubeChannelId() {
    return youtubeChannelId;
  }

  public void setYoutubeChannelId(String youtubeChannelId) {
    this.youtubeChannelId = youtubeChannelId;
  }

  public String getUstreamId() {
    return ustreamId;
  }

  public void setUstreamId(String ustreamId) {
    this.ustreamId = ustreamId;
  }

  public String getLivestreamId() {
    return livestreamId;
  }

  public void setLivestreamId(String livestreamId) {
    this.livestreamId = livestreamId;
  }

  public String getJustintvId() {
    return justintvId;
  }

  public void setJustintvId(String justintvId) {
    this.justintvId = justintvId;
  }

  public String getQikId() {
    return qikId;
  }

  public void setQikId(String qikId) {
    this.qikId = qikId;
  }

	public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getFax() {
    return fax;
  }

  public void setFax(String fax) {
    this.fax = fax;
  }

  public String getComments() {
    return comments;
  }

  public void setComments(String comments) {
    this.comments = comments;
  }

  public String getUniqueId() {
    return uniqueId;
  }

  public void setUniqueId(String uniqueId) {
    this.uniqueId = uniqueId;
  }

  public boolean isOwner() {
    return isOwner;
  }

  public boolean getIsOwner() {
    return isOwner;
  }

  public void setIsOwner(boolean owner) {
    isOwner = owner;
  }

  public void setIsOwner(String owner) {
    isOwner = DatabaseUtils.parseBoolean(owner);
  }

  /**
   * @return the subCategory1Id
   */
  public int getSubCategory1Id() {
    return subCategory1Id;
  }

  /**
   * @param subCategory1Id the subCategory1Id to set
   */
  public void setSubCategory1Id(int subCategory1Id) {
    this.subCategory1Id = subCategory1Id;
  }

  public void setSubCategory1Id(String subCategory1Id) {
    this.subCategory1Id = Integer.parseInt(subCategory1Id);
  }

  /**
   * @return the keywords
   */
  public String getKeywords() {
    return keywords;
  }

  /**
   * @param keywords the keywords to set
   */
  public void setKeywords(String keywords) {
    this.keywords = keywords;
  }

  /**
   * @return the shortDescription
   */
  public String getShortDescription() {
    return shortDescription;
  }

  /**
   * @param shortDescription the shortDescription to set
   */
  public void setShortDescription(String shortDescription) {
    this.shortDescription = shortDescription;
  }


  /**
   * @return the description
   */
  public String getDescription() {
    return description;
  }

  /**
   * @param description the description to set
   */
  public void setDescription(String description) {
    this.description = description;
  }

  public java.sql.Timestamp getRequestDate() {
    return requestDate;
  }

  /**
   * Sets the RequestDate attribute of the Project object
   *
   * @param tmp The new RequestDate value
   */
  public void setRequestDate(java.sql.Timestamp tmp) {
    this.requestDate = tmp;
  }


  /**
   * Sets the requestDate attribute of the Project object
   *
   * @param tmp The new requestDate value
   */
  public void setRequestDate(String tmp) {
    requestDate = DatabaseUtils.parseDateToTimestamp(tmp);
  }

  public Timestamp getEstimatedCloseDate() {
    return estimatedCloseDate;
  }

  /**
   * Sets the estimatedCloseDate attribute of the Project object
   *
   * @param tmp The new estimatedCloseDate value
   */
  public void setEstimatedCloseDate(Timestamp tmp) {
    this.estimatedCloseDate = tmp;
  }


  /**
   * Sets the estimatedCloseDate attribute of the Project object
   *
   * @param tmp The new estimatedCloseDate value
   */
  public void setEstimatedCloseDate(String tmp) {
    this.estimatedCloseDate = DatabaseUtils.parseTimestamp(tmp);
  }

  public Project getProject() {
    if (projectId > -1) {
      return ProjectUtils.loadProject(projectId);
    } else {
      return null;
    }
  }


  public int saveProjectOwner(Connection db) throws SQLException {
    if (userId <= 0) {
      throw new SQLException("User id is required");
    } else if (projectId <= 0) {
      throw new SQLException("Project id is required");
    }
    Project project = ProjectUtils.loadProject(projectId);
    if (project.getOwner() != -1) {
      throw new SQLException("Project owner with id [" + project.getOwner() + "] already is saved");
    }
    int i = 0;
    PreparedStatement pst = db.prepareStatement(
        "UPDATE projects SET owner = ? WHERE project_id = ? AND owner IS NULL");
    pst.setInt(++i, userId);
    pst.setInt(++i, project.getId());
    int resultCount = pst.executeUpdate();
    pst.close();
    if (resultCount == 1) {
      CacheUtils.invalidateValue(Constants.SYSTEM_PROJECT_CACHE, project.getId());
    }
    return resultCount;
  }

  public void buildBeanFromProject(Project project) {
    this.projectId = project.getId();
    this.projectTitle = project.getTitle();
    this.uniqueId = project.getUniqueId();
    this.addressTo = project.getAddressTo();
    this.addressLine1 = project.getAddressLine1();
    this.addressLine2 = project.getAddressLine2();
    this.addressLine3 = project.getAddressLine3();
    this.requestDate = project.getRequestDate();
    this.estimatedCloseDate = project.getEstimatedCloseDate();
    this.subCategory1Id = project.getSubCategory1Id();
    this.city = project.getCity();
    this.state = project.getState();
    this.country = project.getCountry();
    this.postalCode = project.getPostalCode();
    this.webPage = project.getWebPage();
    this.twitterId=project.getTwitterId();
    this.facebookPage = project.getFacebookPage();
    this.youtubeChannelId = project.getYoutubeChannelId();
    this.ustreamId = project.getUstreamId();
    this.livestreamId = project.getLivestreamId();
    this.justintvId = project.getJustintvId();
    this.qikId = project.getQikId();
    this.phone = project.getBusinessPhone();
    this.fax = project.getBusinessFax();
    this.email = project.getEmail1();
    this.keywords = project.getKeywords();
    this.shortDescription = project.getShortDescription();
    this.description = project.getDescription();
  }

  public void populateProjectFromBean(Project project) {
    project.setTitle(this.projectTitle);
    project.setAddressTo(this.addressTo);
    project.setAddressLine1(this.addressLine1);
    project.setAddressLine2(this.addressLine2);
    project.setAddressLine3(this.addressLine3);
    project.setRequestDate(this.requestDate);
    project.setEstimatedCloseDate(this.estimatedCloseDate);
    project.setSubCategory1Id(this.subCategory1Id);
    project.setCity(this.city);
    project.setState(this.state);
    project.setCountry(this.country);
    project.setPostalCode(this.postalCode);
    project.setWebPage(this.webPage);
    project.setTwitterId(this.twitterId);
    project.setFacebookPage(this.facebookPage);
    project.setYoutubeChannelId(this.youtubeChannelId);
    project.setUstreamId(this.ustreamId);
    project.setLivestreamId(this.livestreamId);
    project.setJustintvId(this.justintvId);
    project.setQikId(this.qikId);
    project.setBusinessPhone(this.phone);
    project.setBusinessFax(this.fax);
    project.setEmail1(this.email);
    project.setKeywords(this.keywords);
    project.setShortDescription(this.shortDescription);
    project.setDescription(this.description);
  }

  public void populateProjectFromBeanBasedOnPreferences(HashMap<String, String> preferenceMap, Project project) {
    if (preferenceMap.get("title") != null) {
      project.setTitle(this.projectTitle);
    }
    if (preferenceMap.get("addressTo") != null) {
      project.setAddressTo(this.addressTo);
    }
    if (preferenceMap.get("addressLine1") != null) {
      project.setAddressLine1(this.addressLine1);
    }
    if (preferenceMap.get("addressLine2") != null) {
      project.setAddressLine2(this.addressLine2);
    }
    if (preferenceMap.get("addressLine3") != null) {
      project.setAddressLine3(this.addressLine3);
    }
    if (preferenceMap.get("requestDate") != null) {
      project.setRequestDate(this.requestDate);
    }
    if (preferenceMap.get("estimatedCloseDate") != null) {
      project.setEstimatedCloseDate(this.estimatedCloseDate);
    }
    if (preferenceMap.get("subCategory1") != null) {
      project.setSubCategory1Id(this.subCategory1Id);
    }
    if (preferenceMap.get("city") != null) {
      project.setCity(this.city);
    }
    if (preferenceMap.get("state") != null) {
      project.setState(this.state);
    }
    if (preferenceMap.get("country") != null) {
      project.setCountry(this.country);
    }
    if (preferenceMap.get("postalCode") != null) {
      project.setPostalCode(this.postalCode);
    }
    if (preferenceMap.get("webPage") != null) {
      project.setWebPage(this.webPage);
    }
    if (preferenceMap.get("twitterId") != null) {
      project.setTwitterId(this.twitterId);  
    }
    if (preferenceMap.get("facebookPage") != null) {
      project.setFacebookPage(this.facebookPage);
    }
    if (preferenceMap.get("youtubeChannelId") != null) {
      project.setYoutubeChannelId(this.youtubeChannelId);
    }
    if (preferenceMap.get("ustreamId") != null) {
      project.setUstreamId(this.ustreamId);
    }
    if (preferenceMap.get("livestreamId") != null) {
      project.setLivestreamId(this.livestreamId);
    }
    if (preferenceMap.get("justintvId") != null) {
      project.setJustintvId(this.justintvId);
    }
    if (preferenceMap.get("qikId") != null) {
      project.setQikId(this.qikId);
    }    
    if (preferenceMap.get("businessPhone") != null) {
      project.setBusinessPhone(this.phone);
    }
    if (preferenceMap.get("businessFax") != null) {
      project.setBusinessFax(this.fax);
    }
    if (preferenceMap.get("email1") != null) {
      project.setEmail1(this.email);
    }
    if (preferenceMap.get("keywords") != null) {
      project.setKeywords(this.keywords);
    }
    if (preferenceMap.get("shortDescription") != null) {
      project.setShortDescription(this.shortDescription);
    }
    if (preferenceMap.get("longDescription") != null) {
      project.setDescription(this.description);
    }
  }

  public static ArrayList getTimeZoneParams() {
    ArrayList thisList = new ArrayList();
    thisList.add("requestDate");
    thisList.add("estimatedCloseDate");
    return thisList;
  }
}
