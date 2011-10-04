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
 * Faces validator for TEL number, extension, and NPI.
 * @since 30 Sep 2010
 */
@SuppressWarnings("serial")
public class TelValidator implements Validator, Serializable {
	private static final String TEL_EXPRESSION =  "^(?![0]{10})+(\\d{10})$";
	private static final String TEL_EXT_EXPRESSION =  "^\\d{4}$";
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
			String tel = null;
			if (comp.getId() != null && comp.getId().contains("hone")) { //changed to contains("hone") so it will match Phone or phone and HomePhone as well
				setErrorMessage("Validation Error: TEL is invalid.");
			} else if (comp.getId() != null && comp.getId().equals("FX")) {
				setErrorMessage("Validation Error: FAX is invalid.");
			} else if (comp.getId() != null && comp.getId().equals("npi")) {
				setErrorMessage("Validation Error: NPI is invalid.");
			} else if (comp.getId() != null && comp.getId().equals("extension")) {
				setErrorMessage("Validation Error: Extension is invalid.");
			}
			
			if (value instanceof String) {
				tel = (String) value;
			}
			if(tel != null && !comp.getId().equals("extension")) {
				if (!validateTel(tel)) {
					throw new ValidatorException(new FacesMessage(getErrorMessage()));
				}
			} else if(tel != null && comp.getId().equals("extension")){
				if (!validateExt(tel)) {
					throw new ValidatorException(new FacesMessage(getErrorMessage()));
				}
			}
    	}
    	catch (Exception e) {
    		throw new ValidatorException(new FacesMessage(getErrorMessage()));
		}
    }
	
	/**
	 * Method to validate phone number.
	 * @param phoneNumber
	 * @return boolean
	 */
	public boolean validateTel(String phoneNumber){
		 Matcher matcher = Pattern.compile(TEL_EXPRESSION).matcher(phoneNumber);
		 return matcher.matches();
	 }
	/**
	 * Method to validate phone number.
	 * @param phoneNumber
	 * @return boolean
	 */
	public boolean validateExt(String extension){
		 Matcher matcher = Pattern.compile(TEL_EXT_EXPRESSION).matcher(extension);
		 return matcher.matches();
	 }
}
