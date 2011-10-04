// Choice lists we've got locally
choiceList = new Array();



Element.addMethods( {
    gt: function(element, value ){
  var answer = element.getAnswer();
  if (answer > value) return true;
  return false;
 },
    lt: function(element, value ){
  var answer = element.getAnswer();
  if (answer < value) return true;
  return false;
 },
    ge: function(element, value ){
  var answer = element.getAnswer();
  if (answer >= value) return true;
  return false;
 },
    le: function(element, value ){
  var answer = element.getAnswer();
  if (answer <= value) return true;
  return false;
 },
    eq: function(element, value ){
  var answer = element.getAnswer();
  if (answer == value) return true;
  return false;
 },
    ne: function(element, value ){
  var answer = element.getAnswer();
  if (answer != value) return true;
  return false;
 },
    is: function(element, value ){
  var answer = element.getAnswer();
  if (answer == value) return true;
  return false;
 },
    isTrue: function(element ){
  var answer = element.getAnswer();
  if (answer == 'true') return true;
  if (answer == true) return true;
  return false;
 },
    isFalse: function(element ){
  var answer = element.getAnswer();
  if (answer == 'false') return true;
  if (answer == false) return true;
  return false;
 },
    isAnswered: function(element ){
  var answer = element.getAnswer();
  if (answer != null) return true;
  return false;
 },
    isNotAnswered: function(element ){
  var answer = element.getAnswer();
  if (answer == null) return true;
  return false;
 },
    isNotFalse: function(element ){
  var answer = element.getAnswer();
  if (answer == null) return true;
  if (answer == 'true') return true;
  if (answer == true) return true;
  return false;
 },
    isNotTrue: function(element ){
  var answer = element.getAnswer();
  if (answer == null) return true;
  if (answer == 'false') return true;
  if (answer == false) return true;
  return false;
 },
    getAnswer: function(element ){
  if (element.className!='question') alert( 'answers must be from a question' );
  // Look through question for the answer
  var elements = element.descendants();
  for (var x = 0; x < elements.length; x++) {
//   Tolven.Util.log( 'Element: ' + elements[x].type);
   if (elements[x].nodeType==1) {
    if (elements[x].type=='checkbox') {
//     Tolven.Util.log( 'check checked' );
     return  elements[x].checked;
    }
    if (elements[x].type=='radio' && elements[x].checked) {
//     Tolven.Util.log( 'radio: ' + elements[x].value );
     return elements[x].value;
    }
    if (elements[x].type=='hidden' ||
        elements[x].type=='text' ||
     elements[x].type=='password' ||
     elements[x].type=='textarea') {
     return elements[x].value;
    }
   }
  }
  return null;
 },
    checkVis: function(element ){
  var element = $(element);
  var elements = element.descendants();
  for (var x = 0; x < elements.length; x++) {
   var vis = elements[x].getAttribute('tvisible');
   if (vis!=null && vis !="") {
    var visVal = eval(vis);
    if (visVal != null ) {
     if (elements[x].className=='step') enableStep( elements[x], visVal );
     else if (visVal) elements[x].show();
     else elements[x].hide();

    }
   }
  }
        return element;
    },
     checkDisabled: function(element ){
    var element = $(element);
    var elements = element.descendants();
    for (var x = 0; x < elements.length; x++) {
      var vis = elements[x].getAttribute('evalFunc');
      if (vis!=null && vis !="") {
        var visVal = eval(vis);
      }
    }
        return element;
    }
});

function makeHeading( prefix, stepNumber, title ) {
 return '<span style="display:none" id="' + prefix + 'step' + stepNumber + ':head">Step ' + stepNumber + ' - <strong>' + title + '</strong></span>';
}

function makeStep( prefix, stepNumber, stepElement ) {
 var title = stepElement.getAttribute('wizTitle');
 stepElement.id = prefix+ 'step' + stepNumber;
 stepElement.hide();
 stepElement.setAttribute("root", prefix);
 stepElement.setAttribute("stepNo", stepNumber);
 stepElement.setAttribute('inHeader','true');
 var icon = prefix + 'step' + stepNumber + ':sel';
   stepElement.setAttribute("icon", icon);
 var help = stepElement.getElementsByClassName( "help");
 if (help.length > 0) {
  help[0].id = prefix + 'help' + (stepNumber+1);
  $(help[0]).hide();
 }
 setupWizStepQuestions( prefix, stepElement );
 var stepObj = '<td>';
 stepObj += '<li><a title="' + title + '" id="' + prefix + 'step' + stepNumber + ':sel" class="inactive" href="javascript:showStep(\'' + prefix + '\', ' + stepNumber + ' );"><div>' + title + '</div></a></li>';
 stepObj += '</td>';
 return stepObj;
}

// Cancel and destroy the current wizard, first on the server, then on the browser.
function wizCancel( element ) {
 if (confirm( "Completely cancel and remove?" )) {
  var myAjax = new Ajax.Request(
    'wizCancel.ajaxi',
    {
     method: 'get',
     parameters: 'element='+element,
             onFailure: function(request) {displayError(request,template);},
     onSuccess: wizCancelDone
    });
 }
}

function wizCancelDone( request ) {
// Tolven.Util.log( "response: " + request.responseText );
 closeTab(request.responseText);
}

/**
* Modified to disable submit button once clicked and enable agai when submit fails
* modified on 08/12/2010 by Valsaraj
*/
// Submit specified element (nothing is uploaded, which will already be done
function wizSubmit( root, password ) {
// Tolven.Util.log( "Submit: " + element );
 $(root + "submitButton").disabled = true;
 var element = $(root).getAttribute("tolvenid" );
 var passwordParam = "";
 if (password) {
  passwordParam = '&password='+password.value;
 }
// Tolven.Util.log( "Submit: " + element  + " form: " + $(root).id);
 $(root).setAttribute( "itemStatus", "submitted" );
 var myAjax = new Ajax.Request(
   'wizSubmit.ajaxi',
   {
    method: 'post',
    parameters: 'element='+element+passwordParam,
    onFailure: function(transport) {$(root + "submitButton").disabled = false; alert(transport.statusText);},
    onSuccess: wizSubmitDone
   });
 $(root).setAttribute( "itemStatus", "submitted" );
}

function wizSubmitDone( request ) {
// Tolven.Util.log( "response: " + request.responseText );
 closeTab(request.responseText);
 recentSubmit();
}

// Submit specified element (nothing is uploaded, which will already be done
function wizTransition( element, action ) {
    closeEventBubble();
 var myAjax = new Ajax.Request(
   'wizTransition.ajaxi',
   {
    method: 'get',
    parameters: 'element='+element+'&action='+action,
    onFailure: function(request) {displayError(request,element);},
    onSuccess: wizTransitionDone
   });
// $(root).setAttribute( "itemStatus", "NEW" );
}

function wizTransitionDone( request ) {
// Tolven.Util.log( "response: " + request.responseText );
 var responses = request.responseText.split(",");
 if($(responses[0]))
 closeTab(responses[0]); // Which pane to close
 if (responses[1]!=null) oneSecondShow = responses[1]; // Which pane to open
 recentSubmit();
}
/**
* To nullify  the current trim. Thereby, wizard closes.
* Author Vineetha
* Added on 1/18/2011
*/

