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

package com.concursive.connect.web.modules.wiki.utils;

import com.concursive.commons.text.StringUtils;
import com.concursive.connect.Constants;
import com.concursive.connect.web.modules.badges.dao.ProjectBadge;
import com.concursive.connect.web.modules.blog.dao.BlogPost;
import com.concursive.connect.web.modules.calendar.dao.Meeting;
import com.concursive.connect.web.modules.classifieds.dao.Classified;
import com.concursive.connect.web.modules.discussion.dao.Topic;
import com.concursive.connect.web.modules.discussion.dao.Forum;
import com.concursive.connect.web.modules.documents.dao.FileFolder;
import com.concursive.connect.web.modules.documents.dao.FileItem;
import com.concursive.connect.web.modules.lists.dao.TaskCategory;
import com.concursive.connect.web.modules.profile.beans.ProjectFormBean;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import com.concursive.connect.web.modules.promotions.dao.Ad;
import com.concursive.connect.web.modules.reviews.dao.ProjectRating;
import com.concursive.connect.web.modules.wiki.dao.Wiki;

/**
 * Splits a wiki link into inter-project link properties.  This class was
 * contributed to ConcourseConnect.
 *
 * @author sitsiliya
 * @author matt rajkowski
 * @created April 11, 2007
 */
public class WikiLink {

  // constants
  public static final String SIMPLE = "simple";
  public static final String COMPLEX = "complex";
  public static final String REFERENCE = "reference";

  // properties
  private String project = "";
  private String area = "";
  private String entity = "";
  private String name = "";
  private String status = "";
  public static final String rgex = "[|:]";

  /**
   * Method syntactic parsing of inter-project wiki links:
   * [[Wiki Article]]
   * [[Wiki Article|Display Text]]
   * [[|{project id}:{area}|{entity id}|{alternate name}]]
   * [[|{project unique id}:{area}|{entity id}|{alternate name}]]
   * [[|177:wiki|10|Some name]]
   * [[|177:ticket|4736|Some Ticket]]
   * Empty {project id} means the current project. Otherwise is project id field.
   * {area} is project, wiki, documents, tickets, etc.
   * {entity id} is an identifier, for wiki article name, tickets - ticket number, etc., can be null
   * {alternate name} self explainative
   *
   * @param projectId  the default project id (the project id of this wiki entry for substitution)
   * @param wikiString link
   */
  public WikiLink(int projectId, String wikiString) {
    parseLink(wikiString);

    // Use the specified id
    if (!StringUtils.hasText(project)) {
      project = String.valueOf(projectId);
    }
  }

  public WikiLink(String wikiString) {
    parseLink(wikiString);
  }

  private void parseLink(String wikiString) {
    wikiString = wikiString.replaceAll("(|[\\S\\s]+)\\[", "");
    int ind1 = wikiString.indexOf("]");
    if (ind1 != -1) {
      wikiString = wikiString.substring(0, ind1);
    }

    String rgex_ = "(http|https|ftp)://\\S+[\\S\\s]+";
    String rgex_1 = "(/)\\S+[\\S\\s]+";
    String rgex_2 = "[\\S\\s]+(|\\|)[\\S\\s]+";
    String rgex_3 = "\\|(|[\\S\\s])+:[\\S\\s]+";

    boolean match = wikiString.matches(rgex_) || wikiString.matches(rgex_1);
    boolean match2 = wikiString.matches(rgex_2) && !wikiString.startsWith("|");
    boolean match3 = wikiString.matches(rgex_3) || wikiString.startsWith("|");

    // References an external site - rgex_
    if (match) {
      linkRgex_(wikiString, " ");
      status = REFERENCE;
    }

    // Link to another wiki entry  - rgex_2
    if (match2 && !match && !match3) {
      linkRgex_(wikiString, "|");
      status = SIMPLE;
    }

    // Wiki link to any profile - rgex_3
    if (match3) {
      linkRgex_3(wikiString);
      status = COMPLEX;
    }

    if (!status.equals(REFERENCE) && !StringUtils.hasText(area)) {
      area = "wiki";
    }

    // Check to see if the project's unique id is being used instead of a number
    if (StringUtils.hasText(project)) {
      if (!StringUtils.isNumber(project)) {
        project = String.valueOf(ProjectUtils.retrieveProjectIdFromUniqueId(project));
      }
    }
  }

  /**
   * Method syntactic parsing link type simple and reference
   *
   * @param str link
   * @param st  type link
   */
  void linkRgex_(String str, String st) {
    int ind = str.indexOf(st);
    if (ind != -1) {
      entity = str.substring(0, ind);
      name = str.substring(ind + 1);
    } else {
      name = str;
      entity = str;
    }
  }

  /**
   * Method syntactic parsing link type complex.
   * Ex. 177:ticket|4736|Some Ticket
   * Ex. :ticket|4736|Some Ticket
   * Ex. :ticket|4736|
   * Ex. :ticket|4736
   * Ex. 6:wiki||
   *
   * @param str link
   */

  void linkRgex_3(String str) {
    String[] tokens = str.substring(1).split("[|]");

    // First token
    int projectIdx = tokens[0].indexOf(":");
    if (projectIdx > -1) {
      project = tokens[0].substring(0, projectIdx);
    }
    area = tokens[0].substring(projectIdx + 1);

    if (tokens.length == 1) {
      entity = "";
      name = "the wiki";
    } else {
      // Second token
      entity = tokens[1];

      // Third token
      if (tokens.length > 2 && StringUtils.hasText(tokens[2])) {
        name = tokens[2];
      }
    }
  }

