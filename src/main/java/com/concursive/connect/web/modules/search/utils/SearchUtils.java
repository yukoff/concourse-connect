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
package com.concursive.connect.web.modules.search.utils;

import com.concursive.commons.text.StringUtils;
import com.concursive.connect.indexer.IIndexerSearch;
import com.concursive.connect.indexer.IndexerFactory;
import com.concursive.connect.web.modules.common.social.geotagging.utils.LocationBean;
import com.concursive.connect.web.modules.common.social.geotagging.utils.LocationUtils;
import com.concursive.connect.web.modules.search.beans.SearchBean;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Utilities to work with search strings and Lucene queries
 *
 * @author matt rajkowski
 * @created June 11, 2004
 */
public class SearchUtils {

  private static Log LOG = LogFactory.getLog(SearchUtils.class);
  private final static String DOUBLE_QUOTE = "\"";
  private final static String WHITESPACE_AND_QUOTES = " \t\r\n\"";
  private final static String QUOTES_ONLY = "\"";

  /**
   * Extracts the keywords into tokens, and then either concats them with AND
   * if all words are required, or leaves the tokens alone
   *
   * @param searchText Description of the Parameter
   * @param allWords   Description of the Parameter
   * @return Description of the Return Value
   */
  public static String parseSearchText(String searchText, boolean allWords) {
    StringBuffer sb = new StringBuffer();
    boolean returnTokens = true;
    String currentDelims = WHITESPACE_AND_QUOTES;
    StringTokenizer parser = new StringTokenizer(searchText, currentDelims, returnTokens);
    String token = null;
    boolean spacer = false;
    while (parser.hasMoreTokens()) {
      token = parser.nextToken(currentDelims);
      if (!isDoubleQuote(token)) {
        if (hasText(token)) {
          String gotToken = token.trim().toLowerCase();
          if ("and".equals(gotToken) || "or".equals(gotToken) || "not".equals(gotToken)) {
            if (sb.length() > 0) {
              sb.append(" ");
            }
            sb.append(gotToken.toUpperCase());
            spacer = true;
          } else {
            if (spacer) {
              if (sb.length() > 0) {
                sb.append(" ");
              }
              spacer = false;
            } else {
              if (sb.length() > 0) {
                if (allWords) {
                  sb.append(" AND ");
                } else {
                  sb.append(" ");
                }
              }
            }
            if (gotToken.indexOf(" ") > -1) {
              sb.append("\"").append(gotToken).append("\"");
            } else {
              sb.append(gotToken);
            }
          }
        }
      } else {
        currentDelims = flipDelimiters(currentDelims);
      }
    }
    return sb.toString();
  }

  /**
   * Description of the Method
   *
   * @param searchText Description of the Parameter
   * @return Description of the Return Value
   */
  public static ArrayList<String> parseSearchTerms(String searchText) {
    ArrayList<String> terms = new ArrayList<String>();
    StringBuffer sb = new StringBuffer();
    boolean returnTokens = true;
    String currentDelims = WHITESPACE_AND_QUOTES;
    StringTokenizer parser = new StringTokenizer(searchText, currentDelims, returnTokens);
    String token = null;
    while (parser.hasMoreTokens()) {
      token = parser.nextToken(currentDelims);
      if (!isDoubleQuote(token)) {
        if (hasText(token)) {
          String gotToken = token.trim().toLowerCase();
          if ("and".equals(gotToken) || "or".equals(gotToken) || "not".equals(gotToken)) {
          } else {
            if (sb.length() > 0) {
              sb.append(" ");
            }
            sb.append(gotToken);
            terms.add(sb.toString());
            sb.setLength(0);
          }
        }
      } else {
        currentDelims = flipDelimiters(currentDelims);
      }
    }
    return terms;
  }

  /**
   * Description of the Method
   *
   * @param text Description of the Parameter
   * @return Description of the Return Value
   */
  private static boolean hasText(String text) {
    return (text != null && !text.trim().equals(""));
  }

  /**
   * Gets the doubleQuote attribute of the SearchUtils object
   *
   * @param text Description of the Parameter
   * @return The doubleQuote value
   */
  private static boolean isDoubleQuote(String text) {
    return text.equals(DOUBLE_QUOTE);
  }

  /**
   * Description of the Method
   *
   * @param delims Description of the Parameter
   * @return Description of the Return Value
   */
  private static String flipDelimiters(String delims) {
    String result = null;
    if (delims.equals(WHITESPACE_AND_QUOTES)) {
      result = QUOTES_ONLY;
    } else {
      result = WHITESPACE_AND_QUOTES;
    }
    return result;
  }

  public static synchronized IIndexerSearch retrieveSearcher(int indexType) throws IOException {
    // Get the shared searcher
    return IndexerFactory.getInstance().getIndexerService().getIndexerSearch(indexType);
  }

