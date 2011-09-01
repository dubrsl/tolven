
/** **************** Common Functions ****************** **/
/**
 * To set table background color
 * @author Suja
 * added on 06/23/2010 
 */
setBackground = function (obj) {
	var cls = obj.getAttribute("class").split(" ")[0];
	obj.setAttribute("class", cls + " on");
};
/**
 * To reset table background color
 * @author Suja
 * added on 06/23/2010 
 */
resetBackground = function (obj) {
	var cls = obj.getAttribute("class").split(" ")[0];
	obj.setAttribute("class", cls);
};
/**
 * It is a common function used for display widget on 'Edit' option
 * @author Suja
 * added on 06/23/2010 
 */
wizEditWidget = function (root, widgetIndex) {
	var _saveWidget = $(root + ":save" + widgetIndex);
	var _editWidget = $(root + ":edit" + widgetIndex);
	$(_saveWidget).hide();
	$(_editWidget).show();
};
/**
 * Display the 'Saved' widget and hide the 'Add/Edit' widget
 * @author Suja
 * added on 06/23/2010 
 */
wizCancelUpdate = function (procIndex, root) {
	$(root + ":save" + procIndex).toggle();
	$(root + ":edit" + procIndex).toggle();
};
/**
 * To cancel widget
 * @author Suja
 * added on 06/23/2010 
 */
wizCancelWidget = function (widgetId, anchorId) {
	$(widgetId).hide();
	$(anchorId).show();
};
/**
 * It is a common function used for display new widget on 'Add New' event
 * @author Suja
 * added on 06/23/2010 
 */
wizAddWidget = function (widgetId, anchorId, root) {
	$(root + ":" + anchorId).hide();
	$(root + ":" + widgetId).show();
};
/**
 * To face an element
 */
var TimeToFade = 5000;
function fade(eid) {
	var element = document.getElementById(eid);
	if (element == null) {
		return;
	}
	element.style.display = "block";
	if (element.FadeState == null) {
		if (element.style.opacity == null || element.style.opacity == "" || element.style.opacity == "1") {
			element.FadeState = 2;
		} else {
			element.FadeState = -2;
		}
	}
	if (element.FadeState == 1 || element.FadeState == -1) {
		element.FadeState = element.FadeState == 1 ? -1 : 1;
		element.FadeTimeLeft = TimeToFade - element.FadeTimeLeft;
	} else {
		element.FadeState = element.FadeState == 2 ? -1 : 1;
		element.FadeTimeLeft = TimeToFade;
		setTimeout("animateFade(" + new Date().getTime() + ",'" + eid + "')", 33);
	}
}
/**
 * To animate fade object
 */
function animateFade(lastTick, eid) {
	var element = document.getElementById(eid);
	if (element != null) {
		element.FadeState = -1;
		element.style.display = "block";
		var curTick = new Date().getTime();
		var elapsedTicks = curTick - lastTick;
		if (element.FadeTimeLeft <= elapsedTicks) {
			element.style.opacity = element.FadeState == 1 ? "1" : "0";
			element.style.filter = "alpha(opacity = " + (element.FadeState == 1 ? "100" : "0") + ")";
			if (element.FadeState != 1) {
				element.style.display = "none";
			}
			element.FadeState = element.FadeState == 1 ? 2 : -2;
			return;
		}
		element.FadeTimeLeft -= elapsedTicks;
		var newOpVal = element.FadeTimeLeft / TimeToFade;
		if (element.FadeState == 1) {
			newOpVal = 1 - newOpVal;
		}
		element.style.opacity = newOpVal;
		element.style.filter = "alpha(opacity = " + (newOpVal * 100) + ")";
		setTimeout("animateFade(" + curTick + ",'" + eid + "')", 33);
	}
}
/** **************************************************** **/
/**
 * To show progress note template : Progress Note -> Plan tab
 * @author Suja
 * added on 06/22/2010 
 */
showPNTemplate = function (path, divId, template) {
	new Ajax.Updater(divId, "../wizard/" + template, {parameters:{element:path}, evalScripts:true, onComplete:function (req) {
        		wizHideAjaxLoader();
	}});
};
/**
 * To add progress note template : Progress Note -> Plan tab
 * @author Suja
 * added on 06/22/2010 
 */
