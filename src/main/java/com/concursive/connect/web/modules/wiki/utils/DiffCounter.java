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

/**
 * Description
 *
 * @author matt rajkowski
 * @version $Id$
 * @created Feb 11, 2006
 */

public class DiffCounter {
  private int added = 0;
  private int changedFrom = 0;
  private int changedTo = 0;
  private int changed = 0;
  private int deleted = 0;

  public DiffCounter() {
  }

  public int getAdded() {
    return added;
  }

  public void setAdded(int added) {
    this.added = added;
  }

  public int getChangedFrom() {
    return changedFrom;
  }

  public void setChangedFrom(int changedFrom) {
    this.changedFrom = changedFrom;
  }

  public int getChangedTo() {
    return changedTo;
  }

  public void setChangedTo(int changedTo) {
    this.changedTo = changedTo;
  }

  public int getChanged() {
    return changed;
  }

  public void setChanged(int changed) {
    this.changed = changed;
  }

  public int getDeleted() {
    return deleted;
  }

  public void setDeleted(int deleted) {
    this.deleted = deleted;
  }

  public void lineAdded(int count) {
    added += count;
  }

  public void lineChangedFrom() {
    ++changedFrom;
  }

  public void lineChangedTo() {
    ++changedTo;
  }

  public void lineDeleted(int count) {
    deleted += count;
  }

  public void lineChanged(int count) {
    changed += count;
  }

  public void update() {
    if (changedFrom > changedTo) {
      changed += changedTo;
      deleted += changedFrom - changedTo;
    } else if (changedTo > changedFrom) {
      changed += changedFrom;
      deleted += changedTo - changedFrom;
    } else {
      changed += changedTo;
    }
    changedFrom = 0;
    changedTo = 0;
  }
}
