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

package com.concursive.connect.web.modules.reports.dao;

import com.concursive.commons.date.DateUtils;
import com.concursive.commons.text.StringUtils;
import com.concursive.commons.text.Template;
import com.concursive.connect.web.modules.login.utils.UserUtils;
import com.concursive.connect.web.utils.HtmlSelectProbabilityRange;
import com.concursive.connect.web.utils.PagedListInfo;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperReport;

import javax.servlet.http.HttpServletRequest;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * A collection of Parameter objects.
 *
 * @author matt rajkowski
 * @created September 15, 2003
 */
public class ParameterList extends ArrayList<Parameter> {

  private int criteriaId = -1;
  protected PagedListInfo pagedListInfo = null;
  public HashMap<String, String> errors = new HashMap<String, String>();


  /**
   * Constructor for the ParameterList object
   */
  public ParameterList() {
  }


  /**
   * Sets the criteriaId attribute of the ParameterList object
   *
   * @param tmp The new criteriaId value
   */
  public void setCriteriaId(int tmp) {
    this.criteriaId = tmp;
  }


  /**
   * Sets the criteriaId attribute of the ParameterList object
   *
   * @param tmp The new criteriaId value
   */
  public void setCriteriaId(String tmp) {
    this.criteriaId = Integer.parseInt(tmp);
  }


  /**
   * Gets the criteriaId attribute of the ParameterList object
   *
   * @return The criteriaId value
   */
  public int getCriteriaId() {
    return criteriaId;
  }


  /**
   * Sets the errors attribute of the ParameterList object
   *
   * @param tmp The new errors value
   */
  public void setErrors(HashMap<String, String> tmp) {
    this.errors = tmp;
  }


  /**
   * Gets the errors attribute of the ParameterList object
   *
   * @return The errors value
   */
  public HashMap getErrors() {
    return errors;
  }


  /**
   * Description of the Method
   *
   * @return Description of the Return Value
   */
  public boolean hasErrors() {
    return (errors.size() > 0);
  }


  /**
   * Initially loads the parameters from the JasperReport
   *
   * @param report The new parameters value
   */
  public void setParameters(JasperReport report) {
    JRParameter[] params = report.getParameters();
    for (JRParameter param : params) {
      Parameter thisParam = new Parameter();
      thisParam.setParam(param);
      if (!thisParam.getIsSystemDefined()) {
        this.add(thisParam);
      }
    }
  }


