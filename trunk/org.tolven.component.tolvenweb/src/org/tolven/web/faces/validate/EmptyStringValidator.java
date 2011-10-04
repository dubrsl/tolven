package org.tolven.web.faces.validate;


import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

/**
 * This class validates Encounter.
 * 
 * added on 08/04/2010
 */
@SuppressWarnings("serial")
public class EmptyStringValidator implements Validator, Serializable {

	
	@Override
    public void validate(FacesContext ctx, UIComponent comp, Object value) throws ValidatorException {
		String ERROR_MESSAGE = "Validation Error: Value is required.";
    	try {
			String fieldValue = null;
			String field = (String) comp.getAttributes().get("field");
			
			if (value instanceof String) {
				fieldValue = (String) value;
			}
			ERROR_MESSAGE = "Validation Error: " + field + " is required.";
			
			if (fieldValue==null || (fieldValue!=null && (fieldValue.equals("") || fieldValue.equals("None")))) {
				throw new ValidatorException(new FacesMessage(ERROR_MESSAGE));
			}
    	}
    	catch (Exception e) {
    		throw new ValidatorException(new FacesMessage(ERROR_MESSAGE));
		}
    }
}
