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

package com.concursive.connect.web.modules.discussion.actions;

import com.concursive.commons.web.mvc.actions.ActionContext;
import com.concursive.connect.Constants;
import com.concursive.connect.web.controller.actions.GenericAction;
import com.concursive.connect.web.modules.discussion.dao.Topic;
import com.concursive.connect.web.modules.discussion.dao.Reply;
import com.concursive.connect.web.modules.documents.beans.FileDownload;
import com.concursive.connect.web.modules.documents.dao.FileItem;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;

import java.sql.Connection;

/**
 * Actions that support the discussion portlet
 *
 * @author matt rajkowski
 * @created November 29, 2001
 */
public final class DiscussionActions extends GenericAction {

  public final static String lf = System.getProperty("line.separator");

  public String executeCommandDetails(ActionContext context) {
    // DiscussionActions.do?command=Details&pid=139&iid=3648&cid=174&resetList=true
    String projectId = context.getRequest().getParameter("pid");
    String id = context.getRequest().getParameter("iid");
    String redirect = "/show/" + ProjectUtils.loadProject(Integer.parseInt(projectId)).getUniqueId() + "/topic/" + id;
    context.getRequest().setAttribute("redirectTo", redirect);
    context.getRequest().removeAttribute("PageLayout");
    return "Redirect301";
  }

  public String executeCommandDownload(ActionContext context) {
    String projectId = context.getRequest().getParameter("pid");
    String issueId = context.getRequest().getParameter("iid");
    String replyId = context.getRequest().getParameter("rid");
    String itemId = context.getRequest().getParameter("fid");
    String view = context.getRequest().getParameter("view");
    FileItem thisItem = null;
    Connection db = null;
    try {
      db = getConnection(context);
      Project thisProject = retrieveAuthorizedProject(Integer.parseInt(projectId), context);
      if (!hasProjectAccess(context, thisProject.getId(), "project-discussion-messages-reply")) {
        return "PermissionError";
      }
      if (issueId != null) {
        Topic thisTopic = new Topic(db, Integer.parseInt(issueId), thisProject.getId());
        thisItem = new FileItem(db, Integer.parseInt(itemId), thisTopic.getId(), Constants.DISCUSSION_FILES_TOPIC);
      }
      if (replyId != null) {
        Reply reply = new Reply(db, Integer.parseInt(replyId));
        Topic thisTopic = new Topic(db, reply.getIssueId(), thisProject.getId());
        thisItem = new FileItem(db, Integer.parseInt(itemId), reply.getId(), Constants.DISCUSSION_FILES_REPLY);
      }
    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return ("SystemError");
    } finally {
      this.freeConnection(context, db);
    }
    //Start the download
    try {
      FileItem itemToDownload = thisItem;
      itemToDownload.setEnteredBy(getUserId(context));
      String filePath = this.getPath(context, "projects") +
          getDatePath(itemToDownload.getModified()) +
          itemToDownload.getFilename();
      FileDownload fileDownload = new FileDownload();
      fileDownload.setFullPath(filePath);
      fileDownload.setDisplayName(itemToDownload.getClientFilename());
      if (fileDownload.fileExists()) {
        if (view != null && "true".equals(view)) {
          fileDownload.setFileTimestamp(itemToDownload.getModificationDate().getTime());
          fileDownload.streamContent(context);
        } else {
          fileDownload.sendFile(context);
        }
        //Get a db connection now that the download is complete
        db = getConnection(context);
        itemToDownload.updateCounter(db);
      } else {
        System.err.println("Discussion-> Trying to send a file that does not exist");
      }
    } catch (java.net.SocketException se) {
      //User either canceled the download or lost connection
    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return ("SystemError");
    } finally {
      this.freeConnection(context, db);
    }
    return ("-none-");
  }

  /*
  public String executeCommandChangeWatch(ActionContext context) {
    //Parameters
    String issueId = context.getRequest().getParameter("issueId");
    String value = context.getRequest().getParameter("value");
    Connection db = null;
    try {
      db = getConnection(context);
      // Verify issue view permissions
      Issue thisIssue = new Issue(db, Integer.parseInt(issueId));
      //Load the project
      Project thisProject = retrieveAuthorizedProject(thisIssue.getProjectId(), context);
      if (!hasProjectAccess(context, db, thisProject.getId(), "project-issues-view")) {
        return "PermissionError";
      }
      if ("true".equals(value)) {
        // Add user to watch list
        IssueContact issueContact = new IssueContact();
        issueContact.setIssueId(thisIssue.getId());
        issueContact.setEnteredBy(getUserId(context));
        issueContact.setUserId(getUserId(context));
        issueContact.insert(db);
      } else if ("false".equals(value)) {
        // Remove user from watch list
        IssueContactList.deleteUserId(db, thisIssue.getId(), getUserId(context));
      }
      context.getRequest().setAttribute("watchResult", value);
      return "MakeWatchOK";
    } catch (Exception e) {
      e.printStackTrace(System.out);
    } finally {
      freeConnection(context, db);
    }
    return null;
  }
  */
}
