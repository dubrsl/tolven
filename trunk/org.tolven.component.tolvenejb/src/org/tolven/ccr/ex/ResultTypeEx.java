/**
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
 * @author Kul Bhushan
 * @version $Id: ResultTypeEx.java,v 1.2 2009/05/19 18:29:20 jchurin Exp $
 */

package org.tolven.ccr.ex;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.tolven.ccr.CodedDescriptionType;
import org.tolven.ccr.DateTimeType;
import org.tolven.ccr.ResultType;

public class ResultTypeEx extends ResultType {

	public final static String COLLECTION_DATE = "Collection Date";
	
	public final static String HEMATOLOGY_TYPE = "Hematology";
	public final static String CHEMISTRY_TYPE = "Chemistry";
	public final static String SEROLOGY_TYPE = "Serology";
	public final static String VIROLOGY_TYPE = "Virology";
	public final static String TOXICOLOGY_TYPE = "Toxicology";
	public final static String MICROBIOLOGY_TYPE = "Microbology";
	public final static String PATHOLOGY_TYPE = "Pathology";
	public final static String IMAGING_X_RAY_TYPE = "Imaging X-ray";
	public final static String ULTRASOUND_TYPE = "Ultrasound";
	public final static String CT_TYPE = "CT";
	public final static String MRI_TYPE = "MRI";
	public final static String ANGIOGRAPHY_TYPE = "Angiography";
	public final static String CARDIAC_ECHO_TYPE = "Cardiac Echo";
	public final static String NUCLEAR_MEDICINE_TYPE = "Nuclear Medicine";
	public final static String PROCEDURE_TYPE = "Procedure";
	public final static String ASSESSMENT_TIME = "Assessment Time";
	

    /**
     * Helper to set the object type
     */
    public void setTypeText( String type ) {
   		org.tolven.ccr.CodedDescriptionType tt = CCRFactory.getInstance().createCodedDescriptionType();
   		tt.setText(type);
   		setType(tt);

    }
    /**
     * Helper to get the Object type
     */
    public String getTypeText() {
   		org.tolven.ccr.CodedDescriptionType tt = getType();
   		if (tt==null) return null;
   		return tt.getText();
    }
	
	/**
     * This helper function returns only the DataTypeType with a specific type
     * @return return the first or only DateType matching the specified type or null if not found
     */
	public DateTimeTypeEx getDateTimeType(String type) {		
    	for (DateTimeType dtt : getDateTime()) {
    		if ( dtt.getType() == null) {    			
    			return null;
    		} else {
    			if (type.equals( dtt.getType().getText())) {    				
    				return (DateTimeTypeEx) dtt;
    			}     			
    		}
    	}
    	return null;
    }	

    /**
     * This helper function returns the Date of two flavors of ExactDateTime tag
     * @return Date
     */
    public Date getExactDateTime() {
    	String exactDateStr = null;
    	Date myDate = null;
    	for (DateTimeTypeEx dtt : getDateTimeType()) {
    		if ( dtt.getType() == null) {
    			try {    				
    				exactDateStr = dtt.getExactDateTime();
    				if (exactDateStr==null) return null;
    				//CCR ExactDateTime format: <ExactDateTime>2009-01-27T18:26:31-05:00</ExactDateTime>
    				if ( (exactDateStr.contains("T")) && (exactDateStr.lastIndexOf(":")==22) ) {
    					if (dtt.getExactDateTime() != null) { 
    						myDate = dtt.getDateTimeValue();    					
    					}
    				}
    				else {
    					//CCR ExactDateTime format: <ExactDateTime>1935-04-07</ExactDateTime>
    					myDate = dtt.getDateValue();
    				}

    			} catch (ParseException e) {
    				return null;
    			}
    			return myDate;
    		}
    	}
    	return null;
    }   
    /**
     * This helper function returns the Date of two flavors of ApproximateDateTime tag
     * @return Date
     */
    public String getApproximateDateTime() {
    	CodedDescriptionType approximateDateTime;
    	String approximateDateStr = null;
    	for (DateTimeTypeEx dtt : getDateTimeType()) {
    		if ( dtt.getType() == null) {
    			approximateDateTime = dtt.getApproximateDateTime();
    			if (approximateDateTime != null) {
    				approximateDateStr = approximateDateTime.getText();
    			}
    		}
    	}
    	return approximateDateStr;
    }     
       
	public List<DateTimeTypeEx> getDateTimeType() {	
		List<DateTimeTypeEx> dateTimeTypeEx = new ArrayList<DateTimeTypeEx>();
		for (DateTimeType pte : getDateTime()) {		
			dateTimeTypeEx.add( (DateTimeTypeEx) pte);
		}		
		return dateTimeTypeEx;
	}
    
    /**
     * Helper to set the status text
     * @param status
     */
    public void setStatusText( String status ) {
   		org.tolven.ccr.CodedDescriptionType statusField = CCRFactory.getInstance().createCodedDescriptionType();
   		statusField.setText(status);
   		setStatus(statusField);
    }

    /**
     * Helper to get the status text
     * 
     */
    public String getStatusText( ) {
   		org.tolven.ccr.CodedDescriptionType status = getStatus();
   		if (status==null) return null;
   		return status.getText();
    }   
    
    /**
     * Helper method to get Description text
     */
    public String getDescriptionText( ) {
   		org.tolven.ccr.CodedDescriptionType description = getDescription();
   		if (description==null) return null;
   		return description.getText();
    }
    
    /**
     * Helper method to set description text
     */
    public void setDescriptionText( String description ) {
   		org.tolven.ccr.CodedDescriptionType descriptionField = CCRFactory.getInstance().createCodedDescriptionType();
   		descriptionField.setText(description);
   		setDescription(descriptionField);
    	
    }    
    
    /**
     * Helper method to get DateType text
     */
    public String getDateTypeText( ) {

    	String dateType = "";
    	for (DateTimeType dtt : getDateTime()) {
    		if ( dtt.getType() == null) {    			
    			return null;
    		} else {
    			dateType = dtt.getType().getText();
    		}
    	}
    	return dateType;
    }
    
    /**
     * Set the specified type of date time value.
     * Duplicates (previos dates of this type) are eliminated.
     */
    public void setDateTimeType( String type, Date date) {
    	while (removeDateTimeType( type )!=null);	// Remove existing dates of that type.
   		DateTimeTypeEx dtt = CCRFactory.getInstance().createDateTimeType();
   		dtt.setDateValue(date);
   		CodedDescriptionType dateTimeTypeType = CCRFactory.getInstance().createCodedDescriptionType();
   		dateTimeTypeType.setText(type);
   		dtt.setType(dateTimeTypeType);
   		getDateTime().add(dtt);
    }
    /**
     * Remove the first instance of the specified DateTime or null if it doesn't exist.
     * @param type
     * @return the item removed
     */
    public DateTimeType removeDateTimeType( String type) {
    	int x = 0;
    	for (DateTimeType dtt : getDateTime()) {
    		if (type.equals( dtt.getType().getText())) 
    		{
    			getDateTime().remove(x);
    			return dtt;
    		}
    	}
    	return null;
    	
    }    	
	
}