function wizNullify( element, action ) {
 if (confirm( "Mark the document as Nullified?" )) {
  var myAjax = new Ajax.Request(
    'wizNullify.ajaxi',
    {
     method: 'get',
      parameters: 'element='+element+'&action=nullifiedActive',
             onFailure: function(request) {displayError(request,element);},
     		 onSuccess: function(request) {wizNullifyDone(request,element);}
    });
 }
}
function wizNullifyDone( request ) {
 	closeTab(request.responseText);
}
/**
* To suspend  the current trim. Thereby, wizard closes.
* Author Vineetha
* Added on 1/20/2011
*/

function wizSuspend( element, action ) {
 if (confirm( "Mark the document as Inactive?" )) {
  var myAjax = new Ajax.Request(
    'wizNullify.ajaxi',
    {
     method: 'get',
      parameters: 'element='+element+'&action=suspendActive',
             onFailure: function(request) {displayError(request,element);},
     		 onSuccess: function(request) {wizNullifyDone(request,element);}
    });
 }
}
/**
* To mark  the current trim 'completed'. Thereby, wizard closes.
* Author Vineetha
* Added on 2/1/2011
*/

function wizComplete( element, action ) {
 if (confirm( "Mark the document as Resolved?" )) {
  var myAjax = new Ajax.Request(
    'wizNullify.ajaxi',
    {
     method: 'get',
      parameters: 'element='+element+'&action=completedActive',
             onFailure: function(request) {displayError(request,element);},
     		 onSuccess: function(request) {wizNullifyDone(request,element);}
    });
 }
}

// Setup input fields
function setupWizStepQuestions( prefix, step ) {
 var questions = step.getElementsByClassName( "question");
 for ( var x=0; x < questions.length; x++) {
  var question = questions[x];
  question.setAttribute("root", prefix);
  question.setAttribute("step", step.id);
 }
}

function enableStep( step, enable ) {
 var icon = $(step.getAttribute( "icon" ));
 var root = $(step.getAttribute('root'));
 var currentStep = 1*root.getAttribute('currentStep');
 if (currentStep==0) return;
//    Tolven.Util.log( "Enable step:" + step.id + " current step: " + currentStep);
 if (icon.className == 'disabled' && enable ) {
  icon.className = 'inactive';
  showStep( root.id, currentStep );
 } else if (icon.className != 'disabled' && !enable) {
  icon.className = 'disabled';
  showStep( root.id, currentStep );
 }
}

// Setup steps
function setupWizSteps(prefix, element) {
 var firstStep = 1 ;

 if ($(element).getAttribute('showStepAfterRefresh') != null)
 {
  firstStep = $(element).getAttribute('showStepAfterRefresh');
 }
    var form = $( prefix );
 form.setAttribute("currentStep", 0);
 form.setAttribute("help", "");
 form.setAttribute("changed", "false");

 var steps = form.getElementsByClassName( "step" );
 $(prefix).setAttribute("lastStep", steps.length);
 var divElems = form.getElementsByTagName( "div" );
 var headerTable = "<table style='margin-bottom:20px;'>";
 headerTable +="<tr>";
 var  divElemsCount = divElems.length;
    var stepsCount = 1;
 var blocks = 0;
 for(i=0;i<divElemsCount;i++){
  if(divElems[i].className == 'step' && !divElems[i].getAttribute('inHeader')){
     headerTable += makeStep(prefix, stepsCount, divElems[i]);
   stepsCount++;
   blocks++;
  }
  else if(divElems[i].className == 'branch' && !divElems[i].getAttribute('inHeader')){
    var brachNodes = divElems[i].getElementsByTagName( "div" );

    headerTable+= "<td colSpan='"+blocks+"'></td>";
    for(j=0;j<brachNodes.length;j++){
   if(brachNodes[j].className == 'step' && !brachNodes[j].getAttribute('inHeader')){
     headerTable += makeStep(prefix, stepsCount,  brachNodes[j]);
     stepsCount++;
     blocks++;
      }
   else if(brachNodes[j].className == 'choices' && !brachNodes[j].getAttribute('inHeader')){
     var tempStr = "<li id='"+prefix+"choice"+stepsCount+"_showDrpDwn'> <a href='javascript:toggleDrpDwn(\""+prefix+"choice"+stepsCount+"dropdown_loc\",\""+prefix+"choice"+stepsCount+"_drpDwn\")'>";
     tempStr += "<img src='../images/cyan_right.gif' style='margin-top:11px;'/></a> <br/><element id='"+prefix+"choice"+stepsCount+"dropdown_loc' style='height: 0px; position: absolute;'/>";
     tempStr += "<div id='"+prefix+"choice"+stepsCount+"_drpDwn' class='drpDwn' style='display:none;'>"+brachNodes[j].innerHTML+"</div></li>";
     headerTable += "<td>"+tempStr+"</td>";
      brachNodes[j].setAttribute('inHeader','true');
      blocks++;
   }
  }
    }
    else if (divElems[i].className == 'add' && !divElems[i].getAttribute('inHeader'))
    {
   headerTable += "<td>"+ divElems[i].innerHTML + "</td>";

    }

 }

 if($(prefix+'stepIcons')){
  var tempDiv = createElement('div');
  if(Prototype.Browser.IE)
   tempDiv.className="planWizard";  // it's really wierd that this style messes up layout in FF.
  tempDiv.innerHTML = headerTable+"</tr></table>";

  $(prefix+'stepIcons').appendChild(tempDiv);
 }
 if (form.getAttribute("itemStatus")=='NEW') {
  Event.observe(form, 'click', itemClicked, false);
  form.checkVis();
     form.checkDisabled();
  new Form.Observer( form, 5, wizUploadCheck );
      showStep(prefix, firstStep);
  $('stepStatus').innerHTML="Setup " + prefix + ", first step=" + firstStep + " status";
 } else {
//  $(prefix+'step1').show(); // [JMC] 1/19/2008 This does not appear to be needed
  form.setAttribute("currentStep", 1);
 }
}

function itemClicked( event ) {
 // Do not register events when Asynchronous Submission is stopped.

 var form = Event.findElement( event, 'form');
 if (isStopAsync(form.id) == "false")
 {
  form.checkVis();
     form.checkDisabled();
     form.setAttribute("changed", "true" );
 }
}

function gotoQuestion( prefix, question ) {
 if (question < 21) showStep( prefix, 2 );
 else showStep( prefix, 3 );
 var x = 0;
 var y = 0;
 var e = $(prefix + ':' + question);
 while (e) {
  x += e.offsetLeft;
  y += e.offsetTop;
  e = e.offsetParent;
 }
 window.scrollTo(x, y);
}

// Advance over disabled steps
function advance( prefix, stepNumber ) {
 var step = $(prefix+'step'+stepNumber);
 var icon = $(step.getAttribute( "icon" ));
 if (icon.className=='disabled') {
  return advance( prefix, (1*stepNumber)+1 );
 }
 return stepNumber;
}

