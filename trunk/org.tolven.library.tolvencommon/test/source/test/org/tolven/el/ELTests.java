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
 * @version $Id: ELTests.java 1790 2011-07-22 20:42:40Z joe.isaac $
 */  

package test.org.tolven.el;

import java.util.Properties;

import org.tolven.el.ExpressionEvaluator;

import junit.framework.TestCase;

public class ELTests extends TestCase {
	private static final String SIMPLE_STRING = "Hello World";
	private static final String SIMPLE_VARIABLE = "simple";
	private static final String PLUGIN_PROPERTIES = "pluginConfig";
	private static final String SYSTEM_PROPERTIES = "system";
	
	public void testSimple() {
		ExpressionEvaluator ee = new ExpressionEvaluator();
		ee.addVariable( SIMPLE_VARIABLE, SIMPLE_STRING);
		Object result = ee.evaluate("#{simple}");
		assertEquals(SIMPLE_STRING, result);
	}
	
	public void testSimpleViaMap() {
		ExpressionEvaluator ee = new ExpressionEvaluator();
		ee.put(SIMPLE_VARIABLE, SIMPLE_STRING);
		ee.addVariable( SIMPLE_VARIABLE, SIMPLE_STRING);
		assertEquals(SIMPLE_STRING, ee.get(SIMPLE_VARIABLE));
	}
	
	public void testPropertyMap() {
		ExpressionEvaluator ee = new ExpressionEvaluator();
		Properties properties = new Properties();
		// Load the properties (just one property in this case)
		properties.put(SIMPLE_VARIABLE, SIMPLE_STRING);
		// Tell EL about the properties map
		ee.addVariable( PLUGIN_PROPERTIES, properties);
		// Now look up the property
		Object result = ee.evaluate("#{pluginConfig['simple']}");
		assertEquals(SIMPLE_STRING, result);
	}
	
	public void testTwoPropertyMaps() {
		ExpressionEvaluator ee = new ExpressionEvaluator();
		Properties pluginConfig = new Properties();
		// Load the properties (just one property in this case)
		pluginConfig.put(SIMPLE_VARIABLE, SIMPLE_STRING);
		// Tell EL about the plugin properties map
		ee.addVariable( PLUGIN_PROPERTIES, pluginConfig);
		// and the system properties map
		ee.addVariable( SYSTEM_PROPERTIES, System.getProperties());
		Object userDir = System.getProperties().get("user.dir");
		// Now look up plugin and system properties to create a string
		Object result = ee.evaluate("#{pluginConfig['simple']} at #{system['user.dir']}");
		System.out.println( result );
		assertEquals(SIMPLE_STRING + " at " + userDir, result);
	}
}
