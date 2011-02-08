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
 * @version $Id: TELTests3.java,v 1.2 2009/03/17 11:45:35 jchurin Exp $
 */  

package test.org.tolven.trim;

import org.tolven.trim.ex.TELEx;
import org.tolven.trim.ex.TrimFactory;

import junit.framework.TestCase;

public class TELTests3 extends TestCase {
	private static final String PHONE_NUMBER_SCHEME = "tel";
	private static final String PHONE_NUMBER_ADDRESS = "1-202-555-1212";
	private static final String PHONE_NUMBER = PHONE_NUMBER_SCHEME + ":" + PHONE_NUMBER_ADDRESS;
	private static final String EMAIL_SCHEME = "mailto";
	private static final String EMAIL_ADDRESS = "first.last@mydomain.com";
	private static final String EMAIL = EMAIL_SCHEME + ":" + EMAIL_ADDRESS;
	private static final TrimFactory factory = new TrimFactory( );

	public void testPhoneNumber1() {
		TELEx tel = (TELEx) factory.createTEL();
		tel.setValue(PHONE_NUMBER);
		assertEquals( PHONE_NUMBER_SCHEME, tel.getScheme());
		assertEquals( PHONE_NUMBER_ADDRESS, tel.getAddress());
	}
	
	public void testPhoneNumber2() {
		TELEx tel = (TELEx) factory.createTEL();
		tel.setValue(PHONE_NUMBER_SCHEME + ":");
		String scheme = tel.getScheme();
		assertEquals( PHONE_NUMBER_SCHEME, scheme);
		assertNull( tel.getAddress());
	}

	public void testPhoneNumber3() {
		TELEx tel = (TELEx) factory.createTEL();
		tel.setValue(PHONE_NUMBER_ADDRESS);
		assertNull( tel.getScheme());
		assertEquals( PHONE_NUMBER_ADDRESS, tel.getAddress());
	}
	
	public void testEMailAddress() {
		TELEx tel = (TELEx) factory.createTEL();
		tel.setAddress(EMAIL_ADDRESS);
		assertNull( tel.getScheme());
		assertEquals( EMAIL_ADDRESS, tel.getAddress());
	}
	
	public void testEMailScheme() {
		TELEx tel = (TELEx) factory.createTEL();
		tel.setScheme(EMAIL_SCHEME);
		assertEquals( EMAIL_SCHEME, tel.getScheme());
		assertNull( tel.getAddress());
	}
	
	public void testEMailSchemeAndAddress() {
		TELEx tel = (TELEx) factory.createTEL();
		tel.setScheme(EMAIL_SCHEME);
		tel.setAddress(EMAIL_ADDRESS);
		assertEquals( EMAIL_SCHEME, tel.getScheme());
		assertEquals( EMAIL_ADDRESS, tel.getAddress());
	}
	
}
