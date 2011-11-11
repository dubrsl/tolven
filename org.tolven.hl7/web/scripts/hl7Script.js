
// HL7 Functions

function createHL7Message(path, outputId) {
	new Ajax.Request(
			'getHL7.ajaxhl7',
			{
				method: 'get',
				parameters: "&path="+path,
				onSuccess: function(request) { displayHL7Message(request.responseText,outputId); },
				onFailure: function(request) { displayError(request,param);}
			});
}

function displayHL7Message(response,outputId) {
	response="<pre>"+response+"</pre>";
	$(outputId).innerHTML = response;
}