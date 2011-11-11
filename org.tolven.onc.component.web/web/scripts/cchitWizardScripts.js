/**
 * To set or unset option or checkbox control.
 *
 * Author Valsaraj
 * Added on 05/13/2010 
 */
function preloadCheckValues(root, id, value) {
	var elem = $(root + ':' + id);

	if (value=='Yes') {
		elem.checked=true;
	}
	else if (value=='No') {
		elem.checked=false;
	}
}

/**
 * To disable non editable fields in PLD.
 *
 * Author Valsaraj
 * Added on 06/07/2010 
 */
function disableNonEditableFields(edit, root) {
	if (edit!= null && edit!="") {
		$(root + ':listName').disabled=true;
		$(root + ':nameDisplay').disabled=true;
		$(root + ':ageDisplay').disabled=true;
		$(root + ':raceDisplay').disabled=true;
		$(root + ':genderDisplay').disabled=true;
		$(root + ':zipDisplay').disabled=true;
		$(root + ':nameColumn').disabled=true;
		$(root + ':ageColumn').disabled=true;
		$(root + ':raceColumn').disabled=true;
		$(root + ':genderColumn').disabled=true;
		$(root + ':zipColumn').disabled=true;
	}
}

/**
 * Methods added for lab tests
 *
 * Author Kanagaraj Kuttiannan
 * Added on 07/20/2011
 * 
 */

function showSelection(element)
{
	$('labTestValueSelectionDiv').style.display='block';
	$(element).style.display='none';
	$('labTestValueSelectionDiv').style.top = document.body.clientHeight * .50 + "px";
 	$('labTestValueSelectionDiv').style.left = document.body.clientWidth * .30 + "px";
}

 function showPopupDiv()
{
	$('labTestValueSelectionDiv').style.display='none';
	$($('tempElement').value).style.display='block';
	$('tempTemplateName').value='';
	$('tempElement').value='';
	$('tempMethodArgs').value='';
}

function checkBetweenSelected()
{
	if($('labTestOperator').value=='bet')
	{
		$('endValDiv').style.display='inline';
	}
	else
	{
		$('endValDiv').style.display='none';
		$('endVal').value='';
	}
	
}

function addLabTestValue(templateName, element, methodArgs)
{
	$('tempTemplateName').value=templateName;
	$('tempElement').value=element;
	$('tempMethodArgs').value=methodArgs;
	showSelection(element);
}

function addLabTest()
{
	value = $('tempTemplateName').value.split("/");
	args = $('tempMethodArgs').value.split("|");
	
	if($('labTestOperator').value=='bet')
	{

		if($('startVal').value=="" || $('endVal').value=="")
		{
		  alert('Please enter a value range'); 
		  return;
		}
		else if (isNaN($('startVal').value) || isNaN($('endVal').value) )
		{
		  alert('Invalid Number. Please enter only numberic value'); 
		  return;
		}

		valueWithOperator=value[1].split("-")[1]+':'+$('labTestOperator').value+':'+$('startVal').value-$('endVal').value;
	}
	else
	{
		if($('startVal').value=="")
		{
		  alert('Please enter a value'); 
		  return;
		}
		else if ($('labTestOperator').value!='bet' && (isNaN($('startVal').value) ))
		{
		  alert('Invalid Number. Please enter only numberic value'); 
		  return;
		}
		valueWithOperator=value[1].split("-")[1]+':'+$('labTestOperator').value+':'+$('startVal').value
	}
	
	if ("0"==args[args.length-1]) {
	
		labtestsIncludeCodeObj = $(args[0] + ':labtestsIncludeCode');
		tempValue = labtestsIncludeCodeObj.value;
		labtestsIncludeCodeObj.value = tempValue=="" ? valueWithOperator : tempValue + "," + valueWithOperator;
		$(args[0] + ':labtestsInclude').checked = true;
	}
	else if ("1"==args[args.length-1]) {
		labtestsExcludeCodeObj = $(args[0] + ':labtestsExcludeCode');
		tempValue = labtestsExcludeCodeObj.value;
		labtestsExcludeCodeObj.value = tempValue=="" ? valueWithOperator : tempValue + "," + valueWithOperator;
		$(args[0] + ':labtestsInclude').checked = true;
	}

	$('labTestValueSelectionDiv').style.display='none'

}





/**
 * To select values of clinical findings.
 *
 * Author Valsaraj
 * Added on 05/28/2010
 * Modified on 29/Dec/2010
 * Modified by Khalid
 */
function addValue(templateName, element, methodArgs) {
	value = templateName.split("/");
	args = methodArgs.split("|");
	
	if ("0"==args[args.length-1]) {
		if ("diagnosis"==value[0]) {
			diagnosisIncludeCodeObj = $(args[0] + ':diagnosisIncludeCode');	
			tempValue = trim(diagnosisIncludeCodeObj.value);	
			diagnosisIncludeCodeObj.value = tempValue=="" ?  value[1].split("-")[2] : tempValue + "," + value[1].split("-")[2];
			$(args[0] + ':diagnosisInclude').checked = true;
		}
		else if ("problem"==value[0]) {
			problemIncludeCodeObj = $(args[0] + ':problemIncludeCode');
			tempValue = trim(problemIncludeCodeObj.value);
			problemIncludeCodeObj.value = tempValue=="" ? value[1].split("-")[1] : tempValue + "," + value[1].split("-")[1];
			$(args[0] + ':problemInclude').checked = true;
		}
		else if ("medication"==value[0]) {
			medicationsIncludeCodeObj = $(args[0] + ':medicationsIncludeCode');
			tempValue = trim(medicationsIncludeCodeObj.value);
			medicationsIncludeCodeObj.value = tempValue=="" ? value[1].split("-")[1] : tempValue + "," + value[1].split("-")[1];
			$(args[0] + ':medicationsInclude').checked = true;
		}
		else if ("allergy"==value[0]) {
			allergiesIncludeCodeObj = $(args[0] + ':allergiesIncludeCode');
			tempValue = allergiesIncludeCodeObj.value;
			allergiesIncludeCodeObj.value = tempValue=="" ? value[1] : tempValue + "," + value[1];
			$(args[0] + ':allergiesInclude').checked = true;
		}
		else if ("px"==value[0]) {
			proceduresIncludeCodeObj = $(args[0] + ':proceduresIncludeCode');
			tempValue = proceduresIncludeCodeObj.value;
			proceduresIncludeCodeObj.value = tempValue=="" ? value[1].split("-")[2] : tempValue + "," + value[1].split("-")[2];
			$(args[0] + ':proceduresInclude').checked = true;
		}
		
	}
	else if ("1"==args[args.length-1]) {
		if ("diagnosis"==value[0]) {
			diagnosisExcludeCodeObj = $(args[0] + ':diagnosisExcludeCode');			
			tempValue = diagnosisExcludeCodeObj.value;
			diagnosisExcludeCodeObj.value = tempValue=="" ?  value[1].split("-")[2] : tempValue + "," + value[1].split("-")[2];
			$(args[0] + ':diagnosisExclude').checked = true;	
		}
		else if ("problem"==value[0]) {
			problemExcludeCodeObj = $(args[0] + ':problemExcludeCode');
			tempValue = problemExcludeCodeObj.value;
			problemExcludeCodeObj.value = tempValue=="" ? value[1].split("-")[1] : tempValue + "," + value[1].split("-")[1];
			$(args[0] + ':problemExclude').checked = true;
		}
		else if ("medication"==value[0]) {
			medicationsExcludeCodeObj = $(args[0] + ':medicationsExcludeCode');
			tempValue = medicationsExcludeCodeObj.value;
			medicationsExcludeCodeObj.value = tempValue=="" ? value[1].split("-")[1] : tempValue + "," + value[1].split("-")[1];
			$(args[0] + ':medicationsExclude').checked = true;
		}
		else if ("allergy"==value[0]) {
			allergiesExcludeCodeObj = $(args[0] + ':allergiesExcludeCode');
			tempValue = allergiesExcludeCodeObj.value;
			allergiesExcludeCodeObj.value = tempValue=="" ? value[1] : tempValue + "," + value[1];
			$(args[0] + ':allergiesExclude').checked = true;
		}
		else if ("px"==value[0]) {
			proceduresExcludeCodeObj = $(args[0] + ':proceduresExcludeCode');
			tempValue = proceduresExcludeCodeObj.value;
			proceduresExcludeCodeObj.value = tempValue=="" ? value[1].split("-")[2] : tempValue + "," + value[1].split("-")[2];
			$(args[0] + ':proceduresExclude').checked = true;
		}
		
	}
	else if ("2"==args[args.length-1]) {
		if ("diagnosis"==value[0]) {
		var param = "";
		param = '&template='+templateName;	
		var instAjax = new Ajax.Request(
	        'getDiagnosisName.ajaxcchit', {
	            method: 'get', 
	            parameters: param,
	            onComplete: function(req) {
	        	
	                if (req.responseText.split("|")[0]=="Success") {
	                	var diagnosisName = req.responseText.split("|")[1];
	                	diagnosisNameObj = $(args[0] + ':deathDiagnosisName');	
	        			diagnosisCodeObj = $(args[0] + ':deathDiagnosisCode');
	        			tempValue = trim(diagnosisCodeObj.value);	
	        			diagnosisCodeObj.value = tempValue=="" ?  value[1].split("-")[2] : value[1].split("-")[2];
	        			diagnosisNameObj.value = diagnosisName;
	                 }
	            } 
			});
		}
	}
	
	closeDiv(element);
}

/**
 * To trim characters.
 *
 * Author Valsaraj
 * Added on 05/28/2010 
 */
function trim(str, chars) {
	return ltrim(rtrim(str, chars), chars);
}
 
/**
 * To left trim characters.
 *
 * Author Valsaraj
 * Added on 05/28/2010 
 */
function ltrim(str, chars) {
	chars = chars || "\\s";
	return str.replace(new RegExp("^[" + chars + "]+", "g"), "");
}

/**
 * To right trim characters.
 *
 * Author Valsaraj
 * Added on 05/28/2010 
 */
function rtrim(str, chars) {
	chars = chars || "\\s";
	return str.replace(new RegExp("[" + chars + "]+$", "g"), "");
}

/**
 * To clear checkbox if clinical finding code is removed from input box of PLD.
 *
 * Author Valsaraj
 * Added on 06/07/2010 
 */
function clearCheckboxIfNull(elem, root, check) {
	$(root+':'+check).checked= (elem.value=="" ? false : true);
}

/**
* To enable/disable 'create PHR' option for component S & T,
* if 'primary email' is valid in 'Patient' TRIM.
*
* Author Valsaraj
* Added on 06/15/2010 
*/
function enablePHRCreationAndSharing(element) {
	var primaryEmailObj = $(element+':primaryEmailId');
	
	if (primaryEmailObj!=null && primaryEmailObj.value!='' && validateEmail(primaryEmailObj.value)!=-1) {
		var myAjax = new Ajax.Request(
		    'enablePHRCreationAndSharing.ajaxcchit',
		    {
		     	method: 'get',
		     	parameters: 'element='+element+'&email='+primaryEmailObj.value,
				onComplete: function(req) {	
					if (req.responseText=="") {
						$('createPHRContainer').style.display='block';					
					}
					else {
						$('createPHRContainer').style.display='none';					
						$(element+':createPHR').checked=false;
						$(element+':pinNumber').disabled=true;
						$('pinContainer').style.display='none';
						$('shareDataContainer').style.display='block';					
					}
					
					showHidePIN(element);
					showHideShareOption(element);
				}
		    }
		);
	}
	else if ($(element+':createPHR') != null && $('createPHRContainer') != null) {
		$(element+':createPHR').checked=false;
		$('createPHRContainer').style.display='none';
		showHidePIN(element);
		showHideShareOption(element);
	}
}

/**
* To show/hide PIN input field if 'create PHR' option is set for component S & T.
*
* Author Valsaraj
* Added on 06/15/2010 
*/
function showHidePIN(root) {
	if ($(root+':createPHR').checked==true && $('createPHRContainer').style.display!='none') {
		$(root+':pinNumber').disabled=false;
		$('pinContainer').style.display='block';
	}
	else {
		$(root+':pinNumber').disabled=true;
		$('pinContainer').style.display='none';
	}
}

/**
* To show/hide share checkbob field if 'create PHR' option is set for component S & T.
*
* Author Valsaraj
* Added on 06/16/2010 
*/
function showHideShareOption(root) {
	if ($(root+':tolvenID').value!='' || $(root+':createPHR').checked==true) {
		$('shareDataContainer').style.display='block';
	}
	else {
		$('shareDataContainer').style.display='none';
	}
}

/**
* Validates email.
*/
function validateEmail(email) {
	var reg = /^([A-Za-z0-9_\-\.])+\@([A-Za-z0-9_\-\.])+\.([A-Za-z]{2,4})$/;
	
	return email.search(reg);
}

/**
* Validates email and displays error message when it is invalid.
*/
emailValid = function(root,id,errorBox) {
	var status = true;	
	var emailVal = $(root+':'+id).value;
	
	if ((emailVal!=null)&&(emailVal!="")) {
		if (validateEmail(emailVal) == -1)
	 		$(errorBox).style.display = 'block';
	 	else
	 		$(errorBox).style.display = 'none';
	 }
	 else
	 	$(errorBox).style.display = 'none';

	 if (($('errorBoxEmail').style.display == 'block')||($('errorBoxConEmail').style.display == 'block'))
	 	status = false;
	 else
	 	status = true;
	 	
	return status;	 
}

/**
* To anable/disable next and submit button.
*/  	
enableDisableNextAndSubmit = function(root, enFlag) {
	$(root + "nextButton").disabled = $(root + "submitButton").disabled = (enFlag ? false : true);
}
/**
 * Script to check the date for Procedures
 */
function validateCCHITDates(root, count, errorId, tsId) {
	
	var date2Obj = new Date();
	var j = 0;
	var date1Id = $(root+':Fieldques'+tsId+j);
	
	while(date1Id) {
		var date1Obj = new Date(date1Id.value);
		var warningText = errorId+j;
		
		if (date2Obj > date1Obj) {
			$(warningText).style.display = "block";
		} 
		else {
			$(warningText).style.display = "none";
		}
		
		j++;
		var date1Id = $(root+':Fieldques'+tsId+j);
	}
}

/**
 * Script to validate date for forms under CCHIT spec.
 * @param root
 * @param tsId
 * @param warningText
 * @return
 */
function validateCCHITSpecDate(root, tsId, warningText) {
	
	var currentDate = new Date();
	currentDate.setHours(0,0,0,0);
	
	var timeField = $(root+':Fieldques'+tsId);
	var formTime = new Date(timeField.value);
	
	if (formTime < currentDate ) {
		$(root+warningText).style.display = "block";
		$(root+'removeOrder').disabled = true;
		$(root+'nextButton').disabled = true;
		$(root+'submitButton').disabled = true;
	} 
	else {
		$(root+warningText).style.display = "none";
		$(root+'removeOrder').disabled = false;
		$(root+'nextButton').disabled = false;
		$(root+'submitButton').disabled = false;
	}
}

