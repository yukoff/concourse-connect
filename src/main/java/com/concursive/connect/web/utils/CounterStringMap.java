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

package com.concursive.connect.web.utils;

import com.concursive.commons.text.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Stores strings and a related occurrence count
 *
 * @author matt rajkowski
 * @created Apr 23, 2008
 */
public class CounterStringMap extends HashMap<String, Integer> {

  private int total = 0;

  public int getTotal() {
    return total;
  }

  public void add(String key) {
    total += 1;
    if (StringUtils.hasText(key)) {
      if (this.containsKey(key)) {
        this.put(key, this.get(key) + 1);
      } else {
        this.put(key, 1);
      }
    }
  }

  public ArrayList<CounterPair> getSortedPairs() {
    // Sort the items based on name
    ArrayList<String> temp = new ArrayList<String>(this.keySet());
    Collections.sort(temp);
    // Return a useable array
    ArrayList<CounterPair> sortedEntries = new ArrayList<CounterPair>();
    for (String thisValue : temp) {
      CounterPair pair = new CounterPair(thisValue, this.get(thisValue));
      sortedEntries.add(pair);
    }
    return sortedEntries;
  }
}
