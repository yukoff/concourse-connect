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

package com.concursive.connect.web.utils;

import com.concursive.commons.db.DatabaseUtils;
import com.concursive.commons.objects.ObjectUtils;
import com.concursive.commons.text.StringUtils;
import com.concursive.commons.web.mvc.actions.ActionContext;

import javax.portlet.PortletURL;
import javax.portlet.RenderResponse;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Allows information to be stored in an object for the pagedlist.
 * When a web user visits a page, store this object in the session and call it
 * the name of the Module and View the user is looking at. Retrieve it to
 * resume where the user left off.
 *
 * @author matt rajkowski
 * @created July 12, 2001
 */
public class PagedListInfo implements Serializable {

  public static final long serialVersionUID = 8345429404174283569L;

  public static String allowed = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ.,0123456789_";
  public String[] lettersArray = {"0", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
  public final static int DEFAULT_ITEMS_PER_PAGE = 10;
  public final static int LIST_VIEW = 1;
  public final static int DETAILS_VIEW = 2;
  public final static int LEVEL_UNINITIALIZED = 0;
  public final static int LEVEL_INITIALIZED = 1;
  public final static int LEVEL_READY = 2;
  public static final String REFRESH_PARAMETER = "PAGEDLISTINFO_REFRESH_PARAMETER";
  public static final String LIST_FILTER = "listFilter";

  private int mode = LIST_VIEW;
  private String link = "";
  private String id = null;
  private String columnToSortBy = null;
  private String sortOrder = null;
  private String orderByStatement = null;
  private boolean randomOrder = false;
  private int itemsPerPage = DEFAULT_ITEMS_PER_PAGE;
  private int maxRecords = 0;
  private String currentLetter = "";
  private int currentOffset = 0;
  private int previousOffset = 0;
  private String listView = null;
  private HashMap<String, String> listFilters = new HashMap<String, String>();
  private boolean enableJScript = false;
  private boolean showForm = true;
  private boolean resetList = true;
  private String alternateSort = null;
  private HashMap<String, String> savedCriteria = new HashMap<String, String>();

  //specifically for modules using the contactsList
  private String parentFieldType = "";
  private String parentFormName = "";

  private boolean expandedSelection = false;
  private boolean scrollReload = false;
  private boolean isValid = false;
  private int initializationLevel = LEVEL_UNINITIALIZED;

  private String contextPath = null;

  // added for Portlets
  private String namespace = "";
  private Map<String, String[]> renderParameters = null;


  /**
   * Constructor for the PagedListInfo object
   *
   * @since 1.0
   */
  public PagedListInfo() {
  }


  /**
   * Gets the mode attribute of the PagedListInfo object
   *
   * @return The mode value
   */
  public int getMode() {
    return mode;
  }


  /**
   * Sets the mode attribute of the PagedListInfo object
   *
   * @param tmp The new mode value
   */
  public void setMode(int tmp) {
    if (mode == LIST_VIEW && tmp == DETAILS_VIEW) {
      previousOffset = currentOffset;
    }
    if (mode == DETAILS_VIEW && tmp == LIST_VIEW) {
      currentOffset = previousOffset;
    }
    this.mode = tmp;
  }


  /**
   * Sets the ColumnToSortBy attribute of the PagedListInfo object
   *
   * @param tmp The new ColumnToSortBy value
   * @since 1.0
   */
  public void setColumnToSortBy(String tmp) {
    this.columnToSortBy = tmp;
  }


  /**
   * Sets the ColumnToSortBy attribute of the PagedListInfo object
   *
   * @param enableJScript The new enableJScript value
   * @since 1.0
   */
  public void setEnableJScript(boolean enableJScript) {
    this.enableJScript = enableJScript;
  }


  /**
   * Sets the SortOrder attribute of the PagedListInfo object
   *
   * @param tmp The new SortOrder value
   * @since 1.0
   */
  public void setSortOrder(String tmp) {
    this.sortOrder = tmp;
  }


  /**
   * Sets the randomOrder attribute of the PagedListInfo object
   *
   * @param tmp The new randomOrder value
   * @since 1.0
   */
  public void setRandomOrder(boolean tmp) {
    this.randomOrder = tmp;
  }


  /**
   * Sets the showForm attribute of the PagedListInfo object
   *
   * @param showForm The new showForm value
   */
  public void setShowForm(boolean showForm) {
    this.showForm = showForm;
  }


  /**
   * Sets the resetList attribute of the PagedListInfo object
   *
   * @param resetList The new resetList value
   */
  public void setResetList(boolean resetList) {
    this.resetList = resetList;
  }


  /**
   * Sets the isValid attribute of the PagedListInfo object
   *
   * @param tmp The new isValid value
   */
  public void setIsValid(boolean tmp) {
    this.isValid = tmp;
  }


  /**
   * Sets the isValid attribute of the PagedListInfo object
   *
   * @param tmp The new isValid value
   */
  public void setIsValid(String tmp) {
    this.isValid = DatabaseUtils.parseBoolean(tmp);
  }


  /**
   * Sets the lettersArray attribute of the PagedListInfo object
   *
   * @param tmp The new lettersArray value
   */
  public void setLettersArray(String[] tmp) {
    this.lettersArray = tmp;
  }


  /**
   * Gets the lettersArray attribute of the PagedListInfo object
   *
   * @return The lettersArray value
   */
  public String[] getLettersArray() {
    return lettersArray;
  }


  /**
   * Gets the isValid attribute of the PagedListInfo object
   *
   * @return The isValid value
   */
  public boolean getIsValid() {
    return isValid;
  }


  /**
   * Gets the expandedSelection attribute of the PagedListInfo object
   *
   * @return The expandedSelection value
   */
  public boolean getExpandedSelection() {
    return expandedSelection;
  }


  /**
   * Sets the expandedSelection attribute of the PagedListInfo object
   *
   * @param expandedSelection The new expandedSelection value
   */
  public void setExpandedSelection(boolean expandedSelection) {
    this.expandedSelection = expandedSelection;
    this.setItemsPerPage(DEFAULT_ITEMS_PER_PAGE);
  }


  /**
   * Gets the scrollReload attribute of the PagedListInfo object
   *
   * @return The scrollReload value
   */
  public boolean getScrollReload() {
    return scrollReload;
  }


  /**
   * Sets the scrollReload attribute of the PagedListInfo object
   *
   * @param tmp The new scrollReload value
   */
  public void setScrollReload(boolean tmp) {
    this.scrollReload = tmp;
  }

  public int getInitializationLevel() {
    return initializationLevel;
  }

  public void setInitializationLevel(int initializationLevel) {
    this.initializationLevel = initializationLevel;
  }


  /**
   * Sets the ItemsPerPage attribute of the PagedListInfo object
   *
   * @param tmp The new ItemsPerPage value
   * @since 1.0
   */
  public void setItemsPerPage(int tmp) {
    if (tmp > itemsPerPage || tmp == -1 || tmp == 0) {
      resetList();
    }
    this.itemsPerPage = tmp;
  }


  /**
   * Gets the savedCriteria attribute of the PagedListInfo object
   *
   * @return The savedCriteria value
   */
  public HashMap<String, String> getSavedCriteria() {
    return savedCriteria;
  }


  /**
   * Sets the savedCriteria attribute of the PagedListInfo object
   *
   * @param savedCriteria The new savedCriteria value
   */
  public void setSavedCriteria(HashMap savedCriteria) {
    this.savedCriteria = savedCriteria;
  }


  /**
   * Sets the parentFieldType attribute of the PagedListInfo object
   *
   * @param parentFieldType The new parentFieldType value
   */
  public void setParentFieldType(String parentFieldType) {
    this.parentFieldType = parentFieldType;
  }


  /**
   * Gets the id attribute of the PagedListInfo object
   *
   * @return The id value
   */
  public String getId() {
    return id;
  }


  /**
   * Sets the id attribute of the PagedListInfo object
   *
   * @param id The new id value
   */
  public void setId(String id) {
    this.id = id;
  }


  /**
   * Sets the parentFormName attribute of the PagedListInfo object
   *
   * @param parentFormName The new parentFormName value
   */
  public void setParentFormName(String parentFormName) {
    this.parentFormName = parentFormName;
  }


  /**
   * Gets the parentFormName attribute of the PagedListInfo object
   *
   * @return The parentFormName value
   */
  public String getParentFormName() {
    return parentFormName;
  }


  /**
   * Gets the parentFieldType attribute of the PagedListInfo object
   *
   * @return The parentFieldType value
   */
  public String getParentFieldType() {
    return parentFieldType;
  }


  /**
   * Gets the alternateSort attribute of the PagedListInfo object
   *
   * @return The alternateSort value
   */
  public String getAlternateSort() {
    return alternateSort;
  }


  /**
   * Sets the alternateSort attribute of the PagedListInfo object
   *
   * @param alternateSort The new alternateSort value
   */
  public void setAlternateSort(String alternateSort) {
    this.alternateSort = alternateSort;
  }


  /**
   * Sets the ItemsPerPage attribute of the PagedListInfo object
   *
   * @param tmp The new ItemsPerPage value
   * @since 1.0
   */
  public void setItemsPerPage(String tmp) {
    try {
      this.setItemsPerPage(Integer.parseInt(tmp));
    } catch (Exception e) {
    }
  }


  public void setLink(ActionContext context, String tmp) {
    link = tmp;
    if (context.getRequest().getParameter("popup") != null) {
      link = addParameter(link, "popup", context.getRequest().getParameter("popup"));
    }
    contextPath = context.getRequest().getContextPath();
  }

  public void setLink(String link) {
    this.link = link;
  }

  public String getLink() {
    return link;
  }

  public void setContextPath(String contextPath) {
    this.contextPath = contextPath;
  }

  /**
   * Sets the MaxRecords attribute of the PagedListInfo object
   *
   * @param tmp The new MaxRecords value
   * @since 1.0
   */
  public void setMaxRecords(int tmp) {
    maxRecords = tmp;
    //Check to see if the currentOffset is greater than maxRecords,
    //if so, find a nice page break to stop on
    if (maxRecords <= currentOffset && maxRecords > 0 && getItemsPerPage() != -1) {
      currentOffset = maxRecords;

      while (((currentOffset % getItemsPerPage()) > 0) && (currentOffset > 0)) {
        --currentOffset;
      }
      //Check to see if the page break has any records to display, otherwise
      //go back a page
      if (currentOffset == maxRecords) {
        currentOffset = currentOffset - getItemsPerPage();
      }
    }
  }


  /**
   * Sets the CurrentLetter attribute of the PagedListInfo object
   *
   * @param tmp The new CurrentLetter value
   * @since 1.1
   */
  public void setCurrentLetter(String tmp) {
    this.currentLetter = tmp;
  }


  /**
   * Sets the CurrentOffset attribute of the PagedListInfo object
   *
   * @param tmp The new CurrentOffset value
   * @since 1.1
   */
  public void setCurrentOffset(int tmp) {
    if (tmp < 0) {
      this.currentOffset = 0;
    } else {
      this.currentOffset = tmp;
    }
  }


  /**
   * Sets the CurrentOffset attribute of the PagedListInfo object
   *
   * @param tmp The new CurrentOffset value
   * @since 1.1
   */
  public void setCurrentOffset(String tmp) {
    try {
      this.setCurrentOffset(Integer.parseInt(tmp));
    } catch (Exception e) {
    }
  }

  public void setCurrentPage(int pageNumber) {
    this.setCurrentOffset((pageNumber - 1) * getItemsPerPage());
  }


  /**
   * Sets the ListView attribute of the PagedListInfo object. The ListView
   * property stores what view the user has selected.
   *
   * @param tmp The new ListView value
   * @since 1.11
   */
  public void setListView(String tmp) {
    this.listView = tmp;
  }


  /**
   * When initializing a pagedList, a default view can be set if it is not already set
   *
   * @param tmp
   */
  public void setDefaultListView(String tmp) {
    if (listView == null) {
      this.listView = tmp;
    }
  }


  /**
   * Sets the Parameters attribute of the PagedListInfo object
   *
   * @param context The new Parameters value
   * @return Description of the Return Value
   * @since 1.1
   */
  public boolean setParameters(ActionContext context) {
    return setParameters(context.getRequest());
  }


  /**
   * Sets the Parameters attribute of the PagedListInfo object
   *
   * @param request The new Parameters value
   * @return Description of the Return Value
   * @since 1.1
   */
  public boolean setParameters(HttpServletRequest request) {
    //check for multiple pagedLists on a single page
    if (request.getParameter("pagedListInfoId") != null &&
        !(request.getParameter("pagedListInfoId").equals("")) &&
        this.getId() != null && !"".equals(this.getId().trim()) &&
        !(request.getParameter("pagedListInfoId").equals("null")) &&
        !(request.getParameter("pagedListInfoId").equals(
            this.getId()))) {
      return false;
    }

    Enumeration parameters = request.getParameterNames();
    boolean reset = false;

    String tmpSortOrder = request.getParameter("order");
    if (tmpSortOrder != null && checkAllowed(tmpSortOrder)) {
      this.setSortOrder(tmpSortOrder);
    }

    //Check to see if the user is changing the sort column, or clicking on the
    //same column again
    String tmpColumnToSortBy = request.getParameter("column");
    if (tmpColumnToSortBy != null) {
      if (columnToSortBy != null) {
        if (tmpColumnToSortBy.equals(columnToSortBy)) {
          if (sortOrder == null) {
            this.setSortOrder("desc");
          } else {
            this.setSortOrder(null);
          }
        } else {
          if (sortOrder != null && sortOrder.equals("desc")) {
            this.setSortOrder(null);
          }
        }
      }
      // Security check so that arbitrary queries cannot be executed
      if (checkAllowed(tmpColumnToSortBy)) {
        this.setColumnToSortBy(tmpColumnToSortBy);
      }
    }

    // User has specified a page number to view
    // This will be used as a parameter in the query
    String tmpCurrentPage = request.getParameter("page");
    if (tmpCurrentPage != null) {
      try {
        if (getItemsPerPage() == -1) {
          throw new java.lang.NumberFormatException("All records in one page");
        }
        this.setCurrentPage(Integer.parseInt(tmpCurrentPage));
      } catch (java.lang.NumberFormatException e) {
        this.setCurrentOffset(0);
      }
    }

    //User is changing the number of items to display -- needs to be done after the
    //page select
    String tmpItemsPerPage = request.getParameter("items");
    if (tmpItemsPerPage != null) {
      this.setItemsPerPage(tmpItemsPerPage);
    }

    //The user wants to jump to a specific letter of the alphabet...
    //The alphabet is currently tuned to a specific field that is identified
    //by the object... maybe in the future it will use the column being
    //sorted on.
    String tmpCurrentLetter = request.getParameter("letter");
    if (tmpCurrentLetter != null) {
      this.setCurrentLetter(tmpCurrentLetter);
      //Need to reset the sort because it is configured by the underlying query object
      this.setColumnToSortBy(null);
      this.setSortOrder(null);
    } else {
      this.setCurrentLetter("");
    }

    //The user has selected an offset to go to... could be through a
    //page element that calculates the offset per page
    String tmpCurrentOffset = request.getParameter("offset");
    if (tmpCurrentOffset != null) {
      this.setCurrentOffset(tmpCurrentOffset);
    }

    //The user has changed the view of the pagedList
    String tmpListView = request.getParameter("listView");
    if (tmpListView != null) {
      if (listView != null && !listView.equals(tmpListView)) {
        resetList();
      }
      this.setListView(tmpListView);
    }

    //Populate the PagedListInfo with data filters, reset the list view since
    //the user is changing the filter
    Collection<String> paramNames = Collections.list(request.getParameterNames());
    boolean hasListFilter = false;
    for (String paramName : paramNames) {
      if (paramName.contains(LIST_FILTER) && paramName.length() > LIST_FILTER.length()) {
        String filterNumStr = paramName.split(LIST_FILTER)[1]; // already checked to make sure this doesn't throw indexOutOfBounds
        int filterNum;
        try {
          filterNum = Integer.parseInt(filterNumStr);
        } catch (NumberFormatException ne) {
          continue; // filter name doesn't follow convention so ignore
        }
        String filter = request.getParameter(paramName);
        if (filter != null) {
          addFilter(filterNum, filter);
          hasListFilter = true;
        }
      }
    }

    if (request.getParameter("listFilter1") != null && resetList) {
      String thisFilter = request.getParameter("listFilter1");
      if (listFilters.get("listFilter1") != null && !thisFilter.equals(
          listFilters.get("listFilter1"))) {
        resetList();
      }
    }

    while (parameters.hasMoreElements()) {
      String param = (String) parameters.nextElement();
      if (param.startsWith("search")) {
        if (!(reset)) {
          this.getSavedCriteria().clear();
          reset = true;
        }
        this.getSavedCriteria().put(
            param, request.getParameter(param));
      }
    }
    return true;
  }


  /**
   * Sets the searchCriteria attribute of the PagedListInfo object
   *
   * @param obj The new searchCriteria value
   * @return Description of the Return Value
   */
  public boolean setSearchCriteria(Object obj) {
    if (!this.getSavedCriteria().isEmpty()) {
      Iterator hashIterator = this.getSavedCriteria().keySet().iterator();
      while (hashIterator.hasNext()) {
        String tempKey = (String) hashIterator.next();
        if (this.getCriteriaValue(tempKey) != null && !(this.getCriteriaValue(
            tempKey).trim().equals(""))) {
          //its an int
          if (tempKey.startsWith("searchcode") || tempKey.startsWith(
              "searchdate")) {
            ObjectUtils.setParam(
                obj, tempKey.substring(10), this.getCriteriaValue(tempKey));
          } else {
            ObjectUtils.setParam(
                obj, tempKey.substring(6), "%" + this.getCriteriaValue(
                    tempKey) + "%");
          }
        }
      }
    }
    return true;
  }


  /**
   * Gets the searchOptionValue attribute of the PagedListInfo object
   *
   * @param field Description of the Parameter
   * @return The searchOptionValue value
   */
  public String getSearchOptionValue(String field) {
    if (this.getSavedCriteria() != null && this.getSavedCriteria().get(field) != null) {
      return (String) savedCriteria.get(field);
    }
    return "";
  }


  public int getSearchOptionValueAsInt(String field) {
    if (this.getSavedCriteria() != null && this.getSavedCriteria().get(field) != null) {
      return Integer.parseInt((String) savedCriteria.get(field));
    }
    return -1;
  }


  /**
   * Sets the defaultSort attribute of the PagedListInfo object
   *
   * @param column The new defaultSort value
   * @param order  Ex. "desc" or null
   */
  public void setDefaultSort(String column, String order) {
    if (!this.hasSortConfigured()) {
      this.setColumnToSortBy(column);
      this.setSortOrder(order);
    }
  }

  public void setNamespace(String namespace) {
    this.namespace = namespace;
  }

  public void setRenderParameters(Map<String, String[]> renderParameters) {
    this.renderParameters = renderParameters;
  }

  /**
   * Gets the ColumnToSortBy attribute of the PagedListInfo object
   *
   * @return The ColumnToSortBy value
   * @since 1.0
   */
  public String getColumnToSortBy() {
    return columnToSortBy;
  }


  /**
   * Gets the SortOrder attribute of the PagedListInfo object
   *
   * @return The SortOrder value
   * @since 1.0
   */
  public String getSortOrder() {
    return sortOrder;
  }


  /**
   * Gets the ItemsPerPage attribute of the PagedListInfo object
   *
   * @return The ItemsPerPage value
   * @since 1.0
   */
  public int getItemsPerPage() {
    if (mode == DETAILS_VIEW) {
      return 1;
    }
    return itemsPerPage;
  }


  /**
   * Gets the MaxRecords attribute of the PagedListInfo object
   *
   * @return The MaxRecords value
   * @since 1.11
   */
  public int getMaxRecords() {
    return maxRecords;
  }


  /**
   * Gets the CurrentLetter attribute of the PagedListInfo object
   *
   * @return The CurrentLetter value
   * @since 1.1
   */
  public String getCurrentLetter() {
    return currentLetter;
  }


  /**
   * Gets the CurrentOffset attribute of the PagedListInfo object
   *
   * @return The CurrentOffset value
   * @since 1.1
   */
  public int getCurrentOffset() {
    return currentOffset;
  }


  /**
   * Gets the CurrentOffset attribute of the PagedListInfo object
   *
   * @return The CurrentOffset value
   * @since 1.1
   */
  public boolean getEnableJScript() {
    return enableJScript;
  }


  /**
   * Gets the PageLinks attribute of the PagedListInfo object <p>
   * <p/>
   * The pages are numbered from 1 to the highest page
   *
   * @return The PageLinks value
   * @since 1.0
   */
  public String getNumericalPageLinks() {
    int numPages = this.getNumberOfPages();
    StringBuffer links = new StringBuffer();
    links.append(
        numPages + " page" + ((numPages == 1) ? "" : "s") + " in this view ");
    if (numPages > 1) {
      links.append("[");
      for (int i = 1; i < (numPages + 1); i++) {
        if ((i - 1) * getItemsPerPage() == currentOffset) {
          links.append(" <strong>" + i + "</strong> ");
        } else {
          links.append("<a href=\"" + addParameter(link, "offset", String.valueOf((i - 1) * getItemsPerPage())) + "\"> " + i + " </a>");
        }
      }
      links.append("]");
    }
    return links.toString();
  }

  public String getLinkForPage(int currentPage, RenderResponse response) {
    int newOffset = ((currentPage - 1) * getItemsPerPage());
    return getLinkForOffset(newOffset, response);
  }

  public String getLinkForOffset(int newOffset, RenderResponse response) {
    String thisLink;
    if (response == null) {
      thisLink = addParameter(link, "pagedListInfoId", String.valueOf(this.getId())) +
          (getExpandedSelection() ? "&pagedListSectionId=" + this.getId() : "") +
          "&offset=" + (newOffset > 0 ? newOffset : 0);
    } else {
      PortletURL renderURL = response.createRenderURL();
      Map<String, String[]> params;
      if (renderParameters == null) {
        params = new HashMap<String, String[]>();
      } else {
        params = new HashMap<String, String[]>(renderParameters);
      }
      params.put("pagedListInfoId", new String[]{this.getId()});
      params.put("pagedListSectionId", new String[]{this.getId()});
      params.put("offset", new String[]{String.valueOf(newOffset > 0 ? newOffset : 0)});
      params.put("page", new String[]{String.valueOf(newOffset > 0 ? newOffset % 10 : 0)});
      renderURL.setParameters(params);
      thisLink = renderURL.toString();
    }
    return thisLink;
  }


  /**
   * Gets the listPropertiesHeader attribute of the PagedListInfo object
   *
   * @param formName Description of Parameter
   * @return The listPropertiesHeader value
   */
  public String getListPropertiesHeader(String formName) {
    if (showForm) {
      if (expandedSelection) {
        link = addParameter(link, "pagedListSectionId", String.valueOf(id));
      }
      return ("<form name=\"" + formName + "\" action=\"" + link + "\" method=\"post\">");
    } else {
      return "";
    }

  }


  /**
   * Gets the listPropertiesFooter attribute of the PagedListInfo object
   *
   * @return The listPropertiesFooter value
   */
  public String getListPropertiesFooter() {
    if (showForm) {
      return ("</form>");
    } else {
      return "";
    }
  }

  public int getCurrentPageNumber() {
    return getItemsPerPage() != -1 && getItemsPerPage() != 0 ? ((currentOffset / getItemsPerPage()) + 1) : 1;
  }

  /**
   * Gets the numericalPageEntry attribute of the PagedListInfo object
   *
   * @return The numericalPageEntry value
   */
  public String getNumericalPageEntry() {
    return ("<input type=\"text\" name=\"page\" value=\"" + (getCurrentPageNumber()) + "\" size=\"3\">");
  }


  /**
   * Gets the itemsPerPageEntry attribute of the PagedListInfo object
   *
   * @return The itemsPerPageEntry value
   */
  public String getItemsPerPageEntry(String allLabel) {
    HtmlSelect itemSelect = new HtmlSelect();
    itemSelect.addItem("6");
    itemSelect.addItem("10");
    itemSelect.addItem("12");
    itemSelect.addItem("20");
    itemSelect.addItem("30");
    itemSelect.addItem("50");
    itemSelect.addItem("100");
    itemSelect.addItem("-1", allLabel);
    itemSelect.setJsEvent("onChange='submit();'");
    return (itemSelect.getHtml("items", getItemsPerPage()));
    //return("Items per page <input type=\"text\" name=\"items\" value=\"" + getItemsPerPage() + "\" size=\"3\">");
  }


  /**
   * Gets the numberOfPages attribute of the PagedListInfo object
   *
   * @return The numberOfPages value
   */
  public int getNumberOfPages() {
    if (getItemsPerPage() != -1 && getItemsPerPage() != 0) {
      return (int) Math.ceil((double) maxRecords / (double) getItemsPerPage());
    }
    return 1;
  }


  /**
   * Gets the AlphebeticalPageLinks attribute of the PagedListInfo object
   *
   * @return The AlphebeticalPageLinks value
   * @since 1.1
   */
  public String getAlphabeticalPageLinks() {
    StringBuffer links = new StringBuffer();
    for (int i = 0; i < lettersArray.length; i++) {
      String thisLetter = lettersArray[i];
      if (thisLetter.equals(currentLetter)) {
        links.append(" <strong>" + thisLetter + "</strong> ");
      } else {
        links.append("<a href='" + addParameter(link, "letter", thisLetter) + "'> " + StringUtils.toHtml(thisLetter) + " </a>");
      }
    }
    return links.toString();
  }


  /**
   * Gets the AlphebeticalPageLinks attribute of the PagedListInfo object
   *
   * @param javaScript Description of the Parameter
   * @param formName   Description of the Parameter
   * @return The AlphebeticalPageLinks value
   * @since 1.1
   */
  public String getAlphabeticalPageLinks(String javaScript, String formName) {
    StringBuffer links = new StringBuffer();
    for (int i = 0; i < lettersArray.length; i++) {
      String thisLetter = lettersArray[i];
      if (thisLetter.equals(currentLetter)) {
        links.append(" <strong>" + thisLetter + "</strong> ");
      } else {
        links.append(
            "<a href=\"javascript:" + javaScript + "('letter','" + thisLetter + "','" + formName + "');\"> " + thisLetter + " </a>");
      }
    }
    return links.toString();
  }


  /**
   * Gets the PreviousPageLink attribute of the PagedListInfo object
   *
   * @return The PreviousPageLink value
   * @since 1.1
   */
  public String getPreviousPageLink() {
    return getPreviousPageLink("&laquo;");
  }


  /**
   * Gets the PreviousPageLink attribute of the PagedListInfo object
   *
   * @param linkInfo Description of Parameter
   * @return The PreviousPageLink value
   * @since 1.8
   */
  public String getPreviousPageLink(String linkInfo) {
    return getPreviousPageLink(linkInfo, linkInfo);
  }

  public String getPreviousPageLink(String linkOn, String linkOff) {
    return getPreviousPageLink(linkOn, linkOff, "0");
  }

  /**
   * Gets the PreviousPageLink attribute of the PagedListInfo object
   *
   * @param linkOn  Description of Parameter
   * @param linkOff Description of Parameter
   * @return The PreviousPageLink value
   * @since 1.8
   */
  public String getPreviousPageLink(String linkOn, String linkOff, String formName) {
    return getPreviousPageLink(linkOn, linkOff, "0", null);
  }

  public String getPreviousPageLink(String linkOn, String linkOff, String formName, RenderResponse response) {
    StringBuffer result = new StringBuffer();
    if (getHasPreviousPageLink()) {
      int newOffset = currentOffset - getItemsPerPage();
      //Handle scroll reload
      String scrollStart = "";
      String scrollEnd = "";
      if (scrollReload) {
        scrollStart = "javascript:scrollReload('";
        scrollEnd = "');";
      }
      if (!getEnableJScript()) {
        //Normal link
        String thisLink = getLinkForOffset(newOffset, response);
        result.append(
            "<a href=\"" + scrollStart + thisLink + scrollEnd + "\">" + linkOn + "</a>");
        return result.toString();
      } else {
        //Use javascript for constructing the link
        result.append(
            "<a href=\"javascript:offsetsubmit('" + formName + "','" + (newOffset > 0 ? newOffset : 0) + "');\">" + linkOn + "</a>");
        return result.toString();
      }
    } else {
      return linkOff;
    }
  }

  public boolean getHasPreviousPageLink() {
    return currentOffset > 0 && getItemsPerPage() != -1;
  }


  /**
   * Gets the NextPageLink attribute of the PagedListInfo object
   *
   * @return The NextPageLink value
   * @since 1.1
   */
  public String getNextPageLink() {
    return getNextPageLink("&raquo;");
  }


  /**
   * Gets the NextPageLink attribute of the PagedListInfo object
   *
   * @param linkInfo Description of Parameter
   * @return The NextPageLink value
   * @since 1.8
   */
  public String getNextPageLink(String linkInfo) {
    return getNextPageLink(linkInfo, linkInfo);
  }

  public String getNextPageLink(String linkOn, String linkOff) {
    return getNextPageLink(linkOn, linkOff, "0");
  }

  /**
   * Gets the NextPageLink attribute of the PagedListInfo object
   *
   * @param linkOn  Description of Parameter
   * @param linkOff Description of Parameter
   * @return The NextPageLink value
   * @since 1.8
   */
  public String getNextPageLink(String linkOn, String linkOff, String formName) {
    return getNextPageLink(linkOn, linkOff, "0", null);
  }

  public String getNextPageLink(String linkOn, String linkOff, String formName, RenderResponse response) {
    StringBuffer result = new StringBuffer();
    if (getHasNextPageLink()) {
      //Handle scroll reload
      int newOffset = currentOffset + getItemsPerPage();
      String scrollStart = "";
      String scrollEnd = "";
      if (scrollReload) {
        scrollStart = "javascript:scrollReload('";
        scrollEnd = "');";
      }
      if (!getEnableJScript()) {
        //Normal link
        String thisLink = getLinkForOffset(newOffset, response);
        result.append(
            "<a href=\"" + scrollStart + thisLink + scrollEnd + "\">" + linkOn + "</a>");
        return result.toString();
      } else {
        //Use javascript for constructing the link
        result.append(
            "<a href=\"javascript:offsetsubmit('" + formName + "','" + (currentOffset + getItemsPerPage()) + "');\">" + linkOn + "</a>");
        return result.toString();
      }
    } else {
      return linkOff;
    }
  }

  public boolean getHasNextPageLink() {
    return (currentOffset + getItemsPerPage()) < maxRecords && getItemsPerPage() != -1;
  }


  /**
   * Gets the expandLink attribute of the PagedListInfo object
   *
   * @param expandLink   Description of the Parameter
   * @param collapseLink Description of the Parameter
   * @return The expandLink value
   */
  public String getExpandLink(String expandLink, String collapseLink) {
    if (!expandedSelection) {
      return "<a href=\"" + addParameter(link, "pagedListInfoId", this.getId()) + "&pagedListSectionId=" + this.getId() + "\">" + expandLink + "</a>";
    } else {
      return "<a href=\"" + addParameter(link, "resetList", "true") + "&pagedListInfoId=" + this.getId() + "\">" + collapseLink + "</a>";
    }
  }

  /**
   * Gets the expandLink attribute of the PagedListInfo object
   *
   * @param expandLink   Description of the Parameter
   * @param collapseLink Description of the Parameter
   * @param collapseLink Description of the Parameter
   * @return The expandLink value
   */
  public String getExpandLink(String expandLink, String collapseLink, String tmpParams) {
    if (!expandedSelection) {
      return "<a href=\"" + addParameter(link, "pagedListInfoId", this.getId()) + "&pagedListSectionId=" + this.getId() + tmpParams + "\">" + expandLink + "</a>";
    } else {
      return "<a href=\"" + addParameter(link, "resetList", "true") + "&pagedListInfoId=" + this.getId() + tmpParams + "\">" + collapseLink + "</a>";
    }
  }


  /**
   * Gets the sortIcon attribute of the PagedListInfo object
   *
   * @param columnName Description of Parameter
   * @return The sortIcon value
   */
  public String getSortIcon(String columnName) {
    if (columnName.equals(columnToSortBy)) {
      if (sortOrder != null && sortOrder.indexOf("desc") > -1) {
        return "<img border=\"0\" src=\"" + contextPath + "/images/down.gif\" align=\"bottom\" width=\"12\" height=\"10\" />";
      } else {
        return "<img border=\"0\" src=\"" + contextPath + "/images/up.gif\" align=\"bottom\" width=\"12\" height=\"10\" />";
      }
    } else {
      return "";
    }
  }


  /**
   * Gets the ListView attribute of the PagedListInfo object
   *
   * @return The ListView value
   * @since 1.11
   */
  public String getListView() {
    return listView;
  }


  /**
   * Creates the value and selected information for an HTML combo-box.<p>
   * <p/>
   * In the HTML you would have:<br>
   * <option <%= info.getOptionValue("my") %>>Text </option> <p>
   * <p/>
   * To display:<br>
   * <option value="my" selected>Text</option>
   *
   * @param tmp Description of Parameter
   * @return The OptionValue value
   * @since 1.11
   */
  public String getOptionValue(String tmp) {
    return ("value=\"" + tmp + "\"" + (tmp.equals(listView) ? " selected" : ""));
  }


  /**
   * Gets the filterOption attribute of the PagedListInfo object
   *
   * @param filterName Description of the Parameter
   * @param tmp        Description of the Parameter
   * @return The filterOption value
   */
  public String getFilterOption(String filterName, String tmp) {
    String current = listFilters.get(filterName);
    return ("value=\"" + tmp + "\"" + (tmp.equals(current) ? " selected" : ""));
  }


  /**
   * Gets the filterValue attribute of the PagedListInfo object
   *
   * @param tmp Description of Parameter
   * @return The filterValue value
   */
  public String getFilterValue(String tmp) {
    return listFilters.get(tmp);
  }


  public int getFilterValueAsInt(String tmp) {
    return Integer.parseInt(listFilters.get(tmp));
  }


  /**
   * Gets the criteriaValue attribute of the PagedListInfo object
   *
   * @param tmp Description of the Parameter
   * @return The criteriaValue value
   */
  public String getCriteriaValue(String tmp) {
    return (String) savedCriteria.get(tmp);
  }


  /**
   * Gets the filterKey attribute of the PagedListInfo object
   *
   * @param tmp Description of Parameter
   * @return The filterKey value
   */
  public int getFilterKey(String tmp) {
    try {
      return Integer.parseInt(listFilters.get(tmp));
    } catch (Exception e) {
      return -1;
    }
  }


  /**
   * Gets the refreshTag attribute of the PagedListInfo object
   *
   * @param tmp Description of the Parameter
   * @return The refreshTag value
   */
  public String getRefreshTag(String tmp, ServletRequest request) {
    String linkRefreshParameter = (String) request.getAttribute(REFRESH_PARAMETER);
    return ("<a href=\"" + link + (linkRefreshParameter != null ? linkRefreshParameter : "") + "\">" + tmp + "</a>");
  }


  /**
   * Gets the refreshTag attribute of the PagedListInfo object
   *
   * @param tmp Description of the Parameter
   * @return The refreshTag value
   */
  public String getRefreshTag(String tmp) {
    return ("<a href=\"" + link + "\"> " + tmp + " </a>");
  }


  /**
   * Adds a feature to the Filter attribute of the PagedListInfo object
   *
   * @param param The feature to be added to the Filter attribute
   * @param value The feature to be added to the Filter attribute
   */
  public void addFilter(int param, String value) {
    listFilters.put("listFilter" + param, value);
  }


  /**
   * Description of the Method
   *
   * @return Description of the Return Value
   */
  public boolean hasLink() {
    return (link != null && !"".equals(link.trim()));
  }


  /**
   * Description of the Method
   *
   * @return Description of the Returned Value
   */
  public boolean hasListFilters() {
    return listFilters.size() > 0;
  }

  public boolean hasListFilter(String tmp) {
    return (listFilters.get(tmp) != null);
  }


  /**
   * Description of the Method
   *
   * @return Description of the Returned Value
   */
  public boolean hasSortConfigured() {
    return (this.getColumnToSortBy() != null && !"".equals(this.getColumnToSortBy()));
  }


  /**
   * Description of the Method
   *
   * @return Description of the Returned Value
   */
  public boolean hasSortOrderConfigured() {
    return (this.getSortOrder() != null && !"".equals(this.getSortOrder()));
  }


  /**
   * Description of the Method
   *
   * @param db           Description of Parameter
   * @param sqlStatement Description of Parameter
   */
  public void appendSqlSelectHead(Connection db, StringBuffer sqlStatement) {
    if (DatabaseUtils.getType(db) == DatabaseUtils.MSSQL &&
        this.getItemsPerPage() > 0) {
      int x = this.getItemsPerPage() + this.getCurrentOffset();
      sqlStatement.append("SELECT TOP " + x + " ");
    } else if (DatabaseUtils.getType(db) == DatabaseUtils.ORACLE &&
        this.getItemsPerPage() > 0) {
      sqlStatement.append("SELECT * FROM (SELECT ");
    } else if (DatabaseUtils.getType(db) == DatabaseUtils.DB2 &&
        this.getItemsPerPage() > 0) {
      if (this.randomOrder) {
        sqlStatement.append("SELECT * FROM (SELECT RAND() AS RANDOM_NO, ");
      } else {
        sqlStatement.append("SELECT * FROM (SELECT ROW_NUMBER() OVER (" + orderByStatement + ") AS db_row, ");
      }
    } else if (DatabaseUtils.getType(db) == DatabaseUtils.FIREBIRD &&
        this.getItemsPerPage() > 0) {
      sqlStatement.append("SELECT FIRST " + this.getItemsPerPage() + " ");
      sqlStatement.append("SKIP " + this.getCurrentOffset() + " ");
    } else if (DatabaseUtils.getType(db) == DatabaseUtils.DAFFODILDB &&
        this.getItemsPerPage() > 0) {
      int x = this.getItemsPerPage() + this.getCurrentOffset();
      sqlStatement.append("SELECT TOP (" + x + ") ");
    } else if (DatabaseUtils.getType(db) == DatabaseUtils.INGRES &&
        this.getItemsPerPage() > 0) {
      int x = this.getItemsPerPage() + this.getCurrentOffset();
      sqlStatement.append("SELECT TOP " + x + " ");
    } else {
      sqlStatement.append("SELECT ");
    }
  }


  /**
   * Description of the Method
   *
   * @param db                   Description of Parameter
   * @param appendedSqlStatement Description of Parameter
   */
  public void appendSqlTail(Connection db, StringBuffer appendedSqlStatement) {
    StringBuffer sqlStatement = new StringBuffer();
    sqlStatement.append("ORDER BY ");
    //Determine sort order
    //If multiple columns are being sorted, then the sort order applies to all columns
    if (this.randomOrder) {

      if (DatabaseUtils.getType(db) == DatabaseUtils.POSTGRESQL) {
        sqlStatement.append(" RANDOM() ");
      } else if (DatabaseUtils.getType(db) == DatabaseUtils.INTERBASE) {
        sqlStatement.append("  "); //TODO:Need to implement
      } else if (DatabaseUtils.getType(db) == DatabaseUtils.ORACLE) {
        sqlStatement.append(" dbms_random.value "); //TODO: Needs to be tested
      } else if (DatabaseUtils.getType(db) == DatabaseUtils.DB2) {
        sqlStatement.append(" RANDOM_NO  "); //TODO: Needs to be tested
      } else if (DatabaseUtils.getType(db) == DatabaseUtils.MYSQL) {
        sqlStatement.append(" RAND() "); //TODO: Needs to be tested
      } else if (DatabaseUtils.getType(db) == DatabaseUtils.MSSQL) {
        sqlStatement.append(" NEWID() "); //TODO: Needs to be tested
      }
    } else {
      if (this.getColumnToSortBy().indexOf(",") > -1) {
        StringTokenizer st = new StringTokenizer(this.getColumnToSortBy(), ",");
        while (st.hasMoreTokens()) {
          String column = st.nextToken();
          sqlStatement.append(DatabaseUtils.parseReservedWord(db, column) + " ");
          if (this.hasSortOrderConfigured()) {
            sqlStatement.append(this.getSortOrder() + " ");
          }
          if (st.hasMoreTokens()) {
            sqlStatement.append(",");
          }
        }
      } else {
        sqlStatement.append(DatabaseUtils.parseReservedWord(db, this.getColumnToSortBy()) + " ");
        if (this.hasSortOrderConfigured()) {
          sqlStatement.append(this.getSortOrder() + " ");
        }
      }
    }
    // Keep a handle on just the order by clause for use by appendSqlHead
    orderByStatement = sqlStatement.toString();

    //Determine items per page for PostgreSQL
    if (DatabaseUtils.getType(db) == DatabaseUtils.POSTGRESQL) {
      if (this.getItemsPerPage() > 0) {
        sqlStatement.append("LIMIT " + this.getItemsPerPage() + " ");
      }
      sqlStatement.append("OFFSET " + this.getCurrentOffset() + " ");
    } else if (DatabaseUtils.getType(db) == DatabaseUtils.INTERBASE &&
        this.getItemsPerPage() > 0) {
      int startFrom = this.getCurrentOffset() + 1;
      sqlStatement.append(" ROWS " + startFrom + " TO " +
          (this.getItemsPerPage() + this.getCurrentOffset()));
    } else if (DatabaseUtils.getType(db) == DatabaseUtils.ORACLE) {
      if (this.getItemsPerPage() > 0) {
        //sqlStatement.append(") " +
        //    "WHERE ROWNUM BETWEEN " + this.getCurrentOffset() + " AND " +
        //    (this.getCurrentOffset() + this.getItemsPerPage()) + " ");
        sqlStatement.append(") " +
            "WHERE ROWNUM <= " +
            (this.getCurrentOffset() + this.getItemsPerPage()) + " ");

      }
    } else if (DatabaseUtils.getType(db) == DatabaseUtils.DB2) {
      if (this.getItemsPerPage() > 0) {
        sqlStatement.append(
            "FETCH FIRST " + (this.getItemsPerPage() + this.getCurrentOffset()) + " ROWS ONLY) AS db_row_numbers " +
                "WHERE db_row > " + this.getCurrentOffset() + " AND db_row <= " + (this.getCurrentOffset() + this.getItemsPerPage()) + " ");
      }
    } else if (DatabaseUtils.getType(db) == DatabaseUtils.MYSQL) {
      if (this.getItemsPerPage() > 0) {
        sqlStatement.append("LIMIT " + this.getCurrentOffset() + "," + this.getItemsPerPage() + " ");
      }
    }
    appendedSqlStatement.append(sqlStatement);
  }


  /**
   * Description of the Method
   *
   * @param db Description of Parameter
   * @param rs Description of Parameter
   * @throws SQLException Description of Exception
   */
  public void doManualOffset(Connection db, ResultSet rs) throws SQLException {
    if (this.getItemsPerPage() > 0) {
      DatabaseUtils.skipRowsManual(db, rs, this.getCurrentOffset());
    }
  }

  public void doManualOffset(Connection db, PreparedStatement pst) throws SQLException {
    if (this.getItemsPerPage() > 0) {
      DatabaseUtils.doManualLimit(db, pst, this.getCurrentOffset() + this.getItemsPerPage());
    }
  }

  /**
   * Description of the Method
   *
   * @return Description of the Returned Value
   * @since 1.9
   */
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("===================================================\r\n");
    sb.append("Link: " + link + "\r\n");
    sb.append("Sort Column: " + columnToSortBy + "\r\n");
    sb.append("Sort Order: " + sortOrder + "\r\n");
    sb.append("Items per page: " + getItemsPerPage() + "\r\n");
    sb.append("Total record count: " + maxRecords + "\r\n");
    sb.append("Current offset letter: " + currentLetter + "\r\n");
    sb.append("Current offset record: " + currentOffset + "\r\n");
    sb.append("Current page: " + getPage() + "\r\n");
    sb.append("List View: " + listView + "\r\n");
    return sb.toString();
  }


