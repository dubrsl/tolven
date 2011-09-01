/** **************** JavaScript Functions required for FDB ****************** 
 * @author Suja
**/
/**
 * This function is used to trim whitespaces in a string
 */
function trim(str){
	if (str!=null) {
		return str.replace(/^\s+/, '').replace(/\s+$/, '');
	}
}

/**
 * To show Medication popup
 */
wizAddMedicationWidget1=function(index) {
    openMedPopup1()
    _medicationVal=index;
}

/**
 * To show Medication popup
 */
function openMedPopup1() {
	if ( document.getElementById("rowId"))
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
	if (document.getElementById("fdbType"))
        document.getElementById("fdbType").value ="medication";           

}

/**
 * To show Medication popup
 */
function openMedPopup(popupId,formId,element,status,sourceType) {
	loadValuesFromFDB(formId,element,status,0,sourceType);	
	if ( $(formId+":rowId"))
   		$(formId+":rowId").value = "0";
	$(formId).setAttribute("ajaxSubmit3InProgress", 'true');
    $(formId+":"+popupId).style.display="block";
   
    if (screen) { 
	   leftPos = (screen.width / 2) - 251;
	   topPos = (screen.height / 2) - 162;
	   $(formId+":"+popupId).style.left=leftPos+"px";
	   $(formId+":"+popupId).style.top=topPos+"px";
    }  
	$(formId+":"+status+"startLimit").innerHTML = "1";
    $(formId+":"+status+"endLimit").innerHTML = "14";  	                  
}

/**
 * FDB popup arrow event - First
 */
function selectFirst(type) {
    document.getElementById("rowId").value= 0;
    
    if (type=='DrugAllergy') {
    	document.getElementById("startLimitDrugAllergy").innerHTML ="1";
    	document.getElementById("endLimitDrugAllergy").innerHTML = "14";
    }
    else {
    	document.getElementById("startLimit").innerHTML ="1";
    	document.getElementById("endLimit").innerHTML = "14";
    }
    
    loadDataFromFDB(type);
}

/**
 * FDB popup arrow event - Previous
 */
function selectPrevious(type) {
	var suffix = '';
	
	if (type=='DrugAllergy') {
		suffix = type;
	}
	
    var formId = document.getElementById("formIdValue").value;
    var end = document.getElementById(formId+":totalcount"+suffix).innerHTML;
    var endLim = end.split(" ");
  
    if (document.getElementById("rowId").value == "last") {
        document.getElementById("startLimit"+suffix).innerHTML =endLim[1] - 28;
        document.getElementById("endLimit"+suffix).innerHTML = endLim[1] - 14;
        document.getElementById("rowId").value = parseInt(endLim[1] / 14 - 14);
    }else {
        var i = document.getElementById("rowId").value;
        if (i >0) {
            i--;
        } 
        document.getElementById("rowId").value = i;
        document.getElementById("startLimit"+suffix).innerHTML =(i * 14) + 1;
        document.getElementById("endLimit"+suffix).innerHTML = ( i * 14) + 14;  
    }
    loadDataFromFDB(type);
}

/**
 * FDB popup arrow event - Next
 */
function selectNext(type) {	
   	if (document.getElementById("rowId").value != "last") {
   		var suffix = '';

		if (type=='DrugAllergy') {
			suffix = type;
		}
		
	   var i = document.getElementById("rowId").value;
	   var formId = document.getElementById("formIdValue").value;
	   var end = document.getElementById(formId+":totalcount"+suffix).innerHTML;
	   var endLim = end.split(" ");
	   i++;
	   document.getElementById("rowId").value = i;  
	   
	   if (((i * 14) + 1) < (endLim[1] - 14)) {
	       document.getElementById("startLimit"+suffix).innerHTML =(i * 14) + 1;
	       document.getElementById("endLimit"+suffix).innerHTML = ( i * 14) + 14;
	       loadDataFromFDB(type);
	   }
	}
}

/**
 * FDB popup arrow event - Last
 */
