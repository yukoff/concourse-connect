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
package com.concursive.connect.web.modules.translation.actions;

import com.concursive.commons.web.mvc.actions.ActionContext;
import com.concursive.connect.web.controller.actions.GenericAction;
import com.concursive.connect.web.modules.contactus.dao.ContactUsBean;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.translation.beans.TranslationSearchBean;
import com.concursive.connect.web.modules.translation.dao.LanguageDictionary;
import com.concursive.connect.web.modules.translation.dao.LanguageDictionaryList;
import com.concursive.connect.web.modules.translation.dao.LanguagePack;
import com.concursive.connect.web.modules.translation.dao.LanguagePackList;
import com.concursive.connect.web.utils.PagedListInfo;

import java.sql.Connection;
import java.util.Enumeration;
import java.util.HashMap;

/**
 * Description of the Class
 *
 * @author Ananth
 * @created April 11, 2005
 */
public final class Translation extends GenericAction {

  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandDefault(ActionContext context) {
    Connection db = null;
    try {
      db = this.getConnection(context);
      LanguagePackList packList = new LanguagePackList();
      //packList.setBuildStatistics(true);
      //packList.setOrderByComplete(true);
      packList.buildList(db);
      context.getRequest().setAttribute("languagePackList", packList);
    } catch (Exception e) {
      e.printStackTrace(System.out);
      context.getRequest().setAttribute("Error", e);
      return "SystemError";
    } finally {
      this.freeConnection(context, db);
    }
    return "DefaultOK";
  }


  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandLanguage(ActionContext context) {
    Connection db = null;
    try {
      db = this.getConnection(context);
      String languageId = context.getRequest().getParameter("languageId");
      LanguagePack thisLanguage = new LanguagePack();
      thisLanguage.setBuildStatistics(true);
      thisLanguage.setBuildTeamMembers(true);
      thisLanguage.queryRecord(db, Integer.parseInt(languageId));
      context.getRequest().setAttribute("languagePack", thisLanguage);
      context.getSession().removeAttribute("dictionaryListInfo");
    } catch (Exception e) {
      e.printStackTrace(System.out);
      context.getRequest().setAttribute("Error", e);
      return "SystemError";
    } finally {
      this.freeConnection(context, db);
    }
    return "DetailsOK";
  }


  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandTranslate(ActionContext context) {
    Connection db = null;
    try {
      db = this.getConnection(context);
      String action = context.getRequest().getParameter("action");

      String languageId = context.getRequest().getParameter("languageId");
      LanguagePack thisLanguage = new LanguagePack();
      thisLanguage.setBuildTeamMembers(true);
      thisLanguage.queryRecord(db, Integer.parseInt(languageId));
      if (action != null && "Translate".equals(action)) {
        if (!thisLanguage.allowsTranslation(getUserId(context))) {
          //user does not have access to translate
          return "PermissionError";
        }
      }
      if (action != null && "Approve".equals(action)) {
        if (!thisLanguage.allowsApproval(getUserId(context))) {
          //user does not have access to approve dictionary
          return "PermissionError";
        }
      }
      context.getRequest().setAttribute("languagePack", thisLanguage);

      LanguagePack defaultLanguage = new LanguagePack();
      defaultLanguage.queryRecord(db, LanguagePack.getLanguagePackId(db, LanguagePack.DEFAULT_LOCALE));
      context.getRequest().setAttribute("defaultLanguagePack", defaultLanguage);

      PagedListInfo dictionaryListInfo = this.getPagedListInfo(context, "dictionaryListInfo");
      dictionaryListInfo.setLink(context, ctx(context) + "/Translation.do?command=Translate&languageId=" + languageId + "&action=" + action);
      dictionaryListInfo.setItemsPerPage(12);

      LanguageDictionaryList dictionaryList = new LanguageDictionaryList();
      if (context.getRequest().getAttribute("languageDictionaryList") == null) {
        dictionaryList.setLanguagePackId(Integer.parseInt(languageId));
        if (action != null && "Search".equals(action)) {
          TranslationSearchBean bean = (TranslationSearchBean) context.getFormBean();
          dictionaryList.setSearchBean(bean);
        } else {
          if (action != null && "Translate".equals(action)) {
            dictionaryList.setBuildEmptyPhrasesOnly(true);
          } else {
            dictionaryList.setBuildTranslatedPhrasesOnly(true);
          }
          if (action != null && "Approve".equals(action)) {
            dictionaryList.setIgnoreApproved(true);
            dictionaryList.setApprovedAfter(getUser(context).getLastLogin());
          }
        }
        dictionaryList.setPagedListInfo(dictionaryListInfo);
        dictionaryList.setBuildDefaultValue(true);
        dictionaryList.buildList(db);
      } else {
        //There was an error, so reset the pagedListInfo to the previous page
        dictionaryList = (LanguageDictionaryList) context.getRequest().getAttribute("languageDictionaryList");
        dictionaryListInfo.setCurrentOffset(dictionaryListInfo.getCurrentOffset() - dictionaryListInfo.getItemsPerPage());
      }
      context.getRequest().setAttribute("languageDictionaryList", dictionaryList);
    } catch (Exception e) {
      e.printStackTrace(System.out);
      context.getRequest().setAttribute("Error", e);
      return "SystemError";
    } finally {
      this.freeConnection(context, db);
    }
    return "TranslateOK";
  }


  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandUpdate(ActionContext context) {
    Connection db = null;
    try {
      db = this.getConnection(context);
      LanguageDictionaryList dictionaryList = new LanguageDictionaryList();
      Enumeration e = context.getRequest().getParameterNames();
      boolean valid = true;
      while (e.hasMoreElements()) {
        String param = (String) e.nextElement();
        if (param.startsWith("paramId")) {
          String id = context.getRequest().getParameter(param);
          String value = context.getRequest().getParameter("paramValue" + id);

          //System.out.println("ID: " + id + ", VALUE: " + value);
          String approved = context.getRequest().getParameter("approved" + id);
          LanguageDictionary dictionaryItem = new LanguageDictionary();
          dictionaryItem.setBuildDefaultValue(true);
          dictionaryItem.queryRecord(db, Integer.parseInt(id));
          dictionaryItem.setParamValue1(value);
          if ("Approve".equals(context.getRequest().getParameter("source"))) {
            dictionaryItem.setApproved(approved);
          }
          //dictionaryList.put(dictionaryItem.getParamName(), dictionaryItem);
          dictionaryList.add(dictionaryItem);
          if (!dictionaryItem.isValid()) {
            valid = false;
          }
        }
      }
      if (!valid) {
        context.getRequest().setAttribute("languageDictionaryList", dictionaryList);
        return (executeCommandTranslate(context));
      } else {
        String languageId = context.getRequest().getParameter("languageId");
        dictionaryList.setLanguagePackId(languageId);
        dictionaryList.setModifiedBy(getUserId(context));
        dictionaryList.update(db);
      }
    } catch (Exception e) {
      e.printStackTrace(System.out);
      context.getRequest().setAttribute("Error", e);
      return "SystemError";
    } finally {
      this.freeConnection(context, db);
    }
    return (executeCommandTranslate(context));
  }


