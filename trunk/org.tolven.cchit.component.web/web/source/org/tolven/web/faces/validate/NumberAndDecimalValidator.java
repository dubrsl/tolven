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
 * This class validates number.
 * 
 * @author Pinky
 * added on 06/24/2010
 */
public class NumberAndDecimalValidator implements Validator, Serializable {

	private static final String NUMBER_EXPRESSION1 =  "^\\d+$";
	private static final String NUMBER_EXPRESSION2 = "^([0-9]*|\\d*\\.\\d{1}?\\d*)$";
	private static final String ERROR_MESSAGE = "Validation Error: Enter only Numbers.";
	
	@Override
    public void validate(FacesContext ctx, UIComponent comp, Object value) throws ValidatorException {
		String number = null;
    	try {
			String attributeFactor = comp.getAttributes().get("attributeFactor").toString();
				number = value.toString();		
		
						
			if (number != null) {
				if (! validateNumberOrDecimal(number,attributeFactor)) {
					throw new ValidatorException(new FacesMessage("'"+number+"'"+"must be a number consisting of one or more digits"));
				}
			}
    	}
    	catch (Exception e) {
			throw new ValidatorException(new FacesMessage("'"+number+"'"+"must be a number consisting of one or more digits"));
		}
    }
			
	/**
	 * Validates the input entered by the user.
	 * @param number - the  number to validate
	 * @param attributeFactor - factor to decide whether to validate only numerals or decimal.
	 * @return true or false
	 */
	 public boolean validateNumberOrDecimal(String number, String attributeFactor){
		 Matcher matcher = null;
		if(attributeFactor.equals("number"))
			matcher = Pattern.compile(NUMBER_EXPRESSION1).matcher(number);
		
		else if(attributeFactor.equals("numberAndDecimal"))
			matcher = Pattern.compile(NUMBER_EXPRESSION2).matcher(number);
		
		return matcher.matches();
	 }
}
