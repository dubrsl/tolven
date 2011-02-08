package org.tolven.process;

import java.io.Serializable;
/**
 * A validation error issued from Tolven compute. This error is intended to be related to
 * a user interface or other calling application.
 * @author John Churin
 *
 */
@SuppressWarnings("serial")
public class ValidationException extends Exception implements Serializable {
	/**
	 * A validation mentions the path in the structure as well as the exception message 
	 * @param path
	 * @param message
	 */
	public ValidationException(String path, String message) {
		super(message);
	}

}
