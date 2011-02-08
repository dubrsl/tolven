// removed getPreferencesMenu. Not used;

function getPreferencesMenuItemsFromServer( element, role ){
	var instAjax = new Ajax.Request(
		'getmenuitems.ajaxc', 
		{
			method: 'get', 
			parameters: 'element='+element+'&role=' + role + '&accountUser='+accountUserId, 
			onComplete: parseMenuItems
		});
	hideDialog();
}

// removed showUserPreferences( req ). Not used;

/* 
	Clear the default radio button when the corresponding visibility checkbox is unchecked,
*/
function checkSibling( elem){
	var parentTR = elem.parentNode.parentNode; // Go 2 nodes up to <TR>.
	var inputSiblings = parentTR.getElementsByTagName("input");
	for( var i =0; i < inputSiblings.length; i++){
		if( inputSiblings[i].type == "radio"){
			if(elem.checked == true){
				inputSiblings[i].disabled = false;
			}else{
				inputSiblings[i].checked = false;
				inputSiblings[i].disabled = true;
			}
			break;
		}
	}
}

function parseMenuItems( req ){
	var xmlDoc = req.responseXML;
	try{
		var parentPath = Tolven.Util.getAttributeValue( xmlDoc.getElementsByTagName("response")[0].attributes, "path");//['path'].value;
		var defPath = Tolven.Util.getAttributeValue( xmlDoc.getElementsByTagName("response")[0].attributes, "defpath");
		var title = Tolven.Util.getAttributeValue( xmlDoc.getElementsByTagName("response")[0].attributes, "title");
		var outputBuffer = "";

		outputBuffer += "<div id=\"prefBox\" class=\"\" style=\"width:100%;display:block;\">";
		outputBuffer += "<div class=\"innerUserPrefBox\" style=\"width:100%;display:block\">";
		outputBuffer += "<ul class='prefBoxInstrUL'>";

		var helpNodes = xmlDoc.getElementsByTagName("UserPreferencesHelp");
		for( var i = 0; i < helpNodes.length; i++){
			var helpstr = Tolven.Util.getXMLContent(helpNodes[i].childNodes[0]);
			outputBuffer += "<li>" + helpstr + "</li>";
		}
		outputBuffer += "</ul>";		

		var visibilityHeader = Tolven.Util.getXMLContent(xmlDoc.getElementsByTagName("VisibilityColumnHeader")[0].childNodes[0]);
		var defaultHeader = Tolven.Util.getXMLContent(xmlDoc.getElementsByTagName("DefaultColumnHeader")[0].childNodes[0]);
		var menuHeader = Tolven.Util.getXMLContent(xmlDoc.getElementsByTagName("MenuColumnHeader")[0].childNodes[0]);
		
		outputBuffer += "<table style='border:none;padding:0;'>";
		outputBuffer += "<tr><td style='padding:0 0 0 20px' width='50px'>" + visibilityHeader +"</td>";
		outputBuffer += "<td width='50px'>" + defaultHeader +"</td>";
		outputBuffer += "<td>" + menuHeader +"</td>";
		outputBuffer += "</tr></table>";

		outputBuffer += "<ul id=\"thelist2\" class=\"userpreful\">";
		for( var i = 0; i < xmlDoc.getElementsByTagName("element").length; i++){
			var path = Tolven.Util.getAttributeValue( xmlDoc.getElementsByTagName("element")[i].attributes, "path");
			var checked = Tolven.Util.getAttributeValue( xmlDoc.getElementsByTagName("element")[i].attributes, "visible");
			var disabled = "";
			if( checked != "true") disabled="disabled='true'";
			if( checked == "true") checked = "checked=\"checked\"";
			else checked = "";
			var radioselect = "";
			if( path == (parentPath + defPath)) radioselect = "checked=\"true\""; 
			var txt = xmlDoc.getElementsByTagName("element")[i].childNodes[0].nodeValue;

			outputBuffer += "<li id=\"li:" + path + "\">";
			outputBuffer += "<table style='border:none;padding:0;'>";
			outputBuffer += "<tr><td  style='padding:0 0 0 20px' width='50px'>";
			outputBuffer += "<input type=\"checkbox\" value=\"" + path + "\" " + checked + " onclick=\"checkSibling(this);\" /> ";
			outputBuffer += "</td><td width='50px'>";
			outputBuffer += "<input type=\"radio\" name=\"defaultPath\" path=\"" + parentPath + "\" value=\"" + path + "\" " + disabled + " " + radioselect + "/>   ";
			outputBuffer += "</td><td>";
			outputBuffer += txt; 
			outputBuffer += "</td></tr></table>";
			outputBuffer += "</li>";

		}
		outputBuffer += "</ul>";
		outputBuffer += "<div style=\"text-align:center\">";

		var defaultTxt = Tolven.Util.getXMLContent(xmlDoc.getElementsByTagName("DefaultButton")[0].childNodes[0]);
		var saveTxt = Tolven.Util.getXMLContent(xmlDoc.getElementsByTagName("SaveButton")[0].childNodes[0]);
		var cancelTxt = Tolven.Util.getXMLContent(xmlDoc.getElementsByTagName("CancelButton")[0].childNodes[0]);

		outputBuffer += " <input type=\"button\" value=\"" + defaultTxt + "\" onclick=\"setDefaultPreferences('" + parentPath + "');\"/> ";
		outputBuffer += " <input type=\"button\" value=\"" + saveTxt + "\" onclick=\"savePreferences();\"/> ";
		outputBuffer += " <input type=\"button\" value=\"" + cancelTxt + "\" onclick=\"closePrefDiv();\" /> ";
		
		outputBuffer += "</div></div></div>";
		writeUserPreferenceInnerHTML(title, outputBuffer );
		$("userprefDiv").style.display = 'block';
		$("userprefDiv").style.top = document.body.clientHeight * .20 + "px";
		$("userprefDiv").style.left = document.body.clientWidth * .30 + "px";
		enableSortable();
	}catch(err) {alert(err.description);}

}