addPNTemplate = function (title, contentName, methodName, formId, index, gridType) {
	$(formId + ":" + title + "ActionType").value = 0;
	$(formId + ":" + title + "WidgetIndex").value = "";
	openTemplate(contentName, contentName, methodName, formId, index, gridType);
};
/**
 * To edit progress note template : Progress Note -> Plan tab
 * @author Suja
 * added on 06/22/2010 
 */
editPNTemplate = function (title, widgetIndex, contentName, methodName, formId, index, gridType) {
	$(formId + ":" + title + "ActionType").value = 1;
	$(formId + ":" + title + "WidgetIndex").value = widgetIndex;
	openTemplate(contentName, contentName, methodName, formId, index, gridType);
};
/**
 * To remove template
 * @author Suja
 * added on 06/22/2010 
 */
wizRemoveTemplate = function (ajaxUrl, element, widgetIndex, divId, template) {
    wizShowAjaxLoader();
	var instAjax = new Ajax.Request(ajaxUrl, {method:"get", parameters:"actionType=2&element=" + element + "&widgetIndex=" + widgetIndex, onComplete:function (req) {
		showPNTemplate(element, divId, template);
	}});
};
/**
 * To save test orders : Progress Note -> Plan tab
 * @author Suja
 * added on 06/22/2010 
 */
wizSavePNTestOrders = function (template, root, methodArgs) {
    wizShowAjaxLoader();
	var lArgs = splitArguments(methodArgs);
	var formId = lArgs[0];
	closePopup(root, formId);
	var element = $(formId).getAttribute("tolvenid");
	var param = "";
	param = "actionType=" + $(formId + ":testOrderActionType").value + "&element=" + element + "&widgetIndex=" + $(formId + ":testOrderWidgetIndex").value + "&template=" + template;
	var instAjax = new Ajax.Request("managePNTestOrders.pnotes", {method:"get", parameters:param, onComplete:function (req) {
		if (req.responseText == "Success") {
			showPNTemplate(element, "pnTestOrderDiv", "tempPNTestOrders.jsf");
		} else {
                   wizHideAjaxLoader();
		}
	}});
};
/**
 * To save image orders : Progress Note -> Plan tab
 * @author Vineetha
 * added on 07/02/2010 
 */
wizSavePNImageOrders = function (template, root, methodArgs) {
    wizShowAjaxLoader();
	var lArgs = splitArguments(methodArgs);
	var formId = lArgs[0];
	closePopup(root, formId);
	var element = $(formId).getAttribute("tolvenid");
	var param = "";
	param = "actionType=" + $(formId + ":imageOrderActionType").value + "&element=" + element + "&widgetIndex=" + $(formId + ":imageOrderWidgetIndex").value + "&template=" + template;
	var instAjax = new Ajax.Request("managePNImageOrders.pnotes", {method:"get", parameters:param, onComplete:function (req) {
		if (req.responseText == "Success") {
			showPNTemplate(element, "pnImageOrderDiv", "tempPNImageOrders.jsf");
		} else {
                   wizHideAjaxLoader();
		}
	}});
};
/**
 * To save treatments : Progress Note -> Plan tab
 * @author Suja
 * added on 06/22/2010 
 */
wizSavePNTreatments = function (template, root, methodArgs) {
    wizShowAjaxLoader();
	var lArgs = splitArguments(methodArgs);
	var formId = lArgs[0];
	closePopup(root, formId);
	var element = $(formId).getAttribute("tolvenid");
	var param = "";
	param = "actionType=" + $(formId + ":treatmentActionType").value + "&element=" + element + "&widgetIndex=" + $(formId + ":treatmentWidgetIndex").value + "&template=" + template;
	var instAjax = new Ajax.Request("managePNTreatments.pnotes", {method:"get", parameters:param, onComplete:function (req) {
		if (req.responseText == "Success") {
			showPNTemplate(element, "pnTreatmentDiv", "tempPNTreatments.jsf");
		} else {
                    wizHideAjaxLoader();
		}
	}});
};
/**
 * To save referrals : Progress Note -> Plan tab
 * @author Suja
 * added on 06/22/2010 
 */