  /**
   * Generates the Lucene Query for finding public listings, user-public listings, and
   * user-member listings
   *
   * @param search
   * @param userId
   * @return
   */
  public static String generateProjectQueryString(SearchBean search, int userId, int instanceId, String projectList) {
    LOG.info("Search Query: " + search.getQuery() + " (" + search.getLocation() + ")");
    // The search portal is being used
    String locationTerm = search.getParsedLocation();

    // if search location is 5-digit number, then find the city and state to expand the search,
    // but boost the zip code
    if (StringUtils.hasText(locationTerm)) {
      if (StringUtils.isNumber(locationTerm) && locationTerm.length() == 5) {
        LocationBean location = LocationUtils.findLocationByZipCode(locationTerm);
        if (location != null) {
          locationTerm = "\"" + locationTerm + "\"^30 OR (\"" + location.getCity() + "\"^29 AND " + location.getState() + "^28)";
        }
      } else {
        locationTerm = "\"" + locationTerm + "\"^30";
      }
    }

    // Optimize the terms
    StringBuffer titleValues = new StringBuffer();
    StringBuffer keywordValues = new StringBuffer();
    StringBuffer termValues = new StringBuffer();

    // Find the phrase as-is in quotes for exactness, in the title
    titleValues.append("\"").append(search.getParsedQuery()).append("\"^25");

    // Find matches in the keywords
    keywordValues.append("\"").append(search.getParsedQuery()).append("\"^24");

    // Exact description match
    termValues.append("\"").append(search.getParsedQuery()).append("\"^16");

    // Find the words in the phrase
    ArrayList<String> terms = search.getTerms();

    int titleCount = 23;
    int keywordCount = 19;
    int count = 15;

    for (String term : terms) {
      if (titleValues.length() > 0) {
        titleValues.append(" OR ");
      }
      if (keywordValues.length() > 0) {
        keywordValues.append(" OR ");
      }
      if (termValues.length() > 0) {
        termValues.append(" OR ");
      }
      if (count < 5) {
        count = 5;
      }
      titleValues.append(term).append("^").append(titleCount);
      --titleCount;
      keywordValues.append(term).append("^").append(count);
      --keywordCount;
      termValues.append(term).append("^").append(count);
      --count;
    }
    // Find wildcards for the words in the phrase
    count = 4;
    for (String term : terms) {
      if (termValues.length() > 0) {
        termValues.append(" OR ");
      }
      if (count < 1) {
        count = 1;
      }
      termValues.append(term).append("*").append("^").append(count);
      --count;
    }

    String thisQuery =
        "(approved:1) " +
        (instanceId > -1 ? "AND (instanceId:" + instanceId + ") " : "") +
        "AND (" +
        "(guests:1) " +
        (userId > 0 ? "OR (participants:1) " : "") +
        (StringUtils.hasText(projectList) ? "OR " + "(projectId:(" + projectList + ")) " : "") +
        ") " +
        "AND (closed:0) " +
        "AND (website:0) " +
        (search.getProjectId() > -1 ? "AND (projectId:" + search.getProjectId() + ") " : "") +
        (StringUtils.hasText(search.getQuery()) ? 
          "AND (title:(" + titleValues.toString() + ") OR (keywords:(" + keywordValues.toString() + ")) OR (" + termValues.toString() + ")) " : "") +
        (StringUtils.hasText(locationTerm) ? "AND (location:(" + locationTerm + ")) " : "");

    LOG.debug("Built Query: " + thisQuery);

    return thisQuery;
  }

  /**
   * Generates the Lucene Query for finding public data, user-public data, and
   * user-member data
   *
   * @param search
   * @param userId
   * @param instanceId
   * @param projectListings
   * @return
   * @throws SQLException
   */
  public static String generateDataQueryString(SearchBean search, int userId, int instanceId, String projectListings) throws SQLException {
    // Generate the string
    return (StringUtils.hasText(search.getQuery()) ? "(" + search.getParsedQuery() + ") " : "") +
        (instanceId > -1 ? "AND (instanceId:" + instanceId + ") " : "") +
        "AND (" +
        "(" +
        "((guests:1) AND (membership:0)) " +
        (userId > 0 ? " OR ((participants:1) AND (membership:0)) " : "") +
        ") " +
        (StringUtils.hasText(projectListings) ? "OR " + "(projectId:(" + projectListings + "))" : "") +
        ") ";
  }

  public static String generateValidProjects(Connection db, int userId, int specificProjectId) throws SQLException {
    // @todo get ids from user cache
    // @update cache everytime a user is added or removed from a project team
    // get the projects for the user
    // get the project permissions for each project
    // if user has access to the data, then add to query
    StringBuffer projectList = new StringBuffer();
    PreparedStatement pst = db.prepareStatement(
        "SELECT project_id " +
        "FROM project_team " +
        "WHERE user_id = ? " +
        "AND status IS NULL " +
        (specificProjectId > -1 ? "AND project_id = ? " : ""));
    int i = 0;
    pst.setInt(++i, userId);
    if (specificProjectId > -1) {
      pst.setInt(++i, specificProjectId);
    }
    ResultSet rs = pst.executeQuery();
    while (rs.next()) {
      int projectId = rs.getInt("project_id");
      // these projects override the lower access projects
      if (projectList.length() > 0) {
        projectList.append(" OR ");
      }
      projectList.append(projectId);
    }
    rs.close();
    pst.close();
    return projectList.toString();
  }
}
