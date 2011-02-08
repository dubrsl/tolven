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
 * @version $Id: PersisterLocal.java,v 1.2 2010/03/17 20:10:23 jchurin Exp $
 */  

package org.tolven.app;

import org.tolven.app.entity.MenuData;

/**
 * Implementations of this interface determine how a MenuData item is actually persisted. 
 * Implementations have the ability to control of the persist operation and where data is persisted. Persisters are configured in the Tolven Plugin Framework.
 * @author John Churin
 */
public abstract interface PersisterLocal {
	
	public enum Participation {NONE, EXCLUSIVE, SHARED};
	public enum Operation {PERSIST, DELETE, UPDATE};
	
	/**
	 * Given the menuData item, a persister must return its desired level of participation in the operation.
	 * @param ctrl
	 * @return The level of participation that this QueryBuilder desires. Only one persiter can elect exclusive participation which means
	 * it should be used sparingly. No order is specified for shared participation. 
	 */
	public Participation getParticipation( MenuData md, Operation operation );
	
	/**
	 * 
	 * @param ctrl
	 * @param sbFrom
	 * @param sbWhere
	 * @param sbOrder
	 * @param params
	 */
	public void persist( MenuData md );
	
	/**
	 * Delete a menuData item. This may mean that only a flag is set. But the net effect to the application user
	 * is that the record is deleted.
	 * @param md
	 */
	public void delete( MenuData md );
	
	/**
	 * Update a menuData item. 
	 * @param md
	 */
	public void update( MenuData md );

}
