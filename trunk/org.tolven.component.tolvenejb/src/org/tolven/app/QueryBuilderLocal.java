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
 * @version $Id: QueryBuilderLocal.java,v 1.1 2009/06/23 11:52:34 jchurin Exp $
 */  

package org.tolven.app;

import java.util.Map;

import org.tolven.app.entity.MenuQueryControl;

/**
 * Implementations of this interface determine how a query is actually constructed. Implementations of this interface
 * have low-level control of the query string with the potential for getting the query wrong. 
 * @author John Churin
 */
public abstract interface QueryBuilderLocal {
	
	public enum Participation {NONE, FILTER, EXCLUSIVE};
	
	/**
	 * Given the menu query control, a query Builder should return its desired level of participation in the query.
	 * @param ctrl
	 * @return The level of participation that this QueryBuilder desires.
	 */
	public Participation getParticipation( MenuQueryControl ctrl);
	
	/**
	 * Given the menu query control, populate from, where, and order by clauses and also populate the parameters map.  
	 * An implementation of this interface should decide if it has anything to do with this query or not.
	 * @param ctrl
	 * @param sbFrom
	 * @param sbWhere
	 * @param sbOrder
	 * @param params
	 */
	public void prepareCriteria( MenuQueryControl ctrl, StringBuffer sbFrom, StringBuffer sbWhere, StringBuffer sbOrder, Map<String, Object> params);

}
