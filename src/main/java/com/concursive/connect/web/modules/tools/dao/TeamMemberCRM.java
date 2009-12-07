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

package com.concursive.connect.web.modules.tools.dao;
import com.concursive.connect.web.modules.api.beans.TransactionItem;
import com.concursive.connect.web.modules.api.services.CustomActionHandler;
import com.concursive.commons.db.DatabaseUtils;
import com.concursive.commons.web.mvc.beans.GenericBean;
import com.concursive.connect.Constants;
import com.concursive.connect.cache.utils.CacheUtils;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.login.utils.UserUtils;
import com.concursive.connect.web.modules.members.dao.TeamMember;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import com.concursive.connect.web.utils.LookupList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.HashMap;

import org.aspcfs.utils.StringUtils;

/**
 * Represents a member of a project
 *
 * @author Kailash Bhoopalam
 * @version $Id: TeamMemberCRM.java $
 * @created September 30, 2009
 */
//public class TeamMemberCRM implements CustomActionHandler {
public class TeamMemberCRM implements CustomActionHandler {

  // Team Member Properties
  private String projectProfileId = null;
  private String userProfileId = null;
  private String teamMemberRoleName = null;
  private int modifiedBy = -1;
  private String claimStatus = null;

  public TeamMemberCRM() {
  }

  /**
   * @return the projectProfileId
   */
  public String getProjectProfileId() {
  	return projectProfileId;
  }

	/**
   * @param projectProfileId the projectProfileId to set
   */
  public void setProjectProfileId(String projectProfileId) {
  	this.projectProfileId = projectProfileId;
  }

	/**
   * @return the userProfileId
   */
  public String getUserProfileId() {
  	return userProfileId;
  }

	/**
   * @param userProfileId the userProfileId to set
   */
  public void setUserProfileId(String userProfileId) {
  	this.userProfileId = userProfileId;
  }

	/**
   * @return the teamMemberRoleName
   */
  public String getTeamMemberRoleName() {
  	return teamMemberRoleName;
  }

	/**
   * @param teamMemberRoleName the teamMemberRoleName to set
   */
  public void setTeamMemberRoleName(String teamMemberRoleName) {
  	this.teamMemberRoleName = teamMemberRoleName;
  }

	/**
   * @return the modifiedBy
   */
  public int getModifiedBy() {
  	return modifiedBy;
  }

	/**
   * @param modifiedBy the modifiedBy to set
   */
  public void setModifiedBy(int modifiedBy) {
  	this.modifiedBy = modifiedBy;
  }

  public void setModifiedBy(String modifiedBy) {
  	this.modifiedBy = Integer.parseInt(modifiedBy);
  }

  /**
   * @return the claimStatus
   */
  public String getClaimStatus() {
  	return claimStatus;
  }

	/**
   * @param claimStatus the claimStatus to set
   */
  public void setClaimStatus(String claimStatus) {
  	this.claimStatus = claimStatus;
  }


  public boolean process(TransactionItem transactionItem, Connection db)
      throws Exception {
    int projectId = -1;
    int userId = -1;
    int userLevel = -1;

  	/*
  	HashMap values = (HashMap) transactionItem.getObject();
    String projectProfileId = (String) values.get("projectProfileId");
    String userProfileId = (String) values.get("userProfileId");
    String claimStatus = (String) values.get("claimStatus");
    */
  	
		if (StringUtils.hasText(projectProfileId)){
			Project project  = ProjectUtils.loadProject(projectProfileId);
			if (project != null){
				projectId = project.getId();
			}
		}
		if (StringUtils.hasText(userProfileId)){
			Project project  = ProjectUtils.loadProject(userProfileId);
			if (project.getProfile()){
				User user = UserUtils.loadUser(project.getOwner());
				if (user != null){
					userId = user.getId();
				}
			}
		}
    if (projectId == -1) {
      throw new SQLException("ProjectId was not specified");
    }
    if (userId == -1) {
      throw new SQLException("UserId was not specified");
    }
      
    if ("Claimed".equals(claimStatus)){
    	
    	//set the userlevel as 'manager' role
      LookupList roleList = CacheUtils.getLookupList("lookup_project_role");
      userLevel = roleList.getIdFromLevel(TeamMember.MANAGER);

  		PreparedStatement pst = db.prepareStatement(
          "UPDATE project_team " +
              "SET " +
              "tools = ?, status = ?, userlevel = ?, " +
              "modifiedby = ?, modified = CURRENT_TIMESTAMP " +
              "WHERE project_id = ? " + 
              "AND user_id = ? ");
      int i = 0;
      pst.setBoolean(++i, true);
      pst.setInt(++i, TeamMember.STATUS_ADDED);
      pst.setInt(++i, userLevel);
      pst.setInt(++i, modifiedBy);
      pst.setInt(++i, projectId);
      pst.setInt(++i, userId);
      pst.executeUpdate();
      pst.close();
    	
      //Remove owner field from the project
      Project project = ProjectUtils.loadProject(projectId);
      project.setOwner(userId);
      project.update(db);
    } else if ("Denied".equals(claimStatus)) {

      //Remove owner field from the project
      Project project = ProjectUtils.loadProject(projectId);
      if (project.getOwner() == userId){
        project.setOwner(-1);
        project.update(db);
      }
    }
    CacheUtils.invalidateValue(Constants.SYSTEM_PROJECT_CACHE, projectId);
	  return true;
  }

}


