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
  function refreshUsers() {
    var items = "";
    for (j = 0; j < vAssigned.length; j++) {
      if (items != "") {
        items += "|";
      }
      if (vAssigned[j] != "") {
        items += vAssigned[j] + "," + vRole[j] + "," + vState[j];
      }
    }
    document.forms['inputForm'].elements['assignedUserList_requestItems'].value = items;
  }
  function refreshView() {
    // NOTE: global teamelements_ctx variable must exist
    window.frames['server_list'].location.href=teamelements_ctx + "/ProjectManagementAssignments.do?command=UserList&style=true&pid="+projectId+"&a="+doJoin(vAssigned,"|")+"&r="+doJoin(vRole,"|")+"&s="+doJoin(vState,"|");
  }

  function assignUser() {
    // determine the selected items
    var selUser = document.forms['inputForm'].elements['userAssignedId'];
    var thisUser = selUser.options[selUser.selectedIndex].value;

    var selRole = document.forms['inputForm'].elements['userAssignedRoleId'];
    var thisRole = -1;
    try {
      thisRole = selRole.options[selRole.selectedIndex].value;
    } catch (e) {
    }

    // manage the arrays
    var found = -1;
    for (j = 0; j < vAssigned.length; j++) {
      if (vAssigned[j] == thisUser) {
        found = j;
        if (vState[j] == 2) {
          vRole[j] = thisRole;
          vState[j] = 0;
          refreshUsers();
          refreshView();
        } else {
          alert("User already assigned");
        }
      }
    }
    if (found == -1) {
      if (thisUser != -1) {
        var count = vAssigned.length;
        vAssigned[count] = thisUser;
        vRole[count] = thisRole;
        vState[count] = 1;
      }
      refreshUsers();
      refreshView();
    }
  }

  function removeUser(id) {
    // manage the arrays
    var found = -1;
    for (j = 0; j < vAssigned.length; j++) {
      if (vAssigned[j] == id) {
        if (vState[j] == 1) {
          found = j;
        } else {
          vState[j] = 2;
        }
      }
    }
    if (found > -1) {
      vAssigned = doSplice(vAssigned, found, 1);
      vRole = doSplice(vRole, found, 1);
      vState = doSplice(vState, found, 1);
    }
    refreshUsers();
    refreshView();
  }

  function changeRole(obj) {
    var userId = obj.name.substr(10);
    var roleId = obj.options[obj.selectedIndex].value;
    for (j = 0; j < vAssigned.length; j++) {
      if (vAssigned[j] == userId) {
        vRole[j] = roleId;
      }
    }
    refreshUsers();
  }

  function doSplice(v, index, howMany) {
    var newArray = new Array();
    var count = -1;
    for (i = 0; i < v.length; i++) {
      if (i < index || i >= (index + howMany)) {
        ++count;
        newArray[count] = v[i];
      }
    }
    return newArray;
  }

  function doJoin(v, sep) {
    var newString = "";
    for (j = 0; j < v.length; j++) {
      if (j > 0) {
        newString += sep;
      }
      newString += v[j];
    }
    return newString;
  }