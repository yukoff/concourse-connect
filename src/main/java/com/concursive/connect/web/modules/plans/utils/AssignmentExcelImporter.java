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

import com.concursive.connect.web.modules.plans.dao.Assignment;
import com.concursive.connect.web.modules.plans.dao.Requirement;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;


/**
 * Imports data from other formats into an outline
 *
 * @author matt rajkowski
 * @created June 30, 2004
 */
public class AssignmentExcelImporter {

  /**
   * Description of the Method
   *
   * @param requirement Description of the Parameter
   * @param db          Description of the Parameter
   * @param buffer      Description of the Parameter
   * @return Description of the Return Value
   * @throws java.sql.SQLException Description of the Exception
   */
  public static boolean parse(byte[] buffer, Requirement requirement, Connection db) throws SQLException {
    if (System.getProperty("DEBUG") != null) {
      System.out.println("AssignmentExcelImporter-> parseExcel");
    }
    try {
      db.setAutoCommit(false);
      // stream the Excel Spreadsheet from the uploaded byte array
      POIFSFileSystem fs =
          new POIFSFileSystem(new ByteArrayInputStream(buffer));
      HSSFWorkbook hssfworkbook = new HSSFWorkbook(fs);
      // get the first sheet
      HSSFSheet sheet = hssfworkbook.getSheetAt(0);
      // define objects for housing spreadsheet data
      HSSFRow currentRow = sheet.getRow(0);
      // parse each row, create and insert into a new requirement with a tree
      int rows = sheet.getPhysicalNumberOfRows();
      if (System.getProperty("DEBUG") != null) {
        System.out.println("AssignmentExcelImporter-> Number of rows: " + rows);
      }
      // Columns
      int columnHeader = -1;
      int columnMax = -1;
      boolean columnItemComplete = false;
      short itemColumn = -1;
      short priorityColumn = -1;
      short assignedToColumn = -1;
      short effortColumn = -1;
      short startColumn = -1;
      short endColumn = -1;

      // parse
      for (int r = 0; r < rows; r++) {
        currentRow = sheet.getRow(r);

        if (currentRow != null) {
          // Search for header
          if (columnHeader == -1) {
            int cells = currentRow.getPhysicalNumberOfCells();
            for (short c = 0; c < cells; c++) {
              HSSFCell cell = currentRow.getCell(c);
              if (cell != null) {
                if ("Item".equals(getValue(cell))) {
                  columnHeader = r;
                  itemColumn = c;
                  columnMax = c;
                } else if (itemColumn > -1 && !columnItemComplete && c > itemColumn) {
                  if ("".equals(getValue(cell))) {
                    columnMax = c;
                  } else if (!"".equals(getValue(cell))) {
                    columnItemComplete = true;
                  }
                }
                if ("Priority".equals(getValue(cell))) {
                  columnHeader = r;
                  priorityColumn = c;
                } else if ("Assigned To".equals(getValue(cell))) {
                  columnHeader = r;
                  assignedToColumn = c;
                } else if ("Lead".equals(getValue(cell))) {
                  columnHeader = r;
                  assignedToColumn = c;
                } else if ("Effort".equals(getValue(cell))) {
                  columnHeader = r;
                  effortColumn = c;
                } else if ("Start".equals(getValue(cell))) {
                  columnHeader = r;
                  startColumn = c;
                } else if ("End".equals(getValue(cell))) {
                  columnHeader = r;
                  endColumn = c;
                }
              }
            }
          }
          // Process each column
          if (columnHeader > -1 && r > columnHeader) {
            boolean gotOne = false;
            Assignment assignment = new Assignment();
            assignment.setProjectId(requirement.getProjectId());
            assignment.setRequirementId(requirement.getId());
            // Activities and folders
            if (itemColumn > -1) {
              // Get the first indent level that has data
              for (short c = itemColumn; c <= columnMax; c++) {
                HSSFCell cell = currentRow.getCell(c);
                if (cell != null && !"".equals(getValue(cell))) {
                  assignment.setRole(getValue(cell));
                  assignment.setIndent(c);
                  gotOne = true;
                  break;
                }
              }
            }
            if (gotOne) {
              // Priority
              if (priorityColumn > -1) {
                HSSFCell cell = currentRow.getCell(priorityColumn);
                if (cell != null) {
                  assignment.setPriorityId(getValue(cell));
                }
              }
              // Effort
              if (effortColumn > -1) {
                HSSFCell cell = currentRow.getCell(effortColumn);
                if (cell != null) {
                  assignment.setEstimatedLoe(getValue(cell));
                  if (assignment.getEstimatedLoeTypeId() == -1) {
                    assignment.setEstimatedLoeTypeId(2);
                  }
                }
              }
              // Assigned To
              if (assignedToColumn > -1) {
                HSSFCell cell = currentRow.getCell(assignedToColumn);
                if (cell != null) {
                  assignment.addUsers(getValue(cell));
                }
              }
              // Start Date
              if (startColumn > -1) {
                HSSFCell cell = currentRow.getCell(startColumn);
                if (cell != null) {
                  assignment.setEstStartDate(getDateValue(cell));
                }
              }
              // Due Date
              if (endColumn > -1) {
                HSSFCell cell = currentRow.getCell(endColumn);
                if (cell != null) {
                  assignment.setDueDate(getDateValue(cell));
                }
              }
              assignment.setEnteredBy(requirement.getEnteredBy());
              assignment.setModifiedBy(requirement.getModifiedBy());
              assignment.setStatusId(1);
              // Make sure a valid priority is set
              if (assignment.getPriorityId() < 1 || assignment.getPriorityId() > 3) {
                assignment.setPriorityId(2);
              }
              // Make sure user is on team, before adding, else unset the field
              if (!assignment.hasValidTeam(db)) {
                assignment.getAssignedUserList().clear();
              }
              // Insert the assignment
              assignment.insert(db);
              if (System.getProperty("DEBUG") != null) {
                System.out.println("AssignmentExcelImporter-> Assignment Inserted: " + assignment.getId());
              }
            }
          }
        }
      }
      db.commit();
    } catch (Exception e) {
      db.rollback();
      e.printStackTrace(System.out);
      return false;
    } finally {
      db.setAutoCommit(true);
    }
    return true;
  }


  /**
   * Gets the value attribute of the AssignmentImporter class
   *
   * @param cell Description of the Parameter
   * @return The value value
   */
  private static String getValue(HSSFCell cell) {
    if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
      return String.valueOf(cell.getNumericCellValue());
    }
    if (cell.getCellType() == HSSFCell.CELL_TYPE_BOOLEAN) {
      return String.valueOf(cell.getBooleanCellValue());
    }
    if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
      return cell.getStringCellValue().trim();
    }
    if (cell.getCellType() == HSSFCell.CELL_TYPE_BLANK) {
      return "";
    }
    if (System.getProperty("DEBUG") != null) {
      System.out.println("AssignmentExcelImporter-> NONE: " + cell.getCellType());
    }
    try {
      return cell.getStringCellValue().trim();
    } catch (Exception e) {
    }
    try {
      return String.valueOf(cell.getNumericCellValue());
    } catch (Exception e) {
    }
    try {
      return String.valueOf(cell.getBooleanCellValue());
    } catch (Exception e) {
    }
    return null;
  }

  private static Date getDateValue(HSSFCell cell) {
    try {
      return cell.getDateCellValue();
    } catch (Exception e) {
      return null;
    }
  }

}