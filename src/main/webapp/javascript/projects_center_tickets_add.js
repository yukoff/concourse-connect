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
  // NOTE: global teamelements_ctx variable must exist
function updateCategory() {
  var sel = document.getElementById("selDirectory");
  if (sel.options.length > 0 && sel.options.selectedIndex != -1) {
    var items = "";
    var value = sel.options[sel.selectedIndex].value;
    if (value.indexOf("email|") == 0) {
      hideSpan("listSpan");
      hideSpan("listSpan2");
      hideSpan("contactSpan");
      //hideSpan("contactSpan2");
      hideSpan("thisProjectSpan");
      hideSpan("select1SpanProject");
      hideSpan("select1SpanDepartment");
      hideSpan("select1SpanContacts");
      hideSpan("select2Span");
      hideSpan("select2SpanContacts");
      showSpan("emailSpan");
      showSpan("emailSpan2");
      document.ticketForm.email.focus();
    } else if (value.indexOf("this|") == 0) {
      var sel2 = document.getElementById("selDepartment");
      sel2.options.length = 0;
      var sel3 = document.getElementById("selTotalList");
      sel3.options.length = 0;
      hideSpan("listSpan");
      showSpan("listSpan2");
      hideSpan("contactSpan");
      //hideSpan("contactSpan2");
      hideSpan("select1SpanProject");
      hideSpan("select1SpanDepartment");
      hideSpan("select1SpanContacts");
      showSpan("select2Span");
      hideSpan("select2SpanContacts");
      hideSpan("emailSpan");
      hideSpan("emailSpan2");
      showSpan("thisProjectSpan");
      window.frames['server_commands'].location.href=
        teamelements_ctx + "/ProjectManagementTickets.do?command=Items&source=" + encodeURIComponent(value) + "&out=text";
    } else if (value.indexOf("contacts|") == 0) {
      var sel2 = document.getElementById("selDepartment");
      sel2.options.length = 0;
      var sel3 = document.getElementById("selTotalList");
      sel3.options.length = 0;
      hideSpan("listSpan");
      showSpan("listSpan2");
      showSpan("contactSpan");
      //showSpan("contactSpan2");
      hideSpan("thisProjectSpan");
      hideSpan("select1SpanProject");
      hideSpan("select1SpanDepartment");
      showSpan("select1SpanContacts");
      hideSpan("select2Span");
      showSpan("select2SpanContacts");
      hideSpan("emailSpan");
      hideSpan("emailSpan2");
      document.ticketForm.searchValue.focus();
    } else {
      var sel2 = document.getElementById("selDepartment");
      sel2.options.length = 0;
      var sel3 = document.getElementById("selTotalList");
      sel3.options.length = 0;
      showSpan("listSpan");
      showSpan("listSpan2");
      hideSpan("contactSpan");
      //hideSpan("contactSpan2");
      hideSpan("thisProjectSpan");
      showSpan("select1SpanProject");
      hideSpan("select1SpanDepartment");
      hideSpan("select1SpanContacts");
      showSpan("select2Span");
      hideSpan("select2SpanContacts");
      hideSpan("emailSpan");
      hideSpan("emailSpan2");
      window.frames['server_commands'].location.href=
        teamelements_ctx + "/ProjectManagementTickets.do?command=Projects&source=" + encodeURIComponent(value) + "&out=text";
    }
  }
}

function updateItemList() {
  items = "";
  var sel = document.getElementById("selDirectory");
  var sel2 = document.getElementById("selDepartment");
  if (sel.options.length > 0 && sel.options.selectedIndex != -1 &&
      sel2.options.length > 0 && sel2.options.selectedIndex != -1) {
    var value = sel.options[sel.selectedIndex].value;
    var value2 = sel2.options[sel2.selectedIndex].value;
    var url = teamelements_ctx + "/ProjectManagementTickets.do?command=Items&source=" + encodeURIComponent(value) + "|" + value2 + "&out=text";
    window.frames['server_commands'].location.href=url;
  }
  showSpan("select2Span");
}

function initList(thisId) {
  items += "|" + thisId + "|";
}

function addEmail(form) {
  if (form.email.value.length == 0 || !checkEmail(form.email.value)) {
    alert("Email address could not be added\r\n" +
          "Please make sure the email address is entered correctly");
  } else {
    form.selProjectList.options.length += 1;
    form.selProjectList.options[form.selProjectList.options.length - 1] = new Option(form.email.value, "-1");
    form.email.value = "";
    document.ticketForm.email.focus();
  }
}

function searchName(form) {
  if (form.searchValue.value.length == 0) {
    alert("Please enter a value to search for");
  } else {
    var sel = document.getElementById("selDirectory");
    var value = sel.options[sel.selectedIndex].value;
    var value2 = form.searchValue.value;
    window.frames['server_commands'].location.href=
      teamelements_ctx + "/ProjectManagementTickets.do?command=Items&source=" + encodeURIComponent(value) + "|" + value2 + "&out=text";
  }
}


function addList(form) {
  if (form.selTotalList.options.length > 0 && form.selTotalList.options.selectedIndex != -1) {
    var index = form.selTotalList.selectedIndex;
    var copyValue = form.selTotalList.options[index].value;
    var copyText = form.selTotalList.options[index].text;
    //add to list
    form.selTotalList.options[index] = null;
    form.selProjectList.options.length += 1;
    form.selProjectList.options[form.selProjectList.options.length - 1] = new Option(copyText, copyValue);
    form.selTotalList.selectedIndex = -1;
    //update the array
    for (i = 0; i < vectorUserId.length; i++) {
      if (copyValue == vectorUserId[i]) {
        vectorState[i] = "1";
      }
    }
  }
}

function removeList(form) {
  if (form.selProjectList.options.length > 0 && form.selProjectList.options.selectedIndex != -1) {
    var index = form.selProjectList.selectedIndex;
    var copyValue = form.selProjectList.options[index].value;
    var copyText = form.selProjectList.options[index].text;
    //if exists in team list then move it, otherwise delete it
    form.selProjectList.options[index] = null;
    if (items.indexOf("|" + copyValue + "|") > -1) {
      form.selTotalList.options.length += 1;
      form.selTotalList.options[form.selTotalList.options.length - 1] = new Option(copyText, copyValue);
    }
    form.selProjectList.selectedIndex = -1;
    //update the array
    for (i = 0; i < vectorUserId.length; i++) {
      if (copyValue == vectorUserId[i]) {
        vectorState[i] = "0";
        break;
      }
    }
  }
}

function resetValues(form) {
  alert("Ask the server to build select and clear values");
}

function checkForm(form) {
  if (document.getElementById("insertMembers")) {
    form.insertMembers.value = "";
    form.deleteMembers.value = "";
    //add only if not on list already
    for (i = 0; i < form.selProjectList.options.length; i++) {
      var found = false;
      for (j = 0; j < vectorUserId.length; j++) {
        if (form.selProjectList.options[i].value == vectorUserId[j]) {
          found = true;
        }
      }
      if (!found) {
        if (form.insertMembers.value.length > 0) {
          form.insertMembers.value += "|";
        }
        if (form.selProjectList.options[i].value == -1) {
          form.insertMembers.value += form.selProjectList.options[i].text;
        } else {
          form.insertMembers.value += form.selProjectList.options[i].value;
        }
      }
    }
    //check deletes
    for (j = 0; j < vectorUserId.length; j++) {
      if (vectorState[j] == "0") {
        if (form.deleteMembers.value.length > 0) {
          form.deleteMembers.value += "|";
        }
        form.deleteMembers.value += vectorUserId[j];
      }
    }
  }
  return true;
}