  public int getProjectId() {
    try {
      return Integer.parseInt(project);
    }
    catch (NumberFormatException ex) {
      // Do nothing
    }
    return -1;
  }

  public void setProject(String project) {
    this.project = project;
  }

  public String getArea() {
    return area;
  }

  public void setArea(String area) {
    this.area = area;
  }

  public String getEntity() {
    return entity;
  }

  public int getEntityId() {
    try {
      return Integer.parseInt(entity);
    }
    catch (NumberFormatException ex) {
      // Do nothing
    }
    return -1;
  }

  public String getEntityTitle() {
    return (StringUtils.hasText(entity) ? StringUtils.replace(StringUtils.jsEscape(entity), "%20", "+") : "");
  }


  public void setEntity(String entity) {
    this.entity = entity;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String toString() {
    StringBuffer sb = new StringBuffer("============ WikiLink ============\n");
    sb.append("project: " + project + "\n");
    sb.append("area: " + area + "\n");
    sb.append("entity: " + entity + "\n");
    sb.append("name: " + name + "\n");
    sb.append("status: " + status + "\n");
    return sb.toString();
  }

  public String getPermissionArea() {
    if ("wiki".equals(area)) {
      return area;
    } else if ("list".equals(area)) {
      return "lists";
    } else if ("blog".equals(area)) {
      return "news";
    } else if ("post".equals(area)) {
      return "news";
    } else if ("event".equals(area)) {
      return "calendar";
    } else if ("forum".equals(area)) {
      return "discussion-forums";
    } else if ("topic".equals(area)) {
      return "discussion-topics";
    } else if ("promotion".equals(area)) {
      return "ads";
    } else if ("classified-ad".equals(area)) {
      return "classifieds";
    } else if ("file".equals(area)) {
      return "documents";
    } else if ("folder".equals(area)) {
      return "documents";
    } else if ("review".equals(area)) {
      return "reviews";
    } else {
      return area;
    }
  }

  public static String generateLink(Object object) {
    StringBuffer link = new StringBuffer("[[|");

    if (object instanceof Project) {
      Project project = (Project) object;
      link.append(project.getId());
      link.append(":profile||");
      link.append(project.getTitle());
    } else if (object instanceof ProjectFormBean) {
      ProjectFormBean projectBean = (ProjectFormBean) object;
      link.append(projectBean.getProjectId());
      link.append(":profile||");
      link.append(projectBean.getProjectTitle());
    } else if (object instanceof TaskCategory) {
      TaskCategory taskCategory = (TaskCategory) object;
      if (taskCategory.getLinkModuleId() == Constants.TASK_CATEGORY_PROJECTS) {
        link.append(taskCategory.getLinkItemId());
        link.append(":list|");
        link.append(taskCategory.getId());
        link.append("|");
        link.append(taskCategory.getDescription());
      }
    } else if (object instanceof BlogPost) {
      BlogPost blog = (BlogPost) object;
      link.append(blog.getProjectId());
      link.append(":post|");
      link.append(blog.getId());
      link.append("|");
      link.append(blog.getSubject());
    } else if (object instanceof Meeting) {
      Meeting meeting = (Meeting) object;
      link.append(meeting.getProjectId());
      link.append(":event|");
      link.append(meeting.getId());
      link.append("|");
      link.append(meeting.getTitle());
    } else if (object instanceof Wiki) {
      Wiki wiki = (Wiki) object;
      link.append(wiki.getProjectId());
      link.append(":wiki|");
      link.append(wiki.getSubject());
      link.append("|");
      link.append(wiki.getSubject());
    } else if (object instanceof Forum) {
      Forum forum = (Forum) object;
      link.append(forum.getProjectId());
      link.append(":forum|");
      link.append(forum.getId());
      link.append("|");
      link.append(forum.getSubject());
    } else if (object instanceof Topic) {
      Topic topic = (Topic) object;
      link.append(topic.getProjectId());
      link.append(":topic|");
      link.append(topic.getId());
      link.append("|");
      link.append(topic.getSubject());
    } else if (object instanceof Ad) {
      Ad ad = (Ad) object;
      link.append(ad.getProjectId());
      link.append(":promotion|");
      link.append(ad.getId());
      link.append("|");
      link.append(ad.getHeading());
    } else if (object instanceof Classified) {
      Classified classified = (Classified) object;
      link.append(classified.getProjectId());
      link.append(":classified-ad|");
      link.append(classified.getId());
      link.append("|");
      link.append(classified.getTitle());
    } else if (object instanceof FileItem) {
      FileItem file = (FileItem) object;
      if (file.getLinkModuleId() == Constants.PROJECTS_FILES) {
        link.append(file.getLinkItemId());
        link.append(":file|");
        link.append(file.getId());
        link.append("|");
        link.append(file.getSubject());
      }
    } else if (object instanceof FileFolder) {
      FileFolder folder = (FileFolder) object;
      if (folder.getLinkModuleId() == Constants.PROJECTS_FILES) {
        link.append(folder.getLinkItemId());
        link.append(":folder|");
        link.append(folder.getId());
        link.append("|");
        link.append(folder.getSubject());
      }
    } else if (object instanceof ProjectBadge) {
      ProjectBadge badge = (ProjectBadge) object;
      link.append(badge.getProjectId());
      link.append(":badge|");
      link.append(badge.getBadgeId());
      link.append("|");
      link.append(badge.getBadge().getTitle());
    } else if (object instanceof ProjectRating) {
      ProjectRating rating = (ProjectRating) object;
      link.append(rating.getProjectId());
      link.append(":review|");
      link.append(rating.getId());
      link.append("|");
      link.append(rating.getTitle());
    }
    link.append("]]");

    return link.toString().trim();
  }
}
