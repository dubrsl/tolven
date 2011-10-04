/*
 *  Copyright (C) 2006 Tolven Inc 
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of 
 * the GNU Lesser General Public License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU Lesser General Public License for more details.
 * 
 * Contact: info@tolvenhealth.com
 */
/**
 * This file contains AutomationLocal interface.
 *
 * @package org.tolven.app
 * @author Valsaraj Viswanathan <valsa.v@cyrusxp.com>
 * @copyright Tolven Inc 
 * @since File available since Release 0.0.1
 */
package org.tolven.app;

import java.security.PublicKey;
import org.tolven.trim.Trim;

/**
 * This interface is used to create a tolven user for patient and 
 * to create an account and patient inside that account.
 * 
 * @author Valsaraj Viswanathan <valsa.v@cyrusxp.com>
 * @since File available since Release 0.0.1
 */
public interface AutomationRemote {
	/**
	 * Creates PHR account for patient automatically.
	 * 
	 * @author valsaraj
	 * added on 06/14/2010
	 * @param trim - patient trim oject
	 * @param app - AppEvalAdaptor instance
	 */
	public void automateCreatePHR(Trim trim, AppEvalAdaptor app, PublicKey userPublicKey);
	
	/**
	 * Creates new PHR account and tolven account user for patient automatically.
	 * 
	 * added on 06/14/2010
	 * @author valsaraj
	 * @param trim - patient trim oject
	 * @param app - AppEvalAdaptor instance
	 */
	public void createNewAccount(Trim trim, AppEvalAdaptor app, PublicKey userPublicKey);
}