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

package com.concursive.connect.web.modules.calendar.feed;

import com.concursive.commons.text.StringUtils;
import com.concursive.connect.web.modules.calendar.dao.Meeting;
import com.concursive.connect.web.modules.login.utils.UserUtils;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * Class for working with the project event feed entry
 *
 * @author Kailash Bhoopalam
 * @version $Id$
 * @created April 06, 2007
 */
public class ProjectEventFeedEntry {

  public static SyndEntry getSyndEntry(Connection db, int id, String url) throws SQLException {
    SyndEntry entry = new SyndEntryImpl();
    SyndContent description = new SyndContentImpl();

    try {
      Meeting meeting = new Meeting(db, id);
      Project project = ProjectUtils.loadProject(meeting.getProjectId());

      //TODO: check if the project is public
      if (!project.getApproved() || !project.getApprovalDate().before(new Timestamp(System.currentTimeMillis()))) {
        return null;
      }
      entry.setTitle(project.getTitle() + " (" + project.getRequestDate() + ")");
      entry.setPublishedDate(project.getRequestDate());
      entry.setAuthor(UserUtils.getUserName(project.getEnteredBy()));
      entry.setLink(url + "/show/" + project.getUniqueId());

      description = new SyndContentImpl();
      description.setType("text/html");
      if (StringUtils.hasText(project.getShortDescription())) {
        if (project.getShortDescription().length() > 500) {
          description.setValue(project.getShortDescription().substring(0, 500));
        } else {
          description.setValue(project.getShortDescription());
        }
      }
      entry.setDescription(description);
    } catch (Exception e) {
      //likely if the object does not exist.
      return null;
    }


    return entry;
  }
}

