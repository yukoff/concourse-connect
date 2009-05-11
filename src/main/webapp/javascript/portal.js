
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
 * under the GNU Affero General Public License. Ê
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

var offClass="basket";
var onClass="hovered";
var primedClass="primed";

function initPortletHandle(id) {
  var dd1 = new YAHOO.util.DDProxy(id, "portletGroup");
  dd1.onDragDrop = function(e,id) {
    var dd = YAHOO.util.DDM.getDDById(id);
    buildPortlet(this.getEl().id,  id);
  }
  dd1.isTarget = false;
  dd1.onDrag = function(e) { }
  dd1.startDrag = function(e) {
    var divs = YAHOO.util.Dom.getElementsByClassName(offClass);
    for (var i = 0; i < divs.length; i++) {
      divs[i].className = primedClass;
    }
  }

  // the element id this is hovering over
  dd1.onDragOver = function(e) { }

  // element this item is hovering over
  dd1.onDragEnter = function(e,id) {
    var el = YAHOO.util.DDM.getElement(id);
    if (el.className == primedClass || el.className == offClass) {
      //els[id] = true;
      el.className = onClass;
    }
  }
  // element that was being hovered over
  dd1.onDragOut = function(e, id) {
    // restore the style
    var el = YAHOO.util.DDM.getElement(id);
    if (el.className == onClass) {
      el.className = primedClass;
    }
  }
  dd1.endDrag = function(e,id) {
    var divs = YAHOO.util.Dom.getElementsByClassName(onClass);
    for (var i = 0; i < divs.length; i++) {
      divs[i].className = offClass;
    }
    var divs2 = YAHOO.util.Dom.getElementsByClassName(primedClass);
    for (var i = 0; i < divs2.length; i++) {
      divs2[i].className = offClass;
    }
    /*
    for (var i in els) {
        var el = YAHOO.util.DDM.getElement(i);
        if (el) { el.className = offClass; }
    }
    */
    /*
    // Turn off the cells
    var tbl = document.getElementById("master");
    var rows = tbl.tBodies[0].rows;
    for (var i = 0; i < rows.length - 1; i++) {
      // Skip the first column
      for (var j = 1; j < rows[i].cells.length - 1; j++) {
        var td = rows[i].cells[j];
        // TODO: Change the enclosed div
        td.className=offClass;
      }
    }
    */
  }
}

function initPortletWindow(id) {
  var ddt1 = new YAHOO.util.DDTarget(id, "portletGroup");
}




/* Table Editor */
var letterIndex = '_ABCDEFGHIJKLMNOPQRSTUVWXYZ';

function renameTableIds(tblId) {
  var r = -1;
  var c = 0;
  var d = 0;
  var tbl = document.getElementById(tblId);
  var rows = tbl.tBodies[0].rows;
  for (var i = 0; i < rows.length - 1; i++) {
    ++r;
    rows[i].id = 'r' + r;
    // Skip the first column
    for (var j = 1; j < rows[i].cells.length - 1; j++) {
      ++c;
      var td = rows[i].cells[j];
      td.id = 'r' + r + 'c' + c;
      // get the included div
      initPortletWindow(td.id);
    }
  }
}

/* Display the table uniformly */
function resetColumnWidths(tblHeadObj) {
  var newWidth = Math.round(100/(tblHeadObj.rows[0].cells.length - 1)) + "%";
  for (var i = 1; i <= tblHeadObj.rows[0].cells.length - 1; i++) {
    var thisCell = tblHeadObj.rows[0].cells[i];
    thisCell.width = newWidth;
  }
}

/* Table cell manipulation */
function addColumn(tblId) {
  // Header has two functions
  var tblHeadObj = document.getElementById(tblId).tHead;
  // Title row
  //var titleTH = tblHeadObj.rows[0].cells[0];
  //titleTH.colSpan = titleTH.colSpan + 1;
  // Column Letter
  var newTH = document.createElement('th');
  tblHeadObj.rows[0].appendChild(newTH);
  newTH.innerHTML = '' + letterIndex.substring(tblHeadObj.rows[0].cells.length - 1, tblHeadObj.rows[0].cells.length)
  resetColumnWidths(tblHeadObj);
  // Add body cells
	var tblBody = document.getElementById(tblId).tBodies[0];
  for (var i = 0; i < tblBody.rows.length; i++) {
    var cellName = 'r' + i + 'c' + (tblBody.rows[i].cells.length - 1);
    var newCell = tblBody.rows[i].insertCell(-1);
    configureNewCell(newCell, cellName);
  }
}