/**
 *  To make selectManyCheckbox component to behave as selectOneRadio component.
 */
wizSelectOneCheckbox=function(obj, root, componentName) {
	var checked=false
	
	if (obj.checked==true) {
		checked=true
	}
	
	var elem;
	var iter = 0;
	
	while (elem = $(root + ":" + componentName + ":" + iter)) {
		elem.checked = false;
		iter += 1;
	}
	obj.checked=checked
}

/**
 * To convert selected option in race menu to comma separated strings
 */
function wizConvertCEToString(root, originalText, menu) {
	var text="";
	var iter = 0;
	var elem;
	
	while (elem = $(root + ":" + menu + ":" + iter)) {
		if (elem.checked) {
			if (iter == 3) {
				text = text+(elem.value.split('|')[7])+', ';
			} 
			else {
				text = text+(elem.value.split('|')[5])+', ';
			}
		}
		iter++;
	}
	
	text=text.substring(0, text.length-2);
	$(root+':'+originalText).value=text;
}

function setMenus(root, menu, text) {
	var selectedValues = $(root+':'+text).value;
	
	if(selectedValues.length < 1) {
		return;
	}
	
	var values = selectedValues.split(", ");
	
	for (var i = 0; i < values.length; i++) {
		var iter = 0;
		
		while (elem = $(root + ":" + menu + ":" + iter)) {
			iter++;
			
			if ((elem.value).match(values[i])) {
				elem.checked = true;
				break;
			}
		}
	}
}

/**
 *  To set drop down component.
 */
function setDropDowns(root, menu, text) {
	var selectedValue = $(root+':'+text).value;
	
	if (selectedValue.length < 1) {
		return;
	} 
	else {
		selectedValue = selectedValue.split('|');
		selectedValue = (selectedValue.length > 4) ? selectedValue[5] : selectedValue[0];
	}
	
	var dropDown = $(root+':'+menu);
	
	for (var i = 0; i < dropDown.options.length; i++) {
		if (selectedValue.match(dropDown.options[i].value.split('|')[5])) {
			dropDown.options[i].selected = true;
			break;
		}
	}
}

function showHideDiv(root, checkbox, div) {
	if($(root+':'+checkbox)){
		if ($(root+':'+checkbox).checked==true) {
			$(root+div).style.display = 'block';
		} 
		else {
			$(root+div).style.display = 'none';
		}
	}
}
/**
 * Not used rt now.
 * */
function prePopulateValues(root, id) {
//	$('#{menu.elementLabel}:receivNotif:0').checked==true
	var receivNotification = $(root + ':receivNotif:0');
	var emailMode = $(root + ':notifMethds:0');
	var mailMode = $(root + ':notifMethds:1');
	var phrMode = $(root + ':notifMethds:2');
//	alert(receivNotification);
	if(receivNotification.checked == true && mailMode.checked==false && phrMode.checked==false){
//		alert("Success");
		$(root + ':notifMethds:0').checked = true;	
//		refreshWizard(id,root,'6')
	}
}
/**
 * Method to set the notification email id with primary email, if left empty.  
 */
function setEmail(root, formId, elemId){
	var element = formId;
	var elementId = elemId.id;
	var primaryEmail = $(root+":primaryEmailId").value;
	var notificationEmail = $(root+":emailAdd").value;
	var confirmEmail = $(root+":confEmailAdd").value;
	
	if (elementId == $(root+":receivNotif:0").id){	
		if (primaryEmail != "") {
			if (notificationEmail == "") {
				document.getElementById(root+":emailAdd").value = primaryEmail;
			}
			
			if (confirmEmail == "") {
				document.getElementById(root+":confEmailAdd").value = primaryEmail;
			}
		}
	}
	else if (elementId == $(root+":receivNotif:1").id) {
		
	}
}

/**
* To disable/enable an option in combo box when it is already selecteded/unselected.
*
* Author Valsaraj
* Added on 07/07/2010 
*/
function checkDuplicateAndDisable(root, select1, select2, select3, select4, select5) {
	select1Obj = $(root+":"+select1);
	select2Obj = $(root+":"+select2);
	select3Obj = $(root+":"+select3);
	select4Obj = $(root+":"+select4);
	select5Obj = $(root+":"+select5);
	idx1=select1Obj.selectedIndex;
	idx2=select2Obj.selectedIndex;
	idx3=select3Obj.selectedIndex;
	idx4=select4Obj.selectedIndex;
	idx5=select5Obj.selectedIndex;
	
	for (iter=1; iter<6; iter+=1) {
		if (idx1!=iter && idx2!=iter && idx3!=iter && idx4!=iter && idx5!=iter) {
			select1Obj.options[iter].disabled=false;
			select2Obj.options[iter].disabled=false;
			select3Obj.options[iter].disabled=false;
			select4Obj.options[iter].disabled=false;
			select5Obj.options[iter].disabled=false;
		}
	}
	
	if (idx1>0) {
		select2Obj.options[idx1].disabled=true;
		select3Obj.options[idx1].disabled=true;
		select4Obj.options[idx1].disabled=true;
		select5Obj.options[idx1].disabled=true;
	}
	
	if (idx2>0) {
		select1Obj.options[idx2].disabled=true;
		select3Obj.options[idx2].disabled=true;
		select4Obj.options[idx2].disabled=true;
		select5Obj.options[idx2].disabled=true;
	}
	
	if (idx3>0) {
		select1Obj.options[idx3].disabled=true;
		select2Obj.options[idx3].disabled=true;
		select4Obj.options[idx3].disabled=true;
		select5Obj.options[idx3].disabled=true;
	}
	
	if (idx4>0) {
		select1Obj.options[idx4].disabled=true;
		select2Obj.options[idx4].disabled=true;
		select3Obj.options[idx4].disabled=true;
		select5Obj.options[idx4].disabled=true;
	}
		
	if (idx5>0) {
		select1Obj.options[idx5].disabled=true;
		select2Obj.options[idx5].disabled=true;
		select3Obj.options[idx5].disabled=true;
		select4Obj.options[idx5].disabled=true;
	}
}

/**
* To set/unset when a combo box value is changed.
*
* Author Valsaraj
* Added on 07/07/2010 
*/
function autoCheckbox(obj, root, check) {
	if (obj.selectedIndex>0) {
		$(root+':'+check).checked=true;
		$(root+':'+check+'Val').value='Yes';
	}
	else {
		$(root+':'+check).checked=false;
		$(root+':'+check+'Val').value='No';
	}
}

/**
* To set another field with value from a select field.
*
* Author Valsaraj
* Added on 07/28/2010 
*/
function setHiddenFieldFromCombo(obj, root, targetField) {
	if (obj.selectedIndex>0) {
		$(root+':'+targetField).value=obj.options[obj.selectedIndex].text;
	}
}

/**
* To set another field with value from a select field.
*
* Author Valsaraj
* Added on 07/28/2010 
*/
function setHiddenFieldFromCheckbox(obj, root, targetField) {
	$(root+':'+targetField).value=obj.checked!=false?'Yes':'No';
}

/**
* To validate special characters.
*
* Author Valsaraj
* Added on 07/08/2010 
*/
function validateSpecialChars(root, id, errorMsg) {
	var strData = $(root+':'+id).value;	
	var errorObj = $(errorMsg);
	var iCount, iDataLen;
	var strCompare = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789 ";
	iDataLen = strData.length;
	
	if (iDataLen > 0) {	
		for (iCount=0; iCount < iDataLen; iCount+=1) {
			var cData = strData.charAt(iCount);
		
			if (strCompare.indexOf(cData) < 0 ){
				errorObj.innerHTML="Character "+cData+" not allowed.";				
				return false;
			} 
		}
		
		errorObj.innerHTML="";
		return true;
	}
	else {
		errorObj.innerHTML="";
	}
}

/**
* To validate length.
*
* Author Valsaraj
* Added on 07/27/2010 
*/
function validateLen(root, id, errorMsg, maxLen, minLen) {
	var strData = $(root+':'+id).value;	
	$(root+':'+id).value = strData = trim(strData);
	var errorObj = $(errorMsg);
	var errorFlag = false;
	var iDataLen;
	iDataLen = strData.length;
	
	if (maxLen != null) {
		if (iDataLen > maxLen) {
			errorObj.innerHTML="Maximum length is "+maxLen+".";
			errorFlag = true;
		}
	}
	
	if (minLen != null) {
		if (iDataLen < minLen) {	
			errorObj.innerHTML="Minimum length is  "+minLen+".";
			errorFlag = true;
		}
	}
	
	if (errorFlag!=true) {
		errorObj.innerHTML="";
	}
	
	return errorFlag;
}

/**
* To display all PHR accounts of user in select box.
*
* Author Valsaraj
* Added on 07/12/2010 
*/
function displayUsersPHRAccountIds(element, emailId, targetId) {
	var primaryEmailObj = $(element+':'+emailId);
	
	if (primaryEmailObj!=null && primaryEmailObj.value!='' && validateEmail(primaryEmailObj.value)!=-1) {
		var myAjax = new Ajax.Request(
		    'getUsersPHRAccountIds.ajaxcchit',
		    {
		     	method: 'get',
		     	parameters: 'element='+element+'&email='+primaryEmailObj.value,
				onComplete: function(req) {
					var selObj = $(element+":"+targetId);
					selObj.options.length = 1;
						
					if (req.responseText!=null && req.responseText!="") {
						phrIdArray = req.responseText.split('|');
											
						if (phrIdArray.length > 0) {
							for (iter = 0; iter < phrIdArray.length; iter++) {
								var option = new Option(phrIdArray[iter], phrIdArray[iter]);
								selObj.options.add(option);
							}
						}
					}
				}
		    }
		);
	}
}

/**
* Sets selected PHR account id to input field.
*
* Author Valsaraj
* Added on 07/12/2010 
*/
function setUsersPHRAccountId(element, source, target) {
	$(element+':'+target).value = $(element+':'+source).value;
}

/**
 * Method to load the template wizards. 
 * @author Pinky 
 * @param path
 * @param divId
 * @param template 
 * @param widgetIndex
 * @param fnc
 */
function showOrderAssociationsDropDown(path,divId,template,widgetIndex,root,fnc){
	new Ajax.Updater(divId, '../wizard/'+template+'.jsf', {
		parameters: { element: path,widgetIndex:widgetIndex},evalScripts:true, onComplete:function(req) {
			if(fnc)
				fnc();
			if($('addNewOrderAnchor'))
				$('addNewOrderAnchor').show();
			if(root){
				if(root+':checkRelationship'){
					if($('savedOrder0'))
						$(root+':checkRelationship').value = 1;	
					else
						$(root+':checkRelationship').value = 0;	
				}
			}
			wizHideAjaxLoader();
	    }
			
	});
}

/**
 * Method to close the pop-ups.
 * @author Pinky
 * @param element
 * @param root
 * @param div 
 * @param template
 * @param dropDownDivId
 * @param index 
 * @param relName
 * @param widgetIndex 
 */
closeOrderAssociatePopUps=function(element,root,div,template,dropDownDivId,index,relName,widgetIndex){
	$(div).hide();	
	 var param = "";
	    param = '&element='+element+
	            '&index='+index+
	            '&relName='+relName;
	    wizShowAjaxLoader();
	    var instAjax = new Ajax.Request(
	        'removeUnsaved.ajaxcchit', {
	            method: 'get', 
	            parameters: param,
	            onComplete: function(req) {
	                if (req.responseText=="Success") {
	                	showOrderAssociationsDropDown(element,dropDownDivId,template,widgetIndex);
	                }
	                else {
	                    wizHideAjaxLoader();
	                }                  
	            } 
		});
	
}
/**
 * Method to select the problem from the list and save as a relationship in trim.
 * @author Pinky
 * @param template
 * @param root
 * @param methodArgs 
 */
wizSaveOrderAssociateProblem=function(template, root, methodArgs){
	var lArgs = splitArguments(methodArgs);
    var formId = lArgs[0];
    closePopup(root,formId); 
    var element=$(formId).getAttribute("tolvenid" );
    var widgetIndex = $('currentWidget').value;
    var param = "";
    param = '&element='+element+
            '&template='+template;
    wizShowAjaxLoader();
    var instAjax = new Ajax.Request(
        'saveOrderAssociateProblems.ajaxcchit', {
            method: 'get', 
            parameters: param,
            onComplete: function(req) {
                if (req.responseText.split("|")[0]=="Success") {
                	var index = req.responseText.split("|")[1];
                	showDetailWizard(element,'orderAssociationPopUpDiv','problemTemplate',index,widgetIndex);
                }
                else {
                    wizHideAjaxLoader();
                }                  
            } 
	});
}

/**
 * Method to select the diagnoses from the list and save as a relationship in trim.
 * @author Pinky
 * @param template
 * @param root
 * @param methodArgs 
 */
wizSaveOrderAssociateDiagnoses=function(template, root, methodArgs){
	var lArgs = splitArguments(methodArgs);
    var formId = lArgs[0];
    closePopup(root,formId); 
    var element=$(formId).getAttribute("tolvenid" );
    var widgetIndex = $('currentWidget').value;
    var param = "";
    param = '&element='+element+
            '&template='+template;
    wizShowAjaxLoader();
    var instAjax = new Ajax.Request(
        'saveOrderAssociateDiagnoses.ajaxcchit', {
            method: 'get', 
            parameters: param,
            onComplete: function(req) {
                if (req.responseText.split("|")[0]=="Success") {
                	var index = req.responseText.split("|")[1];
                	showDetailWizard(element,'orderAssociationPopUpDiv','diagnosesTemplate',index,widgetIndex);
                }
                else {
                    wizHideAjaxLoader();
                }                  
            } 
	});
}
/**
 * Method to show the pop-ups which contains the details of the selected problem.
 * @author Pinky
 * @param path
 * @param divId
 * @param templateWizard
 * @param index
 * @param widgetIndex
 */
function showDetailWizard(path,divId,templateWizard,index,widgetIndex){
	new Ajax.Updater(divId, '../wizard/'+templateWizard+'.jsf', {
			parameters: { 
			element: path,index:index,widgetIndex:widgetIndex},
			evalScripts:true, 
			onComplete:function(req) {
				$('orderAssociationPopUpDiv').show();
				wizHideAjaxLoader();
		    }
	});
}
/**
 * Method to save all the details of problem.
 * @author Pinky
 * @param element
 * @param index
 * @param dropDownDivId
 * @param template
 * @param widgetIndex
 */