function selectLast(type) {
	var suffix = '';

	if (type=='DrugAllergy') {
		suffix = type;
	}
	
    document.getElementById("rowId").value = "last";  
    var formId = document.getElementById("formIdValue").value;
    var end = document.getElementById(formId+":totalcount"+suffix).innerHTML;
    var endLim = end.split(" ");
    document.getElementById("startLimit"+suffix).innerHTML =endLim[1] - 14;
    document.getElementById("endLimit"+suffix).innerHTML = endLim[1];  
    loadDataFromFDB(type);
}

/**
 * To load values from FDB db
 */
function loadDataFromFDB(type) {
    var formId = document.getElementById("formIdValue").value;
    var rowId = document.getElementById("rowId").value;
    var suffix = '';
    
    if (type=='DrugAllergy') {
		suffix = type;
	}
	else {
	    type="medication";
	    
	    if (document.getElementById("fdbType"))
			type=document.getElementById("fdbType").value;
	}
    if (type=="medication") {
		var myAjax = new Ajax.Request(
			'drugsUpdate15.ajaxfdb', {
				method: 'get', 
				parameters: '&filter_condition='+$(formId +":filterText").value+'&rowid='+rowId, 
				onProcessing:function() {showProcessing()},
				onFailure: function() {hideProcessing();location.href=document.URL;},
				onComplete: function(response) {
					var drugs= response.responseText.split('|');
			     	var totalCount = drugs.length - 1;
			     	var table = $(formId + ":drugTable");
			     	var rowCount = table.rows.length;
					var j = rowCount -1 ;
					var flag;
					for (var i=j; i>=0; i--) {
					   $(formId + ":drugTable").deleteRow(i);
					   j--;
					}
					flag1 = 0;
					for (var k=0; k<totalCount; k++) {
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
						if ((document.getElementById("formIdValue").value).startsWith("echr")) {
                            if ((document.getElementById("formIdValue").value).startsWith("echrresponses")) {
                                document.getElementById("code"+k).onclick=handleClickReq;
								document.getElementById("med"+k).onclick=handleMedClickReq;
							}else {
								document.getElementById("code"+k).onclick=handleSelect;
								document.getElementById("med"+k).onclick=handleMedSelect;
							}	
						}else {
							document.getElementById("code"+k).onclick=handleClick1;
							document.getElementById("med"+k).onclick=handleMedClick1;
						}
						if (flag1 == 0) {
							$(formId + ":drugTable").rows[k].className="odd";
							flag1 =1;
						}else if (flag1 ==1) {
							$(formId + ":drugTable").rows[k].className="even";
							flag1 =0;
						}
						$(formId + ":drugTable").rows[k].style.cursor="pointer";
						$(formId + ":drugTable").rows[k].cells[0].style.textDecoration="underline";
						$(formId + ":drugTable").rows[k].cells[1].style.textDecoration="underline";
                    }
					if ($(formId + ":filterText").value != "") {
                        $(formId + ":filteredcount").innerHTML=" / "+totalCount+" filtered items";
					}else {
						$(formId + ":filteredcount").innerHTML="";
					}
					hideProcessing();
                }
        });
			
        var drugAjax = new Ajax.Request(
	       'getTotalDrugCount.ajaxfdb', {
	           method : 'get',
	           parameters: '&filter='+$(formId +":filterText").value,
	           onFailure: function() {
	               location.href=document.URL;
	           },
	           onComplete: function (response) {
	               document.getElementById(formId+":totalcount").innerHTML = " " +response.responseText + " items";
	           }	  
	   });
	}else {
	   var myAjax = new Ajax.Request(
            'allergyUpdate15.ajaxfdb', {
                method: 'get', 
	            parameters: '&filter_condition='+$(formId +":filterText"+suffix).value+'&rowid='+rowId, 
			    onProcessing:function() {showProcessing()},
			    onFailure: function() {hideProcessing();location.href=document.URL;},
                onComplete: function(response) {
                    var allergies= response.responseText.split('|');
					var totalCount = allergies.length - 1;
					var table = $(formId + ":allergyTable");
					var rowCount = table.rows.length;
					var j = rowCount -1 ;
					var flag;
					for (var i=j; i>=0; i--) {
                        $(formId + ":allergyTable").deleteRow(i);
                        j--;
                    }
					flag1 = 0;
					for (var k=0; k<totalCount; k++) {
                        var allergy = allergies[k].split('$');
						var row = $(formId + ":allergyTable").insertRow(k);
						row1 = $(formId + ":allergyTable").rows[k];
						var cell1 = row1.insertCell(0);
						var cell2 = row1.insertCell(1);
						cell1.width="290px";
						cell2.width="175px";
						var text = document.createTextNode(allergy[0]);
						var text1 = document.createTextNode(allergy[1]);
						cell1.appendChild(text);
						cell2.appendChild(text1)
						$(formId + ":allergyTable").rows[k].cells[0].id="code"+k;
						$(formId + ":allergyTable").rows[k].id="allergyFromFDB"+k;
						var allergyName = document.getElementById("allergyFromFDB"+k).childNodes[0].innerHTML;
						if ((document.getElementById("formIdValue").value).startsWith("echr")) {
                            document.getElementById("code"+k).onclick=handleClickAllergy;
                        }else {
                            document.getElementById("code"+k).onclick=handleClickAllergy1;
                        }
                        if (flag1 == 0) {
                            $(formId + ":allergyTable").rows[k].className="odd";
							flag1 =1;
						}else if (flag1 ==1) {
							$(formId + ":allergyTable").rows[k].className="even";
							flag1 =0;
						}
						$(formId + ":allergyTable").rows[k].style.cursor="pointer";
						$(formId + ":allergyTable").rows[k].cells[0].style.textDecoration="underline";
                    }
                    if ($(formId + ":filterText"+suffix).value != "") {
                        $(formId + ":filteredcount"+suffix).innerHTML=" / "+totalCount+" filtered items";
						if (totalCount > 14) {
                            $(formId + ":first_img"+suffix).style.visibility = "visible";
							$(formId + ":previous_img"+suffix).style.visibility = "visible";
							$(formId + ":next_img"+suffix).style.visibility = "visible";
							$(formId + ":last_img"+suffix).style.visibility = "visible";
						}	
					}else {
						$(formId + ":filteredcount"+suffix).innerHTML="";
					}
					hideProcessing();
                }
        });
				
        var drugAjax = new Ajax.Request(
            'getTotalAllergyCount.ajaxfdb', {
                method : 'get',
                parameters: '&filter='+$(formId +":filterText"+suffix).value,
				onFailure: function() {
                    location.href=document.URL;
				},
				onComplete: function (response) {
					document.getElementById(formId+":totalcount"+suffix).innerHTML = " " +response.responseText + " items";
				}	  
		});
    } 		
}