function deleteColumn(tblId) {
  // Determine if column can be deleted by using master cell
  var tblHead = document.getElementById(tblId).tHead;
  var cellLength = tblHead.rows[0].cells.length;
  if (tblHead.rows[0].cells.length > 2) {
    // Shrink the headers
    //var titleTH = tblHead.rows[0].cells[0];
    //titleTH.colSpan = titleTH.colSpan - 1;
    tblHead.rows[0].deleteCell(cellLength - 1);
    resetColumnWidths(tblHead);
    // Shrink the body
    var tblBody = document.getElementById(tblId).tBodies[0];
    var iteration = tblBody.rows.length;
    // Iterate the rows and remove the last column
    for (var i = 0; i < iteration; i++) {
      var found = false;
      for (var k = cellLength; k > 1; k--) {
        var bodyTD = tblBody.rows[i].cells[k - 1];
        if (!found && bodyTD) {
          if (bodyTD.colSpan == 1 || bodyTD.colSpan == 0 || !bodyTD.hasAttribute("colSpan")) {
            tblBody.rows[i].deleteCell(k - 1);
          } else {
            bodyTD.colSpan = bodyTD.colSpan - 1;
          }
          found = true;
        }
      }
    }
  }
}

function addRow(tblId) {
  // Determine number of columns by using master cell
  var tblHead = document.getElementById(tblId).tHead;
  var tblBody = document.getElementById(tblId).tBodies[0];
  var cols = tblHead.rows[0].cells.length;
  var iteration = tblBody.rows.length + 1;
  // Construct the row
  //newRow.className = 'row' + (iteration % 2);
  var newRow = tblBody.insertRow(-1);
  newRow.id = 'r' + (tblBody.rows.length - 1);
  // Number the row
  var newCellNum = newRow.insertCell(0);
  newCellNum.innerHTML = iteration;
  newCellNum.className = 'indexed';
  // Create cells
  for (var i = 0; i < cols - 1; i++) {
    var cellName = 'r' + (tblBody.rows.length - 1) + 'c' + i;
    var newCell = newRow.insertCell(i + 1);
    configureNewCell(newCell, cellName);
  }
}

function deleteRow(tblId) {
  var tbl = document.getElementById(tblId);
  var rows = tbl.tBodies[0].rows.length;
  if (rows > 1) {
    tbl.tBodies[0].deleteRow(tbl.tBodies[0].rows.length-1);
  }
}

function expandRight(tdId) {
  // Determine this TDs position in the table, if a cell exists to the right then
  // delete the cell to the right and change this cell's colSpan += 1
  var cell = document.getElementById(tdId);
  var row = cell.parentNode;
  //var rowIndex = row.sectionRowIndex;

  // Calculate cellIndex because function is broken for Safari
  var cellIndex = 0;
  for (var i = 1; i + 1 < row.cells.length; i++) {
    var thisTD = row.cells[i];
    if (thisTD.id == tdId) {
      cellIndex = i;
    }
  }

  // Merge if the cell is not at the end
  if (cellIndex > 0 && cellIndex + 1 < row.cells.length ) {
    var rightCell = row.cells[cellIndex+1];
    if (rightCell) {
      var colSpan = rightCell.colSpan;
      if (colSpan == 0) {
        colSpan = 1;
      }
      var currentColSpan = cell.colSpan;
      if (currentColSpan == 0) {
        currentColSpan = 1;
      }
      // Manipulate the table
      row.deleteCell(cellIndex+1)
      cell.colSpan = currentColSpan + colSpan;
    }
  }
}

/* Define a new portlet window drop-zone */
function configureNewCell(newCell, cellName) {
  // TODO: Create a toolbar div that will be used for this cell's controls
  // Each cell will have (if applicable):
  // Expand left, expand right, expand down, expand up
  newCell.id = cellName;
  newCell.height = "50";
  newCell.vAlign = "top";
  newCell.innerHTML = '<div id="' + cellName + 'd1' + '" class="basket"></div>';
  initPortletWindow(cellName + 'd1');
}

function buildPortlet(fromId, toId) {
  //alert('from ' + fromId + ' to ' + toId);
  var divTo = document.getElementById(toId);
  var divFrom = document.getElementById(fromId);
  divTo.innerHTML += '' +
    '<br />' +
    '<table width="100%" border="0" cellpadding="0" cellspacing="0">' +
    '<tr>' +
    '<th>' + fromId + '</th>' +
    '</tr>' +
    '<tr>' +
    '<td>' + divFrom.innerHTML + '</td>' +
    '</tr>' +
    '</table>';
}