// Hide the current step and show the new step
function showStep( prefix, stepNumber ) {
// $('stepStatus').innerHTML="Showing " + prefix + ", step=" + stepNumber;
//    Tolven.Util.log( "[showStep] stepNumber=" + stepNumber );
 var root = $(prefix);
// Tolven.Util.log( "Root: " + root.id );
 var currentStep = 1*root.getAttribute('currentStep');
 var lastStep = 1*root.getAttribute('lastStep');
 /**
  * Modified to hide signature block in all steps except 'complete' step.
  * added on 25/08/2010 by Valsaraj
  */
  if ($("signatureContainer")!=null) {
	  if (lastStep==stepNumber) {  	
	  	$("signatureContainer").style.display="block";
	  }
	  else {
	  	$("signatureContainer").style.display="none";
	  }
  }
  
 if (stepNumber==currentStep) return;
// Tolven.Util.log( "step-before: " + stepNumber );
// Tolven.Util.log( "step-after: " + stepNo );
 //if preProcessStep fails don't proceed
 if(!preProcessStep(root,currentStep,lastStep))
	return;
 var stepNo = stepNumber;
 $(prefix+'submitButton').disabled=true;
 if ($(prefix+'signaturePasswordField') ) {
   $(prefix+'signaturePasswordField').disabled=true;
 }
 if (lastStep != 1) {
  stepNo = advance(prefix, stepNumber);
  if (stepNo==lastStep) {
   $(prefix+'nextButton').disabled=true;
  } else {
   $(prefix+'nextButton').disabled=false;
  }
  if (stepNo==1) $(prefix+'prevButton').disabled=true;
  else $(prefix+'prevButton').disabled=false;
  if (currentStep > 0) {
   $(prefix+'step'+currentStep).hide();
   $(prefix+'step'+currentStep+':sel').className='inactive';
  }
  if (stepNo > 0) {
   $(prefix+'step'+stepNo+':sel').className='active';
  }
 }
 root.setAttribute('currentStep', stepNo);
 if (stepNo > 0) {
  var step = $(prefix+'step'+stepNo);
  	/**
  	* Modified to enable/disable 'create PHR' option for component S & T,
  	* if 'primary email' is valid in 'Patient' TRIM.
  	* added on 06/15/2010 by Valsaraj
  	*/
	if (prefix.indexOf("echrpatients")!=-1 && stepNo==5) {  	
  		enablePHRCreationAndSharing(prefix);
  	}
  
  step.show();
  if (step.getAttribute('include')!=null) {
   wizUpload( prefix, null );
  }
 }
}

function getStepFromServer( elementId, step ) {
 if (submitActive) alert( "ajaxSubmit3 busy");
 $('stepStatus').innerHTML="Getting element " + elementId + ", step " + step.id ;
 step.innerHTML = 'Awaiting ' + elementId ;
 serialNo++;
 var ajax = new Ajax.Updater(
  step,
  contextPath + step.getAttribute('include'),
  {   method: 'get',
         evalScripts: true,
         onSuccess: function() { getStepFromServerDone( step.id ); },
	     onFailure: function(request) {displayError(request,step.id);},
   		 parameters: 'element='+elementId+'&serialNo='+serialNo});
}

function getStepFromServerDone( stepId ) {
 $('stepStatus').innerHTML="Received step " + stepId ;
 var step = $(stepId);
 if (step==null) return;
 var root = $(step.getAttribute('root'));
 if (root==null) return;
 if (step.getAttribute("stepNo")==root.getAttribute("lastStep")) {
 //don't enable submitbutton if the wizard request for it
  if($(root.id+':disableSubmitButton') == null || $(root.id+':disableSubmitButton').value=='false')
     $(root.id+'submitButton').disabled=false;
  if ($(root.id+'signaturePasswordField') ) {
   $(root.id+'signaturePasswordField').disabled=false;
  }
 }
 $('stepStatus').innerHTML= root.id + " step " + step.getAttribute("stepNo") + " ready" ;
 var s = 1*step.getAttribute("stepNo");
 // Check for help
 var help = step.getElementsByClassName( "help");
 if (help.length > 0) {
  help[0].id = root.id + 'help' + s;
  $(help[0]).hide();
 }
}

// Called by the timeout
function wizUploadCheck( obj, value ) {

 var root = $(obj);
 var formId = root.id;
 var step = $(formId+'step'+root.getAttribute('currentStep'));
 // Don't upload when on the last step
 // And check to see if the form is changed and if Async submission should be stopped.
 if (root.getAttribute('currentStep')!=root.getAttribute('lastStep')
   && isStopAsync(formId) == "false" && step != null && step.getAttribute('autoUpdate') != 'false'){
 		 wizUpload( obj, value );
  }
}

// Upload when the form changes
function wizUpload( obj, value ) {
  var root = $(obj);
 // Don't upload when on the last step
 if (root.getAttribute('itemStatus')=='NEW') {
  var elementId = root.getAttribute( 'tolvenid' );
  ajaxSubmit3( root, elementId );
}
}

submitActive = false;

// Intercept a normal Submit and do it Ajax-style instead.
// In this case, we don't overlay the pane, but rather accept an XML response
// containing errors and further instructions.
function ajaxSubmit3(form, element ) {
 // If the form is no linger being created, don't try uploading
	var formElem = $(form);
 if(formElem.getAttribute('ajaxSubmit3InProgress') != null)
	 return;
 $(form).setAttribute("ajaxSubmit3InProgress", 'true');

 if (form.getAttribute('itemStatus')!='NEW') return;
 submitActive = true;
 $('uploadStatus').innerHTML="Uploading " + form.id;
   var elements = Form.getElements(form);
    var queryComponents = new Array();
    for (var i = 0; i < elements.length; i++) {
          var queryComponent = Form.Element.serialize(elements[i]);
          if (queryComponent) queryComponents.push(queryComponent);
    }
 queryComponents.push( 'element='+element );
 queryComponents.push( 'submit3='+$(form).id );
// Tolven.Util.log( "submit element=" + element + " to: " + form.action );
     var ajax = new Ajax.Request(
         form.action,        // URL
         {                // options
             method:'post',
             onSuccess: function(req) {ajaxSubmit3Done(req,  element,form );},
			 onFailure: function(request) {displayError(request,element);},
             evalScripts: true,
             postBody: queryComponents.join('&')
        });
      return false;
}

// OK to allow Submit button now. Show errors where they belong
function ajaxSubmit3Done( req , element,form) {
	if($(form).getAttribute('ajaxSubmit3InProgress') != null){
		$(form).removeAttribute('ajaxSubmit3InProgress');
	}
	submitActive = false;
	eval("var resp = ("+req.responseText+")");
 // Might be obsolete page
 var root = $(resp.root);
 if (root==null) {
  $('uploadStatus').innerHTML= "Obsolete: " + resp.root;
  return;
 }
   clearErrors( $(resp.element) );
   var errorCount = resp.errors.length+resp.globalErrors.length;
 $('uploadStatus').innerHTML= "Upload " + root.id + " complete with " + errorCount + " errors";
   root.setAttribute( "errorCount", errorCount );
// Tolven.Util.log( "Error Count: " + resp.errors.length + " Global Error Count: " + resp.globalErrors.length );
//   clearLastStep( root );
   for (var e = 0; e < resp.errors.length; e++) {
    var error = resp.errors[e];
//  Tolven.Util.log( resp.element + "Error: " + error.clientId + " Summary: " + error.summary );
    var target = $(error.clientId+"Error");
    if (target!=null) {
     target.innerHTML = error.summary;
     $(target).show();
    }
   }

   if (errorCount > 0 ) {
	    showErrors( root, resp );
	  	window.status = "Errors found";
   } else {
	  	var step = $(root.id+'step'+root.getAttribute('currentStep'));
	  	// Do we need to get this step's content from the server?
	  	if (step.getAttribute("include")!=null) {
	   		window.status = "Download step: " + step.id;
	   		getStepFromServer(root.getAttribute( 'tolvenid'), step);
	  	} else {
	   		window.status = "Upload done";
	  	}
   }
}


