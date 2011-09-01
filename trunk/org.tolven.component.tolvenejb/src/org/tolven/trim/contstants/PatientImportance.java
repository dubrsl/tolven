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
 * @author <your name>
 * @version $Id: PatientImportance.java,v 1.1 2009/05/19 18:30:15 jchurin Exp $
 */  

package org.tolven.trim.contstants;

import org.tolven.trim.CE;
import org.tolven.trim.ex.TrimFactory;

public class PatientImportance {
	protected static final TrimFactory trimFactory = new TrimFactory();

	public static CE vip = trimFactory.createCE();
	static {
		vip.setCode("VIP");
		vip.setCodeSystem("2.16.840.1.113883.5.1075");
		vip.setCodeSystemName("HL7");
	}
	
	public static boolean isVIP( Object code ) {
		return vip.equals(code);
	}
}
