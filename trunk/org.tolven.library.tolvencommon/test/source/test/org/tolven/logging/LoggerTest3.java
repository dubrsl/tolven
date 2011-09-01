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
 * @version $Id: LoggerTest3.java 1790 2011-07-22 20:42:40Z joe.isaac $
 */  

package test.org.tolven.logging;

import static org.apache.log4j.Level.DEBUG;

import org.apache.log4j.Logger;
import org.tolven.logging.TolvenLogger;

import junit.framework.TestCase;

public class LoggerTest3 extends TestCase {
	Logger logger = Logger.getLogger(this.getClass());
	
	@Override
	protected void setUp() throws Exception {
		TolvenLogger.initialize();
		super.setUp();
	}
	public void testWithInit() {
		logger.info("Info message being called from testWithInit");
		logger.warn("Warn message being called from testWithInit");
		logger.error("Error message being called from testWithInit");
	}
	
	public void testLevels() {
		logger.debug("Debug message being called from testWithInit - should not display");
		logger.setLevel( DEBUG );
		logger.debug("Debug message being called from testWithInit - should display");
	}
}
