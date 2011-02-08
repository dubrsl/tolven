package org.tolven.web.rim;

import java.text.ParseException;
import java.util.Date;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import org.tolven.trim.ex.TSEx;
import org.tolven.trim.ex.TrimFactory;

public class TSConverter extends javax.faces.convert.DateTimeConverter implements Converter {
	private static final TrimFactory factory = new TrimFactory( );

	/** 
	 * Given a String containing a date string, return an HL7 TS datatype string
	 * (non-Javadoc)
	 * @see javax.faces.convert.Converter#getAsObject(javax.faces.context.FacesContext, javax.faces.component.UIComponent, java.lang.String)
	 */
	public Object getAsObject(FacesContext ctx, UIComponent comp, String value) throws ConverterException {
		if (this.getTimeStyle()!=null && this.getTimeStyle().length()>0)  {
			setType("both");
		} else {
			setType("date");
		}
//		TolvenLogger.info( "[TSConverter::getAsObject] Type: " + getType(), TSConverter.class);
		Date date = (Date) super.getAsObject(ctx,comp, value);
		return factory.createNewTS(date);
	}

	/**
	 * Given an HL7 TS datatype string (yyymmddhhmmss...) return a string containing date suitable for browsers 
	 */
	public String getAsString(FacesContext ctx, UIComponent comp, Object value) throws ConverterException {
		try {
			if (this.getTimeStyle()!=null && this.getTimeStyle().length()>0)  {
				setType("both");
			} else {
				setType("date");
			}
//			TolvenLogger.info( "[TSConverter::getAsString] Type: " + getType(), TSConverter.class);
//			TolvenLogger.info( "[TSConverter::getAsString] Locale: " + getLocale(), TSConverter.class);
			Date date = ((TSEx) value).getDate();
			if (date==null) return "";
			else return super.getAsString(ctx, comp, date);
		} catch (ParseException e) {
//			TolvenLogger.info( "[getAsString] Exception: " + e.getMessage(), TSConverter.class);
			throw new ConverterException( "Nested Exception", e);
		}
	}

}
