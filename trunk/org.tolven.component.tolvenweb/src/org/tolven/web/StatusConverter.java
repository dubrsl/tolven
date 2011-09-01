package org.tolven.web;

import java.lang.Boolean;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;	

public class StatusConverter implements Converter{
	public Object getAsObject(FacesContext ctx, UIComponent comp, String value)throws ConverterException {
	
		return value;
	
 }
	
	public String getAsString(FacesContext context,
			UIComponent component, Object value) throws ConverterException{
		String inputValue = value.toString();
		if ("true".equals(inputValue) || "false".equals(inputValue)){
			return value.toString();
		}
		Boolean convertValue;
		convertValue = false;
		//TolvenLogger.info("STATUS INPUT " + inputValue, StatusConverter.class);
		if ( "ACTIVE".equals(inputValue)){
			convertValue = true;
		}
		return convertValue.toString();
	}
	
	
}

