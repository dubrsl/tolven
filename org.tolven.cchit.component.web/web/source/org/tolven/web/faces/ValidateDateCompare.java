package org.tolven.web.faces;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.tolven.trim.ex.HL7DateFormatUtility;
import org.tolven.trim.ex.TSEx;

/**
 * This class is the faces validator to compare the date entered with another date.  
 * Comparison is based on the operator selected
 * Here operator:
 * gt for Greater Than
 * lt for Less Than
 * eq for EqualTo
 * gq for Greater and Equal To
 * lq for Less and Equal To
 * nt for Not Equal To
 * @author Valsaraj
 * added on 11/16/2009
 */
public class ValidateDateCompare implements Validator, Serializable {
	public static final long serialVersionUID = 1212122L;	
	private Date compareWith;
	private String operator = "";
	private String date1Name = "Start Date";
	private String date2Name = "End Date";
		
	public void validate(FacesContext ctx, UIComponent comp, Object value) throws ValidatorException {
		try {
			Date dateEntered = null;
			
			if(value == null)
				return;
			
			if (value instanceof String) {
				dateEntered = HL7DateFormatUtility.parseDate((String)value);
			}
			else {
				dateEntered = ((TSEx)value).getDate();	
			}
						
			if (dateEntered != null) {
				initProps(comp);
				
				if (compareWith != null) {
					String errorMessage = "";
					int result = dateEntered.compareTo(compareWith);
					boolean isValid = true;
				
					if (result < 0 && operator.equalsIgnoreCase("ge")) {
						errorMessage = "The " + date2Name + " must be greater than or equal to " + date1Name;
						isValid = false;
					} 
					else if (result > 0 && operator.equalsIgnoreCase("le")) {
						errorMessage = "The " + date2Name + " must be less than or equal to " + date1Name;
						isValid = false;
					} 
					else if (result != 0 && operator.equalsIgnoreCase("eq")) {
						errorMessage = "The " + date2Name + " must be equal to " + date1Name;
						isValid = false;
					} 
					else if (result <= 0 && operator.equalsIgnoreCase("gt")) {
						errorMessage = "The " + date2Name + " must be greater than " + date1Name;
						isValid = false;
	
					} 
					else if (result >= 0 && operator.equalsIgnoreCase("lt")) {
						errorMessage = "The " + date2Name + " must be less than " + date1Name;
						isValid = false;
					}
					else if (result == 0 && operator.equalsIgnoreCase("nt")) {
						errorMessage ="The " +  date2Name + " must not be equal to " + date1Name;
						isValid = false;
					}

					if (! isValid) {
						throw new ValidatorException(new FacesMessage(errorMessage));
					}
				}
				else {
					System.out.println("========== START DATE NULL =========");
				}
			}
		} 
		catch(ParseException e) {
			throw new ValidatorException(new FacesMessage("Please enter a valid date"));
		}		
	}
	
	/**
	 * This method will set the value of both operator and date to compare.
	 */
	private void initProps(UIComponent component) {
		Object compareVal = component.getAttributes().get("compareWith");	
		operator = (String) component.getAttributes().get("operator");
		date1Name = (String) component.getAttributes().get("date1Name");
		date2Name = (String) component.getAttributes().get("date2Name");
		
		if (operator == null) {
			operator = "ge";
		}

		if (compareVal != null) {
			try {
				if (compareVal instanceof String) {
					compareWith = HL7DateFormatUtility.parseDate((String)compareVal);
				}
				else {
					compareWith = ((TSEx)compareVal).getDate();	
				}
			} 
			catch (Exception e) {		
				System.out.println("Error in parsing the Date : " + e);
			}
		}
	}
}
