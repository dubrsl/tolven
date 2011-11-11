    var messageId ;
    var isDiscontinue = false;
    var isNewPrescription = false;
    var patientError = false;
// Submit specified element (nothing is uploaded, which will already be done
function wizTransitioneRx( element, action, status ,msgId) {
	var statusRecieved = status;
    closeEventBubble();
    if(element.endsWith('identity')){
    		element = element.split(':')[0] + ":" + element.split(':')[1] + ":" + "medications:active" ;    		
    		messageId = msgId;
    }
    else{
    		element = element;
    }
    var myAjax = new Ajax.Request(
   'instantiateTrim.ajaxerx',
   {
    method: 'get',
     parameters: '&action='+action+
    			'&context='+element+
    			'&messageId='+messageId+
    			'&status='+status,
    onFailure: function(request) {displayError(request,element);},
    onSuccess: function(request) { 
    	if(statusRecieved == "1"){
    		if(request.responseText!="shared"){
    			wizTransitionIsDone(request);
    			isDiscontinue = true;
    			isNewPrescription = false;
    		}else{
    			alert("Medication status changed to inactive successfully.");
    			closePopUp1();
    		}
    	}else{
    		wizTransitionDone(request);
    		isDiscontinue = false;
    		isNewPrescription = true;
    	}
    }	          
   });
    
}

function wizTransitionIsDone( request ) {
	// Tolven.Util.log( "response: " + request.responseText );
	 var responses = request.responseText.split(",");
	 if($(responses[0])){
		 closeTab(responses[0]); // Which pane to close
	 }
	 if (responses[1]!=null){
		 oneSecondShow = responses[1]; // Which pane to open
		 recentSubmit();
	 }
}
function showErrorDescripton(spanId){
	rowId = spanId.parentNode.parentNode;
	if (rowId.cells[2]!=null){ 
		var medStatus = rowId.cells[2].childNodes[0].innerHTML;
	}
	if (rowId.cells[3]!=null) {
		var phyStatus = rowId.cells[3].childNodes[0].innerHTML;
	}
	if (rowId.cells[4] !=null) {
		var phyLocStatus = rowId.cells[4].childNodes[0].innerHTML;
	}
	if(medStatus == 'Error' || phyStatus == 'Error' || phyLocStatus == 'Error'){
		if (medStatus == 'Error') {
			messageId = rowId.cells[3].innerHTML;
		}else if(phyStatus == 'Error'){
			messageId = rowId.cells[4].innerHTML;
		}else if(phyLocStatus == 'Error'){
			messageId = rowId.cells[5].innerHTML;
		}
		
		var messageType = rowId.cells[1].childNodes[0].innerHTML;
		var instAjax = new Ajax.Request(				
				'showError.ajaxsure',
				{
					method: 'get',
					parameters: '&messageId='+messageId+'&messageType='+messageType,
					onComplete: function(response){
						var fullResponse = response.responseText;
		     		 	var errorDescription = fullResponse.split('|');
		     		 	if (errorDescription[1]) {
		     		 		alert("Error Description: "+errorDescription[1]);
						}else{
							alert("Invalid message id");
						}
				},
					onFailure: function(request) {}
				});
	}else{
		alert("No error message to display");
	}
}
/**
*Function to load values into the pop-up menu.
*/
function showPopup(spanId){
	rowId = spanId.parentNode.parentNode;
	var accountType = document.getElementById("accountType").value;
	if(accountType == 'echr'){
		document.getElementById("prescriber").innerHTML="Prescribed By";
		document.getElementById("medicationNew").disabled=false;
		document.getElementById("medicationAdmin").disabled=false;
		document.getElementById("medicationModify").disabled=false;
		document.getElementById("medicationRefill").disabled=false;
		document.getElementById("medicationReport").disabled=false;
	}	
	var offsetTop = rowId.offsetTop;	
	var urlPrefix = "transparent url(/Tolven/scripts/simile/images/";
	var urlSuffix = ") repeat scroll 0% 0%";
	
	function createPopup(pos, image, left, top, width, height){
		var popup = document.getElementById(pos);
		if(!(pos == "content")){
			popup.style.background = urlPrefix+ image +urlSuffix ;
			popup.style.position = "absolute";
		}
		else{
			popup.style.background = "white none repeat scroll 0% 0%";
			popup.style.overflow = "auto";
			popup.style.position = "absolute";
		}
		popup.style.left = left + "px";
		popup.style.top = (offsetTop+top) + "px";
		popup.style.width = width + "px";
		popup.style.height = height + "px";	
	}
	createPopup("top-left", "bubble-top-left.png", 0, 0, 33, 33);
	createPopup("top", "bubble-top.png", 33, 0, 250, 33);
	createPopup("top-right", "bubble-top-right.png", 283, 0, 40, 33);
	createPopup("left", "bubble-left.png", 0, 33, 33, 125);
	createPopup("right", "bubble-right.png", 283, 33, 40, 125);
	createPopup("bottom-left", "bubble-bottom-left.png", 0, 158, 33, 42);
	createPopup("bottom", "bubble-bottom.png", 33, 158, 250, 42);
	createPopup("bottom-right", "bubble-bottom-right.png", 283, 158, 40, 42);
	createPopup("close-button", "close-button.png", 280, 19, 16, 16);
	createPopup("content", "", 33, 33, 250, 125);
	createPopup("bottom-arrow", "bubble-bottom-arrow.png", 140, 158, 37, 42);
	
	if(accountType == 'echr'){		
		messageId = rowId.cells[0].childNodes[0].innerHTML;
		//alert(messageId);
		if(messageId != '&nbsp;'){		
			var medicationName = rowId.cells[2].innerHTML;
			var prescribedBy = rowId.cells[9].innerHTML;
			var strength = rowId.cells[3].innerHTML;
			var route = rowId.cells[6].innerHTML;
			var refill = rowId.cells[8].innerHTML;
			
			document.getElementById("eventBubble").style.display="block";
			document.getElementById("medicationName").innerHTML = medicationName;
			document.getElementById("prescribedBy").innerHTML = prescribedBy;
			document.getElementById("strength").innerHTML = strength;
			document.getElementById("route").innerHTML = route;
			document.getElementById("refill").innerHTML = refill;
			closePopUp();
		}
	}
	else if(accountType == 'ephr'){
		messageId = rowId.cells[0].childNodes[0].innerHTML;
		if(messageId != '&nbsp;'){		
			var medicationName = rowId.cells[2].innerHTML;
			var strength = rowId.cells[3].innerHTML;
			var route = rowId.cells[7].innerHTML;
			
			document.getElementById("eventBubble").style.display="block";
			document.getElementById("medicationName").innerHTML = medicationName;
			document.getElementById("strength").innerHTML = strength;
			document.getElementById("route").innerHTML = route;
			closePopUp();
		}
	}	
	else return;
}

// Ask the server to instantiate a new item and wait for the new element name
function instantiateTrim( templateId, context, source) {
		var sourceParam;
		if (source == undefined) {
			sourceParam = "";
		} else {
			sourceParam = '&source='+source;
		}
		context = "echr:patient-"+context.split('-')[1].split(':')[0]+":currentMedication-"+messageId;
		var param = 'templateId='+templateId+'&context='+context+sourceParam+'&messageId='+messageId;
		var instAjax = new Ajax.Request(
			'instantiateTrim.ajaxerx',
			{
				method: 'get',
				parameters: param,
				onSuccess: showNewInstance,
				onFailure: function(request) {displayError(request,param);}
			});
}

var flag =0;
function refreshPage(element,elementLabel,step){
	//alert(flag+" "+element+" "+elementLabel+" "+step)
	//if(flag==0){
		refreshWizard(element,elementLabel,step)
		//setTimeout("refreshWizard('"+element+"','"+elementLabel+"','"+step+"');",3000);
		flag = 1;
	//}
}
 
/**
*Functions to show the FDB Details
* @author Rais
* added on 14/09/2009
*/
function enablePopUP() {
	alert('enablePopUP in ePrescription.js will be removed');
	document.getElementById("rowId").value = "0";
	document.getElementById("medicationFromFDBPopUP").style.display="block";
	if (screen) { 
	   leftPos = (screen.width / 2) - 251;
	   topPos = (screen.height / 2) - 162;
	   document.getElementById("medicationFromFDBPopUP").style.left=leftPos+"px";
	   document.getElementById("medicationFromFDBPopUP").style.top=topPos+"px";
	}
	document.getElementById("startLimit").innerHTML = "1";
	document.getElementById("endLimit").innerHTML = "14";								
}
function closePopUp(formId,popId){
	$(formId+":"+popId).style.display="none";
	$(formId).removeAttribute('ajaxSubmit3InProgress');
}

function closePopUp1(){
	$("eventBubble").style.display="none";
}

/**
*To add medication in the ephr patient medication wizard.
**/
function saveCode1(obj , formId , element){
$(formId + ":hiddenDrugName").value = obj.parentNode.parentNode.childNodes[3].childNodes[0].innerHTML;
var code = obj.innerHTML;
var drug = escape(obj.parentNode.parentNode.childNodes[3].childNodes[0].innerHTML);
 var myAjax = new Ajax.Request(
					'fdbMedication.ajaxfdb', 
    			{
    			 method: 'get', 
    			 parameters: '&drug_name='+drug+'&element='+element+'&code='+code, 
         		 onFailure: function() {
         		 location.href=document.URL;
         		 },
	     		  onComplete: function(response) {
	     		 	var fullResponse = response.responseText.split('|');
	     		 	$(formId + ":strength").value = fullResponse[1];
	     		 	$(formId + ":route").value = fullResponse[2];
	     		 	
	    		  }
   				 });
   	$(formId + ":medicineSelected").value = obj.parentNode.parentNode.childNodes[3].childNodes[0].innerHTML;
closePopUp();
}
/**
 * To disable the child nodes of a container on radio, checkbox and select tag events 
 * @author suja
 * added on 5/27/09
 */
wizDisableChildNodes=function(root,elementId,status,containerId){
  if($(containerId) == null)
	return;
  //select the container elements
  var _fieldsToDisable = $(containerId).descendants(); 
  if(!_fieldsToDisable)
    return;
  //iterate the container element list
  for(i=0;i<_fieldsToDisable.length;i++){
    if(_fieldsToDisable[i].tagName.toLowerCase() == 'input' || _fieldsToDisable[i].tagName.toLowerCase() == 'select'){
	  //for radio button and check box event
      if($(root+":"+elementId).tagName.toLowerCase()=='input' && ($(root+":"+elementId).type=='radio' || $(root+":"+elementId).type=='checkbox')){
		if(_fieldsToDisable[i].id != root+":"+elementId){
		  if($(root+":"+elementId).checked==status && $(root+":"+elementId).disabled==false){
			_fieldsToDisable[i].removeAttribute("disabled"); //enable child element
          }else{
			_fieldsToDisable[i].disabled = true; //disable child element
          }
		}
	  }
      //for drop down event
	  if($(root+":"+elementId).tagName.toLowerCase()=='select'){
		if($(root+":"+elementId)[$(root+":"+elementId).selectedIndex].text!=status && $(root+":"+elementId).disabled==false){
		  _fieldsToDisable[i].removeAttribute("disabled"); //enable child element
        }else{
        	_fieldsToDisable[i].disabled = true; //disable child element
        }
	  }
	}
  }
}




/**
*
*/
function saveDrug1(obj , formId , element){
$(formId + ":hiddenDrugName").value = obj.innerHTML;
var drug = escape(obj.innerHTML);
var code = obj.parentNode.parentNode.childNodes[1].childNodes[0].innerHTML;
 var myAjax = new Ajax.Request(
					'fdbMedication.ajaxfdb', 
    			{
    			 method: 'get', 
    			 parameters: '&drug_name='+drug+'&element='+element+'&code='+code, 
         		 onFailure: function() {
         		 location.href=document.URL;
         		 },
	     		  onComplete: function(response) {
         			var fullResponse = response.responseText.split('|');
	     		 	$(formId + ":strength").value = fullResponse[1];
	     		 	$(formId + ":route").value = fullResponse[2];
	     		 	
	     		 }
   				 });	
   				 $(formId + ":medicineSelected").value = obj.innerHTML;
closePopUp();
}

function startFiltering(formId , element){
	
	 var filter = escape($(formId + ":filterText").value);
	 
	// if(filter != "" && null != filter){
		
		 document.getElementById("ajaxLoader").style.display="block";	
var myAjax = new Ajax.Request(
					'drugsUpdate.ajaxfdb', 
    			{
    			 method: 'get', 
    			 parameters: '&filter_condition='+filter, 
    			 onProcessing:function(){showProcessing()},
         		 onFailure: function() {hideProcessing();location.href=document.URL;},
				     		  onComplete: function(response) {
         			document.getElementById("ajaxLoader").style.display="none";	
				     		  		var drugs= response.responseText.split('|');
				     		  		var totalCount = drugs.length - 1;
				     		  		var table = $(formId + ":drugTable");
				     		 		var rowCount = table.rows.length;
									var j = rowCount -1 ;
									var flag;
												 for(var i=j; i>=0; i--) {
									        		$(formId + ":drugTable").deleteRow(i);
									        		j--;
												 }
									flag1 = 0;
									 for(var k=0; k<totalCount; k++){
									  var drug = drugs[k].split('$');
									 	var row = $(formId + ":drugTable").insertRow(k);
									 	row1 = $(formId + ":drugTable").rows[k];
									 	var cell1 = row1.insertCell(0);
									 	var cell2 = row1.insertCell(1);
									 	var cell3 = row1.insertCell(2);
									 	cell1.width="60px";
									 	cell2.width="430px";
									 	cell3.width="110px";
									 	var text = document.createTextNode(drug[0]);
									 	var text1 = document.createTextNode(drug[1]);
									 	var text2 = document.createTextNode(drug[2]);
									 	cell1.appendChild(text);
									 	cell2.appendChild(text1);
									 	cell3.appendChild(text2);
									 	$(formId + ":drugTable").rows[k].cells[0].id="code"+k;
									 	$(formId + ":drugTable").rows[k].cells[1].id="med"+k;
									 	$(formId + ":drugTable").rows[k].id="drugFromFDB"+k;
										var drugName = document.getElementById("drugFromFDB"+k).childNodes[1].innerHTML;
									 	
										if(strStartsWith(document.getElementById("formIdValue").value, "echr") == 0){
									 	document.getElementById("code"+k).onclick=handleClick;
									 	document.getElementById("med"+k).onclick=handleMedClick;
							 
									 	}else{
									 	document.getElementById("code"+k).onclick=handleClick1;
									 	document.getElementById("med"+k).onclick=handleMedClick1;
							 
									 	}
									 	
									 	
									 	
											if(flag1 == 0){
											  $(formId + ":drugTable").rows[k].className="odd";
											   flag1 =1;
											}else if(flag1 ==1){
												$(formId + ":drugTable").rows[k].className="even";
											   flag1 =0;
											}
											
											$(formId + ":drugTable").rows[k].style.cursor="pointer";
											$(formId + ":drugTable").rows[k].cells[0].style.textDecoration="underline";
											$(formId + ":drugTable").rows[k].cells[1].style.textDecoration="underline";
									}
									 
									 
									if($(formId + ":filterText").value != ""){
										$(formId + ":filteredcount").innerHTML=" / "+totalCount+" filtered items";
									}else{
										$(formId + ":filteredcount").innerHTML="";
									}
									if(totalCount < 14){
				     		 			document.getElementById("startLimit").innerHTML = "1";
										document.getElementById("endLimit").innerHTML = totalCount;	
				     		 		}else if(totalCount == 0){
				     		 			document.getElementById("startLimit").innerHTML = "0";
										document.getElementById("endLimit").innerHTML = "0";	
				     		 		}else{
				     		 			document.getElementById("startLimit").innerHTML ="1";
										document.getElementById("endLimit").innerHTML = "14";
				     		 		}
										hideProcessing();
										document.getElementById("rowId").value = "0";
								}
   				 });
 		//$(formId + ":filterButton").click();   
	//} if loop
	
 var drugAjax = new Ajax.Request(
		 'getTotalDrugCount.ajaxfdb',
		 {
		  method : 'get',
		  parameters: '&filter='+filter,
		  onFailure: function(){
			 location.href=document.URL;
		  },
		  onComplete: function (response) {
			  document.getElementById(formId+":totalcount").innerHTML = " " +response.responseText + " items";
		  }	  
		 });
}

function startOTCFiltering(formId , element){
	
	 var filter = escape($(formId + ":filterText").value);
	 
	// if(filter != "" && null != filter){
		
		 document.getElementById("ajaxLoader").style.display="block";	
var myAjax = new Ajax.Request(
					'otcDrugsUpdate.ajaxfdb', 
   			{
   			 method: 'get', 
   			 parameters: '&filter_condition='+filter, 
   			 onProcessing:function(){showProcessing()},
        		 onFailure: function() {hideProcessing();location.href=document.URL;},
				     		  onComplete: function(response) {
        			document.getElementById("ajaxLoader").style.display="none";	
				     		  		var drugs= response.responseText.split('|');
				     		  		var totalCount = drugs.length - 1;
				     		  		var table = $(formId + ":drugTable");
				     		 		var rowCount = table.rows.length;
									var j = rowCount -1 ;
									var flag;
												 for(var i=j; i>=0; i--) {
									        		$(formId + ":drugTable").deleteRow(i);
									        		j--;
												 }
									flag1 = 0;
									 for(var k=0; k<totalCount; k++){
									  var drug = drugs[k].split('$');
									 	var row = $(formId + ":drugTable").insertRow(k);
									 	row1 = $(formId + ":drugTable").rows[k];
									 	var cell1 = row1.insertCell(0);
									 	var cell2 = row1.insertCell(1);
									 	var cell3 = row1.insertCell(2);
									 	cell1.width="60px";
									 	cell2.width="430px";
									 	cell3.width="110px";
									 	var text = document.createTextNode(drug[0]);
									 	var text1 = document.createTextNode(drug[1]);
									 	var text2 = document.createTextNode(drug[2]);
									 	cell1.appendChild(text);
									 	cell2.appendChild(text1);
									 	cell3.appendChild(text2);
									 	$(formId + ":drugTable").rows[k].cells[0].id="code"+k;
									 	$(formId + ":drugTable").rows[k].cells[1].id="med"+k;
									 	$(formId + ":drugTable").rows[k].id="drugFromFDB"+k;
										var drugName = document.getElementById("drugFromFDB"+k).childNodes[1].innerHTML;
									 	
										if(strStartsWith(document.getElementById("formIdValue").value, "echr") == 0){
									 	document.getElementById("code"+k).onclick=handleClick;
									 	document.getElementById("med"+k).onclick=handleMedClick;
							 
									 	}else{
									 	document.getElementById("code"+k).onclick=handleClick1;
									 	document.getElementById("med"+k).onclick=handleMedClick1;
							 
									 	}
									 	
									 	
									 	
											if(flag1 == 0){
											  $(formId + ":drugTable").rows[k].className="odd";
											   flag1 =1;
											}else if(flag1 ==1){
												$(formId + ":drugTable").rows[k].className="even";
											   flag1 =0;
											}
											
											$(formId + ":drugTable").rows[k].style.cursor="pointer";
											$(formId + ":drugTable").rows[k].cells[0].style.textDecoration="underline";
											$(formId + ":drugTable").rows[k].cells[1].style.textDecoration="underline";
									}
									 
									 
									if($(formId + ":filterText").value != ""){
										$(formId + ":filteredcount").innerHTML=" / "+totalCount+" filtered items";
									}else{
										$(formId + ":filteredcount").innerHTML="";
									}
									if(totalCount < 14){
				     		 			document.getElementById("startLimit").innerHTML = "1";
										document.getElementById("endLimit").innerHTML = totalCount;	
				     		 		}else if(totalCount == 0){
				     		 			document.getElementById("startLimit").innerHTML = "0";
										document.getElementById("endLimit").innerHTML = "0";	
				     		 		}else{
				     		 			document.getElementById("startLimit").innerHTML ="1";
										document.getElementById("endLimit").innerHTML = "14";
				     		 		}
										hideProcessing();
										document.getElementById("rowId").value = "0";
								}
  				 });
		//$(formId + ":filterButton").click();   
	//} if loop
	
var drugAjax = new Ajax.Request(
		 'getTotalOTCDrugCount.ajaxfdb',
		 {
		  method : 'get',
		  parameters: '&filter='+filter,
		  onFailure: function(){
			 location.href=document.URL;
		  },
		  onComplete: function (response) {
			  document.getElementById(formId+":totalcount").innerHTML = " " +response.responseText + " items";
		  }	  
		 });
}