function getLimit() {
    return document.getElementById("rowId").value + 14;
}

/**
 * This function handles click events in FDB popup
 */
function handleSelect(obj) {
    var formId = document.getElementById("formIdValue").value;
    var element = document.getElementById("elementValue").value;
    $(formId + ":hiddenDrugName").value = obj.target.parentNode.childNodes[1].innerHTML;
    var drug = obj.target.parentNode.childNodes[1].innerHTML.replace("%","_");
    var priorMedicines = $(formId + ":priorMedicines").value;
    var priorAllergies = $(formId + ":priorAllergies").value;
    var myAjax = new Ajax.Request(
	   'fdbCollect.ajaxfdb', {
            method: 'get', 
            parameters: '&drug_name='+drug+'&element='+element+'&priorMed='+priorMedicines+'&priorAlleg='+priorAllergies, 
            onFailure: function() {
                location.href=document.URL;
            },
            onComplete: function(response) {
                var fullResponse = response.responseText;
                var responseFDB = fullResponse.split('|');
                document.getElementById(formId+":hasDrugDrugInterraction").value=responseFDB[0];
                document.getElementById(formId+":hasDrugFoodInterraction").value=responseFDB[1];
                document.getElementById(formId+":hasDuplicateTherapy").value=responseFDB[2];
                document.getElementById(formId+":hasDrugAllergy").value=responseFDB[3];
                document.getElementById(formId+":strength").value=responseFDB[4];
                document.getElementById(formId+":minmax").value=responseFDB[5];
                document.getElementById(formId+":route").value=responseFDB[6];
            }
    });
	$(formId + ":medicineSelected").value = obj.target.parentNode.childNodes[1].innerHTML;
    closeMedPopUp();
}