  public String executeCommandContext(ActionContext context) {
    Connection db = null;
    try {
      int languageId = Integer.parseInt(context.getRequest().getParameter("languageId"));
      int dictionaryId = Integer.parseInt(context.getRequest().getParameter("id"));

      db = this.getConnection(context);

      LanguagePack thisLanguage = new LanguagePack();
      thisLanguage.setBuildTeamMembers(true);
      thisLanguage.queryRecord(db, languageId);
      context.getRequest().setAttribute("languagePack", thisLanguage);

      LanguagePack defaultLanguage = new LanguagePack();
      defaultLanguage.queryRecord(db, LanguagePack.getLanguagePackId(db, LanguagePack.DEFAULT_LOCALE));
      context.getRequest().setAttribute("defaultLanguagePack", defaultLanguage);

      LanguageDictionary dictionaryItem = new LanguageDictionary(db, dictionaryId);
      context.getRequest().setAttribute("dictionaryItem", dictionaryItem);

      int defaultId = LanguageDictionaryList.queryDefaultId(db, dictionaryId);
      LanguageDictionary defaultDictionaryItem = new LanguageDictionary(db, defaultId);
      context.getRequest().setAttribute("defaultDictionaryItem", defaultDictionaryItem);


    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return "SystemError";
    } finally {
      this.freeConnection(context, db);
    }
    return "ContextOK";
  }


  public String executeCommandApply(ActionContext context) {
    // If logged in, get the user's info
    if (getUserId(context) > -1) {
      User thisUser = getUser(context);
      if (thisUser != null) {
        ContactUsBean bean = (ContactUsBean) context.getFormBean();
        bean.setNameFirst(thisUser.getFirstName());
        bean.setNameLast(thisUser.getLastName());
        bean.setEmail(thisUser.getEmail());
        bean.setOrganization(thisUser.getCompany());
      }
    }
    // Show the contact us form
    return "ApplicationFormOK";
  }


  public String executeCommandSubmitApplication(ActionContext context) {

    ContactUsBean bean = (ContactUsBean) context.getFormBean();
    bean.isValid(context.getSession());

    HashMap errors = bean.getError();

    if (bean.getNameFirst() == null || bean.getNameFirst().trim().length() == 0) {
      errors.put("nameFirstError", "Required field");
    }

    if (bean.getNameLast() == null || bean.getNameLast().trim().length() == 0) {
      errors.put("nameLastError", "Required field");
    }

    if (bean.getEmail() == null || bean.getEmail().trim().length() == 0) {
      errors.put("emailError", "Required field");
    }

    if (bean.getDescription() == null || bean.getDescription().trim().length() == 0) {
      errors.put("descriptionError", "Required field");
    }

    if (errors.isEmpty()) {
      Connection db = null;
      try {
        db = getConnection(context);
        bean.setEmailCopy(true);
        bean.setInstanceId(getInstance(context).getId());
        bean.save(context, db);
      } catch (Exception e) {
        e.printStackTrace(System.out);
      } finally {
        freeConnection(context, db);
      }
      if (bean.getId() > -1) {
        return "SendApplicationOK";
      }
    }

    processErrors(context, errors);
    return "SendApplicationERROR";
  }


  public String executeCommandSearchForm(ActionContext context) {
    Connection db = null;
    try {
      db = this.getConnection(context);
      String languageId = context.getRequest().getParameter("languageId");
      LanguagePack thisLanguage = new LanguagePack(db, Integer.parseInt(languageId));
      context.getRequest().setAttribute("languagePack", thisLanguage);

      LanguagePack defaultLanguage = new LanguagePack();
      defaultLanguage.queryRecord(db, LanguagePack.getLanguagePackId(db, LanguagePack.DEFAULT_LOCALE));
      context.getRequest().setAttribute("defaultLanguagePack", defaultLanguage);

      context.getSession().removeAttribute("dictionaryListInfo");
    } catch (Exception e) {
      e.printStackTrace(System.out);
      context.getRequest().setAttribute("Error", e);
      return "SystemError";
    } finally {
      this.freeConnection(context, db);
    }
    return "SearchFormOK";
  }


}

