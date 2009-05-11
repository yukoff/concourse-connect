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

package com.concursive.connect.web.modules.search.beans;

import com.concursive.commons.db.DatabaseUtils;
import com.concursive.commons.text.StringUtils;
import com.concursive.commons.web.mvc.beans.GenericBean;
import com.concursive.connect.web.modules.search.utils.SearchUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Contains the properties of a search form
 *
 * @author matt rajkowski
 * @version $Id$
 * @created May 17, 2004
 */
public class SearchBean extends GenericBean {
  public final static int UNDEFINED = -1;

  // constant scopes
  public final static int ALL = 1;
  public final static int THIS = 2;

  // constant sections
  public final static int NEWS = 3;
  public final static int DISCUSSION = 4;
  public final static int DOCUMENTS = 5;
  public final static int LISTS = 6;
  public final static int PLAN = 7;
  public final static int TICKETS = 8;
  public final static int DETAILS = 9;
  public final static int WIKI = 10;
  public final static int WEBSITE = 11;
  public final static int ADS = 12;
  public final static int CLASSIFIEDS = 13;
  public final static int REVIEWS = 14;

  // filters
  public final static int BEST_MATCH = -1;
  public final static int HIGHEST_RATED = 1;
  public final static int MOST_REVIEWED = 2;
  public final static int NEWLY_ADDED = 3;
  public final static int NEARBY = 4;
  public final static int MOST_POPULAR = 5;

  // search form
  private String query = null;
  private String type = null;
  private int scope = UNDEFINED;
  private int section = UNDEFINED;
  private int projectId = -1;
  private boolean openProjectsOnly = false;
  private String scopeText = null;
  private int categoryId = -1;
  private String location = null;
  private int filter = BEST_MATCH;
  // helper properties
  private String parsedQuery = null;
  private String parsedLocation = null;
  private ArrayList<String> terms = null;
  private ArrayList<String> locationTerms = null;
  private int numberFound = 0;
  private boolean valid = false;


  /**
   * Constructor for the SearchBean object
   */
  public SearchBean() {
  }


  /**
   * Gets the query attribute of the SearchBean object
   *
   * @return The query value
   */
  public String getQuery() {
    return query;
  }


  /**
   * Sets the query attribute of the SearchBean object
   *
   * @param tmp The new query value
   */
  public void setQuery(String tmp) {
    if (StringUtils.hasText(tmp)) {
      this.query = tmp;
    } else {
      this.query = null;
      parsedQuery = null;
      terms = null;
    }
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String tmp) {
    if (StringUtils.hasText(tmp)) {
      this.location = tmp;
    } else {
      this.location = null;
      parsedLocation = null;
      locationTerms = null;
    }
  }

  /**
   * Gets the scope attribute of the SearchBean object
   *
   * @return The scope value
   */
  public int getScope() {
    return scope;
  }


  /**
   * Sets the scope attribute of the SearchBean object
   *
   * @param tmp The new scope value
   */
  public void setScope(int tmp) {
    this.scope = tmp;
  }


  /**
   * Gets the section attribute of the SearchBean object
   *
   * @return The section value
   */
  public int getSection() {
    return section;
  }


  /**
   * Sets the section attribute of the SearchBean object
   *
   * @param tmp The new section value
   */
  public void setSection(int tmp) {
    this.section = tmp;
  }


  /**
   * Sets the section attribute of the SearchBean object
   *
   * @param tmp The new section value
   */
  public void setSection(String tmp) {
    this.section = Integer.parseInt(tmp);
  }


  /**
   * Sets the type attribute of the SearchBean object
   *
   * @param tmp The new type value
   */
  public void setType(String tmp) {
    type = tmp;
    if (tmp.equals("this")) {
      scope = THIS;
    } else if (tmp.equals("all")) {
      scope = ALL;
    } else {
      scope = UNDEFINED;
    }
  }

