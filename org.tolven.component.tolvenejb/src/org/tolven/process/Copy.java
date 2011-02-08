package org.tolven.process;

import org.tolven.app.el.ELFunctions;
import org.tolven.app.el.TrimExpressionEvaluator;
import org.tolven.logging.TolvenLogger;
import org.tolven.trim.Act;
import org.tolven.trim.Entity;
import org.tolven.trim.Role;

public class Copy extends ComputeBase {

	private String destination;
	private String source;
	
	@Override
	public void compute() throws Exception {
		TrimExpressionEvaluator ee = new TrimExpressionEvaluator();
		ee.addVariable("trim", getTrim());
		ee.addVariable("account", getAccountUser().getAccount());
		if (getNode() instanceof Act) {
			ee.addVariable("act", getNode());
		}
		if (getNode() instanceof Role) {
			ee.addVariable("role", getNode());
		}
		if (getNode() instanceof Entity) {
			ee.addVariable("entity", getNode());
		}
		Object result = ee.evaluate(getSource());
		ee.setValue(getDestination(), result);
//		TolvenLogger.info( "" , Copy.class);
	}


	public String getDestination() {
		return destination;
	}


	public void setDestination(String destination) {
		this.destination = destination;
	}


	public String getSource() {
		return source;
	}


	public void setSource(String source) {
		this.source = source;
	}


}
