package org.tolven.app.entity;
/**
 * Holds a decoded display function from a column
 * @author John Churin
 *
 */
public class DisplayFunction {
	String function;
	String arguments;
	String argumentArray[];
	String from;
	
	/**
	 * A deferred field means that it is acquired from the placeholder or, if this is the placeholder, it is
	 * acquired from the underlying document. To qualify as a deferred field, there must be at least one <from>
	 * and the internal attribute must be null or not be _extended.
	 * @return
	 */
	public boolean isDeferred() {
		return (from!=null && (arguments==null || !MSColumn.EXTENDED_FIELD.equals(arguments)));
	}
	
	public String getFunction() {
		return function;
	}
	
	public void setFunction(String function) {
		this.function = function;
	}
	
	public String getArguments() {
		return arguments;
	}
	
	public void setArguments(String arguments) {
		this.arguments = arguments;
		this.argumentArray = null;
	}
	
	public String[] getArgumentArray() {
		if (argumentArray==null) {
			if (arguments==null || arguments.length()==0) {
				argumentArray = new String[0];
			} else {
				argumentArray = arguments.split("\\,");
			}
		}
		return argumentArray;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}
}
