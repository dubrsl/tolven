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
 * @version $Id: VerhoeffTest.java,v 1.1 2009/04/10 01:47:43 jchurin Exp $
 */  

package test.org.tolven.core.checkdigit;

import org.tolven.checkdigit.Verhoeff;

import junit.framework.TestCase;

public class VerhoeffTest extends TestCase {

	public void testCheck() {
		assertTrue(Verhoeff.check(123008003));
		assertTrue(Verhoeff.check(27247007));
		assertTrue(Verhoeff.check(77496001));
		assertTrue(Verhoeff.check(22008009));
		assertTrue(Verhoeff.check(18127008));
	}

	public void testAppendCheckDigit() {
		assertTrue(123008003 == Verhoeff.appendCheckDigit(12300800));
		assertTrue(27247007 == Verhoeff.appendCheckDigit(2724700));
		assertTrue(77496001 == Verhoeff.appendCheckDigit(7749600));
		assertTrue(22008009 == Verhoeff.appendCheckDigit(2200800));
	}

}
