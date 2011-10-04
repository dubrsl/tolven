package org.tolven.web.faces.validate;

import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlInputText;
import javax.faces.component.html.HtmlSelectBooleanCheckbox;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.tolven.trim.ex.CEEx;

/**
 * If PHR type notification is selected, 
 * there must be an existing PHR account selected or
 * set a checkbox to create new PHR account.
 * 
 * added on 07/08/2010
 */
@SuppressWarnings("serial")
public class PHRNotificationValidator implements Validator, Serializable {
	String existingAccountId = "";
	boolean createNewAccountFlag = false;
	
	private static final String ERROR_MESSAGE = "Validation Error: Please create or select a PHR account.";
	private static final String NOTIFICATION_TYPE = "PHR";
	
	@Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
    	try {
    		CEEx ce = (CEEx) value;
    		
			if (NOTIFICATION_TYPE.equals(ce.getDisplayName())) {
				initProps(component, context);
				
				if (! validatePHRNotification()) {
					throw new ValidatorException(new FacesMessage(ERROR_MESSAGE));
				}
			}
    	}
    	catch (Exception e) {
    		throw new ValidatorException(new FacesMessage(ERROR_MESSAGE));
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
			Object existingAccountIdObj = component.getAttributes().get("existingAccountId");
			HtmlInputText existingAccountIdComp = (HtmlInputText) context.getViewRoot().findComponent(existingAccountIdObj.toString());

			if (existingAccountIdComp != null) {
				existingAccountId = existingAccountIdComp.getSubmittedValue().toString();
			}
		}
		catch (Exception e) {
		   
		}
	   
		try {
		  	Object newAccountObj = component.getAttributes().get("createNewAccount");
		  	HtmlSelectBooleanCheckbox newAccountComp = (HtmlSelectBooleanCheckbox) context.getViewRoot().findComponent(newAccountObj.toString());

		  	if (newAccountComp != null) {
		  		createNewAccountFlag = Boolean.valueOf(newAccountComp.getSubmittedValue().toString());
		  	}
		}
		catch (Exception e) {
		   
		}
	}
	
	/**
	 * Validates PHR notification.
	 * 
	 * @return true or false
	 */
	 public boolean validatePHRNotification() {		 
		return (!existingAccountId.isEmpty() || createNewAccountFlag);
	 }
}