function handleClick1(obj){
var formId = document.getElementById("formIdValue").value;
var element = document.getElementById("elementValue").value;
$(formId + ":hiddenDrugName").value = obj.target.parentNode.childNodes[1].innerHTML;

var drug = escape(obj.target.parentNode.childNodes[1].innerHTML);
var code = obj.target.innerHTML;
 var myAjax = new Ajax.Request(
					'fdbMedication.ajaxfdb', 
    			{
    			 method: 'get', 
    			 parameters: '&drug_name='+drug+'&element='+element+'&code='+code, 
         		 onFailure: function() {
         		 location.href=document.URL;
         		 },
	     		  onComplete: function(response) {
         			var fullResponse = response.responseText.split('|');
	     		 	$(formId + ":strength").value = fullResponse[1];
	     		 	$(formId + ":route").value = fullResponse[2];
	    		  }
   				 });
   				 $(formId + ":medicineSelected").value = obj.target.parentNode.childNodes[1].innerHTML;
closePopUp();
}


function handleMedClick1(obj){
var formId = document.getElementById("formIdValue").value;
var element = document.getElementById("elementValue").value;
$(formId + ":hiddenDrugName").value = obj.target.innerHTML;
var drug = escape(obj.target.innerHTML);
var code = obj.target.parentNode.childNodes[0].innerHTML;
var myAjax = new Ajax.Request(
					'fdbMedication.ajaxfdb', 
    			{
    			 method: 'get', 
    			 parameters: '&drug_name='+drug+'&element='+element+'&code='+code, 
         		 onFailure: function() {
         		 location.href=document.URL;
         		 },
	     		  onComplete: function(response) {
         			var fullResponse = response.responseText.split('|');
	     		 	$(formId + ":strength").value = fullResponse[1];
	     		 	$(formId + ":route").value = fullResponse[2];
	    		  }
   				 });
   				 $(formId + ":medicineSelected").value = obj.target.innerHTML;
closePopUp();
}
function newMethod(formId){
	$(formId + ":computeEnable").value = $(formId + ":hiddenEnabled").value;
	$(formId + ":computeEnable1").value = $(formId + ":hiddenEnabled1").value;
	$(formId + ":computeDrugName").value = $(formId + ":hiddenDrugNameStorage").value;
	$(formId + ":computeFdbResponse").value = $(formId + ":hiddenFdbResponseStorage").value;
}

function showProcessing(formId){
	$(formId+":ajaxLoader").style.display="block";
}

function hideProcessing(formId){
	$(formId+":ajaxLoader").style.display="none";
}

function updateDrugs(obj){
var i = document.getElementById("rowId").value;
 i++;
document.getElementById("rowId").value = i;  				 
}


function getRowId(){
 document.getElementById("id1").value=9;

return document.getElementById("rowId").value;
}

function getLimit(){
return document.getElementById("rowId").value + 14;
}

function alertMessage(formId) {
	var drugComments = $(formId+':drugDrugCause').value;
	var foodComments = $(formId+':drugFoodCause').value;
	var duplicateComments= $(formId+':duplicateCause').value;
	var allergyComments= $(formId+':allergyCause').value;

	if($(formId+':hasDrugDrugInterraction').value == "true" 
	|| $(formId+':hasDrugFoodInterraction').value == "true" 
	|| $(formId+':hasDuplicateTherapy').value == "true"
	|| $(formId+':hasAllergyInterraction').value == "true"){
	$('errorDiv').style.left="100px";
	$('errorDiv').style.top="300px";
	$('errorDiv').style.width="800px";
	
	var errorDisplayFlag = "false";
	var heightDiv = 100;
	var errordata = 	"<table width='100%'><tr><td colspan='2'><br/><b>Some Issues with the Drugs Prescribed ! </b></td></tr>"
	if($(formId+':hasDrugDrugInterraction').value == "true" && $(formId+':physicianPrefDrugDrug').value == "true"){
	    errordata = errordata + "<tr><td colspan='2'><b>. Drug - Drug Interaction </b></td></tr><tr><td colspan='2'>"+drugComments+"</td></tr>" ;
	    heightDiv = heightDiv + 100;
	    errorDisplayFlag = "true";
	}
	if($(formId+':hasDrugFoodInterraction').value == "true" && $(formId+':physicianPrefDrugFood').value == "true"){
		errordata = errordata + "<tr><td colspan='2'><b>. Drug - Food Interaction </b></td></tr><tr><td colspan='2'>"+foodComments+"</td></tr>"
		heightDiv = heightDiv + 100;
		errorDisplayFlag = "true";
	}
	if($(formId+':hasDuplicateTherapy').value == "true"){
		errordata = errordata + "<tr><td colspan='2'> <b>. Duplicate Therapy </b></td></tr><tr><td colspan='2'>"+duplicateComments+"</td></tr>"
		heightDiv = heightDiv + 100;
		errorDisplayFlag = "true";
	}
	if($(formId+':hasAllergyInterraction').value == "true" && $('physicianPrefDrugAllergy').value == "true"){
		errordata = errordata + "<tr><td colspan='2'> <b>. Drug Allergy Interaction </b></td></tr><tr><td colspan='2'>"+allergyComments+"</td></tr>"
		heightDiv = heightDiv + 100;
		errorDisplayFlag = "true";
	}
	errordata = errordata + "<tr><td ><input type='button' value='Close' onclick='getBack()'/></td><td style='text-align: right'></td></tr> </table> "
	
	if (errorDisplayFlag == "true") {
		$('errorDiv').style.height=heightDiv+"px";
	    $('errorDiv').innerHTML = errordata;
		$('errorDiv').style.display="block";
		$('errorDiv').style.overflow="auto";
	}
	}
}

function overRide(formId){
$('errorDiv').style.display="none";
$('errorDivOverRide').style.top="300px";
$('errorDivOverRide').style.left="200px";
$('errorDivOverRide').style.width="460px";
$('errorDivOverRide').style.height="100px";
data = "<table><tr><td>Override Comments</td><td><input type='text' id='overridecomments' style='height:50px;width:300px;' /></td></tr><tr><td colspan='2' style='text-align:right'><input type='button' value='Submit'  onclick='hideError(this,'"+formId+"')' /></td></tr></table>";
$('errorDivOverRide').innerHTML=data;
$('errorDivOverRide').style.display="block";
$('errorDivOverRide').style.overflow = "auto";
}

function hideError(obj){
	$("drilldown:override").innerHTML = $('overridecomments').value;
var comments = obj.parentNode.parentNode.parentNode.childNodes[0].childNodes[1].childNodes[0].value;

var element = $("elementValue").value;
var myAjax = new Ajax.Request(
					'overRideComments.ajaxfdb', 
    			{
    			 method: 'get', 
    			 parameters: '&element='+element+'&comments='+comments, 
         		 onFailure: function() {hideProcessing();location.href=document.URL;},
				     		  onComplete: function(response) {
		
								}
   				 });

document.getElementById('hasDrugDrugInterraction').value = "false" ;
document.getElementById('hasDrugFoodInterraction').value = "false" ;
document.getElementById('hasDuplicateTherapy').value = "false";
$('errorDivOverRide').style.display="none";
refreshWizard(element, formId, 4);

}
function hideErrorDiv(){
var element = document.getElementById("elementValue").value;
var formId = document.getElementById("formIdValue").value;
var myAjax = new Ajax.Request(
					'flushFDBResponse.ajaxfdb', 
    			{
    			 method: 'get', 
    			 parameters: '&element='+element, 
         		 onFailure: function() {hideProcessing();location.href=document.URL;},
				     		  onComplete: function(response) {
		
								}
   				 });

document.getElementById('drugDrugCause').value = "false";
document.getElementById('drugFoodCause').value = "false";
document.getElementById('duplicateCause').value = "false";
$('errorDiv').style.display="none";
}

function getBack(){
$('errorDiv').style.display="none";
}


/**
 * This is a general function used to enable or disable an element.
 * @author Valsaraj
 * added on 07/08/09
 */
function enableDisableElement(elemId, enFlag) {
	$(elemId).disabled = ! enFlag;
	
//alert(elemId+""+enFlag);
}

/**
 * Append errors to error message table.
 * @author Valsaraj
 * added on 07/21/09
 */
function addRowToErrorMsgTable(tbl, left, clientId, errorCode) {
  	var lastRow = tbl.rows.length;
  	var iteration = lastRow;
  
  	if ($('errorCode' + errorCode) == null) {
		var row = tbl.insertRow(lastRow);
		
		// left cell
		var cellLeft = row.insertCell(0);
		cellLeft.setAttribute('id', 'errorCode' + errorCode);
		var textNodeLeft = document.createTextNode(left);
		cellLeft.appendChild(textNodeLeft);
		
		// right cell
		var cellRight = row.insertCell(1);
		var textNodeRight = document.createTextNode("The problem");
		var a = document.createElement("a");
		a.href = "javascript:goto('" + clientId + "');";
		a.appendChild(textNodeRight); 
		cellRight.appendChild(a);
	}
}

/**
 * Returns the first element with specified class name.
 * @author Valsaraj
 * added on 07/21/09
 */
function getElementByClassName(className, tagName) {
	var elems = document.getElementsByTagName(tagName);
	var howMany = elems.length;
	var a = ""
	
	for (var i=0; i < howMany; i++) {
		var thisElem = elems[i];
		var styleClassName = thisElem.className;
		a += styleClassName + "|";

		if (styleClassName==className) {
			return thisElem;
		}
	}
}


/**
 * Creates and displays error message table.
 * @author Valsaraj
 * added on 07/21/09
 */
function diplayErrors( root, errors, displayStepNo) {
 	var lastStep = $(root + 'step' + displayStepNo);
 	var errorBody = "";
 
   	for (var e = 0; e < errors.length; e += 1) {
		var error = errors[e];
    	errorBody += '<tr><td id=\'errorCode' + error.errorCode + '\'>' + error.summary + '</td><td><a href=\'javascript:goto(\"' + error.clientId + '\")\'>The problem</a></td></tr> \n';
   	}

   	if (errorBody != "") {
 		lastStep.innerHTML = makeErrorTable(errorBody);
 	}
}

function saveCodeReq(obj , formId , element){
var drug = escape(obj.parentNode.parentNode.childNodes[3].childNodes[0].innerHTML);
var code = obj.innerHTML;
$(formId + ":medicineSelected").innerHTML = obj.parentNode.parentNode.childNodes[3].childNodes[0].innerHTML;
addMedicinetoTrim(drug , code);
closePopUp();
}

function saveDrugReq(obj , formId , element){
var drug = escape(obj.innerHTML);
var code = obj.parentNode.parentNode.childNodes[1].childNodes[0].innerHTML;
$(formId + ":medicineSelected").innerHTML = obj.innerHTML;
addMedicinetoTrim(drug , code);
closePopUp();
}

function startFilteringReq(formId , element){
	var filterD = escape($(formId + ":filterText").value);
		
		 document.getElementById("ajaxLoader").style.display="block";	
var myAjax = new Ajax.Request(
					'drugsUpdate.ajaxfdb', 
    			{
    			 method: 'get', 
    			 parameters: '&filter_condition='+filterD, 
    			 onProcessing:function(){showProcessing()},
         		 onFailure: function() {hideProcessing();location.href=document.URL;},
				     		  onComplete: function(response) {
         			document.getElementById("ajaxLoader").style.display="none";	
				     		  		var drugs= response.responseText.split('|');
				     		  		var totalCount = drugs.length - 1;
				     		  		var table = $(formId + ":drugTable");
				     		 		var rowCount = table.rows.length;
									var j = rowCount -1 ;
									var flag;
												 for(var i=j; i>=0; i--) {
									        		$(formId + ":drugTable").deleteRow(i);
									        		j--;
												 }
									flag1 = 0;
									 for(var k=0; k<totalCount; k++){
									  var drug = drugs[k].split('$');
									 	var row = $(formId + ":drugTable").insertRow(k);
									 	row1 = $(formId + ":drugTable").rows[k];
									 	var cell1 = row1.insertCell(0);
									 	var cell2 = row1.insertCell(1);
									 	var cell3 = row1.insertCell(2);
									 	var text = document.createTextNode(drug[0]);
									 	var text1 = document.createTextNode(drug[1]);
									 	var text2 = document.createTextNode(drug[2]);
									 	cell1.appendChild(text);
									 	cell2.appendChild(text1);
									 	cell3.appendChild(text2);
									 	$(formId + ":drugTable").rows[k].cells[0].id="code"+k;
									 	$(formId + ":drugTable").rows[k].cells[1].id="med"+k;
									 	$(formId + ":drugTable").rows[k].id="drugFromFDB"+k;
										var drugName = document.getElementById("drugFromFDB"+k).childNodes[1].innerHTML;
									 	
									 	
									 	document.getElementById("code"+k).onclick=handleClickReq;
									 	document.getElementById("med"+k).onclick=handleMedClickReq;
							 
									 	
									 	
											if(flag1 == 0){
											  $(formId + ":drugTable").rows[k].className="odd";
											   flag1 =1;
											}else if(flag1 ==1){
												$(formId + ":drugTable").rows[k].className="even";
											   flag1 =0;
											}
											
											$(formId + ":drugTable").rows[k].style.cursor="pointer";
											$(formId + ":drugTable").rows[k].cells[0].style.textDecoration="underline";
											$(formId + ":drugTable").rows[k].cells[1].style.textDecoration="underline";
									}
									if($(formId + ":filterText").value != ""){
									$(formId + ":filteredcount").innerHTML=" / "+totalCount+" filtered items";
									}else{
									$(formId + ":filteredcount").innerHTML="";
									}
									if(totalCount < 14){
				     		 			document.getElementById("startLimit").innerHTML = "1";
										document.getElementById("endLimit").innerHTML = totalCount;	
				     		 		}else if(totalCount == 0){
				     		 			document.getElementById("startLimit").innerHTML = "0";
										document.getElementById("endLimit").innerHTML = "0";	
				     		 		}else{
				     		 			document.getElementById("startLimit").innerHTML ="1";
										document.getElementById("endLimit").innerHTML = "14";
				     		 		}
											
										hideProcessing();
								}
   				 });
 		//$(formId + ":filterButton").click();   
	
	
}






function handleClickReq(obj){
	var formId = document.getElementById("formIdValue").value;
	var element = document.getElementById("elementValue").value;
	var drug = escape(obj.target.parentNode.childNodes[1].innerHTML);
	var code = obj.target.innerHTML;
	$(formId + ":medicineSelected").innerHTML = obj.target.parentNode.childNodes[1].innerHTML;
	addMedicinetoTrim(drug , code);
	closePopUp();
}

function handleMedClickReq(obj){
	var formId = document.getElementById("formIdValue").value;
	var element = document.getElementById("elementValue").value;
	var code = obj.target.parentNode.childNodes[0].innerHTML;
	var drug = escape(obj.target.innerHTML);
	$(formId + ":medicineSelected").innerHTML = obj.target.innerHTML;
	addMedicinetoTrim(drug , code);
	closePopUp();
}

function alertMessageReq(){
var foodComments =$('drilldown:drugFoodCause').value;
var duplicateComments=$('drilldown:duplicateCause').value;

	if($('drilldown:hasDrugFoodInterraction').value == "true" 
	|| $('drilldown:hasDuplicateTherapy').value == "true"){
	$('errorDiv').style.left="100px";
	$('errorDiv').style.top="300px";
	$('errorDiv').style.width="800px";
	
	var heightDiv = 100;
	var errordata = 	"<table width='100%'><tr><td colspan='2'><br/><b>Some Issues with the Drugs Prescribed ! </b></td></tr>"
	if($('drilldown:hasDrugFoodInterraction').value == "true"){
	errordata = errordata + "<tr><td colspan='2'><b>. Drug - Food Interraction </b></td></tr><tr><td colspan='2'>"+foodComments+"</td></tr>"
	heightDiv = heightDiv + 50;
	}
	if($('drilldown:hasDuplicateTherapy').value == "true"){
	errordata = errordata + "<tr><td colspan='2'> <b>. Duplicate Therapy </b></td></tr><tr><td colspan='2'>"+duplicateComments+"</td></tr>"
	heightDiv = heightDiv + 50;
	}
	
	errordata = errordata + "<tr><td ><input type='button' value='Go Back' onclick='getBackReq()'/></td><td style='text-align: right'><input type='button' value='Override'  onclick='overRideReq()'/></td></tr> </table> "
	
	$('errorDiv').style.height=heightDiv+"px";
    $('errorDiv').innerHTML = errordata;
	$('errorDiv').style.display="block";
	$('errorDiv').style.overflow="auto";
	}
}

function getBackReq(){
var formId = $("formIdValue").value;
var element = document.getElementById("elementValue").value;

document.getElementById('drugFoodCause').value = "false";
document.getElementById('duplicateCause').value = "false";
refreshWizard(element, formId, 2);
$('errorDiv').style.display="none";
}


function overRideReq(){
$('errorDiv').style.display="none";
$('errorDiv').style.top="200px";
$('errorDiv').style.left="200px";
$('errorDiv').style.width="460px";
$('errorDiv').style.height="100px";
data = "<table><tr><td>Override Comments</td><td><input type='text'  style='height:50px;width:300px;'/></td></tr><tr><td colspan='2' style='text-align:right'><input type='button' value='Submit'  onclick='hideErrorReq(this)' /></td></tr></table>";
$('errorDiv').innerHTML=data;
$('errorDiv').style.display="block";
$('errorDiv').style.overflow="auto";
}

function hideErrorReq(obj){
var comments = obj.parentNode.parentNode.parentNode.childNodes[0].childNodes[1].childNodes[0].value;

var element = $("elementValue").value;
var myAjax = new Ajax.Request(
					'overRideCommentsReq.ajaxerx', 
    			{
    			 method: 'get', 
    			 parameters: '&element='+element+'&comments='+comments, 
         		 onFailure: function() {hideProcessing();location.href=document.URL;},
				     		  onComplete: function(response) {
		
								}
   				 });
document.getElementById('hasDrugFoodInterraction').value = "false" ;
document.getElementById('hasDuplicateTherapy').value = "false";
$('errorDiv').style.display="none";
refreshWizard(element, formId, 4);

}

function addMedicinetoTrim(obj , code){
var medicine = obj;
var drugcode = code;
var element = document.getElementById("elementValue").value;
var fullResponse;
var myAjax = new Ajax.Request(
					'addMedicineToTrim.ajaxfdb',
				{
				method: 'get',
				parameters: '&element='+element+'&medicine='+medicine+'&code='+drugcode,
				onFailure: function() {hideProcessing();location.href=document.URL;},
				onComplete: function(response) {
					fullResponse = response.responseText;
					alert(fullResponse);
				}
				});

}


/*	Function to fetch the SureScript files 
 *	which were downloaded on a webservice call
 *	and place it in to a specified 
 *	location(C:\tolven-config\SureScriptsFile)
 *  Added on 03/11/09
*/
function updateDirectory(element){

	var myAjax = new Ajax.Request(
				'updateDirectory.ajaxsure',
				{
					method:'get',
					parameters:'&element='+element,
					onFailure:function(){location.href=document.URL;},
					onComplete:function(response){}
					
				}
	);
}
  
  
function enableAllergiesPopUP(formId, popup) {
	$(formId+":rowId").value="0";
	$(formId+":"+popup).style.display="block";
	$(formId+":startLimit").innerHTML="1";
	$(formId+":endLimit").innerHTML="14";
}

function displayAllergiesPopUP()	{
	document.getElementById("drugAllergiesFromFDBPopUP").style.display="block";
}