function makeErrorTable( body ) {
 return '<p>This form is not ready for submission yet due to the following problem(s):</p><table class="messages" ><thead><tr><td>Message</td><td>Go To</td></tr></thead> \n<tbody>' + body + '</tbody></table>';
}

function makeGoto( clientId ) {
 var question = $(clientId);
 if (question==null) return " ";
 var q = question.getAttribute("wizTitle");
 if (!q) {
  q = "The problem";
 }
 return '<a href="javascript:goto(\'' + clientId + '\');">' + q + '</a>';
}

function makeErrorRow( error ) {
 return '<tr><td>' + error.summary + '</td><td>' + makeGoto(error.clientId) + '</td></tr> \n';
}

function showErrors( root, resp ) {
 var lastStepNo = 1*root.getAttribute('lastStep');
 var lastStep = $(root.id+'step'+lastStepNo);
 var errorBody = "";
   for (var e = 0; e < resp.errors.length; e++) {
    var error = resp.errors[e];
    errorBody += makeErrorRow( error );
   }
   for (var g = 0; g < resp.globalErrors.length; g++) {
    var error = resp.globalErrors[g];
    errorBody += makeErrorRow( error );
   }
 lastStep.innerHTML = makeErrorTable( errorBody );
}

function goto( question ) {
// Tolven.Util.log( question );
 var x = 0;
 var y = 0;
 var p = $(question);
 while (p) {
  if (p.className=='step') {
   showStep( p.getAttribute('root'), 1*p.getAttribute('stepNo') );
   break;
  }
  p = p.parentNode;
 }
 var e = $(question);
 while (e) {
  x += e.offsetLeft;
  y += e.offsetTop;
  e = e.offsetParent;
 }
 window.scrollTo(x, y);
}

function clearLastStep( root ) {
// Tolven.Util.log( "In clearLastStep: " + root.id );
 var lastStep = 1*root.getAttribute( "lastStep" );
// Tolven.Util.log( "Last step: " + lastStep);
 var step = $(root.id+'step'+lastStep);
 step.innerHTML = "Waiting for response...";
}

function clearErrors( node ) {
 if (node==null) return;
 if (node.id) {
  var t = node.id.substr( node.id.length-5, 5);
  if (t == "Error") {
    $(node).hide(  );
   }
 }
   var nodes = node.childNodes;
   for (var c = 0; c < nodes.length; c++) {
    clearErrors( nodes[c] );
   }
}


// Next step
function nextStep( prefix ) {
 var root = $(prefix);
 var currentStep = 1*root.getAttribute('currentStep');
 var lastStep = 1*root.getAttribute('lastStep');
 if (currentStep>=lastStep) return;
 //if preProcessStep fails don't proceed
 if(!preProcessStep(root,currentStep,lastStep))
	return;
 showStep( prefix, currentStep+1);
}

// Previous step
function prevStep( prefix ) {
 var root = $(prefix);
 var currentStep = 1*root.getAttribute('currentStep');
 for (var s = currentStep-1; s>0; s--) {
  var step = $(prefix+'step'+s);
  var icon = $(step.getAttribute( "icon" ));
  if (icon.className!='disabled') {
   showStep( prefix, s);
   return;
  }
 }
}

// Toggle visibility of a detail object based on checkbox
function toggleDetail( checkbox ) {
 var detail = $(checkbox.id+'Detail');
 if ($(checkbox).checked) {
  Element.show(detail);
 } else {
  $(detail).hide();
 }
}

// Toggle visibility of a detail object based on checkbox
function toggleEditOn( parent ) {
 var valueElements = $(parent).getElementsByClassName("value");
 var editElements = $(parent).getElementsByClassName("edit");
 for (var v = 0; v < valueElements.length; v++) $(valueElements[v]).hide();
 for (var e = 0; e < editElements.length; e++) $(editElements[e]).show();
}

// Toggle visibility of a detail object based on checkbox
function toggleEditOff( parent ) {
 var valueElements = $(parent).getElementsByClassName("value");
 var editElements = $(parent).getElementsByClassName("edit");
 for (var v = 0; v < valueElements.length; v++) $(valueElements[v]).show();
 for (var e = 0; e < editElements.length; e++) $(editElements[e]).hide();
}

// Toggle help on the current step or selected fieldset/question
function toggleHelp(prefix, fieldset) {
 var root = $(prefix);
 if (fieldset!=null) {
  var help1 = root.getAttribute("help");
  var help2 = $(prefix + fieldset + "Help");
  if (help2==null) {
   root.setAttribute("help", '');
  } else {
   root.setAttribute("help", help2.id);
   if (help1!='') {
    $(help1).hide(  );
    if (help1==help2.id) {
     root.setAttribute("help", '');
     return;
    }
   }
  }
  //  Tolven.Util.log( prefix + fieldset + " offsetTop=" + $(prefix + fieldset).offsetTop);
  //  help.style.position = 'absolute';
  //  help2.style.top = $(prefix + fieldset).offsetTop + "px";
  //  help2.style.left = help2.offsetLeft + "px";
  Element.show( help2 );
	return;
 }
 var currentStep = 1*root.getAttribute('currentStep');
 var lastStep = 1 * root.getAttribute('lastStep');
  // for the last step set id for help div
 if(currentStep == lastStep){
	$($(prefix + 'step'+currentStep).getElementsBySelector("div[class='help']")[0]).id = prefix + 'help'+(currentStep+1);
 }
 if (currentStep!=0 && $(prefix + 'help'+(currentStep+1)) !=null) {
	//$(prefix + 'help'+(currentStep+1)).toggle();
	var popupDialog = toggleDrpDwn(prefix + 'help'+(currentStep+1),prefix + 'help'+(currentStep+1),true);
	var pos =  Position.cumulativeOffset($(prefix).getElementsBySelector("a[class='help']")[0]);
	$(popupDialog).setStyle({width:'300px',left:(pos[0]-300)+"px",top:pos[1]+"px"});
	return false;
 }
}

function toggleAttention( fieldset ) {
 var fs = $(fieldset);
 if (fs.className!='attention') {
  fs.className='attention';
 } else {
  fs.className='plain';
 }
}

function doNothing() {
}

// User selected a valid choice, copy result to field.
function selectionMade( field, selector ) {
 $(field).value = selector.options[selector.selectedIndex].text;
 $(field).exact = true;
}

// Validate a text field
function validate( field, value ) {
 var choices = choiceList[field.getAttribute("choices")];
 if (choices==null) return;
 var valueLC = value.toLowerCase();
 for (var x = 0; x < choices.length; x++) {
  var choiceLC = choices[x].toLowerCase();
  if (valueLC == choices[x].toLowerCase()) {
   $(field).exact = true;
   field.className = 'selOK';
   field.value = choices[x];
   return;
  }
 }
 $(field).exact = false;
 field.className = 'selError';
}

