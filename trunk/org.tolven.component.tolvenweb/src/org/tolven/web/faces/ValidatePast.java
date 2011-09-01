package org.tolven.web.faces;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.servlet.http.HttpServletRequest;

import org.tolven.trim.ex.HL7DateFormatUtility;
import org.tolven.trim.ex.TSEx;

/**
 * 
 * @author Sashikanth Vema
 * This class is the faces validator to validate the date entered.  
 * Date entered should be a past date, otherwise ValidatorException is thrown 
 */
public class ValidatePast implements Validator, Serializable {

	public static final long serialVersionUID = 1212121L;
	
	/*
	 * (non-Javadoc)
	 * @see javax.faces.validator.Validator#validate(javax.faces.context.FacesContext, javax.faces.component.UIComponent, java.lang.Object)
	 * 
	 * Note: Enhancements could be: 
	 * 		Customer error message (Ex. Date of Birth should be a past date)
	 * 		Retrieve the date and time formats and apply to the server now 
	 * 			ie., determine whether to remove the time from server now or not
	 * 
	 */
	public void validate(FacesContext ctx, UIComponent comp, Object value) throws ValidatorException {
		if(value == null)
			return;
		try {
			Date now = (Date) ((HttpServletRequest)ctx.getExternalContext().getRequest()).getAttribute("tolvenNow");
			
			Date dateEntered = null;
			if (value instanceof String){
				dateEntered = HL7DateFormatUtility.parseDate((String)value);
			}else{
				dateEntered = ((TSEx)value).getDate();	
			}			
			if(dateEntered!=null && dateEntered.after(now))
				throw new ValidatorException(new FacesMessage("Date should be less than or equal to current date"));

		} catch(ParseException e) {
			throw new ValidatorException(new FacesMessage("Please enter a valid date"));

		} // try - catch
		
	} // validate(FacesContext, UIComponent, Object)

} // class ValidatePast