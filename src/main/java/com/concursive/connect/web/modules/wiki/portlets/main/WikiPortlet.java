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
package com.concursive.connect.web.modules.wiki.portlets.main;

import com.concursive.connect.cms.portal.dao.ProjectItemList;
import com.concursive.connect.web.modules.wiki.beans.WikiExportBean;
import com.concursive.connect.web.modules.wiki.jobs.WikiExporterJob;
import com.concursive.connect.web.portal.AbstractPortletModule;
import com.concursive.connect.web.portal.PortalUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;

import javax.portlet.PortletRequest;
import java.util.ArrayList;
import java.util.Vector;

/**
 * Project wiki mvc portlet
 *
 * @author matt rajkowski
 * @created October 30, 2008
 */
public class WikiPortlet extends AbstractPortletModule {

  private static Log LOG = LogFactory.getLog(WikiPortlet.class);

  // Viewers
  public static final String DEFAULT_VIEW = "details";
  public static final String DETAILS_VIEW = "details";
  public static final String EDITOR_VIEW = "editor";
  public static final String SET_RATING_AJAX_VIEW = "setRating";
  public static final String SET_COMMENT_RATING_AJAX_VIEW = "comment-setRating";
  public static final String INDEX_VIEW = "index";
  public static final String VERSIONS_VIEW = "versions";
  public static final String EXPORT_VIEW = "export";
  public static final String EXPORT_QUEUE_VIEW = "queue";
  //public static final String EXPORT_CHECK_QUEUE_AJAX_VIEW = "checkQueue";
  public static final String CONFIGURE_VIEW = "configure";
  public static final String COMMENT_DELETE_VIEW = "comment-delete";
  public static final String TAGS_FORM_VIEW = "setTags";
  public static final String SEARCH_VIEW = "search";
  public static final String SEARCH_RESULTS_VIEW = "search-results";
  public static final String TABLE_OF_CONTENTS_VIEW = "tableOfContents";

  // Actions
  public static final String SAVE_WIKI_ACTION = "save";
  public static final String SAVE_WIKI_FORM_ACTION = "saveWikiForm";
  public static final String SAVE_COMMENTS_ACTION = "saveComments";
  public static final String SAVE_CONFIGURE_LISTS_ACTION = "saveConfigureLists";
  public static final String LOCK_ACTION = "lock";
  public static final String UNLOCK_ACTION = "unlock";
  public static final String EXPORT_ACTION = "export";
  public static final String DELETE_ACTION = "delete";
  //public static final String CLONE_ACTION = "clone";
  public static final String SAVE_TAGS_ACTION = "saveTags";
  public static final String SEARCH_ACTION = "search";

  // Shared variables
  public static final String QUEUE_VALUE = "queueValue";
  public static final String QUEUE_TOTAL = "queueTotal";
  public static final String EXPORTS_FOR_USER = "exportsAvailableToUser";

  public WikiPortlet() {
    defaultCommand = DEFAULT_VIEW;
  }

  protected void doPopulateActionsAndViewers() {
    // Viewers
    viewers.put(INDEX_VIEW, new WikiIndexViewer());
    viewers.put(DETAILS_VIEW, new WikiDetailsViewer());
    viewers.put(VERSIONS_VIEW, new WikiVersionsViewer());
    viewers.put(EDITOR_VIEW, new WikiEditorViewer());
    viewers.put(SET_RATING_AJAX_VIEW, new WikiSetRatingViewer());
    viewers.put(SET_COMMENT_RATING_AJAX_VIEW, new WikiCommentSetRatingViewer());
    viewers.put(EXPORT_VIEW, new WikiExportViewer());
    viewers.put(EXPORT_QUEUE_VIEW, new WikiExportQueueViewer());
    viewers.put(CONFIGURE_VIEW, new WikiConfigViewer());
    viewers.put(COMMENT_DELETE_VIEW, new WikiDeleteCommentViewer());
    viewers.put(TAGS_FORM_VIEW, new TagsFormViewer());
//    viewers.put(SEARCH_VIEW, new WikiSearchViewer());
//    viewers.put(SEARCH_RESULTS_VIEW, new WikiSearchResultsViewer());
    viewers.put(TABLE_OF_CONTENTS_VIEW, new WikiTableOfContentsViewer());

    // Actions
    actions.put(SAVE_WIKI_ACTION, new SaveWikiAction());
    actions.put(SAVE_WIKI_FORM_ACTION, new SaveWikiFormAction());
    actions.put(SAVE_COMMENTS_ACTION, new SaveWikiCommentsAction());
    actions.put(SAVE_CONFIGURE_LISTS_ACTION, new SaveWikiConfigureListsAction());
    actions.put(LOCK_ACTION, new LockWikiAction());
    actions.put(UNLOCK_ACTION, new UnlockWikiAction());
    actions.put(DELETE_ACTION, new DeleteWikiAction());
    actions.put(EXPORT_ACTION, new ExportAction());
    actions.put(SAVE_TAGS_ACTION, new SaveTagsAction());
    //actions.put(DELETE_ACTION, new DeleteWikiAction());
    //actions.put(CLONE_ACTION, new CloneWikiAction());
    //actions.put(SEARCH_ACTION, new SearchWikiAction());
  }

  // This module has the following configuration tables
  public static boolean isValidList(String table) {
    return (table.equals(ProjectItemList.WIKI_STATE) ||
        table.equals(ProjectItemList.WIKI_CATEGORIES));
  }

  public static void populateQueueStats(PortletRequest request, int userId) throws SchedulerException {
    Scheduler scheduler = PortalUtils.getScheduler(request);
    Vector<WikiExportBean> queue = (Vector) scheduler.getContext().get(WikiExporterJob.WIKI_EXPORT_ARRAY);
    Vector<WikiExportBean> available = (Vector) scheduler.getContext().get(WikiExporterJob.WIKI_AVAILABLE_ARRAY);

    // Determine where user is in queue
    int queueValue = 0;
    int queueCount = 0;
    for (WikiExportBean bean : queue) {
      ++queueCount;
      if (bean.getUserId() == userId) {
        queueValue = queueCount;
        break;
      }
    }
    int queueTotal = queue.size();
    request.setAttribute(QUEUE_VALUE, String.valueOf(queueValue));
    request.setAttribute(QUEUE_TOTAL, String.valueOf(queueTotal));

    // Provide the user with a list of their exports...
    ArrayList<WikiExportBean> exportsForUser = new ArrayList<WikiExportBean>();
    for (WikiExportBean bean : available) {
      if (bean.getUserId() == userId) {
        exportsForUser.add(bean);
      }
    }
    request.setAttribute(EXPORTS_FOR_USER, exportsForUser);
  }
}