// If only one value matchtes, just display
// If more than one match, display first match and dropdown with all choices.
// If there's an unanchored match, include in dropdown but not in text field
function fieldChanged( field, value ) {
 if (field.tolvenEvent==null) {
  field.tolvenEvent = new Form.Element.Observer(field.id, 0.3, fieldChanged );
  field.exact=false;
 }
 if (value.length==0 || field.exact==true) {
//  Tolven.Util.log("Hiding(1): " + $(field.id + "List").id);
  $(field.id + "List").hide(  );
  field.className = 'selOK';
  field.exact=false;
  return;
 }
 var choices = choiceList[field.getAttribute("choices")];
// Tolven.Util.log( "Choices attribute: " + field.choices + " in " + field.id );
// Tolven.Util.log( "First choice: " + choiceList.occupations[0] );
 if (choices==null) {
  alert( "No Choices in: " + field.id );
  return;
 }
 var valueLC = value.toLowerCase();
 var matches = new Array();
 for (var x = 0; x < choices.length; x++) {
  if ( value.length <= choices[x].length )  {
   var choiceLC = choices[x].toLowerCase();
   if (valueLC == choiceLC.substr( 0, value.length )) {
    matches.push( choices[x] );
   }
  }
 }
 if (matches.length==0) {
//  Tolven.Util.log("Hiding(2): " + $(field.id + "List").id);
  $(field.id + "List" ).hide( );
  field.className = 'selError';
 } else if (matches.length==1) {
  field.className = 'selOK';
//  Tolven.Util.log("Hiding(3): " + $(field.id + "List").id);
  $(field.id + "List").hide(  );
  if (matches[0].length != value.length) {
   insertAtCursor( field, matches[0].substr( value.length) );
  }
 } else {
  field.className = 'selOK';
  $(field.id +'List').length = 0;
  for (var m = 0; m < matches.length; m++) {
   $(field.id+'List').options[m] = new Option( matches[m], matches[m], false, false );
  }
  field.className = 'selOK';
//  Tolven.Util.log("Showing: " + $(field.id + "List").id);
  $(field.id + "List").show(  );
 }
}

function insertAtCursor(field, value)
{
 //IE support
 if (document.selection)
 {
  field.focus();
  sel = document.selection.createRange();
  sel.text = value;
  sel.moveStart('character', -value.length);
  sel.select();
 }
 //MOZILLA/NETSCAPE support
 else if (field.selectionStart || field.selectionStart == '0')
 {
  var startPos = field.selectionStart;
  var endPos = field.selectionEnd;
  field.value = field.value.substring(0, startPos)
   + value
   + field.value.substring(endPos, field.value.length);
  field.selectionStart = startPos;
  field.selectionEnd = startPos + value.length;
 }
 //Anyone else.
 else
 {
  field.value += value;
 }
}

function setNow( field ) {
 var now = new Date();
 now.setSeconds( 0 );
 $(field).value = toTS( now );
 showTime( field );
}

function fromTS( ts ) {
 return new Date( ts.substr(0, 4), new Number(ts.substr(4,2))-1, ts.substr(6,2), ts.substr(8,2), ts.substr(10,2), ts.substr(12,2) );
}

function showTime( field ) {
 var date = fromTS( $(field).value );
 $(field+'vis').value = date.toString();
}

function toTS( date ) {
 // Set HL7 TS value also
 return "" + date.getFullYear() +
                  ((date.getMonth()+1).toString().length==1?"0":"")+(date.getMonth()+1) +
                  (date.getDate().toString().length==1?"0":"")+date.getDate() +
                  (date.getHours().toString().length==1?"0":"")+date.getHours() +
                  (date.getMinutes().toString().length==1?"0":"")+date.getMinutes() +
                  (date.getSeconds().toString().length==1?"0":"")+date.getSeconds();
}

function addMinutes( field, offset ) {
 var date = fromTS( $(field).value );
 date.setTime(date.getTime() + 60000 * offset);
 $(field).value = toTS( date );

 showTime( field );
}


  function createView(element, viewId ) {
   var a = document.createElement( "a" );
   a.href = "#";
   a.onclick = function() {hideView( element, viewId );};
   a.appendChild( document.createTextNode( "Close") );
   var view = document.createElement( "div" );
   view.id = viewId;
   view.style.display = 'none';
   view.appendChild( a );
   view.appendChild( document.createElement( "br" ));
   var views = $(element+'views');
   views.appendChild(view);
   return view;
  }

  function createImageView(element, viewId, docId ) {
   var view = createView( element, viewId );
   var img = document.createElement( "img" );
   img.src = "/Tolven/document?docId=" + docId + "&width=400&height=400";
   view.appendChild( img );
   return view;
  }

  function createXMLView(element, viewId, docId ) {
   var view = createView( element, viewId );
   var div = document.createElement( "div" );
   view.appendChild( div );
   var ajax = new Ajax.Updater(
		 div,
		 contextPath + '/document',
		 {   method: 'get',
			 parameters: 'docId='+docId+
			             '&serialNo='+serialNo +
			             '&accountUserId='+accountUserId });
   return view;
  }

  function showImageView( element, viewId, docId) {
    var view = $(viewId);
    if (view==null) {
     view = createImageView(element, viewId, docId);
    }
   view.style.display='block';
   $(element+'upload').style.display='none';
  }

  function showXMLView( element, viewId, docId) {
    var view = $(viewId);
    if (view==null) {
     view = createXMLView(element, viewId, docId);
    }
   view.style.display='block';
   $(element+'upload').style.display='none';
  }

  function hideView( element, viewId ) {
   $(viewId).style.display='none';
   $(element+'upload').style.display='block';
  }

function copyTrimElement(element,path,choice,formId){
 var rootForm = $(formId);
 var currentStep = 1 * rootForm.getAttribute('currentStep');
 var lastStep = 1 * rootForm.getAttribute('lastStep');
 var instAjax = new Ajax.Request(
    'copyTrimElement.ajaxi',
     {
      method: 'get',
      parameters: 'element='+element+'&path='+path+'&choice='+choice,
      onComplete: function(req) {refreshWizard(element, formId, currentStep);}
    });
}
function insertPlanChoice(element,path,choice){
  var instAjax = new Ajax.Request(
    'insertChoice.ajaxi',
    {
   method: 'get',
   parameters: 'element='+element+'&path='+path+'&choice='+choice,
   onComplete: function(req) {getRemoteContent(req.responseText);}
    });
}

// This method is  same as ajaxSubmit3(), except that a refresh
// is followed after submission

function ajaxSubmit4(form, element, showStepAfterRefresh) {
	submitActive = true;
	$('uploadStatus').innerHTML="Uploading " + form.id;
	var elements = Form.getElements(form);
    var queryComponents = new Array();
    for (var i = 0; i < elements.length; i++) {
		var queryComponent = Form.Element.serialize(elements[i]);
        if (queryComponent) queryComponents.push(queryComponent);
    }
	queryComponents.push( 'element='+element );
	queryComponents.push( 'refreshWizard=true');

	var ajax = new Ajax.Updater(
	form.id,
	contextPath + '/ajax/paneDispatch.jsf',{
        method:'post',
        onSuccess: function() { 
			refreshWizard(element,form.id, showStepAfterRefresh );
		},
        onFailure: function(request) { displayError(request,element+_params);},
        evalScripts: true,
        postBody: queryComponents.join('&')
    });
}

