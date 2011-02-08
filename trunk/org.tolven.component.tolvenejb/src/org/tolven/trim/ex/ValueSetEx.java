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
 * @version $Id: ValueSetEx.java,v 1.1 2010/01/20 19:54:25 jchurin Exp $
 */  

package org.tolven.trim.ex;

import java.util.List;

import org.tolven.trim.ValueSet;

public class ValueSetEx extends ValueSet {
	public List<Object> getValues() {
		return this.getBindsAndADSAndCDS();
	}
}
