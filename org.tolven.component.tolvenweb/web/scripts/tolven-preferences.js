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

/**
 * To get patient list designer information from server.
 *
 * Author Valsaraj
 * Added on 04/27/2010 
 */
function getPatientListFromServer(element, role){
	var instAjax = new Ajax.Request(
		'getmenuitems.ajaxc', 
		{
			method: 'get', 
			parameters: 'element='+element+'&role=' + role + '&accountUser='+accountUserId + '&source=pld', 
			onComplete: patientListView
		});
	hideDialog();
}

/**
 * Updates patient list via AJAX each time the user presses 'More' tab.
 *
 * Author Valsaraj
 * Added on 05/14/2010 
 */
function updatePatientListFromServer(element, role, placeholderid, dropDownID){
	var instAjax = new Ajax.Request(
		'getmenuitems.ajaxc', 
		{
			method: 'get', 
			parameters: 'element='+element+'&role=' + role + '&accountUser='+accountUserId, 
			onComplete: function(req) { updatePatientList(req, placeholderid, dropDownID); }
		});	
}

/**
 * To get and display summary using AJAX.
 *
 * Author Valsaraj
 * Added on 05/25/2010 
 */
function showSummaryFromServer(element, extension) {
	var instAjax = new Ajax.Request(
		'getSummary.ajaxcchit', 
		{
			method: 'get', 
			parameters: 'element='+element+'&extension='+extension, 
			onComplete: displaySummaryPopup
		});
	hideDialog();
}

/**
 * To check patient list exists or not from server.
 *
 * Author Valsaraj
 * Added on 07/13/2010 
 */
function checkDuplicateFromServer(value, msgContainerId, element, role) {
	var instAjax = new Ajax.Request(
		'getmenuitems.ajaxc', 
		{
			method: 'get', 
			parameters: 'element='+element+'&role=' + role + '&accountUser='+accountUserId, 
			onComplete: function(req) { validateDuplicate(req, value, msgContainerId, element); }
		});
}

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

/**
 * Modified to display new button 'Editor' to to display PatientList Designer page 
 * and to avoid showing "echr:patients:patientListDefinition" from patient lists.
 *
 * modifed on 04/05/2010 by Valsaraj
 */
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
			
			if ("echr:patients:patientListDefinition"!=path) {
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
		}
		outputBuffer += "</ul>";
		outputBuffer += "<div style=\"text-align:center\">";

		var defaultTxt = Tolven.Util.getXMLContent(xmlDoc.getElementsByTagName("DefaultButton")[0].childNodes[0]);
		var saveTxt = Tolven.Util.getXMLContent(xmlDoc.getElementsByTagName("SaveButton")[0].childNodes[0]);
		var cancelTxt = Tolven.Util.getXMLContent(xmlDoc.getElementsByTagName("CancelButton")[0].childNodes[0]);

		if (xmlDoc.getElementsByTagName("EditorButton")[0] != null) {
			var editorTxt = Tolven.Util.getXMLContent(xmlDoc.getElementsByTagName("EditorButton")[0].childNodes[0]);
			outputBuffer += " <input type=\"button\" value=\"" + editorTxt + "\" onclick=\"showPane('echr:patients:patientListDefinition', false, 'echr:patients:patientListDefinition'); closePrefDiv();\"/> "; 
		}
		
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

/**
 * To parse and build patient list designer information.
 *
 * Author Valsaraj
 * Added on 04/27/2010 
 */
function patientListView(req){
	var xmlDoc = req.responseXML;
	
	try{
		outputBuffer = "<table width='100%'><tr class='allergies'><td align='center' style='padding:15px;'><span style='color:white; font-weight:bold; font-size:18px;'>Patient List Designer</span></td></tr><tr><td align='center'><div style='width:90%; border: 1px solid rgb(153, 153, 153);'><table width='100%'>";
		listData = '';
		
		for( var i = 0; i < xmlDoc.getElementsByTagName("element").length; i++){
			var path = Tolven.Util.getAttributeValue( xmlDoc.getElementsByTagName("element")[i].attributes, "path");
			
			if ("echr:patients:patientListDefinition"!=path) {
				var txt = xmlDoc.getElementsByTagName("element")[i].childNodes[0].nodeValue;
				var reference = Tolven.Util.getAttributeValue( xmlDoc.getElementsByTagName("element")[i].attributes, "reference");
				listData += "<tr style='padding:10px;'><td><a href='#' onclick=\"instantiate('patientListDesigner','" + reference + "','" + reference + "');\">";
				listData += txt;
				listData += "</a></td><td style='padding:10px;'>";
				listData += txt;
				listData += "</td><td style='padding:10px;'>PatientList</td></tr>";
			}
		}
		
		if (listData.length==0) {
			listData += "<tr style='padding:10px;'><td colspan='3' align='center' valign='middle' style='height:100px;'>You have not created any lists yet.</td></tr>";
		}
		else {
			outputBuffer += "<tr style='background-color: #DEDADA;'><td style='padding:10px;'><b>Title</b></td><td style='padding:10px;'><b>Display Name</b></td><td style='padding:10px;'><b>Type</b></td></tr>";
		}
		
		outputBuffer += listData;
		outputBuffer += "</table></div></td></tr><tr class='allergies'><td class='submit'><input id='refreshButton' style=width: 70px;' type='button' value='Refresh' onclick=\"refreshList('echr:patients:all');\"/><input id='newButton' style='width: 70px;' type='button' value='New' onclick=\"instantiate('patientListDesigner','echr:patients:patientListDefinition','');\"/><input id='cancelButton' style=width: 70px;' type='button' value='Cancel' onclick=\"showPane('echr:patients:all', false, 'echr:patients:all');\"/></td><br /><br /></tr></table>";
		$("patListDesigner").innerHTML = outputBuffer;
	}
	catch(err) {
		
	}
}