//refreshes the wizard
function refreshWizard(element, id, showStepAfterRefresh){

 submitActive = false;
 $(element).setAttribute("showStepAfterRefresh", showStepAfterRefresh);

 var ajax = new Ajax.Updater(
    element,
    contextPath + '/ajax/paneDispatch.jsf',
    {   method: 'get',
    evalScripts: true,
    onSuccess: function(req) {wizardRefreshComplete(req, element, id, showStepAfterRefresh ); },
    onFailure: function(request) { displayError(request,element);},
     parameters: 'element='+element+'&serialNo='+serialNo +'&accountUserId='+accountUserId });
}

function wizardRefreshComplete(req,element, id, showStepAfterRefresh){

 // Do nothing
}

// Opens popup with a Grid Data.
// methodName and methodArgs will be used to construct hyperlink for Grid entries.
// methodName = call back function from Grid
// methodArgs = Concatenated Arguments Str for the above method.
function openPopup( contentName, placeholderid, formId, methodName, methodArgs,gridType){
 serialNo++;
// Tolven.Util.log( "Getting: " + contentName );
 $('downloadStatus').innerHTML="Get " + contentName + "...";

 // Update this block when ever a similar  new wizard is added.

 // Update this block whenever a similar new wizard is added.
	new Ajax.Request(
	  'createGrid.ajaxf',
	  {
		method: 'get',
		parameters: "element="+contentName+"&gridId="+placeholderid+"&gridType="+gridType+"&methodArgs="+methodArgs+"&methodName="+methodName+"&formId="+formId,
		onSuccess: function(request){ setPopupContent(request,  placeholderid, formId ); },
		onFailure: function(request) {displayError(request,param);}
	  });
}



function setPopupContent( req, placeholderid, formId){
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
 popupElement.style.top = document.body.clientHeight * .30 + "px";
 popupElement.style.left = document.body.clientWidth * .30 + "px";

}

function closePopup(divId, formId)
{
 // Start Wizard Async Submission.
 startAsync(formId);
 closeDiv(divId);
}


function closeDiv(divId)
{
 if($(divId))
	 $(divId).hide();
 //$(divId).innerHTML = "";
}

function buildArguments(args)
{
 var argsStr = "";
  for (var i = 0; i < args.length; i++) {
   argsStr = argsStr + args[i];
   if (i < (args.length - 1)){
    argsStr = argsStr + "|";
   }
  }
  return argsStr;
}

function splitArguments(argsStr)
{
  return argsStr.split("|");
}


function openTemplate(contentName, placeholderid, methodName, formId, index,gridType)
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
 openPopup( contentName, placeholderid, formId,  methodName, methodArgsStr,gridType);
}

// Set template name (Diagnosis Name) that should be added
// and make explicit form submission.
function addTemplate(templateName, element, methodArgs)
{
 var lArgs = splitArguments(methodArgs);
 var formId = lArgs[0];
 var rootForm = $(formId);

 $(formId + ":computeEnable").value = "true";
 $(formId + ":computeTemplate").value = templateName;
 $(formId + ":computePosition").value = lArgs[1];
 $(formId + ":computeAction").value = 'add';

 closeDiv(element);

 var wipNode = rootForm.parentNode;
 ajaxSubmit4(rootForm, wipNode.id, eval(lArgs[1]) + 1);
}

function removeTemplate(formId,postion)
{
 var rootForm = $(formId);
 stopAsync(formId);

 $(formId + ":computeEnable").value = "true";
 $(formId + ":computeAction").value = 'delete';
 $(formId + ":computePosition").value = postion;

 var currentStep = 1 * rootForm.getAttribute('currentStep');
  var lastStep = 1 * rootForm.getAttribute('lastStep');

 var showStepAfterRefresh= 1;
 // Make sure that visble step is any step but NOT last step.
 if (lastStep == 2 || lastStep == 3 ) // When None or 1 Diagnosis is present.
 {
  showStepAfterRefresh = 1;
 }
 else // When more than 1 diagnosis is present.
 {
  showStepAfterRefresh = 2;
 }

 var wipNode = rootForm.parentNode;
 ajaxSubmit4(rootForm, wipNode.id, showStepAfterRefresh);

}

function setEnableAct(formId,enableHidden,enableVal){
	 var rootForm = $(formId);
	 stopAsync(formId);
	 $(formId +":"+enableHidden).value = enableVal;
	 var currentStep = 1 * rootForm.getAttribute('currentStep');
	 var wipNode = rootForm.parentNode;
	 ajaxSubmit4(rootForm, wipNode.id, currentStep);
}

function addProcedureToTrim(procedure,template,formId,element){
 if(element.checked){
  $(element.id+":options").show();
 }else{
  $(element.id+":options").hide();
 }
 var lform = $(formId);

 lform.setAttribute("isFormChanged", "false");
 $(formId + ":enabled").value = "true";
 $(formId + ":enableChoice").value = element.checked?"true":"false";
 $(formId + ":choice").value = template;
 $(formId + ":procName").value = procedure;
 var wipNode = lform.parentNode;
 ajaxSubmit3(lform, wipNode.id, false);
 $(formId + ":enabled").value = false;
}
function stopAsync(id)
{

 $(id).setAttribute("stopAsync", "true");

}
function startAsync(id)
{
 $(id).setAttribute("stopAsync", "false");
}

function isStopAsync(id)
{
 return $(id).getAttribute("stopAsync");
}

function processGroup(groupName){
 var _group = $(groupName).getElementsByTagName("input");
 var _unknownEle = null;
 var _disableUnknown = false;
 for(var i=0;i<_group.length;i++){
  var elem = _group[i];
  if(elem.type != 'checkbox')
   continue;
  if(elem.value.indexOf('Unknown') > -1){
   _unknownEle = elem;
  }else if(elem.checked){
   _disableUnknown = true;
  }
 }
 if(!_unknownEle)
  return;
 if(_disableUnknown )
  _unknownEle.setAttribute("disabled","disabled");
 else
  _unknownEle.removeAttribute("disabled");
}

function showSelectOneMenuOption(selectElem,formId){
 var _elem = selectElem;
 if(!_elem)
  return;
 for(var i=0;i<selectElem.options.length;i++){
  if(i == _elem.selectedIndex){
   if($(_elem.id+":option"+(i-1)))
    $(_elem.id+":option"+(i-1)).show();
  }
  else{
   if($(_elem.id+":option"+(i-1)))
    $(_elem.id+":option"+(i-1)).hide();
  }
 }
 var lform = $(formId);

 lform.setAttribute("isFormChanged", "false");
 $(formId + ":enabled").value = "true";
 $(formId + ":enableChoice").value = "true";
 $(formId + ":choice").value = _elem.options[_elem.selectedIndex].text;
 $(formId + ":procName").value = 'specimenType';
 //alert($(formId + ":choice").value);
 var wipNode = lform.parentNode;
 ajaxSubmit3(lform, wipNode.id, false);
 $(formId + ":enabled").value = false;

}

