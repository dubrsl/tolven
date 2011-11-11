package org.tolven.web.faces.convert;

import java.text.ParseException;
import java.util.Date;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.tolven.trim.ex.HL7DateFormatUtility;
/**
 * This class converts date to MM:dd:yyyy:HHmm datatypes for use in a Faces form.
 * @author Srini Kandula
 *
 */
public class DateFormatL12Converter implements Converter {
	
	public Object getAsObject(FacesContext ctx, UIComponent comp, String string) {
		try {
			return HL7DateFormatUtility.parseDate(string);
		} catch (ParseException e) {
			throw new RuntimeException("Error parsing date "+string,e);
		}
	}
	

	public String getAsString(FacesContext ctx, UIComponent comp, Object object) {
		return HL7DateFormatUtility.formatTSFormatL12((Date)object);
	}

}
