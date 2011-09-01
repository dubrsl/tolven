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
 * @version $Id$
 */
package org.tolven.app;

import java.io.IOException;
import java.io.Writer;

/**
 * Allows column definitions to be obtained for a given sql dialect (or database)
 * 
 * @author Joseph Isaac
 *
 */
public interface SQLDialectHandler {

    public String getDialect();

    public String getColumnType(String internalId);
    /**
     * Format a value for this dialect. For example, a string is normally surrounded by single quotes.
     * @param value
     */
    public void formatValue( StringBuffer sb, Object value);

    /**
     * Identifiers specific to the underlying dialect must be encoded. For example, 
     * patient.id would be converted to patient_id in SQL.
     * @param Stream writer to receive output
     * @param sb String containing Encoded identifier
     */
	public String  encodeIdentifier(String identifier);
    
}