function saveOrderAssociateProblemDetails(element,index,dropDownDivId,template,widgetIndex){
	$('errorBox').innerHTML="";
	var param = "";
    param = '&element='+element+
            '&index='+index+
            '&severity='+$('severity').value+
            '&course='+$('course').value+
            '&outcome='+$('outcome').value+
            '&treatment='+$('treatment').value+
            '&comments='+$('comments').value+
            '&effectiveTime='+$('FieldqueseffectiveTime').value;
    wizShowAjaxLoader();
    if($('FieldqueseffectiveTime').value!=""){
    	var enteredDate = new Date($('FieldqueseffectiveTime').value).setHours(0, 0, 0, 0);
    	var currentDate = new Date().setHours(0, 0, 0, 0);
    	if(enteredDate<currentDate){
		    var instAjax = new Ajax.Request(
		        'saveOrderAssociateProblemDetails.ajaxcchit', {
		            method: 'get', 
		            parameters: param,
		            onComplete: function(req) {
		                if (req.responseText=="Success") {
		                	$('orderAssociationPopUpDiv').hide();
		                	showOrderAssociationsDropDown(element,dropDownDivId,template,widgetIndex);
		                }
		            } 
			});
    	}
    	else{
    		$('errorBox').innerHTML="Date of Onset should be less than the current date";
    	    wizHideAjaxLoader();
    	}
    }
    else{
		$('errorBox').innerHTML="Enter the Date of Onset";
	    wizHideAjaxLoader();
    }
    
    fade('errorBox');
}
    
/**
 * Method to save all the details of diagnoses.
 * @author Pinky
 * @param element
 * @param index
 * @param dropDownDivId
 * @param template
 * @param widgetIndex
 */
function saveOrderAssociateDiagnosesDetails(element,index,dropDownDivId,template,widgetIndex){
	$('errorSpan').innerHTML="";
	var param = "";
    param = '&element='+element+
            '&index='+index+
            '&severity='+$('severity').value+
            '&course='+$('course').value+
            '&episodicity='+$('episodicity').value+
            '&onset='+$('onset').value+
            '&effectiveTime='+$('FieldqueseffectiveTime').value;
    wizShowAjaxLoader();
    if($('FieldqueseffectiveTime').value!=""){
    	var enteredDate = new Date($('FieldqueseffectiveTime').value).setHours(0, 0, 0, 0);
    	var currentDate = new Date().setHours(0, 0, 0, 0);
    	if(enteredDate<currentDate){
		    var instAjax = new Ajax.Request(
		        'saveOrderAssociateDiagnosesDetails.ajaxcchit', {
		            method: 'get', 
		            parameters: param,
		            onComplete: function(req) {
		                if (req.responseText=="Success") {
		                	$('orderAssociationPopUpDiv').hide();
		                	showOrderAssociationsDropDown(element,dropDownDivId,template,widgetIndex);
		                }
		                else
		                	wizHideAjaxLoader();
		            } 
			});
    	}
    	else{
    		$('errorSpan').innerHTML="Date of Diagnosis should be less that the current date";
    		wizHideAjaxLoader();
    	}
    }
    else{
    	$('errorSpan').innerHTML="Enter the Date of Diagnosis";
    	wizHideAjaxLoader();
    }
    
    fade('errorSpan');
}

/**
 * Method to save the value in trim on selecting drop-down for associations.
 * @author Pinky
 * @param title
 * @param dropDownId
 * @param root
 * @param hiddenTextBoxId
 */
function selectAssociateItem(title,dropDownId,root,hiddenTextBoxId){
	var item = document.getElementById(dropDownId).options[document.getElementById(dropDownId).selectedIndex].text;
	$(hiddenTextBoxId).value = item;
}

/**
 * Method to select the immunization order from the list and save as a relationship in trim.
 * @author Pinky
 * @param template
 * @param root
 * @param methodArgs
 */
wizSaveImmunizationOrder=function(template, root, methodArgs){
	var lArgs = splitArguments(methodArgs);
    var formId = lArgs[0];
    closePopup(root,formId); 
    var element=$(formId).getAttribute("tolvenid" );
    var param = "";
    param = '&element='+element+
            '&template='+template;
    wizShowAjaxLoader();
    var instAjax = new Ajax.Request(
        'saveOrders.ajaxcchit', {
            method: 'get', 
            parameters: param,
            onComplete: function(req) {
                if (req.responseText.split("|")[0]=="Success") {
                	$('addNewOrderAnchor').hide();
            		var index = req.responseText.split("|")[1];
                	var template = req.responseText.split("|")[2];
                	var templateDescription = req.responseText.split("|")[3];
                	$('newOrder').show();
                	$('templateDescription').innerHTML = templateDescription;
                	$('template').value = template;
                	$('templateTrim').value = templateDescription;
                	
                	wizHideAjaxLoader();
                	
                }
                else {
                    wizHideAjaxLoader();
                }                  
            } 
	});
}

/**
 * Method to remove the widgets by means of Ajax.
 * @author Pinky
 * @param element
 * @param div
 * @param template
 * @param index
 */
removeTpfRel=function(element,div,template,index,root){
	 var param = "";
	    param = '&element='+element+
	            '&index='+index;
	    wizShowAjaxLoader();
	    var instAjax = new Ajax.Request(
	        'removeTpfRel.ajaxcchit', {
	            method: 'get', 
	            parameters: param,
	            onComplete: function(req) {
	                if (req.responseText=="Success") {
	                	showOrderAssociationsDropDown(element,div,template,"",root);
	                }
	                else {
	                    wizHideAjaxLoader();
	                }                  
	            } 
		});
}

/**
 * Method to save the details of the selected immunization order in trim by Ajax.
 * @author Pinky
 * @param element
 * @param div
 * @param template
 * @param index
 */
saveTpfRel=function(element,div,template,index,root){
	$('msgImmunization').innerHTML = "";
	var templateTrim = $('template').value;
	var problem = document.getElementById('orderAssociateProblems'+index).options[document.getElementById('orderAssociateProblems'+index).selectedIndex].text;
	var diagnosis = document.getElementById('orderAssociateDiagnoses'+index).options[document.getElementById('orderAssociateDiagnoses'+index).selectedIndex].text;
	var date = $('FieldquesorderDate'+index).value;
	var reason = $('reason'+index).value;
	var param = "";
	    param = '&element='+element+
	            '&index='+index+
	            '&problem='+escape(problem)+
	            '&diagnosis='+escape(diagnosis)+
	            '&date='+date+
	            '&reason='+reason+
	            '&template='+templateTrim;
	    wizShowAjaxLoader();
	    if($('FieldquesorderDate'+index).value!=""){
		    var enteredDate = new Date($('FieldquesorderDate'+index).value).setHours(0, 0, 0, 0);
		    var currentDate = new Date().setHours(0, 0, 0, 0);
		    if(enteredDate>=currentDate){
			    var instAjax = new Ajax.Request(
			        'saveTpfRel.ajaxcchit', {
			            method: 'get', 
			            parameters: param,
			            onComplete: function(req) {
			                if (req.responseText.split("|")[0]=="Success") {
			                	var fnRefresh = function(){
			                		var index = req.responseText.split("|")[1];
			                		$('toBeSavedOrder'+index).hide();
				                	$('savedOrder'+index).show();
				                	$('addNewOrderAnchor').show();
			                	}
			                	showOrderAssociationsDropDown(element,div,template,"",root,fnRefresh);
			                }
			                else {
			                    wizHideAjaxLoader();
			                }                  
			            } 
				});
		    }
		    else{
	    		$('msgImmunization').innerHTML="Date of Order should be greater than or equal to the current date";
	    		wizHideAjaxLoader(); 
		    }
	    }
	    else{
	    	$('msgImmunization').innerHTML="Enter the Date of Order";
	    	wizHideAjaxLoader(); 
	    }
	    
	    fade('msgImmunization');
}

/**
 * Method to show the details of the selected immunization order on clicking 'edit' button.
 * @author Pinky
 * @param element
 * @param div
 * @param template
 * @param index
 */
editTpfRel=function(element,div,template,index){
	$('savedOrder'+index).hide();
	$('toBeSavedOrder'+index).show();
	$('currentAction').value = 1;
	
	if($('associateDiagnosesValue'+index)){
		var diagnosis = $('associateDiagnosesValue'+index).value; 
		var diagnosisDropDown = document.getElementById('orderAssociateDiagnoses'+index);
		selectItemFnc(diagnosisDropDown,diagnosis);		
	}
	if($('associateProblemValue'+index)){
		var problem = $('associateProblemValue'+index).value;
		var problemDropDown = document.getElementById('orderAssociateProblems'+index);	
		selectItemFnc(problemDropDown,problem);
	}
	
}

/**
 * Method to cancel the widget.
 * @author Pinky
 * @param element
 * @param div
 * @param template
 * @param index
 */
cancelTpfRel=function(element,div,template,index){
	if($('toBeSavedOrder'+index))
		$('toBeSavedOrder'+index).hide();
	if($('savedOrder'+index))
		$('savedOrder'+index).show();
	if($('newOrder')&&(index=="")){
		$('newOrder').hide();
		$('addNewOrderAnchor').show();
	}
}

/**
 * Method to call the open template function and to save the current value of widget in the wizard.
 * @author Pinky
 * @param top1
 * @param top2
 * @param fnc
 * @param element
 * @param no
 * @param widgetIndex
 * @return
 */
function callOpenTemplateFunc(top1,top2,fnc,element,no,widgetIndex){
	openTemplate(top1, top2, fnc, element, no);
	$('currentWidget').value = widgetIndex;
}
/**
 * Method to display popup with code and description
 */
function openTemplateWithCode(contentName, placeholderid, methodName, formId, index,gridType) {
	openTemplate(contentName, placeholderid, methodName, formId, index+"|withCode",gridType)
}


/**
 * Method to select the laboratory order from the list and save as a relationship in trim.
 * @author Pinky
 * @param template
 * @param root
 * @param methodArgs
 */
wizSaveLabOrder=function(template, root, methodArgs){
	var lArgs = splitArguments(methodArgs);
    var formId = lArgs[0];
    closePopup(root,formId); 
    var element=$(formId).getAttribute("tolvenid" );
    var param = "";
    param = '&element='+element+
            '&template='+template;
    wizShowAjaxLoader();
    var instAjax = new Ajax.Request(
        'saveOrders.ajaxcchit', {
            method: 'get', 
            parameters: param,
            onSuccess: function(req) {
                if (req.responseText.split("|")[0]=="Success") {
                		$('addNewOrderAnchor').hide();
                		var index = req.responseText.split("|")[1];
                    	var template = req.responseText.split("|")[2];
                    	var templateDescription = req.responseText.split("|")[3];
                    	$('newOrder').show();
                    	$('templateDescription').innerHTML = templateDescription;
                    	$('template').value = template;
                    	$('templateTrim').value = templateDescription;
                    	
                    	  wizHideAjaxLoader();
                }
                else {
                    wizHideAjaxLoader();
                }                  
            } 
	});
}

/**
 * Method to select the image order from the list and save as a relationship in trim.
 * @author Pinky
 * @param template
 * @param root
 * @param methodArgs
 */
wizSaveImageOrder=function(template, root, methodArgs){
	var lArgs = splitArguments(methodArgs);
    var formId = lArgs[0];
    closePopup(root,formId); 
    var element=$(formId).getAttribute("tolvenid" );
    var param = "";
    param = '&element='+element+
            '&template='+template;
    wizShowAjaxLoader();
    var instAjax = new Ajax.Request(
        'saveOrders.ajaxcchit', {
            method: 'get', 
            parameters: param,
            onComplete: function(req) {
                if (req.responseText.split("|")[0]=="Success") {
                	
                	$('addNewOrderAnchor').hide();
            		var index = req.responseText.split("|")[1];
                	var template = req.responseText.split("|")[2];
                	var templateDescription = req.responseText.split("|")[3];
                	$('newOrder').show();
                	$('templateDescription').innerHTML = templateDescription;
                	$('template').value = template;
                	$('templateTrim').value = templateDescription;
                	
                    wizHideAjaxLoader();
                	
                }
                else {
                    wizHideAjaxLoader();
                }                  
            } 
	});
}

/**
 * Method to save the details of the selected immunization order in trim by Ajax.
 * @author Pinky
 * @param element
 * @param div
 * @param template
 * @param index
 */
saveImageTpfRel=function(element,div,template,index,root){
	$('msgImageOrder').innerHTML = "";
	var templateTrim = $('template').value;
	var problem = document.getElementById('orderAssociateProblems'+index).options[document.getElementById('orderAssociateProblems'+index).selectedIndex].text;
	var diagnosis = document.getElementById('orderAssociateDiagnoses'+index).options[document.getElementById('orderAssociateDiagnoses'+index).selectedIndex].text;
	var date = $('FieldquesorderDate'+index).value;
	var reason = $('reason'+index).value;
	var param = "";
	    param = '&element='+element+
	            '&index='+index+
	            '&problem='+escape(problem)+
	            '&diagnosis='+escape(diagnosis)+
	            '&date='+date+
	            '&reason='+reason+
	            '&template='+templateTrim;
	    wizShowAjaxLoader();
	    if($('FieldquesorderDate'+index).value!=""){
		    var enteredDate = new Date($('FieldquesorderDate'+index).value).setHours(0, 0, 0, 0);
		    var currentDate = new Date().setHours(0, 0, 0, 0);
		    if(enteredDate>=currentDate){
			    var instAjax = new Ajax.Request(
			        'saveImageTpfRel.ajaxcchit', {
			            method: 'get', 
			            parameters: param,
			            onComplete: function(req) {
			                if (req.responseText.split("|")[0]=="Success") {
			                	var fnRefresh = function(){
			                		var index = req.responseText.split("|")[1];
			                		$('toBeSavedOrder'+index).hide();
				                	$('savedOrder'+index).show();
				                	$('addNewOrderAnchor').show();
			                	}
			                	showOrderAssociationsDropDown(element,div,template,"",root,fnRefresh);
			                }
			                else {
			                    wizHideAjaxLoader();
			                }                  
			            } 
				});
		    }
		    else{
	    		$('msgImageOrder').innerHTML="Date of Order should be greater than or equal to the current date";
	    		wizHideAjaxLoader(); 
		    }
	    }
	    else{
	    	$('msgImageOrder').innerHTML="Enter the Date of Order";
	    	wizHideAjaxLoader(); 
	    }
	    
	    fade('msgImageOrder');
}

/**
 * Method to select the referral request from the list and save as a relationship in trim.
 * @author Pinky
 * @param template
 * @param root
 * @param methodArgs
 */
wizSaveReferralRequest=function(template, root, methodArgs){
	var lArgs = splitArguments(methodArgs);
    var formId = lArgs[0];
    closePopup(root,formId); 
    var element=$(formId).getAttribute("tolvenid" );
    var param = "";
    param = '&element='+element+
            '&template='+template;
    wizShowAjaxLoader();
    var instAjax = new Ajax.Request(
        'saveOrders.ajaxcchit', {
            method: 'get', 
            parameters: param,
            onComplete: function(req) {
                if (req.responseText.split("|")[0]=="Success") {
                	$('addNewOrderAnchor').hide();
            		var index = req.responseText.split("|")[1];
                	var template = req.responseText.split("|")[2];
                	var templateDescription = req.responseText.split("|")[3];
                	$('newOrder').show();
                	$('templateDescription').innerHTML = templateDescription;
                	$('template').value = template;
                	$('templateTrim').value = templateDescription;
                	
                	wizHideAjaxLoader();
                }
                else {
                    wizHideAjaxLoader();
                }                  
            } 
	});
}