function showProcessing() {
    document.getElementById("ajaxLoader").style.display="block";
}

function hideProcessing() {
    document.getElementById("ajaxLoader").style.display="none";
}

/**
 * FDB filtering
 */
function startFilteringDrug(event , obj , formId ,element, status,source) {
	if (event.keyCode != 13) {
		 loadValuesFromFDB(formId,element,status,0,source);		 
    }
}

/**
 * To close FDB popup
 */
function closeMedPopUp() {
    if (document.getElementById("formIdValue"))
        var formId =document.getElementById("formIdValue").value;
    if (document.getElementById("elementValue"))
        var element = document.getElementById("elementValue").value;
    if (document.getElementById("medicationFromFDBPopUP"))
        document.getElementById("medicationFromFDBPopUP").style.display="none";
}

/**
 * To handle FDB Medication Click
 */
function handleMedSelect(obj) {
    //document.getElementById("ajaxLoader").style.display="block";  
    var formId = document.getElementById("formIdValue").value;
    var element = document.getElementById("elementValue").value;
    $(formId + ":hiddenDrugName").value = obj.target.innerHTML;
    var drug = obj.target.innerHTML;
    var code = obj.target.parentNode.childNodes[0].innerHTML;
    loadFDBMedicationDetails(drug,code);  
}

/**
 * To load FDB Medication Details
 */
function loadFDBMedicationDetails(drug, code) {
	var obj = document.getElementById('medicationValue'+_medicationVal);
	
	if (obj==null) {		
		medCodeObj = $(_medicationVal + 'Code');	
		tempValue = trim(medCodeObj.value);
		medCodeObj.value = tempValue=="" ?  code : tempValue + "," + code;
		$(_medicationVal).checked = true;
	}
	else {
	    document.getElementById('medicationValue'+_medicationVal).innerHTML=drug;
	    $('medName'+_medicationVal).value=drug;
	    $('medCode'+_medicationVal).value=code;
	    var formId = document.getElementById("formIdValue").value;
	    //if ($(formId + ":mainMedication")) {
	    var myAjax = new Ajax.Request(
	        'getMedicationDetails.ajaxfdb', {
	            method: 'get', 
	            parameters: '&medicine='+escape(drug)+'&code='+code,//+'&element='+element+'&priorMed='+priorMedicines+'&priorAlleg='+priorAllergies, 
	            onFailure: function() {
	                location.href=document.URL;
	            },
	            onComplete: function(response) {
	                var fullResponse = response.responseText;
	                var responseFDB = fullResponse.split('|');
	                $('medStrength'+_medicationVal).value=responseFDB[0];
	                //$('medRoute'+_medicationVal).value=responseFDB[1];
	                $('medForm'+_medicationVal).value=responseFDB[2];
	                $('medClass'+_medicationVal).value=responseFDB[3];
	                var len = document.getElementById('medRoute'+_medicationVal).options.length;
	                flag=false;
	                for (i=0;i<len;i++) {
	                    str=document.getElementById('medRoute'+_medicationVal).options[i].text;
	                    if (str.split(responseFDB[1]).length>1){
	                        flag=true;
	                        document.getElementById('medRoute'+_medicationVal).options[i].selected=true;
	                    }    
	                }
	                if (flag == false)
	                     document.getElementById('medRoute'+_medicationVal).options[len-1].selected=true;
	            }
	    });
	}
	
    closeMedPopUp();
}

/**
 * To show FDB Medications
 */
showFDBMedications=function(path) { 
	new Ajax.Updater('medicationDiv', '../wizard/fdbMedication.jsf', {
	    parameters: { element: path }, evalScripts:true, 
	    onComplete:function(req) {
	    	wizHideAjaxLoader();
	    }
	});
}