  /**
   * Takes the user input and expands the data into the corresponding jasper
   * parameters
   *
   * @param request The new parameters value
   * @return Description of the Return Value
   */
  public boolean setParameters(HttpServletRequest request) {
    Timestamp startDate = null;
    Timestamp endDate = null;
    for (Parameter param : this) {
      //For each parameter the user is prompted for, evaluate the answer
      if (param.getIsForPrompting()) {
        param.setValue(request.getParameter(param.getName()));
        //Auto-populate the user's range based on selected type
        if (param.getName().equals("userid_range_source")) {
          if (param.getValue().equals("all")) {
            //TODO:"All" wouldn't work, need to swap the _where clause
            addParam("userid_range", "");
          } else if (param.getValue().equals("hierarchy")) {
            addParam("userid_range", UserUtils.getUserIdRange(request));
          } else {
            addParam(
                "userid_range", String.valueOf(UserUtils.getUserId(request)));
          }
        }
        //Lookup lists will result in -1, > -1
        //if other than -1 then use the additional WHERE clause included in
        //the report
        if (param.getName().startsWith("lookup_")) {
          Parameter whereParam = this.getParameter(param.getName() + "_where");
          if (whereParam != null) {
            if (!"-1".equals(param.getValue())) {
              //New case, replace query param with another param and parse
              Template where = new Template(whereParam.getDescription());
              where.addParseElement(
                  "$P{" + param.getName() + "}", param.getValue());
              addParam(whereParam.getName(), where.getParsedText());
            } else {
              addParam(whereParam.getName(), " ");
            }
          }
          addParam(param.getName(), param.getValue());
        }
        //HtmlSelect boolean
        if (param.getName().startsWith("boolean_")) {
          Parameter whereParam = this.getParameter(param.getName() + "_where");
          if (whereParam != null) {
            if ("1".equals(param.getValue())) {
              //Replace the default with the description value
              addParam(whereParam.getName(), whereParam.getDescription());
            } else if ("0".equals(param.getValue())) {
              //Leave the default
            } else {
              //Blank out the default
              addParam(whereParam.getName(), "");
            }
          }
          addParam(param.getName(), param.getValue());
        }
        if (param.getName().startsWith("yesno_")) {
          Parameter whereParam = this.getParameter(param.getName() + "_where");
          if (whereParam != null) {
            if ("1".equals(param.getValue())) {
              //Replace the default with the description value
              addParam(whereParam.getName(), whereParam.getDescription());
            } else if ("0".equals(param.getValue())) {
              //Leave the default
            } else {
              //Blank out the default
              addParam(whereParam.getName(), "");
            }
          }
          addParam(param.getName(), param.getValue());
        }
        if (param.getName().startsWith("number_")) {
          try {
            addParam(param.getName(), String.valueOf(Integer.parseInt(param.getValue())));
          } catch (Exception e) {
            addParam(param.getName(), "0");
          }
        }
        //Percent lookup uses a range from the HtmlSelectProbabilityRange object
        if (param.getName().startsWith("percent_")) {
          //The range will be specified as -0.01|1.01 for the query
          StringTokenizer st = new StringTokenizer(param.getValue(), "|");
          addParam(param.getName() + "_min", st.nextToken());
          addParam(param.getName() + "_max", st.nextToken());
          //Clean up the name for output on the report
          param.setValue(
              HtmlSelectProbabilityRange.getValueFromId(param.getValue()));
        }
        //Integer orgid lookup
        if (param.getName().startsWith("orgid")) {
          Parameter whereParam = this.getParameter(param.getName() + "_where");
          if (whereParam != null) {
            if (!"-1".equals(param.getValue())) {
              //New case, replace query param with another param and parse
              Template where = new Template(whereParam.getDescription());
              where.addParseElement(
                  "$P{" + param.getName() + "}", param.getValue());
              addParam(whereParam.getName(), where.getParsedText());
            } else {
              addParam(whereParam.getName(), " ");
            }
          }
          addParam(param.getName(), param.getValue());
        }
        //Where clause for text fields
        if (param.getName().startsWith("text_")) {
          Parameter whereParam = this.getParameter(param.getName() + "_where");
          if (whereParam != null) {
            if (!"".equals(param.getValue())) {
              //New case, replace query param with another param and parse
              Template where = new Template(whereParam.getDescription());
              where.addParseElement("$P{" + param.getName() + "}", param.getValue());
              addParam(whereParam.getName(), where.getParsedText());
            } else {
              addParam(whereParam.getName(), " ");
            }
          }
          addParam(param.getName(), param.getValue());
        }
        try {
          if (param.getName().startsWith("date_")) {
            if (StringUtils.hasText(param.getValue())) {
              Timestamp tmpTimestamp = DateUtils.getUserToServerDateTime(
                  TimeZone.getTimeZone(UserUtils.getUserTimeZone(request)), DateFormat.SHORT, DateFormat.LONG, param.getValue(), UserUtils.getUserLocale(request));
              SimpleDateFormat formatter = (SimpleDateFormat) SimpleDateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());
              String date = formatter.format(tmpTimestamp);
              param.setValue(date);
              if (param.getName().equals("date_start")) {
                startDate = tmpTimestamp;
              }
              if (param.getName().equals("date_end")) {
                endDate = tmpTimestamp;
              }
            }
            // Where clause for date fields
            Parameter whereParam = this.getParameter(param.getName() + "_where");
            if (whereParam != null) {
              if (StringUtils.hasText(param.getValue())) {
                // New case, replace query param with another param and parse (use the calculated value)
                Template where = new Template(whereParam.getDescription());
                where.addParseElement("$P{" + param.getName() + "}", param.getValue());
                addParam(whereParam.getName(), where.getParsedText());
              } else {
                addParam(whereParam.getName(), " ");
              }
            }
          }
        } catch (Exception e) {
          errors.put(param.getName() + "Error", "no input or invalid date");
        }
      }
      if (System.getProperty("DEBUG") != null) {
        System.out.println(
            "ParameterList-> " + param.getName() + "=" + param.getValue());
      }
    }
    if (startDate != null && endDate != null) {
      if (startDate.after(endDate)) {
        errors.put(
            "date_startError", "The first date cannot be after second date.");
      }
    }
    this.addParam("currency", UserUtils.getUserCurrency(request));
    this.addParam("country", UserUtils.getUserLocale(request).getCountry());
    this.addParam("language", UserUtils.getUserLocale(request).getLanguage());
    this.addParam("userid", String.valueOf(UserUtils.getUserId(request)));
    return !hasErrors();
  }


  /**
   * Builds a list of Parameter objects based on the filter properties of this
   * list object.
   *
   * @param db Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public void buildList(Connection db) throws SQLException {
    PreparedStatement pst = null;
    ResultSet rs = null;
    int items = -1;
    StringBuffer sqlSelect = new StringBuffer();
    StringBuffer sqlCount = new StringBuffer();
    StringBuffer sqlFilter = new StringBuffer();
    StringBuffer sqlOrder = new StringBuffer();
    //Need to build a base SQL statement for counting records
    sqlCount.append(
        "SELECT COUNT(*) as recordcount " +
            "FROM report_criteria_parameter p " +
            "WHERE parameter_id > -1 ");
    createFilter(sqlFilter);
    if (pagedListInfo != null) {
      //Get the total number of records matching filter
      pst = db.prepareStatement(sqlCount.toString() + sqlFilter.toString());
      items = prepareFilter(pst);
      rs = pst.executeQuery();
      if (rs.next()) {
        int maxRecords = rs.getInt("recordcount");
        pagedListInfo.setMaxRecords(maxRecords);
      }
      rs.close();
      pst.close();
      //Determine column to sort by
      pagedListInfo.setDefaultSort("parameter_id", null);
      pagedListInfo.appendSqlTail(db, sqlOrder);
    } else {
      sqlOrder.append("ORDER BY parameter_id ");
    }

    //Need to build a base SQL statement for returning records
    if (pagedListInfo != null) {
      pagedListInfo.appendSqlSelectHead(db, sqlSelect);
    } else {
      sqlSelect.append("SELECT ");
    }
    sqlSelect.append(
        "p.* " +
            "FROM report_criteria_parameter p " +
            "WHERE parameter_id > -1 ");
    pst = db.prepareStatement(
        sqlSelect.toString() + sqlFilter.toString() + sqlOrder.toString());
    items = prepareFilter(pst);
    rs = pst.executeQuery();
    if (pagedListInfo != null) {
      pagedListInfo.doManualOffset(db, rs);
    }
    while (rs.next()) {
      Parameter thisParameter = new Parameter(rs);
      this.add(thisParameter);
    }
    rs.close();
    pst.close();
  }


  /**
   * Defines additional parameters to be added to the query
   *
   * @param sqlFilter Description of the Parameter
   */
  protected void createFilter(StringBuffer sqlFilter) {
    if (sqlFilter == null) {
      sqlFilter = new StringBuffer();
    }
    if (criteriaId != -1) {
      sqlFilter.append("AND p.criteria_id = ? ");
    }
  }


  /**
   * Populates the additional parameters that have been added to the query
   *
   * @param pst Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  protected int prepareFilter(PreparedStatement pst) throws SQLException {
    int i = 0;
    if (criteriaId != -1) {
      pst.setInt(++i, criteriaId);
    }
    return i;
  }


  /**
   * Inserts all of the parameters contained in this object to the database
   *
   * @param db Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public void insert(Connection db) throws SQLException {
    for (Parameter thisParameter : this) {
      thisParameter.setCriteriaId(criteriaId);
      thisParameter.insert(db);
    }
  }


  /**
   * Updates all of the parameters contained in this object against the
   * database
   *
   * @param db Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public void update(Connection db) throws SQLException {
    //Delete all parameters already saved for this criteriaId
    delete(db);
    //Insert the new parameters
    for (Parameter thisParameter : this) {
      thisParameter.setCriteriaId(criteriaId);
      thisParameter.insert(db);
    }
  }


  /**
   * Deletes all of the parameters contained in this object from the database
   *
   * @param db Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public void delete(Connection db) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "DELETE FROM report_criteria_parameter " +
            "WHERE criteria_id = ? ");
    pst.setInt(1, criteriaId);
    pst.execute();
    pst.close();
  }


  /**
   * Adds a feature to the Param attribute of the ParameterList object
   *
   * @param param The feature to be added to the Param attribute
   * @param value The feature to be added to the Param attribute
   * @return Description of the Return Value
   */
  public boolean addParam(String param, String value) {
    for (Parameter thisParameter : this) {
      if (param.equals(thisParameter.getName())) {
        thisParameter.setValue(value);
        return true;
      }
    }
    return false;
  }


  /**
   * Gets the displayValues attribute of the ParameterList object
   *
   * @return The displayValues value
   */
  public String getDisplayValues() {
    StringBuffer sb = new StringBuffer();
    for (Parameter thisParameter : this) {
      if (thisParameter.getIsForPrompting()) {
        if (sb.length() > 0) {
          sb.append(", ");
        }
        sb.append(
            thisParameter.getDescription()).append(
            "=").append(
            thisParameter.getValue());
      }
    }
    return sb.toString();
  }


  /**
   * Gets the classValue attribute of the ParameterList object
   *
   * @param param Description of the Parameter
   * @return The valueClass value
   */
  public java.lang.Class getValueClass(String param) {
    for (Parameter thisParam : this) {
      if (param.equals(thisParam.getName())) {
        return thisParam.getValueClass();
      }
    }
    return null;
  }


  /**
   * Gets the parameter attribute of the ParameterList object
   *
   * @param param Description of the Parameter
   * @return The parameter value
   */
  public Parameter getParameter(String param) {
    for (Parameter thisParam : this) {
      if (param.equals(thisParam.getName())) {
        return thisParam;
      }
    }
    return null;
  }

  public int getValueAsInt(String param) {
    for (Parameter thisParam : this) {
      if (param.equals(thisParam.getName())) {
        return thisParam.getValueAsInt();
      }
    }
    return -1;
  }
}


