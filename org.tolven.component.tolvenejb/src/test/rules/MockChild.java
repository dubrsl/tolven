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
 * @version $Id: MockChild.java,v 1.1 2009/11/14 05:55:41 jchurin Exp $
 */  

package test.rules;

public class MockChild {
	public MockChild() {
		
	}
	private String field2;
	private MockParent parent;
	
	public String getField2() {
		return field2;
	}
	public void setField2(String field2) {
		this.field2 = field2;
	}
	public MockParent getParent() {
		return parent;
	}
	public void setParent(MockParent parent) {
		this.parent = parent;
	}
	
}
