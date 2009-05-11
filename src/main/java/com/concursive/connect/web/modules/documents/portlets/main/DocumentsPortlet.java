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
package com.concursive.connect.web.modules.documents.portlets.main;

import com.concursive.connect.web.portal.AbstractPortletModule;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Project Documents mvc portlet
 *
 * @author matt rajkowski
 * @created November 7, 2008
 */
public class DocumentsPortlet extends AbstractPortletModule {

  private static Log LOG = LogFactory.getLog(DocumentsPortlet.class);

  // Viewers
  public static final String LIST_VIEW = "list";
  public static final String DETAILS_VIEW = "details";
  public static final String FILE_FORM_VIEW = "fileForm";
  public static final String FOLDER_FORM_VIEW = "folderForm";
  public static final String MOVE_FOLDER_VIEW = "moveFolder";
  public static final String MOVE_FILE_VIEW = "moveFile";
  public static final String SET_RATING_AJAX_VIEW = "setRating";

  // Set the default view
  public static final String DEFAULT_VIEW = LIST_VIEW;

  // Actions
  public static final String SAVE_FOLDER_ACTION = "saveFolder";
  public static final String SAVE_FILE_ACTION = "saveFile";
  public static final String DELETE_FOLDER_ACTION = "deleteFolder";
  public static final String DELETE_FILE_ACTION = "deleteFile";
  public static final String MOVE_FOLDER_ACTION = "moveFolder";
  public static final String MOVE_FILE_ACTION = "moveFile";

  public DocumentsPortlet() {
    defaultCommand = DEFAULT_VIEW;
  }

  protected void doPopulateActionsAndViewers() {
    // Viewers
    viewers.put(LIST_VIEW, new DocumentsListViewer());
    viewers.put(DETAILS_VIEW, new DocumentsDetailsViewer());
    viewers.put(FILE_FORM_VIEW, new DocumentsFileFormViewer());
    viewers.put(FOLDER_FORM_VIEW, new DocumentsFolderFormViewer());
    viewers.put(MOVE_FOLDER_VIEW, new DocumentsMoveFolderViewer());
    viewers.put(MOVE_FILE_VIEW, new DocumentsMoveFileViewer());
    viewers.put(SET_RATING_AJAX_VIEW, new DocumentSetInappropriateViewer());

    // Actions
    actions.put(SAVE_FOLDER_ACTION, new SaveFolderAction());
    actions.put(SAVE_FILE_ACTION, new SaveFileAction());
    actions.put(DELETE_FOLDER_ACTION, new DeleteFolderAction());
    actions.put(DELETE_FILE_ACTION, new DeleteFileAction());
    actions.put(MOVE_FOLDER_ACTION, new MoveFolderAction());
    actions.put(MOVE_FILE_ACTION, new MoveFileAction());
  }
}