function closeAllergiesPopUp(formId, popup){
	if(document.getElementById("allergySelected") != null){
		document.getElementById("allergySelected").style.display="block";
	}else if($(formId + ":allergySelected") != null){
		$(formId+":allergySelected").style.display="block";
	}
	/*if(document.getElementById(popup) != null){
		document.getElementById(popup).style.display="none";
	}else if($(formId+":"+popup) != null) {*/
		$(formId+":"+popup).style.display="none";
	//}
}

function selectDrugAllergy(allergen,type,code,formId){
	var rootForm = $(formId);
	$(formId + ":fdbDrugAllergyName").value = allergen;
	$(formId + ":fdbDrugAllergyType").value = type;
	$(formId + ":fdbDrugAllergyCode").value = code;
	$(formId + ":computeEnable").value = "true";
	$(formId + ":computeAction").value = 'selectDrugAllergy';
	var wipNode = rootForm.parentNode;
	var currentStep = 1 * rootForm.getAttribute('currentStep');
	ajaxSubmit4(rootForm, wipNode.id,currentStep);
	$(formId + ":computeEnable").value = "false";
   	//$(formId + ":allergySelected").value = allergen;
   	closeAllergiesPopUp(formId, 'drugAllergiesFromFDBPopUP');
}

function addDrugAllergyDetails(formId, allergen){
	var rootForm = $(formId);
	$(formId + ":fdbDrugAllergyName").value = allergen;
	$(formId + ":computeEnable").value = "true";
	$(formId + ":computeAction").value = 'addDrugAllergyDetails';
	var wipNode = rootForm.parentNode;
	var currentStep = 1 * rootForm.getAttribute('currentStep');
	ajaxSubmit4(rootForm, wipNode.id,currentStep);
	$(formId + ":computeEnable").value = "false";

}

function removeFdbDrugAllergy(formId,index){
	var rootForm = $(formId);
	$(formId + ":computeEnable").value = "true";
	$(formId + ":computePosition").value = index;
	$(formId + ":computeAction").value = 'remove';
	var wipNode = rootForm.parentNode;
	var currentStep = 1 * rootForm.getAttribute('currentStep');
	ajaxSubmit4(rootForm, wipNode.id,currentStep);
	$(formId + ":computeEnable").value = "false";	
}

//method to confirm deleting unsaved medications in medications document wizard
function confirmDeleteUnsavedDrugAllergies(element,formId,nextStepNum){
	if (confirm("Unsaved data on the form will be deleted. Do you want to continue?")) {
		deleteUnsavedDrugAllergies(element,formId,nextStepNum);
		return false;
	}
}

//method to delete unsaved medications in medications document wizard
//this triggers RemoveDisabledMedication.java
function deleteUnsavedDrugAllergies(element,formId,nextStepNum){
	 var rootForm = $(formId);
	 stopAsync(formId);
	 $(formId + ":computeEnable").value = "true";
	 $(formId + ":computeAction").value = 'removeUnsaved';
	 var currentStep = 1 * rootForm.getAttribute('currentStep');
	 var wipNode = rootForm.parentNode;
	 ajaxSubmit4(rootForm, wipNode.id, currentStep);
}


function showDrugAllergyDetailInfo(div, obj){
	$(div).innerHTML="<div style='width: 100%;height: auto; text-align: center;'>"+obj.message+(obj.error?("<span style='color: blue; cursor: pointer;padding-left: 5px;' onclick='Effect.toggle(\"errorTextarea\",\"Appear\",{duration: 0.2});'>Details</span><br /><textarea id='errorTextarea' style='display: none; width: 90%; height: 100px;'>"+obj.error+"</textarea>"):"")+"</div>";
	if (obj.success) {
		fade(div);
	} else {
		$(div).show();
	}
}



function handleClickAllergy(obj){
var formId = document.getElementById("formIdValue").value;
var element = document.getElementById("elementValue").value;
var allergy = escape(obj.target.parentNode.childNodes[0].innerHTML);
var myAjax = new Ajax.Request(
					'addAllergy.ajaxfdb', 
    			{
    			 method: 'get', 
    			 parameters: '&allergy_name='+allergy+'&element='+element, 
         		 onFailure: function() {
         		 location.href=document.URL;
         		 },
	     		  onComplete: function(response) {
	    		  }
   				 });
   	$(formId + ":allergySelected").value = obj.target.parentNode.childNodes[0].innerHTML;

closeAllergiesPopUp(formId, 'drugAllergiesFromFDBPopUP');
}

function handleClickAllergy1(obj){
	var formId = document.getElementById("formIdValue").value;
	var element = document.getElementById("elementValue").value;
	var allergy = escape(obj.target.parentNode.childNodes[0].innerHTML);
	var myAjax = new Ajax.Request(
						'addAllergy.ajaxfdb', 
	    			{
	    			 method: 'get', 
	    			 parameters: '&allergy_name='+allergy+'&element='+element, 
	         		 onFailure: function() {
	         		 location.href=document.URL;
	         		 },
		     		  onComplete: function(response) {
		    		  }
	   				 });
	   	$(formId + ":allergySelected").value = obj.target.parentNode.childNodes[0].innerHTML;

	closeAllergiesPopUp(formId, 'drugAllergiesFromFDBPopUP');
}


function retrieveInformation(obj , formId , element){
	
	var drugname = escape(obj.value.split("|")[5]);
	var myAjax = new Ajax.Request(
						'gatherRefillQuantity.ajaxerx', 
	    			{
	    			 method: 'get', 
	    			 parameters: '&drug_name='+drugname+'&element='+element, 
	         		 onFailure: function() {
	         		 location.href=document.URL;
	         		 },
		     		  onComplete: function(response) {
		     		  		$(formId + ":finalMedication").value = obj.value.split("|")[5];
		     		  		var responseQuantity  = response.responseText.split('|');
		     		  		if(responseQuantity[0] == "" && responseQuantity[1] == ""){
		     		  			$(formId + ":dispenseAmountRequested").value = 0;
		     		  			$(formId + ":refillRequested").value = 0;
		     		  			$(formId + ":refillQ").value = 0;
		     		  		}else{
			     		  		$(formId + ":dispenseAmountRequested").value = responseQuantity[0];
		   						$(formId + ":refillRequested").value = responseQuantity[1];
		   						$(formId + ":refillQ").value = responseQuantity[2];
	   						}
		    		  }
	   				 });
	   		 
	   				   				 
}

function openNewPrescription(){
	if ($("drilldown:response").innerHTML == "Denied New Prescription") {
		// && ($("isScheduleDrug") != null && $("isScheduleDrug").value == "No")) {
var element = document.getElementById('elementValue').value;
					 var myAjax = new Ajax.Request(
										'gatherPatientInformation.ajaxerx', 
					    			{
					    			 method: 'get', 
					    			 parameters: '&element='+element, 
					         		 onFailure: function() {
					         		 location.href=document.URL;
					         		 },
						     		  onComplete: function(response) {
							     		  if(null != response.responseText && response.responseText != ""){
								     		  		 var patientId = response.responseText.split('|')[1];
						                       		 var context = patientId+":medications:active";
						                       		 var templateId = "currentMedicationMenu-"+patientId;
						                       		 var rxReferenceNumber = response.responseText.split('|')[2];
						                       		 var pharmacyName = response.responseText.split('|')[3];
						                       		 var pharmacyncpdpid = response.responseText.split('|')[4];
						                       		 var medicineSelected = response.responseText.split('|')[5];
						                       		 var pharmacyAddressLine1 = response.responseText.split('|')[6];
						                       		 var pharmacyCity = response.responseText.split('|')[7];
						                       		 var pharmacyState = response.responseText.split('|')[8];
						                       		 var pharmacyZip = response.responseText.split('|')[9];
						                       		 var pharmacyTE = response.responseText.split('|')[10];
						                       		 var pharmacyFX = response.responseText.split('|')[11];
						                       		instantiatePrescriptionTrim("obs/evn/patientPrescription",context, null,rxReferenceNumber ,
						                       				pharmacyName, pharmacyncpdpid , medicineSelected, pharmacyAddressLine1, pharmacyCity
						                       				, pharmacyState, pharmacyZip, pharmacyTE, pharmacyFX);
						                  }
						    		  }
					   				 });
}
}

function instantiatePrescriptionTrim( templateId, context, source ,rxReferenceNumber , pharmacyName , pharmacyncpdpid , medicine, pharmacyAddressLine1, pharmacyCity
			, pharmacyState, pharmacyZip, pharmacyTE, pharmacyFX) {
	var sourceParam;
	if (source == undefined) {
		sourceParam = "";
	} else {
		sourceParam = '&source='+source;
	}
	var param = 'templateId='+templateId+'&context='+context+sourceParam+'&rxReferenceNumber='+rxReferenceNumber+'&pharmName='+pharmacyName+'&ncpdpid='+pharmacyncpdpid+
	'&med='+escape(medicine)+'&pharmAdd='+pharmacyAddressLine1+'&city='+pharmacyCity+'&state='+pharmacyState+'&zip='+pharmacyZip+'&tel='+pharmacyTE+'&fax='+pharmacyFX;
	var instAjax = new Ajax.Request(
		'instantiatePrescriptionTrim.ajaxerx',
		{
			method: 'get',
			parameters: param,
			onSuccess: showNewInstance,
			onFailure: function(request) {displayError(request,param);}
		});
}
function submitPrescription(element){
					 var myAjax = new Ajax.Request(
										'submitPrescription.ajaxerx', 
					    			{
					    			 method: 'get', 
					    			 parameters: '&element='+element, 
					         		 onFailure: function() {
					         		 location.href=document.URL;
					         		 },
						     		  onComplete: function(response) {}
					   				 });
}

enableCompute=function(formId){
	$(formId + ":computeEnable").value = "true";
}
function addNonDrugAllergyTemplate(templateName, element, methodArgs)
{
  var lArgs = splitArguments(methodArgs);
  var formId = lArgs[0];
  var rootForm = $(formId);

  $(formId + ":computeEnable").value = "true";
  $(formId + ":computeTemplate").value = templateName;
  $(formId + ":computePosition").value = lArgs[1];
  $(formId + ":computeNewRelation").value = 'addNonDrugAllergy';

  closeDiv(element);

  var wipNode = rootForm.parentNode;
  ajaxSubmit4(rootForm, wipNode.id, eval(lArgs[1]) + 1);
}

function getBackToPreviousPage(){
	var formId = document.getElementById("formIdValue").value;
	var element = document.getElementById("elementValue").value;
	refreshWizard(element, formId, 2);
	$('errorDiv').style.display="none";
}
function overRideRefillQuantity(){
	$('errorDiv').style.display="none";
	refreshWizard(element, formId, 4);
}

function changeRefillInPrescription( element, action ) {
  closeEventBubble();
  var myAjax = new Ajax.Request(
   'changeRefillInPrescription.ajaxerx',
   {
    method: 'get',
    parameters: 'element='+element+'&action='+action,
    onFailure: function(request) {displayError(request,element);},
    onSuccess: wizTransitionDone
  });
}
function loadMedicationDetails(element, formId){
	var instAjax = new Ajax.Request(
			'loadMedicationDetails.ajaxerx',
			{
				method: 'get',
				parameters: '&element='+element,
				onSuccess: function(request){
				
				refreshPage(element,formId,2);
				},
				onFailure: function(request) {displayError(request,param);}
			});
}
function setRequestedMedicineValueSet(element, formId){
	var instAjax = new Ajax.Request(
			'setRequestedMedicineValueSet.ajaxerx',
			{
				method: 'get',
				parameters: '&element='+element,
				onSuccess: function(request){
				
				refreshPage(element,formId,2);},
				onFailure: function(request) {displayError(request,param);}
			});
}
function wizGeneratePatientPrescription(element){
	closePopUp1();
	var hrefPath="/Tolven/private/patientPrescriptionReport.jsf?msgid="+messageId+"&RenderOutputType=pdf";
	window.open(hrefPath, '_blank');
}
var rootForControlled;
var podForControlled;
var elementGlobal;
function wizGeneratePatientPrescriptionControlled(root, element){
	rootForControlled = root;
	elementGlobal = element;
	podForControlled = $("pod").value;
	setTimeout("generateControlledReport();",5000);
}
function generateControlledReport(){
	var hrefPath="/Tolven/private/patientPrescriptionReportForControlledDrug.jsf?pod="+podForControlled+"&root="+rootForControlled+"&element="+elementGlobal+"&RenderOutputType=pdf";
	window.open(hrefPath, '_blank');
}
function wizGenerateErxReport(element){
	var herfPath="/Tolven/private/newRxReport.jsf?element="+element+"&RenderOutputType=pdf";
	window.open(herfPath, '_blank');
}
function wizGenerateControlledSubReport(element){
	var herfPath="/Tolven/private/patientPrescriptionReportForControlledDrug.jsf?element="+element+"&RenderOutputType=pdf";
	window.open(herfPath, '_blank');
}

/**
 * Saves the drug from the first page onwards when clicked on drug code. 
 * @param obj
 * @param formId
 * @param element
 * @return
 */
function saveCode(obj , formId , element) {
	var code = '';
	var drug = '';
	if (obj.parentNode.parentNode.childNodes[3] != null && obj.parentNode.parentNode.childNodes[3].innerHTML.split('span').length>0) {
		code = obj.parentNode.parentNode.childNodes[1].childNodes[0].innerHTML;
		drug = obj.parentNode.parentNode.childNodes[3].childNodes[0].innerHTML;
	} else {
		code = obj.parentNode.parentNode.childNodes[0].childNodes[0].innerHTML;
	    drug = obj.parentNode.parentNode.childNodes[1].childNodes[0].innerHTML;
	}
	if ($(formId+":isFormularyElement") == null) {
		$(formId+":drugQualifier").value="EA";
	    $(formId+":drugQualDescription").value="Each";
		$(formId + ":hiddenDrugName").value = drug;
		var priorMedicines = escape($(formId + ":priorMedicines").value);
		var priorAllergies = escape($(formId + ":priorAllergies").value);
		$("#ajaxLoader").style.display="block";
		var myAjax = new Ajax.Request(
		'fdbCollect.ajaxfdb',{
		    			 method: 'get', 
		    			 parameters: '&drug_name='+drug+'&drug_code='+code+'&element='+element+'&priorMed='+priorMedicines+'&priorAlleg='+priorAllergies, 
		         		 onFailure: function() {
		         		 location.href=document.URL;
	 		 $("ajaxLoader").style.display="none";
		         		 },
			     		  onComplete: function(response) {
			     		 	var fullResponse = response.responseText;
			     		 	var responseFDB = fullResponse.split('|');
 		 	if (responseFDB.length>=18 && responseFDB[17]!=null && $(formId+":strengthUnits"))
 		 		$(formId+":strengthUnits").value=responseFDB[17];
 		  	$(formId+":hasDrugDrugInterraction").value=responseFDB[0];
 		  	$(formId+":hasDrugFoodInterraction").value=responseFDB[1];
 		  	$(formId+":hasDuplicateTherapy").value=responseFDB[2];
 		  	$(formId+":hasDrugAllergy").value=responseFDB[3];
 		  	$(formId+":strength").value=responseFDB[4];
 		  	$(formId+":strength").disabled=true;
 		  	$(formId+":minmax").value=responseFDB[5];
			     		  	if(responseFDB[6] != ""){
 		  		$(formId+":sigcodes").value=responseFDB[6]+",";
			     		  	}else{
 		  		$(formId+":sigcodes").value="";
			     		  	}
 		  	$("maxDose").value=responseFDB[7];
			     		    $(formId + ":classficationFromFdb").value=responseFDB[8];
			     		    $(formId + ":ctactiongroupClassficationFromFdb").value=responseFDB[15];
			     		    $(formId + ":ctetcClassficationFromFdb").value=responseFDB[13];
			     		    $(formId + ":ctfdbClassficationFromFdb").value=responseFDB[14];
			     		    if (responseFDB[18] != null && responseFDB[18] == 'Yes') {
			     		    	$(formId + ":isdrugFormulary").value = responseFDB[18];
				     		    $(formId + ":isdrugFormulary").style.color = "green";
							} else if (responseFDB[18] != null) {
								$(formId + ":isdrugFormulary").value = responseFDB[18];
				     		    $(formId + ":isdrugFormulary").style.color = "red";
							}
			     		    
			     			$('errorDiv').style.left="100px";
			     			$('errorDiv').style.top="300px";
			     			$('errorDiv').style.width="800px";
			     			var flag = "false";
			     			var heightDiv = 150;
			     			var errordata = 	"<table width='100%'><tr><td colspan='2' style='text-align:center'><br/><b>Information on the Drug Prescribed ! </b></td></tr>";
			     			if(null != responseFDB[10] && responseFDB[10] != ""){
			     			errordata = errordata + "<tr><td colspan='2'><b>. Prescriber Messages </b></td></tr><tr><td colspan='2'>"+responseFDB[9]+"</td></tr>";
			     			heightDiv = heightDiv + 50;
			     			flag="true";
			     			}
			     			if(null != responseFDB[10] && responseFDB[10] != ""){
			     				errordata = errordata + "<tr><td colspan='2'><b>. Patient Messages </b></td></tr><tr><td colspan='2'>"+responseFDB[10]+"</td></tr>";
				     			heightDiv = heightDiv + 50;
			     				flag="true";
			     		    }
			     		    if(null != responseFDB[11] && responseFDB[11] != ""){
			     		    	errordata = errordata + "<tr><td colspan='2'><b>. Common Order </b></td></tr><tr><td colspan='2'>"+responseFDB[11]+"</td></tr>";
				     			heightDiv = heightDiv + 25;
			     		    	flag="true";
			     		    }
			     		    if(null != responseFDB[12] && responseFDB[12] != ""){
			     		    	errordata = errordata + "<tr><td colspan='2'><b>. POEM </b></td></tr><tr><td colspan='2'>"+responseFDB[12]+"</td></tr>";
				     			heightDiv = heightDiv + 25;
			     		    	flag="true";
			     		    }
			     			
			     		    if(responseFDB[16] == "true"){
			     		    	errordata = errordata + "<tr><td colspan='2'><b>. Controlled Substance </b></td></tr><tr><td colspan='2'> The Prescription cannot be routed to Surescripts. Please take hard copy of the prescription or dispense from office.</td></tr>";
			     		    	flag="true";
			     		    }
			     		    if(flag == "true"){
			     		    	errordata = errordata + "<tr><td colspan='2' style='text-align:center'><input type='button' value='Close' onclick='closeErrorAlert()'/></td></tr> </table> ";
			     		    	$('errorDiv').style.height=heightDiv+"px";
			     		    	$('errorDiv').innerHTML = errordata;
	//		     		    	$('errorDiv').style.background="#d8e8ff"; // Different color
			     		    	$('errorDiv').style.display="block";
			     		    }
			     		   var ndcAjax = new Ajax.Request(
			     					'retrieveNDCInformation.ajaxfdb',
			     					{
			     						method: 'get',
			     						parameters: '&drug_name='+drug+'&drug_code='+code+'&element='+element,
			     						onSuccess: function(response){
				$("ajaxLoader").style.display="none";
			     						},
			     						onFailure: function(response) {
			     							displayError(request,param);
				$("ajaxLoader").style.display="none";	}
			     					});
			     		   
			    		  }
		   				 });
		   	$(formId + ":medicineSelected").value = obj.parentNode.parentNode.childNodes[3].childNodes[0].innerHTML;
	} else if($(formId+":isFormularyElement") != null) {
		new Ajax.Request(
				'drugFormulary.ajaxfdb',
				{
					method: 'get',
					parameters: '&drug_name='+drug+'&drug_code='+code+'&element='+element,
					onSuccess: function(response){
						var fullResponse = response.responseText;
		     		 	var responseFDB = fullResponse.split('|');
		     		 	if (responseFDB[0] != 'null' && responseFDB[0] != '') {
			     		 	$(formId+':medicineSelected').value = responseFDB[0];
			     		 	$(formId+':medicationPopup').value = "Edit Drug";
		     		 	}
		     		 	if (responseFDB[1] != 'null' && responseFDB[1] != '') {
		     		 		$(formId+':ndc').value = responseFDB[1];
		     		 	} else {
		     		 		$(formId+':ndc').value = "";
						}
		     		 	if (responseFDB[2] != 'null' && responseFDB[2] != '') {
		     		 		$(formId+':strength').value = responseFDB[2];
						} else {
							$(formId+':strength').value = "";
						}
     		 	$("ajaxLoader").style.display="none";
					},
					onFailure: function(response) {
						displayError(request,param);
			$("ajaxLoader").style.display="none";	}
				});
}
	closePopUp();
	document.getElementById("ajaxLoader").style.display="none";
}

