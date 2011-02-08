package org.tolven.web.ccr;

import java.util.ArrayList;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

/**
 * Faces converter between a string and an array of string. 
 * @author John Churin
 *
 */
public class StringListConverter implements Converter {

	/** 
	 * Parse a string and return an array of strings for each space-delimited segment of the string
	 * (non-Javadoc)
	 * @see javax.faces.convert.Converter#getAsObject(javax.faces.context.FacesContext, javax.faces.component.UIComponent, java.lang.String)
	 */
	public Object getAsObject(FacesContext ctx, UIComponent comp, String name) throws ConverterException {
		List<String> components = new ArrayList<String>();
		String n[] = name.trim().split("\\s");
		for ( String s : n) {
			if (s!=null) {
				String st = s.trim();
				if (st.length()>0) components.add(s);
			}
		}
//		TolvenLogger.info( "[stringListConverter] to object: " + components, StringListConverter.class);
		return components;
	}

	/**
	 * Combine name segments with a space between them
	 */
	public String getAsString(FacesContext ctx, UIComponent comp, Object name) throws ConverterException {
		if (name==null) return "";
		List<String> components = (List<String>)name;
		StringBuffer sb = new StringBuffer( 100 );
		for (String s : components) {
			sb.append(s); sb.append(" ");
		}
//		TolvenLogger.info( "[stringListConverter], to string: " + sb.toString(), StringListConverter.class);
		return sb.toString();
	}

}
