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

import java.io.*;

/**
 * This is the info kept per-file.
 */
class fileInfo {

  static final int MAXLINECOUNT = 20000;

  BufferedReader file;
  //DataInputStream file;  /* File handle that is open for read.  */
  public int maxLine;  /* After input done, # lines in file.  */
  node symbol[]; /* The symtab handle of each line. */
  int other[]; /* Map of line# to line# in other file */
  /* ( -1 means don't-know ).            */
  /* Allocated AFTER the lines are read. */

  /**
   * Normal constructor with one filename; file is opened and saved.
   */
  fileInfo(File filename) throws IOException {
    symbol = new node[MAXLINECOUNT + 2];
    other = null;    // allocated later!
    try {
      file = new BufferedReader(new FileReader(filename.toString()));
    } catch (IOException e) {
      System.err.println("Diff can't read file " + filename);
      System.err.println("Error Exception was:" + e);
      throw new IOException(e.getMessage());
    }
  }

  fileInfo(String content) {
    symbol = new node[MAXLINECOUNT + 2];
    other = null;    // allocated later!
    file = new BufferedReader(new StringReader(content));
  }

  // This is done late, to be same size as # lines in input file.
  void alloc() {
    other = new int[symbol.length + 2];
  }
}