/**
 * Saves the drug from first page when clicked on drug. 
 * @param obj
 * @param formId
 * @param element
 * @param isFormulary
 * @return
 */
function saveDrug(code,drug , formId , element) {
	if ($(formId+":isFormularyElement") == null) {
		$(formId+":drugQualifier").value="EA";
		$(formId+":drugQualDescription").value="Each";
		var priorMedicines = escape($(formId + ":priorMedicines").value);
		var priorAllergies = escape($(formId + ":priorAllergies").value);
		$(formId+":ajaxLoader").style.display="block";
		$(formId).setAttribute("ajaxSubmit3InProgress", 'true');

		var myAjax = new Ajax.Request(
							'fdbCollect.ajaxfdb', 
		    			{
		    			 method: 'get', 
		 parameters: '&drug_code='+code+'&element='+element+'&priorMed='+priorMedicines+'&priorAlleg='+priorAllergies, 
		         		 onFailure: function() {
		         		 location.href=document.URL;
		 $("ajaxLoader").style.display="none";
		         		 },
			     		  onComplete: function(response) {
			$(formId).removeAttribute('ajaxSubmit3InProgress');
			     		  	var fullResponse = response.responseText;
			     		 	var responseFDB = fullResponse.split('|');
			if (responseFDB.length>=18 && responseFDB[17]!=null && $(formId+":strengthUnits"))
				$(formId+":strengthUnits").value=responseFDB[17];
			$(formId+":hasDrugDrugInterraction").value=responseFDB[0];
			$(formId+":hasDrugFoodInterraction").value=responseFDB[1];
			$(formId+":hasDuplicateTherapy").value=responseFDB[2];
			$(formId+":hasDrugAllergy").value=responseFDB[3];
			$(formId+":strength").value=responseFDB[4];
			$(formId+":strength").disabled=true;
			$(formId+":minmax").value=responseFDB[5];
			     		  	if(responseFDB[6] != ""){
				$(formId+":sigcodes").value=responseFDB[6]+",";
			     		  	}else{
				$(formId+":sigcodes").value="";
			     		  	}	
			$(formId+":maxDose").value=responseFDB[7];
			     		  	$(formId + ":classficationFromFdb").value=responseFDB[8];
			     		    $(formId + ":ctactiongroupClassficationFromFdb").value=responseFDB[15];
			     		    $(formId + ":ctetcClassficationFromFdb").value=responseFDB[13];
			     		    $(formId + ":ctfdbClassficationFromFdb").value=responseFDB[14];
			     		    if (responseFDB[18] != null && responseFDB[18] == 'Yes') {
			     		    	$(formId + ":isdrugFormulary").value = responseFDB[18];
				     		    $(formId + ":isdrugFormulary").style.color = "green";
							} else if (responseFDB[18] != null) {
								$(formId + ":isdrugFormulary").value = responseFDB[18];
				     		    $(formId + ":isdrugFormulary").style.color = "red";
							}
			     		    
			     		    $('errorDiv').style.left="100px";
			     			$('errorDiv').style.top="300px";
			     			$('errorDiv').style.width="800px";
			     			var flag = "false";
			     			var heightDiv = 150;
			     			var errordata = 	"<table width='100%'>" +
			     					"<tr><td colspan='2' style='text-align:center'><br/><b>Information on the Drug Prescribed ! </b></td></tr>";
			     			if(null != responseFDB[10] && responseFDB[10] != ""){
			     			errordata = errordata + "<tr><td colspan='2'><b>. Prescriber Messages </b></td></tr><tr><td colspan='2'>"+responseFDB[9]+"</td></tr>";
			     			heightDiv = heightDiv + 50;
			     			flag="true";
			     			}
			     			if(null != responseFDB[10] && responseFDB[10] != ""){
			     				errordata = errordata + "<tr><td colspan='2'><b>. Patient Messages </b></td></tr><tr><td colspan='2'>"+responseFDB[10]+"</td></tr>";
				     			heightDiv = heightDiv + 50;
			     				flag="true";
			     		    }
			     		    if(null != responseFDB[11] && responseFDB[11] != ""){
			     		    	errordata = errordata + "<tr><td colspan='2'><b>. Common Order </b></td></tr><tr><td colspan='2'>"+responseFDB[11]+"</td></tr>";
				     			heightDiv = heightDiv + 25;
			     		    	flag="true";
			     		    }
			     		    if(null != responseFDB[12] && responseFDB[12] != ""){
			     		    	errordata = errordata + "<tr><td colspan='2'><b>. POEM </b></td></tr><tr><td colspan='2'>"+responseFDB[12]+"</td></tr>";
				     			heightDiv = heightDiv + 25;
			     		    	flag="true";
			     		    }
			     			
			     		   if(responseFDB[16] == "true"){
			     		    	errordata = errordata + "<tr><td colspan='2'><b>. Controlled Substance </b></td></tr><tr><td colspan='2'> The Prescription cannot be routed to Surescripts. Please dispense it from office.</td></tr>";
			     		    	flag="true";
			     		    }
		     		   	    if(flag == "true"){
			     		    	errordata = errordata + "<tr><td colspan='2' style='text-align:center'><input type='button' value='Close' onclick='closeErrorAlert()'/></td></tr> </table> ";
			     		    	$('errorDiv').style.height=heightDiv+"px";
			     		    	$('errorDiv').innerHTML = errordata;
			     		    	$('errorDiv').style.display="block";
			     		    }	
			     		    
			     		   var ndcAjax = new Ajax.Request(
			     					'retrieveNDCInformation.ajaxfdb',
			     					{
			     						method: 'get',
			     						parameters: '&drug_name='+drug+'&drug_code='+code+'&element='+element,
			     						onSuccess: function(response){
				$(formId+":ajaxLoader").style.display="none";
			     						},
			     						onFailure: function(response) {
			     							displayError(request,param);
					$(formId+":ajaxLoader").style.display="none";	}
			     					});
			     		   
		   $(formId+":ajaxLoader").style.display="none";
			     		 }
		   				 });	
		 $(formId + ":medicineSelected").value = drug;
	} else if ($(formId+":isFormularyElement") != null) {
		new Ajax.Request(
					'drugFormulary.ajaxfdb',
					{
						method: 'get',
						parameters: '&drug_name='+drug+'&drug_code='+code+'&element='+element,
						onSuccess: function(response){
							var fullResponse = response.responseText;
			     		 	var responseFDB = fullResponse.split('|');
			     		 	if (responseFDB[0] != 'null' && responseFDB[0] != '') {
				     		 	$(formId+':medicineSelected').value = responseFDB[0];
				     		 	$(formId+':medicationPopup').value = "Edit Drug";
			     		 	}
			     		 	if (responseFDB[1] != 'null' && responseFDB[1] != '') {
			     		 		$(formId+':ndc').value = responseFDB[1];
			     		 	} else {
			     		 		$(formId+':ndc').value = "";
							}
			     		 	if (responseFDB[2] != 'null' && responseFDB[2] != '') {
			     		 		$(formId+':strength').value = responseFDB[2];
							} else {
								$(formId+':strength').value = "";
							}
				//$(formId+":ajaxLoader").style.display="none";
						},
						onFailure: function(response) {
							displayError(request,param);
				//$(formId+":ajaxLoader").style.display="none";	
			}
					});
	}
	closePopUp(formId,'medicationFromFDBPopUP');
}

/**
 * Saves the drug from the second page onwards when clicked on code.
 * @param obj
 * @return
 */
function handleClick(obj) {
	var formId = document.getElementById("formIdValue").value;
	var element = document.getElementById("elementValue").value;
	var code = obj.target.innerHTML;
	var drug = escape(obj.target.parentNode.childNodes[1].innerHTML);
	if ($(formId+":isFormularyElement") == null) {
		var formId = document.getElementById("formIdValue").value;
		$(formId+":drugQualifier").value="EA";
		$(formId+":drugQualDescription").value="Each";
		$(formId + ":hiddenDrugName").value = obj.target.parentNode.childNodes[1].innerHTML;
		var code = obj.target.innerHTML;
		var drug = escape(obj.target.parentNode.childNodes[1].innerHTML);
		var priorMedicines = escape($(formId + ":priorMedicines").value);
		var priorAllergies = escape($(formId + ":priorAllergies").value);
		document.getElementById("ajaxLoader").style.display="block";
		 var myAjax = new Ajax.Request(
							'fdbCollect.ajaxfdb', 
		    			{
		    			 method: 'get', 
		    			 parameters: '&drug_name='+drug+'&drug_code='+code+'&element='+element+'&priorMed='+priorMedicines+'&priorAlleg='+priorAllergies, 
		         		 onFailure: function() {
		         		 location.href=document.URL;
		         		document.getElementById("ajaxLoader").style.display="none";
		         		 },
			     		  onComplete: function(response) {
			     		  	var fullResponse = response.responseText;
			     		 	var responseFDB = fullResponse.split('|');
			     		 	if (responseFDB.length>=18 && responseFDB[17]!=null && document.getElementById(formId+":strengthUnits"))
			     		 		document.getElementById(formId+":strengthUnits").value=responseFDB[17];
			     		  	document.getElementById(formId+":hasDrugDrugInterraction").value=responseFDB[0];
			     		  	document.getElementById(formId+":hasDrugFoodInterraction").value=responseFDB[1];
			     		  	document.getElementById(formId+":hasDuplicateTherapy").value=responseFDB[2];
			     		  	document.getElementById(formId+":hasDrugAllergy").value=responseFDB[3];
		     		  		document.getElementById(formId+":strength").value=responseFDB[4];
		     		  		document.getElementById(formId+":strength").disabled=true;
			     		  	document.getElementById(formId+":minmax").value=responseFDB[5];
			     		  	if(responseFDB[6] != ""){
			     		  		document.getElementById(formId+":sigcodes").value=responseFDB[6]+",";
			     		  	}else{
			     		  		document.getElementById(formId+":sigcodes").value="";
			     		  	}
			     		  	document.getElementById("maxDose").value=responseFDB[7];
			     		  	$(formId + ":classficationFromFdb").value=responseFDB[8];
			     		    $(formId + ":ctactiongroupClassficationFromFdb").value=responseFDB[15];
			     		    $(formId + ":ctetcClassficationFromFdb").value=responseFDB[13];
			     		    $(formId + ":ctfdbClassficationFromFdb").value=responseFDB[14];
			     		    if (responseFDB[18] != null && responseFDB[18] == 'Yes') {
			     		    	$(formId + ":isdrugFormulary").value = responseFDB[18];
				     		    $(formId + ":isdrugFormulary").style.color = "green";
							} else if (responseFDB[18] != null) {
								$(formId + ":isdrugFormulary").value = responseFDB[18];
				     		    $(formId + ":isdrugFormulary").style.color = "red";
							}
			     		    
			     		    $('errorDiv').style.left="100px";
			     			$('errorDiv').style.top="300px";
			     			$('errorDiv').style.width="800px";
			     			var flag = "false";
			     			var heightDiv = 150;
			     			var errordata = 	"<table width='100%'><tr><td colspan='2' style='text-align:center'><br/><b>Information on the Drug Prescribed ! </b></td></tr>";
			     			if(null != responseFDB[10] && responseFDB[10] != ""){
			     			errordata = errordata + "<tr><td colspan='2'><b>. Prescriber Messages </b></td></tr><tr><td colspan='2'>"+responseFDB[9]+"</td></tr>";
			     			heightDiv = heightDiv + 50;
			     			flag="true";
			     			}
			     			if(null != responseFDB[10] && responseFDB[10] != ""){
			     				errordata = errordata + "<tr><td colspan='2'><b>. Patient Messages </b></td></tr><tr><td colspan='2'>"+responseFDB[10]+"</td></tr>";
				     			heightDiv = heightDiv + 50;
			     				flag="true";
			     		    }
			     		    if(null != responseFDB[11] && responseFDB[11] != ""){
			     		    	errordata = errordata + "<tr><td colspan='2'><b>. Common Order </b></td></tr><tr><td colspan='2'>"+responseFDB[11]+"</td></tr>";
				     			heightDiv = heightDiv + 25;
			     		    	flag="true";
			     		    }
			     		    if(null != responseFDB[12] && responseFDB[12] != ""){
			     		    	errordata = errordata + "<tr><td colspan='2'><b>. POEM </b></td></tr><tr><td colspan='2'>"+responseFDB[12]+"</td></tr>";
				     			heightDiv = heightDiv + 25;
			     		    	flag="true";
			     		    }
			     			
			     		   if(responseFDB[16] == "true"){
			     		    	errordata = errordata + "<tr><td colspan='2'><b>. Controlled Substance </b></td></tr><tr><td colspan='2'> The Prescription cannot be routed to Surescripts. Please dispense it from office.</td></tr>";
			     		    	flag="true";
			     		    }
			     		   
			     		    if(flag == "true"){
			     		    	errordata = errordata + "<tr><td colspan='2' style='text-align:center' ><input type='button' value='Close' onclick='closeErrorAlert()'/></td></tr> </table> ";
			     		    	$('errorDiv').style.height=heightDiv+"px";
			     		    	$('errorDiv').innerHTML = errordata;
//			     		    	$('errorDiv').style.background="#d8e8ff"; // Different color
			     		    	$('errorDiv').style.display="block";
			     		    }	
			     		   var ndcAjax = new Ajax.Request(
			     					'retrieveNDCInformation.ajaxfdb',
			     					{
			     						method: 'get',
			     						parameters: '&drug_name='+drug+'&drug_code='+code+'&element='+element,
			     						onSuccess: function(response){
			     						document.getElementById("ajaxLoader").style.display="none";
			     						},
			     						onFailure: function(response) {
			     							displayError(request,param);
			     						document.getElementById("ajaxLoader").style.display="none";	}
			     					});
			     		   
			    		  }
		   				 });
		   				 $(formId + ":medicineSelected").value = obj.target.parentNode.childNodes[1].innerHTML;
	} else if ($(formId+":isFormularyElement") != null) {
		new Ajax.Request(
				'drugFormulary.ajaxfdb',
				{
					method: 'get',
					parameters: '&drug_name='+drug+'&drug_code='+code+'&element='+element,
					onSuccess: function(response){
						var fullResponse = response.responseText;
		     		 	var responseFDB = fullResponse.split('|');
		     		 	if (responseFDB[0] != 'null' && responseFDB[0] != '') {
			     		 	$(formId+':medicineSelected').value = responseFDB[0];
			     		 	$(formId+':medicationPopup').value = "Edit Drug";
		     		 	}
		     		 	if (responseFDB[1] != 'null' && responseFDB[1] != '') {
		     		 		$(formId+':ndc').value = responseFDB[1];
		     		 	} else {
		     		 		$(formId+':ndc').value = "";
						}
		     		 	if (responseFDB[2] != 'null' && responseFDB[2] != '') {
		     		 		$(formId+':strength').value = responseFDB[2];
						} else {
							$(formId+':strength').value = "";
						}
		     		 	document.getElementById("ajaxLoader").style.display="none";
					},
					onFailure: function(response) {
						displayError(request,param);
					document.getElementById("ajaxLoader").style.display="none";	}
				});
}
	closePopUp();
}

/**
 *  
 * @param obj
 * @return
 */
function handleMedClick(obj){
	var element = document.getElementById("elementValue").value;
	var formId = document.getElementById("formIdValue").value;
	$(formId + ":hiddenDrugName").value = obj.target.innerHTML;
	var drug = escape(obj.target.innerHTML);
	var code = obj.target.parentNode.childNodes[0].innerHTML;
	if ($(formId+":isFormularyElement") == null) {
		
		if ($(formId+":drugQualifier")) {
			$(formId+":drugQualifier").value="EA";
		}
		if ($(formId+":drugQualDescription")) {
			$(formId+":drugQualDescription").value="Each";
		}
		var priorMedicines = '';
		var priorAllergies = '';
		if ($(formId + ":priorMedicines")) {
			priorMedicines = escape($(formId + ":priorMedicines").value);
		}
		if ($(formId + ":priorAllergies")) {
			priorAllergies = escape($(formId + ":priorAllergies").value);
		}
		document.getElementById("ajaxLoader").style.display="block";
		var myAjax = new Ajax.Request(
							'fdbCollect.ajaxfdb', 
		    			{
		    			 method: 'get', 
		    			 parameters: '&drug_name='+drug+'&drug_code='+code+'&element='+element+'&priorMed='+priorMedicines+'&priorAlleg='+priorAllergies, 
		         		 onFailure: function() {
		         		 location.href=document.URL;
		         		  document.getElementById("ajaxLoader").style.display="none";
		         		 },
			     		  onComplete: function(response) {
			     		 	var fullResponse = response.responseText;
			     		 	var responseFDB = fullResponse.split('|');
			     		 	if (responseFDB.length>=18 && responseFDB[17]!=null && document.getElementById(formId+":strengthUnits"))
			     		 		document.getElementById(formId+":strengthUnits").value=responseFDB[17];
			     		  	document.getElementById(formId+":hasDrugDrugInterraction").value=responseFDB[0];
			     		  	document.getElementById(formId+":hasDrugFoodInterraction").value=responseFDB[1];
			     		  	document.getElementById(formId+":hasDuplicateTherapy").value=responseFDB[2];
			     		  	document.getElementById(formId+":hasDrugAllergy").value=responseFDB[3];
		     		  		document.getElementById(formId+":strength").value=responseFDB[4];
		     		  		document.getElementById(formId+":strength").disabled=true;
			     		  	document.getElementById(formId+":minmax").value=responseFDB[5];
			     		  	if(responseFDB[6] != ""){
			     		  		document.getElementById(formId+":sigcodes").value=responseFDB[6]+",";
			     		  	}else{
			     		  		document.getElementById(formId+":sigcodes").value="";
			     		  	}
			     		  	document.getElementById("maxDose").value=responseFDB[7];
			     		  	$(formId + ":classficationFromFdb").value=responseFDB[8];
			     		    $(formId + ":ctactiongroupClassficationFromFdb").value=responseFDB[15];
			     		    $(formId + ":ctetcClassficationFromFdb").value=responseFDB[13];
			     		    $(formId + ":ctfdbClassficationFromFdb").value=responseFDB[14];
			     		    if (responseFDB[18] != null && responseFDB[18] == 'Yes') {
			     		    	$(formId + ":isdrugFormulary").value = responseFDB[18];
				     		    $(formId + ":isdrugFormulary").style.color = "green";
							} else if (responseFDB[18] != null) {
								$(formId + ":isdrugFormulary").value = responseFDB[18];
				     		    $(formId + ":isdrugFormulary").style.color = "red";
							}
			     		    
			     		    $('errorDiv').style.left="100px";
			     			$('errorDiv').style.top="300px";
			     			$('errorDiv').style.width="800px";
			     			var flag = "false";
			     			var heightDiv = 150;
			     			var errordata = 	"<table width='100%'><tr><td colspan='2' style='text-align:center'><br/><b>Information on the Drug Prescribed ! </b></td></tr>";
			     			if(null != responseFDB[10] && responseFDB[10] != ""){
			     			errordata = errordata + "<tr><td colspan='2'><b>. Prescriber Messages </b></td></tr><tr><td colspan='2'>"+responseFDB[9]+"</td></tr>";
			     			heightDiv = heightDiv + 50;
			     			flag="true";
			     			}
			     			if(null != responseFDB[10] && responseFDB[10] != ""){
			     				errordata = errordata + "<tr><td colspan='2'><b>. Patient Messages </b></td></tr><tr><td colspan='2'>"+responseFDB[10]+"</td></tr>";
				     			heightDiv = heightDiv + 50;
			     				flag="true";
			     		    }
			     		    if(null != responseFDB[11] && responseFDB[11] != ""){
			     		    	errordata = errordata + "<tr><td colspan='2'><b>. Common Order </b></td></tr><tr><td colspan='2'>"+responseFDB[11]+"</td></tr>";
				     			heightDiv = heightDiv + 25;
			     		    	flag="true";
			     		    }
			     		    if(null != responseFDB[12] && responseFDB[12] != ""){
			     		    	errordata = errordata + "<tr><td colspan='2'><b>. POEM </b></td></tr><tr><td colspan='2'>"+responseFDB[12]+"</td></tr>";
				     			heightDiv = heightDiv + 25;
			     		    	flag="true";
			     		    }
			     			
			     		   if(responseFDB[16] == "true"){
			     		    	errordata = errordata + "<tr><td colspan='2'><b>. Controlled Substance </b></td></tr><tr><td colspan='2'> The Prescription cannot be routed to Surescripts. Please dispense it from office.</td></tr>";
			     		    	flag="true";
			     		    }
			     		   
		     		   	    if(flag == "true"){
			     		    	errordata = errordata + "<tr><td colspan='2' style='text-align:center'><input type='button' value='Close' onclick='closeErrorAlert()'/></td></tr> </table> ";
			     		    	$('errorDiv').style.height=heightDiv+"px";
			     		    	$('errorDiv').innerHTML = errordata;
//			     		    	$('errorDiv').style.background="#d8e8ff"; // Different color.
			     		    	$('errorDiv').style.display="block";
			     		    }	
			     		    
			     		   var ndcAjax = new Ajax.Request(
			     					'retrieveNDCInformation.ajaxfdb',
			     					{
			     						method: 'get',
			     						parameters: '&drug_name='+drug+'&drug_code='+code+'&element='+element,
			     						onSuccess: function(response){
			     						document.getElementById("ajaxLoader").style.display="none";
			     						},
			     						onFailure: function(response) {
			     							displayError(request,param);
			     						document.getElementById("ajaxLoader").style.display="none";	}
			     					});
			    		  }
		   				 });
						 if ($(formId + ":medicineSelected")) {
							 $(formId + ":medicineSelected").value = obj.target.innerHTML;
						}
	} else if ($(formId+":isFormularyElement") != null) {
		new Ajax.Request(
				'drugFormulary.ajaxfdb',
				{
					method: 'get',
					parameters: '&drug_name='+drug+'&drug_code='+code+'&element='+element,
					onSuccess: function(response){
						var fullResponse = response.responseText;
		     		 	var responseFDB = fullResponse.split('|');
		     		 	if (responseFDB[0] != 'null' && responseFDB[0] != '') {
			     		 	$(formId+':medicineSelected').value = responseFDB[0];
			     		 	$(formId+':medicationPopup').value = "Edit Drug";
		     		 	}
		     		 	if (responseFDB[1] != 'null' && responseFDB[1] != '') {
		     		 		$(formId+':ndc').value = responseFDB[1];
		     		 	} else {
		     		 		$(formId+':ndc').value = "";
						}
		     		 	if (responseFDB[2] != 'null' && responseFDB[2] != '') {
		     		 		$(formId+':strength').value = responseFDB[2];
						} else {
							$(formId+':strength').value = "";
						}
		     		 	document.getElementById("ajaxLoader").style.display="none";
					},
					onFailure: function(response) {
						displayError(request,param);
					document.getElementById("ajaxLoader").style.display="none";	}
				});
	}
	closePopUp();
	document.getElementById("ajaxLoader").style.display="none";
}
	