/**
 * To show FDB Medications
 */
showFDBMedicationHistory=function(path) { 
	new Ajax.Updater('medicationDiv', '../wizard/fdbMedicationHistory.jsf', {
	    parameters: { element: path }, evalScripts:true, 
	    onComplete:function(req) {
	    	wizHideAjaxLoader();
	    }
	});
}

/**
 * To save FDB Medication.
 *
 * modified on 01/14/2010 to save NDC code in medication history.
 * @author Valsaraj Viswanathan 
 */
wizSaveFDBMedication=function(widgetId, anchorId, element, root, actionType, widgetIndex, template) {
	var rootForm = $(root);
	var currentStep = 1 * rootForm.getAttribute('currentStep');
	var lastStep = 1 * rootForm.getAttribute('lastStep');
	var _medName = trim($('medName'+widgetIndex).value);
	var _medCode = trim($('medCode'+widgetIndex).value);
	var _medStrength = trim($('medStrength'+widgetIndex).value);
	var _medForm = trim($('medForm'+widgetIndex).value);
	var _medRoute = trim($('medRoute'+widgetIndex).value);
	var _medDose = trim($('medDose'+widgetIndex).value);
	var _medFrequency = trim($('medFrequency'+widgetIndex).value);	
	var _medDC = trim($('medDC'+widgetIndex).value);
	var _medClass = trim($('medClass'+widgetIndex).value);
	var _medInstruction = trim($('medInstruction'+widgetIndex).value);
	var _params = "";
	var _newRowStr = "";
	
	if (_medName!="") { 
	 	_newRowStr+= "<td>"+ _medName+"</td>";
	 	medicationName = escape(_medName);
	    _params+= '&medicationName='+medicationName;
	}
	else {
	 	$('msgMedication').innerHTML="Please enter Medication Name ...";
	 	fade('msgMedication');
       	if ($('medicationValueHREF'))
         	$('medicationValueHREF').show();
	 	return;
	}
	medicationCode = escape(_medCode);
	_params+= '&medicationCode='+medicationCode;
	if (_medClass!="") { 
	 _newRowStr+= "<td>"+ _medClass+"</td>"; 
	    _params+= '&_medClass='+_medClass;
	  }
	else {
	 _newRowStr+= "<td></td>";
	}
	if (_medStrength!="") { 
	 _newRowStr+= "<td>"+ _medStrength+"</td>"; 
	    _params+= '&_medStrength='+escape(_medStrength);
	  }
	else {
	 _newRowStr+= "<td></td>";
	}
	if (_medForm!="") { 
	 _newRowStr+= "<td>"+ _medForm+"</td>"; 
	    _params+= '&_medForm='+_medForm;
	  }
	else {
	 _newRowStr+= "<td></td>";
	}
	if (_medRoute!="") { 
	 _newRowStr+= "<td>"+ _medRoute+"</td>"; 
	    _params+= '&_medRoute='+_medRoute;
	  }
	else {
	 _newRowStr+= "<td></td>";
	}
	if (_medDose!="") { 
	 _newRowStr+= "<td>"+ _medDose+"</td>"; 
	    _params+= '&_medDose='+_medDose;
	  }
	else {
	 _newRowStr+= "<td></td>";
	}
	if (_medFrequency!="") { 
	 _newRowStr+= "<td>"+ _medFrequency+"</td>"; 
	    _params+= '&_medFrequency='+_medFrequency;
	  }
	else {
	 _newRowStr+= "<td></td>";
	}
	
	if (template != 'path/tempMedicationHistory') {
		var _medStart = trim($('FieldquesmedStart'+widgetIndex).value);
		
		if (_medStart!="") { 
			_newRowStr+= "<td>"+ _medStart+"</td>"; 
		    _params+= '&_medStart='+_medStart;
		}
		else {
			_newRowStr+= "<td></td>";
		}
	}
		
	if (_medDC!="") { 
	 _newRowStr+= "<td>"+ _medDC+"</td>"; 
	    _params+= '&_medDC='+_medDC;
	  }
	else {
	 _newRowStr+= "<td></td>";
	}
	if (_medInstruction!="") { 
	 _newRowStr+= "<td>"+ _medInstruction+"</td>"; 
	    _params+= '&_medInstruction='+_medInstruction;
	  }
	else {
	 _newRowStr+= "<td></td>";
	}
	_newRowStr+= "<td></td>";
	var param = "";
	if (actionType==0) {
	   var newRow = $(root+':'+widgetId+"TBL").insertRow($(root+':'+widgetId+"TBL").rows.length-2);
		$(newRow).update(_newRowStr);
	   param = 'actionType=0&element='+element+_params;
	}
	else {
	  $(root+":"+anchorId+widgetIndex).update(_newRowStr);
	  param = 'actionType=1&element='+element+'&widgetIndex='+widgetIndex+_params;
	}
	 
	wizShowAjaxLoader();
	param += '&template='+template;
	$('msgMedication').innerHTML="";
	
	if (template=='path/tempMedicationHistory') {
		var ndcAjax = new Ajax.Request(
		     					'retrieveNDCInformation.ajaxfdb',
		     					{
		     						method: 'get',
		     						parameters: '&drug_name='+medicationName+'&drug_code='+medicationCode+'&element='+element,
		     						onSuccess: function(response) {
		     							responseArray = response.responseText.split('|');
		     							
		     							if (responseArray.length > 1) {
			     							ndcCodeParts = responseArray[1].split(':');
			     							
			     							if (ndcCodeParts.length > 1) {
		     									ndcCode = ndcCodeParts[1];
		     									param += '&ndcCode=' + ndcCode;
		     									manageMedication(param, template, element, actionType);
			     							}
			     						}
		     						},
		     						onFailure: function(response) {
		     							displayError(response, param);
		     							document.getElementById("ajaxLoader").style.display="none";	
		     						}
		     					});
	}
	else {
		manageMedication(param, template, element, actionType);	  
	}
	
	$(root+':'+widgetId+widgetIndex).hide();
	
	if ($(root+':'+anchorId)!=null)
		$(root+':'+anchorId).show();
}