/**
 * Method to save the details of the selected referral request in trim by Ajax.
 * @author Pinky
 * @param element
 * @param div
 * @param template
 * @param index
 */
saveReferralTpfRel=function(element,div,template,index,root){
	$('msgReferralRequest').innerHTML = "";
	var templateTrim = $('template').value;
	var problem = document.getElementById('orderAssociateProblems'+index).options[document.getElementById('orderAssociateProblems'+index).selectedIndex].text;
	var diagnosis = document.getElementById('orderAssociateDiagnoses'+index).options[document.getElementById('orderAssociateDiagnoses'+index).selectedIndex].text;
	var date = $('FieldquesorderDate'+index).value;
	var reason = $('reason'+index).value;
	var param = "";
	    param = '&element='+element+
	            '&index='+index+
	            '&problem='+escape(problem)+
	            '&diagnosis='+escape(diagnosis)+
	            '&date='+date+
	            '&reason='+reason+
	            '&template='+templateTrim;
	    wizShowAjaxLoader();
	    if($('FieldquesorderDate'+index).value!=""){
		    var enteredDate = new Date($('FieldquesorderDate'+index).value).setHours(0, 0, 0, 0);
		    var currentDate = new Date().setHours(0, 0, 0, 0);
		    if(enteredDate>=currentDate){
			    var instAjax = new Ajax.Request(
			        'saveReferralTpfRel.ajaxcchit', {
			            method: 'get', 
			            parameters: param,
			            onComplete: function(req) {
			                if (req.responseText.split("|")[0]=="Success") {
			                	var fnRefresh = function(){
			                		var index = req.responseText.split("|")[1];
			                		$('toBeSavedOrder'+index).hide();
				                	$('savedOrder'+index).show();
				                	$('addNewOrderAnchor').show();
			                	}
			                	showOrderAssociationsDropDown(element,div,template,"",root,fnRefresh);
			                }
			                else
			                	wizHideAjaxLoader();
			            } 
				});
		    }
		    else{
	    		$('msgReferralRequest').innerHTML="Date of Order should be greater than or equal to the current date";
	    		wizHideAjaxLoader();
		    }
	    }
	    else{
	    	$('msgReferralRequest').innerHTML="Enter the Date of Order";
	    	wizHideAjaxLoader();
	    }
	    
	    fade('msgReferralRequest');
}

/**
 * Method to save the details of the selected problem in trim by Ajax.
 * @author Pinky
 * @param element
 * @param div
 * @param template
 * @param index
 * Modified by valsaraj on 08/27/2010 to allow current date.
 */
saveProbRel=function(element,div,template,index,root){
	$('msgProblemDocclin').innerHTML = "";
	var templateTrim = $('template').value;
	var severity = $('severity'+index).value;
	var course = $('course'+index).value;
	var outcome = $('outcome'+index).value;
	var date = $('FieldquesonsetDate'+index).value;
	var treatment = $('treatment'+index).value;
	var comments = $('comments'+index).value;
	var param = "";
	    param = '&element='+element+
	            '&index='+index+
	            '&severity='+severity+
	            '&course='+course+
	            '&date='+date+
	            '&outcome='+outcome+
	            '&treatment='+treatment+
	            '&comments='+comments+
	            '&template='+templateTrim;
	    wizShowAjaxLoader();
	    if($('FieldquesonsetDate'+index).value!=""){
		    var enteredDate = new Date($('FieldquesonsetDate'+index).value).setHours(0, 0, 0, 0);
		    var currentDate = new Date().setHours(0, 0, 0, 0);
		    if(enteredDate<=currentDate){
			    var instAjax = new Ajax.Request(
			        'saveProbRel.ajaxcchit', {
			            method: 'get', 
			            parameters: param,
			            onComplete: function(req) {
			                if (req.responseText.split("|")[0]=="Success") {
			                	var fnRefresh = function(){
			                		var index = req.responseText.split("|")[1];
			                		$('toBeSavedOrder'+index).hide();
				                	$('savedOrder'+index).show();
				                	if ($('addNewOrderAnchor') != null) {
				                		$('addNewOrderAnchor').show();
									}
			                	}
			                	showOrderAssociationsDropDown(element,div,template,"",root,fnRefresh);
			                }
			                else {
			                    wizHideAjaxLoader();
			                }                  
			            } 
				});
		    }
		    else{
	    		$('msgProblemDocclin').innerHTML="Date of onset should be less than or equal to the current date";
	    		 wizHideAjaxLoader();
		    }
	    }
	    else{
	    	$('msgProblemDocclin').innerHTML="Enter the Date of Onset";
	    	 wizHideAjaxLoader();
	    }
	    
	    fade('msgProblemDocclin');
}

/**
 * Method to select the problem from the list and save as a relationship in trim.
 * @author Pinky
 * @param template
 * @param root
 * @param methodArgs
 */
wizSaveProblemDocclin=function(template, root, methodArgs){
	var lArgs = splitArguments(methodArgs);
    var formId = lArgs[0];
    closePopup(root,formId); 
    var element=$(formId).getAttribute("tolvenid" );
    var param = "";
    param = '&element='+element+
            '&template='+template;
    wizShowAjaxLoader();
    var instAjax = new Ajax.Request(
        'saveOrders.ajaxcchit', {
            method: 'get', 
            parameters: param,
            onComplete: function(req) {
                if (req.responseText.split("|")[0]=="Success") {
                	$('addNewOrderAnchor').hide();
            		var index = req.responseText.split("|")[1];
                	var template = req.responseText.split("|")[2];
                	var templateDescription = req.responseText.split("|")[3];
                	$('newOrder').show();
                	$('templateDescription').innerHTML = templateDescription;
                	$('template').value = template;
                	$('templateTrim').value = templateDescription;
                	
                	wizHideAjaxLoader();
                }
                else {
                    wizHideAjaxLoader();
                }                  
            } 
	});
}

/**
 * Method to select the procedure from the list and save as a relationship in trim.
 * @author Pinky
 * @param template
 * @param root
 * @param methodArgs
 */
wizSaveProcedureDocclin=function(template, root, methodArgs){
	var lArgs = splitArguments(methodArgs);
    var formId = lArgs[0];
    closePopup(root,formId); 
    var element=$(formId).getAttribute("tolvenid" );
    var param = "";
    param = '&element='+element+
            '&template='+template;
    wizShowAjaxLoader();
    var instAjax = new Ajax.Request(
        'saveOrders.ajaxcchit', {
            method: 'get', 
            parameters: param,
            onComplete: function(req) {
                if (req.responseText.split("|")[0]=="Success") {
                	$('addNewOrderAnchor').hide();
            		var index = req.responseText.split("|")[1];
                	var template = req.responseText.split("|")[2];
                	var templateDescription = req.responseText.split("|")[3];
                	$('newOrder').show();
                	$('templateDescription').innerHTML = templateDescription;
                	$('template').value = template;
                	$('templateTrim').value = templateDescription;
                	
                	wizHideAjaxLoader();
                	
                }
                else {
                    wizHideAjaxLoader();
                }                  
            } 
	});
}

/**
 * Method to save the details of the selected laboratory order in trim by Ajax.
 * @author Pinky
 * @param element
 * @param div
 * @param template
 * @param index
 */
saveLabTpfRel=function(element,div,template,index,root){
	$('msgLabOrder').innerHTML = "";
	var templateTrim = $('template').value;
	var problem = document.getElementById('orderAssociateProblems'+index).options[document.getElementById('orderAssociateProblems'+index).selectedIndex].text;
	var diagnosis = document.getElementById('orderAssociateDiagnoses'+index).options[document.getElementById('orderAssociateDiagnoses'+index).selectedIndex].text;
	var date = $('FieldquesorderDate'+index).value;
	var reason = $('reason'+index).value;
	var specimenType = $('labSpecimen'+index).value;
	var conatiner = $('labContainer'+index).value;
	var param = "";
	    param = '&element='+element+
	            '&index='+index+
	            '&problem='+escape(problem)+
	            '&diagnosis='+escape(diagnosis)+
	            '&date='+date+
	            '&reason='+reason+
	            '&specimenType='+specimenType+
	            '&conatiner='+conatiner+
	            '&template='+templateTrim;
	    wizShowAjaxLoader();
	    if($('FieldquesorderDate'+index).value!=""){
		    var enteredDate = new Date($('FieldquesorderDate'+index).value).setHours(0, 0, 0, 0);
		    var currentDate = new Date().setHours(0, 0, 0, 0);
		    if(enteredDate>=currentDate){
			    var instAjax = new Ajax.Request(
			        'saveLabTpfRel.ajaxcchit', {
			            method: 'get', 
			            parameters: param,
			            onComplete: function(req) {
			                if (req.responseText.split("|")[0]=="Success") {
			                	var fnRefresh = function(){
			                		var index = req.responseText.split("|")[1];
			                		$('toBeSavedOrder'+index).hide();
				                	$('savedOrder'+index).show();
				                	$('addNewOrderAnchor').show();
			                	}
			                	showOrderAssociationsDropDown(element,div,template,"",root,fnRefresh);
			                }
			                else
			                	wizHideAjaxLoader(); 
			            },
			            onSuccess: function(req) {
			            	if (req.responseText.split("|")[0]=="Success") {
			                	var fnRefresh = function(){
			                		var index = req.responseText.split("|")[1];
			                		$('toBeSavedOrder'+index).hide();
				                	$('savedOrder'+index).show();
				                	$('addNewOrderAnchor').show();
			                	}
			                	showOrderAssociationsDropDown(element,div,template,"",root,fnRefresh);
			                }
			                else
			                	wizHideAjaxLoader(); 
			            }
			        
				});
		    }
		    else{
	    		$('msgLabOrder').innerHTML="Date of Order should be greater than or equal to the current date";
	    		wizHideAjaxLoader(); 
		    }
	    }
	    else{
	    	$('msgLabOrder').innerHTML="Enter the Date of Order";
	    	wizHideAjaxLoader(); 
	    }
	    
	    fade('msgLabOrder');
}

/**
 * Method to save the details of the selected procedure order in trim by Ajax.
 * @author Pinky
 * @param element
 * @param div
 * @param template
 * @param index
 */
saveProcedureRel=function(element,div,template,index,root){
	$('msgProcedureDocclin').innerHTML = "";
	var templateTrim = $('template').value;
	var problem = document.getElementById('orderAssociateProblems'+index).options[document.getElementById('orderAssociateProblems'+index).selectedIndex].text;
	var diagnosis = document.getElementById('orderAssociateDiagnoses'+index).options[document.getElementById('orderAssociateDiagnoses'+index).selectedIndex].text;
	var date = $('FieldquesorderDate'+index).value;
	var reason = $('reason'+index).value;
	var param = "";
	    param = '&element='+element+
	            '&index='+index+
	            '&problem='+escape(problem)+
	            '&diagnosis='+escape(diagnosis)+
	            '&date='+date+
	            '&reason='+reason+
	            '&template='+templateTrim;
	    wizShowAjaxLoader();
	    if($('FieldquesorderDate'+index).value!=""){
		    var enteredDate = new Date($('FieldquesorderDate'+index).value).setHours(0, 0, 0, 0);
		    var currentDate = new Date().setHours(0, 0, 0, 0);
		    if(enteredDate>=currentDate){
			    var instAjax = new Ajax.Request(
			        'saveProcedureRel.ajaxcchit', {
			            method: 'get', 
			            parameters: param,
			            onComplete: function(req) {
			                if (req.responseText.split("|")[0]=="Success") {
			                	var fnRefresh = function(){
			                		var index = req.responseText.split("|")[1];
			                		$('toBeSavedOrder'+index).hide();
				                	$('savedOrder'+index).show();
				                	$('addNewOrderAnchor').show();
			                	}
			                	showOrderAssociationsDropDown(element,div,template,"",root,fnRefresh);
			                }
			                else
			                	wizHideAjaxLoader();
			            } 
				});
		    }
		    else{
	    		$('msgProcedureDocclin').innerHTML="Date of Order should be greater than or equal to the current date";
	    		wizHideAjaxLoader();
		    }
	    }
	    else{
	    	$('msgProcedureDocclin').innerHTML="Enter the Date of Order";
	    	wizHideAjaxLoader();
	    }
	    
	    fade('msgProcedureDocclin');
}

/**
 * Method to save the details of the selected diagnosis in trim by Ajax.
 * @author Pinky
 * @param element
 * @param div
 * @param template
 * @param index
 * Modified by valsaraj on 08/27/2010 to allow current date.
 */
saveDiagRel=function(element,div,template,index,root){
	$('msgDiagnosis').innerHTML = "";
	var templateTrim = $('template').value;
	var severity = $('severity'+index).value;
	var course = $('course'+index).value;
	var onset = $('onset'+index).value;
	var episodicity = $('episodicity'+index).value;
	var date = $('FieldquesorderDate'+index).value;
	var param = "";
	    param = '&element='+element+
	            '&index='+index+
	            '&severity='+severity+
	            '&course='+course+
	            '&date='+date+
	            '&onset='+onset+
	            '&episodicity='+episodicity+
	            '&template='+templateTrim;
	    wizShowAjaxLoader();
	    if($('FieldquesorderDate'+index).value!=""){
		    var enteredDate = new Date($('FieldquesorderDate'+index).value).setHours(0, 0, 0, 0);
		    var currentDate = new Date().setHours(0, 0, 0, 0);
		    if(enteredDate<=currentDate){
			    var instAjax = new Ajax.Request(
			        'saveDiagRel.ajaxcchit', {
			            method: 'get', 
			            parameters: param,
			            onComplete: function(req) {
			                if (req.responseText.split("|")[0]=="Success") {
			                	var fnRefresh = function(){
			                		var index = req.responseText.split("|")[1];
			                		$('toBeSavedOrder'+index).hide();
				                	$('savedOrder'+index).show();
				                	$('addNewOrderAnchor').show();
			                	}
			                	showOrderAssociationsDropDown(element,div,template,"",root,fnRefresh);
			                }
			                else
			                	wizHideAjaxLoader(); 
			            } 
				});
		    }
		    else{
	    		$('msgDiagnosis').innerHTML="Date of Diagnosis should be less than or equal to the current date";
	    		wizHideAjaxLoader(); 
		    }
	    }
	    else{
	    	$('msgDiagnosis').innerHTML="Enter the Date of Diagnosis";
	    	wizHideAjaxLoader(); 
	    }
	    
	    fade('msgDiagnosis');
}

