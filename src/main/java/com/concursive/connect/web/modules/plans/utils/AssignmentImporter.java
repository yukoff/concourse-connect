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

package com.concursive.connect.web.modules.plans.utils;

import com.concursive.connect.web.modules.documents.utils.FileInfo;
import com.concursive.connect.web.modules.plans.dao.Requirement;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Imports data from other formats into an outline
 *
 * @author matt rajkowski
 * @created June 30, 2004
 */
public class AssignmentImporter {

  /**
   * Description of the Method
   *
   * @param fileInfo    Description of the Parameter
   * @param requirement Description of the Parameter
   * @param db          Description of the Parameter
   * @return Description of the Return Value
   * @throws Exception Description of the Exception
   */
  public static boolean parse(FileInfo fileInfo, Requirement requirement, Connection db) throws Exception {
    if (System.getProperty("DEBUG") != null) {
      System.out.println("AssignmentImporter-> trying to parse: " + fileInfo.getClientFileName().toLowerCase());
    }
    if (fileInfo == null) {
      return false;
    }
    if (fileInfo.getClientFileName().toLowerCase().endsWith(".xls")) {
      return AssignmentExcelImporter.parse(fileInfo.getFileContents(), requirement, db);
    } else if (fileInfo.getClientFileName().toLowerCase().endsWith(".xmloutline")) {
      return AssignmentOmniOutliner2Importer.parse(fileInfo.getFileContents(), requirement, db);
    } else if (fileInfo.getClientFileName().toLowerCase().endsWith(".oo3")) {
      return AssignmentOmniOutliner3Importer.parse(fileInfo.getFileContents(), requirement, db);
    } else if (fileInfo.getClientFileName().toLowerCase().endsWith(".zip")) {
      // Look for contents.xml in .zip and pass to oo3
      ZipInputStream zip = new ZipInputStream(new ByteArrayInputStream(fileInfo.getFileContents()));
      ZipEntry entry = null;
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      byte[] buffer = new byte[1024];
      while ((entry = zip.getNextEntry()) != null) {
        if (entry.getName().endsWith("contents.xml")) {
          int count;
          while ((count = zip.read(buffer)) != -1) {
            out.write(buffer, 0, count);
          }
        }
      }
      zip.close();
      if (out.size() > 0) {
        return AssignmentOmniOutliner3Importer.parse(out.toByteArray(), requirement, db);
      }
    } else if (fileInfo.getClientFileName().toLowerCase().endsWith(".mpp") ||
        fileInfo.getClientFileName().toLowerCase().endsWith(".mpx") ||
        fileInfo.getClientFileName().toLowerCase().endsWith(".xml") ||
        fileInfo.getClientFileName().toLowerCase().endsWith(".mspdi") ||
        fileInfo.getClientFileName().toLowerCase().endsWith(".mpt")) {
      return AssignmentMicrosoftProjectImporter.parse(fileInfo.getFileContents(), requirement, db);
    }
    return false;
  }
}

