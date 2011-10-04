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
 * This class validates zip code(Zip+4 code, either 5 or 9 digits).

 * added on 07/19/2010
 */
@SuppressWarnings("serial")
public class ZipCodeValidator implements Validator, Serializable {

	private static final String ZIP_EXPRESSION = "(^[0-9]{5}$)|(^[0-9]{9}$)";
	private static final String ERROR_MESSAGE = "Validation Error: ZipCode is invalid. Enter zip+4 code.";
	
	@Override
    public void validate(FacesContext ctx, UIComponent comp, Object value) throws ValidatorException {
    	try {
			String zipCode = null;
			
			if (value instanceof String) {
				zipCode = (String) value;
			}
						
			if (zipCode != null) {
				if (! validateZipcode(zipCode)) {
					throw new ValidatorException(new FacesMessage(ERROR_MESSAGE));
				}
			}
    	}
    	catch (Exception e) {
    		throw new ValidatorException(new FacesMessage(ERROR_MESSAGE));
		}
    }
			
	/**
	 * Validates the zip code.
	 * @param zipCode - the zipCode to validate.
	 * @return true or false
	 */
	 public boolean validateZipcode(String zipCode){
		 Matcher matcher = Pattern.compile(ZIP_EXPRESSION).matcher(zipCode);
		 return matcher.matches();
	 }
}
