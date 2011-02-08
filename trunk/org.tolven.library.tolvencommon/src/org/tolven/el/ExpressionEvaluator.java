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
 * @version $Id: ExpressionEvaluator.java,v 1.5.8.1 2010/10/18 08:26:43 joseph_isaac Exp $
 */  

package org.tolven.el;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.el.ArrayELResolver;
import javax.el.BeanELResolver;
import javax.el.CompositeELResolver;
import javax.el.ExpressionFactory;
import javax.el.ListELResolver;
import javax.el.MapELResolver;
import javax.el.ResourceBundleELResolver;
import javax.el.ValueExpression;


/**
 * Expression Language access to application structure.
 * This object can also be used like a map to evaluate expressions though this may not be a good idea since EL cannot actually be nested in any realistic way.
 * Variables in this evaluator can be stacked for scope-limits. For example, certain variables can persist through an entire transaction
 * such as accountUser and now which other variable such as _placehoder change per iteration in a query result.
 * @author John Churin
 */
public class ExpressionEvaluator implements Map<String, Object> {
	private boolean elFunctionsInitialized = false;
	
	public static final String NOW = "now";
	public static final String ACCOUNT = "account";
	public static final String ACCOUNT_USER = "accountUser";
	public static final String USER = "user";

	private ExpressionFactory factory;
	private TolvenContext context;
	private VariableResolver variableResolver;
	private TolvenFunctionMapper functionMapper;
	private ValueExpression expr;
	
	public void initializeCompositeResolver( CompositeELResolver resolver ) {
		resolver.add( variableResolver );
		resolver.add(new ArrayELResolver(false));
		resolver.add(new ListELResolver(false));
		resolver.add(new MapELResolver(false));
		resolver.add(new ResourceBundleELResolver());
		resolver.add(new BeanELResolver(false));
	}
	
	public ExpressionEvaluator( ) {
		functionMapper = new TolvenFunctionMapper();
		variableResolver = new VariableResolver();
		factory = ExpressionFactory.newInstance();
		CompositeELResolver resolver =	new CompositeELResolver();
		initializeCompositeResolver( resolver );
		context = new TolvenContext( resolver, functionMapper );
	}
	
    protected void initializeELFunctions() {};
    
	/**
	 * Pop the most recent map from the stack
	 * @return
	 */
	public void popContext( ) {
		variableResolver.popMap();
	}
	
	/**
	 * Create a new context and push it onto a stack of contexts. 
	 * The contexts are searched starting with the last one to be pushed on the stack.
	 */ 
	public void pushContext( ) {
		variableResolver.pushMap();
	}
	
	/**
	 * Clear all the variables in the current scope only.
	 */
	public void clearVariables() {
		variableResolver.getMap().clear();
	}
	
	/**
	 * Add a variable to this EL context
	 * @param name
	 * @param object
	 */
	public void addVariable( String key, Object value) {
		variableResolver.getMap().put(key, value);
	}
	
	/**
	 * Add a map of variables to this EL context
	 * @param name
	 * @param object
	 */
	public void addVariables( Map<String, Object> map) {
		if (map!=null) {
			variableResolver.getMap().putAll(map);
		}
	}

	/**
	 * Add a map of variables to this EL context
	 * @param name
	 * @param object
	 */
	public void addProperty( String name, Object value) {
		String names[] = name.split("\\.");
		if (names.length==1) {
			addVariable( name, value );
		} else {
			Map<Object, Object> map = variableResolver.getMap();
			for ( int x = 0; x < names.length-1; x++ ) {
				Map<Object, Object> item = (Map<Object, Object>) map.get(names[x]);
				if (item==null) {
					item = new HashMap<Object, Object>(); 
					map.put(names[x], item);
				}
				map = (Map<Object, Object>) item;
			}
			map.put(names[names.length-1], value);
		}
	}

	/**
	 * Evaluate a single expressions, ie #{ } and return the result as an object.
	 * If an error occurs, we evaluate the expression again with more error granularity 
	 * @param expression
	 * @return
	 */
	public Object evaluate( String expression, Class resultClass  )  {
		// Deferred EL function creation should be done now
		if (!elFunctionsInitialized) {
			initializeELFunctions();
			elFunctionsInitialized = true;
		}
		try {
			//	 resolve top-level property
			expr = factory.createValueExpression(context, expression, resultClass);
			return expr.getValue(context);
		} catch (Exception e) {
			return evaluateError( expression, resultClass );
		} 
	}
	
