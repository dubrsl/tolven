package org.tolven.plugin.repository.el;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.el.FunctionMapper;

public class TolvenFunctionMapper extends FunctionMapper {
	Map<String, java.lang.reflect.Method> functions = new HashMap<String, java.lang.reflect.Method>();

	@Override
	public Method resolveFunction(String prefix, String localName) {
		return functions.get(prefix + ":" + localName);
	}
	
	public void addFunction( String prefix, String localName, java.lang.reflect.Method method) {
		functions.put(prefix + ":" + localName, method);
	}
}
