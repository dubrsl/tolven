/*
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
 * @author anil
 * @version $Id: HL7DateFormatUtility.java,v 1.1 2009/04/06 00:50:26 jchurin Exp $
 */

package org.tolven.trim.ex;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Purpose of this class is to provide a centralized Date formatting in HL7 Standards. 
 * 
 * @author Anil
 *
 */
public class HL7DateFormatUtility {
	
	private static final SimpleDateFormat HL7TSformat4 = new SimpleDateFormat("yyyy");
	private static final SimpleDateFormat HL7TSformatL8 = new SimpleDateFormat("yyyyMMdd");
	private static final SimpleDateFormat HL7TSformatL12 = new SimpleDateFormat("yyyyMMddHHmm");	
	private static final SimpleDateFormat HL7TSformatL14 = new SimpleDateFormat("yyyyMMddHHmmss");	
	private static final SimpleDateFormat HL7TSformatL16 = new SimpleDateFormat("yyyyMMddHHmmssZZ");

	public static SimpleDateFormat getHL7FullDateFormat(){
		return HL7TSformatL16;
	}
	
	public static Date parseDate(String value) throws ParseException 
	{
		if (value==null || value.length()==0) return null;
		
		if (value.length() == 4)
		{
			return HL7TSformat4.parse(value);
		}
		else if (value.length() == 8)
		{
			return HL7TSformatL8.parse(value);
		}
		else if (value.length() == 12)
		{
			return HL7TSformatL12.parse(value);
		}
		else if (value.length() == 14)
		{
			return HL7TSformatL14.parse(value);
		}		
		else
		{
			return HL7TSformatL16.parse(value);	
		}

	}
	
}