  /**
   * Description of the Method
   */
  private void resetList() {
    this.setCurrentLetter("");
    this.setCurrentOffset(0);
    previousOffset = 0;
  }


  /**
   * Gets the pageSize attribute of the PagedListInfo object
   *
   * @return The pageSize value
   */
  public int getPageSize() {
    if ((currentOffset + getItemsPerPage()) < maxRecords && getItemsPerPage() != -1) {
      // current = 0
      // items = 10
      // max = 17
      // 0 + 10 < 17
      return (currentOffset + getItemsPerPage());
    } else {
      // current = 10
      // items = 10
      // max = 17
      return (maxRecords);
    }
  }

  public int getPage() {
    int numberOfPages = getNumberOfPages();
    // Validate
    if (currentOffset == 0 || maxRecords == 0 || itemsPerPage == 0 || numberOfPages == 1) {
      return 1;
    }
    return (int) Math.floor(currentOffset / itemsPerPage) + 1;
  }

  public void setPage(int page) {
    setCurrentOffset(page * itemsPerPage);
  }

  private static boolean checkAllowed(String in) {
    if (in == null || in.length() == 0) {
      return true;
    }
    for (int i = 0; i < in.length(); i++) {
      if (allowed.indexOf(in.charAt(i)) == -1) {
        return false;
      }
    }
    return true;
  }

  public String addParameter(String link, String paramName, String paramValue) {
    String appender = "?";
    if (link.contains("?")) {
      appender = "&";
    }
    return link += appender + paramName + "=" + paramValue;
  }

  public boolean moreRecordsExist() {
    return (maxRecords > 0 && currentOffset + itemsPerPage < maxRecords);
  }
}

