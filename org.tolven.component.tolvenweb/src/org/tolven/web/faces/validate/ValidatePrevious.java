package org.tolven.web.faces.validate;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.servlet.http.HttpServletRequest;

import org.tolven.core.TolvenRequest;
import org.tolven.trim.ex.HL7DateFormatUtility;
import org.tolven.trim.ex.TSEx;

/**
 * This class is the faces validator to validate the date entered.  
 * Date entered should be less than the current date, otherwise ValidatorException is thrown
 * @author Valsaraj 
 * added on 07/21/09
 */
public class ValidatePrevious implements Validator, Serializable {

	public static final long serialVersionUID = 1212121L;
	
	public void validate(FacesContext ctx, UIComponent comp, Object value) throws ValidatorException {

		try {
			if(value == null)
				return;
			Date now = TolvenRequest.getInstance().getNow();
			
			Date dateEntered = null;
			
			if (value instanceof String)
			{
				dateEntered = HL7DateFormatUtility.parseDate((String)value);
			}
			else
			{
				dateEntered = ((TSEx)value).getDate();	
			}			 
			
			GregorianCalendar curCal = new GregorianCalendar();
			curCal.setTime(now);
			GregorianCalendar cal = new GregorianCalendar();
			cal.setTime(dateEntered);
			
			if (dateEntered != null && (dateEntered.after(now) || (cal.get(GregorianCalendar.DATE) == curCal.get(GregorianCalendar.DATE) && cal.get(GregorianCalendar.MONTH) == curCal.get(GregorianCalendar.MONTH) && cal.get(GregorianCalendar.YEAR) == curCal.get(GregorianCalendar.YEAR)))) {
				throw new ValidatorException(new FacesMessage("Date should be less than the current date"));
			}			
		} 
		catch(ParseException e) {
			throw new ValidatorException(new FacesMessage("Please enter a valid date"));
		}		
	} // validate(FacesContext, UIComponent, Object)

} // class ValidatePast