function setUserPreferences( userMenuList ){
	var instAjax = new Ajax.Request(
			'setuserpreferences.ajaxc', 
			{
				method: 'post', 
				parameters: 'userpreference='+userMenuList+'&accountUser='+accountUserId, 
				onComplete: closeUserPreferencesDiv
			});
}

// Get the list from the popup and send it to the server in the same order. 
// The server will determine if the sequence/visibilty has been changed.
function savePreferences(){
  var lis = $('thelist2').getElementsByTagName("li");
  var userMenuList = "";
  var defaultSelection = undefined;
  var parentPath;
  for( var ii = 0; ii < lis.length; ii++ ){ 
	var cboxs = lis[ii].getElementsByTagName("input");
	for(var j = 0; j < cboxs.length; j++){

	  if( cboxs[j].type == "checkbox" ){ // Set order
		userMenuList += cboxs[j].value + "," + cboxs[j].checked + ";";

	  }else if( cboxs[j].type == "radio" && cboxs[j].checked == true){// Set default view for the parent.
		defaultSelection = cboxs[j].value;
		parentPath = cboxs[j].attributes["path"].value;
	  }
	}
	
  }
  if( defaultSelection == undefined || parentPath == undefined) {
	alert("Select a default view.");
	return;
  }
  var defaultPathAttribute = "&parentPath=" + parentPath + "&defaultView=" + defaultSelection;
  setUserPreferences( userMenuList + defaultPathAttribute );

}

function getSummaryPreferencesFromServer( element, role,timelineId){
	var instAjax = new Ajax.Request(
			contextPath + '/ajax/userpreferences.jsf', 
			{
				method: 'get', 
				parameters: 'element='+element+'&role=' + role + '&accountUser='+accountUserId, 
				onComplete: getmoresummaries
			});
}

function writeUserPreferenceInnerHTML( title, reqHTML){
	
	var prefHTML = "";
	prefHTML += "<table cellspacing='5' cellpadding='5'>";
	prefHTML += "<tr><td class='userpreftitle'><span style='float:left'>" + title + "</span>";
	prefHTML += "<a class='closetab' href='javascript:closePrefDiv();'></a></td>";
	prefHTML += "</tr><tr><td>";

	prefHTML += reqHTML;

	prefHTML += "</td></tr>";
	prefHTML += "</table>";
	$("userprefDiv").innerHTML = prefHTML;

	$("faderPane").style.display = 'block';
	$("faderPane").observe('click', observeFaderPane);	
}

function getmoresummaries( res ){
	var xmlDoc = res.responseXML;
	var title  = Tolven.Util.getXMLContent(xmlDoc.getElementsByTagName("head")[0].childNodes[0]);
	var reqHTML = Tolven.Util.getXMLContent(xmlDoc.getElementsByTagName("body")[0].getElementsByTagName("div")[0]);
	writeUserPreferenceInnerHTML(title, reqHTML );
	$("userprefDiv").style.display = 'block';
	$("userprefDiv").style.top = document.body.clientHeight * .30 + "px";
	$("userprefDiv").style.left = document.body.clientWidth * .30 + "px";
}

function saveAddMoreSummaries(){
  var lis = $('thelist2').getElementsByTagName("li");
  var userMenuList = "";
  for( var ii = 0; ii < lis.length; ii++ ){ 
	var cboxs = lis[ii].getElementsByTagName("input");
	var seq = cboxs[ 0 ].getAttribute("sequence");
	userMenuList += cboxs[0].value + ",v_" + cboxs[0].checked + "," + seq + ";";
  }
	setSummaryPreferences( userMenuList, true );
	closePrefDiv();
}

//Rollback to default menustructure.. ie. Remove all user preferences for the particular path.
function setDefaultPreferences( element ){
	var instAjax = new Ajax.Request(
		'setdefaultpreferences.ajaxc', 
		{
			method: 'get', 
			parameters: 'element='+element+'&accountUser='+accountUserId, 
			onComplete: closeUserPreferencesDiv
		});
}

function closeUserPreferencesDiv(){
	closePrefDiv();
	alert( "update complete now refresh"  );
	Tolven.Util.windowrefresh();
}

DynaLoad.alertScriptLoaded();