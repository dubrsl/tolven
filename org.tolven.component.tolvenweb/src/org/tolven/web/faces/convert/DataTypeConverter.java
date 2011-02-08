package org.tolven.web.faces.convert;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.tolven.trim.DataType;
import org.tolven.trim.ex.TrimFactory;
/**
 * This class converts HL7 datatypes for use in a Faces form.
 * @author John Churin
 *
 */
public class DataTypeConverter implements Converter {
	private static final TrimFactory trimFactory = new TrimFactory();  
	
	public Object getAsObject(FacesContext ctx, UIComponent comp, String string) {
		return trimFactory.stringToDataType(string);
	}
	

	public String getAsString(FacesContext ctx, UIComponent comp, Object object) {
		return trimFactory.dataTypeToString( (DataType) object );
	}

}