/**
 * To manage Medication.
 *
 * Added on 01/14/2010
 * @author Valsaraj Viswanathan
 */
function manageMedication(param, template, element, actionType) {
	var instAjax = new Ajax.Request(
						'manageMedication.ajaxfdb', 
					  	{
							method: 'get', 
						  	parameters: param,
						  	onComplete: function(req) {
					            if (req.responseText=="Success") {
					            	if (template!=null && template!='') {
					            		showFDBMedicationHistory(element);	            		
					            	}
					            	else {
					            		showFDBMedications(element);
					            	}
				
					                if (actionType==0)
					                  $('msgMedication').innerHTML="Medication has been added ...";
					                else
					                  $('msgMedication').innerHTML="Medication has been updated ...";
					               
					                fade('msgMedication');
					            }
					            else {
									$('msgMedication').innerHTML=req.responseText;
					                wizHideAjaxLoader();
					            }                  
					        } 
					  });
}

/**
 * To remove FDB Medication
 */
wizRemoveFDBMedication = function(element,root,widgetIndex) {
 	$(root+":saveMedication"+widgetIndex).remove();
  	wizShowAjaxLoader();
  	$('msgMedication').innerHTML="";
  	var instAjax = new Ajax.Request(
    	'manageMedication.ajaxfdb', 
      	{
	        method: 'get', 
	        parameters: 'actionType=2&element='+element+'&widgetIndex='+widgetIndex, 
	        onComplete: function(req) {
	    		showFDBMedications(element);
	          	$('msgMedication').innerHTML="Medication has been removed ...";
	          	fade('msgMedication');
	        } 
	      });
}

/**
 * To show Medication popup
 */
wizAddMedicationWidget=function(popupId,formId,element,status,sourceType) {
    openMedPopup(popupId,formId,element,status,sourceType);   
}
/** ************************************************************************* **/


