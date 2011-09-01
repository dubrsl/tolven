
/**
 * Method to select the image order from the list and save as a relationship in trim.
 * @author Pinky
 * @param template
 * @param root
 * @param methodArgs
 */
saveImageOrder=function(template, root, methodArgs){
	var lArgs = splitArguments(methodArgs);
	var formId = lArgs[0];
	var rootForm = $(formId);
	$(formId + ":computeTemplate").value = template;
	$(formId + ":computeEnable").value = "true";
	$(formId + ":computeAction").value = 'add';
	var wipNode = rootForm.parentNode;
	var currentStep = 1 * rootForm.getAttribute('currentStep');
	ajaxSubmit4(rootForm, wipNode.id,currentStep);
	$(formId + ":computeEnable").value = "false";
}

/**
 * Modified 'openTemplate' functionto generate image order pop-ups 
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
function openImageOrderTemplateWithToolTip(contentName, placeholderid, methodName, formId, index, gridType){
	var lform = $(formId);

	// Async submission should be stopped when its required to submit form explicitly.
	// For ex. In Add Diagnosis wizard the form is explicitly submitted upon selecting Diagnosis.
	// During this time Async form submission should be stopped until form is refreshed.

	// set true to Stop Async Submission
	// Should make sure its set back to false at appropriate time or else asyn submission would stop working completely in the session.
	stopAsync(formId);

	// Build required paramters (concactenated with '|' ) that will be passed back to javascript Method Name
	var lArguments = new Array();
	lArguments.push(formId);
	lArguments.push(index);

	var methodArgsStr = buildArguments(lArguments);
	openImageOrderPopupWithToolTip(contentName, placeholderid, formId, methodName, methodArgsStr, gridType);
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
function openImageOrderPopupWithToolTip(contentName, placeholderid, formId, methodName, methodArgs, gridType){
	serialNo++;
	// Tolven.Util.log( "Getting: " + contentName );
	$('downloadStatus').innerHTML="Get " + contentName + "...";

	// Update this block whenever a similar new wizard is added.
	new Ajax.Request(
		'createGridWithToolTip.ajaxcchit',
		{
			method: 'get',
			parameters: "element="+contentName+"&gridId="+placeholderid+"&gridType="+gridType+"&methodArgs="+methodArgs+"&methodName="+methodName+"&formId="+formId,
			onSuccess: function(request){setPopupContent(request,placeholderid, formId);},
			onFailure: function(request){displayError(request,param);}
		});
}
