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

package com.concursive.connect.web.modules.contacts.actions;

import com.concursive.commons.web.mvc.actions.ActionContext;
import com.concursive.connect.Constants;
import com.concursive.connect.web.controller.actions.GenericAction;
import com.concursive.connect.web.modules.contacts.dao.Contact;
import com.concursive.connect.web.modules.contacts.dao.ContactList;

import java.sql.Connection;

/**
 * Actions for generating allocation
 *
 * @author matt rajkowski
 * @version $Id$
 * @created March 16, 2007
 */
public final class Contacts extends GenericAction {

  public String executeCommandAdd(ActionContext context) {
    if (getUser(context).getId() < 0) {
      return "PermissionError";
    }
    return "FormOK";
  }

  public String executeCommandDetails(ActionContext context) {
    if (getUser(context).getId() < 0) {
      return "PermissionError";
    }
    Connection db = null;
    try {
      int contactId = Integer.parseInt(context.getRequest().getParameter("contactId"));
      db = getConnection(context);
      // Use the rules of the class to retrieve the contact
      ContactList contactList = new ContactList();
      contactList.setContactId(contactId);
      contactList.setForUser(getUserId(context));
      if (getUser(context).getAccessViewAllContacts()) {
        contactList.setIncludeAllGlobal(Constants.TRUE);
      }
      contactList.buildList(db);
      Contact thisContact = (Contact) contactList.get(0);
      context.getRequest().setAttribute("contact", thisContact);
    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return ("SystemError");
    } finally {
      freeConnection(context, db);
    }
    return "DetailsOK";
  }

  public String executeCommandModify(ActionContext context) {
    if (getUser(context).getId() < 0) {
      return "PermissionError";
    }
    Connection db = null;
    try {
      int contactId = Integer.parseInt(context.getRequest().getParameter("contactId"));
      db = getConnection(context);
      // Use the rules of the class to retrieve the contact
      ContactList contactList = new ContactList();
      contactList.setContactId(contactId);
      contactList.setForUser(getUserId(context));
      if (getUser(context).getAccessViewAllContacts()) {
        contactList.setIncludeAllGlobal(Constants.TRUE);
      }
      contactList.buildList(db);
      Contact thisContact = (Contact) contactList.get(0);
      context.getRequest().setAttribute("contact", thisContact);
    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return ("SystemError");
    } finally {
      freeConnection(context, db);
    }
    return "FormOK";
  }

  public String executeCommandSave(ActionContext context) {
    if (getUser(context).getId() < 0) {
      return "PermissionError";
    }
    Connection db = null;
    try {
      Contact contact = (Contact) context.getFormBean();
      db = getConnection(context);
      if (contact.getId() == -1) {
        // Insert Contact
        contact.setOwner(getUserId(context));
        contact.setGlobal(false);
        contact.setEnteredBy(getUserId(context));
        contact.setModifiedBy(getUserId(context));
        contact.insert(db);
      } else {
        // Update Contact (if user has access to this contact)
        ContactList contactList = new ContactList();
        contactList.setContactId(contact.getId());
        contactList.setForUser(getUserId(context));
        if (getUser(context).getAccessViewAllContacts()) {
          contactList.setIncludeAllGlobal(Constants.TRUE);
        }
        contactList.buildList(db);
        Contact previousContact = (Contact) contactList.get(0);
        if (previousContact.getId() == contact.getId()) {
          contact.setOwner(previousContact.getOwner());
          contact.setGlobal(previousContact.getGlobal());
          contact.setEnteredBy(previousContact.getEnteredBy());
          contact.setModifiedBy(getUserId(context));
          contact.update(db);
        }
      }
      context.getRequest().setAttribute("contactId", String.valueOf(contact.getId()));
    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return ("SystemError");
    } finally {
      freeConnection(context, db);
    }
    return "SaveOK";
  }

  public String executeCommandDelete(ActionContext context) {
    if (getUser(context).getId() < 0) {
      return "PermissionError";
    }
    Connection db = null;
    try {
      int contactId = Integer.parseInt(context.getRequest().getParameter("contactId"));
      db = getConnection(context);
      // Use the rules of the class to retrieve the contact
      ContactList contactList = new ContactList();
      contactList.setContactId(contactId);
      contactList.setForUser(getUserId(context));
      if (getUser(context).getAccessViewAllContacts()) {
        contactList.setIncludeAllGlobal(Constants.TRUE);
      }
      contactList.buildList(db);
      Contact thisContact = (Contact) contactList.get(0);
      thisContact.delete(db);
      context.getRequest().setAttribute("contactId", String.valueOf(thisContact.getId()));
    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return ("SystemError");
    } finally {
      freeConnection(context, db);
    }
    return "DeleteOK";
  }
}
