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

package com.concursive.connect.web.modules.search.actions;

import com.concursive.commons.text.StringUtils;
import com.concursive.commons.web.mvc.actions.ActionContext;
import com.concursive.connect.Constants;
import com.concursive.connect.cms.portal.dao.DashboardPage;
import com.concursive.connect.cms.portal.dao.DashboardTemplateList;
import com.concursive.connect.cms.portal.utils.DashboardUtils;
import com.concursive.connect.indexer.*;
import com.concursive.connect.web.controller.actions.GenericAction;
import com.concursive.connect.web.modules.profile.dao.PermissionList;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.dao.ProjectCategoryList;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import com.concursive.connect.web.modules.search.beans.SearchBean;
import com.concursive.connect.web.modules.search.utils.SearchUtils;
import com.concursive.connect.web.portal.PortletManager;
import com.concursive.connect.web.utils.PagedListInfo;

import javax.servlet.http.Cookie;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * Actions for working with the search page
 *
 * @author matt rajkowski
 * @version $Id$
 * @created May 27, 2004
 */
public final class Search extends GenericAction {

  /**
   * This command will reindex all data
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public synchronized String executeCommandIndex(ActionContext context) {
    setMaximized(context);
    if (!getUser(context).getAccessAdmin()) {
      return "PermissionError";
    }
    Connection db = null;
    try {
      // Use the configured indexer
      IIndexerService index = IndexerFactory.getInstance().getIndexerService();
      // Create an indexer context
      IndexerContext indexerContext = new IndexerContext(getApplicationPrefs(context));
      indexerContext.setIndexType(Constants.INDEXER_FULL);
      // Retrieve a persistent database connection
      db = getConnection(context, true);
      // Reindex the whole database
      LOG.info("Reindexing the whole database");
      index.reindexAllData(indexerContext, db);
    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return ("SystemError");
    } finally {
      freeConnection(context, db);
    }
    return "IndexOK";
  }

  public synchronized String executeCommandIndexProjects(ActionContext context) {
    if (!getUser(context).getAccessAdmin()) {
      return "PermissionError";
    }
    Connection db = null;
    try {
      // Use the configured indexer
      IIndexerService index = IndexerFactory.getInstance().getIndexerService();
      // Create an indexer context
      IndexerContext indexerContext = new IndexerContext(getApplicationPrefs(context));
      indexerContext.setIndexType(Constants.INDEXER_DIRECTORY);
      // Retrieve a persistent database connection
      db = getConnection(context, true);
      // Reindex the whole database
      LOG.info("Reindexing the directory database");
      index.reindexAllData(indexerContext, db);
    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return ("SystemError");
    } finally {
      freeConnection(context, db);
    }
    return "IndexOK";
  }


  /**
   * This command applies permissions and projects to the query string and
   * provides this resulting hits
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandDefault(ActionContext context) {
    /* if (getUser(context).getId() < 0) {
      return "PermissionError";
    } */
    setMaximized(context);
    SearchBean search = (SearchBean) context.getFormBean();
    PagedListInfo searchBeanInfo = this.getPagedListInfo(context, "searchBeanInfo");
    searchBeanInfo.setLink(context, ctx(context) + "/search");
    Connection db = null;
    try {
      search.parseQuery();
      if (!search.isValid()) {
        return "SearchResultsERROR";
      }

      // Save the location in a cookie
      if (StringUtils.hasText(search.getLocation())) {
        Cookie locationCookie = new Cookie(Constants.COOKIE_USER_SEARCH_LOCATION, search.getLocation());
        locationCookie.setPath("/");
        // 21 day cookie
        locationCookie.setMaxAge(21 * 24 * 60 * 60);
        context.getResponse().addCookie(locationCookie);
      } else {
        // Cleanup the cookie
        Cookie userCookie = new Cookie(Constants.COOKIE_USER_SEARCH_LOCATION, "");
        userCookie.setPath("/");
        userCookie.setMaxAge(0);
        context.getResponse().addCookie(userCookie);
      }

      // Perform the search...
      // get the core Indexer
      IIndexerService indexer = IndexerFactory.getInstance().getIndexerService();
      IIndexerSearch searcher = indexer.getIndexerSearch(Constants.INDEXER_FULL);

      db = getConnection(context);
      long start = System.currentTimeMillis();

      // Create a query string based on the user input and compare with project access
      if (search.getScope() != SearchBean.THIS) {
        search.setProjectId(-1);
      }
      if (search.getProjectId() > -1) {
        Project thisProject = retrieveAuthorizedProject(search.getProjectId(), context);
        context.getRequest().setAttribute("project", thisProject);
        search.setProjectId(thisProject.getId());
      }

      // Base the search on project categories
      ProjectCategoryList categories = new ProjectCategoryList();
      categories.setEnabled(true);
      categories.setTopLevelOnly(true);
      categories.buildList(db);

      // Pass the query string and let the portlet customize it
      DashboardPage page = null;
      // Try the specific one first
      if (search.getCategoryId() > -1) {
        page = DashboardUtils.loadDashboardPage(DashboardTemplateList.TYPE_SEARCH, categories.getValueFromId(search.getCategoryId()));
      }
      // Default to the "All" page
      if (page == null) {
        page = DashboardUtils.loadDashboardPage(DashboardTemplateList.TYPE_SEARCH, "All");
      }
      if (page != null) {
        context.getRequest().setAttribute("dashboardPage", page);
        // Determine if the directory specific indexer is ready to be used
        IIndexerSearch projectSearcher = null;
        if ("true".equals(context.getServletContext().getAttribute(Constants.DIRECTORY_INDEX_INITIALIZED))) {
          // Search public projects only
          LOG.debug("Using directory index...");
          projectSearcher = indexer.getIndexerSearch(Constants.INDEXER_DIRECTORY);
        } else {
          // Use the full index because the directory hasn't loaded
          LOG.debug("Using full index...");
          projectSearcher = indexer.getIndexerSearch(Constants.INDEXER_FULL);
        }

        // Generate a valid project title query string
        String queryString = SearchUtils.generateProjectQueryString(search, getUserId(context));

        // Generate a valid data query string
        String dataQueryString = SearchUtils.generateDataQueryString(search, db, getUserId(context), -1);

        // provide other common items to the portlets
        context.getRequest().setAttribute("searchBean", search);
        context.getRequest().setAttribute("projectCategoryList", categories);
        // data searcher
        context.getRequest().setAttribute("searcher", searcher);
        context.getRequest().setAttribute("dataQueryString", dataQueryString);
        // project searcher
        context.getRequest().setAttribute("projectSearcher", projectSearcher);
        context.getRequest().setAttribute("baseQueryString", queryString);
        boolean isAction = PortletManager.processPage(context, db, page);
        // Show duration
        long end = System.currentTimeMillis();
        long duration = end - start;
        context.getRequest().setAttribute("duration", duration);
        if (System.getProperty("DEBUG") != null) {
          System.out.println("Duration: " + duration + " ms");
        }
        return "SearchResultsPortalOK";
      } else {
        // The search portal IS NOT being used... so the search results are of mixed content
        String queryString = null;
        if (search.getProjectId() > -1) {
          queryString = "(" + buildProjectList(search, db, getUserId(context), search.getProjectId()) + ") AND (" + search.getParsedQuery() + ")";
        } else {
          queryString = "(" + buildProjectList(search, db, getUserId(context), -1) + ") AND (" + search.getParsedQuery() + ")";
        }

        // Embed the various values in the query object...
        String tmpItemsPerPage = context.getRequest().getParameter("items");
        if (tmpItemsPerPage != null) {
          searchBeanInfo.setItemsPerPage(tmpItemsPerPage);
        }
        if ("true".equals(context.getRequest().getParameter("auto-populate"))) {
          searchBeanInfo.setCurrentOffset(0);
        }
        // Execute the query
        IndexerQueryResultList hits = new IndexerQueryResultList(queryString);
        hits.setPagedListInfo(searchBeanInfo);
        searcher.search(hits);
        context.getRequest().setAttribute("hits", hits);
        //System.out.println("Found " + hits.size() + " document(s) that matched query '" + queryString + "':");
        //context.getRequest().setAttribute("queryString", queryString);
        // Get unique project id's from hits
        /*
        ArrayList<String> projectIdList = new ArrayList<String>();
        for (int i = 0; i < hits.size(); i++) {
          Document document = hits.doc(i);
          String projectId = document.get("projectId");
          if (projectId != null && !projectIdList.contains(projectId)) {
            projectIdList.add(projectId);
          }
        }
        context.getRequest().setAttribute("projectIdList", projectIdList);
        */
        // Show duration
        long end = System.currentTimeMillis();
        long duration = end - start;
        context.getRequest().setAttribute("duration", duration);
        if (System.getProperty("DEBUG") != null) {
          System.out.println("Duration: " + duration + " ms");
        }
      }
    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return "SearchResultsERROR";
    } finally {
      freeConnection(context, db);
    }
    return "SearchResultsOK";
  }

  /**
   * Include private data that the user has access to
   *
   * @param db                Description of the Parameter
   * @param userId            Description of the Parameter
   * @param search            Description of the Parameter
   * @param specificProjectId Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  private String buildProjectList(SearchBean search, Connection db, int userId, int specificProjectId) throws SQLException {
    HashMap<Integer, Integer> projectList = new HashMap<Integer, Integer>();
    if (search.getSection() != SearchBean.WEBSITE) {
      PreparedStatement pst = null;
      ResultSet rs = null;
      if (userId > 0) {
        // get the projects for the user
        // get the project permissions for each project
        // if user has access to the data, then add to query
        pst = db.prepareStatement(
            "SELECT project_id, userlevel " +
                "FROM project_team " +
                "WHERE user_id = ? " +
                "AND status IS NULL " +
                (specificProjectId > -1 ? "AND project_id = ? " : ""));
        int i = 0;
        pst.setInt(++i, userId);
        if (specificProjectId > -1) {
          pst.setInt(++i, specificProjectId);
        }
        rs = pst.executeQuery();
        while (rs.next()) {
          int projectId = rs.getInt("project_id");
          int roleId = rs.getInt("userlevel");
          // these projects override the lower access projects
          projectList.put(projectId, roleId);
        }
        rs.close();
        pst.close();
      }
    }
    // build query string
    StringBuffer projectBuffer = new StringBuffer();
    // scan for permissions
    for (Integer projectId : projectList.keySet()) {
      StringBuffer permissionBuffer = new StringBuffer();
      Integer roleId = projectList.get(projectId);
      // for each project check the available user permissions
      Project cachedProject = ProjectUtils.loadProject(projectId);
      PermissionList permissionList = cachedProject.getPermissions();

      if (search.getSection() == SearchBean.DETAILS || search.getSection() == SearchBean.UNDEFINED) {
        // Check for project permissions
        if (permissionList.getAccessLevel("project-details-view") >= roleId) {
          if (permissionBuffer.length() > 0) {
            permissionBuffer.append(" OR ");
          }
          permissionBuffer.append("type:project");
        }
      }
      if (search.getSection() == SearchBean.NEWS || search.getSection() == SearchBean.UNDEFINED) {
        // Check for news permissions
        if (permissionList.getAccessLevel("project-news-view") >= roleId) {
          if (permissionBuffer.length() > 0) {
            permissionBuffer.append(" OR ");
          }
          // current, archived, unreleased
          // check for status permissions
          if (permissionList.getAccessLevel("project-news-view-unreleased") >= roleId) {
            permissionBuffer.append("type:news");
          } else {
            // take into account a date range  [20030101 TO 20040101]
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
            permissionBuffer.append("(type:news AND newsStatus:2 AND newsDate:[20030101 TO " + formatter.format(new Date()) + "])");
          }
        }
      }
      if (search.getSection() == SearchBean.WIKI || search.getSection() == SearchBean.UNDEFINED) {
        // Check for wiki permissions
        if (permissionList.getAccessLevel("project-wiki-view") >= roleId) {
          if (permissionBuffer.length() > 0) {
            permissionBuffer.append(" OR ");
          }
          permissionBuffer.append("type:wiki");
        }
      }
      if (search.getSection() == SearchBean.DISCUSSION || search.getSection() == SearchBean.UNDEFINED) {
        // Check for issue category permissions
        if (permissionList.getAccessLevel("project-discussion-forums-view") >= roleId) {
          if (permissionBuffer.length() > 0) {
            permissionBuffer.append(" OR ");
          }
          permissionBuffer.append("type:issuecategory");
        }
        // Check for issue permissions and issue reply permissions
        if (permissionList.getAccessLevel("project-discussion-topics-view") >= roleId) {
          if (permissionBuffer.length() > 0) {
            permissionBuffer.append(" OR ");
          }
          permissionBuffer.append("type:issue OR type:issuereply");
        }
      }
      if (search.getSection() == SearchBean.DOCUMENTS || search.getSection() == SearchBean.UNDEFINED) {
        // Check for file item permissions
        if (permissionList.getAccessLevel("project-documents-view") >= roleId) {
          if (permissionBuffer.length() > 0) {
            permissionBuffer.append(" OR ");
          }
          permissionBuffer.append("type:file");
        }
      }
      if (search.getSection() == SearchBean.LISTS || search.getSection() == SearchBean.UNDEFINED) {
        // Check for task category permissions and task permissions
        if (permissionList.getAccessLevel("project-lists-view") >= roleId) {
          if (permissionBuffer.length() > 0) {
            permissionBuffer.append(" OR ");
          }
          permissionBuffer.append("type:listcategory OR type:list");
        }
      }
      if (search.getSection() == SearchBean.TICKETS || search.getSection() == SearchBean.UNDEFINED) {
        // Check for ticket permissions
        if (permissionList.getAccessLevel("project-tickets-view") >= roleId) {
          if (permissionList.getAccessLevel("project-tickets-other") >= roleId) {
            // Can access all tickets in this project
            if (permissionBuffer.length() > 0) {
              permissionBuffer.append(" OR ");
            }
            permissionBuffer.append("type:ticket");
          } else {
            // Can access only tickets that the user created in this project
            if (permissionBuffer.length() > 0) {
              permissionBuffer.append(" OR ");
            }
            permissionBuffer.append("(type:ticket AND enteredBy:" + userId + ")");
          }
        }
      }
      if (search.getSection() == SearchBean.PLAN || search.getSection() == SearchBean.UNDEFINED) {
        // Check for requirement permissions
        if (permissionList.getAccessLevel("project-plan-view") >= roleId) {
          if (permissionBuffer.length() > 0) {
            permissionBuffer.append(" OR ");
          }
          permissionBuffer.append("type:outline");
          // Check for assignment folder permissions
          permissionBuffer.append(" OR ");
          permissionBuffer.append("type:activityfolder");
          // Check for assignment permissions
          permissionBuffer.append(" OR ");
          permissionBuffer.append("type:activity");
          // Check for assignment note permissions
          permissionBuffer.append(" OR ");
          permissionBuffer.append("type:activitynote");
        }
      }

      if (search.getSection() == SearchBean.ADS || search.getSection() == SearchBean.UNDEFINED) {
        // Check for ad. permissions
        if (permissionList.getAccessLevel("project-ads-view") >= roleId) {
          if (permissionBuffer.length() > 0) {
            permissionBuffer.append(" OR ");
          }
          permissionBuffer.append("type:ads");
        }
      }

      if (search.getSection() == SearchBean.CLASSIFIEDS || search.getSection() == SearchBean.UNDEFINED) {
        // Check for classified permissions
        if (permissionList.getAccessLevel("project-classifieds-view") >= roleId) {
          if (permissionBuffer.length() > 0) {
            permissionBuffer.append(" OR ");
          }
          permissionBuffer.append("type:classifieds");
        }
      }

      if (search.getSection() == SearchBean.REVIEWS || search.getSection() == SearchBean.UNDEFINED) {
        // Check for review permissions
        if (permissionList.getAccessLevel("project-reviews-view") >= roleId) {
          if (permissionBuffer.length() > 0) {
            permissionBuffer.append(" OR ");
          }
          permissionBuffer.append("type:reviews");
        }
      }

      // append this project and the user's permissions to the query string
      if (permissionBuffer.length() > 0) {
        if (projectBuffer.length() > 0) {
          projectBuffer.append(" OR ");
        }
        projectBuffer.append("(projectId:" + projectId + " AND (" + permissionBuffer.toString() + ")) ");
      }

      // debugging
      if (System.getProperty("DEBUG") != null) {
        if (permissionBuffer.length() == 0) {
          System.out.println("NO PERMISSIONS FOR PROJECT: " + projectId);
        }
      }
    }

    // If the portal is enabled, then include web page results too.
    if ((search.getSection() == SearchBean.WEBSITE && specificProjectId == -1) ||
        (search.getSection() == SearchBean.UNDEFINED && specificProjectId == -1)) {
      StringBuffer portalBuffer = new StringBuffer();
      PreparedStatement pst = db.prepareStatement(
          "SELECT project_id " +
              "FROM projects " +
              "WHERE approvaldate IS NOT NULL " +
              "AND portal = ? ");
      int i = 0;
      pst.setBoolean(++i, true);
      ResultSet rs = pst.executeQuery();
      int portalCount = 0;
      while (rs.next()) {
        ++portalCount;
        if (portalCount == 1) {
          portalBuffer.append("((");
        }
        if (portalCount > 1) {
          portalBuffer.append(" OR ");
        }
        int portalProjectId = rs.getInt("project_id");
        portalBuffer.append("projectId:" + portalProjectId);
      }
      rs.close();
      pst.close();

      // TODO: Make sure the news is enabled...
      if (portalBuffer.length() > 0) {
        // ((projectId:X OR projectId:Y ...
        portalBuffer.append(") AND ");
        // ((projectId:X OR projectId:Y) AND ...
        if (projectBuffer.length() > 0) {
          projectBuffer.append(" OR ");
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        projectBuffer.append(portalBuffer.toString() + "type:news AND newsPortal:true AND newsDate:[20030101 TO " + formatter.format(new java.util.Date()) + "])");
      }
    }

    // user does not have any projects, so lock them into a non-existent project
    // for security
    if (projectBuffer.length() == 0) {
      return "projectId:-1";
    } else {
      return projectBuffer.toString();
    }
  }


  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandTips(ActionContext context) {
    return "TipsOK";
  }
}
