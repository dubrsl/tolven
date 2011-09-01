/*
 *  Copyright (C) 2008 Tolven Inc 
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of 
 * the GNU Lesser General Public License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU Lesser General Public License for more details.
 * 
 * Contact: info@tolvenhealth.com
 */
package org.tolven.app.el;

import javax.el.CompositeELResolver;

import org.tolven.el.ExpressionEvaluator;
import org.tolven.trim.ex.TrimFactory;

/**
 * Expression Language access to application structure. This subclass includes support for TRIM-based expressions
 * This object can also be used like a map to evaluate expressions though this may not be a good idea since EL cannot actually be nested in any realistic way.
 * Variables in this evaluator can be stacked for scope-limits. For example, certain variables can persist through an entire transaction
 * such as accountUser and now which other variable such as _placehoder change per iteration in a query result.
 * @author John Churin
 */
public class TrimExpressionEvaluator extends GeneralExpressionEvaluator {

	private TrimBeanResolver trimBeanResolver;

	@Override
	public void initializeCompositeResolver( CompositeELResolver resolver ) {
		resolver.add( new MenuDataELResolver() );
		trimBeanResolver = new TrimBeanResolver(new TrimFactory());
		resolver.add(trimBeanResolver);
		super.initializeCompositeResolver(resolver);
	}
	
	@Override
	public Object evaluate( String expression, Class resultClass  )  {
		trimBeanResolver.setSettingValue(false);
		return super.evaluate(expression, resultClass );
	}
	
	@Override
	public boolean setValue( String expression, Object value, Class<?> resultClass )  {
		trimBeanResolver.setSettingValue(true);
		return super.setValue(expression, value, resultClass);
	}
}
