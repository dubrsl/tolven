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
 * @version $Id: ModelObject.java,v 1.1 2009/05/24 22:39:32 jchurin Exp $
 */  

package org.tolven.model;

import java.util.Date;

import org.tolven.app.entity.MenuData;

public abstract class ModelObject {
	private Date now;
	private MenuData placeholder;
	
	public ModelObject(MenuData placeholder, Date now ) {
		this.placeholder = placeholder;
		this.now = now;
	}

	public Date getNow() {
		return now;
	}

	public void setNow(Date now) {
		this.now = now;
	}

	public MenuData getPlaceholder() {
		return placeholder;
	}
	
}
