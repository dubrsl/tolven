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
 * @author Joseph Isaac
 * @version $Id: AbstractSQLDialectHandler.java,v 1.1 2009/08/26 07:22:43 jchurin Exp $
 */
package org.tolven.app;

import java.text.SimpleDateFormat;
import java.util.Date;


public abstract class AbstractSQLDialectHandler implements SQLDialectHandler {

	@Override
	public void formatValue(StringBuffer sb, Object value) {
        if (value == null) {
            sb.append("NULL");
        } else if (value instanceof Long){
            sb.append(value.toString());
        } else if (value instanceof Date) {
        	SimpleDateFormat iso8601 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sb.append("'");
            sb.append(iso8601.format((Date)value));
            sb.append("'");
        } else {
            sb.append("'");
            sb.append(value);
            sb.append("'");
        }
	}
	
    /**
     * Encode SQL identifiers For example, 
     * patient.id would be converted to patient_id in SQL.
     * @param identifier
     * @param sb String containing Encoded identifier
     */
	@Override
	public String  encodeIdentifier(String identifier) {
		StringBuffer sb = new StringBuffer( identifier.length());
		for (char c : identifier.toCharArray()) {
			if (Character.isDigit(c) || Character.isLetter(c)) {
				sb.append(c);
			}
			if (c=='.' || c=='-') {
				sb.append('_');
			}
			// Skip everything else
		}
		return sb.toString();
	}


}