/**
 * Method to select the diagnosis from the list and save as a relationship in trim.
 * @author Pinky
 * @param template
 * @param root
 * @param methodArgs
 */
wizSaveDiagnosis=function(template, root, methodArgs){
	var lArgs = splitArguments(methodArgs);
    var formId = lArgs[0];
    closePopup(root,formId); 
    var element=$(formId).getAttribute("tolvenid" );
    var param = "";
    param = '&element='+element+
            '&template='+template;
    wizShowAjaxLoader();
    var instAjax = new Ajax.Request(
        'saveOrders.ajaxcchit', {
            method: 'get', 
            parameters: param,
            onComplete: function(req) {
                if (req.responseText.split("|")[0]=="Success") {
                	//var fnRefresh = function(){
                	$('addNewOrderAnchor').hide();
            		var index = req.responseText.split("|")[1];
                	var template = req.responseText.split("|")[2];
                	var templateDescription = req.responseText.split("|")[3];
                	$('newOrder').show();
                	$('templateDescription').innerHTML = templateDescription;
                	$('template').value = template;
                	$('templateTrim').value = templateDescription;
                	
                	wizHideAjaxLoader();
                	//}
                	//showOrderAssociationsDropDown(element,'diagnosisDiv','dxContent','',fnRefresh);
                	//var index = req.responseText.split("|")[1];
                }
                else {
                    wizHideAjaxLoader();
                }                  
            } 
	});
}

wizShowAjaxLoader=function(){
   var objOverlay = $('ajaxOverlay');
   var objLoadingImage =  $("ajaxLoader");

   var arrayPageSize = getPageSize();
   
   objOverlay.style.width=arrayPageSize[0]+"px";
   objOverlay.style.height=arrayPageSize[1]+"px";
   objOverlay.show();
   leftPos = 0;
   topPos = 0;
   if (screen) {
       leftPos = (screen.width / 2) - 251;
       topPos = (screen.height / 2) - 162;
   }
   
   objLoadingImage.style.left=leftPos+"px";
   objLoadingImage.style.top=topPos+"px";
   objLoadingImage.show();
}

wizHideAjaxLoader=function(){
   $('ajaxLoader').hide();
   $('ajaxOverlay').hide();
}

/*
 * Returns array with page width, height and window width, height
 * added on 12/28/09
 */
function getPageSize(){
    var xScroll, yScroll;
    if (window.innerHeight && window.scrollMaxY) {  
        xScroll = document.body.scrollWidth;
        yScroll = window.innerHeight + window.scrollMaxY;
    } else if (document.body.scrollHeight > document.body.offsetHeight){ // all but Explorer Mac
        xScroll = document.body.scrollWidth;
        yScroll = document.body.scrollHeight;
    } else { // Explorer Mac...would also work in Explorer 6 Strict, Mozilla and Safari
        xScroll = document.body.offsetWidth;
        yScroll = document.body.offsetHeight;
    }
    var windowWidth, windowHeight;
    if (self.innerHeight) { // all except Explorer
        windowWidth = self.innerWidth;
        windowHeight = self.innerHeight;
    } else if (document.documentElement && document.documentElement.clientHeight) { // Explorer 6 Strict Mode
        windowWidth = document.documentElement.clientWidth;
        windowHeight = document.documentElement.clientHeight;
    } else if (document.body) { // other Explorers
        windowWidth = document.body.clientWidth;
        windowHeight = document.body.clientHeight;
    }   
    // for small pages with total height less then height of the viewport
    if(yScroll < windowHeight){
        pageHeight = windowHeight;
    } else { 
        pageHeight = yScroll;
    }
    // for small pages with total width less then width of the viewport
    if(xScroll < windowWidth){  
        pageWidth = windowWidth;
    } else {
        pageWidth = xScroll;
    }
    arrayPageSize = new Array(pageWidth,pageHeight,windowWidth,windowHeight) 
    return arrayPageSize;
}

/**
 * Method to save the selected value of state in the patient trim.
 * @author Pinky
 * @param obj
 * @param root
 */
setState=function(obj,root,element){
	var  name = obj.value;
	var param = "";
    param = '&element='+element+
            '&name='+name;
	var instAjax = new Ajax.Request(
	        'saveStateCode.ajaxcchit', {
	            method: 'get', 
	            parameters: param,
	            onComplete: function(req) {
		        	if (req.responseText.split("|")[0]=="Success") {
		        		$(root+':stateOriginalCode').value = req.responseText.split("|")[1];
		            }
	            } 
		});
}
/**
 * Method to keep the selected value of state in the patient trim.
 * @author Pinky
 * @param root
 * @param code
 * @param element
 */
setStateDropDown=function(root,code,element){
	var param = "";
    param = '&element='+element+
            '&code='+code;
    if(code!=""){
	    var instAjax = new Ajax.Request(
		        'retrieveStateName.ajaxcchit', {
		            method: 'get', 
		            parameters: param,
		            onComplete: function(req) {
			        	if (req.responseText.split("|")[0]=="Success") {
			        		item = req.responseText.split("|")[1];
			        		selectItemFnc('address:3:states',item,root);
			            }
		            } 
			});
    }
}
/**
 * Method to keep the drop-down selected with the desired value.
 * @param myDropdownList
 * @param itemToSelect
 * @param root
 */
selectItemFnc = function(myDropdownList,itemToSelect,root){
	if(root)
		myDropdownList = $(root+':'+myDropdownList);
		
	for (iLoop = 0; iLoop< myDropdownList.options.length; iLoop++)
    {   
      if (myDropdownList.options[iLoop].innerHTML == itemToSelect)
      {
        myDropdownList.options[iLoop].selected = true;
      }
    }
}

var TimeToFade = 5000.0;
function fade(eid)
{
  var element = document.getElementById(eid);
  if(element == null)
    return;
   
  if(element.FadeState == null)
  {
    if(element.style.opacity == null
        || element.style.opacity == ''
        || element.style.opacity == '1')
    {
      element.FadeState = 2;
    }
    else
    {
      element.FadeState = -2;
    }
  }
  
  if(element.FadeState == 1 || element.FadeState == -1)
  {
    element.FadeState = element.FadeState == 1 ? -1 : 1;
    element.FadeTimeLeft = TimeToFade - element.FadeTimeLeft;
  }
  else
  {
    element.FadeState = element.FadeState == 2 ? -1 : 1;
    element.FadeTimeLeft = TimeToFade;
    setTimeout("animateFade(" + new Date().getTime() + ",'" + eid + "')", 33);
  }  
}

function animateFade(lastTick, eid)
{  
  var element = document.getElementById(eid);
  element.FadeState=-1;
  //if(element.FadeState == 1)
  element.style.display="block"; 
  var curTick = new Date().getTime();
  var elapsedTicks = curTick - lastTick;
 
  
  if(element.FadeTimeLeft <= elapsedTicks)
  {
    element.style.opacity = element.FadeState == 1 ? '1' : '0';
    element.style.filter = 'alpha(opacity = '
        + (element.FadeState == 1 ? '100' : '0') + ')';
    if(element.FadeState != 1)
      element.style.display="none";
    element.FadeState = element.FadeState == 1 ? 2 : -2;
    
    return;
  }
 
  element.FadeTimeLeft -= elapsedTicks;
  var newOpVal = element.FadeTimeLeft/TimeToFade;
  if(element.FadeState == 1)
    newOpVal = 1 - newOpVal;

  element.style.opacity = newOpVal;
  element.style.filter = 'alpha(opacity = ' + (newOpVal*100) + ')';
 
  setTimeout("animateFade(" + curTick + ",'" + eid + "')", 33);
}

/**
 * Method to validate mandatory fields in the patient wizards.
 * @author Pinky
 * @param root
 * Added on 07/28/2010 
 */
patientWizardValidations=function(root){
	var errorIndicator = true;
	var regExpName = /^[a-zA-Z0-9\s]+$/;
	if($(root+':firstName').value==""){
		$('errorFirstName').innerHTML = "First name is required.";
		errorIndicator = false;
	}
	else if($(root+':firstName').value!=""){
		if(regExpName.test($(root+':firstName').value)){
			$('errorFirstNameRow').style.display = 'none';
			$('errorFirstName').innerHTML = "";
		}
		else{
			$('errorFirstName').innerHTML = "Invalid First Name: Special characters are not supported.";
			errorIndicator = false;
		}
	}
	
	if($(root+':lastName').value==""){
		$('errorLastName').innerHTML = "Last name is required.";
		errorIndicator = false;
	}
	else if($(root+':lastName').value!=""){
		if(regExpName.test($(root+':lastName').value)){
			$('errorLastNameRow').style.display = 'none';
			$('errorLastName').innerHTML = "";
		}
		else{
			$('errorLastName').innerHTML = "Invalid Last Name: Special characters are not supported.";
			errorIndicator = false;
		}
	}
	
	if(($(root+':Fieldquesdob').value=="")||($(root+':Fieldquesdob').value==null)){
		$('errorDOB').innerHTML = "Date of Birth is required.";
		errorIndicator = false;
	}
	else if($(root+':Fieldquesdob').value!=""){
		var enteredDate = new Date($(root+':Fieldquesdob').value).setHours(0, 0, 0, 0);
	    var currentDate = new Date().setHours(0, 0, 0, 0);
	    if(enteredDate>currentDate){
	    	errorIndicator = false;
	    	$('errorDOB').innerHTML = "Date of Birth should be less than or equal to current date.";
	    }
	    else{
	    	$('errorDOBRow').style.display = 'none';
	    	$('errorDOB').innerHTML = "";
	    }
	}
	if($(root+':addressLine1').value!=""){
		var val = $(root+":addressLine1").value;
		var spaceFormat = /([\S]+\s)+[\S]+$/;
		if(val.search(spaceFormat) == -1){
		    $('errorAddressLine1').innerHTML = "Address Line 1 must contain space.";
		    errorIndicator = false;
		    
		}else{
			$('errorAddressLine1Row').style.display="none";
			$('errorAddressLine1').innerHTML = "";
		}
	}
    if(($(root+':primaryEmailId').value=="")||($(root+':primaryEmailId').value==null)){
		$('errorPrimaryEmail').innerHTML = "Primary Email is required.";
		errorIndicator = false;
	}
	else if($(root+':primaryEmailId').value!=""){
		if (validateEmail($(root+':primaryEmailId').value) == -1){
			$('errorPrimaryEmail').innerHTML = "Validation Error: Primary Email is invalid";
			errorIndicator = false;
		}
		else{
			$('errorPrimaryEmailRow').style.display = 'none';
			$('errorPrimaryEmail').innerHTML = "";
		}
	}
	
	if(($(root+':secondaryEmailId').value!="")&&($(root+':secondaryEmailId').value!=null)){
		if (validateEmail($(root+':secondaryEmailId').value) == -1){
			$('errorSecondaryEmail').innerHTML = "Validation Error: Secondary Email is invalid.";
			errorIndicator = false;
		}
		else{
			$('errorSecondaryEmailRow').style.display = 'none';
			$('errorSecondaryEmail').innerHTML = "";
		}
	}
	else{
		$('errorSecondaryEmailRow').style.display = 'none';
	}
	
	if (($(root + ":racemenu:0").checked == true)
			|| ($(root + ":racemenu:1").checked == true)
			|| ($(root + ":racemenu:2").checked == true)
			|| ($(root + ":racemenu:3").checked == true)
			|| ($(root + ":racemenu:4").checked == true)) {
		$('errorRaceRow').style.display = 'none';
		$('errorRace').innerHTML = "";
	}
	else{
		$('errorRace').innerHTML = "Race is required.";
		errorIndicator = false;
	}
	
	if(($(root+":ethnicitymenu:0").checked==true)||($(root+":ethnicitymenu:1").checked==true)){
		$('errorEthnicityRow').style.display = 'none';
		$('errorEthnicity').innerHTML = "";
	}
	else{
		$('errorEthnicity').innerHTML = "Ethnicity is required.";
		errorIndicator = false;
	}
	if(($(root+":receivNotif:0"))&&($(root+":receivNotif:1"))){
		if(($(root+":receivNotif:0").checked==true)||($(root+":receivNotif:1").checked==true)){
			$('errorPreferenceRow').style.display = 'none';
			$('errorPreference').innerHTML = "";
		}
		else{
			$('errorPreference').innerHTML = "Preference is required.";
			errorIndicator = false;
		}
	}
	else{
		$('errorPreferenceRow').style.display = 'none';
	}
		
	if($(root+':zip').value!=""){
		var val = $(root+":zip").value;
		decimalFormat=/(^\d{5}$)|(^\d{5}\d{4}$)/;
		if(val.search(decimalFormat)!=-1){
		$('errorZipRow').style.display = 'none';
		$('errorZipCode').innerHTML = "";
		    
		}
		else{	
			$('errorZipCode').innerHTML = "ZipCode is invalid.Enter zip + 4 code.";
		    errorIndicator = false;
	   	}
	}
		
	if(errorIndicator==false){
		$('errorDetailDiv').style.display = 'block';
		$('patientSummaryDiv').style.display = 'none';
		$(root + "submitButton").disabled = true;
	}
	else{
		$('patientSummaryDiv').style.display = 'block';
		$('errorDetailDiv').style.display = 'none';
		$(root + "submitButton").disabled = false;
	}
}

/**
 * Method to set patient combo box that matches given name.
 * 
 * @author Valsaraj 
 * Added on 07/28/2010 
 */
function autoSetPatient(elementId, name) {
	selectElement = $(elementId);
	
	if (selectElement.selectedIndex==0) {
		for (iter = 0; iter < selectElement.options.length; iter++) {
			if (selectElement.options[iter].text.toLowerCase()==name.toLowerCase()) {
				selectElement.selectedIndex=iter;
				break;
			}
		}
	}
}


/**
 * Method to validate only numbers.
 * @author Pinky
 * @param root
 * @param id
 * @param errorMsg
 * Added on 07/30/2010 
 */
validateOnlyNumbers = function(root,id,errorMsg){
	var strData = $(root+':'+id).value;	
	var errorObj = $(errorMsg);
	var iCount, iDataLen;
	var strCompare = "0123456789";
	iDataLen = strData.length;
	
	if (iDataLen > 0) {	
		for (iCount=0; iCount < iDataLen; iCount+=1) {
			var cData = strData.charAt(iCount);
		
			if (strCompare.indexOf(cData) < 0 ){
				errorObj.innerHTML="Please enter non-decimal numbers";				
				return false;
			} 
		}
		
		errorObj.innerHTML="";
		return true;
	}
	else {
		errorObj.innerHTML="";
	}
}

/**
 * Method to refresh a list by submitting all trims related to that list.
 * 
 * @author Valsaraj 
 * Added on 08/03/2010 
 */
