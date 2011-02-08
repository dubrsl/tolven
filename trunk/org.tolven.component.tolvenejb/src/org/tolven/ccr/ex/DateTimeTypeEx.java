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
 * @version $Id: DateTimeTypeEx.java,v 1.3 2009/06/29 03:51:30 jchurin Exp $
 */

package org.tolven.ccr.ex;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.lang.time.DateFormatUtils;
import org.tolven.ccr.CodedDescriptionType;
import org.tolven.ccr.DateTimeType;

public class DateTimeTypeEx extends DateTimeType {

	
    public Date getDateValue( ) throws ParseException { 
    	return getDateTimeValue();
    }
    
    public Date getDateTimeValue( ) throws ParseException { 
    	try {
			if (this.exactDateTime==null) return null;
			SimpleDateFormat iso8601;
			if (exactDateTime.length()>=19) {
				iso8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"); 
			} else {
				iso8601 = new SimpleDateFormat("yyyy-MM-dd");
			}
			return iso8601.parse( exactDateTime );
		} catch (Exception e) {
			throw new RuntimeException( "Error parsing iso8601 date: " + exactDateTime, e);
		}
    }
	
	public String formatISO8601(String dt) {
		int pos = dt.lastIndexOf(":");
		return dt.substring(0, pos) + dt.substring(pos+1, pos+3);
	}
	
    public void setDateValue(Date date) {
    	this.exactDateTime = DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.format(date);
    }
    
    public String getApproximateDate() {
		CodedDescriptionType approximateDateTime = getApproximateDateTime();
		if (approximateDateTime == null) return null;			
		return approximateDateTime.getText();    				
    }

}