/**
 * It is a common function used for display new widget on 'Add New' event
 * @author Suja
 * added on 06/23/2010 
 */
wizAddWidget = function (widgetId, anchorId, root) {
	$(root + ":" + anchorId).hide();
	$(root + ":" + widgetId).show();
};


function addFdbDrug(code,name,formId,status){
	var rootForm = $(formId);
	$(formId + ":fdbDrugCode").value = code;
	$(formId + ":fdbDrugName").value = name;
	$(formId + ":drugStatus").value = status;
	$(formId + ":computeEnable").value = "true";
	$(formId + ":computeAction").value = 'add';
	var wipNode = rootForm.parentNode;
	var currentStep = 1 * rootForm.getAttribute('currentStep');
	ajaxSubmit4(rootForm, wipNode.id,currentStep);
	$(formId + ":computeEnable").value = "false";
}
function selectFdbDrug(code,name,formId,status){
	var rootForm = $(formId);
	$(formId + ":fdbDrugCode").value = code;
	$(formId + ":fdbDrugName").value = name;
	$(formId + ":computeEnable").value = "true";
	var wipNode = rootForm.parentNode;
	var currentStep = 1 * rootForm.getAttribute('currentStep');
	ajaxSubmit4(rootForm, wipNode.id,currentStep);		
}


function removeFdbDrug(formId,index,status){
	var rootForm = $(formId);
	$(formId + ":drugStatus").value = status;
	$(formId + ":computeEnable").value = "true";
	$(formId + ":computePosition").value = index;
	$(formId + ":computeAction").value = 'remove';
	var wipNode = rootForm.parentNode;
	var currentStep = 1 * rootForm.getAttribute('currentStep');
	ajaxSubmit4(rootForm, wipNode.id,currentStep);
	$(formId + ":computeEnable").value = "false";	
}
//method to confirm deleting unsaved medications in medications document wizard
function confirmDeleteUnsavedMedications(element,formId,nextStepNum){
	if (confirm("Unsaved data on the form will be deleted. Do you want to continue?")) {
		deleteUnsavedMedications(element,formId,nextStepNum);
		return false;
	}
}

//method to delete unsaved medications in medications document wizard
//this triggers RemoveDisabledMedication.java
function deleteUnsavedMedications(element,formId,nextStepNum){
	 var rootForm = $(formId);
	 stopAsync(formId);
	 $(formId + ":computeEnable").value = "true";
	 $(formId + ":computeAction").value = 'removeUnsaved';
	 var currentStep = 1 * rootForm.getAttribute('currentStep');
	 var wipNode = rootForm.parentNode;
	 ajaxSubmit4(rootForm, wipNode.id, currentStep);
}


function clickFirst(formId,element,status,sourceType){
	loadValuesFromFDB(formId,element,status,1,sourceType);
}

function clickPrevious(formId,element,status,sourceType){
	loadValuesFromFDB(formId,element,status,2,sourceType);
}
function clickNext(formId,element,status,sourceType) {
	loadValuesFromFDB(formId,element,status,3,sourceType);	
}
function clickLast(formId,element,status,sourceType){
	loadValuesFromFDB(formId,element,status,4,sourceType);		
}

