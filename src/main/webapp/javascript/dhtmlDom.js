/*
 | Browser object
 */
function ylib_Browser() {
 d=document;
 this.agt=navigator.userAgent.toLowerCase();
 this.major = parseInt(navigator.appVersion);
 this.dom=(d.getElementById)?1:0; // true for ie6, ns6
 this.ns=(d.layers);
 this.ns4up=(this.ns && this.major >=4);
 this.ns6=(this.dom&&navigator.appName=="Netscape");
 this.op=this.agt.indexOf('opera')!=-1;
 this.ie=(d.all);
 this.ie4=(d.all&&!this.dom)?1:0;
 this.ie4up=(this.ie && this.major >= 4);
 this.ie5=(d.all&&this.dom);
 this.win=((this.agt.indexOf("win")!=-1) || (this.agt.indexOf("16bit")!=-1));
 this.mac=(this.agt.indexOf("mac")!=-1);
}

var oBw = new ylib_Browser();

// like to optimize this further
function ylib_getObj(id,d) {
  var i,x;  if(!d) d=document; 
  if(!(x=d[id])&&d.all) x=d.all[id]; 
  for (i=0;!x&&i<d.forms.length;i++) x=d.forms[i][id];
  for(i=0;!x&&d.layers&&i<d.layers.length;i++) x=ylib_getObj(id,d.layers[i].document);
  if(!x && document.getElementById) x=document.getElementById(id); 
  return x;
}

function ylib_getH(o) { var h=0; if (oBw.ns) { h=(o.height)? o.height:o.clip.height; return h; } h=(oBw.op)? o.style.pixelHeight:o.offsetHeight; return h; }
function ylib_setH(o,h) { if(oBw.ns) {if(o.clip) o.clip.bottom=h;}else if(oBw.op)o.style.pixelHeight=h;else o.style.height=h; }

function ylib_getW(o) { var w=0; if(oBw.ns) { w=(o.width)? o.width:o.clip.width; return w; } w=(oBw.op)? o.style.pixelWidth:o.offsetWidth; return w; }
function ylib_setW(o,w) { if(oBw.ns) {if(o.clip) o.clip.right=w;}else if(oBw.op)o.style.pixelWidth=w;else o.style.width=w; }

function ylib_getX(o) { var x=(oBw.ns)? o.left:(oBw.op)? o.style.pixelLeft:o.offsetLeft; return x;}
function ylib_setX(o,x) { (oBw.ns)? o.left=x:(oBw.op)? o.style.pixelLeft=x:o.style.left=x; }

function ylib_getY(o) {  var y=(oBw.ns)? o.top:(oBw.op)? o.style.pixelTop:o.offsetTop; return y;}
function ylib_setY(o,y) { (oBw.ie||oBw.dom)? o.style.top=y:(oBw.ns)? o.top=y:o.style.pixelTop=y; }

function ylib_setClip(o) { }

function ylib_getPageX(o) { if(oBw.ns) { var x=(o.pageX)? o.pageX:o.x; return x; } else if (oBw.op) {  var x=0; while(eval(o)) { x+=o.stylo.pixelLeft; e=o.offsetParent; } return x; } else { var x=0; while(eval(o)) { x+=o.offsetLeft; o=o.offsetParent; } return x; } }

function ylib_getPageY(o) { if(oBw.ns) { var y=(o.pageY)? o.pageY:o.y; return y; } else if (oBw.op) {  var y=0; while(eval(o)) { y+=o.stylo.pixelTop; o=o.offsetParent; } return y; }  else { var y=0; while(eval(o)) { y+=o.offsetTop; o=o.offsetParent; } return y; } }

function ylib_moveTo(o,x,y) { ylib_setX(o,x);ylib_setY(o,y); }
function ylib_moveBy(o,x,y) { ylib_setX(o,ylib_getPageX(o)+x);ylib_setY(o,ylib_getPageY(o)+y); }

function ylib_setZ(o,z) { if(oBw.ns)o.zIndex=z;else o.style.zIndex=z; }

function ylib_show(o,disp) { 
 (oBw.ns)? '':(!disp)? o.style.display="inline":o.style.display=disp;
 (oBw.ns)? o.visibility='show':o.style.visibility='visible';  
}
function ylib_hide(o,disp) { 
 (oBw.ns)? '':(arguments.length!=2)? o.style.display="none":'';
 (oBw.ns)? o.visibility='hide':o.style.visibility='hidden';  
}

function ylib_setStyle(o,s,v) { if(oBw.ie5||oBw.dom) eval("o.style."+s+" = '" + v +"'"); }
function ylib_getStyle(o,s) { if(oBw.ie5||oBw.dom) return eval("o.style."+s); }

function ylib_getDocW() { }
function ylib_getDocH() { }

function ylib_addEvt(o,e,f,c){ if(o.addEventListener)o.addEventListener(e,f,c);else if(o.attachEvent)o.attachEvent("on"+e,f);else eval("o.on"+e+"="+f)}

function ylib_writeHTML(o,h) { 
 if(oBw.ns){var doc=o.document;doc.write(h);doc.close();return false;}
 if(o.innerHTML)o.innerHTML=h; 
}

// w - beforeBegin,afterBegin,beforeEnd,afterEnd
// width - for ns 4 only
function ylib_insertHTML(o,h,w) {
 if (oBw.op) return;
 if (o.insertAdjacentHTML) { 
  o.insertAdjacentHTML(w,h);
  return;
 }
 if (oBw.ns) {
  ylib_writeHTML(o,h);
  return;
 }
 // mozilla
 var r = o.ownerDocument.createRange();
	r.setStartBefore(o);
	var frag = r.createContextualFragment(h);
	ylib_insertObj(o,w,frag)
}


function ylib_insertObj(o,w,node) {
	switch (w){
	case 'beforeBegin':
		o.parentNode.insertBefore(node,o)
		break;
	case 'afterBegin':
		o.insertBefore(node,o.firstChild);
		break;
	case 'beforeEnd':
		o.appendChild(node);
		break;
	case 'afterEnd':
		if (o.nextSibling){
			o.parentNode.insertBefore(node,o.nextSibling);
		} else {
			o.parentNode.appendChild(node)
		}
		break;
	}
}