wizSavePNReferrals = function (template, root, methodArgs) {
    wizShowAjaxLoader();
	var lArgs = splitArguments(methodArgs);
	var formId = lArgs[0];
	closePopup(root, formId);
	var element = $(formId).getAttribute("tolvenid");
	var param = "";
	param = "actionType=" + $(formId + ":referralActionType").value + "&element=" + element + "&widgetIndex=" + $(formId + ":referralWidgetIndex").value + "&template=" + template;
	var instAjax = new Ajax.Request("managePNReferrals.pnotes", {method:"get", parameters:param, onComplete:function (req) {
		if (req.responseText == "Success") {
			showPNTemplate(element, "pnReferralDiv", "tempPNReferrals.jsf");
		} else {
                    wizHideAjaxLoader();
		}
	}});
};
/**
 * To remove appointment : Progress Note -> Plan tab -> Follow-up
 * @author Suja
 * added on 06/232/2010 
 */
wizRemovePNAppointment = function (element, root, widgetIndex) {
	$(root + ":saveFollowUp" + widgetIndex).remove();
    wizShowAjaxLoader();
	$("msgPNFollowUp").innerHTML = "";
	var instAjax = new Ajax.Request("managePNFollowUp.pnotes", {method:"get", parameters:"actionType=2&element=" + element + "&widgetIndex=" + widgetIndex, onComplete:function (req) {
		showPNTemplate(element, "pnFollowUpDiv", "tempPNFollowUp.jsf");
		$("msgPNFollowUp").innerHTML = "Follow-up has been removed ...";
		fade("msgPNFollowUp");
	}});
};
/**
 * To save appointment : Progress Note -> Plan tab -> Follow-up
 * @author Suja
 * added on 06/232/2010 
 */
wizSavePNAppointment = function (widgetId, anchorId, element, root, actionType, widgetIndex) {
	var rootForm = $(root);
	var currentStep = 1 * rootForm.getAttribute("currentStep");
	var lastStep = 1 * rootForm.getAttribute("lastStep");
	var appointmentDate = $("FieldquesappDate" + widgetIndex).value;
	var staff = $("attender" + widgetIndex).value;
	var location = $("location" + widgetIndex).value;
	var _params = "";
	if (appointmentDate != "") {
		_params += "&appointmentDate=" + appointmentDate;
	} else {
		$("msgPNFollowUp").innerHTML = "Please enter date ...";
		fade("msgPNFollowUp");
		return;
	}
	if (staff != "") {
		_params += "&staff=" + staff;
	}
	if (location != "") {
		_params += "&location=" + location;
	}
	var param = "";
	if (actionType == 0) {
		param = "actionType=0&element=" + element + _params;
	} else {
		param = "actionType=1&element=" + element + "&widgetIndex=" + widgetIndex + _params;
	} 
    wizShowAjaxLoader();
	$("msgPNFollowUp").innerHTML = "";
	var instAjax = new Ajax.Request("managePNFollowUp.pnotes", {method:"get", parameters:param, onComplete:function (req) {
		if (req.responseText == "Success") {
			showPNTemplate(element, "pnFollowUpDiv", "tempPNFollowUp.jsf");
			if (actionType == 0) {
				$("msgPNFollowUp").innerHTML = "Follow-up has been created ...";
			} else {
				$("msgPNFollowUp").innerHTML = "Follow-up has been updated ...";
			}
			fade("msgPNFollowUp");
		} else {
			$("msgPNFollowUp").innerHTML = req.responseText;
                    wizHideAjaxLoader();
		}
	}});
	$(root + ":" + widgetId + widgetIndex).hide();
	$(root + ":" + anchorId + widgetIndex).show();
};
/**
 * To save problem : Progress Note -> Subjective tab
 * @author Suja
 * added on 06/24/2010 
 */