function refreshList(listPath, element) {
	wizShowAjaxLoader();
	var instAjax = new Ajax.Request(
		'refreshList.ajaxcchit', 
		{
			method: 'get', 
			parameters: 'element='+element+'&listpath=' + listPath, 
		    onComplete: function(request) {
		    	wizHideAjaxLoader();
		    	
		    	if (request.responseText=='Success') {		    		
		    		displayPopup('Patient List Designer', "Patient lists updated successfully!", .30, .30);
		    	}
		    	else {
		    		displayPopup('Patient List Designer', "Unable to update patient lists. Please try again later.", .30, .30);
		    	}
		    }
		});
}

/**
* Method to allow only numbers.
* @author Pinky 
* Added on 09/07/2010 
*/
function onlyNumbers(evt) {
	var charCode = (evt.which) ? evt.which : event.keyCode
	if (charCode > 31 && (charCode < 48 || charCode > 57))
	   return false;

	return true;
}


/**
* Method to validate the age ranges entered.
* @author Pinky 
* Added on 09/07/2010 
*/
function validateAgeRanges(){

	var status = true;
	var lowAgeString = $("codesForm:lowAge").value;
	var lowAgeUnit = $("codesForm:lowAgeUnit").value;
	var highAgeString = $("codesForm:highAge").value;
	var highAgeUnit = $("codesForm:highAgeUnit").value;
	lowAge = parseInt(lowAgeString);
	highAge = parseInt(highAgeString);
	
	if((highAgeUnit == lowAgeUnit)&&(lowAge >= highAge))	
		status = false;
	
	else if(highAgeUnit!=lowAgeUnit){
		if((lowAgeUnit == "year")&&(highAgeUnit == "month")&&(lowAge*12 >= highAge))
			status = false;
		if((lowAgeUnit == "year")&&(highAgeUnit == "week")&&(lowAge*365 >= highAge*7))
			status = false;
		if((lowAgeUnit == "year")&&(highAgeUnit == "day")&&(lowAge*365 >= highAge))
			status = false;
		if((lowAgeUnit == "month")&&(highAgeUnit == "year")&&(lowAge >= highAge*12))
			status = false;
		if((lowAgeUnit == "month")&&(highAgeUnit == "week")&&(lowAge*30 >= highAge*7))
			status = false;
		if((lowAgeUnit == "month")&&(highAgeUnit == "day")&&(lowAge*30 >= highAge))
			status = false;
		if((lowAgeUnit == "week")&&(highAgeUnit == "year")&&(lowAge*7 >= highAge*365))
			status = false;
		if((lowAgeUnit == "week")&&(highAgeUnit == "month")&&(lowAge*7 >= highAge*30))
			status = false;
		if((lowAgeUnit == "week")&&(highAgeUnit == "day")&&(lowAge*7 >= highAge))
			status = false;
		if((lowAgeUnit == "day")&&(highAgeUnit == "year")&&(lowAge >= highAge*365))
			status = false;
		if((lowAgeUnit == "day")&&(highAgeUnit == "month")&&(lowAge >= highAge*30))
			status = false;
		if((lowAgeUnit == "day")&&(highAgeUnit == "week")&&(lowAge >= highAge*7))
			status = false;
	}
	
	if(status == false)
		$('ageRangeErrorBox').innerHTML = "Enter correct age range.Higher age range should be greater than low age range.";
	else
		$('ageRangeErrorBox').innerHTML = "";
	return status;
}
/**
* Method to validate encounter values.
* @author Sandheep
* Added on 09/24/2010 
* @param root
* @param selectid
* @param otherInputId
* @param hiddenFieldId
*/
function validateEncounter(root, selectId, otherInputId, hiddenFieldId) {
	var obj = $(root+':'+selectId);
	
	if (obj.options[obj.selectedIndex].text=='Other - not on the list') {
		$(root+':'+hiddenFieldId).value = $(root+':'+otherInputId).value;
	}
	else {
		$(root+':'+hiddenFieldId).value = obj.value;
	}
}

/**
 * Function to calculate the value of BMI in observation trim
 * @author Pinky 
 * Added on 09/29/2010  
 */
calBMIInObs = function(root,heightId,weightId){
	heightCm = parseFloat($(root+':'+heightId).value);
	weightKg = parseFloat($(root+':'+weightId).value);
	if(heightCm!=""&&weightKg!=""){
		var originalValue = (weightKg/((heightCm/100)*(heightCm/100)));
		var value = Math.round(originalValue*10)/10;
		$(root+':bmiValue').value = value;
	}
	else{
		$(root+':bmiValue').value = '0.0';
	}
}

/**
* To validate non-decimal numbers in Respiration Rate (Observation).
* Author Vineetha
* Added on 12/02/2010 
*/
function validateDecimals(root, id, errorMsg) {
	var strData = $(root+':'+id).value;	
	var errorObj = $(errorMsg);
	var iCount, iDataLen;
	var strCompare = "0123456789";
	iDataLen = strData.length;
	
	if (iDataLen > 0) {	
		for (iCount=0; iCount < iDataLen; iCount+=1) {
			var cData = strData.charAt(iCount);
		
			if (strCompare.indexOf(cData) < 0 ){
				errorObj.innerHTML="Please enter non-decimal number values";				
				return false;
			} 
		}
		
		errorObj.innerHTML="";
		return true;
	}
	else {
		errorObj.innerHTML="";
	}
}
/**
 * Method used to convert height from CM to Feet-Inches.
 * @author Vineetha
 * Added on 12/03/2010
 */
heightCnvFn=function(root) {
	 var unit1 = $(root+':heightcm');
	 var unit2 = $(root+':heightfeet');
	 var unit3 = $(root+':heightinch');
	 unit1.value = unit1.value.toString().replace(/[^\d\.eE-]/g,'');
	 if (unit1.value*0.0328083989501 != 0){
		 feet = (unit1.value*0.0328083989501).toString().split(".")[0];
		 inch = Math.round(("."+((unit1.value*0.0328083989501).toString().split(".")[1]))*12);
		 unit2.value = feet;
		 unit3.value = inch;
	 }
}


/**
 * Method used to convert weight from KG to LBS.
 * @author Vineetha
 * Added on 12/03/2010
 */
weightCnvFn=function(root){
	var unit1 = $(root+':weightlbs');
 	var unit2 = $(root+':weightkg');
 	unit2.value = unit2.value.toString().replace(/[^\d\.eE-]/g,'');
 	if(unit2.value!=null&&unit2.value!="")
		unit1.value = Math.round((unit2.value*2.2046));
}

/**
 * Function to calculate the BMI value 
 * in two combinations of height and weight units(feet-lbs or cm-kg)
 * @author Vineetha
 * Added on 12/03/2010
 */
bmiCalFnc=function(root){
	var heightFeet = $(root+':heightfeet').value;
	heightFeet = (heightFeet.replace(/^\W+/,'')).replace(/\W+$/,'');
	
	var heightInch = $(root+':heightinch').value;
	heightInch = (heightInch.replace(/^\W+/,'')).replace(/\W+$/,'');
	
	var heightCm = $(root+':heightcm').value;
	heightCm = (heightCm.replace(/^\W+/,'')).replace(/\W+$/,'');
	
	var weightPound = $(root+':weightlbs').value;
	weightPound = (weightPound.replace(/^\W+/,'')).replace(/\W+$/,'');
	
	var weightKg = $(root+':weightkg').value;
	weightKg = (weightKg.replace(/^\W+/,'')).replace(/\W+$/,'');
	
	//BMI from CM and KG
	if((heightFeet==""&& heightInch==""&&weightPound=="")){
		if(heightCm!=""&&weightKg!=""){
			var original1 = (weightKg/((heightCm/100)*(heightCm/100)));
			var value1 = Math.round(original1*10)/10;
			$(root+':bmiValue').value = value1;
			$(root+':bmiValueHidden').value = value1;
		}
		else{
			$(root+':bmiValue').value = '0.0';
			$(root+':bmiValueHidden').value = '0.0';
		}
	}
	//BMI from FEET-INCHES and LBS
	else if(((heightFeet!="")||(heightInch!=""))&&(weightPound!="")){
		if(heightInch=="")
			heightInch = 0;
		if(heightFeet=="")
			heightFeet = 0;
		var a = parseInt(heightFeet*12)+parseInt(heightInch);
		var original2 = (weightPound/(a*a))*703;
		var value2 = Math.round(original2*10)/10;
		$(root+':bmiValue').value = value2;
		$(root+':bmiValueHidden').value = value2;
	}
	else{
		$(root+':bmiValue').value = '0.0';
		$(root+':bmiValueHidden').value = '0.0';
	}
		
}	
/**
* To save a choosen FDB medication to trim
* @author Nevin
* Added on 12/04/2010 
*/
function saveFDBMedication(element, root, _medName, _medCode, _drugNameType, destGridId) {
	var _params = "";
	if (_medName != "") { 
	    _params+= '&medicationName='+escape(_medName);
	}
	if (_medCode != "") { 
	    _params += '&medicationCode='+escape(_medCode);
	}
	if (_drugNameType != "") { 
	    _params += '&drugNameType='+escape(_drugNameType);
	}
	if (element != "") { 
	    _params += '&element='+element;
	}
	var instAjax = new Ajax.Request(
			  'saveFDBMedication.ajaxcchit', 
			  {
			  method: 'get', 
			  parameters: _params,
			  onComplete: function(req) {
			            if (req.responseText=="Success") {
			            	refreshWizard(element,root,'1');
			            }
			            else {
			             
			            }                  
			        } 
			  });
}
/**
* To remove a choosen FDB medication from trim
* @author Nevin
* Added on 12/04/2010 
*/
removeFDBMedication = function (element, root, _medName, _medCode, destGridId) {
	var _params = "";
	if (_medName != "") { 
	    _params+= '&medicationName='+escape(_medName);
	}
	if (_medCode != "") { 
	    _params += '&medicationCode='+escape(_medCode);
	}
	if (element != "") { 
	    _params += '&element='+element;
	}
	var instAjax = new Ajax.Request(
			  'removeFDBMedication.ajaxcchit', 
			  {
			  method: 'get', 
			  parameters: _params,
			  onComplete: function(req) {
			            if (req.responseText=="Success") {
			            	refreshWizard(element,root,'1');
			            }
			            else {
			             
			            }                  
			        } 
			  });

}
/**
* To display the medications under selected Favourite medication list
* @author Nevin
* Added on 12/04/2010 
*/
showFavouriteMedList = function(path, formId, element){
	var popupSource = "";
	if (element.split(':')[2].split('-')[0] == 'medication') {
		popupSource = 'MedicationHistory';
	} else if (element.split(':')[2].split('-')[0] == 'wip') {
		popupSource = 'NewPrescription';
	}
	var _params = "";
	if (path != "") { 
	    _params+= '&path='+path;
	}
	if (element != "") { 
	    _params+= '&element='+element;
	}
	if (formId != "") { 
	    _params+= '&formId='+formId;
	}
	if (popupSource != "") { 
	    _params+= '&popupSource='+popupSource;
	}
	
	var instAjax = new Ajax.Request(
			  'getFavouriteMedications.ajaxcchit', 
			  {
			  method: 'get', 
			  parameters: _params,
			  onComplete: function(req) {
				  		$('mainDiv').style.display = 'none';
				  		$('favoriteDiv').style.display='block';
			            $('favoriteDiv').innerHTML=req.responseText;
			        } 
			  });
}
/**
* To show all FDB medications
* @author Nevin
* Added on 12/04/2010 
*/
showAllMedications = function(){
    $('favoriteDiv').style.display = 'none';
    $('mainDiv').style.display = 'block';
}
/**
 * On Vital Signs'Revise' action - 
 * setting the current trim as nullified and 
 * created a new trim with active status. 
 * @author Vineetha
 * added on 12/28/2010
 */
function reviseVitalSign(element,action ){
	var arr = element.split(":");
	var myAjax = new Ajax.Request(
			   'reviseVitalSign.ajaxcchit',
			   {
			    method: 'get',
			    parameters: 'element='+arr[0]+':'+arr[1]+':'+arr[2]+'&action='+action,
			    onFailure: function(request) {displayError(request,element.split());},
			    onSuccess: function(request) {reviseVitalSignDone(request,element);}
			   });
}
function cancelVitalSign( element ) {
 if (confirm( "Completely cancel and remove?" )) {
	 var myAjax = new Ajax.Request(
		   'cancelVitalSign.ajaxcchit',
		   {
		    method: 'get',
		    parameters: 'element='+element+'&action=activeNullified',
		    onFailure: function(request) {displayError(request,element.split());},
		    onSuccess: function(request) {
		    	 var myAjax1 = new Ajax.Request(
    			    'wizCancel.ajaxi',
    			    {
    			     method: 'get',
    			     parameters: 'element='+element,
    			             onFailure: function(request) {displayError(request,template);},
    			     onSuccess: wizCancelDone
    			    });
    			 }
		   });
 }
}
function reviseVitalSignDone(request,element){
	closeTab(element);
	oneSecondShow = request.responseText;

}
/**
*To select email id of the selected account and to save values to trim
 * @author Vineetha
 * added on 1/7/2010
*/
wizSelectEA=function(root) {
    var _accountSelected = $(root+':accountNameID');
    var _account = _accountSelected.value.split("|")[5];
    var _accountID = _account.split(" / ")[0];
    var _accountName = _account.split(" / ")[1];
    $(root+':accountId').value=_accountID;
    $(root+':accountName').value=_accountName;

    var instAjax = new Ajax.Request(
        'findEAAccountEMail.ajaxcchit',
        {
           method: 'get',
           parameters: 'accountId='+_accountID,
           onComplete: function(req) {
                      var _resultString = req.responseText;
                      if (_resultString.indexOf('emailId=') != -1) {
                        $(root+':accountEmail').value=_resultString.split("=")[1];
                      }
                }
        });
}


/**
* To validate date in New Patient->Advance Directives.
* Author Vineetha
* Added on 1/13/2011
*/
function validatepatientDate(root, Fieldquesdob, Fieldquesdateadded, FieldqueseffectiveTimeHigh, errorMsg) {
	var date1 = new Date($(root+':'+Fieldquesdateadded).value);
    var date2 = new Date($(root+':'+FieldqueseffectiveTimeHigh).value);
	var date4 = new Date($(root+':'+Fieldquesdob).value);
	
    var date3 = new Date();
   
    var errorObj = $(errorMsg);
   if ((date1!=null)&&(date2!=null))
   {
   	if ((date1 > date4)&& (date2 > date4))
   	{
       if(date1 > date2)
       {
           errorObj.innerHTML="Date of Expiry must be greater than Date Added ";
           return false; 
      }
      else if(date1 > date3)
       {
           errorObj.innerHTML="Added Date must be on or before Current Date";				
           return false; 
       }
       else if(date2 > date3) 
       {
           errorObj.innerHTML="Expiry Date must be on or before Current Date";				
	       return false; 
        }
         else
        {
        	errorObj.innerHTML="";				
	       return false; 
        }
    }
    else if ((date4 > date1)||(date4 > date2))
    {
    	errorObj.innerHTML="Date Added/Date of Expiry should be after Date of Birth";	
    	return false;
    }
     else
        {
        	errorObj.innerHTML="";				
	       return false; 
        }
   }
    
}