function loadValuesFromFDB(formId,element,status,actionType,sourceType){	
	var rowId = $(formId+":rowId").value;
	var filterD = escape($(formId +":"+status+"filterText").value);
	if(sourceType == "selectDrugAllergy"){ 
		var myAjax = new Ajax.Request('allergyUpdate15.ajaxfdb', 
				{method: 'get', 
				 parameters: '&filter_condition='+filterD+'&rowid='+rowId+'&actionType='+actionType, 
				 onProcessing:function(){showProcessing(formId)},
				 onFailure: function() {hideProcessing(formId);location.href=document.URL;},
				 onComplete: function(response) {
						try{
						var resp= response.responseText.evalJSON();
						var totalCount = resp.allergies.length;
						var table = $(formId + ":drugAllergyTable");
						var drugAllergiesTblBody = "<tbody>";
						for(var d=0;d<totalCount;d++){							
							drugAllergiesTblBody+="<tr class='"+(d%2==0?"even":"odd")+"'>";
							drugAllergiesTblBody+="<td><a href='#' onclick=\"selectDrugAllergy('"+resp.allergies[d].allergen+"','"+resp.allergies[d].type+"','"+resp.allergies[d].code+"','"+formId+"')\">"+resp.allergies[d].allergen+"</a></td>";
							drugAllergiesTblBody+="<td>"+resp.allergies[d].type+"</td>";
							drugAllergiesTblBody+="</tr>";								
						}
						drugAllergiesTblBody += "</tbody>";
						$(formId+":drugAllergyTable").update(drugAllergiesTblBody);
						$(formId+":totalcount").innerHTML = " " +resp.totalAllergies + " items";
						$(formId+":startLimit").innerHTML = " " +resp.start;
						$(formId+":endLimit").innerHTML = " " +resp.end;
						$(formId+":rowId").value = resp.offSet;
						
						}catch(e){
							alert(e);
						}
						hideProcessing(formId);
					}
				});	
	}else if(sourceType == 'addFdbDrug' || sourceType == 'saveDrug'){
		var myAjax = new Ajax.Request('drugsUpdate15.ajaxfdb', 
			{method: 'get', 
			 parameters: '&filter_condition='+filterD+'&rowid='+rowId+'&actionType='+actionType, 
			 onProcessing:function(){showProcessing(formId)},
			 onFailure: function() {hideProcessing(formId);location.href=document.URL;},
			  onComplete: function(response) {
					//var drugs= response.responseText.split('|');
						
					//var respText = "{drugs:[{\'id\':\'545576\',\'description\':\'A-MANTLE Topical //Cream\',brand:\'brandName\'},{\'id\':\'545577\',\'description\':\'A-MANTLE Topical //Cream\',brand:\'brandName\'},{\'id\':\'545578\',\'description\':\'A-MANTLE Topical Cream\',brand:\'brandName\'}],total:'100'}";
					try{
					var resp= response.responseText.evalJSON();
					var totalCount = resp.drugs.length;
					var table = $(formId + ":drugTable");
					var drugsTblBody = "<tbody>";
					for(var d=0;d<totalCount;d++){							
						drugsTblBody+="<tr class='"+(d%2==0?"even":"odd")+"'>";
						if(sourceType =='addFdbDrug'){
							drugsTblBody+="<td><a href='#' onclick=\"addFdbDrug('"+resp.drugs[d].code+"','"+resp.drugs[d].name+"','"+formId+"','"+status+"')\">"+resp.drugs[d].code+"</a></td>";
							drugsTblBody+="<td><a href='#' onclick=\"addFdbDrug('"+resp.drugs[d].code+"','"+resp.drugs[d].name+"','"+formId+"','"+status+"')\">"+resp.drugs[d].name+"</a></td>";
						}else if(sourceType =='saveDrug'){
							drugsTblBody+="<td><a href='#' onclick=\"saveDrug('"+resp.drugs[d].code+"','"+resp.drugs[d].name+"','"+formId+"','"+element+"')\">"+resp.drugs[d].code+"</a></td>"
							drugsTblBody+="<td><a href='#' onclick=\"saveDrug('"+resp.drugs[d].code+"','"+resp.drugs[d].name+"','"+formId+"','"+element+"')\">"+resp.drugs[d].name+"</a></td>"
						}
						drugsTblBody+="</tr>";								
					}
					drugsTblBody += "</tbody>";
					$(formId+":"+status+"drugTable").update(drugsTblBody);
					$(formId+":"+status+"totalcount").innerHTML = " " +resp.totalDrugs + " items";
				$(formId+":"+status+"startLimit").innerHTML = " " +resp.start;
				$(formId+":"+status+"endLimit").innerHTML = " " +resp.end;
					$(formId+":rowId").value = resp.offSet;
					
					}catch(e){
						alert(e);
					}
					hideProcessing(formId);
				}
			});	
	}
}