wizSavePNProblem = function (template, root, methodArgs) {
   	wizShowAjaxLoader();
	var lArgs = splitArguments(methodArgs);
	var formId = lArgs[0];
	closePopup(root, formId);
	var element = $(formId).getAttribute("tolvenid");
	var param = "";
	param = "actionType=" + $(formId + ":problemActionType").value + "&element=" + element + "&widgetIndex=" + $(formId + ":problemWidgetIndex").value + "&template=" + template;
	if ($(formId + ":problemActionType").value == 1) {
		if ($("addToList" + $(formId + ":problemWidgetIndex").value).checked == true) {
			param += "&addToList=Yes";
		}
		if ($("FieldquesonsetDate" + $(formId + ":problemWidgetIndex").value).value != "") {
			param += "&onsetDate=" + $("FieldquesonsetDate" + $(formId + ":problemWidgetIndex").value).value;
		}
		if ($("siteofProblem" + $(formId + ":problemWidgetIndex").value).value != "") {
			param += "&siteofProblem=" + $("siteofProblem" + $(formId + ":problemWidgetIndex").value).value;
		}
		if ($("severity_save" + $(formId + ":problemWidgetIndex").value).value != "") {
			param += "&severity_edit=" + $("severity_save" + $(formId + ":problemWidgetIndex").value).value;
		}
		if ($("course_save" + $(formId + ":problemWidgetIndex").value).value != "") {
			param += "&course_edit=" + $("course_save" + $(formId + ":problemWidgetIndex").value).value;
		}
		if ($("outcome_save" + $(formId + ":problemWidgetIndex").value).value != "") {
			param += "&outcome_edit=" + $("outcome_save" + $(formId + ":problemWidgetIndex").value).value;
		}
		if ($("treatment_save" + $(formId + ":problemWidgetIndex").value).value != "") {
			param += "&treatment_edit=" + $("treatment_save" + $(formId + ":problemWidgetIndex").value).value;
		}
		if ($("comments_save" + $(formId + ":problemWidgetIndex").value).value != "") {
			param += "&comments_edit=" + $("comments_save" + $(formId + ":problemWidgetIndex").value).value;
		}
	}
	var instAjax = new Ajax.Request("managePNProblems.pnotes", {method:"get", parameters:param, onComplete:function (req) {
		if (req.responseText == "Success") {
			showPNTemplate(element, "pnProblemDiv", "tempPNProblems.jsf");
			showPNTemplate(element, "pnAssProblemDiv", "tempPNAssessmentProblems.jsf");
		} else {
                   wizHideAjaxLoader();
		}
	}});
};
/**
 * To update problem : Progress Note -> Subjective tab
 * @author Suja
 * added on 06/24/2010 
 */
wizUpdatePNProblem = function (element, root, widgetIndex, actionId, addToList) {
	var rootForm = $(root);
	var onsetDate = $("FieldquesonsetDate" + widgetIndex).value;
	var _params = "";
	var siteofProblem = $("siteofProblem" + widgetIndex).value;
	var severity_save = document.getElementById("severity_save" + widgetIndex).options[document.getElementById("severity_save" + widgetIndex).selectedIndex].text;
	var course_save = document.getElementById("course_save" + widgetIndex).options[document.getElementById("course_save" + widgetIndex).selectedIndex].text;
	var outcome_save = document.getElementById("outcome_save" + widgetIndex).options[document.getElementById("outcome_save" + widgetIndex).selectedIndex].text;
	var treatment_save = $("treatment_save" + widgetIndex).value;
	var comments_save = $("comments_save" + widgetIndex).value;
	if (actionId == 0 && onsetDate != "") {
		_params += "&onsetDate=" + onsetDate;
	}
	if (actionId == 1 && addToList == true) {
		_params += "&addToList=Yes";
	}
	if (siteofProblem != "") {
		_params += "&siteofProblem=" + siteofProblem;
	}
	if (severity_save != "") {
		_params += "&severity_save=" + severity_save;
	}
	if (course_save != "") {
		_params += "&course_save=" + course_save;
	}
	if (outcome_save != "") {
		_params += "&outcome_save=" + outcome_save;
	}
	if (treatment_save != "") {
		_params += "&treatment_save=" + treatment_save;
	}
	if (comments_save != "") {
		_params += "&comments_save=" + comments_save;
	}
	var param = "actionType=" + actionId + "&element=" + element + "&widgetIndex=" + widgetIndex + _params;
    wizShowAjaxLoader();
	var instAjax = new Ajax.Request("updatePNProblem.pnotes", {method:"get", parameters:param, onComplete:function (req) {
		if (req.responseText == "Success") {
			showPNTemplate(element, "pnProblemDiv", "tempPNProblems.jsf");
		} else {
                    wizHideAjaxLoader();
		}
	}});
};
/**
 * To save symptom : Progress Note -> Subjective tab
 * @author Suja
 * added on 06/25/2010 
 */