  public String getType() {
    return type;
  }

  /**
   * Sets the scope attribute of the SearchBean object
   *
   * @param tmp The new scope value
   */
  public void setScope(String tmp) {
    // section
    if (tmp.endsWith("News")) {
      section = NEWS;
    } else if (tmp.endsWith("Discussion")) {
      section = DISCUSSION;
    } else if (tmp.endsWith("Documents")) {
      section = DOCUMENTS;
    } else if (tmp.endsWith("Lists")) {
      section = LISTS;
    } else if (tmp.endsWith("Plan")) {
      section = PLAN;
    } else if (tmp.endsWith("Tickets")) {
      section = TICKETS;
    } else if (tmp.endsWith("Details")) {
      section = DETAILS;
    } else if (tmp.endsWith("Wiki")) {
      section = WIKI;
    } else if (tmp.endsWith("Website")) {
      section = WEBSITE;
    } else if (tmp.endsWith("Ads")) {
      section = ADS;
    } else if (tmp.endsWith("Classifieds")) {
      section = CLASSIFIEDS;
    } else if (tmp.endsWith("Reviews")) {
      section = REVIEWS;
    } else {
      section = UNDEFINED;
    }
    scopeText = tmp;
  }


  /**
   * Gets the projectId attribute of the SearchBean object
   *
   * @return The projectId value
   */
  public int getProjectId() {
    return projectId;
  }


  /**
   * Sets the projectId attribute of the SearchBean object
   *
   * @param tmp The new projectId value
   */
  public void setProjectId(int tmp) {
    this.projectId = tmp;
  }


  /**
   * Sets the projectId attribute of the SearchBean object
   *
   * @param tmp The new projectId value
   */
  public void setProjectId(String tmp) {
    this.projectId = Integer.parseInt(tmp);
  }

  public int getCategoryId() {
    return categoryId;
  }

  public void setCategoryId(int categoryId) {
    this.categoryId = categoryId;
  }

  public void setCategoryId(String tmp) {
    this.categoryId = Integer.parseInt(tmp);
  }

  public boolean getOpenProjectsOnly() {
    return openProjectsOnly;
  }

  public void setOpenProjectsOnly(boolean openProjectOnly) {
    this.openProjectsOnly = openProjectOnly;
  }

  public void setOpenProjectsOnly(String tmp) {
    this.openProjectsOnly = DatabaseUtils.parseBoolean(tmp);
  }

  public String getScopeText() {
    return scopeText;
  }

  public void setScopeText(String scopeText) {
    this.scopeText = scopeText;
  }

  /**
   * Gets the valid attribute of the SearchBean object
   *
   * @return The valid value
   */
  public boolean isValid() {
    return valid;
  }


  /**
   * Gets the parsedQuery attribute of the SearchBean object
   *
   * @return The parsedQuery value
   */
  public String getParsedQuery() {
    return parsedQuery;
  }


  /**
   * Sets the parsedQuery attribute of the SearchBean object
   *
   * @param tmp The new parsedQuery value
   */
  public void setParsedQuery(String tmp) {
    this.parsedQuery = tmp;
  }

  public String getParsedLocation() {
    return parsedLocation;
  }

  public void setParsedLocation(String parsedLocation) {
    this.parsedLocation = parsedLocation;
  }

  /**
   * Gets the terms attribute of the SearchBean object
   *
   * @return The terms value
   */
  public ArrayList<String> getTerms() {
    return terms;
  }


  /**
   * Sets the terms attribute of the SearchBean object
   *
   * @param tmp The new terms value
   */
  public void setTerms(ArrayList<String> tmp) {
    this.terms = tmp;
  }

  public ArrayList getLocationTerms() {
    return locationTerms;
  }

  public void setLocationTerms(ArrayList<String> locationTerms) {
    this.locationTerms = locationTerms;
  }

  public int getFilter() {
    return filter;
  }

