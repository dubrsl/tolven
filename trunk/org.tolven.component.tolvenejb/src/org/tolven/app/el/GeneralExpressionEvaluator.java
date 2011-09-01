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
 * @version $Id: GeneralExpressionEvaluator.java,v 1.2 2009/10/27 08:37:41 jchurin Exp $
 */  

package org.tolven.app.el;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.tolven.el.ExpressionEvaluator;


/**
 * GeneralPurpose Expression Evaluator used in EJB layer. Either this class or a specialized subclass should be used rather than
 * ExpressionEvaluator class directly.
 * @author John Churin
 */
public class GeneralExpressionEvaluator extends ExpressionEvaluator  {
	public static final String EL_FUNCTION_CLASS_NAME = "ELFunction";
	private static Set<String> elFunctionNames =  loadELFuntions();
	
	public static final String NOW = "now";
	public static final String ACCOUNT = "account";
	public static final String ACCOUNT_USER = "accountUser";
	public static final String USER = "user";

	
    private static Set<String> loadELFuntions() {
    	Set<String> elFunctionClasses = new HashSet<String>();
    	try {
				Properties properties = new Properties();
				String propertyFileName = GeneralExpressionEvaluator.class.getSimpleName()+".properties"; 
				InputStream is = GeneralExpressionEvaluator.class.getResourceAsStream(propertyFileName);
				String elFunctionClassNames = null;
				if (is!=null) {
					properties.load(is);
					elFunctionClassNames = properties.getProperty(EL_FUNCTION_CLASS_NAME);
					is.close();
				}
				if (elFunctionClassNames!=null && elFunctionClassNames.length()>0) {
					String names[] = elFunctionClassNames.split(",");
					// Ignore duplicates, normalize the name - include a prefix, even if null
					for (String name : names) {
						int colon = name.indexOf(':');
						if (colon < 0) {
							elFunctionNames.add(name);
						} else {
				    		String prefix;
				    		String className;
				    		prefix = name.substring(0, colon);
				    		className = name.substring(colon+1);
				    		elFunctionClasses.add(prefix + ":" + className);
						}
					}
				}
				return elFunctionClasses;
		} catch (Exception e) {
			throw new RuntimeException( "Error loading EL functions", e);
		}
    }
    
    @Override
	public void addFunctions( String prefix, Class<?> type) {
		elFunctionNames.add(prefix + ":" + type.getName());
	}

    @Override
	public void addFunctions( String prefix, String className) throws ClassNotFoundException {
		elFunctionNames.add(prefix + ":" + className);
	}
   
    @Override
    protected void initializeELFunctions() {
		try {
			for (String functionName : elFunctionNames) {
				String functionNameParts[] = functionName.split("\\:");
//				Class<?> type = this.getClass().getClassLoader().loadClass(functionNameParts[1]);
				Class<?> type = Class.forName(functionNameParts[1]);
				for (Method method : type.getDeclaredMethods()) {
					if (Modifier.isStatic(method.getModifiers()) && Modifier.isPublic(method.getModifiers())) {
						addFunction(functionNameParts[0], method.getName(), method);
					}
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("Error loading EL functions", e);
		}
    }
    
}
