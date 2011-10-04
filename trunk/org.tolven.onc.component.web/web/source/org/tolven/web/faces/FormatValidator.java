package org.tolven.web.faces;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.tolven.logging.TolvenLogger;

public class FormatValidator implements Validator, Serializable{

	public static final long serialVersionUID = 1212124L;
	// Regular expression passed as a parameter.
	private String regEX;
	// Error messages to be displayed.
	private String errorMessage;
	
	@Override
	public void validate(FacesContext ctx, UIComponent comp, Object value)throws ValidatorException {
		
		try {
			initProps(comp);
			Pattern pat = Pattern.compile(regEX);
			Matcher match = pat.matcher(value.toString());
			if(!match.matches()) {
				throw new ValidatorException(new FacesMessage(errorMessage));
			}
		} catch (NullPointerException e ) {
			throw new ValidatorException(new FacesMessage(e.getMessage()));
		} catch (Exception e) {
			throw new ValidatorException(new FacesMessage(e.getMessage()));
		}
	}
	
	/**
	 * This method will set the value of both operator and date to compare.
	 */
	private void initProps(UIComponent component) {
		try{
			regEX = (String) component.getAttributes().get("expression");
			errorMessage = (String) component.getAttributes().get("message");
			if(errorMessage.trim().length() < 1) {
				errorMessage= "Validation error";
			}
		} catch (Exception e) {
			errorMessage= "Validation error";
			TolvenLogger.info("ERROR -------> Validatioin error message not available; Message set to ''Validation error", FormatValidator.class);
		}
		
		
	}
}
