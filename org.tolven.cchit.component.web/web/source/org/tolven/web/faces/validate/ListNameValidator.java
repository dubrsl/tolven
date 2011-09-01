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
 * This class validates name, allows only alphanumeric characters and white spaces.
 * 
 * @author Valsaraj
 * added on 07/08/2010
 */
public class ListNameValidator implements Validator, Serializable {
	private static final String NAME_EXPRESSION = "[a-zA-Z0-9\\s]+";
	private static final String ERROR_MESSAGE = "Invalid name: Special characters are not supported.";
	
	@Override
    public void validate(FacesContext ctx, UIComponent comp, Object value) throws ValidatorException {
    	try {
			String name = null;
			
			if (value instanceof String) {
				name = (String) value;
			}

			if (name != null) {
				if (! validateName(name)) {
					throw new ValidatorException(new FacesMessage(ERROR_MESSAGE));
				}
			}
    	}
    	catch (Exception e) {
    		throw new ValidatorException(new FacesMessage(ERROR_MESSAGE));
		}
    }
			
	/**
	 * Validates the name
	 * @param name - the name to validate
	 * @return true or false
	 */
	 public boolean validateName(String name){
		  return Pattern.compile(NAME_EXPRESSION).matcher(name).matches();
	 }
}