wizSavePNSymptom = function (template, root, methodArgs) {
    wizShowAjaxLoader();
	var lArgs = splitArguments(methodArgs);
	var formId = lArgs[0];
	closePopup(root, formId);
	var element = $(formId).getAttribute("tolvenid");
	var param = "";
	param = "actionType=" + $(formId + ":symptomActionType").value + "&element=" + element + "&widgetIndex=" + $(formId + ":symptomWidgetIndex").value + "&template=" + template;
	if ($(formId + ":symptomActionType").value == 1) {
		if ($("addSymptomToList" + $(formId + ":symptomWidgetIndex").value).checked == true) {
			param += "&addToList=Yes";
		}
		if ($("FieldquesonsetSymptomDate" + $(formId + ":symptomWidgetIndex").value).value != "") {
			param += "&onsetDate=" + $("FieldquesonsetSymptomDate" + $(formId + ":symptomWidgetIndex").value).value;
		}
	}
	var instAjax = new Ajax.Request("managePNSymptoms.pnotes", {method:"get", parameters:param, onComplete:function (req) {
		if (req.responseText == "Success") {
			showPNTemplate(element, "pnSymptomDiv", "tempPNSymptoms.jsf");
		} else {
                    wizHideAjaxLoader();
		}
	}});
};
/**
 * To update symptom : Progress Note -> Subjective tab
 * @author Suja
 * added on 06/25/2010 
 */
wizUpdatePNSymptom = function (element, root, widgetIndex, actionId, addToList) {
	var rootForm = $(root);
	var onsetDate = $("FieldquesonsetSymptomDate" + widgetIndex).value;
	var _params = "";
	if (actionId == 0 && onsetDate != "") {
		_params += "&onsetDate=" + onsetDate;
	}
	if (actionId == 1 && addToList == true) {
		_params += "&addToList=Yes";
	}
	var param = "actionType=" + actionId + "&element=" + element + "&widgetIndex=" + widgetIndex + _params;
    wizShowAjaxLoader();
	var instAjax = new Ajax.Request("updatePNSymptom.pnotes", {method:"get", parameters:param, onComplete:function (req) {
		if (req.responseText == "Success") {
			showPNTemplate(element, "pnSymptomDiv", "tempPNSymptoms.jsf");
		} else {
                    wizHideAjaxLoader();
		}
	}});
};
/**
 * To save diagnoses : Progress Note -> Assessment tab
 * @author Suja
 * added on 06/25/2010 
 */
wizSavePNDiagnoses = function (template, root, methodArgs) {
    wizShowAjaxLoader();
	var lArgs = splitArguments(methodArgs);
	var formId = lArgs[0];
	closePopup(root, formId);
	var element = $(formId).getAttribute("tolvenid");
	var param = "";
	param = "actionType=" + $(formId + ":diagnosisActionType").value + "&element=" + element + "&widgetIndex=" + $(formId + ":diagnosisWidgetIndex").value + "&template=" + template;
	var instAjax = new Ajax.Request("managePNDiagnoses.pnotes", {method:"get", parameters:param, onComplete:function (req) {
		if (req.responseText == "Success") {
			showPNTemplate(element, "pnDiagnosisDiv", "tempPNDiagnoses.jsf");
		} else {
                    wizHideAjaxLoader();
		}
	}});
};
/**
 * To Generate Progress Note Report
 * @author Suja
 * added on 06/25/2010 
 */
wizGenerateProgressNoteReport = function (element) {
	var herfPath = "../drilldown/progressnoteReport.jsf?element=" + element + "&RenderOutputType=pdf";
	window.open(herfPath, "_blank");
};

/**
 * To edit progress note template : Progress Note -> Problem
 * @author Pinky
 * added on 07/29/2010 
 */
editPNTemplateProblem = function (title, widgetIndex, contentName, methodName, formId, index, gridType) {
	$(formId + ":" + title + "ActionType").value = 1;
	$(formId + ":" + title + "WidgetIndex").value = widgetIndex;
	$(formId + ":saveProblem"+widgetIndex).hide();
    $(formId + ":editProblem"+widgetIndex).show();
    $("tempProblemActionType"+widgetIndex).value = 1;
};

