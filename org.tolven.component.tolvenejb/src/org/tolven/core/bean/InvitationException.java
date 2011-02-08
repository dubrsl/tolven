/*
 *  Copyright (C) 2006 Tolven Inc 
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
package org.tolven.core.bean;

import org.tolven.doc.entity.Invitation;

public class InvitationException extends Exception {
	Invitation invitation;
	
	public InvitationException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public InvitationException(String message, Invitation invitation ) {
		super(message);
		this.invitation = invitation;
	}

	public InvitationException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public InvitationException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public InvitationException(String message) {
		super(message);
	}

}
