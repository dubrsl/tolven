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
 * @version $Id: LoggerTest2.java,v 1.1 2009/04/12 08:52:26 jchurin Exp $
 */  

package test.org.tolven.logging;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.tolven.logging.TolvenLogger;

public class LoggerTest2 extends TestCase {
	Logger logger = Logger.getLogger(this.getClass());
	public void testWithDefaultInit() {
		TolvenLogger.defaultInitialize();
        TolvenLogger.info("We are being called from testWithDefaultInit", LoggerTest2.class);
	}
	
}
