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
import com.concursive.connect.web.modules.wiki.dao.*;
import com.concursive.connect.web.utils.LookupList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.StringReader;
import java.util.Map;

/**
 * Methods for working with custom wiki forms
 *
 * @author matt rajkowski
 * @created Jun 4, 2008
 */
public class CustomFormUtils {

  // Logger
  private static Log LOG = LogFactory.getLog(CustomFormUtils.class);

  /**
   * Returns the named form from the wiki
   *
   * @param wiki
   * @param formName
   * @return
   * @throws Exception
   */
  public static CustomForm retrieveForm(Wiki wiki, String formName) throws Exception {
    if (wiki == null || formName == null || !StringUtils.hasText(wiki.getContent())) {
      return null;
    }
    BufferedReader in = new BufferedReader(new StringReader(wiki.getContent()));
    String line = null;
    while ((line = in.readLine()) != null && !line.startsWith("[{form name=\"" + formName + "\"}]")) {
      // Looking for the start
    }
    if (line != null) {
      CustomForm form = WikiToHTMLUtils.retrieveForm(in, line);
      if (form != null) {
        if (formName.equals(form.getName())) {
          return form;
        }
      }
    }
    return null;
  }

  /**
   * Generates a CustomForm object from the provided wiki form content fragment
   * 
   * @param content
   * @return
   * @throws Exception
   */
  public static CustomForm createForm(String content) throws Exception {
    if (content == null) {
      LOG.debug("Content is NULL");
      return null;
    }
    BufferedReader in = new BufferedReader(new StringReader(content));
    String line = null;
    while ((line = in.readLine()) != null && !line.contains("[{form name=\"")) {
      // Looking for the start
    }
    if (line != null) {
      LOG.debug("Form line: " + line);
      CustomForm form = WikiToHTMLUtils.retrieveForm(in, line);
      if (form != null) {
        return form;
      }
    }
    return null;
  }

  /**
   * Updates a wiki with the entered values of the form presented to a wiki user
   *
   * @param wiki
   * @param request
   * @throws Exception
   */
  public static void populateForm(Wiki wiki, HttpServletRequest request) throws Exception {
    wiki.setContent(populateForm(wiki.getContent(), request.getParameterMap()));
  }

  public static void populateForm(Wiki wiki, PortletRequest request) throws Exception {
    wiki.setContent(populateForm(wiki.getContent(), request.getParameterMap()));
  }

  /**
   * Creates the wiki content when creating a new wiki from a template that has a form
   *
   * @param template
   * @param request
   * @return
   * @throws Exception
   */
  public static String populateForm(WikiTemplate template, HttpServletRequest request) throws Exception {
    return populateForm(template.getContent(), request.getParameterMap());
  }

  public static String populateForm(WikiTemplate template, PortletRequest request) throws Exception {
    return populateForm(template.getContent(), request.getParameterMap());
  }

  public static String populateForm(String wikiContent, Map parameterMap) throws Exception {
    BufferedReader in = new BufferedReader(new StringReader(wikiContent));
    String line = null;
    while ((line = in.readLine()) != null && !line.contains("[{form")) {
      // Keep looking
    }
    // Let the Wiki utils create the form objects
    CustomForm form = WikiToHTMLUtils.retrieveForm(in, line);
    if (form != null) {
      for (CustomFormGroup group : form) {
        for (CustomFormField field : group) {
          // Convert parameter to a string
          String requestValue = null;
          LOG.debug("Getting requestValues for field: " + field.getName());
          Object requestValues = parameterMap.get(field.getName());
          if (requestValues == null) {
            requestValue = null;
          } else if (requestValues instanceof String) {
            requestValue = (String) requestValues;
          } else {
            requestValue = ((String[]) requestValues)[0];
          }
          // Handle some HTML Form specific issues here...
          if (field.getType() == CustomFormField.CHECKBOX) {
            if (requestValue == null) {
              requestValue = "false";
            } else {
              requestValue = "true";
            }
          } else if (field.getType() == CustomFormField.SELECT) {
            if (StringUtils.isNumber(requestValue)) {
              LookupList lookupList = field.getLookupList();
              requestValue = lookupList.getValueFromId(requestValue);
            }
          } else if (field.getType() == CustomFormField.TEXTAREA) {
            // handle multi-line parameters
            requestValue = StringUtils.replaceReturns(requestValue, "^");
          } else if (field.getType() == CustomFormField.CURRENCY) {
            // Convert parameter to a string
            String currencyValue = null;
            Object currencyValues = parameterMap.get(field.getName() + "Currency");
            if (currencyValues instanceof String) {
              currencyValue = (String) currencyValues;
            } else {
              currencyValue = ((String[]) currencyValues)[0];
            }
            if (currencyValue != null) {
              field.setValueCurrency(currencyValue);
            }
          }
          field.setValue(requestValue);
        }
      }
    }
    // Now integrate the form entries back into the wiki entry
    int startForm = wikiContent.indexOf("[{form");
    int endForm = wikiContent.indexOf("+++", startForm) + 3;
    StringBuffer sb = new StringBuffer();
    sb.append(wikiContent.substring(0, startForm));
    HTMLToWikiUtils.convertFormToWiki(form, sb);
    sb.append(wikiContent.substring(endForm));
    return sb.toString();
  }
}
