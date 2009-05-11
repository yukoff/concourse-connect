/* ----------------------------------------------------------- 
 | dhtml menu for the pimbar
 ------------------------------------------------------------- */

var gi_ypim_hideTimerID = null;
var gi_ypim_hideTimerID2 = null;
var gi_ypim_menuOnID = 0;
var gb_ypim_isMenuOn = 0;
var gb_ypim_isButOut = 0;
var go_ypim_css = {
 btnOn : "tablob",
 btnOff : "tabloa",
 btnDn : "tabloc",
 curBtnOn : "tabhib",
 curBtnOff : "tabhia"
}

var ypim_count=0;
/*
 | set the css for current button
 */
function ypim_setCurBtn(id,on) {
 var oTab1 = ylib_getObj(id+'1');
 var oTab2 = ylib_getObj(id+'Btn');
 if (on) {
  oTab1.className = go_ypim_css.curBtnOn;
  oTab2.className = go_ypim_css.curBtnOn;
 } else {
  oTab1.className = go_ypim_css.curBtnOff;
  oTab2.className = go_ypim_css.curBtnOff;
 }
}

/*
 | set the css for current button with the arrow image
 | this is for the button down state
 */
//function ypim_setBtnDown(id) {
  function ypim_setBtnDown() {
  //var oBut = ylib_getObj(id);
  //oBut.className = go_ypim_css.btnDn;
  gb_ypim_isMenuOn = 1;
  gb_ypim_isButOut=0;
  if (gi_ypim_hideTimerID2!=null) clearTimeout(gi_ypim_hideTimerID2); 
}

/*
 | set the css for other buttons
 */
function ypim_setBtn(id,on) {
 var oTab1 = ylib_getObj(id+'1');
 var oTab2 = ylib_getObj(id+'Btn');
 if (on) {
  oTab1.className = go_ypim_css.btnOn;
  oTab2.className = go_ypim_css.btnOn;
 } else {
  oTab1.className = go_ypim_css.btnOff;
  oTab2.className = go_ypim_css.btnOff;
 }
}

/*
 | show the dropdown menu
 */
function ypim_showMenu(id) {
 if (gi_ypim_menuOnID!=0) ypim_hideMenu();
 var oMenu = ylib_getObj(id);
 var oTb = ylib_getObj(id+'Tb');
 var x = ylib_getPageX(oTb);
 var y = ylib_getPageY(oTb);
 var h = ylib_getH(oTb);
 ylib_moveTo(oMenu,x,y+h+2);
 ypim_setForm(0);
 ylib_show(oMenu);
 gi_ypim_menuOnID = id;
 gb_ypim_isMenuOn = 1;
 return false;
}

function ypim_over() { 
 gb_ypim_isButOut=0;
 if (gi_ypim_hideTimerID!=null) clearTimeout(gi_ypim_hideTimerID); 
 if (gi_ypim_hideTimerID2!=null) clearTimeout(gi_ypim_hideTimerID2); 
}

function ypim_hide() { 
 gi_ypim_hideTimerID = setTimeout("ypim_hideMenu()",500); 
}

function ypim_butOut() {
 gi_ypim_hideTimerID2 = setTimeout("ypim_doHide()",2000);
 gb_ypim_isButOut = 1;
}

/*
 | not the best way to handle the button out even
 | should be able to just clearout gi_ypim_hideTimerID2
 | not working b/c the timer is not getting cleared out
 */
function ypim_doHide() {
 if (gb_ypim_isButOut) ypim_hideMenu();
}

function ypim_hideMenu() {
 if (gi_ypim_menuOnID == 0 || gb_ypim_isMenuOn == 0) return false;
 var oMenu = ylib_getObj(gi_ypim_menuOnID);
 ylib_hide(oMenu);
 ypim_setForm(1);
 gi_ypim_menuOnID = 0;
 gb_ypim_isMenuOn = 0;
 gb_ypim_isButOut=0;
}

function ypim_setForm(on) {
 if (oBw.ns6) return;
 var oSelect;
 if ( oBw.ie4 )
	oSelect = document.body.all.tags('select');
 else
 	oSelect = document.getElementsByTagName('select');

 var len = oSelect.length;
 var elHide = (len>2)? Math.abs(len/2):len;
 if (on) {
  for (var i=0;i<elHide;i++) { ylib_show(oSelect[i]); }
 } else {
  for (var i=0;i<elHide;i++) { ylib_hide(oSelect[i]); }
 }
}