/**
 * Modified 'openTemplate' function to generate filtered pop-ups in analysis screens.
 * @author Pinky S
 * Added on 1/17/2011
 * @param contentName
 * @param placeholderid
 * @param methodName
 * @param formId
 * @param index
 * @param popupType
 * @param gridType
 */
function openTemplateWithFilteredItems(contentName, placeholderid, methodName, formId, index, popupType,gridType)
{
 var lform = $(formId);

 // Async submission should be stopped when its required to submit form explicitly.
 // For ex. In Add Diagnosis wizard the form is explicitly submitted upon selecting Diagnosis.
 // During this time Async form submission should be stopped until form is refreshed.

 // set true to Stop Async Submission
 // Should make sure its set back to false at appropriate time or else asyn submission would stop working completely in the session.
 stopAsync(formId);

 // Build  required paramters (concactenated with '|' ) that will be passed back to javascript Method Name
  var lArguments = new Array();
  	lArguments.push(formId);
  	lArguments.push(index);

  var methodArgsStr = buildArguments(lArguments);
  
  openPopupWithFilteredItems( contentName, placeholderid, formId,  methodName, methodArgsStr,gridType,popupType);
}

/**
 * Modified 'openPopup' function to generate filtered pop-ups in analysis screens.
 * @author Pinky S
 * Added on 1/17/2011
 * @param contentName
 * @param placeholderid
 * @param formId
 * @param methodName
 * @param methodArgs
 * @param gridType
 * @param popupType
 * @return
 */
function openPopupWithFilteredItems( contentName, placeholderid, formId, methodName, methodArgs,gridType,popupType){
	 serialNo++;
	// Tolven.Util.log( "Getting: " + contentName );
	 $('downloadStatus').innerHTML="Get " + contentName + "...";

	 // Update this block when ever a similar  new wizard is added.

	 // Update this block whenever a similar new wizard is added.
		new Ajax.Request(
		  'createGridWithFilteredItems.ajaxcchit',
		  {
			method: 'get',
			parameters: "element="+contentName+"&gridId="+placeholderid+"&gridType="+gridType+"&methodArgs="+methodArgs+"&methodName="+methodName+"&formId="+formId+"&popupType="+popupType,
			onSuccess: function(request){ setPopupContentFiltered(request,  placeholderid, formId ); },
			onFailure: function(request) {displayError(request,param);}
		  });
	}

/**
 * Modified 'setPopupContent' function to change the path of the close button image.
 * @author Pinky S
 * Added on 1/17/2011
 * @param req
 * @param placeholderid
 * @param formId
 * @return
 */
function setPopupContentFiltered( req, placeholderid, formId){
	 var prefHTML = "";
	  prefHTML += "<div class=\"popupgridheader\">";
	  prefHTML += "<img class='closetab' src='/Tolven/images/x_black.gif' onclick=\"closePopup('" + $(placeholderid).id + "','" + formId + "' );return false;\"/>&nbsp;" ;
	  prefHTML += "</div>";
	  prefHTML += req.responseText;

	 // This is required for Grid to filter and refresh data.
	 visiblePage = placeholderid;
	var popupElement = $(placeholderid);
	 $(popupElement).update(prefHTML);
	 popupElement.style.display = 'block';
	 popupElement.style.top = document.body.clientHeight * .30 + "px";
	 popupElement.style.left = document.body.clientWidth * .30 + "px";

	}

/**
 * Modified 'cui' function to hide the number of filtered items on the pop-ups in the analysis screens.
 * @author Pinky S
 * Added on 1/17/2011
 * @param element
 * @param value
 * @param id
 * @param methodName
 * @param methodArgs
 * @param menuPath
 */
function cuiFiltered(element, value, id, methodName, methodArgs,menuPath){
	if( $( id + "-filter").value == value ){
	 filterValueChangeFiltered(element, value, id ,methodName, methodArgs,menuPath);
	} else $(id).setAttribute('filterValue', $( id + "-filter").value );
}

/**
 * Modified 'checkUserInput' function to hide the number of filtered items on the pop-ups in the analysis screens.
 * @author Pinky S
 * Added on 1/17/2011
 * @param element
 * @param value
 * @param id
 * @param methodName
 * @param methodArgs
 * @param menuPath
 * @return
 */
function checkUserInputFiltered(element, value, id , methodName, methodArgs,menuPath){
	$(id).setAttribute('filterValue', value );
	setTimeout( "cuiFiltered('" + element + "', '" + value + "', '" + id+  "', '" + methodName + "', '" +  methodArgs + "')", 125);
}

/**
 * Modified 'filterValueChange' function to hide the number of filtered items on the pop-ups in the analysis screens.
 * @author Pinky S
 * Added on 1/17/2011
 * @param element
 * @param val
 * @param id
 * @param methodName
 * @param methodArgs
 * @param menuPath
 */
function filterValueChangeFiltered(element, val, id, methodName, methodArgs,menuPath) {
//Tolven.Util.log(val);
	if(!menuPath)
		menuPath = $(id).getAttribute('menuPath');

	var lg = liveGrids[id];
	var requestParams = new Array();
	$(id).setAttribute('filterValue', val );
	lg.setRequestParams( {name: 'element', value: menuPath}, {name: 'filter', value: val}, {name: 'methodName', value: methodName}, {name: 'methodArgs', value: methodArgs} );
//	lg.resetContents();
//	lg.requestContentRefresh(0);
	if (val) {
		var countMDAjax = new Ajax.Request(
			'countMenuData.ajaxi',
			{
				method: 'get',
				parameters: 'element='+menuPath+'&filter='+val,
				onSuccess: function(req) {countMDCompleteFiltered( id, req );}
			});
	} else {
		$(id+"-foot").innerHTML = "" ;
		var grid = $(id+'-grid');
		lg.setTotalRows( 1*grid.getAttribute('totalRows') );
		lg.requestContentRefresh(0);
	}
}

/**
 * Modified 'countMDComplete' function to hide the number of filtered items on the pop-ups in the analysis screens.
 * @author Pinky S
 * Added on 1/17/2011
 * @param rootId
 * @param request
 */
function countMDCompleteFiltered( rootId, request ) {
//	Tolven.Util.log( rootId + " " + request.responseText );
	$(rootId+"-foot").innerHTML = "";
	var lg = liveGrids[rootId];
	lg.setTotalRows( 1*request.responseText );
	lg.requestContentRefresh(0);
}

/**
 * Modified 'createGrid' function to generate filtered pop-ups in Diabetes-analysis screens.
 * @author Pinky S
 * Added on 1/17/2011
 * @param menuPath
 * @param id
 * @param methodName
 * @param methodArgs
 * @param gridType
 */