/**
 * To cancel progress note template : Progress Note -> Problem
 * @author Pinky
 * added on 07/29/2010 
 */
wizCancelTemplateProblem = function (ajaxUrl, element, widgetIndex, divId, template) {
	if($('tempProblemActionType'+widgetIndex).value == 0){
    wizShowAjaxLoader();
	var instAjax = new Ajax.Request(ajaxUrl, {method:"get", parameters:"actionType=2&element=" + element + "&widgetIndex=" + widgetIndex, onComplete:function (req) {
		showPNTemplate(element, divId, template);
	}});
	}
	else
		showPNTemplate(element, divId, template);
};
/**
 * To Submit Observation Trims
 * @author Sandheep
 * added on 10/15/2010
 */
wizSaveObservationTrims=function(element) {
    var instAjax = new Ajax.Request(
       'saveObservationTrims.pnotes', {
           method: 'get', 
           parameters: 'element='+element, 
           onComplete: function(req) {  } 
	});
}

/**
 * Validate associate with encounter drop-down field in progress Note wizard
 * @author Pinky
 * added on 10/18/2010
 */
progressNoteEncounterValidations = function(root){
	if($(root+':associateEncounters').selectedIndex == 0){
		$('errorDetailDiv').style.display = 'block';
		$('progressNoteSummaryDiv').style.display = 'none';
		$('errorassociateEncounters').innerHTML = "Validation Error: Encounter is required.";
		$(root + "submitButton").disabled = true;
	}
	else{
		$('progressNoteSummaryDiv').style.display = 'block';
		$('errorDetailDiv').style.display = 'none';
		$('errorassociateEncounters').innerHTML = "";
		$(root + "submitButton").disabled = false;
	}
}

/**
 * To edit progress note template : Progress Note -> Diagnosis
 * @author Pinky
 * added on 10/19/2010 
 */
editPNTemplateDiagnosis = function (title, widgetIndex, contentName, methodName, formId, index, gridType) {
	$(formId + ":" + title + "ActionType").value = 1;
	$(formId + ":" + title + "WidgetIndex").value = widgetIndex;
	$(formId + ":saveDiagnosis"+widgetIndex).hide();
    $(formId + ":editDiagnosis"+widgetIndex).show();
    $("tempDiagnosisActionType"+widgetIndex).value = 1;
};

/**
 * To cancel progress note template : Progress Note -> Diagnosis
 * @author Pinky
 * added on 10/19/2010 
 */
wizCancelTemplateDiagnosis = function (ajaxUrl, element, widgetIndex, divId, template) {
	if($('tempDiagnosisActionType'+widgetIndex).value == 0){
	    wizShowAjaxLoader();
		var instAjax = new Ajax.Request(ajaxUrl, {method:"get", parameters:"actionType=2&element=" + element + "&widgetIndex=" + widgetIndex, onComplete:function (req) {
			showPNTemplate(element, divId, template);
		}});
	}
	else
		showPNTemplate(element, divId, template);
};

/**
 * To update diagnosis : Progress Note -> Assessment tab ->Diagnosis
 * @author Pinky
 * added on 10/19/2010 
 */
wizUpdatePNDiagnosis = function (element, root, widgetIndex, actionId, addToList) {
	var rootForm = $(root);
	var onsetDate = $("FieldqueseffectiveDate" + widgetIndex).value;
	var _params = "";
	if (actionId == 0 && onsetDate != "") {
		_params += "&onsetDate=" + onsetDate;
	}
	var param = "actionType=" + actionId + "&element=" + element + "&widgetIndex=" + widgetIndex + _params;
    wizShowAjaxLoader();
	var instAjax = new Ajax.Request("updatePNDiagnoses.pnotes", {method:"get", parameters:param, onComplete:function (req) {
		if (req.responseText == "Success") {
			showPNTemplate(element, "pnDiagnosisDiv", "tempPNDiagnoses.jsf");
		} else {
                    wizHideAjaxLoader();
		}
	}});
};

/**
 * To edit progress note template : Progress Note -> Treatments
 * @author Pinky
 * added on 10/19/2010 
 */