function closeErrorAlert(){
	$('errorDiv').style.display="none";
}
	
function closeErrorPrescAlert(){
	$('errorDiv1').style.display="none";
}
	
function searchDrugFilter(event , root){
	if(event.keyCode == 13){
			startFiltering(root , null);
	}
}

//function searchOTCDrugFilter(event , root){
//	if(event.keyCode == 13){
//			startFiltering(root , "OTC");
//	}
//}
function searchOTCDrugFilter(event , root){
	if(event.keyCode == 13){
                    startOTCFiltering(root , null);
	}
}

function startFilteringDrugAllergy(event , obj , formId ,element, source) {
	if (event.keyCode != 13) {
		loadValuesFromFDB(formId,element,'',0,source);	
    }
}

function doRxChangeDrugSearch(event , formId){
	if(event.keyCode == 13){
		startFilteringReq(formId , null);
	}	
}

var noPrescriber = false;

function setBooleanTrue(status){
	if(status == "true")
		noPrescriber = true;
	else if(status == "false")
		noPrescriber = false;
}
function createPrescriberWarning(){
	if(noPrescriber == true){
		$('errorDiv1').style.zIndex=19;
		$('errorDiv1').style.left="350px";
		$('errorDiv1').style.top="200px";
		$('errorDiv1').style.width="700px";
		var errordata = 	"<table width='100%'><tr><td  style='text-align:center'><br/><b>Warning ! </b></td></tr>"
		errordata = errordata + "<tr><td style='text-align:center'><b>Please associate a prescriber with this account in order to prescribe.</b></td></tr>" ;
		errordata = errordata + "<tr><td style='text-align:center'><table width='100%'>";
		errordata = errordata + "<tr><td style='text-align:left;padding-left:15%;'>* Click on the <a href='/Tolven/manage/preferences.jsf'> Preferences</a> link on right hand top of the screen.</td></tr>";
		errordata = errordata + "<tr><td style='text-align:left;padding-left:15%;'>* Click on the <b><u>Account Users</u></b> link.</td></tr>";
		errordata = errordata + "<tr><td style='text-align:left;padding-left:15%;'>* Click on the <b><u>Edit</u></b> link.</td></tr>";
		errordata = errordata + "<tr><td style='text-align:left;padding-left:15%;'>* Select a value from <b>Staff Member Association</b> dropdown and click on <b>Update</b> button.</td></tr>";
		errordata = errordata + "</table></td></tr>";
		errordata = errordata + "<tr><td style='text-align:center'>---------------------------------------------------------------------------------------------------------------------------------</td></tr>";
		errordata = errordata + "<tr><td style='text-align:center'><b>Please note that you would not be able to submit a prescription if you do not follow these steps.</b></td></tr>";
		errordata = errordata + "<tr><td  style='text-align: center'><input type='button' value='Close' onclick='closeErrorPrescAlert()'/></td></tr>";
		$('errorDiv1').style.height="250px";
	    $('errorDiv1').innerHTML = errordata;
		$('errorDiv1').style.display="block";
	}
	
}

function linkToPharmacy(root){
	var pieces = root.split(':');
	var pharmacyUrl = pieces[0] + ":" + pieces[1] + ":pharmacies";
	javascript:showPane(pharmacyUrl, false, pharmacyUrl);
	
}




function disableSelections(root){
//		$(root+":statusField:0").disabled = "true";
//		$(root+":statusField:1").disabled = "true";
//		$(root+":statusField:2").disabled = "true";
}


function instantiateNewPrescription(templateId, context, source){
   instantiate(templateId, context, source);
   isNewPrescription = true;
   isDiscontinue = false;
}

var wrongPatient = false;

checkForErrorsInPatient = function (root){
	if($("errorZip").style.display == "block" || $("errorAddSpace").style.display == "block"|| 
			$("errorAdd").style.display == "block" || $("noSpaceFirst").style.display == "block" 
				|| $("noSpaceMiddle").style.display == "block" || $("noSpaceLast").style.display == "block" 
					|| $("errorMsgHomePhone").style.display == "block" || $("errorMsgWorkPhone").style.display == "block"
						|| $("errorMsgCellPhone").style.display == "block"){
		wrongPatient = true;
	}else{
		wrongPatient = false;
	}
}

function disabledPatientSubmission(root){
	if(wrongPatient)
		$(root+"submitButton").disabled = true;
	else
		$(root+"submitButton").disabled=false;
}

function getPrescriber(formId , elmnt){
	var root = formId;
	var element = elmnt;
	var accountId =  document.getElementById("accountId").value;
	var spiNum = $(root+":spi").value;
	if(spiNum == ""){
		alert("The SPI entered is blank. Please enter a valid SPI Number.");
		return;
	}
	var spiInAccount = $(root+":spisInAccnt").value.split('|');
	for(var i=0; i< spiInAccount.length; i++){
		if(spiNum == spiInAccount[i]){
			alert("The SPI entered already exists in this Account. Please specify another SPI.");
			return;
		}
	}
	document.getElementById("ajaxLoader").style.display="block";
	var myAjax = new Ajax.Request(
			   'getPrescriber.ajaxsure',
			   {
			    method: 'get',
			     parameters: '&root='+root+
			    			'&element='+element+
			    			'&accountId='+accountId+
			    			'&spi='+spiNum,
			    onFailure: function(response) {
				   document.getElementById("ajaxLoader").style.display="none";
				   displayError(response,element);
				},
			    onSuccess: function(response) { 
					document.getElementById("ajaxLoader").style.display="none";
					var step = "1";
					if (response.responseText == "") {
						alert("Connection Timed Out.");
					} else if (response.responseText == "No Master Account") {
						alert("Master User Account Not Associated with this Account");
					} else if(response.responseText == "false"){
						document.getElementById("prescriberNotFoundMsg").style.display="block";
						setTimeout("refreshPage('"+element+"','"+root+"','"+step+"');",5000);
					}else{
						step = "2";
						document.getElementById("prescriberFoundMsg").style.display="block";
						setTimeout("refreshPage('"+element+"','"+root+"','"+step+"');",5000);
					}
			    }	          
			   });
}

function setPhoneNumber(root){
	var phoneNumber = $(root+":phone").value;
	var extension = $(root+":extension").value;
	var prescriberPhone = $(root+":prescriberPhone");
	if (extension == null || extension == "") {
		prescriberPhone.value = phoneNumber;        	
	}else{
		prescriberPhone.value = phoneNumber+"x"+extension;
	}
}

function assignPhoneNumber(root){
	alert($(root+":phone").innerHTML);
	$(root+":prescriberPhone").value = $(root+":phone").innerHTML;
	
}

function getMedicationDetails(msgid){
	var myAjax = new Ajax.Request(
			   'getMedicationDetails.ajaxerx',
			   {
			    method: 'get',
			     parameters: 'pod='+msgid,
			    onFailure: function(response) {
				   displayError(response,element);
				},
			    onSuccess: function(response) { 
			    }	          
			   });
}

function startEndDate(root , element){
	if(null != $(root+":daysSupply").value){
		var startDay = $(root+":FieldqueseffectiveTimeLow").value;
		var days = $(root+":daysSupply").value;	
			if(null != days && days != "" && days !=0){
				var myAjax = new Ajax.Request(
						   'calculateStartEndDate.ajaxerx',
						   {
						    method: 'get',
						     parameters: '&root='+root+'&element='+element+'&days='+days,
						    onFailure: function(response) {
							   displayError(response,element);
							},
						    onSuccess: function(response) {
								if(response.responseText  == null || response.responseText  == ""){
									alert("Please select a valid date.");
								}else{
										$(root+":FieldqueseffectiveTimeLow").value=response.responseText.split('|')[0];
										$(root+":FieldqueseffectiveTimeHigh").value=response.responseText.split('|')[1];
								}		
						    }	          
						   });
			}
			$("errorInEndDate").style.display="none";
	}	
}

function selectStrength (formId){
	$(formId+":strength").value = $(formId+":strength1").value;
	alert($(formId+":strength").value);
}

function createUltimateWarning(formId , num){
	if(num == "1") {
		alert("Prescriber is not successfully associated with Surescripts. Please add the prescriber to surescripts.");
	}
	else if(num == "2"){
		alert("This drug is a controlled substance. The prescription cannot be routed to surescripts. ");
	}
}


function changeLowDate(root , element){
	if(null != $(root+":daysSupply").value){
		var startDay = $(root+":FieldqueseffectiveTimeLow").value;
		var days = $(root+":daysSupply").value;	
			if(null != days && days != "" && days !=0){
				var myAjax = new Ajax.Request(
						   'changeEndDateInAccordance.ajaxerx',
						   {
						    method: 'get',
						     parameters: '&root='+root+'&element='+element+'&days='+days+'&start='+startDay,
						    onFailure: function(response) {
							   displayError(response,element);
							},
						    onSuccess: function(response) { 
								if(response.responseText == null || response.responseText  == ""){
									alert("Please select a valid Active Start Date.");
								}else{
									$(root+":FieldqueseffectiveTimeLow").value=response.responseText.split('|')[0];
									$(root+":FieldqueseffectiveTimeHigh").value=response.responseText.split('|')[1];
								}	
						    }	          
						   });
			}
			$("errorInEndDate").style.display="none";
	}	
}

function changeHighDate(root , element){
	
	if(null == $(root+":FieldqueseffectiveTimeHigh") || $(root+":FieldqueseffectiveTimeHigh") == ""){
		$("errorInEndDate").style.display="block";
		$(root+"submitButton").disabled=true;
	}else{
		var startDay = $(root+":FieldqueseffectiveTimeLow").value;
		var endDay = $(root+":FieldqueseffectiveTimeHigh").value;
		var days = $(root+":daysSupply").value;	
		if(null != days && days != "" && days !=0){
			var myAjax = new Ajax.Request(
					   'displayErrorIfFoundWrong.ajaxerx',
					   {
					    method: 'get',
					     parameters: '&root='+root+'&element='+element+'&days='+days+'&start='+startDay+'&end='+endDay,
					    onFailure: function(response) {
						   displayError(response,element);
						},
					    onSuccess: function(response) { 
							if(response.responseText == "false"){
								$("errorInEndDate").style.display="block";
							}else if(response.responseText == "true"){
								$("errorInEndDate").style.display="none";
							}else if(response.responseText == "error"){
								alert("Please enter a valid date.");
							}
					    }	          
					   });
		}
		$(root+"submitButton").disabled=false;
	}
}

function checkIfZero(formId){
	if(parseInt($(formId+":daysSupply").value)==0){
		$(formId+":daysSupply").value="";
		$(formId+":FieldqueseffectiveTimeLow").value="";
		$(formId+":FieldqueseffectiveTimeHigh").value="";
		alert("Please enter a Non Zero value.");
	}
}

function checkDispenseAmount(formId){

		var dispAmnt = $(formId+":dispenseAmount").value;
	    var isDecimal_re= /^\s*(\+|-)?((\d+(\.\d+)?)|(\.\d+))\s*$/;
		if(dispAmnt.search(isDecimal_re) == -1){
			alert("Please enter a valid Decimal Number.");
		}else{
			if(dispAmnt.indexOf(".") != -1){
				var values = dispAmnt.split('.');
				if(strStartsWith(values[0],"0") == 0){
					if(values[0] == "0"){
					}else{
						while(strStartsWith(values[0],"0") == 0){
							var len = values[0].length;
							if(len > 1){
								values[0] = values[0].substring(1,len);
							}	
						}	
					}	
				}else if(values[0] =="" || values[0] == null){
					values[0] = "0";
				}
				if(values[1].endsWith("0")){
					while(values[1].endsWith("0")){
						var len = values[1].length - 1;
						if(len > 0){
							values[1] = values[1].substring(0,len);
						}else{
							$(formId+":dispenseAmount").value = values[0];
							return;
						}
					}
				}
				$(formId+":dispenseAmount").value = values[0]+"."+values[1];
			}else{
				var val = dispAmnt;
				if(strStartsWith(val, "0") == 0){
					while(strStartsWith(val, "0") == 0){
						var len = val.length;
						if(len > 1){
							val= val.substring(1,len);
						}else{
							$(formId+":dispenseAmount").value = val;
							if(parseInt($(formId+":dispenseAmount").value)==0){
								$(formId+":dispenseAmount").value="";
								alert("Please enter a Non Zero value.");
							}
							return;
						}
					}	
				}				
				$(formId+":dispenseAmount").value = val;
			}
		}
}

String.prototype.trim = function(){return
	(this.replace(/^[\s\xA0]+/, "").replace(/[\s\xA0]+$/, ""))}

function checkRefillAmount(formId){
	if(parseInt($(formId+":newRefill").value)==0) {
		$(formId+":newRefill").value="";
	alert("Please enter a Non Zero value.");}
}

function checkRefillsAmount(formId){
	if(parseInt($(formId+":newRefill").value)==0){
		$(formId+":refills").value="";
		alert("Please enter a Non Zero value.");
	}
}

function convertPhoneNumber(){
	var number = $("drilldown:PrimaryPhone").innerHTML;
	var numbers = number.split(" ");
	number = numbers[6];
	$("drilldown:PrimaryPhone").innerHTML = number;
}

function insertValuesNotes(root , obj){
	if(obj == 0)
		$(root+":noteInputField").value=$(root+":noteInputFieldApproved").value;
	else if(obj == 1)
		$(root+":noteInputFieldApproved").value=$(root+":noteInputField").value;
}

function disableSubmitOnZeroRefill(root){
	var flag=0;
	if(null != $(root+"submitButton")){
		if(null != $("drilldown:refillAmount")){
			if(parseInt($("drilldown:refillAmount").innerHTML)==0 || $("drilldown:refillAmount").innerHTML==""){
				alert("Please enter a Non Zero value for Refills.");
				$(root+"submitButton").disabled=true;
				flag=1;
			}
		}
		if(null != $("drilldown:notes")){
			if($("drilldown:notes").innerHTML == ""){
				alert("Please enter Valid Notes.");
				$(root+"submitButton").disabled=true;
				flag=1;
			}
		}
		if(null != $("drilldown:NotesForDeniedNew")){
			if($("drilldown:NotesForDeniedNew").innerHTML == ""){
				alert("Please enter Valid Notes.");
				$(root+"submitButton").disabled=true;
				flag=1;
			}
		}
		if(null != $("drilldown:NotesForDenied")){
			if($("drilldown:NotesForDenied").innerHTML == ""){
				alert("Please enter Valid Notes.");
				$(root+"submitButton").disabled=true;
				flag=1;
			}
		}
		if(null != $("drilldown:NotesEnteredInsteadOfDropDown")){
			if($("drilldown:NotesEnteredInsteadOfDropDown").innerHTML == ""){
				alert("Please enter Valid Reason for Denial.");
				$(root+"submitButton").disabled=true;
				flag=1;
			}
		}
		if((document.getElementById("patientMentioned").value == '#{null}' ||
				$("drilldown:patientHidden").value == '#{others}') && $("drilldown:response").innerHTML != 'Denied'){
			alert("Please associate a patient with the refillRequest.");
			$(root+"submitButton").disabled=true;
			flag=1;
		}
		if(flag ==0){
			$(root+"submitButton").disabled=false;
		}
	}	
}

function showRefillAmount(){
	if(null != $("drilldown:refillQualifier")){
		if($("drilldown:refillQualifier").innerHTML == 'PRN'){
			//alert("correct");
			return false;
		}else{
			//alert("wrong");
			return true;
		}
	}

}

hideThisPart = function(root){
	if(null != $(root+":responseField:2")){
		if($(root+":responseField:2").checked){
			$('notesRowTextArea').style.visibility="hidden";
			$('reasonRow').style.visibility="visible";
		}
		else{
			$('notesRowTextArea').style.visibility="visible";
			$('reasonRow').style.visibility="hidden";
		}
	}
}

function ismaxlength(obj , root){
	var mlength=obj.getAttribute? parseInt(obj.getAttribute("maxlength")) : ""
	if (obj.getAttribute && obj.value.length>mlength)
	obj.value=obj.value.substring(0,mlength)
	
		if(null!=$(root+":sigcodes") && null!=$(root+":sigcodes").value){
			if($(root+":sigcodes").value == ""){
				$(root+":sigLeft").innerHTML = "* maximum character limit allowed is 140.";
			}else{
				var sigLength = $(root+":sigcodes").value.length;
				if(sigLength!=null && sigLength !=0){
					var left = 140-parseInt(sigLength);
					$(root+":sigLeft").innerHTML = left + " characters left/Total 140 characters.";
				}
			}
				
		}
	
	if(null!=$(root+":comments") && null!=$(root+":comments").value){
		if($(root+":comments").value == ""){
			$(root+":commentsLeft").innerHTML = "* maximum character limit allowed is 210.";
		}else{
			var sigLength = $(root+":comments").value.length;
			if(sigLength!=null && sigLength !=0){
				var left = 210-parseInt(sigLength);
				$(root+":commentsLeft").innerHTML = left + " characters left/Total 210 characters.";
			}
		}
			
	}
	
	if(null!=$(root+":noteInputText") && null!=$(root+":noteInputText").value){
		if($(root+":noteInputText").value == ""){
			$(root+":commentsLeft").innerHTML = "maximum character limit allowed is 70.";
		}else{
			var commentsLength = $(root+":noteInputText").value.length;
			if(commentsLength!=null && commentsLength !=0){
				var left = 70-parseInt(commentsLength);
				$(root+":commentsLeft").innerHTML = left + " characters left/Total 70 characters.";
			}
		}
	}
	
	if(null!=$(root+":denialReasonCodeDetail") && null!=$(root+":denialReasonCodeDetail").value){
		if($(root+":denialReasonCodeDetail").value == ""){
			$(root+":commentsDenied").innerHTML = "maximum character limit allowed is 70.";
		}else{
			var commentsLength = $(root+":denialReasonCodeDetail").value.length;
			if(commentsLength!=null && commentsLength !=0){
				var left = 70-parseInt(commentsLength);
				$(root+":commentsDenied").innerHTML = left + " characters left/Total 70 characters.";
			}
		}
	}
	
}