function createGridWithFilteredDiabetes( menuPath,id, methodName, methodArgs,gridType ) {
	//menuPath - the original path of the menu for which the grid is created
	//id - the html element id on the page
	//methodName - javascript method to be called for the click event on the grid items
	//methodArgs - javascript method arguments
	if(!id)
		id = menuPath;
	var root = $(id);
	var grid = $(id+'-grid');
	if (root.getAttribute( 'gridOffset' )==null) {
		root.setAttribute( 'gridOffset', grid.getAttribute( 'gridOffset'));
		root.setAttribute( 'gridSortCol', grid.getAttribute( 'gridSortCol'));
		root.setAttribute( 'gridSortDir', grid.getAttribute( 'gridSortDir'));
		root.setAttribute( 'filterValue', "");
		root.setAttribute( 'menuPath', menuPath);

//		root.setAttribute( 'gridPreset', grid.getAttribute( 'gridPreset' ));
	}

	var methodNameValue = methodName;
	var methodArgsValue = methodArgs;

	if (methodName == undefined)
	{
		methodNameValue = "";
	}

	if (methodArgs == undefined)
	{
		methodArgsValue = "";
	}

	liveGrids[id] = new Rico.LiveGrid( id+"-LG",
		1*grid.getAttribute('visibleRows'),
		1*grid.getAttribute('totalRows'),
		'menuDataWithFilteredDiabetes.ajaxcchit',
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
			requestParameters: [{name: 'element', value: menuPath},
								{name: 'filter', value: root.getAttribute('filterValue')},
								{name: 'methodName', value: methodNameValue},
								{name: 'methodArgs', value: methodArgsValue },
								{name: 'gridType', value: gridType }],
			sortAscendImg: '../images/sort_asc.gif',
			sortDescendImg:'../images/sort_desc.gif'
		});

	grid.style.width=($(id+'-LG_header').offsetWidth+19)*2+'px';
	grid.style.width=($(id+'-LG_header').offsetWidth+19)+'px';
	grid.style.border='#999999 solid 1px';
	$(id+"-filter").value=root.getAttribute('filterValue');
	new Form.Element.Observer( $(id+"-filter"), 1, function(element, val) {checkUserInputFiltered(element, val, id, methodNameValue, methodArgsValue,menuPath);} );
	if( $(id).getAttribute('filterValue') != null && $(id).getAttribute('filterValue') != "" ) {
	  filterValueChange($(id + "-filter"), $(id).getAttribute('filterValue'), id, methodNameValue, methodArgsValue ,menuPath );
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
 * Modified 'createGrid' function to generate filtered pop-ups in Diabetes-analysis screens.
 * @author Pinky S
 * Added on 1/17/2011
 * @param menuPath
 * @param id
 * @param methodName
 * @param methodArgs
 * @param gridType
 */
function createGridWithFilteredInfluenza( menuPath,id, methodName, methodArgs,gridType ) {
	//menuPath - the original path of the menu for which the grid is created
	//id - the html element id on the page
	//methodName - javascript method to be called for the click event on the grid items
	//methodArgs - javascript method arguments
	if(!id)
		id = menuPath;
	var root = $(id);
	var grid = $(id+'-grid');
	if (root.getAttribute( 'gridOffset' )==null) {
		root.setAttribute( 'gridOffset', grid.getAttribute( 'gridOffset'));
		root.setAttribute( 'gridSortCol', grid.getAttribute( 'gridSortCol'));
		root.setAttribute( 'gridSortDir', grid.getAttribute( 'gridSortDir'));
		root.setAttribute( 'filterValue', "");
		root.setAttribute( 'menuPath', menuPath);

//		root.setAttribute( 'gridPreset', grid.getAttribute( 'gridPreset' ));
	}

	var methodNameValue = methodName;
	var methodArgsValue = methodArgs;

	if (methodName == undefined)
	{
		methodNameValue = "";
	}

	if (methodArgs == undefined)
	{
		methodArgsValue = "";
	}

	liveGrids[id] = new Rico.LiveGrid( id+"-LG",
		1*grid.getAttribute('visibleRows'),
		1*grid.getAttribute('totalRows'),
		'menuDataWithFilteredInfluenza.ajaxcchit',
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
			requestParameters: [{name: 'element', value: menuPath},
								{name: 'filter', value: root.getAttribute('filterValue')},
								{name: 'methodName', value: methodNameValue},
								{name: 'methodArgs', value: methodArgsValue },
								{name: 'gridType', value: gridType }],
			sortAscendImg: '../images/sort_asc.gif',
			sortDescendImg:'../images/sort_desc.gif'
		});

	grid.style.width=($(id+'-LG_header').offsetWidth+19)*2+'px';
	grid.style.width=($(id+'-LG_header').offsetWidth+19)+'px';
	grid.style.border='#999999 solid 1px';
	$(id+"-filter").value=root.getAttribute('filterValue');
	new Form.Element.Observer( $(id+"-filter"), 1, function(element, val) {checkUserInputFiltered(element, val, id, methodNameValue, methodArgsValue,menuPath);} );
	if( $(id).getAttribute('filterValue') != null && $(id).getAttribute('filterValue') != "" ) {
	  filterValueChange($(id + "-filter"), $(id).getAttribute('filterValue'), id, methodNameValue, methodArgsValue ,menuPath );
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
 * Modified 'createGrid' function to generate filtered pop-ups in Hypertension-analysis screens.
 * @author Pinky S
 * Added on 1/17/2011
 * @param menuPath
 * @param id
 * @param methodName
 * @param methodArgs
 * @param gridType
 */
function createGridWithFilteredHypertension( menuPath,id, methodName, methodArgs,gridType ) {
	if(!id)
		id = menuPath;
	var root = $(id);
	var grid = $(id+'-grid');
	if (root.getAttribute( 'gridOffset' )==null) {
		root.setAttribute( 'gridOffset', grid.getAttribute( 'gridOffset'));
		root.setAttribute( 'gridSortCol', grid.getAttribute( 'gridSortCol'));
		root.setAttribute( 'gridSortDir', grid.getAttribute( 'gridSortDir'));
		root.setAttribute( 'filterValue', "");
		root.setAttribute( 'menuPath', menuPath);

//		root.setAttribute( 'gridPreset', grid.getAttribute( 'gridPreset' ));
	}

	var methodNameValue = methodName;
	var methodArgsValue = methodArgs;

	if (methodName == undefined)
	{
		methodNameValue = "";
	}

	if (methodArgs == undefined)
	{
		methodArgsValue = "";
	}

	liveGrids[id] = new Rico.LiveGrid( id+"-LG",
		1*grid.getAttribute('visibleRows'),
		1*grid.getAttribute('totalRows'),
		'menuDataWithFilteredHypertension.ajaxcchit',
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
			requestParameters: [{name: 'element', value: menuPath},
								{name: 'filter', value: root.getAttribute('filterValue')},
								{name: 'methodName', value: methodNameValue},
								{name: 'methodArgs', value: methodArgsValue },
								{name: 'gridType', value: gridType }],
			sortAscendImg: '../images/sort_asc.gif',
			sortDescendImg:'../images/sort_desc.gif'
		});

	grid.style.width=($(id+'-LG_header').offsetWidth+19)*2+'px';
	grid.style.width=($(id+'-LG_header').offsetWidth+19)+'px';
	grid.style.border='#999999 solid 1px';
	$(id+"-filter").value=root.getAttribute('filterValue');
	new Form.Element.Observer( $(id+"-filter"), 1, function(element, val) {checkUserInputFiltered(element, val, id, methodNameValue, methodArgsValue,menuPath);} );
	if( $(id).getAttribute('filterValue') != null && $(id).getAttribute('filterValue') != "" ) {
	  filterValueChange($(id + "-filter"), $(id).getAttribute('filterValue'), id, methodNameValue, methodArgsValue ,menuPath );
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
 * Modified 'createGrid' function to generate filtered pop-ups in Cholesterol-analysis screens.
 * @author Pinky S
 * Added on 1/17/2011
 * @param menuPath
 * @param id
 * @param methodName
 * @param methodArgs
 * @param gridType
 */
function createGridWithFilteredCholesterol( menuPath,id, methodName, methodArgs,gridType ) {
	if(!id)
		id = menuPath;
	var root = $(id);
	var grid = $(id+'-grid');
	if (root.getAttribute( 'gridOffset' )==null) {
		root.setAttribute( 'gridOffset', grid.getAttribute( 'gridOffset'));
		root.setAttribute( 'gridSortCol', grid.getAttribute( 'gridSortCol'));
		root.setAttribute( 'gridSortDir', grid.getAttribute( 'gridSortDir'));
		root.setAttribute( 'filterValue', "");
		root.setAttribute( 'menuPath', menuPath);

//		root.setAttribute( 'gridPreset', grid.getAttribute( 'gridPreset' ));
	}

	var methodNameValue = methodName;
	var methodArgsValue = methodArgs;

	if (methodName == undefined)
	{
		methodNameValue = "";
	}

	if (methodArgs == undefined)
	{
		methodArgsValue = "";
	}

	liveGrids[id] = new Rico.LiveGrid( id+"-LG",
		1*grid.getAttribute('visibleRows'),
		1*grid.getAttribute('totalRows'),
		'menuDataWithFilteredCholesterol.ajaxcchit',
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
			requestParameters: [{name: 'element', value: menuPath},
								{name: 'filter', value: root.getAttribute('filterValue')},
								{name: 'methodName', value: methodNameValue},
								{name: 'methodArgs', value: methodArgsValue },
								{name: 'gridType', value: gridType }],
			sortAscendImg: '../images/sort_asc.gif',
			sortDescendImg:'../images/sort_desc.gif'
		});

	grid.style.width=($(id+'-LG_header').offsetWidth+19)*2+'px';
	grid.style.width=($(id+'-LG_header').offsetWidth+19)+'px';
	grid.style.border='#999999 solid 1px';
	$(id+"-filter").value=root.getAttribute('filterValue');
	new Form.Element.Observer( $(id+"-filter"), 1, function(element, val) {checkUserInputFiltered(element, val, id, methodNameValue, methodArgsValue,menuPath);} );
	if( $(id).getAttribute('filterValue') != null && $(id).getAttribute('filterValue') != "" ) {
	  filterValueChange($(id + "-filter"), $(id).getAttribute('filterValue'), id, methodNameValue, methodArgsValue ,menuPath );
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
 * Modified 'createGrid' function to generate filtered pop-ups in Colorectal-analysis screens.
 * @author Pinky S
 * Added on 1/17/2011
 * @param menuPath
 * @param id
 * @param methodName
 * @param methodArgs
 * @param gridType
 */
function createGridWithFilteredColorectal( menuPath,id, methodName, methodArgs,gridType ) {
	if(!id)
		id = menuPath;
	var root = $(id);
	var grid = $(id+'-grid');
	if (root.getAttribute( 'gridOffset' )==null) {
		root.setAttribute( 'gridOffset', grid.getAttribute( 'gridOffset'));
		root.setAttribute( 'gridSortCol', grid.getAttribute( 'gridSortCol'));
		root.setAttribute( 'gridSortDir', grid.getAttribute( 'gridSortDir'));
		root.setAttribute( 'filterValue', "");
		root.setAttribute( 'menuPath', menuPath);

//		root.setAttribute( 'gridPreset', grid.getAttribute( 'gridPreset' ));
	}

	var methodNameValue = methodName;
	var methodArgsValue = methodArgs;

	if (methodName == undefined)
	{
		methodNameValue = "";
	}

	if (methodArgs == undefined)
	{
		methodArgsValue = "";
	}

	liveGrids[id] = new Rico.LiveGrid( id+"-LG",
		1*grid.getAttribute('visibleRows'),
		1*grid.getAttribute('totalRows'),
		'menuDataWithFilteredColorectal.ajaxcchit',
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
			requestParameters: [{name: 'element', value: menuPath},
								{name: 'filter', value: root.getAttribute('filterValue')},
								{name: 'methodName', value: methodNameValue},
								{name: 'methodArgs', value: methodArgsValue },
								{name: 'gridType', value: gridType }],
			sortAscendImg: '../images/sort_asc.gif',
			sortDescendImg:'../images/sort_desc.gif'
		});

	grid.style.width=($(id+'-LG_header').offsetWidth+19)*2+'px';
	grid.style.width=($(id+'-LG_header').offsetWidth+19)+'px';
	grid.style.border='#999999 solid 1px';
	$(id+"-filter").value=root.getAttribute('filterValue');
	new Form.Element.Observer( $(id+"-filter"), 1, function(element, val) {checkUserInputFiltered(element, val, id, methodNameValue, methodArgsValue,menuPath);} );
	if( $(id).getAttribute('filterValue') != null && $(id).getAttribute('filterValue') != "" ) {
	  filterValueChange($(id + "-filter"), $(id).getAttribute('filterValue'), id, methodNameValue, methodArgsValue ,menuPath );
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
 * Modified 'createGrid' function to generate filtered pop-ups in Mammography-analysis screens.
 * @author Pinky S
 * Added on 1/17/2011
 * @param menuPath
 * @param id
 * @param methodName
 * @param methodArgs
 * @param gridType
 */
function createGridWithFilteredMammography( menuPath,id, methodName, methodArgs,gridType ) {
	if(!id)
		id = menuPath;
	var root = $(id);
	var grid = $(id+'-grid');
	if (root.getAttribute( 'gridOffset' )==null) {
		root.setAttribute( 'gridOffset', grid.getAttribute( 'gridOffset'));
		root.setAttribute( 'gridSortCol', grid.getAttribute( 'gridSortCol'));
		root.setAttribute( 'gridSortDir', grid.getAttribute( 'gridSortDir'));
		root.setAttribute( 'filterValue', "");
		root.setAttribute( 'menuPath', menuPath);

//		root.setAttribute( 'gridPreset', grid.getAttribute( 'gridPreset' ));
	}

	var methodNameValue = methodName;
	var methodArgsValue = methodArgs;

	if (methodName == undefined)
	{
		methodNameValue = "";
	}

	if (methodArgs == undefined)
	{
		methodArgsValue = "";
	}

	liveGrids[id] = new Rico.LiveGrid( id+"-LG",
		1*grid.getAttribute('visibleRows'),
		1*grid.getAttribute('totalRows'),
		'menuDataWithFilteredMammography.ajaxcchit',
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
			requestParameters: [{name: 'element', value: menuPath},
								{name: 'filter', value: root.getAttribute('filterValue')},
								{name: 'methodName', value: methodNameValue},
								{name: 'methodArgs', value: methodArgsValue },
								{name: 'gridType', value: gridType }],
			sortAscendImg: '../images/sort_asc.gif',
			sortDescendImg:'../images/sort_desc.gif'
		});

	grid.style.width=($(id+'-LG_header').offsetWidth+19)*2+'px';
	grid.style.width=($(id+'-LG_header').offsetWidth+19)+'px';
	grid.style.border='#999999 solid 1px';
	$(id+"-filter").value=root.getAttribute('filterValue');
	new Form.Element.Observer( $(id+"-filter"), 1, function(element, val) {checkUserInputFiltered(element, val, id, methodNameValue, methodArgsValue,menuPath);} );
	if( $(id).getAttribute('filterValue') != null && $(id).getAttribute('filterValue') != "" ) {
	  filterValueChange($(id + "-filter"), $(id).getAttribute('filterValue'), id, methodNameValue, methodArgsValue ,menuPath );
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
 * Method to get the code from the pop-ups in the analysis screens.
 * @author Pinky S
 * Added on 1/19/2011
 * @param template
 * @param root
 * @param methodArgs
 */
wizGetCodeFromPopUp=function(template, root, methodArgs){
	var lArgs = splitArguments(methodArgs);
    var formId = lArgs[0];
    closePopup(root,formId); 
    var element=$(formId).getAttribute("tolvenid" );
    var param = "";
    param = '&element='+element+
            '&template='+template;
    wizShowAjaxLoader();
    var instAjax = new Ajax.Request(
        'getCodeFromPopUp.ajaxcchit', {
            method: 'get', 
            parameters: param,
            onComplete: function(req) {
                if (req.responseText.split("|")[0]=="Success") {
            		var code = req.responseText.split("|")[1];
            		if(lArgs[1]==0){
            			$(formId+':includeCode').value = code;
            		}
            		else if(lArgs[1]==1){
            			$(formId+':excludeCode').value = code;
            		}
                	wizHideAjaxLoader();
                }
                else {
                    wizHideAjaxLoader();
                }                  
            } 
	});
}

/**
 * Submits individual trims when multiple problems are submitted.
 * @param templateID
 * @param element
 * @author <unni.s@cyrusxp.com>
 * @since v0.0.21
 */
function submitUniqueTrim(templateID, element) {
	new Ajax.Request('submitUniqueTrim.ajaxcchit',
			{
				method: 'get',
				parameters: "templateID="+templateID+
							"&element="+element,
				onSuccess: function(response){
				},
				onFailure: function(reponse) {
					
				}
			});
}

/**
 * Submits separate problem trim when submitted from progressNotes trim.
 * @param element
 * @author <unni.s@cyrusxp.com>
 * @since v0.0.21
 */
function submitProblemTrim(element) {
	new Ajax.Request('submitProblem.ajaxcchit',
			{
				method: 'get',
				parameters: "element="+element,
				onSuccess: function(response){
				},
				onFailure: function(reponse) {
					
				}
	});
}

/**
 * Method to call 'findLastSnapShotDate.ajaxcchit'(which find the last 
 * snapshot date ),retrieve the date and shows it adjacent to the filtered items.
 * @author Pinky S
 * Added on 02/02/2011
 * @param element
 */
function findLastSnapShotDate(element){
	 var instAjax = new Ajax.Request(
		        'findLastSnapShotDate.ajaxcchit',
		        {
		           method: 'get',
		           parameters: 'element='+element,
		           onComplete: function(req) {
		           		if((req.responseText != "Failure") 
		           				&& (req.responseText.split("|")[1]!= "Failure")){
		                	value = req.responseText.split("|")[1];
		                	$(element+'-foot').innerHTML = " / Last Snapshot Date: "+value;
		                }else{
		                	$(element+'-foot').innerHTML = "";
		                }
		           },onFailure: function(req){
		                 $(element+'-foot').innerHTML = "";
		           }
			});
}

/**
 * Modified 'openTemplate' function  to generate image order pop-ups 
 * with a tool-tip to show the long name of orders.
 * @author Pinky S
 * Added on 1/17/2011
 * @param contentName
 * @param placeholderid
 * @param methodName
 * @param formId
 * @param index
 * @param popupType
 * @param gridType
 */
function openTemplateWithToolTip(contentName, placeholderid, methodName, formId, index,gridType)
{

 var lform = $(formId);

 // Async submission should be stopped when its required to submit form explicitly.
 // For ex. In Add Diagnosis wizard the form is explicitly submitted upon selecting Diagnosis.
 // During this time Async form submission should be stopped until form is refreshed.

 // set true to Stop Async Submission
 // Should make sure its set back to false at appropriate time or else asyn submission would stop working completely in the session.
 stopAsync(formId);

 // Build  required paramters (concactenated with '|' ) that will be passed back to javascript Method Name
  var lArguments = new Array();
  	lArguments.push(formId);
  	lArguments.push(index);

  var methodArgsStr = buildArguments(lArguments);
 openPopupWithToolTip( contentName, placeholderid, formId,  methodName, methodArgsStr,gridType);
}

/**
 * Modified 'openPopup' function to generate image order pop-ups 
 * with a tool-tip to show the long name of orders.
 * @author Pinky S
 * Added on 02/03/2011
 * @param contentName
 * @param placeholderid
 * @param formId
 * @param methodName
 * @param methodArgs
 * @param gridType
 * @param popupType
 * @return
 */
function openPopupWithToolTip( contentName, placeholderid, formId, methodName, methodArgs,gridType){
	 serialNo++;
	// Tolven.Util.log( "Getting: " + contentName );
	 $('downloadStatus').innerHTML="Get " + contentName + "...";

	 // Update this block when ever a similar  new wizard is added.

	 // Update this block whenever a similar new wizard is added.
		new Ajax.Request(
		  'createGridWithToolTip.ajaxcchit',
		  {
			method: 'get',
			parameters: "element="+contentName+"&gridId="+placeholderid+"&gridType="+gridType+"&methodArgs="+methodArgs+"&methodName="+methodName+"&formId="+formId,
			onSuccess: function(request){ setPopupContent(request,  placeholderid, formId ); },
			onFailure: function(request) {displayError(request,param);}
		  });
	}

/**
 * Modified 'createGrid' function to generate image order pop-ups 
 * with a tool-tip to show the long name of orders.
 * @author Pinky S
 * Added on 02/03/2011
 * @param menuPath
 * @param id
 * @param methodName
 * @param methodArgs
 * @param gridType
 */
function createGridWithToolTip( menuPath,id, methodName, methodArgs,gridType ) {
	if(!id)
		id = menuPath;
	var root = $(id);
	var grid = $(id+'-grid');
	if (root.getAttribute( 'gridOffset' )==null) {
		root.setAttribute( 'gridOffset', grid.getAttribute( 'gridOffset'));
		root.setAttribute( 'gridSortCol', grid.getAttribute( 'gridSortCol'));
		root.setAttribute( 'gridSortDir', grid.getAttribute( 'gridSortDir'));
		root.setAttribute( 'filterValue', "");
		root.setAttribute( 'menuPath', menuPath);

//		root.setAttribute( 'gridPreset', grid.getAttribute( 'gridPreset' ));
	}

	var methodNameValue = methodName;
	var methodArgsValue = methodArgs;

	if (methodName == undefined)
	{
		methodNameValue = "";
	}

	if (methodArgs == undefined)
	{
		methodArgsValue = "";
	}

	liveGrids[id] = new Rico.LiveGrid( id+"-LG",
		1*grid.getAttribute('visibleRows'),
		1*grid.getAttribute('totalRows'),
		'menuDataWithToolTip.ajaxcchit',
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
			requestParameters: [{name: 'element', value: menuPath},
								{name: 'filter', value: root.getAttribute('filterValue')},
								{name: 'methodName', value: methodNameValue},
								{name: 'methodArgs', value: methodArgsValue },
								{name: 'gridType', value: gridType }],
			sortAscendImg: '../images/sort_asc.gif',
			sortDescendImg:'../images/sort_desc.gif'
		});

	grid.style.width=($(id+'-LG_header').offsetWidth+19)*2+'px';
	grid.style.width=($(id+'-LG_header').offsetWidth+19)+'px';
	grid.style.border='#999999 solid 1px';
	$(id+"-filter").value=root.getAttribute('filterValue');
	new Form.Element.Observer( $(id+"-filter"), 1, function(element, val) {checkUserInput(element, val, id, methodNameValue, methodArgsValue,menuPath);} );
	if( $(id).getAttribute('filterValue') != null && $(id).getAttribute('filterValue') != "" ) {
	  filterValueChange($(id + "-filter"), $(id).getAttribute('filterValue'), id, methodNameValue, methodArgsValue ,menuPath );
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