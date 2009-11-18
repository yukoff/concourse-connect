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
package com.concursive.connect.web.modules.calendar.portlets.main;

import com.concursive.connect.web.portal.AbstractPortletModule;

/**
 * Project Calendar mvc portlet
 *
 * @author matt rajkowski
 * @created November 7, 2008
 */
public class CalendarPortlet extends AbstractPortletModule {

  // Viewers
  public static final String CALENDAR_VIEW = "calendar";
  public static final String EVENTS_VIEW = "events";
  public static final String EVENT_DETAILS_VIEW = "details";
  public static final String EVENT_FORM_VIEW = "form";
  public static final String SET_EVENT_RATING_AJAX_VIEW = "event-setRating";
  public static final String TAGS_FORM_VIEW = "setTags";
  
  // Default viewer
  public static final String DEFAULT_VIEW = CALENDAR_VIEW;
  
  // Actions
  public static final String SAVE_EVENT_FORM_ACTION = "saveForm";
  public static final String DELETE_EVENT_ACTION = "delete";
  public static final String CLONE_EVENT_ACTION = "clone";
  public static final String DIMDIM_EVENT_ACTION = "dimdimAction";
  public static final String EVENT_INVITEES_CONFIRM_ACTION = "saveInvitees";
  public static final String SAVE_EVENT_JOIN_ACTION = "saveJoinEvent";
  public static final String SAVE_TAGS_ACTION = "saveTags";

  public CalendarPortlet() {
    defaultCommand = DEFAULT_VIEW;
  }

  protected void doPopulateActionsAndViewers() {
    // Viewers
    viewers.put(EVENTS_VIEW, new EventsViewer());
    viewers.put(CALENDAR_VIEW, new CalendarViewer());
    viewers.put(EVENT_DETAILS_VIEW, new EventDetailsViewer());
    viewers.put(EVENT_FORM_VIEW, new EventFormViewer());
    viewers.put(SET_EVENT_RATING_AJAX_VIEW, new EventSetInappropriateViewer());
    viewers.put(TAGS_FORM_VIEW, new TagsFormViewer());

    // Actions
    actions.put(SAVE_EVENT_FORM_ACTION, new SaveEventAction());
    actions.put(EVENT_INVITEES_CONFIRM_ACTION, new SaveEventInviteesAction());
    actions.put(DELETE_EVENT_ACTION, new DeleteEventAction());
    actions.put(DIMDIM_EVENT_ACTION, new EventDimdimAction());
    actions.put(SAVE_EVENT_JOIN_ACTION, new SaveEventJoinAction());
    actions.put(SAVE_TAGS_ACTION, new SaveTagsAction());
//    actions.put(CLONE_EVENT_ACTION, new CloneEventAction());
  }
}
