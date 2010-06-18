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
import com.concursive.connect.web.modules.discussion.dao.Forum;
import com.concursive.connect.web.modules.discussion.dao.Topic;
import com.concursive.connect.web.modules.documents.dao.FileFolder;
import com.concursive.connect.web.modules.documents.dao.FileItem;
import com.concursive.connect.web.modules.lists.dao.TaskCategory;
import com.concursive.connect.web.modules.profile.beans.ProjectFormBean;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import com.concursive.connect.web.modules.promotions.dao.Ad;
import com.concursive.connect.web.modules.reviews.dao.ProjectRating;
import com.concursive.connect.web.modules.wiki.dao.Wiki;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Splits a wiki link into inter-project link properties.  This class was
 * contributed to ConcourseConnect.
 *
 * @author sitsiliya
 * @author matt rajkowski
 * @created April 11, 2007
 */
public class WikiLink {

  private static Log LOG = LogFactory.getLog(WikiLink.class);

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

  // resulting url value
  private String url = "";

  /**
   * Method syntactic parsing of inter-project wiki links:
   * [[Wiki Article]]
   * [[|Wiki Home]]
   * [[Wiki Article|Display Text]]
   * [[|{project id}:{area}|{entity id}|{alternate name}]]
   * [[|{project unique id}:{area}|{entity id}|{alternate name}]]
   * [[|177:wiki|10|Some name]]
   * [[|177:ticket|4736|Some Ticket]]
   * Empty {project id} means the current project. Otherwise is project id field.
   * {area} is project, wiki, documents, tickets, etc.
   * {entity id} is an identifier, for wiki article name, tickets - ticket number, etc., can be null
   * {alternate name} self-explanatory
   *
   * @param projectId  the default project id (the project id of this wiki entry for substitution)
   * @param wikiString link
   */
  public WikiLink(String wikiString, int projectId) {
    // Use the specified id
    if (projectId != -1) {
      project = String.valueOf(projectId);
    }
    // Parse the link
    parseLink(wikiString);
  }

