/**
* To validate numbers and decimals in Smoking Assessments.
* Author Vineetha
* Added on 07/15/2010 
*/
function validateNumbers(root, id, errorMsg) {
	var strData = $(root+':'+id).value;	
	var errorObj = $(errorMsg);
	var iCount, iDataLen;
	var strCompare = "0123456789.";
	iDataLen = strData.length;
	
	if (iDataLen > 0) {	
		for (iCount=0; iCount < iDataLen; iCount+=1) {
			var cData = strData.charAt(iCount);
		
			if (strCompare.indexOf(cData) < 0 ){
				errorObj.innerHTML="Please enter number values";				
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
 * @author Pinky
 * Added on 07/01/2010
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
 * Method used to convert temperature from Farenheit to Centigrade.
 * @author Pinky
 * Added on 07/01/2010
 */
tempCnvFn=function(root) {
 	var unit1 = $(root+':temperatureInC');
 	var unit2 = $(root+':temperatureInF');
 	unit2.value = unit2.value.toString().replace(/[^\d\.eE-]/g,'');
 	if(unit2.value!=null&&unit2.value!="")
		unit1.value = (Math.round(((unit2.value - 32) * 5/9)*100))/100;
}

/**
 * Method used to convert weight from KG to LBS.
 * @author Pinky
 * Added on 07/01/2010
 */
weightCnvFn=function(root){
	var unit1 = $(root+':weightlbs');
 	var unit2 = $(root+':weightkg');
 	unit2.value = unit2.value.toString().replace(/[^\d\.eE-]/g,'');
 	if(unit2.value!=null&&unit2.value!="")
		unit1.value = Math.round((unit2.value*2.2046));
}

/**
 * Method to submit observation trims on submitting vital sign assessment.
 * @author Pinky
 * Added on 07/01/2010
 */
wizSubmitObservationTrims=function(element) {
    var instAjax = new Ajax.Request(
       'submitObservationTrims.ajaxas', {
           method: 'get', 
           parameters: 'element='+element, 
           onComplete: function(req) {  } 
	});
}

/**
 * Function to calculate the BMI value 
 * in two combinations of height and weight units(feet-lbs or cm-kg)
 * @author Pinky
 * Added on 07/29/2010
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
* To validate date in Smoking Assessments.
* Author Vineetha
* Added on 1/13/2011
*/
function validateDate(root, FieldquescommenceDate, FieldquesendDate, errorMsg) {
	var date1 = new Date($(root+':'+FieldquescommenceDate).value);
    var date2 = new Date($(root+':'+FieldquesendDate).value);
  
    var date3 = new Date();
   
    var errorObj = $(errorMsg);
   if ((date1!=null)&&(date2!=null))
   {
       if(date1 > date2)
       {
           errorObj.innerHTML="The Date Ended must be greater than Date Commenced";
           return false; 
      }
      else if(date1 > date3)
       {
           errorObj.innerHTML="Commence Date must be on or before Current Date";				
           return false; 
       }
       else if(date2 > date3) 
       {
           errorObj.innerHTML="End Date must be on or before Current Date";				
	       return false; 
        }
        else
        {
        	errorObj.innerHTML="";				
	       return false; 
        }
    }
    else
        {
        	errorObj.innerHTML="";				
	       return false; 
        }
}
/**
* To display Smoking Status Recodes in Smoking Assessments.
* Author Vineetha
* Added on 1/21/2011
*/
function smokingCode(value, id){
	
	$(id).innerHTML = value.split("|")[1];
}