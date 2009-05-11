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

package com.concursive.connect.web.modules.profile.workflow;

import com.concursive.commons.text.StringUtils;
import com.concursive.commons.workflow.ComponentContext;
import com.concursive.commons.workflow.ComponentInterface;
import com.concursive.commons.workflow.ObjectHookComponent;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.lamatek.tags.google.beans.USAddressGeocoder;

import java.sql.Connection;

/**
 * Increments the viewing counter for a project
 *
 * @author matt rajkowski
 * @created April 25, 2008
 */
public class SetProjectGeocode extends ObjectHookComponent implements ComponentInterface {

  public String getDescription() {
    return "Set the geocode value";
  }

  public boolean execute(ComponentContext context) {
    Project previousProject = (Project) context.getPreviousObject();
    Project thisProject = (Project) context.getThisObject();

    // Determine if this should be geocoded
    boolean doGeocode = false;

    // Project is being updated
    if (context.isUpdate()) {
      // Not previously geocoded and has address fields
      if (!previousProject.isGeoCoded() &&
          !thisProject.isGeoCoded() &&
          ((StringUtils.hasText(thisProject.getCity()) &&
              StringUtils.hasText(thisProject.getState())) ||
              StringUtils.hasText(thisProject.getPostalCode()))) {
        doGeocode = true;
      }
      // Previously geocoded but address or location has changed
      if (previousProject.isGeoCoded() &&
          (!previousProject.getLocation().equals(thisProject.getLocation()) ||
              !previousProject.getAddress().equals(thisProject.getAddress()))) {
        doGeocode = true;
      }
    }

    // Project is being inserted
    if (context.isInsert()) {
      // Project is being inserted
      if (!thisProject.isGeoCoded() &&
          ((StringUtils.hasText(thisProject.getCity()) &&
              StringUtils.hasText(thisProject.getState())) ||
              StringUtils.hasText(thisProject.getPostalCode()))) {
        doGeocode = true;
      }
    }

    if (!doGeocode) {
      return false;
    }

    boolean doUpdate = false;
    USAddressGeocoder geo = new USAddressGeocoder();
    if (StringUtils.hasText(thisProject.getAddress())) {
      geo.setAddress(thisProject.getAddress());
    }
    if (StringUtils.hasText(thisProject.getCity())) {
      geo.setCity(thisProject.getCity());
    }
    if (StringUtils.hasText(thisProject.getState())) {
      geo.setState(thisProject.getState());
    }
    if (StringUtils.hasText(thisProject.getPostalCode())) {
      geo.setZip(thisProject.getPostalCode());
    }
    boolean result = geo.geocode();
    if (result) {
      thisProject.setLatitude(geo.getLatitude());
      thisProject.setLongitude(geo.getLongitude());
      doUpdate = true;
    }

    if (!doUpdate) {
      return false;
    }

    Connection db = null;
    try {
      db = getConnection(context);
      thisProject.updateGeocode(db);
    } catch (Exception e) {
      e.printStackTrace(System.out);
    } finally {
      freeConnection(context, db);
    }
    return true;
  }
}