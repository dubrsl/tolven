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
 * @version $Id: MenuDataTest.java,v 1.1 2009/06/04 19:00:30 jchurin Exp $
 */  

package test.org.tolven.app;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.Map;

import org.tolven.app.entity.MenuData;

import junit.framework.TestCase;

public class MenuDataTest extends TestCase {

	public void testSetInternalField( ) {
		MenuData md = new MenuData();
		md.setInternalField("string01", "hello1");
		Object result = md.getInternalField("string01");
		assertEquals( "hello1", result);
	}

	public void testSetField( ) {
		MenuData md = new MenuData();
		md.setField("string01", "hello2");
		Object result = md.getInternalField("string01");
		assertEquals( "hello2", result);
	}

	public void testSetField2( ) {
		MenuData md = new MenuData();
		md.setField("string01", "hello3");
		Object result = md.getField("string01");
		assertEquals( "hello3", result);
	}
	
	public void testSetDateField( ) {
		MenuData md = new MenuData();
		Date now = new Date();
		md.setField("date01", now);
		Object result = md.getField("date01");
		assertEquals( now, result);
	}
	
	public void testSetNullStringDateField( ) {
		MenuData md = new MenuData();
		md.setField("date01", "");
		Object result = md.getField("date01");
		assertNull( result);
	}

	public void testGetInternalField( ) {
		MenuData md = new MenuData();
		md.setString01("hello");
		Object result = md.getInternalField("string01");
		assertEquals( "hello", result);
	}
	
	public void testListGetMethods( ) {
		for (Map.Entry<String, Method> entry : MenuData.internalGetters.entrySet()) {
			System.out.println("get: " + entry.getKey());
		}
	}
	
	public void testListSetMethods( ) {
		for (Map.Entry<String, Method> entry : MenuData.internalSetters.entrySet()) {
			System.out.println("set: " + entry.getKey());
		}
	}
}
