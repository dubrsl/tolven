package org.tolven.web.rim;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.ConverterException;

public class DoubleConverter extends javax.faces.convert.DoubleConverter {

	/** 
	 * Given a String containing a double floating number, convert it to a double. We use the
	 * Faces converter except when the string is null in which case we simply return 0.0 instead of
	 * an error.
	 * (non-Javadoc)
	 * @see javax.faces.convert.Converter#getAsObject(javax.faces.context.FacesContext, javax.faces.component.UIComponent, java.lang.String)
	 */
	public Object getAsObject(FacesContext ctx, UIComponent comp, String value) throws ConverterException {
		if (value==null || value.trim().length()==0) {
			return Double.valueOf(0.0);
		}
		return super.getAsObject(ctx, comp, value);
	}

}
