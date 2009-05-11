
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

// need to set datatype for editing (searchField[index].type)
function addValues(){
  //<ccp:label name="button.addgt">Add ></ccp:label>
  var insertMode = ("Update >" != document.modifyList.addButton.value);

  var searchList = document.modifyList.selectedList;
  var searchText = document.modifyList.newValue.value;
  var count = 0;
  var x = 0;
  if (searchText == null || searchText.length == 0) {
    alert ("You must provide a value for the new option");
    document.modifyList.newValue.focus();
    return;
  }
  if (insertMode && isDuplicate()) {
    alert('Entry already exists');
    document.modifyList.newValue.focus();
    return;
  }
  if (insertMode) {
    searchList.options[searchList.length] = new Option(searchText, ("*" + searchText));
  } else {
    searchList.options[searchList.selectedIndex].text = searchText;
  }
  document.modifyList.newValue.value = "";
  document.modifyList.addButton.value  = "Add >";
  document.modifyList.newValue.focus();
  return true;
}

function isDuplicate(){
  var searchList = document.modifyList.selectedList;
    for (count=0; count<(searchList.length); count++) {
      if((searchList.options[count].text).toLowerCase() == (document.modifyList.newValue.value).toLowerCase()){
        return true;
      }
    }
  return false;
}

function removeValues(){
	var searchList = document.modifyList.selectedList;

	var tempArray = new Array();
	var offset = 0;
	var count = 0;
	var searchCriteria = new Array();

	if (searchCriteria.length != searchList.length) {
    for (count=0; count<(searchList.length); count++) {
      searchCriteria[count] = searchList.options[count].value;
    }
	}

	if (searchList.length == 0) {
    alert("Nothing to remove");
	} else if (searchList.options.selectedIndex == -1) {
    alert("An item needs to be selected before it can be removed");
  } else {
    searchCriteria[searchList.selectedIndex] = "skip";
    searchList.options[searchList.selectedIndex] = null;
    for (i=0; i < searchCriteria.length; i++){
      if (searchCriteria[i] == "skip") {
        offset = 1;
        delete searchCriteria[i];
        tempArray[i] = searchCriteria[i+offset];
      } else if (i+offset == searchCriteria.length) {
        break;
      } else {
        tempArray[i] = searchCriteria[i+offset];
      }
    }
    delete searchCriteria
    searchCriteria = new Array();
    for (i=0; i < tempArray.length; i++){
      if (tempArray[i] != null) {
        searchCriteria[i] = tempArray[i];
      }
    }
	}
}

function switchToRename() {
  var searchList = document.modifyList.selectedList;
  if (searchList.length == 0) {
    alert("Nothing to rename");
	} else if (searchList.options.selectedIndex == -1) {
    alert("An item needs to be selected before it can be renamed");
  } else {
    document.modifyList.newValue.value = searchList.options[searchList.selectedIndex].text;
    document.modifyList.addButton.value  = "Update >";
    document.modifyList.newValue.focus();
  }
}

// -------------------------------------------------------------------
// swapOptions(select_object,option1,option2)
//  Swap positions of two options in a select list
// -------------------------------------------------------------------
function swapOptions(obj,i,j) {
  var o = obj.options;
  if(o.selectedIndex > -1){

  var i_selected = o[i].selected;
  var j_selected = o[j].selected;
  var temp = new Option(o[i].text, o[i].value, o[i].defaultSelected, o[i].selected);
  var temp2= new Option(o[j].text, o[j].value, o[j].defaultSelected, o[j].selected);
  o[i] = temp2;
  o[j] = temp;
  o[i].selected = j_selected;
  o[j].selected = i_selected;
  }else{
    alert("An item needs to be selected");
  }
}

// -------------------------------------------------------------------
//  Select All options
// -------------------------------------------------------------------
function selectAllOptions(obj) {
  var size = obj.options.length;
  var i = 0;

  if (size == 0) {
    alert ("You must have at least one item in this list.");
    return false;
  }

  for (i=0;i<size;i++) {
    obj.options[i].selected = true;
    document.modifyList.selectNames.value = document.modifyList.selectNames.value + "^" + obj.options[i].text;
  }

  return true;
}

// -------------------------------------------------------------------
// moveOptionUp(select_object)
//  Move selected option in a select list up one
// -------------------------------------------------------------------
function moveOptionUp(obj) {
  // If > 1 option selected, do nothing
  var selectedCount=0;
  for (i=0; i<obj.options.length; i++) {
    if (obj.options[i].selected) {
            selectedCount++;
    }
  }
  if (selectedCount != 1) {
    return;
  }
  // If this is the first item in the list, do nothing
  var i = obj.selectedIndex;
  if (i == 0) {
    return;
  }
  swapOptions(obj,i,i-1);
  obj.options[i-1].selected = true;
}

// -------------------------------------------------------------------
// moveOptionDown(select_object)
//  Move selected option in a select list down one
// -------------------------------------------------------------------
function moveOptionDown(obj) {
  // If > 1 option selected, do nothing
  var selectedCount=0;
  for (i=0; i<obj.options.length; i++) {
    if (obj.options[i].selected) {
      selectedCount++;
    }
  }
  if (selectedCount != 1) {
    return;
  }
  // If this is the last item in the list, do nothing
  var i = obj.selectedIndex;
  if (i == (obj.options.length-1)) {
    return;
  }
  swapOptions(obj,i,i+1);
  obj.options[i+1].selected = true;
}

function sortSelect (select, compareFunction) {
  if (!compareFunction) compareFunction = compareText;
  var options = new Array (select.options.length);
  for (var i = 0; i < options.length; i++)
    options[i] = new Option (
          select.options[i].text,
          select.options[i].value,
          select.options[i].defaultSelected,
          select.options[i].selected
    );

  options.sort(compareFunction);
  select.options.length = 0;
  for (var i = 0; i < options.length; i++) select.options[i] = options[i];
}

function compareText (option1, option2) {
  return option1.text < option2.text ? -1 : option1.text > option2.text ? 1 : 0;
}

