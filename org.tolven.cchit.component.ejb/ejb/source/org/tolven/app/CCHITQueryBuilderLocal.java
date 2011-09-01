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
 * This file contains CCHITQueryBuilderLocal interface.
 *
 * @package org.tolven.app
 * @author Valsaraj Viswanathan <valsa.v@cyrusxp.com>
 * @copyright Tolven Inc 
 * @since File available since Release 0.0.1
 */
package org.tolven.app;

import java.util.Map;

import org.tolven.app.QueryBuilderLocal.Participation;
import org.tolven.app.entity.MenuQueryControl;

/**
 * This interface is the local interface of CCHITQueryBuilderBean which overrides the DefaultQueryBuilder.
 * 
 * @author Valsaraj Viswanathan <valsa.v@cyrusxp.com>
 * @since File available since Release 0.0.1
 */
public abstract interface CCHITQueryBuilderLocal {
	public void prepareCriteria(MenuQueryControl ctrl, StringBuffer sbFrom, StringBuffer sbWhere, StringBuffer sbOrder,Map<String, Object> params);
	public Participation getParticipation(MenuQueryControl ctrl); 
	
}