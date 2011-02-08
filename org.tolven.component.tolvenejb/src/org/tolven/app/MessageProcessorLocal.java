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
 * @version $Id: MessageProcessorLocal.java,v 1.2 2010/03/17 20:10:22 jchurin Exp $
 */  

package org.tolven.app;

import java.util.Date;

/**
 * A instance of this interface processes a JMS message, such as the rule evaluator.
 * @author John Churin
 */
public interface MessageProcessorLocal {

	/**
	 * Beans must implement this interface to actually process a message.
	 * @param message
	 * @param now - the date to use for transactionally consistent timestamp
	 */
	public abstract void process( Object message, Date now );
	
}
