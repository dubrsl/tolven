/*
 * Copyright (C) 2009 Tolven Inc

 * This library is free software; you can redistribute it and/or modify it under the terms of 
 * the GNU Lesser General Public License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;  
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU Lesser General Public License for more details.
 *
 * Contact: info@tolvenhealth.com 
 *
 * @author anil
 * @version $Id: TSValueConverter.java,v 1.2 2009/04/06 00:57:04 jchurin Exp $
 */
package org.tolven.web.rim;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.DateTimeConverter;

import org.tolven.web.util.TSDateFormat;


/**
 * Converter for TS.value
 * 
 * @author Anil
 *
 */
public class TSValueConverter extends DateTimeConverter implements Converter {
	/** 
	 * Given a String containing a date string, return HL7 Formatted Date string
	 * (non-Javadoc)
	 * @see javax.faces.convert.Converter#getAsObject(javax.faces.context.FacesContext, javax.faces.component.UIComponent, java.lang.String)
	 */
	public Object getAsObject(FacesContext ctx, UIComponent comp, String value) throws ConverterException {
		
		if (this.getType()==null || this.getType().length() == 0)  {
			setType("date");
		}
		TSDateFormat lPattern = new TSDateFormat(getLocale(), getDateStyle(), getTimeStyle());

		if (value == null || ((String)value).length() == 0){				
			return null;
		}

		String hl7FormattedDateValue;
		try
		{
			hl7FormattedDateValue = lPattern.getHl7DateFormat().format(lPattern.getSimpleDateFormat().parse(value)); // 2009
		}
		catch (ParseException e) {
			throw new ConverterException( "Nested Exception", e);
		}
		return hl7FormattedDateValue;
	}


	public String getAsString(FacesContext ctx, UIComponent comp, Object value) throws ConverterException {
		try {
			
			TSDateFormat lPattern = new TSDateFormat(getLocale(), getDateStyle(), getTimeStyle());
			
			if (value == null || ((String)value).length() == 0){				
				return "";
			}
			if (this.getType()==null || this.getType().length() == 0)  {
				setType("date");
			}
			Date date = null;
			try
			{	// When Trim is instantiated the Date value is in Hl7 Full standard.
				// First parse using HL7 Full Standard Format.
				date = lPattern.getHl7BaseStandardFormat().parse((String)value);
			}
			catch (ParseException e) {
				date = lPattern.getHl7DateFormat().parse((String)value);
			}
			
			// Format the Date as computed.
			String formattedDate = lPattern.getSimpleDateFormat().format(date);
			if(getType().equals("time"))
				formattedDate = new SimpleDateFormat("hh:mm a").format(date);
			return formattedDate;
			
		} catch (Exception e) {
			throw new ConverterException( "Nested Exception", e);
		}
	}


}