/*
 | initiate the mouseevents for the buttons
 */
function ypim_initMenu() {
 var count = 0;
 if (arguments.length<5) {
  var id = arguments[0];
  var o = ylib_getObj(id);
  ylib_addEvt(o, "mouseover", new Function("ypim_over()"),false);
  ylib_addEvt(o, "mouseout", new Function("ypim_hide()"),false);
//  var oTb = ylib_getObj(id+"Tb");
  //ylib_addEvt(oTb, "mouseover", new Function("ypim_setCurBtn('"+id+"',1)"),false);
  //ylib_addEvt(oTb, "mouseout", new Function("ypim_setCurBtn('"+id+"',0)"),false);
  //var oTb2 = ylib_getObj(id+"Btn");
  //ylib_addEvt(oTb, "mousedown", new Function("ypim_setBtnDown('"+id+"Btn')"),false);
//  ylib_addEvt(oTb, "mousedown", new Function("ypim_setBtnDown()"),false);
//  ylib_addEvt(oTb, "mouseout", new Function("ypim_butOut()"),false);
  count = 1;
 }
 /* for (var i = count; i<4; i++) {
  id = arguments[i];
  o = ylib_getObj(id);
  ylib_addEvt(o, "mouseover", new Function("ypim_over()"),false);
  ylib_addEvt(o, "mouseout", new Function("ypim_hide()"),false);  
  oTb = ylib_getObj(id+"Tb");
  ylib_addEvt(oTb, "mouseover", new Function("ypim_setBtn('"+id+"',1)"),false);
  ylib_addEvt(oTb, "mouseout", new Function("ypim_setBtn('"+id+"',0)"),false);
  oTb2 = ylib_getObj(id+"Btn");
  ylib_addEvt(oTb2, "mousedown", new Function("ypim_setBtnDown('"+id+"Btn')"),false);
  ylib_addEvt(oTb2, "mouseout", new Function("ypim_butOut()"),false);
 } */
}


/* ----------------------------------------------------------- 
 | highlighting table rows
 ------------------------------------------------------------- */
var GS_YML_NEW = "msgnew";
var GS_YML_OLD = "msgold";
var GS_YML_NEWS = "msgnews";
var GS_YML_OLDS = "msgolds";

function yml_hlrow(fmname,boxname,rowname) {
 this.id = fmname+"hlrow";
 eval(this.id+"=this");
 this.fm = ylib_getObj(fmname); // may need to take out
 this.fmName = fmname;
 this.chkName = boxname;
 this.rowName = rowname;
 this.orig_state = new Array();
 this.allChecked = false;
 this.rows = new Array();
 
 this.cssa = GS_YML_NEW;
 this.cssas = GS_YML_NEWS;
 this.cssb = GS_YML_OLD; 
 this.cssbs = GS_YML_OLDS;
 
 this.hilite     = yml_Hilite;
 this.hiliteAll  = yml_HiliteAll;
}

function yml_Hilite(rowID,chkbox) {
 if (chkbox==null || oBw.ns) return;
 var oRow = ylib_getObj(this.rowName+rowID);
 var cl = oRow.className;
 var newCl = "";
 if (chkbox.checked) {
  newCl = (cl==this.cssa)? this.cssas:this.cssbs;
  oRow.className = newCl;
 } else {
  newCl = (cl==this.cssas)? this.cssa:this.cssb;
  oRow.className = newCl;
 }
 this.orig_state[this.orig_state.length] = [rowID,cl];
}