  /**
   * When a WikiLink is needed by a WikiImage, it uses this method which uses
   * the projectId first
   *
   * @param projectId
   * @param wikiString
   */
  public WikiLink(int projectId, String wikiString) {
    // Use the specified id
    project = String.valueOf(projectId);
    // Parse the link
    parseLink(wikiString);
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
    String rgex_0 = "(mailto):\\S+@[\\S\\s]+";
    String rgex_1 = "(/)\\S+[\\S\\s]+";
    String rgex_2 = "[\\S\\s]+(|\\|)[\\S\\s]+";
    String rgex_3 = "\\|(|[\\S\\s])+:[\\S\\s]+";

    boolean match = wikiString.matches(rgex_) || wikiString.matches(rgex_0) || wikiString.matches(rgex_1);
    boolean match2 = wikiString.matches(rgex_2) && !wikiString.startsWith("|");
    boolean match3 = wikiString.matches(rgex_3) || wikiString.startsWith("|");

    // References an external site - rgex_
    if (match) {
      // [[http://external]]
      LOG.debug("match condition");
      linkRgex_(wikiString, " ");
      status = REFERENCE;
    }

    // Link to another wiki entry  - rgex_2
    if (match2 && !match && !match3) {
      // [[Wiki link]]
      // [[Wiki Link|Renamed]]
      LOG.debug("match2 condition");
      linkRgex_(wikiString, "|");
      status = SIMPLE;
    }

    // Wiki link to any profile - rgex_3
    if (match3) {
      LOG.debug("match3 condition");
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

    // Save the URL
    if (WikiLink.REFERENCE.equals(this.getStatus())) {
      url = this.getEntity();
    } else {
      Project thisProject = null;
      if (this.getProjectId() > -1) {
        thisProject = ProjectUtils.loadProject(this.getProjectId());
      } else {
        thisProject = new Project();
      }
      // Links...
      if ("profile".equalsIgnoreCase(this.getArea())) {
        // Links to a profile page
        url = "/show/" + thisProject.getUniqueId();
      } else if ("badge".equalsIgnoreCase(this.getArea())) {
        // Links to a badge
        url = "/badge/" + this.getEntityId();
      } else if ("wiki".equalsIgnoreCase(this.getArea())) {
        // Links to another wiki page
        if (StringUtils.hasText(this.getEntity())) {
          url = "/show/" + thisProject.getUniqueId() + "/wiki/" + this.getEntityTitle();
        } else {
          url = "/show/" + thisProject.getUniqueId() + "/wiki";
        }
        // @todo link to the edit page if the page doesn't exist and user has access to modify the page
      } else {
        url = "/show/" + thisProject.getUniqueId() + "/" + this.getArea().toLowerCase() + (StringUtils.hasText(this.getEntity()) ? "/" + this.getEntityId() : "");
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
   * Ex. |177:issue|4736|Some Ticket
   * Ex. |:issue|4736|Some Ticket
   * Ex. |:issue|4736|
   * Ex. |:issue|4736
   * Ex. |4075:wiki||
   * Ex. |3995:calendar|
   * Ex. |3995:calendar||See our calendar
   * Ex. |Wiki Home
   *
   * @param str link
   */
  void linkRgex_3(String str) {
    String[] tokens = str.substring(1).split("[|]");
    // [[|9999999:topic|3353|info]]

    // First token
    int projectIdx = tokens[0].indexOf(":");
    if (projectIdx > 0) {
      // use the specified id, otherwise none was specified
      project = tokens[0].substring(0, projectIdx);
    }

    LOG.debug("Tokens: " + tokens.length);

    if (tokens.length == 1) {
      // Determine if an area is specified, else use wiki as the default
      if (projectIdx > -1) {
        // [[9999999:topic]]
        area = tokens[0].substring(projectIdx + 1);
      } else {
        // [[|hello]]
        area = "wiki";
        name = tokens[0];
      }
      entity = "";
      if (projectIdx > -1 && StringUtils.hasText(tokens[0].substring(projectIdx + 1))) {
        name = tokens[0].substring(projectIdx + 1);
      }
    } else {
      // First token
      area = tokens[0].substring(projectIdx + 1);

      // Second token
      entity = tokens[1];

      // Third token
      if (tokens.length > 2 && StringUtils.hasText(tokens[2])) {
        name = tokens[2];
      } else {
        // @note this is set by linkRgex_; think about overriding
        //name = area;
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

  public String getEntityId() {
    try {
      return String.valueOf(Integer.parseInt(entity));
    }
    catch (NumberFormatException ex) {
      if ("app".equals(area)) {
        return entity;
      }
    }
    return "-1";
  }

  public String getEntityTitle() {
    if (!StringUtils.hasText(entity)) {
      return "";
    }
    String content = StringUtils.replace(StringUtils.jsEscape(entity), "%20", "+");
    if (content.contains("&apos;")) {
      // This is an invalid HTML character that is being introduced
      content = StringUtils.replace(content, "&apos;", "%27");
    }
    return content;
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

  public String getUrl() {
    return url;
  }

  public String getUrl(String contextPath) {
    if (url != null && url.startsWith("/") && !url.startsWith(contextPath)) {
      return contextPath + url;
    } else {
      return url;
    }
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String toString() {
    StringBuffer sb = new StringBuffer("============ WikiLink ============\n");
    sb.append("project: " + project + "\n");
    sb.append("area: " + area + "\n");
    sb.append("entity: " + entity + "\n");
    sb.append("entityId: " + getEntityId() + "\n");
    sb.append("name: " + name + "\n");
    sb.append("status: " + status + "\n");
    sb.append("url: " + url + "\n");
    return sb.toString();
  }

  /**
   * For checking permissions, the area must be mapped to a project permission
   *
   * @return
   */
  public String getPermissionArea() {
    if ("profile".equals(area) || "app".equals(area)) {
      return "profile";
    } else if ("wiki".equals(area)) {
      return area;
    } else if ("list".equals(area)) {
      return "lists";
    } else if ("blog".equals(area)) {
      return "news";
    } else if ("post".equals(area)) {
      return "news";
    } else if ("event".equals(area)) {
      return "calendar";
    } else if ("discussion".equals(area)) {
      return "discussion-forums";
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

    if (object instanceof Meeting) {
      Meeting meeting = (Meeting) object;
      if (meeting.getIsWebcast()) {
        link.append(" ([[|");
        link.append(meeting.getProjectId());
        link.append(":webcasts|");
        link.append("|");
        link.append("Webcast");
        link.append("]])");
      } 
    }

    return link.toString().trim();
  }
}
