package org.tolven.web.rim;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.ConverterException;
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
 * @author Srini Kandula
 * @version $Id: TrimPQConverter.java,v 1.2 2009/04/06 00:57:04 jchurin Exp $
 */  


public class TrimPQConverter extends javax.faces.convert.DoubleConverter {

	/** 
	 * Given a String containing a double floating number, convert it to a double. We use the
	 * Faces converter except when the string is null in which case we simply return 0.0 instead of
	 * an error.
	 * (non-Javadoc)
	 * @see javax.faces.convert.Converter#getAsObject(javax.faces.context.FacesContext, javax.faces.component.UIComponent, java.lang.String)
	 */
	public Object getAsObject(FacesContext ctx, UIComponent comp, String value) throws ConverterException {
		if (value==null || value.trim().length()==0) {
			return String.valueOf("");
		}
		return super.getAsString(ctx, comp, value);
	}

}