  public void setFilter(int filter) {
    this.filter = filter;
  }

  public void setFilter(String filter) {
    this.filter = Integer.parseInt(filter);
  }

  /**
   * Adds a feature to the NumberFound attribute of the SearchBean object
   *
   * @param count The feature to be added to the NumberFound attribute
   */
  public void addNumberFound(int count) {
    numberFound += count;
  }


  /**
   * Description of the Method
   *
   * @return Description of the Return Value
   */
  public boolean parseQuery() {
    valid = false;
    // break up the query string into pieces to be used for building database queries
    if (query == null || "".equals(query.trim())) {
      return false;
    }
    // get rid of lucene query params
    parsedQuery = parseToAllowedSearch(query);
    if (parsedQuery == null) {
      return false;
    }
    // The terms are for highlighting and optimizing
    terms = SearchUtils.parseSearchTerms(parsedQuery);
    if (terms.size() == 0) {
      return false;
    }
    valid = true;
    if (System.getProperty("DEBUG") != null) {
      System.out.println("SearchBean-> Terms: (" + terms.size() + ") " + terms.toString());
    }
    if (location != null) {
      parsedLocation = parseToAllowedSearch(location);
      if (parsedLocation != null) {
        locationTerms = SearchUtils.parseSearchTerms(parsedLocation);
        if (System.getProperty("DEBUG") != null) {
          System.out.println("SearchBean-> Location: (" + locationTerms.size() + ") " + locationTerms.toString());
        }
      }
    }
    return true;
  }

  public static String parseToAllowedSearch(String query) {
    boolean validChar = false;
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < query.length(); i++) {
      // Lucene requires escaping: + - && || ! ( ) { } [ ] ^ " ~ * ? : \
      if ("&".indexOf(query.charAt(i)) > -1) {
        sb.append(" \\" + query.charAt(i) + " ");
      } else if ("():[]{}-+?~&|!^\\\"* ".indexOf(query.charAt(i)) == -1) {
        sb.append(query.charAt(i));
        validChar = true;
      } else {
        sb.append(" ");
      }
    }
    if (validChar) {
      if (!StringUtils.includesAny("abcdefghijklmnopqrstuvwxyz0123456789", sb.toString().toLowerCase())) {
        return null;
      }
      return SearchUtils.parseSearchText(sb.toString(), false);
    }
    return null;
  }

  public Map<String, String[]> getParameterMap() {
    Map<String, String[]> map = new HashMap<String, String[]>();
    map.put("categoryId", new String[]{"" + categoryId});
    map.put("query", new String[]{getQuery()});
    map.put("location", new String[]{getLocation()});
    map.put("scope", new String[]{getScopeText()});
    map.put("type", new String[]{getType()});
    map.put("filter", new String[]{"" + getFilter()});
    map.put("projectId", new String[]{"" + projectId});
    map.put("openProjectsOnly", new String[]{"" + openProjectsOnly});
    map.put("auto-populate", new String[]{"true"});
    return map;
  }

  public String getUrlByFilter(int newFilter) {
    return "/search?categoryId=" + categoryId + "&query=" + StringUtils.encodeUrl(getQuery()) + "&location=" + StringUtils.encodeUrl(getLocation()) + "&scope=" + getScopeText() + "&type=" + getType() + "&filter=" + newFilter + "&projectId=" + projectId + "&openProjectsOnly=" + openProjectsOnly + "&auto-populate=true";
  }

  public String getUrlByCategory(int newCategoryId) {
    return "/search?categoryId=" + newCategoryId + "&query=" + StringUtils.encodeUrl(getQuery()) + "&location=" + StringUtils.encodeUrl(getLocation()) + "&scope=" + getScopeText() + "&type=" + getType() + "&filter=" + getFilter() + "&projectId=" + projectId + "&openProjectsOnly=" + openProjectsOnly + "&auto-populate=true";
  }

}