function yml_HiliteAll() {
 this.fm = ylib_getObj(this.fmName);
 if (this.fm==null) return;
 if (typeof this.chkName == "string") { this.chkName = new Array(this.chkName); }
 
 var totalLen = 0;
 if (this.allChecked) {
  for (var i=0;i<this.chkName.length;i++) {
   var chks = this.fm.elements[this.chkName[i]]
   if (!chks) continue;
   if (chks.length!=null) {
    for (var m=0;m<chks.length;m++) {
     this.fm.elements[this.chkName[i]][m].checked = false;
    }
    totalLen += chks.length;
   } else {
    this.fm.elements[this.chkName[i]].checked = false;
    totalLen += 1;
   }
  }
  this.allChecked=0;
 } else {
  for (var i=0;i<this.chkName.length;i++) {
   var chks = this.fm.elements[this.chkName[i]]
   if (!chks) continue;
   if (chks.length!=null) {
    for (var m=0;m<chks.length;m++) {
     this.fm.elements[this.chkName[i]][m].checked = true;
    }
    totalLen += chks.length;
   } else {
    this.fm.elements[this.chkName[i]].checked = true;
    totalLen += 1;
   }
  }
  this.allChecked=1
 }
 if (oBw.ns) return false;
 this.allChecked = (this.allChecked)? 0:1;

 for (var i = 0; i<this.orig_state.length; i++) {
  var oldRow = ylib_getObj(this.rowName+this.orig_state[i][0]);
  oldRow.className = this.orig_state[i][1];
 }
 this.orig_state = new Array();
 var newCl = "";

 if (this.allChecked) {
  for (var i=1; i<=totalLen; i++) {
   var oRow = ylib_getObj(this.rowName+i);
   var cl = oRow.className;
   if (cl==this.cssas||cl==this.cssbs) {
    newCl = (cl==this.cssas)? this.cssa:this.cssb;
    oRow.className = newCl;
   } 
   this.allChecked=0;
  }
 } else {
  for (var i=1; i<=totalLen; i++) {
   var oRow = ylib_getObj(this.rowName+i);
   var cl = oRow.className;
   if (cl==this.cssa||cl==this.cssb) {
    newCl = (cl==this.cssa)? this.cssas:this.cssbs;
    oRow.className = newCl;
   }
   this.allChecked=1;
  }
 }
}

/* takes care of synching check all box and check all link */
function ypim_checkAllTxt(o,chkBox,isClear) {
  var allChecked = (isClear)? 0:1;
  o.allChecked=isClear;
  chkBox.checked=allChecked;
  o.hiliteAll();
  return false;
}

function ypim_clearAllTxt(o,chkBox) {
  ypim_checkAllTxt(o,chkBox,1);
  return false;
}


/* ----------------------------------------------------------- 
 | capture key events
 ------------------------------------------------------------- */

var YLIB_SHIFT_KEYCODE = 16;
var YLIB_CTRL_KEYCODE = 17;
var YLIB_ALT_KEYCODE = 18;
var YLIB_SHIFT = "shift";
var YLIB_CTRL = "ctrl";
var YLIB_ALT = "alt";

ylib_keyevt.count=0;
function ylib_keyevt(elm) {
 this.id = "keyevt"+ylib_keyevt.count++;
 eval(this.id + "=this");
 
 this.keys = new Array();
 this.shift=0;
 this.ctrl=0;
 this.alt=0;
 
 this.addKey         = ylib_addKey;
 this.keyevent       = ylib_keyevent;
 this.checkModKeys   = ylib_checkModKeys;
}

// params: 
// cdom - key code for dom browsers, 
// cns4 - key code for ns 4 browser,
// a - action, m - mod key
function ylib_addKey(cdom,cns4,a,m) {
 if (oBw.ie||oBw.dom) { this.keys[cdom] = [a,m]; }
 else { this.keys[cns4] = [a,m]; }
}

var YLIB_COUNT=0;
function ylib_keyevent(evt) {
 if (oBw.ie||oBw.op) evt=event;
 var k = (oBw.ie||oBw.op||oBw.ns6)? evt.keyCode:evt.which;
 this.checkModKeys(evt,k);
 if (this.keys[k]==null) return false;
 var m = this.keys[k][1];
 if ((this.shift && (m.indexOf(YLIB_SHIFT) != -1) || !this.shift && (m.indexOf(YLIB_SHIFT) == -1)) && (this.ctrl && (m.indexOf(YLIB_CTRL) != -1) || !this.ctrl && (m.indexOf(YLIB_CTRL) == -1)) && (this.alt && (m.indexOf("alt") != -1) || !this.alt && (m.indexOf("alt") == -1))) {
  var a = this.keys[k][0];
  a = eval(a); 
  if(typeof a == "function") a();
 }
}

function ylib_checkModKeys(e,k) {
 if (oBw.dom) { 
  this.shift = e.shiftKey;
		this.ctrl = e.ctrlKey;
		this.alt = e.altKey;
 } else {
  // for opera
  this.shift = (k==YLIB_SHIFT_KEYCODE)? 1:0;
		this.ctrl = (k==YLIB_CTRL_KEYCODE)? 1:0;
		this.alt = (k==YLIB_ALT_KEYCODE)? 1:0;
 }
}

var oKey = new ylib_keyevt();
