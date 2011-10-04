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
import java.util.Calendar;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.tolven.logging.TolvenLogger;
import org.tolven.trim.ex.HL7DateFormatUtility;
import org.tolven.trim.ex.TSEx;

/**
 * This class is the faces validator to compare the date entered with another date.  
 * Comparison is based on the operator selected
 * @since 11/16/2009
 */
public class EndTimeValidator implements Validator, Serializable {
	private static final long serialVersionUID = 795660183645635478L;
	private Date startDate;
	private String days = "";
		
	public void validate(FacesContext ctx, UIComponent comp, Object value) throws ValidatorException {
		try {
			Date dateEntered = null;
			if (value instanceof String) {
				dateEntered = HL7DateFormatUtility.parseDate((String)value);
			}else {
				dateEntered = ((TSEx)value).getDate();	
			}
			
			if (dateEntered != null) {
				String errorMessage = "";
				boolean isValid = true;
				initProps(comp);
				if (startDate != null) {
					Calendar startCal = Calendar.getInstance();
					startCal.setTime(startDate);
					startCal.add(Calendar.DAY_OF_WEEK, Integer.parseInt(days));
					Date dateAfter30Days = startCal.getTime();
					if (dateEntered.after(startDate)) {
						if (dateAfter30Days.after(dateEntered)) {
							errorMessage = "Validation Error: The Difference between Active Start Time and Active End Time must be atleast 30 Days";
							isValid = false;
						}
					} else {
						errorMessage = "Validation Error: Active End Time should be greater than the Active Start Time";
						isValid = false;
					}
					if (!isValid) {
						throw new ValidatorException(new FacesMessage(
								errorMessage));
					}
				}
			}
		} 
		catch(Exception e) {
			throw new ValidatorException(new FacesMessage("Validation Error: Please enter a valid date"));
		}		
	}
	
	/**
	 * This method will set the value of both operator and date to compare.
	 * @throws Exception 
	 */
	private void initProps(UIComponent component) throws Exception {
		Object compareVal = component.getAttributes().get("compareWith");	
		days = (String) component.getAttributes().get("days");
		
		if (compareVal != null) {
			try {
				if (compareVal instanceof String) {
					startDate = HL7DateFormatUtility.parseDate((String)compareVal);
				} else {
					startDate = ((TSEx)compareVal).getDate();	
				}
			} 
			catch (Exception e) {		
				TolvenLogger.error("Error in parsing the Date : "+ e, EndTimeValidator.class);
				throw e;
			}
		}
	}
}