function checkForErrorInDate(root , element){
	
	if($(root+":errorInEndDate") && $(root+":errorInEndDate").style.display == "block"){
		$(root+"submitButton").disabled=true;
	}else{
		if(null != $("drilldown:medication") && $("drilldown:medication").innerHTML == ""){
			alert("Please Select a Medication to Prescribe.");
			$(root+"submitButton").disabled=true;
		}else{
			$(root+"submitButton").disabled=false;
		}
	}
	
	if($("drilldown:refQualifier").innerHTML=="PRN"){
		var myAjax = new Ajax.Request(
				   'changeRefillsAmountWhenPRN.ajaxerx',
				   {
				    method: 'get',
				     parameters: '&root='+root+'&element='+element,
				    onFailure: function(response) {
					   displayError(response,element);
					},
				    onSuccess: function(response) { 
				    }	          
				   });
	}	
	
}

function submitMedicationDetails(root, element){
	
	if ($("drilldown:response").innerHTML != "Denied") {
	var patId = "";
		if ($("patientMentioned").value == "#{others}"
				|| $("patientMentioned").value == "#{null}") {
		patId = $("drilldown:patientHidden").value; 
	}else {
		patId = $("patMentioned").value
	}
	if (patId != "") {
	var myAjax = new Ajax.Request(
				'submitMedicationDetailsInBackground.ajaxerx', {
			    method: 'get',
					parameters : '&root=' + root + '&element=' + element
							+ '&patientId=' + patId,
			    onFailure: function(response) {
				   displayError(response,element);
				},
			    onSuccess: function(response) { 
			    }	          
			   });
		}
	}
}

function saveSameMedication(formId , element){
	    $(formId+":drugQualifier").value="EA";
	    $(formId+":drugQualDescription").value="Each";
	    $(formId + ":hiddenDrugName").value = $(formId+":requestedMedicine").innerHTML;
		var drugName = escape($(formId+":requestedMedicine").innerHTML);
		var drugCode = "";
		document.getElementById("ajaxLoader").style.display="block";
		var myAjax = new Ajax.Request(
				   'getDrugCodeFromDrugName.ajaxfdb',
				   {
				    method: 'get',
				     parameters: '&root='+formId+'&element='+element+'&drugName='+drugName,
				    onFailure: function(response) {
					   document.getElementById("ajaxLoader").style.display="none";
					   alert("Unable to find medicine. Select New Medication.");
					},
				    onSuccess: function(response) { 
						document.getElementById("ajaxLoader").style.display="none";
						drugCode = response.responseText;
						var priorMedicines = escape($(formId + ":priorMedicines").value);
						var priorAllergies = escape($(formId + ":priorAllergies").value);
						 var myAjax = new Ajax.Request(
											'fdbCollect.ajaxfdb', 
						    			{
						    			 method: 'get', 
						    			 parameters: '&drug_name='+drugName+'&drug_code='+drugCode+'&element='+element+'&priorMed='+priorMedicines+'&priorAlleg='+priorAllergies, 
						         		 onFailure: function() {
						         		 location.href=document.URL;
						         		  document.getElementById("ajaxLoader").style.display="none";
						         		 },
							     		  onComplete: function(response) {
							     		  	var fullResponse = response.responseText;
							     		 	var responseFDB = fullResponse.split('|');
							     		 	if (responseFDB.length>=18 && responseFDB[17]!=null && document.getElementById(formId+":strengthUnits"))
							     		 		document.getElementById(formId+":strengthUnits").value=responseFDB[17];
							     		  	document.getElementById(formId+":hasDrugDrugInterraction").value=responseFDB[0];
							     		  	document.getElementById(formId+":hasDrugFoodInterraction").value=responseFDB[1];
							     		  	document.getElementById(formId+":hasDuplicateTherapy").value=responseFDB[2];
							     		  	document.getElementById(formId+":hasDrugAllergy").value=responseFDB[3];
						     		  		document.getElementById(formId+":strength").value=responseFDB[4];
						     		  		document.getElementById(formId+":strength").disabled=true;
							     		  	document.getElementById(formId+":minmax").value=responseFDB[5];
							     		  	if(responseFDB[6] != ""){
							     		  		document.getElementById(formId+":sigcodes").value=responseFDB[6]+",";
							     		  	}else{
							     		  		document.getElementById(formId+":sigcodes").value="";
							     		  	}	
							     		  	document.getElementById("maxDose").value=responseFDB[7];
							     		  	$(formId + ":classficationFromFdb").value=responseFDB[8];
							     		    $(formId + ":ctactiongroupClassficationFromFdb").value=responseFDB[15];
							     		    $(formId + ":ctetcClassficationFromFdb").value=responseFDB[13];
							     		    $(formId + ":ctfdbClassficationFromFdb").value=responseFDB[14];
							     		  $('errorDiv').style.left="100px";
							     			$('errorDiv').style.top="300px";
							     			$('errorDiv').style.width="800px";
							     			var flag = "false";
							     			var heightDiv = 150;
							     			var errordata = 	"<table width='100%'><tr><td colspan='2' style='text-align:center'><br/><b>Information on the Drug Prescribed ! </b></td></tr>";
							     			if(null != responseFDB[10] && responseFDB[10] != ""){
							     			errordata = errordata + "<tr><td colspan='2'><b>. Prescriber Messages </b></td></tr><tr><td colspan='2'>"+responseFDB[9]+"</td></tr>";
							     			heightDiv = heightDiv + 50;
							     			flag="true";
							     			}
							     			if(null != responseFDB[10] && responseFDB[10] != ""){
							     				errordata = errordata + "<tr><td colspan='2'><b>. Patient Messages </b></td></tr><tr><td colspan='2'>"+responseFDB[10]+"</td></tr>";
								     			heightDiv = heightDiv + 50;
							     				flag="true";
							     		    }
							     		    if(null != responseFDB[11] && responseFDB[11] != ""){
							     		    	errordata = errordata + "<tr><td colspan='2'><b>. Common Order </b></td></tr><tr><td colspan='2'>"+responseFDB[11]+"</td></tr>";
								     			heightDiv = heightDiv + 25;
							     		    	flag="true";
							     		    }
							     		    if(null != responseFDB[12] && responseFDB[12] != ""){
							     		    	errordata = errordata + "<tr><td colspan='2'><b>. POEM </b></td></tr><tr><td colspan='2'>"+responseFDB[12]+"</td></tr>";
								     			heightDiv = heightDiv + 25;
							     		    	flag="true";
							     		    }
							     			
							     		   if(responseFDB[16] == "true"){
							     		    	errordata = errordata + "<tr><td colspan='2'><b>. Controlled Substance </b></td></tr><tr><td colspan='2'> The Prescription cannot be routed to Surescripts. Please dispense it from office.</td></tr>";
							     		    	flag="true";
							     		    }
							     		   
							     		    if(flag == "true"){
							     		    	errordata = errordata + "<tr><td colspan='2' style='text-align:center'><input type='button' value='Close' onclick='closeErrorAlert()'/></td></tr> </table> ";
							     		    	$('errorDiv').style.height=heightDiv+"px";
							     		    	$('errorDiv').innerHTML = errordata;
//							     		    	$('errorDiv').style.background="#d8e8ff"; // Different color
							     		    	$('errorDiv').style.display="block";
							     		    }	
							     		    
							     		   var ndcAjax = new Ajax.Request(
							     					'retrieveNDCInformation.ajaxfdb',
							     					{
							     						method: 'get',
							     						parameters: '&drug_name='+drugName+'&drug_code='+drugCode+'&element='+element,
							     						onSuccess: function(response){
							     						document.getElementById("ajaxLoader").style.display="none";
							     						},
							     						onFailure: function(response) {
							     							displayError(request,param);
							     						document.getElementById("ajaxLoader").style.display="none";	}
							     					});
							     		   
							     		 }
						   				 });	
						   				 $(formId + ":medicineSelected").value = $(formId+":requestedMedicine").innerHTML;
						closePopUp();
						document.getElementById("ajaxLoader").style.display="none";	
				    }	          
				   });
}


function changeDescription(root , element){
	var qualCode = $(root+":drugQualifier").value;
	var myAjax = new Ajax.Request(
			   'changeQualDescription.ajaxsure',
			   {
			    method: 'get',
			     parameters: '&root='+root+'&element='+element+'&qualCode='+qualCode,
			    onFailure: function(response) {
				   displayError(response,element);
				},
			    onSuccess: function(response) { 
					$(root+":drugQualDescription").value = response.responseText;
			    }	          
			   });
}



function submitSharedMedication(root, element){
	var myAjax = new Ajax.Request(
			   'submitSharedMedication.ajaxerx',
			   {
			    method: 'get',
			     parameters: '&root='+root+'&element='+element,
			    onFailure: function(response) {
				   displayError(response,element);
				},
			    onSuccess: function(response) { 
			    }	          
			   });
}

function submitSharedMedicationHistory(root, element){
	var myAjax = new Ajax.Request(
			   'submitSharedMedicationHistory.ajaxerx',
			   {
			    method: 'get',
			     parameters: '&root='+root+'&element='+element,
			    onFailure: function(response) {
				   displayError(response,element);
				},
			    onSuccess: function(response) { 
			    }	          
			   });
}


/**
*Function to load values into the pop-up menu.
*/
function showSharedPopup(spanId){
	rowId = spanId.parentNode.parentNode;
	var accountType = document.getElementById("accountType").value;
	var offsetTop = rowId.offsetTop;	
	var urlPrefix = "transparent url(/Tolven/scripts/simile/images/";
	var urlSuffix = ") repeat scroll 0% 0%";
	
	function createPopup(pos, image, left, top, width, height){
		var popup = document.getElementById(pos);
		if(!(pos == "content")){
			popup.style.background = urlPrefix+ image +urlSuffix ;
			popup.style.position = "absolute";
		}
		else{
			popup.style.background = "white none repeat scroll 0% 0%";
			popup.style.overflow = "auto";
			popup.style.position = "absolute";
		}
		popup.style.left = left + "px";
		popup.style.top = (offsetTop+top) + "px";
		popup.style.width = width + "px";
		popup.style.height = height + "px";	
	}
	createPopup("top-left", "bubble-top-left.png", 0, 0, 33, 33);
	createPopup("top", "bubble-top.png", 33, 0, 250, 33);
	createPopup("top-right", "bubble-top-right.png", 283, 0, 40, 33);
	createPopup("left", "bubble-left.png", 0, 33, 33, 125);
	createPopup("right", "bubble-right.png", 283, 33, 40, 125);
	createPopup("bottom-left", "bubble-bottom-left.png", 0, 158, 33, 42);
	createPopup("bottom", "bubble-bottom.png", 33, 158, 250, 42);
	createPopup("bottom-right", "bubble-bottom-right.png", 283, 158, 40, 42);
	createPopup("close-button", "close-button.png", 280, 19, 16, 16);
	createPopup("content", "", 33, 33, 250, 125);
	createPopup("bottom-arrow", "bubble-bottom-arrow.png", 140, 158, 37, 42);
	
	if(accountType == 'echr'){		
		messageId = rowId.cells[0].childNodes[0].innerHTML;
		//alert(messageId);
		if(messageId != '&nbsp;'){		
			var medicationName = rowId.cells[2].innerHTML;
			var prescribedBy = rowId.cells[9].innerHTML;
			var strength = rowId.cells[3].innerHTML;
			var route = rowId.cells[6].innerHTML;
			var refill = rowId.cells[8].innerHTML;
			
			document.getElementById("eventBubble").style.display="block";
			document.getElementById("medicationName").innerHTML = medicationName;
			document.getElementById("prescribedBy").innerHTML = prescribedBy;
			document.getElementById("strength").innerHTML = strength;
			document.getElementById("route").innerHTML = route;
			document.getElementById("refill").innerHTML = refill;
			document.getElementById("medicationNew").disabled=true;
			document.getElementById("medicationAdmin").disabled=true;
			document.getElementById("medicationModify").disabled=true;
			document.getElementById("medicationRefill").disabled=true;
			document.getElementById("medicationReport").disabled=true;
			document.getElementById("prescriber").innerHTML="Provider";
			
			closePopUp();
		}
	}
	else if(accountType == 'ephr'){
		messageId = rowId.cells[0].childNodes[0].innerHTML;
		if(messageId != '&nbsp;'){		
			var medicationName = rowId.cells[2].innerHTML;
			var strength = rowId.cells[3].innerHTML;
			var route = rowId.cells[7].innerHTML;
			
			document.getElementById("eventBubble").style.display="block";
			document.getElementById("medicationName").innerHTML = medicationName;
			document.getElementById("strength").innerHTML = strength;
			document.getElementById("route").innerHTML = route;
			closePopUp();
		}
	}	
	else return;
}

function trim(stringToTrim) {
	return stringToTrim.replace(/^\s+|\s+$/g,"");
}

function controlledDrugCases(root , element){
	if(null!= $("errorPON")){
		var errorPon = $("errorPON").innerHTML;
		if(trim(errorPon)=="No Prescriptions with the Mentioned PON found in the Account." && $("otherPrescriber")== null){			
//			$(root+":responseField:2").checked=true;
//			$(root+":responseField:0").disabled=false; 
//			$(root+":responseField:1").disabled=true; //Disable deny with newRx
		}
		else if ($("otherPrescriber") != null && document.getElementById(root+":noteInputText").value != "") {
//			document.getElementById(root+":noteInputText").readOnly=true;
		}
	}		
}

function checkDrugValidity(root , element){
	
	if (($("isScheduleDrug") != null && $("isScheduleDrug").value == "Yes")) {
		if ($(root+":responseField:0").checked == true) {
			$(root+":noteInputText").value = "Schedule III to V:The approved controlled substance Rx will be faxed.";
			$(root+":noteInputText").readOnly = true;
			$(root+":commentsLeft").style.display = "none";
		}
	}
	
	if ($(root+":responseField:0").checked == false && $(root+":responseField:1").checked == false
			&& $(root+":responseField:2").checked == false) {
		$(root+":responseField:2").checked = true;
	}
	
	controlledDrugCases(root , element);
	var pat="";
	var ponMatched = true;
	var patientMatched = true;
	var myAjax = new Ajax.Request(
			   'getAllPatients.ajaxerx',
			   {
			    method: 'get',
			     parameters: '&root='+root+'&element='+element,
			    onFailure: function(response) {
				   displayError(response,element);
				},
			    onSuccess: function(response) { 
					var labels = response.responseText.split('|HRF|');
					if ((labels[labels.length-1] == "Valid PON with RefReq equals PON patient" &&
							$(root+":initialPONStatus").innerHTML != "Nil" && $("errorPON").style.display=="none")){ 
						$("matchPatient").style.display = "none";
						pat = labels[0].split("|SEPARATE|")[0];
						$(root+":patientHidden").value = pat;
					} else {
					var selectbox  = $("patientMentioned");
					var j;
					for(j=selectbox.options.length-1;j>=0;j--)
					{
					selectbox.remove(j);
					}
					var optn1 = document.createElement("OPTION");
					optn1.text = "Select Patient";
					optn1.value = "#{null}";
					selectbox.options.add(optn1);
//						var labels = response.responseText.split('|HRF|');
					for(var i=0; i < labels.length; i++){
						if(labels[i] != ""){
							if ((labels[labels.length-1] == "Valid PON with RefReq equals PON patient" && i< labels.length-1)
									|| (labels[labels.length-1] == "Invalid or no PON with refReq patient in app" && i< labels.length-1)
									|| (labels[labels.length-1] == "Valid PON with mismatch and refillReq patient in app" && i< labels.length-1)
									|| (labels[labels.length-1] == "Valid PON with mismatch and refillReq patient not in app" && i< labels.length-1)) {
								var optn = document.createElement("OPTION");
								optn.text = labels[i].split("|SEPARATE|")[1];
								optn.value = labels[i].split("|SEPARATE|")[0];
								selectbox.options.add(optn);
								pat = labels[0].split("|SEPARATE|")[0];
							}							
							if (labels[i] == "Valid PON with mismatch and refillReq patient in app"
									|| labels[i] == "Valid PON with mismatch and refillReq patient not in app"
									|| labels[i] == "Invalid or no PON with refReq patient not in app") {	
								var optn2 = document.createElement("OPTION");
								optn2.text = "Others";
								optn2.value = "#{others}";
								selectbox.options.add(optn2);
								patientMatched = false;
							}
							if (labels[i] == "Invalid or no PON with refReq patient in app") {	
								var optn2 = document.createElement("OPTION");
								optn2.text = "Others";
								optn2.value = "#{others}";
								selectbox.options.add(optn2);
							}
							if (labels[i] == "Invalid or no PON with refReq patient not in app" 
									|| labels[i] == "Invalid or no PON with refReq patient in app") {
								ponMatched = false;
							}
						}
					}
					}
					if (ponMatched && $(root+":initialPONStatus").innerHTML != 'Nil') { 		//Checking for PON match.
						document.getElementById(root+":ponMatched").innerHTML="Yes";
						document.getElementById(root+":ponMatched").style.color="Green";
					}else {
						document.getElementById(root+":ponMatched").innerHTML="No";
						document.getElementById(root+":ponMatched").style.color="Red";
					}
					if (patientMatched) { 		//Checking for Patient match.
						document.getElementById(root+":patientMatched").innerHTML="Yes";
						document.getElementById(root+":patientMatched").style.color="Green";
					}else {
						document.getElementById(root+":patientMatched").innerHTML="No";
						document.getElementById(root+":patientMatched").style.color="Red";
					}
			    }	          
			   });
}

function printControlledRefillResponse(element, root, patient){
	if($(root+":responseField:0").checked==true){
		var hrefPath=null;
		hrefPath="/Tolven/drilldown/RefillResponseReportForControlledSubstance.jsf?element="+element+"&patientId="+patient+"&RenderOutputType=pdf";
		window.open(hrefPath, '_blank');
	}
}

function showXML(id){
	var myAjax = new Ajax.Request('retrieveXML.ajaxsure',
	 	{
		    method: 'get',
		    parameters: '&id='+id,	//+'&patElement='+patElement
		    onFailure: function(response) {
			   displayError(response,element);
			},
		    onSuccess: function(response) { 
				var str = response.responseText;
				document.getElementById("surescriptsMessageCenter:messageDetails").value = str;
				document.getElementById("messageDetailsDiv").style.display="block";
		    }	          
	   });
}
function closeMessageDetails(){
	document.getElementById("messageDetailsDiv").style.display="none";
}
function changeDaysSupplyToBlank(root){
	if(null != $(root+":daysSupply") && null != $(root+":daysSupply").value){
		if($(root+":daysSupply").value == "0"){
			$(root+":daysSupply").value="";
		}
	}
}

/**
 * Method used to populate the medication pop w.r.t the patient selected from the drop down.
 * @param root
 * @param element
 * @return
 */
