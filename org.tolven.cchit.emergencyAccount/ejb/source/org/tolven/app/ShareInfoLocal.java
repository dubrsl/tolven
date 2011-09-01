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
 * This file contains ShareInfoLocal interface.
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
 * This interface is used to to handle sharing.
 * 
 * @author Valsaraj Viswanathan <valsa.v@cyrusxp.com>
 * @since File available since Release 0.0.1
 */
public interface ShareInfoLocal extends CreatorLocal {
	/**
	 * Shares encounter summary in progress note with PHR account.
	 * 
	 * added on 01/31/2011
	 * @author Valsaraj
	 * @param trim - trim
	 * @param app - AppEvalAdaptor object
	 * @param tm - TolvenMessage object
	 */
	public  void startSharing(Trim trim , AppEvalAdaptor app, TolvenMessage tm) throws Exception;
}
