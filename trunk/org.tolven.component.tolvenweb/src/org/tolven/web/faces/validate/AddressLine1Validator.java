/*
 *  Copyright (C) 2011 Tolven Inc
 *
 * This library is free software; you can redistribute it and/or modify it under 
 * the terms of the GNU Lesser General Public License as published by the Free 
 * Software Foundation; either version 2.1 of the License, or (at your option) 
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT 
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS 
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more 
 * details.
 *
 * Contact: info@tolvenhealth.com
 */
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
 * This class validates AddressLine1. Space separated data(without PO/P O).

 * @since 30/09/2010
 */
public class AddressLine1Validator implements Validator, Serializable { 
	
	private static final long serialVersionUID = 2156541642797429877L;
	private static final String ERROR_MESSAGE_PO = "Validation Error: No PO allowed in Address Line 1.";
	private static final String ERROR_MESSAGE_SPACE = "Validation Error: Address field requires space separated values.";
	private String errorMessage;
	
	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	@Override
    public void validate(FacesContext ctx, UIComponent comp, Object value) throws ValidatorException {
    	try {
			String addressLine1 = null;
			
			if (value instanceof String) {
				addressLine1 = (String) value;
			}

			if (addressLine1 != null) {
				if (! validateAddressLine1(addressLine1)) {
					throw new ValidatorException(new FacesMessage(getErrorMessage()));
				}
			}
    	}
    	catch (Exception e) {
    		throw new ValidatorException(new FacesMessage(getErrorMessage()));
		}
    }
		
	/**
	 * Method to validate address line 1(No PO allowed and space separated value is must).
	 * @param inputAddress
	 * @return true if validation is a success.
	 */
	private boolean validateAddressLine1(String inputAddress){
		//Expression used for checking space
//		String addressExp = "([#\\w\\.-]+\\s)+[\\w\\.-]+"; //if used error message should change accordingly.
		String addressExp = "([\\S]+\\s)+[\\S]+$";
		Pattern spacePattern = Pattern.compile(addressExp);
		Matcher spaceMatcher = spacePattern.matcher(inputAddress);
		boolean spaceValidation = spaceMatcher.find();
		if (spaceValidation) {
			//Checking for PO in address line 1.
			Pattern addressPattern = Pattern.compile("((P)O)|((P)(\\s)O)");
			Matcher addressMatcher = addressPattern.matcher(inputAddress);
			if (addressMatcher.find()) {
				setErrorMessage(ERROR_MESSAGE_PO);
				return false;
			}
		}else {
			setErrorMessage(ERROR_MESSAGE_SPACE);
			return false;
		}
		return true;
	}
}
