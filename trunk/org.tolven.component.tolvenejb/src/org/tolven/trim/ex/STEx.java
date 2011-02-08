package org.tolven.trim.ex;

import org.tolven.trim.ST;

public class STEx extends ST {
	private static final long serialVersionUID = 1L;

	/**
	 * Simple override to get ST to return the actual string value
	 */
	@Override
	public String toString() {
		return this.getValue();
	}
}
