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
 * @version $Id: ProcessLabResultsLocal.java 1957 2011-07-31 22:14:27Z kanag.kuttiannan $
 */  

package org.tolven.mirth.api;

import java.security.PrivateKey;
import java.util.Date;

import org.tolven.app.MessageProcessorLocal;
import org.tolven.core.entity.AccountUser;

import ca.uhn.hl7v2.model.v25.message.ORU_R01;

public interface ProcessLabResultsLocal {
	public void saveLabResults(ORU_R01 obj, AccountUser accountUser, Date now,PrivateKey userPrivateKey); 
}