editPNTemplateTreatments = function (title, widgetIndex, contentName, methodName, formId, index, gridType) {
	$(formId + ":" + title + "ActionType").value = 1;
	$(formId + ":" + title + "WidgetIndex").value = widgetIndex;
	$(formId + ":saveTreatments"+widgetIndex).hide();
    $(formId + ":editTreatments"+widgetIndex).show();
    $("tempTreatmentsActionType"+widgetIndex).value = 1;
};

/**
 * To cancel progress note template : Progress Note -> Treatments
 * @author Pinky
 * added on 10/19/2010 
 */
wizCancelTemplateTreatments = function (ajaxUrl, element, widgetIndex, divId, template) {
	if($('tempTreatmentsActionType'+widgetIndex).value == 0){
	    wizShowAjaxLoader();
		var instAjax = new Ajax.Request(ajaxUrl, {method:"get", parameters:"actionType=2&element=" + element + "&widgetIndex=" + widgetIndex, onComplete:function (req) {
			showPNTemplate(element, divId, template);
		}});
	}
	else
		showPNTemplate(element, divId, template);
};

/**
 * To update treatments : Progress Note -> Plan tab ->Treatments
 * @author Pinky
 * added on 10/19/2010 
 */
wizUpdatePNTreatments = function (element, root, widgetIndex, actionId, addToList) {
	var rootForm = $(root);
	var onsetDate = $("FieldqueseffectiveTreatmentDate" + widgetIndex).value;
	var _params = "";
	if (actionId == 0 && onsetDate != "") {
		_params += "&onsetDate=" + onsetDate;
	}
	var param = "actionType=" + actionId + "&element=" + element + "&widgetIndex=" + widgetIndex + _params;
    wizShowAjaxLoader();
	var instAjax = new Ajax.Request("updatePNTreatments.pnotes", {method:"get", parameters:param, onComplete:function (req) {
		if (req.responseText == "Success") {
			showPNTemplate(element, "pnTreatmentDiv", "tempPNTreatments.jsf");
		} else {
                    wizHideAjaxLoader();
		}
	}});
};

/**
 * To edit progress note template : Progress Note -> Referrals
 * @author Pinky
 * added on 10/20/2010 
 */
editPNTemplateReferrals = function (title, widgetIndex, contentName, methodName, formId, index, gridType) {
	$(formId + ":" + title + "ActionType").value = 1;
	$(formId + ":" + title + "WidgetIndex").value = widgetIndex;
	$(formId + ":saveReferrals"+widgetIndex).hide();
    $(formId + ":editReferrals"+widgetIndex).show();
    $("tempReferralsActionType"+widgetIndex).value = 1;
};

/**
 * To cancel progress note template : Progress Note -> Referrals
 * @author Pinky
 * added on 10/20/2010 
 */
wizCancelTemplateReferrals = function (ajaxUrl, element, widgetIndex, divId, template) {
	if($('tempReferralsActionType'+widgetIndex).value == 0){
	    wizShowAjaxLoader();
		var instAjax = new Ajax.Request(ajaxUrl, {method:"get", parameters:"actionType=2&element=" + element + "&widgetIndex=" + widgetIndex, onComplete:function (req) {
			showPNTemplate(element, divId, template);
		}});
	}
	else
		showPNTemplate(element, divId, template);
};

/**
 * To update Referrals : Progress Note -> Plan tab ->Referrals
 * @author Pinky
 * added on 10/20/2010 
 */
wizUpdatePNReferrals = function (element, root, widgetIndex, actionId, addToList) {
	var rootForm = $(root);
	var onsetDate = $("FieldqueseffectiveReferralDate" + widgetIndex).value;
	var _params = "";
	if (actionId == 0 && onsetDate != "") {
		_params += "&onsetDate=" + onsetDate;
	}
	var param = "actionType=" + actionId + "&element=" + element + "&widgetIndex=" + widgetIndex + _params;
    wizShowAjaxLoader();
	var instAjax = new Ajax.Request("updatePNReferrals.pnotes", {method:"get", parameters:param, onComplete:function (req) {
		if (req.responseText == "Success") {
			showPNTemplate(element, "pnReferralDiv", "tempPNReferrals.jsf");
		} else {
                    wizHideAjaxLoader();
		}
	}});
};

/**
 * To edit progress note template : Progress Note -> Image Orders
 * @author Pinky
 * added on 10/20/2010 
 */
