package org.tolven.web.faces.validate;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

/**
 * Faces validator for DEA number.

 * @since 30 Sep 2010
 */
@SuppressWarnings("serial")
public class DeaValidator implements Validator, Serializable {

	private static final String DEA_EXPRESSION =  "^[a-zA-Z]{2}[0-9]{7}$";
	private static final String ERROR_MESSAGE = "Validation Error: DEA number is invalid.";
	
	@Override
    public void validate(FacesContext ctx, UIComponent comp, Object value) throws ValidatorException {
    	try {
			String dea = null;
			
			if (value instanceof String) {
				dea = (String) value;
			}
						
			if (dea != null) {
				if (! validateDea(dea)) {
					throw new ValidatorException(new FacesMessage(ERROR_MESSAGE));
				}
			}
    	}
    	catch (Exception e) {
    		throw new ValidatorException(new FacesMessage(ERROR_MESSAGE));
		}
    }
			
	/**
	 * Validates the DEA number
	 * @param dea - the  dea number to validate
	 * @return true or false
	 */
	 public boolean validateDea(String dea){
		 Matcher matcher = Pattern.compile(DEA_EXPRESSION).matcher(dea);
		 return matcher.matches();
	 }
}
