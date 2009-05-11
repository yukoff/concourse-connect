var ypim_color, ypim_url;
if (ypim_color == null) ypim_color = "blue";
if (ypim_url == null) ypim_url = "css/";

if (oBw.ie && oBw.win) {
	var ar = new Array(11);
	ar[0]="<style type=\"text/css\">\n";
	ar[1]="body, td {font-family:arial,helvetica,sans-serif;font-size:x-small;}";
	ar[2]="input, textarea {font-family:verdana,arial,helvetica,sans-serif;font-size:80%;}";
	ar[3]="small, .ftitle TD, .verd {font-family:verdana,arial,helvetica,sans-serif;font-size:xx-small;}";	
	ar[4]="big {font-size:small;}";
	ar[5]="font {line-height: 1.35em;}";
	ar[6]="tt, pre {font-family:'courier new',monospace; font-size:x-small}";
	ar[7]=".composef {font-family:'courier new',monospace;font-size:x-small}";
	ar[8]=".mtitle, .errtitle {font-family:arial,helvetica,sans-serif; font-size: large; font-weight: bold;}";
	ar[9]=".tabhit, .tablot {font-family:arial,helvetica,sans-serif; font-weight: bold; font-size: 14px;}";
	ar[10]="</style>";
	}
else if (oBw.ns6 || (oBw.ie && oBw.mac)) {
	var ar = new Array(10);
	ar[0]="<style type=\"text/css\">\n";
	ar[1]="body, td {font-family:arial,helvetica,sans-serif;font-size:12px}";
	ar[2]="input, textarea, select {font-family:verdana,arial,helvetica,sans-serif;font-size:11px;}";
	ar[3]="small, .ftitle TD, .verd {font-family:verdana,arial,helvetica,sans-serif;font-size:10px;}";
	ar[4]="big {font-size:16px;}";
	ar[5]="font {line-height: 1.35em;}";
	ar[6]="tt, pre, .composef {font-family:'courier new',monospace;font-size:12px;}";
	ar[7]=".mtitle, .errtitle {font-family:arial,helvetica,sans-serif; font-size: 24px; font-weight: bold;}";
	ar[8]=".tabhit, .tablot {font-family:arial,helvetica,sans-serif; font-weight: bold; font-size: 14px;}";
	ar[9]="</style>";
	}
else if (oBw.ns && oBw.win) {
	var ar = new Array(9);
	ar[0]="<style type=\"text/css\">\n";
	ar[1]="body, td, input, td input {font-family:arial, helvetica, sans-serif;font-size:small;}";
	ar[2]="small {font-size:x-small;}";
	ar[3]="big {font-size:medium;}";
	ar[4]="tt, pre {font-family:'courier new',monospace;}";
	ar[5]=".mtitle, .errtitle {font-family:arial,helvetica,sans-serif; font-size: 24px; font-weight: bold;}";
	ar[6]=".tabhit, .tablot {font-family:arial,helvetica,sans-serif; font-weight: bold; font-size: 14px;}";
	ar[7]=".verd, .ftitle {font-family: Verdana, Arial, Helvetica; font-size: x-small;}";
	ar[8]="</style>";
	}
else {
	var ar = new Array(9);
	ar[0]="<style type=\"text/css\">\n";
	ar[1]="body, td, input, td input {font-family:arial, helvetica, sans-serif;font-size:medium;}";
	ar[2]="small {font-size:small;}";
	ar[3]="big {font-size:large;}";
	ar[4]="tt, pre {font-family:'courier new',monospace;}";
	ar[5]=".mtitle, .errtitle {font-family:arial,helvetica,sans-serif; font-size: 24px; font-weight: bold;}";
	ar[6]=".tabhit, .tablot  {font-family:arial,helvetica,sans-serif; font-weight: bold; font-size: 14px; }";
	ar[7]=".verd, .ftitle {font-family: Verdana, Arial, Helvetica; font-size: small;}";
	ar[8]="</style>";
	}

var arjoin = ar.join('');
document.write (arjoin);

document.write ("<LINK rel=stylesheet type='text/css' href='"+ypim_url+"pim_style_"+ypim_color+".css'>");
if (oBw.ns6 || oBw.ie) document.write ("<LINK rel=stylesheet type='text/css' href='"+ypim_url+"pim_style_ie_"+ypim_color+".css'>");
