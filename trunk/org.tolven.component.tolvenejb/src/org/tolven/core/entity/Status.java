package org.tolven.core.entity;

/**
 * NEW_LOGIN is assigned with a LoginModule creates a valid user who previously did not exist
 * @author John Churin
 *
 */
public enum Status {
		NEW("new"),
		NULLIFIED("nullified"),
		OBSOLETE("obsolete"),
		ACTIVE("active"),
		OLD_ACTIVE("ACTIVE"),
		ACTIVATED("activated"),
		ACTIVATING("ACTIVATING"),
		COMPLETED("completed"),
		INACTIVE("inactive"),
		OLD_INACTIVE("INACTIVE"),
		REGISTERED("registered"),
		USED("USED");
		
		private final String value;

	    Status(String v) {
	        value = v;
	    }

	    public String value() {
	        return value;
	    }

	    public static Status fromValue(String v) {
	        for (Status c: Status.values()) {
	            if (c.value.equals(v)) {
	                return c;
	            }
	        }
	        throw new IllegalArgumentException(v.toString());
	    }

}
