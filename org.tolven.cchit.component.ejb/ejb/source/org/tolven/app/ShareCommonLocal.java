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
 * This file contains ShareCommonLocal interface.
 *
 * @package org.tolven.app
 * @author Valsaraj Viswanathan <valsa.v@cyrusxp.com>
 * @copyright Tolven Inc 
 * @since File available since Release 0.0.1
 */
package org.tolven.app;

import org.tolven.doc.bean.TolvenMessage;
import org.tolven.trim.Trim;

/**
 * This interface contains common tasks used in sharing process.
 * 
 * @author Valsaraj Viswanathan <valsa.v@cyrusxp.com>
 * @since File available since Release 0.0.1
 */
public interface ShareCommonLocal {
	/**
	 * Sends a trim payload to destination account.
	 * 
	 * added on 06/14/2010
	 * @author Valsaraj Viswanathan <valsa.v@cyrusxp.com>
	 * @param trim - trim object
	 * @param app - AppEvalAdaptor instance
	 * @param tm - TolvenMessage instance
	 * @param destinationAccountId - destination account id
	 */
	public void sendMessage(Trim trim, AppEvalAdaptor app, TolvenMessage tm, Long destinationAccountId);
}