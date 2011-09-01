package org.tolven.web.faces.validate;

import java.io.Serializable;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlInputText;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.component.html.HtmlSelectOneRadio;

import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.tolven.trim.ex.CEEx;

/**
 * This class does validation based on the value of Substance selected from the wizard. 
 * The validation effects value input in text field and should be a  numeral with optional decimal.
 * 
 * 
 * @author Vineetha
 * added on 07/14/2010
 */
public class AssessmentValidator implements Validator, Serializable {
	
	String selectedSubstance = "";
	String cigarette = "";
	String smoker ="";
	
	private static final String NUMBER_EXPRESSION = "[0-9.]+";
	private static final String ERROR_MESSAGE = "Invalid number: Non-numbers and special characters are not supported.";
	private static final String SUBSTANCE_TYPE = "Cigarette smoking tobacco";

	@Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
		CEEx ce = (CEEx) value;
		initProps(component, context);
		if(!(smoker.equals("Never smoker"))&&!(smoker.equals("Smoker, current status unknown"))&&!(smoker.equals("Unknown if ever smoked"))){
			if (SUBSTANCE_TYPE.equals(ce.getDisplayName())) {
				if (cigarette.isEmpty()){
					throw new ValidatorException(new FacesMessage("Validation Error: Pleae enter the quantity of cigarettes."));
				}
				else if (! validateNumber(cigarette)) {
					throw new ValidatorException(new FacesMessage(ERROR_MESSAGE));
				}
			}
			else {
				if (selectedSubstance.isEmpty()){
					throw new ValidatorException(new FacesMessage("Validation Error: Please enter the quantity of selected substance."));
				}
				else if (! validateNumber(selectedSubstance)) {
					throw new ValidatorException(new FacesMessage(ERROR_MESSAGE));
				}
		    }
		}
	}
	
	/**
	 * Set the values of components.
	 * 
	 * @param component
	 * @param context
	 */
	private void initProps(UIComponent component, FacesContext context) {		   
		try {
			Object cigaretteObj = component.getAttributes().get("cigarette");
			HtmlInputText cigaretteComp = (HtmlInputText) context.getViewRoot().findComponent(cigaretteObj.toString());

			if (cigaretteComp != null) {
				 cigarette = cigaretteComp.getSubmittedValue().toString();
			}
		}
		catch (Exception e) {
		   
		}
	   
		try {
			Object selectedSubstanceObj = component.getAttributes().get("selectedSubstance");
			HtmlInputText selectedSubstanceComp = (HtmlInputText) context.getViewRoot().findComponent(selectedSubstanceObj.toString());

			if (selectedSubstanceComp != null) {
				selectedSubstance = selectedSubstanceComp.getSubmittedValue().toString();
			}
		}
		catch (Exception e) {
		   
		}
		
		try{
			Object selectedSmokerObj = component.getAttributes().get("smoker");
			HtmlSelectOneMenu selectedSmokerComp = (HtmlSelectOneMenu) context.getViewRoot().findComponent(selectedSmokerObj.toString());
			
			if(selectedSmokerComp != null){
				smoker = selectedSmokerComp.getValue().toString();
			}
		}
		catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	
	/**
	 * Validates the number
	 * @param number - the number to validate
	 * @return true or false
	 */
	 public boolean validateNumber(String str){
		 return Pattern.compile(NUMBER_EXPRESSION).matcher(str).matches();
	 }
}
