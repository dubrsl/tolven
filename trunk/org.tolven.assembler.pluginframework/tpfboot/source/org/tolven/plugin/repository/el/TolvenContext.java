package org.tolven.plugin.repository.el;

import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.FunctionMapper;
import javax.el.VariableMapper;

public class TolvenContext extends ELContext {
	ELResolver resolver;
	FunctionMapper functionMapper;
	
	public TolvenContext(ELResolver resolver, FunctionMapper functionMapper ) {
		this.resolver = resolver;
		this.functionMapper = functionMapper;
	}
	@Override
	public ELResolver getELResolver() {
		return resolver;
	}

	@Override
	public FunctionMapper getFunctionMapper() {
		return functionMapper;
	}

	@Override
	public VariableMapper getVariableMapper() {
		return null;
	}

}