function buildRxAssocationDropdown(root , element){
	if ($("patientMentioned").value == "#{others}") {
		$("patientPopup").style.display="block";
	}else {
		$("patientPopup").style.display="none";
	}
	$(root+":patientHidden").value= $("patientMentioned").value;
	if($("patientMentioned").value != ""){
		var patElement = $("patientMentioned").value;
		var myAjax = new Ajax.Request(
				   'fetchPatientSpecificRXs.ajaxerx',
				   {
				    method: 'get',
				     parameters: '&root='+root+'&patElement='+patElement,
				    onFailure: function(response) {
					   displayError(response,element);
					},
				    onSuccess: function(response) { 
						var selectbox  = $("prescriptionMentioned");
						var j;
						for(j=selectbox.options.length-1;j>=0;j--)
						{
						selectbox.remove(j);
						}
						
						var optn1 = document.createElement("OPTION");
						optn1.text = "Select Medicine";
						optn1.value = "#{null}";
						selectbox.options.add(optn1);
						
						var labels = response.responseText.split('|');
						for(var i=0; i < labels.length-3; i++){
							if(labels[i] != ""){
								var optn = document.createElement("OPTION");
								optn.text = labels[i];
								optn.value = labels[i].split(':')[1];
								selectbox.options.add(optn);
							}	
						}
						$("patientDetails").innerHTML='';
						$("medicationDetails").innerHTML='';
						$("scheduleDetails").innerHTML='';
						$("refillDetails").innerHTML='';
						$("dateRxDetails").innerHTML='';
						$("newrxdetails").style.display='none';
//						displaySelectedValue(root , element , labels[0].split(':')[1]);
				    }	          
				   });
		
	}else{
		var selectbox  = $("prescriptionMentioned");
		var j;
		for(j=selectbox.options.length-1;j>=0;j--)
		{
		selectbox.remove(j);
		}
	}
	
}

/**
 * Method used to populate the medication pop w.r.t the patient selected from the pop-up.
 * @param path
 * @param element
 * @return
 */
function patientPopupRefReq(path, element){
	var root = $("elementLabel").value;
	closePopup('echr:patients:patPopupList',root );
	
	$(root+":patientHidden").value= path;
	if(path != ""){
		var patElement = path;
		var myAjax = new Ajax.Request(
				   'fetchPatientSpecificRXs.ajaxerx',
				   {
				    method: 'get',
				     parameters: '&root='+root+'&patElement='+patElement,
				    onFailure: function(response) {
					   displayError(response,element);
					},
				    onSuccess: function(response) { 
						var resp = response.responseText.split('|');
						var patName;
						if (resp[resp.length-2] == 'null') {
							patName = resp[resp.length-3]+ ", "+resp[resp.length-1];
						}else {
							patName = resp[resp.length-3]+ ", " +resp[resp.length-2]+ ", "+resp[resp.length-1];
						}
						$(root+":patientSelected").innerHTML = patName;
						
						if ($("change") != null || $("save") != null) {
							var selectbox  = $("prescriptionMentioned");
							var j;
							for(j=selectbox.options.length-1;j>=0;j--)
							{
							selectbox.remove(j);
							}
							
							var optn1 = document.createElement("OPTION");
							optn1.text = "Select Medicine";
							optn1.value = "#{null}";
							selectbox.options.add(optn1);
							
							var labels = response.responseText.split('|');
							for(var i=0; i < labels.length-3; i++){
								if(labels[i] != ""){
									var optn = document.createElement("OPTION");
									optn.text = labels[i];
									optn.value = labels[i].split(':')[1];
									selectbox.options.add(optn);
								}	
							}
							$("patientDetails").innerHTML='';
							$("medicationDetails").innerHTML='';
							$("scheduleDetails").innerHTML='';
							$("refillDetails").innerHTML='';
							$("dateRxDetails").innerHTML='';
							$("newrxdetails").style.display='none';
//							displaySelectedValue(root , element , labels[0].split(':')[1]);
						}
				    }	          
				   });
		
	}else{
		var selectbox  = $("prescriptionMentioned");
		var j;
		for(j=selectbox.options.length-1;j>=0;j--)
		{
		selectbox.remove(j);
		}
	}
//	$(root+":patientHidden").value= path;
}

/**
 * The medicine selected in the drop down will be displayed in
 * 'Original Prescription Details In the Application' block.
 * @param root
 * @param element
 * @return
 */
function displaySelectedValue(root , element){
	var selectedValue = $("prescriptionMentioned").value;
	if(selectedValue != null && selectedValue != "" && selectedValue != "#{null}"){
		$("pon").value = selectedValue;
		var myAjax = new Ajax.Request(
				   'insertPON.ajaxerx',
				   {
				    method: 'get',
				     parameters: '&root='+root+'&element='+element+'&pon='+selectedValue,
				    onFailure: function(response) {
					   displayError(response,element);
					},
				    onSuccess: function(response) { 
						if(response.responseText=="No Prescriptions with the Mentioned PON found in the Account."){
							$("errorPON").style.display="block";
							$("errorPON").innerHTML="No Prescriptions with the Mentioned PON found in the Account.";
							$("newrxdetails").style.display="none";
							
							/*  Processing of the option fields start
							$(root+":responseField:2").checked=true;
							$(root+":responseField:0").disabled=false;
							$(root+":responseField:1").disabled=false;
							//  Processing of the option fields over*/
							
						}else{
							var responseSplit = response.responseText.split("|HRF|");
							
							$("patientDetails").innerHTML=responseSplit[0];
							$("medicationDetails").innerHTML=responseSplit[1];
							$("scheduleDetails").innerHTML=responseSplit[2];
							if(responseSplit[3] != "No Refills"){
							$("refillDetails").innerHTML=responseSplit[3];
							}
							$("dateRxDetails").innerHTML=responseSplit[4];
							$("isControlled").value=responseSplit[5];
							if ($("originalPON").value == "") {
								$("medHead").innerHTML = "Details of Medication with PON "+ selectedValue;
							}
							$("errorPON").innerHTML=null;
							$("errorPON").style.display="none";
							if(null != $("newrxdetails"))
							$("newrxdetails").style.display="block";
						}
						$("save").value="Change";
				    }	          
				   });
	}
}

function removeRxAssociation(root , element){
	var myAjax = new Ajax.Request(
			   'removePON.ajaxerx',
			   {
			    method: 'get',
			     parameters: '&root='+root+'&element='+element,
			    onFailure: function(response) {
				   displayError(response,element);
				},
			    onSuccess: function(response) { 
					$("pon").value="";
					refreshPage(element, root, 2);
			    }	          
			   });
}
function loadRefillsIfNull(root){
	if($(root+":refill").value==""){
		$(root+":refill").value="1";
	}
}

function gatherOriginalPrescriptionInfo(root, element){
		if($("pon").value != null && $("pon").value !=""){
				var myAjax = new Ajax.Request(
				   'gatherOriginalPrescriptionInfo.ajaxerx',
				   {
				    method: 'get',
				     parameters: '&root='+root+'&element='+element,
				    onFailure: function(response) {
					   displayError(response,element);
					},
				    onSuccess: function(response) {
						if(response.responseText=="No Prescriptions with the Mentioned PON found in the Account."){
//							$("patientMatch").style.display="none";
							$("errorPON").style.display="block";
							$("errorPON").innerHTML="No Prescriptions with the Mentioned PON found in the Account.";
							$("newrxdetails").style.display="none";
							if(null != $("change"))
							$("change").style.display="none";
							
						} else{
							var responseSplit = response.responseText.split("|HRF|");
							$("patientDetails").innerHTML=responseSplit[0];
							$("medicationDetails").innerHTML=responseSplit[1];
							$("scheduleDetails").innerHTML=responseSplit[2];
							if(responseSplit[3] != "No Refills"){
							$("refillDetails").innerHTML=responseSplit[3];
							}
							$("dateRxDetails").innerHTML=responseSplit[4];
							$("isControlled").value=responseSplit[5];
							$("errorPON").innerHTML=null;
							$("errorPON").style.display="none";
							$("newrxdetails").style.display="block";
							if(null != $("change")) {
								$("change").style.display="block";
							}
								
							/*  Processing of the option fields start
								$(root+":responseField:2").checked=true;
								$(root+":responseField:0").disabled=false;
							$(root+":responseField:1").disabled=false;*/
								
							/*if($("refillsRequested").value=="0" || $("refillsRequested").value=="" || $("refillsRequested").value=="PRN" || $("refillsRequested").value == null){
								$(root+":responseField:0").checked=true;  // Approved
								$(root+":refillQualifierField:0").checked=true; //Qualifier to R
							}*/
							}
							}
				   });
			}
}

/* ********************************************************* Date Picker Functions ****************************************************************/


var datePickerDivID = "datepicker";
var iFrameDivID = "datepickeriframe";

var dayArrayShort = new Array('Su', 'Mo', 'Tu', 'We', 'Th', 'Fr', 'Sa');
var dayArrayMed = new Array('Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat');
var dayArrayLong = new Array('Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday');
var monthArrayShort = new Array('Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec');
var monthArrayMed = new Array('Jan', 'Feb', 'Mar', 'Apr', 'May', 'June', 'July', 'Aug', 'Sept', 'Oct', 'Nov', 'Dec');
var monthArrayLong = new Array('January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December');
 
var defaultDateSeparator = "-";        
var defaultDateFormat = "ymd"    // valid values are "mdy", "dmy", and "ymd"
var dateSeparator = defaultDateSeparator;
var dateFormat = defaultDateFormat;

function displayDatePicker(dateFieldName, displayBelowThisObject, dtFormat, dtSep)
{
  var targetDateField = document.getElementsByName (dateFieldName).item(0);
 
  // if we weren't told what node to display the datepicker beneath, just display it
  // beneath the date field we're updating
  if (!displayBelowThisObject)
    displayBelowThisObject = targetDateField;
 
  // if a date separator character was given, update the dateSeparator variable
  if (dtSep)
    dateSeparator = dtSep;
  else
    dateSeparator = defaultDateSeparator;
 
  // if a date format was given, update the dateFormat variable
  if (dtFormat)
    dateFormat = dtFormat;
  else
    dateFormat = defaultDateFormat;
 
  var x = displayBelowThisObject.offsetLeft;
  var y = displayBelowThisObject.offsetTop + displayBelowThisObject.offsetHeight ;
 
  // deal with elements inside tables and such
  var parent = displayBelowThisObject;
  while (parent.offsetParent) {
    parent = parent.offsetParent;
    x += parent.offsetLeft;
    y += parent.offsetTop ;
  }
 
  drawDatePicker(targetDateField, x, y);
}

function drawDatePicker(targetDateField, x, y)
{
  var dt = getFieldDate(targetDateField.value );
 
  // the datepicker table will be drawn inside of a <div> with an ID defined by the
  // global datePickerDivID variable. If such a div doesn't yet exist on the HTML
  // document we're working with, add one.
  if (!document.getElementById(datePickerDivID)) {
    // don't use innerHTML to update the body, because it can cause global variables
    // that are currently pointing to objects on the page to have bad references
    //document.body.innerHTML += "<div id='" + datePickerDivID + "' class='dpDiv'></div>";
    var newNode = document.createElement("div");
    newNode.setAttribute("id", datePickerDivID);
    newNode.setAttribute("class", "dpDiv");
    newNode.setAttribute("style", "visibility: hidden;");
    document.body.appendChild(newNode);
  }
 
  // move the datepicker div to the proper x,y coordinate and toggle the visiblity
  var pickerDiv = document.getElementById(datePickerDivID);
  pickerDiv.style.position = "absolute";
  pickerDiv.style.left = x + "px";
  pickerDiv.style.top = y + "px";
  pickerDiv.style.visibility = (pickerDiv.style.visibility == "visible" ? "hidden" : "visible");
  pickerDiv.style.display = (pickerDiv.style.display == "block" ? "none" : "block");
  pickerDiv.style.zIndex = 10000;
 
  // draw the datepicker table
  refreshDatePicker(targetDateField.name, dt.getFullYear(), dt.getMonth(), dt.getDate());
}


/**
This is the function that actually draws the datepicker calendar.
*/
function refreshDatePicker(dateFieldName, year, month, day)
{
  // if no arguments are passed, use today's date; otherwise, month and year
  // are required (if a day is passed, it will be highlighted later)
  var thisDay = new Date();
 
  if ((month >= 0) && (year > 0)) {
    thisDay = new Date(year, month, 1);
  } else {
    day = thisDay.getDate();
    thisDay.setDate(1);
  }
 
  // the calendar will be drawn as a table
  // you can customize the table elements with a global CSS style sheet,
  // or by hardcoding style and formatting elements below
  var crlf = "\r\n";
  var TABLE = "<table cols=7 class='dpTable'>" + crlf;
  var xTABLE = "</table>" + crlf;
  var TR = "<tr class='dpTR'>";
  var TR_title = "<tr class='dpTitleTR'>";
  var TR_days = "<tr class='dpDayTR'>";
  var TR_todaybutton = "<tr class='dpTodayButtonTR'>";
  var xTR = "</tr>" + crlf;
  var TD = "<td class='dpTD' onMouseOut='this.className=\"dpTD\";' onMouseOver=' this.className=\"dpTDHover\";' ";    // leave this tag open, because we'll be adding an onClick event
  var TD_title = "<td colspan=5 class='dpTitleTD'>";
  var TD_buttons = "<td class='dpButtonTD'>";
  var TD_todaybutton = "<td colspan=7 class='dpTodayButtonTD'>";
  var TD_days = "<td class='dpDayTD'>";
  var TD_selected = "<td class='dpDayHighlightTD' onMouseOut='this.className=\"dpDayHighlightTD\";' onMouseOver='this.className=\"dpTDHover\";' ";    // leave this tag open, because we'll be adding an onClick event
  var xTD = "</td>" + crlf;
  var DIV_title = "<div class='dpTitleText'>";
  var DIV_selected = "<div class='dpDayHighlight'>";
  var xDIV = "</div>";
 
  // start generating the code for the calendar table
  var html = TABLE;
 
  // this is the title bar, which displays the month and the buttons to
  // go back to a previous month or forward to the next month
  html += TR_title;
  html += TD_buttons + getButtonCode(dateFieldName, thisDay, -1, "&lt;") + xTD;
  html += TD_title + DIV_title + monthArrayLong[ thisDay.getMonth()] + " " + thisDay.getFullYear() + xDIV + xTD;
  html += TD_buttons + getButtonCode(dateFieldName, thisDay, 1, "&gt;") + xTD;
  html += xTR;
 
  // this is the row that indicates which day of the week we're on
  html += TR_days;
  for(i = 0; i < dayArrayShort.length; i++)
    html += TD_days + dayArrayShort[i] + xTD;
  html += xTR;
 
  // now we'll start populating the table with days of the month
  html += TR;
 
  // first, the leading blanks
  for (i = 0; i < thisDay.getDay(); i++)
    html += TD + "&nbsp;" + xTD;
 
  // now, the days of the month
  do {
    dayNum = thisDay.getDate();
    TD_onclick = " onclick=\"updateDateField('" + dateFieldName + "', '" + getDateString(thisDay) + "');\">";
    
    if (dayNum == day)
      html += TD_selected + TD_onclick + DIV_selected + dayNum + xDIV + xTD;
    else
      html += TD + TD_onclick + dayNum + xTD;
    
    // if this is a Saturday, start a new row
    if (thisDay.getDay() == 6)
      html += xTR + TR;
    
    // increment the day
    thisDay.setDate(thisDay.getDate() + 1);
  } while (thisDay.getDate() > 1)
 
  // fill in any trailing blanks
  if (thisDay.getDay() > 0) {
    for (i = 6; i > thisDay.getDay(); i--)
      html += TD + "&nbsp;" + xTD;
  }
  html += xTR;
 
  // add a button to allow the user to easily return to today, or close the calendar
  var today = new Date();
  var todayString = "Today is " + dayArrayMed[today.getDay()] + ", " + monthArrayMed[ today.getMonth()] + " " + today.getDate();
  html += TR_todaybutton + TD_todaybutton;
  html += "<button class='dpTodayButton' onClick='refreshDatePicker(\"" + dateFieldName + "\");'>this month</button> ";
  html += "<button class='dpTodayButton' onClick='updateDateField(\"" + dateFieldName + "\");'>close</button>";
  html += xTD + xTR;
 
  // and finally, close the table
  html += xTABLE;
 
  document.getElementById(datePickerDivID).innerHTML = html;
  // add an "iFrame shim" to allow the datepicker to display above selection lists
  adjustiFrame();
}


/**
Convenience function for writing the code for the buttons that bring us back or forward
a month.
*/
function getButtonCode(dateFieldName, dateVal, adjust, label)
{
  var newMonth = (dateVal.getMonth () + adjust) % 12;
  var newYear = dateVal.getFullYear() + parseInt((dateVal.getMonth() + adjust) / 12);
  if (newMonth < 0) {
    newMonth += 12;
    newYear += -1;
  }
 
  return "<button class='dpButton' onClick='refreshDatePicker(\"" + dateFieldName + "\", " + newYear + ", " + newMonth + ");'>" + label + "</button>";
}


/**
Convert a JavaScript Date object to a string, based on the dateFormat and dateSeparator
variables at the beginning of this script library.
*/
function getDateString(dateVal)
{
  var dayString = "00" + dateVal.getDate();
  var monthString = "00" + (dateVal.getMonth()+1);
  dayString = dayString.substring(dayString.length - 2);
  monthString = monthString.substring(monthString.length - 2);
 
  switch (dateFormat) {
    case "dmy" :
      return dayString + dateSeparator + monthString + dateSeparator + dateVal.getFullYear();
    case "ymd" :
      return dateVal.getFullYear() + dateSeparator + monthString + dateSeparator + dayString;
    case "mdy" :
    default :
      return monthString + dateSeparator + dayString + dateSeparator + dateVal.getFullYear();
  }
}


/**
Convert a string to a JavaScript Date object.
*/
function getFieldDate(dateString)
{
  var dateVal;
  var dArray;
  var d, m, y;
 
  try {
    dArray = splitDateString(dateString);
    if (dArray) {
      switch (dateFormat) {
        case "dmy" :
          d = parseInt(dArray[0], 10);
          m = parseInt(dArray[1], 10) - 1;
          y = parseInt(dArray[2], 10);
          break;
        case "ymd" :
          d = parseInt(dArray[2], 10);
          m = parseInt(dArray[1], 10) - 1;
          y = parseInt(dArray[0], 10);
          break;
        case "mdy" :
        default :
          d = parseInt(dArray[1], 10);
          m = parseInt(dArray[0], 10) - 1;
          y = parseInt(dArray[2], 10);
          break;
      }
      dateVal = new Date(y, m, d);
    } else if (dateString) {
      dateVal = new Date(dateString);
    } else {
      dateVal = new Date();
    }
  } catch(e) {
    dateVal = new Date();
  }
 
  return dateVal;
}


/**
Try to split a date string into an array of elements, using common date separators.
If the date is split, an array is returned; otherwise, we just return false.
*/
function splitDateString(dateString)
{
  var dArray;
  if (dateString.indexOf("/") >= 0)
    dArray = dateString.split("/");
  else if (dateString.indexOf(".") >= 0)
    dArray = dateString.split(".");
  else if (dateString.indexOf("-") >= 0)
    dArray = dateString.split("-");
  else if (dateString.indexOf("\\") >= 0)
    dArray = dateString.split("\\");
  else
    dArray = false;
 
  return dArray;
}

/**
Update the field with the given dateFieldName with the dateString that has been passed,
and hide the datepicker. If no dateString is passed, just close the datepicker without
changing the field value.

Also, if the page developer has defined a function called datePickerClosed anywhere on
the page or in an imported library, we will attempt to run that function with the updated
field as a parameter. This can be used for such things as date validation, setting default
values for related fields, etc. For example, you might have a function like this to validate
a start date field:

function datePickerClosed(dateField)
{
  var dateObj = getFieldDate(dateField.value);
  var today = new Date();
  today = new Date(today.getFullYear(), today.getMonth(), today.getDate());
 
  if (dateField.name == "StartDate") {
    if (dateObj < today) {
      // if the date is before today, alert the user and display the datepicker again
      alert("Please enter a date that is today or later");
      dateField.value = "";
      document.getElementById(datePickerDivID).style.visibility = "visible";
      adjustiFrame();
    } else {
      // if the date is okay, set the EndDate field to 7 days after the StartDate
      dateObj.setTime(dateObj.getTime() + (7 * 24 * 60 * 60 * 1000));
      var endDateField = document.getElementsByName ("EndDate").item(0);
      endDateField.value = getDateString(dateObj);
    }
  }
}

*/
function updateDateField(dateFieldName, dateString)
{
  var targetDateField = document.getElementsByName (dateFieldName).item(0);
  if (dateString)
    targetDateField.value = dateString;
 
  var pickerDiv = document.getElementById(datePickerDivID);
  pickerDiv.style.visibility = "hidden";
  pickerDiv.style.display = "none";
 
  adjustiFrame();
  targetDateField.focus();
 
  // after the datepicker has closed, optionally run a user-defined function called
  // datePickerClosed, passing the field that was just updated as a parameter
  // (note that this will only run if the user actually selected a date from the datepicker)
  if ((dateString) && (typeof(datePickerClosed) == "function"))
    datePickerClosed(targetDateField);
}


