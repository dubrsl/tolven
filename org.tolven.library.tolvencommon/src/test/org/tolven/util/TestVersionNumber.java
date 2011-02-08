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
 * @version $Id: TestVersionNumber.java,v 1.3 2009/07/05 18:23:44 jchurin Exp $
 */  

package test.org.tolven.util;

import org.tolven.util.VersionNumber;

import junit.framework.TestCase;

public class TestVersionNumber extends TestCase {
	
	/**
	 * Normal version numbers
	 */
	public void test1( ) {
		assertTrue( VersionNumber.compare("1.1.0", "1.1") > 0);
		assertTrue( VersionNumber.compare("1.01", "1.1") == 0);
		assertTrue( VersionNumber.compare("0.1.1", "1.1") < 0 );
		assertTrue( VersionNumber.compare("0.01", "1.1") < 0);
		assertTrue( VersionNumber.compare("0.0.2.1", "0.0.2") > 0);
	}
	
	/**
	 * Number too big
	 */
	public void test2( ) {
		try {
			assertTrue( 0 > VersionNumber.compare("9999999999.1.0", "1.9999999999"));
		} catch (NumberFormatException e) {
			return;
		}
		assertTrue( false );
	}
	
	/**
	 * Many dots, more dots wins if all else is equal
	 */
	public void test3( ) {
		assertTrue( VersionNumber.compare("1.0.0.0.0.0.0", "1.0.0.0.0.0") > 0);
	}

	public void testToString1 () {
		int[] versionArray = VersionNumber.getVersionArray( "0.1.2" );
		assertEquals( "0.1.2", VersionNumber.toVersionString(versionArray));
	}
	
	public void testToString2 () {
		int[] versionArray = VersionNumber.getVersionArray( "0.1.2.0" );
		assertEquals( "0.1.2.0", VersionNumber.toVersionString(versionArray));
		assertFalse( "0.1.2".equals( VersionNumber.toVersionString(versionArray)));
	}
}