editPNTemplateImageOrders = function (title, widgetIndex, contentName, methodName, formId, index, gridType) {
	$(formId + ":" + title + "ActionType").value = 1;
	$(formId + ":" + title + "WidgetIndex").value = widgetIndex;
	$(formId + ":saveImageOrders"+widgetIndex).hide();
    $(formId + ":editImageOrders"+widgetIndex).show();
    $("tempImageOrdersActionType"+widgetIndex).value = 1;
};

/**
 * To cancel progress note template : Progress Note -> Image Orders
 * @author Pinky
 * added on 10/20/2010 
 */
wizCancelTemplateImageOrders = function (ajaxUrl, element, widgetIndex, divId, template) {
	if($('tempImageOrdersActionType'+widgetIndex).value == 0){
	    wizShowAjaxLoader();
		var instAjax = new Ajax.Request(ajaxUrl, {method:"get", parameters:"actionType=2&element=" + element + "&widgetIndex=" + widgetIndex, onComplete:function (req) {
			showPNTemplate(element, divId, template);
		}});
	}
	else
		showPNTemplate(element, divId, template);
};

/**
 * To update Image Orders : Progress Note -> Plan tab ->Image Orders
 * @author Pinky
 * added on 10/20/2010 
 */
wizUpdatePNImageOrders = function (element, root, widgetIndex, actionId, addToList) {
	var rootForm = $(root);
	var onsetDate = $("FieldqueseffectiveImageOrderDate" + widgetIndex).value;
	var _params = "";
	if (actionId == 0 && onsetDate != "") {
		_params += "&onsetDate=" + onsetDate;
	}
	var param = "actionType=" + actionId + "&element=" + element + "&widgetIndex=" + widgetIndex + _params;
    wizShowAjaxLoader();
	var instAjax = new Ajax.Request("updatePNImageOrders.pnotes", {method:"get", parameters:param, onComplete:function (req) {
		if (req.responseText == "Success") {
			showPNTemplate(element, "pnImageOrderDiv", "tempPNImageOrders.jsf");
		} else {
                    wizHideAjaxLoader();
		}
	}});
};

/**
 * To edit progress note template : Progress Note -> Lab Orders
 * @author Pinky
 * added on 10/20/2010 
 */
editPNTemplateLabOrders = function (title, widgetIndex, contentName, methodName, formId, index, gridType) {
	$(formId + ":" + title + "ActionType").value = 1;
	$(formId + ":" + title + "WidgetIndex").value = widgetIndex;
	$(formId + ":saveLabOrders"+widgetIndex).hide();
    $(formId + ":editLabOrders"+widgetIndex).show();
    $("tempLabOrdersActionType"+widgetIndex).value = 1;
};

/**
 * To cancel progress note template : Progress Note -> Lab Orders
 * @author Pinky
 * added on 10/20/2010 
 */
wizCancelTemplateLabOrders = function (ajaxUrl, element, widgetIndex, divId, template) {
	if($('tempLabOrdersActionType'+widgetIndex).value == 0){
	    wizShowAjaxLoader();
		var instAjax = new Ajax.Request(ajaxUrl, {method:"get", parameters:"actionType=2&element=" + element + "&widgetIndex=" + widgetIndex, onComplete:function (req) {
			showPNTemplate(element, divId, template);
		}});
	}
	else
		showPNTemplate(element, divId, template);
};

/**
 * To update Lab Orders : Progress Note -> Plan tab ->Lab Orders
 * @author Pinky
 * added on 10/20/2010 
 */
wizUpdatePNLabOrders = function (element, root, widgetIndex, actionId, addToList) {
	var rootForm = $(root);
	var onsetDate = $("FieldqueseffectiveLabOrderDate" + widgetIndex).value;
	var _params = "";
	if (actionId == 0 && onsetDate != "") {
		_params += "&onsetDate=" + onsetDate;
	}
	var param = "actionType=" + actionId + "&element=" + element + "&widgetIndex=" + widgetIndex + _params;
    wizShowAjaxLoader();
	var instAjax = new Ajax.Request("updatePNLabOrders.pnotes", {method:"get", parameters:param, onComplete:function (req) {
		if (req.responseText == "Success") {
			showPNTemplate(element, "pnTestOrderDiv", "tempPNTestOrders.jsf");
		} else {
                    wizHideAjaxLoader();
		}
	}});
};