function enableRelation(optionName, formId)
{
 var options1 = $(optionName).getElementsByTagName('input');
 for ( i = 0; i < options1.length; i++)
 {
  if (options1[i].checked)
  {
   $(formId + ':' + options1[i].value ).value = 'true';
  }
  else
  {
   $(formId + ':' + options1[i].value ).value = 'false';
  }
 }
}
function toggleGroup(grpName,check){
 if(check)
  return;
 var grpTbl = $(grpName+"Group");
 var options1 = grpTbl.getElementsByTagName('input');
 for ( i = 0; i < options1.length; i++)
 {
  options1[i].checked = check;
 }
}
function preProcessStep(root,currentStep,lastStep){
  var _nextStep = currentStep+1;
  var element = $(root).getAttribute("tolvenid" );
  if(!$(root.id+':preProcessStepParams'))
    return true;
  var _paramstr = $(root.id+':preProcessStepParams').value;
  var _allParams = _paramstr?_paramstr.split(";"):null;
  if(!_allParams)
    return true;
  for(var _p=0;_p<_allParams.length;_p++){
    var _params = _allParams[_p].split(",");
    if(_params[0] == _nextStep){
     return eval(_params[1]+"('"+element+"','"+root.id+"','"+_nextStep+"')");	  
	}
  }
  return true;
}

//------------------START: Favorites wizard relates script -----------------------
toggleSelect = function(element,id){
	if($(element).hasClassName('selected')){
		$(element).removeClassName('selected');
	}else
		$(element).addClassName('selected');
}

// Method to copy/remove items to/from favorites lists
//id - id of the item on the list
//isDelete - boolean represents if the event is to delete the item from list
//srcGridId - source grid id
//destGridId- destination grid id
//element - path of the menuitem(accountList/personalList)
//template - template trim name for harvesting the item when the item is added to the list

function gridEvent(id,isDelete,srcGridId,destGridId,element,template){
  if(isDelete == true || isDelete == 'true' ){
    // remove the selected items from the right hand side grid
	// and trigger ajax to remove the act.
	var selectedItems = $$("tr#"+id);
	for(i=0;i<selectedItems.length;i++){
		$(selectedItems[i]).remove();
	}
  }else{
	 // copy the selected items from the left hand side grid
	// and trigger ajax to add the act.
	  var destGrid = $(destGridId+"_grid");
	  var selectedItems = $$("a#"+id);
	  var selectedItemsIds = "";
	  for(i=0;i<selectedItems.length;i++){
		if(i>0)
			selectedItemsIds+=",";
		selectedItemsIds+=selectedItems[i].id;
		var row = $(destGrid).insertRow($(destGrid).rows.length);
		row.id= id;
		$(row).update("<td onclick=javascript:gridEvent('"+id+"',true,'','"+destGridId+"','"+element+"')>"+selectedItems[i].innerHTML+"</td>");
	  }
  }
  //rewrite the styles

  var destGridRows = $(destGridId+"_grid").rows;
  for(i=0;i<destGridRows.length;i++){
	if(i%2 == 0) destGridRows[i].className = "even";
	else destGridRows[i].className = "odd";
  }
	new Ajax.Request(
	'copyMenuDataToList.ajaxi',
	{
	  method: 'get',
	  parameters: "template="+template+"&srcGridId="+srcGridId+"&menuDataList="+id+"&element="+element+"&isDelete="+isDelete+"&menuDataId="+arguments[5],
	  onSuccess: function(request){  },
	  onFailure: function(request) {displayError(request,template);}
	});
 }
 function viewDetails(menuPath){
 	// popup for codes on the drilldown page
 }
 function showFavoritesGrid(contentName, placeholderid, methodName, args){
	var params = args.split('|');
	var formId = params[0];
	var index = params[1];
	var popupGrids = $$("form#"+formId+" div#popupGrids div");
	for(i=0;i<popupGrids.length;i++){
		$(popupGrids[i]).hide();
	}
	if($(placeholderid) == null){
		var element = createElement('div');
		element.id = placeholderid;
		element.className="popupgrid";
		$$("form#"+formId+" div#popupGrids")[0].appendChild(element);
	}

	openTemplate(contentName, placeholderid, methodName, formId, index);
 }

function viewFavoriteItemDetails( favoriteItem,placeholderid,title,formId){
 var prefHTML = "";

  prefHTML += "<div class=\"popupgridheader\">";
  prefHTML += "<img class='closetab' src='../images/x_black.gif' onclick=\"closePopup('" +placeholderid + "','" + formId + "' );return false;\"/>&nbsp;" ;
  prefHTML += "</div>";
  prefHTML += "<div class='doclinDetails'>";
  prefHTML += "<table cellpadding='2' cellspacing='2'>";
  prefHTML += "<tr><td>_CODE_</td><td>"+title+"</td></tr>";
  prefHTML += "<tr><td>SNOMED</td><td>"+favoriteItem.substring(favoriteItem.lastIndexOf('-')+1)+"</td></tr>";
  prefHTML += "</table></div>";


 var popupElement = $(placeholderid);
 popupElement.innerHTML = prefHTML;
 popupElement.style.display = 'block';
 popupElement.style.top = document.body.clientHeight * .30 + "px";
 popupElement.style.left = document.body.clientWidth * .30 + "px";

}

//------------------END: Favorites wizard relates script -----------------------

//Script to be moved to patientMD plugin
function addPatientToDocument(patientId,menuPath,methodArguments){

	var methodArgs = methodArguments.split('|');
	var formId = methodArgs[0];
	var rootForm = $(formId);
	$(formId + ":patientAdded").value = "true";
	$(formId + ":patientPath").value = patientId;
	//$(formId + ":computePosition").value = lArgs[1];
	//$(formId + ":computeAction").value = 'add';
	//$(formId + ":computeEnable").value = "true";
	//$(formId + ":computeTemplate").value = templateName;
	closeDiv(methodArgs[0]+menuPath.substring(menuPath.indexOf(":")));
	var wipNode = rootForm.parentNode;
	ajaxSubmit4(rootForm, wipNode.id, 2);
}
function disableElement(element){
	//$(prefix+'submitButton').disabled=true;
	$(element).disabled = true;
}

//method to confirm deleting unsaved problems in problems document wizard
function confirmDeleteUnsavedProblems(element,formId,nextStepNum){
	if (confirm("Unsaved data on the form will be deleted. Do you want to continue?")) {
		deleteUnsavedProblems(element,formId,nextStepNum);
		return false;
	}
}

//method to delete unsaved problems in problems document wizard
//this triggers RemoveDisabledAct.java
function deleteUnsavedProblems(element,formId,nextStepNum){
	 var rootForm = $(formId);
	 stopAsync(formId);
	 $(formId +":deleteUnsavedProblems").value = true;
	 var currentStep = 1 * rootForm.getAttribute('currentStep');
	 var wipNode = rootForm.parentNode;
	 ajaxSubmit4(rootForm, wipNode.id, currentStep);
}

function setDeathProblemName(formId, element, val) {
	alert(element.value);
	alert(val);
	/*alert(($formId + ":deathCauseName"));
	alert(document.getElementById("deathCauseName"));
	alert(document.getElementById("deathCauseName").value);
	alert(($formId + ":deathCauseName").value);
	 alert("setDeathProblemName:" + ($formId + ":deathCauseName").value);*/
}

function instantiateAction(actionPath){
	showPane(actionPath,false,actionPath);
	showActionOptions(actionPath+':active:menu_dropdown_loc',actionPath+':active:menu_drpDwn');
}
function setProblemNameValue(srcElement,destElement){
	$(destElement).value = $(srcElement).options[$(srcElement).selectedIndex].text;
}