/**
Use an "iFrame shim" to deal with problems where the datepicker shows up behind
selection list elements, if they're below the datepicker. The problem and solution are
described at:

http://dotnetjunkies.com/WebLog/jking/archive/2003/07/21/488.aspx
http://dotnetjunkies.com/WebLog/jking/archive/2003/10/30/2975.aspx
*/
function adjustiFrame(pickerDiv, iFrameDiv)
{
  // we know that Opera doesn't like something about this, so if we
  // think we're using Opera, don't even try
  var is_opera = (navigator.userAgent.toLowerCase().indexOf("opera") != -1);
  if (is_opera)
    return;
  
  // put a try/catch block around the whole thing, just in case
  try {
    if (!document.getElementById(iFrameDivID)) {
      // don't use innerHTML to update the body, because it can cause global variables
      // that are currently pointing to objects on the page to have bad references
      //document.body.innerHTML += "<iframe id='" + iFrameDivID + "' src='javascript:false;' scrolling='no' frameborder='0'>";
      var newNode = document.createElement("iFrame");
      newNode.setAttribute("id", iFrameDivID);
      newNode.setAttribute("src", "javascript:false;");
      newNode.setAttribute("scrolling", "no");
      newNode.setAttribute ("frameborder", "0");
      document.body.appendChild(newNode);
    }
    
    if (!pickerDiv)
      pickerDiv = document.getElementById(datePickerDivID);
    if (!iFrameDiv)
      iFrameDiv = document.getElementById(iFrameDivID);
    
    try {
      iFrameDiv.style.position = "absolute";
      iFrameDiv.style.width = pickerDiv.offsetWidth;
      iFrameDiv.style.height = pickerDiv.offsetHeight ;
      iFrameDiv.style.top = pickerDiv.style.top;
      iFrameDiv.style.left = pickerDiv.style.left;
      iFrameDiv.style.zIndex = pickerDiv.style.zIndex - 1;
      iFrameDiv.style.visibility = pickerDiv.style.visibility ;
      iFrameDiv.style.display = pickerDiv.style.display;
    } catch(e) {
    }
 
  } catch (ee) {
  }
 
}

function refreshRefillWizard(element, elementLabel, step) {
	  checkDrugValidity(elementLabel, element);
	  gatherOriginalPrescriptionInfo(elementLabel, element);
	  refreshWizard(element,elementLabel,step);
}

/*Custom code for patient popup (from tolvenwiz.js)*/
function openPatientPopup(contentName, placeholderid, methodName, formId, index,gridType) {
	var lform = $(formId);
	stopAsync(formId);
	var lArguments = new Array();
  	lArguments.push(formId);
  	lArguments.push(index);
  	var methodArgsStr = buildArguments(lArguments);
  	openPopupForPatient( contentName, placeholderid, formId,  methodName, methodArgsStr,gridType);
}
function openPopupForPatient( contentName, placeholderid, formId, methodName, methodArgs,gridType){
	serialNo++;
	$('downloadStatus').innerHTML="Get " + contentName + "...";
	new Ajax.Request(
		  'createGrid.ajaxf',
		  {
			method: 'get',
			parameters: "element="+contentName+"&gridId="+placeholderid+"&gridType="+gridType+"&methodArgs="+methodArgs+"&methodName="+methodName+"&formId="+formId,
			onSuccess: function(request){ setPopupContentForPatient(request,  placeholderid, formId ); },
			onFailure: function(request) {displayError(request,param);}
		  });
}

function setPopupContentForPatient( req, placeholderid, formId){
	var prefHTML = "";
	prefHTML += "<div class=\"popupgridheader\">";
	prefHTML += "<img class='closetab' src='../images/x_black.gif' onclick=\"closePopup('" + $(placeholderid).id + "','" + formId + "' );return false;\"/>&nbsp;" ;
	prefHTML += "</div>";
	prefHTML += req.responseText;
	// This is required for Grid to filter and refresh data.
	visiblePage = placeholderid;
	var popupElement = $(placeholderid);
	$(popupElement).update(prefHTML);
	popupElement.style.display = 'block';
	popupElement.style.top = document.body.clientHeight * .60 + "px"; //to move the popup to a specific location.
	popupElement.style.left = document.body.clientWidth * .30 + "px";
}

/* ************************************************************************************************************************************************/

/**
 * Function to display pharmacy template - for pharmacy popup
 * 
 * added on 05/Dec/2010 
 * @author Suja Sundaresan
 * @param path
 */
showPharmacies = function(path) {
	new Ajax.Updater('echr:pharmacies', '/Tolven/wizard/_pharmacyTemplate.jsf', {
		parameters: { element: path }, evalScripts:true, onComplete:function(req) {
			$('echr:pharmacies').style.display="block";
    	}
	});
}

liveGrids = {};
/**
 * Function to user to create pharmacy grid
 * 
 * added on 05/Dec/2010 
 * @author Suja Sundaresan
 * @param menuPath
 * @param id
 * @param methodName
 * @param methodArgs
 * @param ajaxUrl
 */
createPharmacyGrid = function( menuPath,id, methodName, methodArgs, ajaxUrl, curElement) {
	if(!id)
		id = menuPath;
	var root = $(id);
	var grid = $(id+'-grid');
	if(!curElement)
		curElement = menuPath;
	if (root.getAttribute( 'gridOffset' )==null) {
		root.setAttribute( 'gridOffset', grid.getAttribute( 'gridOffset'));
		root.setAttribute( 'gridSortCol', grid.getAttribute( 'gridSortCol'));
		root.setAttribute( 'gridSortDir', grid.getAttribute( 'gridSortDir'));
		root.setAttribute( 'filterPHValue', "");
		root.setAttribute( 'menuPath', menuPath);
		root.setAttribute( 'element', curElement);
	}

	var methodNameValue = methodName;
	var methodArgsValue = methodArgs;

	if (methodName == undefined) {
		methodNameValue = "";
	}

	if (methodArgs == undefined) {
		methodArgsValue = "";
	}

	if (ajaxUrl==null)
		ajaxUrl = 'pharmacyData.ajaxsure';

	liveGrids[id] = new Rico.LiveGrid( id+"-LG",
		1*grid.getAttribute('visibleRows'),
		1*grid.getAttribute('totalRows'),
		ajaxUrl,
		{	prefetchBuffer: true,
			tableClass: 'gridBody',
			loadingClass: 'gridLoading',
            scrollerBorderRight: '0px solid #FF0000',
			offset: 1*root.getAttribute('gridOffset'),
			sortCol: root.getAttribute('gridSortCol'),
			sortDir: root.getAttribute('gridSortDir'),
			largeBufferSize: 5.0,
			nearLimitFactor: 0.4,
			rootId: id,
			onscroll : updateOffset,
			onRefreshComplete: updateSortInfo,
			requestParameters: [{name: 'element', value: root.getAttribute('element')},
								{name: 'filter', value: root.getAttribute('filterPHValue')},
								{name: 'methodName', value: methodNameValue},
								{name: 'methodArgs', value: methodArgsValue },
								{name: 'path', value: root.getAttribute('menuPath') }],
			sortAscendImg: '../images/sort_asc.gif',
			sortDescendImg:'../images/sort_desc.gif'
		});

	grid.style.width=($(id+'-LG_header').offsetWidth+19)*2+'px';
	grid.style.width=($(id+'-LG_header').offsetWidth+19)+'px';
	grid.style.border='#999999 solid 1px';
	$(id+"-filter").value=root.getAttribute('filterPHValue');
	new Form.Element.Observer( $(id+"-filter"), 1, function(element, val) {
		checkPharmacyInput(element, val, id, methodNameValue, methodArgsValue,menuPath, ajaxUrl);
	} );
	if( $(id).getAttribute('filterPHValue') != null && $(id).getAttribute('filterPHValue') != "" ) {
	  pharmacyFilterValueChange($(id + "-filter"), $(id).getAttribute('filterPHValue'), id, methodNameValue, methodArgsValue ,menuPath );
	}

	// Move Focus to Filter textbox.
	var filterBoxId = id + "-filter";
	setFocus(filterBoxId );
	
	//reset the popup grid width after the grid is created
	//this fix is needed only for IE. ok to keep for FF too.
	if($(id).className=="popupgrid"){
		$(id).style.width = $(id+"-grid").getWidth();
	}
}

/**
 * Function to user to check pharmacy input
 * 
 * added on 05/Dec/2010 
 * @author Suja Sundaresan
 * @param element
 * @param value
 * @param id
 * @param methodName
 * @param methodArgs
 * @param menuPath
 * @param ajaxUrl
 */
function cphi(element, value, id, methodName, methodArgs, menuPath, ajaxUrl){
	if( $( id + "-filter").value == value ){
		pharmacyFilterValueChange(element, value, id, methodName, methodArgs, menuPath, ajaxUrl);
	} else {
		$(id).setAttribute('filterPHValue', $( id + "-filter").value );
	}
}

/**
 * Function to user call cphi function at regular interval
 * 
 * added on 05/Dec/2010 
 * @author Suja Sundaresan
 * @param element
 * @param value
 * @param id
 * @param methodName
 * @param methodArgs
 * @param menuPath
 * @param ajaxUrl
 */
function checkPharmacyInput(element, value, id , methodName, methodArgs,menuPath, ajaxUrl){
	$(id).setAttribute('filterPHValue', value );
	setTimeout( "cphi('" + element + "', '" + value + "', '" + id+  "', '" + methodName + "', '" +  methodArgs + "','" + menuPath + "', '" + ajaxUrl + "')", 125);
}

/**
 * This function caluculate pharamcy count on filter value change
 * 
 * added on 05/Dec/2010 
 * @author Suja Sundaresan
 * @param element
 * @param value
 * @param id
 * @param methodName
 * @param methodArgs
 * @param menuPath
 * @param ajaxUrl
 */
function pharmacyFilterValueChange(element, val, id, methodName, methodArgs,menuPath, ajaxUrl) {
	if(!menuPath)
		menuPath = $(id).getAttribute('menuPath');

	var lg = liveGrids[id];
	var requestParams = new Array();
	$(id).setAttribute('filterPHValue', val );
	lg.setRequestParams( {name: 'element', value: $(id).getAttribute('element')}, {name: 'path', value: menuPath}, {name: 'filter', value: val}, {name: 'methodName', value: methodName}, {name: 'methodArgs', value: methodArgs} );
	if (val) {
		if (ajaxUrl=='preferredPharmacyData.ajaxsure')
			ajaxUrl = 'countPreferredPharmacy.ajaxsure';
		else
			ajaxUrl = 'countPharmacy.ajaxsure';

		var countMDAjax = new Ajax.Request(
			ajaxUrl,
			{
				method: 'get',
				parameters: 'element='+menuPath+'&filter='+val,
				onSuccess: function(req) {countPHComplete( id, req );}
			});
	} else {
		$(id+"-foot").innerHTML = "" ;
		var grid = $(id+'-grid');
		lg.setTotalRows( 1*grid.getAttribute('totalRows') );
		lg.requestContentRefresh(0);
	}
}

/**
 * This function display filtered items count on the footer section
 * 
 * added on 05/Dec/2010 
 * @author Suja Sundaresan
 * @param rootId
 * @param request
 */
function countPHComplete( rootId, request ) {
	$(rootId+"-foot").innerHTML = " / " + request.responseText + " filtered items" ;
	var lg = liveGrids[rootId];
	lg.setTotalRows( 1*request.responseText );
	lg.requestContentRefresh(0);
}

/**
 * Inserts the pahramcy details into the div specified.
 */	
function insertPharmacyDetails(ncpdpId, element, divId, fromPopup) {
	new Ajax.Updater(divId, 'insertPharmacyData.ajaxsure',
		{
			method: 'get',
			parameters: "ncpdpId="+ncpdpId+"&element="+element+"&divId="+divId,
				onComplete: function(response) {
					closePopup('echr:pharmacies',element.replace(':','').replace('-','').replace(':','').replace('-','') );
					if (document.getElementById('pharmacyPopup').innerHTML == "Add Pharmacy") {
						$("pharmacyPopup").innerHTML = "Edit Pharmacy";
						
					}
					if (fromPopup == '1') {
						document.getElementById('pharmacyWizClosed').innerHTML = "";
					}
				}
			});
}

/**
 * Sets the service level description in the physician personal page.
 * @param serviceCode
 * @param divId
 * @return
 */
function calculateServiceCode(serviceCode, divId) {
	if (serviceCode != "" && serviceCode != '0') {
		var html = "";
		if ((parseInt(serviceCode) & 1) == 1) {
			html = (html=="" ? "" : html + " | ") + "NEWRx"; 
		}
		if ((parseInt(serviceCode) & 2) == 2) {
			html = (html=="" ? "" : html + " | ") + "REFREQ/REFRES"; 
		}
		if ((parseInt(serviceCode) & 4) == 4) {
			html = (html=="" ? "" : html + " | ") + "RxCHG/CHGRES";
		}
		if ((parseInt(serviceCode) & 8) == 8) {
			html = (html=="" ? "" : html + " | ") + "RxFILL";
		}
		if ((parseInt(serviceCode) & 16) == 16) {
			html = (html=="" ? "" : html + " | ") + "CANRx/CANRxRES";
		}
		if ((parseInt(serviceCode) & 32) == 32) {
			html = (html=="" ? "" : html + " | ") + "MedHistory";
		}
		if ((parseInt(serviceCode) & 64) == 64) {
			html = (html=="" ? "" : html + " | ") + "Eligibility";
		}
		$(divId).innerHTML = html;
	}
}

/**
 * Sets the prior service level check boxes.
 * @param root
 * @param serviceCode
 * @return
 */
function checkServiceLevel(root, serviceCode) {
	
	var supportedSL;
	new Ajax.Request('getSupportedServiceLevel.ajaxerx',
			{
				method: 'get',
				onComplete: function (response) {
					supportedSL = response.responseText;
					if ((parseInt(supportedSL) & 1) != 1) {
						$(root+':serviceLevel:0').disabled = true;
					} else if (serviceCode != "" && serviceCode != '0' && (parseInt(serviceCode) & 1) == 1) {
						$(root+':serviceLevel:0').checked="checked";
					}
					if ((parseInt(supportedSL) & 2) != 2) {
						$(root+':serviceLevel:1').disabled = true;
					} else if (serviceCode != "" && serviceCode != '0' && (parseInt(serviceCode) & 2) == 2) {
						$(root+':serviceLevel:1').checked="checked";
					}
					if ((parseInt(supportedSL) & 4) != 4) {
						$(root+':serviceLevel:2').disabled = true;
					} else if (serviceCode != "" && serviceCode != '0' && (parseInt(serviceCode) & 4) == 4) {
						$(root+':serviceLevel:2').checked="checked";
					}
					if ((parseInt(supportedSL) & 8) != 8) {
						$(root+':serviceLevel:3').disabled = true;
					} else if (serviceCode != "" && serviceCode != '0' && (parseInt(serviceCode) & 8) == 8) {
						$(root+':serviceLevel:3').checked="checked";
					}
					if ((parseInt(supportedSL) & 16) != 16) {
						$(root+':serviceLevel:4').disabled = true;
					} else if (serviceCode != "" && serviceCode != '0' && (parseInt(serviceCode) & 16) == 16) {
						$(root+':serviceLevel:4').checked="checked";
					}
					if ((parseInt(supportedSL) & 32) != 32) {
						$(root+':serviceLevel:5').disabled = true;
					} else if (serviceCode != "" && serviceCode != '0' && (parseInt(serviceCode) & 32) == 32) {
						$(root+':serviceLevel:5').checked="checked";
					}
					if ((parseInt(supportedSL) & 64) != 64) {
						$(root+':serviceLevel:6').disabled = true;
					} else if (serviceCode != "" && serviceCode != '0' && (parseInt(serviceCode) & 64) == 64) {
						$(root+':serviceLevel:6').checked="checked";
					}
				},
				onFailure: function (response) {
					
				}
			});
}

function disablePrescriber(templateId, context, source) {
	var choice = confirm("Do you want to disable the physician?");
	if (choice){
		new Ajax.Request('disablePhysician.ajaxerx',
				{
				method: 'get',
				parameters: "templateId="+templateId+"&context="+context,
				onSuccess:function(response) {
					alert("Physician disabled succssfully.");
				},
				onFailure: function(reponse) {
					alert("Physician disabling Failed. Please try again.");
				}
				});
	}
}

function enableNotes(elementLabel, reference) {
	if ($("isScheduleDrug") != null && $("isScheduleDrug").value == "Yes") {
		if ((elementLabel+":responseField:1") == reference.id) {
			if ($(elementLabel+":noteInputText").value == "Schedule III to V:The approved controlled substance Rx will be faxed."
				|| $(elementLabel+":noteInputText").value == "") {
				$(elementLabel+":noteInputText").value = "A New prescription is being sent to your pharmacy.";
				$(elementLabel+":noteInputText").readOnly = false;
				$(elementLabel+":commentsLeft").style.display = "block";
			}
		} else if((elementLabel+":responseField:0") == reference.id){
			$(elementLabel+":noteInputText").value = "Schedule III to V:The approved controlled substance Rx will be faxed.";
			$(elementLabel+":noteInputText").readOnly = true;
			$(elementLabel+":commentsLeft").style.display = "none";
		}
	}
}

/**
 * On Active Medication 'Revise' action - 
 * setting the current trim as nullified and 
 * created a new trim with active status. 
 */
function reviseActiveMedication(element){
	var arr = element.split(":");
	var myAjax = new Ajax.Request(
			   'reviseActiveMedication.ajaxerx',
			   {
			    method: 'get',
			    parameters: 'element='+arr[0]+':'+arr[1]+':'+arr[2]+'&action=eie',
			    onFailure: function(request) {displayError(request,element.split());},
			    onSuccess: function(request) {reviseActiveMedicationDone(request,element);}
			   });
}

function reviseActiveMedicationDone(request,element){
	closeTab(element.replace(':medicationDetails',''));
	oneSecondShow = request.responseText;
}

/**
 * Validates pharmacy. 
 * @param elementLabel
 * @return
 */
function validatePharmacy(elementLabel) {
	if ($(elementLabel+":dispenseFromOfficeField:1").checked == true && $("NCPDPID") != null) {
		$(elementLabel+":chosedPharm").value = $("NCPDPID").value;
	} else {
		$(elementLabel+":chosedPharm").value = "dummy";
		$(elementLabel+":referringPharm").value = "";
	}
}

/**
 * Displays the pharmacy drop down. 
 */
showPharmacyDropDown=function(path){
    new Ajax.Updater('showPharmacyDropDownDiv', '../wizard/dropDownPharmacyTemplate.jsf', {
           parameters: { element: path }, onComplete:function(req) {wizHideAjaxLoader();}
    });
}

/**
 * Removes the drug from the Drug Formulary list.
 * @param element
 * @return
 */
function removeDrugFormulary(element) {
	if (confirm("Remove from Forumulary List ?")) {
	  var myAjax = new Ajax.Request(
	    'wizNullify.ajaxi',
	    {
	    	method: 'get',
		    parameters: 'element='+element+'&action=nullifiedActive',
		    onFailure: function(request) {
	    		displayError(request,element);
	    	},
		    onSuccess: function(request) {
	    		wizNullifyDone(request,element);
	    	}
	    });
	}
}
