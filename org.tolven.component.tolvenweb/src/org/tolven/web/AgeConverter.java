/*
 *  Copyright (C) 2006 Tolven Inc 
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of 
 * the GNU Lesser General Public License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU Lesser General Public License for more details.
 * 
 * Contact: info@tolvenhealth.com
 */
package org.tolven.web;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import org.tolven.app.el.AgeFormat;

public class AgeConverter implements Converter {

	public Object getAsObject(FacesContext ctx, UIComponent comp, String value) throws ConverterException {
		if (true)
		throw new ConverterException( "Age cannot be converted to a date");
		return null;
	}
	/**
	 * TODO: Timezone, language, threshold parameters still needed.
	 */
	public String getAsString(FacesContext ctx, UIComponent comp, Object value) throws ConverterException {
		Calendar now = new GregorianCalendar();
		now.setTime( new Date());
		if (!(value instanceof Date)) throw new ConverterException( "Age converter requires a date value");
		Calendar dob = new GregorianCalendar();
		dob.setTime((Date)value);
		return AgeFormat.toAgeString( dob, now, ctx.getViewRoot().getLocale());
	}

}