/**
 * To update patient list using latest data received from server.
 *
 * Author Valsaraj
 * Added on 05/14/2010 
 */
function updatePatientList(req, placeholderid, dropDownID) {
	var xmlDoc = req.responseXML;
	
	try {
		var newLists = new Array();
		var existingList = $("echr:patients").innerHTML;
		var count = 0;
		
		for( var i = 0; i < xmlDoc.getElementsByTagName("element").length; i++) {
			var path = Tolven.Util.getAttributeValue( xmlDoc.getElementsByTagName("element")[i].attributes, "path");
			var checked = Tolven.Util.getAttributeValue( xmlDoc.getElementsByTagName("element")[i].attributes, "visible");
				
			if ( checked == "true" && "echr:patients:patientListDefinition"!=path) {				
				if (existingList.indexOf(path)==-1) {
					var txt = xmlDoc.getElementsByTagName("element")[i].childNodes[0].nodeValue;
					newLists[count]=path+'|'+txt;
					count++;
				}
			}
		}
		
		drpDwnElement = $("echr:patients_bar1_drpDwn");
		
		if (count!=0) {	
			var newHTML = drpDwnElement.innerHTML;
			var idx=newHTML.indexOf('<li>');
			
			if (idx==-1) {
				idx=newHTML.indexOf('<li class="showDrpDwn" id="echr:patients_bar1_showDrpDwn">');
			}
			
			if (idx==-1) {
				idx=newHTML.indexOf('<li class="">');
			}
		
			if (idx!=-1) {
				endStr = newHTML.substring(idx);
				newHTML = newHTML.replace(endStr, '');
				
				for (iter=0;iter<count;iter++) {
					item = newLists[iter];
					var values = item.split('|');			
					newHTML += '<li id="' + values[0] + ':sel">' +
									"<a href=\"javascript:showPane('" + values[0] + "', false, '" + values[0] + "');\"> " + values[1] + '</a></li>';
				}
		
				drpDwnElement.innerHTML=newHTML+endStr;				
			}
		}
		
		dialog = $("_drpDwn");
		var show = false;
		drpDwnElement = $(dropDownID);
		if(drpDwnElement == null)
			return;
		place_drpDwn = $(placeholderid);
		var pos = Position.cumulativeOffset(place_drpDwn);
		lefx = pos[0] + "px";//+ place_drpDwn.parentNode.offsetWidth - dialog.offsetWidth)+"px";
		topy = pos[1]+"px";;
		dialog.style.left = lefx;
		dialog.style.top = topy;
		
		if(dialog.name != drpDwnElement.id)
			show = true;
		dialog.innerHTML = drpDwnElement.innerHTML;
   		dialog.name = drpDwnElement.id;
		if(show)
			dialog.show();
		else
			dialog.toggle();
		if(dialog.visible()){
			dialogOffsetRt = dialog.offsetLeft+ dialog.offsetWidth;
			paneComp = $("paneArea");
			paneOffsetRt = paneComp.offsetLeft+  paneComp.offsetWidth;
			if(dialogOffsetRt > paneOffsetRt ){
				dialog.style.left = paneOffsetRt - dialog.offsetWidth + "px";
			}
		}
	}
	catch(err) {
		alert(err.description);
	}
}

/**
 * To display summary using AJAX.
 *
 * Author Valsaraj
 * Added on 05/25/2010 
 */
function displaySummaryPopup(req) {
	displayPopup('Summary', req.responseText, .30, .30);
}

function displayPopup(title, message, height, width) {
	try {		
		var prefHTML = "";
		prefHTML += "<table cellspacing='5' cellpadding='5'>";
		prefHTML += "<tr><td class='userpreftitle'><span style='float:left'>" + title + "</span>";
		prefHTML += "<a class='closetab' href='javascript:closePrefDiv();'><img src=\"/Tolven/images/close.gif\"/></a></td>";
		prefHTML += "</tr><tr><td>";	
		prefHTML += message;	
		prefHTML += "</td></tr>";
		prefHTML += "</table>";
		$("userprefDiv").innerHTML = prefHTML;	
		$("faderPane").style.display = 'block';
		$("faderPane").observe('click', observeFaderPane);
		$("userprefDiv").style.display = 'block';
		$("userprefDiv").style.top = document.body.clientHeight * height + "px";
		$("userprefDiv").style.left = document.body.clientWidth * width + "px";
	}
	catch (err) {
		alert(err.description);
	}
}

/**
 * To check patient list exists or not and display error message if duplicate list.
 *
 * Author Valsaraj
 * Added on 07/13/2010 
 */
function validateDuplicate(req, value, msgContainerId, element) {
	var xmlDoc = req.responseXML;
	value = value.replace(/\s+/g,'').toLowerCase();
	status = true;
	
	try{
		for( var i = 0; i < xmlDoc.getElementsByTagName("element").length; i++){
			var path = Tolven.Util.getAttributeValue( xmlDoc.getElementsByTagName("element")[i].attributes, "path");
			var txt = xmlDoc.getElementsByTagName("element")[i].childNodes[0].nodeValue;
			txt = txt.replace(/\s+/g,'').toLowerCase();
			
			if (element+':'+value==path || value==txt) {
				status = false;
			}
		}
		
		if (status!=true) {
			$(msgContainerId).innerHTML = 'Patient list already exists.';
		}
		else {
			$(msgContainerId).innerHTML = '';
		}
	}
	catch(err) {
		
	}
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
	prefHTML += "<a class='closetab' href='javascript:closePrefDiv();'><img src=\"/Tolven/images/close.gif\"/></a></td>";
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