	public String evaluateError( String expression, Class resultClass  ) {
		int line = 1;
		int col = 1;
		int segmentBegin = -1;
		boolean escape = false;
		char e[] = expression.toCharArray();
		for (int c = 0; c < e.length; c++ ) {
			if ( '\n'==e[c] ) {
				line++;
				col=1;
				continue;
			}
			col++;
			if (escape) {
				escape=false;
				continue;
			}
			if ( '\\'==e[c] ) {
				escape = true;
				continue;
			}
			if (c < e.length-3) {
				if ('#'==e[c] || '$'==e[c]) {
					if ('{'==e[c+1]) {
						segmentBegin=c;
						continue;
					}
				}
			}
			// If we're in an el segment and see the end, do the evaluation
			if ('}'==e[c] && segmentBegin>=0) {
				String segment = expression.substring(segmentBegin, c+1);
				evaluateErrorElement( segment, resultClass, line, col);
				segmentBegin=0;
				continue;
			}
		}
		return null;
	}
	
	public void evaluateErrorElement( String expression, Class resultClass, int line, int col  ) {
		try {
			expr = factory.createValueExpression(context, expression, resultClass);
			expr.getValue(context);
		} catch (Exception e) {
			throw new RuntimeException( "Exception thrown evaluating " + expression + " at line " + line + " column " + col, e);
		} 
	}

	/**
	 * Evaluate a single expressions, ie #{ } and return the result as an object. The result is coerced to the class specified.
	 * @param expression
	 * @return
	 */
	public Object evaluate( String expression )  {
		return evaluate( expression, Object.class); 
	}
	
	/**
	 * Assign the value to the item in the expression.
	 * @param expression Must be an lvalue expression
	 * @param value
	 * @param resultType
	 * @return true if the assignment was made. otherwise false.
	 */
	public boolean setValue( String expression, Object value, Class<?> resultClass )  {
		//	 resolve top-level property
		expr = factory.createValueExpression(context, expression, resultClass);
		Class<?> type = expr.getType(context);
		// Primitives and null go right through
		if (value==null || (type !=null && type.isPrimitive())) {
			// Set the value
			expr.setValue(context, value); 
			return true;
		} else if (type !=null && type.isAssignableFrom(value.getClass())) {
			// Set the value
			expr.setValue(context, value); 
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Assign the value to the item in the expression.
	 * @param expression Must be an lvalue expression
	 * @param value
	 * @return true if the assignment was made. otherwise false.
	 */
	public boolean setValue( String expression, Object value )  {
		return setValue( expression, value, Object.class);
	}

	public void addFunction( String prefix, String localName, java.lang.reflect.Method method) {
		functionMapper.addFunction(prefix, localName, method);
	}

	public void addFunctions( String prefix, Class<?> type) {
		for (Method method: type.getDeclaredMethods()) {
			if (Modifier.isStatic(method.getModifiers()) &&
				Modifier.isPublic(method.getModifiers())) {
				functionMapper.addFunction(prefix, method.getName(), method);
			}
		}
	}

	public void addFunctions( String prefix, String className) throws ClassNotFoundException {
		addFunctions(prefix, this.getClass().forName(className));
	}
	
	/**
	 * In this regard, we behave like a regular map
	 */
	@Override
	public Object get(Object key) {
		return variableResolver.get(key);
	}
	
	/**
	 * Add a map entry to the current (top-of-stack) context
	 */
	@Override
	public Object put(String key, Object value) {
		return variableResolver.getMap().put(key, value);
	}

	@Override
	public void clear() {
		this.clearVariables();
	}

	@Override
	public boolean containsKey(Object key) {
		return variableResolver.containsKey( key );
	}

	@Override
	public boolean containsValue(Object value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Set<java.util.Map.Entry<String, Object>> entrySet() {
		return null;
	}


	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Set<String> keySet() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void putAll(Map<? extends String, ? extends Object> m) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object remove(Object key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int size() {
		return 0;
	}

	@Override
	public Collection<Object> values() {
		// TODO Auto-generated method stub
		return null;
	}
}
