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
 * This class validates email.
 * 
 * @author Valsaraj
 * added on 06/24/2010
 */
public class EmailValidator implements Validator, Serializable {

	private static final String EMAIL_EXPRESSION =  "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	private static final String ERROR_MESSAGE = "Validation Error: Email is invalid.";
	
	@Override
    public void validate(FacesContext ctx, UIComponent comp, Object value) throws ValidatorException {
    	try {
			String email = null;
			
			if (value instanceof String) {
				email = (String) value;
			}
						
			if (email != null) {
				if (! validateEmail(email)) {
					throw new ValidatorException(new FacesMessage(ERROR_MESSAGE));
				}
			}
    	}
    	catch (Exception e) {
    		throw new ValidatorException(new FacesMessage(ERROR_MESSAGE));
		}
    }
			
	/**
	 * Validates the email address
	 * @param email - the  email address to validate
	 * @return true or false
	 */
	 public boolean validateEmail(String email){
		 Matcher matcher = Pattern.compile(EMAIL_EXPRESSION).matcher(email);
		  
		  return matcher.matches();
	 }
}
