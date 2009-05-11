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
package com.concursive.connect.web.modules.common.social.geotagging.cache;

import au.com.bytecode.opencsv.CSVReader;
import com.concursive.connect.cache.CacheContext;
import com.concursive.connect.web.modules.common.social.geotagging.utils.LocationBean;
import com.concursive.connect.web.modules.common.social.geotagging.utils.LocationUtils;
import net.sf.ehcache.constructs.blocking.CacheEntryFactory;

import java.io.InputStreamReader;

/**
 * Provides project names, which are often used
 *
 * @author matt rajkowski
 * @created Apr 9, 2008
 */
public class LocationCacheEntryFactory implements CacheEntryFactory {

  private CacheContext context = null;

  public LocationCacheEntryFactory(CacheContext context) {
    this.context = context;
  }

  public Object createEntry(Object key) throws Exception {
    LocationBean location = null;
    try {
      // Requested zip code
      String zipCodeToFind = String.valueOf(key);

      // Data source
      CSVReader reader = new CSVReader(new InputStreamReader(LocationUtils.class.getResourceAsStream("/zipcode.csv")));
      //"zip","city","state","latitude","longitude","timezone","dst"
      //"00210","Portsmouth","NH","43.005895","-71.013202","-5","1"

      // Get the header columns
      String[] nextLine = reader.readNext();
      int zipColumn = 0;
      int cityColumn = 1;
      int stateColumn = 2;
      int latitudeColumn = 3;
      int longitudeColumn = 4;
      int timeZoneColumn = 5;
      int dstColumn = 6;

      // find the zipcode
      while ((nextLine = reader.readNext()) != null) {
        if (nextLine.length < 7) {
          continue;
        }
        String zipValue = nextLine[zipColumn].trim();
        if (zipCodeToFind.equals(zipValue)) {
          location = new LocationBean();
          location.setZipCode(nextLine[zipColumn]);
          location.setCity(nextLine[cityColumn]);
          location.setState(nextLine[stateColumn]);
          location.setLatitude(nextLine[latitudeColumn]);
          location.setLongitude(nextLine[longitudeColumn]);
          location.setTimeZone(nextLine[timeZoneColumn]);
          location.setDst(nextLine[dstColumn]);
          break;
        }
      }
      reader.close();
    } catch (Exception e) {
      throw new Exception(e);
    }
    return location;
  }
}