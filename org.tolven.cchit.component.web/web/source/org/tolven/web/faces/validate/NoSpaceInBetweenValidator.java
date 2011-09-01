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
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

/**
 * Faces validator which allows 35 chars with no white space in between.
 * @author unni.s@cyrusxp.com
 * @since 30 Sep 2010
 */
public class NoSpaceInBetweenValidator implements Validator, Serializable { 
	
	/** Variable to hold the evaluator expression */
	private static final String NAME_EXPRESSION = "^[\\S]{0,35}$";
	/** Variable to hold the error message */
	private static final String ERROR_MESSAGE = "Validation Error: No space allowed in between.";
	
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
