function createjscssfile(filename, filetype){
	if (filetype=="js"){
		var fileref=document.createElement('script');
		fileref.setAttribute("type","text/javascript");
		fileref.setAttribute("src", filename);
	} else if (filetype=="css"){
		var fileref=document.createElement("link");
		fileref.setAttribute("rel", "stylesheet");
		fileref.setAttribute("type", "text/css");
		fileref.setAttribute("href", filename);
	}
	return fileref;
}

function replacejscssfile(oldfilename, newfilename, filetype){
  var targetelement=(filetype=="js")? "script" : (filetype=="css")?  "link" : "none";
  var targetattr=(filetype=="js")? "src" : (filetype=="css")? "href" :  "none";
  var allsuspects=document.getElementsByTagName(targetelement);
  for (var i=allsuspects.length; i>=0; i--){
  	if (allsuspects[i] && allsuspects[i].getAttribute(targetattr)!=null && allsuspects[i].getAttribute(targetattr).indexOf(oldfilename)!=-1){
  		var newelement=createjscssfile(newfilename, filetype);
      var thisParent = allsuspects[i].parentNode;
      thisParent.removeChild(allsuspects[i]);
      thisParent.appendChild(newelement);
  	}
  }
}

function removejscssfile(filename, filetype){
	var targetelement=(filetype=="js")? "script" : (filetype=="css")? "link" : "none";
	var targetattr=(filetype=="js")? "src" : (filetype=="css")? "href" : "none";
	var allsuspects=document.getElementsByTagName(targetelement);
	for (var i=allsuspects.length; i>=0; i--){
		if (allsuspects[i] && allsuspects[i].getAttribute(targetattr)!=null && allsuspects[i].getAttribute(targetattr).indexOf(filename)!=-1){
			allsuspects[i].parentNode.removeChild(allsuspects[i]);
		}
	}
}

function replaceChildNode(elementId,newNode){
	var targetElement = document.getElementById(elementId);
	if (targetElement.hasChildNodes()){
		while ( targetElement.childNodes.length >= 1 ){
			targetElement.removeChild(targetElement.firstChild);
		}
		targetElement.appendChild(newNode);
	}
}
