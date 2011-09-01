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
 * @version $Id: TrimHandler.java,v 1.2 2010/03/17 20:10:23 jchurin Exp $
 */  

package org.tolven.app;

import org.tolven.app.el.TrimExpressionEvaluator;
import org.tolven.trim.ex.TrimEx;

/**
 * Implementations of this interface can be used to specify how trims are handled. A TrimHandler can be configured
 * in Tolven Plugin Framework tolven-config/plugins.xml file.
 * @author John Churin
 *
 */
public interface TrimHandler {

	public void load( TrimEx trim );
	public void instantiate( TrimEx trim, TrimExpressionEvaluator ee);
	public void upload( TrimEx trim, TrimExpressionEvaluator ee );
	public void submit( TrimEx trim, TrimExpressionEvaluator ee );
	
}