function validatePrescriberDateFields(root) {
	if (comparePrescDates(root + ":FieldquesactiveStartTime", root + ":FieldquesactiveEndTime") == 1 ) {
		$(root+"prescGreater").style.display="block";
		$(root+"prescMinDiff").style.display="none";
		$(root+"prescSameTime").style.display="none";
		$(root+"nextButton").disabled=true;
		//alert("Active Start Time should not be greater than the Active End Time. Form can not be submitted.");
	}else if(comparePrescDates(root + ":FieldquesactiveStartTime", root + ":FieldquesactiveEndTime") == -1 ){
		$(root+"prescGreater").style.display="none";
		$(root+"prescMinDiff").style.display="block";
		$(root+"prescSameTime").style.display="none";
		$(root+"nextButton").disabled=true;
		//alert("The Difference between Active Start Time and Active End Time must be atleast 30 Days. Form can not be submitted.");
	}else if(comparePrescDates(root + ":FieldquesactiveStartTime", root + ":FieldquesactiveEndTime") == 0 ){
		$(root+"prescGreater").style.display="none";
		$(root+"prescMinDiff").style.display="none";
		$(root+"prescSameTime").style.display="block";
		$(root+"nextButton").disabled=true;
		//alert("Both Active Start Time and End Time are the same.The Difference between Active Start Time and Active End Time must be atleast 30 Days. Form can not be submitted.");
	}else if(comparePrescDates(root + ":FieldquesactiveStartTime", root + ":FieldquesactiveEndTime") == -2 ){
		$(root+"prescGreater").style.display="none";
		$(root+"prescMinDiff").style.display="none";
		$(root+"prescSameTime").style.display="none";
		$(root+"nextButton").disabled=false;
	}else{
		$(root+"prescGreater").style.display="none";
		$(root+"prescMinDiff").style.display="none";
		$(root+"prescSameTime").style.display="none";
		$(root+"nextButton").disabled=false;
	}
}

function comparePrescDates(date1Id, date2Id) {
	var date1Str = $(date1Id).value;
	var date1Obj = new Date(date1Str);
	var date2Str = $(date2Id).value;
	var date2Obj = new Date(date2Str);
	if (date1Str != "" && date2Str != "") {
		if (date1Obj > date2Obj) {
				return 1;
		}	
		else if (date1Obj < date2Obj) {
			if(Date.parse(date1Obj) - Date.parse(date2Obj) <= -2592000000){	//2505600000
				return -2;
			}else{
				return -1;
			}	
		}
		else {
			return 0;
		}
	}
}

function validateName(id , root , nameDiv){
	var nextButton = $(root+"nextButton");
	var val = $(root+":"+id).value;
	var spaceFormat = /^[\S]{0,35}$/;
	if(val == ""){
		    $(nameDiv).style.display="none";
		    nextButton.disabled = false;
	}else if(val.search(spaceFormat) == -1){
	    	$(nameDiv).style.display="block";
	    	nextButton.disabled = true;
	}else{
		    $(nameDiv).style.display="none";
		    nextButton.disabled = false;
	}	
}


/**
 * Method to validate address line1. Should contain space separated values and PO/P O is not allowed here. 
 * @param id 
 * @param root menu.elementLabel
 * @param poDiv Error div for PO validation.
 * @param spaceDiv Space div for space validation.
 * @return
 */
function validateAddress(id,root, poDiv, spaceDiv) {
		var nextButton = $(root+"nextButton");
		var val = $(root+":"+id).value;
		var poFormat = /((P)O)|((P)(\s)O)/;
		var spaceFormat = /([\S]+\s)+[\S]+$/;
		if(val.search(spaceFormat) == -1){
			if(val.search(poFormat) == -1){
				$(poDiv).style.display="none";
				$(spaceDiv).style.display="block";
		        nextButton.disabled = true;
			}else{
				$(spaceDiv).style.display="none";
				$(poDiv).style.display="block";
			    nextButton.disabled = true;
		    }
		}else{
			$(spaceDiv).style.display="none";
			nextButton.disabled = false;
		}
		if(val.search(poFormat) != -1){
			$(poDiv).style.display="block";
			nextButton.disabled = true;
		}
		if(val==""){
		    $(spaceDiv).style.display="none";
		    $(poDiv).style.display="none";
		    nextButton.disabled = false;
		}  
}
function strStartsWith(str, prefix) {
    return str.indexOf(prefix);
}

function checkGender(root){
	var gender = $("sex").childNodes[1].innerHTML.split("</legend>")[1];
	if (strStartsWith(gender,"Select") == 0){
		$(root+"submitButton").disabled = true;
		document.getElementById("errorGender").style.display="block";
	}else{
		$(root+"submitButton").disabled = false;
		document.getElementById("errorGender").style.display="none";
	}
}

function onlyNumbers(evt)
{
    var e = evt; // for trans-browser compatibility
    var charCode = e.which || e.keyCode;   
    if (charCode > 31 && (charCode <45 || charCode > 57) || charCode==45 || charCode==47||charCode==46){
        return false;
    }
    else{   
        return true;
    }
}

function onlyNumbersNotZero(evt)
{
    var e = evt; // for trans-browser compatibility
    var charCode = e.which || e.keyCode;   
    if (charCode > 31 && (charCode <45 || charCode > 57) || charCode==45 || charCode==47||charCode==46 ||charCode==48){
        return false;
    }
    else{   
        return true;
    }
}
function onlyNumbersAndDot(evt)
{
    var e = evt; // for trans-browser compatibility
    var charCode = e.which || e.keyCode;   
    if (charCode > 31 && (charCode <45 || charCode > 57) || charCode==45 || charCode==47){
        return false;
    }
    else{   
        return true;
    }
}
/*
 *Function to check for valid email, zip + 4 code and phone number. 
 * Modified for ssn validation
 * @Vineetha 
 */
function checkFormat(id,root, msgContainerId){  
		var val = $(root+":"+id).value;
		var decimalFormat ="";
		if(id == "phone" || id == "HomePhone" || id == "WorkPhone" || id == "CellPhone"){
			decimalFormat=/^(?![0]{10})+(\d{10})/;
		}else if(id == "extension"){
			decimalFormat=/^\d{4}$/;
		}else if(id == "zip"){
			decimalFormat=/(^\d{5}$)|(^\d{5}\d{4}$)/;
		}else if(id == "email" || id == "primaryEmailId" || id == "secondaryEmailId"){
			decimalFormat=/^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9-]+\.[a-zA-Z]{2,6}$/;
		}else if(id == "dea"){
			decimalFormat=/^[a-zA-Z]{2}[0-9]{7}$/;
		}else if(id == "FX"){
			decimalFormat=/^(?![0]{10})+(\d{10})/;
		}else if(id == "spi"){
			decimalFormat=/^\d{10}$/;
		}else if(id == "npi"){
			decimalFormat=/^\d{10}$/;
		}else if(id == "ssn"){
			decimalFormat=/^\d{9}$/;
		}
		
		if(val.search(decimalFormat)==-1){
		    $(msgContainerId).style.display="block";
		}else{
		    $(msgContainerId).style.display="none";
		}
		if(val==""){
		    $(msgContainerId).style.display="none";
		}  
		
}
